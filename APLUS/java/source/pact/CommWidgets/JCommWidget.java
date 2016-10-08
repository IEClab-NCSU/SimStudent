package pact.CommWidgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import pact.CommWidgets.event.HelpEvent;
import pact.CommWidgets.event.HelpEventListener;
import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.ProblemDoneEvent;
import pact.CommWidgets.event.ProblemDoneListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.CommManager.CommManager;
import edu.cmu.pact.CommManager.CommMessageHandler;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.DelayedAction;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.view.AvoidsBackGrading;

//////////////////////////////////////////////////////
/**
 * CommWidgets allow a user to visually build interfaces which can
 * automatically communicate with lisp. JCommWidget: The root class that all
 * comm widgets extend. Provides functionality for communicating with Lisp via
 * Comm.
 */
// ///////////////////////////////////////////////////
public abstract class JCommWidget extends JPanel implements FocusListener, MouseListener,
        edu.cmu.pact.CommManager.CommMessageReceiver {

    private static TutorController controller;

    // private UniversalToolProxy universalToolProxy;
    private CommMessageHandler universalToolProxy;

    protected static Font defaultFont = new Font("Verdana", Font.PLAIN, 10);


    /** Action value for clearing widgets. */
	public static final String 	RESET = "reset";
	
    public String actionName = "";

    private Vector commSelectionVector = new Vector();

    protected String commName;

    protected String commSelection = "";

    protected String applicationName;

    protected boolean initialized;

    protected boolean dirty, locked;

    protected boolean warningShown;

    protected Color startColor = Color.black;

    protected Color correctColor = Color.green.darker();

    protected Color incorrectColor = Color.red;

    protected Color LISPCheckColor = Color.yellow.darker();

    protected Color backgroundHighlightColor = Color.gray;

    protected Color backgroundMouseColor = Color.gray;

    protected Color backgroundNormalColor = Color.white;

    protected Font correctFont, incorrectFont;

    protected boolean updateEachCycle;

    protected boolean isHighlighted;

    protected boolean startStatelocked;

    private EventListenerList studentActionListeners;

    protected Border originalBorder;

    protected boolean isHintBtn = false;

    protected boolean autoCapitalize = false;
    
    protected boolean invisible = false;
    
    protected   int delayTime = 0;   // milliseconds
    
	public static final String DRAG_INTO = "DragInto";                 // JCommPicture
	public static final String PLAY_AUDIO = "PlayAudio";
	public static final String UPDATE_CHOOSER = "UpdateChooser";
	public static final String UPDATE_COMBO_BOX = "UpdateComboBox";
	public static final String UPDATE_COMPOSER = "UpdateComposer";
	public static final String UPDATE_ICON = "UpdateIcon";
	public static final String UPDATE_MULTIPLE_CHOICE = "UpdateMultipleChoice";
	public static final String UPDATE_MULTIPLE_CHOICE_CHECK_BOX = "UpdateMultipleChoiceCheckBox";
	public static final String UPDATE_QUESTION_COMBO_BOX = "UpdateQuestionComboBox";
	public static final String UPDATE_QUESTION_TEXT_FIELD = "UpdateQuestionTextField";
	public static final String UPDATE_TABLE = "UpdateTable";
	public static final String UPDATE_TEXT = "UpdateText";
	public static final String UPDATE_TEXT_AREA = "UpdateTextArea";
	public static final String UPDATE_TEXT_FIELD = "UpdateTextField";
	public static final String UPDATE_AUDIO = "UpdateAudio";
	public static final String UPDATE_LIST = "UpdateList";
	public static final String UPDATE_INVISIBLE = "UpdateInVisible";
	public static final String UPDATE_BACKGROUNDCOLOR = "UpdateBackgroundColor";
	public static final String UPDATE_RADIO_BUTTON = "UpdateRadioButton";
	public static final String BUTTON_PRESSED = "ButtonPressed";
	
    /** Action value for showing or hiding widgets. */
	public static final String SET_VISIBLE = "SetVisible";

	/** Fixed list of {@link JCommWidget} actions for {@link #listActionNames()}. */
	private static List<String> JavaActionNames = null;
	static {
		String[] arr = new String[] {
				DRAG_INTO,              UPDATE_MULTIPLE_CHOICE_CHECK_BOX, UPDATE_AUDIO,
				PLAY_AUDIO,             UPDATE_QUESTION_COMBO_BOX,        UPDATE_LIST,
				UPDATE_CHOOSER,         UPDATE_QUESTION_TEXT_FIELD,       UPDATE_INVISIBLE,
				UPDATE_COMBO_BOX,       UPDATE_TABLE,                     UPDATE_BACKGROUNDCOLOR,
				UPDATE_COMPOSER,        UPDATE_TEXT,                      UPDATE_RADIO_BUTTON,
				UPDATE_ICON,            UPDATE_TEXT_AREA,                 BUTTON_PRESSED,
				UPDATE_MULTIPLE_CHOICE, UPDATE_TEXT_FIELD,                SET_VISIBLE				
		};
		Arrays.sort(arr, String.CASE_INSENSITIVE_ORDER);
		JavaActionNames = Arrays.asList(arr);
	}

	/** Fixed list of {@link JCommWidget} selections for {@link #listFixedSelectionNames()}. */
	private static final List<String> JavaFixedSelectionNames = Arrays.asList(new String[0]);  // empty so far
	
	/**
	 * @return sorted list of all action names 
	 */
	public static List<String> listActionNames() {
		return JavaActionNames;
	}

	/**
	 * @return {@link JCommWidget#listActionNames()}
	 */
	protected static List<String> listFixedSelectionNames() {
		return JavaFixedSelectionNames;
	}
	
    public boolean getAutoCapitalize() {
        return this.autoCapitalize;
    }

    public void setAutoCapitalize(boolean autoCapitalize) {
        this.autoCapitalize = autoCapitalize;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean getDirty() {
        return dirty;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public JCommWidget() {
        setCommSelectionVector(new Vector());
        this.studentActionListeners = new EventListenerList();
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public boolean initialize(TutorController controller) {
        
        if (commName == null)
            throw new NullPointerException();
        
        this.controller = controller;
        this.addCommWidgetName(commName);

        if (initialized)
            return true;
        
	// getCommWidgetVector().addElement(this);
        commSelection = commName + " " + commSelection;
        setCommSelectionVector(getCommWidgets(commSelection));

        if (getUniversalToolProxy()!=null &&
            getUniversalToolProxy().getShowWidgetInfo())
            setToolTipWidgetInfo();
        addCommListener(controller);
        setVisible(!isInvisible());
        initialized = true;
        return initialized;
    }

    /**
     * This method was deprecated but has been returned to service. Its previous use
     * was backwards compatibility with old interfaces. Now it is needed in user interfaces
     * where {@link WrapperSupport#examineInterface(java.awt.Container, TutorController)}
     * lacks the security permissions to learn of private fields in the UI by reflection.  
     * Where the controller is available, instead use
     * {@link #setCommName(String, TutorController) setCommName(commName, controller)}.
     * @param commName new value for {@link #commName}
     */
    public void setCommName(String commName) {
    	setCommName(commName, getController());
    }
    
    /**
     * The commName is the string used in Comm messages to refer to this
     * object.
     * @param controller 
     */
    // ////////////////////////////////////////////////////
    public void setCommName(String commName, TutorController controller) {
    	if(trace.getDebugCode("inter"))
    		trace.outNT("inter", getClass().getSimpleName()+".setCommName("+commName+")");
        setController(controller);
        if(controller != null)
        	setUniversalToolProxy(controller.getUniversalToolProxy());
        if (commName.equals(""))
            return;
        if (this.commName != null)
            removeCommWidgetName(this.commName);

        this.commName = commName;
        addCommWidgetName(commName);

        if (this.commName.equalsIgnoreCase("hint")
                || this.commName.equalsIgnoreCase("help")) {
            setHintBtn(true);
        }
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void setToolTipWidgetInfo() {
        setToolTipText("Comm name: " + commName + " Class: "
                + getClass().getName());
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public String retrieveName() {
        if (commName == null)
            return getClass().getName();
        return commName;
    }

    public void receiveMessage(MessageObject o) {
    	String commComponentName = CommManager.extractComponentName(o);
/* chc
 *         Vector propertyNames = null;

        Vector propertyValues = null;
        try {
            propertyNames = o.extractListValue("PROPERTYNAMES");
            propertyValues = o.extractListValue("PROPERTYVALUES");
        } catch (CommException e) {
            trace.out("bad message: " + e);
            e.printStackTrace();
            return;
        }
 */
//chc        String showHighlight = (String) MessageObject.getValue(propertyNames,
//chc                propertyValues, "Highlight");
        String showHighlight = (String) o.getProperty("Highlight");
        trace.out("--- showHighlight = " + showHighlight);
        if (showHighlight == null)
            return;
        if (showHighlight.equalsIgnoreCase("True")) {
            highlight(commComponentName, HintMessagesManagerForClient.defaultBorder);
        } else
            removeHighlight(commComponentName);
    }

    // ////////////////////////////////////////////////////
    /**
     * Add this widget to the comm manager to receive comm messages
     * 
     * @param controller2
     */
    // ////////////////////////////////////////////////////
    protected void addCommListener(TutorController controller2) {
    	addCommListener(commName, controller2);
    }

    // ////////////////////////////////////////////////////
    /**
     * Add this widget to the comm manager to receive comm messages
     * 
     * @param controller2
     */
    // ////////////////////////////////////////////////////
    protected void addCommListener(String componentName, TutorController controller2) {
    	if (VersionInformation.includesCL())
    		CommManager.addCommListener(componentName, this, controller2);
    }

    // ////////////////////////////////////////////////////
    /**
     * Return a vector of JCommWidget instances from a space-separated string
     * of commNames.
     */
    // ////////////////////////////////////////////////////
    public Vector getCommWidgets(String commNames) {
        if (!hasController())
            return null;
        Vector v = new Vector();
        StringTokenizer st = new StringTokenizer(commNames);
        while (st.hasMoreElements()) {
            JCommWidget t = getController().getCommWidget((String) st.nextElement());
            if (t == null)
                return null;
            v.addElement(t);
        }
        return v;
    }

    
    /**
     * Send a comm message containing the current value of the text field, if
     * the value has changed
     */
    public void sendValue() {
        if (!dirty)
            return;
        MessageObject mo = getCurrentStateMessage();
        if (trace.getDebugCode("dw")) trace.out("dw", "JCommWidget.sendValue("+mo+")");
        getUniversalToolProxy().sendMessage(mo);
        dirty = false;
    }

    // ////////////////////////////////////////////////////
    /**
     * Creates a vector of comm messages which describe the current state of
     * this object relative to the start state
     */
    // ////////////////////////////////////////////////////
    public Vector<MessageObject> getCurrentState() {
        Vector<MessageObject> v = new Vector<MessageObject>();
        v.addElement(getCurrentStateMessage());
        return v;
    }

    // ////////////////////////////////////////////////////
    /**
     * Returns a comm message which describes this interface element.
     * This default implementation simply calls
     * {@link #getDescriptionMessage()}. Subclasses that use the widgetName
     * to determine the proper InterfaceDescription message should override
     * this method. 
     * @param widgetName name as known to the {@link TutorController};
     *        unused in this implementation
     * @return result of {@link #getDescriptionMessage()}
     */
    // ////////////////////////////////////////////////////
    public MessageObject getDescriptionMessage(String widgetName) {
        return getDescriptionMessage();
    }

    // ////////////////////////////////////////////////////
    /**
     * Returns a comm message which describes this interface element.
     */
    // ////////////////////////////////////////////////////
    public MessageObject getDescriptionMessage() {
        return null;
    }
    /**
     * 
     * @param names
     * @param values
     */
	protected void serializeGraphicalProperties(MessageObject messageObject) {
		ComponentDescription cd = new ComponentDescription(this);
		// chc				cd.serializeGraphicalProperties(names, values);
		cd.serializeGraphicalProperties(messageObject);

	}
    public boolean getLock(String selection) {
        // default false
        return false;
    }

    // ////////////////////////////////////////////////////
    /**
     * Creates a comm message which describes the current state of this object
     */
    // ////////////////////////////////////////////////////
    public MessageObject getCurrentStateMessage() {
//        MessageObject mo = new MessageObject("NotePropertySet");
        MessageObject mo = MessageObject.create("InterfaceAction");
        mo.setVerb("NotePropertySet");
        if (!initialize(getController())) {
            trace.err("error: initialization failed.  Returning empty comm message");
            return mo;
        }
        
        Vector selection = new Vector();
        Vector action = new Vector();
        Vector input = new Vector();
        String currentAction = "";
        
        for (int i = 0; i < getCommSelectionVector().size(); i++) {
            JCommWidget dw = (JCommWidget) getCommSelectionVector().elementAt(i);
            if (dw == null) return null;
            selection.addElement(dw.getCommNameToSend());
            currentAction = dw.getActionName();
            action.addElement(currentAction);
            input.addElement(dw.getValue());
        }
        // For a hint message, include the previous widget to have focus
        if (!(currentAction.equalsIgnoreCase(UPDATE_ICON) || 
        		currentAction.equalsIgnoreCase(UPDATE_AUDIO) ||
        		currentAction.equalsIgnoreCase(UPDATE_TEXT) ||
        		currentAction.equalsIgnoreCase(UPDATE_BACKGROUNDCOLOR) ||
        		currentAction.equalsIgnoreCase(UPDATE_LIST)) &&
        		(commName.equalsIgnoreCase("Hint") || commName.equalsIgnoreCase("Help"))) {
            String previous = "";
            if (FocusModel.getLastFocus() != null) {
                previous = FocusModel.getLastFocus();
                // trace.out("previous = " + previous);
                JCommWidget d = hasController() ? getController().getCommWidget(previous) : null;
                // trace.out("d.getCommName() " + d.getCommName());
                // trace.out("d.getLock(previous) = " + d.getLock(previous));
                // check if the widget is locked
                if (d == null || d.getLock(previous)) previous = "";
                // trace.out("after call getLock: previous = " + previous);
            }
            if (previous.equals("")) previous = "Hint";
            selection.addElement(previous);
            action.addElement(HintMessagesManagerForClient.PREVIOUS_FOCUS);
        }

        mo.setSelection(selection);
        mo.setAction(action);
        mo.setInput(input);
        
        mo.setTransactionId(mo.makeTransactionId());
        
        return mo;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void setUpdateEachCycle(boolean update) {
        updateEachCycle = update;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public boolean getUpdateEachCycle() {
        return updateEachCycle;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public boolean isChangedFromResetState() {
        return false;
    }

    /**
     * Return a reference to the internal Swing or AWT component.
     * This base class implementation returns the first component
     * {@link #add(Component)}'d to this container.
     * @return {@link #getComponent(int) getComponent(0)}
     */
    protected Component getNativeComponent() {
    	Component result = null;
    	try {
    		result = getComponent(0);
    	} catch (ArrayIndexOutOfBoundsException aioobe) {}
		if (trace.getDebugCode("dw"))
			trace.out("getNativeComponent() => "+(result == null ? null :
				result.getClass().getSimpleName()+" "+result.getName()));
		return result;
	}
    
    /**
     * Equivalent to {@link Component#setForeground(Color) setForeground(fg)}
     * on {@link #getNativeComponent()}.
	 * @param fg the new foreground color
	 * @see javax.swing.JComponent#setForeground(java.awt.Color)
	 */
	public void setForeground(Color fg) {
		Component nc = getNativeComponent();
		if (nc != null)
			nc.setForeground(fg);
	}

	/**
	 * @return {@link Component#getForeground() getForeground()} from
	 *         {@link #getNativeComponent()}
	 * @see java.awt.Component#getForeground()
	 */
	public Color getForeground() {
		Component nc = getNativeComponent();
		return nc != null ? nc.getForeground() : null;
	}

	// ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void startHighlight(String subElement) {
        HighlightThread t = new HighlightThread(this, subElement);
        t.start();
    }

    public void setFocusable(boolean focuseFlag) {
        super.setFocusable(focuseFlag);
        Component[] allComponents = this.getComponents();
        Component tempComponent;
        for (int i = 0; i < allComponents.length; i++) {
            tempComponent = allComponents[i];
            tempComponent.setFocusable(focuseFlag);
        }
    }


    
    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////

    boolean highlighted = false;
    
    void toggleHighlight() {
	// Sat May 21 22:58:47 2005: Noboru
	// Toggle highlight a specified table cell
	
	if (isHighlighted()) {
	    removeHighlight();
	} else {
	    highlight();
	}
    }
    
    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void highlight(String subElement, Border border) {
    }

    public void highlight() {

	setOriginalBorder(getBorder());
	setBorder(HintMessagesManagerForClient.defaultBorder);
	setHighlighted(true);
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void removeHighlight(String subElement) {
        if (trace.getDebugCode("mps")) trace.out("mps", getClass().getSimpleName()+
        		".removeHighlight("+subElement+") is called");
        setBackground(backgroundNormalColor);
        repaint();
    }

    public void removeHighlight() {
        setBorder(originalBorder);
        repaint();
        setHighlighted(false);
    }

    // ////////////////////////////////////////////////////
    /**
     * @param controller2
     * 
     */
    // ////////////////////////////////////////////////////
    public abstract void reset(TutorController controller);

    public void unlockWidget() {
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void doCorrectAction(String selection, String input) {
    	if (trace.getDebugCode("dw")) trace.out("dw", "doCorrectAction: selection = " +
    			selection + " input = " + input);
    	fireStudentAction(new StudentActionEvent(this));
    }

    /**
     * Method called on receipt of 
     * @param selection
     * @param action
     * @param input
     */
    public void doCorrectAction(String selection, String action, String input) {
    	if (trace.getDebugCode("dw")) trace.out("dw", "doCorrectAction: selection = " +
    			selection + " action = " + action + " input = " + input);
    	if (SET_VISIBLE.equalsIgnoreCase(action))
    		setVisible(input);
    	else if (RESET.equalsIgnoreCase(action))
    		reset(getController());
    	else
    		doCorrectAction(selection, input);
    }

    /**
     * Tutor-accessible control for showing or hiding this widget.
     * @param input Boolean value
     */
    protected void setVisible(String input) {
		boolean v = Boolean.valueOf(input).booleanValue();
		setVisible(v);
	}

    /**
     * Make visible a real bean property. We already have {@link #setVisible(boolean)}.
     * @return {@link #isVisible()}
     */
    public boolean getVisible() {
    	return isVisible();
    }
	public void doLISPCheckAction(String selection, String input) {
    }
    
    public void doLISPCheckAction(String selection, String action, String input) {
    	doLISPCheckAction(selection, input);
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void doIncorrectAction(String selection, String input) {
    }
    
    public void doIncorrectAction(String selection, String action, String input) {
    	doIncorrectAction(selection, input);
    }


    // ////////////////////////////////////////////////////
    /**
     * The commName is the string used in Comm messages to refer to this
     * object.
     */
    // ////////////////////////////////////////////////////
    public String getCommName() {
    	if(trace.getDebugCode("inter"))
    		trace.out("inter", getClass().getSimpleName()+".getCommName() returns "+commName+";");
        return commName;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void removeCommWidgetName(String commName) {
        if (!hasController())
            return;
        getController().getCommWidgetTable().remove(commName);
    }

    // ////////////////////////////////////////////////////
    /**
     * Add a link in the widget table to the given name and this widget.
     */
    // ////////////////////////////////////////////////////
    public boolean addCommWidgetName(String commName) {
        if (!hasController())
            return false;
        getController().getCommWidgetTable().put(commName, this);
        return true;
    }

    // ////////////////////////////////////////////////////
    /**
     * Get the domrin name which will be sent to lisp. (Not necessarily the same
     * as which is displayed in the interface builder)
     */
    // ////////////////////////////////////////////////////
    public String getCommNameToSend() {
        return commName;
    }

    // ////////////////////////////////////////////////////
    /**
     * Return the current value of this widget. Used to get the current value
     * when sending a comm message.
     */
    // ////////////////////////////////////////////////////
    public Object getValue() {
        return null;
    }

    // ////////////////////////////////////////////////////
    /**
     * Used to process an InterfaceAction message
     */
    // ////////////////////////////////////////////////////
    public void doInterfaceAction(String selection, String action, String input) {
    }

    /**
     * Used to process an InterfaceDescription message
     * @param messageObject
     */
    public void doInterfaceDescription(MessageObject messageObject) {
    	ComponentDescription cd = new ComponentDescription(this);
    	cd.executeGraphicalProperties(messageObject);
    }

    // ////////////////////////////////////////////////////
    /**
     * commSelection determines what information is sent in comm messages to
     * lisp
     */
    // ////////////////////////////////////////////////////
    public void setCommSelection(String commSelection) {
        this.commSelection = commSelection;
    }

    // ////////////////////////////////////////////////////
    /**
     * commSelection determines what information is sent in comm messages to
     * lisp
     */
    // ////////////////////////////////////////////////////
    public String getCommSelection() {
        return commSelection;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public String getActionName() {
        return actionName;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    // public void setLocked(boolean locked) {
    // this.locked = locked;
    // }
    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void setCorrectColor(Color correctColor) {
        this.correctColor = correctColor;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public Color getCorrectColor() {
        return correctColor;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void setIncorrectColor(Color incorrectColor) {
        this.incorrectColor = incorrectColor;
    }
    
    /**
	 * @return the {@link #startColor}
	 */
	public Color getStartColor() {
		return startColor;
	}

	/**
	 * @param startColor new value for {@link #startColor}
	 */
	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}

	public void setLISPCheckColor(Color lispColor) {
    	this.LISPCheckColor = lispColor;
    }
    
    public Color getLISPCheckColor() {
    	return LISPCheckColor;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public Color getIncorrectColor() {
        return incorrectColor;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void setCorrectFont(Font correctFont) {
        this.correctFont = correctFont;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public Font getCorrectFont() {
        return correctFont;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void setIncorrectFont(Font incorrectFont) {
        this.incorrectFont = incorrectFont;
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public Font getIncorrectFont() {
        return incorrectFont;
    }

    public void setFocus(String subWidgetName) {
        requestFocus();
        return;
    }

    public void moveFocus() {
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void focusGained(FocusEvent e) {
        pact.CommWidgets.FocusModel.tookFocus(this);
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void focusLost(FocusEvent e) {
        // currentlyFocusedWidget = null;
    }

    public void setEditable(boolean setFlag) {
    }

    static public Font getDefaultFont() {
        return defaultFont;
    }
    
    public static void setDefaultFont(Font font)
    {
    	defaultFont = font;
    }

    public void addStudentActionListener(StudentActionListener l) {
        studentActionListeners.add(StudentActionListener.class, l);
    }

    public void removeStudentActionListener(StudentActionListener l) {
        studentActionListeners.remove(StudentActionListener.class, l);
    }

    public EventListener[] getStudentActionListener() {
        return this.studentActionListeners
                .getListeners(StudentActionListener.class);
    }

    public void fireStudentAction(StudentActionEvent e) {
        if (trace.getDebugCode("dw")) trace.out("dw", "Inside fireStudentAction: "+e);
        Object[] listeners = studentActionListeners.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            // System.out.println("studentListener: " + i + " :" +
            // listeners[i]);
            if (listeners[i] == StudentActionListener.class) {
                // System.out.println("calling studentActionPerformed:
                // JCommQuestion.java");
                ((StudentActionListener) listeners[i + 1])
                        .studentActionPerformed(e);
            }
        }
    }

    public void addIncorrectActionListener(IncorrectActionListener l) {
        studentActionListeners.add(IncorrectActionListener.class, l);
    }

    public void removeIncorrectActionListener(IncorrectActionListener l) {
        studentActionListeners.remove(IncorrectActionListener.class, l);
    }

    public void fireIncorrectAction(IncorrectActionEvent e) {
        Object[] listeners = studentActionListeners.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == IncorrectActionListener.class) {
                ((IncorrectActionListener) listeners[i + 1])
                        .incorrectActionPerformed(e);
            }
        }
    }

    // sanket@cs.wpi.edu
    /**
     * @return Returns #isHintBtn 
     */
    public boolean isHintBtn() {
        return isHintBtn;
    }
	
    /**
     * @param isHintBtn The isHintBtn to set.
     */
    public void setHintBtn(boolean isHintBtn) {
        this.isHintBtn = isHintBtn;
    }

    public void addProblemDoneListener(ProblemDoneListener l) {
        Object[] obj = this.studentActionListeners
                .getListeners(ProblemDoneListener.class);
        if (obj == null || obj.length == 0) {
            studentActionListeners.add(ProblemDoneListener.class, l);
            return;
        }
        // check to see if l is already added as a listener
        for (int i = 0; i < obj.length; i++) {
            if (obj[i].equals(l)) {
                return;
            }
        }
        studentActionListeners.add(ProblemDoneListener.class, l);
    }

    public void removeProblemDoneListener(ProblemDoneListener l) {
        studentActionListeners.remove(ProblemDoneListener.class, l);
    }

    public void removeAllProblemDoneListeners() {
        EventListener[] obj = this.studentActionListeners
                .getListeners(ProblemDoneListener.class);
        for (int i = 0; i < obj.length; i++) {
            this.studentActionListeners.remove(ProblemDoneListener.class,
                    (ProblemDoneListener) obj[i]);
        }
    }

    public void fireProblemDoneEvent(ProblemDoneEvent e) {
        Object[] listeners = studentActionListeners.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == ProblemDoneListener.class) {
                ((ProblemDoneListener) listeners[i + 1]).problemDone(e);
            }
        }
    }

    public void addHelpEventListener(HelpEventListener listener) {
        Object[] obj = this.studentActionListeners
                .getListeners(HelpEventListener.class);
        if (obj == null || obj.length == 0) {
            studentActionListeners.add(HelpEventListener.class, listener);
            return;
        }
        // check to see if l is already added as a listener
        for (int i = 0; i < obj.length; i++) {
            if (obj[i].equals(listener)) {
                return;
            }
        }
        studentActionListeners.add(HelpEventListener.class, listener);

    }

    public void removeHelpEventListener(HelpEventListener listener) {
        this.studentActionListeners.remove(HelpEventListener.class, listener);
    }

    public void removeAllHelpListeners() {
        EventListener[] obj = this.studentActionListeners
                .getListeners(HelpEventListener.class);
        for (int i = 0; i < obj.length; i++) {
            this.studentActionListeners.remove(HelpEventListener.class,
                    (HelpEventListener) obj[i]);
        }
    }

    /**
     * @param event
     */
    public void fireHelpEvent(HelpEvent event) {
        Object[] listeners = studentActionListeners.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == HelpEventListener.class) {
                ((HelpEventListener) listeners[i + 1]).helpSeeked(event);
            }
        }
    }

    protected void setCommSelectionVector(Vector commSelectionVector) {
        if (trace.getDebugCode("dw")) trace.out("dw", " set comm selection vector: "
                + commSelectionVector);
        this.commSelectionVector = commSelectionVector;
    }

    protected Vector getCommSelectionVector() {
        if (commSelectionVector==null)
            commSelectionVector = new Vector();
        return commSelectionVector;
    }

    public static void setController(TutorController _controller) {
        // trace.printStack("functions");
        controller = _controller;
    }
    
    public TutorController getController() {
        return controller;
    }
    private boolean hasController() {
        return getController()!=null;
    }
    
    public void setUniversalToolProxy(CommMessageHandler universalToolProxy) {
        this.universalToolProxy = universalToolProxy;
    }
    public CommMessageHandler getUniversalToolProxy() {
    	if(universalToolProxy != null)
    		return universalToolProxy;
        return (controller == null ? null : getController().getUniversalToolProxy());
    }
    
    public void mouseDoubleClickedWhenMissActive(MouseEvent me) {
        JCommWidget widget = (JCommWidget)me.getComponent();
        if (trace.getDebugCode("miss")) trace.out("miss", "JCommWidget.mouseDoubleClickedWhenMissActive(" + widget+ ")");
        widget.toggleHighlight();
        if(hasController())
        	getController().toggleWidgetFocusForSimSt(widget);
    }

    public Border getOriginalBorder() {
        return originalBorder;
    }

    public void setOriginalBorder(Border originalBorder) {
        this.originalBorder = originalBorder;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {

		// trace.out("dw", "Enter ModifyListsListener mouseClicked");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mouseReleased");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mouseEntered");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mouseExited");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mousePressed");
	}
	
	public static MessageObject createUntutoredActionMessage(Vector<String> s, Vector<String> a, Vector<String> in, UniversalToolProxy utp){
// chc		MessageObject mo = new MessageObject("NotePropertySet");
        MessageObject mo = MessageObject.create(MsgType.UNTUTORED_ACTION);
        mo.setVerb("NotePropertySet");
        mo.setSelection(s);
        mo.setAction(a);
        mo.setInput(in);
 		return mo;
	}

	/**
	 * Tell whether clicking on or tabbing into a given component should cause
	 * the component that lost focus to grade. Used by text fields, to determine
	 * when to submit characters typed so far as the student's attempt.
	 * @param gainedFocus component gaining focus
	 * @return true unless gainedFocus is a HintWindowInterface.HintButton
	 */
	public boolean focusTriggersBackGrading(Component gainedFocus) {
		if (gainedFocus instanceof AvoidsBackGrading)
			return false;
		return true;
	}
	
    enum C {
		lightGray(Color.LIGHT_GRAY), 	lightGreen(new Color(153,255,153)), 		lightBlue(new Color(153,153,255)), 
		lightRed(new Color(255,153,153)), lightPurple(new Color(255,153,255)), lightCyan(new Color(153,255,255)),
		lightYellow(new Color(255,255,153)),
		red(Color.RED), 	blue(Color.BLUE), 		green(Color.GREEN), 
		cyan(Color.CYAN), 	gray(new Color(170,170,170)), 		orange(Color.ORANGE), 
		black(Color.BLACK), yellow(Color.YELLOW), 	pink(Color.PINK),
		white(new Color(255,255,255))
		;
		
		private final Color color;

		// Constructor
		C(Color color) {
			this.color = color;
		}

		public static Color get(String name) {
			for (C a : C.values()) {
				if (a.toString().equals(name))
					return a.color;
			}
			return null;
		}
	}

    /**
     * @return reference to the top-level frame for the student interface
     */
    public Component getStudentInterfaceFrame() {
    	StudentInterfaceWrapper siw;
    	if (controller == null || (siw = controller.getStudentInterface()) == null)
    		return null;
    	return siw.getActiveWindow();
    }

    /**
     * @return true if {@link #getCommNameToSend()} is "Done" and a {@link JCommButton} 
     */
	public boolean isDoneButton() {
		return this instanceof JCommButton && "Done".equalsIgnoreCase(getCommNameToSend());
	}

    /**
     * Tell whether this student interface supports a given message type.
     * @param msgType value from {@link MsgType} constants
     * @return {@link Boolean#TRUE} if supported; else {@link Boolean#FALSE}
     */
	public static Boolean clientSupports(String msgType) {
		if(MsgType.StartStateMessages.equalsIgnoreCase(msgType))
			return Boolean.FALSE;
		return Boolean.TRUE;
	}
}
