/*
 * Created on May 8, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;

public class BR_JGraphVertexView extends VertexView {
	private static final long serialVersionUID = -1036742335115986098L;
	BR_JGraphNode cell;
    private ProblemNode problemNode;
    
    public BR_JGraphVertexView(BR_JGraphNode cell) {
    	
        super (cell);
        this.cell = cell;
        this.problemNode = (ProblemNode) cell.getUserObject();
        setBorder (1);
		GraphConstants.setBackground(getAttributes(), getBackground());
    }

	/**
	 * Set the background color to {@link BR_JGraphNode#getBackground()} if available;
	 * else to {@link BR_JGraphNode#DEFAULT_STATE_COLOR}. 
	 */
	public Color getBackground() {
		Color color = BR_JGraphNode.DEFAULT_STATE_COLOR;
		if (problemNode != null && problemNode.getJGraphNode() != null)
			color = problemNode.getJGraphNode().getBackground();
		if (trace.getDebugCode("br")) trace.out("br", "problemNode "+problemNode+", getJGraphNode() "+
				problemNode.getJGraphNode()+", color "+color);
		return color;
	} 

    /**
     * @param thickness
     */
    private void setBorder(final int thickness) {
        final int padding = 2;
        GraphConstants.setBorder(this.getAttributes(), BorderFactory
                .createCompoundBorder(BorderFactory
                        .createLineBorder(Color.GRAY, thickness), BorderFactory
                        .createEmptyBorder(padding, padding, padding, padding)));

//        GraphConstants.setBorderColor(this.getAttributes(), Color.BLACK);
    }

    /**
     * @param x
     * @param y
     * @param graphNode TODO
     */
    public static void initView(int x, int y, BR_JGraphNode graphNode) {
        GraphConstants.setAutoSize(graphNode.getAttributes(), true);
    	GraphConstants.setBounds(graphNode.getAttributes(), new Rectangle2D.Double(
    			x, y, 0, 0));
    	
        
        GraphConstants.setBackground(graphNode.getAttributes(), graphNode.getBackground());
        GraphConstants.setOpaque(graphNode.getAttributes(), true);
    
        GraphConstants.setEditable(graphNode.getAttributes(), false);
    }
    

}
