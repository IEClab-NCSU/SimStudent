package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class ModTen extends EqFeaturePredicate 
{
	public ModTen() 
	{
	    setArity(1);
	    setName("mod-ten");

	    setReturnValueType(TYPE_ARITH_EXP);
	    setArgValueType(new int[]{TYPE_ARITH_EXP});

	}
	public String apply(Vector args) 
	{
	    return modTen((String)args.get(0)); 
	}
}
