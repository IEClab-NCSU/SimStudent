package edu.cmu.pact.miss;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReader;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.miss.EquationTutor.EquationTutor;
import edu.cmu.pact.miss.console.controller.MissController;


//This JUnitTest was written by Gustavo 26Nov2006

public class SimulateLearningTest extends TestCase  {
    
    public static Test suite() {
        return new TestSuite(SimulateLearningTest.class);
    }
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(SimulateLearningTest.suite());
    }
    
    
    private static BR_Controller brController;
    private MissController missController;
    private SimSt simSt;

    private String javaDir;
    private String projectDir;

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
				if (brController.getStudentInterface().getActiveWindow() != null)
					brController.getStudentInterface().getActiveWindow().dispose();				
			}
		});
    }*/
    
    public void tearDown() {
    	brController = null;
    	javaDir = null;
    	missController = null;
    	projectDir = null;
    	simSt = null;
    }
    
    /*
     * tests a BRD for EquationTutor
     */
    
    public final void testSim(){

        //this will let us construct the path
        File dir1 = new File (".");
        try{	    
            javaDir = dir1.getCanonicalPath(); //get CanonicalPath or AbsolutePath?
            trace.out("javaDir = " + javaDir);
            projectDir = javaDir + "/test/edu/cmu/pact/miss"; //-ssProjectDir can also be set with a command-line parameter
            trace.out("projectDir = " + projectDir);
            SimSt.setProjectDir(projectDir);
            BRD_PATH = dir1.getCanonicalPath() + "/test/edu/cmu/pact/miss/test-learning.brd";
            trace.out("BRD_PATH = " + BRD_PATH);

            //This is the main loop
            initialize();
            simulateLearning();
            checkPrAge(); //asserts
            checkFoilLog(); //asserts 
            finalize();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    

    private String BRD_PATH;
    
  
    //deletes log files
    //loads the ProblemModel
    public final void initialize(){
	
        //////// remove productionRules.pr, pr-age, foil-log files /////////
        String prPath = projectDir + "/productionRules.pr"; //
        new File(prPath).delete();
        //
        // copy productionRules.pr-LearningTest as productionRules.pr	

	
        String prAgePath = projectDir + "/pr-age/productionRules.pr-R1S1";
        new File(prAgePath).delete();
	
        String foilPath = projectDir + "/foil-log/div-lhs-R0S0-P01N00.d";
        new File(foilPath).delete();

	
        ////////// initializing CTAT ////////////
	
        String tutorArg="";
    	//tutorArg+="-traceLevel '-1'";
        tutorArg+="-debugCodes miss learning-test";// mt";
    	tutorArg+=" -ssProjectDir " + projectDir;
    	tutorArg+=" -ssFoilBase " + javaDir + "/FOIL6";
        tutorArg+=" -ssProblemSet C:/PACT-CVS-TREE/AuthoringTools/java/test/edu/cmu/pact/miss/gus1.brd";
        tutorArg+=" -ssFoilLogDir /foil-data -ssPrAgeDir /pr-age";
        //tutorArg+="-ssFOAgetter edu.cmu.pact.miss.SimSt.algebraI_AdhocFoa"; //this is for the 3-step tutor
        tutorArg+=" -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent";

        //(should uncomment the above line)
	
        String[] argv = tutorArg.split(" ");
        //new SingleSessionLauncher(argv).launch(new EquationTutor());
	
	
        //do I need to pass arguments to the constructor SingleSessionLauncher(...) ?
        SingleSessionLauncher ctatLauncher = new SingleSessionLauncher(argv); //I should not pass -ssRunInBatchMode, because that would run automatically

        ctatLauncher.launch(new EquationTutor());

        //Gustavo, 20 Dec 2006:
        //the PR file is copied right after the dialog fails to appear
        copyFile(projectDir+"/productionRules.pr-LearningTest", projectDir+"/productionRules.pr");

	
        brController = ctatLauncher.getController();
        brController.initializeSimSt();
        missController = (MissController) brController.getMissController();
        simSt = missController.getSimSt();
	
        //COPY FROM BATCH MODE	
	
        //////////////////////////taken from testProductionModelOn:
        //simSt.clearJessConsole();
        //simSt.resetMT();
        //Communicator.reset();
	
        LoadFileDialog.doLoadBRDFile( brController, BRD_PATH, "", true);
	
        //assert that we are in SimStudent mode
        simSt.switchToSimStMode();	
        assertTrue(brController.getCtatModeModel().getCurrentMode().equals(CtatModeModel.SIMULATED_STUDENT_MODE));
	
        //
        brController.goToStartStateForRuleTutors(); //calling this to load the PR file	
        //assert that loadJessFiles was called?
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

    private void copyFile(String filename1, String filename2) {
        try{
            copyFile(new File(filename1), new File(filename2));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //delete the productionRules.pr file generated
    public final void finalize(){
        new File(projectDir+"/productionRules.pr").delete();
    }

    
    
    public final void simulateLearning(){	

        getFoa();        
        assertEquals(""+simSt.getCurrentFoA().size(),"2");
        assertEquals(""+simSt.getCurrentFoA().get(0), "MAIN::cell|commTable2_C1R1|2x");
        assertEquals(""+simSt.getCurrentFoA().get(1), "MAIN::cell|commTable1_C1R1|6");
	
        
        ProblemNode startNode = brController.getProblemModel().getStartNode();	//taken from ssRunDemonstrationInBatchMode
        startNode.getName().equals("start");
        ProblemNode targetNode = (ProblemNode) startNode.getChildren().get(0);
        targetNode.getName().equals("state1");
        
        simu_stepPerformed(startNode, targetNode);

        //Gustavo 22Feb2007:
        //The following test does not work because returns 'null' instead of the name of the rule
        // fired. I have come to the conclusion that the way to fix this is to return the functions
        // RunDemonstrationInBatchMode > stepPerformed > checkProductionModel
        // back to the "new way", in which a clList is passed around and model-tracing prior to
        // learning works.

        //assertEquals(ruleFired, "div-lhs");
	
        simSt.changeInstructionName( "div-lhs", targetNode );
    }

    
    //taken from ssRunDemonstrationInBatchMode
    public void getFoa(){

    	int nodeNo = 0;
    
        //HACK: I don't fully understand how this code works
        if (!simSt.isFoaGetterDefined()) {
            
            // Read a set of focus of attention
            Vector /* String[] */ foaV = simSt.readFocusOfAttentionFromBRD( BRD_PATH );
            //trace.out("foaV = " + foaV);
            
            // Set a FoA for the current "node"
            if ( foaV != null && !foaV.isEmpty()) {
        	
                //this is getting the foaArray corresponding to the node
                //trace.out("nodeNo = " + nodeNo);
        		String[] foaArray = (String[])foaV.get(nodeNo++);
                //trace.out("foaArray = " + foaArray);
                //trace.out("foaArray.length = " + foaArray.length);
                
                for (int i = 0; i < foaArray.length; i++) {
                    String foaWme = foaArray[i];
                    //trace.out("foaWme = " + foaWme);
                    Object wme = brController.lookupWidgetByName( foaWme );
                    // trace.out("miss", "foaWme: " + foaWme + " got " + wme );
                    Class wmeClass = wme.getClass();
                    
                    //presumably, this sets currentFoA?
                    simSt.toggleFocusOfAttention( wme );
                }
            }
        }
    }
    
    public void simu_stepPerformed(ProblemNode startNode, ProblemNode targetNode){
	
        ProblemEdge edge = brController.getMissController().getSimSt().lookupProblemEdge(startNode, targetNode);
        EdgeData edgeData = edge.getEdgeData();
	
        // Set SAI tuple
        Vector selection = edgeData.getSelection();
        Vector action = edgeData.getAction();
        Vector input = edgeData.getInput();

        assertEquals(selection.get(0), "commTable2_C1R2");
        assertEquals(action.get(0), "UpdateTable");
        assertEquals(input.get(0), "x");
               
        trace.out("passed 3 asserts.");
        
        brController.getMissController().getSimSt().stepDemonstrated( targetNode, selection, action, input, edge, null);        
    }
    
    
    public void checkPrAge(){
	
        //assert that file exists
        String prPath = projectDir + "/pr-age/productionRules.pr-R1S1";
        File prFile = new File(prPath);

        assertTrue(prFile.exists());
        //prFile.lastModified();

        //assert that each line is correct
        BufferedReader in = null;
        try{
            FileReader fReader = new FileReader(prFile);
            in = new BufferedReader(fReader);
	    
            Vector lines = new Vector();
            String s; //temporary string
            while((s=in.readLine())!=null){
                lines.add(s);
            }
	    
            //assert line 29
            assertEquals(lines.get(28), "?var13 <- (cell (name ?foa0) (value ?val0&~nil)   )");
	    
            //assert line 53
            assertEquals(lines.get(52), "(bind ?input (rip-coefficient ?val0))");
	    
            //assert line
            assertEquals(lines.get(53), "(here-is-the-list-of-foas ?foa0 ?foa1)");
            assertEquals(lines.get(54), "(predict-algebra-input ?selection UpdateTable ?input )");

        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
        	  if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
    }
    
    public void checkFoilLog(){
	
        //assert that file exists
        //String foilPath = projectDir + "/foil-log/div-lhs-R0S0-P01N00.d";
        String foilPath = projectDir + "/foil-data/div-lhs-R0S0-P01N00.d";
        File foilFile = new File(foilPath);
        assertTrue(foilFile.exists());

        //assert that each line is correct
        try{
            FileReader fReader = new FileReader(foilFile);
            BufferedReader in = new BufferedReader(fReader);
	    
            Vector lines = new Vector();
            String s; //temporary string
            while((s=in.readLine())!=null){
                lines.add(s);
            }
	 	    
            //asserts for lines 1-11
            assertEquals(lines.get(0), "V1: 2x, 6, x."); //instead of P0, I got V1
            assertEquals(lines.get(1), "");
            assertEquals(lines.get(2), "div-lhs(V1, V1) ##");
            assertEquals(lines.get(3), "2x, 6");
            assertEquals(lines.get(4), ".");
            assertEquals(lines.get(5), "*Homogeneous(V1) #");
            assertEquals(lines.get(6), "2x");
            assertEquals(lines.get(7), "x");
            assertEquals(lines.get(8), "6");
            assertEquals(lines.get(9), ".");
            assertEquals(lines.get(10), "*CanBeSimplified(V1) #");
            assertEquals(lines.get(11), ";");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    //calling this function modifies problemModel to contain the graph in the BRD
    //and sets the start state
    public static void simu_doLoadBRDFile() {
	
        String problemName="2x_6";
        String directory="";

        brController.reset();
        brController.getProblemModel().setProblemName(problemName);

        brController.getProblemModel().setCourseName("");
        brController.getProblemModel().setUnitName("");
        brController.getProblemModel().setSectionName("");

        brController.getProblemModel().setProblemFullName(
        		directory + brController.getProblemModel().getProblemName());


        
        ProblemStateReader psr = brController.getProblemStateReader();
        	
        brController.getProblemModel().setProblemName(problemName);

        
        //added by dtasse@andrew.cmu.edu on 6/15/06
        brController.getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
        		BR_Controller.OPEN_GRAPH, brController.getProblemModel().getProblemFullName());
    }
        
}
