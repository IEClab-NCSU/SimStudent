package edu.cmu.pact.BehaviorRecorder.ProblemModel;

/*
 * Created on May 30, 2005
 *
 */

import java.awt.event.FocusEvent;
import java.awt.event.TextEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.CtatModeModel;

/**
 * This is a sample template file to use when creating a new test case.
 * 
 */
public class JDomReaderTest extends TestCase {

    private final String _66_PLUS_33 = "test/66+33.brd";

    private BR_Controller controller;

    private ProblemStateReader reader;
    
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
				if (controller.getActiveWindow() != null)
					controller.getActiveWindow().dispose();				
				if (controller.getStudentInterface().getActiveWindow() != null)
					controller.getStudentInterface().getActiveWindow().dispose();
			}
		});
    }

    
    public void testJDom() {

    	trace.addDebugCodes ("jdom,br");
        final CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
        controller = launcher.getFocusedController();
        reader = controller.getProblemStateReader();


//        controller.setBehaviorRecorderMode(BR_Controller.PSEUDO_TUTOR_MODE);
        launcher.launch(new TestPanel());

        controller.openBRDFileAndSendStartState(_66_PLUS_33, null);

        final ProblemModel problemModel = controller.getProblemModel();

        assertTrue(problemModel.getStartNodeCreatedFlag());

        assertFalse(controller.isFirstCheckAllStatesFlag());

        assertFalse(problemModel.isCaseInsensitive());
        assertFalse(problemModel.isUnorderedMode());

        final ProblemGraph problemGraph = problemModel.getProblemGraph();

        assertEquals(5, problemGraph.getNodeCount());
        assertEquals(4, problemGraph.getEdgeCount());

        final ProblemNode startNode = problemModel.getStartNode();
        assertEquals("66+33", startNode.getNodeView().getText());

        List outgoingEdges1 = startNode.getOutgoingEdges();
        assertEquals(2, outgoingEdges1.size());

        ProblemEdge correctEdge, incorrectEdge;

        ProblemEdge e1 = (ProblemEdge) outgoingEdges1.get(0);
        if (e1.dest.getNodeView().getText().equals("state1")) {
            correctEdge = e1;
            incorrectEdge = (ProblemEdge) outgoingEdges1.get(1);
        } else {
            correctEdge = (ProblemEdge) outgoingEdges1.get(1);
            incorrectEdge = e1;
        }

        EdgeData incorrectEdgeData = incorrectEdge.getEdgeData();
        assertEquals("Buggy Action", incorrectEdgeData.getActionType());
        assertEquals("6 + 3 = 9", incorrectEdgeData.getBuggyMsg());

        EdgeData correctEdgeData = correctEdge.getEdgeData();

        Vector skills = correctEdgeData.getSkills();
        assertEquals(1, skills.size());
        assertEquals("Add_two_numbers_no_carry Addition", skills.get(0));

        trace.out("skills = " + correctEdgeData.getSkills());

        ProblemNode expectedCurrentProblemNode = correctEdge.getNodes()[1];

        final Vector action = correctEdgeData.getAction();
        final Vector selection = correctEdgeData.getSelection();
        final Vector input = correctEdgeData.getInput();

        assertEquals(1, action.size());
        assertEquals("UpdateTable", action.get(0));

        assertEquals(1, selection.size());
        assertEquals("Table0_C4R4", selection.get(0));

        assertEquals(1, input.size());
        assertEquals("9", input.get(0));

        JCommTable table = (JCommTable) controller.getCommWidget("Table0");
        assertNotNull(table);


//        assertEquals(19, controller.getCommWidgetTable().size());

        TableCell cell_3_3 = table.getCell(3, 3);
        assertNotNull(cell_3_3);

        TableCell cell_2_3 = table.getCell(2, 3);
        assertNotNull(cell_2_3);
        assertEquals("3", cell_2_3.getText());
        
        sleep(1000);

        // Simulate user entering 9 in cell (3,3) by setting the text and
        // then generating first a text changed event (to set JCommTable's
        // dirty flag) and then a focus lost event, as if the user clicked on 
        // cell (2,3) after typing the "9".
        cell_3_3.setText("9");
        table.textValueChanged(new TextEvent(cell_3_3, TextEvent.TEXT_VALUE_CHANGED));
        table.focusLost(new FocusEvent(cell_3_3, FocusEvent.FOCUS_LOST,
        		false, cell_2_3));
        
//        ProblemNode currentNode = controller.getSolutionState()
//                .getCurrentNode();
//        controller.getExampleTracer().
        
//      ArrayList interpretations = controller.getExampleTracer().getInterpretations();
//		ExampleTracerInterpretation intepretation = (ExampleTracerInterpretation) interpretations.get(0);
//  	ExampleTracerNode currentExampleTracerNode = intepretation.getCurrentState();
//		int foundNodeID = currentExampleTracerNode.getNodeID();
//		
//      assertEquals(expectedCurrentProblemNode.getUniqueID(), foundNodeID);
//
//      ProblemNode foundCurrentProblemNode = currentExampleTracerNode.getProblemNode();
        ProblemNode foundCurrentProblemNode = controller.getExampleTracer().getCurrentNode(false); 
        assertSame(expectedCurrentProblemNode, foundCurrentProblemNode);
        
        assertEquals("9", cell_3_3.getText());
        // Set the state by clicking a node in the Behavior Recorder
        // Then check that the right values show up in the table
        // controller.setCurrentNode2(controller.getProblemModel().getStartNode());
        trace.out("inter", "============================================");
        controller.goToStartState();
        assertEquals("3", cell_2_3.getText());
        cell_3_3 = table.getCell(3, 3);
        // assertNotNull (cell_3_3);
        // assertEquals ("", cell_3_3.getText().trim());

        controller.setBehaviorRecorderMode(CtatModeModel.EXAMPLE_TRACING_MODE);

        ProblemNode state1Node = controller.getProblemModel().getNode("state1");
        controller.setCurrentNode2(state1Node);
        assertSame(controller.getSolutionState().getCurrentNode(), state1Node);

        ProblemNode state2Node = controller.getProblemModel().getNode("state2");
        controller.setCurrentNode2(state2Node);
        assertSame(controller.getSolutionState().getCurrentNode(), state2Node);

        ProblemNode doneNode = controller.getProblemModel().getNode("state3");
        JCommButton doneButton = (JCommButton) controller
                .getCommWidget("Done");
        controller.getUniversalToolProxy().sendMessage(
                doneButton.getCurrentStateMessage());

//        assertSame(doneNode, controller.getSolutionState().getCurrentNode());

    }

    /**
     * Delay for multithreading problems.
     * @param i sleep time in ms
     */
    private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		
	}


	public static Test suite() {
        // Any void method that starts with "test"
        // will be run automatically using this construct
        return new TestSuite(JDomReaderTest.class);
    }

}
