/*
 * Created on May 23, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;


/**
 * EdgeCreatedEvent
 *
 * The EdgeCreatedEvent extends the EdgeEvent class.  This 
 * class signals the introduction of a new edge to the 
 * problem model.
 */
public class EdgeCreatedEvent 
    extends ProblemModelEvent 
    implements EdgeEvent {

    /* --------------------------------------------
     * Internal storage.
     * ----------------------------------------  */

    /** The Edge event deals with a single edge. */
    private ProblemEdge Edge = null;
    private boolean isBeingRewired = false;

	/** Whether this edge should be selected. */
    private boolean selected = false;
    
    public void setEdgeBeingRewired(boolean newValue){ isBeingRewired = newValue;}
    public boolean isEdgeBeingRewired(){return isBeingRewired;}
    
    private LinkGroup groupToAddTo = null;

    /* ------------------------------------------------
     * Constructors 
     * --------------------------------------------- */

    /**
     * Generate a new EdgeCreated event suitable for throwing.
     *
     * @param source:  The source of the event itself.
     * @param edge:    The edge this event relates to.
     */
    public EdgeCreatedEvent(Object source, ProblemEdge edge) {
        super(source, "", null, null);
        this.Edge = edge;
    }

    /**
     * Generate a new EdgeCreated event suitable for throwing.
     *
     * @param source:     The source of the event itself.
     * @param edge:       The edge this event relates to.
     * @param subevents:  Subevents of this event for use.
     */
    public EdgeCreatedEvent(Object source, ProblemEdge edge,
			    List<ProblemModelEvent> Subevents) {
        super(source, "", null, null, Subevents);
        this.Edge = edge;
    }


    /* ----------------------------------------------
     * EdgeEvent Interface.
     * ------------------------------------------- */
    
    /**
     * @return {@link #edge}
     */
    public ProblemEdge getEdge() {
        return this.Edge;
    }
	public void setGroupToAddTo(LinkGroup groupToAddTo) {
		this.groupToAddTo = groupToAddTo;
	}
	public LinkGroup getGroupToAddTo() {
		return groupToAddTo;
	}

    /**
	 * @return the {@link #selected}
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected new value for {@link #selected}
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
