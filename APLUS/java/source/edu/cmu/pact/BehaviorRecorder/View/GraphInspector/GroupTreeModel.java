package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditContextEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditorContextListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupChangeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupChangeListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
/**
 * This GroupTreeModel class tells the groupTree how the tree is structured
 * and notifies listeners when the structure is changed.  This includes changes
 * to a single node, such as when the ordering of a node is switched.  This class
 * also makes sure the expansion status of a node does not change regardless of
 * any change in it's position in the tree
 *  
 * @author Eric Schwelm
 *
 */
public class GroupTreeModel implements TreeModel, GroupChangeListener, EditorContextListener
{			
	HashSet<TreeModelListener> treeListeners;
	ExampleTracerGraph graph;	
	GroupModel groupModel;
	GroupEditorContext editContext;
	JTree groupTree;
	//Array of colors for the nodes to be set to
	Color[] colors;
	int lastColorIndex = 0;
	
	public GroupTreeModel(JTree groupTree, GroupEditorContext editContext)
	{		
		this.groupTree = groupTree;
		treeListeners = new HashSet<TreeModelListener>();
		this.editContext = editContext;
		groupModel = editContext.getGroupModel();
		editContext.addEditorContextListener(this);
		groupModel.addGroupChangeListener(this);
	}
	
	public void addTreeModelListener(TreeModelListener listener) {
		treeListeners.add(listener);			
	}

	public Object getChild(Object parent, int index) {				
		if(parent instanceof LinkGroup)
		{
			LinkGroup group = (LinkGroup) parent;
			LinkGroup[] sortedSubGroups = getSortedSubGroups(group); 				
			ExampleTracerLink[] sortedUniqueLinks = getSortedUniqueLinks(group);
			
			if(index-sortedSubGroups.length<0)
				return sortedSubGroups[index];
			else
				return sortedUniqueLinks[index-sortedSubGroups.length];
		}
		else if(parent instanceof ExampleTracerLink) {
			return null;
		}
		else
			throw new IllegalArgumentException("Tree Node not of type ExampleTracerGroup");
	}

	public int getChildCount(Object arg0) {
		if(arg0 instanceof LinkGroup)
		{
			LinkGroup group = (LinkGroup) arg0;
			return groupModel.getGroupSubgroupCount(group)+
				groupModel.getUniqueLinks(group).size();
		}
		else if (arg0 instanceof ExampleTracerLink) {
			return 0;
		}
		else
			throw new IllegalArgumentException("Tree Node not of type ExampleTracerGroup");
	}

	public int getIndexOfChild(Object arg0, Object arg1) {
		if(arg0 instanceof LinkGroup)
		{
			LinkGroup group = (LinkGroup) arg0;
			if(arg1 instanceof ExampleTracerLink)
			{
				ExampleTracerLink childLink = (ExampleTracerLink) arg1;
				ExampleTracerLink[] sortedUniqueLinks = getSortedUniqueLinks(group);
				return Arrays.binarySearch(sortedUniqueLinks, childLink, new TreeModelLinkComparator()) 
					+ groupModel.getGroupSubgroupCount(group);
			}
			else if(arg1 instanceof LinkGroup) {
				LinkGroup childGroup = (LinkGroup) arg1;
				LinkGroup[] sortedSubGroups = getSortedSubGroups(group);
				return Arrays.binarySearch(sortedSubGroups, childGroup, new TreeModelGroupComparator(groupModel));				
			}
			else
				throw new IllegalArgumentException("Child not of type ExampleTracerGroup/Link");
		}
		else if (arg0 instanceof ExampleTracerLink) {
			return -1;
		}
		else {
			System.out.println(arg0.getClass());
			throw new IllegalArgumentException("Tree Node not of type ExampleTracerGroup");
		}			
	}

	public Object getRoot() {			
		return groupModel.getTopLevelGroup();
	}

	public boolean isLeaf(Object ob) {
		if(ob instanceof LinkGroup)
		{
			return false;
		}
		else if (ob instanceof ExampleTracerLink) {
			return true;
		}
		else
			throw new IllegalArgumentException("Tree Node not of type ExampleTracerGroup");								
	}

	public void removeTreeModelListener(TreeModelListener listener) {				
		treeListeners.remove(listener);
	}

	public void valueForPathChanged(TreePath arg0, Object arg1) {}
	
	public void groupChanged(GroupChangeEvent event) {		
		notifyListeners();
	}
	
	private void fixTreeAfterNotify() {
		for(LinkGroup group : groupModel) {
			if(editContext.getGroupIsExpanded(group)) {				
				groupTree.expandPath(getPathToGroup(group));
			}
			else
				groupTree.collapsePath(getPathToGroup(group));
		}
		if(editContext.getSelectedGroup()!=null)
			groupTree.setSelectionPath(getPathToGroup(editContext.getSelectedGroup()));
	}
	
	private TreePath getPathToGroup(LinkGroup group) {
		LinkedList<LinkGroup> treePath = new LinkedList<LinkGroup>();
		do {
			treePath.addFirst(group);
			group=groupModel.getGroupParent(group);
		} while(group!=null);
		return new TreePath(treePath.toArray(new LinkGroup[0]));
	}
	
	private void notifyListeners() {
		for(TreeModelListener listener : treeListeners) {
			listener.treeStructureChanged(new TreeModelEvent(this, new Object[] {getRoot()}));
		}		
		fixTreeAfterNotify();
	}
	
	private LinkGroup[] getSortedSubGroups(LinkGroup group) {
		Set<LinkGroup> subGroupsSet = groupModel.getGroupSubgroups(group);
		LinkGroup[] subGroups = subGroupsSet.toArray(new LinkGroup[0]);
		Arrays.sort(subGroups, new TreeModelGroupComparator(groupModel));
		return subGroups;
	}
	private ExampleTracerLink[] getSortedUniqueLinks(LinkGroup group) {
		Set<ExampleTracerLink> uniqueLinksSet = groupModel.getUniqueLinks(group);
		ExampleTracerLink[] uniqueLinks = uniqueLinksSet.toArray(new ExampleTracerLink[0]);
		Arrays.sort(uniqueLinks, new TreeModelLinkComparator());
		return uniqueLinks;
	}
	
	public void editorContextChanged(EditContextEvent e) {
		if (editContext.getSelectedGroup() == null)
			groupTree.clearSelection();
		if(e.getType()==EditContextEvent.OTHER)
			notifyListeners();		
	}
}	