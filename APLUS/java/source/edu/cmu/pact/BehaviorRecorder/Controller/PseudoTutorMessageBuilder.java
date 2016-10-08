/*
 * Created on Dec 3, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.util.List;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerSAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pslc.logging.element.SemanticEventElement;

public class PseudoTutorMessageBuilder {

	/**
	 * Indicator value in {@link #buildAssociatedRules(ProblemEdge, String, BR_Controller, String)}
	 * for incorrect evaluations.
	 */
	public static final String INCORRECT = "InCorrect";

	/**
	 * Indicator value in {@link #buildAssociatedRules(ProblemEdge, String, BR_Controller, String)}
	 * for correct evaluations.
	 */
	public static final String CORRECT = "Correct";

	/**
	 * Indicator value in {@link #buildAssociatedRules(ProblemEdge, String, BR_Controller, String)}
	 * for hint responses.
	 */
	public static final String HINT = "Hint";

	/** Property name for Correct/InCorrect value. */
	public static final String INDICATOR = "Indicator";
	
	/** Property name for student SAI in {@value MsgType#ASSOCIATED_RULES} messages. */
	public static final String STUDENT_SELECTION = "StudentSelection";
	
	/** Property name for student SAI in {@value MsgType#ASSOCIATED_RULES} messages. */
	public static final String STUDENT_ACTION = "StudentAction";
	
	/** Property name for student SAI in {@value MsgType#ASSOCIATED_RULES} messages. */
	public static final String STUDENT_INPUT = "StudentInput";

	/** Property name for working memory images. */
	public static final String WMIAGESS_PNAME = "WMImages";
	
	/** Property name for step identifier. */
    public static final String STEP_ID = "StepID";

    /** Property name for hint texts and success and buggy messages in AssociatedRules. */
	public static final String TUTOR_ADVICE = "TutorAdvice";

	/** Value for {@link SemanticEventElement} subtype indicating tutor performed step for student. */
	public static final String TUTOR_PERFORMED = "tutor-performed";

	/** Property name for subtype parameter of {@link SemanticEventElement} */
	public static final String SUBTYPE = "subtype";

	/** Property name for trigger parameter of {@link SemanticEventElement} */
	public static final String TRIGGER = "trigger";

	/** Trigger value for user-performed actions. */
	public static final String TRIGGER_USER = "USER";

	/** Trigger value for tutor-performed actions. */
	public static final String TRIGGER_DATA = "DATA";

	/** Property name for skill levels in AssociatedRules. */
	public static final String SKILLS_PNAME = "Skills";
	
	/** Previous native property name for skills. */
	public static final String RULES_PNAME = "Rules";

	/** Property name for text of {@value MsgType#BUGGY_MESSAGE}. */
	public static final String BUGGY_MSG = "BuggyMsg";

	/**
     * Construct the Comm AssociatedRules message with optional tool_selection.
     * 
     * @return -- Comm AssociatedRules message.
     */
    public static MessageObject buildAssociatedRules(ProblemEdge targetEdge,
            String indicatorValue, String actor, BR_Controller controller, String tool_selection) {
    	return buildAssociatedRules(targetEdge, null, null,
                indicatorValue, actor, controller, tool_selection);
    }
    
	/**
     * Construct the Comm AssociatedRules message with replacement input and optional tool_selection.
     * 
     * @return -- Comm AssociatedRules message.
     */
    public static MessageObject buildAssociatedRules(ProblemEdge targetEdge, Vector input,
    		ExampleTracerSAI studentSAI,
            String indicatorValue, String actor, BR_Controller controller, String tool_selection) {
    
        EdgeData targetEdgeData = targetEdge.getEdgeData();

        Vector selection = targetEdgeData.getMatcher().getDefaultSelectionVector();
        Vector action = targetEdgeData.getMatcher().getDefaultActionVector();
        if (input == null)
        	input = targetEdgeData.getMatcher().getDefaultInputVector();

        return buildAssociatedRules(targetEdge, selection, action, input,
				studentSAI, indicatorValue, actor, controller, tool_selection);
    }

	/**
	 * @param targetEdge
	 * @param selection
	 * @param action
	 * @param input
	 * @param studentSAI
	 * @param indicatorValue
	 * @param actor
	 * @param controller
	 * @param tool_selection
	 * @param targetEdgeData
	 * @return
	 */
	static MessageObject buildAssociatedRules(ProblemEdge targetEdge,
			Vector selection, Vector action, Vector input,
			ExampleTracerSAI studentSAI, String indicatorValue, String actor,
			BR_Controller controller, String tool_selection) {
	    
        EdgeData targetEdgeData = targetEdge.getEdgeData();
        Vector<String> hints = null;
		if (HINT.equalsIgnoreCase(indicatorValue)) {
			hints = targetEdgeData.getHints();
        	input = targetEdgeData.appendHintOrder(input);
		}

        Vector ruleNames = new Vector();
        Vector<String> skillBarVector = null;
        String skillBarDelimiter = null;
        if (!Matcher.isTutorActor(actor, false)) {
        	ruleNames = ProblemModel.getNamedRules(targetEdgeData.getSkills());
        	if (targetEdgeData.getProblemModel() != null) {
        		skillBarVector = targetEdgeData.getProblemModel().getSkillBarVector();
        		skillBarDelimiter = targetEdgeData.getProblemModel().getSkillBarDelimiter();
        	}
        }
        if (trace.getDebugCode("skills")) trace.out("skills", "buildAssocRules targetEdgeData.getProblemModel() pm "+
        		targetEdgeData.getProblemModel()+", actor "+actor+", skillBars\n "+skillBarVector);

        MessageObject newMessage = buildAssociatedRulesMsg(indicatorValue, selection,
        		action, input, studentSAI, ruleNames, skillBarVector, skillBarDelimiter,  
        		Integer.toString(targetEdge.getUniqueID()),
        		actor, controller, tool_selection, hints);

        return newMessage;
	}

    /**
     * Create an AssociatedRules message for the given result. Use this when
     * the ExampleTracerEvent object contains all the information needed.
     * @param result if not null, use {@link ExampleTracerEvent#getResult()} for CORRECT, etc.
     * @param controller
     */
    public static MessageObject buildAssocRulesFromEvent(ExampleTracerEvent result, BR_Controller controller) {
    	ExampleTracerLink link = result.getReportableLink();
    	EdgeData edgeData = (link == null ? null : link.getEdge());
    	Vector<String> skillNames = new Vector<String>(result.getSkillNames());
        Vector<String> skillBarVector = null;
        String skillBarDelimiter = null;
        if (edgeData != null && edgeData.getProblemModel() != null) {
        	skillBarVector = edgeData.getProblemModel().getSkillBarVector();
    		skillBarDelimiter = edgeData.getProblemModel().getSkillBarDelimiter();
        }
        boolean isHint = ExampleTracerTracer.HINT.equalsIgnoreCase(result.getResult());
        MessageObject newMessage =
        		PseudoTutorMessageBuilder.buildAssociatedRulesMsg(result.getResult(),
        				result.getTutorSelection(),
        				result.getTutorAction(),
        				isHint ? edgeData.appendHintOrder(result.getTutorInput()) : result.getTutorInput(),
        				isHint ? null : result.getStudentSAI(),
        				skillNames,      // ruleNames
        				skillBarVector,  // CTAT2974: don't send skills without pKnowns: generates NaN values with AS3
        				skillBarDelimiter,
        				Integer.toString(edgeData.getUniqueID()),
        				result.isTutorPerformed() ? Matcher.DEFAULT_TOOL_ACTOR : Matcher.DEFAULT_STUDENT_ACTOR,
        				controller,
        				(String) (result.getStudentSAI() == null ? null : result.getStudentSAI().getSelectionAsVector().get(0)),
        				result.getTutorAdvice());
        	return newMessage;
    }
	
	public static MessageObject buildAssociatedRulesMsg(String indicatorValue,
			Vector selection, Vector action, Vector input,
			ExampleTracerSAI studentSAI, Vector ruleNames,
			Vector<String> skillBarVector, String skillBarDelimiter, 
			String stepID, String actor, BR_Controller controller,
			String tool_selection, Vector<String> tutorAdvice) {

		MessageObject newMessage = MessageObject.create(MsgType.ASSOCIATED_RULES, "SendNoteProperty");
        Vector<String> emptyV = new Vector<String>();
        emptyV.add("");
        
        newMessage.setProperty(INDICATOR, fixIndicator(indicatorValue));

        newMessage.setSelection(selection == null ? emptyV : selection);
        newMessage.setAction(action == null ? emptyV : action);
        newMessage.setInput(input == null ? emptyV : input);
        
        if (studentSAI != null) {
            newMessage.setProperty(STUDENT_SELECTION,
            		studentSAI.getSelectionAsVector() == null ? emptyV : studentSAI.getSelectionAsVector());
            newMessage.setProperty(STUDENT_ACTION,
            		studentSAI.getActionAsVector() == null ? emptyV : studentSAI.getActionAsVector());
            newMessage.setProperty(STUDENT_INPUT,
            		studentSAI.getInputAsVector() == null ? emptyV : studentSAI.getInputAsVector());
        }

        if (tutorAdvice != null) {
			newMessage.setProperty(TUTOR_ADVICE, tutorAdvice);
			if (isHint(indicatorValue)) {
				newMessage.setProperty(HintMessagesManager.TOTAL_HINTS_AVAIABLE, tutorAdvice.size());
				newMessage.setProperty(HintMessagesManager.CURRENT_HINT_NUMBER,
						(tutorAdvice.size() > 0 ? 1 : 0));
			}
		}
        
        if (actor != null && actor.length() > 0)
        	newMessage.setProperty(Matcher.ACTOR, actor);

		if (ruleNames == null || ruleNames.size() < 1)
			ruleNames = s2v("unnamed");
		newMessage.setProperty(RULES_PNAME, ruleNames);
		
		if (skillBarVector != null && skillBarVector.size() > 0)
			newMessage.setProperty(SKILLS_PNAME, skillBarVector);
		newMessage.setProperty(MessageObject.SKILL_BAR_DELIMITER_TAG, skillBarDelimiter);
		
		newMessage.setProperty(STEP_ID, stepID);

		if (tool_selection != null && tool_selection.length() > 0)
			newMessage.setProperty("tool_selection", tool_selection);

		newMessage.setTransactionId(controller.getSemanticEventId());
		return newMessage;
	}

    /**
     * Construct the Comm Correct message
     * 
     * @return -- Comm Correct message.
     */
    
    public static MessageObject buildCommCorrectMessage(Vector selectionP,
            Vector actionP, Vector inputP, BR_Controller controller) {
    	if (trace.getDebugCode("js"))
    		trace.printStack("js", "buildCommCorrectMessage("+selectionP+","+actionP+","+inputP+")");
        MessageObject newMessage = MessageObject.create(MsgType.CORRECT_ACTION, "SendNoteProperty");
		newMessage.setSelection(selectionP);
		newMessage.setAction(actionP);
		newMessage.setInput(inputP);
        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }

    public static MessageObject createLockWidgetMsg(boolean lockFlag) {
    	MessageObject newMessage = MessageObject.create(MsgType.SEND_WIDGET_LOCK, "SendNoteProperty");
    	newMessage.setProperty(MsgType.WIDGET_LOCK_FLAG, Boolean.toString(lockFlag));
        return newMessage;
    }

    public static MessageObject buildUnLockMessage(Vector selection, BR_Controller controller) {
        MessageObject newMessage = MessageObject.create(MsgType.UNLOCK_COMPOSER, "SendNoteProperty");
		newMessage.setSelection(selection);
        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }

    public static MessageObject buildNoHintMessage(BR_Controller controller) {
        MessageObject newMessage = MessageObject.create(MsgType.NO_HINT_MESSAGE, "SendNoteProperty");
		newMessage.setProperty(STEP_ID, (new Integer(-1)).toString());
		newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }
    
    public static MessageObject buildCommHighLightWidgetMessage(Vector selectionP,
            Vector actionP, BR_Controller controller) {

        MessageObject newMessage = MessageObject.create(MsgType.HIGHLIGHT_MSG, "SendNoteProperty");
        ProblemModel pm = (controller == null ? null : controller.getProblemModel());
        String highlightMsgText = (pm == null || pm.getOutOfOrderMessage() == null ?
        		ProblemModel.DEFAULT_OUT_OF_ORDER_MESSAGE : pm.getOutOfOrderMessage());

        newMessage.setProperty("HighlightMsgText", highlightMsgText);
		newMessage.setSelection(selectionP);
		newMessage.setAction(actionP);

        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }
    public static MessageObject buildProtectToolStepMessage(Vector selectionP, 
    		Vector actionP, Vector inputP, BR_Controller controller){
    	trace.out ("build ProtectToolStep message message");
    	
        MessageObject newMessage = MessageObject.create(MsgType.WRONG_USER_MESSAGE, "SendNoteProperty");
		newMessage.setSelection(selectionP);
		newMessage.setInput(inputP);
        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    	
    }
    /**
     * Construct the Comm buggy message
     * 
     * @return -- Comm buggy message.
     */
    
    public static MessageObject buildCommBuggyMessage(String buggyMsg,
            Vector selectionP, Vector actionP,  BR_Controller controller) {

        MessageObject newMessage = MessageObject.create(MsgType.BUGGY_MESSAGE, "SendNoteProperty");
        newMessage.setProperty(BUGGY_MSG, buggyMsg);
        if (selectionP != null) {
			newMessage.setSelection(selectionP);
			newMessage.setAction(actionP);
        }
        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }

    /**
     * Construct the Comm incorrect message
     * 
     * @return -- Comm incorrect message.
     */
    
    public static MessageObject buildCommIncorrectMessage(Vector selectionP,
            Vector actionP, Vector inputP, BR_Controller controller) {

        MessageObject newMessage = MessageObject.create(MsgType.INCORRECT_ACTION, "SendNoteProperty");
        Vector<String> emptyV = new Vector<String>();
        emptyV.add("");

		newMessage.setSelection(selectionP == null ? emptyV : selectionP);
		newMessage.setAction(actionP == null ? emptyV : actionP);
		newMessage.setInput(inputP == null ? emptyV : inputP);

        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }

    /**
     * Construct the Comm success message
     * 
     * @return -- Comm success message.
     */

    public static MessageObject buildCommSuccessMessage(String successMsg, BR_Controller controller) {

        MessageObject newMessage = MessageObject.create(MsgType.SUCCESS_MESSAGE, "SendNoteProperty");
        newMessage.setProperty("SuccessMsg", successMsg);
        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }

    public static MessageObject buildHintsMsg(ProblemEdge hintsEdge, BR_Controller controller) {
        EdgeData hintsEdgeData = hintsEdge.getEdgeData();

        Vector hints = hintsEdgeData.getHints();
        Vector selection = hintsEdgeData.getSelection();
        Vector action = hintsEdgeData.getAction();
        Vector input = hintsEdgeData.getInput();
        
        String stepID = Integer.toString(hintsEdge.getUniqueID());

        Vector namedRules = ProblemModel.getNamedRules(hintsEdgeData.getSkills());

        Vector<String> skillBarVector = (hintsEdgeData.getProblemModel() == null ?
        		null : hintsEdgeData.getProblemModel().getSkillBarVector());

        return buildHintsMsg(hints, selection, action, input, stepID, namedRules,
        		skillBarVector, controller);
    }

    public static MessageObject buildHintsMsg(Vector hints, Vector selection,
    		Vector action, Vector input, String stepID, Vector namedRules,
    		Vector<String> skillBarVector, BR_Controller controller) {

        MessageObject newMessage = MessageObject.create(MsgType.SHOW_HINTS_MESSAGE, "SendNoteProperty");
        Vector<String> emptyV = new Vector<String>();
        emptyV.add("");

        newMessage.setProperty("HintsMessage", hints);

		newMessage.setSelection(selection == null ? emptyV : selection);
		newMessage.setAction(action == null ? emptyV : action);
		newMessage.setInput(input == null ? emptyV : input);
		
		newMessage.setProperty(STEP_ID, stepID);
    
        if (namedRules != null && namedRules.size() > 0)
            newMessage.setProperty(RULES_PNAME, namedRules);

		if (skillBarVector != null && skillBarVector.size() > 0)
			newMessage.setProperty(SKILLS_PNAME, skillBarVector);

        newMessage.setTransactionId(controller.getSemanticEventId());
        return newMessage;
    }

	public static MessageObject buildConfirmDoneMsg(BR_Controller controller) {

		MessageObject newMessage = MessageObject.create(MsgType.CONFIRM_DONE, "SendNoteProperty");
	    newMessage.setTransactionId(MessageObject.makeTransactionId());
		return newMessage;
	}

	/**
	 * Create an InterfaceAction message.
	 * @param selection
	 * @param action
	 * @param input
	 * @param controller if not null, uses for {@link BR_Controller#getSemanticEventId()}
	 * @return new message
	 */
	public static MessageObject buildInterfaceAction(
			Vector<String> selection, Vector<String> action,
			Vector<String> input, BR_Controller controller) {
		return buildInterfaceAction(selection, action, input, null, controller, null, false);
	}
	public static MessageObject buildInterfaceActionMsg(
			Vector<String> selection, Vector<String> action,
			Vector<String> input, BR_Controller controller) {
		return buildInterfaceActionMsg(selection, action, input, null, controller, null, false);
	}

	/**
	 * Create an InterfaceAction message.
	 * @param selection
	 * @param action
	 * @param input
	 * @param prompt if not null, display this string to request the student's next step
	 * @param controller if not null, uses for {@link BR_Controller#getSemanticEventId()}
	 * @param transactionId if not null, use this transactionId instead
	 * @param suppressLogging
	 * @return new message
	 */
	public static MessageObject buildInterfaceAction(
			Vector<String> selection, Vector<String> action, Vector<String> input,
			String prompt, BR_Controller controller, String transactionId, boolean suppressLogging) {

		MessageObject newMessage = 
			MessageObject.create(MsgType.INTERFACE_ACTION, "SendNoteProperty");

		newMessage.setSelection(selection);
		newMessage.setAction(action);
		newMessage.setInput(input);

		if (prompt != null)
			newMessage.setProperty("prompt", prompt);

		if (transactionId != null)
	    	newMessage.lockTransactionId(transactionId);
	    else if (controller != null)
	    	newMessage.setTransactionId(controller.getSemanticEventId());

		if (suppressLogging)
	    	newMessage.suppressLogging(true);
	    return newMessage;
	}
	
	public static MessageObject buildInterfaceActionMsg(
			Vector<String> selection, Vector<String> action, Vector<String> input,
			String prompt, BR_Controller controller, String transactionId, boolean suppressLogging) {
		MessageObject newMessage = MessageObject.create(MsgType.INTERFACE_ACTION, "SendNoteProperty");
        newMessage.setSelection(selection);
        newMessage.setAction(action);
        newMessage.setInput(input == null ? s2v("") : input);
        if (prompt != null)
        	newMessage.setProperty("prompt", prompt);
	    if (transactionId != null)
	    	newMessage.lockTransactionId(transactionId);
	    else if (controller != null)
	    	newMessage.setTransactionId(controller.getSemanticEventId());
	    if (suppressLogging)
	    	newMessage.suppressLogging(true);
	    return newMessage;
	}

    /**
     * Generate a {@link MsgType#INTERFACE_ACTION} message for tutor-performed steps.
     * @param selection 
     * @param action
     * @param input
     * @param trigger defaults to {@value #TRIGGER_DATA} if null or empty
     * @param subtype defaults to {@value #TUTOR_PERFORMED} if null or empty
     * @return new message
     */
    public static MessageObject buildToolInterfaceAction(Vector selection, Vector action, Vector input,
    		String trigger, String subtype) {
        MessageObject mo = MessageObject.create(MsgType.INTERFACE_ACTION, "NotePropertySet");
        mo.setSelection(selection);
        mo.setAction(action);
        mo.setInput(input);
        mo.setProperty(TRIGGER, (trigger == null || trigger.length() < 1 ? TRIGGER_DATA : trigger));
        mo.setProperty(SUBTYPE, (subtype == null || subtype.length() < 1 ? TUTOR_PERFORMED : subtype));
        mo.lockTransactionId(MessageObject.makeTransactionId());
        return mo;
	}

	/**
	 * @return StartStateEnd message
	 */
	public static MessageObject createStartStateEndMsg() {
		MessageObject newMessage = MessageObject.create("StartStateEnd", "NotePropertySet");
        return newMessage;
	}
	
	/**
	 * Convenience for creating vector from string
	 * @param s
	 * @return Vector with s as first element; null if s is null 
	 */
	public static Vector<String> s2v(String s) {
		if (s == null)
			return null;
		Vector<String> result = new Vector<String>();
		result.add(s);
		return result;
	}
	
	/**
	 * Convenience for getting string from vector or object 
	 * @param obj
	 * @return if v a vector, the first element as a string; else null
	 */
	public static String v2s(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof List) {
			List v = (List) obj;
			if (v.size() < 1 || v.get(0) == null)
				return null;
			return v.get(0).toString();
		}
		return obj.toString();
	}

	/**
	 * Tell whether an indicator value is a hint result. 
	 * @param indicatorObj
	 * @return true if indicatorObj.startsWith({@value #HINT}) ignoring case
	 */
	public static boolean isHint(Object indicatorObj) {
		if (indicatorObj == null)
			return false;
		String indicator = indicatorObj.toString().toLowerCase();
		return indicator.startsWith(PseudoTutorMessageBuilder.HINT.toLowerCase());
	}

	/**
	 * Tell whether an indicator value is a correct result. 
	 * @param indicatorObj
	 * @return true if indicatorObj.startsWith({@value #CORRECT}) ignoring case
	 */
	public static boolean isCorrect(Object indicatorObj) {
		if (indicatorObj == null)
			return false;
		String indicator = indicatorObj.toString().toLowerCase();
		if (indicator.startsWith(PseudoTutorMessageBuilder.CORRECT.toLowerCase()))
			return true;
		return indicator.startsWith(EdgeData.SUCCESS.toLowerCase());
	}

	/**
	 * Convert an indicator from the Solver or rules to the canonical form for logging.
	 * @param indicatorObj hint, correct, buggy, suboptimal, success, etc.
	 * @return one of {@value #HINT}, {@value #CORRECT} or {@value #INCORRECT}
	 */
	public static String fixIndicator(Object indicatorObj) {
		if (isHint(indicatorObj))
			return HINT;
		if (isCorrect(indicatorObj))
			return CORRECT;
		return INCORRECT;
	}

	/**
	 * Tell whether this AssociatedRules message reports a Done step. Also sets a
	 * "correct?" boolean according to {@link #isCorrect(Object) isCorrect(indicatorObj)}.
	 * @param assocRulesResp checks indicator and selection properties
	 * @param correct single-element array to receive an indicator as to whether
	 *        this Done step was correct (true) or incorrect (false)
	 * @return true if selection property matches "Done"
	 */
	public static boolean isDoneStep(MessageObject assocRulesResp, boolean[] correct) {
		correct[0] = false;
		Object indicatorObj = assocRulesResp.getProperty(PseudoTutorMessageBuilder.INDICATOR);
		Object selectionObj = assocRulesResp.getProperty("tool_selection");
		if (indicatorObj == null)
			return false;
		correct[0] = isCorrect(indicatorObj);
		boolean result = "Done".equalsIgnoreCase(v2s(selectionObj));
		if (trace.getDebugCode("ps"))
			trace.outNT("ps", "PseudoTutorMessageBuilder.isDoneStep() indicator "+indicatorObj+
					", tool_selection "+selectionObj+", selection "+assocRulesResp.getSelection()+
					"correct[0] "+correct[0]+", result "+result);
		return result;
	}

}
