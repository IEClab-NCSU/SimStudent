package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

public class Utils {

	public static Set findDescendants(Object mousePressedCell, GraphLayoutCache graphView, GraphModel graphModel) {

		Set descendantVertices = new HashSet();
		DefaultGraphCell defaultGraphCell = (DefaultGraphCell) mousePressedCell;
		return findDescendants (descendantVertices, defaultGraphCell, graphModel);
	}

	private static Set findDescendants(Set descendantVertices, DefaultGraphCell defaultGraphCell, GraphModel graphModel) {
		Object[] outgoingEdges = DefaultGraphModel.getOutgoingEdges(graphModel, (defaultGraphCell).getChildAt(0));
		
		for (int i = 0; i < outgoingEdges.length; i++) {
			Object targetVertex = DefaultGraphModel.getTargetVertex(graphModel, outgoingEdges[i]);
			descendantVertices.add(targetVertex);
			findDescendants(descendantVertices, (DefaultGraphCell) targetVertex, graphModel);
		}
		return descendantVertices;
	}

}
