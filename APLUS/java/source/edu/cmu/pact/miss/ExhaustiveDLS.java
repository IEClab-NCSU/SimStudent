/**
 * Thu Apr 06 23:50:20 2006
 * 
 * @author mazda
 * 
 * Perform Depth Limited search while allowing backking-up
 *  
 */

package edu.cmu.pact.miss;

import java.util.List;

import aima.search.framework.Node;
import aima.search.framework.Problem;
import aima.search.framework.SearchUtils;
import aima.search.uninformed.DepthLimitedSearch;
import aima.util.AbstractQueue;

public class ExhaustiveDLS extends DepthLimitedSearch {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    private AbstractQueue queue = new AbstractQueue();
    
    public AbstractQueue getQueue(){
        return this.queue;
    }
    
    private Node popQueue() {
	if (queue.isEmpty()) {
	    return null;
	} else {
	    Node node = (Node)queue.getFirst();
	    queue.removeFirst();
	    return node;
	}
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public ExhaustiveDLS(int limit) {
    	super(limit);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    boolean hasBeenInitialized = false;
    
    public List search(Problem p) throws Exception {
	
	clearInstrumentation();

	// If the search has never been involved
	// before, then initialize the queue with the initial state of the 
	// given problem
	if (!hasBeenInitialized) { //queue.isEmpty()) {            
	    queue.addToFront(new Node(p.getInitialState()));
            hasBeenInitialized = true;
        }
	
	return exhaustiveDLS(p, limit);
    }
    
    private String printQueue(AbstractQueue givenQueue) {
        String s="";
        List nodes = givenQueue.asList();

        for (int i=0; i<nodes.size(); i++){
            Node node = (Node) nodes.get(i);
            s += SearchUtils.actionsFromNodes(node.getPathFromRoot());
        }
        return s;
    }

    /**
     * 14 Nov 2007
     * @param p
     * @param limit
     * @return the next operator-sequence that explains primaryInstruction
     */
    private List exhaustiveDLS(Problem p, int limit) {

    	List result = null; 
    	while (!queue.isEmpty()) {
    		Node node = popQueue();
    		if (node.getDepth() != limit) {
    			queue.addToFront(expandNode(node, p));
    		}
    		if (p.isGoalState(node.getState())) {
    			result = SearchUtils.actionsFromNodes(node.getPathFromRoot());
    			break;
    		}
    	}
    	//Gustavo 16 Nov 2007: changes made in order to deal with the case when the queue is empty.
    	//since null is the default, there is no need to create CUTOFF later.
    	//result can only be non-null if it was set when goal-test succeeded
    	
    	return (result == null ? createCutOffResult() : result);
    }

    /**
     * the depth-limit is being used twice.
     * @param queueToStartFrom
     * @param lim
     */
    public void setQueueFromWhichToBeginSearch(AbstractQueue queueToStartFrom, int lim) {
        this.queue = queueToStartFrom;
        this.limit = lim;
    }

}
