package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class UnitConv extends ReasonOperator {

	public UnitConv() {
		setName("unit-conv");
		//Takes no arguments, always returns "Unit Conversion"
		// All work is done in the feature predicates.
		setArity(0);
		setReturnValueType(TYPE_REASON);
		setArgValueType(new int[]{});
	}

	public String apply(Vector args) {
		return ReasonOperator.UNITCONV;
	}
}
