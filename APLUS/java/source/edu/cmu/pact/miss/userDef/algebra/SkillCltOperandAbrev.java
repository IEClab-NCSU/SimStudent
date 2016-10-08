package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class SkillCltOperandAbrev extends EqFeaturePredicate {

    public SkillCltOperandAbrev() {
    	setArity(1);
    	setName( "skill-clt-operand-abrev" );
    	setReturnValueType(TYPE_SKILL_OPERAND);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) {
    	
    	String input = null;

    	if (isArithmeticExpression ((String)args.get(0))) {
    		input = "clt " + (String)args.get(0);
    	}
    	return input;
    }
}
