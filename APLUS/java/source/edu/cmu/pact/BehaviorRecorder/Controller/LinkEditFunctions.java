package edu.cmu.pact.BehaviorRecorder.Controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.EnumMap;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.cmu.pact.BehaviorRecorder.Dialogs.PasteSpecialDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.PasteSpecialDialog.EdgeAttribute;
import edu.cmu.pact.BehaviorRecorder.Dialogs.HelpSuccessPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeColorEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeRewiredEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class LinkEditFunctions implements Serializable {
	private static final long serialVersionUID = 2394717262980666306L;
	private static final String DELETE_LINK = "Delete Link";
	public transient String jDialogResult;
	ProblemEdge problemEdge;
	EdgeData edgeData;
	ProblemNode childNode;
	ProblemNode parentNode;
	BR_Controller controller;

    /**
     * Enable a calling routine to set transient fields after deserializing/
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    }
	
	public LinkEditFunctions(ProblemEdge edge, BR_Controller controller){
		this.problemEdge = edge;
		this.controller = controller;
		this.edgeData = edge.getEdgeData();
		this.childNode = edge.getDest();
		this.parentNode = edge.getSource();
	}
	
	public void testProcessInsert(boolean above){
		processInsertNodeAbove2(above);
	}
	
	public void processInsertNodeAbove2(boolean above){    				
    	List<ProblemModelEvent> subEvents = new ArrayList<ProblemModelEvent>();
    	Vector selection = new Vector();
    	selection.add("No_Selection");
		Vector action = new Vector();
		action.add("No_Action");
		Vector input = new Vector();
		input.add("No_Value");
		MessageObject mo = new PseudoTutorMessageBuilder().buildInterfaceAction(selection, 
				action, input, controller);
		ProblemNode newNode = controller.createProblemNode(parentNode, selection ,parentNode.getOutDegree());

		EdgeData newEdgeData = new EdgeData(controller.getProblemModel());	
	    //The following will setup the newEdgeData
	    newEdgeData.addRuleName(RuleProduction.UNNAMED);
	
	    newEdgeData.setSelection((Vector) selection.clone());
	    newEdgeData.setAction((Vector) action.clone());
	    newEdgeData.setInput((Vector) input.clone());
	    newEdgeData.setDemoMsgObj(mo);
	    newEdgeData.setActionType(EdgeData.CORRECT_ACTION);
	    newEdgeData.updateDefaultHint();
	    newEdgeData.getActionLabel().resetForeground();
	    if(above)
	    	newEdgeData.setPreferredEdge(edgeData.isPreferredEdge());
	    else
	    	newEdgeData.setPreferredEdge(true);
	    ProblemEdge newEdge;
	    if(above)
	    	newEdge= controller.getProblemModel().getProblemGraph().addEdge(parentNode, newNode, newEdgeData);
	    else
	    	newEdge= controller.getProblemModel().getProblemGraph().addEdge(newNode, childNode, newEdgeData);
	    newEdgeData.getActionLabel().update();
	    newEdge.addEdgeLabels();
	    ExampleTracerGraph currentGraph = controller.getExampleTracerGraph();
	    EdgeCreatedEvent edgeCreatedEvent = new EdgeCreatedEvent(this, newEdge);
	    
	    LinkGroup smallestContainingGroup = currentGraph.getSmallestContainingGroup(currentGraph.getLink(problemEdge));
	    if(smallestContainingGroup!=null){
	    	edgeCreatedEvent.setGroupToAddTo(smallestContainingGroup);
	    }
	    subEvents.add(edgeCreatedEvent);
	    controller.deleteSingleEdge(problemEdge, false);
	    EdgeDeletedEvent edgeDeletedEvent = new EdgeDeletedEvent(problemEdge);
	    edgeDeletedEvent.setEdgeBeingRewired(true);
	    //subEvents.add(edgeDeletedEvent);
	    ProblemEdge rewireEdge;
	    if(above){
	    	rewireEdge =  controller.getProblemModel().getProblemGraph().addEdge(newNode, childNode, edgeData);
	    	rewireEdge.getEdgeData().setPreferredEdge(true);
	    }else{
	    	rewireEdge =  controller.getProblemModel().getProblemGraph().addEdge(parentNode, newNode, edgeData);
	    }
	    rewireEdge.getEdgeData().getActionLabel().update();
	    rewireEdge.addEdgeLabels();
		edgeCreatedEvent = new EdgeCreatedEvent(this,rewireEdge);
		edgeCreatedEvent.setEdgeBeingRewired(true);
		
		if(smallestContainingGroup!=null)
			edgeCreatedEvent.setGroupToAddTo(smallestContainingGroup);
		EdgeRewiredEvent edgeRewiredEvent = new EdgeRewiredEvent(this, edgeDeletedEvent, edgeCreatedEvent);
		subEvents.add(edgeRewiredEvent);
		NodeCreatedEvent fireMe = new NodeCreatedEvent(this, newNode,subEvents);
		controller.getProblemModel().fireProblemModelEvent(fireMe);
	    return;
	}

	
	public void changeActionType(String newActionType) {
		
		if (newActionType == null ||
				newActionType.equalsIgnoreCase(edgeData.getActionType()))
			return;

		String serializedBeforeEdit = problemEdge.toXMLString();

    	if (newActionType.equals(EdgeData.CORRECT_ACTION)) {
    		handleCorrectAction();
    	} else if (newActionType.equals(EdgeData.BUGGY_ACTION)) {
    		handleBuggyAction();
    	} else if (newActionType.equals(EdgeData.FIREABLE_BUGGY_ACTION)) {
    		handleFireableBuggyAction();
    	} else if (newActionType.equals(EdgeData.UNTRACEABLE_ERROR)) {
    		handleUntraceableAction();
    	}
    	checkpoint(serializedBeforeEdit, newActionType);
    	edgeData.getActionLabel().resetForeground();
	}

	/**
	 * Check whether the XML representation of the edge changed, and checkpoint if so.
	 * @param serializedBeforeEdit
	 * @param newActionType
	 */
	private void checkpoint(String serializedBeforeEdit, String newActionType) {
    	String serializedAfterEdit = problemEdge.toXMLString();
    	if (serializedBeforeEdit.equals(serializedAfterEdit))
    		return;
    	if (trace.getDebugCode("undo"))
    		trace.out("undo", "ActionLabelHandler.actionPerformed("+newActionType+") XML before:\n"+
    				serializedBeforeEdit+"\nXML after:\n"+serializedAfterEdit);

    	//Undo checkpoint for Changing Action Type ID: 1337
    	ActionEvent ae = new ActionEvent(this, 0, ActionLabelHandler.CHANGE_ACTION_TYPE);
    	controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
	}

	private void handleUntraceableAction() {
	        if (edgeData.getActionType().equalsIgnoreCase(
	                EdgeData.CORRECT_ACTION)
	                || edgeData.getActionType().equalsIgnoreCase(
	                        EdgeData.FIREABLE_BUGGY_ACTION)) {
	            updateActionTypeFromCorrectToBuggy(EdgeData.UNTRACEABLE_ERROR);
	        } else {
	            if (childNode == controller
	                    .getSolutionState().getCurrentNode()) {
	                controller.setCurrentNode(parentNode);
	                controller.sendCommMsgs(
	                        parentNode,
	                        controller.getProblemModel()
	                                .getStartNode());
	            }

	            this.edgeData.setActionType(EdgeData.UNTRACEABLE_ERROR);
	            this.edgeData.setBuggyMsg ("");
	        }
	    }
	
	   private void handleFireableBuggyAction() {
	        if (this.edgeData.getActionType().equalsIgnoreCase(
	                EdgeData.CORRECT_ACTION)
	                && (this.edgeData.getSuccessMsg().trim().length() > 0 || edgeData
	                        .haveNoneDefaultHint())) {
	            // trace.out (5, this, "show switchIntentPanel");
	            new ChangeActionTypePanel(
	                    EdgeData.FIREABLE_BUGGY_ACTION);
	        } else {
	            EditBuggyMsgPanel editBuggyMsgPanel = new EditBuggyMsgPanel(true);
	            if (editBuggyMsgPanel.getCancelled() == true)
	                return;

	            this.edgeData.setActionType(EdgeData.FIREABLE_BUGGY_ACTION);
	        }
	   }
	 private void handleCorrectAction() {
	        if ((this.edgeData.getActionType().equalsIgnoreCase(
	                EdgeData.BUGGY_ACTION) || this.edgeData.getActionType()
	                .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
	                && this.edgeData.getBuggyMsg().trim().length() > 0) {
	            new ChangeActionTypePanel( EdgeData.CORRECT_ACTION);
	        } else {
	            // HelpSuccessPanel helpSuccessPanel = new
	            // HelpSuccessPanel(ESE_Frame.instance(), "Edit Success and Hint
	            // Messages");
	            this.edgeData.setActionType(EdgeData.CORRECT_ACTION);
	            this.edgeData.setBuggyMsg ("");
	        }
	        controller.getProblemModel().fireProblemModelEvent(
	        		new EdgeUpdatedEvent(this, this.problemEdge, true));
	    }
	 
	/**
	 * Invoke {@link #processDeleteSingleEdge(boolean)} for JUnit tests.
	 */
	public void testProcessDeleteSingleEdge(){
		processDeleteSingleEdge(true);
	}
	
	/**
	 * Delete a single edge.
	 * @param isJUnitTesting true if JUnit testing: avoids stop on dialogs.
	 */
	public void processDeleteSingleEdge(boolean isJUnitTesting){
		int i;
		
		List<ProblemEdge> childOutEdges = childNode.getOutgoingEdges();
		List<ProblemNode> grandChildren = new ArrayList<ProblemNode>();
		for(i= 0; i < childOutEdges.size(); i++){
			grandChildren.add(childOutEdges.get(0).getDest());
		}
		Vector<ProblemNode> children = parentNode.getChildren();
		String mergeNotPossibleMsg = null;
		
		
		//All Testing to see whether merging can be done:
		ProblemNode conflictNode = null;
		for(Iterator<ProblemNode> it = children.iterator(); it.hasNext();){
			ProblemNode curr = it.next();
			if(grandChildren.contains(curr)){
				mergeNotPossibleMsg = "Merging the states would result in 2 links with the same source and destination";
				conflictNode = curr;
				break;
			}
		}
		if(conflictNode==null){
			List<ProblemNode> parentsParents = parentNode.getParents();
			List<ProblemNode> childsParents = childNode.getParents();
			for(Iterator<ProblemNode> it = parentsParents.iterator(); it.hasNext();){
				ProblemNode curr = it.next();
				if(childsParents.contains(curr)){
					mergeNotPossibleMsg = "Merging the states would result in 2 links with the same source and destination";
					conflictNode = curr;
					break;
				}
			}
		}
		Vector ancestorNodesList;
		if(conflictNode==null){
			List<ProblemNode> childsParents = childNode.getParents();
			for(Iterator<ProblemNode> it = childsParents.iterator(); it.hasNext();){
				ProblemNode curr = it.next();
				if(curr==parentNode)
					continue;
				ancestorNodesList = new Vector();
				controller.getProblemModel().findAncestorNodesListIgnoringLinkX(curr, ancestorNodesList, problemEdge);
				if(ancestorNodesList.contains(parentNode)){
					mergeNotPossibleMsg = "Merging the states would create a cycle in the graph";
					conflictNode = curr;
					break;
				}
			}
		}
		
		
		int result;
		
		//if this method isn't being called by junit
		if(!isJUnitTesting){
			result = doDeleteLinkMergeDialogue("Merge states " + parentNode.getName() + " and " + childNode.getName() + ".", mergeNotPossibleMsg);
		}else{
			if(conflictNode==null)
				result = 1;
			else
				result = 2;
		}
		//Cancel delete-this-link
		if(result==3)
			return;
		if(result==2){//delete only the edge, no merging.
			if (problemEdge.isPreferredEdge())
				resetPreferredPath(false);
			controller.deleteSingleEdge(problemEdge, true);

			//Undo checkpoint for deleting edge ID: 1337
			ActionEvent ae = new ActionEvent(this, 0, DELETE_LINK);
			controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
			return;
		}
		
		//Since we don't want to kill the start State by a merge;
		ProblemNode source,target;
		boolean childIsStartNode = (controller.getExampleTracerGraph().getStartNode().getProblemNode() == childNode);
		if(childIsStartNode){
			target = childNode;
			source = parentNode;
		}
		else{
			target = parentNode;
			source = childNode;
		}
				//Deleting the edge AND merging
		List<ProblemModelEvent> subEvents = new ArrayList<ProblemModelEvent>();
		boolean targetHasPreferredEdge = target.hasOutGoingPreferredEdge(problemEdge);
		boolean sourceHasPreferredEdge = source.hasOutGoingPreferredEdge(null);
		if(!targetHasPreferredEdge && !sourceHasPreferredEdge){
			try{
				ProblemEdge temp;
				temp = controller.getProblemModel().updatePreferredPath(target, problemEdge, false);
				targetHasPreferredEdge = (temp!=null);
			}catch(Exception e){}
		}
		
		controller.deleteSingleEdge(problemEdge, false);//we don't want to delete edge data
		//merge-states2 assumes a graph were preferred states are correct.
		//what if deleting link that is to a state that has 2 incoming buggy edges and we merge.
		subEvents = controller.mergeStates2(source, target, false, false, targetHasPreferredEdge);
		controller.getProblemModel().fireProblemModelEvent(new EdgeDeletedEvent(problemEdge, subEvents));
		
		//Undo checkpoint for deleting edge ID: 1337
		ActionEvent ae = new ActionEvent(this, 0, DELETE_LINK);
		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
		return;
	}
	
	private int doDeleteLinkMergeDialogue(String mergeText, String mergeNotPossibleMsg){
		
		jDialogResult = null;
		String deleteText = "Don't merge states";
		String cancelText = "Cancel";
		JButton merge = new JButton(mergeText);
		if(mergeNotPossibleMsg!=null){
			merge.setEnabled(false);
			merge.setToolTipText(mergeNotPossibleMsg);
		}
		JButton delete = new JButton(deleteText);
		JButton cancel = new JButton(cancelText);
		Object buttons[] = {"Delete this link and...", merge,delete};
		//Object buttons[] = {"Delete this link and...", merge,delete,cancel};
		JOptionPane op = new JOptionPane();
		op.setMessage(buttons);
		Object[] smallButtons = {cancel};
		op.setOptions(smallButtons);
		final JDialog d = op.createDialog(controller.getActiveWindow(), DELETE_LINK);
		ActionListener closeDialog = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				jDialogResult = e.getActionCommand();
				d.dispose();
			}
		};
		merge.addActionListener(closeDialog);
		delete.addActionListener(closeDialog);
		cancel.addActionListener(closeDialog);
		d.setVisible(true);
		if(jDialogResult==null || jDialogResult.equals(cancelText))
			return 3;
		if(jDialogResult.equals(deleteText))
			return 2;
		if(jDialogResult.equals(mergeText))
			return 1;
		return 3;
	}
	
	private boolean resetPreferredPath(boolean throwException) {
		try {
			return (controller.getProblemModel().updatePreferredPath(parentNode, problemEdge, throwException)!=null);
        } catch (ProblemModelException e) {
            NodeView outVertex = parentNode.getNodeView();
            // display warning message
            String message[] = { "You don't have preferred path defined",
                    "from state: " + outVertex.getText().trim(), "", };

            JOptionPane.showMessageDialog(controller.getActiveWindow(), message, "Warning",
                    JOptionPane.WARNING_MESSAGE);

            e.printStackTrace();
        }
		
		return false;
	}
	
	
	private void checkWithProductionSystem() {

		// we need to send slection and input to LISP for check
		if (parentNode != controller.getCurrentNode())
			controller.goToState(parentNode);
		controller.checkWithLispSingle(problemEdge);
	}

	/**
	 * Set this link to be the preferred edge from its source node.
	 * Will remove the preferred path setting from a sibling edge.
	 * @param colorToRestoreTo
	 * @return true if changed the edge; false if no-op
	 */
	public boolean setPreferredArc(Color colorToRestoreTo) {
		// if this arc is a preferred arc then return
		if (edgeData.isPreferredEdge()) {
			return false;
		}
		// get the edge for this label
		ProblemEdge thisEdge = problemEdge;
		// get the source and dest nodes
		ProblemNode sourceNode = thisEdge.getNodes()[ProblemEdge.SOURCE];
		// if more than one correct action arc from the source node
		// then set this arc as the preferred arc and set the other correct
		// action arc as the not preferred arc
		EdgeData tempMyEdge = null;
		ProblemEdge tempEdge = null;
		Enumeration<ProblemEdge> iter = controller.getProblemModel()
				.getProblemGraph().getOutgoingEdges(sourceNode);
		boolean hasPreferPathSet = false;
		while (iter.hasMoreElements() && !hasPreferPathSet) {
			tempEdge = (ProblemEdge) iter.nextElement();
			tempMyEdge = tempEdge.getEdgeData();
			if (tempMyEdge.isPreferredEdge())
				hasPreferPathSet = true;
		}
		if (tempEdge != null && tempMyEdge != null
				&& !tempEdge.equals(thisEdge)) {
			tempMyEdge.setPreferredEdge(false);
			edgeData.setPreferredEdge(true);
			controller.getProblemModel().fireProblemModelEvent(new EdgeUpdatedEvent(this, tempEdge,  true));
			controller.getProblemModel().fireProblemModelEvent(new EdgeUpdatedEvent(this, thisEdge, true));
			if(colorToRestoreTo!=null)
				controller.getProblemModel().fireProblemModelEvent(new EdgeColorEvent(this,thisEdge,colorToRestoreTo));

			//Undo checkpoint for Setting Preferred Path ID: 1337
        	ActionEvent ae = new ActionEvent(this, 0, ActionLabelHandler.SET_AS_PREFERRED_PATH);
        	controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
			return true;
		}
		return false;
	}
	
	   public boolean processDemonstrateLink() {
	    	
	    	// Prevent the user from actually using 'Set Start State' mode
	    	if (controller.getCtatModeModel().isDefiningStartState()) {
	    		JOptionPane.showMessageDialog(controller.getActiveWindow(),
	    				"You are currently in 'Set Start State' mode. Please switch to another mode to use" +
	    				" 'Demonstrate This Link'",
	    				"Warning",
	    				JOptionPane.WARNING_MESSAGE);
	    		return false;
	    	}
	    	
	    	int value = JOptionPane.showConfirmDialog(
					controller.getActiveWindow(), 
					"Going into 'Demonstrate This Link' Recording. " + 
					"This will only work in 'Demonstrate' mode or 'Test Tutor' mode", 
					"Demonstrate This Link",
					JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
	    	if (value == JOptionPane.OK_OPTION) {
	    		controller.goToState(parentNode);
	    		controller.getCtatModeModel().enterDemonstrateThisLinkMode(problemEdge.getUniqueID());
	    		return true;
	    	}
	    	else {
	    		controller.getCtatModeModel().exitDemonstrateThisLinkMode();
	    		return false;
	    	}

	    }
	   
	   	public void processCancelDemonstrateLink() {
			controller.getCtatModeModel().exitDemonstrateThisLinkMode();
	    }
	   public class ChangeActionTypePanel extends JDialog implements ActionListener {

	        /**
		 * 
		 */
		private static final long serialVersionUID = 8808164135294824941L;

			String newIntent;

	        JTextArea displayJTextArea;

	        Container contentPane = getContentPane();

	        private JPanel optionPanel = new JPanel();

	        private JPanel okPanel = new JPanel();

	        private ButtonGroup group = new ButtonGroup();

	        private JRadioButton[] buttons = {
	                new JRadioButton("Cancel"),
	                new JRadioButton(
	                        "Change action type, but keep attached messages."),
	                new JRadioButton(
	                        "Change both action type and attached messages.") };

	        private JButton okButton = new JButton("OK");

	        /** Edge information before edits. */
	        private final String serializedBeforeEdit;

	        public ChangeActionTypePanel(String newIntentP) {
	            super(controller.getActiveWindow(), "Warning",
	                    true);
	    		serializedBeforeEdit = edgeData.getEdge().toXMLString();

	            newIntent = newIntentP;

	           
	            setResizable(false);

	            contentPane.setLayout(new BorderLayout());

	            String displayText;

	            if (newIntentP.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
	                displayText = "The link you selected has a buggy message attached. When you change "
	                        + "the action type from "
	                        + edgeData.getActionType()
	                        + " to "
	                        + newIntent
	                        + " the buggy message is no longer useful (since it "
	                        + "would never be displayed by the tutor). You may want to change "
	                        + "the message or switch it to Help/Success messages.";
	            } else {
	                displayText = "The link you selected has attached Help/Success messages. When you change "
	                        + "the action type from "
	                        + edgeData.getActionType()
	                        + " to "
	                        + newIntent
	                        + " help/success messages are no longer useful (since they "
	                        + "would never be displayed by the tutor). You may want to change "
	                        + "the messages or switch them to Buggy message.";
	            }

	            displayJTextArea = new JTextArea(displayText);
	            contentPane.add(displayJTextArea, BorderLayout.NORTH);
	            displayJTextArea.setEditable(false);
	            displayJTextArea.setLineWrap(true);
	            displayJTextArea.setWrapStyleWord(true);

	            optionPanel.setLayout(new GridLayout(3, 1));

	            for (int i = 0; i < 3; i++) {
	                optionPanel.add(buttons[i]);
	                group.add(buttons[i]);
	            }
	            buttons[0].setSelected(true);

	            contentPane.add(optionPanel, BorderLayout.CENTER);

	            okPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	            okPanel.add(okButton);

	            contentPane.add(okPanel, BorderLayout.SOUTH);

	            okButton.addActionListener(this);

	            addWindowListener(new java.awt.event.WindowAdapter() {

	                public void windowClosing(java.awt.event.WindowEvent e) {
	                    setVisible(false);
	                    dispose();
	                }
	            });

	            setSize (300, 400);
                setLocationRelativeTo(null);
	            setVisible(true);
	        }

	        public void actionPerformed(ActionEvent ae) {
	            setVisible(false);
	            dispose();

	            if (buttons[0].isSelected()) {
	                return;
	            }

	            boolean continueDeleteFlag = false;

	            String oldAuthorIntent = edgeData.getActionType();

	            if ((oldAuthorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION) || oldAuthorIntent
	                    .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
	                    && (newIntent.equalsIgnoreCase(EdgeData.BUGGY_ACTION) || newIntent
	                            .equalsIgnoreCase(EdgeData.UNTRACEABLE_ERROR)))
	                continueDeleteFlag = true;
	            
	            if (continueDeleteFlag) {
	            	handleTraverseableToNonTraverseable2(newIntent);
	               // processDeleteLink(true);
	            }
	            if (buttons[1].isSelected()) {
	                edgeData.setActionType(newIntent);
	                ;
	            } else if (buttons[2].isSelected()) {
	                if (newIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
	                    new HelpSuccessPanel(
	                            controller,
	                            edgeData, true);
	                    edgeData.setBuggyMsg ("");
	                } else {
	                    new EditBuggyMsgPanel(true);

	                 //   if (newIntent.equalsIgnoreCase(EdgeData.BUGGY_ACTION))
	                //        updateCurrNodeAndPreferPathMark();

	                    edgeData.setSuccessMsg ("");
	                    edgeData.setHints(new Vector());
	                }

	                edgeData.setActionType(newIntent);
	            }
	        }
	    }
	   public void showEditBuggyMsgPanel(){
		   new EditBuggyMsgPanel(false);
	   }
	   private class EditBuggyMsgPanel extends JDialog implements ActionListener {

	        /**
		 * 
		 */
		private static final long serialVersionUID = -3351810150675944882L;

			JPanel buggyMsgPanel = new JPanel();

	        JPanel OptionsPanel = new JPanel();

	        JCheckBox checkOption1 = new JCheckBox(
	                "Copy to all links with the same Selection/Action/Input.");

	        JLabel copyFromJLabel = new JLabel(" Copy from buggy states:");

	        JComboBox edgesJComboBox = new JComboBox();

	        Vector myEdgesWithBuggy = new Vector();

	        JPanel okCancelPanel = new JPanel();

	        Container contentPane = getContentPane();

	        JLabel explainJLabel = new JLabel("Please define your buggy message:");

	        JTextArea buggyArea;

	        JScrollPane buggyAreaScrollPane;

	        JButton okJButton = new JButton("   OK  ");

	        JButton cancelJButton = new JButton("Cancel");

	        static final String title = "Edit your buggy message";

	        private boolean cancelled;

	        public boolean getCancelled() {
	            return cancelled;
	        }

	        /** Edge information before edits. */
	        private final String serializedBeforeEdit;
	        
	        /** True if this edit is part of a larger operation, which is checkpointed as a whole. */
	        private boolean partOfLargerEdit = false;

	        private EditBuggyMsgPanel(boolean partOfLargerEdit) {
	            super(controller.getActiveWindow(), title, true);
	    		serializedBeforeEdit = edgeData.getEdge().toXMLString();
	        	this.partOfLargerEdit = partOfLargerEdit;

//	            setLocation(new java.awt.Point(300, 200));
//	            setSize(350, 300);

	            buggyArea = new JTextArea();
	            { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(buggyArea); }
	            buggyArea.setLineWrap(true);
	            buggyArea.setWrapStyleWord(true);
	            buggyArea.setVisible(true);
	            buggyArea.setText(edgeData.getBuggyMsg().trim());

	            buggyAreaScrollPane = new JScrollPane(buggyArea);

	            buggyMsgPanel.setLayout(new BorderLayout());
	            buggyMsgPanel.add(explainJLabel, BorderLayout.NORTH);
	            buggyMsgPanel.add(buggyAreaScrollPane, BorderLayout.CENTER);

	            // process copy_from list
	            ProblemEdge tempEdge;
	            EdgeData tempMyEdge;

	            String tempAuthorIntent;
	            String tempBuggyMsg;

	            NodeView tempParentVertex, tempChildVertex;

	            Enumeration iter = controller
	                    .getProblemModel().getProblemGraph().edges();
	            myEdgesWithBuggy = new Vector();
	            while (iter.hasMoreElements()) {

	                tempEdge = (ProblemEdge) iter.nextElement();
	                if (problemEdge != tempEdge) {
	                    tempMyEdge = tempEdge.getEdgeData();

	                    tempAuthorIntent = tempMyEdge.getActionType();

	                    tempBuggyMsg = tempMyEdge.getBuggyMsg().trim();

	                    if (!tempAuthorIntent
	                            .equalsIgnoreCase(EdgeData.CORRECT_ACTION)
	                            && tempBuggyMsg.length() > 0) {
	                        myEdgesWithBuggy.addElement(tempMyEdge);
	                        tempParentVertex = tempEdge.getNodes()[ProblemEdge.SOURCE]
	                                .getNodeView();
	                        tempChildVertex = tempEdge.getNodes()[ProblemEdge.DEST]
	                                .getNodeView();
	                        edgesJComboBox.addItem(tempParentVertex.getText()
	                                + " to " + tempChildVertex.getText());
	                    }
	                }
	            }

	            if (((DefaultComboBoxModel) edgesJComboBox.getModel()).getSize() == 0)
	                edgesJComboBox.addItem("None");

	            // OptionsPanel.setLayout(new BorderLayout());
	            OptionsPanel.setLayout(new GridLayout(3, 1));

	            OptionsPanel.add(checkOption1);

	            OptionsPanel.add(copyFromJLabel);

	            OptionsPanel.add(edgesJComboBox);

	            buggyMsgPanel.add(OptionsPanel, BorderLayout.SOUTH);

	            // optionPanel.add(edgesJComboBox);
	            edgesJComboBox.addActionListener(this);

	            okCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	            okJButton.setSize(cancelJButton.getSize());
	            okCancelPanel.add(okJButton);
	            okCancelPanel.add(cancelJButton);

	            contentPane.setLayout(new BorderLayout());
	            contentPane.add(buggyMsgPanel, BorderLayout.CENTER);
	            contentPane.add(okCancelPanel, BorderLayout.SOUTH);

	            okJButton.addActionListener(this);
	            cancelJButton.addActionListener(this);

	            addWindowListener(new java.awt.event.WindowAdapter() {

	                public void windowClosing(java.awt.event.WindowEvent e) {
	                    thisWindowClosing();
	                }
	            });

	            setSize (300, 400);
	            setLocationRelativeTo(null);
	            setVisible(true);
	            
	        }

	        public void thisWindowClosing() {
	            setVisible(false);
	            dispose();
	            String serializedAfterEdit = edgeData.getEdge().toXMLString();
	            if (!(serializedBeforeEdit.equals(serializedAfterEdit))) {
	            	if (trace.getDebugCode("undo"))
	            		trace.out("undo", "EditBuggyMsgPanel.windowClosing() XML before:\n"+
	            				serializedBeforeEdit+"\nXML after:\n"+serializedAfterEdit);
	            }        		
	        }

	        public void actionPerformed(ActionEvent ae) {
	            if (ae.getSource() == okJButton) {
	                String newBuggyText = buggyArea.getText();

	                if (newBuggyText != null) {
	                    newBuggyText = newBuggyText.trim();

	                    edgeData.getActionLabel().resetForeground();
	                    edgeData.setBuggyMsg (newBuggyText);
						
	                    
	                    // ESE_Frame.instance().sendInCorrectActionMsg(
	                    // actionLabel.getSelection(), actionLabel.getInput());

	                    // copy the new buggy msg to all links withe the same triple
	                    if (checkOption1.isSelected()) {
	                        Vector sameTripleLinks = controller.getProblemModel()
	                                .findSameTripleEdges(
	                                        edgeData.getSelection(),
	                                        edgeData.getAction(),
	                                        edgeData.getInput());

	                        copyBuggyMsgs(edgeData.getActionLabel(), sameTripleLinks);
	                    }
	                    
	                	//Undo checkpoint for Editing Buggy Message ID: 1337
						if (!this.partOfLargerEdit) {
							ActionEvent evt = new ActionEvent(this, 0, ActionLabelHandler.EDIT_BUG_MESSAGE);
							controller.getUndoPacket().getCheckpointAction().actionPerformed(evt);
						}

	                }

	                setVisible(false);
	                dispose();
	            } else if (ae.getSource() == cancelJButton) {
	                setVisible(false);
	                cancelled = true;
	                dispose();

	            } else if (ae.getSource() == edgesJComboBox) {

	                EdgeData selectedMyEdge;

	                int slectedEdgeIndex = edgesJComboBox.getSelectedIndex();

	                if (slectedEdgeIndex >= 0) {
	                    String selectedItem = (String) edgesJComboBox
	                            .getSelectedItem();
	                    if (!selectedItem.equals("None")) {
	                        selectedMyEdge = (EdgeData) myEdgesWithBuggy
	                                .elementAt(slectedEdgeIndex);

	                        buggyArea.setText(selectedMyEdge.getBuggyMsg()
	                                .trim());
	                    }
	                }
	            }
	        }

	        // ///////////////////////////////////////////////////////////////////////////////
	        /**
	         * copy the this link buggy message to all of links with the same
	         * selection-action-input.
	         */
	        // ///////////////////////////////////////////////////////////////////////////////
	        void copyBuggyMsgs(ActionLabel fromActionLabel, Vector toLinksVector) {

	            if (toLinksVector == null)
	                return;

	            int numberOfLinks = toLinksVector.size();

	            ProblemEdge tempEdge;
	            EdgeData tempMyEdge;

	            for (int i = 0; i < numberOfLinks; i++) {
	                tempEdge = (ProblemEdge) toLinksVector.elementAt(i);
	                tempMyEdge = tempEdge.getEdgeData();
	                tempMyEdge.setBuggyMsg(fromActionLabel.getEdge()
	                        .getBuggyMsg());
	            }

	            return;
	        }
	    }
	   private void updateActionTypeFromCorrectToBuggy(String newAuthorIntent) {
	    	//this is kinda funky... also cancel doesn't work. (or didn't work?)
	        if (this.edgeData.getSuccessMsg().trim().length() > 0
	                || edgeData.haveNoneDefaultHint()) {
	            new ChangeActionTypePanel(
	                    newAuthorIntent);

	            return;
	        }
	        
	        EditBuggyMsgPanel editBuggyMsgPanel = new EditBuggyMsgPanel(true);
	        if (editBuggyMsgPanel.getCancelled() == true) {
	            if (trace.getDebugCode("mps")) trace.out("mps", "RETURNING");
	            controller.fireCtatModeEvent(CtatModeEvent.REPAINT); 
	            return;
	        }
	        handleTraverseableToNonTraverseable2(newAuthorIntent);
	        
	        return;
	    }
	   private void handleBuggyAction() {
	        if (edgeData.getActionType().equalsIgnoreCase(
	                EdgeData.CORRECT_ACTION)
	                || edgeData.getActionType().equalsIgnoreCase(
	                        EdgeData.FIREABLE_BUGGY_ACTION)) {
	            if (trace.getDebugCode("mps")) trace.out("mps", "updating it here???");
	            updateActionTypeFromCorrectToBuggy(EdgeData.BUGGY_ACTION);
	        } else {
	            if (childNode == controller
	                    .getSolutionState().getCurrentNode()) {
	                controller.setCurrentNode(parentNode);
	                controller.sendCommMsgs(
	                        parentNode,
	                        controller.getProblemModel()
	                                .getStartNode());
	            }
	            new EditBuggyMsgPanel(true);
	            edgeData.setActionType(EdgeData.BUGGY_ACTION);
	        }
	    }
	   public void testChangeActionTypeToBuggy(String newActionType){
	    	handleTraverseableToNonTraverseable2(newActionType);
	    }
	   private boolean handleTraverseableToNonTraverseable2(String newActionType){
		   	//EdgeRewiredEvent throwMe;
	    	edgeData.setActionType(newActionType);
	    	if(problemEdge.isPreferredEdge()){
	    		resetPreferredPath(false);
	    		problemEdge.getEdgeData().setPreferredEdge(false);
	    	}
	    	if(childNode.getConnectingEdges().size() == 1){
	    		ProblemModelEvent e = new EdgeUpdatedEvent(this, problemEdge,  true);
	    		if(controller.getCurrentNode() == childNode)
	    			e.setTryToSetCurrentStateTo(parentNode.getUniqueID());
	    		
	    		controller.getProblemModel().fireProblemModelEvent(e);
	    		return true;
	    	}
	    	List<ProblemModelEvent> subEvents = new ArrayList<ProblemModelEvent>();
	    	ProblemNode newDestNode = controller.createProblemNode(parentNode, problemEdge.getEdgeData().getSelection(), parentNode.getOutDegree()-1);
	    	//subEvents.add(new NodeCreatedEvent(this, newDestNode));
	    	
	    	controller.deleteSingleEdge(problemEdge, false);//don't fire event
			ProblemEdge replacementEdge = controller.getProblemModel().getProblemGraph().addEdge(
							problemEdge.getSource(), newDestNode, problemEdge.getEdgeData());
			replacementEdge.addEdgeLabels();
			//replacementEdge.getEdgeData().setActionType(newActionType);
	        replacementEdge.getEdgeData().getActionLabel().update();
			EdgeCreatedEvent edgeCreatedEvent = new EdgeCreatedEvent(this, replacementEdge);
			edgeCreatedEvent.setEdgeBeingRewired(true);
	        //subEvents.add(edgeCreatedEvent);
			EdgeDeletedEvent edgeDeletedEvent = new EdgeDeletedEvent(problemEdge);
			edgeDeletedEvent.setEdgeBeingRewired(true);
			if(controller.getCurrentNode() == childNode){
				edgeDeletedEvent.setTryToSetCurrentStateTo(parentNode.getUniqueID());
		    }
			EdgeRewiredEvent ere = new EdgeRewiredEvent(this, edgeDeletedEvent, edgeCreatedEvent);
			subEvents.add(ere);
			NodeCreatedEvent fireMe = new NodeCreatedEvent(this, newDestNode, subEvents);
			controller.getProblemModel().fireProblemModelEvent(fireMe);
	    	return true;
		
	    }
	   
	   public void processCopyLink() {
		   EdgeData.setCopyData(this.edgeData);
	   }
	   
	   public void processPasteLink() {
		   //EdgeData oldData = this.edgeData.cloneEdgeData();
		   EdgeData pasteData = EdgeData.getCopyData();

	        this.edgeData.setActor(pasteData.getActor());
	        this.edgeData.setSelection(pasteData.copyVector(pasteData.getSelection()));
	        this.edgeData.setAction(pasteData.copyVector(pasteData.getAction()));
	        this.edgeData.setInput(pasteData.copyVector(pasteData.getInput()));
	        this.edgeData.setDemoMsgObj(pasteData.getDemoMsgObj().copy());
	        this.edgeData.setActionType(pasteData.getActionType());
	        this.edgeData.setHints(pasteData.copyVector(pasteData.getAllHints()));
	        this.edgeData.setBuggyMsg(pasteData.getBuggyMsg());
	        this.edgeData.setSuccessMsg(pasteData.getSuccessMsg());
	        this.edgeData.setPreferredEdge(pasteData.isPreferredEdge());
	        this.edgeData.setOldActionType(pasteData.getOldActionType());
	        this.edgeData.setMatcher((Matcher) pasteData.getMatcher().clone());
	        this.edgeData.setMinTraversalsStr(pasteData.getMinTraversalsStr());
	        this.edgeData.setMaxTraversalsStr(pasteData.getMaxTraversalsStr());

	        controller.getProblemModel().fireProblemModelEvent(new EdgeColorEvent(this,
	        		edgeData.getEdge(), pasteData.getDefaultColor()));

	        this.edgeData.getPreLispCheckLabel().resetCheckedStatus(
	        		pasteData.getPreLispCheckLabel() == null ?
	        		EdgeData.NOTAPPLICABLE :
	        		pasteData.getPreLispCheckLabel().getCheckedStatus());

	        this.edgeData.getActionLabel().update();
	        this.problemEdge.getActionLabel().repaint();
		   
	   }
	   
	   public void processPasteSpecialLink() {
		   EdgeData copyData = EdgeData.getCopyData();

        	PasteSpecialDialog psd = new PasteSpecialDialog(this.controller);
        	EnumMap<EdgeAttribute,Boolean> selectMap = psd.getSelectedAttributes();
        	if(selectMap == null) return;
        	EnumMap<EdgeAttribute,Object> valueMap = new EnumMap<EdgeAttribute,
        			Object>(EdgeAttribute.class);
        	for(EdgeAttribute ea : selectMap.keySet()) {
        		if(ea.equals(EdgeAttribute.HINT_MESSAGE)) {
        			valueMap.put(ea, copyData.getHints());
        		}
        		else if(ea.equals(EdgeAttribute.SUCCESS_MESSAGE)) {
        			valueMap.put(ea, copyData.getSuccessMsg());
        		}
        		else if(ea.equals(EdgeAttribute.BUG_MESSAGE)) {
        			valueMap.put(ea, copyData.getBuggyMsg());
        		}
        		else if(ea.equals(EdgeAttribute.MATCHERS)) {
        			valueMap.put(ea, copyData.getMatcher());
        		}
        		else if(ea.equals(EdgeAttribute.ACTION_TYPE)) {
        			valueMap.put(ea, copyData.getActionType());
        		}
        		else if(ea.equals(EdgeAttribute.ACTOR)) {
        			valueMap.put(ea, copyData.getActor());
        		}
        		else if(ea.equals(EdgeAttribute.MIN_TRAVERSALS)) {
        			valueMap.put(ea, copyData.getMinTraversals());
        		}
        		else if(ea.equals(EdgeAttribute.SKILLS)) {
        			Vector<String> skills = (Vector<String>)(copyData.getRuleNames().clone());
        			valueMap.put(ea, skills);
        		}
        		else if(ea.equals(EdgeAttribute.MAX_TRAVERSALS)) {
        			valueMap.put(ea, copyData.getMaxTraversals());
        		}
        	}
        	psd.applyDialogSelection(this.problemEdge,valueMap, false); // true?
	        this.edgeData.getActionLabel().update();
	   }
	   

	   
	   public void processClipboardCopyLink() {
		   this.controller.getProblemModelManager().copySelectedLinks();
	   }
	   

	   
	   public void processClipboardPasteLink() {
		   this.controller.getProblemModelManager().pasteLinks(); // CHANGE THIS? used to pass copy node
	   }
}
