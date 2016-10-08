package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class LastTerm extends EqFeaturePredicate
{

    private static final long serialVersionUID = 1L;
    public LastTerm() 
    {
	setArity(1);
	setName("last-term");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
	return lastTerm((String)args.get(0)); 
    }

}
