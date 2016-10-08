package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel.GraphModelEdit;
import org.jgraph.graph.DefaultGraphModel.GraphModelLayerEdit;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.plaf.basic.BasicGraphUI;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphTreeLayout;

import edu.cmu.pact.BehaviorRecorder.Controller.ActionLabelHandler;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.BehaviorRecorder.View.CheckLispLabel;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Utilities.trace;


public class BR_JGraph extends JGraph implements KeyListener, GraphModelListener {

	private static final long serialVersionUID = 8334138326832536564L;

	private BR_JGraphNode rootCell;
    
    private JGraphTreeLayout jgraphTreeLayout;
    
    private GraphLayoutCache graphView;
    
    private BR_Controller brController;
    
    private Rectangle clip;

    private boolean ignoreGraphModelEvents = false;
    
    
    /* MouseHandler Flags:
     * A lot of the code in this class interprets author actions as a series of
     * mouse actions (click,drag,release,etc.) over a number of jgraph cells 
     * (which correspond to edges/nodes).
     * 
     * These flags are used to represent the series of actions the author performed. 
     */
    
    private ProblemEdge problemEdge;
    
    private Object mousePressedCell;
    
//    private boolean mouseRightClicked;
    
    private Point mouseDownPoint; 
    
    private DefaultGraphCell mouseOverCell;
    
    private ProblemNode mergeCandidate;   
    
    private ProblemNode currentVisitNode;
    
  //  private boolean mouseWasDragged;
    
    /* End MouseHandler Flags
     */
    
    //Used to record Node movement and to facilitated the placement
    //of undo checkpoints.
    private int startX = -1, startY = -1;
    
    
    public PseudoTutorMessageHandler pseudoTutorMessageHandler;
    
    int keyMask;
    
    
    

    //private static String actionLabelToolTipText = "<html><b>Action Label:</b> Click to edit.";
    
    //private static String skillNameToolTipText = "<html><b>Skill Name:</b><br>Click to edit.";
    
    public BR_JGraph(GraphModel graphModel, GraphLayoutCache graphView,
		     BR_Controller brController) {
	super(graphModel, graphView);
	this.graphView = graphView;
	this.graphModel = graphModel;
	this.brController = brController;
	ToolTipManager.sharedInstance().registerComponent(this);
	setLayout();
	//		setXorEnabled(false);  sewall 8/24/07: revert to previous jgraph layout
	setOpaque(false);
	
	if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
	    keyMask = KeyEvent.META_DOWN_MASK;
	else
	    keyMask = KeyEvent.CTRL_DOWN_MASK;
	
    }
    
    // Return Cell Label as a Tooltip
    public String getToolTipText(MouseEvent e) {
	
	String tooltipText="";
	String click = "Right-click";
	if (System.getProperty("os.name").startsWith("Mac"))
	    click = "Ctrl-click";
	
	if (e != null) {
	    // Fetch Cell under Mousepointer
	    Object c = getFirstCellForLocation(e.getX(), e.getY());
	    if (c != null) {
		if (mouseOverCell != null && mouseOverCell instanceof BR_JGraphEdge) {
		    problemEdge = ((BR_JGraphEdge) mouseOverCell).getProblemEdge();
		    tooltipText = problemEdge.getEdgeData().getTooltipText();
		} else
		    tooltipText = "<html><b>Problem State \""
			+ convertValueToString(c)
			+ "\"</b><br>"
			+ click
			+ " to edit, drag to move, click to go to state.</html>";
		return tooltipText;
	    }
	}
	return null;
    }
    
    public void updateUI() {
	setUI(new BasicGraphUI());
	invalidate();
	
	addMouseListener();
	addKeyListener(this);
	addMouseMotionListener();
	setHandleColor(Color.RED);
    }
    
    private void addMouseMotionListener() {
	addMouseMotionListener(new MouseMotionListener() {
		public void mouseDragged(MouseEvent e) {
		    handleMouseDragged(e);
		}
		
		public void mouseMoved(MouseEvent e) {
		    handleMouseMoved(e);
		}
	    });
	}

    protected void handleMouseDragged(MouseEvent e) {
		
		mergeCandidate = null;
		if (mousePressedCell == null)
		    return;
		if (!(mousePressedCell instanceof BR_JGraphNode))
		    return;
		
		BR_JGraphNode sourceNode = (BR_JGraphNode) mousePressedCell;
		
		int x = e.getX();
		int y = e.getY();
		List list = getAllCellsForLocation(x, y);
		
		if (list.size() == 0)
		    return;
		
		Object topCell = list.get(0);
		if (!(topCell instanceof BR_JGraphNode))
		    return;
		
		BR_JGraphNode targetNode = (BR_JGraphNode) topCell;
		ProblemNode targetProblemNode = (targetNode).getProblemNode();
		ProblemNode sourceProblemNode = sourceNode.getProblemNode();
		
		boolean validCandidate 
		    = brController.getProblemModel().canNodesBeMerged(targetProblemNode, 
						    sourceProblemNode, 
						    brController);
		
		if (!validCandidate)
		    return;
		
		mergeCandidate = targetProblemNode;
		
		BR_JGraphVertexView view = (BR_JGraphVertexView) graphView.getMapping(
										      targetNode, false);
		
		graphView.update(view);
		repaint();
    }
    
    private List getAllCellsForLocation(int x, int y) {
		List list = new ArrayList();
		//DefaultGraphCell tempy;
		Object cell = getFirstCellForLocation(x, y);
		Object topmostCell = cell;
		while (cell != null) {
		    list.add(cell);
		    cell = getNextCellForLocation(cell, x, y);
		    if (topmostCell == cell)
			break;
		}
		return list;
    }
    
    protected void handleMouseMoved(MouseEvent e) {
		Object[] selectedEdge = new Object[1];
		Object cell = getFirstCellForLocation(e.getX(), e.getY());
		if (mouseOverCell != null && mouseOverCell instanceof BR_JGraphEdge) {
		    selectedEdge[0] = mouseOverCell;
		    toFront(selectedEdge);
		}
		mouseOverCell = (DefaultGraphCell) cell;
		repaint();
    }
    
    /**
     * 
     */
    private void addMouseListener() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
			    handleMousePressed(arg0);
			}
			
			public void mouseReleased(MouseEvent arg0) {
				handleMouseReleased(arg0);
			}
			
			public void mouseClicked(MouseEvent e) {
			    handleMouseClicked(e);
			}
		    });
    }
    
    protected void handleMouseClicked(MouseEvent e) {
		/**
		 * A class to move click handling farther back on the event queue. 
		 */
		class ClickHandler implements Runnable {
		    private final MouseEvent e;
		    	ClickHandler(MouseEvent e) {
		    	this.e = e;
		    }
		    public void run() {
				Object cell = getFirstCellForLocation(e.getX(), e.getY());
				if (trace.getDebugCode("jgraph")) trace.out("jgraph", " << cell = " + cell 
					  + " x: " + e.getX() 
					  + " y: " + e.getY());
				if (cell == null) {
				    return;
				} else if (cell instanceof BR_JGraphNode) {
				    BR_JGraphNode node = (BR_JGraphNode) cell;
				    if (wasRightClick(e)){
				    	NodeView.evaluatePopup(e, node.getProblemNode(),brController, true);
				    	mousePressedCell = null;
				    	mouseDownPoint = null;
				    }
				}else if (cell instanceof BR_JGraphEdge) {
				    if (trace.getDebugCode("jgraph")) trace.out("jgraph", "It is an edge");
				    if (trace.getDebugCode("jgraph")) trace.out("jgraph", "cell = " + cell 
					      + " x: " + e.getX() 
					      + " y: " + e.getY());
				    
				    BR_JGraphEdge edge = (BR_JGraphEdge) cell;
				    BR_JGraphEdgeView view = (BR_JGraphEdgeView) graphView.getMapping(
												      edge, false);
				    view.doClick(e);
				} 
		    }
		}
		Runnable clickHandler = new ClickHandler(e);
		SwingUtilities.invokeLater(clickHandler);
    }
    
    /**
     * Tell whether a MouseEvent represents a right-click.
     * @param e the mouse event
     * @return true if button either was {@link MouseEvent#BUTTON3} or
     *         was {@link MouseEvent#BUTTON1} or with the Control key
     */
    public static boolean wasRightClick(MouseEvent e) {
	int button = e.getButton();
	if (trace.getDebugCode("jgraph")) trace.out("jgraph", "button = " + button);
	return  (button == MouseEvent.BUTTON3) ||
	    (button == MouseEvent.BUTTON1 && e.isControlDown());
    }
    
    public JGraphTreeLayout getTreeLayout() {
	return jgraphTreeLayout;
    }
    
    private void setLayout() {
	
	Object[] roots = new Object[1];
	roots[0] = rootCell;
	jgraphTreeLayout = new JGraphTreeLayout();
	
	// Set up facade to process tree layout
	jgraphTreeLayout.setOrientation(SwingConstants.NORTH);
	jgraphTreeLayout.setAlignment(SwingConstants.CENTER);
	jgraphTreeLayout.setPositionMultipleTrees(false);
	jgraphTreeLayout.setLevelDistance(60);
    }
    
    /**
     * 
     */
    public void runTreeLayout(Object rootCell) {
	Object[] roots = new Object[1];
	roots[0] = rootCell;
	JGraphFacade facade = new JGraphFacade(this, roots);
	getTreeLayout().run(facade);
	Map nested = facade.createNestedMap(true, false);
	
	graphView.edit(nested);
    }
    
    private void handleMousePressed(MouseEvent e) {
    	if ((e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) == MouseEvent.ALT_DOWN_MASK)
    		return;
    	//int i;
    	mousePressedCell = getFirstCellForLocation(e.getX(), e.getY());
    	if (trace.getDebugCode("jgraph")) trace.out("jgraph", "mouse pressed cell: " + mousePressedCell);
    	mouseDownPoint = new Point(e.getX(), e.getY());
    	
    	//records current (x,y) position of mouse-over node
    	if (mousePressedCell != null && mousePressedCell instanceof BR_JGraphNode)
    	{
    		Point loc = getLocation((BR_JGraphNode)mousePressedCell);
    		startX = (int)loc.getX();
    		startY = (int)loc.getY();
    	}
    }
    
    private void handleMouseReleased(MouseEvent e) {
    	//Whether or not a merge operation will be performed
    	boolean merge = false;
    	
	    Point mouseUpPoint = new Point(e.getX(), e.getY());
	//	boolean changed = false;
	  //  if(mousePressedEdge!=null && mouseWasDragged){
		//	changed = processEdgeBeingDragged(e);
	//	}
	 //   if(changed)
	   // 	return;
		if (trace.getDebugCode("jgraph")) trace.out("jgraph", "mouse released: mousepressedCell = " + mousePressedCell);
		
		if (wasRightClick(e))
		    return;
		if (mousePressedCell == null) { return; }
	
		if (!(mousePressedCell instanceof BR_JGraphNode)) { return; }
	
		BR_JGraphNode node = (BR_JGraphNode) mousePressedCell;
		currentVisitNode = (ProblemNode) node.getUserObject();
		
		currentVisitNode.getNodeView().setLocation(mouseUpPoint);
		
		
		if (!wasMouseMoved(mouseDownPoint, mouseUpPoint)) {
		    brController.problemNodeClicked(currentVisitNode);
		    return;
		}
		
		//		brController.getJGraphWindow().getJGraph().runTreeLayout( currentVisitNode.getJGraphNode());
		
		Set descendants = Utils.findDescendants(mousePressedCell, graphView,
							graphModel);
		
		if (!e.isMetaDown()) moveTree(descendants, mouseDownPoint, mouseUpPoint);
		
		if (mergeCandidate != null) {
			
			merge = true;
			brController.mergeStates2(node.getProblemNode(), mergeCandidate, true, true,
										mergeCandidate.hasOutGoingPreferredEdge(null));
		    mergeCandidate = null;
		}
		
		if (mousePressedCell != null && mousePressedCell instanceof BR_JGraphNode && !merge)
		{
			final BR_JGraphNode jgraphNode = (BR_JGraphNode)mousePressedCell;
			
			//Checks for undo condition
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					Point loc = getLocation(jgraphNode);
					int endX = (int)loc.getX();
					int endY = (int)loc.getY();

					if (endX != startX || endY != startY)
					{
						//Undo checkpoint for Moving Node ID: 1337
						ActionEvent ae = new ActionEvent(this, 0, "Move Node");
						brController.getUndoPacket().getCheckpointAction().actionPerformed(ae);
					}
				}	
			});
		}


    }
    
    /**
     * Tell whether the mouse moved. Reports false if the differences of the
     * X and Y coordinates of the points, respectively, are both less than
     * a tolerance delta. which is currently set to 10.
     * @param dn mouse down point
     * @param up mouse up point
     * @return true if points' coordinates' difference is greater than tolerance 
     */
    private boolean wasMouseMoved(Point dn, Point up) {
	int D = 10;  // tolerance delta
	int dx = Math.abs(dn.x - up.x);
	int dy = Math.abs(dn.y - up.y);
	return (dx > D || dy > D);
    }
    
    private void moveTree(Set descendants, Point mouseDownPoint,
			  Point mouseUpPoint) {
	double deltaX = mouseUpPoint.getX() - mouseDownPoint.getX();
	double deltaY = mouseUpPoint.getY() - mouseDownPoint.getY();
	final Map nestedAttributes = new HashMap();
	Iterator i = descendants.iterator();
	while (i.hasNext()) {
		
	    DefaultGraphCell cell = (DefaultGraphCell) i.next();
	    Object[] cells = new Object[1];
	    cells[0] = cell;
	    CellView[] cellViews = graphView.getMapping(cells);
	    Map attributeMap = new HashMap();
	    Rectangle2D bounds = cellViews[0].getBounds();
	    Rectangle2D bounds2 
		= new Rectangle2D.Double(bounds.getX() + deltaX, 
					 bounds.getY() + deltaY, 
					 bounds.getWidth(), 
					 bounds.getHeight());
	    GraphConstants.setBounds(attributeMap, bounds2);
	    nestedAttributes.put(cells[0], attributeMap);
	}
	
	graphView.edit(nestedAttributes);
    }
    
    public void paint(Graphics g) {
	this.clip = g.getClipBounds();
	if (graphView != null)
	    graphView.update(graphView.getRoots());
	super.paint(g);
    }
    
    public Rectangle getClipBounds() {
	return clip;
    }
    
    public DefaultGraphCell getMouseOverCell() {
	return mouseOverCell;
    }
    
    public MarathonElement getMarathonElement(Point location) {
	Object firstCellForLocation = getFirstCellForLocation(location.getX(),
							      location.getY());
	if (firstCellForLocation instanceof BR_JGraphNode)
	    return (MarathonElement) firstCellForLocation;
	if (firstCellForLocation instanceof BR_JGraphEdge)
	    return ((BR_JGraphEdge) firstCellForLocation).getMarathonElement(location);
	return null;
    }
    
    public MarathonElement getMarathonElement(String uniqueIdentifier) {
	Object[] roots = getRoots();
	for (int i = 0; i < roots.length; i++) {
	    Object root = roots[i];
	    if (root instanceof MarathonElement) {
		String marathonIdentifier = ((MarathonElement) root)
		    .getMarathonIdentifier();
		if (marathonIdentifier.equals(uniqueIdentifier))
		    return (MarathonElement) root;
	    }
	    if (root instanceof BR_JGraphEdge) {
		BR_JGraphEdge edge = (BR_JGraphEdge) root;
		if (edge.matchesMarathonIdentifier(uniqueIdentifier))
		    return edge.getMarathonElement(uniqueIdentifier);
	    }
	}
	
	return null;
    }
    
    public Point getMarathonElementLocation(MarathonElement marathonElement,
            String uniqueIdentifier) {

        if (marathonElement instanceof BR_JGraphNode) {
            BR_JGraphNode node = (BR_JGraphNode) marathonElement;
            CellView nodeView = graphView.getMapping(node, true);
            Rectangle2D bounds = nodeView.getBounds();
            int BoundsX = (int) bounds.getX() + (int) bounds.getWidth() / 2;
            int BoundsY = (int) bounds.getY() + (int) bounds.getHeight() / 2;
            Point p = new Point(BoundsX, BoundsY);
            return p;
        }

        if (marathonElement instanceof BR_JGraphEdge) {
            BR_JGraphEdge edge = (BR_JGraphEdge) marathonElement;
            CellView edgeView = graphView.getMapping(edge, true);
            Rectangle2D bounds = edgeView.getBounds();
            int BoundsX = (int) bounds.getX() + (int) bounds.getWidth() / 2;
            int BoundsY = (int) bounds.getY() + (int) bounds.getHeight() / 2;
            Point p = new Point(BoundsX, BoundsY);
            return p;
        }

        if (marathonElement instanceof ActionLabel) {
            ActionLabel actionLabel = (ActionLabel) marathonElement;
            BR_JGraphEdge graphEdge = actionLabel.getEdge().getEdge()
                    .getJGraphEdge();
            BR_JGraphEdgeView edgeView = (BR_JGraphEdgeView) graphView
                    .getMapping(graphEdge, true);
            Rectangle2D bounds = edgeView.getActionLabelBounds();
            int BoundsX = ((int) bounds.getX() + (int) bounds.getWidth() / 2);
            int BoundsY = ((int) bounds.getY() + (int) bounds.getHeight() / 2);
            Point p = new Point(BoundsX, BoundsY);
            return p;
        }

        if (marathonElement instanceof RuleLabel) {
            BR_JGraphEdge foundEdge = null;
            Object[] roots = getRoots();
            for (int i = 0; i < roots.length; i++) {
                Object root = roots[i];
                if (root instanceof BR_JGraphEdge) {
                    BR_JGraphEdge edge = (BR_JGraphEdge) root;
                    if (edge.matchesMarathonIdentifier(uniqueIdentifier)) {
                        foundEdge = edge;
                        break;
                    }
                }
            }

            BR_JGraphEdgeView edgeView = (BR_JGraphEdgeView) graphView
                    .getMapping(foundEdge, true);
            int ruleIndex = foundEdge.getRuleIndex(uniqueIdentifier);
            Rectangle2D bounds = (Rectangle2D) edgeView.getRuleBoundsList()
                    .get(ruleIndex);
            int BoundsX = ((int) bounds.getX() + (int) bounds.getWidth() / 2);
            int BoundsY = ((int) bounds.getY() + (int) bounds.getHeight() / 2);
            Point p = new Point(BoundsX, BoundsY);

            return p;
        }

        if (marathonElement instanceof CheckLispLabel) {
            BR_JGraphEdge foundEdge = null;
            Object[] roots = getRoots();
            for (int i = 0; i < roots.length; i++) {
                Object root = roots[i];
                if (root instanceof BR_JGraphEdge) {
                    BR_JGraphEdge edge = (BR_JGraphEdge) root;
                    if (edge.matchesMarathonIdentifier(uniqueIdentifier)) {
                        foundEdge = edge;
                        break;
                    }
                }
            }
            BR_JGraphEdgeView edgeView = (BR_JGraphEdgeView) graphView
                    .getMapping(foundEdge, true);
            Rectangle2D bounds = (Rectangle2D) edgeView
                    .getLispCheckLabelBounds();
            int BoundsX = ((int) bounds.getX() + (int) bounds.getWidth() / 2);
            int BoundsY = ((int) bounds.getY() + (int) bounds.getHeight() / 2);
            Point p = new Point(BoundsX, BoundsY);

            return p;
        }
        return null;
    }
    
    // Brings the Specified Cells to Front
    public void toFront(Object[] c) {
	
	getGraphLayoutCache().toFront(c);
    }
    
    // Sends the Specified Cells to Back
    public void toBack(Object[] c) {
	getGraphLayoutCache().toBack(c);
    }
    
    
    
    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
	//		trace.out("Key released:  = " + e);
    }
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
	//		trace.out("Key typed:  = " + e);
    }
    
    /** Handle the key pressed event from the text field. */        
    public void keyPressed(KeyEvent e) {
    	if(trace.getDebugCode("key"))
    		trace.out("key", "BR_JGraph.keyPressed("+e+") mousePressedCell "+trace.nh(mousePressedCell));
    	
		if (mousePressedCell == null)			return;
		
		if (mousePressedCell instanceof BR_JGraphNode) {
			if (e.getModifiersEx() == keyMask) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) { // VK_UP = 38 VK_DOWN = 40
					//			trace.out("MoveToNextStepOnPreferredPath");
					;
					//				currentVisitNode = brController
					//						.MoveToNextStepOnPreferredPath(currentVisitNode);

				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					//			trace.out("MoveToPrevStepOnPreferredPath");
					currentVisitNode = brController.MoveToPrevStepOnPreferredPath(currentVisitNode);
				}
			}
		}
		
		if(mousePressedCell instanceof BR_JGraphEdge) {
			ProblemEdge edge = ((BR_JGraphEdge) mousePressedCell).getProblemEdge();
			if(edge == null || edge.getActionHandler() == null)
				return;
			String actionCommand = null;
			int mask = (keyMask | KeyEvent.SHIFT_DOWN_MASK);
			if((e.getModifiersEx() & mask) == mask) {
				if(e.getKeyCode() == KeyEvent.VK_C)
					actionCommand = ActionLabelHandler.COPY_LINK;
				else if(e.getKeyCode() == KeyEvent.VK_V)
					actionCommand = ActionLabelHandler.PASTE_LINK;
			}
			if(trace.getDebugCode("key"))
				trace.out("key", String.format("BR_JGraph.keyPressed() mods 0x%x, mask 0x%x,"+
						" keyCode 0x%x, C 0x%x, V 0x%x, actionCommand %s", e.getModifiersEx(), mask,
						e.getKeyCode(), KeyEvent.VK_C, KeyEvent.VK_V, actionCommand));
			if(actionCommand != null) {
				edge.getActionHandler().actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_FIRST, actionCommand));
			}
		}
    }


	/* This method is currently used only for processing edge rewiring from jgraph.
	 * An edge rewiring is assumed to have happened if a GraphModelEvent occurs
	 * with 1 change to a BR_JGraphEdge. It has only been rewired if the source/target
	 * nodes of the change br_jgraphedge are out of sync with the source/target nodes 
	 * in the ProblemModel.
	 *
	 */
	public void graphChanged(GraphModelEvent e) {
		//if(true)return;'=
		if(ignoreGraphModelEvents){//A rewiring is currently being processed
			return;
		}
		if(e.getChange() instanceof GraphModelEdit){
			Object[] changes = ((GraphModelEdit)e.getChange()).getChanged();
			boolean sourceChanged = false;
			boolean destChanged = false;
			//System.out.println("START");
			//int i;
		//	System.out.println();
		//	for(i = 0; i < changes.length; i++){
		//		System.out.print(changes[i].getClass().toString()+", ");
		//	}
			if((changes.length == 1) && (changes[0] instanceof BR_JGraphEdge)){
				//	System.out.println("bredge");
					boolean resetToPrevious = false;
					String errorMsg =null;
					boolean addNewEdgeToCTAT = false;
					BR_JGraphEdge edge = (BR_JGraphEdge)changes[0];
					
					ProblemNode sourceFromPE = edge.getProblemEdge().getSource();
					ProblemNode destFromPE = edge.getProblemEdge().getDest();
					//edge.getProblemEdge().getde
					ProblemNode sourceFromPort = null;
					ProblemNode destFromPort = null;
					//the following line is testing if the edge was dropped without a new node attachment
					if(((edge.getSource()!=null)&&(edge.getTarget()!=null)))
					{	
						sourceFromPort = ((BR_JGraphNode)((DefaultPort)(edge.getSource())).getParent()).getProblemNode();
						destFromPort = ((BR_JGraphNode)((DefaultPort)(edge.getTarget())).getParent()).getProblemNode();
						if(sourceFromPE!=sourceFromPort)
							sourceChanged = true;
						if(destFromPE!=destFromPort)
							destChanged = true;
						if(sourceChanged||destChanged){//drag and dropped..
							
							addNewEdgeToCTAT = true;
							if(sourceChanged)
								errorMsg = brController.getProblemModel().testNewSourceNodeForLink(edge.getProblemEdge(), sourceFromPort);
							else
								errorMsg = brController.getProblemModel().testNewDestNodeForLink(edge.getProblemEdge(), destFromPort);
							if(errorMsg==null){//acceptable drag and drop
								resetToPrevious = false;
							}else{
								resetToPrevious = true;//bad drag and drop
								
							}
						}else{
							addNewEdgeToCTAT = false;
						}
					}else {
						resetToPrevious = true;
					}
					if(resetToPrevious){
						BR_JGraphEdge edges[] = new BR_JGraphEdge[1];
						edges[0] = edge;
						
						if((errorMsg!=null)&&(!errorMsg.equalsIgnoreCase("ignore")))
	            			JOptionPane.showMessageDialog(this, errorMsg);
						System.out.println("DONE");
						System.out.flush();
						ignoreGraphModelEvents = true;
						graphView.remove(edges);
			        	edge.setSource(sourceFromPE.getJGraphNode().getChildAt(0));
			        	edge.setTarget(destFromPE.getJGraphNode().getChildAt(0));
			        	edges[0] = edge;
			        	graphView.insert(edges);
			        	brController.getJGraphWindow().repaint();
					}else if (addNewEdgeToCTAT){
						BR_JGraphEdge edges[] = new BR_JGraphEdge[1];
						edges[0] = edge;
						ignoreGraphModelEvents = true;
						if(sourceChanged)
			        		brController.changeEdgeSourceNode(edge.getProblemEdge(), sourceFromPort);
			        	else
			        		brController.changeEdgeDestNode(edge.getProblemEdge(), destFromPort);
						System.out.println();
					}
			}
			
		}
		
		if(e.getChange() instanceof GraphModelLayerEdit){
			
		}
		ignoreGraphModelEvents = false;
	}
	
	//ID: 1337
	private Point getLocation(BR_JGraphNode node)
	{
		if (node == null)
		{
			System.out.println("null parameter to getLocation() in BR_JGraph");
			return new Point(0,0);
		}
		//Coordinates of node
		int x, y;
		GraphLayoutCache graphView = brController.getJGraphWindow().getJGraphController().getGraphView();
		BR_JGraphVertexView jgraphNodeView = (BR_JGraphVertexView) graphView.getMapping(node, false);
		Rectangle2D rect = jgraphNodeView.getBounds();
		
		x = (int)rect.getX();
		y = (int)rect.getY();
		
		return new Point(x,y);
	}
	
	
	
	
}
