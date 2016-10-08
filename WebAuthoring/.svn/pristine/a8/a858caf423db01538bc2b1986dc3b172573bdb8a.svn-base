package pushnegationinward;

import java.util.Vector;

public class _NegateSentence extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;
	public _NegateSentence() {
		setArity(1);
		setName("negate-sentence");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg = (String)args.get(0);
		String result = PLParserWrapper.negate(arg);
		System.out.println("~~~~~negateSentence:"+result);
		return result;
	}
}