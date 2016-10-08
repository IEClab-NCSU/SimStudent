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

public class GetCurrentQuizLevel implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private static final String GET_CURRENT_QUIZ_LEVEL = "get-current-quiz-level";
	
	protected transient ModelTracer amt;
	
	protected transient Context context;
	
	public GetCurrentQuizLevel() {
		this(null);
	}
	
	public GetCurrentQuizLevel(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_CURRENT_QUIZ_LEVEL)) {
			throw new JessException(GET_CURRENT_QUIZ_LEVEL, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			
			int currentSection=amt.getController().getMissController().getSimStPLE().getCurrentQuizSectionNumber();
		
			
			Value rtnValue = new Value(currentSection, RU.INTEGER);
			return rtnValue;
			
	
		}
		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_CURRENT_QUIZ_LEVEL;
	}

}
