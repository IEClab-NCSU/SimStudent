/*
 * Created on Mar 9, 2005
 *
 *	Collinl:  Root controller object handling user input,
 * 	file loading and maintenance of the object states and
 *  other tasks.  
 *  
 *  See below for calls to the FrameController.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.FactoryConfigurationError;

import jess.Fact;
import junit.framework.Assert;

import org.jdom.Element;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.RemoteProxy;
import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.SkillometerManager;
import pact.CommWidgets.StudentInterfaceConnectionStatus;
import pact.CommWidgets.StudentInterfaceWrapper;
import pact.CommWidgets.TutorWindow;
import pact.CommWidgets.TutorWrapper;
import pact.CommWidgets.UniversalToolProxy;
import pact.CommWidgets.WrapperSupport;
import cl.ui.tools.tutorable.CTATTool;
import cl.utilities.sm.Debug;
import edu.cmu.hcii.ctat.env.CTATEnvironment;
import edu.cmu.hcii.runcc.MemorySerializedParser;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent.SetModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.LMS.CTAT_LMS;
import edu.cmu.pact.BehaviorRecorder.Dialogs.CheckAllStatesReport;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.RuleNamesDisplayDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.SaveFileDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.SkillsConsoleDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ChangeCurrentNodeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeRewiredEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NewProblemEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEventFactory;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelManager;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReader;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateWriter;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.DialogueSystemInfo;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerPath;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExpressionMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.SolutionStateModel.ProcessTraversedLinks;
import edu.cmu.pact.BehaviorRecorder.SolutionStateModel.SolutionState;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATComponent;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSAI;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSSELink;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSerializable.IncludeIn;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATStartStateEvent;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BehaviorRecorder.View.BR_Label;
import edu.cmu.pact.BehaviorRecorder.View.CTATUndoable;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.BehaviorRecorder.View.JUndo.JAbstractUndoPacket;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.Hints;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.JGraphPanel;
import edu.cmu.pact.JavascriptBridge.JSBridge;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Log.TutorActionLogV4;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.SocketProxy.HTTPToolProxy;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.TutoringService.Collaborators;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.EmptyIterator;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.ctat.CtatLMSClient;
import edu.cmu.pact.ctat.HTTPMessageObject;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.Skills;
import edu.cmu.pact.ctat.model.StartStateModel;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.ctat.view.CtatFrame;
import edu.cmu.pact.ctatview.CtatFrameController;
import edu.cmu.pact.ctatview.CtatMenuBar;
import edu.cmu.pact.ctatview.CtatModePanel;
import edu.cmu.pact.jess.MT;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.InputChecker;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStMessageDialog;
import edu.cmu.pact.miss.jess.AplusController;
import fri.patterns.interpreter.parsergenerator.Parser;

/**
 * @author Owner
 * 
 */
public class BR_Controller extends TutorController implements PropertyChangeListener, CtatModeListener, ChangeListener {
    
	/** Property name for {@link trace} codes to activate. */
	public static final String DEBUG_CODES = "DebugCodes";

    /** Property name for status governing use of {@link #restoreTransactions}. */
	public static final String PROBLEM_STATE_STATUS = "problem_state_status";
	
	/**
	 * Callback for {@link BR_Controller#createStartState(String, boolean)} when it has to create
	 * a separate thread.
	 */
	public static interface WillNotifyListeners {
		public void notifyListeners();
	}

	/* ******************************* VARIABLES ******************************* */
	
    /**
     * Reduced CTAT mode for studiess
     */
    private boolean isReducedMode;
    /**
     * Needed for restoring state
     */
    private boolean hintMode = true;
    private RuleNamesDisplayDialog ruleDisplayDialog;
    private String homeDir;
    private boolean changePreferredPath = true;
    private boolean useGroupDragging = true;
    //    private String mode = CtatConstants.DEMONSTRATE_MODE;
    
    public static final int FIND_SKILLS = 1;
    public static final int FIND_HIGHLIGHT = 2;
    private final int tabNumber;
    /** Semantic event identifier of current event. */
    private String semanticEventId = "";
    //private LoggingSupport loggingSupport;
    //private boolean connectedToProductionSystem; // currently unused
    
    //    private boolean useJess = false;
    // String jessPath = ""; // currently unused
    // String jessInstanceFile = ""; // currently unused
    
    private Font originalEdgeFont;
    private boolean allowCurrentStateChange = true;
    private boolean traversalCountEnabled;
    public  Lock  widgetSynchronizedLock = new Lock();
    String runType = "";
    
    public void setRunType(String runTypeProperty) {
    	super.setRunType(runTypeProperty);
    	this.runType = runTypeProperty;
    }
    
    public String getRunType() {
    	return this.runType;
    }
    /** Constants for selected Preference items. */
    public static final String LOCK_WIDGETS = "Lock Widgets",
            COMMUTATIVITY = "Commutativity", 
            CASE_SENSITIVE = "Case Sensitive",
            GROUP_DRAG = "Group Dragging",
            SHOW_TT_SAVE = "Show Tutor Type Save Popup",
            SET_PREF_PATH = "Set Preferred Path",
            FORCE_SCAFFOLDING = "Force Scaffolding",
            MACHINE_LEARNING = "Machine Learning",
            DIALOGUE_SYSTEM = "Dialogue System",
            USE_OLI_LOGGING = "Use OLI Logging",
            OLI_LOGGING_URL = "OLI Logging URL",
            USE_DISK_LOGGING = "Use Disk Logging",
            DISK_LOGGING_DIR = "Disk Logging Directory",
            TRAVERSAL_COUNT = "Enable Edge Traversal Counts",
            PROJECTS_DIR = "Projects Directory",
            ALWAYS_LINK_STATES = "Always Link States", USE_LISP = "Use Lisp",
            HIGHLIGHT_RIGHT_WIDGET = "Highlight Right Widget",
            MAX_STUDENT = "Maximum Number of Student Actors",
            ALLOW_TOOL_REPORTED_ACTIONS = "Allow Tool-Performed Actions", 
            SUPPRESS_STUDENT_FEEDBACK = "suppressStudentFeedback",
            HINT_POLICY = "hintPolicy";

    private boolean firstCheckAllStatesFlag = true;
    private boolean sendESEGraphFlag = false;

    private Hashtable interfaceActions_NoneState_Tutor;
    int index_interfaceActions_NoneState_Tutor;
    //private boolean startStateInterface;
    private boolean startStateModified = false;
    private AplusController amt;
    /** Should be non-null only when initializing a model/view pair
     * (use {@link #getProblemModel()} otherwise) */
    //private ProblemModel problemModel;
    private JGraphPanel graphPanel;
    
    private static ProblemNode _copySubgraphNode;
    private JAbstractUndoPacket abstractUndoPacket;
    
    private boolean actionLabelsFlag = true;
    private boolean preCheckLISPLabelsFlag;
    private boolean ruleLabelsFlag;
    private boolean showCallbackFnFlag = false;
    private boolean preferredPathOnlyFlag;

    private PseudoTutorMessageHandler pseudoTutorMessageHandler;
    private CTAT_Options ctatOptions;
    final DemonstrateModeMessageHandler demonstrateModeMessageHandler = new DemonstrateModeMessageHandler (this);
    private SingleSessionLauncher launcher;
    private String tutorModeBrdTempDirectory;
    private boolean dockedNow = false;
    private ProcessTraversedLinks processTraversedLinks;
    private SkillometerManager skillometerManager;
    private RuleActivationTree ruleActivationTree;
    //private boolean startFindWidgetsForProblem;
    //private Vector notFoundWidgetsForProblem;
    private MT modelTracer;
    private boolean lmsLoginEnabled;
	
	/** Writer for serializing the ProblemModel. */
	protected ProblemStateWriter problemStateWriter;
	/** Reader for deserializing from XML. */
	protected ProblemStateReader problemStateReader;
	/** Dynamic part of problem--changes during solving. */
    private SolutionState solutionState;
    
    /** For persistent storage of objects needed by extensions, author functions, etc. */
	private Map<String, Object> sessionStorage =
			Collections.synchronizedMap(new HashMap<String, Object>());
	
	/** 
	 * Format {@link Date} objects for {@link #handleVersionInfo(Vector, Vector)}.
	 * N.B. SimpleDateFormat instances are not thread-safe.  
	 */
	private final DateFormat VersionInfoDateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZ");
    //private Hashtable commNameTable = new Hashtable();
    private boolean missActive = false;
   // private LoggingSupport logger;
    /**
     * Gustavo 10 Sep 2007: used by Interactive Learning, to select whether the next current
     * node is a pre-existing node or a newly-created node.
     */
    //private boolean isBrdOpen = false;
    //private boolean toolTipsInitialized;
    //a VariableTable that holds the last Selection Action Input from the last interfaceAction
    //for display in the VTSplitPane
    private VariableTable saiTable;
    private boolean showWidgetInfo;
    TutorMessageHandler tutorMessageHandler = new TutorMessageHandler(this);
    ArrayList<CtatModeListener> controllerListenerList = new ArrayList<CtatModeListener>();
    private CtatModeModel ctatModeModel;
    /**
     * this overrides protectProdRules
     * if true, preserves the PR without popping-up a dialog
     * if false, 'protectProdRules' will determine whether a dialog pops-up
     */
    private boolean clArgumentSetToProtectProdRules = false;
    /** 'destroyPrWithoutAsking'
     * if true, SimStudent will ask before deleting the existing production rules file.
     * if false, SimStudent deletes the file without asking.
     */
    private boolean deletePrFile = false;
    private ProblemModelManager problemModelManager;
    
    protected Hashtable<String, String> widgetTable = new Hashtable<String, String>();

	/**
	 * Listeners for state changes in the connection to the interface. The listener list is maintained
	 * here, not in {@link UniversalToolProxy}, because instances of that class may not survive as
	 * the communications link to the student interface is broken and reconnected.
	 */
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
    
	// Skill Name Setter - Variables
	private AskHint hintInfo;
    public AskHint getHintInfo() {
		return hintInfo;
	}
	
    public void setHintInfo(AskHint hintInfo) {
		this.hintInfo = hintInfo;
		setSkillInfo(hintInfo.skillName);
	}
    
    private String skillInfo;
    
    public String getSkillInfo() {
		return skillInfo;
	}

	public void setSkillInfo(String skillInfo) {
		this.skillInfo = skillInfo;
	}
	// AskHint hint, String step, ProblemNode parentNode, String message[]
    private String stepInfo;
    private ProblemNode parentNodeInfo;
    private String messageInfo;
	
    public String getStepInfo() {
		return stepInfo;
	}

	public void setStepInfo(String stepInfo) {
		this.stepInfo = stepInfo;
	}

	public ProblemNode getParentNodeInfo() {
		return parentNodeInfo;
	}

	public void setParentNodeInfo(ProblemNode parentNodeInfo) {
		this.parentNodeInfo = parentNodeInfo;
	}
	
	BR_Controller brCtrl = null;
    public BR_Controller getBrController() {
        return brCtrl;
    }
    public void setBrController(BR_Controller brCtrl) {
        this.brCtrl = brCtrl;
    }

	public String getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}
	
	private String doneButtonText;
	
	public String getDoneButtonText() {
		return doneButtonText;
	}

	public void setDoneButtonText(String doneButtonText) {
		this.doneButtonText = doneButtonText;
	}

	public BR_Controller() 
    {
    	this (true, false, null, null, 1); // FIXME: 1 is a bad way to do this by default
    }
    
    public BR_Controller(boolean showCtatWindow) 
    {
        //this (showCtatWindow, false, null, null);
    	this (showCtatWindow, false, null, null, 1); // FIXME: see above
    }
    
 
    public BR_Controller(boolean showCtatWindow, boolean isReducedMode) 
    {
        //this (showCtatWindow, isReducedMode, null, null);
    	this (showCtatWindow, isReducedMode, null, null, 1); // FIXME: see above
    }

    /* Collinl:  Root execution call for the BR_Controller.  This will start
     * 	up the control services, most notably setting up the event listener
     * 	and the CtatFrameController which in turn will handle GUI events. 
     */
    //public BR_Controller(boolean showCtatWindow, boolean isReducedMode, Boolean isOnline, SingleSessionLauncher launcher) 
    public BR_Controller(boolean showCtatWindow, boolean isReducedMode,
    		Boolean isOnline,
    		SingleSessionLauncher launcher, int tabNumber) {
		//FIXME Is this correct? It sets launcher to launcher.getLaunchServer(), and then it overwrites it with the passed in launcher. Should they be the same launcher?
    	super(launcher == null ? null : launcher.getLauncherServer());
    	this.launcher = launcher;
    	setClassName ("BR_Controller");
    	debug ("BR_Controller () inAppletMode() "+inAppletMode()+", isOnline "+isOnline);

    	if (trace.getDebugCode("br")) trace.out("br", "launcher == null ? null : launcher.getLauncherServer() returns " + (launcher == null ? null : launcher.getLauncherServer()));
    	if (trace.getDebugCode("br")) trace.out("br", "in BR_Controller constructor, launcher == null is " + (launcher == null));
    	if (trace.getDebugCode("br")) trace.out("br", "launcher is " + launcher);
    	if (trace.getDebugCode("br")) trace.out("br", "launch server is " + launcher.getLauncherServer());
    	if (trace.getDebugCode("br")) trace.out("br", "getServer() is " + getServer());
    	
        // model/view initialization
        ProblemModel model = new ProblemModel(this);
        this.problemModelManager = new ProblemModelManager(model, this);
    	if (!Utils.isRuntime()) { // if not in tutoring service (author time)
    		//System.out.println(" Tab Number for this mode :  "+tabNumber);
    		this.graphPanel = new JGraphPanel(getServer(), this, tabNumber);
		}
    	

    	
    	if (!Utils.isRuntime())
    		getServer().setNeedAuthoringFeatures(showCtatWindow);
    	this.tabNumber = tabNumber;
    	
    	if (isOnline == null && Utils.isRuntime()) {
    		isOnline = new Boolean(true);
    	}
    	if (!Utils.isRuntime())
    		getServer().initOnlineData(isOnline);
    	

        
    	initCTATModeModel();
    	
        trace.out("brV", "constructor before VariableTable()");
        //if in authoring mode, initialize the saiTable for display in the gui
        if(!Utils.isRuntime())
        {
        	initSAITable();
        	initDisplayFlags();
        }
        

        
        this.solutionState = new SolutionState(this.getProblemModel());
        this.solutionState.reset();

        // initialize problem state I/O
		// initialize undo mechanism
        if(!Utils.isRuntime()) {
        	CTATUndoable CUndo = new CTATUndoable(this);
        	this.abstractUndoPacket = JUndo.makeAbstractUndoable(CUndo, -1);
        }
    	this.problemStateWriter = new ProblemStateWriter(this);
    	this.problemStateReader = new ProblemStateReader(this);
    	
        trace.out("brV", "constructor before SkillometerManager()");
        initSkillometer();
        
        trace.out("brV", "constructor before ProcessedTraversedLinks()");
        initProcessTraversedLinks();
        // Oct 23, 2006 :: Noboru SimSt must be initialized only when it's activated 
        // initializeSimSt();
        trace.out("brV", "constructor about to return");
     }
	
	
	/* ******************************* RANDOMNESS ******************************* */
	/**
	 * @param newSeed new seed for random number generator {@link #gen}.
	 */
	void setRandomSeed(long newSeed) {
		getTSLauncherServer().getRandomGenerator().setSeed(newSeed);
	}

	/**
	 * Generate indices 0, 1, ..., n-1 in random order.
	 * @param n number of indices to generate
	 * @return array of randomized indices
	 */
	public int[] randomOrder(int n) {
		int[] result = new int[n];
		List<Integer> indices = new LinkedList<Integer>();
		for (int i = 0; i < n; ++i)
			indices.add(new Integer(i));
		for (int i = 0; n > 0; ++i, --n)
			result[i] = indices.remove((int) (getTSLauncherServer().getRandomGenerator().nextDouble()*n));
		return result;
	}
	
	/* ******************************* INITIALIZATION ******************************* */
	
	private void initProcessTraversedLinks() {
		processTraversedLinks = new ProcessTraversedLinks(this);
        getProcessTraversedLinks().initTraversedLinks();
	}
	
	private void initCTATModeModel() {
		ctatModeModel = new CtatModeModel(this);
    	addCtatModeListener(this);
	}
	
	private void initDisplayFlags() {
		setShowActionLabels(getTSLauncherServer().getPreferencesModel().getOrSet("Show Action Labels", true));

        setPreCheckLISPLabelsFlag(false);
        setShowRuleLabels(getTSLauncherServer().getPreferencesModel().getOrSet("Show Rule Labels", false));

        setPreferredPathOnlyFlag(false);
	}
	
	private void initSAITable() {
		//if the BR is in authoring mode, initialize the saiTable for display in the gui
		if(!Utils.isRuntime()){
        	saiTable=new VariableTable(true);
        	saiTable.put("selection[0]",null);
        	saiTable.put("action[0]", null);
        	saiTable.put("input[0]", null);
        	saiTable.put("type", null);
        }
	}
	
	private void initSkillometer() {
		if(trace.getDebugCode("tutorservice"))
			trace.out("tutorservice", String.format("BR_C.initSkillometer() TSLauncherServer %s,"+
					" launcher %s, ctatLauncher %s", trace.nh(getTSLauncherServer()),
					trace.nh(getLauncher()),
					(getLauncher() == null ? null : trace.nh(getLauncher().getCTATLauncher()))));
		if(getLauncher().getCTATLauncher() == null)
			return;
		this.skillometerManager = new SkillometerManager();
        if (VersionInformation.includesCL())
        	getLauncher().getCTATLauncher().setCTAT_LMS(new CTAT_LMS(this, skillometerManager));
        else {
        	getLauncher().getCTATLauncher().setCTAT_LMS(new CtatLMSClient() {
        		public boolean isStudentLoggedIn() { return false; }
        		public void logout() {}
        		public void advanceProblem() {}
        	});
        }
	}
	
	public void addCelltoWidgetTable(String selection, String input) {
    	widgetTable.put(selection, input);
    }
	
	public void clearWidgetTable() {
		widgetTable.clear();
	}
    
    public String getWidgetTable(String selection) {
		if(widgetTable.containsKey(selection)) {
			return widgetTable.get(selection);
		}
		return "";
	}

    /* ******************************* HANDLING/NOTIFS ******************************* */
    
    /**
     * Handle a CTAT mode event.
     * @param evt
     */
    public void ctatModeEventOccured(CtatModeEvent evt) {
        
        if (trace.getDebugCode("gusmiss")) trace.out("gusmiss", "gusmiss", "entered ctatModeEventOccured("+evt+")");
        if (evt instanceof CtatModeEvent.SetCurrentNodeEvent) {
        	updateStatusPanel(null);
        	return;
        }
        if (! (evt instanceof CtatModeEvent.SetModeEvent)) { return; }
        CtatModeEvent.SetModeEvent sme = (CtatModeEvent.SetModeEvent) evt;
        CtatMenuBar ctatMenuBar = null;
        CtatFrameController cfController = getCtatFrameController();
        if (cfController != null && cfController.getDockedFrame() != null)
        	ctatMenuBar = cfController.getDockedFrame().getCtatMenuBar();
        
        if (sme.modeChanged() && sme.getMode() == CtatModeModel.SIMULATED_STUDENT_MODE) {
            if (trace.getDebugCode("gusmiss")) trace.out("gusmiss", "getMissController() = " + getLauncher().getMissController());
//            if (getMissController() == null) {
        	initializeSimSt(); //this guarantees that missController is not null

//              initializeSimStInteractive();
                getLauncher().getMissController().dealWithAnyExistingPrFile();

//            }
        }

        //see e-mail to ctat-internal titled "changing behavior recorder mode in the middle of a demonstration?"
//        //new code
//        if (getMissController()!=null && getMissController().getSimSt()!=null && sme.modeChanged() && sme.getMode() != CtatModeModel.SIMULATED_STUDENT_MODE) {
//            getMissController().getSimSt().killInteractiveLearningThreadIfAny();
//        }

        if(!Utils.isRuntime() && sme.modeChanged() && getServer() != null)
        	getServer().loadLayout(sme.getMode());            
        
        if (sme.authorModeChanged()) {
        	sendAuthorModeChangedMsg(sme);
        	if (sme.getAuthorMode() == CtatModeModel.DEFINING_START_STATE)
        		editStartState();
        	else {
        		if((sme.getAuthorMode() == CtatModeModel.TESTING_TUTOR)&&
            		(sme.getPreviousAuthorMode()==CtatModeModel.DEMONSTRATING_SOLUTION)){
                		//        //getHintMessagesManager().cleanUpHintOnChange();
        	        	//setCopySubgraphNode(null);
        	        	//getSolutionState().reset();

        				// processTraversedLinks = new ProcessTraversedLinks(this);
        				// getProcessTraversedLinks().initTraversedLinks();
        				//ProblemNode curr = solutionState.getCurrentNode();
                		//solutionState.setCurrentNode(null);
                		//	goToState(curr);
            	}
        		syncDemoModeWithCurrentState(sme.getAuthorMode() == CtatModeModel.DEMONSTRATING_SOLUTION);
            	if (ctatMenuBar != null)
            		ctatMenuBar.enableSaveGraphMenus(true);
        	}
        }
        
        getExampleTracer().setDemonstrateMode(getCtatModeModel().isDemonstratingSolution());        

        if (ctatMenuBar != null) {
        	if (sme.modeChanged()) {
        		ctatMenuBar.enableJessMenus(sme.getMode() == CtatModeModel.JESS_MODE);
        		ctatMenuBar.enableMassProductionMenus(sme.getMode() == CtatModeModel.EXAMPLE_TRACING_MODE);
        	}
        }        
        if (sme.modeChanged() &&
        		(getCtatModeModel().isRuleEngineTracing() || getCtatModeModel().isExampleTracingMode())) {
        	goToStartState();
        }
    }

    /**
     * Notify the student interface of the author mode change.
     * @param sme
     */
    private void sendAuthorModeChangedMsg(SetModeEvent sme) {
    	if (Utils.isRuntime())
    		return;
    	if(trace.getDebugCode("mode"))
    		trace.printStack("mode", "BR_C.sendAuthorModeChangedMsg("+sme+")");
    	MessageObject mo = MessageObject.create("AuthorModeChange", "NotePropertySet");
    	mo.setProperty("oldMode", sme.getPreviousAuthorMode());
    	mo.setProperty("newMode", sme.getAuthorMode());
    	utp.handleMessage(mo);
	}

	/**
     * Put the demonstrate mode handler into the same state as the new example tracer.
     * @param newModeIsDemoMode true if the new author mode is demonstrate mode
     */
    private void syncDemoModeWithCurrentState(boolean newModeIsDemoMode) {
    	PseudoTutorMessageHandler handler = getPseudoTutorMessageHandler();
        if (handler != null
                && handler.getExampleTracer() != null) {
            ProblemNode currentNode =
            	handler.getExampleTracer().getCurrentNode(newModeIsDemoMode);
            if (currentNode == null)
                //setCurrentNode2(getProblemModel().getStartNode());  // mimic menu
            	setCurrentNode2(getProblemModel().getStartNode());  // mimic menu
            else
                setCurrentNode(currentNode);  //FIXME this setCurrentNode() is obsolete
        }
    }

    /**
     * Take the actions needed when an author is about to edit the start state.
     * These include:<ul>
     * <li></li>
     * </ul>
     */
    private void editStartState() {
    	if(!Utils.isRuntime())
    		sendStartNodeMessages(null, false, false);
	}

	/**
	 * @return		The {@link ExampleTracerGraph} coresponding to the currently focused
	 * 				problem model
	 */
    public ExampleTracerGraph getExampleTracerGraph() {
		return getProblemModel().getExampleTracerGraph();
    }
    

    public void setUniversalToolProxy(UniversalToolProxy utp) {
    	ProblemModel pm = getProblemModel();
    	if(pm != null && this.utp != null)
    		pm.removeProblemModelListener(this.utp);
        this.utp = utp;
    	if(pm != null && this.utp != null)
    		pm.addProblemModelListener(this.utp);

        CtatFrameController cfc = getCtatFrameController();
        CtatFrame cf = (cfc == null ? null : cfc.getDockedFrame());
        if (!Utils.isRuntime() && cf != null) {
        	CtatModePanel cmp = cf.getCtatModePanel();
        	// add a listener so the connection status can update
        	if (cmp != null)
        		addChangeListener(cmp);
        	CtatMenuBar cmb = cf.getCtatMenuBar();
        	if (cmb != null)
        		addChangeListener(cmb);
        }
    }

    //Gustavo 22Nov2006
    //this hack is supposed to be init() for when there is no Student Interface
/*    public void init() {
        problemStateWriter = new ProblemStateWriter(this);
        problemStateReader = new ProblemStateReader(this);
        setBehaviorRecorderMode(CtatModeModel.EXAMPLE_TRACING_MODE);
        getCtatModeModel().setAuthorMode(CtatModeModel.DEFINING_START_STATE);

        getPreferencesModel().addPropertyChangeListener(this);
        loadPreferencesFromModel();	
    }*/
	
    public void init(BRPanel brPanel) {
    	if(trace.getDebugCode("brV"))
    		trace.out("brV", "BR_C.init() before updateStatusPanel()");
        updateStatusPanel(null);    	
    	
    	// associate the pair with the handler; the pair will also handle its problem model
    	this.pseudoTutorMessageHandler = new PseudoTutorMessageHandler(this);
    	ProblemModel pm = getProblemModel();
    	pm.addProblemModelListener(getPseudoTutorMessageHandler());
    	/* Collinl: Core GUI startup iff the gui is called for. */
    	if (!Utils.isRuntime() && getServer().needAuthoringFeatures()) {

    	    // initial display flags
    	    initDisplayFlags();
    	    initSkillometer();

    		//menu bar needs to detect changes in the problem model
    	    if(getServer().getCtatFrameController() == null) {
    	    	getServer().initCtatFrameController();
    	    }
    	    pm.addProblemModelListener(getCtatFrameController().getDockedFrame().getCtatMenuBar());
    	}
    	initSAITable();
        setBehaviorRecorderMode(CtatModeModel.EXAMPLE_TRACING_MODE);
        if(Utils.isRuntime())
            getCtatModeModel().setAuthorMode(CtatModeModel.TESTING_TUTOR);
        else
            getCtatModeModel().setAuthorMode(CtatModeModel.DEFINING_START_STATE);

        getTSLauncherServer().getPreferencesModel().addPropertyChangeListener(this);

        addChangeListener(this);

        loadPreferencesFromModel();
    }

    /**
     * Method for startup to load BRD file from system properties. If
     * {@link System#getProperty(java.lang.String)}"ProblemFileLocation" is
     * set, converts its value to a URL and loads that BRD. Else if
     * {@link System#getProperty(java.lang.String)}"ProblemFileURL" is set,
     * loads the BRD at that URL.
     */
    public void loadBRDFromSystemProperties() {
    	String[] propertiesToTry = {
    			Logger.QUESTION_FILE_PROPERTY,
    			LoadFileDialog.PROBLEM_FILE_LOCATION,
    			LoadFileDialog.PROBLEM_FILE_URL
    	};
    	
    	String problemName = (String) getProperties().getProperty(Logger.PROBLEM_NAME_PROPERTY);
    	if(problemName != null && problemName.length() < 1)
    		problemName = null;
    	
    	String skillsProp = (String) getProperties().getProperty("skills");
    	Skills skills = null;
    	if(skillsProp != null && skillsProp.length() > 0) {
    		try {
    			skills = Skills.factory(skillsProp);
				if(skills != null)
				{
					skills.setExternallyDefined(true);
					if (trace.getDebugCode("applet"))
						trace.out("applet", "handleSetPreferences skills property\n  "+skillsProp+
								"\n  , after factory n="+skills.size()+", skills:\n  "+skills.toXMLString());
				}
    		} catch(Exception e) {
    			trace.errStack("BR_Controller.loadBRDFromSystemProperties() error creating skills \""+
    					e+"\""+" from property value\n  "+skillsProp, e);
    		}
    	}
    	
    	for(String propertyName : propertiesToTry) {
    		String fileToOpen = (String)(getProperties().getProperty(propertyName));
    		if (fileToOpen == null || fileToOpen.length() < 1)
    			continue;

        	File f = null;     // first try as absolute path
        	try {
        		f = new File(fileToOpen);
        	} catch (Exception e) {
        		trace.err("Error converting "+LoadFileDialog.PROBLEM_FILE_LOCATION+" property \""+
        				fileToOpen+"\" to file name: "+e);
        	}
        	if (f != null && f.isAbsolute()) {
        		(new LoadFileDialog(getServer(), this)).handleLoadFileRequest(fileToOpen);
        		break;
        	}
        			 
        	URL url = null;
        	try {
        		url = new URL(fileToOpen);             // try fileToOpen as URL
        	} catch(Exception e) {
        		trace.err("BR_Ctlr.loadBRDFromSystemProperties(): Error converting \""+
        				fileToOpen+"\" to URL: "+e);
        		url = Utils.getURL(fileToOpen, this);  // try relative path name onthe classpath
        	}
        	if (trace.getDebugCode("br"))
        		trace.out("br", "problemFileLocation str = " + fileToOpen + ", url = " + url);
        	if (url == null)
        		trace.err("null URL for problemFileLocation " + fileToOpen);
        	else
        		fileToOpen = url.toString();
        	synchronized(this) {
        		if (trace.getDebugCode("br"))
        			trace.out("br", "brd file to open = " + fileToOpen);
        		boolean result = openBRFromURL(fileToOpen, problemName, skills);
        		if(!Utils.isRuntime()) {
        			ActionEvent ae = new ActionEvent(this, 0, "Opened ProblemFileLocation "+fileToOpen);
        			getUndoPacket().getInitializeAction().actionPerformed(ae);
        		}
        	}
        }
        loadBRModeFromSystemProperties();

    }

    /**
     * Method for startup to set logging parameters and control info from system
     * properties.
     */
    public void loadControlFromSystemProperties() {
   	
    	super.loadControlFromSystemProperties();
    	
    	CTAT_Properties properties = getProperties();
    	
        String debugCodes = (String)properties.getProperty(DEBUG_CODES);
        String loggingAuthToken = (String)properties.getProperty("LoggingAuthToken");
        String loggingSessionID = (String)properties.getProperty("LoggingSessionID");
        String loggingURL = (String)properties.getProperty(Logger.LOG_SERVICE_URL_PROPERTY);
        String loggingDirectory = (String)properties.getProperty(Logger.DISK_LOG_DIR_PROPERTY);
        String loggingUserID = (String)properties.getProperty("LoggingUserID");
        String showBR = (String)properties.getProperty("BehaviorRecorderVisible");

        if (debugCodes != null)
            trace.addDebugCodes(debugCodes);
        loadBRModeFromSystemProperties();
        if (loggingAuthToken != null)
            getLoggingSupport().setAuthToken(loggingAuthToken);
        if (loggingSessionID != null)
            getLoggingSupport().setSessionId(loggingSessionID);
        if (loggingURL != null)
        	getTSLauncherServer().getPreferencesModel().setStringValue(BR_Controller.OLI_LOGGING_URL,
                    loggingURL);
        if (loggingDirectory != null)
        	getTSLauncherServer().getPreferencesModel().setStringValue(BR_Controller.DISK_LOGGING_DIR,
                    loggingDirectory);
        if (loggingUserID != null)
            getLoggingSupport().setLoggingUserID(loggingUserID, true);

        if (showBR != null) {
            fireCtatModeEvent(new CtatModeEvent.SetVisibleEvent(Boolean.valueOf(showBR).booleanValue()));
        }
        if (trace.getDebugCode("br"))
        	trace.out("br", "loadControlFromSystemProperties loggingURL "+loggingURL+
        			", loggingDirectory "+loggingDirectory);
    }

    /**
     * Calculate the proper isVisible() setting for the Behavior Recorder
     * display frame.  
     * @return true if BR display should be visible
     */
    public boolean getShowBehaviorRecorder() {
        String showBR = (String)(getProperties().getProperty("BehaviorRecorderVisible"));
        if (showBR != null)
        	return Boolean.valueOf(showBR).booleanValue();
        if (getOptions() != null)
        	return getOptions().getShowBehaviorRecorder();
        
        return true;
    }
    
    /**
     * 
     */
    private void loadBRModeFromSystemProperties() {
        String BRMode = (String)(getProperties().getProperty("BehaviorRecorderMode"));
        if (trace.getDebugCode("br")) trace.out("br", "loadBRModeFromSystemProperties() BRMode "+BRMode);
        if (BRMode != null)
        {
//            if (BRMode.equals(CtatModeModel.SIMULATED_STUDENT_MODE))
//commented out because not a JUnitTest
//                setModeSimStAndDestroyProdRules();
//            else
                setBehaviorRecorderMode(BRMode);

            /** The cmd line property _DBehaviorRecorderMode=DemonstrateMode no longer fits the GUI model. It used to be Demonstrate or Example Trace or Production Rule. Now we have Tutor Type (e.g., Example Trace) plus a toggle button that is either Demonstrate or Not (e.g., tutoring). **/
//            if (!BRMode.equals(getCtatModeModel().SIMULATED_STUDENT_MODE))
            	//getCtatModeModel().setDemonstrateMode(false);
        }
        
    }

    /**
     * Equivalent to
     * {@link #openBRFromURL(String, String, Skills) openBRFromURL(problemFileURL, null, null)}.
     * @param url URL of BRD file to load
     * @return false if fails
     */
    public boolean openBRFromURL(String problemFileURL) {
    	return openBRFromURL(problemFileURL, null, null);
    }
    
    /**
     * Try to open a BRD file from a URL. Show error if fails. Calls
     * {@link UniversalToolProxy#setProblemFileURL(String)}if succeeds. This
     * method was made public initially only to facilitate unit test code.
     * 
     * @param url URL of BRD file to load
     * @param problemName externally-specified problem name, to be used instead
     *                    of name on start node
     * @param skills externally-specified skills, to be used instead of those defined in the file
     * @return false if fails
     */
    public boolean openBRFromURL(String problemFileURL, String problemName,
    		Skills skills) {
    	if (trace.getDebugCode("br")) trace.out("br", "IN THIS THREAD "+this+" openBRFromURL("+problemFileURL+")");
        String message = "<html>The file could not be found or the format of the file is not recognized. <br>"
            + "Please check the file and try again.<br>" + "Error: ";
        String title = "Error loading file";
        try {
        	boolean result = openBRDFileAndSendStartState(problemFileURL, problemName, skills);
        	if (!result)
        		Utils.showExceptionOccuredDialog(null, message, title);
        	return result;
        } catch (Exception e1) {
        	Utils.showExceptionOccuredDialog(e1, message, title);
            return false;
        }
    }

    /**
     * Tell whether to display warnings. Do not display any {@link JOptionPane}
     * or other modal dialogs unless this method returns true.
     * @return true if a student interface or author interface is visible
     */
    public boolean displayWarnings() {
    	if (getStudentInterface() != null && getStudentInterface().isVisible())
    		return true;
    	CtatFrameController cfController = getCtatFrameController();
    	if (cfController != null
    			&& cfController.getDockedFrame() != null
    			&& cfController.getDockedFrame().isVisible()) {
    		return true;
    	}
		return false;
	}

	// ////////////////////////////////////////////////////
    /**
     * Called when a PropertyChangeEvent is sent from the PreferencesModel.
     * 
     * @param evt
     *            PropertyChangeEvent detailing change
     */
    // ////////////////////////////////////////////////////
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        Object newValue = evt.getNewValue();
        if (setPreference(name, newValue) && !Utils.isRuntime())
        	getPreferencesModel().saveToDisk();
    }

    /**
     * Set the current node in the UI. This method actually changes the display by sending
     * a {@link ChangeCurrentNodeEvent}. Also sets current node in {@link #getSolutionState()}. 
     * @param targetNode new current node
     */
    public void setCurrentNode(ProblemNode targetNode) {
    	ProblemNode oldNode = getCurrentNode();
        if (trace.getDebugCode("feb19")) trace.printStack("feb19","entered setCurrentNode() old "+oldNode+", tgt "+targetNode);
        getSolutionState().setCurrentNode(targetNode);
        //ProblemModel pm = getProblemModel();
        ProblemModel pm = getProblemModel();
        if (pm != null) {
        	ChangeCurrentNodeEvent ccne = new ChangeCurrentNodeEvent(this, oldNode, targetNode);
        	pm.fireProblemModelEvent(ccne);
        }
        
        

        fireCtatModeEvent (new CtatModeEvent.SetCurrentNodeEvent(oldNode, targetNode));

    }
    
    /**
     * Set the current node in the BR.  Unlike {@link #setCurrentNode(ProblemNode)},
     * this method uses the new example tracer.
     * @param newCurrentNode the target node
     * @return List of new states visited from start state to newCurrentNode
     */
    //this function seems to just traverse from the start state forward.
    public Vector setCurrentNode2(ProblemNode newCurrentNode) {
    	return setCurrentNode2(newCurrentNode, false);
    }
    public Vector setCurrentNode2(ProblemNode newCurrentNode, boolean flagged_activation) {
    	ExampleTracerPath pathToNode = null;
        //List<ExampleTracerEvent> results = null; // unused?
        ProblemNode beginNode = getCurrentNode();
        boolean msgsToStudentSent = false;
        // trace.out("miss", "setCurrentNode2(" + newCurrentNode + ")");
        // trace.out("miss", "currentNode = " + getSolutionState().getCurrentNode());
        if (newCurrentNode != beginNode) {

        	if (getCtatModeModel().isExampleTracingMode() && !getCtatModeModel().isDemonstratingSolution()) {
        		resetTraversedLinks();
        		pathToNode = getPseudoTutorMessageHandler().advanceToNode(newCurrentNode); // goes through start state
        		msgsToStudentSent = true;
        	} else {
            	pathToNode = getProblemModel().findPath(newCurrentNode);        		
            	getPseudoTutorMessageHandler().traversePath(pathToNode);
        	}
        		
            processPageInformation(newCurrentNode);

            // need to send back to utp's part Comm
            // msgs & update currNode
            if (!msgsToStudentSent && !flagged_activation)
                sendCommMsgs(newCurrentNode, getProblemModel().getStartNode());   //paints HERE

            setCurrentNode(newCurrentNode);
            fireCtatModeEvent(CtatModeEvent.REPAINT);
        }
        Vector<ProblemEdge> vec = new Vector<ProblemEdge>();
       // System.out.println("Path Taken to state " + newCurrentNode.getName());
        if (pathToNode != null) {
        	for(ExampleTracerLink link : pathToNode){
        		//System.out.print(link.getEdge().getName() + ", ");
        		vec.add(link.getEdge().getEdge());
        	}
        }
        //System.out.println("------------------------------");
        return vec;
    }

    /**
     * gus 03/04 - for multi-page interfaces (e.g. HTML5), send message
     * indicating which page
     * @param newCurrentNode
     */
    private void processPageInformation(ProblemNode newCurrentNode) {
        ProblemEdge tempEdge = null;
        boolean destinationNodeFound = false, sendPageMsg = false;
    
        Enumeration iter = getProblemModel().getProblemGraph().getConnectingEdges(
                newCurrentNode);
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            if (newCurrentNode == tempEdge.getNodes()[ProblemEdge.DEST]) {
                destinationNodeFound = true;
                break;
            }
        }
    
        if (!destinationNodeFound) {
            return;
        }
        boolean destinationNodeFound1;
        Enumeration iter1;
        //Vector pNames, pValues; unused
        String page = null;
        EdgeData edgeData = tempEdge.getEdgeData();
        edu.cmu.pact.ctat.MessageObject msg = edgeData.getDemoMsgObj();
        if (msg != null) {
            try {
                page = (String) msg.getProperty("Page");
            } catch (Exception e) {
                // trace.err("processPageInformation: newCurrentNode = " + newCurrentNode);
                e.printStackTrace();
            }
        }
        
        if (page == null) {
            return;
        }

        destinationNodeFound1 = false;
        iter1 = getProblemModel().getProblemGraph().getConnectingEdges(newCurrentNode);
        while (iter1.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter1.nextElement();
            if (newCurrentNode == tempEdge.getNodes()[ProblemEdge.SOURCE]) {
                destinationNodeFound1 = true;
                break;
            }
        }
        
        String nextPage = null;
        
        if (!destinationNodeFound1)
            sendPageMsg = true;
        else {
            edgeData = tempEdge.getEdgeData();
            msg = edgeData.getDemoMsgObj();
            try {
                nextPage = (String) msg.getProperty("Page");
                if (nextPage.equals(page))
                    sendPageMsg = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sendPageMsg) {
            Vector selection = new Vector();
            Vector input = new Vector();
            Vector action = new Vector();
            selection.add("-setpage-");
            input.add(page);
            action.add("SetPage");
            sendCorrectActionMsg(selection, input, action);
        } else if (nextPage != null) {
            Vector selection = new Vector();
            Vector input = new Vector();
            Vector action = new Vector();
            selection.add("-setpage-");
            input.add(nextPage);
            action.add("SetPage");
            sendCorrectActionMsg(selection, input, action);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public void moveToMatchedEdge(ProblemEdge targetEdge) {
        EdgeData myEdge = targetEdge.getEdgeData();

        if (myEdge.getActionType().equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
            setCurrentNode(targetEdge.getNodes()[ProblemEdge.DEST]);
            sendCorrectActionMsg(myEdge.getStudentSelection(), myEdge
                    .getStudentInput(), myEdge.getStudentAction());
        } else if (myEdge.getActionType().equalsIgnoreCase(
                EdgeData.BUGGY_ACTION)
                || myEdge.getActionType().equalsIgnoreCase(
                        EdgeData.FIREABLE_BUGGY_ACTION)) {

            sendIncorrectActionMsg(myEdge.getStudentSelection(), myEdge
                    .getStudentInput(), myEdge.getStudentAction(), false);

            if (myEdge.getActionType().equalsIgnoreCase(
                    EdgeData.FIREABLE_BUGGY_ACTION)) {
                setCurrentNode(targetEdge.getNodes()[ProblemEdge.DEST]);
                sendCommMsgs(getSolutionState().getCurrentNode(),
                		getProblemModel().getStartNode());
            }

        }

        return;
    }

    /**
     * @return	current node in problem model
     * Added by Kim K.C. 11/21/2005
     */
    public ProblemNode getCurrentNode() {
    	return getSolutionState() == null ? null : getSolutionState().getCurrentNode();
    }

    public void setPreferredWidgetFocus() {
        // move focus to preferred outgoing edge's selection, if applicable
    	if (studentInterface == null)  // until implement setFocus for remote interfaces
    		return;
        String selection = null, action = null;
        Enumeration iter = getProblemModel().getProblemGraph().getConnectingEdges(
                getSolutionState().getCurrentNode());
        while (iter.hasMoreElements()) {
            ProblemEdge edgeTemp = (ProblemEdge) iter.nextElement();

            if (edgeTemp.getNodes()[ProblemEdge.DEST] != getSolutionState()
                    .getCurrentNode()) {
                EdgeData myEdgeTemp = edgeTemp.getEdgeData();

                if (myEdgeTemp.isPreferredEdge()) {
                    selection = myEdgeTemp.getSelection().get(0).toString();
                    action = myEdgeTemp.getAction().get(0).toString();
                }
            }
        }
        if (selection == null || action == null)
            return;
        JCommWidget widget = null;
        if (!(action.equalsIgnoreCase("ButtonPressed")
                || action.equalsIgnoreCase("UpdateRadioButton")
                || action.equalsIgnoreCase("UpdateCheckBox") || action
                .equalsIgnoreCase("UpdateMultipleChoice"))) {
            widget = getCommWidget(selection);
        } else if (studentInterface.getWrapperSupport().hasHintButton()) {
            widget = getCommWidget("Hint");
            if (widget == null)
                widget = getCommWidget("Help");
        }
        if (widget == null) {
            return;
        }
        widget.setFocus(selection);

    }

    /**
     * Tell whether we're accepting messages that will become part of the start
     * state.
     * 
     */
    public boolean isAcceptingStartStateMessages() {
//        return ((!getProblemModel().getStartNodeCreatedFlag()) && getMode()
//                .equalsIgnoreCase(CtatModeModel.DEMONSTRATE_MODE));
        return (getCtatModeModel().isDefiningStartState());

    }
    
    /**
     * 2 August 2007
     * will destroy the PR file, unless the preserve switch is used.
     *
     * 7 November 2007
     * This is meant to be called by JUnitTests.
     */
    public void setModeSimStAndDestroyProdRules(){
        this.setDeletePrFile(true);
        this.setBehaviorRecorderMode(CtatModeModel.SIMULATED_STUDENT_MODE);
    }
    
    public void setModeSimStAndKeepProdRules(){
        this.setDeletePrFile(false);
        this.setBehaviorRecorderMode(CtatModeModel.SIMULATED_STUDENT_MODE);
    }
    
    /**
     * Queue or send a start state message to the student interface.
     * @param o message to send
     */
    public void sendStartStateMsg(MessageObject o) {
    	sendStartStateMsg(o, MsgType.START_STATE_END);
    }
    
    /**
     * Queue or send a start state message to the student interface.
     * @param o message to send
     * @param endMsgType the message type that marks the end of the start state bundle
     */
    private void sendStartStateMsg(MessageObject o, String endMsgType) {
        if (utp == null)
        	return;
        ProblemModel pm = getProblemModel();
        if (pm != null)
        	pm.addInterfaceVariables(o);
        if (!StartStateModel.isProperStartStateMessage(o))
        	return;
        if (clientSupports(MsgType.StartStateMessages)) {
        	if (trace.getDebugCode("startstate"))
        		trace.out("startstate", "bundling on start state:\n  "+o);
        	utp.bundleMessage(o, MsgType.StartStateMessages, endMsgType);
        } else {
        	if (trace.getDebugCode("startstate"))
        		trace.out("startstate", "no bundling on start state:\n  "+o);
        	utp.handleMessage(o);
        }
    }

    /**
     * Tell whether the current student interface supports a given message type.
     * @param msgType value from {@link MsgType} constants
     * @return null if {@link #utp} null;
     *         else result of {@link UniversalToolProxy#clientSupports(String, String)}
     */
	public Boolean clientSupports(String msgType) {
		if(utp == null)
			return null;
		return utp.clientSupports(msgType, getCommShellVersion());
	}
    
    /** The SetPreferences parameter name of the comm shell version. */
    private static final String COMM_SHELL_VERSION = "CommShellVersion";

    /** The version of the Flash client CommShell component. */
    private String commShellVersion;

    /**
     * @return {@link #commShellVersion}
     */
    public String getCommShellVersion() {
		return commShellVersion;
	}

    /**
     * Pass a {@value MsgType#INTERFACE_DESCRIPTION} message to the start state apparatus.
     * These are messages received from the student interface, each with the settings of a component. 
     * @param o the message
     * @param fwdToSSM if true, pass o to {@link UniversalToolProxy#handleStartStateMessageFromInterface(MessageObject)}
     */
    public void handleInterfaceDescriptionMessage (MessageObject o, boolean fwdToSSM) 
    {
    	debug ("handleInterfaceDescriptionMessage() acceptingSSMsgs "+isAcceptingStartStateMessages()+
    			", fwdToSSM "+fwdToSSM);
    	if(!isAcceptingStartStateMessages())
    		return;
    	
    	if(getUniversalToolProxy() != null && fwdToSSM)
    		getUniversalToolProxy().handleStartStateMessageFromInterface(o);
    	
    	Element componentObj=(Element) o.getProperty("serialized");  // from Flash components
    	if(componentObj == null)                                     // from Java components
    		; // FIXME componentObj = (Element) o.getProperty("ComponentDescription");
    	
    	addComponent (componentObj);
	}

    /**
     * Pass a {@value MsgType#UNTUTORED_ACTION} message to the start state apparatus.
     * These are messages received from the student interface, each with the settings of a component. 
     * @param o the message
     * @param fwdToSSM if true, pass o to {@link UniversalToolProxy#handleStartStateMessageFromInterface(MessageObject)}
     */
    public void handleUntutoredActionMessage(MessageObject o, boolean fwdToSSM) 
    {
    	debug ("handleUntutoredActionMessage () fwdToSSM "+fwdToSSM);

    	if(this.isAcceptingStartStateMessages()) {
    		if(getUniversalToolProxy() != null && fwdToSSM)
    			getUniversalToolProxy().handleStartStateMessageFromInterface(o);
    		addInterfaceAction (o.getPropertiesElement());  // UntutoredAction sets SAI like InterfaceAction
    	} else
    		getProblemModel().handleUntutoredAction(o);
    }
    
    /**
     * Pass a message to UTP, usually to the student interface.
     * @param mo message to pass
     * @param original request, needed for HTTP responses
     */
    public void handleMessageUTP(MessageObject mo, MessageObject request) {

    	debug ("handleMessageUTP (mo, req) mo "+mo+", request "+request);
		
		getLogger().oliLog(mo, true);

		if(request instanceof HTTPMessageObject) {
			HTTPToolProxy htp = ((HTTPMessageObject) request).getHttpToolProxy();
			htp.handleMessage(mo);
		} else if (utp != null) {
			utp.handleMessage(mo);
		}
    	
    }
    
    /**
     * Pass a message to UTP, usually to the student interface.
     * @param mo
     */
	public void handleMessageUTP(MessageObject mo) 
	{
		debug ("handleMessageUTP () utp="+utp);
		
		getLogger().oliLog(mo, true);
		
		if (utp != null) 
		{
            if (trace.getDebugCode("msg")) 
            	trace.out("msg", "message = " + mo);
            
        	utp.handleMessage(mo);			
		}
	}
	
	/**
     * Pass a message to UTP, usually to the student interface.
     * @param mo
     */
	public void handleMessageUTP(MessageObject mo, boolean flagged_activation) 
	{
		debug ("handleMessageUTP () utp="+utp);
		
		getLogger().oliLog(mo, true);
		
		if (utp != null) 
		{
            if (trace.getDebugCode("msg")) 
            	trace.out("msg", "message = " + mo);
            
        	utp.handleMessage(mo, flagged_activation);			
		}
	}

    /**
     * Send a Comm Message to the student interface.
     * @param o
     * @param bundleName
     * @param endMsgType
     */
    void bundleMessage(MessageObject o, String bundleName, String endMsgType) {
    	getLogger().oliLog(o, true);// true means msg is from tutor to tool
        if (clientSupports(bundleName))
        	utp.bundleMessage(o, bundleName.toString(), endMsgType);
        else
        	utp.handleMessage(o);
    }

    /**
     * Process a Comm message.
     * @param o message to process
     */
    // ////////////////////////////////////////////////////
    public void handleCommMessage(MessageObject o) 
    {
    	
    	
    	
    	if(trace.getDebugCode("ll"))
    		trace.outNT ("ll", "handleCommMessage ("+o+")");
    	if (launcher != null && launcher.getLauncherServer() != null)
    		o.setTransactionInfo(launcher.getLauncherServer().createTransactionInfo(getSessionId()));
    	
        String messageType = o.getMessageType();
    	if(trace.getDebugCode("br")) trace.out("br", "br_controller handleCommMessage messageType is - "
    											+messageType+"\nMessage is "+o.toString());
        
        if (messageType==null)
        {
        	trace.err("Internal error: no MessageType found in XML:\n  "+o);
        	return;
        }
        
    	if (messageType.equalsIgnoreCase(MsgType.INTERFACE_ACTION)) 
    	{
    		
    		getLoggingSupport().oliLog(o, false); 
    		if(!ssInputChecker(o))
    			return;
    		handleInterfaceActionMessage(o);
    		getWidgetSynchronizedLock().releaseLock();
    		
    	} else if (messageType.equalsIgnoreCase(MsgType.SET_PREFERENCES)) {
    		getLoggingSupport().oliLog(o, false); 
        	if(o instanceof HTTPMessageObject) {
        		setUniversalToolProxy(((HTTPMessageObject) o).getHttpToolProxy());
        		handleSetPreferencesMessage(o);
        		setUniversalToolProxy(null);                        // allow  garbage collection
        	} else
        		handleSetPreferencesMessage(o);
        } else if (messageType.equalsIgnoreCase("RetractSteps")) {
        	getLoggingSupport().oliLog(o, false); 
            handleRetractSteps(o);
        } else if (messageType.equalsIgnoreCase(MsgType.UNTUTORED_ACTION)) {
        	handleUntutoredActionMessage(o, true);
            getWidgetSynchronizedLock().releaseLock();
        } else if (messageType.equalsIgnoreCase(MsgType.PROBLEM_SUMMARY_REQUEST)) {
            getLoggingSupport().oliLog(o, false); 
            handleProblemSummaryRequest(o);
        } else if (messageType.equalsIgnoreCase("LispCheckResult")) {
            tutorMessageHandler.handleLispCheckResultMessage(o, getProblemModel(), getLoggingSupport(), this);
            getWidgetSynchronizedLock().releaseLock();
    	} else if (messageType.equals("ShowHintsMessage")) {
            handleMessageUTP(o);
            getWidgetSynchronizedLock().releaseLock();
        } else if (messageType.equals("ResetAction")) {
            handleMessageUTP(o);
        } else if (messageType.equals("ShowHintsMessageFromLisp")) {
            handleMessageUTP(o);
            getWidgetSynchronizedLock().releaseLock();
        } else if (messageType.equals("BuggyMessage")) {
            handleMessageUTP(o);
            getWidgetSynchronizedLock().releaseLock();
        } else if (messageType.equalsIgnoreCase("GoToInitialState")) {
            handleGoToInitialStateMessage();
        } else if (messageType.equalsIgnoreCase("GoToState")) {
            handleGoToStateMessage(o);
        } else if (messageType.equalsIgnoreCase("CorrectAction")) {
            handleCorrectActionMessage(o);
        } else if (messageType.equalsIgnoreCase("IncorrectAction")) {
            handleIncorrectActionMessage(o);
        } else if (messageType.equalsIgnoreCase("CheckAllStatesResult")) {
            processCheckAllStatesResult(o);
        } else if (messageType.equalsIgnoreCase("ChangeWMState")) {
            tutorMessageHandler.handleChangeWMStateMessage(o, this);
        } else if (messageType.equalsIgnoreCase("ShowLoginWindow")) {
            handleShowLoginWindowMessage();
        } else if (messageType.equalsIgnoreCase("IsTutorVisible")) {
            handleIsTutorVisibleMessage(o);
        } else if (messageType.equalsIgnoreCase("SetBrdTempDirectory")) {
            handleSetBRDTempDirectoryMessage(o);
        } else if (messageType.equalsIgnoreCase("LoadProblem")) {
            getLoggingSupport().oliLog(o, false); 
            handleLoadProblemMessage(o);
        } else if (messageType.equalsIgnoreCase("LoadBrdPathFile")) {
            getLoggingSupport().oliLog(o, false); 
            doLoadBRDPathFileMessage(o);
        } else if (messageType.equalsIgnoreCase("LoadProblemWithHTTP")) {
            getLoggingSupport().oliLog(o, false); 
            handleLoadProblemWithHTTPMessage(o);
        } else if (messageType.equalsIgnoreCase("StartNewProblem")) {
            getLoggingSupport().oliLog(o, false); 
            handleStartNewProblemMessage();
        } else if (messageType.equalsIgnoreCase("CreateStartState")) {
            getLoggingSupport().oliLog(o, false); 
            handleCreateStartStateMessage(o);
        } else if (messageType.equalsIgnoreCase("Quit")) {
            getLoggingSupport().oliLog(o, false); 
            System.out.println(" It is called ");
            handleQuitMessage();
        } else if (messageType.equalsIgnoreCase("QuitWithoutSave")) {
            getLoggingSupport().oliLog(o, false); 
            handleQuitWithoutSaveMessage();
        } else if (messageType.equalsIgnoreCase(MsgType.PROBLEM_RESTORE_END)) {
        	sendProblemRestoreEnd();
        } else if (messageType.equals(MsgType.INTERFACE_DESCRIPTION)) {
            handleInterfaceDescriptionMessage(o, true);
            getWidgetSynchronizedLock().releaseLock();
        } else if (messageType.equals(MsgType.INTERFACE_IDENTIFICATION)) {
            ;
            // getWidgetSynchronizedLock().releaseLock();
        } else if (messageType.equalsIgnoreCase(MsgType.VERSION_INFO)) {
            handleVersionInfo(o, null);
        } else if (messageType.equalsIgnoreCase(MsgType.MESSAGE_BUNDLE)) {
        	handleBundle(o);
        } else if (messageType.equalsIgnoreCase(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS)) {
        	if(getUniversalToolProxy() != null)
        		getUniversalToolProxy().handleGetAllInterfaceDescriptions(o);
        } else if (messageType.equalsIgnoreCase(MsgType.COMPONENT_INFO)) {
            utp.handleComponentInfoMessage(o);
        } else if (messageType.equalsIgnoreCase(MsgType.SET_MODE)) {
            handleSetModeMessage(o);
        } else if (messageType.equals(MsgType.TUTORING_SERVICE_ERROR)) {
        	Collaborators.handleTutoringServiceErrorMessage(o, this);
        } else {
        	try {
        		throw new IllegalArgumentException("MessageType "+messageType);
        	} catch (IllegalArgumentException iae) {
        		trace.errStack("BR_Controller.handleCommMessage(): Don't know message type \""+
        				messageType+"\"; sending "+MsgType.VERSION_INFO, iae);
        	} finally {                                                      // *must* respond somehow
        		handleVersionInfo(o, "Unknown message type: "+messageType);  // default response
        	}
        }
        updateStatusPanel(null);
    }

	/** Names of modes that can be changed by {@link #handleSetModeMessage}. */
    public enum SettableModes {
    	FeedbackPolicy
    };
    
    /**
     * Change a mode in the running Tutoring Service. Currently the only supported
     * modes are in {@link BR_Controller.SettableModes}
     * @param o
     */
	private void handleSetModeMessage(MessageObject o) {
		String responseText = "Success";
		Object mode;
		if(null == (mode = o.getProperty("mode")))
			responseText = "Error: no mode specified";
		else {
			if(SettableModes.valueOf(mode.toString()) != SettableModes.FeedbackPolicy)
				responseText = "Error: mode \""+mode+"\" not supported for change by "+MsgType.SET_MODE;
			else {
				Object value;
				if(null == (value = o.getProperty("value")))
					responseText = "Error: no value specified for mode "+mode;
				else {
					FeedbackEnum fb = FeedbackEnum.fromString(value.toString());
					if(!(fb instanceof FeedbackEnum))
						responseText = "Error: invalid feedback policy \""+value+"\"";
					else {
						if(getProblemModel() == null)
							responseText = "Error: no problem loaded";
						else
							getProblemModel().setSuppressStudentFeedback((FeedbackEnum) fb);
					}
				}
			}
		}
		MessageObject msg = MessageObject.create(MsgType.SET_MODE, "SetNoteProperty");
		msg.setProperty("result", responseText);
		msg.suppressLogging(true);
		handleMessageUTP(msg);
	}
	
	/**
     * @return {@link #launcher}.{@link SingleSessionLauncher#getSessionId()}
     */
    private String getSessionId() {
    	if (launcher != null)
    		return launcher.getSessionId();
		return null;
	}

	/**
     * If SimStudent InputChecker is defined, use it to check input 
     * @param mo InterfaceAction message to check
     */
	private boolean ssInputChecker(MessageObject mo) {

		 
        if(!isSimStudentMode() || !(getMissController().getSimSt().isInputCheckerDefined()))
        	return true;

        Vector<String> selection = (Vector<String>) mo.getProperty("Selection");
        Vector<String> action = (Vector<String>) mo.getProperty("Action");
        Vector<String> input = (Vector<String>) mo.getProperty("Input");
    
        InputChecker checker = getMissController().getSimSt().getInputChecker();
  
        
        String selectionString = "";
        String actionString = "";
        String inputString = "";
        if(selection.size() > 0 && selection.get(0) instanceof String)
        	selectionString = (String) selection.get(0);
        if(action.size() > 0 && action.get(0) instanceof String)
        	actionString = (String) action.get(0);
        if(input.size() > 0 && input.get(0) instanceof String)
        	inputString = (String) input.get(0);

        /*do not invoke input checker when a) done is clicked && when student enters something in quiz  */
        if (selectionString.equalsIgnoreCase("Done")) return true;
       // if (getMissController()!=null && getMissController().getSimSt()!=null && getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()) return true; 
       
        
        
        String[] foaValues = null;
        if(getMissController().getSimSt().getFoaGetter() != null)
        {
        	if(runType.equals("springBoot")) {
        		Vector<Object> vFoa = getMissController().getSimSt().getFoaGetter().foaGetter(this, selectionString, actionString, inputString, null);
            	foaValues = new String[vFoa.size()];
            	int count = 0;
            	for(Object elt: vFoa) {
            		foaValues[count++] = elt.toString();
            	}
        	} else {
        		Vector<Object> vFoa = getMissController().getSimSt().getFoaGetter().foaGetter(this, selectionString, actionString, inputString, null);
        		foaValues = new String[vFoa.size()];
        		int count = 0;
        		for(Object obj:vFoa)
        		{
        			if(!(obj instanceof TableExpressionCell))
        			{
        				foaValues[count] = "";
        			}
        			else
        			{
        				TableExpressionCell cell = (TableExpressionCell) obj;
        				foaValues[count] = cell.getText();
        			}
        			count++;
        		}
        	}
        }
        
        if(!checker.checkInput(selectionString, inputString, foaValues, this))
        {
        	

        	//Fail!  Bad input!
        	if (trace.getDebugCode("ss")) trace.out("ss", "Invalid input");
        	String newInput = checker.interpret(selectionString, inputString, foaValues);
       
        	//trace.err("Lets see if we have a missing step");
        	if (!checker.checkSkipStep(selectionString))	{
        		newInput=null;
        	}

        	
        	trace.err("bad input, new input fore selectionString " +  selectionString + "  is " + newInput);
        	
        	if(newInput != null )
        	{	   	 	
        		
        		//if (inputString.contains("by") || inputString.contains("from")){
        		//	String message="Because "+inputString+" is the same as "+ newInput +", I will change it.";
        		//	new SimStMessageDialog(new Frame(),this.getMissController().getSimStPLE().getSimSt().getSimStLogger(), message);
        		//}
        		//else {
        		//	String message="You typed "+inputString+" but I think you meant "+ newInput +", so I will change it.";
        		//	new SimStMessageDialog(new Frame(),this.getMissController().getSimStPLE().getSimSt().getSimStLogger(), message);
        		//}
        		
        		input.set(0, newInput);
        		mo.setProperty("Input", input);
        		Object select = this.lookupWidgetByName(selectionString);
        		if(select != null && select instanceof TableExpressionCell)
        		{
        			TableExpressionCell cell = (TableExpressionCell) select;
        			cell.setText(newInput);
        		}
        		if (trace.getDebugCode("ss")) trace.out("ss", "Input changed to valid: "+newInput);
        		getMissController().getSimSt().invalidInput(selectionString,inputString,newInput, foaValues);
        	}
        	else
        	{      		
        		getMissController().getSimSt().invalidInput(selectionString,inputString,"", foaValues);
        		return false;
        	}
        }
    	
        return true;
	}
	
	/**
	 * FIXME delete when interfaces support MsgType#GET_ALL_INTERFACE_DESCRIPTIONS
	 */
	private String bundleRedirectType = null;
	public void redirectNextBundle(String msgType) {
		bundleRedirectType = msgType;
	}

	/**
	 * Process a bundle of messages. These are individual XML elements
	 * inside a single &lt;properties&gt; element.
	 * @param mo message with bundle
	 * @return result of {@link #handleBundle(MessageObject, String) handleBundle(mo, null)}
	 */
	private void handleBundle(MessageObject mo) 
	{
		debug ("handleBundle () bundleRedirectType "+bundleRedirectType);
		
		// FIXME delete the following code when interfaces name message bundles
		if(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS.equals(bundleRedirectType)) {
			getUniversalToolProxy().handleGetAllInterfaceDescriptions(mo);
			bundleRedirectType = null;
			return;
		}
		
		List<MessageObject> messages = UniversalToolProxy.unbundle(mo);
		if(messages == null)
			return;
		debug ("There are " + messages.size () + " messages in this bundle");

		int i = 0, nIntDescs = 0;
		for (MessageObject msg : messages) {
			try {
				if(MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(msg.getMessageType()))
					nIntDescs++;
				handleCommMessage(msg);
				i++;
			} catch (Exception e) {
				trace.errStack("Error on bundled msg["+i+"]:\n  "+mo, e);
			}
		}
	}

    /**
     * Reply with version information to a VersionInfo request.
     * @param mo
     * @param diagnostic
     */
    private void handleVersionInfo(MessageObject mo, String diagnostic) {
        if (utp == null)
        	return;
    	MessageObject resp = MessageObject.create("VersionInfo", "SendNoteProperty");
    	resp.setProperty("Version", edu.cmu.pact.Utilities.VersionInformation.RELEASE_NAME);
		resp.setProperty("BuildNumber", edu.cmu.pact.Utilities.VersionInformation.VERSION_NUMBER);
		resp.setProperty("BuildDate", edu.cmu.pact.Utilities.VersionInformation.BUILD_DATE);
		if(diagnostic == null || diagnostic.length() < 1) {
			String[] ntpServer = new String[1];
			Date ntpDate = getNTPDate(ntpServer);
			if (ntpDate != null) {
				resp.setProperty("NtpDate", VersionInfoDateFmt.format(ntpDate));
				resp.setProperty("NtpMillis", Long.toString(ntpDate.getTime()));
				resp.setProperty("NtpServer", ntpServer[0] == null ? "null" : ntpServer[0]);
			}
		} else {
			resp.setProperty("Diagnostic", diagnostic);
			resp.setProperty("ReceiptTime", VersionInfoDateFmt.format(new Date()));
		}
		utp.handleMessage(resp);
	}
    
    /**
     * Get the current time from the NTP server if available.
     * @param ntpServer to return server source for this timestamp
     * @return {@link NtpClient#getNTPDate()} if {@link #getLauncher()} has a NtpClient
     */
    private Date getNTPDate(String[] ntpServer) {
		SingleSessionLauncher launcher = getLauncher();
		if (launcher != null) {
			NtpClient ntpClient = launcher.getNtpClient();
			if (ntpClient != null)
				return ntpClient.getNTPDate(ntpServer);
		}
		return null;
	}

	/**
     * Send the current ProblemSummary response.
     */
    private void handleProblemSummaryRequest(MessageObject o) {
    	MessageObject resp;
    	if (getCtatModeModel().isDefiningStartState())
    		resp = ProblemModel.makeEmptyProblemSummaryResponse("Error - Defining Start State");
    	else if (getCtatModeModel().isDemonstratingSolution())
    		resp = ProblemModel.makeEmptyProblemSummaryResponse("Error - In Demonstrate Mode");
    	else if (getProblemModel() == null)
    		resp = ProblemModel.makeEmptyProblemSummaryResponse("Error - No Problem Loaded");
    	else
    		resp = getProblemModel().handleProblemSummaryRequest();
    	handleMessageUTP(resp, o);
	}

	/**
     * Handle a directive to undo the last N steps in the student interface
     * and tutor.
     * @param propertyNames
     * @param propertyValues
     */
    private void handleRetractSteps(MessageObject mo) {
        Integer nSteps = null;
        try {
        	nSteps = Integer.valueOf((String)mo.getProperty("NumberOfSteps"));
        } catch (Exception e) {
        	trace.err("error getting nSteps from message: "+e+
        			(e.getCause() == null ? "" : e.getCause().toString()) + ";\n  "+mo);
        }
        if (nSteps == null)
        	return;
		if (getProcessTraversedLinks() != null)
			getProcessTraversedLinks().retractLinksFromTail(nSteps.intValue());
	}

	/**
     * Set preferences and properties according to the SetPreferences message.
     * @param propertyNames names vector from message
     * @param propertyValues values vector from message
     * @since CTAT 2.0
     */
    private void handleSetPreferencesMessage(MessageObject mo) {
    	
		getLauncher().editSetPreferences(mo);  // may alter message for collaboration
    	
    	if(getUniversalToolProxy() != null)
    		getUniversalToolProxy().awaitSetPreferences(false);  // no longer waiting

    	{                                                      // always get the Comm Shell version
    		Object prop;                 
    		if(null != (prop = mo.getProperty(COMM_SHELL_VERSION)))
    			commShellVersion = prop.toString();
    		if(null != (prop = mo.getProperty(UniversalToolProxy.HTML5)))
    			getUniversalToolProxy().setStoreAllInterfaceDescriptions(Boolean.parseBoolean(prop.toString()));
    	}
    	
    	// N.B.: must set commShellVersion before calling
    	// getAllInterfaceDescriptions(), which is client-dependent
    	
    	if(!Utils.isRuntime()) {
    		if(getUniversalToolProxy() == null)
    			trace.err("BR_Controller.handleSetPreferencesMessage("+mo.summary()+"): utp is null");
    		else {
    			if(!getUniversalToolProxy().hasInterfaceDescriptions())
    				getUniversalToolProxy().getAllInterfaceDescriptions();
    			if(isAcceptingStartStateMessages()) {
    				getUniversalToolProxy().handleStartStateMessageFromInterface(mo);
    	    		return;
    			}
    		}
    	}

		String brdFile = null;  // problem file to load, from ProblemName preference
		String problemName = null;     // problem name: set this after loading problem
		Skills skills = null;

//propertyNames.add("TutoringServiceMode");       // !!mode
//propertyValues.add(CtatModeModel.JESS_MODE);    // !!mode

		List<String> propertyNames = mo.getPropertyNames();
	    for (int i = 0; i < propertyNames.size(); ++i) {

	    	String name = (String) propertyNames.get(i);
			Object obj = mo.getProperty(name);
    		String value = (obj == null ? null : obj.toString());
    		
        	if (trace.getDebugCode("sp")) trace.outNT("sp", "BR_Controller.handleSetPreferencesMessage["+i+"] "+
        			name+", type "+
        			(obj == null ? "null" : obj.getClass().getSimpleName())+" = "+value);
        	if (trace.getDebugCode("miss")) trace.out("miss", "BR_Controller.handleSetPreferencesMessage["+i+"]\n  "+
        			name+" = "+value);
    		if (name == null || name.equals("MessageType"))
    			continue;

    		if (name.equalsIgnoreCase("ProblemName"))
    			brdFile = value;
    		else if (name.equalsIgnoreCase(Logger.QUESTION_FILE_PROPERTY))
    				brdFile = value;
    		else if (name.equalsIgnoreCase(Logger.PROBLEM_NAME_PROPERTY)) {
    			problemName = value;
    			getLogger().setProblemName(problemName);
    		}
    		else if (name.equalsIgnoreCase(Logger.PROBLEM_TUTOR_FLAG_PROPERTY))
    			getLogger().setProblemTutorFlag(value);
    		else if (name.equalsIgnoreCase(Logger.PROBLEM_OTHER_FLAG_PROPERTY))
    			getLogger().setProblemOtherFlag(value);
    		else if (name.equalsIgnoreCase(Logger.PROBLEM_CONTEXT_PROPERTY))
    			getLogger().setProblemContext(value);
    		else if (name.equalsIgnoreCase(Logger.CONTEXT_MSG_ID_PROPERTY))
    			getLogger().setContextMessageId(value);
    		else if (name.equalsIgnoreCase(Logger.SOURCE_ID_PROPERTY))
    			;
    		else if (name.equalsIgnoreCase(Logger.AUTH_TOKEN_PROPERTY))
    			getLoggingSupport().setAuthToken(value);
    		else if (name.equalsIgnoreCase(Skills.SKILLS)) {
    			try {
    				if (obj instanceof Element)
    					skills = Skills.factory((Element) obj);
    				else if (obj instanceof List)
    					skills = Skills.factory((List) obj);
    				if(skills != null)
    				{
    					skills.setExternallyDefined(true);
    					if (trace.getDebugCode("skills"))
    						trace.out("skills", "handleSetPreferences skills obj "+obj+
    								", after factory n="+skills.size()+", skills:\n  "+skills.toXMLString());
    				}
    			} catch (Exception e) {
    				trace.err("Error getting skills from SetPreferences: "+e+
    						(e.getCause() == null ? "" : "; cause "+e.getCause()));
    			}
    		}
    		else if (name.equalsIgnoreCase(Logger.STUDENT_NAME_PROPERTY)) {
    			getLoggingSupport().setStudentName(value);
	    	} else if (name.equalsIgnoreCase("log_to_remote_server")) {
	    		//Ignore .sfw set preferences for logging at Author time
	    		if(Utils.isRuntime())
	    			getTSLauncherServer().getPreferencesModel().setBooleanValue(USE_OLI_LOGGING, Boolean.valueOf(value));
	    	} else if (name.equalsIgnoreCase(Logger.LOG_SERVICE_URL_PROPERTY)) {
	    		//Ignore .sfw set preferences for logging at Author time
	    		if(Utils.isRuntime())
		    		getTSLauncherServer().getPreferencesModel().setStringValue(OLI_LOGGING_URL, value);
    		}
    		else if (name.equalsIgnoreCase("log_to_disk")) {
    			//Ignore .sfw set preferences for logging at Author time
	    		if(Utils.isRuntime())
	    			getTSLauncherServer().getPreferencesModel().setBooleanValue(USE_DISK_LOGGING, Boolean.valueOf(value));
    		} else if (name.equalsIgnoreCase(Logger.DISK_LOG_DIR_PROPERTY))
    			//Ignore .sfw set preferences for logging at Author time
	    		if(Utils.isRuntime())
	    			getTSLauncherServer().getPreferencesModel().setStringValue(DISK_LOGGING_DIR, value);
    		else if (name.equalsIgnoreCase(Logger.SESSION_ID_PROPERTY)) {
    			getLoggingSupport().setSessionId(value);
    		} else if (name.equalsIgnoreCase(Logger.SCHOOL_NAME_PROPERTY)) {
    			getLogger().setSchoolName(value);
    		} else if (name.equalsIgnoreCase(Logger.CLASS_NAME_PROPERTY)) {
    			getLogger().setClassName(value);
    		} else if (name.equalsIgnoreCase(Logger.CLASS_PERIOD_PROPERTY))
    			getLogger().setClassPeriod(value);
    		else if (name.equalsIgnoreCase(Logger.CLASS_DESCRIPTION_PROPERTY))
    			getLogger().setClassDescription(value);    		
    		else if (name.equalsIgnoreCase(Logger.DATASET_NAME_PROPERTY))
    			getLogger().setDatasetName(value);
    		/** Note: At the moment, flash only sends us one instructor, so we always add them at index 0 */
    		else if (name.equalsIgnoreCase(Logger.INSTRUCTOR_NAME_PROPERTY))
    			getLogger().addInstructorName(value, 0);
    		else if (name.equalsIgnoreCase(Logger.COURSE_NAME_PROPERTY))
    			getLogger().setCourseName(value);
    		else if (name.equalsIgnoreCase(Logger.UNIT_NAME_PROPERTY))
    			getLogger().setUnitName(value);
    		else if (name.startsWith(Logger.CUSTOM_FIELD_NAME))
    		{
    			int index = new Integer(name.split(Logger.CUSTOM_FIELD_NAME)[1]).intValue();
    			getLogger().addCustomFieldName(value,index);
    		}
    		else if (name.startsWith(Logger.CUSTOM_FIELD_VALUE))
    		{
    			int index = new Integer(name.split(Logger.CUSTOM_FIELD_VALUE)[1]).intValue();
    			getLogger().addCustomFieldValue(value,index);
    		}    		
    		else if (name.startsWith(Logger.STUDY_CONDITION_NAME))
    		{
    			int index = new Integer(name.split(Logger.STUDY_CONDITION_NAME)[1]).intValue();
    			getLogger().addStudyConditionName(value,index);
    			getProblemModel().checkHintRandomization(value);
    		}
    		else if (name.startsWith(Logger.STUDY_CONDITION_TYPE))
    		{
    			int index = new Integer(name.split(Logger.STUDY_CONDITION_TYPE)[1]).intValue();
    			getLogger().addStudyConditionType(value,index);
    		}
    		else if (name.startsWith(Logger.STUDY_CONDITION_DESCRIPTION))
    		{
    			int index = new Integer(name.split(Logger.STUDY_CONDITION_DESCRIPTION)[1]).intValue();
    			getLogger().addStudyConditionDescription(value,index);
    		}
    		else if (name.startsWith(Logger.DATASET_LEVEL_NAME))
    		{
    			int index = new Integer(name.split(Logger.DATASET_LEVEL_NAME)[1]).intValue();
    			getLogger().addDatasetLevelName(value,index + 1);
    		}
    		else if (name.startsWith(Logger.DATASET_LEVEL_TYPE))
    		{
    			int index = new Integer(name.split(Logger.DATASET_LEVEL_TYPE)[1]).intValue();
    			getLogger().addDatasetLevelType(value,index + 1);
    		}    		
    		else if (name.equalsIgnoreCase(Logger.SECTION_NAME_PROPERTY))
    			getLogger().setSectionName(value);
    		else if (name.equalsIgnoreCase("TutoringServiceMode"))  // !!mode
    			setBehaviorRecorderMode(value);                     // !!mode
    		else if (name.equalsIgnoreCase(PROBLEM_STATE_STATUS))
    			setProblemStateStatus(value);
    		else if (name.equalsIgnoreCase(Collaborators.COLLABORATORS_SET_PREFS_PROP))
    		{
    			if(trace.getDebugCode("collab"))    // handled in collaborationOK()
    				trace.out("collab", "BR_Ctlr.handleSetPreferences() "+name+" is \""+value+"\"");
    		}
    		else if (name.equalsIgnoreCase(COMM_SHELL_VERSION))
    			;                                   // handled at top of this method
    		else if (name.equalsIgnoreCase(UniversalToolProxy.HTML5))
    			;                                   // handled at top of this method
    		else
    			trace.err("handleSetPreferencesMessage(): unrecognized property "+name);
    		
    	}
	    if(trace.getDebugCode("skills"))
	    	trace.out("skills", "BR_C.handleSetPrefs() commShellVersion "+commShellVersion+", skills "+skills);
	    if (skills != null)
	    	skills.setVersion(commShellVersion);
    	if (brdFile == null) {
    		if(!Utils.isRuntime() && !collaborationOK(mo))
    			return;
        	goToStartState(true, true);  // author time: send current start state after SetPrefs
    	} else {
    		URL problemFileURL = null;
    		if (inAppletMode()) {
				URL codeBase = JSBridge.getCodeBaseURL();
				try {
					problemFileURL = new URL(codeBase, brdFile);
				} catch (MalformedURLException e1) {
    				trace.errStack("handleSetPreferences(): error on new URL("+codeBase+", "+brdFile+")", e1);
				}
    		} else if (brdFile.startsWith("http://") || brdFile.startsWith("https://")) {
    			try {
    				problemFileURL = new URL(brdFile);
    			} catch (MalformedURLException e1) {
    				trace.errStack("handleSetPreferences(): error converting brdFile \""+brdFile+"\" to URL", e1);
    			}
    		}
    		if (problemFileURL == null)
    			problemFileURL = Utils.getURL(brdFile, this);
    		
    		if (trace.getDebugCode("applet")) trace.out("applet","brd url: "+problemFileURL);
    		
    		//set projects directory to load jess files ~eep
    		if (trace.getDebugCode("eep")) trace.out("eep","brdfile"+brdFile+"parent:"+new File(brdFile).getParent() );
    		getTSLauncherServer().getPreferencesModel().setStringValue(PROJECTS_DIR, new File(brdFile).getParent() );
    		
    		if (trace.getDebugCode("sp")) trace.out("sp", "handleSetPreferences(): brdFile "+brdFile+", problemFileURL "+
    				problemFileURL);
    		if (problemFileURL != null)
    		{
    			// Possibly pause here for collaborators; client will wait for SetPrefs response.
    			if(!collaborationOK(mo))
    				return;
    			
    			synchronized(this)
    			{
        			getRuleProductionCatalog().clear();  // each problem to specify own skills
    				boolean success = openBRFromURL(problemFileURL.toString(), problemName, skills); 
    				//eep ^ eventaully calls ProblemStateReaderJDom
    				if(!success && !brdFile.toLowerCase().startsWith("http"))
    				{
    					// If the BRD could not be opened, it may be encrypted. The decrypted form might be
    					// obtained by requesting the file from the local server using HTTP.
						String lastDitchURL = "http://localhost:8080/" + brdFile;
    					try {
    						problemFileURL = new URL(lastDitchURL);
    						success = openBRFromURL(problemFileURL.toString(), problemName,skills);
    					} catch (Exception e) {
    						trace.err("Error opening brd from url \""+lastDitchURL+"\":\n  "+e+"; cause "+e.getCause());
    					}
    				}
    				
    				if (success) {
    					if (launcher != null && launcher.getLauncherServer() != null)
    						launcher.getLauncherServer().updateTransactionInfo(getSessionId(), Boolean.FALSE);
    					return;
    				}
    			}
    		}
    		MessageObject newMessage = MessageObject.create(MsgType.TUTORING_SERVICE_ERROR, "SendNoteProperty");
    		newMessage.setProperty("ErrorType", "Load Problem Error");
    		newMessage.setProperty("Details", "File Not Found: " + brdFile);
    		if (trace.getDebugCode("sp"))
    			trace.out("sp", "Cannot find BRD, sending error message through utp "+trace.nh(utp)+
    					":\n  "+newMessage);
    		sendStartStateMsg(newMessage, newMessage.getMessageType());
    	}
	}
    
	private boolean collaborationOK(MessageObject setPrefs) {
		if (getLauncher() == null)      // possible only when launched from tutoring service
			return true;
		try {
			Collaborators collabs = getLauncher().checkForCollaborators(setPrefs);
			if(trace.getDebugCode("collab"))
				trace.out("collab", "checkForCollaborators()=>"+collabs);
			return true;
		} catch(Exception e) {
			trace.errStack("Error from checkForCollaborators(): "+e, e);
			String errorResponse = "Error in collaboration attempt: "+e;
			if(e instanceof Collaborators.NotReadyException) {
				errorResponse = "The following collaborators are absent: "+
						((Collaborators.NotReadyException) e).absences+". Please reload the tutor.";
				trace.err("errorResponse: "+errorResponse);
			}
			MessageObject newMessage = MessageObject.create("TutoringServiceError", "SendNoteProperty");
			newMessage.setProperty("ErrorType", "Collaboration Error");
			newMessage.setProperty("Details", errorResponse);
			handleMessageUTP(newMessage);
			return false;
		}
	}
	/**
     * 
     */
    private void handleGoToInitialStateMessage() {
        goToStartStateForRuleTutors();
    }

    private void handleGoToStateMessage(MessageObject mo) {
        Vector stepNameVec = (Vector) mo.getProperty("StateName");
        String stateName = (String)stepNameVec.get(0);
        ProblemNode problemNode = getProblemModel().getProblemGraph().getNode(stateName);
        goToState(problemNode);
    }
    
    /**
     * @param mo
     */
    private void handleIncorrectActionMessage(MessageObject mo) {

        if (! (getCtatModeModel().isJessMode() || getCtatModeModel().isTDKMode()
        		|| studentInterface instanceof CTATTool))
        	return;
        Vector<String> selection = mo.getSelection();
        Vector<String> action = mo.getAction();
        Vector<String> input = mo.getInput();
        if (trace.getDebugCode("br")) trace.out("br", "handleIncorrectActionMessage: " + " selection = " + selection
        		+ " input = " + input + " action = " + action);

        sendIncorrectActionMsg(selection, input, action, false);
    }

    /**
     * 
     */
    private void handleShowLoginWindowMessage() {
        utp.showLogin();
    }

    /**
     * @param mo
     */
    private void handleIsTutorVisibleMessage(MessageObject mo) {
        Boolean visible = mo.getPropertyAsBoolean("IsVisible");
        getStudentInterface().setVisible(visible == null ? true : visible.booleanValue());
    }

    /**
     * @param mo
     */
    private void handleSetBRDTempDirectoryMessage(MessageObject mo) {
        String BrdTempDirectory = (String) mo.getProperty("BRDTMPDIRECTORY");
        if (BrdTempDirectory != null) {
            setTutorModeBrdTempDirectory(BrdTempDirectory);
        } else
            if (trace.getDebugCode("br")) trace.out("br", "BrdTempDirectory is null");
    }

    /**
     * 
     */
    private void handleQuitWithoutSaveMessage() {
        if (getStudentInterface() instanceof TutorWindow)
            ((TutorWindow) getStudentInterface()).doLogout(false, false);
        else
            closeApplication(false);
    }

    /**
     * 
     */
    private void handleQuitMessage() {
        if (getStudentInterface() instanceof TutorWindow)
            ((TutorWindow) getStudentInterface()).doLogout();
        else
            closeApplication(true);
    }

    /**
     * @param mo
     */
    private void handleLoadProblemWithHTTPMessage(MessageObject mo) {
        String url = (String) mo.getProperty("ProblemName");
        trace.out("loading problem file from url " + url);
        openBRFromURL(url);
    }

    /**
     * 
     */
    private void handleStartNewProblemMessage() {
        startNewProblem(); // move this?
        CtatFrameController cfController = getCtatFrameController();
        if(cfController.getDockedFrame().getCtatMenuBar() != null)
        	cfController.getDockedFrame().getCtatMenuBar().enableCreateStartStateMenus(true);
    }

        /* BR_Panel removal 8-1-08 ko
         * if (brPanel.getHandler() != null)
            brPanel.getHandler().enableCreateStartStateMenus(true);
    }

    /**
     * @param mo
     */
    private void handleCreateStartStateMessage(MessageObject mo) {
        String startStateName = (String) mo.getProperty("StartStateName");

        ProblemModel pm = getProblemModel();
        if (pm.getStartNodeCreatedFlag()) {
            trace.out(5, this, "StartState has been created already");
//                return;
        }

        if (startStateName == null)
            startStateName = "StartState";
        try {
        	createStartStateInternal(startStateName.trim(), !(getCtatModeModel().isExampleTracingMode()));
        } catch(ProblemModelException pme) {
        	trace.errStack("BR_Controller.handleCreateStartStateMessage() error creating start state "+
        			pme+"; msg was\n  "+mo, pme);
        }
    }

    /**
     * @param mo
     */
    private void handleCorrectActionMessage(MessageObject mo) {

    	if (! (getCtatModeModel().isJessMode() || getCtatModeModel().isTDKMode()
        		|| studentInterface instanceof CTATTool))
            return;
        
        Vector selection = mo.getSelection();
        Vector action = mo.getAction();
        Vector input = mo.getInput();
        Integer uniqueID = mo.getPropertyAsInteger("uniqueID");
        if (trace.getDebugCode("br")) trace.out("br", "handleCorrectActionMessage: " + " selection = " + selection
        		+ " input = " + input + " action = " + action + " uniqueID = " + uniqueID);

        if (uniqueID.intValue() > -1) {
        	ProblemModel pm = getProblemModel();
            ProblemEdge edge = pm.getEdge(uniqueID.intValue());

            // switchFontWithCurrNode (edge.getNodes()[Edge.DEST]);
            // currNode = edge.getNodes()[Edge.DEST];

            setCurrentNode(edge.getNodes()[ProblemEdge.DEST]);
            sendCommMsgs(getSolutionState().getCurrentNode(), pm.getStartNode());
        }

        sendCorrectActionMsg(selection, input, action);
    }


    /**
     * @param mo
     */
    private void doLoadBRDPathFileMessage(MessageObject mo) {
        String BRDPathFileName = (String) mo.getProperty("BrdPathFileName");

        trace.out("Load traversed path file: BRDPathFileName = "
                + BRDPathFileName);

        if (BRDPathFileName != null
                && getProcessTraversedLinks() != null)
            getProcessTraversedLinks().loadTraversedLinks_Fromfile(
                    BRDPathFileName);
        else
            trace.out("ERROR: parsing BRDPathFileName value.");
    }

    /**
     * @param propertyNames
     * @param propertyValues
     * @throws FactoryConfigurationError
     */
    private void handleLoadProblemMessage(MessageObject mo) throws FactoryConfigurationError {
        String tempProblemName = (String) mo.getProperty("ProblemName");

        trace.out(5, this, "Loading problem file " + tempProblemName);

        // replace / by File.separator
        if (File.separator.equals("\\")) {
            // replace / by File.separator
            int index = tempProblemName.indexOf("/");
            while (index >= 0) {
                tempProblemName = tempProblemName.substring(0, index)
                        + File.separator + tempProblemName.substring(index + 1);
                index = tempProblemName.indexOf("/");
            }
        } else {
            // replace \ by File.separator
            int index = tempProblemName.indexOf("\\");

            while (index >= 0) {
                tempProblemName = tempProblemName.substring(0, index)
                        + File.separator + tempProblemName.substring(index + 1);
                index = tempProblemName.indexOf("\\");
            }
        }

        // parse the problem name
        String problemName = tempProblemName;

        int index = problemName.lastIndexOf(File.separator);
        if (index >= 0)
            problemName = problemName.substring(index + 1);

        trace.out(5, this, "after parsing: the problemName = " + problemName);

        File f = new File(tempProblemName);

        try {
            if (f.exists() && f.isFile()) {
                reset();
                if (openBRDFileAndSendStartState(tempProblemName, null, null)) {
                    if (getCtatModeModel().isTDKMode())
                    	getProblemModel().setProblemLoadedFromLispTutor(true);
                    getProblemModel().setProblemName(problemName);
                } else {
                    trace.err("loading file " + tempProblemName + " fails!!!");

                    return;
                }

            } else if (tempProblemName.indexOf(".brd") <= 0) {
                trace.err("file " + tempProblemName + " .");
                f = new File(tempProblemName + ".brd");

                if (f.exists() && f.isFile()) {
                    reset();
                    if (openBRDFileAndSendStartState(tempProblemName + ".brd", null, null)) {
                        if (getCtatModeModel().isTDKMode())
                        	getProblemModel().setProblemLoadedFromLispTutor(true);
                        getProblemModel().setProblemName(problemName);
                    } else {
                        trace.err("loading file " + tempProblemName
                                + ".brd fails!!!");

                        return;
                    }

                } else {
                 //  trace.err("file " + tempProblemName + ".brd is not found."); 
                    trace.err("file " + f.getAbsolutePath()  + " is not found."); // chc modified to show physical path

                    return;
                }
            }
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
            return;
        }

        // 05/03/04: upon Chang's request: this is the
        // indicator the student is logged in.
        // initial ProcessTraversedLinks
        // 03/23/07 JS: moved to problem initialization: see reset()
//        if (getCtatModeModel().isTDKMode()) {
//            if (processTraversedLinks == null)
//                processTraversedLinks = new ProcessTraversedLinks(this);
//
//            trace.out("initTraversedLinks()");
//            getProcessTraversedLinks().initTraversedLinks();
//        }
    }

    public ProcessTraversedLinks getProcessTraversedLinks() {
        return processTraversedLinks;
    }

    /** The parser for the grammar used by {@link ExpressionMatcher}. */
	private Parser formulaParser = null;

	/** See {@link #isStudentInterfaceLocal()}. */
	private boolean studentInterfaceLocal = true;

	/** 
	 * Public access to the parser for the grammar used by {@link ExpressionMatcher}.
	 * @return {@link #formulaParser}
	 */
    public Parser getFormulaParser() {
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "BR_Controller.getFormulaParser() to rtn "+formulaParser);
		if (formulaParser != null) 
			return formulaParser;
		synchronized(this) {
			try {
				formulaParser = new MemorySerializedParser().get(CTATFunctions.getRules(), "CTATFunctions");
		    	if (trace.getDebugCode("functions")) trace.printStack("functions", "new formula parser "+formulaParser);
			} catch (Exception e) {
				trace.err("Error creating formula parser: "+e);
				e.printStackTrace();
				formulaParser = null;
			}
		}
		return formulaParser;
	}
    
    /**
     * If {@link #hintMessagesManager} is null, creates an instance of 
     * {@link HintMessagesManagerStub} if on Android, else {@link HintMessagesManagerImpl}.
     * @return {@link #hintMessagesManager} 
     */
    public HintMessagesManager getHintMessagesManager() 
    {
    	if (hintMessagesManager == null) {
    		if (CTATEnvironment.isLocalAndroid())
    			hintMessagesManager = new HintMessagesManagerStub();
    		else
    			hintMessagesManager = new HintMessagesManagerImpl(this);
    	}
        return hintMessagesManager;
    }

    /**
     * Create the start state node and populate the start state message list. Equivalent to
     * {@link #createStartState(String, false)}; does not wait for InterfaceDescriptions.
     * @param problemName if null, ask the author for a name 
     * @param wantInterfaceDescriptions if true, may have to wait for response: argument for
     *        {@link ProblemModel#loadStartStateMessages(Vector, UniversalToolProxy, boolean)}
     * @return object with node info; null if operation aborted
     * @throws ProblemModelException
     */
    public NodeView createStartState(String problemName)
    		throws ProblemModelException 
    {
    	return createStartStateInternal(problemName, false);
    }

    /**
     * Create the start state node and populate the start state message list.
     * @param problemName if null, ask the author for a name 
     * @param wantInterfaceDescriptions if true, may have to wait for response: argument for
     *        {@link ProblemModel#loadStartStateMessages(Vector, UniversalToolProxy, boolean)}
     * @param callBack if not null, call its {@link WillNotifyListeners#notifyListeners()} method
     * @return object with node info; String if operation queued; null if operation ok but no graph
     * @throws ProblemModelException
     */
	public Object createStartState(final String problemName, final boolean wantInterfaceDescriptions,
    		final WillNotifyListeners callBack) throws ProblemModelException 
    {
    	UniversalToolProxy utp;
    	if((utp = getUniversalToolProxy()) == null
    			|| !utp.getStudentInterfaceConnectionStatus().isConnected()
    			|| utp.hasInterfaceDescriptions())
    		return createStartStateInternal(problemName, wantInterfaceDescriptions);
    	
    	StartStateModel.Listener createStartStateInternalListener = new StartStateModel.Listener() {
			/**
			 * Enqueue a call to {@link BR_Controller#createStartStateInternal(String, boolean)}.
			 * @param evt
			 * @see edu.cmu.pact.ctat.model.StartStateModel.Listener#startStateReceived(java.util.EventObject)
			 */
			public void startStateReceived(EventObject evt) {
    			try {
    				createStartStateInternal(problemName, wantInterfaceDescriptions);
    				getCtatModeModel().setAuthorMode(CtatModeModel.DEMONSTRATING_SOLUTION);
    			} catch(ProblemModelException pme) {
    				pme.printStackTrace();
    			}
    			if(callBack != null)
    				callBack.notifyListeners();
			}
		};
		utp.addStartStateListener(createStartStateInternalListener);
		utp.awaitInterfaceDescriptions(true);
    	if(trace.getDebugCode("startstate"))
    		trace.out("startstate", "*! BR_Ctlr.createSS("+problemName+") queued createStartStateInternalListener");
		utp.getAllInterfaceDescriptions();
    	return "Queued awaiting InterfaceDescriptions";
    }

    /**
     * Create the start state node and populate the start state message list.
     * @param problemName if null, ask the author for a name 
     * @param wantInterfaceDescriptions if true, may have to wait for response: argument for
     *        {@link ProblemModel#loadStartStateMessages(Vector, UniversalToolProxy, boolean)}
     * @return object with node info; null if operation aborted
     * @throws ProblemModelException
     */
    private NodeView createStartStateInternal(String problemName, boolean wantInterfaceDescriptions)
    		throws ProblemModelException 
    {
        if (trace.getDebugCode("startstate"))
        	trace.out("startstate","*! entered createStartState: problemName = " + problemName);
        
        if (getProblemModel().getStartNodeCreatedFlag()) {
            throw (new ProblemModelException(
                    "A start state has already been created for this problem model."));
        }

        if (trace.getDebugCode("br")) trace.out("br", "problem name = \""+problemName+"\"");
        if (problemName == null)
        	return null;

        if (!Utils.isRuntime() && !ProblemModel.checkForValidProblemName(problemName))
            throw new ProblemModelException("The problem name: \"" + problemName
                    + "\" is empty or has unallowed characters.");

        setSemanticEventId(MessageObject.makeTransactionId());  // transaction id for start state msgs
        getProblemModel().setProblemName(problemName);
        if(Utils.isRuntime() && (problemName == null || problemName.length() < 1))
        	return null;
       
        
        CtatFrameController cfController = getCtatFrameController();

        if (cfController != null && cfController.getDockedFrame() != null) {
        	cfController.getDockedFrame().getCtatMenuBar().enableCreateStartStateMenus(false);
        	cfController.getDockedFrame().getCtatMenuBar().enableGotoStartStateMenus(true);
        	cfController.getDockedFrame().getCtatMenuBar().enablePrintGraphMenus(true);
        	cfController.getDockedFrame().getCtatMenuBar().enableSaveGraphMenus(true);
        }
        // Fri Jul 29 11:04:38 2005: Noboru
        // To get a ProblemNode for SimSt, a NodeView must be created
        // at this point.
    
        NodeView nodeView = createStartStateNode(problemName);
     
        
        // Fri May 20 17:16:47 2005: Noboru
        // Notify Miss Controller about the new problem
        if (getCtatModeModel().isSimStudentMode()) 
        {
            getLauncher().getMissController().startStateCreated(nodeView.getProblemNode());
        }
        
      
        Vector<MessageObject> messageVector = new Vector<MessageObject>();     		
        utp.createCurrentStateVector(messageVector, problemName, wantInterfaceDescriptions);
        getProblemModel().loadStartStateMessages(messageVector, utp);		
        getProblemModel().setStartNodeCreatedFlag(true);
        getProblemModel().fireProblemModelEvent(new NodeCreatedEvent(this, getProblemModel().getStartNode()));

        setStartStateSent(true);

        getProblemModel().setProblemName(problemName);

        sendStartStateCreatedMsg(problemName);
 
        setStartStateModified(false);
 
  
        if (getCtatModeModel().isDefiningStartState())
        	getCtatModeModel().setAuthorMode(CtatModeModel.DEMONSTRATING_SOLUTION);
      
        updateStatusPanel("Select File->'Save Graph As ...' to save the problem");
       
        return nodeView;
    }

    public AbstractCtatWindow getActiveWindow() {
        return getServer().getActiveWindow();
    }
    
    /**
     * Roll back font for each element in {@link SolutionState#getUserVisitedEdges()}.
     */
    void sendResetBeforeTraverseToClickedNode() {
        
        if(trace.getDebugCode("ss"))
        	trace.out("ss", "BR_Ctlr.sendResetBeforeTraverseToClickedNode() getSolutionState() "+
        			getSolutionState()+", getUserVisitedEdges() "+
        			(getSolutionState() == null ? null : getSolutionState().getUserVisitedEdges()));
        for (int i = 0; i < getSolutionState().getUserVisitedEdges().size(); i++) {
            ProblemEdge tempEdge = getSolutionState().getUserVisitedEdges().get(i);
            EdgeData tempMyEdge = tempEdge.getEdgeData();

            sendResetActionMsg(tempMyEdge.getSelection());
        }
        getSolutionState().resetUserVisitedEdges();
    }

    // ////////////////////////////////////////////////////////////////////
    /**
     * if the atEdge is in some linkGroup and not the endLink 1. update the
     * current croup as the matched linkGroup; 2. return true; otherwise retur
     * false.
     */
 /*   public// ///////////////////////////////////////////////////////////////////
    boolean isInSomeGroup_NotEndLink(ProblemEdge atEdge) {

        Vector singleGroup;

        // links in linksGroups
        ProblemEdge groupStartEdge;

        for (int i = 0; i < getProblemModel().getLinksGroups().size(); i++) {
            singleGroup = (Vector) getProblemModel().getLinksGroups()
                    .elementAt(i);

            // don't test the last endLink
            for (int j = 1; j < singleGroup.size() - 1; j++) {
                groupStartEdge = (ProblemEdge) singleGroup.elementAt(j);
                if (groupStartEdge == atEdge) {
                    /*getSolutionState().setCurrentGroup(
                            (Vector) singleGroup.clone());*/

    /*                return true;
                }
            }
        }

        return false;
    }*/

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * process the edge success or buggy message
     * 
     * 1. if the matched edge is Correct and dialogueName is not empty
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public void processEdgeSuccessBuggyMessage(ProblemEdge atEdge) {
        EdgeData atMyEdge = atEdge.getEdgeData();

        String authorIntent = atMyEdge.getActionType();

//        String dialogueName = atMyEdge.getDialogueName().trim();

        if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {

            if (atMyEdge.getDialogueSystemInfo().processInfo(
                    DialogueSystemInfo.STEP_SUCCESSFUL))
                return;

            if (atMyEdge.getSuccessMsg().length() > 0
                    && getCtatModeModel().isExampleTracingMode())
                sendSuccessMsg(atMyEdge.getSuccessMsg());

        } else {

            if (atMyEdge.getDialogueSystemInfo().processInfo(
                    DialogueSystemInfo.STEP_ERROR))
                return;

            if (atMyEdge.getBuggyMsg().length() > 0
                    && getCtatModeModel().isExampleTracingMode()) {

                sendBuggyMsg(atMyEdge.getBuggyMsg(), atMyEdge.getSelection()
                		, atMyEdge.getAction());
            }
        }

        return;
    }

    // //////////////////////////////////////////////////////////////////////
    // Counts the number of done states and returns the next appropriate name
    // for a done state
    // //////////////////////////////////////////////////////////////////////
    public String nextDoneName() {
    	return "Done"; //ko 8-2-08 looks like BRPanel's nodeViewTable 
    	/*
        NodeView v = brPanel.getNodeView("Done");
        if (v == null)
            return "Done";
        int count = 1;
        String nextName = null;
        while (v != null) {
            count++;
            nextName = "Done_" + count;
            v = brPanel.getNodeView(nextName);
        }
        return nextName;
        */
    }


    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * algorithm for selecting help/hint message. one widget can have multiple
     * correct inputs. process hints msg to Interface side
     */
    //todelete
    // ////////////////////////////////////////////////////////////////////////////////
  /*  public void handleAskForHintMessage(String focusWidgetSelectionName) {

        trace.out ("mps", "process Hint Message");
        boolean focusWidgetSelected = true;

        if (focusWidgetSelectionName == null)
            focusWidgetSelected = false;
        else if (focusWidgetSelectionName.equalsIgnoreCase("hint")
                || focusWidgetSelectionName.equalsIgnoreCase("")) {

            trace.out(5, this, "focusWidgetSelectionName = "
                    + focusWidgetSelectionName);
            focusWidgetSelected = false;
        }

        // special treatment for focusWidgetSelectionName is "Done"
        // still keep this checking
        if (focusWidgetSelected
                && focusWidgetSelectionName.equalsIgnoreCase("Done")) {
            focusWidgetSelected = processHintRequestDoneButtonSelected(focusWidgetSelected);
        }

        if (getProblemModel().isUnorderedMode() == false) {
            pseudoTutorMessageHandler.processHintRequestOrderedMode(focusWidgetSelectionName, focusWidgetSelected);
        }
        if (getProblemModel().isUnorderedMode()) {
            pseudoTutorMessageHandler.processHintRequestUnorderedMode(focusWidgetSelectionName, focusWidgetSelected);
        }
    }*/

    /**
     * @param focusWidgetSelected
     * @return
     */
    private boolean processHintRequestDoneButtonSelected(boolean focusWidgetSelected) {
        ProblemEdge tempEdge;
        EdgeData myEdge;
        boolean findFlag = false;
        Vector currSelection;
        String selectionName;
        Enumeration iterEdges = getProblemModel().getProblemGraph()
                .getOutgoingEdges(getSolutionState().getCurrentNode());
        while (iterEdges.hasMoreElements() && !findFlag) {
            tempEdge = (ProblemEdge) iterEdges.nextElement();
            myEdge = tempEdge.getEdgeData();
            currSelection = myEdge.getSelection();
            selectionName = (String) currSelection.elementAt(0);
            if (selectionName.equalsIgnoreCase("Done"))
                findFlag = true;
        }

        // upon Vincent request in this case treat as no focused widget
        // 05/19/04
        if (!findFlag)
            focusWidgetSelected = false;
        return focusWidgetSelected;
    }

    /**
     * Semantic event identifier.
     * 
     * @param id
     *            new value for {@link #semanticEventId}
     */
    public void setSemanticEventId(String id) {
        semanticEventId = (id == null ? "" : id);
    }

    /**
     * Semantic event identifier.
     * 
     * @return value of {@link #semanticEventId}
     */
    public String getSemanticEventId() {
    	if (trace.getDebugCode("mo")) trace.out("mo", "BR.getSemanticEventId() returns "+semanticEventId);
        return semanticEventId;
    }

    /**
     * Convenience method for getting {@link ProblemModel#getProblemSummary()}.
     * @return getProblemModel().getProblemSummary() or null
     */
    public ProblemSummary getProblemSummary()
    {
    	return getProblemModel() != null ? getProblemModel().getProblemSummary() : null;
    }

    /**
     * Equivalent to {@link #handleInterfaceActionMessage(MessageObject, boolean) handleInterfaceActionMessage(o, true)}
     * @param o the message
     */
    private void handleInterfaceActionMessage(MessageObject o) {
    
    	handleInterfaceActionMessage(o, true);
    }

    /**
     * Process an {@value MsgType#INTERFACE_ACTION} message.
     * @param o the message
     * @param fwdToSSM if true, pass o to {@link UniversalToolProxy#handleStartStateMessageFromInterface(MessageObject)}
     */
    public void handleInterfaceActionMessage(MessageObject o, boolean fwdToSSM) {

    	

    	
    	 Vector current_selection = (Vector) o.getProperty("Selection");
    	  	    	 
    	/*if in SimStudent Aplus Cog Tutor mode and start state is finished, then display confirmation window whether to start problem or not*/
    	 if (getMissController()!=null && getMissController().getSimSt()!=null && getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && getMissController().getSimStPLE()!=null && getMissController().getSimStPLE().getIsStartStatecompleted()){

    		 	//int selectedOption = JOptionPane.showConfirmDialog(null, "Is this how you want the problem?", "Choose", JOptionPane.YES_NO_OPTION);  
     			//if (selectedOption == JOptionPane.YES_OPTION) {  			
     				/*set the flags*/
     				getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering=true;
     				getMissController().getSimStPLE().setIsStartStateCompleted(false);
     						
     							
     				/*read the problem name from the interface*/
     				String problemName = getMissController().getSimStPLE().getSsInteractiveLearning().createName(getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
     					 
     				
     				/*disable the start state elements*/
     				getMissController().getSimStPLE().setFocusOfStartStateElements(false);
     				/*update the SimStCognitiveTutor latest given problem variable*/
     				getMissController().getSimStPLE().getSsCognitiveTutor().setLastGivenProblem(SimSt.convertFromSafeProblemName(problemName));		
     				/*give the new problem (just like when in CogTutor-Control)*/
     				getMissController().getSimStPLE().getSsCognitiveTutor().giveProblem(SimSt.convertFromSafeProblemName(problemName));
     				
     				/*enable the restart button*/
     			 	AplusPlatform aplus=((AplusPlatform) this.getMissController().getSimStPLE().getSimStPeerTutoringPlatform());
     			 	//aplus.restartButton.setEnabled(true);
     				aplus.setRestartButtonEnabled(true);
     			 	
     				getMissController().getSimStPLE().setFocusOfStartStateElements(false);

     				/*a new problem is given, no need to proceed.*/
     				return;
     			//}	
    	 }
    	
    	
    	 
    	 /*when solving a quiz in aplus control, all interface actions should be directed here*/
    	 if (getMissController()!=null && getMissController().getSimSt()!=null && getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()){
    		 
    		  Vector selection = (Vector) o.getProperty("Selection");
    	      Vector action = (Vector) o.getProperty("Action");
    	      Vector input = (Vector) o.getProperty("Input");
    		  getMissController().getSimStPLE().getSsCognitiveTutor().processQuizInterfaceAction( (String) selection.get(0), (String) action.get(0), (String) input.get(0));
    		 
    		
    	 }
    			 
    	 
    	 
    	 //else if (getMissController()!=null && getMissController().getSimSt()!=null && getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && getMissController().getSimStPLE()!=null && getMissController().getSimStPLE().getIsStartStatecompleted() && getMissController().getSimStPLE().getSsCognitiveTutor().quizSolving){
    	//	 Vector selection = (Vector) o.getProperty("Selection");
    	
    	//	 if (selection!=null && ((String) selection.get(0)).equalsIgnoreCase("done"))
    	//		 JOptionPane.showMessageDialog(null, "faskelokoukoulwsta");
 
    	//}
    	 
    	 
    	
		if (isAcceptingStartStateMessages()) {
			if(getUniversalToolProxy() != null && fwdToSSM)
				getUniversalToolProxy().handleStartStateMessageFromInterface(o);

    		debug ("Warning: interface actions should go to the start state editor when in start state mode!");
	    	addInterfaceAction (o.getPropertiesElement());
	    	return;
		}
    	

		
		
        getHintMessagesManager().cleanUpHintOnChange();

        trace.out ("br", "handleInterfaceActionMessage: message = " + o);
        Vector selection = (Vector) o.getProperty("Selection");
        Vector action = (Vector) o.getProperty("Action");
        Vector input = (Vector) o.getProperty("Input");
    	
        //updates the saiTable with the most recent InterfaceActionSAI if in authoring mode
        if (!Utils.isRuntime()) {
        	updateSAITable(selection, action, input,
        			Hints.isHintSelection(selection) ? "Hint Request" : "Tutored");
        }

        // FIXME:  add interface variable to variable table
//        addInterfaceVariables(o); //!!!not yet: only on correct? Now called by messageTank.

        setSemanticEventId(o.getTransactionId());

        ProblemNode tempNode = getSolutionState().getCurrentNode();
        
        /* Not sure if I need that to log student action, we will see at the pilot!"
        if (getMissController()!=null && getMissController().getSimSt()!=null){
        	Sai sai=new Sai((String) selection.get(0), (String) action.get(0), (String) input.get(0));        	
        	getMissController().getSimSt().getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.INTERFACE_ACTION,getMissController().getSimSt().getProblemStepString(),"", sai);	
        	
        }
        */
        

        /*When in cogtutor mode, first modeltrace using Oracle to see if SAI correct. If result is incorrect, then do not proceed with
         * hanlding the interface action but return. As a result, whatever student wrote stays red. */
       if (getMissController()!=null && getMissController().getSimSt()!=null && getMissController().getSimSt().isSsCogTutorMode() && !getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()){
  
    	  boolean proceed= getMissController().getSimStPLE().getSsCognitiveTutor().processInterfaceAction( (String) selection.get(0), (String) action.get(0), (String) input.get(0));	
    	  if (!proceed) return;
    	  
       }
       
       

               
        if (getCtatModeModel().isDemonstrateThisLinkMode()) {
        	
        	ProblemEdge currentEdge = getProblemModel().getProblemGraph().
        								lookupProblemEdgeByID(getCtatModeModel().getCurrentEdgeID());
        	currentEdge.getEdgeData().handleDemonstrateThisLinkInput(selection, action, input, 
        			o, EdgeData.CORRECT_ACTION);
        	getCtatModeModel().exitDemonstrateThisLinkMode();
        	return;
        }
        else if (getCtatModeModel().isDefiningStartState()) {
        	getProblemModel().appendStartNodeMessage(o);
        	if(isSimStudentMode() && getMissController().isPLEon())
            {
        		getMissController().checkCompletedStartState();
            }
        	return;
        	}
        else if (getCtatModeModel().isDemonstratingSolution()) { // Demonstrate mode

        	demonstrateModeMessageHandler.processDemonstrateInterfaceAction(selection, action, input,
        			o, EdgeData.CORRECT_ACTION);

        	if (getMissController()!=null && getMissController().getSimSt()!=null && getMissController().getSimSt().isSsCogTutorMode() && !getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){	  
        		JCommButton doneButton = (JCommButton) lookupWidgetByName("Done");
        	 	doneButton.setEnabled(true);
        	}
        	
        	
        	return; }
        else  // if (getCtatModeModel().isTestTutorMode()) 
    	    getWidgetSynchronizedLock().lock(this, 60000);
        	// getWidgetSynchronizedLock().lock();
        
        	if (getCtatModeModel().isRuleEngineTracing()) {
        		trace.out ("br", "inside Tutor mode");
        		tutorMessageHandler.processTutorInterfaceAction(selection, action, input,
        				Matcher.DEFAULT_ACTOR, o);
        	} else if (getCtatModeModel().isExampleTracingMode()) {
        		//Example tracing log replays go through here
        		if(trace.getDebugCode("br")) trace.out("br", "BR_Controller handleInterfaceActionMessage(o,boolean) - "
        				+ "getCtatModeModel().isExampleTracingMode() " + getCtatModeModel().isExampleTracingMode());
        		
        		getPseudoTutorMessageHandler().setTransactionId(getSemanticEventId());
        		getPseudoTutorMessageHandler().setRequestMessage(o);
        		getPseudoTutorMessageHandler().processPseudoTutorInterfaceAction(selection, action, input);
        	} else 
        		throw new RuntimeException ("Unknown Behavior Recorder mode: " + getCtatModeModel().getModeTitle());
    }
    
    //returns the saiTable that is created in the handleInterfaceAction method in authoring time
    public VariableTable getsaiTable()
    {
    	return saiTable;
    }
    
    //updates the SAITable, which tracks the most recent student SAI
    public void updateSAITable(List selection, List action, List input, String type)
    {
    	saiTable.clear();
    	
    	Iterator sit = (selection == null ? EmptyIterator.instance() : selection.iterator());
    	Iterator ait = (action == null ? EmptyIterator.instance() : action.iterator());
    	Iterator iit = (input == null ? EmptyIterator.instance() : input.iterator());
    	boolean putType=true;
    	
    	for (int i=0;sit.hasNext()||ait.hasNext()||iit.hasNext();i++)
    	{
    		if(sit.hasNext())
    			saiTable.put("selection["+i+"]", sit.next());
    		if(ait.hasNext())
    			saiTable.put("action["+i+"]", ait.next());
    		if(iit.hasNext())
    			saiTable.put("input["+i+"]",iit.next());
    		if(putType && type != null){
    			saiTable.put("type", type);
    			putType=false;
    		}
    	}
    	if(saiTable.getModel() != null)
    		saiTable.getModel().fireTableDataChanged();
    }
    
    public CtatModeModel getCtatModeModel() {
        return ctatModeModel;
    }


    
    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * find the matched edge to move the currNode, priorities are: 1. preferPath
     * edge; 2. CORRECT_ACTION edge; 3. FIREABLE_BUGGY_ACTION edge
     */
    // ////////////////////////////////////////////////////////////////////////////////
 /*   public ProblemEdge findMatchedEdge() {
        ProblemEdge matchedEdge = null;
        ProblemEdge tempEdge;

        EdgeData tempMyEdge;

        Enumeration iterOutEdge = problemModel.getProblemGraph().outEdges(
                getSolutionState().getCurrentNode());
        while (iterOutEdge.hasMoreElements()) {
            tempEdge = (ProblemEdge) iterOutEdge.nextElement();

            tempMyEdge = tempEdge.getEdgeData();

            // trace.out(
            // "tempMyEdge.actionLabel.getInput() = "
            // + tempMyEdge.actionLabel.getInput());

            if (getSolutionState().testEdgeOnValidPath(tempEdge)
                    && ProblemModel.testEdgeInVector(tempEdge, problemModel
                            .getUnorderedEdges())) {

                String authorIntent = tempMyEdge.getActionType();

                if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
                    matchedEdge = tempEdge;

                    if (tempMyEdge.isPreferredEdge()) {
                        return matchedEdge;
                    }
                } else if (matchedEdge == null
                        && authorIntent
                                .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
                    matchedEdge = tempEdge;
            } // if (testEdgeOnValidPath()
        } // while

        return matchedEdge;
    }*/
    
    /**
     * Reset the traversed links list. Call this when returning to the start state.
     */
    private void resetTraversedLinks() {
    	if (getProcessTraversedLinks() == null)
    		return;
    	getProcessTraversedLinks().initTraversedLinks();
    }

	/**
	 * Set the tutor and the student interface back to the start state.
	 * Also sends start state messages. This is the public interface to this
	 * facility.
	 */
	public void goToStartState() {
		goToStartState(false, true);
	}

	/**
	 * Set the tutor and the student interface back to the start state.
	 * Also sends start state messages. This is the public interface to this
	 * facility.
	 * @param advanceToStudentBeginsHere if true, advance to the state given by
	 *        {@link ProblemModel#getStudentBeginsHereState()}
	 * @param changeViewPoint if true, shift the view port to the new current state
	 */
	public void goToStartState(boolean advanceToStudentBeginsHere, boolean changeViewPoint) {
		goToStartState(getProblemModel().getStudentBeginsHereState(), changeViewPoint);
	}

	/**
	 * Set the tutor and the student interface back to the start state.
	 * Also sends start state messages. This is the public interface to this
	 * facility.
	 * @param studentStartState if not null, advance to the this state
	 * @param changeViewPoint if true, shift the view port to the new current state
	 */
	public void goToStartState(ProblemNode studentStartState, boolean changeViewPoint) {
		ProblemModel pm = getProblemModel();
		// delete UI values from variable table
		pm.getVariableTable().clear();
        if (getMessageTank() != null)
        	getMessageTank().clear();
        if (pm == null || pm.getNodeCount() < 1)
        	return;  // no start state to go to
        
        if (getCtatModeModel().isDemonstratingSolution()) {
            setCurrentNode2(pm.getStartNode());
    		sendStartNodeMessages(null, false);  // CTAT2406
        } else if (getCtatModeModel().isExampleTracingMode()) {
        	setCurrentNode(pm.getStartNode());          // sewall 2014-01-10 update bold font on node
        	int traverseCount = 0;
            resetTraversedLinks();
    		setSolutionState(new SolutionState(pm));   // sewall 2013-09-19 avoid extra ResetAction msgs
    		if (trace.getDebugCode("pm"))
    			trace.printStack("pm", "goToStartState("+studentStartState+") calling initzePseudo");
    		getPseudoTutorMessageHandler().initializePseudoTutorAndSendStartState(studentStartState != null);
            if (studentStartState != null)
            	traverseCount = getPseudoTutorMessageHandler().advanceToStudentBeginsHere(studentStartState);

            getPseudoTutorMessageHandler().doUnrequestedHints(getCurrentNode(), traverseCount);
        } else if (getCtatModeModel().isTDKMode()) {
        	sendStartNodeMessages(getLogger().getProblemName(), false);
        }
 		else {
 			goToStartStateForRuleTutors();
 		}
        if (changeViewPoint && pm.getStartNode() != null && !Utils.isRuntime())
			getJGraphWindow().getJGraph().scrollPointToVisible(pm.getStartNode().getNodeView().getLocation()); 
		fireCtatModeEvent(CtatModeEvent.REPAINT);
		
	}
	
	/**
	 * Return true if we're in the multi-threaded Tutoring Service, which has no GUI.
	 * <b>N.B.:</b> this does not return true when we're in the applet-based tutoring service.
	 * @return {@link #launcher} != null &&
	 * 			  {@link SingleSessionLauncher#getLauncherServer() launcher.getLauncherServer()} != null
	 */
	public boolean inTutoringServiceMode() {
		if(launcher == null)
			return false;
		return launcher.inTutoringServiceMode();
	}
	
	/**
	 * Set the tutor and the student interface back to the start state
	 * for rule engines.
	 */
    public void goToStartStateForRuleTutors() {
    	
    	// This function is called only when we are at the first step of any new problem.
    	if (trace.getDebugCode("br")) trace.out("br", "go to start state()");

        if (getProblemModel().getStartNode() == null)
            return;

        // fix the bug #1112
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        //brPanel.setCursor(hourglassCursor);

        if (getCtatModeModel().isJessTracing()) {  // Jess or SimSt
        	if (trace.getDebugCode("br")) trace.out("br", "getCtatModeModel().isJessTracing()");
            resetTraversedLinks();
            getPseudoTutorMessageHandler().initializePseudoTutor();
        }
        // I do not understand this part as well.
        if (getSolutionState().getCurrentNode() != getProblemModel()
                .getStartNode())
            setCurrentNode2(getProblemModel().getStartNode());

        MessageObject newMessage = 
        	MessageObject.create(MsgType.RESTORE_INITIAL_WM_STATE, "SendNoteProperty");

        if (getProblemModel().getStartNode().getNodeView() != null)
        {        	
        	if (trace.getDebugCode("br")) 
        		trace.out("br", "getProblemModel().getStartNode().getNodeView() ");
        	NodeView startVertex = getProblemModel().getStartNode().getNodeView();
        	newMessage.setProperty("ProblemName", startVertex.getText());
        }
		
        utp.sendProperty(newMessage);
			
			
        //Gustavo 2Dec2006: for each message in startNodeMessageVector, send it.
        Iterator<MessageObject> it = getProblemModel().startNodeMessagesIterator();
        for (int i = 0; it.hasNext(); i++) {
        	MessageObject msg = it.next();
            trace.out("mt", "Sending start Comm Message " + (i+1) +" to LISP: " + msg);
            trace.out("ss", "Sending start Comm Message " + (i+1) +" to LISP: " + msg);
        	utp.sendProperty(msg);          
        }

        // send Start state Comm MSGs to UniversalToolProxy
        sendCommMsgs(getProblemModel().getStartNode(), getProblemModel()
                .getStartNode());

        // fix the bug #1112
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        //brPanel.setCursor(normalCursor);
        if (!Utils.isRuntime())
        	getJGraphWindow().getJGraph().repaint();
        return;
    }
   
    /**
     * @param true means show cancel option on save-file dialog
     * @return response to save-file dialog 
     */
    //does this work... does cancel actually cancel?
    //or is it synonymous with yes
    public int saveCurrentProblemWithUserPrompt(boolean showCancel) {
    	trace.printStack("mg", "BR_Controller (saveCurrentProblemWithUserPrompt): start");
        MissControllerExternal missController = getLauncher().getMissController();
        if (!getAuthorToolsVisible() ||  (missController != null && (missController.isPLEon() || missController.isContestOn() || missController.isBatchModeOn())))
            return JOptionPane.NO_OPTION;

    	ProblemModel pm = getProblemModel();
    	String problemFile = null;  // if have path, prompt is simple file name; else problem name
    	if (pm.getProblemFullName() != null && pm.getProblemFullName().length() > 0) {
    		problemFile = (new File(pm.getProblemFullName())).getName();
          	if (isStartStateModified()) 
          		modifyStartState(pm.getStartNode().getName());
    	} else if (pm.getProblemName() != null && pm.getProblemName().length() > 0)
    		problemFile = pm.getProblemName();
    	else                                         // if no problem name, prompt with panel name
    		problemFile = getJGraphWindow().getName();
        String message[] = { "Do you want to save the current Behavior Graph ("+problemFile+")?", " " };
        int value = -1;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	value = getProblemStateReader().trySaveAndTestForChange(getProblemStateWriter(), baos);
    	if (value != JOptionPane.YES_OPTION)
    		return value;
    	
        if (showCancel)
            value = JOptionPane.showConfirmDialog(getServer().getActiveWindow(),
                    message, "Save Current Behavior Graph",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        else
            value = JOptionPane.showConfirmDialog(getServer().getActiveWindow(),
                    message, "Save Current Behavior Graph",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

    	if (value == JOptionPane.CLOSED_OPTION)  // user clicked X button or pressed Esc
    		value = JOptionPane.CANCEL_OPTION;
        if (value != JOptionPane.YES_OPTION) {
            return value;  // CTAT1356 rtn CANCEL_OPTION so callers can quit op
        }
        
        //second pop-up added 7/20/11 by epfeifer
        if (trace.getDebugCode("eep")) 
        	trace.out("eep","old graph tutType:"+getServer().getCtatFrameController().getDockedFrame().getTutorTypeLabel()+
        		",new graph tutType:"+getCtatModeModel().getCurrentMode());
       //if (showTTSave) the preference to show the tutorType save message is set to true
       //&&	the current tutor type label is not null (this isn't a new graph)
       //&& the current tutor type label is different than the current tutor type
        /*author's note: the tutorTypeLabel field is not actually null ever,
        but the getTutorTypeLabel method returns null if the tutorTypeLabel is equal
        to "No graph has been selected yet", which is initial value of this field
        */
        if (isShowTTSave() &&
        		getServer().getCtatFrameController().getDockedFrame().getTutorTypeLabel()!=null &&
        		!getServer().getCtatFrameController().getDockedFrame().getTutorTypeLabel().equals(
        		getCtatModeModel().getCurrentMode()) ) {
        	//display the tutorType save dialog
    		JCheckBox checkbox = new JCheckBox("Do not show this message again.");
    		Object[] params = {"If you save you will change the tutor type of this brd. Are you sure you want to save?", checkbox};
    		if (showCancel)
    			value = JOptionPane.showConfirmDialog(null, params, "Save Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
    		else
    			value = JOptionPane.showConfirmDialog(null, params, "Save Confirmation", JOptionPane.YES_NO_OPTION);
    		boolean dontShow = checkbox.isSelected();
    		if (trace.getDebugCode("eep")) trace.out("eep","saveBRDandNotifyTT:checkbox="+dontShow+",resp="+value);
    		if (dontShow)
    			setShowTTSave(false);
        }
    	else if (trace.getDebugCode("eep")) trace.out("eep","save tutor type popup not displayed-brd and current type match");
        
        if (value != JOptionPane.YES_OPTION)
        	return value;
        //end second pop-up code

        if (getProblemModel().getProblemFullName() == null 
        		|| getProblemModel().getProblemFullName().length() == 0) {
            NodeView Vertex = getProblemModel().getStartNode().getNodeView();
            boolean result = SaveFileDialog.doDialog(this, Vertex.getText());
            if(trace.getDebugCode("eep"))
            	trace.out("eep", "BR_C.saveCurrentProblemWithUserPrompt() result = " + result);
            if (result == false)
                return JOptionPane.CANCEL_OPTION;
        } else {
            if (null == getProblemStateWriter().saveBRDFile(getProblemModel().getProblemFullName(), baos.toByteArray()))
            	return JOptionPane.CANCEL_OPTION;
        }

        File converter=new File (getProblemModel().getProblemFullName());            
        getCtatFrameController().getDockedFrame().getCtatMenuBar().addRecentfile (converter.getParent(),converter.getName());

        fireCtatModeEvent(CtatModeEvent.REPAINT);
//        brFrame.repaint();
        return value;
    }

    public int startNewProblem() {
    	 return startNewProblem(true);
    }

    /**
     * Clear the problem model and start a new problem.
     * @param showCancel argument to {@link #saveCurrentProblemWithUserPrompt(boolean)};
     *        if null, don't even call it
     * @return result from {@link #saveCurrentProblemWithUserPrompt(boolean)}, if called;
     *        else returns 
     */
    public int startNewProblem(Boolean showCancel) 
    {
    	ProblemModel pm = getProblemModel();
    	
        int ret = JOptionPane.YES_OPTION;
        if(showCancel != null) {
        	if (pm.getProblemGraph().getNodeCount() > 0 && !(getCTAT_LMS().isStudentLoggedIn()
        			|| pm.isProblemLoadedFromLispTutor())) {
        		ret = saveCurrentProblemWithUserPrompt(showCancel);
        		if (ret == JOptionPane.CANCEL_OPTION) { return ret; }
        	}
        }        
        
        // sewall 2013-05-22 get ordering preference before any changes
        Boolean isUnordered = getPreferencesModel().getBooleanValue(BR_Controller.COMMUTATIVITY);
        if(trace.getDebugCode("pr"))
        	trace.outNT("br", "startNewProblem() isUnordered "+isUnordered+", default false");
        if(isUnordered == null)
            isUnordered = Boolean.FALSE;

    	// Tell the start state editor to clear itself ...
    	
    	//getProblemModel().fireProblemModelEvent(new CTATStartStateEvent(this,"NewGraph",null));
        pm.fireProblemModelEvent(new CTATStartStateEvent(this,"NewGraph",null));
    	
        // reset state DGraph
        reset();

        getPreferencesModel().setStringValue("Graph File", null);
        
        if (pm != null && !isStudentInterfaceLocal()) 
        	pm.setUseCommWidgetFlag(false);
       
        if (getCtatFrameController() != null) {
            //Ensure you don't register as listener twice
        	pm.removeProblemModelListener(getCtatFrameController().getDockedFrame().getCtatMenuBar());
	    //menu bar needs to detect changes in the problem model
        	pm.addProblemModelListener(getCtatFrameController().getDockedFrame().getCtatMenuBar());
        }


        //solutionState = new SolutionState(getProblemModel());
        //solutionState.reset();
        setSolutionState(new SolutionState(pm));
        getSolutionState().reset();

        if (!Utils.isRuntime())
        {
            //Ensure registered as listener once once
        	
            pm.removeProblemModelListener(getJGraphWindow().getJGraphController());
            pm.addProblemModelListener(getJGraphWindow().getJGraphController());


            ActionEvent ae = new ActionEvent(this, 0, "Started new problem");  // clear undo stack
        	getUndoPacket().getInitializeAction().actionPerformed(ae);
        }

        getCtatModeModel().setAuthorMode(CtatModeModel.DEFINING_START_STATE);

        MessageObject newMessage = MessageObject.create(MsgType.START_NEW_PROBLEM, "SendNoteProperty");
	    newMessage.setProperty("AuthorMode", getCtatModeModel().getCurrentAuthorMode());
        if (getUniversalToolProxy() != null) {
        	handleMessageUTP(newMessage);
        }

        //brPanel.getScrollPanel().originalDrawArea();

        //brPanel.resetTable();
        resetRuleEngineForNewProblem();
        if (!Utils.isRuntime() && getCtatFrameController() != null) {
        	getCtatFrameController().getDockedFrame().getCtatMenuBar().enableCreateStartStateMenus(true);
        }
        fireCtatModeEvent(CtatModeEvent.REPAINT);

        if (getCtatModeModel().isSimStudentMode()) {
            getMissController().startNewProblem();
        }
        // No activate menu exists now: sdemi 03-oct-06
        // Enable to change an activation status of the Sim. St.
        //        brPanel.getHandler().enableSimStActivationMenu(true);
        
        getProblemModel().fireProblemModelEvent(new NewProblemEvent(this, isUnordered.booleanValue()));
        getExampleTracer().setStartStateVT(pm.getVariableTable());
        //exampletracer.setvt(problemmodel.vt);
        
        // reset the graph title
        getServer().getDockManager().refreshGraphTitle(this.tabNumber);
        return ret;
    }

    /**
     * Tell whether the student interface is part of this Java VM (local) or connected via
     * a communications channel (remote).
     * @return value of {@link #studentInterfaceLocal}
     */
    public boolean isStudentInterfaceLocal() {
		return studentInterfaceLocal;
	}

    /**
     * Set whether the student interface is part of this Java VM (local) or connected via
     * a communications channel (remote).
     * @param new value for {@link #studentInterfaceLocal}
     */
    public boolean setStudentInterfaceLocal(boolean b) {
		return studentInterfaceLocal = b;
	}

	/**
     * Send a RestorJessInitialWMState message to Jess to clear the WM.
     */
    public void resetRuleEngineForNewProblem() {
        if (!getCtatModeModel().isJessMode())
            return;
        MessageObject newMessage = MessageObject.create(MsgType.RESTORE_JESS_INITIAL_WM_STATE);
        newMessage.setVerb("NotePropertySet");
        utp.sendProperty(newMessage);
    }

	/**
     *  Close the student interface window by calling {JFrame#dispose()}. Also
     *  clears {@link #studentInterface}, calls {@link #clearWidgetInformation()}.
     *  Override also calls {@link #startNewProblem(boolean)}, enables interface menus.
     */
    public void closeCurrentInterface() {
        if (studentInterface == null)
            return;

        int ret = startNewProblem(true);
        if (ret == JOptionPane.CANCEL_OPTION)
            return;

        closeStudentInterface();
        getCtatFrameController().getDockedFrame().getCtatMenuBar().enableInterfaceMenus(true);
    }



    // ////////////////////////////////////////////////////////////////////
    /**
     * Reload production rule file
     */
    // ////////////////////////////////////////////////////////////////////
    /**
     * Mon Oct 10 16:48:18 2005:: Noboru
     * 
     * Called by SimSt object when generateUpdateProductionRules() is performed.
     * 
     * @param prFile
     *            an absolute name of the production rule file
     */
    public void reloadProductionRulesFile(File prFile) {

        // Mon Oct 10 16:02:42 2005 :: Noboru
        // **********************************
        /*
         * String [][] prFilePropery = { { "PrFileName", prFile } };
         * MessageObject msg = BR_Frame.makeMessage( "NotePropertySet",
         * utp.getToolProxy(), "reloadProductionRulesFile", prFilePropery );
         * utp.sendProperty( msg );
         */
    	getModelTracer().getRete().reloadProductionRulesFile(prFile, false);
    }

    // Thu Oct 20 17:27:13 2005 :: Noboru
    // Added to stop processCheckAllStatesResult() popping-up a dialogue menu
    // when it is called by Sim. St.
    private boolean checkAllStatesBySimSt = false;

	private ProblemModelEventFactory eventFactory = new ProblemModelEventFactory (this);


	public static final String TEST_MODEL_ALL_STEPS_RESULT = "TEST_MODEL_ALL_STEPS_RESULT";	// program action
	public static final String TEST_MODEL_ALL_STEPS = "TEST_MODEL_ALL_STEPS";
	public static final String TEST_MODEL_1_STEP_RESULT = "TEST_MODEL_1_STEP_RESULT";			// program action
	public static final String TEST_MODEL_1_STEP = "TEST_MODEL_1_STEP";
	
	public static final String SWITCH_MODE = "SWITCH_MODE";
	public static final String GO_TO_STATE = "GO_TO_STATE";

	// valid action type for authoractionlog 
	/** Constants for author action log. */
	 // BEHAVIOR_RECORDER
	public static final String GO_TO_START_STATE = "GO_TO_START_STATE";

	public static final String SAVE_FILE = "SAVE_FILE";

	public static final String OPEN_FILE = "OPEN_FILE";
	public static final String OPEN_GRAPH = "OPEN_GRAPH";
	public static final String OPEN_INTERFACE = "OPEN_INTERFACE";
	/* OPEN_GRAPH and OPEN_INTERFACE are used when opening graphs and interfaces
	 * for extra description.  OPEN_FILE can be used when opening other files.
	 */

	public static final String MENU_ITEM = "MENU_ITEM";
	public static final String EDIT_HINTS = "EDIT_HINTS";
	

	/** Listener for remote (not in this Java VM) student interface. */
	private RemoteProxy remoteProxy;

    /** Current skills console, from Windows menu. */
	private SkillsConsoleDialog skillsConsole = null;
	private boolean showTTSave=true;
	
	/** Controller-specific {@link RuleProduction.Catalog} for use with Tutoring Service. */
	private RuleProduction.Catalog tsRuleProductionCatalog;

	/**
     * @return Returns the checkAllStatesBySimSt.
     */
    public boolean isCheckAllStatesBySimSt() {
        return checkAllStatesBySimSt;
    }

    /**
     * @param checkAllStatesBySimSt
     *            The checkAllStatesBySimSt to set.
     */
    public void setCheckAllStatesBySimSt(boolean checkAllStatesBySimSt) {
        this.checkAllStatesBySimSt = checkAllStatesBySimSt;
    }
        
        /**
         * Convenience method to check if the passed in ProblemModel is
         * the one currently in the main state
         * @param potentialMainStateProblemModel model to be checked for main status
         */
        public boolean isMainProblemModel(ProblemModel potentialMainStateProblemModel)
        {
        		return (getProblemModel() == potentialMainStateProblemModel);
        }

    public void processCheckAllStatesResult(MessageObject mo) {

        Vector edgeList = (Vector) mo.getProperty("EdgeList");

        int edgeNumber = edgeList.size();

        // trace.out ( "edgelist size:" + edgeNumber);

        Vector edgeCheckResult;
        Integer uniqueID;
        String checkResult;

        ProblemEdge edge = null;
        EdgeData myEdge;

        int workedNumber = 0;
        int notWorkedNumber = 0;
        int NANumber = 0;

        String goodChangeText = "   Good Changes: (from inconsistent to consistent)\n";
        String badChangeText = "   Bad Changes: (from consistent to inconsistent)\n";
        String notBadNotGoodChangeText = "   Changes that are neither good nor bad: (from one form of inconsistent to another)\n";

        String workedEdgeIDsText = "";
        String notWorkedEdgeIDsText = "";
        String NAEdgeIDsText = "";

        String secondaryEffectText = "";

        if (isFirstCheckAllStatesFlag()) {
            // trace.out(5, this, "first check all states:");

            for (int i = 0; i < edgeNumber; i++) {
                // trace.out( "i = " + i);

                edgeCheckResult = (Vector) edgeList.elementAt(i);

                if (edgeCheckResult == null) {
                    // trace.out("No more edgeCheckResult in edgeList");
                    return;
                }

                uniqueID = (Integer) edgeCheckResult.elementAt(0);
                checkResult = (String) edgeCheckResult.elementAt(1);

                // trace.out ( "uniqueID: " + uniqueID.intValue());
                // trace.out ( "checkResult: " + checkResult);

                edge = getProblemModel().getEdge(
                        uniqueID.intValue());

                if (edge == null) {
                    // trace.out(
                    // 5,
                    // this,
                    // "No match edge for uniqueID = " + uniqueID.intValue());
                    return;
                }

                myEdge = edge.getEdgeData();
                myEdge.getPreLispCheckLabel().resetAll(uniqueID.intValue(),
                        myEdge.getCheckedStatus());
                myEdge.setCheckedStatus(checkResult);
                // trace.out("missx", "setCkeckedStatus: #6");

                if (myEdge.getCheckedStatus()
                        .equalsIgnoreCase(EdgeData.SUCCESS))
                    sendCorrectActionMsg(myEdge.getSelection(), myEdge
                            .getInput(), myEdge.getAction());
                else
                    // sendBuggyMsg(myEdge.actionLabel.buggyMsg,
                    // myEdge.actionLabel.selection, myEdge.actionLabel.input);
                    sendIncorrectActionMsg(myEdge.getSelection(), myEdge
                            .getInput(), myEdge.getAction(), false);

                if (checkResult.equalsIgnoreCase(EdgeData.NOTAPPLICABLE)) {
                    NANumber++;
                    NAEdgeIDsText = NAEdgeIDsText + uniqueID.intValue() + ", ";
                } else {
                    if (getProblemModel().checkConsistency(
                            myEdge.getCheckedStatus(), myEdge.getActionType())) {
                        workedNumber++;
                        workedEdgeIDsText = workedEdgeIDsText
                                + uniqueID.intValue() + ", ";
                    } else {
                        notWorkedNumber++;
                        notWorkedEdgeIDsText = notWorkedEdgeIDsText
                                + uniqueID.intValue() + ", ";
                    }
                }
            } // end of for loop
            fireCtatModeEvent(CtatModeEvent.REPAINT);
//            brFrame.repaint();
        } else {
            int workBeforeNotworkNowNumber = 0;
            int notWorkBeforeWorkNowNumber = 0;
            int notBadNotGoodNumber = 0;

            String oldCheckedText;

            for (int i = 0; i < edgeNumber; i++) {
                edgeCheckResult = (Vector) edgeList.elementAt(i);
                uniqueID = (Integer) edgeCheckResult.elementAt(0);
                checkResult = (String) edgeCheckResult.elementAt(1);

                // trace.out ( "uniqueID: " + uniqueID.intValue());
                // trace.out ( "checkResult: " + checkResult);

                edge = getProblemModel().getEdge(
                        uniqueID.intValue());
                myEdge = edge.getEdgeData();

                oldCheckedText = myEdge.getCheckedStatus();

                myEdge.getPreLispCheckLabel().resetAll(uniqueID.intValue(),
                        myEdge.getCheckedStatus());

                myEdge.setCheckedStatus(checkResult);
                // trace.out("missx", "setCkeckedStatus: #7");

                if (myEdge.getCheckedStatus()
                        .equalsIgnoreCase(EdgeData.SUCCESS))
                    sendCorrectActionMsg(myEdge.getSelection(), myEdge
                            .getInput(), myEdge.getInput());
                else
                    // sendBuggyMsg(myEdge.actionLabel.buggyMsg,
                    // myEdge.actionLabel.selection, myEdge.actionLabel.input);
                    sendIncorrectActionMsg(myEdge.getSelection(), myEdge
                            .getInput(), myEdge.getInput(), false);

                /*
                 * if (uniqueID.intValue() > 11) { trace.out ( "for arc " +
                 * uniqueID.intValue()); trace.out ( "oldCheckedText: " +
                 * oldCheckedText); trace.out ( "checkResult: " + checkResult);
                 * trace.out ( "old authorIntent: " +
                 * myEdge.actionLabel.oldAuthorIntent); trace.out (
                 * "authorIntent: " + myEdge.actionLabel.getAuthorIntent()); }
                 */
                // get the primary effects
                if (!oldCheckedText.equalsIgnoreCase(EdgeData.NOTAPPLICABLE)
                        && !checkResult
                                .equalsIgnoreCase(EdgeData.NOTAPPLICABLE)) {
                    // trace.out ( "old consistency: " +
                    // checkConsistency(oldCheckedText,
                    // myEdge.actionLabel.oldAuthorIntent));
                    // trace.out ( "new consistency: " +
                    // checkConsistency(checkResult,
                    // myEdge.actionLabel.authorIntent));

                    if (!getProblemModel().checkConsistency(oldCheckedText,
                            myEdge.getOldActionType())
                            && getProblemModel().checkConsistency(checkResult,
                                    myEdge.getActionType())) {

                        notWorkBeforeWorkNowNumber++;

                        if (!oldCheckedText.equalsIgnoreCase(checkResult))
                            goodChangeText = goodChangeText + "    Arc "
                                    + uniqueID.intValue() + " used to "
                                    + oldCheckedText + " but is now "
                                    + checkResult + ".\n";
                        else
                            goodChangeText = goodChangeText + "    Arc "
                                    + uniqueID.intValue()
                                    + " author intent used to "
                                    + myEdge.getOldActionType()
                                    + " but is now " + myEdge.getActionType()
                                    + ".\n";
                    }

                    if (getProblemModel().checkConsistency(oldCheckedText,
                            myEdge.getOldActionType())
                            && !getProblemModel().checkConsistency(checkResult,
                                    myEdge.getActionType())) {
                        workBeforeNotworkNowNumber++;

                        if (!oldCheckedText.equalsIgnoreCase(checkResult))
                            badChangeText = badChangeText + "    Arc "
                                    + uniqueID.intValue()
                                    + " used to consistently be "
                                    + oldCheckedText + " but is now "
                                    + checkResult + ".\n";
                        else
                            badChangeText = badChangeText + "    Arc "
                                    + uniqueID.intValue()
                                    + " used to consistently with author as "
                                    + myEdge.getOldActionType()
                                    + " but is now " + myEdge.getActionType()
                                    + ".\n";

                    }

                    if (!oldCheckedText.equalsIgnoreCase(checkResult)
                            && !getProblemModel().checkConsistency(
                                    oldCheckedText, myEdge.getOldActionType())
                            && !getProblemModel().checkConsistency(checkResult,
                                    myEdge.getActionType())) {
                        notBadNotGoodNumber++;

                        notBadNotGoodChangeText = notBadNotGoodChangeText
                                + "    Arc " + uniqueID.intValue()
                                + " changed from " + oldCheckedText + " to "
                                + checkResult + " but is still inconsistent "
                                + "because it is supposed to be "
                                + myEdge.getActionType() + ".\n";
                    }
                }

                // get the statistics about consistency, inconsistency and
                // No-Applicable
                if (checkResult.equalsIgnoreCase(EdgeData.NOTAPPLICABLE)) {
                    NANumber++;
                    NAEdgeIDsText = NAEdgeIDsText + uniqueID.intValue() + ", ";
                } else {
                    if (getProblemModel().checkConsistency(
                            myEdge.getCheckedStatus(), myEdge.getActionType())) {
                        workedNumber++;
                        workedEdgeIDsText = workedEdgeIDsText
                                + uniqueID.intValue() + ", ";
                    } else {
                        notWorkedNumber++;
                        notWorkedEdgeIDsText = notWorkedEdgeIDsText
                                + uniqueID.intValue() + ", ";
                    }
                }
            } // end of for loop

            // trace.out ( "workBeforeNotworkNowNumber = " +
            // workBeforeNotworkNowNumber);
            // trace.out ( "notWorkBeforeWorkNowNumber = " +
            // notWorkBeforeWorkNowNumber);
            // trace.out ( "notBadNotGoodNumber = " + notBadNotGoodNumber);

            fireCtatModeEvent(CtatModeEvent.REPAINT);
//            brFrame.repaint();

            String checkSecondarytext;

            getProblemModel().setCheckAllEdges(new Vector());

            Enumeration iterOutEdge = getProblemModel().getProblemGraph()
                    .edges();
            while (iterOutEdge.hasMoreElements()) {
                edge = (ProblemEdge) iterOutEdge.nextElement();

                if (getProblemModel().getProblemGraph().outDegree(
                        edge.getNodes()[ProblemEdge.DEST]) > 0) {
                    myEdge = edge.getEdgeData();

                    if (getProblemModel().checkConsistency(
                            myEdge.getCheckedStatus(), myEdge.getActionType())
                            && !getProblemModel()
                                    .checkConsistency(
                                            myEdge.getPreLispCheckLabel().preCheckedStatus,
                                            myEdge.getOldActionType())) {
                        checkSecondarytext = getProblemModel()
                                .checkSecondaryEffects(edge);

                        if (checkSecondarytext != "")
                            secondaryEffectText = checkSecondarytext
                                    + secondaryEffectText;
                    } else if (!getProblemModel().checkConsistency(
                            myEdge.getCheckedStatus(), myEdge.getActionType())
                            && getProblemModel()
                                    .checkConsistency(
                                            myEdge.getPreLispCheckLabel().preCheckedStatus,
                                            myEdge.getOldActionType())) {
                        checkSecondarytext = "   Because Arc "
                                + myEdge.getUniqueID()
                                + " is no longer consistent, "
                                + "you have discovered the following changes have happened that are neither good nor bad:\n";

                        String checkSecondaryBadText = "   Because Arc "
                                + myEdge.getUniqueID()
                                + " is no longer consistent, "
                                + "you have discovered the following changes:\n";

                        boolean secondaryBadFlag = false;

                        ProblemEdge childEdge;
                        EdgeData myChildEdge;
                        boolean addTextFlag = false;

                        Enumeration iterEdge = getProblemModel()
                                .getProblemGraph().getOutgoingEdges(
                                        edge.getNodes()[ProblemEdge.DEST]);
                        while (iterEdge.hasMoreElements()) {
                            childEdge = (ProblemEdge) iterEdge.nextElement();
                            myChildEdge = childEdge.getEdgeData();

                            if (myChildEdge.getCheckedStatus()
                                    .equalsIgnoreCase(EdgeData.NOTAPPLICABLE)
                                    && !getProblemModel()
                                            .checkConsistency(
                                                    myChildEdge
                                                            .getPreLispCheckLabel().preCheckedStatus,
                                                    myChildEdge
                                                            .getOldActionType())) {
                                addTextFlag = true;
                                checkSecondarytext = checkSecondarytext
                                        + "  Arc "
                                        + myChildEdge.getUniqueID()
                                        + " that used to be inconsistent is now "
                                        + EdgeData.NOTAPPLICABLE;
                            } else if (!getProblemModel().checkConsistency(
                                    myChildEdge.getCheckedStatus(),
                                    myChildEdge.getActionType())
                                    && getProblemModel()
                                            .checkConsistency(
                                                    myChildEdge
                                                            .getPreLispCheckLabel().preCheckedStatus,
                                                    myChildEdge
                                                            .getOldActionType())) {
                                secondaryBadFlag = true;
                                checkSecondaryBadText = checkSecondaryBadText
                                        + "  Arc "
                                        + myChildEdge.getUniqueID()
                                        + " that used to be consistent is now "
                                        + myChildEdge.getCheckedStatus();
                            }
                        }

                        if (addTextFlag)
                            secondaryEffectText = checkSecondarytext
                                    + secondaryEffectText;

                        if (secondaryBadFlag)
                            secondaryEffectText = checkSecondaryBadText
                                    + secondaryEffectText;
                    }
                }
            } // end of while

            secondaryEffectText = " Secondary effects caused by primary changes:\n"
                    + secondaryEffectText;
        }

        int tempNumber = workedEdgeIDsText.length();
        String tempString = "";
        if (tempNumber > 2) {
            // trim last ", "
            workedEdgeIDsText = workedEdgeIDsText.substring(0, tempNumber - 2);
            tempNumber = workedEdgeIDsText.lastIndexOf(" ");
            if (tempNumber > 0) {
                tempString = workedEdgeIDsText.substring(tempNumber + 1,
                        workedEdgeIDsText.length());
                workedEdgeIDsText = workedEdgeIDsText.substring(0, tempNumber)
                        + " and " + tempString;
            }
        }

        tempNumber = notWorkedEdgeIDsText.length();
        if (tempNumber > 2) {
            // trim last ", "
            notWorkedEdgeIDsText = notWorkedEdgeIDsText.substring(0,
                    tempNumber - 2);
            tempNumber = notWorkedEdgeIDsText.lastIndexOf(" ");
            if (tempNumber > 0) {
                tempString = notWorkedEdgeIDsText.substring(tempNumber + 1,
                        notWorkedEdgeIDsText.length());
                notWorkedEdgeIDsText = notWorkedEdgeIDsText.substring(0,
                        tempNumber)
                        + " and " + tempString;
            }
        }

        tempNumber = NAEdgeIDsText.length();
        if (tempNumber > 2) {
            // trim last ", "
            NAEdgeIDsText = NAEdgeIDsText.substring(0, tempNumber - 2);
            tempNumber = NAEdgeIDsText.lastIndexOf(" ");
            if (tempNumber > 0) {
                tempString = NAEdgeIDsText.substring(tempNumber + 1,
                        NAEdgeIDsText.length());
                NAEdgeIDsText = NAEdgeIDsText.substring(0, tempNumber)
                        + " and " + tempString;
            }
        }

        String completeAnalysisText = "Complete Analysis:\n\n";

        if (workedNumber > 0)
            completeAnalysisText = completeAnalysisText + workedNumber
                    + " of the " + edgeNumber + " arcs are consistent.\n";
        else
            completeAnalysisText = completeAnalysisText + "None of the "
                    + edgeNumber + " arcs are consistent.\n";

        if (notWorkedNumber > 0)
            completeAnalysisText = completeAnalysisText + notWorkedNumber
                    + " of the " + edgeNumber + " arcs are inconsistent.\n";
        else
            completeAnalysisText = completeAnalysisText + "None of the "
                    + edgeNumber + " arcs are inconsistent.\n";

        if (NANumber > 0)
            completeAnalysisText = completeAnalysisText + NANumber + " of the "
                    + edgeNumber + " arcs are not applicable for testing.\n\n";
        else
            completeAnalysisText = completeAnalysisText + "None of the "
                    + edgeNumber + " arcs are not applicable for testing.\n\n";

        if (workedNumber > 0)
            completeAnalysisText = completeAnalysisText + "The " + workedNumber
                    + " arcs that are consistent are: " + workedEdgeIDsText
                    + ".\n";

        if (notWorkedNumber > 0)
            completeAnalysisText = completeAnalysisText + "The "
                    + notWorkedNumber + " arcs that are inconsistent are: "
                    + notWorkedEdgeIDsText + ".\n";

        if (NANumber > 0)
            completeAnalysisText = completeAnalysisText + "The " + NANumber
                    + " arcs that are not applicable for test are: "
                    + NAEdgeIDsText + ".\n";

        // displaying check all states message
        String displayText = "Details of Changes:\n\n Primary Changes:\n";
        if (isFirstCheckAllStatesFlag()) {
            displayText = completeAnalysisText;
            setFirstCheckAllStatesFlag(false);
        } else
            displayText = displayText + goodChangeText + badChangeText
                    + notBadNotGoodChangeText + "\n\n" + secondaryEffectText
                    + "\n\n" + completeAnalysisText;

        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        Enumeration iter = getProblemModel().getProblemGraph().edges();
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            tempMyEdge = tempEdge.getEdgeData();

            tempMyEdge.setOldActionType(tempMyEdge.getActionType());
        }

        new CheckAllStatesReport(getDockedFrame(), displayText);
 
        getLoggingSupport().programActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
                BR_Controller.TEST_MODEL_ALL_STEPS_RESULT, "", displayText,
                "");

        return;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public void treatAsSameStates(ProblemNode checkedNode, Vector selection,
            Vector action, Vector input, MessageObject CommMsg,
            String authorIntent) {
    	demonstrateModeMessageHandler.treatAsSameState(selection, action, input, CommMsg,
    			authorIntent, checkedNode);
    }
    
    public EdgeData createNewEdge(ProblemNode sourceNode, ProblemNode destNode, Vector selection,    		
            Vector action, Vector input, MessageObject CommMsg,
            String authorIntent, List<ExampleTracerLink> traversedLinks) {
    	//trace.out("mg", "BR_Controller (createNewEdge): CommMsg = " + CommMsg.toString() + ", authorIntent = "
    	//		+ authorIntent + ", traversedLinks size = " + traversedLinks.size());
    	ProblemModel newEdgeModel = sourceNode.getProblemModel();
    	boolean createNewNode = (destNode==null);
    	if(createNewNode) {
		   int childCount = 0;
		   //Enumeration iterEdges = getProblemModel().getProblemGraph().getOutgoingEdges(
		   Enumeration<ProblemEdge> iterEdges = newEdgeModel.getProblemGraph().getOutgoingEdges(
		           sourceNode);
		   while (iterEdges.hasMoreElements()) {
		       iterEdges.nextElement();
		       childCount++;
		   }
		   destNode = createProblemNode(sourceNode, selection, childCount);
		}
    	
    	EdgeData myEdge = new EdgeData(getProblemModel());

    	if (myEdge.getUniqueID() > 1)
        	myEdge.setUniqueID(getProblemModel().getNextEdgeUniqueIDGenerator());  // extra increment for tests
        if (trace.getDebugCode("br")) trace.out("br", "BR_Controller.addNewState() myEdge id is "+myEdge.getUniqueID()+
        		", BR_label id is "+(myEdge.getActionLabel() == null ? "null" :
    				Integer.toString(((BR_Label)myEdge.getActionLabel()).getUniqueID())));
    	
        myEdge.setSelection((Vector) selection.clone());
        myEdge.setAction((Vector) action.clone());
        myEdge.setInput((Vector) input.clone());
        myEdge.setDemoMsgObj(CommMsg);
        myEdge.setActionType(authorIntent);

        myEdge.addRuleName(RuleProduction.UNNAMED);
        checkAddRuleName("unnamed", "");

        /*
        if (traversalCountEnabled)
            myEdge.incrementTraversalCount();
        
        setOriginalEdgeFont(myEdge.getActionLabel().getFont());
*/
        if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
            myEdge.updateDefaultHint();

        myEdge.getActionLabel().resetForeground();

        myEdge.getActionLabel().update();;

        //ProblemEdge edge = getProblemModel().getProblemGraph().addEdge(
        ProblemEdge edge = newEdgeModel.getProblemGraph().addEdge(
                sourceNode, destNode, myEdge);
        // for all out_edges of any node at most one edge is set preferPathMark
        // as true
        if (!authorIntent.equalsIgnoreCase(EdgeData.BUGGY_ACTION))
        	myEdge.setPreferredEdge(getProblemModel().checkSameParentEdges(
        			sourceNode));
        myEdge.getActionLabel().update();        
        edge.addEdgeLabels();

        // default: send CorrectAction Message to UniversalToolProxy??????
        if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
            sendCorrectActionMsg(selection, input, action);
        else
            sendIncorrectActionMsg(selection, input, action, false);       
        
        if(createNewNode)
        	//getProblemModel().fireProblemModelEvent(new NodeCreatedEvent(this, destNode));
        	newEdgeModel.fireProblemModelEvent(new NodeCreatedEvent(this, destNode));
        EdgeCreatedEvent edgeCreated = new EdgeCreatedEvent(this, myEdge.getEdge());
       // if(myEdge.getSelection().get(0).toString().compareToIgnoreCase("No_Selection")!=0)
        //	edgeCreated.setTryToSetCurrentStateTo(myEdge.getEdge().getDest().getUniqueID());    
        //getProblemModel().fireProblemModelEvent(edgeCreated);
        newEdgeModel.fireProblemModelEvent(edgeCreated);
        
        //Place the new link in the proper groups so that it can be traversed next
        //ExampleTracerLink link = getExampleTracerGraph().getLink(myEdge.getEdge().getUniqueID());
        ExampleTracerLink link = newEdgeModel.getExampleTracerGraph().getLink(myEdge.getEdge().getUniqueID());
        
        /*
        if((traversedLinks!=null)&&(traversedLinks.size()!=0)) {
        	ExampleTracerLink lastMatchedLink = traversedLinks.get(traversedLinks.size()-1);	        
	       // List<LinkGroup> groups = getExampleTracerGraph().findGroupsOfLink(lastMatchedLink);
	        LinkGroup smallestContainingGroup = getExampleTracerGraph().getSmallestContainingGroup(lastMatchedLink);
	        if(!smallestContainingGroup.equals(getExampleTracerGraph().getGroupModel().getTopLevelGroup()));
	        	getExampleTracerGraph().getGroupModel().addLinkToGroup(smallestContainingGroup, link);
	        // sewall 8/29/08 CTAT2057: don't add a group 
        }
        */
        if (isMainProblemModel(newEdgeModel))
        {
        	if((traversedLinks!=null)&&(traversedLinks.size()!=0)) {
        	ExampleTracerLink lastMatchedLink = traversedLinks.get(traversedLinks.size()-1);	        
        	// List<LinkGroup> groups = getExampleTracerGraph().findGroupsOfLink(lastMatchedLink);
        	LinkGroup smallestContainingGroup = getExampleTracerGraph().getSmallestContainingGroup(lastMatchedLink);
        	if(!smallestContainingGroup.equals(getExampleTracerGraph().getGroupModel().getTopLevelGroup()));
        		getExampleTracerGraph().getGroupModel().addLinkToGroup(smallestContainingGroup, link);
        	// sewall 8/29/08 CTAT2057: don't add a group 
        	}
        }
        
        return myEdge;                            
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////*/
    public void nextMode() {
        return;
    }

    // //////////////////////////////////////////////////////////////////////
    // 
    // //////////////////////////////////////////////////////////////////////
    /**
     * @return the new ProblemNode that is created
     */
    public ProblemNode addNewState(ProblemNode sourceNode, Vector selectionP,
            Vector actionP, Vector inputP, MessageObject CommMsgP,
            String actionType, boolean flagged_activation) {

    	//There will be two variables: newProblemNode will be the actual new
    	//ProblemNode that is created, and childCount will be the number of children
    	//of the sourceNode (aka, the ProblemNode that the newProblemNode is connected
    	//to)
        ProblemNode newProblemNode;
        int childCount = 0;
        
        //This variable, iterEdges, will count the number of children the 
        //sourceNode has
        Enumeration<ProblemEdge> iterEdges = getProblemModel().getProblemGraph().getOutgoingEdges(
                sourceNode);
        while (iterEdges.hasMoreElements()) {
            iterEdges.nextElement();
            childCount++;
        }

        //Create the actual newProblemNode
        newProblemNode = createProblemNode(sourceNode, selectionP, childCount);

        EdgeData edgeData = new EdgeData(getProblemModel());

        //Testing stuff
        if (edgeData.getUniqueID() > 1) {
        	edgeData.setUniqueID(getProblemModel().getNextEdgeUniqueIDGenerator());  // extra increment for tests
        }
        if (trace.getDebugCode("br"))
        	trace.out("br", "BR_Controller.addNewState() edgeData id is "+edgeData.getUniqueID()+
        			", BR_label id is "+(edgeData.getActionLabel() == null ? "null" :
        				Integer.toString(((BR_Label)edgeData.getActionLabel()).getUniqueID())));
        
        edgeData.addRuleName(RuleProduction.UNNAMED);

        if (traversalCountEnabled)
            edgeData.incrementTraversalCount();

        setOriginalEdgeFont(edgeData.getActionLabel().getFont());
        
        edgeData.setSelection((Vector) selectionP.clone());
        edgeData.setAction((Vector) actionP.clone());
        edgeData.setInput((Vector) inputP.clone());
        edgeData.setDemoMsgObj(CommMsgP);
        edgeData.setActionType(actionType);
        if (actionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
            edgeData.updateDefaultHint();
        }

        edgeData.getActionLabel().resetForeground();

        ProblemEdge problemEdge = getProblemModel().getProblemGraph().addEdge(
                sourceNode, newProblemNode, edgeData);
        if (!actionType.equalsIgnoreCase(EdgeData.BUGGY_ACTION))
        	edgeData.setPreferredEdge(getProblemModel().checkSameParentEdges(
        			sourceNode));
       
        //replace BRPanel.addEdgeLabels
        /*EdgeData myEdge = problemEdge.getEdgeData();
        myEdge.getActionLabel().addHandler(
                new ActionLabelHandler(myEdge.getActionLabel(), this));
         */
        
        problemEdge.addEdgeLabels(); // 08-16-2008 chc moved from BR to ProblemEdge
   
        // default: send CorrectAction message to UniversalToolProxy
        if (actionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
            sendCorrectActionMsg(selectionP, inputP, actionP, flagged_activation);
        }
        else {
            sendIncorrectActionMsg(selectionP, inputP, actionP, flagged_activation);
        }
  
        edgeData.getActionLabel().update();

        final ProblemEdge edge = edgeData.getEdge();
        Assert.assertNotNull(edge);
        //Moved to createProblemNode
       // trace.err("***** new problems is " + newProblemNode.getName());
        getProblemModel().fireProblemModelEvent(new NodeCreatedEvent(this, newProblemNode));
        getProblemModel().fireProblemModelEvent(new EdgeCreatedEvent(this, edge));
        return newProblemNode;
    }    

    public NodeView createStartStateNode(String name) {
        // Craete Start State
        NodeView newNodeView = new NodeView(name, this);
        ProblemGraph r = getProblemModel().getProblemGraph();
        ProblemNode node = new ProblemNode(newNodeView, getProblemModel());
        final ProblemNode newProblemNode = r.addProblemNode(node);
        getSolutionState().setCurrentNode(
                newProblemNode);
        getProblemModel().setStartNode(getSolutionState().getCurrentNode());
        //newNodeView.setLocation(new java.awt.Point(getJGraphWindow().getSize().width / 2
        newNodeView.setLocation(new java.awt.Point(getJGraphWindow().getSize().width / 2
                - newNodeView.getSize().width / 2, 30));
        newNodeView.setFont(new Font("", Font.BOLD | Font.ITALIC, 14));
        updateStatusPanel(null);
        return newNodeView;
    }

    /**
     * Refresh the list generated by {@link ProblemModel#startNodeMessagesIterator()} with any
     * current start state edits. Also send the list to the rule engine.
     * @param problemName
     */
    public void modifyStartState(String problemName) {
        Vector messageVector = new Vector();
        utp.createCurrentStateVector(messageVector, problemName, false); 
        getProblemModel().updateStartStateMessages(messageVector, utp);
		setStartStateModified(false);
    } //calcDaughterNodeLocation(sourceNode, nodeView, childCount)
    
    /**
     * @param sourceNode
     * @param selectionP
     * @param childCount
     * @return
     */
    public ProblemNode createProblemNode(ProblemNode sourceNode, Vector selectionP, int childCount) {
    	if(trace.getDebugCode("mg"))
    		trace.printStack("mg", "BR_Controller (createProblemNode): sourceNode = " + sourceNode.getUniqueID()
    				+ ", selectionP size = " + selectionP.size() + ", childCount = " + childCount);
    	ProblemNode result = createProblemNode(selectionP);
    	Point location = calcDaughterNodeLocation(sourceNode, result.getNodeView(), childCount);
    	result.getNodeView().setLocation(location);
    	return result;
    }
    
    /**
     * Create a new node. This is the lowest-level call.
     * @param selectionP
     * @param location location on the new graph canvas
     * @return new node
     */
    public ProblemNode createProblemNode(List<String> selectionP, Point location) {
    	if(trace.getDebugCode("mg"))
    		trace.printStack("mg", "BR_Ctlr.createProblemNode() selectionP="+selectionP+", location="+location);
    	ProblemNode result = createProblemNode(selectionP);
    	result.getNodeView().setLocation(location);
    	return result;
    }
    
    /**
     * Create a new node. This is the lowest-level call. Keep it private.
     * @param selectionP if the 1st selection matches "Done", call this a done node
     * @return new node
     */
    public ProblemNode createProblemNode(List<String> selectionP) {
    	if(trace.getDebugCode("mg"))
    		trace.printStack("mg", "BR_Ctlr.createProblemNode() selectionP="+selectionP);

    	//Variables for the newProblemNode and etc.
    	ProblemNode newProblemNode;
        boolean isDoneState = false;
        NodeView nodeView;
                
        //ProblemModel destination
        //ProblemModel dstProblemModel = getWindowManager().this.modelViewPair.getProblemModel();
        //ProblemModel pm = sourceNode.getProblemModel();
      //  trace.out("******** Selection is " + selectionP.get(0));
        if (selectionP != null && ((String) selectionP.get(0)).equalsIgnoreCase("Done")) {
        	//nodeView = new NodeView(getProblemModel().nextDoneName(), this);
        	nodeView = new NodeView(getProblemModel().nextDoneName(), this);
            isDoneState = true;
            //nodeView.setDoneState(true);
        } else {
        	nodeView = new NodeView(this);
        }
        
        //Get the problem graph and the current node
        //ProblemGraph problemGraph = getProblemModel().getProblemGraph();
        ProblemGraph problemGraph = getProblemModel().getProblemGraph();
        ProblemNode node = new ProblemNode(nodeView, getProblemModel());
        node.setDoneState(isDoneState);
        
        //Create a new state
        newProblemNode = problemGraph.addProblemNode(node);
//        setParentNodeInfo(newProblemNode);//save the parentnode state
		//[Kevin Zhao](kzhao) edit here for testing
        //getProblemModel().fireProblemModelEvent(new NodeCreatedEvent(this, newProblemNode));
        
        return newProblemNode;
    }

    /**
     * Calculate a location on the graph canvas for a new daughter node.
     * @param sourceNode parent node
     * @param nodeView daughter's NodeView
     * @param childCount parent's childCount
     * @return location for daughter node
     */
    private Point calcDaughterNodeLocation(ProblemNode sourceNode, NodeView nodeView,
    		int childCount) {
    	NodeView currVertex = null;
        if(sourceNode != null) {
        	currVertex = sourceNode.getNodeView();
        	if (currVertex != null) {
        		Point parentLocation = currVertex.getLocation();

        		parentLocation.x += currVertex.getSize().width / 2;

        		Point newLocation = BRPanel.getNewVertexLocation(parentLocation,
        				childCount);
        		newLocation.x -= nodeView.getSize().width / 2;
        		return newLocation;
        	}        
        }
        trace.err("calcDaughterNodeLocation("+sourceNode+", "+nodeView+", "+childCount+") currVertex "+
        		currVertex+" returning null");
		return null;
	}

	/**
     * createProblemNode2	-	This method is essentially createProblemNode, but it updates the graph, and hence
     * 							if anyone wishes to 'just add a node' onto the graph, use this method
     * @param sourceNode	-	This is the sourceNode, aka, where we are "supposed to" connect this new Node to
     * @param selectionP	-	selectionP is the selection
     * @param childCount	-	childCount is the expected number of children, or destinations, this node is supposed 
     * 							to have
     * @return
     */
    public ProblemNode createProblemNode2(ProblemNode sourceNode, Vector selectionP, int childCount) {
        ProblemNode newProblemNode = createProblemNode(sourceNode, selectionP, childCount);
        getProblemModel().fireProblemModelEvent(new NodeCreatedEvent(this, newProblemNode));
        return newProblemNode;
    }

    public void sendCorrectActionMsg(Vector selectionP, Vector inputP, Vector actionP) {
       sendCorrectActionMsg(selectionP, inputP, actionP, null, null, false);
    }
    
    public void sendCorrectActionMsg(Vector selectionP, Vector inputP, Vector actionP, boolean flagged_activation) {
        sendCorrectActionMsg(selectionP, inputP, actionP, null, null, flagged_activation);
    }
    
    public void sendCorrectActionMsg(Vector selectionP, Vector inputP, Vector actionP, String page) {
        sendCorrectActionMsg(selectionP, inputP, actionP, page, null, false);
     }
    
    // gus 03/04 - for multi-page interfaces
    public void sendCorrectActionMsg(Vector selectionP, Vector inputP,
    		Vector actionP, String page, String replay, boolean flagged_activation) {
     
        MessageObject newMessage = MessageObject.create(MsgType.CORRECT_ACTION, "SendNoteProperty");
   
        newMessage.setSelection(selectionP);
        newMessage.setAction(actionP);
        newMessage.setInput(inputP);
      
        if(page != null)
            newMessage.setProperty("Page", page);
        if(replay != null)
            newMessage.setProperty("replay", replay);
        newMessage.setTransactionId(getSemanticEventId());
        handleMessageUTP(newMessage, flagged_activation);
    }

    public void sendInterfaceActionMsg(Vector selectionP, Vector inputP,Vector actionP) 
    {
        MessageObject newMessage =
        	MessageObject.create(MsgType.INTERFACE_ACTION, "SendNoteProperty");
        
        newMessage.setSelection(selectionP);
        newMessage.setInput(inputP);
        newMessage.setAction(actionP);
        newMessage.setTransactionId(getSemanticEventId());

        if (trace.getDebugCode("br")) trace.out("br", "sendInterfaceActionMsg: " + newMessage.toString());
        handleMessageUTP(newMessage);
    }    
    
    public void sendStartStateCreatedMsg(String startStateName) {
        MessageObject newMessage = MessageObject.create(MsgType.START_STATE_CREATED, "SendNoteProperty");
        newMessage.setProperty("StartStateName", startStateName);
        newMessage.setTransactionId(getSemanticEventId());

        if (trace.getDebugCode("br")) trace.out("br", "sendStartStateCreatedMsg: " + newMessage.toString());
        handleMessageUTP(newMessage);
    }

    public void sendIncorrectActionMsg(Vector selectionP, Vector inputP, Vector actionP, boolean flagged_activation) {
        MessageObject newMessage = MessageObject.create(MsgType.INCORRECT_ACTION, "SendNoteProperty");
        newMessage.setSelection(selectionP);
        newMessage.setAction(actionP);
        newMessage.setInput(inputP);
        newMessage.setTransactionId(getSemanticEventId());

        if (trace.getDebugCode("br")) trace.out("br", "sendIncorrectActionMsg: " + newMessage.toString());
       	handleMessageUTP(newMessage);
    }

    public void sendResetActionMsg(Vector selectionP) {
        MessageObject newMessage = MessageObject.create(MsgType.RESET_ACTION, "SendNoteProperty");
        newMessage.setSelection(selectionP);
        newMessage.setTransactionId(getSemanticEventId());

        if (trace.getDebugCode("br")) trace.out("br", "sendResetActionMsg: " + newMessage.toString());
        handleMessageUTP(newMessage);
    }

    // recursive call to send Comm Msgs from atNode state until the targetNode
    // state
    public void sendCommMsgs(ProblemNode sourceNode, ProblemNode targetNode) {
       
        if (sourceNode == targetNode && targetNode == getProblemModel().getStartNode()) {
            if (trace.getDebugCode("br")) trace.out("br", "sendCommgMsgs: if");
            sendStartNodeMessages(null, false);
            if (trace.getDebugCode("br")) trace.out("br", "sentCommMsgs: if");
        } else {
            if (trace.getDebugCode("br")) trace.out("br", "sendCommgMsgs: else");
            sendMessagesFromSourceToTargetNodes(sourceNode, targetNode);
        }

        if (trace.getDebugCode("br")) trace.out("br", "CtatModeModel = " + getCtatModeModel() + " ProblemModel = " + getProblemModel());
//        if (getMode().equalsIgnoreCase(CtatModeModel.EXAMPLE_TRACING_MODE)
        
        /* Sends too many out
         * if (getCtatModeModel().isExampleTracingMode() && getProblemModel().isUnorderedMode()) {
            sendUnorderedEdges();
        }
        */
        return;
    }

	private void sendMessagesFromSourceToTargetNodes(ProblemNode sourceNode, ProblemNode targetNode) {
	    if (trace.getDebugCode("br")) trace.out("br", "entered sendMessagesFromSourceToTargetNodes");
	    if (trace.getDebugCode("br")) trace.out("br", "sourceNode =" + sourceNode.getName() + "    targetNode = " + targetNode.getName());
		
		ProblemEdge foundEdge;
		ProblemNode parentTemp;

		foundEdge = getProblemModel().findIncomingEdgeForCommMsg(sourceNode);

		if(foundEdge != null) {
		    parentTemp = foundEdge.getNodes()[ProblemEdge.SOURCE];

		    sendCommMsgs(parentTemp, targetNode);
		    sendSingleCommMsg(foundEdge, true);
		} // end if (tempEdge != null)
	}
    
    /**
     * Equivalent to
     * {@link #openBRDFileAndSendStartState(String, String, Skills) openBRDFileAndSendStartState(filename, problemName, null)}.
     * @param filename path to load
     * @param problemName externally-specified problem name, to be used instead
     *                    of name on start node
     * @return false if fails
     */
    public boolean openBRDFileAndSendStartState(String filename, String problemName) {
    	return openBRDFileAndSendStartState(filename, problemName, null);
    }

    /**
     * Common entry point to load a BRD file. If no student interface
     * is loaded, calls {@link BR_Controller#setBRDDirectory(String)}.
     * @param filename path to load
     * @param problemName externally-specified problem name, to be used instead
     *                    of name on start node
     * @param skills externally-specified skills, to be used instead of those defined in the file
     * @return true on success
     */
    public boolean openBRDFileAndSendStartState(String filename, String problemName,
    		Skills skills) {
    	long sts = System.currentTimeMillis();
    	if(filename.length() == 0) {
    		if(!(this.getProblemModel().isEmpty())) {
    			this.getProblemModel().reset("", "");
    		}
    	}
    	if(getUniversalToolProxy() != null) {
    		getUniversalToolProxy().resetStartStateModel();
    		if(!Utils.isRuntime())
    			getUniversalToolProxy().getAllInterfaceDescriptions();  // repopulate after reset
    	}
    	
    	boolean result = getProblemStateReader().openBRDiagramFile(filename);

    	ProblemModel pm = getProblemModel();
    	if (trace.getDebugCode("advanceProblem")) trace.outln("advanceProblem", "openBRDFileAndSendStartState("+filename+","+problemName+") returns "+
				result+"; ms "+(System.currentTimeMillis()-sts));
    	// logging
    	if ((problemName != null) && (problemName.length() > 0)) {
    		if (getTSLauncherServer().getLogger() != null)
    			getTSLauncherServer().getLogger().setProblemName(problemName);
    		if (pm != null)
    			pm.setProblemName(problemName);
    	}
    	// handle skills/summary
		ProblemSummary ps = null;
    	if (skills != null && (ps = this.getProblemModel().getProblemSummary()) != null)
    		ps.setSkills(skills);
    	
		String newBRMode = pm.getBehaviorRecorderMode();
		if (newBRMode != null)
			setBehaviorRecorderMode(newBRMode);
    	//clearUnmatchedComponentsAndReviseConnectionStatus();

    	// update information about the graph
    	if (result) {
    		goToStartState(true, true);
    	}
    	updateStatusPanel(null);
       	
    	// save the current state
    	if (!Utils.isRuntime()) {
    			getProblemStateReader().saveImage(getProblemStateWriter());
    	}

		if (trace.getDebugCode("advanceProblem")) trace.outln("advanceProblem", "openBRDFileAndSendStartState() after start state; ms "+
				(System.currentTimeMillis()-sts));

		if(!Utils.isRuntime())
			getServer().getDockManager().refreshGraphTitle(this.tabNumber);
    	clearUnmatchedComponentsAndReviseConnectionStatus();
    	return result;
    }
	
    /************************************* UNDO TEST 1337 ****************************/

	public boolean openBRDFileAndSendStartStateAux(InputStream bais,
			Skills skills) {
		System.out.println("*** Aux-Load Called ***");
		long sts = System.currentTimeMillis();
		ProblemModel pm = getProblemModel();
		String problemName = (pm == null ? null : pm.getProblemName());
		String problemFullName = (pm == null ? null : pm.getProblemFullName());
		boolean result = getProblemStateReader().openBRDiagramFile1337(bais, null);
		if (trace.getDebugCode("undo"))
			trace.out("undo", "openBRDFileAndSendStartState() problemName " + problemName +
					") returns " + result + "; ms " + (System.currentTimeMillis() - sts));
		if (problemFullName != null && problemFullName.length() > 0)
			getProblemModel().setProblemFullName(problemFullName);
		if (getProblemModel() != null)
			getProblemModel().setProblemName(problemName);
		// controller things
		if ((problemName != null) && (problemName.length() > 0)) {
			if (getTSLauncherServer().getLogger() != null)
				getTSLauncherServer().getLogger().setProblemName(problemName);
			//if (getProblemModel() != null)
			//	getProblemModel().setProblemName(problemName);
		}
		ProblemSummary ps = null;
		if (skills != null && (ps = this.getProblemModel().getProblemSummary()) != null)
			ps.setSkills(skills);

		String newBRMode = getProblemModel().getBehaviorRecorderMode();
		if (newBRMode != null)
			setBehaviorRecorderMode(newBRMode);

		if (result) {
			goToStartState(true, true);
		}
		updateStatusPanel(null);

		if (trace.getDebugCode("undo"))
			trace.outln("undo",
					"openBRDFileAndSendStartState() after start state; ms "
							+ (System.currentTimeMillis() - sts));
		return result;
	}
    
	/**
	 * Send the messages in the list returned by
	 * {@link ProblemModel#getStartNodeMessageVector()}.
	 * @param problemName if not null, use as problem name in the StartProblem
	 *        message
	 * @param suppressStartStateEnd if true, suppress StartStateEnd so that it can be
	 *        sent after advance-to-student-starts-here
	 */
	void sendStartNodeMessages(String problemName, boolean suppressStartStateEnd) {
		sendStartNodeMessages(problemName, suppressStartStateEnd, true);
    }    
    
	/**
	 * Send the messages in the list returned by
	 * {@link ProblemModel#getStartNodeMessageVector()}.
	 * @param problemName if not null, use as problem name in the StartProblem
	 *        message
	 * @param suppressStartStateEnd if true, suppress StartStateEnd so that it can be
	 *        sent after advance-to-student-starts-here
	 * @param lockWidgets if true, send the {@value MsgType#SEND_WIDGET_LOCK} with argument
	 * 		  {@link ProblemModel#getLockWidget()} telling whether to lock widgets in the start state;
	 *        if false, send no message and, after a {@link MsgType#START_PROBLEM} msg, call
	 *        {@link #setStartStateInterface(boolean) setStartStateInterface(false)} to undo the lock
	 *        that {@link #doStartProblem_movedFromCommWidget()} effects on the start state
	 */
	private void sendStartNodeMessages(String problemName, boolean suppressStartStateEnd,
			boolean lockWidgets) {
		
		if (trace.getDebugCode("br")) trace.out("br", "sendStartNodeMessages");
		if (trace.getDebugCode("startstate")) trace.printStack("startstate", "sendStartNodeMessages");
		// Tasmia: This iterator is reponsible for changing the content in the interface. I need to find
		// out when it is updated and if we do not update it, then if the activation function
		// can generate the correct production rule.
		Iterator<MessageObject> it = getProblemModel().startNodeMessagesIterator();
		if(!Utils.isRuntime() && getUniversalToolProxy() != null)
			it = getUniversalToolProxy().startNodeMessagesIterator(getProblemModel());
		
		sendInterfaceReboot();
		sendStateGraphMessage();
		if (trace.getDebugCode("br")) trace.out("br", "After sendStateGraphMessage");

//        saveOriginalStartStateMessages(getProblemModel().getStartNodeMessageVector());
		for (int i = 0; it.hasNext(); i++) {
		    MessageObject messageObject = it.next(); 
		    String messageType = messageObject.getMessageType();
			if (trace.getDebugCode("br")) trace.out("br", "startNodeMessageVector["+i+"]: "+messageType);
			
			// Moved to processStartNodeElement() to solve the hint message bug at CTAT-1976
			// Need to make sure modifyStartState do call addInterfaceVariables().
			messageObject = getProblemModel().interpolateAllValues(messageObject);
			
			CTATComponent.editInterfaceDescriptionMessage(messageObject,
					getUniversalToolProxy() instanceof HTTPToolProxy ?
							IncludeIn.full : getProblemModel().getInterfaceDescriptionFilter());

		    editAndLogStartProblemMessage(messageObject, problemName);
		    if (trace.getDebugCode("br")) trace.out("br", "editAndLogStartProblemMessage(problemName "+problemName+")");
			
		    if ("StartStateEnd".equalsIgnoreCase(messageType)) {
		    	if (suppressStartStateEnd)
		    		continue;
		    	if(lockWidgets)
		    		sendStartStateMsg(PseudoTutorMessageBuilder.createLockWidgetMsg(getProblemModel().getLockWidget()));
		    }
		    if (!(MsgType.START_PROBLEM.equalsIgnoreCase(messageType)))
		    	messageObject.suppressLogging(true);      // suppress logging if not the context msg

		    sendStartStateMsg(messageObject);

		    if(MsgType.START_PROBLEM.equalsIgnoreCase(messageType) && !lockWidgets)
		        setStartStateInterface(false);            // prevent Java tutors from locking start state
		}
		//steelers
		getExampleTracer().setStartStateVT(getProblemModel().getVariableTable());
		//getExampleTracer().initialize(problemModel.getVariableTable());
		if (unmatchedComponents.size() > 0 && !getProblemModel().isProblemLoadedFromLispTutor()) {
//			trace.err("<html>The following widgets referenced in the behavior graph are"+
//					" not in the interface.<br />" +
//					Utils.listToHtmlTbl(unmatchedComponents, 4)+
//					"<br />The graph and interface may be incompatible.</html>");
		}
        
	    if (trace.getDebugCode("br")) trace.out("br", "return from sendStartNodeMessages");
        getProblemModel().getProblemSummary().startTimer();
	}

	/**
	 * Message type {@value MsgType#INTERFACE_REBOOT} should be used if {@link #getCommShellVersion()}
	 * is 3.3 or later. We used to rely on {@value MsgType#START_PROBLEM} to clear old entries off
	 * the interface, but it also generated and sent all the {@value MsgType#INTERFACE_DESCRIPTION}
	 * messages.  
	 */
	private void sendInterfaceReboot() {
		if(!clientSupports(MsgType.INTERFACE_REBOOT))
			return;
		MessageObject mo = MessageObject.create(MsgType.INTERFACE_REBOOT);
		sendStartStateMsg(mo);
	}

	public void processInterfaceVariables(MessageObject messageObject) {
        if (getProblemModel() != null)
        	getProblemModel().addInterfaceVariables(messageObject);
	}
 		
	/** msweber 5/24/07 - added a new "StateGraph" message which is sent before 
	 *                    the StartState information.  This contains properties
	 *                    such as caseInsensitive, lockWidget, and suppressStudentFeedback
	 *                    I'm making this message only send when we're in the Tutoring Service
	 *                    since it doesn't seem like any other application needs this extra
	 *                    message.
	 */
	private void sendStateGraphMessage(){
		//if (!isOnline.equals("false"))  // MSWEBER had this in but I took it out because I want it to work in AT and ATT in Flash. 	
		MessageObject stateGraphObject = MessageObject.create("StateGraph", "SendNoteProperty");

		stateGraphObject.setProperty("caseInsensitive", "" + getProblemModel().isCaseInsensitive());
		
		stateGraphObject.setProperty("unordered", "" + getProblemModel().isUnorderedMode());
		
		stateGraphObject.setProperty("lockWidget", "" + getProblemModel().isLockWidget());
            	
		stateGraphObject.setProperty(SUPPRESS_STUDENT_FEEDBACK, "" +
				(getProblemModel().getSuppressStudentFeedback() == FeedbackEnum.HIDE_ALL_FEEDBACK));
    	
		stateGraphObject.setProperty("highlightRightSelection", "" + getProblemModel().getHighlightRightSelection());
    	
		stateGraphObject.setProperty("confirmDone",
				Boolean.toString(getProblemModel().getEffectiveConfirmDone()));

		ProblemSummary ps = getProblemSummary();
		if (trace.getDebugCode("br")) trace.out("br", "sendStateGraph: problemSummary\n"+ps.toXML());
		if (ps != null && ps.getSkills() != null && !ps.getSkills().isExternallyDefined()) {
			Skills skills = ps.getSkills();
		    if (skills != null)
		    	skills.setVersion(commShellVersion);
			String delimiter = skills.getSkillBarDelimiter();
			if(trace.getDebugCode("skills"))
				trace.out("skills", "BR_C.sendStateGraphMsg: delimiter "+delimiter+", skills\n  "+skills);
			if (skills != null && skills.getAllSkills().size() > 0) {
				stateGraphObject.setProperty(MessageObject.SKILL_BAR_DELIMITER_TAG, delimiter);
				stateGraphObject.setProperty("Skills", skills.getSkillBarVector(true, true));
			}
		}

		sendStartStateMsg(stateGraphObject);
		//steelers?
		getExampleTracer().setStartStateVT(getProblemModel().getVariableTable());
	//	getExampleTracer().initialize(problemModel.getVariableTable());
	}
	

	/**
	 * Edit a StartProblem message to set the given problem name.
	 * Also logs the message, to generate the context_message required for the DataShop.
	 * @param messageObject CommMessage to edit
	 * @param problemName new problem name; no-op if null or empty 
	 */
	private void editAndLogStartProblemMessage(MessageObject messageObject, String problemName) {
	    String msgType = messageObject.getMessageType();
	    if (!("StartProblem".equalsIgnoreCase(msgType)))
	    	return;
        if (trace.getDebugCode("log")) trace.out("log", "to log "+msgType+" "+problemName+"; loggingSupport "+getTSLauncherServer().getLoggingSupport());
	    if (problemName != null && problemName.length() >0)
	    	messageObject.setProperty("ProblemName", problemName);
	    messageObject.setProperty("AuthorMode", getCtatModeModel().getCurrentAuthorMode());
	    getLoggingSupport().setDatasetName(getLogger().getCourseName());
	    getLogger().oliLog(messageObject, true);
	}

    public void sendSingleCommMsg(ProblemEdge targetEdge,
            boolean checkCorrectFlag) {
        EdgeData myCurrEdge = targetEdge.getEdgeData();

        // gus 03/04 - for multi-page interfaces
        edu.cmu.pact.ctat.MessageObject obj = myCurrEdge.getDemoMsgObj();
                
        String page = null;
        String replay = null;
        try {
            page = (String) obj.getProperty("Page");
            replay = (String) obj.getProperty(TutorActionLogV4.REPLAY);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        
        if (checkCorrectFlag) {

//            if (!getMode().equalsIgnoreCase(
//                    CtatModeModel.PRODUCTION_SYSTEM_MODE)) {
            if (!(getCtatModeModel().isJessMode() || getCtatModeModel().isTDKMode())) {
                if (myCurrEdge.getActionType().equalsIgnoreCase(
                        EdgeData.CORRECT_ACTION)) {
                	sendCorrectActionMsg(myCurrEdge.getStudentSelection(),
                                myCurrEdge.getStudentInput(), myCurrEdge
                                        .getStudentAction(), page, replay, false); //we now handle null's on page and replay together
                } else
                    sendIncorrectActionMsg(myCurrEdge.getSelection(),
                            myCurrEdge.getStudentInput(), myCurrEdge
                            .getStudentAction(), false);

                return;
            }

            if (myCurrEdge.getCheckedStatus()
                    .equalsIgnoreCase(EdgeData.SUCCESS)) {
                if (page == null)
                    sendCorrectActionMsg(myCurrEdge.getStudentSelection(),
                            myCurrEdge.getStudentInput(), myCurrEdge
                                    .getStudentAction());
                else
                    sendCorrectActionMsg(myCurrEdge.getStudentSelection(),
                            myCurrEdge.getStudentInput(), myCurrEdge
                                    .getStudentAction(), page);
            } else if (myCurrEdge.getCheckedStatus().equalsIgnoreCase(
                    EdgeData.NEVER_CHECKED)) {
                if (myCurrEdge.getActionType().equalsIgnoreCase(
                        EdgeData.CORRECT_ACTION)) {
                    if (page == null)
                        sendCorrectActionMsg(myCurrEdge.getStudentSelection(),
                                myCurrEdge.getStudentInput(), myCurrEdge
                                        .getStudentAction());
                    else
                        sendCorrectActionMsg(myCurrEdge.getStudentSelection(),
                                myCurrEdge.getStudentInput(), myCurrEdge
                                        .getStudentAction(), page);
                } else {
                    sendIncorrectActionMsg(myCurrEdge.getSelection(),
                            myCurrEdge.getStudentInput(), myCurrEdge
                            .getStudentAction(), false);
                }
            } else
                sendIncorrectActionMsg(myCurrEdge.getSelection(), myCurrEdge
                        .getStudentInput(), myCurrEdge
                        .getStudentAction(), false);
        } else {
            edu.cmu.pact.ctat.MessageObject mo = null; // clone edge msg before
            // setSemanticEvent
            try {
                mo = myCurrEdge.getDemoMsgObj().copy();
                mo.setTransactionId(getSemanticEventId());
            } catch (Exception e) {
                System.err.println("Clone edge msg failed: actionLabel "
                        + myCurrEdge.getActionLabel().getText());
                e.printStackTrace();
                mo = myCurrEdge.getDemoMsgObj();
            }
            handleMessageUTP(mo);
        }
        return;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public void sendSuccessMsg(String successMsg) {
        MessageObject newMessage = MessageObject.create(MsgType.SUCCESS_MESSAGE, "SendNoteProperty");
        newMessage.setProperty("SuccessMsg", successMsg);
        newMessage.setTransactionId(getSemanticEventId());

        handleMessageUTP(newMessage);
    }

    public void sendBuggyMsg(String buggyMsg, Vector selectionP, Vector actionP) {
        MessageObject newMessage = MessageObject.create(MsgType.BUGGY_MESSAGE, "SendNoteProperty");
        newMessage.setProperty("BuggyMsg", buggyMsg);
        if (selectionP != null) {
            newMessage.setSelection(selectionP);
			newMessage.setAction(actionP);
        }
        newMessage.setTransactionId(getSemanticEventId());
        handleMessageUTP(newMessage);
    }

    public void sendUnlockMsg(Vector selection) {
        MessageObject newMessage = MessageObject.create("UnlockComposer", "SendNoteProperty");
        newMessage.setSelection(selection);
        newMessage.setTransactionId(getSemanticEventId());

        handleMessageUTP(newMessage);
        return;
    }

    public void sendHighlightMsg (String highlightMsgText,Vector selection,Vector action) 
    {
        MessageObject newMessage = MessageObject.create("HighlightMsg", "SendNoteProperty");

        newMessage.setProperty("HighlightMsgText", highlightMsgText);
        newMessage.setSelection(selection);
        newMessage.setAction(action);
        newMessage.setTransactionId(getSemanticEventId());

        handleMessageUTP(newMessage);
    }
    
    public void sendUnHighlightMsg (String highlightMsgText,Vector selection,Vector action) 
    {
        MessageObject newMessage = MessageObject.create("UnHighlightMsg", "SendNoteProperty");

        newMessage.setProperty("HighlightMsgText", highlightMsgText);
        newMessage.setSelection(selection);
        newMessage.setAction(action);

        handleMessageUTP(newMessage);
    }    

    public void sendHintsMsg(ProblemEdge problemEdge) {
        MessageObject newMessage = MessageObject.create("ShowHintsMessage", "SendNoteProperty");
        EdgeData targetMyEdge = problemEdge.getEdgeData();

        Vector hints = targetMyEdge.getHints();
        newMessage.setProperty("HintsMessage", hints);

        Vector selection = targetMyEdge.getSelection();
        newMessage.setSelection(selection);

        Vector action = targetMyEdge.getAction();
        newMessage.setAction(action);

        Vector input = targetMyEdge.getInput();
        newMessage.setInput(input);

        Vector namedRules = ProblemModel
                .getNamedRules(targetMyEdge.getSkills());
        if (namedRules.size() > 0) {
            newMessage.setProperty("Rules", namedRules);
        }
        newMessage.setTransactionId(getSemanticEventId());

        handleMessageUTP(newMessage);
    }

    public void sendGo_To_WM_State(Vector selectionList, Vector actionList,
            Vector inputList, Vector authorIntentList, Vector uniqueIDList) {
    	sendGo_To_WM_State(selectionList, actionList, inputList,
				authorIntentList, uniqueIDList, false);
    } 
    public void sendGo_To_WM_State(Vector selectionList, Vector actionList,
            Vector inputList, Vector authorIntentList, Vector uniqueIDList,
            boolean goToStart) {
   	
    	MessageObject msg = buildGo_To_WM_State(selectionList, actionList,
                inputList, authorIntentList, uniqueIDList);

    	if (goToStart)
    		goToStartState();
    	else {
    		if (trace.getDebugCode("missmt1")) trace.out("missmt1", "sending start comm MSGs to LISP: ");
    		Iterator<MessageObject> it = getProblemModel().startNodeMessagesIterator();
    		for (int i = 0; it.hasNext(); i++) {
    			// trace.out ("missmt1", "Sending start Comm Message " + (i+1) +" to LISP: ");
    			MessageObject startNodeMsg = it.next();
    			trace.out ("missmt1", "Start Comm Message [" + (i+1) + "] --> " + startNodeMsg);
    			utp.sendProperty(startNodeMsg);
    		}
    	}
        if (trace.getDebugCode("missmt1")) trace.out("missmt1", "sendGo_To_WM_State: utp.sendPropery\n"+msg);
        utp.sendProperty(msg);
    }

    private MessageObject buildGo_To_WM_State(Vector selectionList, Vector actionList,
            Vector inputList, Vector authorIntentList, Vector uniqueIDList) {
    	MessageObject newMessage = MessageObject.create(MsgType.GO_TO_WM_STATE);
    	newMessage.setVerb("SendNoteProperty");
    	newMessage.setProperty("SelectionList", selectionList, true);  // true=>use as is
    	newMessage.setProperty("ActionList", actionList, true);
    	newMessage.setProperty("InputList", inputList, true);
    	newMessage.setProperty("AuthorIntentList", authorIntentList, true);
        newMessage.setProperty("UniqueIDList", uniqueIDList, true);
        return newMessage;
    }

    
    // send ESE-Graph (as Vector) to LISP
    public void sendBehaviorRecorderGraphToLisp(Vector ESEGraph,
            ProblemNode atNode, int nodeDepth) {

        getProblemModel().getCheckAllNodes().addElement(atNode);
        Vector parentChildList = new Vector();
        NodeView Vertext = atNode.getNodeView();

        // add parent node name with UniqueID
        parentChildList.addElement(Vertext.getText() + "_"
                + Vertext.getUniqueID());

        Vector childrenInfoList = new Vector();

        Vector childInfo;

        ProblemEdge edgeTemp;
        EdgeData myEdge;
        ProblemNode childTemp;

        Vector uncheckedChildrenList = new Vector();

        Enumeration iterOutEdge = getProblemModel().getProblemGraph().getOutgoingEdges(
                atNode);
        while (iterOutEdge.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iterOutEdge.nextElement();
            childTemp = edgeTemp.getNodes()[ProblemEdge.DEST];

            if (!getProblemModel().nodeChecked(childTemp))
                uncheckedChildrenList.addElement(childTemp);

            childInfo = new Vector();
            // add child name
            Vertext = childTemp.getNodeView();
            childInfo.addElement(Vertext.getText() + "_"
                    + Vertext.getUniqueID());

            myEdge = edgeTemp.getEdgeData();
            // add uniqeID
            childInfo.addElement(new Integer(myEdge.getUniqueID()));
            // add authorIntent
            // if(myEdge.actionLabel.buggyMsg.equals(""))
            childInfo.addElement(myEdge.getActionType());
            // add selection
            childInfo.addElement(myEdge.getSelection());
            // add action
            childInfo.addElement(myEdge.getAction());
            // add input
            childInfo.addElement(myEdge.getInput());

            childrenInfoList.addElement(childInfo);
        }

        if (childrenInfoList.size() != 0)
            parentChildList.addElement(childrenInfoList);

        parentChildList.addElement(new Integer(nodeDepth));

        ESEGraph.addElement(parentChildList);

        int uncheckedChildrenNum = uncheckedChildrenList.size();
        for (int i = 0; i < uncheckedChildrenNum; i++)
            sendBehaviorRecorderGraphToLisp(ESEGraph,
                    (ProblemNode) uncheckedChildrenList.elementAt(i),
                    nodeDepth + 1);

        if (getProblemModel().getCheckAllNodes().size() == getProblemModel()
                .getProblemGraph().getNodeCount()
                && !isSendESEGraphFlag()) {
            ESEGraph = rebuildBehaviorRecorderGraph(ESEGraph);
            MessageObject newMessage = MessageObject.create(MsgType.SEND_ESE_GRAPH, "SendNoteProperty");
            newMessage.setProperty("ESEGraph", ESEGraph, true);

            // trace.out ( "For send ESE_graph first sending start comm MSGs
            // to LISP: ");
            for (Iterator<MessageObject> it = getProblemModel().startNodeMessagesIterator(); it.hasNext();) {
                // trace.out ( "Sending start Comm Message " + (i+1) +" to
                // LISP: " + (MessageObject)
                // (startNodeMessageVector.elementAt(i)));
                utp.sendProperty(it.next());
            }

            if (trace.getDebugCode("mt")) trace.out("mt", "ESE_Frame send ese-graph to LISP");
            if (trace.getDebugCode("mt")) trace.out("mt", "newMessage = " + newMessage.toString());
            utp.sendProperty(newMessage);

            getLoggingSupport().authorActionLog(
                    AuthorActionLog.BEHAVIOR_RECORDER,
                    BR_Controller.TEST_MODEL_ALL_STEPS, "", "", "");

            // trace.out ( "ESE_Frame send ese-graph to LISP");
            setSendESEGraphFlag(true);

            return;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * pretest which links are to be deleted if testNode is deleted.
     */
 /*   public// ////////////////////////////////////////////////////////////////////////////////
    void preTestWillDeleteLinks(ProblemNode testNode) {

        getProblemModel().setWillDeleteLinks(new Vector());

        // pretest will deleted links
        deleteNode(testNode, false);
        // trace.out(5, this, "willDeleteLinks.size() = " +
        // willDeleteLinks.size());

        // pretest affected link group
        getProblemModel().setWillRemovedLinkGroups(new Vector());
        getProblemModel().preTestWillRemovedLinksGroups();

        return;
    }*/

    public void findPathForProductionRulesChecking(ProblemNode atNode,
            Vector pathEdges) {
        ProblemEdge edgeTemp;
        EdgeData myEdgeTemp;

        Enumeration iter = getProblemModel().getProblemGraph().getConnectingEdges(
                atNode);
        while (iter.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iter.nextElement();

            if (edgeTemp.getNodes()[ProblemEdge.SOURCE] != atNode) {
                myEdgeTemp = edgeTemp.getEdgeData();
                pathEdges.addElement(edgeTemp);

                if (edgeTemp.getNodes()[ProblemEdge.SOURCE] == getProblemModel()
                        .getStartNode()) {
                    Vector selectionList = new Vector();
                    Vector actionList = new Vector();
                    Vector inputList = new Vector();
                    Vector authorIntentList = new Vector();
                    Vector uniqueIDList = new Vector();

                    int sizeOfpathEdges = pathEdges.size();
                    for (int i = sizeOfpathEdges - 1; i >= 0; i--) {

                        edgeTemp = (ProblemEdge) pathEdges.elementAt(i);
                        myEdgeTemp = edgeTemp.getEdgeData();
                        // trace.out( "here4");
                        selectionList.addElement(myEdgeTemp.getSelection());
                        actionList.addElement(myEdgeTemp.getAction());
                        inputList.addElement(myEdgeTemp.getInput());
                        authorIntentList.addElement(myEdgeTemp.getActionType());
                        uniqueIDList.addElement(new Integer(myEdgeTemp.getUniqueID()));
                    }

                    // trace.out ( "Use one path to sendGo_To_WM_State.");
                    sendGo_To_WM_State(selectionList, actionList, inputList,
                            authorIntentList, uniqueIDList);
                } else
                    findPathForProductionRulesChecking(
                            edgeTemp.getNodes()[ProblemEdge.SOURCE], pathEdges);

                return;
            }
        }

        return;
    }

    public void processDeleteNode(ProblemNode deleteNode){
    	//ExampleTracerNode toDelete = getExampleTracerGraph().getNode(deleteNode.getUniqueID());
    	ExampleTracerNode toDelete = deleteNode.getProblemModel().getExampleTracerGraph().getNode(deleteNode.getUniqueID());
    	
    	Set<ExampleTracerNode> nodesReachableFromDeleteNode =  new HashSet<ExampleTracerNode>();
    	getExampleTracerGraph().getNodesReachableFrom(toDelete, nodesReachableFromDeleteNode);
    	
    	Set<ExampleTracerNode> nodesReachable =  new HashSet<ExampleTracerNode>();
    	getExampleTracerGraph().getNodesReachableFrom(getExampleTracerGraph().getStartNode(), nodesReachable);
    	
    	Set<ExampleTracerNode> nodesReachableWithOutDeleteNode =  new HashSet<ExampleTracerNode>();
    	getExampleTracerGraph().getNodesReachableIgnoringX(getExampleTracerGraph().getStartNode(),toDelete, nodesReachableWithOutDeleteNode);
    	
    	boolean deleteNodeConnected = getExampleTracerGraph().isNodeConnected(deleteNode.getUniqueID());
    	
    	boolean subGraphDisconnected = false;
    	if(((!deleteNodeConnected)&& deleteNode.getConnectingEdges().size()>0) ||
    			(nodesReachable.size() > (nodesReachableWithOutDeleteNode.size()+1))){
    		subGraphDisconnected = true;
    	}
    	
    	int result = doDeleteNodeDialogue(deleteNode, subGraphDisconnected);
    	
    	if(result==3)
    		return;
    	if(result==2){
    		List<ProblemModelEvent> events = deleteSingleNode(deleteNode);
    		//deleteSingleNode sets the first element of the returned List to be
    		//a NodeDeletedEvent with an empty List of subevents.
    		NodeDeletedEvent fireMe = (NodeDeletedEvent)events.get(0);
    		List<ProblemModelEvent> addToMe = fireMe.getSubevents();
    		addToMe.addAll(new ArrayList(events.subList(1, events.size())));
    		getProblemModel().fireProblemModelEvent(fireMe);
    		System.out.println("***END:DELETE:2***");
    		
    		//Undo checkpoint for deleting node ID: 1337
    		ActionEvent ae = new ActionEvent(this, 0, "Delete state "+deleteNode.getName());
    		getUndoPacket().getCheckpointAction().actionPerformed(ae);
    		return;
    	}
    	nodesReachableFromDeleteNode.removeAll(nodesReachableWithOutDeleteNode);
    	List<ProblemModelEvent> events = deleteSingleNode(deleteNode);
    	NodeDeletedEvent fireMe = (NodeDeletedEvent)events.get(0);
    	List<ProblemModelEvent> addToMe = fireMe.getSubevents();
		addToMe.addAll(new ArrayList(events.subList(1, events.size())));
    	//NodeDeletedEvent currEvents;
    	for(Iterator<ExampleTracerNode> it = nodesReachableFromDeleteNode.iterator(); it.hasNext();){
    		ProblemNode next = it.next().getProblemNode();
    		events = deleteSingleNode(next);
    		addToMe.addAll(events);
    	}
    	getProblemModel().fireProblemModelEvent(fireMe);
    	
    	System.out.println("***END:DELETE:1***");
    	
    	//Undo checkpoint for deleting node ID: 1337
    	ActionEvent ae = new ActionEvent(this, 0, "Delete subgraph at state "+deleteNode.getName());
    	getUndoPacket().getCheckpointAction().actionPerformed(ae);
    }
    
    public int doDeleteNodeDialogue(ProblemNode deleteNode, boolean subGraphDisconnected){
    	String jDialogResult = null;
    	//String promptText ="Delete this state and...";
    	String deleteSubGraphText = "Delete connecting links and subgraph";
		String deleteText = "Delete connecting links only";
		String cancelText = "Cancel";
		JButton deleteSubGraph = new JButton(deleteSubGraphText);
		if(!subGraphDisconnected){
			deleteSubGraph.setEnabled(false);
			deleteSubGraph.setToolTipText("State deletion doesn't create a disconnected subgraph to delete.");
		}
		JButton delete = new JButton(deleteText);
		JButton cancel = new JButton(cancelText);
		JOptionPane op = new JOptionPane();
		op.setMessage(new Object[] {"Delete this state and...", deleteSubGraph,delete});
		Object[] empty = {cancel};
		op.setOptions(empty);
		final JDialog d = op.createDialog(getServer().getActiveWindow(), "Delete State");
		class DeleteNodeActionListener implements ActionListener {
			protected String jDialogResult;
			
			public void actionPerformed(ActionEvent e){
				this.jDialogResult = e.getActionCommand();
				d.dispose();
			}
		};
		DeleteNodeActionListener closeDialog = new DeleteNodeActionListener(); 
		deleteSubGraph.addActionListener(closeDialog);
		delete.addActionListener(closeDialog);
		cancel.addActionListener(closeDialog);
		d.setVisible(true);
		String result = closeDialog.jDialogResult;
		if(result == null || result.equals(cancelText))
			return 3;
		if(result.equals(deleteText))
			return 2;
		if(result.equals(deleteSubGraphText))
			return 1;
		return 3;
    }
    
    public List<ProblemModelEvent> deleteSingleNode(ProblemNode deleteNode){
    	
    	ProblemModel modelDeletedFrom = deleteNode.getProblemModel();
    	List<ProblemModelEvent> allEvents= new ArrayList();
    	allEvents.add(new NodeDeletedEvent(deleteNode, new ArrayList()));
    	List<ProblemEdge> edgesToDelete = deleteNode.getIncomingEdges();
    	int i;
    	for(i=0; i < edgesToDelete.size(); i++){
    		ProblemEdge curr =edgesToDelete.get(i);
    		if(curr.isPreferredEdge()){
    			try{
    				getProblemModel().updatePreferredPath(curr.getSource(), curr, true);
    			}catch(Exception e){}
    		}
    		this.deleteSingleEdge(curr, false);
    		allEvents.add(new EdgeDeletedEvent(curr));
    	}
    	edgesToDelete = deleteNode.getOutgoingEdges();
    	for(i=0; i < edgesToDelete.size(); i++){
    		ProblemEdge curr =edgesToDelete.get(i);
    		this.deleteSingleEdge(curr, false);
    		allEvents.add(new EdgeDeletedEvent(curr));
    	}
    	
    	getProblemModel().removeNode(deleteNode, false);
    	return allEvents;
    }
    



    /**
     * rebuild eseGraph by the order of the depth
     * 
     * @param graph
     *            old eseGraph Vector
     */

    private Vector rebuildBehaviorRecorderGraph(Vector graph) {
        Vector newGraph = new Vector();
        int numOfElements;
        Vector cloneGraph;
        Vector parentChildList;
        Integer depth;

        for (int i = 0; graph.size() > 0; i++) {
            cloneGraph = (Vector) graph.clone();
            numOfElements = cloneGraph.size();

            for (int j = 0; j < numOfElements; j++) {
                parentChildList = (Vector) cloneGraph.elementAt(j);
                depth = (Integer) parentChildList.elementAt(parentChildList
                        .size() - 1);
                if (i == depth.intValue()) {
                    parentChildList.removeElement(depth);
                    graph.removeElement(parentChildList);
                    newGraph.addElement(parentChildList);
                }
            }
        }

        return newGraph;
    }

    public void sendLoadBRDFileSuccessMsg(String BRDFilePath) {
        MessageObject newMessage = MessageObject.create(MsgType.LOAD_BRD_FILE_SUCCESS, "SendNoteProperty");
        newMessage.setProperty("BRDFilePath", BRDFilePath);
        newMessage.setTransactionId(getSemanticEventId());

        handleMessageUTP(newMessage);

        return;
    }

    // ////////////////////////////////////////////////////////////////////////////
    /**
     * update the Skillometer with rules in the state of targetEdge
     */
    // ////////////////////////////////////////////////////////////////////////////
    public void updateSkillometer(ProblemEdge targetEdge) {
        EdgeData myEdge = targetEdge.getEdgeData();
        boolean correctActionFlag = false;

        if (myEdge.getActionType().equalsIgnoreCase(EdgeData.CORRECT_ACTION))
            correctActionFlag = true;

        // Only update each edge once
        if (myEdge.getUpdatedInSkillometer())
            return;

        myEdge.setUpdatedInSkillometer(true);

        Vector tempRuleVector = myEdge.getRuleLabels();
        String ruleName = "";

        for (int i = 0; i < tempRuleVector.size(); i++) {
            RuleLabel tempRulelabel = (RuleLabel) tempRuleVector.elementAt(i);

            // parsing ruleName
            String tempString = tempRulelabel.getRuleName();
            int index = tempString.indexOf(" ");

            if (index > 0) {
                if (!(tempString.substring(0, index)
                        .equalsIgnoreCase("unnamed") && tempString.substring(
                        index + 1).equalsIgnoreCase("rule"))) {
                    ruleName = tempString.substring(0, index);

                    // frey
                    // CMU's StudentFileManager simply calls the Skillometers
                    // update skill so since there is not an update skill in
                    // the student file manager at the moment, lets just call
                    // the skillometer directly
                    skillometerManager.updateSkill(ruleName, correctActionFlag);
                    // frey
                    // utp.logSkillUpdate(myEdge.actionLabel.selection,
                    // ruleName, loadingCurriculum.getSkillValue(ruleName));

                }
            }

        }

        return;
    }
    
    public  List<ProblemModelEvent> mergeStates2(ProblemNode source, ProblemNode target,
    		boolean fireEvent, boolean confirmWithAuthor, boolean targetHasPreferredEdge) {
    	
    	if(confirmWithAuthor){
	        String title1 = source.getName();
	        String title2 = target.getName();
	        int result = JOptionPane.showConfirmDialog(getServer().getActiveWindow(),
	                "<html>Do you want to merge the states<br>" + "\"<b>" + title1
	                        + "</b>\" and \"<b>" + title2 + "</b>\"?</html>",
	                "Merge States", JOptionPane.WARNING_MESSAGE);
	
	        if (result != JOptionPane.YES_OPTION)
	            return null;
    	}
        boolean targetIsLeaf = target.isLeaf();
        Enumeration<ProblemEdge> temp = getProblemModel().getProblemGraph().getConnectingEdges(source);
        List<ProblemEdge> sourceConnectingEdges = new ArrayList<ProblemEdge>();
        int i;
        for(i = 0; temp.hasMoreElements(); i++){
        	sourceConnectingEdges.add(temp.nextElement());
        }
        List<ProblemModelEvent> subEvents = new ArrayList<ProblemModelEvent>();
        //this will also delete all the edges, so we don't have to explicitly delete
        //the old nodes edges in the problem model.
		getProblemModel().removeNode(source, false);
		subEvents.add(new NodeDeletedEvent(source));
		ProblemEdge tempEdge;
		EdgeData tempEdgeData;
		EdgeDeletedEvent deleteEvent;
		EdgeCreatedEvent createEvent;
		EdgeRewiredEvent rewireEvent;
		LinkGroup groupToReAddTo;
		for(i=0; i < sourceConnectingEdges.size(); i++){
			tempEdge = sourceConnectingEdges.get(i);
			groupToReAddTo = getExampleTracerGraph().getSmallestContainingGroup(getExampleTracerGraph().getLink(tempEdge));
			deleteEvent = new EdgeDeletedEvent(tempEdge);
			deleteEvent.setEdgeBeingRewired(true);
			//subEvents.add(deleteEvent);
			tempEdgeData = tempEdge.getEdgeData();
			ProblemEdge newEdge;
			if(tempEdge.getSource()==source){
				newEdge = getProblemModel().getProblemGraph().addEdge(target, tempEdge.getDest(), tempEdgeData);
				if(targetHasPreferredEdge)
					newEdge.getEdgeData().setPreferredEdge(false);
			}else{
				newEdge = getProblemModel().getProblemGraph().addEdge(tempEdge.getSource(), target, tempEdgeData);
			}
	        tempEdgeData.getActionLabel().update();
	        newEdge.addEdgeLabels();
	        createEvent = new EdgeCreatedEvent(this,newEdge);
	        createEvent.setEdgeBeingRewired(true);
	        createEvent.setGroupToAddTo(groupToReAddTo);
	        rewireEvent = new EdgeRewiredEvent(this, deleteEvent, createEvent);
	        subEvents.add(rewireEvent);
		}
		if(fireEvent){
			NodeDeletedEvent fireMe;
			if(subEvents.size()>1){
				fireMe = new NodeDeletedEvent(((NodeDeletedEvent)subEvents.get(0)).getNode(), 
												new ArrayList(subEvents.subList(1, subEvents.size())));
			}else
				fireMe = new NodeDeletedEvent(((NodeDeletedEvent)subEvents.get(0)).getNode());
			getProblemModel().fireProblemModelEvent(fireMe);
		}
		
		//Only add undo checkpoint if merge operation was via an author-prompt
		if (confirmWithAuthor)
		{
			//Undo checkpoint for Merging Node ID: 1337
	    	ActionEvent ae = new ActionEvent(this, 0, "Merge States");
	    	getUndoPacket().getCheckpointAction().actionPerformed(ae);
		}
		
		return subEvents;
    }

    /**
     * Set the fields of the main window status bar as follows: <ul>
     * <li>whether the current graph is ordered or unordered;</li>
     * <li>the current state name or "No current state";</li>
     * <li>the current file name or {@link ProblemModel#getProblemName()} or "no graph";</li>
     * <li>the tutor type label;</li>
     * <li>the current number of interpretations.</li>
     * </ul> 
     * @param toolTipText text for the file label tool tip;
     *        if null, uses {@link ProblemModel#getProblemFullName()}
     */
    public void updateStatusPanel(String toolTipText) {
    	if(Utils.isRuntime())
    		return;
    	CtatFrame cf = (getCtatFrameController() == null ? null : getCtatFrameController().getDockedFrame());
    	if (cf == null) return;
    	if (toolTipText != null)                             // if caller passed a tool tip, use it
			cf.setProblemStatusToolTip(toolTipText);
    	//if (getProblemModel() == null)
    	//	return;
    	GroupModel gm = (getExampleTracerGraph() == null ? null : getExampleTracerGraph().getGroupModel());
    	ProblemNode currentState = getCurrentNode();
    	
    	ProblemModel pm = getProblemModel();
    	

       	String interpStatusLabel = null;
       	if (getProblemModel().getStartNode() == null)
       		interpStatusLabel = "No interpretations";
       	else if (getExampleTracer() != null) {
       		int ic = getExampleTracer().getInterpretationCount();
       		interpStatusLabel = (ic +" interpretation" + ((ic != 1) ? "s" : ""));
       	}
    	getServer().updateStatusPanel(toolTipText, gm, currentState, pm, interpStatusLabel);
    	
//        fireCtatModeEvent(CtatModeEvent.REPAINT);   // only if really needed
    }
    
    /* Function that should be called whenever an edge needs to be rewired.
     * This function does not update the preferred status, or the node
     * to set as current state during event processing.
     */
	public EdgeRewiredEvent rewireSource(ProblemEdge problemEdge, ProblemNode newSourceNode){
		deleteSingleEdge(problemEdge, false);//don't throw event
    	ProblemEdge replacementEdge = getProblemModel().getProblemGraph().addEdge(newSourceNode, problemEdge.getDest(), problemEdge.getEdgeData());
        replacementEdge.addEdgeLabels();
        replacementEdge.getEdgeData().getActionLabel().update();
    	//new EdgeDeletedEvent(problemEdge)
        EdgeCreatedEvent edgeCreatedEvent = new EdgeCreatedEvent(this, replacementEdge);
        edgeCreatedEvent.setEdgeBeingRewired(true);
        EdgeDeletedEvent edgeDeletedEvent = new EdgeDeletedEvent(problemEdge);
    	edgeDeletedEvent.setEdgeBeingRewired(true);
    	EdgeRewiredEvent returnMe = new EdgeRewiredEvent(this, edgeDeletedEvent, edgeCreatedEvent);
    	//edgeDeletedEvent.setTryToSetCurrentStateTo(tryToSetCurrentStateTo)
		//getProblemModel().fireProblemModelEvent(edgeDeletedEvent);
		return returnMe;
	}    
	
/*	public List<ProblemModelEvent> rewireDest(ProblemEdge problemEdge, ProblemNode newDestNode){
	
    	List<ProblemModelEvent> subEvents = new ArrayList<ProblemModelEvent>();
    	deleteSingleEdge(problemEdge, false);//don't fire event
		ProblemEdge replacementEdge = getProblemModel().getProblemGraph().addEdge(
						problemEdge.getSource(), newDestNode, problemEdge.getEdgeData());
		replacementEdge.addEdgeLabels();
        replacementEdge.getEdgeData().getActionLabel().update();
        
        EdgeCreatedEvent edgeCreatedEvent = new EdgeCreatedEvent(this, replacementEdge);
        edgeCreatedEvent.setEdgeBeingRewired(true);
    	subEvents.add(edgeCreatedEvent);
    	
		//subEvents.add(new EdgeCreatedEvent(this, replacementEdge));
    	EdgeDeletedEvent edgeDeletedEvent = new EdgeDeletedEvent(problemEdge, null);
    	edgeDeletedEvent.setEdgeBeingRewired(true);
    	subEvents.add(edgeDeletedEvent);
    	//if(getCurrentNode()== problemEdge.getDest()){
		//	edgeDeletedEvent.setTryToSetCurrentStateTo(replacementEdge.getSource().getUniqueID());
		//}
    	return subEvents;
	}*/
	
    //This method should ONLY be used after:
    //ProblemModel.TestNewSourceNodeForLink is called with the given edge/node
    //and returns a NULL string, indicating it is safe to make the change (rather then an error msg).
    public void changeEdgeSourceNode(ProblemEdge problemEdge, ProblemNode newSourceNode){
    	//after rewiring attempt to return to current state?
		//take care of preferredEdge
    	List children =	problemEdge.getSource().getOutgoingEdges();
    	if(problemEdge.isPreferredEdge()){
    		try{
    			getProblemModel().updatePreferredPath(problemEdge.getSource(), problemEdge, true);
    		}catch (ProblemModelException e) {
                NodeView outVertex = problemEdge.getSource().getNodeView();
                // display warning message
                String message[] = { "You don't have preferred path defined",
                        "from state: " + outVertex.getText().trim(), "", };
                JOptionPane.showMessageDialog(getServer().getActiveWindow(), message, "Warning",JOptionPane.WARNING_MESSAGE);
                e.printStackTrace();
            }
    	}
    	if(newSourceNode.getOutDegree()==0){
    		if(!problemEdge.isBuggy())
    			problemEdge.getEdgeData().setPreferredEdge(true);
    	}else{
    		problemEdge.getEdgeData().setPreferredEdge(false);
    	}
    	EdgeRewiredEvent fireMe = rewireSource(problemEdge, newSourceNode);
    	//List<ProblemModelEvent> subEvents = rewireSource(problemEdge, newSourceNode);
    	//EdgeDeletedEvent fireMe = (EdgeDeletedEvent) subEvents.get(1);
    	//fireMe.setSubevents(new ArrayList(subEvents.subList(0, 1)));
    	getProblemModel().fireProblemModelEvent(fireMe);
    	
    	//Undo checkpoint for Rewiring ID: 1337
    	ActionEvent ae = new ActionEvent(this, 0, "Change Link Origin");
    	getUndoPacket().getCheckpointAction().actionPerformed(ae);
		
    	/*deleteSingleEdge(problemEdge, false);//don't throw event
 		ProblemEdge replacementEdge = getProblemModel().getProblemGraph().addEdge(newSourceNode, problemEdge.getDest(), problemEdge.getEdgeData());
        replacementEdge.addEdgeLabels();
        replacementEdge.getEdgeData().getActionLabel().update();
    	//new EdgeDeletedEvent(problemEdge)
        EdgeCreatedEvent edgeCreatedEvent = new EdgeCreatedEvent(this, replacementEdge);
        edgeCreatedEvent.setEdgeBeingRewired(true);
    	subEvents.add(edgeCreatedEvent);
    	EdgeDeletedEvent edgeDeletedEvent = new EdgeDeletedEvent(problemEdge, subEvents);
    	edgeDeletedEvent.setEdgeBeingRewired(true);
    	//edgeDeletedEvent.setTryToSetCurrentStateTo(tryToSetCurrentStateTo)
		//getProblemModel().fireProblemModelEvent(edgeDeletedEvent);*/
    }
    //This method should ONLY be used after:
    //ProblemModel.TestNewDestNodeForLink is called with the given edge/node
    //and returns a NULL string, indicating it is safe to make the change (rather then an error msg).
    public void changeEdgeDestNode(ProblemEdge problemEdge, ProblemNode newDestNode){
    	EdgeRewiredEvent throwMe;
    	//List<ProblemModelEvent> subEvents = new ArrayList<ProblemModelEvent>();
    	deleteSingleEdge(problemEdge, false);//don't fire event
		ProblemEdge replacementEdge = getProblemModel().getProblemGraph().addEdge(
						problemEdge.getSource(), newDestNode, problemEdge.getEdgeData());
		replacementEdge.addEdgeLabels();
        replacementEdge.getEdgeData().getActionLabel().update();
        //throwMe.get
        //Integer x = 5;
        EdgeCreatedEvent edgeCreatedEvent = new EdgeCreatedEvent(this, replacementEdge);
        edgeCreatedEvent.setEdgeBeingRewired(true);
    	//subEvents.add(edgeCreatedEvent);
    	
		//subEvents.add(new EdgeCreatedEvent(this, replacementEdge));
    	EdgeDeletedEvent edgeDeletedEvent = new EdgeDeletedEvent(problemEdge);
    	edgeDeletedEvent.setEdgeBeingRewired(true);
    	
    	
    	throwMe = new EdgeRewiredEvent(this, edgeDeletedEvent, edgeCreatedEvent);
    	if(getCurrentNode()== problemEdge.getDest()){
			throwMe.setTryToSetCurrentStateTo(replacementEdge.getSource().getUniqueID());
		}
    	getProblemModel().fireProblemModelEvent(throwMe);
    	
    	//Undo checkpoint for Rewiring Edge ID: 1337
    	ActionEvent ae = new ActionEvent(this, 0, "Change Link Destination");
		getUndoPacket().getCheckpointAction().actionPerformed(ae);
    }
    /**
     * Delete the given edge from the graph. First initializes example tracer
     * to clear any existing state. Fires {@link EdgeDeletedEvent}s.
     * @param problemEdge
     */
    public void deleteSingleEdge(ProblemEdge problemEdge, boolean fireEvent) {
        //EdgeData edgeData = problemEdge.getEdgeData();
        //trace.out ("delete edge: " + edgeData);
        getProblemModel().getProblemGraph().removeEdge(problemEdge);
        if(fireEvent)
        	getProblemModel().fireProblemModelEvent(new EdgeDeletedEvent(problemEdge));
        return;
    }
    
  
 

    // recursive call until atNode state is startNode state, then send Comm
    // MSG Go_To_WM_State to lisp to check chained production rules
    void checkProductionRulesChain(ProblemNode atNode, Vector selectionList,
            Vector actionList, Vector inputList, Vector authorIntentList,
            Vector uniqueIDList) {
        if (atNode == getProblemModel().getStartNode()) {
            Collections.reverse(selectionList);
            Collections.reverse(actionList);
            Collections.reverse(inputList);
            Collections.reverse(authorIntentList);
            Collections.reverse(uniqueIDList);

            sendGo_To_WM_State(selectionList, actionList, inputList,
                    authorIntentList, uniqueIDList);

            return;
        }
        ProblemEdge edgeTemp;
        ProblemNode parentTemp;

        Enumeration iter = getProblemModel().getProblemGraph().getConnectingEdges(
                atNode);
        while (iter.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iter.nextElement();
            parentTemp = edgeTemp.getNodes()[ProblemEdge.SOURCE];

            if (parentTemp != atNode) {
                EdgeData myCurrEdge = edgeTemp.getEdgeData();

                selectionList.addElement(myCurrEdge.getSelection());
                actionList.addElement(myCurrEdge.getAction());
                inputList.addElement(myCurrEdge.getInput());

                // if(myCurrEdge.actionLabel.buggyMsg.equals(""))
                authorIntentList.addElement(myCurrEdge.getActionType());

                uniqueIDList.addElement(new Integer(myCurrEdge.getUniqueID()));

                checkProductionRulesChain(parentTemp, selectionList,
                        actionList, inputList, authorIntentList, uniqueIDList);

                break;
            }
        }
        return;

    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void checkWithLispSingle(ProblemEdge thisEdge) {
        EdgeData myEdge = thisEdge.getEdgeData();

        Vector ruleNames = new Vector();
        RuleLabel tempLabel;
        for (int i = 0; i < myEdge.getRuleLabels().size(); i++) {
            tempLabel = (RuleLabel) myEdge.getRuleLabels().elementAt(i);
            ruleNames.addElement(tempLabel.getText());
        }
        if (ruleNames.size() < 1) // CTAT1202 Comm parse error if size 0
            ruleNames.add("dummy");
        // trace.out ( "call sendLISPCheckMsg()");
        tutorMessageHandler.sendLISPCheckMsg(myEdge.getSelection(), myEdge.getAction(), myEdge
                .getInput(), ruleNames, new Integer(myEdge.getUniqueID()), null);

        String edgeInfoString = "Edge from ";

        ProblemNode tempNode = thisEdge.getNodes()[ProblemEdge.SOURCE];
        edgeInfoString += tempNode.toString() + " to ";

        tempNode = thisEdge.getNodes()[ProblemEdge.DEST];
        edgeInfoString += tempNode.toString();

        Vector skills = thisEdge.getEdgeData().getSkills();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < skills.size(); i++)
            sb.append(skills.get(i) + "\n");
        getLogger().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
                BR_Controller.TEST_MODEL_1_STEP, edgeInfoString, 
                "Skill names: " + sb.toString(), "");
        if (trace.getDebugCode("mt")) trace.out("mt", "checkWithLispSingle("+thisEdge+") skills: "+sb);
    }

    // Tue Oct 25 11:05:05 2005:: Noboru

    // Made a wrapper for checkProductionRulesChainNew so that it can
    // return the pathEdges
    public void checkProductionRulesChainNew(ProblemNode targetNode) {
        checkProductionRulesChainNewOld(targetNode);
    }

    // Wrapper that now returns pathEdges
    public Vector checkProductionRulesChainNewOld(ProblemNode targetNode) {
    	return checkProductionRulesChainNewOld(targetNode, null);
    }

    /**
     * 
     * This is the original method except it now returns pathEdges
     */ 
    private Vector checkProductionRulesChainNewOld(ProblemNode targetNode, MessageObject returnMsg) {
        // first try prefeered path
        Vector pathEdges = new Vector();
        getProblemModel().setSearchPathFlag(true);
        boolean pathFound = findPreferredPath(getProblemModel().getStartNode(), targetNode, pathEdges);
       
        // success
        if (pathFound) {
            getProblemModel().setSearchPathFlag(false);
            Vector selectionList = new Vector();
            Vector actionList = new Vector();
            Vector inputList = new Vector();
            Vector authorIntentList = new Vector();
            Vector uniqueIDList = new Vector();
            buildSAIListsFromPath(pathEdges, selectionList, actionList,
            		inputList, authorIntentList, uniqueIDList);
        	
            sendGo_To_WM_State(selectionList, actionList, inputList, authorIntentList, uniqueIDList);
            
            
            
            //trace.removeDebugCode("missmt1");
        	return pathEdges;
        }
        pathEdges = new Vector();
        getProblemModel().setSearchPathFlag(true);

        findCorrectFireableBuggyPath(targetNode, pathEdges, EdgeData.CORRECT_ACTION);
        // success
        if (!getProblemModel().isSearchPathFlag()) {
            return pathEdges;
        }

        pathEdges = new Vector();
        getProblemModel().setSearchPathFlag(true);
        findCorrectFireableBuggyPath(targetNode, pathEdges, EdgeData.FIREABLE_BUGGY_ACTION);
        // success
        if (!getProblemModel().isSearchPathFlag()) {
            return pathEdges;
        }
        
        //Gustavo, 17Oct2006: if in SimStudent mode, do not pop up this dialog
        // 
        // Noboru : Oct 10, 2007 Move "if" statement from right above String message[]
        // This is done to avoid having [Cannot redefine deftemplate. MAIN::button] popup
        // 
        if (!getCtatModeModel().isSimStudentMode()){

            String message[] = { "You have chosen a buggy state.",
                    "In Tutor Mode the Behavior Recorder",
            "does not transit to the buggy states." };

            JOptionPane.showMessageDialog(getServer().getActiveWindow(), message,
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }

        pathEdges = new Vector();
        findPathForProductionRulesChecking(targetNode, pathEdges);
        return pathEdges;
    }

    /**
     * Try to find a path from one node to another using only preferred edges.
     * @param startNode starting node
     * @param endNode ending node
     * @param pathEdges to return list of edges in found path
     * @return true if found a path
     */
    public boolean findPreferredPath(ProblemNode startNode, ProblemNode endNode, Vector pathEdges) {

        return findPath(startNode, endNode, pathEdges, 1);
    }
    
    // Sun Sep  3 16:19:26 LDT 2006 :: Noboru 
    // Refactoring -- Necessary to find a path with edges that are not preferred
    
    /**
     * Search a sequence of ProblemEdges that connect from startNode to endNode.
     * 
     * @param startNode
     * @param endNode
     * @param pathEdges When the search terminates, this vector contains the
     *        sequence of {@link ProblemEdge}s in reverse order 
     * @param preferredOnly if > 0, then the path returned must have only preferred links;
     *        if 0, then use the first preferred link at each step, but return a path even
     *        with some unpreferred link;
     *        if < 0, then use the first links found
     * @return true if found a path that satisfies
     */
    public boolean findPath(ProblemNode startNode, ProblemNode endNode, Vector /* ProblemEdge*/ pathEdges, 
            int preferred) {
        
        // trace.out("miss", "findPath(" + startNode + ", " + endNode + ", " + preferred + ")");
        
        boolean pathFound = false;
        
        ProblemEdge edgeTemp;
        ProblemEdge lastInEdge = null;
        EdgeData myEdgeTemp;

        Enumeration iter = getProblemModel().getProblemGraph().getConnectingEdges(startNode);
        
        while (iter.hasMoreElements()) {

            edgeTemp = (ProblemEdge) iter.nextElement();
            ProblemNode sourceNode = edgeTemp.getNodes()[ProblemEdge.SOURCE];
            ProblemNode destNode = edgeTemp.getNodes()[ProblemEdge.DEST];
            
            if (destNode != startNode) {
            	
                lastInEdge = edgeTemp;
                myEdgeTemp = edgeTemp.getEdgeData();
                if (trace.getDebugCode("mt")) trace.out("mt", "findPath[" + sourceNode + ", " + destNode + "] isPreferredEdge " + myEdgeTemp.isPreferredEdge());
                
                if (preferred < 0 || myEdgeTemp.isPreferredEdge()) {
                    ProblemNode childNodeTemp = destNode;
                    pathEdges.addElement(edgeTemp);
                    if (childNodeTemp == endNode) {
                        pathFound = true;
                        break;
                    } else {
                        if (findPath(childNodeTemp, endNode, pathEdges, preferred)) {
                            pathFound = true;
                            break;
                        }
                    }
                }
            }
        }
        
        if (!pathFound && lastInEdge != null && preferred <= 0) {
            ProblemNode childNodeTemp = lastInEdge.getNodes()[ProblemEdge.DEST];
            pathEdges.addElement(lastInEdge);
            if (childNodeTemp == endNode) {
                pathFound = true;
            } else {
                if (findPath(childNodeTemp, endNode, pathEdges, preferred)) {
                    pathFound = true;
                }
            }            	
        }
        return pathFound;
    }

    /**
     * Build parallel lists of the selection, action and input attributes
     * from the edges in the given list, which is presumed to be a path
     * from one state to another. Also builds the authorIntentList and
     * uniqueIDList needed by
     * {@link #sendGo_To_WM_State(Vector, Vector, Vector, Vector, Vector)}.
     * @param pathEdges list of edges
     * @param selectionList to return selection from each edge
     * @param actionList to return action from each edge
     * @param inputList to return input from each edge
     * @param authorIntentList to return actionType from each edge
     * @param uniqueIDList  to return uniqueID from each edge
     */
    private void buildSAIListsFromPath(final Vector pathEdges,
            Vector selectionList, Vector actionList, Vector inputList,
            Vector authorIntentList, Vector uniqueIDList) {
        ProblemEdge edgeTemp;
        EdgeData myEdgeTemp;

        int sizeOfpathEdges = pathEdges.size();
        for (int i = 0; i < sizeOfpathEdges; i++) {
            edgeTemp = (ProblemEdge) pathEdges.elementAt(i);
            myEdgeTemp = edgeTemp.getEdgeData();

            selectionList.addElement(myEdgeTemp.getSelection());
            actionList.addElement(myEdgeTemp.getAction());
            inputList.addElement(myEdgeTemp.getInput());
            authorIntentList.addElement(myEdgeTemp
                    .getActionType());
            uniqueIDList.addElement(new Integer(myEdgeTemp.getUniqueID()));
        }
    }

    public void findCorrectFireableBuggyPath(ProblemNode atNode, Vector pathEdges, String edgeAuthorIntentType) {

    	if (!getProblemModel().isSearchPathFlag())
    	{
        	return;
    	}

        ProblemEdge edgeTemp;
        EdgeData myEdgeTemp;

        Enumeration iter = getProblemModel().getProblemGraph().getConnectingEdges(atNode);
        while (iter.hasMoreElements() && getProblemModel().isSearchPathFlag()) {
            
            edgeTemp = (ProblemEdge) iter.nextElement();
            
        	if (edgeTemp.getNodes()[ProblemEdge.SOURCE] != atNode) {
                
                myEdgeTemp = edgeTemp.getEdgeData();
                
                if (myEdgeTemp.getActionType().equalsIgnoreCase(EdgeData.CORRECT_ACTION)
                        || myEdgeTemp.getActionType().equalsIgnoreCase(edgeAuthorIntentType)) {
                	
                	if (edgeTemp.getNodes()[ProblemEdge.SOURCE] == getProblemModel().getStartNode()) {
                        Vector selectionList = new Vector();
                        Vector actionList = new Vector();
                        Vector inputList = new Vector();
                        Vector authorIntentList = new Vector();
                        Vector uniqueIDList = new Vector();

                        pathEdges.addElement(edgeTemp);
                        int sizeOfpathEdges = pathEdges.size();
                        for (int i = sizeOfpathEdges - 1; i >= 0; i--) {
                            edgeTemp = (ProblemEdge) pathEdges.elementAt(i);
                            myEdgeTemp = edgeTemp.getEdgeData();
                            // trace.out( "here4");
                            selectionList.addElement(myEdgeTemp.getSelection());
                            actionList.addElement(myEdgeTemp.getAction());
                            inputList.addElement(myEdgeTemp.getInput());
                            authorIntentList.addElement(myEdgeTemp.getActionType());
                            uniqueIDList.addElement(new Integer(myEdgeTemp.getUniqueID()));
                            
                        }

                        // trace.out ( "Use " + edgeAuthorIntentType + " path to
                        // sendGo_To_WM_State.");
              
                        sendGo_To_WM_State(selectionList, actionList,inputList, authorIntentList, uniqueIDList);
                        getProblemModel().setSearchPathFlag(false);
                        
                        this.getGoToWMStateResponse(atNode.getName()); //keiser 12/29
                        return;
                    }
                    Vector pathEdgesC = (Vector) pathEdges.clone();
                    pathEdgesC.addElement(edgeTemp);
                    
                    findCorrectFireableBuggyPath(edgeTemp.getNodes()[ProblemEdge.SOURCE],
                            pathEdgesC, edgeAuthorIntentType);
                    
                }
            }
        }
 
        return;
    }

    
    /**
     * Tell whether the author tools are visible.
     * @return true if {@link #getActiveWindow()}.isVisible()
     */
    public boolean getAuthorToolsVisible() {
		AbstractCtatWindow aw = getServer().getActiveWindow();
        return aw != null && aw.isVisible();
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void closeApplication(final boolean saveBrdFile) {
    	getServer().closeApplication(this, saveBrdFile);
    }



    // ////////////////////////////////////////////////////
    /**
     * Called at startup to set fields according to saved preferences.
     */
    // ////////////////////////////////////////////////////
    public void loadPreferencesFromModel() {
        PreferencesModel model = getTSLauncherServer().getPreferencesModel();
        setPreference(LOCK_WIDGETS, model.getValue(LOCK_WIDGETS));
        setPreference(CASE_SENSITIVE, model.getValue(CASE_SENSITIVE));
        setPreference(GROUP_DRAG, model.getValue(GROUP_DRAG));
        setPreference(SHOW_TT_SAVE, model.getValue(SHOW_TT_SAVE));
        setPreference(SET_PREF_PATH, model.getValue(SET_PREF_PATH));
        setPreference(FORCE_SCAFFOLDING, model.getValue(FORCE_SCAFFOLDING));
        setPreference(MACHINE_LEARNING, model.getValue(MACHINE_LEARNING));
        setPreference(DIALOGUE_SYSTEM, model.getValue(DIALOGUE_SYSTEM));
        setPreference(OLI_LOGGING_URL, model.getValue(OLI_LOGGING_URL));
        setPreference(USE_OLI_LOGGING, model.getValue(USE_OLI_LOGGING));
        setPreference(DISK_LOGGING_DIR, model.getValue(DISK_LOGGING_DIR));
        setPreference(USE_DISK_LOGGING, model.getValue(USE_DISK_LOGGING));
        setPreference(TRAVERSAL_COUNT, model.getValue(TRAVERSAL_COUNT));
        setPreference(PROJECTS_DIR, model.getValue(PROJECTS_DIR));
        setPreference(USE_LISP, model.getValue(USE_LISP));
        setPreference(COMMUTATIVITY, model.getValue(COMMUTATIVITY));
        setPreference(SUPPRESS_STUDENT_FEEDBACK, model.getValue(SUPPRESS_STUDENT_FEEDBACK));
        setPreference(HINT_POLICY, model.getValue(HINT_POLICY));
        //System.out.println(ALLOW_TOOL+" "+model.setValue());
        setPreference(ALLOW_TOOL_REPORTED_ACTIONS, model.getValue(ALLOW_TOOL_REPORTED_ACTIONS));
        setPreference(MAX_STUDENT,new Integer(1));
        // setPreference(MAX_STUDENT, model.getValue(MAX_STUDENT));
        setPreference(LoggingSupport.ENABLE_AUTHOR_LOGGING, model.getValue(LoggingSupport.ENABLE_AUTHOR_LOGGING));
    }

    // ////////////////////////////////////////////////////
    /**
     * Set a single Preference item.
     * 
     * @param name
     *            of item to set: one of {@link #LOCK_WIDGETS}, etc.
     * @param newValue
     *            new value for item
     * @return true if this a preference value that we may want to save
     */
    // ////////////////////////////////////////////////////
    private boolean setPreference(String name, Object newValue) {

  		if (trace.getDebugCode("br"))
  			trace.outNT("br", "BR_C.setPreference("+name+","+newValue+")");
        if (name == null || newValue == null)
            return false;
        if (name.equalsIgnoreCase(LOCK_WIDGETS)) {
        	trace.out("lock widgets preference changed");
        } else if (name.equalsIgnoreCase(SUPPRESS_STUDENT_FEEDBACK)) {
       		trace.out("student feedback preference changed");
       	} else if (name.equalsIgnoreCase(HINT_POLICY)) {
       		trace.out("hint policy preference changed");
      	} else if (name.equalsIgnoreCase(COMMUTATIVITY)) {
      		if (trace.getDebugCode("et")) {
      			ExampleTracerGraph etg =
      					(getProblemModel() == null ? null : getExampleTracerGraph());
      			trace.out("et", "BR_C.setPreference("+name+","+newValue+"): "+
      					"pm "+getProblemModel()+", getETGraph() "+etg+
      					", getGroupModel() "+(etg == null ? null : etg.getGroupModel()));
      		}
      		getExampleTracerGraph().getGroupModel().setGroupOrdered(
      				getExampleTracerGraph().getGroupModel().getTopLevelGroup(),
      				!((Boolean) newValue));
      		updateStatusPanel(null);
        } else if (name.equalsIgnoreCase(CASE_SENSITIVE)) {
            //applyPreferencesCaseInsensitive();
        	trace.out("case sensitivity preference changed");
        } else if (name.equalsIgnoreCase(ALWAYS_LINK_STATES)) {
        	trace.out("always link states preference changed");
        } else if (name.equalsIgnoreCase(GROUP_DRAG)) {
            Boolean nv = (Boolean) newValue;
            setUseGroupDragging((nv == null || nv.booleanValue()));
        } else if (name.equalsIgnoreCase(SHOW_TT_SAVE)) {
            Boolean nv = (Boolean) newValue;
            setShowTTSave((nv == null || nv.booleanValue()));
        } else if (name.equalsIgnoreCase(SET_PREF_PATH)) {
            Boolean nv = (Boolean) newValue;
            setChangePreferredPath((nv == null || nv.booleanValue()));
        } else if (name.equalsIgnoreCase(TRAVERSAL_COUNT)) {
            Boolean nv = (Boolean) newValue;
            setTraversalCountEnabled(nv != null && nv.booleanValue());
        } else if (name.equalsIgnoreCase(MAX_STUDENT)) {
        	//int nv = ((Integer) newValue).intValue();
        	int nv= 1;
        	if (trace.getDebugCode("log")) trace.out("log", "Max # of Students is now: " +nv);
            // EJ: actions to be added soon
        }else if (name.equalsIgnoreCase(ALLOW_TOOL_REPORTED_ACTIONS)) {
        	//System.out.println("new value "+ newValue);
//        	Boolean nv = (Boolean) newValue;
        	if (trace.getDebugCode("log")) trace.out("log", "Tool actions are now allowed");
            // EJ: actions to be added soon
        } 
        else if (name.equalsIgnoreCase(PROJECTS_DIR)) {
            // no action needed here, just return true to save
        } 
        else if (name.equalsIgnoreCase(DialogueSystemInfo.INVOKE_BROWSER_ON_EXTERNAL_URL)) {
            // no action needed here, just return true to save
        } else if (name.equalsIgnoreCase(DialogueSystemInfo.EXTERNAL_URL_FOR_EDGE_TRAVERSAL)) {
            // no action needed here, just return true to save
        } else if (name.equalsIgnoreCase(PROBLEM_DIRECTORY)) {
        	// CTAT1567: when set Problem Dir, make it the default BRD dir
        	getTSLauncherServer().getPreferencesModel().setStringValue(SaveFileDialog.BRD_OTHER_DIR_KEY,
        			(String) newValue);
        } else if (name.equalsIgnoreCase(LoggingSupport.ENABLE_AUTHOR_LOGGING)) {
            // no action needed here, just return true to save
        } else
            return false; // don't save this preference change
        return true; // maybe save this preference change
    }

    
    public void setShowTTSave(boolean showTTSave) {
    	this.showTTSave = showTTSave;
    	getTSLauncherServer().getPreferencesModel().setBooleanValue(SHOW_TT_SAVE,
     			showTTSave );
    	 
    	if (!inAppletMode())
    		getTSLauncherServer().getPreferencesModel().saveToDisk();
	}
    public boolean isShowTTSave () {
    	return showTTSave;
    }
    
	public void setStudentInterface(StudentInterfaceWrapper tw) {
    	if (tw != studentInterface)
    		this.resetRuleEngineForNewProblem();
        super.setStudentInterface(tw);
    }


    /**
     * Tell whether a student interface (with CommWidgets, e.g.) has been
     * loaded.
     * 
     * @return true if currentInterface is non-null
     */
    public boolean getInterfaceLoaded() {
        if (trace.getDebugCode("mps")) trace.out("mps", "current interface = " + studentInterface);
        return (studentInterface != null);
    }
    
    public ProblemModel getProblemModel() { 
    	//return this.problemModel;
    	return this.problemModelManager.getMainModel();
    }
    
    /*
    public ProblemModel getClipboardModel() {
    	return this.problemModelManager.getClipboardModel();
    }
    */
    
    public ProblemModelManager getProblemModelManager() {
    	return this.problemModelManager;
    }

    public SolutionState getSolutionState() {
        return solutionState;
    }

	/**
	 * @return {@link #problemStateReader}
	 */
    public ProblemStateReader getProblemStateReader() {
    	return this.problemStateReader;
    }
    
	/**
	 * @return {@link #problemStateWriter}
	 */
    public ProblemStateWriter getProblemStateWriter() {
    	return this.problemStateWriter;
    }


    /**
     * @return Returns the studentFileManager.
     */
    public CtatLMSClient getCTAT_LMS() {
        return getLauncher().getCTATLauncher().getCTAT_LMS();
    }

    /**
     * @param originalEdgeFont
     *            The originalEdgeFont to set.
     */
    public void setOriginalEdgeFont(Font originalEdgeFont) {
        this.originalEdgeFont = originalEdgeFont;
    }

    /**
     * @return Returns the originalEdgeFont.
     */
    public Font getOriginalEdgeFont() {
        return originalEdgeFont;
    }

    /**
     * @param allowCurrentStateChange
     *            The allowCurrentStateChange to set.
     */
    public void setAllowCurrentStateChange(boolean allowCurrentStateChange) {
        this.allowCurrentStateChange = allowCurrentStateChange;
    }

    /**
     * @return Returns the allowCurrentStateChange.
     */
    public boolean isAllowCurrentStateChange() {
        return allowCurrentStateChange;
    }

    public boolean getTraversalCountEnabled() {
        return traversalCountEnabled;
    }

    public void setTraversalCountEnabled(boolean enabled) {
        traversalCountEnabled = enabled;
    }

    /**
     * @param useGroupDragging
     *            The useGroupDragging to set.
     */
    public void setUseGroupDragging(boolean useGroupDragging) {
        this.useGroupDragging = useGroupDragging;
    }

    /**
     * @return Returns the useGroupDragging.
     */
    public boolean isUseGroupDragging() {
        return useGroupDragging;
    }

    /**
     * @param changePreferredPath
     *            The changePreferredPath to set.
     */
    public void setChangePreferredPath(boolean changePreferredPath) {
        this.changePreferredPath = changePreferredPath;
    }

    /**
     * @return Returns the changePreferredPath.
     */
    public boolean isChangePreferredPath() {
        return changePreferredPath;
    }

    /**
     * @param actionLabelsFlag
     *            The actionLabelsFlag to set.
     */
    public void setShowActionLabels(boolean actionLabelsFlag) {
    	this.actionLabelsFlag = actionLabelsFlag;
    }

    /**
     * @return Returns the actionLabelsFlag.
     */
    public boolean getShowActionLabels() {
        return this.actionLabelsFlag;
    }

    /**
     * @param preCheckLISPLabelsFlag
     *            The preCheckLISPLabelsFlag to set.
     */
    public void setPreCheckLISPLabelsFlag(boolean preCheckLISPLabelsFlag) {
    	this.preCheckLISPLabelsFlag = preCheckLISPLabelsFlag;
    }

    /**
     * @return Returns the preCheckLISPLabelsFlag.
     */
    public boolean isPreCheckLISPLabelsFlag() {
    	return this.preCheckLISPLabelsFlag;
    }

    /**
     * @param ruleLabelsFlag
     *            The ruleLabelsFlag to set.
     */
    public void setShowRuleLabels(boolean ruleLabelsFlag) {
    	this.ruleLabelsFlag = ruleLabelsFlag;
    }

    public void setShowCallbackFn(boolean flag) {
    	this.showCallbackFnFlag = flag;
    	//fireCtatModeEvent(CtatModeEvent.REPAINT);
    }

    public boolean getShowCallbackFn() {
    	return this.showCallbackFnFlag;
    }
    
    /**
     * @return Returns the ruleLabelsFlag.
     */
    public boolean getShowRuleLabels() {
    	return this.ruleLabelsFlag;
    }

    /**
     * @param preferredPathOnlyFlag
     *            The preferredPathOnlyFlag to set.
     */
    public void setPreferredPathOnlyFlag(boolean preferredPathOnlyFlag) {
    	this.preferredPathOnlyFlag = preferredPathOnlyFlag;
    }

    /**
     * @return Returns the preferredPathOnlyFlag.
     */
    public boolean isPreferredPathOnlyFlag() {
    	return this.preferredPathOnlyFlag;
    }

    /**
     * @param firstCheckAllStatesFlag
     *            The firstCheckAllStatesFlag to set.
     */
    public void setFirstCheckAllStatesFlag(boolean firstCheckAllStatesFlag) {
        this.firstCheckAllStatesFlag = firstCheckAllStatesFlag;
    }

    /**
     * @return Returns the firstCheckAllStatesFlag.
     */
    public boolean isFirstCheckAllStatesFlag() {
        return firstCheckAllStatesFlag;
    }

    /**
     * @param sendESEGraphFlag
     *            The sendESEGraphFlag to set.
     */
    public void setSendESEGraphFlag(boolean sendESEGraphFlag) {
        this.sendESEGraphFlag = sendESEGraphFlag;
    }

    /**
     * @return Returns the sendESEGraphFlag.
     */
    public boolean isSendESEGraphFlag() {
        return sendESEGraphFlag;
    }

    /**
     * @param interfaceActions_NoneState_Tutor
     *            The interfaceActions_NoneState_Tutor to set.
     */
    public void setInterfaceActions_NoneState_Tutor(
            Hashtable interfaceActions_NoneState_Tutor) {
        this.interfaceActions_NoneState_Tutor = interfaceActions_NoneState_Tutor;
    }

    /**
     * @return Returns the interfaceActions_NoneState_Tutor.
     */
    public Hashtable getInterfaceActions_NoneState_Tutor() {
        return interfaceActions_NoneState_Tutor;
    }

    /**
     * @param index_interfaceActions_NoneState_Tutor
     *            The index_interfaceActions_NoneState_Tutor to set.
     */
    public void setIndex_interfaceActions_NoneState_Tutor(
            int index_interfaceActions_NoneState_Tutor) {
        this.index_interfaceActions_NoneState_Tutor = index_interfaceActions_NoneState_Tutor;
    }

    /**
     * @return Returns the index_interfaceActions_NoneState_Tutor.
     */
    public int getIndex_interfaceActions_NoneState_Tutor() {
        return index_interfaceActions_NoneState_Tutor;
    }

    // //////////////////////////////////////////////////////////////////////////////
    //
    // ////////////////////////////////////////////////////////////////////////////////
    public void initializeInterfaceActions_NoneState_Tutor() {
        setInterfaceActions_NoneState_Tutor(new Hashtable());
        setIndex_interfaceActions_NoneState_Tutor(-3);
    }

//    // Fri Jul  7 00:20:40 2006 :: Noboru
//    // Some initialization procedure is only necessary for SimSt working under
//    // an interactive mode (as opposed to a batch mode)
//    public void initializeSimStInteractive() {
//        trace.out("gusmiss", "entered initializeSimStInteractive()");
//
//    	if (getMissController() != null) {
//    		getMissController().dealWithExistingPrFile();
//    	}
//    }
    public boolean isSimStudentMode(){
    	if(getCtatModeModel()!=null)
    		return getCtatModeModel().isSimStudentMode();
    	else
    		return false;
    }
    
    /**
     * Create a {@link MissControllerExternal} instance if there's not one already.
     */
    public void initializeSimSt() {
    	if(!Utils.isRuntime())
    		getLauncher().getCTATLauncher().initializeSimSt();
    }

    public MissControllerExternal getMissController() {
	/*
	if (this.missController == null) {
	    initializeSimSt();
	}
	*/ // System.out.println("MissControllerExternal : "+getLauncher().getMissController());
        return getLauncher().getMissController();
    }

//    private void ensureMissController() {
//	if (!isMissControllerSet()) {
//            initializeSimSt();
//        }
//    }
    
    /**
     * Made public for junit tests.
     * @param missActive
     */
    public void setMissActive(boolean missActive) {
        this.missActive = missActive;
    }

    // Activate Miss Controller
    public void activateMissController(boolean currentStatus) {

        boolean newStatus = !currentStatus;

        getLauncher().getMissController().activate(newStatus);
        setMissActive(newStatus);
//        brFrame.getHandler().switchSimStActivationMenu(newStatus);
        fireCtatModeEvent(new CtatModeEvent.SetSimStudentActivationMenuEvent(newStatus));
    }

    public void runSimStShuffleMode(String[] trainingSet, String[] testSet,
            String output, int numIteration) {

        // Have Sim. St. run in a batch mode
        getMissController().ssShuffleRunInBatch(trainingSet, testSet, output,
                numIteration);
    }

    /**
     * Run Sim St in a batch mode to run a validation test. Called by
     * SingleSessionLauncher when -ssBatchMode is ON
     * 
     * @param trainingSet
     *            a <code>String[]</code> value
     * @param testSet
     *            a <code>String[]</code> value
     */
    public void runSimStInBatchMode(String[] trainingSet, String[] testSet,
            String output) {

        // Have Sim. St. run in a batch mode
        getMissController().runSimStInBatchMode(trainingSet, testSet, output);
    }

    public void runSimStValidationTest(String[] testSet, String output) {

        getMissController().getSimSt().testProductionModelOn(testSet, output);
    }

    public void toggleWidgetFocusForSimSt(Object widget){
    	MissControllerExternal mc = getMissController();
    	if (mc!=null)
            mc.toggleFocusOfAttention(widget);
    }
    /**
     * Called by SingleSessionLauncher Tell Sim. St. whether the evaluation of RHS
     * operators should be cached (default) or not
     * 
     * @param flag
     *            a <code>boolean</code> value
     */
    public void setSsOpCached(boolean flag) {

        getMissController().setOpCached(flag);
    }
    
    public void setFoilLogDir(String foilLogDir) {

        getMissController().setFoilLogDir(foilLogDir);
    }

    public void setPrAgeDir(String prAgeDir) {

        getMissController().setPrAgeDir(prAgeDir);
    }

    public void setSsCondition(String ssCondition) {

        getMissController().setSsCondition(ssCondition);
    }
    
    /*
    public void setSsFoaGetter(String foaGetter) {

        getMissController().setSsFoaGetterClass(foaGetter);
    }
    */

//    public void setSsLearnCltErrorActions(boolean flag) {
//    	getMissController().setSsLearnCltErrorActions(flag);
//    }
//    
//    public void setSsLearnBuggyActions(boolean flag) {
//    	getMissController().setSsLearnBuggyActions(flag);
//    }    
//    
//    public void setSsLearnCorrectActions(boolean flag) {
//    	trace.out("gusBRD", "entered BR_Controller's setSsLearnCorrectActions");
//    	getMissController().setSsLearnCorrectActions(flag);
//    }
    
    public void setSsMemoryWindowSize(String memoryWindowSize) {

//	ensureMissController();
	getMissController().setSsMemoryWindowSize(memoryWindowSize);
    }

    public void setSsLearningLogFile(String learningLogFile) {
	
	getMissController().setSsLearningLogFile(learningLogFile);
    }
    
    public void setSsFeaturePredicateFile(String featurePredcatesFile) {
	
        getMissController().setSsFeaturePredicateFile(featurePredcatesFile);
    }

    /*
    public void ssSetSearchTimeOutDuration(String longDuration) {
        
        getMissController().setSsSearchTimeOutDuration(longDuration);
    }
    */

    public void setSsOperatorFile(String operatorFilePath) {

	getMissController().setSsOperatorFile(operatorFilePath);
    }
    
    public void setSsUserDefSymbols(String userDefSymbols) {
	
        getMissController().setSsUserDefSymbols(userDefSymbols);
    }
    
    /*
    public void setSsMemoryWindowOverRules(boolean flag) {
	
//	ensureMissController();
	getMissController().setSsMemoryWindowOverRules(flag);
    }
    */

    public void setSsForceToUpdateModel(boolean flag) {
	
//	ensureMissController();
	getMissController().setForceToUpdateModel(flag);
    }

    public boolean isDefiningStartState(){
    	return getCtatModeModel().isDefiningStartState();
    }
    public boolean isStartStateInterface(){
    	return startStateInterface;
    }
    public boolean isDemonstratingSolution(){
    	return getCtatModeModel().isDemonstratingSolution();
    }

    /*
    protected void setConnectedToProductionSystem(
            boolean connectedToProductionSystem) {
        this.connectedToProductionSystem = connectedToProductionSystem;
    }

    
    protected boolean isConnectedToProductionSystem() {
        return connectedToProductionSystem;
    }
    */

    public RuleNamesDisplayDialog getRuleDisplayDialog() {
    	return getRuleDisplayDialog(true);
    }
    
    /**
     * Return the Skill Names dialog object. Create if none.
     * @param visible
     * @return {@link #ruleDisplayDialog}
     */
    public RuleNamesDisplayDialog getRuleDisplayDialog(boolean visible) {
        if (ruleDisplayDialog == null)
            ruleDisplayDialog = new RuleNamesDisplayDialog(this, visible);
        return ruleDisplayDialog;
    }


    public void addCtatModeListener (CtatModeListener listener) {
        controllerListenerList.add(listener);
    	if (trace.getDebugCode("br")) trace.out("br", "addCtatModeListener("+listener+") list size now "+
    			controllerListenerList.size());
    }
    
    public void fireCtatModeEvent(CtatModeEvent e) {
    	if (trace.getDebugCode("br")) trace.out("br", "BR_Controller.fireCtatModeEvent() nListeners "+
    			controllerListenerList.size());
        for (Iterator i = controllerListenerList.iterator(); i.hasNext();) {
            CtatModeListener listener = (CtatModeListener) i.next();
            if (trace.getDebugCode("br")) trace.out("br", "fireCtatModeEvent("+e+") to listener "+
            		listener.getClass().getSimpleName());
            listener.ctatModeEventOccured(e);
        }
        if(!Utils.isRuntime()) {
        	JGraphPanel focusedWindow = getJGraphWindow();
        	if (focusedWindow != null)
        		focusedWindow.getJGraph().repaint(); // chc added for JGraph Layout Pro-1.4
        }
    }
    
    /**
     * Public access to singleton instance of this class. If {@link #rat}is
     * null, calls constructor and sets {@link #rat}.
     * <p>
     * 
     * Mutexes on class object to ensure constructor called only once: else
     * multithreaded access can clobber fields while 2nd constructor is active.
     * Ordinary calls, once the singleton instance has been created, do not
     * suffer the mutex overhead: to make this work, though, the assignment to
     * the static {@link #rat}must remain the last action within the mutexed
     * block.
     * 
     */
    public RuleActivationTree getRuleActivationTree() {
        if (ruleActivationTree == null) {
            synchronized (RuleActivationTree.class) { // see header note
                if (ruleActivationTree == null) { // recheck inside mutex
                    ruleActivationTree = new RuleActivationTree(this); // assignment
                    // MUST
                    // be
                } // at end of mutex block
            }
        }
        return ruleActivationTree;
    } 
    
    public CTAT_Options getOptions() {
    	if (trace.getDebugCode("options"))
    		trace.printStack("options", getClass().getSimpleName()+".getOptions() ctatOptions was "+ctatOptions);
        if (ctatOptions == null)
            ctatOptions = new CTAT_Options();
        return ctatOptions;
    }
    
    public void setOptions(CTAT_Options ctatOptions) {
    	if (trace.getDebugCode("options"))
    		trace.outNT("options", getClass().getSimpleName()+".setOptions("+ctatOptions+") value was "+
    				ctatOptions);
		this.ctatOptions = ctatOptions;
		
		if (VersionInformation.includesJess() && ctatOptions.isUseJESSCognitiveTutor())
			getCtatModeModel().setMode(CtatModeModel.JESS_MODE);
		else if (ctatOptions.isUseTDKCognitiveTutor())
			getCtatModeModel().setMode(CtatModeModel.TDK_MODE);
		else if (VersionInformation.isRunningSimSt() && ctatOptions.isUseSimulatedStudent()) {
			getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
			getCtatModeModel().setAuthorMode(CtatModeModel.DEMONSTRATING_SOLUTION);
		}
		else // if (ctatOptions.isUseExampleTracingTutor())
			getCtatModeModel().setMode(CtatModeModel.EXAMPLE_TRACING_MODE);
		
		// To avoid multiple login windows show up.
		if (ctatOptions.getConnectToLispAtStartup())
			utp.connectToTutor();
		else if (ctatOptions.getShowLoginWindow()) {
			getUniversalToolProxy().showLogin();
		}

		setLMSLoginEnabled(ctatOptions.getEnableLMSLogin());

		int interfaceHeight = ctatOptions.getInterfaceHeight();
		int interfaceWidth = ctatOptions.getInterfaceWidth();

		if (interfaceHeight > 0 && interfaceWidth > 0) {
			if (getStudentInterface() instanceof TutorWrapper) {
				((TutorWrapper) getStudentInterface()).setSize(interfaceWidth, interfaceHeight);
				((TutorWrapper) getStudentInterface()).storeSize();
			}
		}

		if (ctatOptions.getShowAdvanceProblemMenu()) {
			getStudentInterface().showAdvanceProblemMenuItem();
		}
	}

    public boolean getLMSLoginEnabled() {
        return lmsLoginEnabled;
    }

    private void setLMSLoginEnabled(boolean loginEnabled) {
        lmsLoginEnabled = loginEnabled;
        getStudentInterface().enableLMSLogin(loginEnabled);
    }

    public boolean getShowAdvanceProblemMenu() {
        return ctatOptions.getShowAdvanceProblemMenu();
    }

    public void setStartFindWidgetsForProblem(boolean startFindWidgetsForProblem) {
        this.startFindWidgetsForProblem = startFindWidgetsForProblem;
        // if (startFindWidgetsForProblem == true)
        // trace.printStackWithStatement("find widgets for problem");
    }

    public boolean getStartFindWidgetsForProblem() {
        return this.startFindWidgetsForProblem;
    }

    public void setNotFoundWidgetsForProblem(Vector notFoundWidgetsForProblem) {
        this.notFoundWidgetsForProblem = notFoundWidgetsForProblem;
    }

    public Vector getNotFoundWidgetsForProblem() {
        if (notFoundWidgetsForProblem == null)
            this.notFoundWidgetsForProblem = new Vector();
        return this.notFoundWidgetsForProblem;
    }

    public boolean invalidWidgetsExist() {
        return getStartFindWidgetsForProblem()
                && getNotFoundWidgetsForProblem().size() > 0;
    }

    public MT getModelTracer() {
        if (VersionInformation.includesJess() && modelTracer == null) {
        	modelTracer = new MT(this);
        	if(modelTracer.getWmeEditor() != null && modelTracer.getWmeEditor().getPanel() != null)  
				modelTracer.getWmeEditor().getPanel().refresh();
        }
        return modelTracer;
    }

    public void resetMT() {
        if (modelTracer != null) {
            modelTracer.clearRete();
        }
    }
    
    /**
     * Tell whether Sim Student is active.
     * 
     * @see edu.cmu.pact.Utilities.CTAT_Controller#updateModelOnTraceFailure()
     */
    public boolean updateModelOnTraceFailure() {
        return getCtatModeModel().isSimStudentMode();
    }

    public void reset() 
    {
    	reset("","");
    }
    
    public void reset(ProblemModel pm) 
    {
    	String problemName = pm.getProblemName();
    	String problemFullName = pm.getProblemFullName();
    	fireCtatModeEvent(CtatModeEvent.CLEAR_DRAWING_AREA);
        getHintMessagesManager().cleanUpHintOnChange();
        setCopySubgraphNode(null);

        if (pm != null)
        	pm.reset(problemName, problemFullName);
        
        if (getSolutionState() != null)
        	getSolutionState().reset();
        
        if (saiTable!=null)
        	saiTable.clear();

        processTraversedLinks = new ProcessTraversedLinks(this);
        getProcessTraversedLinks().initTraversedLinks();
        
        updateStatusPanel(null);
    }
    /**
     * Reinitialize the problem model.
     * @param problemName {@link #problemName} to restore
     * @param problemFullName {@link #problemFullName} to restore
     */
    public void reset(String problemName, String problemFullName) 
    {
    	fireCtatModeEvent(CtatModeEvent.CLEAR_DRAWING_AREA);
        getHintMessagesManager().cleanUpHintOnChange();
        setCopySubgraphNode(null);
        
        ProblemModel pm = getProblemModel();

        if (pm != null)
        	pm.reset(problemName, problemFullName);
        
        if (getSolutionState() != null)
        	getSolutionState().reset();
        
        if (saiTable!=null)
        	saiTable.clear();

        processTraversedLinks = new ProcessTraversedLinks(this);
        getProcessTraversedLinks().initTraversedLinks();
        
        updateStatusPanel(null);
    }
    
    /**
     * Special treatment for the 'Done" match: advance the problem.
     */
    private void processDoneMatch() {
        trace.out ("inter", "process done match");
        HintWindowInterface hwi = null;
		StudentInterfaceWrapper siw = getStudentInterface();
		if (siw != null) 
			hwi = siw.getHintInterface();

        CtatLMSClient ctatLms = getCTAT_LMS(); 
        if (ctatLms != null) {
        	if (ctatLms.isStudentLoggedIn()) {
        		trace.out ("inter", "logged in , advance problem now");
                ctatLms.advanceProblem();
                if (hwi != null)
                	hwi.reset();
                return;           // LMS will advance the problem
        	}
        }

        // here if not logged in to LMS: try done listeners
		if (siw != null) {
			WrapperSupport ws = siw.getWrapperSupport();
			if (ws != null)
				ws.doneActionPerformed();
			if (hwi != null)
				hwi.reset();
		}
    }

    // ////////////////////////////////////////////////////
    /**
     * Called by TutorWindow when the window is created.
     */
    // ////////////////////////////////////////////////////
    public void removeAllHighlights() {
        for (Iterator i = getCommWidgetTable().values().iterator(); i.hasNext(); ) {
            JCommWidget d = (JCommWidget) i.next();
            d.removeHighlight("");
        }
    }

//    public Vector getCommWidgetVector() {
//        return commWidgetVector;
//    }


  /*  public Hashtable getCommWidgetTable() {
        // commNameTable is set by CommWidgets.initialize(), which is called
        // when a new commWidget is created
        return commNameTable;
    }*/

    /**
     * @param toolTipsInitialized The toolTipsInitialized to set.
     */
    public void setToolTipsInitialized(boolean toolTipsInitialized) {
        this.toolTipsInitialized = toolTipsInitialized;
    }

    /**
     * @return Returns the toolTipsInitialized.
     */
    public boolean isToolTipsInitialized() {
        return toolTipsInitialized;
    }

    /**
     * @param showWidgetInfo The showWidgetInfo to set.
     */
    public void setShowWidgetInfo(boolean showWidgetInfo) {
        this.showWidgetInfo = showWidgetInfo;
    }

    /**
     * @return Returns the showWidgetInfo.
     */
    public boolean isShowWidgetInfo() {
        return showWidgetInfo;
    }

    public SingleSessionLauncher getLauncher() {
        return launcher;
    }
    
    public boolean inAppletMode() {
    	if (launcher == null)
    		return false;
    	return launcher.inAppletMode();
    }

    /**
     * Get the tutor's response to the GoToWMState operation. This call is
     * synchronous: it waits for the response from the rule engine.
     * @param  nodeName argument for {@link #goToState(String)} 
     * @return List of response message(s) received from
     *         {@link #goToState(String)}
     */
    public MessageObject getGoToWMStateResponse(String nodeName) {
	
        if (trace.getDebugCode("allrulefirings")) trace.out("allrulefirings", "entered getGoToWMStateResponse");

    	ProblemNode startNode = null;
    	ProblemNode targetNode = null;

        if (trace.getDebugCode("validation")) trace.out("validation", "before {1");

    	{
    		ProblemModel pm = getProblemModel();
    		if (null == pm)
    			throw new IllegalStateException("no problem loaded");
    		if (null == (startNode = pm.getStartNode()))
    			throw new IllegalStateException("start node not found");
    		if (null == (targetNode = pm.getNode(nodeName)))
    			throw new IllegalArgumentException("target node not found: "+
    					nodeName);
    	}
        if (trace.getDebugCode("validation")) trace.out("validation", "after {1");
        
    	Vector pathEdges = new Vector();
        boolean pathFound = findPath(startNode, targetNode, pathEdges, 
        		(!updateModelOnTraceFailure() ? 1 : -1));
        //boolean pathFound = findPath(startNode, targetNode, pathEdges, true);

        if (trace.getDebugCode("validation")) trace.out("validation", "pathEdges = " + pathEdges);

        if (trace.getDebugCode("mt")) trace.out("mt", "pathFound "+pathFound+" ?= !isSearchPathFlag() "+
        		!getProblemModel().isSearchPathFlag());
        // success
        if (!pathFound) {
            throw new IllegalArgumentException("No preferred path from  node "+
                    startNode.getName()+" to node "+targetNode.getName());
        }
        MessageObject request = null;
        if (trace.getDebugCode("validation")) trace.out("validation", "before {2");

        Vector selectionList = new Vector();
        Vector actionList = new Vector();
        Vector inputList = new Vector();
        Vector authorIntentList = new Vector();
        Vector uniqueIDList = new Vector();
        buildSAIListsFromPath(pathEdges, selectionList, actionList,
        		inputList, authorIntentList, uniqueIDList);
        if (trace.getDebugCode("mt")) trace.out("mt", "use preferred path to sendGo_To_WM_State");
        request = buildGo_To_WM_State(selectionList,
        		actionList, inputList, authorIntentList, uniqueIDList);

        MT mt = getModelTracer();
        if (mt == null)
        	throw new IllegalStateException("ModelTracer does not exist");
        trace.out("validation", "before setMaxDepth");
        //mt.getRete().setMaxDepth(0); //Gustavo inserted 29Jan     
        trace.out("validation", "passed setMaxDepth");

        //mt.getRete().setTreeDepth("1"); //Gustavo inserted 29Jan        
        MessageObject response = mt.handleCommMessage(request);

    	if (trace.getDebugCode("mt")) trace.out("mt", "response from sendGo_To_WM_State:\n"+response);        
        if (response == null)
        	throw new RuntimeException("null response from MT.handleCommMessage("+
        			request+");");
        return response;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Save the intermediate (.brdp) file. No-op if {@link #getProcessTraversedLinks()}
     * or {@link #getTutorModeBrdTempDirectory()} returns null.
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void saveTraversedPathFile() {
    	String brdpDir = getTutorModeBrdTempDirectory();
        if (trace.getDebugCode("brdp")) trace.out("brdp", "save TraversedPath to File "+brdpDir);
        if (brdpDir == null)
        	return;

        // detect that processTraversedLinks is created; otherwise do nothing.
        if (getProcessTraversedLinks() == null) {
            trace.err("processTraversedLinks is not created yet.");
            return;
        }
        try {
        	File fi = new File(brdpDir);

        	// if no this folder then create one
        	if (!fi.exists()) {
        		if (trace.getDebugCode("brdp")) trace.out("brdp", "the folder: " + brdpDir
        				+ " does not exist. So create one.");
        		fi.mkdir();
        	} else if (fi.isFile()) {
        		if (trace.getDebugCode("brdp")) trace.out("brdp", brdpDir + " is a file, so delete it & create a folder with the same name.");
        		// if tutorModeBrdTempDirectory is not a directory delete it
        		if (fi.delete())
        			// create a folder with name tutorModeBrdTempDirectory
        			fi.mkdir();
        	}


        	ProblemModel pm = getProblemModel();
        	String traversedPathName = brdpDir + pm.getProblemName();
        	boolean flag = pm.getProblemName().endsWith(
        	".brd");

        	if (flag)
        		traversedPathName = traversedPathName + "p";
        	else
        		traversedPathName = traversedPathName + ".brdp";

        	getProcessTraversedLinks().saveTraversedLinks_Tofile(
        			traversedPathName);
        	
        } catch (Throwable e) {
        	if (trace.getDebugCode("brdp")) trace.out("brdp", "Error saving brdp file to "+brdpDir+": "+e);
        }
    }

    /**
     * @param tutorModeBrdTempDirectory
     *            The tutorModeBrdTempDirectory to set.
     */
    public void setTutorModeBrdTempDirectory(String tutorModeBrdTempDirectory) {
        this.tutorModeBrdTempDirectory = tutorModeBrdTempDirectory;
    }

    /**
     * @return Returns the tutorModeBrdTempDirectory.
     */
    protected String getTutorModeBrdTempDirectory() {
        return tutorModeBrdTempDirectory;
    }

    /**
     * @see CTAT_Controller#isDockedNow().
     */
    public boolean isDockedNow() {
        return dockedNow;
    }

    public BRPanel getBR_Frame() {
    	throw new UnsupportedOperationException("The BR_Panel was removed, see comments in BR_Panel to use it's old fields");
    	//return brPanel;
    }

	public ProblemModelEventFactory getEventFactory() {
		return eventFactory;
	}

    public AbstractCtatWindow getDockedFrame() {
		if(!(getServer() instanceof CTAT_Launcher))
			return null;
    	return getServer().getDockedFrame();
    }

    /**
     * Return the value of BRD_OTHER_DIR_KEY if it is set,
     * otherwise return the value of "user.dir" system property
     * 
     * @return
     */
    public String getPreferredBRDLocation() {
        String targetDir = getTSLauncherServer().getPreferencesModel().getStringValue(
                SaveFileDialog.BRD_OTHER_DIR_KEY);
        if (targetDir == null)
            targetDir = System.getProperty("user.dir");
        return targetDir;
    }

    public void setPreferredBRDLocation(String location) {
        getPreferencesModel().setStringValue(
                SaveFileDialog.BRD_OTHER_DIR_KEY, location);
    }
    /**
     * Get the socket proxy instance.
     * @return value of {@link #remoteProxy}
     */
    public RemoteProxy getRemoteProxy() {
		return remoteProxy;
	}

    /**
     * Set the socket proxy instance.
     * @param remoteProxy new value for {@link #remoteProxy}
     */
	public void setRemoteProxy(RemoteProxy remoteProxy) {
		if(trace.getDebugCode("sp"))
			trace.out("sp", "BR_Controller.setRemoteProxy("+remoteProxy+")");
		this.remoteProxy = remoteProxy;
        ProblemModel pm = getProblemModel(); 
        if (pm != null && remoteProxy != null)
        	pm.setUseCommWidgetFlag(false);
        if (Utils.isRuntime())
        	return;
        CtatFrameController cfc = getCtatFrameController();
        CtatFrame cf = (cfc == null ? null : cfc.getDockedFrame());
        if (cf != null) {
        	CtatMenuBar mb = cf.getCtatMenuBar();
        	if (mb != null)
        		mb.enableOpenInterfaceMenu(Boolean.FALSE);
        }
	}

    /**
     * 
     */
//    public void setDemonstrateMode() {
//        getLoggingSupport().authorActionLog(
//                AuthorActionLog.BEHAVIOR_RECORDER,
//                BR_Controller.SWITCH_MODE, "", getCtatModeModel().getModeTitle(),
//                CtatModeModel.DEMONSTRATE_MODE);
//
//        setBehaviorRecorderMode(CtatModeModel.DEMONSTRATE_MODE);
//
//        if ((getProblemModel().getStartNode() != null)
//                && (getSolutionState().getCurrentNode() != null))
//            sendCommMsgs(getSolutionState()
//                    .getCurrentNode(), getProblemModel()
//                    .getStartNode());
//    }
//
//    /**
//     * 
//     */
//    public void setRuleTracingMode() {
//        getLoggingSupport().authorActionLog(
//                AuthorActionLog.BEHAVIOR_RECORDER,
//                BR_Controller.SWITCH_MODE, "", getCtatModeModel().getModeTitle(),
//                CtatModeModel.PRODUCTION_SYSTEM_MODE);
//
//        setBehaviorRecorderMode(CtatModeModel.PRODUCTION_SYSTEM_MODE);
//    }
//
//
//    public void setExampleTracingMode() {
//        getLoggingSupport().authorActionLog(
//                AuthorActionLog.BEHAVIOR_RECORDER,
//                BR_Controller.SWITCH_MODE, "", getCtatModeModel().getModeTitle(),
//                CtatModeModel.EXAMPLE_TRACING_MODE);
//
//        setBehaviorRecorderMode(CtatModeModel.EXAMPLE_TRACING_MODE);
//
//        int sizeOfCommutEdgesAdded = getProblemModel()
//                .getUnorderedEdges().size();
//
//        for (int i = 0; i < sizeOfCommutEdgesAdded; i++)
//            sendSingleCommMsg((ProblemEdge) getProblemModel().getUnorderedEdges().elementAt(i),
//                    false);
//
//    }

    /**
     * Called by the graph views when a problem node is clicked
     * 
     * @param problemNode
     * @param controller
     * @return true if traversed to state
     */
    
    //Gustavo, 17Oct2006: Generally speaking, clicking on a problem node sets it as the current
    //node, makes the input appear on the student interface and adds it to the WME.
    //Depending on the tutor mode, there are exceptions for buggy nodes.
    public boolean problemNodeClicked(ProblemNode problemNode) {

        if (trace.getDebugCode("miss")) trace.out("miss", "problem node clicked = " + problemNode);

        if (problemNode.equals(getProblemModel().getStartNode())) {
			getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER, 
					BR_Controller.GO_TO_START_STATE, "", "", "");
        	goToStartState(true, true);
        	return true;
        }

        boolean result = goToState(problemNode);
        
    	if (result) {
    	        getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
    	                BR_Controller.GO_TO_STATE, problemNode.toString(), "", ""); 
    	}        

        return result;
    }
    
	public ProblemNode MoveToNextStepOnPreferredPath() {
		ProblemNode currentNode = getCurrentNode();
		ProblemNode NextNode = null;
		ProblemEdge edge = null;

		Enumeration iterOutEdge = getProblemModel().getProblemGraph().getOutgoingEdges(currentNode);
		if (!iterOutEdge.hasMoreElements())  // no outgoing links: stop
			return currentNode;              
										     // AlertType.WARNING.playSound(display);

		while (iterOutEdge.hasMoreElements()) {
			edge = (ProblemEdge) iterOutEdge.nextElement();
			// only check preferred child edge for hint
			if (edge.isPreferredEdge()) {
				NextNode = edge.getDest();
				break;
			}
		}
		if (edge == null) {
			trace.err("MoveToNextStepOnPreferredPath() no preferred link from current node "+
					currentNode);
			return currentNode;
		}
		EdgeData edgeData = edge.getEdgeData();
		edgeData.resetCommMessage();  // ensure comm msg is up-to-date
		handleCommMessage(edgeData.getDemoMsgObj());

		return NextNode;
	}
	
	public ProblemNode MoveToPrevStepOnPreferredPath(ProblemNode problemNode) {
		ProblemNode PrevNode = null;
		ProblemEdge tempEdge;
		
		Vector pathEdges = new Vector();
		this.findPath(getProblemModel().getStartNode(), problemNode, pathEdges, 0);
		if (pathEdges.size() > 0) {
			ProblemEdge nearestEdge = (ProblemEdge) pathEdges.remove(pathEdges.size()-1);
			if (problemNode != nearestEdge.getDest()) {
				trace.err("MoveToPrevStepOnPreferredPath("+problemNode+
						") nearest edge from findPath()="+nearestEdge+" dest!=problemNode");
				return problemNode;
			}
			PrevNode = nearestEdge.getSource();
		}
/*		
		if (problemNode == getProblemModel().getStartNode())
			PrevNode = problemNode;	
		else { // NextNode = problemNode.getNextNode();
		       Enumeration iterInEdge = getProblemModel().getProblemGraph().incomingEdges(problemNode);
		       while (iterInEdge.hasMoreElements()) {
		            tempEdge = (ProblemEdge) iterInEdge.nextElement();
		            // only check preferred child edge for hint
		            if (tempEdge.isPreferredEdge() && (tempEdge.getDest() == problemNode)) {
		            	PrevNode = tempEdge.getSource();
		            	break;
		            }
		       }
		}
*/
		if (trace.getDebugCode("br")) trace.out("br", "Prevous node = " + PrevNode);
        if (PrevNode != null) {
	        boolean result = goToState(PrevNode);
	        return PrevNode;
        } else
        	return problemNode;
 	}
	
    /**
     * Set the problem state to the given node.
     * 
     * @param problemNode
     * @param controller
     * @return true if traversed to state
     */
    //Gustavo, 17Oct2006: Generally speaking, clicking on a problem node sets it as the current
    //node, makes the input appear on the student interface and adds it to the WME.
    //Depending on the tutor mode, there are exceptions for buggy nodes.
    //This function simulates clicking on a node.
    public boolean goToState(ProblemNode problemNode) {
    	if (trace.getDebugCode("br")) trace.out("br", "Entering goToState");

    	if(problemNode == null)  // occurs when pasting into an empty graph
    		return false;
    	if(!getPseudoTutorMessageHandler().getExampleTracerGraph().isNodeConnected(problemNode.getUniqueID()))
        	return false;
        
        getHintMessagesManager().cleanUpHintOnChange();
        
        if (problemNode == getSolutionState().getCurrentNode()
                && problemNode == getProblemModel().getStartNode()) {

            if (getCtatModeModel().isDemonstratingSolution()){

                setCurrentNode2(getProblemModel().getStartNode());
            }
            else {
            	if (getCtatModeModel().isExampleTracingMode()) {
            		getPseudoTutorMessageHandler().initializePseudoTutorAndSendStartState(false);
            	}
            	else {
            		goToStartState();
            	}
            }
            return true;
        }

        //early return on buggy nodes
        if (!getCtatModeModel().isSimStudentMode() && //... but only if not in SimStudent mode
                getProblemModel().isBuggyNode(problemNode)) {

            return false;
        }

        // for DEMONSTRATE_MODE
        //        if (controller.getMode().equalsIgnoreCase(
        //                CtatModeModel.DEMONSTRATE_MODE)) {
//        if (getCtatModeModel().isDemonstrateMode()) {
//            setCurrentNode2(problemNode);
//
//            return;
//        }

        if (getCtatModeModel().isExampleTracingMode()) {
            if (problemNode == getProblemModel().getStartNode()) {
            	getPseudoTutorMessageHandler().initializePseudoTutorAndSendStartState(false);
                getJGraphWindow().getJGraph().repaint();
            } else if (problemNode != getSolutionState().getCurrentNode()) {
            	//problem gotostate doesn't necassarily reset to the startstate...
            	//should we be calling just gotostate..
            	if(getProblemModel()!=null && getExampleTracer()!=null){
            		getProblemModel().setVariableTable(getExampleTracer().getStartStateVT());
                }
            	sendResetBeforeTraverseToClickedNode();
                Vector newUserVisitedStates = setCurrentNode2(problemNode); // runs ex-tracer to new state

                if (trace.getDebugCode("br")) trace.out("br", "extracer current node after setCurrentNode2 "+
                		getCurrentNode().getUniqueID());
            }
            return true;
        }

        // for PRODUCTION_SYSTEM_MODE
        //        if (controller.getMode().equalsIgnoreCase(
        //                CtatModeModel.PRODUCTION_SYSTEM_MODE)) {
        if (getCtatModeModel().isRuleEngineTracing()) {
            if (problemNode == getProblemModel().getStartNode()) {
                // trace.out("miss", "problem node clicked goToStartState()");
                goToStartStateForRuleTutors();
            } 
            else {
                setCurrentNode2(problemNode);
            	
                // Fri Sep 29 23:42:21 LDT 2006 :: Noobru
                // CheckProductionRulesChainNew() does not make the node clicked as a current node
                // when the rule for the corresponding edge is not learned, hence causes a problem
                // for SimStudent.
                // trace.out("miss", "problem node clicked calling checkProductionRulesChainNew()");

                checkProductionRulesChainNew(getSolutionState().getCurrentNode());
                    
                // trace.out("miss", "currnet Node = " + getSolutionState().getCurrentNode());
            }

            // trace.out("miss", "problem node clicked returning 3");
        }
        return true;
    }

    public void ssUseDecomposition()
    {
        getMissController().ssUseDecomposition();
        
    }

	public void setIsReducedMode(boolean b)
	{
		getServer().setReducedMode(b);
	}
	
	public boolean getIsReducedMode()
	{
		return getServer().isReducedMode();
	}

	public void setHintMode(boolean b)
	{
		hintMode = b;
	}
	
	public boolean getHintMode()
	{
		return hintMode;
	}
	
	public PseudoTutorMessageHandler getPseudoTutorMessageHandler() {
		return this.pseudoTutorMessageHandler;
	}

	public ExampleTracerTracer getExampleTracer() {
		//return pseudoTutorMessageHandler.getExampleTracer();
		return getPseudoTutorMessageHandler().getExampleTracer();
	}

	public void restoreDefaultView() {
		getServer().restoreDefaultView();
	}


	/**
	 * Set the {@value SaveFileDialog#BRD_OTHER_DIR_KEY} preference to the directory
	 * portion of the given path. In SimStudent mode, also set
	 * {@value CTAT_Controller#PROBLEM_DIRECTORY} for backward compatibility.
	 * @param path
	 */
	public void setBRDDirectory(String path) {
		String dir = Utils.getDirectory(path);
		if (dir != null) {
			getPreferencesModel().setStringValue(SaveFileDialog.BRD_OTHER_DIR_KEY, dir);
			if(getCtatModeModel().isSimStudentMode()) {
				if (trace.getDebugCode("miss"))
					trace.out("miss", "CTAT_Controller.PROBLEM_DIRECTORY set to: "+dir);
				getPreferencesModel().setStringValue(PROBLEM_DIRECTORY, dir);
			}
		}
	}

	/**
	 * Advance to a given node via the path recorded.
	 * @param linkIds array of link identifiers, in order to follow;
	 *        as returned by {@link #getVisitedPath(int)}
	 * @param tgtNodeId identifier of target node; assumes this node is
	 *        a dest node for one of the links in linkIds
	 */
	private void advanceToNodeViaPath(int[] linkIds, int tgtNodeId) {
		ProblemModel pm = getProblemModel();
		ProblemGraph graph = pm.getProblemGraph();
		if (linkIds == null || linkIds.length < 1)
			return;
		if (graph.getNode(tgtNodeId) == pm.getStartNode())
			return;
		ProblemEdge edge = null;
		ProblemNode dest = null;
		int i = 0;
		do {
			edge = graph.lookupProblemEdgeByID(linkIds[i++]);
			dest = edge.getDest();
			EdgeData edgeData = edge.getEdgeData();
			if (!"tool".equalsIgnoreCase(edgeData.getActor()))
				getPseudoTutorMessageHandler().processPseudoTutorInterfaceAction(
						edgeData.getSelection(), edgeData.getAction(), edgeData.getInput());
		} while (tgtNodeId != dest.getUniqueID());
	}

	/**
	 * Make a list of link identifiers whose links form the current visited
	 * path from the start state.
	 * @param  tgtNodeId identifier of node to find along 
	 * @return ids if tgtNodeId is either the start node or one of the dest
	 *         nodes in the path; null if tgtNode ID 
	 */
/*	private int[] getVisitedPath(int tgtNodeId) {
		List edges = pseudoTutorMessageHandler.getTraversedEdges();
//		List edges = getSolutionState().getUserVisitedEdges();
		int[] linkIds = new int[edges.size()];
		int i = 0;
		boolean foundNode = false;
		if (tgtNodeId == getProblemModel().getStartNode().getUniqueID())
			foundNode = true;  // start node is on every path
		for (Iterator it = edges.iterator(); it.hasNext(); ++i) {
			ProblemEdge edge = (ProblemEdge) it.next();
			linkIds[i] = edge.getUniqueID();
			if (tgtNodeId == edge.getDest().getUniqueID())
				foundNode = true;
		}
		if (trace.getDebugCode("br")) trace.out("br", "getVisitedPath("+tgtNodeId+") edges "+edges+
				", linkIds "+linkIds+", foundNode "+foundNode);
		if (foundNode)
			return linkIds;
		else
			return null;
	}
*/
	public CtatFrameController getCtatFrameController() {
		if(!(getServer() instanceof CTAT_Launcher))
			return null;
		return getServer().getCtatFrameController();
	}
	

	public boolean isStartStateModified() {
		if(getUniversalToolProxy() == null)
			return false;
		return getUniversalToolProxy().isStartStateModified();
	}

	public void setStartStateModified(boolean startStateModified) {
		if (trace.getDebugCode("mps")) trace.out("mps", "Set Modified StartState to " + startStateModified);
		this.startStateModified = startStateModified;
	}
	public Lock getWidgetSynchronizedLock() {
	return widgetSynchronizedLock;
	}
	
	/**
	 * Get a pointer to the rules or skills available for attachment to links.
	 * @return {@link #ruleProductionCatalog}; creates if null
	 */
	public RuleProduction.Catalog getRuleProductionCatalog() {
		if(!Utils.isRuntime())
			return getServer().getRuleProductionCatalog();
		if(tsRuleProductionCatalog == null) {
			synchronized(this) {                // recheck after mutex
				if(tsRuleProductionCatalog == null)
					tsRuleProductionCatalog = new RuleProduction.Catalog(); 
			}
		}
		return tsRuleProductionCatalog;
	}

    /**
     * If newRuleNameText is not in ruleProductionList then add it.
     * @param newRuleNameText
     * @param newProductionSet
     * @return {@link RuleProduction} instance, as existed or newly added
     */
    public RuleProduction checkAddRuleName(String newRuleNameText, String newProductionSet) {
    	RuleProduction result = getRuleProduction(newRuleNameText, newProductionSet);
    	if (result == null) {
    		result = getRuleProductionCatalog().checkAddRuleName(newRuleNameText, newProductionSet);
            // Fri Jul  7 12:02:52 LDT 2006 :: Noboru
            // We don't want to see the pop-up window showing "Skill Names" 
            // at least when SimStudent is getting demonstrated
            if (!getCtatModeModel().isSimStudentMode())
            	getRuleDisplayDialog(false).resetRuleProductionList(false);
    	}
    	return result;
    }

    /**
     * Convenience method to encapsulate {@link #getRuleProductionCatalog()}.
     * @param ruleName
     * @param productionSet
     * @return {@link RuleProduction.Catalog#getRuleProduction(String, String) 
     *          getRuleProductionCatalog.getRuleProduction(ruleName, productionSet)}
     */
    public RuleProduction getRuleProduction(String ruleName, String productionSet) {
		return getRuleProductionCatalog().getRuleProduction(ruleName, productionSet);
	}

    /**
     * Convenience method to encapsulate {@link #getRuleProductionCatalog()}.
     * @param displayName == ruleName + " " + productionSet
     * @return {@link RuleProduction.Catalog#getRuleProduction(String) 
     *          getRuleProductionCatalog.getRuleProduction(displayName)}
     */
    public RuleProduction getRuleProduction(String displayName) {
		return getRuleProductionCatalog().getRuleProduction(displayName);
	}
    
    /**
     * @return {@link #sessionStorage}
     */
	public Map<String, Object> getSessionStorage() {
		return sessionStorage;
	}

	/**
	 * Replace {@link #skillsConsole}. Calls {@link SkillsConsoleDialog#dispose()} on any
	 * old console.
	 * @param skillsConsole
	 * @return old value, or null
	 */
	public SkillsConsoleDialog addSkillsConsole(SkillsConsoleDialog skillsConsole) {
		SkillsConsoleDialog oldConsole = this.skillsConsole ;
		if (oldConsole != null)
			oldConsole.dispose();
		this.skillsConsole = skillsConsole;
		return oldConsole;
	}
	
	/**
	 * Clear {@link #skillsConsole}. 
	 * @return old value, or null
	 */
	public SkillsConsoleDialog removeSkillsConsole() {
		SkillsConsoleDialog oldConsole = this.skillsConsole;
		this.skillsConsole = null;
		return oldConsole;
	}

	/**
	 * Add a set of Skill values to the {@link #skillsConsole}.
	 * @param assocRulesResp
	 */
	public void updateSkillsConsole(MessageObject assocRulesResp) {
		if (skillsConsole != null)
			skillsConsole.append(assocRulesResp);
	}

	/** Messsages to recover the prior state of an interrupted problem. */
	private Map<String, Boolean> restoreTransactions = new HashMap<String, Boolean>();

	/** States with respect to recovering a problem state by replaying past messages. */
	enum ProblemStateStatus {
		empty,        // nothing to recover
		incomplete,   // recovery in progress
		complete      // recovery finished
	};
	/** Current restore status. */
	private ProblemStateStatus problemStateStatus = ProblemStateStatus.empty;

	/**
	 * Tell whether the system is restoring the problem state. If so, output to the display
	 * may be suppressed.
	 * @param transactionId identifier for outstanding transaction; can be null
	 * @return true if w/in this transaction, system is restoring previous problem state;
	 *         else returns {@link #problemStateStatus} == incomplete
	 */
	public boolean isRestoringProblemState(String transactionId) {
		if (transactionId == null)
			transactionId = "";
		Boolean isRestoreTrans = (restoreTransactions == null ?
				null : restoreTransactions.get(transactionId.toLowerCase()));
		if (trace.getDebugCode("br")) trace.outNT("br", "isRestoringProblemState("+transactionId+
				") finds "+isRestoreTrans+", problemStateStatus "+problemStateStatus);
		if (isRestoreTrans != null)            // found this transaction: rtn its proper status
			return isRestoreTrans.booleanValue();
		return ProblemStateStatus.incomplete.equals(problemStateStatus);      // return overall status
	}

	/**
	 * If the argument is not a valid value for ProblemStateStatus, sets ProblemStateStatus.empty.
	 * @param value new value of {@link #problemStateStatus}
	 */
	private void setProblemStateStatus(String value) {
		if(value == null)
			value = "empty";
		try {
			problemStateStatus = ProblemStateStatus.valueOf(value.toLowerCase());
		} catch(Exception e) {
			trace.err("unexpected value for problem_state_status \""+value+"\"; setting empty");
			problemStateStatus = ProblemStateStatus.empty;
		}
	}

	/**
	 * Enter a transaction into {@link #restoreTransactions}. No-op unless
	 * {@link #isRestoringProblemState(String)} is true.
	 * @param interfaceAction tool message establishing the transaction.
	 */
	public void openTransaction(MessageObject interfaceAction) {
		if (!isRestoringProblemState(null))
			return;
		String id = null;
		if (interfaceAction == null || (id = interfaceAction.getTransactionId()) == null)
			return;
		if (restoreTransactions == null)
			restoreTransactions = new HashMap<String, Boolean>();
		restoreTransactions.put(id, Boolean.TRUE);
	}

	/**
	 * Clear a transaction from {@link #restoreTransactions}.
	 * @param transactionId
	 * @return removed entry from {@link #restoreTransactions}
	 */
	public Boolean closeTransaction(String transactionId) {
		Boolean result = null;
		boolean sendProblemRestoreEnd = false;
		if (restoreTransactions == null)
			sendProblemRestoreEnd = false;
		else {
			result = restoreTransactions.remove(transactionId);
			sendProblemRestoreEnd = restoreTransactions.isEmpty();
		}
		if (sendProblemRestoreEnd)
			sendProblemRestoreEnd();
		return result;
	}

	/**
	 * Create and send a ProblemRestoreEnd message.  Sets {@link #problemStateStatus} to complete.
	 */
	private void sendProblemRestoreEnd() {
		MessageObject msg = MessageObject.create(MsgType.PROBLEM_RESTORE_END, "SetNoteProperty");
		msg.suppressLogging(true);

		ProblemSummary ps = getProblemSummary();
		if (trace.getDebugCode("br")) trace.printStack("br", "sendProblemRestoreEnd1: problemSummary\n"+ps.toXML());
		if (ps != null && ps.getSkills() != null) {
			Skills skills = ps.getSkills();
			if (skills != null && skills.getAllSkills().size() > 0) {
				Vector<String> sv = skills.getSkillBarVector(true, true);
				msg.setProperty("Skills", sv);
			}
		}
		setProblemStateStatus("complete");
		handleMessageUTP(msg);
	}

	/**
	 * Add a component's {@value MsgType#INTERFACE_DESCRIPTION} message to the
	 * {@link CTATSSELink#components}.
	 * @param node a JDOM node containing all the serialized XML of one component; no-op if null
	 */	
	private void addComponent (Element node)
	{			
		if(node == null)  // FIXME occurs with Java components
			return;
		CTATComponent component=new CTATComponent ();
		component.fromXML (node);
		if(CTATSSELink.components != null)
			CTATSSELink.components.add(component);
		getProblemModel().fireProblemModelEvent(new CTATStartStateEvent(this,MsgType.INTERFACE_DESCRIPTION,component));
	}	
	/**
	 * 
	 * @param node a JDOM node containing all the serialized XML of one component
	 */	
	private void addInterfaceAction (Element node)
	{			
		CTATSAI interfaceAction=new CTATSAI ();
		interfaceAction.fromCommXML(node);
		getProblemModel().fireProblemModelEvent(new CTATStartStateEvent(this,MsgType.INTERFACE_ACTION,interfaceAction));
	}		
    /**
     * 
     */
    public void interfaceConnected () 
    {        	
    	getProblemModel().fireProblemModelEvent(new CTATStartStateEvent(this,"InterfaceConnected",null));    	
    }
    /**
     * 
     */
    public void interfaceDisconnected () 
    {  
    	getProblemModel().fireProblemModelEvent(new CTATStartStateEvent(this,"InterfaceDisconnected",null));    	
    }    
    
    /**
     * @return TutorMessageHandler#getMessageTank()} if in a rule-based tutor;
     *         else PseudoTutorMessageHandler#getMessageTank() 
     */
    public MessageTank getMessageTank() {
    	if (getCtatModeModel().isRuleEngineTracing())
    		return tutorMessageHandler.getMessageTank();
    	else
    		return getPseudoTutorMessageHandler().getMessageTank();
    }
    /**
     * 
     */
    public void sendInterfaceDescription (String instanceName,Element serialized) 
    {
        if (utp==null)
        	return;
               
        MessageObject newMessage = MessageObject.create(MsgType.INTERFACE_DESCRIPTION, "SendNoteProperty");

    	newMessage.setProperty("Version", edu.cmu.pact.Utilities.VersionInformation.RELEASE_NAME);
		newMessage.setProperty("BuildNumber", edu.cmu.pact.Utilities.VersionInformation.VERSION_NUMBER);
		newMessage.setProperty("BuildDate", edu.cmu.pact.Utilities.VersionInformation.BUILD_DATE);
    	newMessage.setProperty("serialized",serialized,true);
						
        if (trace.getDebugCode("br")) 
        	trace.out("br", "sendInterfaceDescription: " + newMessage.toString());
        
        handleMessageUTP(newMessage);
	}

    /**
     * Call {@link SocketProxy#extCloseConnection()}.
	 * @param preserveSession if true, don't dismantle session information
     */
    public void disconnect(boolean preserveSession) {
    	RemoteToolProxy.sendInterfaceForceDisconnectMsg(this);
		if (getRemoteProxy() instanceof SocketProxy)
			((SocketProxy) getRemoteProxy()).extCloseConnection(preserveSession);
	}
    
    // GETTERS/SETTERS

	/**
	 * @return {@link #getLauncher()}.{@link CTAT_Launcher#getApplet()}
	 */
	public Applet getApplet() {
		if(getLauncher() == null)
			return null;
		return getLauncher().getApplet();
	}
   
    public void setDeletePrFile(boolean b){
        this.deletePrFile = b;
    }
    
    public boolean isDeletePrFile(){
        return deletePrFile;
    }

    public boolean isClArgumentSetToProtectProdRules() {
        return this.clArgumentSetToProtectProdRules;
    }

    public void setClArgumentSetToProtectProdRules(boolean b) {
        this.clArgumentSetToProtectProdRules = b;
    }
    
    public void setHomeDir(String hd){
    	homeDir = hd;
    }
    
    public String getHomeDir(){
    	return homeDir;
    }

    /**   */
    public AplusController getAmt() {
    	MissControllerExternal mc = getMissController();
    	if(mc != null && mc.getSimSt() != null
    			&& mc.getSimSt().isSsMetaTutorMode() && amt == null) {
    		amt = new AplusController(this);
    	}
    	return amt;
    }
    
    public LoggingSupport getLoggingSupport(){
    	return getTSLauncherServer().getLoggingSupport();
    }
    
    public LoggingSupport getLogger(){
    	return getTSLauncherServer().getLoggingSupport();
    }	
    
    public void setBehaviorRecorderMode(String mode) {
    	getCtatModeModel().setMode(mode);
    }
    
    public String getBehaviorRecorderMode() {
        return getCtatModeModel().getCurrentMode();
    }
	
    /**
     * Returns the {@link #JGraphPanel} for the tab.
     */
	@Override
	public JGraphPanel getJGraphWindow() {
		return this.graphPanel;
	}
	
	public JAbstractUndoPacket getUndoPacket() {
		return this.abstractUndoPacket;
	}
	
	public String saveBRDSilently() {
    	ProblemModel pm = getProblemModel();
        String filename = getProblemStateWriter().saveBRDFile(pm.getProblemFullName());
        if (filename != null)  // success
        	updateStatusPanel(pm.getProblemFullName());

        return filename;
	}
	
	/**
	 * @param node
	 * @return		true if some problem model loaded by the controller contains the given
	 * 				node; false otherwise 
	 */
	public boolean hasLoadedNode(ProblemNode node) {
		return (getProblemModel().getProblemGraph().isNodeInGraph(node));
	}
	
	private CTATTabManager getTabManager() {
		return getLauncher().getTabManager();
	}

    /**
     * @param ssm new value for {@link #solutionState}
     */
	public void setSolutionState(SolutionState ssm) {
        solutionState = ssm;
    }
	
	/**
	 * Used for instances where the mouse handler doesn't seem to work (e.g., clicking directly
	 * on the graph).
	 * Is there a better way to do this?
	 */
	public void comeIntoFocus() {
		getServer().getTabManager().updateIfNewTabFocus(this.tabNumber);
	}
	
    /* ************************** STATIC METHODS ************************** */

	/**
	 * Stores the problem node to copy from when using copy/paste.
	 * @param pm
	 */
	public static ProblemNode getCopySubgraphNode() {
        return _copySubgraphNode;
    }
	
	/**
	 * Retrieves the problem node to copy from when using copy/paste.
	 * @param pm
	 */
    public static void setCopySubgraphNode(ProblemNode thisNode) {
        _copySubgraphNode = thisNode;
    }
    
    public int getTabNumber() {
    	return this.tabNumber;
    }

	public CTAT_Launcher getServer() {	// FIXME: MAKE THIS PRIVATE LATER
        if (trace.getDebugCode("br"))
        	trace.out("br", "BR_C.getServer(): launcher is "+trace.nh(launcher)+
        		", to return "+(launcher == null ? null : trace.nh(launcher.getCTATLauncher())));
		
		if(launcher == null)
			return null;

		return launcher.getCTATLauncher();
	}
	
	public int copySelectedLinks() {
		return this.problemModelManager.copySelectedLinks();
	}
	
	public void pasteLinks() {
		this.problemModelManager.pasteLinks(); // CHANGE THIS
	}

	/**
	 * Send a {@link ChangeEvent} to all {@link #changeListeners}. The listener list is maintained
	 * here, not in {@link UniversalToolProxy}, because instances of that class may not survive as
	 * the communications link to the student interface is broken and reconnected.
	 * @param source for {@link ChangeEvent#getSource()} 
	 */
    public void fireChangeEvent(Object source) {
    	ChangeEvent ce = new ChangeEvent(source);
    	for (ChangeListener listener : changeListeners) {
    		listener.stateChanged(ce);
    	}
	}
    
    /**
     * The listener list is maintained here, not in {@link UniversalToolProxy},
	 * because instances of that class may not survive as
	 * the communications link to the student interface is broken and reconnected.
     * @param listener listener to add to {@link #changeListeners}
     */
    public void addChangeListener(ChangeListener listener) {
    	changeListeners.add(listener);
    }
    
    /**
     * @param listener listener to remove from {@link #changeListeners}
     */
    public void removeChangeListener(ChangeListener listener) {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "BR_Controller.removeChangeListener("+trace.nh(listener)+")");
    	changeListeners.remove(listener);
    }

    /**
     * @return current view port on the graph
     */
	public Rectangle getGraphViewPortBounds() {
		JGraphPanel gpw = getJGraphWindow();
		if(gpw != null)
			return gpw.getGraphViewPortBounds();
		return null;
	}

	/**
	 * Update the student interface connection status indicator.
	 * @param e connection change event from {@link #utp}
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
        if (!Utils.isRuntime()) {
        	StudentInterfaceConnectionStatus sics = (utp == null ?
        			StudentInterfaceConnectionStatus.Disconnected : utp.getStudentInterfaceConnectionStatus());
            if(getServer().isDoneIntializing()) {
            	getServer().getDockManager().updateGraphConnectionStatus(getTabNumber(), sics);
            }
		}		
	}
}