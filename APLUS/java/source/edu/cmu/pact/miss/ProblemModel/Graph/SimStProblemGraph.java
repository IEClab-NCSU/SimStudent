package edu.cmu.pact.miss.ProblemModel.Graph;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.Utilities.trace;

/**
 * Graph to hold the nodes and edges for the quiz. 
 */
public class SimStProblemGraph extends ProblemGraph{

	/**	 */
	private SimStNode startNode;
	
	/**	 */
	private SimStNode firstNode;
	
	/**	 */
	private SimStEdge firstEdge;
	
	/**	 */
	private int numSSNodes = 0;

	/**  */
	private int nodeUniqueIDGenerator = 0;
	
	public SimStProblemGraph(SimStNode startStateNode){
		super();
		startNode = startStateNode;
		firstNode = null;
		firstEdge = null;
		++numSSNodes;
	}
	
	public SimStProblemGraph(){
		super();
		startNode = null;
		firstNode = null;
		firstEdge = null;
		numSSNodes = 0;
	}
	
	public SimStNode addSSNode(SimStNode ssNode){
		
		if(firstNode != null)
			firstNode.setPrevNode(ssNode);
		ssNode.setPrevNode(null);
		ssNode.setNextNode(firstNode);
		firstNode = ssNode;
		numSSNodes++;
		
		return firstNode;
	}
	
	public SimStEdge addSSEdge(SimStNode node0, SimStNode node1, SimStEdgeData edgeData){

		SimStNode source = node0;
		SimStNode destination = node1;
		SimStEdge edge = new SimStEdge(source, destination, edgeData);
		if(firstEdge != null)
			firstEdge.previousEdge = edge;
		edge.previousEdge = null;
		edge.nextEdge = firstEdge;
		firstEdge = edge;
		
		source.setOutDegree(source.getOutDegree() + 1);
		return edge;
	}
	
	public boolean containsEdge(SimStNode node0, SimStNode node1){

		for(SimStEdge e=firstEdge; e!=null; e=firstEdge.nextEdge){
			if(e.source==node0 && e.dest==node1)
				return true;
		}
		return false;
	}
	
	public int getNodeUniqueIDGenerator() {
		++nodeUniqueIDGenerator;
		return nodeUniqueIDGenerator;
	}

	public int outDegree(SimStNode node){
		if(node == null)
			return 0;
		return node.getOutDegree();
	}
	
	public int getNodeCount(){
		return this.numSSNodes;
	}
	
	public SimStNode getFirstNode(){
		return this.firstNode;
	}
	
	public SimStNode getStartNode() {
		return startNode;
	}

	public void setStartNode(SimStNode startNode) {
		this.startNode = startNode;
	}

	public void resetGraph(){
		this.firstEdge = null;
		this.firstNode = null;
		this.numSSNodes = 0;
	}
	
	public void removeNode(SimStNode node){
		SimStNode currentNode = node;
		for(SimStEdge e=firstEdge; e != null; e=e.nextEdge){
			if(e.source == currentNode || e.dest == currentNode)
				removeEdge(e);
		}
		
		SimStNode prevNode = currentNode.getPrevNode();
		SimStNode nextNode = currentNode.getNextNode();
		if(prevNode == null)
			firstNode = nextNode;
		else
			prevNode.setNextNode(nextNode);
		if(nextNode != null)
			nextNode.setPrevNode(prevNode);
		numSSNodes--;
	}
	
	public void removeEdge(SimStEdge edge){
		SimStEdge prevEdge = edge.previousEdge;
		SimStEdge nextEdge = edge.nextEdge;
		if(prevEdge == null)
			firstEdge = nextEdge;
		else 
			prevEdge.nextEdge = nextEdge;
		if(nextEdge != null)
			nextEdge.previousEdge = prevEdge;
		edge.getSource().setOutDegree(edge.getSource().getOutDegree() - 1);
	}
	
	public SimStEdge lookUpSSEdge(SimStNode parentNode, SimStNode targetNode){
		Enumeration<SimStEdge> connectingEdges = getConnectingEdges(parentNode);
		while(connectingEdges.hasMoreElements()){
			SimStEdge edge = connectingEdges.nextElement();
			if(edge.getDest()==targetNode)
				return edge;
		}
		return null;
	}
	
	public boolean isLeaf(SimStNode node){
		return (node.getOutDegree() == 0);
	}
	
	public Enumeration<SimStEdge> getIncomingEdges(SimStNode node){
		return new EdgeEnumeration(node, true, false);
	}
	
	public Enumeration<SimStEdge> getOutgoingEdges(SimStNode node) {
		return new EdgeEnumeration(node, false, true);
	}
	
	public Enumeration<SimStEdge> getConnectingEdges(SimStNode node){
		return new EdgeEnumeration(node, true, true);
	}
	
	public Enumeration<SimStNode> parents(SimStNode node){
		return new SimStProblemGraph.ParentsEnumeration(node);
	}
	
	/**
	 * Inner helper classes to deal with Graph
	 */
	private class EdgeEnumeration implements Enumeration<SimStEdge> {
		
		private Enumeration<SimStEdge> allEdges;
		
		SimStEdge currEdge;
		
		private SimStNode targetNode;
		
		private boolean incoming, outgoing;
		
		private EdgeEnumeration(SimStNode node, boolean incoming, boolean outgoing) {

			this.allEdges = new SimStProblemGraph.AllEdgeEnumeration();
			this.targetNode = node;
			this.outgoing = outgoing;
			this.incoming = incoming;
			this.currEdge = scanNextEdge();
		}

		SimStEdge scanNextEdge() {
			while (allEdges.hasMoreElements()) {
				SimStEdge edge = (SimStEdge) allEdges.nextElement();
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

		public SimStEdge nextElement() {
			SimStEdge result = currEdge;
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
	 * Inner helper classes to deal with Graph
	 */
	private class ParentsEnumeration implements Enumeration<SimStNode> {
		
		private Enumeration<SimStEdge> allEdges;
		
		private SimStNode currNode, targetNode;
		
		private ParentsEnumeration(SimStNode ssNode){
			this.targetNode = ssNode;
			this.allEdges = new SimStProblemGraph.AllEdgeEnumeration();
			this.currNode = scanNextNode();
		}
		
		SimStNode scanNextNode(){
			while(allEdges.hasMoreElements()) {
				SimStEdge edge = (SimStEdge)allEdges.nextElement();
				if(edge.dest == targetNode) {
					return edge.source;
				}
			}
			return null;
		}
		
		public boolean hasMoreElements() {
			return (currNode != null);
		}

		public SimStNode nextElement() {
			SimStNode result = currNode;
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
	
	/**
	 * Inner helper classes to deal with Graph
	 */
	private class AllEdgeEnumeration implements Enumeration<SimStEdge> {

		SimStEdge currEdge;

		private AllEdgeEnumeration() {
			currEdge = firstEdge;
		}

		public boolean hasMoreElements() {
			return (currEdge != null);
		}

		public SimStEdge nextElement() {
			SimStEdge result = currEdge;
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

}
