package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;

public abstract class ProblemAssessor {

	public abstract String abstractProblem(String problem);
	
	public abstract String classifyProblem(String problem);
	
	public abstract boolean isProblemComplete(String problem, Vector<ProblemEdge> solutionPath);
	
	public abstract boolean isSolution(String problem, String solution);
	
	public abstract String formatSolution(Vector<ProblemEdge> results, String problem);
	
	public abstract String determineSolution(String problem, Vector<ProblemEdge> solutionPath);
	
	public abstract String determineSolution(String problem, ProblemNode node);
	
	public abstract Vector<ProblemEdge> findSolutionPath(ProblemNode node);
	
	public abstract String calcProblemStepString(ProblemNode startNode, ProblemNode currentNode, String lastOperand);
	
	public abstract String findLastStep(ProblemNode startNode, ProblemNode problemNode);
	
	public abstract String findLastOperand(ProblemNode startNode, ProblemNode problemNode);
	
	public boolean performInteractiveAnswerCheck(SimStPLE ple, String problem, String solution) 
	{ 
		return true;
	}
}
