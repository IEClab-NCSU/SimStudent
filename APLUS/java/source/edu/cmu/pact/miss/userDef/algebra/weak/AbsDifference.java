package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class AbsDifference extends EqFeaturePredicate 
{
    public AbsDifference() 
    {
		setArity(2);
		setName("abs-difference");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
    	String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
        if (isConstant(expString1) == null || isConstant(expString2) == null){
            return null;
        }
        
     	Integer absDiff = null;
    	
     	absDiff = Math.abs((Integer.parseInt(expString1))-(Integer.parseInt(expString2)));
     	
        return (absDiff != null ? absDiff.toString() : null);
    }
 }
