/**
 * Copyright 2014 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

/**
 * Provide a means for the author to create a student-initiated action that would change the
 * current state of the solution to a given node in the graph.
 */
public class goToState implements UsesProblemModel {

	/** Problem model reference. */
	private ProblemModel pm;

	/**
	 * Request that the example tracer state be set to the given state at the end of this step.
	 * Also sends any delayed feedback messages that have accumulated.
	 * @param stateName state name on behavior graph
	 * @returns true if node found; false if not found
	 */
	public boolean goToState(String stateName) {
		ProblemNode state = pm.getNode(stateName);
		if(state == null)
			return false;
		pm.requestGoToState(state);
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
