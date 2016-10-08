// Kevin Jeffries, 2013-04-16

package edu.cmu.pact.SocketProxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.XML;

import edu.cmu.hcii.ctat.CTATHTTPExchange;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.TutoringService.LauncherHandler;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.UniversalToolProxy;

public class HTTPToolProxy extends RemoteToolProxy {
	
	/** Bundle name known to HTML5 client. */
	private static final String CTAT_RESPONSE_MESSAGES = "CTATResponseMessages";

	/** Whether to convert final output to JSON. */
	private static boolean outputJSON = false;

	/** Contains the output stream for the response. */
	private CTATHTTPExchange exchange;
	
	public HTTPToolProxy(CTATHTTPExchange exchange, int format, BR_Controller controller) {
		super();
        this.controller = controller;
		this.exchange = exchange;
		setFormat(format);
	}

	/**
	 * @return the {@link #outputJSON}
	 */
	public static boolean getOutputJSON() {
		return outputJSON;
	}

	/**
	 * @param outputJSON new value for {@link #outputJSON}
	 */
	public static void setOutputJSON(boolean outputJSON) {
		HTTPToolProxy.outputJSON = outputJSON;
	}

	/**
	 * @param msgType {@link MessageObject#getMessageType()} value to test
	 * @param commShellVersion determines whether client is new enough to support msgType
	 * @return {@link Boolean#TRUE} this override always returns true
	 * @see UniversalToolProxy#clientSupports(String, String)
	 */
	public Boolean clientSupports(String msgType, String commShellVersion) {
		return Boolean.TRUE;
	}
	
	/**
	 * Send a single String. Opens a socket on localhost port
	 * {@link #destPort}, sends str, closes the connection.
	 *
	 * @param  str String to send
	 */
	public synchronized void sendXMLString(String str) {
		str = formatMsgString(str);
		try {
			if (trace.getDebugCode("tsltstp"))
				trace.outNT("tsltstp", exchange.getRequestHeaderConcatenated(LauncherHandler.CTATSESSION_HEADER)+" "+str);
			
//			OutputStream os = exchange.getResponseTank(); // put the string in holding tank. It will be sent when exchange.close() is called
//			byte[] bytes = str.getBytes("UTF-8");
//			os.write(bytes);
			byte[] bytes = str.getBytes(encoding);
			exchange.addResponseHeader("Access-Control-Allow-Origin", "*");
			if(getOutputJSON())
				exchange.addResponseHeader("Content-Type", "text");
			exchange.sendResponseHeaders(200, bytes.length);
			OutputStream os = exchange.getOutputStream();
			os.write(bytes);

			exchange.close();
		} catch(Exception e){
			//e.printStackTrace();
			trace.err("SocketToolProxy failed to connect outgoing socket to Tutor Interface: " + e.toString());
		}
	}

	/**
	 * Apply final formatting to the output.
	 * @param str
	 * @return edited str
	 */
	static String formatMsgString(String str) {
		String result;
		if(getOutputJSON())
			result = XML.toJSONObject(str).toString(2); 
		else if(!str.startsWith("<?"))
			result = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>"+str;
		else
			result = str;
		return result;
	}

	/**
	 * Text of socket policy request, used by Flash's security apparatus.
	 * See {@link #handlePolicyFileRequest(String, Socket)}.
	 */
	private static final String policyFileRequest = "<policy-file-request/>";
	
	/**
	 * Text of response to socket policy request, used by Flash's security apparatus.
	 * See {@link #handlePolicyFileRequest(String, Socket)}. Value is:
	 * <pre>
	 * {@value #socketPolicyContent}
	 * </pre>
	 */
    private static final String socketPolicyContent = "<cross-domain-policy>\n"+
				    	"<site-control permitted-cross-domain-policies=\"master-only\"/>\n"+
				    	"<allow-access-from domain=\"*\" to-ports=\"*\" />\n" +
				    	"</cross-domain-policy>\0";
    
    /** Character encoding of XML output. */
    private static final String encoding = "UTF-8";
	
	/**
	 * This code was copied from {@link edu.cmu.pact.SocketProxy.SocketProxy#handlePolicyFileRequest(String, Socket)}
	 * with changes made to use HTTP instead of bare TCP socket
	 * 
     * Check whether the given message is a socket policy request. If so, will reply with
     * {@link #socketPolicyContent} and close the socket.
	 * See the Flash Player document
	 * <a href="http://www.adobe.com/devnet/flashplayer/articles/fplayer9_security_05.html#_Configuring_Socket_Policy">
	 * Configuring Socket Policy</a> for details.
     * @param msg string received from socket
     * @param pSock socket to respond to
     * @return true if msg was a policy file request: if true is returned, pSock has been closed;
     *         false (no-op) otherwise
     */
	public static boolean handlePolicyFileRequest(String msg, /*Socket pSock*/ CTATHTTPExchange exchange) {
		try {
			if (msg == null)
				throw new IOException("null message from socket");
			if (!msg.regionMatches(0, policyFileRequest, 0, policyFileRequest.length()))
				return false;
			/*This is a policy file request*/
			if (trace.getDebugCode("ls")) trace.outNT("ls", "SocketProxy.handlePolicyFileRequest()\nReceived a policy request on "+
					"local "+exchange.getLocalPort()+", remote "+exchange.getRemotePort()+"\n");
			PrintWriter pw = new PrintWriter(exchange.getResponseTank());
			pw.write(socketPolicyContent);
			pw.close();
			exchange.close();
		} catch (IOException ioe) {
			StringBuffer errMsg = new StringBuffer("Error in security handshake with Flash");
			errMsg.append(".\nMore info: exception sending policy file response: ").append(ioe);
			if (ioe.getCause() != null)
				errMsg.append("\n cause: ").append(ioe.getCause());
			trace.errStack(errMsg.toString(), ioe);
			// if !isOnline(), JOptionPane.showMessageDialog() to warn user.
			try { if (exchange != null) exchange.close(); } catch (Exception e) {}
			// fall through to return true since socket now closed
		}
		return true;
	}

	/**
	 * @return {@link #exchange}
	 */
	public CTATHTTPExchange getHttpExchange() {
		return exchange;
	}

	/**
	 * Put this message in the {@link #CTAT_RESPONSE_MESSAGES} bundle.
	 * @param newMessage
	 * @param flush if true, flush the bundle
	 */
	public void bundleResponse(MessageObject newMessage, boolean flush) {
		bundleMessage(newMessage, CTAT_RESPONSE_MESSAGES, flush);		
	}

	/**
	 * @return {@link #exchange}.{@link CTATHTTPExchange#getSocket() getSocket()
	 * @see pact.CommWidgets.RemoteToolProxy#getSocket()
	 */
	protected Object getSocket() {
		return exchange.getSocket();
	}
}
