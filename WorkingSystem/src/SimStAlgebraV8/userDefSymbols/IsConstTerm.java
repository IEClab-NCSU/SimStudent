package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class IsConstTerm extends AlgebraV8UserDefJessSymbol {

	public IsConstTerm() {
		setName("is-constant");
		setArity(1);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	
	public String apply(ArrayList<String> args) {
		
		return isConstantTerm((String)args.get(0));
	}
	
}
