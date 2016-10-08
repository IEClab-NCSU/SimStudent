package pushnegationinward;

import java.util.Vector;

public class _GetNegatedRhsSentence extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public _GetNegatedRhsSentence() {
		setArity(1);
		setName("get-negated-rhs-sentence");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg = args.get(0).toString();
        String result = PLParserWrapper.getNegatedRhs(arg);
		System.out.println("~~~~~rhs-binary-sentence:"+result);
		return result;
	}
}