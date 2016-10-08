package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class Monomial extends EqFeaturePredicate 
{
    public Monomial() 
    {
	setArity(1);
	setName("monomial");
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
	return monomial((String)args.get(0)); 
    }
}
