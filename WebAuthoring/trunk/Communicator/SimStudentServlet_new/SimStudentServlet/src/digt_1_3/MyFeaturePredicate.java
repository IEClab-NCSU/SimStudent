package digt_1_3;

import java.util.Arrays;

import edu.cmu.pact.miss.FeaturePredicate;

public abstract class MyFeaturePredicate extends FeaturePredicate {

	/* definition of the types */
	public static final int TYPE_INPUT_NUMBER = 11;
	public static final int TYPE_OUTPUT_NUMBER = 12;
	public static final int TYPE_FORMULA = 13;
	public static final int TYPE_PROPERTY = 14;
	public static final int TYPE_INVALID = 15;
	
	public MyFeaturePredicate() {
		System.out.println("in digt_1_3");
	}

	//this function is invoked by SimStudent to determine type of values 
	public static Integer valueTypeChecker(String value) {
		int valueType = TYPE_INVALID;
		if (isOutputNumber(value)) 
			valueType = TYPE_OUTPUT_NUMBER;
		else if (isInputNumber(value))
			valueType = TYPE_INPUT_NUMBER;
		else if (isFormula(value))
			valueType = TYPE_FORMULA;
		else if (Constants.INTERFACE_NAME.containsKey(value) |
				Constants.INTERFACE_NAME.values().contains(value))
			valueType = TYPE_PROPERTY;
		return new Integer(valueType);
	}
		
	// input is 0/1
	private static boolean isInputNumber(String line) {
		return (line.matches("[01]"));
	}
	
	/*
	 * first character should be 0/1 then we may have some spaces, then we should have 0/1 again
	 */
	private static boolean isOutputNumber(String line) {
		return (line.matches("[01]\\s*[01]"));
	}
	
	//operator type
	private static boolean isFormula(String line) {
        return Arrays.asList(Constants.PL_OPERATORS).contains(line);
	}
}
