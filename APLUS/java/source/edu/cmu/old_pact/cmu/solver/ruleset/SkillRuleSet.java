package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.pact.Utilities.trace;

//a SkillRuleSet is just a list of SkillRules

public class SkillRuleSet {
	private SkillRule[] rules;
	
	public SkillRuleSet(SkillRule[] theRules) {
		rules = new SkillRule[theRules.length];
		for (int i=0;i<theRules.length;++i)
			rules[i] = theRules[i];
	}
	
	//sometimes it is easier to have a large array with a separate index
	public SkillRuleSet(SkillRule[] theRules,int number) {
		rules = new SkillRule[number];
		for (int i=0;i<number;++i)
			rules[i] = theRules[i];
	}

	public SkillRule findRuleToFire(Equation info,String rulename) {
		SkillRule ruleToFire = null;
//		trace.out("Looking for skill rule, info is "+info+" rule is "+rulename+" "+rules.length+" rules");
		for (int i=0;i<rules.length && ruleToFire==null;++i) {
			if (Rule.allRulesTraced())
				trace.out("checking skill rule number "+i);
			if (rules[i].canFire(rulename,info))
				ruleToFire = rules[i];
		}
		//Can't find skill, so just make one up with same name as rulename
//		if (ruleToFire == null) {
			//trace.out("rule to fire is null");
//			ruleToFire = new SkillRule(rulename);
//			addRule(ruleToFire);
//		}
		return ruleToFire;
	}
	
	public SkillRule[] findAllRulesToFire(Equation info,String rulename) {
		SkillRule[] matchedRules = new SkillRule[rules.length];
		int numSkillsFound=0;
		for (int i=0;i<rules.length;++i) {
			if (rules[i].canFire(rulename,info))
				matchedRules[numSkillsFound++] = rules[i];
		}
		if (numSkillsFound > 0) {
			//trace.out("in findAllRules..., "+numSkillsFound+" skills");
			SkillRule finalRules[] = new SkillRule[numSkillsFound];
			for (int i=0;i<numSkillsFound;++i)
				finalRules[i] = matchedRules[i];
			return finalRules;
		}
		else
			return null;
	}
		
	
	public void addRule(SkillRule theNewRule) {
		SkillRule newRules[] = new SkillRule[rules.length+1];
		for (int i=0;i<rules.length;++i)
			newRules[i] = rules[i];
		newRules[rules.length] = theNewRule;
		rules = newRules;
	}

	public int numRules(){
		return rules.length;
	}
}
		
