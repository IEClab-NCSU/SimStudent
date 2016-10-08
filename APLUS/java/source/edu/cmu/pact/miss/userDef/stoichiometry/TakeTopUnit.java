package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class TakeTopUnit extends StoFeatPredicate {

	public TakeTopUnit() {
		setName("take-top-unit");
		setArity(3);
	}

	public String apply(Vector args) {
		return takeTopUnit((String)args.get(0), (String)args.get(1),
				(String)args.get(2));
	}

}
