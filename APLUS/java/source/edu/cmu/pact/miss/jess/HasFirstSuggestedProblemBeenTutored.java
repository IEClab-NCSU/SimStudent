package edu.cmu.pact.miss.jess;

import java.io.Serializable;
import java.util.HashMap;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class HasFirstSuggestedProblemBeenTutored implements Userfunction,
		Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	private static final String HAS_FIRST_SUGGESTED_PROBLEM_BEEN_TUTORED = "has-first-suggested-problem-been-tutored";
	
	/**	Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/**	Link to current variable context and, thence, to the Rete. */
	protected transient Context context;
	
	public HasFirstSuggestedProblemBeenTutored() {
		this(null);
	}
	
	public HasFirstSuggestedProblemBeenTutored(ModelTracer amt){
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(HAS_FIRST_SUGGESTED_PROBLEM_BEEN_TUTORED)) {
			throw new JessException(HAS_FIRST_SUGGESTED_PROBLEM_BEEN_TUTORED, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String suggestedProblem = SimStRete.NOT_SPECIFIED;
		HashMap<String, Integer> abstractProblemCount = null;
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				
				suggestedProblem = vv.get(1).resolveValue(context).stringValue(context);
				
				if(vv.size() > 2) {
					
					abstractProblemCount = (HashMap) vv.get(2).resolveValue(context).javaObjectValue(context);
				}
			}
		}
		
		if(suggestedProblem != SimStRete.NOT_SPECIFIED && abstractProblemCount != null) {
			
			String suggestedPAbstracted = abstractProblem(suggestedProblem);
			Integer count = abstractProblemCount.get(suggestedPAbstracted);

			if(count != null && count.intValue() > 0) {
				
				return Funcall.TRUE;
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		
		return HAS_FIRST_SUGGESTED_PROBLEM_BEEN_TUTORED;
	}

	private String abstractProblem(String inputP) {
		
		String abstractedP = "";
		String[] sidesP = inputP.split("=");

		if (sidesP.length == 2) {
			inputP = " " + sidesP[0] + "=" + sidesP[1] + " ";
		} else {
			return null;
		}

		abstractedP = new Problem(inputP).getSignedAbstraction();
		return abstractedP;
	}
}
