/**
 * Created: Dec 19, 2013 4:17:15 PM
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
public class HasCoefficient extends AlgebraV8UserDefJessSymbol {

    public HasCoefficient() { 
    	setArity(1);
    	setName("has-coefficient");
    	setArgValueType(new int[]{TYPE_ARITH_TERM});
    }
    
	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		return hasCoefficient((String)args.get(0)); 
	}

}
