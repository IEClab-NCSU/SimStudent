package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditContextEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditorContextListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;

/**
 * Sets the group tree selection to be equal the the EditorContextSelection and visa versa
 * 
 * @author Eric Schwelm
 *
 */
public class GroupTreeSelectionSync implements TreeSelectionListener, EditorContextListener{
	GroupEditorContext editContext;
	JTree tree;
	
	public GroupTreeSelectionSync (JTree tree, GroupEditorContext editContext) {
		this.tree = tree;
		tree.addTreeSelectionListener(this);
		this.editContext = editContext;
	}
	
	public void valueChanged(TreeSelectionEvent event) {
		TreePath[] selectionChangedPaths = event.getPaths();
		for(TreePath path : selectionChangedPaths) {
			if(path.getLastPathComponent() instanceof LinkGroup && event.isAddedPath(path)) {
				LinkGroup group = (LinkGroup)path.getLastPathComponent();
				if(editContext.getSelectedGroup()==null 
				|| !editContext.getSelectedGroup().equals(group))
					editContext.setSelectedGroup(group);
			}
		}
	}

	public void editorContextChanged(EditContextEvent e) {
		//Ignore useless events
		if(e.getType() != EditContextEvent.OTHER)
			return;
		LinkGroup group = editContext.getSelectedGroup();		
		if(group==null)
			return;
		Object ob = tree.getSelectionPath().getLastPathComponent();
		if(!(ob instanceof LinkGroup) || !group.equals((LinkGroup)ob))
			tree.setSelectionPath(getGroupPath(group));			
	}
	private TreePath getGroupPath(LinkGroup group) {
		LinkedList<LinkGroup> treePath = new LinkedList<LinkGroup>();
		do {
			treePath.addFirst(group);
			group=editContext.getGroupModel().getGroupParent(group);
		} while(group!=null);
		return new TreePath(treePath.toArray(new LinkGroup[0]));
	}
}
