package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.Expression;
import edu.cmu.old_pact.cmu.sm.query.Queryable;


//A SideRule is a rule that checks to see whether some operation (typically some kind of
//simplification) can be performed on the left, right or both sides.

public class SideRule extends Rule {
	
	//private static SymbolManipulator sm = new SymbolManipulator();

	public SideRule(String name,Test cond,String act,String[] mess) {
		super(name,cond,act,"",mess);
	}
	
	public SideRule(String name,Test cond[],String act,String[] mess) {
		super(name,cond,act,"",mess);
	}

	public SideRule(String name,Test cond[],String act){
		super(name,cond,act,"");
	}

	public SideRule(String name,Test cond,String act){
		super(name,cond,act,"");
	}

	public RuleMatchInfo canFire(Queryable info,String userAction,String userInput) {
		//System.out.println("in canFire Rule name = "+name);
		RuleMatchInfo tempRuleMatchInfo = null;
		if(testConditionsForHelp(info)){
			tempRuleMatchInfo = testActionAndInput(info,userAction,userInput);
		}
		return (tempRuleMatchInfo);
	}
	
	//override testConditionsForHelp, so that conditions apply to the left and right sides of the
	//equation, not the whole thing
	public boolean testConditionsForHelp(Queryable info) {
		boolean leftPassed = false;
		boolean rightPassed = false;
		
		input = ""; //we set the rule input as a function of the matching here...
		leftPassed = testSide((Equation)info,"left");
		rightPassed = testSide((Equation)info,"right");
		if (leftPassed && rightPassed)
			input = "both";
		else if (leftPassed)
			input = "left";
		else if (rightPassed)
			input = "right";
		if (input.equals(""))
			return false;
		else
			return true;
	}
		
	private boolean testSide(Equation info, String side) {
		if (isTraced())
			System.out.println("  Testing rule: "+getName()+" on "+side+" side");
		boolean OK = true;
		for (int i=0;i<conditions.length && OK;++i) {
			Expression theSide;
			if (side.equals("left"))
				theSide = info.getLeft();
			else
				theSide = info.getRight();
			boolean passed = conditions[i].passes(theSide,true);
			if (isTraced())
				System.out.println("   testing condition "+conditions[i]+" on "+side+"; passes: "+passed);
			if (!passed)
				OK = false;
		}
		if (isTraced())
			System.out.println("   All conditions for "+getName()+" on "+side+" pass: "+OK);
		return OK;
	}

	//override getMessages to substitute "the left side," "the right side" or "both sides" for {*side*}	
	public String[] getMessages(Equation info) {
//		System.out.println("in getMessages, input is "+input+"::"+getName());
//		System.out.println("equation is "+info);
		if(messages != null){
			Expression theSide;
			if (input.equals("right"))
			theSide = info.getRight();
			else
			theSide = info.getLeft();
			//System.out.println("theSide is "+theSide);
			//System.out.println("messages is "+messages);
			//This is a little strange -- if we can simplify on both sides, we will provide an example
			//from the left side but suggest to simplify (or whatever) on both sides
			String resolvedMessages[] = new String[messages.length];
			for (int i=0;i<messages.length;++i) {
				String subMessage = subSide(messages[i]);
				resolvedMessages[i] = resolveMessage(subMessage,theSide);
			}
			if (isTraced())
			resolvedMessages[0] = getName()+": "+resolvedMessages[0];
			return resolvedMessages;
		}
		else{
			System.out.println("SideRule.getMessages: Warning: rule '" + getName() +
							   "' fired for help but has no messages");
			return new String[] {"Sorry, I can't help you here."};
		}
	}
	
	public String subSide(String message) {
//		System.out.println("in subSide with "+message);
		int sideMarker = message.indexOf("{*side*}");
		if (sideMarker >= 0) {
			String sidePhrase;
			if (input.equalsIgnoreCase("left"))
				sidePhrase = "the left side";
			else if (input.equalsIgnoreCase("right"))
				sidePhrase = "the right side";
			else
				sidePhrase = "both sides";
			String newMessage = message.substring(0,sideMarker)+sidePhrase+message.substring(sideMarker+8);
//			System.out.println("subside about to recurse with "+newMessage);
			return subSide(newMessage);
		}
		else
			return message;
	}	
}
