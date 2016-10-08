/**
 * Interface for the domain dependent input matcher to recognize equivalent input values
 * 
 * Created: May 23, 2014 9:46:02 PM
 * 
 * @author mazda
 * (c) Noboru Matsuda 2014
 * 
 */
package SimStudent2.LearningComponents;

/**
 * @author mazda
 *
 */
public abstract class InputMatcher {

	/**
	 * Takes two String values and returns their equivalence
	 * 
	 * @param returnValue
	 * @param input
	 * @return
	 */
	public abstract boolean equivalent(String actualValue, String modelValue);

}
