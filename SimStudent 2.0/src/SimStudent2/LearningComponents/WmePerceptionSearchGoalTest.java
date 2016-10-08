/**
 * Created: Wed Jan 12 13:41:06 2005
 * (c) Noboru Matsuda 2005-2014
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package SimStudent2.LearningComponents;

import java.util.ArrayList;

import SimStudent2.TraceLog;
import aima.search.framework.GoalTest;

public class WmePerceptionSearchGoalTest implements GoalTest {
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	ArrayList<Example> positiveExamples = new ArrayList<Example>();
	ArrayList<Example> negativeExamples = new ArrayList<Example>();


	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
    /**
     * Creates a new <code>LhsGoalTest</code> instance.
     *
     */
	/*
    public WmePerceptionSearchGoalTest() {
    }
    */

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Implementation of aima.search.framework.GoalTest
    /**
     * Since a successor function only generates states that are
     * consistent with examples, if a state is generated at the depth
     * that is equal to (# of seeds in examples) + 1, then that state
     * must satisify a goal condition (i.e., all the wme paths in LHS
     * are consistent with the seeds in examples.  Now, this test is
     * done by invoking isGoalState() method defined in LhsState. 
     *
     * @param object an <code>Object</code> value
     * @return a <code>boolean</code> value
     **/
    public final boolean isGoalState(final Object object) {

    	WmePerceptionSearchState lhs = (WmePerceptionSearchState)object;
    	boolean isGoalState = lhs.isGoalState(getPositiveExamples());
    	
    	/*
    	TraceLog.out("isGoalState: * * * * * * * * * * * * * * * * * * * * * * *");
    	TraceLog.out("WME Perception: " + lhs.getWmePerception());
    	if (isGoalState) {
    		TraceLog.out("TRUE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	}
    	*/
    	
    	return isGoalState; 
    }

	/**
	 * @param example
	 */
	public void updateExamples(Example example) {

		if (example.isPositiveExample()) {
			addPositiveExample(example);
		} else {
			addNegativeExample(example);
		}
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters & Setters 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private void addPositiveExample(Example example) {
		getPositiveExamples().add(example);
	}

	private ArrayList<Example> getPositiveExamples() {
		return positiveExamples;
	}
	
	private void addNegativeExample(Example example) {
		getNegativeExamples().add(example);
	}

	private ArrayList<Example> getNegativeExamples() {
		return negativeExamples;
	}
	
}
