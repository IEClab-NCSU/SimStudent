/**
 * A search agent for searching RHS operator sequence
 * 
 * Created: May 19, 2014 8:43:39 PM
 * @author mazda
 * (c) Noboru Matsuda 2014 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.List;

import SimStudent2.SimStudent;
import aima.search.framework.Problem;
import aima.search.framework.Search;

/**
 * The Goal Test terminates when a sequence of operator satisfies the input/output pair for all examples in the list.
 * 
 * @author mazda
 * 
 */
public abstract class RhsOperatorsLearner {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	ExhaustiveSearchAgent searchAgent = null;
	RhsExhaustiveGoalTest goalTest = null;
	RhsSearchSuccessorFn rhsSuccessorFn = null;
 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	/*
	 * Because an RhsOperatorsLearner instance is created at a runtime by reflection,
	 * the constructor does nothing but just creating an empty object. The initialization 
	 * of the object is then done by init();
	 * 
	 */
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	
	public void init(RhsExhaustiveGoalTest goalTest, RhsSearchSuccessorFn rhsSuccessorFn) {
		
		this.goalTest = goalTest;
		this.rhsSuccessorFn = rhsSuccessorFn;
	}
	
	/**
	 * 
	 * 
	 * @param examples
	 * @return
	 */
	public RhsOperators search(Example example) {
		
		/*
		ExhaustiveSearchAgent searchAgent = getSearchAgent();
		RhsState lastState = searchAgent.getLastState();
		lastState.addExample(example);
		*/
				
		return dispatchSearch(example);
	}

	/**
	 * Called only once when a new skill is demonstrated. 
	 * 
	 * @param examples
	 * @param wmePerception
	 * @return
	 */
	public RhsOperators initialSearch(Example example, WmePerception wmePerception, InputMatcher inputMatcher) {

		ArrayList<WmePath> foa = wmePerception.getFocusOfAttention();
		
		RhsState initialState = makeInitialStateFor(example, foa);
		RhsSearchSuccessorFn rhsSuccessorFn = getRhsSuccessorFn();
		RhsExhaustiveGoalTest goalTest = getGoalTest();

		Problem problem = new Problem( initialState, rhsSuccessorFn, goalTest );
		Search search = new ExhaustiveIDS( SimStudent.getRhsMaxSearchDepth() );
		ExhaustiveSearchAgent rhsSearchAgent = new ExhaustiveSearchAgent( problem, search );
		
		setSearchAgent( rhsSearchAgent );
		
		return dispatchSearch(example);
	}

	private RhsOperators dispatchSearch(Example example) {

		RhsOperators rhsOperators = null;

		ExhaustiveSearchAgent searchAgent = getSearchAgent();
		// Add the newly fed example for the goalTest
		Problem problem = searchAgent.getProblem();
		RhsExhaustiveGoalTest goalTest = (RhsExhaustiveGoalTest)problem.getGoalTest();
		goalTest.addExample(example);

		try {
			
			List<String> actionList = searchAgent.search();
			rhsOperators = new RhsOperators(actionList, goalTest.getJessBindSequence());
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return rhsOperators;
	}
	
	/**
	 * @param examples
	 * @param inputMatcher
	 * @return
	 */
	private RhsState makeInitialStateFor(Example example, ArrayList<WmePath> foa) {

		RhsState rhsState = new RhsState(example, foa);
		// TraceLog.out("rhsState :== " + rhsState);
		return rhsState;
	}
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters and Setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private ExhaustiveSearchAgent getSearchAgent() { return searchAgent; }
	private void setSearchAgent(ExhaustiveSearchAgent searchAgent) { this.searchAgent = searchAgent; }

	private RhsExhaustiveGoalTest getGoalTest() { return goalTest; }
	// private void setGoalTest(RhsExhaustiveGoalTest goalTest) { this.goalTest = goalTest; }

	private RhsSearchSuccessorFn getRhsSuccessorFn() {
		return rhsSuccessorFn;
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Abstract methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	
}
