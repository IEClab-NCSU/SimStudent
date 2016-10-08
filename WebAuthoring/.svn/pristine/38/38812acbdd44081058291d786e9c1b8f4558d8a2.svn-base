package interaction;

import java.util.HashMap;
import java.util.Map.Entry;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.InquiryClSolverTutor;
import edu.cmu.pact.miss.Sai;

/**
 * Oracle that inquiries if SimStudent suggestions are correct or not. 
 * Because we cannot have polling (i.e. display SAI and poll to see if yes/no was clicked (which is what
 * happens for Java), a different approach was followed. Unlike Java, where author is asked about each Sai one
 * at a time, here we first ask author and then invoke the inspectAgendaRuleActivations method of SimStudent. In detail, 
 * what happens is: 
 * 1. SimStudent returns a list of suggestions
 * 2. These suggestions are displayed one by one to author, and they are classified as correct/incorrect. Sai -> correctness is saved 
 *    in a hash named simStTruthTable
 * 3. This  hash is passed to the constructor of the oracle. Thus, when the "isCorrectStep" of the Oracle is invoked, it just
 *    retrieves the correctness of the passed SAI. 
 *    
 * @author nbarba
 *
 */
public class InquiryWebAuthoring extends InquiryClSolverTutor{

	HashMap <Sai,String> simStAgendaTurth=null;
	public InquiryWebAuthoring(HashMap <Sai,String> truthTable){
		this.simStAgendaTurth=truthTable;
     
        
	}
	
	
	public boolean isCorrectStep(String selection, String action, String input){
		Sai test=new Sai(selection, action, input);
		
		for (Entry<Sai, String> entry : simStAgendaTurth.entrySet()) {
			Sai key = entry.getKey();
		    String value = entry.getValue();
		   
		    if (key.getS().equals(selection) && key.getA().equals(action) && key.getI().equals(input)){
		    	if (value.equals(EdgeData.CORRECT_ACTION)) return true;
		    	else return false;
		    }
  
		}
		
		
		String response=simStAgendaTurth.get(test);
		
	
		if (simStAgendaTurth.get(test).equals(EdgeData.CORRECT_ACTION)) return true;
		else return false;
		
		
	}
	
	
}
