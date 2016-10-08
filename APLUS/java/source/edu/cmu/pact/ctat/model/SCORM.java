/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat.model;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary.CompletionValue;

/**
 * Facilities for SCORM 1.2 compliance.
  */
public class SCORM {

	/** Name of completion status data element. */
	public static final String LESSON_STATUS = "cmi.core.lesson_status";
	
	/** Name of score data element. */
	public static final String RAW_SCORE = "cmi.core.score.raw";
	
	/** Name of min score data element. */
	public static final String MIN_SCORE = "cmi.core.score.min";
	
	/** Name of max score data element. */
	public static final String MAX_SCORE = "cmi.core.score.max";

	/** Name of session time data element. */
	public static final String SESSION_TIME = "cmi.core.session_time";
	
	/** Name of exit-reason data element. */
	public static final String EXIT = "cmi.core.exit";
	
	/**
	 * Prescribed values for data element {@value #EXIT}. For {@link ExitReason#done},
	 * the prescribed value is an empty string, but Flash needs an argument, so we
	 * insert a single space.
	 */
	enum ExitReason {
		 timeout("time-out"),
		 suspend("suspend"),
		 logout("logout"),
		 done(" ");  // the prescribed value is an empty string, but Flash needs an argument

		 /** String for external use: see {@link #toString()}. */
		 private final String outputValue;

		 /**
		  * @param outputValue to set {@link #outputValue}
		  */
		 ExitReason(String outputValue) { this.outputValue = outputValue; }

		 /**
		  * @return {@link #outputValue}
		  */
		 public String toString() { return outputValue; }
	}
	
	/** Prescribed values for element {@value SCORM#LESSON_STATUS}. */
	enum LessonStatus {
		passed("passed"),
		completed("completed"),
		failed("failed"),
		incomplete("incomplete"),
		browsed("browsed"),
		notAttempted("not attempted");
		
		/** String for external use: see {@link #toString()}. */
		private final String outputValue;
		
		/**
		 * @param outputValue to set {@link #outputValue}
		 */
		LessonStatus(String outputValue) { this.outputValue = outputValue; }

		/**
		 * @return {@link #outputValue}
		 */
		public String toString() { return outputValue; }
	}

	/**
	 * Value for element {@value #LESSON_STATUS}.
	 * @param ps uses {@link ProblemSummary#getCompletionStatus()}
	 * @return string suitable for SCORM
	 */
	public static String getLessonStatus(ProblemSummary ps) {
		String result;
		if(CompletionValue.complete == ps.getCompletionStatus())
			result = LessonStatus.completed.toString();
		else
			result = LessonStatus.incomplete.toString();
		if(trace.getDebugCode("ps"))
			trace.outNT("ps", "SCORM.getRawScore() returns "+result);
		return result;
	}

	/**
	 * Value for element {@value #RAW_SCORE}. Calculated as
	 * {@link ProblemSummary#getUniqueCorrectUnassisted()}/{@link ProblemSummary#getUniqueSteps()}
	 * @param ps 
	 * @return number in range [0,1]; 0 if {@link ProblemSummary#getUniqueSteps()} returns "0.0"
	 */
	public static String getRawScore(ProblemSummary ps) {
		String result;
		if(ps.getUniqueSteps() == 0)
			result = "0";                  // avoid divide-by-zero fault
		else
			result = String.format("%.0f", ((double) ps.getUniqueCorrectUnassisted()*100)/ps.getUniqueSteps());
		if(trace.getDebugCode("ps"))
			trace.outNT("ps", "SCORM.getRawScore() returns "+result);
		return result;
	}
	
	/**
	 * Value for data element {@value #MIN_SCORE}
	 * @param ps currently unused
	 * @return String constant "0"
	 */
	public static String getMinScore(ProblemSummary ps) {
		return "0";
	}
	
	/**
	 * Value for data element {@value #MAX_SCORE}
	 * @param ps currently unused
	 * @return String constant "0"
	 */
	public static String getMaxScore(ProblemSummary ps) {
		return "100";
	}

	/**
	 * Value for element {@value #SESSION_TIME}. 
	 * @param ps uses {@link ProblemSummary#getTimeElapsed()}
	 * @return number of seconds, to 2 decimal places
	 */
	public static String getSessionTime(ProblemSummary ps) {
		long tms = ps.getTimeElapsed();
		long ms = tms % 1000;
		long s = (tms - ms)/1000;
		long m = s/60;
		long h = m/60;
		s = s % 60;
		if(h > 9999) {
			h = 9999; m = s = 99; ms = 99*10;
		}
		String result = String.format("%04d:%02d:%02d.%02d", h, m, s, ms/10);
		if(trace.getDebugCode("ps"))
			trace.outNT("ps", "SCORM.getSessionTime() given "+tms+" returns "+result);
		return result;
	}
	
	/**
	 * Value for data element {@value #EXIT}.
	 * @param ps currently unused
	 * @return String constant for {@link SCORM.ExitReason.done#toString()}
	 */
	public static String getExitReason(ProblemSummary ps) {
		return ExitReason.suspend.toString();
	}
}
