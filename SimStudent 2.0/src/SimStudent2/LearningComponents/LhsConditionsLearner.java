/**
 * Created: Aug 21, 2014 8:29:34 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import weka.core.Instances;

/**
 * @author mazda
 *
 */
public abstract class LhsConditionsLearner {


	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Abstract methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	/**
	 * @param example
	 * @return
	 */
	public abstract LhsConditions initialLhsConditions(Example example);

	/**
	 * @param example
	 * @return
	 */
	public abstract LhsConditions refineLhsConditions(Example example, Instances j48Instances);

}
