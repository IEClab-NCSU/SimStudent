package pushnegationinward;

import java.util.Arrays;

import edu.cmu.pact.miss.FeaturePredicate;

public abstract class MyFeaturePredicate extends FeaturePredicate {

	/* definition of the types */
	public static final int TYPE_COMPLEX_FORMULA = 14;
	public static final int TYPE_OPERATOR = 15;
	public static final int TYPE_NOT = 16;
	public static final int TYPE_INVALID = 17;
	public static final int TYPE_PROPERTY = 18;

	public MyFeaturePredicate(){
		System.out.println("MyFeaturePredicate constructor pushnegationinward");
	}

	//this function is invoked by SimStudent to determine type of values 
	public static Integer valueTypeChecker(String value) {
		int valueType = TYPE_INVALID;
		try{
		if (Constants.INTERFACE_NAME.containsKey(value) |
				Constants.INTERFACE_NAME.values().contains(value))
			valueType = TYPE_PROPERTY;
		else if (isValidOperator(value))
			valueType = TYPE_OPERATOR;
		else if (isFormula(value))
			valueType = TYPE_COMPLEX_FORMULA;
	    //System.out.println("---- "+value+"  "+valueType);
		}
		catch(Exception ex){
			System.out.println("in value type checker");
			ex.printStackTrace();
		}
		return new Integer(valueType);
	}


	private static boolean isValidOperator(String value) {
		return (Arrays.asList(Constants.VALID_OPERATORS).contains(value));
	}
	
	private static boolean isFormula(String value) {
		return (PLParserWrapper.parse(value) != null);
	}
}
