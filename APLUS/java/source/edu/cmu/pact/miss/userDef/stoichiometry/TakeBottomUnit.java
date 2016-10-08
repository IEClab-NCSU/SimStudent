package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class TakeBottomUnit extends StoFeatPredicate {

	public TakeBottomUnit() {
		setName("take-bot-unit");
		setArity(3);
	}

	public String apply(Vector args) {
		return takeBottomUnit((String)args.get(0), (String)args.get(1),
				(String)args.get(2));
	}

}
