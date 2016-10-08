/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.Controller.MessageTank;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;

/**
 * Send any delayed feedback messages using MessageTank#flushDelayedFeedback()
 */
public class deliverDelayedFeedback implements UsesProblemModel {
	
	/** Problem model reference. */
	private ProblemModel pm;
	
	/**
	 * Send any delayed feedback messages that have accumulated.
	 * @returns true
	 */
	public boolean deliverDelayedFeedback() {
		MessageTank tank = pm.getController().getMessageTank();
		tank.flushDelayedFeedback();
		return true;
	}

	/**
	 * @param new value for {@link #pm}
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesProblemModel#setProblemModel(edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel)
	 */
	public void setProblemModel(ProblemModel pm) {
		this.pm = pm;
	}
}
