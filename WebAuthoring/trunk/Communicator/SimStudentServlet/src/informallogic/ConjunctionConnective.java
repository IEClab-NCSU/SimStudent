package informallogic;

import java.util.Vector;

public class ConjunctionConnective extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public ConjunctionConnective() {
		setArity(0);
		setName("conjunction-connective");
		setReturnValueType(TYPE_WORD);
		setArgValueType(new int[] {    });
	}

	@Override
	public String apply(Vector args) {
		return (Constants.PL_AND);
	}
}
