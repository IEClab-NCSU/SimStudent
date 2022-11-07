package edu.cmu.pact.BehaviorRecorder.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pact.CommWidgets.JCommWidget;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest.SinkToolProxy;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class UIEventMessageTest extends TestCase {

	public UIEventMessageTest() {
		super();
	}

	public UIEventMessageTest(String name) {
		super(name);
	}

/**
 * Common components of model-tracing tests.
 */
public static abstract class MTTest {
	public Vector<String> selection = new Vector<String>();
	public Vector<String> action = new Vector<String>();
	public Vector<String> input = new Vector<String>();
	public String actor = "";
	int nAssocRules = 0;
	
	MTTest(String selection, String action, String input, String actor, int nRules) {
		if (selection != null)
			this.selection.add(selection);
		if (action != null)
			this.action.add(action);
		if (input != null)
			this.input.add(input);
		if (actor != null)
			this.actor= "Student";
		this.nAssocRules = nRules;
	}

	/**
	 * Check the messages produced by the test against the expected
	 * result data in this object.
	 * @param tests the list of tests we're currently executing
	 * @param currTest the test index, for error msgs
	 * @param tMsgs list of {@link MessageObject}s to test
	 */
	public abstract void checkResult(MTTest[] tests, int currTest, Vector tMsgs);
}

/**
 * Associate a hint request (selection) and a model trace result.
 */
/*private static class HintTest extends MTTest {
	String hintSelection = "";
	String[] hintMsgs = new String[0];

	HintTest(String selection, String hintSelection, String[] hintMsgs, int nRules) {
		super(null, null, null, null, nRules);
		this.selection.add("help");
		this.selection.add(selection);
		this.action.add("ButtonPressed");
		this.action.add("PreviousFocus");
		this.input.add("-1");
		this.hintSelection = hintSelection.trim();
		this.hintMsgs = hintMsgs;
		this.nAssocRules = nRules;
	}

	/**
	 * @see UIEventMessageTest.MTTest#checkResult(int, Vector)
	 /
	public void checkResult(MTTest[] tests, int currTest, Vector tMsgs) {
		MessageObject msg = (MessageObject) tMsgs.get(0);
		String messageType = msg.getMessageTypeProperty();
		assertEquals(testListName+"["+currTest+"]: incorrect MessageType",
				"ShowHintsMessage", messageType);

		Vector tsv = (Vector) msg.getProperty("Selection");
		assertNotNull(testListName+"["+currTest+"]: no selection in hint message",
				tsv);
		String tSelection = (String) tsv.get(0);
		trace.out("mt", "this.hintSelection " + hintSelection +
				  " ?= tSelection " + tSelection + ";");
		String ts = (tSelection == null ? "" : tSelection.toLowerCase().trim());
		assertEquals(testListName+"["+currTest+
				"] hintSelections differ", hintSelection.toLowerCase(), ts);
		trace.out("mt", "this.hintMsgs.length " + this.hintMsgs.length +
				  " ?= hintMsgs.size() " + tMsgs.size());
		
		Vector tHintMsgs = (Vector) msg.getProperty("HintsMessage");
		for (int i = 0; i < hintMsgs.length && i < tHintMsgs.size(); ++i) {
			String expected = hintMsgs[i].trim();
			String actual = ((String) tHintMsgs.get(i)).trim();
			trace.out("mt", "msg[" + i + "]: expected, actual:\n  " +
					  expected + "\n  " + actual + "\n");
			assertEquals(testListName+"["+currTest+
					","+i+"] \""+expected+"\" != "+actual, expected, actual);
		}
		assertEquals(testListName+"["+currTest+"] nHints unequal",
				hintMsgs.length, tHintMsgs.size());
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
*/
/**
 * Associate a test (selection, action, input) and a model trace result.
 */
public class AttemptTest extends MTTest {
	String messageType = "";
	private final String successOrBuggyMsg;
	private final boolean outOfOrder;
	private String[] skillNames;
	
	public AttemptTest(String selection, String action, String input,
				  String messageType) {
		this(selection, action, input, messageType, null, false, 1, null);
	}
	
	AttemptTest(String selection, String action, String input,
			  String messageType, int nRules) {
		this(selection, action, input, messageType, null, false, nRules, null);
	}

	public AttemptTest(String selection, String action, String input,
				  String messageType, boolean outOfOrder) {
		this(selection, action, input, messageType, null, outOfOrder, 1, null);
	}
	
	AttemptTest(String selection, String action, String input,
			String messageType, String successOrBuggyMsg) {
		this(selection, action, input, messageType, successOrBuggyMsg, false, 1, null);
	}
	
	AttemptTest(String selection, String action, String input,
			String messageType, String[] skillNames) {
		this(selection, action, input, messageType, null, false, 1, skillNames);
	}
	
	public AttemptTest(String selection, String action, String input,
			String messageType, String successOrBuggyMsg, boolean outOfOrder, int nRules,
			String[] skillNames) {
		super(selection, action, input, "Student", nRules);
		this.messageType = messageType;
		this.successOrBuggyMsg = successOrBuggyMsg;
		this.outOfOrder = outOfOrder;
		this.nAssocRules = nRules;
		this.skillNames = skillNames;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("AttemptTest");
		sb.append("[S=").append(selection);
		sb.append(",A=").append(action);
		sb.append(",I=").append(input);
		sb.append("]r=").append(messageType);
		if (successOrBuggyMsg != null)
			sb.append(",").append(successOrBuggyMsg);
		if (outOfOrder)
			sb.append(",").append("outOfOrder");
		return sb.toString();
	}

	/**
	 * Check the messages expected to be generated by this attempt.
	 * Then check any tutor-performed steps in sequence after this step.
	 * @see UIEventMessageTest.MTTest#checkResult(int, Vector)
	 */
	public void checkResult(MTTest[] tests, int currTest, Vector msgs) {
		assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
		assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
		for (int i = 0; i < msgs.size(); ++i)
			trace.out("mtt", "msgs["+i+"]: "+msgs.get(i));
		
		MessageObject msg = (MessageObject) msgs.remove(0);
		String type0 = msg.getMessageType();
		
		assertEquals(testListName+"["+currTest+"]: not match MessageType",
				this.messageType, type0);
		if (nAssocRules > 0) {
			msg = (MessageObject) msgs.remove(0);
			String type1 = msg.getMessageType();
			assertEquals(testListName+"["+currTest+"]: Rules msgtype wrong", "AssociatedRules", type1);
			Vector rules = (Vector) msg.getProperty("Rules");
			assertEquals(testListName+"["+currTest+"]: no. AssociatedRules wrong",
					 nAssocRules, rules == null ? -1 : rules.size());
			if (skillNames != null) {
				Vector skills = (Vector) msg.getProperty("Skills");
				trace.out("mtt", testListName+"["+currTest+"] skillNames "+Arrays.asList(skillNames)+
						"; skills "+skills);
				for (String skillName : skillNames) {
					int i = 0;
					for (; i < skills.size(); ++i)
						if (((String) skills.get(i)).startsWith(skillName))
							break;
					assertTrue(testListName+"["+currTest+"]: skill "+skillName+" not found",
							i < skills.size());
				}
			}
		}
		if (successOrBuggyMsg != null) {
			String expType1 = "BuggyMessage";
			String textProperty = "BuggyMsg";
			if (messageType.equals(CORRECT)) {
				expType1 = "SuccessMessage";
				textProperty = "SuccessMsg";
			}
			msg = (MessageObject) msgs.remove(0);
			String type1 = msg.getMessageType();

			assertEquals(testListName+"["+currTest+"]: msgType[1] mismatch", expType1, type1);
			String text = (String) msg.getProperty(textProperty);
			assertEquals(testListName+"["+currTest+"]: text", successOrBuggyMsg, text);
		}
		if (outOfOrder) {
			String expType1 = "HighlightMsg";
			String textProperty = "HighlightMsgText";
			String expText = "Instead of the step you are working on, please work on the highlighted step.";
			msg = (MessageObject) msgs.remove(0);
			String type1 = msg.getMessageType();
			assertEquals(testListName+"["+currTest+"]: msgType[1] mismatch", expType1, type1);
			String text = (String) msg.getProperty(textProperty);
			assertEquals(testListName+"["+currTest+"]: text", expText, text);
		}
		/*while (++currTest < tests.length
				/*&& tests[currTest] instanceof TutorPerformedStepTest*) {
			tests[currTest].checkResult(tests, currTest, msgs);*
		}*/
	}
}


/**
 * Associate a test (selection, action, input) and a UIEvent.
 */
public class UIEventTest extends MTTest {
	UIEventTest(String selection, String action, String input, String actor, int nRules) {
		super(selection, action, input, actor, nRules);
	}
	String messageType = "";
	private String[] skillNames;
	//MessageObject msgs = JCommWidget.createUIEventMessage(selection,action,input,null);
	/*
	 * figure out how to test a send and read within JUnit
	 * everything only works right now because nothing actually runs, its just passing stub-functions
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.UIEventMessageTest.MTTest#checkResult(edu.cmu.pact.BehaviorRecorder.ProblemModel.UIEventMessageTest.MTTest[], int, java.util.Vector)
	 */
	//BR_Controller.handleCommMessage(msgs);
	@Override
	public void checkResult(MTTest[] tests, int currTest, Vector msgs) {
		
		assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
		assertFalse(testListName+"["+currTest+"]: msgs is not empty", msgs.size() > 0);
		assertNotNull(testListName+"["+currTest+"]: variable tracks input", controller.getProblemModel().getVariableTable().get(selection.get(0)));
		assertTrue(testListName+"["+currTest+"]: variable table enters correct input", controller.getProblemModel().getVariableTable().get(selection.get(0)) == input.get(0));
		assertNotNull(testListName+"["+currTest+"]: variable table tracks action", controller.getProblemModel().getVariableTable().get(selection.get(0)+".action"));
		assertTrue(testListName+"["+currTest+"]: variable table enters correct action", controller.getProblemModel().getVariableTable().get(selection.get(0)+".action") == action.get(0));
	}	
}


/** Mnemonic for out-of-order cases. */
private static final boolean OUT_OF_ORDER = true;

/** MessageType value for correct actions. */
private static final String CORRECT = "CorrectAction";

/** MessageType value for incorrect actions. */
private static final String INCORRECT = "InCorrectAction";

private static final String WRONGUSER = "WrongUserMessage";
/** Index in XxxxTests[] of current test. */
private int currTest = -1;

/** Controller for this test. */
BR_Controller controller = null;

/** Message sink--substitute for UTP. */
SinkToolProxy sink = null;

/** Whether to prompt before each message to send. */
private static boolean oneAtATime = false;

/** Whether to turn on verbose output. */
private static boolean verbose = false;

/** Tests chosen on command line. */
private static java.util.Set testsChosen = null;

/** Message handler to exercise. */
private PseudoTutorMessageHandler pseudoTutorMessageHandler;

/** Current test we're running. */
private static String testListName;

/**
 * Command-line syntax help.
 */
public static final String usageMsg =
	"Usage:\n" +
	"  edu.cmu.pact.BehaviorRecorder.Controller.UIEventMessageTest [-h] [-v] [test...]\n" +
	"where--\n" +
	"  -h  means print this help message;\n" +
	"  -v  means turn on (verbose) console output;\n" +
	"  test... lists test(s) chosen to run; if none, run all tests.";

public static void main(String[] args) {
	int i;
	for (i = 0; (i < args.length) && ('-' == args[i].charAt(0)); i++) {
		switch(args[i].charAt(1)) {
		case 'h':
			System.err.println(usageMsg);
		    System.exit(1);
			break;            // not reached
		case 'o':
			oneAtATime = true;
			break;
		case 'v':
			trace.addDebugCode("mtt");
		    verbose = true;
		    break;			default:
			System.err.println("Unknown option '" + args[i].charAt(1) +
							   "'. " + usageMsg);
		    System.exit(1);
		}
	}
	
	if (i < args.length)
		testsChosen = new HashSet();
	for ( ; i < args.length; i++)
		testsChosen.add(args[i].toLowerCase());

	junit.textui.TestRunner.run(UIEventMessageTest.suite());
}

/**
 * Suite to run some or all tests in this class.  Uses
 * {@link #testsChosen} to choose the test(s) to run; if empty,
 * runs all tests.
 */
public static Test suite() {
	if (testsChosen == null)
		return new TestSuite(UIEventMessageTest.class);
	
	TestSuite suite= new TestSuite(); 

	if (testsChosen.contains("addition")) {
		suite.addTest(new UIEventMessageTest("testAdd678plus187")); 
	}
	return suite;
}

/**
 * No-argument constructor for JUnit. Equivalent to
 * {@link #UIEventMessageTest(String) JessModelTracingTest(null)}
 */

/**
 * Create the {@link #controller}, {@link #sink} and 
 * {@link #pseudoTutorMessageHandler}.
 */
protected void setUp() throws Exception {
	super.setUp();
	
	CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);

	controller = launcher.getFocusedController();
	
	sink = new SinkToolProxy(controller); 
    controller.setUniversalToolProxy(sink);
    controller.getProblemModel().setUseCommWidgetFlag(false);

//    pseudoTutorMessageHandler = new PseudoTutorMessageHandler(controller);
    pseudoTutorMessageHandler = controller.getPseudoTutorMessageHandler();
}

/**
 * Remove the members established by {@link #setUp()}.
 */
protected void tearDown() throws Exception {
	super.tearDown();
	pseudoTutorMessageHandler = null;
	sink = null;
	controller = null;
}

/** Hint messages for {@link #Add678plus187StudentTestsPreferredPath}. */
private static final String[] hints678plus187C5R1 = {
	"First hint for carry cell C5R1.",
	"Second hint for carry cell C5R1.",
	"Third hint for carry cell C5R1."
};
private static final String[] hints678plus187C4R1 = {
	"First hint for carry cell C4R1.",
	"Second hint for carry cell C4R1.",
	"Third hint for carry cell C4R1."
};

private MTTest[] Add678plus187StudentTestsPreferredPath = { //multi-col addition 678+187, C-col, R-row
	new AttemptTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT, new String[] { "add-addends addition" }),//
	new UIEventTest("pie1", "filledInSlices", "amount=2;slices=1,2", "Student", 0),
	//new HintTest("table1_C3R4", "table1_C5R1", hints678plus187C5R1, 0),
	new AttemptTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT, new String[] { "write-carry addition" }),
	new UIEventTest("pie1", "filledInSlices", "amount=1;slices=2", "Student",0),
	new AttemptTest("table1_C4R1",	"UpdateTable",	"10",	INCORRECT),
	//new HintTest("table1_C3R4", "table1_C4R1", hints678plus187C4R1, 0),
	new AttemptTest("table1_C5R4",	"UpdateTable",	"5",	INCORRECT),
	new UIEventTest("pie1", "filledInSlices", "amount=2;slices=1,2", "Student",0),
	new UIEventTest("pie1", "filledInSlices", "amount=3;slices=1,2,4", "Student",0),
	new AttemptTest("done",	"ButtonPressed",	"-1", INCORRECT, BR_Controller.NOT_DONE_MSG),
	new AttemptTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, new String[] { "add-carry addition" }),
	new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, new String[] { "write-carry addition" }),
	new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, new String[] { "add-carry addition" }),
	new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
};
/**
 * Run the graph 678+187 with student actions.
 */
public void testAdd678plus187Student() {
	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187Student.brd";
    URL url = Utils.getURL(problemFileLocation, this);
    trace.out("mt" + "problemFileLocation str = "
            + problemFileLocation + ", url = " + url);
    controller.openBRFromURL(url.toString());
    List startStateMsgs = sink.getLatestMsgs();
    trace.out("mt", "testAdd678plus187: nStartStateMsgs="+startStateMsgs.size());
	runMTTests(Add678plus187StudentTestsPreferredPath, "Add678plus187StudentPreferred");
}
/**
 * Inputs and modelTrace() results for the Add678plus187 tutor.
 * Alternate path.
 */

/*
public void testCreateToolMessage() {
	Vector selection = new Vector(), action = new Vector(), input = new Vector();
	String transaction_id = pseudoTutorMessageHandler.enqueueToolActionToStudent(selection, action, input);
	trace.out("mt", "transaction_id "+transaction_id);
	assertNotNull("null transaction id", transaction_id);
	assertTrue("empty transaction id", transaction_id.trim().length() > 0);
}
*/
/**
 * Run a list of MTTests.
 * @param  tests array of MTTests to run
 * @param  listName name of test list for log output
 */
public void runMTTests(MTTest[] tests, String listName) {
	testListName = listName;
	BufferedReader promptRdr =
		new BufferedReader(new InputStreamReader(System.in));
	for (currTest = 0; currTest < tests.length; currTest++) {
		if (oneAtATime) {
			trace.out("\n___press Enter to run next test step___");
			try {
				promptRdr.readLine();
			} catch (IOException ioe) {}
		}
		long startTime = (new Date()).getTime();
		MTTest t = tests[currTest];
		Vector msgs=null;
		trace.out("vt", "\nRunning " + listName + " #" + currTest);
		if (t instanceof UIEventTest){
			controller.getProblemModel().handleUntutoredAction(JCommWidget.createUntutoredActionMessage(t.selection, t.action, t.input, null));
			msgs = sink.getLatestMsgs();
			}
		else
			msgs = runProcessPseudoTutorInterfaceAction(t.selection, t.action, t.input, t.actor);
		t.checkResult(tests, currTest, msgs);
		trace.out("mtt", "\nPassed " + listName + " #" + currTest +
				" " + ((new Date()).getTime() - startTime) + " ms");
	}
}

/**
 * Test method for 'edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandler.processPseudoTutorInterfaceAction(Vector, Vector, Vector)'
 * @param selection selection vector
 * @param action action vector
 * @param input input vector
 * @return list of {@link MessageObject}s generated
 */
public Vector runProcessPseudoTutorInterfaceAction(Vector selection,
		Vector action, Vector input, String actor) {
	trace.out("mtt", "trying InterfaceAction" +selection+":"+action+":"+input+":"+actor);
	pseudoTutorMessageHandler.processPseudoTutorInterfaceAction(selection,
			action, input, actor);
	return sink.getLatestMsgs();
}


}
