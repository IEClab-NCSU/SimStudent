package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class SkillRds extends EqFeaturePredicate {

    public SkillRds() {
        setArity(0);
        setName("skill-rds");
        setReturnValueType(TYPE_SIMPLE_SKILL);
        setArgValueType(new int[]{});
    }
    
    public String apply(Vector args) {
        return "rds";
    }
}
