package pushnegationinward;

import java.util.Vector;

public class RM_DoubleNegation extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public RM_DoubleNegation() {
		setArity(1);
		setName("rm-double-negation");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg1 = (String) args.get(0);
		String result = removeDoubleNegation(arg1);
		System.out.println("~~~~~~~~~~~rm-neg:"+arg1+" "+result);
        return result;
	}

	private String removeDoubleNegation(String arg1) {
		return arg1.replaceAll("~\\s*~", "");
	}
}
