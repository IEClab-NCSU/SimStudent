package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SetToOne extends StoFeatPredicate {

	public SetToOne() {
		setName("set-to-one");
		setArity(2);
	}

	public String apply(Vector args) {
		return setNumberToOne((String)args.get(0), (String)args.get(1));
	}
	
}
