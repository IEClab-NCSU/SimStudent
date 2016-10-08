/**
 * Thu Apr 06 23:07:20 2006
 * 
 * Perform Iterative-Deepening Depth-first search while allowing backking-up
 * 
 * 
 * @author mazda
 * (c) Noboru Matsuda 2006-2014
 *  
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Problem;
import aima.search.uninformed.IterativeDeepeningSearch;

public class ExhaustiveIDS extends IterativeDeepeningSearch {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	// The maximum search depth for iterative deepening
	private int depthLimit = Integer.MAX_VALUE;
	
	// The depth of the search reached by the most recent search attempt
	private int lastDepth = 1;

	// The instance of depth-limited search agent. 
	// Starting from the depth limit of 1
    private ExhaustiveDLS lastExhaustiveDLS = new ExhaustiveDLS(1);

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Getter and Setter
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public ExhaustiveDLS getLastExhaustiveDLS() { return lastExhaustiveDLS; }
	public void setLastExhaustiveDLS(ExhaustiveDLS search) { this.lastExhaustiveDLS = search; }

	public int getLastDepth() { return lastDepth; }
    public void setLastDepth(int depth) { this.lastDepth = depth; }

	public int getDepthLimit() { return depthLimit; }
	public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }

	// public AbstractQueue getQueue(){ return lastExhaustiveDLS.getQueue(); }
        
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    /**
     * @param maxDepth
     */
    public ExhaustiveIDS(int maxDepth) {
    	setDepthLimit(maxDepth);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public List<String> search(Problem problem) throws Exception {

    	List<String> result = null;
    	int searchDepth = -1;
    	
    	// Start from resuming the previous search
    	ExhaustiveDLS exhaustiveDLS = getLastExhaustiveDLS();
    	
    	for (searchDepth = getLastDepth(); searchDepth <= getDepthLimit(); searchDepth++) {

    		// Resume from the last search, but make new a search for incremental depth limits
    		if (searchDepth != getLastDepth()) {
    			exhaustiveDLS = new ExhaustiveDLS(searchDepth);
    		}

    		result = exhaustiveDLS.search(problem);

    		int numNodes = getMetrics().getInt(NODES_EXPANDED);
    		numNodes += exhaustiveDLS.getMetrics().getInt(NODES_EXPANDED);
    		getMetrics().set(NODES_EXPANDED, numNodes);

    		// If depthLimit has been reached, then result indicates so
    		if (!cutOffResult(result)) {
    			break;
    		}
    	}

    	// Register the current search status...
    	setLastDepth(searchDepth);
    	setLastExhaustiveDLS(exhaustiveDLS);
    	
    	// If the depthLimit (for the Exhaustive IDS) has been reached, then return an empty array
    	// Otherwise, return whatever the Exhaustive DLS returns.
    	return cutOffResult(result) ? new ArrayList<String>() : result;
    }

    /*
     * This was in the old SimStudent code, but no longer needed...
     * 
    public void setQueueAndDepthFromWhichToBeginSearch(AbstractQueue queueToStartFrom, int depthToStartFrom) {
    	
    	setLastDepth(depthToStartFrom);
    	getLastExhaustiveDLS().setQueueFromWhichToBeginSearch(queueToStartFrom, depthToStartFrom);
    }
    */

}
