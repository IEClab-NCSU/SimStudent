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

public class RemoveProblemFromHeadOfList implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String REMOVE_PROBLEM_FROM_HEAD_OF_LIST = "remove-problem-from-head-of-list";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public RemoveProblemFromHeadOfList(){
		this(null);
	}
	
	public RemoveProblemFromHeadOfList(ModelTracer amt){
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(REMOVE_PROBLEM_FROM_HEAD_OF_LIST)) {
			throw new JessException(REMOVE_PROBLEM_FROM_HEAD_OF_LIST, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				problemList = vv.get(1).resolveValue(context).stringValue(context);
				
				if(vv.size() > 2) {
					problem = vv.get(2).resolveValue(context).stringValue(context);
				}
			}
		}
		
		if(amt == null) {
			if(context.getEngine() instanceof SimStRete) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
		}

		if(amt != null && amt.getController() != null /*simSt.getBrController() != null*/) {
			
			if(problemList != SimStRete.NOT_SPECIFIED && problem != SimStRete.NOT_SPECIFIED) {
				
				String newProblemList = "";
				String tokens[] = problemList.split(":");

				for(int i = 0; i < tokens.length && i < 1; i++) {
					
					String currentP = tokens[i];
					String splitCurrentP[] = currentP.split("=");
					if(splitCurrentP.length == 2)
						currentP =  " " + splitCurrentP[0] + "=" + splitCurrentP[1] + " "; // conform to minerva input problem spec
					Problem prob = new Problem(currentP);
					String absProb = prob.getSignedAbstraction();
					absProb = GetAbstractedProblem.formatProblem(absProb); // convert Nv+N=N to type Av+B=C
					
					if(problem.equalsIgnoreCase(absProb)) {
						
						for(int j = 1; j < tokens.length; j++)
							newProblemList += tokens[j] + ":";
					}

					return new Value(newProblemList, RU.STRING);
				}
				
				return new Value(problemList, RU.STRING);
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return REMOVE_PROBLEM_FROM_HEAD_OF_LIST;
	}
}
