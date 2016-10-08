package pushnegationinward;

import java.util.Vector;

public class DemorganizationAnd extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public DemorganizationAnd() {
		setArity(1);
		setName("demorganization-con");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg = args.get(0).toString();
		arg = arg.replaceAll("\"", "");
        String result = PLParserWrapper.getDemorganizationAnd(arg);
		System.out.println("~~~~~demorganization-and:"+result);
		return result;
	}
}