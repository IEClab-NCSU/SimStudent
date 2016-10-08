package edu.cmu.pact.client;

import java.util.Vector;

import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.ctat.MessageObject;

public interface HintMessagesManager {

	public static final String HINTS_MESSAGE = "HintsMessage";
	public static final String PREVIOUS_HINT_BUTTON = "PreviousHintButton";
	public static final String NEXT_HINT_BUTTON = "NextHintButton";
	public static final String CURRENT_HINT_NUMBER = "CurrentHintNumber";
	/** 
	 * Action vector element corresponding to Selection element that shows
	 * which widget had student's attention when Hint button was pressed.
	 */
	public static final String PREVIOUS_FOCUS = "PreviousFocus";
	public static final String NO_HINT_AVAILABLE_MSG = "No hint message is currently available.";
	public static final String SUCCESS_MESSAGE = "SuccessMessage";
	public static final String BUGGY_MESSAGE = "BuggyMessage";
	public static final String SHOW_HINTS_MESSAGE = "ShowHintsMessage";
	public static final String SHOW_HINTS_MESSAGE_FROM_LISP = "ShowHintsMessageFromLisp";
	public static final String WRONG_USER_MESSAGE = "WrongUserMessage";
	public static final String ASSOCIATED_RULES = "AssociatedRules";
	public static final String NO_HINT_MESSAGE = "NoHintMessage";
	public static final String TOTAL_HINTS_AVAIABLE = "TotalHintsAvailable";
	public static final String INCORRECT_ACTION = "IncorrectAction";
	public static final String CORRECT_ACTION = "CorrectAction";

	public void reset();

	public void setMessageObject(MessageObject o);

	public boolean hasPreviousMessage();

	public boolean hasNextMessage();

	/**
	 * Get the previous message in the sequence, if any. Logs request and response.
	 * @return text of previous message; null if at start of sequence
	 */
	public String getPreviousMessage();

	public String getFirstMessage();

	/**
	 * Get the next message in the sequence, if any. Logs request and response.
	 * @return text of next message; null if at end of sequence
	 */
	public String getNextMessage();

	/**
	 * Get a request for the next or previous hint.  To reflect the student
	 * interface state, the selection returned perhaps should be the widget
	 * with the current focus.  But the step analysis in the DataShop might
	 * depend on the selection being the highlighted widget(s) from the
	 * tutor's main hint response.
	 * @param next true if next, false if previous hint
	 * @return CommMessage for next-hint request
	 */
	public MessageObject getNextHintRequest(boolean next);

	/**
	 * Get the response to the next or previous hint request.
	 * @param next true if next, false if previous hint
	 * @return CommMessage for next-hint response
	 */
	public MessageObject getNextHintResponse(boolean next);

	public String getMessageType();

	public void resetHighlightWidgets();

	public void cleanUpHintOnChange();

	public void removeWidgetsHighlight();

	public void setWidgetFocus();

	public Vector getHighlightedWidgetsVector();

	// ////////////////////////////////////////////////////////////////////////
	/**
	 * called when the user click on "OK" buuton on HintsDialogWindow remove
	 * widgets highlight and set some widget focus
	 */
	// ////////////////////////////////////////////////////////////////////////
	public void dialogCloseCleanup();

	// ////////////////////////////////////////////////////
	/**
	 * set the special message to display. this method is called from
	 * MessageFrame.
	 */
	// ///////////////////////////////////////////////////
	public void setMessages(Vector messageVector);

	// ////////////////////////////////////////////////////
	/**
	 * used together with setMessages mType takes only values: SUCCESS_MESSAGE
	 * or BUGGY_MESSAGE
	 */
	// ///////////////////////////////////////////////////
	public void setMessageType(String mType);

	// ////////////////////////////////////////////////////////////
	/**
	 * Extracts a field position from a comm message vector
	 */
	// ////////////////////////////////////////////////////////////
	public int fieldPosition(Vector from, String fieldName);

	/**
	 * Receive a message via the {@link MessageEventListener} interface.
	 * @param msgEvt
	 * @see edu.cmu.pact.Utilities.MessageEventListener#messageEventOccurred(edu.cmu.pact.Utilities.MessageEvent)
	 */
	public void messageEventOccurred(MessageEvent msgEvt);

	/**
	 * Reference to the hint UI panel.
	 * @return {@link #hintInterface}
	 */
	public HintWindowInterface getHintInterface();

	/**
	 * Set the reference {@link #studentInterfaceWrapper} to the master object.
	 * @param studentInterfaceWrapper
	 */
	public void setStudentInterfaceWrapper(
			StudentInterfaceWrapper studentInterfaceWrapper);

	/**
	 * @param hintInterface new value for {@link #hintInterface}
	 */
	public void setHintInterface(HintWindowInterface hintInterface);

	/**
	 * Request a hint from the tutoring system.
	 */
	public void requestHint();

	public void requestDone();

}