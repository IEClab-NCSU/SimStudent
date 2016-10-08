package edu.cmu.pact.miss.ProblemModel.Graph;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;

/**
 * Edge to connect the SimStNode. Every edge has a source and a 
 * destination and an edgeData to describe the Selection, Action,
 * Input to reach destination from source. Modifying it to extend
 * ProblemEdge to work with the current oracle.
 */
public class SimStEdge extends ProblemEdge{

	/** */
	SimStEdgeData edgeData;
	
	/**	 */
	public SimStNode source, dest;

	/**  */
	public SimStEdge previousEdge, nextEdge;
	
	SimStEdge(SimStNode sourceNode, SimStNode destinationNode, SimStEdgeData eData){
		super();
		this.source = sourceNode;
		this.dest = destinationNode;
		this.edgeData = eData;
		this.previousEdge = null;
		this.nextEdge = null;
	}

	public SimStNode[] getNodes(){
		return new SimStNode[]{this.source, this.dest};
	}
	
	public String getSelection(){
		return (String)edgeData.getSelection().get(0);
	}
	
	public String getAction(){
		return (String)edgeData.getAction().get(0);
	}
	
	public String getInput(){
		return (String)edgeData.getInput().get(0);
	}
	
	public Sai getSai(){
		Sai sai = new Sai(edgeData.getSelection().toString(), edgeData.getAction().toString(), edgeData.getInput().toString());
		return sai;
	}
	
	public boolean isCorrect(){
		String actionType = edgeData.getActionType();
		return actionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION);
	}
	
	public String toString(){
		return " Edge [ " + source + " ," + dest + "  ]";
	}
	
	public SimStEdgeData getEdgeData() {
		return edgeData;
	}

	public void setEdgeData(SimStEdgeData edgeData) {
		this.edgeData = edgeData;
	}

	public void setSource(SimStNode source) {
		this.source = source;
	}

	public SimStNode getSource() {
		return source;
	}
	
	public void setDest(SimStNode dest) {
		this.dest = dest;
	}

	public SimStNode getDest() {
		return dest;
	}
}
