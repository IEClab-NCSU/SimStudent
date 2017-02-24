package informallogic;

import java.util.Arrays;
import java.util.Vector;

public class IsAtomicProposition extends MyFeaturePredicate {

	public IsAtomicProposition() {
		setArity(1);
		setName("is-atomic-proposition");
		setArgValueType(new int[] { TYPE_WORD });
	}

	public String apply(Vector args) {
		String value = ((String) args.get(0)).trim();
		boolean copy = (Arrays.asList(Constants.INF_OPERATORS).contains(value) == false) &
				        (Arrays.asList(Constants.PL_OPERATORS).contains(value) == false);
		System.out.println("~~~~~~~~~~~IsAtomicProposition: "+copy+"  "+value);
		return (copy) ? "T": null;
	}

}
