package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent.SetCurrentNodeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.BRDLoadedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ChangeCurrentNodeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeColorEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeRewiredEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NewProblemEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.HashMap;

public class JGraphController implements ProblemModelListener, CtatModeListener {
    
    /**
     * A class to invoke {@link JGraphController#graphView}.edit(Map) on
     * another thread.
     */
    private class InvokeGraphLayoutCacheEdit implements Runnable {
    	private final Map nestedAttributes;
    	/**
    	 * Set the map argument for {@link GraphLayoutCache#edit(Map)}.
    	 * @param nestedAttributes
    	 */
    	InvokeGraphLayoutCacheEdit(Map nestedAttributes) {
	    this.nestedAttributes = nestedAttributes;
    	}
    	/**
    	 * Invoke {@link GraphLayoutCache#edit(Map)}.
    	 * @see java.lang.Runnable#run()
    	 */
	public void run() {
	    if (trace.getDebugCode("br")) trace.outNT("br", "InvokeGraphLayoutCacheEdit.run() on graphView "+graphView+
			", keySet "+nestedAttributes.keySet());
	    for (Object cell : nestedAttributes.keySet()) {
		if (!(cell instanceof BR_JGraphNode))
		    continue;
		if (trace.getDebugCode("br")) trace.outNT("br", " "+cell+" old "+((BR_JGraphNode) cell).getAttributes());
		if (trace.getDebugCode("br")) trace.outNT("br", " "+cell+" new "+nestedAttributes.get(cell));
	    }
	    graphView.edit(nestedAttributes);
	}
    }

    private BR_Controller brController;
    private JGraphPanel jgraphWindow;
    private GraphLayoutCache graphView;
    //private BR_JGraphNode startStateCell;
    private BR_JGraph jgraph;
    private GraphModel graphModel;

    final private double MINIMUM_ZOOM_SCALE = 0.00001;
  

    public JGraphController(BR_Controller controller,
			    JGraphPanel window, GraphModel graphModel,
			    GraphLayoutCache graphView) {
    	this.brController = controller;
    	this.jgraphWindow = window;
    	this.jgraph = window.getJGraph();
    	this.graphModel = graphModel;
    	brController.addCtatModeListener(this);
    	brController.getProblemModel().addProblemModelListener(this);
    	//brController.getClipboardModel().addProblemModelListener(this); // ADDED
    	this.graphView = graphView;
    }

    private boolean handlePMEventRecursive(ProblemModelEvent event, Set<DefaultEdge> selectedEdges) {

    	//BRDLoaded events don't recurse on each subevent, 
    	//but passes an array of edge/node created events to the appropriate handler.
    	if(event instanceof EdgeColorEvent){
    		handleEdgeColorEvent((EdgeColorEvent)event);
    		return false;
    	}
    	if(event instanceof BRDLoadedEvent){
    		ArrayList<ProblemModelEvent> nodeCreatedEvents = new ArrayList<ProblemModelEvent> (event.collectTypeSubevents(NodeCreatedEvent.class, true, true, true));
    		handleLoadedBRDNodes(nodeCreatedEvents);
    		ArrayList<ProblemModelEvent> edgeCreatedEvents = new ArrayList<ProblemModelEvent> (event.collectTypeSubevents(EdgeCreatedEvent.class, true, true, true));
    		handleLoadedBRDEdges(edgeCreatedEvents);
    		return true;
    	}
    	if (event instanceof NodeCreatedEvent) {
    		resetCopyNode = true;
    		handleNodeCreatedEvent(event);
    	}

    	if (event instanceof NodeUpdatedEvent) {
    		handleNodeUpdatedEvent((NodeUpdatedEvent) event);
    	}

    	if (event instanceof NodeDeletedEvent) {
    		resetCopyNode = true;
    		handleNodeDeletedEvent ((NodeDeletedEvent) event);
    	}

    	if (event instanceof ChangeCurrentNodeEvent) {
    		handleChangeCurrentNodeEvent ((ChangeCurrentNodeEvent) event);
    	}

    	if (event instanceof NewProblemEvent) {
    		resetCopyNode = true;
    		handleNewProblemEvent();
    	}
    	if(event instanceof EdgeRewiredEvent){
    		resetCopyNode = true;
    		EdgeRewiredEvent ere = (EdgeRewiredEvent)event;
    		Color resetColor = ere.getEdgeDeletedEvent().getEdge().getJGraphEdge().getEdgeColor();
    		handleEdgeDeletedEvent (ere.getEdgeDeletedEvent());
    		DefaultEdge edge = handleEdgeCreatedEvent (ere.getEdgeCreatedEvent(), resetColor);
    		if(ere.getEdgeCreatedEvent().isSelected())
    			selectedEdges.add(edge);
    	}
    	if (event instanceof EdgeCreatedEvent) {
    		resetCopyNode = true;
    		DefaultEdge edge = handleEdgeCreatedEvent ((EdgeCreatedEvent) event, null);
    		if(((EdgeCreatedEvent) event).isSelected())
    			selectedEdges.add(edge);
    	}

    	if (event instanceof EdgeUpdatedEvent) {
    		//resetCopyNode = true;
    		handleEdgeUpdatedEvent ((EdgeUpdatedEvent) event);
    	}

    	if (event instanceof EdgeDeletedEvent) {
    		resetCopyNode = true;
    		handleEdgeDeletedEvent ((EdgeDeletedEvent) event);
    	}
    	/* Put a change in here replacing the if line with 
   	   if (!(event instanceof ProblemModelLoadedEvent) 
   	      && (event.isCompoundEventP())) {
    	 */
    	/* Handle a compound event by recursively calling the 
    	 * command on all subevents. 
    	 * Isn't called if event instanceof BRDLoadedEvent
    	 * */
    	if (event.isCompoundEventP()) {
    		for (ProblemModelEvent E : event.getSubevents()) {
    			resetCopyNode = handlePMEventRecursive(E, selectedEdges) || resetCopyNode;
    		}
    	}

    	return resetCopyNode;
    }

    /**
     * Mark the given set of edges as selected.
     * @param selectedEdges
     */
    private void markEdgesSelected(Set<DefaultEdge> selectedEdges) {
    	if(trace.getDebugCode("mg"))
    		trace.out("mg", "JGraphCtlr.markEdgesSelected() nEdges "+selectedEdges.size());
    	DefaultEdge[] edges = new DefaultEdge[selectedEdges.size()];
    	selectedEdges.toArray(edges);
    	jgraph.setSelectionCells(edges);
	}
    
	/**
     * When an event is supplied this code will iteratively handle
     * the events by type recursing on subevents.  Note that this
     * code will, recurse on the subevents directly so if a single
     * event has multiple parents then it will get responded to 
     * on multiple occasions.  
     * The recursion might not be implemented for all events:
     * For the case of a BRDLoadedEvent, the subevents are handled by
     * handleLoadedBRDNodes and handleLoadedBRDEdges.
     * View code for handlePMEventOccured(PME event) for more details
     * 
     * note: This is simply a wrapper function to ensure repaint
     * is only called once, rather then X times after X recursions.
     */
    private boolean resetCopyNode;
    public void problemModelEventOccurred(ProblemModelEvent event) {
    	final Set<DefaultEdge> selectedEdges = new HashSet<DefaultEdge>(); 

    	resetCopyNode = false;
    	resetCopyNode = handlePMEventRecursive(event, selectedEdges);

   		if (!Utils.isRuntime()) {
   			if(resetCopyNode)
   				BR_Controller.setCopySubgraphNode(null);
   			SwingUtilities.invokeLater(new Runnable() {
   				public void run() {
   		   			final CellView[] roots = graphView.getRoots();
   		   			for (int i = 0; i < roots.length; i++) 
   		   				jgraph.updateAutoSize(roots[i]);

   		   			markEdgesSelected(selectedEdges);

   		   			jgraphWindow.repaint();
   		   			jgraphWindow.revalidate();   					
   				}
   			});
   		}
    }
	
    private void handleChangeCurrentNodeEvent(ChangeCurrentNodeEvent event) {
	ProblemNode oldNode = event.getOldProblemNode();
	ProblemNode newNode = event.getNewProblemNode();
	Map nestedAttributes = null;
		
	if (newNode != null)
	    nestedAttributes = setBold(newNode, true, nestedAttributes);
		
	if (oldNode != null && !oldNode.equals(newNode))
	    nestedAttributes = setBold(oldNode, false, nestedAttributes); 
			
	if (nestedAttributes != null)
	    SwingUtilities.invokeLater(new InvokeGraphLayoutCacheEdit(nestedAttributes));
    }

    private Map setBold(ProblemNode problemNode, boolean bold, Map nestedAttributes) {
    	DefaultGraphCell cell = (problemNode == null ? null : problemNode.getJGraphNode());
    	if (trace.getDebugCode("br")) trace.out("br", "setBold("+problemNode+", "+bold+") cell "+cell+", graphView "+graphView);
    	if (problemNode == null || cell == null || graphView == null)
	    return nestedAttributes;

	Object[] cells = new Object[1];
	cells[0] = cell;
	CellView[] cellViews = graphView.getMapping(cells);
	Map attributeMap = new HashMap();
	Font font;
	int thickness;
	final int padding = 2;

	if (bold) {
	    font = BRPanel.BOLD_FONT;
	    thickness = 2;
	} else {
	    font = BRPanel.NORMAL_FONT;
	    thickness = 1;
	}
	GraphConstants.setFont(attributeMap, font);
	GraphConstants.setBorder(attributeMap, BorderFactory
				 .createCompoundBorder(BorderFactory
						       .createLineBorder(Color.BLACK, thickness), BorderFactory
						       .createEmptyBorder(padding, padding, padding, padding)));
	GraphConstants.setResize(attributeMap, true);

	if (nestedAttributes == null)
	    nestedAttributes = new HashMap();
	nestedAttributes.put(cell, attributeMap);
	return nestedAttributes;
    }

    private void handleNodeUpdatedEvent(NodeUpdatedEvent event) {
    	ProblemNode problemNode = event.getNode();
    	BR_JGraphNode cell = (problemNode == null ? null : problemNode.getJGraphNode());
    	if (trace.getDebugCode("br")) trace.out("br", "handleNodeUpdatedEvent("+event+") problemNode "+
    			problemNode+", cell "+cell+", graphView "+graphView);
    	if (problemNode == null || cell == null || graphView == null)
    		return;
    	if (Utils.isRuntime())
    		return;

    	Object[] cells = new Object[1];
    	cells[0] = cell;
    	CellView[] cellViews = graphView.getMapping(cells);
    	Map attributeMap = new HashMap();
    	Rectangle2D bounds = cellViews[0].getBounds();
    	Rectangle2D bounds2 = new Rectangle2D.Double(bounds.getX(),
    			bounds.getY(), bounds.getWidth(), bounds.getHeight());
    	GraphConstants.setBounds(attributeMap, bounds2);
    	GraphConstants.setBackground(attributeMap, cell.getBackground());
    	Map nestedAttributes = new HashMap();
    	nestedAttributes.put(cells[0], attributeMap);		
    	SwingUtilities.invokeLater(new InvokeGraphLayoutCacheEdit(nestedAttributes));
    }



    private void handleNewProblemEvent() {
        graphModel.remove(DefaultGraphModel.getRoots(graphModel));
    }

    /** Deletes the Node, incoming edges, and any associated ports.
     * Not sure if deleting of the incoming edges is required since
     * CTAT throws an event for each edge that is deleted. Also,
     * am not sure why outgoing edges aren't deleted.
     * Note: the ports are deleted by getting all the children of the node.
     * If the ports aren't deleted it seems that JGRAPH won't do it for us
     * and can cause CTAT to bug out.
     **/
    
    private void handleNodeDeletedEvent(NodeDeletedEvent event) {
        final ProblemNode deletedNode = event.getNode();
        if (trace.getDebugCode("jgraph")) trace.out("jgraph", "Node Deleted Event: " + deletedNode);
        final BR_JGraphNode graphNode[] = new BR_JGraphNode[1];
        graphNode[0] = deletedNode.getJGraphNode();
        List cellsToRemove = graphView.getIncomingEdges(graphNode[0], null, true, true);
        cellsToRemove.add(graphNode[0]);
        Enumeration ports = graphNode[0].children();
        while(ports.hasMoreElements()){
        	cellsToRemove.add(ports.nextElement());
        }
        graphView.remove(cellsToRemove.toArray());
    }
    
    private void handleEdgeDeletedEvent(EdgeDeletedEvent event) {
    	BR_JGraphEdge edge[] = new BR_JGraphEdge[1];
            edge[0] = event.getEdge().getJGraphEdge();
            graphView.remove(edge);
    		
    }
	
    private void handleEdgeUpdatedEvent(EdgeUpdatedEvent event) {
		ProblemEdge problemEdge = event.getEdge();
		EdgeData edgeData = problemEdge.getEdgeData();
		DefaultEdge jg_edge = problemEdge.getJGraphEdge();
		    
		GraphConstants.setLineColor(jg_edge.getAttributes(), edgeData.getDefaultColor());
		if (!Utils.isRuntime())
		    graphView.editCell(jg_edge, jg_edge.getAttributes());
		    
    }
    private void handleLoadedBRDEdges(ArrayList<ProblemModelEvent> edgeCreatedEvents){
    	int i; 
    	ProblemEdge problemEdge;
    	
    	DefaultGraphCell[] cells = new DefaultGraphCell[edgeCreatedEvents.size()];
    	for(i=0; i < edgeCreatedEvents.size(); i++){
    		problemEdge = ((EdgeCreatedEvent)edgeCreatedEvents.get(i)).getEdge();
    		EdgeData edgeData = problemEdge.getEdgeData();
    		ProblemNode[] nodes = problemEdge.getNodes();
    		BR_JGraphNode source = nodes[0].getJGraphNode();
            BR_JGraphNode target = nodes[1].getJGraphNode();
            DefaultEdge jg_edge = new BR_JGraphEdge(problemEdge, this);
            
            jg_edge.setSource(source.getChildAt(0));
            jg_edge.setTarget(target.getChildAt(0));
            GraphConstants.setSelectable(jg_edge.getAttributes(), true);
            GraphConstants.setBendable(jg_edge.getAttributes(), false);
            GraphConstants.setEditable(jg_edge.getAttributes(), false);
            GraphConstants.setLineColor(jg_edge.getAttributes(), edgeData.getDefaultColor());
            cells[i] = jg_edge;
    	}
        if (!Utils.isRuntime())
        	graphView.insert(cells);
    }
    private void handleEdgeColorEvent(EdgeColorEvent event){
    	ProblemEdge edge = event.getEdge();
    	Color newColor = (event.getColor() == null ? edge.getEdgeData().getDefaultColor() : event.getColor());
    	BR_JGraphEdge jedge = edge.getJGraphEdge();
    	jedge.setEdgeColor(newColor);
    	GraphConstants.setLineColor(jedge.getAttributes(), newColor);
        if (!Utils.isRuntime())
        	graphView.editCell(jedge, jedge.getAttributes());
    }
    private DefaultEdge handleEdgeCreatedEvent(EdgeCreatedEvent event, Color resetColor) {
        ProblemEdge problemEdge = event.getEdge();
        ProblemNode[] nodes = problemEdge.getNodes();
        BR_JGraphNode source = nodes[0].getJGraphNode();
        BR_JGraphNode target = nodes[1].getJGraphNode();

        DefaultGraphCell[] cells = new DefaultGraphCell[1];
        DefaultEdge jg_edge = new BR_JGraphEdge(problemEdge, this);
        cells[0] = jg_edge;
        jg_edge.setSource(source.getChildAt(0));
        jg_edge.setTarget(target.getChildAt(0));
                
        GraphConstants.setSelectable(jg_edge.getAttributes(), true);
        GraphConstants.setBendable(jg_edge.getAttributes(), false);
        GraphConstants.setEditable(jg_edge.getAttributes(), false);
        if(resetColor!=null){
        	((BR_JGraphEdge) jg_edge).setEdgeColor(resetColor);
        	GraphConstants.setLineColor(jg_edge.getAttributes(), resetColor);
        } else if(problemEdge.getEdgeData().isTutorPerformed(null)) {
        	((BR_JGraphEdge) jg_edge).setEdgeColor(EdgeData.TPAColor);
        	GraphConstants.setLineColor(jg_edge.getAttributes(), EdgeData.TPAColor);
        }
        if (!Utils.isRuntime())
        	graphView.insert(cells);
//      jgraph.runTreeLayout(source);
        return jg_edge;
    }

    private void handleLoadedBRDNodes(ArrayList<ProblemModelEvent> nodeCreatedEvents){
      //  ProblemNode problemNode = ((NodeCreatedEvent) event).getNode();
    	
    	DefaultGraphCell[] cells = new DefaultGraphCell[nodeCreatedEvents.size()];
		int x,y,i;
		ProblemNode curr;
		for(i=0; i < nodeCreatedEvents.size(); i++){
			x = y = 0;
			curr = ((NodeCreatedEvent)nodeCreatedEvents.get(i)).getNode();
			if (!Utils.isRuntime()){        	
				Point location = curr.getNodeView().getLocation();
				x = location.x;
				y = location.y;
				trace.out ("jgraph", "x = " + x + " y = " + y);
			}
			BR_JGraphNode jgraphNode = new BR_JGraphNode(curr, x, y);  
			cells[i] = jgraphNode;
		} 
		if (!Utils.isRuntime())
			graphView.insert(cells);
    }
    /**
     * @param event
     */
    private void handleNodeCreatedEvent(ProblemModelEvent event) {

        ProblemNode problemNode = ((NodeCreatedEvent) event).getNode();
        if(trace.getDebugCode("br"))
        	trace.out("br", "handleNodeCreatedEvent() problemNode "+problemNode);

        boolean updateAuthorUI = (!Utils.isRuntime() && problemNode.getProblemModel().getController() != null);
        int x = 0,y = 0;
        if (updateAuthorUI)
	    {        	
        	Point location = problemNode.getNodeView().getLocation();
        	x = location.x;
        	y = location.y;
        	trace.out ("jgraph", "x = " + x + " y = " + y);
	    }
       	BR_JGraphNode jgraphNode = new BR_JGraphNode(problemNode, x, y);  

        DefaultGraphCell[] cells = new DefaultGraphCell[1];
        cells[0] = jgraphNode;
        
        if (updateAuthorUI)
        	graphView.insert(cells);
	//        jgraph.runTreeLayout(startStateCell);
    }


    public void ctatModeEventOccured(CtatModeEvent e) {
        // Nov. 23, 2007 :: Noboru
        // SimStudent running in the batch mode does not show CTAT windows. 
        if (System.getProperty("noCtatWindow") != null) return;
        
        if (e instanceof CtatModeEvent.RepaintEvent) {
            SwingUtilities.invokeLater(new Runnable() {
		    public void run() {

			jgraphWindow.repaint();

		    }
		} );
        } else if (e instanceof CtatModeEvent.SetCurrentNodeEvent) {
            setCurrentNode ((SetCurrentNodeEvent) e);
	  //  if (brController.getCurrentNode() != null)
		//jgraph.scrollPointToVisible(brController.getCurrentNode().getNodeView().getBottomCenterPoint()); 
//
        } 

    }

    private void setCurrentNode(SetCurrentNodeEvent event) {
    	// currently does nothing
    	/*
        ProblemNode node = event.newCurrentNode;
        BR_JGraphNode currentNode = node.getJGraphNode();
        ProblemNode node2 = event.previousCurrentNode;
        BR_JGraphNode previousNode = node2.getJGraphNode();

        
        Map nested = new HashMap();
        */
	//        Map attributeMap1 = new HashMap();
	//        Map attributeMap2 = new HashMap();
	//        
	//        GraphConstants.setFont(attributeMap1, BRPanel.FONT_BOLD);
	//        GraphConstants.setLineColor(attributeMap1, Color.green);
	//        nested.put(currentNode, attributeMap1);
	//
	//        GraphConstants.setFont(attributeMap2, BRPanel.FONT_NORMAL);
	//        nested.put(previousNode, attributeMap2);
	//        graphView.edit(nested);
    }

    public void zoomListActionPerfomed(String ZoomName) {
    	final double factor = Double.parseDouble(ZoomName.substring(0, ZoomName.length() -1)) / 100.00;       
        zoom(factor, false);
    }
    
    public void zoomOutActionPerfomed() {
        final double factor = 0.9d;
        zoom(factor, true);
    }

    public void zoomInActionPerfomed() {
        final double factor = 1.1d;
        zoom(factor, true);
        
    }

    /**
     * @param factor
     */
    private void zoom(final double factor, boolean relative) {
    	double scale;
    	if (relative)
	    {
		scale = jgraph.getScale();
		scale *= factor;
	    } else scale = factor;
    	
        // attempt to truncate the number so it's not so long
        // does not work perfectly
         
   
        double temp = scale % 0.0001d;
        scale -= temp;
        if (scale < MINIMUM_ZOOM_SCALE)
            scale = MINIMUM_ZOOM_SCALE;
        BR_JGraphNode currentNode;
        try {
            currentNode = brController.getSolutionState().getCurrentNode().getJGraphNode();
        } catch (NullPointerException e) {
            return;
        }
        CellView currentView = graphView.getMapping(currentNode, false);
        Rectangle2D bounds = currentView.getBounds();
        Point2D p = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
        trace.out ("jgraph", "p = " + p);
        jgraph.setScale(scale /* , p */);  // sewall 8/24/07: revert to previous jgraph layout
        
        jgraph.scrollCellToVisible(currentNode);
        jgraphWindow.repaint();
    }

    public DefaultGraphCell getMouseOverCell() {
	return jgraph.getMouseOverCell();
    }

    public GraphLayoutCache getGraphView() {
	return graphView;
    }

}
