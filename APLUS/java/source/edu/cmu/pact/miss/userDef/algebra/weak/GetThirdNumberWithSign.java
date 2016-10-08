package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class GetThirdNumberWithSign extends GetFirstNumberWithSign 
{
    public GetThirdNumberWithSign() 
    {
		setArity(1);
		setName("third-number-with-sign");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args)
    {    
    	String expString = (String)args.get(0); 
	
		if(expString == null)
		{
			return null;
		}
		
		return findSignedNum(expString, 2);
    }
    
}
