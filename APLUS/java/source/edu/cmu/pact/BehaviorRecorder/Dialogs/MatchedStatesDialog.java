package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.DemonstrateModeMessageHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.view.ViewUtils;

public class MatchedStatesDialog extends JDialog implements ActionListener {

    private final Vector matchedNodes;
    private Vector selection;
    private Vector action;
    private Vector input;

    private MessageObject CommMsg;

    private String actionType;

    JTextArea displayJTextArea;

    private JPanel contentPanel = new JPanel();
    private JPanel optionPanel = new JPanel();
    private JPanel okPanel = new JPanel();
    private JLabel optionJLabel = new JLabel("Please make your selection:");
    private JComboBox optionJComboBox;
    private JCheckBox checkOption = new JCheckBox("Always keep my choice.");
    private JButton okButton = new JButton("OK");

    private final BR_Controller controller;
    
    /** Demonstrate mode processor that sent us here. */
    private final DemonstrateModeMessageHandler demonstrateModeMessageHandler;

    public MatchedStatesDialog(BR_Controller controller,
			DemonstrateModeMessageHandler demonstrateModeMessageHandler,
			Vector matchedNodesP,
            Vector selectionP, Vector actionP, Vector inputP,
            MessageObject CommMsgP, String authorIntentP) {
        super(controller.getActiveWindow(), "Matched States", true);
        this.controller = controller;
        this.demonstrateModeMessageHandler = demonstrateModeMessageHandler;
        matchedNodes = (Vector) matchedNodesP.clone();
        selection = selectionP;
        action = actionP;
        input = inputP;
        CommMsg = CommMsgP;
        actionType = authorIntentP;

        setLocationRelativeTo(null);
        setSize(300, 300);
        setResizable(false);

        contentPanel.setLayout(new BorderLayout());
        ViewUtils.setStandardBorder(contentPanel);

        int sizeOfMatchedNodes = matchedNodes.size();
        String[] optionList = new String[sizeOfMatchedNodes + 1];

        optionList[0] = "Create a New State";
        ProblemNode tempNode;
        NodeView Vertex;
        for (int i = 0; i < sizeOfMatchedNodes; i++) {
            tempNode = (ProblemNode) matchedNodes.elementAt(i);
            Vertex = tempNode.getNodeView();
            optionList[i + 1] = "Link to \"" + Vertex.getText() + "\"";
        }

        optionJComboBox = new JComboBox(optionList);

        String displayText = "This state is the same as " + sizeOfMatchedNodes
                + " existing states. "
                + "You could link to one of them as the same state. "
                + "Or you could create a new state "
                + "if you think that students would act differently later on, "
                + "depending on how they got to the current state.";

        displayJTextArea = new JTextArea(displayText);
        contentPanel.add(displayJTextArea, BorderLayout.NORTH);
        displayJTextArea.setEditable(false);
        displayJTextArea.setOpaque(false);
        displayJTextArea.setLineWrap(true);
        displayJTextArea.setWrapStyleWord(true);

        optionPanel.setLayout(new GridLayout(3, 1));
        optionPanel.add(optionJLabel);
        optionPanel.add(optionJComboBox);
        optionPanel.add(checkOption);

        contentPanel.add(optionPanel, BorderLayout.CENTER);

        okPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        okPanel.add(okButton);

        contentPanel.add(okPanel, BorderLayout.SOUTH);
        getContentPane().add(contentPanel);
        
        okButton.addActionListener(this);

        final BR_Controller controller1 = controller;
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                // treat as create a new state
            	ProblemNode currentNode = (controller1.getExampleTracer() != null ?
            			controller1.getExampleTracer().getCurrentNode(true) :
            				controller1.getSolutionState().getCurrentNode());
                ProblemNode newNode =
                	controller1.addNewState(currentNode,
                			selection, action, input, CommMsg, actionType);

                if (actionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION)
                        || actionType
                                .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
                    controller1.setCurrentNode(newNode);
                controller1.setAllowCurrentStateChange(true);
                setVisible(false);
                dispose();
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        int selectedIndex = optionJComboBox.getSelectedIndex();

        if (selectedIndex == 0) {
        	ProblemNode currentNode = (controller.getExampleTracer() != null ?
        			controller.getExampleTracer().getCurrentNode(true) :
        				controller.getSolutionState().getCurrentNode());
            ProblemNode newNode = controller.addNewState(currentNode,
                    selection, action, input, CommMsg, actionType);

            if (actionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION)
                    || actionType
                            .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION)) {
				if (demonstrateModeMessageHandler != null)
					demonstrateModeMessageHandler.tryTrace(selection, action, input);
                controller.setCurrentNode(newNode);
            }
            controller.setAllowCurrentStateChange(true);

            setVisible(false);
            dispose();
        } else if (selectedIndex > 0) {
            ProblemNode checkedNode = (ProblemNode) matchedNodes
                    .elementAt(selectedIndex - 1);
            controller.treatAsSameStates(checkedNode, selection, action, input,
                    CommMsg, actionType);

            controller.setAllowCurrentStateChange(true);
            setVisible(false);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please make your option selection.", "",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

}
