/*
 * Created on Mar 18, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;


/**
 * @author mpschnei, collinl
 *
 * Created on: Mar 18, 2005
 * Updated July 21st 2009 Collin Lynch.
 *
 * This is the listener that should be used to catch all 
 * {@link #ProblemModelEvents} and subclasses of if.  
 * You should not add specialized listeners
 * for the specific subclasses as the ProblemModelEvents can be wrapped up
 * as compound events for more efficient processing.  As a result events
 * of interest such as an EdgeEvent will be embedded in this more general
 * event and should be extracted from it.  
 *
 * Code to identify relevant subevents can be found on the ProblemModelEvent
 * and it can be used to extract all the relevant subitems.
 *
 * In order to register this listener link it to the Problem Model via
 * {@link ProblemModel#addProblemModelListener}.
 */
public interface ProblemModelListener {

    /** Feature called when an event occurred. */
	public void problemModelEventOccurred(ProblemModelEvent e);
    //public void problemModelEventOccurred(ProblemModel pm, ProblemModelEvent e);
}
