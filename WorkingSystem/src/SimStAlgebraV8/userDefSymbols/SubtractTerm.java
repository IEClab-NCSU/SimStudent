/**
 * Created: Dec 20, 2013 9:21:11 PM
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
public class SubtractTerm extends AlgebraV8UserDefJessSymbol {

    public SubtractTerm() {

        setArity(2);
        setName("sub-term");
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
		
		Expression exp1 = parse(expString1);
		Expression exp2 = parse(expString2);
		Expression subtracted = exp1.subtract(exp2).simplify();
		
		return typecheck(expString1, expString2, subtracted);
	}
}
