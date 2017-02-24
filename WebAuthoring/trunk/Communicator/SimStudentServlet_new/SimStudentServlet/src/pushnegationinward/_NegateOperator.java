package pushnegationinward;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class _NegateOperator extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;
	Map<String,String> ops;
	public _NegateOperator() {
		setArity(1);
		setName("negate-operator");
		setReturnValueType(TYPE_OPERATOR);
		setArgValueType(new int[] {TYPE_OPERATOR});
		ops = new HashMap<String,String>();
		ops.put(Constants.AND, Constants.OR);
		ops.put(Constants.OR, Constants.AND);
	}

	@Override
	public String apply(Vector args) {
		String arg = (String)args.get(0);
		System.out.println("~~~~~negateOperator:"+ops.get(arg));
		return ops.get(arg).toString();
	}
}