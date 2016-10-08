package edu.cmu.pact.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.Log.LogFormatUtils;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.trace;

public class TutorshopAdvance implements ProblemAdvance {
	
	private MessageConnection msgConn;
	
	/* For a curriculum service to keep track of */
	private String user_name, school_name, admit_code; 
	
	/* General pathing info to curriculum service */
	private String curriculumServiceHost, tutorshopServlet;
	private int curriculumServicePort;
	
	private List<ProblemAdvancedListener> listeners = new LinkedList<ProblemAdvancedListener>();
	
	private String log_service = "";
	
	/** To replace in the setPreferences String, the important ones are question file and problem name */
	private static final String LOG_SERVICE_URL = "LOG_SERVICE_URL", LOG_TO_REMOTE_SERVER = "LOG_TO_REMOVE_SERVER",
		USER_GUID = "USER_GUID", PROBLEM_NAME = "PROBLEM_NAME", QUESTION_FILE = "QUESTION_FILE";
	
	/** SetPreferences with fill-in-the-string */
	private final String setPreferenceXMLwithNewlines =
		
	"<tutor_related message_sequence>" +
		"\t<verb>NotePropertySet</verb>\r\n" +
		"\t<properties>\r\n" +
			"\t\t<MessageType>SetPreferences</MessageType>\r\n" +
			"\t\t<log_service_url></log_service_url>\r\n" +
			"\t\t<log_to_remote_server>false</log_to_remote_server>\r\n" +
			"\t\t<user_guid>USER_GUID</user_guid>\r\n" +
			"\t\t<problem_name>PROBLEM_NAME</problem_name>\r\n" +
			"\t\t<question_file>QUESTION_FILE</question_file>\r\n" +
			"\t\t<session_id>testTue4_24_07_01</session_id>\r\n" +
			"\t\t<source_id>CTAT_Flash_TutoringService</source_id>\r\n" +	
			"\t\t<container_id>myContainer</container_id>\r\n" +
			"\t\t<external_object_id>myExternalId</external_object_id>\r\n" +
			"\t\t<ProblemName>PROBLEM_NAME</ProblemName>\r\n" +
		"\t</properties>\r\n" +
	"</message>";
	
	private final String setPreferenceXML = "<message><verb>NotePropertySet</verb><properties><MessageType>SetPreferences</MessageType><log_service_url>http://learnlab.web.cmu.edu/log/server</log_service_url><log_to_remote_server>false</log_to_remote_server><log_to_disk>false</log_to_disk><user_guid>myUniqueUserIdentifier</user_guid><problem_name>myGraphName</problem_name><question_file>ChemPT_3T_62_IU.brd</question_file><school_name>CMU</school_name><session_id>mySessionID</session_id><auth_token>myAuth_token</auth_token><source_id>PACT_CTAT_FLASH</source_id><container_id>myContainer</container_id><external_object_id>myExternalId</external_object_id><dataset_name>mySubjectMatter_Arithmetic</dataset_name><ProblemName>ChemPT_3T_62_IU.brd</ProblemName></properties></message>";
	
	/** SetPreferences with fill-in-the-string */
	private final String setPreferenceXMLreal = 
	"<message>" +
		"<verb>NotePropertySet</verb>" +
		"<properties>" +
			"<MessageType>SetPreferences</MessageType>" +
			"<log_service_url></log_service_url>" +
			"<log_to_remote_server>false</log_to_remote_server>" +
			"<user_guid>USER_GUID</user_guid>" +
			"<problem_name>PROBLEM_NAME</problem_name>" +
			"<question_file>QUESTION_FILE</question_file>" +
			"<session_id>testTue4_24_07_01</session_id>" +
			"<source_id>CTAT_Flash_TutoringService</source_id>" +	
			"<container_id>myContainer</container_id>" +
			"<external_object_id>myExternalId</external_object_id>" +
			"<ProblemName>PROBLEM_NAME</ProblemName>" +
		"</properties>" +
	"</message>";
	
	TutorMessageDisplay creator;
	
	public void setLogCreator(TutorMessageDisplay display)
	{
		creator = display;
	}
	
	public void setLoggingService(String loggingService)
	{
		log_service = loggingService;
	}
	
	public String getPreferences(String log_service_url, boolean log_to_remote_server, String user_guid,
			String problem_name, String question_file)
	{
		List preferences = new Vector<String>();
		List values = new Vector<String>();
		preferences.add("MessageType");
		values.add("SetPreferences");
		preferences.add("log_service_url");
		values.add(log_service_url);
		preferences.add("log_to_remote_server");
		values.add(log_to_remote_server);
		preferences.add(Logger.STUDENT_NAME_PROPERTY);
		values.add(user_guid);
		preferences.add("problem_name");
		values.add(problem_name);
		preferences.add("question_file");
		values.add(question_file);
		preferences.add("container_id");
		values.add("myContainer");
		preferences.add("external_object_id");
		values.add("myExternalID");
		
		/*String preferences = setPreferenceXML;
		preferences = preferences.replace(LOG_SERVICE_URL, log_service_url);
		preferences = preferences.replace(LOG_TO_REMOTE_SERVER, Boolean.toString(log_to_remote_server));
		preferences = preferences.replace(USER_GUID, user_guid).replace(PROBLEM_NAME, problem_name);
		preferences = preferences.replace(QUESTION_FILE, question_file);*/
		
		return creator.createSetPreferenceRequest(preferences, values);
	}
	
	public void setUser(String user)
	{
		user_name = user;
	}
	
	public void setSchool(String school)
	{
		school_name = school;
	}
	
	public void setAdmitCode(String admit)
	{
		admit_code = admit;
	}
	
	public void setServletParams(String host, int port, String path)
	{
		curriculumServiceHost = host;
		curriculumServicePort = port;
		tutorshopServlet = path;
	}
	
	public void addProblemAdvancedListener(ProblemAdvancedListener pal)
	{
		listeners.add(pal);
	}
	
	/**
	 * Sends a request to Tutorshop (in CL style) for the next problem
	 * @return - the name of the next problem
	 */
	public String advanceProblem() {
		URL url = null;
		try
		{
			String cmd = "doneNextData"; //can't use login or doneNext since those are for jsp responses
			String s = tutorshopServlet + "?user_guid=" + user_name + "&school_name=" + school_name + "&cmd=" + cmd + "&admit_code=" + admit_code;
			url = new URL("HTTP", curriculumServiceHost, curriculumServicePort, s);
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String resp = br.readLine();
			
			String problemName = null, brdFilename = null;
			int problemNameIndex = -1, nextIndex = -1;
			if((problemNameIndex = resp.indexOf("problem_name=")) >= 0 && (nextIndex = resp.indexOf("&", problemNameIndex)) >= 0)
				problemName = resp.substring(problemNameIndex + "problem_name=".length(), nextIndex);
			
			int brdFilenameIndex = -1;
			nextIndex = -1;
			if((problemNameIndex = resp.indexOf("question_file=")) >= 0 && (nextIndex = resp.indexOf("&", problemNameIndex)) >= 0)
				brdFilename = resp.substring(problemNameIndex + "question_file=".length(), nextIndex);
			
			return problemName;
		}
		catch(MalformedURLException e)
		{
			trace.printStackWithStatement("Malformed URL in advancing problem: " + url);
		}
		catch(IOException ioe)
		{
			trace.printStackWithStatement("IOException in advancing problem: " + url);
		}
		return null;
	}
	
	public void fireProblemAdvanced(String problemName)
	{
		for(ProblemAdvancedListener pal : listeners)
			pal.ProblemAdvanced(problemName);
	}
	
	private boolean isDoneMessage(String xmlStr)
	{
		xmlStr = LogFormatUtils.unescape(xmlStr);
		
		SAXBuilder builder = new SAXBuilder();
		StringReader rdr = new StringReader(xmlStr);
		
		Document doc = null;
		try
		{
			doc = builder.build(rdr);
		}
		catch(IOException ioe)
		{
			trace.out("Could not build document, IOException");
			return false;
		}
		catch(JDOMException je)
		{
			trace.out("Could not build document, JDOMException");
			return false;
		}
		Element root = doc.getRootElement();
		for(Element elt : (List<Element>)root.getChildren())
		{
			boolean doneMsgType = false, doneS = false, doneA = false;
			if(elt.getName().equals("message"))
			{
				for(Element property : (List<Element>)elt.getChildren())
				{
					String propAttribNameVal = property.getAttributeValue("name");
					if(propAttribNameVal == null)
						continue;
					else if(propAttribNameVal.equals("MessageType"))
						doneMsgType = (property.getText().contains("CorrectAction"));
					else if(propAttribNameVal.equals("Selection"))
						doneS = (property.getChild("entry").getText().contains("done"));
					else if(propAttribNameVal.equals("Action"))
						doneA = (property.getChild("entry").getText().contains("ButtonPressed"));
				}
			}
			if(doneMsgType && doneS && doneA)
				return true;
		}
		return false;
	}
	
	/**
	 * If message event is done, get next problem's name, send a setPreference out to CTAT,
	 * and notify the problemAdvancedListeners
	 */
	public void messageEventOccurred(MessageEvent me) 
	{
		String xmlStr = me.getMessageAsString();
		if(!isDoneMessage(xmlStr))
			return;
		
		String problemName = advanceProblem();
		trace.out("Advance problem returned:"  + problemName);
		if(problemName == null)
			return;
		sendSetPreferences(problemName);
	}

	/**
	 * Sets preferences on the CTAT side for which brd to load up
	 * @param v - the preferences
	 */
	public void sendSetPreferences(String problemPath) {
		String preferences = getPreferences(log_service, false, user_name, problemPath, problemPath);
		if(msgConn != null)
			msgConn.sendString(preferences);
	}
	
	public void setMessageConnection(MessageConnection msgConn)
	{
		this.msgConn = msgConn;
	}
}
