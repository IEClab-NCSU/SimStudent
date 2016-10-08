package edu.cmu.pact.BehaviorRecorder.Controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.NewProblemEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerPath;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerSAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.Skill;
/**
 * This class is the repository for all code having directly to do with example tracing.
 * @author mpschnei
 *
 */
public class PseudoTutorMessageHandler implements ProblemModelListener {

	public static final String END_OF_TRANSACTION = "end_of_transaction";

    public static boolean USE_NEW_EXAMPLE_TRACER = true;
   
    // hold the respond Comm messages
    // private Vector messageTank;
    private MessageTank messageTank;

    // used for the option: find the desired hints
    public static final int FIND_HINT = 0;

    // used for the option: find the desired skills
    public static final int FIND_SKILLS = 1;

    // used for the option: find the desired working widget
    public static final int FIND_HIGHLIGHT = 2;

    // whatever the 1st selected widget name
    public static final int FIRST_SELECTED_WIDGET_NAME = 0;

    // the 1st none hint selected widget name
    public static final int NONE_HINT_FIRST_SELECTED_WIDGET_NAME = 1;
    
    // temp use to access methods on BR_Controller
    // later move related methods on solutionStateMode
    BR_Controller controller;
    
    private ExampleTracerGraph exampleTracerGraph;
   // private ExampleTracerTracer exampleTracer; 
    
    public PseudoTutorMessageHandler(BR_Controller controller) {
        this.controller = controller;
        messageTank = new MessageTank(controller);
        exampleTracerGraph = controller.getExampleTracerGraph();
        if(controller.getCtatModeModel()!=null)
        	exampleTracerGraph.getExampleTracer().setDemonstrateMode(
        				controller.getCtatModeModel().isDemonstratingSolution());
        traversedEdges = new ArrayList();
    }


    /** List of {@link ProblemEdge} instances traversed, beginning at start state. */
	private List traversedEdges;
    

    
    public ExampleTracerTracer getExampleTracer(){
    	return exampleTracerGraph.getExampleTracer();
    }

	/**
	 * Reset the data structures for example tracing and send the start state
	 * messages to reinitialize the student interface.
	 * @param delayStartStateEnd to tell {@link BR_Controller#sendStartNodeMessages(String, boolean)}
	 *        to suppress StartStateEnd until after advance
	 */
	public void initializePseudoTutorAndSendStartState(boolean delayStartStateEnd) {
		initializePseudoTutor();
		controller.sendStartNodeMessages(null, delayStartStateEnd);  // null: no change to problemName
		if (controller.getProblemModel().isUseCommWidgetFlag())
			controller.setPreferredWidgetFocus();
		//kurwa.. put breakpoint here.. see if this is good point for an init.
		// sewall 8/31/09: init'ze interps vt after start state
		exampleTracerGraph.getExampleTracer().initialize(controller.getProblemModel().getVariableTable());  
	}
    
    /**
     * Clear out the data structures before starting a new problem.
     */
    public void initializePseudoTutor() {
           	
    	// roll back commutEdgesAdded & userVisitedStates
    	//kurwa probably not the right point
    	//exampleTracerGraph.resetExampleTracer(controller.getProblemModel().getVariableTable());
    	
    	// sewall 2010-10-14: clear existing interpretations
    	exampleTracerGraph.getExampleTracer().initialize(null);  // null => no variable table
    	traversedEdges = new ArrayList();

        trace.out ("inter", "initializePseudoTutor()");
        controller.getHintMessagesManager().cleanUpHintOnChange();

        // roll back font for each element in commutEdgesAdded
        ProblemEdge tempEdge;
        EdgeData tempMyEdge;

        if (controller.getLogger() != null) {
        	controller.getLogger().setProblemName(controller.getProblemModel().getProblemName());
        	//controller.getLogger().setProblemContext(controller.getProblemModel().getProblemFullName());
        }
        controller.sendResetBeforeTraverseToClickedNode();

        if (controller.getProblemModel().getStartNode() != null) {
            controller.setCurrentNode(controller.getProblemModel().getStartNode());
           
           /* if (controller.getProblemModel().isUnorderedMode())
            {
            	//controller.getSolutionState().buildAllCandidatePathsList();
            	
            }
            else
            {*/
            	/*controller.getSolutionState().initializeCurrentGroupsData(
            			controller.getProblemModel().getStartNode());*/
           // }
            // sewall 3/26/07: moved to initializePseudoTutorAndSendStartState(),
            //  since relies on start state msgs having been sent
//          if (controller.getProblemModel().isUseCommWidgetFlag())
//          controller.setPreferredWidgetFocus();
        }

        return;
    }

    /**
     * Test whether need to highlight the right working widget based on the
     * preferences setting.
     */

    private boolean isHighLightRightSelection() {
        boolean highlightRightSelectionFlag = false;
        ProblemModel pm = controller.getProblemModel();
        if (pm != null)
        	highlightRightSelectionFlag = pm.getHighlightRightSelection();

        return highlightRightSelectionFlag;
    }

    /**
     * Transit an edge in the graph as if the student entered the given s,a,i.
     * @param tpaEdge edge for the tutor-performed action
     * @param selection student's selection
     * @param action student's action
     * @param input student's input
     * @param actor one of {{@link Matcher#DEFAULT_TOOL_ACTOR}, {@link Matcher#DEFAULT_STUDENT_ACTOR}}
     */
    public void traverseEdge(EdgeData edgeData, Vector selection, Vector action, Vector input,
    		String actor) {
    	doNewExampleTrace(edgeData, selection, action, input, actor, false);
    	if (trace.getDebugCode("et")) trace.out("et", "*******************Should have advanced");
    }

	/**
	 * Traverse from the start state to the given state.
	 * @param toNode
	 */
	public ExampleTracerPath advanceToNode(ProblemNode toNode) {
        if (controller == null)
        	return null;
        initializePseudoTutorAndSendStartState(false);
        ProblemNode currentNode = controller.getCurrentNode();
        if (trace.getDebugCode("pm")) trace.out("pm", "advanceToNode("+currentNode+"=>"+toNode+")");
        if (toNode == currentNode)
        	return null;
        ExampleTracerPath bestPathToNode = controller.getProblemModel().findPath(toNode);
        for (ExampleTracerLink link : bestPathToNode) {
        	EdgeData edgeData = link.getEdge();
        	if (edgeData == null || edgeData.getEdge() == null)
        		break;
        	if (edgeData.getMinTraversals() < 1 && edgeData.getMaxTraversals() <= edgeData.getTraversalCount())
        		continue;
        	controller.getPseudoTutorMessageHandler().traverseEdge(edgeData,
        			edgeData.getSelection(), edgeData.getAction(), edgeData.getInput(),
        			edgeData.getActor());
        }
        return bestPathToNode;
	}

	public List<ExampleTracerEvent> traversePath(ExampleTracerPath newUserVisitedStates) {
		if (trace.getDebugCode("br")) trace.out("br", "traversePath("+newUserVisitedStates+")");
		List<ExampleTracerEvent> results = new ArrayList<ExampleTracerEvent>();
		traversedEdges = getExampleTracer().evaluateEdges(newUserVisitedStates, results); 
    	return results;
	}
    
    /**
     * Generate a {@link edu.cmu.pslc.logging.ToolMessage} log entry, to create
     * a DataShop transaction where we don't normally have one. E.g., for tutor-
     * performed steps.
     * @param selection 
     * @param action
     * @param input
     * @return new message's transaction_id
     */
	String enqueueToolActionToStudent(Vector selection, Vector action,
			Vector input) {
		return messageTank.enqueueToolActionToStudent(selection, action, input,
				PseudoTutorMessageBuilder.TUTOR_PERFORMED);
	}

	/**
	 * Traverse to the {@link ProblemModel#getStudentBeginsHereState()}.
	 * @returns number of links traversed
	 */
	public int advanceToStudentBeginsHere() {
		ProblemModel pm;
		int result = 0;
        if (controller == null || (pm = controller.getProblemModel()) == null)
        	return result;
        ProblemNode studentBeginsHere = pm.getStudentBeginsHereState();
        if(studentBeginsHere == null)
        	return result;
        else
        	return advanceToStudentBeginsHere(studentBeginsHere);
	}

	/**
	 * Traverse to the given state as if it were the state returned by
	 * {@link ProblemModel#getStudentBeginsHereState()}: logging is suppressed.
	 * @param targetState
	 * @returns number of links traversed
	 */
	public int advanceToStudentBeginsHere(ProblemNode targetState) {
		ProblemModel pm;
		int result = 0;
        if (controller == null || (pm = controller.getProblemModel()) == null)
        	return result;
        ProblemNode currentNode = controller.getCurrentNode();
        if (trace.getDebugCode("pm"))
        	trace.out("pm", "advanceToStudentBeginsHere("+currentNode+"=>"+targetState+")");
    	ProblemNode lastEdgeDest = null;
        try {
        	messageTank.setSuppressLogging(Boolean.TRUE);        // DO NOT return before clearing
        	messageTank.enableBundle(MsgType.StartStateMessages, "StartStateEnd");

        	if (targetState != currentNode) {
        		ExampleTracerPath bestPathToNode = pm.findPath(targetState);
        		if (trace.getDebugCode("pm"))
        			trace.out("pm", "advanceToStudentBeginsHere() path "+bestPathToNode);
        		for (ExampleTracerLink link : bestPathToNode) {
        			EdgeData edgeData = link.getEdge();
        			lastEdgeDest = edgeData.getEndProblemNode();
        			if (edgeData == null || edgeData.getEdge() == null)
        				break;
        			if (edgeData.getMinTraversals() < 1 && edgeData.getMaxTraversals() <= edgeData.getTraversalCount())
        				continue;
        			String actor = edgeData.getActor();
        			if (!Matcher.isTutorActor(actor, false))
        				actor = Matcher.DEFAULT_TOOL_ACTOR;
        			if (trace.getDebugCode("actor"))
        				trace.out("actor", "PsMH.advanceToStudentBeginsHere() edgeData.getActor() "+
        						edgeData.getActor()+", actor "+actor);
        			traverseEdge(edgeData,
        					edgeData.getSelection(), edgeData.getAction(), edgeData.getInput(), actor);
        			result++;
        		}
        	}
            if (trace.getDebugCode("br"))  // sewall 2013/04/25: match trace code in BR_C.sendStartNodeMessages()
            	trace.out("br", "advanceToStudentBeginsHere() to send StartStateEnd");
            controller.sendStartStateMsg(PseudoTutorMessageBuilder.createLockWidgetMsg(pm.getLockWidget()));
        	controller.sendStartStateMsg(PseudoTutorMessageBuilder.createStartStateEndMsg());
        } finally {
        	messageTank.disableBundle(MsgType.StartStateMessages);
        	messageTank.setSuppressLogging(null);       // DO NOT return before clearing
        }
        // sewall 2012/12/05: enable TPAs out of the start state
        checkForTutorAction(lastEdgeDest,
        		getExampleTracer().getCurrentNode(false, false),  // should be targetState
        		null, null, null);                                // null args: use s,a,i from link
        return result;
	}

	/**
	 * Equivalent to {@link #processPseudoTutorInterfaceAction(Vector, Vector, Vector, String)
	 * processPseudoTutorInterfaceAction(Vector, Vector, Vector, "Student")}.
	 * @param selection
	 * @param action
	 * @param input
	 */
	public void processPseudoTutorInterfaceAction(Vector selection, Vector action, Vector input) {
    	processPseudoTutorInterfaceAction(selection, action, input, "Student");
    }

	/**
	 * Process an InterfaceAction through the ExampleTracer.
	 * @param selection
	 * @param action
	 * @param input
	 * @param actor
	 */
    void processPseudoTutorInterfaceAction(Vector selection,
            Vector action, Vector input, String actor) {

    	ProblemModel pm = null;
        if (controller != null && (pm = controller.getProblemModel()) != null)
        	controller.getProblemModel().startSkillTransaction();
    	if (messageTank != null)
    		messageTank.resetMessageTank();
    	else
    		messageTank = new MessageTank(controller);
    	
    	boolean allowHintBias = messageTank.editSelectionAndAction("InterfaceAction", selection, action) 
    								|| pm.areHintsBiasedByCurrentSelection();
    	
    	doNewExampleTrace(selection, action, input, actor, allowHintBias);
    	confirmDoneAction(selection);
    	messageTank.flushMessageTank(controller.getProblemSummary());  // send all the messages collected
    	if (pm != null)
    		pm.checkRequestGoToState();
    	controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
    }
	
	/**
	 * Perform an example-trace.
	 * @param selection student selection from prior step
	 * @param action student action from prior step
	 * @param input student input from prior step
	 * @param actor
	 * @return trace result 
	 */
	private ExampleTracerEvent doNewExampleTrace(Vector selection, Vector action, Vector input, 
			String actor, boolean allowHintBias) {
		ExampleTracerEvent[] rtnResult = new ExampleTracerEvent[1];
		if (trace.getDebugCode("et")) trace.out("et", "doNewExampleTrace: selection = " +
				selection + " action = " + action + " input = " + input + " actor = " + actor);
		String firstSelection = (String) selection.get(0);
		
		if (firstSelection.equalsIgnoreCase("Help") || firstSelection.equalsIgnoreCase("Hint")) {
			ProblemEdge hintLink =
				getExampleTracer().doHint(selection, action, input, actor, rtnResult, allowHintBias);
			ExampleTracerEvent result = rtnResult[0];
			MessageObject mo;
			ProblemSummary ps = controller.getProblemSummary();
			if (hintLink != null) {
				if (result.isSolverResult()) {
					String stepID = Skill.makeStepID(result.getTutorSelection(), result.getTutorAction());
					controller.getProblemModel().updateSkills(Skill.HINT, result.getSkillNames(), stepID);
					mo = PseudoTutorMessageBuilder.buildHintsMsg(result.getTutorAdvice(),
							result.getTutorSelection(), result.getTutorAction(),
							result.getTutorInput(), Integer.toString(hintLink.getUniqueID()), null,
							null, controller);
					messageTank.addToMessageTank(mo);
					messageTank.addToMessageTank(PseudoTutorMessageBuilder.buildAssocRulesFromEvent(result, controller), result);
				} else {
					hintLink.getEdgeData().updateSkills(Skill.HINT);
					mo = PseudoTutorMessageBuilder.buildHintsMsg(hintLink, controller);
					messageTank.addToMessageTank(mo);
					mo = PseudoTutorMessageBuilder.buildAssociatedRules(hintLink,
							PseudoTutorMessageBuilder.HINT, actor, controller, null);
					messageTank.addToMessageTank(mo);
				}
			} else {
				// here if can find no link to suggest; e.g. request hint after successful Done?
				mo = PseudoTutorMessageBuilder.buildNoHintMessage(controller);
				messageTank.addToMessageTank(mo);
				Vector<String> noHints = new Vector<String>();
				noHints.add("No hint available.");
				mo = PseudoTutorMessageBuilder.buildAssociatedRulesMsg(PseudoTutorMessageBuilder.HINT,
						selection, action, input, null, null,
						null, null, "", actor, controller,  // empty string is null step ID
						null, noHints);
				messageTank.addToMessageTank(mo);
			}
			
			messageTank.flushMessageTank(ps);
            return rtnResult[0];
		}
        
		getExampleTracer().evaluate(selection,action,input,actor);
		return finishNewExampleTrace(selection, action, input, actor, true);
	}
	
	/**
	 * Perform an example-trace on a preselected edge, as when doing a tutor-performed step.
	 * @param edgeData edge already selected
	 * @param selection student selection from prior step
	 * @param action student action from prior step
	 * @param input student input from prior step
	 * @param actor
	 * @param doTutorPerformedSteps
	 * @return result from the step traversed
	 */
	private ExampleTracerEvent doNewExampleTrace(EdgeData edgeData, Vector selection, Vector action,
			Vector input, String actor, boolean doTutorPerformedSteps) {
		if (trace.getDebugCode("actor"))
			trace.out("actor", "PSMH.doNewExampleTrace(edge"+edgeData.getUniqueID()+","+selection+
					","+action+","+input+","+actor+")");
        if (controller != null && controller.getProblemModel() != null)
        	controller.getProblemModel().startSkillTransaction();
        //trace.out("mg", "PseudoTutorMessageHandler (doNewExampleTrace): calling printFocus");
        //this.controller.printFocus();
        //this.controller.getExampleTracer()
        getExampleTracer().evaluate(edgeData.getUniqueID(), edgeData.getSelection(), edgeData.getAction(),
        //this.controller.getExampleTracer().evaluate(edgeData.getUniqueID(), edgeData.getSelection(), edgeData.getAction(),
				input, actor);
		ExampleTracerEvent result = getExampleTracer().getResult();
    	String transaction_id = enqueueToolActionToStudent(result.getTutorSelection(), result.getTutorAction(),
    			result.getTutorInput());
    	setTransactionId(transaction_id);
		return finishNewExampleTrace(result.getTutorSelection(), result.getTutorAction(), 
				result.getTutorInput(), actor, doTutorPerformedSteps);
	}
	
	/**
	 * Factored out common conclusion steps to
	 * {@link #doNewExampleTrace(EdgeData, Vector, DVector, Vector, String)}
	 * @param selection
	 * @param action
	 * @param input
	 * @param actor
	 * @param doTutorPerformedSteps whether to go on to traverse a tutor-performed step
	 * @return result of the last step traversed, including tutor-performed steps
	 */
	ExampleTracerEvent finishNewExampleTrace(Vector selection, Vector action, Vector input, String actor,
			boolean doTutorPerformedSteps) {
		ExampleTracerEvent result = getExampleTracer().getResult();
		int depthSoFar = -1;
		
		ProblemModel pm = controller.getProblemModel();
		if (trace.getDebugCode("et")) trace.out("et", "finishNewExampleTrace() result = "+result);
		
		if (result.getReportableVariableTable() != null)
			pm.setVariableTable(result.getReportableVariableTable());
		
		ProblemEdge[] edges = new ProblemEdge[1];
		if(result.getReportableLink()!=null)
			edges[0] = result.getReportableLink().getEdge().getEdge();
		else
			edges[0] = null;
		trace.out ("et", "edges = " + Arrays.asList(edges));
		
		EdgeData edgeData = null;
		if (edges[0] != null) {
			edgeData = edges[0].getEdgeData();
			// Jim: insert here updates to set student vars in
			// edgeData.getProblemModel().setVariable(name, value)
			// edgeData.getName()+".sSelection", "~.sAction" , "~.sInput" to
			// selection.get(0), action.get(0), input.get(0)
		}
		
		if (result.isSolverResult()) {
			String transactionResult = ExampleTracerTracer.CORRECT_ACTION.equals(result.getResult()) ?
        			Skill.CORRECT : Skill.INCORRECT; 
			pm.updateSkills(transactionResult, result.getSkillNames(),
					Skill.makeStepID(result.getTutorSelection(), result.getTutorAction()));
    		ProblemNode newCurrentNode = getExampleTracer().getCurrentNode(false, false);
        	if (result.isSolverDone() && newCurrentNode != null)
        			controller.setCurrentNode(newCurrentNode);  // sewall 06/05/11
    		sendSolverResponse(result);
        	if (result.isSolverDone() && doTutorPerformedSteps) {
                	ExampleTracerEvent tpaResult =
                			checkForTutorAction(edges[0] == null ? null : edges[0].getDest(),
                					newCurrentNode, selection, action, input);
                	if (tpaResult != null)
                		result = tpaResult;
        	}
		} else if (ExampleTracerTracer.CORRECT_ACTION.equals(result.getResult())) {
			MessageObject message = null;
			Vector replacementInput = replaceInput(edgeData, selection, action, input, result);

			// construct correct msg
			if (!Matcher.UNGRADED_TOOL_ACTOR.equalsIgnoreCase(actor)) {
				message = PseudoTutorMessageBuilder.buildCommCorrectMessage(selection, action,
						(replacementInput == null ? input : replacementInput), controller);
				messageTank.addToMessageTank(message, result);
			}
            if (edges[0] != null) {
                controller.getProcessTraversedLinks().addLinkNode(edges[0].getUniqueID(),
                		selection, action, input, EdgeData.CORRECT_ACTION);
            	controller.getSolutionState().addUserVisitedEdge(edges[0]);
            	ProblemNode newCurrentNode = getExampleTracer().getCurrentNode(false);
            	if (newCurrentNode != null)
                	controller.setCurrentNode(newCurrentNode);  // sewall 06/05/11
//              controller.setCurrentNode(edges[0].getDest()); bug: advanced state in unordered
            }
            if (edgeData != null && !Matcher.isTutorActor(actor, false))
            	edgeData.updateSkills(Skill.CORRECT);
            String successMessage = edgeData.getInterpolatedSuccessOrBuggyMsg(true).trim();
            trace.out("success message = " + successMessage);
            if (successMessage.length() > 0) {
            	message = PseudoTutorMessageBuilder.buildCommSuccessMessage(successMessage, controller);
            	messageTank.addToMessageTank(message, result);
            }
            createAssocRulesCorrectMsg(edges[0], selection, action, input, actor, replacementInput, result);
            messageTank.flushMessageTank(controller.getProblemSummary());  // send all the messages collected: sewall 3/20/07
            if (doTutorPerformedSteps) {
            	ExampleTracerEvent tpaResult =
            			checkForTutorAction(edges[0] == null ? null : edges[0].getDest(),
            					getExampleTracer().getCurrentNode(false, false), selection, action, input);
            	if (tpaResult != null)
            		result = tpaResult;
            }
        } else {
            // construct incorrect msg
        	MessageObject message = null;
        	Vector replacementInput = null;
            if (ExampleTracerTracer.SUBOPTIMAL_ACTION.equals(result.getResult()))
            	replacementInput = replaceInput(edgeData, selection, action, input, result);
            message = PseudoTutorMessageBuilder.buildCommIncorrectMessage(selection, action,
            		(replacementInput == null ? input : replacementInput), controller);
            messageTank.addToMessageTank(message);
            
            if (edgeData != null) {              // matched to incorrect link
            	String buggyMessage = edgeData.getInterpolatedSuccessOrBuggyMsg(false).trim();
	            if (buggyMessage.length() != 0) {
	                message = PseudoTutorMessageBuilder.buildCommBuggyMessage(buggyMessage,
	                		selection, action, controller);
	                if (trace.getDebugCode("et")) trace.out("et", "finishNewExampleTrace() buggy message: " + message);
	                messageTank.addToMessageTank(message, result);
	            }
            }

        	// find what step the student should be working on
        	ExampleTracerEvent hintResult = new ExampleTracerEvent(this,
        			new ExampleTracerSAI(selection, action, input, actor));
        	ProblemEdge hintLink = getExampleTracer().traceForHint(hintResult);
        	depthSoFar = (hintResult.getReportableLink() == null ?
        			-1 : hintResult.getReportableLink().getDepth()); 
        	if (hintLink == null)
        		hintLink = getExampleTracer().getBestNextLink(false, new ExampleTracerEvent(this));
			if (hintLink != null && hintLink.getEdgeData() != null)    // chc 10/20/2008
				if (!Matcher.isTutorActor(actor, false))
					hintLink.getEdgeData().updateSkills(Skill.INCORRECT);
            if (trace.getDebugCode("et"))
            	trace.out("et", "finishNewExampleTrace() hinkLink "+hintLink);

            if (ExampleTracerTracer.SUBOPTIMAL_ACTION.equals(result.getResult())) {
                if (edges[0] != null) {
                    controller.getProcessTraversedLinks().addLinkNode(edges[0].getUniqueID(),
                    		selection, action, input, EdgeData.FIREABLE_BUGGY_ACTION);
                	controller.getSolutionState().addUserVisitedEdge(edges[0]);
                	ProblemNode newCurrentNode = getExampleTracer().getCurrentNode(false);
                	if (newCurrentNode != null)
                    	controller.setCurrentNode(newCurrentNode);  // sewall 06/05/11
//                	controller.setCurrentNode(edges[0].getDest()); see bug this date above
                }
				MessageObject newMessage =
					PseudoTutorMessageBuilder.buildAssociatedRules(hintLink == null ? edges[0] : hintLink,
							PseudoTutorMessageBuilder.INCORRECT, actor, controller, 
							(result.getStudentSAI() == null ? null : result.getStudentSAI().getSelectionAsString()));
				messageTank.addToMessageTank(newMessage, result);
				messageTank.flushMessageTank(controller.getProblemSummary()); // send all the messages
												// collected: sewall 3/20/07
	            if (doTutorPerformedSteps)
	            	result = checkForTutorAction(edges[0] == null ? null : edges[0].getDest(),
	            			getExampleTracer().getCurrentNode(false, false), selection, action, input);
            } else {
            	Vector hintSelection = (hintLink != null ? hintLink.getEdgeData().getSelection() : null);
            	String hintSelection0 =
            		(String) (hintSelection != null && hintSelection.size() > 0 ? hintSelection.get(0) : null);
            	Vector hintAction = (hintLink != null ? hintLink.getEdgeData().getAction() : null);

            	if (result.isDoneStepFailed()) {
            		messageTank.flushDelayedFeedback();
            		message = PseudoTutorMessageBuilder.buildCommBuggyMessage(BR_Controller.NOT_DONE_MSG,
            				null, null, controller);
            		messageTank.addToMessageTank(message, result);
            	}				// if no buggy link matched and a step is out of order
            	else {
            		if (trace.getDebugCode("et")) trace.outNT("et", "before out-of-order test"
            				+" result "+result.getResult()+" ?= "+ExampleTracerTracer.NULL_MODEL
            				+"&& (result.isOutOfOrder() "+result.isOutOfOrder()
            				+" || hintResult.isOutOfOrder() "+hintResult.isOutOfOrder()
            				+") && (hintSelection0 "+hintSelection0+" != "
            				+" result.getSelectionAsString() "+result.getSelectionAsString()+")");
            		if (ExampleTracerTracer.NULL_MODEL.equals(result.getResult()) &&
            				(result.isOutOfOrder() || hintResult.isOutOfOrder()) &&  // useless but harmless: hintResult never outOfOrder?
            				hintSelection0 != null &&
            				!(hintSelection0.equalsIgnoreCase(result.getSelectionAsString()))) {            			
            			if (isHighLightRightSelection())
            				message = PseudoTutorMessageBuilder.buildCommHighLightWidgetMessage(
            						hintSelection, hintAction, controller);
            			else
            				message = PseudoTutorMessageBuilder.buildCommBuggyMessage("You need to do"
            						+" other steps first, before doing the step you just worked on."
            						+" You might request a hint for more help.",
            						null, null, controller);
            			if (trace.getDebugCode("et")) trace.out("et", "doNewExampleTrace() out-of-order message: " + message);
            			messageTank.addToMessageTank(message, result);
            		}
            	}
            	MessageObject newMessage = null;
            	if(hintLink != null)
            		newMessage = PseudoTutorMessageBuilder.buildAssociatedRules(hintLink,
            				PseudoTutorMessageBuilder.INCORRECT, actor, controller, 
            				(result.getStudentSAI() == null ? null : result.getStudentSAI().getSelectionAsString()));
            	else  // sewall 2013-05-23: can have no hint link
    				newMessage = PseudoTutorMessageBuilder.buildAssociatedRulesMsg(PseudoTutorMessageBuilder.INCORRECT,
    						selection, action, input, null, null,
    						null, null, "", actor, controller,  // empty string is null step ID
    						null, null);
            	messageTank.addToMessageTank(newMessage, result);
                messageTank.flushMessageTank(controller.getProblemSummary());  // send all the messages collected: sewall 3/20/07
            }
        }
		
		if (depthSoFar < 0) {
			if (trace.getDebugCode("hints"))
				trace.out("hints", "finishNewExampleTrace() result "+result+
						" .reportableLink "+(result == null ? null : result.getReportableLink())+
						" .depth "+(result == null || result.getReportableLink() == null ?
								null : Integer.toString(result.getReportableLink().getDepth())));
			depthSoFar = (result == null || result.getReportableLink() == null ? -1 :
					result.getReportableLink().getDepth()+1);
		}
		doUnrequestedHints(null, depthSoFar);  // for Ilya Goldin, Sept 2012

		return result;
	}

	/**
	 * If indicated, replace the student's input with the result stored for this step in the
	 * {@link ExampleTracerEvent#getReportableVariableTable()}.
	 * @param edgeData matching correct edge 
	 * @param result for proper VariableTable
	 * @return replacementInput; null if no replacement indicated
	 */
	private Vector replaceInput(EdgeData edgeData, Vector selection, Vector action, Vector input,
			ExampleTracerEvent result) {
        if (edgeData == null || !edgeData.replaceInput())
        	return null;

        String vName = "link"+edgeData.getUniqueID()+".input";
        Object vValue = result.getVariableValue(vName);
        Vector replacementInput = null;
        if (vValue == null) {
			trace.err("replaceInput for "+vName+" found null");
			return null;
		} else if (!(vValue instanceof Vector))
			replacementInput = PseudoTutorMessageBuilder.s2v(vValue.toString());
		else
			replacementInput = (Vector) vValue;
        if (trace.getDebugCode("br")) trace.out("br", "replaceInput for "+vName+" found ("+
        		(vValue == null ? "" : vValue.getClass().getSimpleName())+") \""+vValue+"\"");
        return replacementInput;
	}

	/**
	 * Create an InterfaceAction(s) and other messages for a solver result.
	 * Sets suppressLogging for InterfaceAction messages created.
	 * Calls {@link MessageTank#flushMessageTank(ProblemSummary)}.
	 * @param result
	 */
	private void sendSolverResponse(ExampleTracerEvent result) {
		Vector<String> s = (Vector<String>) result.getStudentSAI().getSelectionAsVector();
		Vector<String> a = result.getStudentSAI().getActionAsVector();
		Vector<String> i = result.getStudentSAI().getInputAsVector();
		List<MessageObject> resps = new ArrayList<MessageObject>();
		ExampleTracerEvent.InterfaceAction ia = null;
		while (result.hasGradeableIAMsgs()) {
			ia = result.dequeueInterfaceAction();
			if (trace.getDebugCode("solverdebug")) trace.out("solverdebug", "sendSolverResponse() result "+result.getResult()+", "
					+ia.getIaAction()+", "+ia.getIaOutput()+", \""+ia.getPrompt()+"\", "+ia.getSolverProperties());
			i = ia.getIaOutput();
			resps.add(PseudoTutorMessageBuilder.buildInterfaceActionMsg(s, ia.getIaAction(), i,
					ia.getPrompt(), controller, MessageObject.makeTransactionId(), true));
		}
		messageTank.addToMessageTank(resps);
		resps.clear();                        // don't add them again below
		if (result.isTransaction()) {
			String tutorAdvice =
				(result.getTutorAdvice() == null || result.getTutorAdvice().size() < 1 ?
						null : result.getTutorAdvice().get(0));
			if (ExampleTracerTracer.CORRECT_ACTION.equalsIgnoreCase(result.getResult())) {
				resps.add(PseudoTutorMessageBuilder.buildCommCorrectMessage(s, a, i, controller));
				if (tutorAdvice != null)
					resps.add(PseudoTutorMessageBuilder.buildCommSuccessMessage(tutorAdvice, controller));
			} else {
				resps.add(PseudoTutorMessageBuilder.buildCommIncorrectMessage(s, a, i, controller));
				if (tutorAdvice != null)
					resps.add(PseudoTutorMessageBuilder.buildCommBuggyMessage(tutorAdvice, s, a, controller));
			}
			messageTank.addToMessageTank(resps);
			messageTank.addToMessageTank(PseudoTutorMessageBuilder.buildAssocRulesFromEvent(result, controller), result);
			resps.clear();
		}
		while (null != (ia = result.dequeueInterfaceAction())) {
			resps.add(PseudoTutorMessageBuilder.buildInterfaceActionMsg(s, ia.getIaAction(),
					ia.getIaOutput(), ia.getPrompt(), controller,
					MessageObject.makeTransactionId(), true));
		}
		messageTank.addToMessageTank(resps);
		messageTank.flushMessageTank(controller.getProblemSummary());
	}

	/**
	 * In suppress feedback mode, make sure the student really meant to advance
	 * the problem.  If so advance the problem regardless of whether or not
	 * they have actually completed it.
	 * @param selection student's selection from s-a-i
	 * @return true if this is a done step in suppress feedback mode
	 */
	private boolean confirmDoneAction(Vector selection) {
		String firstSelection = (String) selection.elementAt(0);

        if (!firstSelection.equalsIgnoreCase("Done"))
        	return false;
        ProblemModel pm = controller.getProblemModel();
        if (pm == null)
        	return false;
        if (!pm.getEffectiveConfirmDone())
        	return false;
        MessageObject msg = PseudoTutorMessageBuilder.buildConfirmDoneMsg(controller);
        messageTank.addToMessageTank(msg);
        return true;
    }

	/**
	 * Check whether there's a tutor-performed step at the given node in the
	 * graph and, if so, do it with {@link #traverseEdge(ProblemEdge, Vector, Vector, Vector)}
	 * @param priorEdgeDest the destination node of the last-matched link; can be null
	 * @param currNode the current node, as calculated by the example-tracer algo
	 * @param selection possible arg to {@link #doNewExampleTrace(EdgeData, Vector, Vector, Vector, String, boolean)}
	 * @param action same use as selection
	 * @param input same use as selection
	 * @return result from the last step traversed
	 */
	private ExampleTracerEvent checkForTutorAction(ProblemNode priorEdgeDest, ProblemNode currNode, 
			Vector selection, Vector action, Vector input) {
		EdgeData[] edges = new EdgeData[2];  // max 2 origin nodes
		edges[0] = nodeTutorActionFires(priorEdgeDest, true);
		edges[1] = nodeTutorActionFires(currNode, false);
		int i = 0;
		if (edges[0] == edges[1])
			edges[1] = null;          // fire the tutor action only once
		else if (edges[0] == null)
			i = 1;                    // skip null entry

		ExampleTracerEvent result = null;
		for (; i < edges.length && edges[i] != null; ++i) {
			if (selection == null)
				result = doNewExampleTrace(edges[i], edges[i].getSelection(), edges[i].getAction(), 
						edges[i].getInput(), edges[i].getActor(), true);
			else
				result = doNewExampleTrace(edges[i], selection, action, input,
						edges[i].getActor(), true);
		}
		return result;
	}

	/**
	 * Tell whether the given state has an outgoing tutor-performed action to fire. 
	 * @param src source state whose outgoing link(s) would be candidates
	 * @param linkTriggered true if action must be link-triggered, false if state-triggered
	 * @return EdgeData object if action should fire; else null if not
	 */
	private EdgeData nodeTutorActionFires(ProblemNode src, boolean linkTriggered) {
		if (src == null)
			return null;
		if (src.getOutDegree() != 1 )
			return null;                               // must be exactly one outgoing link
		EdgeData nextEdgeData = ((ProblemEdge) (src.getOutgoingEdges().get(0))).getEdgeData();
		if(nextEdgeData.isTutorPerformed(linkTriggered))
			return nextEdgeData;
		else
			return null;
	}

    /**
     * @param focusWidgetSelectionName
     * @param focusWidgetSelected
     */
  /*  public void processHintRequestOrderedMode(String focusWidgetSelectionName, boolean focusWidgetSelected) {
        boolean continueFlag = true;

        trace.out ("mps", "Process hint request in ordered mode: selected widget = " + focusWidgetSelectionName);
        // in case: currentGroup is not null in currentGroup
        if (controller.getSolutionState().getCurrentGroup() != null) {
            // trace.out(5, this, "currentGroup is not null");
            // 1st try find matched focused link with hint in currentGroup
            if (focusWidgetSelected)
                continueFlag = processValidSinglePath(controller.getSolutionState()
                        .getCurrentGroup(), focusWidgetSelectionName,
                        BR_Controller.FIND_HINT);

            // 2nd try find link with hint in currentGroup
            if (continueFlag)
                continueFlag = processValidSinglePath(controller.getSolutionState()
                        .getCurrentGroup(), "", BR_Controller.FIND_HINT);

            // no hint available
            if (continueFlag
//                    && controller.getMode()
//                            .equalsIgnoreCase(CtatModeModel.EXAMPLE_TRACING_MODE))
                    && controller.getCtatModeModel().isExampleTracingMode()) {
            	MessageObject mo = PseudoTutorMessageBuilder.buildNoHintMessage(controller);
            	messageTank.addToMessageTank(mo);
            	messageTank.flushMessageTank(controller.getProblemSummary());
            }
            return;
        }

        // trace.out(5, this, "currentGroup is null");
        // in case: currentGroup is null and current allowed groups are not
        // empty
        if (controller.getSolutionState().getCurrentAllowedGroups().size() > 0) {
            // trace.out(5, this, "currentAllowedGroups.size() = " +
            // currentAllowedGroups.size());
            Vector singleGroup;

            if (focusWidgetSelected) {
                // a. try matched focused preferrred child link with hint
                continueFlag = processOutStates(controller.getSolutionState()
                        .getCurrentNode(), focusWidgetSelectionName, false,
                        true, false, BR_Controller.FIND_HINT);

                // b. try find matched focused link with hint in
                // currentAllowedGroups
                if (continueFlag) {
                    for (int i = 0; i < controller.getSolutionState()
                            .getCurrentAllowedGroups().size()
                            && continueFlag; i++) {
                        singleGroup = (Vector) controller.getSolutionState()
                                .getCurrentAllowedGroups().elementAt(i);
                        continueFlag = processValidSinglePath(singleGroup,
                                focusWidgetSelectionName,
                                BR_Controller.FIND_HINT);
                    }
                }

                // c. try matched focused child link with hint
                if (continueFlag) {
                    continueFlag = processOutStates(controller.getSolutionState()
                            .getCurrentNode(), focusWidgetSelectionName,
                            false, false, false, BR_Controller.FIND_HINT);
                }
            }

            // repeat above 3 steps with focusWidgetSelectionName = ""
            // a*. try preferrred child link with hint
            if (continueFlag)
                continueFlag = processOutStates(controller.getSolutionState()
                        .getCurrentNode(), "", false, true, false,
                        BR_Controller.FIND_HINT);

            // b* try link with hint in currentAllowedGroups
            if (continueFlag) {
                for (int i = 0; i < controller.getSolutionState()
                        .getCurrentAllowedGroups().size()
                        && continueFlag; i++) {
                    singleGroup = (Vector) controller.getSolutionState()
                            .getCurrentAllowedGroups().elementAt(i);
                    continueFlag = processValidSinglePath(singleGroup, "",
                            BR_Controller.FIND_HINT);
                }
            }

            // c* try child link with hint
            if (continueFlag) {
                continueFlag = processOutStates(controller.getSolutionState()
                        .getCurrentNode(), "", false, false, false,
                        BR_Controller.FIND_HINT);
            }

            // no hint available
            if (continueFlag
//                    && controller.getMode()
//                            .equalsIgnoreCase(CtatModeModel.EXAMPLE_TRACING_MODE))
                    && controller.getCtatModeModel().isExampleTracingMode()) {
            	MessageObject mo = PseudoTutorMessageBuilder.buildNoHintMessage(controller);
            	messageTank.addToMessageTank(mo);
            	messageTank.flushMessageTank(controller.getProblemSummary());
            }
            return;
        }

        // in case: currentGroup is null and current allowed groups are
        // empty
        // 1st check focused widget currect or firable-bug path out from
        // currNode
        if (focusWidgetSelected) {
            continueFlag = processOutStates(controller.getSolutionState()
                    .getCurrentNode(), focusWidgetSelectionName, false,
                    false, false, BR_Controller.FIND_HINT);
        }

        // 2nd check no focused widget currect or firable-bug path out from
        // currNode
        if (continueFlag)
            continueFlag = processOutStates(controller.getSolutionState()
                    .getCurrentNode(), "", false, false, false,
                    BR_Controller.FIND_HINT);

        // no correct or firable-bug path from currNode with help/hint
        if (continueFlag
//                && controller.getMode().equalsIgnoreCase(CtatModeModel.EXAMPLE_TRACING_MODE))
                && controller.getCtatModeModel().isExampleTracingMode()) {
        	MessageObject mo = PseudoTutorMessageBuilder.buildNoHintMessage(controller);
        	messageTank.addToMessageTank(mo);
        	messageTank.flushMessageTank(controller.getProblemSummary());
        }
        return;
    }
*/

    /**
     * Create an AssociatedRules CORRECT message for the given link and result.
     * @param targetEdge matched edge in graph
     * @param selection student selection (log student SAI since step was correct)
     * @param action student action
     * @param inpuut student input
     * @param actor
     * @param replacementInput if not null, use this input to replace the student input
     * @param result if not null, use {@link ExampleTracerEvent#getResult()} for CORRECT, etc.
     */
    private void createAssocRulesCorrectMsg(ProblemEdge targetEdge,
    		Vector selection, Vector action, Vector input, String actor,
    		Vector replacementInput, ExampleTracerEvent result) {
    	
        MessageObject newMessage =
        	PseudoTutorMessageBuilder.buildAssociatedRules(targetEdge, 
        		selection, action, (replacementInput == null ? input : replacementInput), 
        		result.getStudentSAI(), PseudoTutorMessageBuilder.CORRECT, actor,
        		controller, (String) selection.get(0));
        messageTank.addToMessageTank(newMessage, result);
    }

    public  ExampleTracerGraph getExampleTracerGraph(){
    	return exampleTracerGraph;
    }
	/**
	 * Reinitialize example tracer objects. Call this when loading a new graph, e.g.
	 * @param graph
	 */
	public void resetExampleTracer(ExampleTracerGraph graph, ProblemModel problemModel) {
		
		//exampleTracerGraph = new ExampleTracerGraph();
		//exampleTracerGraph.resetExampleTracer(problemModel.getVariableTable());
		//if(controller.getCtatModeModel()!=null)
		//	getExampleTracer().setDemonstrateMode(controller.getCtatModeModel().isDemonstratingSolution());
		//trace.out("et", "new example tracer "+getExampleTracer());
	}

	/**
	 * Advance the example tracer through the steps in a path. Also sets the {@link ProblemModel}'s
	 * variable table to the result of the last link traced. Does not generate output messages.
	 * @param newUserVisitedStates path to follow
	 * @return list of tracer results
	 */
	public List<ExampleTracerEvent> advanceTracerThroughPath(ExampleTracerPath newUserVisitedStates) {
		if (trace.getDebugCode("br")) trace.out("br", "traversePath("+newUserVisitedStates+")");
		List<ExampleTracerEvent> results = new ArrayList<ExampleTracerEvent>();
		traversedEdges = getExampleTracer().evaluateEdges(newUserVisitedStates, results); 
		if (results.size() > 0) {
			ExampleTracerEvent lastResult = results.get(results.size()-1);
			if (lastResult.getReportableVariableTable() != null)
				controller.getProblemModel().setVariableTable(lastResult.getReportableVariableTable());
		}
    	return results;
	}

	/**
	 * @return the {@link #traversedEdges}
	 */
	public List getTraversedEdges() {
		return traversedEdges;
	}

	public void setRequestMessage(MessageObject mo) {
		messageTank.setRequestMessage(mo);
	}

	public void setTransactionId(String semanticEventId) {
		if (semanticEventId != null) {
			messageTank.setTransaction_id(semanticEventId);
		}
	}

	public void problemModelEventOccurred(ProblemModelEvent e) {
		// ADDED 7/01/2013 - DEFINE THE GRAPH TO UPDATE - DOES THIS WORK?
		this.exampleTracerGraph = this.controller.getProblemModel().getExampleTracerGraph();
		
		ProblemNode oldState = controller.getCurrentNode();
		boolean resetToOldState = exampleTracerGraph.handleProblemModelEvent(e);
		//System.out.println("PME in PTMH: " + e.getClass().toString());
		if(e instanceof NewProblemEvent){
			//denverexampleTracerGraph.getExampleTracer().initialize(controller.getProblemModel().getVariableTable());
			exampleTracerGraph.getExampleTracer().setDemonstrateMode(false);
		}
		int tryToGoToState = e.getTryToSetCurrentStateTo();
		if(tryToGoToState!=-1){
			getExampleTracer().resetTracer();
			controller.goToStartState(false,false);
			//gotostate tests reachability of oldstate
			controller.goToState(exampleTracerGraph.getNode(tryToGoToState).getProblemNode());
		}else if(resetToOldState){
			if(controller.getCtatModeModel().isRuleEngineTracing())
				return;
			//calling resetTracer might be redundant, but is needed in some situation
			getExampleTracer().resetTracer();
			controller.goToStartState(false,false);
			//gotostate tests reachability of oldstate
			controller.goToState(oldState);
		}
	}

	/**
	 * @return {@link #messageTank}
	 */
	public MessageTank getMessageTank() {
		return messageTank;
	}

	/**
	 * Maybe deliver an unrequested hint. This feature was added for Ilya Goldin's Fall 2012 study.
	 * @param currentNode
	 * @param depthSoFar
	 */
	public void doUnrequestedHints(ProblemNode currentNode, int depthSoFar) {
		ProblemModel pm = controller.getProblemModel();
		if (pm == null)
			return;

		List<MessageObject> unrequestedHintMsgs = (currentNode == null ?
        		pm.getUnrequestedHint(depthSoFar) : pm.getUnrequestedHint(currentNode, depthSoFar));

		if (unrequestedHintMsgs != null) {
			messageTank.addToMessageTank(unrequestedHintMsgs);
			messageTank.flushMessageTank(null);
			pm.cancelUnrequestedHint(depthSoFar);
		}
	}
}
