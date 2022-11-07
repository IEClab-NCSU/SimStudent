/**
 * 
 */
package edu.cmu.pact.miss;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import pact.CommWidgets.TutorWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.MetaTutor.APlusHintMessagesManager;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;

/**
 * Stubs for MissController methods called from classes outside of edu.cmu.pact.miss.* packages.
 */
public class MissControllerStub implements MissControllerExternal {

	/** In case we have to implement a stub that needs a real controller. */
	private CTAT_Launcher ctatLauncher = null;
	
	public MissControllerStub(CTAT_Launcher ctatLauncher) {
		this.ctatLauncher = ctatLauncher;
	}

	public void activate(boolean newStatus) {}

	public void dealWithAnyExistingPrFile() {}

	public String getDefaultRuleSetName() {
		return SIM_ST_SKILL_SET_NAME;
	}

	public JComponent getMissConsole() {
		return null;
	}

	public SimSt getSimSt() {
		//trace.out("Its returning null");
		return null;
	}

	public boolean isPLEon() {
		return false;
	}

	public void skillNameSet(String ruleName, ProblemNode problemNode) {}

	public boolean isContestOn() {
		return false;
	}

	public void loadInstructions() {}

	public void readSimStOperators() {}

	public void readSimStPredicateSymbols() {}

	public void runSimStInBatchMode(String[] trainingSet, String[] testSet,
			String output) {}

	public void saveInstructions() {}

	public void setFoilLogDir(String foilLogDir) {}

	public void setForceToUpdateModel(boolean flag) {}

	public void setOpCached(boolean flag) {}

	public void setPrAgeDir(String prAgeDir) {}

	public void setSimStInitStateFile() {}

	public void setSimStWmeTypeFile() {}

	public void setSsCondition(String ssCondition) {}

	public void setSsFeaturePredicateFile(String featurePredcatesFile) {}

	public void setSsLearningLogFile(String learningLogFile) {}

	public void setSsMemoryWindowSize(String memoryWindowSize) {}

	public void setSsOperatorFile(String operatorFilePath) {}

	public void setSsUserDefSymbols(String userDefSymbols) {}

	public void ssShuffleRunInBatch(String[] trainingSet, String[] testSet,
			String output, int numIteration) {}

	public void ssUseDecomposition() {}

	public void startNewProblem() {}

	public void startStateCreated(ProblemNode problemNode) {}

	public void testProductionModelOn() {}

	public void setSsSimStName(String name) {}

	public void setSsUserID(String userID) {}

	public void toggleFocusOfAttention(Object widget) {}

	public void stepDemonstrated(ProblemNode newNode, Vector selection,
			Vector action, Vector input, ProblemEdge newAddedEdge) {}

	public TutorWrapper createWrapper(boolean simStPleOn, boolean ssContest, TutorController controller) {
		return null;
	}

	public void runSimStInContestMode() {}

	public void setClArgumentSetToProtectProdRules(boolean flag) {}

	public void setSimStLogURL(String logURL) {}

	public void setSimStProblemsPerQuizSection(int parseInt) {}

	public void setSsBatchMode(boolean flag) {}

	public void setSsMetaTutorMode(boolean flag) {}

	public void setSsQuizProblemAbstractorClass(String quizProblemAbstractor){}

	public void setAPlusHintMessagesManager(APlusHintMessagesManager aPlusHintMessagesManager){}
	
	public APlusHintMessagesManager getAPlusHintMessagesManager() {
		return null;
	}

	public void setSsCLQuizReqMode() {}

	public void setSsCacheOracleInquiry(boolean b) {}

	public void setSsContestPort(int parseInt) {}

	public void setSsContestServer(String address) {}

	public void setSsDeletePrFile(boolean flag) {}

	public void setSsDummyContest(boolean flag) {}

	public void setSsFoaClickDisabled(boolean flag) {}

	public void setSsFoaGetterClass(String foaGetter) {}

	public void setSsFoaSearch(boolean flag) {}

	public void setSsHeuristicBasedIDS(boolean flag) {}

	public void setSsHintMethod(String ssRuleActivationTestMethod) {}

	public void setSsInputCheckerClass(String inputChecker) {}

	public void setSsInteractiveLearning(boolean flag) {}
	
	public void setSsNonInteractiveLearning(boolean flag) {}

	public void setSsInteractiveLearningFlag(boolean flag) {}
	
	public void setSsNonInteractiveLearningFlag(boolean flag) {}

	public void setSsIntroVideo(String name) {}

	public void setSsLearnBuggyActions(boolean equalsIgnoreCase) {}

	public void setSsLearnCltErrorActions(boolean equalsIgnoreCase) {}

	public void setSsLearnCorrectActions(boolean b) {}

	public void setSsLocalLogging(boolean flag) {}

	public void setSsLogging(boolean flag) {}

	public void setSsMaxSearchDepth(int parseInt) {}

	public void setSsMemoryWindowOverRules(boolean flag) {}

	public void setSsNumBadInputRetries(int parseInt) {}

	public void setSsOverview(String name) {}

	public void setSsPathOrderingClass(String cName) {}

	public void setSsPredictObservableActionName(String name) {}

	public void setSsRuleActivationTestMethod(String ssRuleActivationTestMethod) {}

	public void setSsSaiConverterClass(String saiConverter) {}

	public void setSsSearchTimeOutDuration(String longDuration) {}

	public void setSsSelectionOrderGetterClass(String selectionGetter) {}

	public void setSsSelfExplainMode() {}

	public void setSsSimStImage(String img) {}

	public void setSsSkillNameGetterClass(String skillNameGetter) {}

	public void setSsTutorServerTimeOutDuration(String longDuration) {}

	public void setSsVerifyNumFoA(boolean flag) {}

	public void checkCompletedStartState() {}

	public void requestEnterNewProblem() {}

	public boolean isFoaGetterDefined() {
		return false;
	}

	public boolean isFoaSearch() {
		return false;
	}

	public void askSpecifyFocusOfAttention() {}

	public boolean isFocusOfAttentionSpecified() {
		return false;
	}

	public boolean isMissHibernating() {
		return false;
	}

	public SimStPLE getSimStPLE() {
		return null;
	}

	public boolean isBatchModeOn() {
		return false;
	}

	public void setTitle(JFrame activeWindow) {}

	// SimStTutalk
	public void setSsUseTutalk(String param) {}

	public void setSsInterfaceElementGetterClass(String interfaceElementGetter) {}

	public void setSsFixedLearningMode(boolean ssFixedLearningMode) {}

	public void parseArgv(String[] argv) {}
	
	public boolean isSimStPleOn() { return false;}

	public boolean isSsContest() { return false;}

	@Override
	public void runSimStNoTutorInterface() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSimStWebAuthoringBackend(SimStBackendExternal webAuthBackend) {
		// TODO Auto-generated method stub
		
	}
	
	

}
