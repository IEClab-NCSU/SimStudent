/**
 * Created: Jul 19, 2014 7:40:21 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8;

import SimStudent2.LearningComponents.InputMatcher;

/**
 * @author mazda
 *
 */
public class AlgebraV8InputMatcher extends InputMatcher {

	/**
	 * actualValue must be non-null
	 * 
	 * @see SimStudent2.LearningComponents.InputMatcher#equivalent(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean equivalent(String actualValue, String modelValue) {
		
		boolean isEquivalent = false;
		
		if (actualValue != null) {
			
			isEquivalent = actualValue.equals(modelValue);
		}
		
		return isEquivalent;
	}

}
