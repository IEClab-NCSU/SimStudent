package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class AskHintClSolverTutor extends AskHintClAlgebraTutor {

	public AskHintClSolverTutor(BR_Controller brController, ProblemNode currentNode) {
		super(brController, currentNode);
	}
	
	public void getHint(BR_Controller brController, ProblemNode currentNode) {
		
		InquiryClSolverTutor iclSolverTutor = 
			new InquiryClSolverTutor(SimSt.getClSolverTutorHost(), SimSt.getClSolverTutorPort());
		
		// goToState() sets the skill that has been applied most recently
		// to identify the type of type-in step (for which the hintMessage
		// only tell "left" or "right" 
		int numSteps = iclSolverTutor.goToState(brController, currentNode);

		// hintMessage -> null;left;3x+2
		// hintMessage -> 3x+1 = 5;subtract;1
		String hintMessage = iclSolverTutor.askNextStep();
		String clAction = hintMessage.split(";")[1];
		String skillName = null;
		
		// Exit when the tutor said it's done
		if ("done".equals(clAction)) {
			skillName = SimSt.KILL_INTERACTIVE_LEARNING;

		} else {
		
			// Continue otherwise...
			if (numSteps == 0) {
				skillName = clAction;
			} else {
				skillName = iclSolverTutor.getCurrentSkill() + "-typein";
			}
			setSelection(findSelection(brController, currentNode, clAction));
			setInput(findInput(hintMessage, numSteps));
			setSai(new Sai(getSelection(), getAction(), getInput()));
		}
		setSkillName(skillName);
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
        
        String selection = "commTable1_";
        
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
