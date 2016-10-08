/**
 -
 License:
 -
 ChangeLog:
 $Log: CTATHTTPHandlerBase.java,v $
 Revision 1.3  2012/08/30 15:25:33  sewall
 Fix-ups after Alvaro's 2012/08/17 merge.

 -
 Notes:
 -
*/

package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.cmu.pact.Utilities.trace;

public class CTATHTTPHandlerBase extends CTATBase implements CTATHTTPHandlerInterface
{
	private boolean logging=false;
	private FileOutputStream logFile=null;
	private String logFileName;
	
	private FileOutputStream logTutorFile;
	
	private static boolean oldJavaVersion=false; // true if using Java 1.5 or earlier
	
	/**
	 *
	 */	
	public CTATHTTPHandlerBase (String aFileName)	
	{
    	setClassName ("CTATHTTPHandlerBase");
    	debug ("CTATHTTPHandlerBase ("+aFileName+")");
    	
    	//>--------------------------------------------------------------
    	
    	// get JRE version info so as to avoid code that doesn't work on older JREs
    	String version = System.getProperty("java.version");
    	
    	if(version == null)  // this shouldn't ever happen but just in case
    	{
    		oldJavaVersion = true;
    	}
    	else
    	{
    		String[] split = version.split("\\.");
    		try
    		{
    			if(split[0].equals("1") && Integer.valueOf(split[1]) < 6)
    			{
    				oldJavaVersion = true;
    			}
    			else
    			{
    				oldJavaVersion = false;
    			}
    		}
    		catch(IndexOutOfBoundsException e) // version string doesn't follow major.minor convention
    		{
    			oldJavaVersion = true;
    		}
    	}    	
    	
    	//>--------------------------------------------------------------
    	
		if (aFileName!="" && CTATLink.allowWriting==true)
		{
			this.logFileName = aFileName;
			
			File pleaseWork = new File (logFileName);
			
			if (pleaseWork.getParentFile()!=null)
			{
				if(!pleaseWork.getParentFile().exists())
				{
					pleaseWork.getParentFile().mkdirs();
				}
			}	
			
			try 
			{
				pleaseWork.createNewFile();
				
				if(!oldJavaVersion)
				{
					pleaseWork.setReadable(true);
					pleaseWork.setWritable(true);
				}
				
				logFile = new FileOutputStream (pleaseWork, true);
				
				logging = true;				
			} 
			catch (IOException e) 
			{			
				e.printStackTrace();
				
				logging=false;
			}			
		}
		else
			logging = false;
		
		//>---------------------------------------------------------------
		
		if(CTATLink.allowWriting)
		{
			String filename = CTATLink.datashopFile;
			if(filename == null || filename.trim().length() < 1)
				filename = CTATLink.logdir+"datashop.log";

			File sessionLog = new File (filename);
			
			try 
			{
				sessionLog.createNewFile();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!oldJavaVersion)
			{
				sessionLog.setReadable(true);
				sessionLog.setWritable(true);
			}
			
			try 
			{
				logTutorFile=new FileOutputStream (sessionLog,true);
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//>---------------------------------------------------------------		
	}
	/**
	 * 
	 */
	protected Boolean getLogging ()
	{
		return (logging);
	}
	/**
	 * 
	 */
	protected String getLogFile ()
	{
		return(logFileName);
	}	
	/**
	 *
	 */	
	public synchronized boolean writeToLog (String logXMLString)
	{
		debug ("writeToLog ()");
		
		if(!logging)
			return false;
		
		String finalLogStream=logXMLString+"\n";
		
		try 
		{
			for (int i=0; i<finalLogStream.length();++i)
			{
				logTutorFile.write((byte) finalLogStream.charAt(i));
			}
			
			logTutorFile.flush ();
		} 
		catch (IOException e) 
		{
			debug ("IOexception trying to write to datashop logFile: " + logFileName);
			e.printStackTrace();
			return false;
		}		

		return true;
	}	
	/**
	 *
	 */	
	public synchronized boolean writeToAccessLog (String aURI)
	{
		debug ("writeToAccessLog ()");
		
		if(logging==false)
			return false;
		
		StringBuffer formatted=new StringBuffer ();
		formatted.append("[");
		formatted.append(getCurrentDate ());
		formatted.append ("] 127.0.0.1 ");
		formatted.append (aURI);
		formatted.append ("\n");
		
		String converter=formatted.toString();
		
		try 
		{
			for (int i=0; i<converter.length();++i)
			{
				logFile.write((byte)converter.charAt(i));
			}
			
			logFile.flush ();
		} 
		catch (IOException e) 
		{
			debug ("IOexception trying to write to logFile: " + logFileName);
			e.printStackTrace();
			return false;
		}
		return true;
	}	
	/**
	 * This method is a proxy that forwards HTTP requests to a remote server (CTATLink.remoteHost) and sends the
	 * server's response back to the client. It's simple in principle but the implementation
	 * can get tricky, so future changes should be made with these points in mind: (1) The HttpURLConnection
	 * class follows redirects by default, which can be a problem if cookies are used for user
	 * authentication, because HttpURLConnection has no knowledge of cookies. (2) If sending
	 * HTTP requests (strings) over a Socket without using the HttpURLConnection class,
	 * persistent connections ("keep-alive") may be an issue. Make sure to send the header
	 * "Connection: close" to indicate that no more requests are to be sent. (3) Some header fields
	 * must be "translated" before being sent from client to server or from server to client.
	 * In particular, the "host" and "referer" request headers should not contain the local host's name,
	 * and the "location" response header of a redirect response should not contain the remote host's name.
	 * 
	 * This method uses a cache to efficiently fulfill requests. The cache is not revalidated. The cache
	 * can be refreshed from the admin page, accessed with command line option "-admin".
	 * 
	 * @param exchange the HTTP exchange between the client and local host ("proxy")
	 * @param fileURI the requested resource to be provided by the remote server
	 * @param addToCache whether to cache the response content (only for GET requests)
	 */	  	
	protected synchronized boolean handleViaRemoteServer(CTATHTTPExchange exchange, String fileURI, boolean addToCache)
	{	
		debug ("handleViaRemoteServer ()");
		
		return (true);
	}
	/**
	 * 
	 */
	protected boolean sendLocalFile (String preFileURI,
									 String prePathToRoot,
									 CTATHTTPExchange arg0)
	{
		debug ("sendLocalFile ("+preFileURI+")");
		
		String fileURI=null;
		
		try 
		{
			fileURI = java.net.URLDecoder.decode(preFileURI, "UTF-8");
		} 
		catch (UnsupportedEncodingException e1) 
		{
			e1.printStackTrace();
		}
		
		if (fileURI==null)
			fileURI=preFileURI;
		
		String pathToRoot=null;
		
		try 
		{
			pathToRoot = java.net.URLDecoder.decode(prePathToRoot, "UTF-8");
		} 
		catch (UnsupportedEncodingException e1) 
		{		
			e1.printStackTrace();
		}
		
		if (pathToRoot==null)
			pathToRoot=prePathToRoot;
		
		if (CTATLink.cache==null)
		{
			debug ("No static cache object available yet, creating ...");
			
			CTATLink.cache=new CTATContentCache ();
		}
		
		/* If the path to root has been included in the file URI, remove it */
		if (fileURI.contains(pathToRoot)) 
		{
			fileURI = "/" + fileURI.substring(fileURI.indexOf(pathToRoot) + pathToRoot.length());
			debug("new fileURI: " + fileURI);
		}
		
		/* If the request is for the admin page, admin privileges are required */
		if(fileURI.equalsIgnoreCase("/admin.html") && CTATLink.adminLogin == false)
		{
			CTATLink.lastError="403 - forbidden";
			
			arg0.sendResponseHeaders(403, 0); // 403 = forbidden
			
			//arg0.close();
			
			return (true);
		}
    	
		//fileURI = pathToRoot+ File.separator + fileURI.substring (1);
		
		String concat=pathToRoot + fileURI.substring (1);
		
		String cleaned = concat.replace( '\'', '/' );
    	
		debug ("Trying to open: " + cleaned);
    	
		BufferedInputStream bis = null;
		File requestedFile;
		long contentLength;
		String[] lastModified = new String[1];
    		
		try // first try to find the file locally
		{
			if(cleaned.endsWith(".brd") && CTATLink.BRDsAreEncrypted) // local BRDs are stored in encrypted form
			{
				// a BRD file has been requested. If it is stored locally, it must first be decrypted.
				String decrypted = CTATLink.fManager.getContentsEncrypted(cleaned); // decrypt the file
				if(decrypted == null)
				{
					throw new IOException(); // the file cannot be found locally; request it from the remote server
				}

				bis = new BufferedInputStream(new ByteArrayInputStream(decrypted.getBytes("UTF-8")));
				contentLength = decrypted.getBytes("UTF-8").length;
			}
			else // no decryption necessary
			{
				CTATContentCache.Status cs = send304IfNotModified(fileURI, arg0);

				byte[] contentBytes=null;
					
				if (cs!=CTATContentCache.Status.CACHE_INVALID)
				{
					if (CTATContentCache.Status.NOT_MODIFIED.equals(cs))
					{
						debug ("return 304 Not Modified if caller's date recent enough");
						CTATLink.lastError="304 - Not Modified if caller's date recent enough";
						return (true); 
					}
						
					// try to look for the file in the cache	    					
					
					if (CTATContentCache.Status.READ_FROM_CACHE.equals(cs))
					{
						debug ("We have the file in the cache, retrieving ...");
						
						contentBytes = CTATLink.cache.getBytesFromCache(fileURI, lastModified, true);
					}
					else
						debug ("Requested file does not exist in the cache!");
				}
					
				if (contentBytes!=null)
				{
					debug("Content successfully retrieved from cache; last-modified "+lastModified[0] + " with size: " + contentBytes.length);
					
   					contentLength = contentBytes.length;
   					
   					bis = new BufferedInputStream(new ByteArrayInputStream(contentBytes));   					   					
   				}
   				else
   				{
   					debug ("File was not found in the cache, attempting to load directly from disk ...");
   					
   					requestedFile = new File(cleaned);
    					
   					if(!requestedFile.exists())
   					{
   						String withoutUID = CTATContentCache.stripOutUID(cleaned);
   	   					requestedFile = new File(withoutUID);
   	   					
   	   					if(!requestedFile.exists())
   	   						throw new IOException();
   	   					else
   	    					debug ("File " + withoutUID  + " exists, sending to browser ...");
   					}
   					else
   						debug ("File " + cleaned  + " exists, sending to browser ...");
    					
   					bis = new BufferedInputStream(new FileInputStream(requestedFile));
    					
   					contentLength = requestedFile.length();
    					
   					debug ("Opened requested file successfully");
   				}
			}
		}
		catch (IOException e) // IOException means the file could not be found locally
		{
			debug ("Exception: " + e.getMessage());

			if(CTATLink.remoteHost.equals("") || CTATLink.remoteHost.equals("local"))
			{
				debug("The file still wasn't found, and there is no remote server. Sending 404...");

				CTATLink.lastError="The file still wasn't found, and there is no remote server";
				
				arg0.send404(CTATLink.lastError);

				return (true); // the response has been sent
			}
				
			debug ("The file still wasn't found, try connecting to the remote server ...");
			
			String URIpath = arg0.getRequestURI().getRawPath(); // the "path" does not include the query string -- this makes it easier to find the file extension
			boolean addToCache = true;
			if(URIpath.length() < 5 || !URIpath.substring(URIpath.length() - 5).contains("."))
			{
				addToCache = false; // don't cache anything without an extension
			}
			if(fileURI.contains("timestamp"))
			{
				addToCache = false; // don't cache time-sensitive information
			}
			
			boolean success = handleViaRemoteServer(arg0, fileURI, addToCache);
			
			if(success)
			{
				debug("GET request was satisfied via the remote server.");
			}
			else
			{
				CTATLink.lastError="Error when attempting to satisfy GET request via remote server.";
				
				debug (CTATLink.lastError);
				
				arg0.sendResponseHeaders(500, 0); // 500 = Internal server error
				//arg0.close();
			}
				
			return (true); // the response has already been sent
		}
			
		debug ("Sending a response ...");

		Date now = new Date();
		Calendar expires = Calendar.getInstance();
		expires.add(Calendar.HOUR, 12);
		arg0.addResponseHeader("Date", CTATWebTools.headerDateFmt.format(now));
		arg0.addResponseHeader("Expires", CTATWebTools.headerDateFmt.format(expires.getTime()));
			
		if (lastModified[0] != null)
   			arg0.addResponseHeader("Last-Modified", lastModified[0]);
		//arg0.addResponseHeader("Content-Type", "*/*"); // This was causing problems on Safari -- kjeffries, 2012-07-20
		
		arg0.addMimeType ();
							
		arg0.sendResponseHeaders (200, contentLength);
		
		debug ("Obtaining buffered output stream for socket to start writing ...");

		BufferedOutputStream buf = arg0.getOutputStream ();
		
		int res;
		int count=0;
				
		try 
		{
			while((res = bis.read()) != -1)
			{
				//debug ("Writing " + res + " bytes ...");

				buf.write(res);
				count++;
			}
		}
		catch (IOException e) 
		{
			debug ("IO error writing bytes, at " + count + " bytes");
			e.printStackTrace();
			return (false);
		}
				
		debug ("Wrote " + count + " bytes");

		debug ("Flushing socket ...");

		try 
		{
			buf.flush();
		} 
		catch (IOException e) 
		{
			debug ("Error flushing output buffer");
			e.printStackTrace();
			return (false);
		}
					    		
		debug ("Wrote back the entire file succesfully.");
    		
		try 
		{
			bis.close();
		} 
		catch (IOException e) 
		{
			debug ("Error: unable to close bis");
			e.printStackTrace();
		}
				
		//arg0.close();				
						    		
		writeToAccessLog ("<root>");
    						    
		//debug ("Close connection");
						
		return (true);
	}
	/**
	 * Create and send an HTTP 304 Not Modified response if request's "If-Modified-Since"
	 * header date is later than the last-modified date of the file in our cache.
	 * @param fileURI name of file to check, as known to the cache
	 * @param arg0 request-response object to get header and send reply 
	 * @return result of {@link CTATContentCache#isFileUpToDate(String, String)}
	 */
	protected CTATContentCache.Status send304IfNotModified(String fileURI, CTATHTTPExchange arg0) 
	{
		debug ("send304IfNotModified ()");
		
		String ifModifiedSince = arg0.getRequestHeaderConcatenated("If-Modified-Since");
		
		CTATContentCache.Status result = CTATLink.cache.isFileUpToDate(fileURI, ifModifiedSince, true);
		
		if (result == CTATContentCache.Status.NOT_MODIFIED) 
		{
			arg0.addResponseHeader("Connection", "Close");
			sendResponse (arg0, "", 304);
		}
		
		return result;
	}	
	/**
	 * Send a response and flush the output stream.
	 * @param arg0
	 * @param responseBody
	 * @param statusCode
	 */
	public static void sendResponse(CTATHTTPExchange arg0, String responseBody, int statusCode) 
	{
		if(trace.getDebugCode("ll"))
			trace.outNT("ll", "CTATHTTPHandlerBase.sendResponse (\""+responseBody+"\")");
		
		try	
		{
			arg0.setResponseHeader("Access-Control-Allow-Origin","*");
			arg0.sendResponseHeaders(statusCode, responseBody.length());
			//BufferedOutputStream buf = new BufferedOutputStream(arg0.getResponseBody());
			BufferedOutputStream buf =arg0.getOutputStream();
			buf.write(responseBody.getBytes(), 0, responseBody.length());
			buf.flush();
			if(trace.getDebugCode("ll"))
				trace.outNT("ll", "CTATHTTPHandlerBase.sendResponse() POST response written to client.");
		} catch (IOException e) {
			trace.err ("CTATHTTPHandlerBase.sendResponse(): IOError writing back response : "+
					e+"; cause "+e.getCause());
		}
	}

	/**
	 * Send a response with Content-Type "text/html".
	 * @param arg0
	 * @param responseBody
	 * @param statusCode
	 */
	protected void sendHTMLResponse(CTATHTTPExchange arg0, String responseBody, int statusCode) 
	{
		debug ("sendHTMLResponse ()" );
		
		arg0.addResponseHeader("Content-Type", "text/html");
		sendResponse(arg0, responseBody, statusCode);
	}
	/**
	 * Send a string as an HTTP response
	 * @param exchange The object representing this HTTP exchange
	 * @param message The message to send as a response (null means empty string)
	 * @return success/failure
	 */
	protected boolean sendString(CTATHTTPExchange exchange, String message)
	{
		debug ("sendString ()");
		
		boolean success = true;
		
		if(message == null) message = "";
		
		try
		{
			byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
			exchange.sendResponseHeaders(200, bytes.length); // HTTP 200 = OK
			OutputStream out =exchange.getOutputStream();
			out.write(bytes);
			//exchange.close();
		}
		catch(Exception e)
		{
			debug ("Send exception: " + e.getMessage());
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Send a redirect (302) response
	 * @param exchange The object representing this HTTP exchange
	 * @param path the *relative* path to redirect to (should begin with a slash)
	 * @return success/failure
	 */
	protected boolean redirectTo(CTATHTTPExchange exchange, String path)
	{
		debug ("redirectTo ("+path+")");
		
		String fullPath = "http://" + CTATLink.hostName + ":" + CTATLink.wwwPort + path;

		exchange.addResponseHeader("Location", fullPath);
		exchange.sendResponseHeaders(302, 0);
		//exchange.close();
		return true;
	}
	/**
	 *
	 */	
	public int getPOSTContentSize (CTATHTTPExchange arg0)
	{
		debug ("getPOSTContent ()");
				
		List<String> contentLength = arg0.getRequestHeaders().get("Content-Length"); /* arg0.getRequestHeader("Content-Length"); */
		
		if (contentLength!=null)
		{
			int bodySize = Integer.parseInt(contentLength.get(0));
			return bodySize;
		}
		
		return 0;
	}
	/**
	 *
	 */	
	public InputStream getPOSTContentRaw (CTATHTTPExchange arg0)
	{
		debug ("getPOSTContentRaw ()");
							
		InputStream inStream=arg0.getRequestBody();

		return (inStream);
	}	
	/**
	 *
	 */	
	public String getPOSTContent (CTATHTTPExchange arg0)
	{
		debug ("getPOSTContent ()");
							
		String logMessage="";
		
		try 
		{
			logMessage = arg0.getRequestBodyAsString();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
			return (null);
		}
		
		return (logMessage);
	}		
	/**
	 * 
	 * @param arg0
	 * @return
	 */
	@Override
	public boolean handle(CTATHTTPExchange arg0) 
	{
		arg0.send404 ("Error: The server is configured with the default handler class: CTATHTTPHandlerBase");
		
		return false;
	}	
}
