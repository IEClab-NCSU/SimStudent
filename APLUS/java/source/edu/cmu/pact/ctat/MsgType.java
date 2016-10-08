/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;

/**
 * Utilities for testing message types. Meant to begin to consolidate this widely-scattered
 * information.
 */
public class MsgType {

	/** See {@link #isCorrectOrIncorrect(MessageObject)}. */
    private static final String[] correctIncorrect = new String[] { "CorrectAction", "IncorrectAction", "LISPCheckAction" };

	/** See {@link #hasTextFeedback(MessageObject)}. */
    private static final String[] textFeedbackTypes = new String[] {
        "ShowHintsMessage", "SuccessMessage", "BuggyMessage", "WrongUserMessage", "NoHintMessage", "HighlightMsg", "ShowHintsMessageFromLisp"
    };

	/** See {@link #isHintResponse(MessageObject)}. */
    private static final String[] hintResponseTypes = new String[] {
        "ShowHintsMessage", "NoHintMessage", "ShowHintsMessageFromLisp"
    };
    
    /** Selection name for Done step. */
    public static final String DONE = "Done";
    
    /** Action name for Done step. */
    public static final String BUTTON_PRESSED = "ButtonPressed";
    
    /** Property in initialization messages. */
	public static final String PROBLEM_NAME = "ProblemName";

    /**
	 * Tell whether the given message has "correct" or "incorrect" feedback.
	 * Tests as {@link #isMessageType(MessageObject, String[])} against {@link #correctIncorrect}:
	 * {@value #correctIncorrect}
	 * @param mo message to test
	 * @return true if type is "correct" or "incorrect" feedback
	 */
    public static boolean isCorrectOrIncorrect(MessageObject mo) {
		return mo.isMessageType(correctIncorrect);
	}

    /**
	 * Tell whether this object's message type is related to text feedback.
	 * Tests as {@link #isMessageType(MessageObject, String[])} against {@link #textFeedbackTypes}:
	 * {@value #textFeedbackTypes}.
	 * @param mo message to test
	 * @return true if hint related
	 */
    public static boolean hasTextFeedback(MessageObject mo) {
        return mo.isMessageType(textFeedbackTypes);
	}

    /**
     * Tell whether this message records the Done step. Tests selection against
     * {@link #DONE}
     * @param mo message to test
     * @return true if first selection element matches {@value #DONE}, case-insensitive
     */
    public static boolean isDoneMessage(MessageObject mo) {
    	Object sObj = mo.getProperty("Selection");
    	Object aObj = mo.getProperty("Action");
    	if (sObj == null || aObj == null)
    		return false;
    	String s = "";
    	if (!(sObj instanceof List))
    		s = sObj.toString();
    	else {
    		Object sElt = (((List) sObj).size() < 1 ? "" : ((List) sObj).get(0));
    		s = (sElt == null ? "" : sElt.toString());
    	}
    	String a = "";
    	if (!(aObj instanceof List))
    		a = aObj.toString();
    	else {
    		Object sElt = (((List) aObj).size() < 1 ? "" : ((List) aObj).get(0));
    		a = (sElt == null ? "" : sElt.toString());
    	}
    	return DONE.equalsIgnoreCase(s) && BUTTON_PRESSED.equalsIgnoreCase(a);
    }

    /**
	 * Tell whether this object's message type is related to text feedback.
	 * Tests as {@link #isMessageType(MessageObject, String[])} against {@link #textFeedbackTypes}:
	 * {@value #textFeedbackTypes}.
	 * @param mo message to test
	 * @return true if hint related
	 */
    public static boolean isHintResponse(MessageObject mo) {
        return mo.isMessageType(hintResponseTypes);
	}

	/**
	 * Block or delay student feedback messages according to
	 * {@link ProblemModel#getSuppressStudentFeedback()}.
	 * @param mo message
	 * @param suppress whether all feedback is suppressed
	 * @return whether to show, hide or delay
	 */
	public static FeedbackEnum suppressFeedback(MessageObject mo,
			FeedbackEnum suppress) {
        if (suppress == FeedbackEnum.SHOW_ALL_FEEDBACK)
        	return suppress;                                      // hide nothing
		if (MsgType.hasTextFeedback(mo)) {
	        if (suppress == FeedbackEnum.HIDE_ALL_FEEDBACK)
	        	return suppress;                                  // always hide
			String buggyMsg =
				(String) mo.getProperty(PseudoTutorMessageBuilder.BUGGY_MSG);
			if (BR_Controller.NOT_DONE_MSG.equalsIgnoreCase(buggyMsg))
				return FeedbackEnum.SHOW_ALL_FEEDBACK;  // display "you are not done"
			else
				return FeedbackEnum.HIDE_ALL_FEEDBACK;  // hide text--don't delay
		}
		if (MsgType.isCorrectOrIncorrect(mo)) {
			if (MsgType.isDoneMessage(mo))
				return FeedbackEnum.SHOW_ALL_FEEDBACK;
			else
				return suppress;                    // delay for delay, hide for hide
		}
		return FeedbackEnum.SHOW_ALL_FEEDBACK;
	}
	
	/*
	 * Please keep this list in alphabetical order.
	 */
	/** Complete tutor response to a student's step attempt or hint request. */
	public static final String ASSOCIATED_RULES = "AssociatedRules";

	/** Success message to accompany grading. */
	public static final String BUGGY_MESSAGE = "BuggyMessage";

	/** Rule engine's response to {@link MsgType#GO_TO_WM_STATE}. */
	public static final String CHANGE_WM_STATE = "ChangeWMState";

	/** Rule engine's response to {@link MsgType#SEND_ESE_GRAPH}. */
	public static final String CHECK_ALL_STATES_RESULT = "CheckAllStatesResult";

	/** UIEvent: tool records cognitive load. */
	public static final String COGNITIVE_LOAD = "CognitiveLoad";

	/** Inform the Tutoring Service about components in the student interface. */
	public static final String COMPONENT_INFO = "ComponentInfo";

	/** Ask the student whether he or she really wants to quit the tutor. */
	public static final String CONFIRM_DONE = "ConfirmDone";

	/** Grade as correct a student action on the student interface. */
	public static final String CORRECT_ACTION = "CorrectAction";
	
	/** Ask the student interface for all component settings, delivered as a bundle of {@value #INTERFACE_DESCRIPTION} msgs. */
	public static final String GET_ALL_INTERFACE_DESCRIPTIONS = "GetAllInterfaceDescriptions";

	/** UIEvent: student used the glossary. */
	public static final String GLOSSARY = "Glossary";

	/** Author commanded rule engine to trace across SAIs. */
	public static final String GO_TO_WM_STATE = "Go_To_WM_State";

	/** Tutor's "out of order" response, telling student to work on a different step. */
	public static final String HIGHLIGHT_MSG = "HighlightMsg";

	/** Grade as incorrect a student action on the student interface. */
	public static final String INCORRECT_ACTION = "InCorrectAction";
	
	/** Transmit a student (or tutor) action on the student interface. */
	public static final String INTERFACE_ACTION = "InterfaceAction";

	/** Capabilities and settings of a user interface component. */
	public static final String INTERFACE_DESCRIPTION = "InterfaceDescription";

	/** Tell the student interface to disconnect. */
	public static final String INTERFACE_FORCE_DISCONNECT = "InterfaceForceDisconnect";

	/** Client's initial handshake, creating or continuing a session. */
	public static final String INTERFACE_IDENTIFICATION = "InterfaceIdentification";

	/** Ask the client to restore initial settings and send {@link #INTERFACE_DESCRIPTION} for each component. */
	public static final String INTERFACE_REBOOT = "InterfaceReboot";

	/** Submit the student's action to the (rules-based) tutor. */
	public static final String LISP_CHECK =	"LISPCheck";

	/** Tell the student interface that the student's action has been submitted to the (rules-based) tutor. */
	public static final String LISP_CHECK_ACTION = "LISPCheckAction";

	/** Rule engine's response to student attempt or hint request. */
	public static final String LISP_CHECK_RESULT = "LispCheckResult";

	/** Tell the student interface that the problem has been loaded. */
	public static final String LOAD_BRD_FILE_SUCCESS = "LoadBRDFileSuccess";

	/** A list of messages bundled into one for communications efficiency. */
	public static final String MESSAGE_BUNDLE = "MessageBundle";
	
	/** {@link MessageObject#getProperty(String)} argument for messages in a bundle. */
	public static final String MESSAGES = "messages";

	/** Reply to a request for the next (more specific) hint on a step. */
	public static final String NEXT_HINT_MESSAGE = "NextHintMessage";

	/** Reply to a hint request with "no hint available." */
	public static final String NO_HINT_MESSAGE = "NoHintMessage";
	
	/** Reply to a request for the prior (less specific) hint on a step. */
	public static final String PREVIOUS_HINT_MESSAGE = "PreviousHintMessage";

	/** Notice from the student interface that no more problem-restore messages are coming. */
	public static final String PROBLEM_RESTORE_END = "ProblemRestoreEnd";

	/** Request from client for ProblemSummary. */
	public static final String PROBLEM_SUMMARY_REQUEST = "ProblemSummaryRequest";

	/** Reply from Tutoring Service containing ProblemSummary. */
	public static final String PROBLEM_SUMMARY_RESPONSE = "ProblemSummaryResponse";

	/** Tutoring service to clear and student interface. */
	public static final String RESET_ACTION = "ResetAction";

	/** Reset the rule engine working memory to the initial state. */
	public static final String RESTORE_INITIAL_WM_STATE = "RestorInitialWMState";

	/** Reset the Jess rule engine working memory to the initial state. */
	public static final String RESTORE_JESS_INITIAL_WM_STATE = "RestorJessInitialWMState";

	/** Send a message for the Retract Last Step action on a user interface. */
	public static final String RETRACT_STEPS = "RetractSteps";

	/** Test the rules on all steps in a subgraph. */
	public static final String SEND_ESE_GRAPH = "Send_ESEGraph";

	/** Lock any already-initialized student interface components. */
	public static final String SEND_WIDGET_LOCK = "SendWidgetLock";

	/** Change a mode in the tutoring service. */
	public static final String SET_MODE = "SetMode";

	/** Parameters for the tutoring service to select and run the next problem. */
	public static final String SET_PREFERENCES = "SetPreferences";

	/** Tutor response to display a hint. */
	public static final String SHOW_HINTS_MESSAGE = "ShowHintsMessage";
	
	/** Start an empty (new) problem. */
	public static final String START_NEW_PROBLEM = "StartNewProblem";
	
	/** Start of a Problem */
	public static final String START_PROBLEM = "StartProblem";
	
	/** Signal that BR is no longer accepting start state messages. */
	public static final String START_STATE_CREATED = "StartStateCreated";
	
	/** Last message in start state. */
	public static final String START_STATE_END = "StartStateEnd";
	
	/** Bundle name for start state message bundle. */
	public static final String StartStateMessages = "StartStateMessages";

	/** First message in start state. */
	public static final String STATE_GRAPH = "StateGraph"; 

	/** Success message to accompany grading. */
	public static final String SUCCESS_MESSAGE = "SuccessMessage";

	/** Tutor spontaneous message when a session needs to abort. */
	public static final String TUTORING_SERVICE_ERROR = "TutoringServiceError";

	/** Unlock the Java Composer widget. */
	public static final String UNLOCK_COMPOSER = "UnlockComposer";
	
	/** Provide version number to client. */
	public static final String UNTUTORED_ACTION = "UntutoredAction";	

	/** Provide version number to client. */
	public static final String VERSION_INFO = "VersionInfo";

	/** Property passing start state lock to components in {@value #SEND_WIDGET_LOCK} message. */
	public static final String WIDGET_LOCK_FLAG = "WidgetLockFlag";

	/** Tutor response saying that the latest step should be performed by a different actor. */
	public static final String WRONG_USER_MESSAGE = "WrongUserMessage";
}
