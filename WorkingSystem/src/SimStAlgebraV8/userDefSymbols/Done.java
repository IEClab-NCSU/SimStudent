/**
 * Created: Mar 6, 2015 9:43:37 PM
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
public class Done extends AlgebraV8UserDefJessSymbol {

	/**
	 * 
	 */
	public Done() {
		setArity(0);
		setName("done");
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.ArrayList)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		return "done";
	}

}
