/**
* f:/Project/CTAT/ML/ISS/miss/SimSt.java
*
*
* Created: Mon Feb 21 22:38:50 2005
*
* @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
* @version 1.0
*/

package edu.cmu.pact.miss;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jess.Activation;
import jess.Defrule;
import jess.Fact;
import jess.Funcall;
import jess.HasLHS;
import jess.JessException;
import jess.Value;
import mylib.Combinations;
import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import pact.CommWidgets.JCommTextField;
import pact.CommWidgets.StudentInterfaceWrapper;
import aima.search.framework.Node;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.framework.SearchUtils;
import aima.search.framework.TreeSearch;
import aima.search.uninformed.DepthFirstSearch;
import aima.util.AbstractQueue;
import cl.utilities.sm.BadExpressionError;
import edu.cmu.old_pact.dormin.Communicator;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.jess.JessConsole;
import edu.cmu.pact.jess.JessModelTracing;
import edu.cmu.pact.jess.JessOracleRete;
import edu.cmu.pact.jess.MT;
import edu.cmu.pact.jess.MTRete;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.jess.RuleActivationTree.TreeTableModel;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.InquiryClAlgebraTutor.TutorServerTimeoutException;
import edu.cmu.pact.miss.MetaTutor.APlusQuizProblemAbstractor;
import edu.cmu.pact.miss.MetaTutor.MetaTutorAvatarComponent;
import edu.cmu.pact.miss.MetaTutor.XMLReader;
import edu.cmu.pact.miss.PeerLearning.SimStConversation;
import edu.cmu.pact.miss.PeerLearning.SimStExample;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStSolver;
import edu.cmu.pact.miss.PeerLearning.SimStTimeoutDlg;
import edu.cmu.pact.miss.PeerLearning.SimStudentLMS;
import edu.cmu.pact.miss.PeerLearning.GameShow.ContestServer;
import edu.cmu.pact.miss.PeerLearning.SimStSolver.SuccessRatioConflictResolutiionStrategy;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStBrdGraphReader;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStEdge;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStEdgeData;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStGraphNavigator;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;
import edu.cmu.pact.miss.console.controller.MissController;
import edu.cmu.pact.miss.jess.InquiryWebAuthoring;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;
import edu.cmu.pact.miss.storage.FileZipper;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
//import interaction.InquiryWebAuthoring;

public final class SimSt implements Serializable {

	/*
	 * To make the SimSt object serializable only the relevant fields required
	 * would be serialized while other fields would be marked as transient 
	 * (ignore the final and static fields of SimSt)
	 */
	private static final long serialVersionUID =   -422675056102164532L;
   // -
   // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - - -
   // -

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // Version
   //
   public final static String VERSION = "8.7";
   /* 
      Mon Feb 21 22:38:50 2005: 1.00 YAMATO
      + The first "bare-bone" version

      Sun May 29 17:21:21 2005: 1.01 FUJI
      + Embedded into MissConsole

      Sun Jul 31 14:58:40 2005: 1.10 ASAHI
      + Steps demonstrated get model traced and flagged in the BR

      Thu May  4 14:05:37 2006: 1.20 MIYAKO
      + Focus of attention unordered 
      + Domain specific focus of attention getter available
    */

   public final String BAD_INPUT_MESSAGE = "Invalid Input";
   public static final String SLASH = "I";
   public static final String EQUAL_SIGN = "_";
   public static final String OPEN_PAREN = "C";
   public static final String CLOSE_PAREN = "D";
   public static final String DECIMAL = "P"; // P signifies its a period (.) character.
   public static final String MULTIPLY = "X";


   public static final String ACCURACY_SORTED_ACTIVATION_LIST = "AccuracySortedActivationList";
   public static final String DEFAULT_ACTIVATION_LIST = "DefaultActivationList";
   public static String activationListType = DEFAULT_ACTIVATION_LIST;

   private static final String NO_ACTIVATIONS = "NoActivations";
   private static final String NOT_SPECIFIED = "NotSpecified";
      
   String logDirectory = null;
   String runType = System.getProperty("appRunType");
   public String getLogDirectory() {
	   PreferencesModel pm = getBrController().getPreferencesModel();
	   logDirectory = pm.getStringValue(BR_Controller.DISK_LOGGING_DIR);
	   return logDirectory;// = System.getProperty("logDirectory");
   }
   
   public static void setActivationListType(String type)
   {
   	activationListType = type;
   }

   // - 
   // - command line VM option - - - - - - - - - - - - - - - - - - -
   // -
   /**
    * ssLearnNoLabel  Set if you wish SimSt to learn rules without skill names
    * 
    **/
   private boolean ssLearnNoLabel = false;
   public boolean isSsLearnNoLabel() {
       return ssLearnNoLabel;
   }
   public void setSsLearnNoLabel(boolean ssLearnNoLabel) {
       this.ssLearnNoLabel = ssLearnNoLabel;
   }

   /** Singleton Lock object */
	public static Object simStLock = new Object();

	private static final int MAX_THREAD_COUNT = 1;
	
	/**	Thread pool with MAX_THREAD_COUNT available threads at any point of time. */
	private static ExecutorService simStSaveStateThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

	private boolean loggingEnabled = false;
   public boolean getLoggingEnabled()
   {
   	return loggingEnabled;
   }
   public void setLoggingEnabled(boolean logEnabled)
   {
   	loggingEnabled = logEnabled;
   }

   private boolean dummyContestResponse;
   public void setDummyContestResponse(boolean dummy)
   {
   	dummyContestResponse = dummy;
   }
   public boolean isDummyContestResponse()
   {
   	return dummyContestResponse;
   }


   /** Maintains the solved quiz problems for saving them and restoring them during (de)serialization  */
   private Vector<SimStExample> solvedQuizProblem;
	public Vector<SimStExample> getSolvedQuizProblem() {
		return solvedQuizProblem;
	}
	public void setSolvedQuizProblem(Vector<SimStExample> solvedQuizProblem) {
		this.solvedQuizProblem = solvedQuizProblem;
	}

	private boolean localLoggingEnabled = false;
   public boolean getLocalLoggingEnabled()
   {
		return localLoggingEnabled;
   }
   public void setLocalLoggingEnabled(boolean logEnabled)
   {
   	localLoggingEnabled = logEnabled;
   }


   private transient String userID;
   public String getUserID()
   {
   	return userID;
   }
   public void setUserID(String userID)
   {
   	this.userID = userID;
   }

   
   private String problemCheckerOracle = "";
   
   public String getProblemCheckerOracle() {
	return problemCheckerOracle;
   }
   public void setProblemCheckerOracle(String problemCheckerOracle) {
	this.problemCheckerOracle = problemCheckerOracle;
   }


  
   
   // Name of SimStudent that the student named
   public static String SimStName = "";
   static String teacherName = "Mr. Williams";

   public static String getSimStName() {
       return SimStName;
   }
   public static void setSimStName(String simStName) {
       SimStName = simStName;
   }

   public static String getTeacherName() {
       return teacherName;
   }
   public static void setTeacherName(String teacherName) {
       SimSt.teacherName = teacherName;
   }

   String simStImage = SimStPLE.STUDENT_IMAGE;
   public String getSimStImage()
   {
   	return simStImage;
   }
   public void setSimStImage(String simStImage)
   {
   	this.simStImage = simStImage;
   }

   /** HashTable to store the slider bar name and value pairs */
   private Hashtable<String, Integer> skillSliderNameValuePair = new Hashtable<String, Integer>();

   public void setSkillSliderNameValuePair(Hashtable<String, Integer> skillSliderNameValuePair) {
		this.skillSliderNameValuePair = skillSliderNameValuePair;
	}
	public Hashtable<String, Integer> getSkillSliderNameValuePair() {
		return skillSliderNameValuePair;
	}

   private boolean quizReqMode = false;
   public void setSSCLQuizReqMode(boolean quizReqMode)
   {
   	this.quizReqMode = quizReqMode;
   }
   public boolean getSSCLQuizReqMode()
   {
   	return quizReqMode;
   }

   private boolean selfExplainMode = false;
   public void setSSSelfExplainMode(boolean explainMode)
   {
   	this.selfExplainMode = explainMode;
   }
   public boolean isSelfExplainMode()
   {
   	return selfExplainMode;
   }

  private transient ProblemAssessor problemAssessor = new AlgebraProblemAssessor();
   public void setProblemAssessor(ProblemAssessor pa)
   {
   	problemAssessor = pa;
   }

   public ProblemAssessor getProblemAssessor()
   {
   	return problemAssessor;
   }
   
  
   public void setProblemAssessor(String paClassName)
   {
	   
	   try {
           Class paClass = Class.forName(paClassName);
           this.problemAssessor = (AlgebraProblemAssessor) paClass.newInstance();
          
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

   }
   
   
   public static String convertToSafeProblemName(String name)
   {
	    //Replace symbols unallowed in problem names with stand-in character
	    name = name.replaceAll("=", SimSt.EQUAL_SIGN).replaceAll(" ", "");
	    name = name.replaceAll("/", SimSt.SLASH);
	    name = name.replaceAll("\\*", SimSt.MULTIPLY);
	    name = name.replaceAll("\\(", SimSt.OPEN_PAREN);
	    name = name.replaceAll("\\)", SimSt.CLOSE_PAREN);
	    name = name.replaceAll("\\.", SimSt.DECIMAL);
	    
	    
	    return name;
   }

   public static String problemDelimiter="=";
  
   public void setProblemDelimiter(String problemDelimiter){
	  this.problemDelimiter=problemDelimiter;
   }
   
   public String getProblemDelimiter(){
	   return this.problemDelimiter; 
   }
   
   
   public static String convertFromSafeProblemName(String name)
   {

		 name = name.replaceAll(SimSt.EQUAL_SIGN, "=");
		 name = name.replaceAll(SimSt.SLASH, "/");
		 name = name.replaceAll(SimSt.MULTIPLY, "*");
		 name = name.replaceAll(SimSt.OPEN_PAREN, "(");
		 name = name.replaceAll(SimSt.CLOSE_PAREN, ")");
		 name = name.replaceAll(SimSt.DECIMAL, ".");
		 
		 return name;
   }

   // General WME paths flag determines how WME paths are learned
   // If set to false (default), they will be learned from specific to general
   // If set to true, they will be learned from general to specific (will remain general)
   private boolean generalWmePaths = false;
   public void setLearnGeneralWmePaths() {
   	generalWmePaths = true;
   }
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // Constants
   //

   // Maximal search depth
   //
   // 31 Oct: these numbers need to be set 1 above the actual max.
   // This must be fixed soon.
   //
   //13Nov2006: search depth is now a command-line argument
   public static int MAX_SEARCH_DEPTH = 6;

   //13Nov2006: search depth is now a command-line argument
   // Maximal length in RHS operator sequences
   public static int MAX_RHS_OPS = 6;

   private int numBadInputRetries = 0;

   private transient SimStLogger logger;
   
   public SimStLogger getSimStLogger(){return this.logger;}

   private int problemsPerQuiz = 0;
   public void setSimStProblemsPerQuizSection(int num)
   {
   	problemsPerQuiz = num;
   }
   public int getProblemsPerQuiz()
   {
   	return problemsPerQuiz;
   }

   public static final String START_STEP = "START";

   private String problemStepString = START_STEP;
   public String getProblemStepString()
   {
   	return problemStepString;
   }
   public void setProblemStepString(String name)
   {
   	problemStepString = name;
   }

   private InquiryWebAuthoring inquiryWebAuthoring;
   
   

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Obsolete fields... used when Sim Students were still toddlers...
   // 
   // Question - is this actually obsolete? seems to be used in initbackgroundknowledge.

   /** cygwin1.dll required for running foil executable on windows.  */
   public static final String CYGWIN_DLL = "cygwin1.dll";
   /** foil executable for windows */
   public static final String WIN_FOIL = "foil6.exe";
   /** foil executable for mac */
   public static final String MAC_FOIL = "foil6_mac";
   /** foil executable for nix */
   public static final String NIX_FOIL = "foil6_nix";
   	
   private final String INSTRUCTION_FILE = "step-performed.txt";
   private String FEATURE_PREDICATES_FILE = "feature-predicates.txt";
   private String RHS_OP_FILE = "operators.txt";
   private String CONCEPT_FILE = "concepts.txt";

   /**
    * file where a list of decomposers to apply to input is stored
    */
   private String DECOMPOSER_FILE="decomposers.txt";
   /**
    * file where a list of (fully qualified) class names for WMEConstraintPredicates is stored 
    */
   private String CONSTRAINT_FILE="constraints.txt";
   // private final String WME_TYPE_FILE = "EqTutor-wmeType.clp";
   public static final String WME_TYPE_FILE = "wmeTypes.clp";
   // private final String INIT_STATE_FILE = "EqTutor-init.clp";
   public final static String INIT_STATE_FILE = "init.wme";
   private final String DEFAULT_STUCTURE_FILE="wmeStructure.txt";
   /**
    * file specifying the hiearchy of interface components
    */
   private String wmeStructureFile=DEFAULT_STUCTURE_FILE;

   /**
    * 
    * @return the path to the WMEStructureFile
    */
   public String getWmeStructureFile() {
       return wmeStructureFile;
   }
   /**
    * set the path to the WMEStructureFile
    * @param wmeStructureFile
    */
   public void setWmeStructureFile(String wmeStructureFile) 
   {
       this.wmeStructureFile = wmeStructureFile;
       getRete().loadWMEStructureFromFile(wmeStructureFile);
   }

   public void setMaxSearchDepth(int depth) {
       depth++;
       MAX_SEARCH_DEPTH = depth;
       MAX_RHS_OPS = depth;
       Instruction.setMaxSearchDepth(depth);
   }

   /**
    * Set the file from which the feature predicates are taken for background knownledge and read the file in
    * @param featurePredicatesFile
    */
   public void setFeaturePredicateFile(String featurePredicatesFile) 
   {
       clearFeaturePredicates();    
       FEATURE_PREDICATES_FILE = featurePredicatesFile;
       readFeaturePredicates(FEATURE_PREDICATES_FILE);
   }
   /**
    * Set the file from which the WMEConstraints are taken for background knownledge and read the file in
    * @param constraintFile
    */
   public void setConstraintFile(String constraintFile)
   {
       clearConstraints();
       CONSTRAINT_FILE=constraintFile;
       readConstraintPredicates(CONSTRAINT_FILE);
   }
   /**
    * clear the constraint set so a new set can be loaded
    *
    */
   public void clearConstraints()
   {
       constraintPredicateNames.clear();

   }
   /**
    * clear the feature predicate set so a new set can be loaded
    *
    */
   public void clearFeaturePredicates()
   {
       predicates.clear();
       featurePredicateHash.clear();
       if(chunkLoadingModelTracingFunction!=null)
           chunkLoadingModelTracingFunction.resetPredicates();
   }

   // private final String PRODUCTION_RULE_FILE = "ss-productionRules.pr";
   public final static String PRODUCTION_RULE_FIL_ORACEE = "productionRulesJessOracle.pr";
   public final static String PRODUCTION_RULE_FILE = "productionRules.pr";
   private final String USER_PRODUCTION_RULE_FILE = "productionRules-$.pr";//"$_productionRules.pr";
   //Jinyu
   private final String INSTRUCTIONS_FILE = "instructions.txt";
   public String getInsFileName() {return INSTRUCTIONS_FILE; }
   public String getPrFileName() {return PRODUCTION_RULE_FILE; }

   // A directory where old production rule files are preserved
   private final String PR_AGE_DIR = "PR-age";

   // production set name
   private String productionSet = "SimSt";

   // flag to indicate whether CTAT is running in a batch mode.
   private boolean isBatchMode = false;

   
   //
   
   
	/**
    * bit to keep track of whether the decomposeInput flag has already been processed 
    */
   private boolean decomposeInputFlagSet=false;
   private void setIsBatchMode(boolean flag) { isBatchMode = flag; }

   private boolean isValidationMode = false;
   private void setIsValidationMode(boolean flag) { isValidationMode = flag; }
   public boolean isValidationMode(){ 	return isValidationMode;    }

   /**
    * Interactive Learning
    */
   private boolean isInteractiveLearning = false;
   private boolean isInteractiveLearningFlag = false;
   public boolean isInteractiveLearning(){ return this.isInteractiveLearning; }
   public void setIsInteractiveLearning(boolean flag) {
       this.isInteractiveLearning  = flag;
   }
   public boolean isInteractiveLearningFlag() { return this.isInteractiveLearningFlag; }
   public void setIsInteractiveLearningFlag(boolean flag) {
   	this.isInteractiveLearningFlag = flag;
   }
   private transient SimStInteractiveLearning ssInteractiveLearning = null;
   public SimStInteractiveLearning getSsInteractiveLearning() {
       return ssInteractiveLearning;
   }
   public void setSsInteractiveLearning(
           SimStInteractiveLearning ssInteractiveLearning) {
       this.ssInteractiveLearning = ssInteractiveLearning;
   }

	/**
    * Non-Interactive Learning
    */
   private boolean isNonInteractiveLearning = false;
   private boolean isNonInteractiveLearningFlag = false;
   public boolean isNonInteractiveLearning() { return this.isNonInteractiveLearning; }
   public void setIsNonInteractiveLearning (boolean flag) {
   	this.isNonInteractiveLearning = flag;
   }
   public boolean isNonInteractiveLearningFlag() { return this.isNonInteractiveLearningFlag; }
   public void setIsNonInteractiveLearningFlag(boolean flag) {
   	this.isNonInteractiveLearningFlag = flag;
   }

   private boolean dontShowAllRA;
   public void setDontShowAllRA(boolean dontShow) {
       dontShowAllRA = dontShow;
   }
   public boolean dontShowAllRA() {
       return dontShowAllRA;
   }

   //11Dec2007: this flag is only used for the purposes of making a video.
   private boolean useCacheOracleInquiry = true;
   public boolean isUseCacheOracleInquiry(){ return this.useCacheOracleInquiry; }
   public void setSsCacheOracleInquiry(boolean flag) {
       this.useCacheOracleInquiry = flag;
   }

   public static boolean isInteractiveLearningPerformingAStep = false;

   /**
    * Wed Feb 6 2008 6:40PM :: Noboru
    * Switching learing strategy from Traditional to Interactive
    * for the Learning Study (JIAED 2009)
    */
   private boolean switchLearningStrategy = false;
   public boolean isSwitchLearningStrategy() {
       return switchLearningStrategy;
   }
   public void setSwitchLearningStrategy(boolean switchLearningStrategy) {
       this.switchLearningStrategy = switchLearningStrategy;
   }

   // Specify the number training problems that must be learned with
   // the traditional learning.  After learing on these training prolblems,
   // SimStudent switched the learning strategy to Interactive Learning.
   private int switchLearningStrategyAfter = Integer.MAX_VALUE;
   public int getSwitchLearningStrategyAfter() {
       return switchLearningStrategyAfter;
   }
   public void setSwitchLearningStrategyAfter(int switchLearningStrategyOn) {
       this.switchLearningStrategyAfter = switchLearningStrategyOn;
   }

   /**
    * Signal Negative Example when SimSt makes a wrong step during Interactive Learning
    * 
    */

   private boolean ilSignalNegative = true;
   public void setIlSignalNegative( boolean flag ) {
       this.ilSignalNegative = flag;
   }
   public boolean isILSignalNegative() {
       return ilSignalNegative;
   }

   private boolean ilSignalPositive = true;
   public void setIlSignalPositive( boolean flag ) {
       this.ilSignalPositive = flag;
   }
   public boolean isILSignalPositive() {
       return ilSignalPositive;
   }

   /**
    * flag to indicate whether SimSt should attempt 
    * to verify the consistency of number of FoA
    */
   private boolean verifyNumFoA = false;
   public void setVerifyNumFoA (boolean flag) { verifyNumFoA = flag; }
   private boolean verifyNumFoA() { return verifyNumFoA; }

   public /*private*/ boolean decomposeInput;
   /**
    * 
    * @return true if SimSt is using decomposed 
    */
   public boolean getDecomposeInput()
   {
       return decomposeInput;
   }
   /**
    * set the flag as to whether SimSt should decompose input, 
    * if true, initialize decomposers, if false set decomposer list to null
    * @param decompose true if SimSt should decompose input false otherwise
    */
   public void setDecomposeInput(boolean decompose)
   {
       decomposeInput=decompose;
       decomposeInputFlagSet=true;

       if(decomposeInput) {

           decomposers=new Vector<Decomposer>();

           if(!readDecomposers(DECOMPOSER_FILE)) {

               // turn decomposition off if there's a problem with the file
               setDecomposeInput(false);

           } else {

               chunkLoadingModelTracingFunction=new WMEChunkLoader();

               MTRete mtRete=getBrController().getModelTracer().getRete();
               mtRete.addStartStateHookCall(chunkLoadingModelTracingFunction);
               mtRete.addHookCall(chunkLoadingModelTracingFunction);
               ((WMEChunkLoader)chunkLoadingModelTracingFunction).setDecomposers(decomposers);
           }

       } else {

           decomposers=null;
           chunkLoadingModelTracingFunction=null;
       }
   }

   /**
    * Set the name of the file where decomposer classes are specified
    * @param fileName
    */
   public void setDecomposerFile(String fileName)
   {
       DECOMPOSER_FILE=fileName;

       // decomposition flag already set, reload decomposers
       if(decomposeInputFlagSet) {
           setDecomposeInput(true);
       }
   }

   public SimStBackendExternal webAuthoringBackend=null;
   public void setWebAuthoringBackend(SimStBackendExternal webAuthBackend){ webAuthoringBackend = webAuthBackend;}
   public SimStBackendExternal getWebAuthoringBackend(){ return this.webAuthoringBackend;}
   
   /**
    * if true, SimSt will attempt to automagically reorder the focus of attention in a consistent fashion 
    */
   private static boolean autoOrderFOA=true;

   public static boolean isAutoOrderFOA()
   {
       return autoOrderFOA;
   }
   /**
    * set whether SimSt should automatically try and order focus of attention based on the VALUES
    * note that this ignores topological location of the foa
    * @param doAutoOrderFOA 
    */
   public void setAutoOrderFOA(boolean doAutoOrderFOA)
   {
       SimSt.autoOrderFOA = doAutoOrderFOA;
   }

   public boolean getAutoOrderFOA(){
       return autoOrderFOA;
   }


   private String prAgeDir = PR_AGE_DIR;
   public void setPrAgeDir( String prAgeDir ) {
       this.prAgeDir = prAgeDir;
   }
   public String getPrAgeDir() { return prAgeDir; }

   // A package name for "load-package" command appearing in the production
   // rule file
   private String userDefSymbols = null;
   public void setUserDefSymbols(String userDefSymbols) {
       this.userDefSymbols = userDefSymbols; 
   }
   public String getUserDefSymbols() {
       return this.userDefSymbols;
   }

   /*    private static String domainName = "algebra"; //default is "algebra"
   public void setDomainName(String dn) {
       domainName = dn; 
   }
   public static String getDomainName() {
       return domainName;
   }
    */
   private static String predictObservableActionName = "predict-algebra-input";
   public static void setPredictObservableActionName(String name) {
       predictObservableActionName = name; 
   }
   public static String getPredictObservableActionName() {
       return predictObservableActionName;
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // "Home" directory where the CTAT is installed
   //
   static String homeDir = ".";
   public static String getHomeDir() { return homeDir; }
   public /*private*/ void setHomeDir(String homeDir) {
       SimSt.homeDir = homeDir;
   }

   // "Project" directory where the tutor class files are installed
   // This makes sense only when Sim Student is launched by the
   // Behavior Recorder
   //
   // 2/03/2006 16:36:00
   // Due to a complication in intialization, it's decided to be static 
   private static String projectDir = null;
   public String getProjectDir() {
       if ( projectDir == null ) {
           projectDir = new File(".").getPath();
       }
       return projectDir; 
   }
   public static void setProjectDir( String pDir ) {
   	if(trace.getDebugCode("miss"))trace.out("miss", "ProjectDir set to " + pDir );
       projectDir = pDir;
   }
   
   /**
    * This variable is used to set user bundle directory in webaplus and watson 
    */
   private String userBundleDirectory = null;
   
   public String getUserBundleDirectory() {
	   if (userBundleDirectory == null) {
		   trace.err("Missing path for user bundle directory");
	   }
	   return userBundleDirectory;
   }
   public void setUserBundleDirectory(String directory) {
	   userBundleDirectory = directory;
   }
   
   /***
    *  This variable is meant for setting the project directory in the servlet 
    */
   private String projectDirectory = null;

   public String getProjectDirectory() {
	   if(projectDirectory == null)
		   projectDirectory = new File(".").getPath();
	    return projectDirectory;
   }
   public void setProjectDirectory(String directory) {
	     projectDirectory = directory;
   }

   
  
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Student Interface class name
   // Mon Apr 24 22:27:31 2006
   // Need to module out the UserDefSymbols
   //
   private String studentInterfaceClass = null;
   public String getStudentInterfaceClass() {
	   if(runType.equals("springBoot")) {
		   studentInterfaceClass=this.getPackageName();
	   }
	   else {
		   if (this.studentInterfaceClass == null) {
			   BR_Controller brCtl = getBrController();
			   StudentInterfaceWrapper tWrapper = brCtl.getStudentInterface();
			   if(trace.getDebugCode("miss"))trace.out("miss", "tWrapper = " + tWrapper);
			   if (tWrapper != null) {
				   JComponent sInterface = tWrapper.getTutorPanel();
				   String interfaceClass = sInterface.getClass().getName();
				   int dotPos = interfaceClass.lastIndexOf('.');
				   interfaceClass = interfaceClass.substring(0,dotPos);
				   setStudentInterfaceClass(interfaceClass);
				   if(trace.getDebugCode("miss"))trace.out("miss", "StudentInterface: " +  interfaceClass);
			   }
		   }
	   }
       if (studentInterfaceClass==null) studentInterfaceClass=this.getPackageName();
       return studentInterfaceClass;
   }
   public void setStudentInterfaceClass(String studentInterfaceClass) {
       this.studentInterfaceClass = studentInterfaceClass;
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Fri Oct 21 17:00:30 2005
   // Sim. St. activation status
   //
   // Determines if Sim St should induce rules from demonstration or
   // not.  Even when Sim. St. is up running (hence all other CTAT
   // Sim. St.related functions are turned on), you may still want
   // Sim. St. not to learn anything (most likely due to a debugging
   // purpose)
   // 
   private boolean missHibernating = false;
   public void setMissHibernating( boolean status ) {
       this.missHibernating = status;
   }
   public boolean isMissHibernating() {
       return this.missHibernating;
   }
   /**
    * (@link WMEChunkLoader) function used to load  decomposed chunks into working memory
    */
   public /*private*/ transient WMEChunkLoader chunkLoadingModelTracingFunction; 
   /**
    * A vector of decomposers to be applied to inputs
    */
   private transient Vector /*of Decomposer*/<Decomposer> decomposers; 

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // WME definitions
   // 

   // Name of the file where WME types are defined
   private String wmeTypeFile = null;
   public String getWmeTypeFile() { return this.wmeTypeFile; }
   public void setWmeTypeFile( String wmeTypeFile ) {
       this.wmeTypeFile = wmeTypeFile.replace('\\','/');
       if(trace.getDebugCode("miss"))trace.out("miss", "Load WME types from " +  wmeTypeFile );
       if ( getInitStateFile() != null ) {
               initRete( getWmeTypeFile(), getInitStateFile() );
       }
   }

   // Name of the file where the WME values in the initial state are
   // defined
   private String initStateFile = null;
   public String getInitStateFile() { return this.initStateFile; }
   public void setInitStateFile( String initStateFile ) {
       this.initStateFile = initStateFile.replace('\\','/');
       if(trace.getDebugCode("miss"))trace.out("miss", "Initialize WME with " + initStateFile );
       if ( getWmeTypeFile() != null ) {
           initRete( getWmeTypeFile(), getInitStateFile() );
       }
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // A list of predicates
   // 
   private  transient Vector /* String */<String> predicates = new Vector<String>();
   /**
    * a list of predicates(FeaturePredicate objects) that need to be pattern matched as WME fact rather than test patterns
    */
   public  transient /*private*/ Vector /* of FeaturePredicates*/<FeaturePredicate> predicatesToTestAsFacts=new Vector<FeaturePredicate>();

   /**
    * a list of fully qualified predicate names used by WMEConstraints
    * 
    */
   private Vector /*of String*/<String> constraintPredicateNames=new Vector<String>();
   private void addFeaturePredicate( String fp ) {
	String className = fp.substring( 0, fp.indexOf("(") );
	FeaturePredicate predicate=FeaturePredicate.getPredicateByClassName(className);
	
       if(chunkLoadingModelTracingFunction!=null&& decomposeInput)
       {
           if(trace.getDebugCode("miss"))trace.out("miss", "predicate: " + predicate.toString() + "predicate.doTestAsWME: " + predicate.doTestAsWME());
           if(predicate.doTestAsWME())
           {
               if(predicate.isDecomposedRelationship())
                   chunkLoadingModelTracingFunction.addPredicate(predicate);
               predicatesToTestAsFacts.add(predicate);
           }
       }

	// If this Feature has a description, then read it and add to the KB
	// We always wanted to get the description, so let's not put it in the if
	
	if(predicate.getFeatureDescription().isDescribable()) {
	    // Add the description of the feature to our KB - Huan Truong
	    if(trace.getDebugCode("sstt"))trace.out("sstt", "Got a describable predicate: " + (predicate.getFeatureDescription()).getFeatureName());
	    insertFeatureDescription(predicate.getFeatureDescription());
	}

	this.predicates.add( fp );
   }

   /**
    * Add a "definition" of a describable feature to our knowledgebase
    *
    */
   private Vector<Describable> describableFeatures = new Vector();

   public void insertFeatureDescription(Describable feature) {
	describableFeatures.add(feature);
   }

   public Vector<Describable> getAllFeatureDescriptions() {
	return describableFeatures;
   }

   /**
    * add the name of a topological  constraint predicate to the list of constraints to be tested
    * @param constraint the fully qualified class name of a WMEConstraintPredicate
    */
   private void addConstraintFeaturePredicate(String constraint)
   {
       this.constraintPredicateNames.add(constraint);

   }

   public Vector /* String */<String> getPredicates() {
       return this.predicates;
   }

   private int numPredicates() {
       return this.predicates.size();
   }

   private transient HashMap featurePredicateHash = new edu.cmu.pact.miss.HashMap();
   private void resetFeaturePredicateCache() {
       this.featurePredicateHash = new edu.cmu.pact.miss.HashMap();
   }
   HashMap getFeaturePredicateCache() { return this.featurePredicateHash; }

   // Sat Nov  4 16:31:41 EST 2006 :: Noboru
   // For a study with large data set, the cache capacity for FeaturePredicate must
   // be limited to avoid a memory error. 
   public static int FP_CACHE_CAPACITY = 50;
   public void setFpCacheCapacty(int n) { FP_CACHE_CAPACITY = n; }

   private final String[] defaultFeaturePredicates = {
           "edu.cmu.pact.miss.userDef.oldpredicates.HasCoefficient(#)",
           "edu.cmu.pact.miss.userDef.oldpredicates.VarTerm(#)",
           "edu.cmu.pact.miss.userDef.oldpredicates.Monomial(#)",
           "edu.cmu.pact.miss.userDef.oldpredicates.Polynomial(#)",
           "edu.cmu.pact.miss.userDef.oldpredicates.HasVarTerm(#)",
           "edu.cmu.pact.miss.userDef.oldpredicates.HasConstTerm(#)",
           "edu.cmu.pact.miss.userDef.oldpredicates.Homogeneous(#)",
   "edu.cmu.pact.miss.userDef.oldpredicates.NotNull(#)" };

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // A list of operator symbols
   // 
   private final String VOID_OP_CLASS = "edu.cmu.pact.miss.VoidOp";

   private final String[] defaultRhsOperators = {
           "edu.cmu.pact.miss.userDef.oldpredicates.CopyTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.Coefficient",
           "edu.cmu.pact.miss.userDef.oldpredicates.DivTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.InverseTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.ReverseSign",
           "edu.cmu.pact.miss.userDef.oldpredicates.AddTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.EvalArithmetic",
           "edu.cmu.pact.miss.userDef.oldpredicates.RipCoefficient",
           "edu.cmu.pact.miss.userDef.oldpredicates.FirstVarTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.LastTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.LastConstTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.RemoveFirstVarTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.RemoveLastTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.RemoveLastConstTerm",
           "edu.cmu.pact.miss.userDef.oldpredicates.Denominator",
           "edu.cmu.pact.miss.userDef.oldpredicates.Numerator",
           "edu.cmu.pact.miss.userDef.oldpredicates.AddTermBy",
           "edu.cmu.pact.miss.userDef.oldpredicates.DivTermBy",
           "edu.cmu.pact.miss.userDef.oldpredicates.MulTermBy"
   };

   private Vector /* of String */<String> rhsOpList = new Vector<String>();
   public Vector /* of String */<String> getRhsOpList() { return this.rhsOpList; }
   private void addRhsOpList( String rhsOp ) { this.rhsOpList.add(rhsOp); }
   private void addAtIndexRhsOpList(int index, String rhsOp) {this.rhsOpList.add(index, rhsOp);}
   private int numRhsOps() { return this.rhsOpList.size(); }

   public void setOperatorFile(String operatorFilePath) {
       clearOperators();
       RHS_OP_FILE=operatorFilePath;
       readRhsOpList(RHS_OP_FILE);
       if(isHeuristicBasedIDS()) {
       	initOpFreqCountHashMap();
       	initJessToOperatorName();
       }
   }


   public void setOperatorFile(URI operatorFilePath) {
       clearOperators();
       RHS_OP_FILE=operatorFilePath.toString();
       readRhsOpList(operatorFilePath);
   }

   // HashMap for the frequency count of the operators
   private static HashMap OpFreqCountHashMap = new HashMap();

   private void initOpFreqCountHashMap(){
   	for(int count =0; count < rhsOpList.size(); count++) {
   		OpFreqCountHashMap.put(rhsOpList.elementAt(count), new Integer(0));
   	}
   	printOpFreqCountHashMap();
   }

   void printOpFreqCountHashMap() {

   	if(trace.getDebugCode("miss"))trace.out("miss", "------------------------------");
   	Set set = OpFreqCountHashMap.entrySet();
   	Iterator i = set.iterator();
   	
   	while(i.hasNext()) {
   		Map.Entry me = (Map.Entry)i.next();
   		if(trace.getDebugCode("miss"))trace.out("miss", me.getKey() + "        " + me.getValue());
   	}
   }

   private FeaturePredicate getRhsOp(String rhsOpName) {
   	Class classDef;
   	FeaturePredicate rhsOp = null;

   	try {
			classDef = Class.forName(rhsOpName);
			rhsOp = (FeaturePredicate) classDef.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rhsOp;
   }

   // HashMap to map jess name of the operator to the absolute name read from the operator file
   private transient HashMap JessToOperatorName = new HashMap();

   private void initJessToOperatorName() {
   	for(int i=0; i< rhsOpList.size(); i++) {
   		FeaturePredicate fp = (FeaturePredicate) getRhsOp(rhsOpList.elementAt(i).toString());
   		JessToOperatorName.put(fp.getName(), rhsOpList.elementAt(i));
   	}
   	printJessToOperatorName();
   }

   void printJessToOperatorName() {
   	
   	if(trace.getDebugCode("miss"))trace.out("miss", "------------------------------");
   	Set set = JessToOperatorName.entrySet();
   	Iterator i = set.iterator();
   	
   	while(i.hasNext()) {
   		Map.Entry me = (Map.Entry)i.next();
   		if(trace.getDebugCode("miss"))trace.out("miss", me.getKey() + "        " + me.getValue());
   	}
   }

   // clear the operator set so a new can be loaded
   public void clearOperators() {
       rhsOpList.clear();
   }

   private boolean opCached = true;
   public void setOpCached( boolean flag ) { opCached = flag; }
   boolean isOpCached() { return opCached; }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Domain dependent matching function to test if "input" is semantically correct
   //
   private String inputMatcher = null;
   public String getInputMatcher() {
       return inputMatcher;
   }
   public void setInputMatcher(String inputMatcher) {
       this.inputMatcher = inputMatcher;
   }

   private transient FeaturePredicate inputMatcherInstance = null;
   private FeaturePredicate getInputMatcherInstance() {
       if (inputMatcherInstance == null) {
           try {
               Class inputMatcherClass = Class.forName(getInputMatcher());
               inputMatcherInstance = (FeaturePredicate)inputMatcherClass.newInstance();
           } catch (Exception e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }
       }
       return inputMatcherInstance;
   }

   
   
   
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Domain dependent matching function for arguments (input & output)
   // for the operators
   //
   /*Domain dependent type matcher, set by command line argument -ssTypeChecker.*/ 
   private static String typeMatcher = "edu.cmu.pact.miss.FeaturePredicate.isCompatibleType";
   public static String getTypeMatcher() {    return typeMatcher; }
   public void setTypeMatcher(String typeMatcher) {  SimSt.typeMatcher = typeMatcher; }
   
   /*Domain dependent type checker, set by command line argument -ssTypeChecker.*/ 
   private static String typeChecker = "edu.cmu.pact.miss.FeaturePredicate.valueTypeForAlgebra";
   public static String getTypeChecker() {
	   return typeChecker;
   }
   public static void setTypeChecker(String typeMatcher) {
       SimSt.typeChecker = typeMatcher;
   }
   
   
   
   //domain dependent method that checks if a skill is valid or not.
   private static String skillChecker = "edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate.isValidSimpleSkill";
   public static String getSsValidSkillChecker() {
       return skillChecker;
   }
   public void setSsValidSkillChecker(String skillChecker) {
       SimSt.skillChecker = skillChecker;
   }
   
   
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // A number of problems demonstrated
   //
//    private int numProblems = 0;
//    int getNumProblems() { return numProblems; }
//    void setNumProblems( int n ) { numProblems = n; }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // A name of the condition for a validation test
   //
   private String ssCondition = "No-Name";
   public void setSsCondition( String condition ) {
       this.ssCondition = condition;
   }
   public /*private*/ String getSsCondition() { return this.ssCondition; }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Timing to relearn production rules
   //
   // By default, the SimStudent relearn production rules only when they 
   // are incorrect.  Setting forceToUpdateModel to be true breaks this 
   // constraint.
   // 
   private boolean forceToUpdateModel = false;
   private boolean isForceToUpdateModel() { return forceToUpdateModel; }
   public void setForceToUpdateModel( boolean flag ) {
       forceToUpdateModel = flag;
   }

   private boolean isFoaSearch = false;
   public boolean isFoaSearch () { return isFoaSearch; }
   public void setSsFoaSearch( boolean flag ) {
       isFoaSearch = flag;
   }

   private boolean showBottomOutHint=true;
   public boolean getShowBottomOutHint(){return this.showBottomOutHint;}
   public void setShowBottomOutHint(boolean flag){this.showBottomOutHint=flag;}
   
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Fri Jun 16 22:29:27 2006 Noboru
   // Flag telling if the rule is just learned or not
   // 
   // Useful only for the validation test in a batch mode.  Needed to
   // implement to plot a learning curve only with the data points
   // for newly learned rules (otherwize, the same rule would be plotted
   // multiple times
   //
   private final String RULE_LEARNED = "T";
   private final String RULE_NOT_LEARNED = "F";
   private transient HashMap ruleLearned = new edu.cmu.pact.miss.HashMap();
   private void setRuleLearned( String ruleName, String flag ) {
       ruleLearned.put(ruleName, flag);
   }
   public /*private*/ String getRuleLearned( String ruleName ) {
       return (String)ruleLearned.get(ruleName);
   }
   private boolean isRuleLearned( String ruleName ) {
       String ruleLearned = getRuleLearned(ruleName); 
       return ruleLearned != null && ruleLearned.equals(RULE_LEARNED);
   }    
   private void resetRuleLearned() {
       ruleLearned = new edu.cmu.pact.miss.HashMap();
   }
  
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Memory Window 
   // 
   // By default, consider all instructions demonstrated
   private int memoryWindowSize = Integer.MAX_VALUE;
   int getMemoryWindowSize() { return memoryWindowSize; }
   public void setMemoryWindowSize(int memoryWindowSize) {
       this.memoryWindowSize = memoryWindowSize;
       setMemoryWindowSizeSet(true);
   }
   private boolean memoryWindowSizeSet = false;
   private void setMemoryWindowSizeSet( boolean flag ) {
       this.memoryWindowSizeSet = flag;
   }
   private boolean isMemoryWindowSizeSet() {
       return this.memoryWindowSizeSet;
   }

   private boolean ssBatchMode = false;

   public void setSsBatchMode(boolean flag) {
   	this.ssBatchMode = flag;
   }

   public boolean isSsBatchMode() {
   	return this.ssBatchMode;
   }

   /** Boolean flag to indicate if turning off IL/NonIL is allowed after finishing a problem  */
   private transient boolean ssFixedLearningMode = false;
	public boolean isSsFixedLearningMode() {
		return ssFixedLearningMode;
	}
	public void setSsFixedLearningMode(boolean flag) {
		this.ssFixedLearningMode = flag;
	}

   /** Boolean flag to indicate if the meta tutor is enabled or not.*/
   private transient boolean ssMetaTutorMode = false;

   public boolean isSsMetaTutorMode() {
		return ssMetaTutorMode;
	}
	public void setSsMetaTutorMode(boolean ssMetaTutorMode) {
		this.ssMetaTutorMode = ssMetaTutorMode;
		if(isSsMetaTutorMode()){
			//System.out.println(" Resettting the Memory ");
			setModelTraceWM(new ModelTraceWorkingMemory());
		}
	}

	public static String COGNITIVE_AND_METACOGNITIVE="MetacognitiveAndCognitive";
	public static String METACOGNITIVE="MetaCognitive";
	public static String COGNITIVE="Cognitive";
	
	 /** Boolean flag to indicate if the meta tutor is enabled or not.*/
	private transient String ssMetaTutorModeLevel = COGNITIVE_AND_METACOGNITIVE;

	public String getSsMetaTutorModeLevel() {
		
			return ssMetaTutorModeLevel;
	}
	public void setSsMetaTutorModeLevel(String ssMetaTutorMode) {
		this.ssMetaTutorModeLevel = ssMetaTutorMode;//COGNITIVE_AND_METACOGNITIVE;	
	}
		
		
	 /** Boolean flag to indicate if the meta tutor is enabled or not.*/
	   private transient boolean ssWebAuthoringMode = false;
       public static boolean WEBAUTHORINGMODE = false;
	   public boolean isSsWebAuthoringMode() {
			return ssWebAuthoringMode;
		}
	   
	   public void setSsWebAuthoringMode(boolean ssWebAuthorMode) {
			FoilData.WEBAUTHORINGMODE = ssWebAuthorMode;
			this.ssWebAuthoringMode = ssWebAuthorMode;
			WEBAUTHORINGMODE = ssWebAuthorMode;
		}
		
	   /*Boolean indicating if we are in CogTutor mode*/
	   private transient boolean ssCogTutorMode=false; 
	   public boolean isSsCogTutorMode(){return this.ssCogTutorMode;}
	   public void setSsCogTutorMode(boolean flag){this.ssCogTutorMode=flag;}
	   
	   /*Boolean indicating if we are in AplusControl mode. This is a special version
	    * of CogTutor mode: Its a cog tutor that is closer to APLUS.*/	   
	   private transient boolean ssAplusCtrlCogTutorMode=false;
	   public boolean isSsAplusCtrlCogTutorMode(){return this.ssAplusCtrlCogTutorMode;}
	   public void setSsAplusCtrlCogTutorMode(boolean flag){
		   setSsCogTutorMode(true);
		   this.ssAplusCtrlCogTutorMode=flag;
		   SimStPLE.isAplusControlMode=flag;
	   }
	   	   
	
	 /** Boolean flag to indicate if the adhoc for fraction addition should 
	  * be enabled or not.
	  * */
	   private transient boolean ss2014FractionAdditionAdhoc = false;

	   public boolean isSs2014FractionAdditionAdhoc() {
			return ss2014FractionAdditionAdhoc;
		}
		public void setSs2014FractionAdditionAdhoc(boolean flag) {
			this.ss2014FractionAdditionAdhoc = flag;
			
		}
		
		
		
	/**	Keeps track of the correct and incorrect quiz items when SimStudent takes a 
	 * quiz. Is enabled only when the MetaTutor is enabled for SimStudent. */
	private transient APlusQuizProblemAbstractor quizProblemAbstractor = null;

	public APlusQuizProblemAbstractor getQuizProblemAbstractor() {
		return quizProblemAbstractor;
	}
	public void setSsQuizProblemAbstractor(String quizProblemAbstractorClassName) {
		if(isSsMetaTutorMode()) {
			try {
				Class quizProblemAbstractorClass = Class.forName(quizProblemAbstractorClassName);
				this.quizProblemAbstractor = (APlusQuizProblemAbstractor) quizProblemAbstractorClass.newInstance();
			} catch(ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			trace.err("Error trying to instantiate APlusQuizTracker. SimSt not running in MetaTutor" +
					" mode");
		}
	}

	// if set to be true, memory window is defined for each individual rules.  
   // Thus, for example, having memory window of "2" means that for all 
   // rule applications, the SimulatedStudent has an aceess fot the last two 
   // instances of the rule application. 
   private boolean memoryWindowOverIndividualRules = false;
   private boolean isMemoryWindowOverIndividualRules() {
       return memoryWindowOverIndividualRules;
   }
   public void setMemoryWindowOverIndividualRules( boolean flag ) {
       memoryWindowOverIndividualRules = flag;
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // A list of demonstrated steps
   // 

   // A hash table storing instructions where the key is the name of
   // each instruction and the value is a vector of instructions with
   // the same name as the key
   public Hashtable<String, Vector<Instruction>> instructions = new Hashtable<String, Vector<Instruction>>();
   public Hashtable<String, Vector<Instruction>> negativeInstructions = new Hashtable<String, Vector<Instruction>>();

   // A list storing instructions representing negative examples
   private List<Instruction> negativeExamples = new LinkedList<Instruction>();
   public List<Instruction> getNegativeExamples()
   {
		if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "retreiving negative examples, which are: " + negativeExamples);
		
   	return negativeExamples;
   }
   public void addNegativeExample(Instruction example)
   {
	
	   if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "Marking negative example: " + example);
	   
   	negativeExamples.add(example);
   	
   	String name = example.getName();

       Vector /* Instruction */<Instruction> instructions = this.negativeInstructions.get( name );

       if ( instructions == null ) {
           instructions = new Vector<Instruction>();
           this.negativeInstructions.put( name, instructions );
       } else {
           ;
       }

       instructions.add( example );

   }

   // Tue Jun 6 14:40:00 Noboru
   // Need to aggregate all instructions to make it possible to deal with the
   // memory window issue (have SimSt limited access to the past instructions)
   // The basic idea is to write a filter that works along with the old 
   // code (e.g., getInstructionsFor) to filter out the instructions that do not 
   // fall into the memory window. 
   private Vector /* Instruction */<Instruction> allInstructions = new Vector<Instruction>();
   public Vector<Instruction> getAllInstructions() { return allInstructions; }
   private void resetAllInstructions() {
       allInstructions = new Vector<Instruction>();
   }

   // Returns a number of all instructions made so far
   private int numAllInstructions() { return allInstructions.size(); }
   private String prettyNumAllInstructions() {
       String prettyNumAllInstructions = "000";
       prettyNumAllInstructions += numAllInstructions();
       int numLen = prettyNumAllInstructions.length();
       return prettyNumAllInstructions.substring( numLen < 6 ? numLen -3 : 3);
   }

   
   
   // Returns a number of instructions only within the memory window
   private int numInstructions = 0;
   private int getNumInstructions() { return this.numInstructions; }

   public Instruction getInstructionByID(String id){
	   Instruction ret=null;
	   Vector<Instruction> allInst=getAllInstructions();
	   
	   for (Instruction inst:allInst){
		   if (inst.getInstructionID()!=null && inst.getInstructionID().equals(id)){
			   ret=inst;
			   break;
		   }
			   
	   }
	   return ret;
   }
   
   
   
   // adds newInstruction to the list of instructions with that name.
   public void addInstruction( Instruction newInstruction ) {
	   
       String name = newInstruction.getName();

       Vector /* Instruction */<Instruction> instructions =
           this.instructions.get( name );

       if ( instructions == null ) {
           instructions = new Vector<Instruction>();
           this.instructions.put( name, instructions );
       } else {
           ;
       }

       instructions.add( newInstruction );
       numInstructions++;
       consoleDisplayNumInstructions();

       // Aggregate all instructions to control memory window
       this.allInstructions.add( newInstruction );
       // printAllInstructions("add");
   }

   ///27 Nov 2007:
   //removes 'instruction' from every list of instructions, including
   // * this.allInstructions
   // * every list inside the HashTable
   private void removeInstruction( Instruction instruction ) {

       // Remove target instruction from the bag of aggregated instructions
       this.allInstructions.remove(instruction);

       Enumeration<String> names = this.instructions.keys();
       while ( names.hasMoreElements() ) {

           String theName = names.nextElement();
           Vector /* Instruction */ instV =
               this.instructions.get( theName );

           if ( instV.removeElement( instruction ) ) {
               numInstructions--;
               if ( instV.isEmpty() ) {
                   this.instructions.remove( theName );
                   //If the instruction's name ends with -#, it is a disjunct
   	            if(theName.matches(".*-\\d*$"))
   	            {
   	            	//Strip off the -#
   	            	String baseName = theName.substring(0, theName.lastIndexOf("-"));

                       if(disjunctiveSkillNames.containsKey(baseName))
                       {
                       	Vector<String> disjuncts = (Vector<String>)(disjunctiveSkillNames.get(baseName));
                       	disjuncts.remove(theName);
                       }
   	            }
               }
               break;
           }
       }
   }

   public void deleteBadInstruction(Instruction instruction)
   {
   	removeInstruction(instruction);
   }


   // Called when the skill name of the specified instruction has
   // been changed
   public /*private*/ void sortInstruction( Instruction instruction ) {
       // Delete an "old" instruction once that has been renamed (hence 
       // placed in an inapproprite place
       removeInstruction( instruction );
       // Then add the instruction again to the appropriate place
       addInstruction( instruction );
   }

   /**
    * It's unclear what this is doing.
    * @param node
    * @return
    */

   //24 Aug 2007: this needs to change, since we are now using edge, instead of node.
   public /*private*/ Instruction lookupInstructionWithNode( ProblemNode node ) {

       Instruction theInstruction = null;

       // A list of sets of instructions
       Enumeration<Vector<Instruction>> instructionSet = this.instructions.elements();

       while ( instructionSet.hasMoreElements() ) { //for each instruction vector?
           Vector /* Instruction */ instV = instructionSet.nextElement();

           for (int i = 0; i < instV.size() && theInstruction == null; i++) { //for each instruction

               Instruction inst = (Instruction)instV.get(i);
               ProblemNode instNode = inst.getProblemNode();

               // if ( instNode.toString().equals( node.toString() ) ) {
               if ( instNode != null && instNode.equals( node ) ) {
                   theInstruction = inst;
                   break;
               }
               else if (node.toString().equals(inst.getName())) {
                   theInstruction = inst;
                   break;
               }
           }
       }
       return theInstruction;
   }

   // Return all instruction names as an Enumeration
   private Enumeration<String> getInstructionNames() {
       return this.instructions.keys();
   }
   // Return all instruction names as a Vector
   private Vector /* String */<String> getAllSkillNames() {

       Vector<String> skillNames = new Vector<String>();
       Enumeration<String> instructionNames = getInstructionNames();
       while ( instructionNames.hasMoreElements() ) {
           skillNames.add( instructionNames.nextElement() );
       }
       return skillNames;
   }

   /*
    * Returns a list of strings (FoA) that looks like this:
    * < MAIN::cell|commTable2_C1R2|b
    *   MAIN::cell|commTable2_C1R1|b-1
    *   MAIN::cell|commTable1_C1R1|5 >
    *   
    * The first element is "input"
    */
   private Vector /* String */ getFocusOfAttention(String name) {

       Vector /* String */ foa = null; 

       if(trace.getDebugCode("miss"))trace.out("miss", "getFocusOfAttention");
       Vector /* Instruction */<Instruction> instructions = getInstructionsFor(name);
       if (instructions != null && instructions.size() > 0) {
           Instruction instruction = instructions.get(0);
           foa = instruction.getFocusOfAttention();
       }

       return foa;
   }

   //24 April 2007
   //
   public /*private*/ Vector /* Instruction */<Instruction> getInstructionsFor( String name ) {
	   
   	Vector /* Instruction */ tmpInstructions = this.instructions.get( name );

       // 8 September 2007: tmpInstructions can get null because:
       // even though there is a production rule, there is no previous instruction.

       // Return only the instructions that is in the memory-window scope
       Vector /* Instruction */<Instruction> instructions = new Vector<Instruction>();

       if (isMemoryWindowOverIndividualRules()) {

           // Memory window works on individual rules separately, i.e., the SimStudent 
           // has an access to the last N rule applications for all rules
           for (int i = 0; i < getMemoryWindowSize() && i < tmpInstructions.size(); i++) {
               instructions.add((Instruction)tmpInstructions.get(i));
           }

       } else if(tmpInstructions != null){

           // Memory window works across all rule applications, i.e., the SimStudent
           // has an access to the recent N rule applications in the chronological order
           for (int i = 0; i < tmpInstructions.size(); i++) {
               Instruction instruction = (Instruction)tmpInstructions.get(i);
              // trace.out("miss", "Instruction: " + instruction);
               int age = getAllInstructions().size() - getAllInstructions().indexOf(instruction) -1; 
               if (age < getMemoryWindowSize()) {
                   instructions.add(instruction);
                 //  trace.out("miss", "IN.....");
               }
           }
       }
       
       //20Dec2006: Now that we are skipping steps with different foaSizes, this code is probably unnecessary
       //11Dec2006:   <unnecessary-code>
       if (verifyNumFoA()){
           Instruction firstInst = instructions.get(0);
           Instruction currInst = instructions.get(1);	    
           int firstInstFoaSize = firstInst.getFocusOfAttention().size();
           int currInstFoaSize = currInst.getFocusOfAttention().size();

           if (currInstFoaSize!=firstInstFoaSize){
               //instead of null, return the vector containing only the current instruction
               //instructions = null;
               instructions = new Vector<Instruction>();
               instructions.add(currInst);
           }
       }
       // </unnecessary-code>
       //trace.out("returning instructions " + instructions); 
       return instructions;
   }

   private void printInstructions() {
   	if(trace.getDebugCode("miss"))trace.out("miss", "Instructions: =========");
       Enumeration<String> names = getInstructionNames();
       while ( names.hasMoreElements() ) {
           String name = names.nextElement();
           Vector /* of Instruction */<Instruction> instructions = getInstructionsFor( name );
       }
   }

   // Overloaded to allow output to file
   // Instructions are saved as strings - see Instruction.toString()
   private void printInstructions(PrintStream out) {

       //for (int i = 0; i < negativeExamples.size(); i++) {
   	for (int i = negativeExamples.size()-1; i >= 0; i--) {
           Instruction instruction = negativeExamples.get(i);
           //provide identifying character to separate instructions/skills
           out.println("~~~");
           out.print("!");
           out.println(instruction.toString());
       }
       Enumeration<String> names = getInstructionNames();
       boolean firstTime = true;
       while ( names.hasMoreElements() ) {
           String name = names.nextElement();
           Vector /* of Instruction */<Instruction> instructions =
               getInstructionsFor( name );
           //for (int i = 0; i < instructions.size(); i++) {
           for (int i = instructions.size()-1; i >= 0; i--) {
               Instruction instruction = instructions.get(i);
               //provide identifying character to separate instructions/skills
               out.println("~~~");
               out.println(instruction.toString());
           }
       }
   }

   /**
    * Called when [Save Instructions] menu is selected on the Miss
    * Console
    * Re-implemented 6/8/06
    **/

   public void saveInstructions( File output) {
       //setup output stream and print instructions to file
       try {
           FileOutputStream fos = new FileOutputStream( output );
           PrintStream printStream = new PrintStream( fos );
           //start by printing name of production set
           printStream.println(this.productionSet);
           //then print instructions
           printInstructions( printStream );
           printStream.close();
           fos.close();
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   /**
    * Called when [Load Instructions] menu item is selected from Miss Console.
    * Implemented 6/8/06 - Reid Van Lehn <rvanlehn@mit.edu>
    * Regenerates previously saved instructions based on saved FoA results. 
    * Replaces any instructions currently in memory, so prompt user. 
    * 
    * Bugfix: Reads instructions in backwards from file to ensure they are in
    * correct chronological order. 
    */
   public void loadInstructions(File input) throws Exception {

       //start by reading in entire file and converting to string
       byte[] fileInput = new byte[(int)input.length()];
       /*jinyul - modified to prevent java.ioexceptions appearing from
       		   loading instructions. */
       FileInputStream fis = new FileInputStream(input);
       fis.read(fileInput); //reads data into byte array
       fis.close(); 
     
       
       String inputString = new String(fileInput);
       // Now split into individual instructions
       String[] instrStrings = inputString.split("\\s~~~\\s");
       // By default, first entry is the production set, so load this in
       setProductionSetName(instrStrings[0]);
       StringTokenizer tokenizer;
       Vector<String> foa;
       //For each instruction, split into name and FOA
       //Do this backwards
       for (int i=(instrStrings.length-1); i>0; i--) {

       	if(getMissController().isPLEon())
       	{
       		double percentProgress = (double)(instrStrings.length-i)/instrStrings.length;
       		getMissController().getSimStPLE().getSimStPeerTutoringPlatform().setWaitProgress(percentProgress);
       	}
       	
           tokenizer = new StringTokenizer(instrStrings[i], "\n");
           foa = new Vector<String>();
           //Name is first token
           String instrName = tokenizer.nextToken();
           String curToken;
           String action = null;
           //then add focuses of attention
           while (tokenizer.hasMoreTokens()) {
               curToken = tokenizer.nextToken();
               //add
               if (curToken.indexOf("MAIN") > -1) {
                   foa.addElement(curToken);
               }
               else if (curToken.startsWith("PreviousId")){
            	   
               }
               else if (curToken.startsWith("Id")){
            	   
               }
               else if (curToken.length() > 1) {
                   //not foa and not whitespace, so action
                   action = curToken.trim();
               }
           }
           if(!instrName.startsWith("!"))
           { //positive example
           	//Now add new instruction to list
           	if(trace.getDebugCode("miss"))trace.out("miss", "newInstr: " + instrName + "  foa: " + foa);
	            Instruction newInstr = new Instruction(instrName, foa);
	            newInstr.setAction(action);
	            addInstruction(newInstr);
	            
	            //If the instruction's name ends with -#, it is a disjunct
	            if(instrName.matches(".*-\\d*$"))
	            {
	            	//Strip off the -#
	            	String baseName = instrName.substring(0, instrName.lastIndexOf("-"));
	            	//Add to the disjunctive hashmap
	            	addDisjunctiveSkillName(baseName, instrName);
	            }
	            //try to regenerate rule
	            generateUpdateProductionRules(instrName);
           }
           else //negative example
           {
           	instrName = instrName.substring(1);
           	Instruction newInstr = new Instruction(instrName, foa);
	            newInstr.setAction(action);
	            //signal negative instruction
	           if(trace.getDebugCode("miss"))trace.out("miss", "Calling signalInstructionAsNegativeExample:  " + newInstr);
	            signalInstructionAsNegativeExample(newInstr);
           }

           //printInfo(this);
           if(trace.getDebugCode("miss"))trace.out("miss", "Call saveSimStState");
           if (!isSsWebAuthoringMode())
        	   saveSimStState();

       }
       //update skill name list
       updateConsoleSkillName();

   }

   public void loadInstnDeSerialize(File inputFile) {
     	 
     	 
   	// file name in which the object is serialized
   	File filename = inputFile;
   	SimSt simStObj = null;
   	

   	try {
   		FileInputStream fis = new FileInputStream(filename);
   		ObjectInputStream ois = new ObjectInputStream(fis);
   		
   		simStObj = (SimSt) ois.readObject();
   		ois.close();
   		fis.close();
   		setUpFoilDir(simStObj);
   	} catch(IOException e) {
   		e.printStackTrace();
   	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
				
   	// Restore the current simStudent to the same state
   	this.instructions = simStObj.instructions;
   	this.numInstructions = simStObj.numInstructions;
   	this.allInstructions = simStObj.allInstructions;
   	this.rules = simStObj.rules;
   	//this.disjunctiveSkillNames = simStObj.disjunctiveSkillNames;
   	this.foilDataHash = simStObj.foilDataHash;
   	this.negativeExamples = simStObj.negativeExamples;
   	this.solvedQuizProblem = simStObj.solvedQuizProblem;
   	this.skillSliderNameValuePair = simStObj.skillSliderNameValuePair;
   
   	
   	generateDisjunctiveSkillNames();
   	setRuleCount();
   		
   	// Write the productionRules to the file
   	saveProductionRules(SAVE_PR_STEP_BASE);
   	if(trace.getDebugCode("miss"))trace.out("miss", "Deserialization done with inputFile");
   	
   	
  
   	
   	
   }

   public void loadInstnDeSerialize(SimSt ssObj) {

   	setUpFoilDir(ssObj);
   	
   	// Restore the current simStudent to the same state
   	this.instructions = ssObj.instructions;
   	this.numInstructions = ssObj.numInstructions;
   	this.allInstructions = ssObj.allInstructions;
   	this.rules = ssObj.rules;
   	//this.disjunctiveSkillNames = ssObj.disjunctiveSkillNames;
   	this.foilDataHash = ssObj.foilDataHash;
   	this.negativeExamples = ssObj.negativeExamples;
   	this.solvedQuizProblem = ssObj.solvedQuizProblem;
   	this.skillSliderNameValuePair = ssObj.skillSliderNameValuePair;
   	
   	generateDisjunctiveSkillNames();
   	setRuleCount();
   	
   	// Write the productionRules to the file
   	saveProductionRules(SAVE_PR_STEP_BASE);
   	if(trace.getDebugCode("miss"))trace.out("miss", "Deserialization done with ssObj");
   }

   private void setRuleCount()
   {
   	int highestRule = 0;
   	for(Object o:rules.values())
   	{
   		if(((Rule) o).identity > highestRule)
   			highestRule = ((Rule) o).identity;
   	}
   	Rule.count = highestRule;
   	
   }

   public void signalInstructionAsNegativeExample(Instruction instruction) {

	addNegativeExample(instruction);

   	String skillName = instruction.getName();
       initializeFoilDataFor(skillName);
       FoilData foilData = getFoilData(skillName);
       if(foilData != null)
       {
	        foilData.signalTargetExplicitNegative(instruction); //call this only when instArity >= foilData.arity
	        
	        Vector <Instruction> instructions = negativeInstructions.get(skillName);
	        if (instructions != null && !instructions.isEmpty()) {
	        	for (int i = 0; i < instructions.size(); i++) { //for each instruction

	                Instruction inst = instructions.get(i);
	                foilData.addNegativeInstruction( inst );
	        	}
	        }
	        
	        //this saves the foilData into the FOIL input file.
	        charmFoil(foilData);
	        saveProductionRules(SimSt.SAVE_PR_STEP_BASE);
	        
	        
       }
       else
       {
       	new NullPointerException("Foil Data is null").printStackTrace();
       }

       // Do the serialization here
       //printInfo(this);
       //saveSimStState();
   }

   //Returns number of positive examples negated
   public int negateBadPositiveExample(Instruction instruction)
   {
   	Vector<Instruction> positiveExamples = instructions.get(instruction.getName());
   	if(positiveExamples == null)
   		return 0;
       Vector<Instruction> negate = new Vector<Instruction>();
       for(Instruction inst:positiveExamples)
       {
       	if(inst.toString().equals(instruction.toString()))
       	{
       		negate.add(inst);
       	}
       }
       if(negate.size() > 0)
       {
       	//JOptionPane.showMessageDialog(null, "Positive Example negated\n"+instruction+" Remove: "+negate);
       	for(Instruction inst:negate)
       	{
       		positiveExamples.remove(inst);
       	}
       	instructions.put(instruction.getName(), positiveExamples);
       	//JOptionPane.showMessageDialog(null, "Removed: "+negate.size()+" Now:\n"+getInstructionsFor(instruction.getName()));
       }
       else
       {
       	//JOptionPane.showMessageDialog(null, "No positive example negated by\n"+instruction+" in \n"+positiveExamples);
       }

       return negate.size();

   }

 //Returns number of positive examples negated
   public int negateBadNegativeExample(Instruction instruction)
   {
   	Vector<Instruction> negativeExamples = negativeInstructions.get(instruction.getName());
   	
   	if(negativeExamples == null)
   		return 0;
   	
       Vector<Instruction> negate = new Vector<Instruction>();
       for(Instruction inst:negativeExamples)
       {
       	if(inst.toString().equals(instruction.toString()))
       	{
       		negate.add(inst);
       	}
       }
       if(negate.size() > 0)
       {
       	//JOptionPane.showMessageDialog(null, "Positive Example negated\n"+instruction+" Remove: "+negate);
       	for(Instruction inst:negate)
       	{
       		negativeExamples.remove(inst);
       	}
       	negativeInstructions.put(instruction.getName(), negativeExamples);
       	

        List<Instruction> negExamples = getNegativeExamples();
           for(Instruction inst:negate)
       	{
       		negExamples.remove(inst);
       	}
       	//JOptionPane.showMessageDialog(null, "Removed: "+negate.size()+" Now:\n"+negativeInstructions.get(instruction.getName())+"\nAll Negative Examples:"+getNegativeExamples());
       }
       else
       {
       	//JOptionPane.showMessageDialog(null, "No negative example negated by\n"+instruction+" in \n"+negativeExamples);
       }
        
       return negate.size();

   }


   /**
    *  This method writes/serializes the current state of the SimSt object
    *  Fields marked as transient or static/final fields are not serialized
    */
   public synchronized void saveSimStState() {

   	// file name in which the object is serialized
   	String filename = "simst.ser";
   	File serFileUser = null;
   	FileOutputStream serFileUserStream = null;
   	if(getUserID() != null && !runType.equalsIgnoreCase("springBoot")) {
   		filename = "simst-"+getUserID()+".ser";
   	}
   	
   	if (runType.equalsIgnoreCase("springBoot")) {
   		serFileUser = new File(getUserBundleDirectory(), filename);
   	} else {
   		serFileUser = new File(getLogDirectory(), filename);
   	}
   	
   	final SimSt simStObj = this;
   	FileOutputStream fos = null;
   	ObjectOutputStream oos = null;
   	
   	if(!isWebStartMode()) {
   		
   		try {
//				fos = new FileOutputStream(filename);
   				fos = new FileOutputStream(serFileUser);
		    	oos = new ObjectOutputStream(fos);

				oos.writeObject(simStObj);
		    	oos.flush();
		    	fos.flush();
		    	oos.close();
		    	fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
   	} else {
   		
   		try {
				fos = new FileOutputStream(WebStartFileDownloader.SimStWebStartDir+filename);
		    	oos = new ObjectOutputStream(fos);

		    	oos.writeObject(simStObj);
		    	oos.flush();
		    	fos.flush();
		    	oos.close();
		    	fos.close();
		       	if(trace.getDebugCode("miss"))trace.out("miss", "Save state");

	    		Runnable runnable = new Runnable() {
					@Override
					public void run() {
						synchronized(simStLock) {
							try {
								getMissController().getStorageClient().storeObject("simst-"+getUserID()+".ser", simStObj);
								FileZipper.archiveFiles(getUserID());
								getMissController().getStorageClient().storeZIPFile("simst-" + userID +"_" + FileZipper.formattedDate()+".zip",
										WebStartFileDownloader.SimStWebStartDir + userID + "_" + FileZipper.formattedDate()+".zip");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				};
				Future<?> future = simStSaveStateThreadPool.submit(runnable);
   		} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
   	}
   }

   public void archiveAndSaveFilesOnLogout() {
   	
   	if(!isWebStartMode())
   		return;
   	
   	try {
   		synchronized(simStLock) {
   			
				FileZipper.archiveFiles(getUserID());
				getMissController().getStorageClient().storeZIPFile("simst-" + userID +"_" + FileZipper.formattedDate()+".zip",
					WebStartFileDownloader.SimStWebStartDir + userID + "_" + FileZipper.formattedDate()+".zip");
   		}
   	} catch (FileNotFoundException e) {
   		e.printStackTrace();
   	} catch (IOException e) {
   		e.printStackTrace();
   	}
   }

   
   /**
    * Method to solve a problem using the solver
    * @param problem
    * @return
    */
	public SimStSolver createSimStSolver(String problem){
		String jessFilesDirectory= getBrController().getPreferencesModel().getStringValue(CTAT_Controller.PROBLEM_DIRECTORY);	
		String productionRulesFile=jessFilesDirectory+ SimSt.PRODUCTION_RULE_FILE;
		String wmeTypesFile=jessFilesDirectory+ SimSt.WME_TYPE_FILE;  
		String initialFactsFile=jessFilesDirectory+  SimSt.INIT_STATE_FILE; 
		String pleConfig=getProjectDirectory()+"/"+SimStPLE.CONFIG_FILE;
		
		SimStSolver solver=new SimStSolver(SimSt.convertFromSafeProblemName(problem),productionRulesFile,wmeTypesFile, initialFactsFile, pleConfig, this ,getBrController().getModelTracer().getRete());
		
		return solver;
	}
	
	
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Frequency of learning for each rule
   //
   private transient HashMap ruleFreq = new edu.cmu.pact.miss.HashMap();

   public /*private*/ void incRuleFreq( String ruleName ) {
       Integer n = (Integer)ruleFreq.get( ruleName );
       if ( n == null ) {
           ruleFreq.put( ruleName, new Integer( 1 ) );
       } else {
           ruleFreq.put( ruleName, new Integer( n.intValue() +1 ) );
       }
   }

   public /*private*/ int getRuleFreq( String ruleName ) {
       Integer n = (Integer)ruleFreq.get( ruleName );
       return ( n == null ) ? 0 : n.intValue();
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Logging rule activation 
   //

   private boolean learningRuleFiringLogged = false;
   private boolean isLogRuleActivationDuringTraining() { return learningRuleFiringLogged; }
   public void setLearningRuleFiringLogged(boolean flag) { learningRuleFiringLogged = flag; }

   private boolean logPriorRuleActivationsOnTraining = false;
   private boolean isLogPriorRuleActivationOnTraining() { return logPriorRuleActivationsOnTraining; }
   public void setLogPriorRuleActivationsOnTraining(boolean flag) {
       logPriorRuleActivationsOnTraining = flag;
   }

   private boolean logAgendaRuleFiring = false;
   public /*private*/ boolean isLogAgendaRuleFiring() { return logAgendaRuleFiring; }
   public void setLogAgendaRuleFiring(boolean flag) { logAgendaRuleFiring = flag; }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Run validation either on the problem basis 
   // or the demonstration basis
   //
   private boolean testOnProblemBasis = true;
   public void setTestOnProblemBasis( boolean flag ) {
       testOnProblemBasis = flag;
   }
   boolean isTestOnProblemBasis() {
       return testOnProblemBasis;
   }
   boolean isTestOnDemonstrationBasis() {
       return !testOnProblemBasis;
   }

   private boolean testOnLastTrainingOnly = false;
   private boolean testOnLastTrainingOnly() {
       return testOnLastTrainingOnly;
   }
   public void setTestOnLastTrainingOnly(boolean flag) {
       testOnLastTrainingOnly = flag;
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Statistics of the model tracing attempts
   // 

   // Number of attempt to model trace
   private  transient HashMap numAttempt = new edu.cmu.pact.miss.HashMap();
   public /*private*/ void incNumAttempt( String ruleName ) {

       Integer n = (Integer)numAttempt.get( ruleName );
       if ( n == null ) {
           numAttempt.put( ruleName, new Integer( 1 ) );
       } else {
           numAttempt.put( ruleName, new Integer( n.intValue() +1 ) );
       }
   }

   public /*private*/ int getNumAttempt( String ruleName ) {

       Integer n = (Integer)numAttempt.get( ruleName );
       return ( n == null ) ? 0 : n.intValue();
   }

   // Number of successful model tracing attempt
   private transient HashMap numSuccess = new edu.cmu.pact.miss.HashMap();
   public /*private*/ void incNumSuccess( String ruleName ) {

       Integer n = (Integer)numSuccess.get( ruleName );
       if ( n == null ) {
           numSuccess.put( ruleName, new Integer( 1 ) );
       } else {
           numSuccess.put( ruleName, new Integer( n.intValue() +1 ) );
       }
   }
   public /*private*/ int getNumSuccess( String ruleName ) {

       Integer n = (Integer)numSuccess.get( ruleName );
       return ( n == null ) ? 0 : n.intValue();
   }

   // Reset statistics on model tracing
   private void resetNumModelTracing() {
       numAttempt = new edu.cmu.pact.miss.HashMap();
       numSuccess = new edu.cmu.pact.miss.HashMap();
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // Test rule activation
   // 

   /*
   private boolean useExternalRuleActivationTest = false;

   public void setUseExternalRuleActivationTest(boolean flag) {
       this.useExternalRuleActivationTest = flag;
   }

   public boolean useExternalRuleActivationTest() {
       return this.useExternalRuleActivationTest;
   }
   */

   private final String RA_TEST_METHOD_HO = "humanOracle";
   private final String RA_TEST_METHOD_TS = "tutoringService";
   private final String RA_TEST_METHOD_CL = "clAlgebraTutor";
   private final String RA_TEST_METHOD_BRD = "BRD";
   private final String RA_TEST_METHOD_VOID = "void";
   public final static String RA_TEST_METHOD_TUTOR_SOLVER = "ClTutorSolver";
   public final static String RA_TEST_METHOD_TUTOR_SOLVERV2 = "builtInClSolverTutor";
   public final static String RA_TEST_METHOD_JESS_ORACLE = "JessOracle";
   public final static String RA_TEST_METHOD_WEBAUTHORING = "WebAuthoring";
   

   // To use another rule-activation test method listed above,
   // you must explicitly specify it in the command line arguments
   // Use "-ssRuleActivationTestMethod" option.
   private String ruleActivationTestMethod = RA_TEST_METHOD_HO;
   
   public String getRuleActivationTestMethod() {
       return this.ruleActivationTestMethod;
   }

   public void setRuleActivationTestMethod(String ruleActivationTestMethod) {
       this.ruleActivationTestMethod  = ruleActivationTestMethod;
   }
   
   
   // To use another quiz grading method listed above,
   // you must explicitly specify it in the command line arguments
   // Use "-ssQuizGradingMethod" option.
   private String quizGradingMethod = RA_TEST_METHOD_TUTOR_SOLVERV2;
   
   public String getQuizGradingMethod() {
       return this.quizGradingMethod;
   }

   public void setQuizGradingMethod(String quizGradingMethod) {
       this.quizGradingMethod  = quizGradingMethod;
   }
   

   /*nbarba 06/06/14: package name was hard-coded to SimStAlgebraV8, now its defined by command line argument.
    * */
   private String packageName="SimStAlgebraV8";
  // private String packageName="SimStFractionAdditionV1";
   
   public  void setPackageName(String packageName){
		   this.packageName=packageName;
   }
   
   public String getPackageName(){
	   return this.packageName;
   }
   
   
   /*production rule file for jess oracle was hardcoded, not it can be set by command line argument*/
   private String jessOracleProductionFile="productionRulesJessOracle.pr";
   
    public  void setSsJessOracleProductionFile(String oracleFile){
 		   this.jessOracleProductionFile=oracleFile;
    }
    
    public String getSsJessOracleProductionFile(){
 	   return this.jessOracleProductionFile;
    }
    
    
    
   
   /*nbarba 06/19/2014: new pr validation method can be defined to be either "cogfi" (i.e. cognitive fidelity), or "modeltracing"
    * for "quiz" mode, use a problems.txt file.
    * */
   private String prValidationMethod="modeltracing";  //if no arqument is defined, modeltracing validation is defined.
   
   public  void setPrValidationMethod(String prValidationMethod){
		   this.prValidationMethod=prValidationMethod;
   }
   
   public String getPrValidationMethod(){
	   return this.prValidationMethod;
   }
   
   
   private String modelTracingValidationOutcomeMethod= MODELTRACING_VALIDATION_METHOD_STRICT;  //if no arqument is defined, modeltracing validation outcome is strict (i.e. T/F C/B/E).
   
   public  void setModelTracingValidationOutcomeMethod(String modelTracingValidationOutcomeMethod){
		   this.modelTracingValidationOutcomeMethod=modelTracingValidationOutcomeMethod;
   }
   
   public String getModelTracingValidationOutcomeMethod(){
	   return this.modelTracingValidationOutcomeMethod;
   }
   

   
   
   private String hintMethod = AskHint.HINT_METHOD_HD;

   public void setHintMethod(String hintMethod) {
       this.hintMethod  = hintMethod;
   }

   public String getHintMethod() {
       return hintMethod;
   }

   public void setSsNumBadInputRetries(int numRetries)
   {
   	this.numBadInputRetries = numRetries;
   }

   public int getSsNumBadInputRetries()
   {
   	return this.numBadInputRetries;
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // Focus of attention:
   // 

   // A list of focus of attention specified for currently
   // demonstrated step.
   // Must be in the form of "WME-type/WME-name/value"
   private transient Vector /* FoA */<FoA> currentFoA = new Vector<FoA>();
   public /* private */ Vector /* FoA */<FoA> getCurrentFoA() { return this.currentFoA; }
   public void addFoA( FoA foa ) {
       this.currentFoA.add( foa );
   }

   private void removeFoA( FoA foa ) {
       this.currentFoA.remove( foa );
   }

   public int numCurrentFoA() { return this.currentFoA.size(); }

   public void addFoaString(String name){   
	  this.currentFoA.add(new FoA(name));
   }
   
   
   // A flag showing if Focus Of Attention is specified for the
   // current step.  Reset when a step is demonstrated.
   // private boolean focusOfAttentionSpecified = false;
   public boolean isFocusOfAttentionSpecified() {
       return !currentFoA.isEmpty();
   }

   // return FOA widget name vector
   public Vector /* String */<String> getFoAWidgetList(){
       Vector<String> foAList = new Vector<String>();
       for (int i = 0; i < numCurrentFoA(); i++) {
           FoA foa = getCurrentFoA().get(i);
           foAList.add(foa.getCommName());
       }

       return foAList;
   }

   // Tutalk parameters
   // Tutalk is disabled by default, unless specifically enabled.
   // Param: flag := experimenter@server[:[opt1,opt2,...,optn]]
   /* @author Huan Truong */
   private boolean tutalkEnabled = false;
   private String tutalkParam;
   public void setUseTutalk(String param) {
	if(trace.getDebugCode("sstt"))trace.out("sstt", "SimStudent Tutalk is ENABLED");
	tutalkEnabled = true;
	tutalkParam = param;
   }

   public boolean getTutalkEnabled() {
	return tutalkEnabled;
   }

   public String getTutalkParam() {
	return tutalkParam;
   }
   // Forget about the recent focus of attention
   public void clearCurrentFoA() {

       // Unhighlight Comm Widgets that have been highlighted (in WebAuthoring mode we do not need to do that)
	   if (!isSsWebAuthoringMode()){
		   for (int i = 0; i < numCurrentFoA(); i++) {
			   FoA foa = getCurrentFoA().get(i);
           	   foa.resetHighlightWidget();
            }
	   }    
       // Empty the FoA list 
       currentFoA.clear();
   }

   /**
    * Add / remove focus of attention.  Toggled by a mouse click (see
    * JCommTable.mouseClickedWhenMissActive())
    *
    * @param className a <code>Class</code> value
    * @param widget an <code>Object</code> value
    **/
   public void toggleFocusOfAttention(Object widget) {

       FoA foa = new FoA(widget);
       if ( getCurrentFoA().contains( foa ) ) {
           removeFoA( foa );
       } else {
           addFoA( foa );
       }
   }

   void printFoa() {
       this.getMTRete().getFacts();
   }

   // Rete network 
   // 
   private transient AmlRete rete = new AmlRete();
   AmlRete getRete() { 
       return rete;
   }
   
   private void initRete( String wmeTypeFile, String initalWmeFile ) {
	   System.out.println(" Initializing Rete ");
       try {
           getRete().reset();
           getRete().readFile( wmeTypeFile );
           getRete().readFile( initalWmeFile );
       } catch (JessException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

  
  
   
   MTRete getMTRete() {
       return getBrController().getModelTracer().getModelTracing().getRete();
   }

   // Domain dependent ad-hoc method to identify focus of attention
   public /*private*/ boolean isFoaGetterDefined() { return foaGetterClassDefined; }
   private boolean foaGetterClassDefined = false;
   // private boolean foaGetterClassDefined = true;
   public void setFoaGetterClassDefined( boolean flag ) {
       foaGetterClassDefined = flag;
   }
   private transient FoaGetter foaGetter = null;
   private boolean isFoaClickDisabled = false;

   public void setSsFoaGetter(String foaGetterClassName) {
       try {
           Class foaGetterClass = Class.forName(foaGetterClassName);
           this.foaGetter = (FoaGetter)foaGetterClass.newInstance();
           setFoaGetterClassDefined(true);
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }
   
   
   
   

   /* resource getter
   public boolean isResourceGetterDefined() { return foaGetterClassDefined; }
   private boolean resourceGetterClassDefined = false;
   public void setResourceGetterClassDefined( boolean flag ) {
       resourceGetterClassDefined = flag;
   }
   private transient ResourceGetter resourceGetter = null;

   public void setSsResourceGetter(String resourceGetterClassName) {
       try {
           Class resourceGetterClass = Class.forName(resourceGetterClassName);
           this.resourceGetter = (ResourceGetter)resourceGetterClass.newInstance();
           setResourceGetterClassDefined(true);
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }
   */
   
   
   
   
   /*
    * Added by Rohan. Implements an Iterative Deepening Search algorithm
    * based upon a heuristic (count of the number of times an operator has
    * been applied)
    */
   private boolean heuristicBasedIDS = false;

   // Domain dependent ad-hoc method to identify the next probable selection
   public /*private*/ boolean isSelectionOrderGetterDefined() { return selectionOrderGetterClassDefined; }
   private boolean selectionOrderGetterClassDefined = false;
   private void setSelectionOrderGetterClassDefined( boolean flag ) {
       selectionOrderGetterClassDefined = flag;
   }
   private transient SelectionOrderGetter selectionOrderGetter = null;

   public void setSsSelectionGetter(String sogClassName) {
   	try {
           Class selectionOrderGetterClass = Class.forName(sogClassName);
           this.selectionOrderGetter = (SelectionOrderGetter)selectionOrderGetterClass.newInstance();
           setSelectionOrderGetterClassDefined(true);
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   public SelectionOrderGetter getSelectionOrderGetter()
   {
   	return selectionOrderGetter;
   }


   /** Gets the interface-dependent information like the start-state elements, component names  */
   private boolean interfaceElementGetterClassDefined = false;
   public boolean isInterfaceElementGetterClassDefined() {
		return interfaceElementGetterClassDefined;
	}

	private transient Class interfaceElementGetterClass;
	public Class getInterfaceElementGetterClass() {
		return this.interfaceElementGetterClass;
	}
	
	private transient InterfaceElementGetter interfaceElementGetter = null;
	public InterfaceElementGetter getInterfaceElementGetter() {
		return interfaceElementGetter;
	}
	public void setSsInterfaceElementGetter(String interfaceElementGetterClassName) {
		try {
			interfaceElementGetterClass = Class.forName(interfaceElementGetterClassName);
			interfaceElementGetter = (InterfaceElementGetter)interfaceElementGetterClass.newInstance();
			interfaceElementGetterClassDefined = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/* 
    * This is done to decouple the interface dependent code
    * from the existing code base. The sai can vary depending upon if the
    * interface is a 1 table multiple column format or 3 table single column
    * format.
    */
   private boolean saiConverterClassDefined = false;

   private void setSaiConverterClassDefined(boolean flag){
   	saiConverterClassDefined = flag;
   }

   public boolean isSaiConverterDefined(){
   	return saiConverterClassDefined;
   }

	private transient Class saiConverterClass;
	public Class getSaiConverterClass(){
		return this.saiConverterClass;
	}

	private transient SAIConverter saiConverter = null;
	
	public SAIConverter getSAIConverter(){
		return this.saiConverter;
	}
	
   public void setSsSaiConverter(String saiConverterClassName) {

		try {
			saiConverterClass = Class.forName(saiConverterClassName);
			this.saiConverter = (SAIConverter) saiConverterClass.newInstance();
			setSaiConverterClassDefined(true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.simStLogException(e);
		}
   }

   private boolean heuristicBasedIDSearch = false;

   public void setSsHeuristicBasedIDS(boolean flag) {
   	heuristicBasedIDSearch = flag;
   }

   private boolean isHeuristicBasedIDS() {
   	return heuristicBasedIDSearch;
   }

   public void setSsFoaClickDisabled(boolean flag)
   {
   	isFoaClickDisabled = flag;
   }

   public boolean isSsFoaClickDisabled()
   {
   	return isFoaClickDisabled;
   }

   // Domain dependent method to check validity of input
   public boolean isInputCheckerDefined() { return inputCheckerClassDefined; }
   private boolean inputCheckerClassDefined = false;
   public void setInputCheckerClassDefined( boolean flag ) {
       inputCheckerClassDefined = flag;
   }
   private transient InputChecker inputChecker = null;

   public void setSsInputChecker(String inputCheckerClassName) {
	   
	  
       try {
           Class inputCheckerClass = Class.forName(inputCheckerClassName);
           this.inputChecker = (InputChecker)inputCheckerClass.newInstance();
           setInputCheckerClassDefined(true);
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }


   public boolean isStartStateCheckerDefined() { return startStateCheckerClassDefined; }
   private boolean startStateCheckerClassDefined = false;
   public void setStartStateCheckerClassDefined( boolean flag ) {
	   startStateCheckerClassDefined = flag;
   }
   private transient StartStateChecker startStateChecker = null;
   public void setStartStateChecker(String startStateCheckerClassName) {  
       try {
           Class inputCheckerClass = Class.forName(startStateCheckerClassName);
           this.startStateChecker  = (StartStateChecker) inputCheckerClass.newInstance();
           setStartStateCheckerClassDefined(true);
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }
   public StartStateChecker getStartStateChecker() {
	   	return this.startStateChecker;
	}
   
  /* public boolean isSolvable(String oracleClass,String problemName,BR_Controller brController){
		boolean answer = true;
		
		
		SimStProblemGraph brGraph = new SimStProblemGraph();
	    SimStNode problemNode = new SimStNode(SimSt.convertToSafeProblemName(problemName),brGraph);
		brGraph.addSSNode(problemNode);
		brController.getProblemModel().setStartNode(problemNode);
		
		
		
		
		Class[] parameters = new Class[3];
		parameters[0] = String.class;
		parameters[1] = ProblemNode.class;
		parameters[2] = BR_Controller.class;
		
		
		try {
			Class oracle = Class.forName("edu.cmu.pact.miss."+oracleClass);
			Object oracleObj = oracle.newInstance();
			Method askMethod = oracle.getMethod("askNextStep",parameters);
			//System.out.println(" before while loop : "+answer);
			while(answer){
				//System.out.println("Inside the while loop");
				//System.out.println("Next step for node : "+problemNode.toString());
				Sai nextStep = (Sai)askMethod.invoke(oracleObj,problemName,problemNode,brController);
				if(nextStep == null)
					return false;
				if(nextStep.getI().equalsIgnoreCase("donenosolution"))
					return false;
				else if(nextStep.getI().equalsIgnoreCase("done") || nextStep.getS().equalsIgnoreCase("done"))
					break;
				else{
					SimStNode nextNode = new SimStGraphNavigator().simulatePerformingStep(problemNode,nextStep);	
					problemNode = nextNode;
					//System.out.println(" Next Node : "+nextStep.toString());
				}
								
			}
			//System.out.println(" After the while loop ");
			brGraph.clear();
			brController.createStartState(problemName);
			//SimStNode previous = new SimStNode(SimSt.convertToSafeProblemName(problemName),brGraph);
			//brController.getProblemModel().setStartNode(previous);
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProblemModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			brGraph.clear();
		}
		 
		
		return answer;
	}*/
   
   
   /* @author jinyul */
   // Domain dependent ad-hoc method to identify skill name
   public /*private*/ boolean isSkillNameGetterDefined() { return skillNameGetterClassDefined; }
   private boolean skillNameGetterClassDefined = false;
   //private boolean skillNameGetterClassDefined = true;
   public void setSkillNameGetterClassDefined(boolean flag) {
   	skillNameGetterClassDefined = flag;
   }
   private transient SkillNameGetter skillNameGetter = null;
   public void setSsSkillNameGetter(String skillNameGetterClassName) {
   	try {
   		if(trace.getDebugCode("miss"))trace.out("miss","DEBUG: "+skillNameGetterClassName);
   		Class skillNameGetterClass = Class.forName(skillNameGetterClassName);
   		this.skillNameGetter = (SkillNameGetter)skillNameGetterClass.newInstance();
   		setSkillNameGetterClassDefined(true);
   	} catch (Exception e) {
   		e.printStackTrace();
           logger.simStLogException(e);
   	}
   }


   public /*private*/ boolean isPathOrdererDefined() { return pathOrderingClassDefined; }
   private boolean pathOrderingClassDefined = false;
   public void setPathOrderingClassDefined( boolean flag ) {
       pathOrderingClassDefined = flag;    }
   private transient PathOrderer pathOrderer = null;
   public void setSsPathOrderer(String pathOrderingClassName) {
       try {
           Class pathOrderingClass = Class.forName(pathOrderingClassName);
           this.pathOrderer = (PathOrderer)pathOrderingClass.newInstance();
           setPathOrderingClassDefined(true);
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }


   public FoaGetter getFoaGetter() {
       return this.foaGetter;
   }



   
   public InputChecker getInputChecker() {
   	return this.inputChecker;
   }


   public void invalidInput(String selection, String input,String corrected, String[] foas)
   {

   	String message = "";
   	if(corrected == null || corrected.length() == 0)
   	{
	    	if(this.isInputCheckerDefined())
	    	{
	    		message = getInputChecker().invalidInputMessage(selection, input, null);
				if(getMissController().isPLEon())
					getMissController().getSimStPLE().giveDialogMessage(message);
				else
					displayMessage("",message);
	    	} 
			else
			{
				message = BAD_INPUT_MESSAGE;
				if(getMissController().isPLEon())
					getMissController().getSimStPLE().giveDialogMessage(message);
				else
					displayMessage("",message);
			}
	    	
	    	

	    	
	    	//if(getMissController().getSimStPLE() != null && !getBrController().isAcceptingStartStateMessages())
	    		//getMissController().getSimStPLE().unblockOnly(selection);
	    	
	    	
	    	
	    	
   	}
       //if(isSsMetaTutorMode()) {
       //	getModelTraceWM().getEventHistory().add(0, getModelTraceWM().new Event(SimStLogger.BAD_INPUT_RECEIVED));
       //}
   	//logger.simStLog(SimStLogger.SIM_STUDENT_INFO_RECEIVED, SimStLogger.BAD_INPUT_RECEIVED, getProblemStepString(), corrected, "", new Sai(selection, "Inputted", input),0,message);
   }

   /* @author jinyul */
   public SkillNameGetter getSkillNameGetter() {
   	return this.skillNameGetter;
   }
   /**
    * Inner class representing a WME inside the focus of attention
    */
   public class FoA {

       // - FoA
       // - Fields - - - - - - - - - - - - - - - - - - - - - -
       // -

       // FoA is either CommWidet, TableCell (which is a subclass of JTextField),
       // or SsFoaElement
       private Object widget=null;
       Object getWidget() { return this.widget; }

       // private Class className;
       Class<? extends Object> getWidgetClass() { return this.widget.getClass(); }

       private String commName = null;
       String getCommName() {
           return this.commName;
       }

       // - FoA
       // - Constructor - - - - - - - - - - - - - - - - - - - -
       // -
       FoA(Object widget) {
           this.widget = widget;
           if(trace.getDebugCode("foagetter"))trace.out("foagetter", "FoA constructor: className = " + getWidgetClass().toString());           
           
           Class[] argTypes = null;
           Object[] args = null;
           Method getCommNameMethod = null;
           try {
        	   if(!runType.equals("springBoot")) {
        		   getCommNameMethod = getWidgetClass().getMethod( "getCommName", argTypes );
                   this.commName = (String)getCommNameMethod.invoke( getWidget(), args );
        	   } else {
        		   this.commName = (String) widget;
        	   }
               
           } catch (Exception e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }
                   
       }


       
       /**/
       FoA(String name){
    	   this.commName=name;

       }
       
       
       
       // - FoA
       // - Methods - - - - - - - - - - - - - - - - - - - - - -
       // -

       // Returns a value of the comm widget
       String getValue() {
         //  return isSsFoaElement() ? getValueSsFoaElement() : getValueCommWidget();
   
    	   if (this.widget==null) return getValueCommWidget();
    	   if(runType.equals("springBoot")) {
    		   String value = getBrController().getWidgetTable(this.widget.toString());
    		   return value;
    	   }
    	   
    	   return this.widget==null? getValueCommWidget() : (isSsFoaElement() ? getValueSsFoaElement() : getValueCommWidget() );
    	   
    	   
       }

       boolean isSsFoaElement(){
    	   if (widget==null) return false;
           String className = getWidgetClass().toString();
           return className.equals("class edu.cmu.pact.miss.SsFoaElement");
       }

       /**
        * read the "value" off an ssFoaElement object
        */
       private String getValueSsFoaElement() {

           SsFoaElement ssFoaElement = (SsFoaElement)getWidget();
           return ssFoaElement.getValue();
           
       }

       /**
        * Read the "value" off a JCommWidget object. 
        */
       private String getValueCommWidget() {

           // The value should be read from a commWidget in the working memory,
           // instead of an interface object
           String value = null;
           JessModelTracing jmt = getBrController().getModelTracer().getModelTracing();
           MTRete rete = jmt.getRete();
           Fact fact = rete.getFactByName(getCommName());
           try {
               Value v1 = fact.getSlotValue("value");
               String v2 = v1.toString();
               value = v2.replaceAll("\"", "");
           } catch (JessException e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }

           return value;
       }

       // Invoke resetHighlightWidget() method for the widget
       void resetHighlightWidget() {
           if (getStudentInterfaceClass() != null) {
               Method resetHighlightWidget = null;
               try {
                   //Changed to removeHighlight since it is defined in commWidget
                   resetHighlightWidget = getWidgetClass().getMethod( "removeHighlight", (Class[])null );
                   resetHighlightWidget.invoke( getWidget(), (Object[])null );
               } catch (Exception e) {
                   e.printStackTrace();
                   logger.simStLogException(e);
               }
           }
       }

       // Returns a string that must be fed to SimSt.  Focus of
       // Attention must be in the form of "WME-type/WME-name/value",
       // but since the WME-type is unknown at this point, it is
       // specified as "*"
       public String foaString() {
           // getRete(), which returns AmlRete, is used only to get 
           // the WmeType.  The actual value of the Wme is read from
           // a genuine WME, which is maintained by the MTRete

           AmlRete rete = getRete();
           String wmeType = rete.wmeType( getCommName() );

           return wmeType + "|" + getCommName() + "|" + getValue();
       }

       // Two FoA's are equal when they are referreing to the same
       // Comm Widget
       public boolean equals( Object foa ) {
           return ((FoA)foa).getCommName().equals( getCommName() );
       }

       public String toString() {
           return foaString();
       }

   } // end of an inner-class FoA

   /*
   public void resetFocusOfAttentionSpecified() {
   focusOfAttentionSpecified = false;
   }
   public void setFocusOfAttentionSpecified() {
   focusOfAttentionSpecified = true;
   }
    */

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // FOIL Data:: A data collection to compile an input data file for
   // FOIL to run on a particular predicate
   //
   private final String FOIL_LOG = "foil-log";
   private String foilLogDir = FOIL_LOG;
   public void setFoilLogDir( String foilLogDir ) {
       this.foilLogDir = foilLogDir;
   }
   private String getFoilLogDir() { return this.foilLogDir; }

   private Hashtable<String, FoilData> foilDataHash = new Hashtable<String, FoilData>();

   
   //08/05/2014 nbarba:  To setup foil max tuples using command line argument ssFoilMaxTuples. 
   // Default value uses the default hardcoded value in foil source code
   public static String USE_DEFAULT_TUPLES="Use default foil tuples";
   private String foilMaxTuples = USE_DEFAULT_TUPLES;
   public void setFoilMaxTuples( String foilMaxTuples ) {
       this.foilMaxTuples = foilMaxTuples;
   }
   private String getFoilMaxTuples() { return this.foilMaxTuples; }



   // Get a corresponding FoilData for name.  If non exists, then
   // create a new one for the name with the arity, and store it into
   // foilData hash.
   private FoilData getFoilData( String name, int arity ) {
       return getFoilData(name);
   }

   FoilData getFoilData( String name ) {
       FoilData foilData = foilDataHash.get( name );
       return foilData;
   }

   /*
   //24 April 2007
   //if there is no FoilData for the skillname, create one and return that.
   FoilData getNonNullFoilData(String skillname, Instruction instruction){
       int arity = instruction.numFocusOfAttention() -1;
       FoilData foilData = getFoilData( skillname, arity ); //equivalent to getFoilData(name);
       if (foilData == null) { //if there is no FoilData for this skillname, make one
           initializeFoilDataFor( skillname );
       }
       foilData = getFoilData( skillname, arity );
       return foilData;
   }
   */

   private Enumeration<FoilData> getAllFoilData() { return foilDataHash.elements(); }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
   // Rules:
   //
   private HashMap /* Rule */ rules = new edu.cmu.pact.miss.HashMap();

   // Returns a Rule that has a specified name
   public Rule getRule( String name ) { return (Rule)this.rules.get(name); }

   // Add a new rule in the hash table
   public /*private*/ void addRule( Rule rule ) {
       this.rules.put( rule.getName(), rule );
   }

   // Returns a number of Production Rules generated so farr (stored in the rule hash table)
   private int numRules() { return this.rules.size(); }
   // Returns a number of rules as a three digits number
   private String prettyNumRules() { 
       String prettyNumRules = "000";
       prettyNumRules += numRules();
       int numLen = prettyNumRules.length();
       return prettyNumRules.substring( Math.min( numLen - 3, 3 ) );
   }

   public Set getRuleNames() { return rules.keySet(); }

   // Returns an Iterator of the Rules induced
   private Iterator getAllRules() { return this.rules.values().iterator(); }

   public void removeRule(String ruleName)
   {
   	Rule rule = (Rule) (rules.get(ruleName));
   	Vector<Instruction> instList = instructions.get(ruleName);
   	rules.remove(ruleName);
   	instructions.remove(ruleName);
   	
   	 RuleActivationTree tree = getBrController().getRuleActivationTree();
        TreeTableModel ttm = tree.getActivationModel();
        RuleActivationNode root = (RuleActivationNode) ttm.getRoot();
            	
   	MTRete mtRete = getBrController().getModelTracer().getRete();
   	    	
       try {
			mtRete.unDefrule("MAIN::"+ruleName);
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		root.saveState(mtRete);
		trace.out("ss", "Removing "+rule.getName()+" "+rule.getAcceptedRatio()+" ("+instList.size()+" instructions)");
   }

   public HasLHS findRule(String ruleName){
	   return getBrController().getModelTracer().getRete().findDefrule(ruleName);
   }
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Communication with Miss Console
   //
   private static MissController missController = null;
   public /*private*/ MissController getMissController() {
	   return SimSt.missController /*= SimSt.missControllerTable.get(this);
	   return SimSt.missControllerTable.get(this)*/; 
	   
   }
   private void setMissController( MissController missController ) {
	  // SimSt.missControllerTable.put(this,missController);
       SimSt.missController = missController;
   }
   public static boolean isMissControllerSet() {
       return missController != null;
   }

   public BR_Controller getBrController() {
       return getMissController().getBrController();
   };
   
   
  /* public static Hashtable<SimSt,MissController> missControllerTable= new Hashtable<SimSt,MissController>();
   
   public static void setMissController(SimSt simst){
	   if(simst != null)
		   SimSt.missController= SimSt.missControllerTable.get(simst);
   }*/

   private transient SimStRete ssRete = null;

   private void setSsRete(SimStRete ssRete) {
		this.ssRete = ssRete;
	}
	public SimStRete getSsRete() {
		return ssRete;
	}
	
	
   private transient JessOracleRete oracleRete = null;

   private void setJessOracleRete(JessOracleRete oracleRete) {
		this.oracleRete = oracleRete;
	}
	public JessOracleRete JessOracleRete() {
		return oracleRete;
	}
	
	
	private transient SimStProblemGraph validationGraph = null;
	private void setValidationGraph(SimStProblemGraph graph)
	{
		validationGraph = graph;
	}
	public SimStProblemGraph getValidationGraph()
	{
		return validationGraph;
	}

	
	private transient SimStProblemGraph quizGraph = null;
	public void setQuizGraph(SimStProblemGraph graph)
	{
		quizGraph = graph;
	}
	public SimStProblemGraph getQuizGraph()
	{
		return quizGraph;
	}
	
	
	private transient ModelTraceWorkingMemory modelTraceWM = null;

	public ModelTraceWorkingMemory getModelTraceWM() {
		//System.out.println("Model Trace Working Memory : "+modelTraceWM);
		return modelTraceWM;
	}
	public void setModelTraceWM(ModelTraceWorkingMemory modelTraceWM) {
		this.modelTraceWM = modelTraceWM;
	}

	// A log files that holds results of validation test for
   // Sim. Student
   private String TEST_MODEL_LOG_FILE = "test-ProductionRules.txt";
   private String testLogFile = getProjectDir() + "/" + TEST_MODEL_LOG_FILE;
   void setTestLogFile( String logFile ) {
       this.testLogFile = logFile;
   }
   String getTestLogFile() { return testLogFile; }

   // A log file to record results of model tracing during learning
   private final String LEARNING_LOG_FILE = "mt-learning.txt";
   private String learningLogFile = getProjectDir() + "/" + LEARNING_LOG_FILE;
   public void setLearningLogFile(String learningLogFile) {
       this.learningLogFile = learningLogFile;
   }
   private String getLearningLogFile() { return learningLogFile; }


   //29Sep2006: this parameter determines whether SimSt will learn from CLT error actions.
   //It is by default false. We use this to make an experiment in which SimStudent only learns from
   //CLT error actions.
   private boolean learnCltErrorActions = false;
   public void setLearnCltErrorActions (boolean b){
       this.learnCltErrorActions = b; }
   public boolean getLearnCltErrorActions() { return this.learnCltErrorActions; }


   //3Oct2006: this parameter determines whether SimSt will learn from buggy actions.
   //It is by default true.
   private boolean learnBuggyActions = false;
   public void setLearnBuggyActions (boolean b){
       this.learnBuggyActions = b; }
   public boolean getLearnBuggyActions() { return this.learnBuggyActions; }


   //29Sep2006: this parameter determines whether SimSt will learn from correct actions.
   //It is by default true.
   private boolean learnCorrectActions = true;
   public void setLearnCorrectActions (boolean b){
       this.learnCorrectActions = b; }
   public boolean getLearnCorrectActions() { return this.learnCorrectActions; }

   public static boolean WEBSTARTENABLED = false;
   public boolean WebStartMode = false;

   public boolean isWebStartMode() {
		return WebStartMode;
	}
	public void setWebStartMode(boolean webStartMode) {
		WebStartMode = webStartMode;
		WEBSTARTENABLED = webStartMode;
		FoilData.WEBSTARTMODE = webStartMode;
		if(trace.getDebugCode("miss"))trace.out("miss", "WebStartMode set to: " + webStartMode + "   WEBSTARTENABLED: " + webStartMode + 
				" FoilData.WEBSTARTMODE: " + webStartMode);
	}

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // States in the BR
   //
//  private String startStateName = "";
//  // private String getStartStateName() { return this.startStateName; }
//  private void setStartStateName( String name ) {
//  startStateName = name;
//  }


	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // For Wilkinsburg 2005 Algebra I study
   // Specify whether a BRD file fed to a batch run must be
   // verified if it's not a bad BRD or not
   private boolean checkWilkinsburgBadBrdFile = false;
   public void setCheckWilkinsburgBadBrdFile(boolean flag) {
       this.checkWilkinsburgBadBrdFile = flag;
   }
   public boolean isCheckWilkinsburgBadBrdFile() {
       return checkWilkinsburgBadBrdFile;
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Timer to terminate SimSt batch run when it took too long
   // 
   private static boolean isTimed = false;
   private void setIsTimed( boolean flag ) { SimSt.isTimed = flag; }
   public /*private*/ static boolean isTimed() {
       return isTimed;
   }

   // Duration to time out (in millisecond)
   // Set by SingleSessionLauncher "-ssSetTimeout" option
   // 
   private static long timeoutDuration=60000;
   public void setTimeoutDuration( long timeoutDuration ) {
   	if(trace.getDebugCode("miss"))trace.out("miss", "setTimeoutDuration(" + timeoutDuration + ")");
       setIsTimed(true);
       SimSt.timeoutDuration = timeoutDuration;
   }
   private static long getTimeoutDuration() { return timeoutDuration; }  
   
   // Duration to time out (in millisecond)
   // If -1, no timeout
   // 
   private static long serverTimeoutDuration = -1;
   public void setServerTimeoutDuration( long timeoutDuration ) {
       SimSt.serverTimeoutDuration = timeoutDuration;
   }
   static long getServerTimeoutDuration() { return serverTimeoutDuration; }

   private static long simStTimer;
   private static long getSimStTimer() { return simStTimer; }
   void resetSimStTimer() {
       setSearchTimeOut(false);
       SimSt.simStTimer = System.currentTimeMillis();
   }

   public static boolean isRunningOutOfTime(String id) {

       boolean isTimeOut = false;

       if (isTimed()) {
           long duration = System.currentTimeMillis() - getSimStTimer(); 
           isTimeOut = (duration > getTimeoutDuration());
           if (isTimeOut) {
           	if(trace.getDebugCode("miss"))trace.out("miss", id + " search has gotten time out...");
               setSearchTimeOut(true);
           }
       }
       return isTimeOut;
   }

   private static boolean isSearchTimeOut = false;

   public boolean isSearchTimeOut() {
       return SimSt.isSearchTimeOut;
   }
   public static void setSearchTimeOut(boolean isSearchTimeOut) {
       SimSt.isSearchTimeOut = isSearchTimeOut;
   }

 
   /*  Duration (in millisecond) that APLUS is allowed to be inactive 
    *  Set by SingleSessionLauncher "-ssSetInactiveInterfaceTimeout" option. Default value is 5 minutes.
    */ 
    private static long inactiveInterfaceTimeoutDuration=300000;
    public void setInactiveInterfaceTimeoutDuration( long inactiveInterfaceTimeoutDuration ) {
    	setIsInterfaceTimed(true);
        SimSt.inactiveInterfaceTimeoutDuration = inactiveInterfaceTimeoutDuration;
    }
    private long getInactiveInterfaceTimeoutDuration() { return inactiveInterfaceTimeoutDuration; }
    
    /* Boolean indicating if inactiveInterfaceTimeoutDuration is defined from command line argument. 
     */
    private static boolean isInterfaceTimed = false;
    private void setIsInterfaceTimed( boolean flag ) { SimSt.isInterfaceTimed = flag; }
    public static boolean isInterfaceTimed() {
        return isInterfaceTimed;
    }
    
    
    /*Timer to block New Problem button for 1 second after restart*/
 
    private static long newProblemButtonTimer=1000;
    public boolean newProblemButtonLockFlag=true;
    /**
     * Method that schedules a new timer to performa a task after "x". Interval of "x" seconds
     * is defined by "-ssSetInactiveInterfaceTimeout" option.
     */
    public void scheduleNewProblemTimer(){
    	Timer timer=new Timer();
    	timer.schedule( new NewProblemTimer(this.getBrController()),newProblemButtonTimer);    	
    }
    
   class NewProblemTimer extends TimerTask{
	   BR_Controller controller;
     public NewProblemTimer(BR_Controller brController){
    	 this.controller=brController;
     }
	   
	@Override
	public void run() {
		// TODO Auto-generated method stub
		controller.getMissController().getSimSt().newProblemButtonLockFlag=true;
		
	}
	   
   }
    
    
    
    /* Timer to keep track how much time has passed since last time an action was taken on APLUS
     */  
    private static long simStInactiveInterfaceTimer=5000;
    private static long getSimStInactiveInterfaceTimer() { return simStInactiveInterfaceTimer; }
    public void resetSimStInactiveInterfaceTimer() {
        SimSt.simStInactiveInterfaceTimer = System.currentTimeMillis();
    }

     
    /**
     * Boolean method comparing current time and the simStInactiveInterface timer 
     * @return true if APLUS inactive for "inactiveInterfaceTimeoutDuration" miliseconds
     */
    public  boolean isInterfaceInactiveForTooLong() {
        boolean isTimeOut = false;
        long duration = System.currentTimeMillis() - getSimStInactiveInterfaceTimer(); 
        isTimeOut = (duration > getInactiveInterfaceTimeoutDuration());     
        if (!isTimeOut){
        	  //if no timeout occured, invoke timer again so we can check again
        	   this.scheduleInterfaceInactivenessTimer();
        }
        
       return isTimeOut;
    }

   
    /**
     * Actual method that called when APLUS is inactive for too long. 
     * @todo 
     */
    private void inactivationAction(){   	
    	//JOptionPane.showMessageDialog(null,this.getBrController().getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getYesResponseButton().isVisible());
    	
    	String requestType=this.getModelTraceWM().getRequestType();
    	
		
    	if (getBrController().getCurrentNode()==null){	
    		//JOptionPane.showMessageDialog(null,"no tutoring");
    		logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_START, SimStLogger.APLUS_INACTIVE_WINDOW_NO_TUTORING);
    		SimStTimeoutDlg dlg=new SimStTimeoutDlg(getBrController());
    	}
    	else{
    		
    		AskHint hint=this.askForHintQuizGradingOracle(getBrController(), getBrController().getCurrentNode());
    		String typeOfStep= getFoaGetter().getTypeOfStep(hint.getSelection(),getBrController());
    		if (requestType!=null && requestType.contains("feedback")){
    			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_START, SimStLogger.APLUS_INACTIVE_WINDOW_FEEDBACK);
        		SimStTimeoutDlg dlg=new SimStTimeoutDlg(getBrController(),false);
        	}
        	else if (typeOfStep.equals(SimStTimeoutDlg.TYPE_TRANSFORMATION)){
        		logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_START, SimStLogger.APLUS_INACTIVE_WINDOW_HINT_TRANSFORMATION);
    			SimStTimeoutDlg dlg=new SimStTimeoutDlg(getBrController(),true,true, SimStTimeoutDlg.reasoningForTransformation);
        	}
        	else {
        		String skill=getFoaGetter().getStepSkill(hint.getSelection(),getBrController());
        		logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_START, SimStLogger.APLUS_INACTIVE_WINDOW_HINT_TYPEIN);
        		SimStTimeoutDlg dlg=new SimStTimeoutDlg(getBrController(),true,true,"how to " + skill);
        	}
    		
    		
    		
    		
    	/*	AskHint hint=this.askForHintQuizGradingOracle(getBrController(), getBrController().getCurrentNode());
    		String message=getFoaGetter().formulateBasedOnTypeOfString(hint.getSelection(),getBrController(),SimStTimeoutDlg.reasoningForTransformation);
        		String skill=getFoaGetter().getStepSkill(hint.getSelection(),getBrController());
        		logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_START, SimStLogger.APLUS_INACTIVE_WINDOW_HINT_TYPEIN);
        		SimStTimeoutDlg dlg=new SimStTimeoutDlg(getBrController(),true,true,message);
        */
    		
    		
    		
    		
    	}
    	
    	this.scheduleInterfaceInactivenessTimer();  	
    }
    
    
    /**
     * Method that schedules a new timer to performa a task after "x". Interval of "x" seconds
     * is defined by "-ssSetInactiveInterfaceTimeout" option.
     */
    public void scheduleInterfaceInactivenessTimer(){
    	Timer timer=new Timer();
    	timer.schedule( new SimStudentTimerTask(this.getBrController()), this.getBrController().getMissController().getSimSt().getInactiveInterfaceTimeoutDuration());    	
    }
       
    /**
     * Method invoked by the TimerTask (i.e. task that will be performed when timer reaches time interval). 
     */
    private void handleInterfaceInactiveness(){
    	if (isInterfaceInactiveForTooLong())
    			inactivationAction();
    }
  
    
    


    /**
     * Inner class extending the TimerTask, so we can pass the SimStudent object to the 
     * default run function (which is executed when timer interval arrives).
     *     
     * @author nbarba
     *
     */
    class SimStudentTimerTask extends TimerTask  {
    	BR_Controller brController;
    	private void setBrController(BR_Controller brController){ this.brController=brController;	}
    	private BR_Controller getSimSt(){return this.brController;}

    	public SimStudentTimerTask(BR_Controller brController) {
    		setBrController(brController);
    	}

    	@Override
    	public void run() {       	
    		getBrController().getMissController().getSimSt().handleInterfaceInactiveness();
    	}

    }       
    
   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Name of the text file that defines a list of comm widgets.  
   // Used for SimSt running in batch mode without a tutor interface
   // 
   private static String PSEUDO_WIDGET_FILE = "widgets.txt";

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Number of training problems
   // 
   private int MAX_NUM_TRAINING = Integer.MAX_VALUE;
   public void setMaxNumTraining(int maxNumTraining) { MAX_NUM_TRAINING = maxNumTraining; }
   private int numTrained = 1;
   private void resetNumTrained() { numTrained = 1; }
   private void incNumTrained() { numTrained++; }
   private int getNumTrained() { return numTrained; }
   private String prettyGetNumTrained() {
       String prettyNum = "000" + getNumTrained();
       return prettyNum.substring( Math.min(prettyNum.length() -3, 3));
   }

   // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
   // Number of test problems
   //
   public /*private*/ int MAX_NUM_TEST = Integer.MAX_VALUE;
   public void setMaxNumTest(int maxNumTest) { MAX_NUM_TEST = maxNumTest; }
   private int numTested = 0;
   private void resetNumTested() { numTested = 0; }
   public /*private*/ void incNumTested() { numTested++; }
   public /*private*/ int getNumTested() { return numTested; }

   

   // -
   // - Constructor - - - - - - - - - - - - - - - - - - - - - - - -
   // - 


   public SimSt(){}

   /**
    * Creates a new <code>SimSt</code> instance.
    *
    */
   public SimSt( MissController missController ) {

       String format = "MMMMMMMMM dd, yyyy KK:mm:ss a";
       SimpleDateFormat dateFormat = new SimpleDateFormat( format, Locale.US );
       if(trace.getDebugCode("miss"))trace.out("miss", "----------------------------------------");
       if(trace.getDebugCode("miss"))trace.out("miss", "Simulated Student Ver. " + VERSION );
       if(trace.getDebugCode("miss"))trace.out("miss", dateFormat.format( new Date() ) );
       if(trace.getDebugCode("miss"))trace.out("miss", "OS: " + System.getProperty("os.name"));
       if(trace.getDebugCode("miss"))trace.out("miss", "----------------------------------------\n");

       altSug= new Vector<RuleActivationNode>();

       // Set Miss Controller
       setMissController( missController );
       setSsRete(new SimStRete(getBrController()));   
       getBrController().getModelTracer().setExternalRete(this.ssRete); 
       setJessOracleRete(new JessOracleRete(getBrController()));
       logger = new SimStLogger(missController.getBrController());
        
       // Set "Home" dir
       URL codeBase = Utils.getCodeBaseURL( getClass() );
       trace.out ("codebase = " + codeBase);

       URI codeURI = null;
       try {
           codeURI = new URI( codeBase.getFile() );
       } catch (URISyntaxException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
       trace.out ("uri = " + codeURI);
       File codeFile = new File(codeURI.toString());
       // Get "../../" of the codeFile
       String codeDir = codeFile.getParentFile().getParent();
       codeDir = codeDir.replace('\\','/');
       if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")) {
           // Windows has "file:/F:/foo/bar/... as the codeDir
           codeDir = codeDir.replaceFirst("file:/","");
       } else {
           // Mac OS X has "file:/foo/bar/... as the codeDir
           codeDir = codeDir.replaceFirst("file:","");
       }

       
    
      
       //5 June 2007: needed because there are spaces inside "Program Files/Cognitive Tu.../"
       setHomeDir( codeDir ); //strip out the url protocol
       trace.out("HomeDir = " + getHomeDir() );
       if(trace.getDebugCode("miss"))trace.out("miss", "ProjectDir = " + getProjectDir() );

       // Initialize default background knowledge
       checkWebStartMode(codeDir);
       initBackgroundKnowledge();
   	extractFilesFromJar();
   }

   public void setUpFoilDir(SimSt ss) {
   	
   	if(ss != null) {
	    	Hashtable<String, FoilData> foilHash = ss.foilDataHash;
	    	Collection c = foilHash.values();
	    	Iterator itr = c.iterator();
	    	
	    	while(itr.hasNext()) { 
	    		FoilData foilData = (FoilData)itr.next();
	    		// Set up the foil-log and foil6.exe location
	    		if(isSsWebAuthoringMode()){
	    			foilData.setFoilDir();
	    			String foilLogDir = getUserBundleDirectory() + "/" + getFoilLogDir() +  "/";
		    		foilData.setFoilLogDir(foilLogDir);
	    		}
	    		else if(!isWebStartMode()) {
		    		foilData.setFoilDir();
		    		String foilLogDir = getProjectDir() + "/" + getLogDirectory() +  "/" + getUserID() + "-" + getFoilLogDir() +  "/";
		    		foilData.setFoilLogDir(foilLogDir);
	    		} else {
	    			foilData.setFoilLogDir(WebStartFileDownloader.SimStWebStartDir+FOIL_LOG + "_" + getUserID() + 
	    					"_" + FileZipper.formattedDate()+System.getProperty("file.separator"));
	    		}
	    	}
   	}
   }

   
   
   public void checkWebStartMode(String codeDir) {

   	if(codeDir.contains("http:"))
			setWebStartMode(true);
   }

   // Extracts the necessary files from the jar
	public void extractFilesFromJar() {
		
		WebStartFileDownloader fileFinder = null;
		boolean permissionGranted = false, success = false;
		String fileName = "", newFileName = "";
		File f;
		
		if (isWebStartMode()) {
			
			fileFinder = new WebStartFileDownloader();
			fileFinder.findFile(WME_TYPE_FILE);
			fileFinder.findFile(INIT_STATE_FILE);
			fileFinder.findFile(SimStPLE.PROBLEM_STAT_FILE);
			fileFinder.findFile("bkt_params.txt");
			fileFinder.findFile("question_bank.txt");
			fileFinder.findFile("wmeStructure.txt");	
			fileFinder.findFile("productionRulesJessOracle.pr");	
			fileFinder.findFile(XMLReader.PR_MESSAGESCOG_FILE);	
			fileFinder.findFile(XMLReader.PR_MESSAGESMETACOG_FILE);	
			fileFinder.findFile(XMLReader.PR_MESSAGESAPLUSCTRL_FILE);	
			fileFinder.findFile(ModelTracer.PRODUCTION_RULES_MT_COG_FILE);	
			fileFinder.findFile(ModelTracer.PRODUCTION_RULES_MT_METACOG_FILE);	
			fileFinder.findFile(ModelTracer.PRODUCTION_RULES_MT_APLUS_CTRL_FILE);	
			
			
			
			String os = System.getProperty("os.name").toLowerCase();
			if(os.indexOf("win") >= 0) {

				fileFinder.findFile(CYGWIN_DLL);
				fileFinder.findFile(WIN_FOIL);
			
			} else if(os.indexOf("mac") >= 0) {
				
				fileName = fileFinder.findFile(MAC_FOIL);
				if(fileName != null) {
					f = new File(fileName);
					newFileName = f.getAbsolutePath().replace("_mac", "");
					success = f.renameTo(new File(newFileName));
					if(success) {
						permissionGranted = new File(newFileName).setExecutable(true, false);
						if (!permissionGranted) {
							trace.err("-------------FOIL EXECUTABLE PERMISSION DENIED----------");
						}
					}
				}
			
			} else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
				
				fileName = fileFinder.findFile(NIX_FOIL);
				if(fileName != null) {
					f = new File(fileName);
					newFileName = f.getAbsolutePath().replace("_nix", "");
					success = f.renameTo(new File(newFileName));
					if(success) {
						permissionGranted = new File(newFileName).setExecutable(true, false);
						if(!permissionGranted) {
							trace.err("-------------FOIL EXECUTABLE PERMISSION DENIED----------");
						}						
					}
				}
			
			} else {
				
				trace.err("Your OS is not supported for the foil. You need to compile foil for your OS.");
			}
		}
	}
	
   /**
    * Given a set of decomposers, and a set inital Values, run the  all decomposers on the initial values 
    * and then feed all subsequent output into all other decomposers (repeat until there is no valid decomposition) 
    * @param initValues a Vector of strings representing the inital foa values 
    * @param decomposers a Vector of decomposers which we be applied all all values
    * @return a Vector containing the combined output (no duplicates) 
    *of all decomposers applied to the inital values and all subsequent output
    */
   public static Vector /* String */<String> chainDecomposedValues(Vector /* String */ initValues, Vector /* Decomposer*/ decomposers)
   {
       LinkedList<String> inputQueue=new LinkedList<String>();
       inputQueue.addAll(initValues);
       Vector<String> allOutput=new Vector<String>();
       while(!inputQueue.isEmpty())
       {
           Iterator decomposerIter=decomposers.iterator();
           while(decomposerIter.hasNext())
           {
               Decomposer curDecomposer=(Decomposer)decomposerIter.next();

               for(int queueIndex=0; queueIndex<inputQueue.size(); queueIndex++)
               {
                   String curInput=inputQueue.get(queueIndex);

                   Vector chunks=curDecomposer.decompose(curInput);
                   if(chunks!=null)
                   {
                       Iterator chunkIterator=chunks.iterator();
                       while(chunkIterator.hasNext())
                       {
                           String curChunk=(String)chunkIterator.next();
                           if(!allOutput.contains(curChunk))
                               // add to the output set if not already present
                               allOutput.add(curChunk);
                           if(curChunk.length()>1 && !inputQueue.contains(curChunk))
                               // add to the queue to be decomposer if it is not a single charcter and not in the list
                               inputQueue.add(curChunk);
                       }
                   }
               }

           }
           //this input has been decomposed by every decomposer, remove it from the  input list
           inputQueue.removeFirst();
       }
       return allOutput;
   }

   /**
    * add a decomposer to the list of decomposers
    * @param className the fully qualified class name of the decomposer
    */
   private void addDecomposer(String className)
   {
       Decomposer decomposer=null;
       try 
       {

           Class classDef = Class.forName( className );
           decomposer = (Decomposer)classDef.newInstance();
       } 
       catch (InstantiationException e) 
       {
           e.printStackTrace();
           logger.simStLogException(e);
       } 
       catch (ClassNotFoundException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       } 
       catch (IllegalAccessException e) 
       {
           e.printStackTrace();
           logger.simStLogException(e);

       }
       decomposers.add(decomposer);
   }


   // Set default feature predicates and operator symbols
   public void initBackgroundKnowledge() {

	trace.out("miss","****** Attempting to initialize SimSt background knowledge from directory " + getProjectDirectory());
	 	 
	trace.out("miss"," working folder is " + System.getProperty("user.dir"));
	   
       // Read the feature-predicates from the file, if any,
       // otherwise, read them from a default list
//   	String runType = System.getProperty("appRunType");
   	

    try{
    		 if(runType.equalsIgnoreCase("webstart")){
    		   		if(trace.getDebugCode("miss"))trace.out("miss", "readFeaturePredicates WebStart");
    		       	readFeaturePredicates(WebStartFileDownloader.SimStAlgebraPackage+"/"+FEATURE_PREDICATES_FILE);
    		       	if(trace.getDebugCode("miss"))trace.out("miss", "readRhsOpList WebStart");
    		       	readRhsOpList(WebStartFileDownloader.SimStAlgebraPackage+"/"+RHS_OP_FILE); 
    		    	if(trace.getDebugCode("miss"))trace.out("miss", "readConstraintPredicates WebStart");
    		       	readConstraintPredicates(WebStartFileDownloader.SimStAlgebraPackage+"/"+CONSTRAINT_FILE); 
    		   	}
    		   	else if(runType.equalsIgnoreCase("servlet")){
    		   		if(trace.getDebugCode("miss"))trace.out("miss", "readFeaturePredicates Servlet");
    		       	readFeaturePredicates(System.getProperty("projectDir")+"/"+FEATURE_PREDICATES_FILE);
    		       	if(trace.getDebugCode("miss"))trace.out("miss", "readRhsOpList WebAuthoring");
    		   		readRhsOpList(System.getProperty("projectDir")+"/"+ RHS_OP_FILE);
    		    	if(trace.getDebugCode("miss"))trace.out("miss", "readConstraintPredicates WebAuthoring");
    		       	readConstraintPredicates(System.getProperty("projectDir")+"/"+CONSTRAINT_FILE);    		        	
    		   	}
    		   	else if(runType.equalsIgnoreCase("shellscript")) {
    		   		if(trace.getDebugCode("miss"))trace.out("miss", "feature predicate file exists");
    		   	        File featurePredicatesFile = new File(getProjectDir(), FEATURE_PREDICATES_FILE);
    		   	    	File operatorFile = new File(getProjectDir(), RHS_OP_FILE);
    		   	        File constraintFile = new File( getProjectDir(), CONSTRAINT_FILE );
    		           if(featurePredicatesFile != null && featurePredicatesFile.exists())
    		           readFeaturePredicates( featurePredicatesFile.getAbsolutePath() );  
    		           if(operatorFile != null && operatorFile.exists())
    		           readRhsOpList( operatorFile.getAbsolutePath() );
    		           if(constraintFile != null  && constraintFile.exists())
    		           readConstraintPredicates(constraintFile.getAbsolutePath());   
    		   	} else if(runType.equalsIgnoreCase("springBoot")) {
    		   		if(trace.getDebugCode("miss"))trace.out("miss", "read feature predicate springboot");
    		   		readFeaturePredicates(System.getProperty("projectDir")+"/"+FEATURE_PREDICATES_FILE);
    		       	if(trace.getDebugCode("miss"))trace.out("miss", "readRhsOpList springboot");
    		   		readRhsOpList(System.getProperty("projectDir")+"/"+ RHS_OP_FILE);
    		    	if(trace.getDebugCode("miss"))trace.out("miss", "readConstraintPredicates springboot");
    		       	readConstraintPredicates(System.getProperty("projectDir")+"/"+CONSTRAINT_FILE);
    		   	}
    	
    }
    catch(NullPointerException e){
    	 trace.err(" Unknown application runtype");
    }
   
   	
   	// Reading the RHS operator symbols...
   /*	File operatorFile = null;
   	operatorFile = new File(getProjectDir(), RHS_OP_FILE);
   	
	if (operatorFile==null)
   		trace.out("miss","****** File " + RHS_OP_FILE + " not found in " + getProjectDir());
	
   
   	if(operatorFile != null && operatorFile.exists() && runType.equalsIgnoreCase("shellscript") ) {
           readRhsOpList( operatorFile.getAbsolutePath() );
   	} else if(runType.equalsIgnoreCase("webstart")){ // For webstart
   		if(trace.getDebugCode("miss"))trace.out("miss", "readRhsOpList WebStart");
       	readRhsOpList(RHS_OP_FILE);    		
   	} else if(runType.equalsIgnoreCase("servlet")){
   		if(trace.getDebugCode("miss"))trace.out("miss", "readRhsOpList WebAuthoring");
   		readRhsOpList(System.getProperty("packageName")+"/"+ RHS_OP_FILE);
   	}else{
   		
   	}

   	File constraintFile = null;
       constraintFile = new File( getProjectDir(), CONSTRAINT_FILE );
       if (constraintFile != null && constraintFile.exists() && runType.equalsIgnoreCase("shellscript")) {
           readConstraintPredicates(constraintFile.getAbsolutePath());   
       } else if(isWebStartMode()){ // For webstart
       	if(trace.getDebugCode("miss"))trace.out("miss", "readConstraintPredicates WebStart");
       	readConstraintPredicates(CONSTRAINT_FILE);    		        	
       }*/
   }

   // -
   // - Methods  - - - - - - - - - - - - - - - - - - - - - - - - - -
   // -

   // ----------------------------------------------------------------------
   // Communications with Miss Console
   //

   // Needed for shuffle validation where Sim St would keep up for a
   // several validation tests
   private void reset() {

       // Reset Instructions
       instructions = new Hashtable<String, Vector<Instruction>>();
       numInstructions = 0;
       resetAllInstructions();

       // Reset Num steps in MissConsole
       getMissController().setNumStepDemonstrated(0);

       // 
       resetFeaturePredicateCache();


       // rete = new AmlRete();
       //Check if init.wme and wmeTypes.clp exist as a file or are packaged within jar
       File wmeTypeFile = null;
       wmeTypeFile = new File(getProjectDir(), WME_TYPE_FILE);
       File initWmeFile = null;
       initWmeFile = new File(getProjectDir(), INIT_STATE_FILE);

       //if(initWmeFile != null && initWmeFile.isAbsolute() && initWmeFile.exists() 
       //		&& wmeTypeFile != null && wmeTypeFile.isAbsolute() && wmeTypeFile.exists()) {
       	
       String runType = System.getProperty("projectDir");
       
       if(runType.equals("shellscript")) {
       	initRete( getWmeTypeFile(), getInitStateFile() );
       } else {
	        try {
				getRete().reset();
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        if(isWebStartMode()){
	        	parse(WebStartFileDownloader.SimStAlgebraPackage+"/"+WME_TYPE_FILE);
				parse(WebStartFileDownloader.SimStAlgebraPackage+"/"+INIT_STATE_FILE); 
	        }
	        else{
	        	parse(getProjectDirectory()+"/"+WME_TYPE_FILE);
	        	parse(getProjectDirectory()+"/"+INIT_STATE_FILE);
	        }
	        
		}

       // FOIL data
       foilDataHash = new Hashtable<String, FoilData>();

       // rules
       rules = new edu.cmu.pact.miss.HashMap();

       // Number of problems
       resetNumTrained();

       // Frequency of Rule Learning
       ruleFreq = new edu.cmu.pact.miss.HashMap();
       numAttempt = new edu.cmu.pact.miss.HashMap();
       numSuccess = new edu.cmu.pact.miss.HashMap();

       // Disjunctive Skill Names
       disjunctiveSkillNames = new edu.cmu.pact.miss.HashMap();

       // Number of training problems (used in a batch mode)
       resetNumTrained();
       resetNumTested();

       // InputMatcher cache for validation 
       // compairInputCache = new edu.cmu.pact.miss.HashMap();
   }

   public void parse(String fileName) {
   	
       ClassLoader cl = this.getClass().getClassLoader();
       InputStream is = cl.getResourceAsStream(fileName);
       InputStreamReader isr = new InputStreamReader(is);
       BufferedReader br = new BufferedReader(isr);
       try {
			Value val = getRete().parse(br, false);
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }

   public void loadWMEStructureFromReader(String fileName) {

   	ClassLoader cl = this.getClass().getClassLoader();
       InputStream is = cl.getResourceAsStream(fileName);
       InputStreamReader isr = new InputStreamReader(is);
       BufferedReader br = new BufferedReader(isr);
		getRete().loadWMEStructureFromReader(br);
   }

   /** 
    * Called by MissController.  Initialize background knowldege
    * (i.e., initial working memory and WMW types) when Miss Console
    * is up
    * @throws IOException 
    *
    **/
   public void initBKwithMissConsole() {

       // Display predicate symbols into the MissConsole
       getMissController().consoleDisplayPredicates( getPredicates() );
       // Display operators into the MissConsole
       getMissController().consoleDisplayOperators( getRhsOpList() );

       File wmeTypeFile = null;
       String runType = System.getProperty("appRunType");
       if(runType.equals("shellscript"))
    	   wmeTypeFile = new File(getProjectDir(), WME_TYPE_FILE);
       else if(runType.equals("servlet"))
    	   wmeTypeFile = new File(System.getProperty("projectDir"), WME_TYPE_FILE);
       else if(runType.equals("springBoot"))
    	   wmeTypeFile = new File(System.getProperty("projectDir"), WME_TYPE_FILE);

       if(!isWebStartMode()) {
       	  setWmeTypeFile( wmeTypeFile.getAbsolutePath() );
          getMissController().setConsoleWmeTypeFileLabel( getWmeTypeFile() );
       } else { // Added for webstart
           try {
   			getRete().reset();
   		} catch (JessException e) {
   			e.printStackTrace();
   		}
   		if(trace.getDebugCode("rr"))
   			trace.out("rr", "initBKWithMissConsole");
   		try {
   			//if(!isWebStartMode())
   			//	parse(getPackageName()+"/"+WME_TYPE_FILE);
   		   // else
   			    parse(WebStartFileDownloader.SimStAlgebraPackage+"/"+WME_TYPE_FILE);
   		} catch (Exception e) {
   			trace.errStack("Error parsing "+WebStartFileDownloader.SimStAlgebraPackage+
					"/"+WME_TYPE_FILE, e);
   			if(isWebStartMode())
   				return;
   		}

       }

       File initWmeFile = null;
       if(runType.equals("shellscript"))
    	   initWmeFile = new File(getProjectDir(), INIT_STATE_FILE);
       else if(runType.equals("servlet"))
           initWmeFile = new File(System.getProperty("projectDir"), INIT_STATE_FILE);
       else if(runType.equals("springBoot"))
           initWmeFile = new File(System.getProperty("projectDir"), INIT_STATE_FILE);
       
       if(!isWebStartMode() && initWmeFile.exists()) {
           //the result of this should be getProjectDir/init.wme, trivially
           setInitStateFile( initWmeFile.getAbsolutePath() );
           getMissController().setConsoleInitWmeFileLabel( getInitStateFile() );
       } else if(isWebStartMode()){ //Added for webstart 
    	   parse(WebStartFileDownloader.SimStAlgebraPackage+"/"+INIT_STATE_FILE);         	
       }/*else
    	   parse(getPackageName()+"/"+INIT_STATE_FILE);*/

       File initWmeStructureFile = null;
       if(runType.equals("shellscript"))
    	   initWmeStructureFile = new File(getProjectDir(), DEFAULT_STUCTURE_FILE );
       else if(runType.equals("servlet"))
    	   initWmeStructureFile = new File(System.getProperty("projectDir"), DEFAULT_STUCTURE_FILE );
       else if(runType.equals("springBoot"))
    	   initWmeStructureFile = new File(System.getProperty("projectDir"), DEFAULT_STUCTURE_FILE );

       if(!isWebStartMode() && initWmeStructureFile.exists()) {
           //the result of this should be getProjectDir/init.wme, trivially
           setWmeStructureFile( initWmeStructureFile.getAbsolutePath() );
           getMissController().setConsoleInitWmeFileLabel( getInitStateFile() );
       } else if(isWebStartMode()){ // Added for WebStart
    	   loadWMEStructureFromReader(WebStartFileDownloader.SimStAlgebraPackage+"/"+DEFAULT_STUCTURE_FILE);
       }/* else
    	   loadWMEStructureFromReader(getPackageName()+"/"+DEFAULT_STUCTURE_FILE);*/
   }

   /**
    * Called when "New Graph" in BR is selected. 
    *
    */
   public void startNewProblem() {

   		
       killInteractiveLearningThreadIfAny();

       File wmeTypeFile = null;
       wmeTypeFile = new File(getProjectDir(), WME_TYPE_FILE);
       File initWmeFile = null;
       initWmeFile = new File(getProjectDir(), INIT_STATE_FILE);

       // Reset Rete
       
       String runType = System.getProperty("appRunType");
       
       if(runType.equals("shellscript") || runType.equals("servlet") || runType.equals("springBoot")) {
       	initRete( getWmeTypeFile(), getInitStateFile() );
       } else if(isWebStartMode()) { // For webstart
       	try {
				getRete().reset();
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       		parse(WebStartFileDownloader.SimStAlgebraPackage+"/"+WME_TYPE_FILE);
       		parse(WebStartFileDownloader.SimStAlgebraPackage+"/"+INIT_STATE_FILE);
       } else{
    	   try {
				getRete().reset();
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      		//parse(getProjectDirectory()+"/"+WME_TYPE_FILE);
      		//parse(getProjectDirectory()+"/"+INIT_STATE_FILE);
       }

       /*
       try {
           getRete().reset();
           getRete().readFile( getInitStateFile() );
       } catch (JessException e) {
           e.printStackTrace();
       }
        */
   }

   /* 28 Jan 2008
    * kill the SimStInteractiveLearning thread, if there is one
    * also, switches off interactive learning.
    * rename this to: switch off interactive learning.
    */
   public static final String KILL_INTERACTIVE_LEARNING = "-kill-interactive-learning-";

   public void killInteractiveLearningThreadIfAny() {

       if (AskHintHumanOracle.isWaitingForSai){

       	if(trace.getDebugCode("miss"))trace.out("miss", "is Waiting for SAI...");
           Sai killerSai = new Sai("","","");

           if(trace.getDebugCode("miss")) trace.out("miss","Delievering the killerSai");
           AskHintHumanOracle.hereIsTheSai(killerSai);

           /*
           while (!AskHintHumanOracle.isWaitingForSkillName){ //wait until someone is asking for skillname
               try{ Thread.sleep(100);  }  catch(Exception e){ e.printStackTrace();}             
           }
           AskHintHumanOracle.hereIsTheSkillName(KILL_INTERACTIVE_LEARNING);
           */

       } else if (AskHintHumanOracle.isWaitingForSkillName){

       	if(trace.getDebugCode("miss"))trace.out("miss", "is Waiting for SkillName...");
           AskHintHumanOracle.hereIsTheSkillName(KILL_INTERACTIVE_LEARNING);
       }

       // If Simulated Student is running in batch mode it should not reset the IL/NonIL flags, if its running
       // in demonstration then it can reset the IL/NonIL flag at the start of each problem
       if(!isSsFixedLearningMode()) {
	        setIsInteractiveLearning(false);
	        setIsNonInteractiveLearning(false);
       }
   }


   /**
    * Called when a Start State is created in BR
    *
    * @param startStateName a <code>String</code> value
    */
   public void startStateCreated( ProblemNode startProblemNode ) {

       String startStateName = startProblemNode.toString();
       //      setStartStateName( startStateName );

       getMissController().setConsoleCurrentProblemName( startStateName );
       getMissController().addConsoleProblemList( startStateName );

   }

   public boolean isLearningUnlabeled = false;

   private void showActivationList() {

       BR_Controller brController = getBrController();
       MTRete rete = brController.getModelTracer().getRete();

       RuleActivationTree rat = brController.getRuleActivationTree();
       TreeTableModel ttm = rat.getActivationModel();
       RuleActivationNode ran = (RuleActivationNode)ttm.getRoot();
       trace.out("miss", "Root RuleActivationNode = " + ran.getName());

       List wholeAgenda = rete.getAgendaAsList(null);
       ran.createChildren(wholeAgenda, false);
       List children = ran.getChildren();
   }

   /**
    * Given an SAI and a foaGetterClass (a user-defined method to identify focus-of-attention, 
    * which is defined elsewhere), returns a list of GUI elements (i.e., JCommWidget) 
    * that are the corresponding focus of attention for the given "Selection" 
    * 
    * @param instruction
    * @param selection
    * @param action
    * @param input
    */
   private Vector /* Object */ applyFoaGetter(String selection, String action, String input, Vector<ProblemEdge> edgePath) {
   	return getFoaGetter().foaGetter(getBrController(), selection, action, input, edgePath);
   }

   /**
    * Set the production set name
    * @param productionSet new production set name
    */
   public void setProductionSetName(String productionSet){
       this.productionSet = productionSet;
   }

   /**
    * Get the production set name
    * @return production set name (String)
    */
   public String getProductionSetName(){
       return this.productionSet;
   }

   // HashMap of FoA (key: ProblemEdge, Obj: FoA)
   private transient Hashtable<ProblemEdge, Vector<String>> foAMap = new Hashtable<ProblemEdge, Vector<String>>();

   public Hashtable<ProblemEdge, Vector<String>> getFoaTable(){
       return this.foAMap;
   }

   public void setFoaTable(Hashtable<ProblemEdge, Vector<String>> foATable){

   	if(this.foAMap == null){
           this.foAMap = foATable;
       }else{
           //do nothing
       }

   }

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   // Run demonstration and validation in batch mode
   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

   //  30March2007: run SimSt in interactive learning mode, i.e.
   //  the tutor only enters:
   //  * the start state
   //  * Correct/Incorrect jugdements to the answers proposed by
   //  SimSt (if human oracle is being used).
   //  * occasional hints (presently in the form of demonstrations)

   //14 February 2008
   //This is only for interactive mode.
   public void runSimStInteractiveLearning() {
       // turn on "IL mode" 
   	// it will be turned off when "new graph", or mode changed away from SimStudent
       setIsInteractiveLearning(true); 
       setSsInteractiveLearning(new SimStInteractiveLearning(this));
       new Thread(getSsInteractiveLearning()).start();
   }

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   // Run demonstration and validation in batch mode
   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

   void clearJessConsole() {
       BR_Controller brController = getBrController();
       MT mt = brController.getModelTracer();
       JessConsole jc = mt.getConsole();
       jc.clearOutputArea();
   }

   void turnOffJessConsole () {
       // Turn off Jess console output
       BR_Controller brController = getBrController();
       MT mt = brController.getModelTracer();
       mt.getModelTracing().setErrorArea( null );
       if(trace.getDebugCode("miss"))trace.out( "miss", "Jess Console turned off" );
   }

   void resetMT() {
       getBrController().resetMT();
   }

   /**
    * Given a set of (unordered) training problems and test problems,
    * have Sim. St. invoke ssRunInBatchMode()
    *
    * @param testSet a <code>String[]</code> value
    * @param output a <code>String</code> value
    * @param numIteration an <code>int</code> value
    **/
   public void ssShuffleRunInBatch( String [] trainingSet, String[] testSet,
           String output, int numIteration ) {

   	if(trace.getDebugCode("miss"))trace.out("miss", "ssShuffleRunInBatch / " + numIteration );

       // Record of training sequence made so far
       Vector /* int[] */<int[]> sequence = new Vector<int[]>();

       String foilLogStem = getFoilLogDir();
       String prAgeDirStem = getPrAgeDir();

       String orderLogFile = output + "-order.txt";
       File file = new File( orderLogFile );
       if ( file.exists() ) { file.delete(); }
       FileOutputStream outFile = null;
       try {
           outFile = new FileOutputStream( file );
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
       PrintWriter orderLogOut = new PrintWriter( outFile );

       for ( int iteration = 0; iteration < numIteration; iteration++ ) {

           int[] order = makeTrainingSequence( trainingSet.length, sequence );
           if(trace.getDebugCode("miss"))trace.out( "miss", "ssRunInBatchMode in " + printSeq(order) );

           String[] training = new String[ trainingSet.length ];
           for ( int i = 0; i < trainingSet.length; i++ ) {
               training[i] = trainingSet[order[i]];
           }
           recordTrainingSequence( orderLogOut, iteration, training );

           // Logging setup
           String prettyNum = "000" + iteration;
           prettyNum = prettyNum.substring( prettyNum.length() - 3 );
           setFoilLogDir( foilLogStem + "-" + prettyNum );
           setPrAgeDir( prAgeDirStem + "-" + prettyNum );
           String logFile = output + "-" + prettyNum + ".txt";

           // Have Sim. St. validation test with the training
           // problems
           setSsCondition( prettyNum );
           ssRunInBatchMode( training, testSet, logFile );

           // Keep a record of order
           sequence.add( order );
           reset();

           if(trace.getDebugCode("miss"))trace.out( "miss", "ssRunInBatchMode end loop [" + iteration + "]");
       }

       orderLogOut.close();
       try {
           outFile.close();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   private void recordTrainingSequence( PrintWriter out,
           int n, String[] training ) {

       out.print( "" + n + "\t" );
       for ( int i = 0; i < training.length; i++ ) {
           String name = training[i];
           name = name.substring( name.lastIndexOf('/') + 1 );
           name = name.split("\\.")[0];
           out.print( name + "\t" );
       }
       out.println();
       out.flush();
   }

   private transient Random randomGenerator = new Random();
   private int[] makeTrainingSequence( int n, Vector /* int[] */<int[]> sequence ) {

       int[] order = new int[n];

       do {
           Vector /* Integer */<Integer> tmpOrder = new Vector<Integer>();
           for ( int i = 0; i < n; i++ ) {

               Integer k; 
               do {
                   k = new Integer( randomGenerator.nextInt(n) );
               } while ( tmpOrder.contains(k) ); 

               tmpOrder.add(k);
           }

           for (int i = 0; i < n; i++ ) {
               order[i] = tmpOrder.get(i).intValue();
           }
       } while ( sequence.contains( order ) );

       return order;
   }

   private String printSeq( int[] order ) {

       String seq = "";

       for (int i = 0; i < order.length; i++) {
           seq += "" + order[i] + " ";
       }

       return seq;
   }

   /**
    * For a study to apply SimSt to model real students' performances on the log data
    * collected from the Wilkinsburg study.  This method crawls a directory where 
    * log data for a student are located.  Within a given directory, 
    * take the last numTest problems as the test problems. 
    * Randomly take numTraining problems from the rest of the problems as training
    * problems.  Call ssRunInBatchMode with those training and test problems. 
    * 
    * @param BRDdir
    * @param output
    */
   public void analysisOfFitnessWilkinsburg( String studentDir, String output ) {

       int numTraining = MAX_NUM_TRAINING;
       int numTest = MAX_NUM_TEST;
       if(trace.getDebugCode("miss"))trace.out("miss", "analysisOfFitnessWilkinsburg: numTraining = " + numTraining + ", numTest = " + numTest);

       File fileStudent = new File(studentDir);
       if(trace.getDebugCode("miss"))trace.out("miss", "fileStudent = " + fileStudent);
       if (fileStudent.isDirectory()) {

           String[] testSet = pickTestSet(fileStudent, numTest);
           String[] trainingSet = (testSet != null) ? pickTrainingSet(fileStudent, numTraining, testSet) : null;

           if (trainingSet != null && testSet != null) {
               if(trace.getDebugCode("miss"))trace.out("miss", "numTest: " + testSet.length + ", numTrain: " + trainingSet.length);
               // reset();
               ssRunInBatchMode(trainingSet, testSet, output);
           }
       }
   }

   // Return a list of the last numTest file names in the fileStudent directory
   private String[] pickTestSet(File brdDir, int numTest) {

       String[] testSet = new String[numTest];

       String[] brdFile = brdDir.list();
       if(trace.getDebugCode("miss"))trace.out("miss", "pickTestSet pick " + numTest + " in " + brdDir);
       for (int i = brdFile.length; i > 0 && numTest > 0; i--) {
           String brdFileName = brdFile[i-1];
           if (brdFileName.matches(".*brd$") &&
                   isBadWilkinsburgBrdFile(brdFileName) == null) {
               String theBrdName = new File(brdDir, brdFileName).getPath();
               testSet[--numTest] = theBrdName;
               if(trace.getDebugCode("miss"))trace.out("miss", "testProblem: " + theBrdName);
           }
       }
       if(trace.getDebugCode("miss"))trace.out("miss", "pickTestSet exiting with numTest = " + numTest);
       // numTest must be zero, otherwise something wrong should have 
       // been happned in the above for loop
       return numTest == 0 ? testSet : null;
   }

   // Return a list of 
   private String[] pickTrainingSet(File brdDir, int numTraining, String[] testSet) {

       String[] trainingSet = new String[numTraining];
       int numProblem = 0;

       String[] brdFile = brdDir.list();
       for (int i = 0; i < brdFile.length && numProblem < numTraining; i++) {
           String brdFileName = brdFile[i];
           if ( !isMemberOf(brdFileName, testSet)&&
                   brdFileName.matches(".*brd$") &&
                   isBadWilkinsburgBrdFile(brdFileName) == null ) {
               String theBrdName = new File(brdDir, brdFileName).getPath();
               trainingSet[numProblem++] = theBrdName;
               if(trace.getDebugCode("miss"))trace.out("miss", "trainingProblem: " + theBrdName);
           }
       }
       if(trace.getDebugCode("miss"))trace.out("miss", "pickTrainingSet exiting with numProblem = " + numProblem);
       return (numProblem == numTraining) ? trainingSet : null;
   }

   private boolean isMemberOf(String brdFileName, String[] testSet) {
       boolean isMemberOf = false;
       for (int i = 0; i < testSet.length; i++) {
           if (brdFileName.equals(testSet[i])) {
               isMemberOf = true;
               break;
           }
       }
       return isMemberOf;
   }

   private void initSsRunInBatchMode() {
//        getMissController().dealWithExistingPrFile();
       turnOffJessConsole();
   }

   /**
    * Given a set of (ordered) training problems and test problems,
    * have Sim. St read a training problem one at a time, induce
    * production rules, and test those rules against the test
    * problems.  Iterate this rule-induction and model-validation for
    * each of the training problem.
    *
    **/
   public void ssRunInBatchMode( String[] trainingSet, String[] testSet, String output ) {

       initSsRunInBatchMode();

       if(trace.getDebugCode("miss"))trace.out("miss", "Num of Training Set = " + trainingSet.length );

       for (int i = 0; i < trainingSet.length; i++) {
           if(trace.getDebugCode("miss"))trace.out("miss", "Training problem ### i = " + i );
           if(trace.getDebugCode("nbarbaBrd"))trace.out("nbarbaBrd", "*** Training on problem " + trainingSet[i] + " with isInteractiveLearningFlag " + isInteractiveLearningFlag() + " and isNonInteractiveLearningFlag() " + isNonInteractiveLearningFlag());
           //ssRunInBatchMode( trainingSet[i], testSet, output );
           ssRunInBatchMode(trainingSet[i], testSet, output, isInteractiveLearningFlag(), isNonInteractiveLearningFlag());
           if(trace.getDebugCode("miss"))trace.out("miss", "Training Problem ### i = " + i + " done" );
       }
   }


   public void ssRunInContestMode()
   {
   	new ContestServer(this);
   	/*String[] prs = new String[3];
	    prs[0] = "productionRules1.pr";
	    prs[1] = "productionRules2.pr";
	    prs[2] = "productionRulesOdd.pr";
	    ContestMultiProdRules contest = new ContestMultiProdRules(this, prs);
	    contest.contestOnProblem("x+5=9");
	    contest.contestOnProblem("8x-3=2");
	    contest.contestOnProblem("4x+5=9-8x");
	    contest.contestOnProblem("6x=9");*/
	    //contest.contest(currentNode);
   }
   
   public boolean getNodeDoneState() {
       ProblemNode parentNode = getBrController().getParentNodeInfo();
       return parentNode.getDoneState();
   }
   
   public void setNodeDoneState(boolean nodeState) {
       ProblemNode parentNode = getBrController().getParentNodeInfo();//getBrController().getSolutionState().getCurrentNode();
       parentNode.setDoneState(nodeState);
   }
   
   public void ssLogStudentsLearning( String training, String output ) {

       File trainingFile = new File(training); 
       if (trainingFile.isDirectory()) {
           setSsCondition(new File(training).getName());
           String brdFiles[] = trainingFile.list();
           for (int i = 0; i < brdFiles.length; i++) {
               if (brdFiles[i].matches(".*brd$")) {
                   if(trace.getDebugCode("miss"))trace.out("miss", "Located a training problem " + brdFiles[i] + " in " + training);
                   String brdName = new File(trainingFile, brdFiles[i]).getAbsolutePath();
                   if(trace.getDebugCode("miss"))trace.out("miss", "The absolute file name: " + brdName);
                   ssLogStudentsLearning(brdName, output);
               }
           }
           return;
       }

       // Wed Nov  1 16:59:20 LMT 2006 :: Noboru
       // Quite ad-hoc, but needed to run SimSt over Wilkinsburg data
       // If the specified trainiing is known to be a broken BRD (which,
       // is pre-computed and information stored into a file), then skip
       if (isCheckWilkinsburgBadBrdFile()) {
           String brokenType = null;
           if ((brokenType = isBadWilkinsburgBrdFile(training)) != null){
               if(trace.getDebugCode("miss"))trace.out("miss", "BAD BRD: " + training + " reported in " + brokenType);
               return;
           }
       }

       BR_Controller brController = getBrController();
       LoadFileDialog.doLoadBRDFile( brController, training, "", true);

       // *** Uncomment this when you hit a heap memory problem
       // resetFeaturePredicateCache();
       incNumTrained();

       // set true to batch mode flag
       setIsBatchMode(true);

       // Load WME values elements 
       // Switch to SimStudent Mode
       switchToSimStMode();

       ProblemNode parentNode = brController.getProblemModel().getStartNode();
       Stack<ProblemNode> nodeStack = new Stack<ProblemNode>();
       pushChildNodes(parentNode, nodeStack); //push all the children of start-state

       // Iterate demonstration through the problem states
       while ( !nodeStack.isEmpty() ) {

           ProblemNode targetNode = nodeStack.pop();
           if(trace.getDebugCode("miss"))trace.out("miss", "node " + targetNode + " is getting demonstrated.");

           parentNode = (ProblemNode) targetNode.getParents().get(0);
           pushChildNodes(targetNode, nodeStack);

           // The edge coming into the target node
           ProblemEdge edge = lookupProblemEdge(parentNode, targetNode);

           if (edge.isCorrect()) {

               EdgeData edgeData = edge.getEdgeData();
               if(trace.getDebugCode("miss"))trace.out("miss", "edge action type = " + edgeData.getActionType());

               // The skill names in an edgeData looks like 
               // "hoge boo" where boo is a name of the production set
               Vector skillNames = edgeData.getSkills();
               String modelSkillName = ((String)skillNames.get(0)).split(" ")[0];
               String modelStatus = edgeData.getActionType();

               // Record rule fireing during learning
               String condition = getSsCondition();
               // String modelStatus = edge.getSource().cltLogStatus();
               logRuleActivationToFile( LOG_PRTEST_TRAINING, training, targetNode.getName(), 
                       modelStatus, modelSkillName, null, null, condition, output);

               incRuleFreq(modelSkillName);
           }
       }
   }

   /**
    * 
    * @param trainingFile File containing training problems. Can be either text file, brd file, directory
    * containing a list of brdFiles.
    * @param testSetFile
    * @param outputFile
    * @param ILFlag
    * @param NonILFlag
    */
   private void ssRunInBatchMode(String trainingFile, String[] testSetFile, String outputFile, boolean ILFlag, boolean NonILFlag) {
   	
   	// Check if the trainingFile is a textFile, brdFile or a directory
   	File f = new File(trainingFile);
   	if(f.isDirectory()) {
   		
   		// If its a directory with a list of brd files then call the method recursively until you  have a file which is a brd itself
   	
   		
   		String brdFiles[] = f.list();
   		for (int i = 0; i < brdFiles.length; i++) {
   			if (brdFiles[i].matches(".*brd$")) {
   				if(trace.getDebugCode("miss"))trace.out("miss", "Located a training problem " + brdFiles[i] + " in " + f);
   				String brdName = new File(trainingFile, brdFiles[i]).getAbsolutePath();
   				if(trace.getDebugCode("miss"))trace.out("miss", "The absolute file name: " + brdName);
   				ssRunInBatchMode(brdName, testSetFile, outputFile,ILFlag,NonILFlag);
   			}
   		}
   		return;
   		
   		
   		
   	} else if(f.isFile()) {
   		
   		// Check if it's a brd file or a text file. It its a brd file then do the IL/ NonIL
   		// using the brd file
 			
   		if(trainingFile.matches(".*brd$")) {
   			
   				//trace.out("******** Trying to process file : " + trainingFile);
   				//this.ssRunDemonstrationInBatchMode(trainingFile, outputFile);
   				this.ssRunInBatchMode(trainingFile, testSetFile, outputFile);
   				
   			// If its a text file with a list of problems then do the IL / NonIL using the text file	
   		}else if(trainingFile.matches(".*txt$")) {
   			
   			List<String> problemNames = loadProblemNamesFile(trainingFile);
   			trace.out("miss","Start training on " + problemNames.size() + " problems, ILFlag is " + ILFlag);
   			
   			for(String problem : problemNames) {		
   				// If it's interactive learning
   				if(ILFlag) {
   	        		getBrController().startNewProblem(false);
   	                setSsInteractiveLearning(new SimStInteractiveLearning(this));
   					if(trace.getDebugCode("rr"))
   						trace.out("rr", "Running interactive learning on problem: " + problem 
   								+ " IL Status: " + isInteractiveLearning() + " ILFlag Status: " + isInteractiveLearningFlag());
   					setIsInteractiveLearning(isInteractiveLearningFlag());
   	                getSsInteractiveLearning().ssInteractiveLearningOnProblem(problem);
   	                if(trace.getDebugCode("rr"))
   	                	trace.out("rr", "Done IL Status: " + isInteractiveLearning() + " ILFlag Status: " + isInteractiveLearningFlag());

   	            // If it's non interactive learning    
   				} else if(NonILFlag) {
   					
   					getBrController().startNewProblem(false);
   					if(trace.getDebugCode("rr"))
   						trace.out("rr", "Running non-interactive learning on problem: " + problem
   								+ " NIL Status: " + isNonInteractiveLearning() + " NILFlag Status: " + isNonInteractiveLearningFlag());
   					setIsNonInteractiveLearning(isNonInteractiveLearningFlag());
   					ssRunDemonstrationInBatchModeNoBRD(problem, outputFile);
   					if(trace.getDebugCode("rr"))
   						trace.out("rr", "Done NIL Status: " + isNonInteractiveLearning() + " NILFlag Status: " + isNonInteractiveLearningFlag());
   				}
   				
   				// Run the validation after the training has been completed (Test the current prodution rules
   				// by model tracing each of the problems in the testSet.
                   if (isTestOnProblemBasis() && testSetFile != null) {
                       if (!testOnLastTrainingOnly() || getNumTrained() == MAX_NUM_TRAINING) {
                       	if(trace.getDebugCode("rr"))
                       		trace.out("Running validation on testSetFile");
                       		trace.err("######## start Validation");
                           validateProductionModel(testSetFile, outputFile);
                           trace.err("######## end Validation");
                       }
                   }
   			}
   		}
   	}
   }

   int trainingCycle=0;
   
   
   private void ssRunInBatchMode( String training, String[] testSet, String output ) {
       if (getNumTrained() > MAX_NUM_TRAINING) {
           if(trace.getDebugCode("miss"))trace.out("miss", "MAX_NUM_TRAINING exceeded @@@@@@@@@@@@@@@@@@@@@@@@@");
           return;
       }
       
       
       
       if (isSwitchLearningStrategy() && getNumTrained() > getSwitchLearningStrategyAfter()) {
           setIsInteractiveLearning(true);
           // TODO: the hint method should be read from an appropriate field
           setHintMethod("clAlgebraTutor");
           // reset force learning
       }

       

       File trainingFile = new File(training);

       //In case 'training' is a directory, not a file
       if (trainingFile.isDirectory()) {
    	   
           String brdFiles[] = trainingFile.list();
           for (int i = 0; i < brdFiles.length; i++) {
               if (brdFiles[i].matches(".*brd$")) {
                   if(trace.getDebugCode("miss"))trace.out("miss", "Located a training problem " + brdFiles[i] + " in " + training);
                   String brdName = new File(trainingFile, brdFiles[i]).getAbsolutePath();
                   if(trace.getDebugCode("miss"))trace.out("miss", "The absolute file name: " + brdName);
                   ssRunInBatchMode(brdName, testSet, output);
               }
           }
           return;
       } 
       else if(!training.matches(".*brd$") && (isInteractiveLearning() || isNonInteractiveLearning()) ) {
       	//Non-brd file should be list of problem name files, only valid for interactive learning
       	List<String> problems = loadProblemNamesFile(training);
       	for(String problem:problems)
       	{
				/* Treat ";;" in the txt file as comments */
				int index = problem.indexOf(";;");
				if (index == 0)
					continue;
				else if (index > 0)
					problem = problem.substring(0, index);
				
       		if (isInteractiveLearningFlag()) {
       			if(trace.getDebugCode("miss"))trace.out("miss", "Learning Strategy >> Interactive Learning...");
       			setIsInteractiveLearning(true);
       		}
       		else if (isNonInteractiveLearningFlag()) {
       			setIsNonInteractiveLearning(true);
       		}
       		getBrController().startNewProblem(false);
            setSsInteractiveLearning(new SimStInteractiveLearning(this));
            getSsInteractiveLearning().ssInteractiveLearningOnProblem(problem);

               if (isTestOnProblemBasis() && testSet != null) {
                   if (!testOnLastTrainingOnly() || getNumTrained() == MAX_NUM_TRAINING) {
                       validateProductionModel(testSet, output);
                   }
               }
       	}

       	return;
       }

       // Wed Nov  1 16:59:20 LMT 2006 :: Noboru
       // Quite ad-hoc, but needed to run SimSt over Wilkinsburg data
       // If the specified trainiing is known to be a broken BRD (which,
       // is pre-computed and information stored into a file), then skip
       if (isCheckWilkinsburgBadBrdFile()) {
           String brokenType = null;
           if ((brokenType = isBadWilkinsburgBrdFile(training)) != null){
               if(trace.getDebugCode("miss"))trace.out("miss", "BAD BRD: " + training + " reported in " + brokenType);
               return;
           }
       }

       resetMT();

       // Sat Sep 30 16:15:50 LDT 2006 :: Noboru
       // Reset the timer to ensure that the training won't run forever
       if (isTimed()) resetSimStTimer();

       if(trace.getDebugCode("miss"))trace.out( "miss", "Learning production model with " + training );

       if (isLogPriorRuleActivationOnTraining()) {
           resetNumTested();
           testProductionModelOn( training, LOG_PRTEST_TRAINING, "", output );
       }

  
       trainingCycle++;
       this.setIsInteractiveLearning(true);      
       if (isInteractiveLearning() || isNonInteractiveLearning()){ 
           // IL batch mode
           if(trace.getDebugCode("miss"))trace.out("miss", "Learning Strategy >> Interactive Learning...");
         
           this.getBrController().startNewProblem(null);
           setSsInteractiveLearning(new SimStInteractiveLearning(this));
           setIsInteractiveLearning(isInteractiveLearningFlag());
           if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd","Going with BRD interactive learning.... interactive learnig is now " + this.isInteractiveLearning); 
           getSsInteractiveLearning().ssInteractiveLearningWithBRD(training);
       } else { 
           // normal batch mode
           if(trace.getDebugCode("miss"))trace.out("miss", "Learning Strategy >> Traditional Learning...");
           ssRunDemonstrationInBatchMode( training, output );
       }

       if (isSearchTimeOut()) {
           if(trace.getDebugCode("miss"))trace.out("miss", "Search terminated for the time limitation (" + getTimeoutDuration() + " ms)");
       }

       if (isTestOnProblemBasis() && testSet != null) {
           if (!testOnLastTrainingOnly() || getNumTrained() == MAX_NUM_TRAINING) {
        	   if(trace.getDebugCode("nbarbaBrd"))trace.out("nbarbaBrd","Starting validation.......");
               validateProductionModel(testSet, output);
           }
       }

       // Write rules into a file on the step basis
       if(isArchivingProductionRules() && isArchivingPRonProblemBasis()) {
           saveProductionRules(SAVE_PR_PROBLEM_BASE);
       }

       incNumTrained();
       resetNumModelTracing();
   }

   private List<String> loadProblemNamesFile(String filename)
   {
   	int index;
   	List<String> problemNames = new LinkedList<String>();
   	try {
       	File file = new File(filename);
       	if(!file.exists())
       		throw new FileNotFoundException();
       	BufferedReader reader = new BufferedReader(new FileReader(file));
       	String input = reader.readLine();
       	while(input != null)
       	{
       		index = input.indexOf(";;"); // Line beginning with a ;; is a comment so ignore that
       		if(index == 0)
       			continue;
       		else if(index > 0)
       			input = input.substring(0, index);
       		problemNames.add(input);
       		input = reader.readLine();
       	}
       	
       } catch (Exception e) {
           e.printStackTrace();
           String message = "<html>The file "+filename+" could not be found or the format of the file is not recognized. <br>"
       		+ "Please check the file and try again.";
           String title = "Error loading file";
           Utils.showExceptionOccuredDialog(e, message, title);
       }
       return problemNames;
   }

   private String[] wilkinsburgBadBrdFiles = {
           "FoAnotSpecified.txt", "ExpParseException.txt", "propertyChangeException.txt",
           "misc.txt", "falsePositive.txt", "tooFewSteps.txt", "All_Data_94_BRD.txt"
   };
   private String wilkinsBadBrdFilesDir = "Problems/DataShopExport-NG-files";

   public String isBadWilkinsburgBrdFile(String training) {
       String isBadBrdFile = null; 
       // training = "132_m32x-4"
       String brdName = new File(training).getName().substring(4);

       for (int i=0; i < wilkinsburgBadBrdFiles.length; i++) {
           if (isListedInBadBrdFile(brdName, wilkinsburgBadBrdFiles[i])) {
               isBadBrdFile = wilkinsburgBadBrdFiles[i];
               if(trace.getDebugCode("miss"))trace.out("miss", "isBadWilkinsburgBrdFile: " + training + " is bad on " + wilkinsburgBadBrdFiles[i]);
               break;
           }
       }
       return isBadBrdFile;
   }

   private boolean isListedInBadBrdFile(String training, String badBrdListingFile) {
       boolean isListedInBadBrdFile = false;

       //training should be "m774..."
       try {
           String listName = getProjectDir() + "/" + wilkinsBadBrdFilesDir + "/" + badBrdListingFile;
           //listName should be one of the files
           FileReader fReader = new FileReader(listName);
           BufferedReader in = new BufferedReader(fReader);
           String badBrd = null;
           while ((badBrd = in.readLine()) != null) {
               if (badBrd.indexOf(training) > 0) {
                   isListedInBadBrdFile = true;
                   break;
               }
           }
           in.close();
           fReader.close();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
       return isListedInBadBrdFile;
   }

   // Test the current production rules by model tracing each of the test problem
   // in the testSet
   private void validateProductionModel(String[] testSet, String output) {

       // Reset the number of test problems
       resetNumTested();

       for ( int i = 0; i < testSet.length; i++ ) {
           if(trace.getDebugCode("miss"))trace.out("miss", "Test problem #" + (i+1) + " [" + testSet[i] + "]");
           testProductionModelOn( testSet[i], LOG_PRTEST_TEST, output );
       }
   }

   //30Sep2006:
   //The purpose of the following algorithm is to visit all the edges, but only learn
   //those which have a type that SimSt is meant to learn, as set by the command-line
   //parameters.
   //
   //The algorithm is a depth-first search (this is why a stack is used):
   //* initialize parentNode to the start node
   //* initialize stack with start node's children (i.e. push them)
   //WHILE !nodeStack.isEmpty()
   //* targetNode = stack.pop()
   //* set current node to the parent of targetNode
   //* push all children of target node onto stack
   //* edge = getEdge(current,target))
   //* if edge is of a type we are learning, learn edge

   public /*private*/ void switchToSimStMode() {
       BR_Controller brController = getBrController();
       if (!brController.getBehaviorRecorderMode().equals(CtatModeModel.SIMULATED_STUDENT_MODE)) {
           //brController.setBehaviorRecorderMode( CtatModeModel.SIMULATED_STUDENT_MODE );
           brController.setModeSimStAndDestroyProdRules();
       }
   }

   /**
    * <p>Algorithm of running demonstration in BatchMode with a problem <p>
    * 1. Demonstrate a step with the specified oracle <br>
    * 2. Gather the activation list and see if any of the activations generate the step
    * demonstrated in step 1 from the oracle. <br>
    * 3. If (yes) from step 2 Then <br>
    *        Go to the next step<br>
    *        Repeat from 1<br>
    *    Else If (no)<br>
    *        SimStudent learns the step demonstrated<br>
    *        Repeat from 1<br>
    * @param problem
    * @param outputFile
    */
   public void ssRunDemonstrationInBatchModeNoBRD( String problem, String outputFile) {
   	
   	if(trace.getDebugCode("rr"))
   		trace.out("rr", "ssRunDemonstrationInBatchModeNoBRD on problem: " + problem);
   	
   	BR_Controller brController = getBrController();
   	boolean killMessageReceived = false;
   	
   	// Create a start state for the problem
   	if(brController.getProblemModel().getStartNode() == null) {

   		if(isInterfaceElementGetterClassDefined()) {
   			getInterfaceElementGetter().simulateStartStateElementEntry(brController, problem);
   		}
   			
   		// Tell the brController to create the start state
   		String normalProblemName = convertToSafeProblemName(problem);
   		try {
   			brController.createStartState(normalProblemName);
   		} catch (ProblemModelException e) {
   			e.printStackTrace();
   		}
   	}
   	
   	// Keep looping till the done state is reached
   	while(!killMessageReceived) {
	    	
   		ProblemNode currentNode = brController.getCurrentNode();
   		ProblemNode nextCurrentNode = null;
   		
   		// Ask the designated oracle for the demonstration or hint
	    	AskHint hint =  null;
	    	hint = askForHint(brController, currentNode);

	    	// Model selection, action and input is the one suggested by the oracle
	    	Vector<String> selection = new Vector<String>();
	    	Vector<String> action = new Vector<String>();
	    	Vector<String> input = new Vector<String>();
	    	
	    	String modelSelection = hint.getSelection();
	    	String modelAction = hint.getAction();
	    	String modelInput = hint.getInput();
	    	// If its a done step change skill name from kill-interactive-learning to done
	    	if(hint.getSkillName().contains("kill")) {
	    		hint.setSkillName("done");
	    	}
	    	String modelSkillName = hint.getSkillName();
	    	
	    	selection.add(modelSelection);
	    	action.add(modelAction);
	    	input.add(modelInput);
	    	
	    	// Create a new node and edge
	    	SimStNodeEdge ssNodeEdge = makeNewNodeAndEdge(hint.getSai(), currentNode);
	    	hint.setNode(ssNodeEdge.node);
	    	hint.setEdge(ssNodeEdge.edge);
	    	
	    	// Instruction would be created and added
	    	stepDemonstrated(hint.node, hint.getSai(), hint.getEdge(), null);
	    	
	    	if(trace.getDebugCode("rr"))
	    		trace.out("rr", "sel: " + modelSelection + " act: " + modelAction + " inp: " + modelInput + " skillName: " + modelSkillName);
	    	
	    	// See if there is a production rule that explains the demonstrated step i.e. the one oracle suggested
	    	String ruleFired = null;
	    	ruleFired = tryModelTraceSAI(currentNode, selection, action, input);

	        if(trace.getDebugCode("miss"))trace.out("miss", "askHint returning " + hint);
	    	
	    	// None of the activations can generate the model sel, act and input so learn the step
	    	if(ruleFired == null) {
	    		boolean successful = changeInstructionName(hint.skillName, hint.node);
	    		if(successful)
	    			setRuleLearned(modelSkillName, RULE_LEARNED);
	    		else {
	    			nextCurrentNode = null;
	    			break;
	    		}
	    	}
	    	
	    	// Advance the current node to the oracle demonstrated node
   		nextCurrentNode = hint.node;
	    	getBrController().setCurrentNode2(nextCurrentNode);
	    	getBrController().getGoToWMStateResponse(nextCurrentNode.getName());
	    	currentNode = nextCurrentNode;
	    	
	    	if(!(currentNode.isDoneState())) {
	    		
	    		if(trace.getDebugCode("rr"))
	    			trace.out("rr", "Updating the current node");
	    		currentNode = nextCurrentNode;
	    	
	    	} else if(currentNode.isDoneState()){ // Do not go beyond the done state  
	    		
	    		killMessageReceived = true;
	    		if(trace.getDebugCode("rr"))
	    			trace.out("rr", "killMessageReceived");
	    	}
   	}
   }

   public /*private*/ void ssRunDemonstrationInBatchMode( String training, String output ) {

       // SimStTimerException would be thrown by changeInstructionName() to prevent SimSt 
       // spend hours to learn a rule
	
	   
       if(trace.getDebugCode("miss"))trace.out( "miss", "ssRunDemonstrationInBatchMode on " + training );
       trace.err("ssRunDemonstrationInBatchMode on " + training);
       Thread.dumpStack();
       BR_Controller brController = getBrController();
       brController.startNewProblem(null);  
       
       
       //Old deprecated method
      //  LoadFileDialog.doLoadBRDFile( brController, training, "", true);
       LoadFileDialog.doLoadBRDFile(brController.getServer(), training, "", true);
       
       
       
       // *** Uncomment this when you hit a heap memory problem
       // resetFeaturePredicateCache();
       incNumTrained();
       resetRuleLearned();

       // set true to batch mode flag
       setIsBatchMode(true);

       if (isFoaGetterDefined()) {
           getFoaGetter().init(getBrController());
       }

       /* @author: jinyul */

       if (isSkillNameGetterDefined()) {
       	getSkillNameGetter().init(getBrController());
       }

       // Load WME values - - - - - - - - - - - - - - - - - - - - - - 
       // 
       // Make sure it's the SimStudent Mode
       switchToSimStMode();

       // For some unknown reason, these steps must be performed to 
       // get the model trace done correctly in the stepDemonstrated()
       // 
       ProblemNode startNode = brController.getProblemModel().getStartNode();
       ProblemNode doneNode = startNode.getDeadEnd();
      
       // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
       int numStepDemonstrated = 0;
       Vector /* String[] */<String[]> foaV = null;
       if (!isFoaGetterDefined()) {
           // Read a set of focus of attention
           foaV = readFocusOfAttentionFromBRD( training );
       }

       ProblemNode parentNode = brController.getProblemModel().getStartNode();
       Vector allPaths = getAllPaths(parentNode);

       //22Jan2007: loading the given actions onto the WMEs.
       //This is necessary because of BRDs where the given values are at the end.
       updateGivenActions(parentNode);

       Vector<ProblemEdge> edgeCache = new Vector<ProblemEdge>();
       Vector<ProblemEdge> edgePath = new Vector<ProblemEdge>(); //a.k.a. FOA maintenance vector

       MTRete mtRete = getBrController().getModelTracer().getRete();

       List<ProblemEdge> wmeFactsToClear = new Vector<ProblemEdge>();

       // Iterate demonstrations through all alternative solutions 
       for (int path_i=0; path_i<allPaths.size(); path_i++){

           Vector path = (Vector) allPaths.get(path_i);

           //ToDo
           this.showJessFacts();

           //updateJessFactsForNewPath
           clearJessWmeFacts(wmeFactsToClear); //roll back all Jess WME Facts, if any

           // Iterate steps in the path
           path = reorderPath(path);  //efficiency improvement: call pathOrderer.pathOrdering

           for (int edge_i=0; edge_i<path.size(); edge_i++) { //this loop is 'stepsIterated'                

               //ProblemEdge edge = (ProblemEdge) edgeStack.pop();
               ProblemEdge edge = (ProblemEdge) path.get(edge_i);

               //ToDo
               this.showJessFacts();
               int hashC = mtRete.hashCode();

               wmeFactsToClear.add(edge);

               if (edgeCache.contains(edge)) {
                   //if edge has been visited, move on to next edge in stack
                   continue; //20 Sep 2007: we may not want this if learning turns out to be path-dependent.
               } else {
                   // mark this edge as visited
                   edgeCache.add(edge);
               }

               parentNode = edge.source;		
               ProblemNode targetNode = edge.dest;

               //find parent in edge Path (//except for first edge)
               int index = indexOfLastEdgeInPathContainingNode(parentNode, edgePath);  
               if (index!=-1) //-1 means that there is no edge with parentNode as source.
                   removeEdgesAfterInclusive(index, edgePath); //remove that edge and everything after it.
               edgePath.add(edge);

               if ( toBeLearned(edge) ) {

                   if(trace.getDebugCode("miss"))trace.out("miss", "Step from " + parentNode + " to " + targetNode + " is getting learned...");
                   EdgeData edgeData = edge.getEdgeData();

                   // Set SAI tuple
                   Vector /* String */ selection = edgeData.getSelection();
                   Vector /* String */ action = edgeData.getAction();
                   Vector /* String */ input = edgeData.getInput();
                   // debugPringSAI(selection, action, input);

                   String msg = "ssRunDemonstrationInBatchMode: Edge = " + edge + "(" + edgeData.getActionType() + ")";
                   msg += ", <" + selection + ", " + action + ", " + input + ">";
                   if(trace.getDebugCode("miss"))trace.out("miss", msg);

                   if (!isFoaGetterDefined()) {
                       // Set a FoA for the current "node"
                       if ( foaV != null && !foaV.isEmpty()) {
                           String[] foaArray = foaV.get(numStepDemonstrated);
                           for (int i = 0; i < foaArray.length; i++) {
                               String foaWme = foaArray[i];
                               Object wme = brController.lookupWidgetByName( foaWme );
                               if(trace.getDebugCode("miss"))trace.out("miss", "foaWme: " + foaWme + " got " + wme );
                               toggleFocusOfAttention(wme);
                           }
                       }
                   }

                   // dispatch a demonstartion
                   // Sat Sep 30 16:15:50 LDT 2006 :: Noboru
                   // Instruction would be created and added
                   stepDemonstrated( targetNode, selection, action, input, edge, edgePath );

                   // The skill names in an edgeData looks like 
                   // "hoge boo" where boo is a name of the production set
                   Vector skillNames = edgeData.getSkills();
                   String modelSkillName = ((String)skillNames.get(0)).split(" ")[0];
                   // set UNLABELED_SKILL as the skill name only when ssLearnNoLabel & not "Done"
                   if (!modelSkillName.equals(Rule.DONE_NAME) && System.getProperty("ssLearnNoLabel") != null) {
                       modelSkillName = Rule.UNLABELED_SKILL;
                   }

                   // Record rule fireing during learning
                   if (isLogRuleActivationDuringTraining()) {
                       // See if the demonstrated step was correct at the first attemt
                       // (For Carngeig Learning log analysis)
                       // String modelStatus = edge.getSource().cltLogStatus();
                       String actionType = edgeData.getActionType();
                       logRuleActivationsDuringTraining(training, targetNode.getName(), actionType, modelSkillName);
                   }

                   // See if there is a production rule that explains the step demonstrated
                   String ruleFired = null;
                   if (!isForceToUpdateModel()) {
                       ruleFired = tryModelTraceSAI(parentNode, selection, action, input);
                       if(trace.getDebugCode("miss"))trace.out("miss", "ruleFired = " + ruleFired);

                       // tryModelTraceSAI set the "current node" to the parentNode
                       if(trace.getDebugCode("miss"))trace.out("Exiting from tryModelTraceSAI: going back to the node " + targetNode);

                   }

                   getBrController().setCurrentNode2(targetNode);
                   if (trace.getDebugCode("mt")) trace.out("mt", "Called from ssRunDemonstrationInBatchMode");                    
                   getBrController().getGoToWMStateResponse(targetNode.getName());


                   // simulate to add a skill name
                   // Wed Jun 14 00:10:03 LDT 2006  Noboru
                   // change skill name (hence update the mode) only when 
                   // the model trace failed
                   if ((isForceToUpdateModel() || ruleFired == null)){// && modelSkillName.equals("subtract")) { /////CLEAN THIS UP     isForceToUpdateModel() => ruleFired==null?
                       if(trace.getDebugCode("miss"))trace.out("miss", "ssRunDemonstrationInBatchMode: learning a rule " + modelSkillName);
                       changeInstructionName( modelSkillName, targetNode );
                       setRuleLearned(modelSkillName, RULE_LEARNED);
                       numStepDemonstrated++;
                   } else {
                       if(trace.getDebugCode("miss"))trace.out("miss", "ssRunDemonstrationInBatchMode: " + ruleFired + " seems to be okay...");
                       // Sat Jul 22 22:21:25 LDT 2006 :: Noboru
                       // Claim that the rule was correctly model traced (hence no need to
                       // relearn) only when the rule has not been relearned
                       if (!isRuleLearned(modelSkillName)) {
                           setRuleLearned(modelSkillName, RULE_NOT_LEARNED);
                       }
                   }
               } //toBeLearned()
           } // step iteration in a path
       } // path iteration in all alternative solution paths
   }

   private String tryModelTraceSAI(ProblemNode problemNode, Vector selection, Vector action, Vector input) {

       if(trace.getDebugCode("miss"))trace.out("miss", "tryModelTraceSAI: gathering rule activations for the node " + problemNode);

       String ruleFired = null;

       String modelSelection = (String)selection.get(0);
       String modelAction = (String)action.get(0);
       String modelInput = (String)input.get(0);

       Vector /* RuleActivationNode */<RuleActivationNode> ruleActivations = gatherActivationList(problemNode);
       for (int i = 0; i < ruleActivations.size(); i++) {

           RuleActivationNode ran = ruleActivations.get(i);
           String actualSelection = ran.getActualSelection();
           String actualAction = ran.getActualAction();
           String actualInput = ran.getActualInput();

           if(trace.getDebugCode("miss"))trace.out("miss", "tryModelTraceSAI: ============== ");
           if(trace.getDebugCode("miss"))trace.out("miss", "           node: " + problemNode);
           if(trace.getDebugCode("miss"))trace.out("miss", "      rule name: " + ran.getName());
           if(trace.getDebugCode("miss"))trace.out("miss", "actualSelection: " + actualSelection);
           if(trace.getDebugCode("miss"))trace.out("miss", "    actualInput: " + actualInput);

           if (isStepModelTraced(modelSelection, modelAction, modelInput, actualSelection, actualAction, actualInput)) {
               ruleFired = ran.getName();
               if(trace.getDebugCode("miss"))trace.out("miss", "modelTraceSAI: >>>>> result: TruePositive");
               break;
           }
           else
           {
               if(trace.getDebugCode("miss"))trace.out("miss", "modelTraceSAI: >>>>> result: FalsePositive");
           }
       }

       return ruleFired;
   }

   /**
    * 20 Sep 2007
    * clears the given Jess WME Facts.
    * empties the list of wmeFactsToClear.
    */
   private void clearJessWmeFacts(List<ProblemEdge> wmeFactsToClear) {

       MTRete mtRete = getBrController().getModelTracer().getRete();

       for(int i=0; i<wmeFactsToClear.size(); i++){
           ProblemEdge edge = wmeFactsToClear.get(i);
           mtRete.clearJessWmeFact(edge.getSai().getS());
           wmeFactsToClear.remove(edge);
       }

   }



   /**
//     * this method updates the Jess WME Facts to agree with edgePath
//     * @param edgePath
//     */
//    private void updateJessFacts(Vector edgePath) {
//        //iterate through Jess Facts
//        //if a fact corresponds to an edge in edgePath,
//        //check that it's correct.
//        //If not, fix it.
//        JessModelTracing jmt = getBrController().getModelTracer().getModelTracing();
//        List facts = jmt.getRete().getFacts();
//        for (int i=0; i<facts.size(); i++){
//            Fact fact = (Fact) facts.get(i);
//            String factName = null;
//            try{
//                factName = fact.getSlotValue("name").toString();
//            }
//            catch(Exception e){}
//            
//            ProblemEdge edge = getEdgeFromEdgePath(factName,edgePath);
//            
//            String correctValue = (edge==null) ? //if not in the edgePath, correct value is "nil"
//                    "nil" : (String) edge.getEdgeData().getInput().get(0);
//
//            String factValue;
//            try{
//                factValue = fact.getSlotValue("value").toString();
//            }
//            catch(Exception e){}
//            
//            if (!correctValue.equals(factValue)){ //if incorrect, update
//                getRete().modify(fact, "value", new Value(correctValue));
//            }
//            
//        }
//        
//    }
//    

   public void showJessFacts() {
//      String commName = "Numerator0Value";
       JessModelTracing jmt = getBrController().getModelTracer().getModelTracing();

       MTRete rete = jmt.getRete(); 
       List facts = rete.getFacts();

       int hashC = rete.hashCode();

       if(trace.getDebugCode("wmefacts"))trace.out("wmefacts", "Showing Jess Facts:");
   }


   /**
    * used by FoaGetters to return SsFoaElements with the value in their edgePath (i.e. visited edge history).
    */
   public static ProblemEdge getEdgeWithCommName(String commName, List edgePath){
       ProblemEdge result = null;
       for (int i=0; i<edgePath.size(); i++){
           ProblemEdge edge = (ProblemEdge)edgePath.get(i);
           String selection = (String) edge.getEdgeData().getSelection().get(0);
           if (selection.equals(commName)){                
               result = edge;
               break;
           }
       }
       return result;
   }

   //calls by reflection, the specified reorderPathMethod
   //if not defined, does nothing
   public Vector reorderPath(Vector path) {
       Vector resultPath;
       if (this.isPathOrdererDefined()){
       	if(trace.getDebugCode("graph"))trace.out("graph","original path = " + path);
           resultPath = pathOrderer.pathOrdering(path);
           if(trace.getDebugCode("graph"))trace.out("graph","resultPath = " + resultPath);
       }
       else{
           resultPath = path;
       }
       return resultPath;
   }

   public Vector<ProblemEdge> getSolutionPath(ProblemNode node) {
       Vector<Vector <ProblemEdge>> correctPaths = getAllPaths(node, CORRECT_ONLY);
       return correctPaths.get(0);
   }

   static boolean CORRECT_ONLY = true;

   public Vector<Vector <ProblemEdge>> getAllPaths(ProblemNode root) {
       return getAllPaths(root, !CORRECT_ONLY);
   }

   // Traverses the graph, and every time it reaches a leaf, it adds the path to
   // the leaf.  If correctOnly is true, then only a correct path is accumulated
   public Vector<Vector <ProblemEdge>> getAllPaths(ProblemNode root, boolean correctPathOnly) {

       Vector<Vector<ProblemEdge>> paths = new Vector<Vector <ProblemEdge>>();
       Vector<ProblemEdge> edgePath = new Vector<ProblemEdge>();

       Stack<ProblemEdge> edgeStack = new Stack<ProblemEdge>();

       ProblemEdge edge;
       ProblemNode parentNode, lastParent, targetNode;

       // push all the children of <root> onto <edgeStack>
       // The third argument specifies if ignore edges not to be learned. Thus, if we only need 
       // correct solutions, we must get all the edges so that we can determine 
       // if the path contains incorrect edges or not
       pushChildEdges(root, edgeStack, !correctPathOnly);

       while(!edgeStack.isEmpty()){

           edge = edgeStack.pop();
           parentNode = edge.source;
           targetNode = edge.dest;

           //find parent in edge Path (//except for first edge)
           int index = indexOfLastEdgeInPathContainingNode(parentNode, edgePath);  
           if (index!=-1) //-1 means that there is no edge with parentNode as source.
               removeEdgesAfterInclusive(index, edgePath); //remove that edge and everything after it.
           edgePath.add(edge); //add as the last in the path

           // if this edge is a leaf, then keep the path
           if (edge.dest.getChildren().isEmpty())
               paths.add(new Vector<ProblemEdge>(edgePath));
           else
               lastParent = parentNode;

           pushChildEdges(edge, edgeStack, !correctPathOnly); 
       }

       return paths;
   }

   //24Jan2007: this method removes every edge in 'edgePath' after and including 'index'
   public void removeEdgesAfterInclusive(int index, Vector<ProblemEdge> edgePath) {
       //last element has index edgePath.size - 1
       //let's do some algebra:
       // removalSize = lastIndex - index + 1 =
       //     (edgePath.size - 1) - index + 1 = edgePath.size - index 
       int removalSize = edgePath.size() - index; //size 1, index 0, we remove 1, etc. 

       for (int i=0; i<removalSize; i++) //do the following 'removalSize' times:
           edgePath.remove(edgePath.size()-1); //remove last
   }

   //this method returns the index of the last edge in edgePath that has parentNode as a source
   public int indexOfLastEdgeInPathContainingNode(ProblemNode parentNode, Vector<ProblemEdge> edgePath) {
       int index;
       ProblemEdge edge;
       int lastIndex = edgePath.size()-1;
       for(index = lastIndex; index>=0; index--){ //start at last
           edge = edgePath.get(index);
           if (edge.source.equals(parentNode))
               return index;
       }   	
       return -1;
   }


   //23Jan2007: traverses the graph
   public void updateGivenActions(ProblemNode parentNode){

       /* Stack nodeStack = new Stack(); */
       Stack<ProblemEdge> edgeStack = new Stack<ProblemEdge>();

       pushChildEdges(parentNode, edgeStack, false); //push all the children of start-state

       ProblemEdge edge;

       Vector<ProblemEdge> edgeCache = new Vector<ProblemEdge>();
       Vector<String> selectionCache = new Vector<String>(); //to avoid click on a node/selection twice

       while ( !edgeStack.isEmpty() ) {

           edge = edgeStack.pop();

           if (edgeCache.contains(edge))
               continue;
           edgeCache.add(edge);

           parentNode = edge.source;
           ProblemNode targetNode = edge.dest;

           /* pushChildNodes(targetNode, nodeStack);*/
           pushChildEdges(edge, edgeStack, false);

           EdgeData edgeData = edge.getEdgeData();
           if(trace.getDebugCode("stack"))trace.out("stack", "updateGivenActions: edge " + edge + " has action type = " + edgeData.getActionType());

           String selection = (String) edgeData.getSelection().get(0);
           if(trace.getDebugCode("stack"))trace.out("stack", "updateGivenActions: selection = " + selection);

           //whenever we see a "Given Action" for a new selection
           if (edge.isGiven()&&!selectionCache.contains(selection)) {
               selectionCache.add(selection);
               if(trace.getDebugCode("stack"))trace.out("stack", "updateGivenActions: ....... simulating click on Given Action edge " + edge);
               if (trace.getDebugCode("mt")) trace.out("mt", "Called from updateGivenActions");
               getBrController().getGoToWMStateResponse(targetNode.getName());
               continue;
           }
       }

       if (foaGetter!=null) //inform FoaGetter about the selections that are given
           foaGetter.setGaSelections(selectionCache);

   }

   // Specify types of edge to be learned
   boolean toBeLearned(ProblemEdge edge) {
       return (getLearnCorrectActions() && edge.isCorrect()) ||
              (getLearnCltErrorActions() && edge.isCltErrorAction()) ||
              (getLearnBuggyActions() && edge.isBuggy());
   }

   // API for the SAI object
   public void stepDemonstrated(ProblemNode problemNode, Sai sai, ProblemEdge problemEdge,
                                Vector<ProblemEdge> edgePath) 
   {
       Vector /* String */ selectionV = sai.selectionV;
       Vector /* String */ actionV = sai.actionV;
       Vector /* String */ inputV = sai.inputV;
       stepDemonstrated(problemNode, selectionV, actionV, inputV, problemEdge, edgePath);
   }

   
   
   
   public String instructionIdentifierStem=null;
   
   
   public String getInstructionIdentifierStem() {
	   return instructionIdentifierStem;
   }

   public void setInstructionIdentifierStem(String instructionIdentifierStem) {
	   this.instructionIdentifierStem = instructionIdentifierStem;
   }

//This method sets the focusOfAttention of the instruction object, preparing it for learning
   /**
    * @return null
    */
   public void stepDemonstrated( ProblemNode problemNode,
           Vector /* String */ selectionV, Vector /* String */ actionV, Vector /* String */ inputV,
           ProblemEdge problemEdge, Vector<ProblemEdge> edgePath) {

       // Fri Oct 21 17:02:35 2005
       // Do nothing if the author wishes to do so
       if ( isMissHibernating() ) { return; }

       /*if(problemEdge == null)
       {
       	Exception e = new Exception("problemEdge is null");
       	e.printStackTrace();
       	logger.simStLogException(e);
       	return;
       }*/

       // Dec 6, 2007 :: Noboru
       // If the step is "given", then don't even bother making an "instruction"
       if (problemEdge != null && problemEdge.isGiven() ) { return; }

       String selection = (String)selectionV.get(0);
       String action = (String)actionV.get(0);
       String input = (String)inputV.get(0);

     
     
       // The first "Focus of Attention," which corresponds to
       // selection-action-input.
       String wmeType = getRete().wmeType( selection );
           
       String sai = wmeType + "|" + selection + "|" + input;
       
       String instructionID= instructionIdentifierStem + "_" + problemNode.getName();	
       Instruction instruction;
       
       if (this.isSsBatchMode() || !this.getBrController().getMissController().isPLEon() )
    	   instruction  = new Instruction( problemNode, sai);
       else
    	   instruction  = new Instruction( problemNode, sai, instructionID, this.getSsInteractiveLearning().previousPositiveInstructionID);
       
       if (!this.isSsBatchMode() && this.getBrController().getMissController().isPLEon() )
    	   this.getSsInteractiveLearning().previousPositiveInstructionID=instruction.getInstructionID();
   	
        
       instruction.setAction( action );
       instruction.setRecent(true);

       // Add Focus of Attention, which is either specified by the author
       // or identified by a tutor specific FoA getter
       Vector<FoA> v = getCurrentFoA();

     
       
       
       addInstructionFoA(instruction, selection, action, input, edgePath);
       //if(!getBrController().getMissController().getSimSt().getSsInteractiveLearning().isTypeInStepWithNewFoA())
       //	addInstructionFoA(instruction, selection, action, input, edgePath);
       //else if(getBrController().getMissController().getSimSt().getSsInteractiveLearning().isTypeInStepWithNewFoA())
       //	addInstructionFoAAlternate(instruction, selection, action, input, edgePath);
       // add instruction (which inturn updates Console display)
       addInstruction( instruction );

   
       
       // If the [Done] button is pressed, then assign the name
       // "done" to this step 
       if ( Rule.isDoneAction( selection, action, input ) ) {
           instruction.setName( Rule.DONE_NAME );
           updateConsoleSkillName();
       }

       //save FoA and edges
       if(problemEdge != null)
       	foAMap.put(problemEdge, getFoAWidgetList());
       // Reset focus of attention
       // clearCurrentFoA();
   }

   /**
    * @param instruction
    * @param selection
    * @param actionI
    * @param input
    * @param problemEdge
    * @param edgePath
    */
   //16 April 2008
   //Assuming that a focus of attention is either given or gettable from the FOA getter, add it
   //to the instruction object.
   private void addInstructionFoA(Instruction instruction, String selection, String action, String input, 
           Vector<ProblemEdge> edgePath) {
	
	  
	   
   		List /*of String*/<String> foaStrs = new Vector<String>();
       // If a user defined method to identify focus of attention, 
       // then call that method
       if ( isFoaGetterDefined() ) {
           //if the author, by mistake, specifies FOA manually, then the FoaGetter code would
           //deselects those FOAs (just like double clicking those cells again)
           //clearing the FOA solves this problem
           clearCurrentFoA();

           Vector /* Object */ vFoa = applyFoaGetter( selection, action, input, edgePath );
                      
           // If one of the focus of attention is not specified then we need to tell the student 
           // that they have made an error of skipping a step. Check here if the foa specified have
           // some text input into it
           if (vFoa!=null){
               for (int i = 0; i < vFoa.size(); i++) {
               //	if(trace.getDebugCode("miss"))trace.out("miss", "addInstructionFoA: " + ((TableExpressionCell)vFoa.elementAt(i)).getText());
                   Object wedget = vFoa.get(i);
                   toggleFocusOfAttention(wedget);
               }
           }
       }
       printFoa();
       //regardless of how the FOA was gotten, we now populate foaStrs
       for (int i = 0; i < numCurrentFoA(); i++) {
           String foaStr = getCurrentFoA().get(i).foaString();
          
           foaStrs.add(foaStr);//getCurrentFoA().get(i).foaString());
       }
       
       
       updateInstructionFoa(instruction, foaStrs);
   }

   public void updateInstructionFoa(Instruction instruction, List<String> foaStrs) {
       if ( !foaStrs.isEmpty() ) {
           for (int i = 0; i < foaStrs.size(); i++) {
               String foaStr = foaStrs.get(i);
               instruction.addFocusOfAttention( foaStr );
           }
       } else {

           if (!isFoaSearch()) {

               Exception e = new Exception( "Focus of Attention not specified" );
               e.printStackTrace();
               // Wed Oct 25 16:37:49 LDT 2006 :: Noboru
               // Added for Wilkinsburg study -- BRDs converted from log are so broken...
               System.exit(-1);
           }
       }
     
   }

   /**
    * Creates a new node and edge for the hint received from the specified oracle. The way new node
    * and edge is created depends upon the oracle which provided the hint
    * @param hint
    * @param currentNode
    */
   public void createNewNodeAndEdgeForHintReceived(AskHint hint, ProblemNode currentNode) {
   	
   	String hintMethodName = getHintMethod();
		ProblemNode childNode = null;
		ProblemEdge childEdge = null;
	
   	if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_BRD)) {
   		
   		// Get the children of the currentNode in the BRD
   		for(Iterator<ProblemNode> itr = currentNode.getChildren().iterator(); itr.hasNext();) {
   			childNode = itr.next();
   			childEdge = lookupProblemEdge(currentNode, childNode);
   			if(childEdge.isCorrect())
   				break;
   		}
   		
   	} else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_CL)) {
   		
   		trace.err("You need to implement new node and edge creation for oracle: " + hintMethodName);
   		
   	} else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_FAKE_CLT)) {

   		SimStNodeEdge ssNodeEdge = makeNewNodeAndEdge(hint.getSai(), currentNode);
   		childNode = ssNodeEdge.node;
   		childEdge = ssNodeEdge.edge;

   	} else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_FTS)) {
   		
   		if(getSsInteractiveLearning() != null) {
   			childNode = getSsInteractiveLearning().simulatePerformingStep(getBrController().getCurrentNode(), hint.getSai());
   			childEdge = lookupProblemEdge(getBrController().getCurrentNode(), childNode);
   		}

   	} else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_HD)) {  		
   		// For the human demonstration the currentNode is actually the parentNode
   		ProblemNode parentNode = currentNode;
   		childNode = getBrController().getCurrentNode();
   		childEdge = getBrController().getProblemModel().returnsEdge(parentNode, childNode);
   			
   		/*Update the brd graph with the skill name */
   	//	if (getBrController().getMissController().isSimStPleOn() ){
   		//  this is related to item 177
   		//	String defaultSkillName = (String) childEdge.getEdgeData().getRuleNames().get(childEdge.getEdgeData().getRuleNames().size() - 1);
   	//		childEdge.getEdgeData().replaceRuleName("", hint.skillName);
   	//		getBrController().getJGraphWindow().getJGraph().repaint();
   	//	}
   		
   			
   	}else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_WEBAUTHORING)) {  		
   		//ProblemNode parentNode = currentNode;
   		//childNode = getBrController().getCurrentNode();
   		//childEdge = getBrController().getProblemModel().returnsEdge(parentNode, childNode);
   		
   		SimStNodeEdge ssNodeEdge = makeNewNodeAndEdge(hint.getSai(), currentNode);
   		childNode = ssNodeEdge.node;
   		childEdge = ssNodeEdge.edge;
   		
   		
   		
   		
   	} else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_SOLVER_TUTOR)) {
   		
   		if(!getBrController().getMissController().getSimSt().isValidationMode() && 
   			(getBrController().getMissController().getSimSt().getSsInteractiveLearning().isRunningFromBrd() ||
   			getBrController().getMissController().getSimSt().getHintMethod().equals(AskHint.HINT_METHOD_SOLVER_TUTOR))) {

   			SimStNodeEdge ssNodeEdge = makeNewNodeAndEdge(hint.getSai(), currentNode);
   			childNode = ssNodeEdge.node;
   			childEdge = ssNodeEdge.edge;
   		}

   	} else
   		new Exception("No valid oracle specified.");
   	
		hint.setNode(childNode);
		hint.setEdge(childEdge);
   }

   
   //19 April 2007
   //given an SAI and current node, this constructs a new node and a new edge, as a child of currentNode.
   public SimStNodeEdge makeNewNodeAndEdge(Sai sai, ProblemNode currentNode) {
	  
       BR_Controller brController = getBrController();
      
       
       //first click on current node
       brController.setCurrentNode2(currentNode);
     
      
       
       //code inspired by doOneMatchedNode()

       ProblemNode newNode = 
           brController.addNewState(brController.getSolutionState().getCurrentNode(), 
                                    sai.selectionV, sai.actionV, sai.inputV,
                                    null, EdgeData.CORRECT_ACTION); //null messageObject, since we don't care
 
       ProblemEdge newEdge = brController.getProblemModel().returnsEdge(currentNode,newNode); 

       return new SimStNodeEdge(newNode, newEdge);
   }

//    private String checkProductionModel( ProblemNode problemNode ) {
//
//        String ruleFired = null;
//
//        // Store the current mode
//        BR_Controller brController = getBrController();
//        // String BRmode = brController.getCtatModeModel().getModeTitle();
//        // Switch to Prod.System Mode
//        // brController.getCtatModeModel().setMode(CtatModeModel.TYPE_JESS);
//
//        // Get the current step to the problemNode model traced
//        Vector /* ProblemNode */ parents = problemNode.getParents();
//        if (parents.size() > 1) {
//            Exception e = new Exception();
//            e.printStackTrace();
//        }
//        // Switch to the parent node and model trace a single step to the problem node
//        ProblemNode parentNode = (ProblemNode)parents.get(0);
//        brController.setCurrentNode( parentNode );
//
//        // ProblemNode currentNode = brController.getSolutionState().getCurrentNode();
//        MessageObject response = brController.getGoToWMStateResponse(problemNode.getName());
//
//        CheckLinksList clList = CheckLinksList.getCheckedLinksList(response);
//        int targetID = clList.size() -1;
//
//        // Check the result 
//        String mtStatus = clList.getCheckResult(targetID);
//
//        // if SUCCESS
//        if ( mtStatus.equals(EdgeData.SUCCESS) ) {
//            List skillNameList = clList.getRuleSeq(targetID);
//            if ( !skillNameList.isEmpty() ) {
//                ruleFired = (String)skillNameList.get(0);
//                // strip off "MAIN::" from the skillName, if any 
//                int n = ruleFired.indexOf("::");
//                if ( n > 0 ) {
//                    ruleFired= ruleFired.substring(n +2);
//                }
//            }
//        }
//
//        // Get the edge information
//        // Vector /* ProblemEdge */ edges = new Vector();
//        // parentNode.findDescendentEdgesList( parentNode, edges );
//
//        // For some unknown reason (bug?) problemNode.getIncommingEdges() does not work here
//        // You must go with parentNode.getOutgoingEdges() instead...
//        List edges = parentNode.getOutgoingEdges();
//        ProblemEdge edge = (ProblemEdge)edges.get(0); //only single edge coming in
//        EdgeData edgeData = edge.getEdgeData();
//        Vector /* String */ skillNames = edgeData.getSkills();
//        String trueSkillName = (String)skillNames.get(0);
//
//        /*
//         * I have "fixed" NullPointerException that happens further down checkAddRuleName(),
//         * but, when NOT commented-out, simStSmokeTest.sh fails  (May 13, 2006) Noboru
//         * 
//         * Sung-Joo: I changed to enable this feature when CTAT is not running in the 
//         * batch mode. If user demonstrates, it should change the skill name automatically.
//         */ 
//        if (( ruleFired != null ) && !isBatchMode) {
//            //Change the skill name to the fired rule name if there is a match.
//            BR_Controller controller = getBrController();
//            controller.getProblemModel().checkAddRuleName(ruleFired, this.productionSet);
//            Vector labels = edge.getEdgeData().getRuleLabels();
//            RuleLabel label = (RuleLabel)labels.get(0);
//            label.setText(ruleFired + " " + this.productionSet);
//        }
//
//        // Switch back to the original node that has been just demonstrated
//        brController.setCurrentNode( problemNode );
//
//        return ruleFired;
//    }

   public /*private*/ ProblemEdge lookupProblemEdge(ProblemNode parentNode, ProblemNode targetNode) {
       ProblemGraph pg = getBrController().getProblemModel().getProblemGraph(); 
       return pg.lookupProblemEdge(parentNode, targetNode);
   }

   private void pushChildNodes(ProblemNode parentNode, Stack<ProblemNode> nodeStack) {

       Vector /* ProblemNode */ children = parentNode.getChildren();
       for (Iterator iter = children.iterator(); iter.hasNext();) {
           ProblemNode child = (ProblemNode) iter.next();
           nodeStack.push(child);
       }
   }

   //24Jan2007: converting ssRunDemonstrationInBatchMode to have a stack of edges
   private void pushChildEdges(ProblemNode parentNode, Stack<ProblemEdge> edgeStack, boolean ignoreEdgesNotToBeLearned) {

       Vector /* ProblemNode */ children = parentNode.getChildren();
       for (Iterator iter = children.iterator(); iter.hasNext();) {
           ProblemNode child = (ProblemNode) iter.next();
           ProblemEdge edge = lookupProblemEdge(parentNode, child);
           if (!ignoreEdgesNotToBeLearned || toBeLearned(edge)) //if ignoring, only add edges to be learned
               edgeStack.push(edge);
       }
   }

   //24Jan2007: converting ssRunDemonstrationInBatchMode to have a stack of edges
   private void pushChildEdges(ProblemEdge edge, Stack<ProblemEdge> edgeStack, boolean ignoreEdgesNotToBeLearned) {
       pushChildEdges(edge.dest, edgeStack, ignoreEdgesNotToBeLearned);
   }

   private void debugPringSAI(Vector selection, Vector action, Vector input) {
       trace.out("miss", 
               "  selection: " + (selection == null || selection.isEmpty() ? "" : selection.get(0)) );
       trace.out("miss", 
               "     action: " + (action == null || action.isEmpty() ? "" : action.get(0)));
       trace.out("miss", 
               "      input: " + (input == null || input.isEmpty() ? "" : input.get(0)) );
   }

   // Read focus of attention from a BRD file
   public /*private*/ Vector /* String[] */<String[]> readFocusOfAttentionFromBRD(String brdFile) {

       Vector<String[]> foaV = new Vector<String[]>();

       String xmlString = readStateGraph( brdFile );
       if(trace.getDebugCode("miss"))trace.out( "xmlString = " + xmlString );

       Vector /* String */<String> edges = readEdgeElements( xmlString );
       if(trace.getDebugCode("miss"))trace.out("miss", "readEdgeElements: " + edges.size() + " edges read..." );

       for ( int i = 0; i < edges.size(); i++ ) {
           String [] foaArray =
               readFocusOfAttentionForEdge( edges.get(i) );
           foaV.add( foaArray );
       }

       return foaV;
   }

   private String[] readFocusOfAttentionForEdge( String xmlEdgeString ) {

   	if(trace.getDebugCode("miss"))trace.out("miss", "readFocusOfAttentionForEdge: ---");
   	if(trace.getDebugCode("miss"))trace.out("miss", xmlEdgeString);

       Vector /* String */<String> foaV =
           readXmlElements( xmlEdgeString, "focusOfAttention" );

       String[] foaArray = new String[ foaV.size() ];

       for (int i = 0; i < foaV.size(); i++) {
           String foaXmlString = foaV.get(i);
           if(trace.getDebugCode("miss"))trace.out( "miss", foaXmlString );
           foaArray[i] = getXmlTagField( foaXmlString, "target" );
           if(trace.getDebugCode("miss"))trace.out("miss", "foaArray[" + i + "] = " + foaArray[i]);
       }

       return foaArray;
   }

   // Extract a list of <edge>....</edge> from a give xmlString
   private Vector /* String */<String> readEdgeElements( String xmlString ) {
       return readXmlElements( xmlString, "edge" );
   }

   // Extract a list of <xmlTag>...</xmlTag> from a give xmlString
   private Vector /* String */<String> readXmlElements( String xmlString, String xmlTag ) {

       Vector<String> edgeElements = new Vector<String>();

       // true if it's reading a element name between '<' and '>'
       boolean readingElementName = false;
       // true if it's reading contents of the element <xmlTag>
       boolean readingEdgeElement = false;
       // true if '/' has been read
       // boolean closingElement = false;

       // a temporal string buffer
       String xmlBody = "";
       String elementName = "";

       StringReader reader = new StringReader( xmlString );
       int c;
       try {
           while ( (c = reader.read()) != -1 ) {

               if ( readingEdgeElement ) {
                   xmlBody += String.valueOf((char)c);
               }
               if ( readingElementName && c != '>' ) {
                   elementName += String.valueOf((char)c);
               }

               switch (c) {
               case '<':
                   if ( !readingElementName ) {
                       elementName = "";
                       readingElementName = true;
                   }
                   break;
               case '>':
               case ' ':
                   if ( readingElementName ) {
                       // <edge> has been read
                       if ( elementName.equalsIgnoreCase( xmlTag ) ) {
                           xmlBody = "<" + xmlTag + ">";
                           readingEdgeElement = true;
                           // </edge> has been read
                       } else if ( elementName.equalsIgnoreCase( "/" + xmlTag ) ) {
                           readingEdgeElement = false;
                           // closingElement = false;
                           edgeElements.add( xmlBody );
                           xmlBody = "";
                       }
                       readingElementName = false;
                       elementName = "";
                   }
                   break;
               case '/':
                   // closingElement = true;
                   break;
               default:
                   break;
               }
           }
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       return edgeElements;
   }

   private static String getXmlTagField( String xmlString, String tag ) {

       String body = "";

       int start = xmlString.indexOf( "<" + tag + ">" ) + tag.length() + 2;
       int end = xmlString.indexOf( "</" + tag + ">" );
       body = xmlString.substring( start, end );

       return body;
   }

   private String readStateGraph( String brdFileName ) {

       String xmlString = "";

       File brdFile = new File( brdFileName );
       FileReader brdFileReader = null;
       try {
           brdFileReader = new FileReader( brdFile );
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
       BufferedReader brdReader = new BufferedReader( brdFileReader );

       boolean stateGraphRead = false;

       String lineStr;
       try {
           while ( (lineStr = brdReader.readLine()) != null ) {

               if ( !lineStr.equals("") ) {

                   if ( lineStr.charAt(0) == ' ' ) {
                       lineStr = lineStr.replaceFirst( "[ \t]+", "" );
                   }

                   String[] lineTokens = lineStr.split("[ \t]+");

                   // "stateGraph" has not been read
                   if ( !stateGraphRead ) {

                       if ( lineTokens[0].equalsIgnoreCase( "<stateGraph" ) ) {
                           stateGraphRead = true;
                           xmlString += lineStr + " ";
                       }

                       // "stateGraph" has been read
                   } else {

                       xmlString += lineStr + " ";

                   }
               }
           }
           brdReader.close();
           brdFileReader.close();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       return xmlString;
   }

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   // Validation test for the production rules 
   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

   /**
    * Triggered by the "Prod.System" menu in the Sim. St. console
    *
    */
   public void testProductionModel() {

       BR_Controller brc = getBrController();
       ProblemModel pm = brc.getProblemModel();

       if ( pm.getStartNode() != null &&
               pm.getProblemGraph().degree(pm.getStartNode()) <= 0 ) {

           // Let the BR controller that testing production model is
           // initiated by Sim St. so that
           // processCheckAllstatesResult() doesn't pops-up a
           // dialogue window
           brc.setCheckAllStatesBySimSt( true );

           // Code from BR_ActionHandler.java
           Vector eseGraph = new Vector();
           pm.setCheckAllNodes(new Vector());
           brc.setSendESEGraphFlag(false);
           brc.sendBehaviorRecorderGraphToLisp(eseGraph, pm.getStartNode(), 0);
           pm.setCheckAllNodes(new Vector());

           // reset the ad-hoc flag
           brc.setCheckAllStatesBySimSt( false );

           if(trace.getDebugCode("miss"))trace.out("miss", "testProductionModel: " );
           if(trace.getDebugCode("miss"))trace.out("miss", "eseGraph = " + eseGraph.toString());
       }
   }


   /**
    * Triggered by the "Production.System" menu in the Sim. St. Console
    *
    **/
   public void testProductionModelOn() {
       // testProductionModelOn( null, null );
       String file = "8x_16.brd";
       String directory = getProjectDir() + "/Problems/";
       testProductionModelOn( file, directory, getTestLogFile() );
   }

   /*
   public void testProductionModelOn( String problemName ) {
       testProductionModelOn( problemName, "", getTestLogFile() );
   }
    */

   // Called when SimSt is launched just for validation test
   public void testProductionModelOn( String[] testSet, String output ) {
       for (int i = 0; i < testSet.length; i++) {
           testProductionModelOn( testSet[i], LOG_PRTEST_TEST, output );
       }
   }

   public void testProductionModelOn( String problemName, String logMode, String output ) {
       testProductionModelOn( problemName, logMode, "", output );
   }

   public boolean testProductionModelNoTest = false;
   public void setTestProductionModelNoTest(boolean flag) {
       testProductionModelNoTest = flag;
   }
   public boolean getTestProductionModelNoTest(){
       return testProductionModelNoTest;
   }

   public void testProductionModelOn( String problemName, String logMode, String directory, String output ) {

       // Sun Nov 12 02:47:26 EST 2006 :: Noboru
       // for debugging a memory problem 
       if (testProductionModelNoTest) return;

       // Number of test cut-off due to an out-of-memory problem
       if (getNumTested() >= MAX_NUM_TEST) {
       	if(trace.getDebugCode("miss"))trace.out("miss", "MAX_NUM_TEST exceeded #########################");
           return;
       }

       
       // Recursively call testProductionModelOn when the target problem file is a directory
       File problemFile = new File(problemName); 
       
       if (problemFile.isDirectory()) {
           String brdFiles[] = problemFile.list();
                     
           for (int i = 0; i < brdFiles.length; i++) {
               File childFile = new File(problemFile, brdFiles[i]);
               String childFileName = new File(problemFile, brdFiles[i]).getAbsolutePath();
               if (childFileName.matches(".*brd$") || childFile.isDirectory()
               		|| childFileName.matches(".*txt$")) {
            	   
            	   /*save in directory the right above folder of the brd*/
            	   if (!childFileName.contains(".brd"))
            		   directory=brdFiles[i];
                   
            	   testProductionModelOn(childFileName, logMode, directory, output );
               }
           }

       }
       else if(problemName.matches(".*txt$")  )
       {
    	 
       	setIsValidationMode(true);
       	
       	//Txt file should be list of problem name files
       	List<String> problems = loadProblemNamesFile(problemName);
       	for(String problem:problems)
       	{
				/* Treat ";;" in the txt file as comments */
				int index = problem.indexOf(";;");
				if (index == 0)
					continue;
				else if (index > 0)
					problem = problem.substring(0, index);

				problem = problem.trim();
				if(problem.length() == 0)
					continue;
				
		        try
		        {
		        	validateProblem(problem, output);
		        }catch(Exception e)
		        {
		        	trace.err("Error in validating problem "+problem);
		        	e.printStackTrace();
		        }
       	}
       	
       	setIsValidationMode(false);
       	return;
       } 
       else {
    	   	setIsValidationMode(true);
       	
     
       	
       		problemName=problemName.replace(" ", "");
       		problemName=problemName.replace("=", "_");
       	
           ssTestProductionModelOnBRD(problemName, logMode, directory, output);

           setIsValidationMode(false);
       }
   }



   private void validateProblem(String problem, String output)
   {
   	incNumTested();
   	
   	String safeProblemName = SimSt.convertToSafeProblemName(problem);
   	setValidationGraph(new SimStProblemGraph());
   	SimStNode startNode = new SimStNode(safeProblemName, getValidationGraph());
   	getValidationGraph().setStartNode(startNode);
   	getValidationGraph().addSSNode(startNode);
   	String[] startState = readStartStateElements();
   	//String[] startState = {"dorminTable1_C1R1","dorminTable1_C2R1"};
   	//getSsRete().setStartStateElements(startState);
   	
   	SimStNode currentNode = null, nextCurrentNode = null;
   	HashMap hm = new HashMap(); // Mapping between the RAN and the SimStNode
   	
		boolean killMessageReceived = false;
		currentNode = startNode;
		
		while (!killMessageReceived ) {

			if(trace.getDebugCode("rr"))
				trace.out("rr", "currentNode: " + currentNode);
			long activationListStartTime = Calendar.getInstance().getTimeInMillis();
			Vector activationList = gatherActivationList(currentNode, hm);
				
			Collection<RuleActivationNode> activList = createOrderedActivationList(activationList);
			int activationListDuration = (int) ((Calendar.getInstance().getTimeInMillis() - activationListStartTime)/1000);
			
			
			//trace.err("Activation list is " + activationList);
			SimStNode toVisit = validateActivations(currentNode, activList, hm, output);
			if(trace.getDebugCode("rr"))
				trace.out("rr", "toVisitNode: " + toVisit);
			
			if (toVisit != null) {
				currentNode = toVisit;
			} else {
				killMessageReceived = true;
			}

			if(currentNode.isDoneState()) {
				break;
			}
		}
   }



   private SimStNode validateActivations(SimStNode currentNode, Collection<RuleActivationNode> activationList, HashMap hm, String output) 
	{
   	String listAssessment = "";
   	SimStNode nextCurrentNode = null, successiveNode = null;
   	RuleActivationNode backUpRAN = null;
   	
   	SimStNode startNode = getValidationGraph().getStartNode();
   	String problemName = startNode.getName();
   	
   	SimStNode hintNode = null;
   		//	AskHint hint = new AskHintInBuiltClAlgebraTutor(getBrController(), currentNode);
   		//CL oracle must not be hardcoded! Whichever oracle grades the quiz should provide this hint
		AskHint hint = askForHintQuizGradingOracle(getBrController(),currentNode);
   	
   	if(trace.getDebugCode("rr"))
   		trace.out("rr", "hint: " + hint);
   	
   	setProblemStepString(getProblemAssessor().calcProblemStepString(startNode, currentNode, null));
   	        
   	RuleActivationNode randomRan = null;
   	for(RuleActivationNode ran: activationList) 
   	{
           trace.out("ss", "Checking RAN: "+ran);

   		if(ran.getActualInput().equalsIgnoreCase("FALSE"))
   			continue;
   		

   		successiveNode = inspectActivation(currentNode, ran, output);
   		if(trace.getDebugCode("miss"))trace.out("miss", "successiveNode: " + successiveNode);
   		
   		if(successiveNode != null) 
   		{   
   			hm.put(ran, successiveNode);
   		}
   		randomRan = ran;
   	}
   	    	
       String step = startNode.getName();
       if(getProblemAssessor() != null)
       {
       	step = getProblemAssessor().findLastStep(startNode, currentNode);
       	setLastSkillOperand(getProblemAssessor().findLastOperand(startNode, currentNode));
       }
       setProblemStep(step);
       
       RuleActivationNode correctRan = null;
       RuleActivationNode inspectedRan = null;
       int correct = -1;
       for (RuleActivationNode ran:activationList) {
       	if(!dontShowAllRA() || correct == -1)
       	{
	        	inspectedRan = ran;
	            if (!ran.getActualInput().equalsIgnoreCase("FALSE")) 
	            {
	            	
	            	int result = logRuleActivationOracled(LOG_PRTEST_AGENDA,problemName, ran, currentNode, output, hint);
	            	correct = result;
	            	if (result==1){ 
	            	
	            		correctRan=ran;
	            	
	            	}
	            }
       	}
        }
       
       if (correctRan==null)
    	   correctRan=inspectedRan;
       
       //log the test
       logRuleActivationOracled(LOG_PRTEST_TEST,problemName, correctRan, currentNode, output, hint);

       // If there were no rule activations then log the step the oracle suggests. This helps to maintain
       // consistency in the validation output.
       //if(activationList.size() <= 0) {
       	//logRuleActivationOracled(problemName, null, currentNode, output, hint);
       //}

       if(trace.getDebugCode("rr"))
       	trace.out("rr", "hint: " + hint.getSai());
       
      // hintNode = new SimStGraphNavigator().simulatePerformingStep(currentNode, hint.getSai());
       
      // trace.err("CurrentNode is " + currentNode.getName());
      // trace.err("correct rAn is " + correctRan);
       
       if (correctRan!=null)
    	   hintNode = new SimStGraphNavigator().simulatePerformingStep(currentNode,new Sai(correctRan.getActualSelection(),correctRan.getActualAction(),correctRan.getActualInput()));
       
       return hintNode;
   }

	private SimStNode inspectActivation(SimStNode currentNode, RuleActivationNode ran,String output) {
	

		if(trace.getDebugCode("miss"))trace.out("miss", "Enter inspectActivation");
		SimStNode nextCurrentNode = null;

       Sai sai = new Sai(ran.getActualSelection(),ran.getActualAction(),ran.getActualInput());
       if(sai.getS().equals("NotSpecified") || sai.getI().equals("NotSpecified") )
       	return null;

       //The node we get if we run this step
       SimStNode successiveNode = new SimStGraphNavigator().simulatePerformingStep(currentNode, sai);

       //how could successiveNode be null?
       if (successiveNode != null) {

       	SimStEdge edge = getValidationGraph().lookUpSSEdge(currentNode, successiveNode);
           String problemName = getValidationGraph().getStartNode().getName();

           String skill = "";

           if(isSkillNameGetterDefined())
           {
           	skill = getSkillNameGetter().skillNameGetter(getBrController(), sai.getS(), sai.getA(), sai.getI());
           }
           else 
           {
           	skill = ran.getName();
           }

           edge.getEdgeData().addRuleName(skill);

           //Ask if the rule is correct
           String inquiryResult = inquiryRuleActivation(problemName, currentNode, ran);

           //Mark as not correct
           if (!inquiryResult.equals(EdgeData.CORRECT_ACTION)){
               edge.getEdgeData().setActionType(EdgeData.CLT_ERROR_ACTION);
           }  

           // Update a state (or proceed a step, if you will)
          	nextCurrentNode = successiveNode;
       }
       return nextCurrentNode;
	}
		
   //Reads the configuration file and look for the section with the start state elements
	//@return the list of start state elements
   public String[] readStartStateElements()
   {
   	ArrayList<String> startStateElements = new ArrayList<String>();
   	String file = SimStPLE.CONFIG_FILE;
   	ClassLoader cl = this.getClass().getClassLoader();
   	InputStream is = cl.getResourceAsStream(file);
   	InputStreamReader isr = new InputStreamReader(is);
   	BufferedReader reader=null;
   	try
   	{
   		//reader = new BufferedReader(new FileReader(CONFIG_FILE));
       	reader = new BufferedReader(isr);
   		String line = reader.readLine();
   		
   		while(line != null)
   		{
   			if(line.equals(SimStPLE.START_STATE_ELEMENTS_HEADER))
   			{
					line = reader.readLine();
   				while(line != null && line.length() > 0) //Blank line starts next section
   				{
   					startStateElements.add(line);
   					line = reader.readLine();
   				}
   			}
   			else
   			{
   				line = reader.readLine();
   			}
   		}

   	}catch(Exception e)
   	{
   		if(trace.getDebugCode("miss"))trace.out("miss", "Unable to read config file: "+e.getMessage());
   		e.printStackTrace();
   		logger.simStLogException(e,"Unable to read config file: "+e.getMessage());
   	}finally 
   	{
   		try{reader.close();}catch(Exception e){
       		logger.simStLogException(e);
       		}
   	}
   	String[] startStateArray = new String[startStateElements.size()];
   	for(int i=0;i<startStateElements.size();i++)
   		startStateArray[i] = startStateElements.get(i);
   	return startStateArray;
   }

   // TODO
   private void testPrModelAmlRete(String problemName, String logMode, String output) {
   	incNumTested();
   	SsProblem problem = new SsProblem(this, problemName);
   	SsProblemNode startNode = problem.getStartNode();
   	
   	Vector<SsProblemEdge> edgeVisited = new Vector<SsProblemEdge>();
   	LinkedList<SsProblemEdge> edgeToVisit = new LinkedList<SsProblemEdge>(startNode.getOutGoingEdges());

   	resetLastSkillOperand();
   	
   	// TODO uncommnet this...
   	/*
   	while (!edgeToVisit.isEmpty()) {
   		
   		SsProblemEdge edge = (SsProblemEdge)edgeToVisit.poll();
   		
   		if (isSubjectToTest(edge) && !edgeVisited.contains(edge)) {
   			
   			SsProblemNode node = edge.getSource();
   			setProblemStep(findLastEquation(startNode, node));

   			boolean isCorrectEdge = edge.getActionType().equals(EdgeData.CORRECT_ACTION);
   			boolean isCorrectRa = false;
   			RuleActivationNode tmpRa, actualRa, lastRan = null;
   			String flagPrediction, stepPrediction = null;
   			
   			Vector<RuleActivationNode> activationList = problem.gatherActivationList(node);
   			
   			for (RuleActivationNode ran: activationList) {
   				if (!ran.getActualInput().equalsIgnoreCase("FALSE")) {
   					int result = logRuleActivation(problemName, ran, node, edge, output);
   					if (result > 0) {
   						isCorrectRa = true;
   						if (actualRa == null) actualRa = ran;
   					}
   					if (stepPrediction == null && isStepModelTraced(edge, ran)) {
   						stepPrediction = isCorrectEdge ? TRUE_POSITIVE : TRUE_NEGATIVE;
   						actualRa = ran;
   					}
   				}
   				lastRan = ran;
   			}
   			// There should have been no correct rule activations found, or
   			// the step isn't model traced
   			if (actualRa == null) actualRa = lastRan;
   			
   			String actualStatus = isCorrectRa ? EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION;
               if (isCorrectEdge) {
                   flagPrediction = isCorrectRa ? TRUE_POSITIVE : FALSE_NEGATIVE;
                   if (stepPrediction == null) {
                       stepPrediction = isCorrectRa ? FALSE_POSITIVE : FALSE_NEGATIVE;
                   }
               } else {
                   flagPrediction = isCorrectRa ? FALSE_POSITIVE : TRUE_NEGATIVE;
                   if (stepPrediction == null) {
                       stepPrediction = isCorrectRa ? FALSE_POSITIVE : FALSE_NEGATIVE;
                   }
               }

               // Record if the step is model traced or not.
               logModelTraceStatus(problemName, node, edge, 
               		actualRa, actualStatus, flagPrediction, stepPrediction, output);

               edgeVisited.add(edge);

               Vector<SsProblemEdge> edges = edge.getDest().getOutGoingEdges();
               for (SsProblemEdge newEdge: edges) {
                   edgeToVisit.add(newEdge);
               }

               String modelInput = (String)edge.getInput().get(0);
               if (isCorrectEdge && EqFeaturePredicate.isValidSimpleSkill(modelInput.split(" ")[0])) {
                   setLastSkillOperand(modelInput);
               }
   		}
   	}
   	*/
   }

   private void testProductionModelOnBRD( String problemName, String logMode, String directory, String output ) {

       // Sun Nov 19 13:40:32 EST 2006 :: Noboru
       // Quite ad-hoc, but needed to run SimSt over Wilkinsburg data
       // If the specified trainiing is known to be a broken BRD (which,
       // is pre-computed and information stored into a file), then skip
       if (isCheckWilkinsburgBadBrdFile()) {
           String brokenType = isBadWilkinsburgBrdFile(problemName);
           if (brokenType != null){
           	if(trace.getDebugCode("miss"))trace.out("miss", "BAD Training file: " + problemName + " reported in " + brokenType);
               return;
           }
       }

       // Add a tolly for the # test problems
       incNumTested();
       // Initialize variables and reset some Jess related stuff 
       testProductionModelOnBRD_init();
       // Load the BRD
       testProductionModelOnBRD_loadBRD(problemName, directory);

       // Switch to the TEST_TUTOR Mode
       switchToSimStMode();
       BR_Controller brController = getBrController();
       brController.getCtatModeModel().setAuthorMode(CtatModeModel.TESTING_TUTOR);

       // Now, get the steps in the BRD tested...
       long sTime = (new Date()).getTime();
       testProductionModelOnBRD_testSteps(logMode, output, problemName);
       long eTime = (new Date()).getTime();
       if(trace.getDebugCode("miss"))trace.out("miss", "testProductionModelOnBRD done in " + (eTime - sTime) + "ms.");

       // Switch back to the SimStudent mode
       brController.getCtatModeModel().setAuthorMode(CtatModeModel.DEMONSTRATING_SOLUTION);
   }

   private void ssTestProductionModelOnBRD( String problemName, String logMode, String directory, String output ) {

       // Sun Nov 19 13:40:32 EST 2006 :: Noboru
       // Quite ad-hoc, but needed to run SimSt over Wilkinsburg data
       // If the specified trainiing is known to be a broken BRD (which,
       // is pre-computed and information stored into a file), then skip
       if (isCheckWilkinsburgBadBrdFile()) {
           String brokenType = isBadWilkinsburgBrdFile(problemName);
           if (brokenType != null){
           	if(trace.getDebugCode("miss"))trace.out("miss", "BAD Training file: " + problemName + " reported in " + brokenType);
               return;
           }
       }

       // Add a tolly for the # test problems
       incNumTested();
       // Initialize variables and reset some Jess related stuff 
       testProductionModelOnBRD_init();
       // Load the BRD
        SimStBrdGraphReader reader = new SimStBrdGraphReader();  
   	    reader.openBRDFile(problemName);  	    
   	    setValidationGraph(reader.getProblemGraph());
   	
   	 if(trace.getDebugCode("nbarbaBrd"))trace.out("nbarbaBrd","Validation method is " + this.getPrValidationMethod());
       // Now, get the steps in the BRD tested...
       long sTime = (new Date()).getTime();
       
       if (this.getPrValidationMethod().equals("modeltracing")){
    	   
    	   ssTestProductionModelOnBRD_testStepsModelTracing(logMode, output, problemName,directory);
       }
       else{
    	   ssTestProductionModelOnBRD_testStepsCognitiveFidelity(logMode, output, problemName);
    	   
       }
       //ssTestProductionModelOnBRD_testSteps(logMode, output, problemName);
       
       long eTime = (new Date()).getTime();
       if(trace.getDebugCode("miss"))trace.out("miss", "testProductionModelOnBRD done in " + (eTime - sTime) + "ms.");

   }

   private void testProductionModelOnBRD_loadBRD(String problemName, String directory) {

       BR_Controller brController = getBrController();

       // Load a BRD file
       long sTime = (new Date()).getTime();
       if ( problemName != null && directory != null ) {
       	// FIXME: boolean added to doLoadBRDFile; using true here, but should be fixed
           LoadFileDialog.doLoadBRDFile(brController, problemName, directory, true);
       } else {
           //LoadFileDialog.doDialog(brController);
       	// FIXME: needs a boolean for multiple window addition - going with true for now
           LoadFileDialog.doDialog(brController, true);
       }
       long eTime = (new Date()).getTime();
       if(trace.getDebugCode("miss"))trace.out("miss", "testProductionModelOnBRD: " + problemName + " loaded in " + (eTime - sTime) + "ms.");
   }

   private void testProductionModelOnBRD_init() {
       long sTime = (new Date()).getTime();
       clearJessConsole();
       resetMT();
       // Sun Sep 17 00:41:28 LDT 2006 :: Noboru 
       // Communicator has a static Hashtable!!
       Communicator.reset();
       long eTime = (new Date()).getTime();
       if(trace.getDebugCode("miss"))trace.out("miss", "testProductionModelOnBRD initialization done in " + (eTime - sTime) + "ms.");
   }

   // Used for logRuleActivationToFile() to record a problem state for Algebra I Tutor data, 
   // which has a step to enter a skill-operand (e.g., "add -1"). 
   // 
   private String lastSkillOperand = null;
   public String getLastSkillOperand() { return lastSkillOperand; }
   public void setLastSkillOperand(String lastSkillOperand) {
   	if(this.lastSkillOperand != null)
   		lastSkillOperandHistory.add(this.lastSkillOperand);
   	this.lastSkillOperand = lastSkillOperand;
   }
   void resetLastSkillOperand() { this.lastSkillOperand = null; }

   private List<String> lastSkillOperandHistory = new LinkedList<String>();
   public String revertLastSkillOperand()
   {
   	if(lastSkillOperandHistory.size() > 0)
   	{
   		lastSkillOperand = lastSkillOperandHistory.remove(lastSkillOperandHistory.size()-1);
   	}
   	return lastSkillOperand;
   }


   private void testProductionModelOnBRD_testSteps(String logMode, String output, String problemName) {

       BR_Controller brController = getBrController();
       ProblemNode startNode = brController.getProblemModel().getStartNode();

       Vector /* ProblemEdge */<ProblemEdge> edgeVisited = new Vector<ProblemEdge>();
       LinkedList /* ProblemEdge */<ProblemEdge> edgeToVisit = new LinkedList<ProblemEdge>();
       List edges = startNode.getOutgoingEdges();
       for (int i = 0; i < edges.size(); i++) {
           edgeToVisit.add((ProblemEdge)edges.get(i));
       }

       resetLastSkillOperand();

       // Traverse the graph
       while (!edgeToVisit.isEmpty()) { 

           ProblemEdge problemEdge = edgeToVisit.poll();
           ProblemNode problemNode = problemEdge.getSource();

           if (isSubjectToTest(problemEdge) && !edgeVisited.contains(problemEdge)) {

               EdgeData edgeData = problemEdge.getEdgeData();
               boolean isCorrectEdge = edgeData.getActionType().equals(EdgeData.CORRECT_ACTION);

               String step = startNode.getName();
               if(getProblemAssessor() != null)
               	step = getProblemAssessor().findLastStep(startNode, problemNode);
               setProblemStep(step);
               // if (isProblemStepUpdated()) resetLastSkillOperand();

               String actualStatus = EdgeData.CLT_ERROR_ACTION;
               boolean isCorrectRa = false;
               RuleActivationNode tmpRa, actualRa = null;
               String flagPrediction, stepPrediction = null;

               Vector /* RuleActivationNode */<RuleActivationNode> activationList = gatherActivationList(problemNode);
               if(trace.getDebugCode("miss"))trace.out("miss", "RuleActivationList: " + activationList);
               if (!activationList.isEmpty()) {

                   RuleActivationNode ran = null;
                   for (int i = 0; i < activationList.size(); i++) {
                       ran = activationList.get(i);
                       if (!ran.getActualInput().equalsIgnoreCase("FALSE")) {
                           int result = logRuleActivation(problemName, ran, problemNode, problemEdge, output);
                           if (result > 0) {
                               // The rule activation is correct
                               isCorrectRa = true;
                               // Keep the first correct rule activation
                               if (actualRa == null) actualRa = ran;
                           }
                           if (stepPrediction == null) {
                               boolean isModelTraced = isStepModelTraced(edgeData, ran);
                               if (isModelTraced) {
                                   stepPrediction= isCorrectEdge ? TRUE_POSITIVE : TRUE_NEGATIVE;
                                   actualRa = ran;
                               }
                           }
                       }
                   }
                   // There should have been no correct rule activation found nor the 
                   // step isn't model traced...
                   if (actualRa == null) actualRa = ran;
               }
               actualStatus = isCorrectRa ? EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION;
               if (isCorrectEdge) {
                   flagPrediction = isCorrectRa ? TRUE_POSITIVE : FALSE_NEGATIVE;
                   if (stepPrediction == null) {
                       stepPrediction = isCorrectRa ? FALSE_POSITIVE : FALSE_NEGATIVE;
                   }
               } else {
                   flagPrediction = isCorrectRa ? FALSE_POSITIVE : TRUE_NEGATIVE;
                   if (stepPrediction == null) {
                       stepPrediction = isCorrectRa ? FALSE_POSITIVE : FALSE_NEGATIVE;
                   }
               }

               // Record if the step is model traced or not.
               logModelTraceStatus(problemName, problemNode, problemEdge, 
                       actualRa, actualStatus, flagPrediction, stepPrediction, output);

               edgeVisited.add(problemEdge);
               edges = problemEdge.getDest().getOutgoingEdges();
               for (int i = 0; i < edges.size(); i++) {
                   edgeToVisit.add((ProblemEdge)edges.get(i));
               }

               String modelInput = (String)edgeData.getInput().get(0);
               if (isCorrectEdge && EqFeaturePredicate.isValidSimpleSkill(modelInput.split(" ")[0])) {
                   setLastSkillOperand(modelInput);
               }
           }
       }
   }

   

   /* nbarba: new production rule validation (cognitive fidelity), as described on google drive
    * In short, this method 
    * 1. Takes everystate in the BRD. state has outgoing edges (human suggestions for that state - agendaBRD)
    * 2. Gives state to SimStudent. Simstudent has some possible suggestions (activationList) and Oracle grades these suggestions as correct / incorrect
    * 3. Compare these suggestions with outgoing edges of the steate (human student suggestions) and estimate T/F P/N.
    * 	 True Positive: suggestion in SimStudent agenda is Correct (by Oracle) and this suggestion is also in agendaBRD
    * 	 False Positive: suggestion in SimStudent agenda is Correct (by Oracle) and this suggestion is not in agendaBRD
    *    True Negative: suggestion in SimStudent agenda is Incorrect (by Oracle) and this suggestion is in the agendaBRD
    *    False Negative: suggestion in SimStudent agenda is Incorrect (by Oracle) and this suggestion is not in the agendaBRD
 	*    (First letter : if its in agendaBRD or not, Second letter: what oracle said about this suggestion)
    * 5. Proceed to the next state
    * This method examines all rule activations so number of output lines should be equal to number of rule activations. 
    *
    * */
   private void ssTestProductionModelOnBRD_testStepsCognitiveFidelity(String logMode, String output, String problemName) 
   {
      	String[] startState = readStartStateElements();
 

      	if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "********* COGNITIVE FIDELITY ***** ");
      	 
        problemName=problemName.replace(" ", "");
        problemName=problemName.replace("=", "_");
     
        
       SimStNode startNode = getValidationGraph().getStartNode();     
       Vector <SimStEdge> edgeVisited = new Vector<SimStEdge>();
       LinkedList <SimStEdge> edgeToVisit = new LinkedList<SimStEdge>();
      
       List edges = startNode.getOutgoingEdges();
       for (int i = 0; i < edges.size(); i++) {
           edgeToVisit.add((SimStEdge)edges.get(i));
       }

       
       
       resetLastSkillOperand();
       HashMap hm = new HashMap(); // Mapping between the RAN and the SimStNode
       
             String currentStepName="";
             
             
             boolean checkingSameNode=false;
             
             
       String cognitiveFidelityOutcome="";
       SimStNode previousNode=null;
       
       

       
       // Traverse the graph 
       while (!edgeToVisit.isEmpty()) { 

           SimStEdge problemEdge = edgeToVisit.poll();
           SimStNode problemNode = problemEdge.getSource();
           SimStEdgeData edgeData = problemEdge.getEdgeData();
          
           boolean noRuleFired=false;
           
           if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "####################################### ");
           
           if (isSubjectToTest(edgeData.getRuleNames().get(0)) && !edgeVisited.contains(problemEdge)) {
       
               boolean isCorrectEdge = edgeData.getActionType().equals(EdgeData.CORRECT_ACTION);
               noRuleFired=false;
               
               currentStepName=problemNode.getName();
               
            //   if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "At Step "+ problemNode.getName() );
               
               String step = startNode.getName();
               if(getProblemAssessor() != null)
               {
               	step = getProblemAssessor().findLastStep(startNode, problemNode);
               	setLastSkillOperand(getProblemAssessor().findLastOperand(startNode, problemNode));
               }
               
               String tmp=problemNode.getName();
               tmp=tmp.replace(" ","");
               tmp=tmp.replace("=","_");
               problemNode.setName(tmp);
             
             
               
               setProblemStep(step);
               
               step=step.replace(" ","");
               step=step.replace("=","_");
               

             //  if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", " Now we are at state   "+ step  + edgeToVisit);
                if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "At Step "+currentStepName+" BRD has " + problemNode.getOutgoingEdges().size() + " suggestions");
               
               
                boolean isInBRDAgenda=false;
  
                Vector <RuleActivationNode> activationList = gatherActivationList(problemNode, hm);
            	if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "	Simst agenda is "+activationList);

                SimStEdge brdEdgeThatMatches=null;  //variable to hold the outgoing brd edge to be logged in the "agenda". 
                SimStEdge runningBrdEdge=null;
                  
                RuleActivationNode actualRa=null;
               
               if (!problemNode.equals(previousNode)){
                	
                	
               if (!activationList.isEmpty()) {
               		RuleActivationNode ran = null;
               		    		
                   	for (int i = 0; i < activationList.size(); i++) {     	
                   		ran = activationList.get(i);     
                   	 if (!ran.getActualInput().equalsIgnoreCase("FALSE")) {
                   		boolean oracleValidation=false; //what the oracle things about this activation (sai)
                   		
                   		
                   		String status = inquiryRuleActivation(problemName, problemNode, ran);
                   		
                   		int result = EdgeData.CORRECT_ACTION.equals(status) ? 1 : -1;
                   			if (result>0){
                   					oracleValidation=true;	
                   				}
                       	
                   			
                       	if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		Simst suggested "+ ran.getActualAction() + " "+ ran.getActualInput() + " and ORACLE said " + oracleValidation);
                   			
                   				List currentStateBRDSuggestions= problemNode.getOutgoingEdges();
                   				
                   				if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		Number of Human BRD suggestions : "+ currentStateBRDSuggestions.size());
                           		                		
                   					for (int k=0;k<currentStateBRDSuggestions.size();k++){
                   						SimStEdge tmpEdge=(SimStEdge) currentStateBRDSuggestions.get(k);
                   						runningBrdEdge=tmpEdge;
                   						
                   						isInBRDAgenda=isStepModelTraced((SimStEdge) tmpEdge, ran);
                   						
                   						/*Option 1: modelBRD in log be the edge that was correctly modeltraced, or last examined
                   						if (isInBRDAgenda){                  							
                   							if (brdEdgeThatMatches==null)		//store the brd edge that was correctly modeltraced
                   								brdEdgeThatMatches=tmpEdge;                						
                   						}
                   						//tmpEdge.getActionType();
                   						*/
                   						
                   						/*Option2: modelBRD in log be the first correct edge examined */
                   						if (brdEdgeThatMatches==null)		{
                   							if (runningBrdEdge.getEdgeData().getActionType().equals(EdgeData.CORRECT_ACTION))
                   								brdEdgeThatMatches=tmpEdge;
               								
                   						}                   						
                   						
                   						if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "				comparing with BRD suggestion "+ tmpEdge.getAction() + " " + tmpEdge.getInput() + " ---> " + isInBRDAgenda);
                               		
                   						if (isInBRDAgenda)	/*if SimStudent suggestion found in BRD agenda there is no reason to keep looking*/
                   							break;
                   						
                   				}
                   				                				
                   				/*Cognitive Fidelity*/
               				    if (oracleValidation && isInBRDAgenda)	
               				    	cognitiveFidelityOutcome=TRUE_POSITIVE;
               	                else if (oracleValidation && !isInBRDAgenda)
               	                	cognitiveFidelityOutcome=FALSE_POSITIVE;
               	                else if (!oracleValidation && isInBRDAgenda)
               	                	cognitiveFidelityOutcome=TRUE_NEGATIVE;
               	                else if (!oracleValidation && !isInBRDAgenda)
               	                	cognitiveFidelityOutcome=FALSE_NEGATIVE;
               				    
               				    if (brdEdgeThatMatches==null)
               				    	brdEdgeThatMatches=runningBrdEdge;
               				    
               				    int res=logRuleActivationNew(problemName, ran, problemNode, brdEdgeThatMatches, output,cognitiveFidelityOutcome, oracleValidation ? "Correct Action" : "Untraceble Error");
                     	       
               				    if (result > 0) {             				    	
               				    	// Keep the first correct rule activation
               				    	if (actualRa == null) actualRa = ran;
               				    }
               		       
               				    
               				 
               				 
               				    if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", " 	Oracle said  "+ oracleValidation +" and isInBRDAgenda is " +isInBRDAgenda + " --> OUTCOME =" + cognitiveFidelityOutcome);
              	              
                   				
                   				
                   	} //end if actualInput false;
                   	
                   	else{
                   		if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		SimStudent has a suggestion but the input is FALSE... :(");
                   	}
                    
                }
                   	
                  if (actualRa == null) actualRa = ran;
                  if (brdEdgeThatMatches==null) brdEdgeThatMatches=runningBrdEdge;		
                 
                    
                    
               }
               	else{
               			
               		if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		All outgoing edges of this step have been examined.");
               	   
               		noRuleFired=true;
               	
               }
   
               previousNode=problemNode;
                   	 
               
               }
               else{
            	   checkingSameNode=true;
            	   if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		Opps, checking the same node. Moving on to the next one...");
               }
                  
                  if (brdEdgeThatMatches==null) brdEdgeThatMatches=problemEdge;		//this is executed only if no production rule was fired           
                  
              
               
                  
              if (noRuleFired)
                	  logRuleActivationNew(problemName, null, problemNode, brdEdgeThatMatches, output,"", "");
                  
                  //a.k.a. "test"
                  if (!checkingSameNode)
                	  logStepSummary(problemName, problemNode, brdEdgeThatMatches, "", actualRa, output," ");
                 
                  
                  
                  
                  checkingSameNode=false;
                  /*end of if(!activationList.isEmpty()) */
                  edgeVisited.add(problemEdge);
                  edges = problemEdge.getDest().getOutgoingEdges();
                  for (int i = 0; i < edges.size(); i++) {
                      edgeToVisit.add((SimStEdge)edges.get(i));
                  }

                  String modelInput = (String)edgeData.getInput().get(0);
                  if (isCorrectEdge && EqFeaturePredicate.isValidSimpleSkill(modelInput.split(" ")[0])) {
                      setLastSkillOperand(modelInput);
                  }
          }
          }
      }
   
   
   /* New production rule validation (model tracing), as described on google drive
    * In short, this method 
    * 1. For every state in the brd
    * 2. For each one of the outgoing edges of that state
    * 3. Take Simstudent suggestions for that state. (activationList). 
    * 4. Examine outgoing BRD edge, compared to SimStudent suggestions, and and estimate T/F C/E.
    * 	True Correct: ss production rules said correct (i.e. there was a rule that fired that model traced action) and this is correct (brd says this is correct action).
    * 	True Error: ss production rules said said error (i.e. no rule fired or no rule model traced action) and this is error (brd says this is error action).
    *   False Correct: ss production rules said correct (i.e. there was a rule that fired that model traced action) and this is wrong (brd says this is error action).
    *   False Error: ss production rules said said error (i.e. no rule fired or no rule model traced action) and this is error (brd says this is correct action).
    *   In short, 
    *     SIMSTUDENT SAYS ERROR --> has no rule activations OR no rule activation that model traces action
    *     SIMSTUDENT SAYS BUGGY --> Has a buggy rule that model traces action
    *     SIMSTUDANT SAYS CORRECT --> Has a correct rule that model traces action.
    *        
    *      first letter: what oracle says, second letter : if what oracle says is correct.
    * 5. Proceed to the next state
    * Note: this code operates on whatever name the JessOracle expects it to operate.
    * This method examines all states of BRD so number of output lines should be equal to number of BRD states. 
    * */
   private void ssTestProductionModelOnBRD_testStepsModelTracing(String logMode, String output, String problemName, String userID) 
   {      
	   
	   /*first of all, copy the production rules to whatever name the JessOracle expects its production rules to be*/
	 	   
       String[] startState = readStartStateElements();
      	
       SimStNode startNode = getValidationGraph().getStartNode();
       
	     //logRuleActivationComposeHeader(output);
       
       problemName=problemName.replace(" ", "");
       problemName=problemName.replace("=", "_");
       String problemNameShort="-1";
       
       
       Vector <SimStEdge> edgeVisited = new Vector<SimStEdge>();
       LinkedList <SimStEdge> edgeToVisit = new LinkedList<SimStEdge>();
       List edges = startNode.getOutgoingEdges();
       for (int i = 0; i < edges.size(); i++) {
           edgeToVisit.add((SimStEdge)edges.get(i));
       }

       resetLastSkillOperand();
       HashMap hm = new HashMap(); // Mapping between the RAN and the SimStNode
              
       String currentStepName="";
       String modelTracingValidationOutcome="Uninitialized";

       // Traverse the graph       
       while (!edgeToVisit.isEmpty()) { 

           SimStEdge problemEdge = edgeToVisit.poll();
           SimStNode problemNode = problemEdge.getSource();
           SimStEdgeData edgeData = problemEdge.getEdgeData();
          
           if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "####################################### " + output);          
           
           if (isSubjectToTest(edgeData.getRuleNames().get(0)) && !edgeVisited.contains(problemEdge)) {

               
               boolean isCorrectEdge = edgeData.getActionType().equals(EdgeData.CORRECT_ACTION);
               boolean isBuggyEdge = edgeData.getActionType().equals(EdgeData.BUGGY_ACTION);
               boolean isErrorEdge = edgeData.getActionType().equals(EdgeData.UNTRACEABLE_ERROR);
               
               
               String step = startNode.getName();
                      
               step=step.replace(" ","");
               step=step.replace("=","_");
               
               currentStepName=problemNode.getName();
               
               
               if(getProblemAssessor() != null){   
            	   step = getProblemAssessor().findLastStep(startNode, problemNode);                
            	   setLastSkillOperand(getProblemAssessor().findLastOperand(startNode, problemNode));
               }
                         
               String tmp=problemNode.getName();
               tmp=tmp.replace(" ","");
               tmp=tmp.replace("=","_");

               problemNode.setName(tmp);
             
             
               if (problemNameShort.equals("-1")){
            		   problemNameShort=currentStepName;
            		   problemNameShort=problemNameShort.replace(" ", "");
               }
              
           
               
               if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "At Step "+currentStepName+" BRD said " +  problemEdge.getSelection() + problemEdge.getAction() + " "+ problemEdge.getInput() + "with rule " +  (String)edgeData.getSkills().get(0));
     
               setProblemStep(step);
       
               RuleActivationNode actualRa = null; 		// variable to hold the rule activations that model traces the brd sai. 
        
            
               
               // Ask Jess Oracle if BRD selection action input is correct. Oracle outcome can either be Correct, Error or Buggy.
      	    	String oracleOutcome=inquiryJessOracle(problemEdge.getSelection(), problemEdge.getAction(), problemEdge.getInput(), problemNode, problemNameShort);
      	    	     	    		
      	    	//log the "agenda" line of logs
      	    	actualRa=getJessOracleSuggestion( problemNode,  hm,  problemNameShort ,  problemEdge,  output, oracleOutcome, problemName);
      	    	//AskHint hint=askForHint(this.getBrController(), problemNode);
      	    	
      	    	
      	    	
      	       if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "Jess Oracle said that BRD action is " + oracleOutcome);
          
               if (this.getModelTracingValidationOutcomeMethod().equals(MODELTRACING_VALIDATION_METHOD_STRICT)){
            	   	if (isCorrectEdge &&  oracleOutcome.equals(EdgeData.CORRECT_ACTION) )											
            	   		modelTracingValidationOutcome=TRUE_CORRECT;
            	   	else if (isBuggyEdge &&  oracleOutcome.equals(EdgeData.BUGGY_ACTION) )		   				   					
            	   		modelTracingValidationOutcome=TRUE_BUGGY;
            	   	else if (isErrorEdge && oracleOutcome.equals(EdgeData.UNTRACEABLE_ERROR))												
            	   		modelTracingValidationOutcome=TRUE_ERROR;
            	   	else if ((isBuggyEdge || isErrorEdge) && oracleOutcome.equals(EdgeData.CORRECT_ACTION))       
            	   		modelTracingValidationOutcome=FALSE_CORRECT;
            	   	else if ((isCorrectEdge || isErrorEdge) && oracleOutcome.equals(EdgeData.BUGGY_ACTION))		
            	   		modelTracingValidationOutcome=FALSE_BUGGY;							
            	   	else if ((isCorrectEdge || isBuggyEdge) && oracleOutcome.equals(EdgeData.UNTRACEABLE_ERROR))									   
            	   		modelTracingValidationOutcome=FALSE_ERROR;
               }
               else{         	   
            	   	  if (isCorrectEdge && oracleOutcome.equals(EdgeData.CORRECT_ACTION))										
                      	modelTracingValidationOutcome=TRUE_POSITIVE;																		
                      else if ((isBuggyEdge || isErrorEdge) && (oracleOutcome.equals(EdgeData.UNTRACEABLE_ERROR) || oracleOutcome.equals(EdgeData.BUGGY_ACTION)))	
                      	modelTracingValidationOutcome=TRUE_NEGATIVE;
                      else if (isCorrectEdge && (oracleOutcome.equals(EdgeData.UNTRACEABLE_ERROR) || oracleOutcome.equals(EdgeData.BUGGY_ACTION)))				
                      	modelTracingValidationOutcome=FALSE_NEGATIVE;
                      else if ((isBuggyEdge || isErrorEdge) && oracleOutcome.equals(EdgeData.CORRECT_ACTION))					
                      	modelTracingValidationOutcome=FALSE_POSITIVE;	  	   
               }
               
                      
               if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "Step: "+currentStepName+ " in BRD is "+ edgeData.getActionType() +" and Jess Oracle said  " + oracleOutcome + " --> OUTCOME =" + modelTracingValidationOutcome);
               
               String oracleS="";
               String oracleA="";
               String oracleI="";
               if (actualRa==null && oracleOutcome.equals(EdgeData.UNTRACEABLE_ERROR)){
            	   		oracleOutcome="UNKNOWN";
               }
               else if (actualRa!=null && oracleOutcome.equals(EdgeData.UNTRACEABLE_ERROR)){
            	   		oracleS=actualRa.getActualSelection();
            	   		oracleA=actualRa.getActualAction();
            	   		oracleI=actualRa.getActualInput();              	   
               }
               
               
               
               //log the "test" line of logs
              // logStepSummary(problemName, problemNode, problemEdge, modelTracingValidationOutcome, actualRa, output,oracleOutcome);
               logModelTracingValidationStepSummary(problemName, problemNode, problemEdge, modelTracingValidationOutcome,output,oracleOutcome,oracleS,oracleA,oracleI,userID);
               
               edgeVisited.add(problemEdge);
               edges = problemEdge.getDest().getOutgoingEdges();
               for (int i = 0; i < edges.size(); i++) {
                   edgeToVisit.add((SimStEdge)edges.get(i));
               }

               String modelInput = (String)edgeData.getInput().get(0);
               if (isCorrectEdge && EqFeaturePredicate.isValidSimpleSkill(modelInput.split(" ")[0])) {
                   setLastSkillOperand(modelInput);
               }
           }
       }
   }
   
   /*Returns the actual rule activation that model traced the action, or any rule activation if action was not model traced*/
   private RuleActivationNode getJessOracleSuggestion(SimStNode problemNode, HashMap hm, String problemName, SimStEdge problemEdge, String output, String oracleOutcome, String problemNameFull){
	   boolean isBrdActionModelTraced=false;
	   RuleActivationNode actualRa=null;
	   RuleActivationNode runningActualRa=null;
	   //Get jess oracle suggestions
       Vector <RuleActivationNode> activationList = gatherJessOracleAgenda(problemNode, problemName);	          
       
       
       if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "Activation List:  " +  activationList);
       
       if (!activationList.isEmpty()) {
	   
       		RuleActivationNode ran = null;           		
           	for (int i = 0; i < activationList.size(); i++) {     	
           		ran = activationList.get(i);    
           	    if (!ran.getActualInput().equalsIgnoreCase("FALSE")) {
           	    	runningActualRa=ran; //just keep it, if no ran model traces we will use it to log and see how far is simst suggestion from human suggestion.
           	    	               	    	
           	       // int result = logRuleActivationNew(problemNameFull, ran, problemNode, problemEdge, output,"", oracleOutcome);
           	        
           	    		if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "Simst suggested "+ ran.getActualAction() + " "+ ran.getActualInput() + " with rule " + ran.getName());
           	    		
           	    		if (isBrdActionModelTraced==false){  //once we find a ran that model traces the action, there is no need to further update "isInAgendaSS". Without this if, isInAgenda will be updated by every production rule which is wrong.                    	    		
               	    		
               	    		isBrdActionModelTraced=isStepModelTraced(problemEdge, ran);   
                   		
                   			if (isBrdActionModelTraced) {
                   				if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		!Simst was able to modeltrace Human Student action!");	
                   				if (actualRa == null) actualRa=ran;
                   						//break;   //for logging purposes we check all other solutions            		
                   			}
               	    	}
           	    
           	    } 
           	       		                   
           	}
            
       }
	   
       /*If no rule was able to model trace the action, log the last rule that was activated */
	      if (actualRa==null)
	      actualRa=runningActualRa;
        
       
       return actualRa;
   }
   
   /* ModelTracing Validation without using the Jess Oracle, but by asking simstudent directly
   private void ssTestProductionModelOnBRD_testStepsModelTracing(String logMode, String output, String problemName) 
   {      
	   
	   
	   
      	String[] startState = readStartStateElements();
      	//getSsRete().setStartStateElements(startState);
   	 
		
       SimStNode startNode = getValidationGraph().getStartNode();
       
       
       problemName=problemName.replace(" ", "");
       problemName=problemName.replace("=", "_");
       
       
       Vector <SimStEdge> edgeVisited = new Vector<SimStEdge>();
       LinkedList <SimStEdge> edgeToVisit = new LinkedList<SimStEdge>();
       List edges = startNode.getOutgoingEdges();
       for (int i = 0; i < edges.size(); i++) {
           edgeToVisit.add((SimStEdge)edges.get(i));
       }

       resetLastSkillOperand();
       HashMap hm = new HashMap(); // Mapping between the RAN and the SimStNode
              
       String currentStepName="";
       String modelTracingValidationOutcome="Uninitialized";

       // Traverse the graph       
       while (!edgeToVisit.isEmpty()) { 

           SimStEdge problemEdge = edgeToVisit.poll();
           SimStNode problemNode = problemEdge.getSource();
           SimStEdgeData edgeData = problemEdge.getEdgeData();
          
           if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "####################################### " + output);
           //if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "Examining edge:" + problemEdge);
            
           
           if (isSubjectToTest(edgeData.getRuleNames().get(0)) && !edgeVisited.contains(problemEdge)) {

               
               boolean isCorrectEdge = edgeData.getActionType().equals(EdgeData.CORRECT_ACTION);
               boolean isBuggyEdge = edgeData.getActionType().equals(EdgeData.BUGGY_ACTION);
               boolean isErrorEdge = edgeData.getActionType().equals(EdgeData.UNTRACEABLE_ERROR);
               
               
               String step = startNode.getName();
                      
               step=step.replace(" ","");
               step=step.replace("=","_");
               
               currentStepName=problemNode.getName();
               
               
               if(getProblemAssessor() != null){   
            	   step = getProblemAssessor().findLastStep(startNode, problemNode);                
            	   setLastSkillOperand(getProblemAssessor().findLastOperand(startNode, problemNode));
               }
                         
               String tmp=problemNode.getName();
               tmp=tmp.replace(" ","");
               tmp=tmp.replace("=","_");
               problemNode.setName(tmp);
             
             
               
               
               if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "At Step "+currentStepName+" BRD said " +  problemEdge.getSelection() + problemEdge.getAction() + " "+ problemEdge.getInput() + "with rule " +  (String)edgeData.getSkills().get(0));
               
               
               setProblemStep(step);
             
              
               boolean isBrdActionModelTraced=false;
               RuleActivationNode actualRa = null; 		// variable to hold the rule activations that model traces the brd sai. 
               RuleActivationNode runningActualRa=null;  // varible to hold the current rule activation. Usefull for logging when no rule activation modeltraces the brd sai ("test" log entry)
            
               boolean isModelTracedRuleBuggy=false;	//keeps track if the rule that replicates action is buggy.
               boolean noRulesFired=true;			//keeps track if there are any rules fired.
               
               
               
              
               Vector <RuleActivationNode> activationList = gatherActivationList(problemNode, hm);	          
    
               if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "Activation List:  " +  activationList);
               
               if (!activationList.isEmpty()) {
            	   noRulesFired=false;
            	   
               		RuleActivationNode ran = null;           		
                   	for (int i = 0; i < activationList.size(); i++) {     	
                   		ran = activationList.get(i);    
                   	    if (!ran.getActualInput().equalsIgnoreCase("FALSE")) {
                   	    	runningActualRa=ran; //just keep it, if no ran model traces we will use it to log and see how far is simst suggestion from human suggestion.
                   	    	               	    	
                   	        int result = logRuleActivationNew(problemName, ran, problemNode, problemEdge, output,"");
                   	        
                   	    	if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		Simst suggested "+ ran.getActualAction() + " "+ ran.getActualInput() + " with rule " + ran.getName());
                       			
                   	    	if (isBrdActionModelTraced==false){  //once we find a ran that model traces the action, there is no need to further update "isInAgendaSS". Without this if, isInAgenda will be updated by every production rule which is wrong.                    	    		
                   	    		
                   	    		isBrdActionModelTraced=isStepModelTraced(problemEdge, ran);   
                       		
                       			if (isBrdActionModelTraced) {
                       				if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		!Simst was able to modeltrace Human Student action!");	
                       				if (actualRa == null) actualRa=ran;
                       						//break;   //for logging purposes we check all other solutions            		
                       			}
                   	    	}
                   	    } 
                   	    else{
                   	    	if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		SimStudent has no PROPER (i.e. not FALSE)");
                   
                   	    }
                   		
                   		                   
                   	}
                    
               }
               else{
            	   
            	   	if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "		SimStudent has no suggestions for this step.");              	
  
               }
               
               
               
               
               
               //If no rule was able to model trace the action, log the last rule that was activated 
               if (actualRa==null)
            	   	actualRa=runningActualRa;
               
             
              
               
               
          
               
              //Estimate model tracing outcome (no buggy rules considered)            
              // if (isCorrectEdge && isInAgendaSS)	
              // 	modelTracingValidationOutcome=TRUE_CORRECT;
              // else if (!isCorrectEdge && !isInAgendaSS)
              // 	modelTracingValidationOutcome=TRUE_ERROR;
              // else if (isCorrectEdge && !isInAgendaSS)
              // 	modelTracingValidationOutcome=FALSE_ERROR;
              // else if (!isCorrectEdge && isInAgendaSS)
              // 	modelTracingValidationOutcome=FALSE_CORRECT;  
               
               
              // Estimate model tracing outcome (buggy rule considered)		OLD
              // 	  if (isCorrectEdge && isInAgendaSS)	
              //    	modelTracingValidationOutcome=TRUE_CORRECT;
              // 	  else if (isBuggyEdge && isInAgendaSS)
              // 		  modelTracingValidationOutcome=TRUE_BUGGY;
              //    else if (isErrorEdge && !isInAgendaSS)
              //    	modelTracingValidationOutcome=TRUE_ERROR;
              //    else if ((isBuggyEdge || isErrorEdge) && isInAgendaSS)
              //      	modelTracingValidationOutcome=FALSE_CORRECT;
              //   else if ((isCorrectEdge || isErrorEdge) && isInAgendaSS)
              //    	modelTracingValidationOutcome=FALSE_BUGGY;
              //    else if ((isCorrectEdge || isBuggyEdge) && !isInAgendaSS)
              //    	modelTracingValidationOutcome=FALSE_ERROR;
   
   
   
               //SIMSTUDENT SAYS ERROR --> has no rule activations OR no rule activation that model traces action
               //SIMSTUDENT SAYS BUGGY --> Has a buggy rule that model traces action
               //SIMSTUDANT SAYS CORRECT --> Has a correct rule that model traces action.
            
             
               if (  this.getModelTracingValidationOutcomeMethod().equals(MODELTRACING_VALIDATION_METHOD_STRICT)){
            	   	if (isCorrectEdge && isBrdActionModelTraced && !isModelTracedRuleBuggy)								//BRD says correct, simstudent has a correct rule in agenda that model traced brd action				
            	   		modelTracingValidationOutcome=TRUE_CORRECT;
            	   	else if (isBuggyEdge && isBrdActionModelTraced && isModelTracedRuleBuggy)		   				    //brd says buggy, simstudent has a buggy rule in agenda that model traced brd action					
            	   		modelTracingValidationOutcome=TRUE_BUGGY;
            	   	else if (isErrorEdge && (!isBrdActionModelTraced || noRulesFired))									//brd says error, simstudent either a) has no suggestions or b) did not modeltrace the action.						
            	   		modelTracingValidationOutcome=TRUE_ERROR;
            	   	else if (isCorrectEdge && (!isBrdActionModelTraced || isModelTracedRuleBuggy || noRulesFired))       //brd says correct, simstudent either a)has no activations, b) not modeltraced, b) modeltraced by buggy
            	   		modelTracingValidationOutcome=FALSE_CORRECT;
            	   	else if (isBuggyEdge && (!isBrdActionModelTraced || !isModelTracedRuleBuggy || noRulesFired))		//brd says buggy, simstudent a) fired correct rule or b) no rules fired at all, c) not modeltraced
            	   		modelTracingValidationOutcome=FALSE_BUGGY;							
            	   	else if (isErrorEdge && !noRulesFired && isBrdActionModelTraced)									     //brd says error, simstudent fired at least one rule (either buggy or correct, we don't care) that modeltrace the action
            	   		modelTracingValidationOutcome=FALSE_ERROR;
               }
               else{         	   
            	   	  if (isCorrectEdge && (isBrdActionModelTraced && !isModelTracedRuleBuggy))										//BRD correct, simstudent has a correct rule activation that modeltraces brd action
                      	modelTracingValidationOutcome=TRUE_CORRECT;																		
                      else if ((isBuggyEdge || isErrorEdge) && (!isBrdActionModelTraced || isModelTracedRuleBuggy || noRulesFired))	// BRD "not correct", simstudent a) no rule activation / no modeltrace, b) buggy rule activation that modeltraces brd action
                      	modelTracingValidationOutcome=TRUE_INCORRECT;
                      else if (isCorrectEdge && (!isBrdActionModelTraced || isModelTracedRuleBuggy || noRulesFired))				//BRD correct, simstudent a) no rule activation / no modeltrace, b) buggy rule activation that modeltraces brd action
                      	modelTracingValidationOutcome=FALSE_CORRECT;
                      else if ((isBuggyEdge || isErrorEdge) && (isBrdActionModelTraced && !isModelTracedRuleBuggy))					// BRD "not correct", simstudent has a correct rule activation that model traces brd action
                      	modelTracingValidationOutcome=FALSE_INCORRECT;	  	   
               }
               
                      
               if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd", "Step: "+currentStepName+ " in BRD is "+ edgeData.getActionType() +" and isBrdActionModeltraced is " +isBrdActionModelTraced + " --> OUTCOME =" + modelTracingValidationOutcome);
               
               //a.k.a. "test"
               logStepSummary(problemName, problemNode, problemEdge, modelTracingValidationOutcome, actualRa, output);
           	   	
               
               edgeVisited.add(problemEdge);
               edges = problemEdge.getDest().getOutgoingEdges();
               for (int i = 0; i < edges.size(); i++) {
                   edgeToVisit.add((SimStEdge)edges.get(i));
               }

               String modelInput = (String)edgeData.getInput().get(0);
               if (isCorrectEdge && EqFeaturePredicate.isValidSimpleSkill(modelInput.split(" ")[0])) {
                   setLastSkillOperand(modelInput);
               }
           }
       }
   }
   */
    
  
   
	// Returns 1 when a given rule activation node is correct, -1 otherwise
   private int logRuleActivationNew(String problemName, RuleActivationNode ruleActivated, ProblemNode problemNode, ProblemEdge problemEdge, String output,String cognitiveFidelityOutcome, String actualStatus) {
	   
       String nameRuleActivated = "N/A";
       String actualSelection = "null";
       String actualAction = "null";
       String actualInput = "null";

       // ruleActivated is null when there was no rule activated
       if (ruleActivated != null) {       	
           // Get the rule name, but strip off the "MAIN::" part 
           nameRuleActivated = ruleActivated.getName();
           nameRuleActivated = nameRuleActivated.replaceAll( "MAIN::", "" );

           // SAI
           actualSelection = ruleActivated.getActualSelection();
           actualAction = ruleActivated.getActualAction();
           actualInput = ruleActivated.getActualInput();
       }
       
       String modelSelection ="";
       String modelAction = "";
       String modelInput = "";
       String modelStatus = "";
       Vector edgeSkillNames=null;
       String modelSkillName="";
       if (problemEdge!=null){
    	   
    	   EdgeData edgeData = problemEdge.getEdgeData();
           modelSelection = (String)edgeData.getSelection().get(0);
           modelAction = (String)edgeData.getAction().get(0);
           modelInput = (String)edgeData.getInput().get(0);
           modelStatus = edgeData.getActionType();
           
           edgeSkillNames = edgeData.getSkills();
           modelSkillName = (String)edgeSkillNames.get(0);
    	   
       }
    
       if(modelSkillName.indexOf(' ') >= 0)
       	modelSkillName = modelSkillName.substring( 0, modelSkillName.indexOf(' ') );
       // trim the "MAIN::" part
       modelSkillName = modelSkillName.substring(modelSkillName.lastIndexOf(':') +1);
       
     //  String actualStatus = inquiryRuleActivation(problemName, problemNode, ruleActivated);
       
       // Keep the record
      // logRuleActivationToFile(LOG_PRTEST_AGENDA, problemName, problemNode.getName(), modelStatus, modelSkillName, nameRuleActivated, actualStatus, getSsCondition(), output, modelSelection, modelAction, modelInput,  actualSelection, actualAction, actualInput);          
       logRuleActivationToFileNew(LOG_PRTEST_AGENDA, problemName, problemNode.getName(), modelStatus, modelSkillName, nameRuleActivated, actualStatus, cognitiveFidelityOutcome , output, modelSelection , modelAction, modelInput, actualSelection, actualAction, actualInput);
       
       
       
       return EdgeData.CORRECT_ACTION.equals(actualStatus) ? 1 : -1;
   }
   
   
   /*
    * 
    *      
    * */   
   private void logStepSummary(String problemName, SimStNode problemNode, ProblemEdge problemEdge, String modelTracingValidationOutcome,RuleActivationNode actualRa, String output, String oracleOutcome){
	   
	   if (actualRa!=null){
		   if (actualRa.getActualInput().equals("FALSE"))
			   actualRa=null;
	   }
	   
	   
	   String modelStatus = problemEdge.getEdgeData().getActionType();
      
	   Vector skills = problemEdge.getSkills();
       
	   String modelSkillName = "";
       EdgeData edgeData = problemEdge.getEdgeData();
       Vector edgeSkillNames = edgeData.getSkills();
       modelSkillName = (String)edgeSkillNames.get(0);
       if(modelSkillName.indexOf(' ') >= 0)
       	modelSkillName = modelSkillName.substring( 0, modelSkillName.indexOf(' ') );
       // trim the "MAIN::" part
       modelSkillName = modelSkillName.substring(modelSkillName.lastIndexOf(':') +1);
            
      
        String modelSelection = problemEdge.getSelection();
        String modelAction = problemEdge.getAction();
        String modelInput = problemEdge.getInput();
        
        
        String actualSkillName = "N/A";
		String actualSelection = "null";
		String actualAction = "null";
		String actualInput = "null";
		String actualStatus="";


		actualStatus=oracleOutcome;
		if (actualRa!=null){
				//actualStatus = inquiryRuleActivation(problemName, problemNode, actualRa);
				actualSelection=actualRa.getActualSelection();
		        actualAction=actualRa.getActualAction();
		        actualInput=actualRa.getActualInput();	
		        actualSkillName = actualRa.getName();
				actualSkillName = actualSkillName.replaceAll( "MAIN::", "" );
		}
       
       
        
	   logRuleActivationToFileNew(LOG_PRTEST_TEST, problemName, problemNode.getName(), modelStatus, modelSkillName, actualSkillName, actualStatus, modelTracingValidationOutcome , output, modelSelection , modelAction, modelInput, actualSelection, actualAction, actualInput);
       
	   
	   
   }
   
   
 private void logModelTracingValidationStepSummary(String problemName, SimStNode problemNode, ProblemEdge problemEdge, String modelTracingValidationOutcome, String output, String oracleOutcome, String oracleS, String oracleA, String oracleI, String userID){
	   
	   String modelStatus = problemEdge.getEdgeData().getActionType();
      
	   Vector skills = problemEdge.getSkills();
       
	   String modelSkillName = "";
       EdgeData edgeData = problemEdge.getEdgeData();
       Vector edgeSkillNames = edgeData.getSkills();
       modelSkillName = (String)edgeSkillNames.get(0);
       if(modelSkillName.indexOf(' ') >= 0)
       	modelSkillName = modelSkillName.substring( 0, modelSkillName.indexOf(' ') );
       // trim the "MAIN::" part
       modelSkillName = modelSkillName.substring(modelSkillName.lastIndexOf(':') +1);
            
      
        String modelSelection = problemEdge.getSelection();
        String modelAction = problemEdge.getAction();
        String modelInput = problemEdge.getInput();
		String actualStatus=oracleOutcome;
	
        
	 //  logRuleActivationToFileNew(LOG_PRTEST_TEST, problemName, problemNode.getName(), modelStatus, modelSkillName, actualSkillName, actualStatus, modelTracingValidationOutcome , output, modelSelection , modelAction, modelInput, actualSelection, actualAction, actualInput);
       
		
		logRuleActivationToFile_ForModelTracingStepSummary(LOG_PRTEST_TEST, problemName, problemNode.getName(), modelStatus, modelSkillName, actualStatus, modelTracingValidationOutcome , output, modelSelection , modelAction, modelInput, oracleS, oracleA, oracleI, userID);
	       
   }
   
   /* nbabra 06/17/2014: This is the old code for production rule validation using a brd. This is something in between 
    * model tracing validation and cognitive fidelity. What it does is it takes the brd, and for every edge it logs to a file
    * if there is a correct activation (correctness is assigned by the oracle).
    * 
    * */
   private void ssTestProductionModelOnBRD_testSteps(String logMode, String output, String problemName) 
   {
	   
	   
	   trace.err("********* TEST STEPS ***** ");
      	String[] startState = readStartStateElements();
   	//getSsRete().setStartStateElements(startState);
   	    	
       SimStNode startNode = getValidationGraph().getStartNode();

       Vector <SimStEdge> edgeVisited = new Vector<SimStEdge>();
       LinkedList <SimStEdge> edgeToVisit = new LinkedList<SimStEdge>();
       List edges = startNode.getOutgoingEdges();
       for (int i = 0; i < edges.size(); i++) {
           edgeToVisit.add((SimStEdge)edges.get(i));
       }

       resetLastSkillOperand();
       HashMap hm = new HashMap(); // Mapping between the RAN and the SimStNode

       // Traverse the graph
       while (!edgeToVisit.isEmpty()) { 

           SimStEdge problemEdge = edgeToVisit.poll();
           SimStNode problemNode = problemEdge.getSource();
           SimStEdgeData edgeData = problemEdge.getEdgeData();

           if (isSubjectToTest(edgeData.getRuleNames().get(0)) && !edgeVisited.contains(problemEdge)) {


               boolean isCorrectEdge = edgeData.getActionType().equals(EdgeData.CORRECT_ACTION);

               String step = startNode.getName();
               if(getProblemAssessor() != null)
               {
               	step = getProblemAssessor().findLastStep(startNode, problemNode);
               	setLastSkillOperand(getProblemAssessor().findLastOperand(startNode, problemNode));
               }
               setProblemStep(step);

               String actualStatus = EdgeData.CLT_ERROR_ACTION;
               boolean isCorrectRa = false;
               RuleActivationNode tmpRa, actualRa = null;
               String flagPrediction, stepPrediction = null;

               Vector <RuleActivationNode> activationList = gatherActivationList(problemNode, hm);

               if(trace.getDebugCode("miss"))trace.out("miss", "RuleActivationList: " + activationList);
               if (!activationList.isEmpty()) {

                   RuleActivationNode ran = null;
                   for (int i = 0; i < activationList.size(); i++) {
                       ran = activationList.get(i);
                       if (!ran.getActualInput().equalsIgnoreCase("FALSE")) {
                       	String status = inquiryRuleActivation(problemName, problemNode, ran);
                       	int result = EdgeData.CORRECT_ACTION.equals(status) ? 1 : -1;
                        //   int result = logRuleActivation(problemName, ran, problemNode, problemEdge, output);
                       	if (result > 0) {
                               // The rule activation is correct
                               isCorrectRa = true;
                               // Keep the first correct rule activation
                               if (actualRa == null) actualRa = ran;
                           }
                           if (stepPrediction == null) {
                               boolean isModelTraced = isStepModelTraced(problemEdge, ran);
                               if (isModelTraced) {
                                   stepPrediction= isCorrectEdge ? TRUE_POSITIVE : TRUE_NEGATIVE;
                                   actualRa = ran;
                               }
                           }
                       }
                   }
                   // There should have been no correct rule activation found nor the 
                   // step isn't model traced...
                   if (actualRa == null) actualRa = ran;
               }
               actualStatus = isCorrectRa ? EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION;
               if (isCorrectEdge) {
                   flagPrediction = isCorrectRa ? TRUE_POSITIVE : FALSE_NEGATIVE;
                   if (stepPrediction == null) {
                       stepPrediction = isCorrectRa ? FALSE_POSITIVE : FALSE_NEGATIVE;
                   }
               } else {
                   flagPrediction = isCorrectRa ? FALSE_POSITIVE : TRUE_NEGATIVE;
                   if (stepPrediction == null) {
                       stepPrediction = isCorrectRa ? FALSE_POSITIVE : FALSE_NEGATIVE;
                   }
               }

               // Record if the step is model traced or not.
               logModelTraceStatus(problemName, problemNode, problemEdge, 
                       actualRa, actualStatus, flagPrediction, stepPrediction, output);

               edgeVisited.add(problemEdge);
               edges = problemEdge.getDest().getOutgoingEdges();
               for (int i = 0; i < edges.size(); i++) {
                   edgeToVisit.add((SimStEdge)edges.get(i));
               }

               String modelInput = (String)edgeData.getInput().get(0);
               if (isCorrectEdge && EqFeaturePredicate.isValidSimpleSkill(modelInput.split(" ")[0])) {
                   setLastSkillOperand(modelInput);
               }
           }
       }
   }

   
   public boolean headerPrinted=false;
   
   // 
   // 
   private void logRuleActivationToFileNew(String phase, String problemName, String stateName, 
   		String modelStatus, String modelSkillName, 
   		String actualSkillName, String actualStatus,
   		String prediction, String output, 
   		String modelSelection, String modelAction, String modelInput, 
   		String actualSelection, String actualAction, String actualInput) {
   	
   	String format = "MM.dd.yyyy-kk.mm.ss";
   	SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
       String dateTime = dateFormat.format( new Date() );

       int andIndex = actualSkillName.indexOf('&');
       if (andIndex > 0) {
       	actualSkillName = actualSkillName.substring(0, andIndex);
       }

       try {
           File file = new File( output );
           if (!file.exists()) {
           	logRuleActivationComposeHeader(file);
           }
         
           // Open a log file as an append mode
           FileOutputStream fOut = new FileOutputStream( file, true );
           PrintWriter log = new PrintWriter( fOut );

           
          	   if (headerPrinted==false){
          			log.println(this.logHeader);
          			headerPrinted=true;
          	   }
          		   
          	   
             
           
           
           // String skillName = (String)skillNames.get(0);
           int numProblem = getNumTrained();
           int freq = getRuleFreq( modelSkillName );
           int numRules = numRules();
           String numSteps = "" + numAllInstructions();

           //should split into two parts, with last '.'
           String name = new File(problemName).getName();
           String problemID = name.lastIndexOf('.') > 0 ? name.substring(0,name.lastIndexOf('.')) : name;

           // problemStep is updated by inquiryRuleActivation Oracle
           String step = getProblemStep();
           if (modelSkillName.indexOf("typein") > 0 && getLastSkillOperand() != null) {
           	step += " (" + getLastSkillOperand() + ")";
           }

           String studentID=this.getUserID();
           String cond=getSsCondition();
     
           
           String logStr = trainingCycle+ "\t" + studentID + "\t" + cond + "\t"+ dateTime + "\t" +
           problemID + "\t" +
           "\"" + stateName + "\"\t" +
           phase + "\t" +
           "\"" + step + "\"\t" + 
           actualSkillName + "\t" + 
           actualSelection + "\t" + 
           actualAction + "\t" + 
           "\"" + actualInput + "\"\t" + 
           actualStatus + "\t" +
          modelSkillName + "\t" + 
          modelSelection + "\t" + 
           modelAction + "\t" + 
          "\"" + modelInput + "\"\t" +
           modelStatus + "\t" + 
           "\"" + prediction  + "\"";

           

        /*   String logStr = dateTime + "\t" +
           problemID + "\t" +
           "\"" + stateName + "\"\t" +
           phase + "\t" +
           "\"" + step + "\"\t" + 
           modelSelection + "\t" + 
           modelAction + "\t" + 
           "\"" + modelInput + "\"\t" + 
         //  modelSkillName + "\t" + 
           modelStatus + "\t" +
         //  actualSkillName + "\t" + 
          actualSelection + "\t" + 
           actualAction + "\t" + 
          "\"" + actualInput + "\"\t" +
           actualStatus + "\t" + 
           "\"" + prediction  + "\"";
          */ 
           
           
           log.println( logStr );
           log.close();
           fOut.close();
           
             
           

       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   
   private String logHeader = 
		   	"TrainingCycle\t"+"StudentID\t"+"Condition\t"+"Date\t" +
		   	//"Condition\t" +
		   	"TestName\t" +
		   	"StateName\t" +
		   	//"NumTraining\t" + 
		   	//"Freq\t" +
		   	//"NumRule\t" +
		   	//"NumSteps\t" +
		   	"Phase\t" +
		   	"Step\t" + 
			"ActualRule\t" +
			"ActualSelection\t" +
		   	"ActualAction\t" +
		   	"ActualInput\t" +
		   	"ActualOutcome\t" +
		   	"BRDRule\t" +
		    "BRDSelection\t" +
		   	"BRDlAction\t" +
		   	"BRDInput\t" +
		   	"BRDOutcome\t" +
		   	//"FlagPrediction\t" +
		   	//"StepPrediction\t" +
		   	"Outcome";

   
   
   
   private void logRuleActivationToFile_ForModelTracingStepSummary(String phase, String problemName, String stateName, 
	   		String modelStatus, String modelSkillName, String actualStatus,
	   		String prediction, String output, 
	   		String modelSelection, String modelAction, String modelInput, String oracleS, String oracleA, String oracleI, String userID) {
	   	
	   	String format = "MM.dd.yyyy-kk.mm.ss";
	   	SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
	       String dateTime = dateFormat.format( new Date() );

	    

	       try {
	           File file = new File( output );
	           if (!file.exists()) {
	           	logRuleActivationComposeHeader(file);
	           }
	         
	           // Open a log file as an append mode
	           FileOutputStream fOut = new FileOutputStream( file, true );
	           PrintWriter log = new PrintWriter( fOut );

	           
	          	   if (headerPrinted==false){
	          			log.println(this.logHeaderMTValidation);
	          			headerPrinted=true;
	          	   }
	          		   
	          	   
	             
	           
	           
	           // String skillName = (String)skillNames.get(0);
	           int numProblem = getNumTrained();
	           int freq = getRuleFreq( modelSkillName );
	           int numRules = numRules();
	           String numSteps = "" + numAllInstructions();

	           //should split into two parts, with last '.'
	           String name = new File(problemName).getName();
	           String problemID = name.lastIndexOf('.') > 0 ? name.substring(0,name.lastIndexOf('.')) : name;

	           // problemStep is updated by inquiryRuleActivation Oracle
	           String step = getProblemStep();
	           if (modelSkillName.indexOf("typein") > 0 && getLastSkillOperand() != null) {
	           	step += " (" + getLastSkillOperand() + ")";
	           }

	         

	           String logStr = dateTime + "\t" +
	           userID + "\t" +
	           problemID + "\t" +
	           "\"" + stateName + "\"\t" +
	       //    phase + "\t" +
	           "\"" + step + "\"\t" + 
	          // modelSkillName + "\t" +
	           modelSelection + "\t" + 
	           modelAction + "\t" + 
	           modelInput +  "\t" + 
	           modelStatus + "\t" +
	           actualStatus + "\t" +
	           prediction + "\t"+
	           oracleS + " \t"+
	           oracleA + " \t"+
	           oracleI + " \t";
	           
	           log.println( logStr );
	           log.close();
	           fOut.close();
	           
	             
	       } catch (Exception e) {
	           e.printStackTrace();
	           logger.simStLogException(e);
	       }
	   }
   
   
   
   private String logHeaderMTValidation = 
		   	"Date\t" +
		   	//"Condition\t" +
		   	"User ID\t" +
		   	"TestName\t" +
		   	"StateName\t" +
		   	//"NumTraining\t" + 
		   	//"Freq\t" +
		   	//"NumRule\t" +
		   	//"NumSteps\t" +
		  // 	"Phase\t" +
		   	"Step\t" + 
		   //	"BRDRule\t" +
		   	"BRDSelection\t" +
		   	"BRDAction\t" +
		   	"BRDInput\t" +
		   	"BRDOutcome\t" +
		   	"OracleOutcome\t" +
		   	//"FlagPrediction\t" +
		   	//"StepPrediction\t" +
		   	
		   	"ModelTracing Outcome\t" + 
		   	"OracleSelection\t" +
		   	"OracleAction\t" +
		   	"OracleInput\t" ;
   
   
	private String logHeaderNew = 
   	"Date\t" +
   	//"Condition\t" +
   	"TestName\t" +
   	"StateName\t" +
   	//"NumTraining\t" + 
   	//"Freq\t" +
   	//"NumRule\t" +
   	//"NumSteps\t" +
   	"Phase\t" +
   	"Step\t" + 
   	"ModelRule\t" +
   	//"ModelStatus\t" +
   	"ActualRule\t" +
   	"ActualStatus\t" +
   	//"FlagPrediction\t" +
   	//"StepPrediction\t" +
   	"ModelSel\t" +
   	"ModelAction\t" +
   	"ModelInput\t" +
   	"ActualSel\t" +
   	"ActualAction\t" +
   	"ActualInput";

	
	
	
	
	
	
   private boolean isSubjectToTest(SsProblemEdge edge) {
   	String skillName = edge.getSkillName();
		return isSubjectToTest(skillName);
	}

   // This is rather ad-hoc to Algebra I 
   private boolean isSubjectToTest(ProblemEdge problemEdge) {
       String skillName = (String)problemEdge.getSkills().get(0);
       return isSubjectToTest(skillName);
   }

   private boolean isSubjectToTest(String givenSkillName) {
       String skillName = null;
       int idx = givenSkillName != null ? givenSkillName.indexOf(' ') : -1;
       if (idx > 0) {
           skillName = givenSkillName.substring( 0, idx );
       }
       if(trace.getDebugCode("miss"))trace.out("miss", "isSubjectToTest: skillName = " + skillName + "[" + (skillName == null || !skillName.endsWith("hint")) + "]");
       return ( skillName == null || !skillName.endsWith("hint"));
   }




   private void logModelTraceStatus(String problemName, ProblemNode problemNode, 
                                    ProblemEdge problemEdge, RuleActivationNode actualRa,
                                    String actualStatus, String flagPrediction, String stepPrediction,
                                    String output) 
   {

       String modelStatus = problemEdge.getEdgeData().getActionType();
       Vector skills = problemEdge.getSkills();
       String modelSkillName = "";
       if(skills.size() > 0)
       	modelSkillName = (String)skills.get(0);
       else if(isSkillNameGetterDefined())
       	modelSkillName = getSkillNameGetter().skillNameGetter(getBrController(), problemEdge.getSelection(),
       		problemEdge.getAction(), problemEdge.getInput());
       if(modelSkillName.indexOf(' ') >= 0)
       	modelSkillName = modelSkillName.substring( 0, modelSkillName.indexOf(' ') );
       // trim the "MAIN::" part
       modelSkillName = modelSkillName.substring(modelSkillName.lastIndexOf(':') +1);

       String modelSelection = problemEdge.getSelection();
       String modelAction = problemEdge.getAction();
       String modelInput = problemEdge.getInput();
       String actualSkillName = "N/A";
       String actualSelection = "";
       String actualAction = "";
       String actualInput = "";

       if (actualRa != null) {
           // Get the rule name, but strip off the "MAIN::" part 
           actualSkillName = actualRa.getName();
           actualSkillName = actualSkillName.replaceAll( "MAIN::", "" );
           actualSelection = actualRa.getActualSelection();
           actualAction = actualRa.getActualAction();
           actualInput = actualRa.getActualInput();
       }

       logRuleActivationToFile(LOG_PRTEST_TEST, problemName, problemNode.getName(), 
       		modelStatus, modelSkillName, 
       		actualSkillName, actualStatus, 
       		flagPrediction, stepPrediction,
       		getSsCondition(), output,
               modelSelection, modelAction, modelInput, 
               actualSelection, actualAction, actualInput);
   }

   private void logModelTraceStatus(String problemName, ProblemNode problemNode, 
           RuleActivationNode actualRa,
           String actualStatus, String flagPrediction, String stepPrediction,
           String output, AskHint correctAction) 
	{
			String modelStatus = EdgeData.CORRECT_ACTION;
			
			String modelSkillName = correctAction.getSkillName();
			
			// trim the "MAIN::" part
			modelSkillName = modelSkillName.substring(modelSkillName.lastIndexOf(':') +1);
			
			String modelSelection = correctAction.getSelection();
			String modelAction = correctAction.getAction();
			String modelInput = correctAction.getInput();
			String actualSkillName = "N/A";
			String actualSelection = "";
			String actualAction = "";
			String actualInput = "";
			
			if (actualRa != null) {
			// Get the rule name, but strip off the "MAIN::" part 
				actualSkillName = actualRa.getName();
				actualSkillName = actualSkillName.replaceAll( "MAIN::", "" );
				actualSelection = actualRa.getActualSelection();
				actualAction = actualRa.getActualAction();
				actualInput = actualRa.getActualInput();
			}
			
			logRuleActivationToFile(LOG_PRTEST_TEST, problemName, problemNode.getName(), 
			modelStatus, modelSkillName, 
			actualSkillName, actualStatus, 
			flagPrediction, stepPrediction,
			getSsCondition(), output,
			modelSelection, modelAction, modelInput, 
			actualSelection, actualAction, actualInput);
}

// Returns 1 when a given rule activation node is correct, -1 otherwise
   private int logRuleActivationOracled(String phase, String problemName, RuleActivationNode ruleActivated, ProblemNode sourceNode, 
                                      String output, AskHint hint) {

       String nameRuleActivated = NOT_AVAILABLE;
       String actualSelection = NOT_AVAILABLE;
       String actualAction = NOT_AVAILABLE;
       String actualInput = NOT_AVAILABLE;
       String actualStatus = NOT_AVAILABLE;

       // ruleActivated is null when there was no rule activated
       if (ruleActivated != null) {
       	
           // Get the rule name, but strip off the "MAIN::" part 
           nameRuleActivated = ruleActivated.getName();
           nameRuleActivated = nameRuleActivated.replaceAll( "MAIN::", "" );

           // SAI
           actualSelection = ruleActivated.getActualSelection();
           actualAction = ruleActivated.getActualAction();
           actualInput = ruleActivated.getActualInput();
           
       }
       

       //AskHint hint = new AskHintInBuiltClAlgebraTutor(getBrController(), sourceNode);
       String modelSelection = hint.getSelection();
       String modelAction = hint.getAction();
       String modelInput = hint.getInput();
       String modelStatus = EdgeData.CORRECT_ACTION;
       String modelSkillName = hint.getSkillName();
       // trim the "MAIN::" part
       modelSkillName = modelSkillName.substring(modelSkillName.lastIndexOf(':') +1);

       if(ruleActivated != null) {
       	actualStatus = inquiryRuleActivation(problemName, sourceNode, ruleActivated);
       }

       // Keep the record
       if(phase.equals(LOG_PRTEST_AGENDA)) {
       	logRuleActivationToFile(LOG_PRTEST_AGENDA, problemName, sourceNode.getName(), 
       			modelStatus, modelSkillName, 
       			nameRuleActivated, actualStatus, getSsCondition(), output,
       			modelSelection, modelAction, modelInput, 
       			actualSelection, actualAction, actualInput);
       } else {
       	logRuleActivationToFile(LOG_PRTEST_TEST, problemName, sourceNode.getName(),
       			modelStatus, modelSkillName,
       			nameRuleActivated, actualStatus, getSsCondition(), output,
       			modelSelection, modelAction, modelInput,
       			actualSelection, actualAction, actualInput);
       }

       return EdgeData.CORRECT_ACTION.equals(actualStatus) ? 1 : -1;
   }


	// Returns 1 when a given rule activation node is correct, -1 otherwise
   private int logRuleActivation(String problemName, RuleActivationNode ruleActivated, ProblemNode sourceNode, 
                                     ProblemEdge problemEdge, String output) {

       String nameRuleActivated = "N/A";
       String actualSelection = "";
       String actualAction = "";
       String actualInput = "";

       // ruleActivated is null when there was no rule activated
       if (ruleActivated != null) {
       	
           // Get the rule name, but strip off the "MAIN::" part 
           nameRuleActivated = ruleActivated.getName();
           nameRuleActivated = nameRuleActivated.replaceAll( "MAIN::", "" );

           // SAI
           actualSelection = ruleActivated.getActualSelection();
           actualAction = ruleActivated.getActualAction();
           actualInput = ruleActivated.getActualInput();
       }

       EdgeData edgeData = problemEdge.getEdgeData();
       String modelSelection = (String)edgeData.getSelection().get(0);
       String modelAction = (String)edgeData.getAction().get(0);
       String modelInput = (String)edgeData.getInput().get(0);
       String modelStatus = edgeData.getActionType();

       Vector edgeSkillNames = edgeData.getSkills();
       String modelSkillName = (String)edgeSkillNames.get(0);
       if(modelSkillName.indexOf(' ') >= 0)
       	modelSkillName = modelSkillName.substring( 0, modelSkillName.indexOf(' ') );
       // trim the "MAIN::" part
       modelSkillName = modelSkillName.substring(modelSkillName.lastIndexOf(':') +1);

       String actualStatus = inquiryRuleActivation(problemName, sourceNode, ruleActivated);

       // Keep the record
       logRuleActivationToFile(LOG_PRTEST_AGENDA, problemName, sourceNode.getName(), 
       		modelStatus, modelSkillName, 
       		nameRuleActivated, actualStatus, getSsCondition(), output,
               modelSelection, modelAction, modelInput, 
               actualSelection, actualAction, actualInput);

       return EdgeData.CORRECT_ACTION.equals(actualStatus) ? 1 : -1;
   }

   public boolean isStepModelTraced(String edgeSkillName, String modelSelection, String modelAction, String modelInput, 
           String nameRuleActivated, String actualSelection, String actualAction, String actualInput) {

       return (
               skillNameMatched(edgeSkillName, nameRuleActivated) &&
               isStepModelTraced(modelSelection, modelAction, modelInput, actualSelection, actualAction, actualInput)
               );
   }

	private boolean isStepModelTraced(EdgeData edgeData, RuleActivationNode ran) {
		String modelSelection = (String)edgeData.getSelection().get(0);
		String modelAction = (String)edgeData.getAction().get(0);
		String modelInput = (String)edgeData.getInput().get(0);
		String actualSelection = ran.getActualSelection();
		String actualAction = ran.getActualAction();
		String actualInput = ran.getActualInput();
		boolean isStepModelTraced = 
			isStepModelTraced(modelSelection, modelAction, modelInput, actualSelection, actualAction, actualInput);
		return isStepModelTraced;
	}
	
	private boolean isStepModelTraced(SimStEdge edge, RuleActivationNode ran) {
		String modelSelection = edge.getSelection();
		String modelAction = edge.getAction();
		String modelInput = edge.getInput();
		String actualSelection = ran.getActualSelection();
		String actualAction = ran.getActualAction();
		String actualInput = ran.getActualInput();
		
		
		
		boolean isStepModelTraced = 
			isStepModelTraced(modelSelection, modelAction, modelInput, actualSelection, actualAction, actualInput);
		return isStepModelTraced;
	}

   public boolean isStepModelTraced(String modelSelection, String modelAction, String modelInput, 
                                    String actualSelection, String actualAction, String actualInput) 
   {
       return (
               modelSelection.equalsIgnoreCase(actualSelection) &&
               modelAction.equalsIgnoreCase(actualAction) &&
               compairInput(modelInput, actualInput)
               );
   }

   public boolean compairInput(String modelInput, String actualInput) {

       boolean isValid = false;

       if (getInputMatcher() == null) {

           isValid = modelInput.equals(actualInput);

       } else {

           String cachedResult = cachedCompairInput(modelInput, actualInput);
           if (cachedResult != null) {

               isValid = cachedResult.equals("T");

           } else {
               try {
                   FeaturePredicate matcherInstance = getInputMatcherInstance();
                   String result = matcherInstance.inputMatcher(modelInput, actualInput);
                   putCachedCompairInput(modelInput, actualInput, result);
                   isValid = (result != null);
               } catch (Exception e) {
                   e.printStackTrace();
                   logger.simStLogException(e);
               }
           }
       }
       
       return isValid;
   }

   private static edu.cmu.pact.miss.HashMap compairInputCache = new edu.cmu.pact.miss.HashMap();
   // private static HashMap compairInputCache = new HashMap();

   private String cachedCompairInput(String modelInput, String actualInput) {

       String cachedCompairInput = null;

       edu.cmu.pact.miss.HashMap inputCache = 
           (edu.cmu.pact.miss.HashMap)compairInputCache.get(modelInput);
       // HashMap inputCache = (HashMap)compairInputCache.get(lastVar);

       if (inputCache != null) {
           cachedCompairInput = (String)inputCache.get(actualInput);
       }

       return cachedCompairInput;
   }

   private void putCachedCompairInput(String modelInput, String actualInput, String result) {

       edu.cmu.pact.miss.HashMap inputCache = 
           (edu.cmu.pact.miss.HashMap)compairInputCache.get(modelInput);
       // HashMap inputCache = (HashMap)compairInputCache.get(lastVar);

       if (inputCache == null) {
           inputCache = new edu.cmu.pact.miss.HashMap();
           // inputCache = new HashMap();
           compairInputCache.put(modelInput, inputCache);
       }
       inputCache.put(actualInput, result == null ? "F" : "T");
   }

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   // Validate BRDs
   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	private boolean validateStepsInBRD = false;
   public boolean isValidateStepsInBRD() {	return validateStepsInBRD; }
	private void setValidateStepsInBRD(boolean b) { validateStepsInBRD = b; }

   // Open up a give BRD and validate steps that are claimed to be "Correct"
   public void validateStepsInBRD(String brdPath, String output) {

   	setValidateStepsInBRD(true);
   	// Current oracle schema needs to know FoA for a rule activation, which
   	// is not available for BRD validation. Besides, BRD validation validate steps 
   	// only once, hence oracle cache does not work well.
   	// setSsCacheOracleInquiry(false);
   	
   	setRuleActivationTestMethod(RA_TEST_METHOD_TUTOR_SOLVER);
       // setRuleActivationTestMethod(RA_TEST_METHOD_VOID);

       // Recursively call testProductionModelOn when the target problem file is a directory
       File brdFile = new File(brdPath); 

       if (brdFile.isDirectory()) {

           String brdFiles[] = brdFile.list();
           for (int i = 0; i < brdFiles.length; i++) {
               File childFile = new File(brdFile, brdFiles[i]);
               String childFileName = new File(brdFile, brdFiles[i]).getAbsolutePath();
               if (childFile.isDirectory() && !childFileName.equals("CVS"))
                   validateStepsInBRD(childFileName, output);
               if (childFileName.matches(".*brd$"))
                   validateStepsInBRD_aux(childFileName, output);
           }

       } else {

           validateStepsInBRD_aux(brdPath, output);
       }
   }

   private void validateStepsInBRD_aux(String brdPath, String output) {

       if (isCheckWilkinsburgBadBrdFile())
           if (isBadWilkinsburgBrdFile(brdPath) != null) return;

       String brdID = new File(brdPath).getName();
       String studentID = new File(brdPath).getParent();

       File file = new File( output );
       FileOutputStream fOut = null;
       try {
           fOut = new FileOutputStream( file, true );
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
       PrintWriter logPrintWriter = new PrintWriter( fOut );

       testProductionModelOnBRD_init();
       testProductionModelOnBRD_loadBRD(brdPath, "");

       BR_Controller brController = getBrController();
       ProblemNode startNode = brController.getProblemModel().getStartNode();

       List edges = startNode.getOutgoingEdges();
       LinkedList /* ProblemEdge */<ProblemEdge> edgeToVisit = new LinkedList<ProblemEdge>();
       for (int i = 0; i < edges.size(); i++) {
           ProblemEdge edge = (ProblemEdge)edges.get(i);
           edgeToVisit.add(edge);
           if(trace.getDebugCode("miss"))trace.out("miss", "validateStepsInBRD: startNode " + startNode + " has " + edge);
       }

       Vector /* ProblemEdge */<ProblemEdge> edgeVisited = new Vector<ProblemEdge>();

       // Traverse the graph
       while (!edgeToVisit.isEmpty()) { 

           ProblemEdge edge = edgeToVisit.poll();
           ProblemNode node = edge.getSource();

           String problemStep = startNode.getName();
           if(getProblemAssessor() != null)
           	problemStep = getProblemAssessor().findLastStep(startNode, node);
           setProblemStep(problemStep);

           String skillName = (String)edge.getSkills().get(0);
           skillName = skillName.substring( 0, skillName.indexOf(' ') );
           // trim the "MAIN::" part
           skillName = skillName.substring(skillName.lastIndexOf(':') +1);

           // if (!edgeVisited.contains(edge) && edge.isCorrect() && isSubjectToTest(edge)) {
           if (!edgeVisited.contains(edge) && isSubjectToTest(edge)) {

               String selection = edge.getSelection();
               String action = edge.getAction();
               String input= edge.getInput();
               String brdActionType = edge.getActionType();

               if (edge.isCorrect() && EqFeaturePredicate.isValidSimpleSkill(input.split(" ")[0])) {
                   resetLastSkillOperand();
               }

               // The correctness of the rule activation 
               String raActionType = inquiryRuleActivation(brdID, node, selection, action, input);

               String step = getProblemStep();
               if (getLastSkillOperand() != null) {
                   step += "(" + getLastSkillOperand() + ")";
               }

               if (raActionType == null) {
                   logBrdValidation(logPrintWriter, brdID, studentID, node.getName(), step, "Broken-BRD", 
                           null, skillName, selection, input, action);
                   break;
               } 

               boolean isRaCorrect = raActionType.equals(EdgeData.CORRECT_ACTION);
               boolean isBrdCorrect = brdActionType.equals(EdgeData.CORRECT_ACTION);
               String correctness = (isRaCorrect == isBrdCorrect ? "OK" : "NG");

               logBrdValidation(logPrintWriter, brdID, studentID, node.getName(), step, brdActionType,
                       correctness, skillName, selection, input, action);

               // Ad-hoc short-cut to speed-up the validation
               if (raActionType.equals(FALSE_POSITIVE))
                   break;

               if (edge.isCorrect() && EqFeaturePredicate.isValidSimpleSkill(input.split(" ")[0])) {
                   setLastSkillOperand(input);
               }
           }

           edgeVisited.add(edge);
           ProblemNode destNode = edge.getDest();
           edges = destNode.getOutgoingEdges();
           for (int i = 0; i < edges.size(); i++) {
               ProblemEdge childEdge = (ProblemEdge)edges.get(i);
               if(trace.getDebugCode("miss"))trace.out("miss", "validateStepsInBRD: node " + destNode + " has " + childEdge);
               edgeToVisit.add(childEdge);
           }
           if(trace.getDebugCode("miss"))trace.out("miss", " ... there are " + edgeToVisit.size() + " edges to go");
       }

       try {
           logPrintWriter.close();
           fOut.close();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   private void logBrdValidation(PrintWriter logPrintWriter, String brdID,
   		String studentID, String stateName, String step, String actionType,
   		String correctness, String skillName, String selection, String input, String action) {

   	String log = 
   		studentID + "\t" + brdID + "\t" + stateName + "\t" + step  + "\t" + skillName + "\t" + 
   		actionType + "\t" + correctness + "\t" + selection + "\t" + action + "\t" + input;
   	logPrintWriter.println(log);
   }

   /**
    * Returns either TRUE_POSITIVE or FALSE_POSITIVE for a given rule activation at a given
    * problemNode
    * 
    * inquirySolverTutor() returns EdgeData.CORRECT_ACTION or CLT_ERROR_ACTION, though. 
    * This must be adjusted.
    * 
    * @param problemName
    * @param problemNode
    * @param ruleActivated
    * @return
    */
   public String inquiryRuleActivation(String problemName, ProblemNode problemNode, RuleActivationNode ruleActivated) {

       String selection = ruleActivated.getActualSelection();
       String action = ruleActivated.getActualAction();
       String input = ruleActivated.getActualInput();
       String ruleName = ruleActivated.getName();
       
     
       String result = inquiryRuleActivation(problemName, problemNode, ruleName, selection, action, input, ruleActivated);
       return result;
   }

   public String inquiryRuleActivation(String problemName, ProblemNode problemNode,
                                       String selection, String action, String input) {

       return inquiryRuleActivation(problemName, problemNode, "", selection, action, input, null);
   }

   public String inquiryRuleActivation(String problemName, ProblemNode problemNode, String ruleName,  
           String selection, String action, String input, RuleActivationNode ran){
   	
   	//TODO THIS is the ugliest method I have ever seen. Please clean it up. -Maclellan
		
   	ProblemNode startNode = null;
   	String lastEquation = null;
   	
  
   	
   	if(isValidationMode)
   	{
   		startNode = getValidationGraph().getStartNode();
   	}
   	else if(getSsInteractiveLearning() == null || !getSsInteractiveLearning().isTakingQuiz()) {
	        startNode = getBrController().getProblemModel().getStartNode();
	        // String lastEquation = findLastEquation(startNode, problemNode);
	        lastEquation = getProblemStep();
	        if (ruleName.indexOf("typein") > 0 && getLastSkillOperand() != null) {
	            lastEquation += "[" + getLastSkillOperand() + "]";
	        }
   	}

   	if (this.isSsAplusCtrlCogTutorMode() && this.getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()){
   		startNode = getQuizGraph().getStartNode();  		
   	}
   	
       if(trace.getDebugCode("miss"))trace.out("miss", "inquiryRuleActivation: ============== ");
       if(trace.getDebugCode("miss"))trace.out("miss", "        problem: " + problemName);
       if(trace.getDebugCode("miss"))trace.out("miss", "           node: " + problemNode + "(" + lastEquation + ")");
       if(trace.getDebugCode("miss"))trace.out("miss", "      rule name: " + ruleName);
       if(trace.getDebugCode("miss"))trace.out("miss", "actualSelection: " + selection);
       if(trace.getDebugCode("miss"))trace.out("miss", "   actualAction: " + action);
       if(trace.getDebugCode("miss"))trace.out("miss", "    actualInput: " + input);
       if(trace.getDebugCode("miss"))trace.out("miss", "    asking oracle: " + getRuleActivationTestMethod() /*getRuleActivationTestMethod()*/);
       
       String mtStatus = null;
       if (this.isUseCacheOracleInquiry()){
    	 
               mtStatus = lookupRuleActivationStatus(problemName, lastEquation, "", selection, action, input);
       }

       if (mtStatus == null) {
               // if there is no FALSE/FELAS  *or*  !isIlFromBrd, ask the respective oracle
               if (input.toUpperCase().indexOf("FALSE") == -1 && input.toUpperCase().indexOf("FELAS") == -1) {
               		
                   if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_TS)) {
                       mtStatus = inquiryRaTutoringService(selection, action, input, problemNode, problemName + ".brd") ;
                   } 
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_HO)) {
                       // ruleActivated.getName()
                       mtStatus = inquiryRuleActivationOracle(selection, action, input, problemNode, problemName, ruleName, ran, null);
                   }
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_CL)) {
                       String problem = startNode.getName();
                       mtStatus = inquiryClAlgebraTutor(selection, action, input, problemNode, problem);
                   }
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_BRD)){
                       // in interactive interactive learning, we pass current BRD (like here)
                       // but in batch interactive learning, we want to pass whatever BRD is used at the time
                       mtStatus = inquiryRuleActivationBRD(selection, action, input, problemNode.getName(), currentBrdPath);
                   }
                   //nbarba 06/01/14: elseif condition to redirect to the Jess oracly inquiry function 
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_JESS_ORACLE)){
                	   mtStatus=inquiryJessOracle(selection, action, input, problemNode, problemName);
                   }
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_VOID)){
                       mtStatus = "VOID";
                   }
                   //nbarba 06/01/14: elseif condition to redirect to the Jess oracly inquiry function 
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_WEBAUTHORING)){
                	   
                	   mtStatus=this.inquireRAWebAuthoring(selection, input, action, problemNode, problemName);
                	   if (mtStatus.equals(EdgeData.CORRECT_ACTION)){
                		   
                			MessageObject mo = MessageObject.create("InterfaceAction");
                		    mo.setVerb("NotePropertySet");
                		    mo.setSelection(selection);
                		    mo.setAction(action);
                		    mo.setInput(input);    
                		    mo.setTransactionId(mo.makeTransactionId());
                		    
                			this.getBrController().getUniversalToolProxy().sendMessage(mo);
                			getSsRete().setSAIDirectly(selection,action,input);
                	   }
                	   
                   }
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_TUTOR_SOLVER)) {
                       //String problem = getBrController().getProblemModel().getStartNode().getName();
                       mtStatus = inquirySolverTutor(selection, action, input, problemNode, problemName);
                   }
                   else if (getRuleActivationTestMethod().equalsIgnoreCase(RA_TEST_METHOD_TUTOR_SOLVERV2)) {
                	   
                	   		/* when in aplus cog tutor, temporarily switch to taking quiz. this is so the CL oracle can find id solution is correct
                	   		   we switch back again */
                	   		if (this.isSsAplusCtrlCogTutorMode() && this.getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()){
                	   			this.getBrController().getMissController().getSimStPLE().getSsInteractiveLearning().setTakingQuiz(true);
                	   		}
                	   
                	   
                           String problem = "";
                           if(getSsInteractiveLearning()== null || !getSsInteractiveLearning().isTakingQuiz())
                                   problem = startNode.getName();
                           else 
                                   problem = problemName; // When taking quiz use the problemName as we donot have reference to the BR_Controller

                          
                          
                           mtStatus = builtInInquiryClTutor(selection, action, input, problemNode, problem);
                     
                         
                       /*
                           if (this.isSsCogTutorMode() && !this.isSsAplusCtrlCogTutorMode()){
                    	   
                        	   AskHint hint = getCorrectSAI(getBrController(), problemNode);
                        	   JOptionPane.showMessageDialog(null, "faskelo: skill is " + hint.skillName);
                    	   
                    	   
                           }
                    	 */  
                    	   
                    	   
                       
                       if (getSsInteractiveLearning() != null && getSsInteractiveLearning().isTakingQuiz()){

                    	   
                    	   String loggedAction=SimStLogger.CONFIRMATION_REQUEST_CL_ACTION;
                    	   
                    	   if (this.isSsCogTutorMode())
                    		   loggedAction=SimStLogger.CONFIRMATION_REQUEST_CL_ACTION_HUMAN;
                    	   
                           // If what the SimStudent did at this step is wrong ask from Oracle what would have been the right step
                           if(mtStatus.equals(EdgeData.CLT_ERROR_ACTION)){
                        	 
                               AskHint hint = getCorrectSAI(getBrController(), problemNode);
                                         

                            
                               
                               logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ,loggedAction/*SimStLogger.CONFIRMATION_REQUEST_CL_ACTION*/,
                                       getProblemStepString(), mtStatus, "", new Sai(selection, action, input), mtStatus.equals(EdgeData.CORRECT_ACTION),
                                       hint.getSelection(), hint.getAction(), hint.getInput());
                           } else {
                               logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ,loggedAction/*SimStLogger.CONFIRMATION_REQUEST_CL_ACTION*/,
                                       getProblemStepString(), mtStatus, "", new Sai(selection, action, input), mtStatus.equals(EdgeData.CORRECT_ACTION),
                                       "","","");
                           }
                          
                       }
                       
                       /*switch back*/
                   	   if (this.isSsAplusCtrlCogTutorMode() && this.getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().getQuizSolving()){
                   				this.getBrController().getMissController().getSimStPLE().getSsInteractiveLearning().setTakingQuiz(false);
                   	   }
                       
                   }
                   // Sun Aug 20 16:26:02 LDT 2006 :: Noboru
                   // Keep record of the oracles for rule activation status for the future use
 
                   if (mtStatus != null && this.isUseCacheOracleInquiry()) {
                       addRuleActivationStatus(problemName, lastEquation, "", selection, action, input, mtStatus);
                       saveRuleActivationStatus();
                   }
               } else {
                   mtStatus = EdgeData.CLT_ERROR_ACTION;
               }
       }
       
 //      trace.out("### SimStudent for problem " + problemName + " asked " + getRuleActivationTestMethod() + " and result is " + mtStatus);
       if(trace.getDebugCode("miss"))trace.out("miss", "inquiryRuleActivation: >>>>> result: " + mtStatus);
        
       return mtStatus;
}





   private String inquireRAWebAuthoring(String selection, String input, String action, ProblemNode problemNode,
		String problemName) {
	// TODO Auto-generated method stub
	String returnValue = this.getInquiryWebAuthoring().isCorrectStep(selection, action, input) ? EdgeData.CORRECT_ACTION : EdgeData.UNTRACEABLE_ERROR;

	return returnValue;
}

/*public String calcProblemStepString()
   {
       ProblemNode currentNode = getBrController().getCurrentNode();
       if(currentNode == null)
       	return "";
   	String lastEquation = findLastEquation(getBrController().getProblemModel().getStartNode(), currentNode);
   	    	    	
       if(lastEquation == null)
       	lastEquation = getBrController().getProblemModel().getProblemName();

       boolean useOperand = false;
       if(currentNode.getInDegree() > 0)
       {
       	//ProblemEdge edge = (ProblemEdge) currentNode.getIncomingEdges().get(0);
       	ProblemEdge edge = (ProblemEdge) currentNode.getIncomingEdges().get(currentNode.getInDegree()-1);
       	//useOperand = !edge.getSelection().contains("commTable2_");
       	if(edge.getSource().getInDegree() > 0)
       	{
       		ProblemEdge prevEdge = (ProblemEdge) edge.getSource().getIncomingEdges().get(edge.getSource().getInDegree()-1);
       		if(edge.getSelection().contains("dorminTable3_") || prevEdge.getSelection().contains("dorminTable3_"))
       			useOperand = true;
       	}
       	else
       	{
       		useOperand = true;
       	}
       }
   	if (useOperand && getLastSkillOperand() != null) 
       {
           lastEquation += "[" + getLastSkillOperand() + "]";
       }
   	//JOptionPane.showMessageDialog(null, lastEquation);
   	return lastEquation;
   }*/

   public transient Vector<RuleActivationNode> altSug=null;
   
   public String verifyStep(String problemName, ProblemNode problemNode,
                                       String selection, String action, String input)
   {
   	String mtStatus = "";
   	
   	if(problemName == null || problemNode == null || selection == null || action == null || input == null)
   		return EdgeData.UNKNOWN_ACTION;
   	
   	//if(getBrController().getMissController().isPLEon())
   		//getBrController().getMissController().getSimStPLE().setAvatarThinking();
	
//   		if (getInquiryJessOracle()==null)
   			mtStatus = builtInInquiryClTutor(selection, action, input, problemNode, problemName);
//		  else{
						/*System.out.println("@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*");
						System.out.println("@*@*@*@*@* checking for status.... for " + problemNode.getName());
			
						
						mtStatus=inquiryJessOracle(selection, action, input, problemNode, problemName);
						if (mtStatus.contains("Error")){
						System.out.println("@*@*@*@*@* calling again...");
			
							mtStatus=inquiryJessOracle(selection, action, input, problemNode, problemName);
						}
					
						System.out.println("&&&& mtstatus = " + mtStatus);*/

				//trace.out("*@*@*@ ta sugs einai: " + altSug);
			
				//InquiryJessOracle iJessOracle = getInquiryJessOracle();
//			   InquiryJessOracle iJessOracle =  new InquiryJessOracle(getBrController().getMissController().getSimSt(),getBrController()); 
			     
	
//		       iJessOracle.init(this.getBrController().getProblemName());
		       
//		       ProblemNode currentNode = getBrController().getCurrentNode();
						       
//			   ProblemNode prevNode = problemNode;
								
//			   iJessOracle.goToState(this.getBrController() , prevNode);
		   	
//		       try {
//		    	   mtStatus = iJessOracle.isCorrectStep(selection, action, input) ? EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION;
//		    	   trace.out("*@*@*@ Student entered " + selection + action + input  + " and MTSTATUS einai: " + mtStatus);	
//		          } catch (Exception e) {
//		              e.printStackTrace();
//		        }
		        
	
//			*/
			
//		}
			
   		
   		
   		
   	// Added by Rohan.
       // To be removed.
   //	if(trace.getDebugCode("ss"))trace.out("ss", "In verifyStep and called builtInInquiryClTutor() with mtStatus as" + mtStatus);
   	
   	//if(getBrController().getMissController().isPLEon())
   	//	getBrController().getMissController().getSimStPLE().endAvatarThinking();


       return mtStatus;
   }

   // problemStep is updated promptly by ruleActivationInquiry oracles. 
   // 
     String problemStep = null;
     String lastProblemStep = null;
   public String getProblemStep() { return problemStep; }
	public void setProblemStep(String problemStep) {
		this.lastProblemStep = this.problemStep;
		this.problemStep = problemStep;
	}
	private boolean isProblemStepUpdated() {
		boolean isProblemStepUpdated = false;
		if (this.problemStep != null && this.problemStep.equals(lastProblemStep)) {
			isProblemStepUpdated = true;
			this.lastProblemStep = null;
		}
		return isProblemStepUpdated;
	}
	
	/**
    * See if the skill name model traced by MT matches with the one specified by the model
    * (usually they are specified in the BRD). 
    * 
    * @param modelSkill A target skill name to be fired
    * @param tracedSkill A skill name that has been actually fired. 
    * @return
    */
   public /*private*/ boolean skillNameMatched(String modelSkill, String tracedSkill) {

       modelSkill = modelSkill.toLowerCase().replaceAll("auto-", "");
       tracedSkill = tracedSkill.toLowerCase().replaceAll("auto-", "");

       boolean skillNameMatched = false;

       if (modelSkill.equals(tracedSkill)) {
           // Two skill names match when they are identical 
           skillNameMatched = true;
       } else {
           // tracedSkill maatches with the modelSkill when it is a jisjunctive version of
           // the rule generated by SimSt, which must have extra "-N" at the end of modelSkill
           if (tracedSkill.indexOf(modelSkill)==0) {
               // postfix must be "-" followed by a number
               String postfix = tracedSkill.substring(modelSkill.length());
               if (postfix.charAt(0) == '-')  {
                   String version = postfix.substring(1);
                   try {
                       // if the parseInt does not throw exception, then the tracedSkill is good
                       Integer.parseInt(version);
                       skillNameMatched = true;
                   } catch (Exception e) {
                       ;
                   }
               }
           }
       }
       return skillNameMatched;
   }

   /**
    * Record rule firing during training.  Log rules 
    * 
    * @param trainingProblem
    * @param stateName
    * @param modelSkillName
    */
   private void logRuleActivationsDuringTraining(String trainingProblem, String stateName,
           String modelStatus,
           String modelSkillName) {

       BR_Controller brController = getBrController();
       MTRete rete = brController.getModelTracer().getRete();
       Iterator /* Activation */ activations = rete.listActivations();

       String condition = getSsCondition();
       String logFile = getLearningLogFile();
       while (activations.hasNext()) {
           Activation activation = (Activation)activations.next();
           Defrule defrule = activation.getRule(); 
           String actualSkillName = defrule.getName();
           // ruleFired looks like "MAIN::foobar"
           int idx = actualSkillName.lastIndexOf(':');
           // trim the "MAIN::" part
           actualSkillName = actualSkillName.substring(idx +1);
           String actualStatus = skillNameMatched(modelSkillName, actualSkillName) ? EdgeData.SUCCESS : "FALSE";
           /*
           String msg = trainingProblem + "/" + modelSkill + "/" + ruleFired + "/";
           msg += status + "/" + condition + "/";
            */
           logRuleActivationToFile(LOG_PRTEST_TRAINING, trainingProblem, stateName, 
           		modelStatus, modelSkillName, actualSkillName, actualStatus, 
           		condition, logFile);
       }
   }

 
   /**
    * Finds the activations that are fired at this input node.
    * @param ssNode {@link SimStNode} for which to find activations
    * @param hm
    * @return Vector<RuleActivationNode> 
    */
   public Vector<RuleActivationNode> gatherActivationList(SimStNode ssNode, HashMap hm) {
	
   	Vector<RuleActivationNode> activationList = new Vector<RuleActivationNode>();
   	if(ssNode == null)
   		return null;  	
   	try {
   		
   		if(ssNode.getParents().isEmpty()) {			/*for start node , reset rete and restore initial wm state*/
   			ssRete.reset();
   			ssRete.restoreInitialWMState(ssNode, true);	
   		} else {									/*for subsequent nodes, get edges from start to current node and go to wm state. (note: go to wm state calles restoreInitialWMState which clears rete...) */
   			SimStNode startNode = ssNode.getProblemGraph().getStartNode();
   			Vector<SimStEdge> vec = ssNode.findPathToNode(ssNode);
   			//ssRete.reset();	
   			//ssRete.goToWMState(startNode ,vec, false);
   			ssRete.goToWMState(startNode ,vec, true);

   		}
   		   	 		
   			//MTRete mtRete = getBrController().getModelTracer().getRete();

	    	SimStRete ssRete = getSsRete();
	    	
	    	//ssRete.updateWorkingMemory(ssNode, hm);
	        RuleActivationTree tree = getBrController().getRuleActivationTree();
	        TreeTableModel ttm = tree.getActivationModel();
	        RuleActivationNode root = (RuleActivationNode) ttm.getRoot();

	    	root.saveState(ssRete);
		   
	    	List wholeAgenda = ssRete.getAgendaAsList(null);
		   
	    	  if(trace.getDebugCode("miss"))trace.out("miss", "Gathering activation list... Agenda is : " + wholeAgenda);
	            if(trace.getDebugCode("miss"))trace.out("miss", "mtRete facts are " + ssRete.getFacts());
	            
	            
	    	root.createChildren(wholeAgenda, false);
	    	List children = root.getChildren();
	    	JessModelTracing jmt = ssRete.getJmt();

	    	//fire each rule in agenda to get the sai for every rule.
	    	for(int i=0; i< children.size(); i++) {
	    		RuleActivationNode child = (RuleActivationNode)children.get(i);
				root.setUpState(ssRete, i);
				jmt.setNodeNowFiring(child);
	    		child.fire(ssRete);
				jmt.setNodeNowFiring(null);
	    		activationList.add(child);
	    	}
	     
	    	
	    	
	    	
	    	
	    	
	    	
   	} catch(Exception e) {
   		e.printStackTrace();
   	}
   	
   	
   	
   	
   
       activationList = removeDuplicateActivations(activationList);
   	return activationList;
   }

   /**
    * Finds the activations that are fired at a given problemNode
    * @param problemNode
    * @return Vector<RuleActivationNode>
    */
   public Vector /* RuleActivationNode */<RuleActivationNode> gatherActivationList(ProblemNode problemNode) {

       Vector /* RuleActivationNode */<RuleActivationNode> activationList = new Vector<RuleActivationNode>();
       
       //showActivationList();
       try{
    	   if(trace.getDebugCode("miss")) trace.out("miss", "gatherActivationList: currentNode ==>> " + problemNode); 
    	   trace.out("webAuth","******* Hm... current facts are : " + getSsRete().getFacts());
    	   Set<String> nameSet=getRuleNames();     	
	   	   	for (String skillName : nameSet) {
	   	   		trace.out("webAuth","******* found a skill named: " + skillName);
	   	   	}
    	   
    	   
           if (problemNode != getBrController().getSolutionState().getCurrentNode()) {    
        	   if(trace.getDebugCode("miss")) trace.out("miss", "problem node != solution state "); 
           			getBrController().setCurrentNode2(problemNode);
           }

           ProblemNode currentNode = getBrController().getSolutionState().getCurrentNode();
          

           // MTRete mtRete = getBrController().getModelTracer().getRete();
           if (problemNode.getParents().isEmpty()) {    
               // The problemNode is a Start State   
        	   if(trace.getDebugCode("miss")) trace.out("miss", " Problem is a start state "); 
               getBrController().goToStartStateForRuleTutors();	
           } else {
        	   if(trace.getDebugCode("miss")) trace.out("miss", " We are not in a start state .... "); 
               // Go to the given state and get the productions fire
               boolean useInterfaceTemplate = MTRete.getUseInterfaceTemplates();
               MTRete.setUseInterfaceTemplates(false);
               boolean loadJessFilesSucceeded = false;
              
               while (!loadJessFilesSucceeded) {
                   // Why does this line change the state?
            	  
            	   getBrController().checkProductionRulesChainNew(currentNode);
            	  
                   if (!MTRete.loadInterfacetemplatesFailed()) {
                       loadJessFilesSucceeded = true;
                   } else {
                   		if(trace.getDebugCode("miss"))trace.out("miss", "gatherActivationList: RETRYING checkProductionRulesChainNew...");
                   }
                   
               }
               MTRete.setUseInterfaceTemplates(useInterfaceTemplate);

           }

   		
       	
       // showActivationList();

           // Get a root rule-activation node
           RuleActivationTree tree = getBrController().getRuleActivationTree();
           TreeTableModel ttm = tree.getActivationModel();
           RuleActivationNode root = (RuleActivationNode) ttm.getRoot();
	            MTRete mtRete = getBrController().getModelTracer().getRete();
	            //MTRete mtRete= this.getSsRete();
	            root.saveState(mtRete);
	            //mtRete.setResolutionStrategy(new SimStSolver.SuccessRatioConflictResolutiionStrategy(this));	            
	            	            
	            //showActivationList();
	            
	            // Wed Oct  3 16:55:15 EDT 2007 :: Noboru
	            // There may be inactive activations in wholeAgenda, but they are 
	            // excluded in the RuleActivationTree created by createChildren() below
	            List /* Activation */ wholeAgenda = mtRete.getAgendaAsList(null); 
	            
	            if(trace.getDebugCode("miss"))trace.out("miss", "Gathering activation list... Agenda is : " + wholeAgenda);
	            if(trace.getDebugCode("miss"))trace.out("miss", "mtRete facts are " + mtRete.getFacts());
	        //    if(trace.getDebugCode("miss"))trace.out("miss", "ssRete facts are " + this.getSsRete().getFacts());
	            
	           // printInactiveActivations(wholeAgenda);
	                        
	            boolean omitBuggyRules = false;
	            root.createChildren(wholeAgenda, omitBuggyRules);
	            List children = root.getChildren();
	            JessModelTracing jmt = mtRete.getJmt();
	            for (int i = 0; i < children.size(); ++i) {
	                RuleActivationNode child = (RuleActivationNode)children.get(i);
	                // mtRete.dumpAgenda("gatherActivationList: before setUpState[" + i + "]");
	                root.setUpState(mtRete, i);
	                // mtRete.dumpAgenda("gatherActivationList:  after setUpState[" + i + "]");
	                
	                jmt.fireNode(child);                //child.fire(mtRete);
	                activationList.add(child);
	            }
	          
       }
       catch(Exception e){
       	e.printStackTrace();
           logger.simStLogException(e);
       } 


       
       activationList = removeDuplicateActivations(activationList);
       

       return activationList;
   }

   /* Hash to hold the type of hint for each production rule, so we don't 
    * need to re-read the XML file every time. */
   transient HashMap hintTypeHash=null;  
   
  /**
   * Method that returns the type of a currnt hint.
   * @param ruleName
   */
   public String getHintType(String ruleName){
	   String hintType=null;
	   if (hintTypeHash==null){
		   hintTypeHash = new HashMap();
	   }

	   
	   if (hintTypeHash.containsKey(ruleName)){
		   hintType=(String) hintTypeHash.get(ruleName);
	   }
	   else{
		   XMLReader reader;
		   reader = new XMLReader(this);
		   ArrayList<ArrayList<String>> llist = new ArrayList<ArrayList<String>>();
		   reader.parseXMLFile(ruleName, llist);
		   hintType=llist.get(0).get(0).trim();

		   hintTypeHash.put(ruleName,hintType);
	   }

	  return hintType;
   }
   
   
   
   
   SimStNode currentSsNode;
   public void setCurrentSsNode(SimStNode ssNode){
   	this.currentSsNode=ssNode;
   }

   public SimStNode getcurrentSsNode(){
   	return currentSsNode;
   }



   /**
    * Returns a new vector without any of the SAI's duplicated. Does not affect the original acitvation list
    * @param activationList, containing a vector of activations for the current node
    * @return Vector<RuleActivationNode>, filtered list with the duplicate activations removed
    */
   private Vector<RuleActivationNode> removeDuplicateActivations(Vector<RuleActivationNode> activationList)
   {
   	Vector<Sai> goodSais = new Vector<Sai>();
   	Vector<RuleActivationNode> goodActivationList = new Vector<RuleActivationNode>();
   	for(Object o:activationList)
   	{
   		
   		
   		RuleActivationNode ran = (RuleActivationNode) o;
   		trace.out("miss","checking for "+ ran.getName() + " : " + ran.getActualSelection() +" "+ ran.getActualAction() + " "+ran.getActualInput());
   		Sai sai = new Sai(ran.getActualSelection(), ran.getActualAction(),ran.getActualInput());
   		
   		if(!goodSais.contains(sai))
   		{
   			trace.out("miss","adding to goodSAI");
   			goodSais.add(sai);
   			goodActivationList.add(ran);
   		}
   	}
   	return goodActivationList;
   }

   /**
    * Prints out the inactive activations in the current activation list
    * @param wholeAgenda
    */
   private void printInactiveActivations(List wholeAgenda) {
       for (int i = 0; i < wholeAgenda.size(); i++) {
           Activation activation = (Activation)wholeAgenda.get(i);
           if (activation.isInactive()) {
               trace.err("INACTIVE RuleActivation: " + activation);
           }
       }
   }

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
   // Communicating with the Carnegie Learning Algebra I tutor
   // 

   private static final String PTS_SERVER_HOST_PROPERY = "ptsServerHost";

   // private String clAlgebraTutoringServiceHost = "matsuda-pro.pc.cs.cmu.edu";
   private static String clAlgebraTutoringServiceHost = "localhost";
   public static String getClAlgebraTutoringServiceHost() {
       return System.getProperty(PTS_SERVER_HOST_PROPERY) == null ? 
               clAlgebraTutoringServiceHost :
                   System.getProperty(PTS_SERVER_HOST_PROPERY);
   }

   private static String clAlgebraTutoringServicePort = "7878";
   public static String getClAlgebraTutoringServicePort() { return clAlgebraTutoringServicePort; }

   public transient InquiryClAlgebraTutor inquiryClAlgebraTutor = null;
   private InquiryClAlgebraTutor getInquiryClAlgebraTutor() {

       InquiryClAlgebraTutor icat = null;
       while (icat == null) {
           try {
           	if(trace.getDebugCode("ss"))trace.out("ss", "Trying new Server launch");
               icat = new InquiryClAlgebraTutor(getClAlgebraTutoringServiceHost(), getClAlgebraTutoringServicePort());
               if(trace.getDebugCode("ss"))trace.out("ss", "New InquiryClAlgebraTutor launched");
           } catch (Exception e) {
           	if(trace.getDebugCode("miss"))trace.out("miss", "getInquiryClAlgebraTutor: failed to conneect to the Tutoring Service.");
           	if(trace.getDebugCode("miss"))trace.out("miss", "  >> relaunching the server...");
               try {wait(5000);} catch (InterruptedException e1) {e1.printStackTrace();}
               logger.simStLogException(e,"getInquiryClAlgebraTutor: failed to conneect to the Tutoring Service.");
               // InquiryClAlgebraTutor.relaunchTutoringService();
           }
       }
       return icat;
   }

   // Returns null when selection is not valid to test
   public String inquiryClAlgebraTutor(String selection, String action, String input, ProblemNode sourceNode, String problem) {

   	String result = null;
   	
   	if(selection.equalsIgnoreCase(Rule.DONE_NAME))
   		return UNKNOWN;
   	InquiryClAlgebraTutor icat = getInquiryClAlgebraTutor();
   	
   	int numPrevSteps = -1;
   	while (numPrevSteps < 0) {
   		// clAlgebraTutorGotoOneStateBefore returns -1 when it failed to get
   		// appropritae acknowledgement from the TutoringService
   		numPrevSteps = icat.clAlgebraTutorGotoOneStateBefore(getBrController(), sourceNode);
   		if (numPrevSteps == -2) {
   			// -2 means that the selection is not valid (e.g., entering skill-operand w/o type-in)
   			// return FALSE_POSITIVE;
   			return null;
   		} else if (numPrevSteps < 0) {
   			if(trace.getDebugCode("miss"))trace.out("miss", "RETRY on clAlgebraTutorGotoOneStateBefore...");
   		}
   	}
   	if (validSelection(selection, numPrevSteps)) {
   		try
   		{
   			result = icat.isCorrectStep(selection, action, input) ? TRUE_POSITIVE : FALSE_POSITIVE;
   		}
   		catch(TutorServerTimeoutException e)
   		{
   			logger.simStLogException(e);
   			result = immatureSkillOperand(selection, input);
   		}
   		
   	} else {
   		
   		if(trace.getDebugCode("miss"))trace.out("miss", "inquiryClAlgebraTutor: =^=^=^=^=^=^=^ ");
   		if(trace.getDebugCode("miss"))trace.out("miss", "            selection: " + selection);
   		if(trace.getDebugCode("miss"))trace.out("miss", "                input: " + input);
   		if(trace.getDebugCode("miss"))trace.out("miss", "IMMATURE SKILL-OPERAND: =^=^=^=^=^=^=^ ");

   		result = FALSE_POSITIVE;
   	}
   	icat.shutdown();
   	
   	return result;
   }

   private void dummyWait(long duration) {

       long startTime = (new Date()).getTime();
       while ((new Date()).getTime() - startTime < duration) {
           ;
       }
   }

   // ad-hoc 
   // skill-operand can be entered only when numPrevSteps is 0
   // Thus, the following step to enter a skill-operand is automatically wrong
   // 
   // e.d.  2x + 3     = 5      sub 3
   //       2x + 3 - 3 = _____  add 3
   // 
   // this is necessary, because the Carnegie Learning Algebra Tutor is buggy 
   // with those stpes
   public static boolean validSelection(String selection, int numPrevSteps) {

       boolean validSelection = true;

       // selection: commTable1_C3R3
       int cIndex = selection.indexOf('C');
       int rIndex = selection.indexOf('R');
       if(trace.getDebugCode("miss"))trace.out("miss", "cIndex:" + cIndex + "rIndex: " + rIndex);
       if(cIndex < 0 || rIndex < 0)
       	return false;
       String column = selection.substring(cIndex+1, rIndex);
       if(trace.getDebugCode("miss"))trace.out("miss", "column: " + column );

       //String column = selection.substring(commStem.length(),commStem.length()+1);

       if(trace.getDebugCode("miss"))trace.out("miss", "numPrevSteps: " + numPrevSteps);
       if ((numPrevSteps != 0 && "3".equals(column)) ||
           (numPrevSteps == 0 && !"3".equals(column))) {
           validSelection = false;
       }

       if(trace.getDebugCode("miss"))trace.out("miss", "validSelection: selection=" + selection + ", numPrevSteps=" + numPrevSteps + ", column=" + column);
       if(trace.getDebugCode("miss"))trace.out("miss", " >>> returning " + validSelection);

       if(trace.getDebugCode("miss"))trace.out("miss", "validSelection: " + validSelection );
       return validSelection;
   }

   public static final String COMM_STEM = "dorminTable";

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
   // Invoking CL SolverTutor
   // Fri Dec  5 21:52:29 LMT 2008 :: Noboru
   // 

   static String clSolverTutorHost = "localhost";
   public static String getClSolverTutorHost() { return clSolverTutorHost; }
	public void setClSolverTutorHost(String clSolverTutorHost) {
		this.clSolverTutorHost = clSolverTutorHost;
	}

	static String clSolverTutorPort = "5535";
	public static String getClSolverTutorPort() { return clSolverTutorPort; }
	public void setClSolverTutorPort(String clSolverTutorPort) {
		this.clSolverTutorPort = clSolverTutorPort;
	}

   private transient InquiryClSolverTutor iclSolverTutor = null;
   public void setIclSolverTutor(InquiryClSolverTutor iclSolverTutor) {
       this.iclSolverTutor = iclSolverTutor;
   }
   public InquiryClSolverTutor getIclSolverTutor() {
   	if (iclSolverTutor == null) {
   		iclSolverTutor = new InquiryClSolverTutor(getClSolverTutorHost(), getClSolverTutorPort());
   	}
   	return iclSolverTutor;
   }

   public InquiryClSolverTutor restartIclSolverTutor() {
   	iclSolverTutor = new InquiryClSolverTutor(getClSolverTutorHost(), getClSolverTutorPort());
   	return iclSolverTutor;
   }

   public InquiryClSolverTutor getBuiltInClSolverTutor(){
   	if(iclSolverTutor == null) {
   		iclSolverTutor = new InquiryClSolverTutor();
   	}
   	return iclSolverTutor;
   }

   public static InquiryClSolverTutor iclSolverTutorForEqFeaturePredicate = new InquiryClSolverTutor();

   // Ask CL SolverTutor the correctness of the step
   public String inquirySolverTutor(String selection, String action, String input,
   								 ProblemNode currentNode, String problem) {
   	String result = null;

   	InquiryClSolverTutor iClSolverTutor = getIclSolverTutor();
   	// iClSolverTutor.startProblem(problem);
   	int numPrevSteps = iClSolverTutor.goToState(getBrController(), currentNode);
   	// setProblemStep(iClSolverTutor.getProblemStep());
   	if (validSelection(selection, numPrevSteps)) {
   		try
   		{
	    		result = 
	    			iClSolverTutor.isCorrectStep(selection, action, input) ? 
	    					EdgeData.CORRECT_ACTION :
	    						EdgeData.CLT_ERROR_ACTION;
   		}
   		catch(TutorServerTimeoutException e)
   		{
   			logger.simStLogException(e);
   			result = immatureSkillOperand(selection, input);
   		}
   		// addRuleActivationStatus(problem, currentNode, "", selection, action, input, result);

   	} else {
   		
   		result = immatureSkillOperand(selection, input);
   	}

   	return result; 
   }

   private  transient InquiryJessOracle iJessOracle = null;
   
   public InquiryJessOracle getInquiryJessOracle(){
	   	if(iJessOracle == null) {
	   		iJessOracle = new InquiryJessOracle(this,this.getBrController());
	   	}
	   	return iJessOracle;
	   }
   
  
   /* Wrapper function that calls the Jess Oracle to ask if sai is correct or not.*/
   public synchronized String inquiryJessOracle(String selection, String action, String input, ProblemNode currentNode, String problem) {
       String result = null;
       	InquiryJessOracle iJessOracle = getInquiryJessOracle();
       //InquiryJessOracle iJessOracle = new InquiryJessOracle(this, this.getBrController());
       

       iJessOracle.init(problem);
       

      
       if(selection.equalsIgnoreCase("NotSpecified"))
       {
           return EdgeData.CLT_ERROR_ACTION;
       }
     

       iJessOracle.goToState(getBrController(), currentNode);

   
       try {

      		if(trace.getDebugCode("nbarbaBrd"))  trace.out("nbarba", "Calling isCorrectStep sel: " + selection + " act: " + action + " inp: " + input);
      		result = iJessOracle.isCorrectStep(selection, action, input) ? EdgeData.CORRECT_ACTION : EdgeData.UNTRACEABLE_ERROR;


          } catch (Exception e) {
              e.printStackTrace();
        }

  
       return result;
   }
   
   /* Wrapper function that calls the Jess Oracle and returns the agenda. Used by model tracing validation, just for logging purposes*/
   public synchronized Vector<RuleActivationNode> gatherJessOracleAgenda(ProblemNode currentNode, String problem) {

	   Vector <RuleActivationNode> activationList = new Vector<RuleActivationNode>();
	   
	   InquiryJessOracle iJessOracle = getInquiryJessOracle();
       iJessOracle.init(problem);
       
          
     
       iJessOracle.goToState(getBrController(), currentNode);
   	
   	
       try {
      		if(trace.getDebugCode("nbarba"))  trace.out("nbarba", "Calling gatherJessOracleAgenda for problem :" + problem);
      		
      		activationList=iJessOracle.getJessOracleAgenda();

          } catch (Exception e) {
              e.printStackTrace();
        }
       
  
       return activationList;
   }


   public synchronized String builtInInquiryClTutor(String selection, String action, String input, ProblemNode currentNode, String problem) {

       String result = null;
       InquiryClSolverTutor bist = getBuiltInClSolverTutor();
       
       if(isSaiConverterDefined()){
           bist.setSAIConverter(saiConverter);
       }
       if(selection.equalsIgnoreCase("NotSpecified"))
       {
           return EdgeData.CLT_ERROR_ACTION;
       }
       if (selection.equalsIgnoreCase(Rule.DONE_NAME)){
    	   
           String nextStep = null;
           bist.goToState(getBrController(), currentNode);
           nextStep = bist.askNextStep();
           String clAction = nextStep.split(";")[1];

           if(selection.equalsIgnoreCase(clAction))
               return EdgeData.CORRECT_ACTION;
           else
               return EdgeData.CLT_ERROR_ACTION;
       }
     
       int numPrevSteps = bist.goToState(getBrController(),currentNode);
       
    //   trace.err("** goint to state for currentNode with " + numPrevSteps + " steps!");
       if (isSaiConverterDefined() && saiConverter.validSelection(selection, numPrevSteps)) {
           try {
           		if(trace.getDebugCode("rr"))
           			trace.out("rr", "Calling isCorrectStep sel: " + selection + " act: " + action + " inp: " + input);
           			
           	 
                   result = bist.isCorrectStep(selection, action, input) ? EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION;
//                   trace.err("Calling isCorrectStep sel: " + selection + " act: " + action + " inp: " + input + " and result is " + result);
                   if(result.equals(EdgeData.CLT_ERROR_ACTION)) {
                       result = validateCLResponse(selection, action, input, bist);
                   }   

               } catch (Exception e) {
                   // TODO Figure out what is causing this error
//                   e.printStackTrace();
                   result = immatureSkillOperand(selection, input);
               }


       }
       else {
           result = immatureSkillOperand(selection, input);
       }

       return result;
   }

   /**
    * Check the correctness of the SAI and see if its a special case as outlined below
    * 1) Specific cases like Combine Like Terms and Distribute
    * 3(x+2) = 5 [distribute 3(x+2)]
    * 3x+6 = 5 "5 is marked as incorrect by CL Oracle"
    *
    * 2) Special case for divide type-in.
    * 3x+5=2 [divide 3]
    * x+5/3 = 2/3 "x+5/3 is Student Input and (3x+5)/3 is Oracle response which are both correct"
    */
   private String validateCLResponse(String selection, String action, String input, InquiryClSolverTutor bist) {

       String result= EdgeData.CLT_ERROR_ACTION, exp1= null, exp2 = null;

       if(input.contains(" ") || !isFoaGetterDefined()) // Don't override CL Oracle response for transformation step
           return result;

       Vector<Object> foas = getFoaGetter().foaGetter(getBrController(), selection, action, input,null);

       if(foas.size() < 2)
           return result;

       TableExpressionCell skillCell = (TableExpressionCell) foas.get(0);
       TableExpressionCell sideCell = (TableExpressionCell) foas.get(1);

       if(skillCell != null && (skillCell.getText().startsWith("combine") || skillCell.getText().startsWith("clt") || skillCell.getText().startsWith("distribute"))) {
           if(sideCell.getText().equals(input)) {
               result = EdgeData.CORRECT_ACTION;
               return result;
           } else {
               result = EdgeData.CLT_ERROR_ACTION;
               return result;
           }
       } else if(sideCell != null && (sideCell.getText().startsWith("combine") || sideCell.getText().startsWith("clt") || sideCell.getText().startsWith("distribute"))) {
           if(skillCell.getText().equals(input)) {
               result = EdgeData.CORRECT_ACTION;
               return result;
           } else {
               result = EdgeData.CLT_ERROR_ACTION;
               return result;
           }
       }

       boolean correctNess = false;
       try {
           exp1 = bist.getSm().standardize(input, true);
           exp2 = bist.getSm().standardize(bist.askNextStep().split(";")[2],true);
           correctNess = bist.getSm().algebraicEqual(exp1, exp2);
       } catch(BadExpressionError err) {
           err.printStackTrace();
       }
       if(correctNess) {
           result = EdgeData.CORRECT_ACTION;
           return result;
       }
       return result;
   }
	
	public AskHint getCorrectSAI(BR_Controller brController, ProblemNode currentNode){
		
		AskHint hint = null;
		
      	//hint = new AskHintInBuiltClAlgebraTutor(brController, currentNode);
		//CL oracle must not be hardcoded! Whichever oracle grades the quiz should provide this hint
		String simstudentHintMethod=getHintMethod();
		setHintMethod(getQuizGradingMethod());	
	
		hint = askForHint(brController,currentNode);

		setHintMethod(simstudentHintMethod);
		
      	//		hint.getAction() + hint.getInput());
      	return hint;
	}
	
	
   // Ask CL SolverTutor the correctness of the step
   //Special method for 3 table format, instead of 3 columns
   public String inquirySolverTutorV2(String selection, String action, String input,
   								 ProblemNode currentNode, String problem) {
   	String result = null;
   	if(selection.equalsIgnoreCase(Rule.DONE_NAME))
   		return EdgeData.UNKNOWN_ACTION;
   	if(selection.length() > COMM_STEM.length())
   	{
	    	char table = selection.charAt(COMM_STEM.length());
	    	int rowIndex = selection.indexOf('R')+1;
	    	char row = selection.charAt(rowIndex);
	    	selection = COMM_STEM+"1_C"+table+"R"+row;
	    	if(trace.getDebugCode("ss"))trace.out("ss", "New Selection: "+selection);
   	}
   	InquiryClSolverTutor iClSolverTutor = getIclSolverTutor();
   	// iClSolverTutor.startProblem(problem);
   	int numPrevSteps = iClSolverTutor.goToState(getBrController(), currentNode);
   	// setProblemStep(iClSolverTutor.getProblemStep());
   	if (validSelection(selection, numPrevSteps)) {
   		try
   		{
   			result = 
   				iClSolverTutor.isCorrectStep(selection, action, input) ? 
   						EdgeData.CORRECT_ACTION :
   							EdgeData.CLT_ERROR_ACTION;
   			if(isInteractiveLearning && getSsInteractiveLearning().isTakingQuiz())
   				logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.CONFIRMATION_REQUEST_CL_ACTION, 
   					getProblemStepString(), result, "", new Sai(selection,action,input), result.equals(EdgeData.CORRECT_ACTION),"","","");
   		}
   		catch(TutorServerTimeoutException e)
   		{
   			logger.simStLogException(e);
   			iclSolverTutor = restartIclSolverTutor();
   			result = immatureSkillOperand(selection, input);
   		}
   		// addRuleActivationStatus(problem, currentNode, "", selection, action, input, result);

   	} else {
   		
   		result = immatureSkillOperand(selection, input);
   	}

   	return result; 
   }

   private String immatureSkillOperand(String selection, String input) {
	
   	if(trace.getDebugCode("miss"))trace.out("miss", "inquiryClAlgebraTutor: =^=^=^=^=^=^=^ ");
   	if(trace.getDebugCode("miss"))trace.out("miss", "            selection: " + selection);
   	if(trace.getDebugCode("miss"))trace.out("miss", "                input: " + input);
   	if(trace.getDebugCode("miss"))trace.out("miss", "IMMATURE SKILL-OPERAND: =^=^=^=^=^=^=^ ");

		return isValidateStepsInBRD() ? null : EdgeData.CLT_ERROR_ACTION;
	}

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
   // Communicating with a Tutor via Tutoring Service
   // 

   /**
    * 
    * @param ruleActivated
    * @param problemName
    * @return
    */
   public String inquiryRaTutoringService(String selection, String action, String input, 
                                          ProblemNode sourceNode, String problemName) {

       InquiryRaTutoringService irs = getIraTutoringService(problemName);

       // For some unkonwn reason (apparently a bug), an agenda sometimes has
       // rule activations with "NotSpecific" selection, action, and/or input
       if (MTRete.NOT_SPECIFIED.equals(selection) ||
               MTRete.NOT_SPECIFIED.equals(action) ||
               MTRete.NOT_SPECIFIED.equals(input) ) {
           return "SaiNotSpecified";
       }
       // Need to goto the state that is a target node for the investigation
       irs.sendGoToStateMsg(sourceNode.getName());
       return irs.testSAI(selection, action, input) ? TRUE_POSITIVE : FALSE_POSITIVE;
   }

   private transient InquiryRaTutoringService iraTutoringService = null;
   private String currentIraProblemName = null;

   private InquiryRaTutoringService getIraTutoringService(String problemName) {
       if (!problemName.equals(currentIraProblemName)) {
           if (iraTutoringService != null) {
               iraTutoringService.shutdown();
           }
           iraTutoringService = new InquiryRaTutoringService();
           iraTutoringService.init(problemName);
           currentIraProblemName = problemName;
       }
       return iraTutoringService;
   }

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
   // Reading BRD
   // 

   private String currentBrdPath = null;
   public void setCurrentBrdPath(String s){
       currentBrdPath = s;
   }
  

   //also returns a ProblemNode (possible by setting a field)
   public String inquiryRuleActivationBRD(String selection, String action, String input, 
                                          String parentName, String problemFileName) {

      // return InquiryRuleActivationBRD.isValidSAI(selection, action, input, parentName, problemFileName) ?  TRUE_POSITIVE : FALSE_POSITIVE;
	   return InquiryRuleActivationBRD.isValidSAI(selection, action, input, parentName, problemFileName) ?  EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION ;//TRUE_POSITIVE : FALSE_POSITIVE;
	  
   }

   // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
   // Asking to a human oracle
   // 
   public String inquiryRuleActivationOracle(String actualSelection, String actualAction, String actualInput, 
                                             ProblemNode node, String problemName, String ruleName,
                                             RuleActivationNode ran, Boolean isInquiryCorrect) {

	   
       // See if there has been an oracle for the inquired rule activation 
	   String status = EdgeData.CLT_ERROR_ACTION;
       
       String msg = getInquiry(actualSelection, actualAction, actualInput, ran);
       
       if (msg != EdgeData.CLT_ERROR_ACTION) {
    	   	Sai sai = new Sai(actualSelection, actualAction, actualInput);
          	AskHint hint = null;
          	String title = "Applying the rule " + ruleName.replaceAll("MAIN::", "");
          	String step = getProblemStepString();
          	
          	long verifyRequestTime = Calendar.getInstance().getTimeInMillis();
	       	
          	try{
           		if(logger.getLoggingEnabled()){
    	
        			//hint = new AskHintInBuiltClAlgebraTutor(getBrController(), node);
           			//CL oracle should not be hardcoded. Whichever oracle grades the quiz should be provide hint for logging
           			hint = askForHintQuizGradingOracle(getBrController(),node);
           			
           		}
           	}
           	catch(Exception ex){
           				ex.printStackTrace(); logger.simStLogException(ex);
           	}
           	
           	if(logger.getLoggingEnabled())
           	{
           		logger.simStLog(SimStLogger.SIM_STUDENT_INFO_RECEIVED, SimStLogger.CONFIRMATION_REQUEST_ACTION, step, 
        			"", title+":"+msg, sai,node, hint.getSelection(), hint.getAction(), hint.getInput(),0,msg);		
    	        		
           	}
          	
          	int oracle = 0;
	       	oracle = askInquiry(ruleName, msg);
	       	if (oracle == JOptionPane.YES_OPTION) {
	       		status = EdgeData.CORRECT_ACTION;
	       		if(isSkillNameGetterDefined()) {
	       			getSkillNameGetter().skillNameGetter(getBrController(), actualSelection, actualAction, actualInput);
	       		}
	       	}
	       	// TODO: Need to figure out a unified way to handle all the interface actions at one place
	       	// Model-tracing the student interface action (clicking either the Yes or No button) in response to the 
	       	// SimStudent feedback request
	       	performModelTracingForInquiryResponse(oracle, ruleName);
	       	
	       	if(getSsInteractiveLearning() != null)
	       	{
	       		if(logger.getLoggingEnabled())
	       		{
	       			int verifyDuration = (int) ((Calendar.getInstance().getTimeInMillis() - verifyRequestTime)/1000);
		        		logger.simStLog(SimStLogger.SIM_STUDENT_INFO_RECEIVED, SimStLogger.INPUT_VERIFY_ACTION, step, 
		        				status, title+":"+msg, sai,node,hint.getSelection(), hint.getAction(), hint.getInput(), verifyDuration,msg);
	       		}
	       		
	       	}
       }

       if(trace.getDebugCode("miss"))trace.out("miss", "Oracle status ==> " + status );
       
       return status;
   }
   
   public String getInquiry(String actualSelection, String actualAction, String actualInput, 
           RuleActivationNode ran) {
       if (!actualSelection.equals(MTRete.NOT_SPECIFIED) &&
       		(actualInput.toUpperCase().indexOf("FALSE") == -1) ) {
	       	String message[] = generateQueryMessage(actualSelection, actualAction, actualInput, ran);
	       	String msg = "";
	       	for(int i=0;i<message.length;i++)
	       		msg += message[i]+" ";
	       	return msg;
       } else {
    	   return EdgeData.CLT_ERROR_ACTION;
       }
   }
   
   public int askInquiry(String ruleName, String message) {
	   performModelTracingForInquiry();
	   String title = "Applying the rule " + ruleName.replaceAll("MAIN::", "");
	   if(getMissController().getSimStPLE() != null)
		   getMissController().getSimStPLE().setFocusTab(SimStPLE.SIM_ST_TAB);
	   return displayConfirmMessage(title,message);		   
   }
   
   public String getStatusByInquiryResponse(String ruleName, Boolean isInquiryCorrect) {
	   performModelTracingForInquiry();
	   int oracle = isInquiryCorrect ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION;
	   performModelTracingForInquiryResponse(oracle, ruleName);
	   return isInquiryCorrect ? EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION;
   }
   
   // Should update the working memory for model-tracing i.e. actor and stepCorrectness from here
   public void performModelTracingForInquiry() {
	   if(isSsMetaTutorMode()){
		   long start = Calendar.getInstance().getTimeInMillis();
		   getBrController().getMissController().getSimSt().getModelTraceWM().setRequestType("feedback-request"); 	
		   long end = Calendar.getInstance().getTimeInMillis();
	   }
   }
   
   public void performModelTracingForInquiryResponse(int oracle, String ruleName) {
	   if(isSsMetaTutorMode() && getBrController() != null && getBrController().getAmt() != null) {
 			//System.out.println("Yes or No or Done button clicked ");
 			if(oracle == JOptionPane.YES_OPTION) {
 				// If the feedback is for done then run the model-tracer for done, ButtonPressed, -1
 				if(ruleName.replaceAll("MAIN::", "").contains(WorkingMemoryConstants.DONE_BUTTON_SELECTION)) {
 					getBrController().getAmt().handleInterfaceAction(WorkingMemoryConstants.DONE_BUTTON_SELECTION,
 							WorkingMemoryConstants.BUTTON_ACTION, WorkingMemoryConstants.BUTTON_INPUT);
 				} else {
 					getBrController().getAmt().handleInterfaceAction(WorkingMemoryConstants.YES_BUTTON_SELECTION,
 							WorkingMemoryConstants.BUTTON_ACTION, WorkingMemoryConstants.BUTTON_INPUT);
 				}
 			} else if(oracle == JOptionPane.NO_OPTION) {
 				getBrController().getAmt().handleInterfaceAction(WorkingMemoryConstants.NO_BUTTON_SELECTION,
 						WorkingMemoryConstants.BUTTON_ACTION, WorkingMemoryConstants.BUTTON_INPUT);	      				
 			}
 		}
   }

   	boolean hintRequest=false;
   
   private int valueInstruction(Instruction inst, String input,Vector<String> foas)
   {
   	int value = Math.abs(input.length() - inst.getInput().length());
   	int symInput = 0;
   	int puncInput = 0;
   	int digInput = 0;
   	for(int i=0;i<input.length();i++)
   	{
   		if(Character.isLetter(input.charAt(i)))
   			symInput++;
   		else if(Character.isDigit(input.charAt(i)))
   			digInput++;
   		else
   			puncInput++;
   	}
   	int symInst = 0;
   	int puncInst = 0;
   	int digInst = 0;
   	for(int i=0;i<inst.getInput().length();i++)
   	{
   		if(Character.isLetter(inst.getInput().charAt(i)))
   			symInst++;
   		else if(Character.isDigit(inst.getInput().charAt(i)))
   			digInst++;
   		else
   			puncInst++;
   	}
   	value += 5*Math.abs(symInput -symInst)+Math.abs(digInput -digInst)+3*Math.abs(puncInput -puncInst);
   	
   	String instFoas="";
   	String actualFoas="";
   	for(int i=1;i<inst.getFocusOfAttention().size();i++)
   	{
   		if(i-1 < foas.size())
   			actualFoas+= foas.get(i-1);
   		instFoas += inst.getFocusOfAttention().get(i);
   	}
   	
   	int symAFoa = 0;
   	int puncAFoa = 0;
   	int digAFoa = 0;
   	for(int i=0;i<actualFoas.length();i++)
   	{
   		if(Character.isLetter(actualFoas.charAt(i)))
   			symAFoa++;
   		else if(Character.isDigit(actualFoas.charAt(i)))
   			digAFoa++;
   		else
   			puncAFoa++;
   	}
   	int symIFoa = 0;
   	int puncIFoa = 0;
   	int digIFoa = 0;
   	for(int i=0;i<instFoas.length();i++)
   	{
   		if(Character.isLetter(instFoas.charAt(i)))
   			symIFoa++;
   		else if(Character.isDigit(instFoas.charAt(i)))
   			digIFoa++;
   		else
   			puncIFoa++;
   	}
   	value += 5*Math.abs(symAFoa -symIFoa)+Math.abs(digAFoa -digIFoa)+3*Math.abs(puncAFoa -puncIFoa);
   	
   	return value;
   }

   public String[] instructionStepDesc(String ruleName, Sai sai, Vector foas)
   {
   	if(foas == null || sai == null)
   		return null;
   	
   	String[] parts = new String[2];
   	
   	Vector<String> foaContents = new Vector<String>();
   	for(int i=0;i<foas.size();i++)
   	{
   		if(getBrController().lookupWidgetByName((String)foas.get(i)) instanceof JCommTable.TableCell)
   		{
   			JCommTable.TableCell cell = (JCommTable.TableCell)getBrController().lookupWidgetByName((String)foas.get(i));
   			foaContents.add(cell.getText());
   		}
   	}
   	
   	if(ruleName.contains("&"))
   		ruleName = ruleName.substring(0, ruleName.indexOf("&"));
   	Vector<Instruction> insts = instructions.get(ruleName);
   	if(insts == null || insts.size() == 0)
   	{
   		parts[0] = "something similar";
   		parts[1] = "a previous problem";
   		return parts;
   	}
 		Instruction inst = insts.get(0);
   	int compare = valueInstruction(inst, sai.getI(),foaContents); 

 		//Go through the instructions to find the one which is most similar, at a quick size-glance
 		for(int i=1;i<instructions.get(ruleName).size();i++)
 		{
 			Instruction tempInst = (Instruction) instructions.get(ruleName).get(i);
 			int value = valueInstruction(tempInst, sai.getI(),foaContents);
 	    	
 			if(value <= compare)
 			{
 				compare = value;
 				inst = tempInst;
 			}
 		}

 		String foaDesc = "";
   	if(foaGetter != null)
   	{
   		foaDesc = foaGetter.foaStepDescription(inst);
   	}
   	else
   	{
   		foaDesc = inst.getFocusOfAttention().toString();
   	}
   	
   	parts[0] = inst.getInput();
   	parts[1] = foaDesc;
   	
   	return parts;
   }

   public String instructionDesc(String ruleName, Sai sai, Vector foas)
   {
	   
	setWhyNotInstruction(null);
   	if(foas == null || sai == null)
   		return null;
   	
   	Vector<String> foaContents = new Vector<String>();
   	for(int i=0;i<foas.size();i++)
   	{
   		if(getBrController().lookupWidgetByName((String)foas.get(i)) instanceof JCommTable.TableCell)
   		{
   			JCommTable.TableCell cell = (JCommTable.TableCell)getBrController().lookupWidgetByName((String)foas.get(i));
   			foaContents.add(cell.getText());
   		}
   	}
   	
 		Instruction inst = (Instruction) instructions.get(ruleName).get(0);
 		int compare = valueInstruction(inst, sai.getI(),foaContents); 
 		//Go through the instructions to find the one which is most similar, at a quick size-glance
 		for(int i=1;i<instructions.get(ruleName).size();i++)
 		{
 			Instruction tempInst = (Instruction) instructions.get(ruleName).get(i);
 			if(!tempInst.isRecent())
 				continue;
 			int value = valueInstruction(tempInst, sai.getI(),foaContents);
 	    	
 			if(value <= compare)
 			{
 
 				compare = value;
 				inst = tempInst;
 			}
 		}

 		if(!inst.isRecent())
 			return null;
 		
 		String foaDesc = "";
   	if(foaGetter != null)
   	{
   		foaDesc = foaGetter.foaDescription(inst);
   	}
   	else
   	{
   		foaDesc = inst.getFocusOfAttention().toString();
   	}
   	/*String question = "";
   	if(Math.random() > .5)
   	{
	  		question = "But I put "+foaDesc+".  Why doesn't "+sai.getI()+" work now?";
	
	  		if(sai.getS().equalsIgnoreCase(Rule.DONE_NAME))
	  		{
	  			question = "But I put "+foaDesc+".  Why isn't the problem done now?";
	  		}
   	}
   	else
   	{
	  		question = "But before I did "+foaDesc+".  I thought that would be the same with "+sai.getI()+" here.  Why is this different?";
	
	  		if(sai.getS().equalsIgnoreCase(Rule.DONE_NAME))
	  		{
	  			question = "But before I was "+foaDesc+", so I thought it would be the same here. How is this problem different?";
	  		}
   	}
   	return question;*/
   	
   	setWhyNotInstruction(inst);
   	
   	return foaDesc;
   }
   
   
   public String currentStepDescription(String ruleName, Sai sai)
   {
	   
   
   	
   	Vector foas=getBrController().getMissController().getSimSt().getFoaGetter().foaGetter(getBrController(), sai.getS(), sai.getA(), sai.getI(), null);
   	
	if(foas == null || sai == null)
   		return null;
   	
   	String foaDesc="";
   		
	for (int i=0;i<foas.size();i++){
		String foaInput="";
		String foaName="";
					
		if (foas.elementAt(i) instanceof JCommComboBox ){
			foaInput = (String) ((JCommComboBox)foas.elementAt(i)).getValue();
			foaName = (String) ((JCommComboBox)foas.elementAt(i)).getCommName();
		}
		else if (foas.elementAt(i) instanceof JCommTextField  ){
			foaInput = ((JCommTextField )foas.elementAt(i)).getText();
			foaName = (String) ((JCommTextField)foas.elementAt(i)).getCommName();
		}
		else {
			foaInput = ((TableExpressionCell)foas.elementAt(i)).getText();
			foaName = (String) ((TableExpressionCell)foas.elementAt(i)).getCommName();
		}

		foaDesc = foaDesc + " <font color=red>" + foaInput + "</font> and";
	}
  

	if (foaDesc.length()>3)
		foaDesc=foaDesc.substring(0, foaDesc.length()-3);
   
      	
  	return foaDesc;
   }
   
   
   
   
   
   public transient Instruction whyNotInstruction=null;
   public void setWhyNotInstruction(Instruction inst){this.whyNotInstruction=inst;}
   public Instruction getWhyNotInstruction(){return whyNotInstruction;}
   
   public String relevantFoaString(String input, Vector foas)
   {

   	if(!isFoaGetterDefined())
   		return foas.get(0).toString();
   	
   	Vector<String> foaContents = new Vector<String>();
   	for(int i=0;i<foas.size();i++)
   	{
   		if(getBrController().lookupWidgetByName((String)foas.get(i)) instanceof JCommTable.TableCell)
   		{
   			JCommTable.TableCell cell = (JCommTable.TableCell)getBrController().lookupWidgetByName((String)foas.get(i));
   			foaContents.add(cell.getText());
   		}
   	}
   	
   	return foaGetter.relevantFoaString(input, foaContents);
   }


   //TODO: change this to a mapping text file with what values mean
   //Keiser 11/25/09 - create a message if what SimSt did was right, based on the SAI
   //Assumes preposition is part of getComponentName() when used - 12/15
   public String[] generateQueryMessage(String selection, String action, String input, RuleActivationNode ran)
   {
   	String message[] = new String[2];
   	
   	if(getMissController().isPLEon())
   	{
   		SimStPLE ple = getMissController().getSimStPLE();
   		if(ran == null)
   		{
	    		if(selection.equalsIgnoreCase(Rule.DONE_NAME))
	    		{
	    			message[0] = ple.getConversation().getMessage(SimStConversation.DONE_FEEDBACK_TOPIC);
	    			message[1] = "";
	    		}
	    		else
	    		{
	    			message[0] = ple.getConversation().getSimStMessage1(SimStConversation.FEEDBACK_TOPIC, ple.getComponentName(selection), input);
	    			message[1] = "";
	    		}
   		}
   		else
   		{
	    		if(selection.equalsIgnoreCase(Rule.DONE_NAME))
	    		{
	    			message[0] = ple.getConversation().getMessage(SimStConversation.DONE_FEEDBACK_TOPIC, ran.getAgendaIndex());
	    			message[1] = "";
	    		}
	    		else
	    		{
	    			message[0] = ple.getConversation().getSimStMessage(SimStConversation.FEEDBACK_TOPIC, /*ple.getComponentName(selection)*/ selection , input, ran.getAgendaIndex());
	    			message[1] = "";
	    		}
   		}
   	}
   	else
   	{
	    	//Use different messaging if the action is pressing a button
	    	if(action.equalsIgnoreCase("ButtonPressed"))
	    	{
	    		if(selection.equalsIgnoreCase(Rule.DONE_NAME))
	    		{
	    			message[0] = "I think the problem is solved, so I clicked the problem solved button.";
	    		}
	    		else if(getMissController().getSimStPLE() != null && getMissController().getSimStPLE().getComponentName(selection) != null)
	    		{
	    			message[0] = "I clicked "+getMissController().getSimStPLE().getComponentName(selection)+".";
	    		}
	    		else
	    		{
	    			message[0] = "I clicked the " + selection +" button.";
	    		}
	    	}
	    	else if(getMissController().isPLEon() && getMissController().getSimStPLE().getComponentName(selection) != null)
	    	{
	    		message[0] = "I entered \"" + input +"\" in "+getMissController().getSimStPLE().getComponentName(selection)+"."; 
	    	}
	    	else
	    	{
	    		message[0] = "I entered \"" + input + "\" into " + selection +".";
	    	}
	    	
	    	message[1] = "Do you think that would be a good move?";
   	}
   	
   	
   	return message;
   }


   public String lookupHint(String problemName, ProblemNode node) {

       Hashtable<String, String> hintHash = ensureGetHintHash();

       String hintTemplate = genHintTemplate(problemName, node.getName());

       String sai = hintHash.get(hintTemplate);
       if (sai != null) {
       	if(trace.getDebugCode("miss"))trace.out("miss", "lookup hint: %%%%%%%%%%%%%%%");

       	if(trace.getDebugCode("miss"))trace.out("miss", "  |-->   Status: " + sai);
       }

       return sai;
   }
 
   
   private String lookupRuleActivationStatus(String problemName, 
           String state, String ruleName,
           String selection, String action, String input) {

       Hashtable<String, String> activationStatusHash = ensureGetActivationStatusHash();

       String activationTemplate = 
           genActivationTemplate(problemName, state, ruleName, selection, action, input);

       String status = activationStatusHash.get(activationTemplate);
       if (status != null) {
       	if(trace.getDebugCode("miss"))trace.out("miss", "lookupRuleActivationStatus: %%%%%%%%%%%%%%%");
       	if(trace.getDebugCode("miss"))trace.out("miss", "  |   Selection: " + selection);
       	if(trace.getDebugCode("miss"))trace.out("miss", "  |       Input: " + input);
       	if(trace.getDebugCode("miss"))trace.out("miss", "  |      Action: " + action);
       	if(trace.getDebugCode("miss"))trace.out("miss", "  |-->   Status: " + status);
       }

       return status;
   }

   private void addRuleActivationStatus(String problemName, 
           String state, String ruleName,
           String selection, String action, String input,
           String status) {

   	Hashtable<String, String> activationStatusHash = ensureGetActivationStatusHash();
   	String activationTemplate =
   		genActivationTemplate(problemName, state, ruleName, selection, action, input);
   	activationStatusHash.put(activationTemplate, status);
   }

   private String genHintTemplate(String problemName, String nodeName) {

       String hintTemplate = problemName + "$" + nodeName;

       return hintTemplate;
   }

   private String genActivationTemplate(String problemName, String nodeName, String ruleName, 
           String selection, String action, String input) {

       String activationTemplate = problemName + "$" + nodeName + "$" + ruleName;
       activationTemplate += "$" + selection + "$" + action + "$" + input;

       return activationTemplate;
   }

   // Name of a file that keeps rule "correctness" of the rule activation
   private String HINT_FILE = "hint-hash.txt";
   private String RULE_ACTIVATION_STATUS_FILE = "rule-activation-hash.txt";
   public static final String TRUE_POSITIVE = "TruePositive"; // read by SimStInteractive
   private static final String FALSE_POSITIVE = "FalsePositive";
   private static final String TRUE_NEGATIVE = "TrueNegative";
   private static final String FALSE_NEGATIVE = "FalseNegative";
   private static final String UNKNOWN = "Unknown";
   private static final String TRUE_CORRECT="TrueCorrect";
   private static final String TRUE_ERROR="TrueError";
   private static final String FALSE_CORRECT="FalseCorrect";
   private static final String FALSE_ERROR="FalseError";
   private static final String TRUE_BUGGY="TrueBuggy";
   private static final String FALSE_BUGGY="FalseBuggy";
   private static final String TRUE_INCORRECT="TrueIncorrect";
   private static final String FALSE_INCORRECT="FalseIncorrect";
   private static final String MODELTRACING_VALIDATION_METHOD_STRICT="Strict";
   private static final String MODELTRACING_VALIDATION_METHOD_RELAXED="Relaxed";

   
   transient Hashtable<String, String> hintHash = null;

   public Hashtable<String, String> getHintHash() {
       return hintHash;
   }
   void setHintHash(Hashtable<String, String> hintHash) {
       this.hintHash = hintHash;
   }
   
   
   // Hashtable for Activation Status
   // The key consists of "<problemName>$<stateName>$<ruleName>"
   // The value is rule firing status, which might be 
   // either "FalsePositive" or "TuePositive"
   transient Hashtable<String, String> activationStatusHash = null;

   public Hashtable<String, String> getActivationStatusHash() {
       return activationStatusHash;
   }
   void setActivationStatusHash(Hashtable<String, String> activationStatusHash) {
       this.activationStatusHash = activationStatusHash;
   }

   private boolean ruleActivationHashExpanded = false;
   boolean ruleActivationHashExpanded() {
       return ruleActivationHashExpanded; 
   }
   void resetRuleActivatedHashExpanded() {
       ruleActivationHashExpanded(false);
   }
   void ruleActivationHashExpanded(boolean flag) {
       this.ruleActivationHashExpanded = flag;
   }

   private Hashtable<String, String> initHintHash() {//merge this with the other init method?

       Hashtable<String, String> hashtable = new Hashtable<String, String>();

       FileReader fileReader;
       BufferedReader bufferedReader;

       File file = new File(HINT_FILE);
       if (file.exists()) {
           try {
               fileReader = new FileReader(file);
               bufferedReader = new BufferedReader(fileReader); 

               String line = null;
               while ((line = bufferedReader.readLine()) != null) {
                   String[] keyValue = line.split("\t");
                   hashtable.put(keyValue[0], keyValue[1]);
               }

               bufferedReader.close();
               fileReader.close();

           } catch (Exception e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }
       }

       setHintHash(hashtable);
       return hashtable;
   }


   private Hashtable<String, String> initActivationStatusHash() {

       Hashtable<String, String> hashtable = new Hashtable<String, String>();

       FileReader fileReader;
       BufferedReader bufferedReader;

       File file = new File(RULE_ACTIVATION_STATUS_FILE);
       if (file.exists()) {
           try {
               fileReader = new FileReader(file);
               bufferedReader = new BufferedReader(fileReader); 

               String line = null;
               while ((line = bufferedReader.readLine()) != null) {
                   String[] keyValue = line.split("\t");
                   hashtable.put(keyValue[0], keyValue[1]);
               }

               bufferedReader.close();
               fileReader.close();

           } catch (Exception e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }
       }

       setActivationStatusHash(hashtable);
       return hashtable;
   }

   public /*private*/ void saveRuleActivationStatus() {

       PrintStream out;

       File file = new File(RULE_ACTIVATION_STATUS_FILE);
       try {

           FileOutputStream fileOutputStream = new FileOutputStream(file);
           out = new PrintStream(fileOutputStream);

           Enumeration<String> keys = getActivationStatusHash().keys();
           while (keys.hasMoreElements()) {

               String key = keys.nextElement();
               String status = getActivationStatusHash().get(key);

               out.println(key + "\t" + status);
           }

           out.close();
           fileOutputStream.close();

       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   private Hashtable<String, String> ensureGetHintHash() {

       Hashtable<String, String> hintHash = getHintHash();
       if (hintHash == null) {
           hintHash = initHintHash();
       }
       return hintHash;
   }

   private Hashtable<String, String> ensureGetActivationStatusHash() {

       Hashtable<String, String> activationStatusHash = getActivationStatusHash();
       if (activationStatusHash == null) {
           activationStatusHash = initActivationStatusHash();
       }
       return activationStatusHash;
   }

   private final String LOG_PRTEST_TRAINING = "training";
   public /*private*/ final String LOG_PRTEST_TEST = "test";
   private final String LOG_PRTEST_AGENDA = "agenda";
   private final String NOT_AVAILABLE = "N/A";

   /**
    * @param phase Either "training" or "test" The log can be made for rule firing both during training and testing 
    * @param problemName Name of the test problem
    * @param stateName Name of the state the rule is to be applied (starting from the "start state")
    * @param modelSkillName Name of the rule in the model
    * @param actualSkillName Name of the rule actually fired
    * @param actualStatus Status returned from the model tracer
    * @param condition Experimental condition
    * @param output File to be written
    */
   private void logRuleActivationToFile( String phase, String problemName, String stateName,
           String modelStatus, String modelSkillName, 
           String actualSkillName, String actualStatus, 
           String condition, String output ) {

       logRuleActivationToFile(phase, problemName, stateName, 
       		modelStatus, modelSkillName, actualSkillName, actualStatus, 
       		condition, output, "", "", "", "", "", "" );
   }

   // phase: all "agenda" rule activations or "test" 
   // 
   private void logRuleActivationToFile(String phase, String problemName, String stateName,
           String modelStatus, String modelSkillName, 
           String actualSkillName, String actualStatus, 
           String condition, String output, 
           String modelSelection, String modelAction, String modelInput, 
           String actualSelection, String actualAction, String actualInput) {

       String flagPrediction = null;
       String stepPrediction = null;
       boolean isModelTraced= 
       	isStepModelTraced(modelSelection, modelAction, modelInput, actualSelection, actualAction, actualInput);
       if (EdgeData.CORRECT_ACTION.equals(modelStatus)) {
       	if (EdgeData.CORRECT_ACTION.equals(actualStatus)) {
       		flagPrediction = TRUE_POSITIVE;
       		stepPrediction = isModelTraced ? TRUE_POSITIVE : FALSE_POSITIVE;
       	} else {
       		flagPrediction = FALSE_NEGATIVE;
       		stepPrediction = FALSE_NEGATIVE;
       	}
       } else {
       	if (EdgeData.CLT_ERROR_ACTION.equals(actualStatus)) {
       		flagPrediction = TRUE_NEGATIVE;
       		stepPrediction = isModelTraced ? TRUE_NEGATIVE : FALSE_NEGATIVE;
       	} else {
       		flagPrediction = FALSE_POSITIVE;
       		stepPrediction = FALSE_POSITIVE;
       	}
       }

       logRuleActivationToFile(phase, problemName, stateName, modelStatus, modelSkillName,
       		actualSkillName, actualStatus, flagPrediction, stepPrediction, 
       		condition, output, 
       		modelSelection, modelAction, modelInput, actualSelection, actualAction, actualInput);
   }

   // flagPrediction only cares if the step was performed correctly / incorrectly
   // stepPrediction also cares how the step was performed.  Thus, when a step was perforemed
   // correctly but in a different way, it gets "FalsePositive"
   // 
   private void logRuleActivationToFile(String phase, String problemName, String stateName, 
   		String modelStatus, String modelSkillName, 
   		String actualSkillName, String actualStatus,
   		String flagPrediction, String stepPrediction, 
   		String condition, String output, 
   		String modelSelection, String modelAction, String modelInput, 
   		String actualSelection, String actualAction, String actualInput) {
   	
   	String format = "MM.dd.yyyy-kk.mm.ss";
   	SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
       String dateTime = dateFormat.format( new Date() );

       int andIndex = actualSkillName.indexOf('&');
       if (andIndex > 0) {
       	actualSkillName = actualSkillName.substring(0, andIndex);
       }

       try {
           File file = new File( output );
           if (!file.exists()) {
           	logRuleActivationComposeHeader(file);
           }
           // Open a log file as an append mode
           FileOutputStream fOut = new FileOutputStream( file, true );
           PrintWriter log = new PrintWriter( fOut );

           // String skillName = (String)skillNames.get(0);
           int numProblem = getNumTrained();
           int freq = getRuleFreq( modelSkillName );
           int numRules = numRules();
           String numSteps = "" + numAllInstructions();

           //should split into two parts, with last '.'
           String name = new File(problemName).getName();
           String problemID = name.lastIndexOf('.') > 0 ? name.substring(0,name.lastIndexOf('.')) : name;

           // problemStep is updated by inquiryRuleActivation Oracle
           String step = getProblemStep();
           if (modelSkillName.indexOf("typein") > 0 && getLastSkillOperand() != null) {
           	step += " (" + getLastSkillOperand() + ")";
           }

           // String successFlag = (mtStatus == null ? "0" : (mtStatus.equals(EdgeData.SUCCESS) ? "1" : "0"));
           // int numSuccess = getNumSuccess( edgeSkillName );
           // int numAttempt = getNumAttempt( edgeSkillName );
           // double ratio = ( numAttempt != 0 ) ? ((double)numSuccess)/((double)numAttempt) : 0;
           // String rStr = String.valueOf( ratio );
           // String ruleLearned = getRuleLearned(edgeSkillName);

           /*
           String model = modelStatus.equals(EdgeData.SUCCESS) ? 
                   EdgeData.SUCCESS : 
                       (modelStatus.equals("N/A") ? "N/A" : "ERROR");
            */

           
           String studentID=this.getUserID();
           String cond=getSsCondition();
     
           
           String logStr = trainingCycle+ "\t" + studentID + "\t" + cond + "\t"+ dateTime + "\t" +	   
          // String logStr = dateTime + "\t" +
           problemID + "\t" +
           "\"" + stateName + "\"\t" +
           phase + "\t" +
           "\"" + step + "\"\t" + 
           modelSkillName + "\t" + 
           actualSkillName + "\t" + 
           actualStatus + "\t" +
           modelSelection + "\t" + 
           modelAction + "\t" + 
           "\"" + modelInput + "\"\t" + 
           actualSelection + "\t" + 
           actualAction + "\t" + 
           "\"" + actualInput + "\"";

           log.println( logStr );
           log.close();
           fOut.close();

           
			
           
           
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

	
   private void logRuleActivationComposeHeader(File file) {
   	try {
			FileOutputStream fOut = new FileOutputStream(file);
			PrintWriter out = new PrintWriter(fOut);
			out.println(logHeader);
			out.close();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
           logger.simStLogException(e);
		} catch (IOException e) {
			e.printStackTrace();
           logger.simStLogException(e);
		}
	}

   /**
    * Change a name of the instruction with the specified problem
    * node.  Called when a skill name of the edge (in BR) that goes
    * into the specified Problem Node
    *
    * @param name a <code>String</code> value
    * @param node a <code>ProblemNode</code> value
    **/
   public boolean changeInstructionName( String name, ProblemNode node ) {

	  // Thread.dumpStack();
	   
	   
       // Tue Oct 31 14:02:14 LMT 2006 :: Noboru
       // A mindless patch to improve a speed in Wilkinsburg study
       // We don't need to model <skill>-auto-typein
       // 
       // Implement a command-line argument "ssSkipAutoTypein"
       //
       // if (name.indexOf("auto-typein") > 0) return;

       // Sat May 10 10:52:00 2008 :: Noboru
       // Ad-hoc, but for Algebra I Wilkinsburg study.  
       // We need to normalize "*-auto-typein" to "*-typein"
       name = name.replaceAll("-auto-", "-");

       List incomingEdges = node.getIncomingEdges();
       ProblemEdge edge = (ProblemEdge)incomingEdges.get(0);
       EdgeData edgeData = edge.getEdgeData();
       Vector /* String */ skillNames = edgeData.getSkills();
       String oldSkillName = (String)skillNames.get(0);

       Instruction instruction = lookupInstructionWithNode( node );
    
       //11Oct2007: since stepDemonstrated() didn't prepare the focusOfAttention
       //and possibleFoas for this instruction, we need to do that here.
       if (isFoaSearch()){
           prepareFoas(name, edge, instruction);
       }

       return regenerateRules(name, oldSkillName, node, instruction);
   }

   String reasonOfDisjunct="";
   
   public boolean regenerateRules( String name, String oldSkillName, ProblemNode node, Instruction instruction ) {
       // Regenerate rules
       // 
       // Fri Oct  6 10:58:29 LDT 2006 :: Noboru
       // The search might be failed because the skill name specified is not appropriate
       // (Happened in Algebra I study).  It might be wise to set a limitation on time and 
       // if the search gotten timed out, then suggest the author to rename 
       // (or generate an alternative name when running in batch mode).  
       // This can be considered as learning "disjunctive" rules, because what demonstrated
       // is ineed a disjunctive skill
       //
	   
	   if (this.isSsMetaTutorMode())
		   getModelTraceWM().setSimStudentThinking(WorkingMemoryConstants.TRUE);
   		
	   long ruleStartTime = Calendar.getInstance().getTimeInMillis();
   	
       // First attempt with the name originally specified
       boolean ruleLearned = changeInstructionNameFor(name, oldSkillName, node, instruction);
       
       boolean timeOutOnFirstAttempt=isSearchTimeOut();	//boolean to hold if during first attemt a timeout occured (or search space was exhausted)
       
       if(trace.getDebugCode("miss"))trace.out("miss", "changeInstructionName: first attempt on " + name + " -> " + ruleLearned);

       // Second attempt with the previously renamed disjuncts
       if (!ruleLearned) {
    	 
           Vector /* String */<String> disjuncts = getDisjunctiveSkillNamesFor(name);

           if (disjuncts != null) {
               int numDisjuncts = disjuncts.size();
               long disjunctStartTime = Calendar.getInstance().getTimeInMillis();
              	
               for (int i = 0; !ruleLearned && i < numDisjuncts; i++) {
               	
                   String disjunctiveName = disjuncts.get(i);

                   if(trace.getDebugCode("miss"))trace.out("miss", "regenrateRules");
                   //12Dec2006: for bootstrapping, we need to enforce the rule that you only try to learn if the #FOAs matches.
                   Vector<Instruction> instructions = getInstructionsFor(disjunctiveName);
                   int numInst = instructions.size();

                   if(numInst == 0)
                   	continue;

                   Instruction prevInst = instructions.get(numInst-1); //last in instructions
                   int currFoaSize = instruction.getFocusOfAttention().size();
                   int prevFoaSize = prevInst.getFocusOfAttention().size();                    

                   if (currFoaSize==prevFoaSize){
                   	if(trace.getDebugCode("miss"))trace.out("miss", "Attempting to fit existing disjuncts for " + name);
                       ruleLearned = changeInstructionNameFor(disjunctiveName, name, node, instruction);
                       if(trace.getDebugCode("miss"))trace.out("miss", "changeInstructionName: second attempt on " + disjunctiveName + " -> " + ruleLearned);
                   }

                   //This is unnecessary
                   if (ruleLearned) {
                	   //code added to store when a disjucnt is learned and if disjunct learning was acceptable or normal
                	   int timeToLearnDisjunct = (int) ((Calendar.getInstance().getTimeInMillis() - disjunctStartTime)/1000);
                	   String reasonForCreatingDisjunct=timeOutOnFirstAttempt? SimStLogger.ACCEPTABLE_DISJUNCT : SimStLogger.NORMAL_DISJUNCT;
                	   logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.DISJUNCT_LEARNING, getProblemStepString(), reasonForCreatingDisjunct,"", null, timeToLearnDisjunct);
                	  
                	   
                       addDisjunctiveSkillName(name, disjunctiveName);
                   	
                   }
               }
           }
           if(trace.getDebugCode("miss"))trace.out("miss", "changeInstructionName: second attempt on " + name + " -> " + ruleLearned);
       }

       
       boolean timeOutOnSecondAttempt=isSearchTimeOut();	//boolean to hold if during second attemt a timeout occured (or search space was exhausted) 
       
       // Third attempt with a new name specified by the author
       if (!ruleLearned) {	
       	if(trace.getDebugCode("miss"))trace.out("miss", "Must invent a new disjunct for " + name);
 	  
           String disjunctiveName = inquiryDisjunctiveSkillName(name);

           long disjunctStartTime = Calendar.getInstance().getTimeInMillis();
           
           if(trace.getDebugCode("miss"))trace.out("miss", "Attempting to name a new disjunct...");
           
           ruleLearned = changeInstructionNameFor(disjunctiveName, name, node, instruction);
           if (ruleLearned) {
        	   //code added to store when a disjucnt is learned and if disjunct learning was acceptable or normal
        	   int timeToLearnDisjunct = (int) ((Calendar.getInstance().getTimeInMillis() - disjunctStartTime)/1000);
        	   String reasonForCreatingDisjunct=timeOutOnFirstAttempt? SimStLogger.ACCEPTABLE_DISJUNCT : SimStLogger.NORMAL_DISJUNCT;
        	   logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.DISJUNCT_LEARNING, getProblemStepString(), reasonForCreatingDisjunct,"", null, timeToLearnDisjunct);
        	   
               addDisjunctiveSkillName(name, disjunctiveName);
           }
           else
           {
           	//No rule was found for the instruction - file it away with suffix -norule
           	// Rename the skill name 
               instruction.setName( name+"-norule" );
               // Relocate the instruction for now it has different name
               sortInstruction( instruction );
           }
           if(trace.getDebugCode("miss"))trace.out("miss", "changeInstructionName: third attempt on " + disjunctiveName + " -> " + ruleLearned);
       }

       if (ruleLearned) {
           // Display the updated list of skill names
           updateConsoleSkillName();
           // Increment a frequence of learning
           incRuleFreq( name );

           //ToDo: change this to show disjunctiveName also (or instead)
           if(trace.getDebugCode("miss"))trace.out( "miss", name + " has been learned " + getRuleFreq( name ) + " times" );

           int ruleLearnDuration = (int) ((Calendar.getInstance().getTimeInMillis() - ruleStartTime)/1000);
	        
           //if(isSsMetaTutorMode()){
           //	getModelTraceWM().getEventHistory().add(0, getModelTraceWM().new Event(SimStLogger.RULE_LEARN_PERFORMANCE_ACTION));
           //}
           logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.RULE_LEARN_PERFORMANCE_ACTION, getProblemStepString(), 
           		name + " has been learned " + getRuleFreq( name ) + " times", "", ruleLearnDuration);
           if (SimStInteractiveLearning.isWaitingForRuleLearned){
               SimStInteractiveLearning.hereIsTheRule("signal: a rule has been learned!");
           }
           if(getMissController().getSimStPLE() != null && getMissController().getSimStPLE().isConfused())
           {
           	getMissController().getSimStPLE().setAvatarConfused(false);
           }
       }

       clearCurrentFoA();
       
       if (this.isSsMetaTutorMode())
		   getModelTraceWM().setSimStudentThinking(WorkingMemoryConstants.FALSE);
       
       
       return ruleLearned;

   }

   transient HashMap skillArityHash = new HashMap();

   private void prepareFoas(String skillName, ProblemEdge edge, Instruction instruction) {
       //candidate WMEs are the non-empty WMEs except for the last one
       List<String> candidateWMEs = getNonEmptyWMEs();
       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "prepareFoas: candidateWMEs = " + candidateWMEs);
       String selection = edge.getSelection();
       candidateWMEs.remove(selection); //the input can't be in the true FoA

       //set the arity of the skill
       Integer arity = (Integer) skillArityHash.get(skillName); //try to look it up
       if (arity==null){ //if this is the first instruction with this skillname
           arity = candidateWMEs.size(); //figure it out
           skillArityHash.put(skillName,arity);
       }

       //initialize the instruction's possibleFoas, and set to the first one
       try{
           Enumeration possibleFoas = new Combinations( candidateWMEs.toArray(), arity );
           List<Vector<Object>> possibleFoasList = makePossibleFoasList(possibleFoas, instruction);
           if(trace.getDebugCode("foasearch"))trace.out("foasearch", "prepareFoas: arity = " + arity + ", possibleFoasList.size() = " + possibleFoasList.size());
           if(trace.getDebugCode("foasearch"))trace.out("foasearch", "prepareFoas: possibleFoas.get(0) = " + possibleFoasList.get(0));

           instruction.setPossibleFoas(possibleFoasList);

           //initialize the focusOfAttention to the first possible one
           instruction.setToFirstPossibleFoa();

       }
       catch(Exception e){
       	e.printStackTrace();
           logger.simStLogException(e);
      	}
   }

   public void updateSimStWorkingMemoryDirectly(String selection, String action, String input){
	   this.getSsRete().setSAIDirectly(selection, action, input);  
   }
   
   
   
   /**
    * converts an Enumeration of arrays into a list of lists
    * @param en
    * @param instruction 
    * @return
    */
   private List<Vector<Object>> makePossibleFoasList(Enumeration en, Instruction instruction) {
       List<Vector<Object>> l = new Vector<Vector<Object>>();
       while(en.hasMoreElements()){ //for each possible FOA
           Object[] wmeArray = (Object[])en.nextElement();            
           Vector<Object> wmeV = arrayToVector( wmeArray );

           clearCurrentFoA();

           //for each element of wmeV, i.e., each WME, make it use the correct seed format, given by foaString()
           for (int i=0; i<wmeV.size(); i++){
               String selection = (String) wmeV.get(i);
               String wmeType = getRete().wmeType( selection );

               //all for the purpose of getting foaString()
               TableExpressionCell cell = (TableExpressionCell)getBrController().lookupWidgetByName( selection );
               FoA foa = new FoA(cell);
               addFoA(foa);
               String foaStr = getCurrentFoA().get(i).foaString();

               //fix it
               wmeV.set(i, foaStr);

               // idea: look it up from WMEs
//                Fact wmeFact = rete.getFactByName(selection); //gets the WME fact for the given selection
//                try {
//                    Value inputValue = wmeFact.getSlotValue("value");
//                    String input = SimSt.stripQuotes(inputValue.toString());
//                }


           }

           String instrSelection = instruction.getSelection().split("\\|")[1];

           //all for the purpose of getting foaString()
           TableExpressionCell inputCell = (TableExpressionCell)getBrController().lookupWidgetByName( instrSelection );
           FoA inputFoa = new FoA(inputCell);
           addFoA(inputFoa);
           String inputFoaStr = getCurrentFoA().get(this.currentFoA.size()-1).foaString();

           //there should be a function to return inputFoaStr, given selection
           wmeV.add(0, inputFoaStr); //previously "stub"



           l.add(wmeV);
       }
       return l;
   }

   /** 16 Oct 2007. This was copied from RhsSearchSuccessorFn.
    * 
    * @param array
    * @return
    */
   private Vector<Object> arrayToVector( Object[] array ) {

       Vector<Object> v = new Vector<Object>();
       for (int i = 0; i < array.length; i++) {
           v.add( array[i] );
       }
       return v;
   }

   
   /**
    * Asks for hint from the designated oracle. The designated hint oracle is specified in the
    * command line argument when the SimStudent is launched
    * @return hint
    */
   public AskHint askForHint(BR_Controller controller, ProblemNode currentNode) {
   	
   	//if(getBrController().getMissController().getSimStPLE() != null)
   		//getBrController().getMissController().getSimStPLE().setFocusTab(SimStPLE.SIM_ST_TAB);
   	
   	AskHint hint = null;
   	String hintMethodName = getHintMethod();

   	if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_BRD))
   		hint = new AskHintBrd(controller, currentNode);
   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_HD))
   		hint = new AskHintHumanOracle(controller, currentNode);
   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_FTS))
   		hint = new AskHintTutoringServiceFake(controller, currentNode);
   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_CL))
   		hint = new AskHintClAlgebraTutor(controller, currentNode);
   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_FAKE_CLT))
   		hint = new AskHintFakeClAlgebraTutor(controller, currentNode);
   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_SOLVER_TUTOR))
   		hint = new AskHintInBuiltClAlgebraTutor(controller, currentNode);
  	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_JESS_ORACLE))
   		//hint = new AskHintJessOracle(controller,currentNode, controller.getProblemName().replace("_", "="));
   		hint = new AskHintJessOracle(controller,currentNode);
  	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_WEBAUTHORING))
   		hint = new AskHintWebAuthoring(controller, currentNode);
  		//hint=this.getWebAuthoringBackend().askForHintWebAuthoring(controller,currentNode);
   	else 
   		new Exception("No valid hint method was specified!").printStackTrace();
   	
   	
   	if(trace.getDebugCode("nbarbaBrd")) trace.out("nbarbaBrd","### SimStudent is stuck, hint from " + hintMethodName + " is " + hint.getSai());
   	
   	
   	return hint;
   }

   transient Sai saiCache=null;
   public void setSaiCache(Sai sai){	   
	   this.saiCache=sai; 
	   }
   public Sai getSaiCache() { return saiCache;}
   
   transient public String storageClientURL="";
   public String getStorageClientURL(){return this.storageClientURL;}
   public void setStorageClientURL(String url){this.storageClientURL=url;}
   
   /**
    * Asks for hint from the designated Quiz oracle, which is specified by command line arqument -ssQuizGradingMethod.
    * The designated hint oracle is specified in the
    * @return hint
  */
   public AskHint askForHintQuizGradingOracle(BR_Controller controller, ProblemNode currentNode) {
	   	
	   

	   	AskHint hint = null;
	   	String hintMethodName = getQuizGradingMethod();
	   	
	   	if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_BRD))
	   		hint = new AskHintBrd(controller, currentNode);
	   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_HD))
	   		hint = new AskHintHumanOracle(controller, currentNode);
	   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_FTS))
	   		hint = new AskHintTutoringServiceFake(controller, currentNode);
	   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_CL))
	   		hint = new AskHintClAlgebraTutor(controller, currentNode);
	   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_FAKE_CLT))
	   		hint = new AskHintFakeClAlgebraTutor(controller, currentNode);
	   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_SOLVER_TUTOR))
	   		hint = new AskHintInBuiltClAlgebraTutor(controller, currentNode);
	   	else if(hintMethodName.equalsIgnoreCase(AskHint.HINT_METHOD_JESS_ORACLE))
	   		//hint = new AskHintJessOracle(controller,currentNode, controller.getProblemName().replace("_", "="));
	   		hint = new AskHintJessOracle(controller,currentNode);
	   	else 
	   		new Exception("No valid hint method was specified!").printStackTrace();
	   	
	   	return hint;
	   }
   
   
   /**
    * called when the first instruction for a given skill is demonstrated
    * count the number of non-empty WMEs and subtract 1
    * @return
    */
   private List<String> getNonEmptyWMEs() {

       JessModelTracing jmt = getBrController().getModelTracer().getModelTracing();
       List facts = jmt.getRete().getFacts();
       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "facts = " + facts);

       List<String> nonEmptyWmes=new Vector<String>();

//      try{
       for (int i=0; i<facts.size(); i++){
           Fact fact = (Fact) facts.get(i);
           if(trace.getDebugCode("foasearch"))trace.out("foasearch", "fact = " + fact);
           try{
               Value val = fact.getSlotValue("value");
               String value = (val==null) ? null : val.toString();
               if(trace.getDebugCode("foasearch"))trace.out("foasearch", "value = " + value);
               if (!"nil".equals(value)){
                   String selection = fact.getSlotValue("name").toString();
                   if(trace.getDebugCode("foasearch"))trace.out("foasearch", "selection = " + selection);
                   nonEmptyWmes.add(selection);
               }
           }
           catch(Exception e){
               trace.out("foasearch", "couldn't get a value for fact: " + fact);
               logger.simStLogException(e,"couldn't get a value for fact: " + fact);

//                e.printStackTrace();
           }
       }
//        }
//        catch(Exception e){
//        }

       return nonEmptyWmes;
   }



   public /*private*/ String inquiryDisjunctiveSkillName(String name) {
       // Fri Oct  6 12:46:25 LDT 2006 :: Noboru
       // Must prompt the author to enter a new name when running in interactive mode 
       Vector<String> disjuncts = getDisjunctiveSkillNamesFor(name);
       int id = (disjuncts == null) ? 1 : disjuncts.size()+1; 
       return name + "-" + id; 
   }

   public /*private*/ void addDisjunctiveSkillName(String name, String disjunctiveName) {
       Vector<String> disjuncts = getDisjunctiveSkillNamesFor(name);
       if (disjuncts == null) {
           disjuncts = new Vector<String>();
           setDisjunctiveSkillNamesFor(name, disjuncts);
       }
       disjuncts.add(disjunctiveName);
   }

   // Contains a vector of String listing disjunctive skill names for a skill name 
   // specified by a key 
   private edu.cmu.pact.miss.HashMap disjunctiveSkillNames = new edu.cmu.pact.miss.HashMap();
   public /*private*/ Vector<String> getDisjunctiveSkillNamesFor(String name) {
   	if(disjunctiveSkillNames == null)
   		disjunctiveSkillNames = new edu.cmu.pact.miss.HashMap();
       return (Vector<String>)disjunctiveSkillNames.get(name);
   }
   private void setDisjunctiveSkillNamesFor(String name, Vector<String> disjuncts) {
       disjunctiveSkillNames.put(name, disjuncts);
   }
   
   /**
    * Method to calculate disjunctiveSkillNames from the ruleNames. 
    */
   public void generateDisjunctiveSkillNames(){
   	Set<String> nameSet=getRuleNames();    //get the rule names and find out if we have disjunctive rules	    	
   	for (String skillName : nameSet) {
   		if(skillName.matches(".*-\\d*$")){
           	String baseName = skillName.substring(0, skillName.lastIndexOf("-"));
           	addDisjunctiveSkillName(baseName, skillName);
           }
   	}
   	 
   }
   
   public /*private*/ boolean changeInstructionNameFor(String name, String oldSkillName, 
           ProblemNode node, Instruction instruction) {

	   
       if(trace.getDebugCode("rr"))
           trace.out("rr", "Changing instruction name from " + oldSkillName + " to " + name);

     
       
       // Rename the skill name
       instruction.setName( name );
       // Relocate the instruction for now it has different name
       sortInstruction( instruction );
       // Attempt to learn the rule
       resetSimStTimer();

  	 
       boolean ruleLearned = generateUpdateProductionRules( name );

       return ruleLearned;
   }

   // Update a list of skill names
   public /*private*/ void updateConsoleSkillName() {
       if ( isMissControllerSet() ) {
           Vector /* String */<String> skillNames = getAllSkillNames();
           getMissController().updateConsoleSkillNameList( skillNames );
       }
   }

   // Update a number of steps demonstrated on the Miss Console
   private void consoleDisplayNumInstructions() {
       if ( isMissControllerSet() ) {
           getMissController().setNumStepDemonstrated( getNumInstructions() );
       }
   }

   // ----------------------------------------------------------------------
   // Generate rules
   // 

   // private int oldNumRules = 0;
   // private int oldNumSteps = 0;

   /**
    * set whether SimStudent should actually create a production rule file, 
    * if not, FOIL and the searches are still run.
    * Setting this to false is only useful for testing purpose
    * @param flag 
    */
   private boolean archivingProductionRules = true; 
   public /*private*/ boolean isArchivingProductionRules() { return archivingProductionRules; }
   public void setArchivingProductionRules(boolean flag) { archivingProductionRules=flag; }

   private boolean archivingPRonStepBasis = true;
   private boolean isArchivingPRonStepBasis() { return archivingPRonStepBasis; }
   public void setArchivingPRonStepBasis(boolean flag) { this.archivingPRonStepBasis = flag; }

   private boolean archivingPRonProblemBasis = true;
   private boolean isArchivingPRonProblemBasis() { return archivingPRonProblemBasis; }
   public void setArchivingPRonProblemBasis(boolean flag) { this.archivingPRonProblemBasis = flag; }

   private static final int SAVE_PR_PROBLEM_BASE = 1;
   static final int SAVE_PR_STEP_BASE = 2;

   // Generate(or update) production rules not only for the give skillName, but 
   // also update all other skills learned so far.  This is so because
   // the instance of demonstration for skillName serves as a negative
   // example for all other skills.  
   // Filly, save production rules into a file so that the changes in production rules
   // take effect for the following problem solving steps
   private boolean generateUpdateProductionRules( String skillName ) {

   	if(trace.getDebugCode("miss"))trace.out("miss", "Inside generateProductionRules: " + skillName);
   	
       boolean ruleLearned;

       // display a number of rules generated
       if ( isMissControllerSet() ) {
           getMissController().consoleSetNumProductionRules( numRules() );
       }

       // Keep the current production rules with 
       // information on # rules and steps demonstrated
       // Thu Jun 15 17:30:28 2006 Noboru - saveProductionRules() does this automatically 
       // archiveProductionRules();

       // Generate rules
       //
       // Fri Mar 31 17:38:07 2006: This method assumes that Focus of Attention
       // is given in ordered.  We no longer hold this constraint
       // 
       // generateRules( skillName );
       // 
       // Improved version of rule generation / refinement method. 
       // Implicit mapping among unordered focus of attention is 
       // subject to search
       
      
       ruleLearned = generateRulesWithUnorderdFoA( skillName );


       if (ruleLearned){
           // Write rules into a file on the step basis
           if(isArchivingProductionRules() && isArchivingPRonStepBasis()) {
               saveProductionRules(SAVE_PR_STEP_BASE);
           }
       }

       // Do the serialization here
       //printInfo(this);
       //saveSimStState();
       return ruleLearned;
   }

  
   
   // saveMode tells is this archiving is for a training problem basis or a step basis
   // Traininig basis: save rules after trained on a whole single problem
   // Step basis: save rules after learing a skill on a step
   public void saveProductionRules(int saveMode) {

       // Output to a regular productionRules.pr file
   	File prFile = null;
   	
   	prFile = new File(getProjectDir(), getPrFileName());
   	
   	// Check if the application is running locally or via webstart
   	if(isWebStartMode()) {
   		prFile = new File(WebStartFileDownloader.SimStWebStartDir + getPrFileName() );
       }

  	if (isSsWebAuthoringMode()){
   		prFile = new File(getProjectDirectory()+"/"+ getPrFileName() );
   	}
    
   	  //File prFile = new File( getProjectDir(), getPrFileName() );
       FileOutputStream prFileOutputStream = null;

       // Output to an archived file
       // The aged production rule file with # rules and # step
       // performed
       //String ageFileName = getPrFileName() + "-";

       String ageFileName = null;
		ageFileName = getPrFileName() + "-";
       if (saveMode == SAVE_PR_PROBLEM_BASE) {
           String numP = prettyGetNumTrained();
           ageFileName += "P" + numP;
       }
       String numR = prettyNumRules();
       String numS = prettyNumAllInstructions();
       ageFileName += "R" + numR + "S" + numS;
       String fullAgePath = getLogDirectory() + WebStartFileDownloader.separator + getUserID() + "-" + getPrAgeDir();
       
       
       File ageFile = new File( fullAgePath, ageFileName ).getAbsoluteFile();
       if(isWebStartMode()) {
       	fullAgePath = WebStartFileDownloader.SimStWebStartDir + getPrAgeDir() + "_" + getUserID() + "_" + FileZipper.formattedDate();
       	ageFile = new File(fullAgePath + WebStartFileDownloader.separator + ageFileName);
       }
       
      if (isSsWebAuthoringMode()){
    	    fullAgePath = getUserBundleDirectory() +"/" + getPrAgeDir();
           	ageFile = new File(fullAgePath + "/" + ageFileName);
      	}

       boolean userFile = false;
       String userProdRuleFile = "";
       File prFileUser = null;
       FileOutputStream prFileUserStream = null;
       if(getMissController().isPLEon())
       {
       	userFile = true;
       	String id = "default";
       	if(getUserID() != null && getUserID().length() > 0)
       		id = getUserID();
       	userProdRuleFile = USER_PRODUCTION_RULE_FILE.replace("$", id);

//       	prFileUser = new File(getProjectDir(), userProdRuleFile);
       	prFileUser = new File(getLogDirectory(), userProdRuleFile);
       	// Check if the application is running locally or via webstart
       	if(isWebStartMode()) {
       		prFileUser = new File(WebStartFileDownloader.SimStWebStartDir + userProdRuleFile );
           }
       }

    
       
		FileOutputStream ageFileOutputStream = null; 

       File fullAgePathFile = new File( fullAgePath ).getAbsoluteFile();
       if ( !fullAgePathFile.exists() ) {
       	if(trace.getDebugCode("miss"))trace.out( "miss", "Directory " + fullAgePath + " created." );
           fullAgePathFile.mkdirs();
       }
       if (ageFile.exists()) {
           ageFile.delete();
       }

       try {
           prFileOutputStream = new FileOutputStream( prFile );
           ageFileOutputStream = new FileOutputStream( ageFile );
           if(userFile) prFileUserStream = new FileOutputStream(prFileUser);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       PrintStream prPrintStream = new PrintStream( prFileOutputStream );
       PrintStream agePrintStream = new PrintStream( ageFileOutputStream );
       PrintStream prUserPrintStream = null;
       if(userFile) prUserPrintStream = new PrintStream(prFileUserStream);

      
		
       printRules( prPrintStream );
       printRules( agePrintStream );
       if(userFile) printRules(prUserPrintStream);


		
		
       /** Save the files on the server as well. Key to store prodRules is productionRules-userID.pr */ 
       if(SimSt.WEBSTARTENABLED) {
       	try {
				getMissController().getStorageClient().storeFile("productionRules-"+getUserID()+".pr", prFileUser.getCanonicalPath());
       	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }

       prPrintStream.close();
       agePrintStream.close();
       if(userFile) prUserPrintStream.close();
       
       
       try {
           prFileOutputStream.close();
           ageFileOutputStream.close();
           if(userFile) prFileUserStream.close();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

		
		
   }

   /**
    * Given a newly demonstrated skillName, refine (or geneate) production rules
    * 
    * @param skillName
    */
   public boolean generateRulesWithUnorderdFoA(String skillName) {

       boolean ruleLearned;

       // Generate / update production rules only when the skillName is specified
       // (meaning, not when a step if just demonstrated w/o labeling the edge)
       if (skillName.equals( Rule.NONAME ) ) {
           trace.err("SimSt.generateRulesWithUnorderdFoA() gotten NONAME");
           ruleLearned = false;
       }
       else {
           // Generate / update a rule for the step just demonstrated
           ruleLearned = generateRuleFor(skillName);
	        
           // Update LHS conditionals (FOIL part) across all productions
           if (ruleLearned) {
               updateLhsConditions(skillName);
           }
       }
       return ruleLearned;
   }

   /**
    * Update input file for FOIL given a demonstration on a specific step, and
    * run FOIL across all rules accumulated so far.
    * 
    * @param skillName
    */
   public /*private*/ void updateLhsConditions(String skillName) {

       // Initialize FOIL data: make sure there is a FoilData for the target
       // production rule, and make the one if not
       // // // initializeFoilDataFor(skillName);

       // Propagate negative examples AND
       // Invoke FOIL for the LHS feature extraction
       // 
       // Sat Jun 17 16:04:47 2006 Noboru
       // When Memory Window is set, we need to iterate all skills demonstrated so far, 
       // because each time a rule is learned (initializeFoilData() is called), the FOIL data gets reset
       if (isMemoryWindowSizeSet()) {
           arrangeFoilData();
           charmFoil(getFoilData(skillName));
       } else {
           // Add a most recent instruction for a a positive example for "skillName" and
           arrangeFoilData(skillName);
           charmFoilAcrossRules();
       }
   }

   // ----------------------------------------------------------------------
   // Invoke FOIL
   //

   //for each skill, call arrangeFoilData() on it
   private void arrangeFoilData() {

       Vector /* String */<String> allSkills = getAllSkillNames();
       for (int i = 0; i < allSkills.size(); i++) {
           String skillName = allSkills.get(i);
           if (!skillName.equals(Rule.NONAME)) {
               arrangeFoilData( skillName );
           }
       }
   }

   //call arrageFoilData() for the set of instructions that have this skillname
   private void arrangeFoilData( String skillName ) {
       Vector /* Instructions */<Instruction> instructions = getInstructionsFor(skillName);
       if (instructions != null && !instructions.isEmpty()) {
           arrangeFoilData(instructions);
       }
   }

   // extracts the rule name from 'instructions'. (there is one FoilData for each skillname, see the hashtable)
   // 'foilData' <- getFoilData(skillname), i.e. the FoilData for this skillname
   // for each instruction, we add it to 'foilData' and for all other FoilData's (i.e. other skillnames).
   // for each FoilData 'recipient' that is not the same as 'foilData' (i.e. the FoilDatas for other skillnames), 
   //                   call signalTargetNegative()
   //
   private void arrangeFoilData( Vector /* Instruction */<Instruction> instructions ) {

       Instruction instruction = instructions.get(0);
       String name = instruction.getName();
       int arity = instruction.numFocusOfAttention() -1;
       FoilData foilData = initializeFoilDataFor( name );
       
       for (int i = 0; i < instructions.size(); i++) { //for each instruction

           instruction = instructions.get(i);
           foilData.addInstruction( instruction );
           Enumeration<FoilData> allFoilData = getAllFoilData();

           //call signalTargetNegative(instruction) for all FoilData's corresponding to other skillnames
           while ( allFoilData.hasMoreElements() ) {

               FoilData recipient = allFoilData.nextElement();
               if ( recipient != foilData && instruction.getFoilArity() == recipient.getFoilArity()) { 
                   recipient.signalTargetNegative( instruction ); 
               }
           }
       }
   }

   // Run FOIL for all production rules
   private void charmFoilAcrossRules() {

       // Induce LHS features with FOIL for each rule
       Enumeration<FoilData> allFoilData = getAllFoilData();
       while ( allFoilData.hasMoreElements() ) {
           // For each of the rule, ...
           FoilData foilData = allFoilData.nextElement();
           charmFoil(foilData);
       }
   }

   /**
    * @param foilData
    * 
    * searches foilData for features, saves the FOIL input file,
    * and updates the rule with them.
    */
   public void charmFoil(FoilData foilData) {

       String name = foilData.getTargetName();
       //11 May 2007 : otherwise rule gets null, when this method is called from interactive learning
       //name = name.replaceAll("MAIN::", ""); 
       Rule rule = getRule( name );

       // If the remaining part of the rule has been generated, then ...
       if ( rule != null ) {
           // Search for LHS features, and ...
           //long startTime = (new Date()).getTime();
           String numR = prettyNumRules();
           String numS = prettyNumAllInstructions();
           Vector /* V of V of St */ features = foilData.searchFeatures(numR, numS);
           // *** just for debug ************************************
           // Vector /* String */ features = null; // Turn off FOIL
           // *** just for debug ************************************
           //long endTime = (new Date()).getTime();
           //String msg = "FOIL for " + name + " done in " + (endTime - startTime) + "ms.";

           // If the search was successful, then assert the feature 
           if ( features != null) {
               rule.setFeatures( features );
           }
       }
   }

   // Make a new FoilData for the target production rule called "name"
   public FoilData initializeFoilDataFor(String name) {

       FoilData foilData = null;

       // When a memory window is set, then delete a cached FoilData
       if (isMemoryWindowSizeSet()) {
       	
           foilDataHash.remove(name);
       }
       // Create a new foilData for the specified skill when it's not there yet
       // ((foilData = getFoilData(name)) ==  null)
       if ((foilData = foilDataHash.get(name)) == null) {
           Vector /* Instruction */<Instruction> instructions = getInstructionsFor( name );
           if(instructions.size() == 0 && !isMemoryWindowSizeSet())
           {
           	//look for the instruction in the negative examples
           	for(int i=0;i<negativeExamples.size();i++)
           	{
           		if(negativeExamples.get(i).getName().equals(name))
           			instructions.add(negativeExamples.get(i));
           	}
           }
           if(instructions.size() > 0)
           {
	            Instruction instruction = instructions.get(0);
	            int arity = instruction.numFocusOfAttention() -1;
	            
	            if (isSsWebAuthoringMode()) {
	            	foilLogDir = getUserBundleDirectory() + "/" + getFoilLogDir() + "/";
	            } else {
	            	foilLogDir = getProjectDirectory() + "/" + getLogDirectory() + "/" + getUserID() + "-" + getFoilLogDir() + "/";
	            }
	            
	            foilData = new FoilData( name, arity, 
	                    getPredicates(),
	                    getFocusOfAttention(name),
	                    getFeaturePredicateCache(),
	                    foilLogDir , getFoilMaxTuples());
	            foilData.setDecomposers(decomposers);
	            foilDataHash.put( name, foilData );
	            // getFoilData(instruction.getName(),instruction.numFocusOfAttention() -1);
	            if(trace.getDebugCode("miss"))trace.out("miss", "initializeFoilDataFor: FoilData for " + name + " created...");
       	}
           else
           {
           	new Exception("No instructions for "+name).printStackTrace();
           }
       } else {
       	if(trace.getDebugCode("miss"))trace.out("miss", "initializeFoilDataFor: FoilData for " + name + " already exists");
       }

       return foilData;
   }

   
   /* 06/30/2014 nbarba: The lhsPath has always teh values of elements as null (i.e. nil), even if the WME Editor in ctat has the correct values. 
    * This is a fix that takes a wme path entry (note that wme path is a vector of strings), locates the element name, queries SimStRete for the actual value 
    * of that element, corrects the path entry and returns it.
    * */
 
 public  String fixWmePathValues(String path){
   	
   	Pattern  p=Pattern.compile("\\(name .*?\\)",Pattern.DOTALL);
   	Matcher m = p.matcher(path);
   	
   	String foaPattern="";
   	while (m.find()){   		
   		foaPattern=m.group(0);
   		
   	}
    String foaName=foaPattern;
   	
    foaName=foaName.replace("name","");
    foaName=foaName.replace("(","");
	foaName=foaName.replace(")","");
	
   	String actualValue= getSsRete().getFactActualValue(foaName);
   
   	String tmp=foaPattern+" (value nil)";
   	String tmp1=foaPattern+" (value "+actualValue+")";
 
   	String newPath=path.replace(tmp, tmp1);
   	


   	return newPath;
}

 
 
   /**
    * Generate / update a single rule with the given skill name.
    * 
    * @param skillName
    */
   private boolean generateRuleFor(String skillName) {

    	if(trace.getDebugCode("miss"))trace.out("miss", "generateRuleFor: skillName = " + skillName);
       Vector /* Instruction */<Instruction> instructions = getInstructionsFor(skillName);

       
       trace.out("miss","Learning rule for skill " + skillName + " based on instruction " + instructions);
  
       trace.out("miss","Trying to learn a rule, and SsRete facts are " + this.getSsRete().getFacts());
       
       
       	if (skillName.contains("unnamed")){
      		trace.err("UNNAMED!!!! ******** ");
      		return false;
      	}
      
       long startTime, endTime, duration;
       int numOp = 0;
       Vector /* String */ rhsOps = null;

       if (isTimed()) resetSimStTimer();


       //Don't do a RHS for a 'Done' instruction
       boolean isDone = skillName.equalsIgnoreCase(Rule.DONE_NAME);
       if (!isDone) {
           // Searching for RHS operators 
           startTime = (new Date()).getTime();
           if(trace.getDebugCode("miss"))trace.out("miss", "Searching RHS Ops for " + skillName+ "..." );

           // sort the RhsOpList before starting the search for the instruction
           if(isHeuristicBasedIDS())
           	sortHashMapByValuesD(OpFreqCountHashMap);
           
           rhsOps = isFoaSearch() ?
                   searchRhsOpsFor_FoaSearch(instructions) :
                       searchRhsOpsFor(instructions);

           endTime = (new Date()).getTime();
           duration = (endTime - startTime)/1000;
           if (rhsOps!=null) {
               numOp = rhsOps.size();
           }
           if(trace.getDebugCode("miss"))trace.out("miss", "... found " + numOp + " operators in " + duration + "ms.");
           logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.RHS_OPS_SEARCH_PERFORMANCE_ACTION, getProblemStepString(),
           		"RHS Ops Search for " + skillName+ " found " + numOp + " operators","",(int) duration);
       }

       boolean ruleLearned;
       // Fri Oct  6 17:36:36 LDT 2006 :: Noboru
       if (isSearchTimeOut()) {
           ruleLearned = false;
       } else {
           // Searching for LHS WME paths
           startTime = (new Date()).getTime();


           //22Sep2007:
           //if (ssFoaSearch), we try every possibility            
           //     Vector /* String */ lhsPath = searchLhsPathsFoaSearch(instructions);
           //
           // for (foasChoice : ) {
           // 
           //               and instructions
           //}
           //==

           if(trace.getDebugCode("miss"))trace.out("miss", "Searching WME-paths for " + skillName + "...");
           
           Vector /* String */<String> lhsPath = searchLhsPaths(instructions);
           
          
           
           
          if (lhsPath.size()==0){
        	  	trace.err("lhsPath for instruction is empty when searching wme paths for " + skillName + " with instructions " + instructions);
      			new Exception().printStackTrace();
        	  return false;
          }
         //  if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "LHSPath for "+ skillName + " : " + lhsPath);
           
           
           
           /* the lhsPath returned from searchLhsPaths(instructions) has all the values nill.
            * The following for loop iterates the lhsPath and fixes that problem for each lhsPath entry.
            */           
         //  for (int i=0;i<lhsPath.size(); i++){		    
         //		String tmp=lhsPath.get(i); 	
         //		lhsPath.set(i,fixWmePathValues(tmp));		   
          // }

            
            
           endTime = (new Date()).getTime();
           duration = (endTime - startTime)/1000;
           int numPath = lhsPath.size();
           if(trace.getDebugCode("miss"))trace.out("miss", "... found " + numPath + " paths in " + duration + "ms.");

           logger.simStLog(SimStLogger.SIM_STUDENT_LEARNING, SimStLogger.WME_PATH_SEARCH_PERFORMANCE_ACTION, getProblemStepString(),
           		"WME paths Search for " + skillName+ " found " + numPath + " paths","", (int) duration);

           // Make a rule, except feature predicates (FOIL would run later), and 
           // add it to the rulebase
           if ( lhsPath != null && (rhsOps != null || isDone)) {
               String action = instructions.get(0).getAction();
               Vector /* FeaturePredicates */<FeaturePredicate> predicateTestAsFact = (Vector<FeaturePredicate>)predicatesToTestAsFacts.clone();
           
               
               Rule rule = new Rule( skillName, lhsPath, null, predicateTestAsFact, rhsOps, action );
       
               int numFoa=getInstructionsFor(skillName).get(0).numSeeds();
               rule.setNumFoA(numFoa);
       

       
               addRule( rule );
               Vector fullFoas = instructions.get(0).getFocusOfAttention();
               Vector foas = new Vector<String>();
               for(int k=1;k<fullFoas.size();k++)
               {
               	String foa = (String) fullFoas.get(k);
               	if(foa.contains("|"))
               	{
               		foa = foa.substring(foa.indexOf("|")+1);
               		if(foa.contains("|"))
                   	{
                   		foa = foa.substring(0,foa.indexOf("|"));
                   	}
               	}
               	foas.add(foa);
               }
               //if(selection.contains("|")) selection = selection.substring(selection.indexOf("|")+1);
               rule.addAcceptedUse(foas);
               // Increment the count associated with the operators used in the current instruction
               if(isHeuristicBasedIDS())
               	incrementOperatorFrequency(rule.getRhsOp());

               ruleLearned = true;

               endTime = (new Date()).getTime();
               duration = endTime-startTime; 
               if(trace.getDebugCode("miss"))trace.out("miss", "Time to learn rule: " + duration + "ms"); 
               } else {
               ruleLearned = false;
           }
       }
       if(trace.getDebugCode("miss"))trace.out("miss", "Rule Learned: "+ruleLearned );
       
       return ruleLearned;
   }

   private void incrementOperatorFrequency(List rhsOp) {
   	
       for(int i=0; i< rhsOp.size(); i++) {
       	if(trace.getDebugCode("miss"))trace.out("miss","rule.getRhsOp: " +  rhsOp.get(i));
       	int index1 = rhsOp.get(i).toString().indexOf('(');
       	String temp = rhsOp.get(i).toString().substring(index1+1, rhsOp.get(i).toString().length());
       	int index2 = temp.indexOf('(');
       	int index3 = temp.lastIndexOf('?');
       	String jessName = temp.substring(index2+1, index3-1);
       	if(jessName.indexOf('?') != -1) {
       		jessName = jessName.substring(0, jessName.indexOf('?')-1); 
       	}
       	if(trace.getDebugCode("miss"))trace.out("miss", jessName);
       	String absoluteOperatorName = (String) JessToOperatorName.get(jessName);
       	Integer count = (Integer) OpFreqCountHashMap.get(absoluteOperatorName);
       	if(trace.getDebugCode("miss"))trace.out("miss", absoluteOperatorName + " "  +  count.intValue());
       	if(trace.getDebugCode("miss"))trace.out("miss", "Incrementing the count for " + absoluteOperatorName + " to " + (count.intValue()+1));
       	OpFreqCountHashMap.put(absoluteOperatorName, new Integer(count.intValue()+1));
       }

       printOpFreqCountHashMap();
   }

   private transient LinkedHashMap<String, Integer> sortedMap = null;

   public void sortHashMapByValuesD(HashMap passedMap) {
       List mapKeys = new ArrayList(passedMap.keySet());
       List mapValues = new ArrayList(passedMap.values());
       Collections.sort(mapValues);
       Collections.sort(mapKeys);
       Collections.reverse(mapValues);

       sortedMap = new LinkedHashMap<String, Integer>();

       Iterator valueIt = mapValues.iterator();
       while (valueIt.hasNext()) {
           Object val = valueIt.next();
           Iterator keyIt = mapKeys.iterator();

           while (keyIt.hasNext()) {
               Object key = keyIt.next();
               String comp1 = passedMap.get(key).toString();
               String comp2 = val.toString();

               if (comp1.equals(comp2)){
                   passedMap.remove(key);
                   mapKeys.remove(key);
                   sortedMap.put((String)key, (Integer)val);
                   break;
               }

           }

       }

   	OpFreqCountHashMap.clear();
   	rhsOpList.removeAllElements();
   	Set set = sortedMap.entrySet();
   	Iterator i = set.iterator();
   	if(trace.getDebugCode("miss"))trace.out("After sorted: ");
   	while(i.hasNext()) {
   		Map.Entry me = (Map.Entry) i.next();
   		if(trace.getDebugCode("miss"))trace.out("miss", me.getKey() + "    " + me.getValue());
   		addRhsOpList((String) me.getKey());
   		OpFreqCountHashMap.put((String) me.getKey(), (Integer) me.getValue());
   	}
   }

   /*
   private void sortRhsOpList() {
   	
   	Vector<FeaturePredicate> fpVector = new Vector<FeaturePredicate>();
   	HashMap hm = new HashMap();
   	FeaturePredicate fp;
   	int index = 0;
   	
   	Iterator rhsOpIterator = getRhsOpList().iterator();
   	while(rhsOpIterator.hasNext()) {
   		
   		fp = getRhsOp((String)rhsOpIterator.next());
   		hm.put(fp, getRhsOpList().elementAt(index));
   		fpVector.add(fp);
   		++index;
   	}
   	
   	// Added by Rohan.
   	// Sort the RHS Operator list on the basis of frequencyCount.
   	if(isHeuristicBasedIDS()){
   		Collections.sort(fpVector, new RhsOperatorComparator());
   	}
   	

   	rhsOpList.removeAllElements();
   	for(int i=0; i< fpVector.size(); i++) {
   		addRhsOpList(hm.get(fpVector.elementAt(i)).toString());
   	}
   	
   }
   */

   // Inner class to implement the Comparator interface. The compare() method sorts
   // the Vector of operators.
   /*class RhsOperatorComparator implements Comparator<Object> {

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			int h1 = ((FeaturePredicate)arg0).freqCount;
			int h2 = ((FeaturePredicate)arg1).freqCount;
			
			if(h1 == h2)
				return 0;
			else if(h1 < h2)
				return -1;
			else 
				return +1;
		}
   }*/

///////////////////////////////////////////////////////////////////////    
////////////////////// new FoA-search code ////////////////////////////
///////////////////////////////////////////////////////////////////////    

//  when ssFoaSearch is on, this is what gets called instead of searchRhsOpsFor().

   public Vector /* String */ searchRhsOpsFor_FoaSearch(Vector /*Instruction*/<Instruction> instructions) {

       Instruction primaryInstruction = instructions.get(0);

       //initialize the primaryInstruction
       //need to read this code better in searchRhsOpsFor()

       foaSearch(instructions);

       return primaryInstruction.getRhsOpSeq();
   }

// how would the initialization need to work?





   //moved the initialization inside here.
   //Since I made this method recursive, I changed its type to boolean
   //searchRhsOpsFor_FoaSearch still returns a Vector, after calling this.
   //the initialization here may need to happen multiple times, and since
   //the call is to foaSearch(), the initialization needs to happen here

   //sets the instructions' FoAs to be the next possible FoA-set
   //if there is a next possible FoA-set, it returns true
   //otherwise, (i.e. finalFailure), it returns false
   public boolean foaSearch(List<Instruction> instructions){

       int numInst = instructions.size();
       Instruction primaryInstruction = instructions.get(0);
       Instruction lastInstruction = instructions.get(numInst-1);

       boolean mapFound = false;

       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "entered foaSearch: numInst = " + numInst );


       if (numInst < 1){
           new Exception("illegal number of instructions: "+numInst+".").printStackTrace();
           mapFound = false;
           java.lang.System.exit(1);
       }
       else if (numInst == 1) { //if first instruction, or new FoA

           RhsState prevRhsState = (primaryInstruction.getGoalTest()==null) ?
                   null : primaryInstruction.getLastRhsState();

           // Initialize the exhaustive IDS
           initRhsSearch(primaryInstruction);
           // Make the initial search, which finds an operator sequence
           // that fits only the primary instruction

//            primaryInstruction.searchRhsOpSeq();
//
//            //base case
//            mapFound = (primaryInstruction.getLastRhsState() != prevRhsState);

           mapFound = existsMapForCurrentFoas(instructions); //does red backup

       }
       //otherwise
       else { //(numInst > 1)

//            RhsState lastRhsState = primaryInstruction.getLastRhsState();
           boolean finalFailure = false;            

           while (!mapFound && !finalFailure) { //different FoA-set at each iteration

               //RED BACKUP
               mapFound = existsMapForCurrentFoas(instructions); //does red backup

               //if mapFound is true, exit returning true
               if (mapFound)
                   break;

               boolean firstTime = true; //the loop must be entered at least once
               boolean nextFoaExists=false; //after the first time, this will get set properly

               //modify FoA for the last instruction, until it works...
               while ((nextFoaExists && !mapFound)||firstTime) { //stop when the list of FoAs runs out, OR map found

                   firstTime = false;


                   nextFoaExists = lastInstruction.setToNextPossibleFoa();
                   if (nextFoaExists){ //if there is a next one, i.e. the last FoA was modified
                       mapFound = existsMapForCurrentFoas(instructions); //does red backup
                   }
               }

               //if mapFound is true, exit returning true
               if (mapFound){
                   if(trace.getDebugCode("foasearch"))trace.out("foasearch", "some FoA for the last instruction worked!");
                   break;
               }

               //when we run out of FoAs,
               //    do black-backup, i.e. get the next FoA-set for the rest of
               //    the list, and go back to the top of the loop
               if(trace.getDebugCode("foasearch"))trace.out("foasearch", "----- BLACK back-up -----");


               // the lastRhsState for the cdr of a list is where the RHS-search should always begin
               //in particular, if we back up to a list of length 1, the RHS-state to be restored is null, i.e. search starts from the beginning.


               //remove the last instruction                
               List<Instruction> previousInstructions = instructions.subList(0, numInst-2);

               //increment the FoA-set (otherwise, we get into an infinite loop)
               boolean nextPreviousFoasExists = incrementFoaSet(previousInstructions);
               boolean mapFoundForPreviousFoas = false;
               if (nextPreviousFoasExists){

                   //the restoring happens inside existsMapForCurrentFoas()

                   //                    //we restrict this to cases when numInst > 2 because
//                    //this will not work when backing up to a single instruction,
//                    //because there is no instruction with index -1.
//                    //We should just let the search agent be reinitialized, which
//                    //should happen by itself.
//                    if (numInst>2){
//                        //the search state should be loaded appropriately.                
//                        Instruction newPrevInst = (Instruction) instructions.get(numInst-3);
//                        int lastWorkingDepth = newPrevInst.getLastWorkingDepth();
//                        AbstractQueue lastWorkingQueue = newPrevInst.getLastWorkingQueue();
//                        RhsState lastWorkingRhsState = newPrevInst.getLastRhsState();
//                        primaryInstruction.setQueueAndDepthFromWhichToBeginSearch(lastWorkingQueue, lastWorkingDepth);
//                        primaryInstruction.setLastRhsState(lastWorkingRhsState);
//                    }
                   //and then get the next working FoA-prefix
                   mapFoundForPreviousFoas = foaSearch(previousInstructions);
               }

               if (mapFoundForPreviousFoas)
                   lastInstruction.setToFirstPossibleFoa(); //and start the process again
               else
               {
                   finalFailure = true; //everything has been tried

               }
//              nothing else can explain the previous instructions

               //and start over, iterating over the FoAs of the last instruction
           } //end while (!finalFailure && !success)
       } // else (numInst > 1)

       if (mapFound){ //set RHS-state for possible future back-up
           ExhaustiveIDS search = (ExhaustiveIDS) primaryInstruction.getSearch();
           int lastWorkingDepth = search.getLastDepth();
           AbstractQueue lastWorkingQueue = search.getQueue();
           lastInstruction.setLastWorkingDepth(lastWorkingDepth);
           lastInstruction.setLastWorkingQueue(copyQueue(lastWorkingQueue));
           lastInstruction.setLastWorkingRhsState(primaryInstruction.getLastRhsState());
       }

       return mapFound;
   }

   /**
    * 
    * @param givenQueue
    * @return
    */
   private AbstractQueue copyQueue(AbstractQueue givenQueue) {
       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "copyQueue: givenQueue = " + printQueue(givenQueue));
       AbstractQueue newQueue = new AbstractQueue();
       List nodes = givenQueue.asList();

       for (int i=0; i<nodes.size(); i++){
           Node node = (Node) nodes.get(i);
           newQueue.addToBack(node);
       }
       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "copyQueue: returning newQueue = " + printQueue(newQueue));
       return newQueue;
   }

   private String printQueue(AbstractQueue givenQueue) {
       String s="";
       List nodes = givenQueue.asList();

       for (int i=0; i<nodes.size(); i++){
           Node node = (Node) nodes.get(i);
           s += SearchUtils.actionsFromNodes(node.getPathFromRoot());
       }
       return s;
   }



   /**
    * sets the next possible combination of FoAs
    * @param instructions
    * @return
    */
   private boolean incrementFoaSet(List<Instruction> instructions) {
       int numInst = instructions.size();
       boolean success = false;

       int i = numInst - 1;
       while(!success && i>=0){
           success = instructions.get(i).setToNextPossibleFoa();
           i--;
       }
       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "incrementFoaSet: success = " + success);

       return success;
   }


   //red backup: searches for a RHS-states that explain all instruction with the current FoAs
   private boolean existsMapForCurrentFoas(List<Instruction> instructions) {

       int numInst = instructions.size();
       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: instructions.size() = " + instructions.size());

       Instruction primaryInstruction = instructions.get(0);
       Instruction lastInstruction = instructions.get(numInst-1);

       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: " + lastInstruction.getCurrentFoaIndex() + "th FoA");


       RhsState lastRhsState = null;

       ///// "restore the search state"
       if (numInst>1){ 
           //restore lhsRhsState
           //restore 'queue' and 'depth' too
           Instruction penultimateInstruction = instructions.get(numInst-2);
           int lastWorkingDepth = penultimateInstruction.getLastWorkingDepth();
           //the queue corresponds to the last RHS state that explained the sublist
           AbstractQueue lastWorkingQueue = copyQueue(penultimateInstruction.getLastWorkingQueue());
           if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: lastWorkingQueue.size() = " + lastWorkingQueue.size());

           lastRhsState =  penultimateInstruction.getLastWorkingRhsState(); //primaryInstruction.getLastRhsState();
           primaryInstruction.setQueueAndDepthFromWhichToBeginSearch(lastWorkingQueue, lastWorkingDepth);
       }

       resetSimStTimer(); //prevent immediate time-out
       primaryInstruction.setSearchFailed(false); //in case there was just a failure
       ///// end "restore the search state"


       boolean mapFound = false;

       while (!primaryInstruction.isSearchFailed()&&!mapFound){ //only exit when either: no RHS state explains all instructions with the current FoAs; or, a RHS state explains all instructions with the current FoAs.

           //tests current RHS; if this fails, it does red backup and tries all RHSs.
           for (int i=0; i<instructions.size(); i++) { //start from i=1? ... since mapFound is always true of the first one
               Instruction instr = instructions.get(i); 
               if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: i="+ i +", instr = " + instr);

               if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: lastRhsState = " + lastRhsState);

               if(lastRhsState==null) 
                   mapFound = false;
               else
                   mapFound = instr.mapFocusOfAttention(lastRhsState);

               if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: after trying lastRhsState, mapFound = " + mapFound );

               if (!mapFound) {
                   if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: map not found. Continuing RHS search.");
                   primaryInstruction.searchRhsOpSeq();                   //red backup
                   lastRhsState = primaryInstruction.getLastRhsState(); //does this get set in case the above doesn't find anything?
                   break;//i.e. continue the while
               }
               else {
                   if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: map was found. lastRhsState = " + lastRhsState);
               }

           }//end for

       }//end while(!isSearchFailed()&&!mapFound)
       if(trace.getDebugCode("foasearch"))trace.out("foasearch", "existsMapForCurrentFoas: returning mapFound = " + mapFound );
       return mapFound;
   }




   /**
    * Search for an operator sequence for a production rule with the given
    * skill name.  This is for unordered focus of attention.  4/01/2006
    * c.f. searchRhsOps( Vector )
    * 
    * //6March2007: this method returns the operator sequence found.
    * @param skillName
    * @return
    */
   public /*private*/ Vector /* String */ searchRhsOpsFor(Vector /* Instruction */ instructions) {

       trace.out("miss", "searchRhsOpsFor: numInst = " + instructions.size());
       trace.out("miss", "searchRhsOpsFor: instructions = " + instructions);
       
       
       // The first instruction is the one and only one that is used to 
       // determine the operator sequence. Also, it holds 
       // (for a back-up sake) a RhsState that has been reached 
       // most recently 
       Instruction primaryInstruction = (Instruction) instructions.get(0);
       // If the primaryInstruction has not been used to search for the operator 
       // sequence, then initialize the search agent.  This is necessary 
       // because now the search is cached for backing-up

       //does this mean that future searches will start from the last successful RHS-state?
       if (primaryInstruction.getGoalTest() == null || 
               primaryInstruction.getLastRhsState() == null) {
       	// Initialize the exhaustive IDS
           initRhsSearch(primaryInstruction);

           // Make the initial search, which finds an operator sequence
           // that fits only the primary instruction
           primaryInstruction.searchRhsOpSeq();
       }

       // If the vector of instructions contain more than one instructions, then 
       // see if the rest of the instruction agree with the operator sequence 
       // found against the primaryInstruction.  If not, then back-up and offer
       // another operator sequence. 
       int numInst = instructions.size();

       if (numInst > 1) {

           RhsState lastRhsState = primaryInstruction.getLastRhsState();
           boolean mapFound = false;

           resetSimStTimer(); //prevent immediate time-out
           primaryInstruction.setSearchFailed(false); //in case there was just a failure

           //22Sep2007:
           //outer loop: loop until isSearchFailed()
           //OR 'mapFound' is true for ALL instructions.
           //(this can only happen if 'lastRhsState' was the same for the entire 'for' loop)
           //this code is essentially iterating over 'lastRhsState's, until one of them matches all instructions.
           while (!primaryInstruction.isSearchFailed() && !mapFound) {

               //inner loop:
               //for each instruction, check if mapFound
               //if not found, search RHS op-seq for primaryInstruction. 
               for (int i = 1; i < numInst; i++) {
                   Instruction instruction = (Instruction) instructions.get(i);
                   mapFound = instruction.mapFocusOfAttention(lastRhsState);
                   //see if the current operator sequence fits the foa in this order
                   //if so, move to next 'instruction'.
                   //otherwise, get the next RHS state for the primary instruction.
                   if (!mapFound) {
                       // find another operator sequence (backking-up), 
                       // by invoking a search on the instruction, which 
                       // will set a new rhsState and rhsOpSeq
                       primaryInstruction.searchRhsOpSeq();
                       // Inform the search agent the most recently reached state 
                       lastRhsState = primaryInstruction.getLastRhsState();
                       break;
                   }
               }
           }
       }

       // The rhsOpSeq is set by Instruction.searchRhsOpSeq()
       return primaryInstruction.getRhsOpSeq();
   	
       //TODO
   	//This is an alternative version of the entire method which includes some improvements
       //for the error analysis study.
       //However it results in an error where when input is not what is expected by the prodRules
       //the state reverts back to the previous state without failing.
       //Ex: if 6x-9=4x+2 expects add 6 (which prior knowledge results in), and add 9 is entered, 
       //a new state1 will be created with the edge add 9, but the state will revert back to the
       //start state 6x-9_4x+2.  This is visible in the BR graph, but not in SimSt's messaging.

      /* 

       // The first instruction is the one and only one that is used to 
       // determine the operator sequence. Also, it holds 
       // (for a back-up sake) a RhsState that has been reached 
       // most recently 
       Instruction primaryInstruction = (Instruction)instructions.get(0);

       // If the primaryInstruction has not been used to search for the operator 
       // sequence, then initialize the search agent.  This is necessary 
       // because now the search is cached for backing-up

       //does this mean that future searches will start from the last successful RHS-state?
       if (primaryInstruction.getGoalTest() == null || 
               primaryInstruction.getLastRhsState() == null) {

           // Initialize the exhaustive IDS
           initRhsSearch(primaryInstruction);
           // Make the initial search, which finds an operator sequence
           // that fits only the primary instruction
           primaryInstruction.searchRhsOpSeq();
       }


       // If the vector of instructions contain more than one instructions, then 
       // see if the rest of the instruction agree with the operator sequence 
       // found against the primaryInstruction.  If not, then back-up and offer
       // another operator sequence. 
       int numInst = instructions.size();

       if (numInst > 1) {

           RhsState lastRhsState = primaryInstruction.getLastRhsState();
           boolean mapFound = false;


           //22Sep2007:
           //outer loop: loop until isSearchFailed()
           //OR 'mapFound' is true for ALL instructions.
           //(this can only happen if 'lastRhsState' was the same for the entire 'for' loop)
           //this code is essentially iterating over 'lastRhsState's, until one of them matches all instructions.
           while (!primaryInstruction.isSearchFailed() && !mapFound) {

               //inner loop:
               //for each instruction, check if mapFound*
               //if not found, search RHS op-seq for primaryInstruction. 
               for (int i = 1; i < numInst; i++) {
                   Instruction instruction = (Instruction)instructions.get(i);

                   // jinyul - reordering of backtracking and mapping FoA's. 
                   // If no successful map found yet, and interactive learning is turned on, back up
                   // and initiate another search on the instruction.

                   // *Purpose*: Due to implementing the weak input matcher, without backing up SimStudent
                   // can potentially stay on a single operator forever, even if the user says "No" when
                   // asked if the input is correct.
                   if(!mapFound) {
                   	primaryInstruction.searchRhsOpSeq();
                   	lastRhsState = primaryInstruction.getLastRhsState();
                   	mapFound = true;
                   	//mapFound = instruction.mapFocusOfAttention(lastRhsState);
                   	break;
                   }

                   // otherwise, if not interactive learning, directly try to map FoA to the current
                   // instruction, and then if no map is found, then backup and do another search for
                   // the instruction.
                   if(!isInteractiveLearning()) {
                   	mapFound = instruction.mapFocusOfAttention(lastRhsState);
                   	if(!mapFound) {
                   		primaryInstruction.searchRhsOpSeq();
                   		lastRhsState = primaryInstruction.getLastRhsState();
                   		break;
                   	}
                   }

                   //else 
                   	//if(!mapFound) {
                   		//primaryInstruction.searchRhsOpSeq();
                   	//	lastRhsState = primaryInstruction.getLastRhsState();
                   		//break;
                  // 	}


                   //see if the current operator sequence fits the foa in this order
                   //if so, move to next 'instruction'.
                   //otherwise, get the next RHS state for the primary instruction.
                   //if (!mapFound) {
                       // find another operator sequence (backking-up), 
                       // by invoking a search on the instruction, which 
                       // will set a new rhsState and rhsOpSeq
                       //primaryInstruction.searchRhsOpSeq();
                       // Inform the search agent the most recently reached state 
                   	//lastRhsState = primaryInstruction.getLastRhsState();
                       //break;
                   //}
               }
           }

       }


       // The rhsOpSeq is set by Instruction.searchRhsOpSeq()
       return primaryInstruction.getRhsOpSeq();*/
   }

   // Initialize the search agent, and find a rhs operator sequence, which 
   // in turn is stored into cache in the Instruction object 
   // read by getRhsOpSeq()
   // Returns the most recently reached RhsState 
   public /*private*/ void initRhsSearch(Instruction primaryInstruction) {

       boolean opCached = isOpCached();
       boolean heuristicBasedIDS = isHeuristicBasedIDS();
       Vector<String> opList = getRhsOpList();
       HashMap fpCache = getFeaturePredicateCache();
       File wmeTypeFileExists = null;
       String wmeTypeFile = null;
       wmeTypeFileExists = new File(getProjectDir(), WME_TYPE_FILE);
       if(isSsWebAuthoringMode())
    	   wmeTypeFile = getProjectDirectory()+"/"+WME_TYPE_FILE;
       else if(!isWebStartMode()) {
			wmeTypeFile = wmeTypeFileExists.toString();
       } else {
    	   wmeTypeFile = WebStartFileDownloader.SimStAlgebraPackage+"/"+WME_TYPE_FILE;
       }
       primaryInstruction.initRhsSearch(opCached, heuristicBasedIDS, opList, fpCache, wmeTypeFile, getInputMatcher());
   }

   // overloaded initRhsSearch to take useAllFOAs flag
   public /*private*/ void initRhsSearch(Instruction primaryInstruction, boolean useAllFOAsFlag) {

       boolean opCached = isOpCached();
       Vector<String> opList = getRhsOpList();
       HashMap fpCache = getFeaturePredicateCache(); 
       File wmeTypeFileExists = null;
       String wmeTypeFile = null;
       wmeTypeFileExists = new File(getProjectDir(), WME_TYPE_FILE);
       if(isSsWebAuthoringMode())
    	   wmeTypeFile = getProjectDirectory()+"/"+WME_TYPE_FILE;
       else if(!isWebStartMode()) {
       	wmeTypeFile = wmeTypeFileExists.toString();
       } else {
    	   wmeTypeFile = WebStartFileDownloader.SimStAlgebraPackage+"/"+WME_TYPE_FILE;
       }
       primaryInstruction.initRhsSearch(opCached, opList, fpCache, wmeTypeFile, getInputMatcher(), useAllFOAsFlag);

   }

   // Search for the LHS WME-paths, and returns a Vector of strings,
   // each of which represents a WME-path where the last one has a
   // topological constraints
   /* String */
   public /*private*/ Vector<String> searchLhsPaths( Vector /* Instruction */<Instruction> instructions ) {

   	if(trace.getDebugCode("miss"))trace.out("miss", "searchLhsPaths: " + instructions);
       SearchAgent lhsPaths = null;
       LhsSearchSuccessorFn successorFn = null;
   	//	if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "all instructinos to searchLhsPaths are: " + instructions);
    //  	if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "creating Lhs state from : " + instructions.get(0));
       LhsState initState = new LhsState( instructions.get(0) );
       if(isSsWebAuthoringMode())
    	   successorFn = new LhsSearchSuccessorFn(getProjectDirectory()+"/"+WME_TYPE_FILE,
    			   getProjectDirectory()+"/"+INIT_STATE_FILE,
    			   getProjectDirectory()+"/"+DEFAULT_STUCTURE_FILE,
                       instructions,getConstraintPredicateClasses(), this.generalWmePaths,this.ssRete);
       else if(isWebStartMode())
       	successorFn = new LhsSearchSuccessorFn(WebStartFileDownloader.SimStAlgebraPackage+"/"+WME_TYPE_FILE,
       			WebStartFileDownloader.SimStAlgebraPackage+"/"+INIT_STATE_FILE,
       			WebStartFileDownloader.SimStAlgebraPackage+"/"+DEFAULT_STUCTURE_FILE,
                   instructions,getConstraintPredicateClasses(), this.generalWmePaths,this.ssRete);
       else
    	   successorFn = new LhsSearchSuccessorFn( new File(getProjectDir(), WME_TYPE_FILE).toString(),
              		new File(getProjectDir(), INIT_STATE_FILE).toString(),
              		new File(getProjectDir(), DEFAULT_STUCTURE_FILE).toString(),
                      instructions,getConstraintPredicateClasses(), this.generalWmePaths,this.ssRete);
       LhsGoalTest goalTest = new LhsGoalTest();
       Problem problem = new Problem( initState, successorFn, goalTest );

       Search search = new DepthFirstSearch( new TreeSearch() );
  
       if(trace.getDebugCode("miss"))trace.out("miss", "LHS: Searching...");      
       
       try {
           lhsPaths = new SearchAgent( problem, search );
       } catch (Exception e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

   	
       Properties searchProp = lhsPaths.getInstrumentation();
       if(trace.getDebugCode("miss"))trace.out("miss", "LHS: Properties...");
       Enumeration props = searchProp.propertyNames();
       while ( props.hasMoreElements() ) {
           String key = (String)props.nextElement();
           String propVal = searchProp.getProperty( key );
           if(trace.getDebugCode("miss"))trace.out("miss", "     " + key + " -> " + propVal);
       }

       // Since the topological constraints were incrementally built
       // up, only the last action represents a complete set of
       // constraints.  Thus, strip off other "incomplete" constraints.  
       List /* String */ tmpActions = lhsPaths.getActions();


      	//if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "!!!!!!!!!!!!!!!!!!!!!!!! tmpActions after search agent " + tmpActions);
      	
      	
       Vector /* String */<String> actions = new Vector<String>();
       
       for ( int i = 0; i < tmpActions.size(); i++ ) {

           String action = (String)tmpActions.get(i);       
           
           if ( i != tmpActions.size() -1 ) {

               int index = action.indexOf('|');

               if ( index > 0 ) {
                   action = action.substring( 0, index );
               }
           }
           actions.add( action );
       }
       return actions;
   }

   /**
    * @return a Vector of WmeConstraintPredicates based on the constraintPredicateNames list
    */
   private Vector<WMEConstraintPredicate> getConstraintPredicateClasses() {

       Vector<WMEConstraintPredicate> constraints=new Vector<WMEConstraintPredicate>();
       Iterator<String> iter=constraintPredicateNames.iterator();
       while(iter.hasNext())
       {
           WMEConstraintPredicate predicate=(WMEConstraintPredicate)FeaturePredicate.getPredicateByClassName(iter.next());
           constraints.add(predicate);
       }

       return constraints;
   }


   // ----------------------------------------------------------------------
   // Read files
   // 

   /**
    * Read operator symbols from a file
    *
    * @param fileName a <code>String</code> value
    */
   public void readRhsOpList( String fileName ) {

       readRhsOpListAux( fileName );
       if(trace.getDebugCode("miss"))trace.out("miss", numRhsOps() + " operators read from " + fileName);
   }

   private void readRhsOpListAux( String fileName ) {

   	BufferedReader reader = null;
   	
   	// Check if file is on the local disk or it is part of the jar
   	if(new File(fileName).exists() ) {
           try {
               reader = new BufferedReader( new FileReader( fileName ) );
           } catch (FileNotFoundException e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }    		
   	} else { // For webstart
	    	ClassLoader cl = this.getClass().getClassLoader();
	    	InputStream is = cl.getResourceAsStream(fileName);
	    	if(is == null) //File does not exist in provided jars
	    		return;
	    	InputStreamReader isr = new InputStreamReader(is);
	    	reader = new BufferedReader(isr);    		
   	}

       try {
           String rhsOp = null;
           while ( ( rhsOp = reader.readLine() ) != null ) {
               // Strip comment off
               int commentPos = rhsOp.indexOf(';');
               if ( commentPos != -1 ) {
                   rhsOp = rhsOp.substring( 0, commentPos );
               }
               // Strip spaces off
               rhsOp = rhsOp.replaceAll( " ", "" );
               if ( rhsOp.length() > 0 ) {
                   addRhsOpList( rhsOp );
               }
           }
           reader.close();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       // Add the VoidOp to the operator list
       // addRhsOpList( VOID_OP_CLASS );
   }

   /**
    * Read operator symbols from a file
    *
    * @param fileName a <code>String</code> value
    */
   public void readRhsOpList( URI filename ) {

       readRhsOpListAux( filename );
       if(trace.getDebugCode("miss"))trace.out("miss", numRhsOps() + " operators read from " + filename);
   }

   private void readRhsOpListAux( URI filename ) {

       BufferedReader reader = null;
       try {
       	File file = new File(filename);
           reader = new BufferedReader(new FileReader(file));
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       try {
           String rhsOp = null;
           while ( ( rhsOp = reader.readLine() ) != null ) {
               // Strip comment off
               int commentPos = rhsOp.indexOf(';');
               if ( commentPos != -1 ) {
                   rhsOp = rhsOp.substring( 0, commentPos );
               }
               // Strip spaces off
               rhsOp = rhsOp.replaceAll( " ", "" );
               if ( rhsOp.length() > 0 ) {
                   addRhsOpList( rhsOp );
               }
           }
           reader.close();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       // Add the VoidOp to the operator list
       // addRhsOpList( VOID_OP_CLASS );
   }

   /**
    * Read Feature Predicates from a file
    *
    * @param fileName a <code>String</code> value
    */
   public void readPredicateSymbols( String fileName ) {
       readFeaturePredicates( fileName );
       if(trace.getDebugCode("miss"))trace.out("miss", numPredicates() + " feature predicate read" );
       //printFeaturepredicates();
   }

   private void readFeaturePredicates( String fpFile ) {

   	BufferedReader reader = null;
   	
   	// Check if file is on the local disk or it is part of the jar
   	if(new File(fpFile).exists() ) {
           try {
               reader = new BufferedReader( new FileReader( fpFile ) );
           } catch (FileNotFoundException e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }    		
   	} else{ // For webstart
	    	ClassLoader cl = this.getClass().getClassLoader();
	    	InputStream is = cl.getResourceAsStream(fpFile);
	    	if(is == null) //File does not exist in provided jars
	    		return;
	    	InputStreamReader isr = new InputStreamReader(is);
	    	reader = new BufferedReader(isr);    		
   	} 
   	

       try {
           String featurePredicate = null;
           while ( (featurePredicate = readNoneBlankLine(reader)) != null ) {
               addFeaturePredicate( featurePredicate );
           }

           reader.close();

       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   /**
    * read the (fully qualified) constraint predicates class names from a file
    * @param constraintFile
    */
   private void readConstraintPredicates( String constraintFile ) {

   	BufferedReader reader = null;
   	
   	// Check if file is on the local disk or it is part of the jar
   	if(new File(constraintFile).exists() ) {
           try {
               reader = new BufferedReader( new FileReader( constraintFile ) );
           } catch (FileNotFoundException e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }    		
   	} else { // For webstart
	    	ClassLoader cl = this.getClass().getClassLoader();
	    	InputStream is = cl.getResourceAsStream(constraintFile);
	    	if(is == null)  //File does not exist in provided jars
	    		return;
	    	InputStreamReader isr = new InputStreamReader(is);
	    	reader = new BufferedReader(isr);    		
   	}

       try {
           String constraint = null;
           while ( (constraint = readNoneBlankLine(reader)) != null ) {
               addConstraintFeaturePredicate( constraint );
           }

           reader.close();

       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   /**
    * Read Decomposers from a file from a file
    *
    * @param fileName a <code>String</code> value corresponding to the file name
    */

   private boolean readDecomposers( String fileName ) {

       BufferedReader reader = null;

       try {
           reader = new BufferedReader( new FileReader( fileName ) );
       } catch (FileNotFoundException e) {

           logger.simStLogException(e);
           trace.err("file "+fileName+ " does not exist" );
           return false;
       }

       try {
           String curDecomp = null;
           while ( (curDecomp = readNoneBlankLine(reader)) != null ) {
               addDecomposer( curDecomp );
           }

           reader.close();

       } catch (IOException e) {

           logger.simStLogException(e);
           trace.err("Error reading decomposer file");
           e.printStackTrace();
           return false;
       }
       return true;
   }


   private Vector<String> unlearnedConcepts = new Vector();

   /**
    * read the concepts from a file
    * lets SimStudent know about what it needs to learn
    * @param conceptFile
    */
   private void readConcepts( String conceptFile ) {

   	BufferedReader reader = null;

   	// Check if file is on the local disk or it is part of the jar
   	if(new File(conceptFile).exists() ) {
           try {
               reader = new BufferedReader( new FileReader( conceptFile ) );
           } catch (FileNotFoundException e) {
               e.printStackTrace();
               logger.simStLogException(e);
           }
   	} else { // For webstart
   			String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+conceptFile;
	    	ClassLoader cl = this.getClass().getClassLoader();
	    	InputStream is = cl.getResourceAsStream(file);
	    	InputStreamReader isr = new InputStreamReader(is);
	    	reader = new BufferedReader(isr);
   	}

       try {
           String concept = null;
           while ( (concept = readNoneBlankLine(reader)) != null ) {
               if(trace.getDebugCode("sstt"))trace.out("sstt", "Unlearned concept: " + concept);
		unlearnedConcepts.add(concept);
           }

           reader.close();

       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   public Vector<String> getUnlearnedConcepts() {
	return unlearnedConcepts;
   }

   /** Marks a concept as learned
     * @param concept the concept to be marked as learned
     */
   public void markConceptLearned( String concept ) {
	for(int i=0;i<unlearnedConcepts.size();i++) {
	    if (concept.equals(unlearnedConcepts.get(i))) {
		unlearnedConcepts.removeElementAt(i);
		return;
           }
       }
   }

   // Read a set of instructions from a specified file
   private void readInstructions( String instructionFile ) {

       BufferedReader reader = null;
       try {
           reader = new BufferedReader( new FileReader( instructionFile ) );
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       try {

           String instructionName = null;
           while ((instructionName = readNoneBlankLine( reader )) != null) {

               // Focus of Attention consists of SAI followed by
               // dependent WMEs (see step-performed.txt)
               Vector /* of String */<String> focusOfAttention = new Vector<String>();
               readFocusOfAttention( reader, focusOfAttention );
               Instruction instruction =
                   new Instruction( instructionName, focusOfAttention );
               addInstruction( instruction );
           }

           reader.close();

       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }
   }

   private void readFocusOfAttention( BufferedReader reader, Vector /* String */<String> focusOfAttention ) {

       String foa = readLineButComment( reader );
       while ( foa != null && !foa.equals("") ) {
           focusOfAttention.add( foa );
           foa = readLineButComment( reader );
       }
   }

   private String readNoneBlankLine( BufferedReader reader ) {

       String lineStr = readLineButComment( reader );
       while ( lineStr != null && lineStr.equals("") ) {
           lineStr = readLineButComment( reader );
       }
       return lineStr;
   }

   private String readLineButComment( BufferedReader reader ) {

       String lineStr = null;
       try {
           lineStr = reader.readLine();
       } catch (IOException e) {
           e.printStackTrace();
           logger.simStLogException(e);
       }

       // If there is a line to read, then trim a comment
       if ( lineStr != null ) {
           int commentPosition = lineStr.indexOf( ';' );
           if ( commentPosition == 0 ) {
               return readLineButComment( reader );
           } if ( commentPosition > 0 ) {
               return lineStr.substring( 0, commentPosition );
           } else {
               return lineStr;
           }
       }
       return null;
   }

   // ----------------------------------------------------------------------
   // Print Rules
   //

//  private void printRules() {
//  printRules( System.out );
//  }

   private void printRules( PrintStream out ) {

       out.print( Rule.RULE_PREAMBLE_1 );

       String uds = 
           ( (getUserDefSymbols() != null) ? 
                   getUserDefSymbols() : 
                       (getStudentInterfaceClass() + ".UserDefSymbols") );
       out.print("(load-package " + uds + ")\n\n");

       out.print(";; ----------------\n;; Production rules\n;; ----------------\n\n");
       Iterator rules = getAllRules();
       
	

   	
       while ( rules.hasNext() ) {
    	  
    	   	
       	Rule rule = (Rule)rules.next();  
       	
       			
       	   Vector<Instruction> ruleInstructions= getInstructionsFor(rule.getName());
       		      	 
           out.print(rule.toString(ruleInstructions, this.getSsRete()) + "\n");

     
           
           out.print("\n");       
       }
      
    	/*if (isSs2014FractionAdditionAdhoc()){     		
       		try {
				String doneProduction = new Scanner(new File("doneProduction.txt")).useDelimiter("\\A").next();
				   out.print(doneProduction + "\n");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       		
       	}*/
       	
   }

   // ----------------------------------------------------------------------
   // A userful tool for debugging
   //

   public static void suspendForDebug(BR_Controller brController, String title, String[] message) {

       JFrame frame = brController.getActiveWindow();
       JOptionPane.showMessageDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
   }

   // ----------------------------------------------------------------------
   // The MAIN invocation
   // 

   /**
    * Run SimSt in a batch mode without having a tutor interface
    * Wed Dec 20 17:14:06 LMT 2006 :: Noboru
    * This is necessary to run Stoichiometry Tutor (for the Bootstrapping project),
    * which does not have Java interface, but only BRDs 
    *
    * @param args a <code>String[]</code> value
    */
   public static void main(final String[] args) {

       SingleSessionLauncher ctatLauncher = new SingleSessionLauncher(args);
       BR_Controller controller = ctatLauncher.getController();

       readPseudoWidget(controller);

       // Add all Comm Widgets
       /*
			new pact.CommWidgets.JCommTable().setCommName("commTable1", controller);
			new pact.CommWidgets.JCommTable().setCommName("commTable2", controller);
			new pact.CommWidgets.JCommButton().setCommName("done", controller);
        */

       
      //nbarba: 01/16/2014: Now MissController has runSimStNoTutorInterface, not SingleSessionLaungher. 
      //     Miss controller that parses the arquments so 
      //     ctatLauncher.runSimStNoTutorInterface();
       ctatLauncher.getController().getMissController().runSimStNoTutorInterface();

       System.exit(0);
   }

   private static void readPseudoWidget(BR_Controller controller) {

       FileReader fileReader = null;
       try {
           fileReader = new FileReader(PSEUDO_WIDGET_FILE);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
       BufferedReader bufferedReader = new BufferedReader(fileReader);

       String widgetDef = null;
       try {
           while ((widgetDef = bufferedReader.readLine()) != null) {
               if (widgetDef.equals("") || widgetDef.matches("[\\s\\p{Punct}]+.*")) {
                   continue;
               }
               String[] widgetName = widgetDef.split("\\t+");
               Class widgetClass = Class.forName(widgetName[1]);
               Object widget = widgetClass.newInstance();
               Class[] argTypes = new Class[] {String.class, BR_Controller.class};  
               Method setCommName = widgetClass.getMethod("setCommName", argTypes);
               Object[] arguments = new Object[] {widgetName[0], controller};
               setCommName.invoke(widget, arguments);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   public static boolean isSelectionInEdgePath(String selection, Vector edgePath){

       if(trace.getDebugCode("foagetter"))trace.out("foagetter", "isSelectionInEdgePath: edgePath = " + edgePath);

       ProblemEdge edge;
       EdgeData edgeData;
       for (int i=0; i<edgePath.size(); i++){
           edge = (ProblemEdge) edgePath.get(i);
           edgeData = edge.getEdgeData();

           if(trace.getDebugCode("foagetter"))trace.out("foagetter", "edgeData.getSelection() = " + edgeData.getSelection().get(0));

           if (edgeData.getSelection().get(0).equals(selection)){
               if(trace.getDebugCode("foagetter"))trace.out("foagetter", "isSelectionInEdgePath: returning true");
               return true;
           }
       }
       return false;
   }

   public static String stripQuotes(String s) {
       String result = null;
       if (s.charAt(0)=='"' && s.charAt(s.length()-1)=='"')
           result = s.substring(1,s.length()-1);
       else
           result = s;
       return result;
   }

   public void displayMessage(String title, String message)
   {
   	if(getMissController().isPLEon())
   	{
   		getMissController().getSimStPLE().giveMessage(message);
   	}
   	else
   	{
   		JOptionPane.showMessageDialog(getBrController().getActiveWindow(), message, title, JOptionPane.PLAIN_MESSAGE);
   	}
   }

   public void displayMessage(String title, String[] message)
   {
   	if(getMissController().isPLEon())
   	{
   		String msg = "";
   		for(int i=0;i<message.length;i++)
   			msg += message[i]+" ";
   		getMissController().getSimStPLE().giveMessage(msg);
   	}
   	else
   	{
   		JOptionPane.showMessageDialog(getBrController().getActiveWindow(), message, title, JOptionPane.PLAIN_MESSAGE);
   	}
   }

   public int displayConfirmMessage(String title, String message)
   {
   	if(getMissController().isPLEon())
   	{
   		return getMissController().getSimStPLE().giveMessageRequiringResponse(message);
   	}
   	else
   	{
   		return JOptionPane.showConfirmDialog(getBrController().getActiveWindow(), message, title, JOptionPane.YES_NO_OPTION);
   	}
   }

   public void displayActionListenerMessage(String message, ActionListener al)
   {
   	if(getMissController().isPLEon())
   	{
   		getMissController().getSimStPLE().giveMessagePossibleResponse(message, al);
   	}
   	else
   	{
       	int val = JOptionPane.showConfirmDialog(getBrController().getActiveWindow(), message, "", JOptionPane.YES_NO_OPTION);
   		if(val == JOptionPane.YES_OPTION) {
               	al.actionPerformed(null);
   		}
   	}
   }


   public int displayConfirmMessage(String title, String[] message)
   {
   	if(getMissController().isPLEon())
   	{
   		String msg = "";
   		for(int i=0;i<message.length;i++)
   			msg += message[i]+" ";
   		return getMissController().getSimStPLE().giveMessageRequiringResponse(msg);
   	}
   	else
   	{
   		return JOptionPane.showConfirmDialog(getBrController().getActiveWindow(), message, title, JOptionPane.YES_NO_OPTION);
   	}
   }

	public int getInstructionCount() {
		return instructions.size();
	}
	
	//Gets a vector listing all of the foas used for an SAI
	public Vector<String> listFoas(Sai sai) {
		if(!isFoaGetterDefined())
			return null;
		return foaGetter.foaGetterStrings(getBrController(), sai.getS(), sai.getA(), sai.getI(), null);
	}
	
	
	public Collection<RuleActivationNode> createOrderedActivationList(	Vector baseList) {
		
		Collection<RuleActivationNode> activationList;
	    if(activationListType.equals(ACCURACY_SORTED_ACTIVATION_LIST))
	    {
	    	activationList = new ActivationList(baseList, this);
	    }
	    else //default
	    {
	    	activationList = baseList.subList(0, baseList.size());
	    }
				
		return activationList;
	}
		

   /*
   public static void main(final String[] args) {

	BR_Controller brController = new BR_Controller();
	SimSt simSt = new SimSt(new MissController(brController));

       String homeDir = getHomeDir() + "/";
	String brdDirName = homeDir + "../QA/Tests/TestTutors/Projects/SimStAlgebraI/Problems/01-04/BRD/";
       File brdDir = new File(brdDirName);
       String[] brdSubDirs = brdDir.list();
       for (int i = 0; i < brdSubDirs.length; i++) {
           String brdSubDirName = brdDirName + brdSubDirs[i];
           File brdSubDir = new File( brdSubDirName );
           if (brdSubDir.isDirectory()) {
       	String [] brdFiles = brdSubDir.list();
       	for (int j = 0; j < brdFiles.length; j++) {
       	    if (brdFiles[j].indexOf("brd") > 0) {
       		long sTime = (new Date()).getTime();
       		LoadFileDialog.doLoadBRDFile(brController, brdFiles[j], brdSubDirName);
       		long eTime = (new Date()).getTime();
       	    }
       	}
           }
       }
   }
   */

	public void restoreMTWMState(){
		try{
			File file =  null;
			String val = BR_Controller.DISK_LOGGING_DIR;
			if(!isWebStartMode())
//				file = new File(getUserID()+"-wm.xml");
				file = new File(getLogDirectory(), getUserID()+"-wm.xml");
			else
				file = new File(WebStartFileDownloader.SimStWebStartDir+getUserID()+"-wm.xml");
//				file = new File(WebStartFileDownloader.SimStWebStartDir+getUserID()+"_wm.xml");
			if(file.exists()) {
				JAXBContext jaxbContext = JAXBContext.newInstance(ModelTraceWorkingMemory.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				ModelTraceWorkingMemory mt = (ModelTraceWorkingMemory) jaxbUnmarshaller.unmarshal(file);
				setModelTraceWM(mt);
			}
		  } catch (JAXBException e) {
			e.printStackTrace();
		  }

	}
	
	public void saveMTWMState(){
		try{
		
			File file = null;
			//System.out.println(" Web start ? "+isWebStartMode());
			if(!isWebStartMode())
//				file = new File(getUserID()+"-wm.xml");
				
				file = new File(getLogDirectory(), getUserID()+"-wm.xml");
			else
				file = new File(WebStartFileDownloader.SimStWebStartDir+getUserID()+"-wm.xml");
//				file = new File(WebStartFileDownloader.SimStWebStartDir+getUserID()+"_wm.xml");
			
			ModelTraceWorkingMemory mt = (ModelTraceWorkingMemory) getModelTraceWM().clone();
			JAXBContext jaxbContext = JAXBContext.newInstance(ModelTraceWorkingMemory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(mt, file);
			//jaxbMarshaller.marshal(mt, System.out);
		}
		 catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	public InquiryWebAuthoring getInquiryWebAuthoring() {
		return inquiryWebAuthoring;
	}
	public void setInquiryWebAuthoring(InquiryWebAuthoring inquiryWebAuthoring) {
		this.inquiryWebAuthoring = inquiryWebAuthoring;
	}
	
	
}


//end of f:/Project/CTAT/ML/ISS/miss/SimSt.java