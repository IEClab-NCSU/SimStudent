package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public interface Oracle {
		public boolean isCorrectStep();
		public Sai askNextStep(String problemName,ProblemNode node,BR_Controller brcontroller);
}
