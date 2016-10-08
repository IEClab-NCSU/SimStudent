package edu.cmu.pact.miss;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class AskHintClAlgebraTutor extends AskHint {

    public AskHintClAlgebraTutor(BR_Controller brController, ProblemNode currentNode) {
        getHint(brController, currentNode);
        //Don't update the node in the BR as it is just used for logging purpose
        //if (!SimSt.KILL_INTERACTIVE_LEARNING.equals(getSkillName())) {
        //	updateNodeInBR(brController, currentNode);
        //}
    }
    
    public void getHint(BR_Controller brController, ProblemNode currentNode) {
        
        InquiryClAlgebraTutor icat = 
            new InquiryClAlgebraTutor(SimSt.getClAlgebraTutoringServiceHost(), SimSt.getClAlgebraTutoringServicePort());
        
        icat.setAskingHintOn();
        int numPrevSteps = icat.clAlgebraTutorGotoOneStateBefore(brController, currentNode);
        String hintMsg = icat.askHint(brController, currentNode, numPrevSteps);
        icat.shutdown();

        ClAlgebraTutorHint tutorHint = new ClAlgebraTutorHint(hintMsg);
        setSkillName(tutorHint.getSkillName());
        setSai(tutorHint.getSAI(brController, currentNode));
        
    }
}
