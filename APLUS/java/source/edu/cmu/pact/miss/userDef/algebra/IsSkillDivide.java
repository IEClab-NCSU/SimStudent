package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsSkillDivide extends EqFeaturePredicate {

    public IsSkillDivide() {
        setName("is-skill-divide");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }

    public String apply(Vector args) {
        
        String isDivide = null;
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (skill != null && skill.equals("divide")) {
            isDivide = "T";
        }
        return isDivide;
    }
}
