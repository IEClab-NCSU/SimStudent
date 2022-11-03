/**
 * 
 * 
 */
package edu.cmu.pact.miss.PeerLearning;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTextArea;
import pact.CommWidgets.JCommTextField;
import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.MTRete;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.AlgebraProblemAssessor;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.HashMap;
import edu.cmu.pact.miss.Instruction;
import edu.cmu.pact.miss.JTabbedPaneWithCloseIcons;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pact.miss.ProblemAssessor;
import edu.cmu.pact.miss.RegexHashtable;
import edu.cmu.pact.miss.Rule;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.SimStCognitiveTutor;
import edu.cmu.pact.miss.SimStInteractiveLearning;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform.ExampleAction;
import edu.cmu.pact.miss.PeerLearning.QuizPane;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowUtilities;
import edu.cmu.pact.miss.PeerLearning.GameShow.ProblemType;
import edu.cmu.pact.miss.PeerLearning.SimStExplainWhyNotDlg.TextEntryListener;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;
import edu.cmu.pact.miss.console.controller.MissController;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;
import edu.cmu.pact.miss.storage.StorageClient;

/**
 * @author mazda
 *
 */
public class SimStPLE {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	// Action event types
	public static final String NEXT_PROBLEM = "next problem";
	public static final String QUIZ = "quiz";
	public static final String QUIZ_FILE = "quiz.txt";
	public static final String EXAMPLES = "examples";
	public static final String UNDO = MissControllerExternal.UNDO;
	public static final String RESTART = "restart";
	public static final String CFG = "configure";
	public static final String EXAMPLES_FILE = "examples.txt";
	public static final String COMPONENT_NAME_FILE = "compNames.txt";
	public static final String CONFIG_FILE = System.getProperty("configFile") == null ? "simSt-config.txt" : System.getProperty("configFile");

	public static final String CURRICULUM_BROWSER = "curriculum browser";
	public static final String CURRICULUM_FILE = "curriculum.html";
	private static final String ASK_NEW_PROBLEM_MSG = "Enter next problem";
	public static final String NOT_UNDERSTAND = "I still don't get it.  Let's move on to another problem.  Is there an easier one we can try?";
	public static final String ENTER_FULL_PROBLEM = "You haven't finished with entering a problem yet.";
	public static final String ENTER_ANOTHER_PROBLEM = "I don't think this is a good problem for SimStName. Why don't you give another problem?.";
	public static final String PROBLEM_STAT_FILE = "problemStatisticsStudy.csv";
	public static final String PROBLEM_BANK = "problem bank";

	public static final String START_STATE_ELEMENTS_HEADER = "startStateElements";
	private static final String FOA_ELEMENTS_HEADER = "foaElements";
	private static final String COMPONENT_NAMES_HEADER = "componentNames";
	private static final String QUIZ_PROBLEMS_HEADER = "quizProblems";
	private static final String EXAMPLES_HEADER = "examples";
	private static final String MISTAKE_EXPLANATION_HEADER = "mistakeExplanations";
	private static final String PROBLEM_CHOICE_EXPLANATION_HEADER = "problemChoiceExplanations";
	private static final String HINT_EXPLANATION_HEADER = "hintExplanations";
	private static final String SECTIONS_HEADER = "sections";
	public static final String PROBLEM_DELIMITER_HEADER = "problemDelimiter";
	private static final String VALID_SELECTIONS_FOR_SE = "validSelectionsForSelfExplanation";
	
	// Added by Tasmia
	private static final String VALID_SELECTIONS_FOR_BQ = "validSelectionsForBrainstormingQuestions";
	private static final String VALID_SELECTIONS_FOR_BAQ = "validSelectionsForBothAgreeQuestions";
	private static final String SKILL_NICKNAMES = "skillNickName";
	private static final String ASKING_IF_TUTOR_KNOWS_STEP_TOPIC_OPTIONS = "confidenceDemonstration";
	private static final String CONFIDENCE_DEMONSTRATION_HEADER = "confidenceDemonstration";
	//private static final String MISTAKE_EXPLANATION_CTI_HEADER = "mistakeExplanationsCTI";

	

	private final String USER_ID_REQUEST_TITLE = "User ID";
	private final String USER_ID_REQUEST_MSG = "Please enter your User ID:";

	public static final int FIRST_EXAMPLE = 1;
	public static final int SIM_ST_TAB = 0;
	public static final int PROBLEM_BANK_TAB = 1;

	public static final String START_MESSAGE = "Alright, what problem should I try?";
	public static final String THINK_MESSAGE = "Hmm....";
	public static final String CONFUSE_MESSAGE = "Uh...";
	public static final String UNCONFUSE_MESSAGE = "Oh, okay.  I think I understand a little better now.";
	public static final String DONE_MESSAGE = "...and done!  What should I do next?";
	public static final String QUIZ_MESSAGE = "I'm taking the quiz now.  Let's see how I do.";
	public static final String DO_PROBLEM_MSG = "Should I do this problem?";
	public static final String DO_PROBLEM_NO = "OK. Click \"Yes\" when you've entered a problem you like better.";
	public static final String NO_UNDO_MSG = "But we haven't gotten through any work on this problem yet!";
	public static final String UNDO_DONE_MSG = "Oh, is that not the answer to the problem?  Should I go back to working on it?";
	public static final String SHOULD_DO_MSG = "Alright, I erased $.";
	public static final String UNDO_ERROR_MSG = "I'm not quite sure how to undo that step.  Can we just restart the problem or go on to a different one?";
	public static final String RESUME_MSG = "OK, I'm still not sure what I should do next though.  Can you please show me what to do?";
	public static final String RESTART_MSG = "OK, I'm trying this problem again from the beginning.";
	public static final String BACK_TO_WORK_MSG = "Alright, back to the problem.  Let me think what I should do.";
	public static final String LBT_APLUS_SPLASH_MSG_1 = "Hello! My name is Mr Williams, and I am here to help you teach SimStudent. Click on me whenever you want to ask a question";
	public static final String LBT_APLUS_SPLASH_MSG_2 = "And remember, your goal is to help SimStudent pass all the levels of the quiz.\n\n Good luck!";
	public static final String LBT_APLUS_SPLASH_MSG_3 = "You have already completed the final challenge!\n I made another set of final challenge problems\n for SimStudent. To see if SimStudent can pass them click\n the 'YES' button. ";
	public static final String COGTUTOR_APLUS_SPLASH_MSG_1 = "Hello, my name is Mr Williams! I will give you problems to solve and I can also help you solve them. If you get stuck you can click on me to get a hint.\nClick on me now to see what happens.";
	public static final String COGTUTOR_APLUS_SPLASH_MSG_2 = "Hello! My name is Mr Williams and I am here to help you. Click on me whenever you want to ask a question.";
	public static final String COGTUTOR_APLUS_SPLASH_MSG_3 = "And remember, your goal is to master all 4 sections shown on your progress board!\n\n Good luck!";
	public static final String COGTUTOR_APLUS_SPLASH_MSG_4 = "And remember, your goal is to pass all the levels of the quiz. So practice well.\n \nGood luck!";

	public static final String APLUS_SPLASH_OK_BUTTON_TXT_APLUS_CONTROL = "OK, I take the quiz";
	public static final String APLUS_SPLASH_TUTORING_BUTTON_TXT_APLUS_CONTROL = "Let me practice first";
	public static final String APLUS_SPLASH_OK_BUTTON_TXT = "<html>OK, I understand<br> my goal</html>";

	// public static final String APLUS_SPLASH_CANCEL_BUTTON_TXT="<html>I don't know
	// how<br>to use this app</html>";
	public static final String APLUS_SPLASH_CANCEL_BUTTON_TXT = "<html>I'd like to watch<br>the video again</html>";

	public static final String WELCOME_SPLASH = "img/metatutor_splash.gif";
	public static final String TEACHER_BANK_IMAGE = "img/teacher_bank.png";
	public static final String TEACHER_BANK_IMAGE_COGTUTOR = "img/teacher_bank_cogtutor.png";
	public static final String LBT_APLUS_SPLASH_IMG_0 = "img/metatutor_splash_click.png";
	public static final String LBT_APLUS_SPLASH_IMG_1 = "img/splash_1.png";
	public static final String LBT_APLUS_SPLASH_IMG_2 = "img/splash_2.png";
	public static final String LBT_APLUS_SPLASH_IMG_3 = "img/splash_3.png";
	public static final String LBT_APLUS_SPLASH_IMG_4 = "img/splash_4.png";
	public static final String LBT_APLUS_SPLASH_IMG_5 = "img/splash_5.png";

	public static final String COGTUTOR_APLUS_SPLASH_IMG_2 = "img/splash_2_cogTutor.png";
	public static final String COGTUTOR_APLUS_SPLASH_IMG_2_APLUS = "img/splash_2_cogTutor_Aplus.png";
	public static final String COGTUTOR_APLUS_SPLASH_IMG_3 = "img/splash_3_cogTutor.png";
	public static final String COGTUTOR_APLUS_SPLASH_IMG_4 = "img/splash_4_cogTutor.png";
	public static final String COGTUTOR_APLUS_SPLASH_IMG_4_APLUS = "img/splash_4_cogTutor_aplus.png";
	public static final String COGTUTOR_SPLASH_IMG_5 = "img/splash_5_cogTutor.png";
	public static final String COGTUTOR_APLUS_SPLASH_IMG_5 = "img/splash_5_cogTutor_aplus.png";

	public static final String DONE_CAPTION_ENABLED = "Problem is Solved";
	public static final String DONE_CAPTION_DISABLED = "Press enter to complete a step";

	public static final String EXAMPLE_COMM_TEXT = "You can use the examples on the right for practice. Select an example problem and use the buttons above to view solution steps!";

	public static final String OTHER_OPTION = "Other - Type Your Own Explanation";
	public static final String SELECT_OPTION = "Please Select One";
	public static final String FOA_EXAMPLE_MATCH_SYMBOL = "(E)";
	public static final String CURRENT_STEP_MATCH_SYMBOL = "(CE)";
	public static final String INPUT_MATCH_SYMBOL = "(I)";
	public static final String OPERAND_MATCH_SYMBOL = "(#)";
	public static final String OPERATOR_MATCH_SYMBOL = "(O)";
	public static final String PROBLEM_MATCH_SYMBOL = "(P)";
	public static final String FOA_PART_MATCH_SYMBOL = "(F)";

	/*
	 * public static final String STUDENT_IMAGE = "stacyNormal.jpg"; public static
	 * final String STUDENT_THINK_IMAGE = "stacyThink.jpg"; public static final
	 * String STUDENT_SUCCESS_IMAGE = "stacyHappy.jpg";
	 */
	public static final String TEACHER_IMAGE = "img/teacher.png";
	public static String STUDENT_IMAGE = "%img/head1.png%img/hair1.png%img/shirt1.png";
	public static final String NORMAL_EXPRESSION = "img/face1.png";
	public static final String SUCCESS_EXPRESSION = "img/face2.png";
	public static final String THINK_EXPRESSION = "img/face6.png";
	public static final String THINK_EXPRESSION_EX = "img/face6_ex.png";
	public static final String ASK_EXPRESSION = "img/face5.png";
	public static final String SAD_EXPRESSION = "img/face3.png";
	public static final String CONFUSE_EXPRESSION = "img/face3.png";
	public static final String UNDOCK_ICON_IMAGE = "img/Undock_Icon.png";
	public static String METATUTOR_EMPTY_DESK = "img/metatutor_empty.png";
	public static String METATUTOR_IMAGE = "img/metatutor.png";
	public static String METATUTOR_STUDENT_INTERACTION_IMAGE = "img/metatutor_student_interaction.gif";
	public static final String METATUTOR_EMPTY_DESK_COGTUTOR = "img/metatutor_empty_cogTutor.png";
	public static final String METATUTOR_IMAGE_COGTUTOR = "img/metatutor_cogTutor.png";
	public static final String METATUTOR_STUDENT_INTERACTION_IMAGE_COGTUTOR = "img/metatutor_student_interaction_cogTutor.png";
	public static final String BASELINE_MR_WILLIAMS_IMAGE = "img/baseline_Mr.Williams.png";

	// on paper images: Tasmia
	//public static final String ON_PAPER_IMAGE_LOCATION = "img/on_paper";
	public static final String no_preview_image = "img/DefaultOnPaper.png";
	//public static final String E13 = ON_PAPER_IMAGE_LOCATION+"/E13.png";
	public static final String PAPER_CLOSE = "img/paperClose.png";
	
	public static final String BOARD_IMAGE = "img/board.png";
	public static final String NEXT_EXAMPLE_IMAGE = "img/next.png";
	public static final String PREVIOUS_EXAMPLE_IMAGE = "img/previous.png";
	public static final String FFWD_EXAMPLE_IMAGE = "img/ffwd.png";
	public static final String FBWD_EXAMPLE_IMAGE = "img/fbwd.png";

	public static final String NORMAL_STATUS = "NORMAL";
	public static final String FINISHED_STATUS = "FINISHED";
	public static final String THINK_STATUS = "THINK";
	public static final String ASK_STATUS = "ASK";
	public static final String QUIZ_STATUS = "QUIZ";
	private String status = NORMAL_STATUS;

	private static final int BORDER_WIDTH = 5;

	public static final int QUIZ_COMPLETED_EXIT = 104;
	private static final int MAX_THREAD_COUNT = 1;

	public SimStMessageDialog messageDialog;
	public boolean hasExamples = false;

	/** Thread pool with MAX_THREAD_COUNT available threads at any point of time. */
	private static ExecutorService quizThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

	/**
	 * Singleton Lock object for the quiz thread. Before starting the quiz, the
	 * thread must acquire the lock.
	 */
	public static Object quizLock = new Object();

	private List<String> components = new ArrayList<String>();
	
	private String avatarExpressions = NORMAL_EXPRESSION;
	
	public String getAvatarExpressions() {
		return this.avatarExpressions;	
	}
	
	public void setAvatarExpressions(String exp) {
		this.avatarExpressions = exp;
	}

	public List<String> getComponents() {
		return components;
	}

	public void setComponents(List<String> components) {
		this.components = components;
	}

	public String getStatus() {
		return status;
	}

	static String videoIntroductionName = "";

	public static void setVideoIntroductionName(String name) {
		videoIntroductionName = name;
	}

	static String overviewPageName = "";

	public static void setOverviewPageName(String name) {
		overviewPageName = name;
	}
	public static String getOverviewPageName() {
		return overviewPageName;
	}

	/*
	 * nbarba 01/24/2014: after user has taken a test, don't display a message box
	 * but print results on the blackboard this is to get the text area of the
	 * blackboard. it is set from aplus
	 */
	private JTextArea aplusQuizTextArea;

	public void setAplusQuizTextArea(JTextArea aplusQuizTextArea) {
		this.aplusQuizTextArea = aplusQuizTextArea;

	}

	public int problemCount = 0;

	public int getProblemCount() {
		return problemCount;
	}

	public void setProblemCount(int problemCount) {
		this.problemCount = problemCount;
	}

	// BR_Controller
	BR_Controller brController = null;

	// SimStudent
	SimSt simSt = null;
	MissController missController = null;

	// SimStudentInteractiveLearning
	SimStInteractiveLearning ssInteractiveLearning = null;

	// SimStudent Logger
	public SimStLogger logger;

	// The SimStPeetTutoringPlatform
	SimStPeerTutoringPlatform simStPeerTutoringPlatform = null;

	// Curriculum Browser
	CurriculumBrowser curriculumBrowser = null;

	// The Curriculum Level
	int currentLevel = 1;

	/** Current problem that is beign tutored */
	private String problem = null;

	// Let SimStudent solve the problem
	// private String[] quizProblems = {"x+1=10", "2x=8", "5-x=3", "x/4=2" };
	public ArrayList<String> quizProblems;
	private ArrayList<String> randomizedQuizProblems;
	public ArrayList<ArrayList<String>> allQuizProblems;
	public ArrayList<ArrayList<Integer>> allQuizSections;
	public ArrayList<Integer> quizSections;
	public ArrayList<String> currentQuizSection;
	public ArrayList<String> cogTutorActualcurrentQuizSection;

	public int currentQuizSectionNumber = 0;

	String runType = System.getProperty("appRunType");
	private static File videoFile = null;

	public static void setVideoFile(File file) {
		videoFile = file;
	}

	// private String[] quizProblems = {"x+1=10"};
	ArrayList<String> getQuizProblems() {
		return quizProblems;
	}

	String getQuizProblem(int i) {
		return quizProblems.get(i);
	}

	public String getRandomizedQuizProblem(int i) {
		return randomizedQuizProblems.get(i);
	}

	public void randomizeQuizProblems() {
		randomizedQuizProblems = new ArrayList<String>();
		for (int i = 0; i < currentQuizSection.size(); i++) {
			randomizedQuizProblems.add(GameShowUtilities.generate(currentQuizSection.get(i)));
		}

	}

	public int getCurrentQuizSectionNumber() {
		return currentQuizSectionNumber;
	}

	private ArrayList<String> sections;
	private RegexHashtable componentNames;
	private ArrayList<String> startStateElements;
	private ArrayList<String> availableResources;
	private ArrayList<SimStExample> examples;
	private ArrayList<SimStExample> quizQuestions;
	private Hashtable<String, String> exampleExplanations;
	private Hashtable<String, LinkedList<Explanation>> mistakeExplanations;
	private Hashtable<String, LinkedList<Explanation>> problemChoiceExplanations;
	// Tasmia
	private Hashtable<String, LinkedList<Explanation>> confidenceChoiceExplanations;
	private Hashtable<String, LinkedList<Explanation>> hintExplanations;
	private HashSet<String> validSelections;
	private Map<String, String> startState;
	private HashSet<String> validSelections_bq;
	private HashSet<String> validSelections_baq;
	private Hashtable<String, String> skillNickNames;
	private boolean modelTracer = true;
	private String invalidProblemMsg = "";

	public ArrayList<String> getSections() {
		return sections;
	}
	
	public ArrayList<SimStExample> getQuizQuestions() {
		return this.quizQuestions;
	}
	
	public void setQuizQuestions(ArrayList<SimStExample> ques) {
		this.quizQuestions = ques;
	}

	public ArrayList<String> getStartStateElements() {
		return startStateElements;
	}

	public ArrayList<String> getAvailableResources() {
		return availableResources;
	}

	public ArrayList<SimStExample> getExamples() {
		return examples;
	}

	/**
	 * Method for getting the valid selections for self explanation (selection which
	 * SimStudent is allowed to ask self-explanation questions).
	 * 
	 * @return
	 */
	public HashSet<String> getValidSelections() {
		return validSelections;
	}
	public HashSet<String> getValidSelectionsBQ() {
		return validSelections_bq;
	}
	public HashSet<String> getValidSelectionsBAQ() {
		return validSelections_baq;
	}

	private boolean startStatus = false;

	public boolean isStartStatus() {
		return startStatus;
	}

	private boolean confused = false;

	public boolean isConfused() {
		return confused;
	}

	public static boolean quizPassed = false;

	public static boolean isQuizPassed() {
		return quizPassed;
	}

	public int quizLevel = 0;

	public int getQuizLevel() {
		return quizLevel;
	}

	private SimStConversation conversation;

	public SimStConversation getConversation() {
		return conversation;
	}

	public void setConversation(SimStConversation conv) {
		conversation = conv;
	}

	private long startPleTime;

	public String newSectionTitleBase = "";

	/* boolean indicating if final challenge has been finished */
	boolean hasPassedFinalChallenge = false;

	public boolean getHasPassedFinalChallenge() {
		return this.hasPassedFinalChallenge;
	}

	public void setHasPassedFinalChallenge(boolean value) {
		this.hasPassedFinalChallenge = value;
	}

	/*
	 * Hashtable to store problem solution on the quiz - used to detect behaviour
	 * discrepencies. The key consists of problem name, value is a hash map that has
	 * selection as key and value "<action>$<input>"
	 */
	transient Hashtable<String, LinkedHashMap> quizAttemptsHash = null;

	public Hashtable<String, LinkedHashMap> getQuizAttemptsHash() {
		return quizAttemptsHash;
	}

	public void setQuizAttemptsHash(Hashtable<String, LinkedHashMap> quizAttemptsHash) {
		this.quizAttemptsHash = quizAttemptsHash;
	}

	public void initQuizAttemptHash() {
		Hashtable<String, LinkedHashMap> hashtable = new Hashtable<String, LinkedHashMap>();
		setQuizAttemptsHash(hashtable);
	}

	public int restartClickCount = 0;

	public void incRestartClickCount() {
		restartClickCount++;
	}

	public int getRestartClickCount() {
		return restartClickCount;
	}

	public void resetRestartClickCount() {
		restartClickCount = 0;
	}
	
	ProblemEdge edge;
	public ProblemEdge getEdge() {
		return edge;
	}

	public void setEdge(ProblemEdge edge) {
		this.edge = edge;
	}
	
	Sai sai;
	public Sai getSai() {
		return sai;
	}

	public void setSai(Sai sai) {
		this.sai = sai;
	}
	
	ProblemNode currentNode;
	
	public ProblemNode getCurrentNode() {
		return this.currentNode;
	}
	
	public void setCurrentNode(ProblemNode currentNode) {
		this.currentNode = currentNode;
	}
	
	String query;
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	String undoButtonTextInfo;
	
	public String getUndoButtonTextInfo() {
		return undoButtonTextInfo;
	}

	public void setUndoButtonTextInfo(String undoButtonTextInfo) {
		this.undoButtonTextInfo = undoButtonTextInfo;
	}
	
	String undoMessage;
	
	public String getUndoMessage() {
		return undoMessage;
	}

	public void setUndoMessage(String undoMessage) {
		this.undoMessage = undoMessage;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public SimStPLE(BR_Controller brController, SimStPeerTutoringPlatform simStPeerTutoringPlatform, String problemCountString) {
		setBrController(brController);
		getBrController().activateMissController(false);

		setMissController((MissController) getBrController().getMissController());
		getMissController().setPLEon(true);

		setCurriculumBrowser(new CurriculumBrowser());
		// getCurriculumBrowser().setHTMLSource(CURRICULUM_FILE);

		if (videoIntroductionName != null && videoIntroductionName.length() > 0)
			getCurriculumBrowser().setVideoSource(videoIntroductionName);

		if (overviewPageName != null && overviewPageName.length() > 0)
			getCurriculumBrowser().setHtmlSource(overviewPageName);

		SimSt simSt = getMissController().getSimSt();
		setSimSt(simSt);

		// new StudentConfigurationView().setVisible(true);
		logger = new SimStLogger(getBrController());

		String user = simSt.getUserID();
		if (user == null) {
			user = (String) JOptionPane.showInputDialog(brController.getActiveWindow(), USER_ID_REQUEST_MSG,
					USER_ID_REQUEST_TITLE, JOptionPane.QUESTION_MESSAGE);
			simSt.setUserID(user);
		}
		if (getSimSt().getLoggingEnabled() || getSimSt().getLocalLoggingEnabled()) {
			logger.enableLogging(getSimSt().getLoggingEnabled(), getSimSt().getLocalLoggingEnabled(), user);
			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.PLE_STARTED_ACTION, "");
			startPleTime = System.currentTimeMillis();
		} else {
			logger.enableLogging(getSimSt().getLoggingEnabled(), getSimSt().getLocalLoggingEnabled(), user);
		}

		// if(getSimSt().isSsMetaTutorMode()) {
		// getSimSt().getModelTraceWM().getEventHistory().add(0,
		// getSimSt().getModelTraceWM().new Event(SimStLogger.PLE_STARTED_ACTION));
		// }

		// ssDontShowAllRaWhenTutored
		getSimSt().setDontShowAllRA(true);

		// ssLearnNoLabel
		// Have SimSt learn without labeling the skills
		// getSimSt().setSsLearnNoLabel(true);

		((MissController) getBrController().getMissController()).setSimStPLE(this);
		setSsInteractiveLearning(new SimStInteractiveLearning(getSimSt()));
		setSimStPeerTutoringPlatform(simStPeerTutoringPlatform);

		/*
		 * create the cognitive tutor object that keeps track of the cog tutor specifics
		 */
		if (getSimSt().isSsCogTutorMode())
			setSsCognitiveTutor(new SimStCognitiveTutor(getBrController(), this));

		messageDialog = new SimStMessageDialog(new Frame(), logger);
		messageDialog.setLocationRelativeTo(getSimStPeerTutoringPlatform());

		config();
		
//		if (runType.equalsIgnoreCase("springboot")) {
			this.quizQuestions = new ArrayList<SimStExample>();
//		}
		
		if (problemCountString != null)
			setProblemCount(Integer.parseInt(problemCountString));

		// Load the account information
		if (getSimSt().isSsAplusCtrlCogTutorMode())
			loadAccountInfoAplusCogTutor();
		else
			loadAccountInfo();
		// HashTable instructions - SimSt.java
		// int numInstructions - SimSt.java
		// Vector /* Instruction */ allInstructions - SimSt.java
		// JLabel numStepsDemonstrated - MissConsole.java
		// HashMap /* Rule */ rules - SimSt.java
		// Hashtable foilDataHash - SimSt.java
		// List<Instruction> negativeExamples - SimSt.java

		// If the serialized object does not exist then load the instructions from file.
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Loading the instructions now");
		long startTime = System.currentTimeMillis();
		boolean loadingSucceeded = false;
		loadingSucceeded = getMissController().loadInstDeSerialize();
		
		if (!loadingSucceeded) {
			if (getSimStPeerTutoringPlatform() != null)
				getSimStPeerTutoringPlatform().showWaitMessage(true);
			// jinyul - Try to load instructions.txt from previous run.
			getMissController().autoLoadInstructions();
			if (getSimStPeerTutoringPlatform() != null)
				getSimStPeerTutoringPlatform().showWaitMessage(false);
		}

		long endTime = System.currentTimeMillis();
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Time to load the instructions was: " + (endTime - startTime));

		// initialize the QuizAttemptHash
		initQuizAttemptHash();

		// trace.out("miss", "Exiting SimStPLE");
		// readComponentNames();
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Restore Model Tracer Working Memory");
		brController.getMissController().getSimSt().restoreMTWMState();
	}
	
	public SimStPLE(BR_Controller brController, SimStPeerTutoringPlatform simStPeerTutoringPlatform) {
		this(brController, simStPeerTutoringPlatform, null);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/**
	 * Method that dynamically returns the ID of the final challenge section
	 * 
	 * @param sections
	 * @return
	 */
	public int getFinalChallengeID(ArrayList<String> sections) {
		int returnVal = 0;
		for (int i = 0; i < sections.size(); i++) {
			if (sections.get(i).equalsIgnoreCase("-Final Challenge")) {
				returnVal = i;
				break;
			}
		}
		return returnVal;
	}

	public static String passedProblemsList = "NA,";

	public void loadAccountInfoAplusCogTutor() {

		if (trace.getDebugCode("miss"))
			trace.out("miss", "loadAccountInfo: " + getSimSt().getUserID());
		String accountInfo = getSimSt().getUserID() + ".account"; // accountInfo = rohan.account
		File accountFile = null;
		boolean successful = false;

		// Check if the application is running locally or using Webstart
		if (!getMissController().getSimSt().isWebStartMode()) { // Running locally
//			accountFile = new File(brController.getMissController().getSimSt().getProjectDir() + "/" + accountInfo);
			accountFile = new File(brController.getMissController().getSimSt().getLogDirectory() + "/" + accountInfo);
		} else {
			try {

				// Key associated when retrieving the .account file is the
				// getSimSt().getUserID()+.account

				successful = getMissController().getStorageClient().retrieveFile(getSimSt().getUserID() + ".A2account",
						accountInfo, WebStartFileDownloader.SimStWebStartDir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (successful) {
				accountFile = new File(WebStartFileDownloader.SimStWebStartDir + accountInfo);
			}
		}

		if (accountFile != null && accountFile.exists()) {
			setFirstTimeAPLUS(false);
			try {
				// initialize the quiz level to zero (just in case)
				quizLevel = 0;

				BufferedReader read = new BufferedReader(new FileReader(accountFile));

				String charName = read.readLine();
				SimSt.setSimStName(charName);

				String imgName = read.readLine();
				getSimSt().setSimStImage(imgName);
				STUDENT_IMAGE = imgName;

				String tmp = read.readLine();
				passedProblemsList = read.readLine();
				if (passedProblemsList != null && passedProblemsList.length() > 1) {
					String[] parts = passedProblemsList.split(",");
					if (!parts[parts.length - 1].equalsIgnoreCase("NA")) {
						cogTutorCurrentProblem = Integer.parseInt(parts[parts.length - 1]);
						cogTutorCurrentProblem++;
					}

				}

				if (tmp != null && tmp.length() > 0) {
					// figure out which quiz it belongs to
					currentOverallProblem = Integer.parseInt(tmp);
					currentProblem = currentOverallProblem;

					trace.out("ss", "Overall: " + currentOverallProblem);

					int totalNumberOfProblems = 0;
					for (int j = 0; j < allQuizProblems.size(); j++)
						totalNumberOfProblems = totalNumberOfProblems + allQuizProblems.get(j).size();

					// System.out.println(allQuizProblems);
					int numberOfProblemsInFinalChallenge = allQuizProblems.get(1).size();

					if (currentProblem >= totalNumberOfProblems) { // if we must add a new final challenge section

						int howMany2Add = 1
								+ (currentProblem - totalNumberOfProblems) / numberOfProblemsInFinalChallenge;
						int start = allQuizSections.get(allQuizSections.size() - 1).get(0) + 1;

						for (int k = 0; k < howMany2Add; k++) {
							allQuizProblems.add(allQuizProblems.get(1));
							ArrayList<Integer> tmpQuizSectionNumber = new ArrayList<Integer>();

							for (int i = 0; i < numberOfProblemsInFinalChallenge; i++)
								tmpQuizSectionNumber.add(start);

							// add the new quiz section
							allQuizSections.add(tmpQuizSectionNumber);
							start++;

						}
					}

					while (quizLevel < allQuizProblems.size()
							&& currentProblem >= allQuizProblems.get(quizLevel).size()) {
						// Is not on the correct quiz Level yet
						currentProblem -= allQuizProblems.get(quizLevel).size();
						quizLevel++;
						getSimStPeerTutoringPlatform().addTrophy(false);

					}

					// We don't want to restart, now we have multiple final challenges...
					// if(quizLevel >= allQuizProblems.size())
					// quizLevel = 0;
					quizProblems = allQuizProblems.get(quizLevel);
					quizSections = allQuizSections.get(quizLevel);
					currentQuizSection = new ArrayList<String>();
					cogTutorActualcurrentQuizSection = new ArrayList<String>();
					currentQuizSectionNumber = quizSections.get(currentProblem);

					for (int i = 0; i < quizProblems.size(); i++) {
						if (quizSections.get(i) == currentQuizSectionNumber) {
							// always generate new problems after final challenge.
							// currentQuizSection.add(quizProblems.get(i));
							String actualProb = GameShowUtilities.generate(quizProblems.get(i));
							// currentQuizSection.add(GameShowUtilities.generate(quizProblems.get(i)));
							currentQuizSection.add(actualProb);

						}
					}

					int addedSectionCnt = 2; // this means that first added final challenge will be final challenge 2
					// newSectionTitleBase=sections.get(sections.size()-1);
					newSectionTitleBase = sections.get(3);
					if (newSectionTitleBase.startsWith("-"))
						newSectionTitleBase = newSectionTitleBase.substring(1);

					while (currentQuizSectionNumber >= sections.size()) {
						// String newSectionTitle=newSectionTitleBase +" " +addedSectionCnt;
						String newSectionTitle = "Final Challenge" + " " + addedSectionCnt;
						sections.add(newSectionTitle);
						addedSectionCnt++;
						setHasPassedFinalChallenge(true);
					}

					// add appropriate section medals
					// divide by getProblemsPerQuiz to give 1 medal/section
					if (getSimStPeerTutoringPlatform() != null) {
						getSimStPeerTutoringPlatform().augmentMedals(currentProblem, false);
					}

				}

				read.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			/*
			 * When in CogTutor mode we do not want to display the avatar designer because
			 * there is no SimStudent involved
			 */
			if (!getSimSt().isSsCogTutorMode()) {
				StudentAvatarDesigner.createAndShowGUI(simStPeerTutoringPlatform, simSt.getUserID());
			} else {
				SimSt.setSimStName("Practice");
				SimStPLE.saveAccountFile(simSt.getUserID() + ".account");
			}
		}

	}

	public void loadAccountInfo() {
		if (trace.getDebugCode("miss"))
			trace.out("miss", "loadAccountInfo: " + getSimSt().getUserID());
		String accountInfo = getSimSt().getUserID() + ".account"; // accountInfo = rohan.account
		File accountFile = null;
		boolean successful = false;

		// Check if the application is running locally or using Webstart
		if (!getMissController().getSimSt().isWebStartMode()) { // Running locally
//			accountFile = new File(brController.getMissController().getSimSt().getProjectDir() + "/" + accountInfo);
			accountFile = new File(brController.getMissController().getSimSt().getLogDirectory()+ "/" + accountInfo);
		} else {
			try {

				// Key associated when retrieving the .account file is the
				// getSimSt().getUserID()+.account
				successful = getMissController().getStorageClient().retrieveFile(getSimSt().getUserID() + ".account",
						accountInfo, WebStartFileDownloader.SimStWebStartDir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (successful) {
				accountFile = new File(WebStartFileDownloader.SimStWebStartDir + accountInfo);
			}
		}

		Boolean accountInformationExists = false;
		String tmp = "";
		if (runType.equals("springBoot")) {
			String charName = getSimSt().getUserID();
			SimSt.setSimStName(charName);
			STUDENT_IMAGE = getSimSt().getSimStImage();
			tmp = String.valueOf(getProblemCount());
			accountInformationExists = !charName.equals("");
		} else {
			BufferedReader read;
			try {
				read = new BufferedReader(new FileReader(accountFile));
				String charName = read.readLine();
				SimSt.setSimStName(charName);

				String imgName = read.readLine();
				getSimSt().setSimStImage(imgName);
				STUDENT_IMAGE = imgName;

				tmp = read.readLine();
				accountInformationExists = (accountFile != null && accountFile.exists());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

//    	if(accountFile != null && accountFile.exists()) {
		if (accountInformationExists) {
			initQuizInfo(tmp);

//    			read.close();

//    		} catch (FileNotFoundException e) {
//    			e.printStackTrace();
//    		} catch (IOException e) {
//    			e.printStackTrace();
//    		}
			/*
			 * int result = JOptionPane.showConfirmDialog(null,
			 * "Would you like to change your student's look or name?"); if(result ==
			 * JOptionPane.YES_OPTION)
			 * StudentAvatarDesigner.createAndShowGUI(simStPeerTutoringPlatform,
			 * simSt.getUserID());
			 */
		} else {
			/*
			 * When in CogTutor mode we do not want to display the avatar designer because
			 * there is no SimStudent involved
			 */
			if (!runType.equals("springBoot")) {
				if (!getSimSt().isSsCogTutorMode()) {
					StudentAvatarDesigner.createAndShowGUI(simStPeerTutoringPlatform, simSt.getUserID());
				} else {
					SimSt.setSimStName("Practice");
					SimStPLE.saveAccountFile(simSt.getUserID() + ".account");				}
			}
		}
	}
	
	public void initQuizInfo(String currentOverallProblemString) {
		setFirstTimeAPLUS(false);
//		try {
		// initialize the quiz level to zero (just in case)
		quizLevel = 0;

//			BufferedReader read = new BufferedReader(new FileReader(accountFile));
//
//			String charName = read.readLine();
//			SimSt.setSimStName(charName);
//
//			String imgName = read.readLine();
//			getSimSt().setSimStImage(imgName);
//			STUDENT_IMAGE = imgName;
//
//			String tmp = read.readLine();
		if (currentOverallProblemString != null && currentOverallProblemString.length() > 0) {
			// figure out which quiz it belongs to
			currentOverallProblem = Integer.parseInt(currentOverallProblemString);
			currentProblem = currentOverallProblem;

			trace.out("ss", "Overall: " + currentOverallProblem);

			int totalNumberOfProblems = 0;
			for (int j = 0; j < allQuizProblems.size(); j++)
				totalNumberOfProblems = totalNumberOfProblems + allQuizProblems.get(j).size();

			// System.out.println(allQuizProblems);
			int numberOfProblemsInFinalChallenge = allQuizProblems.get(1).size();

			if (currentProblem >= totalNumberOfProblems) { // if we must add a new final challenge section

				int howMany2Add = 1 + (currentProblem - totalNumberOfProblems) / numberOfProblemsInFinalChallenge;
				int start = allQuizSections.get(allQuizSections.size() - 1).get(0) + 1;

				for (int k = 0; k < howMany2Add; k++) {
					allQuizProblems.add(allQuizProblems.get(1));
					ArrayList<Integer> tmpQuizSectionNumber = new ArrayList<Integer>();

					for (int i = 0; i < numberOfProblemsInFinalChallenge; i++)
						tmpQuizSectionNumber.add(start);

					// add the new quiz section
					allQuizSections.add(tmpQuizSectionNumber);
					start++;

				}
			}

			while (quizLevel < allQuizProblems.size() && currentProblem >= allQuizProblems.get(quizLevel).size()) {
				// Is not on the correct quiz Level yet
				currentProblem -= allQuizProblems.get(quizLevel).size();
				quizLevel++;
				if (!runType.equals("springBoot"))
					getSimStPeerTutoringPlatform().addTrophy(false);

			}

			// We don't want to restart, now we have multiple final challenges...
			// if(quizLevel >= allQuizProblems.size())
			// quizLevel = 0;
			quizProblems = allQuizProblems.get(quizLevel);
			quizSections = allQuizSections.get(quizLevel);
			// System.out.println(" Quiz Sections completed : "+quizSections +" "+" Quiz
			// Level "+quizLevel);
			currentQuizSection = new ArrayList<String>();
			currentQuizSectionNumber = quizSections.get(currentProblem);

			// if(trace.getDebugCode("miss"))trace.out("miss", "Quiz level passed: " +
			// currentQuizSectionNumber);

			// brController.getMissController().getSimSt().getModelTraceWM().setQuizLevelPassed(currentQuizSectionNumber);

			// JOptionPane.showMessageDialog(null, "faskelo2 quiz level " +
			// currentQuizSectionNumber);
			if (getSimSt().isSsCogTutorMode())
				this.getSsCognitiveTutor().getSimStLMS().setCurrentSection(currentQuizSectionNumber);

			for (int i = 0; i < quizProblems.size(); i++) {
				if (quizSections.get(i) == currentQuizSectionNumber) {
					// always generate new problems after final challenge.
					// currentQuizSection.add(quizProblems.get(i));
					currentQuizSection.add(GameShowUtilities.generate(quizProblems.get(i)));
				}
			}

			int addedSectionCnt = 2; // this means that first added final challenge will be final challenge 2
			// newSectionTitleBase=sections.get(sections.size()-1);
			newSectionTitleBase = sections.get(3);
			if (newSectionTitleBase.startsWith("-"))
				newSectionTitleBase = newSectionTitleBase.substring(1);

			while (currentQuizSectionNumber >= sections.size()) {
				// String newSectionTitle=newSectionTitleBase +" " +addedSectionCnt;
				String newSectionTitle = "Final Challenge" + " " + addedSectionCnt;
				sections.add(newSectionTitle);
				addedSectionCnt++;
				setHasPassedFinalChallenge(true);
			}

			// add appropriate section medals
			// divide by getProblemsPerQuiz to give 1 medal/section
			if (getSimStPeerTutoringPlatform() != null)
				getSimStPeerTutoringPlatform().augmentMedals(currentProblem, false);

		}
	}

	public boolean firstTimeAPLUS = true;

	public void setFirstTimeAPLUS(boolean flag) {
		this.firstTimeAPLUS = flag;
	}

	public boolean isFirstTimeAPLUS() {
		return this.firstTimeAPLUS;
	}

	/**
	 * Check if a problem is on the quiz attempts primary hash
	 * 
	 * @param problemName
	 * @param quizAttemptsHash
	 * @return
	 */
	public boolean isFailedQuizProblem(String problemName, Hashtable<String, LinkedHashMap> quizAttemptsHash) {
		return quizAttemptsHash.containsKey(problemName);
	}

	/**
	 * Method that checks if there is an inconsistency between quiz and tutoring for
	 * the same problem (i.e. SimStudent suggesting something different), and if so
	 * alerts the SimStConversation to use appropriate language.
	 * 
	 * @param problemName
	 * @param ran
	 * @return
	 */
	public boolean checkForQuizTutoringBehaviourDiscrepency(String problemName, RuleActivationNode ran) {
		boolean returnValue = false;

		if (isFailedQuizProblem(problemName, getQuizAttemptsHash())) {
			// get the quiz solution for this problem
			LinkedHashMap quizSolution = getQuizAttemptsHash().get(brController.getProblemName());
			// if SimStudent made a suggestion for that selection check if it matches the
			// quiz...
			if (ran != null && quizSolution.containsKey(ran.getActualSelection())) {
				String action_input = (String) quizSolution.get(ran.getActualSelection());
				String[] parts = action_input.split("\\$"); // value is in format <action>$<input>
				if (!ran.getActualInput().equals(parts[1])
						&& !this.getConversation().getBehaviourDiscrepencyBroughtUp()) {
					// notify conversations that quiz quiz-tutoring inconsistency has been detected
					this.getConversation().setBehaviourDiscrepency(true);
					returnValue = true;
				}
			} else { // if SimStudent is stuck (no rule activations)
				if (brController.getProblemModel().getEdgeCount() != quizSolution.size()
						&& !this.getConversation().getBehaviourDiscrepencyBroughtUp()) {
					this.getConversation().setBehaviourDiscrepency(true);
					returnValue = true;
				}
			}
		}

		return returnValue;

	}

	public void config() {
		componentNames = new RegexHashtable();
		startStateElements = new ArrayList<String>();
		examples = new ArrayList<SimStExample>();
		exampleExplanations = new Hashtable<String, String>();
		skillNickNames = new Hashtable<String, String>();
		mistakeExplanations = new Hashtable<String, LinkedList<Explanation>>();
		problemChoiceExplanations = new Hashtable<String, LinkedList<Explanation>>();
		// Tasmia
		//if(getSimSt().isCTIFollowupInquiryMode())
			//CONFIG_FILE = "simSt-config-cti.txt";
		confidenceChoiceExplanations = new Hashtable<String, LinkedList<Explanation>>();
		hintExplanations = new Hashtable<String, LinkedList<Explanation>>();
		readConfigFile();
		conversation = new SimStConversation(brController, "simSt-speech.txt");
		
		// Tasmia:
		// if you want to ask more questions when both tutor and tutee agree with each other,
		// add -ssBothAgreeSpeechGetterClass SimStAlgebraV8.SimStBothAgreeSpeech in the program arguements. 
		if(getSimSt().isCTIFollowupInquiryMode() && getSimSt().isbothAgreeSpeechGetterClassDefined())
			conversation.processBothAgreeSpeechFile("simSt-both-agree-speech.txt");
	}

	// Reads the configuration file and applies the items in it to their categories
	// File has sections start stage elements, component names, quiz problems and
	// examples,
	// separated by blank lines and started by header with section name
	public void readConfigFile() {

		// worked like that...
		// String file = simSt.getProjectDir() + "/"+CONFIG_FILE;
		// BufferedReader reader=null;

		BufferedReader reader = null;
		String file = null;
		InputStreamReader isr = null;

		if (runType.equals("springBoot")) {
			file = simSt.getProjectDir() + "/" + CONFIG_FILE;
		} else {
			file = WebStartFileDownloader.SimStAlgebraPackage + "/" + CONFIG_FILE;
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream is = cl.getResourceAsStream(file);
			isr = new InputStreamReader(is);
		}

//    	ClassLoader cl = this.getClass().getClassLoader();
//    	InputStream is = cl.getResourceAsStream(file);
//    	InputStreamReader isr = new InputStreamReader(is);	
//    	BufferedReader reader=null;

		try {
			// reader=new BufferedReader(new FileReader(file));
			if (runType.equalsIgnoreCase("springBoot")) {
				reader = new BufferedReader(new FileReader(file));
			} else {
				reader = new BufferedReader(isr);
			}
			String line = reader.readLine();

			while (line != null) {
				if (line.equals(START_STATE_ELEMENTS_HEADER)) {
					configStartState(reader);
				} else if (line.equals(FOA_ELEMENTS_HEADER)) {
					configFoAElements(reader);
				} else if (line.equals(COMPONENT_NAMES_HEADER)) {
					configCompNames(reader);
				} else if (line.equals(QUIZ_PROBLEMS_HEADER)) {
					configQuiz(reader);
				} else if (line.equals(EXAMPLES_HEADER)) {
					hasExamples = true;
					configExamples(reader);
				} else if (line.equals(MISTAKE_EXPLANATION_HEADER)) {
					configMistakeExplanations(reader);
				} else if (line.equals(PROBLEM_CHOICE_EXPLANATION_HEADER)) {
					configProblemChoiceExplanations(reader);
				} else if (line.equals(HINT_EXPLANATION_HEADER)) {
					configHintExplanations(reader);
				} else if (line.equals(VALID_SELECTIONS_FOR_SE)) {
					validSelections = new HashSet<String>();
					configValidSelections(reader, validSelections);
				} 
				// Added by Tasmia: Updating the permissible selections from config.txt for asking brainstorming questions and both agree questions.
				else if (line.equals(VALID_SELECTIONS_FOR_BQ)) {
					validSelections_bq = new HashSet<String>();
					configValidSelections(reader, validSelections_bq);
				} else if (line.equals(VALID_SELECTIONS_FOR_BAQ)) {
					validSelections_baq = new HashSet<String>();
					configValidSelections(reader, validSelections_baq);
				}
				else if (line.equals(CONFIDENCE_DEMONSTRATION_HEADER)) {
					configConfidenceExplanations(reader);
				}
				else if (line.equals(SKILL_NICKNAMES)) {
					configSkillNickNames(reader);
				} 
				// ended edits by Tasmia
				else if (line.equals(SECTIONS_HEADER)) {
					configSections(reader);
				} else if (line.equals(PROBLEM_DELIMITER_HEADER)) {

					configProblemDelimiter(reader);

				}
				if (line != null) {
					line = reader.readLine();
				}
			}

		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file: " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file: " + e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				logger.simStLogException(e);
			}
		}
	}

	// Read from the config file which states must be filled in to start
	public void configStartState(BufferedReader reader) {

		try {
			String line = reader.readLine();
			while (line != null && line.length() > 0) // Blank line starts next section
			{
				startStateElements.add(line);
				if (!runType.equalsIgnoreCase("springBoot")) {
					addStartStateListener(line);
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (start state): " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (start state): " + e.getMessage());
		}
	}

	public void configFoAElements(BufferedReader br) {
		try {
			String line = br.readLine();
			while (line != null && line.length() > 0) {
				if (!runType.equalsIgnoreCase("springBoot")) {
					if (getSimSt().isSsAplusCtrlCogTutorMode())
						components.add(line);
					addFoAStateListener(line);
				} else {
					components.add(line);
				}
				line = br.readLine();
			}
			components.add("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Read from the config file what the comm widgets are more naturally called
	public void configCompNames(BufferedReader reader) {

		try {
			String line = reader.readLine();
			while (line != null && line.length() > 0) // Blank line starts next section
			{
				int index = line.indexOf(' ');
				String regex = line.substring(0, index);
				String name = line.substring(index + 1);
				componentNames.put(regex, name);
				line = reader.readLine();
			}
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (comp names): " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (comp names): " + e.getMessage());
		}
	}
	
	// Added by Tasmia
	// Read the Jess Oracle production rule file's skills and their nicknames
	public void configSkillNickNames(BufferedReader reader){
		try {
			String line = reader.readLine();
			while (line != null && line.length() > 0) // Blank line starts next section
			{
				int index = line.indexOf(':');
				String skill = line.substring(0, index);
				String nickname = line.substring(index + 1);
				skillNickNames.put(skill, nickname);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (skill nicknames): " + e.getMessage());
		}
	}

	// Read from the config file the delimiter that is used in the problem name
	// e.g. = for equations, /+ for fraction addition.
	public void configProblemDelimiter(BufferedReader reader) {

		try {
			String line = reader.readLine();
			simSt.setProblemDelimiter(line);
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (comp names): " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (comp names): " + e.getMessage());
		}
	}

	// Read sections from config file
	public void configSections(BufferedReader reader) {
		try {
			sections = new ArrayList<String>();

			String line = reader.readLine();
			while (line != null && line.length() > 0) // Blank line starts next section
			{
				sections.add(line);
				line = reader.readLine();

			}

			trace.out("ss", "Sections-------------------------------");
			for (int i = 0; i < sections.size(); i++)
				trace.out("ss", sections.get(i));
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (sections): " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (sections): " + e.getMessage());
		}
	}

	// Read quiz questions from config file
	public void configQuiz(BufferedReader reader) {
		try {
			int segment = 0;
			allQuizProblems = new ArrayList<ArrayList<String>>();
			allQuizSections = new ArrayList<ArrayList<Integer>>();

			allQuizProblems.add(new ArrayList<String>());
			allQuizSections.add(new ArrayList<Integer>());
			String line = reader.readLine();
			while (line != null && line.length() > 0) // Blank line starts next section
			{
				if (line.equals(".")) {
					segment++;
					allQuizProblems.add(new ArrayList<String>());
					allQuizSections.add(new ArrayList<Integer>());
					// trace.out("ss", "New segment");
				} else {
					String[] parts = line.split(":");
					allQuizProblems.get(segment).add(parts[1]);
					allQuizSections.get(segment).add(Integer.parseInt(parts[0]));
					// trace.out("ss", "New Problem: "+line);
				}
				line = reader.readLine();

			}

			quizProblems = allQuizProblems.get(quizLevel);
			quizSections = allQuizSections.get(0);
			currentQuizSection = new ArrayList<String>();

			cogTutorActualcurrentQuizSection = new ArrayList<String>();

			int currentQuizSectionNumber = 0;
			for (int i = 0; i < quizProblems.size(); i++) {
				if (quizSections.get(i) == currentQuizSectionNumber)
					currentQuizSection.add(quizProblems.get(i));
			}

		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (quiz): " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (quiz): " + e.getMessage());
		}
	}

	// Read filled in examples from config file
	public void configExamples(BufferedReader reader) {

		int numExamples = 0;
		try {
			String line = reader.readLine();
			SimStExample example = new SimStExample();
			numExamples++;
			while (line != null && line.length() > 0) // Blank line starts next section
			{
				if (line.startsWith("section:")) {
					String section = line.substring(line.indexOf(':') + 1);
					example.setSection(Integer.parseInt(section));
				} else if (line.startsWith("title:")) {
					String title = line.substring(line.indexOf(':') + 1);
					example.setTitle(title);
				} else if (line.startsWith("text:")) {
					String explanation = line.substring(line.indexOf(':') + 1);
					example.setExplanation(explanation);
				} else if (line.startsWith("shortDescription:")) {
					String[] hint_image = line.split(EXAMPLE_VALUE_MARKER);
					//String hint = line.substring(line.indexOf(':') + 1);
					String hint = hint_image[1].trim();
					example.setShortDescription(hint);
					if(hint_image.length==3) {
						example.addOnPaperImageNames("shortDescription", hint_image[2].trim());
					}
				} 
				// Tasmia added 
				/*else if (line.startsWith("on_paper_images:") && simSt.isSimStStrategyRevealMode()) {
					String image_names = line.substring(line.indexOf(':') + 1);
					ArrayList<String> img_names = new ArrayList<String>(Arrays.asList(image_names.split(",")));
					for (int i=0; i<img_names.size(); i++) {
						String[] selection_name = img_names.get(i).split("-");
						example.addOnPaperImageNames(selection_name[0].trim(), selection_name[1].trim());
					}
					
				}*/ 
				 else if (line.equals(".")) {
					numExamples++;
					examples.add(example);
					example = new SimStExample();
				} else {
					// splits the location from the contents
					String[] values = line.split(EXAMPLE_VALUE_MARKER);

					// values[0] is selection, values[1] is input, values[2] is tooltip
					if (values.length == 3) {
						example.addStep(values[0], values[1], "", "");
					} else if (values.length == 4) {
						example.addStep(values[0], values[1], values[2], values[3]);
					} else if (values.length >= 4 && values.length <=5) {
						if (values[4].trim().equals("correct"))
							example.addStep(values[0], values[1], values[2], values[3], true);
						else if (values[4].trim().equals("incorrect"))
							example.addStep(values[0], values[1], values[2], values[3], false);
						else
							example.addStep(values[0], values[1], values[2], values[3]);
					}
					else if (values.length >= 5) {
						if (values[4].trim().equals("correct"))
							example.addStep(values[0], values[1], values[2], values[3], 1, values[5]);
						else if (values[4].trim().equals("incorrect"))
							example.addStep(values[0], values[1], values[2], values[3], 0, values[5]);
						else
							example.addStep(values[0], values[1], values[2], values[3], -1, values[5]);
					}
				}
				line = reader.readLine();
			}
			examples.add(example);

		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (examples): " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (examples): " + e.getMessage());
		}
	}

	// Read mistake explanations and their conditions for display from config file
	public void configMistakeExplanations(BufferedReader reader) {
		configExplanations(reader, mistakeExplanations);
	}
	// Read mistake explanations and their conditions for display from config file
	public void configProblemChoiceExplanations(BufferedReader reader) {
		configExplanations(reader, problemChoiceExplanations);
	}
	public void configConfidenceExplanations(BufferedReader reader) {
		configExplanations(reader, confidenceChoiceExplanations);
	}

	// Read mistake explanations and their conditions for display from config file
	public void configHintExplanations(BufferedReader reader) {
		configExplanations(reader, hintExplanations);
	}

	private void configExplanations(BufferedReader reader, Hashtable<String, LinkedList<Explanation>> explanations) {

		try {
			String line = reader.readLine();

			while (line != null && line.length() > 0) // Blank line starts next section
			{
				LinkedList<Explanation> questionChoices = new LinkedList<Explanation>();

				if (line.startsWith("text:")) {
					String question = line.substring(line.indexOf(':') + 1);
					explanations.put(question, questionChoices);
					line = reader.readLine();
				}
				while (line != null && !line.equals(".") && line.length() > 0) // . on a line by itself starts next
																				// example
				{

					// : splits line into conditions and explanation.
					String[] parts = line.split(":");
					if (parts.length == 2) {
						String explain = parts[1];
						// Individual conditions are separated by ,'s. They are of the form sreqString
						// for a
						// string (reqString) required in the skill name, s!reqString for a string
						// (reqString)
						// required to be not present in the skill name, or sstr1|str2 for a choice of
						// strings
						// (str1 or str2) either of which can be present in the skill name. p (problem
						// step),
						// i (input) are also valid prefixes.
						String[] conditions = parts[0].split(",");
						questionChoices.add(new Explanation(conditions, explain));
					}
					line = reader.readLine();

				}

				if (line == null || line.length() == 0)
					break;
				line = reader.readLine();
			}
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (explanations): " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (explanations): " + e.getMessage());
		}

	}

	/**
	 * Method for reading from the config file the valid selections for self
	 * explanation (selections which SimStudent is allowed to ask self-explanation
	 * questions).
	 * Also adding the valid selections for brainstorming questions when both tutor tutee are stuck
	 * and for both agree questions when they both agree.
	 * 
	 * @param reader
	 */
	public void configValidSelections(BufferedReader reader, HashSet<String> validSelections) {
		try {
			//validSelections = new HashSet<String>();

			String line = reader.readLine();
			while (line != null && line.length() > 0) // Blank line starts next section
			{
				validSelections.add(line);
				line = reader.readLine();
			}
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Unable to read config file (valid selections) : " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Unable to read config file (valid selections): " + e.getMessage());
		}
	}

	class Explanation {
		List<String> skillConditions;
		List<String> problemConditions;
		List<String> inputConditions;
		String explanation;

		// Conditions are preceded by s for conditions that must be true of skillnames,
		// p for conditions that
		// must be true of problem steps, and i for conditions that must by true of an
		// input string
		Explanation(String[] conditions, String exp) {
			skillConditions = new LinkedList<String>();
			// problemConditions = new LinkedList<String>();
			// inputConditions = new LinkedList<String>();
			for (int i = 0; i < conditions.length; i++) {
				String condition = conditions[i];
				/*
				 * if(condition.startsWith("s")) skillConditions.add(condition.substring(1));
				 * else if(condition.startsWith("p"))
				 * problemConditions.add(condition.substring(1)); else
				 * if(condition.startsWith("i")) inputConditions.add(condition.substring(1));
				 */
				skillConditions.add(condition);
			}
			explanation = exp;
		}

		// A single part of a condition matches if the necessary string is contained in
		// the given string
		// or is not contained, if preceded by !
		boolean singleMatch(String contains, String containedIn) {
			if (contains.startsWith("!")) {
				return !containedIn.contains(contains.substring(1));
			}
			return containedIn.contains(contains);
		}

		// Determine if this mistake explanation applies to the given skill, problem
		// step and input
		boolean matches(String skill, String problem, String input) {
			// All of any listed skill conditions must apply - if we find any that do not
			// apply, return false
			for (String str : skillConditions) {
				if (str.contains(";")) {
					// With an or on a single condition, check to see if any of the parts apply, and
					// if none do
					// the condition fails and it is not a match.
					String[] ors = str.split(";");
					boolean match = false;

					for (int i = 0; i < ors.length; i++) {
						if (singleMatch(ors[i], skill))
							match = true;
					}
					if (!match)
						return false;

				} else {
					if (!singleMatch(str, skill))
						return false;
				}
			}
			/*
			 * //All of any listed problem step conditions must apply - if we find any that
			 * do not apply, return false for(String str:problemConditions) {
			 * if(str.contains("|")) { //With an or on a single condition, check to see if
			 * any of the parts apply, and if none do //the condition fails and it is not a
			 * match. String[] ors = str.split("|"); boolean match = false; for(int
			 * i=0;i<ors.length;i++) { if(singleMatch(ors[i],problem)) match = true; }
			 * if(!match) return false;
			 * 
			 * } else { if(!singleMatch(str,problem)) return false; } } //All of any listed
			 * input conditions must apply - if we find any that do not apply, return false
			 * for(String str:inputConditions) { //With an or on a single condition, check
			 * to see if any of the parts apply, and if none do //the condition fails and it
			 * is not a match. if(str.contains("|")) { String[] ors = str.split("|");
			 * boolean match = false; for(int i=0;i<ors.length;i++) {
			 * if(singleMatch(ors[i],input)) match = true; } if(!match) return false;
			 * 
			 * } else { if(!singleMatch(str,input)) return false; } }
			 */
			// If we have made it this far, no conditions which do not match, thus it
			// matches.
			return true;
		}
	}

	public class QuestionAnswers {
		String question;
		List<String> answers;

		public QuestionAnswers(String q, List<String> a) {
			question = q;
			answers = a;
		}

		public String getQuestion() {
			return question;
		}

		public List<String> getAnswers() {
			return answers;
		}
	}

	// Get a list of all mistakes which could apply to the given skill, problem step
	// and given input
	public QuestionAnswers getMatchingMistakeExplanation(String skill, Sai sai, String problem,
			RuleActivationNode ran) {

		Object[] questions = mistakeExplanations.keySet().toArray();
		if (questions.length == 0) {
			return null;
		}

		String question = questions[(int) (Math.random() * questions.length)].toString();
		// trace.err("going for question " + question);
		List<String> matches = null;
		matches = getMatching(mistakeExplanations.get(question), skill, sai, problem, ran);

		// trace.err("matches are " + question);
		question = replaceMatchSymbols(question, skill, sai, problem, ran);
		// trace.err("question now is" + question);
		if (question == null)
			return null;

		QuestionAnswers qa = new QuestionAnswers(question, matches);
		return qa;
	}

	// Get a list of all reasons to choose a given problem
	public QuestionAnswers getMatchingProblemChoiceExplanation(String problem) {
		Object[] questions = problemChoiceExplanations.keySet().toArray();
		if (questions.length == 0) {
			return null;
		}
		String question = questions[(int) (Math.random() * questions.length)].toString();
		List<String> matches = getMatching(problemChoiceExplanations.get(question), "", null, problem, null);
		question = replaceMatchSymbols(question, "", null, problem, null);

		if (question == null)
			return null;

		QuestionAnswers qa = new QuestionAnswers(question, matches);
		return qa;
	}
	
	// Get a list of all confidence options to choose when tutee says it is stuck.
	// We use this function only when we need to distinguish how confident tutor is about knowing the next step or he is stuck.
	// Currently APLUS does not let tutor express if they are stuck as well.
	// Only gets activated if APLUS is in CTI followup mode.
		public QuestionAnswers getMatchingConfidenceChoiceExplanation() {
			Object[] questions = confidenceChoiceExplanations.keySet().toArray();
			if (questions.length == 0) {
				return null;
			}
			String question = questions[(int) (Math.random() * questions.length)].toString();
			List<String> matches = new ArrayList<String>();
			List <Explanation> exp = confidenceChoiceExplanations.get(question);
			for(int i=0; i<exp.size(); i++) {
				matches.add(exp.get(i).explanation);
			}
			//question = replaceMatchSymbols(question, "", null, problem, null);

			if (question == null)
				return null;

			QuestionAnswers qa = new QuestionAnswers(question, matches);
			return qa;
		}

	// Get a list of all hints which could apply to the given skill, problem step
	// and given input
	public QuestionAnswers getMatchingHintExplanation(String skill, Sai sai, String problem) {
		Object[] questions = hintExplanations.keySet().toArray();
		if (questions.length == 0) {
			return null;
		}
		String question = questions[(int) (Math.random() * questions.length)].toString();
		List<String> matches = getMatching(hintExplanations.get(question), skill, sai, problem, null);
		question = replaceMatchSymbols(question, skill, sai, problem, null);

		if (question == null)
			return null;

		QuestionAnswers qa = new QuestionAnswers(question, matches);
		return qa;
	}

	public List<String> getMatching(List<Explanation> explanations, String skill, Sai sai, String problem,
			RuleActivationNode ran) {
		List<String> matches = new LinkedList<String>();
		String input = "";
		if (sai != null) {
			input = sai.getI();
		}
		matches.add(SELECT_OPTION);

		// trace.err("inside, explanations is " + explanations);
		for (Explanation me : explanations) {

			// If it's a match, include it in the list
			if (me.matches(skill, problem, input)) {
				String explanation = replaceMatchSymbols(me.explanation, skill, sai, problem, ran);
				if (explanation != null)
					matches.add(explanation);
			}
		}
		if (matches.size() == 1) {
			matches.remove(SELECT_OPTION);
		} else {
			matches.add(OTHER_OPTION);
		}
		return matches;

	}

	public String replaceMatchSymbols(String matchString, String skill, Sai sai, String problem,
			RuleActivationNode ran) {
		if (matchString.contains(FOA_EXAMPLE_MATCH_SYMBOL)) {

			if ((simSt.instructions.get(skill) != null) && (simSt.instructions.get(skill).size() > 0)
					&& (ran != null)) {
				String foas = simSt.instructionDesc(skill, sai, ran.getRuleFoas());

				if (foas != null)
					matchString = matchString.replace(FOA_EXAMPLE_MATCH_SYMBOL, foas);
				else {
					return null;
				}
			} else {
				return null;
			}
		}

		if (matchString.contains(CURRENT_STEP_MATCH_SYMBOL)) {

			if ((simSt.instructions.get(skill) != null) && (simSt.instructions.get(skill).size() > 0)
					&& (ran != null)) {
				String foas = simSt.currentStepDescription(skill, sai);

				if (foas != null)
					matchString = matchString.replace(CURRENT_STEP_MATCH_SYMBOL, foas);
				else {
					return null;
				}
			} else {
				return null;
			}
		}
		if (matchString.contains(INPUT_MATCH_SYMBOL)) {
			if (sai != null) {
				String input = sai.getI();
				if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME))
					input = "clicking the problem solved button";
				matchString = matchString.replace(INPUT_MATCH_SYMBOL, input);
			} else {
				matchString = matchString.replace(INPUT_MATCH_SYMBOL, "that input");
			}
		}
		if (matchString.contains(OPERAND_MATCH_SYMBOL)) {
			if (sai != null) {
				String operand = sai.getI();
				if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME))
					operand = "done";
				if (operand.contains(" "))
					operand = operand.substring(operand.indexOf(' ') + 1);
				matchString = matchString.replace(OPERAND_MATCH_SYMBOL, operand);
			} else {
				matchString = matchString.replace(OPERAND_MATCH_SYMBOL, "that operand");
			}
		}
		if (matchString.contains(OPERATOR_MATCH_SYMBOL)) {
			if (sai != null) {
				String operator = sai.getI();
				if (operator.contains(" "))
					operator = operator.substring(0, operator.indexOf(' '));
				if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME))
					operator = "click done";
				matchString = matchString.replace(OPERATOR_MATCH_SYMBOL, operator);
			} else {
				matchString = matchString.replace(OPERAND_MATCH_SYMBOL, "do that");
			}
		}
		if (matchString.contains(PROBLEM_MATCH_SYMBOL)) {
			if (problem != null) {
				problem = SimSt.convertFromSafeProblemName(problem);
				matchString = matchString.replace(PROBLEM_MATCH_SYMBOL, problem);
			} else {
				matchString = matchString.replace(PROBLEM_MATCH_SYMBOL, "this problem");
			}
		}

		if (matchString.contains(FOA_PART_MATCH_SYMBOL)) {
			if (sai != null) {
				Vector<String> foas;
				if (ran == null)
					foas = simSt.listFoas(sai);
				else
					foas = ran.getRuleFoas();
				String relevant = simSt.relevantFoaString(sai.getI(), foas);
				if (relevant != null)
					matchString = matchString.replace(FOA_PART_MATCH_SYMBOL, relevant);
				else
					matchString = matchString.replace(FOA_PART_MATCH_SYMBOL, "the relevant part");
			} else {
				matchString = matchString.replace(FOA_PART_MATCH_SYMBOL, "the relevant part");
			}
		}

		/*matchString = matchString.replace("<font color=blue>", "[fontblue]");
		matchString = matchString.replace("<font color=red>", "[fontred]");
		matchString = matchString.replace("</font>", "[fontend]");

		while (matchString.indexOf('>') > -1) {
			String segment = "";
			int indexEnd = matchString.indexOf('>');
			if (matchString.substring(0, indexEnd).indexOf('<') > -1) {
				segment = matchString.substring(matchString.substring(0, indexEnd).lastIndexOf('<'), indexEnd + 1);

				String wOutBracket = segment.replaceAll("<", "").replaceAll(">", "");

				if (wOutBracket.indexOf('"') > -1) {
					int index1 = wOutBracket.indexOf('"');
					int index2 = wOutBracket.lastIndexOf('"') + 1;
					String toReplace = wOutBracket.substring(index1, index2);
					String replaceWith = toReplace.replaceAll("\"", "");
					replaceWith = replaceWith.replaceAll(" ", "&");
					wOutBracket = wOutBracket.replaceAll(toReplace, replaceWith);
				}
				String[] parts = wOutBracket.split(" ");

				MTRete mtRete = brController.getModelTracer().getRete();
				Userfunction function = mtRete.findUserfunction(parts[0]);

				if (function == null) {
					return null;
				}

				if (wOutBracket.indexOf('&') > -1) {
					for (int i = 0; i < parts.length; i++) {
						parts[i] = parts[i].replaceAll("&", " ");
					}
				}

				String result = "";

				ValueVector values = new ValueVector();
				try {

					for (int i = 0; i < parts.length; i++) {
						if (parts[i] != null)
							values.add(parts[i]);
					}

					Value resultValue = function.call(values, mtRete.getGlobalContext());
					result = resultValue.stringValue(mtRete.getGlobalContext());

				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (result.equals("FALSE")) {
					if (sai.getS().equalsIgnoreCase(Rule.DONE_NAME) && parts.length > 1)
						result = parts[1];
					else
						return null;
				}

				matchString = matchString.replace(segment, result);
			} else {
				return null;
			}
		}*/

		return matchString;
	}

	public int getTotalNumberOfProblemsInQuizSection() {
		int problemsInCurrentQuizSection = 0;
		ArrayList<Integer> tmpQuizSection = allQuizSections.get(quizLevel);
		int currentSection = quizSections.get(currentProblem);

		for (int i = 0; i < tmpQuizSection.size(); i++) {
			if (tmpQuizSection.get(i) == currentSection)
				problemsInCurrentQuizSection++;
		}

		return problemsInCurrentQuizSection;

	}

	public Queue unsolvedProblemsQueue = null;

	public void reloadQuizQuestions(boolean flag) {
		randomizeQuizProblems();
		if (cogTutorActualcurrentQuizSection == null)
			cogTutorActualcurrentQuizSection = new ArrayList<String>();

		cogTutorActualcurrentQuizSection.clear();

		if (unsolvedProblemsQueue == null) {
			unsolvedProblemsQueue = new LinkedList();
		}

		if (getSimStPeerTutoringPlatform() != null) {
			if (!flag)
				getSimStPeerTutoringPlatform().clearQuizzes();
			else
				getSimStPeerTutoringPlatform().clearQuizzesFinalChallenge();			
		}

		/* this is to ensure that only one unlocked section exists in APLUS CogTutor */
		boolean unlockedSectionShown = false;
		// int unlockedSectionProblems=0;

		int problemsInCurrentQuizSection = getTotalNumberOfProblemsInQuizSection();
		int cnt = 0;

		for (int i = 0; i < quizProblems.size(); i++) {
			SimStExample quizProblem = null;

			/* check if current problem was solved in the past */

			if (i < currentProblem) {
				// if i < current problem this means that this problem belongs to a previous
				// section. so go and check if the problem is solved. So if we cannot find this
				// problem here, this
				// means it was solved on a previous session. This thing will not work here.
				// Will not.

				if (simSt != null && simSt.getSolvedQuizProblem() != null
						&& (simSt.getSolvedQuizProblem().size() > i)) {

					quizProblem = simSt.getSolvedQuizProblem().get(i);
				}

				if (quizProblem == null) {

					quizProblem = new SimStExample();
					quizProblem.setIndex(i);
					// quizProblem.setTitle((i+1)+")
					// "+GameShowUtilities.generate(getQuizProblem(i)));
					quizProblem.setTitle(GameShowUtilities.generate(getQuizProblem(i)));

					quizProblem.setStatus(SimStExample.QUIZ_OLD);
					quizProblem.setSection(quizSections.get(i));

				}

			} else if (this.getSimSt().isSsCogTutorMode() && !unlockedSectionShown) {
				boolean isProblemSolved = false;
				if (this.passedProblemsList != null)
					isProblemSolved = this.passedProblemsList.contains(i + ",") ? true : false;

				if (isProblemSolved) {
					currentCorrect++;

					if (quizProg.get(currentQuizSectionNumber) != null)
						quizProg.get(currentQuizSectionNumber).setValue(currentCorrect);

					quizProblem = new SimStExample();
					quizProblem.setIndex(i);
					// quizProblem.setTitle((i+1)+")
					// "+GameShowUtilities.generate(getQuizProblem(i)));

					quizProblem.setTitle(GameShowUtilities.generate(getQuizProblem(i)));

					quizProblem.setStatus(SimStExample.QUIZ_OLD);
					quizProblem.setSection(quizSections.get(i));
					quizProblem.setSimSt(this.getSimSt());
				} else {

					// unlockQuizTaskPaneItem(getQuizProblem(i), i);
					String tmp = GameShowUtilities.generate(getQuizProblem(i));
					// JOptionPane.showMessageDialog(null, "faskelo 111RE Now is " + tmp);

					quizProblem = this.createQuizTaskPaneItem(tmp, i);

					cogTutorActualcurrentQuizSection.add(tmp);

					/*
					 * quizProblem = new SimStExample(); quizProblem.setTitle((i+1)+". " +
					 * getQuizProblem(i)); quizProblem.setIndex(i);
					 * 
					 * getSsInteractiveLearning().clearQuizGraph();
					 * quizProblem.addStartStateFromProblemName(getQuizProblem(i),
					 * startStateElements);
					 * 
					 * unsolvedProblemsQueue.add(getQuizProblem(i));
					 * 
					 * AplusPlatform aplus=(AplusPlatform) this.getSimStPeerTutoringPlatform();
					 * 
					 * if (aplus.lastClickedQuizProblem==null){
					 * 
					 * aplus.lastClickedQuizProblem=quizProblem.getTitle();
					 * 
					 * aplus.lastClickedQuizProblemIndex=i;
					 * 
					 * 
					 * }
					 * 
					 * quizProblem.setStatus(SimStExample.COGTUTOR_QUIZ_NOT_TAKEN);
					 * quizProblem.setSection(quizSections.get(i));
					 * quizProblem.setSimSt(this.getSimSt());
					 */
				}

				cnt++;

				if (cnt == problemsInCurrentQuizSection)
					unlockedSectionShown = true;

			} else {
				quizProblem = new SimStExample();
				// quizProblem.setTitle((i+1)+") -----------------");
				quizProblem.setTitle("-----------------");
				quizProblem.setIndex(i);
				quizProblem.setStatus(SimStExample.QUIZ_LOCKED);
				quizProblem.setSection(quizSections.get(i));
			}

			if (this.getSimSt().isSsAplusCtrlCogTutorMode() && getSimStPeerTutoringPlatform() != null) {
				((AplusPlatform) getSimStPeerTutoringPlatform()).addQuizLabelIcon(quizProblem, false);
				// JOptionPane.showMessageDialog(null, "quizProblem " + quizProblem.getTitle());
			} else if (getSimStPeerTutoringPlatform() != null)
				getSimStPeerTutoringPlatform().addQuiz(quizProblem);
			this.quizQuestions.add(quizProblem);
		}
	}

	public String getComponentName(String formalName) {

		Object cname = componentNames.getRegexMatch(formalName);
		if (cname != null)
			return cname.toString();
		else
			return null;

	}
	
	public String getSkillNickName(String ruleName) {
		return skillNickNames.get(ruleName);
	}

	public String messageComposer(String template, String selection, String action, String input) {
		String returnValue = template;

		if (template
				.contains("<sai>")) { /*
										 * If message contains SAI the invoke the getSaiString and make up the string.
										 */
			returnValue = template.replace(SimStConversation.SAI,
					simSt.getFoaGetter().getSaiString(selection, action, input, this.getBrController()));
		} else {

			String[] words = template.split(" ");
			String newMsg = "";
			for (String word : words) {
				String convertedComponentName = getComponentName(word);
				if (convertedComponentName != null)
					word = convertedComponentName;
				newMsg = newMsg + word + " ";
			}

			returnValue = newMsg;
		}

		return returnValue;
	}

	public void setFocusTab(int tabNum) {
		JTabbedPane tabPane = getSimStPeerTutoringPlatform().getExamplePane();
		if (tabNum >= tabPane.getTabCount()) {
			logger.simStLogException(new ArrayIndexOutOfBoundsException(),
					"Error: Unable to set tab to " + tabNum + " with " + tabPane.getTabCount() + " tabs.");
			return;
		}
		tabPane.setSelectedIndex(tabNum);
	}

	public void showCurriculumBrowser() {
		getCurriculumBrowser().showCB();
	}

	public CurriculumBrowser getCurriculumBrowser() {
		return curriculumBrowser;
	}

	public void setCurriculumBrowser(CurriculumBrowser curriculumBrowser) {
		this.curriculumBrowser = curriculumBrowser;
	}

	public static final String EXAMPLE_LOCATION_MARKER = "#";
	public static final String EXAMPLE_VALUE_MARKER = ":";

	// Toggle button between showing and hiding examples
	public void examples() {
		if (getSimStPeerTutoringPlatform().getExampleButton().getText().equals(EXAMPLE_BUTTON_TEXT_HIDE)) {
			// Hide examples, change button to show
			hideExamples();
			getSimStPeerTutoringPlatform().getExampleButton().setText(EXAMPLE_BUTTON_TEXT_SHOW);
		} else {
			// Show examples, change button to hide
			showExamples();
			getSimStPeerTutoringPlatform().getExampleButton().setText(EXAMPLE_BUTTON_TEXT_HIDE);
		}

	}

	// Create and add a tab for each example in the example file
	public void showExamples() {
		// ArrayList<Hashtable<String,String>> examples = readExamples();
		JTabbedPaneWithCloseIcons exampleTabPane = getSimStPeerTutoringPlatform().getExamplePane();
		JComponent studentInterface = getSimStPeerTutoringPlatform().getStudentInterface();
		for (int exampleNum = 0; exampleNum < examples.size(); exampleNum++) {
			// Hashtable<String,String> example = examples.get(exampleNum);

			JPanel examplePanel = new JPanel();

			// showExample(examplePanel, example, studentInterface);

			// Add the tab for this example
			// exampleTabPane.insertTab("Example "+(exampleNum+1), null, examplePanel, "",
			// exampleNum+2);
			exampleTabPane.addTab("Example " + (exampleNum + 1), examplePanel, false);

		}
		// if(exampleTabPane.getTabCount() > 1)
		// setFocusTab(FIRST_EXAMPLE);
	}

	public void showExample(JPanel examplePanel, Hashtable<String, String> example, JComponent studentInterface) {
		try {
			/*
			 * if(simSt.isSsMetaTutorMode()) { // Is an indicator that the student has seen
			 * one / more examples if(simSt.getModelTraceWM() != null) {
			 * ModelTraceWorkingMemory wm = simSt.getModelTraceWM();
			 * wm.setExamplesTabClicked(WorkingMemoryConstants.TRUE); } }
			 */
			// Copy the student interface
			JComponent newInterface = studentInterface.getClass().newInstance();
			// Disable entry into the fields of the new student interface
			setComponentEnabled(false, newInterface);
			tableNumber = 0;
			fillInExample(example, newInterface);

			newInterface.setPreferredSize(studentInterface.getSize());
			examplePanel.setLayout(new FlowLayout());

			setComponentFont(newInterface, new Font("Serif", Font.PLAIN, 18));

			examplePanel.add(newInterface);
		} catch (Exception e) {
			if (trace.getDebugCode("miss"))
				trace.out("miss", "Error in creating example tab for example: " + e.getMessage());
			e.printStackTrace();
			logger.simStLogException(e, "Error in creating example tab for example: " + e.getMessage());
		}
	}

	int tableNumber = 0;

	// Recursively go through components and their components, assigning the values
	// to any listed in the hashtable
	public void fillInExample(Hashtable<String, String> example, JComponent exampleInterface) {
		// fill in tables
		if (exampleInterface instanceof JCommTable) {
			JCommTable table = (JCommTable) exampleInterface;
			for (int x = 0; x < table.getColumns(); x++) {
				for (int y = 0; y < table.getRows(); y++) {
					String key = tableNumber + EXAMPLE_LOCATION_MARKER + x + EXAMPLE_LOCATION_MARKER + y;
					// If the cell's combination of table, column and row are in the hashtable, then
					// there is a
					// corresponding value to fill into that cell
					if (example.containsKey(key)) {
						TableCell cell = table.getCell(y, x);
						cell.setText(example.get(key));
					}
				}
			}
			tableNumber++;
		} else
		// look inside other components for tables
		{
			for (int i = 0; i < exampleInterface.getComponentCount(); i++) {
				JComponent c = (JComponent) exampleInterface.getComponent(i);
				fillInExample(example, c);
			}
		}

	}

	// An example is a hashtable of all the components which have values and their
	// corresponding values
	// Each item in the list is a different example
	/*
	 * public ArrayList<Hashtable<String,String>> readExamples() { BufferedReader
	 * reader=null; ArrayList<Hashtable<String,String>> examples = new
	 * ArrayList<Hashtable<String,String>>(); try { reader = new BufferedReader(new
	 * FileReader(EXAMPLES_FILE)); String line = reader.readLine(); while(line !=
	 * null) { Hashtable<String,String> example= new Hashtable<String,String>();
	 * while(line != null && line.length() != 0) { //splits the location from the
	 * contents String[] values = line.split(EXAMPLE_VALUE_MARKER); if(values.length
	 * > 1) { //values[0] is table#col#row, values[1] is contents of that cell
	 * example.put(values[0], values[1]); } line = reader.readLine(); }
	 * examples.add(example); line = reader.readLine(); }
	 * 
	 * }catch(Exception e) { e.printStackTrace();
	 * logger.simStLogException(e,"Unable to read examples from file: "+e.getMessage
	 * ()); }finally { try{reader.close();}catch(Exception e){
	 * logger.simStLogException(e); } }
	 * 
	 * return examples; }
	 */

	public void hideExamples() {
		// Remove all tabs except index 0, the main SimSt tab
		JTabbedPane examplePane = getSimStPeerTutoringPlatform().getExamplePane();
		// start from back and work forwards - as tabs are removed indices after them
		// change
		for (int i = examplePane.getTabCount() - 1; i > 0; i--) {
			examplePane.removeTabAt(i);
		}
	}

	public void undo() {
		if(!runType.equals("springBoot")) {
			setFocusTab(SIM_ST_TAB);
		}
		new Thread(new UndoThread()).start();
	}

	private String wasDone = "Was Done";
	private String wasFailToLearn = "Was FailToLearn Stop";
	private String wasNormal = "Was Normal HintRequest";
	private String wasVerify = "Was Verifying Production";

	public class UndoThread implements Runnable {
		private String undoThroughSelection = "";

		public UndoThread() {
			super();
			undoThroughSelection = "";
		}

		public UndoThread(String undoThrough) {
			super();
			undoThroughSelection = undoThrough;
		}

		public void run() {
			if (brController.getCurrentNode() == null || brController.getCurrentNode().getIncomingEdges().size() <= 0) {
				simSt.displayMessage("Undo?", NO_UNDO_MSG);
				return;
			}

			if (undoThroughSelection.length() > 0) {
				undoThroughStep(undoThroughSelection);
			} else {
				undoLastStep();
			}

		}

		public void undoThroughStep(String undoThroughSelection) {
			blockInput(true);

			boolean undoable = true;
			String query = "";
			ProblemEdge finalEdge = null;

			String currentStatus = wasNormal;

			Sai sai = null;
			ProblemNode currentNode = brController.getCurrentNode();

			ProblemEdge edge = ((ProblemEdge) currentNode.getIncomingEdges().get(0));
			if (edge.getEdgeData().getActionType().equals(EdgeData.GIVEN_ACTION)) {
				trace.out("ss", "Dismissing current inquiry " + edge.getSelection());
				currentNode = edge.getSource();
				brController.setCurrentNode2(currentNode);
				if (currentNode.getInDegree() <= 0) {
					simSt.displayMessage("Undo?",
							getConversation().getMessage(SimStConversation.NOTHING_TO_UNDO_TOPIC));
					undoable = false;
				}
				currentStatus = wasVerify;
			}

			ProblemNode revertTo = currentNode;

			if (!simSt.isInteractiveLearning()) {
				if (brController.getCurrentNode().getDoneState())
					currentStatus = wasDone;
				else
					currentStatus = wasFailToLearn;
			}

			int result = JOptionPane.CANCEL_OPTION;

			if (undoable) {
				do {
					ProblemEdge currentEdge = ((ProblemEdge) currentNode.getIncomingEdges().get(0));
					sai = currentEdge.getSai();

					Object obj = brController.lookupWidgetByName(sai.getS());
					if (obj != null && obj instanceof JCommTable.TableCell) {
						((JCommTable.TableCell) obj).setBackground(Color.pink);
					}

					currentNode = currentEdge.getSource();

				} while (!sai.getS().equals(undoThroughSelection) && currentNode.getInDegree() > 0);

				if (currentNode.getInDegree() <= 0 && !sai.getS().equals(undoThroughSelection))
					undoable = false;

				query = getConversation().getMessage(SimStConversation.UNDO_CONFIRM_STEPS_TOPIC, sai.getI());
				// String query = generateUndoQueryMessage(sai.getS(), sai.getA(),sai.getI());
				result = simSt.displayConfirmMessage("Undo?", query);

				sai = null;
				currentNode = brController.getCurrentNode();

				do {
					ProblemEdge currentEdge = ((ProblemEdge) currentNode.getIncomingEdges().get(0));
					sai = currentEdge.getSai();

					Object obj = brController.lookupWidgetByName(sai.getS());
					if (obj != null && obj instanceof JCommTable.TableCell) {
						((JCommTable.TableCell) obj).setBackground(Color.white);
					}

					currentNode = currentEdge.getSource();
					finalEdge = currentEdge;

				} while (!sai.getS().equals(undoThroughSelection) && currentNode.getInDegree() > 0);

				if (currentNode.getInDegree() <= 0 && !sai.getS().equals(undoThroughSelection))
					undoable = false;
			}

			if (result == JOptionPane.YES_OPTION && undoable) {

				if (trace.getDebugCode("rr"))
					trace.out("rr", "Modeltracing the student action: " + SimStPLE.UNDO + "  ButtonPressed" + "  -1");
				if (simSt.isSsMetaTutorMode() && brController.getAmt() != null)
					brController.getAmt().handleInterfaceAction(SimStPLE.UNDO, "ButtonPressed", "-1");

				boolean traversed = false;
				// undo!

				if (currentNode.isStudentBeginsHereState()) {
					trace.out("ss", "Back to start state");
					brController.goToStartState();
					traversed = true;
				} else {
					trace.out("ss", "Back to " + currentNode);
					traversed = brController.goToState(currentNode);
				}

				if (traversed) {
					if (currentNode.getInDegree() > 0) {
						ProblemEdge prev = (ProblemEdge) currentNode.getIncomingEdges().get(0);
						getSimStPeerTutoringPlatform().setUndoButtonText(getUndoButtonTitleString(prev.getInput()));
						simSt.setLastSkillOperand(findLastSkillOperand(prev));
					} else {
						getSimStPeerTutoringPlatform().setUndoButtonText(getUndoButtonTitleString());
						simSt.setLastSkillOperand(null);
					}

					if (currentStatus.equals(wasDone)) {
						String message = getConversation().getMessage(SimStConversation.UNDO_RESUME_TOPIC, sai.getI());
						simSt.displayMessage("SimStudent says...", message);
					} else {
						String message = getConversation().getMessage(SimStConversation.UNDO_SHOULD_DO_TOPIC,
								sai.getI());
						simSt.displayMessage("SimStudent asks...", message);
						if (simSt.getLastSkillOperand() != null && !simSt.getLastSkillOperand().equals(sai.getI())) {
							message = getConversation().getMessage(SimStConversation.UNDO_REMEMBER_PREVIOUS_TOPIC,
									simSt.getLastSkillOperand());
							simSt.displayMessage("SimStudent says...", message);
						} else {
							String tmp = simSt.revertLastSkillOperand();
						}
					}

					if (finalEdge != null && finalEdge.getSai().getS().equals(undoThroughSelection)) {
						Instruction inst = simSt.lookupInstructionWithNode(finalEdge.getDest());
						if (inst != null) {
							simSt.negateBadPositiveExample(inst);
							simSt.signalInstructionAsNegativeExample(inst);
						}
					}
					simSt.setIsInteractiveLearning(true);
					getSsInteractiveLearning().runInteractiveLearning(currentNode, false);
				}
			} else {
				if (currentStatus.equals(wasDone)) {
					simSt.displayMessage("No undo", getConversation().getMessage(SimStConversation.NO_UNDO_DONE_TOPIC));
					getSimStPeerTutoringPlatform().setExpression(SUCCESS_EXPRESSION);
				} else if (currentStatus.equals(wasFailToLearn)) {
					simSt.displayMessage("No undo",
							getConversation().getMessage(SimStConversation.NO_UNDO_FAIL_TO_LEARN_TOPIC));
					getSimStPeerTutoringPlatform().setExpression(CONFUSE_EXPRESSION);
				} else {
					if (revertTo.isStudentBeginsHereState()) {
						trace.out("ss", "Back to start state");
						brController.goToStartState();
					} else {
						trace.out("ss", "Back to " + currentNode);
						brController.goToState(revertTo);
					}

					simSt.displayMessage("No undo", getConversation().getMessage(SimStConversation.NO_UNDO_TOPIC));
					simSt.setIsInteractiveLearning(true);
					getSsInteractiveLearning().runInteractiveLearning(revertTo, true);
				}
			}
		}

		public String findLastSkillOperand(ProblemEdge lastEdge) {
			ProblemEdge prev = lastEdge;
			if (simSt.isSkillNameGetterDefined()) {
				while (prev != null) {
					if (!simSt.getSkillNameGetter().skillNameGetter(brController, prev.getSai().getS(),
							prev.getSai().getA(), prev.getSai().getI()).contains("typein")) {
						return prev.getInput();
					}
					ProblemNode source = prev.getSource();
					if (source.getInDegree() > 0) {
						prev = (ProblemEdge) (source.getIncomingEdges().get(0));
					} else {
						return null;
					}
				}
			}
			return prev.getInput();

		}

		public void undoLastStep() {

			// JOptionPane.showMessageDialog(null,
			// ((ProblemEdge)brController.getCurrentNode().getIncomingEdges().get(0)).getSai());
			if(!runType.equals("springBoot")) {
				blockInput(true);
			}

			ProblemEdge edge = ((ProblemEdge) brController.getCurrentNode().getIncomingEdges().get(0));
			Sai sai = edge.getSai();
			

			if(!runType.equals("springBoot")) {
				Object obj = brController.lookupWidgetByName(sai.getS());
				if (obj != null && obj instanceof JCommTable.TableCell) {
					((JCommTable.TableCell) obj).setBackground(Color.pink);
				}
			}

			String query = generateUndoQueryMessage(sai.getS(), sai.getA(), sai.getI());
			int result = 0;
			setEdge(edge);
			setSai(sai);
			setQuery(query);
			
			if(!runType.equals("springBoot")) {
				result = simSt.displayConfirmMessage("Undo?", query);
				Object obj = brController.lookupWidgetByName(sai.getS());

				if (obj != null && obj instanceof JCommTable.TableCell) {
					((JCommTable.TableCell) obj).setBackground(Color.white);
				}
				
				if (result == JOptionPane.YES_OPTION) {
					onUndoYes(edge, sai, query);
				} else {
					onUndoNo();
				}
			}
		}

	}
	
	public void onUndoYes(ProblemEdge edge, Sai sai, String query) {
		if (trace.getDebugCode("rr"))
			trace.out("rr", "Modeltracing the student action: " + SimStPLE.UNDO + "  ButtonPressed" + "  -1");
		if (simSt.isSsMetaTutorMode() && brController.getAmt() != null)
			brController.getAmt().handleInterfaceAction(SimStPLE.UNDO, "ButtonPressed", "-1");

		String currentStatus = wasNormal;
		if (!simSt.isInteractiveLearning()) {
			if (brController.getCurrentNode().getDoneState())
				currentStatus = wasDone;
			else
				currentStatus = wasFailToLearn;
		}

		// AskHint hint = new AskHintInBuiltClAlgebraTutor(brController,
		// edge.getSource());
		// CL oracle must not be hardcoded! Whichever oracle grades the quiz should
		// provide hint for logging
		AskHint hint = simSt.askForHintQuizGradingOracle(brController, edge.getSource());

		if (hint != null) {
			/*
			 * if(simSt.isSsMetaTutorMode()) { if(simSt.getModelTraceWM() != null) {
			 * simSt.getModelTraceWM().setStepUndone(WorkingMemoryConstants.TRUE); } }
			 */
			// if(getSimSt().isSsMetaTutorMode()) {
			// getSimSt().getModelTraceWM().getEventHistory().add(0,
			// getSimSt().getModelTraceWM().new Event(SimStLogger.UNDO_ACTION));
			// }

			int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis()
					- getSsInteractiveLearning().getProblemRecentTime()) / 1000);
			getSsInteractiveLearning().setProblemRecentTime(Calendar.getInstance().getTimeInMillis());

			logger.simStLog(SimStLogger.SIM_STUDENT_STEP, SimStLogger.UNDO_ACTION, simSt.getProblemStepString(),
					currentStatus, "", sai, edge.getSource(), hint.getSelection(), hint.getAction(),
					hint.getInput(), problemDuration, query);

		}

		boolean traversed = false;
		// undo!

		if (edge.getSource().isStudentBeginsHereState()) {
			brController.goToStartState();
			traversed = true;
		} else {
			traversed = brController.goToState(edge.getSource());
		}
		// JOptionPane.showMessageDialog(null, "Done Node Style: "+traversed);

		// Learning must be started again and let it handle it (but do stop it from
		// coming up with activation lists again - after undo SimStudent expects
		// Student to enter hint)

		if (traversed) {
			if (edge.getSource().getInDegree() > 0) {
				ProblemEdge prev = (ProblemEdge) edge.getSource().getIncomingEdges().get(0);
				if(runType.equals("springBoot")) {
					setUndoButtonTextInfo(getUndoButtonTitleString(prev.getInput()));
				} else {
					getSimStPeerTutoringPlatform().setUndoButtonText(getUndoButtonTitleString(prev.getInput()));
				}
			} else {
				if(runType.equals("springBoot")) {
					setUndoButtonTextInfo(getUndoButtonTitleString());
				} else {
					getSimStPeerTutoringPlatform().setUndoButtonText(getUndoButtonTitleString());
				}
			}
			
			if(runType.equals("springBoot")) {
				String undoMsg = "";
				if (currentStatus.equals(wasDone)) {
					undoMsg = BACK_TO_WORK_MSG;
				} else {
					undoMsg = SHOULD_DO_MSG.replace("$", sai.getI());
					if (simSt.getLastSkillOperand() == null) {
						String NOTHING_DONE_YET = "I don't remember having done anything before that...";
						undoMsg += "\n"+ NOTHING_DONE_YET;
					} else if (!simSt.getLastSkillOperand().equals(sai.getI())) {
						undoMsg += "\n" +"I remember I was trying to " + simSt.getLastSkillOperand() + ".";
					} else {
						String tmp = simSt.revertLastSkillOperand();
					}
				}
				setUndoMessage(undoMsg);
			} else {
				if (currentStatus.equals(wasDone)) {
					simSt.displayMessage("SimStudent says...", BACK_TO_WORK_MSG);
				} else {
					simSt.displayMessage("SimStudent asks...", SHOULD_DO_MSG.replace("$", sai.getI()));
					if (simSt.getLastSkillOperand() == null) {
						String NOTHING_DONE_YET = "I don't remember having done anything before that...";
						simSt.displayMessage(null, NOTHING_DONE_YET);
					} else if (!simSt.getLastSkillOperand().equals(sai.getI())) {
						simSt.displayMessage("SimStudent says...",
							"I remember I was trying to " + simSt.getLastSkillOperand() + ".");
					} else {
						String tmp = simSt.revertLastSkillOperand();
					}
				}
			}

			Instruction inst = simSt.lookupInstructionWithNode(edge.getDest());
			simSt.negateBadPositiveExample(inst);
			simSt.signalInstructionAsNegativeExample(inst);

			simSt.setIsInteractiveLearning(true);
			if (runType.equalsIgnoreCase("springboot")) {
				this.setCurrentNode(edge.getSource());
			} else {
				getSsInteractiveLearning().runInteractiveLearning(edge.getSource(), false);				
			}
		}

		/*
		 * if(!simSt.isInteractiveLearning()) { //if(edge.getDest().getDoneState()) //{
		 * if(edge.getSource().isStudentBeginsHereState()) {
		 * brController.goToStartState(); traversed = true; } else { traversed =
		 * brController.goToState(edge.getSource()); }
		 * //JOptionPane.showMessageDialog(null, "Done Node Style: "+traversed);
		 * 
		 * //Learning must be started again when undoing a done state, so handle this
		 * differently //with messaging - just restart the learning and let it handle it
		 * (but do stop it from //coming up with activation lists again - after undo
		 * SimStudent expects Student to enter hint)
		 * 
		 * if(traversed) { simSt.setIsInteractiveLearning(true);
		 * getSsInteractiveLearning().runInteractiveLearning(edge.getSource(),false); }
		 * return; //} } else if(edge.getSource().isStudentBeginsHereState()) {
		 * brController.goToStartState(); traversed = true;
		 * simSt.displayMessage("SimStudent asks...", SHOULD_DO_MSG); } else { traversed
		 * = brController.goToState(edge.getSource()); if(traversed)
		 * simSt.displayMessage("SimStudent asks...", SHOULD_DO_MSG); } if(!traversed) {
		 * simSt.displayMessage("Error", UNDO_ERROR_MSG); return; } String newStep =
		 * simSt.calcProblemStepString(); simSt.setProblemStepString(newStep);
		 * logger.simStLog(SimStLogger.SIM_STUDENT_STEP,
		 * SimStLogger.STEP_STARTED_ACTION, newStep,"",""); blockInput(false);
		 */
	}
	
	public void onUndoNo() {
		if (!brController.getCurrentNode().getDoneState()) {
			if(runType.equals("springBoot")) {
				setUndoMessage(RESUME_MSG);
			} else {
				blockInput(false);
				simSt.displayMessage("SimStudent asks...", RESUME_MSG);
			}
		}
	}

	public String generateUndoQueryMessage(String selection, String action, String input) {
		String message = "";

		// Use different messaging if the action is pressing a button
		if (action.equalsIgnoreCase("ButtonPressed")) {
			if (selection.equalsIgnoreCase(Rule.DONE_NAME)) {
				message = UNDO_DONE_MSG;
			} else if (getComponentName(selection) != null) {
				message = "Should I go back before I clicked " + getComponentName(selection) + "?";
			} else {
				message = "Should I go back before I clicked the " + selection + " button?";
			}
		} else if (getComponentName(selection) != null) {
			if (!simSt.isInteractiveLearning()) {
				message = "I guess I can give it another try.  Should I erase \"" + input + "\" "
						+ getComponentName(selection) + "?";
			} else {
				message = "Should I erase \"" + input + "\" in " + getComponentName(selection) + "?";
			}
		} else {
			if (!simSt.isInteractiveLearning()) {
				message = "I guess I can give it another try.  Should I erase \"" + input + "\" from " + selection
						+ "?";
			} else {
				message = "Should I erase \"" + input + "\" from " + selection + "?";
			}
		}

		return message;
	}

	/**
	 * code to give the next problem
	 * 
	 * @param switchTab boolean indicating if APLUS should change to new tutoring
	 *                  tab or not.
	 */
	public void nextProblem(boolean switchTab) {

		/*
		 * jinyul - save instructions.txt when moving on to the next problem as partial
		 * progress may have been made.
		 */
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Enter nextProblem to call autoSaveInstructions()");
		/** No longer need to save the instructions, but do for back-up */
		getMissController().autoSaveInstructions("instructions");
		getMissController().getSimSt().saveSimStState();
		getMissController().getSimSt().setProblemStepString(SimSt.START_STEP);
		// getSimStPeerTutoringPlatform().getNextProblemButton().setForeground(Color.black);

		if (switchTab)
			setFocusTab(SIM_ST_TAB);

		JButton nextProblemButton = getSimStPeerTutoringPlatform().getNextProblemButton();
		if (nextProblemButton.getText().equals(getProblemEnteredButtonString())) {
			startProblem();
		} else {
			requestEnterNewProblem();
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getNextSuggestedProblem() {

		if (problemMap == null) {
			trace.err("Problem Bank was not initialized");
			return "";
		}

		Set<String> keys = problemMap.keySet();
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			String problem = (String) itr.next();
			String status = problemMap.get(problem);
			if (status == WorkingMemoryConstants.PROBLEM_NOT_USED_FOR_TUTORING) {
				return problem;
			}
		}
		trace.err("No next suggested problem as all the problems in the Problem Bank have been used");
		return ""; // Unlikely that we will exhaust all the problems
	}

	/*
	 * public void updateProblemMap(String problem, String currentProblem){
	 * 
	 * String suggestedProblem = problem; ModelTraceWorkingMemory wm =
	 * simSt.getModelTraceWM(); if(suggestedProblem != null &&
	 * !suggestedProblem.isEmpty()) { String abstractProblem = ""; String[] token =
	 * suggestedProblem.split("="); suggestedProblem = " " + token[0] + "=" +
	 * token[1] + " "; abstractProblem =
	 * simSt.getQuizProblemAbstractor().abstractProblem(suggestedProblem, null);
	 * if(currentProblem.equalsIgnoreCase(abstractProblem)) {
	 * if(problemMap.containsKey(wm.getSuggestedProblem())) {
	 * problemMap.put(wm.getSuggestedProblem(),
	 * WorkingMemoryConstants.PROBLEM_USED_FOR_TUTORING); } } } }
	 */
	
	public String onRestartClicked() {
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
    		return "";
    	}
    	
    	//restart the problem
    	if (brController.getMissController().getSimSt().isSsCogTutorMode()){
    		controllerActionsOnRestart();
    	}
    	else 
    		return getMissController().pleRestartProblemSimSt();
    	return "";
    }
	
	public void controllerActionsOnRestart() {
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

	public String restartProblem() {
		if (!runType.equalsIgnoreCase("springboot"))
			uiActionsOnRestart();
		return commonActionsOnRestart();
	}
	
	public String commonActionsOnRestart() {
		String step = simSt.getProblemStepString();
		simSt.killInteractiveLearningThreadIfAny();
		brController.goToStartState();
		int problemDuration = (int) ((Calendar.getInstance().getTimeInMillis()
				- getSsInteractiveLearning().getProblemRecentTime()) / 1000);
		getSsInteractiveLearning().setProblemRecentTime(Calendar.getInstance().getTimeInMillis());
		logger.simStLog(SimStLogger.SIM_STUDENT_PROBLEM, SimStLogger.PROBLEM_RESTART_ACTION, step, "", "",
				problemDuration);
		if (!simSt.isSsCogTutorMode())
			startProblem(true);
		if (invalidProblemMsg != null && !invalidProblemMsg.isEmpty())
			return invalidProblemMsg;
		else
			return RESTART_MSG;
	}
	
	public void uiActionsOnRestart() {
		setFocusTab(SIM_ST_TAB);
		if (!simSt.isSsCogTutorMode())
			simSt.displayMessage("Problem Restart", RESTART_MSG);
	}
	
	class QuizThread implements Runnable {
		public void run() {
			getSimStPeerTutoringPlatform().showQuizResultFrame(false);
			getSimStPeerTutoringPlatform().setQuizButtonEnabled(false);
			// getSimStPeerTutoringPlatform().getNextProblemButton().setEnabled(false);
			getSimStPeerTutoringPlatform().setNextProblemButtonEnabled(false);

			setFocusTab(SIM_ST_TAB);
			// quizProblems = readQuizProblems();

			// Backing up the current configuration
			boolean dontShowAllRA = getSimSt().dontShowAllRA();
			getSimSt().setDontShowAllRA(true);
			boolean ilSignalNegative = getSimSt().isILSignalNegative();
			getSimSt().setIlSignalNegative(false);
			boolean ilSignalPositive = getSimSt().isILSignalPositive();
			getSimSt().setIlSignalPositive(false);
			String raTestMethod = getSimSt().getRuleActivationTestMethod();
			getSimSt().setRuleActivationTestMethod(SimSt.RA_TEST_METHOD_TUTOR_SOLVERV2);

			setAvatarQuiz();
			getSsInteractiveLearning().setTakingQuiz(true);

			long startTime = (new Date()).getTime();
			Vector<Vector<ProblemEdge>> /* Vector<String> */ results = solveQuizProblems();
			long endTime = (new Date()).getTime();

			long duration = endTime - startTime;

			displayQuizResultsAlgebraI(results);
			if (getSimStPeerTutoringPlatform().getMedalCount() < currentProblem + currentCorrect)
				getSimStPeerTutoringPlatform().augmentMedals(
						currentProblem + currentCorrect - getSimStPeerTutoringPlatform().getMedalCount(), true);

			int percent = ((currentCorrect + currentProblem) * 100 / quizProblems.size());
			if (percent >= 100) {
				quizPassed = true;
			}

			if (currentCorrect == currentQuizSection.size()) {
				currentProblem += currentQuizSection.size();
				currentOverallProblem += currentQuizSection.size();
				currentCorrect = 0;
			}

			getSimStPeerTutoringPlatform().showQuizResultFrame(true);
			getSimStPeerTutoringPlatform().setQuizButtonEnabled(true);
			// getSimStPeerTutoringPlatform().getNextProblemButton().setEnabled(true);
			getSimStPeerTutoringPlatform().setNextProblemButtonEnabled(true);

			getSsInteractiveLearning().setTakingQuiz(false);

			// Restoring the previous configuration
			getSimSt().setDontShowAllRA(dontShowAllRA);
			getSimSt().setIlSignalNegative(ilSignalNegative);
			getSimSt().setIlSignalPositive(ilSignalPositive);
			getSimSt().setRuleActivationTestMethod(raTestMethod);

			if (quizPassed) {
				getSimStPeerTutoringPlatform().addTrophy(true);

				if (getSimSt().getSSCLQuizReqMode()) {
					JOptionPane.showMessageDialog(null,
							"<html><p>Congratulations!</p><p>With your help, " + getSimStName()
									+ " was able to pass the quiz.</p><p>Click OK to close SimStudent now.</p>",
							"You did it!", JOptionPane.INFORMATION_MESSAGE,
							getSimStPeerTutoringPlatform().createImageIcon("img/trophyIcon.png"));

					// brController.closeApplication(false);
					saveAccountFile(getSimSt().getUserID() + ".account");
					System.exit(QUIZ_COMPLETED_EXIT);
				} else {
					if (getQuizLevel() + 1 < allQuizProblems.size()) {

						JOptionPane.showMessageDialog(null, "<html><p>Congratulations!</p><p>With your help, "
								+ getSimStName()
								+ " was able to pass the quiz</p><p>and is ready to move on to the next level.</p>",
								"You did it!", JOptionPane.INFORMATION_MESSAGE,
								getSimStPeerTutoringPlatform().createImageIcon("img/trophyIcon.png"));
						// move on
						quizLevel++;

						quizProblems = allQuizProblems.get(quizLevel);
						quizSections = allQuizSections.get(quizLevel);
						quizPassed = false;
						currentProblem = 0;
						currentCorrect = 0;

						reloadQuizQuestions(false);
					} else {
						// no more to move on to
						JOptionPane.showMessageDialog(null,
								"<html><p>Congratulations!</p><p>With your help, " + getSimStName()
										+ " was able to pass the quiz.</p><p>Click OK to close SimStudent now.</p>",
								"You did it!", JOptionPane.INFORMATION_MESSAGE,
								getSimStPeerTutoringPlatform().createImageIcon("img/trophyIcon.png"));

						saveAccountFile(getSimSt().getUserID() + ".account");
						System.exit(QUIZ_COMPLETED_EXIT);
					}
				}
			}
			requestEnterNewProblem();

			saveAccountFile(getSimSt().getUserID() + ".account");
		}
	}

	/**
	 * Starts a new thread to run the quiz. To revert to the old version use
	 * QuizThread class.
	 */
	public void quizSimSt() {
		// Future<?> future = quizThreadPool.submit(new SSQuizThread());
		new Thread(new SSQuizThread()).start();
	}

	public static int currentProblem = 0;
	public static int cogTutorCurrentProblem = 0;

	public int currentCorrect = 0;
	public static int currentOverallProblem = 0;

	public final static int MAX_SEARCH_DEPTH = 10;

	private Vector<ProblemEdge> solutionEdges = null;

	public Vector<ProblemEdge> getSolutionEdges() {
		return solutionEdges;
	}

	public void setSolutionEdges(Vector<ProblemEdge> solutionEdges) {
		this.solutionEdges = solutionEdges;
	}

	private Vector<Vector<ProblemEdge>> solveQuizProblems() {

		if (trace.getDebugCode("miss"))
			trace.out("miss", "####### solveQuizProblems...");
		long startQuizTime = Calendar.getInstance().getTimeInMillis();

		int numCorrect = 0;
		Vector<Vector<ProblemEdge>> solutions = new Vector<Vector<ProblemEdge>>();

		randomizeQuizProblems();

		// for (int i = 0; i < getQuizProblems().length; i++) {

		for (int i = 0; i < currentQuizSection.size(); i++) {

			getSimStPeerTutoringPlatform().refresh();
			String problem = getRandomizedQuizProblem(i);

			giveMessage("I'm working on quiz problem #" + (currentProblem + i + 1) + " now.");
			getSimStPeerTutoringPlatform().refresh();

			logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_QUESTION_GIVEN_ACTION,
					"Quiz" + (currentQuizSectionNumber + 1) + "." + (currentProblem + i + 1), "", problem);
			long startQuizQuestionTime = Calendar.getInstance().getTimeInMillis();
			getBrController().startNewProblem();
			getSsInteractiveLearning().ssInteractiveLearningOnProblem(problem);

			/*
			 * Iterative Deepening Search for the Quiz Need to fix the Rete as the
			 * Exhaustive Search is time consuming
			 */
			/*
			 * solutionEdges = null;
			 * getSsInteractiveLearning().createStartStateOnProblem(quizProblem);
			 * 
			 * // 1. Create a initial state for the QuizState // 2. Create a new Problem
			 * 
			 * try { ProblemNode startNode = brController.getProblemModel().getStartNode();
			 * QuizState quizState = new QuizState(this, getSsInteractiveLearning(),
			 * quizProblem); quizState.setProbNode(startNode);
			 * quizState.setCurrentStep(quizProblem); quizState.setPreviousStep("");
			 * quizState.setTypeInStep(false); quizState.setDoneStep(false); Problem problem
			 * = new Problem(quizState, new QuizStateSuccessorFn(), new QuizGoalTest());
			 * Search search = new IterativeDeepeningSearch(); SearchAgent quizSearchAgent =
			 * new SearchAgent(problem, search);
			 * printInstrumentation(quizSearchAgent.getInstrumentation()); } catch
			 * (Exception e) { e.printStackTrace(); } int IDSQuizTime = (int)
			 * (Calendar.getInstance().getTimeInMillis() - startQuizQuestionTime);
			 */

			// Save the solution steps in the BR graph
			ProblemNode startState = getBrController().getProblemModel().getStartNode();

			// TODO: Fix the problem of SimSt not learning done to make this more general...
			// Ad-hoc only for Algebra I assuming a correct solution ends with an equation
			// "x=n"
			Vector<ProblemEdge> solution = simSt.getProblemAssessor().findSolutionPath(startState);
			// solutions.add(solutions.size(), solution != null ? solution : new
			// Vector<ProblemEdge>());
			// System.out.println("solveQuizProblems() gotten a solution " + solution.size()
			// + "...");
			solutions.add(solution != null ? solution : new Vector<ProblemEdge>());

			// solutions.add(solutionEdges != null ? solutionEdges : new
			// Vector<ProblemEdge>());

			getSimStPeerTutoringPlatform().refresh();
			boolean result = getSimSt().getProblemAssessor().isProblemComplete(problem, solution);
			if (result)
				numCorrect++;

			long quizQuestionDuration = (Calendar.getInstance().getTimeInMillis() - startQuizQuestionTime) / 1000;
			logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_QUESTION_ANSWER_ACTION,
					"Quiz" + currentQuizSectionNumber + "." + (currentProblem + i), solutionSteps(problem, solution),
					"", result, (int) quizQuestionDuration);
			/*
			 * System.out.println("SimStPLE.QuizSimSt DONE on " + problem); String[] msg =
			 * {"suspended"}; SimSt.suspendForDebug(getBrController(), "solveQuizProblems",
			 * msg);
			 */
		}

		float pctCorrect = ((float) (numCorrect + currentProblem) / getQuizProblems().size());

		int quizDuration = (int) ((Calendar.getInstance().getTimeInMillis() - startQuizTime) / 1000);
		logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_COMPLETED_ACTION,
				"Quiz" + currentQuizSectionNumber, "" + pctCorrect, "" + numCorrect + "/" + getQuizProblems().size(),
				quizDuration);
		currentCorrect = numCorrect;

		getSimStPeerTutoringPlatform().setQuizProgress(pctCorrect);

		// clear-up the Student Interface
		getBrController().startNewProblem();

		return solutions;
	}

	private static void printInstrumentation(Properties properties) {
		Iterator keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String property = properties.getProperty(key);
			// System.out.println(key + " : " + property);
		}
	}

	public static final String PROBLEM_NOT_SOLVED = "PROBLEM_NOT_SOLVED";

	public String solutionSteps(String problem, Vector<ProblemEdge> solutionPath) {
		String solution = problem;
		if (solutionPath == null)
			solution += "," + PROBLEM_NOT_SOLVED;
		else {
			for (ProblemEdge edge : solutionPath) {
				if (edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
					solution += "," + edge.getSelection();
				else
					solution += "," + edge.getInput();
			}
		}

		return solution;

	}

	/**
	 * Creates a list of all the steps in the solution graph for logging.
	 * 
	 * @param problem
	 * @param solutionPath
	 * @return
	 */
	private String quizSolutionSteps(String problem, Vector<ProblemEdge> solutionPath) {

		StringBuffer solution = new StringBuffer();
		if (solutionPath == null)
			solution.append("," + PROBLEM_NOT_SOLVED);
		else {
			for (ProblemEdge edge : solutionPath) {
				if (edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
					solution.append("," + edge.getSelection());
				else
					solution.append("," + edge.getInput());
			}
		}
		return solution.toString();
	}

	private static boolean correctCompleteAnswers(String problem, Vector<ProblemEdge> solutionPath) {
		String[] problemParts = problem.split("=");
		if (problemParts.length < 2) {
			return false;
		}
		String firstPrev = problemParts[0];
		String secondPrev = problemParts[1];
		if (solutionPath == null || solutionPath.size() == 0) {
			return false;
		} else {
			for (ProblemEdge edge : solutionPath) {

				if (edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME)) {
					// A completed answer ends with done, with the equation before it having one
					// side containing
					// only a variable, and the other side not containing that variable
					// (Will not get to this point if the steps were incorrect)
					// Doesn't work for x=99-9 sort of cases where the students put done at the very
					// first step
					// As a result this is marked as correct even when it is not
					if (firstPrev.length() == 1 && Character.isLetter(firstPrev.charAt(0))) {
						boolean valid = true;
						for (int i = 0; i < secondPrev.length(); i++) {

							if (((secondPrev.charAt(0) == '-') && i == 0) || (secondPrev.charAt(i) == '.')) // Ignore if
																											// the
																											// number is
																											// a
																											// negative
																											// or has a
																											// decimal
																											// in it for
																											// example x
																											// = 23.5

								continue;
							if (secondPrev.charAt(i) == '/') {
								if (secondPrev.substring(i + 1).contains("/")) {
									valid = false;
									break;
								}
							} else if (!Character.isDigit(secondPrev.charAt(i))) {
								valid = false;
								break;
							}
						}
						if (secondPrev.contains(firstPrev) || secondPrev.contains(" ") || !valid) {
							return false;
						} else
							return true;
					}
					if (secondPrev.length() == 1 && Character.isLetter(secondPrev.charAt(0))) {
						boolean valid = true;
						for (int i = 0; i < firstPrev.length(); i++) {

							if (((firstPrev.charAt(0) == '-') && i == 0) || (firstPrev.charAt(i) == '.'))

								continue;
							if (firstPrev.charAt(i) == '/') {
								if (firstPrev.substring(i + 1).contains("/")) {
									valid = false;
									break;
								}
							} else if (!Character.isDigit(firstPrev.charAt(i))) {
								valid = false;
								break;
							}
						}
						if (firstPrev.contains(secondPrev) || firstPrev.contains(" ")) {
							return false;
						} else
							return true;
					}

				}
				if (!edge.isCorrect()) {
					return false;
				}
				firstPrev = secondPrev;
				secondPrev = edge.getInput();
			}
		}

		return false;
	}

	private void displayQuizResultsAlgebra(Vector<Vector<ProblemEdge>> results) {

		SimStPeerTutoringPlatform ssPTP = getSimStPeerTutoringPlatform();
		String solutions = formatSolutionsAlgebra(results);
		// String solutions = "<html><br>TEST <font color=red>RED</font></html>";

		if (currentCorrect == getSimSt().getProblemsPerQuiz() && getSimSt().getProblemsPerQuiz() != 0
				|| (currentProblem + currentCorrect) == quizProblems.size()) {
			int numSections = (int) Math.ceil(quizProblems.size() / ((double) getSimSt().getProblemsPerQuiz()));
			int sectionNum = ((currentProblem * numSections) / quizProblems.size()) + 1;
			ssPTP.displayQuizResults(solutions, sectionNum, true, getQuizLevel() + 1);
		} else if (getSimSt().getProblemsPerQuiz() != 0) {
			int numSections = (int) Math.ceil(quizProblems.size() / ((double) getSimSt().getProblemsPerQuiz()));
			int sectionNum = ((currentProblem * numSections) / quizProblems.size()) + 1;
			ssPTP.displayQuizResults(solutions, sectionNum, false, getQuizLevel() + 1);
		} else {
			ssPTP.displayQuizResults(solutions);
		}

	}

	private String formatSolutionsAlgebra(Vector<Vector<ProblemEdge>> results) {

		if (results.size() == 0) {
			trace.out("ss", quizProblems.size() + " " + quizProblems.get(0));
		}

		String solutions = "<html>";

		if (getSimSt().getProblemsPerQuiz() > 0) {
			int numSections = (int) Math.ceil(quizProblems.size() / ((double) getSimSt().getProblemsPerQuiz()));
			int sectionNum = ((currentProblem * numSections) / quizProblems.size()) + 1;
			solutions += "+++ Quiz Section " + sectionNum + " of " + numSections + " ++<br><br>";
		}

		int i = 0;
		int nCorrect = 0;
		for (Vector<ProblemEdge> path : results) {

			// String solution = formatAlgebraSolutions(path,
			// getRandomizedQuizProblem(currentProblem+i));
			String solution = "";
			if (simSt.getProblemAssessor() != null)
				solution = simSt.getProblemAssessor().formatSolution(path, getRandomizedQuizProblem(i));

			if (solution.indexOf("green") > solution.indexOf("red") && solution.indexOf("done") > 0)
				nCorrect++;

			solutions += "++++ Problem No." + (i + 1) + " ++++++++++<br>";
			solutions += solution + "<br>";
			i++;
		}
		int partialPercent = (nCorrect * 100 / results.size());
		int percent = ((currentCorrect + currentProblem) * 100 / quizProblems.size());
		if (percent >= 100) {
			quizPassed = true;
		}
		if (getSimSt().getProblemsPerQuiz() > 0) {
			solutions += "++ Score:<br>++This section: " + nCorrect + "/" + results.size() + " (" + partialPercent
					+ "%)<br>";
			solutions += "++ Overall: " + (currentCorrect + currentProblem) + "/" + quizProblems.size() + " (" + percent
					+ "%)<br>";
		} else {
			solutions += "++ Score: " + nCorrect + "/" + results.size() + " (" + partialPercent + "%)<br>";
		}
		solutions += "</html>";

		// If either LHS or RHS has not been replaced already, remove it
		solutions = solutions.replace("LHS", "");
		solutions = solutions.replace("RHS", "");
		// System.out.println("solutions = " + solutions);
		return solutions;
	}

	private void displayQuizResultsAlgebraI(Vector<Vector<ProblemEdge>> results) {

		SimStPeerTutoringPlatform ssPTP = getSimStPeerTutoringPlatform();
		String solutions = formatSolutionsAlgebraV2(results);
		// String solutions = "<html><br>TEST <font color=red>RED</font></html>";

		if (currentCorrect == getSimSt().getProblemsPerQuiz() && getSimSt().getProblemsPerQuiz() != 0
				|| (currentProblem + currentCorrect) == quizProblems.size()) {
			int numSections = (int) Math.ceil(quizProblems.size() / ((double) getSimSt().getProblemsPerQuiz()));
			int sectionNum = ((currentProblem * numSections) / quizProblems.size()) + 1;
			ssPTP.displayQuizResults(solutions, sectionNum, true, getQuizLevel() + 1);
		} else if (getSimSt().getProblemsPerQuiz() != 0) {
			int numSections = (int) Math.ceil(quizProblems.size() / ((double) getSimSt().getProblemsPerQuiz()));
			int sectionNum = ((currentProblem * numSections) / quizProblems.size()) + 1;
			ssPTP.displayQuizResults(solutions, sectionNum, false, getQuizLevel() + 1);
		} else {
			ssPTP.displayQuizResults(solutions);
		}

	}

	private String formatSolutionsAlgebraV2(Vector<Vector<ProblemEdge>> results) {

		if (results.size() == 0) {
			trace.out("ss", quizProblems.size() + " " + quizProblems.get(0));
		}

		String solutions = "<html>";

		if (getSimSt().getProblemsPerQuiz() > 0) {
			int numSections = (int) Math.ceil(quizProblems.size() / ((double) getSimSt().getProblemsPerQuiz()));
			int sectionNum = ((currentProblem * numSections) / quizProblems.size()) + 1;
			solutions += "+++ Quiz Section " + sectionNum + " of " + numSections + " ++<br><br>";
		}

		int i = 0;
		int nCorrect = 0;
		for (Vector<ProblemEdge> path : results) {

			String solution = getSimSt().getProblemAssessor().formatSolution(path, getRandomizedQuizProblem(i));

			if (solution.indexOf("green") > solution.indexOf("red") && solution.indexOf("done") > 0)
				nCorrect++;

			solutions += "++++ Problem No." + (i + 1) + " ++++++++++<br>";
			solutions += solution + "<br>";
			i++;
		}
		int partialPercent = (nCorrect * 100 / results.size());
		int percent = ((currentCorrect + currentProblem) * 100 / quizProblems.size());
		if (percent >= 100) {
			quizPassed = true;
		}
		if (getSimSt().getProblemsPerQuiz() > 0) {
			solutions += "++ Score:<br>++This section: " + nCorrect + "/" + results.size() + " (" + partialPercent
					+ "%)<br>";
			solutions += "++ Overall: " + (currentCorrect + currentProblem) + "/" + quizProblems.size() + " (" + percent
					+ "%)<br>";
		} else {
			solutions += "++ Score: " + nCorrect + "/" + results.size() + " (" + partialPercent + "%)<br>";
		}
		solutions += "</html>";

		// If either LHS or RHS has not been replaced already, remove it
		solutions = solutions.replace("LHS", "");
		solutions = solutions.replace("RHS", "");
		// System.out.println("solutions = " + solutions);
		return solutions;
	}

	// ------------------------------
	// Title and label strings
	// ------------------------------

	private final String NEXT_PROBLEM_BUTTON_TEXT = "<html><p>Tutor $ Next Problem</p></html>";
	private final String NEXT_PROBLEM_DIALOG_TEXT = "Tutor $ Next Problem";
	private final String QUIZ_BUTTON_TEXT = "<html><p>Quiz $</p></html>";
	private final String CURRICULUM_BROWSER_BUTTON_TEXT = "<html><p>$</p><p>Introduction</p></html>"; // Curriculum
																										// Browser
																										// renamed to
																										// SimStudent
																										// Introduction
	private final String EXAMPLE_BUTTON_TEXT_SHOW = "<html><p>Show</p><p>Examples</p></html>";
	private final String EXAMPLE_BUTTON_TEXT_HIDE = "<html><p>Hide</p><p>Examples</p></html>";
	private final String ENTER_PROBLEM_BUTTON_TEXT = "<html><p>Click Here When</p><p>Problem Entered</p></html>";
	private final String UNDO_BUTTON_TEXT = "<html><p>Erase Last Step ($)</p></html>";
	private final String UNDO_DONE_BUTTON_TEXT = "<html><p>Problem Is Not Done</p></html>";
	private final String RESTART_BUTTON_TEXT = "<html><p>Restart Problem</p></html>";
	private final String PROBLEM_BANK_BUTTON_TEXT = "<html><p>Generate Bank</p><p>of Problems</html>";

	public String genCurrentLevelTitleString() {
		return "Prepare " + getSimStName() + " for Quiz Level " + getCurrentLevel();
	}

	private String replaceName(String str) {
		return str.replace("$", getSimStName());
	}

	public String getNextProblemButtonTitleString() {
		return replaceName(NEXT_PROBLEM_BUTTON_TEXT);
	}

	public String getProblemEnteredButtonString() {
		return ENTER_PROBLEM_BUTTON_TEXT;
	}

	public String getCurriculumBrowserButtonTitleString() {
		return replaceName(CURRICULUM_BROWSER_BUTTON_TEXT);
	}

	public String getShowExampleButtonTitleString() {
		return EXAMPLE_BUTTON_TEXT_SHOW;
	}

	public String getHideExampleButtonTitleString() {
		return EXAMPLE_BUTTON_TEXT_HIDE;
	}

	public String getUndoButtonTitleString() {
		return UNDO_BUTTON_TEXT.replace("$", "Last Step");
	}

	public String getUndoButtonTitleString(String step) {
		if (step.equalsIgnoreCase("done"))
			return UNDO_DONE_BUTTON_TEXT;
		return UNDO_BUTTON_TEXT.replace("$", step);
	}

	public String getRestartButtonTitleString() {
		return RESTART_BUTTON_TEXT;
	}

	public String getNextProblemDialogTitleString() {
		return replaceName(NEXT_PROBLEM_DIALOG_TEXT);
	}

	public String getQuizButtonTitleString() {
		return replaceName(QUIZ_BUTTON_TEXT);
	}

	public String getProblemBankButtonTitleString() {
		return PROBLEM_BANK_BUTTON_TEXT;
	}

	public void requestEnterNewProblem() {
		// JButton nextProblemButton =
		// getSimStPeerTutoringPlatform().getNextProblemButton();
		// nextProblemButton.setText(getProblemEnteredButtonString());
		// nextProblemButton.setEnabled(false);
		getSsInteractiveLearning().setkillMessageReceived(false);
		getBrController().startNewProblem();
		setAvatarStart();
		this.getConversation().setBehaviourDiscrepencyBroughtUp(false);
	}

	public void startProblem() {
		startProblem(false);
		// this.generateProblemBankTab();
	}

	public void startProblem(boolean restart) {

		if (trace.getDebugCode("miss"))
			trace.out("startProblem");
		if (!checkValidProblemEntered()) {
			setAvatarStart();
			return;
		}

		getSimSt().setIsInteractiveLearning(true);
		if (!runType.equalsIgnoreCase("springboot"))
			new Thread(new ProblemStartThread(restart)).start();
		else
			getSsInteractiveLearning().runInteractiveLearning();
	}

	class ProblemStartThread implements Runnable {
		boolean isRestart = false;

		ProblemStartThread(boolean restart) {
			isRestart = restart;
		}

		public void run() {

			getSsInteractiveLearning().createStartState(getSimStPeerTutoringPlatform().getStudentInterface(),
					isRestart);
			new Thread(getSsInteractiveLearning()).start();
		}
	}

	/**
	 * Helper/Utility method to derive current problem from the start state elements
	 */
	private String getCurrentProblem() {

		String problem = "";
		for (int i = 0; i < startStateElements.size(); i++) {
			String element = startStateElements.get(i);
			Object widget = brController.lookupWidgetByName(element);
			if (widget != null && widget instanceof TableExpressionCell) {
				TableExpressionCell cell = (TableExpressionCell) widget;
				String input = cell.getText();
				if (i + 1 == startStateElements.size()) {
					problem += "=";
					problem += input;
				} else {
					problem += input;
				}
			}
		}
		return problem;
	}

	public boolean checkValidProblemEntered() {
		if (!runType.equalsIgnoreCase("springboot")) {
//			return validateQuestion();
			for (int i = 0; i < startStateElements.size(); i++) {
				String element = startStateElements.get(i);
				Object widget = brController.lookupWidgetByName(element);
				if (widget != null && widget instanceof TableExpressionCell) {
					TableExpressionCell cell = (TableExpressionCell) widget;
					String input = cell.getText();
					if (input.length() < 1) {
						giveMessage(ENTER_FULL_PROBLEM);
						return false;
					}
					if (getSimSt().isInputCheckerDefined()
							&& !getSimSt().getInputChecker().checkInput(element, input, null, this.getBrController())) {
						giveMessage(getSimSt().getInputChecker().invalidInputMessage(element, input, null));
						return false;
					}
				}
			}
		} else {
			for (Map.Entry<String, String> state: startState.entrySet()) {
				String element = state.getKey();
				String input = state.getValue();
				if (input.length() < 1) {
					invalidProblemMsg = ENTER_FULL_PROBLEM;
					return false;
				}
				if (getSimSt().isInputCheckerDefined()
					&& !getSimSt().getInputChecker().checkInput(element, input, null, this.getBrController())) {
					giveMessage(getSimSt().getInputChecker().invalidInputMessage(element, input, null));
					return false;
				}
			}
		}
		invalidProblemMsg = "";
		return true;
	}

	public void addStartStateListener(String element) {
		Object widget = brController.lookupWidgetByName(element);
		if (widget != null && widget instanceof TableExpressionCell) {
			TableExpressionCell cell = (TableExpressionCell) widget;
			cell.addCaretListener(new StartStateChangeListener());
			cell.addFocusListener(new StartStateChangeListener());
		}
	}

	public void addFoAStateListener(String element) {
		Object widget = brController.lookupWidgetByName(element);
		// System.out.println(" Widget "+widget.toString()+" Name : "+element);
		if (widget != null && widget instanceof TableExpressionCell) {
			TableExpressionCell cell = (TableExpressionCell) widget;
			cell.addCaretListener(new FoAStateChangeListener(element));
			cell.addFocusListener(new FoAStateChangeListener(element));
		}
	}

	class StartStateChangeListener implements CaretListener, FocusListener {

		@Override
		public void caretUpdate(CaretEvent arg0) {
			if (/* !getSimStPeerTutoringPlatform().getButtonsShowing() && */ brController.getCurrentNode() == null) {
				checkCompletedStartState();
			}
		}

		@Override
		public void focusGained(FocusEvent arg0) {
		}

		@Override
		public void focusLost(FocusEvent arg0) {

			// Try to model-trace the student entered left hand side and right hand side of
			// the problem
			if (simSt != null && simSt.isSsMetaTutorMode()) {
				if (arg0.getComponent() instanceof TableExpressionCell) {

					if (simSt.getBrController() != null && simSt.getBrController().getAmt() != null) {

						ArrayList<String> ssElements = getStartStateElements();
						for (String s : ssElements) {

							Object widget = brController.lookupWidgetByName(s);
							if (widget != null && widget instanceof TableExpressionCell) {

								TableExpressionCell cell = (TableExpressionCell) widget;
								String input = cell.getText();
								if (input != null && input.length() > 0) {

									/*
									 * if we are in AplusControl mode DO NOT update the model tracer until student
									 * gives a problem (i.e. we don't want the start state to be updated until we
									 * have the whole problem
									 */
									// if (simSt.isSsAplusCtrlCogTutorMode() &&
									// !simSt.getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().lockProblemEntering)
									// return;

									simSt.getBrController().getAmt().handleInterfaceAction(cell.getCommName(),
											"UpdateTable", input);

								}
							}
						}
					}
				}
			}

			/*
			 * if(arg0.getOppositeComponent() instanceof JButton) { JButton buttonClicked =
			 * (JButton) arg0.getOppositeComponent();
			 * if(buttonClicked.getActionCommand().equalsIgnoreCase("configure") ||
			 * buttonClicked.getActionCommand().equalsIgnoreCase(" OK ") ||
			 * buttonClicked.getActionCommand().equalsIgnoreCase(" Next Hint >> ") ||
			 * buttonClicked.getActionCommand().equalsIgnoreCase(" << Previous Hint "))
			 * return; for(int i=0;i<buttonClicked.getActionListeners().length;i++) {
			 * ActionEvent e = new ActionEvent(buttonClicked,
			 * ActionEvent.ACTION_PERFORMED,buttonClicked.getActionCommand());
			 * buttonClicked.getActionListeners()[i].actionPerformed(e); } }
			 */
		}

	}

	class FoAStateChangeListener implements CaretListener, FocusListener {

		String foa = null;

		public FoAStateChangeListener(String element) {
			// TODO Auto-generated constructor stub
			foa = element;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
		 * Looks to check updates in the cell where the wrong FoA has been specified. If
		 * the cell becomes empty then this unlocks all the cells where the possible
		 * text can be entered.
		 */
		@Override
		public void caretUpdate(CaretEvent e) {

			if (simSt != null && !simSt.isSsCogTutorMode()) {
				TableExpressionCell cell1 = (TableExpressionCell) brController.lookupWidgetByName(foa);
				if (!cell1.getText().isEmpty()) {
					JCommButton doneButton = (JCommButton) brController.lookupWidgetByName("Done");
					doneButton.setEnabled(false);
					doneButton.setText(DONE_CAPTION_DISABLED);
				} else {
					JCommButton doneButton = (JCommButton) brController.lookupWidgetByName("Done");
					doneButton.setEnabled(true);
					doneButton.setText(DONE_CAPTION_ENABLED);

				}
			} else {
				JCommButton doneButton = (JCommButton) brController.lookupWidgetByName("Done");
				doneButton.setForeground(Color.BLACK);
			}

			// TODO Auto-generated method stub
			if (simSt != null && simSt.getSsInteractiveLearning() != null
					&& !simSt.getSsInteractiveLearning().foaCorrectness) {

				if (simSt.getSsInteractiveLearning().getHint().getSai().getS().length() > 0) {

					String selection = simSt.getSsInteractiveLearning().getHint().getSai().getS();

					if (brController.lookupWidgetByName(selection) instanceof TableExpressionCell) {

						TableExpressionCell cell = (TableExpressionCell) brController.lookupWidgetByName(selection);
						if (cell.getText().isEmpty()) {
							blockInputWrongFoaEmpty(false); // Unlock all the cells where the student can enter the
															// input
						}
					}
				}
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			if (simSt.isSsAplusCtrlCogTutorMode()) {
				JCommButton doneButton = (JCommButton) brController.lookupWidgetByName("Done");
				doneButton.setEnabled(true);

			}
		}
	}

	public boolean isStartStateCompleted = false;

	public void setIsStartStateCompleted(boolean flag) {
		this.isStartStateCompleted = flag;
	}

	public boolean getIsStartStatecompleted() {
		return this.isStartStateCompleted;
	}

	boolean isRestartClicked = false;

	public void setIsRestartClicked(boolean flag) {
		this.isRestartClicked = flag;
	}

	boolean getIsRestartClicked() {
		return this.isRestartClicked;
	}

	public void checkCompletedStartState() {
		String element = null, input = null;
		if (getSsInteractiveLearning() != null && getSsInteractiveLearning().isTakingQuiz())
			return;

		if (!getSimSt().isSsCogTutorMode() && getSimStPeerTutoringPlatform().getButtonsShowing())
			return;

		HashMap componentValues = new HashMap();
		Component[] list = getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getStudentInterface()
				.getComponents();

		if (getSimSt().isSsAplusCtrlCogTutorMode()) {
			/*
			 * for Aplus control, because 2 tutorin interfaces confuse CTAT funtions, make
			 * sure we check the correct tutoring interface
			 */
			for (int i = 0; i < startStateElements.size(); i++) {
				element = startStateElements.get(i);
				input = getMissController().getSimStPLE().getSsInteractiveLearning().lookUpWidgetValue(list, element);
				if (input.length() < 1) {

					if (!getIsRestartClicked())
						((AplusPlatform) this.getSimStPeerTutoringPlatform()).setAplusControlConfirmPanelVisible(false);

					setIsStartStateCompleted(false);
					return;
				}
			}

		}

		else {
			/* for all other cases go with the normal checking... */
			for (int i = 0; i < startStateElements.size(); i++) {
				element = startStateElements.get(i);
				Object widget = brController.lookupWidgetByName(element);
				if (widget != null && widget instanceof TableExpressionCell) {
					TableExpressionCell cell = (TableExpressionCell) widget;
					input = cell.getText().toLowerCase();

					if (input.length() < 1) {
						setIsStartStateCompleted(false);
						return;
					}

				}
			}

		}

		if (!getSimSt().isSsCogTutorMode())
			simSt.displayActionListenerMessage(conversation.getMessage(SimStConversation.START_PROBLEM_TOPIC),
					new SimpleListener());
		else {
			if (getSimSt().isSsAplusCtrlCogTutorMode() && !getIsRestartClicked())
				((AplusPlatform) this.getSimStPeerTutoringPlatform()).setAplusControlConfirmPanelVisible(true);

			if (getSimSt().isSsAplusCtrlCogTutorMode() && !this.getSsCognitiveTutor().lockProblemEntering) {
				/*
				 * so when we are in AplusControl mode, with focus lost we can start the problem
				 */
				// setIsStartStateCompleted(true);
			}

		}

	}

	String cellText1 = null, cellText2 = null;

	public boolean checkVariableUsedStartState() {
		if (!getSimSt().isInputCheckerDefined())
			return true; // Assume good if no checker

		for (int i = 0; i < startStateElements.size(); i++) {
			String element = startStateElements.get(i);
			Object widget = brController.lookupWidgetByName(element);
			if (widget != null && widget instanceof TableExpressionCell) {
				TableExpressionCell cell = (TableExpressionCell) widget;
				String input = cell.getText();
				if (i == 0)
					cellText1 = input.toLowerCase();
				else if (i == 1)
					cellText2 = input.toLowerCase();
			}
		}

		return (getSimSt().getInputChecker().checkVariables(cellText1, cellText2));
	}

	private String askNewProblem() {
		String message = ASK_NEW_PROBLEM_MSG;
		String title = getNextProblemDialogTitleString();
		String problem = (String) JOptionPane.showInputDialog(getSimStPeerTutoringPlatform(), message, title,
				JOptionPane.QUESTION_MESSAGE);
		// if(getSimSt().isSsMetaTutorMode()) {
		// getSimSt().getModelTraceWM().getEventHistory().add(0,
		// getSimSt().getModelTraceWM().new Event(SimStLogger.PROBLEM_REQUEST_ACTION));
		// }
		logger.simStLog(SimStLogger.SIM_STUDENT_DIALOGUE, SimStLogger.PROBLEM_REQUEST_ACTION, problem,
				title + ":" + message);
		return problem;
	}

	public void blockInputWrongFoaEmpty(boolean isBlocking) {

		Component[] components = getSimStPeerTutoringPlatform().getStudentInterface().getComponents();
		for (int i = 0; i < components.length; i++) {
			setComponentEnabled(!isBlocking, components[i]);
		}
	}

	public void blockInput(boolean isBlocking) {
		// trace.out("*****************************");
		// Thread.dumpStack();
		// trace.out("*****************************");
		Component[] components = getSimStPeerTutoringPlatform().getStudentInterface().getComponents();
		for (int i = 0; i < components.length; i++) {
			setComponentEnabled(!isBlocking, components[i]);
		}

		// If unblocked, have it find a cell to focus to
		if (!isBlocking)
			setCorrectFocus();
	}

	public void unblockAllButStartState(boolean isBlocking) {

		Component[] components = getSimStPeerTutoringPlatform().getStudentInterface().getComponents();
		for (int i = 0; i < components.length; i++) {
			setComponentEnabled1(!isBlocking, components[i]);

		}

	}

	/**
	 * Method to toggle if start state elements are focusable.
	 * 
	 * @param flag
	 */
	public void setFocusOfStartStateElements(boolean flag) {

		for (String startStateElementString : getStartStateElements()) {
			JComponent widget = (JComponent) getBrController().lookupWidgetByName(startStateElementString);
			widget.setFocusable(flag);
		}
	}

	/**
	 * Method to toggle if start state elements are focusable. This enforces to be
	 * on the student interface!
	 * 
	 * @param flag
	 */
	public void setFocusOfStartStateElementsStudentInterface(boolean flag) {

		for (String startStateElementString : getStartStateElements()) {

			Component[] list = getSimStPeerTutoringPlatform().getStudentInterface().getComponents();

			getSsInteractiveLearning().getStudentInterfaceWidget(list, startStateElementString);
			getSsInteractiveLearning().tmpComponent.setFocusable(flag);

		}
	}

	public void unblockOnly(String toUnblock) {
		blockInput(true);
		Component unblockComp = (Component) brController.lookupWidgetByName(toUnblock);
		setComponentEnabled(true, unblockComp);
		unblockComp.requestFocus();
	}

	private void setCorrectFocus() {

		// ProblemNode s=null;
		// s.getName();
		// trace.out("set correct focus!");
		if (simSt.isSelectionOrderGetterDefined() && brController.getCurrentNode() != null
				&& brController.getCurrentNode().getInDegree() > 0) {
			ProblemEdge lastEdge = ((ProblemEdge) brController.getCurrentNode().getIncomingEdges().get(0));
			String lastSelection = lastEdge.getSelection();
			String nextComponent = "";
			if (lastEdge.getSource().getInDegree() > 0) {
				String priorSelection = ((ProblemEdge) lastEdge.getSource().getIncomingEdges().get(0)).getSelection();
				nextComponent = simSt.getSelectionOrderGetter().nextSelection(lastSelection, priorSelection);
			} else
				nextComponent = simSt.getSelectionOrderGetter().nextSelection(lastSelection);
			if (nextComponent == null)
				return;
			Object widget = brController.lookupWidgetByName(nextComponent);
			if (widget != null && widget instanceof Component) {
				((Component) widget).requestFocus();
			}
		} else if (simSt.isSelectionOrderGetterDefined()) {
			String nextComponent = simSt.getSelectionOrderGetter().startSelection();
			if (nextComponent == null)
				return;
			Object widget = brController.lookupWidgetByName(nextComponent);
			if (widget != null && widget instanceof Component) {
				((Component) widget).requestFocus();
			}
		}
	}

	boolean isAplusStartUp = true;

	public boolean isAplusStartUp() {
		return this.isAplusStartUp;
	}

	public void setAplusStartUp(boolean flag) {
		this.isAplusStartUp = flag;
	}

	/**
	 * Method to unblock the start state elements from the tutoring interface
	 */
	public void unblockStartStateElements() {
		// unblock only start state elements

		for (int i = 0; i < startStateElements.size(); i++) {
			Object widget = brController.lookupWidgetByName(startStateElements.get(i));
			if (widget != null && widget instanceof Component) {
				setComponentEnabled(true, (Component) widget);
			}
		}

	}

	public void setAvatarStart() {
		
		if (runType.equalsIgnoreCase("springBoot")) {
			status = NORMAL_STATUS;
			if (isAplusStartUp)
				setAplusStartUp(false);
			startStatus = true;
			this.avatarExpressions = "NORMAL_EXPRESSION";
			return;
		}

		status = NORMAL_STATUS;
		// Remove any other border and replace it with an empty border taking the same
		// space
		// getSimStPeerTutoringPlatform().getSimStAvatarLayerIcon().setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH,
		// BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
		blockInput(true);
		// unblock only start state elements
		unblockStartStateElements();

		getSimStPeerTutoringPlatform().refresh();
		AplusPlatform aplus = (AplusPlatform) getSimStPeerTutoringPlatform();

		/*
		 * Display the splash screen only the first time APLUS is launched If its not
		 * the first time AND we are in CogTutor mode, then either display problem &
		 * review examples (CogTutor), or just review examples (AplusCogTutor)
		 */

		if (isFirstTimeAPLUS()) {
			if (this.getSimSt().isSsCogTutorMode())
				aplus.showSplashScreen();
			// System.out.println("do nothing");
		} else if (this.getSimSt().isSsCogTutorMode() && isAplusStartUp) {
			this.cogTutorLaunchComplete();
			/* disable the restart button for AplusControl */
			if (getSimSt().isSsAplusCtrlCogTutorMode()) {
				aplus.setRestartButtonEnabled(false);

			}

		}

		/* set the startup flag to false so we know that APLUS is launched */
		if (isAplusStartUp)
			setAplusStartUp(false);

		getSimStPeerTutoringPlatform().refresh();

		/* refresh the interface */
		if (getSimSt().isSsCogTutorMode()) {
			getSimStPeerTutoringPlatform().refresh();

			return;
		}

		// 12/23/2014 nbarba: We decided to not clear the speech.
		// getSimStPeerTutoringPlatform().clearSpeech();
		// giveMessage(START_MESSAGE);

		giveMessage(conversation.getMessage(SimStConversation.NEW_PROBLEM_TOPIC));
		startStatus = true;
		// getSimStPeerTutoringPlatform().setImage(STUDENT_IMAGE);
		getSimStPeerTutoringPlatform().setExpression(NORMAL_EXPRESSION);
		getSimStPeerTutoringPlatform().setUndoButtonEnabled(false);
		getSimStPeerTutoringPlatform().setUndoButtonText(getUndoButtonTitleString());
		getSimStPeerTutoringPlatform().setRestartButtonEnabled(false);
		getSimStPeerTutoringPlatform().setWait(false);
		getSimStPeerTutoringPlatform().hideTakingQuiz();
	}

	boolean goingToQuiz = false;

	/**
	 * Method to control what happens after launching APLUS in cog tutor mode. a)
	 * For CogTutor mode, when splash screen is closed then give next problem &
	 * review examples. b) For Aplus Control mode, just show the examples
	 */
	public void cogTutorLaunchComplete() {

		if (goingToQuiz)
			return; /*
					 * if we want to quiz, then don't bother continue launching we are already where
					 * we want
					 */

		/*
		 * When splash screen is closed and we are in CogTutor mode than give next
		 * problem & review examples. If we are in Aplus Control mode, then just close
		 * and show the examples
		 */
		if (getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().isSsCogTutorMode()) {

			if (!getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt()
					.isSsAplusCtrlCogTutorMode())
				getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor()
						.giveNextProblem(true);

			if (this.isFirstTimeAPLUS() && getSimStPeerTutoringPlatform().getBrController().getMissController()
					.getSimSt().isSsAplusCtrlCogTutorMode()) {
				goingToQuiz = true;
				getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE()
						.setFirstTimeAPLUS(false);
				getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor()
						.gotoQuizSection();
			} else {
				if (this.isFirstTimeAPLUS()) {
					getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE()
							.setFirstTimeAPLUS(false);
					getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE()
							.getSsCognitiveTutor().reviewExampleSection(true);
				}
			}

		}

	}

	/*
	 * variable to hold students decision for message given by MrWilliams that have
	 * "Don't show this again" option"
	 */
	boolean showPopupAtExamples = true;

	public boolean getShowPopupAtExamples() {
		return showPopupAtExamples;
	}

	public void setShowPopupAtExamples(boolean flag) {
		this.showPopupAtExamples = flag;
	}

	boolean reviewExamplesAfterFail = false;

	public void setReviewExamplesAfterFail(boolean flag) {
		this.reviewExamplesAfterFail = flag;
	}

	public boolean getReviewExamplesAfterFail() {
		return this.reviewExamplesAfterFail;
	}

	public void setAvatarQuiz() {
		if (runType.equalsIgnoreCase("springBoot")) {
			startStatus = false;
			status = QUIZ_STATUS;
			this.avatarExpressions = "NORMAL_EXPRESSION";
			return;
		}
		startStatus = false;
		status = QUIZ_STATUS;
		// getSimStPeerTutoringPlatform().getSimStAvatarLayerIcon().setBorder(BorderFactory.createLineBorder(Color.blue,
		// BORDER_WIDTH));
		// Do not allow editing of values while avatar is taking quiz
		blockInput(true);
		getSimStPeerTutoringPlatform().refresh();
		// 12/23/2014 nbarba: We decided to not clear the speech.
		// getSimStPeerTutoringPlatform().clearSpeech();
		// startStatus = false;
		// getSimStPeerTutoringPlatform().setImage(STUDENT_THINK_IMAGE);
		getSimStPeerTutoringPlatform().setExpression(NORMAL_EXPRESSION);
		getSimStPeerTutoringPlatform().setUpTakingQuiz();
		getSimStPeerTutoringPlatform().setUndoButtonEnabled(false);
		getSimStPeerTutoringPlatform().setRestartButtonEnabled(false);
		getSimStPeerTutoringPlatform().setWait(true);

	}

	public void setAvatarFinished() {
		status = FINISHED_STATUS;
		// getSimStPeerTutoringPlatform().getSimStAvatarLayerIcon().setBorder(BorderFactory.createLineBorder(Color.green,
		// BORDER_WIDTH));
		// Do not allow editing of values once avatar is done
		if(!runType.equals("springBoot"))
			blockInput(true);
		// jinyul - Save instructions now that a particular problem has been finished.
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Enter setAvatarFinished to call autoSaveInstructions()");
		/** No need to save the instructions */
		// getMissController().autoSaveInstructions();
		getMissController().getSimSt().saveSimStState();
		// giveMessage(DONE_MESSAGE);
		giveMessage(conversation.getMessage(SimStConversation.SOLVED_TOPIC));
		startStatus = false;
		// getSimStPeerTutoringPlatform().setImage(STUDENT_SUCCESS_IMAGE);
		if(!runType.equals("springBoot")) {
			getSimStPeerTutoringPlatform().setExpression(SUCCESS_EXPRESSION);
			getSimStPeerTutoringPlatform().setUndoButtonEnabled(true);
			getSimStPeerTutoringPlatform().setRestartButtonEnabled(true);
			getSimStPeerTutoringPlatform().setWait(false);
		} else {
			this.avatarExpressions = "SUCCESS_EXPRESSION";
		}
		if (brController != null && brController.getMissController().getSimSt().isSsMetaTutorMode()) {
			// keep the previous problem just in case student clicks restart

			brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(null);

		}
	}

	public void setAvatarFinishedWrong() {
		status = FINISHED_STATUS;
		// getSimStPeerTutoringPlatform().getSimStAvatarLayerIcon().setBorder(BorderFactory.createLineBorder(Color.green,
		// BORDER_WIDTH));
		// Do not allow editing of values once avatar is done
		if(!runType.equals("springBoot"))
			blockInput(true);
		// jinyul - Save instructions now that a particular problem has been finished.
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Enter setAvatarFinished to call autoSaveInstructions()");
		/** No need to save the instructions */
		// getMissController().autoSaveInstructions();
		getMissController().getSimSt().saveSimStState();
		giveMessage(conversation.getMessage(SimStConversation.VERIFY_WRONG));
		startStatus = false;
		if(!runType.equals("springBoot")) {
			getSimStPeerTutoringPlatform().setExpression(CONFUSE_EXPRESSION);
			getSimStPeerTutoringPlatform().setUndoButtonEnabled(true);
			getSimStPeerTutoringPlatform().setRestartButtonEnabled(true);
			getSimStPeerTutoringPlatform().setWait(false);
		} else {
			this.avatarExpressions = "CONFUSE_EXPRESSION";
		}
		// if (brController!=null &&
		// brController.getMissController().getSimSt().isSsMetaTutorMode() )
		// brController.getMissController().getSimSt().getModelTraceWM().setStudentEnteredProblem(null);

	}

	// Displays that the avatar is thinking
	public void setAvatarThinking(String customMsg) {
		status = THINK_STATUS;
		// Add a yellow border while avatar is thinking
		// getSimStPeerTutoringPlatform().getSimStAvatarLayerIcon().setBorder(BorderFactory.createLineBorder(Color.yellow,
		// BORDER_WIDTH));
		// Do not allow editing of values while avatar is thinking - disable all cells
		// of tables
		if(!runType.equals("springBoot"))
			blockInput(true);
		// giveMessage(THINK_MESSAGE);
		if(customMsg == null)
			giveMessage(conversation.getMessage(SimStConversation.THINK_TOPIC));
		else if (!customMsg.isEmpty())
			giveMessage(customMsg);
		startStatus = false;
		// getSimStPeerTutoringPlatform().setImage(STUDENT_THINK_IMAGE);
		// getSimStPeerTutoringPlatform().setExpression(THINK_EXPRESSION_EX);
		if(!runType.equals("springBoot")) {
			getSimStPeerTutoringPlatform().setExpression(THINK_EXPRESSION_EX);
			getSimStPeerTutoringPlatform().setUndoButtonEnabled(false);
			getSimStPeerTutoringPlatform().setRestartButtonEnabled(true);
			getSimStPeerTutoringPlatform().setWait(true);
			scheduleSimStAvatarTimer();
		} else {
			this.avatarExpressions = "THINK_EXPRESSION_EX";
		}
		currentThinkingImage = THINK_EXPRESSION_EX;
		// currentThinkingImage=THINK_EXPRESSION_EX;

	}
	
	public void setAvatarThinking() {
		setAvatarThinking(null);
	}

	// Displays that the avatar is done thinking
	public void setAvatarNormal() {
		if (runType.equalsIgnoreCase("springBoot")) {
			status = NORMAL_STATUS;
			startStatus = false;
			this.avatarExpressions = isConfused() ? "CONFUSE_EXPRESSION" : "NORMAL_EXPRESSION";
			return;
		}
		status = NORMAL_STATUS;
		// Remove yellow thinking border and replace it with an empty border taking the
		// same space
		getSimStPeerTutoringPlatform().getSimStAvatarLayerIcon()
				.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
		// Re-enable all cells of tables in the student interface to allow for editing
		// again since thinking is done
		blockInput(false);
		startStatus = false;
		// getSimStPeerTutoringPlatform().setImage(STUDENT_IMAGE);
		if (isConfused())
			getSimStPeerTutoringPlatform().setExpression(CONFUSE_EXPRESSION);
		else
			getSimStPeerTutoringPlatform().setExpression(NORMAL_EXPRESSION);
		getSimStPeerTutoringPlatform().setUndoButtonEnabled(true);
		getSimStPeerTutoringPlatform().setRestartButtonEnabled(true);
		getSimStPeerTutoringPlatform().setWait(false);
		getSimStPeerTutoringPlatform().hideTakingQuiz();
		avatarTimer.cancel();
		avatarTimer.purge();
	}

	// Displays that the avatar is thinking
	public void setAvatarAsking() {
		if (runType.equalsIgnoreCase("springBoot")) {
			status = ASK_STATUS;
			startStatus = false;
			this.avatarExpressions = "ASK_EXPRESSION";
			return;
		}
		status = ASK_STATUS;
		// Add a yellow border while avatar is thinking
		// getSimStPeerTutoringPlatform().getSimStAvatarLayerIcon().setBorder(BorderFactory.createLineBorder(Color.yellow,
		// BORDER_WIDTH));
		// Do not allow editing of values while avatar is thinking - disable all cells
		// of tables
		blockInput(true);
		startStatus = false;
		// getSimStPeerTutoringPlatform().setImage(STUDENT_THINK_IMAGE);
		getSimStPeerTutoringPlatform().setExpression(ASK_EXPRESSION);
		getSimStPeerTutoringPlatform().setUndoButtonEnabled(false);
		getSimStPeerTutoringPlatform().setRestartButtonEnabled(true);
		getSimStPeerTutoringPlatform().setWait(false);
	}

	public void setAvatarConfused(boolean confusion) {
		// TODO Elaborate!
		confused = confusion;
		if (runType.equalsIgnoreCase("springBoot")) {
			this.avatarExpressions = confusion ? "CONFUSE_EXPRESSION" : "NORMAL_EXPRESSION";
			return;
		}
		if (confusion) {
			getSimStPeerTutoringPlatform().setExpression(CONFUSE_EXPRESSION);
			giveMessage(CONFUSE_MESSAGE);
		} else {
			getSimStPeerTutoringPlatform().setExpression(NORMAL_EXPRESSION);
			giveMessage(UNCONFUSE_MESSAGE);
		}
	}

	public void unBlockQuiz(boolean flag) {

		AplusPlatform aplus = ((AplusPlatform) getSimStPeerTutoringPlatform());
		/*
		 * Component[] components = aplus.quizInterface.getComponents(); for(int
		 * i=0;i<components.length;i++) { setComponentEnabled(flag, components[i]);
		 * 
		 * }
		 * 
		 */
		SimStPLE.setComponentEnabled(flag, aplus.quizInterface);

	}

	public TableExpressionCell lookUpWidgetQuiz(String name) {
		AplusPlatform aplus = ((AplusPlatform) getSimStPeerTutoringPlatform());
		selectionCell = null;
		lookUpWidgetQuiz(name, aplus.quizInterface);
		return selectionCell;
	}

	TableExpressionCell selectionCell = null;

	public void lookUpWidgetQuiz(String name, Component comp) {
		if (comp instanceof JCommTable) {
			JCommTable table = (JCommTable) comp;
			TableExpressionCell[][] cells = table.getCells();
			for (int j = 0; j < cells.length; j++) {
				for (int k = 0; k < cells[j].length; k++) {
					if (name.equals(cells[j][k].getCommName()))
						selectionCell = cells[j][k];

				}
			}
		}
		// Recursively search inside a container for setting
		else if (comp instanceof Container) {
			Component[] compsInComp = ((Container) comp).getComponents();
			for (int m = 0; m < compsInComp.length; m++) {
				lookUpWidgetQuiz(name, compsInComp[m]);
			}
		}
	}

	public static boolean isAplusControlMode = false;

	// Keiser - 11/24/2009 - enables or disables the cells of any CommTables and
	// any JButtons - recursively looks inside of any containers to find CommTables
	// or JButtons hidden there
	public static void setComponentEnabled(boolean enabled, Component comp) {

		// Individually enable or disable cells of a comm table
		if (comp instanceof JCommTable) {
			JCommTable table = (JCommTable) comp;
			TableExpressionCell[][] cells = table.getCells();
			for (int j = 0; j < cells.length; j++) {
				for (int k = 0; k < cells[j].length; k++) {

					cells[j][k].setEnabled(enabled);
					// cells[j][k].setBackground(Color.white);
					if (enabled) {
						if (cells[j][k].getBackground() != Color.pink)
							cells[j][k].setBackground(Color.white);
						Border outline = BorderFactory.createLineBorder(Color.darkGray, 1);
						Border space = BorderFactory.createLineBorder(Color.white, 5);
						cells[j][k].setBorder(BorderFactory.createCompoundBorder(outline, space));
					} else {

						if (isAplusControlMode) {

							if (cells[j][k].getBackground() != Color.pink) {
								cells[j][k].setBackground(new Color(222, 222, 222));
							}
							Border outline = BorderFactory.createLineBorder(Color.black, 1);
							Border space = BorderFactory.createLineBorder(Color.white, 2);
							cells[j][k].setBorder(BorderFactory.createCompoundBorder(outline, space));
						} else {
							if (cells[j][k].getBackground() != Color.pink) {
								cells[j][k].setBackground(Color.white);
							}
							Border outline = BorderFactory.createLineBorder(Color.lightGray, 1);
							Border space = BorderFactory.createLineBorder(Color.white, 5);
							cells[j][k].setBorder(BorderFactory.createCompoundBorder(outline, space));
						}

					}
				}
			}
		} else if (comp instanceof TableExpressionCell) {
			TableExpressionCell cell = (TableExpressionCell) comp;

			cell.setEnabled(enabled);
			// cell.setBackground(Color.white);
			if (enabled) {
				cell.setBackground(Color.white);
				Border outline = BorderFactory.createLineBorder(Color.darkGray, 1);
				Border space = BorderFactory.createLineBorder(Color.white, 5);
				cell.setBorder(BorderFactory.createCompoundBorder(outline, space));
			} else {
				cell.setBackground(Color.white);
				Border outline = BorderFactory.createLineBorder(Color.lightGray, 1);
				Border space = BorderFactory.createLineBorder(Color.white, 5);
				cell.setBorder(BorderFactory.createCompoundBorder(outline, space));
			}
		}
		// Disable dropdowns
		else if (comp instanceof JCommComboBox) {
			comp.setEnabled(enabled);
		}
		// Disable textfields
		else if (comp instanceof JTextField) {
			comp.setEnabled(enabled);
		}
		// Disable Buttons
		else if (comp instanceof javax.swing.JButton) {
			comp.setEnabled(enabled);
		}
		// Recursively search inside a container for setting
		else if (comp instanceof Container) {
			Component[] compsInComp = ((Container) comp).getComponents();
			for (int m = 0; m < compsInComp.length; m++) {
				setComponentEnabled(enabled, compsInComp[m]);
			}
		}
	}

	public static void setComponentEnabled1(boolean enabled, Component comp) {

		// Individually enable or disable cells of a comm table
		if (comp instanceof JCommTable) {
			JCommTable table = (JCommTable) comp;
			TableExpressionCell[][] cells = table.getCells();
			for (int j = 0; j < cells.length; j++) {
				for (int k = 0; k < cells[j].length; k++) {

					if (cells[j][k].getCommName().equals("dorminTable1_C1R1")
							|| cells[j][k].getCommName().equals("dorminTable2_C1R1")) {
						cells[j][k].setEnabled(enabled);

						if (enabled) {
							if (cells[j][k].getBackground() != Color.pink)
								cells[j][k].setBackground(Color.white);
							Border outline = BorderFactory.createLineBorder(Color.darkGray, 1);
							Border space = BorderFactory.createLineBorder(Color.white, 5);
							cells[j][k].setBorder(BorderFactory.createCompoundBorder(outline, space));
						} else {
							if (cells[j][k].getBackground() != Color.pink)
								cells[j][k].setBackground(Color.white);
							Border outline = BorderFactory.createLineBorder(Color.lightGray, 1);
							Border space = BorderFactory.createLineBorder(Color.white, 5);
							cells[j][k].setBorder(BorderFactory.createCompoundBorder(outline, space));
						}
					}

				}
			}
		} else if (comp instanceof TableExpressionCell) {

		}
		// Disable dropdowns
		else if (comp instanceof JCommComboBox) {
			comp.setEnabled(enabled);
		}
		// Disable textfields
		else if (comp instanceof JTextField) {
			comp.setEnabled(enabled);
		}
		// Disable Buttons
		else if (comp instanceof javax.swing.JButton) {
			// comp.setEnabled(enabled);
		}
		// Recursively search inside a container for setting
		else if (comp instanceof Container) {

			Component[] compsInComp = ((Container) comp).getComponents();
			for (int m = 0; m < compsInComp.length; m++) {
				setComponentEnabled1(enabled, compsInComp[m]);
			}
		}

	}

	// Sets the font for a component and all of its non-JCommLabel contents
	public static void setComponentFont(JComponent component, Font f) {
		component.setFont(f);
		if (component instanceof JCommTable.TableExpressionCell) {
			JCommTable.TableExpressionCell table = (JCommTable.TableExpressionCell) component;
			((JCommTable.TableExpressionCell) component).setDisabledTextColor(Color.DARK_GRAY);

		}
		Component[] comps = component.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JComponent && !(comps[i] instanceof JCommLabel))
				setComponentFont((JComponent) comps[i], f);
		}
	}

	// Keiser - recursively looks inside of any containers to find any CommWidgets,
	// setting
	// the colors used all to black
	public void removeComponentColor(Component comp) {
		// Individually enable or disable cells of a comm table
		if (comp instanceof JCommWidget) {
			JCommWidget widget = (JCommWidget) comp;
			widget.setCorrectColor(Color.black);
			widget.setIncorrectColor(Color.black);
			widget.setLISPCheckColor(Color.black);
		}
		// Recursively search inside a container for setting
		else if (comp instanceof Container) {
			Component[] compsInComp = ((Container) comp).getComponents();
			for (int m = 0; m < compsInComp.length; m++) {
				removeComponentColor(compsInComp[m]);
			}
		}
	}

	JPopupMenu undoContextMenu;
	String undoCandidate = "";
	JCommTable.TableExpressionCell cellToUse;

	public void setUpUndo(JComponent studentInterface) {
		undoContextMenu = new JPopupMenu();
		JMenuItem anItem = new JMenuItem("Erase back through this step");
		anItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// JOptionPane.showMessageDialog(null, "Erase "+undoCandidate);
				setFocusTab(SIM_ST_TAB);
				new Thread(new UndoThread(undoCandidate)).start();
			}
		});
		undoContextMenu.add(anItem);
		if (getSimSt().isSsMetaTutorMode()) {
			// TODO: Add new flag for cognitive help available and use that here
			anItem = new JMenuItem("Is this step correct?");
			anItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					isStepCorrect(cellToUse);
				}
			});
			undoContextMenu.add(anItem);
		}
		MouseListener undoMenuListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				if (event.isPopupTrigger()) {
					JCommTable.TableExpressionCell source = (JCommTable.TableExpressionCell) event.getSource();
					if (getStartStateElements().contains(source.getCommName()))
						return;
					if (source.getText().length() > 0) {
						undoContextMenu.show(event.getComponent(), event.getX(), event.getY());
						undoCandidate = source.getCommName();
						cellToUse = source;
					}
				}
			}
		};
		addUndoMenu(studentInterface, undoMenuListener);
	}

	public static void addUndoMenu(JComponent component, MouseListener ml) {
		if (component instanceof JCommTable.TableExpressionCell) {
			component.addMouseListener(ml);
		}
		Component[] comps = component.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JComponent && !(comps[i] instanceof JCommLabel))
				addUndoMenu((JComponent) comps[i], ml);
		}
	}

	/*
	 * isStepCorrect: Check if the step in a given cell is correct. If it occurred
	 * at some point in the br Graph, judge correctness at that point in the graph.
	 * If it did not, judge based on if it were to be done now. Gives a pop-up
	 * message with correctness.
	 * 
	 * @param the cell to be checked for correctness
	 */
	public void isStepCorrect(JCommTable.TableExpressionCell verifyCell) {
		// TODO: Make available to other widgets than TableExpressionCell

		// Determine SAI of cell.
		String selection = verifyCell.getCommName();
		String action = "UpdateTable";
		String input = verifyCell.getText();
		ProblemNode node = getBrController().getCurrentNode();

		// Search for the node in the current path of the br graph
		boolean nodeFound = false;
		while (node != null && node.getInDegree() > 0 && !nodeFound) {
			ProblemEdge edge = ((ProblemEdge) node.getIncomingEdges().get(0));
			String select = edge.getSelection();
			if (select.equals(selection)) {
				nodeFound = true;
			}
			node = edge.getSource();
		}

		// Node was not found, base it on current node
		if (!nodeFound)
			node = getBrController().getCurrentNode();

		// Determine problem name
		String problem = "";
		if (getSsInteractiveLearning() == null || !getSsInteractiveLearning().isTakingQuiz())
			problem = getBrController().getProblemModel().getStartNode().getName();
		else
			problem = simSt.getValidationGraph().getStartNode().getName(); // When taking quiz use the problemName as we
																			// donot have reference to the BR_Controller

		// Check Correctness
		String correctness = simSt.builtInInquiryClTutor(selection, action, input, node, problem);

		// Provide pop-up message with correctness
		String message = "";
		if (correctness.equals(EdgeData.CORRECT_ACTION)) {
			message = getConversation().getSimStMessage1(SimStConversation.STEP_CORRECT_TOPIC,
					getComponentName(selection), input);
		} else {
			message = getConversation().getSimStMessage1(SimStConversation.STEP_INCORRECT_TOPIC,
					getComponentName(selection), input);
		}
		// String message = "Based on the previous steps, "+input+" would be "
		// +()? "correct" : "incorrect")+" here.";
		getMissController().getSimStPLE().giveDialogMessage(message);
		Sai sai = new Sai(selection, action, input);
		logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.CONTEXT_MENU_QUESTION_ACTION,
				simSt.getProblemStepString(), "Is this step correct?", "", sai, 0, message);

	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Getters & setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void incCurrentLevel() {
		this.currentLevel++;
	}

	public BR_Controller getBrController() {
		return brController;
	}

	public void setBrController(BR_Controller brController) {
		this.brController = brController;
	}

	public MissController getMissController() {
		return missController;
	}

	public void setMissController(MissController missController) {
		this.missController = missController;
	}

	public SimSt getSimSt() {
		return simSt;
	}

	public void setSimSt(SimSt simSt) {
		this.simSt = simSt;
	}

	public SimStPeerTutoringPlatform getSimStPeerTutoringPlatform() {
		return simStPeerTutoringPlatform;
	}

	public void setSimStPeerTutoringPlatform(SimStPeerTutoringPlatform simStPeerTutoringPlatform) {
		this.simStPeerTutoringPlatform = simStPeerTutoringPlatform;
	}

	public String getSimStName() {
		return simSt.getSimStName();
	}

	public void setSimStName(String simStName) {
		simSt.setSimStName(simStName);
	}

	public String getTeacherName() {
		return simSt.getTeacherName();
	}

	public void setTeacherName(String teacherName) {
		simSt.setTeacherName(teacherName);
	}

	public SimStInteractiveLearning getSsInteractiveLearning() {
		return ssInteractiveLearning;
	}

	public void setSsInteractiveLearning(SimStInteractiveLearning ssInteractiveLearning) {
		this.ssInteractiveLearning = ssInteractiveLearning;
	}

	SimStCognitiveTutor ssCognitiveTutor;

	public SimStCognitiveTutor getSsCognitiveTutor() {
		return ssCognitiveTutor;
	}

	public void setSsCognitiveTutor(SimStCognitiveTutor ssCognitiveTutor) {
		this.ssCognitiveTutor = ssCognitiveTutor;
	}

	public void giveMessage(String message) {
		giveMessage(message, getSimStName());
	}
	
	public void giveMessage(String message, String name) {
		if (getSimStPeerTutoringPlatform() != null) {
			getSimStPeerTutoringPlatform().showButtons(false);

			// getSimStPeerTutoringPlatform().setSpeech(message);
			getSimStPeerTutoringPlatform().appendSpeech(message, name);
		}
	}
	
	public void giveDialogMessage(String message) {
		messageDialog.showMessage(message);
	}

	public void giveModalDialogMessage(String message) {
		messageDialog.setModal(true);
		messageDialog.showMessage(message);
		messageDialog.setModal(false);
	}

	public void setMessage(String message) {
		getSimStPeerTutoringPlatform().showButtons(false);
		getSimStPeerTutoringPlatform().setSpeech(message);

	}
	
	public int giveMessageRequiringAttention(String message) {
		// getSimStPeerTutoringPlatform().setSpeech(message);
		setAvatarAsking();
		getSimStPeerTutoringPlatform().appendSpeech(message, "");
		getSimStPeerTutoringPlatform().showContinueButton(true);

		if (this.getSimStPeerTutoringPlatform() != null)
			this.getSimStPeerTutoringPlatform().scrollPaneToBottom();

		YesNoBucket lock = new YesNoBucket();

		getSimStPeerTutoringPlatform().getYesResponseButton().addActionListener(new YesNoListener(lock));
		getSimStPeerTutoringPlatform().getYesResponseButton().setActionCommand("" + JOptionPane.YES_OPTION);
		getSimStPeerTutoringPlatform().getNoResponseButton().addActionListener(new YesNoListener(lock));
		getSimStPeerTutoringPlatform().getNoResponseButton().setActionCommand("" + JOptionPane.NO_OPTION);

		String response = lock.waitForYesNo();

		getSimStPeerTutoringPlatform().showContinueButton(false);

		return Integer.parseInt(response);

	}
	
	public int giveMessageRequiringResponse(String message) {
		// getSimStPeerTutoringPlatform().setSpeech(message);
		setAvatarAsking();
		getSimStPeerTutoringPlatform().appendSpeech(message, getSimStName());
		getSimStPeerTutoringPlatform().showButtons(true);

		if (this.getSimStPeerTutoringPlatform() != null)
			this.getSimStPeerTutoringPlatform().scrollPaneToBottom();

		YesNoBucket lock = new YesNoBucket();

		getSimStPeerTutoringPlatform().getYesResponseButton().addActionListener(new YesNoListener(lock));
		getSimStPeerTutoringPlatform().getYesResponseButton().setActionCommand("" + JOptionPane.YES_OPTION);
		getSimStPeerTutoringPlatform().getNoResponseButton().addActionListener(new YesNoListener(lock));
		getSimStPeerTutoringPlatform().getNoResponseButton().setActionCommand("" + JOptionPane.NO_OPTION);

		String response = lock.waitForYesNo();

		getSimStPeerTutoringPlatform().showButtons(false);

		if (response.equals(getSimStPeerTutoringPlatform().getYesResponseButton().getActionCommand())) {
			getSimStPeerTutoringPlatform().appendSpeech("Yes", "Me");
		} else {
			getSimStPeerTutoringPlatform().appendSpeech("No", "Me");
		}

		return Integer.parseInt(response);

	}

	public void giveMessagePossibleResponse(String message, ActionListener al) {
		// getSimStPeerTutoringPlatform().setSpeech(message);
		getSimStPeerTutoringPlatform().appendSpeech(message, getSimStName());
		getSimStPeerTutoringPlatform().showButtons(true);
		getSimStPeerTutoringPlatform().getYesResponseButton().addActionListener(al);
		getSimStPeerTutoringPlatform().getYesResponseButton().setActionCommand("" + JOptionPane.YES_OPTION);
		getSimStPeerTutoringPlatform().getNoResponseButton().addActionListener(al);
		getSimStPeerTutoringPlatform().getNoResponseButton().setActionCommand("" + JOptionPane.NO_OPTION);

	}

	public String giveMessageFreeTextResponse(String message) {
		LinkedBlockingQueue<String> bucket = new LinkedBlockingQueue<String>();

		this.setAvatarAsking();

		getSimStPeerTutoringPlatform().appendSpeech(message, getSimStName());
		getSimStPeerTutoringPlatform().showTextResponse(true);

		for (ActionListener al : getSimStPeerTutoringPlatform().getTextResponseSubmitButton().getActionListeners()) {
			getSimStPeerTutoringPlatform().getTextResponseSubmitButton().removeActionListener(al);
		}
		getSimStPeerTutoringPlatform().getTextResponseSubmitButton().addActionListener(new TextEntryListener(bucket));

		String response = "";

		getSimStPeerTutoringPlatform().scrollPaneToBottom();

		try {
			response = bucket.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (response.length() > 0 && !response.equals("K-1"))
			getSimStPeerTutoringPlatform().appendSpeech(response, "Me");
		// getSimStPeerTutoringPlatform().getTextResponse().setText("");
		getSimStPeerTutoringPlatform().getTextResponse().setSelectedItem("");

		getSimStPeerTutoringPlatform().showTextResponse(false);
		this.setAvatarNormal();

		return response;

	}

	public String giveMessageSelectableResponse(String message, List<String> selections) {
		LinkedBlockingQueue<String> bucket = new LinkedBlockingQueue<String>();

		this.setAvatarAsking();

		getSimStPeerTutoringPlatform().appendSpeech(message, getSimStName());
		getSimStPeerTutoringPlatform().showTextResponseOptions(true, selections);

		for (ActionListener al : getSimStPeerTutoringPlatform().getTextResponseSubmitButton().getActionListeners()) {
			getSimStPeerTutoringPlatform().getTextResponseSubmitButton().removeActionListener(al);
		}
		getSimStPeerTutoringPlatform().getTextResponseSubmitButton().addActionListener(new TextEntryListener(bucket));

		String response = "";

		getSimStPeerTutoringPlatform().scrollPaneToBottom();

		try {
			response = bucket.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (response.length() > 0) {
			String trim_response = response.split("K")[0];
			getSimStPeerTutoringPlatform().appendSpeech(trim_response, "Me");
		}
		// getSimStPeerTutoringPlatform().getTextResponse().setText("");
		getSimStPeerTutoringPlatform().getTextResponse().setSelectedItem("");

		getSimStPeerTutoringPlatform().showTextResponseOptions(false, null);
		this.setAvatarNormal();

		return response;

	}
	
	// Tasmia
	// This is essentially the same function as the above with few added lines. I made a new one because the earlier one was not working
	 /*
	  public String giveMessageSelectableResponse_(String message, List<String> selections)
	    {
		  	
	    	LinkedBlockingQueue<String> bucket = new LinkedBlockingQueue<String>();
	    	
	    	brController.getMissController().getSimStPLE().setAvatarAsking();
	        brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().appendSpeech(message,getSimStName());
	   
	    	
	        brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().showTextResponseOptions__(true,selections);
	
	        for (ActionListener al : getSimStPeerTutoringPlatform().getTextResponseSubmitButton().getActionListeners()) {
				getSimStPeerTutoringPlatform().getTextResponseSubmitButton().removeActionListener(al);
			}
			getSimStPeerTutoringPlatform().getTextResponseSubmitButton().addActionListener(new TextEntryListener(bucket));

			String response = "";

			getSimStPeerTutoringPlatform().scrollPaneToBottom();

			try {
				response = bucket.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (response.length() > 0)
				getSimStPeerTutoringPlatform().appendSpeech(response, "Me");
			// getSimStPeerTutoringPlatform().getTextResponse().setText("");
			getSimStPeerTutoringPlatform().getTextResponse().setSelectedItem("");

			getSimStPeerTutoringPlatform().showTextResponseOptions(false, null);
			this.setAvatarNormal();
	    	
			return response;
	    	

	    }
	 */

	class TextEntryListener implements ActionListener {

		BlockingQueue<String> bucket;

		TextEntryListener(BlockingQueue<String> bucket) {
			this.bucket = bucket;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * if(e.getModifiers() > 0) { //The user selected an option instead of entered
			 * it. if(e.getSource() instanceof JComboBox) { JComboBox combo = ((JComboBox)
			 * e.getSource()); combo.setSelectedItem(combo.getSelectedItem()); } return; }
			 */
			String response = "";
			if (e.getSource() instanceof JTextField) {
				response = ((JTextField) e.getSource()).getText();
			}
			if (e.getSource() instanceof JComboBox) {
				if (((JComboBox) e.getSource()).getSelectedItem() == null)
					return;
				if (((JComboBox) e.getSource()).getSelectedItem().equals(SELECT_OPTION))
					return;
					//response = "";
				else {
					response = (String) ((JComboBox) e.getSource()).getSelectedItem();
					int index = (int)((JComboBox) e.getSource()).getSelectedIndex();
					if(index != -1)
						response = response + "K" + index;
					
				}
			}
			if (e.getSource() == getSimStPeerTutoringPlatform().getTextResponseSubmitButton()) {
				JComboBox combo = getSimStPeerTutoringPlatform().getTextResponse();

				if (combo.getSelectedItem() == null)
					response = "";
				else if (combo.getSelectedItem().equals(SELECT_OPTION))
					return;
					//response = "";
				else {
					response = (String) (combo.getSelectedItem());
					int index = (int)(combo.getSelectedIndex());
					if(index != -1)
						response = response + "K" + index;
				}

			}
			try {

				bucket.put(response);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			getSimStPeerTutoringPlatform().getTextResponseSubmitButton().removeActionListener(this);

		}

	}

	class SimpleListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			getSimStPeerTutoringPlatform().showButtons(false);

			if (e.getActionCommand().equals("" + JOptionPane.YES_OPTION)) {
				getSimStPeerTutoringPlatform().appendSpeech("Yes", "Me");
				if (simSt.isSsMetaTutorMode()) {
					if (brController.getAmt() != null) {
						brController.getAmt().handleInterfaceAction("yes", "ButtonPressed", "-1");
					}
				}
			} else if (e.getActionCommand().equals("" + JOptionPane.NO_OPTION)) {
				getSimStPeerTutoringPlatform().appendSpeech("No", "Me");
				if (simSt.isSsMetaTutorMode()) {
					if (brController.getAmt() != null) {
						brController.getAmt().handleInterfaceAction("no", "ButtonPressed", "-1");
					}
				}
			}

			// After printing response, deal with argument accordingly
			startQuestion(e.getActionCommand());
		}

	}

	/**
	 * Helper method that returns the problem that was entered on the interface. To
	 * make this method domain independent, return value is of the form (e.g. for
	 * Algebra) {LHS}{RHS}.
	 * 
	 * @return
	 */
	private String getEnteredProblem() {
		String returnString = "";
		String element = "";

		for (int i = 0; i < startStateElements.size(); i++) {
			element = startStateElements.get(i);
			Object widget = brController.lookupWidgetByName(element);
			if (widget != null && widget instanceof TableExpressionCell) {
				TableExpressionCell cell = (TableExpressionCell) widget;
				returnString += "{" + cell.getText() + "}";
			} else if (widget != null && widget instanceof JCommTextField) {
				JCommTextField textField = (JCommTextField) widget;
				returnString += "{" + textField.getText() + "}";
			} else if (widget != null && widget instanceof JCommComboBox) {
				JCommComboBox dropDownField = (JCommComboBox) widget;
				returnString += "{" + dropDownField.getText() + "}";
			}

		}
		return returnString;
	}

	// Begin solving problem if user has selected yes
	public void startQuestion(String arg) {
		if (arg != null && arg.equals("" + JOptionPane.YES_OPTION)) {
			if (validateQuestion()) {
				startProblem();

			}
		} else if (arg != null && arg.equals("" + JOptionPane.NO_OPTION)) {
			giveMessage(conversation.getMessage(SimStConversation.START_PROBLEM_NO_TOPIC));
		} else {
			giveMessage(START_MESSAGE);
		}
	}

	public boolean validateQuestion() {
		List<String> elements = new ArrayList<String>();
		List<String> inputs = new ArrayList<String>();
		List<String> quesMessage = new ArrayList<String>();
		if (!runType.equalsIgnoreCase("springboot")) {
			String element = null;
			String input = null;
			for (int i = 0; i < startStateElements.size(); i++) {
				element = startStateElements.get(i);
				Object widget = brController.lookupWidgetByName(element);
				if (widget != null && widget instanceof TableExpressionCell) {
					elements.add(element);
					TableExpressionCell cell = (TableExpressionCell) widget;
					input = cell.getText().toLowerCase();
					inputs.add(input);
				}
			}
		} else {
			for (Map.Entry<String, String> state: startState.entrySet()) {
				elements.add(state.getKey());
				inputs.add(state.getValue());
			}
		}
		quesMessage = checkQuestion(elements, inputs);
		if(quesMessage.size() > 1) {
			giveDialogMessage(quesMessage.get(1));
		}
		return Boolean.parseBoolean(quesMessage.get(0));
	}

	public List<String> checkQuestion(List<String> elements, List<String> inputs) {
		List<String> quesMessage = new ArrayList<String>();
		for (int i = 0; i < elements.size(); i++) {
			String element = elements.get(i);
			String input = inputs.get(i);
			if (input.length() < 1) {
				quesMessage.add("false");
				quesMessage.add(ENTER_FULL_PROBLEM);
				return quesMessage;
			}
			if (getSimSt().isInputCheckerDefined()
					&& !getSimSt().getInputChecker().checkInput(element, input, null, this.getBrController())) {
				quesMessage.add("false");
				quesMessage.add(getSimSt().getInputChecker().invalidInputMessage(element, input, null));
				return quesMessage;
			}
		}

		boolean passed = false;
		String problemName1 = "";
		if (runType.equals("springBoot")) { 
			passed = getSimSt().getInputChecker().checkVariables(inputs.get(0), inputs.get(1));
    		problemName1 = createComponentName(inputs);
		} else {
			passed = checkVariableUsedStartState();
			problemName1 = simSt.getSsInteractiveLearning()
					.createName(getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
		}
		String prob = SimSt.convertFromSafeProblemName(problemName1);

		if ((prob.contains("/0") && !prob.contains("/0."))) {
			String actualMessage = ENTER_ANOTHER_PROBLEM.replace("SimStName", SimSt.getSimStName());
			quesMessage.add("false");
			quesMessage.add(actualMessage);
			return quesMessage;
		}
		
		String[] parts = prob.split("=");
		if (parts[0].equals(parts[1]) || hasMoreThanOneVariables(prob)) {
			String actualMessage = ENTER_ANOTHER_PROBLEM.replace("SimStName", SimSt.getSimStName());
			quesMessage.add("false");
			quesMessage.add(actualMessage);
			return quesMessage;
		}
		
		if (simSt.isStartStateCheckerDefined() && brController.getMissController().isSimStPleOn()
				&& simSt.isSsMetaTutorMode()) {
			String problemName = "";
			if (runType.equals("springBoot")) {
				problemName = createComponentName(inputs);
			} else {
				problemName = simSt.getSsInteractiveLearning()
						.createName(getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
			}
			
			boolean isOKProblem = simSt.getStartStateChecker()
					.checkStartState(SimSt.convertFromSafeProblemName(problemName), brController);

			// this.wait();
			if (!isOKProblem) {
				String actualMessage = ENTER_ANOTHER_PROBLEM.replace("SimStName", SimSt.getSimStName());
				quesMessage.add("false");
				quesMessage.add(actualMessage);
				return quesMessage;
			}
		}
		
		if (simSt.isSsMetaTutorMode() || simSt.isSsAplusCtrlCogTutorMode()) {
			String problemName = "";
			if (runType.equals("springBoot")) {
				problemName = createComponentName(inputs);
			} else {
				problemName = simSt.getSsInteractiveLearning()
						.createName(getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
			}
			boolean solveable = simSt.getSsInteractiveLearning().isSolvable(simSt.getProblemCheckerOracle(),
					problemName, brController);
			if (!solveable) {
				String message = "	Try giving solvable equation ";
				quesMessage.add("false");
				quesMessage.add(message);
				return quesMessage;
			}
		}
		
		if (!passed) {
			String message = "";
			if (runType.equals("springBoot")) {
				message = getSimSt().getInputChecker().invalidVariablesMessage(inputs.get(0), inputs.get(1));
			} else {
				message = getSimSt().getInputChecker().invalidVariablesMessage(cellText1, cellText1);
			}
			quesMessage.add("false");
			quesMessage.add(message);
			return quesMessage;
		}
		
		getSimSt().getModelTraceWM().setStudentEnteredProblem(prob);
		if( prob.equals(ModelTraceWorkingMemory.suggestedProblem.replaceAll("\\s+", ""))) {
			getSimSt().getModelTraceWM().setProblemType("failedQuizProblem");
		}
		quesMessage.add("true");
		return quesMessage;
	}
	
	public String createComponentName(List<String> inputs) {
		String componentName = "";
		for(int i = 0; i < inputs.size(); i++) {
			if(componentName.length() > 0) {
				componentName += "_"+ inputs.get(i);
			} else {
				componentName += inputs.get(i);
			}
		}
		return componentName;
	}

	public boolean validateQuestion1() {
		String element = null;
		String input = null;

		for (int i = 0; i < startStateElements.size(); i++) {
			element = startStateElements.get(i);
			Object widget = brController.lookupWidgetByName(element);
			if (widget != null && widget instanceof TableExpressionCell) {
				TableExpressionCell cell = (TableExpressionCell) widget;
				input = cell.getText().toLowerCase();
				if (input.length() < 1) {
					giveDialogMessage(ENTER_FULL_PROBLEM);
					return false;
				}
				if (getSimSt().isInputCheckerDefined()
						&& !getSimSt().getInputChecker().checkInput(element, input, null, this.getBrController())) {
					giveDialogMessage(getSimSt().getInputChecker().invalidInputMessage(element, input, null));
					// simSt.displayMessage("",
					// getSimSt().getInputChecker().invalidInputMessage(element, input));
					return false;
				}
			}
		}

		boolean passed = false;
		passed = checkVariableUsedStartState();
		String problemName1 = simSt.getSsInteractiveLearning()
				.createName(getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
		String prob = SimSt.convertFromSafeProblemName(problemName1);

		/**
		 * @author Vishnu
		 * @date March 29th 2017 The following 'if' condition was commented because the
		 *       constraint didn't allow student to enter problems like 8n+5=0 and
		 *       0=7x+3
		 */

		// if ((prob.contains("/0") && !prob.contains("/0."))|| (prob.startsWith("0") &&
		// !prob.startsWith("0.")) || prob.contains("=0"))
		if ((prob.contains("/0") && !prob.contains("/0."))) {
			String actualMessage = ENTER_ANOTHER_PROBLEM.replace("SimStName", SimSt.getSimStName());
			giveDialogMessage(actualMessage);
			return false;
		}

		String[] parts = prob.split("=");
		if (parts[0].equals(parts[1]) || hasMoreThanOneVariables(prob)) {
			String actualMessage = ENTER_ANOTHER_PROBLEM.replace("SimStName", SimSt.getSimStName());
			giveDialogMessage(actualMessage);
			return false;

		}

		// trace.err("Ok we gave a problem....");
		if (simSt.isStartStateCheckerDefined() && brController.getMissController().isSimStPleOn()
				&& simSt.isSsMetaTutorMode()) {
			String problemName = simSt.getSsInteractiveLearning()
					.createName(getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
			boolean isOKProblem = simSt.getStartStateChecker()
					.checkStartState(SimSt.convertFromSafeProblemName(problemName), brController);

			// this.wait();
			if (!isOKProblem) {
				String actualMessage = ENTER_ANOTHER_PROBLEM.replace("SimStName", SimSt.getSimStName());
				giveDialogMessage(actualMessage);
				return false;
			}

		}
		// System.out.println(" Before the Checker ");

		if (simSt.isSsMetaTutorMode() || simSt.isSsAplusCtrlCogTutorMode()) {
			String problemName = simSt.getSsInteractiveLearning()
					.createName(getSimStPeerTutoringPlatform().getStudentInterface().getComponents());
			boolean solveable = simSt.getSsInteractiveLearning().isSolvable(simSt.getProblemCheckerOracle(),
					problemName, brController);
			// System.out.println(" Is it solvable : "+solveable);
			if (!solveable) {
				String message = "	Try giving solvable equation ";
				giveDialogMessage(message);
				return false;
			}
		}

		if (!passed) {
			giveDialogMessage(getSimSt().getInputChecker().invalidVariablesMessage(cellText1, cellText1));
			// simSt.displayMessage("",getSimSt().getInputChecker().invalidVariablesMessage(cellText1,
			// cellText2));
			return false;
		}

		// trace.err("returning true....");
		getSimSt().getModelTraceWM().setStudentEnteredProblem(prob);
		if (prob.equals(ModelTraceWorkingMemory.suggestedProblem.replaceAll("\\s+", "")))
			getSimSt().getModelTraceWM().setProblemType("failedQuizProblem");

		return true;
	}

	boolean hasMoreThanOneVariables(String problem) {
		boolean returnValue = false;
		boolean variableSeen = false;
		char variableChar = '\0';
		char[] invalidVariables = { 'd', 'e', 'f', 'l', 'D', 'E', 'F', 'L' };

		if (problem != null) {
			for (int i = 0; i < problem.length(); i++) {
				char current = problem.charAt(i);

				if (Character.isLetter(current)) {
					for (int j = 0; j < invalidVariables.length; j++) {
						if (current == invalidVariables[j]) {
							returnValue = true;/// formatInvalidVariableUsedMessage();
						}
					}

					if (!variableSeen) {
						variableChar = current;
						variableSeen = true;
					} else {
						if (current != variableChar)
							returnValue = true;// "You can use only one letter as a variable term in the equation.";
						else if (current == problem.charAt(i - 1) && i > 0)
							returnValue = true;// "Did you forget to put an operator between " + current + " ?";
					}
				}
			}

		}
		return returnValue;
	}

	public void setUpVideoTab() {
		if (trace.getDebugCode("miss"))
			trace.out("miss", "setUpVideoTab");
		if (videoIntroductionName == null || videoIntroductionName.length() <= 0)
			return;
		JTabbedPaneWithCloseIcons exampleTabPane = getSimStPeerTutoringPlatform().getExamplePane();
		Component video = getCurriculumBrowser().getVideoPanel();
		String videoTab = "Tutorial";
		ImageIcon icon = getSimStPeerTutoringPlatform().createImageIcon("img/video.png");
		exampleTabPane.insertTab(videoTab, icon, video, "", 7);
		// exampleTabPane.addTab(videoTab, icon, video, false);
		exampleExplanations.put(videoTab, "This video should give you an idea of how to tutor a student.");
	}

	public void setUpOverviewTab() {
		if (trace.getDebugCode("miss"))
			trace.out("miss", "setUpOverviewTab");
		if (!getCurriculumBrowser().isHtmlSet())
			return;
		JTabbedPaneWithCloseIcons exampleTabPane = getSimStPeerTutoringPlatform().getExamplePane();
		Component overview = getCurriculumBrowser().getBrowserPane();
		overview.setName("Unit Overview");
		String overviewTab = "Unit Overview";
		overview.setSize(200, 200);
		// exampleTabPane.insertTab(overviewTab, null, overview, "", 2);
		exampleTabPane.addTab(overviewTab, overview, false);
		exampleExplanations.put(overviewTab,
				"This page shows you what is covered in the unit and gives some suggested problems.");
		exampleTabPane.setSelectedComponent(overview);
		if (trace.getDebugCode("miss"))
			trace.out("miss", "Exit setUpOverviewTab");
	}

	public void generateProblemBankTab() {
		JTabbedPaneWithCloseIcons exampleTabPane = getSimStPeerTutoringPlatform().getExamplePane();
		String[] columns = { "Problem", "Attempts", "Difficulty" };

		readProblemStatisticFile();
		Component bank = getSimStPeerTutoringPlatform().createProblemBank(columns, problemStatData);
		String bankTab = "Problem Bank";
		bank.setSize(400, 400);

		int tabIndex = exampleTabPane.indexOfTab(bankTab);
		if (tabIndex >= 0) {
			exampleTabPane.remove(tabIndex);
		}

		// exampleTabPane.addTab(bankTab, bank);
		exampleTabPane.addTab(bankTab, bank, true);
		exampleExplanations.put(bankTab,
				"This page lists problems similar to ones that have been used before.  The number of attempts is how many times a problem like it was tried, and the difficulty depends on how many of those tries were successful.");
	}

	public void fillSelection(Component component, String selection, String input, Color borderColor,
			boolean highlight) {
		if (component instanceof JCommTable) {
			fillInTableCell((JCommTable) component, selection, input, borderColor, highlight);
		} else if (component instanceof JCommTextField) {
			JCommTextField field = (JCommTextField) component;
			String name = "" + field.getName();
			field.setText(input);
			if (highlight)
				field.setBackground(Color.PINK);
			if (borderColor != null)
				field.setBorder(BorderFactory.createLineBorder(borderColor, 2));
		} else if (component instanceof JCommComboBox) {
			JCommComboBox dropDown = (JCommComboBox) component;
			String name = component.getName();
			dropDown.doCorrectAction(name, "UpdateComboBox", input);
			if (highlight)
				dropDown.setBackground(Color.PINK);
			if (borderColor != null)
				dropDown.setBorder(BorderFactory.createLineBorder(borderColor, 2));

		} else if (component instanceof JCommTextArea) {

			JCommTextArea area = (JCommTextArea) component;
			String name = "" + area.getName();
			area.setText(input);
			if (highlight)
				area.setBackground(Color.PINK);
			if (borderColor != null)
				area.setBorder(BorderFactory.createLineBorder(borderColor, 2));
		}

		else if (component instanceof Container) {
			Container container = (Container) component;
			for (Component comp : container.getComponents()) {
				fillSelection(comp, selection, input, borderColor, highlight);
			}
		}

	}

	public void fillInTableCell(JCommTable table, String selection, String input, Color borderColor,
			boolean highlight) {
		int rows = table.getRows();
		int columns = table.getColumns();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				TableCell cell = table.getCell(r, c);
				String selectionCell = table.getName() + "_C" + (c + 1) + "R" + (r + 1);

				if (selectionCell.equals(selection)) {
					cell.setText(input);

					if (borderColor != null) {
						Border blueBorder = BorderFactory.createLineBorder(borderColor, 2);
						cell.setBorder(blueBorder);
					}

					if (highlight)
						cell.setBackground(Color.pink);

				}

			}
		}

	}

	public void setUpTab(String tab) {
		/*
		 * if(trace.getDebugCode("miss")) trace.out("miss","setUpTab: " + tab);
		 * if(!tab.contains(getSimStName()) && !tab.contains("Overview") &&
		 * !tab.contains("Tutorial") && !tab.contains("Problem") && tab.contains(" ")) {
		 * JTabbedPane exampleTabPane = getSimStPeerTutoringPlatform().getExamplePane();
		 * JPanel examplePanel = (JPanel) exampleTabPane.getSelectedComponent();
		 * JComponent studentInterface =
		 * getSimStPeerTutoringPlatform().getStudentInterface(); int exampleNumber =
		 * Integer.parseInt(tab.substring(tab.indexOf(' ')+1));
		 * //Hashtable<String,String> example =
		 * examples.get(exampleTabPane.getSelectedIndex()-1); Hashtable<String,String>
		 * example = examples.get(exampleNumber-1);
		 * 
		 * if(examplePanel.getComponentCount() <= 0) showExample(examplePanel, example,
		 * studentInterface); } showTabText(tab);
		 */
	}

	String previousSpeechText = "";
	boolean buttonsVisible = false;

	public void showTabText(String tab) {
		if (tab.equals(getSimStName())) {
			// Main tab, switch back to what it was before
			if (previousSpeechText.isEmpty()) {
				getSimStPeerTutoringPlatform().clearSpeech();
				getSimStPeerTutoringPlatform().appendSpeech(START_MESSAGE, getSimStName());
			} else
				getSimStPeerTutoringPlatform().setFormattedSpeech(previousSpeechText);
			// getSimStPeerTutoringPlatform().showButtons(buttonsVisible);
			if (buttonsVisible)
				getSimStPeerTutoringPlatform().restoreButtons();
			previousSpeechText = "";
			buttonsVisible = true;
			getSimStPeerTutoringPlatform().setImageTeacher(false);
			getSimStPeerTutoringPlatform().showMedals(true);
			if (simSt.isSsMetaTutorMode()) {
				getSimStPeerTutoringPlatform().getMetaTutorComponent().setVisible(true);
			}
		} else {
			if (previousSpeechText.length() == 0) {
				previousSpeechText = getSimStPeerTutoringPlatform().getCurrentSpeechText();
				buttonsVisible = getSimStPeerTutoringPlatform().getButtonsShowing();
			}
			// Example, show example text
			setMessage(exampleExplanations.get(tab));
			getSimStPeerTutoringPlatform().showButtons(false);
			getSimStPeerTutoringPlatform().showMedals(false);
			getSimStPeerTutoringPlatform().setImageTeacher(true);
			// if(simSt.isSsMetaTutorMode()) {
			// getSimStPeerTutoringPlatform().getMetaTutorComponent().setVisible(false);
			// getSimSt().getModelTraceWM().setSsName(SimSt.SimStName);
			// if(tab.contains("Overview")) {
			// getSimSt().getModelTraceWM().setUnitOverviewTab(WorkingMemoryConstants.TAB_ACTION);
			// }
			// }
		}
	}

	public static Object[][] problemStatData = null;

	/**
	 * HashMap to store problem read from ProblemBank and whether the problem has
	 * been used or not while tutoring.
	 */
	public static java.util.HashMap<String, String> problemMap = null;

	private static ProblemAssessor assessor;

	/*
	 * Read the file tracking results of each different type of problem into the
	 * hashtable for reference. The file has the following format Problem, number of
	 * attempts, difficulty. The difficulty is calculated as followed: ratio of
	 * steps asked for hints / total steps per student and then average (from
	 * student table). Difficulty is normalized per type (i.e. one step - two step).
	 */
	public synchronized static void readProblemStatisticFile() {
		List<String> fileData = new LinkedList<String>();
		try {
			File file = null;
			if (!SimSt.WEBSTARTENABLED)
				file = new File(PROBLEM_STAT_FILE);
			else
				file = new File(WebStartFileDownloader.SimStWebStartDir + PROBLEM_STAT_FILE);

			if (!file.exists())
				return;
			BufferedReader f = new BufferedReader(new FileReader(file));

			String line = f.readLine();

			while (line != null && !line.equals("-1")) {
				fileData.add(line);
				line = f.readLine();
			}

			f.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		problemStatData = new Object[fileData.size()][];
		problemMap = new java.util.HashMap<String, String>();

		for (int i = 0; i < fileData.size(); i++) {
			String[] problemDetails = fileData.get(i).split(",");

			/*
			 * problemStatData[i] = new Object[5];
			 * 
			 * problemStatData[i][0] = GameShowUtilities.generate(problemDetails[0]);
			 * problemStatData[i][1] = new Integer(problemDetails[1]); problemStatData[i][2]
			 * = new Integer(problemDetails[2]);
			 */

			int pct = new Integer(problemDetails[2]);
			int starCount = pct / 20;

			if (pct < 20) {
				starCount = 1;
			} else if (pct >= 20 && pct < 40) {
				starCount = 2;
			} else if (pct >= 40 && pct < 60) {
				starCount = 3;
			} else if (pct >= 60 && pct < 80) {
				starCount = 4;
			} else
				starCount = 5;

			if (starCount <= 0)
				starCount = 1;
			if (starCount > 5)
				starCount = 5;

			String diff = "img/" + starCount + "-star.png";
			/*
			 * problemStatData[i][3] = pct+"%"; problemStatData[i][4] =
			 * GameShowUtilities.createImageIcon(diff);
			 */

			/*
			 * nbarba 01/17/2014: old implementation of problem bank, without type of
			 * problem showing problemStatData[i] = new Object[3]; problemStatData[i][0] =
			 * GameShowUtilities.generate(problemDetails[0]); problemStatData[i][1] = new
			 * Integer(problemDetails[1]); problemStatData[i][2] =
			 * GameShowUtilities.createImageIcon(diff);
			 */
			if (diff.equals("img/0-star.png"))
				diff = "img/1-star.png";

			assessor = new AlgebraProblemAssessor();

			/*
			 * problemStatData[i] = new Object[4]; problemStatData[i][0] =
			 * GameShowUtilities.generate(problemDetails[0]); problemStatData[i][1] =
			 * assessor.classifyProblem(GameShowUtilities.generate(problemDetails[0]));
			 * problemStatData[i][2] = new Integer(problemDetails[1]); problemStatData[i][3]
			 * = GameShowUtilities.createImageIcon(diff);
			 * 
			 */
			problemStatData[i] = new Object[4];
			problemStatData[i][0] = GameShowUtilities.generate(problemDetails[0]);
			problemStatData[i][1] = assessor.classifyProblem(GameShowUtilities.generate(problemDetails[0]));
			problemStatData[i][2] = GameShowUtilities.createImageIcon(diff);

			problemMap.put((String) problemStatData[i][0], WorkingMemoryConstants.PROBLEM_NOT_USED_FOR_TUTORING);
		}
	}

	public static void saveAccountFile(String accountFile) {
		// If the app. is running locally then save the accountFile in the current
		// directory
		// else save the accountFile in the Public folder and also store it on the
		// server for later retrieval
		if (trace.getDebugCode("miss"))
			trace.out("miss", "saveAccountFile: " + accountFile);
		if (!SimSt.WEBSTARTENABLED) { // running locally
			try {
				FileWriter f = new FileWriter(new File(accountFile));
//				FileWriter f = new FileWriter(new File(logDirectory, accountFile));
				f.write(SimSt.getSimStName() + "\n");
				f.write(SimStPLE.STUDENT_IMAGE + "\n");
				f.write("" + SimStPLE.currentOverallProblem);
				f.flush();
				f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // Not running locally but using webstart

			File acFile = new File(WebStartFileDownloader.SimStWebStartDir + accountFile);
			FileWriter fw;
			try {
				fw = new FileWriter(acFile);
				fw.write(SimSt.getSimStName() + "\n");
				fw.write(SimStPLE.STUDENT_IMAGE + "\n");
				fw.write("" + SimStPLE.currentOverallProblem);
				fw.flush();
				fw.close();
				StorageClient sc = new StorageClient();
				sc.storeFile(accountFile, acFile.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveAccountFileAplusCogTutor(String accountFile) {
		if (SimStPLE.passedProblemsList.contains("null"))
			return;
		// If the app. is running locally then save the accountFile in the current
		// directory
		// else save the accountFile in the Public folder and also store it on the
		// server for later retrieval
		if (trace.getDebugCode("miss"))
			trace.out("miss", "saveAccountFile: " + accountFile);
		if (!SimSt.WEBSTARTENABLED) { // running locally
			try {
				FileWriter f = new FileWriter(new File(accountFile));
				f.write(SimSt.getSimStName() + "\n");
				f.write(SimStPLE.STUDENT_IMAGE + "\n");
				f.write("" + SimStPLE.currentOverallProblem + "\n");
				f.write("," + SimStPLE.passedProblemsList);
				f.flush();
				f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // Not running locally but using webstart

			File acFile = new File(WebStartFileDownloader.SimStWebStartDir + accountFile);
			FileWriter fw;
			try {
				fw = new FileWriter(acFile);
				fw.write(SimSt.getSimStName() + "\n");
				fw.write(SimStPLE.STUDENT_IMAGE + "\n");
				fw.write("" + SimStPLE.currentOverallProblem + "\n");
				fw.write("," + SimStPLE.passedProblemsList);
				fw.flush();
				fw.close();
				StorageClient sc = new StorageClient();
				sc.storeFile(accountFile, acFile.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class YesNoListener implements ActionListener {
		YesNoBucket response;

		YesNoListener(YesNoBucket response) {
			this.response = response;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			response.put(arg0.getActionCommand());
		}

	}

	public void setTitle(JFrame activeWindow) {
		JFrame topFrame = null;
		try {
			topFrame = (JFrame) SwingUtilities.getWindowAncestor(brController.getStudentInterface().getTutorPanel());
		} catch (Exception ex) {

		}

		if (topFrame != null)
			topFrame.setTitle("SimStudent V" + SimSt.VERSION + " - " + simSt.getUserID() + getConditionIdentifier());
		else
			activeWindow
					.setTitle("SimStudent V" + SimSt.VERSION + " - " + simSt.getUserID() + getConditionIdentifier());

	}

	/**
	 * Method to update the title bar with something to differentiate conditions
	 * 
	 * @return
	 */
	public String getConditionIdentifier() {
		String returnValue = "";

		if (getSimSt().isSsAplusCtrlCogTutorMode()) {
			returnValue = "TA";
		} else if (getSimSt().isSsCogTutorMode()) {
			returnValue = "TC";
		} else if (getSimSt().isSsMetaTutorMode() && getSimSt().getSsMetaTutorModeLevel().equals(SimSt.METACOGNITIVE)) {
			returnValue = "AM";
		} else if (getSimSt().isSsMetaTutorMode() && getSimSt().getSsMetaTutorModeLevel().equals(SimSt.COGNITIVE)) {
			returnValue = "AC";
		} else if (getSimSt().isSsMetaTutorMode()) {
			returnValue = "AP";
		} else {
			returnValue = "AB";
		}

		return returnValue;
	}

	public SimStExample createQuizTaskPaneItem(String problem, int index) {
		SimStExample quizProblem = new SimStExample();
		// quizProblem.setTitle((index+1)+") " + problem);
		quizProblem.setTitle(problem);
		quizProblem.setIndex(index);
		// Thread.dumpStack();
		// JOptionPane.showMessageDialog(null, "faskelo2 " + problem);

		getSsInteractiveLearning().clearQuizGraph();
		quizProblem.addStartStateFromProblemName(problem, startStateElements);

		unsolvedProblemsQueue.add(problem);

		AplusPlatform aplus = (AplusPlatform) this.getSimStPeerTutoringPlatform();

		if (aplus.lastClickedQuizProblem == null) {
			aplus.lastClickedQuizProblem = quizProblem.getTitle();

			aplus.lastClickedQuizProblemIndex = index;

		}

		// JOptionPane.showMessageDialog(null, "faskelo2 aplus.last " +
		// aplus.lastClickedQuizProblem);

		quizProblem.setStatus(SimStExample.COGTUTOR_QUIZ_NOT_TAKEN);
		quizProblem.setSection(quizSections.get(index));
		quizProblem.setSimSt(this.getSimSt());
		return quizProblem;

	}

	/**
	 * Method that updates the QuizSection pane. This method is responsible for 1.
	 * Updating the indexes 2. Unlocking the next section 3. Unlocking the items of
	 * the next section.
	 */
	public void sectionPassedUpdateQuizSectionPane() {

		currentProblem += currentQuizSection.size();
		currentOverallProblem += currentQuizSection.size();
		currentCorrect = 0;

		currentQuizSectionNumber++;
		currentQuizSection.clear();
		currentQuizSection = new ArrayList<String>();

		for (int i = 0; i < quizProblems.size(); i++) {
			if (quizSections.get(i) == currentQuizSectionNumber) {
				/* add the next quiz section items */
				currentQuizSection.add(quizProblems.get(i));
				trace.out("ss", "Added " + quizProblems.get(i) + " to " + currentQuizSectionNumber);
				/* update the quiz task pane with the newly added quiz items */
				SimStExample quizProblem = createQuizTaskPaneItem(getQuizProblem(i), i);
				// getSimStPeerTutoringPlatform().addQuiz(quizProblem);

				((AplusPlatform) getSimStPeerTutoringPlatform()).addQuizLabelIcon(quizProblem, false);

			}
		}

		getSimStPeerTutoringPlatform().unlockQuiz(currentQuizSectionNumber);

	}
	
	private String quizAssessment;
	
	public String getQuizAssessment() {
		return this.quizAssessment;
	}
	
	public void setQuizAssessment(String qa) {
		this.quizAssessment = qa;
	}

	/**
	 * Starts the quiz in a separate thread using our own graph-model. See
	 * {@link SimStProblemGraph}.
	 */
	public class SSQuizThread implements Runnable {

		public void run() {
			// Adding the synchronized block on the whole quiz. If two threads come in only
			// the one which
			// acquires the lock goes ahead while the other waits.

			System.out.println(" This is the quiz !!! ");
			if (runType.equalsIgnoreCase("springboot")) {
				return;
			}
			synchronized (quizLock) {

				if (currentQuizSectionNumber == sections.size())
					currentQuizSectionNumber = sections.size() - 1;

				// brController.getMissController().getSimSt().getModelTraceWM().setQuizJustTaken(WorkingMemoryConstants.TRUE);
				// brController.getMissController().getSimSt().getModelTraceWM().setResourceViewed(WorkingMemoryConstants.FALSE);

				if (getSections().get(currentQuizSectionNumber).contains("Final")) {
					((AplusPlatform) getSimStPeerTutoringPlatform()).quizPanes.get(0).setCollapsed(true);
					((AplusPlatform) getSimStPeerTutoringPlatform()).quizPanes.get(1).setCollapsed(true);
					((AplusPlatform) getSimStPeerTutoringPlatform()).quizPanes.get(2).setCollapsed(true);
				}

				getSimStPeerTutoringPlatform().setQuizButtonEnabled(false);
				// getSimStPeerTutoringPlatform().getNextProblemButton().setEnabled(false);
				getSimStPeerTutoringPlatform().setNextProblemButtonEnabled(false);
				MouseListener[] mListeners = null;
				if (simSt != null && simSt.isSsMetaTutorMode()) {
					mListeners = getSimStPeerTutoringPlatform().setMetaTutorComponentEnabled(false, null);
				}

				if (currentProblem == 0)
					reloadQuizQuestions(true);

				boolean dontShowAllRA = getSimSt().dontShowAllRA();
				getSimSt().setDontShowAllRA(true);
				boolean ilSignalNegative = getSimSt().isILSignalNegative();
				getSimSt().setIlSignalNegative(false);
				boolean ilSignalPositive = getSimSt().isILSignalPositive();
				getSimSt().setIlSignalPositive(false);
				String raTestMethod = getSimSt().getRuleActivationTestMethod();

				// now from command line we can define either jess oracle or CL oracle
				getSimSt().setRuleActivationTestMethod(getSimSt().getQuizGradingMethod());
				// getSimSt().setRuleActivationTestMethod(SimSt.RA_TEST_METHOD_TUTOR_SOLVERV2);

				setAvatarQuiz();
				getSsInteractiveLearning().setTakingQuiz(true); // w

				Vector<SimStExample> results = startQuizProblems(); // Start solving the problems // w - return results
				
				getMissController().autoSaveInstructions("graded_instructions");
				for (int i = 0; i < results.size(); i++) {
					getSimStPeerTutoringPlatform().addQuiz(results.get(i));
					trace.out("ss",
							"Problem: " + results.get(i).getTitle() + " Section: " + results.get(i).getSection());
				}

				trace.out("ss", "Taking the Quiz ()()()()())()()()()()()()()()()()()()())))()()");
				trace.out("ss",
						"Current Problem: " + currentProblem + " Current Quiz Section #: " + currentQuizSectionNumber);

				// displayQuizResultsAlgebra(results);
				if (getSimStPeerTutoringPlatform().getMedalCount() < currentProblem + currentCorrect)
					getSimStPeerTutoringPlatform().augmentMedals(
							currentProblem + currentCorrect - getSimStPeerTutoringPlatform().getMedalCount(), true);

				int percent = ((currentCorrect + currentProblem) * 100 / quizProblems.size()); // w
				if (percent >= 100) {
					quizPassed = true; // w
				}

				getSimStPeerTutoringPlatform().showQuizResultFrame(true);

				AplusPlatform aplus = (AplusPlatform) getSimStPeerTutoringPlatform();

				// aplus.setQuizButtonImg("img/quiz_again.png");
				// aplus.refresh();

				String sectn = sections.get(currentQuizSectionNumber);
				if (sectn.startsWith("-"))
					sectn = sectn.substring(1);
				String quizAssessment = "I've graded " + SimSt.getSimStName() + "'s quiz on " + sectn + ".\n\n"; // w

				if (currentCorrect == currentQuizSection.size()) {

					int passedQuizSectionSize = currentQuizSection.size();

					trace.out("ss", "Updating ================================================");

					currentProblem += currentQuizSection.size();
					currentOverallProblem += currentQuizSection.size();
					currentCorrect = 0;
					/***
					 * Update in the working memory
					 */
					brController.getMissController().getSimSt().getModelTraceWM()
							.setQuizLevelPassed(currentQuizSectionNumber);
					currentQuizSectionNumber++;
					currentQuizSection.clear();
					currentQuizSection = new ArrayList<String>();
					for (int i = 0; i < quizProblems.size(); i++) {
						if (quizSections.get(i) == currentQuizSectionNumber) {
							currentQuizSection.add(quizProblems.get(i));
							trace.out("ss", "Added " + quizProblems.get(i) + " to " + currentQuizSectionNumber);
						}
					}

					getSimStPeerTutoringPlatform().unlockQuiz(currentQuizSectionNumber);

					// sectionPassedUpdateQuizSectionPane();

					if (passedQuizSectionSize == 1)
						quizAssessment += "An excellent job!  The problem was completed correctly. Move on to the next quiz. Click then [Next Quiz] button.";
					else
						quizAssessment += "An excellent job!  All of the problems were completed correctly.Move on to the next quiz. Click then [Next Quiz] button.";

					aplus.setQuizButtonImage("img/quiz_next.png");
					aplus.refreshQuizButtonImage();
				} else if (currentCorrect > 0) {
					quizAssessment += currentCorrect + " out of the " + currentQuizSection.size()
							+ " problems were completed correctly.";
					quizAssessment += "Click on " + getSimStName() + " tab to go back to tutoring";
					aplus.setQuizButtonImage("img/quiz_again.png");
					aplus.refreshQuizButtonImage();
				} else {
					quizAssessment += "None of the problems were right this time.";
					quizAssessment += "Click on the [" + getSimStName() + "] tab to go back to tutoring";
					aplus.setQuizButtonImage("img/quiz_again.png");
					aplus.refreshQuizButtonImage();

				}

				if (!quizPassed) {
					giveDialogMessage(quizAssessment); // w return this with results
				}

				getSimStPeerTutoringPlatform().setQuizButtonEnabled(true);
				// getSimStPeerTutoringPlatform().getNextProblemButton().setEnabled(true);
				getSimStPeerTutoringPlatform().setNextProblemButtonEnabled(true);
				if (simSt != null && simSt.isSsMetaTutorMode()) {
					getSimStPeerTutoringPlatform().setMetaTutorComponentEnabled(true, mListeners);
				}
				getSsInteractiveLearning().setTakingQuiz(false);

				getSimSt().setDontShowAllRA(dontShowAllRA); // Restoring the previous configuration
				getSimSt().setIlSignalNegative(ilSignalNegative);
				getSimSt().setIlSignalPositive(ilSignalPositive);
				getSimSt().setRuleActivationTestMethod(raTestMethod);

				// makes more sense to save account before displaying the assessment message.
				requestEnterNewProblem();
//				saveAccountFile("./"+getSimSt().getLogDirectory()+"/"+ getSimSt().getUserID() + ".account");
				saveAccountFile(getSimSt().getLogDirectory()+"/"+ getSimSt().getUserID() + ".account");
//				saveAccountFile(getSimSt().getUserID()+".account");

				if (quizPassed) {

					getSimStPeerTutoringPlatform().addTrophy(true);

					if (getSimSt().getSSCLQuizReqMode()) /* If APLUS is running on webstart mode */
					{

						int reply = JOptionPane.showConfirmDialog(null,
								"<html><p>Congratulations!</p><p>With your help, " + getSimStName()
										+ " was able to pass the final challenge!</p><p>I have another set of challenging quiz problems. Would you like to see if "
										+ getSimStName() + " can solve them?</p>",
								"You did it!", JOptionPane.YES_NO_OPTION);

						if (reply == JOptionPane.YES_OPTION) {

							getSimSt().archiveAndSaveFilesOnLogout();
							saveAccountFile(getSimSt().getUserID() + ".account");

							quizProblems = allQuizProblems.get(quizLevel);
							quizSections = allQuizSections.get(quizLevel);
							quizPassed = false;
							currentProblem = 0;

							// reload account

							if (getSimSt().isSsAplusCtrlCogTutorMode())
								loadAccountInfoAplusCogTutor();
							else
								loadAccountInfo();

							String newPaneTitle = newSectionTitleBase + " " + (quizLevel + 1);

							/* Add new final challenge section in quiz tab */
							((AplusPlatform) getSimStPeerTutoringPlatform()).quizProblems
									.add(new LinkedList<ExampleAction>());
							QuizPane quizTaskPane = new QuizPane(newPaneTitle, logger, brController,
									((AplusPlatform) getSimStPeerTutoringPlatform()).actionListener);
							((AplusPlatform) getSimStPeerTutoringPlatform()).quizPanes.add(quizTaskPane);
							((Container) ((AplusPlatform) getSimStPeerTutoringPlatform()).quizContainer)
									.add(quizTaskPane);
							quizTaskPane.updatePane(false, false);
							quizTaskPane.setCollapsed(true);

							/* Add a new final challenge quizmeter graph on tutoring tab */
							ClickableProgressBar pb = new ClickableProgressBar(0, currentQuizSection.size());
							JLabel levelLabel = new JLabel(newPaneTitle);
							levelLabel.setFont(AplusPlatform.S5_MED_FONT);
							pb.setStringPainted(true);
							Border emptyBorder = BorderFactory.createLineBorder(Color.black, 1);
							pb.setBorder(emptyBorder);
							pb.setValue(0);
							quizProg.add(pb);
							((AplusPlatform) getSimStPeerTutoringPlatform()).sectionMeterPanel.add(levelLabel);
							((AplusPlatform) getSimStPeerTutoringPlatform()).sectionMeterPanel
									.add(quizProg.get(currentQuizSectionNumber));

							AplusPlatform.splashBLShown = true;
							((AplusPlatform) getSimStPeerTutoringPlatform()).quizButtonQuiz.doClick();

						} else {
							JOptionPane.showMessageDialog(null,
									"<html><p>Thank you for your participation in our study!</p><p>Click OK to close SimStudent now.</p>",
									"You did it!", JOptionPane.INFORMATION_MESSAGE);
							saveAccountFile(getSimSt().getUserID() + ".account");
							System.exit(QUIZ_COMPLETED_EXIT);
						}
					} else /* If APLUS is running locally */
					{
						/*
						 * if(getQuizLevel()+1 < allQuizProblems.size()){
						 * 
						 * JOptionPane.showMessageDialog(null,
						 * "<html><p>Congratulations!</p><p>With your help, "+getSimStName()
						 * +" was able to pass the quiz</p><p>and is ready to move on to the next level.</p>"
						 * , "" + "You did it!", JOptionPane.INFORMATION_MESSAGE,
						 * getSimStPeerTutoringPlatform().createImageIcon("img/trophyIcon.png"));
						 * quizLevel++; quizProblems= allQuizProblems.get(quizLevel); quizSections =
						 * allQuizSections.get(quizLevel); quizPassed = false; currentProblem = 0; }
						 * else{
						 */

						int reply = JOptionPane.showConfirmDialog(null,
								"<html><p>Congratulations!</p><p>With your help, " + getSimStName()
										+ " was able to pass the final challenge!</p><p>I have another set of challenging quiz problems. Would you like to see if "
										+ getSimStName() + " can solve them?</p>",
								"You did it!", JOptionPane.YES_NO_OPTION);

						if (reply == JOptionPane.YES_OPTION) {
							saveAccountFile(getSimSt().getUserID() + ".account");
							quizProblems = allQuizProblems.get(quizLevel);
							quizSections = allQuizSections.get(quizLevel);
							quizPassed = false;
							currentProblem = 0;

							// reload account info.
							loadAccountInfo();
							String newPaneTitle = newSectionTitleBase + " " + (quizLevel + 1);

							/* Add new final challenge section in quiz tab */
							((AplusPlatform) getSimStPeerTutoringPlatform()).quizProblems
									.add(new LinkedList<ExampleAction>());
							QuizPane quizTaskPane = new QuizPane(newPaneTitle, logger, brController,
									((AplusPlatform) getSimStPeerTutoringPlatform()).actionListener);
							((AplusPlatform) getSimStPeerTutoringPlatform()).quizPanes.add(quizTaskPane);
							((Container) ((AplusPlatform) getSimStPeerTutoringPlatform()).quizContainer)
									.add(quizTaskPane);
							quizTaskPane.updatePane(false, false);
							quizTaskPane.setCollapsed(true);
							/* Add a new final challenge quizmeter graph on tutoring tab */

							ClickableProgressBar pb = new ClickableProgressBar(0, currentQuizSection.size());
							JLabel levelLabel = new JLabel(newPaneTitle);
							levelLabel.setFont(AplusPlatform.S5_MED_FONT);
							pb.setStringPainted(true);
							Border emptyBorder = BorderFactory.createLineBorder(Color.black, 1);
							pb.setBorder(emptyBorder);
							pb.setValue(0);
							quizProg.add(pb);
							((AplusPlatform) getSimStPeerTutoringPlatform()).sectionMeterPanel.add(levelLabel);
							((AplusPlatform) getSimStPeerTutoringPlatform()).sectionMeterPanel
									.add(quizProg.get(currentQuizSectionNumber));

							AplusPlatform.splashBLShown = true;
							((AplusPlatform) getSimStPeerTutoringPlatform()).quizButtonQuiz.doClick();

						} else {
							JOptionPane.showMessageDialog(null,
									"<html><p>Thank you for your participation in our study!</p><p>Click OK to close SimStudent now.</p>",
									"Thank you!", JOptionPane.INFORMATION_MESSAGE);

							saveAccountFile(getSimSt().getUserID() + ".account");
							System.exit(QUIZ_COMPLETED_EXIT);
						}
						// }
					}
				}

			}
		}
	
		public Vector<SimStExample> takeQuiz() {
			return takeQuiz(null);
		}
		
		public Vector<SimStExample> takeQuiz(MouseListener[] mListeners) {
			boolean dontShowAllRA = getSimSt().dontShowAllRA();
			getSimSt().setDontShowAllRA(true);
			boolean ilSignalNegative = getSimSt().isILSignalNegative();
			getSimSt().setIlSignalNegative(false);
			boolean ilSignalPositive = getSimSt().isILSignalPositive();
			getSimSt().setIlSignalPositive(false);
			String raTestMethod = getSimSt().getRuleActivationTestMethod();

			// now from command line we can define either jess oracle or CL oracle
			getSimSt().setRuleActivationTestMethod(getSimSt().getQuizGradingMethod());
			// getSimSt().setRuleActivationTestMethod(SimSt.RA_TEST_METHOD_TUTOR_SOLVERV2);

			setAvatarQuiz();				
			getSsInteractiveLearning().setTakingQuiz(true); // w

			Vector<SimStExample> results = startQuizProblems(); // Start solving the problems // w - return results

			if (!runType.equalsIgnoreCase("springboot")) {
				for (int i = 0; i < results.size(); i++) {
					getSimStPeerTutoringPlatform().addQuiz(results.get(i));
					trace.out("ss",
							"Problem: " + results.get(i).getTitle() + " Section: " + results.get(i).getSection());
				}
			}

			trace.out("ss", "Taking the Quiz ()()()()())()()()()()()()()()()()()()())))()()");
			trace.out("ss",
					"Current Problem: " + currentProblem + " Current Quiz Section #: " + currentQuizSectionNumber);

			// displayQuizResultsAlgebra(results);
			
			if (!runType.equalsIgnoreCase("springboot")) {
				if (getSimStPeerTutoringPlatform().getMedalCount() < currentProblem + currentCorrect)
					getSimStPeerTutoringPlatform().augmentMedals(
							currentProblem + currentCorrect - getSimStPeerTutoringPlatform().getMedalCount(), true);
			}
				
			int percent = ((currentCorrect + currentProblem) * 100 / quizProblems.size()); // w
			if (percent >= 100) {
				quizPassed = true; // w
			}

			if (!runType.equalsIgnoreCase("springboot")) {
				getSimStPeerTutoringPlatform().showQuizResultFrame(true);
			}

			AplusPlatform aplus = (AplusPlatform) getSimStPeerTutoringPlatform();

			// aplus.setQuizButtonImg("img/quiz_again.png");
			// aplus.refresh();

			String sectn = sections.get(currentQuizSectionNumber);
			if (sectn.startsWith("-"))
				sectn = sectn.substring(1);
			String quizAssessment = "I've graded " + SimSt.getSimStName() + "'s quiz on " + sectn + ".\n\n"; // w

			if (currentCorrect == currentQuizSection.size()) {

				int passedQuizSectionSize = currentQuizSection.size();

				trace.out("ss", "Updating ================================================");

				currentProblem += currentQuizSection.size();
				currentOverallProblem += currentQuizSection.size();
				currentCorrect = 0;
				/***
				 * Update in the working memory
				 */
				brController.getMissController().getSimSt().getModelTraceWM()
						.setQuizLevelPassed(currentQuizSectionNumber);
				currentQuizSectionNumber++;
				currentQuizSection.clear();
				currentQuizSection = new ArrayList<String>();
				for (int i = 0; i < quizProblems.size(); i++) {
					if (quizSections.get(i) == currentQuizSectionNumber) {
						currentQuizSection.add(quizProblems.get(i));
						trace.out("ss", "Added " + quizProblems.get(i) + " to " + currentQuizSectionNumber);
					}
				}

				if (!runType.equalsIgnoreCase("springboot")) {
					getSimStPeerTutoringPlatform().unlockQuiz(currentQuizSectionNumber);
				}

				// sectionPassedUpdateQuizSectionPane();

				if (passedQuizSectionSize == 1)
					quizAssessment += "An excellent job!  The problem was completed correctly. Move on to the next quiz. Click then [Next Quiz] button.";
				else
					quizAssessment += "An excellent job!  All of the problems were completed correctly.Move on to the next quiz. Click then [Next Quiz] button.";

				if (!runType.equalsIgnoreCase("springboot")) { 
					aplus.setQuizButtonImage("img/quiz_next.png");
					aplus.refreshQuizButtonImage();
				}
			} else if (currentCorrect > 0) {
				quizAssessment += currentCorrect + " out of the " + currentQuizSection.size()
						+ " problems were completed correctly.";
				quizAssessment += "Click on " + getSimStName() + " tab to go back to tutoring";
				if (!runType.equalsIgnoreCase("springboot")) {
					aplus.setQuizButtonImage("img/quiz_again.png");
					aplus.refreshQuizButtonImage();
				}
			} else {
				quizAssessment += "None of the problems were right this time.";
				quizAssessment += "Click on the [" + getSimStName() + "] tab to go back to tutoring";
				if (!runType.equalsIgnoreCase("springboot")) {
					aplus.setQuizButtonImage("img/quiz_again.png");
					aplus.refreshQuizButtonImage();
				}
			}

			if (!runType.equalsIgnoreCase("springboot")) {
				if (!quizPassed) {
					giveDialogMessage(quizAssessment); // w return this with results
				}
			}

			if (!runType.equalsIgnoreCase("springboot")) {
				getSimStPeerTutoringPlatform().setQuizButtonEnabled(true);
				// getSimStPeerTutoringPlatform().getNextProblemButton().setEnabled(true);
				getSimStPeerTutoringPlatform().setNextProblemButtonEnabled(true);
				if (simSt != null && simSt.isSsMetaTutorMode()) {
					getSimStPeerTutoringPlatform().setMetaTutorComponentEnabled(true, mListeners);
				}
			}
			getSsInteractiveLearning().setTakingQuiz(false);

			getSimSt().setDontShowAllRA(dontShowAllRA); // Restoring the previous configuration
			getSimSt().setIlSignalNegative(ilSignalNegative);
			getSimSt().setIlSignalPositive(ilSignalPositive);
			getSimSt().setRuleActivationTestMethod(raTestMethod);

			// makes more sense to save account before displaying the assessment message.
			requestEnterNewProblem();
			setQuizAssessment(quizAssessment);
			setProblemCount(currentOverallProblem);
			return results;
		}
	}

	// nbarba: Added to keep track of the progress bars on tutoring tab
	public ArrayList<ClickableProgressBar> quizProg;
	public int currentQuizSectionCorrect;

	/**
	 * Method to update the QuizAttemptHash based on the solution of the quiz
	 * problem
	 * 
	 * @param problem  the problem name
	 * @param solution a vector containing the solution to the quiz.
	 */
	private void updateQuizAttemptHash(String problem, Vector<ProblemEdge> solution) {
		LinkedHashMap shm = new LinkedHashMap();

		for (int i1 = 0; i1 < solution.size(); i1++) {
			String stepname = solution.get(i1).getSource().getName();
			String value = "" + solution.get(i1).getAction() + "$" + solution.get(i1).getInput(); // similar to the rule
																									// activation hash
																									// (genActivationTemplate),
																									// template for hash
																									// value is
																									// action$input.
			shm.put(solution.get(i1).getSelection(), value);

		}
		// must convert it to safe problem name since this is the way we know the
		// problem in interactive learning
		getQuizAttemptsHash().put(SimSt.convertToSafeProblemName(problem), shm);

	}

	Timer avatarTimer = null;
	String currentThinkingImage = null;

	/**
	 * Method that schedules a new timer to performa a task after "x". This method
	 * is used to schedule when the thinking SimStudent png will be updated (to
	 * create the animation)
	 */
	public void scheduleSimStAvatarTimer() {
		avatarTimer = new Timer();
		avatarTimer.schedule(new SimStAvatarTimerTask(this.getBrController()), 1500);
	}

	/**
	 * Method to toggle the thinking SimStudent image (to create the animation).
	 * Note: using a gif is a bad idea because gif transparency does not fully
	 * support alpha channel and creates artifacts.
	 */
	void updateSimStAvatarWhileThinking() {

		if (getStatus().equals(THINK_STATUS)) {
			if (currentThinkingImage.equals(THINK_EXPRESSION)) {
				currentThinkingImage = THINK_EXPRESSION_EX;
				getSimStPeerTutoringPlatform().setExpression(THINK_EXPRESSION_EX);
			} else {
				currentThinkingImage = THINK_EXPRESSION;
				getSimStPeerTutoringPlatform().setExpression(THINK_EXPRESSION);
			}
		}
		if (getStatus().equals(THINK_STATUS))
			scheduleSimStAvatarTimer();
	}

	/**
	 * Inner class extending the TimerTask, so we can pass the SimStudent object to
	 * the default run function (which is executed when timer interval arrives).
	 * 
	 * @author nbarba
	 *
	 */
	class SimStAvatarTimerTask extends TimerTask {
		BR_Controller brController;

		private void setBrController(BR_Controller brController) {
			this.brController = brController;
		}

		private BR_Controller getSimSt() {
			return this.brController;
		}

		public SimStAvatarTimerTask(BR_Controller brController) {
			setBrController(brController);
		}

		@Override
		public void run() {
			getBrController().getMissController().getSimStPLE().updateSimStAvatarWhileThinking();
		}

	}

	/**
	 * Solves the problems in the quiz.
	 * 
	 * @return Vector<Vector<SimStEdge>>
	 */
	public Vector<SimStExample> startQuizProblems() {

		if (simSt.isSsMetaTutorMode())
			this.getSimSt().getModelTraceWM().setQuizInProgress("true");

		/**
		 * reset the wm for consective resource review
		 */

		// this.getSimSt().getModelTraceWM().setConsecutiveResourceReview(0);
		// if(trace.getDebugCode("rr"))trace.out("rr", "####### startQuizProblems...");
		long startQuizTime = Calendar.getInstance().getTimeInMillis();
		int numCorrect = 0;

		Vector<SimStExample> solutions = new Vector<SimStExample>();

		HashMap hm = new HashMap();
		String failedQuizProblems = "";

		randomizeQuizProblems();

		giveMessage(QUIZ_MESSAGE);

		this.getQuizAttemptsHash().clear();
		if (simSt.isSsMetaTutorMode())
			this.getSimSt().getModelTraceWM().setAllQuizFailed("false");

		for (int i = 0; i < currentQuizSection.size(); i++) {

			if (!runType.equalsIgnoreCase("springboot")) {
				getSimStPeerTutoringPlatform().refresh();				
			}
			String problem = getRandomizedQuizProblem(i);
			if (!runType.equalsIgnoreCase("springboot")) {				
				getSimStPeerTutoringPlatform().refresh();
			}

			long startQuizQuestionTime = Calendar.getInstance().getTimeInMillis();

			getBrController().startNewProblem(); // To set the platform for starting a new problem.

			// problem = "3x+3x=0";
			System.out.println("Problem : " + problem);
			getSsInteractiveLearning().createStartStateQuizProblem(problem);
			logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_QUESTION_GIVEN_ACTION,
					"Quiz" + (currentQuizSectionNumber + 1) + "." + (currentProblem + i + 1), problem, "");

			if (!runType.equalsIgnoreCase("springboot")) {
				getSimStPeerTutoringPlatform().setQuizMessage("Working on quiz problem #" + (currentProblem + i + 1) + ".");				
			}

			getSsInteractiveLearning().startQuizProblem();

			/*
			 * Here is where the quiz problem is actually solved. We have two cases: 1. In
			 * case of normal APLUS, let SimStudent interactively solve the problem 2. In
			 * case of Aplus Control, get get the solution human student entered
			 */
			/*
			 * Vector<Sai> saiSolutionVector=null; if (getSimSt().isSsCogTutorMode() ){
			 * saiSolutionVector=getSsCognitiveTutor().getQuizSolution(); } else{
			 * saiSolutionVector=getSsInteractiveLearning().startQuizProblem_new(); }
			 * 
			 * //Update the quiz graph with the solution and grade it String
			 * returnValue1=getSsInteractiveLearning().gradeQuizProblemSolution(
			 * saiSolutionVector,null);
			 */

			SimStProblemGraph problemGraph = getSsInteractiveLearning().getQuizGraph();

			SimStNode startNode = problemGraph.getStartNode();

			Vector<ProblemEdge> solution = startNode.findSolutionPath();

			/*
			 * for(int e=0 ; e<solution.size(); e++){ ProblemEdge edge = solution.get(e);
			 * System.out.println("Edge : "+edge.getSelection()+" Action : "+edge.getAction(
			 * )+" Input : "+edge.getInput()+" Result : "+edge.isCorrect()); }
			 */

			// String v_solution = simSt.getProblemAssessor().determineSolution(problem,
			// startNode /*getSsInteractiveLearning().getQuizGraph().getStartNode() */);

			long questionDuration = (Calendar.getInstance().getTimeInMillis() - startQuizQuestionTime) / 1000;

			boolean result = false;// correctCompleteAnswers(problem, solution);
			if (simSt.getProblemAssessor() != null) {
				result = simSt.getProblemAssessor().isProblemComplete(problem, solution);
			}
			

			if (result) {
				numCorrect++;
			} else {
				failedQuizProblems += problem + ":";
			}
			hm.put(" " + problem + " ", Boolean.valueOf(result));

			// nbarba, added for the Quizmeter
			currentQuizSectionCorrect = numCorrect;
			if (!runType.equalsIgnoreCase("springboot") && quizProg != null && quizProg.get(currentQuizSectionNumber) != null)
				quizProg.get(currentQuizSectionNumber).setValue(currentQuizSectionCorrect);

			/* update the quizAttemptHash */
			if (!result && solution != null)
				updateQuizAttemptHash(problem, solution);

			long quizQuestionDuration = (Calendar.getInstance().getTimeInMillis() - startQuizQuestionTime) / 1000;
			logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_QUESTION_ANSWER_ACTION,
					"Quiz" + (currentQuizSectionNumber + 1) + "." + (currentProblem + i + 1),
					quizSolutionSteps(problem, solution), "", result, (int) quizQuestionDuration);

			getSsInteractiveLearning().setQuizGraph(null);

			SimStExample quizResult = new SimStExample();

			trace.out("ss", "Taking the Quiz ()()()()())()()()()()()()()()()()()()())))()()");
			trace.out("ss", "Current Problem: " + (currentProblem + i + 1) + " Current Quiz Section #: "
					+ (currentQuizSectionNumber + 1));

			quizResult.setSection(quizSections.get(currentProblem));
			quizResult.setIndex(currentProblem + i);
			// quizResult.setTitle((currentProblem+i+1)+") "+problem);
			quizResult.setTitle(problem);
			boolean incorrect = false;
			boolean finishTypein = false;
			String incorrectStep = "";

			String[] sp = problem.split(simSt.getProblemDelimiter());
			ArrayList<String> startElements = getStartStateElements();
			for (int j = 0; j < startElements.size() && j < sp.length; j++) {
				quizResult.addStep(startElements.get(j), sp[j].trim(), "", "");
			}

			boolean doneClickedTooSoon = false;
			int count = 0;
			String incorrectLHS = "";
			if (solution != null) {
				for (int j = 0; j < solution.size(); j++) {
					boolean correctness = solution.get(j).isCorrect();
					String selection = solution.get(j).getSelection();
					String input = solution.get(j).getInput();

					if (selection.equalsIgnoreCase(Rule.DONE_NAME)) {
						input = "Saying the problem is complete here";
					}

					trace.out("ss", "Selection: " + selection + " Input: " + input + " Correctness: " + correctness);

					EdgeData edge = solution.get(j).getEdgeData();
					// If already incorrect, but it was in the middle of a typein step
					if (incorrect && !finishTypein) {
						// Check to see if this current step is no longer a typein
						if (edge.getRuleNames() != null && edge.getRuleNames().size() > 0
								&& !((String) edge.getRuleNames().get(0)).contains("-typein"))
							finishTypein = true;
					}
					// Grade while all previous steps are correct, and until the current typein is
					// finished
					if (!incorrect || !finishTypein) {

						String comment = input + " is correct."; // this is the tooltip.
						count++;
						if (!correctness)
							comment = input + " is not correct";

						quizResult.addStep(selection, input, comment, "", correctness);

						// Don't need to change correctness if it is already set - false true typein is
						// still incorrect
						if (incorrect)
							continue;
						incorrect = !correctness;
						if (incorrect) {
							// just set to incorrect - if this wasn't a typein, we're not in the middle of
							// typein
							incorrectStep = input;
							if (selection.equalsIgnoreCase(Rule.DONE_NAME)) {
								doneClickedTooSoon = true;
							}

							if (edge.getRuleNames() != null && edge.getRuleNames().size() > 0
									&& !((String) edge.getRuleNames().get(0)).contains("-typein"))
								finishTypein = true;
						}
					} else {
						if(incorrectLHS.isEmpty())
							incorrectLHS = input;
						quizResult.addStep(selection, input,
								"This step wasn't graded, because a mistake was already made.", "");
					}
				}
			}
			if (incorrect) {

				// String inquiryResult =
				// simSt.inquiryRuleActivation(quizGraph.getStartNode().getName(), currentNode,
				// "", sai.getS(), sai.getA(), sai.getI(), null);
				if (doneClickedTooSoon) {
					quizResult.setExplanation(getSimStName()
							+ " thought the problem is solved here and clicked the \"Problem is Solved\" button. However it is incorrect to click the \"Problem is solved\" button here ");
				} else {
					quizResult.setExplanation(incorrectStep + " is incorrect");
				}

				/* check is solution is correct but given in more steps */
				String via_solution = simSt.getProblemAssessor().determineSolution(quizResult.getTitle(),
						startNode /* getSsInteractiveLearning().getQuizGraph().getStartNode() */);
				// JOptionPane.showMessageDialog(null, "faskelo solution is " + via_solution);
				boolean correct = simSt.getProblemAssessor().isSolution(quizResult.getTitle(), via_solution);
				
				if(correct && (solution.size() - count -1)<=2) {
					String explanation = quizResult.getExplanation();
					quizResult.setExplanation(explanation
							+ " because it requires more steps than the best solution. By the way, "+incorrectLHS+" is incorrect if you were to do "+incorrectStep);
				}
				else if (correct) {
					String explanation = quizResult.getExplanation();
					quizResult.setExplanation(explanation
							+ " because it requires you to perform "+(solution.size() - count -1)+" more steps; however, this problem can be solved in fewer steps.");

				} else {
					String explanation = quizResult.getExplanation();
					quizResult.setExplanation("The problem is not solved yet. By the way, " + explanation
							+ " because it requires more steps than the best solution.");
				}

				quizResult.setStatus(SimStExample.QUIZ_INCORRECT);
			} else if (!result) // Not correct, but does not contain any mistakes.
			{

				/*
				 * AskHint hint=this.getSimSt().askForHintQuizGradingOracle(brController,
				 * this.getSsInteractiveLearning().getQuizGraph().getNode(solution.size())); if
				 * (hint.getSelection().equals(Rule.DONE_NAME)){
				 * quizResult.setExplanation(getSimStName()
				 * +" performed all the steps correctly except saying that the problem is finished."
				 * + getSimStName() + " did not click the \"Problem is Solved\" button."); }
				 * else{ quizResult.
				 * setExplanation("The problem doesn't have any mistakes, but it wasn't finished."
				 * ); }
				 */

				ProblemAssessor assessor = new AlgebraProblemAssessor();
				String problemType = assessor.classifyProblem(problem);
				boolean isDoneMissing = false;
				if ((problemType.equals(AlgebraProblemAssessor.ONE_STEP_EQUATION) && solution.size() == 3)
						|| (problemType.equals(AlgebraProblemAssessor.TWO_STEP_EQUATION) && solution.size() == 6)
						|| (problemType.equals(AlgebraProblemAssessor.BOTH_SIDES_EQUATION) && solution.size() == 9))
					isDoneMissing = true;

				if (isDoneMissing) {
					quizResult.setExplanation(getSimStName() + " performed all the steps correctly except "
							+ getSimStName() + " did not click the \"Problem is Solved\" button. You need to teach "
							+ getSimStName() + " to click the  \"Problem is Solved\" button");
				} else {
					quizResult.setExplanation("The problem doesn't have any mistakes, but it wasn't finished.");
				}

				quizResult.setStatus(SimStExample.QUIZ_INCOMPLETE);
			} else {

				quizResult.setExplanation("This problem is correct.");
				quizResult.setStatus(SimStExample.QUIZ_CORRECT);
			}

			solutions.add(quizResult);
		}

		// trace.err("Problems not solved on quiz: " + quizAttemptsHash.toString());

		float pctCorrect = ((float) (numCorrect + currentProblem) / getQuizProblems().size());
		int quizDuration = (int) ((Calendar.getInstance().getTimeInMillis() - startQuizTime) / 1000);

		if (numCorrect == currentQuizSection.size()) {

			if (simSt != null && simSt.getSolvedQuizProblem() == null) {

				simSt.setSolvedQuizProblem(new Vector<SimStExample>());
			}

			for (SimStExample ssExample : solutions) {

				simSt.getSolvedQuizProblem().add(ssExample);
			}

		}

		// System.out.println(" isSsMetaTutorMode : "+simSt.isSsMetaTutorMode());
		if (simSt.isSsMetaTutorMode()) {

			updateWorkingMemoryWithQuizResults(numCorrect, failedQuizProblems);
		}

		logger.simStLog(SimStLogger.SIM_STUDENT_QUIZ, SimStLogger.QUIZ_COMPLETED_ACTION,
				"Quiz" + (currentQuizSectionNumber + 1), "" + pctCorrect,
				"" + numCorrect + "/" + getQuizProblems().size(), quizDuration);
		currentCorrect = numCorrect;

		if (!runType.equalsIgnoreCase("springboot"))
			getSimStPeerTutoringPlatform().setQuizProgress(pctCorrect);
		getBrController().startNewProblem(); // To set the platform for starting a new problem
		simSt.setProblemStepString(SimSt.START_STEP);

		/*
		 * if ( numCorrect==0){ String
		 * message=conversation.getMessage(SimStConversation.ALL_QUIZ_FAILED_TOPIC);
		 * String section=sections.get(quizLevel+1);
		 * message=message.replace("<section>", section.toLowerCase());
		 * giveMessage(message); }
		 */

		return solutions;
	}

	public void updateWorkingMemoryWithQuizResults(int numCorrect, String failedQuizProblems) {
		String quizResult = "";
		String input = ""; // input = pass || input = fail:3x+6=15:2x+1=5x-8

		/***
		 * In AplusControl & CogTutor Control mode, we update working memory after
		 * attempting each problem in the quiz So the number of Correct answer is either
		 * 0 or 1 there is no need to update 'AllQuizFailCount' working memory as the
		 * student cannot attend all the questions in a section at a time
		 */
		if (brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode()
				|| brController.getMissController().getSimSt().isSsCogTutorMode()) {
			if (numCorrect == 0) {
				quizResult = WorkingMemoryConstants.QUIZ_SECTION_FAILED;
				input = quizResult + ":" + failedQuizProblems;
				int count = this.getMissController().getSimSt().getModelTraceWM().getQuizFailCount();
				brController.getMissController().getSimSt().getModelTraceWM().setQuizFailCount(++count);
			} else {
				input = WorkingMemoryConstants.QUIZ_SECTION_PASSED;
				brController.getMissController().getSimSt().getModelTraceWM().setQuizFailCount(0);
			}
			brController.getAmt().handleInterfaceAction("ssquizCompleted", "implicit", input);
			this.getSimSt().getModelTraceWM().setQuizInProgress("false");
		} else {
			if (numCorrect == currentQuizSection.size()) {
				quizResult = WorkingMemoryConstants.QUIZ_SECTION_PASSED;
				input = quizResult;
				brController.getMissController().getSimSt().getModelTraceWM().setQuizFailCount(0);
				brController.getMissController().getSimSt().getModelTraceWM().setAllQuizFailCount(0);
			} else {
				/* update the fail quiz in working memory */
				int count = this.getMissController().getSimSt().getModelTraceWM().getQuizFailCount();
				brController.getMissController().getSimSt().getModelTraceWM().setQuizFailCount(++count);
				quizResult = WorkingMemoryConstants.QUIZ_SECTION_FAILED;
				input = quizResult + ":" + failedQuizProblems;
			}

			// This sends a message to the WM about the status of the quiz and failed
			// problems
			brController.getAmt().handleInterfaceAction("ssquizCompleted", "implicit", input);
			this.getSimSt().getModelTraceWM().setQuizInProgress("false");

			if (numCorrect == 0) {
				this.getSimSt().getModelTraceWM().setAllQuizFailed("true");
				int count = this.getMissController().getSimSt().getModelTraceWM().getAllQuizFailCount();
				brController.getMissController().getSimSt().getModelTraceWM().setAllQuizFailCount(++count);
			}
		}

		// System.out.println(" Faliure Count ");
		// System.out.println(" All Quiz failed :
		// "+this.getSimSt().getModelTraceWM().getAllQuizFailed());
		// System.out.println(" No of failed :
		// "+this.getMissController().getSimSt().getModelTraceWM().getAllQuizFailCount());
		// System.out.println(" Failed count so far :
		// "+this.getMissController().getSimSt().getModelTraceWM().getQuizFailCount());
	}

	public List<Integer> quizSectionProgress() {
		List<Integer> quizProgress = new ArrayList<Integer>();
		for (int i = 0; i < getSections().size(); i++) {
			if (i < getCurrentQuizSectionNumber())
				quizProgress.add(100);
			else
				quizProgress.add(0);
		}
		return quizProgress;
	}

	public SimStExample colorCodeQuizSolutionSteps(Vector<ProblemEdge> solution, String problem) {
		problem = SimSt.convertFromSafeProblemName(problem);
		String[] sp = problem.split(simSt.getProblemDelimiter());
		ArrayList<String> startElements = getStartStateElements();
		SimStExample quizResult = new SimStExample();
		quizResult.setSection(quizSections.get(currentProblem));
		quizResult.setIndex(currentProblem);
		quizResult.setTitle(problem);

		boolean incorrect = false;
		boolean finishTypein = false;
		String incorrectStep = "";

		boolean result = false;// correctCompleteAnswers(problem, solution);
		if (simSt.getProblemAssessor() != null) {
			result = simSt.getProblemAssessor().isProblemComplete(problem, solution);
		}

		for (int j = 0; j < startElements.size() && j < sp.length; j++) {

			quizResult.addStep(startElements.get(j), sp[j].trim(), "", "");
		}

		boolean doneClickedTooSoon = false;
		if (solution != null) {
			for (int j = 0; j < solution.size(); j++) {
				boolean correctness = solution.get(j).isCorrect();
				String selection = solution.get(j).getSelection();
				String input = solution.get(j).getInput();

				if (selection.equalsIgnoreCase(Rule.DONE_NAME)) {
					input = "Saying the problem is complete here";
				}

				EdgeData edge = solution.get(j).getEdgeData();
				// If already incorrect, but it was in the middle of a typein step
				if (incorrect && !finishTypein) {
					// Check to see if this current step is no longer a typein
					if (edge.getRuleNames() != null && edge.getRuleNames().size() > 0
							&& !((String) edge.getRuleNames().get(0)).contains("-typein"))
						finishTypein = true;
				}
				// Grade while all previous steps are correct, and until the current typein is
				// finished
				if (!incorrect || !finishTypein) {

					String comment = input + " is correct."; // this is the tooltip.
					if (!correctness)
						comment = input + " is not correct";

					quizResult.addStep(selection, input, comment, "", correctness);

					// Don't need to change correctness if it is already set - false true typein is
					// still incorrect
					if (incorrect)
						continue;
					incorrect = !correctness;
					if (incorrect) {
						if (selection.equalsIgnoreCase(Rule.DONE_NAME)) {
							doneClickedTooSoon = true;
						}

						// just set to incorrect - if this wasn't a typein, we're not in the middle of
						// typein
						incorrectStep = input;
						if (edge.getRuleNames() != null && edge.getRuleNames().size() > 0
								&& !((String) edge.getRuleNames().get(0)).contains("-typein"))
							finishTypein = true;
					}
				} else {
					quizResult.addStep(selection, input, "This step wasn't graded, because a mistake was already made.",
							"");
				}
			}
		}
		if (incorrect) {

			if (doneClickedTooSoon) {
				// quizResult.setExplanation("You performed all the steps correctly except
				// saying that the problem is finished. You did not click the \"Problem is
				// Solved\" button.");
				quizResult.setExplanation(
						"You thought the problem is solved here and clicked the \"Problem is Solved\" button. However it is incorrect to click the \"Problem is solved\" button here.");

			} else {
				quizResult.setExplanation(incorrectStep + " is incorrect.");
			}

			quizResult.setStatus(SimStExample.QUIZ_INCORRECT);
		} else if (!result) // Not correct, but already checked for any mistakes.
		{

			quizResult.setExplanation("The problem doesn't have any mistakes, but it wasn't finished.");
			quizResult.setStatus(SimStExample.QUIZ_INCOMPLETE);
		} else {
			quizResult.setExplanation("This problem is correct.");
			quizResult.setStatus(SimStExample.QUIZ_CORRECT);
		}

		return quizResult;
	}

	/**
	 * shutdown Perform any clean-up for properly shut-down PLE
	 */
	public void shutdown() {
		int pleDuration = (int) ((Calendar.getInstance().getTimeInMillis() - startPleTime) / 1000);
		// System.out.println( " Right place for shutting down the application ");
		brController.getMissController().getSimSt().saveMTWMState();

		logger.simStShortLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.PLE_CLOSED_ACTION, "", "", pleDuration);
	}

	public boolean isModelTracer() {
		return modelTracer;
	}

	public void setModelTracer(boolean modelTracer) {
		this.modelTracer = modelTracer;
	}

	public Map<String, String> getStartState() {
		return startState;
	}

	public void setStartState(Map<String, String> startState) {
		this.startState = startState;
	}

}
