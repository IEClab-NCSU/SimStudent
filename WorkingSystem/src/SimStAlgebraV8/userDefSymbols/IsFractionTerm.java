package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class IsFractionTerm extends AlgebraV8UserDefJessSymbol {
	
	public IsFractionTerm() {
		setArity(1);
		setName("is-fraction-term");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}
	
	public String apply(ArrayList<String> args) {
		
		System.out.println("IsFractionTerm.apply() must be defined");
		// return isFractionTerm((String)args.get(0));
		return null;
	}
	
}
