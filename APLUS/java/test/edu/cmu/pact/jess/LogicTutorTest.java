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
import edu.cmu.pact.Utilities.trace;

/*
 * Test harness for JessModelTracing, see JessModelTracingTest
 * @author sewall / pfeifer
 */

public class LogicTutorTest extends TestCase {
	
	static int CORRECT = JessModelTracing.CORRECT;
	static int NOMODEL = JessModelTracing.NOMODEL;
	static int BUGGY = JessModelTracing.BUGGY;
	static int FIREABLE = JessModelTracing.FIREABLE;
	
	private static boolean verbose = false;
	private static int doDisplayFrame = 0; // 0=>hide; 1=>show; 2=>show & keep
	private static JTextArea displayFrameTextArea;
	private static JFrame displayFrame = null;
	private static java.util.Set testsChosen = null;
	
	private int currTest = -1;

	private MTRete r;
	private JessModelTracing jmt;
	private TextOutput textOutput = TextOutput.getTextOutput(System.out);
	
	/** Timestamp of test start time. */
	private long setUpTime;

	private static boolean useBinaryJessFiles = false;
	
	/** Whether to prompt before each message to send. */
	private static boolean oneAtATime = false;
	
	/** Count the number of times the constructor is called. */
	private static int constructorCallCount = 0;
	
	/** Marker interface for model-tracing tests. */
	static interface MTTest {}

	/**
	 * Associate a hint request (selection) and a model trace result.
	 */
	private static class HintTest implements MTTest {
		String selection = "";
		String hintSelection = "";
		String[] hintMsgs = new String[0];

		HintTest(String selection, String hintSelection, String[] hintMsgs) {
			this.selection = selection;
			this.hintSelection = hintSelection.trim();
			this.hintMsgs = hintMsgs;
		}


		boolean checkResult(int currTest, String tSelection, Vector tMsgs) {
			trace.out("mt", "this.hintSelection " + hintSelection +
					  " ?= tSelection " + tSelection + ";");
			boolean result = true;
			String ts = (tSelection == null ? "" : tSelection.toLowerCase().trim());
			LogicTutorTest.assertEquals("checkResult["+currTest+
					"] hintSelections differ", hintSelection.toLowerCase(), ts);
			trace.out("mt", "this.hintMsgs.length " + this.hintMsgs.length +
					  " ?= hintMsgs.size() " + tMsgs.size());
			for (int i = 0; i < hintMsgs.length && i < tMsgs.size(); ++i) {
				String expected = this.hintMsgs[i].trim().toLowerCase();
				String actual = ((String) tMsgs.get(i)).trim().toLowerCase();
				trace.out("mt", "msg[" + i + "]: expected, actual:\n  " +
						  expected + "\n  " + actual + "\n");
				LogicTutorTest.assertEquals("checkResult["+currTest+
						","+i+"] mismatch", expected, actual);
					result = false;
			}
			assertEquals("checkResult["+currTest+"] lengths "+hintMsgs.length+" != "+tMsgs.size(),
					hintMsgs.length, tMsgs.size());
			return result;
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
	private static class LispCheckTest implements MTTest {
		String selection = "";
		String action = "";
		String input = "";
		private final String[] msgs;
		int result = 0;

		LispCheckTest(String selection, String action, String input,
					  int result) {
			this(selection, action, input, result, null);
		}

		LispCheckTest(String selection, String action, String input,
					  int result, String[] msgs) {
			this.selection = selection;
			this.action = action;
			this.input = input;
			this.result = result;
			this.msgs = msgs;
		}
		
		

		boolean checkResult(String listName, int currTest, int result, Vector msgs) {
			trace.out("mt", "this.result " + this.result +
					  " ?= result " + result + ";");
			assertEquals(listName+"["+currTest+"] "+this, this.result, result);
			trace.out("mt", listName+" expected msgs: "+this.msgs);
			trace.out("mt", listName+"   actual msgs: "+msgs);
			if (this.msgs == null)       // if test's field null, skip msg check
				return true;
			for (int i = 0; i < this.msgs.length && i < msgs.size(); ++i) {
				String expected = this.msgs[i].trim().toLowerCase();
				String actual = ((String) msgs.get(i)).trim().toLowerCase();
				assertEquals(listName+"["+currTest+","+i+"] mismatch", expected, actual);
			}
			assertEquals(listName+"["+currTest+"] lengths "+this.msgs.length+" != "+msgs.size(),
					this.msgs.length, msgs.size());
			return true;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("LispCheckTest");
			sb.append("[S=").append(selection);
			sb.append(",A=").append(action);
			sb.append(",I=").append(input);
			sb.append("]r=").append(result);
			return sb.toString();
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
		
		textOutput.println(">>> setUp()");
		super.setUp();
	
		if (doDisplayFrame > 0 && displayFrame == null) {
			displayFrame = new JFrame("LogicTutorTest");
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
		r.getJmt().dispose();
		r = null;
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

			if (!results[0])
				r.saveState(bloadFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionFailedError("Error loading Jess files: " + e);
		}
		textOutput.println("time(loadJessFiles) = " +
						   ((new Date()).getTime() - startTime));
	}
	
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
				t.checkResult(listName, currTest, result, msgs);
			} else if (mtt instanceof HintTest) {
				HintTest t = (HintTest) mtt;
				Vector hintMsgs = new Vector();
				r.setGlobalSAI(t.selection, "ButtonPressed", "-1");
				int result =
					jmt.modelTrace(true, t.selection, "", "", hintMsgs);
				trace.out("mt", "HintTest(\"" + t.selection + "\") rtns " +
						  result);
				String hintSelection = (jmt.getTutorSelection() != null ?
										jmt.getTutorSelection() : t.selection);
				t.checkResult(currTest, hintSelection, hintMsgs);
			} else
				System.err.println("unsupported MTTest type " + mtt.getClass());
			
			trace.out("mtt", "\nPassed " + listName + " #" + currTest +
			        " " + ((new Date()).getTime() - startTime) + " ms");
			jmt.getRete().clearState();
			System.gc();
		}
	}
	private static final String[] HintTestResult2var = {
		"elements of the sentential logic expression must be inserted as headings of the truth table.",
	    "Decompose the expression into elements and insert as headings of the empty columns in the truth table." +
	    " for instance, the expression  p  ->  q  &  p  could be decomposed as a series of individual" +
	    " tokens -  p , ->,  q , &,  p - or as a left-to-right parse tree -  p  ->  q , (  p  ->  q ) &  P",
	    "Insert the element ' (P|Q) ' as a heading in the highlighted cell. This is the start of a parse tree decomposition."
	};
	
	private static MTTest[] LogicTutorTest2var= {
		new LispCheckTest("commTextField1",	"UpdateTextField",	"p|q",	CORRECT),
		new LispCheckTest("commTextField9",	"UpdateTextField",	"p",	CORRECT),
		new LispCheckTest("commTextField14",	"UpdateTextField",	"q",	CORRECT),	
		new HintTest("commTextField13", "commTextField13", HintTestResult2var),
		new LispCheckTest("commTextField13",	"UpdateTextField",	"p|q",	CORRECT),
		new LispCheckTest("commTable1_C1R1",	"UpdateTable",		"T",	CORRECT),
		new LispCheckTest("commTable1_C1R2",	"UpdateTable",		"T",	CORRECT),
		new LispCheckTest("commTable1_C1R3",	"UpdateTable",		"T",	CORRECT),
		new LispCheckTest("commTable1_C1R4",	"UpdateTable",		"F",	CORRECT),
		new LispCheckTest("commRadioButton15","UpdateRadioButton","true",	CORRECT)
	};
	
	private static MTTest[] LogicTutorTest3var= {
		new LispCheckTest("commTextField1", 	"UpdateTextField",	"(p&q)|(q&~r)", CORRECT),
		new LispCheckTest("commTextField15",	"UpdateTextField", 	"p",			CORRECT),
		new LispCheckTest("commTextField9",	"UpdateTextField",	"q",			CORRECT),
		new LispCheckTest("commTextField14",	"UpdateTextField",	"r",			CORRECT),
		new LispCheckTest("commTextField13",	"UpdateTextField",	"p&q",			CORRECT),
		new LispCheckTest("commTextField8",	"UpdateTextField",	"~r",			CORRECT),
		new LispCheckTest("commTextField7",	"UpdateTextField",	"q&~r",			CORRECT),
		new LispCheckTest("commTextField6",	"UpdateTextField",	"(p&q)|(q&~r)",	CORRECT),
		new LispCheckTest("commTable1_C1R1",	"UpdateTable",		"T",			CORRECT),
		new LispCheckTest("commRadioButton14","UpdateRadioButton","true",			CORRECT),
		new LispCheckTest("commTable1_C1R2",	"UpdateTable",		"F",			BUGGY)	
	};
	
	private static MTTest[] LogicTutorTestSimple= {
		new LispCheckTest("commTextField1", "UpdateTextField", "p&q", CORRECT)
	};
	
	private static final String[] HintTestResultFA = {
		"You must find a numerator so that the new fraction is equivalent to the original fraction.",
		"you multiplied the denominator by  5 . so you need to multiply the numerator by the same amount.",
		"the new numerator is equal to  5  times  1 ."
	};
	
	private static final String[] HintTestResultFADone = {
       "Is there anything else to do? Even though unreduceddenom was set incorrectly to 15+15=30;", 
       "If the greatest common divisor of a numerator and a denominator is 1, then the fraction cannot be further reduced.",
	   "The greatest common divisor of  11  and  15  is 1.",
       "You are done. Press the done button."
	};
	
	private static MTTest[] FractionAdditionTest= {
		new LispCheckTest("convertDenom2", 	"UpdateTextField", "15", CORRECT),
		new LispCheckTest("convertDemon1", 	"UpdateTextField", "13", NOMODEL),
		new LispCheckTest("convertDenom1", 	"UpdateTextField", "15", CORRECT),
		new HintTest("convertNum1", "convertNum1", HintTestResultFA),
		new LispCheckTest("convertNum1",	"UpdateTextField", "5",  CORRECT),
		new LispCheckTest("convertNum2",	"UpdateTextField", "6",  CORRECT),
		new LispCheckTest("unreducedDenom", "UpdateTextField", "30", FIREABLE),
		new LispCheckTest("unreducedDenom", "UpdateTextField", "15", CORRECT),
		new LispCheckTest("unreducedNum",	"UpdateTextField", "11", CORRECT),
		new LispCheckTest("finalDenom",		"UpdateTextField", "15", NOMODEL),
		new LispCheckTest("finalNum",		"UpdateTextField", "11", NOMODEL),
		new HintTest("finalNum", "done", HintTestResultFADone),
		new LispCheckTest("done",			"ButtonPressed",   "-1", CORRECT)
	};
	
	public LogicTutorTest() {this(null,verbose);}
	
	public LogicTutorTest(String arg, boolean verbose) 
	{
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
	
	public void testLogicTutorSimple(){
		loadJessFiles("LogicTutor", useBinaryJessFiles);
		runMTTests(LogicTutorTestSimple, "LogicTutor");
	}
	
	public void testLogicTutor2var(){
		loadJessFiles("LogicTutor", useBinaryJessFiles);
		runMTTests(LogicTutorTest2var, "LogicTutor");
	}
	
	/*
	 * Note: this test is somewhat slow
	 */
	public void testLogicTutor3var(){
		loadJessFiles("LogicTutor", useBinaryJessFiles);
		runMTTests(LogicTutorTest3var, "LogicTutor");
	}
	
	public void testFractionAddition(){
		loadJessFiles("FractionAddition", useBinaryJessFiles);
		runMTTests(FractionAdditionTest, "FractionAddition");
	}
	
	/*
	 * This method tests all methods of the type "public void test"+x+"()"
	 * where x is some String
	 */
	public static Test suite() {
		return new TestSuite (LogicTutorTest.class);
	}
	
	
}
