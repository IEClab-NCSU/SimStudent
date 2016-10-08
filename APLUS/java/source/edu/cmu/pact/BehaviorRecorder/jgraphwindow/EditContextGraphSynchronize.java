package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.Utilities.CTAT_Controller;

/**
 * This class listens on the jgraph.  It allows groups to be selected by clicking on
 * the graph and updates the editContext's set of selected links based on
 * which links are selected in the graph
 * 
 * If the mouse is pressed and released inside the icon
 * that represents a given group, this class selected that group in the edit context
 * 
 * @author Eric Schwelm
 *
 */
public class EditContextGraphSynchronize 
    extends MouseAdapter 
    implements GraphSelectionListener{

    GroupEditorContext editContext;
    LinkGroup currentGroup;
    
    public EditContextGraphSynchronize(CTAT_Controller controller, JGraph graph) {
	graph.addMouseListener(this);
	graph.getSelectionModel().addGraphSelectionListener(this);
	this.editContext = controller.getProblemModel().getEditContext();
    }
    
    public void mousePressed(MouseEvent e) {
	currentGroup = editContext.getGroupByPointOnGraph(e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
	if(currentGroup!=null) {
	    //if the group pressed on is the same as group released on, selected that group
	    if(currentGroup.equals(editContext.getGroupByPointOnGraph(e.getPoint())))
		editContext.setSelectedGroup(currentGroup);
	    currentGroup=null;
	}
    }
    
    public void valueChanged(GraphSelectionEvent e) {
	for(Object ob : e.getCells()) {
	    if(ob instanceof BR_JGraphEdge) {
		BR_JGraphEdge edge = (BR_JGraphEdge)ob;
		editContext.setLinkIsSelected(
					      editContext.getLinkFromEdge(edge.getProblemEdge()), 
					      e.isAddedCell(ob));
	    }
	}
    }
}
