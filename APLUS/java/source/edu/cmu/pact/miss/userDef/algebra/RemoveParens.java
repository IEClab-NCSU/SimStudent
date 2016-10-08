package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class RemoveParens extends EqFeaturePredicate {

	private static final long serialVersionUID = 1L;

	public RemoveParens() 
	{
	    setArity(1);
	    setName("remove-parens");
	    setReturnValueType(TYPE_ARITH_EXP);
	    setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	public String apply(Vector args) 
	{		
		return removeParens((String)args.get(0)); 
	}

}