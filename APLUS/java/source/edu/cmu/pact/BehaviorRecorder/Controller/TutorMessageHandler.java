/*
 * Created on Jan 10, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.DialogueSystemInfo;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerSAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.SolutionStateModel.ProcessTraversedLinks;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.HTTPMessageObject;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.miss.Rule;

/**
 * This class holds the code that processes messages which come from the
 * production rule system into the BR_Controller.
 * 
 * @author mpschnei
 * 
 * Created on: Jan 10, 2006
 */
public class TutorMessageHandler {

	/**
	 * Enable the rest of the code to suppress {@value MsgType#LISP_CHECK_ACTION} messages.
	 * @return prior value of {@link #disableLispCheckActionMsgs}
	 */
	private boolean disableLispCheckActionMsgs() {
		if(Utils.isRuntime())
			return true;
		if(controller == null)
			return false;
		String commShellVersion = controller.getCommShellVersion();
		if(commShellVersion == null)
			return false;
		return ("3.0".compareTo(commShellVersion) >= 0);
	}

    private BR_Controller controller;

    /** hold the response Comm messages */
    private MessageTank messageTank;

    public TutorMessageHandler(BR_Controller controller) {
        this.controller = controller;
        messageTank = new MessageTank(controller);
    }

    /**
     * Handle an interface action message recieved from the production system
     */
    void processTutorInterfaceAction(Vector selection, Vector action,
            Vector input, String actor, MessageObject currInterfaceActionTutor) {
        Vector ruleNames = new Vector();
        int uniqueID = -1;

        if (controller != null) {
        	if (controller.getProblemModel() != null)
            	controller.getProblemModel().startSkillTransaction();
        	controller.openTransaction(currInterfaceActionTutor);
        }
        if (messageTank != null)
    		messageTank.resetMessageTank();
    	else
    		messageTank = new MessageTank(controller);
        messageTank.setRequestMessage(currInterfaceActionTutor);

    	// 9/24/09
       /* if (firstSelection.equalsIgnoreCase("Help")
                || firstSelection.equalsIgnoreCase("Hint")) {
            handleInterfaceActionHelpMessage(ruleNames);
        } else {*/
        uniqueID = handleInterfaceActionNonHelpMessage(selection, action,
        		input, actor, currInterfaceActionTutor, ruleNames);
        //}
        messageTank.editSelectionAndAction("InterfaceAction", selection, action);

        if (ruleNames.size() < 1) // CTAT1202: Comm parse error if size 0
            ruleNames.addElement("dummy");
        if (trace.getDebugCode("mt")) trace.out("mt", "processTutorInterfaceAction() uniqueID=" + uniqueID
                + ", ruleNames(0)=" + ruleNames.get(0));
        if (trace.getDebugCode("mt")) trace.out("mt", "controller.brPanel.isDialogueMsgSent()= null"); //edit by ko 8-2-08 in getting rid of brpanel

        sendLISPCheckMsg(selection, action, input, ruleNames, new Integer(uniqueID),
        		currInterfaceActionTutor);
        messageTank.flushMessageTank(controller.getProblemSummary(), false);  // false: not end of transaction
    }

    /**
     * Forward the student SAI to the rule engine in a {@value MsgType#LISP_CHECK}
     * message for evaluation.
     * @param selectionP
     * @param actionP
     * @param inputP
     * @param ruleNamesP
     * @param actionLabelTagIDP
     * @param msg
     */
    void sendLISPCheckMsg(Vector selectionP, Vector actionP,
            Vector inputP, Vector ruleNamesP, Integer actionLabelTagIDP,
            MessageObject msg) {

    	
        MessageObject newMessage = MessageObject.create(MsgType.LISP_CHECK, "SendNoteProperty");
        newMessage.setSelection(selectionP);
        newMessage.setAction(actionP);
        newMessage.setInput(inputP);

        newMessage.setProperty("RuleNames", ruleNamesP);
        newMessage.setProperty("ActionLabelTagID", actionLabelTagIDP);

        String transactionId = (msg == null ? null : msg.getTransactionId());
        if (transactionId != null)
        	newMessage.setTransactionId(transactionId);

        if(msg instanceof HTTPMessageObject)
        	((HTTPMessageObject) msg).getHttpToolProxy().sendProperty(newMessage);
        else
        	controller.getUniversalToolProxy().sendProperty(newMessage);
    }

    /**
     * @param selection
     * @param action
     * @param input
     * @param actor
     * @param currInterfaceActionTutor
     * @param ruleNames
     * @return
     */
    private int handleInterfaceActionNonHelpMessage(Vector selection,
            Vector action, Vector input, String actor,
            MessageObject currInterfaceActionTutor, Vector ruleNames) {
    	
    	int uniqueID = -1;
        sendLISPCheckActionMsg(selection, input);
        
        if (Utils.isRuntime())
        	return uniqueID;   // CTAT2504: disable example tracing at student time
        
        // upon Chang reqest: always sendLISPCheckMsg even in hint or help
        // case
        ProblemEdge tempEdge;
        EdgeData myEdge = null;


        if (PseudoTutorMessageHandler.USE_NEW_EXAMPLE_TRACER) {
//FIXME        	controller.getExampleTracer().tryEvaluate(selection, action, input, actor);
        	controller.getExampleTracer().evaluate(selection, action, input, actor);
        	ExampleTracerEvent result = controller.getExampleTracer().getResult();
        	if (ExampleTracerTracer.NULL_MODEL.equals(result.getResult()))
        		uniqueID = -1;
        	else {
        		ProblemEdge reportableLink = result.getReportableLink().getEdge().getEdge();
        		myEdge = reportableLink.getEdgeData();
				if (trace.getDebugCode("mt")) trace.out("mt", "reportableLink from new Ex-tracer "+myEdge);
        	}
        } else {
        	myEdge = doOldExampleTraceLookup(selection, action, input);
			if (trace.getDebugCode("mt")) trace.out("mt", "reportableLink from old Ex-tracer "+myEdge);
        }
        
        
        
        if (myEdge != null)
        	uniqueID = myEdge.getUniqueID();
        // trace.out ( "uniqeID :" + uniqeID);

        if (uniqueID != -1) {
        	// We found a matching edge
            RuleLabel tempLabel;
            for (int i = 0; i < myEdge.getRuleLabels().size(); i++) {
                tempLabel = (RuleLabel) myEdge.getRuleLabels().elementAt(i);
                ruleNames.addElement(tempLabel.getText());
            }
        } else {
        	// We did not find an edge
            ruleNames.addElement("dummy");
            uniqueID = controller.index_interfaceActions_NoneState_Tutor--;
            controller.getInterfaceActions_NoneState_Tutor().put(
                    new Integer(uniqueID), currInterfaceActionTutor);
        }
        return uniqueID;
    }

    /**
     * Send a {@value MsgType#LISP_CHECK_ACTION} message to the student interface,
     * to tell the student to wait for the tutor's response.
     * @param selectionP
     * @param inputP
     */
    private void sendLISPCheckActionMsg(Vector selectionP, Vector inputP) {
    	if(disableLispCheckActionMsgs())
    		return;
        MessageObject newMessage = MessageObject.create(MsgType.LISP_CHECK_ACTION, "SendNoteProperty");
        newMessage.setSelection(selectionP);
        newMessage.setInput(inputP);
        newMessage.setTransactionId(controller.getSemanticEventId());

        if (trace.getDebugCode("br")) trace.out("br", "sendLISPCheckActionMsg: " + newMessage.toString());
        messageTank.addToMessageTank(newMessage);
    }

    private EdgeData doOldExampleTraceLookup(Vector selection, Vector action, Vector input) {
        
    	int uniqueID = -1;
    	ProblemEdge tempEdge;
    	EdgeData myEdge = null;
    	// trace.out("caseInsensitive = " + caseInsensitive);
        Enumeration iter = controller
                .getProblemModel()
                .getProblemGraph()
                .getConnectingEdges(controller.getSolutionState().getCurrentNode());
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            if (tempEdge.getNodes()[ProblemEdge.DEST] == controller
                    .getSolutionState().getCurrentNode()) {
                myEdge = tempEdge.getEdgeData();
                if (controller.getProblemModel().matchStates(myEdge,
                        selection, action, input)) {
                    trace.out("it's not a new state");
                    uniqueID = myEdge.getUniqueID();
                    break;
                }
            }
        }

        if (uniqueID == -1) {
            Enumeration iterEdges = controller.getProblemModel()
                    .getProblemGraph().getOutgoingEdges(
                            controller.getSolutionState().getCurrentNode());
            while (iterEdges.hasMoreElements()) {
                tempEdge = (ProblemEdge) iterEdges.nextElement();
                myEdge = tempEdge.getEdgeData();
                if (controller.getProblemModel().matchStates(myEdge,
                        selection, action, input)) {
                    uniqueID = myEdge.getUniqueID();
                    break;
                }
            }
        }
        return myEdge;
	}

	/**
     * @param ruleNames
     */
  /*  private void handleInterfaceActionHelpMessage(Vector ruleNames) {
        ruleNames.addElement("dummy");
        if (controller.dialogSystemSupport.connectedToDialogSystem)
            controller.handleAskForHintMessage("");
    }*/

	public synchronized void handleLispCheckResultMessage(MessageObject o,
			ProblemModel problemModel2, LoggingSupport loggingSupport2,
			BR_Controller controller) {

		if (trace.getDebugCode("popup")) trace.out("popup", "entered handleLispCheckResultMessage");
	    
		if (trace.getDebugCode("lispcheckresult")) trace.out("lispcheckresult", "BR.handleCommMessage(LispCheckResult) "
				+ o);
		String transactionId = o.getTransactionId();
		try {
			//		messageTank.clear();   sewall 2013-08-19 should we clear the tank here? 

			String checkResult = (String) o.getProperty("Result");
			if (trace.getDebugCode("popup")) trace.out("popup", "checkResult = " + checkResult);

			Integer actionLabelTagID = o.getPropertyAsInteger("actionLabelTagID");
			if (actionLabelTagID == null)
				actionLabelTagID = -2;

			ProblemEdge thisEdge = problemModel2
					.getEdge(actionLabelTagID.intValue());

			if (thisEdge != null) {
				String edgeInfoString = "Edge from ";

				ProblemNode tempNode = thisEdge.getNodes()[ProblemEdge.SOURCE];
				edgeInfoString += tempNode.toString() + " to ";

				tempNode = thisEdge.getNodes()[ProblemEdge.DEST];
				edgeInfoString += tempNode.toString();

				loggingSupport2.programActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
						BR_Controller.TEST_MODEL_1_STEP_RESULT, edgeInfoString,
						checkResult, "");
			}

			if (controller.getCtatModeModel().isSimStudentMode())
				processLispCheckResultSimStMode(o, controller);
			else if (controller.getCtatModeModel().isRuleEngineTracing())
				processLispCheckResultTutorMode(o, controller);
			else {
				try {
					throw new IllegalStateException("handling LispCheckResult msg"+
							" but mode indicates no rule engine");
				} catch (IllegalStateException ise) {
					ise.printStackTrace();
				}
			}
			messageTank.setTransaction_id(transactionId);
			ProblemSummary ps = controller.getProblemSummary();
			messageTank.flushMessageTank(ps);
			controller.getModelTracer().updateProblemSummaryFacts(ps);  // revise during think time
		} catch(Exception e) {
			trace.errStack("TutorMessageHandler.handleLispCheckResultMessage(): error "+e+
					"\n  processing\n  "+o+"\n", e);
			MessageObject errorResponse =
					MessageObject.create(MsgType.TUTORING_SERVICE_ERROR, "SendNoteProperty");
			errorResponse.setProperty("ErrorType", "Model Tracing Failure");
			errorResponse.setProperty("Details", "Error processing response from rules engine: "+e);
			controller.handleMessageUTP(errorResponse);
		}
		controller.closeTransaction(transactionId);
	}

	/**
	 * @param controller
	 * @param checkResult
	 * @param actionLabelTagID
	 * @param selection student selection
	 * @param input student input
	 * @param action student action 
	 */
	void processLispCheckResultActionLabelGTM1(BR_Controller controller,
			String checkResult, Integer actionLabelTagID, Vector selection,
			Vector input, Vector action) {
		{
			if (trace.getDebugCode("lispcheckresult")) trace.out("lispcheckresult",
					"process lisp check result action: checkResult = "
							+ checkResult + " selection = " + selection
							+ " input = " + input);

			// the author intent and the result of check with production system
			// do not match ???
			ProblemEdge matchedEdge = controller.getProblemModel()
					.getEdge(actionLabelTagID.intValue());

			EdgeData matchedEdgeData = matchedEdge.getEdgeData();
			matchedEdgeData.getPreLispCheckLabel().resetAll(actionLabelTagID.intValue(),
					matchedEdgeData.getCheckedStatus());
			matchedEdgeData.setCheckedStatus(checkResult);
			// trace.out("missx", "setCkeckedStatus: #1");

			if (checkResult.equalsIgnoreCase(EdgeData.SUCCESS)
					|| checkResult.equalsIgnoreCase(EdgeData.FIREABLE_BUG)) {

				handleSuccessOrFireableBugActionType(controller, checkResult,
						selection, input, action, matchedEdge);

			} else {
				MessageObject mo =
					PseudoTutorMessageBuilder.buildCommIncorrectMessage(selection,
							action, input, controller);
				messageTank.addToMessageTank(mo);
			}
			if (DialogueSystemInfo.getUseDialogSystem(controller))
				controller.processEdgeSuccessBuggyMessage(matchedEdge);

			String actionType = "";

			actionType = getActionType(checkResult);

			// if the author intent and the result of check with production
			// system
			// do not match
			if (!controller.getIsReducedMode()
					&& !actionType.equalsIgnoreCase(matchedEdgeData.getActionType())) {
                
				handleUnmatchedActionTypeAndActionResult(controller,
						checkResult, matchedEdgeData);
			}
		}
	}

	private static void handleUnmatchedActionTypeAndActionResult(
			BR_Controller controller, String checkResult, EdgeData myEdge) {
		// dont want this message dialog to be displayed to the student

		// Added for debug. To be removed.
		if (trace.getDebugCode("rr"))
			trace.out("rr", "handleUnmatchedActionTypeAndActionResult() Utils.isRuntime() "+Utils.isRuntime());

		if (Utils.isRuntime())
			return;
		
		if (checkResult.equalsIgnoreCase(EdgeData.NOTAPPLICABLE)) {
			showCantYetBeTestedMessage(controller);
		} else {
			showDoesNotWorkAsSpecifiedMessage(controller, checkResult, myEdge);
		}
	}

	private static void showDoesNotWorkAsSpecifiedMessage(
			BR_Controller controller, String checkResult, EdgeData myEdge) {
		if (trace.getDebugCode("lispcheckresult")) trace.out("lispcheckresult",
				"  ***  this is where it notifies the user");
		String message[] = { "On this step the production rule model",
				"does not work as specified in the Behavior Graph.",
				"The Behavior Graph indicates " + myEdge.getActionType() + ",",
				"the model-tracing algorithm returned " + checkResult };

		// upon Vincent request change the message
		// zz 06-07-04: fix the bug CTAT#436
		/*
		 * "The arc is inconsistent. It is defined to be: " +
		 * myEdge.actionLabel.getAuthorIntent(), "\nBut the rules that you have
		 * written cause it to be: " + checkResult };
		 */
		JOptionPane.showMessageDialog(controller.getActiveWindow(), message,
				"Warning", JOptionPane.WARNING_MESSAGE);
	}

	private static void showCantYetBeTestedMessage(BR_Controller controller) {
		// zz upon Vincent request 06-11-04
		String[] message = { "The production rule model cannot be",
				"tested yet on this step." };

		JOptionPane.showMessageDialog(controller.getActiveWindow(), message,
				"Prod. System Check Message", JOptionPane.WARNING_MESSAGE);
	}

	private static String getActionType(String checkResult) {
		String actionType;
		if (checkResult.equalsIgnoreCase(EdgeData.SUCCESS)) {
			actionType = EdgeData.CORRECT_ACTION;

		} else if (checkResult.equalsIgnoreCase(EdgeData.BUGGY)) {
			actionType = EdgeData.BUGGY_ACTION;

		} else if (checkResult.equalsIgnoreCase(EdgeData.FIREABLE_BUG)) {
			actionType = EdgeData.FIREABLE_BUGGY_ACTION;

		} else {
			actionType = EdgeData.UNTRACEABLE_ERROR;
		}
		return actionType;
	}

	public void handleSuccessOrFireableBugActionType(
			BR_Controller controller, String checkResult, Vector selection,
			Vector input, Vector action, ProblemEdge edge) {
		controller.setCurrentNode(edge.getNodes()[ProblemEdge.DEST]);

  		if (controller.getProcessTraversedLinks() != null) {
  			if (trace.getDebugCode("mt")) trace.out("mt", "add traversed link to DOM");
  			controller.getProcessTraversedLinks().addLinkNode(edge.getUniqueID(),
  					 selection, action, input,
  					 EdgeData.checkResultToActionType(checkResult));
  		} else {
			trace.err("LispResultCheck("+selection+","+action+","+input+
					"): ProcessTraversedLinks object is null");
  		}

  		MessageObject mo;
		if (checkResult.equalsIgnoreCase(EdgeData.SUCCESS)) {
			mo = PseudoTutorMessageBuilder.buildCommCorrectMessage(selection, action, input,
					controller);
		} else {
			mo = PseudoTutorMessageBuilder.buildCommIncorrectMessage(selection, action, input,
					controller);
		}
		messageTank.addToMessageTank(mo);
	}

	/**
	 * @param controller
	 * @param checkResult
	 * @param actionLabelTagID
	 * @param selection
	 * @param input
	 * @return
	 */
	boolean processLispCheckResultActionLabelLTEQM1(
			BR_Controller controller, String checkResult, 
			Integer actionLabelTagID, Vector selection, Vector action, Vector input) {

		String firstSelection;

		// the author intent and the result of check with production system
		// match
		String actionType = EdgeData.checkResultToActionType(checkResult);
		if (checkResult.equalsIgnoreCase(EdgeData.SUCCESS)) {
			// check if the problem is finished. if so then advance to the
			// next
			// problem
			firstSelection = (String) selection.get(0);
			trace.out("First Selection: " + firstSelection);
			if (firstSelection.equalsIgnoreCase("Done")) {
				controller.getCTAT_LMS().advanceProblem();

				// clear the messages - hints and buggy
				if (controller.getStudentInterface() != null)
					controller.getStudentInterface().getHintInterface().reset();
			}
		}
		// trace.out("authorIntent: " + authorIntent);
		// add new state
		if (actionType.length() > 0) {
			MessageObject newStateInterfaceObject = null;

			if (actionLabelTagID.intValue() < -2) {
				newStateInterfaceObject = (MessageObject) controller
						.getInterfaceActions_NoneState_Tutor().get(
								actionLabelTagID);
				if (newStateInterfaceObject == null) {
					trace.err("no MessageObject is in HashTable interfaceActions_NoneState_Tutor"+
							" for actionLabelTagID = "+actionLabelTagID.toString());
					return false;
				}

				controller.getInterfaceActions_NoneState_Tutor().remove(
						actionLabelTagID);

				action = newStateInterfaceObject.getAction();
				selection = newStateInterfaceObject.getSelection();
				input = newStateInterfaceObject.getInput();

			} else if (actionLabelTagID.intValue() == -2) {
				// This (-2) is the "updateEachCycle" result from TDK, where
				// the rule engine generates a next step in the student
				// interface.
				ProcessTraversedLinks ptl = controller.getProcessTraversedLinks();
				if (action == null)         // msg from TDK lacks action: create Vector so
					action = new Vector();  // that ptl.createInterfaceAction() can fill it
				if (ptl != null)
					newStateInterfaceObject = ptl.createInterfaceAction(selection, action, input
						/* , Matcher.DEFAULT_TOOL_ACTOR FIXME: tutor-performed step!!! */);
				else
					trace.err("LispResultCheck("+selection+","+action+","+input+
							"): ProcessTraversedLinks object is null");
			}

			// use the validation in this method.
				controller.demonstrateModeMessageHandler
						.processDemonstrateInterfaceAction(selection, action,
								input, newStateInterfaceObject, actionType);
			// FIXME sewall 04/24/07: tryTrace() redundant with processDemonstrateInterfaceAction()
			// commented out 06/05/07
			// controller.demonstrateModeMessageHandler.tryTrace(selection, action, input);
			Enumeration iterEdges = controller.getProblemModel()
					.getProblemGraph().edges();
			if (iterEdges.hasMoreElements()) {   // ?get edge added by processDemonstrateInterfaceAction()
				ProblemEdge edge = (ProblemEdge) iterEdges.nextElement();
				EdgeData myEdge = edge.getEdgeData();
				myEdge.getPreLispCheckLabel().resetAll(
						myEdge.getUniqueID(),
						myEdge.getCheckedStatus());
				myEdge.setCheckedStatus(checkResult);
				// trace.out("missx", "setCkeckedStatus: #2");

			}
		} else {
			MessageObject mo = PseudoTutorMessageBuilder.buildCommIncorrectMessage(selection,
					action, input, controller);
			if (trace.getDebugCode("mt")) trace.out("mt", "result from LispCheckResult actionLabelTagID "+
					actionLabelTagID+": \n  "+mo); 
			messageTank.addToMessageTank(mo);
		}
		return true;
	}

	// /////////////////////////////////////////////////////////////////////
	/**
	 * called when mode is Tutor and messageType is LispCheckResult
	 * 
	 * @param controller
	 *            TODO
	 */
	// /////////////////////////////////////////////////////////////////////
	void processLispCheckResultTutorMode(MessageObject mo, BR_Controller controller) {

		String checkResultMessageResultValue = (String) mo.getProperty("Result");
		String actionLabelTagID = (String) mo.getProperty("actionLabelTagID");
		Vector selection = (Vector) mo.getSelection();
		Vector action = (Vector) mo.getAction();
		Vector input = (Vector) mo.getInput();
		Vector studentSelection = (Vector) mo.getProperty(PseudoTutorMessageBuilder.STUDENT_SELECTION);
		Vector studentAction = (Vector) mo.getProperty(PseudoTutorMessageBuilder.STUDENT_ACTION);
		Vector studentInput = (Vector) mo.getProperty(PseudoTutorMessageBuilder.STUDENT_INPUT);
		Vector<String> hintMsgs = (Vector<String>) mo.getProperty("HintMessages");
		String buggyMsg = (String) mo.getProperty("BuggyMsg");
		String successMsg = (String) mo.getProperty("SuccessMsg");
		Vector<String> ruleNames = (Vector<String>) mo.getProperty("ProductionList");
		List<String> skillNames = (Vector<String>) mo.getProperty("Skills");
		Vector wmImages = (Vector) mo.getProperty(PseudoTutorMessageBuilder.WMIAGESS_PNAME);
		if (trace.getDebugCode("lispcheckresult")) trace.out("lispcheckresult", "processLispCheckResultTutorMode "
				+ checkResultMessageResultValue + " " + actionLabelTagID + " "
				+ selection + " " + action + " " + input + " BuggyMsg="
				+ buggyMsg + " SuccessMsg=" + successMsg
				+ " WMImages length " + (wmImages == null ? 0 :wmImages.size()));

		MessageObject resultMsg = null;
		Vector<String> tutorAdvice = null;
		ExampleTracerSAI studentSAI = null;
		if (hintMsgs != null && hintMsgs.size() > 0) {
			resultMsg = PseudoTutorMessageBuilder.buildHintsMsg(hintMsgs, selection, action, input,
					actionLabelTagID, ruleNames, null, controller);
			tutorAdvice = hintMsgs;
		} else {
			studentSAI = new ExampleTracerSAI(studentSelection, studentAction, studentInput, null);
			if (Utils.isRuntime()) {
				if (checkResultMessageResultValue.equalsIgnoreCase(EdgeData.SUCCESS))
					resultMsg = PseudoTutorMessageBuilder.buildCommCorrectMessage(studentSelection, studentAction, studentInput, controller);
				else
					resultMsg = PseudoTutorMessageBuilder.buildCommIncorrectMessage(studentSelection, studentAction, studentInput, controller);
				messageTank.addToMessageTank(resultMsg);
			} else if (Integer.valueOf(actionLabelTagID) > -1) {
				processLispCheckResultActionLabelGTM1(controller,
						checkResultMessageResultValue, Integer.valueOf(actionLabelTagID), studentSelection,
						studentInput, studentAction);
			} else {
				boolean keepGoing =

					processLispCheckResultActionLabelLTEQM1(controller, checkResultMessageResultValue,
							Integer.valueOf(actionLabelTagID), studentSelection, studentAction, studentInput);
				if (!keepGoing)
					return;
				// throw new RuntimeException("ActionLabelID should == 1");
			}
		}
		// CTAT1304: don't overwrite buggy msg
		if (buggyMsg != null && buggyMsg.length() > 0) {
			resultMsg = PseudoTutorMessageBuilder.buildCommBuggyMessage(buggyMsg,
					selection, action, controller);
			(tutorAdvice = new Vector<String>()).add(buggyMsg);
		} else if (successMsg != null && successMsg.length() > 0) {
			resultMsg = PseudoTutorMessageBuilder.buildCommSuccessMessage(successMsg, controller);
			(tutorAdvice = new Vector<String>()).add(successMsg);
		}
		messageTank.addToMessageTank(resultMsg);
		
		// sewall 2013/08/22: step identifier as selection+action instead of unpredictable integers from graph
		String stepID = Skill.makeStepID(selection, action);

		List<Skill> modifiedSkills = null;
		ProblemModel pm = controller.getProblemModel();
		Vector<String> skillBarVector = null;
		String skillBarDelimiter = null;
		if(skillNames == null || skillNames.isEmpty())
			skillNames = ruleNamesToSkillNames(ruleNames);
		if (pm != null) {
			if (skillNames.size() > 0) {
				String result = PseudoTutorMessageBuilder.fixIndicator(checkResultMessageResultValue);
				modifiedSkills = pm.updateSkills(result, skillNames, stepID);
			}
			skillBarVector = pm.getSkillBarVector();
			skillBarDelimiter = pm.getSkillBarDelimiter(); 
		}		
		String toolSelection = (studentSelection == null || studentSelection.size() < 1
				? null : (String) studentSelection.get(0));
		
		// sewall 2012/12/04: hard-code actor: rule-based tutor-performed actions bypass this class 
		String actor = edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher.DEFAULT_ACTOR;

		resultMsg = PseudoTutorMessageBuilder.buildAssociatedRulesMsg(checkResultMessageResultValue,
				selection, action, input, studentSAI, ruleNames,
				skillBarVector,   // CTAT2974: don't send skills without pKnowns: generates NaN values with AS3
				skillBarDelimiter, stepID, actor, controller, toolSelection, tutorAdvice);
		if (wmImages != null && wmImages.size() > 0)
			resultMsg.setProperty(PseudoTutorMessageBuilder.WMIAGESS_PNAME, wmImages);
		
		List<Element> customFields = new ArrayList<Element>();
		Object customFieldsFromRules = mo.getProperty("custom_fields");
		if(customFieldsFromRules instanceof List)
			customFields.addAll((List<Element>) customFieldsFromRules);
		Element knowledgeTraceElt = skillsToKnowledgeTrace(modifiedSkills);
		if(knowledgeTraceElt != null)
			customFields.add(knowledgeTraceElt);
		if(!customFields.isEmpty())
			resultMsg.setProperty("custom_fields", customFields, true);  // true => use as is

		messageTank.addToMessageTank(resultMsg);

		controller.fireCtatModeEvent(CtatModeEvent.REPAINT);

		// brFrame.repaint();
	}

	/**
	 * Add a custom_field element with a &lt;name&gt; child "knowledge-trace" and a &lt;value&gt;
	 * child whose content is a string of the format
	 * 		skill-name1 skill-category1 p-known1[;skill-name2 skill-category2 p-known2[; ... ] ]
	 * @param skills skills to list
	 * @return Element of this format
	 */
	private Element skillsToKnowledgeTrace(List<Skill> skills) {
		if(skills == null || skills.isEmpty())
			return null;
		Element nameElt = new Element("name");
		nameElt.addContent("knowledge-trace");
		
		StringBuilder sb = new StringBuilder();
		int i = 0, nDelimiters = skills.size()-1; 
		for(Skill sk : skills) {
			String name = sk.getName();
			if(name.length() > 25) {
				StringBuilder nb = new StringBuilder();
				nb.append(name.substring(0, 11)).append("...").append(name.substring(name.length()-11));
				name = nb.toString();
			}
			String pKnown = String.format("%.2f", sk.getPKnown());
			sb.append(name).append(',').append(sk.getCategory()).append(',').append(pKnown);
			if(i++ < nDelimiters)
				sb.append(';');
		}
		Element valueElt = new Element("value");
		valueElt.addContent(sb.toString());

		Element elt = new Element("custom_field");
		elt.addContent(nameElt).addContent(valueElt);
		return elt;
	}

	/** For finding embedded white space. */
	private static final Pattern WhiteSpacePattern = Pattern.compile("\\s+");

	/**
	 * Convert a list of rule names into skill names:
	 * <ul>
	 * <li>If the ruleName has an embedded space, the category is the substring preceding the first space.</li>
	 * <li>If the ruleName is of the form "<i>MODULE</i>::<i>rule</i>", the category is the module.</li>
	 * </ul>
	 * Any embedded whitespace is converted to a single '_'.
	 * @param ruleName
	 * @return revised list with names of the form "<i>skillName</i> <i>category</i>"
	 */
	Vector<String> ruleNamesToSkillNames(List<String> ruleNames) {
		Vector<String> skillNames = new Vector<String>();
		if (ruleNames == null || ruleNames.size() < 1)
			return skillNames;
		for (String ruleName : ruleNames) {
			String skillName = ruleName.trim();
			String category = "";
			Matcher m = WhiteSpacePattern.matcher(skillName);
			int delimiter = (m.matches() ? m.start() : -1);
			if (delimiter > 0) {
				category = skillName.substring(0, delimiter);
				skillName = skillName.substring(m.end());
			} else if ((delimiter = skillName.indexOf("::")) > 0) {
				category = skillName.substring(0, delimiter);  // category is Jess module name
				skillName = skillName.substring(delimiter+2); 
			}
			if (skillName.length() < 1 && category.length() > 0) {
				skillName = category;
				category = "";
			}
			StringBuffer sb = new StringBuffer(WhiteSpacePattern.matcher(skillName).replaceAll("_"));
			if (category.length() > 0)
				sb.append(' ').append(category);
			skillNames.add(sb.toString());
		}
		return skillNames;
	}

	static void processLispCheckResultSimStMode(MessageObject mo, BR_Controller controller) {
	    
	    String checkResult = (String) mo.getProperty("Result");
            Integer actionLabelTagID = mo.getPropertyAsInteger("actionLabelTagID");
            
            ProblemNode bottomNode = controller.getCurrentNode();                
            ProblemNode bottomNodeParent = (bottomNode.getParents()==null) ? 
                    bottomNode : (ProblemNode) bottomNode.getParents().get(0);

            ProblemEdge edge = controller.getMissController().getSimSt().lookupProblemEdge(bottomNodeParent,bottomNode);
            
         // Added for debug. To be removed.
         if (trace.getDebugCode("rr")) trace.out("rr", "controller.getMissController.getSimSt.isInteractiveLearning:" + controller.getMissController().getSimSt().isInteractiveLearning());
	    
         // Added by Rohan Raizada. This is done because we dont want the prod. Rule 
         // message to pop up with the current version. The value before changing this was
         // if (edge == null || controller.getMissController.getSimSt.isInteractiveLearning())
         if (edge == null || controller.getCtatModeModel().isSimStudentMode()){
	    	if (trace.getDebugCode("ss")) trace.out("ss", "Returing from processLispCheckResultSimStMode() as InteractiveLearning is True");
	    	return; //probably a terrible idea
	    }

	    EdgeData myEdge = edge.getEdgeData();
	    String oldCheckedText = myEdge.getCheckedStatus();

	    myEdge.getPreLispCheckLabel().resetAll(actionLabelTagID.intValue(), myEdge.getCheckedStatus());
	    myEdge.setCheckedStatus(checkResult);
	    
	    if (checkResult.equalsIgnoreCase(EdgeData.SUCCESS)) {
                
	        myEdge.setCheckedStatus(EdgeData.SUCCESS);
	        ProblemNode childTemp = edge.getNodes()[ProblemEdge.DEST];
	        controller.setCurrentNode(childTemp);
	        NodeView currVertex = controller.getSolutionState().getCurrentNode().getNodeView();
	        controller.sendCommMsgs(childTemp, controller.getProblemModel()
	                .getStartNode());
	    }

	    controller.fireCtatModeEvent(CtatModeEvent.REPAINT);

            if (checkResult.equalsIgnoreCase(EdgeData.SUCCESS)) {
                
                Vector skillNameVector = (Vector) mo.getProperty("ProductionList");
                String skillName = (Rule.getRuleBaseName((String) skillNameVector.get(0))).replaceAll("MAIN::", "");
                
                String newProductionSet = "SimSt";
                //insert code here: edge.rename
                ((RuleLabel) edge.getEdgeData().getRuleLabels().get(0)).setText(skillName + " " + newProductionSet);                
                
	        String message = "The production rule model seems to \ntrace the selected step correctly.";

	        // sanket@cs.wpi.edu
	        // JOptionPane.showMessageDialog( this,
	        // message,
	        // "LISP Check Message",
	        // JOptionPane.INFORMATION_MESSAGE);
	        JOptionPane.showMessageDialog(controller.getActiveWindow(),
	                message, "Prod. System Check Message",
	                JOptionPane.INFORMATION_MESSAGE);
                // sanket@cs.wpi.edu
	    } else {
	    	
	    	// Added for debug. To be removed.
	    	if (trace.getDebugCode("rr")) trace.out("rr", "processLispCheckResultSimStMode()" + checkResult.equalsIgnoreCase(EdgeData.SUCCESS));
	        showCantYetBeTestedMessage(controller);
	    }

	    myEdge.setOldActionType(myEdge.getActionType());

	}

	public void handleChangeWMStateMessage(MessageObject mo, BR_Controller controller) {
		
	    controller.setCurrentNode(controller.getProblemModel().getStartNode());
	
	    ProblemEdge edge = null;
	    EdgeData myEdge;
	    Vector singleCheckedlink;
	    Integer uniqueID;
	    String checkedResult;
	    String authorIntent;
	    Object tempObject = mo.getProperty("checkLinksList");
	
	    if (tempObject instanceof Vector) {
	        Vector arcsTraversedList = (Vector) tempObject;
	
	        int arcsTraversedNumber = arcsTraversedList.size();
	        if (arcsTraversedNumber > 0) {
	            for (int i = 0; i < arcsTraversedNumber; i++) {
	                singleCheckedlink = (Vector) arcsTraversedList.elementAt(i);
	                uniqueID = (Integer) singleCheckedlink.elementAt(0);
	                checkedResult = (String) singleCheckedlink.elementAt(1);
	
	                edge = controller.getProblemModel().getEdge(
	                        uniqueID.intValue());
	                myEdge = edge.getEdgeData();
	                authorIntent = myEdge.getActionType();
	                myEdge.setCheckedStatus(checkedResult);
	                // trace.out("missx", "setCkeckedStatus: #5");
	
	                if ((checkedResult.equalsIgnoreCase(EdgeData.SUCCESS) &&
	                			authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
	                        || (checkedResult.equalsIgnoreCase(EdgeData.FIREABLE_BUG) &&
	                        		authorIntent.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))) {
	
	                	// sewall 4/16/07: now use LispResultCheck method
	                	if (PseudoTutorMessageHandler.USE_NEW_EXAMPLE_TRACER && controller.getExampleTracer() != null) {
	                		boolean exTraceResult = controller.getExampleTracer().evaluate(myEdge);
	                		if (!exTraceResult && !controller.getCtatModeModel().isSimStudentMode()) {
	                		    // Noboru 9/04/2007
	                                // SimSt hit this step a lot. This step must be "normal" for SimSt, especilly
	                                // when SimSt is running a validation test for production rules 
	                                // with an existing BRD
	                		    trace.err("ChangeWMStateMessage: example trace fails while rule engine succeeds");
	                		    // break; FIXME stop here?
	                		}
	                	}
	                	handleSuccessOrFireableBugActionType(controller, checkedResult,
	                			myEdge.getSelection(), myEdge.getInput(), myEdge.getAction(), edge);
	                    controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
	//                        setCurrentNode(edge.getNodes()[ProblemEdge.DEST]);
	//
	//                        if (checkedResult.equalsIgnoreCase(EdgeData.SUCCESS)) {
	//                            sendCorrectActionMsg(myEdge.getSelection(), myEdge
	//                                    .getInput(), myEdge.getAction());
	//                        } else
	//                            sendIncorrectActionMsg(myEdge.getSelection(),
	//                                    myEdge.getInput());
	
	                } else {
	            		MessageObject response = 
	            			PseudoTutorMessageBuilder.buildCommIncorrectMessage(myEdge.getSelection(),
	            					myEdge.getInput(), myEdge.getAction(), controller);
	            		messageTank.addToMessageTank(response);
	                }
	            }
	        }
	    }
		messageTank.flushMessageTank(controller.getProblemSummary());

		controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
	}

	/**
	 * @return {@link #messageTank}
	 */
	public MessageTank getMessageTank() {
		return messageTank;
	}
}
