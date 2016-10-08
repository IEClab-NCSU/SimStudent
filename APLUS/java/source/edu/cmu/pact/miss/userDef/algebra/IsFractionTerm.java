package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class IsFractionTerm extends EqFeaturePredicate 
{
	public IsFractionTerm() 
	{
		 
		
			setArity(1);
			setName("is-fraction-term");
			setArgValueType(new int[]{TYPE_ARITH_EXP});
			
		

		

	}
	public String apply(Vector args) 
	{
	
		
		return isFractionTerm((String)args.get(0)); 
	}
}
