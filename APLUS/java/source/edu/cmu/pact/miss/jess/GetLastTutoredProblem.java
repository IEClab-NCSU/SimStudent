package edu.cmu.pact.miss.jess;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class GetLastTutoredProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	private static final String GET_LAST_TUTORED_PROBLEM = "get-last-tutored-problem";
	
	
	/**	 */
	protected transient ModelTracer amt;
	
	/**	 */
	protected transient Context context;
	
	/**	 */
	private static String lastAbstractedProblem;
	private static String lastSimilarProblem;
	private static int lastAbstractedProblemSolvedCount;
	
	public GetLastTutoredProblem() {
		this(null);
	}
	
	public GetLastTutoredProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
	
		if(!vv.get(0).stringValue(context).equals(GET_LAST_TUTORED_PROBLEM)) {
			throw new JessException(GET_LAST_TUTORED_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			///if(vv.size() > 1) {
				
			//	problem = vv.get(1).resolveValue(context).stringValue(context);
			//}
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			
			
			//System.out.println(amt.getController().getMissController().getSimSt().getModelTraceWM().allTutoredProblemList);
			String lastP=ModelTraceWorkingMemory.allTutoredProblemList.peek();
			
			return new Value(lastP, RU.STRING);
			
			
			
			
		}

		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_LAST_TUTORED_PROBLEM;
	}


}
