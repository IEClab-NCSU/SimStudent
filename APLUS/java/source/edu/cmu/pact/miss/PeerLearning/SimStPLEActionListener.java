package edu.cmu.pact.miss.PeerLearning;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jess.Funcall;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.console.controller.MissController;
import edu.cmu.pact.miss.jess.AplusController;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;

public class SimStPLEActionListener implements ActionListener, ChangeListener {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    BR_Controller brController = null;
    
    SimStLogger logger;
    
    long lastTabViewStart;
    String lastTabViewed;
    
	private long exampleStartTime = 0;
	private long quizStartTime = 0;
	private String exampleTitle = "";
	private String quizTitle = "";
    
    public long getExampleStartTime() {
		return exampleStartTime;
	}

	public void setExampleStartTime(long exampleStartTime) {
		this.exampleStartTime = exampleStartTime;
	}

	int previousSliderValue;

        
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public SimStPLEActionListener(BR_Controller brController) {
        setBrController(brController);
        logger = new SimStLogger(getBrController());
        
        lastTabViewStart = Calendar.getInstance().getTimeInMillis(); //initial viewing begins when ActionListener first created
        lastTabViewed = "";
        previousSliderValue = 0;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    //when a button is clicked
    public void actionPerformed(ActionEvent event) {
    	
    	
        String actionCommand = event.getActionCommand();
        
        String sourceText = "";
        
        //check to see what the button used said
        if(event.getSource() instanceof JButton)
        {
        	if(!(((JButton) event.getSource()).isEnabled()))
        	{
        		if(trace.getDebugCode("ss"))trace.out("ss", "Button is not enabled!");
        		return;
        	}
        	sourceText = ((JButton)event.getSource()).getText();
        }
        
        if(brController.getMissController() != null && brController.getMissController().getSimSt() != null && brController.getMissController().getSimSt().isSsMetaTutorMode()) {
        	if(brController.getAmt() != null && !(SimStPLE.UNDO.equals(event.getActionCommand()))) {
        		if(trace.getDebugCode("rr"))trace.out("rr", "Modeltracing the student action: " + event.getActionCommand() + "  ButtonPressed" + "  -1");
        		brController.getAmt().handleInterfaceAction(event.getActionCommand(), "ButtonPressed", "-1");
        	}
        }
        
        if (SimStPLE.NEXT_PROBLEM.equals(actionCommand)) {
        	
        	if (brController.getMissController().getSimSt().newProblemButtonLockFlag==false){
        		return;
        	}
        	
        	brController.getMissController().getSimStPLE().setIsRestartClicked(false);

        	
          	this.getMissController().getSimStPLE().resetRestartClickCount();
          	
          	if (brController.getMissController().getSimSt().isSsMetaTutorMode()){
          		brController.getMissController().getSimSt().getModelTraceWM().setSolutionCheckError("false");
          		brController.getMissController().getSimSt().getModelTraceWM().setNextSelection("nil");
          		brController.getMissController().getSimSt().getModelTraceWM().setNextAction("nil");
          		brController.getMissController().getSimSt().getModelTraceWM().setNextInput("nil");
          		brController.getMissController().getSimSt().getModelTraceWM().setSolutionGiven("false");
          		brController.getMissController().getSimSt().getModelTraceWM().setRestartCount(0);
          		brController.getAmt().pendingCount.clear();   	
          		brController.getMissController().getSimSt().getModelTraceWM().setSimStudentThinking(WorkingMemoryConstants.FALSE);
        		brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(null);
          	}
        	  
        	
        	if(sourceText.equals(brController.getMissController().getSimStPLE().getProblemEnteredButtonString()))
        		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.PROBLEM_ENTERED_BUTTON_ACTION,"");
        	else
        		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.NEXT_PROBLEM_BUTTON_ACTION,"");
        	if((brController.getCurrentNode()!= null && !brController.getCurrentNode().isDoneState())
        			/*|| !brController.getMissController().getSimStPLE().getStatus().equals(SimStPLE.FINISHED_STATUS)*/)
        	{
        		SimSt simSt = getMissController().getSimSt();
            	int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis() - simSt.getSsInteractiveLearning().getProblemStartTime())/1000);
            	logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_ABANDONED_ACTION, simSt.getProblemStepString(),
            			"","",problemDuration);
        	}
        	//the current problem is completed
        	else if(brController.getCurrentNode()!= null && brController.getCurrentNode().isDoneState())
        	{
        		SimSt simSt = getMissController().getSimSt();
            	int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis() - simSt.getSsInteractiveLearning().getProblemStartTime())/1000);
            	logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_COMPLETED_ACTION, simSt.getProblemStepString(),
            			"","",problemDuration);  	
        	}
        	
        	if (getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
        		getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering=false;
				AplusPlatform aplus=((AplusPlatform) getMissController().getSimStPLE().getSimStPeerTutoringPlatform());
                aplus.setRestartButtonEnabled(false);

        	}

			
        	//move on to the next problem
            getMissController().pleNextProblem();
            
            
        
			if (getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
				this.getBrController().getUniversalToolProxy().resetStartStateModel();	
				/*reset the flags for the start state */
				getBrController().getMissController().getSimStPLE().setIsStartStateCompleted(false);
 				getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering=false;

				this.getBrController().getMissController().getSimStPLE().unblockAllButStartState(false);
				getMissController().getSimStPLE().getSsCognitiveTutor().clearStartStateFromStudentInterface();
				getMissController().getSimStPLE().setFocusOfStartStateElementsStudentInterface(true);


			}
			
			((AplusPlatform) getMissController().getSimStPLE().getSimStPeerTutoringPlatform()).aplusControlConfirmationFrameClicked=false;
            
        } 
        //button was "Quiz"
        else if (SimStPLE.QUIZ.equals(actionCommand)) {
        	
           	this.getMissController().getSimStPLE().resetRestartClickCount();
           	if (brController.getMissController().getSimSt().isSsMetaTutorMode()){
           		this.brController.getMissController().getSimSt().getModelTraceWM().setSolutionCheckError("false");
           		brController.getMissController().getSimSt().getModelTraceWM().setRestartCount(0);
           		brController.getMissController().getSimSt().getModelTraceWM().setNextSelection("nil");
           		brController.getMissController().getSimSt().getModelTraceWM().setNextAction("nil");
           		brController.getMissController().getSimSt().getModelTraceWM().setNextInput("nil");
           		brController.getMissController().getSimSt().getModelTraceWM().setSolutionGiven("false");
           		brController.getMissController().getSimSt().getModelTraceWM().setTutoredProblemsWithoutHint(0);      	
           		brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(null);
        	}
        	
        	
        	logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.QUIZ_BUTTON_ACTION, "");
        	
        	takeQuiz();
        } 
        //button was "Curriculum Browser" - Deprecated
        else if (SimStPLE.CURRICULUM_BROWSER.equals(actionCommand)) {
        	logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.CURRICULUM_BROWSER_BUTTON_ACTION, "");
            getMissController().pleCurriculumBrowser();
        }
        //button was "Show Examples" - Deprecated
        else if (SimStPLE.EXAMPLES.equals(actionCommand)) {
        	
        	logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLES_BUTTON_ACTION, "");
            getMissController().pleExamplesSimSt();
        } 
        //button was "Erase Last Step"
        else if (SimStPLE.UNDO.equals(actionCommand)) {
        	
        	logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.UNDO_BUTTON_ACTION, "");
            //undo the last step
        	getMissController().pleUndoSimSt();
        }
        //button was "Restart Problem"
        else if (SimStPLE.RESTART.equals(actionCommand)) {

        	brController.getMissController().getSimSt().newProblemButtonLockFlag=false;
        	brController.getMissController().getSimSt().scheduleNewProblemTimer();
        	brController.getMissController().getSimStPLE().setIsRestartClicked(true);
       
        	getMissController().getSimStPLE().incRestartClickCount();
        	
        	if (brController.getMissController().getSimSt().isSsMetaTutorMode()){
        		     		
        		brController.getMissController().getSimSt().getModelTraceWM().setNextSelection("nil");
        		brController.getMissController().getSimSt().getModelTraceWM().setNextAction("nil");
        		brController.getMissController().getSimSt().getModelTraceWM().setNextInput("nil");
        		brController.getMissController().getSimSt().getModelTraceWM().setSolutionGiven("false");
        		
        		/*update the restart count in working memory*/
        		int count=this.getMissController().getSimStPLE().getRestartClickCount();		
        		brController.getMissController().getSimSt().getModelTraceWM().setRestartCount(count); 
        		brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(this.getMissController().getSimStPLE().getSsInteractiveLearning().getPreviousTutoredProblem());			

        	}
        	
        	
        	if (brController.getMissController().getSimSt().isSsMetaTutorMode())
        		this.brController.getMissController().getSimSt().getModelTraceWM().setSolutionCheckError("false");

        	String problem=null;
        	if (brController.getMissController().getSimSt().isSsMetaTutorMode())
        		this.brController.getMissController().getSimSt().getModelTraceWM().getStudentEnteredProblem();
        	
        	
        	if (brController.getMissController().getSimSt().isSsCogTutorMode()){
    			getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("hint-request");
    		}
        	
        	
        	logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.RESTART_BUTTON_ACTION, "");
        	
        	if (brController.getMissController().getSimSt().isSsCogTutorMode() && brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()){
	
        		getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().initQuizSolutionHash();    
        		getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().initFailedQuizSolutionHash();  
        		getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().enterFirstUnsolvedQuizProblemToInterface(true);
        		getBrController().getMissController().getSimStPLE().unBlockQuiz(true);
        		
        		return;
        	}
        	
        	
        	
        	//restart the problem
        	if (brController.getMissController().getSimSt().isSsCogTutorMode()){
        		
        		getBrController().getMissController().getSimStPLE().nextProblem(false);
        		
        		if (!brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
        			getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().giveNextProblem(false);
        		}
        		else if (getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()){
        			getBrController().getMissController().getSimStPLE().unBlockQuiz(true);
        		}
        		else{
        			getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().giveProblem(getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().getLastGivenProblem());
        		}        		
        	}
        	else 
        		getMissController().pleRestartProblemSimSt();
        	
        	
        	
        }
        //button was "Configure Avatar" - Deprecated
        else if (SimStPLE.CFG.equals(actionCommand)) {
        	
        	logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.AVATAR_CONFIGURE_BUTTON_ACTION, "");
            StudentAvatarDesigner.createAndShowGUI(getMissController().getSimStPLE().getSimStPeerTutoringPlatform(),getMissController().getSimSt().getUserID());
        }  
        //Do not know what the button was
        else {
            new Exception("Invalid action event: " + event).printStackTrace();
        }
    }
    
    
    
    //when a tab is selected
	@Override
	public void stateChanged(ChangeEvent event) {
		
		if(event.getSource() instanceof JTabbedPane)
		{
			
			//if (this.getMissController().getSimStPLE().getStatus().equals(SimStPLE.THINK_STATUS)){
			//	return;
			//}
				
			  JTabbedPane tabPane = (JTabbedPane) event.getSource();
		
        	//System.out.println(" Tab Clicked , is Model Tracer enabled : "+brController.getMissController().getSimStPLE().isModelTracer());

			//If there wasn't a previously viewed tab, say it was the first tab
			if(lastTabViewed.length() == 0)
			{
				lastTabViewed = "Tutor";
			}
			
			 if(lastTabViewed.contains(AplusPlatform.overviewTabTitle) && brController.getMissController().getSimStPLE().isModelTracer() && getBrController().getMissController().getSimSt().isSsMetaTutorMode()) {
		       brController.getMissController().getSimSt().getModelTraceWM().setOverviewScrolled(false);
			 }
			 
			 if(lastTabViewed.contains(AplusPlatform.exampleTabTitle) && brController.getMissController().getSimStPLE().isModelTracer()) {
				 brController.getMissController().getSimSt().getModelTraceWM().setExampleProblemViewed(false);
			 }
			 
			// System.out.println(" Tab Start time : "+lastTabViewStart);
			int duration = (int) ((Calendar.getInstance().getTimeInMillis() - lastTabViewStart)/1000);
	        lastTabViewStart = Calendar.getInstance().getTimeInMillis(); 


	        if(brController.getMissController() != null && brController.getMissController().getSimSt() != null
	        		&& brController.getMissController().getSimSt().isSsMetaTutorMode()) {

	        	if(brController.getAmt() != null && brController.getMissController().getSimSt().isSsMetaTutorMode() && brController.getMissController().getSimStPLE().isModelTracer()) {
	      
	        		String tabText = tabPane.getTitleAt(tabPane.getSelectedIndex()).replaceAll("\\s+", "");
	        		//System.out.println("Calling Model Tracer");
        			brController.getAmt().handleInterfaceAction(tabText, "TabClicked", "-1");
	        	}
	        }
	        
	        if(brController.getMissController().getSimStPLE().isModelTracer())
	        logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.TAB_LEFT_ACTION, "",lastTabViewed,"",duration);

	        if (tabPane.getTitleAt(tabPane.getSelectedIndex()).contains(AplusPlatform.exampleTabTitle) && brController.getMissController().getSimStPLE().isModelTracer()){
	        	brController.getMissController().getSimSt().getModelTraceWM().setExamplesTabClicked("True");
	    		brController.getMissController().getSimSt().getModelTraceWM().setResourceViewed("True");
      			brController.getMissController().getSimSt().getModelTraceWM().setQuizFailCount(0); 
	        }
	        
	        if (tabPane.getTitleAt(tabPane.getSelectedIndex()).contains(AplusPlatform.overviewTabTitle) && brController.getMissController().getSimStPLE().isModelTracer()){
	        	brController.getMissController().getSimSt().getModelTraceWM().setUOTabClicked("True");
	        	brController.getMissController().getSimSt().getModelTraceWM().setResourceViewed("True");
      			brController.getMissController().getSimSt().getModelTraceWM().setQuizFailCount(0); 
      		}
	        
	        if (tabPane.getTitleAt(tabPane.getSelectedIndex()).contains(AplusPlatform.bankTabTitle) && brController.getMissController().getSimStPLE().isModelTracer()){
	        	brController.getMissController().getSimSt().getModelTraceWM().setProblemBankTabClicked("True");
	        }
	
 
	        if(tabPane.getTitleAt(tabPane.getSelectedIndex()).contains(AplusPlatform.bankTabTitle) || tabPane.getTitleAt(tabPane.getSelectedIndex()).contains(AplusPlatform.videoTabTitle) && brController.getMissController().getSimStPLE().isModelTracer())
	        	brController.getMissController().getSimSt().getModelTraceWM().setConsecutiveResourceReview(brController.getMissController().getSimSt().getModelTraceWM().getConsecutiveResourceReview()+1);;
 
	        
	        	 if (trace.getDebugCode("mt")) trace.out("mt", " Tab " + tabPane.getTitleAt(tabPane.getSelectedIndex()) +" is clicked"); 
	         

	        	
			if(lastTabViewed.contains(AplusPlatform.exampleTabTitle) && brController.getMissController().getSimStPLE().isModelTracer())
			{
				//if(exampleTitle.length() > 0)
		    	//{
		    		long exampleDuration = (Calendar.getInstance().getTimeInMillis() - getExampleStartTime())/1000;
		    		//logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_VIEW_END, 
		    			//	"", exampleTitle+"TabSwiched", "", (int) exampleDuration);
		    		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_VIEW_END, 
		    				"","TabSwitched","", (int) exampleDuration);
		    	//}
			}
			if(lastTabViewed.equals(AplusPlatform.quizTabTitle) && brController.getMissController().getSimStPLE().isModelTracer())
			{
				if(quizTitle.length() > 0)
		    	{
					
					//AplusPlatform aplus=(AplusPlatform) this.getMissController().getSimStPLE().getSimStPeerTutoringPlatform();
					//aplus.setQuizButtonImg(aplus.getQuizButtonImage());
					//aplus.refresh();

					
					
		    		long quizDuration = (Calendar.getInstance().getTimeInMillis() - quizStartTime)/1000;
		    		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.QUIZ_VIEW_END_TAB, 
		    				"", quizTitle, "", (int) quizDuration);
		    	}
			}
	        
			
			
			
			
			
			
	        //and going to the new tab
			
			String title = "Tutor";
			if(tabPane.getSelectedIndex() > 0)
				title = tabPane.getTitleAt(tabPane.getSelectedIndex());
			if(brController.getMissController().getSimStPLE().isModelTracer())
			logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.TAB_SWITCH_ACTION, title);
			if(trace.getDebugCode("miss"))trace.out("miss", "In stateChanged and calling setUpTab");
			
			
			if (getBrController().getMissController().getSimSt().isSsCogTutorMode() && brController.getMissController().getSimStPLE().isModelTracer()){
			      getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("hint-request");
			      getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().setQuizSolving(false);
			      
			}
			
			
			/*if in aplus control, then when we click on quiz tab give the first unsolved solved problem. */
			if (getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && tabPane.getTitleAt(tabPane.getSelectedIndex()).equals(AplusPlatform.quizTabTitle) && brController.getMissController().getSimStPLE().isModelTracer()){
				
				/*first tell the model tracer that we are no longer in tutoring mode */
				if (brController.getMissController().getSimSt().isSsMetaTutorMode() && brController.getMissController().getSimStPLE().isModelTracer()){
	          		brController.getMissController().getSimSt().getModelTraceWM().setSolutionCheckError("false");
	          		brController.getMissController().getSimSt().getModelTraceWM().setNextSelection("nil");
	          		brController.getMissController().getSimSt().getModelTraceWM().setNextAction("nil");
	          		brController.getMissController().getSimSt().getModelTraceWM().setNextInput("nil");
	          		brController.getMissController().getSimSt().getModelTraceWM().setSolutionGiven("false");
	          		brController.getMissController().getSimSt().getModelTraceWM().setRestartCount(0);
	          		brController.getAmt().pendingCount.clear();   	
	          		brController.getMissController().getSimSt().getModelTraceWM().setSimStudentThinking(WorkingMemoryConstants.FALSE);
	        		brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(null);
	          	}
				/*tell cogTutor that its ok to expect student to give problem next time we come back on tutoring tab*/
				getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering=false;
				System.out.println(" Clearing the interface in the Aplus Control  : "+tabPane.getTitleAt(tabPane.getSelectedIndex()));
				/*clear tutoring interface so next time we come back its ready*/
				getMissController().getSimStPLE().requestEnterNewProblem();
				
				
				getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().enterFirstUnsolvedQuizProblemToInterface(false);
				getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().setQuizSolving(true);
				
			}
			
			/*This is very important: when a student types a solution on the quiz, all the SAI's get appended on the start state message. As a result, when trying (after
			 * solving a quiz problem) to enter a new problem then the quiz solution apperas. With this we make sure that start state model is restarted */
			if (getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && tabPane.getTitleAt(tabPane.getSelectedIndex()).equals("Practice") && brController.getMissController().getSimStPLE().isModelTracer() ){
				this.getBrController().getUniversalToolProxy().resetStartStateModel();	
				//((AplusPlatform) this.getMissController().getSimStPLE().getSimStPeerTutoringPlatform()).nextProblemButton.doClick();
	
				if (brController.getMissController().getSimSt().isSsMetaTutorMode()){
	          		brController.getMissController().getSimSt().getModelTraceWM().setSolutionCheckError("false");
	          		brController.getMissController().getSimSt().getModelTraceWM().setNextSelection("nil");
	          		brController.getMissController().getSimSt().getModelTraceWM().setNextAction("nil");
	          		brController.getMissController().getSimSt().getModelTraceWM().setNextInput("nil");
	          		brController.getMissController().getSimSt().getModelTraceWM().setSolutionGiven("false");
	          		brController.getMissController().getSimSt().getModelTraceWM().setRestartCount(0);
	          		brController.getAmt().pendingCount.clear();   	
	          		brController.getMissController().getSimSt().getModelTraceWM().setSimStudentThinking(WorkingMemoryConstants.FALSE);
	        		brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(null);
	          	}
	        		getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering=false;
					AplusPlatform aplus=((AplusPlatform) getMissController().getSimStPLE().getSimStPeerTutoringPlatform());
	                aplus.setRestartButtonEnabled(false);
	     
				/*prepare for next problem*/
	            getMissController().pleNextProblem();

					this.getBrController().getUniversalToolProxy().resetStartStateModel();	
					/*reset the flags for the start state */
					getBrController().getMissController().getSimStPLE().setIsStartStateCompleted(false);
	 				getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering=false;

					this.getBrController().getMissController().getSimStPLE().unblockAllButStartState(false);
				
				
					getMissController().getSimStPLE().getSsCognitiveTutor().clearStartStateFromStudentInterface();
				
					getMissController().getSimStPLE().setFocusOfStartStateElementsStudentInterface(true);


			}
			
			
			
			//And display the new tab
			getMissController().getSimStPLE().setUpTab(tabPane.getTitleAt(tabPane.getSelectedIndex()));
            
			
			//track the tab you are looking at for next time, as the one you left
			if(tabPane.getSelectedIndex() > 0)
				lastTabViewed = tabPane.getTitleAt(tabPane.getSelectedIndex());
			else
				lastTabViewed = "Tutor";
            //if switched to example tab and an example was open, now looking at it
            if(lastTabViewed.equals(AplusPlatform.exampleTabTitle) && brController.getMissController().getSimStPLE().isModelTracer())
			{
				if(exampleTitle.length() > 0)
		    	{
			    	exampleStartTime = Calendar.getInstance().getTimeInMillis();
					logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_VIEW_TAB, exampleTitle);
		    	}
			}
            if(lastTabViewed.equals(AplusPlatform.quizTabTitle) && brController.getMissController().getSimStPLE().isModelTracer())
			{
				if(quizTitle.length() > 0)
		    	{
			    	quizStartTime = Calendar.getInstance().getTimeInMillis();
					logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.QUIZ_VIEW_TAB, quizTitle);
		    	}
			}
		}
		if(event.getSource() instanceof JSlider)
		{
			JSlider source = (JSlider) event.getSource();
			//Don't log it if it's not done adjusting yet.
			if(source.getValueIsAdjusting())
				return;
			String skill = ((JLabel) source.getLabelTable().get(0)).getText();
			int newValue = source.getValue();
			Sai sai = new Sai(skill, "Skillometer Updated", ""+newValue);
			int change = newValue - previousSliderValue;
    		SimSt simSt = getMissController().getSimSt();
    		
    		// Save the current value for the slider
    		if(simSt != null) {
    			if(simSt.getSkillSliderNameValuePair() == null)
    				simSt.setSkillSliderNameValuePair(new Hashtable<String, Integer>());

    			Hashtable<String, Integer> skillSlider =  simSt.getSkillSliderNameValuePair();
    			skillSlider.put(skill, newValue);
    		}
    		
			logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.SKILLOMETER_UPDATE_ACTION,
					simSt.getProblemStepString(), ""+change, "", sai);
			previousSliderValue = newValue;
		}
	}
	
	public void exampleSwitched(String newExampleTitle)
	{
		/*if(exampleTitle.length() > 0)
    	{
    		long exampleDuration = (Calendar.getInstance().getTimeInMillis() - exampleStartTime);
    		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_VIEW_END, 
    				"", exampleTitle, "", (int) exampleDuration);
    	}*/
		
    	exampleTitle = newExampleTitle;
    	setExampleStartTime(Calendar.getInstance().getTimeInMillis());
		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_VIEW, newExampleTitle);
	}
	
	public void quizSwitched(String newQuizTitle)
	{
		if(quizTitle.length() > 0)
    	{
			//System.out.println("Quiz Start time : "+quizStartTime);
    		long quizDuration = (Calendar.getInstance().getTimeInMillis() - quizStartTime)/1000;
    		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.QUIZ_VIEW_END, 
    				"", quizTitle, "", (int) quizDuration);
    	}
    	quizTitle = newQuizTitle;
    	quizStartTime = Calendar.getInstance().getTimeInMillis();
    	//System.out.println(" Setting the Start for quiz : "+quizStartTime);

		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.QUIZ_VIEW, newQuizTitle);
	}
	
	public void takeQuiz()
	{
    	//New quiz, start these over
    	quizTitle = "";
    	quizStartTime = 0;
    	//System.out.println(" The start time for quiz : "+quizStartTime);
    	//the current problem is not complete
    	if(brController.getCurrentNode()!= null && !brController.getCurrentNode().isDoneState())
    	{
    		SimSt simSt = getMissController().getSimSt();
        	int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis()/1000 - simSt.getSsInteractiveLearning().getProblemStartTime()/1000));
        	logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_LEFT_QUIZ_ACTION, simSt.getProblemStepString(),
        			"","",problemDuration);
    	}
    	//the current problem is complete
    	else if(brController.getCurrentNode()!= null && brController.getCurrentNode().isDoneState())
    	{
    		SimSt simSt = getMissController().getSimSt();
        	int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis()/1000 - simSt.getSsInteractiveLearning().getProblemStartTime()/1000));
        	logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_COMPLETED_ACTION, simSt.getProblemStepString(),
        			"","",problemDuration);
    	}
    	
    	//give the quiz
        getMissController().pleQuizSimSt();
    	
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Getters & Setters
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public BR_Controller getBrController() {
        return brController;
    }

    public void setBrController(BR_Controller brController) {
        this.brController = brController;
    }

    private MissController getMissController() {
        return ((MissController) getBrController().getMissController());
    }
    
}
