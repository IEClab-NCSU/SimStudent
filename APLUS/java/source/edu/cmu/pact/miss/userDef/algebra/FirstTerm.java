package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class FirstTerm extends EqFeaturePredicate {

    public FirstTerm() 
		{
	setArity(1);
	setName("first-term");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
	
		}
	public String apply(Vector args) 
	{
	
		
		return firstTerm((String)args.get(0)); 
	}
}
