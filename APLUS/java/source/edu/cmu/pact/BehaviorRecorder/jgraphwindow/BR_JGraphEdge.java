/*
 * Created on May 3, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;

public class BR_JGraphEdge extends DefaultEdge implements MarathonElement {
    
	private static final long serialVersionUID = -830251680303940439L;
	private Color edgeColor = null;
    private ProblemEdge problemEdge;
    private transient JGraphController controller;

    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller.getJGraphWindow().getJGraphController();
    }
    
    public BR_JGraphEdge(ProblemEdge problemEdge, JGraphController controller) {
        super("");
        this.problemEdge = problemEdge;
        problemEdge.setJGraphEdge(this);
        setOptions(problemEdge);
        this.controller = controller;
    }
    
    
    public JGraphController getController () {
    	return controller;
    }
    
    /**
     * @param problemEdge
     */
    private void setOptions(ProblemEdge problemEdge) {
        int arrow = GraphConstants.ARROW_TECHNICAL;
        GraphConstants.setLineEnd(this.getAttributes(), arrow);
        GraphConstants.setEndFill(this.getAttributes(), false);
        GraphConstants.setDisconnectable(this.getAttributes(), true);
        GraphConstants.setMoveable(this.getAttributes(), true);
        GraphConstants.setConnectable(this.getAttributes(), true);
        GraphConstants.setSelectable(this.getAttributes(), false);
        if(this.getEdgeColor()!=null)
        	GraphConstants.setLineColor(this.getAttributes(), this.getEdgeColor());
        GraphConstants.setLabelAlongEdge(this.getAttributes(), true);
        
        boolean preferred = problemEdge.isPreferredEdge();
        if (preferred) {
            GraphConstants.setLineWidth(this.getAttributes(),2);
        }
    }
    
    public ProblemEdge getProblemEdge() {
        return problemEdge;
    }
    
    
    public MarathonElement getMarathonElement(String uniqueIdentifier) {
	
	String[] split = uniqueIdentifier.split("::");
	if (split[1].equals("Rule")) {
	    int index = getRuleIndex(uniqueIdentifier);
	    return (MarathonElement) problemEdge.getEdgeData().getRuleLabels().get(index);
	} else if (split[1].equals("ActionLabel")) {
	    return problemEdge.getActionLabel();
    } else if (split[1].equals("CheckLabel")) {
        return problemEdge.getPreLispCheckLabel();
    } else if (split[1].equals("Link")) {
        return this;
	}
	return null;
    }
    
    public String getMarathonIdentifier() {
        return "Link" + problemEdge.getUniqueID() + "::Link";
    }

    public String getText() {
        return "Link" + problemEdge.getUniqueID();
    }

    public int getTextStyle() {
        return 0;
    }

    public String getToolTipText() {
        return problemEdge.getEdgeData().getTooltipText();
    }
    
    public int getRuleIndex(String uniqueIdentifier) {
	String[] split = uniqueIdentifier.split("::");
	String recordedRuleText = split[2];
	int index = 0;
	List<RuleLabel> ruleList = problemEdge.getEdgeData().getRuleLabels();
	int count = 0;
	for (Iterator<RuleLabel> rules = ruleList.iterator(); rules.hasNext();) {
	    RuleLabel ruleLabel = (RuleLabel) rules.next();
	    String ruleText = ruleLabel.getText();
	    if (recordedRuleText.equals(ruleText))
		index = count;
	    count ++;
	}
	return index;
    }
    
    
    public MarathonElement getMarathonElement(Point location) {
        CellView mapping = controller.getGraphView().getMapping(this, true);
        BR_JGraphEdgeView view = (BR_JGraphEdgeView) mapping;
        return view.getMarathonElement(location);
    }
    
    
    public boolean matchesMarathonIdentifier(String uniqueIdentifier) {
        String[] split = uniqueIdentifier.split("::");
        if (split[0]
                .equals("Edge" + problemEdge.getActionLabel().getUniqueID()))
            return true;
        if (split[0].equals("Link" + problemEdge.getUniqueID()))
            return true;
        return false;
    }


	public void setEdgeColor(Color edgeColor) {
		this.edgeColor = edgeColor;
	}


	public Color getEdgeColor() {
		return edgeColor;
	}
}
