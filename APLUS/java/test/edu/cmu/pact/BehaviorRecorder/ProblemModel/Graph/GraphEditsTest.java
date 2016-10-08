package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandler;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest.AttemptTest;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest.MTTest;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest.SinkToolProxy;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.BR_JGraph;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.JGraphController;
import edu.cmu.pact.ctat.model.CtatModeModel;
/**
 * Note: This test is dependent on some functions in pseudotutormessagehandlertest
 * Changes to the code that function might effect the functionality of this test.
 * If anything ever goes wrong
 * 
 * @author blojasie
 *
 */
public class GraphEditsTest extends TestCase {
	
	static {
		CTATTabManager.setMaxNumTabs(15);
	}
	
	/** MessageType value for correct actions. */
	private static final String CORRECT = "CorrectAction";

	/** MessageType value for incorrect actions. */
	private static final String INCORRECT = "InCorrectAction";
	
	/** Index in XxxxTests[] of current test. */
	private int currTest = -1;
	
	/** Controller for this test. */
	
	
	//BR_Controller controller = null;
	
	/** Message sink--substitute for UTP. */
	SinkToolProxy sink = null;

	/** Message handler to exercise. */
	private PseudoTutorMessageHandler pseudoTutorMessageHandler;


	private ExampleTracerGraph exampleTracerGraph;

	/** Current test we're running. */
	private static String testListName;
	
	
	/*Fields for storing CTAT_launcher and SSL across tests	 */	
	private static CTAT_Launcher ctatLauncher = new CTAT_Launcher(new String[0]);
	private static BR_Controller controller = ctatLauncher.getFocusedController();
	private static SingleSessionLauncher ssl = ctatLauncher.getFocusedController().getLauncher();
	public GraphEditsTest()
	{
		//ctatLauncher = new CTAT_Launcher(new String[0]);
		//ssl = ctatLauncher.getFocusedController().getLauncher();
	//	controller = ssl.getController();
	}
	
	
	public static Test suite() {
        TestSuite suite = new TestSuite(GraphEditsTest.class);
        return suite;
    }
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(GraphEditsTest.suite());
	}
	
	protected void setUp() throws Exception{
		super.setUp();
		controller = ssl.getController();
		controller.startNewProblem(null);
		sink = new SinkToolProxy(controller); 
        controller.setUniversalToolProxy(sink);
        controller.getProblemModel().setUseCommWidgetFlag(false);
        //public void actionPerformed
        //br_jgraph.graphChanged(GraphModelEvent e) {
        //nodeview.copy/paste
        //pseudoTutorMessageHandler = new PseudoTutorMessageHandler(controller);
        //
        pseudoTutorMessageHandler = controller.getPseudoTutorMessageHandler();
        exampleTracerGraph = controller.getExampleTracerGraph();
	}
	
	
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	
	private static MTTest[] DeleteSingleLinkAndMergeTests = {
		new AttemptTest("table1_C1R1",	"UpdateTable",	"1",	CORRECT),
		new AttemptTest("table1_C2R1",	"UpdateTable",	"2",	INCORRECT),
		new AttemptTest("table1_C3R1",	"UpdateTable",	"3",	CORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testDeleteSingleLinkAndMerge(){
		//Load brd
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testDeleteSingleLinkAndMerge.brd";
       // URL url = Utils.getURL(problemFileLocation, this);
        controller.openBRDFileAndSendStartState(problemFileLocation,null);
        EdgeData deleteEdge = controller.getProblemModel().getEdge(3).getEdgeData();
        int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        int originalNodeCount  = controller.getProblemModel().getProblemGraph().getNodeCount();
        deleteEdge.getEdge().getLinkEditFunctions().testProcessDeleteSingleEdge();
        List startStateMsgs = sink.getLatestMsgs();
        assertTrue(this.controller.getProblemModel().getProblemGraph().getEdgeCount() == (originalEdgeCount-1));
        assertTrue(this.controller.getProblemModel().getProblemGraph().getNodeCount() == (originalNodeCount -1));
        
        runMTTests(DeleteSingleLinkAndMergeTests, "DeleteSingleLinkAndMerge");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));
	}
	
	private static MTTest[] DeleteSingleLinkDisconnectTests = {
		new AttemptTest("table1_C1R1",	"UpdateTable",	"3",	CORRECT),
		new AttemptTest("table1_C2R1",	"UpdateTable",	"4",	INCORRECT),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"6",	CORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testDeleteSingleLinkDisconnect(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testDeleteSingleLinkDisconnect.brd";
		//URL url = Utils.getURL(problemFileLocation, this);
        controller.openBRDFileAndSendStartState(problemFileLocation, null);
        EdgeData deleteEdge = controller.getProblemModel().getEdge(3).getEdgeData();
        int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        deleteEdge.getEdge().getLinkEditFunctions().testProcessDeleteSingleEdge();
        List startStateMsgs = sink.getLatestMsgs();
        //the node is still reachable...but...
        assertFalse(this.controller.getExampleTracerGraph().isNodeConnected(deleteEdge.getEdge().dest.getUniqueID()));
        //the node still exists
        assertTrue(this.controller.getExampleTracerGraph().getNode(deleteEdge.getEdge().dest.getUniqueID())!=null);
        //the edge has been deleted
        assertTrue(this.controller.getProblemModel().getProblemGraph().getEdgeCount() == (originalEdgeCount-1));
        runMTTests(DeleteSingleLinkDisconnectTests, "DeleteSingleLinkDisconnect");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));
	}
	
	private static MTTest[] DeleteSingleLinkTests = {
		new AttemptTest("table1_C1R1",	"UpdateTable",	"3",	CORRECT),
		new AttemptTest("table1_C3R1",	"UpdateTable",	"5",	CORRECT),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"6",	INCORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	public void testDeleteSingleLink(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testDeleteSingleLink.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
        EdgeData deleteEdge = controller.getProblemModel().getEdge(12).getEdgeData();
        int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        deleteEdge.getEdge().getLinkEditFunctions().testProcessDeleteSingleEdge();
        List startStateMsgs = sink.getLatestMsgs();
        assertTrue(this.controller.getProblemModel().getProblemGraph().getEdgeCount() == (originalEdgeCount-1));
        runMTTests(DeleteSingleLinkTests, "DeleteSingleLink");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done_2"));//done 14
	}
	
	private static MTTest[] rewireSourceTests = {
		new AttemptTest("table1_C1R1",	"UpdateTable",	"1",	CORRECT),
		new AttemptTest("table1_C2R1",	"UpdateTable",	"2",	CORRECT),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"5",	CORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testRewireSource(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testRewireSource.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
        ProblemEdge rewireEdge = controller.getProblemModel().getEdge(9);
        ProblemNode newSource = controller.getProblemModel().getNode("state2");
        //int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        String result = controller.getProblemModel().testNewDestNodeForLink(rewireEdge, newSource);
        assertTrue((result==null));
        //removeSingleEdgeFromJgraph(rewireEdge);
        controller.changeEdgeSourceNode(rewireEdge, newSource);
        List startStateMsgs = sink.getLatestMsgs();
        //assertTrue(this.controller.getProblemModel().getProblemGraph().getEdgeCount() == (originalEdgeCount-1));
        runMTTests(rewireSourceTests, "RewireSource");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));//done 14	
	}
	
	private static MTTest[] rewireDestTests = {
		new AttemptTest("table1_C1R1",	"UpdateTable",	"1",	CORRECT),
		new AttemptTest("table1_C2R1",	"UpdateTable",	"2", INCORRECT, true),
		new AttemptTest("table1_C6R1",	"UpdateTable",	"6",	CORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testRewireDest(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testRewireDest.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
        ProblemEdge rewireEdge = controller.getProblemModel().getEdge(1);
        ProblemNode newDest = controller.getProblemModel().getNode("state5");
        //int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        String result = controller.getProblemModel().testNewDestNodeForLink(rewireEdge, newDest);
        assertTrue((result==null));
        //removeSingleEdgeFromJgraph(rewireEdge);
        controller.changeEdgeDestNode(rewireEdge, newDest);
        List startStateMsgs = sink.getLatestMsgs();
        //assertTrue(this.controller.getProblemModel().getProblemGraph().getEdgeCount() == (originalEdgeCount-1));
        runMTTests(rewireDestTests, "RewireDest");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));//done 14	
	}
	
	private static MTTest[] rewireEverythingTests = {
		new AttemptTest("table1_C1R1",	"UpdateTable",	"1",	CORRECT),
		new AttemptTest("table1_C2R1",	"UpdateTable",	"2", CORRECT),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"5",	CORRECT),
		new AttemptTest("table1_C3R1",	"UpdateTable",	"3",	CORRECT),
		new AttemptTest("table1_C6R1",	"UpdateTable",	"6",	CORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testRewireEverything(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testRewireEverything.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
        ProblemEdge rewireEdge;
        ProblemNode newDest,newSource;
        
        rewireEdge = controller.getProblemModel().getEdge(3);
        newDest = controller.getProblemModel().getNode("state4");
        String result = controller.getProblemModel().testNewDestNodeForLink(rewireEdge, newDest);
        assertTrue((result==null));
        controller.changeEdgeDestNode(rewireEdge, newDest);
        //List startStateMsgs = sink.getLatestMsgs();

        rewireEdge = controller.getProblemModel().getEdge(9);
        newDest = controller.getProblemModel().getNode("state2");
        result = controller.getProblemModel().testNewDestNodeForLink(rewireEdge, newDest);
        assertTrue((result==null));
        controller.changeEdgeDestNode(rewireEdge, newDest);
        
        rewireEdge = controller.getProblemModel().getEdge(11);
        newSource = controller.getProblemModel().getNode("state3");
        result = controller.getProblemModel().testNewSourceNodeForLink(rewireEdge, newSource);
        assertTrue((result==null));
        controller.changeEdgeSourceNode(rewireEdge, newSource);
        List startStateMsgs = sink.getLatestMsgs();
        
        runMTTests(rewireEverythingTests, "RewireEverything");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));//done 14	
	}
	
	private static MTTest[] rewireRestoreStateTests = {
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testRewireRestoreState(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testRewireEverything.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
        //Click ProblemNode
        ProblemNode state6 =controller.getProblemModel().getNode("state6");
        controller.problemNodeClicked(state6);
        
        ProblemEdge rewireEdge = controller.getProblemModel().getEdge(11);
        ProblemNode newSource = controller.getProblemModel().getNode("state2");
        //int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        String result = controller.getProblemModel().testNewSourceNodeForLink(rewireEdge, newSource);
        assertTrue((result==null));
        //removeSingleEdgeFromJgraph(rewireEdge);
        controller.changeEdgeSourceNode(rewireEdge, newSource);
        List startStateMsgs = sink.getLatestMsgs();
        
        assertTrue(state6==controller.getCurrentNode());
        runMTTests(rewireRestoreStateTests, "RewireRestoreState");
	}
	
	public void testRewireSourceGoToParent(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testRewireEverything.brd";
		//URL url = Utils.getURL(problemFileLocation, this);
        controller.openBRDFileAndSendStartState(problemFileLocation,null);
        //Click ProblemNode
        ProblemNode state5 =controller.getProblemModel().getNode("state5");
        controller.problemNodeClicked(state5);
        
        ProblemNode parentOfState5 =controller.getProblemModel().getNode("state4");
        
        ProblemEdge rewireEdge = controller.getProblemModel().getEdge(9);
        ProblemNode newDest = controller.getProblemModel().getNode("state2");
        //int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        String result = controller.getProblemModel().testNewDestNodeForLink(rewireEdge, newDest);
        assertTrue((result==null));
        //removeSingleEdgeFromJgraph(rewireEdge);
        controller.changeEdgeDestNode(rewireEdge, newDest);
        List startStateMsgs = sink.getLatestMsgs();
        assertTrue(parentOfState5==controller.getCurrentNode());
	}
	public void testRewireGoToStartState(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/testRewireEverything.brd";
		//URL url = Utils.getURL(problemFileLocation, this);
        controller.openBRDFileAndSendStartState(problemFileLocation,null);
        //Click ProblemNode
        ProblemNode state6 =controller.getProblemModel().getNode("state6");
        controller.problemNodeClicked(state6);
        
        ProblemEdge rewireEdge = controller.getProblemModel().getEdge(9);
        ProblemNode newDest = controller.getProblemModel().getNode("state2");
        //int originalEdgeCount = controller.getProblemModel().getProblemGraph().getEdgeCount();
        String result = controller.getProblemModel().testNewDestNodeForLink(rewireEdge, newDest);
        assertTrue((result==null));
        //removeSingleEdgeFromJgraph(rewireEdge);
        controller.changeEdgeDestNode(rewireEdge, newDest);
        List startStateMsgs = sink.getLatestMsgs();
        
        assertTrue(controller.getExampleTracerGraph().getStartNode().getProblemNode()==controller.getCurrentNode());
        
	}
	private static MTTest[] MergeWithGroupsTests = {
		new AttemptTest("table1_C3R2",	"UpdateTable",	"1",	CORRECT),
		//new AttemptTest("table1_C2R1",	"UpdateTable",	"2", INCORRECT, true),
		new AttemptTest("table1_C4R2",	"UpdateTable",	"2",	CORRECT),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"4",	CORRECT),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"5",	CORRECT),
		//new AttemptTest("table1_C4R2",	"UpdateTable",	"6",	CORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testMergeWithGroups(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/groupsAndStartStateVariablesGraph.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
        ProblemNode state12 = controller.getProblemModel().getNode("state12");
        ProblemNode state2 = controller.getProblemModel().getNode("state2");
        ExampleTracerGraph graph = controller.getExampleTracerGraph();
        LinkGroup for23 = graph.getSmallestContainingGroup(graph.findLinkByID(23));
        LinkGroup for29 = graph.getSmallestContainingGroup(graph.findLinkByID(29));
        LinkGroup for25 = graph.getSmallestContainingGroup(graph.findLinkByID(25));
        controller.mergeStates2(state12, state2, true, false, true);
        sink.getLatestMsgs();
        //Testing whether the groups were preserved in the merge.
        ExampleTracerLink temp = graph.getLink(23);
        assertTrue(graph.getGroupModel().isLinkInGroup(for23, temp));
        temp = graph.getLink(25);
        assertTrue(graph.getGroupModel().isLinkInGroup(for25, temp));
        assertTrue(temp.getPrevNode() == state2.getUniqueID());
        temp = graph.getLink(29);
        assertTrue(graph.getGroupModel().isLinkInGroup(for29, temp));
        assertNull(graph.getNode(state12.getUniqueID()));
        assertNotNull(graph.getNode(state2.getUniqueID()));
        //  graph.getGroupModel().isLinkInGroup(group, link);
        runMTTests(MergeWithGroupsTests, "MergeWithGroups");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));	
	}
	private static MTTest[] CopyPasteTests = {
		new AttemptTest("table1_C6R1",	"UpdateTable",	"6",	CORRECT),
		//new AttemptTest("table1_C2R1",	"UpdateTable",	"2", INCORRECT, true),
		new AttemptTest("table1_C4R1",	"UpdateTable",	"4",	CORRECT),
		new AttemptTest("table1_C3R1",	"UpdateTable",	"3",	CORRECT),
		new AttemptTest("table1_C5R1",	"UpdateTable",	"5",	CORRECT),
		//new AttemptTest("table1_C4R2",	"UpdateTable",	"6",	CORRECT),
		new AttemptTest("done",	"ButtonPressed",	"-1",	CORRECT)
	};
	
	public void testCopyPaste(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/startStateVariablesGraph.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
		ProblemNode state5 = controller.getProblemModel().getNode("state5");
        ProblemNode state12 = controller.getProblemModel().getNode("state12");
        controller.setCopySubgraphNode(state5);
        state12.getNodeView().paste();
        sink.getLatestMsgs();
        runMTTests(CopyPasteTests, "CopyPaste");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done_2"));
        ProblemEdge e27 = controller.getProblemModel().getEdge(27);
        assertNotNull("edge27 not found", e27);
        ProblemEdge e34 = controller.getProblemModel().getEdge(34);
        assertNotNull("edge34 not found", e34);
        EdgeData ed27 = e27.getEdgeData();
        EdgeData ed34 = e34.getEdgeData();
        assertNotSame("edges 27, 34 have same CommMsgObj",
        		ed27.getDemoMsgObj(), ed34.getDemoMsgObj());
        Matcher m27 = ed27.getMatcher();
        Matcher m34 = ed34.getMatcher();
        assertNotSame("matchers for edges 27, 34", m27, m34);
        Vector input27 = ed27.getInput();
        Vector input34 = ed34.getInput();
        assertNotSame("getInput edgeData 27, 34", input27, input34);
        ed34.setInput(PseudoTutorMessageBuilder.s2v("fred"));
        assertNotSame("getInput[0] edgeData 27, 34", input27.get(0), input34.get(0));
        this.assertFalse("getInput[0] edgeData 27, 34", input27.get(0).equals(input34.get(0)));
        ed34.resetCommMessage();
        input27 = ed27.getInput();
        input34 = ed34.getInput();
        this.assertFalse("getInput[0] after resetCommMessage 27, 34", input27.get(0).equals(input34.get(0)));
	}
	
	private static MTTest[] AddBlankStepTests = {
		//new AttemptTest("table1_C6R1",	"UpdateTable",	"6",	CORRECT),
		new AttemptTest("No_Selection",	"No_Action", "No_Value",	CORRECT),
	};
	public void testAddBlankStep(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/startStateVariablesGraph.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
		ProblemNode state5 = controller.getProblemModel().getNode("state5");
		controller.problemNodeClicked(state5);
		state5.getNodeView().addBlankState();
		sink.getLatestMsgs();
        runMTTests(AddBlankStepTests, "AddBlankStep");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));	
	}
	
	private static MTTest[] InsertNodeAboveTests = {
		new AttemptTest("No_Selection",	"No_Action", "No_Value",	CORRECT),
		new AttemptTest("table1_C6R1",	"UpdateTable",	"6",	CORRECT)
	};
	public void testInsertNodeAbove(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/startStateVariablesGraph.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
		ProblemEdge insertEdge = controller.getProblemModel().getEdge(9);
		insertEdge.getLinkEditFunctions().testProcessInsert(true);
		sink.getLatestMsgs();
        runMTTests(InsertNodeAboveTests, "InsertNodeAbove");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));	
	}
	private static MTTest[] InsertNodeBelowTests = {
		new AttemptTest("table1_C6R1",	"UpdateTable",	"6",	CORRECT),
		new AttemptTest("No_Selection",	"No_Action", "No_Value",	CORRECT)
	};
	public void testInsertNodeBelow(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/startStateVariablesGraph.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
		ProblemEdge insertEdge = controller.getProblemModel().getEdge(9);
		insertEdge.getLinkEditFunctions().testProcessInsert(false);
		sink.getLatestMsgs();
        runMTTests(InsertNodeBelowTests, "InsertNodeBelow");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));	
	}
	private static MTTest[] ChangeActionTypeTests = {
		new AttemptTest("table1_C3R1",	"UpdateTable",	"3", INCORRECT, true)
	};
	public void testChangeActionType(){
		String problemFileLocation = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/startStateVariablesGraph.brd";
		controller.openBRDFileAndSendStartState(problemFileLocation,null);
		ProblemEdge changeEdge = controller.getProblemModel().getEdge(3);
		changeEdge.getLinkEditFunctions().testChangeActionTypeToBuggy("Buggy Action");
		changeEdge.getEdgeData().setBuggyMsg("WTTTTTTFFF!~!!");
		ProblemEdge edge9 = controller.getProblemModel().getEdge(9);
		ProblemEdge edge21 = controller.getProblemModel().getEdge(21);
		assertTrue(edge9.isPreferredEdge()||edge21.isPreferredEdge());
		assertTrue(changeEdge.isBuggy());
		changeEdge = controller.getProblemModel().getEdge(3);
		assertFalse(changeEdge.isPreferredEdge());
		sink.getLatestMsgs();
        //runMTTests(ChangeActionTypeTests, "ChangeActionType");
        doDemoModeStartAndDoneStateClick(controller.getProblemModel().getNode("Done"));	
	}
	//change action type.. insertnodeabove/below
	/**
	 * Run a list of MTTests.
	 * @param  tests array of MTTests to run
	 * @param  listName name of test list for log output
	 */
	public void runMTTests(MTTest[] tests, String listName) {
		testListName = listName;
		BufferedReader promptRdr = new BufferedReader(new InputStreamReader(System.in));
		for (currTest = 0; currTest < tests.length; currTest++) {
			long startTime = (new Date()).getTime();
			MTTest t = tests[currTest];
			Vector msgs = runProcessPseudoTutorInterfaceAction(t.selection, t.action, t.input, t.actor);
			System.out.println("S = " + t.selection + "A = " + t.action + "I = " + t.input);
			t.checkResult(tests, currTest, msgs);
		}
	}
	
	private void doDemoModeStartAndDoneStateClick(ProblemNode done){
		String demo = CtatModeModel.DEMONSTRATING_SOLUTION;
		String etm = CtatModeModel.EXAMPLE_TRACING_MODE;
		String testing = CtatModeModel.TESTING_TUTOR;
		CtatModeEvent.SetModeEvent changeToDemoMode = new CtatModeEvent.SetModeEvent(etm,etm, demo,testing);
		controller.ctatModeEventOccured(changeToDemoMode);
		ProblemNode start =controller.getExampleTracerGraph().getStartNode().getProblemNode();
		controller.problemNodeClicked(start);
		assertTrue(controller.getSolutionState().getCurrentNode() == start);
		controller.problemNodeClicked(done);
		assertTrue(controller.getSolutionState().getCurrentNode() == done);
	}
	/**
	 * Test method for 'edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandler.processPseudoTutorInterfaceAction(Vector, Vector, Vector)'
	 * @param selection selection vector
	 * @param action action vector
	 * @param input input vector
	 * @return list of {@link MessageObject}s generated
	 */
	public Vector runProcessPseudoTutorInterfaceAction(Vector selection,
			Vector action, Vector input, String actor) {
		pseudoTutorMessageHandler.processPseudoTutorInterfaceAction(selection,action, input);
		return sink.getLatestMsgs();
	}
}
