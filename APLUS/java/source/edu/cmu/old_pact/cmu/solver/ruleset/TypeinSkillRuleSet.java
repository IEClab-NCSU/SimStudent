package edu.cmu.old_pact.cmu.solver.ruleset;


/*This class is pretty bare at the moment.  This subclass is here to
  support future differences between TypeinSkillRules and normal
  SkillRules.  (See TypeinSkillRule.java for more details.)*/

public class TypeinSkillRuleSet extends SkillRuleSet{
	public TypeinSkillRuleSet(TypeinSkillRule[] theRules){
		super(theRules);
	}

	public TypeinSkillRuleSet(TypeinSkillRule[] theRules,int number){
		super(theRules,number);
	}
}
