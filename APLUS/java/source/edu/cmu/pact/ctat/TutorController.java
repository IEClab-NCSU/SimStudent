package edu.cmu.pact.ctat;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jdom.Element;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommComposer;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.StudentInterfaceConnectionStatus;
import pact.CommWidgets.StudentInterfaceWrapper;
import pact.CommWidgets.UniversalToolProxy;
import pact.CommWidgets.WrapperSupport;
import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Properties;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.JGraphPanel;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.jess.MT;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pslc.logging.LogContext;

public class TutorController extends CTATBase implements CTAT_Controller
{	
	/** Key for Default Projects Folder preference. */
	public static final String PROJECTS_DIRECTORY = "Projects Directory";

	/** The container through which we interact with a custom Java student interface. */
    protected StudentInterfaceWrapper studentInterface = null;

	protected Hashtable<String, JCommWidget> commNameTable = new Hashtable<String, JCommWidget>();
    protected boolean startStateInterface;
    protected boolean startFindWidgetsForProblem;
    protected Vector<String> notFoundWidgetsForProblem;
    protected boolean toolTipsInitialized;
    protected UniversalToolProxy utp;
    public static final String NOT_DONE_MSG = "I'm sorry, but you are not done yet. Please continue working.";
    public static final String NOT_ALLOW_DONE_HINTS_MSG = "You can not get Done Hints now.";
    protected Set<Object> unmatchedComponents = new LinkedHashSet<Object>();
    protected HintMessagesManager hintMessagesManager;
    
    /** Whether feedback to the student should be suppressed (true) or allowed (false). */
	private boolean suppressStudentFeedback = false;

	/** Link to the top-level student runtime object. */
	private TSLauncherServer tsLauncherServer;
	
	private String runType = "";

    public String getRunType() {
		return runType;
	}

	public void setRunType(String runType) {
		this.runType = runType;
	}

	/** System properties to check in {@link #loadControlFromSystemProperties()}. */
    private static final String[] systemPropertiesToCheck = {
		Logger.DISK_LOG_DIR_PROPERTY,
		Logger.AUTH_TOKEN_PROPERTY,
		Logger.SESSION_ID_PROPERTY,
		Logger.LOG_SERVICE_URL_PROPERTY,
        Logger.STUDENT_NAME_PROPERTY,
		Logger.SCHOOL_NAME_PROPERTY,
		Logger.COURSE_NAME_PROPERTY,
		Logger.UNIT_NAME_PROPERTY,
		Logger.SECTION_NAME_PROPERTY,
		Logger.ENABLE_AUTHOR_LOGGING,
		Logger.STUDY_CONDITION_NAME 
    };

    public TutorController(TSLauncherServer tsLauncherServer)
    {
    	this.tsLauncherServer = tsLauncherServer;
    	setClassName ("TutorController");
    	debug ("TutorController ()");
    }
    
    /**
     * Constructor for {@link TutorMessageDisplay}, where we do not get a value for
     * {@link #authorComponent}.
     * @param lc
     * @param studentInterface
     */
    public TutorController(LogContext lc, StudentInterfaceWrapper studentInterface)
    {
    	this.studentInterface = studentInterface;
    	// this.authorComponent = null;  
    	if (utp != null && !Utils.isRuntime()) {
    		if (studentInterface == null)
    			utp.setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.Disconnected);
    		else
    			utp.setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.NewlyConnected);
    	}
    	getTSLauncherServer().setLogger(lc);
    }
    
	public PreferencesModel getPreferencesModel() 
	{
		// is this for a problem model or the controller? looks like the latter...
		if(getTSLauncherServer() == null)
			return null;
		return getTSLauncherServer().getPreferencesModel();
    }
    public CTAT_Properties getProperties()
    {
    	return getTSLauncherServer().getProperties();
    }

    /**
     * Method for startup to set parameters and control info from system
     * properties. For each element of {@link #systemPropertiesToCheck}, calls
     * {@link #properties}.{@link CTAT_Properties#setProperty(String, Object) setProperty()}.
     */
    public void loadControlFromSystemProperties() {
    	if (inAppletMode())
    		return;
    	for (String prop : systemPropertiesToCheck)
    		getProperties().setProperty(prop, System.getProperty(prop));
    }

    public void saveTraversedPathFile()
    {
    	
    }
    
    /**
     * @return false (always, in this superclass implementation
     */
    public boolean inAppletMode() {
    	return false;
    }
        
    public HintMessagesManager getHintMessagesManager() {
        //siw.getwrappersupport.gethintmessagesmanager().
    	if (hintMessagesManager == null){
            hintMessagesManager = new HintMessagesManagerForClient(this);
            if(studentInterface != null)
            	if(studentInterface.getWrapperSupport()!=null)
            		studentInterface.getWrapperSupport().setController(this);
            		
    	}
    	return hintMessagesManager;
    }
    public boolean shouldDisplayWarnings(){
    	return false; //lojas... something smarter    
    }
    
    /**
     * Record a reference to the current Java student interface. Also calls
     * {@link UniversalToolProxy#setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus)}
     * with status {@value StudentInterfaceConnectionStatus#NewlyConnected}.
     * @param currentInterface container through which we control the interface
     */
    public void setStudentInterface(StudentInterfaceWrapper currentInterface){
    	this.studentInterface = currentInterface;
        if (utp != null && !Utils.isRuntime()) {
        	utp.setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.NewlyConnected);
        }
    }
    
    public void addCtatModeListener(Object noOp){
    	return;
    }

    public edu.cmu.pact.ctat.MessageObject getOriginalStartStateNodeMessage(Vector selection, Vector action){
    	return null;
    }
	public void setStartStateModified(boolean noOp){
		return;
	}
    public LoggingSupport getLoggingSupport() {
    	return null;
	}
    
    public EventLogger getEventLogger() {
    	return getTSLauncherServer().getEventLogger();
    }
	
    public LogContext getLogger(){
    	return getTSLauncherServer().getLogger();
    }
    // ////////////////////////////////////////////////////
    /**
     * Process a comm message
     * 
     * @param controller
     */
    public AbstractCtatWindow getActiveWindow(){
    	return null;
    }
    public void handleCommMessage(edu.cmu.pact.ctat.MessageObject mo) {
    	return;
    }
    public boolean isShowWidgetInfo(){
    	return false;
    }
    public boolean isDemonstratingSolution(){
    	return false;
    }
    public boolean isStartStateInterface(){
    	return false;
    }
    public boolean isDefiningStartState(){
    	return false;
    }
    public boolean isStartStateModified(){
    	return false;
    }
    // ////////////////////////////////////////////////////
    //BORG: Edit. changed handlecommmessage to not take a controller as an argument since that is kinda silly.
    //Why would you have a function in a class take an instance of that class as an argument.
    //Only called by handlecommmessage in utp..
    public void handleCommMessage_movedFromCommWidget(edu.cmu.pact.ctat.MessageObject mo) {
        Vector propertyNames = null;
        Vector propertyValues = null;
        if (trace.getDebugCode("msg")) trace.out("msg", "msg to student interface:\n"+mo);
        


        try {
            propertyNames = new Vector(mo.getPropertyNames());
            propertyValues = new Vector(mo.getPropertyValues());
            String type = (String) MessageObject.getValue(propertyNames, propertyValues,
                    "MessageType");

            if (trace.getDebugCode("dw")) trace.out("dw", "property names = " + new ArrayList(propertyNames));
            if (trace.getDebugCode("dw")) trace.out("dw", "property values = "
                    + new ArrayList(propertyValues));
            
            if (type.equalsIgnoreCase("ConfirmDone")) {
            	doConfirmDone();
            	return;
            }

            if (type.equalsIgnoreCase("ResetAction")) {
                doResetAction_movedFromCommWidget(propertyNames,
                        propertyValues);
                return;
            }

            if (type.equalsIgnoreCase("InterfaceDescription")) {
                handleInterfaceDescriptionMessage_movedFromCommWidget(
                        mo);
                return;
            }

            if (type.equalsIgnoreCase("InterfaceAction")) {
                doInterfaceAction_movedFromCommWidget(propertyNames,
                        propertyValues);
                return;
            }
            if (type.equalsIgnoreCase("StartProblem")) {
                doStartProblem_movedFromCommWidget();
                return;
            }

            if (type.equalsIgnoreCase("StartStateEnd")) {
                doStartStateEnd_movedFromCommWidget();
                return;
            }

            if (type.equalsIgnoreCase("StartNewProblem")) {
                doStartNewProblem_movedFromCommWidget();
                return;
            }

            
            if (type.equalsIgnoreCase("CorrectAction")) {
            	if(getRunType() == "")
            		doCorrectAction_movedFromCommWidget(propertyNames,propertyValues);
            	return;
            }
            if (type.equalsIgnoreCase("IncorrectAction")) {
            	if(getRunType() == "")
            		doIncorrectAction_movedFromCommWidget(propertyNames,
            			propertyValues);
            	return;
            }
            
            
            if (type.equalsIgnoreCase("LISPCheckAction")) {
            	if(getRunType() == "")
            		doLispCheckAction_movedFromCommWidget(propertyNames,propertyValues);
                return;
            }
            if (type.equalsIgnoreCase("StateGraph")) {
            	doStateGraph(propertyNames,propertyValues);
                return;
            }
            if(type.equalsIgnoreCase("WrongUserMessage")){
            	doIncorrectAction_movedFromCommWidget(propertyNames,propertyValues);
            	return;
            }
            if (type.equalsIgnoreCase("UnlockComposer")) {
                doUnlockComposer_movedFromCommWidget(propertyNames,
                        propertyValues);
                return;
            }
            if (type.equalsIgnoreCase(MsgType.INTERFACE_REBOOT)) {
            	doInterfaceReboot();
            	return;
            }
            if (type.equalsIgnoreCase(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS)) {
            	doGetAllInterfaceDescriptions();
            	return;
            }
            if(type.equalsIgnoreCase(MsgType.SEND_WIDGET_LOCK)) {
            	doSendWidgetLock(mo);
            	return;
            }

        } catch (IllegalArgumentException ie) {
            Vector selection = (Vector) MessageObject.getValue(propertyNames, propertyValues,
            		"selection");
            if (selection != null && selection.size() > 0) {
            	unmatchedComponents.add(selection.get(0));
            }  // defer warning user until we have all unmatchedComponents from the start state
            return;
        } catch (Exception e) {
            trace.err("Error handleCommMessage_movedFromCommWidget("+
            		mo +"): " + e);
            e.printStackTrace(System.err);
            return;
        }
        trace.out("**ERROR**: don't know type "
                + (String) propertyNames.elementAt(0));
    }

    private void doSendWidgetLock(MessageObject mo) {
    	Boolean lock = mo.getPropertyAsBoolean(MsgType.WIDGET_LOCK_FLAG); // FIXME
		for(JCommWidget w : getCommWidgetTable().values())
			w.isChangedFromResetState();
	}

	/**
     * Restore all widget settings to their original compile-time settings.
     */
    private void doInterfaceReboot() {
		
		UniversalToolProxy utp = getUniversalToolProxy();
		if(utp == null)
			return;

		String CommComboBoxClassName = pact.CommWidgets.JCommComboBox.class.getName();
		String CommComposerClassName = pact.CommWidgets.JCommComposer.class.getName();

		Map<String, JCommWidget> widgetTable = getCommWidgetTable();
		for(String wName : widgetTable.keySet()) {
			JCommWidget w = widgetTable.get(wName);
			
			w.reset(this);            // try to restore compile-time settings

			String className = w.getClass().getName();  // sewall 2014-01-20 preserved old exception
			if (className.equalsIgnoreCase(CommComboBoxClassName)
					|| className.equalsIgnoreCase(CommComposerClassName))
				w.setEditable(false);
		}
	}

	/**
     * Process problem-wide settings from the stateGraph message.
     * @param propertyNames
     * @param propertyValues
     */
    protected void doStateGraph(Vector propertyNames, Vector propertyValues) {
    	WrapperSupport ws = (getStudentInterface() == null ? null : getStudentInterface().getWrapperSupport()); 

    	Object ssf = MessageObject.getValue(propertyNames, propertyValues, "suppressStudentFeedback");
    	if (ssf != null && ws != null) {
    		suppressStudentFeedback = Boolean.parseBoolean(ssf.toString());
    		ws.suppressFeedback(suppressStudentFeedback);
    	}
	}

    /**
     * Display a dialog for the student to confirm that he or she is done
     * with this problem. For use when student feedback is suppressed,
     * so that the student can't tell when all steps are finished correctly.
     */
    protected void doConfirmDone() {
		String message = "Are you finished with this problem?";
        String title = "Confirm";
        int result = JOptionPane.showConfirmDialog(getStudentInterface().getActiveWindow(),
        		message, title, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION)
            processDoneMatch();
        else {  // CTAT2833: log student "no" response to Confirm Done
        	MessageObject mo = createConfirmDoneMessage(result);
        	LoggingSupport loggingSupport = getLoggingSupport();
        	if (loggingSupport != null)
        		loggingSupport.oliLog(mo, false);  // false => tool_message
        }
    }
    
    /**
     * Generate a message to record the student's response to Confirm Done. 
     * @return {@link MsgType#INTERFACE_ACTION} message
     */
    private MessageObject createConfirmDoneMessage(int optionPaneResult) {
    	MessageObject mo = MessageObject.create(MsgType.INTERFACE_ACTION, "NotePropertySet");

        mo.setSelection(MsgType.CONFIRM_DONE);  // dummy selection
        mo.setAction(JCommButton.BUTTON_PRESSED);
        mo.setInput(optionPaneResult == JOptionPane.YES_OPTION ? "yes" : "no");

    	mo.setTransactionId(mo.makeTransactionId());
    	return mo;
	}

	/**
     * Special treatment for the 'Done" match: advance the problem.
     */
    /**Borg: there is a copy of this code in br_Controller that has 
     * br_controller specific functionallity. 
     */
   private void processDoneMatch() {
        trace.out ("inter", "process done match");
        HintWindowInterface hwi = null;
		StudentInterfaceWrapper siw = getStudentInterface();
        // here if not logged in to LMS: try done listeners
		if (siw != null) {
			WrapperSupport ws = siw.getWrapperSupport();
			if (ws != null)
				ws.doneActionPerformed();
			if (hwi != null)
				hwi.reset();
		}
    }
    public StudentInterfaceWrapper getStudentInterface() {
        return studentInterface;
    }

    /**
	 * @return the {@link #authorComponent}
	 */
	public JComponent getTutorPanel() {
		if(getStudentInterface() == null)
			return null;
		if(getStudentInterface().getWrapperSupport() == null)
			return null;
		return getStudentInterface().getWrapperSupport().getTutorPanel();
	}

	private void doResetAction_movedFromCommWidget(Vector propertyNames, Vector propertyValues) {
        Vector selection = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "selection");
        String widgetName;
        for (int i = 0; i < selection.size(); i++) {
            widgetName = (String) selection.elementAt(i);
            JCommWidget d = getCommWidget(widgetName);
            if (d == null) {
                trace.out(5, "JCommWidget.java",
                        "Error: can't find selection called "
                                + selection.elementAt(i));
                continue;
            }
            final String CommTableClassName = "pact.CommWidgets.JCommTable";
            if (d.getClass().getName().equalsIgnoreCase(CommTableClassName))
                ((JCommTable) d).singleCellReset(widgetName);
            else
                d.reset(this);
        }
    }

    public void doStartProblem_movedFromCommWidget() {

        StudentInterfaceWrapper stInterface = getStudentInterface(); 
        if (stInterface != null && stInterface.getHintInterface() != null) {
            stInterface.getHintInterface().reset();
		}
        resetAllWidgets();
        setStartStateInterface(true);
        // set JCommComboBox non-editable
        Iterator<JCommWidget> e = getCommWidgetTable().values().iterator();
        String CommComboBoxClassName = "pact.CommWidgets.JCommComboBox";
        String CommComposerClassName = "pact.CommWidgets.JCommComposer";
        String className = "";
        while (e.hasNext()) {
            JCommWidget w = e.next();
            className = w.getClass().getName();
            if (className.equalsIgnoreCase(CommComboBoxClassName)
                    || className.equalsIgnoreCase(CommComposerClassName))
            	w.setEditable(false);
        }
        setStartFindWidgetsForProblem(true);
        setNotFoundWidgetsForProblem(new Vector<String>());
    }

    protected void doInterfaceAction_movedFromCommWidget(Vector propertyNames,
            Vector propertyValues) {
        
        Vector selection = (Vector) MessageObject.getValue(propertyNames, propertyValues, "selection");
        Vector action = (Vector) MessageObject.getValue(propertyNames, propertyValues, "action");
        Vector input = (Vector) MessageObject.getValue(propertyNames, propertyValues, "input");

        boolean oneWidgetFound = false;
        if (trace.getDebugCode("inter")) trace.out("inter", "doInterfaceAction_mov... selection "+selection);
        //if(selection == null) trace.out("mg", "TutorController (doInterfaceAction_movedFromCommWidget): null selection");
        /* ******************************* CHANGED 06/10/2013 ******************************* */
        /* edited to add check for null */
        if(selection != null) {
        	//trace.out("mg", "TutorController (doInterfaceAction_movedFromCommWidget): non-null selection");
            for (int i = 0; i < selection.size(); i++) {
                JCommWidget d = getCommWidget((String) selection.elementAt(i));
                if (trace.getDebugCode("inter")) trace.out("inter", "doInterfaceAction_mov... d["+i+"] "+d) ;
                if (d == null) continue;
                oneWidgetFound = true;
                d.doInterfaceAction((String) selection.elementAt(i), (String) action.elementAt(i),
                		input.elementAt(i) == null ? null : input.elementAt(i).toString());
            }
            
            if (!oneWidgetFound) {
                throw new IllegalArgumentException("Could not find widgets for selection: "	+ selection);
            }
        }
    }

    protected void handleInterfaceDescriptionMessage_movedFromCommWidget(
            Vector propertyNames, Vector propertyValues) {
        String tempCommName = (String) MessageObject.getValue(propertyNames,
                propertyValues, "CommName");
        if (tempCommName == null || tempCommName == "")
            return;
        JCommWidget d = getCommWidget(
                tempCommName);
        if (d == null)
            return;
//        d.doInterfaceDescription(propertyNames, propertyValues);
    }
    
    protected void handleInterfaceDescriptionMessage_movedFromCommWidget(
            edu.cmu.pact.ctat.MessageObject messageObject) {
        String tempCommName = (String) messageObject.getProperty("CommName");
        
        if (tempCommName == null || tempCommName == "")
            return;
        JCommWidget d = getCommWidget(
                tempCommName);
        if (d == null)
            return;
        d.doInterfaceDescription(messageObject);
    }

    private void doCorrectAction_movedFromCommWidget(Vector propertyNames,
            Vector propertyValues) {
        Vector selection = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "selection");
        Vector action = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "action");
        Vector input = (Vector) MessageObject.getValue(propertyNames, propertyValues, "input");
        if (selection.elementAt(0) == null)
            trace.printStack("mps");
   
        
        for (int i = 0; i < selection.size(); i++) {
            JCommWidget d = getCommWidget(
                    (String) selection.elementAt(i));
            if (d == null) {
                /*
                 * trace.out( 5, "JCommWidget.java", "Error: can't find
                 * selection called " + selection.elementAt(i));
                 */
                continue;
            }
                    	
			d.doCorrectAction((String) selection.elementAt(i), (String) action.elementAt(i), 
					input.elementAt(i) == null ? null : input.elementAt(i).toString());
        }
    }

    public void doLispCheckAction_movedFromCommWidget(Vector propertyNames,
            Vector propertyValues) {
        Vector selection = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "selection");
        Vector action = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "action");
        Vector input = (Vector) MessageObject.getValue(propertyNames, propertyValues, "input");
        int n = Math.min(selection.size(), input.size());
        for (int i = 0; i < n; i++) {
            JCommWidget d = getCommWidget(
                    (String) selection.elementAt(i));
            if (d == null) {
                trace.out("Error: can't find selection called "
                        + selection.elementAt(i));
                continue;
            }
            if (d instanceof JCommComposer && i < action.size())
               d.doLISPCheckAction((String) selection.elementAt(i), (String) action.elementAt(i), (String) input
                    .elementAt(i));
            else
        	   d.doLISPCheckAction((String) selection.elementAt(i), (String) input.elementAt(i));
        }
    }

    public void doIncorrectAction_movedFromCommWidget(Vector propertyNames,
            Vector propertyValues) {
        Vector selection = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "selection");
        Vector action = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "action");
        Vector input = (Vector) MessageObject.getValue(propertyNames, propertyValues, "Input");
        for (int i = 0; i < selection.size(); i++) {
            JCommWidget d = getCommWidget(
                    (String) selection.elementAt(i));
            if (d == null) {
                trace.out("Error: can't find selection called "
                        + selection.elementAt(i));
                continue;
            }
            d.doIncorrectAction((String) selection.elementAt(i), (String) action.elementAt(i),
            		input.elementAt(i) == null ? null : input.elementAt(i).toString());
        }
    }
   
  
    public void doUnlockComposer_movedFromCommWidget(Vector propertyNames,
            Vector propertyValues) {
        Vector selection = (Vector) MessageObject.getValue(propertyNames, propertyValues,
                "selection");
        for (int i = 0; i < selection.size(); i++) {
            JCommWidget d = getCommWidget(
                    (String) selection.elementAt(i));
            if (d == null) {
                trace.out("Error: can't find selection called "
                        + selection.elementAt(i));
                continue;
            }
            if (d.actionName.equalsIgnoreCase("UpdateComposer"))
                d.unlockWidget();
        }
    }

    
    //this will always be null for tutorcontrolller.
    // ////////////////////////////////////////////////////
    /**
     * Called by TutorWindow when the window is created.
     */
    // ////////////////////////////////////////////////////
    public void initAllWidgets_movedFromCommWidget() {
        if (utp == null)
            throw new NullPointerException();
        if (!isToolTipsInitialized()) {
            ToolTipManager t = ToolTipManager.sharedInstance();
            t.setInitialDelay(100);
            t.setReshowDelay(100);
            setToolTipsInitialized(true);
        }
        for (Iterator<JCommWidget> i = getCommWidgetTable().values().iterator(); i.hasNext();) {
            JCommWidget d = i.next();
            d.initialize(this);
            d.addMouseListener(getStudentInterface());
        }
    }

    public void doStartNewProblem_movedFromCommWidget() {
    	if(getStudentInterface() != null) {
        	HintWindowInterface hintInterface = getStudentInterface().getHintInterface();
            if (hintInterface != null) 
                hintInterface.reset();
    	}
        resetAllWidgets();
        setStartStateInterface(true);
        setStartStateSent(false);
        // set JCommComboBox editable
        Iterator<JCommWidget> i = getCommWidgetTable().values().iterator();
        String CommComboBoxClassName = "pact.CommWidgets.JCommComboBox";
        String CommComposerClassName = "pact.CommWidgets.JCommComposer";
        String className = "";
        while (i.hasNext()) {
            JCommWidget w = i.next();
            className = w.getClass().getName();

            if (className.equalsIgnoreCase(CommComboBoxClassName))
                ((JCommComboBox) w).setInitialValues();

            if (className.equalsIgnoreCase(CommComboBoxClassName)
                    || className.equalsIgnoreCase(CommComposerClassName))
                w.setEditable(true);
        }
    	/*
    	HintWindowInterface hintInterface = getStudentInterface().getHintInterface();
        if (hintInterface != null) 
            hintInterface.reset();
        resetAllWidgets();
        setStartStateInterface(true);
        setStartStateSent(false);
        // set JCommComboBox editable
        Iterator<JCommWidget> i = getCommWidgetTable().values().iterator();
        String CommComboBoxClassName = "pact.CommWidgets.JCommComboBox";
        String CommComposerClassName = "pact.CommWidgets.JCommComposer";
        String className = "";
        while (i.hasNext()) {
            JCommWidget w = i.next();
            className = w.getClass().getName();

            if (className.equalsIgnoreCase(CommComboBoxClassName))
                ((JCommComboBox) w).setInitialValues();

            if (className.equalsIgnoreCase(CommComboBoxClassName)
                    || className.equalsIgnoreCase(CommComposerClassName))
                w.setEditable(true);
        }
        */
    }

    //This method is overridden by br_controller (the commented lines are executed)
    public void doStartStateEnd_movedFromCommWidget() {
        setStartStateInterface(false);
        setStartStateSent(true);
        // display message
        // mfeng@wpi: add a condition:
        // UniversalToolProxy.getMode().compareTo("Tutor") == 0
        // so that the warning msg shows up only when we run problems in
        // tutor mode
        // if (universalToolProxy.getMode().compareTo("Tutor") == 0 &&
        // startFindWidgetsForProblem
        setStartFindWidgetsForProblem(false);
        /*if (invalidWidgetsExist()) {
            trace.printStack("lll");
            WidgetsNotFoundDialog d = new WidgetsNotFoundDialog(controller);
            d.setWidgetNamesList(getNotFoundWidgetsForProblem());
            d.setVisible(true);
        }*/
    }

	/**
	 * Handle an {@value MsgType#GET_ALL_INTERFACE_DESCRIPTIONS} message: send each component's
	 * {@link MsgType#INTERFACE_DESCRIPTION} message. 
	 */
	public void doGetAllInterfaceDescriptions() {

		Map<String, JCommWidget> widgetTable = getCommWidgetTable();
		for(String wName : widgetTable.keySet()) {
			JCommWidget w = widgetTable.get(wName);

			MessageObject o = w.getDescriptionMessage(wName);
			if (o == null) {
				// Following diagnostic is ok for JCommTable, each cell of
				// which is in widgetEntries.
				if (trace.getDebugCode("comm")) trace.out("comm", "Can't create message for " + wName +
						" because can't initialize (need to set comm name?)");
			} else
				bundleMessage(o, MsgType.GET_ALL_INTERFACE_DESCRIPTIONS);
			
			if(w.isHintBtn())  // skip start state interface action from hint button: warns in demo mode
				continue;
			if(w.isDoneButton())  // skip start state interface action from done button, too
				continue;

			if (w.isChangedFromResetState()) {
				Vector<MessageObject> messageVector = w.getCurrentState();
				for (int j = 0; j < messageVector.size(); j++) {
					o = messageVector.elementAt(j);
					if(o != null)
						bundleMessage(o, MsgType.GET_ALL_INTERFACE_DESCRIPTIONS);
					if (trace.getDebugCode("startstate"))
						trace.out ("startstate", "UTP.doGetAllInterfaceDescriptions() widget "+
								w.getCommName()+", currentState "+o.summary());
				}
			}
		}
		flushBundle(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS);
	}

	/** Message bundles in progress for <i>inbound</i> calls to {@link #handleCommMessage(MessageObject)}. */
	private Map<String, List<Element>> bundles = new HashMap<String, List<Element>>();
	
	/**
	 * Append to a bundle of <i>inbound</i> messages to be sent to CTAT. This is not the same as
	 * code meant to bundle <i>outbound</i> messages in, e.g. {@link RemoteToolProxy}. 
	 * @param mo message to append
	 * @param bundleName key to {@link #bundles}
	 * @return count of messages in bundle
	 */
	private int bundleMessage(MessageObject mo, String bundleName) {
		List<Element> bundle = bundles.get(bundleName);
		if (bundle == null)                             // start a new bundle
			bundle = new LinkedList<Element>();
		bundle.add(mo.toElement());
		bundles.put(bundleName, bundle);
		return bundle.size();
	}

	/**
	 * Bind all elements in the named bundle into a message with {@link MessageObject#getMessageType()}
	 * is the bundle name, send to {@link #handleCommMessage(MessageObject)} and delete the bundle.
	 * @param bundleName
	 * @return count of messages in bundle
	 */
	private int flushBundle(String bundleName) {
		List<Element> bundle = bundles.remove(bundleName);
		if (bundle == null || bundle.size() < 1)
			return 0;
		MessageObject mo = MessageObject.create(bundleName, "NotePropertySet");
		mo.setProperty("messages", bundle);
		handleCommMessage(mo);
		return bundle.size();
	}

	/**
	 * Called when the last {@value MsgType#INTERFACE_DESCRIPTION} message has been received
	 * when author is about to edit the start state.  No-op in superclass.
	 */
    public void finishedReceivingInterfaceStartState() { }

	/**
     * 
     */
    public JCommWidget getCommWidget(String commName) {
    	// trace.out(5, "JCommWidget.java", "commName =" + commName);
    	// zz add 11/06/03: deal with the same commName IgnoreCase

    	// chc added 12/13/06 to allow Author create new Blank state with "No_Selection".
    	if (commName.equals("No_Selection")) return null;

    	Enumeration<String> iterKeys = getCommWidgetTable().keys();
    	String keyName;
    	while (iterKeys.hasMoreElements()) {
    		keyName = iterKeys.nextElement();
    		if (keyName.equalsIgnoreCase(commName)) {

    			JCommWidget t = commNameTable.get(keyName);
    			if (t != null)
    				return t;

    			break;
    		}
    	}

    	if (getStartFindWidgetsForProblem())
    		getNotFoundWidgetsForProblem().addElement(commName);
    	else if (getInterfaceLoaded()) {
    		try {
    			throw new RuntimeException("Request for unknown component "+commName);
    		} catch (RuntimeException re) {
    	    	if ("null".equalsIgnoreCase(commName)) {
    	    		if (trace.getDebugCode("dw"))
    	    			trace.printStack("dw", "TC.getCommWidget(): request for \"null\" component");
    	    	} else
    	    		re.printStackTrace();
    		}
    	}
    	return null;
    }
    
    
    /**
     * Returns Comm Widget that has "name" as its commName
     * 
     * @param The
     *            name to search
     * @return A comm widget with the same name
     */
    public Object lookupWidgetByName(String name) {
        for (Iterator<JCommWidget> i = getCommWidgetTable().values().iterator(); i.hasNext();) {
            JCommWidget widget = i.next();
            String widgetName = widget.getCommName();
            String className = widget.getClass().getName();
           // trace.err(className);
            if (widgetName.equalsIgnoreCase(name)) {
            	//trace.out("foagetter", "lookupWidgetByName/widget: returning an object of type " + widget.getClass());
                return widget;

            } else if (className.equalsIgnoreCase("pact.CommWidgets.JCommTable") || className.equalsIgnoreCase("pact.DorminWidgets.DorminTable")) {
                int rows = ((JCommTable) widget).getRows();
                int columns = ((JCommTable) widget).getColumns();

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < columns; c++) {
                        JCommTable.TableCell cell = ((JCommTable) widget).getCell(r, c);
                        if (cell.getCommName().equalsIgnoreCase(name)) {
                     
                        	if (trace.getDebugCode("boots20")) trace.out("boots20","lookupWidgetByName/cell: returning an object of type " + cell.getClass());
                            return cell;
                        }
                    }
                }
            }
        }
        new Exception("Invalid widget name: " + name).printStackTrace();
        return null;
    }

    // ////////////////////////////////////////////////////
    /**
     * Called by TutorWindow when the window is created.
     */
    // ////////////////////////////////////////////////////
    public void removeAllHighlights() {
        for (Iterator<JCommWidget> i = getCommWidgetTable().values().iterator(); i.hasNext(); ) {
            JCommWidget d = i.next();
            d.removeHighlight("");
        }
    }

//    public Vector getCommWidgetVector() {
//        return commWidgetVector;
//    }


    public Hashtable<String, JCommWidget> getCommWidgetTable() {
        // commNameTable is set by CommWidgets.initialize(), which is called
        // when a new commWidget is created
        return commNameTable;
    }

    // ////////////////////////////////////////////////////
    /**
     * Set all widgets to the reset state
     * 
     * @param controller2
     */
    // ////////////////////////////////////////////////////
    public void resetAllWidgets() {

        ArrayList<JCommWidget> widgets = new ArrayList<JCommWidget>(getCommWidgetTable().values());
        for (int i = 0; i < widgets.size(); i++) {
            JCommWidget w = widgets.get(i);
            w.reset(this);
        }
    }


    protected void clearWidgetInformation() {

        trace.printStack ("inter", "CLEAR WIDGET TABLE");

        getCommWidgetTable().clear();
    }
    
    /**
     * @param startStateInterface
     *            The startStateInterface to set.
     */
    protected void setStartStateInterface(boolean startStateInterface) {
        this.startStateInterface = startStateInterface;
    }
    protected void setStartFindWidgetsForProblem(boolean startFindWidgetsForProblem) {
        this.startFindWidgetsForProblem = startFindWidgetsForProblem;
        // if (startFindWidgetsForProblem == true)
        // trace.printStackWithStatement("find widgets for problem");
    }
    protected boolean getStartFindWidgetsForProblem() {
        return this.startFindWidgetsForProblem;
    }

    public void setNotFoundWidgetsForProblem(Vector<String> notFoundWidgetsForProblem) {
        this.notFoundWidgetsForProblem = notFoundWidgetsForProblem;
    }

    public Vector<String> getNotFoundWidgetsForProblem() {
        if (notFoundWidgetsForProblem == null)
            this.notFoundWidgetsForProblem = new Vector<String>();
        return this.notFoundWidgetsForProblem;
    }
    /**
     * @return Returns the toolTipsInitialized.
     */
    public boolean isToolTipsInitialized() {
        return toolTipsInitialized;
    }
    /**
     * @param toolTipsInitialized The toolTipsInitialized to set.
     */
    public void setToolTipsInitialized(boolean toolTipsInitialized) {
        this.toolTipsInitialized = toolTipsInitialized;
    }
    
    /**
     * @param startStateSent
     *            The startStateSent to set.
     */
    public void setStartStateSent(boolean startStateSent) {
    	//this.startStateSent = startStateSent;//noop... br_controller needs override.
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
    public boolean invalidWidgetsExist() {
        return getStartFindWidgetsForProblem() && (getNotFoundWidgetsForProblem().size() > 0);
    }

    /**
     * @return Returns the startStateSent.
     */
    public boolean isStartStateSent() {
        return true;
    }
    public UniversalToolProxy getUniversalToolProxy() {
        return utp;
    }
    public void toggleWidgetFocusForSimSt(Object dw){
    	return;
    }
    public boolean isSimStudentMode(){
    	return false;
    }
	public void addCtatModeListener(CtatModeListener listener) {
		// TODO Auto-generated method stub
		
	}
	public void closeApplication(boolean b) {
		// TODO Auto-generated method stub
		
	}

	/**
     *  Close the student interface window by calling {JFrame#dispose()}. Also
     *  clears {@link #studentInterface}, calls {@link #clearWidgetInformation()}.
     */
    public void closeStudentInterface() {
        if (studentInterface == null)
            return;
        if(trace.getDebugCode("startstate"))
        	trace.out("startstate", "closeStudentInterface() before clearWidgetInfo");
        clearWidgetInformation();
        getPreferencesModel().setStringValue("Interface File", null);
        if(trace.getDebugCode("startstate"))
        	trace.out("startstate", "closeStudentInterface() before setStuIntConnSta(Disconnected)");
        if (utp != null && !Utils.isRuntime())
        	utp.setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.Disconnected);
        if(trace.getDebugCode("startstate"))
        	trace.out("startstate", "closeStudentInterface() before dispose");
        if (studentInterface instanceof JFrame)
        	((JFrame) studentInterface).dispose();
        if(trace.getDebugCode("startstate"))
        	trace.out("startstate", "closeStudentInterface() after dispose");
        studentInterface = null;
        /*if(CTATTabManager.getNumTabs() > 0)
     	   CTATTabManager.setNumTabs(CTATTabManager.getNumTabs()-1);*/
    }

    public void enqueueToolActionToStudent(Vector selection, Vector action,
			Vector input) {
		// TODO Auto-generated method stub
		
	}

    public BRPanel getBR_Frame() {
		// TODO Auto-generated method stub
		return null;
	}
	public CtatModeModel getCtatModeModel() {
		// TODO Auto-generated method stub
		return null;
	}
	public AbstractCtatWindow getDockedFrame() {
		// TODO Auto-generated method stub
		return null;
	}
	public JGraphPanel getJGraphWindow() {
		// TODO Auto-generated method stub
		return null;
	}
	public MissControllerExternal getMissController() {
		// TODO Auto-generated method stub
		return null;
	}
	public MT getModelTracer() {
		// TODO Auto-generated method stub
		return null;
	}
	public ProblemModel getProblemModel() {
		// TODO Auto-generated method stub
		return null;
	}
	public RuleActivationTree getRuleActivationTree() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean updateModelOnTraceFailure() {
		// TODO Auto-generated method stub
		return false;
	}
    
    /**
	 * Whether feedback to the student should be suppressed (true) or allowed (false).
	 * @return the {@link #suppressStudentFeedback}
	 */
	public boolean getSuppressStudentFeedback() {
		return suppressStudentFeedback;
	}
	/**
	 * Whether feedback to the student should be suppressed (true) or allowed (false).
	 * @param suppressStudentFeedback new value for {@link #suppressStudentFeedback}
	 */
	public void setSuppressStudentFeedback(boolean suppressStudentFeedback) {
		this.suppressStudentFeedback = suppressStudentFeedback;
	}

	/**
	 * Empty the set of {@link #unmatchedComponents}.
	 * Also calls {@link UniversalToolProxy#setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus)}
	 * to set the {@link StudentInterfaceConnectionStatus#Connected Connected} state back to
	 * {@link StudentInterfaceConnectionStatus#NewlyConnected NewlyConnected}.
	 */
	public void clearUnmatchedComponentsAndReviseConnectionStatus() {
		trace.out("mg", "TutorController.clearUnmatchedComponentsAndReviseConnectionStatus():"+
				" clearing unmatchedComponents, size was "+unmatchedComponents.size());
		unmatchedComponents.clear();
		if (utp != null && !Utils.isRuntime()) {
			if (utp.getStudentInterfaceConnectionStatus() == StudentInterfaceConnectionStatus.Connected) {
				utp.setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.NewlyConnected);
			}
		}
	}

	/**
	 * @return {@link ProblemModel#getProblemFullName()}.
	 * @see edu.cmu.pact.Utilities.CTAT_Controller#getProblemFullName()
	 */
	public String getProblemFullName() {
		ProblemModel pm = getProblemModel();
		return (pm == null ? null : pm.getProblemFullName());
	}

	/**
	 * @return {@link ProblemModel#getProblemName()}.
	 * @see edu.cmu.pact.Utilities.CTAT_Controller#getProblemName()
	 */
	public String getProblemName() {
		ProblemModel pm = getProblemModel();
		return (pm == null ? null : pm.getProblemName());
	}

	/**
	 * Instantiate an object of the {@value Logger#STUDENT_INTERFACE_PROPERTY} class.
	 * @param studentInterfaceClassName
	 * @return instance of the given class
	 */
	public static JComponent createStudentInterface(String studentInterfaceClassName) {
		if(studentInterfaceClassName == null)
			return null;
		Class<JComponent> studentInterfaceClass = null;
		Class cls = null;
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
			cls = Class.forName(studentInterfaceClassName, true, loader);
		} catch(Throwable e) {
			throw new IllegalArgumentException("Could not load "+Logger.STUDENT_INTERFACE_PROPERTY+" class \""+
					studentInterfaceClassName+"\"\n  using loader "+loader+"; cause:\n  "+e, e);
		}
		try {
			studentInterfaceClass = (Class<JComponent>) cls;
		} catch(ClassCastException cce) {
			throw new IllegalArgumentException("The "+Logger.STUDENT_INTERFACE_PROPERTY+" class "+cls+
					" must inherit from "+JComponent.class.getName(), cce);
		}
		try {
			return (JComponent) studentInterfaceClass.newInstance();
		} catch(Exception e) {
			throw new IllegalStateException("Error instantiating "+Logger.STUDENT_INTERFACE_PROPERTY+
					" class "+cls+"; cause:\n  "+e, e);
		}
	}

	/**
	 * @return null
	 */
	public Applet getApplet() {
		return null;
	}

    protected TSLauncherServer getTSLauncherServer() {
    	return tsLauncherServer;
    }

	/**
	 * @return null in this superclass
	 */
	public ProblemSummary getProblemSummary() { return null; }

	public CTAT_Launcher getServer() {	// FIXME: MAKE THIS PRIVATE LATER
		return null;
	}
}
