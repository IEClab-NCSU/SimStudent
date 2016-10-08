package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class GetConstTermSymbols extends EqFeaturePredicate {

	public GetConstTermSymbols ()
	{		
			setArity(1);
			setName("get-const-term-symbols");
			setReturnValueType(TYPE_ARITH_EXP);
			setArgValueType(new int[]{TYPE_ARITH_EXP});

	}
	public String apply(Vector args) 
	{		
		return getConstTermSymbols((String)args.get(0)); 
	}
}