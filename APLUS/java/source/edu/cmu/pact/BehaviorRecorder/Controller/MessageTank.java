package edu.cmu.pact.BehaviorRecorder.Controller;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import pact.CommWidgets.RemoteToolProxy;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerSAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.SocketProxy.HTTPToolProxy;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.HTTPMessageObject;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.ProblemSummary.CompletionValue;

public class MessageTank {
	
	public static final String END_OF_TRANSACTION = "end_of_transaction";
	
    private BR_Controller controller;
	
    /** Holds the response Comm messages */
    private LinkedHashMap<MessageObject,ExampleTracerEvent> messageTank;

    private String transaction_id = null;

	/** Selection (of Selection-Action-Input) value last received from student. */
	private Vector<String> lastStudentSelection;
	
	/** Action value last received from student **/
	private Vector<String> lastStudentAction;

	/** True if the tank is logically empty. */
	private volatile boolean tankEmpty = true;

	/** To turn off logging: use with care. Values true & false have effect; no-op if null. */
	private Boolean suppressLogging = null;
    
	//////////////////////////////////////////////////////
	/**
		Constructor
	*/
	//////////////////////////////////////////////////////
	public MessageTank(BR_Controller controller) {
        this.controller = controller;
        messageTank = new LinkedHashMap<MessageObject,ExampleTracerEvent>();
        tankEmpty = true;
	}

	/**
	 * Collect results from other messages into the "AssociatedRules" message.
	 * @param ps summary results to update
	 */
	public void consolidateMessageTank(ProblemSummary ps) {
		List<MessageObject> respondMsgs = getMessageTank();

		if (trace.getDebugCode("br")) trace.out("br", "consolidateMessageTank() nMsgs "+respondMsgs.size());
		if (trace.getDebugCode("br")) trace.out("br", "============================================");

		MessageObject associatedSkillsMsg = null;
		String success = "";
		String buggy = "";

		for (MessageObject msg : respondMsgs) {
			if (trace.getDebugCode("br")) trace.out("br", "respond message == " + msg.toString());

			if (msg.getMessageType().equals("AssociatedRules")) {
				associatedSkillsMsg = msg;
			} else {
				if (msg.getMessageType().equals("SuccessMessage"))
					success = (String) msg.getProperty("SuccessMsg");
				else {
					if (msg.getMessageType().equals("BuggyMessage"))
						buggy = (String) msg.getProperty("BuggyMsg");

					if (msg.getMessageType().equals("HighlightMsg"))
						buggy = (String) msg.getProperty("HighlightMsgText");

					if (msg.getMessageType().equals("NotDoneMessage"))
						buggy = (String) msg.getProperty("Message");
				}
			}
		}

		if (associatedSkillsMsg != null) {
			if (!success.equals(""))
				associatedSkillsMsg.setProperty("TutorAdvice", success);
			else if (!buggy.equals(""))
				associatedSkillsMsg.setProperty("TutorAdvice", buggy);
			associatedSkillsMsg.setProperty("LogAsResult", Boolean.toString(true));
			if (trace.getDebugCode("br")) trace.out("br", "* associatedSkillsMsg == "+ associatedSkillsMsg.toString());
			updateProblemSummary(ps, associatedSkillsMsg);
		}

		if (trace.getDebugCode("br")) trace.out("br", "============================================");


	}
	
	/**
	 * Send all the messages in the tank to the student interface.
	 * @param ps if not null, problem summary to update
	 */
	public synchronized void flushMessageTank(ProblemSummary ps) {
		flushMessageTank(ps, true);
	}
	
	/**
	 * A comparator to sort success, buggy, no-hint and out-of-order messages to the tail of a list.
	 */
	private static class MessageTankComparator implements Comparator<MessageObject> {
		
		/** Message types to defer. */
		private static HashSet<String> msgTypesToDefer = new HashSet<String>(Arrays.asList(
				new String[] {"SuccessMessage", "BuggyMessage", "HighlightMsg", "NotDoneMessage"}));

		/**
		 * Sort success, buggy, no-hint and out-of-order messages to the tail.
		 * @param o1 left-hand operand of < relation
		 * @param o2 right-hand operand
		 * @return -1, 0 or 1 if o1 precedes, equals or follows o2, respectively
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(MessageObject o1, MessageObject o2) {
			String m1 = o1.getMessageType();
			String m2 = o2.getMessageType();
			if (msgTypesToDefer.contains(m1))
				return msgTypesToDefer.contains(m2) ? 0 : 1;   // o2 <= o1
			if (msgTypesToDefer.contains(m2))
				return msgTypesToDefer.contains(m1) ? 0 : -1;  // o1 <= o2
			return 0;
		}
	}
	
	/**
	 * Send all the messages in the tank to the student interface.
	 * @param ps if not null, problem summary to update
	 * @param endOfTransaction set to false if this is not the end of the transaction
	 */
	public synchronized void flushMessageTank(ProblemSummary ps, boolean endOfTransaction) {
		if (tankEmpty)
			return;
		consolidateMessageTank(ps);
		
		List<MessageObject> respondMsgs = getMessageTank();
		Collections.sort(respondMsgs, new MessageTankComparator());  // defer success, buggy msgs, etc.

		/** sewall 2011/03/15 excise suppressed messages before setting END_OF_TRANSACTION */
		ProblemModel pm = (controller == null ? null : controller.getProblemModel());
		FeedbackEnum suppress = (pm  == null ? FeedbackEnum.SHOW_ALL_FEEDBACK
				: controller.getProblemModel().getSuppressStudentFeedback());
		for (ListIterator<MessageObject> it = respondMsgs.listIterator(); it.hasNext();) {
			MessageObject msg = it.next();
			FeedbackEnum dispose = MsgType.suppressFeedback(msg, suppress);
			switch (dispose) {
			case SHOW_ALL_FEEDBACK:
				continue;
			case DELAY_FEEDBACK:
				addToDelayedFeedbackTank(msg);
				it.remove();
	            if (trace.getDebugCode("br"))
	            	trace.out("br", "MessageTank hid "+msg.getMessageType());
				break;
			case HIDE_ALL_FEEDBACK:
				it.remove();
	            if (trace.getDebugCode("br"))
	            	trace.out("br", "MessageTank discarded "+msg.getMessageType());
				break;
			default:
				continue;
			}
		}
		
        // send group of msgs
        MessageObject msg;
        for (int i = 0; i < respondMsgs.size(); i++) {
            msg = respondMsgs.get(i);         // .elementAt(i);
            if (transaction_id != null)
            	msg.setTransactionId(transaction_id);
            boolean setEndOfTx = (endOfTransaction && (i >= respondMsgs.size()-1));
            msg.setProperty(END_OF_TRANSACTION, Boolean.toString(setEndOfTx));
            updateSessionInfo(msg, setEndOfTx);
            if (trace.getDebugCode("br")) trace.out("br", "transaction_id "+transaction_id+", respond message\n " + msg.toString());
            processInterfaceVariables(msg);
            sendMessage(msg, setEndOfTx);
        }
        transaction_id = null;  // sewall 2011/11/27: avoid dupl transaction_id in confirmDone
        tankEmpty = true;
        if (trace.getDebugCode("tank")) trace.out("tank", "From flushMessageTank");
        controller.getWidgetSynchronizedLock().releaseLock();
	}

	/**
	 * Determine whether to call {@link SingleSessionLauncher#getLauncherServer()}'s
	 * end-of-problem housekeeping methods.
	 * @param msg response message
	 * @param endOfTx true if this is the last response in a transaction
	 */
	private void updateSessionInfo(MessageObject msg, boolean endOfTx) {
        if (!endOfTx || !controller.inTutoringServiceMode())
        	return;
        SingleSessionLauncher launcher = controller.getLauncher();
        if (launcher == null || launcher.getLauncherServer() == null) {
        	if(Utils.isRuntime())
        		trace.err("SingleSessionLauncher "+launcher+" or LauncherServer "+
        				launcher.getLauncherServer()+" in tutoring service");
        	return;
        }
        boolean[] correct = new boolean[1];
        boolean isDoneStep = PseudoTutorMessageBuilder.isDoneStep(msg, correct);
        if (isDoneStep && correct[0]) {
        	launcher.getLauncherServer().updateTransactionInfo(launcher.getSessionId(), Boolean.TRUE);
        	launcher.endCollaboration();
        }
	}

	private HashMap<String, MessageObject> delayedFeedback = null;
	
	/**
	 * Add this message to the delayed feedback tank, creating it if necessary.
	 * @param msg
	 */
	private void addToDelayedFeedbackTank(MessageObject msg) {
		Vector<String> selection = msg.getSelection();
		if (selection == null)
			return;
		if (delayedFeedback == null)
			delayedFeedback = new LinkedHashMap<String, MessageObject>(); 
		delayedFeedback.put(selection.toString(), msg);
	}

	/**
	 * For selected messages, set selection variables to input values in the
	 * ProblemModel's variable table.
	 * @param msg
	 */
	private void processInterfaceVariables(MessageObject msg) {
		String msgType = msg.getMessageType();
		if (msgType == null)
			return;
		msgType = msgType.toLowerCase(); 
		if (msgType.startsWith("correct")
				|| msgType.startsWith("interfaceaction"))
			controller.processInterfaceVariables(msg);
	}

	/**
	 * Update the summary counts for this problem according to the results of the 
	 * current transaction
	 * @param ps
	 * @param assocRulesResp currently must be AssociatedRules msg 
	 */
    private void updateProblemSummary(ProblemSummary ps, MessageObject assocRulesResp) {
    	if (trace.getDebugCode("ps"))
    		trace.outNT("ps", "MessageTank.updateProblemSummary() "+assocRulesResp);
		if (ps == null || assocRulesResp == null)
			return;

    	ProblemModel pm = (controller == null ? null : controller.getProblemModel());
		FeedbackEnum ssfb = (pm == null ? 
				FeedbackEnum.SHOW_ALL_FEEDBACK : pm.getSuppressStudentFeedback());
		ExampleTracerEvent evt = messageTank.get(assocRulesResp);

		Object stepIDobj = assocRulesResp.getProperty(PseudoTutorMessageBuilder.STEP_ID);
		if (stepIDobj == null)
			return;                                        
		String stepID = stepIDobj.toString();
		Object indicatorObj =
			assocRulesResp.getProperty(PseudoTutorMessageBuilder.INDICATOR);
    	if (evt != null && evt.isSolverResult()) {
    		stepID = evt.getSolverStepID(stepID);
    		indicatorObj = evt.getResult();
    	}
    	
    	// The completion status could change to 'complete' when the student presses
    	// Done with feedback suppressed. If the student then replies "no" to the
    	// ConfirmDone prompt, then we'll reset the completion status to 'incomplete'
    	// with the very next request. 
    	CompletionValue cv = CompletionValue.incomplete;
		ps.setCompletionStatus(cv, pm.getEffectiveConfirmDone());
    	boolean correct[] = new boolean[1];
    	boolean doneStep = PseudoTutorMessageBuilder.isDoneStep(assocRulesResp, correct);
    	if (doneStep) {
    		cv = (correct[0] ? CompletionValue.complete : CompletionValue.incomplete);
    		if (ssfb == FeedbackEnum.HIDE_ALL_FEEDBACK)
    			cv = CompletionValue.complete;
    		if (trace.getDebugCode("ps")) 
    			trace.out("ps", "stepID "+stepID+" doneStep, correct="+correct[0]+
    					", suppressFeedback="+ssfb+", completion="+cv);
    		ps.setCompletionStatus(cv, true);
    	}
    	if (cv == CompletionValue.complete)
			ps.stopTimer();  // sewall 2012/12/04: stop timer here: could finish on tutor-performed step
		else
			ps.restartTimer();

    	Object actor = assocRulesResp.getProperty(Matcher.ACTOR);
		if (actor != null && actor.toString().toLowerCase().startsWith("t"))
			return;  // sewall 2012/12/04: don't let tutor-performed steps affect the problem summary
    	
    	if (PseudoTutorMessageBuilder.isHint(indicatorObj)) {
			ps.addHint(stepID);
    	} else if (!PseudoTutorMessageBuilder.isCorrect(indicatorObj)) {
			ps.addError(stepID);
    	} else if (indicatorObj != null) {
			ps.addCorrect(stepID);
		}
		if (trace.getDebugCode("ps")) 
			trace.out("ps", "stepID "+stepID+", indicator "+indicatorObj+", ps:\n  "+ps.toXML());
		if (controller != null)
			controller.updateSkillsConsole(assocRulesResp);
	}

    /**
     * Send a message by forwarding it through the {{@link #controller}.
     * @param newMessage
     * @param endOfTx true if this is the last message in the transaction
     */
	private void sendMessage(MessageObject newMessage, boolean endOfTx) {
		if (controller.isRestoringProblemState(newMessage.getTransactionId()))
			newMessage.suppressLogging(true);
		else if (suppressLogging != null)
			newMessage.suppressLogging(suppressLogging.booleanValue());
		if(requestMessage instanceof HTTPMessageObject) {
			sendMessageHTTP(newMessage, endOfTx);
			return;
		}
		Map.Entry<String, String> bundle = getCurrentBundle();
		if (bundle == null)
			controller.handleMessageUTP(newMessage);
		else
			controller.bundleMessage(newMessage, bundle.getKey(), bundle.getValue());
    }
	
	private void sendMessageHTTP(MessageObject newMessage, boolean flush) {
		HTTPToolProxy htp = ((HTTPMessageObject) requestMessage).getHttpToolProxy();
		htp.bundleResponse(newMessage, flush);
	}

	/**
	 * Returns {@link Boolean#TRUE} if currently preventing logging; {@link Boolean#FALSE} if
	 * currently logging unconditionally; null if no effect.
	 * @return {@link #suppressLogging}.
	 */
	public Boolean getSuppressLogging() {
		return suppressLogging;
	}
	
	/**
	 * Current-enabled bundles: see {@link #enableBundle(String, String)}
	 */
	private Map<String, String> bundles = null;

	/** The request we're responding to. */
	private MessageObject requestMessage;

	/**
	 * Direct {@link #sendMessage(MessageObject)} to bundle the messages until the 
	 * given MessageType is seen
	 * @param bundleName destination bundle
	 * @param endMsgType type of message to close the bundle
	 */
	void enableBundle(String bundleName, String endMsgType) {
		if (bundles == null)
			bundles = new HashMap<String, String>();
		bundles.put(bundleName, endMsgType);
	}

	/**
	 * Remove the {@link #bundles} entry having the given name.
	 * @param bundleName
	 */
	void disableBundle(String bundleName) {
		if (bundles == null)
			return;
		bundles.remove(bundleName);	
	}
	
	/**
	 * Get any entry from {@link #bundles}.
	 * @return first entry given by iterator; null if {@link #bundles} null or empty
	 */
	Map.Entry<String, String> getCurrentBundle() {
		if (bundles == null)
			return null;
		for (Map.Entry<String, String> entry : bundles.entrySet())
			return entry;
		return null;
	}

	/**
	 * @param suppressLogging new value for {@link #suppressLogging}
	 */
	void setSuppressLogging(Boolean suppressLogging) {
		this.suppressLogging = suppressLogging;
	}
    
    public List<MessageObject> getMessageTank() {
        return new ArrayList<MessageObject>(this.messageTank.keySet());
    }

    /**
     * Add a new entry to the #messageTank with no accompanying {@link ExampleTracerEvent} instance.
     * @param newMessage
     */
    public void addToMessageTank(MessageObject newMessage) {
    	addToMessageTank(newMessage, null);
    }

    /**
     * Add new entries to the #messageTank with no accompanying {@link ExampleTracerEvent} instance.
     * @param newMessages
     */
    public void addToMessageTank(List<MessageObject> newMessages) {
    	for (MessageObject mo : newMessages)
    		addToMessageTank(mo, null);
    }

    /**
     * Add a new entry to the {@link #messageTank} with a (possibly null) {@link ExampleTracerEvent} instance.
     * @param newMessage
     * @param evt example tracer result describing the creation of this message
     */
    public void addToMessageTank(MessageObject newMessage, ExampleTracerEvent evt) {
    	if (trace.getDebugCode("tank")) trace.out("tank", "addToMessageTank("+messageTank.size()+"): "+newMessage+
    			(evt == null ? "" : "\n event: "+evt+". MessageTank@"+Integer.toHexString(hashCode())));
    	if (newMessage == null)
    		return;
    	synchronized(this) {
    		if (tankEmpty)
    			messageTank = new LinkedHashMap<MessageObject, ExampleTracerEvent>();
    		messageTank.put(newMessage, evt);
    		tankEmpty = false;
    	}
    }

    /**
     * Declare the {@link #messageTank} empty. Sets {@link #tankEmpty} but doesn't clear contents yet.
     */
    public synchronized void resetMessageTank() {
        tankEmpty  = true;
    }


	/**
	 * @return the {@link #transaction_id}
	 */
	public String getTransaction_id() {
		return transaction_id;
	}


	/**
	 * @param transaction_id new value for {@link #transaction_id}
	 */
	void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

    /**
     * Send a message to the student interface. This API is meant for tutor-performed actions and
     * side effects generated from a link match. Converts arguments to Vector<String> instances and
     * calls {@link #enqueueMessageToStudent(String, Vector, Vector, Vector, String)}.
     * @param messageType
     * @param selection 
     * @param action
     * @param input
     * @param subtype
     * @return new message's transaction_id
     */
    public String enqueueMessageToStudent(String messageType, String selection, String action, String input,
    		String subtype) {
    	Vector<String> s = PseudoTutorMessageBuilder.s2v(selection);
    	Vector<String> a = PseudoTutorMessageBuilder.s2v(action);
    	Vector<String> i = PseudoTutorMessageBuilder.s2v(input);
    	return enqueueMessageToStudent(messageType, s, a, i, subtype);
	}

    /**
     * Create a {@value MsgType#INTERFACE_ACTION} message for a tutor-performed step and
     * add it to the tank.
     * @param selection 
     * @param action
     * @param input
     * @param subtype
     * @return new message's transaction_id
     */
    public String enqueueToolActionToStudent(Vector selection, Vector action, Vector input,
    		String subtype) {
        return enqueueMessageToStudent(MsgType.INTERFACE_ACTION, selection, action, input, subtype);
	}

    /**
     * Send a message to the student interface. This API is meant for tutor-performed actions and
     * side effects generated from a link match. Creates a new transaction_id value for the message. 
     * Adds the new message to the tank.
     * @param messageType
     * @param selection
     * @param action
     * @param input
     * @param subtype
     * @return new message's transaction_id
     */
    String enqueueMessageToStudent(String messageType, Vector selection, Vector action, Vector input,
    		String subtype) {
		MessageObject mo = MessageObject.create(messageType, "NotePropertySet");
		mo.setSelection(selection);
		mo.setAction(action);
		mo.setInput(input);
        mo.setProperty(PseudoTutorMessageBuilder.TRIGGER, "DATA");
        mo.setProperty(PseudoTutorMessageBuilder.SUBTYPE, (subtype == null || subtype.length() < 1 ?
        		PseudoTutorMessageBuilder.TUTOR_PERFORMED : subtype));
        mo.lockTransactionId(MessageObject.makeTransactionId());    	

        ExampleTracerEvent evt = new ExampleTracerEvent(this,
        		new ExampleTracerSAI(selection, action, input, Matcher.DEFAULT_TOOL_ACTOR));

        addToMessageTank(mo, evt);
        return mo.getTransactionId();
    }
    
	/**
	 * If the given message attributes match, return a possibly-different selection to
	 * guide hint determination. 
	 * @param messageType
	 * @param selection
	 * @param action
	 * @return true if selection and action now may have previous focus to use;
	 *         false if any previous focus should be ignored
	 */
	public boolean editSelectionAndAction(String messageType, Vector selection, Vector action) {
		
		Vector<String> lastSelection = lastStudentSelection;
		lastStudentSelection = selection;
		
		Vector<String> lastAction = lastStudentAction;
		lastStudentAction = action;
		
		if (!isHintSelection(selection))         // only edit hint requests
			return false;		
		if (!"InterfaceAction".equalsIgnoreCase(messageType))
			return false;
		if (lastSelection == null || lastSelection.size() < 1 || lastSelection.get(0).length() < 1) // no prior selection
			return maybeBlankPreviousFocus(selection, action);
		if (isHintSelection(lastSelection))             // 2nd & later hint cancels
			return maybeBlankPreviousFocus(selection, action);
		if (!lastTransactionAffectsHint())
			return maybeBlankPreviousFocus(selection, action);
		if (!controller.getProblemModel().areHintsBiasedByCurrentSelection() &&
				!controller.getProblemModel().areHintsBiasedByPriorError()) {
			return maybeBlankPreviousFocus(selection, action);
		}
		if (!controller.getProblemModel().areHintsBiasedByPriorError())
			return maybeBlankPreviousFocus(selection, action);
		
		for (int i = 1; i <= lastSelection.size(); ++i) {
			if (i < selection.size())
				selection.set(i, lastSelection.get(i-1));
			else
				selection.add(lastSelection.get(i-1));
		}
		
		if (action != null) {
			if (action.size() > 1) {
				action.set(1, HintMessagesManagerForClient.PREVIOUS_FOCUS);
			} else {
				action.add(HintMessagesManagerForClient.PREVIOUS_FOCUS);
			}
			
			for (int i=0; i<lastAction.size(); i++)
				action.add(lastAction.get(i));
		}

		if (trace.getDebugCode("ett")) trace.out("ett","[edit selection] s: "+selection+", a: "+action);
		return true;
	}

	/**
	 * In the given vectors, if {@link ProblemModel#areHintsBiasedByCurrentSelection()} returns false,
	 * then set all elements after the 0th to the empty string to avoid hint bias. 
	 * @param selection student selection, where element[1] may be the selected component prior to the hint
	 * @param action student action, where element[1] may be {@value HintMessagesManagerForClient#PREVIOUS_FOCUS}
	 * @return {@link ProblemModel#areHintsBiasedByCurrentSelection()}
	 */
	private boolean maybeBlankPreviousFocus(Vector selection, Vector action) {
		if (controller.getProblemModel().areHintsBiasedByCurrentSelection())
			return true;
		for(int i = 1; i < selection.size(); ++i)
			selection.set(i, "");
		for(int i = 1; i < action.size(); ++i)
			action.set(i, "");
		return false;
	}

	/**
	 * Tell whether the last transaction can affect the hint chosen in this transaction.
	 * @return true if last transaction was an incorrect step attempt but not out of order 
	 */
	private boolean lastTransactionAffectsHint() {
		List<MessageObject> tankContents = getMessageTank();
		boolean foundIncorrect = false;
		boolean isOutOfOrder = false;
		for (int i = 0; !isOutOfOrder && i < tankContents.size(); ++i) {
			MessageObject mo = (MessageObject) tankContents.get(i);
			if (!foundIncorrect)
				foundIncorrect = "IncorrectAction".equalsIgnoreCase(mo.getMessageType());
			if (!isOutOfOrder)
				isOutOfOrder = "HighlightMsg".equalsIgnoreCase(mo.getMessageType());
		}
		return foundIncorrect && !isOutOfOrder;
	}

	/**
	 * @param selection 
	 * @return true if the first element of selection is "Hint" or "Help"
	 */
	private static boolean isHintSelection(Vector selection) {
		if (selection == null || selection.size() < 1) // no selection to offer
			return false;
		String firstSelection = (String) selection.get(0);
		return "Help".equalsIgnoreCase(firstSelection) || "Hint".equalsIgnoreCase(firstSelection); 
	}

	/**
	 * Send contents of {@link #delayedFeedback} list and clear the list.
	 */
	public void flushDelayedFeedback() {
		if (trace.getDebugCode("msg"))
			trace.out("msg", "flushDelayedFeedback() no. to send "+
					(delayedFeedback == null ? "null" : Integer.toString((delayedFeedback.size()))));
		if (delayedFeedback == null)
			return;
		for (MessageObject mo : delayedFeedback.values()) {
			processInterfaceVariables(mo);
			sendMessage(mo, false);  // false: so far, these are always sent in an enclosing transaction
		}
		delayedFeedback.clear();
	}

	/**
	 * Clear all contents. Clears {@link #messageTank}, {@link #delayedFeedback},
	 * {@link #bundles}. Nulls {@link #lastStudentSelection}, {@link #transaction_id}.
	 * Set {@link #tankEmpty}.
	 */
	public void clear() {
		if (messageTank != null)
			messageTank.clear();
		tankEmpty = true;
		if (bundles != null)
			bundles.clear();
		lastStudentSelection = null;
		lastStudentAction = null;
		if (delayedFeedback != null)
			delayedFeedback.clear();
		setTransaction_id(null);
	}

	public void setRequestMessage(MessageObject mo) {
		this.requestMessage = mo;
	}
}
