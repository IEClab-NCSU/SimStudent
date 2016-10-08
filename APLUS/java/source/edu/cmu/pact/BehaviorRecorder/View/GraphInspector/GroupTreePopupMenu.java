package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
/**
 * This class provides the menu when one right clicks on the tree.
 * It handles detecting the click as well as running the menu
 * @author Eric Schwelm
 *
 */
public class GroupTreePopupMenu extends MouseAdapter implements ActionListener{
	private GroupModel groupModel;
	private GroupEditorContext editContext;
	
	private Frame mainWindow;
	private JTree groupTree;
	private JPopupMenu groupPopupMenu;
	private LinkGroup currentGroup;
	private CTAT_Controller controller;


	private JDialog renameGroupDialog;
	private JCheckBoxMenuItem isGroupDisplayed;
	private JMenuItem deleteGroup;
	private JMenuItem renameGroup;
	private JCheckBoxMenuItem isGroupOrdered;
	private JCheckBoxMenuItem isGroupReenterable;
	private JMenuItem deleteSubGroups;
	private GroupNameEditor groupNameEditor;
	
	public GroupTreePopupMenu(JTree groupTree, GroupEditorContext editContext, Frame mainWindow, CTAT_Controller controller) {
		this.mainWindow = mainWindow;
		this.groupTree = groupTree;
		this.editContext = editContext;
		this.controller = controller;
		groupModel = editContext.getGroupModel();	
		groupTree.addMouseListener(this);
		initWindows();
	}
	
	private void initWindows() {
		//Group Popup Window
		groupPopupMenu = new JPopupMenu();
		
		isGroupDisplayed = new JCheckBoxMenuItem("Displayed On Graph");
		isGroupDisplayed.addActionListener(this);
		groupPopupMenu.add(isGroupDisplayed);
		
		deleteGroup = new JMenuItem("Delete");
		deleteGroup.addActionListener(this);
		groupPopupMenu.add(deleteGroup);
		
		renameGroup = new JMenuItem("Rename"); 
		renameGroup.addActionListener(this);
		groupPopupMenu.add(renameGroup);
		
		isGroupOrdered = new JCheckBoxMenuItem("Ordered");
		isGroupOrdered.addActionListener(this);
		groupPopupMenu.add(isGroupOrdered);
		
		isGroupReenterable = new JCheckBoxMenuItem("Reenterable");
		isGroupReenterable.addActionListener(this);
		groupPopupMenu.add(isGroupReenterable);
		
		deleteSubGroups = new JMenuItem("Delete All Subgroups");
		deleteSubGroups.addActionListener(this);
		groupPopupMenu.add(deleteSubGroups);

		//Rename group window
		renameGroupDialog = new JDialog(mainWindow, "Rename Group", true);
		JPanel innerPanel1 = new JPanel(); //All components go in this
		innerPanel1.setLayout(new BoxLayout(innerPanel1, BoxLayout.Y_AXIS));
		
		innerPanel1.add(new JLabel("Enter Group Name"));
				
		groupNameEditor = new GroupNameEditor(groupModel);
		innerPanel1.add(groupNameEditor);
		
		JPanel innerPanel2 = new JPanel(); //Holds ok and cancel buttons
		innerPanel2.setLayout(new BoxLayout(innerPanel2, BoxLayout.X_AXIS));				
		JButton okButton = new JButton("Ok");
		groupNameEditor.setTargetButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {
				groupModel.setGroupName(currentGroup, groupNameEditor.getGroupName());				
				renameGroupDialog.setVisible(false);
				
				//Undo checkpoint for renaming groups ID: 1337
				if (controller instanceof BR_Controller)
				{
					ActionEvent ae = new ActionEvent(this, 0, "Rename Group");
					((BR_Controller)controller).getUndoPacket().getCheckpointAction().actionPerformed(ae);
				}
			}
		});
		cancelButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {			
				renameGroupDialog.setVisible(false);
			}
		});
		
		
		innerPanel2.add(okButton);
		innerPanel2.add(cancelButton);
		innerPanel1.add(innerPanel2);
		
		innerPanel1.add(groupNameEditor.getMessageField());		
		
		renameGroupDialog.add(innerPanel1);
		renameGroupDialog.pack();
		renameGroupDialog.setResizable(false);	
		
		//Add link window
		
	}
	
	public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {        	      
        	TreePath path = groupTree.getPathForLocation(e.getX(), e.getY());
        	if(path==null)
        		return;
        	Object ob = path.getLastPathComponent();
        	if(ob instanceof LinkGroup)
        	{   
        		currentGroup = (LinkGroup) ob;
        		configureMenuForGroup(currentGroup);
        		groupPopupMenu.show(groupTree, e.getX(), e.getY());
        		return;
        	}
        }    
    }
    
    private void configureMenuForGroup (LinkGroup group) {    	
    	isGroupDisplayed.setSelected(editContext.getGroupIsDisplayedOnGraph(group));
    	
    	//Enable if not the top level group
    	deleteGroup.setEnabled(!group.equals(groupModel.getTopLevelGroup()));
   		renameGroup.setEnabled(!group.equals(groupModel.getTopLevelGroup()));

   		//Always Enabled
   		//Make its selection match the group's 
    	isGroupOrdered.setSelected(groupModel.isGroupOrdered(group));
    	
    	isGroupReenterable.setSelected(groupModel.isGroupReenterable(group));    	
    	//Enable if the group has at least 1 subgroup
    	deleteSubGroups.setEnabled(groupModel.getGroupSubgroupCount(group)!=0);	
    }
    
    public void actionPerformed(ActionEvent arg0) {
    	JMenuItem src = 
    		(JMenuItem) (arg0.getSource() instanceof JMenuItem ? arg0.getSource() : null);
    	if (trace.getDebugCode("undo"))
    		trace.out("undo", "GroupTreePopupMenu.actionPerformed("+(src != null ? src.getText() : "")+")");
    	
    	if(arg0.getSource().equals(isGroupDisplayed)){
    		editContext.setGroupIsDisplayedOnGraph(currentGroup, isGroupDisplayed.isSelected());
    	}
		else if(arg0.getSource().equals(deleteGroup)){
			groupModel.removeGroupKeepSubgroups(currentGroup);
			
			//Undo checkpoint for deleting groups ID: 1337
			if (controller instanceof BR_Controller)
			{
				ActionEvent ae = new ActionEvent(this, 0, "Delete Group");
				((BR_Controller)controller).getUndoPacket().getCheckpointAction().actionPerformed(ae);
			}
		}
		else if (arg0.getSource().equals(renameGroup)) {			
			groupNameEditor.redoNamesList();
			groupPopupMenu.setVisible(true);
			renameGroupDialog.setLocation(groupPopupMenu.getLocationOnScreen());
			renameGroupDialog.setVisible(true);
		}
		else if (arg0.getSource().equals(isGroupOrdered)) {
			boolean isOrdered = isGroupOrdered.isSelected();
			groupModel.setGroupOrdered(currentGroup, isOrdered);
			if (currentGroup == groupModel.getTopLevelGroup())
				controller.getProblemModel().getController().updateStatusPanel(null);

			//Undo checkpoint for setting group ordered ID: 1337
			if (controller instanceof BR_Controller)
			{
				ActionEvent ae = new ActionEvent(this, 0, "Set Group "+(isOrdered ? "Ordered" : "Unordered"));
				((BR_Controller)controller).getUndoPacket().getCheckpointAction().actionPerformed(ae);
			}
		}
		else if(arg0.getSource().equals(isGroupReenterable)) {
			boolean isReenterable = isGroupReenterable.isSelected();
			groupModel.setGroupReenterable(currentGroup, isReenterable);
			
			//Undo checkpoint for setting group reenterable ID: 1337
			if (controller instanceof BR_Controller)
			{
				ActionEvent ae = new ActionEvent(this, 0, "Set Group "+(isReenterable ? "" : "Not ")+"Reenterable");
				((BR_Controller)controller).getUndoPacket().getCheckpointAction().actionPerformed(ae);
			}
		}
		else if (arg0.getSource().equals(deleteSubGroups)) {
			groupModel.removeAllGroupSubgroups(currentGroup);
			
			//Undo checkpoint for deleting subgroups ID: 1337
			if (controller instanceof BR_Controller)
			{
				ActionEvent ae = new ActionEvent(this, 0, "Delete All Subgroups");
				((BR_Controller)controller).getUndoPacket().getCheckpointAction().actionPerformed(ae);
			}
		}	
		groupPopupMenu.setVisible(false);
	}
}
