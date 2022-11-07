// author: kjeffries

/** 
 * This class is intended as a replacement for com.sun.net.httpserver.HttpExchange,
 * which is not available on some platforms (e.g. Android). It represents an HTTP
 * request from a client and a response to be generated in exchange. Each instance
 * of this class represents only one request/response pair, and the socket connection
 * is closed after the response is sent. Currently, persistent connections are not used.
 * 
 * Methods should usually be called in this order as necessary:
 * getRequestMethod()
 * getRequestHeaders()
 * getRequestBody()
 * getResponseHeaders() and/or addResponseHeader(String, String)
 * sendResponseHeaders(int, long)
 * getResponseBody()
 * close()
 * 
 * The most important deviation of this class from the original HttpExchange is
 * that this class does not support chunked transfer encoding of responses, so the
 * content length must be known and specified BEFORE the response body is sent, so
 * a response body cannot be generated and sent on the fly without already knowing
 * its length.
 */

package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;

public class CTATHTTPExchange extends CTATBase 
{
	private static final String TUTORSHOP_COOKIE_DELETED = "tutorshop_cookie_deleted";
	private boolean initialized = false; // will be set to true upon successful completion of the constructor
	private Socket socket=null;
	private Map<String, List<String>> requestHeaders = null;
	private Map<String, String> requestHeadersConcatenated = null;
	private Map<String, List<String>> responseHeaders = null;
	private byte[] requestBody;
	private InputStream requestBodyStream = null;
	private String requestMethod;
	private URI requestURI;
	private String requestProtocolString;
	private boolean responseHeadersSent = false;
	private InputStream requestIn; // used only internally; requestBodyStream is what getRequestBody() returns
	//private OutputStream responseOut; // initialized in sendResponseHeaders and returned by getResponseBody()
	private int responseCode = -1;
	private boolean badRequest = false;
	private static final Charset defaultCharset = Charset.forName("ISO-8859-1");
	private Charset requestCharset = defaultCharset;
	private Map<String, String> requestParameters;
	private static final Pattern charsetPattern = Pattern.compile(".*charset=([-a-zA-Z0-9]+)");
	
	private SendOnCloseOutputStream responseTank = null;
		
	private BufferedOutputStream bufferedResponseOut=null;
	
	/**
	 * 
	 */
	public CTATHTTPExchange()
	{
		setClassName ("CTATHTTPExchange");
		debug("CTATHTTPExchange()");		
	}
	/**
	 * Constructs a new CTATHTTPExchange object and parses the request message
	 * received through the specified socket
	 * 
	 * @param s A Socket where a client has submitted an HTTP request
	 * @throws IOException
	 */
	public CTATHTTPExchange(Socket s) throws IOException
	{
		setClassName ("CTATHTTPExchange");
		debug("CTATHTTPExchange()");
		
		socket = s;

		requestIn = new BufferedInputStream (socket.getInputStream());
		
		int bodyLength = readRequestHeaders(requestIn);
		readRequestBody (requestIn, bodyLength);
		
		if(!badRequest)
		{
			initialized=true;
		}
		else
			debug ("Bad request for: " + socket);
	}	
	/**
	 * This will give you a stream that you can write the response body to.
	 * You must send the HTTP headers via {{@link #sendResponseHeaders(int, long)} before calling this method.
	 */
	public BufferedOutputStream getOutputStream ()
	{
		/* Ideally, this should be in here. But it was causing problems with other code.
		if(!responseHeadersSent) {
			debug("getOuputStream() called before sendResponseHeaders() in CTATHTTPExchange");
			return null;
		}
		*/
		
		if (socket==null)
		{
			debug ("Socket is null, already closed?");
			return (null);
		}
		
		OutputStream responseOut =null;
		
		if (bufferedResponseOut==null)
		{
			// Actually send the headers
			try 
			{
				responseOut = socket.getOutputStream();
				
				bufferedResponseOut = new BufferedOutputStream(responseOut);
			} 
			catch (Exception e) 
			{
				debug ("Error sending headers! " + e.getMessage());
				e.printStackTrace();
			}		
		}	
				
		return (bufferedResponseOut);
	}
	/**
	 * 
	 */
	public Socket getSocket ()
	{
		return (socket);
	}
	/**
	 * 
	 */
	public void checkSocket ()
	{
		debug ("checkSocket ()");
		
		if (socket==null)
		{
			debug ("Socket is null");
			return;
		}
		
		if (socket.isBound()==true)
		{
			debug ("Socket is bound");
		}
		else
			debug ("Socket is unbound");
		
		if (socket.isClosed()==true)
		{
			debug ("Socket is closed");
		}
		else
			debug ("Socket is open");
		
		if (socket.isConnected()==true)
		{
			debug ("Socket is connected");
		}
		else
			debug ("Socket is not connected");			
	}
	/**
	 * Constructs a new CTATHTTPExchange object and parses the request message
	 * received through the specified socket
	 * 
	 * @param s A Socket where a client has submitted an HTTP request
	 * @throws IOException
	 */
	public Boolean processSocket (Socket s)
	{
		debug("processSocket()");
		
		socket = s;

		try 
		{
			requestIn = new BufferedInputStream (socket.getInputStream());
		} 
		catch (IOException e) 
		{		
			debug ("IO Exception in getting buffered input stream from socket");
			e.printStackTrace();
			return (false);
		}
		
		int bodyLength=0;
		
		try 
		{
			bodyLength = readRequestHeaders(requestIn);
		} 
		catch (IOException e) 
		{		
			debug ("IO Exception in getting request headers");
			e.printStackTrace();
			return (false);
		}
		
		try 
		{
			readRequestBody (requestIn, bodyLength);
		} 
		catch (IOException e) 
		{
			debug ("IO Exception in reading request body");
			e.printStackTrace();
			return (false);
		}
		
		if(!badRequest)
		{
			initialized=true;
		}
		else
		{
			debug ("Bad request for: " + socket);
			return (false);
		}
		
		return (true);
	}
	
	/**
	 * @return whether this object has been successfully initialized
	 */
	public boolean isInitialized()
	{
		return initialized;
	}
	

	/**
	 * @return the currently open and available input stream
	 */	
	public InputStream getInputStream ()
	{
		return (requestIn);
	}
	
	/**
	 * @return the port number on which the request came in and from which the response will be sent out, or -1 if this object has no associated socket
	 */
	public int getLocalPort() {
		if(socket != null) {
			return socket.getLocalPort();
		} else {
			return -1;
		}
	}
	
	/**
	 * @return the port number from which the request came and to which the response will be sent, or -1 if this object has no associated socket
	 */
	public int getRemotePort() {
		if(socket != null) {
			return socket.getPort();
		} else {
			return -1;
		}
	}

	/**
	 * @return the URI of the requested resource, or null if the constructor failed
	 */
	public URI getRequestURI()
	{
		if(initialized == false)
		{
			return null;
		}
		
		return requestURI;
	}
	
	/**
	 * @return the request method ("GET", "POST", etc.), or null if the constructor failed
	 */
	public String getRequestMethod()
	{
		if(initialized == false)
		{
			return null;
		}
		
		return requestMethod;
	}
	
	/**
	 * @return the address of the local socket for this connection
	 */
	public InetSocketAddress getLocalAddress()
	{
		return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
	}
	
	/**
	 * @return the address of the remote (client) socket for this connection
	 */
	public InetSocketAddress getRemoteAddress()
	{
		return new InetSocketAddress(socket.getInetAddress(), socket.getPort());
	}
	
	/**
	 * @return the protocol version string included in the request (e.g. "HTTP/1.1"), or null if the constructor failed
	 */
	public String getProtocol()
	{
		if(initialized == false)
		{
			return null;
		}
		
		return requestProtocolString;
	}
	
	/**
	 * @return the response code if already set, or -1 if not yet set, or 0 if the constructor failed
	 */
	public int getResponseCode()
	{
		if(initialized == false)
		{
			return 0;
		}
		
		return responseCode;
	}
	
	/**
	 * This method is used to examine the headers that were present in the request. Headers
	 * are represented as a Map between the header field name (a String) and a List of the value
	 * or values (either comma-separated or on multiple header lines) for that header field.
	 * 
	 * @return a Map containing the request headers, or null if the constructor failed
	 */
	public Map<String, List<String>> getRequestHeaders()
	{
		if(initialized == false)
		{
			return null;
		}
		
		// create a copy of the request headers map (to prevent the caller from altering it) and return the copy
		Map<String, List<String>> mapCopy = new TreeMap<String, List<String>>(requestHeaders);
		Set<String> keySet = requestHeaders.keySet();
		for(String key : keySet)
		{
			List<String> oldList = requestHeaders.get(key);
			List<String> newList = new ArrayList<String>();
			for(String value : oldList)
			{
				newList.add(value);
			}
			mapCopy.put(key, newList);
		}
		return mapCopy;
	}

	/**
	 * Retrieve the value of a given request header.
	 * @param name header name
	 * @return header value or null
	 */
	public List<String> getRequestHeader(String name) {
		if(initialized == false)
			return null;
		if (name == null || name.length() < 1)
			return null;
		name = name.toLowerCase();
		List<String> value = requestHeaders.get(name);
		if (value == null)
			return null;
		return new ArrayList<String>(value);
	}

	/**
	 * Retrieve all values of a given request header concatenated, embedding
	 * any commas that would otherwise separate values. Some header values,
	 * such as dates, have embedded commas. For any header that appears more
	 * than once, each respective value is separated by a comma.
	 * @param name header name
	 * @return concatenated header value or null
	 */
	public String getRequestHeaderConcatenated(String name) {
		if(initialized == false)
			return null;
		if (name == null || name.length() < 1)
			return null;
		name = name.toLowerCase();
		return requestHeadersConcatenated.get(name);
	}
	
	/**
	 * This method returns the body of the request method, after chunked transfer encoding
	 * has been removed, but with other transfer codings possibly still in place.
	 * 
	 * @return an InputStream containing the request body, or null if the constructor failed
	 */
	public InputStream getRequestBody()
	{
		debug ("getRequestBody() ");
		
		if(initialized == false)
		{
			return null;
		}
		
		if(requestBodyStream == null)
		{
			requestBodyStream = new ByteArrayInputStream(requestBody);
		}
		
		return requestBodyStream;
	}
	
	/**
	 * This method can be called to get a mutable Map of headers that will be sent
	 * (by calling sendResponseHeaders) as part of the response. See also addResponseHeader.
	 * 
	 * @return a mutable Map of the response headers that will be sent, or null if the constructor failed
	 */
	public Map<String, List<String>> getResponseHeaders()
	{
		debug ("getResponseHeaders() ");
		
		if(initialized == false)
		{
			return null;
		}
		
		if(responseHeaders == null)
		{
			responseHeaders = new TreeMap<String, List<String>>();
		}
		
		return responseHeaders;
	}
	
	/**
	 * This convenience method adds the specified response header value to the list of values
	 * for the specified field name. It allows a name-value pair to be added to the response
	 * headers without dealing directly with the List of values.
	 * 
	 * @param fieldName the name of the header field
	 * @param fieldValue the value to be added to the list of values for this field
	 * @return a Map containing the new response headers, or null if the constructor failed
	 */
	public Map<String, List<String>> addResponseHeader(String fieldName, String fieldValue)
	{
		debug ("addResponseHeader ("+fieldName+","+fieldValue+")");
		
		if(initialized == false)
		{
			debug ("Error: CTATHTTPExchange object has not been initialized");
			return null;
		}
		
		if(responseHeaders == null)
		{
			responseHeaders = new TreeMap<String, List<String>>();
		}
		
		if(responseHeaders.containsKey(fieldName))
		{
			// if a list of values already exists for this field name, just add to the list
			responseHeaders.get(fieldName).add(fieldValue); 
		}
		else
		{
			// if a field with this name does not exist, make it, along with a new list containing fieldValue
			List<String> valueList = new ArrayList<String>();
			valueList.add(fieldValue);
			responseHeaders.put(fieldName, valueList);
		}
		
		return responseHeaders;
	}
	/**
	 * 
	 */
	public void send404 (String aMessage)
	{
		debug ("send404 ("+aMessage+")");
		
		addResponseHeader("Content-Type","text/plain");
		
		sendResponseHeaders (404,aMessage.length());
		
		//BufferedOutputStream buf = new BufferedOutputStream(getResponseBody());
		BufferedOutputStream buf = this.getOutputStream();
		
		try 
		{
			buf.write(aMessage.getBytes(), 0, aMessage.length());
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			buf.flush();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		//close();
	}
	/**
	 * 
	 */
	public void sendOptions ()
	{
		debug ("sendOptions ()");
		
		String aMessage="";
		
		//addResponseHeader ("Access-Control-Allow-Origin","*");
		//addResponseHeader ("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
		addResponseHeader ("Content-Type","text/plain");
		addResponseHeader ("Allow","GET,HEAD,POST,OPTIONS,TRACE");
						
		sendResponseHeaders (200,aMessage.length());
		
		BufferedOutputStream buf = this.getOutputStream();
		
		try 
		{
			buf.write(aMessage.getBytes(), 0, aMessage.length());
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			buf.flush();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		//close();
	}	
	/**
	 * Sends the current response headers, as set by manipulating the Map returned by
	 * getResponseHeaders or by adding headers through addResponseHeader. This method must
	 * be called before calling getResponseBody.
	 * 
	 * @param rCode the HTTP response code, e.g. 200 for OK
	 * @param responseLength the exact length in bytes of the message body to be sent. Must be nonnegative. 
	 * @throws IOException
	 */
	public void sendResponseHeaders(int rCode, long responseLength) //throws IOException
	{
		//trace.printStack("ll", "sendResponseHeaders ("+rCode+","+responseLength+")");
		
		debug ("sendResponseHeaders ("+rCode+","+responseLength+")");
		
		if(initialized == false)
		{
			debug ("Error: CTATHTTPExchange object has not been initialized");
			return;
		}
		
		if(responseTank != null) {
			debug("Can't call sendResponseHeaders if responseTank is being used");
			return;
		}
		
		if(responseHeaders == null)
		{
			debug ("INFO: no response headers available, generating new ones ...");			
			responseHeaders = new TreeMap<String, List<String>>();
		}
		else
			debug ("We have proper pre-existing response headers, re-using ...");
		
		//this.setResponseHeader ("Access-Control-Allow-Origin","*");
		//this.setResponseHeader ("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
		
		//responseHeaders = new TreeMap<String, List<String>>();
		
		responseCode = rCode;
		
		final String CRLF = "\r\n"; // line terminator for HTTP headers
		
		// construct the status line (initial line of response)
		String statusLine = "HTTP/1.1 " + rCode + " " + getReasonPhrase(rCode) + CRLF;
		
		// Modify headers as necessary. The content length must be specified if known
		// and Connection must be "close" because keep-alive is not (yet) supported.
		if(responseLength > 0)
		{
			List<String> contentLengthList = new ArrayList<String>();
			contentLengthList.add("" + responseLength);
			responseHeaders.put("Content-Length", contentLengthList);
		}
		
		List<String> connectionList = new ArrayList<String>();
		connectionList.add("close"); // TODO implement persistent connections in the future?
		responseHeaders.put("Connection", connectionList);
				
		//this.getOutputStream();
		
		bufferedResponseOut=this.getOutputStream();
		
		if (bufferedResponseOut==null)
		{
			debug ("bufferedResponseOut == null");
		}
				
		/*
		BufferedOutputStream bufferedResponseOut=null;
		
		try 
		{
			bufferedResponseOut = new BufferedOutputStream(socket.getOutputStream());
		} 
		catch (IOException e) 
		{
			debug ("Error obtaining buffered output stream for outgoing socket!");
			e.printStackTrace();
			return;
		}
		*/
		
		/*
		if (bufferedResponseOut==null)
		{
			debug ("Internal error: can't create output buffered stream");
			return;
		}
		*/
		
		try 
		{
			bufferedResponseOut.write(statusLine.getBytes("ISO-8859-1"));
		} 
		catch (UnsupportedEncodingException e) 
		{
			debug ("Error UnsupportedEncodingException!");
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			debug ("Error generic IO exception writing response");
			e.printStackTrace();
		}
		
		Set<String> keySet = responseHeaders.keySet();
		
		//debug ("Processing keySet: " + keySet.size());
		
		for (String key : keySet)
		{
			StringBuilder thisLine = new StringBuilder(key + ": ");
			List<String> values = responseHeaders.get(key);
			Iterator<String> valuesIter = values.iterator();
			
			while(valuesIter.hasNext())
			{
				thisLine.append(valuesIter.next());
				if(valuesIter.hasNext())
				{
					if("Set-Cookie".equalsIgnoreCase(key))
						thisLine.append(CRLF).append(key).append(": ");
					else
						thisLine.append(", ");
				}
			}
			
			thisLine.append(CRLF);
			
			if(key.contains("Cookie"))
				debug("Sending Cookie directive:\n  "+thisLine);
			
			//trace.out("Writing: " + thisLine.toString());
			
			try 
			{
				bufferedResponseOut.write(thisLine.toString().getBytes("ISO-8859-1"));
			} 
			catch (UnsupportedEncodingException e) 
			{
				debug ("Error: UnsupportedEncodingException");
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				debug ("Error: unspecified error exception");
				e.printStackTrace();
			}
		}
		
		debug ("Writing bytes ...");
		
		try 
		{
			bufferedResponseOut.write(CRLF.getBytes("ISO-8859-1"));
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			debug ("Error UnsupportedEncodingException!");
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			debug ("Error IO error writing response bytes");
			e.printStackTrace();
		}
		
		debug ("Flushing socket ...");
		
		try 
		{
			bufferedResponseOut.flush();
		} 
		catch (IOException e) 
		{
			debug ("Error flushing response output");
			e.printStackTrace();
		}
		
		// DON'T CLOSE bufferedResponseOut !!
		
		responseHeadersSent = true;
		
		debug ("Response sent");
	}
	
	/**
	 * Call this method to begin sending a response body. The OutputStream that is returned comes
	 * directly from the Socket, so there is no enforcement of content length and no support
	 * for chunked transfer encoding. However, the amount of bytes written to the stream should be
	 * equal to the amount specified in the call to sendResponseHeaders. 
	 * 
	 * @return an OutputStream to which the response body can be written, or null if the constructor failed
	 */
	/*
	public OutputStream getResponseBody()
	{		
		debug ("getResponseBody ()");
		
		if (initialized==false)
		{
			debug ("Error: CTATHTTPExchange object has not been initialized");
			return null;
		}
		
		if (!responseHeadersSent)
		{
			debug ("Error: The response headers haven't been sent yet");
			return null;
		}
		
		if (responseOut==null)
		{
			debug ("We don't have an outputstream yet, acquiring ...");
			
			try 
			{
				responseOut = socket.getOutputStream();
			} 
			catch (IOException e) 
			{
				debug("Problem getting socket's OutputStream. "+ e);
			}
		}

		return responseOut;
	}
	*/
	
	/**
	 * It is not necessary to call {@link #sendResponseHeaders(int, long)} before calling this method.
	 * The headers and message will be sent when {@link #close()} is called.
	 * 
	 * @return an OutputStream to which a response body of arbitrary length can be written
	 */
	public OutputStream getResponseTank() {
		if(initialized && !responseHeadersSent) { // if response headers have already been sent, just use getOutputStream, not getResponseTank
			if(responseTank == null) {
				responseTank = new SendOnCloseOutputStream(this);
			}
			return responseTank;
		}
		else return null;
	}
	
	private void sendReponseTank() throws IOException {
		if(initialized && !responseHeadersSent && responseTank != null) {
			// calc the length of the message, send that in the Content-Length header, then send the message itself
			sendResponseHeaders(200, responseTank.size());
			OutputStream out = getOutputStream();
			responseTank.writeTo(out);
			out.close();
			return;
		}
	}
	
	/**
	 * Closes the streams and socket. This method should be called after sending a request. 
	 */
	public void close()
	{
		debug ("close ()");
		
		if (initialized == false)
		{
			debug ("initialized == false");
			return;
		}
		
		if (socket==null)
		{
			debug ("Socket already closed");
			return;
		}
		
		
		if(responseTank != null) {
			try {
				responseTank.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (bufferedResponseOut!=null)
		{
			debug ("Closing output buffer ...");
			
			try {
				bufferedResponseOut.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try 
			{
				bufferedResponseOut.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			bufferedResponseOut=null;
		}
		
		// Exceptions are ignored because they mean that the stream is already closed
				
		debug ("Closing requestIn");
		
		try 
		{
			if (requestIn != null)
			{
				requestIn.close();
			}
			else
				debug ("Error: requestIn is null");
		} 
		catch (IOException e) 
		{ 
			debug ("Error closing requestIn");
		}
		
		requestIn = null;
						
		debug ("Trying to close socket ...");
		
		if (socket != null)
		{			
			try 
			{
				socket.close();
			} 
			catch (IOException e) 
			{ 
				debug ("Error closing socket");
			}
		}
		else
			debug ("Socket already closed!");
		
		socket = null;
	}
	
	/**
	 * Reads in the headers of an HTTP request, determines the length of the message body,
	 * and initializes the class's requestHeaders maps according to the headers in the request.
	 * Populates {@link #requestHeaders} and {@link #requestHeadersConcatenated}.
	 * 
	 * @param requestIn an InputStream containing the HTTP request message
	 * @return length of the message body in bytes, with -1 indicating chunked transfer encoding
	 * @throws IOException
	 */
	private int readRequestHeaders(InputStream requestIn) throws IOException
	{
		debug ("readRequestHeaders ()");
		
		int contentLength = 0;
		boolean chunkedEncoding = false; // is chunked transfer encoding being used?
		
		// read in the request-line (first line of the request)
		int b; // current byte
		ByteArrayOutputStream requestLine = new ByteArrayOutputStream();
		
		do    // consume any leading newlines
		{
			b = requestIn.read();
		} 
		while(b == (byte)'\n' || b == (byte)'\r');
		
		do    // read in the request-line
		{
			requestLine.write(b);
			
			if(b == (byte)'\n')
			{
				break; // end of request line
			}
			
		} while((b = requestIn.read()) != -1);
		
		// extract information from the request-line
		String [] requestLineComponents = (new String(requestLine.toByteArray(), "ISO-8859-1")).split(" ");
		
		for (int t=0;t<requestLineComponents.length;t++)
		{
			String jsTest=requestLineComponents [t];
			
			//debug ("Request line: " + jsTest);
			
			if (jsTest.indexOf("/http")!=-1)
				requestLineComponents [t]=jsTest.substring(1);
		}
		
		if(requestLineComponents.length != 3) // the request line should have 3 space-separated components
		{
			handleBadRequest();
			return 0;
		}
		
		requestMethod = requestLineComponents[0].trim();
		
		String fullURI=requestLineComponents[1].trim();
		
		debug ("Processing request URI: " + fullURI);
		
		try 
		{
			requestURI = new URI(fullURI);
		} 
		catch (Exception e) 
		{
			handleBadRequest();
			return 0;
		}
		
		requestProtocolString = requestLineComponents[2].trim(); 
		
		// read remaining header lines
		if(requestHeaders == null)
		{
			requestHeaders = new TreeMap<String, List<String>>();
			requestHeadersConcatenated = new HashMap<String, String>();
		}
		
		ByteArrayOutputStream currentLine = new ByteArrayOutputStream();
		b = requestIn.read(); // 'b' represents the current byte being read
		while(b != -1) // while the end of stream has not been reached
		{
			currentLine.write(b);

			if(b == (byte)'\n')  // if this is the end of the current header line
			{
				String currentLineString = new String(currentLine.toByteArray(), "ISO-8859-1");
				if(currentLineString.equals("\r\n") || currentLineString.equals("\n"))
				{
					// blank line indicates end of headers
					break;
				}

				int nextByte = requestIn.read();
				if(nextByte != (byte)' ' && nextByte != (byte)'\t') // A newline signifies the end of a header field UNLESS followed by a space or tab (line folding)
				{
					int colonPosition = currentLineString.indexOf(':'); // colon separates field name from field value
					if(colonPosition < 0)
					{
						handleBadRequest();
						return 0;
					}
					String fieldName = currentLineString.substring(0, colonPosition).trim().toLowerCase();
					// "entire" because it may be a comma-separated list of sub-values; trim per RFC 2616 sec 4.2
					String entireFieldValue = currentLineString.substring(colonPosition + 1).trim();
					String[] fieldValues = entireFieldValue.split(","); // split the comma-separated list
					List<String> fieldValueList = new ArrayList<String>();
					for(String fieldValue : fieldValues)
					{
						fieldValueList.add(fieldValue.trim());
					}

					if(!requestHeaders.containsKey(fieldName))
					{
						requestHeaders.put(fieldName, fieldValueList);
						requestHeadersConcatenated.put(fieldName, entireFieldValue);
					}
					else // a field with this name has already been encountered
					{
						// append this field value list to the end of the already existing list
						List<String> newValueList = requestHeaders.get(fieldName);
						for(String value : fieldValueList)
							newValueList.add(value);
						requestHeaders.put(fieldName, newValueList);
						
						String newValue = requestHeadersConcatenated.get(fieldName);
						newValue = newValue+","+entireFieldValue;
						requestHeadersConcatenated.put(fieldName, newValue);						
					}

					if(fieldName.equalsIgnoreCase("Cookie"))
							debug("Cookie: (raw) "+currentLineString+"\n  (table): "+
									requestHeaders.get("Cookie"));
					
					if(fieldName.equalsIgnoreCase("Content-Length"))
					{
						contentLength = Integer.valueOf(fieldValues[0].trim()); // remember the specified content-length
					}
					else if(fieldName.equalsIgnoreCase("Transfer-Encoding"))
					{
						if(!fieldValues[0].equalsIgnoreCase("identity"))
						{
							// If there is a transfer encoding other than "identity", chunked transfer encoding is being used
							chunkedEncoding = true;
						}
					}
					else if(fieldName.equalsIgnoreCase("Content-Type"))
					{
						for(String field : fieldValues) {
							Matcher m = charsetPattern.matcher(field);
							if(!m.find())
								continue;
							String charsetName = m.group(1);
							try {
								requestCharset = Charset.forName(charsetName);
							} catch (Exception e) {
								trace.errStack("Error interpreting charset name \""+
										charsetName+"\" in HTTP header "+fieldName, e);
								requestCharset = defaultCharset;
							}
							break;
						}
					}

					currentLine = new ByteArrayOutputStream(); // make a fresh new buffer to hold the next line
				}

				b = nextByte;
			}
			else
			{
				b = requestIn.read();
			}
		}

		if(chunkedEncoding)
		{
			contentLength = -1; // a return value of -1 indicates chunked transfer encoding is being used
		}
		
		return contentLength;
	}
	
	/**
	 * Reads the message body of an HTTP request and stores the body in requestBody[].
	 * 
	 * @param requestIn an InputStream from which the request body can be read (headers having already been read)
	 * @param contentLength length in bytes of the message body. Negative indicates chunked transfer encoding.
	 * @throws IOException
	 */
	private void readRequestBody(InputStream requestIn, int contentLength) throws IOException
	{	
		debug ("readRequestBody ()");
		
		if(contentLength >= 0)
		{
			// read in contentLength number of bytes
			requestBody = new byte[contentLength];
			int n = 0;
			while(n < contentLength)
			{
				n += requestIn.read(requestBody, n, contentLength - n);
			}
			requestParameters = extractRequestParameters(requestBody);
		}
		else // chunked transfer encoding
		{
			boolean lastChunkRead = false;
			ByteArrayOutputStream messageBody = new ByteArrayOutputStream(); // used to store the message body as it is read
			while(!lastChunkRead)
			{
				// read first line of chunk to determine chunk size
				ByteArrayOutputStream firstLine = new ByteArrayOutputStream(); // holds the first line of the chunk (which indicates the chunk's size)
				int b; // current byte
				while((b = requestIn.read()) != -1)
				{
					firstLine.write(b);
					
					if(b == (byte)'\n')
					{
						break; // end of first line
					}
				}
				String firstLineString = new String(firstLine.toByteArray(), "ISO-8859-1");
				int semicolonPosition = firstLineString.indexOf(';'); // semicolon separates length-of-chunk from rest of first line
				String chunkSizeHex; // the chunk size is given as a hexadecimal string to the left of the semicolon
				if(semicolonPosition == -1) // there is no semicolon in the first line of the chunk
				{
					chunkSizeHex = firstLineString.trim(); 
				}
				else
				{
					chunkSizeHex = firstLineString.substring(0, semicolonPosition).trim();
				}
				int chunkSize = Integer.parseInt(chunkSizeHex, 16); // convert hex string to integer
				
				// A chunk size of zero indicates the last chunk
				if(chunkSize == 0)
				{
					lastChunkRead = true;
				}
				else
				{
					// This is not the last chunk, so there is data to be read. Read chunkSize number of bytes.
					for(int i = 0; (i < chunkSize) && ((b = requestIn.read()) != -1); i++)
					{
						messageBody.write(b);
					}
					
					// Consume the CRLF (or just LF) that separates chunks
					b = requestIn.read();
					if(b != '\r' && b != '\n')
					{
						handleBadRequest();
						return;
					}
					if(b == '\r')
					{
						b = requestIn.read();
						if(b != '\n')
						{
							handleBadRequest();
							return;
						}
					}
				}
			}
			
			/* TODO: Consume anything following the chunks (trailers or CRLF); this is only necessary if persistent connections are being used */
			
			requestBody = messageBody.toByteArray();
			
			// Remove the "chunked" value from the Transfer-Encoding header, because that transfer-coding has been removed from the message body
			// The code below basically does requestHeaders.get("Transfer-Encoding").remove("chunked"), but with case insensitivity.
			Set<String> keySet = requestHeaders.keySet();
			for(String fieldName : keySet)
			{
				if(fieldName.equalsIgnoreCase("Transfer-Encoding"))
				{
					List<String> transferEncodingValues = requestHeaders.get(fieldName);
					for(String value : transferEncodingValues)
					{
						if(value.equalsIgnoreCase("chunked"))
						{
							transferEncodingValues.remove(value);
							if(transferEncodingValues.size() == 0)
							{
								requestHeaders.remove(fieldName); // remove the entire "Transfer-Encoding" header if "chunked" was the only value
							}
							return;
						}
					}
				}
			}
			
		}
		
		return;
	}

	/**
	 * @return the requestParameters
	 */
	public Map<String, String> getRequestParameters() 
	{
		if(requestParameters == null)
			return new LinkedHashMap<String, String>();
		return requestParameters;
	}

	/**
	 * Create a map of key,value pairs from the POST data in the requestBody.
	 * @param requestBody
	 * @return map
	 */
	private Map<String, String> extractRequestParameters(byte[] requestBody) 
	{
		debug ("extractRequestParameters ()");
		
		int s, e = 0;
		Map<String, String> requestParameters = null;
		while(e < requestBody.length) {
			int c = requestBody[e];
			while(c == '&' && ++e < requestBody.length)
				c = requestBody[e];
			if (e >= requestBody.length)
				break;
			s = e;
			while(c != '&' && ++e < requestBody.length)
				c = requestBody[e];
			String paramPair;
			try {
			paramPair = new String(/*Arrays.*/copyOfRange(requestBody,s, e),
					requestCharset.name()); // in Java 1.6 and later, passing the charset itself works, but in 1.5 the charset's string name, and the try-catch, is needed
			} catch(java.io.UnsupportedEncodingException ex) { paramPair = new String(/*Arrays.*/copyOfRange(requestBody, s, e)); }
			int k = paramPair.indexOf('=');
			if(requestParameters == null)
				requestParameters = new LinkedHashMap<String, String>();
			if(k > 0)
				requestParameters.put(paramPair.substring(0, k),
						paramPair.substring(k+1));
			else if(k < 0)
				requestParameters.put(paramPair, null);
		}
		return requestParameters;
	}

	/**
	 * duplicates functionality of copyOfRange method in java.util.Arrays for compatibility with Java 1.5
	 */
	public static byte[] copyOfRange(byte[] original, int from, int to)
	{
		//debug ("copyOfRange ()");
		
		if(from > to)
			throw new IllegalArgumentException("`from` is greater than `to`");
		if(original == null)
			throw new NullPointerException("`original` is null");
		if(from < 0 || from > original.length)
			throw new ArrayIndexOutOfBoundsException((from < 0) ? "`from` is negative" : "`from` is greater than length of `original`");
		
		byte[] result = new byte[to - from];
		int end = ((to < original.length) ? to : original.length);
		for(int i = 0; i + from < end; ++i)
			result[i] = original[i + from];
		
		return result;
	}
	
	/**
	 * Gets a corresponding "reason phrase" for a given response code
	 * 
	 * @param rCode an HTTP response code
	 * @return a String describing that response code
	 */
	private String getReasonPhrase(int rCode)
	{
		switch(rCode)
		{
		case 100:
			return "Continue";
		case 101:
			return "Switching Protocols";
		case 200:
			return "OK";
		case 201:
			return "Created";
		case 202:
			return "Accepted";
		case 203:
			return "Non-Authoritative Information";
		case 204:
			return "No Content";
		case 205:
			return "Reset Content";
		case 206:
			return "Partial Content";
		case 300:
			return "Multiple Choices";
		case 301:
			return "Moved Permanently";
		case 302:
			return "Found";
		case 303:
			return "See Other";
		case 304:
			return "Not Modified";
		case 305:
			return "Use Proxy";
		case 307:
			return "Temporary Redirect";
		case 400:
			return "Bad Request";
		case 401:
			return "Unauthorized";
		case 402:
			return "Payment Required";
		case 403:
			return "Forbidden";
		case 404:
			return "Not Found";
		case 405:
			return "Method Not Allowed";
		case 406:
			return "Not Acceptable";
		case 407:
			return "Proxy Authentication Required";
		case 408:
			return "Request Time-out";
		case 409:
			return "Conflict";
		case 410:
			return "Gone";
		case 411:
			return "Length Required";
		case 412:
			return "Precondition Failed";
		case 413:
			return "Request Entity Too Large";
		case 414:
			return "Request-URI Too Large";
		case 415:
			return "Unsupported Media Type";
		case 416:
			return "Requested range not satisfiable";
		case 417:
			return "Expectation Failed";
		case 500:
			return "Internal Server Error";
		case 501:
			return "Not Implemented";
		case 502:
			return "Bad Gateway";
		case 503:
			return "Service Unavailable";
		case 504:
			return "Gateway Time-out";
		case 505:
			return "HTTP Version not supported";
		default:
			return "Status code " + rCode;	
		}
	}
	
	/**
	 * Sends a "bad request" response to the client and closes the socket.
	 * 
	 * @throws IOException
	 */
	private void handleBadRequest() throws IOException
	{
		debug ("handleBadRequest ()");
		
		badRequest = true;
		
		final String response = "HTTP/1.1 400 Bad Request\r\nConnection: close\r\n\r\n";
		socket.getOutputStream().write(response.getBytes("ISO-8859-1"));
		socket.close();
	}

	/**
	 * Return the value of the named parameter from {@link #requestParameters}.
	 * @param name key in {@link #requestParameters}
	 * @return value from {@link #requestParameters}; null if no {@link #requestParameters}
	 */
	public String getRequestParameter(String name) 
	{
		if (requestParameters == null)
			return null;
		else
			return requestParameters.get(name);
	}
	
	/**
	 * Pretty-print for debugging
	 * @return multi-line formatted string 
	 */
	public String toString() 
	{
		StringBuilder sb = new StringBuilder(requestMethod)
				.append(" ").append(requestURI)
				.append(" ").append(requestProtocolString);
		return sb.toString();
	}

	/**
	 * Set a response header, replacing any existing values. 
	 * @param hdrName
	 * @param newValue
	 */
	public void setResponseHeader(String hdrName, String newValue) 
	{
		debug ("setResponseHeader ("+hdrName+","+newValue+")");
		
		List<String> values = getResponseHeaders().get(hdrName);
		
		if (values == null)
		{
			addResponseHeader(hdrName, newValue);
		}
		else 
		{
			values.clear();
			values.add(newValue);
			responseHeaders.put(hdrName, values);  // shouldn't be necessary
		}
	}

	/**
	 * Get the value of the named cookie from the request header.
	 * @param name
	 * @return cookie value; null if not found
	 */
	public String getRequestCookie(String name) 
	{
		debug ("getRequestCookie ("+name+")");
		
		String result = null;
		List<String> cookies = getRequestHeader("Cookie");
		if (cookies != null) {
			for (Iterator<String> it = cookies.iterator(); it.hasNext(); ) {
				String cookie = it.next();
				if (cookie == null || !cookie.startsWith(name))
					continue;
				if (cookie.length() < name.length()+2) // +2 for '=.' 
					result = "";
				else
					result = cookie.substring(name.length()+1);
			}
		}
		if (trace.getDebugCode("cookie"))
			trace.out("cookie", "getRequestCookie("+name+") to return "+
					(TUTORSHOP_COOKIE_DELETED.equalsIgnoreCase(result) ? null : result));
		if (TUTORSHOP_COOKIE_DELETED.equalsIgnoreCase(result)) {
			trace.err("getRequestCookie found "+TUTORSHOP_COOKIE_DELETED+"; returning null");
			result = null;
		}
		return result;
	}

	/**
	 * Add or replace a cookie value in the {@link #responseHeaders}.
	 * @param name cookie identifier
	 * @param value new value; null to direct the browser to delete the cookie
	 */
	public void setResponseCookie(String name, String value) 
	{
		debug ("setResponseCookie ("+name+","+value+")");
		
		if (value == null)       // use Max-Age=0 to direct browser to delete
			value = TUTORSHOP_COOKIE_DELETED+"; Expires=Thu, 01-Jan-1970 00:00:01 GMT";
		if (trace.getDebugCode("cookie"))
			trace.out("cookie", "setResponseCookie("+name+", "+value+")");
		List<String> cookies = responseHeaders.get("Set-Cookie");
		if (cookies == null)
			addResponseHeader("Set-Cookie", name+'='+value);
		else {
			for (int i = 0; i < cookies.size(); ++i) {
				String cookie = cookies.get(i);
				if (cookie == null || !cookie.startsWith(name))
					continue;
				cookies.set(i, name+'='+value);
				return;
			}
			cookies.add(name+'='+value);
		}
	}
	/**
	 * 
	 */
	public void addMimeType ()
	{
		debug ("addMimeType ()");
		
		// this ensures that CSS will be rendered properly on certain browsers
		if(getRequestURI().getRawPath().endsWith(".css"))
		{
			addResponseHeader("Content-Type", "text/css");
		}
			
		if(getRequestURI().getRawPath().endsWith(".js"))
		{
			addResponseHeader("Content-Type", "application/javascript");
		}
			
		if(getRequestURI().getRawPath().endsWith(".xml"))
		{
			addResponseHeader("Content-Type", "text/xml");
		}			
			
		if (getRequestURI().getRawPath().endsWith(".png"))
		{
			addResponseHeader("Content-Type","image/png");
		}
			
		if (getRequestURI().getRawPath().endsWith(".gif"))
		{
			addResponseHeader("Content-Type","image/gif");
		}
			
		if (getRequestURI().getRawPath().endsWith(".jpg"))
		{
			addResponseHeader("Content-Type","image/jpg");
		}
			
		if (getRequestURI().getRawPath().endsWith(".swf"))
		{
			addResponseHeader("Content-Type","application/x-shockwave-flash");
		}		
	}
	/**
	 * Test harness for {@link #extractRequestParameters(byte[])}.
	 * @param ampersand-delimited "name=value" parameter pair(s)
	 */
	public static void main(String[] args) 
	{
		CTATHTTPExchange che = new CTATHTTPExchange();
		
		for(String arg : args) 
		{
			Map<String, String> requestParameters =
				che.extractRequestParameters(arg.getBytes());
			System.out.printf("\n%s :\n", arg);
			
			if (requestParameters == null)
				continue;
			
			for(String key : requestParameters.keySet())
				System.out.printf("  %s=%s\n", key, requestParameters.get(key));						
		}
		
		che.close();
	}	

	/** Cache for {@link #getRequestBodyAsString()}. */
	private String requestBodyAsString = null; 
	
	/**
	 * Read the entire input stream {@link #getRequestBody()} into a String; assume UTF-8 encoding.
	 * @return request body; empty string if {@link #getRequestBody()} returns null
	 */	
	public String getRequestBodyAsString () throws IOException 
	{
		if(requestBodyAsString == null) {
			requestBodyAsString = convertStreamToString( getRequestBody() );
			debug( "getRequestBodyAsString:\n"+requestBodyAsString );
		}
		return requestBodyAsString; 
	}	
	
	/**
	 * Read an entire input stream into a String; assume UTF-8 encoding.
	 * @param is stream to read 
	 * @return stream contents; empty string if argument null
	 */	
	public static String convertStreamToString (InputStream is) throws IOException 
	{
		if (is != null) 
		{
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			
			try 
			{
				Reader reader = new BufferedReader (new InputStreamReader(is, "UTF-8"));
				int n;
				
				while ((n = reader.read(buffer)) != -1) 
				{
					writer.write(buffer, 0, n);
					if(trace.getDebugCode("ll"))
						trace.outNT("ll", "convertStreamToString() nBytes="+n);
				}
			} 
			finally 
			{
				is.close();
			}
			
			return writer.toString();
			
		} 
		else 
		{       
			return "";
		}
	}	
	
	private static class SendOnCloseOutputStream extends ByteArrayOutputStream {
		private CTATHTTPExchange parent;
		
		public SendOnCloseOutputStream(CTATHTTPExchange parent) {
			super();
			this.parent = parent;
		}
		
		@Override
		public void close() throws IOException{
			parent.sendReponseTank();
		}
	}

	public String getIPAddress() {
		if(socket == null || socket.getInetAddress() == null)
			return null;
		return socket.getInetAddress().getHostAddress();
	}
	
 }
