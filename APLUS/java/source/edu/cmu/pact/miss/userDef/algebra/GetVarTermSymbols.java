package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class GetVarTermSymbols extends EqFeaturePredicate {

    public GetVarTermSymbols () {		
	setArity(1);
	setName("get-var-term-symbols");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    public String apply(Vector args) {		
	return getVarTermSymbols((String)args.get(0)); 
    }
}