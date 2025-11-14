package edu.cmu.pact.miss;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jess.Activation;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.BKT.BKT;
import edu.cmu.pact.miss.BKT.LMS;
import edu.cmu.pact.miss.MetaTutor.APlusHintDialog;
//import edu.cmu.pact.miss.MetaTutor.Activation;
import edu.cmu.pact.miss.MetaTutor.XMLReader;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform;
import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommWidget;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform.JLabelIcon;
import edu.cmu.pact.miss.PeerLearning.ClickableProgressBar;
import edu.cmu.pact.miss.PeerLearning.QuizPane;
import edu.cmu.pact.miss.PeerLearning.SimStConversation;
import edu.cmu.pact.miss.PeerLearning.SimStExample;
import edu.cmu.pact.miss.PeerLearning.SimStHintLogAgent;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStMessageDialog;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStudentLMS;
import edu.cmu.pact.miss.PeerLearning.Skillometer;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform.ExampleAction;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;
import edu.cmu.pact.miss.jess.ActivationListDrop;
import edu.cmu.pact.miss.jess.ModelTracingEvent;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;

import javax.swing.JDialog;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXTaskPane;
import edu.cmu.pact.jess.SimStJessModelTracingBKT;

import static edu.cmu.pact.miss.InquiryClAlgebraTutor.findPathDepthFirst;

/*
 * Class that holds all the specifics for the CogTutor mode of APLUS.
 * In class, this details holds 
 * - the LMS, that provides the problems based on the knowledge-tracing results (will be added, problems are hardcoded for now)
 * - the knowledge tracer, that updates the skills based on student performance
 * - whatever is needed for APLUS to work on CogTutor mode.
 */
public class SimStCognitiveTutor {


	private static final String LEVEL_MASTERED_MSG="Congratulations, you have mastered the skills for this section. Click OK to proceed.";
	private static final String REVIEW_LEVEL_MSG="Before you practice on <level>, let's take a look at some examples first! Review the examples and click on the [Tutoring] tab to practice."; 
	private static final String PROBLEM_SOLVED_MSG="Finished this one! Why don't you practive solving another one?";
	private static final String QUIZ_SOLVED_CORRECTLY="Your solved the problem correctly! You can continue working on the next problem. ";
	private static final String QUIZ_SOLVED_INCORRECTLY = "Your solution is not correct. You can edit your solution.<br><br>Alternatively, you may practice the equations.";
	private static final String QUIZ_SOLVED_INCORRECTLY_EXAMPLES_ADD = "<br><br>I would also encourage you to review examples. ";
	protected Color startColor = Color.black;

	protected Color correctColor = Color.green.darker();

	protected Color incorrectColor = Color.red;

	public boolean lockProblemEntering=false;

	private LMS simStLMS;
	public LMS getSimStLMS(){return this.simStLMS;}
	private void setSimStLMS(LMS lms){this.simStLMS=lms;}

	public BKT simStBKT;
	public BKT getSimStBKT(){return this.simStBKT;}
	private void setSimStBKT(BKT bkt){this.simStBKT=bkt;}
	

	BR_Controller brController;
	void setBrController(BR_Controller controller){this.brController=controller;}
	BR_Controller getBrController(){return this.brController;}

	SimStPLE simStPLE;
	void setSimStPLE(SimStPLE ple){this.simStPLE=ple;}
	SimStPLE getSimStPLE(){return this.simStPLE;}

	/*variable to hold the current level*/
	int currentLevel=0;

	int problemsGivenCount=0;
	void incProblemsGivenCount(){problemsGivenCount=problemsGivenCount+1;}
	int getProblemsGivenCount(){return this.problemsGivenCount;}
	
	String lastGivenProblem=null;
	public void setLastGivenProblem(String problem){this.lastGivenProblem=problem;}
	public String getLastGivenProblem(){return this.lastGivenProblem;}
	
	
	SimStLogger simStLogger=null;
	void setSimStLogger(SimStLogger logger){this.simStLogger=logger;}
	SimStLogger getSimStLogger(){return this.simStLogger;}
	
	/*boolean indicating if student is on quiz tab, so we know we are in quiz solving mode (where he handle interface actions differently)*/
	public boolean quizSolving=false;
	public void setQuizSolving(boolean flag){this.quizSolving=flag;}
	public boolean getQuizSolving(){return this.quizSolving;}
	
	

	
 	/*Quiz solution hash to keep track of */
 	Map<String, Sai> quizSolutionHash;
 	public void initQuizSolutionHash(){
 		
 		
 		if (quizSolutionHash==null){
 			quizSolutionHash = new LinkedHashMap<String, Sai>();
 		}
 		else{
 			quizSolutionHash.clear();
 		}	
 	}
 	public Map<String, Sai> getQuizSolutionHash(){return this.quizSolutionHash;}
 	
 	
 	Map<Integer,Map<String, Sai>> wrongSolutions;
 	
 	Map<String, Sai> failedQuizProblemSolutionHash;
 	
 	public void initFailedQuizSolutionHash(){
 		if (failedQuizProblemSolutionHash==null){
 			failedQuizProblemSolutionHash = new LinkedHashMap<String, Sai>();
 		}
 		else{
 			failedQuizProblemSolutionHash.clear();
 		}	
 	}
 	
 	public Map<String, Sai> getFailedQuizSolutionHash(){return this.failedQuizProblemSolutionHash;}
 	
 	/*variable to keep track the quiz index of the current quiz taken. We need this so we can update the correct quiz item on the JXTaskPane after quiz grading*/
 	private int currentQuizIndex=0;
	public void setCurrentQuizIndex(int index){this.currentQuizIndex=index;}
	public int getCurrentQuizIndex(){return this.currentQuizIndex;}
	

	
	
	/**
	 * Constructor
	 */
	public SimStCognitiveTutor(BR_Controller brController,SimStPLE ple){

		//Create the simStudentLMS object
		simStLMS=new LMS(this.getSimStLogger());
		//simStLMS=new LMS(this.getSimStPLE().getSimSt());
		simStBKT=new BKT(brController.getMissController().getSimSt());
		//simStBKT=((SimStJessModelTracingBKT) getSimStPLE().getMissController().getBrController().getModelTracer().getJMT()).getBKT();
		
		setBrController(brController);
		setSimStPLE(ple);
		setSimStLogger(ple.getSimSt().getSimStLogger());
	
	}

	
	/**
	 * Method for giving the next problem in cog tutor mode.
	 * @param prooceedToNext if false means that last given problem is given again, do not ask LMS for next problem.
 	 */
	public void giveNextProblem(boolean proceedToNext){
		//make sure LMS has the simSt object
		getSimStLMS().setSimSt(this.getSimStPLE().getSimSt());
			
		//give problem
		String nextProblem=getLastGivenProblem();
		if (proceedToNext){
			//nextProblem=getSimStLMS().getNextProblem();
			nextProblem=getSimStLMS().getNextProblem(this.getSimStBKT().bkt_vals);

			this.setLastGivenProblem(nextProblem);
		}
		
		giveProblem(nextProblem);
	}


	
	/**
	 * Method that gives a problem to human student in cogTutor mode.
	 * @param problem the problem to give
	 */
	public void giveProblem(String problem){

		incProblemsGivenCount();
		
		getSimStPLE().getSsInteractiveLearning().createStartStateOnProblem(problem);
		
		/*Log that new problem is entered. SOS: this transaction will held by the "back to the future" mechanism. it will be released once student clicks done correctly.*/
		if (getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode())
			getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_ENTERED_ACTION, problem, "", "");		
		else{
			String lmsID="LMS_"+this.getProblemsGivenCount();
			getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_ENTERED_ACTION, problem, lmsID, this.getSimStBKT().getLValues());		
			this.getSimStLMS().logGains(getSimStLogger(),lmsID);
		}

		getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_DURATION, problem, "", "");
		
		if (this.getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode()){
				repaintStartStateOnStudentInterface(SimSt.convertFromSafeProblemName(problem));
				getSimStPLE().setFocusOfStartStateElementsStudentInterface(false);			
		}
		//getSimStPLE().getSsInteractiveLearning().createStartStateOnProblem(problem);
		//make all cells available to student except from the start state elements.
		getSimStPLE().blockInput(false);  
		getSimStPLE().setFocusOfStartStateElements(false);	//<-- This fixed the error of ModelTracer not working when switching tabs
		
		/*just in case, update the working memory with the problem*/
		if (this.getBrController().getMissController().getSimSt().getModelTraceWM().getStudentEnteredProblem()==null){
			this.getBrController().getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(problem);
		}
		
		
		//prepare the model tracer
		prepareModeltracer();
		
		String failedProblems=this.brController.getMissController().getSimSt().getModelTraceWM().getQuizProblemsFailedList();
		if (failedProblems!=null && failedProblems.contains(problem))
			this.brController.getMissController().getSimSt().getModelTraceWM().setProblemType("failedQuizProblem");
		
	
		
	}
	
		
	/**
	 * Paint start state based on a problem. Used to solve the problem where sometimes 
	 * (after taking quiz on AplusCogTutor), problem was empty from the interface (but present in graph)
	 * @param problemName
	 */
	public void repaintStartStateOnStudentInterface(String problemName){
		
		String[] sp = problemName.split("=");
		
		String c1r1Value = sp[0].trim();
		String c2r1Value = sp[1].trim();

			ArrayList<String> startElements = getSimStPLE().getStartStateElements();
			
			Component[] list=getSimStPLE().getSimStPeerTutoringPlatform().getStudentInterface().getComponents();
			
			for (int i = 0; i < startElements.size() && i < sp.length; i++) {
				
				getSimStPLE().getSsInteractiveLearning().setWidgetValue(list,startElements.get(i),sp[i].trim());
			}	
	}
	
	public void clearStartStateFromStudentInterface(){
		ArrayList<String> startElements = getSimStPLE().getStartStateElements();
		Component[] list=getSimStPLE().getSimStPeerTutoringPlatform().getStudentInterface().getComponents();
		
		for (int i = 0; i < startElements.size() ; i++) {
			
			getSimStPLE().getSsInteractiveLearning().setWidgetValue(list,startElements.get(i),"");
		}	
	}

	/**
	 * Method that prepares the modeltracer for the problem 
	 * 
	 */
	public void prepareModeltracer(){

		/*notify that problem is started*/
		//trace.out(" Notify the problem is started");
		getBrController().getAmt().handleInterfaceAction("yes","ButtonPressed","-1");
		/*initialize all variables*/
		getSimStPLE().getSsInteractiveLearning().initModelTracerForProblem();
		/*notify MT that we are always expecting for a hint*/
		trace.out(" Preparing for Model Tracer !!!");
		getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("hint-request");
	}

	/**
	 * Method that takes the student SAI and tries to figure out if its correct or not. 
	 * 
	 * @param selection
	 * @param action
	 * @param input
	 * @return true if the BR_controller should proceed or not 
	 */
	public boolean processInterfaceAction(String selection, String action, String input){
		

		
		/*Log student action*/
      	//String step = brController.getMissController().getSimSt().getProblemStepString();
        ProblemNode currentNode = brController.getSolutionState().getCurrentNode();
        ProblemNode startNode = currentNode.getProblemModel().getStartNode();
        Vector<ProblemEdge> pathEdges = findPathDepthFirst(startNode, currentNode);
        String step = brController.getMissController().getSimSt().getStepNameGetter().getStepName(pathEdges, startNode);

      	ProblemNode parentNode= brController.getSolutionState().getCurrentNode();	
		getSimStPLE().getSimSt().setProblemStepString(step);

      	AskHint hint = getBrController().getMissController().getSimSt().askForHint(getBrController(),getBrController().getSolutionState().getCurrentNode());
      	
      	if (hint!=null){     	
      		String result="";
      		if (!getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode()) {
      			double probCorrect= getSimStBKT().probC(hint.skillName);
      			result="L="+  getSimStBKT().getSkill_probL(hint.skillName)+", pC="+probCorrect;
      		}
      		
      			this.getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_INFO_RECEIVED, SimStLogger.STUDENT_STEP_ENTERED, step,result,"",new Sai(selection,action,input),parentNode, hint.getSelection(), hint.getAction(), hint.getInput(), 0,"");
      			
      	}
      	
		/*process it*/
		return selection.equalsIgnoreCase("done")? processDone(selection,action,input) : processInterfaceSAI(selection,action,input);

	}
	
	/**
	 * Method to get the quiz solution 
	 * @return
	 */
	public Vector<Sai> getQuizSolution(){
		
			/*Convert the quiz solution hash to Vector<Sai> because thats what the SimStPLE grading method expects.*/
			Vector<Sai> tmpSolution = new Vector<Sai>();
			for (String key : getQuizSolutionHash().keySet()) {
				           tmpSolution.add(getQuizSolutionHash().get(key));
			}
			return tmpSolution;
	}
	/**
	 * Method that takes the student SAI taken at the quiz and processes it 
	 * 
	 * @param selection
	 * @param action
	 * @param input
	 * @return true if the BR_controller should proceed or not 
	 */
	public boolean processQuizInterfaceAction(String selection, String action, String input){
		
		/* Do not process quiz action if its about the LHS OR RHS. 
		 * TODO: this should be avoided to make the code domain independent. */
		
		if (selection.equals("dorminTable1_C1R1")) return false;
		if (selection.equals("dorminTable2_C1R1")) return false;
	
		/*if startQuizTime = 0 this means that student just started taking the quiz, 
		 * so a) start the timer and b) log that student started to take the quiz.*/
		if (startQuizTime==0){
			startQuizTime = Calendar.getInstance().getTimeInMillis();
			String problem=SimSt.convertFromSafeProblemName(getSimStPLE().getSsInteractiveLearning().getQuizGraph().getStartNode().getName());
			getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_QUESTION_GIVEN_ACTION_HUMAN, "Quiz"+(getSimStPLE().currentQuizSectionNumber+1)+"."+(getCurrentQuizIndex()),"",problem); 	
       		/*update working memory that student is taking the quiz, so restart how many problems student sovled*/
			brController.getMissController().getSimSt().getModelTraceWM().setTutoredProblemsWithoutHint(0);      	
       		
		}

		return selection.equalsIgnoreCase("done") ? gradeQuiz(selection,action,input) : addQuizStep(selection,action,input);

	}
	
	public boolean gradingQuiz=false;
	

	boolean isTakingQuiz=false;
	/**
	 * Method that grades the quiz
	 * @return
	 */
	private boolean gradeQuiz(String selection,String action,String input){
		
		isTakingQuiz=true;
		/*notify APLUS model tracing memory that student is taking the quiz*/
		brController.getAmt().handleInterfaceAction("quiz", "ButtonPressed", "-1");
		brController.getMissController().getSimSt().getModelTraceWM().setQuizIncomplete("nil"); 
		
		
        AplusPlatform aplus=((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform());


		//quizSolution.add(new Sai(selection, action, input));
		//trace.out(" In the hash : "+selection+"  value : "+input);
		getQuizSolutionHash().put(selection, new Sai(selection, action, input));
		
		

	
	/*set the quiz graph so quiz grading methods work*/
		this.getBrController().getMissController().getSimSt().setQuizGraph(this.getSimStPLE().getSsInteractiveLearning().getQuizGraph());
	
		/*Convert the quiz solution hash to Vector<Sai> because thats what the SimStPLE grading method expects.*/
		Vector<Sai> tmpSolution = new Vector<Sai>();
		List<String> components = this.getBrController().getMissController().getSimStPLE().getComponents();
		
		 for(int i=0; i< components.size(); i++) {
			 String key = components.get(i);
			 if(getQuizSolutionHash().containsKey(key)){
				// trace.out(" Key : "+key+"   value : "+getQuizSolutionHash().get(key));
				 tmpSolution.add(getQuizSolutionHash().get(key));
			 } 
			/* else 
				trace.out("Not found in the hash  "+key);*/
		 }
		/*for (String key : getQuizSolutionHash().keySet()) {
			          tmpSolution.add(getQuizSolutionHash().get(key));
					 // if(!key.equalsIgnoreCase("done"))
						//  		components.add(key);
			          trace.out(" Key "+key+" Selection : "+getQuizSolutionHash().get(key).getS()+"  Action : "+getQuizSolutionHash().get(key).getA()+" Input :   "+getQuizSolutionHash().get(key).getI());
		}*/
		
	
		
		/*call existing quiz grading method to see if solution is correct*/
		String res=this.getSimStPLE().getSsInteractiveLearning().gradeQuizProblemSolution(tmpSolution/*quizSolution*/, null);
		//trace.out(" Result  :  "+res);
		String quizAssessment ="";
		
		String problem=SimSt.convertFromSafeProblemName(getSimStPLE().getSsInteractiveLearning().getQuizGraph().getStartNode().getName());
		
		boolean isQuizSolutionCorrect=false;
		int correct = 0;
		if (res.equals(SimStInteractiveLearning.QUIZ_SOLUTION_CORRECT)){
			//if solution is correct then update the passedProblemList
						
			correct = 1;
			SimStPLE.passedProblemsList=SimStPLE.passedProblemsList+SimStPLE.cogTutorCurrentProblem+",";

			SimStPLE.cogTutorCurrentProblem++;

			//increase the count of currentCorrect (so we know when a section is finished)
			getSimStPLE().currentCorrect++;
			isQuizSolutionCorrect=true;
			quizAssessment = getSimStPLE().getConversation().getMessage(SimStConversation.COG_TUTOR_QUIZ_CORRECT);//QUIZ_SOLVED_CORRECTLY	;
			 
			if (this.getSimStPLE().quizProg.get(this.getSimStPLE().currentQuizSectionNumber)!=null)
					this.getSimStPLE().quizProg.get(this.getSimStPLE().currentQuizSectionNumber).setValue(this.getSimStPLE().currentCorrect);
		
			initFailedQuizSolutionHash();
		}
		else{
			quizAssessment = QUIZ_SOLVED_INCORRECTLY;
			correct = 0;
			if (failedQuizProblemSolutionHash==null) initFailedQuizSolutionHash();
        	failedQuizProblemSolutionHash.putAll(quizSolutionHash);
        	
        	
		}
		
		isTakingQuiz=false;
		
		/*update the model tracer working memory with the quiz results*/
		//trace.out(" Updating the working memory ");
		/**
		 * The next line is a bug. It updates the working memory with the No of problems that have been attempted correctly so far
		 * So the next line is commented 
		 */
		//this.getSimStPLE().updateWorkingMemoryWithQuizResults(getSimStPLE().currentCorrect, problem);
		this.getSimStPLE().updateWorkingMemoryWithQuizResults(correct, problem);
    	
		
		/*lock the quiz interface so students cannot change their decision*/			
		//this.getSimStPLE().unBlockQuiz(false);
		
		
		/*Create the SimStExample object with the quiz solution (required by already existing method to display quiz results*/	
		SimStProblemGraph problemGraph = this.getSimStPLE().getSsInteractiveLearning().getQuizGraph();  
        SimStNode startNode = problemGraph.getStartNode();
        Vector<ProblemEdge> solutionPath = startNode.findSolutionPath();
        
        SimStExample tmpQuizSimStExample=this.getSimStPLE().colorCodeQuizSolutionSteps(solutionPath,getSimStPLE().getSsInteractiveLearning().getQuizGraph().getStartNode().getName());

        if (aplus.lastClickedQuizProblemIndex!=-1)
        	tmpQuizSimStExample.setIndex(aplus.lastClickedQuizProblemIndex);
        else
        	tmpQuizSimStExample.setIndex(this.getCurrentQuizIndex());
 	
        if (aplus.lastClickedQuizProblem!=null)
        	tmpQuizSimStExample.setTitle(aplus.lastClickedQuizProblem);
        else
        	//tmpQuizSimStExample.setTitle((this.getCurrentQuizIndex()+1)+". " + tmpQuizSimStExample.getTitle()); 
        	tmpQuizSimStExample.setTitle(tmpQuizSimStExample.getTitle()); 
               
        tmpQuizSimStExample.setSimSt(brController.getMissController().getSimSt());            
      
        if( getSimStPLE().getSimSt().getSolvedQuizProblem() == null)
        	getSimStPLE().getSimSt().setSolvedQuizProblem(new Vector<SimStExample>());

        
        
        /*store the solutionHash to example, so student can resume...*/
        if (!failedQuizProblemSolutionHash.isEmpty())
        	tmpQuizSimStExample.getQuizSolutionHash().putAll(failedQuizProblemSolutionHash);
        
        /*Update the Quiz interface with the quiz results*/
        aplus.addQuizLabelIcon(tmpQuizSimStExample,true);


		
        /*unblock incorrect steps*/
      //  unblockIncorrectSteps(tmpQuizSimStExample);
        
        /*if solution is not correct, enable the restart button*/
        if (!res.equals(SimStInteractiveLearning.QUIZ_SOLUTION_CORRECT))
        	aplus.restartButtonQuiz.setEnabled(true);
        
        /*update the board*/
        aplus.setSpeech(tmpQuizSimStExample.getExplanation(), true);
        
      
        
        boolean allSectionProblemsSolved=false;
        
        /*Check if entire section was completed.*/
		if(getSimStPLE().currentCorrect == getSimStPLE().currentQuizSection.size()){
			allSectionProblemsSolved=true;	
		}
		else{
			
		}
		
		
		/*Start of logging the result. Here we must log two things
		 * 1. QUIZ_QUESTION_ANSWER_ACTION, which has the solution steps
		 * 2. QUIZ_COMPLETED_ACTION, which tells us what is going in the quiz.*/
			int quizDuration = (int) ((Calendar.getInstance().getTimeInMillis() - startQuizTime)/1000);
			//ProblemNode startState = getBrController().getProblemModel().getStartNode();
						
			//Vector<Vector <ProblemEdge>> solutions = new Vector<Vector <ProblemEdge>>();
			//solutions.add(solutionPath != null ? solutionPath : new Vector<ProblemEdge>());
			getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_QUESTION_ANSWER_ACTION_HUMAN, "Quiz"+(getSimStPLE().currentQuizSectionNumber+1)+"."+(getCurrentQuizIndex()), getSimStPLE().solutionSteps(problem,solutionPath), "", isQuizSolutionCorrect, (int) quizDuration);

        
			float pctCorrect = ((float)(getSimStPLE().currentCorrect)/getSimStPLE().currentQuizSection.size());
			this.getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_COMPLETED_ACTION, "Quiz"+(this.getCurrentQuizIndex()+1), ""+pctCorrect, ""+getSimStPLE().currentCorrect+"/"+getSimStPLE().currentQuizSection.size(), quizDuration);
			startQuizTime=0; //reset quiz time so its ready for next quiz problem
		/*End of logging the result*/
		
		/*update working memory variable "quiz incomplete" if solution is correct BUT section is incomplete
		 * this indicates that problem was correct but we have more problems to solve*/
		if (isQuizSolutionCorrect && !allSectionProblemsSolved)
			brController.getMissController().getSimSt().getModelTraceWM().setQuizIncomplete("true"); 		
		
		
		boolean isFinalChallengePassed=false;

		if (allSectionProblemsSolved && this.getSimStPLE().currentQuizSectionNumber!=3 && this.getSimStPLE().currentQuizSectionNumber<3){
			
			currentLevel=this.getSimStPLE().currentQuizSectionNumber;
			
			quizAssessment="Congratulations, you completed the "+getSimStPLE().getSections().get(currentLevel)+" section. <br><br>You can now proceed to the next section, "+getSimStPLE().getSections().get(currentLevel+1)+".<br><br>If you are not ready for the next section, you can review examples."; 

			/*update the quiz indexes and unlock next quiz section*/
			getSimStPLE().sectionPassedUpdateQuizSectionPane();
			
		
			/*reset the lastClickedQuizItem so it will be the first item of the next section*/
			aplus.lastClickedQuizProblem=null;
			
			/*unlock the quiz items in the new section*/
			//getSimStPLE().reloadQuizQuestions(false);
			
		}
		else if (allSectionProblemsSolved && this.getSimStPLE().currentQuizSectionNumber>=3){
			currentLevel=this.getSimStPLE().currentQuizSectionNumber;

			getSimStPLE().sectionPassedUpdateQuizSectionPane();

			isFinalChallengePassed=true;
          			
					
		}
		
		
		
		/**display the assessment popup window*/
		if (allSectionProblemsSolved){
		//	new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), quizAssessment);
			new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), quizAssessment,false,SimStMessageDialog.SHOW_QUIZ_EXAMPLES);

			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).quizPanes.get(this.getSimStPLE().currentQuizSectionNumber-1).setCollapsed(true);

		}
		else{
				
			if (!isQuizSolutionCorrect){
					quizAssessment=quizAssessment+QUIZ_SOLVED_INCORRECTLY_EXAMPLES_ADD.replace("<level>", getSimStPLE().getSections().get(currentLevel));

				new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), quizAssessment,false,SimStMessageDialog.SHOW_TRHREE_BUTTONS);
			//	reviewExamplesShownForThisSection=true;
			}
			else 
				new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), quizAssessment);

		
		}	


		
		/*update the account file*/
		SimStPLE.saveAccountFileAplusCogTutor( getBrController().getMissController().getSimSt().getUserID()+".account");
		
		if (isFinalChallengePassed){
			this.addFinalChallenge();
			getSimStPLE().reloadQuizQuestions(true);
		}
	
         /*clear the solution again*/
         initQuizSolutionHash();
      

        /*if all examples are solved, review next section*/
 		if (allSectionProblemsSolved){
 			//updateNextProblemIndexes(problem);
 			reviewExamplesShownForThisSection=false;
 			
 			//update: On 12/20/2015 we decided not to go to examples unless we failed a problem...
 			//this.reviewExampleSection();
 			
 		}
 		
 		if (!isQuizSolutionCorrect)
 			tmpFiledQuizedExample=tmpQuizSimStExample;
 		else {
 			tmpFiledQuizedExample=null;
 			//updateNextProblemIndexes(problem);
 			aplus.lastClickedQuizProblemIndex++;
 					
 			
 			if (isFinalChallengePassed==true && aplus.lastClickedQuizProblemIndex==15 && getSimStPLE().quizProblems.size()==8){
 				aplus.lastClickedQuizProblemIndex=0;
 	 		//	JOptionPane.showMessageDialog(null, "FACE MPIKAME");

 			}
 			String tmp=aplus.lastClickedQuizProblem;
 			

 			aplus.lastClickedQuizProblem=getSimStPLE().quizProblems.get(aplus.lastClickedQuizProblemIndex);
 			
 			
 			if (finalChallengeReached){
 				aplus.lastClickedQuizProblemIndex=0;
 				aplus.lastClickedQuizProblem=getSimStPLE().cogTutorActualcurrentQuizSection.get(aplus.lastClickedQuizProblemIndex);
 				finalChallengeReached=false;
 			}
 			
 			//just a test, will be deleted...
 			if (aplus.lastClickedQuizProblem.contains("A") && aplus.lastClickedQuizProblem.contains("B"))
 				aplus.lastClickedQuizProblem=getSimStPLE().cogTutorActualcurrentQuizSection.get(aplus.lastClickedQuizProblemIndex);

 			
 			//JOptionPane.showMessageDialog(null, aplus.lastClickedQuizProblem);
 			
 			enterFirstUnsolvedQuizProblemToInterface(false);
 
 			
 		}
 		
 		
 		if (getSimStPLE().getReviewExamplesAfterFail()){
 			getSimStPLE().setReviewExamplesAfterFail(false);
 			this.reviewExampleSection();
 		}
 		
 		
		return true;
	}

	boolean finalChallengeReached=false;
	
	void addFinalChallenge(){
		SimStPLE.passedProblemsList="NA";
		finalChallengeReached=true;
		SimStPLE.cogTutorCurrentProblem=0;
		
		int reply = JOptionPane.showConfirmDialog(null, "<html><p>Congratulations!</p><p>You were able to pass the final challenge!</p><p>I have another set of challenging quiz problems. Would you like to see if you can solve them?</p>", "You did it!", JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {

			getSimStPLE().getSimSt().archiveAndSaveFilesOnLogout();
			SimStPLE.saveAccountFileAplusCogTutor(getSimStPLE().getSimSt().getUserID()+".account");

			getSimStPLE().quizProblems= getSimStPLE().allQuizProblems.get(getSimStPLE().quizLevel);
			getSimStPLE().quizSections = getSimStPLE().allQuizSections.get(getSimStPLE().quizLevel);
			SimStPLE.quizPassed = false;
			SimStPLE.currentProblem = 0;

			
			if (getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode())
				  getSimStPLE().loadAccountInfoAplusCogTutor();
			else
				  getSimStPLE().loadAccountInfo();
			
			
			String newPaneTitle=getSimStPLE().newSectionTitleBase + " "  + (getSimStPLE().quizLevel+1);


			//Add new final challenge section in quiz tab	
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).quizProblemsJLabelIcon.add(new LinkedList<JLabelIcon>());
			QuizPane quizTaskPane = new QuizPane(newPaneTitle,getSimStPLE().logger, brController, ((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).actionListener);
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).quizPanes.add(quizTaskPane);
			((Container) ((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).quizContainer).add(quizTaskPane);
			quizTaskPane.updatePane(false, false);
			quizTaskPane.setCollapsed(true);

			//Add a new final challenge quizmeter graph on tutoring tab
			ClickableProgressBar pb = new ClickableProgressBar(0,getSimStPLE().currentQuizSection.size());
			JLabel levelLabel= new JLabel(newPaneTitle);
			levelLabel.setFont(AplusPlatform.S5_MED_FONT);
			pb.setStringPainted(true);
			Border emptyBorder = BorderFactory.createLineBorder(Color.black,1);
			pb.setBorder(emptyBorder);
			pb.setValue(0);	
			getSimStPLE().quizProg.add(pb);
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).sectionMeterPanel.add(levelLabel);
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).sectionMeterPanel.add(getSimStPLE().quizProg.get(getSimStPLE().currentQuizSectionNumber));

			
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).lastClickedQuizProblem=null;
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).lastClickedQuizProblemIndex=0;
			
			AplusPlatform.splashBLShown=true;


		}
		else{	            			
			JOptionPane.showMessageDialog(null, "<html><p>Thank you for your participation in our study!</p><p>Click OK to close SimStudent now.</p>",
					"You did it!", JOptionPane.INFORMATION_MESSAGE);           
			SimStPLE.saveAccountFile(getSimStPLE().getSimSt().getUserID()+".account");
			System.exit(SimStPLE.QUIZ_COMPLETED_EXIT);	            			
		}
		
	}
	
	
	
	boolean reviewExamplesShownForThisSection=false;
		/**
		 * 
		 * @param example
		 */
	  public void unblockIncorrectSteps(SimStExample example)
	    {

			AplusPlatform aplus=((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform());	
			Component[] components = aplus.quizInterface.getComponents();
			
			
	    	for(int i=0;i<components.length;i++)
	    	{
	    		SimStPLE.setComponentEnabled(true, components[i]);
	    	}
	    	
	    }
	
	long startQuizTime = 0;

	
	/**
	 * Method for adding a Quiz Step.
	 * @param selection
	 * @param action
	 * @param input
	 * @return
	 */
	private boolean addQuizStep(String selection,String action,String input){
		//trace.out(" Step added : " + selection + " "+ input);
		//updateInterfaceElementWithTutorResponse("Done", startColor);
		setInterfaceElementColor("Done",startColor);
		
		restoreFailedQuizSolution();
	        
		if (getQuizSolutionHash()==null){
			this.initQuizSolutionHash();
		}
		
		//trace.out(" In the hash : "+selection+"  value : "+input);
		getQuizSolutionHash().put(selection, new Sai(selection, action, input));
		
		failedQuizProblemSolutionHash.put(selection, new Sai(selection, action, input));

		
		//JOptionPane.showMessageDialog(null, "Quiz hash added step to " + getQuizSolutionHash());

		return true;
	}
	
	
	
	
	private void restoreFailedQuizSolution(){
		
			
		/*if there is a previous solution lingering out, use that*/
        if (this.failedQuizProblemSolutionHash!=null && !this.failedQuizProblemSolutionHash.isEmpty()){	        
        	quizSolutionHash.putAll(failedQuizProblemSolutionHash);
            //failedQuizProblemSolutionHash.clear();
            quizSolutionHash.remove("done");
        }
        
        
	}
	
	public void removeQuizStep(String selection){
		
		restoreFailedQuizSolution();
		
		if (quizSolutionHash!=null){
			//trace.out(" Removed from hash : "+selection);
			quizSolutionHash.remove(selection);
		}
		
		if (failedQuizProblemSolutionHash!=null){
			failedQuizProblemSolutionHash.remove(selection);
		}
		
	}
	
	public void startProblem(){
		brController.getMissController().getSimStPLE().setIsStartStateCompleted(true);
		
		brController.getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering=true;
		brController.getMissController().getSimStPLE().setIsStartStateCompleted(false);
					
						
			/*read the problem name from the interface*/
			String problemName = brController.getMissController().getSimStPLE().getSsInteractiveLearning().createName(brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
				 
			
			/*disable the start state elements*/
			brController.getMissController().getSimStPLE().setFocusOfStartStateElements(false);
			/*update the SimStCognitiveTutor latest given problem variable*/
			brController.getMissController().getSimStPLE().getSsCognitiveTutor().setLastGivenProblem(SimSt.convertFromSafeProblemName(problemName));		
			/*give the new problem (just like when in CogTutor-Control)*/
			brController.getMissController().getSimStPLE().getSsCognitiveTutor().giveProblem(SimSt.convertFromSafeProblemName(problemName));
			
			/*enable the restart button*/
		 	AplusPlatform aplus=((AplusPlatform) brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform());
		 	//aplus.restartButton.setEnabled(true);
		 	aplus.setRestartButtonEnabled(true);
		 	
		 	brController.getMissController().getSimStPLE().setFocusOfStartStateElements(false);
		
	
	}	
	/**
	 * Method to process Done (i.e. if done is correct, then it gives a new problem)
	 * @param selection
	 * @param action
	 * @param input
	 * @return always returns false, because we do not want (after this method is called) CTAT to proceed to update the interface.
	 */
	public boolean processDone(String selection, String action, String input){
		
    	brController.getMissController().getSimStPLE().setIsRestartClicked(false);

		
		/*When done is clicked don't process it (there is another method for that). This is here just as a precaution, in theory after Quiz done we should never find ourselves here */
		if (getQuizSolving()) return false;
		
		boolean isDoneCorrect=processInterfaceSAI(selection,action,input);

		
		/*notify working memory that problem is solved*/
		this.brController.getMissController().getSimSt().getModelTraceWM().setProblemStatus("solved");

		
		if (!isDoneCorrect){ 
			AskHint hint = getBrController().getMissController().getSimSt().askForHint(getBrController(),getBrController().getSolutionState().getCurrentNode());
			if (hint.getInput().contains("rf")){				
				isDoneCorrect=true;
				setInterfaceElementColor("Done",correctColor);	
			}
			
		}
		
		if (isDoneCorrect)  {	 

			/*before giving next problem, send the "next problem" clicked (even though its internal), so the "back to the future" mechanism can unlock the "new problem entered" */ 
			this.getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.NEXT_PROBLEM_BUTTON_ACTION,"");
			
			
			/*tell the model-tracer that problem is done */
			// brController.getAmt().handleInterfaceAction("solved", "implicit", "-1");
			brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(null);

			/*if all skills are mastered then take care of this*/
			if (!getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode() &&  getSimStBKT().isMastered() && getSimStLMS().allSkillsChecked())
				promoteToNextLevel();
			
			/*In cogTutor mode display the next problem, else show window that problem is finished.*/
			if (!getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
				//click the next problem button
				this.getSimStPLE().nextProblem(false);
				
				 
				//give the next problem
				this.giveNextProblem(true); 
			}
			else{
				//String hintType=getCurrentMTHintType();
				//String givenMessageTopic= hintType.equals(SimStHintLogAgent.QUIZ)? SimStConversation.QUIZ_COG_TUTOR_TOPIC : SimStConversation.NEW_PROBLEM_COG_TUTOR_TOPIC;
				String givenMessageTopic = SimStConversation.COG_TUTOR_AFTER_PROBLEM_TOPIC;
				
				
				new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), getSimStPLE().getConversation().getMessage(givenMessageTopic));
				/*clear the interface so its ready for the next problem*/
				 this.getSimStPLE().nextProblem(false);
				 							 
				 /*enable problem entering*/
				 lockProblemEntering=false;
				 
				 AplusPlatform aplus=((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform());

				 //aplus.restartButton.setEnabled(false);
				 aplus.setRestartButtonEnabled(false);
		        	
				getSimStPLE().unblockAllButStartState(false);
				getSimStPLE().getSsCognitiveTutor().clearStartStateFromStudentInterface();
				getSimStPLE().setFocusOfStartStateElementsStudentInterface(true);
				aplus.aplusControlConfirmationFrameClicked=false;
		        	
			}			

		}

	
		
		/*returning false meams whoever calls this method will not proceed.*/
		return false;
	}
	

	
	static public String firingNode="";
	
	private String getCurrentMTHintType(){
		
		firingNode="";
		
		getSimStPLE().getSimSt().getBrController().getAmt().handleInterfaceAction("activations", "MetaTutorClicked", "-1");
		//return getSimStPLE().getSimSt().getBrController().getAmt().getaPlusModelTracing().getMatchedNode().getName();
		
		try{	
			while(firingNode.equals(""))
			{
					Thread.sleep(200);
			}
		}
		catch(InterruptedException ex){
			
		}
		
		return getSimStLogger().getHintLogAgent().getCurrentHintType(firingNode);
		
		
	
		
	}

	

	/**
	 * 
	 * @param selection
	 * @param action
	 * @param input
	 * @return true if caller should proceed or not
	 */
	public boolean processInterfaceSAI(String selection, String action, String input){

		boolean returnValue=true;

		/*Paint "done" black because if it was incorrect in the past, we don't want to see it red*/
		updateInterfaceElementWithTutorResponse("Done", startColor,false);

		ProblemNode problemNode=getBrController().getSolutionState().getCurrentNode();
		String problemName=getBrController().getProblemName();       		
		String result=getBrController().getMissController().getSimSt().inquiryRuleActivation(problemName, problemNode, selection,action,input);         	              	
		if (trace.getDebugCode("cogTutor")) trace.out("cogTutor", "    ---> Result is " + result);

		int correctness=0;
		// if action is incorrect then do not proceed. 	 
		if (!result.equals(EdgeData.CORRECT_ACTION)){  		
			updateInterfaceElementWithTutorResponse(selection, incorrectColor,false);
			returnValue=false;
			correctness=0;
			previousIncorrectStepExists=true;
		}
		else{
			updateInterfaceElementWithTutorResponse(selection, correctColor,true);
			correctness=1;
		}

		/*Update the quiz interface based on the BKT. Update happens only when skill is correct*/
        if (getSimStPLE().getSimSt().isSsCogTutorMode() && !getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode() && correctness==1){
        	
        	
     	   AskHint hint = getSimStPLE().getSimSt().getCorrectSAI(getBrController(), problemNode);
     	   String skillUsed=hint.skillName;
     	   
     	   if (correctness==1 && selection.contains("dorminTable3")){
     		   if (!input.contains(skillUsed)){
     			   	String[] parts=input.split(" ");
     			   	skillUsed=parts[0];   			   
     		   }	   
     	   }
     	   
     	   
     	   if (selection.equalsIgnoreCase("Done")) skillUsed="done";
     	  
     	   /*If we had a previous incorrect step, blame that one...*/
     	   if (previousIncorrectStepExists || getStepHintGiven()) correctness=0;
     	   
     	 	 
     	   /*update the BKT parameters*/
     	   getSimStBKT().update(correctness, skillUsed);
     	   getSimStBKT().saveBKTParametersFile();
     	   
     	   /*reset the flags indicating we have a previous incorrect step or hint*/
     	   previousIncorrectStepExists=false;	
     	   setStepHintGiven(false);
     	   
     	   /*Update the progress bar*/
     	  if (getSimStPLE().quizProg.get(getSimStPLE().currentQuizSectionNumber)!=null){	
				int value = getSimStBKT().getAverageMastery();
				getSimStPLE().quizProg.get(getSimStPLE().currentQuizSectionNumber).setValue(value);
			}

     	  

        } 
		
     
		
		return returnValue;

	}

	
	boolean previousIncorrectStepExists=false;
	public boolean getPreviousIncorrectStepExists(){return previousIncorrectStepExists;}
	public void setPreviousIncorrectStepExists(boolean flag){this.previousIncorrectStepExists=flag;}
	
	
	boolean stepHintGiven=false;
	public void setStepHintGiven(boolean flag){this.stepHintGiven=flag;}
	public boolean getStepHintGiven(){return stepHintGiven;}
	
	/**
	 * Utility method to update an interface element
	 * @param selection
	 * @param color
	 */
	void updateInterfaceElementWithTutorResponse(String selection, Color color, boolean correct){

		Object widget= getBrController().lookupWidgetByName(selection);			
		if (widget instanceof JCommTable.TableCell) {
			((JCommTable.TableCell) widget).setForeground(color);
			
			((JCommTable.TableCell) widget).repaint();
			if (correct) {
				((JCommTable.TableCell) widget).setEnabled(false);
				((JCommTable.TableCell) widget).setDisabledTextColor(color);
			
			}
			
		}
		else if (widget instanceof JCommButton){
			((JCommButton) widget).setForeground(color);
			((JCommButton) widget).repaint();
		}
		
		
	}

	
	/**
	 * Utility method to update an interface element
	 * @param selection
	 * @param color
	 */
	void setInterfaceElementColor(String selection, Color color){

		Object widget= getBrController().lookupWidgetByName(selection);

		if (widget instanceof JCommTable.TableCell) {
			((JCommTable.TableCell) widget).setForeground(color);
			((JCommTable.TableCell) widget).repaint();
		}
		else if (widget instanceof JCommButton){
			((JCommButton) widget).setForeground(color);
			((JCommButton) widget).setBorder(BorderFactory.createEmptyBorder());
			((JCommButton) widget).repaint();
		}


	}
	
	/**
	 * Utility method to update an interface element
	 * @param selection
	 * @param color
	 */
	void enableQuizInterfaceElement(String selection, boolean flag){

		Object widget= getBrController().lookupWidgetByName(selection);
		if (widget instanceof JCommButton){
			((JCommButton) widget).setForeground(Color.BLACK);
			((JCommButton) widget).setEnabled(flag); 
			((JCommButton) widget).repaint();


		}


	}

	
	/**
	 * Based on storyboard, these actions should happen
	 * 1. Display window that level is mastered
	 * 2. Switch to examples
	 * 3. Select new level
	 * 4. Display message to read new level
	 */
	private void promoteToNextLevel(){

		
		
		
		ArrayList<String >currentQuizSection = new ArrayList<String>();
		for(int i=0;i<getSimStPLE().quizProblems.size();i++)
		{
			if(getSimStPLE().quizSections.get(i) == getSimStPLE().currentQuizSectionNumber)
			{
				currentQuizSection.add(getSimStPLE().quizProblems.get(i));
			}
		}	
		SimStPLE.currentOverallProblem+=currentQuizSection.size();
		
		currentLevel++;
			

		this.getSimStPLE().currentQuizSectionNumber++;

		
		
		if (getSimStPLE().getSections().size()==this.getSimStPLE().currentQuizSectionNumber){
			
			JOptionPane.showMessageDialog(null, "<html><p>Thank you for your participation in our study!</p><p>Click OK to close SimStudent now.</p>",
					"You did it!", JOptionPane.INFORMATION_MESSAGE);           
			SimStPLE.saveAccountFile(getSimStPLE().getSimSt().getUserID()+".account");
			System.exit(SimStPLE.QUIZ_COMPLETED_EXIT);	            			
		
		}

		
		currentLevel=this.getSimStPLE().currentQuizSectionNumber;

		SimStPLE.saveAccountFile(getSimStPLE().getSimSt().getUserID()+".account");

		String currentLevelString=getSimStPLE().getSections().get(currentLevel-1);
		String nextLevelString=getSimStPLE().getSections().get(currentLevel);
		currentLevelString=currentLevelString.replaceAll("-", "");
		nextLevelString=nextLevelString.replaceAll("-", "");
		//String promoteMessage="Congratulations, you have mastered all skills for "+currentLevelString+". <br><br>You can now proceed to the next section, "+nextLevelString+".<br><br>If you are not ready for the next section, you can review examples."; 
		
		String promoteMessage=getSimStPLE().getConversation().getMessage(SimStConversation.COG_TUTOR_SECTION_PASSED_TOPIC);
		promoteMessage=promoteMessage.replace("<level>", currentLevelString);
		promoteMessage=promoteMessage.replace("<nextLevel>", nextLevelString);
		//String promoteMessage="Congratulations, you have mastered all skills for "+currentLevelString+". <br> Before you proceed to the next section, "+nextLevelString+", its a good idea to review some examples."; 
			
		new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), promoteMessage,false,false, SimStMessageDialog.REVIEW_EXAMPLES);
		
		getSimStLMS().setCurrentSection(this.getSimStPLE().currentQuizSectionNumber);
		//update BKT parameters for next section.
		  if (getSimStPLE().quizProg.get(getSimStPLE().currentQuizSectionNumber)!=null){
			    getSimStBKT().resetBKT_view_params();
				int value = getSimStBKT().getAverageMastery();
				getSimStPLE().quizProg.get(getSimStPLE().currentQuizSectionNumber).setValue(value);
			}
		  
		/*  if (getSimStPLE().getReviewExamplesAfterFail()){
	 			getSimStPLE().setReviewExamplesAfterFail(false);
	 			this.reviewExampleSection();
	 		}
		*/
		  
		reviewExampleSection();
	}
	
	public void reviewExampleSection(){
		reviewExampleSection(false);
	}
	
	public void reviewExampleSection(boolean isAplusJustLaunched){
		currentLevel=this.getSimStPLE().currentQuizSectionNumber;
		displayAppropriateExample(currentLevel);	
	
		try{
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).getAplusTabs().setSelectedIndex(AplusPlatform.EXAMPLES_TAB_INDEX);

		}
		catch(Exception ex){
			
		}
		
		if (getSimStPLE().getShowPopupAtExamples())
			new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), contextualizeExamplesMessage(currentLevel,isAplusJustLaunched),true);
	}
	
	
	
	
	public void goToPractice(){
		
		try{
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).getAplusTabs().setSelectedIndex(0);

		}
		catch(Exception ex){
			
		}
		
	}
	
	
	public void gotoQuizSection(){
		currentLevel=this.getSimStPLE().currentQuizSectionNumber;
	
		
		try{
			((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).getAplusTabs().setSelectedIndex(AplusPlatform.EXAMPLES_TAB_INDEX+1);

		}
		catch(Exception ex){
			
		}
		
		new SimStMessageDialog(new Frame(),this.getSimStPLE().getSimSt().getSimStLogger(), "You solve quiz problems on the right, and see how far you can go. Good luck!",false);
	}
	
	/**
	 * Method to show the appropriate example
	 * @param level
	 */
	private void displayAppropriateExample(int level){
		
		AplusPlatform aplus=((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform());
		List<JXTaskPane> examplePanes=aplus.examplePanes;
		/*Collapse all */
		for (JXTaskPane pane : examplePanes){
				pane.setCollapsed(true);
		}
	

		
		/*Expand the proper level and display the first example*/
		if (level==3){
			aplus.examplePanes.get(level-1).setCollapsed(false);		
			aplus.exampleProblems.get(level-1).get(0).actionPerformed(null);
		}
		else{
			aplus.examplePanes.get(level).setCollapsed(false);		
			aplus.exampleProblems.get(level).get(0).actionPerformed(null);
		}
			
	}
	
	/**
	 * Method to contextualize the message given to student when 
	 * being transfered to the Examples tab, based on the name of
	 * the current level.
	 * @param level
	 * @param isAplusJustLaunched 
	 * @return
	 */
	private String contextualizeExamplesMessage(int level,boolean isAplusJustLaunched){

		String levelName=getSimStPLE().getSections().get(level);			
		levelName=levelName.replaceAll("-", "");
		
		String msg="";
		if (getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode()){
			msg=getSimStPLE().getConversation().getMessage(SimStConversation.APLUS_CONTROL_QUIZ_SECTION_PASSED_TOPIC);
		}
		else {
			if (isAplusJustLaunched)
				msg=getSimStPLE().getConversation().getMessage(SimStConversation.COG_TUTOR_SECTION_PASSED_EXAMPLES_START_TOPIC);
			else 
				msg=getSimStPLE().getConversation().getMessage(SimStConversation.COG_TUTOR_SECTION_PASSED_EXAMPLES_TOPIC);
			
		}
		
		return msg.replace("<level>", levelName);
		//return this.REVIEW_LEVEL_MSG.replace("<level>", levelName);

	}

	public SimStExample tmpFiledQuizedExample=null;
	
	int lastQuizProblemViewed=-1;
	
	/**
	 * Enter on quiz interface the first unsolved quiz problem
	 */
	public void enterFirstUnsolvedQuizProblemToInterface(boolean emptyInterface){
		
		AplusPlatform aplus=((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform());	
		this.getSimStPLE().unBlockQuiz(true);
		
		startQuizTime=0;
		this.setCurrentQuizIndex(this.getSimStPLE().getCurrentQuizSectionNumber());	
		
		
		aplus.setSpeech("");
		
		String problemName= null;
	
		if (aplus.lastClickedQuizProblem!=null){
			/*take the problem name from lastClickedQuizProblem*/
				problemName=aplus.lastClickedQuizProblem;
		}
		else {
			 /*In theory we should never be here: now lastClickedQuizPRoblem is updated both a) when student clicks on quiz title, b) on reloadQuizQuestions when
			 * loading the problems at the begining. As a result, lastClickedQuizPRoblem always has either a) what student clicked or b) the first unsolved
			 * quiz problem. I keep this code here just for safety, (will be removed)*/
			problemName=(String) this.getSimStPLE().unsolvedProblemsQueue.poll();

			if (problemName==null)	{		

				problemName=getSimStPLE().currentQuizSection.get(0); /*get the first problem of the current section*/
				
			}
				
		}
	
		
		SimStExample tmp;
		/* if this example was solved in the past,  get the SimStExample and fill the interafce. If not, then just make start
		 * state and fill the interface with that.*/
		if (tmpFiledQuizedExample!=null && !emptyInterface)
			tmp=tmpFiledQuizedExample;
		else{
			tmp=new SimStExample();
			tmp.addStartStateFromProblemName(problemName, getSimStPLE().getStartStateElements());
		}
		getSimStPLE().getSsInteractiveLearning().clearQuizGraph();
		getSimStPLE().getSsInteractiveLearning().createStartStateQuizProblem(problemName);
	
		ExampleAction problem = aplus.new ExampleAction(tmp,true);
		
		
		problem.fillInExample(aplus.quizInterface,true,true);
		
		setQuizSolving(true);
		
		if (failedQuizProblemSolutionHash!=null && !this.failedQuizProblemSolutionHash.isEmpty()){
			restoreFailedQuizSolution();
		}
		else{	
			this.initQuizSolutionHash();
			this.initFailedQuizSolutionHash();

		}
		
		
	}

	
	
	
	
}
