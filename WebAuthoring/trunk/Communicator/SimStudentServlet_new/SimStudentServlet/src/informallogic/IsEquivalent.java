package informallogic;

import java.util.Vector;

public class IsEquivalent extends MyFeaturePredicate {

	public IsEquivalent() {
		setArity(2);
		setName("is-equivalent");
	}

	@Override
	public String inputMatcher( String exp1, String exp2 ){
		boolean isEquivalent = exp1.trim().equals(exp2.trim());
		System.out.println("~~~~~~~~isEquivalent: "+isEquivalent+" exp1: "+exp1+"  exp2:"+exp2);
		return (isEquivalent ? "T" : null);
   }

	@Override
	public String apply(Vector arg0) {
		return null;
	}
}