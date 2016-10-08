package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class IsVarTerm extends AlgebraV8UserDefJessSymbol {

	public IsVarTerm() {
		setName("is-variable");
		setArity(1);
	}

	public String apply(ArrayList<String> args) {

		return isVarTerm((String)args.get(0));
	}

}
