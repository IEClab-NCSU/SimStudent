package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SetUnitFlash extends StoFeatPredicate {

	public SetUnitFlash() {
		setName("set-unit-flash");
		setArity(1);
		setArgValueType(new int[]{TYPE_UNIT});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return setUnitFlash((String)args.get(0));
	}
}
