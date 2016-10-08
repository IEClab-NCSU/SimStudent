package edu.cmu.pact.miss.jess;

import java.util.LinkedList;
import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory.Event;

public class QuizBeforeTutorNextProblem extends ModelTracePredicate {

	/**	 */
	private static final long serialVersionUID = 1L;

	public QuizBeforeTutorNextProblem() {
		setName("quiz-before-tutor-next-problem");
	}
	
	@Override
	public String apply(Vector argv, Context context) {
		
		LinkedList<Event> history;
		try {

			history = (LinkedList<Event>)((Fact)argv.get(0)).getSlotValue("eventHistory").javaObjectValue(context);
			Event first = history.getFirst();
			Event second = history.get(1);
			if(first.getAction().equalsIgnoreCase(SimStLogger.NEXT_PROBLEM_BUTTON_ACTION) && second.getAction()
					.equalsIgnoreCase(SimStLogger.QUIZ_BUTTON_ACTION)){
				return "T";
			}
		} catch (JessException je) {
			je.printStackTrace();
		}
		
		return null;
	}
}
