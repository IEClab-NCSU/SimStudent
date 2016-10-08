package edu.cmu.pact.miss.MetaTutor;

import java.util.ArrayList;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.HashMap;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory;

/**
 * 
 */
public interface APlusQuizProblemAbstractor {

	/**
	 * Returns an array of items on the quiz that the SimStudent 
	 * was able to solve correctly.
	 */
	public abstract String[] getQuizItemsSolvedCorrectly();
	
	/**
	 * 
	 */
	public abstract String[] getQuizItemsSolvedIncorrectly();
	
	/**
	 * 
	 */
	public abstract void setProblemsToTutor(boolean isQuiz, ModelTraceWorkingMemory wm);

	public abstract void abstractQuizProblems(HashMap hm, ModelTraceWorkingMemory wm);
	
	public abstract String abstractProblem(String problem, ModelTraceWorkingMemory wm);
	
	public abstract String abstractProblem(ArrayList<String> startStateElements, BR_Controller brController);

}
