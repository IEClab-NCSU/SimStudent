package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class IsProblemSubsetOfList implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String IS_PROBLEM_SUBSET_OF_LIST = "is-problem-subset-of-list";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public IsProblemSubsetOfList(){
		this(null);
	}
	
	public IsProblemSubsetOfList(ModelTracer amt){
		this.amt = amt;
	}

	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(IS_PROBLEM_SUBSET_OF_LIST)) {
			throw new JessException(IS_PROBLEM_SUBSET_OF_LIST, "called but ValueVector head differs", vv.get(0).stringValue(context));
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
				
				String tokens[] = problemList.split(":");
				for(int i = 0; i < tokens.length; i++) {
					
					String currentP = tokens[i];
					String splitCurrentP[] = currentP.split("=");
					if(splitCurrentP.length == 2)
						currentP =  " " + splitCurrentP[0] + "=" + splitCurrentP[1] + " "; // conform to minerva input problem spec
					Problem prob = new Problem(currentP);
					String absProb = prob.getSignedAbstraction();
					absProb = GetAbstractedProblem.formatProblem(absProb); // convert Nv+N=N to type Av+B=C
					
					if(problem.equalsIgnoreCase(absProb)) {
						
						return Funcall.TRUE;
					}
				}
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return IS_PROBLEM_SUBSET_OF_LIST;
	}

}
