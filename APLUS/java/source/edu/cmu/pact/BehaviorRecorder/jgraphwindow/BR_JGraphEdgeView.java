/*
 * Created on May 8, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.JGraph;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

import edu.cmu.pact.BehaviorRecorder.Controller.ActionLabelHandler;
import edu.cmu.pact.BehaviorRecorder.Controller.RuleLabelHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Utilities.trace;

public class BR_JGraphEdgeView extends EdgeView {

	private static final long serialVersionUID = -7748053385117552070L;

	private static EdgeRenderer renderer = new BR_EdgeRenderer();

    private BR_JGraphEdge jgraphEdge;
    private ProblemEdge problemEdge;

	private Rectangle2D actionLabelBounds;
	
	private Rectangle2D LispCheckLabelBounds;

	private List<Rectangle2D> ruleBoundsList = new ArrayList<Rectangle2D>();

    public BR_JGraphEdgeView(Object cell) {
        super (cell);
        jgraphEdge = (BR_JGraphEdge) cell;
        problemEdge = jgraphEdge.getProblemEdge();
    }

    
    public boolean intersects(JGraph graph, Rectangle2D rect){
    	if (super.intersects(graph, rect))
    			return true;
    	if (intersectsActionLabel(rect))
    			return true;

    	int index = getRuleBoundsIndex(rect);
    	
    	if (index == -1)
    		return false;
    	
    	return true;
    }


	private int getRuleBoundsIndex(Rectangle2D rect) {
    	if (problemEdge.getController().getShowRuleLabels() == false)
    		return -1;

    	int index = -1;
    	for (int i = 0; i < ruleBoundsList.size(); i++) {
    		Rectangle2D bound = (Rectangle2D) ruleBoundsList.get(i);
    		if (bound.intersects(rect)) { 
    			index = i;
    			break;
    		}
    	}
		return index;
	}


    private boolean intersectsActionLabel(Rectangle2D rect) {
        return actionLabelBounds != null 
                && problemEdge.getController().getShowActionLabels() 
                && actionLabelBounds.intersects(rect);
    }

	private boolean intersectsCheckLispLabel(Rectangle2D rect) {
		return LispCheckLabelBounds != null 
    			&& problemEdge.getController().isPreCheckLISPLabelsFlag() 
    			&& LispCheckLabelBounds.intersects(rect);
	}

	private boolean intersectsLink(Rectangle2D rect) {
	    Rectangle2D bounds = this.getBounds();
        return bounds != null
                && bounds.intersects(rect);
    }

    public CellViewRenderer getRenderer() {
        return BR_JGraphEdgeView.renderer;
    }
    
    public void update() {
    	//Color edgeColor = problemEdge.getEdgeData().getEdgeView().getEdgeColor();
		//GraphConstants.setLineColor(this.getAttributes(), edgeColor);
        boolean preferred = problemEdge.isPreferredEdge();
        if (preferred) {
            GraphConstants.setLineWidth(this.getAttributes(),2);
        } else {
            GraphConstants.setLineWidth(this.getAttributes(),1);
        }
        
        super.update(problemEdge.getController().getJGraphWindow().getJGraph().getGraphLayoutCache());
    }
    
    public boolean isMouseOver() {
        return jgraphEdge.getController().getMouseOverCell() == jgraphEdge;
    }
    
    public ProblemEdge getProblemEdge() {
    	return problemEdge;
    }


	public void setActionLabelBounds(Rectangle2D actionLabelBounds) {
		this.actionLabelBounds = actionLabelBounds;
	}


	
	public void doClick(MouseEvent e) {
		if(trace.getDebugCode("editstudentinput"))
			trace.out("editstudentinput", String.format("BR_JGraphEdgeView.doClick(x=%d, y=%d, count=%d): edge %s",
					e.getX(), e.getY(), e.getClickCount(), problemEdge.getUniqueID()));
		this.problemEdge.getController().comeIntoFocus();
		Rectangle2D rect = new Rectangle2D.Float (e.getX(), e.getY(), 1, 1);
		if (intersectsActionLabel(rect)) {
			if(e.getClickCount() > 1)
				ActionLabelHandler.doubleClick(e, problemEdge.getController(), problemEdge.getActionHandler());
			else if(BR_JGraph.wasRightClick(e))
				ActionLabelHandler.evaluatePopup(e, problemEdge.getController(), problemEdge.getActionHandler());
			return;
		}
		else {
		}
		int index = getRuleBoundsIndex(rect);
		if (index == -1)
			return;
		
		RuleLabel label = (RuleLabel) problemEdge.getEdgeData().getRuleLabels().get(index);
		RuleLabelHandler.evaluatePopup(e, problemEdge.getController(), label.getHandler());
	}


	public void addRuleBound(Rectangle2D ruleLabelBounds) {
		ruleBoundsList.add(ruleLabelBounds);
	}

	public List<Rectangle2D> getRuleBoundsList() {
		return ruleBoundsList;
	}
	
	public void clearRuleBoundsList() {
		ruleBoundsList.clear();
	}


	public MarathonElement getMarathonElement(Point location) {
		Rectangle2D rect = new Rectangle2D.Double(location.x, location.y, 1, 1);
		if (intersectsActionLabel(rect))
			return problemEdge.getActionLabel();
		if (intersectsCheckLispLabel(rect))
			return problemEdge.getPreLispCheckLabel();
		
		int index = getRuleBoundsIndex(rect);
		if (index != -1) {
	        RuleLabel label = (RuleLabel) problemEdge.getEdgeData().getRuleLabels().get(index);
	        return label;
		}
		if (intersectsLink(rect)) {
		    return jgraphEdge;
		}
        return null;	
	}


	public Rectangle2D getActionLabelBounds() {
		return actionLabelBounds;
	}


	public Rectangle2D getLispCheckLabelBounds() {
		return LispCheckLabelBounds;
	}


	public void setLispCheckLabelBounds(Rectangle2D lispCheckLabelBounds) {
		LispCheckLabelBounds = lispCheckLabelBounds;
	}
			
}
