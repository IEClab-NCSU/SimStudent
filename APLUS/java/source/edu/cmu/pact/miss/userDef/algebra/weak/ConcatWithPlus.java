package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class ConcatWithPlus extends EqFeaturePredicate 
{
    public ConcatWithPlus() 
    {
		setArity(2);
		setName("concat-with-plus");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply(Vector args)
    {
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
        //if (isVariable(expString2) == null || isConstant(expString1) == null){
        //    return null;
        //}
        
		if( expString2.charAt(0) != '-')
		{
			expString2 = "+"+expString2;
		}
		
     	String res = expString1+expString2;
     	
        return (res != null ? res : null);
    }
}
