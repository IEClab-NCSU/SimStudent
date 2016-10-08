package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class DivTermDecimal extends EqFeaturePredicate {

    public DivTermDecimal() {
        setName("div-term-decimal");
        setArity(2);
    }

    public String apply(Vector args) {
        return divTermDecimal((String)args.get(0), (String)args.get(1));
    }
}
