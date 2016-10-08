package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.LinkedList;
import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class NumOpNum extends EqFeaturePredicate 
{
    public NumOpNum() 
    {
		setArity(1);
		setName("num-op-num");
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
    	
        if (!isArithmeticExpression(expString)) {
            return null;
        }
        
        try 
        {
            AlgExp exp = AlgExp.parseExp( expString );
            
            if(!exp.isSimple())
            {            	
            	for(int x = 0; x < expString.length(); x++)
            	{
            		if(Character.isDigit(expString.charAt(x)))
            		{
            			boolean operatorFound = false;
            			boolean term2Found = false;
                    	String number = "";
            			number += expString.charAt(x);
            			
            			while (isValidChar(expString, x+1))
            			{
            				x++;
            				number += expString.charAt(x);
            			}
            			
            			int i = x+1;
            			
            			if(isValidOperator(expString, i))
            			{
            				number += expString.charAt(i);
            				operatorFound = true;
            			}
            			
            			while (isValidChar(expString, i+1))
            			{
            				i++;
            				number += expString.charAt(i);
            				term2Found = true;
            			}
            			
            			if(operatorFound && term2Found)
            			{
            				return number;
            			}
            		}
            	}
            }
            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean isValidChar(String exp, int index)
    {
    	if(index >= exp.length())
    	{
    		return false;
    	}
    	
    	if(Character.isDigit(exp.charAt(index)))
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isValidOperator(String exp, int index)
    {
    	if(index >= exp.length())
    	{
    		return false;
    	}
    	
    	if(exp.charAt(index) == '+')
    	{
    		return true;
    	}
    	
    	if(exp.charAt(index) == '-')
    	{
    		return true;
    	}
    	
    	return false;
    }
 }
