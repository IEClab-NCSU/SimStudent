package edu.cmu.pact.miss;

import cl.utilities.TestableTutor.SAI;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public abstract class SAIConverter {

	/*
	 * These methods must be overridden, depending upon the interface you are
	 * working with is 1 table multiple column or 3 table single column or some new
	 * interface. 
	 */
	public abstract SAI convertCtatSaiToClSai(String selection, String action, String input);
    public abstract boolean validSelection(String selection, int numPrevSteps);
	public abstract String convertClResponseToCtatSai(BR_Controller brController,
			ProblemNode currentNode, String clAction);
	public abstract boolean isSelectionTransformationStep(String selection);
	
}
