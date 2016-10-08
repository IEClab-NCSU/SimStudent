package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SolConc extends ReasonOperator {

	public SolConc() {
		setName("sol-conc");
		setArity(1);
	}

	public String apply(Vector args) {
		return supplyReason((String)args.get(0), ReasonOperator.SOLCONC);
	}

}
