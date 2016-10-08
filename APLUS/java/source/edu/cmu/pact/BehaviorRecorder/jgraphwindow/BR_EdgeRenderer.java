/*
 * Created on Jun 29, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.GraphConstants;

import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Utilities.trace;

public class BR_EdgeRenderer extends EdgeRenderer {
	private static final double SYMBOLLENFRACTION = .9;
	private static final int SYMBOLSPACING = 15;
	private static final int SYMBOLWIDTH = 10; 
	private static final long serialVersionUID = 1L;
	private BR_JGraphEdgeView edgeView;
    private final Stroke lineStroke = new BasicStroke(1.5f);
	private boolean showActionLabels;
	private boolean showRuleLabels;
	private boolean showPreLispCheckLabels;
    public void paint(Graphics g) {
        
    
        super.paint(g);
//        trace.out("Start BR_EdgeRenderer paint");
        Rectangle jgraphClipBounds = edgeView.getProblemEdge().getController().getJGraphWindow().getJGraph().getClipBounds();
        
        showActionLabels = edgeView.getProblemEdge().getController().getShowActionLabels();
		showRuleLabels = edgeView.getProblemEdge().getController().getShowRuleLabels();
		showPreLispCheckLabels = edgeView.getProblemEdge().getController().isPreCheckLISPLabelsFlag();
		int labelCount = 0;
	    if (showPreLispCheckLabels)
	        labelCount += 1;
        if (showActionLabels)
        	labelCount += 1;
        if (showRuleLabels)
        	labelCount += edgeView.getProblemEdge().getEdgeData().getRuleLabels().size();
 
        GroupEditorContext editContext = edgeView.getProblemEdge().getController().getProblemModel().getEditContext();
        drawGroups(g, jgraphClipBounds, editContext);
        
        if (showPreLispCheckLabels)
			drawPreLispCheckLabels(g, labelCount, jgraphClipBounds);
        if (showActionLabels)
        	drawActionLabel(g, labelCount, jgraphClipBounds);
        if (showRuleLabels)
        	drawRuleLabels(g, labelCount, jgraphClipBounds);
         
		
    }
    
    private void drawGroups(Graphics g, Rectangle jgraphClipBounds, GroupEditorContext editContext) {
    	GroupModel groupModel = editContext.getGroupModel();
    	List<LinkGroup> groupsToDraw = editContext.getDisplayedGroupsByLink(
    			edgeView.getProblemEdge().getController().getExampleTracerGraph().getLink(
    					edgeView.getProblemEdge()));
    	editContext.resetProblemEdgeMap(edgeView.getProblemEdge());
    	//Setup Graphics2D object
    	Point2D startPoint = edgeView.getPoint(0);
    	Point2D endPoint = edgeView.getPoint(edgeView.getPointCount()-1);
    	Point2D edgeCenter = new Point2D.Double( (startPoint.getX()+endPoint.getX())/2,
				(startPoint.getY()+endPoint.getY())/2);    	
    	Point2D arrowVector = new Point2D.Double(startPoint.getX()-endPoint.getX(),
    			endPoint.getY()-startPoint.getY());
    	
    	//Rotation needed to have groups aligned to the right of the edge
    	double rotation = Math.atan2(arrowVector.getX(), arrowVector.getY())-1.57079633;//-1.57079633 = -pi/2 for -90 degree rotation     	
    	double groupSymbolLength = Point2D.distance(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY()) * SYMBOLLENFRACTION;
    	
    	//Draw the groups
    	Graphics2D g2D = (Graphics2D) g;    	    	
    	for(int i = 0; i<groupsToDraw.size(); i++) {
    		if(groupsToDraw.get(i)!=null) {
    			LinkGroup groupToDraw = groupsToDraw.get(i);
    			g2D.setColor(Color.BLACK);
    			Shape shapeToDraw;
    			if(groupModel.isGroupOrdered(groupToDraw)) {
    				shapeToDraw = new Rectangle2D.Double(edgeCenter.getX()-groupSymbolLength/2, edgeCenter.getY()+SYMBOLSPACING*(i+1), groupSymbolLength, SYMBOLWIDTH);
    			}
    			else {
    				shapeToDraw = new Ellipse2D.Double(edgeCenter.getX()-groupSymbolLength/2, edgeCenter.getY()+SYMBOLSPACING*(i+1), groupSymbolLength, SYMBOLWIDTH);    				
    			}
    			g2D.setClip(jgraphClipBounds);
    			AffineTransform transform = g2D.getTransform();    			
    			transform.rotate(rotation, edgeCenter.getX(), edgeCenter.getY());  
    			shapeToDraw = transform.createTransformedShape(shapeToDraw);
    			g.setColor(editContext.getGroupColor(groupToDraw));
    			g2D.fill(shapeToDraw);    			
    			if(editContext.getGroupIsHovered(groupToDraw))
					g.setColor(Color.BLUE);
    			if(editContext.getSelectedGroup() !=null 
    					&& editContext.getSelectedGroup().equals(groupToDraw))
					g.setColor(Color.GREEN);    	
    			g2D.setStroke(new BasicStroke(2));
    			g2D.draw(shapeToDraw);
    			editContext.addEdgeShapeGroupMap(edgeView.getProblemEdge(), shapeToDraw, groupToDraw);    				
    		}
    	}
    }

	private void drawPreLispCheckLabels(Graphics g, int labelCount, Rectangle jgraphClipBounds) {
		JLabel PreLispCheckLabel = edgeView.getProblemEdge().getEdgeData().getPreLispCheckLabel();

		int slices = labelCount + 1;
		int currentLabel = 1;
		if (showActionLabels) {
			currentLabel++;
		}
		int oneSlice = GraphConstants.PERMILLE / (slices);

		String text = PreLispCheckLabel.getText();

		Rectangle2D LispCheckLabelBounds = drawLabel(g, oneSlice * currentLabel, text, null, Color.black, PreLispCheckLabel.getFont(), jgraphClipBounds);
	    
        edgeView.setLispCheckLabelBounds(LispCheckLabelBounds);   

			
	}
	

	
	private void drawRuleLabels(Graphics g, int labelCount, Rectangle jgraphClipBounds) {
		edgeView.clearRuleBoundsList();
		List<RuleLabel> ruleList = edgeView.getProblemEdge().getEdgeData().getRuleLabels();
		
		Font font2 = edgeView.getProblemEdge().getEdgeData().getActionLabel().getFont();
		ExampleTracerTracer exampleTracer = edgeView.getProblemEdge().getController().getExampleTracer();
		
		
		if (PseudoTutorMessageHandler.USE_NEW_EXAMPLE_TRACER) {
			boolean visited = edgeIsVisited(exampleTracer);		
			if (visited)
				font2 = BRPanel.VISITED_EDGE_FONT;
			else if (edgeView.isMouseOver())
				font2 = BRPanel.NORMAL_FONT;
			else
				font2 = BRPanel.SMALL_FONT;
		}
		
		int slices = labelCount + 1;
		int currentLabel = 1;
		if (showActionLabels) {
			currentLabel++;
		}
		if (showPreLispCheckLabels) {
			currentLabel++;
		}
		int oneSlice = GraphConstants.PERMILLE / (slices);
		for (Iterator<RuleLabel> rules = ruleList.iterator(); rules.hasNext();) {
			RuleLabel ruleLabel = (RuleLabel) rules.next();
			String text = ruleLabel.getText();
	 //       Rectangle2D ruleLabelBounds = drawLabel(g, oneSlice * currentLabel, text, null, Color.black, ruleLabel.getFont(), jgraphClipBounds);
			Rectangle2D ruleLabelBounds = drawLabel(g, oneSlice * currentLabel, text, null, Color.black, font2, jgraphClipBounds);
	        edgeView.addRuleBound(ruleLabelBounds);
			currentLabel++;
		}
		
    
	}

	/**
	 * Determine whether this edge has been visited. Uses the new example tracer
	 * {@link ExampleTracerTracer}. For traversable links, uses
	 * {@link ExampleTracerTracer#getInterpretations()}. For buggy links, uses
	 * {@link ExampleTracerEvent#getReportableLink()}. 
	 * @param exampleTracer
	 * @return
	 */
	private boolean edgeIsVisited(ExampleTracerTracer exampleTracer) {
		boolean visited = false;
		ProblemEdge problemEdge = edgeView.getProblemEdge();
		visited = exampleTracer.isLinkVisited(problemEdge.getUniqueID());
//		trace.outNT("demo", "link["+problemEdge.getUniqueID()+"] visited "+visited);
		return visited;
	}

	private void drawActionLabel(Graphics g, int labelCount, Rectangle jgraphClipBounds) {
//		trace.out(" drawActionLabel labelCount = " + labelCount);
        int location = GraphConstants.PERMILLE / (1 + labelCount);
        final String label = edgeView.getProblemEdge().getEdgeData().getActionLabel().getText();

		Font font2 = edgeView.getProblemEdge().getEdgeData().getActionLabel().getFont();
		ExampleTracerTracer exampleTracer = edgeView.getProblemEdge().getController().getExampleTracer();
		
		
		if (PseudoTutorMessageHandler.USE_NEW_EXAMPLE_TRACER) {
			boolean visited = edgeIsVisited(exampleTracer);		
			if (visited)
				font2 = BRPanel.VISITED_EDGE_FONT;
			else if (edgeView.isMouseOver())
				font2 = BRPanel.NORMAL_FONT;
			else
				font2 = BRPanel.SMALL_FONT;
		}

		Color textColor = edgeView.getProblemEdge().getActionLabel().getForeground();
        
		Rectangle2D actionLabelBounds = drawLabel(g, location, label, null, textColor, font2, jgraphClipBounds);
		edgeView.setActionLabelBounds(actionLabelBounds);

	}

	private Rectangle2D drawLabel(Graphics g, int lineLocation, final String label, ImageIcon icon, Color textColor, Font font, Rectangle jgraphClipBounds) {
		final Point2D labelPosition2 = getLabelPosition(new Point(lineLocation, 0));
		g.setFont(font);

		Graphics2D g2 = (Graphics2D) g;
        final double x2 = labelPosition2.getX();
        final double y2 = labelPosition2.getY();
        Rectangle2D stringBounds = getFontMetrics(font).getStringBounds(label, g);
        int newX = (int) (stringBounds.getX() + x2 - stringBounds.getWidth() / 2);
		int newY = (int) (stringBounds.getY() + y2);
		int iconWidth = 0;
		if (icon != null)
			iconWidth = icon.getIconWidth();
		Rectangle2D labelBounds = getClipBounds(stringBounds, newX, newY, iconWidth);
		
		labelBounds = labelBounds.createIntersection(jgraphClipBounds);
        g.setClip(labelBounds);
        g2.setColor(new Color(248,248,248)); // light gray label background
        
        g2.fill(labelBounds);
        g2.setStroke(lineStroke);
        g2.setColor(Color.GRAY); //label border color
        g2.draw(labelBounds);
        g2.setColor(textColor);
        g2.drawString(label,(float) (x2 - stringBounds.getWidth() / 2) + iconWidth / 2, (float) y2);
        drawIcon(g, icon, labelBounds);

		return labelBounds;
	}

	private void drawIcon(Graphics g, ImageIcon icon, Rectangle2D actionLabelBounds) {
		if (icon == null)
			return;
		
		int iconX = (int) actionLabelBounds.getMinX();
		int iconY = (int) (actionLabelBounds.getMinY() + 
				(actionLabelBounds.getHeight() - icon.getIconHeight()) / 2);

		g.drawImage(icon.getImage(), 
				iconX, 
				iconY, 
				null);
	}

	private Rectangle2D getClipBounds(Rectangle2D stringBounds, int newX, int newY, int iconWidth) {
		Rectangle2D clipBounds = new Rectangle2D.Float (newX,newY,(int) stringBounds.getWidth(),(int) stringBounds.getHeight());
		int pad = 3;
		Rectangle2D newclipBounds = new Rectangle2D.Double (
				clipBounds.getX() - pad - iconWidth / 2, 
				clipBounds.getY() - pad,
				clipBounds.getWidth() + pad * 2 + iconWidth, 
				clipBounds.getHeight() + pad * 2 );
		return newclipBounds;
	}
    
    public Component getRendererComponent(
            JGraph graph,
            CellView view,
            boolean sel,
            boolean focus,
            boolean preview) {
        edgeView = (BR_JGraphEdgeView) view;
        return super.getRendererComponent(graph, view, sel, focus, preview);
    }
}