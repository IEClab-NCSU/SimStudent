package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.JessOracleRete;
import edu.cmu.pact.jess.RuleActivationNode;

public class AskHintJessOracle extends AskHint{


	BR_Controller brController=null;
	private void setBrController(BR_Controller brController){
		this.brController=brController;
	}
	private BR_Controller getBrController(){
		return this.brController;
	}
	
	
	String problem="";
	public AskHintJessOracle(BR_Controller brController,
			ProblemNode currentNode) {
		
		/*	if (brController.getMissController().getSimStPLE().getSsInteractiveLearning().isTakingQuiz()){			
				Sai cached=brController.getMissController().getSimSt().getSaiCache();
				if (cached==null) {	return;	}	
				this.setSelection(cached.getS());
				this.setAction(cached.getA());
				this.setInput(cached.getI());
				return;
			}
		*/		
		
			setBrController(brController);
			this.problem=brController.getProblemName().replace("_", "=");
			getHint(brController,currentNode);
			
	}
	
	 
	
	
	
	
	@Override
	public void getHint(BR_Controller brController, ProblemNode currentNode) {
			
		
		this.setBrController(brController);
		Vector <RuleActivationNode> activationList = new Vector<RuleActivationNode>();
	   
      //  InquiryJessOracle iJessOracle =  getBrController().getMissController().getSimSt().getInquiryJessOracle();
        InquiryJessOracle iJessOracle =  new InquiryJessOracle(getBrController().getMissController().getSimSt(),getBrController()); 
        
        
        iJessOracle.init(problem);
 
        iJessOracle.goToState(getBrController(), currentNode);

        
        Sai nextStepSai = iJessOracle.askNexStep();

       // trace.out("****" + iJessOracle.altSug);
        brController.getMissController().getSimSt().altSug.clear();
        brController.getMissController().getSimSt().altSug=iJessOracle.altSug;
        
		if (nextStepSai == null) {
			this.setSelection("NotAvailable");
			this.setAction("NotAvailable");
			this.setInput("NotAvailable");
		} else {
			this.setSelection(nextStepSai.getS());
			this.setAction(nextStepSai.getA());
			this.setInput(nextStepSai.getI());
			this.setRuleName(nextStepSai.getRuleName());
			
			String[] msg = new String[2];

			this.setSai(new Sai(nextStepSai.getS(), nextStepSai.getA(),
					nextStepSai.getI()));

			this.setHintMsg(iJessOracle.getHintMessages());

		}
		/*
   	
        try {
      		if(trace.getDebugCode("nbarba"))  trace.out("jessOracle", "Calling gatherJessOracleAgenda for problem :" + problem);
      		
      		activationList=iJessOracle.getJessOracleAgenda(getBrController());

           } catch (Exception e) {
              e.printStackTrace();
         }
	
       
       	 activationList=iJessOracle.removeDuplicateSai(activationList);
		
       
       	 if (activationList.size()==0) {
       		 this.setSelection("NotAvailable");
			 this.setAction("NotAvailable");
			 this.setInput("NotAvailable");		
       	 }
       	 else{
       		String sel=activationList.get(0).getActualSelection();
    		String act=activationList.get(0).getActualAction();
    		String inp=activationList.get(0).getActualInput();
    		
       		
			this.setSelection(sel);
			this.setAction(act);
			this.setInput(inp);
			String[] msg=new String[1];
			
			this.setSai(new Sai(sel,act,inp)); 
			msg[0]=" " + this.getSelection() + " " + this.getAction() + " " + this.getInput();
			this.setHintMsg(msg);
       		
       		
       	}*/
       

		
		
		
	}
	
		
		
		
}
