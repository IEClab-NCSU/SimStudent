package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;

public class QuizItemsLeft implements Userfunction, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String QUIZ_ITEMS_LEFT = "quiz-items-left";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public QuizItemsLeft(){
		this(null);
	}
	
	public QuizItemsLeft(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {

		if(!vv.get(0).stringValue(context).equals(QUIZ_ITEMS_LEFT)) {
			throw new JessException(QUIZ_ITEMS_LEFT, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String failedQuizProblems = SimStRete.NOT_SPECIFIED;
		String failedQuizProblemsSolved = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				failedQuizProblems = vv.get(1).resolveValue(context).stringValue(context);

				if(vv.size() > 2) {
					failedQuizProblemsSolved = vv.get(2).resolveValue(context).stringValue(context);
				}
			}
		}
		
		boolean solved = true;
		
		if(failedQuizProblems != SimStRete.NOT_SPECIFIED && failedQuizProblemsSolved != SimStRete.NOT_SPECIFIED) {
			
			String [] failedQuizProblemsList = failedQuizProblems.split(":");
			for(int i = 0; i < failedQuizProblemsList.length && solved; i++) {

				solved = false;
				String failedP = failedQuizProblemsList[i];
				String [] failedQuizProblemsSolvedList = failedQuizProblemsSolved.split(":");

				if(failedQuizProblemsSolvedList.length > 0) {
					
					for(int j=0; j < failedQuizProblemsSolvedList.length ; j++) {

						String solvedP = failedQuizProblemsSolvedList[j];
						if(failedP.equalsIgnoreCase(solvedP)) {
						
							solved = true;
							break;
						} 
					}
				}
			}
			
			if(solved) {
				return Funcall.FALSE;
			} else {
				return Funcall.TRUE;
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return QUIZ_ITEMS_LEFT;
	}

}
