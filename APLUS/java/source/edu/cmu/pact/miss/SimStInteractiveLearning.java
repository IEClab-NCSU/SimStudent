package edu.cmu.pact.miss;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Value;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import pact.CommWidgets.JCommWidget;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.jess.MTRete;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform;
import edu.cmu.pact.miss.PeerLearning.AplusSpotlight;
import edu.cmu.pact.miss.PeerLearning.SimStConversation;
import edu.cmu.pact.miss.PeerLearning.SimStExplainWhyNotDlg;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStRememberBubble;
import edu.cmu.pact.miss.PeerLearning.SimStPLE.QuestionAnswers;
import edu.cmu.pact.miss.PeerLearning.SimStPeerTutoringPlatform;
import edu.cmu.pact.miss.PeerLearning.SimStSolver;
import edu.cmu.pact.miss.PeerLearning.SimStSolver.SolutionStepInfo;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStEdge;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStGraphNavigator;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class SimStInteractiveLearning implements Runnable {

	public final String UNLABELED_SKILLNAME = "unlabeled-interactive-learning";

	private SimSt simSt;

	public SimSt getSimSt() {
		return simSt;
	}

	public void setSimSt(SimSt simSt) {
		this.simSt = simSt;
		if (simSt.getSsInteractiveLearning() == null)
			simSt.setSsInteractiveLearning(this);
	}
	
	// TODO: stop using static
	public static BR_Controller brController; // used by simulateCellTextEntry()

	public static void setBrController(BR_Controller br){
		SimStInteractiveLearning.brController = br;
	}
	
	public static BR_Controller getBrController(SimSt simst)
	{
		return SimStInteractiveLearning.brController/*SimStInteractiveLearning.simstLookup.get(simst)*/;
	}
	
	// Logger to which simStudent specific messages can be sent
	private SimStLogger logger;

	public SimStLogger getLogger() {
		return logger;
	}
	
	final int CHANCE = 60;

	public void setLogger(SimStLogger log) {
		logger = log;
	}

	/** Graph to maintain the quiz nodes and edges */
	private SimStProblemGraph quizGraph = null;

	public void setQuizGraph(SimStProblemGraph quizGraph) {
		this.quizGraph = quizGraph;
	}

	public SimStProblemGraph getQuizGraph() {
		return quizGraph;
	}
	
	public void clearQuizGraph(){
		quizGraph=null;
	}
	String runType = System.getProperty("appRunType");
	private Hashtable<String, String> filledInComponents = new Hashtable<String, String>();

	// used by
	// DemonstrateModeMessageHandler.processDemonstrateInterfaceAction(),
	// which does not have an instance of SimStInteractiveLearning
	private boolean runningFromBrd = false;
	private boolean killMessageReceived = false; 

	public boolean isRunningFromBrd() {
		return runningFromBrd;
	}

	public void setRunningFromBrd(boolean runningFromBrd) {
		this.runningFromBrd = runningFromBrd;
	}

	// used by DemonstrateModeMessageHandler.doZeroMatchedNodes()
	public static boolean isWaitingForDemonstration = false;
	// used by SimSt.changeInstructionName()
	public static boolean isWaitingForRuleLearned;

	// used by hereIsTheRule(), which is static
	private static MessageDrop messageDrop;

	public MessageDrop getMessageDrop() {
		return messageDrop;
	}

	// Set to be true when PLE is running and SimSt is working on the Quiz
	// so that no hint request would be made
	private boolean takingQuiz = false;

	public void setTakingQuiz(boolean flag) {
		takingQuiz = flag;
	}

	public boolean isTakingQuiz() {
		return takingQuiz;
	}

	// Set to be true when the SimStudent fails to learn on a type-in step for
	// the
	// first time and calls for use of alternate FoA.
	private boolean typeInStepWithNewFoA = false;

	public boolean isTypeInStepWithNewFoA() {
		return typeInStepWithNewFoA;
	}

	public void setTypeInStepWithNewFoA(boolean typeInStepWithNewFoA) {
		this.typeInStepWithNewFoA = typeInStepWithNewFoA;
	}

	// Used to track length of entire time spent on problem, including undos
	private long problemStartTime = 0;

	public void setProblemStartTime(long time) {
		problemStartTime = time;
		setProblemRecentTime(problemStartTime);
	}

	public long getProblemStartTime() {
		return problemStartTime;
	}

	// Start time of the problem or the most recent restart time
	private long problemRecentTime = 0;

	public void setProblemRecentTime(long time) {
		problemRecentTime = time;
	}

	public long getProblemRecentTime() {
		return problemRecentTime;
	}

	public boolean foaCorrectness = true;

	public boolean isFoaCorrectness() {
		return foaCorrectness;
	}

	private void setFoaCorrectness(boolean foaCorrectness) {
		this.foaCorrectness = foaCorrectness;
	}

	private AskHint hint; // Making it global to test

	public AskHint getHint() {
		return hint;
	}
	
	private String givenProblemName;
	
	
	public String getGivenProblemName() {
		return givenProblemName;
	}

	public void setGivenProblemName(String givenProblemName) {
		this.givenProblemName = givenProblemName;
	}
	
	private String hintInformation;

	public String getHintInformation() {
		return hintInformation;
	}

	public void setHintInformation(String hintInformation) {
		this.hintInformation = hintInformation;
	}
	
	private int numStepsPerformed = 0;
	private String step;
	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	private long stepStartTime;
	
	public long getStepStartTime() {
		return stepStartTime;
	}

	public void setStepStartTime(long stepStartTime) {
		this.stepStartTime = stepStartTime;
	}

	private String solution;
	
	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}
	
	public boolean correct;
	
	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	
	public ProblemNode nextCurrentNode = null;
	public RuleActivationNode backupRan = null;
	public int ruleQueryCounter = 0;
	public int firstStategy = 0;

	public boolean explanationGiven=false;
	public boolean getExplanationGiven(){return this.explanationGiven;}
	public void setExplanationGiven(boolean flag){this.explanationGiven=flag;}
	
	//public static Hashtable<SimSt,BR_Controller> simstLookup = new Hashtable<SimSt,BR_Controller>();
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public SimStInteractiveLearning(SimSt simSt) {
		setSimSt(simSt);
		//SimSt.setMissController(simSt);
		brController = simSt.getBrController();
		//simstLookup.put(simSt, brController);
		setLogger(new SimStLogger(brController));

		// If Tutalk is enabled, then get its parameters from SimStudent.
		if (simSt.getTutalkEnabled()) {
			// Currently I haven't planned anything for the params
			String[] tutalkParam = simSt.getTutalkParam().split(",");
			String tutalkUid = simSt.getUserID();
			tutalkBridge = new SimStTutalk(tutalkUid, this);
			tutalkBridge.initialize();
		}
	}

	public SimStTutalk getTutalk() {
		return tutalkBridge;
	}

	public void run() {
		runInteractiveLearning();
	}

	/*
	 * 22 May 2007 batch mode: extracts the problem name from the BRD, and calls
	 * ssInteractiveLearning with that In case BRD is used for Test or Hint, we
	 * should set currentBrdPath.
	 */
	public void ssInteractiveLearningWithBRD(String brdPath) {

		// String[] sp = brdPath.split("/");
		// String training = sp[sp.length-1];
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Inside ssInteractiveLearningWithBRD");
		setRunningFromBrd(true);

		
		/*If for some reason we have nodes here, then reset the controller. */
		//if (brController.getProblemModel().getNodeCount()>0)
		//	brController.reset();
		
		LoadFileDialog.doLoadBRDFile(getBrController(getSimSt()), brdPath, "", true);
		
		
		// this needs to be done *again*
		getBrController(getSimSt()).getCtatModeModel().setAuthorMode(
				CtatModeModel.DEMONSTRATING_SOLUTION);

		// Make topLevelGroup unordered
		ExampleTracerGraph exTracerGraph = getBrController(getSimSt()).getProblemModel()
				.getExampleTracerGraph();
		GroupModel groupModel = exTracerGraph.getGroupModel();
		LinkGroup topLevelGroup = exTracerGraph.getGroupModel()
				.getTopLevelGroup();
		groupModel.setGroupOrdered(topLevelGroup, false);

		ProblemNode startNode = getBrController(getSimSt()).getProblemModel().getStartNode();
		String problemName = startNode.getName();
		ssInteractiveLearningOnProblem(problemName);
	}

	// Need an API for Interactive Learning on a give equation
	// e.g. ssInteractiveLearning("3x = 9");
	public void ssInteractiveLearningOnProblem(String problemName) {

		// Enter a problem & create a start state
		createStartStateOnProblem(problemName);
		// call interactive learning on it
		runInteractiveLearning();
	}

	
	/**
	 * Create a new graph for the quizProblem, make a new node using the name of
	 * the problem and add it to the graph.
	 * 
	 * @param quizProblem
	 */
	public void createStartStateQuizProblem(String quizProblem) {

		filledInComponents.clear();
		if (quizGraph == null) {
			String safeProblemName = SimSt
					.convertToSafeProblemName(quizProblem);
			quizGraph = new SimStProblemGraph();
			SimStNode startNode = new SimStNode(safeProblemName, quizGraph);
			quizGraph.setStartNode(startNode);
			quizGraph.addSSNode(startNode);
		}
	}

	// Create a start state for the problem string on the input of form LHS=RHS,
	// split by =
	public void createStartStateOnProblem(String problemName) {
		
		filledInComponents.clear();
		if (trace.getDebugCode("miss"))
			trace.out("miss", "createStartStateOnProblem: " + problemName);

		
		// TODO: generalize this, so that the argument is a list of <selection,input> pairs
		try {
			// if no BRD is loaded, create the start state
			if (brController.getProblemModel().getStartNode() == null) {
				
				trace.out("webAuth","webAuthoringMode is " + this.getSimSt().isSsWebAuthoringMode());				
				if (this.getSimSt().isSsWebAuthoringMode()==false){/*on WebAuthoring mode do not update the interface with start state*/
					String[] sp = problemName.split("=");
		
					String c1r1Value = sp[0].trim();
					String c2r1Value = sp[1].trim();

					if (brController.getMissController().isPLEon()) {
						SimStPLE ple = getBrController(getSimSt()).getMissController().getSimStPLE();
						ArrayList<String> startElements = ple.getStartStateElements();
						

						for (int i = 0; i < startElements.size() && i < sp.length; i++) {
							simulateCellTextEntry(startElements.get(i),sp[i].trim());
							
							/*if we are in cogTutor mode, also update the modeltracer*/
							if (getSimSt().isSsCogTutorMode() && getSimSt().isSsMetaTutorMode()){
								brController.getAmt().handleInterfaceAction(startElements.get(i), "UpdateTable", sp[i].trim());
							}
						}
					} 
					else {
						try {
							// update the cell text
							simulateCellTextEntry("dorminTable1_C1R1", c1r1Value);
							simulateCellTextEntry("dorminTable2_C1R1", c2r1Value);
						} catch (Exception e) {
							// TODO Fix this hacky change to something less
							// interface dependent rather than by
							// catching an exception
							trace.out("ss", "Using old setup interface format due to failure & no start state info");
							simulateCellTextEntry("dorminTable1_C1R1", c1r1Value);
							simulateCellTextEntry("dorminTable1_C2R1", c2r1Value);
						}
					}
				}
				
								
				// Replace symbols unallowed in problem names with stand-in
				// character
				String normalProblemName = SimSt
						.convertToSafeProblemName(problemName);
				if (trace.getDebugCode("ss"))
					trace.out("ss", "Problem Name created: " + normalProblemName);

				
				// Problem starts when created
				setProblemStartTime(Calendar.getInstance().getTimeInMillis());

				brController.createStartState(normalProblemName);
				
			} else {
							
				// Problem is already created- use existing start state
				ProblemNode node = brController.getProblemModel()
						.getStartNode();
				if (trace.getDebugCode("miss"))
					trace.out("miss",
							"ssInteractiveLearningOnProblem: startNode = "
									+ node);
			}
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "ssInteractiveLearningOnProblem: - - - - Exception - - - -");
			e.printStackTrace();
			logger.simStLogException(e,"ssInteractiveLearningOnProblem: - - - - Exception - - - -");
		}
	}

	
	
public void fillInQuizProblem(String problemName) {
		
		filledInComponents.clear();
		

		
		// TODO: generalize this, so that the argument is a list of <selection,input> pairs
		try {
			// if no BRD is loaded, create the start state
			if (getBrController(getSimSt()).getProblemModel().getStartNode() == null) {
				
				trace.out("webAuth","webAuthoringMode is " + this.getSimSt().isSsWebAuthoringMode());				
				if (this.getSimSt().isSsWebAuthoringMode()==false){/*on WebAuthoring mode do not update the interface with start state*/
					String[] sp = problemName.split("=");
		
					String c1r1Value = sp[0].trim();
					String c2r1Value = sp[1].trim();

					if (getBrController(getSimSt()).getMissController().isPLEon()) {
						SimStPLE ple = getBrController(getSimSt()).getMissController().getSimStPLE();
						ArrayList<String> startElements = ple.getStartStateElements();
						for (int i = 0; i < startElements.size() && i < sp.length; i++) {
							simulateCellTextEntry(startElements.get(i),sp[i].trim());
							
							
						}
					} 
					else {
						try {
							// update the cell text
							simulateCellTextEntry("dorminTable1_C1R1", c1r1Value);
							simulateCellTextEntry("dorminTable2_C1R1", c2r1Value);
						} catch (Exception e) {
							// TODO Fix this hacky change to something less
							// interface dependent rather than by
							// catching an exception
							trace.out("ss", "Using old setup interface format due to failure & no start state info");
							simulateCellTextEntry("dorminTable1_C1R1", c1r1Value);
							simulateCellTextEntry("dorminTable1_C2R1", c2r1Value);
						}
					}
				}
				

			} 
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "ssInteractiveLearningOnProblem: - - - - Exception - - - -");
			e.printStackTrace();
			logger.simStLogException(e,"ssInteractiveLearningOnProblem: - - - - Exception - - - -");
		}
	}




	// Put together a name for the problem based on all the current values which
	// the given components hold
	public String createName(Component[] list) {
		String compNames = "";
		for (int i = 0; i < list.length; i++) {
			// For CommTables, concat all non-empty cells
			if (list[i] instanceof JCommTable) {
				JCommTable table = (JCommTable) list[i];
				TableExpressionCell[][] cells = table.getCells();
				for (int j = 0; j < cells.length; j++) {
					for (int k = 0; k < cells[j].length; k++) {
						String namePart = cells[j][k].getText();
						if (namePart.length() > 0) {
							if (compNames.length() > 0)
								compNames += "_" + namePart;
							else
								compNames += namePart;
						}
					}
				}
			}
			// For components, recursively find names for all their contents and
			// concatenate these on
			else if (list[i] instanceof JComponent) {
				JComponent comp = (JComponent) list[i];
				String namePart = createName(comp.getComponents());
				if (compNames.length() > 0 && namePart.length() > 0) {
					compNames += "_" + namePart;
				} else if (namePart.length() > 0) {
					compNames += namePart;
				}
			}
		}
		return compNames;
	}
	
	/**
	 * Method that takes a list of components, a component name and returns
	 * the value of that component.
	 * @param list
	 * @param componentName
	 * @return
	 */
	public String lookUpWidgetValue(Component[] list,String componentName) {
		String compNames = "";
		for (int i = 0; i < list.length; i++) {
			// For CommTables, concat all non-empty cells
			if (list[i] instanceof JCommTable) {
				JCommTable table = (JCommTable) list[i];
				TableExpressionCell[][] cells = table.getCells();
				for (int j = 0; j < cells.length; j++) {
					for (int k = 0; k < cells[j].length; k++) {
						if (cells[j][k].getCommName().equals(componentName)){
							String namePart = cells[j][k].getText();
							if (namePart.length() > 0) {
								if (compNames.length() > 0)
									compNames += "_" + namePart;
								else
									compNames += namePart;
							}
						}
						
					}
				}
			}
			// For components, recursively find names for all their contents and
			// concatenate these on
			else if (list[i] instanceof JComponent) {
				JComponent comp = (JComponent) list[i];
				String namePart=lookUpWidgetValue(comp.getComponents(),componentName);
				if (compNames.length() > 0 && namePart.length() > 0) {
					compNames += "_" + namePart;
				} else if (namePart.length() > 0) {
					compNames += namePart;
				}
			}
		}
		return compNames;
	}
	
	public Component tmpComponent=null;
	
	//code in progress
	public Component getStudentInterfaceWidget(Component[] list,String componentName){
		Component returnComponent=null;
		
		for (int i = 0; i < list.length; i++) {

			if (list[i] instanceof JCommTable) {
				JCommTable table = (JCommTable) list[i];
				TableExpressionCell[][] cells = table.getCells();
				for (int j = 0; j < cells.length; j++) {
					for (int k = 0; k < cells[j].length; k++) {
						if (cells[j][k].getCommName().equals(componentName)){
							tmpComponent=cells[j][k];
						}					
					}
				}
			}
			else if (list[i] instanceof JComponent) {
				JComponent comp = (JComponent) list[i];
				getStudentInterfaceWidget(comp.getComponents(),componentName);
				
			}
		}
		
		return returnComponent;
	}
	

	
	public String setWidgetValue(Component[] list,String componentName,String stringValue) {
		String compNames = "";

		for (int i = 0; i < list.length; i++) {
			// For CommTables, concat all non-empty cells
			if (list[i] instanceof JCommTable) {
				JCommTable table = (JCommTable) list[i];
				TableExpressionCell[][] cells = table.getCells();
				for (int j = 0; j < cells.length; j++) {
					for (int k = 0; k < cells[j].length; k++) {
						//trace.out("ss","comparing " + cells[j][k].getCommName() + " and " + componentName);
						if (cells[j][k].getCommName().equals(componentName)){
							cells[j][k].setText(stringValue);
							
							String namePart = cells[j][k].getText();
							
							if (namePart.length() > 0) {
								if (compNames.length() > 0)
									compNames += "_" + namePart;
								else
									compNames += namePart;
							}
						}
						
					}
				}
			}
			// For components, recursively find names for all their contents and
			// concatenate these on
			else if (list[i] instanceof JComponent) {
				JComponent comp = (JComponent) list[i];
				String namePart=setWidgetValue(comp.getComponents(),componentName,stringValue);
				if (compNames.length() > 0 && namePart.length() > 0) {
					compNames += "_" + namePart;
				} else if (namePart.length() > 0) {
					compNames += namePart;
				}
			}
		}
		return compNames;
	}
	
	
	// Create a start state using all the cells currently filled into the
	// interface
	public void createStartState(JComponent studentInterface) {

		createStartState(studentInterface, false);
		
	}

	
    
	// Create a start state using all the cells currently filled into the
	// interface
	public void createStartState(JComponent studentInterface,
			boolean problemRestart) {
		
		
		filledInComponents.clear();
		if (trace.getDebugCode("miss"))
			trace.out("miss", "createStartState....");

		// calculate name based on filled-in components
		String problemName = createName(studentInterface.getComponents());
		
		/**
		 * Reset the working memory element 
		 */
		getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().setConsecutiveResourceReview(0);;

		String normalProblemName = "";
		try {
			// if no BRD is loaded, create the start state
			if (getBrController(getSimSt()).getProblemModel().getStartNode() == null) {

				// Replace symbols unallowed in problem names with stand-in
				// character
				normalProblemName = SimSt.convertToSafeProblemName(problemName);

				// Problem starts when created
				setProblemStartTime(Calendar.getInstance().getTimeInMillis());
				getBrController(getSimSt()).createStartState(normalProblemName);

			} else {

				// Problem is already created- use existing start state
				ProblemNode node = getBrController(getSimSt()).getProblemModel()
						.getStartNode();
				if (trace.getDebugCode("miss"))
					trace.out("miss",
							"ssInteractiveLearningOnProblem: startNode = "
									+ node);
			}
			if (!problemRestart) {
				// if(getSimSt().isSsMetaTutorMode()) {
				// getSimSt().getModelTraceWM().getEventHistory().add(0,
				// getSimSt().getModelTraceWM().new
				// Event(SimStLogger.PROBLEM_ENTERED_ACTION));
				// }
				//JOptionPane.showMessageDialog(null, "faskelo1");

				
				logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM,
						SimStLogger.PROBLEM_ENTERED_ACTION, normalProblemName,
						"", "");
				//JOptionPane.showMessageDialog(null, "faskelo2");

				logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM,
						SimStLogger.PROBLEM_DURATION, normalProblemName,
						"", "");
				//JOptionPane.showMessageDialog(null, "faskelo3");

		
			}

			// Problem is created, if selfExplanation is on (and we're not just
			// restarting same problem), ask why
			/*
			 * if(simSt.isSelfExplainMode() && !problemRestart) {
			 * 
			 * // If enabled, TuTalk engine will "hijack" the legacy
			 * self-explain mode // then exits the function when done. - Huan T.
			 * if (simSt.getTutalkEnabled()) { // Connect to tutalk server
			 * tutalkBridge.setProblemName(problemName.replaceAll("_", "="));
			 * contextVariables.clear();
			 * contextVariables.addVariable("%equation%",
			 * SimSt.convertFromSafeProblemName(problemName));
			 * tutalkBridge.connect("why_problem_dialog", contextVariables,
			 * SimStLogger.PROBLEM_ENTERED_EXPLAIN_ACTION);
			 * 
			 * // Busy loop when the student haven't finished the smalltalk with
			 * Tutalk. // Needed to prevent SimStudent to go ahead and do the
			 * problem before finishing the conversation. while
			 * (tutalkBridge.getState() != SimStTutalk.TUTALK_STATE_DONE) { try
			 * { Thread.sleep(250); } catch (java.lang.InterruptedException e) {
			 * // Nothing we can do here. } }
			 * 
			 * // Nothing needs to be done after this point, returning is fine.
			 * // However, if you need something to be done after
			 * self-explaining, // change this if to if-else and remove this
			 * return. return; }
			 * 
			 * problemName = (problemName.replaceAll("_", "=")); //put problem
			 * name into question String question =
			 * "Why did you choose the problem "+problemName+"?"; String
			 * explanation = "";
			 * 
			 * //Explanation duration includes time from prompt to explanation
			 * received long explainRequestTime =
			 * Calendar.getInstance().getTimeInMillis();
			 * if(brController.getMissController().getSimStPLE() == null ) {
			 * //If no PLE, support selfExplanation through JOptionPanes
			 * explanation = JOptionPane.showInputDialog(null, question,
			 * "Please Provide an Explanation", JOptionPane.PLAIN_MESSAGE ); }
			 * else { //explanation =
			 * brController.getMissController().getSimStPLE
			 * ().giveMessageFreeTextResponse(question); QuestionAnswers qa =
			 * brController.getMissController().getSimStPLE().
			 * getMatchingProblemChoiceExplanation(problemName); if(qa == null)
			 * return; question = qa.getQuestion(); explanation =
			 * brController.getMissController
			 * ().getSimStPLE().giveMessageSelectableResponse(question,
			 * qa.getAnswers()); }
			 * 
			 * int explainDuration = (int)
			 * (Calendar.getInstance().getTimeInMillis() - explainRequestTime);
			 * 
			 * if(explanation != null && explanation.length() > 0) { //Log
			 * non-empty explanation
			 * logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION,
			 * SimStLogger.PROBLEM_ENTERED_EXPLAIN_ACTION, problemName,
			 * explanation, question,explainDuration,question); } else { //Log
			 * empty explanation as no explanation given - specify requested
			 * explanation as problem choice
			 * logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION,
			 * SimStLogger.PROBLEM_ENTERED_EXPLAIN_ACTION, problemName,
			 * SimStLogger.NO_EXPLAIN_ACTION, question,
			 * explainDuration,question); } }
			 */

		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss",
						"ssInteractiveLearningOnProblem: - - - - Exception - - - -");
			e.printStackTrace();
			logger.simStLogException(e,
					"ssInteractiveLearningOnProblem: - - - - Exception - - - -");
		}
	}

	// 22 May 2007: this method simulates the
	// the user entering the value into the Student Interface.
	//
	// called by AskHintTutoringServiceFake.getHint(), which
	// does not have an instance of SimStInteractiveLearning
	public static void simulateCellTextEntry(int row, int col, String value) {

		JCommTable commTable = (JCommTable) brController
				.lookupWidgetByName("commTable1");
		TableCell cell = commTable.getCell(row, col);

		// we should pass the TableExpressionCell, but getCell returns an
		// element of cells,
		// which are TableExpressionCell's

		FocusEvent e = new FocusEvent(cell, 1005, false, cell);
		cell.setText(value);
		commTable.focusLost(e);
	}

	// 6 Sep 2009 :: Noboru
	//
	// Generalized for the commTable, but it's still only for the TableCell...
	public static void simulateCellTextEntry(String tableCell, String value) {

	
		TableCell cell = (TableCell) brController.lookupWidgetByName(tableCell);

		// we should pass the TableExpressionCell, but getCell returns an
		// element of cells,
		// which are TableExpressionCell's
		FocusEvent e = new FocusEvent(cell, FocusEvent.FOCUS_LOST, false, cell);
		cell.setText(value);

		int idx = tableCell.indexOf('_');
		String tableName = tableCell.substring(0, idx);
		JCommTable commTable = (JCommTable) brController
				.lookupWidgetByName(tableName);
		commTable.focusLost(e);
		
	}

	/**
	 * Method to retreive specific edge from quiz graph
	 * @param edgeID the edge we want, e.g. 3rd 
	 * @return
	 */
	public SimStEdge getQuizGraphEdge(int edgeID){
		
		SimStProblemGraph quizGraph=this.getQuizGraph();
		SimStNode start = quizGraph.getStartNode();

		int parentNodeID=edgeID-1;
		
		for (int i=0;i<parentNodeID;i++){
				 start=start.getPrevNode();	//getPrevNode moves gives the next node in the quiz graph...  
		}
		
		return (SimStEdge) start.getOutgoingEdges().get(0);
	}
	
	/**
	 * Method to return the correctness of a quiz graph edge 
	 * @param edgeID the edge we want to check its correctness, e.g. 3rd
	 * @return
	 */
	public String getQuizGraphEdgeCorrectness(int edgeID){
		
		return getQuizGraphEdge(edgeID).getEdgeData().getActionType();
		
	}
	
	public static String QUIZ_SOLUTION_CORRECT="Correct";
	public static String QUIZ_SOLUTION_INCORRECT="Incorrect";
	public static String QUIZ_SOLUTION_INCOMPLETE="Incomplete";
	
	/**
	 * Method to grade the quiz graph using the oracle, once the problem is solved
	 * @param solution
	 * @param solutionStepsInfo
	 * @return true of problem is solved correctly, false otherwise
	 */
	public String gradeQuizProblem(Vector<Sai> solution, Vector<SolutionStepInfo> solutionStepsInfo){
		
		String returnValue=QUIZ_SOLUTION_CORRECT;
		SimStNode currentNode=quizGraph.getStartNode();
		
		
		boolean hasAnErrorOccured=false;
		for (int i=0;i<solution.size();i++){

			/*get all the necessary information for that solution step*/
			Sai sai= solution.get(i);
			String ruleName="empty";
			if (solutionStepsInfo!=null)
				ruleName=solutionStepsInfo.get(i).firedRuleName;
		
			//add new node to current quiz graph node
			SimStNode successiveNode = new SimStGraphNavigator().simulatePerformingStep(currentNode, sai);
			
			//addded so JessOracle can grade correctly on the quiz
			if (simSt.isSsAplusCtrlCogTutorMode())
				simSt.setCurrentSsNode(currentNode);


			if (successiveNode!=null /*&& !returnValue.equals(QUIZ_SOLUTION_INCORRECT)*/){
				
				//returnValue=gradeQuizProblemStep(sai,currentNode,ruleName,solution,SimSt.convertFromSafeProblemName(quizGraph.getStartNode().getName()));

				String inquiryResult = simSt.inquiryRuleActivation(quizGraph.getStartNode().getName(), currentNode, "", sai.getS(), sai.getA(), sai.getI(), null);
				
				if (inquiryResult.equals(EdgeData.CLT_ERROR_ACTION))
					hasAnErrorOccured=true;
					
				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(inquiryResult); 
				currentNode.getOutgoingEdges().get(0).getEdgeData().getRuleNames().add(ruleName);	
				
				returnValue=inquiryResult.equals(EdgeData.CORRECT_ACTION) ? QUIZ_SOLUTION_CORRECT : QUIZ_SOLUTION_INCORRECT;

				if (logger.getLoggingEnabled() && !this.getSimSt().isSsCogTutorMode()) {
					String step = simSt.getProblemStepString();
					if (solutionStepsInfo!=null)
						logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.ACTIVATION_RULES_ACTION, step, solutionStepsInfo.get(i).agenda.size() + " rules found", "");
					else
						logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.ACTIVATION_RULES_ACTION, step, "", "");	
					int count = 0;

				}
				
				
				if (!returnValue.equals(QUIZ_SOLUTION_CORRECT) && sai.getS().equalsIgnoreCase("Done")){	
					
					//JOptionPane.showMessageDialog(null, sai.getS() + " was incorrect");
			    	//	AskHint hint = getSimSt().askForHint(getSimSt().getBrController(),currentNode);
					//if (hint.getInput().contains("rf")){
					//	JOptionPane.showMessageDialog(null, sai.getS() + " was RF incorrect");
					//}

					
					if (solution.size()>3){
						//xi xi 
						String via_solution = simSt.getProblemAssessor().determineSolution(quizGraph.getStartNode().getName(), quizGraph.getStartNode());	
						boolean correct = simSt.getProblemAssessor().isSolution( quizGraph.getStartNode().getName(), via_solution);
						if (correct){
		            		returnValue=QUIZ_SOLUTION_CORRECT;
		            		hasAnErrorOccured=false;
		    				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
		            	}
						
						//returnValue=rfPatch(quizGraph.getStartNode().getName(),solution,currentNode);
	    				

						//if (returnValue.equals(QUIZ_SOLUTION_CORRECT))
						//	hasAnErrorOccured=false;
		            	
		            	
					}
					
					
					
				}
				
				/*if an incorrect step has been detected, don't bother grading anymore.*/
				//if (returnValue.equals(QUIZ_SOLUTION_INCORRECT)) return returnValue;
			
				
				currentNode=successiveNode;
			}

		}
		
		
	
		
		/*if all steps are correct but last step is not done, then solution is incomplete.*/
		Sai lastSai=solution.get(solution.size()-1);	
		if (returnValue.equals(QUIZ_SOLUTION_CORRECT) && !lastSai.getS().equalsIgnoreCase("Done")){
			returnValue=QUIZ_SOLUTION_INCOMPLETE;
		}
		
		
		/*If at least one step is incorrect then entire solution is incorrect*/
		if (hasAnErrorOccured) {

			returnValue=QUIZ_SOLUTION_INCORRECT;
		}
		


		return returnValue;
	
	}
	
	private String rfPatch(String problem, Vector<Sai> solution,SimStNode currentNode){
		String returnValue=QUIZ_SOLUTION_INCORRECT;
		Sai lastSai=solution.get(solution.size()-2);
		Sai beforeLastSai=solution.get(solution.size()-3);
	

		
		
		
		
		if (problem.equals("7c-10_8-9c")){
			if ((beforeLastSai.getI().equals("c") && lastSai.getI().equals("18/16")) || (beforeLastSai.getI	().equals("18/16") && lastSai.getI().equals("c"))){
				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
				returnValue= QUIZ_SOLUTION_CORRECT;
			}	
		}
		else if (problem.equals("3x_12")){
			if ((beforeLastSai.getI().equals("x") && lastSai.getI().equals("12/3")) || (beforeLastSai.getI	().equals("12/3") && lastSai.getI().equals("x"))){
				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
				returnValue= QUIZ_SOLUTION_CORRECT;
			}	
		}
		else if (problem.equals("3x-6_x+2")){
			if ((beforeLastSai.getI().equals("x") && lastSai.getI().equals("8/2")) || (beforeLastSai.getI	().equals("8/2") && lastSai.getI().equals("x"))){
				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
				returnValue= QUIZ_SOLUTION_CORRECT;
			}	
		}
		else if (problem.equals("9x+2_x+8")){
			if ((beforeLastSai.getI().equals("x") && lastSai.getI().equals("6/8")) || (beforeLastSai.getI	().equals("6/8") && lastSai.getI().equals("x"))){
				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
				returnValue= QUIZ_SOLUTION_CORRECT;
			}	
		}
		else if (problem.equals("1+5b_9b-3")){
			if ((beforeLastSai.getI().equals("b") && lastSai.getI().equals("4/4")) || (beforeLastSai.getI	().equals("4/4") && lastSai.getI().equals("b"))){
				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
				returnValue= QUIZ_SOLUTION_CORRECT;
			}	
		}
		else if (problem.equals("6-4x_-3-x")){
			if ((beforeLastSai.getI().equals("x") && lastSai.getI().equals("9/3")) || (beforeLastSai.getI	().equals("9/3") && lastSai.getI().equals("x"))){
				currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
				returnValue= QUIZ_SOLUTION_CORRECT;
			}	
		}
		
		
		return returnValue;
	}
	

	/**
	 * Method to solve a quiz problem, using the SimStSolver.
	 * This is faster than previous method to solve quiz (now named {@link #startQuizProblem_old}) 
	 * @return true if problem was solved correctly, false otherwise
	 */
	public Vector<Sai> startQuizProblem_new(){
		boolean returnValue=false;

		/*Necessary jess files that must be passed to the solver */
		String jessFilesDirectory=getBrController(getSimSt()).getPreferencesModel().getStringValue(CTAT_Controller.PROBLEM_DIRECTORY);//brController.getPreferencesModel().getStringValue(BR_Controller.PROJECTS_DIR);//this.getSimSt().getProjectDir();	
		String productionRulesFile=jessFilesDirectory+ SimSt.PRODUCTION_RULE_FILE;
		String wmeTypesFile=jessFilesDirectory+ SimSt.WME_TYPE_FILE;  
		String initialFactsFile=jessFilesDirectory+  SimSt.INIT_STATE_FILE; 
		String pleConfig=getSimSt().getProjectDir()+"/"+SimStPLE.CONFIG_FILE;

		String quizProblem=SimSt.convertFromSafeProblemName(quizGraph.getStartNode().getName());
 
		/*Create new solver to solve the current problem*/
		//SimStSolver solver=new SimStSolver(quizProblem,productionRulesFile,wmeTypesFile, initialFactsFile, pleConfig, simSt);

		
		// SimSt Rete
		//SimStSolver solver=new SimStSolver(quizProblem,productionRulesFile,wmeTypesFile, initialFactsFile, pleConfig, simSt,simSt.getSsRete());
		// MT Rete
		SimStSolver solver=new SimStSolver(quizProblem,productionRulesFile,wmeTypesFile, initialFactsFile, pleConfig, simSt,brController.getModelTracer().getRete());

		
		Vector<Sai> solution=new Vector();
		Vector<SolutionStepInfo> solutionStepsInfo=new Vector<SolutionStepInfo>();

		try {
			solver.createStartState();
			solution=solver.solve();
		} catch (JessException e1) {

			e1.printStackTrace();
		}
		
	
		solutionStepsInfo=solver.getSolutionStepInfo();
	
		//String returnValue1=gradeQuizProblemSolution(solution,solutionStepsInfo);

		return solution;
	}
	
	
	
	
	
	/**
	 * Method that grades if a solution for a quiz problem is correct.
	 * @param solution
	 * @param solutionStepsInfo
	 * @return Method returns QUIZ_SOLUTION_CORRECT, QUIZ_SOLUTION_INCORRECT or QUIZ_SOLUTION_INCOMPLETE
	 */
	public String gradeQuizProblemSolution(Vector<Sai> solution,Vector<SolutionStepInfo> solutionStepsInfo){
		String returnValue=QUIZ_SOLUTION_INCORRECT;
		String quizProblem=SimSt.convertFromSafeProblemName(quizGraph.getStartNode().getName());
				
		
		try{		
			returnValue=gradeQuizProblem(solution,solutionStepsInfo);
		}
		catch(Exception ex){
			Sai lastSai=solution.get(solution.size()-1);	
	
			/*if last step is done & correct then */
			if (lastSai.getS().equalsIgnoreCase("Done")){
				String answer=simSt.getProblemAssessor().determineSolution(quizProblem, quizGraph.getStartNode());

				boolean isSolutionCorrect=simSt.getProblemAssessor().performInteractiveAnswerCheck(brController.getMissController().getSimStPLE(), quizProblem, answer);
				if (isSolutionCorrect){
						returnValue=QUIZ_SOLUTION_CORRECT;
				}

			}
			else returnValue=QUIZ_SOLUTION_INCORRECT;
			
		}
		
		return returnValue;
	}

	
	
		
	/**
	 * method for grading the step in a quiz. 
	 * @param sai
	 * @param currentNode
	 * @param ruleName
	 * @param solution
	 * @param problemName
	 * @return
	 */
	public boolean gradeQuizProblemStep(Sai sai,ProblemNode currentNode,String ruleName,Vector<Sai> solution,String problemName){
		boolean returnValue=true;
		
		try{		
			/*see if sai is correct*/	
			String inquiryResult = simSt.inquiryRuleActivation(quizGraph.getStartNode().getName(), currentNode, "", sai.getS(), sai.getA(), sai.getI(), null);
			
			/*update quiz graph edge with correctness and rulename*/
			currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(inquiryResult); 
			currentNode.getOutgoingEdges().get(0).getEdgeData().getRuleNames().add(ruleName);
			
			returnValue=inquiryResult.equals(EdgeData.CORRECT_ACTION) ? true : false;
		}
		catch(Exception ex){
			/*If a step caused a fatal exception, then see if overall solution is correct*/
			Sai lastSai=solution.get(solution.size()-1);	
			/*if last step is done then solution might be correct...*/
			if (lastSai.getS().equalsIgnoreCase("Done")){
				
				String answer=simSt.getProblemAssessor().determineSolution(problemName, quizGraph.getStartNode());
				boolean isSolutionCorrect=simSt.getProblemAssessor().performInteractiveAnswerCheck(brController.getMissController().getSimStPLE(), problemName, answer);
				if (isSolutionCorrect){
					currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CORRECT_ACTION); 
					currentNode.getOutgoingEdges().get(0).getEdgeData().getRuleNames().add(ruleName);
					returnValue=true;
				}
				else{
					currentNode.getOutgoingEdges().get(0).getEdgeData().setActionType(EdgeData.CLT_ERROR_ACTION); 
					currentNode.getOutgoingEdges().get(0).getEdgeData().getRuleNames().add(ruleName);
					returnValue=false;
				}

			}
						
		}
			
	return returnValue;
				
	}
	
	/**
	 * Starts to solve the quiz problem
	 * Deprecated method, used to be startQuizProblem, replaced by {@link #startQuizProblem}
	 */
	public void startQuizProblem() {

		SimStNode startNode = quizGraph.getStartNode();
		SimStNode currentNode = null, nextCurrentNode = null;
		HashMap hm = new HashMap(); // Mapping between the RAN and the SimStNode

		boolean killMessageReceived = false;
		currentNode = startNode;
		simSt.setProblemStepString(startNode.getName());

		
		while (!killMessageReceived) {
			simSt.setCurrentSsNode(currentNode);

			long activationListStartTime = Calendar.getInstance().getTimeInMillis();
			Vector activationList = simSt.gatherActivationList(currentNode, hm);
			
			
			//orders rule activations so the one with the highest accepted rate is first.
			Collection<RuleActivationNode> activList = simSt.createOrderedActivationList(activationList);	
			int activationListDuration = (int) ((Calendar.getInstance().getTimeInMillis() - activationListStartTime)/1000);
				
		
			//simulate first rule in activation list and get next node (this call adds node to the quizGraph).

			try{
				nextCurrentNode = inspectRuleActivations(currentNode, activList, hm);
			}
			catch(Exception ex){
				ex.printStackTrace();
				return;
			}
			
			if (logger.getLoggingEnabled()) {
				String step = simSt.getProblemStepString();
				logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.ACTIVATION_RULES_ACTION, step, activList.size() + " rules found", "",activationListDuration);
				int count = 0;
				
				//hint = new AskHintInBuiltClAlgebraTutor(brController,currentNode);	
				//CL oracle must not be hardcoded! Whichever oracle grades the quiz should provide hint for logging
       			hint = simSt.askForHintQuizGradingOracle(getBrController(getSimSt()),currentNode);
				
		
       		if (hint.skillName!=null){
				for (RuleActivationNode ran : activList) {
					count++;
					Sai sai = new Sai(ran.getActualSelection(),
							ran.getActualAction(), ran.getActualInput());
					if (!sai.getI().equals("NotSpecified")
							&& !sai.getI().equals("FALSE")) {
						logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING,
								SimStLogger.ACTIVATION_RULE_ACTION, step, ""
										+ count, "", sai, currentNode,
								hint.getSelection(), hint.getAction(),
								hint.getInput());
					}
				}
			}
			}

			
			
			if (nextCurrentNode != null) {
				currentNode = nextCurrentNode;
				if (currentNode.getIncomingEdges().size() > 0) {
					ProblemEdge edge = (ProblemEdge) currentNode
							.getIncomingEdges().get(0);
					String input = edge.getInput();
					if (EqFeaturePredicate
							.isValidSimpleSkill(input.split(" ")[0])) {
						simSt.setLastSkillOperand(input);
					}
				}
			} else {
				killMessageReceived = true;
			}

			if (currentNode.isDoneState()) {
				break;
			}
		}
		
		//return true;
	}

	private SimStNode inspectRuleActivations(SimStNode currentNode,
			Collection<RuleActivationNode> activationList, HashMap hm) {

		String listAssessment = "";
		SimStNode nextCurrentNode = null, successiveNode = null;
		RuleActivationNode backUpRAN = null;
		
		
		
		Iterator itr = activationList.iterator();
		for (RuleActivationNode ran : activationList) {	
			
			if (ran.getActualInput().equalsIgnoreCase("FALSE"))
				continue;
			
			
			
			successiveNode = inspectActivation(currentNode, ran);
				
			if (successiveNode != null) {
					                
				// Update the HashMap to have a mapping of RAN and new node
				// generated using the activation
				hm.put(ran, successiveNode);
				if (nextCurrentNode == null && isTakingQuiz()) {
					if (successiveNode.getInDegree() > 0) {
						String lastSkill = "";
							if (simSt.getLastSkillOperand() != null	&& simSt.getLastSkillOperand().indexOf(' ') >= 0) {
								lastSkill = simSt.getLastSkillOperand().substring(0,simSt.getLastSkillOperand().indexOf(' '));
							}

							if (((SimStEdge) successiveNode.getIncomingEdges().get(0)).isCorrect()) {
								listAssessment += "Correct: Input: " + ran.getActualInput() + " " + lastSkill + " (" + ran.getName() + ")\n";
								nextCurrentNode = successiveNode;
							} else if (!ran.getActualInput().contains("FALSE") ) {
								listAssessment += "Backup: Input: " + ran.getActualInput() + " " + lastSkill + " (" + ran.getName() + ")\n";
								// backUpRAN = ran;
								nextCurrentNode = successiveNode;
							} else {
									listAssessment += "Not usable: Input: "  + ran.getActualInput() + " " + lastSkill+ " (" + ran.getName() + ")\n";
						}
					}
				} else if (nextCurrentNode == null) {
					nextCurrentNode = successiveNode;
				}
				if (nextCurrentNode != null && getSimSt().dontShowAllRA()){
					break;
				}
			}
		}
		
		/*Question: When is this if condition satisfied? backUpRan=ran is commented above, so this never seems to be used....*/
		if (nextCurrentNode == null && isTakingQuiz() && backUpRAN != null) {
			listAssessment += "Backup used: " + backUpRAN.getName() + " " + backUpRAN.getActualInput() + "\n";
			nextCurrentNode = (SimStNode) inspectActivation(currentNode, backUpRAN);/* inspectActivation(currentNode, backUpRAN); */
			hm.put(backUpRAN, nextCurrentNode);
		} else {
			listAssessment += "Not backup used\n";
		}

		trace.out("ss", listAssessment);
		simSt.setProblemStepString(simSt.getProblemAssessor().calcProblemStepString(getQuizGraph().getStartNode(),nextCurrentNode, null));
		
		return nextCurrentNode;
	}

	private SimStNode inspectActivation(SimStNode currentNode,
			RuleActivationNode ran) {

		SimStNode nextCurrentNode = null;
	
		Sai sai = getSai(ran);
		if (sai.getS().equals("NotSpecified")
				|| sai.getI().equals("NotSpecified"))
			return null;

		// The node we get if we run this step
		SimStNode successiveNode = new SimStGraphNavigator()
				.simulatePerformingStep(currentNode, sai);
		if (successiveNode != null) {

			SimStEdge edge = getQuizGraph().lookUpSSEdge(currentNode,
					successiveNode);
			// make sure the rule name from the ran is filled into the edge
			// data's rule names
			edge.getEdgeData().getRuleNames().add(ran.getName());
			String problemName = getQuizGraph().getStartNode().getName();

			// Ask if the rule is correct
			String inquiryResult = simSt.inquiryRuleActivation(problemName,
					currentNode, ran);

			// Rule is correct, use it and update var to its node
			if (inquiryResult.equals(EdgeData.CORRECT_ACTION)) {
				nextCurrentNode = successiveNode;
								
			}
			// Rule is not correct, but taking quiz, so may want to use as
			// backup
			else if (isTakingQuiz()) {
				// Mark as not correct
				if (!inquiryResult.equals(EdgeData.CORRECT_ACTION)) {
					edge.getEdgeData().setActionType(EdgeData.CLT_ERROR_ACTION);
				}
				// Update a state (or proceed a step, if you will)
				nextCurrentNode = successiveNode;
			}
		}
		return nextCurrentNode;
	}

	
	
    /*variable to hold the previous tutored problem, just in case student clicks restart*/
    public String previousProblemTutored=null;
    public void setPreviousTutoredProblem(String problem){ this.previousProblemTutored=problem;}
    public String getPreviousTutoredProblem(){return this.previousProblemTutored;}
    
    
	// runs interactive learning on start node
	public void runInteractiveLearning() {

		simSt.setLastSkillOperand(null);
		ProblemNode currentNode = getBrController(getSimSt()).getProblemModel().getStartNode();
		runInteractiveLearning(currentNode, true);
	}

	
	public ProblemNode currentNode=null;
	public void setCurrentNode(ProblemNode node){this.currentNode=node;}
	public ProblemNode getCurrentNode(){return this.currentNode;}
	
	
	/**
	 * Method that triggers SimStudent to take the next step. Method used both by runInteractiveLearning and 
	 * the doNextStep used in webAuthoring. 
	 * @param currentNode
	 * @return Returns a list of all the activations
	 */
	public Collection<RuleActivationNode> getActivations(ProblemNode currentNode){
		
		if (currentNode==null){
			currentNode=this.currentNode;
		}
		
		
		Vector activationList = simSt.gatherActivationList(currentNode);	
		Collection<RuleActivationNode> activList = simSt.createOrderedActivationList(activationList);
			 
		return activList;
	}
	
	/**
	 * Method called to make SimStudnet take the next step. Used only for webAuthoring...
	 * @return A list of SAI's, if SimStudent has a suggestion, or Nothing.
	 */
	public ArrayList<Sai> doNextStep(){
		
		ArrayList<Sai> saiList= new ArrayList<Sai>();
		if (this.currentNode==null) {
			trace.out("miss"," **** Current node is NULL, going setting current node to start node");
			setCurrentNode(getBrController(getSimSt()).getProblemModel().getStartNode());
		
		}
		else{
			trace.out("miss"," **** Current node is NOT the start state" + currentNode.getName());
		}
		
		/*get the rule activations*/
		Collection<RuleActivationNode> activList=getActivations(currentNode);
		
		/*convert them into an Sai list*/
		for (RuleActivationNode ran : activList) {
			Sai sai = new Sai(ran.getActualSelection(), ran.getActualAction(), ran.getActualInput());
			saiList.add(sai);
		}
		
		
		return saiList;
	
	}
	
	   public boolean isSolvable(String oracleClass,String problemName,BR_Controller brController){
			boolean answer = true;
			
			
			SimStProblemGraph brGraph = new SimStProblemGraph();
		    SimStNode problemNode = new SimStNode(SimSt.convertToSafeProblemName(problemName),brGraph);
			brGraph.addSSNode(problemNode);
			brController.getProblemModel().setStartNode(problemNode);
			
			
			
			
			Class[] parameters = new Class[3];
			parameters[0] = String.class;
			parameters[1] = ProblemNode.class;
			parameters[2] = BR_Controller.class;
			
			
			try {
				Class oracle = Class.forName("edu.cmu.pact.miss."+oracleClass);
				Object oracleObj = oracle.newInstance();
				Method askMethod = oracle.getMethod("askNextStep",parameters);
				//System.out.println(" before while loop : "+answer);
				while(answer){
					//System.out.println("Inside the while loop");
					//System.out.println("Next step for node : "+problemNode.toString());
					Sai nextStep = (Sai)askMethod.invoke(oracleObj,problemName,problemNode,brController);
					if(nextStep.getI().equalsIgnoreCase("donenosolution"))
						return false;
					else if(nextStep.getI().equalsIgnoreCase("done"))
						break;
					else{
						SimStNode nextNode = new SimStGraphNavigator().simulatePerformingStep(problemNode,nextStep);	
						problemNode = nextNode;
						//System.out.println(" Next Node : "+nextStep.toString());
					}
									
				}
				//System.out.println(" After the while loop ");
				brGraph.clear();
				brController.createStartState(SimSt.convertToSafeProblemName(problemName));
				setProblemStartTime(Calendar.getInstance().getTimeInMillis());
				//SimStNode previous = new SimStNode(SimSt.convertToSafeProblemName(problemName),brGraph);
				//brController.getProblemModel().setStartNode(previous);
				
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProblemModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				brGraph.clear();
			}
			 
			
			return answer;
		}
	public void initModelTracerForProblem(){

		if (simSt.isSsMetaTutorMode() && simSt.getMissController().isPLEon()){
			simSt.getModelTraceWM().setSolutionSteps(0);
			simSt.getModelTraceWM().setAfterQuiz(WorkingMemoryConstants.FALSE);
			simSt.getModelTraceWM().setPreviousProblemType(previousType);
			simSt.getModelTraceWM().setHintRequest("false");
			simSt.getModelTraceWM().allTutoredProblemList.push(SimSt.convertFromSafeProblemName(getBrController(getSimSt()).getProblemName()));	
		    setPreviousTutoredProblem(getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().getStudentEnteredProblem());
		}
		
	}

	// runs interactive learning on given node
	// startWithActivations will skip gathering activation list on the first
	// round if false
	// -no effect after first step. generally used only as false for after undo
	// step to not
	// repeat mistakes and move straight to questioning
	public void runInteractiveLearning(ProblemNode currentNode,
			boolean startWithActivations) {
			
//		int numStepsPerformed = 0;
		this.numStepsPerformed = 0;
		int numStepsBrd = isRunningFromBrd() ? brdDepth() : -1;
//		boolean killMessageReceived = false; // whether a message to kill the IL
												// thread has been received
		boolean activations = startWithActivations; // whether to gather
													// activation list
		boolean askedExplanation = false; // whether an explanation has already
											// been requested on that step
		String lastInputExplained = null;
		String lastSkillExplained = null;

		
		initModelTracerForProblem();
		
		
		long unixTime = System.currentTimeMillis() / 1000L;
		simSt.instructionIdentifierStem=String.valueOf(unixTime);
		previousPositiveInstructionID="start:"+ getBrController(getSimSt()).getProblemName();
		
		getBrController(getSimSt()).getMissController().getSimSt().hintRequest=false;
		this.previousType=currentType;
		String problem=getBrController(getSimSt()).getProblemName();
		ProblemAssessor assessor = new AlgebraProblemAssessor();
	    currentType=assessor.classifyProblem(problem);
	    setAskedExplanation(false);
	    
		
		// when running not from a BRD, it never gets "done" - reaching a done
		// state breaks out of loop
		while ((!isRunningFromBrd() || this.numStepsPerformed != numStepsBrd || getBrController(getSimSt()).getMissController().isBatchModeOn()) && !this.killMessageReceived) {

			if (getBrController(getSimSt()).getMissController().isPLEon())
				getBrController(getSimSt()).getMissController().getSimStPLE().getConversation().setBehaviourDiscrepency(false);	
			 
						
			askedExplanation = false;
			/*step = simSt.getProblemAssessor().calcProblemStepString(currentNode.getProblemModel().getStartNode(), currentNode,simSt.getLastSkillOperand());
			
			simSt.setProblemStepString(step);
			if (logger.getLoggingEnabled())
					logger.simStLog(SimStLogger.SIM_STUDENT_STEP, SimStLogger.STEP_STARTED_ACTION, step, "", "");

			// used to calculate time for full step
//			long stepStartTime = Calendar.getInstance().getTimeInMillis();
			stepStartTime = Calendar.getInstance().getTimeInMillis();*/
			
//			calculateFullStepTime(step, stepStartTime, currentNode);
			calculateFullStepTime(currentNode);
			
			if (trace.getDebugCode("ss"))
				trace.out("ss","----------------" + step + " IL: "+ simSt.isInteractiveLearning());
			// Set SimStudent to thinking
			
			if(!runType.equals("springBoot")) {
				if (getBrController(getSimSt()).getMissController().isPLEon() && !isTakingQuiz())
					getBrController(getSimSt()).getMissController().getSimStPLE().setAvatarThinking();
			}
			
			ProblemNode nextCurrentNode = null;

			// Should gather activation list
			if (activations) {
				long activationListStartTime = Calendar.getInstance()
						.getTimeInMillis();

				/*kept ony for miss output*/
				Vector activationList = simSt.gatherActivationList(currentNode);
				//Collection<RuleActivationNode> activList = simSt.createOrderedActivationList(activationList);

				Collection<RuleActivationNode> activList=getActivations(currentNode);
				
				/*
				 * SimStudent only logs the production rule activation list
				 * if it is learning interactively
				 */	
				if (simSt.isInteractiveLearning()) {
					if (trace.getDebugCode("miss"))
						trace.out("miss", "activationList for Problem = " + getBrController(getSimSt()).getProblemModel().getProblemName() + " >> " + activationList);

					int activationListDuration = (int) ((Calendar.getInstance().getTimeInMillis() - activationListStartTime)/1000);

					if (logger.getLoggingEnabled()) {
						logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING,SimStLogger.ACTIVATION_RULES_ACTION, step,activList.size() + " rules found", "",activationListDuration);
						int count = 0;
						
						//	hint = new AskHintInBuiltClAlgebraTutor(brController, currentNode);	
						//CL oracle must not be hardcoded! Whichever oracle grades the quiz should provide hint for logging
		       			hint = simSt.askForHintQuizGradingOracle(getBrController(getSimSt()),currentNode);
	
		       			
						
						for (RuleActivationNode ran : activList) {
							Sai sai = new Sai(ran.getActualSelection(), ran.getActualAction(), ran.getActualInput());
							if (!sai.getI().equals("NotSpecified") && !sai.getI().equals("FALSE")) {
								ran.setAgendaIndex(count);
						
								logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING,SimStLogger.ACTIVATION_RULE_ACTION,step, "" + count, "", sai, currentNode,hint.getSelection(), hint.getAction(),hint.getInput());
								count++;
							}
						}
					}
				 	

					nextCurrentNode = inspectAgendaRuleActivations(currentNode,activList);
				
				/*	// Get an explanation for why the last activation didn't work
					if (nextCurrentNode == null && !isTakingQuiz() && !askedExplanation) {
						for (RuleActivationNode ran : activList) {
							// for ( int i=activationList.size()-1; i >= 0; i--)
							// {
							// RuleActivationNode ran = (RuleActivationNode)
							// activationList.get(i);

							// Do not process FALSE inputs, just move on to next
							if (ran.getActualInput().equalsIgnoreCase("FALSE")|| ran.getActualInput().equalsIgnoreCase(MTRete.NOT_SPECIFIED))
								continue;
							// Only ask about done if it's the first thing tried
							// if(ran.getActualSelection().equalsIgnoreCase(Rule.DONE_NAME)
							// )
							// continue;
							if (lastInputExplained != null && lastInputExplained.equals(ran.getActualInput()) && lastSkillExplained != null && lastSkillExplained.equals(ran.getName())) {
								break;
							}
							lastInputExplained = ran.getActualInput();
							lastSkillExplained = ran.getName();
						
							explainWhyWrong(ran);
							askedExplanation = true;
							
							break;
						}
					}*/
				}
			}

			// only the first step of activations can be skipped
			activations = true;

			// Finish SimStudent thinking
			if(!runType.equals("springBoot")) {
				if (getBrController(getSimSt()).getMissController().isPLEon() && !isTakingQuiz())
					getBrController(getSimSt()).getMissController().getSimStPLE().setAvatarNormal();
			}
			boolean hintReceived = false;

			// Mon Aug 31 16:53:50 2009: Noboru code-sharing for Quiz SimSt for
			// simStPLE where
			// asking Hint is not appropriate
			//
			// Null nextCurrentNode means that no step was performed correctly
			if (nextCurrentNode == null && !isTakingQuiz() && currentNode != null) {
				
				/*check for any inconsistancies between quiz and tutoring */
				if (getBrController(getSimSt()).getMissController().isPLEon())
					getBrController(getSimSt()).getMissController().getSimStPLE().checkForQuizTutoringBehaviourDiscrepency(brController.getProblemName(),null);	
				
				nextCurrentNode = askWhatToDoNext(currentNode);
				hintReceived = true;
				if (trace.getDebugCode("ss"))
					trace.out("ss", "Calling askWhatToDoNext  "
							+ "currentNode: " + currentNode
							+ " nextCurrentNode: " + nextCurrentNode);
			}

			// Null nextCurrentNode after the Oracle inquiry means that the user
			// initiated a new problem			
			if (nextCurrentNode != null) {

				if (!askedExplanation && hintReceived)
					explainWhyRight(nextCurrentNode);

				// update currentNode
				currentNode = nextCurrentNode;
				applyNodeInstructions(currentNode);
			} else {
				stopLearningProblem(); // next current node is null, stop learning on this problem
			}

			// Step complete - calculate total time on step for logging
			calculateLogSteps(step, stepStartTime);
//			int stepDuration = (int) ((Calendar.getInstance().getTimeInMillis() - stepStartTime)/1000);
//			step = getBrController(getSimSt()).getMissController().getSimSt()
//					.getProblemStepString();
//			logger.simStLog(SimStLogger.SIM_STUDENT_STEP,
//					SimStLogger.STEP_COMPLETED_ACTION, step, "", "",
//					stepDuration);
	


			// Do not continue after done state
			if (currentNode.getDoneState()) {
				doneNodeState();
				// check if the answer is right
//				String solution = simSt.getProblemAssessor().determineSolution(getBrController(getSimSt()).getProblemModel().getProblemName(),brController.getProblemModel().getStartNode());	
//				boolean correct = simSt.getProblemAssessor().isSolution( getBrController(getSimSt()).getProblemModel().getProblemName(), solution);
				/*solution = simSt.getProblemAssessor().determineSolution(getBrController(getSimSt()).getProblemModel().getProblemName(),brController.getProblemModel().getStartNode());	
				setSolution(solution);
				correct = simSt.getProblemAssessor().isSolution( getBrController(getSimSt()).getProblemModel().getProblemName(), solution);
				setCorrect(correct);
				
//				If solution checking failed, then log the reason of failing
				if (solution.length()<2 && getBrController(getSimSt()).getMissController().isPLEon()){
					logger.simStLog(SimStLogger.SIM_STUDENT_STEP,SimStLogger.SOLUTION_CHECKING_FAILED, "", "");	
//				    String title=  brController.getStudentInterface().getActiveWindow().getTitle();
//					title=title+"*";
//				
//					brController.getStudentInterface().getActiveWindow().setTitle(title);
//				
//					JFrame topFrame=null;
//					try{
//						topFrame = (JFrame) SwingUtilities.getWindowAncestor(brController.getStudentInterface().getTutorPanel());
//						title=topFrame.getTitle();
//						title=title+"*";
//						topFrame.setTitle(title);
//					}
//					catch(Exception ex){
//						
//					}				
					
				}
				
				
				
				if (simSt.isSsMetaTutorMode()){
					//System.out.println(" Setting the tutored Corectness : "+correct);
					getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().setTutoredProblemCorrectness(correct);

				}
				
				// log problem done
				
				
				//System.out.println("  end :  "+Calendar.getInstance().getTimeInMillis()+"  start " + getProblemStartTime());
			
				int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis() - getProblemStartTime())/1000);
				

				logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_DONE_ACTION, simSt.getProblemStepString(), "", "", problemDuration);
				logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_ANSWER_SUBMIT_ACTION, new Sai("Answer", "Submit", solution), correct);
				*/
				// taking quiz is special case displayed differently
				if (!isTakingQuiz()) {
					takingQuiz(solution, correct);
			/*		simSt.setIsInteractiveLearning(false);

					// conversation about double-checking work
					if (trace.getDebugCode("rr"))
						trace.out("rr", "Calling checkAnswer: " + getBrController(getSimSt()).getProblemName() + " solution: " + solution + " correct: " + correct);
					
					boolean verified = checkAnswer(getBrController(getSimSt()).getProblemName(), solution, correct);
					if (trace.getDebugCode("rr")) trace.out("rr", "Called checkAnswer: verified " + verified);

					if (!verified && simSt.isSsMetaTutorMode())
						 simSt.getModelTraceWM().setSolutionCheckError("true");
					
					
					
					if (getBrController(getSimSt()).getMissController().getSimSt().isSsMetaTutorMode()){
						simSt.getModelTraceWM().setSolutionGiven("true");
						simSt.getModelTraceWM().setSolutionSteps(this.numStepsPerformed);
					
						if (!getBrController(getSimSt()).getMissController().getSimSt().hintRequest  ){
							int count=getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().getTutoredProblemsWithoutHint();
							getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().setTutoredProblemsWithoutHint(++count);
						}else{
							getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().setTutoredProblemsWithoutHint(0);
						}
								
						
					}
			
					
					if (getBrController(getSimSt()).getMissController() != null
							&& getBrController(getSimSt()).getMissController().getSimSt() != null
							&& getBrController(getSimSt()).getMissController().getSimSt()
									.isSsMetaTutorMode()) {
						//System.out.println(" Student steps are verified ");
						if (verified)
							getBrController(getSimSt()).getAmt().handleInterfaceAction(
									"sssolutionCorrectness", "implicit",
									"correct");
						else
							getBrController(getSimSt()).getAmt().handleInterfaceAction(
									"sssolutionCorrectness", "implicit",
									"incorrect");
					}

					// Block SimSt until next problem
					if (getBrController(getSimSt()).getMissController().isPLEon() && verified)
						getBrController(getSimSt()).getMissController().getSimStPLE()
								.setAvatarFinished();
					else if (getBrController(getSimSt()).getMissController().isPLEon())
						getBrController(getSimSt()).getMissController().getSimStPLE()
								.setAvatarFinishedWrong();
					
	
					if (simSt.isSsMetaTutorMode())
						getBrController(getSimSt()).getAmt().handleInterfaceAction("solved", "implicit", "-1");*/
				}
				break;
			}
		}
		
		pruneBadRules();
		///brController.getMissController().getSimSt().getModelTraceWM().setSolved("false");
	}
	
	public boolean isDoneState() {
		return this.currentNode.isDoneState();
	}
	
	public void calculateFullStepTime(ProblemNode currentNode) {
		step = simSt.getProblemAssessor().calcProblemStepString(currentNode.getProblemModel().getStartNode(), currentNode,simSt.getLastSkillOperand());
		setStep(step);
		simSt.setProblemStepString(step);
		if (logger.getLoggingEnabled())
				logger.simStLog(SimStLogger.SIM_STUDENT_STEP, SimStLogger.STEP_STARTED_ACTION, step, "", "");

		// used to calculate time for full step
//		long stepStartTime = Calendar.getInstance().getTimeInMillis();
		stepStartTime = Calendar.getInstance().getTimeInMillis();
		setStepStartTime(stepStartTime);
	}
	
	public void applyNodeInstructions(ProblemNode currentNode) {
		try {
			// Apply instructions for selected next node
			Vector<Instruction> instnVector = new Vector<Instruction>();
			getBrController(getSimSt()).setCurrentNode2(currentNode);
			Instruction instn = simSt.lookupInstructionWithNode(getBrController(getSimSt()).getCurrentNode());
			instnVector.clear();
			instnVector.add(instn);
		} catch (Exception e) {
			logger.simStLogException(e, "setCurrentNode2 to "+ currentNode + " failed");
		}
		this.numStepsPerformed++;
		
		if (currentNode.getIncomingEdges().size() > 0) {
			ProblemEdge edge = (ProblemEdge) currentNode.getIncomingEdges().get(0);
			String input = edge.getInput();
			
			// update skillname on transformation steps
		// 	if (EqFeaturePredicate
		//			.isValidSimpleSkill(input.split(" ")[0])) {
		//		simSt.setLastSkillOperand(input);
		//	}
	
			// invoking the valid skill checker to see if input is a valid skill
			if (FeaturePredicate
					.isSkillValid(input.split(" ")[0])) {
				simSt.setLastSkillOperand(input);
			}
			
			
			// Update undo button to mention last completed step
			if(!runType.equals("springBoot")) {
				if (getBrController(getSimSt()).getMissController().isPLEon()) {
					
					final SimStPeerTutoringPlatform platform = getBrController(getSimSt())
						.getMissController().getSimStPLE()
						.getSimStPeerTutoringPlatform();
					if (edge.getSelection()
						.equalsIgnoreCase(Rule.DONE_NAME)) {
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								platform.setUndoButtonText(getBrController(getSimSt())
									.getMissController().getSimStPLE()
									.getUndoButtonTitleString("done"));
							}
						});
						
					} else {
						final String undoText = getBrController(getSimSt())
							.getMissController().getSimStPLE()
							.getUndoButtonTitleString(input);
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
							platform.setUndoButtonText(undoText);
							}
						});
					}
				}
			}
		}
	}
	
	public void stopLearningProblem() {
		this.killMessageReceived = true;
		if (!getSimSt().isSsBatchMode()&& !getSimSt().isInteractiveLearning()&& !getSimSt().isNonInteractiveLearning()) {
			simSt.setIsInteractiveLearning(false);
			simSt.setIsNonInteractiveLearning(false);
		}

		if (trace.getDebugCode("ss"))
				trace.out("ss", "killMessageReceived is true.");

				// Finish SimStudent thinking
		if(!runType.equals("springBoot")) {
			if (getBrController(getSimSt()).getMissController().isPLEon()
						&& !getBrController(getSimSt()).getMissController().getSimStPLE()
								.isStartStatus()) {
				getBrController(getSimSt()).getMissController().getSimStPLE().blockInput(true);
			}
		}
	}
	
	public void calculateLogSteps(String step, long stepStartTime) {
		int stepDuration = (int) ((Calendar.getInstance().getTimeInMillis() - stepStartTime)/1000);
		step = getBrController(getSimSt()).getMissController().getSimSt()
				.getProblemStepString();
		logger.simStLog(SimStLogger.SIM_STUDENT_STEP,
				SimStLogger.STEP_COMPLETED_ACTION, step, "", "",
				stepDuration);
	}
	
	public String problemStepName() {
		String problemName = getBrController(getSimSt()).getProblemModel().getProblemName();
		if(runType.equals("SpringBoot")) {
			problemName = brController.getStepInfo();
		}
		return problemName;
	}
	
	public ProblemNode problemStartNode() {
		ProblemNode startNode = brController.getProblemModel().getStartNode();
		if(runType.equals("SpringBoot")) {
			startNode = brController.getProblemModel().getStartNode();
		}
		return startNode;
	}
	
	public void doneNodeState() {
//		solution = simSt.getProblemAssessor().determineSolution(getBrController(getSimSt()).getProblemModel().getProblemName(),brController.getProblemModel().getStartNode());	
		solution = simSt.getProblemAssessor().determineSolution(problemStepName(),problemStartNode());	
		setSolution(solution);
//		correct = simSt.getProblemAssessor().isSolution( getBrController(getSimSt()).getProblemModel().getProblemName(), solution);
		correct = simSt.getProblemAssessor().isSolution( problemStepName(), solution);
		setCorrect(correct);
		
//		If solution checking failed, then log the reason of failing
		if (solution.length()<2 && getBrController(getSimSt()).getMissController().isPLEon()){
			logger.simStLog(SimStLogger.SIM_STUDENT_STEP,SimStLogger.SOLUTION_CHECKING_FAILED, "", "");	
//		    String title=  brController.getStudentInterface().getActiveWindow().getTitle();
//			title=title+"*";
//		
//			brController.getStudentInterface().getActiveWindow().setTitle(title);
//		
//			JFrame topFrame=null;
//			try{
//				topFrame = (JFrame) SwingUtilities.getWindowAncestor(brController.getStudentInterface().getTutorPanel());
//				title=topFrame.getTitle();
//				title=title+"*";
//				topFrame.setTitle(title);
//			}
//			catch(Exception ex){
//				
//			}				
			
		}
		
		
		
		if (simSt.isSsMetaTutorMode()){
			//System.out.println(" Setting the tutored Corectness : "+correct);
			getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().setTutoredProblemCorrectness(correct);

		}
		
		// log problem done
		
		
		//System.out.println("  end :  "+Calendar.getInstance().getTimeInMillis()+"  start " + getProblemStartTime());
	
		int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis() - getProblemStartTime())/1000);
		

		logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_DONE_ACTION, simSt.getProblemStepString(), "", "", problemDuration);
		logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_ANSWER_SUBMIT_ACTION, new Sai("Answer", "Submit", solution), correct);

	}
	
	public void takingQuiz(String solution, boolean correct) {
		simSt.setIsInteractiveLearning(false);
		// conversation about double-checking work
		if (trace.getDebugCode("rr"))
			trace.out("rr", "Calling checkAnswer: " + getBrController(getSimSt()).getProblemName() + " solution: " + solution);
		
		boolean verified = checkAnswer(getBrController(getSimSt()).getProblemName(), solution);
		
		logAndModelTraceAnswer(verified);
	}

	public String currentType="";
	public String previousType="";
	
	public boolean shouldCheckAnswer() {
		boolean doCheck = false;
		if (simSt.isSelfExplainMode() && getBrController(getSimSt()).getMissController().isPLEon()) {
			if (Math.random() < .4)
				doCheck = true;
			if (this.simSt.isSs2014FractionAdditionAdhoc())
				doCheck = false;
		}
		return doCheck;
	}
	
	private boolean checkAnswer(String problem, String solution) {
		boolean doCheck = shouldCheckAnswer();

		if (doCheck) {
			SimStPLE ple = getBrController(getSimSt()).getMissController().getSimStPLE();
			if(!runType.contentEquals("springBoot")) {
				ple.setAvatarThinking();
			}
			return simSt.getProblemAssessor().performInteractiveAnswerCheck(
					ple, problem, solution);

		} else
			return true;
	}
	
	public void logAndModelTraceAnswer(boolean verified) {
		if (trace.getDebugCode("rr")) trace.out("rr", "Called checkAnswer: verified " + verified);

		if (!verified && simSt.isSsMetaTutorMode())
			 simSt.getModelTraceWM().setSolutionCheckError("true");
		
		if (getBrController(getSimSt()).getMissController().getSimSt().isSsMetaTutorMode()){
			simSt.getModelTraceWM().setSolutionGiven("true");
			simSt.getModelTraceWM().setSolutionSteps(this.numStepsPerformed);
		
			if (!getBrController(getSimSt()).getMissController().getSimSt().hintRequest  ){
				int count=getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().getTutoredProblemsWithoutHint();
				getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().setTutoredProblemsWithoutHint(++count);
			}else{
				getBrController(getSimSt()).getMissController().getSimSt().getModelTraceWM().setTutoredProblemsWithoutHint(0);
			}
		}
		
		if (getBrController(getSimSt()).getMissController() != null
				&& getBrController(getSimSt()).getMissController().getSimSt() != null
				&& getBrController(getSimSt()).getMissController().getSimSt()
						.isSsMetaTutorMode()) {
			//System.out.println(" Student steps are verified ");
			if (verified)
				getBrController(getSimSt()).getAmt().handleInterfaceAction(
						"sssolutionCorrectness", "implicit",
						"correct");
			else
				getBrController(getSimSt()).getAmt().handleInterfaceAction(
						"sssolutionCorrectness", "implicit",
						"incorrect");
		}

		// Block SimSt until next problem
		if (getBrController(getSimSt()).getMissController().isPLEon() && verified)
			getBrController(getSimSt()).getMissController().getSimStPLE()
					.setAvatarFinished();
		else if (getBrController(getSimSt()).getMissController().isPLEon())
			getBrController(getSimSt()).getMissController().getSimStPLE()
					.setAvatarFinishedWrong();
		if (simSt.isSsMetaTutorMode())
			getBrController(getSimSt()).getAmt().handleInterfaceAction("solved", "implicit", "-1");
	}
	
	public List<String> getAnswerCheckMessages(String problem, String solution) {
		SimStPLE ple = getBrController(getSimSt()).getMissController().getSimStPLE();
		return simSt.getProblemAssessor().getInteractiveAnswerCheckMessages(ple, problem, solution);
	}

	public void pruneBadRules() {
		// Delete bad rules
		double average = 0;
		int total = 0;
		// if(trace.getDebugCode("ss"))trace.out("ss",
		// "Examining Rules for Pruning");
		// if(trace.getDebugCode("ss"))System.out.println("--------------------------------------------");
		for (Object name : simSt.getRuleNames()) {
			String ruleName = (String) name;
			Rule rule = simSt.getRule(ruleName);
			int recencyValue = Rule.count;
			recencyValue -= (rule.identity + rule.getAcceptedUses());
			double ruleRating = rule.getAcceptedRatio();
			// if(trace.getDebugCode("ss"))System.out.println(ruleName+": "+ruleRating+" "+rule.getAcceptedUses()
			// + " ["+recencyValue+"]");
			average += recencyValue;
			total++;
		}
		average /= total;
		// if(trace.getDebugCode("ss"))System.out.println("Average: "+average);
		// if(trace.getDebugCode("ss"))System.out.println("--------------------------------------------");

		Vector<String> toRemove = new Vector<String>();
		for (Object name : simSt.getRuleNames()) {
			String ruleName = (String) name;
			Rule rule = simSt.getRule(ruleName);
			int recencyValue = Rule.count;
			recencyValue -= (rule.identity + rule.getAcceptedUses());
			double ruleRating = rule.getAcceptedRatio();
			if (ruleRating < .25 && recencyValue >= average && recencyValue > 5) {
				toRemove.add(ruleName);
				// simSt.removeRule(ruleName);
			}
		}
		for (String name : toRemove) {
			simSt.removeRule(name);
		}
		simSt.saveProductionRules(SimSt.SAVE_PR_STEP_BASE);
	}

	// Given a rule activationList, test each rule activation and take the first
	// successrul
	// activation as a representative step performance.
	// Blame all false rule firing and signal negative example.
	public ProblemNode inspectAgendaRuleActivations(ProblemNode currentNode,
			Collection<RuleActivationNode> activationList) {


	
		
		nextCurrentNode = null;
		ProblemNode successiveNode = null;
		// ProblemNode backupNode = null;
		backupRan = null;
		ruleQueryCounter=0;
		firstStategy=ASK_UNINITIALIZED;
		
		
		// Tracks all activation rules looked at, but nothing is currently done
		// to display this info
		String listAssessment = "";
		StringBuilder listAssessmentBuilder = new StringBuilder();

		// Go through full activationList and inspect each until a good
		// activation is found
		for (RuleActivationNode ran : activationList) {
			// Added to support calling the method for individual activations from WebAPLUS
			boolean cont = inspectAgendaRuleActivation(currentNode, ran, successiveNode, activationList.size(), listAssessmentBuilder);
			if (!cont)
				break;
		}
		
		listAssessment = listAssessmentBuilder.toString();

		// if we've gotten through all the nodes and have a backup for the quiz
		// but no selected answer
		// use the backup
		if (!runType.equals("springBoot")) {
			if (nextCurrentNode == null && isTakingQuiz() && backupRan != null) {
				listAssessment += "Backup used: " + backupRan.getName() + " "
						+ backupRan.getActualInput() + "\n";
				nextCurrentNode = inspectRuleActivation(currentNode, backupRan, null);
				if (trace.getDebugCode("miss"))
					trace.out("miss", "Backup used:");
			} else {
				listAssessment += "Not backup used\n";
			}	
		} else {
			// TODO handle backup option for the quiz for spring boot
		}
		return nextCurrentNode;
	}
	
	public boolean inspectAgendaRuleActivation(ProblemNode currentNode, RuleActivationNode ran, ProblemNode successiveNode, int totalActivations, StringBuilder listAssessmentBuilder) {
		// RuleActivationNode ran = (RuleActivationNode)
		// activationList.get(i);
		trace.out("ss", "Checking RAN: " + ran);
		// Do not process FALSE inputs, just move on to next
		if (ran.getActualInput().equalsIgnoreCase("FALSE")){
			trace.out("miss","Input with selection " + ran.getActualSelection() + "with FALSE selection detected....moving on to next.");
			return true;
		}
		
		/*Check for any inconsistencies between tutoring and quiz*/
		if (getBrController(getSimSt()).getMissController().isPLEon())
			getBrController(getSimSt()).getMissController().getSimStPLE().checkForQuizTutoringBehaviourDiscrepency(brController.getProblemName(),ran);

		if (!runType.equalsIgnoreCase("SpringBoot"))
			successiveNode = inspectRuleActivation(currentNode, ran, null);
		ruleQueryCounter++;
		
		if (successiveNode != null) {
			
			// On the quiz, we want to maintain a backup answer to use if
			// none of them are correct
			if (nextCurrentNode == null && isTakingQuiz()) {
				// Only process nodes we can get to
				if (successiveNode.getInDegree() > 0) {
					String lastSkill = "";
					// Only include the operator of the last skill operand
					// in the variable lastSkill
					if (simSt.getLastSkillOperand() != null
							&& simSt.getLastSkillOperand().indexOf(' ') >= 0) {
						lastSkill = simSt.getLastSkillOperand()
								.substring(
										0,
										simSt.getLastSkillOperand()
												.indexOf(' '));
					}

					// Correct case - assign directly to nextCurrentNode
					if (((ProblemEdge) successiveNode.getIncomingEdges()
							.get(0)).isCorrect()) {
						listAssessmentBuilder.append("Correct: Input: " +
								          ran.getActualInput() + " " + lastSkill +
								          " (" + ran.getName() + ")\n");
						nextCurrentNode = successiveNode;
						// JOptionPane.showMessageDialog(null, "Correct");
					}
					// Valid backups can be anything at the start state
					// (last operand is null),
					// a skill which was not also the last skill used, or
					// any typein step.
					else if (!ran.getActualInput().contains("FALSE")
							&& (simSt.getLastSkillOperand() == null || !ran
									.getName().contains(lastSkill))
							|| ran.getName().contains("typein")) {
						listAssessmentBuilder.append("Backup: Input: " +
								          ran.getActualInput() + " " + lastSkill +
								          " (" + ran.getName() + ")\n");
						backupRan = ran;
						if (trace.getDebugCode("miss"))
							trace.out("miss", " backupRan: " + backupRan);
					}
					// All other nodes are not valid backups as they could
					// result in getting caught in a loop
					// eg x+2=4, add 2 -> x+4=6. add 4 would be a repetitive
					// loop, but is not valid for backup
					// because add is also the last skill used
					else {
						listAssessmentBuilder.append("Not usable: Input: " +
								          ran.getActualInput() + " " + lastSkill +
								          " (" + ran.getName() + ")\n");
					}
				}
			}
			// not possibly backup, definitely OKed node, just use
			else if (nextCurrentNode == null) {
				nextCurrentNode = successiveNode;
			}
			if (nextCurrentNode != null && getSimSt().dontShowAllRA()) {
				return false;
			}
		}
		else {
			if (!isTakingQuiz() && !ran.getActualInput().equalsIgnoreCase("FALSE") && !ran.getActualInput().equalsIgnoreCase(MTRete.NOT_SPECIFIED)){				
					if (ruleQueryCounter==1){
						setFirstRanStudentSaidNo(ran);
						firstStategy=firstStrategyToAskSelfExplQ(totalActivations);
					}
					
					if ((firstStategy==ASK_IMMEDIATELY || (firstStategy==ASK_AFTER_SECOND_NO && ruleQueryCounter==2)) && secondStrategyToAskSelfExplQ(ran)){
						setLastSkillExplained(getFirstRanStudentSaidNo().getName());
						// TODO handle explain why queries for spring boot
					}
			}
		}
		return true;
	}

	/**
	 * Method to check if its OK for SimStudent to ask for self-explanation question
	 * @return
	 */
	boolean isItOkToAskForSelfExplanationQuestion(RuleActivationNode ran){
		return (!isTakingQuiz() && !this.getAskedExplanation() && !ran.getActualInput().equalsIgnoreCase("FALSE") && !ran.getActualInput().equalsIgnoreCase(MTRete.NOT_SPECIFIED));
	}
	
	boolean askedExplanation=false;
	void setAskedExplanation(boolean flag){this.askedExplanation=flag;}
	boolean getAskedExplanation(){return this.askedExplanation;}
	
	RuleActivationNode firstRanStudentSaidNo;
	public void setFirstRanStudentSaidNo(RuleActivationNode ran){this.firstRanStudentSaidNo=ran;}
	public RuleActivationNode getFirstRanStudentSaidNo(){return this.firstRanStudentSaidNo;}

	String lastSkillExplained="";
	public void setLastSkillExplained(String skill){this.lastSkillExplained=skill;}
	String getLastSkillExplained(){return this.lastSkillExplained;}
	
	/**
	 * 2nd strategy to determine if to ask about self explanation question (2nd strategy). 
	 * With this strategy
	 * a) never ask for same thing twice in a row
	 * b) ask only once per problem
	 * c) a random controls how often SimStudent asks
	 * @param ran
	 * @return
	 */
	public boolean secondStrategyToAskSelfExplQ(RuleActivationNode ran){
	 	Random randomGenerator = new Random();
	    double number=randomGenerator.nextDouble(); 
		return (!this.getAskedExplanation() /*&& !lastInputExplained.equals(ran.getActualInput())*/ && !lastSkillExplained.equals(ran.getName()) && number > 0.1);
			
	}
	
	
	public static int ASK_UNINITIALIZED=-1;
	public static int ASK_IMMEDIATELY=1;
	public static int ASK_AFTER_SECOND_NO=2;

	
	/**
	 * 1st strategy for asking self-explanation questions. 
	 * Randomly selects if student should: 
	 * 1. ask immediately after 1st time student says no
	 * 2. ask after the 2nd time student says no (question will be about 1st rule)
	 * @param agendaSize the size of the agenda. Necessary, because we want SELF_EXPL_STRATEGY_AFTER_SECOND_NO to be activated
	 * 		  only if there is more than one rule in the agenda
	 * @return
	 */
	public int firstStrategyToAskSelfExplQ(int agendaSize){
		int returnValue=ASK_UNINITIALIZED;

		 	Random randomGenerator = new Random();
		    double number=randomGenerator.nextDouble(); 
		    if( number <= 0.5) {
		    	returnValue=ASK_IMMEDIATELY;
		    }
		    else {
		    	returnValue=ASK_AFTER_SECOND_NO;
		    }
		     
		    /* if selected to ask after second time student said no, then reconsider strategy (50% chance of asking)*/
		    if (returnValue==ASK_AFTER_SECOND_NO && agendaSize<2){
		    	returnValue= ASK_IMMEDIATELY;
		    }
		    
		    //Forcing to ask immediately, because PI's suggested so. 
		    // For asking after 2nd time we need better language (see todo list item 157).
		    returnValue=ASK_IMMEDIATELY;
	    return returnValue;
	}
	
	
	HashSet<String> explainedWhyRightSkills = new HashSet<String>();
	HashSet<String> explainedSelectionSkills = new HashSet<String>();
	
	/**
	 * Method that returns true if selection is marked in SimStudent configuration file as
	 * a valid selection for self explanation (selection which SimStudent is allowed to ask self-explanation questions).
	 * @param selection
	 * @return
	 */
	private boolean isSelectionValidForSelfExplanation(String selection){	
		return simSt.getBrController().getMissController().getSimStPLE().getValidSelections()!=null && simSt.getBrController().getMissController().getSimStPLE().getValidSelections().contains(selection);
	}
	
	public void explainWhyRight(ProblemNode node) {
		
		
		ProblemEdge edge = null;
		if (node.getInDegree() <= 0)
			return;
		edge = (ProblemEdge) node.getIncomingEdges().get(0);
		if (edge.getEdgeData().getRuleNames().size() <= 0)
			return;
		String skillName = (String) edge.getEdgeData().getRuleNames()
				.get(edge.getEdgeData().getRuleNames().size() - 1);

	
		if (trace.getDebugCode("sstt"))
			trace.out("sstt", "Why right TutalkI Trace skillName:" + skillName
					+ "SAI = " + edge.getSai().getS() + ";"
					+ edge.getSai().getA() + ";" + edge.getSai().getI());
		
		
		
		if (explainedWhyRightSkills==null)
			explainedWhyRightSkills = new HashSet<String>();
		
		Random r = new Random();
	    int probability = r.nextInt(100);
		
		//if (simSt.isSelfExplainMode() && !skillName.contains("typein") && !skillName.contains("unnamed")) {
		//10/06/2014: now selection is the one that defines if SimStudent should ask for self explanation
		if (simSt.isSelfExplainMode() && isSelectionValidForSelfExplanation(edge.getSelection()) && !explainedWhyRightSkills.contains(skillName) 
			&& probability <= CHANCE && !explainedSelectionSkills.contains(edge.getSelection())){
			
			explainedWhyRightSkills.add(skillName);
			explainedSelectionSkills.add(edge.getSelection());
			
			Sai sai = edge.getSai();
			step = simSt.getProblemStepString();

//			String question = "What is it about the equation that made you know to put "+ sai.getI() + "?";
			String question = "Why do you think I need to "+ sai.getI() + " next ?";

			if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME)) {
				question = "How do you know that the problem is done?";
			}

			if (simSt.getTutalkEnabled()) {
				// Hand over the context of the problem to the tutalk engine
				tutalkBridge.setProblemName(getBrController(getSimSt()).getMissController()
						.getSimSt().getProblemStepString());
				contextVariables.clear();
				contextVariables.addVariable("%sai_i%", sai.getI());
				String stepCV = step.replaceAll("_", "=");
				if (stepCV.contains("[")) {
					String operation = "do something";
					if (stepCV.contains("]")) {
						operation = stepCV.substring(stepCV.indexOf("[" + 1,
								stepCV.indexOf("]")));
					}
					stepCV = stepCV.substring(0, stepCV.indexOf("["));
					stepCV = SimSt.convertFromSafeProblemName(stepCV);
					stepCV = operation + " on " + stepCV;
				} else
					stepCV = SimSt.convertFromSafeProblemName(stepCV);
				contextVariables.addVariable("%problemStepString%", stepCV);

				if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME)) {
					tutalkBridge.connect("why_right_done_dialog",
							contextVariables, SimStLogger.HINT_EXPLAIN_ACTION);
				} else {
					tutalkBridge.connect("why_right_dialog", contextVariables,
							SimStLogger.HINT_EXPLAIN_ACTION);
				}

				// Busy loop when the student haven't finished the smalltalk
				// with Tutalk.
				// Needed to prevent SimStudent to go ahead and do the problem
				// before finishing the conversation.
				while (tutalkBridge.getState() != SimStTutalk.TUTALK_STATE_DONE) {
					try {
						Thread.sleep(250);
					} catch (java.lang.InterruptedException e) {
						// Nothing we can do here.
					}
				}

				// Nothing needs to be done after this point, returning is fine.
				// However, if you need something to be done after
				// self-explaining,
				// change this if to if-else and remove this return.
				return;
			}

			String explanation = "";
			//setExplanationGiven(true);
			if (getBrController(getSimSt()).getMissController().getSimSt().isSsMetaTutorMode())
				getBrController(getSimSt()).getAmt().handleInterfaceAction("selfExp", "implicit", "-1");
			 
			long explainRequestTime = Calendar.getInstance().getTimeInMillis();
			if (getBrController(getSimSt()).getMissController().getSimStPLE() == null) {
				explanation = JOptionPane.showInputDialog(null, question,
						"Please Provide an Explanation",
						JOptionPane.PLAIN_MESSAGE);
			} else {
				SimStPLE ple = getBrController(getSimSt()).getMissController().getSimStPLE();
				// explanation =
				// brController.getMissController().getSimStPLE().giveMessageFreeTextResponse(question);
				/*
				 * QuestionAnswers qa =
				 * ple.getMatchingHintExplanation(skillName, sai,
				 * simSt.getProblemStepString()); if(qa == null) return;
				 * question = qa.getQuestion(); explanation =
				 * ple.giveMessageSelectableResponse(question, qa.getAnswers());
				 */
				if(runType.equals("springBoot")) {
					explanation = getHintInformation();
				} else {
					explanation = ple.giveMessageFreeTextResponse(question);
				}
				if (explanation != null && explanation.length() > 0) {
					ple.giveMessage(ple.getConversation().getMessage(
							SimStConversation.CONFIRM_TOPIC));
				} else {
					ple.giveMessage(ple.getConversation().getMessage(
							SimStConversation.SKIPPED_TOPIC));
				}
			}

			
			int explainDuration = (int) (Calendar.getInstance()
					.getTimeInMillis() - explainRequestTime);
			step = getBrController(getSimSt()).getMissController().getSimSt()
					.getProblemStepString();
			if (explanation != null && explanation.length() > 0) {
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION,
						SimStLogger.HINT_EXPLAIN_ACTION, step, explanation,
						question, sai, explainDuration, question);
			} else {
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION,
						SimStLogger.HINT_EXPLAIN_ACTION, step,
						SimStLogger.NO_EXPLAIN_ACTION, question, sai,
						explainDuration, question);
			}
		}
	}

	public void explainWhyWrong(RuleActivationNode ran) {
		
		if (simSt.isSelfExplainMode()) {

			Sai sai = new Sai(ran.getActualSelection(), ran.getActualAction(),
					ran.getActualInput());
			String ruleName = ran.getName().replaceAll("MAIN::", "");
			if (trace.getDebugCode("sstt"))
				trace.out("sstt",
						"Why wrong TutalkI Trace skillName: " + ran.getName()
								+ "SAI = " + sai.getS() + ";" + sai.getA()
								+ ";" + sai.getI());

			String step = simSt.getProblemStepString();

			String question = "Why shouldn't I put " + sai.getI() + "?";
			if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME)) {
				question = "Why isn't the problem finished?";
			}

			// If enabled, TuTalk engine will "hijack" the legacy self-explain
			// mode
			// then exits the function when done. - Huan T.
			if (simSt.getTutalkEnabled()) {
				// Hand over the context of the problem to the tutalk engine
				tutalkBridge.setProblemName(step);
				contextVariables.clear();
				contextVariables.addVariable("%sai_i%", sai.getI());
				String stepCV = step.replaceAll("_", "=");
				if (stepCV.contains("[")) {
					String operation = "do something";
					if (stepCV.contains("]")) {
						trace.out("ss", "Parsing: " + stepCV);
						operation = stepCV.substring(stepCV.indexOf("[") + 1,
								stepCV.indexOf("]"));
					}
					stepCV = stepCV.substring(0, stepCV.indexOf("["));
					stepCV = SimSt.convertFromSafeProblemName(stepCV);
					stepCV = "\"" + operation + "\" and \"" + stepCV + "\"";
				} else {
					stepCV = SimSt.convertFromSafeProblemName(stepCV);
					stepCV = "\"" + stepCV + "\"";
				}
				contextVariables.addVariable("%problemStepString%", stepCV);
				String[] priorStep = getSimSt().instructionStepDesc(ruleName,
						sai, ran.getRuleFoas());
				contextVariables.addVariable("%prior_step%", priorStep[1]);
				contextVariables.addVariable("%prior_i%", priorStep[0]);
				if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME)) {
					tutalkBridge.connect("why_wrong_done_dialog",
							contextVariables,
							SimStLogger.INPUT_WRONG_EXPLAIN_ACTION);
				} else {
					// tutalkBridge.connect("why_wrong", contextVariables);
					// }
					tutalkBridge.connect("why_wrong_dialog", contextVariables,
							SimStLogger.INPUT_WRONG_EXPLAIN_ACTION);
				}

				// Busy loop when the student haven't finished the smalltalk
				// with Tutalk.
				// Needed to prevent SimStudent to go ahead and do the problem
				// before finishing the conversation.
				while (tutalkBridge.getState() != SimStTutalk.TUTALK_STATE_DONE) {
					try {
						Thread.sleep(250);
					} catch (java.lang.InterruptedException e) {
						// Nothing we can do here.
					}
				}

				// Nothing needs to be done after this point, returning is fine.
				// However, if you need something to be done after
				// self-explaining,
				// change this if to if-else and remove this return.
				return;
			}

			if (ruleName.indexOf('&') > 0)
				ruleName = ruleName.substring(0, ruleName.indexOf('&'));
			/*
			 * if(simSt.instructions.get(ruleName) != null) { question =
			 * simSt.instructionDesc(ruleName, sai, ran.getRuleFoas()); }
			 */
			
			
			String explanation = "";
			
			if (getBrController(getSimSt()).lookupWidgetByName(sai.getS()) != null) {
				Object widget = getBrController(getSimSt()).lookupWidgetByName(sai.getS());
				if (widget instanceof JCommTable.TableCell) {
					((JCommTable.TableCell) widget).setText(sai.getI());
					((JCommTable.TableCell) widget).setBackground(Color.pink);
				}
			}

			long explainRequestTime = Calendar.getInstance().getTimeInMillis();
			if (getBrController(getSimSt()).getMissController().getSimStPLE() == null) {
				explanation = JOptionPane.showInputDialog(null, question,
						"Please Provide an Explanation",
						JOptionPane.PLAIN_MESSAGE);
			} else {

				SimStPLE ple = getBrController(getSimSt()).getMissController().getSimStPLE();
				// List<String> choices =
				// getMissController().getSimStPLE().getMatching(ruleName,
				// getProblemStepString(), sai.getI());
				// explanation =
				// getMissController().getSimStPLE().giveMessageSelectableResponse(question,
				// choices);
				// explanation =
				// brController.getMissController().getSimStPLE().giveMessageFreeTextResponse(question);
				QuestionAnswers qa = ple.getMatchingMistakeExplanation(
						ruleName, sai, simSt.getProblemStepString(), ran);
					
				if (qa == null) {
				
					if (getBrController(getSimSt()).lookupWidgetByName(sai.getS()) != null) {
						Object widget = getBrController(getSimSt()).lookupWidgetByName(sai
								.getS());
						if (widget instanceof JCommTable.TableCell) {
							((JCommTable.TableCell) widget).setText("");
							((JCommTable.TableCell) widget)
									.setBackground(Color.white);
						}
					}
					return;
				}
				
				explainedSelectionSkills.add(sai.getS());
				Instruction inst=getBrController(getSimSt()).getMissController().getSimSt().getWhyNotInstruction();				
				if (inst.getPreviousID()==null)
					return;
				
				
				//System.out.println("***Previous id in instruction is " + inst.getPreviousID());
				
				
				
				setAskedExplanation(true);
				setExplanationGiven(true);
				if (getBrController(getSimSt()).getMissController().getSimSt().isSsMetaTutorMode())
					getBrController(getSimSt()).getAmt().handleInterfaceAction("selfExp", "implicit", "-1");
				
				question = qa.getQuestion();

				//Instruction inst=brController.getMissController().getSimSt().getWhyNotInstruction();		
				AplusSpotlight spotlight=null;//
				String[] javaVersionElements = System.getProperty("java.version").split("\\.");
				int major = Integer.parseInt(javaVersionElements[1]);

				if (major>=7)
					spotlight=new AplusSpotlight(getBrController(getSimSt()).getMissController().getSimStPLE().getSimStPeerTutoringPlatform(), brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getTutoringAvatarPanel(),SimStRememberBubble.RIGHT,null);
				
				
				SimStExplainWhyNotDlg whyNotDlg=new SimStExplainWhyNotDlg(getBrController(getSimSt()).getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getStudentInterface() ,brController,sai,inst,question);
							
				//explanation = ple.giveMessageSelectableResponse(question, qa.getAnswers());
				explanation = whyNotDlg.giveMessageSelectableResponse(question, qa.getAnswers(),sai.getI());

				//simSt.getModelTraceWM().setSelfExplanation("false");
				
				if (explanation != null && explanation.length() > 0) {
					ple.giveMessage(ple.getConversation().getMessage(
							SimStConversation.CONFIRM_TOPIC));
				} else {
					ple.giveMessage(ple.getConversation().getMessage(
							SimStConversation.SKIPPED_TOPIC));
				}
				
				whyNotDlg.setVisible(false);
				if (spotlight!=null)
					spotlight.removeSpotlight();
			}
			
			if (getBrController(getSimSt()).lookupWidgetByName(sai.getS()) != null) {
				Object widget = getBrController(getSimSt()).lookupWidgetByName(sai.getS());
				if (widget instanceof JCommTable.TableCell) {
					((JCommTable.TableCell) widget).setText("");
					((JCommTable.TableCell) widget).setBackground(Color.white);
				}
			}
				
			

			int explainDuration = (int) ((Calendar.getInstance().getTimeInMillis() - explainRequestTime)/1000);

			if (explanation != null && explanation.length() > 0) {
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION,
						SimStLogger.INPUT_WRONG_EXPLAIN_ACTION, step,
						explanation, question, sai, explainDuration, question);
			} else {
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION,
						SimStLogger.INPUT_WRONG_EXPLAIN_ACTION, step,
						SimStLogger.NO_EXPLAIN_ACTION, question, sai,
						explainDuration, question);
			}
		}
	}

	// Examine an activation rule to see if it is valid. If OKed, return the new
	// node in the BR graph
	// If not OK, return null
	public String previousMessageGiven="";
	public ProblemNode inspectRuleActivation(ProblemNode currentNode,
			RuleActivationNode ran, String inquiryResult) {
	
		ProblemNode nextCurrentNode = null;

		// Make sure that currentNode is the current node
		secureCurrentNode(currentNode);
		Sai sai = getSai(ran);

		if (sai.getS().equals("NotSpecified") || sai.getI().equals("NotSpecified"))
			return null;
	
		// The node we get if we run this step
		ProblemNode successiveNode = simulatePerformingStep(currentNode, sai);
		if (trace.getDebugCode("miss"))
			trace.out("miss", " successiveNode: " + successiveNode);
	
		ProblemNode startNode = getBrController(getSimSt()).getProblemModel().getStartNode();

		String step = startNode.getName();
		if (simSt.getProblemAssessor() != null)
			step = simSt.getProblemAssessor().findLastStep(startNode,
					currentNode);
		simSt.setProblemStep(step);

		// how could successiveNode be null?
		if (successiveNode != null) {

			String skillName = removeAmpersand(ran.getName()) + " simStIL";
			ProblemEdge edge = updateEdgeSkillName(currentNode, successiveNode,
					skillName);

			String problemName = getBrController(getSimSt()).getProblemModel().getStartNode()
					.getName();

			edge.getEdgeData().setActionType(EdgeData.GIVEN_ACTION);
	
			// Ask if the rule is correct
			if (!runType.equals("springBoot")) {
				inquiryResult = simSt.inquiryRuleActivation(problemName, 
						currentNode, ran);				
			}
			if (!inquiryResult.equals(EdgeData.CORRECT_ACTION) && getBrController(getSimSt()).getMissController().isPLEon()){			
				SimStPLE ple = getBrController(getSimSt()).getMissController().getSimStPLE();			
				//brController.getMissController().getSimSt().displayMessage("",ple.getConversation().getMessage(SimStConversation.THINK_TOPIC));			
				if (!sai.getS().equals("done")){
					String message=ple.getConversation().getSimStMessage1(SimStConversation.FEEDBACK_NEGATIVE_TOPIC,sai.getS(),sai.getI());
					this.previousMessageGiven=message;
					getBrController(getSimSt()).getMissController().getSimSt().displayMessage("",message);	
				}
				else { 
					String message=ple.getConversation().getSimStMessage1(SimStConversation.FEEDBACK_NEGATIVE_DONE_TOPIC,sai.getS(),sai.getI());
					this.previousMessageGiven=message;
					getBrController(getSimSt()).getMissController().getSimSt().displayMessage("",message);	
				}
				
			}
						
			if (trace.getDebugCode("miss"))
				trace.out("miss", "    >>>>> result: " + inquiryResult);
			edge.getEdgeData().setActionType(inquiryResult);

			// Rule is correct, use it and update var to its node
			if (inquiryResult.equals(EdgeData.CORRECT_ACTION)) {
				
				// Update a state (or proceed a step, if you will)
				if (simSt.getHintMethod().equalsIgnoreCase(
						AskHint.HINT_METHOD_BRD)) {
					// When using hintBRD, proceed a step only when the step
					// performed
					// is in the BRD
					ProblemNode childInBRD = getFirstCorrectChild(currentNode);
					ProblemEdge edgeInBRD = simSt.lookupProblemEdge(
							currentNode, childInBRD);
					if (doesEdgeMatchRan(edgeInBRD, ran)) {
						nextCurrentNode = childInBRD;
					}
				} else {
					nextCurrentNode = successiveNode;
				}
				// TODO:: The 4th argument must be edge-path, which apparently
				// is only for Stoich so far
				if (simSt.isILSignalPositive()) {
					signalInstructionAsPositiveExample(ran, nextCurrentNode,
							sai, null);
				}
			}
			// Rule is not correct, but taking quiz, so may want to use as
			// backup
			else if (isTakingQuiz()) {
				// Mark as not correct
				if (!inquiryResult.equals(EdgeData.CORRECT_ACTION)) {
					edge.getEdgeData().setActionType(EdgeData.CLT_ERROR_ACTION);
				}

				// Update a state (or proceed a step, if you will)
				if (simSt.getHintMethod().equalsIgnoreCase(
						AskHint.HINT_METHOD_BRD)) {
					// When using hintBRD, proceed a step only when the step
					// performed
					// is in the BRD
					ProblemNode childInBRD = getFirstCorrectChild(currentNode);
					ProblemEdge edgeInBRD = simSt.lookupProblemEdge(
							currentNode, childInBRD);
					if (doesEdgeMatchRan(edgeInBRD, ran)) {
						nextCurrentNode = childInBRD;
					}
				} else {
					nextCurrentNode = successiveNode;
				}

			} else {

				// not correct and not possible backup quiz answer

				edge.getEdgeData().setActionType(EdgeData.BUGGY_ACTION);
				// add as negative example
				if (ran.getRuleFoas() != null && simSt.isILSignalNegative()) {
					// if we have the FOAs, signal them as a negative example
					trace.out("miss",
							"InteractiveLearning: signalling negative example for "
									+ ran.getName());
					trace.out("miss",
							"ran.getRuleFoas() = " + ran.getRuleFoas());
					signalInstructionAsNegativeExample(ran, successiveNode, sai);
				}

				{
					// 9 Sep 2007: this may need to be uncommented, because of
					// IIL
					// why is this not done for true positive ?
					getBrController(getSimSt()).setCurrentNode2(getBrController(getSimSt()).getProblemModel()
							.getStartNode());
					getBrController(getSimSt()).setCurrentNode2(currentNode);
				}
			}

		}
		return nextCurrentNode;
	}

	// add skillname onto edge
	protected ProblemEdge updateEdgeSkillName(ProblemNode currentNode,
			ProblemNode successiveNode, String skillName) {

		ProblemEdge edge = simSt.lookupProblemEdge(currentNode, successiveNode);
		try {
			if (edge != null) {
				((RuleLabel) edge.getEdgeData().getRuleLabels().get(0))
						.setText(skillName.replaceAll("MAIN::", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.simStLogException(e);
		}
		if (trace.getDebugCode("miss"))
			trace.out("miss", "runInteractiveLearning: step performed = "
					+ edge);
		return edge;
	}

	// Make sure the BR.getCurrentNode() is 'currentNode'
	protected void secureCurrentNode(ProblemNode currentNode) {

		if (!getBrController(getSimSt()).getCurrentNode().equals(currentNode)) {
			// 09-06-2007 Noboru :: This shouldn't be happening.
			// gatherActivationList() must be improved
			// This is also necessary when getCurrentNode() is a new buggy state
			// (See Gustavo's comment below)
			if (trace.getDebugCode("miss"))
				trace.out("miss",
						"currentNode (" + currentNode
								+ ") disagrees with behavior recorder ("
								+ getBrController(getSimSt()).getCurrentNode() + ")");
			getBrController(getSimSt()).setCurrentNode2(currentNode);
			if (trace.getDebugCode("miss"))
				trace.out("miss",
						"now it's set back to " + getBrController(getSimSt()).getCurrentNode());
		}
	}

	// get hint from designated hint oracle
	public ProblemNode askWhatToDoNext(ProblemNode currentNode) {
		String problemName = currentNode.getProblemModel().getProblemName();
//		boolean successful = false; // To signify that learning has been
									// successful or not
//		boolean stillLearning = true; // To signify that SimStudent has not yet
										// learned a step
//		ProblemNode node = null;
		trace.out("miss", "runInteractiveLearning: getting a hint on node "
				+ currentNode);
		// hint = askHint(brController, currentNode);
		
		hint = simSt.askForHint(getBrController(getSimSt()), currentNode);
		
		trace.out("miss","hint returned is " + hint);
		simSt.createNewNodeAndEdgeForHintReceived(hint, currentNode);
		if (trace.getDebugCode("miss"))
			trace.out("miss", "askHint returning " + hint);
		if (trace.getDebugCode("miss"))
			trace.out("miss", "After call to askHint currentNode: "
					+ currentNode + " hint: " + hint);
		trace.out("miss", "runInteractiveLearning: learning a skill " + hint.skillName);

		// used to calc time used in processing learning attempt
		setGivenProblemName(problemName);
		return askNodeSkillName(currentNode, problemName, null);
	}
	
	
	public ProblemNode askNodeSkillName(ProblemNode currentNode, String problemName, AskHint hintInfo) {
		if (problemName == null && currentNode != null) {
			problemName = currentNode.getProblemModel().getProblemName();
		}
		ProblemNode node = null;
		boolean successful = false; 
		boolean stillLearning = true;
		if(hintInfo != null) {
			this.hint = hintInfo;
		}
		long learningStartTime = Calendar.getInstance().getTimeInMillis();
		int repeats = 0;

		// always try calc at least once
		do {
			foaCorrectness = true;
			learningStartTime = Calendar.getInstance().getTimeInMillis();
			// if hint = killer SAI/skillname, return killer node, i.e. null
			if(hint.skillName == null) return node;
			if (!hint.skillName.equals(SimSt.KILL_INTERACTIVE_LEARNING)) {

				// Set SimStudent to thinking
				if(runType.isEmpty()) {
					if (getBrController(getSimSt()).getMissController().isPLEon()) 
						getBrController(getSimSt()).getMissController().getSimStPLE().setAvatarThinking();
				}
				// Create a new node and edge using the hint SAI
				simSt.stepDemonstrated(hint.node, hint.getSai(), hint.edge,null);
				
				int foaCount = 0;
				for (int i = 0; i < simSt.numCurrentFoA(); i++) {
				     //old code, that used to add an foa to the foaCount only if its  not null.
					 //if((((TableExpressionCell)
					 //(simSt.getCurrentFoA().elementAt(i)).getWidget()).getText()).length()> 0)
					++foaCount;
				}

				// If the two FoA's have not been specified don't try to learn
				//if(foaCount == 2) <-- This is algebra domain dependent, we in general we need at least one foa.
				if (foaCount >= 1) {
					trace.out("ss", "Hint: " + hint + " Node: " + hint.node);
					//simSt.getModelTraceWM().setThinking("true");
					successful = simSt.changeInstructionName(hint.skillName,hint.node);
					//simSt.getModelTraceWM().setThinking("false");
				
				} else {
					foaCorrectness = false;
				}

				// If learning is unsuccessful and the step is a type-in step
				// then try changing the
				// instruction to alternate FOA.
				// 1. Remove the bad instruction which failed to learn
				// 2. Get the new instruction with alternate FOA for
				// type-in-step
				// if(!successful && hint.skillName.contains("-typein")) {
				//
				// Instruction badInstr =
				// simSt.lookupInstructionWithNode(hint.node);
				// simSt.deleteBadInstruction(badInstr);
				//
				// this.typeInStepWithNewFoA = true;
				//
				// simSt.stepDemonstrated(hint.node, hint.getSai(), hint.edge,
				// null);
				// successful = simSt.changeInstructionName(hint.skillName,
				// hint.node);
				// this.typeInStepWithNewFoA = false;
				// }

				if (successful) {
					// No further learning/input needed if learning was
					// successful
					node = hint.node;
					stillLearning = false;
				}
				else{
					int failToLearnDuration = (int) (Calendar.getInstance()
							.getTimeInMillis() - learningStartTime);
					logger.simStLog(
							SimStLogger.SIM_STUDENT_LEARNING,
							SimStLogger.FAILURE_OF_LEARNING,
							simSt.getProblemStepString(),
							SimStLogger.FAILURE_OF_LEARNING_RESULT,
							"", null, failToLearnDuration);
				}

				// SimStudent is done thinking
				if(!getBrController(getSimSt()).getRunType().equals("springBoot")) {
				if (getBrController(getSimSt()).getMissController().isPLEon())
					getBrController(getSimSt()).getMissController().getSimStPLE()
							.setAvatarNormal();
				}
				// failure to learn on inputted step and we have not used up
				// additional attempts
				if (stillLearning && repeats < simSt.getSsNumBadInputRetries()) {
					// If the failure to learn happens and it was because FoA
					// was not specified properly then when
					// the wrong input cell is empty again all the cells which
					// don't have text in them become editable
					// again including the wrong input cell.
					// Only allow the student to change the input she didn't
					// learn on
					if (getBrController(getSimSt()).getMissController().isPLEon())
						getBrController(getSimSt()).getMissController().getSimStPLE()
								.unblockOnly(hint.getSai().getS());

					// Remove the bad instruction which it wasn't able to learn
					// from
					Instruction badInstr = simSt.lookupInstructionWithNode(hint.node);
					simSt.deleteBadInstruction(badInstr);

					JCommWidget widget = null;
					String selection = hint.getSai().getS();

					// Revert back to previous state
					boolean result = false;
					if (currentNode.isStudentBeginsHereState())
						getBrController(getSimSt()).goToStartState();
					else {
						result = getBrController(getSimSt()).goToState(currentNode);
						if (trace.getDebugCode("miss"))
							trace.out("miss", "result: " + result);
					}

					// log fail to learn w/ time to calculate it
					int failToLearnDuration = (int) (Calendar.getInstance()
							.getTimeInMillis() - learningStartTime);
					// if(getSimSt().isSsMetaTutorMode()) {
					// getSimSt().getModelTraceWM().getEventHistory().add(0,
					// getSimSt().getModelTraceWM().new
					// Event(SimStLogger.NOT_LEARN_ACTION));
					// }
					int noInstructionsForSkill=getBrController(getSimSt()).getMissController().getSimSt().getInstructionsFor(hint.skillName).size();
			      	String noInstructions=SimStLogger.INSTRUCTION_SIZE + " = " +  noInstructionsForSkill;   		      	
			      	
					logger.simStLog(
							SimStLogger.SIM_STUDENT_LEARNING,
							SimStLogger.NOT_LEARN_ACTION,
							simSt.getProblemStepString(),
							"Retries Remaining:"
									+ (simSt.getSsNumBadInputRetries() - repeats) +" [" +noInstructions+ "]",
							"", hint.getSai(), failToLearnDuration,
							hint.getRetryMessage()[0]);
				
					// If the problem has changed from the one we are working
					// on, then don't do the display, kill learning
					if (getBrController(getSimSt()).getProblemModel() != null
							&& problemName.equals(getBrController(getSimSt())
									.getProblemModel().getProblemName())) {
						MessageObject mo = MessageObject
								.create(MsgType.INTERFACE_ACTION);
						mo.setSelection(hint.getSelection());
						mo.setAction(hint.getAction());
						mo.setInput(hint.getInput());

						if (hint.getInput()!=null && hint.getSelection()!=null)
							getBrController(getSimSt()).handleCommMessage(mo);

						// widget =
						// (JCommWidget)brController.lookupWidgetByName(selection);
						// widget.performAction(hint.getSai());

						hint.getRetry(getBrController(getSimSt()), currentNode);
					} else {
						// if moved onto new problem, learning is killed on this
						// one
						stillLearning = false;
						hint.skillName = SimSt.KILL_INTERACTIVE_LEARNING;
					}
				}

			}

			repeats++;
			// repeat as long as fail to learn and we have more retry attempts
		} while (stillLearning && repeats < simSt.getSsNumBadInputRetries() + 1); // Go
																					// one
																					// extra
																					// time
																					// to
																					// do
																					// final
																					// attempts
																					// at
																					// processing

		// Added to ensure that if failure to learn occurs and students
		// enter a new problem the SimSt does not crash
		if (hint.skillName.equals(SimSt.KILL_INTERACTIVE_LEARNING)) {
			if (trace.getDebugCode("ss"))
				trace.out("ss", "---Kill Message---");
			node = null;
		}
		// fail to learn, but retries are up
		else if (stillLearning && getBrController(getSimSt()).getMissController().isPLEon()) {
			// did get some # of retries
			if (simSt.getSsNumBadInputRetries() != 0) {
				// Final bad instruction removal
				Instruction badInstr = simSt
						.lookupInstructionWithNode(hint.node);
				simSt.deleteBadInstruction(badInstr);

				simSt.setIsInteractiveLearning(false);

				int failToLearnDuration = (int) (Calendar.getInstance()
						.getTimeInMillis() - learningStartTime);

				logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING,
						SimStLogger.NOT_LEARN_SKIP_ACTION,
						simSt.getProblemStepString(), hint.skillName,
						"Retries Remaining:"
								+ (simSt.getSsNumBadInputRetries() - repeats),
						hint.getSai(), failToLearnDuration,
						SimStPLE.NOT_UNDERSTAND);

				// prompt to try different problem
				if (getBrController(getSimSt()).getMissController().isPLEon()) {
					SimStPLE ple = getBrController(getSimSt()).getMissController()
							.getSimStPLE();
					ple.setAvatarConfused(true);
					getBrController(getSimSt())
							.getMissController()
							.getSimSt()
							.displayMessage(
									"",
									ple.getConversation()
											.getMessage(
													SimStConversation.FAIL_TO_LEARN_GIVE_UP_TOPIC));
				} else
					getBrController(getSimSt()).getMissController().getSimSt()
							.displayMessage("", SimStPLE.NOT_UNDERSTAND);

				// Need to disable the button, the student can only Restart this
				// problem or Tutor next problem.
				getBrController(getSimSt()).getMissController().getSimStPLE()
						.getSimStPeerTutoringPlatform()
						.setUndoButtonEnabled(false);
				// JButton undoButton =
				// brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getUndoButton();
				// undoButton.setText(brController.getMissController().getSimStPLE().getUndoButtonTitleString(badInstr.getInput()));

			}
			// no retries at all, just log fail to learn
			else {
				int failToLearnDuration = (int) (Calendar.getInstance()
						.getTimeInMillis() - learningStartTime);

				logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING,
						SimStLogger.NOT_LEARN_SKIP_ACTION,
						simSt.getProblemStepString(), hint.skillName,
						"Retries Remaining:"
								+ (simSt.getSsNumBadInputRetries() - repeats),
						hint.getSai(), failToLearnDuration, "");

			}
			// filledInComponents.put(hint.getSai().getS(),
			// hint.getSai().getI());
			// return currentNode;
			node = null;
		}

		if (trace.getDebugCode("ss"))
			trace.out(
					"ss",
					"Node: " + node + " CurrentNode: "
							+ getBrController(getSimSt()).getCurrentNode());
		return node;
	}

	// get node you would end up at from doing step at given node
	public ProblemNode simulatePerformingStep(ProblemNode currentNode, Sai sai) {
		ProblemNode successiveNode = null;

		SimStNodeEdge nodeEdge = lookupNodeWithSai(sai, currentNode);

		if (nodeEdge == null || !nodeEdge.edge.isCorrect()) {
			// SimStudent performed steps are dealt slightly different than
			// student performed
			// step. This eventually updates the text in the interface for the
			// SimStudent
			// performed step, but does not call sendValue for the step unlike
			// human tutor step.
			nodeEdge = simSt.makeNewNodeAndEdge(sai, currentNode);
		}

		try {
			getBrController(getSimSt()).setCurrentNode2(nodeEdge.node);
			successiveNode = nodeEdge.node;

			if (getBrController(getSimSt()).getCurrentNode() != currentNode) {
				successiveNode = getBrController(getSimSt()).getCurrentNode();
			} else {
				// Something wrong happened and current node did not move
				if (trace.getDebugCode("miss"))
					trace.out("miss", "getCurrentNode() disagrees with "
							+ currentNode + "...");
			}
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss",
						"SimStInteractiveLearning.simulatePerformingStep() got "
								+ e + "...");
			e.printStackTrace();
			logger.simStLogException(e,
					"SimStInteractiveLearning.simulatePerformingStep() got "
							+ e + "...");
		}
		return successiveNode;
	}

	private SimStNodeEdge lookupNodeWithSai(Sai sai, ProblemNode currentNode) {
		SimStNodeEdge nodeEdge = null;

		if (trace.getDebugCode("misIL"))
			trace.out("misIL", "currentNode = " + currentNode);
		if (trace.getDebugCode("misIL"))
			trace.out("misIL", "sai = " + sai);

		// try to find one in the child edges
		Vector /* ProblemNode */children = currentNode.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ProblemNode child = (ProblemNode) children.get(i);
			ProblemEdge edge = simSt.lookupProblemEdge(currentNode, child);
			Sai edgeSai = edge.getSai();

			if (trace.getDebugCode("misIL"))
				trace.out("misIL", "edge = " + edge);
			if (trace.getDebugCode("misIL"))
				trace.out("misIL", "edgeSai = " + edgeSai);

			if (matches(edgeSai, sai)) {
				if (trace.getDebugCode("misIL"))
					trace.out("misIL", "matched");
				nodeEdge = new SimStNodeEdge(child, edge);
				break;
			} else {
				if (trace.getDebugCode("misIL"))
					trace.out("misIL", "no match");
			}
		}
		return nodeEdge;
	}

	// copied from SimSt.isStepModelTraced()
	public boolean matches(Sai sai1, Sai sai2) {
		return (sai1.getS().equals(sai2.getS())
				&& sai1.getA().equals(sai2.getA()) && simSt.compairInput(
				sai1.getI(), sai2.getI()));
	}

	/**
	 * 10 Sep 2007: the number of steps to solve the problem in this BRD. This
	 * may only work for a *linear* graph, i.e. single correct path
	 * 
	 * @return
	 */
	private int brdDepth() {
		ProblemNode startNode = getBrController(getSimSt()).getProblemModel().getStartNode();
		ProblemNode lastNode = startNode.getDeadEnd();

		ProblemNode currNode = startNode;

		int edgeCount = 0;
		while (currNode != lastNode) {
			edgeCount++;
			currNode = getFirstCorrectChild(currNode);
		}
		return edgeCount;
	}

	/**
	 * whether the edge matches the RuleActivationNode
	 * 
	 * @param edge
	 * @param ran
	 * @return
	 */
	private boolean doesEdgeMatchRan(ProblemEdge edge, RuleActivationNode ran) {
		if (trace.getDebugCode("simIL"))
			trace.outln("simIL", "entered doesEdgeMatchRan.");

		boolean result;

		if (edge == null) // the case of IIL
			result = false;
		else {
			EdgeData edgeData = edge.getEdgeData();

			String modelS = (String) edgeData.getSelection().get(0);
			String modelA = (String) edgeData.getAction().get(0);
			String modelI = (String) edgeData.getInput().get(0);
			;

			String ranS = ran.getActualSelection();
			String ranA = ran.getActualAction();
			String ranI = ran.getActualInput();

			result = simSt.isStepModelTraced(ranS, ranA, ranI, modelS, modelA,
					modelI);
			if (trace.getDebugCode("simIL"))
				trace.outln("simIL", "doesEdgeMatchRan: returning " + result);
		}
		return result;
	}

	/**
	 * 8 September 2007
	 * 
	 * @param node
	 * @return node's first correct child
	 */
	private ProblemNode getFirstCorrectChild(ProblemNode node) {
		ProblemNode result = null;
		List children = node.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ProblemNode child = (ProblemNode) children.get(i);
			ProblemEdge edge = simSt.lookupProblemEdge(node, child);
			if (edge.getEdgeData().getActionType()
					.equals(EdgeData.CORRECT_ACTION)) {
				result = child;
				break;
			}
		}
		return result;
	}

	// subtract-3&1 becomes subtract-3
	protected String removeAmpersand(String ruleActivationName) {
		int index = ruleActivationName.lastIndexOf("&");
		String res = (index == -1) ? ruleActivationName : ruleActivationName
				.substring(0, index);
		return res;
	}

	// called by
	// AskHintTutoringServiceFake.getHint(), which does not have an instance
	// of SimStInteractiveLearning
	public static int getSelectionCol(String s) {
		int res;
		char resChar = s.charAt(s.lastIndexOf("C") + 1);
		res = Integer.valueOf(String.valueOf(resChar)).intValue();
		return res;
	}

	public String currentStep(ProblemNode startNode, ProblemNode problemNode) {
		Vector /* ProblemEdge */pathEdges = InquiryClAlgebraTutor
				.findPathDepthFirst(startNode, problemNode);
		String lastEquation = (pathEdges != null ? step(pathEdges)
				: problemNode.getName());
		// lastEquation() may return null
		if (lastEquation == null) {
			if (pathEdges != null && pathEdges.size() > 0) {
				lastEquation = startNode.getName() + "["
						+ ((ProblemEdge) pathEdges.get(0)).getInput() + "]";
			} else {
				lastEquation = startNode.getName();
			}
		}
		return lastEquation;
	}

	private String step(Vector /* ProblemEdge */pathEdges) {

		String step = null;
		int edgeCount = 0;
		ProblemEdge[] edgeQueue = new ProblemEdge[3];

		for (int i = 0; i < pathEdges.size(); i++) {

			edgeQueue[edgeCount++] = (ProblemEdge) pathEdges.get(i);

			if (step != null && edgeCount == 1) {
				step = step + "[" + edgeQueue[edgeCount - 1].getInput() + "]";
			}
			if (edgeCount == 3) {
				String[] eqSide = new String[2];
				for (int j = 0; j < 2; j++) {
					EdgeData edgeData = edgeQueue[j + 1].getEdgeData();
					String input = (String) edgeData.getInput().get(0);
					eqSide[j] = input;
				}
				step = eqSide[0] + " = " + eqSide[1];
				edgeCount = 0;
			}
		}
		return step;
	}

	// called by
	// AskHintTutoringServiceFake.getHint(), which does not have an instance
	// of SimStInteractiveLearning
	public static int getSelectionRow(String s) {
		int res;
		char resChar = s.charAt(s.lastIndexOf("R") + 1);
		res = Integer.valueOf(String.valueOf(resChar)).intValue();
		return res;
	}

	// is there a correspondence between the names of hint method and rule
	// activation test methods?
	public AskHint askHint(BR_Controller brController2, ProblemNode currentNode) {
		if (getBrController(getSimSt()).getMissController().getSimStPLE() != null)
			getBrController(getSimSt()).getMissController().getSimStPLE()
					.setFocusTab(SimStPLE.SIM_ST_TAB);

		AskHint hint = null;
		String methodName = simSt.getHintMethod();

		// get hint of the correct askHint type
		if (methodName.equalsIgnoreCase(AskHint.HINT_METHOD_BRD))
			hint = new AskHintBrd(getBrController(getSimSt()), currentNode);
		else if (methodName.equalsIgnoreCase(AskHint.HINT_METHOD_HD)) {
			hint = new AskHintHumanOracle(getBrController(getSimSt()), currentNode);
			if (trace.getDebugCode("miss"))
				trace.out("miss", "hint has been returned:" + hint);
		} else if (methodName.equalsIgnoreCase(AskHint.HINT_METHOD_FTS))
			hint = new AskHintTutoringServiceFake(getBrController(getSimSt()), currentNode,
					this);
		else if (methodName.equalsIgnoreCase(AskHint.HINT_METHOD_CL))
			hint = new AskHintClAlgebraTutor(getBrController(getSimSt()), currentNode);
		else if (methodName.equalsIgnoreCase(AskHint.HINT_METHOD_FAKE_CLT))
			hint = new AskHintFakeClAlgebraTutor(getBrController(getSimSt()), currentNode);
		else if (methodName.equalsIgnoreCase(AskHint.HINT_METHOD_SOLVER_TUTOR))
			// hint = new AskHintClSolverTutor(brController, currentNode);
			hint = new AskHintInBuiltClAlgebraTutor(getBrController(getSimSt()), currentNode);
		else
			new Exception("No valid hint method was specified!")
					.printStackTrace();

		trace.out("miss", "askHint returning " + hint);

		return hint;
	}

	

	
	String previousPositiveInstructionID=null;
	
	public String getPreviousPositiveInstructionID() {
		return previousPositiveInstructionID;
	}

	public void setPreviousPositiveInstructionID(String previousPositiveInstructionID) {
		this.previousPositiveInstructionID = previousPositiveInstructionID;
	}

	// instruction is correct - process as positive example
	private void signalInstructionAsPositiveExample(RuleActivationNode ran,
			ProblemNode node, Sai sai, Vector edgePath) {
		
		String instructionID=simSt.instructionIdentifierStem + "_" + node.getName();	
		//trace.err("******* sai is " + sai + " instID is "+  instructionID + " and previous is " + previousPositiveInstructionID);
		Instruction instruction = makeInstruction(node, sai, instructionID, previousPositiveInstructionID);
		
		previousPositiveInstructionID=instruction.getInstructionID();
		
		//trace.err("******* previous ID has been updated ans its now " + previousPositiveInstructionID);
		
		String skillName = Rule.getRuleBaseName(ran.getName()).replaceAll(
				"MAIN::", "");
		instruction.setName(skillName);
		simSt.sortInstruction(instruction);

		// TODO - catch no rule
		Rule rule = simSt.getRule(skillName);
		if (rule != null)
			rule.addAcceptedUse(ran.getRuleFoas());

		Vector ruleFoas = ran.getRuleFoas();
		{
			List /* of string */foaStrs = makeFoaStrings(ruleFoas);
			simSt.updateInstructionFoa(instruction, foaStrs);
			// if(getSimSt().isSsMetaTutorMode()) {
			// getSimSt().getModelTraceWM().getEventHistory().add(0,
			// getSimSt().getModelTraceWM().new
			// Event(SimStLogger.POSITIVE_EXAMPLE_ACTION));
			// }
			logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING,
					SimStLogger.POSITIVE_EXAMPLE_ACTION,
					simSt.getProblemStepString(), "FOA:"
							+ instruction.getFocusOfAttention().toString(), "",
					sai);

			simSt.negateBadNegativeExample(instruction);

			simSt.updateLhsConditions(skillName);
		}

		simSt.saveProductionRules(SimSt.SAVE_PR_STEP_BASE);

		// Do the serialization here
		// getSimSt().saveSimStState();
	}

	// copied from signalInstructionAsNegativeExample
	private List /* of string */makeFoaStrings(Vector foas) {

		List foaStrs = new Vector();
		MTRete rete = getBrController(getSimSt()).getModelTracer().getRete();

		for (int i = 0; i < foas.size(); i++) {
			String foa = (String) foas.get(i);
			String selection = foa; // since 'foa' is storing the selection

			Fact wmeFact = rete.getFactByName(selection); // gets the WME fact
															// for the given
															// selection
			try {
				Value inputValue = wmeFact.getSlotValue("value");
				String input = SimSt.stripQuotes(inputValue.toString());
				String wmeType=wmeFact.getName();
				//String foaStr = "MAIN::cell|" + selection + "|" + input;
				String foaStr = wmeType + "|" + selection + "|" + input;
				if (trace.getDebugCode("ss"))
					trace.out("ss", "---FOA: " + foaStr);
				foaStrs.add(foaStr);
			} catch (Exception e) {
				e.printStackTrace();
				logger.simStLogException(e);
			}
		}
		return foaStrs;
	}

	// instruction is incorrect - process as negative example and don't use it
	private void signalInstructionAsNegativeExample(RuleActivationNode ran,
			ProblemNode node, Sai sai) {
		// if foas.size() < foilDataArity, do nothing
		Instruction instruction = makeInstruction(node, sai,null,null); // create a new instruction
		
		Vector foas = ran.getRuleFoas();
		MTRete rete = getBrController(getSimSt()).getModelTracer().getRete();
			
		String negTuples = "[";
		// adding FOAs to the instruction
		for (int i = 0; i < foas.size(); i++) {
			String foa = (String) foas.get(i);
			String selection = foa; // since 'foa' is storing the selection
			Fact wmeFact = rete.getFactByName(selection); // gets the WME fact
															// for the given
															// selection

			try {
				
				Value inputValue = wmeFact.getSlotValue("value");
				String input = SimSt.stripQuotes(inputValue.toString());
				
				// TODO:: Get rid of this hard-code
				//String foaStr = "MAIN::cell|" + selection + "|" + input;
				String wmeType=wmeFact.getName();
				String foaStr = wmeType + "|" + selection + "|" + input;
				
				instruction.addFocusOfAttention(foaStr);
				negTuples += input + ", ";
			} catch (Exception e) {
				e.printStackTrace();
				logger.simStLogException(e);
			}
		}
		negTuples = negTuples.substring(0, negTuples.length() - 2) + "]";
		if (trace.getDebugCode("miss"))
			trace.out("miss", "signalInstructionAsNegativeExample: "
					+ negTuples);
		// if(getSimSt().isSsMetaTutorMode()) {
		// getSimSt().getModelTraceWM().getEventHistory().add(0,
		// getSimSt().getModelTraceWM().new
		// Event(SimStLogger.NEGATIVE_EXAMPLE_ACTION));
		// }
		
	
		logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING,
				SimStLogger.NEGATIVE_EXAMPLE_ACTION,
				simSt.getProblemStepString(), "FOA:" + negTuples, "", sai);

		// calling signalTargetNegative
		String skillName = Rule.getRuleBaseName(ran.getName()).replaceAll(
				"MAIN::", "");
		instruction.setName(skillName);

		simSt.getRule(skillName).addRejectedUse(ran.getRuleFoas());

		simSt.negateBadPositiveExample(instruction);

		
		simSt.signalInstructionAsNegativeExample(instruction);

	}


	
	// 24 April 2007
	// makes an Instruction object with the given SAI and node.
	// refactoring: stepDemonstrated() should call this function.
	private Instruction makeInstruction(ProblemNode node, Sai sai, String id,String previousID) {

		String selection = sai.getS();
		String action = sai.getA();
		String input = sai.getI();

		// The first "Focus of Attention," which corresponds to
		// selection-action-input.
		String wmeType = simSt.getRete().wmeType(selection);
		String saiStr = wmeType + "|" + selection + "|" + input;

		Instruction instruction = new Instruction(node, saiStr,id,previousID);
		instruction.setAction(action);
		instruction.setRecent(true);
    
	       
		return instruction;
	}

	public class SaiNodeEdge {
		Sai sai;
		ProblemNode newNode;
		ProblemEdge newEdge;

		public SaiNodeEdge(Vector sV, Vector aV, Vector iV, ProblemNode nNode,
				ProblemEdge nEdge) {
			sai = new Sai(sV, aV, iV);
			newNode = nNode;
			newEdge = nEdge;
		}

		public SaiNodeEdge(String s, String a, String i, ProblemNode nNode,
				ProblemEdge nEdge) {
			sai = new Sai(s, a, i);
			newNode = nNode;
			newEdge = nEdge;
		}
	}

	protected Sai getSai(RuleActivationNode ran) {
		return new Sai(ran.getActualSelection(), ran.getActualAction(),
				ran.getActualInput());
	}

	// 17 April 2007
	// asks for a demonstration in the shell.
	// returns the SAI
	public Sai askHintAuthorShell(ProblemNode currentNode) {

		// if isLearningUnlabeled, then oracleString is the SAI. Else, SAI +
		// skillname.
		Vector[] oracleString = askHintHumanOracleShell(currentNode);

		Vector selectionV = oracleString[0];
		Vector actionV = oracleString[1];
		Vector inputV = oracleString[2];
		return new Sai(selectionV, actionV, inputV);
	}

	// returns the SAI
	private Vector[] askHintHumanOracleShell(ProblemNode currentNode) {

		String title = "Please give SimStudent a demonstration";

		String message[] = {
				"SimStudent has run out of matching rules for node \""
						+ currentNode + "\".",
				"Please give SimStudent a hint in the form of a demonstration.",
				"Enter the SAI in the shell." };
		
		JFrame frame = getBrController(getSimSt()).getActiveWindow();

		// JOptionPane.showMessageDialog(frame, message, title,
		// JOptionPane.PLAIN_MESSAGE);
		simSt.displayMessage(title, message);

		Vector[] result = null;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String sai = "";

			String inputMessage = null;
			inputMessage = "enter the SAI>> ";

			// continue when something is entered.
			while (sai.equals("")) {
				System.out.print(inputMessage);
				sai = br.readLine();
			}

			String[] sp = sai.split(",");
			String[] sArray = { sp[0] };
			Vector selectionV = makeVector(sArray);
			String[] aArray = { sp[1] };
			Vector actionV = makeVector(aArray);
			String[] iArray = { sp[2] };
			Vector inputV = makeVector(iArray);

			Vector[] res = { selectionV, actionV, inputV };
			result = res;
		} catch (IOException e) {
			e.printStackTrace();
			logger.simStLogException(e);
		}
		return result;
	}

	Vector makeVector(String[] ss) {
		Vector v = new Vector();
		for (int i = 0; i < ss.length; i++) {
			v.add(ss[i]);
		}
		return v;
	}

	Vector makeSingletonVector(String s) {
		String[] sa = { s };
		return makeVector(sa);
	}

	// this function puts a message in messageDrop.
	//
	// called by
	// SimSt.changeInstructionName(), which does not have an instance of
	// SimStInteractiveLearning
	public static void hereIsTheRule(String s) {
		messageDrop.put(s);
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getSkillname() {
		return skillname;
	}

	public void setSkillname(String skillname) {
		this.skillname = skillname;
	}

	private SimStTutalk tutalkBridge;
	SimStTutalkContextVariables contextVariables = new SimStTutalkContextVariables();
	
	private String selection;
	private String action;
	private String input;
	private String skillname;

}
