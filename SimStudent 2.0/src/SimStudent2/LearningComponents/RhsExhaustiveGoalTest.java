/**
 * Created: Jun 16, 2014 19:54:00
 * 
 * @author mazda
 * (c) Noboru Matsuda 2014
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import SimStudent2.TraceLog;
import SimStudent2.ProductionSystem.JessBindSequence;
import aima.search.framework.GoalTest;


/**
 * @author mazda
 *
 */
public class RhsExhaustiveGoalTest implements GoalTest {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// RhsState lastState = null;

	// True if productions must use all FoA instances specified
	private boolean useAllFoA = false;
	
	// A list of examples given
	private Vector<Example> examples = new Vector<Example>();
	
	// A domain dependent Input Matcher 
	InputMatcher inputMatcher = null;
	
	// A cache for input value matching
	HashMap<String, HashMap<String, String>> equivalentValuesCashe = new HashMap<String, HashMap<String,String>>();
	
	private JessBindSequence jessBindSequence = null;
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /**
     * @param instructions
     */
	// public RhsExhaustiveGoalTest() {}


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public boolean isGoalState(final Object object) {

    	boolean isGoalState = false;
    	
    	RhsState rhsState = (RhsState)object;

    	// ----- debug -----
    	// setRhsState( rhsState );
    	// ----- debug -----

    	if (hasConsistentOpSequenceWith(rhsState, getExamples())) {
    		if (!isUseAllFoA() || rhsState.isAllFoaUsed()) {
    			// Keep a snapshot of jessBindSequence for generalizeProduction(Example,Production) to 
    			// compute isConsistentWith(example) 
    			setJessBindSequence(rhsState.getJessBindSequence());
    			isGoalState = true;
    		}
    	}

    	/*
    	if (isGoalState) {
    		setLastState(rhsState);
    	} else {
    		if(trace.getDebugCode("rhs"))trace.out("rhs", "goalTest failed on " + rhsState + " for " + getInstruction());
    	}
    	*/

    	return isGoalState;
    }
    
	/**
	 * @param examples
	 * @return
	 */
	private boolean hasConsistentOpSequenceWith(RhsState rhsState, Vector<Example> examples) {
		
		boolean hasConsistentOpSequenceWith = false;
		
		// An example contains abstracted WmePaths. Those that correspond to FoA has foaNameVariable. 
		Example primaryExample = examples.firstElement();
		ArrayList<String> foaVarNames = primaryExample.getFoaValueVariables();
		
		String ruleName = null;
		for (Example example : examples) {
			
			// TraceLog.out("GoalTest: rhsState = " + rhsState + ", example = " + example);
			
			if (ruleName != null && !ruleName.equals(example.getRuleName())) {
				new Exception("RhsExhaustiveGoalTest: Inconsistent rule names >> " + examples).printStackTrace();
			} else {
				ruleName = example.getRuleName();
			}
			
			// First, apply a sequence of operators to the FoA given by the example
			String returnValue = rhsState.getJessBindSequence().apply(example, foaVarNames);
			
			if (returnValue != null) {
			
				// The operator invocations have terminated legally.
				// Now, it's time to test if it returns the "input" value.
				hasConsistentOpSequenceWith = equivalentValues(returnValue, example.getInput());
				
				// Abandon if the returnValue from the operator invocation is not equivalent to the "input" 
				// specified in the example
				if (!hasConsistentOpSequenceWith) {
					break;
				}
				
			} else {
				
				// The operator invocation was not valid hence abandon.
				break;
			}
		}
		
		return hasConsistentOpSequenceWith;
	}

	/**
	 * @param returnValue
	 * @param input
	 * @return
	 */
	private boolean equivalentValues(String returnValue, String input) {

		boolean isEquivalentValues = false;
		
		String equivalentValuesCacheValue = lookupEquivalentValuesInCache(returnValue, input);
		
		// equivalentValues() invocation has been cached
		if (equivalentValuesCacheValue != null) {
			
			isEquivalentValues = equivalentValuesCacheValue.equals("T");
			
		} else {
			
			isEquivalentValues = getInputMatcher().equivalent(returnValue, input);
			casheEquivalentValues(returnValue, input, isEquivalentValues);
		}
		
		// TraceLog.out("equivalentValues(" + returnValue + ", " + input + ") = " + isEquivalentValues);
		
		return isEquivalentValues;
	}

	/**
	 * @param returnValue
	 * @param input
	 * @param isEquivalentValues
	 */
	private void casheEquivalentValues(String returnValue, String input, boolean isEquivalentValues) {
		
		HashMap<String, String> cacheTable = getEquivalentValuesCashe().get(returnValue);
		
		if (cacheTable == null) {
			cacheTable = new HashMap<String, String>();
			getEquivalentValuesCashe().put(returnValue, cacheTable);
		}
		
		cacheTable.put(input, isEquivalentValues ? "T" : "F");
	}

	/**
	 * @param returnValue
	 * @param input
	 * @return
	 */
	private String lookupEquivalentValuesInCache(String returnValue, String input) {
		
		String isEquivalentValues = null;
		
		HashMap<String,String> cacheTable = getEquivalentValuesCashe().get(returnValue);
		
		if (cacheTable != null) {
			isEquivalentValues = cacheTable.get(input);
		}
		
		return isEquivalentValues;
	}


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Getters and Setters 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	private Vector<Example> getExamples() {
		return examples;
	}
	public void addExample(Example example) {
		this.examples.add(example);
	}
	
	private boolean isUseAllFoA() {
		return useAllFoA;
	}
	/*
	private void setUseAllFoA(boolean useAllFoA) {
		this.useAllFoA = useAllFoA;
	}
	*/


	private InputMatcher getInputMatcher() {
		return inputMatcher;
	}
	public void setInputMatcher(InputMatcher inputMatcher) {
		this.inputMatcher = inputMatcher;
	}


	private HashMap<String, HashMap<String, String>> getEquivalentValuesCashe() {
		return equivalentValuesCashe;
	}

	JessBindSequence getJessBindSequence() {
		return jessBindSequence;
	}

	private void setJessBindSequence(JessBindSequence jessBindSequence) {
		this.jessBindSequence = jessBindSequence;
	}
    
}
