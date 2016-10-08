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

public class GetSecondQuizFailedProblem implements Userfunction, Serializable {

	private static final String GET_SECOND_QUIZ_FAILED_PROBLEM = "get-second-quiz-failed-problem";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetSecondQuizFailedProblem(){
		this(null);
	}
	
	public GetSecondQuizFailedProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_SECOND_QUIZ_FAILED_PROBLEM)) {
			throw new JessException(GET_SECOND_QUIZ_FAILED_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		String secondProblem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				problemList = vv.get(1).resolveValue(context).stringValue(context);
			}
		}
		
		if(problemList != SimStRete.NOT_SPECIFIED) {
			String[] list = problemList.split(":");
			if(list.length > 1) {
				secondProblem = list[1];
				if(secondProblem != SimStRete.NOT_SPECIFIED) {
					Value rtnValue = new Value(secondProblem, RU.STRING);
					return rtnValue;
				}
			}
		} else {
			return Funcall.FALSE;
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return GET_SECOND_QUIZ_FAILED_PROBLEM;
	}

}
