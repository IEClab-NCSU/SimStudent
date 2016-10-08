package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;

public class QuizState implements Cloneable {

	// Fields
	private String originalEqn;

	public String getOriginalEqn() {
		return originalEqn;
	}
	public void setOriginalEqn(String originalEqn) {
		this.originalEqn = originalEqn;
	}
	
	private String previousStep;

	public String getPreviousStep() {
		return previousStep;
	}
	public void setPreviousStep(String previousStep) {
		this.previousStep = previousStep;
	}
	
	private String currentStep;

	public String getCurrentStep() {
		return currentStep;
	}
	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}
	
	private ProblemNode probNode;
	
	public ProblemNode getProbNode() {
		return probNode;
	}
	public void setProbNode(ProblemNode probNode) {
		this.probNode = probNode;
	}

	private SimStInteractiveLearning ssInteractiveLearning;
	
	public SimStInteractiveLearning getSsInteractiveLearning() {
		return ssInteractiveLearning;
	}
	public void setSsInteractiveLearning(SimStInteractiveLearning ssInteractiveLearning) {
		this.ssInteractiveLearning = ssInteractiveLearning;
	}
	
	private SimStPLE simStPLE;
	public SimStPLE getSimStPLE() {
		return simStPLE;
	}
	public void setSimStPLE(SimStPLE simStPLE) {
		this.simStPLE = simStPLE;
	}

	private boolean isTypeInStep = false;
	public boolean isTypeInStep() {
		return isTypeInStep;
	}
	public void setTypeInStep(boolean isTypeInStep) {
		this.isTypeInStep = isTypeInStep;
	}
	
	private boolean isDoneStep = false;
	public boolean isDoneStep() {
		return isDoneStep;
	}
	public void setDoneStep(boolean isDoneStep) {
		this.isDoneStep = isDoneStep;
	}
	
	public QuizState() {}
	
	public QuizState(SimStPLE ssPLE, SimStInteractiveLearning ssInterLearning, String origEqn) {
		this.simStPLE = ssPLE;
		this.ssInteractiveLearning = ssInterLearning;
		this.originalEqn = origEqn;
	}

	
}
