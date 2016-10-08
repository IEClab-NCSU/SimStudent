package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.AskHintInBuiltClAlgebraTutor;
import edu.cmu.pact.miss.AskHintJessOracle;

/**
 * 
 */
public class GetCorrectSAI implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_CORRECT_SAI = "get-correct-sai";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/**	Link to the current variable Context, and thence to Rete. */
	protected transient Context context;
	
	/**
	 * No argument constructor.
	 */
	public GetCorrectSAI(){
		
	}
	
	/**
	 * Constructor with link to the Model Tracer.
	 * @param controller
	 */
	public GetCorrectSAI(ModelTracer amt) {
		this.amt = amt;
	}
	
	/**
	 * Gets the CL SAI for the current step and stores it in the RHS of the production
	 * rule. 
	 * @param vv argument list
	 * @param context Jess context for resolving values
	 * @return {@link jess.Funcall#FALSE} when the CL SAI is not set else {@link jess.Value}
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		
		StringBuffer sb = new StringBuffer(); 
		ProblemNode node = null;
		AskHint hint;
		
		this.context = context;
		if(!vv.get(0).stringValue(context).equals(GET_CORRECT_SAI))
			throw new JessException(GET_CORRECT_SAI, "called but ValueVector head differs", vv.get(0).stringValue(context));
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			
			if((amt != null) && (amt.getController() != null) && (amt.getController() instanceof BR_Controller)) {
				
			//if(vv.size() > 1) {
				//String currentNodeName = vv.get(1).resolveValue(context).stringValue(context);
				node = ((BR_Controller)amt.getController()).getCurrentNode();
				if(node != null) {
					
					
					//hint = new AskHintInBuiltClAlgebraTutor((BR_Controller)amt.getController(), node);
					//CL oracle must not be hardcoded! Whichever oracle grades the quiz should provide this hint
					hint = ((BR_Controller)amt.getController()).getMissController().getSimSt().askForHintQuizGradingOracle((BR_Controller)amt.getController(),node);
			//		hint = new AskHintJessOracle((BR_Controller)amt.getController(),node);
				
					
					
					if(!hint.getSelection().isEmpty()){
						sb.append(hint.getSelection()+",");
						
						if(!hint.getAction().isEmpty()){
							sb.append(hint.getAction()+",");
							if(!hint.getInput().isEmpty()){
								sb.append(hint.getInput());
								if(hint.getHintMsg() != null) {
									for(int i=0; i < hint.getHintMsg().length; i++) {
										if(i == 0) {
											sb.append(": " + hint.getHintMsg()[i]);
										} else {
											sb.append("; " + hint.getHintMsg()[i]);
										}
									}
								}	
							}
						}
					}
				}
			}
			//}
			if(sb != null && sb.length() > 0) {
				return new Value(sb.toString(), RU.STRING);
			} else {
				return Funcall.FALSE;
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return GET_CORRECT_SAI;
	}

}
