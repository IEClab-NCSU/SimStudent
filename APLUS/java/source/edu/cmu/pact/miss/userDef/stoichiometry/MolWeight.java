package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MolWeight extends ReasonOperator {

	public MolWeight() {
		setName("mol-weight");
		setArity(1);
		setReturnValueType(StoFeatPredicate.TYPE_REASON);
		setArgValueType(new int[] {TYPE_ARITH_EXP});
	}

	public String apply(Vector args) {
		return supplyReason((String)args.get(0), ReasonOperator.MOLWEIGHT);
	}
}
