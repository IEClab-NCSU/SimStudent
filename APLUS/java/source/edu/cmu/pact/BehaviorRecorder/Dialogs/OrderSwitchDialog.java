package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.DemonstrateModeMessageHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.view.ViewUtils;

public class OrderSwitchDialog extends JDialog implements ActionListener
{
	private BRPanel brFrame;
	private ProblemNode checkedNode;
	private Vector selection;
	private Vector action;
	private Vector input;
	private MessageObject CommMsg;
	private String authorIntent;
	
	JTextArea displayJTextArea;
	
	private JPanel contentPanel = new JPanel();	
	private JPanel optionPanel = new JPanel();
	private JPanel okPanel = new JPanel();
	
	private ButtonGroup group = new ButtonGroup();    	
	private JRadioButton[] buttons = {new JRadioButton(),
										new JRadioButton("Create New State.")};
										
	//private JCheckBox removeAllCurrentRulesCheckBox = new JCheckBox("Do not show me this dialog again.\nAlways treat as the same.");
	private JCheckBox checkOption = new JCheckBox("Always keep my choice.");
	
	private JButton okButton = new JButton("OK");
    private BR_Controller controller;

    /** Demonstrate mode processor that sent us here. */
    private DemonstrateModeMessageHandler demonstrateModeMessageHandler;

	
	public OrderSwitchDialog (	BR_Controller controller,
								DemonstrateModeMessageHandler demonstrateModeMessageHandler,
								ProblemNode checkedNodeP,
								Vector selectionP,
								Vector actionP,
								Vector inputP,
								MessageObject CommMsgP,
								String authorIntentP)
	{
		super(controller.getActiveWindow(), "Merge states?", true);
		
        this.controller = controller;
        this.demonstrateModeMessageHandler = demonstrateModeMessageHandler;
		checkedNode = checkedNodeP;
		selection = selectionP;
		action = actionP;
		input = inputP;
		CommMsg = CommMsgP;
		authorIntent = authorIntentP;
		
        setLocationRelativeTo(null);
    	setSize(300, 300);
    	setResizable(false);
    	
    	contentPanel.setLayout(new BorderLayout());
    	ViewUtils.setStandardBorder(contentPanel);
    	
    	NodeView Vertex;
    	Vertex = checkedNode.getNodeView();
    	
    	NodeView currVertex;
    	currVertex = controller.getSolutionState().getCurrentNode().getNodeView();
    	
    	buttons[0].setText("Link to \"" + Vertex.getText() + "\"");
    						
    	String displayText = "The current state is the same as \"" + Vertex.getText() +
    						"\". You could link \"" + currVertex.getText() + "\" to " +
    						"\"" + Vertex.getText() + "\". Or you could create a new state " +
    						"if you think that students would act differently later on, " +
    						"depending on how they got to the current state.";
    	
    	displayJTextArea = new JTextArea(displayText);				
    	displayJTextArea.setOpaque(false);
        contentPanel.add(displayJTextArea, BorderLayout.NORTH);
    	displayJTextArea.setEditable(false);
    	displayJTextArea.setLineWrap(true);
    	displayJTextArea.setWrapStyleWord(true);
    	optionPanel.setLayout(new GridLayout(3, 1));
    	
		for (int i=0; i<2; i++)
		{
			optionPanel.add(buttons[i]);
			group.add(buttons[i]);
		}
		buttons[0].setSelected(true);
		
		optionPanel.add(checkOption);
		
		contentPanel.add(optionPanel, BorderLayout.CENTER);
		
		okPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okPanel.add(okButton);
		
		contentPanel.add(okPanel, BorderLayout.SOUTH);
		getContentPane().add(contentPanel);
		
		okButton.addActionListener(this);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	// treat as KEEP_SAME 
            	//ese_Frame.treatAsSameStates(checkedNode, selection, action, input, CommMsg);
            	// treat as create a new state
            	ProblemNode newNode = OrderSwitchDialog.this.controller.addNewState (OrderSwitchDialog.this.controller.getSolutionState().getCurrentNode(), selection, action, input, CommMsg, authorIntent, false);
				
				if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION) || authorIntent.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
                    OrderSwitchDialog.this.controller.setCurrentNode(newNode);
                OrderSwitchDialog.this.controller.setAllowCurrentStateChange(true);	
				brFrame.repaint();
		        
            	setVisible(false);
    			dispose();
            }
    	});
 	
    	setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if (buttons[0].isSelected())
		{

                controller.treatAsSameStates(checkedNode, selection, action, input, CommMsg, authorIntent);
		}
		else
		{
			ProblemNode newNode = controller.addNewState (controller.getSolutionState().getCurrentNode(),
					selection, action, input, CommMsg, authorIntent, false);
			
			if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION) || authorIntent.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION)) {
				if (demonstrateModeMessageHandler != null)
					demonstrateModeMessageHandler.tryTrace(selection, action, input);
				controller.setCurrentNode(newNode);
			}
		}

		controller.setAllowCurrentStateChange(true);
                
		setVisible(false);
		dispose();
	}
}
