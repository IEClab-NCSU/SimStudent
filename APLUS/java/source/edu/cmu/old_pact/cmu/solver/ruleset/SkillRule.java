package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;

//a SkillRule is a rule that determines which skill is involved in a particular user action
//SkillRules always have one condition that tests the name of the tracing rule that fired
public class SkillRule {
	private Test[] conditions;
	private String name;           //name of the rule - needs to match the tracing rule that fired
	private String subskillName;  //subskill name (if rule matches, this is the skill that changes)
	protected boolean traceLocal=false;    //if true, trace this rule for debugging
	private String[] traceRules;
	private boolean multipleTraceRules=false;

	//The basic constructor takes a name for the rule, a name for the model-tracing rule (the subskill)
	//and a set of conditions
	public SkillRule(String traceRuleName,String subskill,Test[] cond) {
		subskillName = subskill;
		name = traceRuleName;
		conditions = new Test[cond.length];
		for (int i=0;i<cond.length;++i)
			conditions[i] = cond[i];
	}
	
	//If we give a rule name and a skill name, there are no conditions
	public SkillRule(String traceRuleName,String subskill) {
		subskillName = subskill;
		name = traceRuleName;
		conditions = new Test[0];
	}
	
	//If all we give is a name, then the skill name is the same as the rule name
	//(and there are no further conditions)
	public SkillRule(String traceRuleName) {
		subskillName = traceRuleName;
		name = traceRuleName;
		conditions = new Test[0];
	}
	
	//if we get an array of strings as the rule name, this skill applies if *any* of the trace rules match
	public SkillRule(String[] traceRuleNames,String subskill) {
		multipleTraceRules=true;
		traceRules = new String[traceRuleNames.length];
		for (int i=0;i<traceRuleNames.length;++i)
			traceRules[i] = traceRuleNames[i];
		subskillName = subskill;
		conditions=new Test[0];
	}
			
	public SkillRule(String[] traceRuleNames,String subskill,Test[] cond) {
		multipleTraceRules=true;
		traceRules = new String[traceRuleNames.length];
		for (int i=0;i<traceRuleNames.length;++i)
			traceRules[i] = traceRuleNames[i];
		subskillName = subskill;
		conditions = new Test[cond.length];
		for (int i=0;i<cond.length;++i)
			conditions[i] = cond[i];
	}

	public boolean canFire(String ruleName,Equation info) {
		boolean OK = true;
		if (!multipleTraceRules && !ruleName.equalsIgnoreCase(name))
			OK = false;
		else if (multipleTraceRules) {
			OK = false;
			for (int i=0;i<traceRules.length && !OK;++i) {
				if (ruleName.equalsIgnoreCase(traceRules[i]))
					OK=true;
				if (isTraced())
					System.out.println("checking skill rule *"+traceRules[i]+"* against *"+ruleName+"*: "+OK);
			}
		}
		if (isTraced() && !multipleTraceRules) {
			System.out.println("Strategic rule *"+ruleName+"* matches skill rule named*"+name+"*: "+OK);
		}
		if (conditions != null) {
			for (int i=0;i<conditions.length && OK;++i) {
				if (!(conditions[i].passes(info,true)))
					OK = false;
			}
		}
		if (isTraced()) {
			System.out.println("Skill rule "+getName()+" (skill is "+subskillName+") passes conditions: "+OK);
		}
		return OK;
	}
	
	public String getName() {
		if(name == null){
			StringBuffer sb = new StringBuffer(64);
			sb.append("{");
			for(int i=0;i<traceRules.length;i++){
				if(i != 0){
					sb.append("; ");
				}
				sb.append(traceRules[i]);
			}
			sb.append("}");
			return sb.toString();
		}
		else{
			return name;
		}
	}

	public String toString(){
		return getName();
	}

	//defineRuleSet is used to define a set of related skill rules
	//All need to be based on the same trace rule and differ from each other by one other condition
	public static SkillRule[] defineRuleSet(String baseName,String traceRule,NamedTestSet[] conditions) {
		SkillRule[] ruleSet = new SkillRule[conditions.length];
		for (int i=0;i<conditions.length;++i) {
			NamedTestSet thisSet = conditions[i];
			String ruleName = baseName+" "+thisSet.myName;
			ruleSet[i] = new SkillRule(ruleName,traceRule,thisSet.tests);
		}
		return ruleSet;
	}
	
	public void setTraceRule(boolean onOrOff) {
		traceLocal = onOrOff;
	}
	
	public boolean isTraced() {
		return (traceLocal || Rule.allRulesTraced());
	}
	
	//the subSkill name is the way the tutor describes the skill that has changed
	//From LMS's perspective, this is the subskill name
	public String getSubskillName() {
		return subskillName;
	}

		
}
