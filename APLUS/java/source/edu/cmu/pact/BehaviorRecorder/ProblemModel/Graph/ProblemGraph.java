package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class ProblemGraph implements Serializable {

	// Each ESDigraph object is a directed graph whose elements and
	// edge attributes are arbitrary objects.

	// This directed graph is represented by a node set and edge set as follows.
	// firstNode is a link to the first node in a DLL (Doubly Linked List) of ESDigraphNode
	// objects, each of which contains an element. firstEdge is a link to the
	// first node in a DLL of ESDigraphEdge objects, each of which
	// contains an attribute and is linked to the edge's source and destination
	// nodes. size contains the graph's size.

	private static final long serialVersionUID = -8217291579641205667L;

	/** Name for Done node. Prefix for additional done nodes. */
	public static final String DONE_NODE_NAME = "Done";
	
	private ProblemNode firstNode;
	private ProblemEdge firstEdge;
	private int numNodes;
	
	/** Set to ensure that done states have unique names. Contains done node names in lower case. */
	private Map<String, ProblemNode> doneNodeNames;

	//When we first create a graph, we will have no nodes nor edges, and of course, the size is 0
	public ProblemGraph() {
        firstNode = null;
		firstEdge = null;
		numNodes = 0;
		doneNodeNames = new LinkedHashMap<String, ProblemNode>();
	}

	//This is the number of nodes in the current Problem Graph
	public int getNodeCount() {
		return this.numNodes;
	}

	//Returns the first node ever
	public ProblemNode getFirstNode() {
		return this.firstNode;
	}

	//Returns the ith node
	public ProblemNode getNode(int i) {
		ProblemNode e = firstNode;
		while (e != null) {
			if (e.getNodeOrder() == i)
				return e;
			e = e.getNextNode();
		}
		return null;
	}

	/**
	 * Find a node by name. This method is linear in the number of nodes in the
	 * graph.
	 * 
	 * @param name
	 *            node name to seek
	 * @return 1st node returned by {@link ProblemGraph#nodes()} with which
	 *         getName() matches name; null if none matches
	 */
	public ProblemNode getNode(String name) {
		for (Enumeration<ProblemNode> en = nodes(); en.hasMoreElements();) {
			ProblemNode node = (ProblemNode) en.nextElement();
			if (node.getName().equals(name))
				return node;
		}
		return null;
	}
	
	// Return the number of edges connecting node in this graph.
	public int degree(ProblemNode node) {
		int numEdges = 0;
		for (ProblemEdge e = firstEdge; e != null; e = e.nextEdge) {
			if (e.source == node || e.dest == node)
				numEdges++;
		}
		return numEdges;
	}

	// Return true if and only if there is an edge connecting node0 and
	// node1 in this graph. (If the graph is directed, node0 is the edge's
	// source and node1 is the destination.)
	public boolean containsEdge(ProblemNode node0, ProblemNode node1) {
		for (ProblemEdge e = firstEdge; e != null; e = e.nextEdge) {
			if (e.source == node0 && e.dest == node1)
				return true;
		}
		return false;
	}
	
	// Return the number of out-edges of node in this directed graph.
	public int outDegree(ProblemNode node) {
	    if (node == null)
            return 0;
        return node.getOutDegree();
	}

	// ////////// Transformers ////////////

	// Make the graph empty
	public void clear() {
//		trace.out("mt", "ProblemGaraph.clear()=>empty EdgeEnumeration");
		this.firstNode = null;
		this.firstEdge = null;
		this.numNodes = 0;
		doneNodeNames.clear();
	}

	/**
	 * @param node
	 * @return
	 */
	public ProblemNode addProblemNode(ProblemNode node) {
		if (node.getDoneState())
			addDoneName(node.getName(), node);
		if (firstNode != null)
			firstNode.setPrevNode(node);
		node.setPrevNode(null);
		node.setNextNode(firstNode);
		firstNode = node;
		numNodes++;
		
		node.setNodeOrder(numNodes);
		if (trace.getDebugCode("pm")) trace.out("pm", "addProblemNode("+node+"): size = " + numNodes);
		return node;
	}
	
	// Add to this graph a new edge connecting node0 and node1, but
	// containing no attribute, and return the new edge. (If the graph is
	// directed, node0 is the edge's source and node1 is the destination.)
	public ProblemEdge addEdge(ProblemNode node0, ProblemNode node1) {
		return addEdge(node0, node1, null);
	}

	//returns whether there already exists an edge between source and destination.
	public boolean doesEdgeExist(ProblemNode source, ProblemNode dest){
		ProblemEdge tempEdge = firstEdge;
		while(tempEdge!=null){
			if((tempEdge.source == source) &&(tempEdge.dest == dest)){
				return true;
			}
			tempEdge = tempEdge.nextEdge;
		}
		return false;
	}
	// Add to this graph a new edge connecting node0 and node1, and
	// containing attribute attr, and return the new edge. (If the graph is
	// directed, node0 is the edge's source and node1 is the destination.)
	public ProblemEdge addEdge(ProblemNode node0, ProblemNode node1,
			EdgeData edgeData) {
		ProblemNode source = node0;
		ProblemNode dest = node1;
		ProblemEdge edge = new ProblemEdge(source, dest, edgeData);
		// zz add to fix bug
		if (firstEdge != null)
			firstEdge.prevEdge = edge;
		edge.prevEdge = null;
		edge.nextEdge = firstEdge;
		firstEdge = edge;
		if (trace.getDebugCode("br")) trace.out("br", "addEdge("+source.getName()+"-"+dest.getName()+
				"), nextedge id("+(edge.nextEdge == null ? -1 : edge.nextEdge.getUniqueID())+")");
		source.setOutDegree(source.getOutDegree() + 1);
		edgeData.setEdge(edge);
		dumpEdges("addEdge("+source.getName()+"-"+dest.getName()+")");
		return edge;
	}

	private void dumpEdges(String label) {
		if (!trace.getDebugCode("br"))
			return;
		StringBuffer sb = new StringBuffer(label);
		for (ProblemEdge next = firstEdge; next != null; next = next.nextEdge)
			sb.append(" ").append(next.getUniqueID());
		if (trace.getDebugCode("br")) trace.out("br", "dumpEdges: "+sb);
	}

	// Remove node from this graph, together with all connecting edges.
	public void removeNode(ProblemNode node) {
		ProblemNode currNode = node;
		for (ProblemEdge e = firstEdge; e != null; e = e.nextEdge) {
			if (e.source == currNode || e.dest == currNode) {
				removeEdge(e);
			}
		}

		ProblemNode prevNode = currNode.getPrevNode();
		ProblemNode nextNode = currNode.getNextNode();
		if (prevNode == null)
			firstNode = nextNode;
		else
			prevNode.setNextNode(nextNode);
		if (nextNode != null)
			nextNode.setPrevNode(prevNode);
		numNodes--;
		doneNodeNames.remove(currNode.getName());
	}

	// Remove edge from this graph.
	public void removeEdge(ProblemEdge edge) {
		ProblemEdge prevEdge = edge.prevEdge;
		ProblemEdge nextEdge = edge.nextEdge;
		if (trace.getDebugCode("br")) trace.out("br", "removeEdge("+edge.getUniqueID()+") from between prev edge id("+
				(prevEdge == null ? -1 : prevEdge.getUniqueID())+")&("+
				(nextEdge == null ? -1 : nextEdge.getUniqueID())+")");
		if (prevEdge == null) {
			firstEdge = nextEdge;
//			trace.out("mt", "graph.removeEdge()=>firstEdge now " + firstEdge);
		} else
			prevEdge.nextEdge = nextEdge;
		if (nextEdge != null)
			nextEdge.prevEdge = prevEdge;
		edge.source.setOutDegree(edge.source.getOutDegree() - 1);
	}

	public ProblemEdge lookupProblemEdgeByID(int id) {

		ProblemEdge theEdge = null;

		Enumeration<ProblemEdge> edges = edges();
		while (edges.hasMoreElements()) {

			ProblemEdge edge = (ProblemEdge) edges.nextElement();
			EdgeData edgeData = edge.getEdgeData();
			if (edgeData.getUniqueID() == id) {
				theEdge = edge;
				break;
			}
		}

		return theEdge;
	}

	// ////////// Enumerations ////////////

	// Return an Enumeration that will visit all nodes of this graph, in no
	// particular order.
	public Enumeration<ProblemNode> nodes() {
		return new ProblemGraph.AllNodeEnumeration();
	}

	// Return an Enumeration that will visit all edges of this graph, in no
	// particular order.
	public Enumeration<ProblemEdge> edges() {
		return new ProblemGraph.AllEdgeEnumeration();
	}

	// Return an Enumeration that will visit all the neighbors of node in
	// this graph, in no particular order.
	public Enumeration<ProblemNode> neighbors(ProblemNode node) {
		return new NodeEnumeration(node, false);
	}

	/**
	 * Return an Enumeration that will visit all connecting edges of node in
	 * this graph, in no particular order.
	 * 
	 * @param node
	 * @return
	 */
	public Enumeration<ProblemEdge> getConnectingEdges(ProblemNode node) {
		return new EdgeEnumeration(node, true, true);
	}
	
	public Enumeration<ProblemEdge> getIncomingEdges(ProblemNode node){
		return new EdgeEnumeration(node,true, false);
	}
	/**
	 * Return an Enumeration that will visit all the out-EDGES of node in this
	 * directed graph, in no particular order.
	 * 
	 * @param node
	 * @return
	 */
	public Enumeration<ProblemEdge> getOutgoingEdges(ProblemNode node) {
		return new EdgeEnumeration(node, false, true);
	}
	// Return an Enumeration that will visit all the successors(NODES) of
	// node in this directed graph, in no particular order.
	public Enumeration<ProblemNode> successors(ProblemNode node) {
		return new NodeEnumeration(node, true);
	}

	public ProblemEdge lookupProblemEdge(ProblemNode parentNode, ProblemNode targetNode) {
	    Enumeration<ProblemEdge> connectingEdges = getConnectingEdges(parentNode);
            while (connectingEdges.hasMoreElements()) {
                ProblemEdge edge = (ProblemEdge) connectingEdges.nextElement();
                if (edge.getDest() == targetNode) {
                    return edge;
                }
            }
	    return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.BehaviorRecorder.Graph#parents(pact.BehaviorRecorder.Node)
	 */
	public Enumeration<ProblemNode> parents(ProblemNode node) {
		return new ProblemGraph.ParentsEnumeration(node);
	}

	public Vector<ProblemNode> siblings(ProblemNode node) {
		Enumeration<ProblemNode> parents = parents(node);
		Vector<ProblemNode> siblings = new Vector<ProblemNode>();
		while (parents.hasMoreElements()) {
			Enumeration<ProblemNode> successors = successors((ProblemNode) parents
					.nextElement());
			while (successors.hasMoreElements()) {
				ProblemNode sib = (ProblemNode) successors.nextElement();
				if (sib != node && !siblings.contains(sib))
					siblings.addElement(sib);
			}
		}
		return siblings;
	}

	public boolean isNodeInGraph(ProblemNode testNode) {
		ProblemNode tempNode;

		Enumeration<ProblemNode> iter = nodes();

		while (iter.hasMoreElements()) {
			tempNode = (ProblemNode) iter.nextElement();
			if (tempNode == testNode)
				return true;
		}

		return false;
	}

	// ////////////////////////////////////////////////////////////////////////

	private class AllNodeEnumeration implements Enumeration<ProblemNode> {

		private ProblemNode currNode;

		private AllNodeEnumeration() {
			currNode = firstNode;
		}

		public boolean hasMoreElements() {
			return (currNode != null);
		}

		public ProblemNode nextElement() {
			ProblemNode result = currNode;
			if (result == null)
				throw new NoSuchElementException();
			currNode = currNode.getNextNode();
			return result;
		}

		public void remove() {
			if (currNode == null)
				throw new IllegalStateException();
			removeNode(currNode);
		}
	}

	public boolean hasChildren(ProblemNode node) {

		Enumeration<ProblemEdge> edges = new ProblemGraph.AllEdgeEnumeration();
		while (edges.hasMoreElements()) {
			ProblemEdge currEdge = (ProblemEdge) edges.nextElement();
			if (currEdge.source == node) {
				return true;
			}
		}

		return false;
	}

	private class ParentsEnumeration implements Enumeration<ProblemNode> {

		private Enumeration<ProblemEdge> allEdges;

		private ProblemNode currNode, targetNode;

		private ParentsEnumeration(ProblemNode node) {
			this.targetNode = node;
			this.allEdges = new ProblemGraph.AllEdgeEnumeration();
			this.currNode = scanNextNode();
		}

		ProblemNode scanNextNode() {
			while (allEdges.hasMoreElements()) {
				ProblemEdge currEdge = (ProblemEdge) allEdges.nextElement();
				if (currEdge.dest == targetNode) {
					return currEdge.source;
				}
			}
			return null;
		}

		public boolean hasMoreElements() {
			return (currNode != null);
		}

		public ProblemNode nextElement() {
			ProblemNode result = currNode;
			if (result == null)
				throw new NoSuchElementException();
			currNode = scanNextNode();
			return result;
		}

		public void remove() {
			if (currNode == null)
				throw new IllegalStateException();
			removeNode(currNode);
		}
	}

	// ////////////////////////////////////////////////////////////////////////

	private class NodeEnumeration implements Enumeration<ProblemNode> {

		private Enumeration<ProblemEdge> allEdges;

		private ProblemNode currNode, targetNode;

		private boolean directed;

		private NodeEnumeration(ProblemNode node, boolean directed) {
			this.targetNode = node;
			this.directed = directed;
			this.allEdges = new ProblemGraph.AllEdgeEnumeration();
			this.currNode = scanNextNode();
		}

		ProblemNode scanNextNode() {
			while (allEdges.hasMoreElements()) {
				ProblemEdge currEdge = (ProblemEdge) allEdges.nextElement();
				if (currEdge.source == targetNode) {
					return currEdge.dest;
				} else if (!directed && currEdge.dest == targetNode) {
					return currEdge.source;
				}
			}
			return null;
		}

		public boolean hasMoreElements() {
			return (currNode != null);
		}

		public ProblemNode nextElement() {
			ProblemNode result = currNode;
			if (result == null)
				throw new NoSuchElementException();
			currNode = scanNextNode();
			return result;
		}

		public void remove() {
			if (currNode == null)
				throw new IllegalStateException();
			removeNode(currNode);
		}
	}

	// ////////////////////////////////////////////////////////////////////////

	private class AllEdgeEnumeration implements Enumeration<ProblemEdge> {

		ProblemEdge currEdge;

		private AllEdgeEnumeration() {
			currEdge = firstEdge;
		}

		public boolean hasMoreElements() {
			return (currEdge != null);
		}

		public ProblemEdge nextElement() {
			ProblemEdge result = currEdge;
			if (result == null)
				throw new NoSuchElementException();
			currEdge = currEdge.nextEdge;
			return result;
		}

		public void remove() {
			if (currEdge == null)
				throw new IllegalStateException();
			removeEdge(currEdge);
		}
	}

	// ////////////////////////////////////////////////////////////////////////

	private class EdgeEnumeration implements Enumeration<ProblemEdge> {

		private Enumeration<ProblemEdge> allEdges;

		ProblemEdge currEdge;

		private ProblemNode targetNode;

		private boolean incoming, outgoing;

		private EdgeEnumeration(ProblemNode node, boolean incoming, boolean outgoing) {

			this.allEdges = new ProblemGraph.AllEdgeEnumeration();
			this.targetNode = node;
			this.outgoing = outgoing;
			this.incoming = incoming;
			this.currEdge = scanNextEdge();
		}

		ProblemEdge scanNextEdge() {
			while (allEdges.hasMoreElements()) {
				ProblemEdge edge = (ProblemEdge) allEdges.nextElement();
				if (incoming){
					if ((edge.dest == targetNode))
						return edge;
				}
				if (outgoing){
					if ((edge.source == targetNode))
						return edge;
				}
			
			}
			return null;
		}

		public boolean hasMoreElements() {
			return (currEdge != null);
		}

		public ProblemEdge nextElement() {
			ProblemEdge result = currEdge;
			if (result == null)
				throw new NoSuchElementException();
			currEdge = scanNextEdge();
			return result;
		}

		public void remove() {
			if (currEdge == null)
				throw new IllegalStateException();
			removeEdge(currEdge);
		}
	}

	/**
	 * This method counts the number of edges in the graph each time it is
	 * called.
	 * 
	 * @return Returns the number of edges.
	 */
	public int getEdgeCount() {
		Enumeration<ProblemEdge> edges = edges();
		int count = 0;
		while (edges.hasMoreElements()) {
			edges.nextElement();
			count++;
		}
		return count;
	}

	/**
	 * @return true if the graph has any edges
	 */
	public boolean hasEdges() {
		return edges().hasMoreElements();
	}

	/**
	 * Add a new node to the set {@link #doneNodeNames} of names.
	 * @param doneNodeName new node's name
	 * @param doneNode new node
	 * @throws IllegalArgumentException if doneNodeName duplicates existing name
	 */
	private void addDoneName(String doneNodeName, ProblemNode doneNode) {
		if (doneNodeName == null || doneNodeName.length() < 1)
			return;
		ProblemNode existingDoneNode = doneNodeNames.get(doneNodeName);
		if (existingDoneNode != null)
			throw new IllegalArgumentException("Duplicate Done node name "+doneNodeName+
					" on nodes "+existingDoneNode.getUniqueID()+", "+doneNode.getUniqueID());
		doneNodeNames.put(doneNodeName, doneNode);
	}

	/**
	 * Return the name that would be given to the next done node.
	 * @return {@value #DONE_NODE_NAME} if no other done states;
	 * 			else {@value #DONE_NODE_NAME} with sizeof({@link #doneNodeNames}}) appended.
	 */
	public String nextDoneName() {
		int nextDoneNodeNo = doneNodeNames.size() + 1;
		if (nextDoneNodeNo <= 1)         // no "Done" nodes so far
			return DONE_NODE_NAME;
		String prefix = DONE_NODE_NAME + '_';
		String result;
		while (doneNodeNames.containsKey(result = prefix+nextDoneNodeNo))
			nextDoneNodeNo++;
		return result;
	}

	/**
	 * Rename a node. Updates {@link #doneNodeNames} if a done node.
	 * @param problemNode
	 * @param oldName
	 * @param newName
	 */
	public void renameNode(ProblemNode problemNode, String oldName,	String newName) {
		if (problemNode.isDoneState()) {
			doneNodeNames.remove(oldName);
			addDoneName(newName, problemNode);
		}
	}
}
