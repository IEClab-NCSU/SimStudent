package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class BeforeSecondNumberAdd extends EqFeaturePredicate 
{
	/**
	 * Redundant operator comapared to After_Number__?
	 */
    public BeforeSecondNumberAdd() 
    {
		setArity(1);
		setName("before-second-number-add");
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
		
		if(getOperandBefore(expString, 1) == '+')
		{
			return "T";
		}
		
		return null;
    }
    
    protected char getOperandBefore(String expString, int t)
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
                
        if(x > 0 && x - number.length() - 1 >=0 && expString.charAt(x - number.length() - 1) == '-')
        {
        	x = x - number.length() - 1;
        }
        
        x--;
        
        boolean negativeFound = false;
        
        if(expString.charAt(x) == '-' && x >=0)
        {
        	x--;
        	negativeFound = true;
        }
		
		while(x >= 0)
		{
			if(isOperator(expString.charAt(x)))
			{
				return expString.charAt(x);
			}
			
			if((Character.isLetter(expString.charAt(x)) ||
				Character.isLetter(expString.charAt(x))) && negativeFound)
			{
				return '-';
			}
			
			x--;
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
