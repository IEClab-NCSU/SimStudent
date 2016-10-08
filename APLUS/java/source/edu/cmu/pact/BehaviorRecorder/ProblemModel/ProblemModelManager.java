package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Utilities.trace;

public class ProblemModelManager {
	
	/** Increment to {@link #offset} when pasting links back to their source graph. */
	private static final int PASTE_OFFSET = 20;
	
	private ProblemModel mainModel;
	//private ProblemModelUnattached clipboardModel;
	private BR_Controller controller;

	/** Current offset for pasting links back to their source graph. */
	private int offset = 0;

	private static ProblemModel _intermediateModel;
//	private static List<ExampleTracerLink> _copiedEdges;
	private static List<ExampleTracerLink> _topLinks;
	private static List<ExampleTracerLink> _bottomLinks;
	private static Set<ProblemNode> _topNodes;
	private static Set<ExampleTracerLink> selectedLinks;
	private static ProblemModel sourceProblemModel;
	private static Rectangle selectedBounds;
	//private static ProblemNode sourceNode;

	public ProblemModelManager(ProblemModel main, BR_Controller controller) {
		this.mainModel = main;
		//this.clipboardModel = clipboard;
		this.controller = controller;

//		if(_copiedEdges == null) {
//			_copiedEdges = new ArrayList<ExampleTracerLink>();
//		}
		if(_topNodes == null) {
			_topNodes = new HashSet<ProblemNode>();
		}
		if(_topLinks == null) {
			_topLinks = new ArrayList<ExampleTracerLink>();
		}
		if(_bottomLinks == null) {
			_bottomLinks = new ArrayList<ExampleTracerLink>();
		}
	}

	public ProblemModel getMainModel() {
		return this.mainModel;
	}

	/*
	public ProblemModelUnattached getClipboardModel() {
		return this.clipboardModel;
	}
	*/

	/**
	 * This method populates {@link #selectedLinks} from {@link ProblemModel#getSelectedLinks()}.
	 * It does a bunch of other stuff about finding the tops and bottoms of the connected
	 * portions of the copied subgraph, but so far that work is unused.
	 * @return number of links on the clipboard
	 */
	public int copySelectedLinks() {
		_intermediateModel = new ProblemModel(null);
		_intermediateModel.setProblemName("intermedate");
//		ProblemNode copyNode = BR_Controller.getCopySubgraphNode();
//		BR_Controller.setCopySubgraphNode(_intermediateModel.getStartNode());
		if(trace.getDebugCode("mg"))
			trace.out("mg", "ProblemModelManager (copySelectedLinks): start");
		_topNodes.clear();
		_topLinks.clear();
		_bottomLinks.clear();
//		_copiedEdges.clear();

		sourceProblemModel = controller.getProblemModel();
		selectedLinks = controller.getProblemModel().getSelectedLinks();
		if(trace.getDebugCode("mg"))
			trace.out("mg", "PMMgr.copySelectedLinks(): links "+selectedLinks);

		Set<Integer> linkIDs = new HashSet<Integer>();
		Set<Integer> nodeIDs = new HashSet<Integer>();
		Set<ProblemNode> nodes = new HashSet<ProblemNode>();
		Set<String> bottomIDs = new HashSet<String>();

		for (ExampleTracerLink link : selectedLinks) {
			linkIDs.add(Integer.valueOf(link.getUniqueID()));
			int prevNodeID = link.getPrevNode();
			nodeIDs.add(Integer.valueOf(prevNodeID));
			nodes.add(mainModel.getProblemNode(prevNodeID));
			int nextNodeID = link.getNextNode();
			nodeIDs.add(Integer.valueOf(nextNodeID));
			nodes.add(mainModel.getProblemNode(nextNodeID));
		}
		selectedBounds = getSelectedBounds(nodes);
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "PMMgr.copySelectedLinks(): linkIDs "+linkIDs);
			trace.out("mg", "PMMgr.selectedBounds:              "+selectedBounds);
			trace.out("mg", "PMMgr.copySelectedLinks(): nodeIDs "+nodeIDs);
			trace.out("mg", "PMMgr.copySelectedLinks(): nextNodes "+nodes);
		}

		Set<ExampleTracerLink> _topLinks = new HashSet<ExampleTracerLink>();
		for (ExampleTracerLink link : selectedLinks) {
			// look for the "top" components: links whose parents aren't among those selected
			int prevNodeID = link.getPrevNode();
			if (!nodeIDs.contains(Integer.valueOf(prevNodeID))) {
				_topLinks.add(link);
				_topNodes.add(mainModel.getProblemNode(prevNodeID));
			}
		}
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "PMMgr.copySelectedLinks(): _topLinks "+_topLinks);
			trace.out("mg", "PMMgr.copySelectedLinks(): _topNodes "+_topNodes);
		}
		
		// look for the bottom links, whose nextNodes have no outgoing links selected
		for(ProblemNode nextNode : nodes) {
			int nodeID = nextNode.getUniqueID();
			ExampleTracerNode node = mainModel.getExampleTracerGraph().getNode(nodeID);
			boolean outLinkFound = false;
			for(ExampleTracerLink outLink : node.getOutLinks()) {
				if(!linkIDs.contains(Integer.valueOf(outLink.getUniqueID())))
					continue;
				outLinkFound = true;
				break;
			}
			if(outLinkFound)
				continue;
			for(ExampleTracerLink inLink : node.getInLinks()) {
				if(linkIDs.contains(Integer.valueOf(inLink.getUniqueID()))) {
					_bottomLinks.add(inLink);
					// store the "next" node ID for paste-until
					bottomIDs.add(inLink.getEdge().getName());
				}
			}
		}
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "PMMgr.copySelectedLinks(): _bottomLinks "+_bottomLinks);
			trace.out("mg", "PMMgr.copySelectedLinks(): bottomIDs "+bottomIDs);
		}
		return selectedLinks.size();
	}


	/*
	public void addLinkManual(ExampleTracerLink start, ExampleTracerLink end,
			Vector selectionP) {
		// make sure start is in main model, end is in clipboard model
		if (this.mainModel.getEdge(start.getID()) == null
				|| this.clipboardModel.getEdge(end.getID()) == null) {
			// error message
			String text = "You must connect a pasted link to a link in the main model.";
			JOptionPane op = new JOptionPane();
			op.createDialog(controller.getActiveWindow(), text);
			return;
		}
		ProblemNode startNode = this.mainModel.getProblemNode(start
				.getNextNode());
		// create a node between end and start
		ProblemNode newNode = this.controller.createProblemNode2(startNode,
				selectionP, 2); // last arg = 0?
		// for now, add in information from the start link
		EdgeData startData = start.getEdge();
		// second argument null?
		Vector s = new Vector();
		Vector a = new Vector();
		Vector i = new Vector();
		s.add(startData.getSelection());
		a.add(startData.getAction());
		i.add(startData.getInput());
		controller.createNewEdge(startNode, newNode, s, a, i,
				startData.getDemoMsgObj(), startData.getActionType(),
				new ArrayList<ExampleTracerLink>());
		// add end to the main model and take it off the clipboard model
		this.controller.deleteSingleEdge(this.mainModel.getEdge(end.getID()),
				true); // is this what we want?
	}
	*/

	/**
	 * Paste a collection of links in {@link #selectedLinks} into a destination graph.
	 * Cf. {@link NodeView#paste()}. Instead of recursing, this code iterates through the 
	 * {@link #selectedLinks} creating nodes and copying links. 
	 */
	public void pasteLinks() {
		if(selectedLinks == null || selectedLinks.isEmpty())
			return;

		// Copied links will land in their original arrangement, but shifted to the right of
		// links already in the destination graph.
		int xOffset = 0; // getVisualWidth(controller.getProblemModel())+10; FIXME
		Rectangle xlate = calcOffset(selectedBounds, controller.getGraphViewPortBounds());
		
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "PMMgr.pasteLinks() no of selectedLinks "+selectedLinks.size());
			trace.out("mg", "PMMgr.pasteLinks() selectedBounds      "+selectedBounds);
			trace.out("mg", "PMMgr.pasteLinks() xlate               "+xlate);
			trace.out("mg", "PMMgr.pasteLinks() number of top nodes "+_topNodes.size());
		}		
		List<ProblemModelEvent> subEvents = new ArrayList<ProblemModelEvent>();
		
		ProblemNode firstNode = null;
		Map<ProblemNode, ProblemNode> oldToNewNodeMap = new LinkedHashMap<ProblemNode, ProblemNode>();
		Map<ProblemNode, ProblemNode> newNodesOldNodes = new LinkedHashMap<ProblemNode, ProblemNode>();
		Map<ProblemEdge, ProblemEdge> oldToNewEdges = new LinkedHashMap<ProblemEdge, ProblemEdge>();

		ProblemModel pm = controller.getProblemModel();
		firstNode = pm.getStartNode();
		if(firstNode == null) {
			controller.getCtatFrameController().getDockedFrame().getCtatModePanel().createStartState();
			firstNode = pm.getStartNode();
		}

		for(ExampleTracerLink currLink : selectedLinks){
			EdgeData currEdgeData = currLink.getEdge(); 
			ProblemEdge currEdge = currEdgeData.getEdge();
			EdgeData newData = currEdgeData.cloneEdgeData(pm);
			ProblemNode currSource = currEdge.getSource();
			ProblemNode currDest = currEdge.getDest();
			ProblemNode newSource = oldToNewNodeMap.get(currSource);
			if(newSource==null) {
				Point sourceLoc = currSource.getNodeView().getLocation();
				Point newSourceLoc = new Point(xlate.x+xlate.width+sourceLoc.x, xlate.y+xlate.height+sourceLoc.y);
				if(trace.getDebugCode("mg"))
					trace.out("mg", "PMM.pasteLinks() old sourceLoc "+sourceLoc+", new "+newSourceLoc);
				newSource = controller.createProblemNode(null, newSourceLoc);
				newNodesOldNodes.put(newSource, currSource);
				NodeCreatedEvent evt =
						new NodeCreatedEvent((firstNode == null ? this : firstNode.getNodeView()), newSource);
				if(firstNode == null) { // on 1st node, fire thru to complete construction; other events below
					firstNode = newSource;
					controller.getProblemModel().fireProblemModelEvent(evt);
				} else
					subEvents.add(evt);
				oldToNewNodeMap.put(currSource, newSource);
			}
			ProblemNode newDest = oldToNewNodeMap.get(currDest);
			if(newDest==null){
				Point destLoc = currDest.getNodeView().getLocation();
				Point newDestLoc = new Point(xlate.x+xlate.width+destLoc.x, xlate.y+xlate.height+destLoc.y);
				if(trace.getDebugCode("mg"))
					trace.out("mg", "PMM.pasteLinks() old sourceLoc "+destLoc+", new "+newDestLoc);
				newDest = controller.createProblemNode(newData.getSelection(), newDestLoc);
				newNodesOldNodes.put(newSource, currSource);
				subEvents.add(new NodeCreatedEvent(firstNode.getNodeView(), newDest));
				oldToNewNodeMap.put(currDest, newDest);
			}
			ProblemEdge newEdge =
					controller.getProblemModel().getProblemGraph().addEdge(newSource, newDest, newData);
			
			newEdge.addEdgeLabels();
			newData.getActionLabel().update();
//			allInspectedEdges.add(newEdge);
//			allInspectedEdges.add(currEdge);
			oldToNewEdges.put(currEdge, newEdge);
			//newEdges.add(newEdge);
			EdgeCreatedEvent newEdgeEvt = new EdgeCreatedEvent(firstNode.getNodeView(), newEdge);
			newEdgeEvt.setSelected(true);
			subEvents.add(newEdgeEvt);
		}
		for(Map.Entry<ProblemNode, ProblemNode> newOld : newNodesOldNodes.entrySet())
			setPreferredPath(newOld.getValue(), newOld.getKey(), oldToNewEdges);
		
		// This follows NodeView.paste(), where the event source is a preexisting node.
		NodeCreatedEvent fireMe = new NodeCreatedEvent(firstNode.getNodeView(),
				((NodeCreatedEvent)subEvents.get(0)).getNode(),
				new ArrayList<ProblemModelEvent>(subEvents.subList(1, subEvents.size())));
		controller.getProblemModel().fireProblemModelEvent(fireMe);
	}

	/**
	 * Find among an already-chosen set of outlink-only nodes the node with the greatest number
	 * of descendants.
	 * @param topNodes source nodes in the set of links having no incoming links
	 * @param links count only links in this set 
	 * @return
	 */
	private ProblemNode findBestStartNode(Set<ProblemNode> topNodes,
			Set<ExampleTracerLink> links) {

		Set<Integer> linkIDs = new HashSet<Integer>();
		for(ExampleTracerLink link : links)
			linkIDs.add(new Integer(link.getID()));

		int rLinks = -1;
		ProblemNode result = null;

		for(ProblemNode tn : topNodes) {
			int nLinks = countDescendants(tn, linkIDs, 0);
			if(rLinks >= nLinks)
				continue;
			rLinks = nLinks;
			result = tn;
		}
		if(trace.getDebugCode("mg"))
			trace.out("mg", "PMMgr.findBestStartNode("+topNodes+") result "+result+", n "+rLinks);
		return result;
	}

	/**
	 * Count the links connected directly or indirectly to node n, but only those links in the set.
	 * @param n
	 * @param links
	 * @param count result so far
	 * @return count
	 */
	private int countDescendants(ProblemNode n, Set<Integer> linkIDs, int count) {
		if(trace.getDebugCode("mg"))
			trace.out("mg", "PMMgr.countDescendants("+n+") entry count "+count);
		for(ProblemEdge edge : n.getOutgoingEdges()) {
			if(linkIDs.contains(edge.getUniqueID()))
				count = countDescendants(edge.getDest(), linkIDs, count)+1;  // +1 for this edge 
		}
		if(trace.getDebugCode("mg"))
			trace.out("mg", "PMMgr.countDescendants("+n+") retrn count "+count);
		return count;
	}

	/**
	 * Return a rectangle whose bounds are x and y offsets for elements in the target graph. 
	 * @param sb
	 * @param vp
	 * @return
	 */
	private Rectangle calcOffset(Rectangle sb, Rectangle vp) {
		if(sourceProblemModel == controller.getProblemModel()) {
			offset += PASTE_OFFSET;
			return new Rectangle(0, 0, offset, offset);
		}
		if(sb == null || vp == null)
			return null;
		int dx = (vp.x+(vp.width+1)/2)-(sb.x+(sb.width+1)/2);
		int dy = (vp.y+(vp.height+1)/2)-(sb.y+(sb.height+1)/2);
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "PMMgr.calcOffset() sb "+sb);
			trace.out("mg", "PMMgr.calcOffset() vp "+vp);
			trace.out("mg", "PMMgr.calcOffset() sb midpoint "+
					(sb.x+(sb.width+1)/2)+", "+(sb.y+(sb.height+1)/2));
			trace.out("mg", "PMMgr.calcOffset() vp midpoint "+
					(vp.x+(vp.width+1)/2)+", "+(vp.y+(vp.height+1)/2));
			trace.out("mg", "PMMgr.calcOffset() dx, dy      "+
					dx+", "+dy);
		}
		final int m = 10;  // margin
		int dxAll = sb.x+dx;
		if(dxAll < 0) dxAll = -dxAll+m;  // left edge would be clipped: slide everyone right 
		else dxAll = 0;
		int dyAll = sb.y+dy;
		if(dyAll < 0) dyAll = -dyAll+m;  // top edge would be clipped: slide everyone down
		else dyAll = 0;
		return new Rectangle(dx, dy, dxAll, dyAll);
	}

	/**
	 * Set the preferred path in the new node according to its setting in the old node.
	 * @param on old node
	 * @param nn new node
	 * @param oldToNewEdges map pairing old edges with new
	 */
	private void setPreferredPath(ProblemNode on, ProblemNode nn,
			Map<ProblemEdge, ProblemEdge> oldToNewEdges) {
		int n = 0;
		EdgeData firstCorrect = null;
		EdgeData firstSuboptimal = null;
		for(ProblemEdge oldEdge : on.getOutgoingEdges()) {
			ProblemEdge newEdge = oldToNewEdges.get(oldEdge);
			if(newEdge == null)  // this sibling of a selected edge wasn't itself selected
				continue;
			EdgeData newEdgeData = newEdge.getEdgeData();
			boolean p = oldEdge.getEdgeData().isPreferredEdge();
			if(p && (++n) > 1)
				trace.err("PMMgr.setPreferredPath() nPreferredEdges "+n+">1 from node "+on+
						" at edge "+oldEdge.getEdgeData().toString());
			if(n < 2)
				newEdgeData.setPreferredEdge(p);
			if(firstCorrect == null) {
				if(EdgeData.CORRECT_ACTION.equalsIgnoreCase(newEdgeData.getActionType()))
					firstCorrect = newEdgeData;
			}
			if(firstSuboptimal == null) {
				if(EdgeData.FIREABLE_BUGGY_ACTION.equalsIgnoreCase(newEdgeData.getActionType()))
					firstSuboptimal = newEdgeData;
			}
		}
		if(n < 1) {
			if(firstCorrect != null)
				firstCorrect.setPreferredEdge(true);
			else if(firstSuboptimal != null)
				firstSuboptimal.setPreferredEdge(true);
			else
				trace.err("PMMgr.setPreferredPath() no correct or subopimal edges from node "+on+
						" mapping to node "+nn);
		}
		n = 0;
		for(ProblemEdge newEdge : nn.getOutgoingEdges()) {
			if(newEdge.getEdgeData().isPreferredEdge())
				++n;
		}
		if(n > 1)
			trace.err("PMMgr.setPreferredPath() too many "+n+" preferred edges from node "+on+
					" mapping to node "+nn);
	}

	/**
	 * Find the x-coordinate of the right-hand edge of right-most node on the graph. 
	 * @param problemModel
	 * @return right-most node's x-coordinate plus its width 
	 */
	private int getVisualWidth(ProblemModel problemModel) {
		int result = 0;
		int width = 0;
		for(Enumeration<ProblemNode> nodes = problemModel.getProblemGraph().nodes(); nodes.hasMoreElements();) {
			NodeView nv = nodes.nextElement().getNodeView();
			if(nv == null || nv.getLocation() == null)
				continue;
			Point location = nv.getLocation();
			if(result >= location.x)
				continue;
			result = location.x;
			width = nv.getSize().width;
		}
		return result + width;
	}

	/**
	 * Find the bounds of the subgraph to be pasted.
	 * @param problemModel
	 * @return bounds in subgraph's source coordinates
	 */
	private Rectangle getSelectedBounds(Set<ProblemNode> nodes) {
		Rectangle r = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		boolean revised = false;
		for(ProblemNode node : nodes) {
			NodeView nv = node.getNodeView();
			if(nv == null)
				continue;
			Point location = nv.getLocation();
			if(location == null)
				continue;
			Dimension dim = nv.getSize();
			if(dim == null)
				continue;
			if(r.x > location.x)
				r.x = location.x;
			if(r.y > location.y)
				r.y = location.y;
			revised = true;
		}
		for(ProblemNode node : nodes) {
			NodeView nv = node.getNodeView();
			if(nv == null)
				continue;
			Point location = nv.getLocation();
			if(location == null)
				continue;
			Dimension dim = nv.getSize();
			if(dim == null)
				continue;
			if(r.width < location.x+dim.width-r.x)
				r.width = location.x+dim.width-r.x;
			if(r.height < location.y+dim.height-r.y)
				r.height = location.y+dim.height-r.y;
			revised = true;
		}
		if(!revised)
			return null;
		return r;
	}

	public static ProblemModel getClipboardProblemModel() {
		return _intermediateModel;
	}

	/**
	 * @return the number of selected links.
	 */
	public int nSelectedLinks() {
		return (selectedLinks == null ? 0 : selectedLinks.size());
	}
}
