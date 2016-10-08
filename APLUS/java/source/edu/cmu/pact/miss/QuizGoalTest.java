package edu.cmu.pact.miss;

import java.util.Vector;

import aima.search.framework.GoalTest;
import cl.tutors.solver.SolverTutor;
import cl.utilities.TestableTutor.InvalidParamException;
import cl.utilities.TestableTutor.SAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class QuizGoalTest implements GoalTest {

	private static HashMap hm = new HashMap();
	
	/*
	 * isGoalState asks the CL Oracle if we are at the solution state.
	 * If so it returns true, else it returns false
	 * (non-Javadoc)
	 * @see aima.search.framework.GoalTest#isGoalState(java.lang.Object)
	 */
	@Override
	public boolean isGoalState(Object state) {
		// TODO Auto-generated method stub
		boolean isGoalState = false;
		
		QuizState quizState = (QuizState) state;
		
		// Check if the solution of the equation is already cached in the hashmap
		if(hm.containsKey(quizState.getOriginalEqn())) {
			isGoalState = checkGoalReached(quizState);
		} else {
			findSolution(quizState);
			isGoalState = checkGoalReached(quizState);
		}
		
		// Ask the Carnegie Learning Oracle if the goal is reached
		// state has originalEqn (3x+6=15), prevStep of the form (3x+6=15 [divide 3])
		// and the currentStep (x=3)
		// isGoalState = checkGoalReached(quizState);
		
		return isGoalState;
	}

	private void findSolution(QuizState quizState) {
		
		boolean goalReached = false;
		
		SAI finalStep = null;
		ExampleTracerEvent objETEvent = new ExampleTracerEvent(new Object());
		
		SolverTutor solverTutor = getClSolverTutor();
		try {
			solverTutor.setParameter("Solver", "TypeInMode", "false");
			solverTutor.setParameter("Solver", "AutoSimplify", "true");
			solverTutor.setCltPmWholeSide(true);
		} catch (InvalidParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(solverTutor != null) {
			solverTutor.startProblem(quizState.getOriginalEqn());
			finalStep = solverTutor.solveIt(objETEvent, true);		
		}
		else {
			try {
				throw new Exception("SolverTutor not initialized. Aborting");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		hm.put(quizState.getOriginalEqn().trim(), setSolutionInCorrectForm(finalStep.getSelection().trim()));
	}
	
	private boolean checkGoalReached(QuizState quizState) {
	
		boolean goalReached = false;
		
		
		// finalStep.getSelection() has the final solution of the form "x = 3"
		// Compare this against the currentStep to see if the goal has been reached
		// Goal is reached if and only if the current node is a done and the previous
		// node is the actual solution of the equation say "x = 3"
		if(quizState.isDoneStep()) {
			if(hm.get(quizState.getOriginalEqn()).toString().equalsIgnoreCase(setSolutionInCorrectForm(quizState.getPreviousStep()))) {
				goalReached = true;
				// Need to send the ProblemEdge from startNode to the endNode for display of quiz results
				ProblemNode startNode = quizState.getSsInteractiveLearning().brController.getProblemModel().getStartNode();
				ProblemNode endNode = quizState.getProbNode();
				Vector<ProblemEdge> solution = new Vector<ProblemEdge>();
				quizState.getSsInteractiveLearning().brController.findPreferredPath(startNode, endNode, solution);
				quizState.getSimStPLE().setSolutionEdges(solution);
			}
		}
		
		return goalReached;
	}
	
	// Following method aligns the equation passed to it to the form "x = n"
	// This is required to validate the answer 
	private String setSolutionInCorrectForm(String answer) {
		
		String formattedSoln = null;
		String[] solutionParts = answer.split("=");
		
		if(solutionParts.length < 2)
			return answer; 
		
		String firstPart = solutionParts[0].trim();
		String secondPart = solutionParts[1].trim();
		
		if(firstPart.length() == 1 && Character.isLetter(firstPart.charAt(0))) {
			formattedSoln = firstPart + " = " + secondPart;
		} else {
			formattedSoln = secondPart + " = " + firstPart;
		}
		
		return formattedSoln;
	}
	
	public QuizGoalTest() {
		// TODO Auto-generated constructor stub
		setClSolverTutor(new SolverTutor());
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	// 
	private SolverTutor clSolverTutor = null;
	public SolverTutor getClSolverTutor() {
		return clSolverTutor;
	}
	public void setClSolverTutor(SolverTutor clSolverTutor) {
		this.clSolverTutor = clSolverTutor;
	}

	private String originalEquation = "3x+6 = 15";
	
	private String prevStep = "3x = 9";
	private String finalStep = "x = 3";
}
