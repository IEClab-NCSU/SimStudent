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
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class GetAbstractedProblem implements Userfunction, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String GET_ABSTRACTED_PROBLEM = "get-abstracted-problem";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetAbstractedProblem(){
		this(null);
	}
	
	public GetAbstractedProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {

		if(!vv.get(0).stringValue(context).equals(GET_ABSTRACTED_PROBLEM)) {
			throw new JessException(GET_ABSTRACTED_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				problem = vv.get(1).resolveValue(context).stringValue(context);
			}
		}
		
		if(problem != SimStRete.NOT_SPECIFIED) {
			
			String[] token = problem.split("=");
			if(token.length == 2) {
				problem = " " + token[0] + "=" + token[1] + " ";
			} else {
				return Funcall.FALSE;
			}
			
			Problem minervaProblem;
			minervaProblem = new Problem(problem);
			String abstractedProblemWithSign = minervaProblem.getSignedAbstraction();
			abstractedProblemWithSign = formatProblem(abstractedProblemWithSign);

			Value rtnValue = new Value(abstractedProblemWithSign, RU.STRING);
			return rtnValue;
		}
		
		return Funcall.FALSE;
	}

	/**
	 * Utility method to abstract the problem using the minerva package
	 * @param inputP
	 * @return
	 */
	protected static String abstractProblemUsingMinerva(String inputP) {
		
		Problem minervaProblem;
		minervaProblem = new Problem(inputP);
		return minervaProblem.getSignedAbstraction();
	}
	
	/**
	 * Utility method to format the problem from Nv+N=N to form Av+B=C
	 * @param problem
	 * @return
	 */
	protected static String formatProblem(String abstractedProblemWithSign) {
		
		String prob = "";
		char nextConstant = 'A';
		char varUpper = 'V';
		char varLower = 'v';
		
		for(int i=0; i< abstractedProblemWithSign.length(); i++) {
			char current = abstractedProblemWithSign.charAt(i);
			if(current == varUpper || current == varLower) {
				prob += current;
				continue;
			}
			
			if(Character.isLetter(current)) {
				prob += nextConstant++;
			} else {
				prob += current;
			}
		}
		return prob;

	}
	
	@Override
	public String getName() {
		return GET_ABSTRACTED_PROBLEM;
	}

}
