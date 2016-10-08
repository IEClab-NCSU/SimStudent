package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class FirstNumberLetterChunk extends EqFeaturePredicate 
{
    public FirstNumberLetterChunk() 
    {
		setArity(1);
		setName("first-number-letter-chunk");
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
	   
        String result = "";
        int x = 0;

        	
        	while (x < expString.length() && (!Character.isDigit(expString.charAt(x))) && (!Character.isLetter(expString.charAt(x))) )
        	{
        		x++;
        	}
        	while (x < expString.length() && Character.isDigit(expString.charAt(x)))
        	{
        		result += expString.charAt(x);
        		x++;
        	}
        	while (x < expString.length() && Character.isLetter(expString.charAt(x)))
        	{
        		result += expString.charAt(x);
        		x++;
        	}       	
        	
    		if(!result.equals(""))
    		{
    			return result;
    		}
    		else
    		{
    			return null;
    		}	
        	
    }
    

}
