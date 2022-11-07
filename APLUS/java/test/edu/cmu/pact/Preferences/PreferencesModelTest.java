/*
 * $Id: PreferencesModelTest.java 18514 2012-11-06 20:42:00Z sewall $
 */
package edu.cmu.pact.Preferences;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * Test harness for PreferencesModel.
 * @author sewall
 */
public class PreferencesModelTest extends TestCase
								  implements PropertyChangeListener {

	/**
	 * Filenames for interface preferences files.
	 */
	private static final String INTERFACE_XML_FILE = "InterfacePreferences.xml";
	private static final String INTERFACE2_XML_FILE = "InterfacePreferences2.xml";

	/**
	 * Value for string item in {@link #INTERFACE_XML_SCRIPT}.
	 */
	private static final String INTERFACE_STRING_VALUE =
		"/Tutors/tutors/someOther.brd";

	/**
	 * Value for string item in {@link #INTERFACE_XML_SCRIPT}.
	 */
	private static final String INTERFACE2_STRING_VALUE =
		"and now for something completely different";

	/**
	 * Name (key) for string item in {@link #XML_SCRIPT}.
	 */
	private static final String STRING_NAME = "OLI Logging URL";

	/**
	 * Value for string item in {@link #XML_SCRIPT}.
	 */
	private static final String STRING_VALUE =
		"http://learnlab.web.cmu.edu/log/server";

	/**
	 * Name (key) for boolean item in {@link #testSaveAndLoad()}.
	 */
	private static final String BOOLEAN_NAME = "Lock Widgets";

	/**
	 * Value for boolean item in {@link #testSaveAndLoad()}.
	 */
	private static final Boolean BOOLEAN_VALUE = new Boolean(true);
	
	/**
	 * Name (key) for enum item in {@link #testSaveAndLoad()}. 
	 */
	private static final String ENUM_NAME = "suppressStudentFeedback";
	
	/**
	 * Value for enum item in {@link #testSaveAndLoad()}. 
	 */
	private static final Enum ENUM_VALUE = FeedbackEnum.HIDE_ALL_FEEDBACK;

	/**
	 * Name (key) for integer item in {@link #testSaveAndLoad()}.
	 */
	private static final String INTEGER_NAME = "Max Buggy Rules";

	/**
	 * Value for integer item in {@link #testSaveAndLoad()}.
	 */
	private static final Integer INTEGER_VALUE = new Integer(4);

	/**
	 * Number of Preference instances expected in {@link #testSaveAndLoad()}.
	 */
	private int expectedLoadCount = 0;

	/**
	 * Whether to preserve the XML output file. False by default.
	 * See {@link #main()} to turn this on.
	 */
	private static boolean preserveXmlFile = false;

	/**
	 * Whether to show a {@link PreferencesWindow} frame with the data.
	 * See {@link #main()} to turn this on.
	 */
	private static boolean doFrame = false;

	/**
	 * Name of default XML output file. Written to current directory.
	 */
	private static final String BR_PREFS_FILE = "testBrPrefs.xml";

	/**
	 * Name of temporary XML input file. Written to current directory;
	 * removed when done.
	 */
	private static final String tmpFileBaseName = "PreferencesModelTest.xml";

	/** File to load, absolute or relative path. */
	private String tmpFile = null;

	private PreferencesModel prefModel;

	/** Count editor requests. */
	private int changeCount = 0;
	
	/**
	 * Command-line options are:<ul>
	 * <li>-d to turn on trace, save temp XML file
	 * <li>-f to open frame {@link PreferencesWindow}
	 * </ul>
	 */
	public static void main(String[] args) {
		for (int i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
			char opt = args[i].charAt(1);
			switch (opt) {
			case 'd':
				preserveXmlFile = true;
				trace.setTraceLevel(5);
				trace.addDebugCode("pr");
				break;
			case 'f':
				doFrame = true;
				break;
			default:
				System.err.println("undefined command-line option " + opt);
			}
		}
		junit.textui.TestRunner.run(PreferencesModelTest.suite());
	}

	public static Test suite() { 
		TestSuite suite= new TestSuite(PreferencesModelTest.class); 
		return suite;
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		URL url = Utils.getFileInSamePackage(this, tmpFileBaseName);
		if (url == null)
			tmpFile = tmpFileBaseName;
		else {
			try {
				tmpFile = URLDecoder.decode(url.getFile(), "UTF-8");
			} catch (java.io.UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}
	}
	
	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		if (!preserveXmlFile)
			(new File(tmpFile)).delete();
	}

	/**
	 * Receive changes made by loading Preferences files or by editors.
	 *
	 * @param  evt PropertyChangeEvent detailing change
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		trace.out("Change #" + (++changeCount) + ": " +
						   evt.getPropertyName() +
						   " from " + evt.getOldValue() +
						   " to " + evt.getNewValue());
	}

	/**
	 * Test {@link PreferencesModel#saveToDisk()},
	 * {@link PreferencesModel#loadFromDisk()}.
	 */
	public void testSaveAndLoad() {
		File f = new File(tmpFile);
		f.delete();
		prefModel = new PreferencesModel(PreferencesModel.AUTHOR_SCOPE,
										 tmpFile);
		prefModel.setStringValue(STRING_NAME, STRING_VALUE);
		expectedLoadCount++;
		prefModel.setBooleanValue(BOOLEAN_NAME, BOOLEAN_VALUE);
		expectedLoadCount++;
		prefModel.setEnumValue(ENUM_NAME, ENUM_VALUE);
		expectedLoadCount++;
		prefModel.setIntegerValue(INTEGER_NAME, INTEGER_VALUE);
		expectedLoadCount++;

		assertEquals(expectedLoadCount,
					 prefModel.saveToDisk(PreferencesModel.AUTHOR_SCOPE, tmpFile));

		prefModel = new PreferencesModel(PreferencesModel.AUTHOR_SCOPE,
										 "dummy");      // dummy=>empty model
		prefModel.addPropertyChangeListener(this);
		assertEquals(expectedLoadCount,
					 prefModel.loadFromDisk(PreferencesModel.AUTHOR_SCOPE, tmpFile));

		StringBuffer result = new StringBuffer();
		for (Iterator it = prefModel.iterator(PreferencesModel.AUTHOR_SCOPE);
			 	it.hasNext(); ) {
			result.append(it.next().toString() + "\n");
		}
		assertEquals("getStringValue", STRING_VALUE, prefModel.getStringValue(STRING_NAME));
		trace.out("pr", "prefModel.getStringValue(STRING_NAME)=" +
				  prefModel.getStringValue(STRING_NAME) + ", STRING_VALUE=" +
				  STRING_VALUE);
		assertEquals("getBooleanValue", BOOLEAN_VALUE, prefModel.getBooleanValue(BOOLEAN_NAME));
		assertEquals("getIntegerValue", INTEGER_VALUE, prefModel.getIntegerValue(INTEGER_NAME));
		assertEquals("getEnumValue", ENUM_VALUE, prefModel.getEnumValue(ENUM_NAME));
	}

	/**
	 * Test that {@link PreferencesModel.INTERFACE_SCOPE} values supersede
	 * {@link PreferencesModel.INSTALLATION_SCOPE} values.
	 */
	public void testInterfaceScope() {
		URL url = Utils.getFileInSamePackage(this, PreferencesModel.INSTALLATION_PREF_FILENAME);
				
        trace.out ("url = " + url);
		prefModel = new PreferencesModel(PreferencesModel.INSTALLATION_SCOPE,
										 url);
		prefModel.addPropertyChangeListener(this);

		assertEquals(STRING_VALUE, prefModel.getStringValue(STRING_NAME));
		trace.out("pr", "prefModel.getStringValue(STRING_NAME)=" +
				  prefModel.getStringValue(STRING_NAME) + ", STRING_VALUE=" +
				  STRING_VALUE);

		prefModel.loadFromURL(PreferencesModel.INTERFACE_SCOPE,
							  Utils.getFileInSamePackage(this, INTERFACE_XML_FILE));
		assertEquals(INTERFACE_STRING_VALUE, prefModel.getStringValue(STRING_NAME));
		trace.out("pr", "prefModel.getStringValue(STRING_NAME)=" +
				  prefModel.getStringValue(STRING_NAME) +
				  ", INTERFACE_STRING_VALUE=" + INTERFACE_STRING_VALUE);
		showFrame();
		prefModel.saveToDisk(PreferencesModel.INTERFACE_SCOPE,
							 "testInterfacePrefs.xml");

		prefModel.loadFromURL(PreferencesModel.INTERFACE_SCOPE,
							  Utils.getFileInSamePackage(this, INTERFACE2_XML_FILE));
		assertEquals(INTERFACE2_STRING_VALUE, prefModel.getStringValue(STRING_NAME));
		trace.out("pr", "prefModel.getStringValue(STRING_NAME)=" +
				  prefModel.getStringValue(STRING_NAME) +
				  ", INTERFACE2_STRING_VALUE=" + INTERFACE2_STRING_VALUE);
		showFrame();
		prefModel.saveToDisk(PreferencesModel.INTERFACE_SCOPE,
							 "testInterfacePrefs2.xml");
		prefModel.saveToDisk(BR_PREFS_FILE);
	}

	/**
	 * Test tree-building in {@link PreferencesModel}.
	 */
	public void testTree() {
		URL url = Utils.getFileInSamePackage(this, PreferencesModel.INSTALLATION_PREF_FILENAME);
		prefModel = new PreferencesModel(PreferencesModel.INSTALLATION_SCOPE, url);
		prefModel.addPropertyChangeListener(this);

		// commented out to avoid excess text being printed during tests
        //PreferencesWindow.GroupNode root = prefModel.getDisplayRoot();
        //trace.out(root.prettyPrint());
		showFrame();
		prefModel.saveToDisk(BR_PREFS_FILE);
	}

	/**
	 * Display the preferences editor window and wait until the user closes it.
	 * No-op if {@link #doFrame} is false.
	 */
	private Integer lock = new Integer(0);
	private void showFrame() {
		if (!doFrame)
			return;
		lock = new Integer(lock.intValue()+1);
		final PreferencesWindow frame = PreferencesWindow.create(prefModel, null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				frame.dispose();
				synchronized(lock) {				
					trace.out("pr", "thread " + Thread.currentThread().getName() +
							  " to notifyAll(): lock value "  + lock);
					lock.notifyAll();
				}
			}
		});
		frame.show();
		synchronized(lock) {
			try {
				trace.out("pr", "thread " + Thread.currentThread().getName() +
						  " to wait(): lock value "  + lock);
				lock.wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
}
