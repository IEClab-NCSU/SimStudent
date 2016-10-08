package edu.cmu.pact.miss;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class AskHintTutoringServiceFake extends AskHint {

    // do not use this constructor
    public AskHintTutoringServiceFake(BR_Controller brController, ProblemNode currentNode){
    }
    //do not use this method
    public void getHint(BR_Controller brController, ProblemNode currentNode) {
    }

    
    
    public AskHintTutoringServiceFake(BR_Controller brController, ProblemNode currentNode, SimStInteractiveLearning simstIl){
        getHint(brController, currentNode, simstIl);
    }
    
    //Gustavo 17 May 2007: gets the SAI from the user, as a comma-separated string
    private Sai askSai(BR_Controller brController) {

        JFrame frame = brController.getActiveWindow();
        
        String result = (String)JOptionPane.showInputDialog(
                frame,
                "Please enter the SAI for the step to be demonstrated.\n",
                "Please enter the SAI",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
                
        String[] sp = result.split(",");
        return new Sai(sp[0],sp[1],sp[2]);
    }

    //Gustavo 17April2007
    //prompts the user for a skill-name with a dialog
    private String askSkillName(BR_Controller brController) {

        JFrame frame = brController.getActiveWindow();
        
        String result = (String)JOptionPane.showInputDialog(
                frame,
                "Please enter the skill-name for the step demonstrated.\n"+
                "If you'd like SimStudent to learn it unlabeled, just click OK.",
                "Please enter the skill-name",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
                
        return result;
    }
    

/**
 * Gustavo 9 Sep 2007: this is hard-coded for IIL.
 * 
 * will need to let the Behavior Recorder create the node itself, if it judges necessary.
 * @param simstIl 
 */
    public void getHint(BR_Controller brController, ProblemNode currentNode, SimStInteractiveLearning simstIl){
        
        SimSt simSt = brController.getMissController().getSimSt();
        
//        String title = "FAKE TUTORING SERVICE";
//
//      String message[] = {
//              "SimStudent has run out of matching rules.",
//      "Please demonstrate in the Student Interface"};
//      JFrame frame = brController.getActiveWindow();
//      JOptionPane.showMessageDialog(frame, message, title,
//              JOptionPane.PLAIN_MESSAGE);
      
      //SAI dialog
      setSai(askSai(brController));
      
      //skillname dialog
      this.skillName = askSkillName(brController);        

      /*
      ProblemNode prevCurrentNode = brController.getCurrentNode();
      this.node = simstIl.simulatePerformingStep(prevCurrentNode, getSai());      
      this.edge = simSt.lookupProblemEdge(prevCurrentNode, this.node);
      */
    }

    
    
}
