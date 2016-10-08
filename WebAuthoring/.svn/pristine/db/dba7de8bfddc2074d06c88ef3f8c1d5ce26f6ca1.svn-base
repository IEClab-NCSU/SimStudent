package informallogic;

import java.util.Vector;

public class IsConjunctionConnective extends MyFeaturePredicate {

	public IsConjunctionConnective() {
		setArity(1);
		setName("is-conjunction-connective");
		setArgValueType(new int[] { TYPE_WORD });
	}

	public String apply(Vector args) {
		String value = ((String) args.get(0)).trim();
		boolean isConjunction = value.equals(Constants.INF_AND) |  value.equals(Constants.PL_AND);
		System.out.println("~~~~~~~~~~~IsConjunctionConnective: "+isConjunction+"  "+value);
		return (isConjunction ? "T" : null);
	}

}
