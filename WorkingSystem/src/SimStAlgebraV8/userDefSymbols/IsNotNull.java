package SimStAlgebraV8.userDefSymbols;
import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class IsNotNull extends AlgebraV8UserDefJessSymbol {

	/**
     * Creates a new <code>NotNull</code> instance.
     *
     */
	public IsNotNull() {
		
		setName( "not-null" );
		setArity( 1 );
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	
	public String apply( ArrayList<String> args ) {
		return ((String)args.get(0)).equals("") ? FALSE_VALUE : TRUE_VALUE;
    }
}
