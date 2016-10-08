package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public interface SimStBackendExternal {
	
		public AskHint askForHintWebAuthoring(BR_Controller controller, ProblemNode problemNode);
		public String  inquireRAWebAuthoring(String selection, String input, String action, ProblemNode problemNode, String problemName);
		
}
