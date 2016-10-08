package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class AskHintFakeClAlgebraTutor extends AskHint {

    public AskHintFakeClAlgebraTutor(BR_Controller brController, ProblemNode currentNode) {
        getHint(brController, currentNode);
    }
    //read the hint file
    public void getHint(BR_Controller brController, ProblemNode currentNode) {
        //first, we need to find the correct line in the hints file
        SimSt simSt = brController.getMissController().getSimSt();
        String problemName = brController.getProblemModel().getProblemName();
        String textSai = simSt.lookupHint(problemName,currentNode);
        setSai(new Sai (textSai));
        
        /*
        SimStNodeEdge newNodeEdge = simSt.makeNewNodeAndEdge(getSai(), currentNode);
        setNode(newNodeEdge.node);
        setEdge(newNodeEdge.edge);
        */
    }
}
