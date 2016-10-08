/**
 * 
 */
package edu.cmu.pact.miss.PeerLearning.GameShow;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import pact.CommWidgets.TutorWrapper;

/**
 * @author mazda
 *
 */
public class GameShowWrapper extends TutorWrapper {

	private Contestant contestant = null;
	
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public GameShowWrapper(BR_Controller controller) {
    	super(controller);
        wrapperSupport.setRunningSimStGameShow(true);
        super.initTutorWindow();
        wrapperSupport.getController().setStudentInterface(this);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    
    public void doLogout()
    {
    	if(contestant != null)
    	{
    		contestant.leave();
    	}
    	super.doLogout(false,false);
    }

	public void setContestant(Contestant contestant) {
		this.contestant = contestant;
		
	}
    

}
