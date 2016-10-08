package edu.cmu.pact.jess;

import java.util.Vector;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.BKT.BKT;

/**
 * Sim Student augmentation of {@link JessModelTracing} that includes the BKT.  
 * @author nbarba
 */
public class SimStJessModelTracingBKT extends SimStJessModelTracing{
	
	/*BKT object*/
	BKT bkt=null;
	void setBKT(BKT bkt){this.bkt=bkt;}
	public BKT getBKT(){return this.bkt;}
	SimSt simSt=null;
	
	/**
	 * Returns the superclass constructor result {@link JessModelTracing#JessModelTracing(MTRete, CTAT_Controller)}.
	 * @param r
	 * @param controller
	 */
	public SimStJessModelTracingBKT(MTRete r, CTAT_Controller controller) {
		
		super(r, controller);
		
		/*construct the BKT*/
		bkt=new BKT(controller.getMissController().getSimSt());
		simSt=controller.getMissController().getSimSt();
	}
	
	/**
	 * After modeltracing update the BKT parameters
	 */
	public int modelTrace(boolean isHint, String selection, String action, String input, Vector<String> msgs){
		int returnValue=super.modelTrace(isHint, selection, action, input, msgs);
		
		int correctness=(returnValue==JessModelTracing.CORRECT)? 1 : 0;
		
		String skillName=bkt.getSimSt().getBrController().getModelTracer().getJMT().getRuleModeltraced().getName();
		
		if (correctness==0)
			simSt.getMissController().getSimStPLE().getSsCognitiveTutor().setPreviousIncorrectStepExists(false);
		else{
			/*BKT is updated only when step is completed, based on the first attempt.*/
			correctness = 	(simSt.getMissController().getSimStPLE().getSsCognitiveTutor().getPreviousIncorrectStepExists() || 	simSt.getMissController().getSimStPLE().getSsCognitiveTutor().getStepHintGiven()) ? 0:1;
			skillName = simSt.getSkillNameGetter().getPreviousSkill();
			
			bkt.update(correctness, skillName);
			bkt.saveBKTParametersFile();
		}
			
			
	
		
		return returnValue;
	}
	
}
