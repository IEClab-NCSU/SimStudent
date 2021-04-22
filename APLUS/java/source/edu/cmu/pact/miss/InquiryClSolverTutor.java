/**
 * API to communicate with cl.tutors.solver.InquirySolverTutor
 * 
 */
package edu.cmu.pact.miss;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JOptionPane;

import cl.tutors.solver.SolverTutor;
import cl.utilities.TestableTutor.InvalidParamException;
import cl.utilities.TestableTutor.InvalidStepException;
import cl.utilities.TestableTutor.SAI;
import cl.utilities.sm.BadExpressionError;
import cl.utilities.sm.Equation;
import cl.utilities.sm.Expression;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.SymbolManipulator;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/**
 * @author mazda
 * Mon Dec 08 17:11:47 2008
 *
 */
public class InquiryClSolverTutor extends InquiryClAlgebraTutor {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Class fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	// Message headers -- must be shared with cl.tutors.solver.InquirySolverTutor
	private static final String MSG_DELIM = "&";
	private static final String START_PROBLEM = "StartProblem";
	private static final String DO_STEP = "DoStep";
	private static final String TEST_SAI = "TestSAI";
	private static final String GET_NEXT_STEP = "GetNextStep";

	// This field is set by goToState() to remember the last "skill" applied so that
	// edu.cmu.pact.miss.AskHintClSolverTutor.getHint() can identify the type of
	// a typein step (whihc, w/o this information can not determine which skill the
	// typein is for.
	private String currentSkill = null;
	public String getCurrentSkill() { return currentSkill; }
	public void setCurrentSkill(String currentSkill) {
		this.currentSkill = currentSkill;
	}
	private void updateCurrentSkill(String input) {
		String skillName = input.split(" ")[0].toLowerCase();
		if (EqFeaturePredicate.isValidSimpleSkill(skillName)) {
			setCurrentSkill(skillName);
		}
	}
	private void resetCurrentSkill() {
		setCurrentSkill(null);
	}

	private SolverTutor solverTutor;
	public SolverTutor getSolverTutor() { return this.solverTutor; }
	public void setSolverTutor(SolverTutor solverTutor) {
		this.solverTutor = solverTutor;
	}

	// SymbolManipulator can be used to test the equivalence of two expressions
	// In some cases it might be required to make a call to standardize before
	// testing equivalence.
	private SymbolManipulator sm;
	
    public SymbolManipulator getSm() {
		return sm;
	}
	public void setSm(SymbolManipulator sm) {
		this.sm = sm;
	}

	private SAIConverter saiConverter = null;
    
    public SAIConverter getSAIConverter(){
    	return this.saiConverter;
    }
    
    public void setSAIConverter(SAIConverter saiCon){
    	this.saiConverter = saiCon;
    }
    
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public InquiryClSolverTutor(){
		super();
		initSolverTutor();
	}
	
	private void initSolverTutor() {
		try {
			setSolverTutor(new SolverTutor());
			// When TypeInMode is false, the tutor does typein but in a none-simplified form
			getSolverTutor().setParameter("Solver", "TypeInMode", "true");
			getSolverTutor().setParameter("Solver", "AutoSimplify", "true");
			getSolverTutor().setCltPmWholeSide(true);
			setSm(new SymbolManipulator(SMParserSettings.HS_DEFAULT));
		} catch (InvalidParamException e) {
			e.printStackTrace();
		}
	}

	public InquiryClSolverTutor(String serverName, String serverPort) {
		super(serverName, serverPort);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Receiving message from the InquirySolverTutor 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	 void handleMessage(String msg) {
		 super.handleMessage(msg);
	 }
	 
	 // - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
	 // Helper functions
	 // - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
	 
	 public void startProblem(String problem) {
		 String msg = START_PROBLEM + MSG_DELIM + problem;
		 dispatchClientMsg(msg);
	 }
	 
	 public int goToState(BR_Controller brController, ProblemNode currentNode) {
		 
		 ProblemNode startNode = null;
		 // startNode = brController.getProblemModel().getStartNode();
		 if(brController.getMissController().getSimSt().isValidationMode() )
		 {
			 startNode = brController.getMissController().getSimSt().getValidationGraph().getStartNode();
		 } else if(brController.getMissController().getSimSt().getSsInteractiveLearning() == null ||
				 !brController.getMissController().getSimSt().getSsInteractiveLearning().isTakingQuiz()){
			 startNode = brController.getProblemModel().getStartNode();
			 if(startNode == null)
				 return -1;
		 } else {
			 startNode = brController.getMissController().getSimSt().getSsInteractiveLearning().getQuizGraph().getStartNode();
			 if(startNode == null)
				 return -1;
		 }
		 
		 ProblemEdge[] edgeQueue = new ProblemEdge[3];
		 String problem[] = new String[1];
		 problem[0] = startNode.getName();

		 int edgeCount = 0;
		 // Do nothing for the start state...
		 if (currentNode != startNode) {
			 // Traverse the edges on the path but the last one
			 Vector /* ProblemEdge */ pathEdges = findPathDepthFirst(startNode, currentNode);
			 // Returns the last complete equation as problem[0] 
			 // and the following intermediate steps as edgeQueue[]
			 if(pathEdges != null) {
				 if(brController.getRunType().equals("springBoot")) {
					 edgeCount = searchWebLastEquation(problem, edgeQueue, pathEdges, "springBoot");
				 } else {
					 edgeCount = searchLastEquation(problem, edgeQueue, pathEdges);
				 }
			 }
		 }
		 problem[0] = SimSt.convertFromSafeProblemName(problem[0]);
		 /*problem[0] = problem[0].replaceAll(SimSt.EQUAL_SIGN, "=");
		 problem[0] = problem[0].replaceAll(SimSt.SLASH, "/");
		 problem[0] = problem[0].replaceAll(SimSt.OPEN_PAREN, "(");
		 problem[0] = problem[0].replaceAll(SimSt.CLOSE_PAREN, ")");
		 problem[0] = problem[0].replaceAll(SimSt.DECIMAL, ".");*/		 
		 startProblem(problem[0]);
		 setProblemStep(problem[0]);
		 resetCurrentSkill();
		 for (int i = 0; i < edgeQueue.length; i++) {
			 ProblemEdge edge = edgeQueue[i];
			 if (edge != null) {
				 
				 EdgeData edgeData = edge.getEdgeData();
				 String selection = (String)edgeData.getSelection().get(0);
				 if(selection.equalsIgnoreCase(Rule.DONE_NAME))
				 	return edgeCount;
				 /*
				 char table = selection.charAt(SimSt.COMM_STEM.length());
				 if(table != '1') //Not Single table format.  Switch multi table format to multi column, single table
				 {
					int rowIndex = selection.indexOf('R')+1;
			    	char row = selection.charAt(rowIndex);
			    	selection = SimSt.COMM_STEM+"1_C"+table+"R"+row;
				 }
				 */
				 String action = "";
				 String input = (String)edgeData.getInput().get(0);
				 doTheStep(selection, action, input);
				 updateCurrentSkill(input);
			 }
		 }
		 return edgeCount;
	 }
	 
	 // Used for SimSt.logRuleActivationToFile
	 private String problemStep = null;
	 public String getProblemStep() { return problemStep; }
	 public void setProblemStep(String problemStep) { this.problemStep = problemStep; }
	 
	 private void doTheStep(String selection, String action, String input) {
		 String msg = DO_STEP + MSG_DELIM + selection + MSG_DELIM + action + MSG_DELIM + input;
		 dispatchClientMsg(msg);
	 }

	 // Called by isCorrectStep()
	 void sendInquiryStepPerformed(String selection, String action, String input) {
		 String msg = TEST_SAI + MSG_DELIM + selection + MSG_DELIM + action + MSG_DELIM + input;
		 dispatchClientMsg(msg);
	 }
	 
	 public String askNextStep() {
		 dispatchClientMsg(GET_NEXT_STEP);
		 String hintMsg = waitForHintMessage();
		 return hintMsg;
	 }
	 
	 
	 //------------------------------------------------------------//
	 //------------------------------------------------------------//
	 //------------------------------------------------------------//
	 
		// set to be positive when the last skill applied is basic arithmetic skills
		// namely, +, -, *, or /
		// This value must be reset to 0 when a new problem is started
		// isBasicSkillApplied() throws an exception when called on 0
		private int basicSkillApplied = 0;
		private boolean isBasicSkillApplied() {
			if (basicSkillApplied == 0)
				new Exception("Skill has never been applied yet").printStackTrace();
			return basicSkillApplied > 0;
		}
		private boolean isNonBasicSkillApplied() {
			return basicSkillApplied < 0;
		}
		private void resetBasicSkillApplied() {
			basicSkillApplied = 0;
		}
		private void setBasicSkillApplied(boolean flag) {
			basicSkillApplied = flag ? 1 : -1;
		}
		
		private boolean isBasicSkill(String action) {
			boolean isBasicSkill = 
				"add".equals(action) || "subtract".equals(action) ||
				"multiply".equals(action) || "divide".equals(action) ||
				"glarb".equals(action);
			return isBasicSkill;
		}

		// Must be reset by startProblem() and updated by doStep() for non-basic skill
		private String[] typeinExpressions = new String[2];
		
		private void setTypeinExpressions(String equation) {
			typeinExpressions = equation.split(" = ");
		}
		
		private void resetTypeinExpressions() {
			if (typeinExpressions.length>1){
					for (int i = 0; i < 2; i++)
				typeinExpressions[i] = null;
			}
		}
		
		private void removeTypeinExpression(int side) {
			typeinExpressions[side] = null;
		}
	
		private void removeTypeinExpression(String input) {
			for (int i = 0; i < 2; i++) {
				if (typeinExpressions.length>1 && typeinExpressions[i] != null) {
					if (isEqualExpression(input, typeinExpressions[i])) {
						removeTypeinExpression(i);
						return;
					}
				}
			}
			new Exception("removeTypeinExpression: no such expression " + input + " in " + typeinExpressions);
		}
		
		private int lookupTypeinExpression(String input) {
			
			int typeinExpression = -1;
			for (int i = 0; i < typeinExpressions.length; i++) {
				if (typeinExpressions[i] != null) {
					if (isEqualExpression(typeinExpressions[i], input)) {
						typeinExpression = i;
						break;
					}
				}
			}
			return typeinExpression;
		}

		/** 
		 *  clientMsg::  MessageCmd&arg1&arg2
		 *
		 **/
	    private String[] getMessageArg(String clientMsg) {
			
			String[] msgArg = null;
	    	int idx = clientMsg.indexOf(MSG_DELIM);
	    	if (idx > 0) {
	    		String clientMsgArg = clientMsg.substring(idx+1);
	    		msgArg = clientMsgArg.split(MSG_DELIM);
	    	}
			return msgArg;
		}
		
		private String getMessageCmd(String clientMsg) {
			return clientMsg.split(MSG_DELIM)[0];
		}
		
		private void dispatchClientMsg(String clientMsg) {

			String cmd = getMessageCmd(clientMsg);
			String[] arg = getMessageArg(clientMsg);
			
			if (START_PROBLEM.equals(cmd)) {
				startTheProblem(arg[0]);
			} else if (DO_STEP.equals(cmd)) {
				try {
					doStep(arg[0], arg[1], arg[2]); 
										
				} catch (IllegalStateException e) {
					e.printStackTrace();					
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else if (TEST_SAI.equals(cmd)) {
				testSAI(arg[0], arg[1], arg[2]);
			} else if (GET_NEXT_STEP.equals(cmd)) {
				getNextStep();
			} else {
				System.out.println("dispatchClientMsg: invalid msg >> " + clientMsg);
			}
		}	

		
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// Initialize a problem
		private void startTheProblem(String problem) {
			startProblem(getSolverTutor(), problem);
		}
		
		public void startProblem(SolverTutor solverTutor, String problem) {
			if(trace.getDebugCode("rr"))
				trace.out("rr", "startProblem(" + problem + ")");
			String solverProblem = problem + ";" + "solve!";
			solverTutor.startProblem(solverProblem);
			resetBasicSkillApplied();
			resetTypeinExpressions();
		}
		private static final String COMM_STEM= "dorminTable";
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// Perform a step given (a set of) SAI
		private void doStep(String selection, String action, String input) throws IllegalStateException, NullPointerException {
			
			if(trace.getDebugCode("rr"))
				trace.out("rr", "doStep(" + selection + "," + action + "," + input + ")");
			SAI sai = null;
			ExampleTracerEvent result = new ExampleTracerEvent(new Object());
			if(getSAIConverter() != null)
			{
				sai = this.saiConverter.convertCtatSaiToClSai(selection, action, input);
			}
			else
			{
				String clAction = null;
				String clInput = null;
				
                //if(trace.getDebugCode("miss"))
                //        trace.out("miss", selection);

                selection.indexOf("_");
				//String table = selection.substring(selection.indexOf("_")+1, selection.indexOf("_")+2);
				String table = selection.substring(COMM_STEM.length(), COMM_STEM.length()+1);
				int tableVal = Integer.parseInt(table);
                String column = selection.substring(COMM_STEM.length()+3, COMM_STEM.length()+4);
                //Chris Suggestion not used (comment previous 3 lines and un-comment the following)
                //String column = selection.substring(selection.indexOf("_C")+2, selection.indexOf("R"));
               // JOptionPane.showMessageDialog(null,"selection="+selection+", table="+table+",tableVal="+tableVal+", col=" + column);
                int colVal = Integer.parseInt(column);
				if(tableVal==3 || colVal == 3){
					String[] tokens = input.split(" ");
					if(tokens[0].toLowerCase().startsWith("combine")) {
						clAction = "clt";
						clInput = (tokens.length == 4 ? tokens[3] : "" );
					} else {
						clAction = tokens[0];
						clInput = (tokens.length == 2 ? tokens[1] : "" );
					}
					   
					sai = new SAI("", clAction, clInput);
				}
				else {
				    clAction = (tableVal==1 && colVal == 1 ? "left" : "right");
				    clInput = input;	
				   	sai = new SAI("", clAction, clInput);
				}
				//Chris suggestion: 
				//sai = new SAI(selection, action , input);
			}
			String clAction = sai.getAction();	
				
			if (!isRedundantTypein(clAction)) 
			{
				try {
					getSolverTutor().doStep(sai, result);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!isTypeinAction(clAction)) {
					if (!isBasicSkill(clAction)) {
						SAI nextStep = whatToDoNext();
						//JOptionPane.showMessageDialog(null, "DoStep: "+clAction+" "+nextStep);
					
						String equation = nextStep.getSelection();
						setTypeinExpressions(equation);
					}
					setBasicSkillApplied(isBasicSkill(clAction));
				}
			} else 
			{
				// Redundant typein hence the input must be removed from typeinExpressions
				removeTypeinExpression(input);
			}
		}
		
		/**
		 * This calls SolverTutor and asks for the hint messages associated with the current / next step.
		 * @param selection
		 * @param action
		 * @param input
		 * @return An array of hint messages with each message having a hierarchial description
		 * of what step should be done.
		 */
		public String[] getHintMessages(String selection, String action, String input){
			
			String[] hintMessages = null;
			SAI sai = null;
			if(getSAIConverter() != null) { 
				sai = saiConverter.convertCtatSaiToClSai(selection,action, input);
			} else {
				
				String clAction = null;
				String clInput = null;
				
				String table = selection.substring(COMM_STEM.length(), COMM_STEM.length()+1);
				int tableVal = Integer.parseInt(table);
				String column = selection.substring(COMM_STEM.length()+3, COMM_STEM.length()+4);
				//Chris suggestion: 
				//String column = selection.substring(selection.indexOf("_C")+2, selection.indexOf("R"));
				int colVal = Integer.parseInt(column);
				//Chris suggestion: remove tableVal==3.
				if(tableVal==3 || colVal == 3) {
					String[] tokens = input.split(" ");
					if(tokens[0].toLowerCase().startsWith("combine")){
						clAction = "clt";
						clInput = (tokens.length == 4 ? tokens[3] : "");
					} else {
						clAction = tokens[0];
						clInput = (tokens.length == 2 ? tokens[1] : "");
					}
					sai = new SAI("", clAction, clInput);
				} else {
					//chris suggestion: remove tableVal==1
					clAction = (tableVal==1 && colVal == 1 ? "left" : "right");
					clInput = input;
					sai = new SAI("", clAction, clInput);
				}
				//Chris suggestion
				//sai = new SAI(selection, action, input);
			}
			
			try {
				hintMessages = getSolverTutor().getHintMessage(sai);
			} catch (InvalidStepException e) {
				e.printStackTrace();
				System.out.println("-----e.getCause:------" + e.getCause());
			}
			
			return hintMessages;
		}
		
		public void performSteps(SolverTutor tutor, Vector /* SAI */ stepsSoFar) {

			for (int i = 0; i < stepsSoFar.size(); i++) {
				SAI sai = (SAI)stepsSoFar.get(i);
				try {
					tutor.doStep(sai);
				} catch (InvalidStepException e) {
					e.printStackTrace();
				}
			}
		}

		
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// Ask for a hint on what to do next
		private void getNextStep() {
			SAI nextStep = whatToDoNext();

			//if(trace.getDebugCode("ss"))
			//	trace.out("ss", "Selection: " + nextStep.getSelection());
			//	trace.out("ss", "Action: " + nextStep.getAction());
			//	trace.out("ss", "Input: " + nextStep.getInput());

			String msg = HINTMESSAGE + ";" + nextStep.getSelection() + ";" + nextStep.getAction() + ";" + nextStep.getInput();

			//if(trace.getDebugCode("rr"))
			//	trace.out("rr", "getNextStep >> " + msg);

			handleMessage(msg);
		}

		private SAI whatToDoNext() {
			SAI nextStep = null;
			//chris had this deleted
			if (isNonBasicSkillApplied()) {	
				for (int i = 0; i < 2; i++) {			
					if (typeinExpressions[i] != null) {					
						// i = 0 for "left" and 1 for "right"			
						nextStep = new SAI("", i == 0 ? "left" : "right", typeinExpressions[i]);				
						break;
					}
				}
			} else {
				nextStep = this.solverTutor.getNextStep("tutor");
			}


			if (nextStep != null && nextStep.getSelection() == null){
				nextStep.setSelection(this.solverTutor.getCurrentProblem().toString());
			}

			return nextStep;
		}

		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// Ask the TutorSolver if the give SAI is correct 
		private void testSAI(String selection, String action, String input) {

			boolean testSAI = isTheCorrectStep(selection, action, input);
			if(trace.getDebugCode("rr"))
				trace.out("rr", "testSAI(" + selection + "," + action + "," + input + ") = " + testSAI);
			handleMessage(testSAI ? APPROVE : FLAG);
		}

		private boolean isTheCorrectStep(String selection, String action, String input) {
			return isCorrectStep(getSolverTutor(), selection, action, input);
		}

		// Given a CTAT SAI, returns its correctness
		public boolean isCorrectStep(SolverTutor tutor, String selection, String action, String input) {

			boolean isCorrectStep = false;
			if(getSAIConverter() != null){

				SAI sai = this.saiConverter.convertCtatSaiToClSai(selection, action, input);

				if (isRedundantTypein(sai.getAction())) {
					// the step is a typing-in the "input" for non-basic skill
					int side = lookupTypeinExpression(sai.getInput());
					if (side != -1) {
						removeTypeinExpression(side);
						isCorrectStep = true;
					}
				} else {
					// the step is either typing-in for a basic skill or applying a skill
					try {
						isCorrectStep = tutor.isStepOK(sai);
					} catch (InvalidStepException e) {
						e.printStackTrace();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
			return isCorrectStep;
		}

		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
		// Misc functions
		/*private SAI generateClSAIfromCtatSAI(String selection, String action, String input) {
			CtatSAI ctatSAI = new CtatSAI(selection, action, input);
			SAI sai = new SAI("", ctatSAI.getClAction(), ctatSAI.getClInput());
			return sai;
		}*/
		
		// typein step is not necessary for non-basic arithmetic skills (i.e., +, -, *, /)
		private boolean isRedundantTypein(String action) {
			
			boolean isRedundantTypein = false;
			if (isTypeinAction(action)) {
				isRedundantTypein = isNonBasicSkillApplied();
			}
			return isRedundantTypein;
		}
		
		private boolean isTypeinAction(String action) {
			boolean isTypeinAction = "left".equals(action) || "right".equals(action);
			return isTypeinAction;
		}
		
		boolean isEqualExpression(String exp1, String exp2) {
			
			boolean isEqualExpression = false;
			try {
				Equation eq1 = new Equation(exp1);
				Expression ex1 = eq1.getLeft();
				Equation eq2 = new Equation(exp2);
				Expression ex2 = eq2.getLeft();
				
				Expression sub = 
					ex1.subtract(ex2).simplify(true, true, true, true, true, true, true, true, true, true, true, true);
				isEqualExpression = sub.isZero();
				
			} catch (BadExpressionError e) {
				e.printStackTrace();
			}

			return isEqualExpression;
		}

		
		/*public class CtatSAI {

			// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - + 
			// Fields
			// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - + 
			
			String selection = null;
			String action = null;
			String input = null;
			String clSelection = null;
			String clAction = null;
			String clInput = null;

			public String getClAction() { return clAction; }
			public String getClInput() { return clInput; }

			// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - + 
			// Constructor
			// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - + 
			
			public CtatSAI(String selection, String action, String input) {
				this.selection = selection;
				this.action = action;
				this.input = input;

				// For CL SolverTutor, an "action" is an operator defined in SolverOperation, 
				// which is encoded in an "input" in the CTAT tutor.
				// For skillOperand (e.g., "add -1") 
				//     ClAction is the skill
				//     ClInput is the operand
				// For typein (e.g., "3x+1")
				//     ClAction is left, if ctatSelection is for LHS
				//                 right, if ctatSelection is for RHS
				//     ClInput is the same as ctatInput
				String column = getSelectionColumn();
				if (column.equals("3")) {
					// The SAI is for skill-operand
					String[] tokens = input.split(" ");
					clAction = tokens[0];
					clInput = (tokens.length == 2 ? tokens[1] : "" );
				} else {
					// The SAI is for type-in
			        // selection -> "commTable1_C3R1"
			        String indexStr = getSelectionColumn();
			        int index = Integer.parseInt(indexStr);
			        clAction = (index == 1 ? "left" : "right");
			        clInput = input;
				}
			}
			
			private String getSelectionColumn() {
				int indexC = selection.indexOf('C');
				int indexR = selection.indexOf('R');
				String indexStr = selection.substring(indexC +1, indexR);
				return indexStr;
			}

		} // end of class CtatSAI */

		static String problem = "3x+2 = -2x+9";
	    static String selection = "commTable1_C2R2";
	    static String action = "";
	    static String input = "-2x+7";
	    static String steps[] = {
	        "commTable1_C3R1", "add -2", "commTable1_C1R2", "3x"
	    };

	 // - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
	 // Main 
	 // - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
	 /**
	  * @param args
	  */
	 public static void main(String[] args) {
		 
//		 String str = "a&b&c";
//		 String[] strv = str.split("&");
		 
		 trace.addDebugCode("miss-cl");
		 trace.setTraceLevel(5);
		 
		 String host = "localhost"; 
		 // InquiryClSolverTutor clSolverTutor = new InquiryClSolverTutor(SimSt.clSolverTutorHost, SimSt.clSolverTutorPort);
		 InquiryClSolverTutor clSolverTutor = new InquiryClSolverTutor();
		 clSolverTutor.startProblem(problem);
         for(int i = 0; i < steps.length; i += 2)
             clSolverTutor.doStep(steps[i], "", steps[i + 1]);

         boolean flag = false;
		try {
			flag = clSolverTutor.isCorrectStep(selection, action, input);
		} catch (TutorServerTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(trace.getDebugCode("miss"))System.out.println((new StringBuilder()).append("isCorrectStep(").append(selection).append(",").append(action).append(",").append(input).append(") = ").append(flag).toString());
         SAI nextStep = clSolverTutor.whatToDoNext();
         if(trace.getDebugCode("miss"))System.out.println((new StringBuilder()).append("nextStep = ").append(nextStep).toString());

		 //try
		 //{
		 //	 clSolverTutor.isCorrectStep("commTable1_C3R1", "", "rds");
		 //}catch(TutorServerTimeoutException e)
		 //{
		 //	 e.printStackTrace();
		 //}
//		 clSolverTutor.doStep("commTable1_C3R2", "", "rds");
//		 String nextStep = clSolverTutor.askNextStep();
		 //clSolverTutor.shutdown();
	 }
}
