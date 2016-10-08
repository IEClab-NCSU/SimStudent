package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class IsNumeratorOf extends AlgebraV8UserDefJessSymbol {

    public IsNumeratorOf() {
    	setArity(2);
    	setName("is-numerator-of");
    	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }
    
    public String apply(ArrayList<String> args) {
    	System.out.println("IsNumeratorOf.apply() must be defined");
    	// return isNumeratorOf((String)args.get(0),(String)args.get(1));
    	return null;
    }
    
}
