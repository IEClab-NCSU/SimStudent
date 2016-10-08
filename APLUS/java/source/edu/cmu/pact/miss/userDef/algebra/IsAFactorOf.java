package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsAFactorOf extends EqFeaturePredicate {

	public IsAFactorOf() 
	{
		setArity(2);
		setName("is-a-factor-of");
		setTestAsWME(true);
		argNames=new Vector();
		argNames.add("factor");
		argNames.add("exp");
		setDecomposedRelationship(true);
	}

	public String apply(Vector args) 
	{
		return isAFactorOf((String)args.get(0),(String)args.get(1));
	
	}

}
