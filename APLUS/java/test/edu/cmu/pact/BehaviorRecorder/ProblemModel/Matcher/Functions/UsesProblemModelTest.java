/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest.SinkToolProxy;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.Utilities.Utils;

/**
 * @author sewall
 *
 */
public class UsesProblemModelTest extends TestCase {

	public static Test suite() {
        TestSuite suite = new TestSuite(UsesProblemModelTest.class);
        return suite;
    }
	
	private CTAT_Launcher launcher = null;
	private BR_Controller controller = null;
	private SinkToolProxy sink = null;

	/**
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		launcher = new CTAT_Launcher(new String[0]);
		controller = launcher.getFocusedController();
		sink = new SinkToolProxy(controller); 
        controller.setUniversalToolProxy(sink);
	}

	/**
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		controller = null;
		sink = null; 
		launcher = null;
	}

	/**
	 * Test method for {@link edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesProblemModel#setProblemModel(edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel)}.
	 */
	public final void testSetProblemModel() {
		regex_matcher af = null;

		// test the author function
		{
	        controller.getProblemModel().setUseCommWidgetFlag(false);
	    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187.brd";
	        URL url = Utils.getURL(problemFileLocation, this);
	        controller.openBRFromURL(url.toString());
		}
		af = _test1();
		af = _test2();
		{
	        controller.getProblemModel().setUseCommWidgetFlag(false);
	    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/Controller/678plus187Student.brd";
	        URL url = Utils.getURL(problemFileLocation, this);
	        controller.openBRFromURL(url.toString());
		}
		af = _test3();

		// test whether the session storage performed
		Map<String, Object> ss = controller.getSessionStorage();
		assertEquals("sessionStorage.size() after 1 fn", 1, ss.size());
		Map<String, Pattern> af1ss = (Map<String, Pattern>) ss.get(af.getClass().getName());
		assertEquals("af1ss.size() after 2 patterns", 2, af1ss.size());
	}
	
	private regex_matcher _test1() {		// test the author function
		regex_matcher af = new regex_matcher(); // author function
		af.setProblemModel(controller.getProblemModel());
		assertTrue("af.regex(.*fix.*, suffix)", af.regexMatch(".*fix.*", "suffix"));
		return af;
	}
	
	private regex_matcher _test2() {		// test the author function
		regex_matcher af = new regex_matcher(); // author function
		af.setProblemModel(controller.getProblemModel());
		assertFalse("af.regex(fix.*, prefix)", af.regexMatch(".*blix.*", "prefix"));		
		return af;
	}
	
	private regex_matcher _test3() {		// test the author function
		regex_matcher af = new regex_matcher(); // author function
		af.setProblemModel(controller.getProblemModel());
		assertTrue("af.regex(.*fix.*, prefix)", af.regexMatch(".*fix.*", "prefix"));
		return af;
	}
}

/**
 * Example of an author function class that uses the
 * {@link ProblemModel#getSessionStorage()} facility. This function tests strings
 * against regular expressions. It stores the compiled regular expressions in a
 * HashMap<String, Pattern> where the key is the regex and the value is the Pattern
 * instance holding the compiled form of that regex.
 * @author sewall
 */
class regex_matcher implements UsesProblemModel {
	
	/** For getting our {@link ProblemModel#getSessionStorage()} map. */
	private ProblemModel problemModel;
	
	/**
	 * Mark this function as needing the {@link ProblemModel} from the runtime.
     * @param pm new value for {@link #problemModel}
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesProblemModel#setProblemModel(edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel)
	 */
	public void setProblemModel(ProblemModel pm){
		this.problemModel = pm;
	}
	
	/**
	 * Tell if a string matches a regular expression. Save compiled regexes for future
	 * use in a {@link ProblemModel#getSessionStorage()} facility named by my class name. 
	 * @param regex regular expression for recognizer
	 * @param toRecognize string to try to recognize
	 * @return true if pattern recognizes string
	 */
	public boolean regexMatch(String regex, String toRecognize) {
		Pattern p;
		if (regex == null)                               // avoid null pointer exceptions
			return (toRecognize == null);        // declare match if both arguments null?

		if (toRecognize == null)      // a null test string matches no regular expression
			return false;

		synchronized (getClass()) {    // mutex against other instances of this author fn
			
			String ssName = getClass().getName();              // name my session storage
			Map<String, Pattern> ss =                           // get my session storage
				(Map<String, Pattern>) problemModel.getSessionStorage().get(ssName);
			if (ss == null)                     // on 1st call, create my session storage
				problemModel.getSessionStorage().put(ssName, ss = new HashMap<String, Pattern>());
			
			p = ss.get(regex);    // now, in my session storage, see if I have this regex
			if (p == null)                            // if not, compile & store it there
				ss.put(regex, p = Pattern.compile(regex));
		}
		return p.matcher(toRecognize).matches();
	}
}
