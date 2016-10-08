/**
 * Created: Dec 27, 2013 9:26:31 PM
 * 
 * An RHS Operator sequence uses in a Production object
 *
 * This object contains enough information to compose the RHS operator sequence like this: 
 * 
 * (bind ?val2 (get-const-term ?val0))
 * (bind ?input (skill-subtract ?val2))
 * (modify ?var27 (value ?input))
 * ;; (predict-algebra-input ?selection UpdateTable ?input)
 * ;; (modify ?var27 (value ?*sInput*))
 * 
 * Basically, this requires a sequence of operators each with a variable binding.
 * 
 * 
 * 
 * @author mazda
 * (c) Noboru Matsuda 2013-2014
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.List;

import SimStudent2.TraceLog;
import SimStudent2.ProductionSystem.JessBindSequence;

/**
 * @author mazda
 * 
 */
public class RhsOperators {
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private List<String> jessBindClauses;
	
	private JessBindSequence jessBindSequence;

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param jessBindClauses
	 * @param jessBindSequence 
	 */
	public RhsOperators(List<String> jessBindClauses, JessBindSequence jessBindSequence) {
		setJessBindClauses(jessBindClauses);
		setJessBindSequence(jessBindSequence);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Method
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @return
	 */
	public String toStringForProduction() {
		
		JessBindSequence bindSequence = getJessBindSequence();
		String stringForProduction = bindSequence.toStringForProduction();
		
		return stringForProduction;
	}
	
	@Override
	public String toString() {
		
		// jessBindClauses = [(bind ?val130 (get-operand ?val99)), (bind ?val144 (sub-term ?val93 ?val130))]
		return "RhsOperators [jessBindClauses = " + jessBindClauses + "]";
	}

	/**
	 * @param example
	 * @return
	 */
	public boolean isConsistentWith(Example example, ArrayList<String> foaVarNames) {
		
		String modelInput = getJessBindSequence().apply(example, foaVarNames);
		String actualInput = example.getInput();
		
//		TraceLog.out("isConsistentWith(" + example + ", " + foaVarNames + ")");
//		TraceLog.out("modelInput: " + modelInput);
//		TraceLog.out("actualInput: " + actualInput);
		
		return (modelInput != null && modelInput.equals(actualInput));
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters and setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public List<String> getJessBindClauses() {
		return this.jessBindClauses;
	}

	private void setJessBindClauses(List<String> jessBindClauses) {
		this.jessBindClauses = jessBindClauses;
	}

	private JessBindSequence getJessBindSequence() {
		return this.jessBindSequence;
	}

	private void setJessBindSequence(JessBindSequence jessBindSequence) {
		this.jessBindSequence = jessBindSequence;
	}

}
