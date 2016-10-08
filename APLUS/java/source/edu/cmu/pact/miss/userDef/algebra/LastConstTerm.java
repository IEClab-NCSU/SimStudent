package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class LastConstTerm extends EqFeaturePredicate 
{
	public LastConstTerm() 
	{
	    setArity(1);
	    setName("last-const-term");
	    
	    setReturnValueType(TYPE_ARITH_EXP);
	    setArgValueType(new int[]{TYPE_ARITH_EXP});

	}
	public String apply(Vector args) 
	{
	
		
		return lastConstTerm((String)args.get(0)); 
	}
}
