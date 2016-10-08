package edu.cmu.pact.BehaviorRecorder.jgraphwindow;



import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class BR_JGraphNode extends DefaultGraphCell implements MarathonElement {
	private static final long serialVersionUID = -5533537476348383406L;

	/** Default background color for nodes in the graph. */
	public static final Color DEFAULT_STATE_COLOR = Color.WHITE;

    /** Background color for nodes having {@link ProblemNode#isBeforeStartState()} true. */
	public static final Color BEFORE_STUDENT_BEGINS_COLOR = new Color(191,191,191);
	
	private ProblemNode problemNode;
	private Port port;
	public BR_JGraphNode(ProblemNode problemNode, int x, int y) {
		super (problemNode);
		//this.
		//GraphConstants.setMoveable(getAttributes(), true);
        this.problemNode = problemNode;
		problemNode.setJGraphNode(this);
        port = createPort();
        BR_JGraphVertexView.initView(x, y, this);
	}
	
	
	
	public Port createPort(){
		DefaultPort temp = new DefaultPort(userObject);
		add(temp);
		temp.setParent(this);
		return temp;
	}
    public static Object[] getVertices(DefaultGraphModel graphModel) {

        Set vertices = new HashSet();
        Object[] roots = DefaultGraphModel.getRoots(graphModel);
        for (int i = 0; i < roots.length; i++) {
            if (DefaultGraphModel.isVertex(graphModel, roots[i]))
                vertices.add(roots[i]);
        }
        return vertices.toArray();
    }
	
    public ProblemNode getProblemNode() {
        return (ProblemNode) getUserObject();
    }
	public void runLayout() {
		
	}

	public String getMarathonIdentifier() {
		return "GraphNode." + toString();
	}

	public String getText() {
		return problemNode.getNodeView().getText();
	}

	public String getToolTipText() {
        return problemNode.getNodeView().getToolTipText();
    }
	
	/* Used by Marathon regression tests to determine the font style of a node. 
	 * Returns 0 for plain, 3 for bold.
	 * @see edu.cmu.pact.BehaviorRecorder.jgraphwindow.MarathonElement#getTextStyle()
	 */
	public int getTextStyle() {
	    int style = -1;	    
	    if (this.getAttributes() != null) {
	        style = GraphConstants.getFont(this.getAttributes()).getStyle();
	    }
        return style;
	}

	public Color getBackground() {
		if (problemNode != null && problemNode.isBeforeStartState())
			return BEFORE_STUDENT_BEGINS_COLOR;
		else
			return DEFAULT_STATE_COLOR;
	}
	

}
