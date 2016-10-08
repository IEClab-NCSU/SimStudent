package informallogic;

import java.util.Vector;

public class Copy extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public Copy() {
		setArity(1);
		setName("copy");
		setReturnValueType(TYPE_WORD);
		setArgValueType(new int[] { TYPE_WORD });
	}

	@Override
	public String apply(Vector args) {
		System.out.println("~~~~copy: "+(String) args.get(0));
		return (String) args.get(0);
	}
}
