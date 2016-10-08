package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class SkillRf extends EqFeaturePredicate {

    public SkillRf() {
        setArity(0);
        setName("skill-rf");
	setReturnValueType(TYPE_SIMPLE_SKILL);
	setArgValueType(new int[]{});

    }

    public String apply(Vector args) {
        return "rf";
    }

}
