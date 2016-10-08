package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MolWeightUnitDenominator extends StoFeatPredicate {

	public MolWeightUnitDenominator() {
		setName("mol-weight-unit-denominator");
		setArity(1);
		setArgValueType(new int[]{TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return "mol";
	}

}
