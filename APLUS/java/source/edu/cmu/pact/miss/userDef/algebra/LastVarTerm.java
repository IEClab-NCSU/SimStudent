package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class LastVarTerm extends EqFeaturePredicate {

	private static final long serialVersionUID = 1L;

	public LastVarTerm() 
	{
		setArity(1);
		setName("last-var-term");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{
	
		
		return lastVarTerm((String)args.get(0)); 
	}

}
