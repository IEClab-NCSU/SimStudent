/*
 * $Id: UtilsTest.java 19702 2013-11-07 21:07:28Z sdemi $
 */
package edu.cmu.pact.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Dialogs.BrdFilter;


/**
 * Test for Utils. 
 */
public class UtilsTest extends TestCase {


	public UtilsTest() {
		
	}
	
	/**
	 * Call JUnit Test constructor, then construct a listener process.
	 *
	 * @param  superArg arg for superclass constructor
	 */
	public UtilsTest(String arg) {
		super(arg);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(UtilsTest.class);
//		suite.addTest(new UtilsTest("differenceOfSetsTest"));
//		suite.addTest(new UtilsTest("expandPropertyReferencesTest"));
//		//suite.addTest(new UtilsTest("getFileAsResourceTest"));
//		suite.addTest(new UtilsTest("getCodeBaseURLTest"));
//		suite.addTest(new UtilsTest("resolveToInterfaceHomeTest"));
		return suite;
	}
	
	public void testAppendSlash() {
		String s;
		assertEquals(s="fred", "fred/", Utils.appendSlash(s));
		assertEquals(s="fred/", "fred/", Utils.appendSlash(s));
		assertEquals(s="\\fred", "\\fred\\", Utils.appendSlash(s));
		assertEquals(s="\\fred/", "\\fred/", Utils.appendSlash(s));
		assertEquals(s="/fred\\", "/fred\\", Utils.appendSlash(s));
		assertEquals(s="/slash\\fred", "/slash\\fred\\", Utils.appendSlash(s));
		assertEquals(s="/fred", "/fred/", Utils.appendSlash(s));
	}
	
	public void testAddHtmlComment() {
		String[][] tests =  {
				{ "plain text", "comment", "<html>plain text <!-- comment --> </html>" },
				{ "<html> text </html>", "comment", "<html> text  <!-- comment --> </html>" },
				{ "<HTML> text </HTML>", "comment", "<HTML> text  <!-- comment --> </HTML>" },
				{ "<HTML> text </HTML>", "<!--comment-->", "<HTML> text <!--comment--></HTML>" }
		};
		for(int i = 0; i < tests.length; ++i)
			assertEquals("error at ["+i+"]", tests[i][2], Utils.addHtmlComment(tests[i][0], tests[i][1]));
	}
	
	private static Utils utils = new Utils();
	
	public void testCheckDirnameValid() {
		trace.addDebugCode("mt");
		String path;
		assertTrue(path = "c:\\Program Files\\",
				Utils.isDirectoryReadable(path, utils));
		assertFalse(path = "c:\\Program Files\\Internet Explorer\\iexplore.exe",
				Utils.isDirectoryReadable(path, utils));
		assertTrue(path = "http://earle.pslc.cs.cmu.edu/CTAT/latest",
				Utils.isDirectoryReadable(path, utils));
		assertFalse(path = "http://earle.pslc.cs.cmu.edu/CTAT/does/not/exist",
				Utils.isDirectoryReadable(path, utils));
		assertTrue(path = "https://pact-cvs.pact.cs.cmu.edu",
				Utils.isDirectoryReadable(path, utils));
		assertFalse(path = "https://pact-cvs.pact.cs.cmu.edu/test.html",
				Utils.isDirectoryReadable(path, utils));
		assertTrue(path = "edu/cmu/pact/Preferences",
				Utils.isDirectoryReadable(path, utils));
		assertFalse(path = "edu/cmu/pact/nonsense",
				Utils.isDirectoryReadable(path, utils));
		assertTrue(path = "./",
				Utils.isDirectoryReadable(path, utils));
		assertFalse(path = "./solverTypein.brd",
				Utils.isDirectoryReadable(path, utils));
	}
	
	public void testFindFiles() {
		List<File> result;
		String knownFile = "Projects/FlashFractionAddition/ExampleTracingProblems/1416.brd";
		File f;
		result = Utils.findFiles(new File("Projects"), new BrdFilter());
		if (trace.getDebugCode("util")) trace.out("util", "result.size() "+result.size());
		assertTrue("result.size() "+result.size()+" < 25", result.size() >= 25);
		assertTrue("result.size() "+result.size()+" > 100", result.size() <= 100);
		for (File f0 : result)
			assertTrue("found non-brd file "+f0.getPath(), f0.getPath().endsWith(".brd"));

		f = new File(knownFile);
		assertTrue("missing "+f.getPath(), result.contains(f));

		f = new File("Projects/nonexistent.brd");
		assertFalse("found "+f.getPath(), result.contains(f));

		result = Utils.findFiles(f, new BrdFilter());
		assertTrue("nonexistent file had nonzero result.size() "+result.size(), result.size() == 0);
		
		result = Utils.findFiles(f = new File(knownFile), new BrdFilter());
		assertTrue("missing "+f.getPath(), result.contains(f));
		assertTrue("single retrieval size != 1: "+result.size(), result.size() == 1);
		
		f = new File("classes");
		result = Utils.findFiles(f, new BrdFilter());
		assertTrue("classes dir had >0 .brd files: "+result.size(), result.size() == 0);
		result = Utils.findFiles(f, null);
		assertFalse("classes dir had < 100 files: "+result.size(), result.size() < 100);
	}
	
	public static void testListToHtmlTbl() {
		String[] strs = { "one", "two", "three", "four", "five", "six", "seven", "eight" };
		String prefix = "<table border=\"0\" cellpadding=\"1\">";
		String[] tbls = { // index+1 is table size
				"<tr><td>&nbsp;one</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>",
				"<tr><td>&nbsp;one,</td><td>&nbsp;two</td><td>&nbsp;</td><td>&nbsp;</td>",
				"<tr><td>&nbsp;one,</td><td>&nbsp;two,</td><td>&nbsp;three</td><td>&nbsp;</td>",
				"<tr><td>&nbsp;one,</td><td>&nbsp;two,</td><td>&nbsp;three,</td><td>&nbsp;four</td>",
				"<tr><td>&nbsp;one,</td><td>&nbsp;two,</td><td>&nbsp;three,</td><td>&nbsp;four,</td></tr>"+
				"<tr><td>&nbsp;five</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>",
				"<tr><td>&nbsp;one,</td><td>&nbsp;two,</td><td>&nbsp;three,</td><td>&nbsp;four,</td></tr>"+
				"<tr><td>&nbsp;five,</td><td>&nbsp;six</td><td>&nbsp;</td><td>&nbsp;</td>",
				"<tr><td>&nbsp;one,</td><td>&nbsp;two,</td><td>&nbsp;three,</td><td>&nbsp;four,</td></tr>"+
				"<tr><td>&nbsp;five,</td><td>&nbsp;six,</td><td>&nbsp;seven</td><td>&nbsp;</td>",
				"<tr><td>&nbsp;one,</td><td>&nbsp;two,</td><td>&nbsp;three,</td><td>&nbsp;four,</td></tr>"+
				"<tr><td>&nbsp;five,</td><td>&nbsp;six,</td><td>&nbsp;seven,</td><td>&nbsp;eight</td>",
		};
		String suffix = "</tr></table>";
		
		for (int i = 0; i <= strs.length; ++i) {
			Collection<Object> sList = 
				new ArrayList<Object>(Arrays.asList(Arrays.copyOfRange(strs, 0, i)));
			String result = Utils.listToHtmlTbl(sList, 4);
			if (trace.getDebugCode("util"))
				System.out.printf("[%d] %s\n", i, result);
			if (sList.size() < 1)
				assertEquals("empty list", "", result);
			else {
				assertTrue("prefix for list length "+i, result.startsWith(prefix));
				assertTrue("suffix for list length "+i, result.endsWith(suffix));
				assertEquals("body for list length "+i, tbls[i-1],
						result.substring(prefix.length(), result.length()-suffix.length()));
			}
		}
	}
	
	public void testUpperCaseInitials() {
		String s;
		assertNull("null", Utils.upperCaseInitials(s=null));
		assertEquals("empty", s="", Utils.upperCaseInitials(s));
		assertEquals("numeric", s="2392573029", Utils.upperCaseInitials(s));
		assertEquals("leading digit", s="2fred 9jane", Utils.upperCaseInitials(s));
		assertEquals("one word", s="Fred", Utils.upperCaseInitials(s.toLowerCase()));
		assertEquals("two words", s="Fred Jane", Utils.upperCaseInitials(s.toLowerCase()));
		assertEquals("hyphen", s="Fred-Jane", Utils.upperCaseInitials(s.toLowerCase()));
	}

	/**
	 * Check that {@link Utils#sleep(long)} delays at least as long as told to.
	 */
	public void testSleep() {
		long times[] = { -1, 0, 1, 3, 10, 100, 1000, 9999 };
		for (long ms : times) {
			long elapsed = Utils.sleep(ms);
			assertFalse("sleep("+ms+") too short: "+elapsed, ms > elapsed);
			if (trace.getDebugCode("util"))
				System.out.printf("Utils.sleep(%4d) = %4d\n", ms, elapsed);
		}
	}
	
	/**
	 * Test differenceOfSets().
	 */
	public void testDifferenceOfSets() {
		Set m = new HashSet(), s = new HashSet(), d = null;		
		m.add("a"); m.add("b"); m.add("c"); 
		s.add("b"); s.add("c"); s.add("d");
		d = Utils.differenceOfSets(m, s);
		assertEquals(1, d.size());
		assertTrue(d.contains("a"));
	}

	/**
	 * Try to expand several strings.
	 */
	public void testExpandPropertyReferences() {
		String noRefs = "This string has no references.";
		String undefRefIn  = "An undefined property is ${undefined}.";
		String undefRefOut = "An undefined property is .";
		String oneRefIn  = "The user.dir property is ${user.dir}.";
		String oneRefOut = "The user.dir property is " +
			System.getProperty("user.dir") + ".";
		String twoRefIn  =
			"The user.dir property is ${user.dir}, user.name is ${user.name}.";
		String twoRefOut = "The user.dir property is " +
			System.getProperty("user.dir") + ", user.name is " +
			System.getProperty("user.name") + ".";
		String endRefIn  = "The user.dir property is ${user.dir}";
		String endRefOut = "The user.dir property is " +
			System.getProperty("user.dir");
		String startRefIn  = "${user.dir} is the user.dir property.";
		String startRefOut = System.getProperty("user.dir") +
			" is the user.dir property.";

		String noRefsResult = Utils.expandPropertyReferences(noRefs);
		trace.out("util", "noRefs result: " + noRefsResult);
		assertEquals(noRefs, noRefs);

		String undefRefResult = Utils.expandPropertyReferences(undefRefIn);
		trace.out("util", "undefRef result: " + undefRefResult);
		assertEquals(undefRefOut, undefRefResult);

		String oneRefResult = Utils.expandPropertyReferences(oneRefIn);
		trace.out("util", "oneRef result: " + oneRefResult);
		assertEquals(oneRefOut, oneRefResult);

		String twoRefResult = Utils.expandPropertyReferences(twoRefIn);
		trace.out("util", "twoRef result: " + twoRefResult);
		assertEquals(twoRefOut, twoRefResult);

		String endRefResult = Utils.expandPropertyReferences(endRefIn);
		trace.out("util", "endRef result: " + endRefResult);
		assertEquals(endRefOut, endRefResult);

		String startRefResult = Utils.expandPropertyReferences(startRefIn);
		trace.out("util", "startRef result: " + startRefResult);
		assertEquals(startRefOut, startRefResult);
	}

	/**
	 * Try to retrieve several resources.
	 */
	public void _testGetFileAsResource() {
		URL url;
		try {
			url = new URL("http://learnlab.web.cmu.edu/");
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			return;
		}
		File f = Utils.getFileAsResource(url.toString(), this);
		int lineNo = 0;
		try {
			BufferedReader rdr = new BufferedReader(new FileReader(f));
			String line;
			for (lineNo = 1; null != (line = rdr.readLine()); ++lineNo)
				trace.out("" + lineNo + ". " + line);
		} catch (IOException ioe) {
			System.err.println("error reading url " + url + " at line " +
							   lineNo + ":");
			ioe.printStackTrace();
		}
	}

	/**
	 * Try to retrieve several resources.
	 */
	public void testGetCodeBaseURL() {

		trace.out("util", Utils.INTERFACE_HOME_PROPERTY + "property=" + 
				  System.getProperty(Utils.INTERFACE_HOME_PROPERTY));
		String p;
		URL url = Utils.getCodeBaseURL();
		trace.out("util", "getCodeBaseURL() result: " + url + ";");

		url = Utils.getCodeBaseURL(p = "ProblemsOrganizer");
		trace.out("util", "getCodeBaseURL(" + p + ") result: " + url + ";");

		url = Utils.getCodeBaseURL(p = "edu.cmu.pact.Preferences/InstallationPreferences.xml");
		trace.out("util", "getCodeBaseURL(" + p + ") result: " + url + ";");

		//url = Utils.getCodeBaseURL(AdditionTutorChaining.TutorInterface.class);
		trace.out("util", "getCodeBaseURL(" + p + ") result: " + url + ";");
	}

	/**
	 * Try resolving several paths to interface home.
	 */
	public void testResolveToInterfaceHome() {
		trace.out("util", Utils.INTERFACE_HOME_PROPERTY + "property=" + 
				  System.getProperty(Utils.INTERFACE_HOME_PROPERTY));

		String p;
		URL url = Utils.resolveToInterfaceHomeURL(p = "");

		trace.out("util", "resolveToInterfaceHomeURL("+p+") result: "+url+";");
		File fi = Utils.resolveToInterfaceHomeFile(p);
		trace.out("util", "resolveToInterfaceHomeFile("+p+") result: "+fi+";");

		//url = Utils.getCodeBaseURL(AdditionTutorChaining.TutorInterface.class);
		System.setProperty(Utils.INTERFACE_HOME_PROPERTY,
						   url.toExternalForm());
		trace.out("util", Utils.INTERFACE_HOME_PROPERTY + "property=" + 
				  System.getProperty(Utils.INTERFACE_HOME_PROPERTY));

		url = Utils.resolveToInterfaceHomeURL(p = "");
		trace.out("util", "resolveToInterfaceHomeURL("+p+") result: "+url+";");
		fi = Utils.resolveToInterfaceHomeFile(p);
		trace.out("util", "resolveToInterfaceHomeFile("+p+") result: "+fi+";");

		url = Utils.resolveToInterfaceHomeURL(p = "ProblemsOrganizer");
		trace.out("util", "resolveToInterfaceHomeURL("+p+") result: "+url+";");
		fi = Utils.resolveToInterfaceHomeFile(p);
		trace.out("util", "resolveToInterfaceHomeFile("+p+") result: "+fi+";");
	}
	
	/**
	 * Test {@link Utils#invokeBrowser(String)}.
	 */
	public void invokeBrowserTest() {
		String url;
		long sleepSecs = 8;
		try {
			url = "http://www.cmu.edu";
			Utils.invokeBrowser(url);
			trace.out("util", "on url "+url+"; to sleep "+sleepSecs+" sec");
			Thread.sleep(sleepSecs*1000);  // sleep() takes msec
			url = "http://junk.cmu.edu";
			Utils.invokeBrowser(url);
			trace.out("util", "on url "+url+"; to sleep "+sleepSecs+" sec");
			Thread.sleep(sleepSecs*1000);  // sleep() takes msec
			url = "http://www.cs.cmu.edu";
			Utils.invokeBrowser(url);
			trace.out("util", "on url "+url+"; to sleep "+sleepSecs+" sec");
			Thread.sleep(sleepSecs*1000);  // sleep() takes msec
			
		} catch (InterruptedException ie) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}

	/** Help message with command-line arguments. */
	public static final String usageMsg =
		"Usage:\n" +
		"  UtilsTest [-d] [test ...]\n" +
		"where--\n" +
		"  -d means turn on debugging;\n" +
		"  test ... are individual tests to run; if none specified, runs suite().\n";

	/**
	 * Command-line usage: see {@link #usageMsg}.
	 */
	public static void main(String args[]) {
		boolean debugOn = false;
		int i;

		for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
			char opt = args[i].charAt(1);
			switch (opt) {
			case 'd':
				debugOn = true;
				break;
			default:
				System.err.println("Undefined command-line option " + opt +
								   ". " + usageMsg);
				System.exit(1);
			}
		}
		if (debugOn)
			trace.addDebugCode("util");
		if (i >= args.length)
			junit.textui.TestRunner.run(UtilsTest.suite());
		else {
			for (; i < args.length; ++i) {
				junit.textui.TestRunner.run(new UtilsTest(args[i]));
			}
		}
			
	}
	
	public final void testCleanup() {
		String s;
		
		s = "1234abcd*(*&#";
		assertEquals("no white space", s, Utils.cleanup(s));
		s = "  \t\n  \r\n";
		assertEquals("only white space", " ", Utils.cleanup(s));
		s = "2 white spaces";
		assertEquals("2 single white spaces", s, Utils.cleanup(s));
		s = " whitespaces";
		assertEquals("leading spaces", s, Utils.cleanup("   "+s));
		s = "whitespaces ";
		assertEquals("trailing spaces", s, Utils.cleanup(s+"   "));
		s = "white space";
		assertEquals("embedded linefeed", s, Utils.cleanup(s.substring(0,5)+"\n"+s.substring(6)));
		s = "white space";
		assertEquals("embedded tab", s, Utils.cleanup(s.substring(0,5)+"\t"+s.substring(6)));
		s = " white s pace ";
		assertEquals("junk all over", s, Utils.cleanup("\t \r\n"+s.substring(1,6)+"\t\t"+
				s.substring(7,8)+"\n   \n\r"+s.substring(9,13)+" \t\r\n"));
	}

}
