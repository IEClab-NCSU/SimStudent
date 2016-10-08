package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class GetFirstConstant extends EqFeaturePredicate
{
    public GetFirstConstant() 
    {
		setArity(1);
		setName("first-constant");
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
		
		return findFirstConstant(expString);
    }
    
    protected String findFirstConstant(String expString)
    {
        String number = "";
        int x = 0;
     
       while(x < expString.length())
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
						
	        if(x >= expString.length() || !Character.isLetter(expString.charAt(x)))
	        {
	        	if(number.equals(""))
	        	{
	        		return null;
	        	}
	        	else
	        	{
	                if(x - number.length() > 0 && expString.charAt(x - number.length() - 1) == '-')
	                {
	                	number = "-" + number;
	                }
	                
	        		return number;
	        	}
	        }
        }

        return null;
    }
}
