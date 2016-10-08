package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ExampleTracerNode {
	
	private int nodeID;
	private ArrayList<ExampleTracerLink> outLinks;
	private ProblemNode problemNode;
	private Set<ExampleTracerLink> inLinks;
	
	/**
	 * Constructor (initializes the outlinks with an empty list)
	 * @param nodeID
	 */
	public ExampleTracerNode(ProblemNode problemNode) {
		nodeID = problemNode.getUniqueID();
		this.problemNode = problemNode;
		outLinks = new ArrayList<ExampleTracerLink>();
		inLinks = new HashSet<ExampleTracerLink>();
	}
	
	/**
	 * Constructor
	 * @param nodeID
	 * @param outLinks
	 */
	ExampleTracerNode (int nodeID, ArrayList<ExampleTracerLink> outLinks) {
		this.nodeID = nodeID;
		this.outLinks = outLinks;
		inLinks = new HashSet<ExampleTracerLink>();
	}
	
	ExampleTracerNode (int nodeID) {
		this(nodeID, new ArrayList<ExampleTracerLink>());
	}
	
	/**
	 * @return - returns the Node ID
	 */
	public int getNodeID () {
		return this.nodeID;
	}
	
	/**
	 * @return - returns the outlinks
	 */
	public ArrayList<ExampleTracerLink> getOutLinks() {
		return this.outLinks;
	}
	
	/**
	 * Adds a link to the outLinks
	 * @param link
	 */
	void addOutLink(ExampleTracerLink link) {
		this.outLinks.add(link);
	}

	public ProblemNode getProblemNode() {
		return problemNode;
	}
	
	/**
	 * Return the inlink with the given id.
	 * @param id
	 * @return {@link #inLinks} entry; null if not found
	 */
	boolean containsInLink(ExampleTracerLink link) {
		return inLinks.contains(link);
	}
	
	public Set<ExampleTracerLink> getInLinks() {
		return inLinks;
	}
	
	/**
	 * Nulls {@link #inLinks}.
	 */
	void clearInLinks() {
		inLinks.clear();
	}
	
	/**
	 * Add an element to {@link #inLinks}.
	 * @param link link to add
	 */
	void addInLink(ExampleTracerLink link) {
		inLinks.add(link);
	}
}