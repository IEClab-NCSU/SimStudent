package edu.cmu.pact.miss;


import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.Sai;

public class AskHintWebAuthoring extends AskHint{

	
	BR_Controller brController;
	Sai sai;
	public AskHintWebAuthoring(BR_Controller brController, ProblemNode problemNode){
	        this.brController=brController;
	        this.skillName=brController.getMissController().getSimSt().getSsInteractiveLearning().getSkillname();
	        String selection = brController.getMissController().getSimSt().getSsInteractiveLearning().getSelection();
	        String action = brController.getMissController().getSimSt().getSsInteractiveLearning().getAction();
	        String input = brController.getMissController().getSimSt().getSsInteractiveLearning().getInput();
	        this.sai= new Sai(selection,action,input);
	        getHint(brController, null);
			brController.getMissController().getSimSt().updateSimStWorkingMemoryDirectly(sai.getS(),sai.getA(),sai.getI());

	       // this.updateNodeInBR(brController, problemNode);
	        //System.out.println("hint.node = " + this.getNode() );
	        
	}
	
	@Override
	public void getHint(BR_Controller controller, ProblemNode problemNode) {
		// TODO Auto-generated method stub
		
	
			String selection=sai.getS();//"textField2";
			String action=sai.getA();//"UpdateTextArea";
			String input=sai.getI();//"divide 3";
	
			this.setSelection(selection);
			this.setAction(action);
			this.setInput("input");
			
			String[] msg = new String[2];

			this.setSai(new Sai(selection,action,input));
		//	this.skillName=skill;
			this.setHintMsg(msg);
	}

	
	
}
