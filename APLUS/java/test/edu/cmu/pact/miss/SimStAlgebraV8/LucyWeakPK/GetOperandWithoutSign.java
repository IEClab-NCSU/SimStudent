package edu.cmu.pact.miss.SimStAlgebraV8.LucyWeakPK;

import java.util.Vector;

import javax.swing.JOptionPane;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/**
 * @author mazda
 * 
 * For the ThreeStep Algebra I Tutor
 * 
 * Extract an operand from the third column.  E.g., "4x" from "add 4x", or "3x" from "add -3x"
 *
 */
public class GetOperandWithoutSign extends EqFeaturePredicate {

    public GetOperandWithoutSign() {
	setName("get-operand-without-sign");
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

        
//        trace.out("gusmiss", "GetOperand returning: " + getOperand);
        
	if(getOperand.startsWith("-"))
	{
		getOperand = getOperand.substring(1);
	}
	
	return getOperand;
	
    }

}
