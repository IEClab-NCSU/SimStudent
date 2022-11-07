package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.pact.Utilities.trace;

public class TypeinBugRuleSet extends RuleSet{
	public TypeinBugRuleSet(TypeinBugRule[] theRules){
		super(theRules);
	}

	public TypeinBugRuleSet(TypeinBugRule[] theRules, int len){
		super(theRules,len);
	}

	public Rule findRuleToFire(Equation info,String action,String input,String typeinInput,String expectedInput){
		boolean foundrule = false;
		RuleMatchInfo ruleToFire = null;
		int len = numRules();
		for (int i=0;i<len && foundrule == false;++i) {
			if (Rule.allRulesTraced())
				trace.out("checking rule number "+i);
			ruleToFire = ((TypeinBugRule)getRule(i)).canFire(info,action,input,typeinInput,expectedInput);
			if (ruleToFire!=null && ruleToFire.getBoolean()==true) {
				foundrule = true;
				ruleToFire.setRule(getRule(i));
			}
			else {
				ruleToFire = null;
			}
		}
		if(ruleToFire == null){
			return null;
		}
		else{
			return ruleToFire.getRule();
		}
	}
}
