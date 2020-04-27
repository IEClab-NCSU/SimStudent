/*
 * Created on Sep 7, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.RemoteProxy;
import pact.CommWidgets.StudentInterfaceWrapper;
import pact.CommWidgets.TutorWrapper;
import pact.CommWidgets.UniversalToolProxy;
import pact.CommWidgets.UniversalToolProxyForLisp;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTab;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.JavascriptBridge.JSProxy;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.SocketProxy.SocketToolProxy;
import edu.cmu.pact.TutoringService.Collaborators;
import edu.cmu.pact.TutoringService.Collaborators.NotReadyException;
import edu.cmu.pact.TutoringService.LauncherServer;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.TutoringService.TSLogInfo.Session;
import edu.cmu.pact.TutoringService.TransactionInfo.Single;
import edu.cmu.pact.Utilities.CTAT_Controller;
//import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.StudentInterfacePanel;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.view.CtatFrame;
import edu.cmu.pact.ctatview.CtatMenuBar;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.StudentAvatarDesigner;

public class SingleSessionLauncher 
{
	/** Command-line or applet parameter specifying codes for {@link trace#addDebugCodes(String)} . */
	public static final String DEBUG_CODES = "debugCodes";

	/** Command-line or applet parameter directing use of Javascript bridge to student interface. */
	public static final String USE_JS_BRIDGE = "useJsBridge";

	public static final String USE_HTTP = "useHTTP";
	private BR_Controller controller;    
    private TSLauncherServer launcher = null;    
    private String projectDir = null;    
    private TutorWrapper wrapper;
    private Applet applet = null;
    private CTATTabManager tabManager;
//    private final int portOffset;

    
    /** Session identifier, set when running under the tutoring service. */
    private String sessionId = null;
    
    /**
	 * @return the {@link #sessionId}
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId new value for {@link #sessionId}
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	// Set a flag to have Sim. St. validation test
    private boolean ssRunValidation = false;    
    private boolean hideCTAT;

	private CTAT_Launcher ctatLauncher;
    
    
    private void setProjectDir(String pDir)
    {
    	
    }
    
    public String getProjectDir()
    {
    	return projectDir;
    }
    
    
	private String packageName = "";
    
    /**
     * Entry point for running the tools independently of a particular student
     * interface. If the sole argument is "-version", calls
     * {@link VersionInformation#main(String[])} and exits.
     * @param argv see {@link #parseArgv(String[])}
     */
    /*
    public static void main(String argv[]) 
    {
    	if (argv.length == 1 && "-version".equalsIgnoreCase(argv[0])) 
    	{
    		VersionInformation.main(argv);
    		System.out.printf("includes %-5s: %b\n", "Jess" , VersionInformation.includesJess());
    		System.out.printf("includes %-5s: %b\n", "SimSt" , VersionInformation.includesSimSt());
    		System.out.printf("includes %-5s: %b\n", "CL" , VersionInformation.includesCL());
    		System.exit(2);
    	}

    	JComponent studentInterface =
    			TutorController.createStudentInterface(System.getProperty(Logger.STUDENT_INTERFACE_PROPERTY));
    	if(studentInterface != null)
    		new SingleSessionLauncher(argv).launch(studentInterface);
    	else
    		new SingleSessionLauncher(argv).launch();
    }
    */
    
    /**
     * This method gives, e.g., the student interfaces access to the
     * {@link edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller}.
     * 
     * @return Returns the controller.
     */
    public BR_Controller getController() {
       return this.controller;
    }
    
    public TSLauncherServer getLauncherServer() {
    	return this.launcher;
    }
    
    public boolean inTutoringServiceMode()
    {
    	//return this.launcher != null;
    	return (this.launcher instanceof LauncherServer);
    }

    /**
     * @return true if {@link #applet} != null
     */
     public boolean inAppletMode()
     {
    	return (applet != null);
     }
 
    /**
     * @return {@link #applet}
     */
    public Applet getApplet() {
    	return applet;
    }

    public SingleSessionLauncher(boolean showCtatWindow) {
        this(null, showCtatWindow);
    }

    public SingleSessionLauncher() {
        this(true);
    }
    
    public SingleSessionLauncher(String[] argv, CTATTabManager tabManager,
    		CTAT_Launcher ctatLauncher, CTATTab tab) {
    	this(null, null, argv, true, null, null, null, tabManager, 
    			ctatLauncher, tab);
    }
    
    public SingleSessionLauncher(String[] argv) {
        this(argv, true);
    }

    public SingleSessionLauncher(String[] argv, boolean showCtatWindow)
    {
    	this(null, null, argv, showCtatWindow, null, null, null);
    }
    
    public SingleSessionLauncher(Socket sock, BufferedReader br, String[] argv, 
    		boolean showCtatWindow, TSLauncherServer ls, MessageObject setPrefsMsg,
    		Applet applet) {
    			this(sock, br, argv, showCtatWindow, ls, setPrefsMsg, applet,
    					null, null, null);
    		}

    /* CollinL:  On launch this appears to be the primary startup method.  It in turn
     * 	Sets up the basic features of the system and, depending upon the settings 
     * 	will either cause a controller display to come of showCtatWindow is true.
     */
    public SingleSessionLauncher(Socket sock, BufferedReader br, String[] argv, 
    		boolean showCtatWindow, TSLauncherServer ls, MessageObject setPrefsMsg,
    		Applet applet, CTATTabManager tabManager,
    		CTAT_Launcher ctatLauncher, CTATTab containerTab) {
    	if(trace.getDebugCode("mg"))
    		trace.out("mg", String.format("SingleSessionLauncher(sock %s, br %s, argv.len %d, show %b,\n"+
    				"  ls %s, setPrefs %s, applet %s, tabMgr %s, ctatLauncher %s, tab %s)",
    				sock, trace.nh(br), argv.length, showCtatWindow, trace.nh(ls),
    				(setPrefsMsg == null ? null : setPrefsMsg.summary()), trace.nh(applet),
    				trace.nh(tabManager), trace.nh(ctatLauncher), trace.nh(containerTab)));
    	
    	this.ctatLauncher = ctatLauncher;
    	this.applet = applet;
    	Utils.setRuntime((inAppletMode() || (ctatLauncher == null)) && (!showCtatWindow));

    	int tabNumber = 1;
    	if(containerTab != null) {
    		containerTab.setLauncher(this);
    		tabNumber = containerTab.getTabNumber();
    	}
    	// set the port number: 1502/default for tab 1, 1501 for tab 2, 1500 for tab 3, ...
    	// so calculate an offset to subtract from the port number for this
    	// individual launcher
//    	this.portOffset = tabNumber - 1;
    	if(ls == null && ctatLauncher != null)
    		ls = ctatLauncher.getAuthorLauncherServer();  // needed for preferences, properties, etc.
    	this.launcher = ls;
//    	if(ctatLauncher == null)
//    		ctatLauncher = new AuthorLauncher();
    	
        if(!inTutoringServiceMode() && !inAppletMode()) {
	    	// Fixes a bug in the jfilechooser according to this:
	        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4711700
	    	System.setProperty("swing.disableFileChooserSpeedFix", "true");
        	
        	installNativeLookAndFeel();
        }
        
        String noTutorInterface = null;
        
        if (!inAppletMode())
        	noTutorInterface = System.getProperty("noCtatWindow");
        else
        	noTutorInterface = "true";
        
        if (noTutorInterface != null) {
            trace.out("No CTAT Window");
            showCtatWindow = false;
        }
        
        /* end move up */

        /* Collinl: This code will start the BR_Controller which is the underlying 
         * 	service of the system.  The prior work was setup but the controller 
         * 	object, so far as I can tell is what will begin the root behavior 
         * 	processes. 
         */
        if (argv != null) 
        	controller = new BR_Controller(showCtatWindow, PreCheckReducedMode(argv),
        			new Boolean(inTutoringServiceMode()), this, tabNumber);
        else
        	controller = new BR_Controller(showCtatWindow, false,
        			new Boolean(inTutoringServiceMode()), this, tabNumber);

        if(!Utils.isRuntime())
        	AuthorUI.setJCommWidgetController(controller);
//        controller.setConstructedByCTATLauncher(true);
		controller.setHomeDir(getPackageNameAsPath(this));
		
		UniversalToolProxy utp = null;
		if (!(argv != null && Arrays.asList(argv).contains("-"+USE_JS_BRIDGE))) {
			utp = (VersionInformation.includesCL() ?
	        		new UniversalToolProxyForLisp() : new UniversalToolProxy());
	        controller.setUniversalToolProxy(utp);
	        utp.init(controller);
		}
		
    	if (trace.getDebugCode("ssl")) trace.out("ssl", "In the SingleSessionsLauncher constructor, the socket (sock) is " + (sock == null ? "null" : "not null"));
		
    	// sewall 2011/2/9: must call this before parseArgv
    	// 2014/5/26: when constructor is called from CTAT_Launcher (4 args), it always passed a null socket.
    	//			  socket doesn't seem to be initialized anywhere else in SingleSessionLauncher
        if (sock != null)
        {
        	controller.setRemoteProxy(new SocketProxy(sock, SocketProxy.argvToMsgFormat(argv)));
        	((SocketProxy) controller.getRemoteProxy()).setController(controller, br);
        }
        
        // Thu Oct 27 16:23:13 2005:: Noboru
        if (argv != null) {
            trace.out("br", "going to parse Argv...");
            parseArgv(argv);
        }
        
        if (!Utils.isRuntime())
        	utp.setStudentInterfaceConnectionStatus(null);  // update connection type in CtatModePanel
      
        //ko - these come from BRPanel's initComponents (called from constructor)
        CtatMenuBar handler = null;
     
        if (!Utils.isRuntime()) {
        	//handler = new CtatMenuBar(ctatLauncher); // moved to CTAT_Launcher

        	controller.getProblemModel().setCaseInsensitive(true);
        	controller.getProblemModel().setAllowToolMode(true);
        	controller.getProblemModel().setMaxStudents(1);
        	controller.getSolutionState().resetUserVisitedEdges();


            handler = ctatLauncher.getCtatMenuBar();
            handler.applyPreferences();
        }

        controller.initializeInterfaceActions_NoneState_Tutor();
        
        //end stuff from BR_Panel
        
        controller.init(null);

        //controller.loadJGraph(); // replaced by controller initialization
        controller.loadControlFromSystemProperties();

        if (controller.getRemoteProxy() != null) {
        	if (inTutoringServiceMode())
        		controller.getRemoteProxy().setupLogServlet(setPrefsMsg);
        	if (trace.getDebugCode("sp")) trace.out("sp", "SingleSessionLauncher starting socket proxy");
            controller.getRemoteProxy().start();
        }

        /*
        if (showCtatWindow) {
            controller.dockWindowsNow();
        }
        */

        if(!Utils.isRuntime())  // ADDED: at end of initialization, subscribe to open-file requests
        	AuthorUI.addOpenFileListener(controller);
    }

    /**
     * Call {@link pact.CommWidgets.JCommWidget#setController(TutorController)}
     * using reflection, so that it doesn't generate an error in Android.
     * @param controller
     */
    private void setJCommWidgetController(TutorController controller) {
    	String jcwName = "pact.CommWidgets.JCommWidget";
    	String scName = "setController";
    	Class jcwClass = null;
    	try {
    		jcwClass = Class.forName(jcwName);
    		Class[] argSignature = {TutorController.class};
    		Method scMethod = jcwClass.getMethod(scName, argSignature);
    		Object[] args = {controller};
    		scMethod.invoke(null, args);   // null instance ok: method is static
    	} catch(Exception e) {
    		trace.err("Error trying to invoke class "+jcwName+" method "+scName+": "+e+";\n  cause "+e.getCause());
    		return;
    	}
	}

	public static void installNativeLookAndFeel() {
        String nativeLF = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(nativeLF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // The launcher
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // 
    public void launch() {
        controller.loadControlFromSystemProperties();
        controller.loadBRDFromSystemProperties();
    }

    /**
     * Launcher for CL2006-compatible student interfaces.
     * @param wrapper StudentInterfaceWrapper implementation
     * @param clTutorPanel a tutor panel created from the CL_TutorTemplate
     */
    public void launchCL(StudentInterfaceWrapper wrapper, JPanel clTutorPanel) {
        installNativeLookAndFeel();
        wrapper.getWrapperSupport().setController(controller);
        getController().setStudentInterface(wrapper);
        continueLaunch(wrapper, clTutorPanel);
    }
    
    /**
     * Launcher for native CTAT student interfaces.
     * @param tutorPanel a tutor panel created from TutorTemplate
     */
    public void launch(JComponent tutorPanel) {
    	  // nbarba 01/16/2014: all SimStudent related code now reside in MissController so all calls to SimStudent fields should be made through getMissController().
        installNativeLookAndFeel();
       
        wrapper = getMissController().createWrapper(getMissController().isSimStPleOn(), getMissController().isSsContest(), controller);
       
        if (wrapper == null)
        	wrapper = new TutorWrapper(controller);
        controller.setStudentInterface(wrapper);
        continueLaunch(wrapper, tutorPanel);
        
    
        if (getMissController().isSimStPleOn()) {
            controller.setModeSimStAndDestroyProdRules();
            controller.getMissController().requestEnterNewProblem();
            getMissController().setTitle(wrapper.getActiveWindow());
        } 
        else if(getMissController().isSsContest()) {
            controller.setModeSimStAndDestroyProdRules();
        } else {
        	
            if (controller.getOptions().getShowLoginWindow() || 
                    controller.getOptions().getConnectToLispAtStartup())
                wrapper.setVisible(false);
            else wrapper.setVisible(true);
            
        }
        
        
    }
    
    
   
    
    
    
    
    
    /**
     * 
     * @param tutorPanel
     */
    public void launchSimStPLE(JComponent tutorPanel) {
        if (trace.getDebugCode("miss")) trace.out("miss", "Launching SimSt PLE...");
        installNativeLookAndFeel();
        wrapper = getMissController().createWrapper(true, false, controller);
        controller.setStudentInterface(wrapper);
        continueLaunch(wrapper, tutorPanel);
        // wrapper.setVisible(true);
        controller.setModeSimStAndDestroyProdRules();
        controller.getMissController().requestEnterNewProblem();
    }

    /**
     * Common code for launchers called from student interfaces.
     * @param wrapper StudentInterfaceWrapper implementation
     * @param tutorPanel panel built from a TutorTemplate or CL_TutorTemplate
     */
    /**
     * @param wrapper
     * @param tutorPanel
     */
    private void continueLaunch(StudentInterfaceWrapper wrapper, JComponent tutorPanel) {
        CTAT_Options ctatOptions = wrapper.setTutorPanel(tutorPanel);
        if (trace.getDebugCode("inter")) trace.out("inter", "options = " + ctatOptions); 
        
        if (tutorPanel instanceof StudentInterfacePanel){
        	((StudentInterfacePanel) tutorPanel).setController(controller);
        	
        }
        
        if (ctatOptions == null) {
            ctatOptions = new CTAT_Options();
        }
        // Default options go here
        if (hideCTAT)
            ctatOptions.setShowBehaviorRecorder(false);

        
        if (trace.getDebugCode("inter")) trace.out("inter", "options = " + ctatOptions);
        if (ctatOptions != null)
            if (trace.getDebugCode("inter")) trace.out("inter", "options.showBR = "
                    + ctatOptions.getShowBehaviorRecorder());

        controller.setOptions(ctatOptions);

        if (!hideCTAT && getMissController() != null && getMissController().getSimSt() != null){
        	
        	if(getMissController().getSimSt().isSsCogTutorMode() || getMissController().getSimSt().isSsAplusCtrlCogTutorMode() || SimSt.getSimStName().length() > 0)
        	      wrapper.setVisible(true);        	 
        		
        }
        else
        	 wrapper.setVisible(true);
            
        if (controller.getDockedFrame() != null){
        	((CtatFrame)controller.getDockedFrame()).getCtatMenuBar().enableInterfaceMenus(false);
        	//((CtatFrame)controller.getDockedFrame()).setEnabled(false);
        }
     
        //30Nov2006: in order to run JUnitTests, we need to pass an absolute path (projectDir)
        //the default case (-ssProjectDir not passed) is like before, getPackageNameAsPath(tutorPanel).
        String projDir = getProjectDir();
        String homeDirPath = projDir != null ? projDir : getPackageNameAsPath(tutorPanel);
        //String homeDirPath = getPackageNameAsPath(tutorPanel);
        if (trace.getDebugCode("miss")) trace.out("miss", "homeDirPath = " + homeDirPath);
        setInterfaceHome(homeDirPath, tutorPanel);

        /** Override compile-time CTAT_Options in interface with runtime env */
        controller.loadControlFromSystemProperties();
        controller.loadBRDFromSystemProperties();
        	
       if (getMissController() != null && getMissController().getSimSt() != null
    		   && getMissController().getSimSt().getMissController().isSsRunValidation()) {
    	   controller.setModeSimStAndDestroyProdRules();
    	 
    	   getMissController().getSimSt().getMissController().runSimStValidationTest();
    	   
    	   System.exit(0);
       }
       else if (controller.getMissController().getSimSt().isSsBatchMode()) {
           if (trace.getDebugCode("miss")) trace.out("miss", "ssBatchMode ON...");
           controller.setModeSimStAndDestroyProdRules();   
           controller.getMissController().runSimStNoTutorInterface();
           System.exit(0);
       }
        
        // Fri Oct 28 18:15:13 2005 :: Noboru
        /*if (isSsRunValidation()) {
            controller.setModeSimStAndDestroyProdRules();
            //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
            runSimStValidationTest();
            System.exit(0);
            
        } else if (isSsBatchMode()) {
            if (trace.getDebugCode("miss")) trace.out("miss", "ssBatchMode ON...");
            controller.setModeSimStAndDestroyProdRules();
            //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
            runSimStInBatchMode();
            System.exit(0);
        /*} else if (isSsContest()) {
            trace.out("miss", "ssContest ON...");
            if (trace.getDebugCode("miss")) trace.out("miss", "ssContest ON...");
            controller.setModeSimStAndDestroyProdRules();
            //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
            runSimStInContestMode();
            System.exit(0);  */
//        } else if (isSsIlBatchMode()) {
//            trace.out("miss", "ssBatchMode ON...");
//            controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
//            runSimStInILBatchMode();
//            System.exit(0);
            
        /*} else if (isSsShuffleRunMode()) {
            if (trace.getDebugCode("miss")) trace.out("miss", "ssShuffleMode ON...");
            controller.setModeSimStAndDestroyProdRules();
            //            controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
            runSimStShuffleMode();
            System.exit(0);
            
        } else if (ssAnalysisOfFitnessWilkinsburg) {
            if (trace.getDebugCode("miss")) trace.out("miss", "ssAnalysisOfFitnessWilkinsburg is ON...");
            controller.setModeSimStAndDestroyProdRules();

            //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
            runSsAnalysisOfFitnessWilkinsburg();
            System.exit(0);
            
        } else if (ssLogStudentsLearning) {
            if (trace.getDebugCode("miss")) trace.out("miss", "ssAnalysisOfFitnessWilkinsburg is ON...");
            controller.setModeSimStAndDestroyProdRules();

            //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
            runSsLogStudentsLearning();
            System.exit(0);
            
        } else if (ssValidateStepsInBRD) {
            if (trace.getDebugCode("miss")) trace.out("miss", "ssValidateStepsInBRD is ON...");
            controller.setModeSimStAndDestroyProdRules();
            runSsValidateStepsInBRD();
            System.exit(0);
            
        } else {
            // Fri Jul  7 00:20:40 2006 :: Noboru
            // Some initialization procedure is necessary only when SimSt is 
            // running under an interactive mode (as opposed to the batch mode)
            
            //the following code would be needed in case it becomes possible for CTAT to start in SimSt mode:
            //if (we are in SimStudent mode)
            //            controller.initializeSimStInteractive();
        }*/
        //
        // **IMPORTANT NOTE** all of the Sim. St. related methods
        // listed above call System.exit() hence no methods would be
        // executed hereafter if one of these conditions holds
        //  
        if (!controller.getShowBehaviorRecorder() && controller.getCtatFrameController() != null)
        		controller.getCtatFrameController().getDockedFrame().setVisible(false);
        
        
    }

   

    /**
     * Tell all the places that need to know the interface's directory.
     * 
     * @param path
     *            relative path returned by {@link #getPackageNameAsPath(Object)}.
     */
    private void setInterfaceHome(String path, Object studentInterface) {
        if (controller != null) {
            PreferencesModel pm = controller.getPreferencesModel();
            if (pm != null)
            {
                pm.setStringValue(BR_Controller.PROBLEM_DIRECTORY, path);
            }
        }
        controller.setHomeDir(path);
        URL url = Utils.getURL(path, studentInterface);
//        URL url = studentInterface.getClass().getResource("/");
        if (trace.getDebugCode("inter"))
        	trace.out("inter", "setInterfaceHome() path "+path+", studentInterface "+studentInterface+", url "+url);
  
        if (url == null)
        	return;
        String urlStr = url.toString();
        String parentUrlStr = urlStr.substring(0, urlStr.length()-path.length());
        if(!inAppletMode())
        	System.setProperty(Utils.INTERFACE_HOME_PROPERTY, parentUrlStr);
        controller.getProperties().setProperty(Utils.INTERFACE_HOME_PROPERTY, parentUrlStr);
        
        /* Overrides the CTAT_Controller.PROBLEM_DIRECTORY set above if SimSt. is launched using WebStart */
    	if(controller.getMissController() != null && controller.getMissController().getSimSt() != null &&
    			controller.getMissController().getSimSt().isWebStartMode()) {
    		controller.getPreferencesModel().setStringValue(CTAT_Controller.PROBLEM_DIRECTORY, WebStartFileDownloader.SimStWebStartDir);
    		trace.out("miss", "WebStart Mode: CTAT_Controller.PROBLEM_DIRECTORY: " + BR_Controller.PROBLEM_DIRECTORY);
    	}
        if (trace.getDebugCode("inter"))
        	trace.out("inter", "setInterfaceHome() INTERFACE_HOME_PROPERTY now set to " + parentUrlStr);
    }

    public static String getPackageNameAsPath(Object obj) {

        if (obj.getClass().getPackage() == null)
            return "";

        String packageName = obj.getClass().getPackage().getName();
        StringTokenizer st = new StringTokenizer(packageName, ".");
        String dirName = "";
        String currDir = "";
        while (st.hasMoreTokens()) {
            currDir = st.nextToken();
            dirName += currDir + "/";
        }
        return dirName;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // A Simple Parser for Command Line Argument (CLA)
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    // Given a command line arguments, argv, return a list of
    // parameters for the argument at argv[keyIndex], which by
    // definition starts at argv[keyIndex+1]
    private String[] getArgvParameter(String argv[], int keyIndex) {

        ArrayList<String> paramList = new ArrayList<String>();
        for (int i = keyIndex + 1; i < argv.length; i++) {

        	if (argv[i].length() < 1)
        		continue;
            if (argv[i].charAt(0) == '-') {
                break;
            }

            paramList.add(argv[i]);

        }

        // Returns null if no parameter is present
        String[] getArgvParameter = null;
        if (!paramList.isEmpty()) {

            getArgvParameter = new String[paramList.size()];
            for (int i = 0; i < paramList.size(); i++) {
                getArgvParameter[i] = paramList.get(i);
            }
        }

        return getArgvParameter;
    }

    /**
     * Set a System property from a String of the form 
     *    "D<i>name</i>[=<i>value</i>]"
     * @param propName property name with possible value following 1st
     *              equals sign "="
     * @param value optional separate value; ignored if "=" is embedded
     *              in propName; else uses element 0
     */
    private void setSystemProperty(String propName, String[] values) {
    	
    	if (propName.length() < 2)
    		throw new IllegalArgumentException("missing system property");

    	String pName = propName.substring(1);
    	int eqIndex = pName.indexOf("=");
    	if (eqIndex > 0) {
    		if (!inAppletMode())
    			System.setProperty(pName.substring(0, eqIndex), pName.substring(eqIndex+1));
    					
    		controller.getProperties().setProperty(pName.substring(0, eqIndex),
    				pName.substring(eqIndex+1));
    		
    	} else if (eqIndex < 0) {          // property value is ""
    		String value = "";
    		if (values != null && values.length > 0)
    			value = values[0];
    		if (!inAppletMode())
    			System.setProperty(pName, value);
    		controller.getProperties().setProperty(pName, value);
    	} else                             // property name is ""
    		throw new IllegalArgumentException("bad system property");
    }

    // Thu Apr 20 16:35:11 2007 :: chc
    // Pre check for setup CtatMenuBar.
    private boolean PreCheckReducedMode(String[] argv) {

		for (int i = 0; i < argv.length; i++) {
			String key = argv[i];
			if(key == null)
				continue;
			if (key.length() > 0 && key.charAt(0) == '-') {
				try {
					String keyStem = key.substring(1);
					if (keyStem.startsWith("reduced"))
						return true;

				} catch (IllegalArgumentException iae) {
					trace.err("Error on command line argument " + i + " '"
							+ key + "': " + iae);
				}
			}
		}
		return false;
	}

    // Thu Oct 27 16:35:11 2005 :: Noboru
    // A simple but generic parser for the command line arguments (CLA)
    private void parseArgv(String[] argv) {
    	
        for (int i = 0; i < argv.length; i++) {
        	//System.out.println("argv[" + i + "] = " + argv[i]);
        	if(trace.getDebugCode("miss"))trace.out("miss","argv[" + i + "] = " + argv[i]);

            String key = argv[i];
            if(key.length() < 1)
            	continue;
            
            if (key.charAt(0) == '-') {
            	try {
            	    // Strip off the '-' at the beginning
                    String keyStem = key.substring(1);
                    
                    if(trace.getDebugCode("miss"))trace.out("miss", "keyStem=" + keyStem);
                    
                    // "parameters" that follow the key
                    String[] parameter = getArgvParameter(argv, i);

                    // If you need to add more switches, simply follow the
                    // convention below. Try to put just a single method
                    // call for each switch

                    if (keyStem.startsWith("D")) {
                    	setSystemProperty(keyStem, parameter);

                    } else if (keyStem.equalsIgnoreCase("useOldExampleTracer")) {
                        setUseNewExampleTracer(false);
                        
                    } 
                    /*else if(keyStem.equalsIgnoreCase("ssMetaTutorMode")){
                    	setSsMetaTutorMode(true);
                    
                    } else if(keyStem.equalsIgnoreCase("ssQuizProblemAbstractor")) {
                    	setSsQuizProblemAbstractorClass(parameter[0]);
                    	
                    }*/ else if (keyStem.equalsIgnoreCase("traceLevel")) {
                        setTraceLevel(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase(DEBUG_CODES)) {
                        addDebugCodes(parameter);

                    } else if (keyStem.startsWith("sp")) {
                        setSocketProxyOptions(keyStem, parameter);
                        
                    } else if (keyStem.equalsIgnoreCase(USE_JS_BRIDGE)) {
                    	configureJSBridge();
            		
            		}/* else if (keyStem.equalsIgnoreCase("ssBatchMode")) {

                    	setSsBatchMode(true);
                    } else if(keyStem.equalsIgnoreCase("ssFixedLearningMode")) {
                    	
                    	setSsFixedLearningMode(true);
                    } else if (keyStem.equalsIgnoreCase("ssContest")) {
                      	setSsContest(true);
                    } else if (keyStem.equalsIgnoreCase("ssInteractiveLearning")) {
                        setSsInteractiveLearning(true);
//                  } else if (keyStem.equalsIgnoreCase("ssILBatchMode")) {
//                        setSsIlBatchMode(true);
                    } else if (keyStem.equalsIgnoreCase("ssNonInteractiveLearning")) {
                    	setSsNonInteractiveLearning(true);
                    } else if (keyStem.equalsIgnoreCase("ssIlSignalNegative")) {
                        setSsIlSignalNegative(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssIlSignalPositive")) {
                        setSsIlSignalPositive(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssCondition")) {
                        setSsCondition(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssConstraintFile")) {
                        setSSConstraintFile(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssDecomposerFile")) {
                        setSSDecomposerFile(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssDecomposeInput")) {
                        ssUseDecomposition();

                    } else if (keyStem.equalsIgnoreCase("ssDoNotSaveRules")) {
                        ssDoNotSaveRules();

//                    } else if (keyStem.equalsIgnoreCase("ssExternalRuleActivationTest")) {
//                        ssUseExternalRuleActivationTest();
                        
                    } else if (keyStem.equalsIgnoreCase("ssFeaturePredicateFile")) {
                        setSsFeaturePredicateFile(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssFoaGetterClass")) {
                        setSsFoaGetterClass(parameter[0]);
                    
                    } else if (keyStem.equalsIgnoreCase("ssSelectionOrderGetterClass")) {
                        setSsSelectionOrderGetterClass(parameter[0]);
                    
                    } else if(keyStem.equalsIgnoreCase("ssClSolverTutorSAIConverter")) {
                    	setSsSaiConverterClass(parameter[0]);
                    
                    } else if(keyStem.equalsIgnoreCase("ssInterfaceElementGetterClass")) {
                    	setSsInterfaceElementGetterClass(parameter[0]);
                    
                    } else if(keyStem.equalsIgnoreCase("ssFoaClickDisabled")) {
                    	setSsFoaClickDisabled(true);
                    
                    } else if (keyStem.equalsIgnoreCase("ssInputCheckerClass")) {
                        setSsInputCheckerClass(parameter[0]);
                    }
                    
                    /* @author: jinyul, skillNameGetter is an ad-hoc method to identify
                     * skill name based on current selection. */
                   /* else if (keyStem.equalsIgnoreCase("ssSkillNameGetterClass")) {
                    	setSsSkillNameGetterClass(parameter[0]);
                    } else if (keyStem.equalsIgnoreCase("ssFoaSearch")) {
                        setSsFoaSearch(true);
                        
                    } else if (keyStem.equalsIgnoreCase("ssPathOrderingClass")) {
                        setSsPathOrderingClass(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssCacheOracleInquiry")) {
                        setSsCacheOracleInquiry(parameter[0]);

                    } else if(keyStem.equalsIgnoreCase("ssHeuristicBasedIDS")) {
                    	setSsHeuristicBasedIDS(true);
                    }
      
                    else if (keyStem.equalsIgnoreCase("ssPreservePrFile")) {
                        setSsPreservePrFile(true);

                    } else if (keyStem.equalsIgnoreCase("ssDeletePrFile")) {
                        setSsDeletePrFile(true);
                    
                    } else if (keyStem.equalsIgnoreCase("ssRuleActivationTestMethod")) {
                        setSsRuleActivationTestMethod(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssHintMethod")) {
                        setSsHintMethod(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssFoilLogDir")) {
                        setSsFoilLogDir(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssForceToUpdateModel")) {
                    	setSsForceToUpdateModel(true);

                    } else if (keyStem.equalsIgnoreCase("ssInputMatcher")) {
                        setSsInputMatcher(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssTypeChecker")) {
                        setSsTypeChecker(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssLearningLogFile")) {
                    	setSsLearningLogFile(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssLearnCorrectActions")) {
                    	setSsLearnCorrectActions(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssLearnCltErrorActions")) {
                    	setSsLearnCltErrorActions(parameter[0]);
                    	
                    } else if (keyStem.equalsIgnoreCase("ssLearnBuggyActions")) {
                    	setSsLearnBuggyActions(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssLearningRuleFiringLogged")) {
                        ssLearningRuleFiringLogged();

                    } else if (keyStem.equalsIgnoreCase("ssMemoryWindowOverRules")) {
                    	setSsMemoryWindowOverRules(true);

                    } else if (keyStem.equalsIgnoreCase("ssNumBadInputRetries")) {
                    	setSsNumBadInputRetries(parameter[0]);
                    } else if (keyStem.equalsIgnoreCase("ssNoOpCache")) {
                	// Sat Nov  4 16:07:26 EST 2006 :: Noboru
                	// This has actually no effect.  
                	// Because the _instance_ of FeaturePredicate (i.e., a RHS operator)
                	// must be created and stored anyways.
                	// What really makes sense is to limit the capacity of cache,
                	// which is implemented as -ssCacheCapacity 
                        setSsOpCached(false);
                        
                    } else if (keyStem.equalsIgnoreCase("ssSetFpCacheCapacity")) {
                	setSsFpCacheCapacity(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssNumIteration")) {
                        setSsValidationNumIteration(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssMaxSearchDepth")) {
                        setSsMaxSearchDepth(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssOperatorFile")) {
                        setSsOperatorFile(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssProjectDir")) {
                        setSsProjectDir(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssPrAgeDir")) {
                        setSsPrAgeDir(parameter[0]);
                    	
                    } else if (keyStem.equalsIgnoreCase("ssProblemSet")) {
                        setSsValidationProblemSet(parameter);
                        
                    } else if (keyStem.equalsIgnoreCase("ssRunValidation")) {
                        setSsRunValidation(true);

                    } else if (keyStem.equalsIgnoreCase("ssVerifyNumFoA")) {
                    	setSsVerifyNumFoA(true);
                     
                    } else if (keyStem.equalsIgnoreCase("ssShuffleValidation")) {
                        setSsShuffleRunMode(true);
                        
                    } else if (keyStem.equalsIgnoreCase("ssTestSet")) {
                        setSsValidationTestSet(parameter);

                    } else if (keyStem.equalsIgnoreCase("ssTestOutput")) {
                        setSsValidationOutput(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssUserDefSymbols")) {
                        setSsUserDefSymbols(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssPredictObservableActionName")) {
                        setSsPredictObservableActionName(parameter[0]);                        
                        
                    } else if (keyStem.equalsIgnoreCase("ssSetMemoryWindowSize")) {
                    	setSsMemoryWindowSize(parameter[0]);
                	
                    } else if (keyStem.equalsIgnoreCase("ssSearchTimeOutDuration")) {
                        ssSearchTimeOutDuration(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssTutorServerTimeOutDuration")) {
                        ssTutorServerTimeOutDuration(parameter[0]);
                      
                    }*/ else if (keyStem.equalsIgnoreCase("reduced")) {
                        setReducedMode(true);
                        
                    } /*else if (keyStem.equalsIgnoreCase("ssNoAutoOrderFOA")) {
                        ssAutoOrderFOAOff();
                        
                    } else if (keyStem.equalsIgnoreCase("ssLearnNoLabel")) {
                    	if (trace.getDebugCode("ss")) trace.out("ss", "Setting ssLearnNoLabel in SingleSessionLauncher");
                        setSsLearnNoLabel();
                        
                    } else if (keyStem.equalsIgnoreCase("ssLogAgendaRuleFiring")) {
                        ssLogAgendaRuleFiring();
                        
                    } else if (keyStem.equalsIgnoreCase("ssLogPriorRuleActivationsOnTraining")) {
                        ssLogPriorRuleActivationsOnTraining();
                        
                    } else if (keyStem.equalsIgnoreCase("ssTestOnLastTrainingOnly")) {
                    	ssTestOnLastTrainingOnly();
                        
                    } else if (keyStem.equalsIgnoreCase("ssCheckWilkinsburgBadBrd")) {
                    	ssCheckWilkinsburgBadBrd();
                	
                    } else if (keyStem.equalsIgnoreCase("ssSetMaxNumTraining")) {
                    	ssSetMaxNumTraining(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssSetMaxNumTest")) {
                    	ssSetMaxNumTest(parameter[0]);
                	
                    } else if (keyStem.equalsIgnoreCase("ssSwitchLearningStrategyAfter")) {
                        ssSwitchLearningStrategyAfter(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssTestProductionModelNoTest")) {
                    	ssSetTestProductionModelNoTest(true);
                	
                    } else if (keyStem.equalsIgnoreCase("ssAnalysisOfFitnessWilkinsburg")) {
                    	ssAnalysisOfFitnessWilkinsburg();
                	
                    } else if (keyStem.equalsIgnoreCase("ssLogStudentsLearning")) {
                    	ssLogStudentsLearning();
                        
                    } else if (keyStem.equalsIgnoreCase("ssValidateStepsInBRD")) {
                        setSsValidateStepsInBRD();
                	
                    } else if (keyStem.equalsIgnoreCase("ssDontShowAllRaWhenTutored")) {
                    	ssDontShowAllRaWhenTutored();
                    
                    } else if (keyStem.equalsIgnoreCase("ssClSolverTutorHost")) {
                    	ssSetClSolverTutorHost(parameter[0]);
                    	
                    } else if (keyStem.equalsIgnoreCase("ssRunInPLE")) {
                        setSimStPleOn(true);
                    } else if (keyStem.equalsIgnoreCase("ssLogging")) {
                        setSimStLogging(true);
                    } else if (keyStem.equalsIgnoreCase("ssLocalLogging")) {
                        setSimStLocalLogging(true);
                    } else if (keyStem.equalsIgnoreCase("ssDummyContest")) {
                        setSimStDummyContest(true);
                    } else if (keyStem.equalsIgnoreCase("ssContestServer")) {
                        setSimStContestServer(parameter[0]);
                    } else if (keyStem.equalsIgnoreCase("ssContestPort")) {
                        setSimStContestPort(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssUserID")) {
                    	setSimStUserId(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssSimStName")) {
                    	setSimStName(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssSimStImage")) {
                    	setSimStImage(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssProblemsPerQuizSection")) {
                    	setSimStProblemsPerQuizSection(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssLogURL")) {
                    	setSimStLogURL(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssCLQuizReqMode")) {
                    	setSsCLQuizReqMode();
                    } else if(keyStem.equalsIgnoreCase("ssSelfExplainMode")) {
                    	setSsSelfExplainMode();
                    } else if(keyStem.equalsIgnoreCase("ssIntroVideo")) {
                    	setSsIntroVideo(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssLoadPrefsFile")) {
                    	setSsPrefsFile(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssOverviewPage")) {
                    	setSsOverview(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssActivationList")) {
                    	setSsActivationList(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssTutalkParams")) {
                    	// @author Huan Truong
                    	// This flag enables Tutalk dialog mode and sets ctatLauncher parameters
                    	// Param: flag := experimenter@ctatLauncher[:[opt1,opt2,...,optn]]
                    	setSsTutalkParams(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssGeneralWMEPaths")){
                    	// @author samanz  
                    	// This flag will switch simstudent to not learn any specific wmepaths
                    	// Which will create rules to only have multivariable WME path selectors
                    	setSsGeneralWmePaths();
                    } */ else if(keyStem.equalsIgnoreCase(USE_HTTP)){
                    	controller.setStudentInterfaceLocal(false);
                    } else if(keyStem.equalsIgnoreCase(USE_JS_BRIDGE)){
                    	;  // just don't complain
                    } else if(keyStem.equalsIgnoreCase(CTAT_Launcher.SKIP_MONITOR_ARG)) {
                    	;  // just don't complain
                    } 
                    else if (keyStem.equalsIgnoreCase("ssPackageName")) {
                    	ctatLauncher.setPackageName(parameter[0]);
                    	
                    } 
                    else if (keyStem.startsWith("ss")){
                    	//SimStudent arguments are now parsed in miss controller. This is just to avoid the IllegalArgumentException
                    }
                    
                    else {	
                        throw new IllegalArgumentException ("Unknown command line argument: " + keyStem);
                    }
                } catch (IllegalArgumentException iae) {
                    trace.err("Error on command line argument " + i + " '"
                            + key + "': " + iae);
                }
            }
        }
    }

	/**
     * Set options for the {@link #socketProxy} instance from the command line.
     * Creates the instance if not already created.
     * N.B. the {@link SocketToolProxy} instance created here is not connected
     * to the {@link SocketTool} instance. Instead it's a placeholder to ensure that
     * {@link BR_Controller#getUniversalToolProxy()} returns the correct type.
     * @param key option name
     * @param parameters option values
     */
    private void setSocketProxyOptions(String key, String[] parameters) {
    	controller.setStudentInterfaceLocal(false);  // with these parameters, interface is remote
    	if(!(controller.getUniversalToolProxy() instanceof SocketToolProxy)) {
            SocketToolProxy stp = new SocketToolProxy(controller);
            controller.setUniversalToolProxy(stp); // dummy: see header comment
    	}
    }
    
    private void configureJSBridge() {
		RemoteProxy jsProxy = new JSProxy(controller);
		controller.setRemoteProxy(jsProxy);
		controller.setUniversalToolProxy(jsProxy.getToolProxy());
		controller.setStudentInterfaceLocal(false);
	}

    // *
    // BEGIN USER DEFINED CLA HANDLER * * * * * * * * * * * * * * * * * * * * *
    // Place methods that do the work for each command line argument (CLA) here
    // *
    //

    // Set up debug code
    public void addDebugCodes(String[] debugCode) 
    {
        if (debugCode != null) 
        {
            // sewall 1/4/05: add debug codes from command line args
            for (int i = 0; i < debugCode.length; ++i) 
            {
                if (debugCode[i] != null && debugCode[i].length() > 0) 
                {
                    trace.addDebugCode(debugCode[i]);
                }
            }
        }
    }
    
    private void setUseNewExampleTracer(boolean flag) {
        PseudoTutorMessageHandler.USE_NEW_EXAMPLE_TRACER = flag;
    }
    
    // set trace level for trace.out()
    private void setTraceLevel(String level) {

        trace.setTraceLevel(Integer.parseInt(level));
    }

    /**
     * SimSt relating stuffs
     */

    private boolean simStPleOn = false;
    private void setSimStPleOn(boolean flag) {
        simStPleOn = flag;
    }
    private boolean isSimStPleOn() {
        return simStPleOn;
    }
    
    MissControllerExternal getMissController() {
    	if(getCTATLauncher() == null)
    		return null;
        return getCTATLauncher().getMissController();
    }
    
    public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
     * Keep the Behavior Recorder and Student Interface hidden. Useful for
     * tests. Makes them run faster.
     * 
     * @param hideCTAT
     */
    public void setKeepHidden(boolean keepHidden) {
        this.hideCTAT = keepHidden;
    }
    
    /**
     * Set to true to run CTAT in reduced mode (for a study)
     * @param isReduced
     */
    public void setReducedMode(boolean isReduced)
    {
    	controller.setIsReducedMode(isReduced);
    }
    
    public void setSimStUserId(String userID)
    {
    	getMissController().setSsUserID(userID);
    }
    
    public void setSimStName(String name)
    {
    	getMissController().setSsSimStName(name);
    }
    
    public void setSimStImage(String img)
    {
    	getMissController().setSsSimStImage(img);
    }
    
    public void setSimStProblemsPerQuizSection(String num)
    {
    	getMissController().setSimStProblemsPerQuizSection(Integer.parseInt(num));
    }

    public void setSimStLogURL(String logURL)
    {
    	getMissController().setSimStLogURL(logURL);
    }
    
    private void setSsCLQuizReqMode() {
    	getMissController().setSsCLQuizReqMode();
	}
    
    private void setSsSelfExplainMode()
    {
    	getMissController().setSsSelfExplainMode();
    }

    // *
    // END USER DEFINED CLA HANDLER * * * * * * * * * * * * * * * * * * * * *
    // *
	/**
	 * @return the wrapper
	 */
	public TutorWrapper getWrapper() {
		return wrapper;
	}
	/**
	 * @param wrapper the wrapper to set
	 */
	public void setWrapper(TutorWrapper wrapper) {
		this.wrapper = wrapper;
	}

	/**
	 * @return instance from {@link TSLauncherServer#getNtpClient()};
	 *         null if {@link #getTSLauncherServer()} returns null
	 */
	public NtpClient getNtpClient() {
		TSLauncherServer ls = getLauncherServer();
		if (ls == null)
			return null;
		else
			return ls.getNtpClient();
	}

	/**
	 * Possibly set up collaboration between this instance and others in the same 
	 * Tutoring Service.
	 * @param setPrefs {@value MsgType#SET_PREFERENCES} message with all parameters
	 * @return shared {@link Collaborators} or null if not collaborating 
	 */
	public Collaborators checkForCollaborators(MessageObject setPrefs)
			throws Collaborators.NotReadyException {
		if(getLauncherServer() == null)
			return null;
		return getLauncherServer().checkForCollaborators(getSessionId(), setPrefs);
	}
 
	/**
	 * @return current Collaborators set, if any
	 */	
	public Collaborators.Collaborator findCollaborator() {
		if(getLauncherServer() == null)
			return null;
		return getLauncherServer().findCollaborator(getSessionId()); 
	}

	/**
	 * Remove {@link Collaborators} info from this session.
	 */
	public void endCollaboration() {
		if(getLauncherServer() == null)
			return;
		getLauncherServer().endCollaboration(getSessionId()); 
	}

	/**
	 * @return current ProblemSummary contents, as a String; empty element if Error.
	 */
	public String getProblemSummary() {
		try {
			ProblemModel pm = controller.getProblemModel();
			ProblemSummary ps = pm.getProblemSummary();
			return ps.toXML();
		} catch(Exception e) {
			trace.errStack("Error getting problem summary text: "+e+"; cause "+e.getCause(), e);
			return "<ProblemSummary />";
		}
	}
	
	public CTATTabManager getTabManager() {
		return this.tabManager;
	}

	public CTAT_Launcher getCTATLauncher() {
		return ctatLauncher;
	}

	/**
	 * Alter a {@value MsgType#SET_PREFERENCES} message as needed for collaboration.
	 * @param setPrefs
	 * @return result from {@link TSLauncherServer#editSetPreferences(MessageObject)}
	 */
	public MessageObject editSetPreferences(MessageObject setPrefs) {
		return getLauncherServer().editSetPreferences(setPrefs, getSessionId());
	}

	/**
	 * Add a new session. Calls {@link #setSessionId(String)} with new session id.
	 * @param guid new session id
	 */
	public void addNewSession(String guid) {
		String sessId = getLauncherServer().addNewSession(guid);
		setSessionId(sessId);
		TSLauncherServer.Session session = getLauncherServer().getSession(sessId);
		session.setController(getController());
		getController().getProperties().setProperty("guid", sessId);
	}
}

/**
 * APIs to features specific to the author interface. This separation from {@link SingleSessionLauncher}
 * enables us to omit those features' packages from the student runtime jars.
 */
class AuthorUI {

	/**
	 * Register for open-file events from the OS generated when a user double-clicks
	 * on a CTAT file (e.g., a .brd file).
	 * @param controller
	 */
	static void addOpenFileListener(BR_Controller controller) {
		org.simplericity.macify.eawt.DefaultApplication appProxy =  // Interface to OS X services
				new org.simplericity.macify.eawt.DefaultApplication();
		appProxy.addApplicationListener(new MacListener(controller));
	}

	/**
	 * Pass controller to {@link JCommWidget#setController(TutorController)}.
	 * @param controller
	 */
	public static void setJCommWidgetController(BR_Controller controller) {
		JCommWidget.setController(controller);
	}
}
