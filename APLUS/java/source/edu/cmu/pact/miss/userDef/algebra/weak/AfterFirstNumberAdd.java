package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class AfterFirstNumberAdd extends EqFeaturePredicate 
{
    public AfterFirstNumberAdd() 
    {
		setArity(1);
		setName("after-first-number-add");
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
		
		if(getOperandAfter(expString, 0) == '+')
		{
			return "T";
		}
		
		return null;
    }
    
    protected char getOperandAfter(String expString, int t)
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
		
		while(x < expString.length())
		{
			if(isOperator(expString.charAt(x)))
			{
				return expString.charAt(x);
			}
			
			x++;
		}
		
		return ' ';
    }
    
    protected boolean isOperator(char c)
    {
    	if(c == '+'){return true;}
    	if(c == '-'){return true;}
    	if(c == '*'){return true;}
    	if(c == '/'){return true;}

    	return false;
    }
}
