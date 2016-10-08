/**
 * 
 */
package edu.cmu.pact.ctat;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

/**
 * @author chc
 *
 */
public interface StudentInterfacePanel {
	public BR_Controller getController();
	
    public void setController(BR_Controller controller);
}
