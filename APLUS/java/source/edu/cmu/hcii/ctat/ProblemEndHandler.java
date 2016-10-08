package edu.cmu.hcii.ctat;

/**
 * Pass an object that implements this interface to CTATHTTPHandler
 * to be notified at the completion of each problem.
 */
public interface ProblemEndHandler {

	/**
	 * Pass the problem summary from a just-completed problem to a user and get
	 * an indication of whether the Local TutorShop should exit.
	 * @param problemSummary summary from the problem just completed
	 * @return true means the caller can now exit, for its work is done; false otherwise 
	 */
	public boolean problemEnd(String problemSummary);
	
}
