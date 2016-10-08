package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsSkillSubtract extends EqFeaturePredicate {

    public IsSkillSubtract() {
        setName("is-skill-subtract");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }

    public String apply(Vector args) {
        
        String isSubtract = null;
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (skill != null && skill.equals("subtract")) {
            isSubtract = "T";
        }
        return isSubtract;
    }
}
