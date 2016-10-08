/**
 * Created: Mar 2, 2015 8:21:36 PM
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
public class GetAllButFirstChar extends AlgebraV8UserDefJessSymbol {

	/**
	 * 
	 */
	public GetAllButFirstChar() {
        setArity(1);
        setName("get-all-but-first-char");
        setArgValueType(new int[]{TYPE_ARITH_TERM});
        setReturnValueType(TYPE_ARITH_TERM);
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.ArrayList)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		return getAllButFirstChar(args.get(0));
	}


}
