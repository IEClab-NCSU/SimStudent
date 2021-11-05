/**
 * Describe class MissController here.
 *
 *
 * Created: Thu May 05 12:49:11 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.console.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jess.JessException;
import pact.CommWidgets.TutorWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.SimStBackendExternal;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.MetaTutor.APlusHintMessagesManager;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStPLEWrapper;
import edu.cmu.pact.miss.PeerLearning.GameShow.Contestant;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowWrapper;
import edu.cmu.pact.miss.console.view.MissConsole;
import edu.cmu.pact.miss.storage.StorageClient;

public class MissController implements MissControllerExternal {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    /** Miss: the Simulated Student object */
    SimSt simSt;
    /** Logger for sim student logs */
    private SimStLogger logger;
    /** Miss Console object */
    private MissConsole missConsole;
    /** StorageClient for saving instructions file on server */
    private StorageClient storageClient;
	/**	Hint messages manager to display hints to student tutor in meta-tutor mode */
	private APlusHintMessagesManager aPlusHintMessagesManager;
	private CTAT_Launcher ctatLauncher;
	String runType = System.getProperty("appRunType");
	
    // Menu items on the Miss Console menu bar
    public static final String FILE_MENU = "File";
    public static final String NEW_PROBLEM = "New Problem";
    public static final String LOAD_WME_TYPE = "Load WME Types";
    public static final String INIT_WME = "Initialize WMEs";
    public static final String LOAD_PREDICATE = "Load Feature Predicates";
    public static final String LOAD_OPERATOR = "Load Operators";
    // Added 6/7/06 - new items for saving/loading old instructions
    public static final String LOAD_INSTRUCTIONS = "Load Instructions";
    public static final String SAVE_INSTRUCTIONS = "Save Instructions";

    public static final String PROD_SYS_MENU = "Prod.System";
    public static final String TEST_MODEL_ON = "Test Current Model on...";

    public static final String DEBUG_MENU = "Debugging...";
    public static final String HIBERNATE_SS = "Hibernate Sim.St";
    public static final String WAKEUP_SS = "Wakeup Sim.St";

    public static final String INSTRUCTIONS_FILE_NAME = "instructions.txt";
    
    /** A File Chooser */
    private JFileChooser fileChooser = new JFileChooser();
    /** Mode to preserve existing production rules */
    public static final String PRESERVE_PR = "preserve_pr";
    
    private String typeChecker = "edu.cmu.pact.miss.FeaturePredicate.valueTypeForAlgebra";
    
    public SimSt getSimSt() {
    	return this.simSt;
    }
    
    public MissConsole getMissConsole() {
    	return this.missConsole;
    }
    
    void setMissConsole(MissConsole missConsole) {
    	this.missConsole = missConsole;
    }
    
    public SimStLogger getLogger() {
    	return logger;
    }
    
    public StorageClient getStorageClient() {
		return storageClient;
	}
	public void setStorageClient(StorageClient storageClient) {
		this.storageClient = storageClient;
	}
	
	public APlusHintMessagesManager getAPlusHintMessagesManager() {
		return aPlusHintMessagesManager;
	}
	public void setAPlusHintMessagesManager(
			APlusHintMessagesManager aPlusHintMessagesManager) {
		this.aPlusHintMessagesManager = aPlusHintMessagesManager;
	}
	
	// BR_Controller object
    //private BR_Controller brController;
    
    public BR_Controller getBrController() {
    	return getCtatLauncher().getFocusedController();
    	//return this.brController;
    }
    
    JFileChooser getFileChooser() {
    	return this.fileChooser;
    }


    public String getDefaultRuleSetName() {
        return SIM_ST_SKILL_SET_NAME;
    }
    
    // SimSt Peer Learning Environment
    private SimStPLE simStPLE = null;
    public SimStPLE getSimStPLE() {
        return simStPLE;
    }
    public void setSimStPLE(SimStPLE simStPLE) {
        this.simStPLE = simStPLE;
    }
    
    // set to be true by SimSTPLE when it's running
    private boolean PLEon = false;
    public boolean isPLEon() {
        return PLEon;
    }
    public void setPLEon(boolean eon) {
        PLEon = eon;
    }
    
    
    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - - - 
    // -

    /**
     * Creates a new <code>MissController</code> instance.
     *
     */
    public MissController(BR_Controller brController) {
        this(brController, "");
    }

    /////// this should be the event handler for when "Simulated Student" is selected
    public void SimStudentSelected() {}
    
    public MissController(CTAT_Launcher ctatLauncher, String preservePr) {
    	this.ctatLauncher = ctatLauncher;
        initMissController(this.getBrController(), preservePr);
    }
    
    public MissController(BR_Controller brController, String preservePr) {
        initMissController(brController, preservePr);
    }

	/**
	 * @param brController
	 */
	protected void initMissController(BR_Controller brController, String preservePr) {
		// setBrController must come first, since SimSt() referers
        // brController

        logger = new SimStLogger(brController);
        
      //this should only happen when "Simulated Student" is selected
    	this.simSt = new SimSt(this);
    	this.missConsole = new MissConsole(this, brController);
    	String runType = System.getProperty("appRunType");
    	
    	if(runType != null && runType.equalsIgnoreCase("servlet")){
        	SimSt.setProjectDir(System.getProperty("projectDir"));
    		getSimSt().setSsWebAuthoringMode(true);
    	}
    	else if(runType != null && runType.equalsIgnoreCase("webstart")) {
    		getSimSt().setWebStartMode(true);
    	}
    	else if(runType != null && runType.equalsIgnoreCase("springBoot")) {
    		SimSt.setProjectDir(System.getProperty("projectDir"));
    		getSimSt().setSsWebAuthoringMode(true);
    	}
    	
    	getSimSt().initBKwithMissConsole();
        // Restore the FileChooser preference
        PreferencesModel pm = getPreferencesModel();
        String dir = pm.getStringValue("Miss FileChooser Path");
        if (dir != null) {
            fileChooserSetCurrentDir(dir);
        }

        // Initialize the StorageClient object for saving the file
        this.storageClient = new StorageClient();
        // Initialize things for CTAT
        // 
        // Set "Project" dir ...
//        String projectDir = getProjectDir();
//        getSimSt().setProjectDir(projectDir);

        // Backup old production rule file
        // Fri Jul  7 00:20:40 LDT 2006 :: Noboru
        // This must be done only when SimSt is running under the interactive mode
        // hence moved into initSimStInteractive()
//        if (!preservePr.equals(PRESERVE_PR)) {
//            backupPrFile(getSimSt().getProjectDir(), getSimSt().getPrFileName());
//        }
	}

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    public void reset() {
	// getMissConsole().reset();
    }

    // Gustavo, 2 August 2007 
    // Initialization only for the interactive mode
    //
    //This refactoring is to prevent pop-ups in JUnitTests
    /**
     * Gustavo 8 Nov 2007:
     *  Check if the PR file already exists
     *  If so, deal with it.
     */
    public void dealWithAnyExistingPrFile() {

    	boolean deletePrFile;
        int buttonPressed;

        String prFileName = getSimSt().getPrFileName();
        String userBundleDir = getSimSt().getUserBundleDirectory();
        File prFile;
        if (userBundleDir != null)
        	prFile = new File(userBundleDir, prFileName);
        else
        	prFile = new File(getSimSt().getProjectDir(), prFileName); //always needs to be created anyway
        
        //Gustavo 8 Nov 2007: we only do something if a PR file already exists
        if (prFile.exists()) {
        	if(trace.getDebugCode("ss"))trace.out("ss", "Running in Contest Mode: " + isSsContest());
            if (getBrController().isClArgumentSetToProtectProdRules() || isSsContest()){ //don't pop-up, don't delete
            	if(trace.getDebugCode("gusmiss"))trace.out("gusmiss", "initializeSimStInteractive: preserve is true");
                deletePrFile = false;
            }
            else {
            	if(trace.getDebugCode("gusmiss"))trace.out("gusmiss", "initializeSimStInteractive: preserve is false");
            	            	
                if (!getBrController().isDeletePrFile()) { //pop-up, maybe delete
                	if(trace.getDebugCode("gusmiss"))trace.out("gusmiss", "initializeSimStInteractive: delete is false");
                    buttonPressed = askWhatToDoWithExistingPrFile(prFile, getSimSt().getPrFileName());

                    //if appropriate, rename the PR file 
                    if (buttonPressed==JOptionPane.YES_OPTION){ //rename
                        File backupFile = chooseFileByDialog(OPEN);
                        prFile.renameTo(backupFile);
                        if(trace.getDebugCode("miss"))trace.out("miss",
                                "The file " + prFileName + " has been renamed to " +
                                backupFile);            
                    }

                    //specify whether to delete
                    deletePrFile = (buttonPressed==JOptionPane.NO_OPTION);

                }
                else { //don't pop-up, do delete
                	if(trace.getDebugCode("gusmiss"))trace.out("gusmiss", "initializeSimStInteractive: protect is false");
                    deletePrFile = true;
                }
            }

            
            if(deletePrFile) {
            	if(trace.getDebugCode("miss"))trace.out("miss", "deleting PR file");
            	deleteFile(prFile);
            }
        }

    }

    
    
    
    /**
     * delete a file, if it exists
     * @param dir
     * @param prFileName
     */
    private void deleteFile(String dir, String fileName) {
        File file = new File(dir, fileName);
        deleteFile(file);
    }
        
    /**
     * delete a file, if it exists
     */
    private void deleteFile(File file) {
        if (file.exists()){
            System.out.println("deleting a file!");
            file.delete();
        }
    }

    
    
    
    /**
     * Triggered by a menu item in the Behavior Recorder 
     *
     * @param isActive a <code>boolean</code> value
     */
    public void activate(boolean newStatus) {

        getMissConsole().setVisible(newStatus);
        String msg = 
            "Miss Controller " + (newStatus ? "" : "de") + "activated!!";
        if(trace.getDebugCode("miss"))trace.out("miss", msg);
    }
    
    /**
     * Triggered by the Sim. St. Console menu to activate / inactivate
     * Sim. St. rule induction while Sim. St. console is up running
     * hence other Sim. St. related CTAT features is on
     *
     * @param hibernate a <code>boolean</code> value
     **/
    public void hibernateSimSt(boolean hibernate) {

	getSimSt().setMissHibernating(hibernate);
	getMissConsole().switchMissHibernationMenu(hibernate);
    }

    public boolean isMissHibernating() {
	return getSimSt().isMissHibernating();
    }

    /**
     * Called by MissConsole when "New Graph" in the BR is selected
     *
     */
    public void startNewProblem() {
        this.getSimSt().startNewProblem();
    }

    /**
     * Called by BR_Controller when a new start state is created
     *
     * @param initStateName a <code>String</code> value
     */
    public void startStateCreated(ProblemNode startProblemNode) {
	this.getSimSt().startStateCreated(startProblemNode);
    }

    /**
     * Called by SimSt when a start state name is changed
     *
     * @param name a <code>String</code> value
     */
    public void setConsoleCurrentProblemName(String name) {
	getMissConsole().setCurrentProblemName(name);
    }

    public void addConsoleProblemList(String name) {
	getMissConsole().addProblemList(name);
    }

    /**
     * Called by BR_Controller when a new step is demonstrated.
     * Inquiry SimSt if Focus of Attention is specified or not. 
     *
     * @return a <code>boolean</code> value
     **/
    public boolean isFocusOfAttentionSpecified() {
	return this.getSimSt().isFocusOfAttentionSpecified();
    }

    /**
     * Called by BR_Controller when a step is demonstrated but focus
     * of attention is not specified
     *
     **/
    public void askSpecifyFocusOfAttention() {
	getMissConsole().message("Please specify Focus Of Attention.");
	if(trace.getDebugCode("gusIL"))trace.out("gusIL", "Please specify Focus Of Attention.");
    }

    /**
     * Called by an instance of JCommWidget (diffent type of
     * JCommWidget might have it's own method to call this method)
     * when an actino to specify a focus-of-attention is taken place
     *
     * @param widgetName a name of the JCommWidget where the mouse
     * was clicked
     **/
    public void toggleFocusOfAttention(Object widget) {

	this.getSimSt().toggleFocusOfAttention(widget);
    }
    
    /**
     * Called by DemonstrateModeModelHandler (which is called by BR_Controller)
     * when a new edge (link btw states) is added to the BR. Is also called when
     * a new node is generated (which will most often happen).
     *
     * @param selection a <code>Vector</code> value
     * @param action a <code>Vector</code> value
     * @param input a <code>Vector</code> value
     * @param messageObject a <code>MessageObject</code> value
     * @param actionType a <code>String</code> value
     **/
    public void stepDemonstrated(ProblemNode state,
				  Vector /* String */ selection,
				  Vector /* String */ action,
				  Vector /* String */ input,
				  ProblemEdge edge) {
    	
	getSimSt().stepDemonstrated(state, selection, action, input, edge, null);//, null);
    }
    
    /**
     * Called when a skill name (i.e., the production name) is chaged
     * on the edge that goes into a specified state
     *
     * @param name the new skill name
     * @param state a Problem Node to which the specified edge is
     * pointing
     **/
    public void skillNameSet(String name, ProblemNode state) {
	getSimSt().changeInstructionName(name, state); 
    }

    /**
     * Called by Instruction.setName() when a skill name is changed
     *
     * @param name a <code>String</code> value
     */
    public void updateConsoleSkillNameList(Vector /* String */ skillNames) {
	getMissConsole().updateSkillNameList(skillNames);
    }

    /**
     * Update the number of steps displayed on the Miss Console
     *
     * @param n an <code>int</code> value
     */
    public void setNumStepDemonstrated(int n) {

	getMissConsole().setNumStepsDemonstrated(n);
    }

    /**
     * Called by SimSt when rules are re-compiled
     *
     * @param n an <code>int</code> value
     */
    public void consoleSetNumProductionRules(int n) {

	getMissConsole().setNumProductionRules(n);
    }

    /**
     * Called by BR_Controller.updateCurrNode() when a state in the BR
     * is clicked, which signals the Student Interface to resume
     * previous state hence needed to re-highlight the focus of
     * attention for that state
     *
     * @param state a <code>ProblemNode</code> value
     **/
    public void rollBackState(ProblemNode state) {

	// *****
    }

    public void ssShuffleRunInBatch(String[] trainingSet, String[] testSet,
				     String output, int numIteration) {

	getSimSt().ssShuffleRunInBatch(trainingSet, testSet, output,
					numIteration);
    }

    private boolean batchMode = false;
    public boolean isBatchModeOn()
    {
    	return batchMode;
    }
    public void runSimStInBatchMode(String[] trainingSet,
            String[] testSet,
            String output) {
    	batchMode = true;
        getSimSt().ssRunInBatchMode(trainingSet, testSet, output);
    }
    
    private boolean contestMode = false;
    public boolean isContestOn()
    {
    	return contestMode;
    }
    public void runSimStInContestMode()
    {
    	contestMode = true;
    	getSimSt().ssRunInContestMode();
    }

//    public void runSimStInILBatchMode(String[] trainingSet,
//            String[] testSet,
//            String output) {
//
////      String[] trSet = {"C:/CVS-TREE/Tutors/SimSt/SimStAlgebraI/Problems/01-08/BRD/075kbY53c1/141_9.66+6.5y_-7.84y-0.73.brd"};
////        String[] trSet = {"C:/CVS-TREE/Tutors/SimSt/SimStAlgebraI/Problems/01-08/BRD/075kbY53c1/142_3.16+4.9x_-4.66-8.77x.brd"};
////        trainingSet = trSet;
////        testSet = null;
////        output = null;
//        getSimSt().ssRunInILBatchMode(trainingSet, testSet, output);
//    }


    /**
     * Triggered by SingleSessionLauncher Tell Sim. St. whether the evaluation
     * of RHS operators should be cached (default) or not
     *
     * @param flag a <code>boolean</code> value
     **/
    public void setOpCached(boolean flag) {

	getSimSt().setOpCached(flag);
    }

    
    public void setFoilLogDir(String foilLogDir) {
	getSimSt().setFoilLogDir(foilLogDir);
    }

    public void setFoilMaxTuples(String foilMaxTuples) {
    	getSimSt().setFoilMaxTuples(foilMaxTuples);
        }
    
    
    
    public void setPrAgeDir(String prAgeDir) {

	getSimSt().setPrAgeDir(prAgeDir);
    }

    public void setSsBatchMode(boolean flag) {
    	getSimSt().setSsBatchMode(flag);
    }
    
    public void setSsFixedLearningMode(boolean flag) {
    	getSimSt().setSsFixedLearningMode(flag);
    }
    
    public void setSsWebAuthoringMode(boolean flag) {
    if(getSimSt() != null) {
		getSimSt().setSsWebAuthoringMode(flag);
	}
    }
    
    
    public void setSsMetaTutorMode(boolean flag) {
    	if(getSimSt() != null) {
    		getSimSt().setSsMetaTutorMode(flag);
    	}
    	aPlusHintMessagesManager = new APlusHintMessagesManager(getCtatLauncher());
    }
    
    public void setSsCogTutorMode(boolean flag) {
    	if(getSimSt() != null) {
    		getSimSt().setSsCogTutorMode(flag);
    	}
    
    }
    
    public void setSsAplusCogTutorMode(boolean flag) {
    	if(getSimSt() != null) {
    		getSimSt().setSsAplusCtrlCogTutorMode(true);
    	}
    
    }
    
    
    
    public void setSsMetaTutorModeLevel(String ssMetatutorModeLevel) {
    	if(getSimSt() != null) {
    		getSimSt().setSsMetaTutorModeLevel(ssMetatutorModeLevel);
    	}
    }

    
    public void setSsQuizProblemAbstractorClass(String quizProblemAbstractor) {
    	getSimSt().setSsQuizProblemAbstractor(quizProblemAbstractor);
    }
    
    public void setSsCondition(String ssCondition) {

	getSimSt().setSsCondition(ssCondition);
    }

    public void setSsFoaGetterClass(String foaGetter) {
    	getSimSt().setSsFoaGetter(foaGetter);
    }
    //nbarba 06/09/16: Problem Accessor may be added externally to define how problem is evaluated.
    public void setSsProlbemAssesorClass(String paClass) {
    	getSimSt().setProblemAssessor(paClass);
    }
    
    public void setSsSelectionOrderGetterClass(String selectionGetter) {
    	getSimSt().setSsSelectionGetter(selectionGetter);
    }
    
    public void setSsSaiConverterClass(String saiConverter) {
    	getSimSt().setSsSaiConverter(saiConverter);
    }
    
    public void setSsInterfaceElementGetterClass(String interfaceElementGetter) {
    	getSimSt().setSsInterfaceElementGetter(interfaceElementGetter);
    }
    
    public void setSsHeuristicBasedIDS(boolean flag) {
    	getSimSt().setSsHeuristicBasedIDS(flag);
    }

    public void setSsFoaClickDisabled(boolean flag)
    {
    	getSimSt().setSsFoaClickDisabled(flag);
    }

    public void setSsInputCheckerClass(String foaGetter) {   	 
    	getSimSt().setSsInputChecker(foaGetter);
    }
    
    public void setSsStartStateCheckerClass(String ssChecker) {   	 
    	getSimSt().setStartStateChecker(ssChecker);
    }
    
    /* @author: jinyul */
    public void setSsSkillNameGetterClass(String skillNameGetter) {
    	getSimSt().setSsSkillNameGetter(skillNameGetter);
    }
    
    /* @author: Tasmia */
    /*public void setSsNearSimilarProblemsGetterClass(String nearSimilarProblemsGetter) {
    	getSimSt().setSsNearSimilarProblemsGetter(nearSimilarProblemsGetter);
    }
    public void setSsBothAgreeSpeechGetterClass(String bothAgreeSpeechGetterClass) {
    	getSimSt().setSsBothAgreeSpeechGetter(bothAgreeSpeechGetterClass);
    }*/
    public void setSsPathOrderingClass(String className) {
        getSimSt().setSsPathOrderer(className);
    }    
    
    public void setSsMemoryWindowSize(String memoryWindowSize) {
	getSimSt().setMemoryWindowSize(Integer.parseInt(memoryWindowSize));
    }

    public void setSsLearningLogFile(String learningLogFile) {
	getSimSt().setLearningLogFile(learningLogFile);
    }

    public void setSsLearnCltErrorActions(boolean flag) {
    	getSimSt().setLearnCltErrorActions(flag);
    }
    
    public void setSsLearnBuggyActions(boolean flag) {
    	getSimSt().setLearnBuggyActions(flag);
    }

    public void setSsLearnCorrectActions(boolean flag) {
    	getSimSt().setLearnCorrectActions(flag);
    }
    
    public void setSsVerifyNumFoA(boolean flag) {
    	if(trace.getDebugCode("nlexc"))trace.out("nlexc", "setSsIsBootstrapping called!");
	getSimSt().setVerifyNumFoA(flag);
    }
    
    public void setSsFeaturePredicateFile(String featurePredcatesFile) {
	getSimSt().setFeaturePredicateFile(featurePredcatesFile);
    }
    
    public void setSsMaxSearchDepth(int i) {
    	getSimSt().setMaxSearchDepth(i);
    }

    public void setSsOperatorFile(String operatorFilePath) {
    	getSimSt().setOperatorFile(operatorFilePath);
    }
    
    public void setSsUserDefSymbols(String userDefSymbols) {
        getSimSt().setUserDefSymbols(userDefSymbols);
    }
    
    public void setSsPredictObservableActionName(String dn) {
        getSimSt().setPredictObservableActionName(dn);
    }
    

    public void setSsMemoryWindowOverRules(boolean flag) {
	getSimSt().setMemoryWindowOverIndividualRules(flag);
    }

    public void setForceToUpdateModel(boolean flag) {
	getSimSt().setForceToUpdateModel(flag);
    }

    public void setSsFoaSearch(boolean flag) {
        getSimSt().setSsFoaSearch(flag);
    }
    
    
    public void setClArgumentSetToProtectProdRules(boolean flag) {
        getBrController().setClArgumentSetToProtectProdRules(flag);
    }
    
    public void setSsDeletePrFile(boolean flag) {
        getBrController().setDeletePrFile(flag);
    }
    

    public void setSsSearchTimeOutDuration(String longDuration) {
        getSimSt().setTimeoutDuration(Long.parseLong(longDuration));
    }
    
    public void setSsInactiveInterfaceTimeOutDuration(String longDuration) {
        getSimSt().setInactiveInterfaceTimeoutDuration(Long.parseLong(longDuration));
    }
    
    public void setSsTutorServerTimeOutDuration(String longDuration) {
        getSimSt().setServerTimeoutDuration(Long.parseLong(longDuration));
    }
    

    public void setSsLogging(boolean flag) {
    	getSimSt().setLoggingEnabled(flag);
    }

    public void setSsLocalLogging(boolean flag) {
    	getSimSt().setLocalLoggingEnabled(flag);
    }
    
    public void setSsDummyContest(boolean flag)
    {
    	getSimSt().setDummyContestResponse(flag);
    }

    public void setSsContestServer(String server)
    {
    	Contestant.setContestServer(server);
    }
    
    public void setSsContestPort(int port)
    {
    	Contestant.setContestPort(port);
    }
    
    public void setSsUserID(String userID)
    {
    	getSimSt().setUserID(userID);
    	getBrController().getLogger().setAnonymizedStudentName(userID);
    }
    
    public void setSsSimStName(String name)
    {
    	getSimSt().setSimStName(name);
    }
    
    public void setSsSimStImage(String img)
    {
    	getSimSt().setSimStImage(img);
    }
    
    public void setSimStProblemsPerQuizSection(int num)
    {
    	getSimSt().setSimStProblemsPerQuizSection(num);
    }

    public void setSimStLogURL(String logURL)
    {
    	SimStLogger.log_url = logURL;
    }
    

    public void ssUseDecomposition()
    {
        getSimSt().setDecomposeInput(true);
        
    }

    public void setSsRuleActivationTestMethod(String ssRuleActivationTestMethod) {
        getSimSt().setRuleActivationTestMethod(ssRuleActivationTestMethod);
    }

    public void setSsQuizGradingMethod(String ssQuizGradingMethod) {
        getSimSt().setQuizGradingMethod(ssQuizGradingMethod);
    }
    
    

    
    public void setSsJessOracleProductionFile(String ssPackageName) {
        getSimSt().setSsJessOracleProductionFile(ssPackageName);
    }
    
    
    public void setSsPackageName(String ssPackageName) {
        getSimSt().setPackageName(ssPackageName);
    }
    
    
    public void setSsPrValidationMethod(String ssPrValidationMethod) {
        getSimSt().setPrValidationMethod(ssPrValidationMethod);
    }
    
    
    public void setSsModelTracingValidationOutcome(String ssModelTracingValidationOutcome) {
        getSimSt().setModelTracingValidationOutcomeMethod(ssModelTracingValidationOutcome);
    }
    
    
    public void setSsHintMethod(String ssRuleActivationTestMethod) {
        getSimSt().setHintMethod(ssRuleActivationTestMethod);
    }
    
    
    
    
    public void setSsNumBadInputRetries(int numRetries)
    {
    	getSimSt().setSsNumBadInputRetries(numRetries);
    }

    /* @author: Huan Truong */
    public void setSsUseTutalk(String param)
    {
    	getSimSt().setUseTutalk(param);
    }
    /* @author: Tasmia */
    public void setSsUseCTIBothStuckWithTutalk(String param)
    {
    	getSimSt().setUseCTIBothStuckWithTutalk(param);
    }
    
    public void setSsIntroVideo(String name){
    	SimStPLE.setVideoIntroductionName(name);
    }

    public void setSsOverview(String name){
    	SimStPLE.setOverviewPageName(name);
    }
    
	public void setSsCLQuizReqMode() {
    	if(getSimSt()!= null)
		{
			getSimSt().setSSCLQuizReqMode(true);
		}
	}
	
	
	   
	   
	public void setSsSelfExplainMode()
	{    	
		if(getSimSt()!= null)
		{
			getSimSt().setSSSelfExplainMode(true);
		}
	}
	
	// Added by Tasmia for CTI
	public void setSsConstructiveTuteeInquiryFTIMode()
	{    	
		if(getSimSt()!= null)
		{
			getSimSt().setSsConstructiveTuteeInquiryFTIMode(true);
		}
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Background knowledge I/O
    // 

    /**
     * Returns a file name for the file that contains WME type
     * definitions
     *
     * @return a <code>String</code> value
     **/
    public String getSimStWmeTypeFile() {
	return getSimSt().getWmeTypeFile();
    }

    /**
     * Triggered by a file menu in the Miss Console, set a file name
     * for the WmeTypeFile
     *
     **/
    public void setSimStWmeTypeFile() {

	getMissConsole().message("Select a file that contains WME types...");
	
	File file = chooseFileByDialog(OPEN);
	if (file == null) {
		return;
	}
	String fileName = file.getAbsolutePath();

	getSimSt().setWmeTypeFile(fileName);
	setConsoleWmeTypeFileLabel(fileName);

	// getMissConsole().clearMessage();
    }

    public void setConsoleWmeTypeFileLabel(String label) {
    	if (getMissConsole() != null)
    		getMissConsole().setWmeTypeFileLabel(label);
    }

    /**
     * Returns a file name to initialize the WMEs 
     *
     * @return a <code>String</code> value
     */
    public String getSimStInitStateFile() {
	return getSimSt().getInitStateFile();
    }

    /**
     * Triggered by a file menu in the Miss Console.  Set a file name
     * for the InitStateFile
     *
     **/
    public void setSimStInitStateFile() {

	getMissConsole().message("Select a file that contains the initial WME definitions...");

	File file = chooseFileByDialog(OPEN);
	if (file == null) {
		return;
	}
	String fileName = file.getAbsolutePath();

	getSimSt().setInitStateFile(fileName);
	setConsoleInitWmeFileLabel(fileName);

	// getMissConsole().clearMessage();
    }

    public void setConsoleInitWmeFileLabel(String label) {
	getMissConsole().setInitWmeFileLabel(label);
    }

    /**
     * Triggered by a file menu in the Miss Console, have Sim Student
     * read predicate symbols from a specified file
     *
     **/
    public void readSimStPredicateSymbols() {

	String msg = "Select a file to read predicate symbols...";
	getMissConsole().message(msg);

	File file = chooseFileByDialog(OPEN);
	if (file == null) {
		msg += "no file selected.";
		getMissConsole().message(msg);
		return;
	}
	String fileName = file.getAbsolutePath();

	msg += "done\nRead predicates from " + fileName + "...";
	getMissConsole().message(msg);

	// Have SimSt read predicate symbols...
	getSimSt().readPredicateSymbols(fileName);
	// Diaplay predicate symbols to the console...
	consoleDisplayPredicates(getSimSt().getPredicates());

	msg += "done";
	getMissConsole().message(msg);
    }

    public void consoleDisplayPredicates( Vector /* String */ predicates ) {
    	if(getMissConsole() != null)
    		getMissConsole().displayPredicates( predicates );
    }

    /**
     * Triggered by a file menu in the Miss Console, have Sim Student
     * read operator symbols from a specified file
     *
     **/
    public void readSimStOperators() {

	String msg = "Select a file to read operator symbols...";
	getMissConsole().message(msg);

	File file = chooseFileByDialog(OPEN);
	if (file == null) {
		msg += "no file selected.";
		getMissConsole().message(msg);
		return;
	}
	String fileName = file.getAbsolutePath();

	msg += "done\nRead operator symbols from " + fileName + "...";
	getMissConsole().message (msg);

	// Have SimSt read operator symbols
	getSimSt().readRhsOpList(fileName);
	// Display operator symbols to the console
	consoleDisplayOperators(getSimSt().getRhsOpList());

	msg += "done";
	getMissConsole().message(msg);
    }

    public void consoleDisplayOperators(Vector /* String */ ops) {
	getMissConsole().displayOperators(ops);
    }
    
    /**
     * Added 6/8/06 - Reid Van Lehn <rvanlehn@mit.edu>
     * Save/load operations for instructions, called from console menu
     */
    
    public void saveInstructions() {
    	String msg = "Select location to save demonstrated steps...";
    	getMissConsole().message(msg);

    	File file = chooseFileByDialog(SAVE);
    	if (file == null) {
    		msg += "no file selected.";
    		getMissConsole().message(msg);
    		return;
    	}
       	String fileName = file.getAbsolutePath();

    	msg += "done\nSaved work to " + fileName + "...";
    	getMissConsole().message (msg);

    	// Print the instructions to the file
    	getSimSt().saveInstructions(file);

    	msg += "done";
    	getMissConsole().message(msg);
    }
    
    // jinyul - Automatically saves current instructions into instructions.txt or instructions-userID.txt
    public void autoSaveInstructions() {
    	String msg="";
        String fileName = "instructions.txt";
        if(getSimSt() != null && getSimSt().getUserID() != null && !getSimSt().isSsWebAuthoringMode())
        	fileName = "instructions-"+getSimSt().getUserID()+".txt";
        msg += "done\nSaved work to " + fileName + "...";
    	getMissConsole().message (msg);

    	// Check if the application is running locally or via webstart
    	File instructionsFile = null;
        File wmeTypeFile = null;
        wmeTypeFile = new File(new File(".").getAbsolutePath(), SimSt.WME_TYPE_FILE);
        
        if(wmeTypeFile != null && wmeTypeFile.isAbsolute() && wmeTypeFile.exists()) {
        	if(getSimSt().isSsWebAuthoringMode()) {
        		instructionsFile = new File(getSimSt().getUserBundleDirectory(), fileName);
        	} else {
        		instructionsFile = new File(getSimSt().getLogDirectory(), fileName); 
        	}
        	getSimSt().saveInstructions(instructionsFile);
        } else if(getSimSt().isWebStartMode()) { // For webstart
    		
        	try {
        		instructionsFile = new File(WebStartFileDownloader.SimStWebStartDir + fileName);
    			getSimSt().saveInstructions(instructionsFile);
    			storageClient.storeFile("instructions-"+getSimSt().getUserID()+".txt", instructionsFile.getCanonicalPath());
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
         } else if (getSimSt().isSsWebAuthoringMode()) {
        	 instructionsFile = new File(getSimSt().getUserBundleDirectory(), fileName);
        	 getSimSt().saveInstructions(instructionsFile);
         }
        msg += "done";
        getMissConsole().message(msg);
    }
    
    public void loadInstructions() {
    	String msg = "Select file to load previously demonstrated steps...";
    	getMissConsole().message(msg);

    	File file = chooseFileByDialog(OPEN);
    	if (file == null) {
    		msg += "no file selected.";
    		getMissConsole().message(msg);
    		return;
    	}
       	String fileName = file.getAbsolutePath();

    	msg += "done\nOpened work from " + fileName + "...";
    	getMissConsole().message (msg);

    	// Print the instructions to the file
    	try {
    		getSimSt().loadInstructions(file);
    	} catch (Exception e) {
    		getMissConsole().message("Error: Could not load instructions.");
    		e.printStackTrace();
    		logger.simStLogException(e);
    		return;
    	}
    	msg += "done";
    	getMissConsole().message(msg);
    }

    //jinyul
    public void autoLoadInstructions() {
    	
    	String fileName = "instructions.txt";
    	if(getSimSt() != null && getSimSt().getUserID() != null && !getSimSt().isSsWebAuthoringMode())
        	fileName = "instructions-"+getSimSt().getUserID()+".txt";
        File file = null;
        if(getSimSt().isSsWebAuthoringMode())
        	file = new File(getSimSt().getUserBundleDirectory(), fileName);
        else 
        	file = new File(fileName);

    	// Check if application is running locally or using web-start
        if(!getSimSt().isWebStartMode()) {
	    	
	    	// Load the instructions from the file
	    	if(file!=null && file.exists()) {
	        	getBrController().setDeletePrFile(true);
	        	this.dealWithAnyExistingPrFile();
	        	/*in webAuthoring, ask about the instructions
	        	if (this.getSimSt().isSsWebAuthoringMode()){
	        		int buttonPressed=askToResumeTraining();
	        		 if (buttonPressed==JOptionPane.NO_OPTION){ 
	        			 	return;
	        		 }
	        	}*/
	        	
	    		try {
					getSimSt().loadInstructions(file);
				} catch (Exception e) {
					trace.out("Error reading instructions file.");
					setClArgumentSetToProtectProdRules(false);
					return;
				}
	    		setClArgumentSetToProtectProdRules(true);
	    	}
	    	if(file==null || !file.exists() || (getSimSt() != null && getSimSt().getInstructionCount() == 0))
	    	{
	    		fileName = "instructions-9_initial.txt";
	    		file = new File(fileName);
	
	        	// Load the instructions from the file
	        	if(file!=null && file.exists()) {
	            	getBrController().setDeletePrFile(true);
	            	this.dealWithAnyExistingPrFile();
	        		try {
	        			if(trace.getDebugCode("miss"))trace.out("miss", "Loading instructions file: " + file);
	    				getSimSt().loadInstructions(file);
	    			} catch (Exception e) {
	    				trace.out("Error reading instructions file.");
	    				e.printStackTrace();
	    				setClArgumentSetToProtectProdRules(false);
	    				return;
	    			}
	        		setClArgumentSetToProtectProdRules(true);
	        		if(trace.getDebugCode("miss"))trace.out("miss", "Exiting autoLoadInstructions");
	        	}
	    	}
        } else { // Running using web-start 
        	
        	boolean successful = false;
        	if(trace.getDebugCode("miss"))trace.out("miss", "Loading the file: " + fileName + "for user: " + getSimSt().getUserID());
        	try {
				successful = storageClient.retrieveFile(getSimSt().getUserID(), fileName,
						WebStartFileDownloader.SimStWebStartDir);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	if(successful) { 
        		
        		file = new File(WebStartFileDownloader.SimStWebStartDir + fileName);
        		if(trace.getDebugCode("miss"))trace.out("miss", "WebStart: Loading the file for the user: " + getSimSt().getUserID() + "  " + WebStartFileDownloader.SimStWebStartDir + fileName);
            	// Load the instructions from the file
                getBrController().setDeletePrFile(true);
                this.dealWithAnyExistingPrFile();
            	try {
        			getSimSt().loadInstructions(file);
        		} catch (Exception e) {
        			setClArgumentSetToProtectProdRules(false);
        			return;
        		}
            	setClArgumentSetToProtectProdRules(true);

        	} else { 
           		
        		if(trace.getDebugCode("miss"))trace.out("miss", "WebStart: Loading the default instructions file");
        		WebStartFileDownloader ff = new WebStartFileDownloader();
        		String path = ff.findFile("instructions-9_initial.txt");
            	// Load the instructions from the file
                getBrController().setDeletePrFile(true);
                this.dealWithAnyExistingPrFile();
            	try {
        			getSimSt().loadInstructions(new File(path));
        		} catch (Exception e) {
        			setClArgumentSetToProtectProdRules(false);
        			return;
        		}
            	setClArgumentSetToProtectProdRules(true);
        	}
        }   
    } 
        
    /**
     * Initializes the prior knowledge by either reading the instructions from the file
     * or deserializing the simst.ser object 
     */
    public boolean loadInstDeSerialize() {
    	trace.err("Loading InstnDeSerialize");
    	if(trace.getDebugCode("miss"))trace.out("miss", "Inside loadInstnDeSerialize");
    	String fileName = "SimStDefault.ser";
    	if(getSimSt() != null && getSimSt().getUserID() != null) {
    		fileName = "simst-"+getSimSt().getUserID()+".ser";
    		if (runType.equalsIgnoreCase("springBoot")) {
    			fileName = "simSt.ser";
    		}
    	}
    	File serFile;
    	if (runType.equalsIgnoreCase("springBoot")) {
    		serFile = new File(getSimSt().getUserBundleDirectory(), fileName);
    	} else {
    		serFile = new File(getSimSt().getLogDirectory(), fileName);		
    	}
    	// Check if the application is running locally or using Webstart
    	if(!getSimSt().isWebStartMode()) { // Running locally
    		trace.err("Simstudent running locally...");
    		if(serFile != null && serFile.exists()) { // Load the simst-userid.ser file if it exists
    	    	getBrController().setDeletePrFile(true);
    	    	this.dealWithAnyExistingPrFile();
    	    	getSimSt().loadInstnDeSerialize(serFile);
    	    	setClArgumentSetToProtectProdRules(true);
    	    	return true;
    		} else if(serFile == null || !serFile.exists() || getSimSt() != null) { // Else load the simst.ser file if it exists
    			
    			fileName = "SimStDefault.ser";
    			serFile = new File(getSimSt().getProjectDir(), fileName);
    			
    			if(serFile != null && serFile.exists()) {

	        	getBrController().setDeletePrFile(true);
	        	this.dealWithAnyExistingPrFile();
	        	getSimSt().loadInstnDeSerialize(serFile);
	        	setClArgumentSetToProtectProdRules(true);    	
	        	return true;
    			}
    		}
    	} else { // Running using web-start
    		trace.err("Simstudent running from WebStart");
    		if(trace.getDebugCode("miss"))trace.out("miss", "Loading the instructions by deserializing using Webstart");
    		SimSt simStObj = null;
    		try { 
	    		// Key associated when retrieving the .ser file is simst-getSimSt().getUserID()+.ser
    			//trace.err("retrieving object... " + "simst-"+getSimSt().getUserID()+".ser");
    			simStObj = (SimSt) storageClient.retrieveObject("simst-"+getSimSt().getUserID()+".ser");
    			System.out.println(simStObj);
			} catch (IOException e) {
				//if(trace.getDebugCode("rr"))
					trace.err(e.getMessage());
			}
    		
				
        	if(simStObj != null) { // relevant file was able to be downloaded 
        		trace.err("Object found, resuming files");
                getBrController().setDeletePrFile(true);
                this.dealWithAnyExistingPrFile();
                getSimSt().loadInstnDeSerialize(simStObj);
            	setClArgumentSetToProtectProdRules(true);
            	return true;
        	} else { 
        		
        		WebStartFileDownloader ff = new WebStartFileDownloader();
        		String path = ff.findFile("SimStDefault.ser");
        		trace.err("Object not found, loading file " + path);
        		if(path != null) {
        			getBrController().setDeletePrFile(true);
        			this.dealWithAnyExistingPrFile();
        			getSimSt().loadInstnDeSerialize(new File(path));
        			setClArgumentSetToProtectProdRules(true);
        			return true;
        		} else
        			return false;
        	}
    	}
    	return false;
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Saving the Production-Rule files in a chronological order
    // 


    Integer whatToDoWithExistingPrFile = null;
    
    /**
     * This only gets called if a PR already exists.
     * 
     * @param brController a <code>BR_Controller</code> value
     * Gustavo 2 August 2007
     * returns the user's response to the dialog
     * @param brController a <code>BR_Controller</code> value
     */
    public int askWhatToDoWithExistingPrFile(File prFile, String prFileName) {

    	//@ROHAN - this should not be cached since the prFile is deleted automatically if it is not 
    	// the first time
        //this code caches the answer 
        //if (whatToDoWithExistingPrFile==null){ //if first time, ask

            int doBackup = -1;

//          if (prFile.exists()) {
            String title = "Backup " + prFileName + "...";

            String message[] = {
                    "There exists a production rule file (" + prFileName + ").",
                    "Would you like to save a copy of this file?",
                    "",
                    "  Yes: Save a copy & delete a current file",
                    "  No: Delete a current file",
            "  Cancel: Use a current file"};

            JFrame frame = getCtatLauncher().getActiveWindow();

            doBackup =
                JOptionPane.showConfirmDialog(frame, message, title,
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
            //CANCEL_OPTION case: leave the file

            whatToDoWithExistingPrFile = doBackup;
          //}                       
        //}
        return whatToDoWithExistingPrFile;
    }

    
    /**
     * This only gets called if a PR already exists.
     * 
     * @param brController a <code>BR_Controller</code> value
     * Gustavo 2 August 2007
     * returns the user's response to the dialog
     * @param brController a <code>BR_Controller</code> value
     */
    public int askToResumeTraining() {

            int doResume = -1;

            String title = "Resuming training...";

            String message[] = {
                    "Would you like to resume training using previously saved instructions?",
            			};
            doResume =
                JOptionPane.showConfirmDialog(null, message, title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        
        return doResume;
    }
    
    
    public String getNumProductionRules() {

	return getMissConsole().getNumProductionRules().getText();
    }

    /**
     * Describe <code>agePrFile</code> method here.
     *
     * @param pDir the ProjectDir where the tutor class file is in
     * @param ageDir the directory under pDir where the old production
     * rule file is preserved
     * @param prFileName a production rule file that is to be preserved
     * @param numSteps 
     * @param numRules 
     **/
    public void agePrFile(String pDir, String ageDir, String prFileName, int numRules, int numSteps) {

	// The current (old) production rule file
	File prFile = new File(pDir, prFileName);

	// The aged production rule file with # rules and # step
	// performed
	String ageFileName = prFileName + "-" + "R" + numRules + "S" + numSteps;
	String fullAgePath = pDir + "/" + ageDir;
	File ageFile = new File(fullAgePath, ageFileName).getAbsoluteFile();

	File fullAgePathFile = new File(fullAgePath).getAbsoluteFile();
	if (!fullAgePathFile.exists()) {
		if(trace.getDebugCode("miss"))trace.out("miss", "Directory " + fullAgePath + " created.");
	    fullAgePathFile.mkdirs();
	}
	if (ageFile.exists()) {
		if(trace.getDebugCode("miss"))trace.out("miss", "#### MissController.agePrFile: target file exists " + ageFile);
	    ageFile.delete();
	}
	
	boolean result = prFile.renameTo(ageFile);
	if (result) {
		if(trace.getDebugCode("miss"))trace.out("miss",
		       "File " + prFile + " has been preserved as " + ageFile);
	} else {
		if(trace.getDebugCode("miss"))trace.out("miss",
		       "!!" + prFile + " renaming failed (" + ageFile + ")");
	}
    }

    /**
     * Called by SimSt.checkProductionModel() 
     *
     * @return an <code>ArrayList</code> value
     */
    public ArrayList getMtRuleSeq() {
	return getBrController().getModelTracer().getMtRuleSeq();
    }

    public String getThisRuleName() {
	return getBrController().getModelTracer().getRete().getThisRuleName();
    }

    public Iterator getListActivations() {
	return getBrController().getModelTracer().getRete().listActivations();
    }

    /**
     * Triggered by Sim. St. Console to test a current production-rule
     * set.
     *
     **/
    public void testProductionModelOn() {
    	getSimSt().testProductionModelOn();
    }

    /**
     * Returns a rule name that gets fired lastly. 
     *
     * INCORRECT IMPLEMENTATION:
     * listActivations returns all activations on current agenda,
     * regardless of whether they have been fired. Hence,
     * last activation in list isn't necessarily last fired one.
     *
     * @return a <code>String</code> value
     */
    //OBSOLETE - is not used
   // public String getMTRuleFired() {
//	return null;
    /*	
    String ruleName = null;

	edu.cmu.pact.jess.MTRete rete = getBrController().getModelTracer().getRete();
	Iterator activations = rete.listActivations();
	Activation activation = null;
	while (activations != null && activations.hasNext()) {
	    activation = (Activation)activations.next();
	    System.out.println("Rule: " + activation.getRule().toString().split("::")[1]
	                       + " Salience: " + activation.getSalience() +
	                       " Active? " + activation.isInactive());
	}
	if (activation != null) {
	    Defrule rule = activation.getRule();
	    // rule -> "MAIN::foobar"
	    ruleName = rule.toString().split("::")[1];
	}
	System.out.println("Last rule: " + ruleName);
	
	// alt
	ArrayList fired = rete.getFiredRuleList();
	if (fired == null) {
		System.out.println("No fired rule list");
		return ruleName;
	}
	ListIterator iter = fired.listIterator();
	while(iter.hasNext()) {
		System.out.println(iter.next());
	}
	
	return ruleName;
	*/
    	
    	/*
    	edu.cmu.pact.jess.MT mt = getBrController().getModelTracer();
    	ArrayList ruleSeq = mt.getMtRuleSeq();
    	if (ruleSeq == null) {
    		System.out.println("****** No rule seq");
    		return null;
    	}
    	ListIterator iter = ruleSeq.listIterator();
    	while (iter.hasNext()) {
    		System.out.println("Rule: " + iter.next().toString());
    	}
    	//find last rule in sequence
    	if (ruleSeq.size() > 0){
    		String lastRuleName = ruleSeq.get(ruleSeq.size()-1).toString();
    		System.out.println("Last rule: " + lastRuleName);
    		return lastRuleName;
    	}
    	return null;
  //  	*/
  //  } 


    public void resetMT() {
	try {
	    getBrController().getModelTracer().getRete().reset();
	} catch (JessException e) {
	    e.printStackTrace();
		logger.simStLogException(e);
	}
    }

    public int runMT() {

	try {
	    return getBrController().getModelTracer().getRete().run();
	} catch (JessException e) {
	    e.printStackTrace();
		logger.simStLogException(e);
	}
	return -1;
    }

    /*
    Iterator activations = getMissController().getListActivations();
    while (activations.hasNext()) {
	
	Activation activation = (Activation)activations.next();
    }
    */
    
    public void reloadProductionRulesFile(File prFile) {
	getBrController().reloadProductionRulesFile(prFile);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Directory management
    // 

    // File Mode for the File Chooser
    final static int OPEN = 1;
    final static int SAVE = 2;

    /**
     * Triggered by a file menu in the Miss Console.  Have the Miss
     * Console launch a File Chooser and let the user select a file
     *
     * @return a name of the file selected
     **/
    File chooseFileByDialog(int mode) {

	File theFile = null;

	int returnVal = (mode == OPEN) ?
	    fileChooserShowOpenDialog() :
	    fileChooserShowSaveDialog();

	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    theFile = fileChooserGetSelectedFile();

	}
	return theFile;
    }

    /**
     * Controlling a File Chooser
     *
     */
    public int fileChooserShowOpenDialog() {
	return getFileChooser().showOpenDialog(getMissConsole());
    }
    public int fileChooserShowSaveDialog() {
	return getFileChooser().showSaveDialog(getMissConsole());
    }
    public File fileChooserGetSelectedFile() {
	String path = fileChooserGetCurrentDir();
	PreferencesModel pm = getPreferencesModel();
	pm.setStringValue("Miss FileChooser Path", path);
	pm.saveToDisk();
	trace.out("FileChooserPath: " + path);
	return getFileChooser().getSelectedFile();
    }
    public String fileChooserGetCurrentDir() {
	return getFileChooser().getCurrentDirectory().getPath();
    }
    public void fileChooserSetCurrentDir(String dir) {
	getFileChooser().setCurrentDirectory(new File(dir));
    }

    /**
     * Returns the preference model maintained in the BR_Controller
     *
     * @return a <code>PreferencesModel</code> value
     */
    public PreferencesModel getPreferencesModel() {
	return getBrController().getPreferencesModel();
    }

    /**
     * Returns a directory name where the Project files are located
     *
     */
    public String getProjectDir() {
	//Borg... this should work exactly the same as it did before.
    String path = getBrController().getHomeDir();
	File file = Utils.getFileAsResource(path, this);
	return file.getAbsolutePath().replace('\\','/');
    //return "";
    }
    
    // ---------------------------------------------------------------------------
    // Peer Learning Environment
    // 
    
    // Called by SimStPLEActionListener when the [Tutor SimSt Next Problem] button is clicked
    public void pleNextProblem() {
        getSimStPLE().nextProblem(true);
    }

    public void pleQuizSimSt() {
        getSimStPLE().quizSimSt();
    }
    
    public void pleCurriculumBrowser() {
    	getSimStPLE().showCurriculumBrowser();
    }
    
    public void pleExamplesSimSt() {
        getSimStPLE().examples();
    }
    
    public void pleUndoSimSt() {
    	getSimStPLE().undo();
    }
    public void pleRestartProblemSimSt() {
    	getSimStPLE().restartProblem();
    }
    

    // ------------------------------------------------------------
    // Misc helpers

    // Tracing for Debugging 
    void trace(String message) {
    	if(trace.getDebugCode("miss"))trace.out("miss", message);
    }
    
    /**
     * For a test use; Activate a Simulated Student
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(final String[] args) {

        MissController controller = new MissController(new BR_Controller());
        controller.activate(true);
    }
    
    public void setSsInteractiveLearning(boolean flag) {
        getSimSt().setIsInteractiveLearning(flag);
    	getSimSt().setIsInteractiveLearningFlag(flag);
    }
    public void setSsNonInteractiveLearning(boolean flag) {
    	getSimSt().setIsNonInteractiveLearning(flag);
    }
    public void setSsInteractiveLearningFlag(boolean flag) {
    	getSimSt().setIsInteractiveLearningFlag(flag);
    }
    public void setSsNonInteractiveLearningFlag(boolean flag) {
    	getSimSt().setIsNonInteractiveLearningFlag(flag);
    }
    
    public void setSsCacheOracleInquiry(boolean flag) {
        getSimSt().setSsCacheOracleInquiry(flag);
    }
    
    /**
     * Instantiate the proper wrapper class for the student interface.
     * @param simStPleOnArg
     * @param simStPleOnArg
     * @param controller
     * @return {@link GameShowWrapper}, {@link SimStPLEWrapper} or {@link TutorWrapper} instance
     */
    //nbarba 01/16/2014: arguments need to be renamed as old name is actually a local variable
	public TutorWrapper createWrapper(boolean simStPleOnArg, boolean ssContestArg, TutorController controller) {
		TutorWrapper wrapper = null;
		if (simStPleOnArg)
			wrapper = new SimStPLEWrapper((BR_Controller) controller);
		else
			wrapper = new TutorWrapper(controller);
        if (ssContestArg)
        	wrapper = new GameShowWrapper((BR_Controller) controller);
        return wrapper;
	}
	
	/**
	 * Call {@link SimStPLE#requestEnterNewProblem()}.
	 */
	public void requestEnterNewProblem() {
		getSimStPLE().requestEnterNewProblem();
	}
	
	/**
	 * Call {@link SimStPLE#checkCompletedStartState()}.
	 */
	public void checkCompletedStartState() {
		getSimStPLE().checkCompletedStartState();
	}
	
	/**
	 * @return {@link SimSt#isFoaGetterDefined()}
	 */
	public boolean isFoaGetterDefined() {
		return getSimSt().isFoaGetterDefined();
	}

	/**
	 * @return {@link SimSt#isFoaSearch()}
	 */
	public boolean isFoaSearch() {
		return getSimSt().isFoaSearch();
	}
	
	/**
	 * Call {@link SimStPLE#setTitle(JFrame)}.
	 * @param activeWindow 
	 */
	public void setTitle(JFrame activeWindow) {
		getSimStPLE().setTitle(activeWindow);
	}
	
	private CTAT_Launcher getCtatLauncher() {
		if(ctatLauncher instanceof CTAT_Launcher)
			return (CTAT_Launcher) ctatLauncher;
		else if(Utils.isRuntime())
			return null;
		try {
			throw new IllegalStateException("MissController.getServer() called with ctatLauncher "+ctatLauncher);			
		} catch(Exception e) {
			trace.errStack("Programming error: "+e, e);
			return null;
		}
	}
	
	 // nbarba 01/16/2014: function exists also in SingleSessionLaungher, also needed here as SimStudent arguments are parsed here now. Need to have it in only one place...
	 private String[] getArgvParameter(String argv[], int keyIndex) {

	        ArrayList<String> paramList = new ArrayList<String>();
	        for (int i = keyIndex + 1; i < argv.length; i++) {

	        	if (argv[i].length() < 1)
	        		continue;
	            if (argv[i].charAt(0) == '-') {
	                break;
	            }

	            paramList.add(argv[i]);

	        }

	        // Returns null if no parameter is present
	        String[] getArgvParameter = null;
	        if (!paramList.isEmpty()) {

	            getArgvParameter = new String[paramList.size()];
	            for (int i = 0; i < paramList.size(); i++) {
	                getArgvParameter[i] = paramList.get(i);
	            }
	        }

	        return getArgvParameter;
	    }
	 
	 
	public void parseArgv(String[] argv) {
	
		
		
		
        for (int i = 0; i < argv.length; i++) {

        	if(trace.getDebugCode("miss"))trace.out("miss","argv[" + i + "] = " + argv[i]);

            String key = argv[i];
            if(key.length() < 1)
            	continue;
   
            if (key.charAt(0) == '-') {
            	try {
            	    // Strip off the '-' at the beginning
                    String keyStem = key.substring(1);
                    
                    if(trace.getDebugCode("miss"))trace.out("miss", "keyStem=" + keyStem);
                    
                    // "parameters" that follow the key
                    String[] parameter = getArgvParameter(argv, i);

                    // If you need to add more switches, simply follow the
                    // convention below. Try to put just a single method
                    // call for each switch

                    if (!keyStem.startsWith("ss")) {
                    	continue;
                        
                    } else if(keyStem.equalsIgnoreCase("ssMetaTutorMode")){
                    	setSsMetaTutorMode(true);
                    
                    } 
                    else if(keyStem.equalsIgnoreCase("ssCogTutorMode")){
                    	 setSsMetaTutorMode(true);
                    	 setSsCogTutorMode(true);
                    
                    } 
                    else if(keyStem.equalsIgnoreCase("ssAplusCtrlCogTutorMode")){
                    	 setSsMetaTutorMode(true);
                    	 setSsAplusCogTutorMode(true);
                   
                   } 
                    else if(keyStem.equalsIgnoreCase("ssMetaTutorModeLevel")){
                    	setSsMetaTutorModeLevel(parameter[0]);           
                    } 
                    else if (keyStem.equalsIgnoreCase("ssWebAuthoringMode")) {
                    	setSsWebAuthoringMode(true);
                    
                    } 
                    else if(keyStem.equalsIgnoreCase("ssQuizProblemAbstractor")) {
                    	setSsQuizProblemAbstractorClass(parameter[0]);
            		
            		} else if (keyStem.equalsIgnoreCase("ssBatchMode")) {
                    	setSsBatchMode(true);
                    } else if(keyStem.equalsIgnoreCase("ssFixedLearningMode")) {
                    	
                    	setSsFixedLearningMode(true);
                    } else if (keyStem.equalsIgnoreCase("ssContest")) {
                      	setSsContest(true);
                    } else if (keyStem.equalsIgnoreCase("ssInteractiveLearning")) {
                        setSsInteractiveLearning(true);
//                  } else if (keyStem.equalsIgnoreCase("ssILBatchMode")) {
//                        setSsIlBatchMode(true);
                    } else if (keyStem.equalsIgnoreCase("ssNonInteractiveLearning")) {
                    	setSsNonInteractiveLearning(true);
                    } else if (keyStem.equalsIgnoreCase("ssIlSignalNegative")) {
                        setSsIlSignalNegative(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssIlSignalPositive")) {
                        setSsIlSignalPositive(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssCondition")) {
                        setSsCondition(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssConstraintFile")) {
                        setSSConstraintFile(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssDecomposerFile")) {
                        setSSDecomposerFile(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssDecomposeInput")) {
                        ssUseDecomposition();

                    } else if (keyStem.equalsIgnoreCase("ssDoNotSaveRules")) {
                        ssDoNotSaveRules();

//                    } else if (keyStem.equalsIgnoreCase("ssExternalRuleActivationTest")) {
//                        ssUseExternalRuleActivationTest();
                        
                    } else if (keyStem.equalsIgnoreCase("ssFeaturePredicateFile")) {
                        setSsFeaturePredicateFile(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssFoaGetterClass")) {
                        setSsFoaGetterClass(parameter[0]);
                    
                    }      
                    else if (keyStem.equalsIgnoreCase("ssDeletePrFile")){
                    	getBrController().setDeletePrFile(true);
                    }
                    else if (keyStem.equalsIgnoreCase("ssProblemAccessorClass")) {
                    	setSsProlbemAssesorClass(parameter[0]);
                    
                    } 
                    else if (keyStem.equalsIgnoreCase("ssSelectionOrderGetterClass")) {
                        setSsSelectionOrderGetterClass(parameter[0]);
                    
                    } else if(keyStem.equalsIgnoreCase("ssClSolverTutorSAIConverter")) {
                    	setSsSaiConverterClass(parameter[0]);
                    
                    } else if(keyStem.equalsIgnoreCase("ssInterfaceElementGetterClass")) {
                    	setSsInterfaceElementGetterClass(parameter[0]);
                    
                    } else if(keyStem.equalsIgnoreCase("ssFoaClickDisabled")) {
                    	setSsFoaClickDisabled(true);
                    
                    } else if (keyStem.equalsIgnoreCase("ssInputCheckerClass")) {
                    	
                        setSsInputCheckerClass(parameter[0]);
                    }
                    else if (keyStem.equalsIgnoreCase("ssStartStateCheckerClass")) {
                    	
                        setSsStartStateCheckerClass(parameter[0]);
                    }
                    /* @author: jinyul, skillNameGetter is an ad-hoc method to identify
                     * skill name based on current selection. */
                    else if (keyStem.equalsIgnoreCase("ssSkillNameGetterClass")) {
                    	setSsSkillNameGetterClass(parameter[0]);
                    } 
                    /*@author: Tasmia, 
                     * nearSimilarProblemsGetter is an method that returns similar looking problems
                     * that are in edit distance 1 from the current problem
                     * */
                    /*else if (keyStem.equalsIgnoreCase("ssNearSimilarProblemsGetterClass")) {
                    	setSsNearSimilarProblemsGetterClass(parameter[0]);
                    }*/
                    /*else if (keyStem.equalsIgnoreCase("ssBothAgreeSpeechGetterClass")) {
                    	setSsBothAgreeSpeechGetterClass(parameter[0]);
                    }*/
                    else if (keyStem.equalsIgnoreCase("ssFoaSearch")) {
                        setSsFoaSearch(true);
                        
                    } else if (keyStem.equalsIgnoreCase("ssPathOrderingClass")) {
                        setSsPathOrderingClass(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssCacheOracleInquiry")) {
                        setSsCacheOracleInquiry(parameter[0]);

                    } else if(keyStem.equalsIgnoreCase("ssHeuristicBasedIDS")) {
                    	setSsHeuristicBasedIDS(true);
                    }
      
                    else if (keyStem.equalsIgnoreCase("ssPreservePrFile")) {
                        setSsPreservePrFile(true);

                    } else if (keyStem.equalsIgnoreCase("ssDeletePrFile")) {
                        setSsDeletePrFile(true);
                    
                    } 
                    else if (keyStem.equalsIgnoreCase("ssQuizGradingMethod")) {
                        setSsQuizGradingMethod(parameter[0]);

                    }else if (keyStem.equalsIgnoreCase("ssRuleActivationTestMethod")) {
                        setSsRuleActivationTestMethod(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssHintMethod")) {
                        setSsHintMethod(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssFoilLogDir")) {
                        setSsFoilLogDir(parameter[0]);

                    } 
                    else if (keyStem.equalsIgnoreCase("ssFoilMaxTuples")) {
                        setSsFoilMaxTuples(parameter[0]);

                    }
                    else if (keyStem.equalsIgnoreCase("ss2014FractionAdditionAdhoc")) {
                        setSs2014FractionAdditionAdhoc(true);

                    }
                    else if (keyStem.equalsIgnoreCase("ssForceToUpdateModel")) {
                    	setSsForceToUpdateModel(true);

                    } else if (keyStem.equalsIgnoreCase("ssInputMatcher")) {
                        setSsInputMatcher(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssTypeChecker")) {
                        setSsTypeChecker(parameter[0]);

                    } 
                    else if (keyStem.equalsIgnoreCase("ssValidSkillChecker")) {
                        setSsValidSkillChecker(parameter[0]);

                    } 
                    else if (keyStem.equalsIgnoreCase("ssLearningLogFile")) {
                    	setSsLearningLogFile(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssLearnCorrectActions")) {
                    	setSsLearnCorrectActions(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssLearnCltErrorActions")) {
                    	setSsLearnCltErrorActions(parameter[0]);
                    	
                    } else if (keyStem.equalsIgnoreCase("ssLearnBuggyActions")) {
                    	setSsLearnBuggyActions(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssLearningRuleFiringLogged")) {
                        ssLearningRuleFiringLogged();

                    } else if (keyStem.equalsIgnoreCase("ssMemoryWindowOverRules")) {
                    	setSsMemoryWindowOverRules(true);

                    } else if (keyStem.equalsIgnoreCase("ssNumBadInputRetries")) {
                    	setSsNumBadInputRetries(parameter[0]);
                    } else if (keyStem.equalsIgnoreCase("ssNoOpCache")) {
                	// Sat Nov  4 16:07:26 EST 2006 :: Noboru
                	// This has actually no effect.  
                	// Because the _instance_ of FeaturePredicate (i.e., a RHS operator)
                	// must be created and stored anyways.
                	// What really makes sense is to limit the capacity of cache,
                	// which is implemented as -ssCacheCapacity 
                        setSsOpCached(false);
                        
                    } else if (keyStem.equalsIgnoreCase("ssSetFpCacheCapacity")) {
                	setSsFpCacheCapacity(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssNumIteration")) {
                        setSsValidationNumIteration(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssMaxSearchDepth")) {
                        setSsMaxSearchDepth(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssOperatorFile")) {
                        setSsOperatorFile(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssProjectDirectory")) {
                        setSsProjectDir(parameter[0]);
                    } 
                    else if(keyStem.equalsIgnoreCase("ssUserBundleDir")) {
                    	// @author raj
                    	// this flag will allow us to set the user bundle space
                    	setSsUserBundleDir(parameter[0]);
                    }
                    else if (keyStem.equalsIgnoreCase("ssPackageName")) {
                    	setSsPackageName(parameter[0]);
                    } 
                    else if (keyStem.equalsIgnoreCase("ssJessOracleProductionFile")) {
                    	setSsJessOracleProductionFile(parameter[0]);
                    } 
                    else if (keyStem.equalsIgnoreCase("ssValidationMethod")) {
                    	setSsPrValidationMethod(parameter[0]);
                    } 
                    else if (keyStem.equalsIgnoreCase("ssModelTracingValidationOutcome")) {
                    	setSsModelTracingValidationOutcome(parameter[0]);
                    } 
                    else if (keyStem.equalsIgnoreCase("ssPrAgeDir")) {
                        setSsPrAgeDir(parameter[0]);
                    	
                    } else if (keyStem.equalsIgnoreCase("ssProblemSet")) {
                        setSsValidationProblemSet(parameter);
                        
                    } else if (keyStem.equalsIgnoreCase("ssRunValidation")) {
                        setSsRunValidation(true);

                    } else if (keyStem.equalsIgnoreCase("ssVerifyNumFoA")) {
                    	setSsVerifyNumFoA(true);
                     
                    } else if (keyStem.equalsIgnoreCase("ssShuffleValidation")) {
                        setSsShuffleRunMode(true);
                        
                    } else if (keyStem.equalsIgnoreCase("ssTestSet")) {
                        setSsValidationTestSet(parameter);

                    } else if (keyStem.equalsIgnoreCase("ssTestOutput")) {
                        setSsValidationOutput(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssUserDefSymbols")) {
                        setSsUserDefSymbols(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssPredictObservableActionName")) {
                        setSsPredictObservableActionName(parameter[0]);                        
                        
                    } else if (keyStem.equalsIgnoreCase("ssSetMemoryWindowSize")) {
                    	setSsMemoryWindowSize(parameter[0]);
                	
                    } else if (keyStem.equalsIgnoreCase("ssSearchTimeOutDuration")) {
                        ssSearchTimeOutDuration(parameter[0]);

                    } else if (keyStem.equalsIgnoreCase("ssTutorServerTimeOutDuration")) {
                        ssTutorServerTimeOutDuration(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssSetInactiveInterfaceTimeout")) {
                        ssInactiveInterfaceTimeOutDuration(parameter[0]);

                    }else if (keyStem.equalsIgnoreCase("ssNoAutoOrderFOA")) {
                        ssAutoOrderFOAOff();
                        
                    } else if (keyStem.equalsIgnoreCase("ssLearnNoLabel")) {
                    	if (trace.getDebugCode("ss")) trace.out("ss", "Setting ssLearnNoLabel in SingleSessionLauncher");
                        setSsLearnNoLabel();
                        
                    } else if (keyStem.equalsIgnoreCase("ssLogAgendaRuleFiring")) {
                        ssLogAgendaRuleFiring();
                        
                    } else if (keyStem.equalsIgnoreCase("ssLogPriorRuleActivationsOnTraining")) {
                        ssLogPriorRuleActivationsOnTraining();
                        
                    } else if (keyStem.equalsIgnoreCase("ssTestOnLastTrainingOnly")) {
                    	ssTestOnLastTrainingOnly();
                        
                    } else if (keyStem.equalsIgnoreCase("ssCheckWilkinsburgBadBrd")) {
                    	ssCheckWilkinsburgBadBrd();
                	
                    } else if (keyStem.equalsIgnoreCase("ssSetMaxNumTraining")) {
                    	ssSetMaxNumTraining(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssSetMaxNumTest")) {
                    	ssSetMaxNumTest(parameter[0]);
                	
                    } else if (keyStem.equalsIgnoreCase("ssSwitchLearningStrategyAfter")) {
                        ssSwitchLearningStrategyAfter(parameter[0]);
                        
                    } else if (keyStem.equalsIgnoreCase("ssTestProductionModelNoTest")) {
                    	ssSetTestProductionModelNoTest(true);
                	
                    } else if (keyStem.equalsIgnoreCase("ssAnalysisOfFitnessWilkinsburg")) {
                    	ssAnalysisOfFitnessWilkinsburg();
                	
                    } else if (keyStem.equalsIgnoreCase("ssLogStudentsLearning")) {
                    	ssLogStudentsLearning();
                        
                    } else if (keyStem.equalsIgnoreCase("ssValidateStepsInBRD")) {
                        setSsValidateStepsInBRD();
                	
                    } else if (keyStem.equalsIgnoreCase("ssDontShowAllRaWhenTutored")) {
                    	ssDontShowAllRaWhenTutored();
                    
                    } else if (keyStem.equalsIgnoreCase("ssClSolverTutorHost")) {
                    	ssSetClSolverTutorHost(parameter[0]);
                    	
                    } else if (keyStem.equalsIgnoreCase("ssRunInPLE")) {
                        setSimStPleOn(true);
                    }else if (keyStem.equalsIgnoreCase("ssNoLogging")) {
                        setSimStLogging(false);
                        setSimStLocalLogging(false);
                    } else if (keyStem.equalsIgnoreCase("ssLogging")) {
                        setSimStLogging(true);
                    } else if (keyStem.equalsIgnoreCase("ssLocalLogging")) {
                        setSimStLocalLogging(true);
                    } else if (keyStem.equalsIgnoreCase("ssDummyContest")) {
                        setSimStDummyContest(true);
                    } else if (keyStem.equalsIgnoreCase("ssContestServer")) {
                        setSimStContestServer(parameter[0]);
                    } else if (keyStem.equalsIgnoreCase("ssContestPort")) {
                        setSimStContestPort(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssUserID")) {
                    	setSimStUserId(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssSimStName")) {
                    	setSsSimStName(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssSimStImage")) {
                    	setSsSimStImage(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssProblemsPerQuizSection")) {
                    	setSimStProblemsPerQuizSection(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssLogURL")) {
                    	setSimStLogURL(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssCLQuizReqMode")) {
                    	setSsCLQuizReqMode();
                    } else if(keyStem.equalsIgnoreCase("ssSelfExplainMode")) {
                    	setSsSelfExplainMode();
                    } else if(keyStem.equalsIgnoreCase("ssConstructiveTuteeInquiryFTIMode")) {
                    	setSsConstructiveTuteeInquiryFTIMode();
                    } else if(keyStem.equalsIgnoreCase("ssIntroVideo")) {
                    	setSsIntroVideo(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssLoadPrefsFile")) {
                    	setSsPrefsFile(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssOverviewPage")) {
                    	setSsOverview(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssActivationList")) {
                    	setSsActivationList(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssTutalkParams")) {
                    	// @author Huan Truong
                    	// This flag enables Tutalk dialog mode and sets server parameters
                    	// Param: flag := experimenter@server[:[opt1,opt2,...,optn]]
                    	setSsTutalkParams(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssCTIBothStuckParams")) {
                    	// @author Tasmia Shahriar
                    	// This flag enables Tutalk dialog mode and sets server parameters
                    	setCTIBothStuckParams(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssGeneralWMEPaths")){
                    	// @author samanz  
                    	// This flag will switch simstudent to not learn any specific wmepaths
                    	// Which will create rules to only have multivariable WME path selectors
                    	setSsGeneralWmePaths();
                    } else if (keyStem.equalsIgnoreCase("ssTypeMatcher")){
                    	 setSsTypeMatcher(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssLogFolder")){
                    	setSsLogFolder(parameter[0]);
                    } else if(keyStem.equalsIgnoreCase("ssProblemCheckerOracle")) {
                    	setSsProblemCheckerOracle(parameter[0]);
                    }
                    else {	
                        throw new IllegalArgumentException ("Unknown SimStudent command line argument: " + keyStem);
                    }
                } catch (IllegalArgumentException iae) {
                    trace.err("Error on command line argument " + i + " '"
                            + key + "': " + iae);
                }
            }
        }
    }
	/**
	 * @author Vishnu Priya 
	 * @param string
	 * To set the oracle for problem Checker
	 */
    private void setSsProblemCheckerOracle(String oracleClass) {
		// TODO Auto-generated method stub
		getSimSt().setProblemCheckerOracle(oracleClass);
	}

	private void setSsLogFolder(String logFolder) {
		// TODO Auto-generated method stub
    	//@author Vishnu Priya Chandra Sekar
    	// enable to specify the log folder structure
		trace.out("miss", "Setting the log folder :  " +logFolder);

    	PreferencesModel pm = getBrController().getPreferencesModel();
    	pm.setStringValue(BR_Controller.DISK_LOGGING_DIR, logFolder);
	}
	
	private void setSsUserBundleDir (String userBundleDir) {
		trace.out("miss", "Setting the user bundle directory :  " + userBundleDir);
		File directory = new File(userBundleDir);
		if (!directory.exists()) {
			directory.mkdir();
		}
		this.getSimSt().setUserBundleDirectory(userBundleDir);
	}

	// Thu Oct 27 15:20:09 2005:: Noboru
    //
    // Run a validation test for Sim. Student to validate production rules
    // against a test problems
    private String[] ssValidationProblemSet;

    private void setSsValidationProblemSet(String[] problemSet) {
        this.ssValidationProblemSet = problemSet;
    }

    String[] getSsValidationProblemSet() {
        return this.ssValidationProblemSet;
    }

    private String[] ssValidationTestSet;

    private void setSsValidationTestSet(String[] testSet) {
        this.ssValidationTestSet = testSet;
    }

    public /*private*/ String[] getSsValidationTestSet() {
        return this.ssValidationTestSet;
    }

    private String ssValidationOutput;

    private void setSsValidationOutput(String validationOutput) {
        this.ssValidationOutput = validationOutput;
    }

    public /*private*/ String getSsValidationOutput() {
        return this.ssValidationOutput;
    }

    private int ssValidationNumIteration;

    private int getSsValidationNumIteration() {
        return this.ssValidationNumIteration;
    }

    private void setSsValidationNumIteration(String n) {
        this.ssValidationNumIteration = Integer.parseInt(n);
    }

    
//    //a flag to have SimSt run in interactive-learning batch mode
//    private boolean ssIlBatchMode = false;
//
//    private boolean isSsIlBatchMode() {
//        return this.ssIlBatchMode;
//    }

    
    /** A flag to have Simulated Student run in a batch mode */
    private boolean ssBatchMode = false;

    public boolean isSsBatchMode() {
        return this.ssBatchMode;
    }

   

    /** A flag to indicate that the Simulated Student should not turn off Interactive or Non-Interactive Learning
     *  Used in conjunction with batch mode to prevent resetting IL/NonIL after completing a problem when
     *  creating a start state  */
    private boolean ssFixedLearningMode = false;
    
    public boolean isSsFixedLearningMode() {
		return ssFixedLearningMode;
	}


	private boolean ssContest = false;

    public boolean isSsContest() {
    	return this.ssContest;
    }
    
     private void setSsContest(boolean flag) {
    	this.ssContest = flag;
    }
   


    private void ssDontShowAllRaWhenTutored() {
    	getSimSt().setDontShowAllRA(true);
    }
    
    private void setSsIlSignalNegative(String flag) {
        getSimSt().setIlSignalNegative(new Boolean(flag));
    }
    
    private void setSsIlSignalPositive(String flag) {
       getSimSt().setIlSignalPositive(new Boolean(flag));
    }
    
	private void ssSetClSolverTutorHost(String clSolverTutorHost) {
		getSimSt().setClSolverTutorHost(clSolverTutorHost);
	}
	
    // A flag to have Sim. St. run in a shuffle validation mode
    private boolean ssShuffleRunMode = false;

    // A flag to have SimSt carry out Analysis of Fitness on the Wilkinsburg 
    // data. Number of training and test problems must be specified
    // by ssSetMaxNumTest and ssSetMaxNumTraining
    private boolean ssAnalysisOfFitnessWilkinsburg = false;

    private boolean isSsShuffleRunMode() {
        return this.ssShuffleRunMode;
    }

    private void setSsShuffleRunMode(boolean flag) {
        this.ssShuffleRunMode = flag;
    }

    private boolean ssRunValidation = false;  
    
    public boolean isSsRunValidation() {
        return this.ssRunValidation;
    }

    private void setSsRunValidation(boolean flag) {
        this.ssRunValidation = flag;
    }
    
    private void setSsTypeMatcher (String typeMatcher){
    	getSimSt().setTypeMatcher(typeMatcher);	
    }
    
    private void setSsInputMatcher(String inputMatcher) {
        getSimSt().setInputMatcher(inputMatcher);
    }

    private void setSsTypeChecker(String typeChecker) {
    	//setTypeChecker(typeChecker);
        SimSt.setTypeChecker(typeChecker);     
    }
    
    private void setSsValidSkillChecker(String validSkillChecker) {
        getSimSt().setSsValidSkillChecker(validSkillChecker);     
    }
    
    
    
    private void ssLogAgendaRuleFiring() {
    	getSimSt().setLogAgendaRuleFiring(true);
    }
    
    private void ssLogPriorRuleActivationsOnTraining() {
        getSimSt().setLogPriorRuleActivationsOnTraining(true);
    }

    private void ssLearningRuleFiringLogged() {
        getSimSt().setLearningRuleFiringLogged(true);
    }

    // Number of training & test problems must be set with ssSetMaxNumTest()
    // and ssSetMaxNumTraining()
    private void ssAnalysisOfFitnessWilkinsburg() {
	this.ssAnalysisOfFitnessWilkinsburg  = true;
    }
    
    /**
     * Flag to run SimSt.validateStepsInBRD()
     */
    private boolean ssValidateStepsInBRD = false;
    private void setSsValidateStepsInBRD() {
        this.ssValidateStepsInBRD = true;
    }
    private void runSsValidateStepsInBRD() {
        SimSt simSt = getSimSt();
        simSt.validateStepsInBRD(getSsValidationTestSet()[0], getSsValidationOutput());
    }

    
    // Commented out 07/19/2013 (syyang) - these seem to be unused.
    /*
    private String ssValidateStepsInBrdFileName = "";
    private String getSsValidateStepsInBrdFileName() {
        return ssValidateStepsInBrdFileName;
    }
    private void setSsValidateStepsInBrdFileName(String brdFileName) {
        this.ssValidateStepsInBrdFileName = brdFileName;
    }
    */

    /**
     *  A flag to have SimSt only output real students' learning log
     */
    private boolean ssLogStudentsLearning = false;
    
    private void ssLogStudentsLearning() {
	this.ssLogStudentsLearning = true;
    }

    private void ssCheckWilkinsburgBadBrd() {
    	getSimSt().setCheckWilkinsburgBadBrdFile(true);
    }

    /**
     * set the file SimStudent reads it's decomposers from
     * @param fileName
     */
    private void setSSDecomposerFile(String fileName) {
    	getSimSt().setDecomposerFile(fileName);
    }

    /**
     * set the file SimStudent reads its topological constraints from
     * @param fileName
     */
    private void setSSConstraintFile(String fileName) {
        getSimSt().setConstraintFile(fileName);
    }

    /**
     * disable SimStudent's generating Production rule files
     *
     */
    private void ssDoNotSaveRules() {
    	getSimSt().setArchivingProductionRules(false);
    }
    
    /*
    private void ssUseExternalRuleActivationTest() {
        getMissController().getSimSt().setUseExternalRuleActivationTest(true);
    }
    */

    private void ssSwitchLearningStrategyAfter(String num) {
        getSimSt().setSwitchLearningStrategy(true);
        getSimSt().setSwitchLearningStrategyAfter(Integer.parseInt(num));
    }
    
    private void ssSetMaxNumTest(String num) {
        getSimSt().setMaxNumTest(Integer.parseInt(num));
    }

    private void ssSetMaxNumTraining(String num) {
    	getSimSt().setMaxNumTraining(Integer.parseInt(num));
    }
    
    private void setSsFpCacheCapacity(String num) {
    	getSimSt().setFpCacheCapacty(Integer.parseInt(num));
    }

    private void ssSetTestProductionModelNoTest(boolean b) {
    	getSimSt().setTestProductionModelNoTest(b);
    }
    
    private void ssTestOnLastTrainingOnly() {
    	getSimSt().setTestOnLastTrainingOnly(true);
    }

    /**
     * turn SimStudent's auto ordering of focus of attention based of value off
     *
     */
    private void ssAutoOrderFOAOff()
    {
       getSimSt().setAutoOrderFOA(false);
    }

   

    /**
     * Have SimStudent learn skills without labels
     */
    private void setSsLearnNoLabel() {
        getSimSt().setSsLearnNoLabel(true);
    }

    /**
     * Given a set of test problems, have Sim. St. run a validation test on the
     * current production rules (i.e., productionRules.pr)
     * 
     */
    public void runSimStValidationTest() {		
    	getSimSt().testProductionModelOn(getSsValidationTestSet(), getSsValidationOutput());
    }

    private void runSimStShuffleMode() {

        ssShuffleRunInBatch(getSsValidationProblemSet(),
                getSsValidationTestSet(), getSsValidationOutput(),
                getSsValidationNumIteration());
    }

    private void runSsAnalysisOfFitnessWilkinsburg() {
		SimSt simSt = getSimSt();
		simSt.analysisOfFitnessWilkinsburg(getSsValidationProblemSet()[0], getSsValidationOutput());
    }

    private void runSsLogStudentsLearning() {
		SimSt simSt = getSimSt();
		simSt.ssLogStudentsLearning(getSsValidationProblemSet()[0], getSsValidationOutput());
    }
    
    // Set FoilBase for a FoilData object to specify a directory to find
    // foil6.exe

    // Given a set of training and test problems as well as an output
    // file to report the results of validation, have Sim. St. run a rule
    // generation with the training problems and run a validation test
    // against the test problems.
   public void runSimStInBatchMode() {
        runSimStInBatchMode(getSsValidationProblemSet(),
                getSsValidationTestSet(), getSsValidationOutput());
    }

   

    
//    private void runSimStInILBatchMode() {
//
//        getMissController().runSimStInILBatchMode(getSsValidationProblemSet(),
//                getSsValidationTestSet(), getSsValidationOutput());
//    }

    
    // Tell Sim. St. whether the evaluation of RHS operators should be
    // cached (default) or not
    private void setSsOpCached(boolean flag) {
        setOpCached(flag);
    }

    private void setSsProjectDir(String projectDir) {
    	if (projectDir.startsWith("SimStAlgebra")){
    		projectDir = System.getProperty("user.home") + System.getProperty("file.separator") +
    		"Public" + System.getProperty("file.separator") + projectDir;
    		SimSt.setProjectDir(projectDir);
    		this.setProjectDir(projectDir);
    	} else {
    		SimSt.setProjectDir(projectDir);
    		this.setProjectDir(projectDir);
    		this.getSimSt().setProjectDirectory(projectDir);
    	}
    }

    private void setSsFoilLogDir(String foilLogDir) {
        setFoilLogDir(foilLogDir);
    }
    private void setSsFoilMaxTuples(String foilMaxTuples) {
        setFoilMaxTuples(foilMaxTuples);
    }
    
    private void setSsPrAgeDir(String prAgeDir) {
        setPrAgeDir(prAgeDir);
    }


    private void setSs2014FractionAdditionAdhoc(boolean flag) {
        if(getSimSt() != null) {
    		getSimSt().setSs2014FractionAdditionAdhoc(flag);
    	}
        
    }
    
    
  

  
    public void setSsNumBadInputRetries(String num){
    	setSsNumBadInputRetries(Integer.parseInt(num));
    }
    
    public void setSsPrefsFile(String filename)
    {
    	// Check if the filename exists on the local file system or is in jar
    	/*File brPrefsFile = new File(new File(".").getAbsolutePath(), filename);
    	if(brPrefsFile != null && brPrefsFile.exists() && brPrefsFile.isAbsolute()) {
    		trace.out("miss", "Loading the prefs file locally:  " + filename);
    		getBrController().getPreferencesModel().setPreferenceFile(filename);
    		getBrController().getPreferencesModel().loadFromDisk();
    	} else {
    		WebStartFileDownloader finder = new WebStartFileDownloader();
    		String file = finder.findFile(filename);
    		trace.out("miss", "Loading the prefs file:  " + file);
    		getBrController().getPreferencesModel().setPreferenceFile(file);
    		getBrController().getPreferencesModel().loadFromDisk();
    	}*/
    	if(SimSt.WEBSTARTENABLED){
    		WebStartFileDownloader finder = new WebStartFileDownloader();
    		String file = "";
    		if(getSimSt().getPackageName().equals("TabbedTest"))
    			file = finder.findTabbedTestPrefsFile(filename);
    		else
    			file = finder.findFile(filename);
    		trace.out("miss", "*Loading the prefs file webstart:  " + filename);
    		getBrController().getPreferencesModel().setPreferenceFile(file);
    	}
    	else if(getSimSt().isSsWebAuthoringMode()){
    		trace.out("miss", "*Loading the prefs file locally:  " + getSimSt().getProjectDir()+System.getProperty("file.separator")+filename);
    		getBrController().getPreferencesModel().setPreferenceFile(getSimSt().getProjectDirectory()+System.getProperty("file.separator")+filename);
    	}
    	else{
    		trace.out("miss", "*Loading the prefs file locally:  " + getSimSt().getProjectDir()+System.getProperty("file.separator")+filename);
    		getBrController().getPreferencesModel().setPreferenceFile(getSimSt().getProjectDir()+System.getProperty("file.separator")+filename);
    	}
    		getBrController().getPreferencesModel().loadFromDisk();
    }
    
    
    public void setSsActivationList(String name)
    {
    	SimSt.setActivationListType(name);
    }

    /* @author: Huan Truong */
    private void setSsTutalkParams (String flag) {
    	setSsUseTutalk(flag);
    }
    
    private void setCTIBothStuckParams(String flag) {
    	setSsUseCTIBothStuckWithTutalk(flag);
    }
    
    public void setSsGeneralWmePaths() {
    	getSimSt().setLearnGeneralWmePaths();
    }

    private void setSsMaxSearchDepth(String num) {
        setSsMaxSearchDepth(Integer.parseInt(num));
    }
    
     // Set the max duration for learning
    private void ssSearchTimeOutDuration(String longDuration) {
        setSsSearchTimeOutDuration(longDuration);
    }
    
   
    // Set the max duration allowed for interface to be inactive
    private void  ssInactiveInterfaceTimeOutDuration(String longDuration) {
        setSsInactiveInterfaceTimeOutDuration(longDuration);
    }
    
    // Set the max duration for waiting for the tutor ctatLauncher to check answers
    private void ssTutorServerTimeOutDuration(String longDuration) {
        setSsTutorServerTimeOutDuration(longDuration);
    }
    
    private void setSimStLogging(boolean flag) {
    	setSsLogging(flag);
    }
    
    private void setSimStDummyContest(boolean flag) {
    	setSsDummyContest(flag);
    }
    
    private void setSimStContestServer(String address) {
    	setSsContestServer(address);
    }
    
    private void setSimStContestPort(String port) {
    	setSsContestPort(Integer.parseInt(port));
    }
    
    private void setSimStLocalLogging(boolean flag) {
    	setSsLocalLogging(flag);
    }

    private void setSsForceToUpdateModel(boolean flag) {
    	setForceToUpdateModel(flag);
    }
   
    
    private void setSsCacheOracleInquiry(String flag) {
        setSsCacheOracleInquiry(!flag.equalsIgnoreCase("false"));
    }
  
    private void setSsPreservePrFile(boolean flag) {
        setClArgumentSetToProtectProdRules(flag);
    }

    
    private void setSsLearnCltErrorActions (String flag) {
    	setSsLearnCltErrorActions(flag.equalsIgnoreCase("true"));
    }

    private void setSsLearnBuggyActions (String flag) {
    	setSsLearnBuggyActions(flag.equalsIgnoreCase("true"));
    }

    private void setSsLearnCorrectActions (String flag) {
    	setSsLearnCorrectActions(!flag.equalsIgnoreCase("false"));
    }
	
  

    /// This was also empty on SingleSessionLauncher
    private void setProjectDir(String pDir)
    {
    	
    }
    
    
    public void setSimStProblemsPerQuizSection(String num)
    {
    	setSimStProblemsPerQuizSection(Integer.parseInt(num));
    }
    
    private boolean simStPleOn = false;
    private void setSimStPleOn(boolean flag) {
        simStPleOn = flag;
    }
    
    
    public boolean isSimStPleOn() {
        return simStPleOn;
    }
 
 
    
    public void setSimStUserId(String userID)
    {
    	setSsUserID(userID);
    }
    
    
    // Wed Dec 20 17:33:49 LMT 2006 :: Noboru
    // For the Bootstrapping project with Stoichiometry, which does not have Java 
    // Tutor interface, but only BRDs
    
    public void runSimStNoTutorInterface() {

    	if (trace.getDebugCode("miss")) trace.out("miss", "runSimStNoTutorInterface...");
            //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
            //runSimStInBatchMode();
            if (isSsRunValidation()) {
               
            	getBrController().setModeSimStAndDestroyProdRules();
                //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
                runSimStValidationTest();
                System.exit(0);
            } else {
                if (trace.getDebugCode("miss")) trace.out("miss", "ssBatchMode ON...");
                getBrController().setModeSimStAndDestroyProdRules();
                //controller.getCtatModeModel().setMode(CtatModeModel.SIMULATED_STUDENT_MODE);
                runSimStInBatchMode();
                System.exit(0);
            }        
        }
    
    public void addSimStWebAuthoringBackend(SimStBackendExternal webAuthBack){
   
    		this.getSimSt().setWebAuthoringBackend(webAuthBack); 
    		
	 }

	/*public String getTypeChecker() {
		return typeChecker;
	}

	public void setTypeChecker(String typeChecker) {
		this.typeChecker = typeChecker;
	}*/
    
    
    
}

	

//
// end of MissController.java
// 
