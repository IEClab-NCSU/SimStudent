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

public class GetFailedQuizProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_FAILED_QUIZ_PROBLEM = "get-failed-quiz-problem";
	
	/**	Link to current variable context and, thence, to the Rete. */
	protected transient Context context;
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		this.context = context;
		
		if(vv.get(0).stringValue(context).equals(GET_FAILED_QUIZ_PROBLEM)){
			throw new JessException(GET_FAILED_QUIZ_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				problemList = vv.get(1).resolveValue(context).stringValue(context);
			}
		}
		
		if(problemList != SimStRete.NOT_SPECIFIED) {
			String[] failedProblemList = problemList.split(":");
			if(failedProblemList.length > 0) {
				for(int i=0; i< failedProblemList.length; i++) {
					String problemToTutor = failedProblemList[i]; // Get the first failed problem and use it as the problem to tutor
					return new Value(problemToTutor, RU.STRING);
				}
			} else {
				return Funcall.FALSE;
			}
		}
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return GET_FAILED_QUIZ_PROBLEM;
	}
}
