package edu;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEventTest;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReaderTest;
import edu.cmu.pact.Utilities.trace;

/*
 * Created on Jan 19, 2006
 *
 */

public class GlobalTestSuiteManager {
	
	/** Root of the test directory subtree, relative to current directory. */
	private String testRootDir = "test";
	
	public GlobalTestSuiteManager() {}

	/**
	 * Constructor sets all fields.
	 * @param testRootDir
	 */
	public GlobalTestSuiteManager(String testRootDir) {
		this.testRootDir = testRootDir;
	}

    // Process all files and directories under dir
    private void loadAllFiles(File dir, List list) {
        list.add(dir);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                loadAllFiles(new File(dir, children[i]), list);
            }
        }
    }

    private List getTestFiles() {
    	final String cvsInPath = File.separator+"CVS"+File.separator;
        List fileList = new ArrayList();
        List testList = new ArrayList();
        loadAllFiles(new File(testRootDir), fileList);
        for (Iterator files = fileList.iterator(); files.hasNext();) {
            File file = (File) files.next();
            String absPath = file.getAbsolutePath();
            if (absPath.indexOf(cvsInPath) >= 0)
            	continue;
            if (absPath.endsWith("Test.java"))
                testList.add(file);
        }
        return testList;
    }

    List getTestClasses() throws ClassNotFoundException {
    	String slash;
    	if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
    		slash = "\\";
    	else
    		slash = "/";
    	
        List testFiles = getTestFileNames();
        List testClasses = new ArrayList();
        for (Iterator i = testFiles.iterator(); i.hasNext();) {
            String className = (String) i.next();
            className = replace(className, slash, ".");
            Class cls = Class.forName(className);
            if (cls.getPackage() == null)
                continue;
            testClasses.add(cls);
        }
        Comparator c = new Comparator() {
            public int compare(Object o1, Object o2) {
                Class c1 = (Class) o1;
                Class c2 = (Class) o2;
                return c1.getName().compareTo(c2.getName());
            }
        };
        Collections.sort(testClasses, c);
        return testClasses;
    }

    private String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    private List getTestFileNames() {
    	String test;
    	if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
    		test = "test\\";
    	else
    		test = "test/";
        List files = getTestFiles();
        List filenames = new ArrayList();
        for (Iterator i = files.iterator(); i.hasNext();) {
            File f = (File) i.next();
            String path = f.getPath();
            path = path.substring(test.length(), path.length());
            path = path.substring(0, path.length() - ".java".length());
            filenames.add(path);
        }
        return filenames;
    }

    /**
     * Scans the folder "test/" and subfolders and finds all files ending with
     * "Test.java". The files are read in off the classpath and added to the
     * return test suite.  The tests are then alphabetized and returned 
     * as a test suite.
     * 
     * @return A test suite containing all test classes in "test/"
     */
    public TestSuite getGlobalTestSuite() throws ClassNotFoundException,
            SecurityException, InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException {
        TestSuite suite = new TestSuite();
        List testList = new ArrayList();
        List tests =  getTestClasses(); // Arrays.asList(new Class[] {ProblemModelEventTest.class, ProblemStateReaderTest.class});
        for (Iterator t = tests.iterator(); t.hasNext();) {
            Class c = (Class) t.next();
        	trace.out("test", "test class: "+c.getName());
            TestCase testCase; 
            try {
            	Object testObject = c.newInstance();
            	if (! (testObject instanceof TestCase)) {
            		trace.err ("testObject " + testObject + " is not a TestCase");
            		continue;
            	}
            		
            	testCase = (TestCase) testObject;
            } catch (Exception e) {
            	trace.errStack("Could not instantiate "+c+":\n  "+e+"; cause: "+e.getCause(), e);
            	continue;
            }
            
            TestSuite suite2 = null;
            try {
            	Method m = testCase.getClass().getMethod("suite", null);
            	if (m == null) {
            		trace.err("no suite() method in class "+testCase.getClass().getName());
            		continue;
            	}
            	Test testSuite = (Test) m.invoke(testCase, null);
            	suite2 = (TestSuite) testSuite;
            } catch (Throwable e) {
            	trace.errStack("Error running suite() method in testCase "+testCase+":\n  "+e+"; cause: "+e.getCause(), e);
            	continue;
            }
            Enumeration e = suite2.tests();
            while (e.hasMoreElements()) {
            	Object test = e.nextElement();
            	trace.out("test", "test: "+test);
                testList.add(test);
            }
        }
        Comparator c = new Comparator() {
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        };
        Collections.sort(testList, c);
        for (Iterator testIt = testList.iterator(); testIt.hasNext();) {
            Test nextElement = (Test) testIt.next();
            suite.addTest(nextElement);
        }
        return suite;
    }

    public static void main(String args[]) {
        try {
        	GlobalTestSuiteManager mgr = (args.length > 0 ?
        			new GlobalTestSuiteManager(args[0]): new GlobalTestSuiteManager());
            mgr.getGlobalTestSuite();
        } catch (Exception e) {
            System.err.println(e + " " + e.getCause());
            e.printStackTrace(System.err);
        }
    }
}
