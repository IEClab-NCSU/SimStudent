/**
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATCLBridge.java,v 1.14 2012/10/16 20:01:34 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATCLBridge.java,v $
 Revision 1.14  2012/10/16 20:01:34  vvelsen
 Lots of changes to align the various http handlers. Most of the work has been done in the DVD handler for FIRE which now uses the progress database to also store user information

 Revision 1.13  2012/10/15 21:25:07  sewall
 Revise calls to CTATLink constructor to specify the proper CTATXxxxFileManager.

 Revision 1.12  2012/10/05 15:06:40  vvelsen
 Added a new subclass of the local tutorshop that can work completely offline. Also added a patch for the situation where the applet version of the file manager was assigned as the default manager

 Revision 1.11  2012/10/03 17:50:26  sewall
 First working version with CTATAppletFileManager.

 Revision 1.10  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.9  2012/09/18 00:47:01  sewall
 Now also save datasetName to config.data.

 Revision 1.8  2012/09/14 13:05:48  vvelsen
 Started migrating the curriculum.xml to Octav's new format

 Revision 1.7  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.6  2012/09/11 13:16:18  vvelsen
 Made various changes to the local http handler. It can now use both a directory.txt file as well as a curriculum.xml file. Added a way to indicate if the server should generate flashvars with an info field. When the info field is generated it creates the horizontal menu bar at the top of the screen that shows all available problems

 Revision 1.5  2012/09/08 15:35:02  sewall
 Interim work: end-to-end launch, run CTAT problem, enter CL session. Lacks J. Booth curriculum selection algo, problem set end notification.

 Revision 1.4  2012/09/07 18:15:05  vvelsen
 Quick checkin so that Jonathan can test his CL bridge code

 Revision 1.3  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 $RCSfile: CTATCLBridge.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATCLBridge.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Pass an object that implements this interface to CTATHTTPHandler
 * to be notified at the completion of the problem set
 */
public class CTATCLBridge extends CTATFlashTutorShop implements Runnable, ProblemSetEndHandler, ProblemEndHandler 
{
	//private static final long serialVersionUID = -1L;		
	
	/** Format for timestamps. */
	private static final DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd.HH.mm.SS");
	
//	private CTATCLBridge service=null;
	private CTATCurriculum curriculum=null;
	
	/**
	 * 
	 */
	public CTATCLBridge ()
	{
		setClassName ("CTATCLBridge");
    	debug ("CTATCLBridge ()");	
	}
	/**
	 * TODO somehow communicate the user id to the local tutoring service
	 */
	public void runBridge(String aUsername,String contentJar) 
	{
		debug ("runBridge ("+aUsername+","+contentJar+")");
		
    	System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); // forces java.net.HttpURLConnection to accept "Via" header
    	
    	@SuppressWarnings("unused")
		CTATLink link=          // run the CTATLink constructor--to allow for decryption of the config file
				new CTATLink(new CTATAppletFileManager());
    	    
    	if (contentJar!=null)
    	{
    		if (contentJar.isEmpty()==false)
    			CTATLink.mountJar (contentJar);
    	}	
		
    	//CTATLink.contentJar=contentJar;    	
    	CTATLink.userID=aUsername;
    	CTATLink.allowWriting=false;
    	CTATLink.printDebugMessages=true;
    	CTATLink.remoteHost="local";
    	CTATLink.hostName = "localhost";
    	CTATLink.wwwPort=8080;

    	// Configure CTATLink according to config file
    	CTATLink.fManager.configureCTATLink(); 
		    	
    	//useSysTray=false;
    	    	    
    	if (CTATLink.fManager.doesFileExist(CTATLink.htdocs + "/curriculum.xml")==true);
    	{
    		debug ("Curriculum file exists, loading ...");
    		
    		//File currFile=new File (CTATLink.htdocs + "/curriculum.xml");
    		
    		String currContents=CTATLink.fManager.getContents(CTATLink.htdocs + "/curriculum.xml");
    		
    		try 
    		{
    			curriculum = new CTATCurriculum (currContents);
    		} 
    		catch (Exception e) 
    		{
    			e.printStackTrace(System.out);
    			JOptionPane.showMessageDialog (null, "An error occurred when reading the curriculum description ("+CTATLink.htdocs+"curriculum.xml):\n"+e.getMessage()); 
    			curriculum = null; 
    		}
    		
    		if (curriculum!=null)
    		{
    			debug ("Curriculum check: " + curriculum.toString());
    			curriculum.problemSetsPath=CTATLink.htdocs+"/FlashTutors/";
    			curriculum.loadAllProblemSets ();
    		}
    	}	
    	    	        	
        debug ("Configuring the locall offline code some more ...");
        
//       	service=new CTATCLBridge ();       	
    	setProblemSetEndHandler(this); //
    	setProblemEndHandler(this); // 
    	setUseTray(false); // 
//    	otherServers.addAll(otherServers); //

        debug ("Starting thread ...");
    	
    	try 
    	{
    		String accessLogName = "access"+dateFmt.format(new Date())+".log";
    		
    		CTATHTTPLocalHandler handler=new CTATHTTPLocalHandler (CTATLink.logdir+accessLogName,
    															   new UserProgressDatabase ());
    		//handler.setCurriculum(curriculum);
    		
    		debug ("Assigning local handler ...");
    		
			setHandler (handler); //
		} 
    	catch (IOException e1) 
    	{		
    		debug ("Error: unable to installe HTTP handler");
    		e1.printStackTrace(System.out);
    		System.exit(1);
		}    	
        
        java.awt.EventQueue.invokeLater (this);
	}
	
	/**
	 *  Call {@link #runBarebones()}.
	 */
	public void run()
	{
		debug ("run ()");

		runBarebones(); //
	}
	
	/**
	 * Implement in child class to be informed when the problem set
	 * has been finished by a student.
	 * @return false If true, would mean that local tutorshop could exit.
	 */
	public boolean problemSetEnd(List<String> problemSummaries)
	{
		return false;
	}
	/**
	 * Implement in child class to be informed when a problem
	 * has been finished by a student.
	 * @return false If true, would mean that local tutorshop could exit.
	 */
	public boolean problemEnd(String problemSummary) 
	{
		return false;
	}
    /**
	 *
	 */
    public static void main(String args[]) 
	{
    	System.out.println ("main (CTATCLBridge) ()");
    	   	    	    	
        for (int i=0;i<args.length;i++)
        {
        	if (args [i].compareTo ("-config")==0)
        	{
            	@SuppressWarnings("unused")
        		CTATLink link = new CTATLink(); // run the CTATLink constructor; this is necessary to allow for decryption of the config file

        		makeConfigFile();
        		return;
        	}        	     	
        }	
    	    	    	        
        System.out.println ("Starting thread (CTATCLBridge) ...");
        
        java.awt.EventQueue.invokeLater (new Runnable() 
        {
            public void run()
            {
            	System.out.println ("run (CTATCLBridge)()");
            	
            	CTATCLBridge service=new CTATCLBridge ();
            	
            	System.out.println ("run (CTATCLBridge) Executing bridge call ()");
            	
                service.runBridge ("dummy","jb.jar");
                //service.runBridge ("dummy","");
            }
        });        
    }       		
}
