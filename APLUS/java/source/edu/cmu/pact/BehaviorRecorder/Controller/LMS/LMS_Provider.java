/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.Controller.LMS;

/**
 * Generic services provided by an Learner Management System.
 */
public interface LMS_Provider {

	/**
	 * Advance to the next problem in the curriculum.  Call this method
	 * when the student has completed a problem.  May exit if succeeds.
	 * @throw Exception with nested exception
	 */
	public void advanceProblem() throws Exception;
}
