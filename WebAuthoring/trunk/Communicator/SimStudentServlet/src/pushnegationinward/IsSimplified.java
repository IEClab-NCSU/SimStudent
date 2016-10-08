package pushnegationinward;

import java.util.Vector;

public class IsSimplified extends MyFeaturePredicate {

	public IsSimplified() {
		setArity(1);
		setName("is-simplified");
		setArgValueType(new int[] { TYPE_COMPLEX_FORMULA});
	}

	public String apply(Vector args) {		
		String arg1 = (String) args.get(0);
		arg1 = arg1.replaceAll("\"", "");
		boolean empty = arg1.trim().isEmpty() | arg1.equals("nil");
		boolean simplified = (isSimplified(arg1)) & (empty == false);
		System.out.println("~~~~~~~~~~~IsSimplified:"+arg1+"  "+simplified);
		return (simplified ? "T" : null);
	}

	private boolean isSimplified(String value) {
		return value.matches("^.*~\\s*~.*$") == false;
	}
}
