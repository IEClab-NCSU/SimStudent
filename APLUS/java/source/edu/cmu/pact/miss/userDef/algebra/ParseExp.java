package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class ParseExp extends EqFeaturePredicate {

    public ParseExp() {
        setName("parse-exp");
        setArity(1);
    }

    public String apply(Vector args) {
        try {
            return AlgExp.parseExp((String)args.get(0)).toString();
        } catch (ExpParseException e) {
            System.out.println("ParseExp.apply(" + (String)args.get(0) + ")");
            e.printStackTrace();
            return null;
        }
    }
}
