/*
 * Created on Jun 10, 2005
 */
package edu.cmu.pact.jess;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import jess.Defrule;
import jess.HasLHS;
import jess.JessException;
import jess.Pattern;
import jess.Value;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WhyNotTest extends TestCase {
	
	/** Whether debug output is enabled. */
	private static boolean debugOn = false;
	
	/** Sink for Jess console messages. */
	private TextOutput textOutput = TextOutput.getNullOutput();

	/** The Rete to use for our several tests. */
	private MTRete r;

	private CTAT_Controller controller;
	MT mt;
	WMEEditor wmeeditor;
	
	/** Help message with command-line arguments. */
	public static final String usageMsg =
		"Usage:\n" +
		"  WhyNotTest [-d] [testName ...]\n" +
		"where--\n" +
		"  -d means turn on debugging;\n" +
		"  testName is one or more individual tests to run; default is all tests.\n";

	/**
	 * Command-line usage: see {@link #usageMsg}.
	 */
	public static void main(String args[]) {
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
		if (i >= args.length) {
			for (int t = 0; t < tests.length; ++t) {
				junit.textui.TestRunner.run(tests[t]);
			}
		}else{
			for (; i < args.length; ++i) {
				Test test = new WhyNotTest(args[i]);
				junit.textui.TestRunner.run(test);
			}
		}
	}

	/**
	 * Complete list of tests. Cf. default list of those methods
	 * beginning with "testXXX" in {@link #suite()}.
	 */
	private static final Test[] tests = {
		new WhyNotTest("testIndexToLineNo"),
		new WhyNotTest("bindMultislotPattern")
	};
	
	/** Default list of tests. @see #tests for complete list. */
	public static Test suite() {
		return new TestSuite(WhyNotTest.class);
	}

	public WhyNotTest() {
		
	}
	
	/**
	 * Constructor for WhyNotTest.
	 * @param arg0
	 */
	public WhyNotTest(String arg0) {
		super(arg0);
		if (debugOn)
			textOutput = TextOutput.getTextOutput(System.out);
	}

	/**
	 * Load the deftemplates, facts and rules files for a given test.
	 *
	 * @param  basename base part of filename for files to load; e.g.,
	 *             use "AdditionChaining" to load AdditionChaining.clp,
	 *             AdditionChaining.wme, AdditionChaining.clp
	 */
	private void loadJessFiles(String basename) {
		long startTime = (new Date()).getTime();
		try {
			String dir = getClass().getPackage().getName().replace('.', '/');
			String path = dir + "/" + basename;
			r.clear();
			boolean results[] =
			    r.loadJessFiles(null,
			            		path + ".clp",
			            		path + ".pr",
			            		path + ".wme",
			            		null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionFailedError("Error loading Jess files: " + e);
		}
		textOutput.println("time(loadJessFiles) = " +
						   ((new Date()).getTime() - startTime));
	}		

	/**
	 * A Jess script and particular rule name for {@link #bindMultislotPattern()}.
	 */
	private class Script {
		/** Base name (omit "MAIN::") of rule on which to call WhyNot. */
		final String ruleName;
		/** Script of deftemplates, rules, assertions. */
		final String script;
		/**
		 * Constructor sets all fields.  Replaces "%RULE_NAME%" in script with
		 * contents of ruleName.
		 * @param ruleName
		 * @param script
		 */
		Script(String ruleName, String script) {
			this.ruleName = ruleName;
			this.script = script.replaceAll("%RULE_NAME%", ruleName);
		}
	}
	
	/** Test scripts for {@link #bindMultislotPattern()}. */
	private Script[] bindMultislotPatternTests = {
		new Script("print-tuple",
				"(deftemplate tuple (slot name) (multislot values))\n"+
				"(defrule MAIN::%RULE_NAME%\n"+
				"  (tuple (name ?n) (values ?s $?u four $?))\n"+
				"  (test (printout t \"(nth$ 2 $?u)=\" (nth$ 2 $?u) crlf))\n"+
				"  (test (eq (nth$ 2 $?u) three))\n"+
				"=>\n"+
				"  (printout t \"u=\" ?u \", v=\" ?v \"\" crlf)\n"+
				")\n"+
				"(assert (tuple (name t) (values one two three)))\n"+
				"(assert (tuple (name t) (values one two three four)))\n"+
				"(assert (tuple (name t) (values one two three four five)))\n"+
				"(rules) (facts) (agenda)\n"),
		new Script("print-tuple",
				"(deftemplate tuple (slot name) (multislot values))\n"+
				"(defrule MAIN::%RULE_NAME%\n"+
				"  (tuple (name ?n) (values ?s $?u four $? ?f))\n"+
				"  ?f <- (tuple (values $?tv&:(= 3 (length$ $?tv))))\n"+
				"  (test (eq (nth$ 2 $?u) three))\n"+
				"=>\n"+
				"  (printout t \"u=\" ?u \", v=\" ?v \"\" crlf)\n"+
				")\n"+
				"(bind ?f1 (assert (tuple (name t1) (values one two three))))\n"+
				"(bind ?f2 (assert (tuple (name t2) (values one two three four))))\n"+
				"(assert (tuple (name t3) (values one two three four five ?f1)))\n"+
				"(assert (tuple (name t4) (values one two three four five ?f1)))\n"+
				"(assert (tuple (name t5) (values one two three four five ?f2)))\n"+
				"(rules) (facts) (agenda)\n"),
		new Script("function-call",
				"(deftemplate tuple (slot name) (multislot values))\n"+
				"(defrule MAIN::%RULE_NAME%\n"+
				"  (tuple (name ?n) (values ?s $?u ?four&:(eq ?four ?s) $?))\n"+
				"  (test (printout t \"(nth$ 2 $?u)=\" (nth$ 2 $?u) crlf))\n"+
				"  (test (eq (nth$ 2 $?u) three))\n"+
				"=>\n"+
				"  (printout t \"u=\" ?u \", v=\" ?v \"\" crlf)\n"+
				")\n"+
				"(assert (tuple (name t) (values one two three)))\n"+
				"(rules) (facts) (agenda)\n")
	};
	
	/**
	 * Test {@link WhyNot#bindMultislotPattern(Value,Pattern,int,Stack)}.
	 */
	public void bindMultislotPattern() {
		for (int i = 0; i < bindMultislotPatternTests.length; ++i)
			doBindMultislotPattern(bindMultislotPatternTests[i]);
	}
	
	/**
	 * Test {@link WhyNot#bindMultislotPattern(Value,Pattern,int,Stack)}
	 * on a single script.
	 * @param  script Script to test
	 */
	private void doBindMultislotPattern(Script script) {
		r = new MTRete(null);
		if (debugOn)
			r.setTextOutput(textOutput);
		String ruleName = "MAIN::"+script.ruleName;
		Reader rdr = new StringReader(script.script);
		try {
			r.parse(rdr);
		} catch (JessException je) {
			throw new AssertionFailedError("Error parsing test2decl: " + je);
		}
		java.util.List ag = new ArrayList();
		for (Iterator it = r.listActivations(); it.hasNext(); )
			ag.add(it.next());
		for (int i = 0; i < ag.size(); ++i) {
			for (int j = i+1; j < ag.size(); ++j)
				trace.out("mt", ag.get(i)+"["+i+"] "+
						(ag.get(i).equals(ag.get(j))? "==" : "!=")+" "+
						ag.get(j)+"["+j+"]");
		}
		ArrayList factsList = new ArrayList();
		for (Iterator it = r.listFacts(); it.hasNext(); )
			factsList.add(it.next());
		Map rulesMap = new java.util.HashMap();
		for (Iterator it = r.listDefrules(); it.hasNext(); ) {
			HasLHS rule = (HasLHS) it.next();
			rulesMap.put(rule.getName(), rule);
		}
		final WhyNot wn = new WhyNot(ruleName, factsList, rulesMap, r.getEventLogger());
		wn.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent we) {
		        wn.dispose();
			}
		});
		wn.setRete(r);
    	mt = new MT(controller);
    	wmeeditor = new WMEEditor(r, mt, null, true, true);
		wn.reasonOut(wmeeditor);
	}

	/**
	 * Test {@link WhyNot#indexToLineNo(int)}.
	 */
	public void testIndexToLineNo() {
		r = new MTRete();
		loadJessFiles("AdditionChaining");
		
		Defrule rule = (Defrule) r.findDefrule("focus-on-first-column");
		RulePrinter rulePrinter = new RulePrinter(rule);
		assertEquals("line 2", rulePrinter.patternIndexToLineNo(1));
		
		rule = (Defrule) r.findDefrule("WhyNot-line-test-1");
		rulePrinter = new RulePrinter(rule);
		assertEquals("line 3", rulePrinter.patternIndexToLineNo(1));
		
		Reader rdr = new StringReader("(defrule test2decl"+
				"(declare (salience -100) (no-loop TRUE))"+
				"?problem <- (problem (subgoals ) (interface-elements $? ?table))"+
				"=>"+
				"(printout t \"2 decls\" crlf))");
		try {
			r.parse(rdr);
		} catch (JessException je) {
			throw new AssertionFailedError("Error parsing test2decl: " + je);
		}
		rule = (Defrule) r.findDefrule("test2decl");
		rulePrinter = new RulePrinter(rule);
		assertEquals("line 4", rulePrinter.patternIndexToLineNo(1));
		
		String text = "(defrule test2decl\n"+
			"(declare (salience -100)\n"+
			"         (no-loop TRUE)\n"+
			"         (some other decl))\n"+
			"?problem <- (problem (subgoals ) (interface-elements $? ?table))\n"+
			"=>\n"+
			"(printout t \"2 decls\" crlf))";
		assertEquals("line 5", RulePrinter.patternIndexToLineNo(1, text));
	}
}

