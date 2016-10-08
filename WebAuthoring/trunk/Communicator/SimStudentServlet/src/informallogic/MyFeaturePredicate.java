package informallogic;

import java.util.Arrays;

import edu.cmu.pact.miss.FeaturePredicate;

public abstract class MyFeaturePredicate extends FeaturePredicate {

	/* definition of the types */
	public static final int TYPE_WORD = 11;
	public static final int TYPE_PROPERTY = 14;
	public static final int TYPE_INVALID = 15;

	public MyFeaturePredicate(){
		System.out.println("MyFeaturePredicate constructor");
	}
	
	private static boolean isWord(String value) {
		return (value.matches("\\w+(-\\w+)*") | 
				Arrays.asList(Constants.PL_OPERATORS).contains(value)); //regex for matching alphanumeric words connected by hyphens
	}

	/*
	 * the type checker. This function is invoked by SimStudent to determine the
	 * type of a string value
	 */
	public static Integer valueTypeChecker(String value) {
		System.out.println("valueTypeChecker");
		int valueType = TYPE_INVALID;
	    if (Constants.INTERFACE_NAME.containsKey(value) |
					Constants.INTERFACE_NAME.values().contains(value))
				valueType = TYPE_PROPERTY;
	    else if (isWord(value)) {
			valueType = TYPE_WORD;
		}
		//System.out.println("Type: "+value+"  "+valueType);
		return new Integer(valueType);
	}
}
