/**
 
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPLocalHandler.java,v 1.12 2012/10/16 20:01:34 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATHTTPLocalHandler.java,v $
 Revision 1.12  2012/10/16 20:01:34  vvelsen
 Lots of changes to align the various http handlers. Most of the work has been done in the DVD handler for FIRE which now uses the progress database to also store user information

 Revision 1.11  2012/10/11 19:13:48  vvelsen
 Started reworking the local http handler to work with the CS2N specific DVD handler

 Revision 1.10  2012/10/03 17:50:26  sewall
 First working version with CTATAppletFileManager.

 Revision 1.9  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.8  2012/09/14 22:20:46  sewall
 Now display htdocs/endofproblemset.html after last problem in problem set.

 Revision 1.7  2012/09/14 13:42:23  sewall
 In doPost() for path /problemselect.cgi, avoid NPEs when cmd parameter is missing.

 Revision 1.6  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.5  2012/09/11 13:16:18  vvelsen
 Made various changes to the local http handler. It can now use both a directory.txt file as well as a curriculum.xml file. Added a way to indicate if the server should generate flashvars with an info field. When the info field is generated it creates the horizontal menu bar at the top of the screen that shows all available problems

 Revision 1.4  2012/09/08 15:35:02  sewall
 Interim work: end-to-end launch, run CTAT problem, enter CL session. Lacks J. Booth curriculum selection algo, problem set end notification.

 Revision 1.3  2012/09/07 19:16:59  vvelsen
 Temporarily removed references to the curriculum class

 Revision 1.2  2012/09/07 18:15:05  vvelsen
 Quick checkin so that Jonathan can test his CL bridge code

 Revision 1.1  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 $RCSfile: CTATHTTPLocalHandler.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPLocalHandler.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
   
*/

package edu.cmu.hcii.ctat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
//import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class CTATHTTPLocalHandler extends CTATHTTPHandlerBase implements CTATHTTPHandlerInterface
{		
	private List<String> problemSummaries = new ArrayList<String>();	
	public String currentAssignment;
				
	private static String updateProgressPage = "<html><header></header><body><center><table width=\"100%\" height=\"100%\"><tr><td valign=\"middle\" align=\"center\"><h1>System update in progress, can't run student assignments at this point. Please try again later</h1></td></tr></table></center></body></html>";	
	private static String templateFileNotFound = "<html><body><center><table width=100% height=100%><tr><td align=center valign=middle></td></tr></table></center></body></html>";
			
	protected CTATObjectTagDriver fTagGenerator=null;
	
	private CTATCurriculum curriculum=null;
	private CTATProblemSet fProblemSet=null;
	private CTATWebTools fWebTools=null;	
					
	Pattern remoteSocketURL = Pattern.compile("remoteSocketURL\\s*=\\s*[^&]*");
	Pattern remoteSocketPort = Pattern.compile("remoteSocketPort\\s*=\\s*\\d*");
	Pattern htdocs_remoteBRDs = Pattern.compile(CTATLink.htdocs+"remoteBRDs.");
	
	/** If finished, call server.{@link ExitableServer#startExiting() startExiting()} so caller will quit. */
	
	protected UserProgressDatabase localUserDB=null;
	
	/**
	 *
	 */	
	public CTATHTTPLocalHandler (String logFileName,
								 UserProgressDatabase aDB) //throws IOException 
	{		
		super (logFileName);
		
    	setClassName ("CTATHTTPLocalHandler");
    	debug ("CTATHTTPLocalHandler ("+logFileName+","+CTATLink.allowWriting+")");	
    
    	localUserDB=aDB;
    	
    	// forces java.net.HttpURLConnection to accept "Via" header
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); 
		    	    	    	    			
		if (CTATLink.deployType==CTATLink.DEPLOYFLASH)		
			fTagGenerator=new CTATFlashDriver ();
		
		if (CTATLink.deployType==CTATLink.DEPLOYHTML5)		
			fTagGenerator=new CTATHTML5Driver ();
				
		fProblemSet=new CTATProblemSet ();
		fWebTools=new CTATWebTools ();
				
		debug("CTATHTTPLocalHandler() Done");					
	}
	/**
	 *
	 */
	public CTATCurriculum getCurriculum() 
	{
		return curriculum;
	}
	/**
	 *
	 */
	public void setCurriculum(CTATCurriculum aCurriculum) 
	{
		this.curriculum = aCurriculum;
	}	
	/**
	 *
	 */
	protected String directoryToMenu ()
	{
		debug ("directoryToMenu ()");
		
		StringBuffer composite=new StringBuffer ();
		
		composite.append("<ul class=\"collapsibleList\" id=\"curriculum\">");
		
		if (curriculum!=null)
		{
			debug ("We have a curriculum, distilling problem sets ...");
			
			debug ("Building directory listing for first problem set");
			
			ArrayList <CTATProblemSet> entries=curriculum.getProblemSets(curriculum.getFirstAssignment ());
						
			debug ("Proceeding ("+entries.size()+") ...");
									
			for (int i=0;i<entries.size();i++)
			{
				CTATProblemSet entry=entries.get(i);
				
				if (entry!=null)
				{
					StringBuffer builder=new StringBuffer ();
					
					/*
					builder.append("<li><a href=\"showproblemset.cgi?problemname=");
					builder.append(entry.getDirectory());
					builder.append("\">");
					builder.append(entry.getDescription ());
					builder.append("</a>");
					builder.append(entry.listProblemSetHTML ());
					builder.append("</li>");
					*/
					
					builder.append("<li>");
					builder.append(entry.getDirectory());
					builder.append(" : ");
					builder.append(entry.getDescription ());
					builder.append(entry.listProblemSetHTML ());
					builder.append("</li>");					
										
					debug (builder.toString());
						
					composite.append(builder.toString());					
				}
				else
					debug ("Internal error: entry object for index " + i + " is null!");
			}
		}
		else
		{
			debug ("The curriculum object is null!");
						
			ArrayList <CTATProblemSet> entries=CTATLink.fDirectoryTXT.getEntries();
				
			for (int i=0;i<entries.size();i++)
			{
				CTATProblemSet entry=entries.get(i);
				
				/*
				composite.append("<li><a href=\"showproblemset.cgi?problemname="+entry.getDirectory()+"\">"+entry.getDescription ()+"</a>");
				composite.append(entry.listProblemSetHTML ());
				composite.append("</li>");
				*/
								
				composite.append("<li>"+entry.getDirectory()+" : "+entry.getDescription ());				
				composite.append(entry.listProblemSetHTML ());
				composite.append("</li>");				
			}			
		}	
		
		composite.append("</ul>");		
		
		return (composite.toString());
	}
	/**
	 *
	 */	
	public String createRoot (String aProblemSet)
	{
		debug ("createRoot ("+aProblemSet+")");
				
		String composite="";
		String problemMenu="";		
		Boolean showInfo=false;		
		String indexFile=CTATObjectTagDriver.createIndexFile ();
				
		// First load the html template. We can also use this to display error messages to the user
		
		String indexTemplate=CTATLink.fManager.getContents (CTATLink.htdocs+indexFile);

		if (indexTemplate==null)
		{
			debug ("Error: can't process root template");
			return (null);
		}
		
		if (indexTemplate.indexOf("flashtags-full")!=-1)
			showInfo=true;
		else
			showInfo=false;

		problemMenu=directoryToMenu ();

		ArrayList<CTATProblemSet> problemSets=null;

		if (curriculum!=null)
		{
			debug ("We have a curriculum, loading first problem set in assignment ...");
			
			problemSets=curriculum.getProblemSets(curriculum.getFirstAssignment ());
		}
		else
		{
			debug ("We don't have a valid curriculum, using directory.txt ...");
			problemSets=CTATLink.fDirectoryTXT.getEntries();
		}
		
		if (problemSets.size()==0)
		{
			Utils.showExceptionOccuredDialog(null, "No problem sets could be found in the FlashTutors directory (under htdocs). " +
					"This may be because tutors were not installed locally or the required problem_set.xml files are missing.",
					"No tutors found");
			System.exit(0);
		}
		
		/*
		debug ("Trying to obtain first index from problem sets list ("+problemSets.size()+")...");
			
		for (int t=0;t<problemSets.size();t++)
		{
			CTATProblemSet aSet=problemSets.get(t);
				
			debug ("Examining problem set: " + aSet.getName());
		}
		*/
			
		fProblemSet=problemSets.get(0);
				
		if (fProblemSet!=null)
		{							
			fProblemSet.reset();
				
			//CTATProblem nextProblem=fProblemSet.getNextProblem();
			
			CTATProblem startProblem=fProblemSet.getNextProblem ();
										
			// If nextProblem is null, the user has passed the last problem of the set
			//if(nextProblem == null)
			if (startProblem==null)
			{
				String lastproblemTemplate = CTATLink.fManager.getContents(CTATLink.htdocs+"templates/endofproblemset.html");
				if(lastproblemTemplate == null)
				{
					debug("Error: can't process \"end of problem set\" template");
					lastproblemTemplate = "You have reached the end of the problem set."; // make our own end-of-problem-set message
				}
					
				processProblemSetEnd(problemSummaries);
				
				composite = lastproblemTemplate.replaceFirst("directory.txt", directoryToMenu());
				return composite;
			}
				
			fTagGenerator.setIncludeMenu(showInfo);
				
			String flashTags=fTagGenerator.generateObjectTags (startProblem,fProblemSet);
			
			if (showInfo==true)
				composite=indexTemplate.replaceFirst ("flashtags-full",flashTags);
			else
				composite=indexTemplate.replaceFirst ("flashtags",flashTags);
				
			composite=composite.replaceFirst ("ProblemSetTitle",startProblem.name +" : " + fProblemSet.getDescription() + " : " + fProblemSet.getName());		
			composite=composite.replaceFirst ("directory.txt",problemMenu);
		}
		else
		{
			if (showInfo==true)
				composite=indexTemplate.replaceFirst ("flashtags-full","Error: unable to obtain first problem set from repository");
			else
				composite=indexTemplate.replaceFirst ("flashtags","Error: unable to obtain first problem set from repository");
		}

		return (composite);
	}	
	/**
	 *
	 */	
	public String createPage (CTATProblem aProblem)
	{
		debug ("createPage ()");
		
		String composite="";
		Boolean showInfo=false;		
		
		// If the problem is null, assume the user has passed the last problem of the set
		
		//>----------------------------------------------------------------
		
		if (aProblem==null)
		{
			String lastproblemTemplate = CTATLink.fManager.getContents(CTATLink.htdocs+"templates/endofproblemset.html");
			if(lastproblemTemplate == null)
			{
				debug("Error: can't process \"end of problem set\" template");
				lastproblemTemplate = "You have reached the end of the problem set."; // make our own end-of-problem-set message
			}
			
			processProblemSetEnd(problemSummaries);
			
			composite = lastproblemTemplate.replaceFirst("directory.txt", directoryToMenu());
			return composite;
		}
		
		//>----------------------------------------------------------------
		
		String indexTemplate=CTATLink.fManager.getContents (CTATLink.htdocs+CTATObjectTagDriver.createIndexFile ());
		if (indexTemplate==null)
		{
			debug ("Error: can't process root template");
			return (null);
		}
		
		if (indexTemplate.indexOf("flashtags-full")!=-1)
			showInfo=true;
		else
			showInfo=false;
		
		fProblemSet.setCurrentIndex(aProblem.index);
		
		String problemMenu=directoryToMenu ();
		
		String flashTags=fTagGenerator.generateObjectTags (aProblem,fProblemSet);
			
		if (showInfo==true)
			composite=indexTemplate.replaceFirst ("flashtags-full",flashTags);
		else
			composite=indexTemplate.replaceFirst ("flashtags",flashTags);
			
		composite=composite.replaceFirst ("ProblemSetTitle",aProblem.name +" : " + fProblemSet.getDescription() + " : " + fProblemSet.getName());		
		composite=composite.replaceFirst ("directory.txt",problemMenu);
										
		//>----------------------------------------------------------------
		
		return (composite);
	}		

	/**
	 * Call {@link ProblemEndHandler#problemEnd(String)} if handler present.
	 * (Previously, if callback returned true, call {@link ExitableServer#startExiting()}.)
	 * @param summary problem summary text to pass to problemEnd(String) 
	 * @return result from {@link ProblemEndHandler#problemEnd(String)} 
	 */
	private boolean processProblemEnd(String summary) 
	{
		if(CTATLink.problemEndHandler == null)
			return false;
		
		return CTATLink.problemEndHandler.problemEnd(summary);
	}	

	/**
	 * Call {@link ProblemSetEndHandler#problemSetEnd(String)} if handler present.
	 * (Previously, if callback returned true, call {@link ExitableServer#startExiting()}.)
	 * @param summaries problem summary texts to pass to problemSetEnd(List<String>) 
	 */
	private boolean processProblemSetEnd(List<String> summaries) 
	{
		if(CTATLink.problemSetEndHandler == null)
			return false;
		
		return CTATLink.problemSetEndHandler.problemSetEnd(summaries);
	}	
	
	/**
	 * For all intends and purposes the handle method below is the original and
	 * complete version of the code that can process incoming url requests. When
	 * overriding this method please call it when your own code did not find a
	 * match it should respond to
	 */	
	public synchronized boolean handle (CTATHTTPExchange arg0)
	{
		debug ("handle ("+arg0.getRequestURI().getPath()+"); active threads "+Thread.activeCount());
		
		Boolean processed=false;
				
		//>--------------------------------------------------------------------------
				
		String requestMethod = arg0.getRequestMethod();
		
		debug("Request method: " + requestMethod + ", Request URI: " + arg0.getRequestURI());
		
		//>--------------------------------------------------------------------------
		
		if (requestMethod.equalsIgnoreCase ("options"))
		{
	    	debug ("Processing OPTIONS ...");
	    	
	    	arg0.sendOptions();
	    	
	    	processed=true;
		}	
		
		//>--------------------------------------------------------------------------
		
		if (requestMethod.equalsIgnoreCase ("post"))
		{
	    	debug ("Processing POST ...");
	    	
			if (getLogging ()  || !CTATLink.allowWriting) //if writing to disk is allowed, logging must be done
			{
				processed=doPOST (arg0);	
			}
			else
			{
				debug ("Requested post, but HTTPServer wasn't called with logfile");
				
				arg0.sendResponseHeaders (501, 0);
			}			
		}
		
		//>--------------------------------------------------------------------------
		
		if (requestMethod.equalsIgnoreCase ("get"))
		{
			processed=doGET (arg0);

			if (processed==false)
			{
				String fileURI=arg0.getRequestURI().toString();
				
				// None of the previous compares detected a well known request, that
				// means we should assume the browser wants to retrieve a file directly
				// as specified by the url.
    
				processed=sendLocalFile (fileURI,CTATLink.htdocs,arg0);
				
				if (processed==false)
				{
					arg0.send404 ("Error: Did not understand GET request ("+CTATLink.lastError+")");					
				}
			}
		}
		
		//>--------------------------------------------------------------------------
		
		arg0.close();
		
		return (processed);
	}
	/**
	 *
	 */	
	public Boolean doGET (CTATHTTPExchange arg0)
	{
		debug ("doGET ()");
		    	
		String fileURI=arg0.getRequestURI().toString();

		debug ("File uri = " + fileURI);
		
		boolean found=false;		
		    	
    	//>------------------------------------------------------------------
	    
	    if(fileURI.equalsIgnoreCase("/exittutorshop.cgi") || fileURI.equalsIgnoreCase("/exitclient.cgi"))
    	{
    		debug("Processing exit TutorShop request ...");
    		
    		found = true;
    		
			if (trace.getDebugCode("kill"))
				trace.printStack("kill", "handle("+arg0+") for /exittutorshop.cgi");

			sendHTMLResponse(arg0, CTATLink.SO_LONG, 200);  // 200 => OK
    		
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
    			arg0.getOutputStream().write(updateProgressPage.getBytes());
    			debug ("Wrote back update in progress message to browser");
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
    	
	    /* Crossdomain.xml Requested. For successful exchange:
	     * ->Crossdomain.xml might need to be up-to-date with flash policies
	     * ->Valid content-type must be specified in the responseheader:
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
	    		arg0.getOutputStream().write(CTATLink.crossDomainPolicy.getBytes());
	    		debug ("Wrote back Crossdomain.xml..");
	    		arg0.close();
	    	}
	    	catch(Exception e)
	    	{
	    		debug ("Exception in trying to write back crossdomain.xml");		    	
	    	}
	    }
    	
		//>---------------------------------------------------------------------	
	    
	    /**
	     * Please explain what a getpush is and what the response means/does
	     */
		
    	if (fileURI.contains("/getpush.cgi"))
    	{
    		found=true;
    		
    		debug ("Processing 'getpush' GET request ...");
    		
    		String responseBody = "<p>get<b>push</b> response</p>";
    		
    		sendHTMLResponse(arg0, responseBody, 200);
    	}
	    
    	//>------------------------------------------------------------------
	    
	    if ((fileURI.equalsIgnoreCase ("/index.html")) || (fileURI.equalsIgnoreCase ("/index.htm")) || (fileURI.equalsIgnoreCase ("/")))
	    {
	    	debug ("Processing root request ...");
	    	
	    	found=true;		    	
	    	
    		String rootResponse=createRoot ("");

    		if (rootResponse!=null)
    		{
    			try
    			{
    				arg0.addResponseHeader("Content-Type", "text/html");
    				arg0.sendResponseHeaders(200, rootResponse.getBytes().length);
    				arg0.getOutputStream().write(rootResponse.getBytes());

    				debug ("Wrote back root index file");
    				arg0.close();

    				writeToAccessLog ("/");
    			}
    			catch(Exception e)
    			{
    				debug ("Exception in trying to write back root index file");			    
    			}
    		}
    		else
    		{
    			try
    			{
    				arg0.addResponseHeader("Content-Type", "text/html");
    				arg0.sendResponseHeaders(404, templateFileNotFound.getBytes().length);
    				arg0.getOutputStream().write(templateFileNotFound.getBytes());
    				debug ("Wrote back root index file");
    				arg0.close();
    			}
    			catch(Exception e)
    			{
    				debug ("Exception in trying to write back root index file");			    
    			}
    		}
	    }
	    
    	//>------------------------------------------------------------------		    
	    
	    /**
	     * We use this method when we're running completely self containd and
	     * more or less in sales mode. You would use this to pre-configure a
	     * problem set directory with problems sets which are then read and
	     * presented here.
	     */
	    
	    if (fileURI.indexOf("showproblemset.cgi")!=-1)
	    {
	    	debug ("Processing 'show problem set' request ...");
	    	
	    	found=true;
	    	
	    	fWebTools.showURI (arg0.getRequestURI());
	    	Map<String, String> map=fWebTools.getQueryMap (arg0.getRequestURI().getRawQuery());
	    	String selectedProblemSet=map.get("problemname");
	    	
	    	String rootResponse=createRoot (selectedProblemSet);		    
	    			    	
	    	if (rootResponse!=null)
	    	{
	    		try
	    		{		    	
	    			arg0.addResponseHeader("Content-Type", "text/html");
	    			arg0.sendResponseHeaders(200, rootResponse.getBytes().length);
	    			arg0.getOutputStream().write(rootResponse.getBytes());
	    			debug ("Wrote back root index file");
	    			arg0.close();
	    			
	    			writeToAccessLog ("/");
	    		}
	    		catch(Exception e)
	    		{
	    			debug ("Exception in trying to write back root index file");			    
	    		}
	    	}
	    	else
	    	{
	    		try
	    		{
	    			arg0.addResponseHeader("Content-Type", "text/html");
	    			arg0.sendResponseHeaders(404, templateFileNotFound.getBytes().length);
	    			arg0.getOutputStream().write(templateFileNotFound.getBytes());
	    			debug ("Wrote back root index file");
	    			arg0.close();
	    		}
	    		catch(Exception e)
	    		{
	    			debug ("Exception in trying to write back root index file");			    
	    		}
	    	}
	    }
	    
    	//>------------------------------------------------------------------
	    
	    /**
	     * Jump to a particular problem in a problem set. Use the position
	     * variable to figure out to which index we should jump
	     */
	    
	    if(fileURI.contains("gotoproblem.cgi") || fileURI.contains("problemselect.cgi"))
	    {
	    	debug ("Processing "+fileURI+" request ...");
	    	found = true;
	    	
	    	// Extract the problem number from the URI
	    	CTATWebTools urltools=new CTATWebTools ();
	    	Map<String, String> map = urltools.parseQuery(arg0.getRequestURI().getRawQuery());
	    	int problemNum = Integer.valueOf(map.get("position"));
	    	
	    	fProblemSet.setCurrentIndex (problemNum);
			
	    	// Send the requested problem as a response
			String responseBody = createPage(fProblemSet.getNextProblem ());
					
			debug ("Sending: " + responseBody);
				
			arg0.addResponseHeader("Content-Type", "text/html");
			arg0.sendResponseHeaders (200,responseBody.length());
			
			BufferedOutputStream buf=arg0.getOutputStream();

			try 
			{
				buf.write (responseBody.getBytes(),0,responseBody.length());
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
			
			debug ("Response written to client.");					
	    }
	    
	    //>------------------------------------------------------------------
	    
	    /**
	     * This happens when the user clicks on the "View statistics" button on admin.html in admin mode.
	     * It returns a HTML page that shows statistics about the local problem sets and logs.
	     */
	    
	    if(fileURI.contains("/stats.cgi"))
	    {
	    	found = true;
	    	
	    	StringBuilder responseBuilder;
	    	
	    	if(CTATLink.adminLogin)
	    	{
		    	responseBuilder = new StringBuilder("<h1>Statistics</h1>");
		    	
		    	/* Load the problem sets in order to calculate statistics */
		    	boolean result = true;
		    	
		    	if (CTATLink.fDirectoryTXT.isLoaded ()==false)
					result = CTATLink.fDirectoryTXT.loadDirectoryTXT (CTATLink.htdocs+"FlashTutors/directory.txt");
				
				if(result == true)
				{
					ArrayList<CTATProblemSet> problemSets = CTATLink.fDirectoryTXT.getEntries();
					
					int numProblemSets = problemSets.size();
					int totalProblems = 0;

					/* Calculate statistics and add them to the response */
					for(int i = 0; i < numProblemSets; i++)
					{
						CTATProblemSet probSet = problemSets.get(i);
						totalProblems += probSet.getNumProblems();
						responseBuilder.append("Problem set \"" + probSet.getName() + "\": " + probSet.getNumProblems() + " problems<br/>");
					}
					
					responseBuilder.append("<hr/>");
					responseBuilder.append("Number of problem sets: " + numProblemSets + "<br/>");
					responseBuilder.append("Total problems: " + totalProblems + "<br/>");
				}
				else
				{
					responseBuilder.append("Error: The problem sets could not be loaded.<br/>");
				}
				
				/* Add DataShop upload statistics */
				responseBuilder.append("<hr/>");
				Date lastUpload = CTATDataShop.getLastUploadTime();
				if(lastUpload != null)
				{
					DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
					responseBuilder.append("Last upload of log data to DataShop was " + df.format(lastUpload) + "<br/>");
				}
				else
				{
					responseBuilder.append("Log data has not been uploaded to DataShop during this session.<br/>");
				}
				
				responseBuilder.append("Size of local log file: " + (new File(CTATLink.datashopFile)).length() + " bytes<br/>");
				
				responseBuilder.append("<a href=\"/admin.html\">Return to administrator page</a>");
				String responseBody = responseBuilder.toString();
				
				/* Send response */
				try
		    	{
		    		arg0.addResponseHeader("Content-Type", "text/html");
		    		arg0.sendResponseHeaders(200, responseBody.length());
		    		BufferedOutputStream buf =arg0.getOutputStream();
		    		buf.write(responseBody.getBytes(), 0, responseBody.length());
		    		buf.flush();
		    	}
		    	catch(IOException e)
		    	{
		    		debug("IOError writing back response: " + e);
		    	}
	    	}
	    	else
	    	{
    			arg0.sendResponseHeaders(403, 0); // 403 = forbidden
	    	}			
	    }
	    	    	
		//>---------------------------------------------------------------------		
		
		if (fileURI.contains("/nextproblem.cgi"))
		{
			found=true;
   		
			debug ("Processing 'next problem' request ...");		
					
			CTATProblem nextProblem=fProblemSet.getNextProblem ();
			
			if (nextProblem==null)
			{
				debug ("nextProblem==null, attempting to roll over to the next problem set");
				
				ArrayList<CTATProblemSet> problemSets=null;				
				
				if (curriculum!=null)
				{
					debug ("We have a curriculum, loading next problem set in assignment ...");
					
					problemSets=curriculum.getProblemSets(curriculum.getNextStudentAssignment(fProblemSet.getName()));
				
					if (fProblemSet!=null)
						fProblemSet=problemSets.get(0);						
				}
				else
				{
					debug ("We don't have a valid curriculum, using directory.txt ...");

					fProblemSet=CTATLink.fDirectoryTXT.getNextEntry();			
				}
				
				if (fProblemSet!=null)
				{
					debug ("We appear to have more problem sets to go through, booting: " + fProblemSet.getName());
					nextProblem=fProblemSet.getNextProblem ();
				}
				else
					debug ("It appears we've reached the end of the assignment");				
			}
						
			fProblemSet.deActivate();
			nextProblem.setActive(true);
			
			String responseBody=createPage (nextProblem);

			sendHTMLResponse(arg0, responseBody, 200);  // 200 => OK			
		}		    
	    
	    //>------------------------------------------------------------------
	    
	    return (found);
	}
	/**
	 *
	 */	
	public Boolean doPOST (CTATHTTPExchange arg0)
	{
		debug ("doPOST ()");
				
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
				writeToLog (logMessage);
       	
				//arg0.getResponseHeaders().add ("Content-Type", "text/html; charset=ISO-8859-1");
			
				arg0.sendResponseHeaders(500, 0);

				responseBody += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
				responseBody += "<log-result>\n";
				responseBody += "  <read-request success=\"true\" length=\"" + getPOSTContentSize (arg0) + "\" />\n";
				responseBody += "  <write-file success=\"true\">\n    ";
				responseBody += CTATLink.logdir+"/log/server";
				responseBody += "\n  </write-file>\n";
				responseBody += "</log-result>\n";
					
				arg0.addResponseHeader("Content-Type", "text/xml"); /* arg0.addResponseHeader("Content-Type", "text/xml") */
				
				sendResponse (arg0, responseBody, 200);  // 200 => OK				
			}	
		}
		
		//>---------------------------------------------------------------------		
		
		if (fileURI.contains("/problemselect.cgi") || fileURI.contains("/lastproblem.cgi"))
		{
			found=true;
   		
			debug ("Processing '/problemselect.cgi or /lastproblem.cgi' request ...");		
   		    		
			String queryString=getPOSTContent (arg0);
			if (queryString!=null)
			{			
				Map<String, String> map=urltools.parseQuery (queryString);
				
				String summary=map.get ("summary");
				
				try 
				{
					if (summary != null)
					{
						summary = URLDecoder.decode(summary, "UTF-8");
						
						debug ("Storing problem summary ...");
												
						problemSummaries.add(summary);
						
						debug ("Notifying handlers ...");
						
						processProblemEnd(summary);
					}
				} 
				catch (UnsupportedEncodingException uee) 
				{
					System.out.println("Exception decoding UTF-8:"+uee);  // shouldn't happen
				}
				
				/*
				 * Implemented according to:
				 * https://docs.google.com/document/d/1B4r8jf4vv8dDkL5ULl1aSMngpmS5TevB1qsGyjSweho/edit
				 */
				
				//sendResponse(arg0," ", 200);
				sendHTMLResponse(arg0," ", 200);  // 200 => OK
									
				/*
				String cmd=map.get("cmd");
				
				debug ("Processing cmd: " + cmd);
				
				if (summary != null || "doneNextData".equals(cmd))
				{		
					debug ("Processing doneNextData ...");
					problemSummaries.add(summary);
					processProblemEnd(summary);

					if(fileURI.contains("/lastproblem.cgi")) {
						debug ("Processing /lastproblem.cgi");
						String endPage=CTATLink.fManager.getContents (CTATLink.htdocs+"templates/endofproblemset.html");
						if(endPage == null || endPage.length() < 1)
							endPage = "<html><body><p>You have completed the last problem in this problem set."+
									" There are no more problems to display.</p></body></html>";
						sendHTMLResponse(arg0, endPage, 200);
					} else { 
						CTATProblem nextProblem = fProblemSet.getNextProblem();  // advance position
						int remaining = fProblemSet.countRemainingProblems();    // then look beyond
						responseBody=fTagGenerator.generateFlashVars (nextProblem, fProblemSet, remaining < 1);
						arg0.addResponseHeader("Content-Type", "application/x-www-form-urlencoded");
						sendResponse(arg0, responseBody, 200);
					}
				}
				
				if ("doneNext".equals(cmd))
				{		
					debug ("Processing doneNext ...");
					responseBody=createPage (fProblemSet.getNextProblem ());
					sendHTMLResponse(arg0, responseBody, 200);  // 200 => OK
				}
				
				if ("resume".equals(cmd))
				{		
					debug ("Processing resume ...");
					responseBody=createPage (fProblemSet.getCurrentProblem ()); // problem counter should already have been advanced (in doneNext)
					sendHTMLResponse(arg0, responseBody, 200);  // 200 => OK
					problemSummaries.add(summary);
					processProblemEnd(summary);
				}
				*/								
			}	
		}
		   	    	
		//>---------------------------------------------------------------------		
		
		if (fileURI.contains("/gotoproblem.cgi"))
		{
			found=true;
   		
			debug ("Processing 'goto problem' request ...");		
   		
			if (!CTATLink.adminLogin) 
			{
				if (!CTATLink.getAdminPassword()) 
				{
		    		arg0.sendResponseHeaders(204, 0); // 204 = no content
		    		//arg0.close();

	    			return (false);
				}
			}
   		
			String queryString=getPOSTContent (arg0);
			if (queryString!=null)
			{			
				Map<String, String> map=urltools.parseQuery (queryString);
				
				//String summary=map.get ("summary");
				String nextProblem=map.get ("position");				
				//String cmd=map.get("cmd");
				
				fProblemSet.setCurrentIndex (Integer.parseInt(nextProblem.trim()));
				responseBody=fTagGenerator.generateFlashVars (fProblemSet.getNextProblem (),fProblemSet);
				sendHTMLResponse(arg0, responseBody, 200);								
			}
		}
   	
		//>---------------------------------------------------------------------
   	
		if(fileURI.contains("datashop.cgi"))
	    {
	    	/* This is a request to upload log to datashop */
	    	
	    	found = true;
	    	
	    	if(CTATLink.adminLogin)
	    	{
	    		// Choose the DataShop server URL based on the user's selection
		    	String postContent = getPOSTContent(arg0);
		    	if(postContent.contains("=QA"))
		    	{
		    		CTATLink.datashopURL = "http://pslc-qa.andrew.cmu.edu/log/server"; // QA server
		    	}
		    	else if(postContent.contains("=Main"))
		    	{
		    		int response = JOptionPane.showConfirmDialog(null, "You have chosen to upload the log to the main DataShop server. " +
		    				"Only actual student data should be sent to this server. Are you sure you want to continue?", 
		    				"Please confirm your selection", JOptionPane.YES_NO_OPTION);
		    		if(response == JOptionPane.NO_OPTION)
		    		{
		    			arg0.sendResponseHeaders(204, 0); // 204 = no content
		    			arg0.close();
		    			return (false);
		    		}
		    		CTATLink.datashopURL = "http://learnlab.web.cmu.edu/log/server"; // main DataShop server
		    	}
		    	else if(postContent.contains("=Test"))
		    	{
		    		CTATLink.datashopURL = "http://digger.pslc.cs.cmu.edu/log/server/sandboxlogger.php"; // third test server
		    	}
	    		
		    	// Send HTTP response
	    		responseBody = "<html>Currently uploading data to DataShop. This may take a few minutes. " +
	    		"You will be notified when the data transfer is complete.<br/>" +
	    		"<a href=\"/admin.html\">Return to administrator page</a></html>";
	    		
	    		sendHTMLResponse(arg0, responseBody, 200);
		    	
		    	CTATDataShop datashop = new CTATDataShop();
		    	boolean success = datashop.migrateData();
		    	
		    	if(success)
		    	{
		    		JOptionPane.showMessageDialog(null, "The data transfer you requested is complete.");
		    	}
		    	else
		    	{
		    		JOptionPane.showMessageDialog(null, "An exception occurred which prevented the data from being transferred.");
		    	}
	    	}
	    	else
	    	{
	    		/* Admin login is required */

	    		arg0.sendResponseHeaders(403, 0); // 403 = forbidden
	    	}		    	
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
	    }
		
	    //>------------------------------------------------------------------
	    
	    if(fileURI.equalsIgnoreCase("/exittutorshop.cgi"))
	    {
	    	debug("Processing exit TutorShop request ...");
   		
	    	found = true;

	    	responseBody = CTATLink.SO_LONG;
			sendHTMLResponse(arg0, responseBody, 200);  // 200 => OK
			arg0.close();
   		
			System.exit(0);
	    }
	    		
		//>---------------------------------------------------------------------
		
		return (found);
	}	
}
