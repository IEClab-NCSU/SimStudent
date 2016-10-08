package digt_1_3;

import java.util.Vector;

public class IsConjunction extends MyFeaturePredicate {
	
	public IsConjunction() {
		setArity(1);
		setName("is-conjunction");
		setArgValueType(new int[] {TYPE_FORMULA});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isConjunction = isConjunction(arg);
		//System.out.println("~~~~~~~~~~~is-conjunction:"+arg+"  "+(isConjunction));
		return (isConjunction) ? "T": null;
	}
	
	private boolean isConjunction(String value) {
		value = value.replaceAll("\"", "");
		return value.equals(Constants.PL_AND);
	}

}
