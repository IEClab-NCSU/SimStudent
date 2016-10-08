package pushnegationinward;

import java.util.Vector;

public class DemorganizationOr extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public DemorganizationOr() {
		setArity(1);
		setName("demorganization-dis");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg = args.get(0).toString();
		arg = arg.replaceAll("\"", "");
        String result = PLParserWrapper.getDemorganizationOr(arg);
		System.out.println("~~~~~demorganization-or:"+result);
		return result;
	}
	
}