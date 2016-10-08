package SimStAlgebraV8;

import java.util.Vector;
import javax.swing.JOptionPane;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SAIConverter;
import edu.cmu.pact.miss.Rule;
import edu.cmu.pact.miss.InquiryClAlgebraTutor;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import cl.utilities.TestableTutor.SAI;

public class AlgebraV8AdhocSAIConverter extends SAIConverter {
	
	/*
	* Interface specific implementation for specifying the selection,
	* action and input. This is done to decouple the existing code 
	* from specifying the selection, action, input which needs to be
	* commented / uncommented out depending upon if it is a 1 table 
	* multiple column or 3 table single column format.
	*	
	*/
	public SAI convertCtatSaiToClSai(String selection, String action, String input){
			
		String clSelection = null;
		String clAction = null;
		String clInput = null;
		SAI sai = null;
			
		String table = selection.substring(DORMIN_STEM.length(), DORMIN_STEM.length()+1);
		int tableVal = Integer.parseInt(table);
		if(tableVal == 3){
			String[] tokens = input.split(" ");
			if(tokens[0].toLowerCase().startsWith("combine")) {
				clAction = "clt";
				clInput = (tokens.length == 4 ? tokens[3] : "" );
			} else {
				clAction = tokens[0];
				clInput = (tokens.length == 2 ? tokens[1] : "" );
			}
			sai = new SAI("", clAction, clInput);
			return sai;
		}
		else {
		    clAction = (tableVal == 1 ? "left" : "right");
		    clInput = input;	
			sai = new SAI("", clAction, clInput);
			return sai;
		}
	}
		
	// ad-hoc 
	// skill-operand can be entered only when numPrevSteps is 0
	// Thus, the following step to enter a skill-operand is automatically wrong
	// 
	// e.d.  2x + 3     = 5      sub 3
	//       2x + 3 - 3 = _____  add 3
	// 
	// this is necessary, because the Carnegie Learning Algebra Tutor is buggy 
	// with those stpes
	public boolean validSelection(String selection, int numPrevSteps) {
	        
	    boolean validSelection = true;
	        
	    // selection: dorminTable1_C3R3
	    int cIndex = selection.indexOf('C');
	    int rIndex = selection.indexOf('R');
	    if(cIndex < 0 || rIndex < 0)
	      	return false;
	        String table = selection.substring(DORMIN_STEM.length(), DORMIN_STEM.length()+1);
	        if ((numPrevSteps != 0 && "3".equals(table)) ||
	            (numPrevSteps == 0 && !"3".equals(table))) {
	            validSelection = false;
	        }

	        return validSelection;
	}
	
	/*
	* Interface specific implementation for specifying the selection,
	* action and input. This is done to decouple the existing code 
	* from specifying the selection, action, input which needs to be
	* commented / uncommented out depending upon if it is a 1 table 
	* multiple column or 3 table single column format.
	*	
	* skillName might be "left" or "right" for a type-in step
	*/
	@SuppressWarnings("unchecked")
	public String convertClResponseToCtatSai(BR_Controller brController,
			ProblemNode currentNode, String clAction) {

		String selection = "dorminTable1_";
		ProblemNode startNode = null;
		// To revert to the old quiz use 
		// startNode = brController.getProblemModel().getStartNode();
		if(brController.getMissController().getSimSt().isValidationMode()) {
			 startNode = brController.getMissController().getSimSt().getValidationGraph().getStartNode();

		} else if(brController.getMissController().getSimSt().getSsInteractiveLearning() == null  || 
			(!brController.getMissController().getSimSt().getSsInteractiveLearning().isTakingQuiz() && !brController.getMissController().getSimSt().isValidationMode())){
			 startNode = brController.getProblemModel().getStartNode();
			 if(startNode == null)
				 return "";
		} else {
			 startNode = brController.getMissController().getSimSt().getSsInteractiveLearning().getQuizGraph().getStartNode();
			 if(trace.getDebugCode("miss")) trace.out("miss", "" + startNode.getName());
		 }

		//ProblemNode startNode = brController.getProblemModel().getStartNode();
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
    	if(selection.length() > DORMIN_STEM.length()) {

	    	char table = selection.charAt(DORMIN_STEM.length());
	    	int colIndex = selection.indexOf('C')+1;
	    	char col = selection.charAt(colIndex);
	    	int rowIndex = selection.indexOf('R')+1;
	    	char row = selection.charAt(rowIndex);
	    	selection = DORMIN_STEM+col+"_C"+table+"R"+row;
    	}

		return selection;
	}
	
	public boolean isSelectionTransformationStep(String selection) {
		
		String transformationSelection = DORMIN_STEM+"3_";
		if(selection.contains(transformationSelection))
			return true;
		else 
			return false;
	}
	
	public static final String DORMIN_STEM = "dorminTable";
}

