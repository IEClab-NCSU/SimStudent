package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class AppendVarSymbol extends EqFeaturePredicate {

	public AppendVarSymbol()
	{
		setName("append-var-symbol");
		setArity(2);
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
	}
	
	
	public String apply(Vector args) 
	{
            String ret = appendVarSymbol((String)args.get(0),(String)args.get(1));
            return ret;
	}
}

