package edu.cmu.pact.miss.userDef.fractionAddition;

import java.util.Vector;

import edu.cmu.pact.miss.FeaturePredicate;
import edu.cmu.pact.Utilities.trace;

public class IsEquivalent extends FeaturePredicate{
	
	 public IsEquivalent() {
        setArity(2);
        setName("is-equivalent");
    }

	   public String apply(Vector args) {
		   	return ((String)args.get(0)).equals((String)args.get(1))? "T" : null;		 
        }
    
}

