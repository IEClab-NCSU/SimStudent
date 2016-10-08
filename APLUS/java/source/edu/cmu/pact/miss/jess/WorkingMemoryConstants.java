package edu.cmu.pact.miss.jess;

/**
 * 
 */
public class WorkingMemoryConstants {

	/**  */
	private static final long serialVersionUID = 1L;

	/**	Constant to denote that the step is performed correctly */
	public static final String STEP_CORRECT = "correct";
	
	/**	Constant to denote that the step is performed incorrectly */
	public static final String STEP_INCORRECT = "incorrect";
	
	/**	Constant to denote that the step was performed by the SimStudent */
	public static final String SIMSTUDENT_ACTOR = "simstudent";
	
	/**	Constant to denote that the step was performed by the Student tutor teaching the SimStudent */
	public static final String STUDENT_TUTOR_ACTOR = "tutor";
	
	/**	Constant to denote that the SimStudent is requesting for help */
	public static final String HELP_REQUESTED = "true";
	
	/**	Constant to denote the outcome for successful completion of a quiz section */
	public static final String QUIZ_SECTION_PASSED = "pass";
	
	/**	Constant to denote the outcome for unsuccessful completion of a quiz section */
	public static final String QUIZ_SECTION_FAILED = "fail";
	
	/** Constant to denote that the request that SimStudent has made is a feedback */
	public static final String FEEDBACK_REQUEST = "feedback-request";
	
	/**	Constant to denote that the request that SimStudent has made is a hint */
	public static final String HINT_REQUEST = "hint-request";
	
	/**	 */
	public static final String TRUE = "true";
	
	/**	 */
	public static final String FALSE = "false";
	
	/**	Constant to denote the label for the done / problem is solved button */
	public static final String DONE_BUTTON_SELECTION = "done";
	
	/**	 */
	public static final String YES_BUTTON_SELECTION = "yes";
	
	/**	 */
	public static final String NO_BUTTON_SELECTION = "no";
	
	/**	 */
	public static final String BUTTON_ACTION = "ButtonPressed";
	
	/**	 */
	public static final String BUTTON_INPUT = "-1";
	
	/**	 */
	public static final String TAB_ACTION = "TabClicked";
	
	/**	 */
	public static final String PROBLEM_SOLVED = "clicking the problem solved button";
	
	/**
	 * Student actions are modeled in the working memory as lastAction. Example: If a 
	 * student takes a quiz then lastAction is set to Quiz or if a student clicks on 
	 * problem bank then the lastAction is set to ProblemBank
	 */
	public static final String QUIZ_TAKEN_ACTION = "Quiz";
	
	/**
	 * Student actions are modeled in the working memory as lastAction. Example: If a 
	 * student takes a quiz then lastAction is set to Quiz or if a student clicks on 
	 * problem bank then the lastAction is set to ProblemBank
	 */
	public static final String PROBLEM_BANK_REVIEWED_ACTION = "ProblemBank";
	
	/**	 */
	public static final String PROBLEM_USED_FOR_TUTORING = "Used";
	
	/**	 */
	public static final String PROBLEM_NOT_USED_FOR_TUTORING = "NotUsed";
	
	/**	 */
	public static final String SIMST_TAB = "simst";
}
