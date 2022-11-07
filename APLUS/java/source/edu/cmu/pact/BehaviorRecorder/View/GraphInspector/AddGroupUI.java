package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditContextEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditorContextListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupChangeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupChangeListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
/**
 * This class allows the creation of new groups.  It provides the functionality
 * for the dockable "Group Creator" window.
 * 
 * Group creation requires a set of links, a name, and an ordering.  The links are
 * provided by the GroupEditorContext.  The name and ordering are queried through
 * dialogs.  
 * 
 * If a group is selected and at least one link is selected, the user
 * may remove the selected links and are in the group and add the selected links
 * that are not in the group as one operation
 * 
 * @author Eric Schwelm
 *
 */
public class AddGroupUI extends JPanel implements ActionListener, GroupChangeListener, EditorContextListener
{
	private static final long serialVersionUID = -503524705341597865L;
	static final String CREATE_GROUP = "Create Group";
	protected JButton createGroupButton;
	protected JButton addRemoveLinksButton;
	protected JTextArea errorMessageField;
	
	protected JDialog createGroupDialog;
	protected GroupNameEditor groupNameEditor;
	protected JRadioButton ordered;	
	protected JButton okButton;
	
	protected GroupEditorContext editContext;
	protected GroupModel groupModel;
	protected Frame mainWindow;
	
	private CTAT_Launcher server;
	private Map<Integer, GroupModel> groupMap;
	
	public AddGroupUI(CTAT_Launcher server) {
		this.server = server;
		this.mainWindow = server.getActiveWindow();
		this.groupMap = new HashMap<Integer, GroupModel>();
		refresh();
	}
	
	public void refresh() {
		removeAll();
		BR_Controller controller = server.getFocusedController();
		this.editContext = controller.getProblemModel().getEditContext();
		editContext.addEditorContextListener(this);

		// don't use this as a listener for models that aren't currently active
		if(groupModel != null) {
			groupModel.removeGroupChangeListener(this);
		}
		Integer currentKey = Integer.valueOf(controller.getTabNumber());
		// retrieve the model if one already exists, otherwise create a new one
		if(this.groupMap.containsKey(currentKey)) {
			groupModel = this.groupMap.get(currentKey);
		}
		else {
			groupModel = editContext.getGroupModel();
			this.groupMap.put(currentKey, groupModel);
		}
		// in either case the model shouldn't have this as a listener, so add it
		groupModel.addGroupChangeListener(this);
		
		createGroupButton = new JButton(CREATE_GROUP);	
		createGroupButton.addActionListener(this);
		createGroupButton.setEnabled(false);
		
		addRemoveLinksButton = new JButton("Add/Remove Links");
		addRemoveLinksButton.addActionListener(this);
		addRemoveLinksButton.setEnabled(false);
		
		errorMessageField = new JTextArea();
		errorMessageField.setEditable(false);
		errorMessageField.setLineWrap(true);
		/*
		if(newGroup) {
			
			disableGroupCreation("No Links Selected");
		}
		else {
			testCurrentSelection();
		}
		*/
		testCurrentSelection();
		//disableGroupCreation("No Links Selected");
								
		JPanel buttonLayout = new JPanel();
		buttonLayout.setLayout(new BoxLayout(buttonLayout, BoxLayout.X_AXIS));
		buttonLayout.add(createGroupButton);
		buttonLayout.add(addRemoveLinksButton);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(buttonLayout);
		add(errorMessageField);		
	}	
	
	//Create group button clicked
	public void actionPerformed(ActionEvent e)
	{
		BR_Controller controller = server.getFocusedController();		
		if(e.getSource().equals(createGroupButton)) {
			new AddGroupPopup(
					mainWindow, 
					CREATE_GROUP, 
					true, 
					editContext, 
					createGroupButton.getLocationOnScreen(), controller);
		}
		else if(e.getSource().equals(addRemoveLinksButton)) {
			LinkGroup group = getSelectedGroup();
			String actionDesc = null;
			for(ExampleTracerLink link : getSelectedLinks()) {
				if(groupModel.isLinkInGroup(group, link)) {		
					groupModel.removeLinkFromGroup(group, link);
				}
				else {
					groupModel.addLinkToGroup(group, link);

				}
				actionDesc = "Add or remove links in group";
			}
			
			//trace.out("**Checkpoint test for addRemoveLinksButton **");
			//Undo checkpoint for add/remove to group ID: 1337
			if (actionDesc != null) {                           // only if links moved
				ActionEvent ae = new ActionEvent(this, 0, actionDesc);
				controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
			}
		}
	}	
	public void groupChanged(GroupChangeEvent e) {
		testCurrentSelection();}
	public void editorContextChanged(EditContextEvent e) {
		testCurrentSelection();}
	
	public boolean isGroupSelected() {
		return getSelectedGroup()!=null;
	}
	
	public LinkGroup getSelectedGroup() {
		return editContext.getSelectedGroup();
	}
	
	public boolean isLinkSelectionNonEmpty() {
		return getSelectedLinks().size()!=0;
	}
	
	/**
	 * Displays a message and enables/disables the createGroupButton
	 * based on whether the selected links can be made into a group 
	 * 
	 * Also enables/disables the add/remove links button based on if a
	 * valid group and links are selected
	 */
	public void testCurrentSelection()
	{	
		Set<ExampleTracerLink> links = getSelectedLinks();
		if(links.size() == 0)
		{
			disableGroupCreation("No Links Selected");
			return;
		}
		else {
			String validMessage = groupModel.isLinkSetAddableAsGroup(links);
			boolean isValid = validMessage.equals("");
			if(validMessage.equals("")) {
				validMessage = "Correct Link selection";
			}
			validMessage += "\n\nSelected Links:";
			for(ExampleTracerLink link : links)
				validMessage+="\n"+link;
			if(isValid)
				enableGroupCreation(validMessage);
			else
				disableGroupCreation(validMessage);
		}		
		if(links.size()==0 
		|| editContext.getSelectedGroup()==null 
		|| editContext.getSelectedGroup().equals(groupModel.getTopLevelGroup()))
			addRemoveLinksButton.setEnabled(false);
		else
			addRemoveLinksButton.setEnabled(true);
	}
	
	private Set<ExampleTracerLink> getSelectedLinks() {
		return editContext.getSelectedLinks();
	}
	
	private void disableGroupCreation(String message)
	{						
		errorMessageField.setText(message);
		createGroupButton.setEnabled(false);
	}
	
	private void enableGroupCreation(String message)
	{
		errorMessageField.setText(message);		
		createGroupButton.setEnabled(true);		
	}
}
