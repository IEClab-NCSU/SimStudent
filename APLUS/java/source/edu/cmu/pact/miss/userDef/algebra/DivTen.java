package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class DivTen extends EqFeaturePredicate 
{
	public DivTen() 
	{
	    setArity(1);
	    setName("div-ten");
	    setReturnValueType(TYPE_ARITH_EXP);
	    setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	public String apply(Vector args) 
	{
		return divTen((String)args.get(0)); 
	}
}
