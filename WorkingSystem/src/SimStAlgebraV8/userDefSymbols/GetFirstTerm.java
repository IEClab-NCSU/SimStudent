/**
 * Created: Feb 28, 2015 9:19:15 PM
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
public class GetFirstTerm extends AlgebraV8UserDefJessSymbol {

	/**
	 * 
	 */
	public GetFirstTerm() {
    	setArity(1);
    	setName("get-first-term");
    	setReturnValueType(TYPE_ARITH_EXP);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.ArrayList)
	 */
	@Override
	public String apply(ArrayList<String> args) {

		String exp = (String)args.get(0);
		
		return getFirstTerm(exp);
	}

}
