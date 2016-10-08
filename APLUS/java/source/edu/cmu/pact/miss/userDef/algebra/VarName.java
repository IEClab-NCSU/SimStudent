package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class VarName extends EqFeaturePredicate 
{
	public VarName() 
	{
	    setArity(1);
	    setName("var-name");
	    setReturnValueType(TYPE_ARITH_EXP);
	    setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	public String apply(Vector args) 
	{
	    return varName((String)args.get(0)); 
	}
}
