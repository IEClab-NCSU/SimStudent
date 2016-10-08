package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class HasCoefficient extends EqFeaturePredicate 
{
    public HasCoefficient() 
    {
	setArity(1);
	setName("has-coefficient");
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
	return hasCoefficient((String)args.get(0)); 
    }
}
