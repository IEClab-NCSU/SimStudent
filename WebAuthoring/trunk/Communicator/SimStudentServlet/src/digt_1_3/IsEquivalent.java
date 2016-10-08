package digt_1_3;

import java.util.Vector;

public class IsEquivalent extends MyFeaturePredicate {

	public IsEquivalent() {
		setArity(2);
		setName("is-equivalent");
	}
	
	@Override
	public String inputMatcher( String exp1, String exp2 ){
		exp1 = exp1.replaceAll(" ",""); //remove any space in arg1
		exp2 = exp2.replaceAll(" ",""); //remove any space in arg2
		boolean isEquivalent = exp1.equals(exp2);
		//System.out.println("~~~~~~~~isEquivalent: "+isEquivalent+" exp1: "+exp1+"  exp2:"+exp2);
		return (isEquivalent ? "T" : null);
	}

	@Override
	public String apply(Vector arg0) {
		return null;
	}
}


