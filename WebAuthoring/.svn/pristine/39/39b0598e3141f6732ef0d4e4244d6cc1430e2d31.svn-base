package pushnegationinward;

import java.util.Vector;

public class IsNegationOnDisjunction extends MyFeaturePredicate {

	public IsNegationOnDisjunction() {
		setArity(1);
		setName("is-negation-on-disjunction");
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isNegDisjunction = isNegOnDisjunction(arg);
		System.out.println("~~~~~~~~~~~is-negation-on-disjunction:"+args+" "+isNegDisjunction);
		return isNegDisjunction ? "T": null;
	}
	
	private boolean isNegOnDisjunction(String value) {
		value = value.replaceAll("\"", "");
		return value.startsWith("~(") & value.contains(Constants.OR);
	}
}
