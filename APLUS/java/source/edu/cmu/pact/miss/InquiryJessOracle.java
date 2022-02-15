package edu.cmu.pact.miss;

import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import cl.tutors.solver.SolverTutor;
import jess.JessException;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.JessModelTracing;
import edu.cmu.pact.jess.JessOracleRete;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.jess.RuleActivationTree.TreeTableModel;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStEdge;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;

public class InquiryJessOracle extends InquiryClSolverTutor{

	/********************************
	 * Variables, getters and setters
	 ********************************/
	JessOracleRete oracleRete;
	private void setJessOracleRete(JessOracleRete oracleRete) {
			this.oracleRete = oracleRete;
	}
	public JessOracleRete getJessOracleRete() {
			return oracleRete;
	}
	
	BR_Controller brController=null;
	private void setBrController(BR_Controller brController){
		this.brController=brController;
	}
	private BR_Controller getBrController(){
		return this.brController;
	}
		
	SimSt simSt;
	private void setSimSt(SimSt simSt){
		this.simSt=simSt;
	}	
	private SimSt getSimSt(){	
		return this.simSt;
	}
	
	ProblemNode currentNode;
	private void setCurrentNode(ProblemNode currentNode){
		this.currentNode=currentNode;
	}
	private ProblemNode getCurrentNode(){
		return this.currentNode;
	}
	
	private JessOracle jessOracle;
	
	public JessOracle getJessOracle() { 
		return this.jessOracle; 
	}
	public void setJessOracle(JessOracle jessOracle) {
		this.jessOracle = jessOracle;
	}
	
	RuleActivationNode nextStepRuleActivationNode=null;
	
	void setNextStepRuleActivationNode(RuleActivationNode nextStepRuleActivationNode){
		this.nextStepRuleActivationNode=nextStepRuleActivationNode;	
	}
	
	RuleActivationNode getNextStepRuleActivationNode(){
		return this.nextStepRuleActivationNode;	
	}
	
	boolean isAgendaEmpty=true;
	private void setIsAgendaEmpty(boolean flag){ this.isAgendaEmpty=flag;}
	public boolean getIsAgendaEmpty(){return this.isAgendaEmpty; } 
	
	
	public Vector <RuleActivationNode> altSug=null;
	
	/************************
	 * Constructors
	 ************************/
	public InquiryJessOracle(SimSt simSt, BR_Controller brController){		
			setJessOracleRete(new JessOracleRete(brController));
			setSimSt(simSt);
			this.setBrController(brController);
			altSug = new Vector<RuleActivationNode>();
	}
	
	
	boolean filesloaded=false;
	/************************
	 * High level functions
	 ************************/
	
	/* Initialize JessOracle*/
	public void init(String problemName){

		
				
		try {
			//if (filesloaded==false){
				oracleRete.init(problemName, true);
			//	filesloaded=true;
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}	
	
	
	public Sai askNexStep(){
		altSug.clear();
		Vector<RuleActivationNode> oracleAgenda= getJessOracleAgenda();	
		
		oracleAgenda=removeDuplicateSai(oracleAgenda);
		if (oracleAgenda.size()==0) return null;	//if JessOracle had no production rule to fire, return null
       
		altSug=oracleAgenda;
		
		this.setNextStepRuleActivationNode(oracleAgenda.get(0));
		trace.err("*** firing Jess oracle rule is " + oracleAgenda.get(0).getName());
		trace.err("*** entire Jess oracle agenda is " + oracleAgenda);
       	String sel=oracleAgenda.get(0).getActualSelection();
		String act=oracleAgenda.get(0).getActualAction();
		String inp=oracleAgenda.get(0).getActualInput();
		String ruleName = oracleAgenda.get(0).getName().replace("MAIN::", "");
		
		int index = ruleName.indexOf('&');
		if(index > -1) ruleName = ruleName.substring(0, index);

		Sai hintSai= new Sai(sel,act,inp, ruleName);
		return hintSai;
       
	}
	

	
	/*  High level function that checks if a sai is correct. */
	public boolean isCorrectStep(String selection, String action, String input){
		
		boolean isCorrectStep=false;
		
		Vector<RuleActivationNode> oracleAgenda=getJessOracleAgenda();	
		Vector<Sai> suggestions= new Vector<Sai>();
		   
		if (oracleAgenda.isEmpty())
				this.setIsAgendaEmpty(true);
		
	    for(RuleActivationNode ran: oracleAgenda){		
	    	   Sai temp = new Sai(ran.getActualSelection(),ran.getActualAction(),ran.getActualInput());
 	   
	    	   if (simSt.isStepModelTraced(selection,  action, input, temp.getS(), temp.getA(), temp.getI())){
	    		   simSt.setSaiCache(new Sai(selection,action,input));
	    		   isCorrectStep=true;
	    	   }
	    	   
	    }
 
	    return isCorrectStep;
	       
		
	}
	
	
	public String[] getHintMessages(){
		if (getNextStepRuleActivationNode()==null) 
			return null;
		else 
			return getHintMessages(getNextStepRuleActivationNode());
	}
	
	
	/************************
	 * Help functions
	 ************************/
	
	/**
	 * This calls Jess Oracle and asks for the hint messages associated with the current / next step.
	 * @param selection
	 * @param action
	 * @param input
	 * @return An array of hint messages with each message having a hierarchial description
	 * of what step should be done.
	 */
	private String[] getHintMessages(RuleActivationNode ran){
		
		Vector<String> hintMessages=ran.getHintMessages();
		
		for (int i=0;i<hintMessages.size(); i++){
			//String actualMessage=convertInterfaceElementNames(hintMessages.get(i));
			String actualMessage=hintMessages.get(i);
			if (brController.getMissController().getSimStPLE()!=null)
				actualMessage=brController.getMissController().getSimStPLE().messageComposer(actualMessage, null, null, null);
			
			hintMessages.set(i, actualMessage);
			
			
		}
				
			
		
	
		return hintMessages.toArray(new String[hintMessages.size()]);
	
	}
	
	private String convertInterfaceElementNames(String msg){
		
		String[] words = msg.split(" ");
		String newMsg="";
		for (String word:words){		
			String convertedComponentName=brController.getMissController().getSimStPLE().getComponentName(word);		
			if (convertedComponentName!=null) word=convertedComponentName;	
			newMsg=newMsg + word + " ";
		}

		return newMsg;

		
	}
	
	
	
	/* Function that returns the agenda of the Jess oracle.*/ 
	public Vector<RuleActivationNode> getJessOracleAgenda(){
		
		Vector <RuleActivationNode> activationList = new Vector<RuleActivationNode>();		
		try{
			RuleActivationTree tree = brController.getRuleActivationTree();
			TreeTableModel ttm = tree.getActivationModel();
			RuleActivationNode root = (RuleActivationNode) ttm.getRoot();

			root.saveState(oracleRete);

			List wholeAgenda = oracleRete.getAgendaAsList(null);
    	 	
			root.createChildren(wholeAgenda, false);
			List children = root.getChildren();
			JessModelTracing jmt = oracleRete.getJmt();
    	

			for(int i=0; i< children.size(); i++) {
				RuleActivationNode child = (RuleActivationNode)children.get(i);
				root.setUpState(oracleRete, i);
				jmt.setNodeNowFiring(child);
				child.fire(oracleRete);
				jmt.setNodeNowFiring(null);					
				activationList.add(child);
			}
    	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
    	 activationList = removeDuplicateSai(activationList);      
         return activationList;
		
	}
	
	
	
	/* Function that moves SimStudent to a specific state, specified by problem node.*/
	public int goToState(BR_Controller  brController, ProblemNode problemNode){
				

		setCurrentNode(problemNode);
		setBrController(brController);
		try{	
			if ((getSimSt().getSsInteractiveLearning()!=null && getSimSt().getSsInteractiveLearning().isTakingQuiz()) || 
				(getSimSt().isSsAplusCtrlCogTutorMode() && brController.getMissController().getSimStPLE().getSsCognitiveTutor().isTakingQuiz)	){ 		   
						
						SimStNode currentNode=getSimSt().getcurrentSsNode();
  	   	   	
						if (currentNode!=null){
							if(currentNode.getParents().isEmpty()) {

								oracleRete.reset();
								oracleRete.restoreInitialWMState(currentNode, true);

							} else {

								SimStNode startNode = currentNode.getProblemGraph().getStartNode();
								Vector<SimStEdge> vec = currentNode.findPathToNode(currentNode);
								oracleRete.reset();
								oracleRete.goToWMState(startNode ,vec, true);

							}
						}
					}
			else{   	   	  	  
				if (problemNode!=null){
						if(problemNode.getParents().isEmpty()) {
							//oracleRete.reset();
							//oracleRete.restoreInitialWMState(currentNode, true);
					} else {        			
									 
        			 		if (simSt.isValidationMode()){
        			 			SimStNode startNode=getSimSt().getValidationGraph().getStartNode();  
        			 			//System.out.println(" Validation : "+startNode);
        			 			Vector<ProblemEdge> vec = findPathDepthFirst(startNode,problemNode); 
        			 			oracleRete.reset();	
        			 			oracleRete.goToWMState(startNode ,vec, true);
        			 		}
        			 		else{
        			 			ProblemNode startNode = brController.getProblemModel().getStartNode(); 
        			 			//System.out.println(" Start Node : "+startNode);
        			 			Vector<ProblemEdge> vec = findPathDepthFirst(startNode,problemNode); 
        			 			oracleRete.reset();
        			 			oracleRete.goToWMState(startNode ,vec, true);

        			 		}
        			 	 				 
					}
				}
  	   	
  	   		}
		} catch (JessException e) {
		// TODO Auto-generated catch block
				
			e.printStackTrace();
	}
		return 0;
	}
	
	
	
	
	
	
	/**
	    * Returns a new vector without any of the SAI's duplicated. Does not affect the original activation list
	    * @param activationList, containing a vector of activations for the current node
	    * @return Vector<RuleActivationNode>, filtered list with the duplicate activations removed
	 */
	   Vector<RuleActivationNode> removeDuplicateSai(Vector<RuleActivationNode> activationList)
	   {
	   	Vector<Sai> goodSais = new Vector<Sai>();
	   	Vector<RuleActivationNode> goodActivationList = new Vector<RuleActivationNode>();
	   	for(RuleActivationNode ran:activationList)
	   	{
	   		
	   		Sai sai = new Sai(ran.getActualSelection(), ran.getActualAction(),ran.getActualInput());
	   		if(!goodSais.contains(sai))
	   		{
	   			goodSais.add(sai);
	   			goodActivationList.add(ran);
	   		}
	   	}
	   	return goodActivationList;
	   }
	      
}
