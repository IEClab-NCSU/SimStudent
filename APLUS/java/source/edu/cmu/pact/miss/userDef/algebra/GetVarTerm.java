package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class GetVarTerm extends EqFeaturePredicate 
{

    public GetVarTerm() 
    {
	setArity(1);
	setName("get-var-term");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
    	String value = firstVarTerm((String)args.get(0)); 
    	if(value == null && this.isAVarTerm((String)args.get(0)) != null &&
    			this.isAVarTerm((String)args.get(0)).equals("T"))
    		value = (String)args.get(0);
    	return value;
    			    	 
    }
}
