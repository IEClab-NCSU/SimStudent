package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;


public class IsPolynomial extends EqFeaturePredicate
{
	public IsPolynomial() 
	{
		setArity(1);
		setName("is-polynomial");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{
		return polynomial((String)args.get(0));
	}

}
