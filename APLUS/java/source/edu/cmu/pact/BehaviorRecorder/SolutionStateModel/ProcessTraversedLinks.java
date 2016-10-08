/*
 * ProcessTraversedLinks.java
 *
 * Created on April 29, 2004, 1:05 PM
 */

package edu.cmu.pact.BehaviorRecorder.SolutionStateModel;

/**
 *
 * @author  zzhang
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import pact.CommWidgets.JCommWidget;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MessagePlayer;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.CtatModeModel;

//////////////////////////////////////////////////////////////////////
/**
 *  to process traversed links
 *
 */
//////////////////////////////////////////////////////////////////////

public class ProcessTraversedLinks {

	TraversedLinksModel traversedLinksModel;

	private final String LINK_NAME_PREFIX = "link_";
	private final int NON_VALID_ID = -999;

    private BR_Controller controller;

	//////////////////////////////////////////////////////////////////////
	/**
	 *      constructor
	 * @param controller 
	 */
	//////////////////////////////////////////////////////////////////////

	public ProcessTraversedLinks(BR_Controller controller) {
		traversedLinksModel = new TraversedLinksModel(controller);
		this.controller = controller;
	}  

	//////////////////////////////////////////////////////////////////////
	/**
	    save the traversed links to file: traversedLinksFileName
	 */
	//////////////////////////////////////////////////////////////////////

	public void saveTraversedLinks_Tofile(String traversedLinksFileName) {
		traversedLinksModel.saveTraversedLinks_Tofile(traversedLinksFileName);
		return;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	    return the traversed links as XML string
	 */
	//////////////////////////////////////////////////////////////////////

	public String getTraversedLinks_asXML()
	{
		return traversedLinksModel.getTraversedLinks_asXML();
	}

	//////////////////////////////////////////////////////////////////////
	/**
	    no any traversed link yet, called when the student just at the start of the problem
	 */
	//////////////////////////////////////////////////////////////////////

	public void initTraversedLinks() {
		if (trace.getDebugCode("br")) trace.out("br", "initTraversedLinks()");
		traversedLinksModel.initDom();
	}

	//////////////////////////////////////////////////////////////////////
	/**
	    load traversed links from the file: traversedLinksFileName
	 */
	//////////////////////////////////////////////////////////////////////

	public void loadTraversedLinks_Fromfile(String traversedLinksFileName) {
		traversedLinksModel.loadTraversedLinks_Fromfile(traversedLinksFileName);

		Vector nameList = traversedLinksModel.getTraversedLinkNodesNames();

        if (nameList.size() == 0) {
            traversedLinksModel.initDom();
            return;
        }
        
        ProblemNode lastVisitedNode = controller.getSolutionState().getCurrentNode();
        
		String tempName;
		int tempID;

		ProblemEdge tempEdge;
		EdgeData myEdge;

		TraversedLinkObject traversedLinkObject;

		Vector selection;
		Vector action;
		Vector input;

		String authorIntent;

		MessageObject commMsgObj = null;
		Vector propertyNames = null;
		Vector propertyValues = null;
		
		int maxLabelID = 0;

		for (int i = 0; i < nameList.size(); i++) {
			tempName = (String) nameList.elementAt(i);
			tempID = getUniqueIDFromLinkName(tempName);

			if (tempID == NON_VALID_ID) {
				trace.err("link name "+tempName+" has no valid id number");
				continue;
			}
			
			if (maxLabelID < tempID)
				maxLabelID = tempID;

			tempEdge = controller.getProblemModel().getEdge(tempID);

			if (tempEdge != null) { // saved link is on the BR graph

				myEdge = tempEdge.getEdgeData();
				authorIntent = myEdge.getActionType();
				selection = myEdge.getSelection();
				action = myEdge.getAction();
				input = myEdge.getInput();

				if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
                    controller.sendCorrectActionMsg(selection, input, action);

				else if (authorIntent.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
                    controller.sendIncorrectActionMsg(selection, input, action);

				//if (controller.getMode().equalsIgnoreCase(CtatModeModel.PRODUCTION_SYSTEM_MODE))
                if (controller.getCtatModeModel().isJessMode() || controller.getCtatModeModel().isTDKMode())
					//ESE_Frame.instance().switchToCurrNode((Node) tempEdge.getNodes()[Edge.DEST]);
                	lastVisitedNode = tempEdge.getNodes()[ProblemEdge.DEST];
				
			} else if (tempEdge == null) {
				// saved link is not on the BR graph, so create and add a new one edge	
								
				traversedLinkObject = traversedLinksModel.getTraversedLinkObject(tempName);
				if (traversedLinkObject == null) {
					trace.err("Cannot find traversedLinkObject for name "+tempName);
					continue;
				}
				commMsgObj = traversedLinkObject.getCommMsgObj();
				
				selection = commMsgObj.getSelection();
				action = commMsgObj.getAction();
				input = commMsgObj.getInput();

				ProblemNode newNode = controller.addNewState(lastVisitedNode, selection, action, input,
						commMsgObj, traversedLinkObject.getAuthorIntent());

                lastVisitedNode = newNode;
			} // else if block

		} // for block

        if (lastVisitedNode != controller.getSolutionState().getCurrentNode())
            controller.setCurrentNode(lastVisitedNode);
            
//		if (BR_Label.getUniqueIDGenerator()	< maxLabelID)
//			BR_Label.resetUniqueIdGenerator(maxLabelID + 1);
				
		return;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	    load traversed links from the XML string
	 */
	//////////////////////////////////////////////////////////////////////

	public void loadTraversedLinks_FromXML(String currentState) {
		traversedLinksModel.loadTraversedLinks_FromXML(currentState);

		Vector nodeList = traversedLinksModel.getTraversedLinkNodes();

        if (nodeList.size() == 0) {
            traversedLinksModel.initDom();
            return;
        }
        
//        ProblemNode lastVisitedNode = controller.getSolutionState().getCurrentNode();
        
        ArrayList list = new ArrayList();
       
		for (int i = 0; i < nodeList.size(); i++) {
			TraversedLinkObject node = (TraversedLinkObject) nodeList.elementAt(i);
			if (node != null) {
				int nodeID = node.getUniqueID();

				if (nodeID == NON_VALID_ID) {
					trace.out(5, this, "link name has no valid id number");
					continue;
				}
			
				MessageObject message = node.getCommMsgObj();
				if (trace.getDebugCode("br")) trace.out("br", "loadTraversedLinks_FromXML: message = " + message);
				list.add(message);
			}
		}
		
        MessagePlayer player = new MessagePlayer(controller, list, true);
        player.setForwardToClientProxy(controller.getUniversalToolProxy());
        try {
        	controller.setHintMode(false);
        	controller.getLoggingSupport().setEnableLog(false);
        	player.run();
        }
        finally {
        	controller.setHintMode(true);
        	controller.getLoggingSupport().setEnableLog(true);
        }

//        if (lastVisitedNode != controller.getSolutionState().getCurrentNode())
//            controller.setCurrentNode(lastVisitedNode);
	}

	//////////////////////////////////////////////////////////////////////
	/**
	    parsing the uniqueID from link name.
	 */
	//////////////////////////////////////////////////////////////////////

	private int getUniqueIDFromLinkName(String linkName) {
		if (linkName == null)
			return NON_VALID_ID;

		if (linkName.equals(""))
			return NON_VALID_ID;

		int index = linkName.indexOf(LINK_NAME_PREFIX);

		if (index < 0)
			return NON_VALID_ID;

		String idString = linkName.substring(LINK_NAME_PREFIX.length());

		return Integer.valueOf(idString).intValue();
	}

	/**
	 * Add an entry to the {@link #traversedLinksModel}. 
	 * @param edge link in graph
	 * @param selection student's selection
	 * @param action student's action
	 * @param input student's input
	 * @param messageObject InterfaceAction (only?) message with student s,a,i
	 * @param checkResult result {@link EdgeData#BUGGY}, e.g.), from rule engine, if any
	 */
	public void addLinkNode(int uniqueID, Vector selection, Vector action, Vector input,
			String actionType) {
		MessageObject messageObject = createInterfaceAction(selection, action, input);
		traversedLinksModel.addLinkNode(LINK_NAME_PREFIX+uniqueID, messageObject, actionType, uniqueID);
	}

	/**
	 * Add an entry to the {@link #traversedLinksModel}. 
	 * @param edge link in graph
	 * @param selection student's selection
	 * @param action student's action
	 * @param input student's input
	 * @param messageObject InterfaceAction (only?) message with student s,a,i
	 * @param checkResult result {@link EdgeData#BUGGY}, e.g.), from rule engine, if any
	 */
	public void addLinkNode(ProblemEdge edge, MessageObject messageObject, String checkResult) {
		EdgeData edgeData = edge.getEdgeData();

		String authorIntent = EdgeData.checkResultToActionType(checkResult);
		if (authorIntent == null || authorIntent.length() < 1)
			authorIntent = edgeData.getActionType();

		// only add the link with the authorIntent as CORRECT_ACTION or FIREABLE_BUGGY_ACTION
		if (!(authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION)
                        || authorIntent.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))) {
			return;
		}

		if (messageObject == null)
			messageObject = edgeData.getDemoMsgObj();
		int uniqueID = edgeData.getUniqueID();

		traversedLinksModel.addLinkNode(LINK_NAME_PREFIX+uniqueID, messageObject, authorIntent, uniqueID);
	}

	/**
	 * Create an InterfaceAction message from the given property values.
	 * @param selection returns null if null or empty
	 * @param action if empty or null, tries to get action from the controller's
	 *               table of CommWidgets
	 * @param input returns null if null or empty
	 * @return Comm message of type InterfaceAction
	 */
	public MessageObject createInterfaceAction(Vector selection, Vector action, Vector input) {
		if (selection == null || selection.isEmpty() || input == null || input.isEmpty())
			return null;
		MessageObject result = MessageObject.create(MsgType.INTERFACE_ACTION, "NotePropertySet");

		// get actions from selections
		// FIXME: this won't work for remote BR
		if (action == null)
			action = new Vector();
		if (action.size() < 1) { // HACK!!!: update caller's Vector if not null but empty
			if (controller.getProblemModel().isUseCommWidgetFlag()) {
				for (int i = 0; i < selection.size(); i++) {
					String selectionName = (String) selection.elementAt(i);
					JCommWidget d = controller.getCommWidget(selectionName);
					if (d != null)
						action.addElement(d.getActionName());
				}
			}
		}
		result.setSelection(selection);
		result.setAction(action);
		result.setInput(input);
		return result;
	}

	/**
	 * Retract the solution state by undoing given number of steps. Sets the tutor
	 * back to the start state and then replays remaining steps through the tutor
	 * and the student interface.
	 * @param nToDelete number of steps to undo; returns to start state if
	 *               greater than number of steps in {@link #traversedLinksModel}.
	 * @return number of steps undone
	 */
	public int retractLinksFromTail(int nToDelete) {
		int result = traversedLinksModel.deleteLinksFromTail(nToDelete);
		List linkNodes = traversedLinksModel.getTraversedLinkNodes();
		if (trace.getDebugCode("br")) trace.out("br", "retractLinksFromTail(): nToDelete "+nToDelete+
				", nRemaining "+linkNodes.size());

		// save list of nodes above before goToStartState() clears TraversedLinksModel
		controller.goToStartState();
		if (linkNodes.size() < 1)
			return result;
		CtatModeModel mm = controller.getCtatModeModel();
		if (mm != null && mm.isRuleEngineTracing())
			goToWmState(linkNodes);
		else {
			int i = 0;
			for (Iterator it = linkNodes.iterator(); it.hasNext(); ++i) {
				TraversedLinkObject tlo = (TraversedLinkObject) it.next();
				controller.handleCommMessage(tlo.getCommMsgObj());
			}
		}
		return result;
	}

	/**
	 * Build a Go_To_WM_State message from the {@link #traversedLinksModel}
	 * and call {@link BR_Controller#sendGo_To_WM_State(Vector, Vector, Vector, Vector, Vector)
	 * controller.sendGo_To_WM_State()} to return to the start state and send it to the rule engine.
	 * @param linkNodes path of TraversedLinkObject objects to follow;
	 *        no-op if empty
	 * @return number of links traversed
	 */
	private int goToWmState(List linkNodes) {
		if (linkNodes.size() < 1) {
			trace.err("ProcessedTraversedLinks.goToWmState(): empty link list");
			return 0;
		}
		
		Vector selectionList = new Vector();
		Vector actionList = new Vector();
        Vector inputList = new Vector();
		Vector authorIntentList = new Vector();
        Vector uniqueIDList = new Vector();

        int i = 0;
        String errMsg = null;
		for (Iterator it = linkNodes.iterator(); it.hasNext(); ++i) {
			TraversedLinkObject linkNode = (TraversedLinkObject) it.next();
			MessageObject msg = linkNode.getCommMsgObj();
			Vector selection = (Vector) msg.getProperty("Selection");
			Vector action = (Vector) msg.getProperty("Action");
			Vector input = (Vector) msg.getProperty("Input");
			if (selection == null) {
				errMsg = "selection["+i+"] is null";
				break;
			} else if (action == null) {
				errMsg = "action["+i+"] is null";
				break;
			} else if (input == null) {
				errMsg = "input["+i+"] is null";
				break;
			} else {
				selectionList.add(selection);
				actionList.add(action);
				inputList.add(input);
				authorIntentList.add(linkNode.getAuthorIntent());
				uniqueIDList.add(new Integer(linkNode.getUniqueID()));
			}
		}
		if (i < linkNodes.size()) {
			trace.err("ProcessTraversedLinks: failed to Go_To_WM_State due to problem at link "+
					i+": "+errMsg);
			return 0;
		}
		controller.sendGo_To_WM_State(selectionList, actionList, inputList,
				authorIntentList, uniqueIDList, true);
		return i;
	}

	/**
	 * Get the CommMsgs from the currently-saved links.
	 * @return list with element type MessageObject
	 */
	public List getCommMsgs() {
		return traversedLinksModel.getCommMsgs();
	}
}
