/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat.model;

import java.io.File;
import java.net.URL;

import javax.swing.SwingUtilities;

import pact.CommWidgets.StudentInterfaceWrapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeListener;
import edu.cmu.pact.BehaviorRecorder.Dialogs.OpenInterfaceDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.TestPanel;
import edu.cmu.pact.Utilities.Utils;

public class  CtatModeModelTest extends TestCase implements CtatModeListener {

	SingleSessionLauncher launcher = null; 
		
	/** Last event generated. */
	private CtatModeEvent.SetModeEvent lastEvt;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

    public static Test suite() {
        return new TestSuite(CtatModeModelTest.class);
    }

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
				if (launcher == null)
					return;
				BR_Controller ctlr = launcher.getController();
				if (ctlr == null)
					return;
				if (ctlr.getActiveWindow() != null)
					ctlr.getActiveWindow().dispose();
				StudentInterfaceWrapper siw = ctlr.getStudentInterface();
				if (siw != null &&  siw.getActiveWindow() != null)
					siw.getActiveWindow().dispose();
			}
		});
    }

	/**
	 * Test method for {@link CtatModeModel#CtatModeModel(BR_Controller)}.
	 */
	public void testCtatModeModel() throws ProblemModelException {
		launcher = new CTAT_Launcher(new String[0]).getFocusedController().getLauncher();
		launcher.launch(new TestPanel());
		BR_Controller controller = launcher.getController();

        controller.setDeletePrFile(true); //Gustavo 6Nov2007: this prevents the PR dialog
                
		CtatModeModel cmm = controller.getCtatModeModel();
		controller.addCtatModeListener(this);

		assertEquals("initial tutor type", CtatModeModel.EXAMPLE_TRACING_MODE,
				cmm.getCurrentMode());
		assertEquals("initial author mode", CtatModeModel.DEFINING_START_STATE,
				cmm.getCurrentAuthorMode());
		lastEvt = null;
		controller.setBehaviorRecorderMode(CtatModeModel.TDK_MODE);
		assertNotNull("TDK event received", lastEvt);
		assertEquals("TDK mode correct", CtatModeModel.TDK_MODE, lastEvt.getMode());
		
		lastEvt = null;
		controller.setBehaviorRecorderMode(CtatModeModel.EXAMPLE_TRACING_MODE);
		assertEquals("Ex-Tracing event mode", CtatModeModel.EXAMPLE_TRACING_MODE,
				lastEvt.getMode());
		assertEquals("Ex-Tracing event author mode", CtatModeModel.DEFINING_START_STATE,
				lastEvt.getAuthorMode());
		assertEquals("Ex-tracing author mode", CtatModeModel.DEFINING_START_STATE,
				cmm.getCurrentAuthorMode());
		controller.createStartState("CtatModeModelTest");
		assertEquals("Ex-tracing author mode", CtatModeModel.DEMONSTRATING_SOLUTION,
				cmm.getCurrentAuthorMode());
		
		lastEvt = null;
		OpenInterfaceDialog.openInterface(new File("./AdditionTutorChaining/TutorInterface.class"),
				controller);
		URL brdLocation = Utils.getURL("OneEdge.brd", this);
		assertNotNull("brd location OneEdge.brd", brdLocation);
		controller.openBRFromURL(brdLocation.toString());
		assertEquals("Test Tutor event after read brd", CtatModeModel.TESTING_TUTOR,
				lastEvt.getAuthorMode());
		assertEquals("Test Tutor author mode after read brd", CtatModeModel.TESTING_TUTOR,
				cmm.getCurrentAuthorMode());
		
		lastEvt = null;
		controller.setBehaviorRecorderMode(CtatModeModel.SIMULATED_STUDENT_MODE);
		assertEquals("SimSt event mode", CtatModeModel.SIMULATED_STUDENT_MODE,
				lastEvt.getMode());
		assertEquals("SimSt event author mode", CtatModeModel.DEFINING_START_STATE,
				lastEvt.getAuthorMode());
		assertEquals("Ex-tracing author mode", CtatModeModel.DEFINING_START_STATE,
				cmm.getCurrentAuthorMode());
		
	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.getModeComboBoxModel()'
	 */
	public void testGetModeComboBoxModel() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.getAuthorModeComboBoxModel()'
	 */
	public void testGetAuthorModeComboBoxModel() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.getDemonstrateButtonModel()'
	 */
	public void testGetDemonstrateButtonModel() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.getModeTypeList()'
	 */
	public void testGetModeTypeList() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.setAuthorMode(String)'
	 */
	public void testSetAuthorMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.setMode(String)'
	 */
	public void testSetMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.isRuleEngineTracing()'
	 */
	public void testIsRuleEngineTracing() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.isJessTracing()'
	 */
	public void testIsJessTracing() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.isJessMode()'
	 */
	public void testIsJessMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.isExampleTracingMode()'
	 */
	public void testIsExampleTracingMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.setDemonstrateMode(boolean)'
	 */
	public void testSetDemonstrateMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.isDemonstrateMode()'
	 */
	public void testIsDemonstrateMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.isTDKMode()'
	 */
	public void testIsTDKMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.isSimStudentMode()'
	 */
	public void testIsSimStudentMode() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.getModeTitle()'
	 */
	public void testGetModeTitle() {

	}

	/*
	 * Test method for 'edu.cmu.pact.ctat.model.CtatModeModel.getCurrentMode()'
	 */
	public void testGetCurrentMode() {

	}

	/**
	 * Test mode listeners.
	 * @param e
	 */
	public void ctatModeEventOccured(CtatModeEvent e) {
		if (e instanceof CtatModeEvent.SetModeEvent)
			lastEvt = (CtatModeEvent.SetModeEvent) e;
	}

}
