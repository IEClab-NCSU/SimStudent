package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
/**
 * This class adds functionality to the normal tree render.
 * It displays whether the group is displayed on the graph and whether
 * the group is hovered over in the graph
 * 
 * @author Eric Schwelm
 *
 */
public class GroupTreeRenderer extends DefaultTreeCellRenderer {
    
    GroupEditorContext editContext;
    
    public GroupTreeRenderer(GroupEditorContext editContext) {
	this.editContext = editContext;
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object target,
						  boolean isSelected, 
						  boolean isExpanded, 
						  boolean isLeaf, 
						  int row, boolean hasFocus) {
	setBackgroundNonSelectionColor(Color.WHITE);
	setBorderSelectionColor(Color.WHITE);
	if(target instanceof LinkGroup) {
	    LinkGroup group = (LinkGroup) target;
	    
	    if(!group.equals(editContext.getSelectedGroup()) 
	       && editContext.getGroupIsDisplayedOnGraph(group))
		setBackgroundNonSelectionColor(editContext.getGroupColor(group));
	    /*	if(editContext.getGroupIsHovered(group))
		setBackgroundNonSelectionColor(Color.GRAY);*/
	    
	    Component c = super.getTreeCellRendererComponent(tree, target, 
							     isSelected, 
							     isExpanded, 
							     isLeaf, row, 
							     hasFocus);
	    DefaultTreeCellRenderer label = ((DefaultTreeCellRenderer) c);
	    label.setText(editContext.getGroupModel().getTreeText(group));
	    return label;
	}
	else if(target instanceof ExampleTracerLink){
	    ExampleTracerLink link = (ExampleTracerLink) target;
	    if(editContext.getLinkIsSelected(link)) {
		setBackgroundNonSelectionColor(Color.LIGHT_GRAY); 
	    }
	    return super.getTreeCellRendererComponent(tree, target, isSelected, 
						      isExpanded, isLeaf, row, 
						      hasFocus);
	}
	return super.getTreeCellRendererComponent(tree, target, isSelected, 
						  isExpanded, isLeaf, row, 
						  hasFocus);
    }

}
