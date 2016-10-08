package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class FirstNumberWithVar extends EqFeaturePredicate 
{
    public FirstNumberWithVar() 
    {
		setArity(1);
		setName("first-number-with-var");
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
	   
        String number = "";
        int x = 0;
        
        do{
        
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
        	while (x < expString.length() && (Character.isLetter(expString.charAt(x))))
        	{
        		number += expString.charAt(x);
        		x++;
        	}
        	
        }while(x < expString.length() && !Character.isLetter(number.charAt(number.length()-1)  ) );
		
		
		if(!number.equals("") && !Character.isDigit(number.charAt(number.length()-1)))
		{
			return number;
		}
		else
		{
			return null;
		}
    }
    

}
