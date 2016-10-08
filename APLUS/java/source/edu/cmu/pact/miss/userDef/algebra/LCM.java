package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class LCM extends EqFeaturePredicate 
{
	public LCM()
	{
		setName("lcm");
		setArity(2);
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
		
	}
	public String apply(Vector args) 
	{
		return lcm((String)args.get(0),(String)args.get(1));
	}
}
