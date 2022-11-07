package edu.cmu.pact.miss.ProblemModel.Graph;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Rule;

/**
 *
 */
public class SimStNode extends ProblemNode{

	/**	 */
	private SimStNode prevNode;
	
	/**  */
	private SimStNode nextNode;
	
	/**	 */
	private int outDegree;
	
	/**	 */
	private String name;
	
	/**	 */
	private boolean doneState = false;
	
	/**  */
	private SimStProblemGraph problemGraph;
	
	/**	 */
	private List<SimStNode> children;
	
	public SimStNode(String name, SimStProblemGraph graph){
		super();
		this.name = name;
		this.problemGraph = graph;
		this.outDegree = 0;
		this.setPrevNode(null);
		this.setNextNode(null);
	}
	
	public int getInDegree(){
		return getIncomingEdges().size();
	}
	
    public ProblemEdge isChildNode(ProblemNode node) {
        List<ProblemEdge> outgoingEdges = getOutgoingEdges();
        for (int i = 0; i < outgoingEdges.size(); i++) {
        	SimStEdge ssEdge = (SimStEdge)outgoingEdges.get(i);
            if (node.equals(ssEdge.getDest())) {
                return ssEdge;
            }
        }
        return null;
    }

	public Vector<ProblemNode> getChildren(){
		
		Vector<ProblemNode> children = new Vector<ProblemNode>();
		List<ProblemEdge> outgoingEdges = getOutgoingEdges();
		for(Iterator itr = outgoingEdges.iterator(); itr.hasNext();){
			SimStEdge ssEdge = (SimStEdge) itr.next();
			children.add(ssEdge.getDest());
		}
		
		return children;
	}

	public Vector getParents(){
		
		Vector vec = new Vector();
		Enumeration parents = getProblemGraph().parents(this);
		while(parents.hasMoreElements()){
			SimStNode ssNode = (SimStNode) parents.nextElement();
			vec.add(ssNode);
		}
		return vec;
	}
	
	public List<ProblemEdge> getOutgoingEdges() {

		ArrayList list = new ArrayList();
		for(Enumeration iterEdges = getProblemGraph().getOutgoingEdges(this); iterEdges.hasMoreElements();){
			SimStEdge edge = (SimStEdge) iterEdges.nextElement();
			list.add(edge);
		}
		return list;
	}

	public List getIncomingEdges(){
		ArrayList list = new ArrayList();
		for(Enumeration iterEdges = getProblemGraph().getIncomingEdges(this); iterEdges.hasMoreElements();){
			SimStEdge edge = (SimStEdge) iterEdges.nextElement();
			list.add(edge);
		}
		return list;
	}

	public boolean isLeaf(){
		return getProblemGraph().isLeaf(this);
	}
	
    public Vector<ProblemEdge> findSolutionPath() {
        return findSolutionPath(new String[2]);
    }

    public Vector<ProblemEdge> findSolutionPath(String[] equation) {
        // This is a leaf node
        if (isLeaf()) {
            return new Vector<ProblemEdge>();
        }
        
        List<ProblemEdge> outEdges = getOutgoingEdges();
        Vector<ProblemEdge> path;
        ProblemEdge backupWrongAnswer = null;
        for (ProblemEdge edge : outEdges) { 
        	if (edge.isCorrect()) {
                // AlgebraV2 Tutor: "commTableX_CaRb"
                String selection = (String) edge.getEdgeData().getSelection().get(0);
                String input = (String) edge.getEdgeData().getAction().get(0);

                if(selection.equalsIgnoreCase(Rule.DONE_NAME))
                {
                	
                	path = new Vector<ProblemEdge>();
                	path.add(0,edge);
                	return path;
                }
                else
                {
                	if (selection.contains("dorminTable")){
	                	int idx = selection.indexOf("_") -1;
	                	char c = selection.charAt(idx);
	                	int col = c - '1' +1;

	                	switch (col) {
	                		case 1: equation[0] = input; break;
	                		case 2: equation[1] = input; break;
	                		case 3: equation[0] = equation[1] = null; break;
	                	}
                	}

                    SimStNode child = (SimStNode) edge.getDest();
                    path = child.findSolutionPath(equation);
                }
                if (path != null) {
                    path.add(0, edge);
                    // trace.out("findSolutionPathAlgebra() returning " + path.size() + "...");
                    return path;
                }
                else
                {
                	path = new Vector<ProblemEdge>();
                    path.add(0, edge);
                    return path;
                }
            }
            else
            {
            	if(!edge.getEdgeData().getInput().get(0).toString().equalsIgnoreCase("FALSE"))
            	{
            		if(backupWrongAnswer != null)
            		{
            			if(backupWrongAnswer.getDest().isLeaf() && !edge.getDest().isLeaf())
                			backupWrongAnswer = edge;
            		}
            		else
            		{
            			backupWrongAnswer = edge;
            		}
            	}
            }
        }
        if(backupWrongAnswer != null)
        {
        	
            SimStNode child = (SimStNode) backupWrongAnswer.getDest();
            path = child.findSolutionPath(equation);
            
            if (path != null) {
                path.add(0, backupWrongAnswer);
                // trace.out("findSolutionPathAlgebra() returning " + path.size() + "...");
                return path;
            }
            else
            {
            	path = new Vector<ProblemEdge>();
                path.add(0, backupWrongAnswer);
                return path;
            }
        }
        return null;
    } 
	
    public Vector findPathToNode(SimStNode ssNode){
    
    	SimStNode endNode = ssNode;
    	Vector pathEdges = new Vector();
    	return findPathDepthFirst(getProblemGraph().getStartNode(), endNode);
    }
    
	private Vector findPathDepthFirst(ProblemNode /*SimStNode*/ startNode, ProblemNode /*SimStNode*/ endNode){
		
		/* Base case */
		if(startNode == endNode)
			return null;
		
		/* Recursive case */
		SimStEdge edge = null;
		if((edge = (SimStEdge) (startNode.isChildNode(endNode))) != null){
			Vector path = new Vector();
			path.add(0, edge);
			return path;
		} else {
			Vector children = startNode.getChildren();
			if(children.isEmpty())
				return null;
			for(int i=0; i < children.size(); i++){
				SimStNode childNode = (SimStNode) children.elementAt(i);
				Vector path = findPathDepthFirst(childNode, endNode);
				if(path != null) {
					path.add(0, startNode.isChildNode(childNode));
					return path;
				}
			}
			return null;
		}
	}

	public String toString(){
    	return getName();
    }
    
	public SimStNode getPrevNode() {
		return prevNode;
	}

	public void setPrevNode(SimStNode prevNode) {
		this.prevNode = prevNode;
	}

	public SimStNode getNextNode() {
		return nextNode;
	}

	public void setNextNode(SimStNode nextNode) {
		this.nextNode = nextNode;
	}

	public int getOutDegree() {
		return outDegree;
	}

	public void setOutDegree(int outDegree) {
		this.outDegree = outDegree;
	}

	public boolean isDoneState() {
		return doneState;
	}

	public void setDoneState(boolean doneState) {
		this.doneState = doneState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public SimStProblemGraph getProblemGraph() {
		return problemGraph;
	}
}
