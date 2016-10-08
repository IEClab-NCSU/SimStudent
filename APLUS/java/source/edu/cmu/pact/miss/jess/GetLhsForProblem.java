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

public class GetLhsForProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private static final String GET_LHS_FOR_PROBLEM = "get-lhs";
	
	protected transient ModelTracer amt;
	
	protected transient Context context;
	
	public GetLhsForProblem() {
		this(null);
	}
	
	public GetLhsForProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_LHS_FOR_PROBLEM)) {
			throw new JessException(GET_LHS_FOR_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				problem = vv.get(1).resolveValue(context).stringValue(context);
			}
		}

		if(problem.length() > 0 && problem != SimStRete.NOT_SPECIFIED) {

			if (problem.contains("=")){
				String[] problemSides = problem.split("=");
			
				if(problemSides.length > 1) {
					String lhs = problemSides[0].trim();
					Value rtnValue = new Value(lhs, RU.STRING);
					return rtnValue;
				}
			}
			else {
				String[] problemSides = problem.split("\\+");
				
				if(problemSides.length > 1) {
					String lhs = problemSides[0].trim();
					Value rtnValue = new Value(lhs, RU.STRING);
					return rtnValue;
				}
				
			}
			
		}
		
		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_LHS_FOR_PROBLEM;
	}

}
