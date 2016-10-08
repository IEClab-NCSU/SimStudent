package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class Sum extends EqFeaturePredicate 
{
    public Sum() 
    {
		setArity(1);
		setName("sum");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_EXP_LIST});
    }

    public String apply(Vector args)
    {	
    	int sum = 0;
    	String numlist = (String)args.get(0);
    	
        numlist = numlist.substring(1,numlist.length()-1);
        String[] nums = numlist.split(" ");
        
        try{
        	for(int i = 0; i<nums.length ;i++)
        	{
        		sum += Integer.parseInt(nums[i]);
        	}
        }
        catch(NumberFormatException e)
        {
        	return null;
        }
        
        return ""+sum;
    }
    

}
