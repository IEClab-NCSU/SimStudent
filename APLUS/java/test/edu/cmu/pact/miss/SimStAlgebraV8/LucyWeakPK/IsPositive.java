package edu.cmu.pact.miss.SimStAlgebraV8.LucyWeakPK;

import java.util.Vector;

import javax.swing.JOptionPane;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsPositive extends EqFeaturePredicate 
{
    public IsPositive() 
    {
	setArity(1);
	setName("is-positive");
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
    	String value = (String)args.get(0);
    	if(!value.startsWith("-")) 
    		return "T";
    	return null;
    }
}
