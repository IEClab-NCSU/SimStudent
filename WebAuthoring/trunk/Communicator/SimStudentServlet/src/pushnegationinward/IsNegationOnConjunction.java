package pushnegationinward;

import java.util.Vector;

public class IsNegationOnConjunction extends MyFeaturePredicate {

	public IsNegationOnConjunction() {
		setArity(1);
		setName("is-negation-on-conjunction");
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isNegConjunction = isNegOnConjunction(arg);
		System.out.println("~~~~~~~~~~~is-negation-on-conjunction:"+args+" "+isNegConjunction);
		return isNegConjunction ? "T": null;
	}

	private boolean isNegOnConjunction(String value) {
		value = value.replaceAll("\"", "");
		return value.startsWith("~(") & value.contains(Constants.AND);
	}
}
