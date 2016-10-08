package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class HasParentheses extends AlgebraV8UserDefJessSymbol {

	public HasParentheses(){
		setArity(1);
		setName("has-parentheses");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(ArrayList<String> args) {
		System.out.println("HasParentheses.apply() must be defined.");
		// return hasParentheses((String)args.get(0));
		return null;
	}
	
}
