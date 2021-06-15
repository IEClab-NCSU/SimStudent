package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsConstant extends EqFeaturePredicate {

	public IsConstant() 
	{
		setName("is-constant");
		setArity(1);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
		setFeatureDescription(getName(),"is a constant");
	}

	public String apply(Vector args) 
	{
		
		return isConstant((String)args.get(0));
	}

}
