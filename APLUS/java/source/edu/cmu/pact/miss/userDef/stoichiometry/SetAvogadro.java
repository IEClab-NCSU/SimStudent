package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SetAvogadro extends StoFeatPredicate {

	public SetAvogadro() {
		setName("avogadro");
		setArity(1);
	}

	public String apply(Vector args) {
		return avogadro((String)args.get(0));
	}

}
