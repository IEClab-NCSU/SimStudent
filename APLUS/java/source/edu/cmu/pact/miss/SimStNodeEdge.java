package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class SimStNodeEdge {
    public ProblemNode node;
    public ProblemEdge edge;
    SimStNodeEdge(ProblemNode n, ProblemEdge e){
        node = n;
        edge = e;
    }
}

