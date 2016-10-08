package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class IsATermOf extends EqFeaturePredicate 
{

	public IsATermOf()
	{
		setName("is-a-term-of");
		setArity(2);
		setTestAsWME(true);
		setDecomposedRelationship(true);
		argNames=new Vector();
		argNames.add("term");
		argNames.add("exp");
	}
	public String apply(Vector args) 
	{
	
		return isATermOf((String)args.get(0),(String)args.get(1));
	}

}
