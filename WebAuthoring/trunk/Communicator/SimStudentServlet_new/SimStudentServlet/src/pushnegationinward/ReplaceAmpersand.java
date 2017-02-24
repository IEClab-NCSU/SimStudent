package pushnegationinward;

import java.util.Vector;

public class ReplaceAmpersand extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public ReplaceAmpersand() {
		setArity(1);
		setName("replace-ampersand");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] { TYPE_COMPLEX_FORMULA });
	}

	@Override
	public String apply(Vector args) {
		String arg0 = (String)args.get(0);
		return arg0.replaceAll("&", "&amp;");
	}
}
