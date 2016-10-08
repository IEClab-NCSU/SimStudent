package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class ButLastTerm extends EqFeaturePredicate {

    public ButLastTerm() 
    {			
	setArity(1);
	setName("but-last-term");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
	
    }
    public String apply(Vector args) 
    {	
	return butLastTerm((String)args.get(0)); 
    }
	
}
