package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class ConcatenateX extends EqFeaturePredicate 
{
    public ConcatenateX() 
    {
		setArity(1);
		setName("concatenate-x");
		setReturnValueType(TYPE_EXP_LIST);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
    	String expString = (String)args.get(0); 
    	expString = expString +="x";
    	return expString;
    }
}
