package edu.cmu.pact.BehaviorRecorder.Controller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelTest;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 * 
 */
public class ServerTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(ServerTest.class);
        return suite;
    }

    public void testCreateController() {

        trace.addDebugCode ("inter");
        trace.addDebugCode ("mps");
        trace.addDebugCode ("mo");
        
        Runnable r = new Runnable() {
            public void run() {
            	CTAT_Launcher launcher1 = new CTAT_Launcher(new String[0]);
                ProblemModelTest test1 = new ProblemModelTest();
                test1.runTestPsuedoTutorModelTracing(launcher1);
            }
        };
        
        new Thread(r).start();

        CTAT_Launcher launcher2 = new CTAT_Launcher(new String[0]);
        ProblemModelTest test2 = new ProblemModelTest();
        test2.runTestPsuedoTutorModelTracing(launcher2);
    }
}