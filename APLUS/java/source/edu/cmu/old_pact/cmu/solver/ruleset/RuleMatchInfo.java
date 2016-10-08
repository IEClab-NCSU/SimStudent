package edu.cmu.old_pact.cmu.solver.ruleset;

public class RuleMatchInfo {
	private boolean bval;
	private Rule rule;
	private String action;
	private String input;
	
	public RuleMatchInfo() {
		bval = false;
		rule = null;
		action = null;
		input = null;
	}
	
	public RuleMatchInfo(boolean b,Rule rul,String act,String inp) {
		bval = b;
		rule = rul;
		action = act;
		input = inp;
	}

	public boolean getBoolean() {
		return bval;
	}
	
	public void setBoolean(boolean b) {
		bval = b;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public void setRule(Rule rul) {
		rule = rul;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String act) {
		action = act;
	}
	
	public String getInput() {
		return input;
	}
	
	public void setInput(String inp) {
		input = inp;
	}
}