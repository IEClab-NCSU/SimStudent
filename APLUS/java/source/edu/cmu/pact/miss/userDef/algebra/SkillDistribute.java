package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class SkillDistribute extends EqFeaturePredicate{

	public SkillDistribute(){
    	setArity(1);
    	setName( "skill-distribute" );
    	setReturnValueType(TYPE_SKILL_OPERAND);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});

	}

	@Override
	public String apply(Vector args) {
		// TODO Auto-generated method stub
    	String input = null;

    	if (isArithmeticExpression ((String)args.get(0))) {
    		input = "distribute " + (String)args.get(0);
    	}
    	return input;
	}
}
