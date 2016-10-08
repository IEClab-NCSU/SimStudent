package edu.cmu.pact.miss;

import java.util.ArrayList;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

/**
 * 
 */
public interface InterfaceElementGetter {

	// Method signatures
	/**	Start state elements for the interface defined in the config file */
	ArrayList<String> getStartStateElements();
	
	/**	FoA elements for the interface defined in the config file */
	String[] getFoAElements();
	
	/**	Component Names for the interface defined in the config file */
	String[] getComponentNames();
	
	public void simulateStartStateElementEntry(BR_Controller controller, String problemName);
}
