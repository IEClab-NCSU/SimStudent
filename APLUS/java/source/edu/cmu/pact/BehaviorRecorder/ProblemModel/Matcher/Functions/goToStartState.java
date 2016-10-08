package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

/**
 * A Matcher function that can generate a return to the problem's start state.
 */
public class goToStartState implements UsesProblemModel {

	/** Problem model reference. */
	private ProblemModel pm;

	/**
	 * Request that the example tracer state be set back to the start state
	 * ({@link ProblemModel#getStudentBeginsHereState()}) at the end of this step.
	 * Also sends any delayed feedback messages that have accumulated.
	 * @returns true
	 */
	public boolean goToStartState() {
		ProblemNode state = pm.getStudentBeginsHereState();
		pm.requestGoToState(state);
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

