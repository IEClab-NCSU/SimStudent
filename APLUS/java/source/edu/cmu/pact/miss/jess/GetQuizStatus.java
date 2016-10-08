package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;

public class GetQuizStatus implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_QUIZ_STATUS = "get-quiz-status";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetQuizStatus() {
		this(null);
	}
	
	public GetQuizStatus(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_QUIZ_STATUS)) {
			throw new JessException(GET_QUIZ_STATUS, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String quizStatus = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				String input = vv.get(1).resolveValue(context).stringValue(context);
				String[] token = input.split(":");
				if(token.length > 1) {
					quizStatus = token[0];
				} else {
					quizStatus = input;
				}
				
				if(quizStatus != SimStRete.NOT_SPECIFIED) {
					Value rtnValue = new Value(quizStatus, RU.STRING);
					return rtnValue;
				}
			}
		}
		
		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_QUIZ_STATUS;
	}

}
