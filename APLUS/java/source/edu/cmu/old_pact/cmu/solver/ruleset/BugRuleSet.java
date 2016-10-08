package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;

//a BugRuleSet is a list of bug rules

public class BugRuleSet extends RuleSet {
	public BugRuleSet(Rule[] theRules) {
		super(theRules);
	}

	public BugRuleSet(Rule[] theRules,int numRules){
		super(theRules,numRules);
	}

	public Rule findRuleToFire(Equation info,String action,String input,String desiredAction,String desiredInput) {
		Rule ruleToFire = null;
		for (int i=0;i<numRules() && ruleToFire==null;++i) {
			BugRule thisRule = (BugRule)(getRule(i));
			if(thisRule.isTraced()){
				System.out.println("checking bug rule "+thisRule.getName());
				System.out.println(info.toString()+action+input+desiredAction+desiredInput);
			}
			if (thisRule.canFire(info,action,input,desiredAction,desiredInput).getBoolean())
				ruleToFire = thisRule;
		}
		return ruleToFire;
	}
}
		
