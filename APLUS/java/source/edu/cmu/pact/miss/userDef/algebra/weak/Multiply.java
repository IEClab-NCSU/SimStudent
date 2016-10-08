package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class Multiply extends EqFeaturePredicate 
{
    public Multiply() 
    {
		setArity(2);
		setName("multiply");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    /*
     * 	multiplies two integers (does not support multiplication of fractions)
     */
    public String apply(Vector args)
    {
    	if(args.size() != 2) return null;
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
        if (isConstant(expString1) == null || isConstant(expString2) == null){
            return null;
        }
        
        int term1 = Integer.parseInt(expString1);
        int term2 =(Integer.parseInt(expString2));
     	
        String result = "";
        result += term1 * term2;
        
        return result;
    }
}
