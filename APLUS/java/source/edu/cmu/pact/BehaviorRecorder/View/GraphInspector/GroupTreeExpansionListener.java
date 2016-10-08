package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
/**
 * This class makes sure each node's isExpanded field is accurate.
 * That field is used to compensate for the way in which the tree model
 * notifies its listeners
 * @author Administrator
 *
 */
public class GroupTreeExpansionListener implements TreeExpansionListener {
	GroupEditorContext editContext;
	public GroupTreeExpansionListener(JTree groupTree, GroupEditorContext editContext) {
		groupTree.addTreeExpansionListener(this);
		this.editContext= editContext;
	}
	public void treeCollapsed(TreeExpansionEvent event) {
		TreePath alteredPath = event.getPath();
		if(alteredPath.getLastPathComponent() instanceof LinkGroup) {
			LinkGroup alteredGroup = (LinkGroup) alteredPath.getLastPathComponent();
			if(editContext.getGroupIsExpanded(alteredGroup)==true) {
				editContext.setGroupIsExpanded(alteredGroup, false);
			}
		}
	}

	public void treeExpanded(TreeExpansionEvent event) {		
		TreePath alteredPath = event.getPath();
		if(alteredPath.getLastPathComponent() instanceof LinkGroup) {
			LinkGroup alteredGroup = (LinkGroup) alteredPath.getLastPathComponent();
			if(editContext.getGroupIsExpanded(alteredGroup)==false) {
				editContext.setGroupIsExpanded(alteredGroup, true);
			}
		}
	}

}
