package informallogic;

import java.util.Vector;

public class IsDisjunctionConnective extends MyFeaturePredicate {

	public IsDisjunctionConnective() {
		setArity(1);
		setName("is-disjunction-connective");
		setArgValueType(new int[] { TYPE_WORD });
	}

	public String apply(Vector args) {
		String value = ((String) args.get(0)).trim();
		boolean isDisjunction = value.equals(Constants.INF_OR) |  value.equals(Constants.PL_OR);
		System.out.println("~~~~~~~~~~~IsDisjunctionConnective: "+isDisjunction+" "+value);
		return ( isDisjunction? "T" : null);
	}

}
