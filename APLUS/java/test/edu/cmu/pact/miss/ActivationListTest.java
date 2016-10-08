package edu.cmu.pact.miss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import javax.swing.SwingUtilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.EquationTutor.EquationTutor;
import edu.cmu.pact.miss.console.controller.MissController;


public class ActivationListTest extends TestCase {
    
    public static Test suite() {
        return new TestSuite(ActivationListTest.class);
    }
    
    public void testDummy() {  // FIXME: restore tests to active set?
    	assertTrue("dummy method to avoid JUnit warning", true);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ActivationListTest.suite());
    }
    
    public final void bestActivationList(){
        initialize();
        asserts();
//        finalize();
    }
    
    public void tearDown() {
    	brController = null;
    	missController = null;
    	simSt = null;
    	brdPath = null;
    	javaDir = null;
    	projectDir = null;
    	prPath = null;
    }

    /*protected void tearDown() {
    	try {
			super.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	long tearDownTime = System.currentTimeMillis();
		try {
			Thread.sleep(100);			
		} catch (InterruptedException ie) {
			System.err.printf("%s.tearDown slept %d ms: %s\n",
					getClass().getSimpleName(),
					System.currentTimeMillis()-tearDownTime, ie.toString());
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (brController.getActiveWindow() != null)
					brController.getActiveWindow().dispose();				
			}
		});
    }*/

    BR_Controller brController;
    MissController missController; 
    private SimSt simSt;

    String javaDir = null;
    String brdPath;
    String prPath;
    String projectDir;
    
    //copies the PR file
    //starts CTAT with the BRD file
    public void initialize() {
        //////////////////////////
        //setting variables
        //////////////////////////
        try{
            javaDir = new File (".").getCanonicalPath();
            javaDir = javaDir.replace('\\', '/');
        }
        catch(Exception e){
            e.printStackTrace();
        }
        projectDir = javaDir + "/test/edu/cmu/pact/miss/EquationTutor";
        brdPath = projectDir + "/ActivationList.brd";
//        brdPath = projectDir + "/onenode.brd";
//        brdPath = projectDir + "/onenode2.brd";

        //////////////////////////        
        //delete existing PR file  
        //////////////////////////
        prPath = projectDir + "/productionRules.pr";
//        new File(prPath).delete();


        
        //////////////////////////
        //INITIALIZING CTAT
        //////////////////////////
        SingleSessionLauncher ctatLauncher = initializeCtat(javaDir, projectDir, brdPath);
        ctatLauncher.launch(new EquationTutor());

        System.out.println("copying " + projectDir+"/productionRules.pr-ActivationListTest to" + prPath);

        //the PR file SHOULD BE copied right after the dialog fails to appear
        copyFile(projectDir+"/productionRules.pr-ActivationListTest", prPath);
//        System.in.read();

        brController = ctatLauncher.getController();
        brController.initializeSimSt();
        missController = (MissController) brController.getMissController();
        simSt = missController.getSimSt();

        SimSt.setProjectDir(projectDir);
        
        
//        simSt.clearJessConsole();
//        simSt.resetMT();
//        Communicator.reset();
        
        //do not destroy PR!
        brController.setClArgumentSetToProtectProdRules(true);
        brController.setBehaviorRecorderMode(CtatModeModel.SIMULATED_STUDENT_MODE);
        
//        brController.setModeSimStAndDestroyProdRules();
        
     
        LoadFileDialog.doLoadBRDFile( brController.getServer(), brdPath, "", true);
        
    }
    
    public void copyFile(File inFile, File outFile) throws Exception {
//        System.out.println("entered copyFile(File inFile, File outFile)");
        FileInputStream fis  = new FileInputStream(inFile);
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buffer = new byte[1024];
        int bufferLength;
        while((bufferLength=fis.read(buffer))!=-1) {
//            System.out.println("writing: " + buffer);

            fos.write(buffer, 0, bufferLength);
        }
        fis.close();
        fos.close();
    }

    private void copyFile(String filename1, String filename2) {
        try{
        copyFile(new File(filename1), new File(filename2));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

//    *** runValidation.sh ***
//        TutorArg="-ssRunValidation"
//        TutorArg="${TutorArg} -traceLevel '-1'"
//        TutorArg="${TutorArg} -debugCodes miss allrulefirings"
//        TutorArg="${TutorArg} -ssProjectDir ${SIMSTDIR}"
//        TutorArg="${TutorArg} -ssFOILBASE ${FOILBASE}"
//        TutorArg="${TutorArg} -ssCondition Smoke"
//        TutorArg="${TutorArg} -ssTestSet $TESTS"
//        TutorArg="${TutorArg} -ssFoilLogDir ${FOILDIR} -ssPrAgeDir ${PRDIR}"
//        TutorArg="${TutorArg} -ssTestOutput $TESTLOG"
//        TutorArg="${TutorArg} -ssTestRuleFiringLogged"
//        TutorArg="${TutorArg} -ssRuleActivationResultCheckingMethod humanOracle"

//  java -cp C:/CVS-TREE/AuthoringTools/java/lib/CommWidgets.jar;
//    C:/CVS-TREE/Tutors/SimSt/SimStEquation/..;C:/CVS-TREE/Tutors/SimSt/SimStEquation
//    -Xmx512m SimStEquation.EquationTutor -ssRunValidation -traceLevel '-1' 
//    -debugCodes miss allrulefirings
//    -ssProjectDir C:/CVS-TREE/Tutors/SimSt/SimStEquation
//    -ssFOILBASE C:/CVS-TREE/AuthoringTools/java/FOIL6
//    -ssCondition Smoke
//    -ssTestSet C:/CVS-TREE/Tutors/SimSt/SimStEquation/Test-noIsomorphic/3x+2x_21-6.brd
//    -ssFoilLogDir SmokeTestLog/foil-data-000
//    -ssPrAgeDir SmokeTestLog/pr-age-000
//    -ssTestOutput SmokeTestLog/validation-log.txt
//    -ssTestRuleFiringLogged
//    -ssRuleActivationResultCheckingMethod humanOracle    
    
    //initialize CTAT to do a validation test
    public SingleSessionLauncher initializeCtat(String javaDir, String projectDir, String brdPath) {

        String tutorArg="";
//        tutorArg+="-debugCodes miss mt";
        tutorArg+="-ssProjectDir " + projectDir;
        tutorArg+=" -ssFoilBase " + javaDir + "/FOIL6";
        //tutorArg+=" -ssTestSet " + brdPath;
        tutorArg+=" -ssTestRuleFiringLogged";
        tutorArg+=" -ssRuleActivationTestMethod humanOracle";//-ssRuleActivationResultCheckingMethod humanOracle";

        System.out.println("tutorArg = " + tutorArg );
        
        String[] argv = tutorArg.split(" ");

        return new SingleSessionLauncher(argv);    
    }


    private static final int NUMBER_ATTRIBUTES = 4;

    
    //for each step in the BRD, assert that each RuleActivationNode in the agenda
    //has the correct actualSelection, actualAction, actualInput.
    public void asserts() {

        ProblemNode startNode = brController.getProblemModel().getStartNode();
        
        
        System.out.println("startNode = " + startNode);
        System.out.println("brController.getCurrentNode() = " + brController.getCurrentNode());

        //simSt.switchToSimStMode();
//        brController.getCtatModeModel().setAuthorMode(CtatModeModel.TESTING_TUTOR);
        
//        ProblemNode doneNode = startNode.getDeadEnd();
//        System.out.println("doneNode = " + doneNode);
//        brController.setCurrentNode2( doneNode );
//        
//        String doneStr = doneNode.getName();
//        MessageObject response = brController.getGoToWMStateResponse(doneStr);  
//        CheckLinksList clList = CheckLinksList.getCheckedLinksList(response);
//        System.out.println("clList = " + clList);
        
        //@Rohan: If the activationList has duplicate activations the duplicates are removed before sending it back
        // to the caller. The testSet is modified to reflect the activation list without duplicates. 
        // See SimSt.removeDuplicateActivations()
        String[][] testSet = { {"MAIN::do-arith-lhs", "commTable2_C1R2", "UpdateTable", "5x"},
         {"MAIN::copy-rhs-1", "commTable1_C1R2", "UpdateTable", "21-6", "MAIN::do-arith-rhs", "commTable1_C1R2", "UpdateTable", "15"},
         {"MAIN::div-lhs", "commTable2_C1R3", "UpdateTable","5x/5"},
         {"MAIN::multi-rhs-1", "commTable1_C1R3", "UpdateTable", "15*FALSE", "MAIN::div-rhs", "commTable1_C1R3", "UpdateTable","15/5"},
         {"MAIN::do-arith-lhs", "commTable2_C1R4", "UpdateTable", "x"},
         {"MAIN::do-arith-rhs-1", "commTable1_C1R4", "UpdateTable", "3"},
         {"MAIN::done-1", "done", "ButtonPressed", "-1"}    
        };

        /* Original version of testSet before duplicate activations were removed.
        String[][] testSet = { {"MAIN::do-arith-lhs", "commTable2_C1R2", "UpdateTable", "5x", "MAIN::do-arith-lhs-1", "commTable2_C1R2", "UpdateTable", "5x"},
                {"MAIN::copy-rhs-1", "commTable1_C1R2", "UpdateTable", "21-6", "MAIN::do-arith-rhs", "commTable1_C1R2", "UpdateTable", "15", "MAIN::do-arith-rhs-1", "commTable1_C1R2", "UpdateTable", "15", "MAIN::copy-rhs", "commTable1_C1R2", "UpdateTable", "21-6"},
                {"MAIN::div-lhs", "commTable2_C1R3", "UpdateTable","5x/5","MAIN::div-lhs-1", "commTable2_C1R3", "UpdateTable","5x/5"},
                {"MAIN::multi-rhs-1", "commTable1_C1R3", "UpdateTable", "15*FALSE", "MAIN::div-rhs", "commTable1_C1R3", "UpdateTable","15/5","MAIN::div-rhs-1", "commTable1_C1R3", "UpdateTable","15/5","MAIN::multi-rhs", "commTable1_C1R3", "UpdateTable", "15*FALSE"},
                {"MAIN::do-arith-lhs", "commTable2_C1R4", "UpdateTable", "x", "MAIN::do-arith-lhs-1", "commTable2_C1R4", "UpdateTable", "x"},
                {"MAIN::do-arith-rhs-1", "commTable1_C1R4", "UpdateTable", "3", "MAIN::do-arith-rhs", "commTable1_C1R4", "UpdateTable", "3"},
                {"MAIN::done-1", "done", "ButtonPressed", "-1", "MAIN::done", "done", "ButtonPressed", "-1"},    
               };
        */
        
        ProblemNode currentNode = startNode;

        
        System.out.println("before for");
        for (int i=0; i<testSet.length; i++){
            //System.out.println("currentNode = " + currentNode);
            Vector activationList = simSt.gatherActivationList(currentNode);
            String[] state = testSet[i];
            int nRules = state.length/NUMBER_ATTRIBUTES; //the length will always be a multiple of NUMBER_ATTRIBUTES

            
            String rulesExpected = "";
            for (int ind=0;ind<nRules; ind++){
                rulesExpected += " "+state[NUMBER_ATTRIBUTES*ind];
            }
            System.out.println("expecting " + nRules + " rules, namely: " + rulesExpected);            
            
            String rulesInActivationList = "";
            for (int ind=0;ind<activationList.size(); ind++){
                rulesInActivationList += " "+activationList.get(ind);
            }
            System.out.println("got " + activationList.size()+ " rules, namely: " + rulesInActivationList);
            

            assertEquals(nRules, activationList.size());
            System.out.println("currentNode = " + currentNode);
            for (int j=0; j<nRules; j++){
                RuleActivationNode ran = (RuleActivationNode) activationList.get(j);
                System.out.println(j+ "th rule for "+ currentNode + ":");
                System.out.println("ran.getName() = " + ran.getName());                
                System.out.println("ran.getActualSelection() = " + ran.getActualSelection());                
                System.out.println("ran.getActualAction() = " + ran.getActualAction());
                System.out.println("ran.getActualInput() = " + ran.getActualInput());    
                
                assertEquals(state[NUMBER_ATTRIBUTES*j], ran.getName());
                assertEquals(state[NUMBER_ATTRIBUTES*j+1], ran.getActualSelection());                
                assertEquals(state[NUMBER_ATTRIBUTES*j+2], ran.getActualAction());                
                assertEquals(state[NUMBER_ATTRIBUTES*j+3], ran.getActualInput());                
                System.out.println(j+ "th rule for "+ currentNode + " passed!");

            }                
            currentNode = (ProblemNode) currentNode.getChildren().get(0); //first child
        }   
    }

    
    //deletes the PR file
    public void finalize() {
        new File(prPath).delete();
    }
        
}
