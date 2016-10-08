/**
 * Created: Dec 20, 2013 7:05:32 PM
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
public class GetConstTerm extends AlgebraV8UserDefJessSymbol {

    public GetConstTerm() { 

    	setArity(1);
    	setName("get-const-term");
    	setReturnValueType(TYPE_ARITH_EXP);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }


	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {

		String exp = (String)args.get(0);
		
		return getConstTerm(exp);
    }


}
