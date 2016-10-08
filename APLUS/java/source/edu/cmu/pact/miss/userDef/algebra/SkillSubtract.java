package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class SkillSubtract extends EqFeaturePredicate {

    public SkillSubtract() {
	setArity( 1 );
	setName( "skill-subtract" );
	setReturnValueType(TYPE_SKILL_OPERAND);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) {
    	
        if (!isArithmeticExpression ((String)args.get(0)))
    		return null;

	return "subtract " + (String)args.get(0);
    }

}
