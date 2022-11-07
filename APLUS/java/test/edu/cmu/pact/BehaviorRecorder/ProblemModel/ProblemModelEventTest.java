/*
 * July 23rd 2009
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.SolutionStateModel.SolutionState;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * @author collinl
 * 
 * This class provides a test for the problem model and problem model events. 
 * When run it will generate and throw different problem model events which 
 * can then be "caught" by the registered listener.  I developed this to test
 * the compound events which should be handled by this.  
 */
public class ProblemModelEventTest 
    extends TestCase 
    implements ProblemModelListener {

    private static final String WILDCARD_INPUT = "input*";
    private static final String WILDCARD_ACTION = "action*";
    private static final String WILDCARD_SELECTION = "selection*";
    private static final String TESTFILE_BRD = "testfile.brd";
    private BR_Controller controller;
    private Logger logger;
    private ProblemModel problemModel;
    private SolutionState solutionState;
    private ProblemGraph PGraph;
    //private Vector selection;
    //private Vector action;
    //private Vector input;
    private NodeView vertex1;
    private NodeView vertex2;
    private NodeView vertex3;
    private ProblemNode startNode;

    public static Test suite() {
        return new TestSuite(ProblemModelEventTest.class);
    }

    /** For restoring value to {@link Utils#setSuppressDialogs(boolean)}. */
    private static Boolean saveSuppressDialogs = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
    	super.setUp();
    	
    	if(saveSuppressDialogs == null)
    		saveSuppressDialogs = Boolean.valueOf(Utils.getSuppressDialogs());

    	String[] args = {
    			"-Dunit_name=TestUnit",
    			"-"+CTAT_Launcher.SKIP_MONITOR_ARG
    	};

        CTAT_Launcher cLauncher = new CTAT_Launcher(args);
        controller = cLauncher.getFocusedController();
        Utils.setSuppressDialogs(true);
//    	
//    	SingleSessionLauncher launcher = new SingleSessionLauncher(args, false);
//    	Utils.setRuntime(true);  // set true after SingleSessionLauncher() constructor
//    	this.controller = launcher.getController();
    	assertNotNull(controller);

    	String problemFileLocation = "678plus187.brd";
    	URL url = Utils.getURL(problemFileLocation, this);
    	trace.addDebugCode("log");
    	trace.out("log", "problemFileLocation str = " 
    			+ problemFileLocation + ", url = "+url);
    	if (url == null) {
    		trace.err("null URL for problemFileLocation "+problemFileLocation);
    	} else {
    		this.controller.openBRFromURL(url.toString());        
    	}
    	this.logger = this.controller.getLogger();

    	problemModel = this.controller.getProblemModel();
    	problemModel.addProblemModelListener(this);
    }


    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        Utils.setSuppressDialogs(saveSuppressDialogs);
    }


    public void testPseudoTutorModelTracing() {
        
        testProblemModelListeners();
    }


    /**
     * @param launcher:  SingleSessionLauncher for this item.
     *
     * Having loaded a problem model from the file and iterate 
     * over the contents.  For the present just output the 
     * nodes in the graph.  
     */
    public void testProblemModelListeners() {
    	testEvents(controller.getProblemModel());

    }


    
    /* -----------------------------------------------
     * Actual test code.
     * -------------------------------------------- */

    /** 
     * This code doesn't test the actual listener, (ha ha) 
     * rather it calls individual subtests below to fire
     * events based upon the problem model and then has 
     * them handled.  This is really about testing the 
     * events themselves.  
     * @param PMod problem model, already loaded with graph
     */
    private void testEvents(ProblemModel PMod) {

    	/* First test the existence of the problem model
    	 * By displaying things. */
    	PGraph = PMod.getProblemGraph();

    	Enumeration Nodes = this.PGraph.nodes();
    	assertNotNull("Nodes Empty.", Nodes);
    	trace.out("Nodes Generated: " + Nodes);

    	Enumeration Edges = this.PGraph.edges();
    	assertNotNull("Edges Empty", Edges);
    	trace.out("Edges Generated: " + Edges);

    	// Package atomic events.
    	testAtomicEvents(PGraph);

    	// Package a compound event and extract the parts.
    	//this.testPackageEvent();
    }

    
    /** 
     * Test the individual atomic events by generating one of 
     * each type and then passing it to be handled.
     */
    private void testAtomicEvents(ProblemGraph PGraph) {

	ProblemModelEvent Ev;
	ProblemNode N = PGraph.getNode(1);   // not getFirstNode(), which gets the last one
	assertNotNull("No Start Node.", N);
	Vector<ProblemNode> Children = N.getChildren();
	assertFalse("error in size", Children.isEmpty());
	ProblemNode M = ((ProblemNode) Children.elementAt(0));
	assertNotNull("No child of start node.", M);
	List<ProblemEdge> OutgoingEdges = N.getOutgoingEdges();
	ProblemEdge PEdg = (ProblemEdge) OutgoingEdges.get(0);
	ArrayList<ProblemModelEvent> Subevents = new ArrayList<ProblemModelEvent>(); 
	
	// Change Current Node Event from start to a child.
	ChangeCurrentNodeEvent CCNEv 
	    = new ChangeCurrentNodeEvent(this.controller, N, M);
	this.problemModel.fireProblemModelEvent(CCNEv);
	Subevents.add(CCNEv);

	// Fire an edge creation event.
	EdgeCreatedEvent EDEv 
	    = new EdgeCreatedEvent(this.problemModel, PEdg);
	this.problemModel.fireProblemModelEvent(EDEv);
	// Now do so with subevents.
	Subevents.add(EDEv);
	EDEv = new EdgeCreatedEvent(this.problemModel, PEdg, Subevents);
	this.problemModel.fireProblemModelEvent(EDEv);

	// Fire an edge creation failed..
	Subevents = new ArrayList<ProblemModelEvent>(); 
	EdgeCreationFailedEvent EFEv 
	    = new EdgeCreationFailedEvent(this.controller,
					  EdgeCreationFailedEvent.Reason.LINK_AFTER_BUGGY_LINK,
					  "Test Error msg.");
	this.problemModel.fireProblemModelEvent(EFEv);
	Subevents.add(EFEv);
	EFEv = new EdgeCreationFailedEvent(this.controller,
					  EdgeCreationFailedEvent.Reason.LINK_AFTER_BUGGY_LINK, 
					  "Test Error msg.",
					  Subevents);
	this.problemModel.fireProblemModelEvent(EFEv);
	

	// Fire an edge deleted.
	Subevents = new ArrayList<ProblemModelEvent>(); 
	EdgeDeletedEvent EDlEv = new EdgeDeletedEvent(PEdg);
	this.problemModel.fireProblemModelEvent(EDlEv);
	Subevents.add(EDlEv);
	EDlEv = new EdgeDeletedEvent(PEdg, Subevents);
	this.problemModel.fireProblemModelEvent(EFEv);
	
	
	// Fire an Edge Updated crucially make it linking
	// changed data not changed.
	Subevents = new ArrayList<ProblemModelEvent>(); 
	EdgeUpdatedEvent EUEv 
	    = new EdgeUpdatedEvent(this, PEdg,  false);
	this.problemModel.fireProblemModelEvent(EUEv);
	Subevents.add(EUEv);
	EUEv = new EdgeUpdatedEvent(this, PEdg,  false, Subevents);
	this.problemModel.fireProblemModelEvent(EUEv);

	// Generate New problem events.
	Subevents = new ArrayList<ProblemModelEvent>(); 
	NewProblemEvent NEv = new NewProblemEvent(this, true);
	this.problemModel.fireProblemModelEvent((ProblemModelEvent) NEv);
	Subevents.add(NEv);
	NEv = new NewProblemEvent(this, true, Subevents);
	this.problemModel.fireProblemModelEvent(NEv);

	// Node Created Events.
	Subevents = new ArrayList<ProblemModelEvent>(); 
	NodeCreatedEvent NCEv = new NodeCreatedEvent(this.controller, N);
	this.problemModel.fireProblemModelEvent(NCEv);
	Subevents.add(NCEv);
	NCEv = new NodeCreatedEvent(this.controller, N, Subevents);
	this.problemModel.fireProblemModelEvent(NCEv);
	
	// Node Deleted Events.
	NodeDeletedEvent NDEv = new NodeDeletedEvent(N);
	this.problemModel.fireProblemModelEvent(NDEv);
	Subevents.add(NDEv);
	NDEv = new NodeDeletedEvent(N, Subevents);
	this.problemModel.fireProblemModelEvent(NDEv);

	// Node Updated Events.
	NodeUpdatedEvent NUEv = new NodeUpdatedEvent(this.controller, N);
	this.problemModel.fireProblemModelEvent(NUEv);
	Subevents.add(NUEv);
	NUEv = new NodeUpdatedEvent(this.controller, N, Subevents);
	this.problemModel.fireProblemModelEvent(NUEv);
    }




    /** 
     * Trap Events by type checking each one.
     */
    public void problemModelEventOccurred(ProblemModelEvent Ev) {
	
	// Test to ensure that edges are supplied with Edge Events.
	if (Ev instanceof EdgeEvent) {
	    assertNotNull("Empty Edge", ((EdgeEvent) Ev).getEdge());
	}
	
	// Test to ensure that nodes are supplied with node events.
	if (Ev instanceof NodeEvent) {
	    assertNotNull("Empty Node", ((NodeEvent) Ev).getNode());
	}
	

	// Test each event type in turn.  
	if (Ev instanceof ChangeCurrentNodeEvent) {
	    ChangeCurrentNodeEvent E = (ChangeCurrentNodeEvent) Ev;
	    assertNotNull("New problem node missing.", E.getNewProblemNode());
	    assertNotNull("Old Problem node missing.", E.getOldProblemNode());
	}
	else if (Ev instanceof EdgeCreatedEvent) {
	    EdgeCreatedEvent E = (EdgeCreatedEvent) Ev;
	    assertNotNull("Edge not Provided", E.getEdge());
	}
	else if (Ev instanceof EdgeCreationFailedEvent) {
	    EdgeCreationFailedEvent E = (EdgeCreationFailedEvent) Ev;
	    assertNotNull("Missing Cause.", E.getCause());
	    assertNotNull("Missing Message.", E.getMsg());
	}
	else if (Ev instanceof EdgeDeletedEvent) {
	    EdgeDeletedEvent E = (EdgeDeletedEvent) Ev;
	    assertNotNull("Missing Edge", E.getEdge());
	}
	else if (Ev instanceof EdgeUpdatedEvent) {
	    EdgeUpdatedEvent E = (EdgeUpdatedEvent) Ev;
	    assertNotNull("Missing Edge", E.getEdge());
	    //assertTrue("Linking Not changed.", E.edgeLinkingChangedP());
	    assertTrue("Data changed.", !E.edgeDataChangedP());
	}
	else if (Ev instanceof NewProblemEvent) {
	    assertTrue("Not Unordered", ((NewProblemEvent) Ev).isUnordered());
	}
	else if (Ev instanceof NodeCreatedEvent) {
	    assertNotNull("Node Null.", ((NodeEvent) Ev).getNode());
	}
	else if (Ev instanceof NodeDeletedEvent) {
	    assertNotNull("Node Null.", ((NodeEvent) Ev).getNode());
	}
	else if (Ev instanceof NodeUpdatedEvent) {
	    assertNotNull("Node Null.", ((NodeEvent) Ev).getNode());
	}

	
	// Finally recursively fire the subevents after collecting
	// all of them. 
	if (Ev.isCompoundEventP()) {
	    for (ProblemModelEvent E : Ev.getSubevents()) {
	    	this.problemModelEventOccurred(E);
	    }
	
	    // Also test collection for node events.
	    ArrayList<Class> TypeList = new ArrayList();
	    TypeList.add(edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeEvent.class);
	    List<ProblemModelEvent> NE = Ev.collectTypeSubevents(ProblemModelEvent.class, true, true, false);
	    for (ProblemModelEvent E : NE) { this.problemModelEventOccurred(E); }
	}
    }	
}
