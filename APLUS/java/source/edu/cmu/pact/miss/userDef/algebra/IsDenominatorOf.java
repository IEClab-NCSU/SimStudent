package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsDenominatorOf extends EqFeaturePredicate {

	public IsDenominatorOf() {
		setArity(2);
		setName("is-denominator-of");
		setTestAsWME(true);
		setDecomposedRelationship(true);
		argNames=new Vector();
		argNames.add("denom");
		argNames.add("exp");
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{
		return isDenominatorOf((String)args.get(0),(String)args.get(1));
	}

}