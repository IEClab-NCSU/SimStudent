package edu.cmu.old_pact.cmu.solver.ruleset;


/*A TypeinSkillRule represents the ability to perform a given action
  (whereas a SkillRule represents the ability to choose which action
  to perform).  The implementation is basically the same, though: a
  skill rule is conditioned on one or more trace rules, and can also
  have its own conditions.  For normal skill rules, the trace rule(s)
  is a strategic rule; for typein skill rules, the trace rule(s) is a
  skill rule.
  
  This class is pretty bare for now; it behaves the same as a
  SkillRule, but has different semantics.  In the future, typein skill
  rules should be smart about what side they apply to.  For example,
  when subtracting 4 from 3x+4=5, typing 3x on the left should be
  represented by one typein skill, while typing 1 on the right should
  be represented by another (or maybe we only count the skill that
  takes "work" -- doing the subtraction on the right in the previous
  example).*/

public class TypeinSkillRule extends SkillRule{
	public TypeinSkillRule(String traceRuleName,String subskill,Test[] cond) {
		super(traceRuleName,subskill,cond);
	}
	
	public TypeinSkillRule(String traceRuleName,String subskill) {
		super(traceRuleName,subskill);
	}
	
	public TypeinSkillRule(String traceRuleName) {
		super(traceRuleName);
	}
	
	public TypeinSkillRule(String[] traceRuleNames,String subskill) {
		super(traceRuleNames,subskill);
	}
			
	public TypeinSkillRule(String[] traceRuleNames,String subskill,Test[] cond) {
		super(traceRuleNames,subskill,cond);
	}

	/*this creates a typein skill based on a normal skill; the name of
      the new typein rule is generated automatically by appending " -
      whole" to the name of the traced skill rule.  (Don't ask me why,
      but that seems to be something of a convention.)*/
	public static TypeinSkillRule makeTISkill(String traceRuleName){
		return new TypeinSkillRule(traceRuleName,traceRuleName + " - whole");
	}

	public String getName(){
		return "[Typein Skill: " + super.getName() + "]";
	}
}
