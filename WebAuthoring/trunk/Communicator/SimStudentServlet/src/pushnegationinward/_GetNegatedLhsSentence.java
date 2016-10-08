package pushnegationinward;

import java.util.Vector;

public class _GetNegatedLhsSentence extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public _GetNegatedLhsSentence() {
		setArity(1);
		setName("get-negated-lhs-sentence");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg = args.get(0).toString();
        String result = PLParserWrapper.getNegatedLhs(arg);
		System.out.println("~~~~~lhs-binary-sentence:"+result);
		return result;
	}
}