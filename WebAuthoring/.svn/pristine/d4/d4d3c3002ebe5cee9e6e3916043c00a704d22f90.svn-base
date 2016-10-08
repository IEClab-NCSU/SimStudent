package interaction;


import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.Sai;

public class AskHintWebAuthoring extends AskHint{

	
	BR_Controller brController;
	SAI sai;
	public AskHintWebAuthoring(BR_Controller brController, ProblemNode problemNode, String currentSkill,SAI sai){
	        this.brController=brController;
	        this.skillName=currentSkill;
	        this.sai=sai;
	        getHint(brController, null);
	       // this.updateNodeInBR(brController, problemNode);
	        //System.out.println("hint.node = " + this.getNode() );
	        
	}
	
	@Override
	public void getHint(BR_Controller controller, ProblemNode problemNode) {
		// TODO Auto-generated method stub
		
	
			String selection=sai.getFirstSelection();//"textField2";
			String action=sai.getFirstAction();//"UpdateTextArea";
			String input=sai.getFirstInput();//"divide 3";
	
			this.setSelection(selection);
			this.setAction(action);
			this.setInput("input");
			
			String[] msg = new String[2];

			this.setSai(new Sai(selection,action,input));
		//	this.skillName=skill;
			this.setHintMsg(msg);
	}

	
	
}
