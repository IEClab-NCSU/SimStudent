package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class CanBeSimplifiedPolynomial extends EqFeaturePredicate {

    public CanBeSimplifiedPolynomial() {
	setName("can-be-simplified-polynomial");
	setArity(1);
    }

    public String apply(Vector /* String */ args) {
	return canBeSimplifiedMonomial((String)args.get(0));
    }

}
