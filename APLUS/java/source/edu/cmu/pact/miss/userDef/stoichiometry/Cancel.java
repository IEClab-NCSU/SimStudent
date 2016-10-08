package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class Cancel extends StoFeatPredicate {

	public Cancel() {
		setName("cancel");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_SUBSTANCE});
		setReturnValueType(TYPE_CANCEL);
	}

	public String apply(Vector args) {
		return ": true";
	}

	
}

