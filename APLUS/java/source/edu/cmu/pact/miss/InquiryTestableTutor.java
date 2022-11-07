/**
 * 
 */
package edu.cmu.pact.miss;

import cl.utilities.TestableTutor.SAI;
import cl.utilities.TestableTutor.TestTRETutor;
import cl.utilities.TestableTutor.TestableTutor;


/**
 * @author mazda
 *
 */
public class InquiryTestableTutor {
	
//	SolverTutor solverTutor = null;
	SAI[] rat = null;
	String[] hintMsg = null;
	TestTRETutor tutor = null;
	TestableTutor tt = null;
	
	public InquiryTestableTutor () {}
	
	void init() {
		tutor = new TestTRETutor("");
		tt = tutor.getTestableTutor();

//		tt.startProblem("3x=9");

//		rat = solverTutor.getAllNextSteps("");
//		try {
//			hintMsg = solverTutor.getHintMessage(rat[0]);
//		} catch (InvalidStepException e) {
//			e.printStackTrace();
//		}
//		trace.out(hintMsg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		InquiryTestableTutor tt = new InquiryTestableTutor();
		tt.init();
	}
}
