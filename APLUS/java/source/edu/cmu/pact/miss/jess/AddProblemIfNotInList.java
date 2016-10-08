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

public class AddProblemIfNotInList implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String ADD_PROBLEM_IF_NOT_IN_LIST = "add-problem-if-not-in-list";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public AddProblemIfNotInList(){
		this(null);
	}
	
	public AddProblemIfNotInList(ModelTracer amt){
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(ADD_PROBLEM_IF_NOT_IN_LIST)) {
			throw new JessException(ADD_PROBLEM_IF_NOT_IN_LIST, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				problemList = vv.get(1).resolveValue(context).stringValue(context);
				
				if(vv.size() > 2) {
					problem = vv.get(2).resolveValue(context).stringValue(context);
					String temp[] = problem.split("=");
					if(temp.length == 2)
						problem = " " + temp[0] + "=" + temp[1] + " ";
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
				
				String abstractedStudentP = GetAbstractedProblem.abstractProblemUsingMinerva(problem);
				abstractedStudentP = GetAbstractedProblem.formatProblem(abstractedStudentP);
				String tokens[] = problemList.split(":");
				boolean found = false;
				
				for(int i = 0; i < tokens.length; i++) {
					
					String currentP = tokens[i];
					String splitCurrentP[] = currentP.split("=");
					if(splitCurrentP.length == 2)
						currentP =  " " + splitCurrentP[0] + "=" + splitCurrentP[1] + " "; // conform to minerva input problem spec

					String abstractedTokenP = GetAbstractedProblem.abstractProblemUsingMinerva(currentP);
					abstractedTokenP = GetAbstractedProblem.formatProblem(abstractedTokenP); // convert Nv+N=N to type Av+B=C
					
					
					if(abstractedStudentP.equalsIgnoreCase(abstractedTokenP)) {
						found = true;
						break;
					}
				}

				if(!found)
					problemList = problem.trim()+":"+problemList;
				return new Value(problemList, RU.STRING);
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return ADD_PROBLEM_IF_NOT_IN_LIST;
	}
}
