/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class ProblemEdgeTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(ProblemEdgeTest.class);
        return suite;
    }

	private BR_Controller controller;

	/**
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
		controller = launcher.getFocusedController();
	}

	private static final String[] edgeXMLStrings = {
		"",
		"<edge>\r\n"+
		"    <actionLabel preferPathMark=\"true\" minTraversals=\"1\" maxTraversals=\"1\">\r\n"+
		"        <studentHintRequest />\r\n"+
		"        <stepSuccessfulCompletion />\r\n"+
		"        <stepStudentError />\r\n"+
		"        <uniqueID>1</uniqueID>\r\n"+
		"        <message>\r\n"+
		"            <verb>NotePropertySet</verb>\r\n"+
		"            <properties>\r\n"+
		"                <MessageType>InterfaceAction</MessageType>\r\n"+
		"                <Selection>\r\n"+
		"                    <value>convertDenom1</value>\r\n"+
		"                </Selection>\r\n"+
		"                <Action>\r\n"+
		"                    <value>UpdateTextField</value>\r\n"+
		"                </Action>\r\n"+
		"                <Input>\r\n"+
		"                    <value>24</value>\r\n"+
		"                </Input>\r\n"+
		"            </properties>\r\n"+
		"        </message>\r\n"+
		"        <buggyMessage>No, this is not correct.</buggyMessage>\r\n"+
		"        <successMessage />\r\n"+
		"        <hintMessage>Please enter '24' in the highlighted field.</hintMessage>\r\n"+
		"        <callbackFn />\r\n"+
		"        <actionType>Correct Action</actionType>\r\n"+
		"        <oldActionType>Correct Action</oldActionType>\r\n"+
		"        <checkedStatus>Never Checked</checkedStatus>\r\n"+
		"        <matcher>\r\n"+
		"            <matcherType>ExactMatcher</matcherType>\r\n"+
		"            <matcherParameter name=\"selection\">convertDenom1</matcherParameter>\r\n"+
		"            <matcherParameter name=\"action\">UpdateTextField</matcherParameter>\r\n"+
		"            <matcherParameter name=\"input\">24</matcherParameter>\r\n"+
		"            <matcherParameter name=\"actor\">Student</matcherParameter>\r\n"+
		"        </matcher>\r\n"+
		"    </actionLabel>\r\n"+
		"    <preCheckedStatus>No-Applicable</preCheckedStatus>\r\n"+
		"    <rule>\r\n"+
		"        <text>unnamed</text>\r\n"+
		"        <indicator>-1</indicator>\r\n"+
		"    </rule>\r\n"+
		"    <sourceID>1</sourceID>\r\n"+
		"    <destID>2</destID>\r\n"+
		"    <traversalCount>0</traversalCount>\r\n"+
		"    <SimSt />\r\n"+
		"</edge>"
	};


	public void testToXMLString() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/subgroups.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        int edgeID = 1;
        ProblemEdge edge = controller.getProblemModel().getEdge(edgeID);
		assertEquals("edge["+edgeID+"] from subgroups.brd",
				edgeXMLStrings[edgeID], edge.toXMLString());
	}
}
