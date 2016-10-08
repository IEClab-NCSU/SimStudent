package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;

public class EdgeRewiredEvent extends ProblemModelEvent implements EdgeEvent {

    private ProblemEdge newEdge = null;
    EdgeDeletedEvent edgeDeletedEvent = null;
    EdgeCreatedEvent edgeCreatedEvent = null;
    /* ------------------------------------------------
     * Constructors 
     * --------------------------------------------- */

    /**
     * Generate a new EdgeRewiredEvent suitable for throwing.
     *
     * @param source:  The source of the event itself.
     * @param edge:    The edge this event relates to.
     */
    public EdgeRewiredEvent(Object source, EdgeDeletedEvent edgeDeletedEvent, EdgeCreatedEvent edgeCreatedEvent) {
        super(source, "", null, null);
        this.edgeDeletedEvent = edgeDeletedEvent;
        this.newEdge = edgeCreatedEvent.getEdge();
        this.edgeCreatedEvent = edgeCreatedEvent;
    }
    
    public EdgeRewiredEvent(Object source, EdgeDeletedEvent edgeDeletedEvent, EdgeCreatedEvent edgeCreatedEvent,
		    List<ProblemModelEvent> Subevents) {
    	super(source, "", null, null, Subevents);
    	this.edgeDeletedEvent = edgeDeletedEvent;
    	this.newEdge = edgeDeletedEvent.getEdge();
    	this.edgeCreatedEvent = edgeCreatedEvent;
    	//super.
    }
    
    public EdgeDeletedEvent getEdgeDeletedEvent(){
    	return edgeDeletedEvent;
    }
    public EdgeCreatedEvent getEdgeCreatedEvent(){
    	return edgeCreatedEvent;
    }

    /**
     * @return {@link #edge}
     */
    public ProblemEdge getEdge() {
        return this.newEdge;
    }
}
