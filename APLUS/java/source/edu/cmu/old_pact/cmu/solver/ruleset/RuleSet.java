package edu.cmu.old_pact.cmu.solver.ruleset;

import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.Equation;

//a RuleSet is just a list of rules. 

public class RuleSet {
	private Rule[] rules;
	
	public RuleSet(Rule[] theRules) {
		rules = new Rule[theRules.length];
		for (int i=0;i<theRules.length;++i)
			rules[i] = theRules[i];
	}
	
	public RuleSet(Rule[] theRules,int numRules) {
		rules = new Rule[numRules];
		for (int i=0;i<numRules;++i)
			rules[i] = theRules[i];
	}
	
	/**
	* Need to search through ALL the rules, so we're not depending on the rules order
	**/
	public RuleMatchInfo findRuleToFire(Equation info,String action,String input) {
		boolean foundrule = false;
		RuleMatchInfo ruleToFire = null;
		int len = rules.length;
		for (int i=0;i<len && foundrule == false;++i) {
			if (Rule.allRulesTraced())
				System.out.println("checking rule number "+i+": "+rules[i].getName());
			ruleToFire = rules[i].canFire(info,action,input);
			if (ruleToFire!=null && ruleToFire.getBoolean()==true) {
				foundrule = true;
				ruleToFire.setRule(rules[i]);
			}
			else {
				ruleToFire = null;
			}
		}
		return ruleToFire;
	}
	
	private static boolean inActions(String action,String[] actions) {
		if(action == null){
			return true;
		}
		boolean found=false;
		int len = actions.length;
		for (int i=0;i<len && !found;++i)
			if (actions[i].equalsIgnoreCase(action))
				return true;
		/*if the action is "nil", then it's always valid.  Right now
          this is used to allow us to have hints when the equation has
          been solved but there is no solver-specific done menu item.
          Writing the rules for that with the action as null rather
          than "nil" was allowing any of the three dones when it
          should've only allowed done: unique solution.*/
		return found || action.equalsIgnoreCase("nil");
	}

	//findRuleForHelp find the appropriate rule, ignoring the user's input and action
	//We check to see that the action specified in the rule is in ValidActions, since
	//we don't want to recommend an action that can't be taken
	//This allows us to change menus, and the help rules will adapt automatically
	public Rule findRuleForHelp(Equation info,String[] validActions) {
		Rule ruleToFire = null;
		int len = rules.length;
		for (int i=0;i<len;++i) {
			if (inActions(rules[i].getAction(),validActions) &&
				rules[i].testConditionsForHelp(info)){
				//System.out.println("in RuleSet findRuleForHelp canFire for "+rules[i].getName()+" i="+i);
				if(ruleToFire == null){
					ruleToFire = rules[i];
					if(ruleToFire.hasMessages()){
						//System.out.println("RS.fRFH: rule '" + ruleToFire + "' matches for help and has messages; returning");
						return ruleToFire;
					}
					/*else{
					  System.out.println("RS.fRFH: rule '" + ruleToFire + "' matches for help, but has no messages; keep looking");
					  }*/
				}
				else if(rules[i].hasMessages()){
					//System.out.println("RS.fRFH: rule '" + rules[i] + "' matches and has messages; returning it instead of '" + ruleToFire + "'.");
					return rules[i];
				}
			}
		}
		//System.out.println("RS.fRFH: rule '" + ruleToFire + "' matches; didn't find any with help, so returning it.");
		return ruleToFire;
	}
	
//Olga	
	//findAllowedRules find all rules for a given equation
	//about ValidActions see comments for findRuleForHelp
	public Rule[] findAllowedRules(Equation info,String[] validActions) {
		Vector rulesV = new Vector();
		int len = rules.length;
		for (int i=0;i<len;++i) {
			if (inActions(rules[i].getAction(),validActions) &&
				rules[i].testConditionsForHelp(info)){
				rulesV.addElement(rules[i]);
			}
		}
		Rule[] firedRules = new Rule[rulesV.size()];
		rulesV.copyInto(firedRules);
		return firedRules;
	}
// end Olga
	
	public Rule getRule(int num) {
		return rules[num];
	}
	
	public Rule getRuleByName(String name) {
		Rule foundRule=null;
		for (int i=0;i<rules.length&&foundRule==null;++i) {
			if (rules[i].getName().equalsIgnoreCase(name))
				foundRule = rules[i];
		}
		return foundRule;
	}
	
	public int numRules() {
		return rules.length;
	}
}
		
