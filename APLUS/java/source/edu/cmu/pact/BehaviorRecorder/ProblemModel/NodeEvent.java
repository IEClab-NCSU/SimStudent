/**
 * Created July 22nd 2009.
 * @author Collin Lynch.
 * @copyright 2009 CTAT project.
 */

package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;


/** 
 * The NodeEvent interface is an intermediate between the 
 * {@link #ProblemModelEvent} and the NodeCreated, etc events.  It 
 * can be constructed with a specific node in mind and can 
 * be called to detect updates or changes later.
 *
 * This was created as an interface due to the need to use super 
 * instantiation in the events and the fact that the ProblemNodes
 * are needed for multiple event types.
 */
public interface NodeEvent {

    /**
     * Obtain the node associated with this event.
     *
     * @return {@link #Node}
     */
    public ProblemNode getNode();
}
