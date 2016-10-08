package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class ListAddSymbols extends EqFeaturePredicate {
	public ListAddSymbols ()
	{		
			setName("list-add-symbols");
			setArity(1);
			setReturnValueType(TYPE_ARITH_EXP);
			setArgValueType(new int[]{TYPE_EXP_LIST});
	}
	public String apply(Vector args) 
	{		           
		String ret = listAddSymbols((String)args.get(0));
                return ret;
	}
}
