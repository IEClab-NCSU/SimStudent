/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import org.jgraph.JGraph;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditContextEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.EditorContextListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupChangeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupChangeListener;
import edu.cmu.pact.Utilities.CTAT_Controller;

/**
 * Refresh graph when the GroupEditorContext or GroupModel fires an event
 * 
 * @author Eric Schwelm
 *
 */
public class RefreshGraphOnGroupChange implements GroupChangeListener, EditorContextListener{
	JGraph graph;
	public RefreshGraphOnGroupChange(CTAT_Controller controller,
			JGraph jgraph) {
		controller.getProblemModel().getEditContext().addEditorContextListener(this);
		controller.getProblemModel().getExampleTracerGraph().getGroupModel().addGroupChangeListener(this);
		this.graph = jgraph;
	}

	public void groupChanged(GroupChangeEvent e) {
		graph.repaint();}	
	public void editorContextChanged(EditContextEvent e) {
		graph.repaint();}
	
}
