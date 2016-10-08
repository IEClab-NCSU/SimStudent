package edu.cmu.pact.BehaviorRecorder.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import edu.cmu.pact.BehaviorRecorder.Dialogs.EditSkillNameDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.HelpSuccessPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.trace;

/////////////////////////////////////////////////////////////////////////////////////////////////
/**

*/
/////////////////////////////////////////////////////////////////////////////////////////////////
public class RuleLabelHandler extends MouseInputAdapter implements ActionListener {
	public BRPanel brPanel;
	ProblemEdge problemEdge;
	public EdgeData edgeData;
	public RuleLabel ruleLabel;
	final static String COPY_SKILL_NAME = "Copy Skill Name";
	final static String EDIT_SKILL_NAME = "Edit Skill Name";
	final static String ADD_RULE = "Add New Skill Name to Link";

	final static String DELETE_SKILL_NAME = "Delete Skill Name from Link";
	public BR_Controller controller;
    
    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    }
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public RuleLabelHandler(RuleLabel ruleLabel, ProblemEdge problemEdge, BR_Controller controller) {
        this.controller = controller;
		this.problemEdge = problemEdge;
		this.edgeData = this.problemEdge.getEdgeData();
		this.ruleLabel = ruleLabel;
		ruleLabel.setHandler(this);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public void mouseReleased(MouseEvent e) {
		//evaluatePopup (e);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public void mousePressed(MouseEvent e) {
		evaluatePopup(e, controller, this);
	}

	public void deleteSkillName() {
	
		int result =
			JOptionPane.showConfirmDialog(
				controller.getActiveWindow(),
				"<html>Do you want to delete this skill name from this link?</html>",
				"Delete skill name?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
				
		if (result != JOptionPane.YES_OPTION)
			return;
			
		if (edgeData.getRuleLabels().size() > 1)  {
			edgeData.removeRuleName(ruleLabel.getText());
			edgeData.getRuleLabels().remove(ruleLabel);
			edgeData.updateMovedFromEdgeView();
	//		this.brPanel.getScrollPanel().drawingArea.remove(ruleLabel);
		} else {
			controller.checkAddRuleName("unnamed", "");
			ruleLabel.setText("unnamed");
		}
		// Fri May 27 17:20:50 2005: Noboru
		// SimSt must know about this change....
		// 
		// I wish I could deal this and EDIT_SKILL_NAME action
		// at the same place (possibly ruleLabel.setText(),
		// but the code is so messy and to mutually
		// inter-connected...)
		// 
		// ******************************
		trace.out("ruleLabel deleted...");
		

		//Undo checkpoint for Deleting Skill Names ID: 1337
		ActionEvent ae = new ActionEvent(this, 0, DELETE_SKILL_NAME);
		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
		if (trace.getDebugCode("undo"))
			trace.out("undo", "Checkpoint: Deleting (1)");
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param controller TODO
	 * @param handler TODO
	
	*/
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public static void evaluatePopup(MouseEvent e, BR_Controller controller, RuleLabelHandler handler) {

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setName("ruleLabelPopupMenu");
		JMenuItem menuItem;
                
		menuItem = new JMenuItem(COPY_SKILL_NAME);
                if (controller.getRuleProductionCatalog().size() == 0)
                    menuItem.setEnabled(false);
                else
                    menuItem.addActionListener(handler);
		popupMenu.add(menuItem);

		menuItem = new JMenuItem(EDIT_SKILL_NAME);
                menuItem.setEnabled(true);
		menuItem.addActionListener(handler);
		popupMenu.add(menuItem);

		menuItem = new JMenuItem(ADD_RULE);
		menuItem.addActionListener(handler);
		popupMenu.add(menuItem);

		menuItem = new JMenuItem(DELETE_SKILL_NAME);
		menuItem.addActionListener(handler);
		popupMenu.add(menuItem);

		// CTAT1564: removed obsolete machine-learning menu items
		
		popupMenu.show(e.getComponent(), e.getX(), e.getY());

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e) {

		String action = e.getActionCommand();
		
		if (trace.getDebugCode("undo")) trace.out("undo", "RuleLabelHandler.actionPerformed("+action+")");
		controller.
			getLoggingSupport().
				authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER, 
								action, edgeData.getName(), "", "");
		Vector ruleLabels = edgeData.getRuleLabels();
		
		if (action.equals(EDIT_SKILL_NAME)) {
			new EditSkillNameDialog(this, ruleLabel.getText());
			return;
		}

		if (action.equals (DELETE_SKILL_NAME)) {
			deleteSkillName();
			return;
		}

		if (action.equals(COPY_SKILL_NAME)) {
			List<String> ruleProductions =
					controller.getRuleProductionCatalog().getRuleDisplayNames(true);
			if (ruleProductions.size() < 1) {
				JOptionPane.showMessageDialog(controller.getActiveWindow(),
						"No skills to copy!", "Skills to Copy", JOptionPane.WARNING_MESSAGE);
				return;
			}
			String[] rulesStrings = ruleProductions.toArray(new String[ruleProductions.size()]);
                        
			String s =
				(String) JOptionPane.showInputDialog(
					this.ruleLabel,
					"Please select your ruleName",
					"RuleName Selection Dialog",
					JOptionPane.QUESTION_MESSAGE,
					null,
					rulesStrings,
					rulesStrings[0]);

			if (s == null)
				return;

            RuleProduction selectedRule = controller.getRuleProduction(s);
            if (selectedRule == null) {
            	trace.err("Copy Skill Name: rule \"+s+\" not found by controller.getRuleProduction()");
            	return;
            }
            String originalRule = ruleLabel.getText();
            ruleLabel.setText(selectedRule.getDisplayName());
            edgeData.replaceRuleName(originalRule, selectedRule.getDisplayName());
      
			if (selectedRule.getHints().size() > 0) {
				String message1, message2;
				message1 = "The rule [" + s + "] has hints.";
				if (edgeData.getHints().size() == 0)
					message2 = "Do you want to copy the rule hints to the link hints?";
				else
					message2 = "Do you want to override the link hints with the rule hints?";
				String message[] = { message1, message2 };
				int result =
					JOptionPane.showConfirmDialog(
							this.ruleLabel,
							message,
							"",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);

				if (result == JOptionPane.YES_OPTION) {
					edgeData.setHints(selectedRule.getHints());
					new HelpSuccessPanel(controller, edgeData, true);
				}
			}
			ruleLabel.repaint();  // force rewrite of text in JLabel

			//Undo checkpoint for Adding Skill Names ID: 1337
			ActionEvent ae = new ActionEvent(this, 0, COPY_SKILL_NAME);
			controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
			if (trace.getDebugCode("undo"))
				trace.out("undo", "Checkpoint: "+COPY_SKILL_NAME);

			return;
		}

		if (action.equals(ADD_RULE)) {

			Object[] options = { "Before", "After", "Cancel"};

			int selectedValue =
				JOptionPane.showOptionDialog(
					controller.getActiveWindow(),
					"Would you like to add a skill name before or after the skill name you selected?",
					"Add New Skill Name",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null,
					options,
					options[0]);

			// Fri May 27 16:08:03 2005: Bug fix: Noboru
			// Do nothing when "CANCELED"
			if ( selectedValue != 2 ) {
	
			    // System.out.println("???");
			    RuleLabel newRuleLabel = new RuleLabel("r" + ruleLabels.size(), controller);
			    controller.checkAddRuleName(newRuleLabel.getText(), "");
			    
			    int currLabelIndex = ruleLabels.indexOf(this.ruleLabel);
			    
			    if (selectedValue == 0)
				ruleLabels.insertElementAt(newRuleLabel, currLabelIndex);
			    else if (selectedValue == 1)
				ruleLabels.insertElementAt(newRuleLabel, currLabelIndex + 1);
			    
	//		    this.brPanel.getScrollPanel().drawingArea.add(newRuleLabel);
			    newRuleLabel.addMouseListener(new RuleLabelHandler(newRuleLabel, this.problemEdge, controller));
			    
			    problemEdge.getEdgeData().updateMovedFromEdgeView();
			    
			    
    			//Undo checkpoint for Adding Skill Names ID: 1337
				ActionEvent ae = new ActionEvent(this, 0, ADD_RULE);
				controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
				if (trace.getDebugCode("undo"))
					trace.out("undo", "Checkpoint: "+ADD_RULE);
			}
			return;
		}
	}
    
    
    

    // -sanket
    /**
     * get all the examples from the current graph and also the list of
     * selection action inputs for each example in the graph from the start
     * state
     * 
     * @param selectionList -
     *            list of list of selection s from start node to each example
     *            edge
     * @param actionList -
     *            a list of list of action
     * @param inputList -
     *            list of list of inputs
     * @param edgeList -
     *            list of edges for each example in the graph
     * @param ruleName -
     *            name of the rule of which multiple instances are to be found
     * @param associatedElementsList -
     *            list of associated elements for each arc
     * @param associatedElementsValuesList -
     *            list of values of the associated elements for each arc
     * @param negativeExamplesList -
     *            list of negative examples elements
     * @param negativeExamplesValuesList -
     *            list of negative examples values
     */
    public void getAllExamplesSAI(Vector selectionList, Vector actionList,
            Vector inputList, Vector ruleNamesList, Vector edgeList,
            RuleLabel ruleName, Vector associatedElementsList,
            Vector associatedElementsValuesList, Vector negativeExamplesList,
            Vector negativeExamplesValuesList) {
        // trace.out(5,this,"inside getAllsetExamples...");
        getAllEdges(controller.getProblemModel().getStartNode(), edgeList, ruleName);
        getSAIForAllEdges(edgeList, selectionList, actionList, inputList,
                ruleNamesList);
        int size = edgeList.size();
        EdgeData myEdge;
        for (int i = 0; i < size; i++) {
            myEdge = ((ProblemEdge) edgeList.get(i)).getEdgeData();
            if (myEdge.getActionType().equals(
                    EdgeData.CORRECT_ACTION)) {
                associatedElementsList.add(myEdge
                        .getAssociatedElements());
                associatedElementsValuesList.add(myEdge
                        .getAssociatedElementsValues());
            } else {
                negativeExamplesList.add(myEdge
                        .getAssociatedElements());
                negativeExamplesValuesList.add(myEdge
                        .getAssociatedElementsValues());
            }
        }
    }

    // -sanket
    /**
     * this method is used to find the list of selection action inputs from the
     * start state to all the edges in the edgeList
     * 
     * @param ruleNamesList
     *            is a Vector of Vector of Vector of rule names of each of the
     *            examples from the start state
     */
    public void getSAIForAllEdges(Vector edgeList, Vector selectionList,
            Vector actionList, Vector inputList, Vector ruleNamesList) {
        // //trace.out(5,this,"inside getSAIForAllEdges...");
        Enumeration enumeration = edgeList.elements();
        ProblemEdge edge;
        Vector selection, action, input, ruleNames;
        while (enumeration.hasMoreElements()) {
            selection = new Vector();
            action = new Vector();
            input = new Vector();
            ruleNames = new Vector();

            edge = (ProblemEdge) enumeration.nextElement();
            getSAIForEdge(controller.getProblemModel().getStartNode(), selection, action,
                    input, ruleNames, edge);
            selectionList.addElement(selection);
            actionList.addElement(action);
            inputList.addElement(input);
            ruleNamesList.addElement(ruleNames);
        }
    }

    // -sanket
    /**
     * get the selection action input list from the start state to the given
     * edge
     * 
     * @param ruleNames
     *            is a vector of vector of ruleNames from the start state to the
     *            current edge
     */
    public int getSAIForEdge(ProblemNode atNode, Vector selectionList,
            Vector actionList, Vector inputList, Vector ruleNames,
            ProblemEdge edge) {
        // //trace.out(5, this, "Inside getSAIForEdge...");
        ProblemEdge edgeTemp;
        ProblemNode childTemp;
        // Vector selectionListC,actionListC,inputListC,ruleNamesC;

        Enumeration iterOutEdge = controller.getProblemModel().getProblemGraph().getOutgoingEdges(
                atNode);

        while (iterOutEdge.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iterOutEdge.nextElement();
            if (edgeTemp.equals(edge)) {
                EdgeData myCurrEdge = edgeTemp.getEdgeData();
                selectionList.add(0, myCurrEdge.getSelection());
                actionList.add(0, myCurrEdge.getAction());
                inputList.add(0, myCurrEdge.getInput());

                Vector rules = new Vector();
                RuleLabel tempLabel;
                for (int i = 0; i < myCurrEdge.getRuleLabels().size(); i++) {
                    tempLabel = (RuleLabel) myCurrEdge.getRuleLabels().elementAt(i);
                    rules.addElement(tempLabel.getText());
                }
                ruleNames.add(0, rules);
                return -1;
            }
            childTemp = edgeTemp.getNodes()[ProblemEdge.DEST];
            int i = getSAIForEdge(childTemp, selectionList, actionList,
                    inputList, ruleNames, edge);
            if (i == -1) {
                EdgeData myCurrEdge = edgeTemp.getEdgeData();
                selectionList.add(0, myCurrEdge.getSelection());
                actionList.add(0, myCurrEdge.getAction());
                inputList.add(0, myCurrEdge.getInput());

                Vector rules = new Vector();
                RuleLabel tempLabel;
                for (int j = 0; j < myCurrEdge.getRuleLabels().size(); j++) {
                    tempLabel = (RuleLabel) myCurrEdge.getRuleLabels().elementAt(j);
                    rules.addElement(tempLabel.getText());
                }
                ruleNames.add(0, rules);
                return -1;
            }
        }
        return 0;
    }

    // -sanket
    // function for finding all the examples where a particular rule is used
    // the function makes recursive call to itself
    public void getAllEdges(ProblemNode atNode, Vector edgeList,
            RuleLabel ruleName) {

        ProblemEdge edgeTemp;
        ProblemNode childTemp;

        Enumeration iterOutEdge = controller.getProblemModel().getProblemGraph().getOutgoingEdges(
                atNode);
        String str, str1;
        while (iterOutEdge.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iterOutEdge.nextElement();
            childTemp = edgeTemp.getNodes()[ProblemEdge.DEST];

            EdgeData myCurrEdge = edgeTemp.getEdgeData();
            RuleLabel tempLabel;
            str1 = ruleName.getText();
            str1 = str1.replaceAll("\\s+", " ");

            for (int i = 0; i < myCurrEdge.getRuleLabels().size(); i++) {
                tempLabel = (RuleLabel) myCurrEdge.getRuleLabels().elementAt(i);
                str = tempLabel.getText();
                str = str.replaceAll("\\s+", " ");

                if (str.equals(str1) && !ProblemModel.containsEdge(edgeList, edgeTemp)) {
                    edgeList.add(edgeTemp);
                    break;
                }
            }

            getAllEdges(childTemp, edgeList, ruleName);
        }
        return;
    }



    public ProblemEdge getProblemEdge() {
    	return problemEdge;
    }
	
}
