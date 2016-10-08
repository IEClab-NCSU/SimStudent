package edu.cmu.pact.miss.jess;

import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;

public class QuizPassed extends ModelTracePredicate {

	public QuizPassed() {
		setName("quiz-passed");
	}
	
	@Override
	public String apply(Vector argv, Context context) {

		String quizOutcome = "";
		
		try {
			quizOutcome = ((Fact)argv.get(0)).getSlotValue("quizOutcome").stringValue(context);
			if(quizOutcome.equalsIgnoreCase(WorkingMemoryConstants.QUIZ_SECTION_PASSED)) {
				return "T";
			}
		} catch (JessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
