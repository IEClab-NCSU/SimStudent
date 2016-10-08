package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class ListAddTerm extends EqFeaturePredicate {

	public ListAddTerm() 
	{
		setName("list-add-term");
		setArity(1);
	}

	public String apply(Vector args) 
	{
		
		return listAddTerm((String)args.get(0));
	}

}
