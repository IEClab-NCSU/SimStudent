package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class ArithDiv extends EqFeaturePredicate {

    public ArithDiv() {
        setArity(2);
        setName("arith-div");
    }

    public String apply(Vector args) {
        
        String quotient = null;
        
        try {
            int divisor = Integer.parseInt((String)args.get(0));
            int divident = Integer.parseInt((String)args.get(1));
            quotient = "" + (divisor / divident);

        } catch (NumberFormatException e) {
            ;
        }

        return quotient;
    }

}
