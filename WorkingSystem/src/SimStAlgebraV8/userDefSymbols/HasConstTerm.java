package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

public class HasConstTerm extends AlgebraV8UserDefJessSymbol {
	
    public HasConstTerm() {
    	setArity(1);
    	setName("has-const-term");
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(ArrayList<String> args) {
    	System.out.println("HasConstTerm.apply() must be defined.");
    	// return hasConstTerm((String)args.get(0));
    	return null;
    }
}
