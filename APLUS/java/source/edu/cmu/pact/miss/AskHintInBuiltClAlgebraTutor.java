package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class AskHintInBuiltClAlgebraTutor extends AskHintClAlgebraTutor {


    public static final String COMM_STEM = "commTable";

	public AskHintInBuiltClAlgebraTutor(BR_Controller brController,
			ProblemNode currentNode) {
		super(brController, currentNode);
	}

	public void getHint(BR_Controller brController, ProblemNode currentNode) {

		InquiryClSolverTutor iclSolverTutor = new InquiryClSolverTutor();
		if(brController.getMissController().getSimSt().isSaiConverterDefined())
			iclSolverTutor.setSAIConverter(brController.getMissController().getSimSt().getSAIConverter());
		
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
			//skillName = SimSt.KILL_INTERACTIVE_LEARNING;
			skillName = "done";
			setSelection("done");
			setAction("ButtonPressed");
			setInput("-1");
			setSai(new Sai(getSelection(), getAction(), getInput()));
		} else {
			// Continue otherwise...
			if (numSteps == 0) {
				skillName = clAction;
			} else {
				skillName = iclSolverTutor.getCurrentSkill() + "-typein";
			}
			
			if(brController.getMissController().getSimSt().isSaiConverterDefined()) {
								
				setSelection(iclSolverTutor.getSAIConverter().convertClResponseToCtatSai(brController, currentNode, clAction));
				setInput(findInput(hintMessage, numSteps));
				setSai(new Sai(getSelection(), getAction(), getInput())); 
				if(iclSolverTutor.getSAIConverter().isSelectionTransformationStep(getSelection())) {
					String[] msg = null;
					msg = iclSolverTutor.getHintMessages(getSelection(), "", "");
					if(msg != null) {
						setHintMsg(msg);
					}
				}
			}
			else
			{
				String selection = "commTable1_";
				ProblemNode startNode = brController.getProblemModel().getStartNode();
				Vector /* ProblemEdge */path = InquiryClAlgebraTutor.findPathDepthFirst(startNode, currentNode);

				int stepDepth = (path == null) ? 0 : path.size();
				if ((stepDepth % 3) == 0) {
					// This is for SkillOperand
					selection += "C" + 3 + "R" + (stepDepth / 3 + 1);
				} else {
					// This is for typein
					int column = "left".equals(clAction) ? 1 : 2;
					selection += "C" + column + "R" + (stepDepth / 3 + 2);
				}
				setSelection(selection);
				setInput(findInput(hintMessage, numSteps));
				setSai(new Sai(getSelection(), getAction(), getInput())); 
			}
			
			// Convert the input oracle gives as per the input checker(if defined).
			// Example Oracle: clt 7y+(-6)-(-6) InputChecker: combine like terms 7y+(-6)-(-6)
			if(brController != null && brController.getMissController() != null && brController.getMissController().getSimSt() != null
					&& brController.getMissController().getSimSt().isInputCheckerDefined()) {

				if(!brController.getMissController().getSimSt().getInputChecker().checkInput(getSelection(), getInput(), null,null)) {
					String newInput = brController.getMissController().getSimSt().getInputChecker().interpret(getSelection(), getInput());
					if(newInput != null && newInput.length() > 0) {
						setInput(newInput);
						setSai(new Sai(getSelection(), getAction(), getInput()));
					}
				}
			}
		}
		
		setSkillName(skillName);
		
		/*
		if(!brController.getMissController().getSimSt().isValidationMode() && brController.getMissController().
				getSimSt().getSsInteractiveLearning() != null && !clAction.equalsIgnoreCase("done") 
				&& (brController.getMissController().getSimSt().getSsInteractiveLearning().isRunningFromBrd()
				|| brController.getMissController().getSimSt().getHintMethod().equals(AskHint.HINT_METHOD_SOLVER_TUTOR)))
		{
			updateNodeInBR(brController, currentNode);
		}
		*/
		
		if (skillName.contains("rf") || skillName.contains("clt") ){
			String[] r=null;
			setHintMsg(r);
		}
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

	
//	skillName might be "left" or "right" for a type-in step
	@SuppressWarnings("unchecked")
	private String findSelection(BR_Controller brController,
			ProblemNode currentNode, String clAction) {

		String selection = "commTable1_";

		ProblemNode startNode = brController.getProblemModel().getStartNode();
		Vector /* ProblemEdge */path = InquiryClAlgebraTutor.findPathDepthFirst(startNode, currentNode);

		int stepDepth = (path == null) ? 0 : path.size();
		if ((stepDepth % 3) == 0) {
			// This is for SkillOperand
			selection += "C" + 3 + "R" + (stepDepth / 3 + 1);
		} else {
			// This is for typein
			int column = "left".equals(clAction) ? 1 : 2;
			selection += "C" + column + "R" + (stepDepth / 3 + 2);
		}
		
		// Change the selection format to suit the 3 table 1 column format
    	if(selection.length() > COMM_STEM.length())
    	{
	    	char table = selection.charAt(COMM_STEM.length());
	    	int colIndex = selection.indexOf('C')+1;
	    	char col = selection.charAt(colIndex);
	    	int rowIndex = selection.indexOf('R')+1;
	    	char row = selection.charAt(rowIndex);
	    	selection = COMM_STEM+col+"_C"+table+"R"+row;
    	}

		return selection;
	}
}
