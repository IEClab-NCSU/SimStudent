package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.LinkedList;
import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class IsFirstNumberPositive extends EqFeaturePredicate 
{
    public IsFirstNumberPositive() 
    {
		setArity(1);
		setName("is-first-number-positive");
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
		
		String number = findSignedNum(expString, 0);
		
		if(number == null)
		{
			return null;
		}
		
		if(number.charAt(0) != '-')
		{
			return "T";
		}
		
		return null;
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
                
        if(x - number.length()  > 0 && expString.charAt(x - number.length() - 1) == '-')
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
