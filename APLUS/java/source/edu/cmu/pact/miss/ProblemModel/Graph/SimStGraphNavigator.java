package edu.cmu.pact.miss.ProblemModel.Graph;

import java.util.Vector;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;

public class SimStGraphNavigator {

	public SimStNode simulatePerformingStep(SimStNode currentNode, Sai sai) {
    	SimStNode successiveNode = null;

    	SSNodeEdge nodeEdge = lookupNodeWithSai(sai, currentNode);
    	
        if (nodeEdge == null || !nodeEdge.edge.isCorrect()){
        	nodeEdge = makeNewNodeAndEdge(sai, currentNode);
        }

        try {
        	successiveNode = nodeEdge.node;
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return successiveNode;
    }

    private SSNodeEdge makeNewNodeAndEdge(Sai sai, SimStNode currentNode){
    	
    	SimStProblemGraph problemGraph = currentNode.getProblemGraph();
    	boolean isDoneState = false;
    	SimStNode newNode = new SimStNode("state " + problemGraph.getNodeUniqueIDGenerator(), problemGraph);
    	problemGraph.addSSNode(newNode);
    	
    	if(sai.getS().equalsIgnoreCase("done")){
    		newNode.setDoneState(true);
    	}
    	
    	SimStEdgeData newEdgeData = new SimStEdgeData();
    	newEdgeData.setSelection(sai.getS());
    	newEdgeData.setAction(sai.getA());
    	newEdgeData.setInput(sai.getI());
    	newEdgeData.setActionType(EdgeData.CORRECT_ACTION);
    	
    	SimStEdge newEdge = problemGraph.addSSEdge(currentNode, newNode, newEdgeData);
    	return new SSNodeEdge(newNode, newEdge);
    }
    
    private SSNodeEdge lookupNodeWithSai(Sai sai, SimStNode currentNode) {
    	SSNodeEdge nodeEdge = null;
    	
        Vector  children = currentNode.getChildren();
        for (int i = 0; i<children.size(); i++) {
        	SimStNode child = (SimStNode) children.get(i);
        	SimStEdge edge = currentNode.getProblemGraph().lookUpSSEdge(currentNode, child);
            Sai edgeSai = edge.getSai();
            
            if (sai.getS().equalsIgnoreCase(edgeSai.getS()) && sai.getA().equalsIgnoreCase(edgeSai.getA())
            		&& sai.getI().equalsIgnoreCase(edgeSai.getI())){
                nodeEdge = new SSNodeEdge(child, edge);
                break;
            }
        }
        return nodeEdge;
    }
    

    private class SSNodeEdge {
    	
    	public SimStNode node;
    	public SimStEdge edge;
    	SSNodeEdge(SimStNode ssNode, SimStEdge ssEdge){
    		node = ssNode;
    		edge = ssEdge;
    	}
    }
	
}
