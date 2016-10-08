package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsSkillMultiply extends EqFeaturePredicate {

    public IsSkillMultiply() {
        setName("is-skill-multiply");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }

    public String apply(Vector args) {
        
        String isMultiply = null;
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (skill != null && skill.equals("multiply")) {
            isMultiply = "T";
        }
        return isMultiply;
    }
}
