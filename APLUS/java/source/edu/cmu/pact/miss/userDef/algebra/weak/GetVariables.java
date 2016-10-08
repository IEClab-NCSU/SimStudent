package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.LinkedList;
import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class GetVariables extends EqFeaturePredicate 
{
    public GetVariables() 
    {
		setArity(1);
		setName("get-variables");
		setReturnValueType(TYPE_EXP_LIST);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args)
    {	
    	String expString = (String)args.get(0);
    	
        if (!isArithmeticExpression(expString)) {
            return null;
        }
        try 
        {
        	AlgExp exp = AlgExp.parseExp( expString );
        	
        	LinkedList<String> vars = new LinkedList<String>();
            int count = 0;
            	
            for(int x = 0; x < expString.length(); x++)
            {
            	String var = "";
            	
            	if(Character.isLetter(expString.charAt(x)))
            	{
            		var += expString.charAt(x);
            			
            		while (x < expString.length() - 1 && (Character.isLetter(expString.charAt(x+1))))
            		{
            			x++;
            			var += expString.charAt(x);
            		}
            			
           			vars.add(var);
           			count++;
            	}
            }
            
            if(vars.size() > 0)
            {
            	return makeList(vars.toArray(new String[count]));
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
}
