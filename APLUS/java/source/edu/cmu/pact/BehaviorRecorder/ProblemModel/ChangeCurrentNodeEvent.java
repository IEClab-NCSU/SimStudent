/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

/**
 * An event to transmit the fact that the current node has changed from
 * a former node to a new one.  This extends the problem model event 
 * and does not implement the NodeEvent as the node itself is not 
 * altered in a meaningful sense.  
 */
public class ChangeCurrentNodeEvent extends ProblemModelEvent {
    
    /* -------------------------------------------------
     * Internal Storage.
     * ---------------------------------------------- */
    
    /** Prior problem node. */
    private final ProblemNode oldProblemNode;

    /** Current problem node. */
    private final ProblemNode newProblemNode;

    /** Not sure why this is here. */
    private static final long serialVersionUID = 1L;

    /* --------------------------------------------------
     * Constructors.
     * ----------------------------------------------- */

    /**
     * Construct a new ChangeCurrentNodeEvent to signal a shift
     * from one item to another.
     *
     * @param source: the {@link #BR_Controller} that originated this.
     * @param oldNode: the previously active {@link #ProblemNode}.
     * @param newNode: the currently active {@link #ProblemNode}.
     */
    public ChangeCurrentNodeEvent(BR_Controller source, 
				  ProblemNode oldNode, 
				  ProblemNode newNode) {
	super(source, "ChangeCurrentNode", oldNode, newNode);
	this.oldProblemNode = oldNode;
	this.newProblemNode = newNode;
    }
    
    /** Get the previous problem node. */
    public ProblemNode getOldProblemNode() {
	return oldProblemNode;
    }

    /** Get the current problem node. */
    public ProblemNode getNewProblemNode() {
	return newProblemNode;
    }
	
    
    
}
