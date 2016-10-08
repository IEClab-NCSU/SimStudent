package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;


public abstract class StartStateChecker {

	
	
    public abstract boolean checkStartState(String problem, BR_Controller brController);
    
}
