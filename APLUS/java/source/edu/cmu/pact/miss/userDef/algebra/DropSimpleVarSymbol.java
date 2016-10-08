package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class DropSimpleVarSymbol extends EqFeaturePredicate {
    public DropSimpleVarSymbol ()
	{		
	setName("drop-simple-var-symbol");
	setArity(1);
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});	
	}

    public String apply(Vector args) {
	return dropSimpleVarSymbol((String)args.get(0)); 
    }
}
