package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MolRatio extends StoFeatPredicate {

	public MolRatio() {
		setName("mol-ratio");
		setArity(3);
	}

	//Ensure that there is no order to FOA
	public String apply(Vector args) {
		return molarRatio((String)args.get(0), (String)args.get(1),
				(String)args.get(2));
	}
	
}
