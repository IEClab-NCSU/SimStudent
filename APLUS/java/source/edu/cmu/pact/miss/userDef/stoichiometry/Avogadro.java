package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class Avogadro extends ReasonOperator {

	public Avogadro() {
		setName("avogadro");
		setArity(1);
	}

	public String apply(Vector args) {
		return supplyReason((String)args.get(0), ReasonOperator.AVOGADRO);
	}

}
