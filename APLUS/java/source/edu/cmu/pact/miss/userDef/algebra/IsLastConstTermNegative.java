package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class IsLastConstTermNegative extends EqFeaturePredicate{

	public IsLastConstTermNegative() 
	{
		setArity(1);
		setName("is-lastconstterm-negative");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	@SuppressWarnings("unchecked")
	public String apply(Vector args) 
	{
		String temp = lastConstTerm((String)args.get(0));
		if(temp != null)
			return isNegative(temp);
		else 
			return null;
	}
	
	/*@Override
    public String getDescription() {
    	return "contain a negative constant term at the end";
    }*/
}
