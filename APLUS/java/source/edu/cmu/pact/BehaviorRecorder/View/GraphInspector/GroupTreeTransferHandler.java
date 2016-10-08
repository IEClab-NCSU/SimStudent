package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.DefaultGroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.Utilities.trace;
/**
 * This class provides the drag and drop functionality in the tree.
 * It allows the user to move a single link from one group to any other
 * group
 */
public class GroupTreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	private static final DataFlavor linkFlavor = new DataFlavor(ExampleTracerLink.class, "ExampleTracerLink");
	GroupModel groupModel;
	private BR_Controller controller;
	TransferSupport trans;
	
	public GroupTreeTransferHandler(GroupModel groupModel, BR_Controller controller) {
		this.groupModel = groupModel;
		this.controller = controller;
	}

	public int getSourceActions(JComponent c){
		if(c instanceof JTree)
			return TransferHandler.MOVE;
		return 0;
	}	
	
	public Transferable createTransferable(JComponent source) {
		if(source instanceof JTree) {
			JTree tree = (JTree)source;
			TreePath[] selectionPaths = tree.getSelectionPaths();
			for(int i = 0; i<selectionPaths.length; i++) {
				if(selectionPaths[i].getLastPathComponent() instanceof ExampleTracerLink){
					ExampleTracerLink selectedLeaf = (ExampleTracerLink) selectionPaths[i].getLastPathComponent();
					return new DataHandler(selectedLeaf, linkFlavor.getMimeType());
				}
			}
		}
		return null;
	}
	
	public void exportDone(JComponent source, Transferable data, int action) {
		
		
	}
	
	public boolean importData(TransferHandler.TransferSupport support) {
		if (trace.getDebugCode("groups"))
			trace.out("groups", "GTTH.importData() isDrop "+support.isDrop()+", dropLocation "+
					(support.getDropLocation() == null ? "null" : support.getDropLocation().getClass()));
		trans = support;
		if (!support.isDrop()) {
            return false;
        }
		
		DropLocation dropLocation = support.getDropLocation();
		if(dropLocation instanceof JTree.DropLocation) {				
			JTree.DropLocation treeDropLocation = (JTree.DropLocation) dropLocation;
			TreePath dropPath = treeDropLocation.getPath();
			if (trace.getDebugCode("groups"))
				trace.out("groups", "GTTH.importData() path "+dropPath+", lastComponent "+
						(dropPath == null ? "null" : dropPath.getLastPathComponent().getClass()));
			if(dropPath==null)
				return false;
			
			LinkGroup groupDroppedOn;
			if(dropPath.getLastPathComponent() instanceof LinkGroup) {
				groupDroppedOn = (LinkGroup)dropPath.getLastPathComponent();
			}
			else if(dropPath.getLastPathComponent() instanceof ExampleTracerLink) {
				ExampleTracerLink linkDroppedOn = (ExampleTracerLink) dropPath.getLastPathComponent();
				groupDroppedOn = groupModel.getUniqueContainingGroup(linkDroppedOn);
			}
			else
				return false;
			ExampleTracerLink draggedLink;
			try {
				draggedLink = (ExampleTracerLink) support.getTransferable().getTransferData(linkFlavor);
			} catch (Exception e) {
				trace.errStack("GTTH.importData(): error getting draggedLink; groupDroppedOn "+groupDroppedOn, e);
				return false;
			}
			boolean alreadyInGroup = groupModel.getUniqueLinks(groupDroppedOn).contains(draggedLink);
			if (trace.getDebugCode("groups"))
				trace.out("groups", "GTTH.importData() draggedLink "+draggedLink+
						(alreadyInGroup ? "" : " not")+ " already in groupDroppedOn "+groupDroppedOn);
			if(alreadyInGroup)
				return false;
			groupModel.addLinkToGroup(groupDroppedOn, draggedLink);
			
    		//Undo checkpoint for renaming node ID: 1337
			ActionEvent ae = new ActionEvent(this, 0, "Move link to different group");
    		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);			
		}
			
		return true;
	}
	
	public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDataFlavorSupported(linkFlavor)) {
		    return false;
		}
		if(support.getComponent() instanceof JTree) {
			DropLocation dropLocation = support.getDropLocation();
			if(dropLocation instanceof JTree.DropLocation) {				
				JTree.DropLocation treeDropLocation = (JTree.DropLocation) dropLocation;
				TreePath dropPath = treeDropLocation.getPath();
				if(dropPath!=null) {
					return true;
				}
			}
		}
		return false;
	}
}