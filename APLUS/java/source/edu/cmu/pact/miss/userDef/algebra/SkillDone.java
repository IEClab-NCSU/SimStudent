package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class SkillDone extends EqFeaturePredicate {

	
    public SkillDone() {
        setArity(2);
        setName("skill-done");
        setReturnValueType(TYPE_SIMPLE_SKILL);
        setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});

    }
    
    public String apply(Vector args) {
        return "done";
    }
}
