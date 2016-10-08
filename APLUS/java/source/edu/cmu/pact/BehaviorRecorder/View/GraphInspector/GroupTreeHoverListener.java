package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;

/**
 * This class in a mouse motion listener on the group tree.  It updates the
 * hover information in the editor context.
 * 
 * @author Eric Schwelm
 *
 */
public class GroupTreeHoverListener implements MouseMotionListener {
	JTree groupTree;
	GroupEditorContext editContext;
	LinkGroup lastGroupHovered;
	ExampleTracerLink lastLinkHovered;
	public GroupTreeHoverListener(JTree groupTree, GroupEditorContext editContext) {
		this.groupTree = groupTree;
		groupTree.addMouseMotionListener(this);
		this.editContext = editContext;
	}
	public void mouseMoved(MouseEvent e) {
		if(lastGroupHovered!=null) {
			editContext.setGroupIsHovered(lastGroupHovered, false);
			lastGroupHovered=null;
		}
		if(lastLinkHovered!=null) {
			editContext.setLinkIsHovered(lastLinkHovered, false);
			lastLinkHovered=null;
		}
		TreePath path = groupTree.getClosestPathForLocation((int)e.getPoint().getX(), (int)e.getPoint().getY());
		if(groupTree.getPathBounds(path).contains(e.getPoint())) {	
			Object ob = path.getLastPathComponent();
			if(ob instanceof LinkGroup) {
				lastGroupHovered = (LinkGroup) ob;
				editContext.setGroupIsHovered(lastGroupHovered, true);
			}
			else if(ob instanceof ExampleTracerLink) {
				lastLinkHovered = (ExampleTracerLink) ob;
				editContext.setLinkIsHovered(lastLinkHovered, true);
			}
		}				
	}
	public void mouseDragged(MouseEvent arg0) {}
}
