package pushnegationinward;

import java.util.Vector;

public class _IsNotSentence extends MyFeaturePredicate {

	public _IsNotSentence() {
		setArity(1);
		setName("is-not-sentence");
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	public String apply(Vector args) {
		String a1 = args.get(0).toString(); 
		boolean isNot = PLParserWrapper.isNotSentence(a1);
		System.out.println("~~~~~~~~~~~is-not:"+args+" "+(isNot));
		return (isNot) ? "T": null;
	}
}


