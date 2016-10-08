package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class GetVariableSymbol extends EqFeaturePredicate {

public GetVariableSymbol() 
{			
	setArity(1);
	setName("get-variable-symbol");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
}

public String apply(Vector args) 
{	
       String ret = getVariableSymbol((String)args.get(0));
    return ret;
}

}