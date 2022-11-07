/*
 * Copyright 2008-2012 Carnegie Mellon University
 */
package edu.cmu.pact.jess;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import jess.JessException;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;


/**
 * Test harness for JessModelTracing.
 * @author sewall
 */

public class JessModelTracingTest extends TestCase {

	static final int CORRECT = JessModelTracing.CORRECT;
	static final int NOMODEL = JessModelTracing.NOMODEL;
	static final int BUGGY = JessModelTracing.BUGGY;

	/** Common code for model-tracing tests. */
	private static abstract class MTTest {
		protected String selection = "";
		protected String action = "";
		protected String input = "-1";  // initialize for dummy value with hint button
	}

	/**
	 * Associate a hint request (selection) and a model trace result.
	 */
	private static class HintTest extends MTTest {
		String hintSelection = "";
		String[] hintMsgs = new String[0];

		HintTest(String selection, String hintSelection, String[] hintMsgs) {
			this.selection = selection;
			this.hintSelection = hintSelection.trim();
			this.hintMsgs = hintMsgs;
		}


		boolean checkResult(String listName, int currTest, String tSelection, Vector tMsgs) {
			trace.out("mt", "this.hintSelection " + hintSelection +
					  " ?= tSelection " + tSelection + ";");
			boolean result = true;
			String ts = (tSelection == null ? "" : tSelection.toLowerCase().trim());
			JessModelTracingTest.assertEquals(listName+" checkResult["+currTest+
					"] hintSelections differ", hintSelection.toLowerCase(), ts);
			trace.out("mt", "this.hintMsgs.length " + this.hintMsgs.length +
					  " ?= hintMsgs.size() " + tMsgs.size());
			for (int i = 0; i < hintMsgs.length && i < tMsgs.size(); ++i) {
				String expected = this.hintMsgs[i].trim().toLowerCase();
				String actual = ((String) tMsgs.get(i)).trim().toLowerCase();
				trace.out("mt", "msg[" + i + "]: expected, actual:\n  " +
						  expected + "\n  " + actual + "\n");
				JessModelTracingTest.assertEquals(listName+" checkResult["+currTest+
						","+i+"] mismatch", expected, actual);
					result = false;
			}
			assertEquals(listName+" checkResult["+currTest+"] lengths "+hintMsgs.length+" != "+tMsgs.size(),
					hintMsgs.length, tMsgs.size());
			return result;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("HintTest");
			sb.append(" [S=").append(selection);
			sb.append(", hS=").append(hintSelection);
			sb.append(", nH=").append(hintMsgs.length);
			sb.append(" {\n");
			for (int i = 0; i < hintMsgs.length; ++i)
				sb.append("  [").append(i).append("] ").append(hintMsgs[i]).append("\n");
			sb.append("}]");
			return sb.toString();
		}
	}

	/**
	 * Associate a test (selection, action, input) and a model trace result.
	 */
	private static class LispCheckTest extends MTTest {
		private final String[] msgs;
		int result = 0;
		public String tutorSelection, tutorAction, tutorInput;

		LispCheckTest(String selection, String action, String input,
					  int result) {
			this(selection, action, input, result, null);
		}

		LispCheckTest(String selection, String action, String input,
					  int result, String[] msgs) {
			this(selection, action, input, result, msgs, null, null, null);
		}

		LispCheckTest(String selection, String action, String input, int result, String[] msgs,
				String tutorSelection, String tutorAction, String tutorInput) {
			this.selection = selection;
			this.action = action;
			this.input = input;
			this.result = result;
			this.msgs = msgs;
			this.tutorSelection = tutorSelection;
			this.tutorAction = tutorAction;
			this.tutorInput = tutorInput;
		}

		boolean checkResult(String listName, int currTest, int result, Vector msgs) {
			trace.out("mt", "this.result " + this.result +
					  " ?= result " + result + ";");
			return checkResult(listName, currTest, result, msgs, null, null, null);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("LispCheckTest");
			sb.append(" [S=").append(selection);
			sb.append(", A=").append(action);
			sb.append(", I=").append(input);
			sb.append("] ").append(resultName(result));
			return sb.toString();
		}
		
		String resultName(int result) {
			switch(result) {
			case CORRECT: return "Correct";
			case NOMODEL: return "NoModel";
			case BUGGY:   return "Buggy  ";
			default:      return "unknown ("+result+")";
			}
		}

		boolean checkResult(String listName, int currTest, int result,
				Vector msgs, String tutorSelection, String tutorAction,
				String tutorInput) {
			assertEquals(listName+"["+currTest+"] "+this, this.result, result);
			trace.out("mt", listName+" expected msgs: "+this.msgs);
			trace.out("mt", listName+"   actual msgs: "+msgs);
			if (this.msgs != null) {      // if test's field null, skip msg check
				for (int i = 0; i < this.msgs.length && i < msgs.size(); ++i) {
					String expected = this.msgs[i].trim().toLowerCase();
					String actual = ((String) msgs.get(i)).trim().toLowerCase();
					assertEquals(listName+"["+currTest+","+i+"] mismatch", expected, actual);
				}
				assertEquals(listName+"["+currTest+"] lengths "+this.msgs.length+" != "+msgs.size(),
						this.msgs.length, msgs.size());
			}
			if(this.tutorSelection == null)
				return true;
			assertEquals(listName+"["+currTest+"] tutorSelection", this.tutorSelection, tutorSelection);
			assertEquals(listName+"["+currTest+"] tutorAction", this.tutorAction, tutorAction);
			assertEquals(listName+"["+currTest+"] tutorInput", this.tutorInput, tutorInput);
			return true;
		}
	}

	/**
	 * Index in XxxxTests[] of current test.
	 */
	private int currTest = -1;

	private MTRete r;
	private JessModelTracing jmt;
	private TextOutput textOutput = TextOutput.getNullOutput();
	private long setUpTime;

	private static boolean useBinaryJessFiles = false;
	
	/** Whether to prompt before each message to send. */
	private static boolean oneAtATime = false;
	
	/** Count the number of times the constructor is called. */
	private static int constructorCallCount = 0;

	private static boolean verbose = false;
	private static int doDisplayFrame = 0; // 0=>hide; 1=>show; 2=>show & keep
	private static JTextArea displayFrameTextArea;
	private static JFrame displayFrame = null;
	private static java.util.Set<String> testsChosen = null;

	/**
	 * Command-line syntax help.
	 */
	public static final String usageMsg =
		"Usage:\n" +
		"  jess.JessModelTracingTest [-h] [-b] [-o] [-d|D] [-p] [-f|F] [-v] [-w[watch]*] [test...]\n" +
		"where--\n" +
		"  -h  means print this help message;\n" +
		"  -b  means try to load Jess setup from binary files;\n" +
		"  -o  means run one step at a time (prompt between steps);\n" +
		"  -d  means turn on debug messages;\n" +
		"  -D  means turn on lots & lots of debug messages;\n" +
		"  -p  means pause on startup to permit profiler to connect (see below);\n" +
		"  -f  means display a GUI frame with Jess output; -F avoids closing the frame;\n" +
		"  -v  means turn on (verbose) console output;\n" +
		"  -w  with nothing following turns on (watch all); or turn on singly as--\n" +
		"      -wactivations\n" +
		"      -wcompilations\n" +
		"      -wfacts\n" +
		"      -wfocus\n" +
		"      -wrules;\n" +
		"  test... test(s) chosen to run; if none or \"all\", run all tests.\n" +
		"\n" +
		"To enable profiling with Java VisualVM on port 3333, set these properties,\n" +
		"on the JVM command line (does not work to set them from within the program):\n" +
		"  -Dcom.sun.management.jmxremote.port=3333\n" +
		"  -Dcom.sun.management.jmxremote.ssl=false\n" +
		"  -Dcom.sun.management.jmxremote.authenticate=false";

	/**
	 * Command-line options: see {@link #usageMsg}.
	 */
	public static void main(String[] args) {
		java.util.List watchOptions = null;
		int i = 0;
		for (i = 0; (i < args.length) && ('-' == args[i].charAt(0)); i++) {
			switch(args[i].charAt(1)) {
			case 'b':
				useBinaryJessFiles = true;
				break;
			case 'D':
				trace.addDebugCode("strat");
			case 'd':
				trace.addDebugCode("mt");
				trace.addDebugCode("mtt");
				verbose = true;
				break;
			case 'o':
				oneAtATime = true;
				break;
			case 'p':  // setting properties here doesn't work; must set on cmd line
				System.setProperty("com.sun.management.jmxremote.port", "3333");
				System.setProperty("com.sun.management.jmxremote.ssl", "false");
				System.setProperty("com.sun.management.jmxremote.authenticate", "false");
				System.out.printf("Start profiler and press <Enter> to continue...");
				try {System.in.read();} catch(Exception e) {}
				break;
			case 'F':
				doDisplayFrame = 2;
				break;
			case 'f':
				doDisplayFrame = 1;
				break;
			case 'h':
				System.err.println(usageMsg);
			    System.exit(1);
				break;            // not reached
			case 'v':
				trace.addDebugCode("mtt");
			    verbose = true;
			    break;
			case 'w':
				trace.out("mtt", "watchOptions " + args[i]);
				if (watchOptions == null)
					watchOptions = new LinkedList();
				if (args[i].length() > 2)
					watchOptions.add(args[i].substring(2));
			    break;
			default:
				System.err.println("Unknown option '" + args[i].charAt(1) +
								   "'. " + usageMsg);
			    System.exit(1);
			}
		}
		MTRete.setWatchOptions(watchOptions);
		
		if (i < args.length)
			testsChosen = new HashSet<String>();
		for ( ; i < args.length; i++)
			testsChosen.add(args[i].toLowerCase());
		
		junit.textui.TestRunner.run(JessModelTracingTest.suite());
	}

	/**
	 * No-argument constructor for JUnit. Equivalent to
	 * {@link #JessModelTracingTest(String, boolean) JessModelTracingTest(null,verbose)}
	 */
	public JessModelTracingTest() {
		this (null, verbose);
	}

	/**
	 * Default constructor for JUnit TestCase subclasses.
	 * @param  arg argument for superclass constructor
	 * @param  verbose whether to turn on console output
	 */
	public JessModelTracingTest(String arg, boolean verbose) {
		super(arg);
		if (verbose) {
		    if (doDisplayFrame > 0) {
		    	Document doc = new PlainDocument();
		        textOutput = TextOutput.getTextOutput(doc);
		        if (displayFrameTextArea == null)
		            displayFrameTextArea = new JTextArea(doc);
		    } else {
			    textOutput = TextOutput.getTextOutput(System.out);
		    }		   
			textOutput.println("constructorCallCount "+(++constructorCallCount));
		}
	}

	/**
	 * Suite to run tests in this class. Uses {@link #testsChosen} to select tests;
	 * if null, runs all tests in compile-defined order.
	 */
	public static Test suite() {
		if (testsChosen == null)
			return new TestSuite(JessModelTracingTest.class);

		TestSuite suite= new TestSuite(); 

		if (testsChosen.contains("all")) {
			suite.addTest(new JessModelTracingTest("testAdditionChainingMatchers", verbose)); 
			suite.addTest(new JessModelTracingTest("testSubtraction", verbose)); 
			suite.addTest(new JessModelTracingTest("testAdditionChaining2", verbose)); 
			suite.addTest(new JessModelTracingTest("testAdditionChaining", verbose)); 
			addLogicTutorTest(testsChosen, suite);
		}
		if (testsChosen.contains("additionchaining")) {
			suite.addTest(new JessModelTracingTest("testAdditionChaining", verbose)); 
			suite.addTest(new JessModelTracingTest("testAdditionChaining2", verbose)); 
			suite.addTest(new JessModelTracingTest("testAdditionChainingMatchers", verbose)); 
		}
		if (testsChosen.contains("additionchaining2")) {
			suite.addTest(new JessModelTracingTest("testAdditionChaining2", verbose)); 
		}
		if (testsChosen.contains("additionchainingMatchers")) {
			suite.addTest(new JessModelTracingTest("testAdditionChainingMatchers", verbose)); 
		}
		if (testsChosen.contains("subtraction")) {
			suite.addTest(new JessModelTracingTest("testSubtraction", verbose)); 
		}
		if (testsChosen.contains("logictutor")) {
			addLogicTutorTest(testsChosen, suite);
		}
		return suite;
	}

	/**
	 * Add the Logic Tutor to the test suite, with some extra diagnostics for the extra
	 * classes it needs.
	 * @param testsChosen
	 * @param suite
	 * @throws AssertionFailedError
	 */
	private static void addLogicTutorTest(java.util.Set<String> testsChosen, TestSuite suite) {
		try {
			ClassLoader cl = JessModelTracingTest.class.getClassLoader();
			cl.loadClass("LogicTutor.LogicExprParser");
			suite.addTest(new JessModelTracingTest("testLogicTutor", verbose));
		} catch (ClassNotFoundException e) {
			if (testsChosen == null) {
			    if (verbose) {
			        trace.out("*** Skipped test LogicTutor: " +
			        				   "LogicExprParser class not found");
			    }
			}
			else   // throw error if LogicTutor was requested explicitly
				throw new AssertionFailedError("Error loading LogicTutor: " + e.toString());
		} catch (NoClassDefFoundError er) {
			throw new AssertionFailedError("Need runcc.jar for LogicTutor: " + er.toString());
		}
	}

	/**
	 * Create the Rete and model tracer.
	 */
	private void createModelTracer() throws JessException {
		r = new MTRete();
		r.addOutputRouter("t", textOutput.getWriter());
		r.setWatchRouter("t");

		jmt = new JessModelTracing(r, null);
		jmt.setUseBreakPoints(false);
		jmt.setErrorArea(textOutput);
		MT.loadDefaultUserfunctions(this, r, jmt);
		r.setJmt(jmt);
	}
	
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		trace.addDebugCodes(System.getProperty("DebugCodes"));
		Utils.setRuntime(true);
		
		if(trace.getDebugCode("mtt"))
			trace.out("mtt", "JMTTest.setUp(): getName()="+getName());
		
		textOutput.println(">>> setUp()");
		super.setUp();
	
		if (doDisplayFrame > 0 && displayFrame == null) {
			displayFrame = new JFrame("JessModelTracingTest");
			displayFrame.getContentPane().add(displayFrameTextArea);
			displayFrame.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			        displayFrame.dispose();
				}
			});
			displayFrame.pack();
			displayFrame.show();
		}
		createModelTracer();
		setUpTime = System.currentTimeMillis();
	}
	
	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		if (trace.getDebugCode("timing"))
			System.out.printf("\nElapsed time for %s: %d ms\n\n", getName(), System.currentTimeMillis()-setUpTime);
		
		textOutput.println("<<< tearDown()");
		super.tearDown();
		if (displayFrame != null && doDisplayFrame <= 1) {
		    displayFrame.dispose();
		    displayFrame = null;
		}
	}

	/**
	 * Load the deftemplates, facts and rules files for a given test.
	 *
	 * @param  basename base part of filename for files to load; e.g.,
	 *             use "AdditionChaining" to load AdditionChaining.clp,
	 *             AdditionChaining.wme, AdditionChaining.clp
	 * @param  useBinary true means try to load entire state from binary file
	 */
	private void loadJessFiles(String[] filenames, boolean useBinary) {
		long startTime = (new Date()).getTime();
		try {
			String dir = getClass().getPackage().getName().replace('.', '/');
			String path = dir + "/";
			String bloadFile = path + filenames[0];
			r.clear();
			boolean results[] =
			    r.loadJessFiles((useBinary ? bloadFile : null),
			            		path + filenames[1],
			            		path + filenames[2],
			            		path + filenames[3],
			            		null);
			//trace.out("  Found ?  "+results[0]);
			if (!results[0])
				r.saveState(bloadFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionFailedError("Error loading Jess files: " + e);
		}
		textOutput.println("time(loadJessFiles) = " +
						   ((new Date()).getTime() - startTime));
	}		

	/**
	 * Load the deftemplates, facts and rules files for a given test.
	 *
	 * @param  basename base part of filename for files to load; e.g.,
	 *             use "AdditionChaining" to load AdditionChaining.clp,
	 *             AdditionChaining.wme, AdditionChaining.clp
	 * @param  useBinary true means try to load entire state from binary file
	 */
	private void loadJessFiles(String basename, boolean useBinary) {
		long startTime = (new Date()).getTime();
		try {
			String dir = getClass().getPackage().getName().replace('.', '/');
			String path = dir + "/" + basename;
			String bloadFile = path + ".bload";
			r.clear();
			boolean results[] =
			    r.loadJessFiles((useBinary ? bloadFile : null),
			            		path + ".clp",
			            		path + ".pr",
			            		path + ".wme",
			            		null);

			if (!results[0])
				r.saveState(bloadFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionFailedError("Error loading Jess files: " + e);
		}
		textOutput.println("time(loadJessFiles) = " +
						   ((new Date()).getTime() - startTime));
	}		

	private static final String[] addNoHintTestResult1 = {
		"Note that the addends in column 6 sum to a value greater than 9.",
		"Write the carry from column 6 to the next column.",
		"Write 1 at the top of column 5 ."
	};
	private static final String[] addNoHintTestResult2 = {
		"Work in the column on the right. This is the 'ones' column. ",
		"Add the two numbers in the 'ones' column. If the sum is greater than 9, you need to write "+
				"the 'ones' part in the 'ones' column and the 'tens' part in the next column to the left.",
		"The sum in the first column is  15 . Write  5  the 'ones' part in the highlighted cell. "+
				"Carry the 'tens' part into the next column. "
	};
	/**
	 * Inputs and modelTrace() results for the AdditionChainingTutor.
	 */
	private static MTTest[] AdditionNoChainingTests = {
		new HintTest("table1_C1R1", "table1_C6R4", addNoHintTestResult2),
		new LispCheckTest("table1_C5R1",	"UpdateTable",	"2",	NOMODEL, null,
//				"table1_C5R1",	"UpdateTable",	"1"),  // if silent hint not require hint texts
				"table1_C6R4",	"UpdateTable",	"5"),  // if silent hint does require hint texts
		new HintTest("table1_C5R1", "table1_C6R4", addNoHintTestResult2),
		new LispCheckTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT),
//		new LispCheckTest("table1_C5R1",	"UpdateTable",	"2",	BUGGY, additionChainingMsg0),
//		new LispCheckTest("table1_C4R4",	"UpdateTable",	"0",	BUGGY, additionChainingMsg0),
		new LispCheckTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT),
//		new LispCheckTest("table1_C5R1",	"UpdateTable",	"2",	NOMODEL),
//		new HintTest("table1_C5R1", "table1_C5R1", addHintTestResult2),
//		new HintTest("table1_C1R1", "table1_C5R4", addHintTestResult3),
		new LispCheckTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT),
		new LispCheckTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT),
		new LispCheckTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT)
	};

	/**
	 * Test the AdditionChaining tutor.
	 */
	public void testAdditionNoChaining() {
		loadJessFiles("AdditionNoChaining", useBinaryJessFiles);
		runMTTests(AdditionNoChainingTests, "AdditionNoChaining");
	}
	
	/**
	 * Expected hints for 1st hint request in {@link #AdditionChainingTests}.
	 */
	private static final String[] addHintTestResult1 = {
			"Start with the column on the right. This is the ones column ",
			"You need to add the two digits in this column. Adding 8 and 7 gives 15 .",
			"The sum that you have 15 is greater than 9.0 So you need to carry 10 of the 15 to the 5 column. And you need to write the rest of the sum at the bottom of the table1_Column6 column.",
			"You need to complete the work on the 6 column.",
			"Write carry from the 6 to the next column.",
			"Write 1 at the top of the 5 column from the right."
		};
	/**
	 * Expected hints for 2nd hint request in {@link #AdditionChainingTests}.
	 */
	private static final String[] addHintTestResult2 = {
			"You need to complete the work on the 6 column.",
			"Write carry from the 6 to the next column.",
			"Write 1 at the top of the 5 column from the right."
		};
	/**
	 * Expected hints for 3rd hint request in {@link #AdditionChainingTests}.
	 */
	private static final String[] addHintTestResult3 = {
			"Move on to the 5 column from the right.This is the table1_column5 column.",
			"You need to add the two digits in this column. Adding 7 and 8 gives 15 .",
			"There is a carry in to this column so you need to add the value " +
				"carried in. This gives 15 + 1 equals 16 .",
			"The sum that you have 16 is greater than 9.0 so you need to " +
				"carry 10 of the 16 to the 4 column. And you need to write " +
				"the rest of the sum at the bottom of the table1_column5 column.",
			"Write sum 6 at the bottom of the 5 column."
		};

	/** Feedback messages for {@link #AdditionChainingTests} and {@link #AdditionChaining2Tests}. */
	private static final String[] additionChainingMsg0 = {
		"Start with the column all the way to the right, the ones column. You've started in another column."
	};
	private static final String[] additionChainingMsg1 = {
		"Good job! You are in the 5 column from the right. This is the table1_column5 column."
	};
	private static final String[] additionChainingMsg2 = {
		"Good job! You are in the 4 column from the right. This is the table1_column4 column."
	};
	/**
	 * Inputs and modelTrace() results for the AdditionChainingTutor.
	 */
	private static MTTest[] AdditionChainingTests = {
			new HintTest("table1_C1R1", "table1_C5R1", addHintTestResult1),
			new HintTest("table1_C5R1", "table1_C5R1", addHintTestResult1),
			new LispCheckTest("table1_C5R1",	"UpdateTable",	"2",	BUGGY, additionChainingMsg0),
			new LispCheckTest("table1_C4R4",	"UpdateTable",	"0",	BUGGY, additionChainingMsg0),
			new LispCheckTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT),
			new LispCheckTest("table1_C5R1",	"UpdateTable",	"2",	NOMODEL),
			new HintTest("table1_C5R1", "table1_C5R1", addHintTestResult2),
			new LispCheckTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT),
			new HintTest("table1_C1R1", "table1_C5R4", addHintTestResult3),
			new LispCheckTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT, additionChainingMsg1),
			new LispCheckTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT),
			new LispCheckTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, additionChainingMsg2)
		};

	/** A shorter test sequence to check the other order of operations. */
	private static MTTest[] AdditionChaining2Tests = {
			new LispCheckTest("table1_C4R1",	"UpdateTable",	"2",	BUGGY, additionChainingMsg0),
			new LispCheckTest("table1_C6R4",	"UpdateTable",	"5",	CORRECT),
			new LispCheckTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT),
			new LispCheckTest("table1_C4R1",	"UpdateTable",	"2",	NOMODEL),
			new LispCheckTest("table1_C4R1",	"UpdateTable",	"1",	CORRECT, additionChainingMsg1),
			new LispCheckTest("table1_C5R4",	"UpdateTable",	"6",	CORRECT),
			new LispCheckTest("table1_C4R4",	"UpdateTable",	"8",	CORRECT, additionChainingMsg2)
		};

	/**
	 * Test the AdditionChaining tutor.
	 */
	public void testAdditionChaining() {
		loadJessFiles("AdditionChaining", useBinaryJessFiles);
		runMTTests(AdditionChainingTests, "AdditionChaining");
	}

	/**
	 * Test the AdditionChaining tutor some more.
	 */
	public void testAdditionChaining2() {
		loadJessFiles("AdditionChaining", useBinaryJessFiles);
		runMTTests(AdditionChaining2Tests, "AdditionChaining2");
	}
	
	/** A shorter test sequence to check the other order of operations. */
	private static MTTest[] AdditionChainingMatchersTests = {
			new LispCheckTest("table1_C4R1",	"UpdateTable",	"2",	BUGGY),
			new LispCheckTest("table1_C6R4",	"UpdateTable",	"any",	CORRECT), // write-sum has AnyMatcher
			new LispCheckTest("table1_C5R1",	"UpdateTable",	"1",	CORRECT), // write-sum has RangeMatcher
			new LispCheckTest("table1_C4R1",	"UpdateTable",	"3",	NOMODEL),
			new LispCheckTest("table1_C4R1",	"UpdateTable",	"2",	CORRECT),
			new LispCheckTest("table1_C5R4",	"UpdateTable",	"anyway",	CORRECT),
			new LispCheckTest("table1_C4R4",	"UpdateTable",	"anymore",	CORRECT),
			new LispCheckTest("tf1",			"UpdateTextField","DEF",	NOMODEL), // regex match abc.*
			new LispCheckTest("tf1",			"UpdateTextField","abcdef",	CORRECT),
			new LispCheckTest("tf2",			"UpdateTextField","Zorro",	CORRECT)  // wildcard match Z
		};

	/**
	 * Test the AdditionChainingMatchers tutor.
	 */
	private static final String[] AdditionChainingMatchersFiles = {
		"matchers/start.bload", "matchers/wmeTypes.clp",
		"matchers/productionRules.pr", "matchers/start.wme"
	};
	public void testAdditionChainingMatchers() {
		loadJessFiles(AdditionChainingMatchersFiles, useBinaryJessFiles);
		runMTTests(AdditionChainingMatchersTests, "AdditionChainingMatchers");
	}

	private static final String[] subHintTestResult2 = {
			"You need to now borrow from the carry that you wrote.",
			"Borrow from cell table0_c5r3 and write the borrow in cell above it.",
			"You need to write a borrow in column 5 .",
			"Write a borrow in cell table0_c5r2 .",
			"Write 9 in cell table0_c5r2 ."
		};
	private static MTTest[] SubtractionTests = {
		new LispCheckTest("Table0_C3R3",	"UpdateTable",	"6",	CORRECT),
		new LispCheckTest("Table0_C4R3",	"UpdateTable",	"10",	CORRECT),
		new LispCheckTest("Table0_C4R2",	"UpdateTable",	"8",	NOMODEL),
		new LispCheckTest("Table0_C4R2",	"UpdateTable",	"9",	CORRECT),
		new LispCheckTest("Table0_C5R3",	"UpdateTable",	"10",	CORRECT),
		new HintTest("Table0_C3R1", "Table0_C5R2", subHintTestResult2),
		new LispCheckTest("Table0_C5R6",	"UpdateTable",	"4",	NOMODEL),
		new LispCheckTest("Table0_C5R2",	"UpdateTable",	"9",	CORRECT),
		new LispCheckTest("Table0_C6R3",	"UpdateTable",	"13",	CORRECT),
		new LispCheckTest("Table0_C6R6",	"UpdateTable",	"9",	CORRECT),
		new LispCheckTest("Table0_C5R6",	"UpdateTable",	"4",	CORRECT),
		new LispCheckTest("Table0_C4R6",	"UpdateTable",	"2",	CORRECT),
		new LispCheckTest("Table0_C2R3",	"UpdateTable",	"3",	CORRECT),
		new LispCheckTest("Table0_C3R2",	"UpdateTable",	"16",	CORRECT),
		new LispCheckTest("Table0_C3R6",	"UpdateTable",	"8",	CORRECT),
		new LispCheckTest("Table0_C2R6",	"UpdateTable",	"2",	CORRECT)
	};

	/**
	 * Test the Subtraction tutor.
	 */
	public void testSubtraction() {
		loadJessFiles("Subtraction", useBinaryJessFiles);
		runMTTests(SubtractionTests, "Subtraction");
	}
	
	private static MTTest[] LinearEquationTests = {
		new LispCheckTest("hideButton",     "ButtonPressed",        "-1",                   CORRECT),
		new LispCheckTest("checkBoxGroup1", "UpdateCheckBox",       "Post-Explanations: true", CORRECT),
		new LispCheckTest("solveLeft1",     "UpdateTextField",      "6x",                   NOMODEL),
		new LispCheckTest("solveLeft1",     "UpdateTextField",      "5x",                   CORRECT),
		new LispCheckTest("solveRight1",    "UpdateTextField",      "2x+8-2",               CORRECT),
		new LispCheckTest("postExplOp1",    "UpdateComboBox",       "Multiplied both sides by", NOMODEL),
		new LispCheckTest("postExplOp1",    "UpdateComboBox",       "Subtracted",           CORRECT),
		new LispCheckTest("postExplNum1",   "UpdateTextField",      "2",                    CORRECT),
		new LispCheckTest("postExplSide1",  "UpdateComboBox",       "to/from both sides",   CORRECT),
		new LispCheckTest("solveLeft2",     "UpdateTextField",      "2x",                   NOMODEL),
		new LispCheckTest("solveLeft2",     "UpdateTextField",      "5x-2x",                NOMODEL),
		new LispCheckTest("solveLeft2",     "UpdateTextField",      "5x",                   CORRECT),
		new LispCheckTest("solveRight2",    "UpdateTextField",      "2x+6",                 CORRECT),
		new LispCheckTest("solveLeft3",     "UpdateTextField",      "5x-2x",                CORRECT),
		new LispCheckTest("solveRight3",    "UpdateTextField",      "2x",                   NOMODEL),
		new LispCheckTest("solveRight3",    "UpdateTextField",      "6",                    CORRECT),
		new LispCheckTest("postExplOp3",    "UpdateComboBox",       "Added",                CORRECT),
		new LispCheckTest("postExplNum3",   "UpdateTextField",      "2x",                   NOMODEL),
		new LispCheckTest("postExplNum3",   "UpdateTextField",      "-2x",                  CORRECT),
		new LispCheckTest("postExplSide3",  "UpdateComboBox",       "to/from both sides",   CORRECT),
		new LispCheckTest("solveLeft4",     "UpdateTextField",      "3x",                   CORRECT),
		new LispCheckTest("solveRight4",    "UpdateTextField",      "6",                    CORRECT),
		new LispCheckTest("solveLeft5",     "UpdateTextField",      "x",                    CORRECT),
		new LispCheckTest("solveRight5",    "UpdateTextField",      "3",                    NOMODEL),
		new LispCheckTest("solveRight5",    "UpdateTextField",      "2",                    CORRECT),
		new LispCheckTest("postExplOp5",    "UpdateComboBox",       "Multiplied both sides by", NOMODEL),
		new LispCheckTest("postExplOp5",    "UpdateComboBox",       "Divided both sides by", CORRECT),
		new LispCheckTest("postExplNum5",   "UpdateTextField",      "3",                    CORRECT),
		new LispCheckTest("postExplSide5",  "UpdateComboBox",       "to/from both sides",   CORRECT),
		new LispCheckTest("done",           "ButtonPressed",        "-1",                   CORRECT)
	};

	/**
	 * Test the LinearEquation tutor.
	 */
	public void testLinearEquation() {
		loadJessFiles("LinearEquation", useBinaryJessFiles);
		runMTTests(LinearEquationTests, "LinearEquation");
	}

	/**
	 * Run a list of MTTests.
	 * @param  tests array of MTTests to run
	 * @param  listName name of test list for log output
	 */
	public void runMTTests(MTTest[] tests, String listName) {
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
			MTTest mtt = tests[currTest];
			if (mtt instanceof LispCheckTest) {
				LispCheckTest t = (LispCheckTest) mtt;
				r.setGlobalSAI(t.selection, t.action, t.input);
				Vector msgs = new Vector();
				int result =
					jmt.modelTrace(false, t.selection, t.action, t.input, msgs);
				if(t.tutorSelection != null)
					t.checkResult(listName, currTest, result, msgs,
							jmt.getTutorSelection(), jmt.getTutorAction(), jmt.getTutorInput());
				else
					t.checkResult(listName, currTest, result, msgs);
			} else if (mtt instanceof HintTest) {
				HintTest t = (HintTest) mtt;
				Vector hintMsgs = new Vector();
				r.setGlobalSAI(t.selection, "ButtonPressed", "-1");
				int result =
					jmt.modelTrace(true, t.selection, "", "", hintMsgs);
				trace.out("mt", "HintTest(\"" + t.selection + "\") rtns " +
						  result + ", messages:\n  "+hintMsgs);
				String hintSelection = (jmt.getTutorSelection() != null ?
										jmt.getTutorSelection() : t.selection);
				t.checkResult(listName, currTest, hintSelection, hintMsgs);
			} else
				System.err.println("unsupported MTTest type " + mtt.getClass());
			
			trace.outNT("timing", "Passed " + listName + "[" + currTest + "] " +
			        mtt + " " + ((new Date()).getTime() - startTime) + " ms");
			jmt.getRete().clearState();
			System.gc();
		}
	}

	/**
	 * Inputs and modelTrace() results for the LogicTutor.
	 */
	private static final String[] logicHintTestResult1 = {
		"Start by typing a sentential logic expression in the highlighted text box.",
		"A valid expression in sentential logic is a combination of the following symbols: (1) single-character variables P, Q, R, S, (2) implication (->), (3) bidirectional implication (<->), (4) conjunction (&), (5) disjunction (|), (6) negation (~)"
	};
	private static MTTest[] LogicTutorTests = {
		new HintTest("commTextField1", "commTextField1", logicHintTestResult1),
		new LispCheckTest("commTextField1", "UpdateTextField", "P->Q|R<->S&", BUGGY),
		new LispCheckTest("commTextField1", "UpdateTextField", "P->Q|R<->S", CORRECT),
		new LispCheckTest("commTextField16", "UpdateTextField", "P", CORRECT),
		new LispCheckTest("commTextField15", "UpdateTextField", "Q", CORRECT),
		new LispCheckTest("commTextField9", "UpdateTextField", "R", CORRECT),
		new LispCheckTest("commTextField14", "UpdateTextField", "S", CORRECT),
		new LispCheckTest("commTextField13", "UpdateTextField", "P->Q", CORRECT),
		new LispCheckTest("commTextField8", "UpdateTextField", "R<->S", CORRECT),
		new LispCheckTest("commTextField7", "UpdateTextField", "((P->Q)|(R<->S))", CORRECT)
	};

	/**
	 * Test the LogicTutor tutor.
	 */
	public void testLogicTutor() {
		loadJessFiles("LogicTutor", useBinaryJessFiles);
		runMTTests(LogicTutorTests, "LogicTutor");
	}
}
