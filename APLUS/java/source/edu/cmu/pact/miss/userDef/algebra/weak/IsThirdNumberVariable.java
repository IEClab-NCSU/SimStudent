package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.LinkedList;
import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class IsThirdNumberVariable extends IsFirstNumberVariable 
{
    public IsThirdNumberVariable() 
    {
		setArity(1);
		setName("is-third-number-variable");
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
		
		boolean isVariable = isNumVariable(expString, 2); 
		
		if(isVariable)
		{
			return "T";
		}
		
		return null;
    }
    
    protected boolean isNumVariable(String expString, int t)
    {
        int x = 0;
     
        for(int i = 0; i <= t; i++)
        {        	
			while (x < expString.length() && (!Character.isDigit(expString.charAt(x))))
			{
				x++;
			}
			
			while (x < expString.length() && (Character.isDigit(expString.charAt(x))))
			{
				x++;
			}
        }
        		
		if(Character.isLetter(expString.charAt(x)))
		{
			return true;
		}
        
        return false;
    }
}