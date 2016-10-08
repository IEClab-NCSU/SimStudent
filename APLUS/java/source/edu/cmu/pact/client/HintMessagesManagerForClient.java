/*
 * HintMessgaesManager.java
 *
 * Created on July 13, 2004, 10:46 AM
 */

package edu.cmu.pact.client;

/**
 * 
 * @author zzhang
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.net.URL;
//import java.util.Iterator;
import java.util.LinkedHashMap;
//import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.SimSt;

public class HintMessagesManagerForClient implements MessageEventListener, HintMessagesManager{

	// default highLight border
	public static final Border defaultBorder = BorderFactory.createLineBorder(
			Color.blue, 4);

	// Comm message type
    protected String type;

    // Comm message from Tutor or Pseudo-Tutor side
    protected MessageObject hintsMessageObject;

    // HTML format hint messages to display, each element is a String
    private Vector messages;

    // for the current displayed hint message
    private String currentMessage;

    // maximum hint message index: number of messages - 1
    private int maxMessageIndex;

    // current display hint message index, range: 0 to maxMessageIndex
    private int currentMessageIndex;

    // whether need highlight corresponding widgets
    protected boolean highlightFlag = true;

    // each element is a vector holding the current highlight widget names list
    protected Vector highlightWidgetNames = new Vector();

    /**
     * For recording information on a highlighted widget.
     */
    protected class HighlightWidget {
    	private final String selection;
    	private final Border border;
    	private final String action;
    	public HighlightWidget(String selection, Border border, String action) {
        	this.selection = selection;
        	this.border = border;
        	this.action = action;
    	}
    	public HighlightWidget(String selection, Border border, Vector actionV, int i) {
        	this.selection = selection;
        	this.border = border;
        	if (actionV == null || actionV.size() <= i)
        		this.action = null;
        	else {
        		String action = (String) actionV.get(i);
        		this.action = (action.length() < 1 ? null : action);
        	}
    	}
		String getAction() { return action; }
		Border getBorder() {	return border; }
		public String getSelection() { return selection; }
    }

    protected HighlightWidget highlightWidgetPair;

    protected String selectionName;
    
    /** Transaction identifier for previous- and next-hint responses. */
    private String transactionId;

    // name & widget should be in pair
    // each element is a highlightWidgetPair
    protected Vector currentHighlightNames = new Vector();

    // each element is a JCommWidget
    private Vector currentHighlightWidgets = new Vector();

    // temp
    private int mouseClickedNum = 0;

    private TutorController controller;

	private StudentInterfaceWrapper studentInterfaceWrapper;

	private HintWindowInterface hintInterface;

	/** Step identifier, for logging. */
	private String stepID;

    private static final String HIGHLIGHT_MESSAGE = "HighlightMsg";

    /** Creates a new instance of HintMessgaesManager */
    public HintMessagesManagerForClient(TutorController controller) {
        this.controller = controller;
        reset();
    }

    /**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#reset()
	 */
    public void reset() {
        highlightFlag = true;
        maxMessageIndex = -1;
        currentMessageIndex = -1;

        type = "";

        hintsMessageObject = null;

        messages = new Vector();
        currentMessage = "";
        highlightWidgetNames = new Vector();
        currentHighlightNames = new Vector();
        currentHighlightWidgets = new Vector();

        return;
    }
    
    /**
	 * @param o
	 * @see edu.cmu.pact.client.HintMessagesManager#setMessageObject(edu.cmu.pact.ctat.MessageObject)
	 */
    public void setMessageObject(MessageObject o) {
        reset();
        hintsMessageObject = o;
        parseMessages();
        mouseClickedNum = 0;

    }

    /**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#hasPreviousMessage()
	 */
    public boolean hasPreviousMessage() {
        return (currentMessageIndex > 0);
    }

    /**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#hasNextMessage()
	 */
    public boolean hasNextMessage() {
        return (currentMessageIndex < maxMessageIndex);
    }

    void oliLog(MessageObject mo, boolean tutorToTool) {
    	if(controller==null)
    		return;
    	LoggingSupport loggingSupport = controller.getLoggingSupport();
    	if (loggingSupport != null)
    		loggingSupport.oliLog(mo, tutorToTool);
    }

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getPreviousMessage()
	 */
    public String getPreviousMessage() {
    
        if (this.hasPreviousMessage()) {
        	MessageObject mo = getNextHintRequest(false); // false: previous, not next
            oliLog(mo, false);  // false: tool to tutor

            currentMessageIndex--;
            currentMessage = (String) messages.elementAt(currentMessageIndex);

            mo = getNextHintResponse(false);              // false: previous, not next
            oliLog(mo, true);    // true: tutor to tool
            return currentMessage;
        }
        return null;
    }

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getFirstMessage()
	 */
	public String getFirstMessage() {
		if (maxMessageIndex >= 0) {
			currentMessageIndex = 0;
			currentMessage = (String) messages.elementAt(currentMessageIndex);
			return currentMessage;
		}
		return null;
	}

    /**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getNextMessage()
	 */
	public String getNextMessage() {
        if (hasNextMessage()) {
        	MessageObject mo = getNextHintRequest(true);
            oliLog(mo, false);
            
            currentMessageIndex++;
            currentMessage = (String)messages.elementAt(currentMessageIndex);

            mo = getNextHintResponse(true);
            oliLog(mo, true);

            return currentMessage;
        }
        return null;
    }

    /**
     * Convert the list {@link #currentHighlightNames} into a vector of
     * widget names suitable for logging.
     * @param action optional vector for actions
     * @return vector of selection names from the widget pairs
     */
    private Vector getHighlightedWidgets(Vector action) {
    	Vector result = new Vector();
    	for (int i = 0; i < currentHighlightNames.size(); ++i) {
    		HighlightWidget v = (HighlightWidget)currentHighlightNames.get(i);
    		result.add(v.getSelection());
    		if (v.getAction() != null && action != null)
    			action.add(v.getAction());
    	}
		return result;
	}

    /**
	 * @param next
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getNextHintRequest(boolean)
	 */
    public MessageObject getNextHintRequest(boolean next) {
    	MessageObject mo = MessageObject.create(MsgType.INTERFACE_ACTION, "NotePropertySet");
    	Vector<String> selection = new Vector<String>();
        Vector<String> action = new Vector<String>();
        Vector<String> input = new Vector<String>();
        Vector hw = getHighlightedWidgets(null);

        selection.addElement(next ? NEXT_HINT_BUTTON : PREVIOUS_HINT_BUTTON);
        action.addElement(JCommButton.BUTTON_PRESSED);
        input.addElement("-1");
        if (hw!=null && hw.size()>0 && hw.get(0) != null) {
        	selection.addElement(hw.get(0).toString());
        	action.addElement(PREVIOUS_FOCUS);
        }    	
    	mo.setSelection(selection);
    	mo.setAction(action);
    	mo.setInput("-1");

    	mo.setTransactionId(mo.makeTransactionId());
    	setTransactionId(mo.getTransactionId());
    	return mo;
    }
    
     /**
	 * @param next
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getNextHintResponse(boolean)
	 */
    public MessageObject getNextHintResponse(boolean next) {
    	String msgType = (next ? MsgType.NEXT_HINT_MESSAGE : MsgType.PREVIOUS_HINT_MESSAGE);
        MessageObject mo = MessageObject.create(msgType, "SendNoteProperty");
        mo.setProperty(CURRENT_HINT_NUMBER,  currentMessageIndex+1);
        mo.setProperty(TOTAL_HINTS_AVAIABLE,  maxMessageIndex+1);

        Vector selection = getHighlightedWidgets(null);
        if (selection == null || selection.size() < 1)
        	selection = getHintMessageSelection();
        if (selection != null && selection.size() > 0)
        	mo.setProperty("Selection", selection);
        Vector action = getHintMessageAction();
    	if (action != null && action.size()>0)
    		mo.setProperty("Action", action);
    	Vector input = (Vector) getHintMessageProperty("Input");
    	if (input != null && input.size()>0)
    		mo.setProperty("Input", input);

    	/* Not a new step, so no skill update on next or previous hint. */
    	Vector skills = (Vector) getHintMessageProperty("Rules");
    	if (skills != null && skills.size()>0)
    		mo.setProperty("Rules", skills);
    	if (stepID != null)
    		mo.setProperty("StepID", stepID);

    	mo.setProperty(HINTS_MESSAGE, currentMessage);

        if (getTransactionId() != null)
        	mo.setTransactionId(getTransactionId());

        return mo;
	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getMessageType()
	 */
	public String getMessageType() {
        return this.type;
    }

    /**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#resetHighlightWidgets()
	 */
    public void resetHighlightWidgets() {
        trace.out("mps", "resetHighlightWidgets(): highlightFlag = "
                + highlightFlag + " type = " + type);
        if (!highlightFlag)
            return;

        if (type.equalsIgnoreCase(SHOW_HINTS_MESSAGE_FROM_LISP)) {
            if (currentHighlightWidgets.size() > 0)
                removeCurrentHighlights();

            currentHighlightNames = (Vector) highlightWidgetNames
                    .elementAt(currentMessageIndex);
        }

        setCurrentHighlights();

        return;
    }

    void removeCurrentHighlights() {
    	if(trace.getDebugCode("inter"))
    		trace.printStack("inter", "HMMFC.removeCurrentHighlights() currentHighlightWidgets "+
    				currentHighlightWidgets);
        for (int i = 0; i < currentHighlightWidgets.size(); i++) {
            JCommWidget d = (JCommWidget) currentHighlightWidgets
                    .elementAt(i);

            highlightWidgetPair = (HighlightWidget) currentHighlightNames.elementAt(i);
            selectionName = highlightWidgetPair.getSelection();
            d.removeHighlight(selectionName);
        }

        currentHighlightWidgets = new Vector();
    }

    protected Border getHighlightBorder(String colorString) {
        Color color = Color.blue;
        Border border;

        if (colorString.equalsIgnoreCase("Blue"))
            color = Color.blue;
        else if (colorString.equalsIgnoreCase("Red"))
            color = Color.red;
        else if (colorString.equalsIgnoreCase("Yellow"))
            color = Color.yellow;
        else if (colorString.equalsIgnoreCase("Green"))
            color = Color.green;
        else if (colorString.equalsIgnoreCase("Black"))
            color = Color.black;
        else if (colorString.equalsIgnoreCase("DarkGray"))
            color = Color.darkGray;
        else if (colorString.equalsIgnoreCase("Cyan"))
            color = Color.cyan;
        else if (colorString.equalsIgnoreCase("Magenta"))
            color = Color.magenta;
        else if (colorString.equalsIgnoreCase("Orange"))
            color = Color.orange;
        else if (colorString.equalsIgnoreCase("Gray"))
            color = Color.gray;
        else if (colorString.equalsIgnoreCase("Pink"))
            color = Color.pink;
        else if (colorString.equalsIgnoreCase("LightGray"))
            color = Color.lightGray;
        else
            trace.out(5, this, "Not recognizied color String: " + colorString);

        border = BorderFactory.createLineBorder(color, 4);

        return border;
    }

    protected JCommWidget selectedWidget() {
        return controller==null ? null : controller.getCommWidget(selectionName);
    }
    
    private void setCurrentHighlights() {

    	if(trace.getDebugCode("mps"))
    		trace.out("mps", "HMMFC.setCurrentHighlights() currentHighlightWidgets "+currentHighlightWidgets+
    				", currentHighlightNames "+currentHighlightNames);

    	if (currentHighlightWidgets.size() > 0)
            return;

        currentHighlightWidgets = new Vector();

        for (int i = 0; i < currentHighlightNames.size(); i++) {
            highlightWidgetPair = (HighlightWidget) currentHighlightNames.elementAt(i);
            selectionName = (String)highlightWidgetPair.getSelection();

            JCommWidget selectedWidget = selectedWidget();

            if (trace.getDebugCode("mps"))
            	trace.out("mps", "highlight widget name: selectionName="+selectionName+
            			", selectedWidget "+selectedWidget);
            if (selectedWidget!=null) {
                selectedWidget.highlight(selectionName, (Border) highlightWidgetPair.getBorder());
                if (!type.equalsIgnoreCase(SUCCESS_MESSAGE) &&
                    !type.equalsIgnoreCase(BUGGY_MESSAGE))
                    selectedWidget.setFocus(selectionName);
                currentHighlightWidgets.addElement(selectedWidget);
            } else
                // name & widget should be in pair
                currentHighlightNames.removeElementAt(i);
        }

        return;
    }

    /**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#cleanUpHintOnChange()
	 */
    public void cleanUpHintOnChange() {
        removeWidgetsHighlight();
        if (controller!=null &&
            controller.getStudentInterface()!=null &&
            controller.getStudentInterface().getHintInterface()!=null)
            controller.getStudentInterface().getHintInterface().reset();
    }

    private LinkedHashMap<MouseEvent, Integer> mouseEvents = new LinkedHashMap<MouseEvent, Integer>(); 
    
    // Tue Mar 28 22:53:11 2006 Noboru
    // Added MouseEvent as an argument to capture double-clicking on a JCommWidget
    // to get focus of attention for SimStudent
    /**
	 * @param me
	 * @see edu.cmu.pact.client.HintMessagesManager#tutorWindowClicked(java.awt.event.MouseEvent)
	 */
    public void tutorWindowClicked(MouseEvent me) {
    	Integer count = mouseEvents.get(me);
    	if(count == null)
    		count = new Integer(1);
    	else
    		count = new Integer(count.intValue()+1);
    	mouseEvents.put(me, count);
    	if(mouseEvents.size() > 100) {
    		MouseEvent oldest = mouseEvents.keySet().iterator().next();
    		mouseEvents.remove(oldest);
    	}
    		
    	if(trace.getDebugCode("inter"))
    		trace.outNT("inter", "HMMFC.tutorWindowClicked() mouseClickedNum "+mouseClickedNum+", count "+count);
    	mouseClickedNum++;

        // Trap double clicking when SimStudent is avtive
        if (me.getClickCount()==2 &&
                controller!=null &&
                controller.isSimStudentMode()) {
        	SimSt simSt = controller.getMissController().getSimSt();
        	//Ignore double clicking to select Foa if Foa clicking is disabled and a
        	//different method of getting Foa is defined (so SimSt is not locked up by
        	//having no method of getting Foa)
        	if(simSt.isSsFoaClickDisabled() && simSt.isFoaGetterDefined())
        	{
        		return;
        	}
        	
            Component c = me.getComponent();
            Class cc = c.getClass();
            if (cc.equals(TableExpressionCell.class)) {
                c = ((TableExpressionCell)c).getTable();
                ((JCommWidget)c).mouseDoubleClickedWhenMissActive(me);
                c = null;
            }
            
            while (c!=null) {
                if (c instanceof JCommWidget) {
                    ((JCommWidget)c).mouseDoubleClickedWhenMissActive(new MouseEvent(c, me.getID(), me.getWhen(), me.getModifiers(),
                            me.getX(), me.getY(), me.getClickCount(), me.isPopupTrigger()));
                    c = null;
                } else
                    c = c.getParent();
            }
        } else if(count.intValue() < 2 && mouseClickedNum > 1)
            removeWidgetsHighlight();
    }

    /**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#removeWidgetsHighlight()
	 */
    public void removeWidgetsHighlight() {
    	
    	if(trace.getDebugCode("inter"))
    		//trace.printStack("inter", "HMMFC.removeWidgetsHighlight() type "+type+
    		trace.out("inter", "HMMFC.removeWidgetsHighlight() type "+type+
    				", currentHighlightWidgets "+currentHighlightWidgets);

        JCommWidget selectedWidget = null;
        String widgetName = "";

        if (type.equalsIgnoreCase("BuggyMessage")) {
            String CommButtonClassName = "pact.CommWidgets.JCommButton";
            String className;

            for (int i = 0; i < currentHighlightWidgets.size(); i++) {
                selectedWidget = (JCommWidget) currentHighlightWidgets
                        .elementAt(i);
                className = selectedWidget.getClass().getName();
                if (className.equalsIgnoreCase(CommButtonClassName)) {
                    selectedWidget.removeHighlight("");
                    selectedWidget.moveFocus();
                }
            }
        }

        // remove highlights on other widgets
        for (int i = 0; i < currentHighlightWidgets.size(); i++) {
            selectedWidget = (JCommWidget) currentHighlightWidgets
                    .elementAt(i);
            highlightWidgetPair = (HighlightWidget) currentHighlightNames.elementAt(i);
            widgetName = highlightWidgetPair.getSelection();
            selectedWidget.removeHighlight(widgetName);
        }

        if (type.equalsIgnoreCase("ShowHintsMessage") && messages.size() == 1
                && widgetName.equalsIgnoreCase("Done")) {
            boolean flagSpecialMsg = false;

            String hintMessage = (String) messages.elementAt(0);

            if (hintMessage.equalsIgnoreCase(TutorController.NOT_DONE_MSG)
                    || hintMessage
                            .equalsIgnoreCase(TutorController.NOT_ALLOW_DONE_HINTS_MSG)
                    || hintMessage.equalsIgnoreCase(NO_HINT_AVAILABLE_MSG))
                flagSpecialMsg = true;

            if (flagSpecialMsg && selectedWidget != null)
                selectedWidget.moveFocus();
        }

        currentHighlightWidgets = new Vector();  // FIXME clear currentHighlightNames too?

        return;
    }

    /**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#setWidgetFocus()
	 */
    public void setWidgetFocus() {
        // upon Chang's request: set the focus on the first widget
        if (currentHighlightWidgets.size() > 0) {
            // set the focus on the first widget
            JCommWidget selectedWidget = (JCommWidget) currentHighlightWidgets
                    .elementAt(0);
            highlightWidgetPair = (HighlightWidget) currentHighlightNames.elementAt(0);
            String widgetName = highlightWidgetPair.getSelection();
            selectedWidget.setFocus(widgetName);

            String hintMessage = (String) messages.elementAt(0);

            if (hintMessage.equalsIgnoreCase(TutorController.NOT_DONE_MSG)
                    || hintMessage
                            .equalsIgnoreCase(TutorController.NOT_ALLOW_DONE_HINTS_MSG)
                    || hintMessage.equalsIgnoreCase(NO_HINT_AVAILABLE_MSG))
                selectedWidget.moveFocus();
        }

        return;
    }

    /**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getHighlightedWidgetsVector()
	 */
    public Vector getHighlightedWidgetsVector() {
        return currentHighlightWidgets;
    }

    // ////////////////////////////////////////////////////////////////////////
    /**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#dialogCloseCleanup()
	 */
    // ////////////////////////////////////////////////////////////////////////
    public void dialogCloseCleanup() {
        if (currentHighlightWidgets.size() > 0) {
            removeWidgetsHighlight();

            setWidgetFocus();
        }

        reset();

        return;
    }

    protected Object getHintMessageProperty(String propertyName) {
        return hintsMessageObject.getProperty(propertyName);
    }

    protected Vector getHintMessageVector(String propertyName) {
 //       return (Vector)getHintMessageProperty(propertyName);
    	Object result = getHintMessageProperty(propertyName);
    	
    	Vector vResult = new Vector();
    	if (result == null) {
    		vResult.add("null");
    		return vResult;
    	} 
    	else if (result instanceof Vector) 
    		return (Vector) result;
    	else {
    		vResult.add(result.toString());
    		return vResult;
    	}

    }

    protected Vector getHintMessageSelection() {
         return getHintMessageVector("Selection");
    	
    }

    protected Vector getHintMessageAction() {
        return getHintMessageVector("Action");
    }
    
    protected boolean parseMessages() {
        // process buggy msg
        type = hintsMessageObject.getMessageType();

        trace.out("inter", "hint message = " + hintsMessageObject);

        if (type.equalsIgnoreCase(HIGHLIGHT_MESSAGE)) {
            processHighlightMessage();
            return true;
        }

        if (type.equalsIgnoreCase(SUCCESS_MESSAGE)) {
            processSuccessMessage();
            return true;
        }

        if (type.equalsIgnoreCase(BUGGY_MESSAGE)) {
            processBuggyMessage();
            return true;
        }

        if (type.equalsIgnoreCase(NO_HINT_MESSAGE)) {
            processNoHintMessage();
            return true;
        }
        if (type.equalsIgnoreCase(WRONG_USER_MESSAGE)){
        	processWrongUserMessage();
            return true;
    	}
        if (type.equalsIgnoreCase(CORRECT_ACTION)) {
        	processCorrectOrIncorrectMessage(true);
        	return true;
        }
        if (type.equalsIgnoreCase(INCORRECT_ACTION)) {
        	processCorrectOrIncorrectMessage(false);
        	return true;
        }
        if (type.equalsIgnoreCase(ASSOCIATED_RULES)) {
        	processAssociatedRulesMessage();
        	return true;
        }
        // for "ShowHintsMessage" & "ShowHintsMessageFromLisp"
        
        Vector v = getHintMessageVector(HINTS_MESSAGE);

        if (v==null) {
            messages.addElement(NO_HINT_AVAILABLE_MSG);
        } else {
            trace.out("inter", "HintsMessage vector = " + v);
            messages = new Vector();
            for (String hint: (Vector<String>)v)
                messages.addElement(cleanedUpHint(hint));
        }

        maxMessageIndex = messages.size() - 1;

        if (type.equalsIgnoreCase(SHOW_HINTS_MESSAGE)) {
            processShowHintsMessage();
            return true;
        }

        /** parsing highlight widgets */
    /*    if (type.equalsIgnoreCase(SHOW_HINTS_MESSAGE_FROM_LISP)) {
            processShowHintsMessageFromLisp();
        }*/

        return false;
    }

    private void processAssociatedRulesMessage() {
    	stepID = (String) getHintMessageProperty("StepID");
    	if (true)
    		return;  // rest disabled: redundant with ShowHintMessage
		Boolean correct = false;
		Object indicatorProperty = getHintMessageProperty("Indicator");
		if (indicatorProperty instanceof String)
			correct = "CORRECT".equalsIgnoreCase((String) indicatorProperty);
    	Object tutorAdviceProperty = getHintMessageProperty("TutorAdvice");
    	if (tutorAdviceProperty instanceof Vector) {
    		messages.addElement(cleanedUpHint((String)((Vector)tutorAdviceProperty).get(0)));
    		if (correct)
    			highlightSelection();
    	} else if (tutorAdviceProperty instanceof String) {
    		messages.addElement(cleanedUpHint((String) tutorAdviceProperty));
    		if (correct)
    			highlightSelection();
    	} else {
    		messages.addElement(cleanedUpHint(composeMessageFromEvaluation(correct)));
    	}
        maxMessageIndex = messages.size() - 1;
	}



    private void processShowHintsMessage() {
    	stepID = (String) getHintMessageProperty("StepID");

        Vector selectionNamesVector = getHintMessageSelection();
        Vector actionVector = getHintMessageAction();

        if (selectionNamesVector==null) {
            highlightFlag = false;
            return;
        }

        trace.out(5, this, "selectionPart.size() = " + selectionNamesVector.size());

        if (selectionNamesVector.size()==0) {
            highlightFlag = false;
            return;
        }

        if (selectionNamesVector.get(0)==""){         	
        	 highlightFlag = false;
             return;
        }          
        
        for (int i=0; i<selectionNamesVector.size(); i++) {
            selectionName = (String)selectionNamesVector.elementAt(i);
            if (selectedWidget()!=null)
                currentHighlightNames.addElement(new HighlightWidget(selectionName, defaultBorder, actionVector, i));
        }
    }

    /**
     * 
     */
    private void processNoHintMessage() {
        messages.addElement(NO_HINT_AVAILABLE_MSG);
        maxMessageIndex = messages.size() - 1;

        highlightFlag = false;
    }
    
    /**
     * Compose a feedback message from a CorrectAction response.
     * @param true for correct, false for incorrect
     */
    private void processCorrectOrIncorrectMessage(boolean correct) {
        //messages.addElement(cleanedUpHint(composeMessageFromEvaluation(correct)));
        maxMessageIndex = messages.size() - 1;
        highlightFlag = false;
    }
    
    private String composeMessageFromEvaluation(boolean correct) {
    	StringBuffer msg = new StringBuffer();
    //	msg.append(correct ? "Correct response" : "Incorrect response"); //sorry, Bruce doesn't want this ...
    	Vector selection = getHintMessageSelection();
    	if (correct && selection != null && selection.size() > 0)
    		msg.append(" in component ").append(selection.get(0));
    	return msg.toString();
    }

    private final static String WrongUserMessage = "Your answer might be right. However, this is not your task. Please work on your part.";
    private void processWrongUserMessage() {
        messages.addElement(cleanedUpHint(WrongUserMessage));
        maxMessageIndex = messages.size() - 1;
        highlightFlag = false;
    }

    private void processBuggyMessage() {
        messages.addElement(cleanedUpHint((String)getHintMessageProperty("BuggyMsg")));
        maxMessageIndex = messages.size() - 1;

        Vector selectionPart = getHintMessageSelection();
        Vector actionPart = getHintMessageAction();

        // biuld highlight widget names list
        if (selectionPart != null) {
            for (int i = 0; i < selectionPart.size(); i++) {
                selectionName = (String) selectionPart.elementAt(i);
                if (selectedWidget()!=null)
                    currentHighlightNames.addElement(new HighlightWidget(selectionName, defaultBorder, actionPart, i));
            }
        } else
            highlightFlag = false;
    }

    /**
     * Add an action member to a highlightWidgetPair vector
     * @param highlightWidgetPair vector to update
     * @param actionPart source vector
     * @param i index in source vector
     */
    private void addActionToHighlightWidgetPair(Vector highlightWidgetPair,
    		Vector actionPart, int i) {
    	if (highlightWidgetPair == null)
    		return;
    	if (actionPart == null || actionPart.size() <= i)
    		return;
    	String action = (String) actionPart.get(i);
    	if (action.length() < 1)
    		return;
    	highlightWidgetPair.add(action);
	}

    private void processSuccessMessage() {
        highlightFlag = false;
        String msgText = (String)getHintMessageProperty("SuccessMsg");
        if (msgText == null || msgText.trim().length() < 1)
        	msgText = composeMessageFromEvaluation(true);
        messages.addElement(cleanedUpHint(msgText));
        maxMessageIndex = messages.size() - 1;
    }

    private void processHighlightMessage() {
    	if (!highlightSelection())
    		return;

        String highlightMsgText = (String)getHintMessageProperty("HighlightMsgText");

        if (highlightMsgText == null)
            return;

        if (highlightMsgText == "")
            return;

        messages.addElement(highlightMsgText);

        maxMessageIndex = messages.size() - 1;

        return;
    }

    private boolean highlightSelection() {
        Vector selection = getHintMessageVector("selection");
        Vector action = getHintMessageVector("action");

        if (selection == null || action == null)
            return false;

        String actionString;

        currentHighlightNames = new Vector();

        for (int i = 0; i < selection.size(); i++) {
            selectionName = (String) selection.elementAt(i);

            if (selectionName == null)
                continue;

            JCommWidget selectedWidget = selectedWidget();

            if (selectedWidget==null)
                continue;

            actionString = (String) action.elementAt(i);

            if (actionString == null)
                continue;

            if (!(actionString.equalsIgnoreCase("ButtonPressed") || actionString
                    .equalsIgnoreCase("UpdateCheckBox"))) {
                selectedWidget.setFocus(selectionName);
                highlightWidgetPair = new HighlightWidget(selectionName,
                		defaultBorder, actionString);
                currentHighlightNames.addElement(highlightWidgetPair);
            }
        }
        return (currentHighlightNames.size() > 0);
	}


    // ////////////////////////////////////////////////////
    /**
	 * @param messageVector
	 * @see edu.cmu.pact.client.HintMessagesManager#setMessages(java.util.Vector)
	 */
    // ///////////////////////////////////////////////////
    public void setMessages(Vector messageVector) {
        this.messages = messageVector;
        this.maxMessageIndex = messages.size() - 1;
        this.currentMessageIndex = -1;

        // special messages, don't need highlight widget now.
        highlightFlag = false;
        currentHighlightWidgets = new Vector();
        currentHighlightNames = new Vector();

        return;
    }

    // ////////////////////////////////////////////////////
    /**
	 * @param mType
	 * @see edu.cmu.pact.client.HintMessagesManager#setMessageType(java.lang.String)
	 */
    // ///////////////////////////////////////////////////
    public void setMessageType(String mType) {
        this.type = mType;
    }

    private String cleanedUpHint(String hintStr) {
        String cleanedUp = replaceImg(hintStr);
        return cleanedUp.replaceAll("\n", "<BR>");
    }
    
    /**
     * zz moved from Sanket class MessageFrame
     * 
     * replace the image path in the <img src="imagepath"> with
     * getClass().getResource()
     * 
     * @param message
     * @return
     */
    private String replaceImg(String message) {
        StringBuffer messageBuffer = new StringBuffer(message);
        Matcher matcher;
        // match the <img src= "" > text
        Pattern pattern = Pattern.compile("<\\s*img.*src\\s*=[^>]*");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            String imgPath = message.substring(matcher.start(), matcher.end());
            int start = matcher.start();
            // extract the path between the ""
            pattern = Pattern.compile("\".*\"");

            matcher = pattern.matcher(imgPath);
            if (matcher.find()) {
                int imgStart = start + matcher.start();
                int end = imgStart + matcher.end();
                String tempStr = matcher.group();
                tempStr = tempStr.replaceAll("\"", "");
                URL url = getClass().getResource(tempStr);
                if (url != null)
                    messageBuffer.replace(imgStart, end, url.toString());
            } else {
                // When we insert the <img src = "blah"> text in the hint
                // message in
                // the production rules, the " " are stripped off the hint
                // messages
                // when they are sent to the interface. Hence extract the image
                // path
                // even if the " " are not present
                int imgStart = -1, imgEnd = -1;

                // trace.addDebugCode("sdc");
                // trace.out("sdc", "********** Image path before extracting
                // path: " + imgPath + " ********");

                StringTokenizer st = new StringTokenizer(imgPath, " ");
                String nextToken;
                // split the path at each space character
                while (st.hasMoreTokens()) {
                    nextToken = st.nextToken();
                    int index;

                    // trace.addDebugCode("sdc");
                    // trace.out("sdc", "********** nextToken: " + nextToken + "
                    // ********");

                    if (nextToken.startsWith("src")
                            || nextToken.startsWith("SRC")
                            || nextToken.startsWith("Src")) {

                        // trace.addDebugCode("sdc");
                        // trace.out("sdc", "********** startsWith src
                        // ********");

                        if (nextToken.endsWith("=")) {

                            // trace.addDebugCode("sdc");
                            // trace.out("sdc", "********** ends with =
                            // ********");

                            // there is no space between the src and '=' ie. the
                            // image path is specified as <img src= blah>
                            if (st.hasMoreTokens()) {

                                // trace.addDebugCode("sdc");
                                // trace.out("sdc", "********** image path: " +
                                // imgPath + " ********");

                                imgStart = message.indexOf("src=") + 5;
                                imgPath = st.nextToken();
                                imgEnd = imgStart + imgPath.length();

                                // trace.addDebugCode("sdc");
                                // trace.out("sdc", "********** image path: " +
                                // imgPath + " imgStart: " + imgStart +
                                // "********");

                                break;
                            }
                        } else if ((index = nextToken.indexOf("=")) != -1) {

                            // there is no space between src, '=' and the image
                            // path ie. image path is specified as <img
                            // src=blah>
                            int tempIndex = message.indexOf(nextToken);
                            imgStart = tempIndex + index + 1;
                            imgPath = nextToken.substring(index + 1, nextToken
                                    .length());
                            imgEnd = imgStart + imgPath.length();

                            // trace.addDebugCode("sdc");
                            // trace.out("sdc", "********** x image path: " +
                            // imgPath + " ********");

                            break;
                        } else {
                            // get the index of "src" in the image path
                            int tempIndex = message.indexOf(nextToken);
                            // check to see if the next token is '=' ie. the
                            // image path is specified as <img src = blah >
                            nextToken = st.nextToken();
                            if (nextToken.equals("=")) {
                                // then the next token is the path
                                if (st.hasMoreTokens()) {
                                    // get the index of the first '=' after
                                    // 'src'
                                    imgStart = tempIndex
                                            + imgPath.indexOf(nextToken,
                                                    tempIndex) + 2;
                                    imgPath = st.nextToken();
                                    imgEnd = imgStart + imgPath.length();
                                    break;
                                }
                            }
                        }
                    }
                }
                // trace.addDebugCode("sdc");
                // trace.out("sdc", "********** Image path after extracting
                // path: " + imgPath + " ********");
                // replace the relative path with the absolute path to the image
                URL url = getClass().getResource(imgPath);
                if (url != null && imgStart > -1 && imgEnd > -1) {
                    messageBuffer.replace(imgStart, imgEnd, url.toString());
                }
            }
        }
        // trace.addDebugCode("sdc");
        // trace.out("sdc", "********** Message after replacing image path: " +
        // messageBuffer.toString() + " ********");

        return messageBuffer.toString();
    }

    // ////////////////////////////////////////////////////
    /**
     * Extract the desired value from propertyValues and return it
     * 
     * @param propertyNames
     *            Property name vector from Comm message
     * @param propertyValues
     *            Property value vector from Comm message
     * @param propertyName
     *            The property name of the value being sought
     * 
     * The property value requested, or null if not found
     */
    // ////////////////////////////////////////////////////
    Object getValue(Vector propertyNames, Vector propertyValues, String propertyName) {
        int pos = fieldPosition(propertyNames, propertyName);
        // trace.out ("property name = " + propertyName + " position = " + pos
        // );
        // trace.out ("property value = " + propertyValues.elementAt (pos));

        if (pos != -1)
            return propertyValues.elementAt(pos);

        return null;
    }

    // ////////////////////////////////////////////////////////////
    /**
	 * @param from
	 * @param fieldName
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#fieldPosition(java.util.Vector, java.lang.String)
	 */
    // ////////////////////////////////////////////////////////////
    public int fieldPosition(Vector from, String fieldName) {
        int toret = -1;
        int s = from.size();
        for (int i = 0; i < s; i++)
            if (((String) from.elementAt(i)).equalsIgnoreCase(fieldName))
                return i;
        return toret;
    }

	/**
	 * @return the {@link #transactionId}
	 */
	private String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId new value for {@link #transactionId}
	 */
	private void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @param msgEvt
	 * @see edu.cmu.pact.client.HintMessagesManager#messageEventOccurred(edu.cmu.pact.Utilities.MessageEvent)
	 */
	public void messageEventOccurred(MessageEvent msgEvt) {
		try {
			if (msgEvt.isQuitMsg())     // should remove listener?
				return;
			MessageObject mo = (MessageObject) msgEvt.getMessage(); 
			if (mo.isMessageType(StudentInterfaceWrapper.cleanUpMessages))
				cleanUpHintOnChange();
			if (MsgType.hasTextFeedback(mo) || mo.isMessageType(StudentInterfaceWrapper.tutorEvaluationMessages)) {
				setMessageObject(mo);
				String message = getFirstMessage();
				if (trace.getDebugCode("msg"))
					trace.out("msg", "Printing: " + message + " for type: " + mo.getMessageType());
				getHintInterface().showMessage(message);
			}
		} catch (Exception e) {
			trace.err("Error on "+msgEvt+": "+e+(e.getCause() == null ? "" : "; cause "+e.getCause()));
			e.printStackTrace();
		}
		
	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getHintInterface()
	 */
	public HintWindowInterface getHintInterface() {
		return hintInterface;
	}

	private StudentInterfaceWrapper getStudentInterfaceWrapper(){
		if(studentInterfaceWrapper==null)
			setStudentInterfaceWrapper(controller.getStudentInterface());
		return studentInterfaceWrapper;
	}
	/**
	 * @param studentInterfaceWrapper
	 * @see edu.cmu.pact.client.HintMessagesManager#setStudentInterfaceWrapper(pact.CommWidgets.StudentInterfaceWrapper)
	 */
	public void setStudentInterfaceWrapper(StudentInterfaceWrapper studentInterfaceWrapper) {
		this.studentInterfaceWrapper = studentInterfaceWrapper;
	}

	/**
	 * @param hintInterface
	 * @see edu.cmu.pact.client.HintMessagesManager#setHintInterface(edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface)
	 */
	public void setHintInterface(HintWindowInterface hintInterface) {
		this.hintInterface = hintInterface;
	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#requestHint()
	 */
	public void requestHint() {
		getStudentInterfaceWrapper().requestHint();
	}
	
	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#requestDone()
	 */
	public void requestDone() {
		getStudentInterfaceWrapper().requestDone();
	}
}
