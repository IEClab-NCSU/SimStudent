package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsSkillAdd extends EqFeaturePredicate {

    public IsSkillAdd() {
        setName("is-skill-add");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }
    
    public String apply(Vector args) {
        
        String isAdd = null;
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (skill != null && skill.equals("add")) {
            isAdd = "T";
        }
        return isAdd;
    }
}
