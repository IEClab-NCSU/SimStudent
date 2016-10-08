package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class CompSto extends ReasonOperator {

	public CompSto() {
		setName("comp-sto");
		setArity(2);
	}

	public String apply(Vector args) {
		return supplyReason((String)args.get(0), ReasonOperator.COMPSTO);
	}

}
