package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class SubTerm extends EqFeaturePredicate {

    public SubTerm()
    {
        setArity(2);
        setName("sub-term");
        setReturnValueType(TYPE_ARITH_EXP);
        setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
        return subTerm((String)args.get(0),(String)args.get(1));

    }

}


