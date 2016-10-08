package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class CanBeSimplifiedMonomial extends EqFeaturePredicate {

    public CanBeSimplifiedMonomial() {
	setName("can-be-simplified-term");
	setArity(1);
    }

    public String apply(Vector /* String */ args) {
	return canBeSimplifiedMonomial((String)args.get(0));
    }

}
