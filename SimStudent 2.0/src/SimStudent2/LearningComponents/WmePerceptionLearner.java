/**
 * Created: Dec 24, 2013 7:18:35 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import SimStudent2.SimStudent;
import SimStudent2.TraceLog;
import aima.search.framework.Problem;

/**
 * @author mazda
 *
 */
public abstract class WmePerceptionLearner {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Abstract methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Exhaustive Search Agent is maintained for individual skills
	Hashtable<String, ExhaustiveSearchAgent> searchAgent = new Hashtable<String, ExhaustiveSearchAgent>();

	// Successor Function
	WmePerceptionSearchSuccessorFn successorFunction = null;
	
	// Goal Test
	WmePerceptionSearchGoalTest goalTest = null;
	

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public WmePerceptionLearner(WmePerceptionSearchSuccessorFn successorFunction, WmePerceptionSearchGoalTest goalTest) {
		
		// TraceLog.out("****** WmePerceptionLearner: successorFunction = " + successorFunction);
		
		setSuccessorFunction(successorFunction);
		setGoalTest(goalTest);
	}
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Methods 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	// 
	// Make a new production for example that is given for a novel skill
	// Make an initial ("new") WME perception for the given example
	// 
	/**
	 * @param example
	 * @param rete 
	 * @return
	 */
	public WmePerception initialWmePerception(Example example) {

		ArrayList<WmePath> foa = example.getFoA();
		// TraceLog.out("foa = " + foa);
		
		WmePath selection = example.getSAI().getSelectionWmePath();
		
		WmePerception wmePerception = new WmePerception(foa, selection);
		// TraceLog.out("wmePerception = " + wmePerception);
		
		return wmePerception;
	}
	
	// 
	// Refine existing production (based on currentExamples) with a newly given example.
	// 
	/**
	 * @param newExample
	 * @param currentProduction
	 * @return
	 */
	public WmePerception generalizeLhsWmePerception(Example newExample, Production currentProduction) {

		WmePerception wmePerception = null;
		
		if (currentProduction == null) {

			// There must be, by definition, currently learned production
			// See SimStudent.generalizeLhsWmePerception()
			new Exception("generalizeLhsWmePerception: currentProduction can't be null.").printStackTrace();
			
		} else {
			
			WmePerception currentWmePerception = currentProduction.getWmePerception();

			// See if the specified example matches to the WME perception
			if (currentWmePerception.isApplicable(newExample)) {
				
				wmePerception = currentWmePerception;
				
			} else {
				
				// Resume the exhaustive search by considering given <example>'s
				wmePerception = refineCurrentWmePerception(newExample, currentProduction);
			}
		}
		return wmePerception;
	}

	//
	// Generalize a current WME perception consistent with a set of examples for a given skill
    // Positive examples are used to generalize WME Paths, whereas negative examples are used to specialize them.
	// Examples contain both positive and negative examples 
	//
    private WmePerception refineCurrentWmePerception(Example newExample, Production currentProduction) {

    	WmePerception wmePerception = null;
    	
		// See Instruction.java
		ExhaustiveSearchAgent searchAgent = getSearchAgentFor(currentProduction);
		
		try {
			
			// TraceLog.out("refineCurrentWmePerception: successorFunction = " + getSuccessorFunction());
			
			// How would <examples> be passed to the search agent?
			// Problem in the SearchAgent holds the successor function 
			getSuccessorFunction().updateExamples(newExample);
			getGoalTest().updateExamples(newExample);
			
			List<String> wmePerceptionString = searchAgent.search();
			
			/*
			TraceLog.out("refineCurrentWmePerception: wmePerceptionString");
			for (int i = 0; i < wmePerceptionString.size(); i++) {
				TraceLog.out("[" + i + "] = " + wmePerceptionString.get(i));
			}
			*/
			
			String wmePerceptionFlatString = wmePerceptionString.get(wmePerceptionString.size()-1);
			ArrayList<String> foaValueVariables = currentProduction.getWmePerception().getFoaValueVariables();

			wmePerception = new WmePerception(wmePerceptionFlatString, foaValueVariables);
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return wmePerception;
	}

    
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getter & Setter
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public WmePerceptionSearchSuccessorFn getSuccessorFunction() { return successorFunction; }
	private void setSuccessorFunction(WmePerceptionSearchSuccessorFn successorFunction) { 
		this.successorFunction = successorFunction; 
	}

	public WmePerceptionSearchGoalTest getGoalTest() { return goalTest; }
	public void setGoalTest(WmePerceptionSearchGoalTest goalTest) { this.goalTest = goalTest; }
	
	public Hashtable<String, ExhaustiveSearchAgent> getSearchAgent() { return searchAgent; }
	public void setSearchAgent(Hashtable<String, ExhaustiveSearchAgent> searchAgent) { this.searchAgent = searchAgent; }

	// Initiate and retrieve search agent for individual skill
	ExhaustiveSearchAgent getSearchAgentFor(Production production) { 
		
		ExhaustiveSearchAgent searchAgent = null;
				
		String skillName = production.getName();
		if (getSearchAgent().containsKey(skillName)) {
		
			searchAgent = getSearchAgent().get(skillName);
		
		} else {
			
			WmePerception mostSpecificWmePerception = production.getWmePerception();
			searchAgent = createNewSearchAgent(mostSpecificWmePerception);
			getSearchAgent().put(skillName, searchAgent);
		}
		return searchAgent;
	}
	
	ExhaustiveSearchAgent createNewSearchAgent(WmePerception mostSpecificWmePerception) {

		WmePerceptionSearchState initialState = new WmePerceptionSearchState(mostSpecificWmePerception);
		Problem problem = new Problem(initialState, getSuccessorFunction(), getGoalTest());
		ExhaustiveIDS search = new ExhaustiveIDS(SimStudent.MAX_WME_PERCEPTION_SEARCH_DEPTH);
		
		return new ExhaustiveSearchAgent(problem, search);
	}

    
}
