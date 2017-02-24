package digt_1_3;

import java.util.Vector;

public class IsTrue extends MyFeaturePredicate {

	public IsTrue() {
		setArity(1);
		setName("is-true");
		setArgValueType(new int[] {TYPE_INPUT_NUMBER});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isTrue = arg.replaceAll(" ", "").equals("1");
		return (isTrue) ? "T": null;
	}

}
