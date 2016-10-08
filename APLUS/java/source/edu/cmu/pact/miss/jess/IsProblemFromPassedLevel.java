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
import edu.cmu.pact.miss.AlgebraProblemAssessor;
import edu.cmu.pact.miss.ProblemAssessor;
import edu.cmu.pact.miss.jess.ModelTracer;

public class IsProblemFromPassedLevel implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private static final String IS_PROBLEM_FROM_PASSED_LEVEL = "is-problem-from-passed-level";
	
	protected transient ModelTracer amt;
	
	protected transient Context context;
	
	public IsProblemFromPassedLevel() {
		this(null);
	}
	
	public IsProblemFromPassedLevel(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(IS_PROBLEM_FROM_PASSED_LEVEL)) {
			throw new JessException(IS_PROBLEM_FROM_PASSED_LEVEL, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		Value returnVal=Funcall.FALSE;
		int problemLevel=-1;
		int currentSection=-1;
		int quizLevel=-1;
		if(context.getEngine() instanceof SimStRete) {
			
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			String lhs=SimStRete.NOT_SPECIFIED;
			String rhs=SimStRete.NOT_SPECIFIED;
			
			if(vv.size() > 1) {
				lhs = vv.get(1).resolveValue(context).stringValue(context);
				rhs = vv.get(2).resolveValue(context).stringValue(context);
				quizLevel = vv.get(3).resolveValue(context).intValue(context);
			}
				
			if (lhs != SimStRete.NOT_SPECIFIED && rhs != SimStRete.NOT_SPECIFIED)
				problem=lhs + "=" + rhs;
			
			if (problem != SimStRete.NOT_SPECIFIED){
				
				ProblemAssessor assessor = new AlgebraProblemAssessor();
				String problemType=assessor.classifyProblem(problem);
				if (problemType.equalsIgnoreCase(AlgebraProblemAssessor.ONE_STEP_EQUATION)){
					problemLevel=0;
				}
				else if (problemType.equalsIgnoreCase(AlgebraProblemAssessor.TWO_STEP_EQUATION)){
					problemLevel=1;
				}
				else if (problemType.equalsIgnoreCase(AlgebraProblemAssessor.BOTH_SIDES_EQUATION)){
					problemLevel=2;
				}
				else returnVal=Funcall.FALSE;
				
				
				if (problemLevel < currentSection)
					returnVal=Funcall.TRUE;
				
			
			}
			
			
	
		}
	
		return returnVal;
	}

	@Override
	public String getName() {
		return IS_PROBLEM_FROM_PASSED_LEVEL;
	}

}
