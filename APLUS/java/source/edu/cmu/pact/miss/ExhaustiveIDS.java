/**
 * Thu Apr 06 23:07:20 2006
 * 
 * @author mazda
 * 
 * Perform Iterative-Deepening Depth-first search while allowing 
 * backking-up
 *  
 */
package edu.cmu.pact.miss;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Problem;
import aima.search.uninformed.IterativeDeepeningSearch;
import aima.util.AbstractQueue;

public class ExhaustiveIDS extends IterativeDeepeningSearch {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    private int lastDepth = 1;

    public int getLastDepth(){
        return lastDepth;
    }

    private ExhaustiveDLS searchSchema = new ExhaustiveDLS(1);

    public AbstractQueue getQueue(){
        return searchSchema.getQueue();
    }

    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    /**
     * 
     */
    public ExhaustiveIDS() {
    	this(Integer.MAX_VALUE);
    }

    /**
     * @param maxDepth
     */
    public ExhaustiveIDS(int maxDepth) {
    	super(maxDepth);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public List search(Problem p) throws Exception {

    	List result = null;

    	int searchDepth;
    	for (searchDepth = lastDepth; searchDepth <= limit; searchDepth++) {

    		if (searchDepth != lastDepth) {
    			searchSchema = new ExhaustiveDLS(searchDepth);
    		}

    		result = searchSchema.search(p);

    		int numNodes = getMetrics().getInt(NODES_EXPANDED);
    		numNodes += searchSchema.getMetrics().getInt(NODES_EXPANDED);
    		getMetrics().set(NODES_EXPANDED, numNodes);

    		// The result returned can only be a cutOff result if 'limit' has been reached
    		if (!cutOffResult(result)) {
    			break;
    		}
    	}
    	lastDepth = searchDepth;
    	return !cutOffResult(result) ? result : new ArrayList();
    }

    public void setQueueAndDepthFromWhichToBeginSearch(AbstractQueue queueToStartFrom, int depthToStartFrom) {
    	this.lastDepth = depthToStartFrom;
    	searchSchema.setQueueFromWhichToBeginSearch(queueToStartFrom, depthToStartFrom);
    }
}
