package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class IsDenominatorOf extends AlgebraV8UserDefJessSymbol {

	public IsDenominatorOf() {
		setArity(2);
		setName("is-denominator-of");
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
	}

	public String apply(ArrayList<String> args) {
		System.out.println("IsDenominatorOf.apply() must be defined.");
		// return isDenominatorOf((String)args.get(0),(String)args.get(1));
		return null;
	}
	
}