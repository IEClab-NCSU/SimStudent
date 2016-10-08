package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class EvalArithmetic extends EqFeaturePredicate 
{
    public EvalArithmetic() 
    {
	setArity(1);
	setName("eval-arithmetic");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
//        System.out.println("EvalArithmetic(" + (String)args.get(0) + ")");
        String value = evalArithmetic((String)args.get(0));
//        System.out.println("EvalArithmetic returning: " + value);
	return value; 
    }
    
}
