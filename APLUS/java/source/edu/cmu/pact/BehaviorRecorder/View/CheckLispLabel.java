package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.MarathonElement;


public class CheckLispLabel extends JLabel implements MarathonElement
{
	private static final long serialVersionUID = -8627795754670483665L;
	public String preCheckedStatus = EdgeData.NOTAPPLICABLE;
    private BR_Controller controller;
    private EdgeData edgeData;
    
    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    }
	 
	public CheckLispLabel(EdgeData edge, BR_Controller controller)
	{
		super();
        this.controller = controller;
        this.edgeData = edge;
		this.setVisible(true);
                this.setSize(new java.awt.Dimension(60, 30));
	}
	
	public void resetText(int arcID)
	{
		if (preCheckedStatus.equalsIgnoreCase(EdgeData.SUCCESS))
			this.setText(arcID + "C");
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.NO_MODEL))
			this.setText(arcID + "U");
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.BUGGY))
			this.setText(arcID + "B");
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.FIREABLE_BUG))
			this.setText(arcID + "F");
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.NOTAPPLICABLE) ||
					preCheckedStatus.equalsIgnoreCase(EdgeData.NEVER_CHECKED))
			this.setText(arcID + "N");
	}
	
	public void resetForeground()
	{
		if (preCheckedStatus.equalsIgnoreCase(EdgeData.SUCCESS))
			this.setForeground(ActionLabel.correctColor);
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.NO_MODEL))
			this.setForeground(ActionLabel.untacebleColor);
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.BUGGY))
			this.setForeground(ActionLabel.buggyColor);
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.FIREABLE_BUG))
			this.setForeground(ActionLabel.fireableBuggyColor);
		else if (preCheckedStatus.equalsIgnoreCase(EdgeData.NOTAPPLICABLE) ||
					preCheckedStatus.equalsIgnoreCase(EdgeData.NEVER_CHECKED))
			this.setForeground(ActionLabel.unknownColor);
	}
	
	public void resetCheckedStatus (String chekedStatus)
	{
            this.preCheckedStatus = chekedStatus;
	}
        
        public String getCheckedStatus () {
            return preCheckedStatus;
        }
	
	public void resetAll(int arcID, String chekedStatus)
	{
		resetCheckedStatus(chekedStatus);
		resetText(arcID);
		resetForeground();
	}

	public void update(int uniqueID, ProblemNode startNode, ProblemNode endNode) {
            int EdgeStartX, EdgeEndX;
            int EdgeStartY, EdgeEndY;

            //System.err.println("startNode = " + startNode);

            NodeView startVertex = startNode.getNodeView();
            if (startVertex == null) return; // fix bug CTAT1937
            NodeView endVertex = endNode.getNodeView();
            
            Point NodeStartLocation, NodeEndLocation;
            Dimension NodeStartSize, NodeEndSize;

            Point LabelLocation = new Point();
            Dimension LabelSize;

            NodeStartLocation = startVertex.getLocation();
            NodeStartSize = startVertex.getSize();

            EdgeStartX = NodeStartLocation.x + NodeStartSize.width/3;
            EdgeStartY = NodeStartLocation.y + NodeStartSize.height/2;

            NodeEndLocation = endVertex.getLocation();
            NodeEndSize = endVertex.getSize();

            EdgeEndX = NodeEndLocation.x + NodeEndSize.width/3;
            EdgeEndY = NodeEndLocation.y + NodeEndSize.height/2;

            // set preLispCheckStatus
            LabelSize = getSize();

            LabelLocation = new Point();
            LabelLocation.x = EdgeStartX + (EdgeEndX - EdgeStartX) / 5 + 15;
            LabelLocation.y = EdgeStartY + (EdgeEndY - EdgeStartY) / 5 - LabelSize.height/2;

            setLocation(LabelLocation);
            setSize(LabelSize);
	    resetText(uniqueID);
	    resetForeground();
	    setVisible(controller.isPreCheckLISPLabelsFlag());
	    setHorizontalAlignment(SwingConstants.LEFT);
	}

	public String getMarathonIdentifier() {
		return "Edge" + this.edgeData.getEdge().getUniqueID() + "::CheckLabel";
	}
	
	// Used by Marathon to determine the font style of a label.
	// NOTE: The style for CheckLispLabel is always plain.
	public int getTextStyle() {
		return this.getFont().getStyle();
	}

    /** 
     * Tooltips are defined on the edge, not the label itself.
     * @return Empty string.
     */
    public String getToolTipText() {
        return "";
    }

    /**
     * @return {@link #preCheckedStatus} as an XML element;
     *         returns null if {@link #preCheckedStatus} is null
     */
	public Element toElement() {
		if (preCheckedStatus == null)
			return null;
		return new Element("preCheckedStatus").setText(preCheckedStatus);
	}

}
