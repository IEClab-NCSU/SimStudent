/**
 * Created: Dec 31, 2013 12:16:13 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8;

import SimStudent2.LearningComponents.WmePerceptionLearner;
import SimStudent2.LearningComponents.WmePerceptionSearchGoalTest;
import SimStudent2.LearningComponents.WmePerceptionSearchSuccessorFn;

/**
 * @author mazda
 *
 */
public class AlgebraV8WmePerceptionLearner extends WmePerceptionLearner {

	/**
	 * @param successorFunction
	 * @param goalTest
	 */
	public AlgebraV8WmePerceptionLearner(WmePerceptionSearchSuccessorFn successorFunction, WmePerceptionSearchGoalTest goalTest) {
		super(successorFunction, goalTest);
	}
}
