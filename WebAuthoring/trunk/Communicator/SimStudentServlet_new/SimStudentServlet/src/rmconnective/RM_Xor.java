package rmconnective;

import java.util.Vector;

public class RM_Xor extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;

	public RM_Xor() {
		setArity(2);
		setName("rm-xor-connective");
		setReturnValueType(TYPE_NNF);
		setArgValueType(new int[] {TYPE_NNF,TYPE_NNF});
	}

	@Override
	public String apply(Vector args) {
	    String a1 = args.get(0).toString();
		String a2 = args.get(1).toString();
//		System.out.println("operator@rm-xor:   "+a1+"  "+a2+"   "+"(~"+a1+Constants.AND_CONNECTIVE+a2+")"+Constants.OR_CONNECTIVE+"("+a1+Constants.AND_CONNECTIVE+"~"+a2+")");
		return ("(~"+a1+" "+Constants.AND_CONNECTIVE+" "+a2+")"+" "+Constants.OR_CONNECTIVE+" "+"("+a1+" "+Constants.AND_CONNECTIVE+" "+"~"+a2+")");
	}
}

