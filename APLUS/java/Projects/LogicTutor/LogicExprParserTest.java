/*
 * $Id: LogicExprParserTest.java 10858 2010-03-23 17:28:28Z sdemi $
 */
package LogicTutor;

import java.util.*;
import java.io.*;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test harness for LogicExprParser.
 * @author sewall
 */
public class LogicExprParserTest extends TestCase
								 implements LogicExprParser.Listener {

	/**
	 * Associate a test input string, a parse result, and a variable count.
	 */
	private static class SingleTest {
		String input = "";
		boolean result = false;
		int variableCount = 0;

		SingleTest(String input, boolean result, int variableCount) {
			this.input = input;
			this.result = result;
			this.variableCount = variableCount;
		}
	}

	/**
	 * List of expressions to parse and results.
	 */
	private static SingleTest[] singleTests = {
		new SingleTest("P->Q",		true,	2),
		new SingleTest("p->q",		false,	2),  // only upper case P,Q,R,S
		new SingleTest("P<->Q",		true,	2),
		new SingleTest("P & Q",		true,	2),
		new SingleTest("P | Q",		true,	2),
		new SingleTest("P ! Q",		false,	2),  // bad operator
		new SingleTest("(P|Q)->R",	true,	3),
		new SingleTest("(P | Q))",	false,	2),  // unbalanced parentheses
		new SingleTest("(P|Q)->R&S",true,	4)
	};

	/**
	 * Index in singleTests of current test.
	 */
	private int currentTest = -1;

	private LogicExprParser logicExprParser;

	/**
	 * Called when a parse() attempt has been completed.
	 *
	 * @param  evt object containing the results of the parse()
	 */
	public void parseCompleted(LogicExprParser.ParseEvent evt) {
		SingleTest t = singleTests[currentTest];
		assertEquals(t.input, evt.getInput());
		assertEquals(t.result, evt.getResult());
		if (evt.getResult())
			assertEquals(t.variableCount, evt.getVariableCount());
		else
			System.out.println("Parse failed:\n" + evt.getErrorMsg());
	}
	
	/**
	 * No command-line options.
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(LogicExprParserTest.suite());
	}

	public LogicExprParserTest(String arg) {
		super(arg);
	}

	public static Test suite() { 
		TestSuite suite= new TestSuite(); 
		suite.addTest(new LogicExprParserTest("parseList")); 
		return suite;
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		logicExprParser = LogicExprParser.instance();
		logicExprParser.addParseEventListener(this);
	}
	
	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test {@link PreferencesModel#saveToDisk()},
	 * {@link PreferencesModel#loadFromDisk()}.
	 */
	public void parseList() {
		for (currentTest = 0; currentTest < singleTests.length; currentTest++) {
			SingleTest t = singleTests[currentTest];
			logicExprParser.parse(t.input);
		}
	}
}
