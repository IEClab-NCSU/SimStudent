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

public class GetFirstProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	private static final String GET_FIRST_PROBLEM = "get-first-problem";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetFirstProblem() {
		this(null);
	}
	
	public GetFirstProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_FIRST_PROBLEM)) {
			throw new JessException(GET_FIRST_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		String firstProblem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				problemList = vv.get(1).resolveValue(context).stringValue(context);
			}
		}
		
		if(problemList != SimStRete.NOT_SPECIFIED) {
			String[] list = problemList.split(":");
			if(list.length > 0) {
				firstProblem = list[0];
				if(firstProblem != SimStRete.NOT_SPECIFIED) {
					Value rtnValue = new Value(formatProblem(firstProblem), RU.STRING);
					return rtnValue;
				}
			}
		} else {
			return Funcall.FALSE;
		}
		
		return Funcall.FALSE;
	}

	private String formatProblem(String problem) {
		
		String[] token = problem.split("=");
		if(token.length == 2 && token[0].trim().length() > 0 && token[1].trim().length() > 0) {
	
			problem = token[0].trim() + "  =  " +  token[1].trim();
		}
		
		return problem;
	}

	@Override
	public String getName() {
		return GET_FIRST_PROBLEM;
	}

}
