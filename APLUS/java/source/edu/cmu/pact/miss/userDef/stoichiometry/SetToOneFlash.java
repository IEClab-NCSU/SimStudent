package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SetToOneFlash extends StoFeatPredicate {

	public SetToOneFlash() {
		setName("set-to-one-flash");
		setArity(0);
		setReturnValueType(TYPE_ARITH_EXP);
	}

	public String apply(Vector args) {
		return oneFlash();
	}
	
}
