package edu.cmu.pact.BehaviorRecorder.Controller;


import javax.swing.SwingUtilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.TestPanel;

/**
 * @author mpschnei
 * 
 */
public class BR_ControllerTest extends TestCase {

	private CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
	
    public static Test suite() {
        TestSuite suite = new TestSuite(BR_ControllerTest.class);
        return suite;
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
				try {
					BR_Controller ctlr = launcher.getFocusedController();
					if (ctlr.getActiveWindow() != null)
						ctlr.getActiveWindow().dispose();
					if (ctlr.getStudentInterface() != null && ctlr.getStudentInterface().getActiveWindow() != null)
						ctlr.getStudentInterface().getActiveWindow().dispose();
				} catch (Exception e) {}
			}
		});
    }

    public void testCreateController() {
    	launcher.getFocusedController();
    }
    
    public void testBadStartStateName() {
    	//launcher = new SingleSessionLauncher(false);
    	launcher.launch(new TestPanel());
        BR_Controller controller = launcher.getFocusedController();
        
        controller.initAllWidgets_movedFromCommWidget();
        try {
            controller.createStartState("goodname");
        } catch (Exception e1) {
        	e1.printStackTrace();
            fail();
        }

        try {
            controller.createStartState("duplicateName");
            fail();
        } catch (Exception e) {
        }

        try {
            controller.createStartState("bad name with spaces");
            fail();
        } catch (Exception e) {
        }

      //  launcher = new SingleSessionLauncher(false);
    	launcher.launch(new TestPanel());
        controller = launcher.getFocusedController();

        try {
            controller.createStartState("badnamewith!@#$@#");
            fail();
        } catch (ProblemModelException e) {
        }
    }
}