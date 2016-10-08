package pact.CommWidgets;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.OpenInterfaceDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.UnmatchedSelectionsDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.CommManager.CommMessageHandler;
import edu.cmu.pact.CommManager.CommMessageReceiver;
import edu.cmu.pact.Utilities.DelayedAction;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.UserLogin;
import edu.cmu.pact.ctat.MessageListener;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.model.CtatListModel;
import edu.cmu.pact.ctat.model.StartStateModel;
import edu.cmu.pact.jess.MT;

/**
 * Serves as the single communication point between the production system and
 * the authoring tools (at least in theory). Also the connection point between
 * the student interface and the tools.
 */
public class UniversalToolProxy implements CommMessageReceiver, CommMessageHandler, PropertyChangeListener,
	ProblemModelListener {

	/** An Author UI for {@link UniversalToolProxy#rebootInterface(Component, InterfaceRebootDialog)}. */
	public static interface RebootInterfaceDialog extends Runnable {
		/**
		 * @return true if should proceed, false to cancel
		 */
		public boolean confirm();
		/**
		 * Invoke the UI.
		 */
		public void invoke();
		/**
		 * Dispose of the UI.
		 */
		public void dispose();
	}

	public String applicationName = "AuthoringTool";
	protected boolean initialized;
	protected boolean useBehaviorRecorder = true;
	protected boolean showLoginWindow;
	boolean showWidgetInfo;
    
    private boolean hideWarnings, commutative = false, showDebugInfo = true;
	private boolean autoCapitalize;

	public String tutorName;
    

	public String lastHintSelection = "";

	private boolean caseInsensitive = true;

	public boolean connectedToProductionSystem;
    
    protected BR_Controller controller;

    /** Internal listeners for messages sent. */
	private Set<MessageListener> messageListeners;
	
	/** State of the connection to the student interface. */
	protected StudentInterfaceConnectionStatus studentInterfaceConnectionStatus = null;
	
	/** Listeners for StartStateModel changes. */
	protected List<StartStateModel.Listener> startStateListeners = new LinkedList<StartStateModel.Listener>();
	
	/** For reconciling the start state between the student interface and the graph. */
	protected StartStateModel startStateModel = null;
	
	/** Dialog to help author with selections present in a graph but absent from a student interface. */
	private UnmatchedSelectionsDialog unmatchedSelectionsDialog = null;
	
	/** The SetPreferences parameter name of the boolean telling whether this is an HTML5 interface. */
	public static final String HTML5 = "HTML5";
    
	public UniversalToolProxy(){
		studentInterfaceConnectionStatus = StudentInterfaceConnectionStatus.Disconnected;
	}

    /**
	 * Return the current state of the preference named by {@link BR_Controller#LOCK_WIDGETS}.
	 * @return {@link ProblemModel#getLockWidget()} if problem model defined; else default true
	 */
    public boolean lockWidget () {
        ProblemModel pm = controller.getProblemModel();
        if (pm != null)
            return pm.getLockWidget();
        else
            return true;
    }

	public void setApplicationName(String applicationName) {

		this.applicationName = applicationName;

	}



	
	//////////////////////////////////////////////////////
	/**
	 * To be run by TutorWindow.java when the interface is run.
	 * @param controller2 
	 */
	//////////////////////////////////////////////////////
	public void init(BR_Controller controller) {

        if (controller == null)
        		throw new InvalidParameterException("Controller cannot be null.");
        
        setController(controller);
		controller.getPreferencesModel().addPropertyChangeListener(this);
	}

	//////////////////////////////////////////////////////
	/**
	 * Called when a PropertyChangeEvent is sent from the PreferencesModel.
	 *
	 * @param  evt PropertyChangeEvent detailing change
	 */
	//////////////////////////////////////////////////////
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		if (name.equalsIgnoreCase(BR_Controller.OLI_LOGGING_URL)) {
			getController().getLoggingSupport().changeOLILoggingURL((String) newValue);
		} else if (name.equalsIgnoreCase(BR_Controller.USE_OLI_LOGGING)) {
			Boolean nv = (Boolean) newValue;
			if (trace.getDebugCode("log")) trace.out("log", "USE_OLI_LOGGING now " +
					  (nv != null && nv.booleanValue()));
		} else if (name.equalsIgnoreCase(BR_Controller.DISK_LOGGING_DIR)) {
            getController().getLoggingSupport().changeDiskLoggingDir((String) newValue);
		} else if (name.equalsIgnoreCase(BR_Controller.USE_DISK_LOGGING)) {
			Boolean nv = (Boolean) newValue;
			if (trace.getDebugCode("log")) trace.out("log", "USE_DISK_LOGGING now " +
					  (nv != null && nv.booleanValue()));
		} else
			return;
		if (trace.getDebugCode("in")) trace.out("in", "Changed " + name + " from " + evt.getOldValue() +
				  " to " + newValue);
	}

	private static final String[] movedFromWidgetMessages = new String[] {
		MsgType.INTERFACE_ACTION,
		MsgType.INTERFACE_DESCRIPTION,
		MsgType.START_PROBLEM,
		MsgType.START_STATE_END,
		MsgType.START_NEW_PROBLEM,
		MsgType.CORRECT_ACTION,
		MsgType.RESET_ACTION,
		MsgType.LISP_CHECK_ACTION,
		MsgType.INCORRECT_ACTION,
		MsgType.UNLOCK_COMPOSER,
		"HintList",
		MsgType.WRONG_USER_MESSAGE,
		MsgType.CONFIRM_DONE,
		MsgType.STATE_GRAPH,
		MsgType.INTERFACE_REBOOT,
		MsgType.GET_ALL_INTERFACE_DESCRIPTIONS,
		MsgType.SEND_WIDGET_LOCK
    };

	/**
	 * No-op in this superclass.
	 * @param o
	 * @see edu.cmu.pact.CommManager.CommMessageReceiver#receiveMessage(edu.cmu.pact.ctat.MessageObject)
	 */
	public void receiveMessage(MessageObject o) {

		//	trace.out ("received message for " +
		// o.getParameter("OBJECT").toString());
	}
	
 	/**
	 * Process a Comm message from the tutor.
	 * @param  messageObject MessageObject to process
	 */
	public void handleMessage(MessageObject messageObject)
	{
		boolean suppressFeedback = (suppressFeedback(messageObject) == FeedbackEnum.HIDE_ALL_FEEDBACK);
		if(suppressFeedback)
			return;
		handleMessageByPlatform(messageObject);
	}
	
 	/**
	 * Tool-specific processing for a Comm message from the tutor.
	 * @param  messageObject MessageObject to process
	 */
	public void handleMessageByPlatform(MessageObject messageObject)
	{		
        if (trace.getDebugCode("utp")) 
        	trace.out("utp", "inside UniversalToolProxy.handleMessage():\n" + messageObject);
		
        //2014-6-3 clean up handleMessage using checkMessage and transmitMessage
        if(checkMessage(messageObject)){
        	return;
        }
        
        fireMessageSent(messageObject);
		if(trace.getDebugCode("log"))trace.out("log", "UTP handleMessage after fireMessageSent for message:"+messageObject.toString());
		
        transmitMessage(messageObject);

        if (trace.getDebugCode("utp")) trace.out("utp", "UTP: don't know message type" + messageObject.getMessageType());
	}

	/**
	 * Check whether the MessageObject should be suppressed or delayed
	 * @param messageObject
	 * @return true if handleMessage should stop, false if it should continue
	 */
	protected synchronized boolean checkMessage(MessageObject messageObject){
		boolean suppressInterface = suppressMsgFromInterface(messageObject);
		boolean delayed = processUpdateAfterDelay(messageObject);
		
		//if any of these are true, end handleMessage early
		return suppressInterface || delayed;
	}
	
	private synchronized void fireMessageSent(MessageObject messageObject) {
		if (messageListeners == null)
			return;
		for (Iterator<MessageListener> it = messageListeners.iterator(); it.hasNext(); ){
			((MessageListener)it.next()).messageSent(messageObject);
			if(trace.getDebugCode("log"))trace.out("log", "UTP fireMessageSent iterator sending: "+messageObject.toString());
		}
	}
	
	/**
	 * After checkMessage and fireMessageSent, transmit the message to the corresponding 
	 * methods. Method ends when it finds the correct result.
	 * @param messageObject
	 */
	private synchronized void transmitMessage(MessageObject messageObject){
        // clear the message in the message window.
        
        if (controller!=null && controller.getHintMessagesManager()!=null &&
            messageObject.isMessageType(StudentInterfaceWrapper.cleanUpMessages))
            controller.getHintMessagesManager().cleanUpHintOnChange();

        if (messageObject.isMessageType(movedFromWidgetMessages)) {
            controller.handleCommMessage_movedFromCommWidget(messageObject);
            return;
        }

        if (messageObject.isMessageType("LoadProblem")) {
            trace.out ("utp", "load problem: not used in UTP");
            return;
        }
            
        if (MsgType.hasTextFeedback(messageObject) && controller.getHintMode()) {
            // set up message data
            controller.getHintMessagesManager().setMessageObject(messageObject);
            String message = controller.getHintMessagesManager().getFirstMessage();
            if (controller.getStudentInterface() != null)
                controller.getStudentInterface().getHintInterface().showMessage(message);
            
            return;
        }

        if (messageObject.isMessageType("ShowLoginWindow")) {
            trace.out ("utp", "login message received");
            showLogin();
            return;
        }

        if (messageObject.isMessageType("IsTutorVisible")) {
            Boolean visible = (Boolean)messageObject.getProperty("IsVisible");

            if (controller.getStudentInterface() == null) {
                if (trace.getDebugCode("utp")) trace.out("utp", "cannot set tutor visible: tutor frame == null");
                return;
            }
            controller.getStudentInterface().setVisible(visible.booleanValue());

            return;
        }
        
	}
	/**
	 * Register a listener to receive messages sent.
	 * @param listener
	 */
	public synchronized void addMessageListener(MessageListener listener) {
		if (listener == null)
			return;
		if (messageListeners == null)
			messageListeners = new HashSet<MessageListener>();
		messageListeners.add(listener);
		
		if(trace.getDebugCode("log"))trace.out("log", "UTP addMessageListener: "+listener.toString());
	}

	/**
	 * Unregister a listener to receive messages sent.
	 * @param listener
	 */
	public synchronized void removeMessageListener(MessageListener listener) {
		if (listener == null)
			return;
		if (messageListeners == null)
			return;
		messageListeners.remove(listener);
	}

	
	/**
	 * Block student feedback messages if
	 * {@link ProblemModel#getSuppressStudentFeedback()} is true.
	 * @param messageType message type
	 * @return true if should block
	 */
	protected FeedbackEnum suppressFeedback(MessageObject messageObject) {
		return MsgType.suppressFeedback(messageObject,
				getController().getProblemModel().getSuppressStudentFeedback());
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getHideWarnings() {
		return hideWarnings;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setHideWarnings(boolean hideWarnings) {
		this.hideWarnings = hideWarnings;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getShowDebugInfo() {
		return showDebugInfo;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setShowDebugInfo(boolean showDebugInfo) {
		this.showDebugInfo = showDebugInfo;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setCommutativity(boolean commutative) {
		this.commutative = commutative;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getCommutativity() {
		return commutative;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getCaseInsensitive() {
		return caseInsensitive;
	}

	//////////////////////////////////////////////////////
	/**
	 * If true, all text fields will always uppercase their input
	 */
	//////////////////////////////////////////////////////
	public void setAutoCapitalize(boolean autoCapitalize) {
		this.autoCapitalize = autoCapitalize;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getAutoCapitalize() {
		return autoCapitalize;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public String getApplicationName() {
		return applicationName;
	}
	
	public void sendProperty(MessageObject msg) {
		
		BR_Controller ctlr = getController();
        if (ctlr == null || !ctlr.getCtatModeModel().isRuleEngineTracing())
        	return;
      
		if (ctlr != null && ctlr.getCtatModeModel().isJessTracing()) {
			MT mt = ctlr.getModelTracer();
			if (mt != null) {
				//System.out.println("#*#*#*#*#*#*#");System.out.println("#*#*#*#*#*#*#");System.out.println("#*#*#*#*#*#*#");System.out.println("#*#*#*#*#*#*#");
				//Thread.dumpStack();
				//trace.out("webAuth","***** trying to handle commMessage" + msg);
				//System.out.println("#*#*#*#*#*#*#");System.out.println("#*#*#*#*#*#*#");System.out.println("#*#*#*#*#*#*#");System.out.println("#*#*#*#*#*#*#");
				MessageObject mo = mt.handleCommMessage(msg);
				if (mo != null)
					ctlr.handleCommMessage(mo);
			} else
				trace.err("UTP.sendProperty(): controller has null model tracer");
			return;
		} else {
			try {
				throw new IllegalStateException("UTP.sendProperty() called when not Jess tracing; message:\n  "+msg);
			} catch (Exception e) {
				trace.errStack("UTP.sendProperty() called when not Jess tracing; message:\n  "+msg, e);
			}
		}
	}

	public void setTutorName(String _tutorName) {
		tutorName = new String(_tutorName);
        if (getController() != null)
    		getController().getLoggingSupport().setTutorSessionID(tutorName
    			+ DateFormat.getDateTimeInstance(DateFormat.SHORT,
    											 DateFormat.LONG).format(new Date()));
	}

	/**
	 * Pass a message to the Behavior Recorder.
	 * zz add 11/21/02 replace: sendToESE (MessageObject mo)
	 * @param mo the message
	 */
	public void sendMessage(MessageObject mo) {
		if (trace.getDebugCode("gusmiss")) trace.out("gusmiss", "entered UTP.sendCommMessage()");
		getController().handleCommMessage(mo);
	}

	//////////////////////////////////////////////////////
	/**
	 * Populates a vector which, when sent to this interface, will
	 * reset it to the state it's currently in. Used for the start
	 * state in the Behavior Recorder.
	 * 
	 * @param v Vector of messages to add to; if null, creates a Vector
	 * @param problemName ProblemName property for StartProblem message
	 * @param wantInterfaceDescriptions true means we want all {@value MsgType#INTERFACE_DESCRIPTION} msgs
	 * @return Vector to which msgs were added
	 */
	//////////////////////////////////////////////////////
	public Vector<MessageObject> createCurrentStateVector(Vector<MessageObject> v,
			String problemName, boolean wantInterfaceDescriptions) {
	    // trace.outln("functions", "createCurrentStateVector");
	    // trace.printStack();
	    if (v == null)
	        v = new Vector<MessageObject>();
	    
	    v.addAll(getStartStateModel().createStartStateMessageList(getController().getProblemModel(),
	    		wantInterfaceDescriptions));
	    
	    return v;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean connectToTutor() {
	    if (controller.getCtatModeModel().isJessTracing()) {
            connectedToProductionSystem = true;
            return true;
        }
		throw new IllegalStateException("UTP.connectToTutor() called when not Jess tracing");
	}

	/**
	 * Label for student interface connection status.
	 * @return {@value #JAVA}
	 */
	public String getStudentInterfacePlatform() {
		return JAVA;
	}

	/**
	 * @return the {@link #studentInterfaceConnectionStatus}
	 */
	public StudentInterfaceConnectionStatus getStudentInterfaceConnectionStatus() {
		return studentInterfaceConnectionStatus;
	}
	
	/**
	 * Revise the {@link #studentInterfaceConnectionStatus} and generate an event.
	 * @param sics new value for {@link #studentInterfaceConnectionStatus};
	 *        a null argument doesn't change the field but does send an event
	 */
	public void setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus sics) {
		StudentInterfaceConnectionStatus oldSics = studentInterfaceConnectionStatus;
		if (sics != null)
			studentInterfaceConnectionStatus = sics;
		if(!studentInterfaceConnectionStatus.isConnected())
			unmatchedSelectionsDialog = null;
		if (trace.getDebugCode("inter"))
			trace.out("inter", "updateStudentInterfaceConnection() "+oldSics+" => "+sics);
		getController().fireChangeEvent(this);
	}
	
	/**
	 * Send a {@link EventObject} to all listeners. <b>N.B.: this is a one-time notice.</b> The
	 * {@link #startStateListeners} list is cleared after firing. 
	 * @param source for {@link EventObject#getSource()}: should be instance of {@link StartStateModel}
	 */
    protected void fireStartStateEvent(Object source) {
    	EventObject evt = new EventObject(source);
    	if(trace.getDebugCode("editstudentinput"))
    		trace.printStack("editstudentinput", "UTP.fireStartStateEvent("+evt+") n listeners "+
    				startStateListeners.size());
		for(Iterator<StartStateModel.Listener> it = startStateListeners.iterator(); it.hasNext();) {
    		it.next().startStateReceived(evt);
    	}
		startStateListeners.clear();
	}
    
    /**
     * @param listener listener to add to {@link #startStateListeners}
     */
    public void addStartStateListener(StartStateModel.Listener listener) {
    	startStateListeners.add(listener);
    }
    
    /**
     * @param listener listener to remove from {@link #startStateListeners}
     */
    public void removeStartStateListener(StartStateModel.Listener listener) {
    	startStateListeners.remove(listener);
    }

    /**
     * Process a {@value MsgType#COMPONENT_INFO} message.
     * Calls {@link #showComponentMismatch(String)}.
     * @param o message from student interface
     */
    public void handleComponentInfoMessage(MessageObject o) {
    	Object mismatchMsg = o.getProperty("ComponentMismatchMessage");
    	if (mismatchMsg instanceof String)
    		trace.err("UTP.handleComponentInfoMessage("+o.summary()+") mismatch msg "+mismatchMsg);
	}
    
	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setShowWidgetInfo(boolean t) {
		showWidgetInfo = t;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getShowWidgetInfo() {
		return showWidgetInfo;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setShowLoginWindow(boolean showLoginWindow) {
		this.showLoginWindow = showLoginWindow;
		if (showLoginWindow) {
			//			showLogin();
		}
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public UserLogin showLogin() {
		JFrame frame = new JFrame();
		//		trace.out ("creating login window");
		UserLogin uLog = createUserLogin(frame);
		// Add window listener.
		uLog.addWindowListener(new WindowAdapter() {
			public void windowDeactivated(WindowEvent e) {
				//trace.out ("window de-activated: isVisible = " +
				// e.getWindow().isVisible());
				if (e.getWindow().isVisible() == true)
					e.getWindow().toFront();
			}
		});
		uLog.addPropertyChangeListener(new PropertyChangeListener() {
			/**
			 * Listen for {@link UserLogin#LOGIN_NAME} or
			 * {@link UserLogin#QUIT_BUTTON} events from UserLogin.
			 * @param evt the event; user name is in newValue
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				String eName = evt.getPropertyName();
				if (trace.getDebugCode("br")) trace.out("br", "UserLogin event "+eName+": old "+evt.getOldValue()+
						", new "+evt.getNewValue());
				if (UserLogin.LOGIN_NAME.equals(eName)) {
					LoggingSupport ls = getController().getLoggingSupport();
					if (ls != null)
						ls.setStudentName((String) evt.getNewValue());
					//If this fails its because the ctat_launcher doesn't
					//set the controllers wrapper until ctatlauncher.launch is called
					//make sure launch is called..
					StudentInterfaceWrapper wrapper = controller.getStudentInterface();
					trace.err("UTP.propertyChange("+eName+") controller.getWrapper() "+wrapper);
					if (wrapper != null)
						wrapper.setVisible(true);
				} else if (UserLogin.QUIT_BUTTON.equals(eName)) {
					StudentInterfaceWrapper si = getController().getStudentInterface();
					if (si instanceof TutorWrapper)
						((TutorWrapper) si).doLogout(false, false);
				}
			}
		});
		uLog.setVisible(true);
        uLog.toFront();
        return uLog;
	}

	/**
	 * Generate a {@link UserLogin} dialog without reference to other objects
	 * @param frame
	 * @return
	 */
	protected UserLogin createUserLogin(JFrame frame) {
		return new UserLogin(frame);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getShowLoginWindow() {
		return showLoginWindow;
	}

    /**
     * @param controller The controller to set.
     */
    protected void setController(BR_Controller controller) {
        if (controller == null)
            throw new InvalidParameterException("Controller cannot be null");
        this.controller = controller;
    }



    /**
     * @return Returns the controller.
     */
    public BR_Controller getController() {
        return controller;
    }

    /**
     * Subclasses may override this method to bundle messages for performance.
     * When called with a message whose MessageType matches endMsgType, the
     * subclass should include that message as the last and then flush the bundle. 
     * @param o message to send or bundle for sending later
     * @param bundleName a label for the bundle
     * @param endMsgType the MessageType value that signals the end of the bundle
     */
	public void bundleMessage(MessageObject o, String bundleName, String endMsgType) {
		handleMessage(o);
	}

	/**
	 * Unpack a bundle of messages.
	 * @param mo message bundle
	 * @return list of messages inside
	 */
	public static List<MessageObject> unbundle(MessageObject mo) {
		Object messagesObj = mo.getProperty(MsgType.MESSAGES);
		if (!(messagesObj instanceof List)) {
			trace.err("unbundle: "+MsgType.MESSAGES+" property not a List:\n  "+mo);
			return null;
		}
		if (((List) messagesObj).size() < 1 || !(((List) messagesObj).get(0) instanceof Element)) {
			trace.err("unbundle: "+MsgType.MESSAGES+" property empty or not List<Element>:\n  "+mo);
			return null;
		}
		Iterator<Element> it = ((List<Element>) messagesObj).iterator();
		List<MessageObject> result = new ArrayList<MessageObject>(((List<Element>) messagesObj).size());
		for (int i = 0; it.hasNext(); ++i) {
			Element msgElt = it.next();
			try {
				result.add(MessageObject.fromElement(msgElt));
			} catch (Exception e) {
				trace.errStack("Error unbundling msg["+i+"]:\n    "+mo, e);
			}
		}
		return result;
	}

	/**
	 * @return null Subclass overrides
	 */
	public JCommWidgetsToolProxy getToolProxy() {
		return null;
	}
	public void setProperty(MessageObject mo) {
		getController().handleCommMessage(mo);
	}

	/** For testing action values in {@link #isDelayedAction(String, String[])}. */
    private static final Pattern DelayedActionPattern = Pattern.compile("\\w+");

    /** Return value from {@link #getStudentInterfacePlatform()} for Java student interfaces. */
	public static final String JAVA = "Java";

    /** Return value from {@link #getStudentInterfacePlatform()} for Flash student interfaces. */
	public static final String FLASH = "Flash";
	
    /**
     * Decide whether an action value has a delay value suffix and parse it.
     * @param action string to parse
     * @param revisedAction to return action value without delay suffix
     * @return delay value
     */
    public static int isDelayedAction(String action, String[] revisedAction) {
    	if(action == null || action.length() < 1)
    		return 0;
    	
		int j = 0, delayTime = 0;
		String temp[] = { "", "" };
		Matcher m = DelayedActionPattern.matcher(action);
		while (j < temp.length && m.find()) {
			if (m.group() != null) {
				temp[j++] = m.group();
			}
		}
		if (trace.getDebugCode("msg"))
			trace.out("msg", "UTP.isDelayedAction("+action+") temp[] "+Arrays.toString(temp));
		if (j == 2) {
			try {
				delayTime = Integer.parseInt(temp[1].trim());
				revisedAction[0] = temp[0];
			} catch (NumberFormatException nfe) {
				revisedAction[0] = action;
				delayTime = 0;                    // not a delayed action
			}
		}
		return delayTime;
    }
    
    /**
     * Check whether the given action has a delay setting. If so, invoke a {@link DelayedAction}
     * instance to process a message of this description after the delay. A delay setting is an
     * suffix ":N" to the action, where N is an integer specifying a delay in milliseconds before
     * the message should be sent to the user interface.
     * @param messageType
     * @param selection
     * @param action
     * @param input
     * @return true if this is a delayed action
     */
    public boolean processUpdateAfterDelay(String messageType, String selection, String action, String input) {

    	String[] revisedAction = new String[1];
    	int delay = isDelayedAction(action, revisedAction);		
    		
    	if (delay == 0)
    		return false;

    	action = revisedAction[0];   // 2012-08-01: remove delay from action to prevent looping

    	final MessageObject mo = MessageObject.create(messageType);
        mo.setSelection(selection);
        mo.setAction(action);
        mo.setInput(input);
        
        sendMsgAfterDelay(mo, delay);
        return true;
    }

    /**
     * On a new thread, call {@link #handleMessage(MessageObject)} after the given time interval. 
     * Returns immediately.
     * @param mo message to send
     * @param ms milliseconds to wait
     */
    private void sendMsgAfterDelay(final MessageObject mo, int ms) {

        if (trace.getDebugCode("msg"))
        	trace.out("msg", "sendMsgAfterDelay("+mo.getSelection()+", "+mo.getAction()+", "+mo.getInput()+") <= "+ms);
        
    	DelayedAction dA = new DelayedAction(new Runnable() {
    			public void run() {
    				handleMessage(mo);
    			}
    	});
    	
    	dA.setDelayTime (ms);
    	dA.start();
    }

    /**
     * Check whether the given action has a delay setting. If so, invoke a {@link DelayedAction}
     * instance to process a message of this description after the delay. A delay setting is an
     * suffix ":N" to the action, where N is an integer specifying a delay in milliseconds before
     * the message should be sent to the user interface.
     * @param messageType
     * @param selection
     * @param action
     * @param input
     * @return true if this is a delayed action
     */
    public boolean processUpdateAfterDelay(MessageObject mo) {

    	String action = mo.getFirstAction();
    	String[] revisedAction = new String[1];
    	int delay = isDelayedAction(action, revisedAction);		
    		
    	if (delay == 0)
    		return false;

    	mo.setAction(revisedAction[0]);   // 2012-08-01: remove delay from action to prevent looping
    	
    	sendMsgAfterDelay(mo, delay);
    	return true;
    }

    /**
     * Pass a message from the student interface to the {@link #getStartStateModel()}. 
     * @param mo message to pass
     */
	public void handleStartStateMessageFromInterface(MessageObject mo) {
		getStartStateModel().addStudentInterfaceMessage(mo);
	}

	/**
	 * Get an iterator for the current start state from {@link #getStartStateModel()}.
	 * This is the proper source of the current start state at student run time.
	 * @param pm for start state messages in the .brd
	 * @return result from {@link StartStateModel#startNodeMessagesIterator(ProblemModel)}
	 */
	public Iterator<MessageObject> startNodeMessagesIterator(ProblemModel pm) {
		return getStartStateModel().startNodeMessagesIterator(pm);
	}

	/**
	 * @return {@link JCommWidget#listActionNames()}
	 */
	protected List<String> listFixedSelectionNames() {
		return JCommWidget.listFixedSelectionNames();
	}

	/**
	 * @return {@link JCommWidget#listActionNames()}
	 */
	protected List<String> listActionNames() {
		return JCommWidget.listActionNames();
	}
	
	/**
	 * @return {@link #startStateModel}; create a new instance if null
	 */
	public StartStateModel getStartStateModel() {
		if(startStateModel == null) {
			startStateModel = new StartStateModel(listFixedSelectionNames(), listActionNames());
			if(!Utils.isRuntime()) {
				unmatchedSelectionsDialog = new UnmatchedSelectionsDialog(this);
				addStartStateListener(unmatchedSelectionsDialog);
			}
		}
		return startStateModel;
	}

	/**
	 * Compare start state messages in the student interface and in the {@link ProblemModel}.
	 * @param problemModel
	 * @return table of results; null if none to compare
	 */
	public String compareStartStateMessages(ProblemModel problemModel) {
		if(Utils.isRuntime())
			return null;
		return getStartStateModel().compareStartStateMessages(problemModel);
	}

	/**
	 * Nulls {@link #startStateModel}.
	 */
	public void resetStartStateModel() {
		if(trace.getDebugCode("startstate"))
			trace.printStack("startstate", "UTP.resetCompareStartStateMessages()");
		startStateModel = null;
		unmatchedSelectionsDialog = null;
	}

	/**
	 * Call this with argument author is about to edit the start state.
	 * @param beginning true when author initiates action; false when ready for edits
	 * @return result from {@link StartStateModel#setUserBeganStartStateEdit(boolean)} 
	 */
	public boolean setUserBeganStartStateEdit(boolean beginning) {
		return getStartStateModel().setUserBeganStartStateEdit(beginning);
	}

	/**
	 * @return {@link StartStateModel#isStartStateModified()} from {@link #getStartStateModel()}
	 */
	public boolean isStartStateModified() {
		return getStartStateModel().isStartStateModified();
	}
	
	/**
	 * Process a {@value MsgType#GET_ALL_INTERFACE_DESCRIPTIONS} message, which should be a bundle
	 * of {@value MsgType#INTERFACE_DESCRIPTION} messages. These are individual XML elements
	 * inside a single &lt;properties&gt; element.
	 * @param mo message with bundle
	 */
	public void handleGetAllInterfaceDescriptions(MessageObject mo) 
	{
		if(trace.getDebugCode("ll"))
			trace.out("ll", "handleGetAllInterfaceDescriptions ()");
    	if(trace.getDebugCode("editstudentinput"))
    		trace.out("editstudentinput", "UTP.handleGetAllInterfaceDescriptions("+mo.summary()+
    				") n listeners "+startStateListeners.size());
		
		List<MessageObject> messages = unbundle(mo);
		if(messages == null)
			return;
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "UTP.handleGetAllInterfaceDescriptions() bundle size "+messages.size());

		awaitInterfaceDescriptions(false);
		
		resetStartStateModel();                           // reinitialize StartStateModel
		getStartStateModel().addStudentInterfaceMessageBundle(messages);  // pass all to StartStateModel
		setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.Connected);
		fireStartStateEvent(getStartStateModel());
		if(!getController().isAcceptingStartStateMessages())
			return;
		for (int i = 0; i < messages.size(); ++i) {
			MessageObject msg = messages.get(i);
			try {
				String msgType = msg.getMessageType();
				// call controller.handleInterface{Description|Action}Message()
				// with fwdToSSM argument false since passed to SSM already
				if(MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(msgType))
					getController().handleInterfaceDescriptionMessage(msg, false);
				else if(MsgType.INTERFACE_ACTION.equalsIgnoreCase(msgType))
					getController().handleInterfaceActionMessage(msg, false);
				else if(MsgType.UNTUTORED_ACTION.equalsIgnoreCase(msgType))
					getController().handleUntutoredActionMessage(msg, false);
				else {
					trace.err("Unexpected message type \""+msg.getMessageType()+"\" in bundle "+
							MsgType.GET_ALL_INTERFACE_DESCRIPTIONS+"\n    "+msg);
					getController().handleCommMessage(msg);
				}
			} catch (Exception e) {
				trace.errStack("Error on bundled msg["+i+"]:\n  "+mo, e);
			}
		}
	}

	/**
	 * Act as if the author had chosen the student interface settings for this component in the
	 * start state. Calls {@link StartStateModel#chooseSISettings(String)}.
	 * @param intDescMsg {@value MsgType#INTERFACE_DESCRIPTION} msg from student interface
	 * @param pm problem whose start state should be changed
	 */
	public void chooseSISettingsInStartState(MessageObject intDescMsg, ProblemModel pm) {
		getStartStateModel().commitSISettings(intDescMsg, pm);
	}

	/**
	 * Close and restart a student interface within the same {@link TutorController} instances.
	 * This is synchronous for Java student interfaces. Subclasses will need to override
	 * for remote student interfaces.
	 * @param dialog unused in this implementation
	 * @return true if the interface has been restarted;
	 *         false if there's no student interface connected
	 */
	public boolean rebootInterface(RebootInterfaceDialog dialog) {
		if(getController() == null)
			return false;
		JComponent tutorPanel = getController().getTutorPanel();
		if(tutorPanel == null)
			return false;

		Class tutorPanelClass = tutorPanel.getClass();

		getController().closeStudentInterface();

		StudentInterfaceWrapper siw = OpenInterfaceDialog.openInterface(tutorPanelClass, getController(), null);
        if (siw == null) 
    		return false;        

        siw.setVisible(true);
        //	trace.out("mg", "OpenInterfaceDialog (openInterface): working with controller " + controller.getTabNumber());
        controller.setStudentInterface(siw);
        return true;
	}

	/**
	 * Send a {@value MsgType#GET_ALL_INTERFACE_DESCRIPTIONS} message. This method is synchronous:
	 * the response should have been received by the time this returns.
	 * @return false if no interface is connected; else true
	 */
	public boolean getAllInterfaceDescriptions() {
		if(trace.getDebugCode("startstateverbose"))
			trace.printStack("startstateverbose", "UTP.getAllInterfaceDescriptions()");
		if(!getStudentInterfaceConnectionStatus().isConnected())
			return false;
		handleMessage(MessageObject.create(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS));
		return true;
	}

	/**
	 * Test whether a message should be suppressed from the user interface. Return true if
	 * the given message (using its message type) should <i>not</i> be sent to the user interface.
	 * @param o message to test
	 * @return this superclass implementation always returns false
	 */
	protected boolean suppressMsgFromInterface(MessageObject o) {
		return false;
	}

	/**
	 * Populate the given list model with the selection names. 
	 * @param listModel
	 */
	public void listSelectionNames(CtatListModel listModel) {
		if(getStartStateModel().nInterfaceDescriptionsFromInterface() > 0)
			listModel.addAll(getStartStateModel().getComponentNames());
		else {
			handleMessage(MessageObject.create(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS));
			listModel.addAll(getStartStateModel().getComponentNames());
		}
	}

	/**
	 * @param selection argument for {@link StartStateModel#getAllActionNames()}
	 * @return {@link #getStartStateModel()}.getAllActionNames()
	 */
	public Collection<String> getAllActionNames() {
		return getStartStateModel().getAllActionNames();
	}

	/**
	 * @param msgType argument for {@link JCommWidget#clientSupports(String)}
	 * @param commShellVersion not used
	 * @return
	 */
	public Boolean clientSupports(String msgType, String commShellVersion) {
		return JCommWidget.clientSupports(msgType);
	}

	/**
	 * Tell whether we can yet display {@value MsgType#INTERFACE_DESCRIPTION} messages. 
	 * @return false if no interface connected; else true if
	 *         {@link StartStateModel#nInterfaceDescriptionsFromInterface()} > 0
	 */
	public boolean hasInterfaceDescriptions() {
		if(!getStudentInterfaceConnectionStatus().isConnected())
			return false;
		int n = getStartStateModel().nInterfaceDescriptionsFromInterface();
		return (n > 0);
	}

	/**
	 * @return true
	 */
	public boolean getStoreAllInterfaceDescriptions() {
		return true;
	}

	/**
	 * @param storeAllInterfaceDescriptions not used: this method is a no-op
	 */
	public void setStoreAllInterfaceDescriptions(boolean storeAllInterfaceDescriptions) {
		if(trace.getDebugCode("intdesc"))
			trace.printStack("intdesc", "UTP *NO-OP* .setStoreAllInterfaceDescriptions("+storeAllInterfaceDescriptions+")");
	}

	/**
	 * Call {@link unmatchedSelectionsDialog}.{@link UnmatchedSelectionsDialog#startStateReceived(EventObject)}
	 */
	public void initUnmatchedSelectionsDialog() {
		if(unmatchedSelectionsDialog == null || startStateModel == null)
			return;
		EventObject evt = new EventObject(startStateModel);
		unmatchedSelectionsDialog.startStateReceived(evt);
	}

	/**
	 * @return {@link #unmatchedSelectionsDialog}
	 */
	public UnmatchedSelectionsDialog getUnmatchedSelectionsDialogLauncher() {
		return unmatchedSelectionsDialog;
	}

	/**
	 * Notify the menu and other alert sources that 
	 * @param enable true if the dialog should be enabled; false if not
	 */
	public void notifyUnmatchedSelectionsDialogAvailable(boolean enable) {
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "UTP.notifyUnmatchedSelectionsDialogAvailable("+enable+") this UTP "+
					trace.nh(this)+", BR_C's UTP "+trace.nh(getController().getUniversalToolProxy()));
		getController().getCtatFrameController().getDockedFrame().getCtatMenuBar()
				.enableFindObsoleteComponents(enable);
		getController().getCtatFrameController().getDockedFrame().updateUnmatchedSelections(enable);
	}

	/**
	 * Tell whether the author UI should check for obsolete selections.
	 * @return true only if all of these are true:<ul>
	 *         <li>a student interface is connected;</li>
	 *         <li>a the current problem model has a start node;</li>
	 *         <li>{@link UnmatchedSelectionsDialog.Launcher#hasDialog()} from
	 *             {@link #getUnmatchedSelectionsDialogLauncher()} returns true.</li>
	 *         </ul>
	 */
	public boolean enableObsoleteSeletionDialog() {
		if(!getStudentInterfaceConnectionStatus().isConnected())
			return false;
		ProblemModel pm;
		if(((pm = getController().getProblemModel()) == null) || pm.getStartNode() == null)
			return false;
		if(getUnmatchedSelectionsDialogLauncher() == null)
			return false;
		return getUnmatchedSelectionsDialogLauncher().hasDialog();
	}
	
	/**
	 * Pass the ProblemModel change to {@link #unmatchedSelectionsDialog}, if not defined.
	 * @param e describes change
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener#problemModelEventOccurred(edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent)
	 */
	public void problemModelEventOccurred(ProblemModelEvent e) {
		if(this.unmatchedSelectionsDialog != null)
			unmatchedSelectionsDialog.problemModelEventOccurred(e);
	}

	/**
	 * No-op in this superclass.
	 * @param begin true means begin wait; false means stop waiting
	 */
	public void awaitSetPreferences(boolean begin) {}

	/**
	 * No-op in this superclass.
	 * @param begin true means begin wait; false means stop waiting
	 */
	public void awaitInterfaceDescriptions(boolean begin) {}

	/**
	 * Try to save the student interface to the given file.
	 * @param chosenFile
	 * @throws Exception
	 */
	public void saveStudentInterfaceFile(File chosenFile) throws Exception {
		getStartStateModel().saveStudentInterfaceFile(chosenFile);
	}
}
