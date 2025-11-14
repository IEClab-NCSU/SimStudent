/**
 * 
 */
package edu.cmu.pact.miss;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import pact.CommWidgets.TutorWrapper;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.MetaTutor.APlusHintMessagesManager;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;

/**
 * MissController methods used by classes outside of the edu.cmu.pact.miss.* packages.
 */
public interface MissControllerExternal {

	/** Default skill set name appeared in the EditSkillNameDialog. */
	public final static String SIM_ST_SKILL_SET_NAME = "simSt";
	public static final String UNDO = "undo";

	public SimSt getSimSt();
	
	public void skillNameSet(String ruleName, ProblemNode problemNode);
	
	public void activate(boolean newStatus);

	public void dealWithAnyExistingPrFile();

	public String getDefaultRuleSetName();

	public JComponent getMissConsole();
	
	public boolean isPLEon();

	public void loadInstructions();

	public void readSimStOperators();

	public void testProductionModelOn();

	public void readSimStPredicateSymbols();

	public void saveInstructions();

	public void runSimStInBatchMode(String[] trainingSet, String[] testSet, String output);

	public void setFoilLogDir(String foilLogDir);

	public void setPrAgeDir(String prAgeDir);

	public void setForceToUpdateModel(boolean flag);

	public void setOpCached(boolean flag);

	public void setSimStInitStateFile();

	public void setSimStWmeTypeFile();

	public void setSsCondition(String ssCondition);

	public void setSsFeaturePredicateFile(String featurePredcatesFile);

	public void setSsLearningLogFile(String learningLogFile);

	public void setSsOperatorFile(String operatorFilePath);

	public void setSsUserDefSymbols(String userDefSymbols);

	public void ssUseDecomposition();

	public void startStateCreated(ProblemNode problemNode);

	public boolean isContestOn();

	public void startNewProblem();

	public void setSsMemoryWindowSize(String memoryWindowSize);

	public void ssShuffleRunInBatch(String[] trainingSet, String[] testSet,
			String output, int numIteration);

	public void toggleFocusOfAttention(Object widget);

	public void setSsUserID(String userID);

	public void setSsSimStName(String name);

	public void stepDemonstrated(ProblemNode newNode, Vector selection,
			Vector action, Vector input, ProblemEdge newAddedEdge);

	public TutorWrapper createWrapper(boolean simStPleOn, boolean ssContest, TutorController controller);

	public void setSsBatchMode(boolean flag);

	/** To enable meta tutor support for SimStudent. */
	public void setSsMetaTutorMode(boolean flag);

	/**	To abstract the quiz problems. */
	public void setSsQuizProblemAbstractorClass(String quizProblemAbstractor);

	public APlusHintMessagesManager getAPlusHintMessagesManager();
	
	public void setAPlusHintMessagesManager(APlusHintMessagesManager aPlusHintMessagesManager);

	public void setSsInteractiveLearning(boolean flag);
	
	public void setSsNonInteractiveLearning(boolean flag);

	public void setSsInteractiveLearningFlag(boolean flag);
	
	public void setSsNonInteractiveLearningFlag(boolean flag);

	public void runSimStInContestMode();

	public void setSsFoaGetterClass(String foaGetter);

    public void setSsStepNameGetterClass(String stepNameGetter);

	public void setSsInputCheckerClass(String inputChecker);

	public void setSsPathOrderingClass(String cName);

	public void setSsRuleActivationTestMethod(String ssRuleActivationTestMethod);

	public void setSsHintMethod(String ssRuleActivationTestMethod);

	public void setSsNumBadInputRetries(int parseInt);

	public void setSsIntroVideo(String name);

	public void setSsOverview(String name);

	public void setSsMaxSearchDepth(int parseInt);

	public void setSsPredictObservableActionName(String name);

	public void setSsSearchTimeOutDuration(String longDuration);

	public void setSsLogging(boolean flag);

	public void setSsDummyContest(boolean flag);

	public void setSsContestServer(String address);

	public void setSsContestPort(int parseInt);

	public void setSsLocalLogging(boolean flag);

	public void setSsMemoryWindowOverRules(boolean flag);

	public void setSsFoaSearch(boolean flag);

	public void setSsFoaClickDisabled(boolean flag);

	public void setSsCacheOracleInquiry(boolean b);

	public void setSsHeuristicBasedIDS(boolean flag);

	public void setClArgumentSetToProtectProdRules(boolean flag);

	public void setSsDeletePrFile(boolean flag);

	public void setSsLearnCltErrorActions(boolean equalsIgnoreCase);

	public void setSsLearnBuggyActions(boolean equalsIgnoreCase);

	public void setSsLearnCorrectActions(boolean b);

	public void setSsVerifyNumFoA(boolean flag);

	public void setSsSimStImage(String img);

	public void setSimStProblemsPerQuizSection(int parseInt);

	public void setSimStLogURL(String logURL);

	public void setSsCLQuizReqMode();

	public void setSsSelfExplainMode();

	public void setSsTutorServerTimeOutDuration(String longDuration);

	public void setSsSkillNameGetterClass(String skillNameGetter);

	public void setSsSaiConverterClass(String saiConverter);

	public void setSsSelectionOrderGetterClass(String selectionGetter);

	public void requestEnterNewProblem();

	public void checkCompletedStartState();

	public boolean isFoaGetterDefined();

	public boolean isFoaSearch();

	public void askSpecifyFocusOfAttention();

	public boolean isMissHibernating();

	public boolean isFocusOfAttentionSpecified();

	public SimStPLE getSimStPLE();

	public boolean isBatchModeOn();

	public void setTitle(JFrame activeWindow);

	// SimStTutalk
	public void setSsUseTutalk(String param);

	public void setSsInterfaceElementGetterClass(String interfaceElementGetter);

	public void setSsFixedLearningMode(boolean ssFixedLearningMode);

	public void parseArgv(String[] argv);

	public boolean isSimStPleOn();

	public boolean isSsContest();
	
	public void runSimStNoTutorInterface();
	
	public void addSimStWebAuthoringBackend(SimStBackendExternal webAuthBackend);
}
