package edu.cmu.pact.BehaviorRecorder;

/*
 * Created on May 30, 2005
 *
 */

import javax.swing.JPanel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;

/**
 * This is a sample template file to use when creating a new test case.
 * 
 */
public class InterfaceSizeTest extends TestCase {

    private static final int HEIGHT = 100;
    private static final int WIDTH = 200;

    private class TestPanel extends JPanel {
        CTAT_Options options = new CTAT_Options();
        
        public TestPanel() {
            setLayout(null);
            options.setInterfaceHeight(InterfaceSizeTest.HEIGHT);
            options.setInterfaceWidth(InterfaceSizeTest.WIDTH);
            add(options);
        } 
    }

    public void testInterfaceSize() {
        CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
        launcher.launch(new TestPanel());
        StudentInterfaceWrapper wrapper = launcher.getFocusedController().getStudentInterface();
        assertEquals (HEIGHT, wrapper.getHeight());
        assertEquals (WIDTH, wrapper.getWidth());
    }

    public static Test suite() {

        return new TestSuite(InterfaceSizeTest.class);
    }


}
