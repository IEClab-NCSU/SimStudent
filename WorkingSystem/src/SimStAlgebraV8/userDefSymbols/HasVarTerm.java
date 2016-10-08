/**
 * Created: Dec 16, 2013
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
public class HasVarTerm extends AlgebraV8UserDefJessSymbol {

    public HasVarTerm() {
    	setArity(1);
    	setName("has-var-term");
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(ArrayList<String> args) {
    	return hasVarTerm((String)args.get(0)); 
    }
}
