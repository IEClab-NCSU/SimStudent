/**
 
 $Author: sewall $ 
 $Date: 2013-09-24 09:41:40 -0400 (Tue, 24 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPHandler.java,v 1.31 2012/10/17 20:31:53 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATHTTPHandler.java,v $
 Revision 1.31  2012/10/17 20:31:53  vvelsen
 Added missing files

 Revision 1.30  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.29  2012/09/07 18:15:05  vvelsen
 Quick checkin so that Jonathan can test his CL bridge code

 Revision 1.28  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.27  2012/08/24 20:09:35  kjeffries
 *** empty log message ***

 Revision 1.26  2012/08/23 15:56:06  kjeffries
 show message when curriculum.xml fails to load

 Revision 1.25  2012/08/20 15:31:36  kjeffries
 restore some stuff that was lost in the merge

 Revision 1.20  2012/08/15 20:24:05  kjeffries
 problemEndHandler is notified at the end of each problem

 Revision 1.19  2012/08/13 14:59:12  kjeffries
 *** empty log message ***

 Revision 1.18  2012/08/08 14:27:46  vvelsen
 Did some refactoring to make a start to reduce the size of our http handler class. In that same effort a new method was created for custom http handlers to be provided to our http server

 Revision 1.17  2012/08/07 21:10:19  kjeffries
 add support framework for offline mode and local logins

 Revision 1.16  2012/07/23 16:24:09  kjeffries
 preliminary support for offline mode, fixed bug that happened in Safari, and other assorted changes

 Revision 1.15  2012/06/18 22:02:22  kjeffries
 *** empty log message ***

 Revision 1.14  2012/06/12 19:33:06  kjeffries
 use the cache object in CTATLink if it exists rather than making a new one

 Revision 1.13  2012/06/08 18:27:06  kjeffries
 set system property to fix bug where "via" http header was not being sent

 Revision 1.12  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.11  2012/05/24 16:12:27  kjeffries
 cache contents can now be refreshed in admin mode by asking filesToDownload.php on the server what to download

 Revision 1.10  2012/04/13 19:44:38  vvelsen
 Started adding some proper threading and visualization tools for our download manager

 Revision 1.9  2012/02/08 23:36:07  sewall
 Rest of working version of 304 Not Modified responses, with +12 hr Expires heading.

 Revision 1.8  2012/02/08 21:06:16  sewall
 First working version of 304 Not Modified responses, with +1 day Expires heading.

 Revision 1.7  2012/01/18 22:10:39  sewall
 No longer omit .htm, .html files from the cache.

 Revision 1.6  2012/01/12 15:17:24  sewall
 Fix pattern htdocs_remoteBRDs: extra '.' prevented match in SetPreferences when restoring problem state.

 Revision 1.5  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.55  2011/12/22 21:35:04  sewall
 With possible fixes for the cache-clearing bug traced to the Sophos-imposed NTLM authentication exchange observed at Propel Montour.

 Revision 1.54  2011/12/06 04:21:37  sewall
 No longer edit remoteSocketURL, remoteSocketPort and question_file only when find a Content-Type header with "text/html".

 Revision 1.53  2011/11/28 06:33:43  sewall
 1) New args to SingleSessionLauncher constr. 2) Put debug-trace switch in config panel. 3) Add local path to brd files stored locally. 4) Add run.jar and launcher TutorShopUSB.

 Revision 1.52  2011/11/10 23:50:45  sewall
 1. maxCachedFiles now a CTATLink parameter. 2. no longer delete files from disk cache. 3. hash map lookup for cache. 4. ignore rails timestamps in file names. 5. kill old instance by monitor port request.

 Revision 1.51  2011/10/06 04:26:13  sewall
 Disable exit on timeout in polling: could kill server after application has removed the hidden frame.

 Revision 1.50  2011/10/04 19:00:04  sewall
 Increase non-poll timeout to 30 min.

 Revision 1.49  2011/10/03 07:06:40  sewall
 Patch timeout logic. Timeout now set at 10 min.

 Revision 1.48  2011/10/03 05:23:58  sewall
 Now new instance of tutorshop blanks old instance's browser screen.

 Revision 1.47  2011/10/02 20:57:30  sewall
 Now exit if poll not received for 5 intervals.

 Revision 1.46  2011/09/29 16:32:26  sewall
 Revised SO_LONG to be a little less ugly.

 Revision 1.45  2011/09/29 15:52:16  sewall
 Now replace browser page when user exits server via Swing gui.

 Revision 1.44  2011/09/29 04:10:24  sewall
 Port changes from AuthoringTools/java/source/, where this code is now maintained.

 Revision 1.43  2011/09/27 13:57:27  sewall
 Add CTATHTTPExchange.requestParameters. Change to use student-fs for student. Demo getpush.cgi capability.

 Revision 1.42  2011/09/26 17:00:15  sewall
 Refactoring: add CTATLink.getAdminPassword(), use CTATHTTPHandler.sendResponse() more often. Ajax: add 'getpush.cgi' entry to CTATHTTPHandler.doPost().

 Revision 1.41  2011/09/15 19:03:00  sewall
 Now replace tag 'exitTutorShop' with button to exit.

 Revision 1.40  2011/09/15 13:38:36  sewall
 Added '/exittutorshop.cgi' path for new button on admin.html. Also a little refactoring: sendResponse() and sendHTMLResponse().

 Revision 1.39  2011/08/26 21:04:45  kjeffries
 some small cleanups

 Revision 1.38  2011/08/12 20:43:08  kjeffries
 *** empty log message ***

 Revision 1.37  2011/08/05 21:05:39  kjeffries
 Addded support for use of local tutoring service with remote BRDs

 Revision 1.36  2011/07/28 20:50:52  kjeffries
 handle cases where certain directories do not exist

 Revision 1.35  2011/07/27 21:07:19  kjeffries
 Now displays warning message when attempting to refresh cache when no remote server is specified.

 Revision 1.34  2011/07/22 20:50:04  kjeffries
 Writing to disk can be turned off based on CTATLink.allowWriting. Also cleaned up some of the HTML responses in admin mode

 Revision 1.33  2011/07/20 19:59:38  kjeffries
 Added code to handle end of problem set.

 Revision 1.32  2011/07/18 15:03:23  kjeffries
 Added an admin mode option to refresh the cache, improved proxy "translation" of HTML from remote server to correct problem-to-problem sequencing, and other small fixes.

 Revision 1.31  2011/07/07 21:06:43  kjeffries
 Removed ContentCache class from this class and made it its own top-level class. Added "translation" of absolute URLs in hyperlinks received from remote server. Remote host is now set in the config file and stored as CTATLink.remoteHost.

 Revision 1.30  2011/07/01 17:00:01  kjeffries
 Displays message on failure when uploading data to DataShop from admin page

 Revision 1.29  2011/06/30 16:28:52  kjeffries
 Fixed a bug in the proxy server in which content which came from the remote server using chunked transfer coding had the coding removed by HttpURLConnection but still contained the "Transfer-Encoding: chunked" header in place when forwarding the response to the browser.

 Revision 1.28  2011/06/28 21:26:53  kjeffries
 Changed to use class CTATHTTPExchange rather than HttpExchange which is available only in Java version 6

 Revision 1.27  2011/06/24 19:44:48  kjeffries
 better support for HTTP HEAD requests

 Revision 1.26  2011/06/23 20:58:21  kjeffries
 Increased reliability of proxy. Proxy can now accept HEAD requests, which fixes some problems that some browsers had.

 Revision 1.25  2011/06/17 21:27:50  kjeffries
 Proxy server now can perform POST requests for login, etc. but runs VERY slowly -- maybe should make better use of buffering for I/O

 Revision 1.24  2011/06/10 20:53:29  kjeffries
 Improved proxy server and caching.

 Revision 1.23  2011/06/08 20:44:28  kjeffries
 BRD files can now be decrypted when they are requested via HTTP GET request.

 Revision 1.22  2011/06/03 20:35:34  kjeffries
 Added "proxy server" functionality and caching. Most admin mode options now use POST (not GET) requests. User can choose which DataShop server to upload to.

 Revision 1.21  2011/05/27 20:52:31  kjeffries
 Added admin mode which allows admin to upload data to DataShop and view statistics about problem sets and logs. Admin mode can be accessed through "-admin" command line option. The main page is admin.html on the USB disk image

 Revision 1.20  2011/05/20 20:40:03  kjeffries
 Added code to handle GET requests for a specific problem

 Revision 1.19  2011/05/20 16:25:19  kjeffries
 Did some debugging on the problem-to-problem sequencing.

 Revision 1.18  2011/05/18 15:30:04  kjeffries
 Changed to reflect CTATDirectoryTXT's use of CTATProblemSet rather than CTATDirectoryEntry to represent each problem set.

 Revision 1.17  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central registry. Please see CTATLink for more information.

 Revision 1.16  2011/04/11 19:05:15  vvelsen
 Fixed a bug where the problem set menu wasn't generated for pages other than the root or the index page of a problem set.

 Revision 1.15  2011/04/08 15:31:30  vvelsen
 Full implementation now of problem set navigation using the floating menu. The code has now changed to ensure that all problem set navigation is derived from the directory.txt index file.

 Revision 1.14  2011/04/08 14:19:17  vvelsen
 Added code that reads in the directory.txt file in the FlashTutors directory. It will also show the contents of this file as a floating navigational menu in the top left of every page.

 Revision 1.13  2011/04/07 19:56:00  vvelsen
 First version that has complete problem set sequencing handling.

 Revision 1.12  2011/04/01 20:09:57  vvelsen
 Further features and refinements in the problem sequencing code.

 Revision 1.11  2011/03/30 16:25:55  vvelsen
 Almost finished problem selection and problem sequencing. The tutor now also properly sets the top navigation menu.

 Revision 1.10  2011/03/29 18:59:53  vvelsen
 More capabilities in the management of a problem set.

 Revision 1.9  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 Revision 1.8  2011/02/17 17:43:47  vvelsen
 Small cleanups to move regularly used strings into CTATLink

 Revision 1.7  2011/02/16 16:42:10  vvelsen
 Added more refinement to the update class that will work either in the background or at a time when nobody is doing tutoring.

 Revision 1.6  2011/02/15 15:20:42  vvelsen
 Added proper datashop logging to disk through http in the built-in web server. Added encryption code for logfiles, text based streams, etc. Added a first update scheduler that can be used to both retrieve new versions of the application as well as new content and student info.

 Revision 1.5  2011/02/10 14:57:37  sewall
 Remove leading slash from sessionLog filename "/logs/datashop.log" to make relative path.

 Revision 1.4  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 Revision 1.3  2011/02/08 14:42:54  vvelsen
 More features added. The server now properly generates html that can load swf files. SWF files loaded through that html connect back to the built-in tutoring service but the code still needs a lot of work to deal with brd path checking and Flash security handling. Web server log files are now properly generated.

 Revision 1.2  2011/02/07 19:50:15  vvelsen
 Added a number of files that can manage local files as well as generate html from parameters and templates.

 Revision 1.1  2011/02/06 16:55:27  vvelsen
 Added a first working version of a standalone USB based TutorShop.

 $RCSfile: CTATHTTPHandler.java,v $ 
 $Revision: 19533 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPHandler.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
	Log format:
 
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Instructions-Default.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Hint-Default.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Back-Default.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Done-Default.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Start-Default.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Done-Click.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Back-Click.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Instructions-Click.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Hint-Click.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Hint-Hover.png HTTP/1.1" 304 -
		64.246.196.144 - - [03/Feb/2011:18:50:12 -0500] "GET /images/skindata/Instructions-Hover.png HTTP/1.1" 304 -
 
 
*/

package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
//import java.net.URLEncoder;
//import java.rmi.server.UID;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

class CTATHTTPHandler extends CTATHTTPHandlerBase implements CTATHTTPHandlerInterface
{
	/**
	 * Statuses for the poll alarm.
	 */
	public enum AlarmState 
	{
		INACTIVE,  // initial state
		SNOOZE,    // awaiting timeout or poll
		POLL,      // poll received
		TIMEOUT,   // timeout expired
		INTERRUPT, // wait interrupted by exception
		CANCEL     // alarm cancelled
	}
	
	/** Timer for polling. */
	private class PollAlarm 
	{
		volatile AlarmState state = AlarmState.INACTIVE;
		Thread alarm = null;
	}
	
	/** Alarm object. */
	private PollAlarm pushAlarm = new PollAlarm();

	/**
	 * Operations that can be pushed to the browser frame. 
	 */
	public static enum PushResponse 
	{
		Nothing,
		Exit,        // client gets SO_LONG and server exits
		ClientExit   // client just gets SO_LONG
	}		

	// This is no longer needed because the program is exited via the system tray icon:
	//private static final String exitTutorShop = "<form method=\"POST\" action=\"/exittutorshop.cgi\"><input type=\"submit\" value=\"Exit TutorShop\" ></form>";
	
	/** Document to write when TutorShop server is exiting. */
	private static final String SO_LONG = 
		"<html>\n"+
		"  <head>\n"+
		"    <title>TutorShop Exiting</title>\n"+
		"    <style>body { background-color: #DDDDDD; font-family: Verdana, sans-serif; }</style>\n"+
		"  </head>\n"+
		"  <body>\n"+
		"    <table width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n"+
		"      <tr>\n"+
		"        <td valign=\"middle\" align=\"center\">\n"+
		"          <h2>This session has ended. Local TutorShop exiting...</h2>\n"+
		"        </td>\n"+
		"      </tr>\n"+
		"    </table>\n"+
		"  </body>\n"+
		"</html>\n";
	
	private UserProgressDatabase userProgressDatabase = new UserProgressDatabase();	
	private String currentAssignment;
			
	/** Name of the cookie holding a forward-to address. */
	private static final String TUTORSHOP_FORWARD_TO = "tutorshop_forward_to";
	
	private static String updateProgressPage = "<html><header></header><body><center><table width=\"100%\" height=\"100%\"><tr><td valign=\"middle\" align=\"center\"><h1>System update in progress, can't run student assignments at this point. Please try again later</h1></td></tr></table></center></body></html>";	
		
	private String pathToRoot;
		
	private CTATContentCache cache;
	
	/** A queue of operations that this server wants to push to the client. */
	private Queue<PushResponse> pushQueue = new LinkedList<PushResponse>();

	/** Unique identifier of this server instance. */
	//private String serverInstanceId = "S"+new UID();
	
	/** URL-encoded version of {@link #serverInstanceId}. */
	//private String encodedServerInstanceId = URLEncoder.encode(serverInstanceId, "UTF-8");
		
	private static long totalBytesReceivedRemotely = 0; // for debugging and evaluation of cache efficiency
	
	Pattern remoteSocketURL = Pattern.compile("remoteSocketURL\\s*=\\s*[^&]*");
	Pattern remoteSocketPort = Pattern.compile("remoteSocketPort\\s*=\\s*\\d*");
	Pattern htdocs_remoteBRDs = Pattern.compile(CTATLink.htdocs+"remoteBRDs.");
		
	/**
	 *
	 */	
	public CTATHTTPHandler (String pathToRoot, String logFileName) throws IOException 
	{
		super (logFileName);
		
    	setClassName ("CTATHTTPHandler");
    	debug ("CTATHTTPHandler ()");	
    	
    	// forces java.net.HttpURLConnection to accept "Via" header
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); 
		    	    	    	    	
		this.pathToRoot = pathToRoot;
				
		if(!CTATLink.remoteHost.equals("") && !CTATLink.remoteHost.equals("local")) // if the remoteHost string is non-empty and not "local"
		{
			if(CTATLink.cache == null)
			{
				File cacheDirectory = new File(CTATLink.htdocs + "/cache/");
				if(!cacheDirectory.exists())
				{
					cacheDirectory.mkdir();
				}
				cache = new CTATContentCache(cacheDirectory, CTATLink.allowWriting);
				CTATLink.cache = cache;
			}
			else
			{
				cache = CTATLink.cache;
			}
		}
		else // no remote host; run local tutors
		{
			cache = new CTATContentCache(); // no arguments for constructor because cache is not to be written to disk
		}
	}
	/**
	 *
	 */	
	public String convertStreamToString (InputStream is) throws IOException 
	{
		debug ("convertStreamToString ()");
		
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
							
		InputStream inStream=arg0.getRequestBody();
		String logMessage="";
		
		try 
		{
			logMessage = convertStreamToString (inStream);
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
	 */	
	public void doPost (CTATHTTPExchange arg0)
	{
		debug ("doPost ()");
		
		/*
		if (processCustomPost (arg0)==true)
		{
			debug ("Custom POST handler has processed the message, no need to further process");
			return;
		}
		*/
		
		boolean found=false;
				
		CTATWebTools urltools=new CTATWebTools ();
		
		String fileURI = arg0.getRequestURI().toString();
		URI queryURI=arg0.getRequestURI();
		
		urltools.showURI (queryURI);
		
		String responseBody="";
		
		//>---------------------------------------------------------------------
		
		if (fileURI.contains("/log/server"))
		{
			found=true;
			
			debug ("Logging DataShop traffic to disk ...");
			
			String logMessage=getPOSTContent (arg0);
			if (logMessage!=null)
			{
				Boolean result = writeToLog (logMessage);
        	
				//arg0.getResponseHeaders().add ("Content-Type", "text/html; charset=ISO-8859-1");
			
				if(result==false)
				{
					arg0.sendResponseHeaders(500, 0);
				}
				else
				{
					responseBody += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
					responseBody += "<log-result>\n";
					responseBody += "  <read-request success=\"true\" length=\"" + getPOSTContentSize (arg0) + "\" />\n";
					responseBody += "  <write-file success=\"true\">\n    ";
					responseBody += pathToRoot+"/log/server";
					responseBody += "\n  </write-file>\n";
					responseBody += "</log-result>\n";
					
					arg0.addResponseHeader("Content-Type", "text/xml"); /* arg0.addResponseHeader("Content-Type", "text/xml") */
					sendResponse(arg0, responseBody, 200);  // 200 => OK
				}
				
				arg0.close();
			}	
		}
			    
    	//>---------------------------------------------------------------------
    	
    	if(fileURI.contains("/refreshcache"))
    	{
    		found = true;
    		
    		if(CTATLink.adminLogin)
    		{
    			if(CTATLink.remoteHost.equals("") || CTATLink.remoteHost.equals("local"))
    			{
    				JOptionPane.showMessageDialog(null, "The content cannot be refreshed because no remote server is specified in the configuration file. To change this, run the program in config mode.");

    				arg0.sendResponseHeaders(204, 0); // 204 = no content
   					arg0.close();

    				return;
    			}
    			
    			int choice = JOptionPane.showConfirmDialog(null, "You have chosen to refresh the local cache using content on another server. " +
    					"This should only be done with a reliable and fast Internet connection. Are you sure you want to do this?",
    					"Please confirm", JOptionPane.YES_NO_OPTION);
    			
    			if(choice == JOptionPane.NO_OPTION)
    			{
   					arg0.sendResponseHeaders(204, 0); // 204 = no content
   					arg0.close();

    				return;
    			}
    			
    			// Send HTTP response
	    		responseBody = "<html>Currently refreshing the cache. This may take a few minutes. " +
	    		"You will be notified when the refresh process is complete.<br/>" +
	    		"<a href=\"/admin.html\">Return to administrator page</a></html>";
	    		
    			sendHTMLResponse(arg0, responseBody, 200);  // 200 => OK
		    	
		    	// Do the actual refreshing
    			boolean success;
    			/* This line refreshes all and only the files already in the cache:
		    	success = cache.refreshCache(CTATLink.remoteHost);
		    	*/
    			
    			// This code assumes the inventory of cacheable filenames is in a local file named filesToDownload.txt
    			/*java.util.Scanner input;
    			try {
    				input = new java.util.Scanner(new File(CTATLink.htdocs, "filesToDownload.txt"));
    				ArrayList<String> URIs = new ArrayList<String>();
    				while(input.hasNextLine()) {
    					URIs.add(input.nextLine());
    				}
    				success = cache.refreshCertainFiles(CTATLink.remoteHost, URIs.toArray(new String[0]), true);
    			} catch (java.io.FileNotFoundException e) {
    				debug("could not find filesToDownload.txt");
    				success = false;
    			}*/
    			
    			// Ask the server what to cache. A list of cacheable filenames should be returned by filesToDownload.php
    			try {
    				URL url = new URL("http", CTATLink.remoteHost, "/filesToDownload.php");
    				java.net.HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    				if(conn.getResponseCode() >= 400)
    				{
    					success = cache.refreshCache(CTATLink.remoteHost);
    				}
    				else
    				{
    					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    					ArrayList<String> lines = new ArrayList<String>();
    					String thisLine;
    					while((thisLine = reader.readLine()) != null)
    					{
    						if(thisLine.length() > 0)
    							lines.add("/" + thisLine);
    					}
    					success = cache.refreshCertainFiles(CTATLink.remoteHost, lines.toArray(new String[0]), true, true);
    				}
    			}
    			catch (MalformedURLException e) { debug(e.toString()); success = false; }
    			catch (IOException e) { debug(e.toString()); success = false; }
    			
		    	if(success)
		    	{
		    		JOptionPane.showMessageDialog(null, "The refreshing is complete.");
		    	}
		    	else
		    	{
		    		JOptionPane.showMessageDialog(null, "An exception occurred which prevented the data from being transferred. You may want to try refreshing the cache at a later date.");
		    	}
	    	}
	    	else
	    	{
	    		/* Admin login is required */
	    		arg0.sendResponseHeaders(403, 0); // 403 = forbidden
	    	}
		    	
	    	arg0.close();
    		
    	}
    		
	    //>------------------------------------------------------------------
	    
	    if(fileURI.equalsIgnoreCase("/changeadminpassword.cgi"))
    	{
    		debug("Processing change admin password request ...");
    		
    		found = true;
    		
    		if(CTATLink.adminLogin)
    		{		
				String newPassword = JOptionPane.showInputDialog(null, "Please enter a new administrator password.");
				
				int statusCode;
				if(newPassword != null)
				{
					(CTATLink.fManager).setContentsEncrypted(CTATLink.adminPasswordFilename, newPassword);
					responseBody = "<html>Your password has been changed successfully.<br/>" +
	    			"<a href=\"/admin.html\">Return to administrator page</a><html>";
					statusCode = 200; // OK
				}
				else // cancel was pressed
				{
					/*responseBody = "Your password will remain the same.<br/>" +
					"<a href=\"/admin.html\">Return to administrator page</a>";*/
					responseBody = "";
					statusCode = 204; // "no content" because cancel was pressed
				}
				sendHTMLResponse(arg0, responseBody, statusCode);
    		}
    		else 
    		{
    			/* Admin login is required */
   				arg0.sendResponseHeaders(403, 0); // 403 = forbidden
    		}
    		
    		arg0.close();
    	}
		
	    //>------------------------------------------------------------------
	    
	    if(fileURI.equalsIgnoreCase("/exittutorshop.cgi"))
    	{
    		debug("Processing exit TutorShop request ...");
    		
    		found = true;

    		responseBody = SO_LONG;
			sendHTMLResponse(arg0, responseBody, 200);  // 200 => OK
			arg0.close();
    		
    		System.exit(0);
    	}
	    
	    //>------------------------------------------------------------------
    	
		if (found==false)
		{
			/* If the POST request is not understood, forward it to the remote server */
			
			boolean success = handleViaRemoteServer(arg0, fileURI, false);
			if(success)
			{
				debug("POST request has been satisfied via the remote server");
			}
		}    	
		
		//>---------------------------------------------------------------------		
	}

	/**
	 * Set a new {@link CTATHTTPHandler.AlarmState} state on {@link #pushAlarm},
	 * taking any necessary associated action: <ul>
	 *   <li>INACTIVE: initial state</li>
	 *   <li>SNOOZE: set timeout and wait</li>
	 *   <li>POLL: reset to INACTIVE</li>
	 *   <li>TIMEOUT: call {@link System#exit(int)} </li>
	 *   <li>INTERRUPT: reset to INACTIVE</li>
	 *   <li>CANCEL: reset to INACTIVE</li>
	 * </ul>
	 * @param newState
	 */
	public void notifyPush(AlarmState newState) {
		synchronized(pushAlarm) {
			switch(newState) {
			case POLL:
				pushAlarm.state = AlarmState.POLL;
				pushAlarm.notifyAll();
				break;
			case SNOOZE:
				pushAlarm.state = AlarmState.SNOOZE;
				pushAlarm.alarm = new Thread() {
					public void run() {
						synchronized(pushAlarm) {
							long ms = CTATLink.pushPollingInterval+600000;  // 10 min
							long then = System.currentTimeMillis()+ms;
							while(pushAlarm.state == AlarmState.SNOOZE
									&& 0 < (ms = then-System.currentTimeMillis())) {
								try {
									pushAlarm.wait(ms);
								} catch (InterruptedException ie) {
									;
								}
							}
							if (pushAlarm.state == AlarmState.SNOOZE)
								notifyPush(AlarmState.TIMEOUT);
						}
					}
				};
				pushAlarm.alarm.start();
				break;
			case TIMEOUT:
				if (trace.getDebugCode("kill"))
					trace.printStack("kill", "notifyPush("+newState+") to exit");
				System.exit(0);
				break;              // not reached
			case INACTIVE:
			case INTERRUPT:
			case CANCEL:
			default:
				pushAlarm.state = AlarmState.INACTIVE;
				pushAlarm.notifyAll();
			}
		}
	}
	
	/**
	 * Queue an operation to be pushed to the client.
	 * @param pr enum value representing the operation
	 */
	public void enqueuePushResponse(PushResponse pr) 
	{
		synchronized(pushQueue) 
		{
			pushQueue.add(pr);
		}
	}

	/**
	 *
	 */	
	public synchronized boolean handle (CTATHTTPExchange arg0)
	{
		debug ("handle ()");
		
		String path = arg0.getRequestURI().getPath();
				
		if(path.startsWith("/run_assignment") && !path.endsWith("getpush.cgi"))
		{
			currentAssignment = path.substring(path.lastIndexOf('/')+1);
		}
		
		if(path.startsWith("/run_student_assignment") && !path.endsWith("getpush.cgi"))
		{
			String details = path.substring("run_student_assignment".length());
			String[] split = details.split("/");
			if(split.length == 3)  // "/studentassignment/problem" splits to { "", "studentassignment", "problem" }
			{
				String studentAssignment = split[1]; // student assignment includes info on user, assignment, and problem set
				String problem = split[2];
				
				// Assume that studentAssignment is also the problem set name. This is true for offline mode assignments, but not for assignments that originate from the remote server.
				// A change has to be made either here or on the server to allow for more seamless integration of online and offline mode.
				if(CTATLink.userID!= null)
				{
					if(currentAssignment == null)
					{
						debug("received run_student_assignment request when currentAssignment was null; cannot update userProgressDatabase");
					}
					
					try {
						userProgressDatabase.setCurrentProblem(CTATLink.userID, currentAssignment, studentAssignment, Integer.valueOf(problem), true);
					}
					catch(NumberFormatException e) { e.printStackTrace(); }
					catch(IOException e) { e.printStackTrace(); }
				}
			}
		}
		
		debug ("handle () cont ...");
		
		String requestMethod = arg0.getRequestMethod();
		
		debug("Request method: " + requestMethod + ", Request URI: " + arg0.getRequestURI());
		
		if (requestMethod.equalsIgnoreCase ("post"))
		{
	    	debug ("Processing POST ...");
	    	
			if (this.getLogging()  || !CTATLink.allowWriting) //if writing to disk is allowed, logging must be done
				doPost (arg0);
			else
			{
				debug ("Requested post, but HTTPServer wasn't called with logfile");
				
				arg0.sendResponseHeaders (501, 0);

				arg0.close();
			}
			
			return (true);
		}
		
		String fileURI=arg0.getRequestURI().toString();

		debug ("File uri = " + fileURI + ", with request method: " + requestMethod);
		
		boolean found=false;

		if (requestMethod.equalsIgnoreCase ("get"))
		{
	    	debug ("Processing GET request: "+arg0);
	    	
	    	/*
			if (processCustomGet (arg0)==true)
			{
				debug ("Custom GET handler has processed the message, no need to further process");
				return (true);
			}
			*/
	    	
	    	//>------------------------------------------------------------------
		    
		    if(fileURI.equalsIgnoreCase("/exittutorshop.cgi") || fileURI.equalsIgnoreCase("/exitclient.cgi"))
	    	{
	    		debug("Processing exit TutorShop request ...");
	    		
	    		found = true;
				if (trace.getDebugCode("kill"))
					trace.printStack("kill", "handle("+arg0+") for /exittutorshop.cgi");

				sendHTMLResponse(arg0, SO_LONG, 200);  // 200 => OK
				arg0.close();
	    		
				if (fileURI.contains("exittutorshop"))
					System.exit(0);
				else
					return (true);
	    	}
	    	
	    	//>------------------------------------------------------------------
	    	
	    	if (CTATLink.appState.compareTo("updating")==0)
	    	{	    		
	    		try
	    		{		    	
	    			arg0.addResponseHeader("Content-Type", "text/html");
	    			arg0.sendResponseHeaders(200, updateProgressPage.getBytes().length);
	    			//arg0.getResponseBody().write(updateProgressPage.getBytes());
	    			arg0.getOutputStream().write(updateProgressPage.getBytes());
	    			debug ("Wrote back update in progress message to brwoser");
	    			arg0.close();
	    			
	    			writeToAccessLog ("<update in progress>");
	    		}
	    		catch(Exception e)
	    		{
	    			debug ("Exception in trying to write back root index file");			    
	    		}
	    		
	    		return (true);
	    	}
	    	
	    	//>------------------------------------------------------------------	    	
	    	
		    /*Crossdomain.xml Requested.
		     *For successful exchange:
		     *->Crossdomain.xml might need to be up-to-date with flash policies
		     *->Valid content-type must be specified in the responseheader:
		     */
				    		    	
		    if (fileURI.equalsIgnoreCase ("/crossdomain.xml"))
		    {
		    	debug ("Processing crossdomain request ...");
		    	
		    	found=true;
		    	
		    	debug ("Writing back the crossdomain policy...");
		    	try
		    	{
		    		arg0.addResponseHeader("Content-Type", "application/xml");
		    		arg0.sendResponseHeaders(200,CTATLink.crossDomainPolicy.getBytes().length);
		    		//arg0.getResponseBody().write(CTATLink.crossDomainPolicy.getBytes());
		    		arg0.getOutputStream().write(CTATLink.crossDomainPolicy.getBytes());
		    		debug ("Wrote back Crossdomain.xml..");
		    		arg0.close();
		    	}
		    	catch(Exception e)
		    	{
		    		debug ("Exception in trying to write back crossdomain.xml");		    	
		    	}
		    }
   			    
	    	//>------------------------------------------------------------------
		    
		    if ((fileURI.equalsIgnoreCase ("/index.html")) || (fileURI.equalsIgnoreCase ("/index.htm")) || (fileURI.equalsIgnoreCase ("/")))
		    {
		    	debug ("Processing root request ...");
		    	
	    		boolean success = handleViaRemoteServer(arg0, "/", false);
	    		
	    		if(success)
		    	{
		    		debug("GET request was satisfied via the remote server.");
		    	}
		    	else
		    	{
		    		debug("Error when attempting to satisfy GET request via remote server.");
		    		arg0.sendResponseHeaders(500, 0); // 500 = Internal server error
		    		arg0.close();
		    	}
		    }
		    		    	    	
		    //>------------------------------------------------------------------
		    
		    /**
		     * None of the previous compares detected a well known request, that
		     * means we should assume the browser wants to retrieve a file directly
		     * as specified by the url.
		     */
		    
		    if (found==false)
		    {
		    	sendLocalFile (fileURI,pathToRoot,arg0);
		    }	
		    
	    	//>------------------------------------------------------------------		    
		}
		else
		{
			// if it's not a GET or a POST request, try forwarding it to the remote server
			boolean success = handleViaRemoteServer(arg0, fileURI, false);
			if(success)
			{
				debug(requestMethod + " request was satisfied via the remote server.");
			}
			else
			{
				debug("Error when attempting to satisfy " + requestMethod + " request via remote server.");

				arg0.sendResponseHeaders(500, 0); // 500 = Internal server error

				arg0.close();
			}
		}
		
		return (true);
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
		
		String requestMethod;
		long contentLength = 0;
		int bytesTransferred = 0;
		
		if(exchange.getRequestMethod().equalsIgnoreCase("GET"))
		{
			requestMethod = "GET";
		}
		else if(exchange.getRequestMethod().equalsIgnoreCase("HEAD"))
		{
			requestMethod = "HEAD";
			addToCache = false;
		}
		else if(exchange.getRequestMethod().equalsIgnoreCase("POST"))
		{
			requestMethod = "POST";
			addToCache = false; // don't cache POST requests; that would block communication from client to server
		}
		else
		{
			return false; // only handle GET, HEAD, and POST requests
		}
		
		// get user id for FIRE
		String cookie = exchange.getRequestCookie("_cs2n_uid");
		
		if(cookie != null) 
			CTATLink.userID=cookie;
		
		try
		{
			HttpURLConnection.setFollowRedirects(false); // this is necessary to handle some POST requests such as logins
			
			URL url = determineRemoteURL(exchange, fileURI);

			HttpURLConnection remoteConn = (HttpURLConnection) url.openConnection(); // establish a connection with the remote server
			
			// set timeouts -- don't wait forever for a connection that isn't going to happen
			//remoteConn.setConnectTimeout(5000);
			//remoteConn.setReadTimeout(5000);
			
			remoteConn.setRequestMethod(requestMethod);
			
			// Forward the request headers from the client to the remote server
			Map<String, List<String>> requestHeaders = exchange.getRequestHeaders();
			Set<String> requestHeaderKeys = requestHeaders.keySet();
			for(String key : requestHeaderKeys)
			{
				if(key.equalsIgnoreCase("Accept-Encoding"))
				{
					continue; // do not accept compressed or otherwise encoded responses, because the proxy cannot understand them
				}
				
				List<String> values = requestHeaders.get(key);
				for(String value : values)
				{
					value = translateProxyRequest(value);
					//debug("Request header: " + key + " = " + value);
					remoteConn.addRequestProperty(Utils.upperCaseInitials(key), value);
				}
			}
			
			addViaHeading(remoteConn);  // Identify ourself to the remote server
			
			// Forward the body of the POST request
			if(requestMethod.equals("POST"))
			{
				remoteConn.setDoOutput(true);
				BufferedInputStream requestIn = new BufferedInputStream(exchange.getRequestBody());
				BufferedOutputStream requestOut = new BufferedOutputStream(remoteConn.getOutputStream());
				
				int b; // holds one byte at a time
				while((b = requestIn.read()) != -1)
				{
					requestOut.write(b);
				}
				
				requestIn.close();
				requestOut.close();
			}
			else
			{
				// Read in the request body (if any) just to clear the stream
				BufferedInputStream requestIn = new BufferedInputStream(exchange.getRequestBody());
				while(requestIn.read() != -1)
					;
				
				requestIn.close();
			}
			
			// Read the response and forward the headers back to the client
			String responseHeaderKey, responseHeaderValue;
			for(int i = 1; (responseHeaderKey = remoteConn.getHeaderFieldKey(i)) != null; i++)
			{
				responseHeaderValue = remoteConn.getHeaderField(i);
				responseHeaderValue = translateProxyResponse(responseHeaderKey, responseHeaderValue);
				//debug("Response header: " + responseHeaderKey + " = " + responseHeaderValue);
				
				// Add the translated response header. Do not do this if this header value indicates chunked transfer encoding,
				// because that transfer coding is no longer in place, having been removed by the HttpURLConnection. 
				if(!(responseHeaderKey.equalsIgnoreCase("Transfer-Encoding") && responseHeaderValue.equalsIgnoreCase("chunked")))
				{
					exchange.addResponseHeader(responseHeaderKey, responseHeaderValue);
				}
				
				// remember the content length
				if(responseHeaderKey.equalsIgnoreCase("Content-Length"))
				{
					contentLength = Long.valueOf(responseHeaderValue);
				}
				
				if(responseHeaderKey.equalsIgnoreCase("Set-Cookie"))
					debug("via remote server: Set-Cookie: "+responseHeaderValue);
				
				// determine if the content is HTML; if it is, all absolute links that contain the remote host's name must be translated
//				if(responseHeaderKey.equalsIgnoreCase("Content-Type"))
//				{
//					if(responseHeaderValue.toLowerCase().contains("text/html"))
//					{
//						contentIsHtml = true;
//					}
//				}
			}
			
			int responseCode = remoteConn.getResponseCode();
			if (300 <= responseCode && responseCode < 400)
				checkRedirectTo3rdParty(remoteConn, exchange);
			String lastModified = remoteConn.getHeaderField("Last-Modified");
			if(lastModified == null) {
				lastModified = remoteConn.getHeaderField("Date"); // if the server did not specify a last-modified time, use the current time
				if(lastModified == null) {
					lastModified = CTATWebTools.headerDateFmt.format(new Date()); // if all else fails, use current time on local system
				}
			}
			
			if(requestMethod.equals("HEAD"))
			{
				debug ("Sending response headers ...");
				
				// There is no message body in response to a HEAD request. Just send the headers.
				exchange.sendResponseHeaders(responseCode, -1);
			}
			else
			{
				// Send the headers and forward the message body from the remote server to the client, caching as necessary
				exchange.sendResponseHeaders(responseCode, contentLength);
				InputStream responseIn = new BufferedInputStream(remoteConn.getInputStream());
				//BufferedOutputStream responseOut = new BufferedOutputStream(exchange.getResponseBody());
				BufferedOutputStream responseOut =exchange.getOutputStream();
				
//				if(contentIsHtml)
				
				InputStream responseInResult = translateProxyHtml(responseIn); // HTML hyperlinks that contain absolute URLs need to be translated to replace the remote host's name with "localhost"

				debug ("Sending data to browser ...");
					
				if(addToCache == true && responseCode == 200)
				{
					ByteArrayOutputStream cacheOut = new ByteArrayOutputStream(); // will hold the content to be cached
					
					int b;
										
					while((b = responseInResult.read()) != -1)
					{
						responseOut.write(b);
						cacheOut.write(b);
						bytesTransferred++;
					}
					
					cache.addToCache(fileURI, cacheOut.toByteArray(), lastModified, true);
				}
				else // no caching
				{
					int b;
					while((b = responseInResult.read()) != -1)
					{
						responseOut.write(b);
						bytesTransferred++;
					}
				}
				
				responseIn.close();
				//responseOut.close();
			}
			
			exchange.close();
		}
		catch(Exception e)
		{
			debug("Exception in proxy server: " + e);
			e.printStackTrace();
			debug("bytes transferred: " + bytesTransferred + "; contentLength: " + contentLength);
			totalBytesReceivedRemotely += bytesTransferred;
			return false;
		}
		
		totalBytesReceivedRemotely += bytesTransferred;
		
		debug("totalBytesReceivedRemotely: " + totalBytesReceivedRemotely);

		return true;
	}

	/**
	 * Create the URL for the remote request from the given request. If the
	 * client request has a {@value #TUTORSHOP_FORWARD_TO} cookie, returns its
	 * value. Else uses {@link CTATLink#remoteHost} + the given fileURI path.
	 * @param exchange holds client request, with cookies
	 * @param fileURI path portion of the original URL
	 * @return cookie value or default {@link CTATLink#remoteHost}+fileURI
	 */
	private URL determineRemoteURL(CTATHTTPExchange exchange, String fileURI)
			throws MalformedURLException {
		String result = exchange.getRequestCookie(TUTORSHOP_FORWARD_TO);
		if (result == null)
			result = "http://" + CTATLink.remoteHost + fileURI;
		try {
			debug("determineRemoteURL() to return "+result);
			return new URL(result);
		} catch (MalformedURLException mue) {
			trace.err("Error creating remoteURL with address \""+
					result+"\": "+mue+
					(mue.getCause() == null ? "" : "\ncause: "+mue.getCause()));
			throw mue;
		}
	}

	/**
	 * If this response from the remote server is a redirect (HTTP code 3xx) to
	 * a host outside the domain of {@link CTATLink#remoteHost}, then substitute
	 * {@link CTATLink#hostName} and {@link CTATLink#wwwPort} and record the 
	 * redirect address in a cookie.
	 * @param remoteConn has the response from the remote server
	 * @param exchange where to build the response to the local browser
	 */
	private void checkRedirectTo3rdParty(HttpURLConnection remoteConn,
										 CTATHTTPExchange exchange) 
	{
		debug ("checkRedirectTo3rdParty ()");
		
		String location = remoteConn.getHeaderField("Location");
		URI uri = null, newURI = null;
		
		try 
		{
			uri = new URI(location);
		} 
		catch (Exception e) 
		{
			trace.err("checkRedirectTo3rdParty: Location header \""+location+"\" not a URI: "+e);
			return;
		}
		
		// Upon any redirect back to our domain, remove the forward-to cookie address.
		if (same2ndLevelDomain(uri.getHost(), CTATLink.remoteHost)) 
		{
			exchange.setResponseCookie(TUTORSHOP_FORWARD_TO, null);
			return;
		}
		
		debug("checkRedirectTo3rdParty() setting cookie "+TUTORSHOP_FORWARD_TO+'='+location);
		
		exchange.setResponseCookie(TUTORSHOP_FORWARD_TO, location); 
		
		try 
		{
			newURI = new URI(uri.getScheme(), 
							 uri.getUserInfo(),
							 CTATLink.hostName, 
							 CTATLink.wwwPort,
							 uri.getPath(), 
							 uri.getQuery(), 
							 uri.getFragment());
			
			exchange.setResponseHeader("Location", newURI.toString());			
			exchange.setResponseHeader("Pragma", "no-cache");
			exchange.setResponseHeader("Cache-Control", "no-cache");
			
		} catch (Exception e) {
			trace.err("checkRedirectTo3rdParty: error setting local Location to replace \""+location+"\": "+e);
		}
	}
	
	/**
	 * Identify ourself to the remote server using the "Via" header, with the values<ul>
	 * <li>received-protocol = 1.1</li>
	 * <li>host localhost:8080</li>
	 * <li>comment (ClientSideTutorShop)</li>
	 * </ul>The remote host may include special content useful to this client.
	 * 
	 * Note: HttpURLConnection may refuse to accept a "Via" field unless
	 * system propery "sun.net.http.allowRestrictedHeaders" is set to "true"
	 * 
	 * @param remoteConn
	 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
	 */
	private void addViaHeading(HttpURLConnection remoteConn) 
	{
		remoteConn.addRequestProperty ("Via", "1.1 localhost:8080 (ClientSideTutorShop)");
	}
	
	/**
	 * Translate the HTTP headers of a request made by the proxy
	 */
	private String translateProxyRequest(String original)
	{
		debug ("translateProxyRequest ("+original+")");
		
		// Replace all occurences of the local host's name (possibly followed by a
		// colon and port number) with the remote host's name (and port number).
		String result = original.replace(CTATLink.hostName + ":" + CTATLink.wwwPort, CTATLink.remoteHost + ":80"); // 80 is default port for HTTP
		result = result.replace(CTATLink.hostName, CTATLink.remoteHost);
		return result;
	}
	/**
	 * Translate the HTTP headers of a response received by the proxy.  Handle Location
	 * headers specially: if the original location's domain doesn't match
	 * that of {@link CTATLink#remoteHost}, then save the URI in {@link #authURI}.
	 * This is meant to accommodate the observed 
	 * behavior of the Sophos content filter at Propel Montour, whose HTTP 307
	 * Temporary Redirects would defeat our host replacement scheme. 
	 * @param key header name
	 * @param original header value
	 * @return revised header value
	 */
	private String translateProxyResponse(String key, String original)
	{
		debug ("translateProxyRequest ("+key+","+original+")");
		
		// Replace all occurences of the remote host's name (possibly followed by a
		// colon and port numer) with the local host's name and port number.
		String result = original.replaceAll("\\Q" + CTATLink.remoteHost + "\\E(:\\d*)?", CTATLink.hostName + ":" + CTATLink.wwwPort);
		return result;
	}

	/**
	 * Tell if 2 hostnames share top- and 2nd-level domain names.
	 * @param h1 first host name
	 * @param h2 second host name
	 * @return true if last 2 elements of "."-delimited domain names match
	 */
	private boolean same2ndLevelDomain(String h1, String h2) 
	{
		if (h1 == null || h2 == null)
			return false;
		
		String[] a1 = h1.split("\\."), a2 = h2.split("\\.");
		
		int i = a1.length-1, j = a2.length-1;
		
		if (i < 1 || j < 1)
			return false;
		
		for (; i > a1.length-3 && j > a2.length-3; --i, --j) 
		{
			if (!a1[i].equalsIgnoreCase(a2[j]))
				return false;
		}
		return true;
	}
	
	/**
	 * Translate HTML content received by the proxy
	 */
	private InputStream translateProxyHtml(InputStream original)
	{
		debug("translateProxyHtml");
		
		// All HTML links that include absolute addresses must be translated so that the remote host's name is not used
		
		// Read the InputStream into a String using ISO-8859-1, a 1-byte encoding
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedInputStream bis = new BufferedInputStream(original);
		int b;
		String str;
		try
		{
			while((b = bis.read()) != -1)
			{
				baos.write(b);
			}
			str = new String(baos.toByteArray(), "ISO-8859-1"); // str now holds the contents of the original InputStream; ISO-8859-1 maps one byte to one character
		} catch(Exception e) {
			debug(e.toString());
			e.printStackTrace();
			return null;
		}
/*		
		// Replace all occurences within a hyperlink of the remote host's name (possibly followed by a
		// colon and port numer) with the local host's name and port number.
		// Only hyperlinks (beginning with "href") undergo this translation because otherwise flashvars would get messed up.
		str = str.replaceAll("href\\s*=\\s*\"http://\\Q" + CTATLink.remoteHost + "\\E(:\\d*)?", "href=\"http://" + CTATLink.hostName + ":" + CTATLink.wwwPort);
		
		// Translate the curriculum_service_url flashvar that seems to have an impact on problem-to-problem sequencing.
		// If this is not done, when the Done button is pressed, the next tutor will come directly
		// from the remote server, bypassing the proxy and cache.
		str = str.replaceAll("curriculum_service_url\\s*=\\s*http://\\Q" + CTATLink.remoteHost + "\\E(:\\d*)?", "curriculum_service_url=http://" + CTATLink.hostName + ":" + CTATLink.wwwPort);

		if(CTATLink.useLocalTutoringService)
		{
			//specify the URL of the local tutoring service
			str = str.replaceAll("remoteSocketURL\\s*=\\s*[^&]*", "remoteSocketURL=" + CTATLink.hostName);
			//str = str.replaceAll("remoteSocketURL\\s*=\\s*\\Q" + CTATLink.remoteHost + "\\E", "remoteSocketURL=" + CTATLink.hostName);
			str = str.replaceAll("remoteSocketPort\\s*=\\s*\\d*", "remoteSocketPort=" + CTATLink.tsPort); // specify the port of the local tutoring service
			// Make sure the BRD is stored locally; this is necessary if the tutoring service is to be run locally
			str = translateQuestionFile(str);
		}
		//str = str.replaceAll("\\Q" + CTATLink.remoteHost + "\\E(:80)?", CTATLink.hostName + ":" + CTATLink.wwwPort);
		//str = str.replaceAll("remoteSocketPort=\\d*", "remoteSocketPort=" + CTATLink.tsPort);
*/
		
		str = str.replaceAll("http://\\Q" + CTATLink.remoteHost + "\\E(:80)?/", "http://" + CTATLink.hostName + ":" + CTATLink.wwwPort + "/");
		if(CTATLink.useLocalTutoringService)
		{
			str = remoteSocketURL.matcher(str).replaceAll("remoteSocketURL=" + CTATLink.hostName);
			str = remoteSocketPort.matcher(str).replaceAll("remoteSocketPort=" + CTATLink.tsPort); // specify the port of the local tutoring service
			str = translateQuestionFile(str);
		}
		
		// Create an InputStream out of the translated String, and return that InputStream
		byte[] bytes;
		try {
			bytes = str.getBytes("ISO-8859-1");
		} catch(UnsupportedEncodingException e) {
			return null; // this should never happen; ISO-8859-1 should always be supported
		}
		return new ByteArrayInputStream(bytes);
	}	
	/**
	 * Ensure that the remote question_file BRD is stored locally and rewrite its path so the tutoring service can find the local BRD
	 */
	private synchronized String translateQuestionFile(String originalHtml) 
	{
		debug("translateQuestionFile");
		
		String str = originalHtml;
		
		// extract the question_file flashvar from the HTML
		int questionFileIndex = originalHtml.indexOf("question_file");
		if(questionFileIndex < 0) {
			return str; // there is no question_file flashvar
		}
		int st = str.indexOf("=", questionFileIndex)+1;
		int end = str.indexOf("&", questionFileIndex);
		if (st < questionFileIndex || st > questionFileIndex+"question_file".length()+2)
		{
			st = str.indexOf(">", questionFileIndex)+1;
			end = str.indexOf("<", questionFileIndex);
		}
		debug("questionFileIndex, st, end "+questionFileIndex+","+st+","+end+"; originalHtml\n  "+originalHtml);
		String question_file = originalHtml.substring(st, end);
		question_file = question_file.trim();
		
		File localQuestionFile = null;
		// if this is a restore, remove the initial path elements added earlier
		debug("***question_file "+question_file+", pattern \""+htdocs_remoteBRDs.pattern()+"\"");
		Matcher m = htdocs_remoteBRDs.matcher(question_file);
		if (m.find()) {
			localQuestionFile = new File(question_file);
		} else {
			// ensure that the indicated question_file is stored locally
			localQuestionFile = new File(new File(CTATLink.htdocs, "remoteBRDs"), question_file);
		}
		debug("localQuestionFile "+localQuestionFile);
		if(!localQuestionFile.exists()) {
			// if the BRD does not exist locally, it must be created
			debug("Making new local BRD file: " + question_file);
			if(!CTATLink.allowWriting)
			{
				JOptionPane.showMessageDialog(null, "The local tutoring service requires a local BRD that does not exist and cannot be written because writing to disk is disabled.\n" +
						"This problem may be solved by configuring the program to use a remote tutoring service or to allow writing to disk.");
				return str;
			}
			localQuestionFile.getParentFile().mkdirs();
			try {
				localQuestionFile.createNewFile();
			} catch (IOException e) {
				debug(e.toString());
				return str;
			}
			
			// read the BRD from the remote server
			URL brdURL;
			try {
				brdURL = new URL("http://" + CTATLink.remoteHost + "/" + question_file);
			} catch (MalformedURLException e) {
				debug(e.toString());
				return str;
			}
			try {
				// store the BRD locally
				InputStream brdIn = new BufferedInputStream(brdURL.openStream());
				OutputStream brdOut = new BufferedOutputStream(new FileOutputStream(localQuestionFile));
				int b;
				while((b = brdIn.read()) != -1) {
					brdOut.write(b);
				}
				brdIn.close();
				brdOut.close();
				// encrypt if necessary
				/*
				if(CTATLink.BRDsAreEncrypted)
				{
					CTATLink.fManager.setContentsEncrypted(localQuestionFile.toString(), CTATLink.fManager.getContents(localQuestionFile.toString()));
				}
				*/
			} catch (IOException e) {
				debug(e.toString());
				return str;
			}
			
			debug("Local BRD " + localQuestionFile + " successfully created.");
		}
		
		str = str.replace(question_file, localQuestionFile.getPath().replace('\\', '/')); // convert backslashes to forward slashes because otherwise they will get lost on the way to the tutoring service
		
		return str;
	}
}
