package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class SkillClt extends EqFeaturePredicate {

    /**
     * This skill takes no argument.  It just returns a string "clt"
     */
    public SkillClt() {
        setArity(0);
        setName("skill-clt");
	setReturnValueType(TYPE_SIMPLE_SKILL);
	setArgValueType(new int[]{});

    }
    
    public String apply(Vector args) {
        return "clt";
    }
}
