package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.Utilities.trace;


/** Gustavo 16 Nov 2007
 * This operator is redundant. The function it calls is identical to the function for 
 * the operator VarName.
 */
public class RipCoefficient extends CLBased 
{
    public RipCoefficient() 
    {
	setArity(1);
	setName("rip-coefficent");//SOS: misspelling kept for consistency reasons with previous studies.
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
    	Expression exp = parse((String)args.get(0));
    	
    	while(!isRipCoefficientDone(exp)) {
    		previous = exp.toString();
    		exp = exp.exceptSimplifiedCoefficient();
    	
    		/*02/26/14 nbarba addition: this is why it crashed during the validation*/
    		if (exp==null)
    			return null;
    		
        	if(exp.toString().equals(previous))
        		return null;
    	}
    	
		return exp.toString();
		
		//return coefficient((String)args.get(0));
    }
    
    private String previous = null;
    
    public boolean isRipCoefficientDone(Expression exp) {  
    	if(exp == null)
    		return true;
    	
    	String expStr = exp.toString();
    	
    	for(int i = 0; i < expStr.length(); i++) {
    		if(Character.isDigit(expStr.charAt(i)))
    				return false;
    	}
    	
    	return true;
    }

}
