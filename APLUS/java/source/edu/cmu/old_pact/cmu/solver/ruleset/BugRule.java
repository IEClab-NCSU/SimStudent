package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;

//Regular strategic rules can use conditions based on properties of the equation and on the user's action
//and input. Bug rules can also define conditions based on the *desired* action and input
public class BugRule extends Rule {
	private String desiredInput=null;
	private String desiredAction=null;

	//constructor for bug rule with multiple conditions
	public BugRule(String nme, Test[] cond,String act,String inp,String mess) {
		super(nme,cond,act,inp);
		messages = new String[1];
		messages[0]=mess;
	}
	
	public BugRule(String nme, Test[] cond,String act,String inp,String[] mess) {
		super(nme,cond,act,inp);
		messages = new String[mess.length];
		for (int i=0;i<mess.length;++i)
			messages[i]=mess[i];
	}
	
	//constructor for bug rule with one condition
	public BugRule(String nme, Test cond,String act,String inp,String mess) {
		super(nme,cond,act,inp);
		messages = new String[1];
		messages[0]=mess;
	}

	public BugRule(String nme, Test cond,String act,String inp,String[] mess) {
		super(nme,cond,act,inp);
		messages = new String[mess.length];
		for (int i=0;i<mess.length;++i)
			messages[i]=mess[i];
	}

	//constructor for bug rule with one condition, using desired input and action
	public BugRule(String nme, Test cond,String act,String inp,String desAct,String desInp,String mess) {
		super(nme,cond,act,inp);
		desiredInput = desInp;
		desiredAction = desAct;
		messages = new String[1];
		messages[0]=mess;
	}
	
	public BugRule(String nme, Test cond,String act,String inp,String desAct,String desInp,String[] mess) {
		super(nme,cond,act,inp);
		desiredInput = desInp;
		desiredAction = desAct;
		messages = new String[mess.length];
		for (int i=0;i<mess.length;++i)
			messages[i]=mess[i];
	}
	
	//constructor for bug rule with no conditions, using desired input and action
	public BugRule(String nme,String act,String inp,String desAct,String desInp,String mess) {
		super(nme,act,inp);
		desiredInput = desInp;
		desiredAction = desAct;
		messages = new String[1];
		messages[0]=mess;
	}

	public BugRule(String nme,String act,String inp,String desAct,String desInp,String[] mess) {
		super(nme,act,inp);
		desiredInput = desInp;
		desiredAction = desAct;
		messages = new String[mess.length];
		for (int i=0;i<mess.length;++i)
			messages[i]=mess[i];
	}

	//constructor for bug rule with no condition, using user's action and input
	public BugRule(String nme,String act,String inp,String mess) {
		super(nme,act,inp);
		messages = new String[1];
		messages[0]=mess;
	}

	public BugRule(String nme,String act,String inp,String[] mess) {
		super(nme,act,inp);
		messages = new String[mess.length];
		for (int i=0;i<mess.length;++i)
			messages[i]=mess[i];
	}

	public RuleMatchInfo canFire(Equation info,String userAction,String userInput,String expectedAction,String expectedInput) {
		RuleMatchInfo inputAndActionOK = new RuleMatchInfo();
		boolean conditionsOK = false;
		
		if (conditions == null)
			conditionsOK = true;
		else
			conditionsOK = testConditionsForHelp(info);
		if (conditionsOK) {
			if (getAction() == null && getInput() == null)
				inputAndActionOK.setBoolean(true);
			//if action but no input, just string-test action
			else if (getAction() != null && getInput() == null) {
				if (getAction().equalsIgnoreCase(userAction))
					inputAndActionOK.setBoolean(true);
			}
			else
				inputAndActionOK = testActionAndInput(info,userAction,userInput);
			if (inputAndActionOK.getBoolean()) {
				RuleMatchInfo desiredOK = new RuleMatchInfo();
				if (desiredInput != null && desiredInput.equalsIgnoreCase("=")) {//if the desiredInput is "=", we check that the user input = the expected input
					desiredOK = Rule.testInput(info,expectedInput,userInput);
				}
				else if (desiredInput != null) {
					desiredOK = Rule.testInput(info,expectedInput,desiredInput);
				}
				else
					desiredOK.setBoolean(true);
				if (desiredOK.getBoolean() == true && desiredAction != null)
					desiredOK.setBoolean(desiredAction.equalsIgnoreCase(expectedAction));
				return desiredOK;
			}
			else {
				inputAndActionOK.setBoolean(false);
				return inputAndActionOK;
			}
		}
		else {
			inputAndActionOK.setBoolean(false);
			return inputAndActionOK;
		}
	}

}
	
