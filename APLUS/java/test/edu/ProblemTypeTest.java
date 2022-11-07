package edu;

import cl.tutors.solver.ProblemCheckerTest;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.miss.ProblemAssessor;
import junit.framework.TestCase;

public class ProblemTypeTest extends TestCase {
	private CTAT_Launcher launcher ;
	public ProblemTypeTest(String classname) {
		super(classname);
	}
	public void setUp() {
		
		
		// instiatinate the SImSt object 
		trace.out("setup");
	}
	
	
	public void testClassifyProblem() { 
		String tutorArg="";
		tutorArg+=" -ssRunInPLE";
		tutorArg+=" -debugCodes miss";
		String[] argv = tutorArg.split(" ");
		
		launcher = new CTAT_Launcher(argv);
	
		
		
		ProblemAssessor assess = launcher.getFocusedController().getMissController().getSimSt().getProblemAssessor();
		assertEquals("Equal ?","TwoStep",assess.classifyProblem("-4-9c=-5"));
		trace.out("test case 1 : -4-9c=-5 passed");
		assertEquals("Equal ?","TwoStep",assess.classifyProblem("10=3p-3"));
		trace.out("test case 2 : 10=3p-3 passed");
		assertEquals("Equal ?","Complex",assess.classifyProblem("(a+4)/12=11"));
		trace.out("test case 3 : (a+4)/12=11 passed");
		assertEquals("Equal ?","Complex",assess.classifyProblem("(b+11)/7=2"));
		trace.out("test case 4 : (b+11)/7=2 passed");
		assertEquals("Equal ?","Complex",assess.classifyProblem("(c+10)/4=6"));
		trace.out("test case 5 : (c+10)/4=6 passed");
		assertEquals("Equal ?","TwoStep",assess.classifyProblem("5=-4+8m"));
		trace.out("test case 6 : 5=-4+8m passed");
		assertEquals("Equal ?","OneStep",assess.classifyProblem("-10y=-7"));
		trace.out("test case 7 : -10y=-7 passed");
		assertEquals("Equal ?","OneStep",assess.classifyProblem("2+y=4"));
		trace.out("test case 8 : 2+y=4 passed");
		assertEquals("Equal ?","OneStep",assess.classifyProblem("9m=-10"));
		trace.out("test case 9 : 9m=-10 passed");
		assertEquals("Equal ?","OneStep",assess.classifyProblem("m/9=11"));
		trace.out("test case 10 : m/9=11 passed");
		
		
		
	}

	public static void main(String[] args) {
		TestCase test = new ProblemTypeTest("testClassifyProblem");
		test.run();
	}
}
