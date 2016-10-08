/*
 * Created on May 8, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;


/**
 * The NodeDeletedEvent is an extentsion of the {@link NodeEvent NodeEvent}
 * class which signals deletion of a specific node from the model.
 */
public class NodeDeletedEvent 
    extends ProblemModelEvent 
    implements NodeEvent {

    
    /* --------------------------------------------
     * Internal storage.
     * ----------------------------------------  */

    /** The node this event deals with. */
    private ProblemNode Node = null;


    /* ------------------------------------------
     * Constructors
     * --------------------------------------- */
     
    /**
     * Construct the supplied NodeDeletedEvent as needed. 
     *
     * @param thisNode:  The node being deleted.
     */
    public NodeDeletedEvent(ProblemNode thisNode) {
        super(thisNode, "node deleted", null, thisNode);
        this.Node = thisNode;
    }


    /**
     * Construct the supplied NodeDeletedEvent as needed. 
     *
     * @param thisNode:  The node being deleted.
     */
    public NodeDeletedEvent(ProblemNode thisNode, 
			    List<ProblemModelEvent> Subevents) {
        super(thisNode, "node deleted", null, thisNode, Subevents);
        this.Node = thisNode;
    }


    /* ------------------------------------------------
     * Debugging output.
     * --------------------------------------------- */

    /**
     * Dump for debugging.
     *
     * @return super.toString() + {@link #deletedNode}
     */
    public String toString() {
	StringBuffer sb = new StringBuffer(super.toString());
	sb.append("; node ").append(this.Node);
	return sb.toString();
    }



    /* ------------------------------------------
     * Node Event Interface.
     * --------------------------------------- */

    /**
     * @return {@link #Node}
     */
    public ProblemNode getNode() {
        return this.Node;
    }    
}
