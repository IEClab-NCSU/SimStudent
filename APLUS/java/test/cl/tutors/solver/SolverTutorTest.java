package cl.tutors.solver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import cl.common.SolverOperation;
import cl.tutors.solver.rule.SolverGoal;
import cl.utilities.Logging.Logger;
import cl.utilities.TestableTutor.InitializationException;
import cl.utilities.TestableTutor.InvalidParamException;
import cl.utilities.TestableTutor.InvalidStepException;
import cl.utilities.TestableTutor.SAI;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandler;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest.SinkToolProxy;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerSAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.BR_JGraph;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.JGraphController;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class SolverTutorTest extends TestCase {
	
	
	
	
	/*Sets MAX_TABS to 7, as 7 tests are to be run*/
	static
	{
		CTATTabManager.setMaxNumTabs(7);
	}
	
	private static List<String> list(String ... strs) {
		List<String> result = new ArrayList<String>();
		for (String s : strs) {
			if (s != null)
				result.add(s);
		}
		return result;
	}
	
	/**
	 * Common components of model-tracing tests.
	 */
	public abstract static class MTTest {
		public Vector selection = new Vector();
		public Vector action = new Vector();
		public Vector input = new Vector();
		public Vector<String> tutorAction = new Vector();
		public Vector<String> tutorInput = new Vector();
		public String actor = "";
		protected String[] skillNames;
		
		MTTest(String selection, String action, String input, String actor, String tutorAction, 
				String tutorInput, String[] skillNames) {
			this(selection, action, input, actor, tutorAction, list(tutorInput), skillNames);
		}
		
		MTTest(String selection, String action, String input, String actor, String tutorAction,
				List<String> tutorInput, String[] skillNames) {
			if (selection != null)
				this.selection.add(selection);
			if (action != null)
				this.action.add(action);
			if (input != null)
				this.input.add(input);
			if (actor != null)
				this.actor= "Student";
			if (tutorAction != null)
				this.tutorAction.add(tutorAction);
			if (tutorInput != null)
				this.tutorInput.addAll(tutorInput);
			this.skillNames = skillNames;
		}

		/**
		 * Check the messages produced by the test against the expected
		 * result data in this object.
		 * @param tests the list of tests we're currently executing
		 * @param currTest the test index, for error msgs
		 * @param tMsgs list of {@link MessageObject}s to test
		 */
		public abstract void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> tMsgs);

		public void checkAssociatedRules(MessageObject msg, int currTest, int msgIdx) {
			assertEquals(testListName+"["+currTest+"]["+msgIdx+"]: Rules msgtype wrong",
					"AssociatedRules", msg.getMessageType());
			if (skillNames != null && skillNames.length > 0) {
				Vector rules = (Vector) msg.getProperty("Rules");
				assertEquals(testListName+"["+currTest+"]: no. Rules wrong",
						 skillNames.length, rules == null ? -1 : rules.size());
				if (skillNames != null) {
					Vector skills = (Vector) msg.getProperty("Skills");
					trace.out("log", testListName+"["+currTest+"] skillNames "+Arrays.asList(skillNames)+
							"; skills "+skills);
					for (String skillName : skillNames) {
						msgIdx = 0;
						for (; msgIdx < skills.size(); ++msgIdx)
							if (((String) skills.get(msgIdx)).startsWith(skillName))
								break;
						assertTrue(testListName+"["+currTest+"]: skill "+skillName+" not found",
								msgIdx < skills.size());
					}
				}
			}
		}
	}
	
	/**
	 * Test a prompt-within-transaction message exchange. Here the prompt only creates a step in the tool.
	 */
	private static class PromptAttemptNextTest extends AttemptNextTest {
		Vector<String> promptAction = new Vector<String>();
		Vector<String> promptInput;
		String prompt;
		
		PromptAttemptNextTest(String selection, String action, String input,
				String tutorAction, List<String> tutorInput, String messageType,
				String successOrBuggyMsg, String[] skillNames,
				String promptAction, List<String> promptInput, String prompt) {
			super(selection, action, input, tutorAction, tutorInput, messageType,
					successOrBuggyMsg, skillNames);
			this.promptAction.add(promptAction);
			this.promptInput = new Vector<String>(promptInput);
			this.prompt = prompt;
		}
		public void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> msgs) {
			int i = 0;
			trace.out("log", "msgs["+currTest+"]["+i+"]: "+msgs.get(i));
			MessageObject msg = msgs.remove(i);
			assertEquals(testListName+"["+currTest+"]["+i+"]: not match IA MessageType",
					INTERFACE_ACTION, msg.getMessageType());
			assertEquals(testListName+"["+currTest+"]["+i+"]: selection", selection, msg.getProperty("selection"));
			assertEquals(testListName+"["+currTest+"]["+i+"]: promptAction", promptAction, msg.getProperty("action"));
			if (promptInput != null) {
				assertEquals(testListName+"["+currTest+"]["+i+"]: promptInput", promptInput, msg.getProperty("input"));
			}
			assertEquals(testListName+"["+currTest+"]["+i+"]: prompt", prompt, msg.getProperty("prompt"));

			super.checkResult(tests, currTest, msgs, new Integer(++i));
		}
	}
	
	/**
	 * Test a transaction-then-prompt message exchange.
	 */
	private static class AttemptPromptTest extends AttemptTest {
		Vector<String> promptAction = new Vector<String>();
		Vector<String> promptInput;
		String prompt;
		
		AttemptPromptTest(String selection, String action, String input,
				String tutorAction, List<String> tutorInput, String messageType,
				String successOrBuggyMsg, String[] skillNames,
				String promptAction, List<String> promptInput, String prompt) {
			super(selection, action, input, tutorAction, tutorInput, messageType,
					successOrBuggyMsg, skillNames);
			this.promptAction.add(promptAction);
			this.promptInput = new Vector<String>(promptInput);
			this.prompt = prompt;
		}
		public void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> msgs) {
//			super.checkResult(tests, currTest, msgs);
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
			MessageObject msg = null;
			for (int j = 0; j < msgs.size(); ++j)
				trace.out("log", "msgs["+currTest+"]["+j+"]: "+msgs.get(j));
			int i = -1;
			if (tutorInput.size() > 0) {
				msg = msgs.remove(0); ++i;
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match IA MessageType",
						INTERFACE_ACTION, msg.getMessageType());
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match tutorAction",
						tutorAction.toString(), msg.getProperty("action").toString());
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match tutorInput",
						tutorInput.toString(), msg.getProperty("input").toString());
			}			
			msg = msgs.remove(0); ++i;
			assertEquals(testListName+"["+currTest+"]["+i+"]: not match MessageType",
					messageType, msg.getMessageType());
			msg = (MessageObject) msgs.remove(0); ++i;
			checkAssociatedRules(msg, currTest, i);
//			TestCase.assertEquals(testListName+"["+currTest+"]: wrong no. of msgs", 1, msgs.size());
//			i -= msgs.size();
			msg = msgs.remove(0); ++i;
			assertEquals(testListName+"["+currTest+"]["+i+"]: not match IA MessageType",
					INTERFACE_ACTION, msg.getMessageType());
			assertEquals(testListName+"["+currTest+"]["+i+"]: selection", selection, msg.getProperty("selection"));
			assertEquals(testListName+"["+currTest+"]["+i+"]: promptAction", promptAction, msg.getProperty("action"));
			if (promptInput != null) {
				assertEquals(testListName+"["+currTest+"]["+i+"]: promptInput", promptInput, msg.getProperty("input"));
			}
			assertEquals(testListName+"["+currTest+"]["+i+"]: prompt", prompt, msg.getProperty("prompt"));
			if (successOrBuggyMsg != null) {
				String expType1 = "BuggyMessage";
				String textProperty = "BuggyMsg";
				if (messageType.equals(CORRECT)) {
					expType1 = "SuccessMessage";
					textProperty = "SuccessMsg";
				}
				msg = (MessageObject) msgs.remove(0); ++i;
				assertEquals(testListName+"["+currTest+"]["+i+"]: msgType[1] mismatch",
						expType1, msg.getMessageType());
				String text = (String) msg.getProperty(textProperty);
				assertEquals(testListName+"["+currTest+"]: text", successOrBuggyMsg, text);
			}
		}
	}
	
	/**
	 * Test a non-transaction prompt message exchange.
	 */
	private static class PromptTest extends MTTest {
		String prompt = "";
		PromptTest(String selection, String action, String tutorAction, String prompt) {
			this(selection, action, "", tutorAction, list(""), prompt);
		}
		PromptTest(String selection, String action, String input, String tutorAction, List<String> tutorInput, String prompt) {
			super(selection, action, input, null, tutorAction, tutorInput, null);
			this.prompt = prompt;
		}
		public void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> msgs) {
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertEquals(testListName+"["+currTest+"]: wrong no. of msgs", 1, msgs.size());
			MessageObject msg = null;
			for (int i = 0; i < msgs.size(); ++i)
				trace.out("log", "msgs["+currTest+"]["+i+"]: "+msgs.get(i));
			int i = 0;
			msg = msgs.remove(i);
			assertEquals(testListName+"["+currTest+"]["+i+"]: not match IA MessageType",
					INTERFACE_ACTION, msg.getMessageType());
			assertEquals(testListName+"["+currTest+"]["+i+"]: selection", selection, msg.getProperty("selection"));
			assertEquals(testListName+"["+currTest+"]["+i+"]: tutorAction", tutorAction, msg.getProperty("action"));
			if (tutorInput != null) {
				assertEquals(testListName+"["+currTest+"]["+i+"]: tutorInput", tutorInput, msg.getProperty("input"));
			}
			assertEquals(testListName+"["+currTest+"]["+i+"]: prompt", prompt, msg.getProperty("prompt"));
			
		}
	}

	/**
	 * Associate a hint request (selection) and a model trace result.
	 */
	private static class HintTest extends MTTest {
		String hintSelection = "";
		String[] hintMsgs = new String[0];

		HintTest(String selection, String hintSelection, String[] hintMsgs,
				String[] skillNames) {
			super(null, null, null, null, null, (String) null, skillNames);
			this.selection.add("help");
			this.selection.add(selection);
			this.action.add("ButtonPressed");
			this.action.add("PreviousFocus");
			this.input.add("-1");
			this.hintSelection = hintSelection.trim();
			this.hintMsgs = hintMsgs;
		}

		/**
		 * @see PseudoTutorMessageHandlerTest.MTTest#checkResult(int, Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> tMsgs) {
			int m = 0;
			MessageObject msg = (MessageObject) tMsgs.get(m);
			String messageType = msg.getMessageType();
			assertEquals(testListName+"["+currTest+"]: incorrect MessageType",
					"ShowHintsMessage", messageType);

			Vector tsv = (Vector) msg.getProperty("Selection");
			assertNotNull(testListName+"["+currTest+"]: no selection in hint message",
					tsv);
			String tSelection = (String) tsv.get(0);
			trace.out("mt", "this.hintSelection " + hintSelection +
					  " ?= tSelection " + tSelection + ";");
			String ts = (tSelection == null ? "" : tSelection.toLowerCase().trim());
			assertEquals(testListName+"["+currTest+"]["+m+
					"] hintSelections differ", hintSelection.toLowerCase(), ts);
			trace.out("solver", "this.hintMsgs.length " + this.hintMsgs.length +
					  " ?= tMsgs.size() " + tMsgs.size());
			
			Vector tHintMsgs = (Vector) msg.getProperty("HintsMessage");
			for (int i = 0; i < hintMsgs.length && i < tHintMsgs.size(); ++i) {
				String expected = hintMsgs[i].trim();
				String actual = ((String) tHintMsgs.get(i)).trim();
				trace.out("mt", "msg[" + i + "]: expected, actual:\n  " +
						  expected + "\n  " + actual + "\n");
				assertEquals(testListName+"["+currTest+
						"] hintMsg["+i+"] check failed", expected, actual);
			}
			assertEquals(testListName+"["+currTest+"] nHints unequal",
					hintMsgs.length, tHintMsgs.size());
			msg = (MessageObject) tMsgs.get(++m); ;
			checkAssociatedRules(msg, currTest, m);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("HintTest");
			sb.append("S=").append(selection);
			sb.append(",hS=").append(hintSelection);
			sb.append(",nH=").append(hintMsgs.length);
			for (int i = 0; i < hintMsgs.length; ++i) {
				String h = (hintMsgs[i].length() < 30 ?
						 hintMsgs[i] : hintMsgs[i].substring(0,30));
				sb.append("[").append(h).append("]");
			}
			return sb.toString();
		}
	}

	/**
	 * Associate a test (selection, action, input) and a model trace result.
	 */
	public static class AttemptTest extends MTTest {
		String messageType = "";
		final String successOrBuggyMsg;
		
		public AttemptTest(String selection, String action, String input,
				String tutorAction, String tutorInput, String messageType,
				String successOrBuggyMsg, String[] skillNames) {
			this(selection, action, input, tutorAction, list(tutorInput), messageType,
					successOrBuggyMsg, skillNames);
		}
		
		public AttemptTest(String selection, String action, String input,
				String tutorAction, List<String> tutorInput, String messageType,
				String successOrBuggyMsg, String[] skillNames) {
			super(selection, action, input, "Student", tutorAction, tutorInput, skillNames);
			this.messageType = messageType;
			this.successOrBuggyMsg = successOrBuggyMsg;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("AttemptTest");
			sb.append("[S=").append(selection);
			sb.append(",A=").append(action);
			sb.append(",I=").append(input);
			sb.append("]r=").append(messageType);
			if (successOrBuggyMsg != null)
				sb.append(",").append(successOrBuggyMsg);
			return sb.toString();
		}

		/**
		 * Check the messages expected to be generated by this attempt.
		 * 
		 * @see PseudoTutorMessageHandlerTest.MTTest#checkResult(int, Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> msgs) {
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
			MessageObject msg = null;
			for (int i = 0; i < msgs.size(); ++i)
				trace.out("log", "msgs["+currTest+"]["+i+"]: "+msgs.get(i));
			int i = -1;
			if (tutorInput.size() > 0) {
				msg = msgs.remove(0); ++i;
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match IA MessageType",
						INTERFACE_ACTION, msg.getMessageType());
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match tutorAction",
						tutorAction.toString(), msg.getProperty("action").toString());
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match tutorInput",
						tutorInput.toString(), msg.getProperty("input").toString());
			}			
			msg = msgs.remove(0); ++i;
			assertEquals(testListName+"["+currTest+"]["+i+"]: not match MessageType",
					messageType, msg.getMessageType());
			msg = (MessageObject) msgs.remove(0); ++i;
			checkAssociatedRules(msg, currTest, i);
			if (successOrBuggyMsg != null) {
				String expType1 = "BuggyMessage";
				String textProperty = "BuggyMsg";
				if (messageType.equals(CORRECT)) {
					expType1 = "SuccessMessage";
					textProperty = "SuccessMsg";
				}
				msg = (MessageObject) msgs.remove(0); ++i;
				assertEquals(testListName+"["+currTest+"]["+i+"]: msgType[1] mismatch",
						expType1, msg.getMessageType());
				String text = (String) msg.getProperty(textProperty);
				assertEquals(testListName+"["+currTest+"]: text", successOrBuggyMsg, text);
			}
		}
	}

	/**
	 * Associate a test (selection, action, input) and a model trace result.
	 */
	public static class AttemptNextTest extends MTTest {
		String messageType = "";
		private final String successOrBuggyMsg;
		
		public AttemptNextTest(String selection, String action, String input,
				String tutorAction, String tutorInput, String messageType,
				String successOrBuggyMsg, String[] skillNames) {
			this(selection, action, input, tutorAction, list(tutorInput), messageType,
					successOrBuggyMsg, skillNames);
		}
		
		public AttemptNextTest(String selection, String action, String input,
				String tutorAction, List<String> tutorInput, String messageType,
				String successOrBuggyMsg, String[] skillNames) {
			super(selection, action, input, "Student", tutorAction, tutorInput, skillNames);
			this.messageType = messageType;
			this.successOrBuggyMsg = successOrBuggyMsg;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("AttemptTest");
			sb.append("[S=").append(selection);
			sb.append(",A=").append(action);
			sb.append(",I=").append(input);
			sb.append("]r=").append(messageType);
			if (successOrBuggyMsg != null)
				sb.append(",").append(successOrBuggyMsg);
			return sb.toString();
		}

		/**
		 * Check the messages expected to be generated by this attempt.
		 * @param tests
		 * @param currTest
		 * @param msgs
		 * @see cl.tutors.solver.SolverTutorTest.MTTest#checkResult(cl.tutors.solver.SolverTutorTest.MTTest[], int, java.util.Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> msgs) {
			checkResult(tests, currTest, msgs, null);
		}

		/**
		 * Check the messages expected to be generated by this attempt.
		 * @param tests
		 * @param currTest
		 * @param msgs
		 * @see cl.tutors.solver.SolverTutorTest.MTTest#checkResult(cl.tutors.solver.SolverTutorTest.MTTest[], int, java.util.Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, Vector<MessageObject> msgs, Integer msgIdx) {
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
			MessageObject msg = null;
			int n = (msgIdx == null ? 0 : msgIdx.intValue());
			for (int i = 0; i < msgs.size(); ++i)
				trace.out("log", "msgs["+currTest+"]["+(i+n)+"]: "+msgs.get(i));
			int i = n;
			msg = msgs.remove(0); ++i;
			assertEquals(testListName+"["+currTest+"]["+i+"]: not match MessageType",
					messageType, msg.getMessageType());
			msg = (MessageObject) msgs.remove(0); ++i;
			checkAssociatedRules(msg, currTest, i);
			if (tutorAction != null && tutorAction.size() > 0) {
				msg = msgs.remove(0); ++i;
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match IA MessageType",
						INTERFACE_ACTION, msg.getMessageType());
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match tutorAction",
						tutorAction.toString(), msg.getProperty("action").toString());
				assertEquals(testListName+"["+currTest+"]["+i+"]: not match tutorInput",
						tutorInput.toString(), msg.getProperty("input").toString());
			}			
			if (successOrBuggyMsg != null) {
				String expType1 = "BuggyMessage";
				String textProperty = "BuggyMsg";
				if (messageType.equals(CORRECT)) {
					expType1 = "SuccessMessage";
					textProperty = "SuccessMsg";
				}
				msg = (MessageObject) msgs.remove(0); ++i;
				assertEquals(testListName+"["+currTest+"]["+i+"]: msgType[1] mismatch",
						expType1, msg.getMessageType());
				String text = (String) msg.getProperty(textProperty);
				assertEquals(testListName+"["+currTest+"]: text", successOrBuggyMsg, text);
			}
		}
	}

	private static final SAI three_y_plus_4_eq_5[] = {
			new SAI(null, "subtract", "4"),				
			new SAI(null, "clt", "3y+4-4"),
			new SAI(null, "clt", "5-4"),
			new SAI(null, "divide", "3"),
			new SAI(null, "rf", "3y/3"),
			new SAI(null, "done", "")				
	};
	
	private static List<String> testsEntered = null;
	
	/** MessageType value for interface actions. */
	private static final String INTERFACE_ACTION = "InterfaceAction";
	
	/** MessageType value for correct actions. */
	private static final String CORRECT = "CorrectAction";

	/** MessageType value for incorrect actions. */
	private static final String INCORRECT = "InCorrectAction";
	
	/** Index in XxxxTests[] of current test. */
	private int currTest = -1;
	
	/** Controller for this test. */
	private static CTAT_Launcher ctatLauncher = new CTAT_Launcher(new String[0]);		
	private static BR_Controller controller = ctatLauncher.getFocusedController();
	
	/** Message sink--substitute for UTP. */
	SinkToolProxy sink = null;

	/** Message handler to exercise. */
	private PseudoTutorMessageHandler pseudoTutorMessageHandler;

	private BR_JGraph br_jgraph;
	private JGraphController jcontroller;
	private ExampleTracerGraph exampleTracerGraph;

	private SolverGoal goal;

	/** Current test we're running. */
	private static String testListName;
	
	/**
	 * Suite to run some or all tests in this class.  Uses
	 * {@link #testsEntered} to choose the test(s) to run; if empty,
	 * runs all tests.
	 */
	public static Test suite() {
		return new TestSuite(SolverTutorTest.class);
	}
	
	/**
	 * Print a help message.
	 * @param s optional diagnostic preamble.
	 */
	private static void usage(String s) {
		if (s != null && s.length() > 0)
			System.out.printf("%s. ", s);
		trace.out("Usage:\n"+
				"  "+SolverTutorTest.class.getName()+" [-h][-o] \"equation\"\n"+
				"where--\n"+
				"  -h   prints this help message;\n"+
				"  -o   prints all opcodes that the SolverTutor will accept;\n"+
				"  \"equation,variable,instruction\" ...  is (are) equations to solve,\n"+
				"       each in the form \"3y-7=17,y,solve for y\".\n");
	}

	/**
	 * @param args See {@link #usage(String)}.
	 */
	public static void main(String[] args) {
		int i = 0;
		for (; i < args.length && '-' == args[i].charAt(0); ++i) {
			char option = Character.toLowerCase(args[i].charAt(1));
			switch(option) {
			case 'h':
				usage("Help");
				return;
			case 'o':
				SolverOperation[] so = SolverOperation.getAllOperations();
				for (int j = 1; j <= so.length; j++)
					System.out.printf("%2d. %s\n", j, so[j-1].toString());
				return;
			default:
				break;  //fall through to use '-' as minus sign 
			}
		}
		testsEntered = Arrays.asList(args);
				
//		Logger.addLoggerProperty("solverdebug", "true");  // force update from System.properties
//		Logger.addLoggerProperty("LOGEVERYTHING", "true");  // force update from System.properties
		
		SolverTutorTest stt = new SolverTutorTest();
		if (testsEntered.size()-i < 1) {
			stt.solve(new String[] {"3x+4=5", "x", "solve for x"}, null);
			trace.out();
			stt.solve(new String[] {"3y+4=5", "y", "solve for y"}, three_y_plus_4_eq_5);
			return;
		}
		for (String oneTest = args[i]; i < args.length; oneTest = args[++i]) {
			String[] spec = oneTest.split(",");
			if (spec.length < 3)
				System.err.printf("Bad argument[%d] \"%s\"-- need 2 commas: \"eq,var,instr\"\n", i, oneTest);
			else
				stt.solve(spec, null);
			trace.out();         // blank line between tests
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		goal = null;
		sink = new SinkToolProxy(controller); 
        controller.setUniversalToolProxy(sink);
        controller.getProblemModel().setUseCommWidgetFlag(false);
        pseudoTutorMessageHandler = controller.getPseudoTutorMessageHandler();
        exampleTracerGraph = controller.getExampleTracerGraph();
	}

	/**
	 * Remove the members established by {@link #setUp()}.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		pseudoTutorMessageHandler = null;
		sink = null;
	}

	/**
	 * Run a list of MTTests.
	 * @param  tests array of MTTests to run
	 * @param  listName name of test list for log output
	 */
	public void runMTTests(MTTest[] tests, String listName) {
		testListName = listName;
		if (trace.getDebugCode("table")) {
			System.out.printf("<br><b>Test: %s</b>\n<table border=\"1\" cellpadding=\"2\">\n", listName);
			trace.out("<tr><th>From</th><th>MessageType</th><th>Selection</th><th>Action</th><th>Input</th><th>Prompt</th><th>BuggyMsg or SuccessMsg</th></tr>");
		}
		final String blankRowFmt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>\n";
		final String rowFmt = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n";
		for (currTest = 0; currTest < tests.length; currTest++) {
			MTTest t = tests[currTest];
			long startTime = (new Date()).getTime();
			Vector<MessageObject> msgs =
				runProcessPseudoTutorInterfaceAction(t.selection, t.action, t.input, t.actor);
			long finishTime = (new Date()).getTime();
			trace.outNT("time", "runMTTests(InterfaceAction, "+t.selection+", "+t.action
					+", "+t.input+") => "+msgs.size()+" msgs in "+(finishTime-startTime)+" ms");
			if (trace.getDebugCode("table")) {
				Object p;
				System.out.printf(blankRowFmt);
				System.out.printf(rowFmt, "Student", "InterfaceAction",
						t.selection == null ? "" : t.selection.toString(),
						t.action == null ? "" : t.action.toString(),
						t.input == null ? "" : t.input.toString(), "", "");
				for (MessageObject msg : msgs)
					System.out.printf(rowFmt, "Tutor", msg.getMessageType(),
							((p = msg.getProperty("selection")) == null ? "" : p.toString()),
							((p = msg.getProperty("action")) == null ? "" : p.toString()),
							((p = msg.getProperty("input")) == null ? "" : p.toString()),
							((p = msg.getProperty("prompt")) == null ? "" : p.toString()),
							((p = msg.getProperty("BuggyMsg")) == null && (p = msg.getProperty("SuccessMsg")) == null ? "" : p.toString()));
			}
			t.checkResult(tests, currTest, msgs);
		}
		if (trace.getDebugCode("table"))
			System.out.printf("</table>\n");
	}
	
	/** Student steps for {@link #testTypein()}. */
	private static MTTest[] Link1TypeinTests = {
		new HintTest("solver1", "solver1",
				new String[] { "What can you do to both sides of the equation to eliminate the constant value from the left side?",
				"<expression>-6</expression> is the constant value on the left side.  What can you do to both sides of the equation to eliminate the <expression>-6</expression> (make it zero)?",
		        "You can add <expression>6</expression> to both sides of the equation to eliminate the constant value of <expression>-6</expression> (<expression>-6</expression> + <expression>6</expression> = 0)." },
		        new String[] {"Remove_constant", "ax+b=c,_negative"}),
		new PromptAttemptNextTest("solver1", "Solver_requestaddorsubtractterms", "-5y-6=9",
				"nextEquation", list("-5y-6 = 9"), INCORRECT, null,
				new String[] {"Remove_constant", "ax+b=c,_negative"},
				"promptLabel", list("-5y-6=9"), "Add/subtract terms in -5y-6=9?"),
		new PromptTest("solver1", "Solver_requestaddtobothsides", "promptOperand",
				"Add "+SolverTutor.INPUT_BOX+" to both sides."),
		/* [3] next */
		new AttemptNextTest("solver1", "Solver_addtobothsides", "-6",
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), INCORRECT,
				"To remove <expression>-6</expression> from the left side, you can add a positive to it."
				+" Erase your last step and add <expression>6</expression> to both sides.",
				new String[] {"Remove_constant", "ax+b=c,_negative"}),
		new AttemptNextTest("solver1",	"Solver_left", "-5y-12", null, list(), CORRECT, null,
				null),
		new AttemptNextTest("solver1",	"Solver_right", "9-6", "nextEquation", list("-5y-12 = 9-6"), CORRECT, null,
				null),
		/* [6] next */
		new AttemptPromptTest("solver1", "Solver_requestaddorsubtractterms", "-5y-12 = 9-6",
				"promptLabel", list("-5y-12 = 9-6"), CORRECT, null,
				new String[] {"Select_Combine_Terms"},
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), "Enter result [left] = [right]"),  // Add/Subtract like terms -6+6
		new AttemptNextTest("solver1", "Solver_right", "3",
				"nextEquation", list("-5y-12 = 3"), CORRECT, null,
				new String[] {"Do_Combine_Terms_-_Whole"}),
		new PromptTest("solver1", "Solver_requestaddtobothsides", "promptOperand",
				"Add "+SolverTutor.INPUT_BOX+" to both sides."),
		/* [9] next */
		new AttemptPromptTest("solver1", "Solver_addtobothsides", "12",
				null, (List) null, CORRECT, null,
				new String[] {"Remove_constant", "ax+b=c,_negative"},
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), "Enter result [left] = [right]"),
		new AttemptNextTest("solver1",	"Solver_right", "3-12", 
				null, (List) null, INCORRECT,
				"<expression>3-12</expression> is equal to <expression>3</expression> minus <expression>12</expression>.  You need to calculate <expression>3</expression> plus <expression>12</expression>.",
				null),
		new AttemptNextTest("solver1",	"Solver_right", "3+12",
				null, (List) null, CORRECT, null,
				new String[] {"Add/Subtract"}),
		/* [12] next */
		new AttemptNextTest("solver1",	"Solver_left", "-5y",
				"nextEquation", list("-5y = 3+12"), CORRECT, null,
				new String[] {"Add/Subtract"}),
		new PromptTest("solver1", "Solver_requestdividebothsides", "promptOperand",
				"Divide both sides by "+SolverTutor.INPUT_BOX+"."),
		new AttemptPromptTest("solver1",	"Solver_dividebothsides", "5", 
				null, (List) null, INCORRECT,
				"In this equation, y is multiplied by <expression>-5</expression>."
				+" Dividing by <expression>5</expression> leaves -y, so you still need to remove the negative sign."
				+" It is better to divide by <expression>-5</expression>, since that would leave y.",
				new String[] {"Select_Combine_Terms"},
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), "Enter result [left] = [right]"),
		/* [15] next */
		new AttemptNextTest("solver1",	"Solver_right", "3",
				null, (List) null, INCORRECT,
				"Just divide <expression>3+12</expression> by <expression>5</expression>."
				+"  You can simplify in the next step.",
				null),  // FIXME no skills?
		new AttemptNextTest("solver1",	"Solver_left", "-5y/5",
				null, (List) null, CORRECT, null,
				null),  // FIXME no skills?
		new AttemptNextTest("solver1",	"Solver_right", "(3+12)/5",
				"nextEquation", list("-5y/5 = (3+12)/5"), CORRECT, null,
				null),  // FIXME no skills?
		/* [18] next */
		new AttemptPromptTest("solver1",	"Solver_requestsimplifyfractions", "-5y/5 = (3+12)/5",
				"promptLabel", list("-5y/5 = (3+12)/5"), CORRECT, null,
				null,
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), "Enter result [left] = [right]"),
		new AttemptNextTest("solver1",	"Solver_left", "-y",
				"nextEquation", list("-y = (3+12)/5"), CORRECT, null,
				null),
		new AttemptTest("solver1", "Solver_finished", "undefined",
				null, (List) null, INCORRECT, "You have not finished yet.",
				null),
		/* [21] next */
		new PromptTest("solver1", "Solver_requestmultiplybothsides", "promptOperand",
				"Multiply both sides by "+SolverTutor.INPUT_BOX+"."),
		new AttemptPromptTest("solver1",	"Solver_multiplybothsides", "-1",
				null, (List) null, CORRECT, null,
				new String[] {"Make_variable_positive", "Remove_coefficient", "Remove_negative_coefficient"},
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), "Enter result [left] = [right]"),
		new AttemptNextTest("solver1",	"Solver_left", "-y*(-1)",
				null, (List) null, CORRECT, null,
				null),  // FIXME no skills?
		/* [24] next */
		new AttemptNextTest("solver1",	"Solver_right", "-(3+12)/5",
				"nextEquation", list("-y*(-1) = -(3+12)/5"), CORRECT, null,
				null),  // FIXME no skills?
		new AttemptPromptTest("solver1", "Solver_requestaddorsubtractterms", "-y*(-1) = -(3+12)/5",
				"promptLabel", list("-y*(-1) = -(3+12)/5"), CORRECT, null,
				new String[] {"Select_Eliminate_Parens"},
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), "Enter result [left] = [right]"),
/*!!*/ //		"promptTypein", list("-y*(-1)", SolverTutor.INPUT_BOX), "Enter right-hand side"),  // Add/Subtract like terms -6+6
		new AttemptNextTest("solver1", "Solver_right", "-15/5",
				"nextEquation", list("-y*(-1) = -15/5"), CORRECT, null,
				new String[] {"Do_Eliminate_Parens_-_whole"}),
		/* [27] next */
		new AttemptPromptTest("solver1", "Solver_requestsimplifysigns", "-y*(-1) = -15/5",
				"promptLabel", list("-y*(-1) = -15/5"), INCORRECT, 
				"Simplify the fraction <expression>-15/5</expression>.",
				null,
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), "Enter result [left] = [right]"),
/*!!*/ //				"promptTypein", list(SolverTutor.INPUT_BOX, "-15/5"), "Enter left-hand side"),
		new AttemptNextTest("solver1", "Solver_left", "y", null, (List) null, CORRECT, null, null),
		new AttemptNextTest("solver1", "Solver_right", "-3",
				"nextEquation", list("y = -3"), CORRECT, null,
				null),  // FIXME no skills?
		new AttemptTest("done", "ButtonPressed", "-1", null, (List) null, CORRECT, null, null)
	};
	
	/**
	 * Load <tt>test/solver.brd</tt> and try a step
	 */
	public void testTypein(){
		testSolverLink1("test/solverTypein.brd", Link1TypeinTests, "Link1TypeinTests");
	}
	
	/** Student steps for {@link #testTypein()}. */
	private static MTTest[] Link1TypeinCorrectTests = {
		new HintTest("solver1", "solver1",
				new String[] { "What can you do to both sides of the equation to eliminate the constant value from the left side?",
				"<expression>-6</expression> is the constant value on the left side.  What can you do to both sides of the equation to eliminate the <expression>-6</expression> (make it zero)?",
		        "You can add <expression>6</expression> to both sides of the equation to eliminate the constant value of <expression>-6</expression> (<expression>-6</expression> + <expression>6</expression> = 0)." },
		        new String[] {"Remove_constant", "ax+b=c,_negative"}),
		new PromptTest("solver1", "Solver_requestaddtobothsides", "promptOperand",
				"Add "+SolverTutor.INPUT_BOX+" to both sides."),
		new AttemptNextTest("solver1", "Solver_addtobothsides", "6",
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX), CORRECT, null,
				new String[] {"Remove_constant", "ax+b=c,_negative"}),
		/* [3] next */
		new AttemptNextTest("solver1",	"Solver_left", "-5y-6+6", null, list(), CORRECT, null,
				null),
		new AttemptNextTest("solver1",	"Solver_right", "9+6", "nextEquation", list("-5y-6+6 = 9+6"), CORRECT, null,
				null),
		new AttemptPromptTest("solver1", "Solver_requestaddorsubtractterms", "-5y-6+6 = 9+6",
				"promptLabel", list("-5y-6+6 = 9+6"), CORRECT, null,
				new String[] {"Select_Combine_Terms"},
				"promptTypein", list(SolverTutor.INPUT_BOX, SolverTutor.INPUT_BOX),  "Enter result [left] = [right]"),
		/* [6] next */
		new AttemptNextTest("solver1",	"Solver_left", "-5y", null, list(), CORRECT, null,
				null),
		new AttemptNextTest("solver1",	"Solver_right", "15", "nextEquation", list("-5y = 15"), CORRECT, null,
				null),
	};	

	/**
	 * Load <tt>test/solver.brd</tt> and try a step
	 */
	public void testTypeinCorrect(){
		testSolverLink1("test/solverTypein.brd", Link1TypeinCorrectTests, "Link1TypeinCorrectTests");
	}
	
	/** Student steps for {@link #testNotAutoSimplify()}. */
	private static MTTest[] Xeq4over2Tests = {
		new HintTest("solver", "solver",
				new String[] { "Put the equation in its simplest form.",
				"Simplify fractions on the right side." },
		        null),
		new PromptAttemptNextTest("solver", "Solver_requestsimplifyfractions", "x = 4/2",
				"nextEquation", list("x = 2"), CORRECT, null,
				null,
				"promptLabel", list("4/2"), "Simplify 4/2?"),
		new AttemptTest("done", "ButtonPressed", "-1", null, (List) null, CORRECT, null, null)
	};

	
	/**
	 * Load <tt>test/.brd</tt> and try a step
	 */
	public void testXeq4over2(){
//		goal = (SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION);
		testSolverLink1("test/x_eq_4over2.brd", Xeq4over2Tests, "Xeq4over2Tests");
	}
	
	/** Student steps for {@link #testNotAutoSimplify()}. */
	private static MTTest[] Link1NotAutoSimplifyTests = {
		new HintTest("solver1", "solver1",
				new String[] { "What can you do to both sides of the equation to eliminate the constant value from the left side?",
				"<expression>-6</expression> is the constant value on the left side.  What can you do to both sides of the equation to eliminate the <expression>-6</expression> (make it zero)?",
		        "You can add <expression>6</expression> to both sides of the equation to eliminate the constant value of <expression>-6</expression> (<expression>-6</expression> + <expression>6</expression> = 0)." },
		        new String[] {"Remove_constant", "ax+b=c,_negative"}),
		new PromptTest("solver1", "Solver_requestaddtobothsides", "promptOperand", "Add "+SolverTutor.INPUT_BOX+" to both sides."),
		new AttemptNextTest("solver1", "Solver_addtobothsides", "6", "nextEquation", "-5y-6+6 = 9+6", CORRECT, null,
				new String[] {"Remove_constant", "ax+b=c,_negative"}),
		/* [3] next */
		new PromptTest("solver1", "Solver_requestaddorsubtractterms", "-5y-6+6 = 9+6", "chooseSubexpression",
				list("-5y-6+6", "9+6"), "Add/subtract terms in which subexpression?"),
		new AttemptNextTest("solver1",	"Solver_addorsubtractterms", "9+6", "nextEquation", "-5y-6+6 = 15", CORRECT, null,
				new String[] {"Select_Combine_Terms"}),
		new PromptAttemptNextTest("solver1", "Solver_requestaddorsubtractterms", "-5y-6+6 = 15", "nextEquation",
				list("-5y = 15"), CORRECT, null,
				new String[] {"Select_Combine_Terms"},
				"promptLabel", list("-5y-6+6"),
				"Add/subtract terms in -5y-6+6?"),  // Add/Subtract like terms -6+6
		/* [6] next */
		new PromptTest("solver1", "Solver_requestdividebothsides", "promptOperand", "Divide both sides by "+SolverTutor.INPUT_BOX+"."),
		new AttemptNextTest("solver1",	"Solver_dividebothsides", "5", "nextEquation", "-5y/5 = 15/5", INCORRECT,
				"In this equation, y is multiplied by <expression>-5</expression>."
				+" Dividing by <expression>5</expression> leaves -y, so you still need to remove the negative sign."
				+" It is better to divide by <expression>-5</expression>, since that would leave y.",
				new String[] {"Remove_coefficient", "Remove_negative_coefficient"}),
		new PromptTest("solver1", "Solver_simplifyfractions", "-5y/5 = 15/5", "chooseSubexpression",
				list("-5y/5", "15/5"), "Simplify which fraction?"),
		/* [9] next */
		new AttemptNextTest("solver1",	"Solver_simplifyfractions", "-5y/5", "nextEquation", "-y = 15/5", CORRECT, null,
				null),
		new AttemptNextTest("solver1",	"Solver_simplifyfractions", "15/5", "nextEquation", "-y = 3", CORRECT, null,
				null),
		new AttemptTest("solver1", "Solver_finished", "undefined", null, (String) null, INCORRECT, "You have not finished yet.", null),
		/* [12] next */
		new PromptTest("solver1", "Solver_requestmultiplybothsides", "promptOperand", "Multiply both sides by "+SolverTutor.INPUT_BOX+"."),
		new AttemptNextTest("solver1",	"Solver_multiplybothsides", "-1", "nextEquation", "-y*(-1) = 3*(-1)", CORRECT, null,
				new String[] {"Make_variable_positive", "Remove_coefficient", "Remove_negative_coefficient"}),
		new PromptTest("solver1",	"Solver_performmultiplication", "-y*(-1) = 3*(-1)", "chooseSubexpression",
				list("-y*(-1)", "3*(-1)"), "Perform multiplication in which subexpression?"),
		/* [15] next */
		new AttemptNextTest("solver1",	"Solver_performmultiplication", "-y*(-1)", "nextEquation", "y = 3*(-1)", CORRECT, null,
				null),
		new AttemptNextTest("solver1",	"Solver_performmultiplication", "3*(-1)", "nextEquation", "y = -3", CORRECT, null,
				null),
		new AttemptTest("solver1", "Solver_finished", "undefined", null, (String) null, CORRECT,
				"Completed: Unique Solution", null),
		new AttemptTest("done", "ButtonPressed", "-1", null, (List) null, CORRECT, null, null)
	};
	
	/**
	 * Load <tt>test/solver.brd</tt> and try a step
	 */
	public void testNotAutoSimplify(){
		testSolverLink1("test/solverNotAutoSimplify.brd", Link1NotAutoSimplifyTests, "Link1NotAutoSimplifyTests");
	}
	
	/** Student steps for {@link #testAutoSimplify()}. */
	private static MTTest[] Link1AutoSimplifyTests = {
		new AttemptNextTest("solver1",	"Solver_distribute", "-5y-6", "nextEquation", "-5y-6 = 9", INCORRECT, null,
				new String[] {"Remove_constant", "ax+b=c,_negative"}),
		new PromptTest("solver1", "Solver_requestsubtractfrombothsides", "promptOperand", "Subtract "+SolverTutor.INPUT_BOX+" from both sides."),
		new AttemptNextTest("solver1",	"Solver_subtractfrombothsides", "12", "nextEquation", "-5y-18 = -3", INCORRECT, null,
				new String[] {"Remove_constant", "ax+b=c,_negative"}),
		/* [3] next */
		new AttemptNextTest("solver1",	"Solver_restart", "-5y-6 = 9", null, (List) null, CORRECT, null,
				null),
		new HintTest("solver1", "solver1",
				new String[] { "What can you do to both sides of the equation to eliminate the constant value from the left side?",
				"<expression>-6</expression> is the constant value on the left side.  What can you do to both sides of the equation to eliminate the <expression>-6</expression> (make it zero)?",
		        "You can add <expression>6</expression> to both sides of the equation to eliminate the constant value of <expression>-6</expression> (<expression>-6</expression> + <expression>6</expression> = 0)." },
		        new String[] {"Remove_constant", "ax+b=c,_negative"}),
		new PromptTest("solver1", "Solver_requestaddtobothsides", "promptOperand", "Add "+SolverTutor.INPUT_BOX+" to both sides."),
		/* [6] next */
		new AttemptNextTest("solver1",	"Solver_addtobothsides", "6", "nextEquation", "-5y = 15", CORRECT, null,
				new String[] {"Remove_constant", "ax+b=c,_negative"}),
		new AttemptNextTest("solver1",	"Solver_addorsubtractterms", "-5y", "nextEquation", "-5y = 15", INCORRECT, null,
				new String[] {"Remove_coefficient", "Remove_negative_coefficient"}),
		new AttemptNextTest("solver1",	"Solver_dividebothsides", "5", "nextEquation", "-y = 3", INCORRECT,
				"In this equation, y is multiplied by <expression>-5</expression>."
				+" Dividing by <expression>5</expression> leaves -y, so you still need to remove the negative sign."
				+" It is better to divide by <expression>-5</expression>, since that would leave y.",
				new String[] {"Remove_coefficient", "Remove_negative_coefficient"}),
		/* [9] next */
		new AttemptTest("solver1", "Solver_finished", "undefined", null, (String) null, INCORRECT, "You have not finished yet.", null),
		new AttemptNextTest("solver1",	"Solver_multiply", "-1", "nextEquation", "y = -3", CORRECT, null,
				new String[] {"Make_variable_positive", "Remove_coefficient", "Remove_negative_coefficient"}),
//		new AttemptTest("solver1", "Solver_finished", "undefined", null, (String) null, CORRECT, "Completed: Unique Solution", null),
		new AttemptTest("done", "ButtonPressed", "-1", null, (String) null, CORRECT, null, new String[] {"done Done"})
	};
	
	/**
	 * Load <tt>test/solver.brd</tt> and try a step
	 */
	public void testAutoSimplify(){
		testSolverLink1("test/solver.brd", Link1AutoSimplifyTests, "Link1AutoSimplifyTests");
	}
	
	/**
	 * Load <tt>test/solver.brd</tt> and try a step
	 */
	private void testSolverLink1(String problemFileLocation, MTTest[] script, String scriptName){
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
		ExampleTracerLink link = controller.getProblemModel().getExampleTracerGraph().getLink(1);
		EdgeData edgeData = link.getEdge();
		Matcher m = edgeData.getMatcher();
		assertTrue("Link 1 not have SolverMatcher", m instanceof SolverMatcher);
		if (goal != null)
			((SolverMatcher) m).getSolverTutor().setGoal(goal);
		sink.getLatestMsgs();  // flush

        runMTTests(script, scriptName);
	}
	
	private static MTTest[] Link1ExprTypeinTests = {
		/** Student steps for {@link #testExprTypein()}. */
		new HintTest("solver", "solver",
				new String[] { "Remove the parentheses from the expression.",
				"In the expression <expression>2x-[3-6x]+7</expression>, <expression>(3-6x)</expression> is preceded by <expression>-1</expression>.",
		        "Distribute <expression>-1</expression> over <expression>(3-6x)</expression>." },
		        null),   // verified that distribute not a traceable rule, hence no skill from solver
		new AttemptPromptTest("solver", "Solver_requestdistribute", "2x-(3-6x)+7",
				"promptLabel", list("2x-(3-6x)+7"), CORRECT, null,
				null,    // verified that distribute not a traceable rule, hence no skill from solver
				"promptExpression", list(SolverTutor.INPUT_BOX), "Enter simplified expression"),  // Add/Subtract like terms -6+6
		new AttemptNextTest("solver", "Solver_expr", "2x-3+6x+7",
				"nextExpression", list("2x-3+6x+7"), CORRECT, null,
				null),
		// [3] next
		new AttemptTest("done", "ButtonPressed", "-1", null, (String) null, INCORRECT,
				"I'm sorry, but you are not done yet. Please continue working.", null),
		new AttemptPromptTest("solver", "Solver_requestaddorsubtractterms", "2x-3+6x+7",
				"promptLabel", list("2x-3+6x+7"), CORRECT, null,
				new String[] {"Consolidate_vars_with_coeff Solver", "Select_Combine_Terms Solver"},
				"promptExpression", list(SolverTutor.INPUT_BOX), "Enter simplified expression"),  // Add/Subtract like terms -6+6
		new AttemptNextTest("solver", "Solver_expr", "8x-3+7",
				"nextExpression", list("8x-3+7"), CORRECT, null, null),
		// [6] next
		new AttemptPromptTest("solver", "Solver_requestsubtractfrombothsides", "8x-3+7",
				"promptLabel", list("8x-3+7"), INCORRECT,
				"Transformation actions are not applicable when no equation is present.",
				new String[] {"Consolidate_vars_with_coeff Solver", "Select_Combine_Terms Solver"},
				"nextExpression", list("8x-3+7"), "8x-3+7"),
		new AttemptPromptTest("solver", "Solver_requestaddorsubtractterms", "8x-3+7",
				"promptLabel", list("8x-3+7"), CORRECT, null,
				new String[] {"Consolidate_vars_with_coeff Solver", "Select_Combine_Terms Solver"},
				"promptExpression", list(SolverTutor.INPUT_BOX), "Enter simplified expression"),  // Add/Subtract like terms -6+6
		new AttemptNextTest("solver", "Solver_expr", "8x-4",
				null, (List) null, INCORRECT, null, null),
		// [9] next
		new AttemptNextTest("solver", "Solver_expr", "8x+4",
				"nextExpression", list("8x+4"), CORRECT, null, null),
		new HintTest("solver", "done",
				new String[] { "Please click on the highlighted button." },
				null),   // verified that distribute not a traceable rule, hence no skill from solver
//		new AttemptNextTest("solver", "Solver_done", "8x+4", null, (List) null, CORRECT, null, null),
		new AttemptTest("done", "ButtonPressed", "-1", null, (String) null, CORRECT, null, null)
	};
	
	/**
	 * Load <tt>test/2x-(3-6x)+7.brd</tt> and try a step
	 */
	public void testExprTypein(){
		testSolverLink1("test/2x-P3-6xP+7.brd", Link1ExprTypeinTests, "Link1ExprTypeTests");
	}
	
	public void testThree_y_plus_4_eq_5() {
		solve(new String[] {"3y+4=5", "y", "solve for y"}, three_y_plus_4_eq_5);
	}

	private void solve(String[] problemSpec, SAI[] steps) {
		SolverTutor tt = new SolverTutor();
		try {
			tt.startProblem(problemSpec);  //dummy call to startproblem so that our first setparameters won't barf
		}
		catch(InitializationException ie){
            Logger.log("testsolver",ie);
		}
		cl.tutors.solver.test.Test.disableCache(); //otherwise we run out of memory
            
		int i = -1;
		try {
            tt.setParameter("Solver","AutoSimplify","false");
            tt.setParameter("Solver","TypeinMode","false");
    		if (steps == null) {
    			System.out.printf("All First Steps:\n");
    			SAI[] firstSteps = tt.getAllNextSteps("Solver");
    			for (i = 0; i < firstSteps.length; ++i) {
    				List<String> skills = Arrays.asList(tt.getSkills(firstSteps[i]));
    				System.out.printf("%2d. %-30.30s %-4s\n",
    						i, firstSteps[i].toString(), skills.toString());
    			}
    			ExampleTracerEvent result = new ExampleTracerEvent(tt); 
    			tt.solveIt(result);
                System.out.printf("after solveIt(): isDone() %b\n", tt.isDone("Solver"));
    		} else {
    			System.out.printf("%d Given Steps:\n", steps.length);
    			for (i = 0; i < steps.length && !tt.isDone("Solver"); ++i) {
        			ExampleTracerSAI sai = new ExampleTracerSAI("solverMain", steps[i].getAction(),
        					steps[i].getInput());
        			ExampleTracerEvent result = new ExampleTracerEvent(tt, sai); 
    				System.out.printf("%2d. %-30s %5b %s\n %-12s",
    						i, steps[i].toString(),
    						tt.doStep(steps[i], result), result.toString(),
    						result.getInterfaceActions());
    				List<ExampleTracerEvent.InterfaceAction> ias = result.getInterfaceActions();
    				if (ias != null) {
    					for (ExampleTracerEvent.InterfaceAction ia : ias) {
    						Map<String, Object> solverProps = ia.getSolverProperties();
    						if (solverProps != null) {
    							for (String key : solverProps.keySet())
    								System.out.printf(" [%s,%s]", key, 
    										solverProps.get(key) == null ? "" : solverProps.get(key).toString());
    						}
    					}
    				}
    				trace.out();
    			}
                System.out.printf("after steps[%d]: isDone() %b\n",
                		steps.length, tt.isDone("Solver"));
    		}
        } catch(InvalidParamException ipe){
            Logger.log(ipe);
        } catch(InvalidStepException ise){
            Logger.log("on step "+i+": "+(steps == null ? null : steps[i].toString()), ise);
        }		
	}

	/**
	 * Test method for 'edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandler.processPseudoTutorInterfaceAction(Vector, Vector, Vector)'
	 * @param selection selection vector
	 * @param action action vector
	 * @param input input vector
	 * @return list of {@link MessageObject}s generated
	 */
	public Vector<MessageObject> runProcessPseudoTutorInterfaceAction(Vector selection,
			Vector action, Vector input, String actor) {
		pseudoTutorMessageHandler.processPseudoTutorInterfaceAction(selection,action, input);
		return sink.getLatestMsgs();
	}
}
