package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class ReverseSign extends EqFeaturePredicate 
{
    private static final long serialVersionUID = 1L;

    public ReverseSign() 
    {
	setArity(1);
	setName("reverse-sign");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
	return reverseSign((String)args.get(0)); 
    }
    
}
