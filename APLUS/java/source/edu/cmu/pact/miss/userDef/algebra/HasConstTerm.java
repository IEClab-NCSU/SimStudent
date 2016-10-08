package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class HasConstTerm extends EqFeaturePredicate 
{
    public HasConstTerm() 
    {
	setArity(1);
	setName("has-const-term");
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
	return hasConstTerm((String)args.get(0)); 
    }
}
