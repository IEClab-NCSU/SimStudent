package edu;

/*
 * Created on May 30, 2005
 *
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Owner
 * 
 */
public class GlobalTestSuiteTest extends TestCase {


    public void testGetTestClasses() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
    	GlobalTestSuiteManager mgr = new GlobalTestSuiteManager();
        List fileList = mgr.getTestClasses();
        assertFalse(fileList.size() == 0);
        for (Iterator i = fileList.iterator(); i.hasNext();) {
            Class c = (Class) i.next();
            Object o = c.newInstance();
            assertNotNull (c.getPackage());
            if (!(o instanceof TestCase))
            	System.err.println("Not a JUnit TestCase: "+c);
            else {
            	Method m = c.getMethod("suite",null);
            	assertNotNull (m);
            }
        }
    }

    public void testGetTestSuite() throws SecurityException, IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    	GlobalTestSuiteManager mgr = new GlobalTestSuiteManager();
        TestSuite testSuite = mgr.getGlobalTestSuite();
        TestCase lastTest = null;
        for (Enumeration tests = testSuite.tests(); tests.hasMoreElements();) {

            TestCase thisTest = (TestCase) tests.nextElement();
            if (lastTest != null) {
                final int compareTo = lastTest.toString().compareTo(thisTest.toString());
                assertTrue (compareTo <= 0);
            }
            lastTest = thisTest;
        }
    }
    
    public static Test suite() {
        return new TestSuite(GlobalTestSuiteTest.class);
    }

}
