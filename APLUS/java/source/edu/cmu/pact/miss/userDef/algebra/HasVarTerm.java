package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class HasVarTerm extends EqFeaturePredicate 
{
    public HasVarTerm() 
    {
	setArity(1);
	setName("has-var-term");
	setArgValueType(new int[]{TYPE_ARITH_EXP});
	setFeatureDescription(getName(),"contain a variable term");
    }

    public String apply(Vector args) 
    {
	return hasVarTerm((String)args.get(0)); 
    }
}
