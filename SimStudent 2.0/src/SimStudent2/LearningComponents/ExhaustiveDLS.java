/**
 * Thu Apr 06 23:50:20 2006
 * 
 * Perform Depth Limited search while allowing backing up
 * 
 * 
 * @author mazda
 * (c) Noboru Matsuda 2006-2014 
 *  
 */

package SimStudent2.LearningComponents;

import java.util.List;

import SimStudent2.TraceLog;
import aima.search.framework.Node;
import aima.search.framework.Problem;
import aima.search.framework.SearchUtils;
import aima.search.uninformed.DepthLimitedSearch;
import aima.util.AbstractQueue;

public class ExhaustiveDLS extends DepthLimitedSearch {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	private int depthLimit = 0;
	public int getDepthLimit() { return depthLimit; }
	public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }

	boolean hasBeenInitialized = false;
	public boolean hasBeenInitialized() { return hasBeenInitialized; }
	public void setHasBeenInitialized(boolean hasBeenInitialized) {
		this.hasBeenInitialized = hasBeenInitialized;
	}

	private AbstractQueue queue = new AbstractQueue();
	public AbstractQueue getQueue(){ return this.queue;	}
	public void setQueue(AbstractQueue queue) { this.queue = queue; }

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
		setDepthLimit(limit);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public List<String> search(Problem p) throws Exception {

		clearInstrumentation();

		// If the search has never been involved before, 
		// then initialize the queue with the initial state of the given problem
		if (!hasBeenInitialized()) {

			queue.addToFront(new Node(p.getInitialState()));
			setHasBeenInitialized(true);
		}

		return exhaustiveDLS(p, getDepthLimit());
	}

	/**
	 * @param problem
	 * @param depthLimit
	 * @return an operator sequence (i.e., a list of "Action") that explains a given "problem"
	 */
	private List<String> exhaustiveDLS(Problem problem, int depthLimit) {

		List<String> result = null;

		while (!queue.isEmpty()) {
			
			Node node = popQueue();
			
			/*
			if (node.getState() instanceof WmePerceptionSearchState) {
				TraceLog.out("............................................");
				TraceLog.out("exhaustiveDLS(" + depthLimit + ") : node[" + node + "] " + ((WmePerceptionSearchState)node.getState()).getWmePerception());
			}
			*/
			
			/*
			if (node.getState() instanceof WmePerceptionSearchState) {
				TraceLog.out("calling the goal test for node[" + node + "]");
			}
			*/
			if (problem.isGoalState(node.getState())) {
				result = SearchUtils.actionsFromNodes(node.getPathFromRoot());
				break;
			}

			if (node.getDepth() != depthLimit) {
				List<Node> successorNodes = expandNode(node, problem);

				/*
				if (node.getState() instanceof WmePerceptionSearchState) {
					TraceLog.out("expandNode called for node[" + node + "] " + ((WmePerceptionSearchState)node.getState()).getWmePerception());
					TraceLog.out("exhaustiveDLS(" + depthLimit + ") - - - - - - - - - - - - - - -");
					for (Node aNode : successorNodes) {
						TraceLog.out("aNode: " + ((WmePerceptionSearchState)aNode.getState()).getWmePerception());
					}
					TraceLog.out("- - - - - - - - - - - - - - - - - - - - - -");
				}
				*/
				
				queue.addToFront(successorNodes);
			}
			
		}
		
		return result == null ? createCutOffResult() : result;
	}
	
	public String printQueue(AbstractQueue givenQueue) {
		
		String queueString="";
		
		@SuppressWarnings("unchecked")
		List<Node> nodes = givenQueue.asList();

		for (int i=0; i<nodes.size(); i++){
			Node node = (Node) nodes.get(i);
			queueString += SearchUtils.actionsFromNodes(node.getPathFromRoot());
		}
		return queueString;
	}

	/**
	 * the depth-limit is being used twice.
	 * @param queueToStartFrom
	 * @param lim
	 */
	/*
	 * This was from the old SimStudent code, but no longer needed...
	 * 
	public void setQueueFromWhichToBeginSearch(AbstractQueue queueToStartFrom, int lim) {
		
		setQueue(queueToStartFrom);
		setDepthLimit(lim);
	}
	*/

}
