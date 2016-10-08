package pushnegationinward;

import java.util.Vector;

public class CanMoveNotInward extends MyFeaturePredicate {

	public CanMoveNotInward() {
		setArity(1);
		setName("can-move-not-inward");
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	public String apply(Vector args) {
		String a1 = args.get(0).toString(); 
		boolean result = canMoveNotInward(a1);
		System.out.println("~~~~~can move not: "+result);
		return (result) ? "T": null;
	}

	private boolean canMoveNotInward(String value) {
		value = value.replaceAll("\"", "");
		return value.startsWith("~(");
	}

}


