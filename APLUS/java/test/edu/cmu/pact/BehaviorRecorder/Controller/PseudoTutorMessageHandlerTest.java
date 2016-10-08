/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.HintPolicyEnum;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.model.Skill;

public class PseudoTutorMessageHandlerTest extends TestCase {

	/** Still needed for Java-based tracer; not needed for JavaScript-based tracer (I hope). */
	protected static BR_Controller controller = null;

	/**
	 * No-argument constructor for JUnit. Equivalent to
	 * {@link #PseudoTutorMessageHandlerTest(String) JessModelTracingTest(null)}
	 */
	public PseudoTutorMessageHandlerTest() {
		this (null);
	}
	
	/**
	 * Regular constructor.
	 * @param name test name
	 */
	public PseudoTutorMessageHandlerTest(String name) {
		super(name);
	}
	
	/**
	 * Substitute for {@link UniversalToolProxy} to collect messages sent.
	 */
	public static class SinkToolProxy extends UniversalToolProxy {
		
		/**
		 * Messages collected by {@link #handleCommMessage(MessageObject)}
		 * since last call to {@link #getLatestMsgs()}.
		 */
		Vector<MessageObject> msgsRcvd = new Vector<MessageObject>();
		
		/** For {@link #addStartStateMessage(MessageObject). */
		private List<MessageObject> startStateMessages;

		/** For getting the initial hint message. */
		private HintMessagesManager hintMsgsMgr = null;
		
		/**
		 * Initialize the superclass.
		 * @param controller
		 */
		public SinkToolProxy(BR_Controller controller) {
			try {
				super.init(controller);
				if(controller != null)
					hintMsgsMgr = getController().getHintMessagesManager();
				else
					hintMsgsMgr = new HintMessagesManagerForClient(null);
			} catch(Exception e) {
				trace.err("Error on UTP.init("+controller+"): "+e+"; cause "+e.getCause());
				hintMsgsMgr = new HintMessagesManagerForClient(null);
			}
		}
		
		/**
		 * Override for UTP communications setup method.
		 */
		Socket connect() { return null; }
		/**
		 * Override to retrieve list of start state messages for external
		 * interface.
		 * 
		 * @param  v Vector of messages to add to; if null, creates a Vector
		 * @return Vector to which msgs were added
		 */
		protected synchronized Vector createCurrentStateVector(Vector v) {
			if (v == null)
				v = new Vector();
			trace.out("log", "startStateVector size " + v.size() + ", to add " +
					  (startStateMessages==null ? 0 : startStateMessages.size()) +
					  " startStateMessages");
			if (startStateMessages != null)
				v.addAll(startStateMessages);
			return v;
		}

		
		/**
		 * Override to add a message to the list of start state messages
		 * {@link #startStateMessages}.  Creates list if necessary.
		 * @param  msg MessageObject to add
		 */
		synchronized void addStartStateMessage(MessageObject msg) {
			if (startStateMessages == null)
				startStateMessages = new ArrayList<MessageObject>();
			startStateMessages.add(msg);
		}

		/**
		 * Override to collect messages into {@link #msgsRcvd}
		 * @param mo message received
		 * @see UniversalToolProxy#handleCommMessage(MessageObject)
		 */
		public void handleMessage(MessageObject mo) {
			
			msgsRcvd.add(mo);
			trace.out("log", "handleCommMessage("+mo+")");
			String messageType = (String) mo.getMessageType();
			if (messageType != null && messageType.matches(".*[hH]int.*")) {
				if (hintMsgsMgr != null) {
					hintMsgsMgr.setMessageObject(mo);
	                String message = hintMsgsMgr.getFirstMessage();
				}
			}
		}
		
		/**
		 * Collect the messages received by
		 * {@link #handleCommMessage(MessageObject)} since the last call
		 * to this method. Clears {@link #msgsRcvd}.
		 * @return list of latest messages.
		 */
		public Vector<MessageObject> getLatestMsgs() {
			Vector<MessageObject> result = new Vector<MessageObject>(msgsRcvd);
			trace.out("mtt", "sink.getLatestMsgs() retrieves "+result.size());
			msgsRcvd.clear();
			return result;
		}
	}
	
	/**
	 * Common components of model-tracing tests.
	 */
	public abstract static class MTTest {
		public Vector selection = new Vector();
		public Vector action = new Vector();
		public Vector input = new Vector();
		public String actor = "";
		int nAssocRules = 0;
		private String[] skillNames;
		private Map<String, Skill> skills;
		protected String tutorSelection = null, tutorAction = null, tutorInput = null;
		
		MTTest(String selection, String action, String input, String actor, int nAssocRules,
				String[] skillNames) {
			init(selection, action, input, actor);
			this.nAssocRules = nAssocRules;
			this.skillNames = skillNames;
		}
		
		MTTest(String selection, String action, String input, String actor, int nAssocRules,
				Skill ...skills) {
			init(selection, action, input, actor);
			this.nAssocRules = nAssocRules;
			if (skills == null)
				return;
			this.skills = new HashMap<String, Skill>();
			for (Skill skill : skills)
				this.skills.put(skill.getSkillName(), skill);
		}
		private void init(String selection, String action, String input, String actor) {
			if (selection != null)
				this.selection.add(selection);
			if (action != null)
				this.action.add(action);
			if (input != null)
				this.input.add(input);
			if (actor != null)
				this.actor= "Student";
		}

		/**
		 * Check the messages produced by the test against the expected
		 * result data in this object.
		 * @param tests the list of tests we're currently executing
		 * @param currTest the test index, for error msgs
		 * @param tMsgs list of {@link MessageObject}s to test
		 */
		public abstract void checkResult(MTTest[] tests, int currTest, List<MessageObject> tMsgs);

		/**
		 * Check the skills in an Associated Rules msg.
		 * @param testListName
		 * @param currTest
		 * @param msgs removes msgs.get(0), expects it to be an AssociatedRules msg
		 */
		protected void checkAssocRules(String testListName, MTTest[] tests, int currTest, List<MessageObject> msgs) {
			MTTest test = tests[currTest];
			if (nAssocRules < 1)
				return;
			MessageObject msg = (MessageObject) msgs.remove(0);
			String type1 = msg.getMessageType();
			assertEquals(testListName+"["+currTest+"]: Rules msgtype wrong", "AssociatedRules", type1);
			String indicator = (test instanceof HintTest ? PseudoTutorMessageBuilder.HINT :
				((AttemptTest) test).messageType.equals(CORRECT) ? "CORRECT" : "INCORRECT").toLowerCase();
			assertEquals(testListName+"["+currTest+"]: Rules indicator wrong", indicator,
					msg.getProperty(PseudoTutorMessageBuilder.INDICATOR).toString().toLowerCase());
			if(tutorSelection != null)
				assertEquals(testListName+"["+currTest+"]: tutor selection wrong",
					tutorSelection.toLowerCase(), msg.getSelection0().toLowerCase());
			if(tutorAction != null)
				assertEquals(testListName+"["+currTest+"]: tutor action wrong",
						tutorAction.toLowerCase(), msg.getAction0().toLowerCase());
			if(tutorInput != null)
				assertEquals(testListName+"["+currTest+"]: tutor input wrong",
						tutorInput.toLowerCase(), msg.getInput0().toLowerCase());
			if (skills != null) {
				Vector msgSkills = (Vector) msg.getProperty("Skills");
				assertEquals(testListName+"["+currTest+"] nSkills", skills.size(), msgSkills.size());
				for (Iterator it = msgSkills.iterator(); it.hasNext(); ) {
					Skill mSk = Skill.skillBarToSkill((String) it.next());
					checkSkill(testListName+"["+currTest+"]", skills.get(mSk.getSkillName()), mSk);
				}
			} else if (skillNames != null) {
				Vector msgSkills = (Vector) msg.getProperty("Skills");
				trace.out("mtt", testListName+"["+currTest+"] skillNames "+Arrays.asList(skillNames)+
						"; skills "+msgSkills);
				if (skillNames.length < 1)
					assertNull(testListName+"["+currTest+"] should have no skills", msgSkills);
				else
					assertEquals(testListName+"["+currTest+"] wrong nSkillNames", skillNames.length, msgSkills.size());
				for (String skillName : skillNames) {
					int i = 0;
					for (; i < msgSkills.size(); ++i)
						if (((String) msgSkills.get(i)).startsWith(skillName))
							break;
					assertTrue(testListName+"["+currTest+"]: skill "+skillName+" not found",
							i < msgSkills.size());
				}
			}
		}
	}
	
	/**
	 * Subclass for testing Matcher
	 */
	private static class AttemptReplaceTest extends AttemptTest {
		
		private String tInput;

		AttemptReplaceTest(String selection, String action, String sInput, String tInput,
				String[] rules) {
			super(selection, action, sInput, CORRECT, rules); // replace only on correct steps
			this.tInput = tInput;
		}

		/**
		 * Check the messages expected to be generated by this attempt.
		 * Then check any tutor-performed steps in sequence after this step.
		 * @see PseudoTutorMessageHandlerTest.MTTest#checkResult(int, Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, List<MessageObject> msgs) {
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
			MessageObject msg = (MessageObject) msgs.get(0);
			trace.out("mtt", "msgs[0]: "+msg);
			assertEquals("Input value in Correct/Incorrect msg[0]",
					PseudoTutorMessageBuilder.s2v(tInput), msg.getProperty("Input"));
			super.checkResult(tests, currTest, msgs);
		}
	}

	/**
	 * Associate a hint request (selection) and a model trace result.
	 */
	private static class HintTest extends MTTest {
		String hintSelection = "";
		String[] hintMsgs = new String[0];
		
		HintTest(String selection, String action,  String hintSelection, String[] hintMsgs, int nRules) {
			this(selection, action,  hintSelection, hintMsgs, nRules, (String[]) null);
		}
		
		HintTest(String selection, String hintSelection, String[] hintMsgs, int nRules) {
			this(selection, null,  hintSelection, hintMsgs, nRules, (String[]) null);
		}
		HintTest(String selection, String action, String hintSelection, String[] hintMsgs, int nRules,
				String[] skillNames) {
			super(null, null, null, null, nRules, skillNames);
			init(selection, action, hintSelection, hintMsgs);
		}
		HintTest(String selection, String hintSelection, String[] hintMsgs, int nRules,
				Skill ...skills) {
			super(null, null, null, null, nRules, skills);
			init(selection, null, hintSelection, hintMsgs);
		}
		private void init(String selection, String action, String hintSelection, String[] hintMsgs) {
			this.selection.add("help");
			this.selection.add(selection);
			this.action.add("ButtonPressed");
			this.action.add("PreviousFocus");
			
			if (action != null)
				this.action.add(action);
			
			this.input.add("-1");
			this.hintSelection = hintSelection.trim();
			this.hintMsgs = hintMsgs;
		}

		/**
		 * @see PseudoTutorMessageHandlerTest.MTTest#checkResult(int, Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, List<MessageObject> tMsgs) {
			MessageObject msg = tMsgs.remove(0);
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
			assertEquals(testListName+"["+currTest+
					"] hintSelections differ", hintSelection.toLowerCase(), ts);
			
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
			this.checkAssocRules(testListName, tests, currTest, tMsgs);
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
	 * Check a tutor-performed step against model-tracing result messages.
	 */
	private static class TutorPerformedStepTest extends MTTest {
		String expTutorMessageType = "";
		
		TutorPerformedStepTest(String selection, String action, String input,
					  String messageType, int nRules) {
			super(selection, action, input, "Tool", nRules);
			this.expTutorMessageType = messageType;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("TutorPerformedStepTest");
			sb.append("[S=").append(selection);
			sb.append(",A=").append(action);
			sb.append(",I=").append(input);
			sb.append("]r=").append(expTutorMessageType);
			return sb.toString();
		}

		/**
		 * @see PseudoTutorMessageHandlerTest.MTTest#checkResult(int, Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, List<MessageObject> msgs) {
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
			MessageObject toolMsg = (MessageObject) msgs.remove(0);
			trace.out("mt", "TutorPerformedStepTest.checkResult msg[0]\n "+toolMsg);
			String toolMessageType = toolMsg.getMessageType();
			assertEquals(testListName+"["+currTest+"]: incorrect MessageType",
					"InterfaceAction", toolMessageType);
			
			MessageObject msg = (MessageObject) msgs.remove(0);
			trace.out("mt", "TutorPerformedStepTest.checkResult msg[1]\n "+msg);
			String messageType = msg.getMessageType();
			assertEquals(testListName+"["+currTest+"]: incorrect MessageType",
					expTutorMessageType, messageType);
			Vector sv = (Vector) msg.getProperty("Selection");
			assertNotNull(testListName+"["+currTest+"]: selection is null", sv);
			assertEquals(testListName+"["+currTest+"]: selection mismatch",
					selection.get(0), sv.get(0));
			Vector av = (Vector) msg.getProperty("Action");
			assertNotNull(testListName+"["+currTest+"]: action is null", av);
			assertEquals(testListName+"["+currTest+"]: action mismatch",
					action.get(0), av.get(0));
			Vector iv = (Vector) msg.getProperty("Input");
			assertNotNull(testListName+"["+currTest+"]: input is null", iv);
			assertEquals(testListName+"["+currTest+"]: input mismatch", input.get(0), iv.get(0));			

		}
	}

	/**
	 * Associate a test (selection, action, input) and a model trace result.
	 */
	public static class AttemptTest extends MTTest {
		String messageType = "";
		private String successOrBuggyMsg;
		private boolean outOfOrder;
		
		public AttemptTest(String selection, String action, String input,
					  String messageType) {
			this(selection, action, input, messageType, null, false, 1, (String[]) null);
		}
		
		AttemptTest(String selection, String action, String input,
				  String messageType, int nRules) {
			this(selection, action, input, messageType, null, false, nRules, (String[]) null);
		}
		
		AttemptTest(String selection, String action, String input, String messageType,
				int nRules, String tutorSelection, String tutorAction, String tutorInput) {
			this(selection, action, input, messageType, null, false, nRules, (String[]) null);
			this.tutorSelection = tutorSelection;
			this.tutorAction = tutorAction;
			this.tutorInput = tutorInput;
		}
	
		public AttemptTest(String selection, String action, String input,
					  String messageType, boolean outOfOrder) {
			this(selection, action, input, messageType, null, outOfOrder, 1, (String[]) null);
		}
		
		AttemptTest(String selection, String action, String input,
				String messageType, String successOrBuggyMsg) {
			this(selection, action, input, messageType, successOrBuggyMsg, false, 1, (String[]) null);
		}
		
		AttemptTest(String selection, String action, String input,
				String messageType, String[] skillNames) {
			this(selection, action, input, messageType, null, false, 1, skillNames);
		}
		
		public AttemptTest(String selection, String action, String input,
				String messageType, String successOrBuggyMsg, boolean outOfOrder, int nRules,
				String[] skillNames) {
			super(selection, action, input, "Student", nRules, skillNames);
			init(messageType, successOrBuggyMsg, outOfOrder);
		}
		
		public AttemptTest(String selection, String action, String input,
				String messageType, String successOrBuggyMsg, boolean outOfOrder, int nRules,
				Skill ...skills) {
			super(selection, action, input, "Student", nRules, skills);
			init(messageType, successOrBuggyMsg, outOfOrder);
		}
		
		private void init(String messageType, String successOrBuggyMsg, boolean outOfOrder) {
			this.messageType = messageType;
			this.successOrBuggyMsg = successOrBuggyMsg;
			this.outOfOrder = outOfOrder;
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
		 * @see PseudoTutorMessageHandlerTest.MTTest#checkResult(int, Vector)
		 */
		public void checkResult(MTTest[] tests, int currTest, List<MessageObject> msgs) {
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
			for (int i = 0; i < msgs.size(); ++i)
				trace.out("mtt", "msgs["+i+"]: "+msgs.get(i));
			
			MessageObject msg = (MessageObject) msgs.remove(0);
			String type0 = msg.getMessageType();
			assertEquals(testListName+"["+currTest+"]: not match MessageType",
					this.messageType, type0);
			checkAssocRules(testListName, tests, currTest, msgs);
			if (successOrBuggyMsg != null) {
				String expType1 = "BuggyMessage";
				String textProperty = "BuggyMsg";
				if (messageType.equals(CORRECT)) {
					expType1 = "SuccessMessage";
					textProperty = "SuccessMsg";
				}
				msg = (MessageObject) msgs.remove(0);
				String type1 = msg.getMessageType();

				assertEquals(testListName+"["+currTest+"]: text feedback mismatch", expType1, type1);
				String text = (String) msg.getProperty(textProperty);
				assertEquals(testListName+"["+currTest+"]: text", successOrBuggyMsg, text);
			}
			if (outOfOrder) {
				String expType1 = "HighlightMsg";
				String textProperty = "HighlightMsgText";
				String expText = "Instead of the step you are working on, please work on the highlighted step.";
				msg = (MessageObject) msgs.remove(0);
				String type1 = msg.getMessageType();
				assertEquals(testListName+"["+currTest+"]: text feedback mismatch", expType1, type1);
				String text = (String) msg.getProperty(textProperty);
				assertEquals(testListName+"["+currTest+"]: text", expText, text);
			}
			while (++currTest < tests.length
					&& tests[currTest] instanceof TutorPerformedStepTest) {
				tests[currTest].checkResult(tests, currTest, msgs);
			}
		}
	}
	
	static class DelayedFeedbackTest extends MTTest {
		static List<String> messageTypes = new ArrayList<String>();
		String messageType;
		DelayedFeedbackTest(String selection, String action, String input, String messageType, int nSkills) {
			super(selection, action, input, null, nSkills, (String[]) null);
			this.messageType = messageType;
		}
		public void checkResult(MTTest[] tests, int currTest, List<MessageObject> msgs) {
			assertTrue(testListName+"["+currTest+"]: msgs list is null", msgs != null);
			assertTrue(testListName+"["+currTest+"]: msgs is empty", msgs.size() > 0);
			for (int i = 0; i < msgs.size(); ++i)
				trace.out("mtt", "msgs["+i+"]: "+msgs.get(i));

			if("done".equalsIgnoreCase((String) selection.get(0))) {
				MessageObject msg = (MessageObject) msgs.remove(0);
				String type0 = msg.getMessageType();
				assertEquals(testListName+"["+currTest+"]: not match MessageType",
						this.messageType, type0);
			} else
				messageTypes.add(messageType);
			checkAssocRules(testListName, tests, currTest, msgs);
			if("done".equalsIgnoreCase((String) selection.get(0))) {
				int i;
				for(i = 0; i < msgs.size() && i < messageTypes.size(); ++i) {
					MessageObject msg = (MessageObject) msgs.get(i);
					assertEquals(testListName+"["+currTest+"]: not match MessageType",
							messageTypes.get(i), msg.getMessageType());
					
				}
				assertFalse("nMsgs > nIndicators", i < msgs.size());
				assertFalse("nMsgs < nIndicators", i < messageTypes.size());
			}
			messageTypes.clear();
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
	//BR_Controller controller = null;
	
	/** Message sink--substitute for UTP. */
	SinkToolProxy sink = null;
	
	/** Whether to prompt before each message to send. */
	private static boolean oneAtATime = false;

	/** Whether to turn on verbose output. */
	private static boolean verbose = false;
	
	/** Tests chosen on command line. */
	protected static java.util.Set<String> testsChosen = null;

	/** Message handler to exercise. */
	private PseudoTutorMessageHandler pseudoTutorMessageHandler;

	/** Current test we're running. */
	private static String testListName;
	
	/**
	 * Command-line syntax help.
	 */
	public static final String usageMsg =
		"Usage:\n" +
		"  edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest [-h] [-v] [test...]\n" +
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
			    break;
			default:
				System.err.println("Unknown option '" + args[i].charAt(1) + "'. " + usageMsg);
			    System.exit(1);
			}
		}
		
		if (i < args.length)
			testsChosen = new HashSet();
		for ( ; i < args.length; i++)
			testsChosen.add(args[i].toLowerCase());

		junit.textui.TestRunner.run(PseudoTutorMessageHandlerTest.suite());
	}
	
	/**
	 * Suite to run some or all tests in this class.  Uses
	 * {@link #testsChosen} to choose the test(s) to run; if empty,
	 * runs all tests.
	 */
	public static Test suite() {
		TestSuite allTests = new TestSuite(PseudoTutorMessageHandlerTest.class);
		if (testsChosen == null)
			return allTests;
		
		TestSuite suite= new TestSuite(); 
		for (Enumeration en = allTests.tests(); en.hasMoreElements();) {
			TestCase t = (TestCase) en.nextElement();
			if (testsChosen.contains(t.getName().toLowerCase()))
				suite.addTest(t);
		}
		if (testsChosen.contains("addition")) {
			suite.addTest(new PseudoTutorMessageHandlerTest("testAdd678plus187")); 
		}
		return suite;
	}

	/**
	 * Create the {@link #controller}, {@link #sink} and 
	 * {@link #pseudoTutorMessageHandler}.
	 */
	protected void setUp() throws Exception {
		super.setUp();

		controller.startNewProblem(null);
		sink = new SinkToolProxy(controller); 
        controller.setUniversalToolProxy(sink);
        controller.getProblemModel().setUseCommWidgetFlag(false);
        controller.getCtatModeModel().setAuthorMode(CtatModeModel.TESTING_TUTOR);
    
//        pseudoTutorMessageHandler = new PseudoTutorMessageHandler(controller);
        pseudoTutorMessageHandler = controller.getPseudoTutorMessageHandler();
	}

	/**
	 * Remove the members established by {@link #setUp()}.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		pseudoTutorMessageHandler = null;
		sink = null;
	}

	/** Hint messages for {@link #Add678plus187StudentTestsPreferredPath}. */
	private static final String[] hints678plus187C5R1s2 = {
		"First hint for carry cell C5R1 Step 2.",
		"Second hint for carry cell C5R1 Step 2.",
		"Third hint for carry cell C5R1 Step 2."
	};
	private static final String[] hints678plus187C4R1 = {
		"First hint for carry cell C4R1.",
		"Second hint for carry cell C4R1.",
		"Third hint for carry cell C4R1."
	};
	
	private static final String[] hints678plus187C5R4 = {
		"First hint for C5R4.",
		"Second hint for C5R4.",
		"Third hint for C5R4."
	};
	
	private static MTTest[] MoreSpecificBugLink = {
		new AttemptTest("Regz_AddDecimals1_B",	"UpdateTextField",	".14", INCORRECT, "regz; pegz", false, 1,
				(String[]) null),
	};
	/**
	 * Run the graph 678+187 with student actions.
	 */
	public void testMoreSpecificBugLink() {
		String testName = "MoreSpecificBugLink";
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/MoreSpecificBugLink.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt", "problemFileLocation str = "+problemFileLocation +
        		", url = "+url);
        controller.openBRFromURL(url.toString());
        List<MessageObject> startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "testMoreSpecificBugLink: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(MoreSpecificBugLink, testName);
	}
	
	private static MTTest[] Add678plus187StudentTestsPreferredPath = {
		new AttemptTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT, null, false, 1,
				new Skill("add-addends addition", (float) 0.66)),
		new HintTest("table1_C3R4", "table1_C5R1", hints678plus187C5R1s2, 1,
				new Skill("write-carry addition", (float) 0.26769233)),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT, null, false, 1,
				new Skill("write-carry addition", (float) 0.26769233)),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"10",	INCORRECT, null, false, 1,
				new Skill("write-carry addition", (float) 0.25247115)),
		new HintTest("table1_C3R4", "table1_C4R1", hints678plus187C4R1, 1,
				new Skill("write-carry addition", (float) 0.25247115)),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"5",	INCORRECT, null, false, 1,
				new Skill("add-carry addition", (float) 0.26769233)),
		new AttemptTest("done",	"ButtonPressed", "-1", INCORRECT, BR_Controller.NOT_DONE_MSG, false, 1,
				new Skill("add-carry addition", (float) 0.26769233)),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, null, false, 1,
				new Skill("add-carry addition", (float) 0.26769233)),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, null, false, 1,
				new Skill("write-carry addition", (float) 0.25247115)), // C4R1 above matched other path
		new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, null, false, 1,
				new Skill("add-carry addition", (float) 0.6270968)),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT, null, false, 1,
				new Skill("done addition", (float) 0.66))
	};
	/**
	 * Run the graph 678+187 with student actions.
	 */
	public void testAdd678plus187StudentPreferred() {
		String testName = "Add678plus187StudentPreferred";
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187Student.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        List<MessageObject> startStateMsgs = sink.getLatestMsgs();
        checkStateGraphSkills(testName, startStateMsgs, new Skill("add-carry addition"),
        		new Skill("add-addends addition"), new Skill("write-carry addition"),
        		new Skill("done addition"));
        trace.out("mt", "testAdd678plus187: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add678plus187StudentTestsPreferredPath, testName);
	}

	/**
	 * Find the StateGraph message and call {@link #checkSkill(String, Skill, Skill)}
	 * for each entry in the skillBarVector.
	 * @param testName for assert() captions
	 * @param msgs list of messages to search
	 * @param skills expected {@link Skill}s to test against
	 */
	private void checkStateGraphSkills(String testName, List<MessageObject> msgs, Skill ...skills ) {
		if (skills == null || skills.length < 1)
			return;
		HashMap<String, Skill> expSkills = new HashMap<String, Skill>();
		for (Skill sk : skills) expSkills.put(sk.getSkillName(), sk);
		for (MessageObject mo : msgs) {
			if (!"StateGraph".equalsIgnoreCase(mo.getMessageType()))
				continue;
			Vector msgSkills = (Vector) mo.getProperty("Skills");
			assertEquals(testName+" checkStateGraphSkills: nSkills wrong", expSkills.size(), msgSkills.size());
			for (Iterator it = msgSkills.iterator(); it.hasNext(); ) {
				Skill mSk = Skill.skillBarToSkill((String) it.next());
				checkSkill(testName+" checkStateGraphSkills", expSkills.get(mSk.getSkillName()), mSk);
			}
			return;  // success
		}
		fail(testName+" checkStateGraphSkills: expected "+expSkills.size()+" skills, found none");
	}

	/**
	 * Check a single skill's {@link Skill#getSkillName()} and {@link Skill#getPKnown()}.
	 * @param testName for assert() labels
	 * @param expSk expected values
	 * @param foundSk actual values
	 */
	private static void checkSkill(String testName, Skill expSk, Skill foundSk) {
		assertNotNull(testName+": unexpected skill "+foundSk, expSk);
		assertEquals(testName+": wrong skillName ", expSk.getSkillName(), foundSk.getSkillName());
		assertEquals(testName+": "+expSk.getSkillName()+" wrong pKnown",
				expSk.getPKnown(), foundSk.getPKnown(), Float.MIN_VALUE);
	}

	/**
	 * Inputs and modelTrace() results for the Add1_6thPlus3_14ths tutor.
	 */
	private static MTTest[] Add1_6thPlus3_14ths = {
		new HintTest("dummy", "firstDenConv",
				new String[] {"Please enter '42' in the highlighted field."}, 0),
		new AttemptTest("firstDenConv", "UpdateTextArea", "42", CORRECT),
		new HintTest("firstNumConv", "secNumConv",    // regex match "(first|sec)NumConv" on selection
				new String[] {"Please enter '9' in the highlighted field."}, 0),
		new AttemptTest("done",	"ButtonPressed", "-1",	INCORRECT,
				"I'm sorry, but you are not done yet. Please continue working."),
		new AttemptTest("secNumConv", "UpdateTextArea", "9", CORRECT),
		new AttemptTest("done",	"ButtonPressed", "-1",	INCORRECT,
				"I'm sorry, but you are not done yet. Please continue working."),
		new AttemptTest("secDenConv", "UpdateTextArea", "42", CORRECT),
		new AttemptTest("done",	"ButtonPressed", "-1",	CORRECT, 0)
	};
	/**
	 * Run the graph 678+187 with student actions.
	 */
	public void testAdd1_6thPlus3_14ths() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/1_6thPlus3_14ths.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "testAdd678plus187Alt: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add1_6thPlus3_14ths, "Add1_6thPlus3_14ths");
	}

	/**
	 * Inputs and modelTrace() results for the Add1_6thPlus3_14ths tutor.
	 */
	private static MTTest[] traversalHints = {
		new HintTest("dummy", "firstDenConv",
				new String[] {"Please enter '30' in the highlighted field."}, 0),
		new AttemptTest("firstDenConv", "UpdateTextArea", "30", CORRECT),
		new HintTest("dummy", "secDenConv",
				new String[] {"Please enter '60' in the highlighted field."}, 0),
		new AttemptTest("secDenConv", "UpdateTextArea", "60", CORRECT),
		new AttemptTest("done",	"ButtonPressed", "-1",	CORRECT, 0)
	};
	/**
	 * Run the graph 678+187 with student actions.
	 */
	public void testTraversalHints() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/traversalHints.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mtt", "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "testTraversalHints: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(traversalHints, "traversalHints");
	}

	/**
	 * Inputs and modelTrace() results for the Add678plus187 tutor.
	 * Alternate path.
	 */
	/** Hint messages for {@link #Add678plus187StudentTestsPreferredPath}. */
	private static final String[] hints678plus187C5R1s1 = {
		"First hint for carry cell C5R1 Step 1.",
		"Second hint for carry cell C5R1 Step 1.",
		"Third hint for carry cell C5R1 Step 1."
	};
	private static String[] hints678plus187C6R4s1 = {
		"Please enter '5' in the highlighted cell."
	};
	private static MTTest[] Add678plus187StudentTestsAltPath = {
		new HintTest("table1_C5R1", "table1_C5R1", hints678plus187C5R1s1, 1),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT, 1),
		new AttemptTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT, 1),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, 1),
		new TutorPerformedStepTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, 1),
		new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, 1),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT, 1)
	};
	/**
	 * Run the graph 678+187 with student actions.
	 */
	public void testAdd678plus187StudentAlt() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187Student.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "testAdd678plus187Alt: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add678plus187StudentTestsAltPath, "Add678plus187StudentAlt");
	}
	private static MTTest[] Add678plus187TestsPreferredPath = {
		new AttemptTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT, 1),
		new HintTest("table1_C3R4", "table1_C5R1", hints678plus187C5R1s2, 1),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT, 1),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"5",	INCORRECT, 1),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, 1),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, 1),
		new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, 1),
		new TutorPerformedStepTest("done",	"ButtonPressed",	"-1",	CORRECT, 1)
	};
	
	private static MTTest[] Add678plus187WithPreviousActions = {
		new AttemptTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT, 1),
		new HintTest("table1_C3R4", "UpdateTable", "table1_C5R1", hints678plus187C5R1s2, 1),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT, 1),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"5",	INCORRECT, 1),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, 1),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, 1),
		new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, 1),
		new TutorPerformedStepTest("done",	"ButtonPressed",	"-1",	CORRECT, 1)
	};
	
	private static MTTest[] textFieldWithBuggyLinks = {
		new AttemptTest("answerTextField",	"UpdateTextField",	"%(errorText1)%",	INCORRECT,	1,
				"answerTextField",	"UpdateTextField",	"%(answerText)%"),
		new AttemptTest("done",				"ButtonPressed",	"-1",				INCORRECT,
				"I'm sorry, but you are not done yet. Please continue working."),
		new AttemptTest("answerTextField",	"UpdateTextField",	"%(noModelText)%",	INCORRECT,	1,
				"answerTextField",	"UpdateTextField",	"%(answerText)%"),
		new AttemptTest("answerTextField",	"UpdateTextField",	"%(answerText)%",	CORRECT,	1),
		new AttemptTest("done",				"ButtonPressed",	"-1",				CORRECT,	1)
	};
	
	public void testTextFieldWithBuggyLinks() {
		String label = "TextFieldWithBuggyLinks";
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/textFieldWithBuggyLinks.brd";
    	openBRD(problemFileLocation);
        checkStartStateMsgs(label, 5, 0);
		runMTTests(textFieldWithBuggyLinks, label);
	}
	
	private static MTTest[] AddFractions14plus16OneIncorrect = {
		new AttemptTest("firstDenConv",	"UpdateTextField",	"12",	CORRECT, 0),
		new AttemptTest("secDenConv",	"UpdateTextField",	"12",	CORRECT, 0),
		new AttemptTest("firstNumConv",	"UpdateTextField",	"3",	CORRECT, 0),
		new AttemptTest("secNumConv",	"UpdateTextField",	"2",	CORRECT, 0),
		new AttemptTest("ansDen1",	"UpdateTextField",	"12",	CORRECT, 0),
		new AttemptTest("ansNum1",	"UpdateTextField",	"5",	CORRECT, 0),
		new AttemptTest("ansDenFinal1",	"UpdateTextField",	"12",	CORRECT, 0),
		new AttemptTest("ansNumFinal1",	"UpdateTextField",	"5",	CORRECT, 0),
		new AttemptTest("donedddd",	"ButtonPressed",	"-1",	INCORRECT, 0),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT, 0)		
	};
	
	public void testAddFractions14plus16OneIncorrect() {
		String label = "AddFractions14plus16OneIncorrect";
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/1416flash.brd";
    	openBRD(problemFileLocation);
        checkStartStateMsgs(label, 8, 0);
		runMTTests(AddFractions14plus16OneIncorrect, label);
	}
	
	/**
	 * Run the graph 678+187.
	 */
	public void testAdd678plus187() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
    	openBRD(problemFileLocation);
        
        checkStartStateMsgs("Add678plus187Preferred", 16, 0);

        runMTTests(Add678plus187TestsPreferredPath, "Add678plus187Preferred");
	};
	
	/**
	 * Run the graph 678+187.
	 */
	public void testAdd678plus187repeatWithPreviousActions() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
    	openBRD(problemFileLocation);
        
        checkStartStateMsgs("Add678plus187Preferred", 16, 0);

        runMTTests(Add678plus187TestsPreferredPath, "Add678plus187Preferred");
	
        //Repeat the tests sending the previous action on the hint requests:
        controller.goToStartState();
        sink.getLatestMsgs();
        
        runMTTests(Add678plus187WithPreviousActions, "Add678plus187WithPreviousActions");
	}
	
	protected void openBRD(String problemFileLocation) {
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt", "openBRD("+problemFileLocation+") url = " + url);
        controller.openBRFromURL(url.toString());
	}

	private static MTTest[] testMatchersSteps = {
		new AttemptTest("firstNumGiven", "UpdateTextArea",	"1",	CORRECT, 1),
		new AttemptTest("firstDenGiven", "UpdateTextArea",	"2",	CORRECT, 1),
		new AttemptTest("firstNumConv", "UpdateTextArea",	"Three",	CORRECT, 1),
		new AttemptTest("firstDenConv",	"UpdateTextArea",	"four",	CORRECT, 1),
		new AttemptTest("secNumGiven",	"UpdateTextArea",	"5",	CORRECT, 1),
		new AttemptTest("secDenGiven",	"UpdateTextArea",	"6",	CORRECT, 1),
	};

	public void testDifferentMatchers() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/testMatchers.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        
        sink.getLatestMsgs();
        
        runMTTests(testMatchersSteps, "testMatchers");
	}
	
	/* No Hint Biasing */
	private static MTTest[] Add678plus187HintPolicy0 = {
		new HintTest("table1_C5R1", "table1_C6R4", hints678plus187C6R4s1, 1),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"5",	INCORRECT, 1),
		new HintTest("table1_C6R1", "table1_C6R4", hints678plus187C6R4s1, 1)
	};
	
	/* Hints biased by prior error only:  */
	private static MTTest[] Add678plus187HintPolicy1 = {
		new HintTest("table1_C5R1", "table1_C6R4", hints678plus187C6R4s1, 1),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"9",	INCORRECT, 1),
		new HintTest("table1_C6R4", "table1_C5R1", hints678plus187C5R1s1, 1)		
	};
	
	/* Hints biased by selection only: */
	private static MTTest[] Add678plus187HintPolicy2 = {
		new HintTest("table1_C1R1", "table1_C6R4", hints678plus187C6R4s1, 1),
		new AttemptTest("table1_C6R4",	"UpdateTable",	"9",	INCORRECT, 1),
		new HintTest("table1_C5R1", "table1_C5R1", hints678plus187C5R1s1, 1)
	};
	
	/* Hints biased by both: */
	private static MTTest[] Add678plus187HintPolicy3 = {
		new HintTest("table1_C5R1", "table1_C5R1", hints678plus187C5R1s1, 1),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"9",	INCORRECT, 1),
		new HintTest("table1_C6R4", "table1_C5R1", hints678plus187C5R1s1, 1)
	};

	/**
	 * Run the graph 678+187 with different hint policies.
	 */
	public void testAdd678plus187HintPolicy0() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        
        controller.getProblemModel().setHintPolicy(HintPolicyEnum.HINTS_UNBIASED);
        controller.goToStartState();
        sink.getLatestMsgs();
        runMTTests(Add678plus187HintPolicy0, "Add678plus187HintPolicy0");
	}
	
	public void testAdd678plus187HintPolicy1() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        
        controller.getProblemModel().setHintPolicy(HintPolicyEnum.HINTS_BIASED_BY_PRIOR_ERROR_ONLY);
        controller.goToStartState();
        sink.getLatestMsgs();
        runMTTests(Add678plus187HintPolicy1, "Add678plus187HintPolicy1");
	}
	
	public void testAdd678plus187HintPolicy2() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        
        controller.getProblemModel().setHintPolicy(HintPolicyEnum.HINTS_BIASED_BY_CURRENT_SELECTION_ONLY);
        controller.goToStartState();
        sink.getLatestMsgs();
        runMTTests(Add678plus187HintPolicy2, "Add678plus187HintPolicy2");
	}
	
	public void testAdd678plus187HintPolicy3() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        
        controller.getProblemModel().setHintPolicy(HintPolicyEnum.HINTS_BIASED_BY_ALL);
        controller.goToStartState();
        sink.getLatestMsgs();
        runMTTests(Add678plus187HintPolicy3, "Add678plus187HintPolicy3");
	}
	
	private static MTTest[] Add678plus187demo3Steps = {
		new AttemptTest("table1_C5R4",	"UpdateTable",	"5",	INCORRECT, 1),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, 1),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	INCORRECT, true), // already done
		new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, 1),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT, 1)
	};
	/**
	 * Run the graph 678plus187demo3Steps.
	 */
	public void testAdd678plus187demo3Steps() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187demo3Steps.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        
        checkStartStateMsgs("Add678plus187demo3Steps", 25, 3);

        runMTTests(Add678plus187demo3Steps, "Add678plus187demo3Steps");
	}

	/**
	 * Check that the number of start state messages matches the given argument,
	 * that the last message is StartStateEnd, that the 2nd to last is SendWidgetLock,
	 * and that there's no other StartStateEnd msg. 
	 * @param testName for diagnostics
	 * @param nMsgs expected number of msgs
	 * @param nDemoSteps number of msgs after the start state
	 */
	private void checkStartStateMsgs(String testName, int nMsgs, int nDemoSteps) {
        List<MessageObject> startStateMsgs = sink.getLatestMsgs();
		if (trace.getDebugCode("mtt")) {
			System.out.printf("checkStartStateMsgs(%s, %d) nStartStateMsgs %d; types:\n",
					testName, nMsgs, startStateMsgs.size());
			int i = 0;
			for (MessageObject mo : startStateMsgs)
				System.out.printf("   %2d %-22s %-22s %-22s %-22s\n", ++i, mo.getMessageType(),
						mo.getSelection(), mo.getAction(), mo.getInput());
		}
        int nStateGraph = 0, nStartStateEnd = 0;
        for (int i = 0; i < startStateMsgs.size(); ++i) {
        	String msgType = startStateMsgs.get(i).getMessageType();
        	trace.out("mtt", "  ["+i+"] "+msgType);
        	if ("StateGraph".equalsIgnoreCase(msgType))
        		nStateGraph++;
        	else if ("StartStateEnd".equalsIgnoreCase(msgType))
        		nStartStateEnd++;
        	if (i < 1)                // AuthorModeChange can occur before StateGraph
        		assertTrue(testName+": StateGraph not first",
        				"InterfaceReboot".equalsIgnoreCase(msgType) || "AuthorModeChange".equalsIgnoreCase(msgType) || "StateGraph".equalsIgnoreCase(msgType));
        	else if (i == startStateMsgs.size()-1)
        		assertEquals(testName+": StartStateEnd not last", "StartStateEnd", msgType);
//        	else if (i == startStateMsgs.size()-(1+(nDemoSteps*3)+1))  // Lock+n*(IntAction+Correct+AssocRules)+SSEnd
//        		assertEquals(testName+": SendWidgetLock not just before demo steps", "SendWidgetLock", msgType);
        	else
        		assertFalse(testName+": StartStateEnd at msg "+i+" of "+startStateMsgs.size(),
        				"StartStateEnd".equalsIgnoreCase(msgType));
        }
        assertEquals(testName+": no. of StateGraph msgs", 1, nStateGraph);
        assertEquals(testName+": no. of StartStateEnd msgs", 1, nStartStateEnd);
        assertEquals(testName+": nStartStateMsgs", nMsgs, startStateMsgs.size());
	}

	/**
	 * Inputs and modelTrace() results for the 678plus187replacementFormula tutor, main path.
	 * Note input expressions for sums are not mod 10.
	 */
	private static MTTest[] Add678plus187replacementFormulaTests = {
		new AttemptReplaceTest("table1_C6R4",	"UpdateTable",	"8+7", "15", null),
		new AttemptTest("table1_C5R1",	"UpdateTable", "1",	CORRECT),
		new AttemptReplaceTest("table1_C5R4",	"UpdateTable", "1+7+8",	"16", null),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT),
		new AttemptReplaceTest("table1_C4R4",	"UpdateTable",	"1+7", "8",	null),
		new TutorPerformedStepTest("done",	"ButtonPressed",	"-1",	CORRECT, 1)
	};
	/**
	 * Run the graph 678plus187replacementFormula.
	 */
	public void testAdd678plus187replacementFormula() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187replacementFormula.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "testAdd678plus187AltPath: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add678plus187replacementFormulaTests, "678plus187replacementFormula");
	}
	
	/**
	 * Inputs and modelTrace() results for the Add678plus187 tutor,
	 * Alternate path, with delayed feedback.
	 */
	private static MTTest[] Add678plus187DelayedFeedback = {
		new DelayedFeedbackTest("table1_C6R1",	"UpdateTable",	"1",	INCORRECT, 1),
		new DelayedFeedbackTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT, 1),
		new DelayedFeedbackTest("table1_C6R4",	"UpdateTable",	"6",	INCORRECT, 1),
		new DelayedFeedbackTest("done",        "ButtonPressed",	"-1",	INCORRECT, 1),
		new DelayedFeedbackTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT, 1),
		new DelayedFeedbackTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, 1),
		new DelayedFeedbackTest("table1_C5R4",	"UpdateTable",	"5",	INCORRECT, 1),
		new DelayedFeedbackTest("done",        "ButtonPressed",	"-1",	INCORRECT, 1),
		new DelayedFeedbackTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, 1),
		new DelayedFeedbackTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, 1),
		new DelayedFeedbackTest("done",        "ButtonPressed",	"-1",	CORRECT, 1)
	};
	/**
	 * Run the graph 678+187 with delayed feedback
	 */
	public void testAdd678plus187DelayedFeedback() {
		trace.addDebugCode("mtt");
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mtt","problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        controller.getProblemModel().setSuppressStudentFeedback(FeedbackEnum.DELAY_FEEDBACK);
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mtt", "testAdd678plus187DelayedFeedback: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add678plus187DelayedFeedback, "Add678plus187DelayedFeedback");
	}
	
	/**
	 * Inputs and modelTrace() results for the Add678plus187 tutor.
	 * Alternate path.
	 */
	private static MTTest[] Add678plus187TestsAltPath = {
		new AttemptTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT),
		new AttemptTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT),
		new TutorPerformedStepTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, 1),
		new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT),
		new TutorPerformedStepTest("done",	"ButtonPressed",	"-1",	CORRECT, 1)
	};
	/**
	 * Run the graph 678+187.
	 */
	public void testAdd678plus187AltPath() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "testAdd678plus187AltPath: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add678plus187TestsAltPath, "Add678plus187Alt");
	}
	
	/** Steps and expected results for the graph 678plus187formulaTPA.brd. */
	private static MTTest[] Add678plus187formulaTPATests = {
		new AttemptTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT, 1),
		new TutorPerformedStepTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT, 1),
		new AttemptTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, 1),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, 1),
		new AttemptTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, 1),
		new TutorPerformedStepTest("done",	"ButtonPressed",	"-1",	CORRECT, 1)
	};
	/**
	 * Run the graph 678+187.
	 */
	public void testAdd678plus187formulaTPA() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187formulaTPA.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "testAdd678plus187formulaTPA: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add678plus187formulaTPATests, "Add678plus187formulaTPA");
	}
	
	public void testRandomNumberGenerator() {
		System.out.println("testRandomNumberGenerator():");
		int n = 10;
		for (int i = 0; i < n; ++i)
			System.out.printf(" %.7f", Math.random());
		System.out.println();

		int seed = 42;
		for (int s = 0; s < 2; s++) {
			for (int k = 3; k > 0; k--) {
				if (s > 0)
					controller.setRandomSeed(seed);
				int[] ro = controller.randomOrder(n); 
				for (int i = 0; i < n; ++i)
					System.out.printf(" %2d", ro[i]);
				System.out.println();
			}
			if (s < 1)
				System.out.println("Now setting seed to "+seed+" each time...");
		}
	}

	/**
	 * Inputs and modelTrace() results for the Add678plus187 tutor.
	 * Preferred path, with hint and buggy result.
	 */
	private static MTTest[] CountTest = { 
		
		//For Unordered Mode::
/*		new AttemptTest("table1_C3R1","UpdateTable", "3", CORRECT),
		// FIXME create special actor-match failure result in Matcher
		new AttemptTest("table1_C2R1","UpdateTable", "2", INCORRECT), //WRONGUSER
		new AttemptTest("table1_C1R1","UpdateTable", "5", INCORRECT),	
		new AttemptTest("table1_C1R1","UpdateTable","1", CORRECT),				
		new TutorPerformedStepTest("table1_C2R1","UpdateTable","2",CORRECT)
 */		
		//For Partially Ordered Mode:
		new AttemptTest("table1_C3R2","UpdateTable", "7", INCORRECT, OUT_OF_ORDER),
		new AttemptTest("table1_C2R1","UpdateTable", "2", INCORRECT, OUT_OF_ORDER),
		new AttemptTest("table1_C1R1","UpdateTable","1", CORRECT),	
		new TutorPerformedStepTest("table1_C2R1","UpdateTable","2",CORRECT,1),
		new AttemptTest("table1_C4R1","UpdateTable", "4", CORRECT),
		new AttemptTest("table1_C1R2","UpdateTable", "5",CORRECT),
		new AttemptTest("table1_C2R2","UpdateTable", "6", INCORRECT, OUT_OF_ORDER),
		new AttemptTest("table1_C3R1","UpdateTable", "3", CORRECT),
		new TutorPerformedStepTest("table1_C2R2","UpdateTable","6",CORRECT,1),
		new AttemptTest("table1_C3R2","UpdateTable", "7", CORRECT)
	};

	/**
	 * Run the graph Count.brd.
	 */
	public void testCount() {
		String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/Count.brd";

		URL url = Utils.getURL(problemFileLocation, this);
		trace.out("mt" ,"problemFileLocation str = "
				+ problemFileLocation + ", url = " + url);
		controller.openBRFromURL(url.toString());

		List startStateMsgs = sink.getLatestMsgs();
		trace.out("mtt","test counting problem: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(CountTest, "CountTest");
	}
	
	/**
	 * Inputs and modelTrace() results for the 31d4p5d5 tutor.
	 */
	private static MTTest[] Add32d4plus4d5 = {
		new AttemptTest("operation", "UpdateComboBox", "+", CORRECT),
		new AttemptTest("R1tens", "UpdateTextArea", "3", CORRECT),
		new AttemptTest("R1ones", "UpdateTextArea", "2", CORRECT),
		new AttemptTest("R1dec", "UpdateTextArea", ".",	CORRECT),
		new AttemptTest("R1tenths", "UpdateTextArea", "4", CORRECT),
		new TutorPerformedStepTest("R2dec",	"UpdateTextArea", ".", CORRECT, 0)
	};

	/**
	 * Run the graph 32d4p4d5_unordered.brd.
	 */
	public void testAdd32d4plus4d5() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/32d4p4d5_unordered.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        assertNotNull("Add32d4plus4d5: null URL for "+Add32d4plus4d5, url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "Add32d4plus4d5: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(Add32d4plus4d5, "Add32d4plus4d5");
	}
	
	/**
	 * Inputs and modelTrace() results for the TPAnoHints tutor.
	 */
	private static MTTest[] TPAnoHints = {
		new AttemptTest("table1_C6R4", "UpdateTable", "5", CORRECT),
		new TutorPerformedStepTest("table1_C5R1", "UpdateTable", "1", CORRECT, 0),
		new AttemptTest("table1_C5R4", "UpdateTable", "6", CORRECT),
		new TutorPerformedStepTest("table1_C4R1", "UpdateTable", "1",	CORRECT, 0),
		new AttemptTest("table1_C4R4", "UpdateTable", "8", CORRECT)
	};

	/**
	 * Run the graph TPAnoHints.brd.
	 */
	public void testTPAnoHints() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/TPAnoHints.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        assertNotNull("TPAnoHints: null URL for "+TPAnoHints, url);
        controller.openBRFromURL(url.toString());
        List startStateMsgs = sink.getLatestMsgs();
        trace.out("mt", "AddTPAnoHints: nStartStateMsgs="+startStateMsgs.size());
		runMTTests(TPAnoHints, "TPAnoHints");
	}
	
	public void testCreateToolMessage() {
		Vector selection = new Vector(), action = new Vector(), input = new Vector();
		String transaction_id = pseudoTutorMessageHandler.enqueueToolActionToStudent(selection, action, input);
		trace.out("mt", "transaction_id "+transaction_id);
		assertNotNull("null transaction id", transaction_id);
		assertTrue("empty transaction id", transaction_id.trim().length() > 0);
	}

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
				System.out.println("\n___press Enter to run next test step___");
				try {
					promptRdr.readLine();
				} catch (IOException ioe) {}
			}
			long startTime = (new Date()).getTime();
			MTTest t = tests[currTest];
			if (t instanceof TutorPerformedStepTest) {
				sink.getLatestMsgs();
				continue;
			}
			trace.out("mtt", "\nRunning " + listName + " #" + currTest);
			List<MessageObject> msgs = 
					runProcessPseudoTutorInterfaceAction(t.selection, t.action, t.input, t.actor);
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
	protected List<MessageObject> runProcessPseudoTutorInterfaceAction(Vector<String> selection,
			Vector<String> action, Vector<String> input, String actor) {
		trace.out("mtt", "trying InterfaceAction" +selection+":"+action+":"+input+":"+actor);
		pseudoTutorMessageHandler.processPseudoTutorInterfaceAction(selection,
				action, input, actor);
		return sink.getLatestMsgs();
	}
}
