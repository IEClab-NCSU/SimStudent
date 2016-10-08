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
import edu.cmu.pact.jess.SimStRete;

public class GetFailedQuizItems implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_FAILED_QUIZ_ITEMS = "get-failed-quiz-items";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetFailedQuizItems() {
		this(null);
	}
	
	public GetFailedQuizItems(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_FAILED_QUIZ_ITEMS)) {
			throw new JessException(GET_FAILED_QUIZ_ITEMS, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String failedPList = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
				
		/*	if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			
			if((amt != null) && (amt.getController() != null) && (amt.getController() instanceof BR_Controller)) {
				amt.getController().getMissController().getSimSt().getModelTraceWM().quizFailedProblemsList=null;
				amt.getController().getMissController().getSimSt().getModelTraceWM().quizFailedProblemsList=new String[vv.size()];	
			}
		*/	
			
			if(vv.size() > 1) {
				String input = vv.get(1).resolveValue(context).stringValue(context);
				
				String[] token = input.split(":");
				if(token.length >= 2) {
					failedPList = "";
					for(int i=1; i < token.length; i++) {
						
					//	amt.getController().getMissController().getSimSt().getModelTraceWM().quizFailedProblemsList[i-1]=token[i];
						
						failedPList = failedPList + token[i] + ":";
					}
				}
				
				if(failedPList != SimStRete.NOT_SPECIFIED) {
					Value rtnValue = new Value(failedPList, RU.STRING);
					return rtnValue;
				}
			}
		}
		
		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_FAILED_QUIZ_ITEMS;
	}
}
