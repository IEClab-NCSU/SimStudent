package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

/**
 * @author mazda
 * 
 * For the ThreeStep Algebra I Tutor
 * 
 * Extract an operand from the third column.  E.g., "4x" from "add 4x"
 *
 */
public class GetOperand extends EqFeaturePredicate {

    public GetOperand() {
	setName("get-operand");
	setArity(1);
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_SKILL_OPERAND});
    }

    public String apply(Vector args) {

	String getOperand = null;
	
	String arg = (String)args.get(0);
	
	if (isArithmeticExpression(arg) || isExprList(arg)){
		return null;
	}
	
	int delimiterIndex = arg.indexOf(' ');
	if ( delimiterIndex > 0 ) {
	    // String action = arg.substring(0, delimiterIndex);
	    getOperand = arg.substring( delimiterIndex +1 );
	}

//	if(isArithmeticExpression(getOperand))
//		return null;

        
	return getOperand;
	
    }

}
