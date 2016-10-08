/*
 * Created on Apr 11, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;

/** 
 * NodeCreatedEvents are a specialized subset of the NodeEvent class
 * that signal the creation of a new node in the problem model.
 */ 
public class NodeCreatedEvent 
    extends ProblemModelEvent 
    implements NodeEvent{

    /* -----------------------------------------
     * Internal Storage. 
     * -------------------------------------- */

    /** (collinl) Not sure why this is here. */
    private static final long serialVersionUID = 1L;

    
    /** The node this event deals with. */
    private ProblemNode Node = null;


    
    /* ------------------------------------------
     * Constructors.
     * ----------------------------------------*/

    /**
     * Generate a new NodeCreatedEvent specifying a source 
     * as well as the node and a related arc.
     *
     * @param source:      The object initiating this event.
     * @param problemNode: The node associated with this event.
     */
    public NodeCreatedEvent(Object source, ProblemNode problemNode) {
	
        super(source, "", null, null);
        this.Node = problemNode;
    }

    /**
     * Generate a new NodeCreatedEvent specifying a source 
     * as well as the node and a related arc.
     * 
     * @param source:      The object initiating this event.
     * @param problemNode: The {@link #ProblemNode} associated with this event.
     * @param Subevents:   The Subevents associated with this.
     */
    public NodeCreatedEvent(Object source, 
			    ProblemNode problemNode,
			    List<ProblemModelEvent> Subevents) {
	
        super(source, "", null, null, Subevents);
        if(trace.getDebugCode("pm"))
        	trace.out("pm", "NodeCreatedEvent("+trace.nh(source)+", "+problemNode+", "+
        			(Subevents == null ? null : "nSubevents="+Subevents.size()));
        this.Node = problemNode;
    }


    
    /**
     * Generate a new NodeCreatedEvent that links to the 
     * {@link #BR_Controller}
     * that initiated the node event as well as the 
     * {@link #ProblemNode}
     * that was created.
     *
     * @param controller:  The BR_Controller that initiated the event.
     * @param problemNode: The newly minted node itself.
     */
    public NodeCreatedEvent(BR_Controller controller, ProblemNode problemNode) {
        super(controller, "", null, null);
	this.Node = problemNode;
    }

    /**
     * Generate a new NodeCreatedEvent that links to the 
     * {@link #BR_Controller}
     * that initiated the node event as well as the 
     * {@link #ProblemNode}
     * that was created.
     *
     * @param controller:  The BR_Controller that initiated the event.
     * @param problemNode: The newly minted node itself.
     * @param Subevents:   The Subevents associated with this.
     */
    public NodeCreatedEvent(BR_Controller controller, 
			    ProblemNode problemNode,
			    List<ProblemModelEvent> Subevents) {
        super(controller, "", null, null, Subevents);
	this.Node = problemNode;
    }

    /* --------------------------------------------
     * NodeEvent interface.
     * ----------------------------------------  */

    /**
     * @return {@link #Node}
     */
    public ProblemNode getNode() {
        return this.Node;
    }


}
