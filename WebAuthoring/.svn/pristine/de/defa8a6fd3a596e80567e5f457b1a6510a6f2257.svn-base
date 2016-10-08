package digt_1_3;

import java.util.Vector;

public class IsDisjunction extends MyFeaturePredicate {

	public IsDisjunction() {
		setArity(1);
		setName("is-disjunction");
		setArgValueType(new int[] {TYPE_FORMULA});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isDisjunction = isDisjunction(arg);
		//System.out.println("~~~~~~~~~~~is-disjunction:"+arg+" "+(isDisjunction));
		return (isDisjunction) ? "T": null;
	}

	private boolean isDisjunction(String value) {
		value = value.replaceAll("\"", "");
		return  value.equals(Constants.PL_OR);
	}
}
