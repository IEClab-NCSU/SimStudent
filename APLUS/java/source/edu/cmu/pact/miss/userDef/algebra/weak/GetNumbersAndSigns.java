package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.LinkedList;
import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class GetNumbersAndSigns extends EqFeaturePredicate 
{
    public GetNumbersAndSigns() 
    {
		setArity(1);
		setName("get-number-and-signs");
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
    	
        if (!isArithmeticExpression(expString)) {
            return null;
        }
        try 
        {
        	AlgExp exp = AlgExp.parseExp( expString );
        	
        	LinkedList<String> numbers = new LinkedList<String>();
            int count = 0;
            	
            for(int x = 0; x < expString.length(); x++)
            {
	            String number = "";
	            
	            while(x < expString.length() - 1 && !isValidChar(expString.charAt(x)))
	            {
	            	x++;
	            }
	            
	            if(isValidChar(expString.charAt(x)))
	            {
	            	number += expString.charAt(x);
	            }
	            
	            while (x < expString.length() - 1 && Character.isDigit(expString.charAt(x+1)))
	            {
	            	x++;
	            	number += expString.charAt(x);
	            }
	            
	            if(!number.equals("") && !number.equals("-"))
	            {
		           	numbers.add(number);
		          	count++;
	            }
            }
            
            if(numbers.size() > 0)
            {
            	return makeList(numbers.toArray(new String[count]));
            }
            else
            {
            	return null;
            }
        }
        catch(ExpParseException e)
        {  
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean isValidChar(Character c)
    {
    	if(Character.isDigit(c))
    	{
    		return true;
    	}
    	
    	if(c == '-')
    	{
    		return true;
    	}
    	
    	return false;
    }
}
