/**
 * Created: Dec 20, 2013 9:24:29 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;
import cl.utilities.sm.Expression;

/**
 * @author mazda
 *
 */
public class MultiplyTerm extends AlgebraV8UserDefJessSymbol {

	public MultiplyTerm() {

		setName("mul-term");
		setArity(2);
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
		
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
		/*
	    try {			
            AlgExp e1=AlgExp.parseExp(expString1);
            AlgExp e2=AlgExp.parseExp(expString2);

            if (e1.isPolynomial() && e2.isPolynomial()) return null;
            if (e1.isPolynomial() && e2.isSimpleTerm()) return null;
            if (e1.isSimpleTerm() && e2.isPolynomial()) return null;
	    }
	    catch(ExpParseException e) {
	    	e.printStackTrace();
	    }
	    */
		
		Expression exp1 = parse(expString1);
		Expression exp2 = parse(expString2);
		Expression multiplied = exp1.multiply(exp2).simplify();
		
		return typecheck(expString1, expString2, multiplied);
	}
}
