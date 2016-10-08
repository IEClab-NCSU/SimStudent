/**
 * Created: Jun 21, 2014 9:01:17 PM
 * @author mazda
 * 
 */
package SimStudent2.ProductionSystem;

import java.util.ArrayList;

/**
 * @author mazda
 *
 */
public class Void extends UserDefJessSymbol {

	public Void() {

        setArity(1);
        setName(UserDefJessSymbol.VOID_OP_NAME);
        setArgValueType(new int[]{});
        setReturnValueType(TYPE_NULL);
    }

	
	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		return "";
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#valueType(java.lang.String)
	 */
	@Override
	public int valueType(String arg) {
		return 0;
	}

}
