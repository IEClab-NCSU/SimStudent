package edu.cmu.pact.miss;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.PeerLearning.SimStConversation;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowUtilities;
import edu.cmu.pact.miss.minerva_3_1.StepAbstractor;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/*
 * AlgebraProblemAssessor - determines information about problems
 * 		in an Algebra domain.
 */
public class AlgebraProblemAssessor extends ProblemAssessor {

	//Constants describing types algebra problems can be classified into
	public static final String UNKNOWN_EQUATION = "Unknown";
	public static final String ONE_STEP_EQUATION = "OneStep";
	public static final String TWO_STEP_EQUATION = "TwoStep";
	public static final String BOTH_SIDES_EQUATION = "BothSides";
	public static final String BOTH_SIDES_COMPLEX_EQUATION = "BothSidesComplex";
	public static final String COMPLEX_EQUATION = "Complex";
	public static final String SIMPLIFY_EQUATION = "Simplify";
	public static final String COMBINE_CONSTANT_TERMS_EQUATION = "CombineConstantTerms";
	
	public static final String NO_SOLUTION = "?";
	
	
	@Override
	/*
	 * abstractProblem - abstracts the basic pattern of the problem
	 * @param problem - the name of the problem to be abstracted
	 * @return the problem pattern using a Minerva abstracted format
	 */
	public String abstractProblem(String problem) {
		StepAbstractor abstractor = new StepAbstractor();
		String abstracted = abstractor.signedAbstraction(SimSt.convertFromSafeProblemName(problem));
		return abstracted;
	}

	@Override
	/*
	 * classifyProblem - determines the classification of the type of problem
	 * @param - the name of the problem to be classified
	 * @return the type of the problem, one of: one step, two step, variables on both sides,
	 * 		complex with variables on both sides, complex and simplify
	 */
	public String classifyProblem(String problem) {
		String abstracted = abstractProblem(problem);
		String[] parts = abstracted.split("=");
			//Has no equal sign or more than 1
		if(parts.length > 2 || parts.length < 2) return UNKNOWN_EQUATION;
			//Has no variables
		if(!parts[0].contains("v") && !parts[1].contains("v")) return UNKNOWN_EQUATION;
			//Has variables on both sides
		if(parts[0].contains("v") && parts[1].contains("v"))
		{
				//Also has parentheses, fractions or needs to clt variables
			if(parts[0].contains("(") || parts[1].contains("(") || parts[0].split("v").length > 2
					|| parts[1].split("v").length > 2 || parts[0].contains("/") || parts[1].contains("/"))
				return BOTH_SIDES_COMPLEX_EQUATION;
			return BOTH_SIDES_EQUATION;
		}
		//Not a bothSides equation, but has parentheses, fractions or needs to clt variables
		if(parts[0].contains("(") || parts[1].contains("(") || parts[0].split("v").length > 2
				|| parts[1].split("v").length > 2 
				|| (parts[0].contains("/") && parts[0].indexOf('/') < parts[0].indexOf('v')) 
				|| (parts[1].contains("/") && parts[1].indexOf('/') < parts[1].indexOf('v')))
			return COMPLEX_EQUATION;
			//We already know there's only a variable on one side - if it's only the variable,
			//the other side just needs simplified at most
		if(parts[0].equals("v") || parts[1].equals("v")) return SIMPLIFY_EQUATION; 
			//One side is just a constant
		if(isNumber(parts[0]))
		{
				//Special case for just negative variable
			if(parts[1].equals("-v")) return ONE_STEP_EQUATION;
				//Special case for negative right before the variable, ie 4-x, -x+4, but not -x
			if(parts[1].contains("-v") ) return TWO_STEP_EQUATION;
				//And the other side has 2 numbers, ie 4x-3
			if( countNumbers(parts[1]) == 2) return TWO_STEP_EQUATION;
				//Or 1 number, ie 4x or x-3
			if( countNumbers(parts[1]) == 1) return ONE_STEP_EQUATION;
			  	//has multiple constant terms, but otherwise two steps
			if( countNumbers(parts[1]) > 2) return COMBINE_CONSTANT_TERMS_EQUATION;
		}
		if(isNumber(parts[1]))
		{
				//Special case for just negative variable
			if(parts[0].equals("-v")) return ONE_STEP_EQUATION;
				//Special case for negative right before the variable, ie 4-x, -x+4, but not -x
			if(parts[0].contains("-v") ) return TWO_STEP_EQUATION;
			
			if( countNumbers(parts[0]) == 2) return TWO_STEP_EQUATION;
			if( countNumbers(parts[0]) == 1) return ONE_STEP_EQUATION;
			  	//has multiple constant terms, but otherwise two steps
			if( countNumbers(parts[0]) > 2) return COMBINE_CONSTANT_TERMS_EQUATION;
		}
		
		return UNKNOWN_EQUATION;
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
	/*
	 * formatSolution - neatly formats the solution to the problem into HTML for display
	 * @param path - the steps taken in reaching the final answer to the problem
	 * @param problem - the name of the problem being solved
	 * @return an html formatted string of the solution path with correct steps colored
	 * 		green and incorrect steps colored red
	 */
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
	/*
	 * isProblemComplete - determines if work on the problem is complete with all the steps
	 * 		correct along the way
	 * @param problem - the name of the problem being worked on
	 * @param solutionPath - all of the steps taken in solving the problem
	 * @return true if the problem's steps are all correct and no further steps are needed, or
	 * 		false if either further steps are needed or there is an incorrect step
	 */
	public boolean isProblemComplete(String problem, Vector<ProblemEdge> solutionPath) {
			 
		String[] problemParts = problem.split("=");
    	if(problemParts.length < 2)
    	{
    		return false;
    	}
    	String firstPrev = problemParts[0];
    	String secondPrev = problemParts[1];
    	if(solutionPath == null || solutionPath.size() == 0 )
    	{
    		return false;
    	}
        else
        {
        	for (ProblemEdge edge : solutionPath) {
        		
        		if(edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
        		{
        			//A completed answer ends with done, with the equation before it having one side containing
        			//only a variable, and the other side not containing that variable
        			//(Will not get to this point if the steps were incorrect)
        			// Doesn't work for x=99-9 sort of cases where the students put done at the very first step
        			// As a result this is marked as correct even when it is not
        			if(firstPrev.length() == 1 && Character.isLetter(firstPrev.charAt(0)))
        			{
        				boolean valid = true;
        				for(int i=0; i< secondPrev.length(); i++) {

        					if(((secondPrev.charAt(0) == '-') && i==0) || (secondPrev.charAt(i) == '.')) // Ignore if the number is a negative or has a decimal in it for example x = 23.5

        						continue;
        					if(secondPrev.charAt(i) == '/')
        					{
        						if(secondPrev.substring(i+1).contains("/"))
        						{
        							valid = false;
        							break;
        						}
        					}
        					else if(!Character.isDigit(secondPrev.charAt(i))) {
        						valid = false;
        						break; 	
        					}
        				}
        				if(secondPrev.contains(firstPrev) || secondPrev.contains(" ") || !valid)
        				{
        		    		return false;
        				}
        				else
        					return true;
        			}
        			if(secondPrev.length() == 1 && Character.isLetter(secondPrev.charAt(0)))
        			{
        				boolean valid = true;
        				for(int i=0; i< firstPrev.length(); i++) {

        					if(((firstPrev.charAt(0) == '-') && i==0)|| (firstPrev.charAt(i) == '.')) 

        						continue;
        					if(firstPrev.charAt(i) == '/')
        					{
        						if(firstPrev.substring(i+1).contains("/"))
        						{
        							valid = false;
        							break;
        						}
        					}
        					else if(!Character.isDigit(firstPrev.charAt(i))) {
        						valid = false;
        						break;
        					}
        				}
        				if(firstPrev.contains(secondPrev) || firstPrev.contains(" "))
        				{
        		    		return false;
        				}
        				else
        					return true;
        			}
        			
        		}
        		if(!edge.isCorrect())
        		{
		    		return false;
        		}
        		firstPrev = secondPrev;
        		secondPrev = edge.getInput();
        	}
        }
        
        return false;
	}

	@Override
	/*
	 * isSolution - determines if a solution is a correct solution to a problem, 
	 * 		irrespective of the steps taken to reach it
	 * @param problem - the name of the problem being solved
	 * @param solution - the solution to that problem being checked
	 * @return true if the solution is a valid solution to the problem, false otherwise
	 */
	public boolean isSolution(String problem, String solution) {
		return GameShowUtilities.isSolution(SimSt.convertFromSafeProblemName(problem), solution);
	}
	
	@Override
	/*
	 * determineSolution - given the steps used to reach a solution, determine what
	 * 		that final solution is
	 * @param problem - the name of the problem which is solved
	 * @param solutionPath - the steps taking in reaching the solution
	 * @return the solution, or "?" if it is unable to be determined, such as if the
	 * 		problem does not end with Done
	 */
	public String determineSolution(String problem, Vector<ProblemEdge> solutionPath) {
		if (solutionPath==null) return NO_SOLUTION;
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
	/*
	 * determineSolution - given the root node of a graph of a solution, determine what
	 * 		the final solution is
	 * @param problem - the name of the problem which is solved
	 * @param node - the root node of the solution's graph
	 * @return the solution, or "?" if it is unable to be determined, such as if the
	 * 		problem does not end with Done
	 */
	public String determineSolution(String problem, ProblemNode node) {
		Vector<ProblemEdge> path = findSolutionPath(node);
		return determineSolution(problem,path);
	}

	@Override
	/*
	 * findSolutionPath - compile the list of steps taken in reaching a solution
	 * @param node - the root node of the solution's graph
	 * @return a vector containing a list of the steps taken in order, not including
	 * 		any backtracking.
	 */
	public Vector<ProblemEdge> findSolutionPath(ProblemNode node) {
		return node.findSolutionPathAlgebra();
	}

	@Override
	public String calcProblemStepString(ProblemNode startNode, ProblemNode currentNode, String lastOperand) 
	{
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

	/*
	 * (non-Javadoc)
	 * @see edu.cmu.pact.miss.ProblemAssessor#performInteractiveAnswerCheck(edu.cmu.pact.miss.PeerLearning.SimStPLE, java.lang.String, java.lang.String)
	 */
	public boolean performInteractiveAnswerCheck(SimStPLE ple, String problem, String solution) 
	{ 
		DecimalFormat df = new DecimalFormat("#.#####");
		if(ple == null)
			return true;
		
		if (solution.equals(NO_SOLUTION))
			return true;
		
		SimStConversation conversation = ple.getConversation();
		String var = determineVariable(solution);
		String value = determineValue(solution);
		String[] problemParts = SimSt.convertFromSafeProblemName(problem).split("=");
		boolean correctness = false;
		if(var.length() == 0 || value.length() == 0)
		{
			String message = conversation.getMessage(SimStConversation.NO_VAR_VALUE_CHECK_ANS);
			ple.giveMessage(message);
			pause();
		}
		else
		{
			String message = conversation.getMessage(SimStConversation.CHECK_ANSWER, SimSt.convertFromSafeProblemName(problem), null, value, var, -1);
			ple.giveMessage(message);
			pause();
			ArrayList<String> startStates = ple.getStartStateElements();
			Double[] plugIns = new Double[2];
			String[] plugStrs = new String[2];
			for(int i=0;i<startStates.size()&&i<problemParts.length&&i<2;i++)
			{
				//String selection = "in" + ple.getComponentName(startStates.get(i));
				String selection = ple.getComponentName(startStates.get(i));
				String plugged = plugInValue(problemParts[i], var, value);
				plugIns[i] = evaluateExpression(plugged);
				if(plugIns[i] - plugIns[i].intValue() == 0)
					plugStrs[i] = ""+plugIns[i].intValue();
				else
					plugStrs[i] = ""+df.format(plugIns[i]);
				String plugExpr = "";
				if(problemParts[i].equals(plugStrs[i]))
					plugExpr = plugStrs[i];
				else
					plugExpr = plugged+" or "+plugStrs[i];
				trace.out("ss", "ProblemParts[i] "+problemParts[i]);
				trace.out("ss", "Plugged "+plugged);
				trace.out("ss", "PlugIns[i] "+plugIns[i]);
				trace.out("ss", "PlugStrs[i] "+plugStrs[i]);
				trace.out("ss", "PlugExpr "+plugExpr);
				if(problemParts[i].contains(var))
					ple.giveMessage(conversation.getMessage(SimStConversation.PLUG_IN, selection, null, problemParts[i], plugExpr, i));
				else
					ple.giveMessage(conversation.getMessage(SimStConversation.NO_VAR_TO_PLUG, selection, null, problemParts[i], plugExpr, i));
				pause();
				
			}
			correctness = (Math.abs(plugIns[0] - plugIns[1]) < .0001);
			if(correctness)
			{
				ple.giveMessage(conversation.getMessage(SimStConversation.BALANCE_CHECK_ANSWER, "", null, plugStrs[0], ""+plugStrs[1], -1));
			}
			else
			{
				ple.giveMessage(conversation.getMessage(SimStConversation.NO_BALANCE_CHECK_ANSWER, "", null, ""+plugStrs[0], ""+plugStrs[1], -1));
			}
			pause();
			
		}
				
		return correctness;
	}
	
	private void pause()
	{
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private String plugInValue(String expression, String var, String value)
	{
		String result = expression.replaceAll(var, "("+value+")");
		return GameShowUtilities.insertMultiplySymbols(result);
	}
	
	private double evaluateExpression(String expression)
	{
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		String eval = null;
		try {
			eval = ""+engine.eval(expression);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return Double.parseDouble(eval);
	}
	
	private String determineVariable(String equation)
	{
		String var = "";
		for(int j=0;j<equation.length();j++)
		{
			if(Character.isLetter(equation.charAt(j)))
				var = ""+equation.charAt(j);
		}
		
		return var;
	}
	
	private String determineValue(String equation)
	{
		String[] solParts = equation.split("=");
		for(int i=0;i<solParts.length;i++)
		{
			boolean containsVar = false;
			for(int j=0;j<solParts[i].length();j++)
			{
				if(Character.isLetter(solParts[i].charAt(j)))
					containsVar = true;
			}
			if(!containsVar)
				return solParts[i];
		}
		return "";
	}

}
