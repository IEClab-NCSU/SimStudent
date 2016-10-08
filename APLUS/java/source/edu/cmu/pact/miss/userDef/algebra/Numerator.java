package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
public class Numerator extends EqFeaturePredicate 
{
	public Numerator() 
	{
	    setArity(1);
	    setName("numerator");
	    setReturnValueType(TYPE_ARITH_EXP);
	    setArgValueType(new int[]{TYPE_ARITH_EXP});

	}
	public String apply(Vector args) 
	{
	
		return numerator((String)args.get(0)); 
	}

}
