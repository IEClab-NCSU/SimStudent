package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class SkillMtOperand extends EqFeaturePredicate {

	public SkillMtOperand() {
		setArity(1);
    	setName( "skill-mt" );
    	setReturnValueType(TYPE_SKILL_OPERAND);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

    public String apply(Vector args) {
    	
    	String input = null;
    	if (isArithmeticExpression ((String)args.get(0))) {
    		input = "mt " + (String)args.get(0);
    	}
    	return input;
    }

}
