/**
 * Created: Dec 19, 2013 7:59:36 PM
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
public class IsLastConstTermNegative extends AlgebraV8UserDefJessSymbol {

	public IsLastConstTermNegative() {
		
		setArity(1);
		setName("is-lastconstterm-negative");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		return isLastConstTermNegative((String)args.get(0));
	}

}
