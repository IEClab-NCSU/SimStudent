package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class ClOracle implements Oracle {
	
	
	private String selection = null;
	public String getSelection() { return selection; }
	public void setSelection(String selection) { this.selection = selection; }

	private String action = "UpdateTable";
	public String getAction() { return action; }
	public void setAction(String action) { this.action = action; }

	private String input = null;
	public String getInput() { return input; }
	public void setInput(String input) { this.input = input; }
	
	@Override
	public boolean isCorrectStep() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Sai askNextStep(String problemName, ProblemNode node, BR_Controller brController) {
		// TODO Auto-generated method stub
		
		InquiryClSolverTutor clSolver = 
				new InquiryClSolverTutor();
		//if(brController.getMissController().getSimSt().isSaiConverterDefined())
			//clSolver.setSAIConverter(brController.getMissController().getSimSt().getSAIConverter());
		int numSteps=clSolver.goToState(brController, node);
		String message = clSolver.askNextStep();
		String clAction = message.split(";")[1];
		//System.out.println("Message : "+message);
		setSelection(findSelection(brController, node, clAction));
		setInput(findInput(message, numSteps));
		return new Sai(getSelection(), getAction(), getInput());
		
		
	}
	
	private String findInput(String hintMessage, int numSteps) {
		String input = null;
		String[] msg = hintMessage.split(";");
		if (numSteps == 0) {
			// Skill operand...
			input = msg[1];
			if (EqFeaturePredicate.isOperandArithmeticSkill(msg[1])) {
				// add operand
				input += " " + msg[2];
			}
		} else {
			// typein
			input = msg[2];
		}
		return input;
	}

    // skillName might be "left" or "right" for a type-in step
	private String findSelection(BR_Controller brController, ProblemNode currentNode, String clAction) {
        
        String selection = "dorminTable1_";
        
        ProblemNode startNode = brController.getProblemModel().getStartNode();
        Vector /* ProblemEdge */ path = InquiryClAlgebraTutor.findPathDepthFirst(startNode, currentNode);
        
        int stepDepth = (path == null) ? 0 : path.size();
        if ((stepDepth % 3) == 0) {
            // This is for SkillOperand
            selection += "C" + 3 + "R" + (stepDepth/3 + 1);
        } else {
            // This is for typein
        	int column = "left".equals(clAction) ? 1 : 2;
            selection += "C" + column + "R" + (stepDepth/3 + 2);
        }
        
        return selection;
    }

}
