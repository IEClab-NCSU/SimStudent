package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

//a StdOpRule is a rule that uses one of the standard operators
//  (add, subtract, multiply or divide)
//This operates just like a regular rule, except that action and input match
//if they match normally or if the inverse operators match

public class StdOpRule extends Rule {
	//private static SymbolManipulator sm = new SymbolManipulator();

	public StdOpRule(String nme, Test[] cond,String act,String inp,String[] mess) {
		super(nme,cond,act,inp,mess);
		verifyRule(act,inp);
	}
			
	public StdOpRule(String nme, Test[] cond,String act,String inp) {
		super(nme,cond,act,inp);
		verifyRule(act,inp);
	}

	public StdOpRule(String nme, Test cond,String act,String inp,String[] mess) {
		super(nme,cond,act,inp,mess);
		verifyRule(act,inp);
	}
	
	public StdOpRule(String nme, Test cond,String act,String inp) {
		super(nme,cond,act,inp);
		verifyRule(act,inp);
	}

	private void verifyRule(String action,String input) {
		if (!action.equalsIgnoreCase("Add") &&
			!action.equalsIgnoreCase("Subtract") &&
			!action.equalsIgnoreCase("Multiply") &&
			!action.equalsIgnoreCase("Divide"))
			throw new IllegalArgumentException("StdOpRule can only use ADD, SUBTRACT, MULTIPLY or DIVIDE actions");
		if (input.charAt(0) != '{')
			throw new IllegalArgumentException("StdOpRule must get input to evaluate");
	}
	
	protected RuleMatchInfo testActionAndInput(Queryable info,String userAction,String userInput) {
		if (isTraced())
			trace.out("  in TAI, stdoprule...");
		//first, check to see if the action and input work as given
		RuleMatchInfo passes = super.testActionAndInput(info,userAction,userInput);
		if (!(passes.getBoolean())) {
			String newAction;
			String newInput = userInput;
			if (userAction.equalsIgnoreCase("Multiply"))
				newAction = "Divide";
			else if (userAction.equalsIgnoreCase("Divide"))
				newAction = "Multiply";
			else if (userAction.equalsIgnoreCase("Add"))
				newAction = "Subtract";
			else //must be subtract so use Add
				newAction = "Add";
			try {
				if (userAction.equalsIgnoreCase("Multiply") ||
					userAction.equalsIgnoreCase("Divide"))
						newInput = sm.reciprocal(userInput);
				else if (userAction.equalsIgnoreCase("Add") ||
						userAction.equalsIgnoreCase("Subtract"))
					newInput = sm.negate(userInput);
			}
			catch (BadExpressionError e) {
				trace.out("Bad expression checking alternate action and input in "+getName());
			}
			if (isTraced())
				trace.out("  StdOpRule checking alternate action and input: "+newAction+"::"+newInput);
			passes = super.testActionAndInput(info,newAction,newInput);
		}
		return passes;
	}
}
