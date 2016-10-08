/**
 * 
 */
package edu.cmu.pact.miss.PeerLearning;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.ctat.TutorController;
import pact.CommWidgets.TutorWrapper;

/**
 * @author mazda
 *
 */
public class SimStPLEWrapper extends TutorWrapper {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public SimStPLEWrapper(BR_Controller controller) {
    	super(controller);
        wrapperSupport.setRunningSimStPLE(true);
        super.initTutorWindow();
        wrapperSupport.getController().setStudentInterface(this);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    

}
