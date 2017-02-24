package pushnegationinward;

import java.util.Vector;

public class _Disjunction extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public _Disjunction() {
		setArity(2);
		setName("disjunction");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA,TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg0 = args.get(0).toString();
		String arg1 = args.get(1).toString();
        String result = PLParserWrapper.getDisjunction(arg0, arg1);
		System.out.println("~~~~~disjunction:"+result);
		return result;
	}
}