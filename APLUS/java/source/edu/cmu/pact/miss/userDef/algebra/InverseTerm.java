package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class InverseTerm extends EqFeaturePredicate 
{
    public InverseTerm() 
    {
	setArity(1);
	setName("inverse-term");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
	return inverseTerm((String)args.get(0)); 
    }
    
}
