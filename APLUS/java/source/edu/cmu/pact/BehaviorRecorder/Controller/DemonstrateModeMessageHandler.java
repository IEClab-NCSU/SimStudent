/*
 * Created on Dec 7, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Dialogs.MatchedStatesDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.OrderSwitchDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreationFailedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.miss.AskHintHumanOracle;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimStInteractiveLearning;

public class DemonstrateModeMessageHandler {

    private BR_Controller controller;

    public DemonstrateModeMessageHandler(BR_Controller controller) {
        this.controller = controller;
    }

    
    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public void processDemonstrateInterfaceAction(Vector selection,
            Vector action, Vector input, MessageObject messageObject,
            String actionType) {
        
    	if (trace.getDebugCode("demo")) trace.out("demo", "processDemonstrateInterfaceAction("+selection+","+action+","+input+","+actionType);
    	String firstSelection = (String) selection.elementAt(0);

        boolean isMissActive = checkForMiss();
        if (isMissActive
                && !controller.getMissController().isFoaGetterDefined()
                && !controller.getMissController().isFoaSearch())
            return;

        boolean isHint = checkForHint(firstSelection);
        if (isHint)
            return;
        
        ProblemNode currentNode = controller.getSolutionState().getCurrentNode();

        //Don't do anything if merging dialog is 
        if (checkForCurrentStateChange())
            return;

        if (matchesExistingEdge(selection, action, input)) {  // N.B.: matchesExistingEdge() has side effects 
            if (trace.getDebugCode("npe")) 
            	trace.out("npe", "processDemoIA: matchesExistingEdge("+selection+", "+action+", "+input+
            			") true,\n  controller.getMissController() "+controller.getMissController());
        	if (!controller.getMissController().isPLEon())
        		return;
        }
        
        // to prevent adding new state after the Done state is created
        if (currentNode.isDoneState()) {
            processDoneStateMessage();
            return;
        }
        
        // find matched child state
        // 2008/08/21: preserve this only if people really want to prevent the author from
        // adding overlapping outgoing edges irrespective of ordering constraints
        if (handleCheckedNode(selection, action, input, actionType))
            return;
        
        // 2008/08/21: this is how the rule engines add their new nodes
        if (controller.getCtatModeModel().isJessMode()
                || controller.getCtatModeModel().isTDKMode()) {
        	addNewNodeForRuleEngine(selection, action, input, messageObject, actionType);
        	return;
        }
            
        // 2008/08/21: 
        Vector updatedMatchedNodesCopy = controller.getProblemModel().findSameStates(
                controller.getSolutionState().getCurrentNode(), selection, action, input);

        int numberOfMatchedNodes = updatedMatchedNodesCopy.size();

        Vector updatedMatchedNodes = getUpdatedMatchedNodes(updatedMatchedNodesCopy, numberOfMatchedNodes);

        updatedMatchedNodesCopy = (Vector) updatedMatchedNodes.clone();

        numberOfMatchedNodes = updatedMatchedNodesCopy.size();

        SimStInteractiveLearning ssIL = null;
        try {
            ssIL = controller.getMissController().getSimSt().getSsInteractiveLearning();
        } catch (Exception e) {
            // If SimStudent is not initiated, you'll get a null pointer exception here.
        }
        if (numberOfMatchedNodes == 0 || (ssIL != null && (ssIL.isRunningFromBrd() || controller.getMissController().isPLEon()))) { 
        	doZeroMatchedNodes(selection, action, input, messageObject, actionType);            
        } else if (numberOfMatchedNodes == 1) {
        	doOneMatchedNode(selection, action, input, messageObject, actionType, updatedMatchedNodesCopy);
        } else {
            doMultipleMatchedNodes(selection, action, input, messageObject, actionType, updatedMatchedNodesCopy);
        }

//        trace.out("wmefacts", "after doZeroMatchedNodes 2, before returning:");
//        controller.getMissController().getSimSt().showJessFacts();
        controller.getJGraphWindow().getJGraph().scrollPointToVisible(controller.getCurrentNode().getNodeView().getBottomCenterPoint()); 

		ActionEvent ae = new ActionEvent(this, 0,
				"Demonstrate step ["+selection.get(0)+","+action.get(0)+","+input.get(0)+"]");        
        controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        return;
    }

    /**
     * See if the given s,a,i matches an existing edge.
     * @param selection
     * @param action
     * @param input
     * @return true if this step traces to an existing edge
     */
    private boolean matchesExistingEdge(Vector selection, Vector action, Vector input) {
        ExampleTracerEvent tryTraceResult = tryTrace(selection, action, input);
        String tryTraceAnswer = tryTraceResult.getResult();
        if(tryTraceAnswer.equals(ExampleTracerTracer.NULL_MODEL))
        	return false;
        ProblemEdge tryTraceEdge = tryTraceResult.getReportableLink().getEdge().getEdge();
        ProblemNode targetNode = tryTraceEdge.getDest();

//      ProblemNode targetNode = findMatchingState(selection, action, input);
        
        if (trace.getDebugCode("demo")) trace.out("demo", "is same state = "+targetNode+", tryTraceAnswer "+tryTraceAnswer);

        // FIXME sewall 4/24/07: why setCurrentNode2()? tryTrace() worked, so just setCurrentNode?
    	String actionType = tryTraceEdge.getEdgeData().getActionType();
        if (tryTraceEdge.isCorrectorFireableBuggy()) {
        	ProblemNode newCurrentNode = controller.getExampleTracer().getCurrentNode(true);
        	controller.setCurrentNode(newCurrentNode);  // fixed 06/05/07
        } else {
        	if (!EdgeData.BUGGY_ACTION.equalsIgnoreCase(actionType)
        			&& !EdgeData.UNTRACEABLE_ERROR.equalsIgnoreCase(actionType)) {
        		trace.err("edge "+tryTraceEdge+" not isTraversable()"+
        				" but actionType "+actionType+" not "+
        				EdgeData.BUGGY_ACTION+" nor "+EdgeData.UNTRACEABLE_ERROR);
        	} else
                controller.fireCtatModeEvent(CtatModeEvent.REPAINT); 
        	// sewall 6/11/07: don't show this on the buggy link
//        	String message[] = { "You are not allowed to add a new link after a buggy link." };
//
//        	JOptionPane.showMessageDialog(controller.getActiveWindow(),
//        			message, "Warning", JOptionPane.WARNING_MESSAGE);
        }
        EdgeData myEdge = tryTraceEdge.getEdgeData();
        if (controller.getTraversalCountEnabled()) {
            myEdge.incrementTraversalCount();
        }

        if (EdgeData.CORRECT_ACTION.equalsIgnoreCase(actionType))
            controller.sendCorrectActionMsg(selection, input, action);
    	else
    		controller.sendIncorrectActionMsg(selection, input, action);
        
        return true;
	}


	/**
     * Try to trace the given (s,a,i) against the {@link #controller}'s
     * {@link ExampleTracerTracer}.
     * @param selection
     * @param action
     * @param input
     * @return destination node of matched link, if trace was successful
     */
    public ExampleTracerEvent tryTrace(Vector selection, Vector action, Vector input) {
    	 if (trace.getDebugCode("EMS")) trace.out("EMS",""+controller.getExampleTracer());
        controller.getExampleTracer().evaluate(selection, action, input, Matcher.DEFAULT_STUDENT_ACTOR);
        ExampleTracerEvent result = controller.getExampleTracer().getResult();
        if (trace.getDebugCode("EMS")) trace.out("EMS",""+result);
        return result;
	}

	/**
     * @param matchedNodes
     * @param sizeOfMatch
     * @return
     */
    private Vector getUpdatedMatchedNodes(Vector matchedNodes, int sizeOfMatch) {
        ProblemNode nodeTemp;
        Vector updatedMatchedNodes = new Vector();

        for (int i = 0; i < sizeOfMatch; i++) {
            nodeTemp = (ProblemNode) matchedNodes.elementAt(i);
            if (!controller.getSolutionState().isCurrentNodeOrParent(nodeTemp))
                updatedMatchedNodes.addElement(nodeTemp);
        }
        return updatedMatchedNodes;
    }


    /**
     * @param selection
     * @param action
     * @param input
     * @param messageObject
     * @param actionType
     * @param findMatchedNodes
     */
    private void doMultipleMatchedNodes(Vector selection, Vector action, Vector input, MessageObject messageObject, String actionType, Vector findMatchedNodes) {
        controller.setAllowCurrentStateChange(false);
        new MatchedStatesDialog(controller, this, findMatchedNodes, selection, action,
                input, messageObject, actionType);
    }


    /**
     * @param selection - This is the selection for the link
     * @param action - This is the action for the link
     * @param input - This is the input for the link
     * @param messageObject - This is the messageObject for the link
     * @param actionType - This is the actionType for the link
     * @param findMatchedNodes - This is the matched node for the link
     */
    private void doOneMatchedNode(Vector selection, Vector action, Vector input, MessageObject messageObject, String actionType, Vector findMatchedNodes) {

        if (trace.getDebugCode("gusIL")) trace.out("gusIL","entered doOneMatchedNode");
        ProblemEdge tempEdge;
        Enumeration iter;
        ProblemNode checkedNode;
        checkedNode = (ProblemNode) findMatchedNodes.elementAt(0);
        PreferencesModel prefs = controller.getPreferencesModel();
        Boolean alwaysLinkStates = prefs.getBooleanValue(controller.ALWAYS_LINK_STATES);
        if (trace.getDebugCode("gusIL")) trace.out("gusIL", "**alwaysLinkStates** " + alwaysLinkStates +
        		", prefsModel "+prefs);
        
        if (alwaysLinkStates.booleanValue()){
        	treatAsSameState(selection, action, input, messageObject,
					actionType, checkedNode);
        } else {
        	ProblemNode exTracerCurrentNode = controller.getExampleTracer().getCurrentNode(true);
            iter = controller.getProblemModel().getProblemGraph().getConnectingEdges(exTracerCurrentNode);
            while (iter.hasMoreElements()) {
                tempEdge = (ProblemEdge) iter.nextElement();
                if (tempEdge.getNodes()[ProblemEdge.DEST] == exTracerCurrentNode
                        && (ProblemNode) findMatchedNodes.elementAt(0) == tempEdge.getNodes()[ProblemEdge.SOURCE]) {

                    ProblemNode newNode = controller.addNewState(exTracerCurrentNode, selection, action, input,
                            messageObject, actionType);
                    // find just added Edge
                    ProblemEdge newAddedEdge = controller.getProblemModel()
                            .returnsEdge(exTracerCurrentNode, newNode);

                    if (newAddedEdge.isCorrectorFireableBuggy() && tryTrace(newAddedEdge.getEdgeData()))
                        controller.setCurrentNode(newNode);

                    // Sun May 22 21:57:20 2005: Noboru
                    // Inform Sim. St. about the new problem node
                    if (controller.getCtatModeModel().isSimStudentMode()) {
                        // System.out.println("##### sizeOfMatch(1): " +
                        // newNode);
                        MissControllerExternal mc = controller.getMissController();
                        if (AskHintHumanOracle.isWaitingForSai) //InteractiveLearning suppresses this call 
                            AskHintHumanOracle.hereIsTheSai(new Sai(selection, action, input));
                        else if (controller.getMissController().getSimSt().isInteractiveLearning())
                        {
                            //do nothing   

                        }
                        else //normal SimStudent demonstration through the interface 
                            mc.stepDemonstrated(newNode, selection, action, input, newAddedEdge);
                        
//                        //if isInteractiveLearning, set waitingForDemonstration semaphor to false
//                        Vector[] sai = {selection, action, input};
//                        System.out.println("doOneMatchedNode: before, mc.getSimSt().getSai()[0] = " + mc.getSimSt().getSai()[0]);
//                        mc.getSimSt().setSai(sai);
//                        System.out.println("doOneMatchedNode: after, mc.getSimSt().getSai()[0] = " + mc.getSimSt().getSai()[0]);
//                        mc.getSimSt().getSai().notifyAll();
                    }
                }
            }
            // trace.out( "OrderSwitchDialog created");
            controller.setAllowCurrentStateChange(false);
            new OrderSwitchDialog(controller, this, checkedNode, selection, action,
                    input, messageObject, actionType);
        }
    }


	/**
	 * Create a new edge that links 2 existing nodes.
	 * @param selection
	 * @param action
	 * @param input
	 * @param messageObject
	 * @param actionType
	 * @param checkedNode
	 */
	void treatAsSameState(Vector selection, Vector action, Vector input,
			MessageObject messageObject, String actionType,
			ProblemNode checkedNode) {
		EdgeData newEdge = controller.createNewEdge(
				controller.getExampleTracer().getCurrentNode(true),
				checkedNode,
				selection, action, input,
				messageObject,
				actionType,
				controller.getExampleTracer().getBestInterpretation().getMatchedLinks());
		controller.getExampleTracer().extendPaths();
		if (newEdge.getEdge().isCorrectorFireableBuggy()) {
		    boolean traced = tryTrace(newEdge);                
		}
		controller.setCurrentNode(controller.getExampleTracer().getCurrentNode(true));
	}

    /**
     * Try to example-trace a given edge.
     * @param edgeData edge to trace
     * @return result of {@link ExampleTracerTracer#evaluate(EdgeData)}
     */
    boolean tryTrace(EdgeData edgeData) {
    	if (controller == null || controller.getExampleTracer() == null)
    		return false;
    	else
    		return controller.getExampleTracer().evaluate(edgeData);
	}

	/**
     * @param selection
     * @param action
     * @param input
     * @param messageObject
     * @param actionType
     */
    // Why do we have one method  rather then {dozero,doone,domany}"matchednodes"
    private void doZeroMatchedNodes(Vector selection, Vector action, Vector input, MessageObject messageObject, String actionType) {

        ProblemNode newNode = null;
       	ProblemEdge newAddedEdge = null;

       	// trace.out("miss", "doZeroMatchedNodes: isWaitingForDemonstration = " + SimStInteractiveLearning.isWaitingForDemonstration);
       	
        //this is what gets executed in ordinary demonstrations in interactive mode
        if (!VersionInformation.includesJess() || !SimStInteractiveLearning.isWaitingForDemonstration){
            EdgeData newEdge = controller.createNewEdge(
            		//controller.getCurrentNode(true),
            		controller.getSolutionState().getCurrentNode(),
            		null,
            		selection, action, input,
            		messageObject,
            		actionType,
            		controller.getExampleTracer().getBestInterpretation().getMatchedLinks());
           controller.getExampleTracer().extendPaths();
            if (newEdge.getEdge().isCorrectorFireableBuggy()) {
                boolean traced = tryTrace(newEdge);                
            }
            controller.setCurrentNode(newEdge.getEdge().getDest());
            newNode = newEdge.getEdge().getDest();
            newAddedEdge = newEdge.getEdge();
        }
        
       
        
        // Sun May 22 21:57:14 2005: Noboru
        // Inform Sim. St. about the new problem node and the step
        // demonstrated
        if (controller.getCtatModeModel().isSimStudentMode()) {
            
            if (SimStInteractiveLearning.isWaitingForDemonstration){
                Sai sai = new Sai(selection, action, input);
                if (trace.getDebugCode("miss")) trace.out("miss", "delivering the SAI: " + sai);
                AskHintHumanOracle.hereIsTheSai(sai);
            }
            else {
                if (AskHintHumanOracle.isWaitingForSai){ 
                    //InteractiveLearning suppresses this call 
                    AskHintHumanOracle.getSaiDrop().put(new Sai(selection, action, input));
                }
                else if (controller.getRunType().equalsIgnoreCase("springboot")) {
                	//do nothing
                }
                else if (controller.getMissController().getSimSt().isInteractiveLearning()) {
                    //do nothing
                    ;
                }
                else {
                    //normal SimStudent demonstration through the interface 
                    MissControllerExternal mc = controller.getMissController();
                    mc.stepDemonstrated(newNode, selection, action, input, newAddedEdge);
                }
                // Sat Jul 30 14:25:47 2005: Noboru
                // When Simulated Student is active, try model-trace a step
                // demonstrated
                if (trace.getDebugCode("miss")) trace.out("miss", "Trying to model trace a step...");
                
               
                //if SimStudent is in CogTutor mode, do not pass the tutor action to the interface
                //(i.e. do not update the colors of the cells, we did that already).              
                if (controller.getMissController().getSimSt().isSsCogTutorMode()){
                	 if (trace.getDebugCode("cogTutor")) trace.out("cogTutor", "SimStudent is in CogTutor mode so NOT updating interface with tutor response....");
                	return;
                }
                
                controller.tutorMessageHandler.processTutorInterfaceAction(selection, action, input,
                        Matcher.DEFAULT_ACTOR, messageObject);
                
                
            }
        }
    }


    /**
     * @param selection
     * @param action
     * @param input
     * @param messageObject
     * @param actionType
     */
    private void addNewNodeForRuleEngine(Vector selection, Vector action, Vector input,
    		MessageObject messageObject, String actionType) {

        // create new state node
        ProblemNode newNode = controller.addNewState(controller.getSolutionState()
                .getCurrentNode(), selection, action, input, messageObject,
                actionType);

        if (trace.getDebugCode("br")) trace.out( "br", "addNewNodeIfNeeded() newNode name "+newNode.getName()+", "+newNode);

        // find just added Edge
        ProblemEdge newAddedEdge = controller.getProblemModel().returnsEdge(
                controller.getSolutionState().getCurrentNode(), newNode);

        if (newAddedEdge.isCorrectorFireableBuggy()) {
        	tryTrace(selection, action, input);
        	controller.setCurrentNode(newNode);


			//          if (controller.getMode().equalsIgnoreCase(
			//                  CtatModeModel.PRODUCTION_SYSTEM_MODE)
			  if (controller.getCtatModeModel().isRuleEngineTracing()
			          && controller.getProcessTraversedLinks() != null) {
			      trace.out("add traversed link to DOM");
			      controller.getProcessTraversedLinks().addLinkNode(newAddedEdge.getUniqueID(),
			              selection, action, input, actionType);
			  }
        }
    }
    
    /**
     * @param selection
     * @param action
     * @param input
     * @param actionType
     * @param currentNodeView
     */
    private boolean handleCheckedNode(Vector selection, Vector action, Vector input, String actionType) {
        
        ProblemNode checkedNode = controller.getProblemModel().findSameChildState(
                controller.getSolutionState().getCurrentNode(), selection, action, input);

        if (checkedNode == null) {
            return false;
        }

        EdgeData myEdge;
        // switchFontWithCurrNode (checkedNode);
        // currNode = checkedNode;

        if (trace.getDebugCode("mps")) trace.out("mps", "find matched child state");

        ProblemEdge checkedEdge = controller.getProblemModel().returnsEdge(
                controller.getSolutionState().getCurrentNode(), checkedNode);

        if (checkedEdge == null)
            return true;

        myEdge = checkedEdge.getEdgeData();
        if (controller.getTraversalCountEnabled()) {
            myEdge.incrementTraversalCount();
        }

        if ((actionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION) || actionType
                .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
                && checkedEdge.isCorrectorFireableBuggy())
            controller.setCurrentNode(checkedNode);

        // default: send CorrectAction message to UniversalToolProxy
        if (actionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION)
                && myEdge.getActionType().equalsIgnoreCase(
                        EdgeData.CORRECT_ACTION)) {
            controller.sendCorrectActionMsg(selection, input, action);
        } else {
            controller.sendIncorrectActionMsg(selection, input, action);
        }

        return false;
    }

    /**
     * 
     */
    private boolean checkForCurrentStateChange() {
        // prevent adding new state before the user finish the the current
        // MatchedStatesDialog
        if (!controller.isAllowCurrentStateChange()) {
            String message[] = { "Please FIRST handle your current Dialog." };

            JOptionPane.showMessageDialog(controller.getActiveWindow(), message,
                    "Warning", JOptionPane.WARNING_MESSAGE);

            // roll back to previous value in the related widget
            controller.sendCommMsgs(controller.getSolutionState().getCurrentNode(),
                    controller.getProblemModel().getStartNode());

            return true;
        }
        return false;
    }


    /**
     * Gustavo 12Oct2007: apparently, this returns true when FoA is not specified
     */
    private boolean checkForMiss() {
        // Wed May 11 22:27:01 2005 - Noboru
        // When Simulated Student is active, send a information about
        // the step demonstrated.
    	if (!VersionInformation.isRunningSimSt())
    		return false;
    	if (Utils.isRuntime())
    		return false;
        if (controller.getCtatModeModel().isSimStudentMode()) {
            MissControllerExternal mc = controller.getMissController();
            // Do nothing but warning if the focus of attention has
            // not been specified
            if (!mc.isMissHibernating() && !mc.isFocusOfAttentionSpecified()) {
                if(controller.getProblemModel().getStartNode()!=null)
                    mc.askSpecifyFocusOfAttention();

                return true;
            }
        }
        return false;
    }


    /**
     * @param firstSelection
     */
    private boolean checkForHint(String firstSelection) {
        if ((firstSelection.equalsIgnoreCase("Help") || firstSelection
                .equalsIgnoreCase("Hint"))) {
            JOptionPane
                    .showMessageDialog(
                            controller.getActiveWindow(),
                            "The use of the Help button cannot be demonstrated to the Behavior Recorder.",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 
     */
    private void processDoneStateMessage() {
        // add warning msg
        String message[] = { "You are not allowed to add a child state to the Done state." };

        JOptionPane.showMessageDialog(controller.getActiveWindow(), message,
                "Warning", JOptionPane.WARNING_MESSAGE);
        EdgeCreationFailedEvent evt = new EdgeCreationFailedEvent(controller,
        		EdgeCreationFailedEvent.Reason.LINK_AFTER_DONE_STATE, message[0]); 
        controller.getProblemModel().fireProblemModelEvent(evt);

        // roll back to previous value in the related widget
        controller.sendCommMsgs(controller.getSolutionState().getCurrentNode(),
                controller.getProblemModel().getStartNode());
    }

}
