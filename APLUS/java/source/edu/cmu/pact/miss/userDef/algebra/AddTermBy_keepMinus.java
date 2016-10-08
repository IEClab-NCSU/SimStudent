package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class AddTermBy_keepMinus extends EqFeaturePredicate {
	
	private static final long serialVersionUID = 1L;

	public AddTermBy_keepMinus() 
	{	
			setArity(2);
			setName("add-term-by_keep-minus");
	}
	public String apply(Vector args) 
	{		
		return addTermBy_keepMinus((String)args.get(0),(String)args.get(1)); 
	}

}