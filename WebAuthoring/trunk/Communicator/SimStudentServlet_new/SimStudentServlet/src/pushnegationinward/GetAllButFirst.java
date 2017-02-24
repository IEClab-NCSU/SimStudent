package pushnegationinward;

import java.util.Vector;

public class GetAllButFirst extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public GetAllButFirst() {
		setArity(1);
		setName("get-all-but-first");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		String arg = args.get(0).toString();
        String result = getAllButFirst(arg);
		System.out.println("~~~~~get-all-but-first:"+result);
		return result;
	}

	private String getAllButFirst(String arg) {
		String value = arg.replaceAll(" ", "");
		if (arg.startsWith("~("))
		{
			
			return arg.substring(1,arg.length());
		}
		else
		{
			value = value.replaceAll("\\(", "");
			value = value.replaceAll("\\)", "");
			if (value.charAt(0) != '~')
				return value.substring(0,1);
			else if (value.charAt(1) != '~')
				return value.substring(0, 2);
			else if (value.charAt(2) != '~')
				return value.substring(0, 3);
			else
				return value.substring(0, 4);
		}
	}
}