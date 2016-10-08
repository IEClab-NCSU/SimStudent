package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class FirstVarTerm extends EqFeaturePredicate 
{

    public FirstVarTerm() 
    {
	setArity(1);
	setName("first-var-term");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
	return firstVarTerm((String)args.get(0)); 
    }
}
