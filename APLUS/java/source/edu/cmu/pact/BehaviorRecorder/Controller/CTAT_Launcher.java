package edu.cmu.pact.BehaviorRecorder.Controller;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import cl.launcher.Session;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTab;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.TutoringService.Collaborators;
import edu.cmu.pact.TutoringService.Monitor;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.TutoringService.TSLogInfo;
import edu.cmu.pact.TutoringService.TransactionInfo;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.CtatLMSClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.ctat.view.CtatFrame;
import edu.cmu.pact.ctatview.CtatFrameController;
import edu.cmu.pact.ctatview.CtatMenuBar;
import edu.cmu.pact.ctatview.DockManager;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pact.miss.MissControllerStub;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.console.controller.MissController;

/**
 * A launcher server that initializes the components necessary to run CTAT.
 * Much of this has been moved from {@link BR_Controller} in order to support
 * multiple behavior recorders and reorganized launch structure; this should
 * hold most of the "singleton" data.
 * @author syyang
 *
 */
public class CTAT_Launcher {
	
	/** Command-line argument to omit the {@link Monitor} instance. Must be all lower case. */
	public static final String SKIP_MONITOR_ARG = "skipmonitor";
	
	private DockManager dockManager;
	private CTATTabManager tabManager;
	private CtatMenuBar ctatMenuBar;
	
	private CtatFrameController ctatFrameController;
	private RuleProduction.Catalog ruleProductionCatalog;

	private CtatLMSClient ctat_lms;
    private MissControllerExternal missController;
	
    /* ***** Flags ***** */
    private boolean needAuthoringFeatures;
	/** Reduced CTAT mode for studies */
	private boolean inReducedMode;
	private boolean inAppletMode;
	private boolean initialized;
	private Monitor monitor;
	private AuthorLauncherServer authorLauncherServer;
	private boolean isWebStart ;
	private boolean standAlone;
	private boolean servlet;
	
    private String packageName;
    private SingleSessionLauncher ssLauncher;
    
    /* ************************* INITIALIZATION ************************* */
	
	public CTAT_Launcher() {
		this(null, true, false, false);
	}
	
    public CTAT_Launcher(String argv[]) {
    	this(argv, true, false, false);
    }
    
	public CTAT_Launcher(String argv[], boolean showCtatWindow,
			boolean inAppletMode, boolean inReducedMode) {
		super();
		
    	if (LoadFileDialog.passFileNameToOtherInstance("spServerPort", argv))
    		System.exit(0);   // pass brd name and quit if another instance is active
    	
    	// Just one tab for SimStudent
		
		
		VersionInformation.setRunningSimSt(hasSimStArgs(argv));    

		
		
		this.initialized = false;
		this.needAuthoringFeatures = showCtatWindow;
		this.inAppletMode = inAppletMode;
		this.inReducedMode = inReducedMode;
		this.dockManager = new DockManager(this); // CHANGE THIS
		this.tabManager = new CTATTabManager(this, argv);
        this.authorLauncherServer = createAuthorLauncherServer(argv);

        determineEnvironment();

        if(hasSimStArgs(argv)){
			
			/*if(isServlet()){
				trace.out(" No of Max Tabs allowed "+this.tabManager.getMaxTabsServlet());
				trace.out(" No of Tabs created : "+this.tabManager.getNumTabServlet());
				this.tabManager.setMaxTabsServlet(1);
			}
			else*/
				CTATTabManager.setMaxNumTabs(1);

		}
        
        /*nbarba 01/15/2014: option to show ctat window must be here, not SingleSessionLauncher*/
        String noTutorInterface = System.getProperty("noCtatWindow");
        if (noTutorInterface != null) {
            trace.out("No CTAT Window");
            this.needAuthoringFeatures = false;
        }
      
        
       
       // if(showCtatWindow()) {
			int tabNumber = tabManager.getNextTabNumber();
			// Create the first tab manually so that initCtatFrameController()'s call tree can use getFocusedController()
			CTATTab firstTab = new CTATTab(tabNumber);
			tabManager.setFocusedTab(firstTab, true);
//			SingleSessionLauncher launcher = new SingleSessionLauncher(argv, tabManager, this, firstTab);
			SingleSessionLauncher launcher = new SingleSessionLauncher(argv, tabManager, this, firstTab);
			setSingleSessionLauncher(launcher);
			initCtatFrameController();  // must have a BR_Controller
			tabManager.getNewTab(firstTab, launcher);
			tabManager.setTabVisibility(tabNumber, true);

			tabManager.setFocusedTab(this.tabManager.getTabByNumber(tabNumber), true);
			ProblemModel pm = this.tabManager.getTabByNumber(tabNumber).getProblemModel();
			pm.addProblemModelListener(getCtatFrameController().getDockedFrame().getCtatMenuBar());
			// add listeners to the first tab that were lost because
			// the CtatFrameController wasn't/couldn't be initialized 
			pm.addProblemModelListener(pm.getController().getPseudoTutorMessageHandler());
			pm.getController().addChangeListener(getCtatFrameController().getDockedFrame().getCtatModePanel());
			/* Original Code
			dockWindowsNow(/*needAuthoringFeatures*//*false);
			*/
			needAuthoringFeatures = hasBehaviorRecorder(argv);
			dockWindowsNow(needAuthoringFeatures);
			
			getCtatMenuBar();
			
		//}
         
		
           
           
        if (!isInAppletMode()) {
	        // sewall 2011/07/02: ensure SimSt controller is non-null
        	// nbarba 01/16/2014: SimStudent in now initialized in MissController, argv should be passed .
        	initializeSimSt(argv);
        }

        startMonitor(argv);
    	
    	// Finalize the first graph tab; load the connection status icon after window docking
    	int firstTabNumber = 1;
    	BR_Controller controller = getTabManager().getTabByNumber(firstTabNumber).getController();
    	getDockManager().updateGraphConnectionStatus(firstTabNumber, controller.getUniversalToolProxy().getStudentInterfaceConnectionStatus());
    	getDockManager().markAsFocused(1, -1); // highlight the first tab as focused
        this.initialized = true;

        authorLauncherServer.startListener();
	}

	private void determineEnvironment() {
		// TODO Auto-generated method stub
		String appContainer = System.getProperty("appRunType");
		if(appContainer != null ){
			if(appContainer.equalsIgnoreCase("webstart"))
				setWebStart(true);
			else if(appContainer.equalsIgnoreCase("servlet"))
					setServlet(true);
				
			else if(appContainer.equalsIgnoreCase("shellscript"))
				   setStandAlone(true);
		}
		
			
	}

	/**
	 * Start a {@link Monitor} thread to listen for double-clicks to open files.
	 * @param argv no-op if {@value #SKIP_MONITOR_ARG} is among the args
	 */
	private void startMonitor(String[] argv) {
		for(String arg : argv) {
			if(arg.toLowerCase().contains(SKIP_MONITOR_ARG))
				return;
		}   
    	monitor = new Monitor(Monitor.MONITOR_PORT);
    	monitor.addRequestHandler(LoadFileDialog.NAME, new LoadFileDialog(this));
    	monitor.start();
	}

	/**
	 * If any command-line option begins with "-sp", then start {@link AuthorLauncherServer}. 
	 * @param argv command-line arguments
	 */
	private AuthorLauncherServer createAuthorLauncherServer(String[] argv) {
		if(argv == null)
			return new AuthorLauncherServer(-1, tabManager, null);

		int i = 0;
		while(i < argv.length && !(argv[i].startsWith("-spServerPort"))) ++i;
		if(i >= argv.length)
			return new AuthorLauncherServer(-1, tabManager, argv);

		int eq = argv[i].indexOf('=');
		try {
			String portStr;
			if(eq >= 0)
				portStr = argv[i].substring(eq+1);
			else
				portStr = (i < argv.length-1 ? argv[i+1] : "");
			int port = Integer.parseInt(portStr);
			return new AuthorLauncherServer(port, tabManager, argv);
		} catch(NumberFormatException nfe) {
			Utils.showExceptionOccuredDialog(nfe,
					String.format("Bad -spServerPort arguments \"%s%s\": %s", argv[i],
							(eq >= 0 ? "" : " "+argv[i+1]), nfe.toString()),
					"Command line error");
			return new AuthorLauncherServer(-1, tabManager, argv);
		}
	}

	/**
	 * @return the {@link #authorLauncherServer}
	 */
	public AuthorLauncherServer getAuthorLauncherServer() {
		return authorLauncherServer;
	}

	public void initCtatFrameController() {
		if(this.ctatFrameController == null) {
			this.ctatFrameController = new CtatFrameController(this); 
		}
	}
    
    /** Taken from the {@link BR_Controller} constructor. */
    public void initOnlineData(Boolean isOnline) {
    	if (isOnline != null) {
    		getAuthorLauncherServer().getProperties().setProperty("isOnline", isOnline.toString());
    		getAuthorLauncherServer().getPreferencesModel().setPreventSaves(isOnline.booleanValue());
    	}
    }
	
	/* ************************* MAIN ************************* */
    
    /**
     * Entry point for running the tools independently of a particular student
     * interface. If the sole argument is "-version", calls
     * {@link VersionInformation#main(String[])} and exits.
     * @param argv see {@link #parseArgv(String[])}
     */
    public static void main(final String argv[]) 
    {
    	if (argv.length == 1 && "-version".equalsIgnoreCase(argv[0])) 
    	{
    		VersionInformation.main(argv);
    		System.out.printf("includes %-5s: %b\n", "Jess" , VersionInformation.includesJess());
    		System.out.printf("includes %-5s: %b\n", "SimSt" , VersionInformation.includesSimSt());
    		System.out.printf("includes %-5s: %b\n", "CL" , VersionInformation.includesCL());
    		System.exit(2);
    	}
    	 
    	VersionInformation.setRunningSimSt(hasSimStArgs(argv));
    	
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    	    	JComponent studentInterface =
    	    			TutorController.createStudentInterface(System.getProperty(Logger.STUDENT_INTERFACE_PROPERTY));
    	    	CTAT_Launcher cl = new CTAT_Launcher(argv);
    	    	SingleSessionLauncher launcher = cl.getTabManager().getTabByNumber(1).getLauncher();
    	    	if(studentInterface != null) {
    	    		launcher.launch(studentInterface);
    	    	}
    	    	else {
    	    		launcher.launch();
    	    	}    			
    		}
    	});
    }
    
    /* ************************* GUI ************************* */

    /**
     * @param argv command-line arguments
     * @return true if any argument begins with "-ss"
     */
    private static boolean hasSimStArgs(String[] argv) {
    	if(argv == null)
    		return false;
		for(String arg : argv) {
			if(arg.toLowerCase().startsWith("-ss"))
				return true;
		}
		return false;
	}
    
    /**
     * Returns true if the command line argument contains -br or debug code is br
     */
    private static boolean hasBehaviorRecorder(String[] argv){
    	if(argv == null)
    		 return false;
         for(String arg : argv){
        	 if(arg.toLowerCase().equals("-br") || arg.toLowerCase().equals("br") )
        		 return true;
         }
         return false;
    }

	/** Taken from {@link BR_Controller}. */
	public void restoreDefaultView() {
		CtatFrameController cfController = getCtatFrameController();
		if (cfController != null)
			cfController.restoreDefaultView();
	}

	/** Load all windows. Taken from {@link BR_Controller}. */
	public void dockWindowsNow(boolean showCtatWindow) {
		CtatFrameController cfController = getCtatFrameController();
		if (cfController != null)
			cfController.dockWindowsNow(showCtatWindow);
	}
	
	/** Update the status panel at the bottom of the window.
	 * Taken from {@link BR_Controller}. */
	public void updateStatusPanel(String toolTipText, GroupModel gm, ProblemNode currentState,
			ProblemModel pm, String interpStatusLabel) {
    	if(Utils.isRuntime()) return;
    	CtatFrame cf = (getCtatFrameController() == null ? null : getCtatFrameController().getDockedFrame());
    	if (cf == null) return;
    	if (toolTipText != null) {	// if caller passed a tool tip, use it
    		cf.setProblemStatusToolTip(toolTipText);
    	}
    	
    	if (gm != null) {
    		cf.setOrderStatusLabel(gm.isGroupOrdered(gm.getTopLevelGroup()) ? "Ordered" : "Unordered");
    	}
    	cf.setCurrentStateLabel(currentState == null ? null : currentState.getName());
    	
    	cf.updateUnmatchedSelections(graphHasObsoleteComponents(getFocusedController()));

    	String problemFullName = pm.getProblemFullName();
		String problemFile;
		if (problemFullName != null && problemFullName.length() > 0) {
			if (toolTipText == null)
				cf.setProblemStatusToolTip(problemFullName);          // tool tip is full path name
			problemFile = (new File(problemFullName)).getName();      // label is simple file name
		} else
			problemFile = pm.getProblemName();              // else label is problemName
       	if (problemFile != null && problemFile.length() > 0)
          	cf.setProblemNameLabel(problemFile);
       	else {
       		cf.setProblemNameLabel("No graph has been opened yet");
			if (toolTipText == null)
				cf.setProblemStatusToolTip("Select File->'Open Graph' to load the problem");
       		cf.setTutorTypeLabel("No graph has been opened yet");
       	}
       	
       	if(interpStatusLabel != null) {
       		cf.setInterpStatusLabel(interpStatusLabel);
       	}
	}

	/**
	 * @param controller to get proper {@link UniversalToolProxy} instance
	 * @return {@link UniversalToolProxy#enableObsoleteSeletionDialog()} result
	 */
	private boolean graphHasObsoleteComponents(BR_Controller controller) {
		UniversalToolProxy utp = controller.getUniversalToolProxy();
		if(utp == null)
			return false;
		return utp.enableObsoleteSeletionDialog();
	}

	/* ************************* HANDLING ************************* */
	
    
    /** Saves the current window layout. Taken from {@link BR_Controller}. */
    public void saveDockedLayout(String layout) {
    	CtatFrameController cfController = getCtatFrameController();
		if (cfController != null) {
			cfController.saveLayout(layout);
		}
    }
    
    /** Taken from {@link BR_Controller}. */
    public void closeApplication(final BR_Controller controller, final boolean saveBrdFile) {
    	if (trace.getDebugCode("close")) trace.out("close", "saveBrdFile "+saveBrdFile+
    			", getAuthorToolsVisible() "+controller.getAuthorToolsVisible());
    	saveDockedLayout(controller.getCtatModeModel().getCurrentMode());
        try {
            if (!controller.getAuthorToolsVisible())
                exit(controller, false);
        } catch (NullPointerException e) {
            exit(controller, false);
        }

        if (getCTAT_LMS().isStudentLoggedIn()
                || controller.getProblemModel().isProblemLoadedFromLispTutor()
                || !saveBrdFile) {
            exit(controller, saveBrdFile);
            return;
        }
        exit(controller, saveBrdFile);
    }

    /** Taken from {@link BR_Controller}. */
    private void exit(BR_Controller controller, final boolean saveBrdFile) {
    	String appType = System.getProperty("appRunType");
    	
    	if(trace.getDebugCode("mg"))
    		trace.printStack("mg", "CTAT_Launcher (exit): start");

        if (!Utils.isRuntime()) {
        	getAuthorLauncherServer().getPreferencesModel().saveToDisk();
        }

        if (getCTAT_LMS().isStudentLoggedIn())
            getCTAT_LMS().logout();

        // trace.out ("stateDGRaph.size = " + stateDGraph.size());

    	int currentTab = controller.getTabNumber();
    	trace.out("mg", "CTAT_Launcher (exit): on tab " + currentTab);
        if (controller.getAuthorToolsVisible()
                && controller.getProblemModel().getProblemGraph().getNodeCount() > 0
                && !(getCTAT_LMS().isStudentLoggedIn() || controller.getProblemModel()
                        .isProblemLoadedFromLispTutor()) && saveBrdFile) {
        	// save the current graph first
        	trace.out("mg", "CTAT_Launcher (exit): saving problem " + currentTab);
            if (controller.saveCurrentProblemWithUserPrompt(true) == JOptionPane.CANCEL_OPTION) {
            	return;
            }
        }
            // loop over all tabs and save if necessary
            for(int i = 0; i < getTabManager().getNumTabs(); i++) {
            	int tabNumber = i+1;
            	if(tabNumber == currentTab) continue; // already saved this
            	BR_Controller control = getTabManager().getTabByNumber(tabNumber).getController();
            	trace.out("mg", "CTAT_Launcher (exit): saving problem " + tabNumber + " if needed");
            	// don't save empty problems
                if ((!control.getProblemModel().isEmpty()) &&
                		control.saveCurrentProblemWithUserPrompt(true) == JOptionPane.CANCEL_OPTION) {
                	return;
                }
            }

    	getDockManager().clearGraphViews(controller.getCtatModeModel().getCurrentMode());
        

        MissControllerExternal missController = getMissController(); 
        if (missController != null) {
            SimSt simSt = missController.getSimSt();
            if (simSt != null && simSt.inquiryClAlgebraTutor != null) {
                simSt.inquiryClAlgebraTutor.shutdown();
            }
            SimStPLE ple = missController.getSimStPLE();
            if(ple != null)
            {
            	ple.shutdown();
            }
        }
        
//        if (brFrame != null)
//        	brFrame.dispose();
        
        if(appType != null && !appType.equals("servlet"))
        			System.exit(0);
    }
    
	public PreferencesModel getPreferencesModel() {
		return getAuthorLauncherServer().getPreferencesModel();
	}
	
    public LoggingSupport getLoggingSupport(){
		return getAuthorLauncherServer().getLoggingSupport();
    }
    
	public void setCTAT_LMS(CtatLMSClient lms) {
		this.ctat_lms = lms;
	}
	
	public CtatLMSClient getCTAT_LMS() {
		return this.ctat_lms;
	}
    
    public void setMissController(MissControllerExternal mc) {
    	this.missController = mc;
    }
	
	public MissControllerExternal getMissController() {
		return this.missController;
	}
    
    /**
     * Create a {@link MissControllerExternal} instance if there's not one already.
     * @param argv command-line arguments
     */
    public void initializeSimSt() {
    	initializeSimSt(null);
	}
    
    /**
     * Create a {@link MissControllerExternal} instance if there's not one already.
     * @param argv command-line arguments
     */
    public void initializeSimSt(String[] argv) {
    	if (getMissController() == null || argv != null) {
    		MissControllerExternal missController = null;
    		if(VersionInformation.isRunningSimSt() && !Utils.isRuntime())
    			missController = new MissController(this, "");
    		else
    			missController = new MissControllerStub(this);
    		setMissController(missController);
    		if (trace.getDebugCode("miss"))
    			trace.out("miss", "===>miss instanceof "+
    					(getMissController() == null ? "null" : getMissController().getClass().getSimpleName()));
    		if(!Utils.isRuntime()) {
    			CtatFrameController cfController = getCtatFrameController();
    			if (cfController != null) {
    				cfController.getDockManager().addSimStConsole();
    			}
    		}
    		missController.parseArgv(argv);
    	}
    }
    
    /* ************************* SETTERS/GETTERS ************************* */

    public AbstractCtatWindow getActiveWindow() {
        return getDockedFrame();
    }
    
    public void setAppletMode(boolean inMode) {
    	this.inAppletMode = inMode;
    }
    
    public boolean isInAppletMode() {
    	return this.inAppletMode;
    }
	
	public CtatFrameController getCtatFrameController() {
		return this.ctatFrameController;
	}
	
	public DockManager getDockManager() {
		return this.dockManager;
	}
    
    public CtatMenuBar getCtatMenuBar() {
    	if(this.ctatMenuBar == null) {
    		this.ctatMenuBar = new CtatMenuBar(this);
    	}
    	return this.ctatMenuBar;
    }
    
    public void setSingleSessionLauncher(SingleSessionLauncher ssLauncher) {
    	this.ssLauncher = ssLauncher;
    }
    
    public SingleSessionLauncher getSingleSessionLauncher() {
    	return this.ssLauncher;
    }
    
    public AbstractCtatWindow getDockedFrame() {
    	AbstractCtatWindow result = null;
    	CtatFrameController cfController = getCtatFrameController();
        if (cfController != null)
            result = cfController.getDockedFrame();
        if (trace.getDebugCode("frame"))
        	trace.outNT("frame", "getDockedFrame() returns "+
        			(result == null ? null : result.getClass().getSimpleName()));
        return result;
    }
    
    public BR_Controller getFocusedController() {
    	return getTabManager().getFocusedTab().getController();
    }

    /**
     * @return {@link #getFocusedController()}
     * @deprecated Use {@link #getFocusedController()} instead.
     */
    public BR_Controller getController() {
    	return getFocusedController();
    }
	
	public void setReducedMode(boolean b)
	{
		this.inReducedMode = b;
	}
	
	public boolean isReducedMode()
	{
		return this.inReducedMode;
	}
	
	public void setNeedAuthoringFeatures(boolean flag) {
		this.needAuthoringFeatures = flag;
	}
	
	public boolean needAuthoringFeatures() {
		return this.needAuthoringFeatures;
	}
    
    public CTATTabManager getTabManager() {
    	return this.tabManager;
    }
	
	/* ************************* INTERFACE METHODS ************************* */
	
	/**
	 * No-op for now.
	 */
	public boolean removeSession(String guid) {
		return false;
	}
	
	/**
	 * No-op for now.
	 */
	public void updateTimeStamp(String guid) {}

	/**
	 * No-op for now.
	 */
	
	/**
	 * No-op for now.
	 */
	public TransactionInfo.Single createTransactionInfo(String sessionId) {
		return null;
	}

	/**
	 * No-op for now.
	 */
	public void updateTransactionInfo(String sessionId, Object info) {}

	/**
	 * No-op for now.
	 */
	public NtpClient getNtpClient() {
		return null;
	}

	/**
	 * No-op for now.
	 */
	public int enqueueToCollaborators(String guid, MessageObject mo) {
		return 0;
	}

	/**
	 * No-op for now.
	 */
	public Collaborators checkForCollaborators(String sessionId, MessageObject setPrefs)
			throws Collaborators.NotReadyException {
		return null;
	}

	/**
	 * No-op for now.
	 */	
	public Collaborators.Collaborator findCollaborator(String sessionId) {
		return null;
	}

	/**
	 * No-op for now.
	 */
	public void endCollaboration(String sessionId) {}
	
	public boolean isDoneIntializing() {
		return this.initialized;
	}
	
	public void launch(JComponent component) {
		getTabManager().getTabByNumber(1).getLauncher().launch(component);
	}
	
	/**
	 * Get a pointer to the rules or skills available for attachment to links.
	 * @return {@link #ruleProductionCatalog}; creates if null
	 */
	public RuleProduction.Catalog getRuleProductionCatalog() {
		if (ruleProductionCatalog == null)
			ruleProductionCatalog = new RuleProduction.Catalog();
		return ruleProductionCatalog;
	}

	/**
	 * Load a perspective file proper to the given mode  
	 * @param mode one of {@value CtatModeModel#EXAMPLE_TRACING_MODE}, 
	 *        {@value CtatModeModel#JESS_MODE}, etc.
	 */
	public void loadLayout(String mode) {
		if(getDockManager() != null)
			getDockManager().loadLayout(mode);
	}

	public boolean isWebStart() {
		return isWebStart;
	}

	public void setWebStart(boolean isWebStart) {
		this.isWebStart = isWebStart;
	}

	public boolean isStandAlone() {
		return standAlone;
	}

	public void setStandAlone(boolean standAlone) {
		this.standAlone = standAlone;
	}

	public boolean isServlet() {
		return servlet;
	}

	public void setServlet(boolean servlet) {
		this.servlet = servlet;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
