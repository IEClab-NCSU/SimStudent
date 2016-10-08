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

public class GetAProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_A_PROBLEM = "get-a-problem";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetAProblem(){
		this(null);
	}
	
	public GetAProblem(ModelTracer amt){
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_A_PROBLEM)) {
			throw new JessException(GET_A_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				problemList = vv.get(1).resolveValue(context).stringValue(context);
			}
			
		}
		
		String[] token = problemList.split(":");
		if(token.length > 0) {
			problem = token[0];
			if(problem != SimStRete.NOT_SPECIFIED) {
				Value rtnValue = new Value(problem, RU.STRING);
				return rtnValue;
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return GET_A_PROBLEM;
	}

}
