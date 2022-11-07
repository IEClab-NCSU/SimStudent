//package edu.cmu.pact.miss;
//
//import java.util.Date;
//import java.util.Vector;
//
//import cl.tutors.solver.SolverTutor;
//import cl.utilities.TestableTutor.InvalidParamException;
//import cl.utilities.TestableTutor.InvalidStepException;
//import cl.utilities.TestableTutor.SAI;
//import cl.utilities.sm.BadExpressionError;
//import cl.utilities.sm.Equation;
//import cl.utilities.sm.Expression;
//import edu.cmu.pact.Utilities.trace;
//import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
//import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
//import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
//import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
//import edu.cmu.pact.miss.InquiryClAlgebraTutor.TutorServerTimeoutException;
//import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
//
//public class CLTutor {
//
//	private static final String MSG_DELIM = "&";
//	private static final String START_PROBLEM = "StartProblem";
//	private static final String DO_STEP = "DoStep";
//	private static final String TEST_SAI = "TestSAI";
//	private static final String GET_NEXT_STEP = "GetNextStep";
//	
//    private static final String FLAG = "FLAG";
//    protected static final String APPROVE = "APPROVE";
//    private static final String HINTMESSAGE = "HINTMESSAGE";
//    private static final Object GOODBYE_GREETING = "%BYE%";
//
//    private static final String YOU_HAVE_SOLVED = "You have solved the equation";
//    private static final String YOU_ARE_DONE = "You are done with all steps";
//
//	// Constructor for the class. Creates a SolverTutor object.
//	CLTutor() {
//		initSolverTutor();
//	}
//
//	// private/protected members
//	private SolverTutor solverTutor;
//
//	public SolverTutor getSolverTutor() {
//		return this.solverTutor;
//	}
//
//	public void setSolverTutor(SolverTutor solverTutor) {
//		this.solverTutor = solverTutor;
//	}
//
//	protected static final int MAX_NUM_WAIT = 10;
//	protected final int WAIT_DURATION = 2000;
//
//	// method to instantiate the SolverTutor object.
//	private void initSolverTutor() {
//		try {
//			setSolverTutor(new SolverTutor());
//			// When TypeInMode is false, the tutor does typein but in a
//			// none-simplified form
//			getSolverTutor().setParameter("Solver", "TypeInMode", "true");
//			getSolverTutor().setParameter("Solver", "AutoSimplify", "true");
//			getSolverTutor().setCltPmWholeSide(true);
//		} catch (InvalidParamException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// set to be positive when the last skill applied is basic arithmetic skills
//	// namely, +, -, *, or /
//	// This value must be reset to 0 when a new problem is started
//	// isBasicSkillApplied() throws an exception when called on 0
//	private int basicSkillApplied = 0;
//	private boolean isBasicSkillApplied() {
//		if (basicSkillApplied == 0)
//			new Exception("Skill has never been applied yet").printStackTrace();
//		return basicSkillApplied > 0;
//	}
//	private boolean isNonBasicSkillApplied() {
//		return basicSkillApplied < 0;
//	}
//	private void resetBasicSkillApplied() {
//		basicSkillApplied = 0;
//	}
//	private void setBasicSkillApplied(boolean flag) {
//		basicSkillApplied = flag ? 1 : -1;
//	}
//	
//	private boolean isBasicSkill(String action) {
//		boolean isBasicSkill = 
//			"add".equals(action) || "subtract".equals(action) ||
//			"multiply".equals(action) || "divide".equals(action);
//		// trace.out("isBasicSkill(" + action + ") = " + isBasicSkill );
//		return isBasicSkill;
//	}
//
//	// Must be reset by startProblem() and updated by doStep() for non-basic skill
//	private String[] typeinExpressions = new String[2];
//	
//	private void setTypeinExpressions(String equation) {
//		typeinExpressions = equation.split(" = ");
//	}
//	
//	private void resetTypeinExpressions() {
//		for (int i = 0; i < 2; i++)
//			typeinExpressions[i] = null;
//	}
//	
//	private void removeTypeinExpression(int side) {
//		typeinExpressions[side] = null;
//	}
//
//	private void removeTypeinExpression(String input) {
//		for (int i = 0; i < 2; i++) {
//			if (typeinExpressions[i] != null) {
//				if (isEqualExpression(input, typeinExpressions[i])) {
//					removeTypeinExpression(i);
//					return;
//				}
//			}
//		}
//		new Exception("removeTypeinExpression: no such expression " + input + " in " + typeinExpressions);
//	}
//	
//	private int lookupTypeinExpression(String input) {
//		
//		int typeinExpression = -1;
//		for (int i = 0; i < typeinExpressions.length; i++) {
//			if (typeinExpressions[i] != null) {
//				if (isEqualExpression(typeinExpressions[i], input)) {
//					typeinExpression = i;
//					break;
//				}
//			}
//		}
//		return typeinExpression;
//	}
//
//	boolean isEqualExpression(String exp1, String exp2) {
//		
//		boolean isEqualExpression = false;
//		try {
//			Equation eq1 = new Equation(exp1);
//			Expression ex1 = eq1.getLeft();
//			Equation eq2 = new Equation(exp2);
//			Expression ex2 = eq2.getLeft();
//			
//			Expression sub = 
//				ex1.subtract(ex2).simplify(true, true, true, true, true, true, true, true, true, true, true, true, true);
//			isEqualExpression = sub.isZero();
//			
//		} catch (BadExpressionError e) {
//			e.printStackTrace();
//		}
//
//		return isEqualExpression;
//	}
//
//    // Returns a number of steps from the "problem" to the specified node
//    // The "problem" is a intermediate state of the original equation that
//    // is the most recently entered full LHS and RHS
//    //
//    // Returns -1 for timeout
//    // Returns -2 for invalid steps
//    // 
//    public int clAlgebraTutorGotoOneStateBefore(BR_Controller brController, ProblemNode node) {
//        
//        
//        ProblemNode startNode = brController.getProblemModel().getStartNode();
//        
//        ProblemEdge[] edgeQueue = new ProblemEdge[3];
//        String problem[] = new String[1];
//        problem[0] = startNode.getName();
//
//        int edgeCount = 0;
//        // Do nothing for the start state...
//        if (node != startNode) {
//        	// Traverse the edges on the path but the last one
//        	Vector /* ProblemEdge */ pathEdges = findPathDepthFirst(startNode, node);
//        	edgeCount = searchLastEquation(problem, edgeQueue, pathEdges);
//        }
//
//        // Added 
//        //setCurrentProblem(problem[0]);
//
//        boolean hintDelivered = false;
//        while (!hintDelivered) {
//            setCurrentProblem(problem[0]);
//            
//            // right after making a new problem, three hint messages delivered from PTS Plus
//            String hintMsg[] = new String[3];
//            for (int i = 0; i < 3; i++) {
//                hintMsg[i] = waitForHintMessage();
//                if (hintMsg[i] == null) {
//                    resetMsgFromTutoringService();
//                    break;
//                }
//                if (i == 2) hintDelivered = true;
//            }
//            if (hintDelivered) {
//                if ((!hintMsg[0].equals(hintMsg[1]) || !hintMsg[1].equals(hintMsg[2])) &&
//                    (!hintMsg[0].startsWith(YOU_HAVE_SOLVED) ||
//                     !hintMsg[1].startsWith(YOU_HAVE_SOLVED) ||
//                     !hintMsg[2].startsWith(YOU_ARE_DONE)) ) {
//                    hintDelivered = false;
//                    resetMsgFromTutoringService();
//                } else {
//                    addHint(hintMsg[0]);
//                }
//            }
//        }
//        
//        /*
//        String message[] = {"clAlgebraTutorGotoOneStateBefore", "A new problem is set to", problem };
//        SimSt.suspendForDebug(brController, "InquiryClAlgebraTutor", message);
//        */
//        
//
//        for (int i = 0; i < edgeQueue.length; i++) {
//        
//            ProblemEdge edge = edgeQueue[i];
//            if (edge != null) {
//                
//                EdgeData edgeData = edge.getEdgeData();
//
//                String selection = (String)edgeData.getSelection().get(0);
//                
//               	if(selection.equalsIgnoreCase(Rule.DONE_NAME)) 
//               		return edgeCount; 
//               	char table = selection.charAt(SimSt.COMM_STEM.length());
//               	if(table != '1') //Not Single table format.  Switch multi table format to multi column, single table
// 			    {
//               		int rowIndex = selection.indexOf('R')+1;	
//			    	char row = selection.charAt(rowIndex);                
//			    	selection = SimSt.COMM_STEM+"1_C"+table+"R"+row;               		
// 			    } 
//			    	
//			    if (!SimSt.validSelection(selection, i)) {
//                    return -2;
//                }
//                
//                String input = (String)edgeData.getInput().get(0);
//                Vector edgeSkillNames = edgeData.getSkills();
//                String edgeSkillName = (String)edgeSkillNames.get(0);
//                if (edgeSkillName.indexOf(' ') > 0) {
//                    edgeSkillName = edgeSkillName.substring( 0, edgeSkillName.indexOf(' ') );
//                }
//                
//                String msg = "  +++ sending stepPerformed for " + edge.getSource();
//                msg += ", skillName: " + edgeSkillName + ", selection: " + selection + ", input: " + input;
//
//                try
//             	{
//                	boolean feedback = isCorrectStep(selection, null, input);
//             	}
//        		catch(Exception e)
//        		{
//                    return -1;
//        		}
//                
//                // This gives a hint for the step just performed by isCorrectStep() above
//                // sendInquiryMsg("GetHint");
//                String hintMsg = waitForHintMessage();
//                if (hintMsg == null) {
//                    return -1;
//                }
//                
//                addHint(hintMsg);
//            }
//        }
//        return edgeCount;
//    }
//
//    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    // Seeking for a hint
//    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
//    Vector /* String */ hintMessage = new Vector();
//    private void addHint(String hintMessage) {
//        this.hintMessage.add(hintMessage);
//    }
//    private String getHint(int numPrevSteps) {
//        return (String)hintMessage.get(numPrevSteps);
//    }
//    
//    // GetHint
//    // numPrevSteps represents a number of steps made to reach a "currentNode" since a 
//    // problem is set.  Note that the problem is not necessarily the original equation. 
//    // It is the closest intermediate expression to the current state.
//    public String askHint(BR_Controller brController, ProblemNode currentNode, int numPrevSteps) {
//        return getHint(numPrevSteps);
//    }
//    
//	// Set by handleMessage(). isCorrectStep() is waiting for this to be set
//	String msgFromTutoringService = null;
//
//	protected void resetMsgFromTutoringService() {
//		msgFromTutoringService = null;
//	}
//
//	protected String waitForHintMessage() {
//
//		String hintMsg = null;
//
//		int numWait = 0;
//		long startTime = (new Date()).getTime();
//		try {
//			while (msgFromTutoringService == null) {
//				// numWait + "]");
//				wait(WAIT_DURATION);
//				if (++numWait > MAX_NUM_WAIT)
//					break;
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		long endTime = (new Date()).getTime();
//		long duration = endTime - startTime;
//		// msgFromTutoringService + "| in " + duration + "ms.");
//
//		// Something wrong should have been happened...
//		if (msgFromTutoringService == null) {
//			// shutdown();
//			// System.exit(-1);
//		}
//
//		hintMsg = msgFromTutoringService;
//		resetMsgFromTutoringService();
//
//		return hintMsg;
//	}
//
//	private String currentProblem = null;
//
//	public void setCurrentProblem(String problemName) {
//		sendInquiryCreateProblem(problemName);
//		currentProblem = problemName;
//	}
//
//	public String getCurrentProblem() {
//		return currentProblem;
//	}
//
//    // CreateProblem&Solve for y&-4y = -3y+5+(-2y)
//    private void sendInquiryCreateProblem(String problemName) {
//        String varName = getVarName(problemName);
//        String inquiryMsg = "CreateProblem" + "&";
//        inquiryMsg += "Solve for " + varName + "&";
//        inquiryMsg += problemName;
//        //sendInquiryMsg(inquiryMsg);
//        dispatchClientMsg(inquiryMsg);
//    }
//
//    private String getVarName(String problemName) {
//        String varName = "";
//        for (int i = 0; i < problemName.length(); i++) {
//            char c = problemName.charAt(i);
//            if ( ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ) {
//                varName += c;
//                break;
//            }
//        }
//        return varName.toLowerCase();
//    }
//
//	/** 
//	 *  clientMsg::  MessageCmd&arg1&arg2
//	 *
//	 **/
//    private String[] getMessageArg(String clientMsg) {
//		
//		String[] msgArg = null;
//    	int idx = clientMsg.indexOf(MSG_DELIM);
//    	if (idx > 0) {
//    		String clientMsgArg = clientMsg.substring(idx+1);
//    		// trace.out("clientMsgArg = " + clientMsgArg);
//    		msgArg = clientMsgArg.split(MSG_DELIM);
//    	}
//		return msgArg;
//	}
//	
//	private String getMessageCmd(String clientMsg) {
//		return clientMsg.split(MSG_DELIM)[0];
//	}
//	
//	private void dispatchClientMsg(String clientMsg) {
//
//		// trace.out("dispatchClientMsg: " + clientMsg);
//		
//		String cmd = getMessageCmd(clientMsg);
//		String[] arg = getMessageArg(clientMsg);
//		
//		if (START_PROBLEM.equals(cmd)) {
//			startProblem(arg[0]);
//		} else if (DO_STEP.equals(cmd)) {
//			doStep(arg[0], arg[1], arg[2]);
//		} //else if (TEST_SAI.equals(cmd)) {
//			//testSAI(arg[0], arg[1], arg[2]);}
//		else if (GET_NEXT_STEP.equals(cmd)) {
//			getNextStep();
//		} else {
//			trace.out("dispatchClientMsg: invalid msg >> " + clientMsg);
//		}
//	}	
//
//	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
//	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
//	// Initialize a problem
//	private void startProblem(String problem) {
//		startProblem(getSolverTutor(), problem);
//	}
//	
//
//	public void performSteps(SolverTutor tutor, Vector /* SAI */ stepsSoFar) {
//
//		for (int i = 0; i < stepsSoFar.size(); i++) {
//			SAI sai = (SAI)stepsSoFar.get(i);
//			// trace.out("InquirySolverTutor: doStep(" + sai + ")");
//			try {
//				tutor.doStep(sai);
//			} catch (InvalidStepException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//
//	public static Vector /* ProblemEdge */findPathDepthFirst(
//			ProblemNode startNode, ProblemNode endNode) {
//
//		if (startNode == endNode)
//			return null;
//
//		ProblemEdge theEdge = null;
//		if ((theEdge = startNode.isChildNode(endNode)) != null) {
//			Vector path = new Vector();
//			path.add(theEdge);
//			return path;
//		} else {
//			Vector /* ProblemNode */childlen = startNode.getChildren();
//			if (childlen.isEmpty()) {
//				return null;
//			} else {
//				for (int i = 0; i < childlen.size(); i++) {
//					ProblemNode childNode = (ProblemNode) childlen.get(i);
//					Vector path = findPathDepthFirst(childNode, endNode);
//					if (path != null) {
//						path.add(0, startNode.isChildNode(childNode));
//						return path;
//					}
//				}
//				return null;
//			}
//		}
//	}
//
//	 // Used for SimSt.logRuleActivationToFile
//	 private String problemStep = null;
//	 public String getProblemStep() { return problemStep; }
//	 public void setProblemStep(String problemStep) { this.problemStep = problemStep; }
//
//		// This field is set by goToState() to remember the last "skill" applied so that
//		// edu.cmu.pact.miss.AskHintClSolverTutor.getHint() can identify the type of
//		// a typein step (whihc, w/o this information can not determine which skill the
//		// typein is for.
//		private String currentSkill = null;
//		public String getCurrentSkill() { return currentSkill; }
//		public void setCurrentSkill(String currentSkill) {
//			this.currentSkill = currentSkill;
//		}
//		private void updateCurrentSkill(String input) {
//			String skillName = input.split(" ")[0].toLowerCase();
//			if (EqFeaturePredicate.isValidSimpleSkill(skillName)) {
//				setCurrentSkill(skillName);
//			}
//		}
//		private void resetCurrentSkill() {
//			setCurrentSkill(null);
//		}
//
//
//	protected int searchLastEquation(String[] problem, ProblemEdge[] edgeQueue,
//			Vector /* ProblemEdge */pathEdges) {
//
//		int edgeCount = 0;
//
//		for (int i = 0; i < pathEdges.size(); i++) {
//
//			edgeQueue[edgeCount++] = (ProblemEdge) pathEdges.get(i);
//
//			if (edgeCount == 3) {
//				String[] eqSide = new String[2];
//				for (int j = 0; j < 2; j++) {
//					EdgeData edgeData = edgeQueue[j + 1].getEdgeData();
//					String selection = (String) edgeData.getSelection().get(0);
//					char table = selection.charAt(SimSt.COMM_STEM.length());
//					if (table != '1') // Not Single table format. Switch multi
//										// table format to multi column, single
//										// table
//					{
//						int rowIndex = selection.indexOf('R') + 1;
//						char row = selection.charAt(rowIndex);
//						selection = SimSt.COMM_STEM + "1_C" + table + "R"
//								+ row;
//					}
//					String input = (String) edgeData.getInput().get(0);
//					if ("1".equals(getSelectionColumn(selection))) {
//						eqSide[0] = input;
//					} else if ("2".equals(getSelectionColumn(selection))) {
//						eqSide[1] = input;
//					} else {
//						return -2;
//					}
//				}
//				problem[0] = eqSide[0] + " = " + eqSide[1];
//				edgeCount = 0;
//				for (int k = 0; k < 3; k++) {
//					edgeQueue[k] = null;
//				}
//			}
//		}
//		return edgeCount;
//	}
//
//	 public int goToState(BR_Controller brController, ProblemNode currentNode) {
//		 
//		 ProblemNode startNode = brController.getProblemModel().getStartNode();
//		 if(startNode == null)
//			 return -1;
//		 ProblemEdge[] edgeQueue = new ProblemEdge[3];
//		 String problem[] = new String[1];
//		 problem[0] = startNode.getName();
//
//		 int edgeCount = 0;
//		 // Do nothing for the start state...
//		 if (currentNode != startNode) {
//			 // Traverse the edges on the path but the last one
//			 Vector /* ProblemEdge */ pathEdges = findPathDepthFirst(startNode, currentNode);
//			 // Returns the last complete equation as problem[0] 
//			 // and the following intermediate steps as edgeQueue[] 
//			 edgeCount = searchLastEquation(problem, edgeQueue, pathEdges);
//		 }
//		 
//		 problem[0] = problem[0].replaceAll(SimSt.EQUAL_SIGN, "=");
//		 problem[0] = problem[0].replaceAll(SimSt.SLASH, "/");
//		 problem[0] = problem[0].replaceAll(SimSt.OPEN_PAREN, "(");
//		 problem[0] = problem[0].replaceAll(SimSt.CLOSE_PAREN, ")");
//		 problem[0] = problem[0].replaceAll(SimSt.DECIMAL,".");
//		 startProblem(this.solverTutor, problem[0]);
//		 setProblemStep(problem[0]);
//		 resetCurrentSkill();
//		 for (int i = 0; i < edgeQueue.length; i++) {
//			 ProblemEdge edge = edgeQueue[i];
//			 
//			 if (edge != null) {
//				 
//				 EdgeData edgeData = edge.getEdgeData();
//				 String selection = (String)edgeData.getSelection().get(0);
//				 if(selection.equalsIgnoreCase(Rule.DONE_NAME))
//				 	return edgeCount;
//				 char table = selection.charAt(SimSt.COMM_STEM.length());
//				 if(table != '1') //Not Single table format.  Switch multi table format to multi column, single table
//				 {
//					int rowIndex = selection.indexOf('R')+1;
//			    	char row = selection.charAt(rowIndex);
//			    	selection = SimSt.COMM_STEM+"1_C"+table+"R"+row;
//				 }
//				 String action = "";
//				 String input = (String)edgeData.getInput().get(0);
//				 doStep(selection, action, input);
//				 updateCurrentSkill(input);
//			 }
//		 }
//		 return edgeCount;
//	 }
//
//
//	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *
//	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *
//	// Perform a step given (a set of) SAI
//	private void doStep(String selection, String action, String input) {
//		trace.out("doStep(" + selection + "," + action + "," + input
//				+ ")");
//		SAI sai = generateClSAIfromCtatSAI(selection, action, input);
//		String clAction = sai.getAction();
//		if (!isRedundantTypein(clAction)) {
//			// trace.out("SAI = " + sai);
//			try {
//				this.solverTutor.doStep(sai);
//			} catch (InvalidStepException e) {
//				e.printStackTrace();
//			}
//			if (!isTypeinAction(clAction)) {
//				if (!isBasicSkill(clAction)) {
//					SAI nextStep = whatToDoNext();
//					String equation = nextStep.getSelection();
//					setTypeinExpressions(equation);
//				}
//				setBasicSkillApplied(isBasicSkill(clAction));
//			}
//		} else {
//			// Redundant typein hence the input must be removed from
//			// typeinExpressions
//			removeTypeinExpression(input);
//		}
//	}
//	
//    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    // Set the current state one step before the node
//    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//
//    // Set to be true only when the method clAlgebraTutorGotoOneStateBefore() 
//    // is called to gather hint messages
//    private boolean askingHintOn = false;
//    public void setAskingHintOn() { this.askingHintOn = true; }
//    public void resetAskingHintOn() { this.askingHintOn = false; }
//    private boolean isAskingHintOn() {
//        return askingHintOn;
//    }
//
//    
//	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
//	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * 
//	// Ask for a hint on what to do next
//	public SAI getNextStep() {
//		SAI nextStep = whatToDoNext();
//		String msg = nextStep.getSelection() + ";" + nextStep.getAction() + ";" + nextStep.getInput();
//		trace.out("getNextStep >> " + msg);
//		return nextStep;
//	}
//
//	private SAI whatToDoNext() {
//		// trace.out("whatToDoNext called ...");
//		SAI nextStep = null;
//		if (isNonBasicSkillApplied()) {
//			for (int i = 0; i < 2; i++) {
//				if (typeinExpressions[i] != null) {
//					// i = 0 for "left" and 1 for "right"
//					nextStep = new SAI("", i == 0 ? "left" : "right", typeinExpressions[i]);
//					break;
//				}
//			}
//		} else {
//			nextStep = this.solverTutor.getNextStep("tutor");
//		}
//		
//		return nextStep;
//	}
//
//	public void startProblem(SolverTutor solverTutor, String problem) {
//		trace.out("startProblem(" + problem + ")");
//		String solverProblem = problem + ";" + "solve!";
//		solverTutor.startProblem(solverProblem);
//		resetBasicSkillApplied();
//		resetTypeinExpressions();
//	}
//
//	private Object getSelectionColumn(String selection) {
//		int indexC = selection.indexOf('C');
//		int indexR = selection.indexOf('R');
//		String selectionColumn = selection.substring(indexC + 1, indexR);
//		return selectionColumn;
//	}
//	
//
//	public boolean isCorrectStep(String selection, String action, String input) {
//		
//		boolean isCorrectStep = false;
//		
//		SAI sai = generateClSAIfromCtatSAI(selection, action, input);
//
//		if (isRedundantTypein(sai.getAction())) {
//			// the step is a typing-in the "input" for non-basic skill
//			int side = lookupTypeinExpression(sai.getInput().toString());
//			if (side != -1) {
//				removeTypeinExpression(side);
//				isCorrectStep = true;
//			}
//		} else {
//			// the step is either typing-in for a basic skill or applying a
//			// skill
//			try {
//				isCorrectStep = this.solverTutor.isStepOK(sai);
//			} catch (InvalidStepException e) {
//				e.printStackTrace();
//			}
//			// trace.out("isCorrectStep(" + sai + ") = " +
//			// isCorrectStep);
//		}
//		return isCorrectStep;
//	}
//
//	private SAI generateClSAIfromCtatSAI(String selection, String action,
//			String input) {
//		
//		CtatSAI ctatSAI = new CtatSAI(selection, action, input);
//		SAI sai = new SAI(" ", ctatSAI.getClAction(), ctatSAI.getClInput());
//		return sai;
//	}
//
//	private boolean isRedundantTypein(String action) {
//
//		boolean isRedundantTypein = false;
//		if (isTypeinAction(action)) {
//			isRedundantTypein = isNonBasicSkillApplied();
//		}
//		return isRedundantTypein;
//	}
//
//	private boolean isTypeinAction(String action) {
//		boolean isTypeinAction = "left".equals(action)
//				|| "right".equals(action);
//		// trace.out("isTypeinAction(" + action + ") = " +
//		// isTypeinAction);
//		return isTypeinAction;
//	}
//
//
//	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//	// = = =
//	// SAI for CTAT
//	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//	// = = =
//
//	class CtatSAI {
//
//		// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - +
//		// Fields
//		// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - +
//
//		String selection = null;
//		String action = null;
//		String input = null;
//		String clSelection = null;
//		String clAction = null;
//		String clInput = null;
//
//		public String getClAction() {
//			return clAction;
//		}
//
//		public String getClInput() {
//			return clInput;
//		}
//
//		// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - +
//		// Constructor
//		// - + - + - + - + - + - + - + - + - + - + - + - + - + - + - +
//
//		public CtatSAI(String selection, String action, String input) {
//			this.selection = selection;
//			this.action = action;
//			this.input = input;
//
//			// For CL SolverTutor, an "action" is an operator defined in
//			// SolverOperation,
//			// which is encoded in an "input" in the CTAT tutor.
//			// For skillOperand (e.g., "add -1")
//			// ClAction is the skill
//			// ClInput is the operand
//			// For typein (e.g., "3x+1")
//			// ClAction is left, if ctatSelection is for LHS
//			// right, if ctatSelection is for RHS
//			// ClInput is the same as ctatInput
//			String column = getSelectionColumn();
//			if (column.equals("3")) {
//				// The SAI is for skill-operand
//				String[] tokens = input.split(" ");
//				clAction = tokens[0];
//				clInput = (tokens.length == 2 ? tokens[1] : "");
//			} else {
//				// The SAI is for type-in
//				// selection -> "commTable1_C3R1"
//				String indexStr = getSelectionColumn();
//				// trace.out("selection: " + selection + ", indexStr: "
//				// + indexStr);
//				int index = Integer.parseInt(indexStr);
//				clAction = (index == 1 ? "left" : "right");
//				clInput = input;
//			}
//		}
//
//		private String getSelectionColumn() {
//			int indexC = selection.indexOf('C');
//			int indexR = selection.indexOf('R');
//			String indexStr = selection.substring(indexC + 1, indexR);
//			return indexStr;
//		}
//
//		private SAI generateClSAIfromCtatSAI(String selection, String action,
//				String input) {
//			CtatSAI ctatSAI = new CtatSAI(selection, action, input);
//			SAI sai = new SAI("", ctatSAI.getClAction(), ctatSAI.getClInput());
//			return sai;
//		}
//
//	} // end
//
//	static String problem = "-3x+2 = -2x+9";
//	static String selection = "commTable1_C3R1";
//	static String action = "UpdateTable";
//	static String input = "add -2";
//	static String[] steps = { "commTable1_C3R1", "add -2",
//			"commTable1_C1R2", "3x", };
//}
