package pushnegationinward;

import java.util.Vector;

public class _GetLhsBinarySentence extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public _GetLhsBinarySentence() {
		setArity(1);
		setName("get-lhs-binary-sentence");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg = args.get(0).toString();
        String result = PLParserWrapper.getLhs(arg);
		System.out.println("~~~~~lhs-binary-sentence:"+result);
		return result;
	}
}