/*
 * Created on Apr 22, 2004
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingUtilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerPath;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.AnyMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherTest;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RangeMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.WildcardMatcher;
import edu.cmu.pact.BehaviorRecorder.SolutionStateModel.SolutionState;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 * 
 */
public class ProblemModelTest extends TestCase {

    private static final String WILDCARD_INPUT = "input*";
    private static final String WILDCARD_ACTION = "action*";
    private static final String WILDCARD_SELECTION = "selection*";
    private static final String TESTFILE_BRD = "testfile.brd";
    private BR_Controller controller;
    private ProblemModel problemModel;
    private SolutionState solutionState;
    private Vector selection;
    private Vector action;
    private Vector input;
    private NodeView vertex1;
    private NodeView vertex2;
    private NodeView vertex3;
    private ProblemNode startNode;

    public static Test suite() {
        return new TestSuite(ProblemModelTest.class);
    }

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
				if (controller == null)
					return;
				if (controller.getActiveWindow() != null)
					controller.getActiveWindow().dispose();				
				if (controller.getStudentInterface() != null && controller.getStudentInterface().getActiveWindow() != null)
					controller.getStudentInterface().getActiveWindow().dispose();
			}
		});
    }


    public void testPseudoTutorModelTracing() {
        
        final CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
        runTestPsuedoTutorModelTracing(launcher);

    }


    /**
     * @param launcher
     */
    public void runTestPsuedoTutorModelTracing(final CTAT_Launcher launcher) {
        createProblemModel(launcher.getFocusedController().getLauncher());


        doDefaultMatchingTests(problemModel, startNode, selection, action, input);

        doRangeMatchingTests(problemModel, selection, action, vertex1);

        doSimpleRegexMatchingTests(problemModel, vertex3);
    }


    private void testWildcardMatcher(NodeView v) {
        ProblemNode node = v.getProblemNode();
        Enumeration edges = problemModel.getProblemGraph().getOutgoingEdges(node);
        ProblemEdge edge = (ProblemEdge) edges.nextElement();
        WildcardMatcher m = (WildcardMatcher) edge.getEdgeData().getMatcher();
        assertEquals (m.getSimpleActionPattern(), WILDCARD_ACTION);
        assertEquals (m.getSimpleSelectionPattern(), WILDCARD_SELECTION);
        assertEquals (m.getSimpleInputPattern(), WILDCARD_INPUT);
        
    }

    /**
     * @return
     */
    private ProblemNode createProblemModel(SingleSessionLauncher launcher) {

        launcher.launch(new TestPanel());
        controller = launcher.getController();
        problemModel = controller.getProblemModel();
        solutionState = controller.getSolutionState();
        
//      Create the start state
        NodeView startState = null;
        try {
            startState = controller.createStartState("start_state");
        } catch (ProblemModelException e) {
            e.printStackTrace();
            assertFalse(true);
        }
        ProblemGraph r = problemModel.getProblemGraph();
        ProblemNode node = new ProblemNode(startState, problemModel);

        //Vertex startState = new Vertex("start state", problemModel);
        final ProblemNode problemNode = r.addProblemNode(node);
        solutionState.setCurrentNode(problemNode);
		
        problemModel.setStartNode(solutionState.getCurrentNode());
        startNode = problemModel.getStartNode();
        selection = MatcherTest.makeVector("mySelection");
        action = MatcherTest.makeVector("myAction");
        input = MatcherTest.makeVector("myInput");
        vertex1 = createDefaultMatcherEdge(controller, problemModel, startState, selection, action, input);
        
		vertex2 = createRangeMatcherEdge(controller, problemModel, vertex1, selection, 
                        action, input, "0.0", "102.34");
        vertex3 = createAnyMatcherEdge(controller, problemModel, vertex2, selection, 
                        action, input);
        createWildcardMatcherEdge(controller, problemModel, vertex3, 
                        WILDCARD_SELECTION, WILDCARD_ACTION, WILDCARD_INPUT);
        return startNode;
    }

    /**
     * @param problemModel
     * @param vertex3
     */
    private void doSimpleRegexMatchingTests(ProblemModel problemModel, NodeView vertex3) {
        ProblemEdge matchedEdge;
        // Test Simple Regex matching edge
        matchedEdge = problemModel.findMatchingEdge(MatcherTest.makeVector ("selection"), MatcherTest.makeVector ("action"), MatcherTest.makeVector ("input"), vertex3.getProblemNode());
        assertFalse (matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(MatcherTest.makeVector ("selectionXXX"), MatcherTest.makeVector ("actionYYY"), MatcherTest.makeVector ("inputZZZ"), vertex3.getProblemNode());
        assertFalse (matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(MatcherTest.makeVector ("bad selection"), MatcherTest.makeVector ("action"), MatcherTest.makeVector ("input"), vertex3.getProblemNode());
        assertTrue (matchedEdge == null);
    }

    /**
     * @param problemModel
     * @param selection
     * @param action
     * @param vertex1
     */
    private void doRangeMatchingTests(ProblemModel problemModel, Vector selection, Vector action, NodeView vertex1) {
        ProblemEdge matchedEdge;
        // Test range matching edge
        matchedEdge = problemModel.findMatchingEdge(selection, action, MatcherTest.makeVector ("1"), vertex1.getProblemNode());
        assertFalse (matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(selection, action, MatcherTest.makeVector ("0.0001"), vertex1.getProblemNode());
        assertFalse(matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(selection, action, MatcherTest.makeVector ("102.34"), vertex1.getProblemNode());
        assertFalse(matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(selection, action, MatcherTest.makeVector ("-1"), vertex1.getProblemNode());
        assertTrue(matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(selection, action, MatcherTest.makeVector ("104"), vertex1.getProblemNode());
        assertTrue(matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(selection, action, MatcherTest.makeVector ("bad input"), vertex1.getProblemNode());
        assertTrue(matchedEdge == null);
    }

    /**
     * @param problemModel
     * @param startNode
     * @param selection
     * @param action
     * @param input
     */
    private void doDefaultMatchingTests(ProblemModel problemModel, ProblemNode startNode, Vector selection, Vector action, Vector input) {
        ProblemEdge matchedEdge;
        // Test default matching edge
        matchedEdge = problemModel.findMatchingEdge(selection, action, input, startNode);
        assertFalse (matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(MatcherTest.makeVector ("wrong selection"), 
                action, input, startNode);
        assertTrue(matchedEdge == null);
        matchedEdge = problemModel.findMatchingEdge(selection, MatcherTest.makeVector ("wrong action"), 
                input, startNode);
        assertTrue(matchedEdge == null);
    }

    /**
     * @param controller
     * @param problemModel
     * @param currentVertex
     * @param selection
     * @param action
     * @param input
     */
    private NodeView createWildcardMatcherEdge(BR_Controller controller, 
            ProblemModel problemModel, NodeView currentVertex, 
            String selectionPattern, String actionPattern,
            String inputPattern) {
        NodeView vertex = new NodeView("state 5", controller);
        ProblemGraph r = problemModel.getProblemGraph();
        ProblemNode node = new ProblemNode(vertex, problemModel);
        ProblemNode problemNode = r.addProblemNode(node);
        EdgeData edgeData = new EdgeData(problemModel);
        
        WildcardMatcher rm = new WildcardMatcher();
        rm.setActionPattern(actionPattern);
        rm.setSelectionPattern(selectionPattern);
        rm.setInputPattern(inputPattern);
        edgeData.setMatcher(rm);
        problemModel.getProblemGraph().addEdge(
                currentVertex.getProblemNode(), problemNode, edgeData);
        return vertex;
    }

    /**
     * @param controller
     * @param problemModel
     * @param currentVertex
     * @param selection
     * @param action
     * @param input
     */
    private NodeView createRangeMatcherEdge(BR_Controller controller, 
            ProblemModel problemModel, NodeView currentVertex, 
            Vector selection, Vector action, Vector input, 
            String minimum, String maximum) {
        NodeView vertex = new NodeView("state 4", controller);
        ProblemGraph r = problemModel.getProblemGraph();
        ProblemNode node = new ProblemNode(vertex, problemModel);
        ProblemNode problemNode = r.addProblemNode(node);
        EdgeData edgeData = new EdgeData(problemModel);
        edgeData.setSelection(selection);
        edgeData.setAction(action);
        edgeData.setInput(input);
        
        RangeMatcher rm = new RangeMatcher();
        rm.setAction((String) action.elementAt(0));
        rm.setSelection((String) selection.elementAt(0));
        rm.setMinimum (minimum);
        rm.setMaximum (maximum);
        edgeData.setMatcher(rm);
        problemModel.getProblemGraph().addEdge(
                currentVertex.getProblemNode(), problemNode, edgeData);
        return vertex;
    }


    /**
     * @param controller
     * @param problemModel
     * @param currentVertex
     * @param selection
     * @param action
     * @param input
     */
    private NodeView createAnyMatcherEdge(BR_Controller controller, 
            ProblemModel problemModel, NodeView currentVertex, 
            Vector selection, Vector action, Vector input) {
        
        NodeView vertex = new NodeView("state 3", controller);
        ProblemGraph r = problemModel.getProblemGraph();
        ProblemNode node = new ProblemNode(vertex, problemModel);
        ProblemNode problemNode = r.addProblemNode(node);
        EdgeData edgeData = new EdgeData(problemModel);
        edgeData.setSelection(selection);
        edgeData.setAction(action);
        edgeData.setInput(input);
        
        AnyMatcher am = new AnyMatcher();
        am.setAction((String) action.elementAt(0));
        am.setSelection((String) selection.elementAt(0));
        edgeData.setMatcher(am);
        problemModel.getProblemGraph().addEdge(
                currentVertex.getProblemNode(), problemNode, edgeData);
        return vertex;
    }

    /**
     * @param controller
     * @param problemModel
     * @param currentVertex
     * @param selection
     * @param action
     * @param input
     */
    private NodeView createDefaultMatcherEdge(BR_Controller controller, ProblemModel problemModel, 
            NodeView currentVertex, Vector selection, Vector action, Vector input) {
        // create a new state
        NodeView vertex = new NodeView("state 2", controller);
        ProblemGraph r = problemModel.getProblemGraph();
        ProblemNode node = new ProblemNode(vertex, problemModel);
        ProblemNode problemNode = r.addProblemNode(node);
        EdgeData edgeData = new EdgeData(problemModel);

        edgeData.setSelection(selection);
		edgeData.setAction(action);
        edgeData.setInput(input);

        problemModel.getProblemGraph().addEdge(
                currentVertex.getProblemNode(), problemNode, edgeData);
        return vertex;
    }

	/**
	 * Test {@link ProblemModel#findPath(ProblemNode)}.
	 */
    public void testFindPath() {
		CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
		Utils.setSuppressDialogs(true);
		controller = launcher.getFocusedController();
		problemModel = controller.getProblemModel();
		problemModel.setUseCommWidgetFlag(false);

		findPath678plus187();
		findPath37plus45();
    }

	/**
	 * Test {@link ProblemModel#findPath(ProblemNode)} in 678plus187formulaTPA.brd.
	 */
	private void findPath37plus45() {
        loadProblemFile("edu/cmu/pact/BehaviorRecorder/ProblemModel/37plus45.brd");
        int destId;
        
        int[] expectedLinkIds4 = {1,3,5};
        ProblemNode dest = problemModel.getProblemNode(destId = 4);
        ExampleTracerPath path = problemModel.findPath(dest);
    	trace.out("pm", "37plus45 path to node "+destId+": "+path);
        int i = 0;
        for (ExampleTracerLink link : path)
        	assertEquals("link ["+i+"] to node "+destId, expectedLinkIds4[i++], link.getID());
	}
	
	/**
	 * Test {@link ProblemModel#findPath(ProblemNode)} in 678plus187formulaTPA.brd.
	 */
	private void findPath678plus187() {
        loadProblemFile("edu/cmu/pact/BehaviorRecorder/Controller/678plus187formulaTPA.brd");
        int destId;
        
        int[] expectedLinkIds3 = {1,3};
        ProblemNode dest = problemModel.getProblemNode(destId = 3);
        ExampleTracerPath path = problemModel.findPath(dest);
    	trace.out("pm", "678plus187 path to node "+destId+": "+path);
        int i = 0;
        for (Iterator<ExampleTracerLink> it = path.iterator(); it.hasNext(); ++i) {
        	ExampleTracerLink link = it.next();
        	assertEquals("link ["+i+"] to node "+destId, expectedLinkIds3[i], link.getID());
        }

        int[] expectedLinkIds5 = {1,3,5,7};
        dest = problemModel.getProblemNode(destId = 5);
        path = problemModel.findPath(dest);
    	trace.out("pm", "678plus187 path to node "+destId+": "+path);
        i = 0;
        for (Iterator<ExampleTracerLink> it = path.iterator(); it.hasNext(); ++i) {
        	ExampleTracerLink link = it.next();
        	assertEquals("link ["+i+"] to node "+destId, expectedLinkIds5[i], link.getID());
        }

        int[] expectedLinkIds8 = {1,3,5,7,11,13};
        dest = problemModel.getProblemNode(destId = 8);
        path = problemModel.findPath(dest);
    	trace.out("pm", "678plus187 path to node "+destId+": "+path);
        i = 0;
        for (Iterator<ExampleTracerLink> it = path.iterator(); it.hasNext(); ++i) {
        	ExampleTracerLink link = it.next();
        	assertEquals("link ["+i+"] to node "+destId, expectedLinkIds8[i], link.getID());
        }
	}

	/**
	 * @param problemFileLocation
	 */
	private void loadProblemFile(String problemFileLocation) {
		URL url = Utils.getURL(problemFileLocation, this);
        trace.out("pm", "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
	}
}
