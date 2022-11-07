//Ask jonathan about backlog and also ask about using flush every X bytes...
//What if disk reading occurs ridiculously fast and we go overboard writing to 
//the buffered output stream... I guess since both input and output are buffered
//that should never happen? I guess I am doing extra buffering for no reason...

/*Simple HTTP server that takes GET requests for files and
 *takes PUT request for logging purposes.
 *
 *Command Line Arguments:
 *
 *Main takes 1 or 2 or 3 arguments. 
 *The 1st argument is the port #.
 *The 2nd argument is optional (but if not provided, functionality isn't guaranteed). 
 *It is the absolute path to the root directory
 *for hosting files. If the path is left unspecified it is the 
 *current directory specified by System.getProperties("user.dir")
 *The 3rd argument is the logFileName.
 *
 *Request Method Processing:
 *
 *If request method is GET for crossdomain.xml:
 *	The response will be the hard-coded string crossdomainpolicy
 *If request method is GET for a file_name:
 *	The response will be the bytes of the file found at args[1]+file_name
 *	If file can't be found. Sends back a 404 HTTP error.
 *If request method is PUT the fileURL must be "/log/server"
 *	The body of the put method is written to pathToRoot+logFileName
 *	Any other PUT requests will generate a 403 HTTP error.
 *NOTE: The content-type in the responses to non crossdomain.xml GET requests
 *is specified to be star slash star, which is very unspecific. Currently
 *flash seems to have no problem with this.
 *Written by Borg Lojasiewicz 07/08/09
 */
package edu.cmu.pact;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.cmu.pact.Utilities.trace;

public class HTTPServer{
	private static String usage = "Please enter 2 or 3 arguments: the port number to run the server on\n"+
								  "Followed by the absolute directory from which you want to load files"+
								  "The third argument is the optional logging file, if it isn't specified"+
								  "logging is unsupported and all put requests will generate errors"+
								  "Note: ports below 1024 on linux machines need root access to work";
	public static void main(String args[]) throws IOException{
		int port;
		String logFileName = "";
		String pathToRoot;
		if(args.length==1){//I am not sure if user.dir will work properly on all OS
			port = Integer.parseInt(args[0]);
			pathToRoot = System.getProperty("user.dir");
		}else if(args.length==2){
			port = Integer.parseInt(args[0]);
			pathToRoot = args[1];
		}else if(args.length==3){
			port = Integer.parseInt(args[0]);
			pathToRoot = args[1];
			logFileName = args[2];
		}else{
			trace.out(usage);
			return;
		}
		new HTTPServer(port, pathToRoot, logFileName);
	}
	
	HTTPServer(int port, String pathToRoot, String logFileName){
		try {
			//InetSocketAddress address = new InetSocketAddress(port);
			
			InetSocketAddress address = new InetSocketAddress(port);
			//address = new InetSocketAddress();
			trace.out("Address = "+ address.toString());
			//Should we use a backlog (max number of queued incoming connections)? 0 goes to system default.
			HttpServer server = HttpServer.create(address, 0);
			//server.bind(address, 0);
			HTTPHandler handler; 
			try{
				handler = new HTTPHandler(pathToRoot, logFileName);
			}catch(IOException e){
				trace.out("error", "Failed to open logFile, exiting HTTP Server");
				trace.out("Failed to open logFile, exiting HTTP Server\n" + usage);
				return;
			}
			server.createContext("/", handler);
			server.start();
			trace.out("http", "HTTPServer open and listenning on " + address.getHostName() + address.getPort());
			trace.out("HTTPServer open and listenning on " + address.getHostName() + address.getPort());
		}
		catch (Exception uhe) {
			trace.out("http", uhe + " : Failed to open HTTPserver on localhost:" + port);
			trace.out("Failed to open HTTPserver on localhost:" + port + " error = " + uhe);
			trace.out(usage);
		}
	}
}
class HTTPHandler implements HttpHandler{
	private static String crossDomainPolicy = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    +"<!DOCTYPE cross-domain-policy SYSTEM \"http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd\">"
		+"<cross-domain-policy>"
		  +"<allow-access-from domain=\"127.0.0.1\"/>"
		  +"<allow-access-from domain=\"localhost\"/>"
		  +"<allow-access-from domain=\"*\" secure=\"false\"/>"
		+"</cross-domain-policy>";
	private String pathToRoot;
	//private String logFileName;
	private FileOutputStream logFile;
	private Boolean logging;
	private String logFileName;
	public HTTPHandler(String pathToRoot, String logFileName) throws IOException{
		this.pathToRoot = pathToRoot;
		if(logFileName!=""){
			this.logFileName = logFileName;
			File pleaseWork = new File(pathToRoot+"/" +logFileName);
			pleaseWork.createNewFile();
			pleaseWork.setReadable(true);
			pleaseWork.setWritable(true);
			logFile = new FileOutputStream(pleaseWork, true);
			logging = true;
		}else
			logging = false;
	}
	
	public synchronized boolean writeToLog(ByteArrayOutputStream baos){
		try {
			baos.writeTo(logFile);
			baos.flush();
			logFile.flush();
		} catch (IOException e) {
			trace.out("error", "IOexception trying to write to logFile: " + logFileName);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void doPost(HttpExchange arg0){
		String fileURI = arg0.getRequestURI().toString();
		String responseBody = "";
		if(fileURI.contains("/log/server")){
			int size = 0;
			List<String> contentLength = arg0.getRequestHeaders().get("Content-Length");
			int bodySize;
			ByteArrayOutputStream baos;
			if(contentLength!=null){
				bodySize = Integer.parseInt(contentLength.get(0));
				baos = new ByteArrayOutputStream(bodySize);
			}else{
				baos = new ByteArrayOutputStream();
			}
			BufferedInputStream in = new BufferedInputStream(arg0.getRequestBody());
	        int b = 0;
	        int numNulls = 0; 
	        //read counting nulls and send to trace.out("error"....)
	        try{
		        while(true){
		        	b = in.read();
					if(b==-1){
						break;
					}else if(b ==0){
						numNulls++;
					}else{
						baos.write(b);
						size++;
					}
				}
	        }catch(IOException e){
	        	trace.out("http", "Put failed reading request body, IOException: " + e);
	        	arg0.close();
	        	return;
	        }
	        if(numNulls >0){
	        	trace.out("error", "numNulls on post method = " + numNulls + " size of msg = " + size);
	        }
        	arg0.getResponseHeaders().add("Content-Type", "text/html; charset=ISO-8859-1");
        	//This step is synchronized to prevent io filewrite interference
        	Boolean result = writeToLog(baos);
        	try{
        		if(result==false){
        			arg0.sendResponseHeaders(500, 0);
        		}else{
        			responseBody += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        			responseBody += "<log-result>\n";
        			responseBody += "  <read-request success=\"true\" length=\"" + size + "\" />\n";
        			responseBody += "  <write-file success=\"true\">\n    ";
        			responseBody += pathToRoot+"/log/server";
        			responseBody += "\n  </write-file>\n";
        			responseBody += "</log-result>\n";
        			arg0.sendResponseHeaders(200, responseBody.length());
        			BufferedOutputStream buf = new BufferedOutputStream(arg0.getResponseBody());
        			buf.write(responseBody.getBytes(), 0, responseBody.length());
        		}
        	}catch(IOException e){
    			trace.out("error", "IOError writing back response : " + e);
    		}
    		arg0.close();
		}else{
			trace.out("http", "put request didn't specify /log/server");
			try{
				arg0.sendResponseHeaders(403, 0);
				arg0.close();
			}catch(IOException e){
				trace.out("error", "IOError writing back response : " + e);
			}
			return;
		}
	}
	
	public void handle(HttpExchange arg0){
		trace.out("Handling a connection");
		String requestMethod = arg0.getRequestMethod();
		if(requestMethod.equalsIgnoreCase("put")){
			if(logging)
				doPost(arg0);
			else{
				trace.out("http", "Requested post, but HTTPServer wasn't called with logfile");
				try{
					arg0.sendResponseHeaders(501, 0);
				}catch(IOException e){
					trace.out("error", "IOException sending response : " + e);
				}
				arg0.close();
			}
			return;
		}
		String fileURI = arg0.getRequestURI().toString();
		trace.out("File uri = " + fileURI);
		trace.out("http", "Request Method = " + requestMethod); 
		if(requestMethod.equalsIgnoreCase("get")){
		    /*Crossdomain.xml Requested.
		     *For successful exchange:
		     *->Crossdomain.xml might need to be uptodate with flash policies
		     *->Valid content-type must be specified in the responseheader:
		     */
		    if(fileURI.equalsIgnoreCase("/crossdomain.xml")){
		    	trace.out("http", "Writing back the crossdomain policy...");
		    	try{
		    		arg0.getResponseHeaders().add("Content-Type", "application/xml");
		    		arg0.sendResponseHeaders(200, crossDomainPolicy.getBytes().length);
		    		arg0.getResponseBody().write(crossDomainPolicy.getBytes());
		    		trace.out("http", "Wrote back Crossdomain.xml..");
		    		arg0.close();
		    	}catch(Exception e){
		    		trace.out("http", e + " : Exception in trying to write back crossdomain.xml");
		    		trace.out(e.toString() + "Exception in trying to write back crossdomain.xml");
		    	}
		    }else{
		    	fileURI = pathToRoot+ File.separator + fileURI.substring(1);
		    	trace.out("http", "Trying to open " + fileURI);
		    	try{
		    		BufferedInputStream bis = null;
		    		File requestedFile;
		    		try{
		    			requestedFile = new File(fileURI);
		    			bis = new BufferedInputStream(new FileInputStream(requestedFile));
		    			trace.out("http", "Opened requested file succesfully");
		    		}catch(IOException e){
		    			trace.out("http", e + " : failed to open/read requested file ");
		    			arg0.sendResponseHeaders(404, 0);
		    			arg0.close();
		    			return;
		    		}
		    		arg0.getResponseHeaders().add("Content-Type", "*/*");
		    		arg0.sendResponseHeaders(200, (requestedFile.length()));
		    		BufferedOutputStream buf = new BufferedOutputStream(arg0.getResponseBody());
		    		//byte[] byteBuf = new byte[1024];
		    		int res;
		    		while(true){
		    			res = bis.read();
		    			if(res==-1)
		    				break;
		    			buf.write(res);
		    		}
		    		buf.flush();
		    		bis.close();
		    		trace.out("http","Wrote back the entire file succesfully");
		    		arg0.close();
				    trace.out("http","Closed the connection");
				    trace.out("Close connection");
		    	}catch(IOException e){
		    		trace.out("http", e + "Exception in trying to write back response");
		    		trace.out(e + "Exception in trying to write back response");
		    	}
		    } 
		}
	}
}