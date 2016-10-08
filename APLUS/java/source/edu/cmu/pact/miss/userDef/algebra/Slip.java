package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class Slip extends EqFeaturePredicate {

	    public Slip() {
	        setArity(0);
	        setName("slip");
	    }

	    public String apply(Vector args) {
	        return "slip";
	    }	
}