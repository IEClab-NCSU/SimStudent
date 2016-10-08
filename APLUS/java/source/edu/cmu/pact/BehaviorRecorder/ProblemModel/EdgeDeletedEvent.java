/** 
 * Updated July 22nd 2009.
 * @author Collin Lynch
 * @copyright 2009 CTAT Project.
 */

package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;

/**
 * Inform listeners about a request to delete a link from the problem graph.
 * @author sewall, collinl
 *
 * This event will be fired when an edge is removed from the graph.  This 
 * will be signalled by the interface items and trapped by the 
 * ProblemModelListener.
 * Please note that this event (as of 10/19/09) triggers the ExampleTracerGraph
 * to go ahead and delete the entire subgraph (any links/nodes that would
 * be disconnected from the root by the removal of this edge)
 */
public class EdgeDeletedEvent 
    extends ProblemModelEvent
    implements EdgeEvent {

    /* --------------------------------------------
     * Internal storage.
     * ----------------------------------------  */

    /** The Edge event deals with a single edge. */
	
    /** The Edge event deals with a single edge. */
    private ProblemEdge Edge = null;
    private boolean isBeingRewired = false;
    public void setEdgeBeingRewired(boolean newValue){ isBeingRewired = newValue;}
    public boolean isEdgeBeingRewired(){return isBeingRewired;}
    /**
     * Generate a new atomic EdgeDeletedEvent signaling the 
     * Removal of this edge and this edge alone.
     *
     * @param edge: The Edge related to this event.
     */
    public EdgeDeletedEvent(ProblemEdge edge) {
	super(edge, "Single Edge Deleted", null, edge);
	this.Edge = edge;
    }

    /**
     * Generate a new atomic EdgeDeletedEvent signaling the 
     * Removal of this edge and this edge alone.
     *
     * @param edge: The Edge related to this event.
     * @param subevents:  Subevents of this event.
     */
    public EdgeDeletedEvent(ProblemEdge edge,
			    List<ProblemModelEvent> Subevents) {
    	super(edge, "Single Edge Deleted", null, edge, Subevents);
    	this.Edge = edge;
    }
    
    public String toString() {
    	StringBuffer sb = new StringBuffer(super.toString());
    	sb.append("; edge ").append(this.Edge);
    	if (this.Edge != null)
    	    sb.append(", edgeData ").append(this.Edge.getEdgeData());
    	return sb.toString();
    }
    
    /**
     * @return {@link #edge}
     */
    public ProblemEdge getEdge() {
        return this.Edge;
    }

}
