/**
 * Created: Dec 21, 2013 12:51:35 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8;

import static org.junit.Assert.assertTrue;
import jess.JessException;
import jess.RU;
import jess.Value;

import org.junit.Before;
import org.junit.Test;

/**
 * Testing the Solver module assuming the AgebraV8 interface taken from SimStudentAlgebraV8
 * 
 * @author mazda
 * 
 */
public class AlgebraV8SolverTest {

	AlgebraV8Solver solver = new AlgebraV8Solver("wmeTypes.clp", "init.wme", "productionRules.pr");
	
	@Before
	public void setUp() {

		solver.resetRete();
		
		/*
		// System.out.println("loading WME Deftemplate...\n");
		solver.loadWmeDeftemplate();

		// System.out.println("loading WME...\n");
		solver.loadInitWme();
		
		// System.out.println("loading production rules...\n");
		solver.loadProductionRule();
		*/
		
		// System.out.println("initializing a problem...\n");
		solver.enterProblem("3x+1=10");
		
		// System.out.println("printing production rules...\n");
		// solver.printPR();
		// System.out.println("done.\n");
		
	}

	/*
	// solver.printPR();
	solver.printFacts();
	
	try {
		// Reteat rule firing until the problem is done.
		while (solver.isSlotValueOfFact(WME_NAME_PROBLEM, "done", new Value(true))) {

			System.out.println("Printing rule activations...\n");
			solver.printRuleActivations();
	    	
			solver.getRete().run(1);
			
			System.out.println("Printing WME...\n");
	    	solver.printFacts();
			
		}

	} catch (JessException e) {
		e.printStackTrace();
	}
	*/
	
	/**
	 * Test method for 
	 * {@link SimStudent2.ProductionSystem.Solver#isSlotValueOfFact(java.lang.String, java.lang.String, jess.Value)}.
	 */
	@Test
	public void testIsSlotValueOfFact() {
		
		Value expectedRhsValue = null;
		Value expectedLhsValue = null;
		Value expectedDoneValue = null;

		try {
			expectedRhsValue = new Value("3x+1", RU.SYMBOL);
			expectedLhsValue = new Value("10", RU.SYMBOL);
			expectedDoneValue = new Value(true);
		} catch (JessException e) {
			e.printStackTrace();
		}
		assertTrue(solver.isSlotValueOfFact("dorminTable1_C1R1", "value", expectedRhsValue));
		assertTrue(solver.isSlotValueOfFact("dorminTable2_C1R1", "value", expectedLhsValue));
		
		try {
			solver.getRete().run();
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		solver.printFacts();
		assertTrue(solver.isSlotValueOfFact("init", "done", expectedDoneValue));
		
	}
}
