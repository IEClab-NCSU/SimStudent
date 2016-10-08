/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.TutoringService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.Utilities.trace;

/**
 * Type for handling a single kind of {@link Monitor} request.
 */ 
public abstract class RequestHandler extends CTATBase implements Runnable, Cloneable 
{		
	/** Socket for continuing communications. */
	private transient Socket cSock = null;
	
	/** Input stream on socket. */
	private transient Reader in;
	
	/** Output stream on socket. */
	private transient PrintWriter out;
	
	/**
	 * 
	 */
	public RequestHandler ()
	{
    	setClassName ("RequestHandler");
    	debug ("RequestHandler ()");    			
	}		
	/** 
	 * Blank the i/o fields in the clone. 
	 */
	protected RequestHandler clone() throws CloneNotSupportedException 
	{
		debug ("clone ()");
		
		RequestHandler newObj = (RequestHandler) super.clone();
		newObj.cSock = null;
		newObj.in = null;
		newObj.out = null;
		return newObj;
	}
	/**
	 * @param sock new value for {@link #cSock}
	 * @param in   new value for {@link #in}
	 * @param out  new value for {@link #out}
	 */
	void setSocketIO (Socket sock, Reader in, PrintWriter out) 
	{
		debug ("setSocketIO ()");
		
		this.cSock = sock;
		this.in = in;
		this.out = out;
	}
	/**
	 * Read and handle a stream of requests.
	 */
	public void run() 
	{
		debug ("run ()");
		
		while (true) 
		{            // exit on client quit or i/o error
			Element root = null, resp = null;
			String request = null;
			
			try 
			{
				request = SocketProxy.readToEom(in, 0x00);
				
				if (trace.getDebugCode("ls"))
					trace.outNT("ls", "RequestHandler.handleRequest() '"+request+"'");
				
				if (request == null || request.length() <= 0) 
				{ 
					// got EOF
					if (trace.getDebugCode("ls"))
						trace.outNT("ls", "RequestHandler.run() got empty request '"+
								request+"'; exiting...");
					close();
					return;
				}
				
				if (gotQuitMessage(request))
					return;
			} 
			catch (IOException ioe) 
			{ 
				// superclass of SocketException
				trace.errStack("Monitor: thread exiting upon error reading socket "+cSock+
						": "+ioe+(ioe.getCause() == null ? "" : ";\n  cause "+ioe.getCause()), ioe);
				close();
				return;
			}
			
			try 
			{
				SAXBuilder builder = new SAXBuilder();	
				Document doc = builder.build(new StringReader(request));
				root = doc.getRootElement();
				resp = handleRequest(root);
			} 
			catch (Throwable t) 
			{
				trace.errStack("Monitor: error handling request "+	(root == null ? "(null)" : root.getName()), t);
				resp = new Element("error");
				resp.setAttribute("exception", t.getClass().getName());
				resp.addContent(t.getMessage());
			}
			
			try 
			{
				Monitor.outputter.output(resp, out);
				out.write('\0');
				out.flush();
			} 
			catch (Throwable t) 
			{
				trace.errStack("Monitor: error writing socket to reply to request "+(root == null ? "(null)" : root.getName()), t);
				gotQuitMessage(null);   // close socket
				return;
			}
		}
	}
	/**
	 * Permit client to explicitly quit the connection by sending
	 * "&lt;quit/&gt;" or by a null message (indicates a closed socket).  
	 * Closes and nulls {@link #in}, {@link #out}, {@link #cSock}.
	 * @param request message from client
	 * @return false if request not a quit; else true after i/o closed
	 */
	private boolean gotQuitMessage(String request) 
	{
		debug ("gotQuitMessage ()");
		
		if (request != null && request.length() > 0  && !("<quit/>".equals(request)))
			return false;
		
		if (trace.getDebugCode("ls")) 
			trace.out("ls", "Monitor.gotQuitMessage("+request+") from socket "+cSock);
		
		close();

		return true;
	}
	/**
	 * Close {@link #in}, {@link #out}, {@link #cSock}. Handles all exceptions.
	 */
	private void close() 
	{
		debug ("close ()");
		
		if (in != null) 
		{
			try { in.close(); } catch (Exception e) {}
		}
		
		if (out != null) 
		{
			try { out.close(); } catch (Exception e) {}
		}
		
		if (cSock != null) 
		{
			try { cSock.close();} catch (Exception e) {}
		}
		
		in = null;
		out = null;
		cSock = null;
	}

	/**
	 * Respond to a single request.
	 * @param req request
	 * @return XML element to write as response 
	 */
	public abstract Element handleRequest(Element req);
}
