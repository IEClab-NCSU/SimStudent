package rmconnective;

import java.util.Vector;

public class IsImplicationConnective extends MyFeaturePredicate {

	public IsImplicationConnective() {
		setArity(1);
		setName("is-implication-connective");
		setArgValueType(new int[] { TYPE_OPERATOR });
	}

	public String apply(Vector args) {
		String a1 = (String) args.get(0);
		a1 = a1.replaceAll("\"", "");
		return (a1.equals(Constants.IMP_CONNECTIVE) ? "T" : null);
	}
}
