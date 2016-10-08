/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import java.net.URL;
import java.util.Collection;

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
public class ProblemNodeTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(ProblemNodeTest.class);
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

	private static final String allNodes =
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>nested-groups-test</text>\r\n"+
		"    <uniqueID>1</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>297</x>\r\n"+
		"        <y>30</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state1</text>\r\n"+
		"    <uniqueID>2</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>140</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state2</text>\r\n"+
		"    <uniqueID>3</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>250</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state3</text>\r\n"+
		"    <uniqueID>4</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>360</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state4</text>\r\n"+
		"    <uniqueID>5</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>470</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state5</text>\r\n"+
		"    <uniqueID>6</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>580</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state6</text>\r\n"+
		"    <uniqueID>7</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>690</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state7</text>\r\n"+
		"    <uniqueID>8</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>800</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"false\">\r\n"+
		"    <text>state8</text>\r\n"+
		"    <uniqueID>9</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>340</x>\r\n"+
		"        <y>910</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n"+
		"<node locked=\"false\" doneState=\"true\">\r\n"+
		"    <text>Done</text>\r\n"+
		"    <uniqueID>10</uniqueID>\r\n"+
		"    <dimension>\r\n"+
		"        <x>343</x>\r\n"+
		"        <y>1020</y>\r\n"+
		"    </dimension>\r\n"+
		"</node>\r\n";


	public void testToXMLString() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/subgroups.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
        StringBuffer sb = new StringBuffer();
        Collection<ExampleTracerNode> nodes =
        	controller.getProblemModel().getExampleTracerGraph().getNodeMap().values();
        for (ExampleTracerNode node : nodes)
        	sb.append(node.getProblemNode().toXMLString()).append("\r\n");
		assertEquals("all <nodes>s from subgroups.brd",
				allNodes, sb.toString());
	}
}
