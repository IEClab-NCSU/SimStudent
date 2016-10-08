package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class VarName extends EqFeaturePredicate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public VarName() {
	setName("var-name");
	setArity(1);
    }

    public String apply(Vector /* String */ args) {
	return varName( (String)args.get(0) );
    }

}
