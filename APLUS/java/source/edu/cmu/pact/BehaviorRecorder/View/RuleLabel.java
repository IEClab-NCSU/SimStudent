package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.RuleLabelHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReaderJDom;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.MarathonElement;

public class RuleLabel extends BR_Label implements MarathonElement {
    private String ruleNameText = null;

    private boolean nameSet;

    private boolean mouseEntered;
    
    private BR_Controller controller;

	private RuleLabelHandler ruleLabelHandler;
    
    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    	if (ruleLabelHandler != null)
    		ruleLabelHandler.restoreTransients(controller);
    }

    public RuleLabel(String ruleNameText, BR_Controller controller) {
    	super();
    	this.controller = controller;
        this.ruleNameText = ruleNameText;
        this.setText(ruleNameText);
        this.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                RuleLabel.this.mouseEntered(e);
            }

            public void mouseExited(MouseEvent e) {
                RuleLabel.this.mouseExited(e);
            }

        });
    }

    public RuleLabel(RuleLabel l) {
    	super();
        controller = l.controller;
        if (controller == null)
            throw new NullPointerException("BR_Controller == null");
        this.ruleNameText = l.ruleNameText;
        this.setText(ruleNameText);
        nameSet = l.nameSet;
    }


    public String getRuleName() {
        return this.ruleNameText;
    }

    public void setText(String ruleText) {

        super.setText(ruleText);
        this.ruleNameText = ruleText;
        nameSet = true;
    }

    public boolean isNameSet() {
        return nameSet;
    }

    public void update(ProblemNode startNode, ProblemNode endNode,
            int ruleNumber, int ruleCount) {

        NodeView startVertex = startNode.getNodeView();
        if (startVertex == null) return;  // fix bug CTAT1937
        NodeView endVertex = endNode.getNodeView();

        Point startPoint = new Point(startVertex.getOutgoingEdgePoint());
        Point endPoint = new Point(endVertex.getIncomingEdgePoint());

        Point location = new Point();

        resetSize();

        double factor = 0.75;

        startPoint.x += (endPoint.x - startPoint.x) * factor;
        startPoint.y += (endPoint.y - startPoint.y) * factor;

        location.x = startPoint.x + (endPoint.x - startPoint.x) / ruleCount
                * (ruleNumber) - getSize().width / 2;
        location.y = startPoint.y + (endPoint.y - startPoint.y) / ruleCount
                * (ruleNumber) - getSize().height / 2;

        setLocation(location);
        setVisible(controller.getShowRuleLabels());

    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void mouseEntered(MouseEvent e) {
        // trace.out(5, this, "MOUSE ENTERED");

        if (!mouseEntered) {
            setFont(null);

            mouseEntered = true;
            resetSize();

            controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void mouseExited(MouseEvent e) {
        if (mouseEntered) {
            setFont(BRPanel.SMALL_FONT);

            mouseEntered = false;

            resetSize();
            controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
        }
    }
    
    public String toString() {
        return getRuleName();
    }

	public RuleLabelHandler getHandler() {
		return ruleLabelHandler;
	}

	public void setHandler(RuleLabelHandler handler) {
		this.ruleLabelHandler = handler;
	}

	public String getMarathonIdentifier() {
		return "Edge" + ruleLabelHandler.getProblemEdge().getActionLabel().getUniqueID() + "::Rule::" + ruleNameText;
	}
	
	// Used by Marathon to determine the font style of a label.
	// NOTE: This does not work consistently when the style is plain (i.e., 0)
	// and sometimes causes a NullPointerException in Marathon.  Just return
	// 0 in that case.
	public int getTextStyle() {
		try {
			return ruleLabelHandler.getProblemEdge().getActionLabel().getFont().getStyle();
		} catch (java.lang.NullPointerException npe) {
			System.out.println("Marathon threw NPE getting text style for rule label");
		}
		System.out.println("Couldn't get text style for "+getMarathonIdentifier()+"; return 0 instead");
		return 0;
	}

    /** 
     * Tooltips are defined on the edge, not the label itself.
     * @return Empty string.
     */
    public String getToolTipText() {
        return "";
    }
    
    /**
     * Create an XML element for .brd files. Includes an indicator element currently
     * unread by {@link ProblemStateReaderJDom}.
     * @return XML element for brd
     */
    public Element toElement() {
    	Element elt = new Element("rule");
    	String text = getText();
    	if (text != null)
    		elt.addContent(new Element("text").setText(text));
    	elt.addContent(new Element("indicator").setText(Integer.toString(-1)));
    	return elt;
    }
}
