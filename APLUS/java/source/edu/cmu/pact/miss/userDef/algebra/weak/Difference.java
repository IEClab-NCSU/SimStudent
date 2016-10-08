package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class Difference extends EqFeaturePredicate 
{
    public Difference() 
    {
		setArity(1);
		setName("difference");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_EXP_LIST});
    }

    public String apply(Vector args)
    {	
    	int diff = 0;
    	String numlist = (String)args.get(0);
    	
        numlist = numlist.substring(1,numlist.length()-1);
        String[] nums = numlist.split(" ");
        diff = Integer.parseInt(nums[0]);
        
        try{
        	for(int i = 1; i<nums.length ;i++)
        	{
        		diff -= Integer.parseInt(nums[i]);
        	}
        }
        catch(NumberFormatException e)
        {
        	return null;
        }
        
        return ""+diff;
    }
    

}
