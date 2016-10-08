package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class IsNumeratorOf extends EqFeaturePredicate {

    public IsNumeratorOf() {
	setArity(2);
	setName("is-numerator-of");
	setTestAsWME(true);
	setDecomposedRelationship(true);
	argNames=new Vector();
	argNames.add("num");
	argNames.add("exp");
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
	return isNumeratorOf((String)args.get(0),(String)args.get(1));
    }
}
