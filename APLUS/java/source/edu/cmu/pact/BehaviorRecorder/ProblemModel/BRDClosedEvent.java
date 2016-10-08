package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

public class BRDClosedEvent extends ProblemModelEvent {

	private static final String EVENT_PROPERTY = "BRD File Closed";
	
	protected BRDClosedEvent(Object source, List<ProblemModelEvent> subEvents) {
		super(source, EVENT_PROPERTY, null, null, subEvents);
	}

}
