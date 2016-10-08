package edu.cmu.pact.miss;

import cl.tutors.solver.SolverTutor;
import cl.utilities.TestableTutor.InvalidParamException;
import cl.utilities.TestableTutor.SAI;

public class NextStepTutor {

	public NextStepTutor() {
		// TODO Auto-generated constructor stub
		initSolverTutor();
	}
	
	private SolverTutor solverTutor;
	private static final String HINTMESSAGE = "HINTMESSAGE";

	public SolverTutor getSolverTutor() {
		return this.solverTutor;
	}

	public void setSolverTutor(SolverTutor solverTutor) {
		this.solverTutor = solverTutor;
	}

	// initialize the SolverTutor object here
	private void initSolverTutor() {
		try {
			setSolverTutor(new SolverTutor());
			// When TypeInMode is false, the tutor does typein but in a
			// none-simplified form
			getSolverTutor().setParameter("Solver", "TypeInMode", "true");
			getSolverTutor().setParameter("Solver", "AutoSimplify", "true");
			getSolverTutor().setCltPmWholeSide(true);
		} catch (InvalidParamException e) {
			e.printStackTrace();
		}
	}

	// set to be positive when the last skill applied is basic arithmetic skills
	// namely, +, -, *, or /
	// This value must be reset to 0 when a new problem is started
	// isBasicSkillApplied() throws an exception when called on 0
	private int basicSkillApplied = 0;
	private boolean isNonBasicSkillApplied() {
		return basicSkillApplied < 0;
	}
	// Must be reset by startProblem() and updated by doStep() for non-basic skill
	private String[] typeinExpressions = new String[2];
	
	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
	// Ask for a hint on what to do next
	public SAI getNextStep() {
		SAI nextStep = whatToDoNext();
		String msg = HINTMESSAGE + ";" + 
			nextStep.getSelection() + ";" + nextStep.getAction() + ";" + nextStep.getInput();
		System.out.println("getNextStep >> " + msg);
		//replyMessage(msg);
		return nextStep;
	}

	private SAI whatToDoNext() {
		// System.out.println("whatToDoNext called ...");
		SAI nextStep = null;
		if (isNonBasicSkillApplied()) {
			for (int i = 0; i < 2; i++) {
				if (typeinExpressions[i] != null) {
					// i = 0 for "left" and 1 for "right"
					nextStep = new SAI("", i == 0 ? "left" : "right", typeinExpressions[i]);
					break;
				}
			}
		} else {
			nextStep = this.solverTutor.getNextStep("tutor");
		}
		
		return nextStep;
	}

}
