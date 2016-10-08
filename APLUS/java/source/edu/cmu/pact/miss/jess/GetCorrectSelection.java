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
import edu.cmu.pact.miss.AlgebraProblemAssessor;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.AskHintInBuiltClAlgebraTutor;
import edu.cmu.pact.miss.ProblemAssessor;

/**
 * 
 */
public class GetCorrectSelection implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_CORRECT_SELECTION = "get-selection-name";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/**	Link to the current variable Context, and thence to Rete. */
	protected transient Context context;
	
	/**
	 * No argument constructor.
	 */
	public GetCorrectSelection(){
		
	}
	
	/**
	 * Constructor with link to the Model Tracer.
	 * @param controller
	 */
	public GetCorrectSelection(ModelTracer amt) {
		this.amt = amt;
	}
	
	/**
	 * Gets the correct SAI for the current step and returns the selection (formated using the description in SimStPLE).
	 * @param vv argument list
	 * @param context Jess context for resolving values
	 * @return {@link jess.Funcall#FALSE} when the CL SAI is not set else {@link jess.Value}
	 */
	public Value call(ValueVector vv, Context context) throws JessException {

		if(!vv.get(0).stringValue(context).equals(GET_CORRECT_SELECTION)) {
			throw new JessException(GET_CORRECT_SELECTION, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String name = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				name = vv.get(1).resolveValue(context).stringValue(context);
			}
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			
			
			
		}


		if (name.equals("nil")) return Funcall.NIL;
	
		if(name.length() > 0) {

			String prettyName="";
			if (((BR_Controller)amt.getController()).getMissController().isPLEon()){
			
				prettyName=((BR_Controller)amt.getController()).getMissController().getSimStPLE().getComponentName(name);
			}
			

			if (prettyName.length()>4)
				prettyName=prettyName.replace("for", "");
			
			
			if (prettyName.contains("transmation"))
				prettyName="transformation";
			
			
			
			Value rtnValue = new Value(prettyName, RU.STRING);
			return rtnValue;
			
			
			
		}
		
		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_CORRECT_SELECTION;
	}

}
