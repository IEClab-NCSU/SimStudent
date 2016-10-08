package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class IsMonomial extends AlgebraV8UserDefJessSymbol {
    
	public IsMonomial() {
		setArity(1);
		setName("monomial");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	
	public String apply(ArrayList<String> args) {

		return isMonomial( (String)args.get(0) );
	}
}
