package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
/**
 * This class allows the user to select a valid name for a group.  It maintains a 
 * jcombobox with all the current group names and provides an editing field for entering
 * a new one.  It also checks every time the input is changed and enables/disables a given
 * button if the input is a valid/invalid name.  If enter is pressed in the editing field
 * it simulates a click on the given button.
 * 
 * @author Eric Schwelm
 *
 */
public class GroupNameEditor extends JComboBox implements DocumentListener, KeyListener{

	private static final long serialVersionUID = 1L;
	private static final String INVALIDNAMEMESSAGE = "Invalid Group Name";
	private static final String VALIDNAMEMESSAGE = "Valid Group Name";
	JButton targetButton;
	JTextField editField;
	JTextField groupNameMessageField;
	GroupModel groupModel;
	
	public GroupNameEditor(GroupModel groupModel) {
		super();
		setEditable(true);
		editField = (JTextField)getEditor().getEditorComponent();
		editField.addKeyListener(this);
		editField.getDocument().addDocumentListener(this);
		groupNameMessageField = new JTextField(INVALIDNAMEMESSAGE);
		groupNameMessageField.setEditable(false);
		this.groupModel = groupModel;
	}
	
	public void setTargetButton(JButton button) {
		targetButton = button;
	}
	
	public void redoNamesList() {
		removeAllItems();
		Set<String> names = groupModel.getAllGroupNames();		
		String[] sortedNames = names.toArray(new String[0]);
		Arrays.sort(sortedNames);
		for(String name : sortedNames) {
			//Don't add topLevel group's name
			if(!groupModel.getGroupName(groupModel.getTopLevelGroup()).equals(name))
				this.addItem(name);
		}
		if(targetButton!=null)
			targetButton.setEnabled(false);
		checkInput();
		editField.selectAll();
	}
	
	public String getGroupName() {
		return editField.getText();
	}
	
	public JTextField getMessageField() {
		return groupNameMessageField;
	}
	
	private void checkInput() {
		
		if(groupModel.isGroupNameValid(editField.getText())) {
			if(targetButton!=null)
				targetButton.setEnabled(true);
			groupNameMessageField.setText(VALIDNAMEMESSAGE);
		}
		else {
			if(targetButton!=null)
				targetButton.setEnabled(false);
			groupNameMessageField.setText(INVALIDNAMEMESSAGE);
		}
			
	}
	
	//Check the current input whenever it changes
	public void changedUpdate(DocumentEvent e) {
		checkInput();}
	public void insertUpdate(DocumentEvent e) {
		checkInput();}
	public void removeUpdate(DocumentEvent e) {
		checkInput();}

	//Click the button if enter is pressed
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ENTER)	
			if(targetButton!=null)
				targetButton.doClick();
	}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
}
