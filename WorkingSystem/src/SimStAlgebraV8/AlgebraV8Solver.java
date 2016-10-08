/**
 * Created: Sun Dec 15, 2013
 * Noboru Matsuda
 * 
 * An Algebra V8 Tutor implementation of Solver
 * 
 */
package SimStAlgebraV8;

import SimStudent2.LearningComponents.SAI;
import SimStudent2.LearningComponents.WmePath;
import SimStudent2.ProductionSystem.Solver;
import SimStudent2.ProductionSystem.SsRete;

public class AlgebraV8Solver extends Solver {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	final static String WME_NAME_PROBLEM = "init";
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param string
	 * @param string2
	 * @param string3
	 */
	public AlgebraV8Solver(String wmeDeftemplateFileName, String initWmeFileName, String productionFileName) {
		super(wmeDeftemplateFileName, initWmeFileName, productionFileName);
	}

	// equation: "3x+1=10"
	@Override
	public void enterProblem(String equation) {

		SsRete rete = getRete();
		
		int equalSignIdx = equation.indexOf('=');
		String lhsExp = equation.substring(0, equalSignIdx);
		String rhsExp = equation.substring(equalSignIdx+1);
		
		// System.out.println("LHS: " + lhsExp + "\n");
		// System.out.println("RHS: " + rhsExp + "\n");
		
		// startStateElements
		// dorminTable1_C1R1
		// dorminTable2_C1R1
		WmePath selectionWmePath = null;
		selectionWmePath = new WmePath("dorminTable1_C1R1", rete, WmePath.SELECTION);
		SAI lhsSAI = new SAI(selectionWmePath, "enterTable", lhsExp);
		selectionWmePath = new WmePath("dorminTable2_C1R1", rete, WmePath.SELECTION);
		SAI rhsSAI = new SAI(selectionWmePath, "enterTable", rhsExp);

		// System.out.println("lhsSAI: " + lhsSAI);
		// System.out.println("rhsSAI: " + rhsSAI);
		
		enterStep(lhsSAI);
		enterStep(rhsSAI);
		
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// MAIN call
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
    /**
     * @param args
     */
	/*
    public static void main(String[] args) {
    	
    	Solver solver = new AlgebraV8Solver();

    	solver.loadWmeDeftemplate("wmeTypes.clp");
    	
    	
    	solver.loadInitWme("init.wme");

    	solver.enterProblem("3x+1=10");
    	
    	solver.loadPR("productionRules.pr");

    	// solver.printPR();
    	solver.printFacts();
    	
    	int numIterations = 0;
    	try {
    		// Reteat rule firing until the problem is done.
    		while (!solver.isSlotValueOfFact(WME_NAME_PROBLEM, "done", new Value(true))) {

    			System.out.println("Printing rule activations...\n");
    			solver.printRuleActivations();
    	    	
    			solver.getRete().run(1);
    			
    			System.out.println("Printing WME...\n");
    	    	solver.printFacts();
    	    	System.out.println("... after running " + ++numIterations + " times.\n");
    	    	
    		}

    	} catch (JessException e) {
			e.printStackTrace();
		}
    }
    */


}
