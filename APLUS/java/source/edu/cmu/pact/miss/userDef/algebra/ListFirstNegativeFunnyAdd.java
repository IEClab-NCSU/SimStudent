package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class ListFirstNegativeFunnyAdd extends EqFeaturePredicate {

	public ListFirstNegativeFunnyAdd()
	{
		setArity(1);
		setName("list-first-negative-funny-add");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_EXP_LIST});
		
	}
	public String apply(Vector args) 
	{
		return listFirstNegativeFunnyAdd((String)args.get(0));
	}

}
