package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.query.Queryable;


//A CatchallRule is a rule that discovers solutions that it cannot produce
//Basically, the catchall rule checks to see if, when the user's action is applied,
//the equation becomes simpler than the one the user started with.
//This can be used to catch cases where the user combines 2 steps into one
//[e.g. adding -3x-6 to 3x+4=5x+6]
//The CatchallRule can't provide help -- all it can do is verify that an operation
//simplifies the equation

public class CatchallRule extends Rule {
	
//	private static SymbolManipulator sm = new SymbolManipulator();

	public CatchallRule(String name) {
		super(name,"","");
	}
	
	public RuleMatchInfo canFire(Queryable info,String userAction,String userInput) {
		boolean ok = false;
		if (userAction.equalsIgnoreCase("add") ||
			userAction.equalsIgnoreCase("subtract") ||
			userAction.equalsIgnoreCase("multiply") ||
			userAction.equalsIgnoreCase("divide")) {
			try {
				String oldLeft = sm.simplify(info.getProperty("left").getStringValue());
				String oldRight = sm.simplify(info.getProperty("right").getStringValue());
				String leftQuery = "["+userAction+"]"+" ['"+oldLeft+"']"+" ['"+userInput+"']";
//				System.out.println("left Query is "+leftQuery);
				Queryable leftQresult = info.evalQuery(leftQuery);
				String rightQuery = "["+userAction+"]"+" ['"+oldRight+"']"+" ['"+userInput+"']";
				Queryable rightQresult = info.evalQuery(rightQuery);
				if(leftQresult != null && rightQresult != null){
					String newLeft = sm.simplify(leftQresult.getStringValue());
					String newRight = sm.simplify(rightQresult.getStringValue());
					int oldLeftComplexity = sm.complexity(oldLeft);
					int newLeftComplexity = sm.complexity(newLeft);
					int oldRightComplexity = sm.complexity(oldRight);
					int newRightComplexity = sm.complexity(newRight);
					if(isTraced()){
						System.out.println("CR.cF: testing action " + userAction);
						System.out.println("CR.cF: left : " + oldLeft + " [" + oldLeftComplexity + "] ==> " +
										   newLeft + " [" + newLeftComplexity + "]");
						System.out.println("CR.cF: right: " + oldRight + " [" + oldRightComplexity + "] ==> " +
										   newRight + " [" + newRightComplexity + "]");
					}
					if (newLeftComplexity < oldLeftComplexity &&
						newRightComplexity <= oldRightComplexity)
						ok = true;
					else if (newRightComplexity < oldRightComplexity &&
							 newLeftComplexity <= oldLeftComplexity)
						ok = true;
				}
			}
			catch (BadExpressionError err) {
			}
			catch (NoSuchFieldException err) {
			}
		}
		RuleMatchInfo ret = new RuleMatchInfo(ok,this,userAction,userInput);
		return ret;
	}
	
	//since the Catchall rule can't give help, we return false here...
	public boolean testConditionsForHelp(Queryable info) {
		return false;
	}
}
