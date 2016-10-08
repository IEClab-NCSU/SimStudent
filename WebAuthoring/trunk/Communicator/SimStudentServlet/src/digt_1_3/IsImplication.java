package digt_1_3;

import java.util.Vector;

public class IsImplication extends MyFeaturePredicate {

	public IsImplication() {
		setArity(1);
		setName("is-implication");
		setArgValueType(new int[] {TYPE_FORMULA});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isImplication = isImplication(arg);
		//System.out.println("~~~~~~~~~~~is-implication:"+a1+" "+(isImplication));
		return (isImplication) ? "T": null;
	}
	
	private boolean isImplication(String value) {
		value = value.replaceAll("\"", "");
		return value.equals(Constants.PL_IMP);
	}
}
