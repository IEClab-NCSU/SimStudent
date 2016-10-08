/**
 * Made in an attempt to convert LauncherServer to work completely over HTTP. 
 * 
 * Kevin Jeffries
 */

package edu.cmu.pact.TutoringService;

import java.io.IOException;

import edu.cmu.hcii.ctat.CTATHTTPExchange;
import edu.cmu.hcii.ctat.CTATHTTPHandlerBase;
import edu.cmu.hcii.ctat.CTATHTTPHandlerInterface;
import edu.cmu.hcii.ctat.CTATLink;
import edu.cmu.pact.SocketProxy.HTTPToolProxy;
import edu.cmu.pact.SocketProxy.LogServlet;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.HTTPMessageObject;

/**
 * 
 * @author sewall
 *
 */
public class LauncherHandler extends CTATHTTPHandlerBase implements CTATHTTPHandlerInterface {

	/** HTTP header name for sessionID (a.k.a. guid) value. */
	public static final String CTATSESSION_HEADER = "ctatsession";

	/** Pass logging requests to this instance. */
	private LauncherServer launcherServer;
	
	LauncherHandler(LauncherServer launcherServer) 
	{
		super (CTATLink.logdir+"/access.log");
		
		this.launcherServer = launcherServer;
	}

	public synchronized boolean handle(CTATHTTPExchange exch) {

		debug ("handle ()");
		
		String requestMethod = exch.getRequestMethod();
		
		debug("Request method: " + requestMethod + ", Request URI: " + exch.getRequestURI());
		
		if (requestMethod.equalsIgnoreCase ("options"))
			return doOptions(exch);

		try {
			String msg = exch.getRequestBodyAsString();
			if (HTTPToolProxy.handlePolicyFileRequest(msg, exch))
				return true;

			if (LogServlet.handleLogRecord(exch, launcherServer.getLogServlet()))
				return true;
		} catch(IOException ioe) {
			sendHTMLResponse(exch, "<p>Error retrieving request body: "+ioe+
					".</p><p>Cause: "+ioe.getCause()+"</p>", 400);  // 400 => Bad Request
			exch.close();
		} catch(Exception e) {
			sendHTMLResponse(exch, "<p>Error processing request: "+e+
					".</p><p>Cause: "+e.getCause()+"</p>", 500);  // 500 => System Error
			exch.close();
		}
			
		String guid = exch.getRequestHeaderConcatenated(CTATSESSION_HEADER);
		
		if(trace.getDebugCode("ls"))
			trace.out("ls", "guid "+guid+" LauncherHandler.handle( "+exch+" )");

		LauncherServer.Session sess = launcherServer.getSession(guid);

		if(sess == null) {
			if(trace.getDebugCode("ls"))
				trace.out("ls", "LauncherHandler accepted new session: guid "+guid+
						", CTATHTTPExchange "+exch.toString());
			HTTPSession hSess = new HTTPSession(launcherServer, guid, exch, this);
			hSess.startActionHandler();
			return true;    // reply is on this thread
		} else if (sess instanceof HTTPSession) {
			postRequestToActionHandler(exch, (HTTPSession) sess);
			return false;   // reply is on other thread
		} else {
			trace.err("Error session "+guid+" type "+sess.getClass().getSimpleName()+
					" not an HTTPSession; request:\n  "+exch);
			sendResponse(exch, "Error session "+guid+" found but not an HTTPSession", 500);
			return true;
		}
	}

	/**
	 * From https://developer.mozilla.org/en-US/docs/HTTP/Access_control_CORS.
	 * @param exch
	 * @return true
	 */
	private boolean doOptions(CTATHTTPExchange exch) {
		String allowHeaders =
				exch.getRequestHeaderConcatenated("Access-Control-Request-Headers");
		exch.addResponseHeader("Access-Control-Allow-Origin", "*");
		exch.addResponseHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		exch.addResponseHeader("Access-Control-Allow-Headers", allowHeaders);
		exch.addResponseHeader("Access-Control-Max-Age", "1728000");  // 20 days
		exch.addResponseHeader("Content-Length", "0");  // needed to complete empty resp?
		sendResponse(exch, "", 200);
		return true;
	}

	/**
	 * Create an HTTPProxy, bundle it with a request and queue them to the session's ActionHandler.
	 * @param msg the request, already extracted from the exchange object 
	 * @param exchange
	 * @param session
	 */
	private void postRequestToActionHandler(CTATHTTPExchange exchange,
			HTTPSession session) {
		HTTPMessageObject mo = new HTTPMessageObject(exchange, session.getMsgFormat(),
				session.getController());
		session.getActionHandler().enqueue(mo);
	}
}
