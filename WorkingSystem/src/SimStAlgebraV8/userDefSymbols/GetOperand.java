/**
 * Created: Dec 20, 2013 6:36:28 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

/**
 * @author mazda
 *
 */
public class GetOperand extends AlgebraV8UserDefJessSymbol {

    public GetOperand() {
    	setName("get-operand");
    	setArity(1);
    	setReturnValueType(TYPE_ARITH_EXP);
    	setArgValueType(new int[]{TYPE_SKILL_OPERAND});
    }

    /* (non-Javadoc)
     * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
     */
    @Override
	public String apply(ArrayList<String> args) {

    	String getOperand = null;
    	
    	String arg = (String)args.get(0);
    	
    	/*
    	if (isArithmeticExpression(arg) || isExprList(arg)) {
    		return null;
    	}
    	*/
    	
    	int delimiterIndex = arg.indexOf(' ');
    	if ( delimiterIndex > 0 ) {
    		getOperand = arg.substring( delimiterIndex +1 );
    	}
    	
    	return getOperand;
	}

}
