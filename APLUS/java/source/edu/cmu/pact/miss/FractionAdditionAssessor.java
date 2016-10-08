package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowUtilities;
import edu.cmu.pact.miss.minerva_3_1.StepAbstractor;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class FractionAdditionAssessor extends ProblemAssessor {

	public static final String UNKNOWN_FRACTION = "Unknown";
	
	public static final String NO_SOLUTION = "?";
	
	@Override
	public String abstractProblem(String problem) {
		StepAbstractor abstractor = new StepAbstractor();
		String abstracted = abstractor.signedAbstraction(SimSt.convertFromSafeProblemName(problem));
		return abstracted;
	}

	@Override
	public String classifyProblem(String problem) {
		return UNKNOWN_FRACTION;
	}

	@Override
	public boolean isProblemComplete(String problem,
			Vector<ProblemEdge> solutionPath) {
		

			
			
			if(solutionPath == null || solutionPath.size() < 7 ){
	    		return false;
	    	}
			
			for (ProblemEdge edge : solutionPath) {
				if(edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
        		{
					return true;
        		}
				if (!edge.isCorrect()) return false;	
			}
			return false;
		
	}
	
	/*
	 * isNumber - determines if a string represents just a number or negative number in abstracted form,
	 * 		ie B, C, -B, -C.
	 * @param value - the string that may represent a number
	 * @return true if the string represents an abstracted number or negative number, or false otherwise
	 */
	private boolean isNumber(String value)
	{
		if(value.length() == 1 && Character.isLetter(value.charAt(0)) && value.charAt(0) != 'v')
			return true;
		if(value.startsWith("-") && value.length() == 2 
				&& Character.isLetter(value.charAt(1)) && value.charAt(1) != 'v')
			return true;
		return false;
	}
	
	private int countNumbers(String value)
	{
		int count = 0;
		for(int i=0;i<value.length();i++)
		{
			if(Character.isLetter(value.charAt(i)) && value.charAt(i) != 'v')
				count++;
		}
		return count;
	}

	@Override
	public boolean isSolution(String problem, String solution) {
		//return GameShowUtilities.isSolution(SimSt.convertFromSafeProblemName(problem), solution);
		//Fraction addition has no variables so the function above cannot be used
		return false;
	}

	@Override
	public String formatSolution(Vector<ProblemEdge> path, String problem) {
String eqTemplate = "LHS = RHS  [SKILL]";
        
        String solution = "";
        String step = eqTemplate.replace("LHS = RHS", problem);
                                
        for (ProblemEdge edge : path) {
            // Selection: "dorminTable1_C3R2"
            String selection = edge.getSelection();
            if(selection.equalsIgnoreCase(Rule.DONE_NAME)) 
            {
            	String color = (isProblemComplete(problem,path) ? "green" : "red");
                            	
            	String input = "<font color=" + color + "><b>" + edge.getSelection() + "</b></font>";
                step = step.replaceAll("SKILL", input);
                solution += step + "<br>";
                step = eqTemplate;
            	continue;
            }
            int idx = selection.indexOf("_") -1;
            if(idx < 0)
            	continue;
            char c = selection.charAt(idx);
            int column = c - '1' +1;

            boolean isCorrect = edge.isCorrect();
            String color = (isCorrect ? "green" : "red");
                       
            String input = "<font color=" + color + "><b>" + edge.getInput() + "</b></font>";
            
            switch (column) {
            case 1: step = step.replace("LHS", input); break;
            case 2: step = step.replace("RHS", input); break;
            case 3: 
                step = step.replaceAll("SKILL", input);
                solution += step + "<br>";
                step = eqTemplate;
                break;
            }
          
        }
        
        if (!step.equals(eqTemplate)) solution += step + "<br>";
        solution = solution.replace("[SKILL]", "");
        
        return solution;
	}

	@Override
	public String determineSolution(String problem,
			Vector<ProblemEdge> solutionPath) {
		ProblemEdge lastStep = solutionPath.get(solutionPath.size()-1);
		if(!lastStep.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
		{
			//If the last step is not done, there is no solution
			return NO_SOLUTION;
		}
		if(solutionPath.size() <= 1)
		{
			//If the only step is done, the problem is the solution
			return SimSt.convertFromSafeProblemName(problem);
		}
		if((solutionPath.size()-1) % 3 != 0)
		{
			//If (not including the done step) the number of steps is not a multiple of 3,
			//then there is no solution.  Because a solution will with a full set of 
			// Transformation, LHS, RHS
			return NO_SOLUTION;
		}
		
		//Solution is 'Second_to_Last_Step=Last_Step'
		String solution = solutionPath.get(solutionPath.size()-3).getInput();  //Second to last step
		solution += "=";
		solution += solutionPath.get(solutionPath.size()-2).getInput(); //Last step
				
		return solution;
	}

	@Override
	public String determineSolution(String problem, ProblemNode node) {
		Vector<ProblemEdge> path = findSolutionPath(node);
		return determineSolution(problem,path);
	}

	@Override
	public Vector<ProblemEdge> findSolutionPath(ProblemNode node) {
		return node.findSolutionPathAlgebra();
	}

	@Override
	public String calcProblemStepString(ProblemNode startNode,
			ProblemNode currentNode, String lastOperand) {
		
		if(currentNode == null)
        	return "";
    	String lastEquation = findLastStep(startNode, currentNode);
    	    	    	
        if(lastEquation == null)
        	lastEquation = startNode.getName();
        
        boolean useOperand = false;
        if(currentNode.getInDegree() > 0)
        {
        	ProblemEdge edge = (ProblemEdge) currentNode.getIncomingEdges().get(currentNode.getInDegree()-1);
        	
        	if(edge.getSource().getInDegree() > 0)
        	{
        		ProblemEdge prevEdge = (ProblemEdge) edge.getSource().getIncomingEdges().get(edge.getSource().getInDegree()-1);
        		if(edge.getSelection().contains("dorminTable3_") || prevEdge.getSelection().contains("dorminTable3_"))
        			useOperand = true;
        	}
        	else
        	{
        		useOperand = true;
        	}
        }
        if(lastOperand == null)
        	lastOperand = findLastOperand(startNode, currentNode);
    	if (useOperand && lastOperand != null) 
        {
            lastEquation += "[" + lastOperand + "]";
        }
    	return lastEquation;
	}

	@Override
	public String findLastStep(ProblemNode startNode, ProblemNode problemNode) {
		Vector /* ProblemEdge */ pathEdges = InquiryClAlgebraTutor.findPathDepthFirst(startNode, problemNode);
        String lastEquation = (pathEdges != null ? lastEquation(pathEdges) : problemNode.getName());
        // lastEquation() may return null
        if (lastEquation == null) 
            lastEquation = startNode.getName();
        return lastEquation;
	}

	
private String lastEquation(Vector /* ProblemEdge */ pathEdges) {
        
    	String lastEquation = null;
    	int edgeCount = 0;
    	ProblemEdge[] edgeQueue = new ProblemEdge[3];
    	
    	for (int i = 0; i < pathEdges.size(); i++) {
    		    		
    		edgeQueue[edgeCount++] = (ProblemEdge)pathEdges.get(i);

    		if (edgeCount == 3) {
    			String[] eqSide = new String[2];
    			for (int j = 0; j < 2; j++) {
    				EdgeData edgeData = edgeQueue[j+1].getEdgeData();
    				String input = (String)edgeData.getInput().get(0);
    				eqSide[j] = input;
    			}
    			
    			lastEquation = eqSide[0] + "=" + eqSide[1];
    			edgeCount = 0;
    			for (int k = 0; k < 3; k++) {
    				edgeQueue[k] = null;
    			}
    		}
    	}
    	return lastEquation;
    }

	@Override
	public String findLastOperand(ProblemNode startNode, ProblemNode problemNode) {
		Vector /* ProblemEdge */ pathEdges = InquiryClAlgebraTutor.findPathDepthFirst(startNode, problemNode);
		String lastOperand = "";
		
		if(pathEdges == null) return lastOperand;
		
		int edgeCount = 0;
    	ProblemEdge[] edgeQueue = new ProblemEdge[3];
    	
    	for (int i = 0; i < pathEdges.size(); i++) {
    		    		
    		edgeQueue[edgeCount++] = (ProblemEdge)pathEdges.get(i);

    		if(edgeCount == 1)
    		{
				EdgeData edgeData = edgeQueue[edgeCount-1].getEdgeData();
    			String lastSkill = (String)edgeData.getInput().get(0);
    		    if (EqFeaturePredicate.isValidSimpleSkill(lastSkill.split(" ")[0])) {
    		    	lastOperand = lastSkill;
    	    	}
    		}
    		if (edgeCount == 3) {
    			edgeCount = 0;
    			for (int k = 0; k < 3; k++) {
    				edgeQueue[k] = null;
    			}
    		}
    		
    	}
    	return lastOperand;
	}

}
