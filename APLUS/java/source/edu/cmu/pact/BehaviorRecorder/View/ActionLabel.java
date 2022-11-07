package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import edu.cmu.pact.BehaviorRecorder.Controller.ActionLabelHandler;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.MarathonElement;
import edu.cmu.pact.Utilities.trace;


/////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *  
 */
/////////////////////////////////////////////////////////////////////////////////////////////////
public class ActionLabel extends BR_Label implements MarathonElement {

	private static final long serialVersionUID = -3182422487549005269L;

	public final static String FAILBEFORE_CORRECT = "Fail before, Correct now";

    public final static String CORRECTBEFORE_INCORRECT = "Correct before, Fail now";

    public final static Color correctColor = Color.green.darker().darker();

    public final static Color untacebleColor = Color.yellow.darker().darker();

    public final static Color buggyColor = Color.red.darker();

    public final static Color fireableBuggyColor = Color.blue.darker();

    public final static Color unknownColor = Color.black;

    public final static int VIEW_SHORT = 0;
   
    static public int  classView = VIEW_SHORT;

    private EdgeData edgeData;

    private boolean mouseEntered;

    String defaultHint = "";

    private static ImageIcon hintIcon;

    private ProblemModel problemModel;
    
    private transient BR_Controller controller;

    private ActionLabelHandler handler;
    
    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    	if (handler != null)
    		handler.restoreTransients(controller);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public ActionLabel(EdgeData edge, ProblemModel problemModel) {
        this("unnamed action", edge, problemModel);
        
    }

    public ActionLabelHandler getHandler() {
        return handler;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public ActionLabel(String actionTypeText, EdgeData edgeData, ProblemModel problemModel) {
        super(actionTypeText, edgeData);
        this.problemModel = problemModel;
		this.controller = problemModel.getController();
	        
		setText(actionTypeText);
		
	    this.edgeData = edgeData;
		
		if (edgeData != null) {
		    
		    resetForeground();
		    
		    addMouseListener(new MouseAdapter() {
			    public void mouseEntered(MouseEvent e) {
				ActionLabel.this.mouseEntered(e);
			    }
			    
			    public void mouseExited(MouseEvent e) {
				ActionLabel.this.mouseExited(e);
			    }
			    
			});
		    
		    //updateToolTip();
		    
		    loadHintIcon();
		    setIcon (hintIcon);
		}
		
		return;
    }
    
    public void setEdgeData(EdgeData edge) {
        edgeData = edge;
    }
    
    /**
     * Test whether this and another instance refer to the same label.
     * @param label other instance
     * @return this.{@link getUniqueID()} == label.{@link getUniqueID()}
     */
    public boolean equals(ActionLabel label) {
        return (getUniqueID() == label.getUniqueID());
    }
    
    public int getUniqueID() {
    	if (edgeData == null)
    		return -1;
    	else
    		return edgeData.getUniqueID();
    }

    public EdgeData getEdge() {
    	return edgeData;
    }

    private boolean testActionLabelInCommutEdgesAdded(ActionLabel testActionLabel) {
        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        ActionLabel tempActionLabel;

        for (int i = 0; i < controller.getSolutionState().getUserVisitedEdges().size(); i++) {
            tempEdge = (ProblemEdge) controller.getSolutionState().getUserVisitedEdges().get(i);
            tempMyEdge = tempEdge.getEdgeData();
            tempActionLabel = tempMyEdge.getActionLabel();
            if (tempActionLabel == testActionLabel)
                return true;
        }

        return false;
    }

    
    private static void loadHintIcon() {
		

        if (hintIcon != null)
            return;
        
        try {
			URL iconURL = ClassLoader.getSystemResource("pact/HintIcon.gif");

			if (iconURL != null) {
			    hintIcon = new ImageIcon(iconURL,
			                         "connected");
			}


		} catch (Exception e) {
			trace.out ("error loading images for connection icon: exception = " + e);
		}

    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void updateToolTip() {
    	//see br_jgraph.setToolTipText
        setToolTipText (null);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void setText(String text) {
        update();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void mouseEntered(MouseEvent e) {
    	
    	boolean continueFlag = true;
        if (controller != null)
        	return;
        if (controller.getCtatModeModel().isExampleTracingMode())
                continueFlag = !testActionLabelInCommutEdgesAdded(this);

        if (continueFlag) setFont(null);

        mouseEntered = true;
        update();
        controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void mouseExited(MouseEvent e) {
        boolean continueFlag = true;
        if (controller != null)
        	return;
        if (controller.getCtatModeModel().isExampleTracingMode())
                continueFlag = !testActionLabelInCommutEdgesAdded(this);

        if (continueFlag) setFont(BRPanel.SMALL_FONT);
        
        mouseEntered = false;

        update();
        
        controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void resetForeground() {
        if (edgeData == null || edgeData.getActionType().equalsIgnoreCase(EdgeData.CORRECT_ACTION))
            setForeground(correctColor);
        else if (edgeData.getActionType().equalsIgnoreCase(EdgeData.UNTRACEABLE_ERROR))
            setForeground(untacebleColor);
        else if (edgeData.getActionType().equalsIgnoreCase(EdgeData.BUGGY_ACTION))
            setForeground(buggyColor);
        else if (edgeData.getActionType().equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
            setForeground(fireableBuggyColor);
        else if (edgeData.getActionType().equalsIgnoreCase(EdgeData.UNKNOWN_ACTION))
                setForeground(unknownColor);
    }

		
	public void update() {
		if(edgeData == null)return;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateToolTip();
				ActionLabel.super.setText(edgeData.getActionLabelText(false, classView));
				resetSize();
				updateLocation();
				repaint();
			}
		});
	}

    /**
     * 
     */
    private void updateLocation() {
        if (edgeData.getSourceProblemNode() == null) {
			return;
		}
        
		if (edgeData.getEndProblemNode() == null) {
			return;
		}
		
        NodeView startVertex = edgeData.getSourceProblemNode().getNodeView();
        NodeView endVertex = edgeData.getEndProblemNode().getNodeView();

        if (startVertex == null || endVertex == null) { return; }

        Point startPoint = startVertex.getOutgoingEdgePoint();
        Point endPoint = endVertex.getIncomingEdgePoint();

        Dimension size = getSize();
        
        boolean showLabels = false;
        if (controller != null)
        	showLabels = controller.getPreferencesModel().getBooleanValue("Show Rule Labels").booleanValue();
        
        double factor;
        if (showLabels)
            factor = 0.6; 
        else
            factor = 0.75;  
        
        Point LabelLocation = new Point();
        LabelLocation.x = startPoint.x + (int) ((endPoint.x - startPoint.x) * factor)
                - size.width / 2;
        LabelLocation.y = startPoint.y + (int) ((endPoint.y - startPoint.y) * factor)
                - size.height / 2;

        setLocation(LabelLocation);

        if (controller != null) {
        	if (controller.isPreferredPathOnlyFlag()) {
        		setVisible(controller.getShowActionLabels() && edgeData.isPreferredEdge());
        	} else {
        		setVisible(controller.getShowActionLabels());
        	}
        }
    }

    public void addHandler(ActionLabelHandler handler) {
        edgeData.getActionLabel().addMouseListener(
                new ActionLabelHandler(edgeData.getActionLabel(), controller));
        this.handler = handler;
    }

    public void setMouseEntered(boolean entered) {
    	mouseEntered = entered;
    	update();
    }

	public String getMarathonIdentifier() {
		return "Edge" + getUniqueID() + "::" + "ActionLabel";
	}

	// Used by Marathon to determine the font style of a label.
	// NOTE: This does not work consistently when the style is plain (i.e., 0)
	// and sometimes causes a NullPointerException in Marathon.  Just return
	// 0 in that case.
	public int getTextStyle() {
		try {
			return edgeData.getActionLabel().getFont().getStyle();
		} catch (java.lang.NullPointerException npe) {
			trace.out("Marathon threw NPE getting text style for action label");
		}
		trace.out("Couldn't get text style for "+getMarathonIdentifier()+"; return 0 instead");
		return 0;
	}

    /** 
     * Tooltips are defined on the edge, not the label itself.
     * @return Empty string.
     */
    public String getToolTipText() {
        return "";
    }

}
