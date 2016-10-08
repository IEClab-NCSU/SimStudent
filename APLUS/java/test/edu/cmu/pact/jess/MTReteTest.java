/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.util.ArrayList;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Value;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.trace;


/**
 * Tests for MTRete.
 */
public class MTReteTest extends TestCase {
	
	/** Whether debug output is on. */
	private static boolean debugOn = false;

	/** Rete to use. */
	private MTRete r = null;
	
	/** Sink for Jess console messages. */
	private TextOutput consoleOutput = TextOutput.getTextOutput(System.out);
	
	/** Sink for Jess console messages. */
	private TextOutput nullOutput = TextOutput.getNullOutput();
	
	/** Number of facts asserted. */
	private int nFacts = 0;
	
	/** Return the suite of all tests. */
    public static Test suite() {
        // Any void method that starts with "test" 
        // will be run automatically using this construct
        return new TestSuite(MTReteTest.class);
    }
    
	/** Help message with command-line arguments. */
	public static final String usageMsg =
		"Usage:\n" +
		"  MTReteTest [-d] [testName ...]\n" +
		"where--\n" +
		"  -d means turn on debugging;\n" +
		"  testName ... is one or more tests to run; default is all tests.\n";

	/**
	 * Command-line usage: see {@link #usageMsg}.
	 */
	public static void main(String[] args) {
		int i;

		for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
			char opt = args[i].charAt(1);
			switch (opt) {
			case 'd':
				debugOn = true;
				break;
			default:
				System.err.println("Undefined command-line option " + opt +
								   ". " + usageMsg);
				System.exit(1);
			}
		}
		if (debugOn)
			trace.addDebugCode("mt");
		if (i >= args.length)
			junit.textui.TestRunner.run(MTReteTest.suite());
		else{
			for (; i < args.length; ++i) {
				Test test = new MTReteTest(args[i]);
				junit.textui.TestRunner.run(test);
			}
		}
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		String script = 
			"(deftemplate noname (slot value))\n"+
			"(deftemplate button (slot name))\n"+
			"(deftemplate textField (slot name) (slot value))\n"+
			"(deftemplate comboBox (slot name) (slot value) (multislot values))\n"+
			"(bind ?f1 (assert (notemplate ordered fact)))\n"+
			"(bind ?f2 (assert (noname (value noname-value))))\n"+
			"(bind ?f3 (assert (button (name done))))\n"+
			"(bind ?f4 (assert (button (name hint))))\n"+
			"(bind ?f5 (assert (textField (name tf1))))\n"+
			"(bind ?f6 (assert (textField (name tf2) (value 21))))\n"+
			"(bind ?f7 (assert (comboBox (name cb1))))\n"+
			"(bind ?f8 (assert (comboBox (name cb2) (values one two three))))\n"+
			"(bind ?f9 (assert (comboBox (name cb3) (value vee) (values aay bee cee))))\n"+
			"(facts)\n";
		r = new MTRete();
		if (debugOn) {
			r.setTextOutput(consoleOutput);
			r.addOutputRouter(r.DEFAULT_IO_ROUTER, consoleOutput.getWriter());
			r.setWatchRouter(r.DEFAULT_IO_ROUTER);
		} else {
			r.setTextOutput(nullOutput);
			r.addOutputRouter(r.DEFAULT_IO_ROUTER, nullOutput.getWriter());
		}
		try {
			r.executeCommand(script);
			nFacts = 9;
		} catch (JessException je) {
			fail(je.toString());
			if (debugOn)
				je.printStackTrace();
			return;
		}
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		r = null;
	}

	/**
	 * Constructor for MTReteTest.
	 * @param arg0
	 */
	public MTReteTest(String arg0) {
		super(arg0);
	}

	public MTReteTest() {
	}

	/**
	 * Test for ArrayList getFacts()
	 */
	public final void testGetFacts() {
		ArrayList facts = r.getFacts();
		assertEquals(facts.size(), nFacts);
	}

	/**
	 * Test for getFactByName()
	 */
	public final void testGetFactByName() {
		String name = "";
		Fact result;
		try {
			result = r.getFactByName(name = "not there");
			assertNull(result);
			result = r.getFactByName(name = "tf1");
			assertEquals(name, result.getSlotValue("name").toString());
			result = r.getFactByName(name = "done");
			assertEquals(name, result.getSlotValue("name").toString());
			result = r.getFactByName(name = "cb1");
			assertEquals(name, result.getSlotValue("name").toString());
			result = r.getFactByName(name = "cb3");
			assertEquals("vee", result.getSlotValue("value").toString());
		} catch (JessException je) {
			fail("Failure on getFactByName(\""+name+"\"): "+je.toString());
			if (debugOn)
				je.printStackTrace();
		}
	}
	
	/**
	 * Test for setSAIDirectly()
	 */
	public final void testSetSAIDirectly() {
		String selection = "";
		String action = "";
		String input = "";
		Fact f = null;
		try {
			assertFalse(r.setSAIDirectly(selection="notThere", action="updateComboBox", input="five"));

			assertTrue(r.setSAIDirectly(selection="cb2", action="updateComboBox", input="multiword string"));
			f = r.getFactByName(selection);
			assertEquals(input, f.getSlotValue("value").stringValue(null));

			assertTrue(r.setSAIDirectly(selection="cb2", action="updateComboBox", input="(exprParens)"));
			f = r.getFactByName(selection);
			assertEquals(input, f.getSlotValue("value").stringValue(null));

			assertTrue(r.setSAIDirectly(selection="cb2", action="updateComboBox", input="(a)/(4b*c)-2"));
			f = r.getFactByName(selection);
			assertEquals(input, f.getSlotValue("value").stringValue(null));

			assertTrue(r.setSAIDirectly(selection="cb3", action="updateComboBox", input="six"));
			f = r.getFactByName(selection);
			assertEquals(input, f.getSlotValue("value").symbolValue(null));

			assertTrue(r.setSAIDirectly(selection="cb3", action="updateComboBox", input="*6six"));
			f = r.getFactByName(selection);
			assertEquals(input, f.getSlotValue("value").stringValue(null));

			assertTrue(r.setSAIDirectly(selection="tf2", action="updateTextField", input=null));
			f = r.getFactByName(selection);
			assertEquals("nil", f.getSlotValue("value").symbolValue(null));

			assertTrue(r.setSAIDirectly(selection="tf1", action="updateTextField", input="42"));
			f = r.getFactByName(selection);
			assertEquals(Integer.parseInt(input), f.getSlotValue("value").intValue(null));

			assertTrue(r.setSAIDirectly(selection="tf1", action="updateTextField", input="-2.0e4"));
			f = r.getFactByName(selection);
			assertEquals(Double.parseDouble(input), f.getSlotValue("value").floatValue(null), 0);
		} catch (JessException je) {
			fail("Failure on testSetSAIDirectly(\""+selection+"\"): "+je.toString());
			if (debugOn)
				je.printStackTrace();
		}
	}
	
	/**
	 * Test for {@link MTRete#stringToValue(String s, Context ctx)}.
	 */
	public final void testStringToValue() {
		String s = null;
		Value v = null;
		try {
			int i = 21;
			s = String.valueOf(i);
			v = new Value(i, RU.INTEGER);
			assertTrue(v.equals(MTRete.stringToValue(s, null)));

			double d = -1.4e4;
			s = String.valueOf(d);
			v = new Value(d, RU.FLOAT);
			assertTrue(v.equals(MTRete.stringToValue(s, null)));
			
			s = " string with whitespace";
			v = new Value(s, RU.STRING);
			assertTrue(v.equals(MTRete.stringToValue(s, null)));
			
			s = "(expression w/ space & parentheses)";
			v = new Value(s, RU.STRING);
			assertTrue(v.equals(MTRete.stringToValue(s, null)));
			
			s = "(expressionWithParentheses)";
			v = new Value(s, RU.STRING);
			assertTrue(v.equals(MTRete.stringToValue(s, null)));
			
			s = "(all+the-arith/ops*+exp)^^2";
			v = new Value(s, RU.STRING);
			assertTrue(v.equals(MTRete.stringToValue(s, null)));
		} catch (JessException je) {
			fail("Failure on testStringToValue\""+s+"\"): "+je.toString());
			if (debugOn)
				je.printStackTrace();
		}
	}
	
	/**
	 * Test the function that determines whether a rule is buggy or not.
	 */
	public final void testIsBuggyRuleName() {
		String s;
		assertTrue(s="buggy", MTRete.isBuggyRuleName(s));
		assertTrue(s="BUGGY", MTRete.isBuggyRuleName(s));
		assertTrue(s="Buggy", MTRete.isBuggyRuleName(s));
		assertTrue(s="bUgGy", MTRete.isBuggyRuleName(s));
		assertTrue(s="BUG-RULE", MTRete.isBuggyRuleName(s));
		assertTrue(s="bug-rule", MTRete.isBuggyRuleName(s));
		assertTrue(s="Bug-rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="Bugler-sounds-charge", MTRete.isBuggyRuleName(s));
		assertFalse(s="Bugs-are-my-biz", MTRete.isBuggyRuleName(s));
		assertTrue(s="Bug is this rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="some-other-rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="ANOTHER-rule", MTRete.isBuggyRuleName(s));

		assertTrue(s="MAIN::buggy", MTRete.isBuggyRuleName(s));
		assertTrue(s="MAIN::BUGGY", MTRete.isBuggyRuleName(s));
		assertTrue(s="MAIN::Buggy", MTRete.isBuggyRuleName(s));
		assertTrue(s="MAIN::bUgGy", MTRete.isBuggyRuleName(s));
		assertTrue(s="MAIN::BUG-RULE", MTRete.isBuggyRuleName(s));
		assertTrue(s="MAIN::bug-rule", MTRete.isBuggyRuleName(s));
		assertTrue(s="MAIN::Bug-rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="MAIN::Bugler-sounds-charge", MTRete.isBuggyRuleName(s));
		assertFalse(s="MAIN::Bugs-are-my-biz", MTRete.isBuggyRuleName(s));
		assertTrue(s="MAIN::Bug is this rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="MAIN::some-other-rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="MAIN::ANOTHER-rule", MTRete.isBuggyRuleName(s));

		assertTrue(s="OTHER-MODULE::buggy", MTRete.isBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::BUGGY", MTRete.isBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Buggy", MTRete.isBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::bUgGy", MTRete.isBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::BUG-RULE", MTRete.isBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::bug-rule", MTRete.isBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Bug-rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::Bugler-sounds-charge", MTRete.isBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::Bugs-are-my-biz", MTRete.isBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Bug is this rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::some-other-rule", MTRete.isBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::ANOTHER-rule", MTRete.isBuggyRuleName(s));
	}
	
	/**
	 * Test the function that determines whether a rule name is for a fireable-buggy rule.
	 */
	public final void testIsFireableBuggyRuleName() {
		String s;
		assertTrue(s="fireable-buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIREABLE-BUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="Fireable-Buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FiReAbLe-bUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIREABLE-BUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="fireable-bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="Fireable-Bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="Fireable-Bugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="Fireable-Bugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="Fireable-Bug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="fireable-some-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FIREABLE-ANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="firable-buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIRABLE-BUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="Firable-Buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="Firable-bUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIRABLE-BUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="firable-bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="Firable-Bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="Firable-Bugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="Firable-Bugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="Firable-Bug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="firable-some-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FIRABLE-ANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="fireablebuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIREABLEBUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FireableBuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FiReAbLebUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIREABLEBUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="fireablebug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FireableBug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FireableBugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FireableBugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FireableBug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="fireablesome-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FIREABLEANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="firablebuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIRABLEBUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FirableBuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FirablebUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FIRABLEBUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="firablebug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FirableBug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FirableBugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FirableBugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="FirableBug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="firablesome-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="FIRABLEANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="MAIN::fireable-buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FIREABLE-BUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::Fireable-Buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FiReAbLe-bUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FIREABLE-BUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::fireable-bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::Fireable-Bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::Fireable-Bugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::Fireable-Bugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::Fireable-Bug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::fireable-some-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::FIREABLE-ANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="MAIN::firable-buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FIRABLE-BUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::Firable-Buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::Firable-bUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FIRABLE-BUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::firable-bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::Firable-Bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::Firable-Bugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::Firable-Bugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::Firable-Bug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::firable-some-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::FIRABLE-ANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="OTHER-MODULE::fireable-buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FIREABLE-BUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Fireable-Buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FiReAbLe-bUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FIREABLE-BUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::fireable-bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Fireable-Bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::Fireable-Bugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::Fireable-Bugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Fireable-Bug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::fireable-some-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::FIREABLE-ANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="OTHER-MODULE::firable-buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FIRABLE-BUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Firable-Buggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Firable-bUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FIRABLE-BUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::firable-bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Firable-Bug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::Firable-Bugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::Firable-Bugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::Firable-Bug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::firable-some-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::FIRABLE-ANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="MAIN::fireablebuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FIREABLEBUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FireableBuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FiReAbLebUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FIREABLEBUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::fireablebug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FireableBug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::FireableBugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::FireableBugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="MAIN::FireableBug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::fireablesome-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="MAIN::FIREABLEANOTHER-rule", MTRete.isFireableBuggyRuleName(s));

		assertTrue(s="OTHER-MODULE::firablebuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FIRABLEBUGGY", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FirableBuggy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FirablebUgGy", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FIRABLEBUG-RULE", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::firablebug-rule", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FirableBug-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::FirableBugler-sounds-charge", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::FirableBugs-are-my-biz", MTRete.isFireableBuggyRuleName(s));
		assertTrue(s="OTHER-MODULE::FirableBug is this rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::firablesome-other-rule", MTRete.isFireableBuggyRuleName(s));
		assertFalse(s="OTHER-MODULE::FIRABLEANOTHER-rule", MTRete.isFireableBuggyRuleName(s));
	}
}
