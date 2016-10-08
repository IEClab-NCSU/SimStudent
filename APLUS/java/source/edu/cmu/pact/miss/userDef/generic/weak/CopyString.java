package edu.cmu.pact.miss.userDef.generic.weak;

import java.util.Vector;

import edu.cmu.pact.miss.FeaturePredicate;
import edu.cmu.pact.miss.userDef.stoichiometry.StoFeatPredicate;

public class CopyString extends FeaturePredicate{
	public CopyString(){
		setName("copy-string");
		//Simply copies the object passed in
		setArity(1);
		setReturnValueType(TYPE_OBJECT);
		setArgValueType(new int[]{TYPE_OBJECT});
	}

	public String apply(Vector args) {
		return (String)args.get(0);
	}
}
