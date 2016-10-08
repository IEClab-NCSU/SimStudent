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

public class GetTypeOfProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private static final String GET_TYPE_OF_PROBLEM = "get-type-of-problem";
	
	protected transient ModelTracer amt;
	
	protected transient Context context;
	
	public GetTypeOfProblem() {
		this(null);
	}
	
	public GetTypeOfProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_TYPE_OF_PROBLEM)) {
			throw new JessException(GET_TYPE_OF_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				problem = vv.get(1).resolveValue(context).stringValue(context);
			}
		}

		if(problem.length() > 0 && problem != SimStRete.NOT_SPECIFIED) {

			problem=problem.replace(" ","");
			ProblemAssessor assessor = new AlgebraProblemAssessor();
			String problemType=assessor.classifyProblem(problem);
			problemType=problemType.toLowerCase();			
			
			int currentSection=amt.getController().getMissController().getSimStPLE().getCurrentQuizSectionNumber();
			String cur=amt.getController().getMissController().getSimStPLE().getSections().get(currentSection);
			cur=cur.toLowerCase();
			
			if (cur.contains("challenge")) cur="equations with variables on both sides";
			
			Value rtnValue = new Value(cur, RU.STRING);
			return rtnValue;
			
	
		}
		
		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_TYPE_OF_PROBLEM;
	}

}
