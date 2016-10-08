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

public class GetProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private static final String GET_PROBLEM = "get-problem";
	
	protected transient ModelTracer amt;
	
	protected transient Context context;
	
	public GetProblem() {
		this(null);
	}
	
	public GetProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_PROBLEM)) {
			throw new JessException(GET_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String lhs = SimStRete.NOT_SPECIFIED;
		String rhs = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				lhs = vv.get(1).resolveValue(context).stringValue(context);
				if(vv.size() > 2) {
					
					rhs = vv.get(2).resolveValue(context).stringValue(context);
				}
			}
		}

		if(lhs != SimStRete.NOT_SPECIFIED && rhs != SimStRete.NOT_SPECIFIED && lhs.length() > 0
				&& rhs.length() > 0) {
			
			String problem = lhs + "=" + rhs;
			Value rtnValue = new Value(problem, RU.STRING);
			return rtnValue;
		}
		
		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_PROBLEM;
	}

}
