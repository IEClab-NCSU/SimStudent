package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class SkillCltOperand extends EqFeaturePredicate {

    public SkillCltOperand() {
    	setArity(1);
    	setName( "skill-clt-operand" );
    	setReturnValueType(TYPE_SKILL_OPERAND);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) {
    	
    	String input = null;

    	if (isArithmeticExpression ((String)args.get(0))) {
    		input = "combine like terms " + (String)args.get(0);
    	}
    	return input;
    }
}
