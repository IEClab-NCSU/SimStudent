/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.TutorInterface;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.CtatModeModel;


/**
 * Test harness.
 */
public class GoToWMMessgHandlerTest extends TestCase {

	/** Whether to wait for input in prompt() calls. @see #main(String[]) */
	private static boolean doPrompt = false;

	/** Controller for all test instances. */
	private BR_Controller brController = null;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
    protected void tearDown() {
    	try {
			super.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	long tearDownTime = System.currentTimeMillis();
		try {
			Thread.sleep(100);			
		} catch (InterruptedException ie) {
			System.err.printf("%s.tearDown slept %d ms: %s\n",
					getClass().getSimpleName(),
					System.currentTimeMillis()-tearDownTime, ie.toString());
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (brController.getActiveWindow() != null)
					brController.getActiveWindow().dispose();				
				if (brController.getStudentInterface().getActiveWindow() != null)
					brController.getStudentInterface().getActiveWindow().dispose();
			}
		});
    }

    /**
	 * Constructor for GoToWMMessgHandlerTest.
	 * @param arg0
	 */
	public GoToWMMessgHandlerTest() {
		SingleSessionLauncher launcher = new CTAT_Launcher(new String[0]).getFocusedController().getLauncher();
		launcher.launch(new TutorInterface());
		brController = launcher.getController();
	}

	/**
	 * Any void method that starts with "test" 
     * will be run automatically using this construct.
	 * @return test suite
	 */
    public static Test suite() {
        return new TestSuite(GoToWMMessgHandlerTest.class);
    }
    
    /**
     * If {@link #doPrompt} is true, wait for a line from stdin.
     */
    private static void prompt(String s) {
		if (!doPrompt)
			return;
    	try {
    		if (s != null)
    			System.out.print(s);
    		System.out.print("> ");
    		while ('\n' != System.in.read());
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
    }
	
    /** Usage message. @see #main(String[]) */
	private static final String USAGE =
		"Usage:\n"+
		"  java ... GoToWMMessgHandlerTest [-d] [-w]\n"+
		"where--\n"+
		"  -d means prompt before each step;\n"+
		"  -w means show the Jess tools' windows.\n";
    
    /**
     * For stand-alone testing. Sets {@link #doPrompt} true.
     */
    public static void main(String[] args) {
    	for (int i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
    		if (args[i].length() < 2)
    			usageExit("missing option letter at argument "+(i+1));
    		char option = args[i].charAt(1);
    		switch(option) {
    		case 'd':
            	doPrompt = true;
            	break;
    		default:
    			usageExit("unsupported option \"-"+option+"\"");
    		}
    	}
		junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Print a usage message and exit.
     * @param msg error message to precede {@link #USAGE}
     */
    private static void usageExit(String msg) {
    	System.err.println("Error: "+msg+". "+USAGE);
		System.exit(2);
	}

	/**
     * Test {@link GoToWMMessgHandler#processMessage()}.
     */
    public void testProcessMessage() {
    	trace.addDebugCodes(System.getProperty("DebugCodes"));
    	prompt("start");
    	brController.loadControlFromSystemProperties();
    	brController.getPreferencesModel().setStringValue(BR_Controller.PROBLEM_DIRECTORY, "edu/cmu/pact/jess");
    	prompt("after loadControl");
        brController.getCtatModeModel().setMode(CtatModeModel.JESS_MODE);
        prompt("after setUseJess");
    	String problemFileLocation = "edu/cmu/pact/jess/start.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        brController.openBRFromURL(url.toString());
        prompt("after open graph");
        prompt("after show Jess windows");
//        brController.setBehaviorRecorderMode(CtatModeModel.JESS_MODE);
//        prompt("after set mode");
        ProblemModel pm = brController.getProblemModel();
        ProblemNode startNode = pm.getStartNode();
        assertEquals("start", startNode.getName());
        ProblemNode doneNode = pm.getNode("Done");
        assertEquals("Done", doneNode.getName());
        brController.setCurrentNode2(doneNode);
        ProblemNode chainNode = brController.getSolutionState().getCurrentNode();
        trace.out("mt", "after setCurrentNode2 SolutionState.getCurrentNode() "+
        		chainNode.getName());
        long startTime = (new Date()).getTime();
        MessageObject response = brController.getGoToWMStateResponse(doneNode.getName());
    	prompt("after go to Done: response=\n"+response+", time (ms) "+
    			((new Date()).getTime() - startTime));
    	assertEquals("wrong MessageType", "ChangeWMState", response.getMessageType());
    	CheckLinksList checkLinksList = CheckLinksList.getCheckedLinksList(response);
    	assertEquals("wrong number of links in response", 6, checkLinksList.size());
    	for (int i = 0; i < checkLinksList.size(); ++i) {
    		switch (i) {
    		case 0: {
    			String[] ruleSeq = { "MAIN::focus-on-first-column", "MAIN::add-addends", "MAIN::must-carry", "MAIN::write-sum" };
    			assertCheckLink(checkLinksList, i, 1, EdgeData.SUCCESS, ruleSeq);
    			break;
    		}
    		case 1 : {
    			String[] ruleSeq = { "MAIN::write-carry" };
    			assertCheckLink(checkLinksList, i, 2, EdgeData.SUCCESS, ruleSeq);
    			break;
    		}
    		case 2: {
    			String[] ruleSeq = { "MAIN::focus-on-next-column", "MAIN::add-addends", "MAIN::add-carry", "MAIN::must-carry", "MAIN::write-sum" };
    			assertCheckLink(checkLinksList, i, 4, EdgeData.SUCCESS, ruleSeq);
    			break;
    		}
    		case 3: {
    			String[] ruleSeq = { "MAIN::write-carry" };
    			assertCheckLink(checkLinksList, i, 6, EdgeData.SUCCESS, ruleSeq);
    			break;
    		}
    		case 4: {
    			String[] ruleSeq = { "MAIN::focus-on-next-column", "MAIN::add-addends", "MAIN::add-carry", "MAIN::write-sum" };
    			assertCheckLink(checkLinksList, i, 8, EdgeData.SUCCESS, ruleSeq);
        		break;
    		}
    		case 5: {
    			String[] ruleSeq = { "MAIN::focus-on-next-column", "MAIN::done" };
    			this.assertCheckLink(checkLinksList, i, 17, EdgeData.SUCCESS, ruleSeq);
    			break;
    		}
    		default:
    			fail("too many links");
    		}
    	}
    }
    
    /**
     * Check the contents of a checkLinksList element in the ChangeWMState
     * response from the rule engine. The element is a Vector with 3 elements
     * which give the results from the rule engine on a single step (i.e., an
     * edge in the BR graph) in the path traversed by the Go_To_WM_State
     * operation. 
     * @param checkLink response data
     * @param linkIndex index of link element to analyze
     * @param expLinkID expected value of the uniqueID element, which
     *        identifies the edge; if less than -2, not checked
     * @param expCheckResult expected value of the checkResult element;
     *        if null, not checked
     * @param expRuleSeq expected rule names for this step;
     *        if null, not checked
     * @return true if all checks pass
     */
    private boolean assertCheckLink(CheckLinksList checkLinksList, int linkIndex,
    		int expLinkID, String expCheckResult, String[] expRuleSeq) {
    	int linkID = checkLinksList.getLinkID(linkIndex);
    	String checkResult = checkLinksList.getCheckResult(linkIndex);
    	
    	assertEquals("linkIndex "+linkIndex, expLinkID, linkID);
    	assertEquals("linkIndex "+linkIndex, expCheckResult, checkResult);
		List ruleSeq = checkLinksList.getRuleSeq(linkIndex);
		if (expRuleSeq == null || expRuleSeq.length <= 0)
			assertEquals("linkIndex "+linkIndex, 0, ruleSeq.size());
		else {
			assertEquals("linkIndex "+linkIndex,
					expRuleSeq.length, ruleSeq.size());
			for (int i = 0; i < expRuleSeq.length; ++i)
				assertEquals("linkIndex "+linkIndex,
						expRuleSeq[i], (String) ruleSeq.get(i));
		}
    	return true;
    }
}
