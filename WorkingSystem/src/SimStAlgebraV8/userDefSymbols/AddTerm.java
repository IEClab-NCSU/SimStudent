/**
 * Created: Dec 20, 2013 9:22:34 PM
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
public class AddTerm extends AlgebraV8UserDefJessSymbol {

    public AddTerm() {

        setArity(2);
        setName("add-term");
        setReturnValueType(TYPE_ARITH_TERM);
        setArgValueType(new int[]{TYPE_ARITH_TERM, TYPE_ARITH_TERM});
        setIsCommutative(true);
    }
    
    /* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
		return addTerm(expString1, expString2);
    }

	public String addTerm_simple(String expString1, String expString2) {
		
		String result = null;
		
		char var1 = expString1.charAt(expString1.length()-1);
		char var2 = expString2.charAt(expString2.length()-1);
		
		if (var1 == var2) {
		
			String coef1Str = expString1.substring(0, expString1.length()-1);
			String coef2Str = expString1.substring(0, expString2.length()-1);
			
			int coef1 = (coef1Str.isEmpty() ? 1 : Integer.parseInt(coef1Str));
			int coef2 = (coef2Str.isEmpty() ? 1 : Integer.parseInt(coef2Str));
			
			int coef = coef1 + coef2;
			
			result = "" + coef + var1;
		}
		return result;
	}

	public String addTerm(String expString1, String expString2) {

		Expression exp1 = parse(expString1);
		Expression exp2 = parse(expString2);
		Expression added = exp1.add(exp2).simplify();
		
		return typecheck(expString1, expString2, added);
    }


}
