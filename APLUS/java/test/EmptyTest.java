/*
 * Created on May 30, 2005
 *
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This is a sample template file to use when creating a new test case.
 * 
 */
public class EmptyTest extends TestCase {

    public void testSampleTest() {

    }

    protected void setUp() {
    }

    public static Test suite() {
        // Any void method that starts with "test" 
        // will be run automatically using this construct
        return new TestSuite(EmptyTest.class);
    }

}
