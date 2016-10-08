/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat;

/**
 * Calls used by the rest of the application on an LMS.
 * @author sewall
 */
public interface CtatLMSClient {

	/**
	 * Tell whether the student is logged in to the LMS.
	 * @return true if logged in
	 */
	public boolean isStudentLoggedIn();
	
	/**
	 * Log the student out.
	 */
    public void logout();
    
    /**
     * Advance to the next problem in the LMS.
     */
    public void advanceProblem();
}
