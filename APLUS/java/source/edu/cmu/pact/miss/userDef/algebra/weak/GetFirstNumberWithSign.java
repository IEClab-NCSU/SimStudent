package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class GetFirstNumberWithSign extends EqFeaturePredicate 
{
    public GetFirstNumberWithSign() 
    {
		setArity(1);
		setName("first-number-with-sign");
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
		
		return findSignedNum(expString, 0);
    }
    
    protected String findSignedNum(String expString, int t)
    {
        String number = "";
        int x = 0;
     
        for(int i = 0; i <= t; i++)
        {
        	number = "";
        	
			while (x < expString.length() && (!Character.isDigit(expString.charAt(x))))
			{
				x++;
			}
			
			while (x < expString.length() && (Character.isDigit(expString.charAt(x))))
			{
				number += expString.charAt(x);
				x++;
			}
        }
                
        if(x - number.length() > 0 && expString.charAt(x - number.length() - 1) == '-')
        {
        	number = "-" + number;
        }
		
		if(!number.equals(""))
		{
			return number;
		}
		else
		{
			return null;
		}
    }
}
