package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class JessRuleOracle implements Oracle {

	
	@Override
	public boolean isCorrectStep() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Sai askNextStep(String problemName,ProblemNode node, BR_Controller brController) {
		// TODO Auto-generated method stub
		InquiryJessOracle jessOracle = new InquiryJessOracle(brController.getMissController().getSimSt(),brController);
		jessOracle.init(problemName);
		//System.out.println("Goning to state : "+node);
		jessOracle.goToState(brController,node);
		return jessOracle.askNexStep();
	}

}
