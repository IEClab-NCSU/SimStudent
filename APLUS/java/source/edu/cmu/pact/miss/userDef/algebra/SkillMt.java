package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class SkillMt extends EqFeaturePredicate {

    public SkillMt() {
        setArity(0);
        setName("skill-mt");
	setReturnValueType(TYPE_SIMPLE_SKILL);
	setArgValueType(new int[]{});
    }

    public String apply(Vector args) {
        return "mt";
    }

}
