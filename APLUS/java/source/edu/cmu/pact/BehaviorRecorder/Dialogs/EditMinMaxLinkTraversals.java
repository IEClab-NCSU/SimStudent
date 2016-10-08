package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import edu.cmu.pact.BehaviorRecorder.Controller.ActionLabelHandler;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.ctat.view.ViewUtils;
/**
 * This dialog presents an interface to the user for editing the
 * minimum and maximum number of times a link may be traversed.
 * I will not allow the min to be greater than the max, but
 * they can be equal.
 * 
 * @author Eric Schwelm
 *
 */
public class EditMinMaxLinkTraversals extends JDialog implements ActionListener, DocumentListener {
	private JTextField minTextField;
	private JTextField maxTextField;
	private JButton okButton;
	private JButton cancelButton;
	private EdgeData edge;
	private BR_Controller controller;
	public EditMinMaxLinkTraversals(Frame owner, 
			String title, 
			boolean modal, EdgeData edge, BR_Controller controller) {
		
		super(owner, title, modal);
		this.controller = controller;
		this.edge = edge;
		
		JPanel mainVertPanel = new JPanel(); //All components go in this
		mainVertPanel.setLayout(new BoxLayout(mainVertPanel, BoxLayout.Y_AXIS));

		JPanel descPanel = new JPanel();
		descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.X_AXIS));
		ViewUtils.setStandardBorder(descPanel);
	    JLabel displayJLabel = new JLabel("<html>Set the minimum and maximum number of times <br>" +
	    		"that this link can be traversed:</html>");
	    descPanel.add(displayJLabel);
        mainVertPanel.add(descPanel);
        
		JPanel ltfPanel = new JPanel();
		ltfPanel.setLayout(new BoxLayout(ltfPanel, BoxLayout.X_AXIS));
		ViewUtils.setStandardBorder(ltfPanel);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		JPanel tfPanel = new JPanel();
		tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.Y_AXIS));

		Document minDoc = new PlainDocument();
		minTextField = new JTextField(minDoc, edge.getMinTraversalsStr(), 8);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(minTextField); }
        minTextField.setHorizontalAlignment(JTextField.CENTER);
        JLabel minLabel = new JLabel("Minimum Traversals: ");
        minLabel.setHorizontalAlignment(JLabel.LEFT);
		labelPanel.add(minLabel);
		tfPanel.add(minTextField);
		
		Document maxDoc = new PlainDocument();
		maxTextField = new JTextField(maxDoc, edge.getMaxTraversalsStr(), 8);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(maxTextField); }
		maxTextField.setHorizontalAlignment(JTextField.CENTER);
        JLabel maxLabel = new JLabel("Maximum Traversals: ");
        maxLabel.setHorizontalAlignment(JLabel.LEFT);
        labelPanel.add(maxLabel);
		tfPanel.add(maxTextField);

		ltfPanel.add(labelPanel);
		ltfPanel.add(tfPanel);
		mainVertPanel.add(ltfPanel);
		
		JPanel buttonPanel = new JPanel(); //Holds ok and cancel buttons
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		ViewUtils.setStandardBorder(buttonPanel);
		
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		mainVertPanel.add(buttonPanel);
		
		add(mainVertPanel);
		pack();
		setResizable(false);
        setLocationRelativeTo(owner);
		minDoc.addDocumentListener(this);  // delay until after okButton created
		maxDoc.addDocumentListener(this);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==okButton) {

			//Undo checkpoint for Editing Min/Max Traversals ID: 1337
			int minBefore = edge.getMinTraversals();
			int maxBefore = edge.getMaxTraversals();
			
			
			edge.setMinTraversalsStr(minTextField.getText());
			edge.setMaxTraversalsStr(maxTextField.getText());
			
			//Undo checkpoint for Editing Min/Max Traversals ID: 1337
			int minAfter = edge.getMinTraversals();
			int maxAfter = edge.getMaxTraversals();
			boolean saveCheckPoint = (minBefore != minAfter || maxBefore != maxAfter);
			
			controller.getProblemModel().fireProblemModelEvent(new EdgeUpdatedEvent(this, edge.getEdge(), true));
			
			//Undo checkpoint for Editing Min/Max Traversals ID: 1337
			if (saveCheckPoint) {
				ActionEvent ae = new ActionEvent(this, 0, ActionLabelHandler.REUSEABLE_LINKS);
				controller.getUndoPacket().getCheckpointAction().actionPerformed(ae); 
			}
		}
		else if(arg0.getSource()==cancelButton) {			
		}
		this.setVisible(false);
	}
	
	/**
	 * Disable the ok button if minTraversals is greater than max traversals
	 */
	private void stateChanged(DocumentEvent evt) {
		try {
			int min = Integer.parseInt(minTextField.getText());
			int max = Integer.parseInt(maxTextField.getText());
			okButton.setEnabled(min <= max);
		} catch (Exception e) {
			okButton.setEnabled(true);
		}
	}

	public void changedUpdate(DocumentEvent evt) {
		stateChanged(evt);
	}
	public void insertUpdate(DocumentEvent evt) {
		stateChanged(evt);
	}
	public void removeUpdate(DocumentEvent evt) {
		stateChanged(evt);
	}
}
