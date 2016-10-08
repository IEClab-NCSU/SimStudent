/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2014-04-08 12:20:44 -0400 (Tue, 08 Apr 2014) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATLink.java,v 1.22 2012/10/08 22:41:10 kjeffries Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATLink.java,v $
 Revision 1.22  2012/10/08 22:41:10  kjeffries
 quick fix, added no-arg constructor to avoid breaking other code

 Revision 1.21  2012/10/05 15:06:40  vvelsen
 Added a new subclass of the local tutorshop that can work completely offline. Also added a patch for the situation where the applet version of the file manager was assigned as the default manager

 Revision 1.20  2012/10/03 17:50:26  sewall
 First working version with CTATAppletFileManager.

 Revision 1.19  2012/09/14 13:05:48  vvelsen
 Started migrating the curriculum.xml to Octav's new format

 Revision 1.18  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.17  2012/09/07 18:15:05  vvelsen
 Quick checkin so that Jonathan can test his CL bridge code

 Revision 1.16  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.15  2012/08/17 22:23:19  kjeffries
 commit after Alvaro's merge -- should compile now but more work needs to be done to ensure that nothing important got lost in the merge

 Revision 1.14  2012/08/17 17:50:32  alvaro
 merging versions

 Revision 1.10  2012/06/12 19:31:19  kjeffries
 added static fields 'initialized' (to be set true after the config file is read) and 'cache'

 Revision 1.9  2012/04/16 16:37:35  vvelsen
 Added a SwingWorker class that can be accessed through CTATLink. It's pretty much the only way in which random code can access a progress bar and update it the way Swing wants you to do it

 Revision 1.8  2012/04/13 19:44:38  vvelsen
 Started adding some proper threading and visualization tools for our download manager

 Revision 1.7  2012/04/10 15:14:59  vvelsen
 Refined a bunch of classes that take care of file management and file downloading. We can now generate, save and compare file CRCs so that we can verify downloads, etc

 Revision 1.6  2012/03/16 15:15:28  vvelsen
 Small fixes here and there to the monitor, link and other support classes. Mostly reformatting work.

 Revision 1.5  2012/02/24 20:53:35  vvelsen
 Added a bunch of small tools and refinements to file management.

 Revision 1.4  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.24  2011/11/28 06:33:43  sewall
 1) New args to SingleSessionLauncher constr. 2) Put debug-trace switch in config panel. 3) Add local path to brd files stored locally. 4) Add run.jar and launcher TutorShopUSB.

 Revision 1.23  2011/11/10 23:50:45  sewall
 1. maxCachedFiles now a CTATLink parameter. 2. no longer delete files from disk cache. 3. hash map lookup for cache. 4. ignore rails timestamps in file names. 5. kill old instance by monitor port request.

 Revision 1.22  2011/09/30 20:25:56  sewall
 Add FlashVars dataset_name, dataset_level_type1, dataset_level_name1, problem_name SessionLog

 Revision 1.21  2011/09/29 15:52:16  sewall
 Now replace browser page when user exits server via Swing gui.

 Revision 1.20  2011/09/29 04:10:24  sewall
 Port changes from AuthoringTools/java/source/, where this code is now maintained.

 Revision 1.19  2011/09/26 17:00:15  sewall
 Refactoring: add CTATLink.getAdminPassword(), use CTATHTTPHandler.sendResponse() more often. Ajax: add 'getpush.cgi' entry to CTATHTTPHandler.doPost().

 Revision 1.18  2011/08/26 21:04:45  kjeffries
 some small cleanups

 Revision 1.17  2011/08/12 20:43:57  kjeffries
 Added printDebugMessages boolean. Files in installable package are no longer encrypted.

 Revision 1.16  2011/08/05 21:06:39  kjeffries
 Added boolean for use of local tutoring service

 Revision 1.15  2011/07/22 20:51:27  kjeffries
 Added allowWriting field, corrected admin password filename

 Revision 1.14  2011/07/18 15:06:50  kjeffries
 Added static variables indicating whether the local BRDs and problem_set.xml's are encrypted.

 Revision 1.13  2011/07/07 20:58:01  kjeffries
 Added remoteHost static field indicating the name of the remote server.

 Revision 1.12  2011/06/24 19:41:36  kjeffries
 Added comments describing use of config file.

 Revision 1.11  2011/06/08 20:45:49  kjeffries
 Encryption key is now stored directly as a string rather than in a file.

 Revision 1.10  2011/05/27 20:53:51  kjeffries
 Added fields pertaining to admin mode and specified location of key file used in encryption/decryption.

 Revision 1.9  2011/05/17 15:23:50  kjeffries
 added static variable hostName

 Revision 1.8  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central registry. Please see CTATLink for more information.

 Revision 1.7  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 Revision 1.6  2011/02/17 17:43:47  vvelsen
 Small cleanups to move regularly used strings into CTATLink

 Revision 1.5  2011/02/16 16:42:10  vvelsen
 Added more refinement to the update class that will work either in the background or at a time when nobody is doing tutoring.

 Revision 1.4  2011/02/15 20:38:35  vvelsen
 Fixed a bug in the url fetch class that would throw errors left and right because it wasn't using the proper java classes. Did some cleaning on debugging as well.

 Revision 1.3  2011/02/15 15:20:42  vvelsen
 Added proper datashop logging to disk through http in the built-in web server. Added encryption code for logfiles, text based streams, etc. Added a first update scheduler that can be used to both retrieve new versions of the application as well as new content and student info.

 Revision 1.2  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 Revision 1.1  2011/02/08 14:42:54  vvelsen
 More features added. The server now properly generates html that can load swf files. SWF files loaded through that html connect back to the built-in tutoring service but the code still needs a lot of work to deal with brd path checking and Flash security handling. Web server log files are now properly generated.

 $RCSfile: CTATLink.java,v $ 
 $Revision: 20116 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATLink.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat;

//import java.io.File;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.trace;

/**
 * We provide file management abstraction through this file, which acts as a
 * static registry in which all platform and configuration specific information
 * can be found. The values specified here are defaults, which are normally
 * overwritten at program startup according to the config file. Run with
 * -config option to create a config file.
 * 
 */

/* TODO Any changes to the fields of this class should be reflected in the
 * saveConfigData() and configureCTATLink() methods of CTATDesktopFileManager  
 */

public class CTATLink extends CTATBase
{
	/** Document to write when TutorShop server is exiting. */
	public static final String SO_LONG = 
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
	
	//>---------------------------------------------------------
	
	// One of "DEFAULT" , "LOCAL" , "OFFLINE" , "CS2N"
	
	public static String handlerConfig="BASIC";
	
	//>---------------------------------------------------------
	
	public static int DEPLOYFLASH=0;
	public static int DEPLOYHTML5=1;
	
	public static int deployType=DEPLOYFLASH;
	
	//>---------------------------------------------------------	
	
	public static int MOBILEAPIDISABLED=0;
	public static int MOBILEAPION=1;
	public static int MOBILEAPIAUTO=2;
	
	public static int deployMobileAPI=MOBILEAPIDISABLED;
	
	//>---------------------------------------------------------	
	
	public static Boolean reuseSWF=false;
	
	//>---------------------------------------------------------
	
	public static String lastError=""; 
	
	public static boolean initialized = false; // set to true when this class's static fields have been initialized (e.g. from config file)
	
	public static boolean noNetwork=false;
	
	public static String	mountedFileSystem="DISK";
	
	public static int		debugLine=0;
	
	/// The htdocs path is currently configured for Desktop usage. I'm assuming
	/// that for the Android platform we would need something like: htdocs="/SDCARD/htdocs"
	public static String	etc="./etc/";
	public static String	logdir="./logs/";
	public static String	htdocs="./htdocs/";
	public static String	configFilePath = "./etc/config.data";	
	public static String 	profileDir="./";
	public static String	adminPasswordFilename = "./etc/password.txt";
	public static String	urlPassThrough="";
	public static boolean	adminLogin = false; // has an administrator password been entered?
	
	public static String    remoteHost = ""; // empty string (or "local") means run local tutors (from USB stick). anything else means use proxy mode to open browser to that server's main page initially	
	public static String	hostName = "localhost";
	public static int		wwwPort=8080;
	public static int		tsPort=4000;
	public static int		tsMonitorPort=4001;
	public static int 		remoteTutoringService=0;
	
	public static String	appMode="normal";  // normal, install, diagnostics, safemode, update, admin
	public static String	appState="normal"; // normal, updating, disabled
	public static String	keyString="824C64D824A9CA38E09767FDA395240C";	
	public static String	datashopURL="http://augustus.pslc.cs.cmu.edu/log/server/sandboxlogger.php";
	public static String	datashopFile="./logs/datashop.log";
	public static String	datasetName="FIRE Preview";
	public static String	updateURL="http://ctat.pact.cs.cmu.edu/updates";
	public static boolean   requirePredefinedUserid = false;
	
	public static boolean dlContent = false;
	public static boolean skipGlobalAssets = false;

	public static String	FIREClass="";
	
	public static String crossDomainPolicy = "<?xml version=\"1.0\"?>\n" +
	"<!DOCTYPE cross-domain-policy SYSTEM \"http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd\">\n" + 
	"<cross-domain-policy>\n" + 
	"<allow-access-from domain=\"*\" to-ports=\"*\" />\n" +
	"</cross-domain-policy>\0";
		
	public static boolean problemSetXMLsAreEncrypted = false; //true; // use command line option to indicate not encrypted
	public static boolean BRDsAreEncrypted = false; //true; // use command line option to indicate not encrypted
	
	public static boolean allowWriting = true; // is the program allowed to write to disk? should be false when run from CD-ROM; set via config file
	public static boolean inMemoryOnly = false; // Dependent on allowWriting above. If that variable is false then this parameter can be set to true to manage the user progress database exclusively in memory
	public static boolean useLocalTutoringService = true; // use local TS by default; if false, remoteHost must be non-empty
	
	/** Turn on trace output in {@link CTATBase#debug(String)}. */
	public static boolean printDebugMessages = trace.getDebugCode("ll");
	//public static boolean printDebugMessages = true;

	/** Whether to generate navigation buttons on the tutor frame. */
	public static boolean showNavButtons = true;

	// Platform specific drivers. We only want one instance per driver
	// so that there are no other classes accessing the environment
	// in different ways
	
	public static CTATCryptoUtils crypto=null;	
	public static CTATFileManager fManager=null;
	public static CTATLogSnoopInterface logSnooper=null;
	/** Time in milliseconds between polls from the pushFrame on the UI. */
	public static long pushPollingInterval = 2000;
	
	/** Maximum number of files to cache in memory. */
	public static int maxCachedFiles = 1000;
	
	public static CTATContentCache cache = null; /* This cache should be associated with the main content-cache directory on disk.
	                                                Check here before making a new instance of CTATContentCache with that directory.
	                                                It's not necessary to have only one instance but it's best not to have too many. */
	
	public static ProblemSetEndHandler problemSetEndHandler = null;
	public static ProblemEndHandler problemEndHandler = null;
	public static UserProgressDatabase userProgress=null;
	public static String userID="offline_user";
	
	public static CTATDirectoryTXT fDirectoryTXT=null;
	
	// This shouldn't be here but was too lazy to make it an argument and
	// trace all the methods that should have this
	public static Boolean generateHTMLIndex=false;
	
	public CTATLink() // This constructor is a quick fix to avoid breaking other code. -- Kevin Jeffries, 2012-10-08 
	{
		this(new CTATDesktopFileManager());
	}
	/**
	 *
	 */
    public CTATLink (CTATFileManager aManager)
    {
		setClassName ("CTATLink");
		CTATLink.debug ("CTATLink","CTATLink ()");	    	
		
		crypto=new CTATCryptoUtils ();
		
		// Replace the code below with a version that works on Android
   	
		fManager=aManager;
		
		if (fManager==null)
			fManager=new CTATDesktopFileManager ();
   	
		CTATLink.debug ("CTATLink","CTATLink () Done");
    }

    /**
     * @return {@link #getPathPrefix()}+"htdocs/cache"
     */
	public static String getCacheDir() {
		return getPathPrefix()+"htdocs/cache";
	}

	/**
	 * Return the root directory for the working files.  If {@link #mountedFileSystem}
	 * is null or "DISK", return the default "./". Else return {@link #mountedFileSystem}
	 * with a trailing "?" or "/" if a .jar or not, respectively.  
     * @return default "./" 
     */
	private static String getPathPrefix() {
		if("DISK".equalsIgnoreCase(mountedFileSystem))
			return "./";
		if(mountedFileSystem == null)
			return "./";
		if(mountedFileSystem.toLowerCase().endsWith(".jar"))
			return mountedFileSystem+"?";
		return mountedFileSystem+"/";
	}
    
    /**
     * We assume that all the paths listed above are well-formed, meaning
     * that they are all relative and start with ./ This method will
     * re-map all paths to now start with the name of the provided jar
     * followed by a ? to make it a fully qualified URI
     */
    public static void mountJar (String aJarFile)
    {
    	CTATLink.debug ("CTATLink","mountJar ("+aJarFile+")");
    	
    	CTATLink.mountedFileSystem=aJarFile;
    	
    	processMount ();
    	
    	CTATDesktopFileManager checker=new CTATDesktopFileManager ();
    	checker.jarListContents (CTATLink.mountedFileSystem);
    	
    	CTATLink.debug ("CTATLink","mountJar ("+aJarFile+") Done");
    }
    /**
     * 
     */
    public static void processMount ()
    {
    	CTATLink.debug("CTATLink","processMount ()");
    	
    	if (CTATLink.mountedFileSystem.equalsIgnoreCase("DISK")==false)
    	{
    		CTATLink.debug("CTATLink","File system mount point is now "+mountedFileSystem+", remapping ...");

    		String sep = (mountedFileSystem.toLowerCase().endsWith(".jar") ? "?" : "/");
    		
    		if (CTATLink.etc.indexOf("./")!=-1)
    			CTATLink.etc=CTATLink.etc.replaceFirst ("./",CTATLink.mountedFileSystem+sep);
    		else
    			CTATLink.debug("CTATLink","Error: etc does not contain ./, instead:" + CTATLink.etc);
    		
    		if (CTATLink.logdir.indexOf("./")!=-1)
    			CTATLink.logdir=CTATLink.logdir.replaceFirst ("./",CTATLink.mountedFileSystem+sep);
    		else
    			CTATLink.debug("CTATLink","Error: logdir does not contain ./, instead:" + CTATLink.logdir);    		
    		
    		if (CTATLink.htdocs.indexOf("./")!=-1)
    			CTATLink.htdocs=CTATLink.htdocs.replaceFirst ("./",CTATLink.mountedFileSystem+sep);
    		else
    			CTATLink.debug("CTATLink","Error: htdocs does not contain ./, instead:" + CTATLink.htdocs);    		
    		
    		if (CTATLink.configFilePath.indexOf("./")!=-1)
    			CTATLink.configFilePath=CTATLink.configFilePath.replaceFirst ("./",CTATLink.mountedFileSystem+sep);
    		else
    			CTATLink.debug("CTATLink","Error: configFilePath does not contain ./, instead:" + CTATLink.configFilePath);    		
    		
    		if (CTATLink.profileDir.indexOf("./")!=-1)
    			CTATLink.profileDir=CTATLink.profileDir.replaceFirst ("./",CTATLink.mountedFileSystem+sep);
    		else
    			CTATLink.debug("CTATLink","Error: profileDir does not contain ./, instead:" + CTATLink.profileDir);    		
    		
    		if (CTATLink.adminPasswordFilename.indexOf("./")!=-1)
    			CTATLink.adminPasswordFilename=CTATLink.adminPasswordFilename.replaceFirst ("./",CTATLink.mountedFileSystem+sep);
    		else
    			CTATLink.debug("CTATLink","Error: adminPasswordFilename does not contain ./, instead:" + CTATLink.adminPasswordFilename);    		
    	}	
    }
    /**
     * Prompt for administrator password using {@link JOptionPane}.
     * @return true if user entered correct admin password;
     *         false on error or user pressed cancel
     */
	public static boolean getAdminPassword() 
	{
    	String password = (new CTATDesktopFileManager()).getContentsEncrypted(CTATLink.adminPasswordFilename);
    	
    	if(password == null)
    	{
    		JOptionPane.showMessageDialog(null, "Error reading password file.");
    		return false;
    	}
    	String input;
    	do 
    	{
    		input = JOptionPane.showInputDialog(null, "Please enter the administator password.");
    	} while(input != null && !input.equals(password));
    	
		return (input != null);  // input == null if user pressed Cancel
	}

	/**
	 * Initialize the fields from a file image.
	 * @param decrypted contents of the config file, decrypted if necessary
	 * @return true if the minimum no. of fields is found
	 */
	public static boolean parse(String decrypted) 
	{
		CTATLink.debug("CTATLink","parse ()");
		
		if (CTATLink.initialized==true)
		{
			CTATLink.debug("CTATLink","We've already been called, aborting");
			return (false);
		}
		
		if (decrypted==null)
		{
			CTATLink.debug("CTATLink","decrypted==null (default values are to be used)");
			
			CTATLink.initialized = true; // default values are to be used
			return false;
		}
					
		// Read the string from the decrypted file into the fields of CTATLink
		String[] fields = decrypted.split("\t");
					
		showStringArray (fields);
		
		if(fields.length < 11)
		{
			CTATLink.initialized = true; // default values are to be used
			return false;
		}
		
		fields[fields.length-1] = fields[fields.length-1].trim();  // strip trailing CRLF
		
		CTATLink.htdocs = fields[0];
		CTATLink.hostName = fields[1];
		try { CTATLink.wwwPort = Integer.valueOf(fields[2]); } catch (NumberFormatException e) { /* invalid data; keep default */ }
		try { CTATLink.tsPort = Integer.valueOf(fields[3]); } catch (NumberFormatException e) { /* invalid data; keep default */ }
		try { CTATLink.tsMonitorPort = Integer.valueOf(fields[4]); } catch (NumberFormatException e) { /* invalid data; keep default */ }
		CTATLink.remoteHost = fields[5];
		CTATLink.etc = fields[6];
		CTATLink.datashopURL = fields[7];
		CTATLink.datashopFile = fields[8];
		CTATLink.crossDomainPolicy = fields[9];
		CTATLink.adminPasswordFilename = fields[10];
		
		CTATLink.processMount();
		
		CTATLink.debug("CTATLink","Allow writing: " + fields[11]);
		
		if(fields[11].equalsIgnoreCase("false"))
			CTATLink.allowWriting = false;
		else
			CTATLink.allowWriting = true;		
		
		if(fields.length >= 13 && fields[12].equals("false"))
			CTATLink.useLocalTutoringService = false;
		else
			CTATLink.useLocalTutoringService = true;

		if(fields.length >= 14)
		{
			try { CTATLink.maxCachedFiles = Integer.valueOf(fields[13]); } catch (NumberFormatException e) { /* invalid data; keep default */ }
		}

		if(fields.length >= 15 && fields[14].equals("false"))
			CTATLink.printDebugMessages = false;
		else
			CTATLink.printDebugMessages = true;
				
		if(fields.length >= 16)
			CTATLink.datasetName = fields[15];
		
		if(fields.length >= 17 && fields[16].equals("false"))
			CTATLink.noNetwork = false;
		else
			CTATLink.noNetwork = true;

		if(fields.length >= 18)
			CTATLink.logdir = fields[17];
		
		if(fields.length >= 19 && fields[18].equals("false"))
			CTATLink.showNavButtons = false;
		else
			CTATLink.showNavButtons = true;
				
		if(fields.length > 20)
		{
			CTATLink.debug("CTATLink","fields[20]:" + fields[20]);
			
			if (fields[20].equals("flash")==true)
			{
				CTATLink.debug ("CTATLink","Configuring local tutorshop for Flash tutors");
				CTATLink.deployType=CTATLink.DEPLOYFLASH;
			}
			
			if (fields[20].equals("html5")==true)
			{
				CTATLink.debug ("CTATLink","Configuring local tutorshop for HTML5 tutors");
				CTATLink.deployType=CTATLink.DEPLOYHTML5;
			}			
		}
		
		if(fields.length > 21)
		{
			CTATLink.handlerConfig=fields[21];
			
			CTATLink.debug ("CTATLink","Installing handler config: " + CTATLink.handlerConfig);
		}
		
		if(fields.length > 22)
		{			
			CTATLink.debug("CTATLink","fields[23]:" + fields[23]);
			
			if (fields[23].equals("disabled")==true)
			{
				CTATLink.deployMobileAPI=CTATLink.MOBILEAPIDISABLED;
			}
			
			if (fields[23].equals("on")==true)
			{
				CTATLink.deployMobileAPI=CTATLink.MOBILEAPION;			
			}
			
			if (fields[23].equals("auto")==true)
			{
				CTATLink.deployMobileAPI=CTATLink.MOBILEAPIAUTO;			
			}
		}

		CTATLink.initialized = true;

		return true;
	}
	/**
	 * 
	 * @param stringList
	 */
	public static void showStringArray (String[] stringList)
	{
		CTATLink.debug("CTATLink","showStringArray (" + stringList.length + ")");
		
		for (int i=0;i<stringList.length;i++)
		{
			CTATLink.debug("CTATLink","field ["+i+"]:"+ stringList [i]);
		}
	}
}
