/*
 * Created on May 8, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

/**
 * This event signals the construction of a new problem.  When called
 * it means a reload or clearing of the existing problem model and 
 * as a consequence a change to the associated elements.
 */
public class NewProblemEvent extends ProblemModelEvent {

    /* ----------------------------------------------
     * Internal Storage.
     * --------------------------------------------*/

    /** Static message for the event. */
    private static final String NEW_PROBLEM = "New Problem";
    
    /** 
     * Flag indicating the order of graph construction. 
     * True means create the new graph with the top-level 
     *(universe) group unordered. */
    private final boolean isUnordered;


    /* -----------------------------------------------
     * Constructors.
     * ---------------------------------------------*/
    
    /**
     * Construct a new problem event using a Boolean class.
     *
     * @param source:       The source of the call.
     * @param isUnordered:  Flag indicating construction order.
     *
     */
    public NewProblemEvent(Object source, Boolean isUnordered) {
        this(source, 
	     (isUnordered == null ? false : isUnordered.booleanValue()));
    }

 
    /**
     * Construct a new problem event using a boolean.
     *
     * @param source:       The source of the call.
     * @param isUnordered:  Flag indicating construction order.
     */
    public NewProblemEvent(Object source, boolean isUnordered) {
        super(source, NEW_PROBLEM, null, null);
        this.isUnordered = isUnordered;
    }


    /**
     * Construct a new problem event using a Boolean class.
     *
     * @param source:       The source of the call.
     * @param isUnordered:  Flag indicating construction order.
     * @param Subevents:    Subevents of this item.
     */
    public NewProblemEvent(Object source, Boolean isUnordered,
			   List<ProblemModelEvent> Subevents) {
        this(source, 
	     (isUnordered == null ? false : isUnordered.booleanValue()),
	     Subevents);
    }

 
    /**
     * Construct a new problem event using a boolean.
     *
     * @param source:  The source of the call.
     * @param isUnordered:  Flag indicating construction order.
     * @param Subevents:    Subevents of this event.
     */
    public NewProblemEvent(Object source, boolean isUnordered,
			   List<ProblemModelEvent> Subevents) {
        super(source, NEW_PROBLEM, null, null, Subevents);
        this.isUnordered = isUnordered;
    }


    /* -----------------------------------------
     * Parameter Access.
     * -------------------------------------- */
    
    /**
     * @return the {@link #isUnordered}
     */
    public boolean isUnordered() {
	return isUnordered;
    }
}
