/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import jess.Funcall;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.jess.MT;
import edu.cmu.pact.jess.MTRete;

/**
 * Send a message to the student interface to take a tutor-performed action.
 */
public class performTutorAction implements UsesProblemModel {

	/**	For access to the runtime controller. */
	ProblemModel pm = null;

	/**
	 * Send a {@link MsgType#INTERFACE_ACTION} message.
	 * @param selection
	 * @param action
	 * @param input
	 * @return true if sent
	 */
	public boolean performTutorAction(String selection, String action, String input) {
		return performTutorAction(selection, action, input, MsgType.INTERFACE_ACTION);
	}
	/**
	 * Send a message with the given message type.
	 * @param selection
	 * @param action
	 * @param input
	 * @param messageType
	 * @return true if sent
	 */
	public boolean performTutorAction(String selection, String action, String input, String messageType) {
		if (messageType.length() < 1)
			return false;
        
        BR_Controller controller = (pm == null ? null : pm.getController());
        if (controller == null)
        	return false;

        String transactionId = controller.getMessageTank().enqueueMessageToStudent(messageType,
        		selection, action, input, PseudoTutorMessageBuilder.TUTOR_PERFORMED);
    	if (trace.getDebugCode("functions"))
    		trace.out("functions", String.format("performTutorAction(%s, %s, %s, %s) transactionId %s",
    				selection, action, input, messageType, transactionId));
		return true;
	}
	
	/**
	 * @param pm
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesProblemModel#setProblemModel(edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel)
	 */
	public void setProblemModel(ProblemModel pm) {
		this.pm = pm;
	}

}
