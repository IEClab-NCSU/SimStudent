/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;


/**
 * @author sewall collinl
 *
 * Event fired when attempts to create an Edge failed.  This is a
 * descendant of the ProblemModelEvent and can contain children
 * such as other node or edge events.
 */
public class EdgeCreationFailedEvent extends ProblemModelEvent {
	
    /* ------------------------------------------------
     * Subclasses.
     * ----------------------------------------------*/

    /**  The Reason Enum specifies the failure rationale. */
    public static enum Reason { 
	LINK_AFTER_BUGGY_LINK,
	LINK_AFTER_DONE_STATE
    }
    
    /* ------------------------------------------------
     * Internal Storage.
     * --------------------------------------------- */

    /** Source of the error. */
    private final Reason cause;

    /** A message indicating the issue underlying this. */
    private final String msg;  


    /* --------------------------------------------------
     * Constructors.
     * ------------------------------------------------*/


    /**
     * Generate a new EdgeCreationFailed event with no children.
     *
     * @param controller:  The BR_Controller that issued this event.
     * @param cause:       The cause of the error.
     * @param msg:         A message indicating status.
     */
    public EdgeCreationFailedEvent(BR_Controller controller, 
				   Reason cause, String msg) {
        super(controller, "", null, null);
        this.cause = cause;
        this.msg = msg;
	}

    /**
     * @param controller:  The BR_Controller that issued this event.
     * @param cause:       The cause of the error.
     * @param msg:         A message indicating status.
     * @param Subevents:   The events wrapped in this event.
     */
    public EdgeCreationFailedEvent(BR_Controller controller, 
				   Reason cause, String msg,
				   List<ProblemModelEvent> Subevents) {
        super(controller, "", null, null, Subevents);
        this.cause = cause;
        this.msg = msg;
    }


    /* ---------------------------------------------------
     * Parameter Retreival.
     * -------------------------------------------------*/

    /**
     * @return the {@link #msg}
     */
    public String getMsg() {
	return msg;
    }
    
    /**
     * @return the {@link #cause}
     */
    public Reason getCause() {
	return cause;
    }
}
