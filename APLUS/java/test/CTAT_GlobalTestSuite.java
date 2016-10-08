/*
 * Created on Apr 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */



import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.GlobalTestSuiteManager;
import edu.cmu.pact.Utilities.trace;


/**
 * @author mpschnei
 *
 */
public class CTAT_GlobalTestSuite {
	
	/** Root of the test directory subtree, relative to current directory. */
	private static String testRootDir = "test";

	public static Test suite() throws SecurityException, IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException { 

        trace.setTraceLevel(0);

        GlobalTestSuiteManager mgr = new GlobalTestSuiteManager(testRootDir);
        TestSuite suite;
        
        suite = mgr.getGlobalTestSuite();
        
		return suite;
	}

	/** Command-line usage. */
	private static final String usageMsg =
		"Usage: java CTAT_GlobalTestSuite [-h] [-n] [-d debugCode,...] [testRootDir]\n"+
		"where--\n"+
		"  -h means print this help message and exit;\n"+
		"  debugCode,... is a comma-separated list of trace labels;\n"+
		"  -n means form the suite of tests but don't execute them;\n"+
		" testRootDir is the root of the test directory tree.";
	
    /**
     * Command-line usage: see {@link #usageMsg}.
     */
    public static void main(String args[]) {
    	boolean noexecute = false;
    	int i;
    	for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
    		if (args[i].length() < 2)
    			continue;
    		switch(args[i].charAt(1)) {
    		case 'd':
    			if (i+1 < args.length)
    				trace.addDebugCodes(args[++i]);
    			break;
    		case 'h':
    			System.out.print(usageMsg);
    			return;
    		case 'n':
    			noexecute = true;
    			break;
    		default:
    			trace.err("Unknown command-line switch \""+args[i]+"\" "+usageMsg);
    		}
    	}
    	try {
    		if (i < args.length)
    			testRootDir = args[i];
            Test suite = suite();
            if (!noexecute)
            	junit.textui.TestRunner.run(suite);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

}
