package digt_1_3;

import java.util.Vector;

public class IsFalse extends MyFeaturePredicate {

	public IsFalse() {
		setArity(1);
		setName("is-false");
		setArgValueType(new int[] {TYPE_INPUT_NUMBER});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isFalse = arg.replaceAll(" ", "").equals("0");
		return (isFalse) ? "T": null;
	}

}
