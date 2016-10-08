/**
 * Created: Dec 19, 2013 8:35:42 PM
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
public class IsHomogeneous extends AlgebraV8UserDefJessSymbol {

    public IsHomogeneous() { 
    	
    	setArity(1);
    	setName("is-homogeneous");
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		return homogeneous( (String)args.get(0) ); 
	}

}
