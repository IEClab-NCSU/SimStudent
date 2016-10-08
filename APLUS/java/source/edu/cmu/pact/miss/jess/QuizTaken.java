package edu.cmu.pact.miss.jess;

import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;

public class QuizTaken extends ModelTracePredicate {

	public QuizTaken(){
		setName("quiz-taken");
	}
	
	@Override
	public String apply(Vector argv, Context context) {

		String quizTaken = "";
		
		try {
			quizTaken = ((Fact)argv.get(0)).getSlotValue("quiz").stringValue(context);
			if(quizTaken.equalsIgnoreCase(WorkingMemoryConstants.TRUE)) {
				return "T";
			}
		} catch (JessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
