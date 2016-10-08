/**
 * Created: Dec 19, 2013 6:56:19 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

/**
 * @author mazda
 *
 */
public class IsPolynomial extends AlgebraV8UserDefJessSymbol {

	public IsPolynomial() { 

		setArity(1);
		setName("is-polynomial");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		
		return isPolynomial( (String)args.get(0) );
	}
}
