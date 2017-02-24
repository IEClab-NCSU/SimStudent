package pushnegationinward;

import java.util.Vector;

public class _IsNegated extends MyFeaturePredicate {

	public _IsNegated() {
		setArity(1);
		setName("is-not-operator");
		setArgValueType(new int[] {TYPE_OPERATOR});
	}

	public String apply(Vector args) {
		String a = args.get(0).toString(); 
//		System.out.println("isLiteral~~~~~~~~~~~"+a1+"  "+a1.matches("[~]*[A-Za-z]"));
		return (a.equals(Constants.NOT)) ? "T": null;
	}
}


