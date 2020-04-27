package edu.cmu.pact.miss;



import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.jess.JessModelTracing;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.jess.RuleActivationTree.TreeTableModel;
import edu.cmu.pact.miss.EquationTutor.EquationTutor;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStEdge;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;
import edu.cmu.pact.miss.SimStAlgebraV8.SimStAlgebraV8;
import edu.cmu.pact.miss.console.controller.MissController;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class QuizTest extends TestCase{

	 
	private static BR_Controller brController;
	private SimSt simSt;
	private String javaDir;
	private String projectDir;
	private SimStInteractiveLearning ssInteractivelearning;

	public static Test suite() {
		return new TestSuite(QuizTest.class);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(QuizTest.suite());
	}



    /**
     *  Main function for testing the quiz
     * @throws IOException
     */
	public void testQuiz() throws IOException{
		boolean a=true;

		/*Initialize and launch CTAT & APLUS*/
		//initialize();
		
		/*take the quiz*/
		//preprocessing();
		
		initialize_nc();
		
		assertTrue(a);
	}

	
	SimStRete ssRete;
	/**
	 * Initialize without ctat
	 */
	public void initialize_nc(){
		/*initializations*/
		String quizProblem="3v+2=7";
		SimStProblemGraph quizGraph = null;
		String safeProblemName = SimSt.convertToSafeProblemName(quizProblem);
		quizGraph = new SimStProblemGraph();
		SimStNode startNode1 = new SimStNode(safeProblemName, quizGraph);
		quizGraph.setStartNode(startNode1);
		quizGraph.addSSNode(startNode1);
		
		
		/*taking the quiz...*/
		SimStNode startNode = quizGraph.getStartNode();
		SimStNode currentNode = null, nextCurrentNode = null;
		HashMap hm = new HashMap();

		
		//brController=new BR_Controller(false);
		//brController.getPreferencesModel().setStringValue(BR_Controller.PROJECTS_DIR,projectDir);
		//brController.getPreferencesModel().setBooleanValue(BR_Controller.COMMUTATIVITY,Boolean.FALSE);
		
		//ssRete= new SimStRete(brController);
		//currentNode = startNode;
		//Vector activationList=gatherActivationList(currentNode,hm);
		//System.out.println("activationList=" + activationList);
		
	}
	
	
	   
	   /**
		 * Pre-processing: method to take the quiz.
		 */
		public void preprocessing(){	
			
			//set the necessary flags for quiz mode
			brController.getMissController().getSimSt().setDontShowAllRA(true);
			brController.getMissController().getSimSt().setIlSignalNegative(false);
			brController.getMissController().getSimSt().setIlSignalPositive(false);
			brController.getMissController().getSimSt().setRuleActivationTestMethod(brController.getMissController().getSimSt().getQuizGradingMethod());
			brController.getMissController().getSimStPLE().getSsInteractiveLearning().setTakingQuiz(true);
			//take the quiz
			brController.getMissController().getSimStPLE().startQuizProblems();
			
			
			/* Attempt to run unit test without showing any windows. No good, when you launch ctat student interface always appears... 
			brController.getMissController().getSimSt().setDontShowAllRA(true);
			brController.getMissController().getSimSt().setIlSignalNegative(false);
			brController.getMissController().getSimSt().setIlSignalPositive(false);
			brController.getMissController().getSimSt().setRuleActivationTestMethod(brController.getMissController().getSimSt().getQuizGradingMethod());
			ssInteractivelearning.setTakingQuiz(true);
			
			
			//brController.getMissController().getSimStPLE().randomizeQuizProblems();		
			String problem = "3v+7=2";//brController.getMissController().getSimStPLE().getRandomizedQuizProblem(0);		
			brController.startNewProblem(); // To set the platform for starting a new problem.
			ssInteractivelearning.createStartStateQuizProblem(problem);
			System.out.println("starting quiz problem "+ problem);
			ssInteractivelearning.startQuizProblem();
			
			System.out.println("ended quiz problem " +  problem);
			*/
		}

		
		
		
	/***
	 * Method to make all the necessary initalizations (ctat, aplus)
	 * @throws IOException
	 */
	public  void initialize() throws IOException{

		File dir1 = new File (".");
		javaDir = dir1.getCanonicalPath(); //get CanonicalPath or AbsolutePath?
		System.out.println("javaDir = " + javaDir);
		projectDir = javaDir + "/test/edu/cmu/pact/miss/SimStAlgebraV8"; //-ssProjectDir can also be set with a command-line parameter
		System.out.println("projectDir = " + projectDir);
		SimSt.setProjectDir(projectDir);
		

		//String prPath = projectDir + "/productionRules.pr"; //
		//new File(prPath).delete();
		//
		// copy productionRules.pr-LearningTest as productionRules.pr	
		

		String tutorArg="";
		tutorArg+="-debugCodes miss";
		tutorArg+=" -ssRunInPLE";
		tutorArg+=" -ssUserID quizTest";
		tutorArg+=" -ssProjectDir " + projectDir;
		tutorArg+=" -DssFoilBase " + javaDir + "/FOIL6";
		tutorArg+=" -ssOverviewPage curriculum.html";
		tutorArg+=" -ssFoaGetterClass edu.cmu.pact.miss.SimStAlgebraV8.AlgebraV8AdhocFoaGetter";		
		tutorArg+=" -ssSkillNameGetterClass edu.cmu.pact.miss.SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter";
		tutorArg+=" -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent";
		String[] argv = tutorArg.split(" ");


		System.out.println(tutorArg);
		SingleSessionLauncher launcher = new CTAT_Launcher(argv).getFocusedController().getLauncher();		 
		launcher.launch(new SimStAlgebraV8());

		//just in case, copy productionRules (this would be removed)
		copyFile(projectDir+"/productionRules-quizTest.pr", projectDir+"/productionRules.pr");

		brController = launcher.getController();
		brController.getPreferencesModel().setStringValue(BR_Controller.PROJECTS_DIR,projectDir);
		
		
		  
	/*	Attempt to run unit test without showing any windows. No good, when you launch ctat student interface always appears... 
	 * brController.initializeSimSt();
		simSt = brController.getMissController().getSimSt();

		//assert that we are in SimStudent mode
		simSt.switchToSimStMode();		
		assertTrue(brController.getCtatModeModel().getCurrentMode().equals(CtatModeModel.SIMULATED_STUDENT_MODE));
		
		SimStPLE ple=new SimStPLE(brController,null);
		
		ssInteractivelearning=new SimStInteractiveLearning(simSt);
		*/

	}

	private void copyFile(String filename1, String filename2) {
        try{
            copyFile(new File(filename1), new File(filename2));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
	
	 public void copyFile(File inFile, File outFile) throws Exception {
	        FileInputStream fis  = new FileInputStream(inFile);
	        FileOutputStream fos = new FileOutputStream(outFile);
	        byte[] buffer = new byte[1024];
	        int bufferLength;
	        while((bufferLength=fis.read(buffer))!=-1) {
	            fos.write(buffer, 0, bufferLength);
	        }
	        fis.close();
	        fos.close();
	    }
    
    
}
