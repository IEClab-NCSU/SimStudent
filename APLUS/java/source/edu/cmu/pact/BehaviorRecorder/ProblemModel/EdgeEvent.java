/*
 * Created on July 21st 2009
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;


/** 
 * The EdgeEvent interface is an intermediate between the 
 * {@link ProblemModelEvent ProblemModelEvent}
 * and the EdgeCreated, etc events.  It exists to identify 
 * those events that relate to one edge and thus have a 
 * related edge.  The method itself for access to the edge 
 * will have to be implemented by the individual subclasses.
 *
 * I opted for an interface as it is a) necessary to use super
 * for the subevents and b) some events such as the nodecreated event
 * actually relate to both nodes and edges. 
 */
public interface EdgeEvent {

    /** 
     * Retreive the problem Edge on this item. 
     * @return {@link #ProblemEdge}
     */
    public ProblemEdge getEdge();
}
