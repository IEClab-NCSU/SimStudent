package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

//Gustavo 22Jan2007
//unlike MolWeight (which provides a Reason), this operator returns
//the molecular weight of the substance given.

public class MolWeightValue extends StoFeatPredicate {

	public MolWeightValue() {
		setName("mol-weight-value");
		setArity(1);
		setArgValueType(new int[]{TYPE_SUBSTANCE});
		setReturnValueType(TYPE_ARITH_EXP);		
	}

	public String apply(Vector args) {
		return molWeightValue((String)args.get(0));
	}

}
