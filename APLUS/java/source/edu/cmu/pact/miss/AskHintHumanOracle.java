package edu.cmu.pact.miss;

import java.util.Calendar;

import javax.swing.JFrame;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.PeerLearning.SimStConversation;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;
import pact.CommWidgets.JCommButton;

public class AskHintHumanOracle extends AskHint {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    BR_Controller brController = null;
    public BR_Controller getBrController() {
        return brController;
    }
    public void setBrController(BR_Controller brController) {
        this.brController = brController;
    }

    private static SaiDrop saiDrop;
    public static SaiDrop getSaiDrop(){
        return saiDrop;
    }

    private static MessageDrop skillNameDrop;
    public static MessageDrop getSkillNameDrop(){
        return skillNameDrop;
    }
    
    public static boolean isWaitingForSkillName = false;
    public static boolean isWaitingForSai = false;
    
    SaiAndSkillName saiAndSkillName = null;
    
    private SimStLogger logger;
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 	
    public AskHintHumanOracle(BR_Controller brController, ProblemNode parentNode){
        setBrController(brController);
        logger = new SimStLogger(brController);
        getHint(brController, parentNode);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    //this function puts an Sai in the SaiDrop.
    public static void hereIsTheSai(Sai sai) {
    	if(trace.getDebugCode("miss")) trace.out("miss","AskHintHumanOracle hereIsTheSai");
        saiDrop.put(sai);
    }

    //this function puts an Sai in the SaiDrop.
    public static void hereIsTheSkillName(String skillName) {
        skillNameDrop.put(skillName);
    }

    //28 Jan 2008: killer SAI and skillname
    //used for killing the thread that is waiting for an SAI
    public static void sendTheKillThreadMessage() {
    }

    private SaiAndSkillName waitForSaiAndSkillName(SaiAndSkillName saiNamedSkill) {
        
        String skillName = null;      
        this.saiDrop = new SaiDrop();
        isWaitingForSai = true;    
        Sai sai  = null;
        if(brController.getRunType().isEmpty()) {
        	sai = saiDrop.getSai();
        } else {
        	sai = saiNamedSkill.sai;
        }
        isWaitingForSai = false;
       
        if (sai.getI().isEmpty()) {

            skillName = SimSt.KILL_INTERACTIVE_LEARNING;
        }
        /**
         * @author jinyul
         * If skillNameGetter is defined, then use that to name the skill.
         */
        else if(getBrController().getMissController().getSimSt().isSkillNameGetterDefined()) {
        	/* parse for skillName using algebraoneadhocskillnamegetter */
        	SkillNameGetter sng = getBrController().getMissController().getSimSt().getSkillNameGetter();
        	skillName = sng.skillNameGetter(getBrController(), sai.getS(), sai.getA(), sai.getI());        	
        }
        else if (getBrController().getMissController().getSimSt().isSsLearnNoLabel()) {
            
            skillName = Rule.UNLABELED_SKILL;
            
        } else {
            this.skillNameDrop = new MessageDrop();
            isWaitingForSkillName = true;
            skillName = skillNameDrop.getMessage();
            isWaitingForSkillName = false;
        }

        return new SaiAndSkillName(sai, skillName);
    }

    public void getHint(BR_Controller brController, ProblemNode parentNode){

      AskHint hint = null;  

      if(logger.getLoggingEnabled()){
    	  	//hint = new AskHintInBuiltClAlgebraTutor(brController, parentNode);    		
    	  	//CL oracle must not be hardcoded! Whichever oracle grades the quiz should provide hint for logging
      		hint = brController.getMissController().getSimSt().askForHintQuizGradingOracle(brController,parentNode); 
      		brController.setHintInfo(hint);
      		trace.err("*** hint from designated oracle is :" + hint.getAction() + hint.getInput());	
      }
      
      String title = "SimStudent is asking for a hint";
      String message[] = { "I'm stuck. Please give me a hint." };
      String step = brController.getMissController().getSimSt().getProblemStepString();
      if(brController.getMissController().isPLEon())
      {
    	  SimStPLE ple = brController.getMissController().getSimStPLE();
    	
	      if(step.contains("["))
	      {
	    	  String op = step.substring(step.indexOf('[')+1, step.indexOf(']'));
	    	  String topic=ple.getSsInteractiveLearning().getExplanationGiven() ? SimStConversation.TYPEIN_HINT_EXPLANATION_TOPIC : SimStConversation.TYPEIN_HINT_TOPIC;
	    	  ple.getSsInteractiveLearning().setExplanationGiven(false);
	    	  message[0] = ple.getConversation().getMessage(topic, op);
	      }
	      else
	      {
	    	 
	    	 // String topic=ple.getSsInteractiveLearning().getExplanationGiven() ? SimStConversation.TRANSFORMATION_HINT_EXPLANATION_TOPIC : SimStConversation.TRANSFORMATION_HINT_TOPIC;
	    	  ple.getSsInteractiveLearning().setExplanationGiven(false);
	    	  message[0] = ple.getConversation().getMessage(SimStConversation.TRANSFORMATION_HINT_TOPIC);
	      }
      }
      else
      {
	      if(step.contains("["))
	      {
	    	  String op = step.substring(step.indexOf('[')+1, step.indexOf(']'));
	    	  message[0] = "I'm not quite sure how to "+op+" here.  Can you please show me what to do?";
	      }
	      else
	      {
	    	  message[0] = "I'm stuck.  I don't know what to do next.  Please show me what to do!.";
	      }
      }
      JFrame frame = brController.getActiveWindow();
      /*if(brController.getMissController().isPLEon())
      {
    	  brController.getMissController().getSimStPLE().giveMessage(message[0]);
      }
      else
      {
    	  JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
      }*/
      brController.getMissController().getSimSt().displayMessage(title, message);
      if(hint != null)
      {
    	  logger.simStLog(SimStLogger.SIM_STUDENT_DIALOGUE, SimStLogger.HINT_REQUEST_ACTION,step,"","",
	  				null, null, hint.getSelection(), hint.getAction(), hint.getInput(), 0,message[0]);
      }
      
	  if(brController.getMissController() != null && brController.getMissController().getSimSt() != null
			  && brController.getMissController().getSimSt().isSsMetaTutorMode()) {

		 // brController.getAmt().handleInterfaceAction("sshint", "implicit", "-1");
		  getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("hint-request");
	  }

//	  long hintRequestTime = Calendar.getInstance().getTimeInMillis();
      
	 
	  if (hint!=null && brController.getMissController().getSimSt().isSsMetaTutorMode() )
    	  brController.getMissController().getSimSt().getModelTraceWM().setStudentSaiEntered(WorkingMemoryConstants.FALSE);  
	  
	  brController.setStepInfo(step);
	  brController.setParentNodeInfo(parentNode);
	  brController.setMessageInfo(message[0]);
	  if(!brController.getRunType().isEmpty()) {
		  brController.setBrController(brController);
	  }
	  if(brController.getRunType()== "") {
		  SkillNameandSAISet(hint, step, parentNode, message[0], null, brController);
	  }
     /* saiAndSkillName = waitForSaiAndSkillName();
      setSai(saiAndSkillName.sai);
      this.skillName = saiAndSkillName.skillName;
      
     //System.out.println(" Skill  :  "+this.skillName);
      if (this.brController.getMissController().isPLEon())
    	  this.brController.getMissController().getSimStPLE().blockInput(true);
      
      if(brController.getMissController().getSimSt().isSsMetaTutorMode() && this.saiAndSkillName.sai.getS().equalsIgnoreCase("done") && this.saiAndSkillName.sai.getA().equalsIgnoreCase("ButtonPressed")){

    	  brController.getMissController().getSimSt().getModelTraceWM().setProblemStatus("solved");
      }
      
      if (hint!=null && brController.getMissController().getSimSt().isSsMetaTutorMode() )
    	  brController.getMissController().getSimSt().getModelTraceWM().setStudentSaiEntered(WorkingMemoryConstants.TRUE);  
      
	  if (hint!=null && brController.getMissController().getSimSt().isSsMetaTutorMode() ){
	    	  brController.getMissController().getSimSt().getModelTraceWM().setNextSelection(hint.getSelection());
	   		  brController.getMissController().getSimSt().getModelTraceWM().setNextAction(hint.getAction());
	   		  brController.getMissController().getSimSt().getModelTraceWM().setNextInput(hint.getInput());  		  
	   		  brController.getMissController().getSimSt().hintRequest=true;
	  
	  }
      
		JCommButton doneButton = (JCommButton) brController.lookupWidgetByName("Done");
   		if (doneButton!=null){
   			//System.out.println(" Done button is clicked ");
   			doneButton.setText(SimStPLE.DONE_CAPTION_ENABLED);
   		}
   		
      
      if (!skillName.equals(SimSt.KILL_INTERACTIVE_LEARNING)){
    	
    	  // TODO: Need to figure out a unified way to handle all the interface actions at one place
	      // Model-tracing the student interface action in response to the SimStudent help request
	      if(brController.getMissController().getSimSt().isSsMetaTutorMode()) {
	  	      getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("");
	    	  brController.getAmt().handleInterfaceAction(saiAndSkillName.sai.getS(), saiAndSkillName.sai.getA(), saiAndSkillName.sai.getI());
	      }
	      	
    	  if(logger.getLoggingEnabled())
    	  {
		      //hint = new AskHintInBuiltClAlgebraTutor(brController, parentNode);
		      
//		      boolean correct = false;
//		      if(brController.getMissController().getSimSt().verifyStep(brController.getProblemModel().getProblemName(),
//		    	parentNode, saiAndSkillName.sai.getS(), saiAndSkillName.sai.getA(), saiAndSkillName.sai.getI()).equals(EdgeData.CORRECT_ACTION))
//		    	correct = true;
		      
		
		      	int hintDuration = (int) ((Calendar.getInstance().getTimeInMillis() - hintRequestTime)/1000);
		      	step = brController.getMissController().getSimSt().getProblemStepString();
		      	
		      	logger.simStLog(SimStLogger.SIM_STUDENT_INFO_RECEIVED, SimStLogger.HINT_RECEIVED, 
		    		  step,"","",saiAndSkillName.sai,parentNode, hint.getSelection(),
		    		  hint.getAction(), hint.getInput(), hintDuration,message[0]);
    	  }
      		
          // ProblemModel pModel = brController.getProblemModel();
          // ProblemNode startNode = pModel.getStartNode();
          //this.node = brController.getCurrentNode();
          //this.edge = brController.getProblemModel().returnsEdge(parentNode,node);      
      }

      //this.node = brController.getCurrentNode();
      //this.edge = brController.getProblemModel().returnsEdge(parentNode,node); 
      if(saiAndSkillName != null && edge != null)
      {
    	  edge.getEdgeData().addRuleName(saiAndSkillName.skillName);
      }
      */
    }
    
    public void SkillNameandSAISet(AskHint hint, String step, ProblemNode parentNode, String message,SaiAndSkillName saiNamedSkill, BR_Controller brController) {
    	long hintRequestTime = Calendar.getInstance().getTimeInMillis();
    	if(brController.getRunType().isEmpty()) {
    		saiAndSkillName = waitForSaiAndSkillName(null);
    	} else {
    		saiAndSkillName = waitForSaiAndSkillName(saiNamedSkill);
    	}
        setSai(saiAndSkillName.sai);
        this.skillName = saiAndSkillName.skillName;
        
       //System.out.println(" Skill  :  "+this.skillName);
        if(brController.getRunType().isEmpty()) {
        	if (this.brController.getMissController().isPLEon())
        		this.brController.getMissController().getSimStPLE().blockInput(true);
        }
        
        if(brController.getMissController().getSimSt().isSsMetaTutorMode() && this.saiAndSkillName.sai.getS().equalsIgnoreCase("done") && this.saiAndSkillName.sai.getA().equalsIgnoreCase("ButtonPressed")){

      	  brController.getMissController().getSimSt().getModelTraceWM().setProblemStatus("solved");
        }
        
        if (hint!=null && brController.getMissController().getSimSt().isSsMetaTutorMode() )
      	  brController.getMissController().getSimSt().getModelTraceWM().setStudentSaiEntered(WorkingMemoryConstants.TRUE);  
        
  	  if (hint!=null && brController.getMissController().getSimSt().isSsMetaTutorMode() ){
  	    	  brController.getMissController().getSimSt().getModelTraceWM().setNextSelection(hint.getSelection());
  	   		  brController.getMissController().getSimSt().getModelTraceWM().setNextAction(hint.getAction());
  	   		  brController.getMissController().getSimSt().getModelTraceWM().setNextInput(hint.getInput());  		  
  	   		  brController.getMissController().getSimSt().hintRequest=true;
  	  
  	  }
  	  if(brController.getRunType().isEmpty()) {
  		  JCommButton doneButton = (JCommButton) brController.lookupWidgetByName("Done");
  		  if (doneButton!=null){
  			  //System.out.println(" Done button is clicked ");
  			  doneButton.setText(SimStPLE.DONE_CAPTION_ENABLED);
  		  }
  	  }
        
        if (!skillName.equals(SimSt.KILL_INTERACTIVE_LEARNING)){
      	
      	  // TODO: Need to figure out a unified way to handle all the interface actions at one place
  	      // Model-tracing the student interface action in response to the SimStudent help request
  	      if(brController.getMissController().getSimSt().isSsMetaTutorMode()) {
  	  	      getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("");
  	    	  brController.getAmt().handleInterfaceAction(saiAndSkillName.sai.getS(), saiAndSkillName.sai.getA(), saiAndSkillName.sai.getI());
  	      }
  	      	
      	  if(logger.getLoggingEnabled())
      	  {
  		      //hint = new AskHintInBuiltClAlgebraTutor(brController, parentNode);
  		      
//  		      boolean correct = false;
//  		      if(brController.getMissController().getSimSt().verifyStep(brController.getProblemModel().getProblemName(),
//  		    	parentNode, saiAndSkillName.sai.getS(), saiAndSkillName.sai.getA(), saiAndSkillName.sai.getI()).equals(EdgeData.CORRECT_ACTION))
//  		    	correct = true;
  		      
  		
  		      	int hintDuration = (int) ((Calendar.getInstance().getTimeInMillis() - hintRequestTime)/1000);
  		      	step = brController.getMissController().getSimSt().getProblemStepString();
  		      	
  		      	logger.simStLog(SimStLogger.SIM_STUDENT_INFO_RECEIVED, SimStLogger.HINT_RECEIVED, 
  		    		  step,"","",saiAndSkillName.sai,parentNode, hint.getSelection(),
  		    		  hint.getAction(), hint.getInput(), hintDuration,message);
      	  }
        		
            // ProblemModel pModel = brController.getProblemModel();
            // ProblemNode startNode = pModel.getStartNode();
            //this.node = brController.getCurrentNode();
            //this.edge = brController.getProblemModel().returnsEdge(parentNode,node);      
        }

        //this.node = brController.getCurrentNode();
        //this.edge = brController.getProblemModel().returnsEdge(parentNode,node); 
        if(saiAndSkillName != null && edge != null)
        {
      	  edge.getEdgeData().addRuleName(saiAndSkillName.skillName);
        }
    }
    
    public String[] getRetryMessage()
    {
    	String value;
        if(saiAndSkillName.sai.getI().indexOf(' ') > -1)
        	value = saiAndSkillName.sai.getI().substring(saiAndSkillName.sai.getI().indexOf(' ')+1);
        else
        	value = saiAndSkillName.sai.getI();
  
        String message[] = { "The step you suggest may be a correct one but it is taking me too long to calculate. Can you please give me an easier problem to work on?" };
        return message;
    }
    
    public void getRetry(BR_Controller brController, ProblemNode parentNode){
        
    	if(trace.getDebugCode("miss")) trace.out("miss", "getRetry in AskHintHumanOracle");
    	AskHint hint = null;
        String title = "SimStudent is confused";
        
        String message[] = getRetryMessage();
        
        String step = brController.getMissController().getSimSt().getProblemStepString();
        brController.getMissController().getSimSt().displayMessage(title, message);

  	    if(brController.getMissController() != null && brController.getMissController().getSimSt() != null
			  && brController.getMissController().getSimSt().isSsMetaTutorMode()) {
  	    	//brController.getAmt().handleInterfaceAction("sshint", "implicit", "-1");	
  	    	getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("hint-request");
  	    	
	    }
  	    
	  	long hintRequestTime = Calendar.getInstance().getTimeInMillis();
      	
	  	if(trace.getDebugCode("miss")) trace.out("miss","Calling waitForSaiAndSkillName");
        saiAndSkillName = waitForSaiAndSkillName(null);

        setSai(saiAndSkillName.sai);
        this.skillName = saiAndSkillName.skillName;     

        if (!skillName.equals(SimSt.KILL_INTERACTIVE_LEARNING)){

           	hint = new AskHintInBuiltClAlgebraTutor(brController, parentNode);
           	
        	/*boolean correct = false;
        	if(brController.getMissController().getSimSt().verifyStep(brController.getProblemModel().getProblemName(),
      		  parentNode, saiAndSkillName.sai.getS(), saiAndSkillName.sai.getA(), saiAndSkillName.sai.getI()).equals(EdgeData.CORRECT_ACTION))
      	  correct = true;*/

	      	int hintDuration = (int) ((Calendar.getInstance().getTimeInMillis() - hintRequestTime)/1000);
	      	
	      	step = brController.getMissController().getSimSt().getProblemStepString();

	      	
	      	int noInstructionsForSkill=this.brController.getMissController().getSimSt().getInstructionsFor(saiAndSkillName.skillName).size();
	      	String resultToLog=SimStLogger.INSTRUCTION_SIZE + " = " +  noInstructionsForSkill;   		      	
	      	
	     	/*10/07/2014: the result column now holds the number of instructions we have so far (for the specific skill)*/
        	logger.simStLog(SimStLogger.SIM_STUDENT_INFO_RECEIVED, SimStLogger.RETRY_RECEIVED, 
      		  step,resultToLog,"",saiAndSkillName.sai,parentNode, hint.getSelection(),
      		  hint.getAction(), hint.getInput(), hintDuration, message[0]);
        	
               
	        /*boolean correct = false;
	        if(brController.getMissController().getSimSt().verifyStep(brController.getProblemModel().getProblemName(),
	      		  parentNode, saiAndSkillName.sai.getS(), saiAndSkillName.sai.getA(), saiAndSkillName.sai.getI()).equals(EdgeData.CORRECT_ACTION))
	      	  correct = true;*/
	        
            // ProblemModel pModel = brController.getProblemModel();
            // ProblemNode startNode = pModel.getStartNode();
            this.node = brController.getCurrentNode();
            this.edge = brController.getProblemModel().returnsEdge(parentNode,node);
        }
      }
}
