package rmconnective;

import java.util.Arrays;
import edu.cmu.pact.miss.FeaturePredicate;

public abstract class MyFeaturePredicate extends FeaturePredicate {

	/* definition of the types */
	public static final int TYPE_OPERATOR = 13;
	public static final int TYPE_NNF = 14;
	public static final int TYPE_INVALID = 15;
	public static final int TYPE_PROPERTY = 16;

	
	//this function is invoked by SimStudent to determine type of values 
	public static Integer valueTypeChecker(String value) {
		int valueType = TYPE_INVALID;
		if (Constants.INTERFACE_NAME.containsKey(value) |
				Constants.INTERFACE_NAME.values().contains(value))
			valueType = TYPE_PROPERTY;
		else if (isOperator(value))
			valueType = TYPE_OPERATOR;
		else if (isConnectiveFree(value))
			valueType = TYPE_NNF;
		System.out.println("--------   "+value+"  "+valueType);
		return new Integer(valueType);
	}
		

	private static boolean isConnectiveFree(String value) {
		boolean hasNoConnective = !(value.contains(Constants.IMP_CONNECTIVE) |
							    value.contains(Constants.BIIMP_CONNECTIVE) |
							    value.contains(Constants.XOR_CONNECTIVE) |
							    value.contains(Constants.NAND_CONNECTIVE ))
							    & (PLParserWrapper.parse(value) != null);
		return hasNoConnective;	
	}

	private static boolean isOperator(String line) {
		return (Arrays.asList(Constants.OPERATORS).contains(line));
	}
}
