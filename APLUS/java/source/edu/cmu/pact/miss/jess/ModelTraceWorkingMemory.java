package edu.cmu.pact.miss.jess;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jess.Funcall;
import jess.Value;


/**
 * 
 *  This class acts like a monitor. In other words, it perceives the actions and updates the working memory accordingly.
 *  This class must have only one instance for the APLUS environment. 
 *  The name of the class is misleading . It has to be renamed as MTReteMonitor
 */
@XmlRootElement
public class ModelTraceWorkingMemory implements Serializable,  Cloneable {

	/** Default Constant for serialization */
	private static final long serialVersionUID = 1L;

	/**	Serves as a lock to modify any field in the working memory. Before any thread
	 * tries to modify it must acquire a lock to ensure the operations are atomic */
	public static Object wmLock = new Object();
	
	/**	HashMap to keep all the problems that SimStudent failed on the quiz and the student tutored
	 *  Av = B     1
	 *  Av+B = C   2
	 */
	public static HashMap<String, Integer> quizProblemsTutoredListAllSections = new HashMap<String, Integer>();

	@XmlElement
	public HashMap<String, Integer> getQuizProblemsTutoredListAllSections() {
		return quizProblemsTutoredListAllSections;
	}
	
	
	
	/**	 */
	public static Stack<String> allTutoredProblemList= new Stack<String>();
	
	@XmlElement
	public Stack<String> getAllTutoredProblemList() {
		return allTutoredProblemList;
	}
	
	
	/**	Default no-arg Constructor */
	public ModelTraceWorkingMemory(){}
	
	private String lhsProblem;
	
	@XmlElement
	public String getLhsProblem() {
		return lhsProblem;
	}
	
	public void setLhsProblem(String lhsProblem) {
		this.lhsProblem = lhsProblem;
	}

	private String rhsProblem;
	
	@XmlElement
	public String getRhsProblem() {
		return rhsProblem;
	}
	
	public void setRhsProblem(String rhsProblem) {
		this.rhsProblem = rhsProblem;
	}

	
	private String lastTutoredProblem;
	
	@XmlElement
	public String getLastTutoredProblem() {
		return lastTutoredProblem;
	}
	
	public void setLastTutoredProblem(String lastTutoredProblem) {
		this.lastTutoredProblem = lastTutoredProblem;
	}
	
	
	private String quizInProgress=WorkingMemoryConstants.FALSE;
	
	public void setQuizInProgress(String flag){
		quizInProgress=flag;
	}
	
	@XmlElement
	public String getQuizInProgress() {
		return quizInProgress;
	}
	
	private String lhsQProblem;
	
	public String getLhsQProblem() {
		return lhsQProblem;
	}
	
	@XmlElement
	public void setLhsQProblem(String lhsQProblem) {
		this.lhsQProblem = lhsQProblem;
	}

	private String rhsQProblem;
	
	public String getRhsQProblem() {
		return rhsQProblem;
	}
	
	@XmlElement
	public void setRhsQProblem(String rhsQProblem) {
		this.rhsQProblem = rhsQProblem;
	}
		
	/**	 */
	private String studentEnteredProblem;	
	public String getStudentEnteredProblem() {
		return studentEnteredProblem;
	}
	
	@XmlElement
	public void setStudentEnteredProblem(String studentEnteredProblem) {
		this.studentEnteredProblem = studentEnteredProblem;
	}

	/**	Variable to denote the request. Can be either a feedback or a hint request. */
	private String requestType;
	
	public String getRequestType() {
		return requestType;
	}

	private String solved;

	@XmlElement
	public String getSolved() {
		return solved;
	}
	
	public void setSolved(String solved) {
		this.solved = solved;
	}
	
	
	private String currentQuizLevel;

	@XmlElement
	public String getCurrentQuizLevel() {
		return currentQuizLevel;
	}
	
	public void setCurrentQuizLevel(String currentQuizLevel) {
		this.currentQuizLevel = currentQuizLevel;
	}
	
	
	private String allQuizFailed;
	
	@XmlElement
	public String getAllQuizFailed() {
		return allQuizFailed;
	}
	
	public void setAllQuizFailed(String allQuizFailed) {
		this.allQuizFailed = allQuizFailed;
	}
	
	
	
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**	 */
	private String hintRequestSolvedQuizProblem;
	
	@XmlElement
	public String getHintRequestSolvedQuizProblem() {
		return hintRequestSolvedQuizProblem;
	}

	public void setHintRequestSolvedQuizProblem(String hintRequestSolvedQuizProblem) {
		this.hintRequestSolvedQuizProblem = hintRequestSolvedQuizProblem;
	}

//	/**	Variable to keep track of if SimStudent asked for hint on a problem
//	 *  Is set if there is a call to setHelpRequested for a step on a problem */
//	private String hintRequestOnProblem; 
//	
//	public String getHintRequestOnProblem() {
//		return hintRequestOnProblem;
//	}
//
//	public void setHintRequestOnProblem(String hintRequestOnProblem) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****");
//		this.hintRequestOnProblem = hintRequestOnProblem;
//	}

//	/**	Variable set to the problem currently being tutored */
//	private String currentProblem;
//	
//	public String getCurrentProblem() {
//		return currentProblem;
//	}
//
//	public void setCurrentProblem(String currentProblem) {
//		this.currentProblem = currentProblem;
//	}
	
	
	
	
	private int tutoredProblemsWithoutHint = 0;
	
	@XmlElement
	public int getTutoredProblemsWithoutHint() {
		return tutoredProblemsWithoutHint;
	}

	public void setTutoredProblemsWithoutHint(int tutoredProblemsWithoutHint) {
		this.tutoredProblemsWithoutHint = tutoredProblemsWithoutHint;
	}
	
	
	private int solutionSteps = 0;//WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public int getSolutionSteps() {
		return solutionSteps;
	}
	public void setSolutionSteps(int solutionSteps) {
		this.solutionSteps = solutionSteps;
	}
	
	/*private String restartCount = WorkingMemoryConstants.FALSE;
	
	public String getRestartCount() {
		return restartCount;
	}

	public void setRestartCount(String restartCount) {
		this.restartCount = restartCount;
	}
	*/
	
	private int restartCount = 0;
	
	@XmlElement
	public int getRestartCount() {
		return restartCount;
	}

	public void setRestartCount(int restartCount) {
		this.restartCount = restartCount;
	}
	
	
	
	private int quizFailCount = 0;
	
	@XmlElement
	public int getQuizFailCount() {
		return quizFailCount;
	}

	public void setQuizFailCount(int quizFailCount) {
		this.quizFailCount = quizFailCount;
	}
	
	
	private int allQuizFailCount = 0;
	
	@XmlElement
	public int getAllQuizFailCount() {
		return allQuizFailCount;
	}

	public void setAllQuizFailCount(int allQuizFailCount) {
		this.allQuizFailCount = allQuizFailCount;
	}
	
	
	
	private String canSolve = WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getCanSolve() {
		return canSolve;
	}

	public void setCanSolve(String canSolve) {
		this.canSolve = canSolve;
	}
	
	
	
	
	
	private String restartClickedRepeatedly = WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getRestartClickedRepeatedly() {
		return restartClickedRepeatedly;
	}

	public void setRestartClickedRepeatedly(String resourceViewed) {
		this.restartClickedRepeatedly = resourceViewed;
	}
	
	private String resourceViewed = WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getResourceViewed() {
		return resourceViewed;
	}

	public void setResourceViewed(String resourceViewed) {
		this.resourceViewed = resourceViewed;
	}
		
	
	private String tutoredProblemCorrectness = WorkingMemoryConstants.TRUE;
	
	@XmlElement
	public String getTutoredProblemCorrectness() {
		return tutoredProblemCorrectness;
	}

	public void setTutoredProblemCorrectness(boolean problemTutoredCorrectly) {
		if (problemTutoredCorrectly)
			this.tutoredProblemCorrectness = WorkingMemoryConstants.TRUE;
		else this.tutoredProblemCorrectness =WorkingMemoryConstants.FALSE;
		//this.tutoredProblemCorrectness = tutoredProblemCorrectness;
	}
	
	
	private String examplesTabClicked = WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getExamplesTabClicked() {
		return examplesTabClicked;
	}

	public void setExamplesTabClicked(String examplesTabClicked) {
		this.examplesTabClicked = examplesTabClicked;
	}
	
	
	private String simStudentThinking = WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getSimStudentThinking() {
		return simStudentThinking;
	}

	public void setSimStudentThinking(String simStudentThinking) {
		this.simStudentThinking = simStudentThinking;
	}
	
	
	
	
	
	private String UOTabClicked = WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getUOTabClicked() {
		return UOTabClicked;
	}

	public void setUOTabClicked(String UOTabClicked) {
		this.UOTabClicked = UOTabClicked;
	}

	private String problemBankTabClicked = WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getProblemBankTabClicked() {
		return problemBankTabClicked;
	}

	public void setProblemBankTabClicked(String problemBankTabClicked) {
		//trace.out(" this : "+this+"  ProblemBankClicked : "+problemBankTabClicked);
		this.problemBankTabClicked = problemBankTabClicked;
	}

	
	
	/**	 */
	private String problemType;
	
	@XmlElement
	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	/**	 */
	private String solutionCorrectness;
	
	@XmlElement
	public String getSolutionCorrectness() {
		return solutionCorrectness;
	}

	public void setSolutionCorrectness(String solutionCorrectness) {
		this.solutionCorrectness = solutionCorrectness;
	}

	/**	 */
	private String problemStatus;
	
	@XmlElement
	public String getProblemStatus() {
		return problemStatus;
	}

	public void setProblemStatus(String problemStatus) {
		this.problemStatus = problemStatus;
	}

	/**	Variable set to "true" when the quiz is taken */
	private String quizTaken = WorkingMemoryConstants.FALSE;

	@XmlElement
	public String getQuizTaken() {
		return quizTaken;
	}

	
	public void setQuizTaken(String quizTaken) {
		this.quizTaken = quizTaken;
	}

	/**	Variable set to "true" when the quiz is taken */
	private String quizIncomplete = WorkingMemoryConstants.FALSE;

	@XmlElement
	public String getQuizIncomplete() {
		return quizIncomplete;
	}

	
	public void setQuizIncomplete(String quizIncomplete) {
		this.quizIncomplete = quizIncomplete;
	}

	
	
	
	
	/**	Variable set to "true" when SimStudent is thinking*/
	private String thinking = WorkingMemoryConstants.FALSE;

	@XmlElement
	public String getThinking() {
		return thinking;
	}

	public void setThinking(String thinking) {
		this.thinking = thinking;
	}

	/**	Variable set to "true" when SimStudent asks for self-explanation*/
	private String selfExplanation = WorkingMemoryConstants.FALSE;

	@XmlElement
	public String getSelfExplanation() {
		return selfExplanation;
	}

	public void setSelfExplanation(String selfExplanation) {
		this.selfExplanation = selfExplanation;
	}
	
	/**	Variable set to "true" when SimStudent is thinking*/
	private String solutionCheckError = WorkingMemoryConstants.FALSE;

	@XmlElement
	public String getSolutionCheckError() {
		return solutionCheckError;
	}

	public void setSolutionCheckError(String solutionCheckError) {
		this.solutionCheckError = solutionCheckError;
	}
	

	
	
	
	private String studentSaiEntered=WorkingMemoryConstants.TRUE;
	
	@XmlElement
	public String getStudentSaiEntered(){
		return studentSaiEntered;
	}
	public void setStudentSaiEntered(String val){
		this.studentSaiEntered=val;
	}
	
	
	
	private String nextSelection="nil";
	
	@XmlElement
	public String getNextSelection(){
		return nextSelection;
	}
	public void setNextSelection(String nextSel){
		this.nextSelection=nextSel;
	}
	
	
	private String nextAction="nil";
	
	@XmlElement
	public String getNextAction(){
		return nextAction;
	}
	public void setNextAction(String nextAct){
		this.nextAction=nextAct;
	}
	
	private String nextInput="nil";
	
	@XmlElement
	public String getNextInput(){
		return nextInput;
	}
	public void setNextInput(String nextInp){
		this.nextInput=nextInp;
	}
	
	
	private String solutionGiven=WorkingMemoryConstants.FALSE;
	@XmlElement
	public String getSolutionGiven(){return this.solutionGiven;}
	public void setSolutionGiven(String sol){this.solutionGiven=sol;}
	
	
	private String previousProblemType="nil";
	@XmlElement
	public String getPreviousProblemType(){return this.previousProblemType;}
	public void setPreviousProblemType(String sol){this.previousProblemType=sol;}
	
	
	
	private String hintRequest=WorkingMemoryConstants.FALSE;
	@XmlElement
	public String getHintRequest(){return this.hintRequest;}
	public void setHintRequest(String hintRequest){this.hintRequest=hintRequest;}
	
	
	
	
	/**	Variable set to "true" when SimStudent just took the quiz*/
	private String afterQuiz = WorkingMemoryConstants.FALSE;

	@XmlElement
	public String getAfterQuiz() {
		return afterQuiz;
	}

	public void setAfterQuiz(String afterQuiz) {
		this.afterQuiz = afterQuiz;
	}
	
	
	
	/** Variable to keep track of the outcome of the quiz. Accepted values are "pass" / "fail" */
	private String quizOutcome;
	
	@XmlElement
	public String getQuizOutcome() {
		return quizOutcome;
	}

	public void setQuizOutcome(String quizOutcome) {
		this.quizOutcome = quizOutcome;
	}

	/**	Variable to keep track if the Unit Overview tab was visited by the student */
	private String UOTabReviewed;
	
	@XmlElement
	public String getUOTabReviewed() {
		return UOTabReviewed;
	}

	public void setUOTabReviewed(String status) {
		UOTabReviewed = status;
	}

			
	/**	Variable to keep track if the Examples tab was visited by the student */
	private String ExamplesReviewed=WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getExamplesReviewed() {
		return ExamplesReviewed;
	}

	public void setExamplesReviewed(String status) {
		ExamplesReviewed = status;
	}

	/**	 */
	private String quizProblemsFailedList;
	
	@XmlElement
	public String getQuizProblemsFailedList() {
		return quizProblemsFailedList;
	}

	public void setQuizProblemsFailedList(String quizProblemsFailedList) {
		this.quizProblemsFailedList = quizProblemsFailedList;
	}

	/*Added so we can access all failed quiz problems from a multi-slot from production rules */
	private String[] quizFailedProblemsList;
	
	@XmlElement
	public String[] getQuizFailedProblemsList() {
		return quizFailedProblemsList;
	}
	
	
	
	
	private String moreSteps=WorkingMemoryConstants.FALSE;
	
	@XmlElement
	public String getMoreSteps() {
		return moreSteps;
	}

	public void setMoreSteps(String moreSteps) {
		this.moreSteps = moreSteps;
	}
	
	
		
	/**	 */
	private String quizProblemsTutoredList;
	
	@XmlElement
	public String getQuizProblemsTutoredList() {
		return quizProblemsTutoredList;
	}

	public void setQuizProblemsTutoredList(String quizProblemsTutoredList) {
		this.quizProblemsTutoredList = quizProblemsTutoredList;
	}

	/**	 */
	private String quizProblemsPassed;
	
	@XmlElement
	public String getQuizProblemsPassed() {
		return quizProblemsPassed;
	}

	public void setQuizProblemsPassed(String quizProblemsPassed) {
		this.quizProblemsPassed = quizProblemsPassed;
	}

	/** Slot to keep track of if the APlus was launched	 */
	private String APlusLaunched = WorkingMemoryConstants.TRUE;

	@XmlElement
	public String getAPlusLaunched() {
		return APlusLaunched;
	}
	
	public void setAPlusLaunched(String status) {
		APlusLaunched = status;
	}
	
	private int consecutiveResourceReview = 0;
	
	@XmlElement
	public int getConsecutiveResourceReview() {
		return consecutiveResourceReview;
	}


	public void setConsecutiveResourceReview(int consecutiveResourceReview) {
		this.consecutiveResourceReview = consecutiveResourceReview;
	}
	
	private boolean overviewScrolled = false;

	@XmlElement
	public boolean isOverviewScrolled() {
		return overviewScrolled;
	}


	public void setOverviewScrolled(boolean overviewScrolled) {
		this.overviewScrolled = overviewScrolled;
		if(overviewScrolled)
			this.consecutiveResourceReview++;
	}

	private boolean exampleProblemViewed = false;
	
	@XmlElement
	public boolean isExampleProblemViewed() {
		return exampleProblemViewed;
	}


	public void setExampleProblemViewed(boolean exampleProblemViewed) {
		this.exampleProblemViewed = exampleProblemViewed;
		if(exampleProblemViewed)
			this.consecutiveResourceReview++;
	}


	private int quizLevelPassed = 0;
	
	@XmlElement
	public int getQuizLevelPassed() {
		return quizLevelPassed;
	}


	public void setQuizLevelPassed(int quizLevelPassed) {
		this.quizLevelPassed = quizLevelPassed;
	}

	public static String suggestedProblem = "";

	@XmlElement
	public String getSuggestedProblem() {
		return suggestedProblem;
	}

 	
	/**
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {}
	
	/**
	 * @param in
	 * @throws IOException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException {}
	

	/**	 */
	public Object clone() {
		try {
			ModelTraceWorkingMemory clonedWM = (ModelTraceWorkingMemory)super.clone();
			return clonedWM;
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		ModelTraceWorkingMemory wm = new ModelTraceWorkingMemory();
	}
	
	


	










	/**
	 *
	 */
	public class Event {
		
		public Event(String action) {
			this.action = action;
		}
		
		private String action;

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}
	}


//	/**	Name of the SimStudent */
//	private String ssName;
//	
//	public String getSsName() {
//		return ssName;
//	}
//
//	public void setSsName(String ssName) {
//		this.ssName = ssName;
//	}
//
//	/** Keeps track of the correctness for the step  */
//	private String stepCorrectness;
//	
//	public String getStepCorrectness() {
//		return stepCorrectness;
//	}
//
//	public void setStepCorrectness(String stepCorrectness) {
//		this.stepCorrectness = stepCorrectness;
//	}
//
//	/**	Step for which step correctness was determined */
//	private String step;
//	
//	public String getStep() {
//		return step;
//	}
//
//	public void setStep(String step) {
//		if(step.equals(WorkingMemoryConstants.BUTTON_INPUT)) {
//			this.step = WorkingMemoryConstants.PROBLEM_SOLVED;
//		} else {
//			this.step = step;
//		}
//	}
//
//	/**	Variable to keep track for when the step is undone*/
//	private String stepUndone = WorkingMemoryConstants.FALSE;
//	
//	public String getStepUndone() {
//		return stepUndone;
//	}
//
//	public void setStepUndone(String stepUndone) {
//		this.stepUndone = stepUndone;
//	}
//
//	/**	Variable set to the {@link ProblemNode} name when help is requested by the SimStudent.
//	 *  Required to avoid race-condition with the interactive learning where in the node is 
//	 *  updated while the model-tracing has not yet started. */
//	private String problemNodeName;
//	
//	public String getProblemNodeName() {
//		return problemNodeName;
//	}
//
//	public void setProblemNodeName(String problemNodeName) {
//		this.problemNodeName = problemNodeName;
//	}
//
//	private String lastAction;
//
//	public String getLastAction() {
//		return lastAction;
//	}
//
//	public void setLastAction(String lastAction) {
//		this.lastAction = lastAction;
//	}
//	/**	 */
//	private String tutoringActivityStarted = WorkingMemoryConstants.FALSE;
//	
//	public String getTutoringActivityStarted() {
//		return tutoringActivityStarted;
//	}
//
//	public void setTutoringActivityStarted(String tutoringActivityStarted) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****");
//		this.tutoringActivityStarted = tutoringActivityStarted;
//	}
//	/**	Variable is set when a student should enter a problem */
//	private String enterProblem;
//	
//	public String getEnterProblem() {
//		return enterProblem;
//	}
//
//	public void setEnterProblem(String enterProblem) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****");
//		this.enterProblem = enterProblem;
//	}
//	/** Tells whether the action was performed by student tutor / simstudent */
//	private String actor;
//
//	public String getActor() {
//		return actor;
//	}
//
//	public void setActor(String actor) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****");
//		this.actor = actor;
//	}
//	/**	Variable to keep track of if Student tutor has taken a look at the examples or not */
//	private String examplesTabClicked = WorkingMemoryConstants.FALSE;
//	
//	public String getExamplesTabClicked() {
//		return examplesTabClicked;
//	}
//
//	public void setExamplesTabClicked(String examplesTabClicked) {
//		this.examplesTabClicked = examplesTabClicked;
//	}
//	/**	 */
//	private String problemEntered;
//	
//	public String getProblemEntered() {
//		return problemEntered;
//	}
//
//	public void setProblemEntered(String problemEntered) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****");
//		this.problemEntered = problemEntered;
//	}
//	/**	 */
//	private String suggestedProblem;
//	
//	public String getSuggestedProblem() {
//		return suggestedProblem;
//	}
//
//	public void setSuggestedProblem(String suggestedProblem) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****");
//		this.suggestedProblem = suggestedProblem;
//	}
//	/**	 */
//	private String suggestedProblems;
//	
//	public String getSuggestedProblems() {
//		return suggestedProblems;
//	}
//
//	public void setSuggestedProblems(String suggestedProblems) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****" + suggestedProblems);
//		this.suggestedProblems = suggestedProblems;
//	}
//	/**	 */
//	private String quizProblemsFailed;
//	
//	public String getQuizProblemsFailed() {
//		return quizProblemsFailed;
//	}
//
//	public void setQuizProblemsFailed(String quizProblemsFailed) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting quizProblemsFailed*****" + quizProblemsFailed);
//		this.quizProblemsFailed = quizProblemsFailed;
//	}
//	
//	/**	Variable is set when SimStudent requests for help on a step */
//	private String helpRequested;
//	
//	public String getHelpRequested() {
//		return helpRequested;
//	}
//
//	public void setHelpRequested(String helpRequested) {
//		if(trace.getDebugCode("rr"))trace.out("rr", "*****Setting*****");
//		this.helpRequested = helpRequested;
//		setHintRequestOnQuizProblemType(WorkingMemoryConstants.TRUE);
//		//setHintRequestOnProblem(WorkingMemoryConstants.TRUE);
//	}
//
//	private String hintRequestOnQuizProblemType = WorkingMemoryConstants.FALSE;
//	
//	public String getHintRequestOnQuizProblemType() {
//		return hintRequestOnQuizProblemType;
//	}
//
//	public void setHintRequestOnQuizProblemType(String hintRequestOnQuizProblemType) {
//		
//		if(trace.getDebugCode("rr"))trace.out("rr", "Enter in setHintRequestOnQuizProblemType");
//		if(modelTracedEvents.size() > 0 && modelTracedEvents.getFirst().equals("quizProblemType")) {
//			if(modelTracedEvents.size() > 1) {
//				String abstractedProblem = modelTracedEvents.get(1);
//				String[] token = currentProblem.split("=");
//    			if(token.length == 2) {
//    				currentProblem = " " + token[0] + "=" + token[1] + " ";
//    				Problem minervaProblem = new Problem(currentProblem);
//    				String currentAbstractedProblem = minervaProblem.getSignedAbstraction();
//    				if(trace.getDebugCode("rr"))trace.out("rr", "abstractedProblem: " + abstractedProblem + " currentAbstractedProblem: " + currentAbstractedProblem);
//    				if(abstractedProblem.equals(currentAbstractedProblem)) {
//    					if(trace.getDebugCode("rr"))trace.out("rr", "Setting hintRequestOnQuizProblemType");
//    					this.hintRequestOnQuizProblemType = hintRequestOnQuizProblemType;					
//    				}	
//    			}	
//			}
//		}
//	}
//
//	/**	Variable to set the problem that was completed before the currentProblem */
//	private String completedProblem;
//	
//	public String getCompletedProblem() {
//		return completedProblem;
//	}
//
//	public void setCompletedProblem(String completedProblem) {
//		this.completedProblem = completedProblem;
//	}
//
//	public static String resolveWMValues(String msg, ModelTraceWorkingMemory wm) {
//		
//		if(trace.getDebugCode("rr"))trace.out("rr", "Enter in resolveWMValues in XMLReader : " + msg);
//		char c = '$';
//		int beginIndex, endIndex;
//		String value = null, field = null;
//		
//		beginIndex = msg.indexOf(c);
//		if(beginIndex == -1) {
//			return msg;
//		} else {
//			endIndex = msg.indexOf(c, beginIndex+1);
//			if(endIndex == -1) {
//				return msg;
//			} else {
//				try {
//					field = msg.substring(beginIndex+1, endIndex);
//					Class cls = wm.getClass();
//					Field[] fieldList = cls.getDeclaredFields(); {
//						for(int i=0; i < fieldList.length; i++) {
//							Field fld = fieldList[i];
//							if(fld.getName().equalsIgnoreCase(field.trim())) {
//								value = (String) fld.get(wm);
//								if(trace.getDebugCode("rr"))trace.out("rr", "field: " + field + " value: " + value);
//							}
//						}
//					}
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		msg = msg.replaceAll("\\$", "");
//		msg = msg.replace(field, value);
//		if(trace.getDebugCode("rr"))trace.out("rr", "Exit from resolveWMValues in ModelTraceWorkingMemory : " + msg);
//		return msg;
//	}

}
