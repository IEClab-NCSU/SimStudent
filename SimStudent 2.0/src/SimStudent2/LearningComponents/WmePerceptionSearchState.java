/**
 * Created: Jan 4, 2014 9:45:09 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;


/**
 * @author mazda
 *
 */
public class WmePerceptionSearchState {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
	// Fields 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private WmePerception wmePerception = null;


	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	/**
	 * @param wmePerception
	 */
	public WmePerceptionSearchState(WmePerception wmePerception) {
		setWmePerception(wmePerception);
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @return
	 */
	public boolean isGoalState(ArrayList<Example> positiveExamples) {
		
		// If proposed generalization of the currentWmePerception is consistent with positive examples, ...
		return getWmePerception().isConsistentWith(positiveExamples);
	}

	/**
	 * @return
	 */
	public boolean isRunningOutOfTime() {
		// ToDo WmePerceptionSearchState.isRunningOutOfTime()
		return false;
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getter & Setter
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public WmePerception getWmePerception() { return wmePerception; }
	private void setWmePerception(WmePerception wmePerception) { this.wmePerception = wmePerception; }	
	
}
