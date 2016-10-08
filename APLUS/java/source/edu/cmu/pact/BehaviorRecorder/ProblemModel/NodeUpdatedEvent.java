/**
 * Updated July 22nd 2009
 * @author Collin Lynch
 * @copyright 2009 CTAT project.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;


/**
 * The NodeUpdatedEvent is a special NodeEvent class reflecting a 
 * change being made to a single node.  As a subset of the 
 * ProblemModelEvent it may have children below it.
 */
public class NodeUpdatedEvent 
    extends ProblemModelEvent 
    implements NodeEvent {

    /* -----------------------------------------------
     * Internal Storage.
     * -------------------------------------------- */

    /** The node this event deals with. */
    private ProblemNode Node = null;

    /** Not sure why this is here. */
    private static final long serialVersionUID = 1L;


    /* -----------------------------------------------
     * Constructors.
     * -------------------------------------------- */

    /**
     * Generate a new NodeUpdated Event signaling a change to the node.
     *
     * @param source:  The {@link #BR_Controller} that originated this.
     * @param problemNode:  The {@link #ProblemNode} that was updated.
     */
    public NodeUpdatedEvent(BR_Controller source, ProblemNode problemNode) {
	super(problemNode, "NodeUpdated", null, problemNode);
	this.Node = problemNode;
    }

    /**
     * Generate a new NodeUpdated Event signaling a change to the node
     *
     * @param source:      The {@link #BR_Controller} that originated this.
     * @param problemNode: The {@link #ProblemNode} that was updated.
     * @param Subevents:   Subevents of this event for use.
     */
    public NodeUpdatedEvent(BR_Controller source, 
			    ProblemNode problemNode,
			    List<ProblemModelEvent> Subevents) {
	super(problemNode, "NodeUpdated", null, problemNode, Subevents);
	this.Node = problemNode;
    }


    /* ------------------------------------------
     * NodeEvent Interface
     * --------------------------------------- */

    /**
     * @return {@link #Node}
     */
    public ProblemNode getNode() {
        return this.Node;
    }
}
