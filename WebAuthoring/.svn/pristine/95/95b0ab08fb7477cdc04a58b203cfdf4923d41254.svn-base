package pushnegationinward;

import java.util.Vector;

public class _IsConjunction extends MyFeaturePredicate {
	
	public _IsConjunction() {
		setArity(1);
		setName("is-conjunction");
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	public String apply(Vector args) {
		String arg = args.get(0).toString(); //we set arg 0 to operator
		boolean isConjunction = isConjunction(arg);
		System.out.println("~~~~~~~~~~~is-conjunction:"+arg+"  "+(isConjunction));
		return (isConjunction) ? "T": null;
	}
	
	private boolean isConjunction(String value) {
		return PLParserWrapper.isAndSentence(value);
	}

}
