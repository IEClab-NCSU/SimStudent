/**
 * Updated June 21st 2009
 */

package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;

/** 
 * The EdgeUpdatedEvent class reflects a change to a given edge event
 * in this case the direction of the edge or the data of the edge
 * It has embedded flags indicating what part of the edge was changed
 * so that a check can be made.  This may be the edge linking or the
 * {@link EdgeData EdgeData}.  
 *
 * This event, once sent will guide any updates including model changes.
 * The class itself is a subset of the {@link EdgeEvent EdgeEvent} class.
 *
 * Note: [Kevin Zhao](kzhao) Well, when I first created this event, 
 * I tried to imitate the EdgeCreatedEvent. However, this was created 
 * more so to make the 'insert a node in a link'
 * better. So, this method really isn't "EdgeUpdatedEvent" rather 
 * htan more of a "EdgeFixInsertInNode" event. Sorry :/
 */
public class EdgeUpdatedEvent 
    extends ProblemModelEvent
    implements EdgeEvent {
    

    /* ---------------------------------------
     * Internal Storage.
     * ------------------------------------ */

    /** 
     * True if the edge's link parameters (src/dest node)
     * were changed by this event.      
     */
   // private boolean EdgeLinkingChanged = false;

    /** True if the Edge Data was altered by this event. */ 
    private boolean EdgeDataChanged = false;

    /** The Edge event deals with a single edge. */
    private ProblemEdge Edge = null;    

    /** (Collinl)  Not sure why this is here. */
    private static final long serialVersionUID = 1L;


    // private ProblemEdge problemEdge;

    // /** True if this is a first-step item TO BE REMOVED. */
    //private boolean firstStep;
	

    /* ----------------------------------------------
     * Constructors.
     * -----------------------------------------*/

    /**
     * Generate and return a new EdgeUpdatedEvent that will 
     * be used to signal changes.
     *
     * @param source: the source of the edge change.
     * @param edge:  is the ProblemEdge that is being updated
     * @param EdgeLinkingChanged: Flag indicating if the link 
     *                   was updated.
     * @param EdgeDataChanged:  Flag indicating change to the edge data.
     */
    public EdgeUpdatedEvent(Object source, ProblemEdge edge, 
			    //boolean EdgeLinkingChanged, 
			    boolean EdgeDataChanged) {
	super(source, "EdgeUpdated", null, edge);
	this.Edge = edge;
	//this.EdgeLinkingChanged = EdgeLinkingChanged;
	this.EdgeDataChanged = EdgeDataChanged;
    }

    /**
     * Signal updates to the edge events.
     *
     * @param source: the source of the edge change.
     * @param edge:  is the ProblemEdge that is being updated
     * @param EdgeLinkingChanged: Flag indicating if the link 
     *                   was updated.
     * @param EdgeDataChanged:  Flag indicating change to the edge data.
     */
    public EdgeUpdatedEvent(Object source, ProblemEdge edge, 
			    //boolean EdgeLinkingChanged, 
			    boolean EdgeDataChanged,
			    List<ProblemModelEvent> Subevents) {
	super(source, "EdgeUpdated", null, edge, Subevents);
	this.Edge = edge;
	//this.EdgeLinkingChanged = EdgeLinkingChanged;
	this.EdgeDataChanged = EdgeDataChanged;
    }
    

    
    
    /* ----------------------------------------
     * Source Interfaces.
     * ------------------------------------  */

    /** Return true if the edge's linking was changed. */
   /* public boolean edgeLinkingChangedP() { 
	return this.EdgeLinkingChanged;
    }*/

    /** Return true if the edge's data was changed. */
    public boolean edgeDataChangedP() { 
	return this.EdgeDataChanged;
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


}
