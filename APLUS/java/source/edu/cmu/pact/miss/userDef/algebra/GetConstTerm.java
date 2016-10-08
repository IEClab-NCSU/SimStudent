package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class GetConstTerm extends EqFeaturePredicate 
{

    public GetConstTerm() 
    {
	setArity(1);
	setName("get-const-term");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
    	String value = lastConstTerm((String)args.get(0));
    	if(value == null && this.hasConstTerm((String)args.get(0)) != null &&
    			this.hasConstTerm((String)args.get(0)).equals("T"))
    		value = (String)args.get(0);
    	return value;

    	 
    }
}
