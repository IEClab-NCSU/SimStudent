/**
 * 
 * Created: Dec 23, 2013 9:45:53 PM
 * @author mazda
 * 
 * (c) Noboru Matsuda 2013-2014
 * 
 */
package SimStAlgebraV8;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import SimStudent2.SimStudent;
import SimStudent2.TraceLog;
import SimStudent2.LearningComponents.InputMatcher;
import SimStudent2.LearningComponents.LhsConditionsLearnerJ48;
import SimStudent2.LearningComponents.Production;
import SimStudent2.LearningComponents.RhsExhaustiveGoalTest;
import SimStudent2.LearningComponents.RhsOperators;
import SimStudent2.LearningComponents.RhsOperatorsLearner;
import SimStudent2.LearningComponents.RhsSearchSuccessorFn;
import SimStudent2.LearningComponents.WmePerceptionLearner;
import SimStudent2.LearningComponents.WmePerceptionSearchGoalTest;
import SimStudent2.LearningComponents.WmePerceptionSearchSuccessorFn;
import SimStudent2.ProductionSystem.ProductionWriter;
import SimStudent2.ProductionSystem.Solver;

/**
 * @author mazda
 *
 */
public class SimStAlgebraV8ProductionTest {

	SimStudent simStudent = null;

	Solver solver = null;
		
	String rhsOperatorsLearnerClassName = "SimStAlgebraV8.AlgebraV8RhsOperatorsLearner";
	String rhsGoalTestClassName = "SimStudent2.LearningComponents.RhsExhaustiveGoalTest";
	// Vector<String> rhsOperatorList = null;

	LhsConditionsLearnerJ48 lhsConditionsLearnerJ48 = new LhsConditionsLearnerJ48("feature-predicates.txt");
	
	/**
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		
		TraceLog.setTraceLevel(5);

		solver = new AlgebraV8Solver("wmeTypes.clp", "init.wme", "productionRules.pr");

		Class<RhsOperatorsLearner> rhsOperatorsLearnerClass = (Class<RhsOperatorsLearner>)Class.forName(rhsOperatorsLearnerClassName);
		Class<RhsExhaustiveGoalTest> rhsGoalTestClass = (Class<RhsExhaustiveGoalTest>)Class.forName(rhsGoalTestClassName);
		RhsSearchSuccessorFn rhsSearchSuccessorFn = new RhsSearchSuccessorFn("operators.txt");
		InputMatcher inputMatcher = new AlgebraV8InputMatcher();
		
		WmePerceptionSearchSuccessorFn wmePerceptionSearchSuccessorFn = new WmePerceptionSearchSuccessorFn();
		WmePerceptionSearchGoalTest wmePerceptionSearchGoalTest = new WmePerceptionSearchGoalTest();
		WmePerceptionLearner wmePerceptionLearner = new AlgebraV8WmePerceptionLearner(wmePerceptionSearchSuccessorFn, wmePerceptionSearchGoalTest);
		
		simStudent = 
				new SimStudent(solver, wmePerceptionLearner, lhsConditionsLearnerJ48, rhsOperatorsLearnerClass, rhsGoalTestClass, inputMatcher, rhsSearchSuccessorFn);
		
	}

	@Test  // *************************************************************
	public void testgeneralizeExamplesFromFile() {
		
		simStudent.generalizeExamplesFromFile("batchExamples.txt");
		//simStudent.generalizeExamplesFromFile("batchExamples-debug.txt");
		
		ArrayList<Production> productions = simStudent.getLearnedProductions();
		
		for (Production production : productions) {
			
			ProductionWriter productionWriter = new ProductionWriter(production);
			productionWriter.printToFile();
		}
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Helper methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	/*
	 * Returns the list of RHS operators for a given production  
	 */
	/*
	private List<String> getBindClausesFor(String productionName) {
		Production doneProduction = simStudent.getProductionFor(productionName);
		RhsOperators doneRhsOperators = doneProduction.getRhsOperators();
		List<String> doneBindClauses = doneRhsOperators.getJessBindClauses();
		return doneBindClauses;
	}
	*/

}
