package digt_1_3;

import java.util.Vector;

public class TruthAssignment_10 extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public TruthAssignment_10() {
		setArity(0);
		setName("truth_assignment_10");
		setReturnValueType(TYPE_OUTPUT_NUMBER);
		setArgValueType(new int[] {});
	}

	@Override
	public String apply(Vector args) {
		return "1 0";
	}
}
