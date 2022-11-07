package cl.tutors.solver;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
//import SimStAlgebraV8.SimStAlgebraV8;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.ProblemChecker;
import edu.cmu.pact.miss.SimStAlgebraV8.SimStAlgebraV8;
import edu.cmu.pact.miss.console.controller.MissController;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class ProblemCheckerTest extends TestCase {
	private CTAT_Launcher launcher ;
	public ProblemCheckerTest(String classname) {
		super(classname);
	}
	public void setUp() {
		
		
		// instiatinate the SImSt object 
		trace.out("setup");
	}
	
	public void testCalculate() {
		trace.out("Successfully called");
	}
	public void testIsSolvable() { 
		String tutorArg="";
		tutorArg+=" -ssRunInPLE";
		tutorArg+=" -debugCodes miss";
		String[] argv = tutorArg.split(" ");
		
		launcher = new CTAT_Launcher(argv);
	
        launcher.getFocusedController().initializeSimSt();
    	//trace.out(" Reason  : "+launcher.getFocusedController().getMissController().getSimSt());
		//assertEquals("Is 3x+3=12 solvable ? ",true,launcher.getFocusedController().getMissController().getSimSt().isSolvable("ClOracle","3x+3_12", launcher.getFocusedController()));
		//assertEquals("Is 3x+3=3x+12 solvable ? ",false,launcher.getFocusedController().getMissController().getSimSt().isSolvable("ClOracle","3x+3_3x+12", launcher.getFocusedController()));
		
	}

	public static void main(String[] args) {
		TestCase test = new ProblemCheckerTest("testIsSolvable");
		test.run();
	}
}
