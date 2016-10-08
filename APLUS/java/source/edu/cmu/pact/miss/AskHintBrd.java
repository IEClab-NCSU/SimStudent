package edu.cmu.pact.miss;

import java.util.Iterator;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class AskHintBrd extends AskHint {

    public AskHintBrd(BR_Controller brController, ProblemNode currentNode){
        getHint(brController, currentNode);
    }

    public void getHint(BR_Controller brController, ProblemNode currentNode){
        
        SimSt simSt = brController.getMissController().getSimSt();

        ProblemNode child = null;
        ProblemEdge edge = null;

        //pick first edge that is labeled "Correct"
        for (Iterator iter = currentNode.getChildren().iterator(); iter.hasNext();) {
            child = (ProblemNode) iter.next();
            edge = simSt.lookupProblemEdge(currentNode, child);
            if (edge.isCorrect())
                break;
        }
        
        EdgeData edgeData = edge.getEdgeData();
        setSai(new Sai(edgeData.getSelection(), edgeData.getAction(), edgeData.getInput()));
        Vector skillNames = edgeData.getSkills();
        
        String skillName = ((String)skillNames.get(0)).split(" ")[0];        
        this.skillName = removeAutoFromSkillName(skillName); //taken from runDemonstrationInBatchMode

        /*
        this.node = child;
        this.edge = edge;
        */
    }

    public String removeAutoFromSkillName(String skillName){
        String res = skillName;
        int indexAuto = skillName.indexOf("-auto");
        if (indexAuto>0)
            res = res.substring(0, indexAuto)+res.substring(indexAuto+5);
        return res;
    }
    
    
}
