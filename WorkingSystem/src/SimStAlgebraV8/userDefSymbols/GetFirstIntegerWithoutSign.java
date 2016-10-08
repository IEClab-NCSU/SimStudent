/**
 * Created: Dec 19, 2013 9:50:37 PM
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
public class GetFirstIntegerWithoutSign extends AlgebraV8UserDefJessSymbol {

	public GetFirstIntegerWithoutSign() {
		
        setArity(1);
        setName("get-first-integer-without-sign");
        setArgValueType(new int[]{TYPE_ARITH_EXP});
        setReturnValueType(TYPE_ARITH_TERM);
    }
    
	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		
		return getFirstIntegerWithoutSign((String)args.get(0));
	}
}
