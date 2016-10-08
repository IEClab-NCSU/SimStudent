/**
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATFlashTutorShop.java,v 1.37 2012/10/16 20:01:34 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATFlashTutorShop.java,v $
 Revision 1.37  2012/10/16 20:01:34  vvelsen
 Lots of changes to align the various http handlers. Most of the work has been done in the DVD handler for FIRE which now uses the progress database to also store user information

 Revision 1.36  2012/10/12 14:34:36  kjeffries
 more GUI/threading fixes

 Revision 1.35  2012/10/10 14:11:56  vvelsen
 Started patching the download code to make sure it works well with the config panel. Lots of bugs right now and at this point in time we should not rely on the download results

 Revision 1.34  2012/10/05 15:06:40  vvelsen
 Added a new subclass of the local tutorshop that can work completely offline. Also added a patch for the situation where the applet version of the file manager was assigned as the default manager

 Revision 1.33  2012/10/05 14:23:35  kjeffries
 fixed some threading issues

 Revision 1.32  2012/10/01 22:33:15  sewall
 Now Monitor.request() throws and exception.

 Revision 1.31  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.30  2012/09/21 13:19:01  vvelsen
 Quick checkin to get vital code into CVS for FIRE

 Revision 1.29  2012/09/18 15:21:55  vvelsen
 We should have a complete and working asset download manager that can be run as part of the config panel (or command line)

 Revision 1.28  2012/09/17 17:08:51  sewall
 In makeConfigFile(), read the existing config.data file before showing the editor.

 Revision 1.27  2012/09/14 13:05:48  vvelsen
 Started migrating the curriculum.xml to Octav's new format

 Revision 1.26  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.25  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.24  2012/08/21 21:44:28  kjeffries
 added option to download all content that will be needed for the curriculum as described in curriculum.xml

 Revision 1.23  2012/08/20 15:31:36  kjeffries
 restore some stuff that was lost in the merge

 Revision 1.20  2012/08/15 20:24:56  kjeffries
 can now add a problemEndHandler that is passed the problem summary at the end of each problem

 Revision 1.19  2012/08/13 13:07:46  kjeffries
 when starting up, use CS2N-specific http handler

 Revision 1.18  2012/08/10 15:08:01  vvelsen
 Added a class that can represent the local tutoring service as one single class. This is especially useful if we want to integrate a problem set directly into a Carnegie Learning curriculum for example, or another such LMS

 Revision 1.17  2012/07/23 16:17:29  kjeffries
 UI changes, also increased compatibility for older JREs

 Revision 1.16  2012/06/23 18:48:11  sewall
 CTAT2891: Move request logic from CTATFlashTutorShop to Monitor. First working version.

 Revision 1.15  2012/06/22 19:31:30  sewall
 Fix address for localhost: was 127.0.0.7, now 127.0.0.1.

 Revision 1.14  2012/06/18 22:01:59  kjeffries
 start the diagnostic ConnectivityChecker thread at startup, and other small changes

 Revision 1.13  2012/06/12 19:41:59  kjeffries
 use CTATLink's file manager instead of CTATDesktopFileManager to read config file

 Revision 1.12  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.11  2012/05/24 16:13:36  kjeffries
 also show exit UI when in admin mode

 Revision 1.10  2012/05/14 15:28:55  vvelsen
 Disabled automatic logging of critical events in CTATDeamon because it is the base class of CTATHTTPServer, which meant that anything that uses that class would automatically want to create a logfile

 Revision 1.9  2012/05/07 19:08:15  vvelsen
 Started some refactoring of our Java tree (with permission) First we'll do a bunch of small utilities that almost nobody uses, which seems to be the majority of our code

 Revision 1.8  2012/04/16 16:37:35  vvelsen
 Added a SwingWorker class that can be accessed through CTATLink. It's pretty much the only way in which random code can access a progress bar and update it the way Swing wants you to do it

 Revision 1.7  2012/02/29 18:09:13  vvelsen
 Small fix to the proper main in the local tutorshop

 Revision 1.6  2012/02/29 17:44:53  vvelsen
 Refined our file classes and local tutorshop to behave better when managed by a loader class. Added some nice utility functions in the file manager

 Revision 1.5  2012/02/03 20:49:11  sewall
 Move LauncherServer.CommunicationsThread to new public class Monitor.

 Revision 1.4  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.32  2011/11/10 23:50:45  sewall
 1. maxCachedFiles now a CTATLink parameter. 2. no longer delete files from disk cache. 3. hash map lookup for cache. 4. ignore rails timestamps in file names. 5. kill old instance by monitor port request.

 Revision 1.31  2011/10/11 13:17:09  sewall
 Replace path '/student-fs.html' with '/local' for OAuth patch.

 Revision 1.30  2011/10/04 19:00:04  sewall
 Increase non-poll timeout to 30 min.

 Revision 1.29  2011/10/03 23:56:20  sewall
 Now use shutdown service on monitor port to kill prior instance.

 Revision 1.28  2011/09/30 20:25:56  sewall
 Add FlashVars dataset_name, dataset_level_type1, dataset_level_name1, problem_name SessionLog

 Revision 1.27  2011/09/29 15:52:16  sewall
 Now replace browser page when user exits server via Swing gui.

 Revision 1.26  2011/09/29 04:10:24  sewall
 Port changes from AuthoringTools/java/source/, where this code is now maintained.

 Revision 1.25  2011/09/27 17:51:53  sewall
 ExitUI now has window listener and DO_NOTHING_ON_CLOSE to prevent window disappearing (but application still running) when user clicks X button at top right.

 Revision 1.24  2011/09/27 13:57:27  sewall
 Add CTATHTTPExchange.requestParameters. Change to use student-fs for student. Demo getpush.cgi capability.

 Revision 1.23  2011/09/21 14:54:09  sewall
 Now use ProcessRunner in killByPid().

 Revision 1.22  2011/09/21 13:09:04  sewall
 Now kill prior instance on startup.

 Revision 1.21  2011/09/15 17:34:41  sewall
 Added Exit GUI for students.

 Revision 1.20  2011/09/14 19:14:32  sewall
 Restore capability to view & edit all config parameters.

 Revision 1.19  2011/08/26 21:04:45  kjeffries
 some small cleanups

 Revision 1.18  2011/08/12 20:42:27  kjeffries
 Admin password is required for config mode. Added -debug command line option to print debug statements.

 Revision 1.17  2011/08/05 21:04:17  kjeffries
 Use LauncherServer instead of CTATLauncherServer to launch tutoring service

 Revision 1.16  2011/07/27 21:11:00  kjeffries
 Configuration settings can now be changed through a graphical interface (run with -config)

 Revision 1.15  2011/07/22 20:47:59  kjeffries
 Writing to disk (e.g. logging) can now be turned on/off in config file

 Revision 1.14  2011/07/18 14:40:33  kjeffries
 Added support for command line options -XMLsUnencrypted and -BRDsUnencrypted

 Revision 1.13  2011/07/07 21:00:08  kjeffries
 Did some cleaning up of config file creation mode; added a remote host field to the config file.

 Revision 1.12  2011/06/24 19:42:55  kjeffries
 Added documentation for makeConfigFile()

 Revision 1.11  2011/06/03 20:34:46  kjeffries
 Added config mode which allows for creation of configuration files.

 Revision 1.10  2011/05/27 20:48:52  kjeffries
 Added support for admin mode and config file that specifies CTATLink fields. See comments in main() for how to create a config file.

 Revision 1.9  2011/05/17 15:22:48  kjeffries
 corrected hard-coding of URI

 Revision 1.8  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central registry. Please see CTATLink for more information.

 Revision 1.7  2011/04/01 20:09:57  vvelsen
 Further features and refinements in the problem sequencing code.

 Revision 1.6  2011/02/17 17:43:47  vvelsen
 Small cleanups to move regularly used strings into CTATLink

 Revision 1.5  2011/02/15 15:20:42  vvelsen
 Added proper datashop logging to disk through http in the built-in web server. Added encryption code for logfiles, text based streams, etc. Added a first update scheduler that can be used to both retrieve new versions of the application as well as new content and student info.

 Revision 1.4  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 Revision 1.3  2011/02/08 14:42:54  vvelsen
 More features added. The server now properly generates html that can load swf files. SWF files loaded through that html connect back to the built-in tutoring service but the code still needs a lot of work to deal with brd path checking and Flash security handling. Web server log files are now properly generated.

 Revision 1.2  2011/02/07 19:50:15  vvelsen
 Added a number of files that can manage local files as well as generate html from parameters and templates.

 Revision 1.1  2011/02/06 16:55:27  vvelsen
 Added a first working version of a standalone USB based TutorShop.

 $RCSfile: CTATFlashTutorShop.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATFlashTutorShop.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import edu.cmu.hcii.ctat.CTATHTTPHandler.PushResponse;
//import edu.cmu.hcii.utilities.DiagTools;
import edu.cmu.pact.Utilities.trace;

/**
 *  
 */
public class CTATFlashTutorShop extends CTATBaseTutorShop implements ProblemSetEndHandler, ProblemEndHandler
{    		
	private CTATCurriculum curriculum=null;
	
	private UserProgressDatabase localUserDB=null;    

    /**
     * I've moved the actual execution of the code into a separate method since we need to have control
     * over if we want to just load the class or run it. This is important because the updater would
     * otherwise accidentally run the server just by instantiating the class.  
	 */
    public CTATFlashTutorShop () 
    {    	    	    	
    	setClassName ("CTATFlashTutorShop");
    	debug ("CTATFlashTutorShop ()");
    	    	
    	debug ("microedition.platform: " + System.getProperty("microedition.platform"));    	    	    	
    }

    /**
     * If {@link CTATLink#remoteHost} is set to an external machine, invoke
     * {@link CTATDiagnostics#runBackgroundConnectivityChecker()}.
     */
    protected void monitorConnectivity() 
    {

    	if( !(CTATLink.remoteHost.equals("") || CTATLink.remoteHost.equals("local")) )
        {
        	diags.runBackgroundConnectivityChecker(); // run thread in background to detect loss of internet connection
        }
		
	}
    /**
     * 
     */
	protected void invokeBrowserOnLocalWebServer()
    {
        /*
         * If the calling thread does not have the necessary permissions, and this is invoked from 
         * within an applet, AppletContext.showDocument() is used. Similarly, if the calling does 
         * not have the necessary permissions, and this is invoked from within a Java Web Started 
         * application, BasicService.showDocument()  is used. 
         * Throws:
         * NullPointerException - if uri is null 
         * UnsupportedOperationException - if the current platform does not support the Desktop.Action.BROWSE action 
         * IOException - if the user default browser is not found, or it fails to be launched, or the default handler application failed to be launched 
         * SecurityException - if a security manager exists and it denies the AWTPermission("showWindowWithoutWarningBanner") permission, or the calling thread is not allowed to create a subprocess; and not invoked from within an applet or Java Web Started application 
         * IllegalArgumentException - if the necessary permissions are not available and the URI can not be converted to a URL
         */
    	
        try
        {
        	Desktop.getDesktop();
        } catch(NoClassDefFoundError e) { /* ignore missing java.awt.Desktop class */ }
        
        debug ("Launching browser ...");
        
        //debug ("active threads "+DiagTools.listThreads());
        
        launchBrowser ();
	}
	/**
     * If {@link #useSysTray} is true, try to create a SysTray icon.
     */
    protected void createSysTrayIcon() 
    {       
        debug ("Launching trayicon ...");        
        
        if (useSysTray==true)
        {
        	try
        	{
        		if (Desktop.isDesktopSupported ()==false)
        		{
        			debug ("Diagnostic: TutorShop is not allowed to start a desktop application");
        			return;
        		}
        	} 
        	catch(NoClassDefFoundError e) 
        	{ /* ignore; this can happen in older versions of JRE which do not have java.awt.Desktop and java.awt.TrayIcon */ }
        
        		try
        		{
        			// display the system tray (if possible on this platform)
        			LocalTSSystemTray systray = LocalTSSystemTray.getInstance();
        			systray.setWebServer(wserver);
        			systray.display();
        		} 
        		catch(NoClassDefFoundError e) 
        		{ 
        			/* ignore; this can happen in older versions of JRE which do not have java.awt.Desktop and java.awt.TrayIcon */         			
        		}        
        }             
	}

    /**
     * 
     */
    public static void launchBrowser ()
    {
    	CTATBase.debug ("CTATHTML5Driver","launchBrowser ()");
    	
        URI uri=null;
        
        Desktop tmpDesktop=null;    
        
        try
        {
        	tmpDesktop=Desktop.getDesktop();
        } 
        catch(NoClassDefFoundError e) { /* ignore missing java.awt.Desktop class */ }
                
        try 
        {
            if(CTATLink.appMode.equals("admin")) 
            {
            	if (!CTATLink.getAdminPassword()) 
            	{
            		System.exit(0); // null indicates that cancel was pressed
            	}
            	
            	// At this point, the correct password has been entered
            	CTATLink.adminLogin = true;
            	uri = new URI ("http://" + CTATLink.hostName + ":" + CTATLink.wwwPort + "/admin.html");
            }
            else 
            {
            	uri=new URI ("http://" + CTATLink.hostName + ":" + CTATLink.wwwPort);
            }
            
            try
            {
            	tmpDesktop.browse (uri);
            	
                //CTATBase.debug ("CTATFlashTutorShop","after desktop.browse("+uri+"): active threads "+DiagTools.listThreads());
            }
            catch(NoClassDefFoundError e) // this will catch old JRE versions that do not have the Desktop class
            {
            	JOptionPane.showMessageDialog(null, "Please open a browser and enter "+uri+" in the address bar.");
            }
        }
        catch (IOException ioe) 
        {
            ioe.printStackTrace();
        }
        catch (URISyntaxException use) 
        {
            use.printStackTrace();
        }
    }
	/**
	 * Create a config file that specifies values for the static fields of CTATLink.
	 * This method is called when running with the -config option.
	 */
    public static void makeConfigFile()
    {
    	CTATBase.debug ("CTATHTML5Driver","makeConfigFile ()");
    	
    	/* Prompt for administrator password */
    	/*
    	String password = (new CTATDesktopFileManager()).getContentsEncrypted(CTATLink.adminPasswordFilename);
    	
    	if(password == null)
    	{
    		JOptionPane.showMessageDialog(null, "Error reading password file.");
    		System.exit(1);
    	}
    	
    	String input=null;
    	    	
    	do 
    	{
    		input = JOptionPane.showInputDialog(null, "Please enter the administator password.");
    	} 
    	while(input != null && !input.equals(password));
    	
    	if(input == null)
    	{
    		System.exit(0); // null indicates that cancel was pressed
    	}
    	*/
    	try {
    		CTATLink.fManager.configureCTATLink();  // read the existing file, if any
    	} catch (Exception e) {
    		System.err.println("Error reading config.data: "+e+";\n  cause "+e.getCause());
    		e.printStackTrace();
    	}
    	
    	JFrame frame = new JFrame();
    	frame.setTitle ("Standalone TutorShop -- Configuration Settings");
    	frame.setSize(838,554);
    	frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    	frame.setContentPane(new CTATConfigPanel (frame));
    	//frame.add (new CTATConfigPanel (frame));
    	//frame.pack ();
    	frame.setVisible (true);
    }
    /**
     * Show a small gui that lets the user quit the application.
     */
    protected void showExitUI() 
    {
    	debug ("showExitUI ()");
    	
    	try
    	{
    		if(LocalTSSystemTray.getInstance().isDisplayed())
    			return; // don't show the UI if its functionality is accessible via system tray
    	} catch(NoClassDefFoundError e) { /* assume the system tray icon is not displayed */}
    	
    	final JFrame frame = new JFrame("TutorShop");
    	frame.getContentPane().setLayout(new BorderLayout());
    	JPanel panel = new JPanel();
    	panel.setBorder(new EmptyBorder(3, 3, 3, 3));
    	
    	JButton exitBtn = new JButton("Exit TutorShop");
    	exitBtn.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			confirmExit(e, frame);
    		}
    	});
    	panel.add(exitBtn);
    	panel.setPreferredSize(new Dimension(200,50));  // enough for frame title
    	frame.getContentPane().add(panel);

    	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // replaces HIDE
    	frame.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			confirmExit(e, frame);
    		}
    	});
    	if (trace.getDebugCode("exit"))
    		trace.out("exit", "showExitUI.frame listeners "+frame.getWindowListeners()+
    				", defaultCloseOperation "+frame.getDefaultCloseOperation());
    	
    	frame.pack();
    	frame.setVisible(true);
	}    
        
    /**
     * Prompt the user whether he or she wants to quit, and
     * call {@link System#exit(int)} if confirmed.
     * @param e event triggering this prompt
     * @param parentFrame parent for {@link JOptionPane}
     */
	protected void confirmExit(EventObject e, JFrame parentFrame) 
	{
		debug ("confirmExit ()");
		
		if (trace.getDebugCode("exit"))
			trace.out("kill", "confirmExit("+e+")");
		int okCancel = JOptionPane.showConfirmDialog(parentFrame,
				"Are you sure you want to quit TutorShop?",
				"Confirm Exit TutorShop", 
				JOptionPane.OK_CANCEL_OPTION);
		if (okCancel != JOptionPane.OK_OPTION)
			return;
		if (wserver != null && wserver.getHandler() instanceof CTATHTTPHandler)
			((CTATHTTPHandler) wserver.getHandler()).enqueuePushResponse(PushResponse.Exit);
		long ms = CTATLink.pushPollingInterval+2000;
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			trace.errStack("Error while sleeping "+ms+"ms awaiting poll for exit", ie);
		}
		System.exit(0);
	}    
    
    /**
     * 
     */
    public static boolean downloadContent (JTextArea aConsole, 
    									   JProgressBar aBar, 
    									   boolean skipGlobalAssets,
    									   boolean useSwingWorker,
    									   Runnable doWhenDone)
    {
    	CTATBase.debug ("CTATFlashTutorShop","downloadContent ()");
    	
    	boolean success=false;
    	
    	CTATTutorUpdater updater=new CTATTutorUpdater(null,aConsole,aBar);
    	
    	CTATCurriculum curr=updater.getCourse (CTATLink.FIREClass);
    	
    	if (curr==null)
    	{
    		JOptionPane.showMessageDialog(null, "Error: unable to download asssignment ("+CTATLink.lastError+")");
    		return (false);
    	}

    	if (CTATLink.fManager.setContents(CTATLink.htdocs+"/curriculum.xml",updater.getCurriculumXML())==false)
    	{
    		JOptionPane.showMessageDialog(null, "Error: unable to save: " + CTATLink.htdocs + "/curriculum.xml");
    		return (false);
    	}
    	        	
    	try
    	{
    		CTATContentCache cache=new CTATContentCache(new File (CTATLink.htdocs,"/cache/"),true);
    		cache.setConsole (aConsole);
    		cache.setProgressBar (aBar);
    		
    		updater.updateContentFromCurriculum(curr,
    											cache,
    											new File(CTATLink.htdocs,"/remoteBRDs/"),
    											CTATLink.remoteHost,
    											skipGlobalAssets,
    											useSwingWorker,
    											doWhenDone);
    		success = true; // updateContentFromCurriculum runs in its own thread, so at this point it may not have even finished. But assume it is successful.
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		//success = false;
    	}
    	
    	/*
    	Can't do this:
    	
    	if(success)
    		JOptionPane.showMessageDialog(null, "Content downloaded successfully.");
    		
    	because `success` being true doesn't mean the content has actually been downloaded yet. (It will be downloaded in a separate thread.)
    	*/
    	
    	if(!success)
    		JOptionPane.showMessageDialog(null, "Content download failed.");  
    	
    	return (success);
    }
    /**
     * 
     */
    public static Boolean parseArgs (String args [])
    {
    	CTATBase.debug ("CTATFlashTutorShop","parseArgs ()");
    	
        for (int i = 0; i < args.length; i++)
        {
        	if (args [i].compareTo ("-normal")==0)
        		CTATLink.appMode="normal";
        	
        	if (args [i].compareTo ("-safemode")==0)
        		CTATLink.appMode="safemode";
        	
        	if (args [i].compareTo ("-diagnostics")==0)
        		CTATLink.appMode="diagnostics";
        	
        	if (args [i].compareTo ("-install")==0)
        		CTATLink.appMode="install";
        	
        	if (args [i].compareTo ("-update")==0)
        	{
        		CTATLink.appMode="update";
        	}
        	
        	if (args [i].compareTo ("-admin")==0)
        		CTATLink.appMode="admin";
        	
        	if (args [i].compareTo ("-config")==0)
        	{
        		makeConfigFile();
        		return (false);
        	}
        	
        	if (args [i].compareTo ("-XMLsUnencrypted")==0)
        		CTATLink.problemSetXMLsAreEncrypted = false;
        	
        	if (args [i].compareTo ("-BRDsUnencrypted")==0)
        		CTATLink.BRDsAreEncrypted = false;
        	
        	if (args [i].compareTo ("-debug")==0)
        		CTATLink.printDebugMessages = true;
        	
        	if (args [i].compareTo ("-skipGlobalAssets")==0)
        		CTATLink.skipGlobalAssets = true;
        	
        	if (args [i].compareTo ("-downloadcontent")==0)
        	{
        		CTATLink.dlContent = true;
        		CTATLink.FIREClass=args [i+1];
        	}
        }	 
        
        return (true);
    }
    /**
     * 
     */
    protected void runPrep ()        
    {
    	debug ("runPrep ()");
    	
		killPreviousInstance();  // make any previous instance exit
		
    	System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); // forces java.net.HttpURLConnection to accept "Via" header
    	
    	@SuppressWarnings("unused")
		CTATLink link=new CTATLink(new CTATDesktopFileManager ()); // run the CTATLink constructor; this is necessary to allow for decryption of the config file
    	    
    	try 
    	{
			//localUserDB=new UserProgressDatabase (!CTATLink.allowWriting);
    		localUserDB=new UserProgressDatabase (true);
		} 
    	catch (IOException e2) 
    	{		
			e2.printStackTrace();
			System.exit(1);
		}
    	
    	// Configure CTATLink according to config file
    	CTATLink.fManager.configureCTATLink(); 

    	if((CTATLink.remoteHost.isEmpty()==true) || (CTATLink.remoteHost.equalsIgnoreCase("local")==true))
    	{
    		CTATLink.cache=new CTATContentCache (new File (CTATLink.getCacheDir()),false);

    		if (CTATLink.fManager.doesFileExist(CTATLink.htdocs + "/curriculum.xml")==true)
    		{
    			debug ("Curriculum file exists, loading ...");
    		    		
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

    				curriculum.loadAllProblemSets ();
    			}
    		}
    		else
    			debug ("Info: " + (CTATLink.htdocs + "/curriculum.xml") + " does not exist, attemting to load directory.txt instead ...");

    		if (curriculum==null)
    		{
    			debug ("No curriculum object, attempting to load from directory.txt ...");
    				
    			CTATLink.fDirectoryTXT=new CTATDirectoryTXT ();
    			
    			if (CTATLink.fDirectoryTXT.isLoaded ()==false)
    				CTATLink.fDirectoryTXT.loadDirectoryTXT (CTATLink.htdocs+"FlashTutors/directory.txt");				
    		}
    		else
    			debug ("We have a curriculum, no need to load directory.txt");    			

    		debug ("Configuring the local offline code some more ...");
               	
    		setProblemSetEndHandler(this);
    		setProblemEndHandler(this);
    	}
    	
    	setUseTray(true);	    	
    }
    /**
     * 
     */
    protected Boolean installHandler () throws IOException
    {
    	debug ("installHandler ("+CTATLink.handlerConfig+")");
    	
    	DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd.HH.mm.SS");    	    	
		
  		String accessLogName = "access"+dateFmt.format(new Date())+".log";    	
    	
    	//>-----------------------------------------------------------
    	
    	if (CTATLink.handlerConfig.equalsIgnoreCase("DEFAULT")==true)
    	{
    		debug ("Installing instance of CTATHTTPHandler ...");
    		
    		CTATHTTPHandler handler=new CTATHTTPHandler	(CTATLink.htdocs,CTATLink.logdir+"/access.log");
    		
    		setHandler (handler);
    	}
    	    	    	
    	//>-----------------------------------------------------------
    	
    	if (CTATLink.handlerConfig.equalsIgnoreCase("LOCAL")==true)
    	{
    		if((CTATLink.remoteHost.equalsIgnoreCase("local")==false) && (CTATLink.remoteHost.isEmpty()==false))
    		{
    			CTATLink.lastError="The local TutorShop is configured for local usage but the configuration has an external hostname";
    			debug (CTATLink.lastError);
    			return (false);
    		}
    		
    		debug ("Installing instance of CTATHTTPLocalHandler ...");
    		            		
       		CTATHTTPLocalHandler handler=new CTATHTTPLocalHandler (CTATLink.logdir+accessLogName,
       																localUserDB);           		
           	setHandler (handler);     	    		
    	}    	
    	
    	//>-----------------------------------------------------------
    	
    	if (CTATLink.handlerConfig.equalsIgnoreCase("OFFLINE")==true)
    	{
    		if((CTATLink.remoteHost.equalsIgnoreCase("local")==false) && (CTATLink.remoteHost.isEmpty()==false))
    		{
    			CTATLink.lastError="The local TutorShop is configured for local usage but the configuration has an external hostname";
    			debug (CTATLink.lastError);
    			return (false);
    		}
    		
    		debug ("Installing instance of CTATHTTPLocalHandler ...");
    		    		        	            		
    		CTATOfflineHTTPHandler handler=new CTATOfflineHTTPHandler (CTATLink.logdir+accessLogName,
    																   localUserDB,
    																   curriculum);           		
           	setHandler (handler);     	    		
    	}    	    	
    	
    	//>-----------------------------------------------------------    	
    	
    	if (CTATLink.handlerConfig.equalsIgnoreCase("CS2N")==true)
    	{    	
    		debug ("Installing instance of CTATCS2NHandler ...");
    		
    		try
    		{    		    		
    			CTATCS2NHandler handler=new CTATCS2NHandler (CTATLink.logdir+accessLogName,
						   									 localUserDB,
						   									 curriculum);

    			handler.setCurriculum(curriculum);
    		
    			handler.setUserid(System.getProperty("offline_user"));

    			debug ("Assigning local handler ...");

    			setHandler (handler);    		
    		} 
    		catch (Exception e1) 
    		{		
    			debug ("Error: unable to install HTTP handler");
    			CTATLink.lastError=e1.getMessage();
    			debug (CTATLink.lastError);
    			return (false);  // sewall 2013/05/18 changed for Android: was System.exit(1);  
    		}
    	}	
    	
    	//>-----------------------------------------------------------    	
    	
    	return (false);
    }
	/**
	 * @return {@link #nowExiting}
	 */
	public boolean isExiting() 
	{
		return nowExiting;
	}
	/**
	 * Set {@link #nowExiting} and call {@link #dispose()}.
	 * @return previous value of {@link #nowExiting}
	 */
	public boolean startExiting() 
	{
		debug ("startExiting ()");
		
		boolean result = nowExiting;
		
		nowExiting = true;
		
		debug("startExiting: nowExiting was "+result+"; nOtherServers "+otherServers.size()+ "; then call dispose()");
		
		if (otherServers != null) 
		{
			for (ExitableServer es : otherServers)
				es.startExiting();
		}
		
		return result;
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
	 * Use this method if you want to run the local tutorshop in your own
	 * application or server. It can be called as a method anywhere in your
	 * program but only once.
	 */
    public void mainForLoader (String args[])
	{    	
    	debug ("main ()");
    	
    	System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); // forces java.net.HttpURLConnection to accept "Via" header
    	
    	@SuppressWarnings("unused")
		CTATLink link = new CTATLink(new CTATDesktopFileManager ()); // run the CTATLink constructor; this is necessary to allow for decryption of the config file
    	
    	// Configure CTATLink according to config file
    	CTATLink.fManager.configureCTATLink(); 
    	
    	if (CTATFlashTutorShop.parseArgs (args)==false)
    	{
    		return;
    	}
    	
        debug ("Starting thread ...");
        
        java.awt.EventQueue.invokeLater (new Runnable() 
        {
            public void run()
            {
            	debug ("run ()");
            	
            	// So far all we've done is configuration which filled
            	// a number of public static objects
            	
                CTATFlashTutorShop service=new CTATFlashTutorShop ();
                service.otherServers.add(ls);
                service.otherServers.add(wserver);
                
                // Execute the basic thread located in CTATBaseTutorShop
                
                service.runBasic();
            }
        });       
    }
    /**
	 *
	 */
    public static void main(String args[]) 
	{
    	CTATBase.debug ("CTATFlashTutorShop","main ()");
    	
    	System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); // forces java.net.HttpURLConnection to accept "Via" header
    	
    	@SuppressWarnings("unused")
		CTATLink link = new CTATLink(new CTATDesktopFileManager ()); // run the CTATLink constructor; this is necessary to allow for decryption of the config file
    	
    	// Configure CTATLink according to config file
    	CTATLink.fManager.configureCTATLink(); 
    	    	
    	if (CTATFlashTutorShop.parseArgs (args)==false)
    	{
    		return;
    	}
    	
        if (CTATLink.dlContent==true)
        {
        	downloadContent (null,null,CTATLink.skipGlobalAssets,false,null);
        	return;
        }
        
        CTATBase.debug ("CTATFlashTutorShop","Starting thread ...");
        
        java.awt.EventQueue.invokeLater (new Runnable() 
        {
            public void run()
            {
            	CTATBase.debug ("CTATFlashTutorShop","run ()");
            	
            	// So far all we've done is configuration which filled
            	// a number of public static objects
            	
                CTATFlashTutorShop service=new CTATFlashTutorShop ();
                
                // Execute the basic thread located in CTATBaseTutorShop
                
                service.runBasic();
            }
        });        
    }
}
