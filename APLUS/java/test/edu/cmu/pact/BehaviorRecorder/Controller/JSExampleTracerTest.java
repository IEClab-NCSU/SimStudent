/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * @author sewall
 *
 */
public class JSExampleTracerTest extends PseudoTutorMessageHandlerTest {

	/** Address of example tracer for use with JUnit and {@link #main(String[])}. */
	private static String url = JSExampleTracer.DEFAULT_URL;
	
	/** Whether to start the child process; default is true. */
	private static boolean startChild = true;

	/** Communications interface to example tracer. */
	private JSExampleTracer jsET;
	
	/**
	 * Suite to run some or all tests in this class.  Uses
	 * {@link #testsChosen} to choose the test(s) to run: if null, runs default test
	 * testAddFractions14plus16OneIncorrect. If it contains "all", runs all tests.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		if (testsChosen == null)
			suite.addTest(new JSExampleTracerTest("testAddFractions14plus16OneIncorrect", url)); 
		else if(testsChosen.contains("all") || testsChosen.contains("ALL"))
			suite = new TestSuite(JSExampleTracerTest.class);
		else {
			for(String test : testsChosen)
				suite.addTest(new JSExampleTracerTest(test, url)); 
		}
		return suite;
	}	

	/**
	 * Print a usage message and exit. Never returns.
	 * @param errMsg optional error message
	 * @param e optional exception; will print stack backtrace
	 */
	private static void usageExit(String errMsg, Throwable e) {
		if(e != null)
			e.printStackTrace(System.err);
		System.err.printf("\n%s%sUsage:\n"+
				"    java -cp ... %s [-h] [-n] [-u url]\n"+
				"where--\n"+
				"    -h  means print this help message;\n"+
				"    -n  prevents automatic startup of nodejs: you must start nodejs manually;\n"+
				"    url is the tutor engine address; default %s.\n",
				(errMsg == null ? "" : errMsg),
				(errMsg == null ? "" : (e == null ? ". " : ": "+e+".")),
				JSExampleTracerTest.class.getName(),
				JSExampleTracer.DEFAULT_URL);
		System.exit(2);
	}

	/**
	 * Launcher for interactive use.
	 * @param args see {@link #usageExit(String, Throwable)} for command-line args
	 */
	public static void main(String[] args) {
		int i = 0;
		try {
			for(;  i < args.length && '-' == args[i].charAt(0); ++i) {
				char c = args[i].charAt(1);
				switch(Character.toLowerCase(c)) {
				case 'h':
				    usageExit(null, null);
					break;            // not reached
				case 'n':
					startChild = false;
					break;
				case 'u':
					url = args[++i];
					break;
				default:
					throw new IllegalArgumentException("Command-line option '-"+c+"' undefined");
				}
			}
		} catch(Exception e) {
			usageExit("Error processing argument["+i+"]", e);
		}
		if (i < args.length)
			testsChosen = new HashSet<String>();
		for ( ; i < args.length; i++)
			testsChosen.add(args[i]);

		junit.textui.TestRunner.run(JSExampleTracerTest.suite());
	}

	/**
	 * No-argument constructor for JUnit. Equivalent to
	 * {@link #JSExampleTracerTest(String, String) JessModelTracingTest(null, null)}
	 */
	public JSExampleTracerTest() {
		this(null, url);
	}
	
	/**
	 * Regular constructor.
	 * @param name test name
	 * @param url address for {@link JSExampleTracer#JSExampleTracer(String)}
	 */
	public JSExampleTracerTest(String name, String url) {
		super(name);
	}

	/**
	 * No-op to avoid processing in superclass.
	 * @see edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest#setUp()
	 */
	protected void setUp() {
		jsET = new JSExampleTracer(url, startChild); 
		sink = new SinkToolProxy(null); 		
	}

	/**
	 * Stops the child process with {@link JSExampleTracer#killChild()}. 
	 * @see edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest#tearDown()
	 */
	protected void tearDown() {
		jsET.killChild();
	}

	/**
	 * Create a {@value MsgType#SET_PREFERENCES} message to load the given .brd file.
	 * @param problemFileLocation
	 * @see edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest#openBRD(java.lang.String)
	 */
	protected void openBRD(String problemFileLocation) {
		try {
			List<MessageObject> startStateMsgs = jsET.openBRD(problemFileLocation);
			for(MessageObject startStateMsg : startStateMsgs)
				sink.handleMessage(startStateMsg);        
		} catch(Exception ioe) {
			fail("Error reading BRD file "+problemFileLocation+": "+ioe);
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
		MessageObject mo = PseudoTutorMessageBuilder.buildInterfaceActionMsg(selection, action, input, null);
		mo.setTransactionId(MessageObject.makeTransactionId());
		List<MessageObject> responses = jsET.getExampleTracerResponses(mo);
		for(MessageObject response : responses)
			sink.handleMessage(response);
		return sink.getLatestMsgs();
	}
}
