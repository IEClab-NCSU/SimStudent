package AdditionTutorChaining;

/*
 * Created on May 30, 2005
 *
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This is a sample template file to use when creating a new test case.
 * 
 */
public class TutorInterfaceTest extends TestCase {

    protected void setUp() throws Exception {
    }

    public static Test suite() {
        // Any void method that starts with "test" 
        // will be run automatically using this construct
        return new TestSuite(TutorInterfaceTest.class);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

	public void testDummy() {}
}
