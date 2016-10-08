/**
 * Created: Dec 27, 2013 9:23:32 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import weka.core.Instances;
import SimStudent2.TraceLog;


/**
 * @author mazda
 *
 */
public class LhsConditions {

	/* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** 
	 * Example of J48 tree output:
	 * 
	 * HomogeneousA_( > -1)&&HasCoefficientA_( <= -1)
	 * 
	 * 
	 * Example of LHS conditions in a Jess Production Rule: 
	 * 
	 * (or
     *   (and
     *      (test (is-lastconstterm-negative ?val0 ) )
     *      (test (not (has-var-term ?val0 )) )
     *   )
     *   (and
     *      (test (is-lastconstterm-negative ?val0 ) )
     *      (test (has-parentheses ?val0 ) )
     *   )
     * )
     *
	 * ***** ***** ***** ***** ***** ***** ***** ***** ***** *****/ 
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	Instances j48Instances = null;
	
	String j48tree = null;
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructors
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param j48Instances 
	 * @param j48tree
	 */
	public LhsConditions(Instances j48Instances, String j48tree) {
		
		// TraceLog.out("j48Instances: " + j48Instances);
		TraceLog.out("j48tree: " + j48tree);
		
		setJ48Instances(j48Instances);
		setJ48tree(j48tree);
	}
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @return
	 */
	public String toFormattedString() {
		
		String formattedString = null;
		
		if (getJ48tree().isEmpty()) {

			formattedString = ";; No feature conditions found yet...";
		
		} else {
		
			formattedString = "" + getJ48tree();
			
		}
		
		return formattedString;
	}
	
	public String toString() {
		
		String str = "LhsConditions[" + this.j48tree + "]";
		return str;
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters & Setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public Instances getJ48Instances() {
		return j48Instances;
	}


	private void setJ48Instances(Instances j48Instances) {
		this.j48Instances = j48Instances;
	}


	private String getJ48tree() {
		return j48tree;
	}


	private void setJ48tree(String j48tree) {
		this.j48tree = j48tree;
	}


}

