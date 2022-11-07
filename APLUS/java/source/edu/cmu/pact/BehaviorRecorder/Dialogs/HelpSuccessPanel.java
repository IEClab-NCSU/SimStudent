package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.trace;



/////////////////////////////////////////////////////////////////////////////////////////////////
/**

*/
/////////////////////////////////////////////////////////////////////////////////////////////////
// create help and success msgs panel
public class HelpSuccessPanel extends JDialog implements ActionListener
{	
    JScrollPane helpScrollPanel;
    JPanel moreOptionsPanel = new JPanel();
    JPanel helpPanel = new JPanel();
    JPanel southPanel = new JPanel();
    JPanel optionPanel = new JPanel();
    JPanel okCancelPanel = new JPanel();


    JLabel successJLabel = new JLabel("Please Edit Success Message:");
    JTextArea successArea = new JTextArea();
    { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(successArea); }

    int numberHelps = 0;
    int numberHelpsInputArea = 3;
    JTextArea [] helpArea;
    JLabel [] helpJLabel;

    final int helpPaneHeight = 550;
    final int helpPaneWidth = 500;

    JCheckBox checkOption1 = new JCheckBox("Copy to all links with the same Selection/Action/Input.");
    JCheckBox checkOption2 = new JCheckBox("Copy to all links with the same Prodution Rule."); 
    JCheckBox checkOption3 = new JCheckBox("Copy hints to the Prodution Rule.");

    JLabel edgesJLabel = new JLabel(" Copy hints from the following link:");
    JComboBox edgesJComboBox = new JComboBox();
    Vector myEdgesWithHints = new Vector();
    
    JLabel rulesJLabel = new JLabel(" Copy hints from the following rule:");
    JComboBox rulesJComboBox = new JComboBox();
    Vector<RuleProduction> rulesWithHints = new Vector<RuleProduction>();
    
    JButton doneJButton = new JButton("Done");
    JButton cancelJButton = new JButton("Cancel");
    JButton moreJButton = new JButton("Add Hint Level");
    JButton clearJButton = new JButton("Clear Hints");
    JButton showMoreOptionsButton = new JButton	("More Options");
    boolean clearFlag = false;

    String ruleLabelText;

    Vector stateEdges;

    String title = "Edit Hint and Success Messages";
    EdgeData edgeData;
    ProblemEdge problemEdge;

    JPanel showOptionsPanel;
    boolean showingOptions;

    String defaultHint = "";
    private BR_Controller controller;

    /** Edge information before edits, for {@link #checkpoint}. */
    private final String serializedBeforeEdit;
    
    /** True if this edit is part of a larger operation, which is checkpointed as a whole. */
    private boolean partOfLargerEdit = false;

    public HelpSuccessPanel(BR_Controller controller, EdgeData edgeData,
    		boolean partOfLargerEdit) {
        super(controller.getActiveWindow(), "", true);
        this.partOfLargerEdit = partOfLargerEdit;
        
      //Save BRD serialization at start to compare for change upon close (for undo)
        serializedBeforeEdit = edgeData.getEdge().toXMLString();
        
        
        
        this.controller = controller;
        setResizable (false);
        this.edgeData = edgeData; 
        defaultHint = edgeData.formDefaultHint();
        
        problemEdge = edgeData.getEdge();

        NodeView tempVertex = problemEdge.getNodes()[ProblemEdge.SOURCE].getNodeView();
        title = title + " from " + tempVertex.getText();

        tempVertex = problemEdge.getNodes()[ProblemEdge.DEST].getNodeView();
        title = title + " to " + tempVertex.getText();
        this.setTitle(title);

        setLocation(new java.awt.Point(300,100));
        setSize(helpPaneWidth, helpPaneHeight);

        moreOptionsPanel.setLayout(new BorderLayout());
        moreOptionsPanel.add(successJLabel,BorderLayout.NORTH);
        successArea = new JTextArea(3, 30);
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(successArea); }
        successArea.setLineWrap(true);
        successArea.setWrapStyleWord(true);
        successArea.setText(edgeData.getSuccessMsg());
        moreOptionsPanel.add(successArea,BorderLayout.CENTER);

        numberHelps = edgeData.getAllHints().size();
        //trace.out(5, this, "this edge has " + numberHelps + " hints"); 
        if (numberHelps > numberHelpsInputArea)
        	numberHelpsInputArea = numberHelps;

        sethelpPanel(edgeData.getActionLabel());

        helpScrollPanel = new JScrollPane(helpPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(helpScrollPanel, BorderLayout.CENTER);

        JPanel clearMorePanel = new JPanel();
        clearMorePanel.add(clearJButton );
        clearMorePanel.add(moreJButton );

        centerPanel.add (clearMorePanel, BorderLayout.SOUTH);

        if (numberHelpsInputArea > 3) {
            Rectangle rect = new Rectangle(helpPanel.getSize().width -10, helpPanel.getSize().height - 10, 5, 20);
            JViewport jViewport = helpScrollPanel.getViewport();
            jViewport.scrollRectToVisible(rect);
        }

        JPanel checkOptionsPanel = new JPanel(new GridLayout(3, 1));
        checkOptionsPanel.add(checkOption1);
        checkOptionsPanel.add(checkOption2);
        checkOptionsPanel.add(checkOption3);
        
        moreOptionsPanel.add (checkOptionsPanel, BorderLayout.SOUTH);

        // for checkOption2 we need check that the current edge has Production Set defined
        boolean checkFlag = false;
        boolean checkOption3Flag = false;

        EdgeData thisMyEdge = problemEdge.getEdgeData();
        int thisNumberOfRules = thisMyEdge.getRuleLabels().size();
        RuleLabel thisRuleLabel;

        for (int i=0; i<thisNumberOfRules; i++) {
            thisRuleLabel = (RuleLabel) thisMyEdge.getRuleLabels().elementAt(i);
            if (thisRuleLabel.isNameSet()) {
                ruleLabelText = thisRuleLabel.getText();
                //trace.out(5, this, "ruleLabelText: " + ruleLabelText);
                checkOption2.setText("Copy to all links with Prodution Rule: " + ruleLabelText);
                checkOption3.setText("Copy hints to the Prodution Rule: " + ruleLabelText);

                RuleProduction tempESE_RuleProduction;

                tempESE_RuleProduction = controller.getRuleProduction(ruleLabelText);

                if (tempESE_RuleProduction != null) {
                    checkOption3Flag = true;
                    if (tempESE_RuleProduction.getHints().size() == 0) 
                        checkOption3.setSelected(true);
                    else
                        checkOption3.setSelected(false);
                } else
                    checkOption3Flag = false;

                checkFlag = true;
                break;
            }
        }

        checkOption2.setEnabled(checkFlag);
        checkOption3.setEnabled(checkFlag && checkOption3Flag);

        optionPanel.setLayout(new GridLayout(4, 1));
        
        optionPanel.add(edgesJLabel);

        // process copy from the selected edge with hints
        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        int      lastItem = 0;
        NodeView tempParentVertex, tempChildVertex;

        Enumeration iter = controller.getProblemModel().getProblemGraph().edges();
        stateEdges = new Vector();
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge)iter.nextElement();
            if (problemEdge != tempEdge) {
                tempMyEdge = tempEdge.getEdgeData(); 
                if (tempMyEdge.getAllHints().size() > 1 ||
                    (tempMyEdge.getAllHints().size() == 1 && tempMyEdge.haveNoneDefaultHint())) {
                    
                    myEdgesWithHints.addElement(tempMyEdge);
                    tempParentVertex = tempEdge.getNodes()[ProblemEdge.SOURCE].getNodeView();
                    tempChildVertex = tempEdge.getNodes()[ProblemEdge.DEST].getNodeView();	
                    edgesJComboBox.addItem(tempParentVertex.getText() + " to " + tempChildVertex.getText());
                    lastItem++;
                }
            }
        }

        if (((DefaultComboBoxModel) edgesJComboBox.getModel()).getSize() == 0)
            edgesJComboBox.addItem("None");
        else
        	edgesJComboBox.addItem("Select one");
        edgesJComboBox.setSelectedIndex(lastItem);
        optionPanel.add(edgesJComboBox);
        edgesJComboBox.addActionListener(this);
        
        // process copy from the selected rule with hints
        
        optionPanel.add(rulesJLabel);
        
        for (RuleProduction rp : controller.getRuleProductionCatalog().values()) {
            if (rp.getHints().size() < 1)
            	continue;
            if (trace.getDebugCode("br")) trace.out("br", "rule "+rp.getDisplayName()+" has hints:\n"+rp.dumpHints());
            rulesWithHints.addElement(rp);
            rulesJComboBox.addItem(rp.getDisplayName());
        }
        
        if (((DefaultComboBoxModel) rulesJComboBox.getModel()).getSize() == 0)
            rulesJComboBox.addItem("None");

        optionPanel.add(rulesJComboBox);
        rulesJComboBox.addActionListener(this);
        
        okCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        doneJButton.setSize(cancelJButton.getSize());
        moreJButton.setSize(cancelJButton.getSize());
        clearJButton.setSize(cancelJButton.getSize());

        okCancelPanel.add(doneJButton );
        okCancelPanel.add(cancelJButton );

        southPanel.setLayout(new BorderLayout());

        showOptionsPanel = new JPanel(new FlowLayout());
        showMoreOptionsButton.setSize (showMoreOptionsButton.getPreferredSize());
        showOptionsPanel.add (showMoreOptionsButton);
        showOptionsPanel.add (moreOptionsPanel);
        moreOptionsPanel.setVisible (false);
        moreOptionsPanel.setBorder (BorderFactory.createEmptyBorder (2, 2, 2, 2));

        southPanel.add(showOptionsPanel, BorderLayout.NORTH);

        JPanel anotherPanel = new JPanel(new BorderLayout());

        anotherPanel.add(optionPanel,BorderLayout.NORTH); 
        anotherPanel.add(okCancelPanel,BorderLayout.SOUTH); 

        southPanel.add (anotherPanel, BorderLayout.SOUTH);

        Container c = getContentPane();

        JPanel contentPane = new JPanel ();
        c.add (contentPane);

        contentPane.setLayout(new BorderLayout());

        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(southPanel, BorderLayout.SOUTH);

        contentPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        clearJButton.addActionListener(this);
        moreJButton.addActionListener(this);
        doneJButton.addActionListener(this);
        cancelJButton.addActionListener(this);
        showMoreOptionsButton.addActionListener (this);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                 thisWindowClosing();
            }
        });
        

    }



    /////////////////////////////////////////////////////////////////////////////////
    /**

     */
    /////////////////////////////////////////////////////////////////////////////////
    void sethelpPanel(ActionLabel referActionLabel)
    { 	
        int numberHelpsReferActionLabel = referActionLabel.getEdge().getAllHints().size();
        
        helpArea = new JTextArea[numberHelpsInputArea];
        helpJLabel = new JLabel[numberHelpsInputArea];

        helpPanel.setLayout(new GridLayout(numberHelpsInputArea,1));
        String tempString;
        for (int i=0; i<numberHelpsInputArea; i++) {
            JPanel innerPanel = new JPanel(new BorderLayout());

            tempString = "Please Edit Hint Message " + (i+1) + ":";
            helpJLabel[i] = new JLabel(tempString);
            //helpJLabel[i].setSize(new Dimension(280, 14));
            helpJLabel[i].setBackground(Color.darkGray.darker());
            innerPanel.add (helpJLabel[i], BorderLayout.NORTH);

            helpArea[i] = new JTextArea(3, 30);
            { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(helpArea[i]); }
            helpArea[i].setLineWrap(true);
            helpArea[i].setWrapStyleWord(true);
            helpArea[i].setVisible(true);
            innerPanel.add (helpArea[i], BorderLayout.CENTER);

            helpPanel.add (innerPanel);
        }

        if (!clearFlag) {
            String currentHint = "";
            
            for (int i=0; i<numberHelpsReferActionLabel - 1; i++) {
                // zz 02/09/04: helpArea[i].setText((String) referActionLabel.getAllHints().elementAt(i));
                // only set non default hints
                currentHint = (String) referActionLabel.getEdge().getAllHints().elementAt(i);
                helpArea[i].setText(currentHint);
            }
            
            if (numberHelpsReferActionLabel > 0) {
                // check that the last hint is default hint
                currentHint = (String) referActionLabel.getEdge().getAllHints().elementAt(numberHelpsReferActionLabel - 1);

                if (currentHint.equalsIgnoreCase(defaultHint))
                    helpArea[numberHelpsInputArea - 1].setText(currentHint);
                else 
                    helpArea[numberHelpsReferActionLabel - 1].setText(currentHint);
            }
            //
        } else
            successArea.setText("");

        Rectangle rect = new Rectangle(helpPanel.getSize().width -10, helpPanel.getSize().height - 10, 5, 20);

        helpPanel.scrollRectToVisible(rect);

        clearFlag = false;
    }

    /////////////////////////////////////////////////////////////////////////////////
    /**

    /////////////////////////////////////////////////////////////////////////////////
    public Insets getInsets()
    {
            return new Insets(25,10,10,10);
    }

     */
    /////////////////////////////////////////////////////////////////////////////////
    /**

     */
    /////////////////////////////////////////////////////////////////////////////////
    public void thisWindowClosing() {
    	
        setVisible(false);
        dispose();
        checkpoint();
    }

    /**
     * Create a checkpoint for undo: call this when exiting the dialog.
     */
    private void checkpoint() {

    	if (partOfLargerEdit)  // no-op if whole operation is to be checkpointed later
    		return;
    	
        //Undo checkpoint for Editing Hint/Success Messages ID: 1337
        String serializedAfterEdit = edgeData.getEdge().toXMLString();
        if (!(serializedBeforeEdit.equals(serializedAfterEdit))) {
        	if (trace.getDebugCode("undo"))
				trace.out("undo", "EditStudentInputDialog.close() XML before:\n"+
        				serializedBeforeEdit+"\nXML after:\n"+serializedAfterEdit);
        			
			//Undo checkpoint for Editing Hint/Success Messages ID: 1337
			ActionEvent ae = new ActionEvent(this, 0, "Edit Hint and Success Messages");
			controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
			if (trace.getDebugCode("undo"))
				trace.out("Checkpoint: Change Hint/Success (1)");
        }
	}



	/////////////////////////////////////////////////////////////////////////////////
    /**

     */
    /////////////////////////////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent ae) {
        //JButton selectedButton = (JButton) ae.getSource();
        //trace.out(5, this, "ae.getSource() = " + ae.getSource().toString());
    	//edgeData.seth
        // process copy hints from the selected action edge. 
        if (ae.getSource() == edgesJComboBox) {
            EdgeData selectedMyEdge;

            int slectedEdgeIndex = edgesJComboBox.getSelectedIndex();
            if (slectedEdgeIndex >= 0 && slectedEdgeIndex < edgesJComboBox.getItemCount() - 1) {
                String selectedItem = (String) edgesJComboBox.getSelectedItem();
                if (!selectedItem.equals("None")) {
                    //trace.out(5, this, "selectedItem = " + selectedItem);

                    boolean flag = true;
                    String temtText;

                    for (int i=0; i<numberHelpsInputArea ; i++) {
                        temtText = helpArea[i].getText();
                        if (temtText != null) {
                            temtText = temtText.trim();
                            if(temtText.length() != 0) {
                                flag = false;
                                break;
                            }
                        }
                    }

                    if (flag == false) {
                        Object[] options = { "OK", "Cancel" };
                        String message[] = {"Do you want to replace the current hints with",
                                            "the hints from the edge you just selected?"};

                        int capValue = JOptionPane.showOptionDialog(null, 
                                                                    message, 
                                                                    "Warning", 
                                                                    JOptionPane.DEFAULT_OPTION, 
                                                                    JOptionPane.WARNING_MESSAGE,
                                                                    null, 
                                                                    options, 
                                                                    options[0]);

                        if (capValue == JOptionPane.OK_OPTION)
                            flag = true;
                    }

                    if (flag) {
                        selectedMyEdge = (EdgeData) myEdgesWithHints.elementAt(slectedEdgeIndex);

                        helpPanel.removeAll();

                        numberHelpsInputArea = java.lang.Math.max(3, selectedMyEdge.getAllHints().size());
                        //trace.out(5, this,"selected edge has hints.size() = " + numberHelpsInputArea);

                        sethelpPanel(selectedMyEdge.getActionLabel());  
                    }  				
                } 
            }
        } else if (ae.getSource() == rulesJComboBox) {

            int slectedEdgeIndex = rulesJComboBox.getSelectedIndex();

            if (slectedEdgeIndex >= 0) {
                String selectedItem = (String) rulesJComboBox.getSelectedItem();
                
                if (selectedItem.equals("None"))
                    return;
                
                //trace.out(5, this, "selectedItem = " + selectedItem);

                boolean flag = true;
                String temtText;

                for (int i=0; i<numberHelpsInputArea ; i++) {
                    temtText = helpArea[i].getText();
                    if (temtText != null) {
                        temtText = temtText.trim();
                        if(temtText.length() != 0) {
                            flag = false;
                            break;
                        }
                    }
                }

                if (flag == false) {
                    Object[] options = { "OK", "Cancel" };
                    String message[] = {"Do you want to replace the current hints with",
                                        "the hints from the rule you just selected?"};

                    int capValue = JOptionPane.showOptionDialog(null, 
                                                                message, 
                                                                "Warning", 
                                                                JOptionPane.DEFAULT_OPTION, 
                                                                JOptionPane.WARNING_MESSAGE,
                                                                null, 
                                                                options, 
                                                                options[0]);

                    if (capValue == JOptionPane.OK_OPTION)
                        flag = true;
                }

                if (flag) {
                    ActionLabel referActionLabel = new ActionLabel(this.edgeData, controller.getProblemModel());
           
                    RuleProduction selectedRule = rulesWithHints.elementAt(slectedEdgeIndex);
                    
                    referActionLabel.getEdge().setHints(selectedRule.getHints());
                    
                    helpPanel.removeAll();

                    numberHelpsInputArea = java.lang.Math.max(3, referActionLabel.getEdge().getAllHints().size());

                    sethelpPanel(referActionLabel);
                }  				
            } 
        
        } else if (ae.getSource() == moreJButton) {
            // temp ActionLabel to hold current text in success and hints inputs
			EdgeData referEdgeData = new EdgeData(controller.getProblemModel());
            ActionLabel referActionLabel = referEdgeData.getActionLabel();
           
            updateTextAreas(referActionLabel);

            helpPanel.removeAll();

            numberHelpsInputArea = numberHelpsInputArea + 1;
            
            sethelpPanel(referActionLabel);

            if (numberHelpsInputArea > 3) {
                Rectangle rect = new Rectangle(helpPanel.getSize().width -10, helpPanel.getSize().height - 10, 5, 20);
                JViewport jViewport = helpScrollPanel.getViewport();
                jViewport.scrollRectToVisible(rect);
            }
        } else if (ae.getSource() == clearJButton) {
            Object[] options = { "Yes", "No" };
            int capValue = JOptionPane.showOptionDialog(this, 
                        "<html>Do you want to clear ALL of the hints in this window?</html>", 
                        "Clear Hints", 
                        JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.WARNING_MESSAGE,
                        null, 
                        options, 
                        options[0]);

            if (capValue == JOptionPane.OK_OPTION) {
                helpPanel.removeAll();
                numberHelpsInputArea = 3;
                clearFlag = true;
                sethelpPanel(edgeData.getActionLabel());
            }
        } else if (ae.getSource() == doneJButton){
            updateTextAreas(edgeData.getActionLabel());

            // same Selection/Action/Input 
            if (checkOption1.isSelected() && edgeData.getAllHints().size() > 0)	{ 
                Vector sameValuesStates = controller.getProblemModel().findSameTripleEdges(
                        edgeData.getSelection(), 
                        edgeData.getAction(), 
                        edgeData.getInput());

                copySuccessHintMsgs(edgeData.getActionLabel(), sameValuesStates);
            }

            // same Prodution Rule
            if (checkOption2.isSelected() && edgeData.getAllHints().size() > 0)	{
                Vector sameProductionSetsStates = controller.getProblemModel().findSameProductionSetsEdge(ruleLabelText);
                copySuccessHintMsgs(edgeData.getActionLabel(), sameProductionSetsStates);
            }

            //trace.out(5, this, "actionLabel.hints.size(): " + actionLabel.hints.size());
            Vector<String> myHints = edgeData.getAllHints();
            if (checkOption3.isSelected() && myHints.size() > 0) {
                // attach actionLabel.hints to the 1st defined productionSet-rulelabel

                RuleProduction rp = controller.getRuleProduction(ruleLabelText);

                if (rp != null && !(myHints.equals(rp.getHints()))) {
                    rp.setHints(myHints);
                    if (trace.getDebugCode("undo"))
                    	trace.out("undo", "HelpSuccessPanel copied myHints "+myHints+" to rule "+rp);
                }
            }
            controller.getProblemModel().fireProblemModelEvent(new EdgeUpdatedEvent(this, problemEdge, true));
            setVisible(false);
            dispose();
            checkpoint();

        } else if (ae.getSource() == showMoreOptionsButton) {
            if (showingOptions) {
                moreOptionsPanel.setVisible (false);
                showingOptions = false;
                showMoreOptionsButton.setText ("More Options");
            } else {
                moreOptionsPanel.setVisible (true);
                showingOptions = true;
                showMoreOptionsButton.setText ("Hide Options");
            }
        } else {
            setVisible(false);
            dispose();
            checkpoint();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    /**

     */
    /////////////////////////////////////////////////////////////////////////////////
    void copySuccessHintMsgs(ActionLabel fromactionLabel, Vector toStatesVector) {
        
        if (toStatesVector == null)
            return;
        
        int numberOfStates = toStatesVector.size();

        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        boolean noEdgesChanged = true;
        String tempBeforeEdit = null;
        
        for (int i=0; i<numberOfStates; i++) {
            tempEdge = (ProblemEdge) toStatesVector.elementAt(i);
            tempMyEdge = tempEdge.getEdgeData();
            if (noEdgesChanged)            // continue checking only until 1st change
            	tempBeforeEdit = tempEdge.toXMLString();
            tempMyEdge.setSuccessMsg(fromactionLabel.getEdge().getSuccessMsg());
            tempMyEdge.setHints((Vector) fromactionLabel.getEdge().getAllHints().clone());
            if (noEdgesChanged)
            	noEdgesChanged = (tempBeforeEdit.equals(tempEdge.toXMLString()));
        }
        if (!noEdgesChanged)  {           // some edge changed
        	if (trace.getDebugCode("undo"))
        		trace.out("undo", "HelpSuccessPanel.copySuccessHintMsgs() 1st changed edge was\n"+tempBeforeEdit);
        }

        return;
    }

    /////////////////////////////////////////////////////////////////////////////////
    /**

     */
    /////////////////////////////////////////////////////////////////////////////////
    void updateTextAreas(ActionLabel referActionLabel) {
        String newText;

        // update success msg in referActionLabel
        newText = successArea.getText();
        if (newText != null) {
            newText = newText.trim();
            referActionLabel.getEdge().setSuccessMsg(newText);
        } else
            referActionLabel.getEdge().setSuccessMsg("");

        // update hint msgs in referActionLabel
        Vector newHints = new Vector();
        for (int i=0; i<numberHelpsInputArea ; i++) {
            newText = helpArea[i].getText();
            if (newText != null) {
                newText = newText.trim();
//                if(!newText.equals(""))
                newHints.addElement(newText);
            } else 
                newHints.addElement ("");
        }
        
        logHintChange(referActionLabel.getEdge().getHints(), newHints,
        		referActionLabel.getEdge().getSkills().toString());

    	referActionLabel.getEdge().setHints(newHints);
    }

    /**
     * Log an author action if hints changed.
     * @param oldHints
     * @param newHints
     * @param logArgument arg to log method
     */
	private void logHintChange(Vector oldHints, Vector newHints, String logArgument) {
		try {
			if (oldHints.size() == newHints.size()) {
				int i = 0;
				while (i < oldHints.size() && oldHints.get(i).equals(newHints.get(i)))
					i++;
				if (i >= oldHints.size())
					return;
			}
			StringBuffer oldHintsBuffer = new StringBuffer();
			for(int i = 0; i < oldHints.size(); i++)
				oldHintsBuffer.append("Hint " + (i+1) + ": " + oldHints.get(i) + "\n");
			
			StringBuffer newHintsBuffer = new StringBuffer();
			for(int i = 0; i < newHints.size(); i++)
				newHintsBuffer.append("Hint " + (i+1) + ": " + newHints.get(i) + "\n");
			
			controller.getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
					BR_Controller.EDIT_HINTS, logArgument,
					"Old: " + oldHintsBuffer.toString() + "New: " + newHintsBuffer.toString(), 
			"");
		} catch (Exception e) {         // don't let logging exceptions stop us
			trace.err("Error "+e+" logging hint change");
			e.printStackTrace();
		}
	}
}
