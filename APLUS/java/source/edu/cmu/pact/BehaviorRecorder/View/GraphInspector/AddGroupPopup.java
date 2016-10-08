package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;

public class AddGroupPopup extends JDialog {
	private static final long serialVersionUID = 3597912111649564727L;
	GroupModel groupModel;
	GroupEditorContext context;
	GroupNameEditor groupNameEditor;
	JRadioButton ordered;
	
	final BR_Controller controller;
	
	
	public AddGroupPopup(Frame mainWindow, String title, boolean modal, GroupEditorContext editContext, Point loc,
			BR_Controller ctrl) 
	{
		super(mainWindow, title, modal);
		controller = ctrl;
		
		groupModel = editContext.getGroupModel();
		context = editContext;
		
		JPanel innerPanel1 = new JPanel(); //All components go in this
		innerPanel1.setLayout(new BoxLayout(innerPanel1, BoxLayout.Y_AXIS));
		
		innerPanel1.add(new JLabel("Enter Group Name"));
				
		groupNameEditor = new GroupNameEditor(groupModel);

		innerPanel1.add(groupNameEditor);
		
		ButtonGroup orderingGroup = new ButtonGroup(); 
		ordered = new JRadioButton("Ordered");
		JRadioButton unordered = new JRadioButton("Unordered");
		orderingGroup.add(ordered);
		orderingGroup.add(unordered);
		unordered.setSelected(true);
		innerPanel1.add(ordered);
		innerPanel1.add(unordered);
		
		JPanel innerPanel2 = new JPanel(); //Holds ok and cancel buttons
		innerPanel2.setLayout(new BoxLayout(innerPanel2, BoxLayout.X_AXIS));				
		JButton okButton = new JButton("Ok");
		groupNameEditor.setTargetButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {
				groupModel.addGroup(groupNameEditor.getGroupName(), ordered.isSelected(), context.getSelectedLinks());
				
				//Undo Checkpoint for Adding Group ID: 1337
				if (controller != null) {
					ActionEvent ae = new ActionEvent(this, 0, AddGroupUI.CREATE_GROUP);
					controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
				}
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {			
				setVisible(false);
			}
		});
		
		
		innerPanel2.add(okButton);
		innerPanel2.add(cancelButton);
		innerPanel1.add(innerPanel2);
		
		innerPanel1.add(groupNameEditor.getMessageField());		
		
		add(innerPanel1);		
		setLocation(loc);
		groupNameEditor.redoNamesList();
		setResizable(false);
		pack();
		setVisible(true);
	}
}
