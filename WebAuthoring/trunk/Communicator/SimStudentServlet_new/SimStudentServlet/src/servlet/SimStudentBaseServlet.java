package servlet;
import interaction.Backend;
import interaction.InterfaceAttribute;
import interaction.InterfaceEvent;
import interaction.SAI;
import interaction.SimBackend;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.cmu.pact.Utilities.trace;
import logging.LogWriterForwarder;

/** SimstudentBaseServlet is a basic Servlet that interacts with the web browser version of
   CTAT. This class has the functionality to communicate bidirectionally with
   the client side. This servlet was built for apache-tomcat-7.0.54 server. The
   post requests are how the web browser sends information to this servlet - this
   is managed using XMLHttpRequest.
   The get requests are how this servlet can send information to the web browser
   - this is managed using EventSource objects that periodically poll for messages.


	NOTE: do not put the InterfaceBuilder html/javascript code in this project's
	WebContent folder and run from Eclipse; for some reason Eclipse crashes. You
	have to run the InterfaceBuilder code off of Tomcat on the console and develop
	the java code through a text editor.

   @author Patrick Nguyen
 */
public class SimStudentBaseServlet extends HttpServlet implements ServletContextListener{
	private LogWriterForwarder logger;
	private MessageManager messMan;
	private Map<String,Backend> backends;
	private Map<Backend,String> users;
	private Backend ab;

	/**
	 * Called when servlet starts up, used in place of constructor
	 */
	public void init(ServletConfig servletConfig)
	{
		ResponseMessage.staticFiles = servletConfig.getServletContext().getRealPath("/WEB-INF")+"/files";
		messMan = new MessageManager();
		logger = new LogWriterForwarder();
		backends = new HashMap<String,Backend>();
		users = new HashMap<Backend,String>();

		//logger.setFile(new File(getInitParameter("LogFileName")));
		//logger.setLogServerURL(getInitParameter("LogServerURL"));
		//(new Thread(logger, "MyLogger")).start();
	}

	/** This method is called whenever the client sends a post request.
	 * It parses the request and sends a response using the provided arguments.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		System.out.println("\nReceived post request");
		String req = retrieveRequest(request);//read input stream, a xml file sent by the client
//		System.out.println(req);
		System.out.println("request:" + req);
//		Utilities.prettyPrintXMLAsString(req);
//		HttpSession session = request.getSession();
//		System.out.println("Session is: "+session.getId());

		String resp = getResponse(req);//place where everything is parsed
//		System.out.println("response:");
//		Utilities.prettyPrintXMLAsString(resp);

		sendResponse(response,resp);//write back to client
//		System.out.println("Finished processing post request");
	}
	
	public void doOptions(HttpServletRequest req, HttpServletResponse resp)
	        throws IOException {
	    //The following are CORS headers. Max age informs the 
	    //browser to keep the results of this call for 1 day.
	    resp.setHeader("Access-Control-Allow-Origin", "*");
	    resp.setHeader("Access-Control-Allow-Methods", "GET, POST");
	    resp.setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, ctatsession, content-type");
	    resp.setHeader("Access-Control-Max-Age", "600");
	    //Tell the browser what requests we allow.
	    resp.setHeader("Allow", "GET, HEAD, POST, TRACE, OPTIONS");
	}

	/*
	 * Reads the request from the argument, line by line.
	 */
	private String retrieveRequest(HttpServletRequest request)
	{
		//we read the data from the request input stream instead of expecting name=value pairs
		String req = "";
		try(
				BufferedReader bf = new BufferedReader(new InputStreamReader(request.getInputStream()));
				){
			String temp="";
			while((temp=bf.readLine()) != null)
				req+= temp+"\n";//hopefully the \n won't break anything
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return req;
	}

	/* This method is used to parse the post request message. We case
       on the type of message, and if the request is none of those requests then
       we simply return an empty string. Right now we've hardcoded a response for
       SetPreferences, and the InterfaceAction response depends on the backend. Will
       change later.
	 */
	private String getResponse(String request)
	{
		RequestMessage[] rms = RequestParser.parseMultipleRequests(request);//wraps request string in object
		ResponseMessage[] resps = new ResponseMessage[rms.length];
		int i = 0;
		for(RequestMessage rm : rms){
			String session = rm.getSessionId();
			//requests this servlet takes care of
			if(rm instanceof SetPreferenceRequest){//same as new StartStateResponse().toXML()
				SetPreferenceRequest spr = (SetPreferenceRequest)rm;
				
				// Added by Shruti for demonstrating a valueTypeChecker input argument to Backend
				System.out.println("Arguments passed: "+spr.getArgument());
				String[] argV = parseArgument(spr.getArgument());
				
				System.out.println("Backend directory: " + spr.getBackendDirectory());
				
				Backend b = loadBackend(spr.getBackendDirectory(),spr.getBackendEntryClass(),argV);
				b.setServlet(this);
				b.setSession(session);
				
				b.userID=((SetPreferenceRequest) rm).getUserGuid();
				b.problemName=spr.getArgument();
				backends.put(session,b);
				users.put(b, ((SetPreferenceRequest) rm).getUserGuid());
				java.util.Date date= new java.util.Date();
				System.out.println("LogPatch: [User : " + ((SetPreferenceRequest) rm).getUserGuid() +"],[ProblemInfo:"+spr.getArgument()+"],[Session:"+session+"],[TransID:"+ rm.getTransactionId()+"],[SAI:empty],[Result:Problem Started],[EventTime:"+new Timestamp(date.getTime())+"]");

//				giveWMEs(b,spr.getWmes());
				if(spr.getDriveUrl() != null && spr.getDriveToken() != null){
					resps[i] = new StartStateResponse(spr.getDriveUrl(),spr.getDriveToken());
				}else{
					if(spr.getQuestionFile() == null){
						System.out.println("Default response");
						resps[i] = new StartStateResponse();
					}else{
						resps[i] = new StartStateResponse(spr.getQuestionFile());
					}

				}
				giveWMEs(b,((StartStateResponse)resps[i]).getStartStateXML());
				b.init();

				b.setInitialSAIs(((StartStateResponse)resps[i]).getStartStateInterfaceActions());
			}else if(rm instanceof InterfaceAttribute){
				Backend b = backends.get(session);
				b.addComponent((InterfaceAttribute)rm);
			}else if(rm instanceof LogMessageRequest){//log messages are a different type of message from the above
				String message = ((LogMessageRequest)rm).getMessage();
				return logger.logOrQueueAndReply(message);
			}

			//requests the extending servlet takes care of
			else if(rm instanceof InterfaceActionRequest){
				InterfaceActionRequest req=(InterfaceActionRequest)rm;
				Backend b = backends.get(session);
				if(b == null){
					System.out.println("Session not registered");
					continue;
				}
				
				SAI sai = req.getSai();
			//	System.out.println("LoggingPatch: [User1 : " + b.userID +"],[Session:"+session+"],[TransID:"+rm.getTransactionId()+"[SAI:"+sai.getFirstSelection()+","+sai.getFirstAction()+","+sai.getFirstInput()+"]");
				InterfaceEvent event;
				if(sai.getAction().get(0).equals("DoubleClick")){
					//b.processDoubleClick(sai);
					event = new InterfaceEvent(sai,InterfaceEvent.Event.DOUBLE_CLICK,rm.getTransactionId());
				}
				else{
					//b.processSAI(sai);
					
					event = new InterfaceEvent(sai,InterfaceEvent.Event.SAI,rm.getTransactionId());
				}
				b.processInterfaceEvent(event);

			}else{
				//nothing
				System.out.println("cannot form a response!");
				System.out.println(request);
			}
			i++;
		}
		return ResponseCreator.createXML(resps);
	}




	/*
	 * Takes a response message and sends it back to the client. We always use the same headers here.
	 */
	private void sendResponse(HttpServletResponse responseMech, String responseText)
	{
		try{
			//System.out.println("Response is: "+responseText);
			//we deal with plain text here and let the client deal with the parsing
			responseMech.setContentType("text/plain; charset=ISO-8859-1");
			//encoding must be set to UTF-8
			responseMech.setCharacterEncoding("UTF-8");
			//we don't want to cache the respons

			responseMech.setHeader("Cache-Control", "no-cache");
			responseMech.setHeader("Access-Control-Allow-Origin", "*");
			responseMech.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
			responseMech.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,ctatsession");
			responseMech.setHeader("Pragma", "no-cache");
			//actually write back the response
			PrintWriter writer = responseMech.getWriter();
			writer.write(responseText);
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Dynamically loads a Backend class based on the directory and name provided
	 * Note - Added parameter String[] argV by Shruti
	 * @param url Url of the directory containing all backend code
	 * @param name Name of entry point class that extends Backend
	 * @param argV array of string arguments for loading appropriate production rules, wmeTypes and init wme files
	 * @return Backend object created from the loaded class
	 */
	private Backend loadBackend(String url, String name, String[] argV){
		try{
			ClassLoader loader = SimStudentBaseServlet.class.getClassLoader();
			/*
            String u = loader.getResource("servlet/SimStudentBaseServlet.class").toString();
            String s = "servlet/SimStudentBaseServlet.class";
            URL url1 = new URL(u.substring(0,u.length()-s.length()));
			 */
			URL dir = new URL(url);
			System.out.println(dir);
			URL[] urls = {dir};
			URLClassLoader ucl = new URLClassLoader(urls,loader);
			Class c = ucl.loadClass(name);

			//Modified by Shruti
			Backend b = null;
			//check if the parameterised constructor exists for the requested backend class
			if(c.getConstructor(String[].class) != null)
				b = (Backend)c.getConstructor(String[].class).newInstance((Object)argV);
			//else return an empty backend with no implementation
			else
				b = new EmptyBackend(argV);
			
			return b;
		}catch(Exception e){
			e.printStackTrace();
			return new EmptyBackend(argV);//no implementation
		}
	}
	/**
	 * Passes the WME files to the backend, given the url for the .wme files
	 * @param b Backend object to pass the WMEs to
	 * @param urls Urls of the .wme
	 */
	private void giveWMEs(Backend b, List<String> urls) {
		System.out.println("Giving wmes from urls");
		List<String> wmes = new ArrayList<String>(urls.size());
		for(int i = 0; i < urls.size(); i++){
			try{
				String a = readFromURL(urls.get(i));
				wmes.add(a);
			}catch(Exception e){
				System.out.println(urls.get(i)+" is not a valid file url");
			}
		}
		b.setWME(wmes);
	}

	public void giveWMEs(Backend b, String brd){
		System.out.println("Giving wmes from BRD");
		String start = "<jessDeftemplates>";
		String end = "</jessInstances>";
		List<String> wmes = new ArrayList<String>();
		while(brd.indexOf(start) != -1){
			int i0 = brd.indexOf(start);
			int i1 = brd.indexOf(end)+end.length();
			wmes.add(brd.substring(i0,i1));
			brd = brd.substring(i1);
		}
		b.setWME(wmes);
	}
	/**
	 * Reads in a string given a url.
	 * @param file Url of the file
	 * @return String data from the file
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private String readFromURL(String file) throws MalformedURLException,IOException{
		System.out.println("Extracting "+file);
		URL url = new URL(file);
		BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream()));
		String brd="";
		String temp;
		while((temp=bf.readLine())!=null){
			brd+=temp;
		}
		bf.close();
		return brd;
	}

	/* This method is periodically called by a Javascript EventSource object, and
       meant to send messages from x this servlet to the the client as needed. Because
       EvenSource polls for messages, all messages come with id's that can be used
       to distinguish repeated messages and new ones; the client only needs to
       parse distinct new messages. Messages managed by the object MessageManager
       object, which contains the current id and message to send out. This class
       can give this object to any other class in the server in order to
       send new messages as needed. All messages are in JSON format with the object
       containing two fields: the id and the message.
       For more info: http://www.html5rocks.com/en/tutorials/eventsource/basics/
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//EventSource requires the text to be in a specific format
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,session_id");

		String session = request.getParameter("session_id");

		//all messages must be in the form "data: <message>\n\n" for EventSource
		PrintWriter writer = response.getWriter();
		String message;
		boolean hasMessage = false;
		while((message = messMan.getMessage(session)) != null){
			//System.out.print("@SimStudentBase: Sent message:");
			System.out.println(message);
			trace.out("Sent message:" + message);
			writer.write("data: "+message+"\n\n");
			hasMessage = true;
		}
		if(!hasMessage){
			writer.write(": Alex! Comment to keep the connection alive");			
		}
		writer.close();
	}
	/**
	 * Sends an SAI back to the interface associated with the given session
	 * @param p SAI to send back
	 * @param session Id of the session associated with the backend
	 */
	public void sendSAI(SAI p, String session){
		if(p == null) return;
		String message=ResponseCreator.createTutorPerformedResponse(p);
		messMan.addMessage(session,message);
	}


	/**
	 * Sends an hint message to the CTATHintWindow 
	 * @param p Message to send to hint window
	 * @param session Id of the session associated with the backend
	 */
	public void sendHintMessage(ArrayList hints, String session){
		if(hints == null) return;
		String message=ResponseCreator.createTutorPerformedResponseHint(hints);
		messMan.addMessage(session,message);
	}
	
	

	/**
	 * Sends the result of the student sai back to the interface associated
	 * with the given session
	 * @param p SAI that the student gave
	 * @param result whether or not the SAI matched the tutor or not
	 * @param session session Id of the session associated with the backend
	 */
	public void sendGradedResponse(String result, SAI p, String session) {
		if (p == null || result == null || result.equals("")) return;

	}
	/**
	 * Modifies the component associated with @code{im}
	 * @param im Object representing the component to modify
	 * @param session Id of interface associated with the component
	 */
	public void modifyInterface(InterfaceAttribute im,String session){
		String xml = im.modificationXML();
		messMan.addMessage(session,xml);
	}
		


	/**
	 *
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		System.out.println ("ServletContextListener destroyed");
	}
	/**
	 *
	 */
	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		System.out.println("ServletContextListener started");

		ServletContext ctx=event.getServletContext();

	}
	


	/**
	 * This method parses the Argument string value to an array
	 * of arguments before sending it to backend class for parsing.
	 * Example argument = "-traceOn -folder informallogic -problem if_p_or_q_then_r -ssTypeChecker informallogic.MyFeaturePredicate.valueTypeChecker"
	 * @param String argument
	 * @return String[] argsV
	 * @author SHRUTI
	 */
	private String[] parseArgument(String argument) {
		try{
			//check if there are arguments and are in the expected format
			if(argument!=null && argument.contains("-"))
				return argument.split("-");
			//else return an empty String array
			else
				return new String[]{};
		}
		catch(Exception e){
			e.printStackTrace();
			return new String[]{};
		}
	}

}
