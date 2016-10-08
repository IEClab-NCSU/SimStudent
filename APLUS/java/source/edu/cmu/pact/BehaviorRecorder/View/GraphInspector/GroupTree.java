package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
/**
 * This class initializes the groupTree and all its components.
 * It also serves as the scroll pane that holds the tree.
 * 
 * @author Eric Schwelm
 *
 */
public class GroupTree extends JScrollPane {
	private static final long serialVersionUID = -9178135949749299329L;
	private CTAT_Launcher server;
	
    public GroupTree(CTAT_Launcher server) {
    	this.server = server;
    	refresh();
    }
    
    public void refresh() {
    	BR_Controller controller = server.getFocusedController();
    	
    	GroupEditorContext editContext = controller.getProblemModel().getEditContext();

    	GroupModel groupModel = editContext.getGroupModel();
    	JTree tree = new JTree();
    	tree.setName("GroupEditorTree");

    	GroupTreeRenderer treeRenderer = new GroupTreeRenderer(editContext);
    	tree.setCellRenderer(treeRenderer);

    	GroupTreeTransferHandler transferHandler 
    	= new GroupTreeTransferHandler(groupModel, controller);
    	tree.setTransferHandler(transferHandler);
    	tree.setDragEnabled(true);			

    	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    	GroupTreeModel treeModel = new GroupTreeModel(tree, editContext);
    	tree.setModel(treeModel);

    	new GroupTreeSelectionSync(tree, editContext);
    	new GroupTreeExpansionListener(tree, editContext);		
    	new GroupTreeHoverListener(tree, editContext);
    	new GroupTreePopupMenu(tree, editContext, 
    			this.server.getActiveWindow(), controller);

    	tree.setRootVisible(true);		
    	tree.setShowsRootHandles(true);

    	setViewportView(tree);		 
    }			
}