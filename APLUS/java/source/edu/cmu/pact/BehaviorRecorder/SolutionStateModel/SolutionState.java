/*
 * Created on Mar 9, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.SolutionStateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 *
 * Created on: Mar 9, 2005
 */

public class SolutionState {
    
    
    private ProblemNode currentNode; // current state node
    
    private List<ProblemEdge> userVisitedEdges;


    private ProblemModel problemModel;
    
    public SolutionState(ProblemModel problemModel) {
		if(trace.getDebugCode("ss"))
			trace.out("ss", "SolutionState.SolutionState(\""+problemModel+"\")");
        this.problemModel = problemModel;
        resetUserVisitedEdges();
    }

    /**
     * @param currNode The currNode to set.
     */
    public void setCurrentNode(ProblemNode currNode) {
        this.currentNode = currNode;
    }

    /**
     * @return Returns the currNode.
     */
    public ProblemNode getCurrentNode() {
        return currentNode;
    }

    /**
     * @return Returns the userVisitedStates.
     */
    public List<ProblemEdge> getUserVisitedEdges() {
        return userVisitedEdges;
    }

	public void addUserVisitedEdge(ProblemEdge visitedEdge) {
		if(trace.getDebugCode("ss"))
			trace.outNT("ss", "SolutionState.addUserVisitedEdge("+visitedEdge+"): size was "+
					(userVisitedEdges == null ? -1 : userVisitedEdges.size()));
		userVisitedEdges.add(visitedEdge);
	}

	/**
	 * Create a new empty {@link #userVisitedEdges}.
	 */
	public void resetUserVisitedEdges() {
		if(trace.getDebugCode("ss"))
			trace.out("ss", "SolutionState.resetUserVisitedEdges(): size was "+
					(userVisitedEdges == null ? -1 : userVisitedEdges.size()));
		userVisitedEdges = new ArrayList<ProblemEdge>();
	}
	
  

    // test whether v is current Node or current node parent.
    public boolean isCurrentNodeOrParent(ProblemNode nodeTest) {
        Vector parentEdgeslist = new Vector();

        problemModel.findParentEdgesList(getCurrentNode(),
                parentEdgeslist);

        ProblemEdge edgeTemp;
        ProblemNode nodeTemp;

        for (int i = 0; i < parentEdgeslist.size(); i++) {
            edgeTemp = (ProblemEdge) parentEdgeslist.elementAt(i);
            nodeTemp = edgeTemp.getNodes()[ProblemEdge.DEST];

            if (nodeTest == nodeTemp)
                return true;
        }

        return false;
    }

    

    public void reset() {
    	
    	resetUserVisitedEdges();
        setCurrentNode(null);
    }

	
}
