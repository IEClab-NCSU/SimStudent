/**
 *	ActionLabelHandlerTest - 	This class is supposed to test the functions in
 *								ActionLabelHandler.java.
 *	Author: Kevin Zhao (kzhao)
 *
 *	Current Tests Exist for the Following Methods:
 *		processInsertNodeAbove()
 *		processInsertNodeBelow()
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class ActionLabelHandlerTest extends TestCase{
	
	/** Controller for this test. */
	BR_Controller controller = null;
	
	CTAT_Launcher ctatLauncher;
	
	/** This will represent the suite for the test */
	public static Test suite() {
		return new TestSuite(ActionLabelHandlerTest.class);
	}
	
	/** This is the default SetUp for this class */
	protected void setUp() throws Exception {
		super.setUp();
		
		CTAT_Launcher ctatLauncher = new CTAT_Launcher(new String[0]);
		controller = ctatLauncher.getFocusedController();
		
        controller.getProblemModel().setUseCommWidgetFlag(false);
	}

	/**
	 * Remove the members established by {@link #setUp()}.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		controller = null;
	}
	
	/* This method will the processInsertNodeAbove() */
	public void testProcessInsertNodeAbove() {
		
		//This will load the BRD into the BR_Controller. For convenience, we will use the 678plus187Student.brd
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187Student.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        //Tracing for fun
        trace.out("ActionLabelHandlerTest" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        //Now, actually open the BRD into the controller
        controller.openBRFromURL(url.toString());
        
        //Now, check if there doesn't exist the newest edge
        assertNull(controller.getProblemModel().getProblemGraph().lookupProblemEdgeByID(22));
        
        //Now get an edge ID and test it ...
        controller.getProblemModel().getProblemGraph().lookupProblemEdgeByID(1).getLinkEditFunctions().processInsertNodeAbove2(true);
        
        //Now check if its there
        assertNotNull(controller.getProblemModel().getProblemGraph().lookupProblemEdgeByID(22));
	}
	
	/* This method will the processInsertNodeBelow() */
	public void testProcessInsertNodeBelow() {
		
		//This will load the BRD into the BR_Controller. For convenience, we will use the 678plus187Student.brd
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187Student.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        //Tracing for fun
        trace.out("ActionLabelHandlerTest" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        //Now, actually open the BRD into the controller
        controller.openBRFromURL(url.toString());
        
        //Now, check if there doesn't exist the newest edge
        assertNull(controller.getProblemModel().getProblemGraph().lookupProblemEdgeByID(22));
        
        //Now get an edge ID and test it ...
        controller.getProblemModel().getProblemGraph().lookupProblemEdgeByID(1)
        	.getLinkEditFunctions().processInsertNodeAbove2(false);
        
        //Now check if its there
        assertNotNull(controller.getProblemModel().getProblemGraph().lookupProblemEdgeByID(22));
	}
}
