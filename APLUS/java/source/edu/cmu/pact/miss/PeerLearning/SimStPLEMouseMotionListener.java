package edu.cmu.pact.miss.PeerLearning;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.SimSt;


public class SimStPLEMouseMotionListener implements MouseMotionListener {

	BR_Controller brController = null;
    SimStLogger logger;
	        
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public SimStPLEMouseMotionListener(BR_Controller brController) {
        setBrController(brController);
        logger = new SimStLogger(getBrController());
    }

	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		//when mouse is dragged, reset the interface timer
		getSimSt().resetSimStInactiveInterfaceTimer();
	}



	@Override
	public void mouseMoved(MouseEvent arg0) {
		//when mouse is moved, reset the interface timer
		getSimSt().resetSimStInactiveInterfaceTimer();
	}
	
	
	
	
	 // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     // Getters & Setters
     // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public BR_Controller getBrController() {
        return brController;
    }

    public void setBrController(BR_Controller brController) {
        this.brController = brController;
    }

    private SimSt getSimSt() {
        return ((SimSt) getBrController().getMissController().getSimSt());
    }




    
}
