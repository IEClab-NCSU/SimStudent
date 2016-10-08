/*
 * Created on May 8, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

public class BR_CellViewFactory extends DefaultCellViewFactory {


    /**
     * Constructs a VertexView view for the specified object.
     */
    protected VertexView createVertexView(Object cell) {
        return new BR_JGraphVertexView((BR_JGraphNode) cell);
    }

    /**
     * Constructs an EdgeView view for the specified object.
     */
    protected EdgeView createEdgeView(Object cell) {
        return new BR_JGraphEdgeView(cell);
    }
    
   
}
