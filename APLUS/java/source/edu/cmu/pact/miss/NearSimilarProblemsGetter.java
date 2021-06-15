package edu.cmu.pact.miss;

import java.util.ArrayList;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

/* @author: Tasmia
 * 
 * Purpose of this class is to give near similar problems from the current one when tutor 
 * and tutee both are stuck. It is good to have near similar problems in edit distance 1 
 * from the current problem, meaning, the similar problems must differ by one solution step 
 * from the current problem. The code would still work if it is not in edit distance 1.
 * 
 * */

public class NearSimilarProblemsGetter {
	/**
     * must be overridden
     */
    public ArrayList<String> /* Object */ nearSimilarProblemsGetter(ProblemNode currentProblem)
    {
    	new Exception("you must override NearSimilarProblemsGetter.nearSimilarProblemsGette() with your domain-specific implementation.").printStackTrace();
    	return null;
    }
}
