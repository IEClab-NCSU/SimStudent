package rmconnective;

import java.util.Vector;

public class RM_DoubleNegation extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public RM_DoubleNegation() {
		setArity(1);
		setName("rm-double-negation");
		setReturnValueType(TYPE_NNF);
		setArgValueType(new int[] {TYPE_NNF});
	}

	@Override
	public String apply(Vector args) {
		String arg1 = (String) args.get(0);
        return arg1.replaceAll("~\\s*~", "");
	}
}
