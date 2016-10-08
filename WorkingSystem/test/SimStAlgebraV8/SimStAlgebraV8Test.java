/**
 * 
 * Created: Dec 23, 2013 9:45:53 PM
 * @author mazda
 * 
 * (c) Noboru Matsuda 2013-2014
 * 
 */
package SimStAlgebraV8;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import SimStudent2.SimStudent;
import SimStudent2.TraceLog;
import SimStudent2.LearningComponents.Example;
import SimStudent2.LearningComponents.InputMatcher;
import SimStudent2.LearningComponents.LhsConditionsLearnerJ48;
import SimStudent2.LearningComponents.Production;
import SimStudent2.LearningComponents.RhsExhaustiveGoalTest;
import SimStudent2.LearningComponents.RhsOperators;
import SimStudent2.LearningComponents.RhsOperatorsLearner;
import SimStudent2.LearningComponents.RhsSearchSuccessorFn;
import SimStudent2.LearningComponents.SAI;
import SimStudent2.LearningComponents.WmePath;
import SimStudent2.LearningComponents.WmePerception;
import SimStudent2.LearningComponents.WmePerceptionLearner;
import SimStudent2.LearningComponents.WmePerceptionSearchGoalTest;
import SimStudent2.LearningComponents.WmePerceptionSearchSuccessorFn;
import SimStudent2.ProductionSystem.Solver;
import SimStudent2.ProductionSystem.SsRete;

/**
 * @author mazda
 *
 */
public class SimStAlgebraV8Test {

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
		
		
		// System.out.println("loading WME Deftemplate...\n");
		/*
		 * The following "initialization is now automatically done by the AlgebraV8Solver() constructor
		 * To enter a new problem, call solver.resetRete();
		 * 
		solver.loadWmeDeftemplate();
		solver.resetRete();
		
		// System.out.println("loading production rules...\n");
		solver.loadProductionRule();

		// System.out.println("loading WME...\n");
		solver.loadInitWme();
		*/
		
	}

	// @Test  // *************************************************************
	public void testprintRuleActivationAtInitialState() {
		
		//System.out.println("printRuleActivation()");
		
		solver.enterProblem("x+1=10");
		solver.printRuleActivations();
		
		assertSame(2, solver.sizeRuleActivation());
	}
	
	
	@Test  // *************************************************************
	public void testgeneralizeExamplesFromFile() {
		
		simStudent.generalizeExamplesFromFile("batchExamples.txt");
		//simStudent.generalizeExamplesFromFile("batchExamples-debug.txt");
		
		// The length of the RHS operator sequence for "done" must be 1
		// 
		List<String> doneBindClauses = getBindClausesFor("done");
		assertSame(1, doneBindClauses.size());

		List<String> subtractTypeinBindClauses = getBindClausesFor("subtract-typein");
		assertSame(2, subtractTypeinBindClauses.size());
		
		Production subtractTypeinProduction = simStudent.getProductionFor("subtract-typein");
		WmePerception wmePerception = subtractTypeinProduction.getWmePerception();
		ArrayList<String> foaValueVariable = wmePerception.getFoaValueVariables();
		
		TraceLog.out("foaValueVariable = " + foaValueVariable);
		
		assertSame(2, foaValueVariable.size());
		assertNotNull(foaValueVariable.get(0));
		assertNotNull(foaValueVariable.get(1));
	}

	
	///@Test  // *************************************************************
	public void testmakeNewProductionSubtract() {
		
		Example example;
		String[] foa;

		solver.enterProblem("x+1=10");

		foa = new String[]{"dorminTable1_C1R1", "dorminTable2_C1R1"};
		example = makeAlgebraV8Example("dorminTable3_C1R1", "subtract 1", "subtract", foa);


		Production production = simStudent.makeNewProduction(example);
		TraceLog.out("" + production);
		
		// Skill name . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		// 
		assertEquals("subtract", production.getName());

		// LHS WME retrieval . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		// 
		String foaStr = "[?var5 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var4 ? ? ?))\n"
				+ "?var4 <- (MAIN::table (columns ?var3))\n"
				+ "?var3 <- (MAIN::column (cells ?var1 ? ? ? ? ?))\n"
				+ "?var1 <- (MAIN::cell (name ?foa0) (value ?val2&~nil))\n"
				+ ", ?var11 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ? ?var10 ? ?))\n"
				+ "?var10 <- (MAIN::table (columns ?var9))\n"
				+ "?var9 <- (MAIN::column (cells ?var7 ? ? ? ? ?))\n"
				+ "?var7 <- (MAIN::cell (name ?foa6) (value ?val8&~nil))\n]";
		
		String selection = "?var17 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ? ? ?var16 ?))\n"
				+ "?var16 <- (MAIN::table (columns ?var15))\n"
				+ "?var15 <- (MAIN::column (cells ?var13 ? ? ? ? ?))\n"
				+ "?var13 <- (MAIN::cell (name ?selection12) (value ?val14&nil))\n";

		String wmePathStr = foaStr + selection;
		
		assertEquals(wildcardVarNames(wmePathStr), wildcardVarNames(production.getLhsWmePath()));
		
		// RHS Operator sequence . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		// 
		String rhsOperators = "RhsOperators [jessBindClauses = [(bind ?val24 (get-first-integer-without-sign ?val2)), (bind ?val31 (skill-subtract ?val24))]]";
		assertEquals(wildcardVarNames(rhsOperators), wildcardVarNames(production.getRhsOperators().toString()));
	}
	
	///@Test    // *************************************************************
	public void testgeneralizeGivenExampleSubtract() {
		
		solver.enterProblem("2x+4=7");

		String[] foa;
		Example example;
		Production production;
		
		foa = new String[]{"dorminTable1_C1R1", "dorminTable2_C1R1"};
		example = makeAlgebraV8Example("dorminTable3_C1R1", "subtract 4", "subtract", foa);
		production = simStudent.generalizeGivenExample(example);
		assertEquals("subtract", production.getName());
		//
		TraceLog.out("" + production);
		//
		assertEquals("subtract", production.getName());
		
		foa = new String[]{"dorminTable1_C1R1", "dorminTable3_C1R1"};
		example = makeAlgebraV8Example("dorminTable1_C1R2", "2x", "subtract-typein", foa);
		production = simStudent.generalizeGivenExample(example);
		assertEquals("subtract-typein", production.getName());
		//
		TraceLog.out("" + production);
		// 
		String rhsOperators = "RhsOperators [jessBindClauses = [(bind ?val@ (get-first-term ?val@))]]";
		assertEquals(wildcardVarNames(rhsOperators), wildcardVarNames(production.getRhsOperators().toString()));
		
		foa = new String[]{"dorminTable2_C1R1", "dorminTable3_C1R1"};
		example = makeAlgebraV8Example("dorminTable2_C1R2", "3", "subtract-typein", foa);
		production = simStudent.generalizeGivenExample(example);
		assertEquals("subtract-typein", production.getName());
		//
		TraceLog.out("" + production);
		
		
		
		
		
		// System.out.println(rhsOperators);
		// System.out.println(production.getRhsOperators());
	}

	///@Test    // *************************************************************
	public void testmakeNewProductionAdd() {
		
		Example example;
		String[] foa;
		
		solver.enterProblem("3+2x=-x+7");
		
		foa = new String[]{"dorminTable1_C1R1", "dorminTable2_C1R1"};
		example = makeAlgebraV8Example("dorminTable3_C1R1", "add x", "add", foa);
		
		Production production = simStudent.makeNewProduction(example);
		TraceLog.out("" + production);
	}
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Helper methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	/*
	private Example makeSubtractExample() {
		// System.out.println("generalizeGivenExample()");
		
		SsRete rete = solver.getRete();
		
		WmePath selectionWmePath = new WmePath("dorminTable3_C1R1", rete, WmePath.SELECTION);
		
		SAI sai = new SAI(selectionWmePath, "EnterTable", "subtract 1");
		solver.enterStep(sai);
		
		ArrayList<WmePath> foa = new ArrayList<WmePath>();
		foa.add(new WmePath("dorminTable1_C1R1", rete, WmePath.FOA));
		foa.add(new WmePath("dorminTable2_C1R1", rete, WmePath.FOA));
		
		// Make an example
		Example example = new Example("subtract", foa, sai);
		return example;
	}
	*/
	
	private Example makeAlgebraV8Example(String selection, String input, String skill, String[] foaStrs) {

		SsRete rete = solver.getRete();
		
		WmePath selectionWmePath = new WmePath(selection, rete, WmePath.SELECTION);
		SAI sai = new SAI(selectionWmePath, "EnterTable", input);
		solver.enterStep(sai);

		// TraceLog.out("**********************************");
		// solver.printFacts();
				
		ArrayList<WmePath> foa = new ArrayList<WmePath>();
		for (String foaStr : foaStrs) {
			foa.add(new WmePath(foaStr, rete, WmePath.FOA));
		}

		return new Example(skill, foa, sai);
	}
	
	private String wildcardVarNames(String rhsOperators) {
		return rhsOperators.replaceAll("[0-9]+", "@");
	}

	/*
	 * Returns the list of RHS operators for a given production  
	 */
	private List<String> getBindClausesFor(String productionName) {
		Production doneProduction = simStudent.getProductionFor(productionName);
		RhsOperators doneRhsOperators = doneProduction.getRhsOperators();
		List<String> doneBindClauses = doneRhsOperators.getJessBindClauses();
		return doneBindClauses;
	}


}
