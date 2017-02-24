package rmconnective;

import java.util.Vector;

public class IsSimplified extends MyFeaturePredicate {

	public IsSimplified() {
		setArity(1);
		setName("is-simplified");
		setArgValueType(new int[] { TYPE_NNF });
	}

	public String apply(Vector args) {		
		String arg1 = (String) args.get(0);
		arg1 = arg1.replaceAll("\"", "");
		boolean empty = arg1.trim().isEmpty() | arg1.equals("nil");
		boolean simplified = arg1.matches("^.*~\\s*~.*$") == false & empty == false;
		System.out.println("~~~~~~~~~~~IsSimplified:"+arg1+"  "+simplified);
		return (simplified ? "T" : null);
	}
}
