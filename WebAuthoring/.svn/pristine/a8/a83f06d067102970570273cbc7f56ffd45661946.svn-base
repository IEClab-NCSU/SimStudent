package rmconnective;

import java.util.Vector;

public class IsXorConnective extends MyFeaturePredicate {

	public IsXorConnective() {
		setArity(1);
		setName("is-xor-connective");
		setArgValueType(new int[] { TYPE_OPERATOR });
	}

	public String apply(Vector args) {
		String a1 = (String) args.get(0);
		a1 = a1.replaceAll("\"", "");
		return (a1.equals(Constants.XOR_CONNECTIVE) ? "T" : null);
	}
}
