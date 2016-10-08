package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MolWeightUnitNumerator extends StoFeatPredicate {

	public MolWeightUnitNumerator() {
		setName("mol-weight-unit-numerator");
		setArity(1);
		setArgValueType(new int[]{TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return "g";
	}

}
