package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/**
 * 
 * @author Rohan
 *
 */
public class AddParentheses extends EqFeaturePredicate{
	
	public AddParentheses(){
		setName( "add-parentheses" );
		setArity(1);
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[] {TYPE_ARITH_EXP});
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 */
	public String apply(Vector args){
		
		if(!isArithmeticExpression((String) args.get(0)))
			return null;
		
		/* If the exp has a parentheses don't add another one */
		if((((String)args.get(0)).startsWith("(")) && (((String) args.get(0)).endsWith(")")))
			return (String)args.get(0);
		
		if(trace.getDebugCode("miss"))trace.out("miss", "In AddParentheses apply: " + "("+args.get(0)+")");
		return "(" + args.get(0) + ")";
	}
}
