package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class HasParentheses extends EqFeaturePredicate{

	public HasParentheses(){
		setArity(1);
		setName("has-parentheses");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(Vector args) {
		return hasParentheses((String)args.get(0));
	}
	
}
