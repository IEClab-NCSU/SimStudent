package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.LinkedList;
import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class IsThirdNumberPositive extends IsFirstNumberPositive 
{
    public IsThirdNumberPositive() 
    {
		setArity(1);
		setName("is-third-number-positive");
		setReturnValueType(TYPE_EXP_LIST);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
    	String expString = (String)args.get(0); 
    	
		if(expString == null)
		{
			return null;
		}
		
		String number = findSignedNum(expString, 2);
		
		if(number == null)
		{
			return null;
		}
		
		if(number.charAt(0) == '-')
		{
			return "T";
		}
		
		return null;
    }
}