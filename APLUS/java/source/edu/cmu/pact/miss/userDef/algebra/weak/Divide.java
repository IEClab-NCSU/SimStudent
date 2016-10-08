package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class Divide extends EqFeaturePredicate 
{
    public Divide() 
    {
		setArity(2);
		setName("sum");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    /*
     * 	divides two integers (does not support division involving fractions)
     */
    public String apply(Vector args)
    {
    	if(args.size() != 2) return null;
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
        if (isConstant(expString1) == null || isConstant(expString2) == null){
            return null;
        }
        
        int numerator = Integer.parseInt(expString1);
        int denominator =(Integer.parseInt(expString2));
     	
        if(denominator == 0) return null;
        
        if((numerator / (double)denominator) % 1 != 0)
        	return numerator + "/" + denominator;
        
        String result = "";
        result += numerator / denominator;
        
        return result;
    }
}
