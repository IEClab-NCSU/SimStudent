package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsVariable extends EqFeaturePredicate {

	public IsVariable() 
	{
		setName("is-variable");
		setArity(1);
	}

	public String apply(Vector args) 
	{

		return isVariable((String)args.get(0));
	}

}
