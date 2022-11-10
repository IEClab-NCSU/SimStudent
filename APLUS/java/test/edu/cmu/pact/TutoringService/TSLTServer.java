/*Simple HTTP server that takes requests for files from harddisk
 *The root folder for the harddisk is hardcoded in the static 
 *final string TutoringServiceURI.
 *The HTTP server only accepts GET requests.
 *If the get request is for "localhost:976/pathtofile
 *It loads the file that TutoringServiceURI/pathtofile.
 *The header for the response will specift the contentype to be text/*.
 *If the get request is for localhost:976/crossdomain.xml
 *It will load the crossdomain.xml file found in Tutoringserviceuri/crossdomain.xml
 *and set the responseheade field for content-type to be application/xml. 
 *The port is specified in the static int port. Vista seems to have stolen my port 8080
 *for its own greedy purposes (and won't let me kill the process using it..)
 *so I am defaulting the port to be the sum of the char valus in the String "dislikevista".
 */

package edu.cmu.pact.TutoringService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.cmu.pact.Utilities.trace;

public class TSLTServer{
	private static String dislikevista = "dislikevista";
	private static int port;
	public static void main(String args[]) throws IOException{
		try {
			int i;
			for(i=0; i < dislikevista.length(); i++){
				port += (int)dislikevista.charAt(i);
			}
			InetSocketAddress address = new InetSocketAddress("localhost", port);
			trace.out("Address = "+ address.toString());
			HttpServer server = HttpServer.create(address, 0);
			server.createContext("/",new TSLTHandler());
			server.start();
			trace.out("TSLTServer open and listenning on " + address.getHostName() + address.getPort());
		}
		catch (Exception uhe) { 
			trace.out("Failed to open TSLT server on localhost:" + port);
		}
	}
	
	
}
// if starts with a slash, then check current directory
// else assume its absolute filepath
// System.getProperty("user.dir");
// URI Root command line argument, if not specified curr dirr
// Port command line argument, 
// print usage if we fail to bind to port:
// If port below 1024, need root access in linux.
// package source->edu.cmu.pact.HTTPServer.
class TSLTHandler implements HttpHandler{
	private static String TutoringServiceURI = "C:\\Users\\Borg\\Desktop\\pact-cvs-tree\\AuthoringTools\\java\\test\\edu\\cmu\\pact\\TutoringService";
	public void handle(HttpExchange arg0) throws IOException {
		// TODO Auto-generated method stub
		//System.getProperties()
		String requestMethod = arg0.getRequestMethod();
		String fileURI = arg0.getRequestURI().toString();
		byte[] buffer = new byte[1024];
		trace.out("Bytes cast as chars from requestbody");
		while(arg0.getRequestBody().available() > 0){
			trace.out((char)arg0.getRequestBody().read());
		}
		trace.out("END Request body");
		trace.out("FileURI: " + fileURI);
		if(requestMethod.equalsIgnoreCase("get")){
		    String response = "";
		    /*Crossdomain.xml Requested.
		     *For successful exchange:
		     *crossdomain.xml must be in the folder specified by TutoringServiceURI
		     *Valid content-type must be specified in the responseheader:
		     *"application/xml" is accepted by flash 8.0..
		     */
		    if(fileURI.equalsIgnoreCase("/crossdomain.xml")){
		    	trace.out("Writing back the crossdomain policy...");
		    	trace.out("Trying to open crossdomain.xml");
		    	try{
			    	BufferedReader br = new BufferedReader(new FileReader(TutoringServiceURI + "\\crossdomain.xml"));
			    	while(br.ready()){
		    			response += br.readLine();
		    		}
			    	br.close();
		    		trace.out(response);
		    		arg0.getResponseHeaders().add("Content-Type", "application/xml");
		    		arg0.sendResponseHeaders(200, response.getBytes().length);
		    		arg0.getResponseBody().write(response.getBytes());
		    		trace.out("Wrote back Crossdomain.xml..");
		    		arg0.close();
			    	trace.out("closed httpexchange");
		    	}catch(Exception e){
		    		trace.out(e.toString() + "Exception in trying to write back response or opening crossdomain.xml");
		    	}
		    }
		    /*Some other "fileuri" is  Requested.
		     *For successful exchange:
		     *"fileuri" must be in the folder specified by TutoringServiceURI
		     *Only headers in response are 200(OK response), the http version,
		     *and the content-type being text/*
		     *The content-type text/* might not work for non-text files.. not sure...
		     */
		    else{
		    	fileURI = TutoringServiceURI+fileURI;
		    	trace.out("Trying to open " + fileURI);
		    	try{
		    		BufferedReader br = new BufferedReader(new FileReader(fileURI));
		    		trace.out("opened file..");
		    		while(br.ready()){
		    			int temp = br.read();
		    			if(temp!=-1)
		    				response+=(char)temp;
		    			else break;
		    		}
		    		br.close();
		    		trace.out("read file of length : " + response.length());
		    		trace.out(response);
		    		arg0.getResponseHeaders().add("Content-Type", "text/*");
		    		arg0.sendResponseHeaders(200, response.getBytes().length);
		    		arg0.getResponseBody().write(response.getBytes());
		    		trace.out("Wrote back crap from a file..");
		    		arg0.close();
				    trace.out("closed..");
		    	}catch(Exception e){
		    		trace.out(e.toString() + "Exception in trying to write back response or opening requested URI");
		    	}
		    } 
		}
	}
}