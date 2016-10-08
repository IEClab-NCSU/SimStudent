package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;
public class Denominator extends EqFeaturePredicate 
{
	public Denominator() 
	{
	    setArity(1);
	    setName("denominator");
	    setReturnValueType(TYPE_ARITH_EXP);
	    setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	public String apply(Vector args) 
	{
		return denominator((String)args.get(0)); 
	}
	
	 public static void main(final String[] args) {
		 
		 Denominator d = new Denominator();
		 String denom = d.denominator("2/y*y");
		 System.out.println(denom);
	 }
	 
	 
}
