package rmconnective;

import java.util.Vector;

public class IsBiimplicationConnective extends MyFeaturePredicate {

	public IsBiimplicationConnective() {
		setArity(1);
		setName("is-biimplication-connective");
		setArgValueType(new int[] { TYPE_OPERATOR });
	}

	public String apply(Vector args) {
		String a1 = (String) args.get(0);
		a1 = a1.replaceAll("\"", "");
		return (a1.equals(Constants.BIIMP_CONNECTIVE) ? "T" : null);
	}
}
