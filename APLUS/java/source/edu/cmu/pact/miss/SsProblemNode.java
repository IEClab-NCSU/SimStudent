/**
 * 
 */
package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

/**
 * @author mazda
 *
 */
public class SsProblemNode extends ProblemNode {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	Vector<SsProblemEdge> outGoingEdges = new Vector<SsProblemEdge>();
	public Vector<SsProblemEdge> getOutGoingEdges() {
		return outGoingEdges;
	}
	public void setOutGoingEdges(Vector<SsProblemEdge> outGoingEdge) {
		this.outGoingEdges = outGoingEdge;
	}

	Vector<ProblemNode> children = new Vector<ProblemNode>();
	public Vector<ProblemNode> getChildren() { return children; }
	public void setChildren(Vector<ProblemNode> children) { this.children = children; }
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
}
