package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class LikeTerms extends EqFeaturePredicate {

	public LikeTerms() 
	{
		setName("like-terms");
		setArity(2);
	}

	public String apply(Vector args) 
	{
		return likeTerms((String)args.get(0),(String)args.get(1));
	}

}
