package edu.cmu.hcii.ctat;

import java.util.List;

/**
 * Pass an object that implements this interface to CTATHTTPHandler
 * to be notified at the completion of the problem set
 */
public interface ProblemSetEndHandler {

	/**
	 * Pass the list of problem summaries from the entire problem set to a user and get
	 * an indication of whether the Local TutorShop should exit.
	 * @param problemSummaries all available summaries from the problem set just completed
	 * @return true means the caller can now exit, for its work is done; false otherwise 
	 */
	public boolean problemSetEnd(List<String> problemSummaries);
	
}
