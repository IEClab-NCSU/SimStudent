package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.Border;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.Dialogs.InsertSubgraphDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * Used as the visual representation of a ProblemNode in a ProblemGraph
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////
public class NodeView extends JTextField {
	private static final long serialVersionUID = 1179326355714681798L;

	private static final String RUN_TREE_LAYOUT = "Run Tree Layout";

	private String nodeName;

	private boolean locked = false;

	private Point incomingEdgePoint, outgoingEdgePoint;

	private int widthBuffer = 10;

	static public final int VERTEX_SEPERATION_DISTANCE = 20;

	private Border originalBorder, outlineBorder;

	private boolean pasteSubgraphFlag = true;

	private ProblemNode problemNode = null;


	private transient BR_Controller controller;
    
    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    }

	public void setPasteSubgraphFlag(boolean pasteSubgraphFlag) {
		this.pasteSubgraphFlag = pasteSubgraphFlag;
	}

	public boolean getPasteSubgraphFlag() {
		return this.pasteSubgraphFlag;
	}
	
	public void setProblemNode (ProblemNode problemNode) {
		this.problemNode = problemNode;
		if (problemNode != null)
			problemNode.setName(getText());
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public NodeView(BR_Controller controller) {
		this("state" + controller.getProblemModel().getNodeUniqueIDGenerator(), controller);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public NodeView(String nodeName, BR_Controller controller) {
		super(nodeName);
		this.controller = controller;
		setText(nodeName);
		this.nodeName = nodeName;
		setVisible(true);

		updateSize();
		setEditable(false);
		setBackground(Color.white);

		ProblemModel pm = controller.getProblemModel();
		pm.updateNodeUniqueIDGenerator(pm.getNodeUniqueIDGenerator()+1);
		setHorizontalAlignment(JTextField.CENTER);

		// 11-09-06 :: Noboru getting rid of Old BR

		updateToolTip();
		originalBorder = getBorder();

		outlineBorder = BorderFactory
				.createLineBorder(Color.blue.brighter(), 5);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	private void updateToolTip() {
		String click = "Right-click";
		if (System.getProperty("os.name").startsWith("Mac"))
			click = "Ctrl-click";

		setToolTipText("<html><b>Problem State \"" + getText() + "\"</b><br>"
				+ click
				+ " to edit, drag to move, click to go to state.</html>");

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public Point getCenterPoint() {
		Point upperLeft = getLocation();
		upperLeft.x += getWidth() / 2;
		upperLeft.y += getHeight() / 2;
		return upperLeft;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public Point getBottomCenterPoint() {
		Point upperLeft = getLocation();
		upperLeft.x += getWidth() / 2;
		upperLeft.y += getHeight();
		return upperLeft;
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isDoneState() {
		if (getText().startsWith("Done"))
			return true;
		return problemNode.getDoneState();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public void showOutline(boolean showOutline) {
		if (showOutline) {
			this.setBorder(outlineBorder);
		} else {
			this.setBorder(originalBorder);
		}
	}


	NodeView cloneVertex() {
		// Vertex newVertex = new Vertex(getText());
		// NodeView newVertex = new NodeView(controller);
	    NodeView newVertex;
		if (getText().startsWith("Done"))
			newVertex = new NodeView(controller.getProblemModel().nextDoneName() , controller);
		else 
			newVertex = new NodeView(controller);
			
		newVertex.setLocked(locked);

		newVertex.setDoneState(problemNode, isDoneState());

		return newVertex;
	}

	
	/**
	 * . recursive method to paste subgraph
	 * 
	 * @pasteNode the node to past subgraph
	 * @copyNode the node to copy subgraph
	 * @mapNodes list of copyNode and pastNode pairs
	 */
/*	private void pasteSubGraph(ProblemNode pasteNode, ProblemNode copyNode,
		Vector mapNodes) {
		if(copyNode==null)
			return;
		if(!controller.getExampleTracerGraph().isNodeReachable(copyNode.getUniqueID()))
			return;
		if(!controller.getExampleTracerGraph().isNodeReachable(pasteNode.getUniqueID()))
			return;
			// if the copy node has no child, return.
	    if (controller.getProblemModel().isLeaf(copyNode))
	    	return;

	    // loop through copyNode children
	    ProblemEdge edgeTemp;
	    ProblemEdge edgeMatchSAI;
	    ProblemNode nodeAdded;
	    ProblemNode copyChildNode;
	    Vector childEdgesList = new Vector();
	    Enumeration iter = controller.getProblemModel().getProblemGraph()
	    .getOutgoingEdges(copyNode);
	    while (iter.hasMoreElements())
		childEdgesList.addElement(iter.nextElement());

	    for (int i = childEdgesList.size() - 1; i >= 0; i--) {
		cloneEdgeOnlyFlag = false;

		edgeTemp = (ProblemEdge) childEdgesList.elementAt(i);
		copyChildNode = edgeTemp.getNodes()[ProblemEdge.DEST];
		nodeAdded = findMapNode(copyChildNode, mapNodes);
		
        if (copyChildNode.equals(pasteNode)) continue;  // temp fixed to pass CTAT 1336 - pastenode is a new child of a copied node. 
												        // which will create cycle.
		// copyChildNode has been processed before, its crresponding
		// pasteNode exists
		// add the edge from copyChildNode to nodeAdded with edgeTemp
		if (nodeAdded != null) {
		    edgeMatchSAI = controller.getProblemModel()
		    .findMatchSAIChildEdge(pasteNode, edgeTemp);

		    if (edgeMatchSAI != null) {
			ProblemNode matchedDestNode = edgeMatchSAI.getNodes()[ProblemEdge.DEST];
			if (!isVisitedPasteChildNode(mapNodes, matchedDestNode)) {
			    // delete all of connection edges of edgeMergeToDESTNode
			    NodeView matchedDestNodeVertex = matchedDestNode
			    .getNodeView();
			    matchedDestNodeVertex.delete(false);
			}
		    }

		    cloneEdge(pasteNode, nodeAdded, edgeTemp);
		    cloneEdgeOnlyFlag = true;
		} else {

		    edgeMatchSAI = controller.getProblemModel()
		    .findMatchSAIChildEdge(pasteNode, edgeTemp);

		    if (edgeMatchSAI != null) {
		    	ProblemNode matchedDestNode = edgeMatchSAI.getNodes()[ProblemEdge.DEST];
				if (isVisitedPasteChildNode(mapNodes, matchedDestNode)) {
				    // delete all of connection edges of edgeMergeToDESTNode
				    trace.out(5, this, "delete edgeMatchSAI");
				    controller.deleteEdge(edgeMatchSAI);
				    nodeAdded = cloneStateAndEdge(pasteNode, edgeTemp);
				} else {
				    nodeAdded = mergeEdges(edgeMatchSAI, edgeTemp);
				}
		    } else {
				EdgeData myEdgeTemp = edgeTemp.getEdgeData();
				Vector findMatchedNodes = controller.getProblemModel()
				.findSameStates(pasteNode,
					myEdgeTemp.getSelection(),
					myEdgeTemp.getAction(),
					myEdgeTemp.getInput());
	
				trace.out(5, this, "myEdgeTemp.actionLabel.getInput() = "
					+ myEdgeTemp.getInput());
	
				// the paste edge has an equivalent state
				if (findMatchedNodes.size() > 0) {
				    trace
				    .out(5, this,
				    "find the matched eqivalent state in paste node.");
				    Vector findMatchedNodesCopyNode = controller
				    .getProblemModel().findSameStates(copyNode,
					    myEdgeTemp.getSelection(),
					    myEdgeTemp.getAction(),
					    myEdgeTemp.getInput());
	
				    // copy node subgraph child edge has a separate
				    // eqivalent state
				    // create a separate eqivalent state for paste subgraph
				    if (findMatchedNodesCopyNode.size() > 1) {
				    	trace.out(5, this,"find the eqivalent state for copyNode.");
				    	nodeAdded = cloneStateAndEdge(pasteNode, edgeTemp);
				    } else { // original copy graph has no eqivalent
						// states.
						Boolean alwaysLinkStates = controller
						.getPreferencesModel().getBooleanValue(BR_Controller.ALWAYS_LINK_STATES);
		
						/*
						// 11-09-06 :: Noboru getting rid of Old BR
						trace.out(5, this, "alwaysLinkStates = " + alwaysLinkStates);
						trace.out(5, this, "ESE_Frame.instance().orderSwitchKeepSameFlag = "
							+ controller.brPanel.getOrderSwitchKeepSameFlag());
						 */
		
						// add a paste edge clone edge between pasteNode and
						// the eqivalent node.
						// if alwaysLinkStates is true or
						// orderSwitchKeepSameFlag is SET_KEEP_SAME*/
				/*		if (alwaysLinkStates.booleanValue()) {
						    nodeAdded = (ProblemNode) findMatchedNodes
						    .elementAt(0);
						    cloneEdge(pasteNode, nodeAdded, edgeTemp);
						    cloneEdgeOnlyFlag = true;
						} else
						    // creat a separate eqivalent state
						    nodeAdded = cloneStateAndEdge(pasteNode,
							    edgeTemp);
					    }
				} else
			    nodeAdded = cloneStateAndEdge(pasteNode, edgeTemp);
		    }
		}

		// update mapNodes
		Vector mapPair = new Vector();
		mapPair.addElement(copyChildNode);
		mapPair.addElement(nodeAdded);
		mapNodes.addElement(mapPair);

		if (!cloneEdgeOnlyFlag)
		    pasteSubGraph(nodeAdded, copyChildNode, mapNodes);
	    }
	    
//	    controller.getJGraphWindow().getJGraph().runTreeLayout(problemNode.getJGraphNode());
	    return;
	}*/

	boolean isVisitedPasteChildNode(Vector mapNodes, ProblemNode testNode) {
		ProblemNode nodeTemp;
		Vector mapPair;
		for (int i = 0; i < mapNodes.size(); i++) {
			mapPair = (Vector) mapNodes.elementAt(i);
			nodeTemp = (ProblemNode) mapPair.elementAt(1);
			if (nodeTemp == testNode)
				return true;
		}

		return false;
	}

	/**
	 * . Create a new stat node and a new edge between pasteNode and the new
	 * state node.
	 * 
	 * @pasteNode the node to past subgraph
	 * @edgeCopy the edge to be cloned.
	 */

	ProblemNode cloneStateAndEdge(ProblemNode pasteNode, ProblemEdge edgeCopy) {
		if (pasteNode == null || edgeCopy == null)
			return null;

		ProblemNode copyChildNode = edgeCopy.getNodes()[ProblemEdge.DEST];
		// create a new node clone of copyChildNode
		NodeView copyChildVertex = copyChildNode.getNodeView();
		NodeView newVertex = copyChildVertex.cloneVertex();
		ProblemGraph r = controller.getProblemModel().getProblemGraph();
		ProblemNode node = new ProblemNode(newVertex, controller.getProblemModel());
		ProblemNode nodeAdded = r.addProblemNode(node);
		

		setVertexLocation(newVertex, pasteNode);
		
		
		controller.getProblemModel().fireProblemModelEvent(new NodeCreatedEvent (controller, node));
		
		// add a pasteedge clone edge between pasteNode and
		// new added node of copyChildNode clone
		cloneEdge(pasteNode, nodeAdded, edgeCopy);

		return nodeAdded;
	}

	/**
	 * . create a new edge which is a clone of edgeCopy.
	 * 
	 * @FromNode the source node of cloned new edge.
	 * @todNode the destination node of cloned new edge.
	 * @edgeCopy the edge to be cloned.
	 */

	void cloneEdge(ProblemNode FromNode, ProblemNode todNode,
			ProblemEdge edgeCopy) {
		if (edgeCopy == null)
			return;

		EdgeData myEdgeCopy = edgeCopy.getEdgeData();

		EdgeData myEdgeNew = myEdgeCopy.cloneEdgeData();

		// only one preferred child edge allowed
		if (myEdgeNew.isPreferredEdge()) {
			EdgeData myEdgeTemp = null;
			ProblemEdge edgeTemp = null;
			Enumeration<ProblemEdge> iter = controller.getProblemModel().getProblemGraph()
					.getOutgoingEdges(FromNode);
			while (iter.hasMoreElements()) {
				edgeTemp = (ProblemEdge) iter.nextElement();
				myEdgeTemp = edgeTemp.getEdgeData();
				myEdgeTemp.setPreferredEdge(false);
			}
		}

		myEdgeNew.getActionLabel().resetForeground();

		ProblemEdge edge = controller.getProblemModel().getProblemGraph()
				.addEdge(FromNode, todNode, myEdgeNew);

        myEdgeNew.getActionLabel().update();
		edge.addEdgeLabels();
		controller.getProblemModel().fireProblemModelEvent(new EdgeCreatedEvent (controller, edge));
		return;
	}

	/**
	 * . merge copy node child edge to paste node child edge
	 * 
	 * @edgeMergeTo paste node child edge
	 * @edgeMergeFrom cope node child edge
	 * @return the new merged edge destination node.
	 */
/*
	ProblemNode mergeEdges(ProblemEdge edgeMergeTo, ProblemEdge edgeMergeFrom) {
		if (edgeMergeTo == null || edgeMergeFrom == null)
			return null;

		ProblemNode edgeMergeToSOURCENode = edgeMergeTo.getNodes()[ProblemEdge.SOURCE];
		ProblemNode edgeMergeToDESTNode = edgeMergeTo.getNodes()[ProblemEdge.DEST];

		ProblemNode copyChildNode = edgeMergeFrom.getNodes()[ProblemEdge.DEST];

		// case 1: either edgeMergeTo or edgeMergeFrom is buggy
		// then edgeMergeFrom replaces edgeMergeTo
		if (edgeMergeFrom.isBuggy() || edgeMergeTo.isBuggy()) {
			// delete all of connection edges of edgeMergeToDESTNode
			NodeView edgeMergeToDESTVertex = edgeMergeToDESTNode.getNodeView();
			edgeMergeToDESTVertex.delete(false);

			// create a new node clone of copyChildNode
			NodeView copyChildVertex = copyChildNode.getNodeView();
			NodeView newVertex = copyChildVertex.cloneVertex();
			ProblemGraph r = controller.getProblemModel().getProblemGraph();
			ProblemNode node = new ProblemNode(newVertex, controller.getProblemModel());
			ProblemNode nodeAdded = r.addProblemNode(node);

			cloneEdge(edgeMergeToSOURCENode, nodeAdded, edgeMergeFrom);

			return nodeAdded;
		}

		// case2: really merge edgeMergeFrom into edgeMergeTo
		EdgeData myEdgeMergeTo = edgeMergeTo.getEdgeData();
		EdgeData myEdgeMergeFrom = edgeMergeFrom.getEdgeData();

		if (myEdgeMergeFrom.haveNoneDefaultHint())
			myEdgeMergeTo.setHints(myEdgeMergeFrom.getHints());

		if (myEdgeMergeFrom.hasRealRule()) {
			for (String ruleName : myEdgeMergeFrom.getRuleNames())
				myEdgeMergeTo.addRuleName(ruleName);
			edgeMergeTo.getEdgeData().getEdgeView().update();
		}

		return edgeMergeToDESTNode;
	}*/

	/**
	 * . test whether the copy node is already processed
	 * 
	 * @testdNode the node to be tested
	 * @mapNodes list of copyNode and pastNode pairs
	 * @return null if the testNode is not processed; otherwise return
	 *         testNode's map paste node.
	 */

	ProblemNode findMapNode(ProblemNode testdNode, Vector mapNodes) {
		if (mapNodes == null)
			return null;

		if (mapNodes.size() == 0)
			return null;

		if (testdNode == null)
			return null;

		Vector mapPair;
		ProblemNode nodeTemp;
		for (int i = 0; i < mapNodes.size(); i++) {
			mapPair = (Vector) mapNodes.elementAt(i);
			nodeTemp = (ProblemNode) mapPair.elementAt(0);
			if (testdNode == nodeTemp)
				return (ProblemNode) mapPair.elementAt(1);
		}

		return null;
	}

	public boolean isPasteSubgraph() {
		// no COPY-NODE set
		if (BR_Controller.getCopySubgraphNode() == null) {
			trace.out(5, this, "no copy node is selected.");
			return false;
		}

		if (controller.getProblemModel().getProblemGraph().outDegree(
				BR_Controller.getCopySubgraphNode()) == 0) {
			trace.out(5, this, "copy node has no child.");
			BR_Controller.setCopySubgraphNode(null);	
			return false;
		}
		/*if (problemNode == BR_Controller.getCopySubgraphNode()) {
			return false;
		}*/
		return isOkForPasteSubgraph();
	}
	
	/**
	 * Tell whether this node is proper destination for pasting a subgraph.
	 * @return true if not {@link ProblemNode#isBuggyNode()} and not {@link #isDoneState()}
	 */
	private boolean isOkForPasteSubgraph() {
		ProblemNode problemNode = controller.getProblemModel()
				.getProblemNodeForNodeView(this);

		if (problemNode.isBuggyNode()) {
			if (trace.getDebugCode("borg")) trace.out("borg",  "buggy node");
			return false;
		}

		if (isDoneState()) {
			trace.out(5, this, "done node");
			return false;
		}

		if (!pasteSubgraphFlag) {
			trace.out(5, this, "pasteSubgraphFlag = " + pasteSubgraphFlag);
			return false;
		}
		return true;
	}
	
    // ////////////////////////////////////////////////////

    /**

     * Creates a comm message which describes the Author create new blank state of this object

     */

    // ////////////////////////////////////////////////////

    public MessageObject getAuthorCreateBlankStepMessage() {
        MessageObject mo = MessageObject.create(MsgType.INTERFACE_ACTION, "NotePropertySet");
        mo.setSelection("No_Selection");
        mo.setAction("No_Action");
        mo.setInput("No_Value");
        return mo;
    }

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 **/
	// ///////////////////////////////////////////////////////////////////////////////////////////////
//	ProblemNode cloneStateAndEdge(ProblemNode pasteNode, ProblemEdge edgeCopy) {
	public void addBlankState() {
		 Vector selection = new Vector();
		 Vector action = new Vector();
		 Vector input = new Vector();
		 selection.addElement("No_Selection");
		 action.addElement("No_Action");
		 input.addElement("No_Value");
		 MessageObject mo = PseudoTutorMessageBuilder.buildCommCorrectMessage(selection, action, input, controller);
		 controller.createNewEdge(problemNode, null, selection,action, input,
				 mo,  EdgeData.CORRECT_ACTION,null);
	}
 
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public void renameState() {

		trace.out("rename state");

		if (problemNode == null) {
			//problemNode = controller.getProblemModel()
			problemNode = controller.getProblemModel()
					.getProblemNodeForNodeView(this);
		}
		String oldName = getText().trim();
		String title = "Rename state " + oldName;
		Object response;
		//if (problemNode == controller.getProblemModel().getStartNode()) {
		if (problemNode == controller.getProblemModel().getStartNode()) {
			String[] messages = {
					"The start node text is generally the same as the problem .brd file name.",
					"If you want to keep them the same you may select the menu item",
					"'Save Graph As...' under the menu 'File' after you change the node text." };

			response = JOptionPane.showInputDialog(controller.getActiveWindow(),
					messages, title,
					JOptionPane.QUESTION_MESSAGE, null, null, oldName);
		} else {
			response = JOptionPane.showInputDialog(controller.getActiveWindow(),
					"Please enter the new state name:", title,
					JOptionPane.QUESTION_MESSAGE, null, null, oldName);
		}
		String newName = (response == null ? "" : response.toString());

		//while (controller.getProblemModel().getNode(newName) != null) {
		while (controller.getProblemModel().getNode(newName) != null) {
			final String[] messages = {
					"The name \"+newName+\" matches a state name already in use.",
					"Please enter a different state name or an empty name to quit."
			};
			newName = JOptionPane.showInputDialog(controller.getActiveWindow(),
					messages, title, JOptionPane.QUESTION_MESSAGE);
		}
		
		if (newName != null && !newName.equals("")) {
			setText(newName);
			if (problemNode == controller.getProblemModel().getStartNode())
				controller.updateStatusPanel("Select File->'Save Graph As ...' to save the problem");
			else
				controller.updateStatusPanel(null);

			ProblemModelEvent nodeUpdatedEvent = new NodeUpdatedEvent(controller, getProblemNode());
			controller.getProblemModel().fireProblemModelEvent(nodeUpdatedEvent);
		}
		controller.fireCtatModeEvent(CtatModeEvent.REPAINT);

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public void setIncomingEdgePoint(Point p) {
		incomingEdgePoint = p;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public void setOutgoingEdgePoint(Point p) {
		outgoingEdgePoint = p;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public Point getIncomingEdgePoint() {
		if (incomingEdgePoint != null) {
			return incomingEdgePoint;
		}
		return getMidPoints()[2];
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public Point getOutgoingEdgePoint() {
		if (outgoingEdgePoint != null) {
			return outgoingEdgePoint;
		}
		return getMidPoints()[0];

	}

	// ////////////////////////////////////////////////////////////////////////////
	/**
	 * Determine the mid-points of the four sides of this vertex (text field)
	 */
	// ////////////////////////////////////////////////////////////////////////////
	public Point[] getMidPoints() {

		Point location = getLocation();
		Dimension size = getSize();

		Point[] midPoints = new Point[4];

		for (int i = 0; i < 4; i++)
			midPoints[i] = new Point();

		midPoints[0].x = location.x + size.width / 2;
		midPoints[0].y = location.y;

		midPoints[1].x = location.x + size.width;
		midPoints[1].y = location.y + size.height / 2;

		midPoints[2].x = location.x + size.width / 2;
		midPoints[2].y = location.y + size.height;

		midPoints[3].x = location.x;
		midPoints[3].y = location.y + size.height / 2;

		return midPoints;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	private void updateSize() {
		FontMetrics fontMetrics = getFontMetrics(BRPanel.BOLD_FONT);
		int width = fontMetrics.stringWidth(nodeName) + widthBuffer;

		setSize(new java.awt.Dimension(width, 25));
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public void setText(String s) {
		// turn off text selection
		if (problemNode != null)
			problemNode.setName(s);
		select(0, 0);
		nodeName = s;
		updateSize();
		updateToolTip();
		super.setText(s);
	}

	public boolean equals(NodeView v) {
		return (this.getUniqueID() == v.getUniqueID());
	}

	public int getUniqueID() {
		
		return problemNode.getUniqueID();
	}

	public void setUniqueID(int uniqueIDValue) {
		problemNode.setUniqueID(uniqueIDValue);
	}

	public boolean getLocked() {
		return locked;
	}

	public void setLocked(boolean lockFlag) {
		locked = lockFlag;
	}

	public boolean getDoneState() {
		if (problemNode == null)
			return false;
		return problemNode.getDoneState();
	}

	public void setDoneState(ProblemNode problemNode, boolean doneStatelag) {
		problemNode.setDoneState(doneStatelag);
	}

	// ////////////////////////////////////////////////////////////////////////////
	/**
	 * Find which point in endPoints is closest to the center of the given
	 * vertex
	 */
	// ////////////////////////////////////////////////////////////////////////////
	public static Point findClosestPoint(NodeView vertex, Point[] endPoints) {

		Point startPoint = vertex.getLocation();
		Dimension size = vertex.getSize();
		startPoint.x += size.width / 2;
		startPoint.y += size.height / 2;

		float distance = 10000;
		int closest = 0;
		for (int i = 0; i < endPoints.length; i++) {
			float newDistance = distance(startPoint, endPoints[i]);
			if (newDistance < distance) {
				distance = newDistance;
				closest = i;
			}
		}
		return endPoints[closest];
	}

	// ////////////////////////////////////////////////////////////////////////////
	/**
	 * Find the distance between two points
	 */
	// ////////////////////////////////////////////////////////////////////////////
	protected static float distance(Point p1, Point p2) {

		int xd = p1.x - p2.x;
		int yd = p1.y - p2.y;
		return (float) Math.sqrt(xd * xd + yd * yd);
	}

	// ////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the start and end points for an edge connecting these two vertices
	 */
	// ////////////////////////////////////////////////////////////////////////////
	public static Point[] getEdgePoints(NodeView startVertex, NodeView endVertex) {

		Point[] startMidPoints = startVertex.getMidPoints();
		Point[] endMidPoints = endVertex.getMidPoints();

		Point[] points = new Point[2];

		points[0] = startMidPoints[2];
		points[1] = findClosestPoint(startVertex, endMidPoints);
		return points;
	}

	public void moveTree(int deltaX, int deltaY, int dragNum) {
		//listener.moveTree(deltaX, deltaY, dragNum);
	}

	public ProblemNode getProblemNode() {
		return problemNode;
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public void copy() {
		String errorMsg = null;
	/*	if (!controller.getExampleTracerGraph().isNodeConnected(problemNode.getUniqueID())) {
			errorMsg = "The selected node is currently disconnected from the start state./n"
					  + "Please reconnect the graph before continuing";
		}*/
		//if (controller.getProblemModel().getProblemGraph().outDegree(problemNode) == 0) {	
		if (controller.getProblemModel().getProblemGraph().outDegree(problemNode) == 0) {
			errorMsg = "The selected node has no outgoing links so the copy operation has been cancelled";
		}
		if (problemNode.isBuggyNode() || isDoneState()) {
			errorMsg = "You cannot copy the subgraph of a buggy or done state";
		}
		if(errorMsg!=null){
			if (trace.getDebugCode("borg")) trace.out("borg", errorMsg);
			BR_Controller.setCopySubgraphNode(null);
		}else {
			BR_Controller.setCopySubgraphNode(problemNode);
		}
	}
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public void paste() {
		String errorMsg = null;
		ProblemNode copyNode = BR_Controller.getCopySubgraphNode();
		if(copyNode==null)
			errorMsg ="No copynode selected";
		/*if(!controller.getExampleTracerGraph().isNodeConnected(copyNode.getUniqueID()))
			errorMsg ="The copynode is currently disconnected from the start state";
*/		//if (controller.getProblemModel().isLeaf(copyNode))
		if (controller.getProblemModel().isLeaf(copyNode))
			errorMsg ="The copynode has no outgoing edges";
		/*if(!controller.getExampleTracerGraph().isNodeConnected(problemNode.getUniqueID()))
			errorMsg ="The node you are pasting to isn't reachable from the start state";
	*/	if(errorMsg!=null){
			return;
		}
		BR_Controller.setCopySubgraphNode(null);
		paste(copyNode);
	}
	
	public Map<ProblemEdge, ProblemEdge> pasteUntil(ProblemNode copyNode, Set<String> stopNodes) {
		HashMap<ProblemNode, ProblemNode> oldToNewMapping = new HashMap<ProblemNode, ProblemNode>();
		HashSet<ProblemEdge> allNewEdges = new HashSet<ProblemEdge>();
		HashMap<ProblemEdge, ProblemEdge> oldToNewEdges = new HashMap<ProblemEdge, ProblemEdge>();
		subEvents = new ArrayList<ProblemModelEvent>();
		
		//Getting the current PreferredEdge
		if(trace.getDebugCode("mg"))
			trace.out("mg", "NodeView.pasteUntil(copyNode="+copyNode+",\n  stopNodes="+stopNodes+")");
		ProblemModel pm = problemNode.getProblemModel();
		ProblemGraph graph = pm.getProblemGraph();
		Enumeration<ProblemEdge> outEdges = graph.getOutgoingEdges(problemNode);
		ProblemEdge tempEdge;
		ProblemEdge preferredEdge = null;
		while(outEdges.hasMoreElements()){
			tempEdge = outEdges.nextElement();
			if(tempEdge.isPreferredEdge()){
				preferredEdge = tempEdge;
				break;
			}
		}
		
		//pasteSubGraph2(problemNode, copyNode, oldToNewMapping, allNewEdges, oldToNewEdges);
		pasteSubGraph2Until(problemNode, copyNode, oldToNewMapping, allNewEdges, oldToNewEdges, stopNodes);
		
		//resetting the new edges to be unpreferred if we used to have a preferred edge before paste.
		if(preferredEdge!=null){
			outEdges = graph.getOutgoingEdges(problemNode);
			while(outEdges.hasMoreElements()){
				tempEdge = outEdges.nextElement();
				if(tempEdge!=preferredEdge)
					tempEdge.getEdgeData().setPreferredEdge((false));
			}
		}
		//extract first event from subevents and set it as the mainevent with everything else a subevent.
//		NodeCreatedEvent fireMe = new NodeCreatedEvent(this,
//				((NodeCreatedEvent)subEvents.get(0)).getNode(), new ArrayList(subEvents.subList(1, subEvents.size())));
//		controller.getProblemModel().fireProblemModelEvent(fireMe);
		
		return oldToNewEdges;
	}

	/**
	 * Paste the subgraph rooted at copyNode onto this node. N.B.:<b> copyNode might be in a different graph.</b>
	 * @return map of old to new edges
	 */
	public Map<ProblemEdge, ProblemEdge> paste(ProblemNode copyNode) {
		
		HashMap<ProblemNode, ProblemNode> oldToNewMapping = new HashMap<ProblemNode, ProblemNode>();
		HashSet<ProblemEdge> allNewEdges = new HashSet<ProblemEdge>();
		HashMap<ProblemEdge, ProblemEdge> oldToNewEdges = new HashMap<ProblemEdge, ProblemEdge>();
		subEvents = new ArrayList<ProblemModelEvent>();
		
		//Getting the current PreferredEdge
		ProblemModel pm = problemNode.getProblemModel();
		ProblemGraph graph = pm.getProblemGraph();
		Enumeration<ProblemEdge> outEdges = graph.getOutgoingEdges(problemNode);
		ProblemEdge tempEdge;
		ProblemEdge preferredEdge = null;
		while(outEdges.hasMoreElements()){
			tempEdge = outEdges.nextElement();
			if(tempEdge.isPreferredEdge()){
				preferredEdge = tempEdge;
				break;
			}
		}
		
		pasteSubGraph2(problemNode, copyNode, oldToNewMapping, allNewEdges, oldToNewEdges);
		
		//resetting the new edges to be unpreffered if we used to have a preffered edge before paste.
		if(preferredEdge!=null){
			outEdges = graph.getOutgoingEdges(problemNode);
			while(outEdges.hasMoreElements()){
				tempEdge = outEdges.nextElement();
				if(tempEdge!=preferredEdge)
					tempEdge.getEdgeData().setPreferredEdge((false));
			}
		}
		//extract first event from subevents and set it as the mainevent with everything else a subevent.
		NodeCreatedEvent fireMe = new NodeCreatedEvent(this,
				((NodeCreatedEvent)subEvents.get(0)).getNode(), new ArrayList(subEvents.subList(1, subEvents.size())));
		controller.getProblemModel().fireProblemModelEvent(fireMe);
		
		return oldToNewEdges;
		//controller.resetExampleTracer();
		/* sewall 2009/01/03: must recalculate link depths after any graph change */
		//controller.getProblemModel().getExampleTracerGraph().redoLinkDepths();
	}
	private List<ProblemModelEvent> subEvents;
	
	/**
	 * 
	 * @param pasteNode
	 * @param copyNode
	 * @param oldToNewNodeMapping
	 * @param allInspectedEdges
	 * @param oldToNewEdges
	 */
	private void pasteSubGraph2(ProblemNode pasteNode, ProblemNode copyNode,
			HashMap<ProblemNode,ProblemNode> oldToNewNodeMapping, HashSet<ProblemEdge> allInspectedEdges,
			HashMap<ProblemEdge, ProblemEdge> oldToNewEdges){
			//check if copynode isn't in mapNodes
			//if(oldToNewNodeMapping.get(copyNode)!=null)
			//	return;
			
			List<ProblemEdge> outEdges = copyNode.getOutgoingEdges();
			if(outEdges.size()==0)
				return;
			ProblemNode currNode,tempNode,newNode;
			ProblemEdge newEdge,currEdge;
			int i;
			EdgeData newData;
			List<ProblemNode> newNodes = new ArrayList<ProblemNode>();
			List<ProblemNode> newNodesOldNodes = new ArrayList<ProblemNode>();
			//List<ProblemEdge> newEdges = new ArrayList<ProblemEdge>();
			for(i=0; i < outEdges.size(); i++){
				currEdge = outEdges.get(i);
				if(allInspectedEdges.contains(currEdge))
					continue;
				newData = currEdge.getEdgeData().cloneEdgeData(pasteNode.getProblemModel());
				currNode = currEdge.getDest();
				tempNode = oldToNewNodeMapping.get(currNode);
				if(tempNode==null){
					newNode = controller.createProblemNode(pasteNode, newData.getSelection() ,pasteNode.getOutDegree());
					newNodes.add(newNode);
					newNodesOldNodes.add(currNode);
					subEvents.add(new NodeCreatedEvent(this, newNode));
					oldToNewNodeMapping.put(copyNode, pasteNode);
					newEdge = controller.getProblemModel().getProblemGraph().addEdge(pasteNode, newNode, newData);
				}else{
					newEdge = controller.getProblemModel().getProblemGraph().addEdge(pasteNode, tempNode, newData);
				}
				
				newEdge.addEdgeLabels();
				newData.getActionLabel().update();
				allInspectedEdges.add(newEdge);
				allInspectedEdges.add(currEdge);
				oldToNewEdges.put(currEdge, newEdge);
				//newEdges.add(newEdge);
				subEvents.add(new EdgeCreatedEvent(this, newEdge));
			}
			
			for(i=0; i < newNodes.size(); i++){
				pasteSubGraph2(newNodes.get(i), newNodesOldNodes.get(i),oldToNewNodeMapping, allInspectedEdges, oldToNewEdges);
			}
			
			return;
	}
	
	private void pasteSubGraph2Until(ProblemNode pasteNode, ProblemNode copyNode,
			HashMap<ProblemNode,ProblemNode> oldToNewNodeMapping, HashSet<ProblemEdge> allInspectedEdges,
			HashMap<ProblemEdge, ProblemEdge> oldToNewEdges, Set<String> stopNodes) {
		//check if copynode isn't in mapNodes
		//if(oldToNewNodeMapping.get(copyNode)!=null)
		//	return;
		
		List<ProblemEdge> outEdges = copyNode.getOutgoingEdges();
		if(outEdges.size()==0)
			return;
		ProblemNode currNode,tempNode,newNode;
		ProblemEdge newEdge,currEdge;
		int i;
		EdgeData newData;
		List<ProblemNode> newNodes = new ArrayList<ProblemNode>();
		List<ProblemNode> newNodesOldNodes = new ArrayList<ProblemNode>();
		//List<ProblemEdge> newEdges = new ArrayList<ProblemEdge>();
		for(i=0; i < outEdges.size(); i++){
			currEdge = outEdges.get(i);
			// if edge in stopNodes, continue
			if(stopNodes.contains(currEdge.getEdgeData().getName())) {
				continue;
			}
			if(allInspectedEdges.contains(currEdge))
				continue;
			newData = currEdge.getEdgeData().cloneEdgeData(pasteNode.getProblemModel());
			currNode = currEdge.getDest();
			tempNode = oldToNewNodeMapping.get(currNode);
			if(tempNode==null){
				newNode = controller.createProblemNode(pasteNode, newData.getSelection() ,pasteNode.getOutDegree());
				newNodes.add(newNode);
				newNodesOldNodes.add(currNode);
				subEvents.add(new NodeCreatedEvent(this, newNode));
				oldToNewNodeMapping.put(copyNode, pasteNode);
				newEdge = controller.getProblemModel().getProblemGraph().addEdge(pasteNode, newNode, newData);
			}else{
				newEdge = controller.getProblemModel().getProblemGraph().addEdge(pasteNode, tempNode, newData);
			}
			
			newEdge.addEdgeLabels();
			newData.getActionLabel().update();
			allInspectedEdges.add(newEdge);
			allInspectedEdges.add(currEdge);
			oldToNewEdges.put(currEdge, newEdge);
			//newEdges.add(newEdge);
			subEvents.add(new EdgeCreatedEvent(this, newEdge));
		}
		
		for(i=0; i < newNodes.size(); i++){
			pasteSubGraph2Until(newNodes.get(i), newNodesOldNodes.get(i),oldToNewNodeMapping, allInspectedEdges, oldToNewEdges, stopNodes);
		}
		
		return;
	}
	
    void setVertexLocation(NodeView atVertex, ProblemNode sourceNode) {
    	int childCount = controller.getProblemModel().getProblemGraph()
                .outDegree(sourceNode);

        trace.out(5, this, "childCount = " + childCount);

        // set Vertex location temporary
        NodeView sourceVertex = (NodeView) sourceNode.getNodeView();
        Point parentLocation = sourceVertex.getLocation();

        parentLocation.x += sourceVertex.getSize().width / 2;

        Point newLocation = getNewVertexLocation(parentLocation, childCount);
        newLocation.x -= atVertex.getSize().width / 2;

        atVertex.setLocation(newLocation);
        System.err.println("New Location = " + newLocation);
        return;
    }

    // //////////////////////////////////////////////////////////////////////
    // 
    // Calclulate where to place the next Vertex box the user has just created.
    //
    // //////////////////////////////////////////////////////////////////////
    public static Point getNewVertexLocation(Point parentLocation, int childCount) {

        double length = 130;

        if (childCount == 0)
            length = 110;

        double angle = 0.0;

        double baseAngle = Math.PI / 5;

        if (childCount < 5)
            angle = (childCount + 1) / 2 * baseAngle;
        else if (childCount < 7) {
            angle = (childCount + 1) / 2 * baseAngle - baseAngle / 2;
        } else {
            angle = baseAngle * 1.5;
            angle += (childCount - 7) / 2 * baseAngle / 2;
            length = 180;
        }

        int x, y;

        if (childCount % 2 == 1)
            angle = angle * -1;

        x = (int) (length * Math.sin(angle));
        y = (int) (length * Math.cos(angle));
        angle = angle / Math.PI * 180;
        return new Point(parentLocation.x + x, parentLocation.y + y);
    }
    
    public static final String DELETE = "Delete...";

    public static final String RENAME = "Rename...";
    
    public static final String CREATE = "Add Blank Step";
    
    public static final String CREATE_DISABLED = "Add Blank Step (Enabled in 'Demonstrate' Mode)";
    
    public static final String COPY = "Copy Subgraph";
    public static final String MOVE_SUBGRAPH = "Move Subgraph";
    public static final String PASTE = "Paste Subgraph";
    public static final String INSERT_SUBGRAPH = "Insert Subgraph from File";
    
	// [Kevin Zhao](kzhao) -	This is the String Name that will be used for Cancel Demonstrating a Link
	private static final String CANCEL_DEMONSTRATE_LINK = "Cancel Demonstrate This Link Mode";
    
    public static void evaluatePopup(MouseEvent e, final ProblemNode problemNode, final BR_Controller controller, boolean showLayoutMenu) {
    	boolean notStartNode = true;

        // Don't let user delete start node
        if (problemNode == controller.getProblemModel()
                .getStartNode())
            notStartNode = false;

        JPopupMenu popupMenu = new JPopupMenu();
        
        // [Kevin Zhao](kzhao) - Special Case for canceling DemonstrateThisLinkMode
        if (controller.getCtatModeModel().isDemonstrateThisLinkMode()) {
        	JMenuItem menuItem = new JMenuItem(CANCEL_DEMONSTRATE_LINK);
        	popupMenu.add(menuItem);
        	menuItem.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			controller.getCtatModeModel().exitDemonstrateThisLinkMode();
        		}
        	});
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        	return;
        }
        ////////////////////////////////////////////////////////////////////////////////
        
        JMenuItem menuItem = new JMenuItem(DELETE);
        menuItem.setEnabled(notStartNode);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.out.println("***DELETE***");
            	trace.out ("problem node = " + problemNode);
            	trace.out ("node view = " + problemNode.getNodeView());
            	controller.processDeleteNode(problemNode);
            	 
            }
            
        });
        popupMenu.add(menuItem);

        menuItem = new JMenuItem(RENAME);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                problemNode.getNodeView().renameState();
                
        		//Undo checkpoint for renaming node ID: 1337
    			ActionEvent ae = new ActionEvent(this, 0, "Rename State");
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
            }
            
        });
        popupMenu.add(menuItem);
        
        
        //[Kevin Zhao](kzhao) - Created a different menu option to let the user know that they can't use
        //						'Add Blank State' unless in 'Demonstrate' Mode
        menuItem = new JMenuItem(CREATE);
        if(problemNode.isBuggyNode() || problemNode.isDoneState()){
        	menuItem.setEnabled(false);
        	menuItem.setToolTipText("You cannot add a blank state to a buggyNode or a DoneState");
        }
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (trace.getDebugCode("br"))
            		trace.out("br", "CREATE:CALLED");
                problemNode.getNodeView().addBlankState();
                
                //Undo checkpoint for creating blank node ID: 1337
    			ActionEvent ae = new ActionEvent(this, 0, CREATE);
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
            }
            
        });
        popupMenu.add(menuItem);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        menuItem = new JMenuItem(COPY);
        menuItem.setEnabled(notStartNode);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (trace.getDebugCode("br"))
            		trace.out("br", "Copy Subgraph");           	
                problemNode.getNodeView().copy();
            }
            
        });
        popupMenu.add(menuItem);

        if (problemNode.isLeaf()) menuItem.setEnabled(false);
        
        menuItem = new JMenuItem(PASTE);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (trace.getDebugCode("br"))
            		trace.out("br", "Paste Subgraph");
            	
                problemNode.getNodeView().paste();
                
                //Undo checkpoint for pasting ID: 1337
    			ActionEvent ae = new ActionEvent(this, 0, PASTE);
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
            }
            
        });
        popupMenu.add(menuItem);

        if (!problemNode.getNodeView().isPasteSubgraph()) menuItem.setEnabled(false);
        
        menuItem = new JMenuItem(INSERT_SUBGRAPH);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (trace.getDebugCode("br"))
            		trace.out("br", "insert subgraph");
                InsertSubgraphDialog.doDialog(controller, problemNode);
                
                //Undo checkpoint for inserting subgraph ID: 1337
    			ActionEvent ae = new ActionEvent(this, 0, INSERT_SUBGRAPH);
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
            }
        });
        popupMenu.add(menuItem);
        if (!problemNode.getNodeView().isOkForPasteSubgraph()) menuItem.setEnabled(false);

        if (showLayoutMenu) {
	        menuItem = new JMenuItem(RUN_TREE_LAYOUT);
	        menuItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                controller.getJGraphWindow().getJGraph().runTreeLayout(problemNode.getJGraphNode());
	                
	                //Undo checkpoint for running tree layout ID: 1337
	    			ActionEvent ae = new ActionEvent(this, 0, RUN_TREE_LAYOUT);
	        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
	            }
	            
	        });
	        popupMenu.add(menuItem);
        }
        
        menuItem = new JMenuItem("Run Interactive Learning here");
        menuItem.setEnabled(controller.getCtatModeModel().isSimStudentMode());
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.getMissController().getSimSt().runSimStInteractiveLearning();
            }
            
        });
        popupMenu.add(menuItem);

        menuItem = new JCheckBoxMenuItem("Start State", problemNode.isStudentBeginsHereState());
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	problemNode.getProblemModel().setStudentBeginsHereState(problemNode);
            	
            	//Undo checkpoint for setting start state ID: 1337
            	ActionEvent ae = new ActionEvent(this, 0, "Designate state as start state");
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
            }
        });
        popupMenu.add(menuItem);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
}

