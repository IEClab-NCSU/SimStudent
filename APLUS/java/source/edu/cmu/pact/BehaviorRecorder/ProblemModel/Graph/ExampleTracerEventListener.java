package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

/**
 * 
 * @author eharpste
 *
 * This Listener type was created because it was decided that there was no need to burden the 
 * ProblemModelListener system with knowledge of things specific to ExampleTracerEvents.
 *
 */

public interface ExampleTracerEventListener {
	    /** Feature called when an event occurred. */
	    public void ExampleTracerEventOccurred(ExampleTracerEvent e);
}
