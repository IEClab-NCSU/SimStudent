package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsAVarTerm extends EqFeaturePredicate{

    public IsAVarTerm() 
    {
		setArity(1);
		setName("is-a-var-term");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
		
		// Inject the description here
		setFeatureDescription(
			"variable", // the concept's name
			"A variable is an element, feature, or factor that is liable to vary or change." // definition of the concept in case the tutor asks
		    );
		setFeatureDescription(getName(),"is an element, feature, or factor that is liable to vary or change.");
		
    }

    public String apply(Vector args) 
    {
    	return isAVarTerm((String)args.get(0)); 
    }

}
