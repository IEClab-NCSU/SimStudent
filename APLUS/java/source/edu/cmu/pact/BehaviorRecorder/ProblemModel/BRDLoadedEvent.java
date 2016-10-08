package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.List;

public class BRDLoadedEvent extends ProblemModelEvent {
	 private static final String NEW_BRD_LOADED = "New BRD Loaded";
	 List<ProblemModelEvent> subEvents = null;
	 public BRDLoadedEvent(Object source, List<ProblemModelEvent> subEvents) {
	     //Object source, String propertyName, Object oldValue, Object newValue, List<ProblemModelEvent> Subevents  
		 super(source, NEW_BRD_LOADED ,null, null, subEvents);
		 this.subEvents = subEvents;
	 }
}
