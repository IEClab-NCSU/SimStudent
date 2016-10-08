/**
 * @author Dan Tasse - dtasse@andrew.cmu.edu
 * 6/23/06
 */

package edu.cmu.pact.Log;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jess.Deffacts;
import jess.Deffunction;
import jess.Defglobal;
import jess.Deftemplate;
import jess.JessException;
import jess.Value;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.oli.log.client.DiskLogger;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.SinkLogger;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.MTRete;

public class LogReteChangesTest extends TestCase {

	
	private static String LOGGING_DIR;
	
	private LoggingSupport ls;
	private EventLogger el;
	
	private static CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
	private static BR_Controller brc = launcher.getFocusedController();

	private static final String[] expectedFactChangesLogEntries = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>DELETE_fact</action_type>\n"+
		"  <argument>MAIN::textField</argument>\n"+
		"  <result>(MAIN::textField (name tf1) (value old-tf1-value))</result>\n"+
		"  <result_details />\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>ADD_fact</action_type>\n"+
		"  <argument>MAIN::textField</argument>\n"+
		"  <result>(MAIN::textField (name tf1) (value new-tf1-value))</result>\n"+
		"  <result_details />\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>ADD_fact</action_type>\n"+
		"  <argument>MAIN::textField</argument>\n"+
		"  <result>(MAIN::textField (name tf5) (value new-tf5-value))</result>\n"+
		"  <result_details />\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	public void testLogFactChanges() throws IOException, JDOMException, JessException {
		SinkLogger sink = new SinkLogger();
		LoggingSupport ls = el.getLoggingSupport();
		DiskLogger saveDiskLogger = ls.getSubstituteDiskLogger();
        ls.setSubstituteDiskLogger(sink);

        MTRete oldRete = new MTRete(); 
		MTRete newRete = new MTRete();
		String baseScript =
			"(deftemplate MAIN::textField (slot name) (slot value))\n" +
			"(bind ?f1 (assert (textField (name tf1) (value old-tf1-value))))\n"+
			"(bind ?f2 (assert (textField (name tf2) (value old-tf2-value))))\n"+
			"(bind ?f3 (assert (textField (name tf3) (value ?f2))))\n"+
		    "(bind ?f4 (assert (textField (name tf4) (value ?f1))))\n";
		String editScript =
			"(modify ?f1 (value new-tf1-value))\n"+
			"(bind ?f5 (assert (textField (name tf5) (value new-tf5-value))))\n";
		
		try {
			oldRete.eval(baseScript);
			newRete.eval(baseScript);
			newRete.eval(editScript);

			LogReteChanges lrc = new LogReteChanges(el, oldRete, newRete);
			lrc.logReteChanges();
			List logEntries = sink.getLatestInfoFields();
			checkStringsVsList("log entries", expectedFactChangesLogEntries, logEntries);
		} finally {
			ls.setSubstituteDiskLogger(saveDiskLogger);
		}
	}

	/**
	 * Compare a list of strings against expected ones.
	 * @param label
	 * @param expected
	 * @param actual
	 */
	private void checkStringsVsList(String label, String[] expected, List actual) {
		int i = 0;
		for (Iterator it = actual.iterator(); it.hasNext(); ++i)
			trace.out("log", label+"["+i+"]:\n "+it.next());
		assertEquals("Wrong number of "+label, expected.length, actual.size());
		i = 0;
		for (Iterator it = actual.iterator(); it.hasNext() && i < expected.length; ++i) {
			String actualItem = (String)it.next();
			assertEquals(label+"["+i+"]", expected[i].trim(), actualItem.trim());
		}
	}


	public void testLogDeftemplateChanges() throws IOException, JDOMException, JessException
	{
		
		MTRete oldRete = new MTRete(); 
		MTRete newRete = new MTRete();
		
		Deftemplate deletedDeftemplate = new Deftemplate("deletedDeftemplate", "aaa", oldRete);
		Deftemplate editedDeftemplate = new Deftemplate("editedDeftemplate", "bbb", oldRete);
		Deftemplate addedDeftemplate = new Deftemplate("addedDeftemplate", "ccc", newRete);
		oldRete.addDeftemplate(deletedDeftemplate);
		oldRete.addDeftemplate(editedDeftemplate);
		editedDeftemplate = new Deftemplate("editedDeftemplate", "ddd", newRete);
		newRete.addDeftemplate(editedDeftemplate);
		newRete.addDeftemplate(addedDeftemplate);
		
		LogReteChanges lrc = new LogReteChanges(el, oldRete, newRete);
		lrc.logReteChanges();
		
		Document doc = loadFromFile(ls.lastLogFile);
		List kids = doc.getRootElement().getChildren();

		// sewall 2007/01/17: skip 2 extra PACT_BR START_TUTOR entries at start 
		for(int i = 4 /*2*/; i < kids.size(); i++)
		{
			Element elem = (Element) (kids.get(i));
			assertEquals("log_action", elem.getName());
			assertEquals("EXTERNAL_EDITOR", elem.getAttributeValue("source_id"));
			Element e2 = elem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			if (e2.getChildText("action_type").equals("ADD_deftemplate"))
			{
				assertEquals("MAIN::addedDeftemplate", e2.getChildText("argument"));
				assertEquals("(deftemplate MAIN::addedDeftemplate \n   \"ccc\")", 
						e2.getChildText("result"));
			}
			else if (e2.getChildText("action_type").equals("EDIT_deftemplate"))
			{
				assertEquals("MAIN::editedDeftemplate", e2.getChildText("argument"));
				assertEquals("(deftemplate MAIN::editedDeftemplate \n   \"bbb\")", 
						e2.getChildText("result"));
				assertEquals("(deftemplate MAIN::editedDeftemplate \n   \"ddd\")", 
						e2.getChildText("result_details"));				
			}
			else if (e2.getChildText("action_type").equals("DELETE_deftemplate"))
				assertEquals("MAIN::deletedDeftemplate", e2.getChildText("argument"));
			else
				fail("Incorrect action type: " + e2.getChildText("action_type"));
		}

	}
	
	public void testLogDefruleChanges() throws IOException, JDOMException, JessException
	{
		
		MTRete oldRete = new MTRete();
		MTRete newRete = new MTRete();
		
		//for some reason, this is the only way I could find to add defrules to the rete.
                // Mac OS does not work on file names wiht a backslash
                String prFile1 = new File(LOGGING_DIR, "rules1.pr").getAbsolutePath();
                String prFile2 = new File(LOGGING_DIR, "rules2.pr").getAbsolutePath();
                oldRete.batch(prFile1);
                newRete.batch(prFile2);
                /*
		oldRete.batch(LOGGING_DIR + "\\rules1.pr");
		newRete.batch(LOGGING_DIR + "\\rules2.pr");
                */
		
		LogReteChanges lrc = new LogReteChanges(el, oldRete, newRete);
		lrc.logReteChanges();
		
		Document doc = loadFromFile(ls.lastLogFile);
		List kids = doc.getRootElement().getChildren();
	    // sewall 2007/01/17: skip 2 extra PACT_BR START_TUTOR entries at start 
		for(int i = 4/*2*/; i < kids.size(); i++)
		{
			Element elem = (Element) (kids.get(i));
			assertEquals("log_action", elem.getName());
			assertEquals("EXTERNAL_EDITOR", elem.getAttributeValue("source_id"));
			Element e2 = elem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			if (e2.getChildText("action_type").equals("ADD_defrule"))
			{
				assertEquals("MAIN::rule3", e2.getChildText("argument"));
				assertEquals("(defrule MAIN::rule3 \n   (MAIN::ddd) \n   =&gt;)", 
						e2.getChildText("result"));
			}
			else if (e2.getChildText("action_type").equals("EDIT_defrule"))
			{
				assertEquals("MAIN::rule2", e2.getChildText("argument"));
				assertEquals("(defrule MAIN::rule2 \n   (MAIN::bbb) \n   =&gt;)", 
						e2.getChildText("result"));
				assertEquals("(defrule MAIN::rule2 \n   (MAIN::ccc) \n   =&gt;)", 
						e2.getChildText("result_details"));				
			}
			else if (e2.getChildText("action_type").equals("DELETE_defrule"))
				assertEquals("MAIN::rule1", e2.getChildText("argument"));
		}

	}
	
	public void testLogDeffunctionChanges() throws JessException, IOException, JDOMException
	{		
		MTRete oldRete = new MTRete(); 
		MTRete newRete = new MTRete();
		
		Deffunction deletedDeffunction = new Deffunction("deletedDeffunction", "aaa");
		Deffunction editedDeffunction = new Deffunction("editedDeffunction", "bbb");
		Deffunction addedDeffunction = new Deffunction("addedDeffunction", "ccc");
		oldRete.addUserfunction(deletedDeffunction);
		oldRete.addUserfunction(editedDeffunction);
		editedDeffunction = new Deffunction("editedDeffunction", "ddd");
		newRete.addUserfunction(editedDeffunction);
		newRete.addUserfunction(addedDeffunction);
		
		LogReteChanges lrc = new LogReteChanges(el, oldRete, newRete);
		lrc.logReteChanges();
		
		Document doc = loadFromFile(ls.lastLogFile);
		List kids = doc.getRootElement().getChildren();
	    // sewall 2007/01/17: skip 2 extra PACT_BR START_TUTOR entries at start 
		for(int i = 4 /*2*/; i < kids.size(); i++)
		{
			Element elem = (Element) (kids.get(i));
			assertEquals("log_action", elem.getName());
			assertEquals("EXTERNAL_EDITOR", elem.getAttributeValue("source_id"));
			Element e2 = elem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			if (e2.getChildText("action_type").equals("ADD_deffunction"))
			{
				assertEquals("addedDeffunction", e2.getChildText("argument"));
				assertEquals("(deffunction addedDeffunction () \"ccc\")", 
						e2.getChildText("result"));
			}
			else if (e2.getChildText("action_type").equals("EDIT_deffunction"))
			{
				assertEquals("editedDeffunction", e2.getChildText("argument"));
				assertEquals("(deffunction editedDeffunction () \"bbb\")", 
						e2.getChildText("result"));
				assertEquals("(deffunction editedDeffunction () \"ddd\")", 
						e2.getChildText("result_details"));				
			}
			else if (e2.getChildText("action_type").equals("DELETE_deffunction"))
				assertEquals("deletedDeffunction", e2.getChildText("argument"));
			else
				fail("Incorrect action type: " + e2.getChildText("action_type"));
		}
	}
	
	public void testLogDefglobalChanges() throws JessException, IOException, JDOMException
	{
		
		MTRete oldRete = new MTRete(); 
		MTRete newRete = new MTRete();
		
		
		Defglobal deletedDefglobal = new Defglobal("deletedDefglobal", new Value(true));
		Defglobal editedDefglobal = new Defglobal("editedDefglobal", new Value(true));
		Defglobal addedDefglobal = new Defglobal("addedDefglobal", new Value(true));
		oldRete.addDefglobal(deletedDefglobal);
		oldRete.addDefglobal(editedDefglobal);
		editedDefglobal = new Defglobal("editedDefglobal", new Value(false));
		newRete.addDefglobal(editedDefglobal);
		newRete.addDefglobal(addedDefglobal);
		
		LogReteChanges lrc = new LogReteChanges(el, oldRete, newRete);
		lrc.logReteChanges();
		
		Document doc = loadFromFile(ls.lastLogFile);
		List kids = doc.getRootElement().getChildren();
	    // sewall 2007/01/17: skip 2 extra PACT_BR START_TUTOR entries at start 
		for(int i = 4 /*2*/; i < kids.size(); i++)
		{
			Element elem = (Element) (kids.get(i));
			assertEquals("log_action", elem.getName());
			assertEquals("EXTERNAL_EDITOR", elem.getAttributeValue("source_id"));
			Element e2 = elem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			if (e2.getChildText("action_type").equals("ADD_defglobal"))
			{
				assertEquals("addedDefglobal", e2.getChildText("argument"));
				assertEquals("(defglobal ?addedDefglobal = TRUE)", 
						e2.getChildText("result"));
			}
			else if (e2.getChildText("action_type").equals("EDIT_defglobal"))
			{
				assertEquals("editedDefglobal", e2.getChildText("argument"));
				assertEquals("(defglobal ?editedDefglobal = TRUE)", 
						e2.getChildText("result"));
				assertEquals("(defglobal ?editedDefglobal = FALSE)", 
						e2.getChildText("result_details"));				
			}
			else if (e2.getChildText("action_type").equals("DELETE_defglobal"))
				assertEquals("deletedDefglobal", e2.getChildText("argument"));
			else
				fail("Incorrect action type: " + e2.getChildText("action_type"));
		}

	}
	
	public void testLogDeffactsChanges() throws JessException, IOException, JDOMException
	{
		
		MTRete oldRete = new MTRete(); 
		MTRete newRete = new MTRete();
		
		
		Deffacts deletedDeffacts = new Deffacts("deletedDeffacts", "aaa", oldRete);
		Deffacts changedDeffacts = new Deffacts("changedDeffacts", "bbb", oldRete);
		Deffacts addedDeffacts = new Deffacts("addedDeffacts", "ccc", newRete);
		oldRete.addDeffacts(deletedDeffacts);
		oldRete.addDeffacts(changedDeffacts);
		changedDeffacts = new Deffacts("changedDeffacts", "ddd", newRete);
		newRete.addDeffacts(changedDeffacts);
		newRete.addDeffacts(addedDeffacts);
		
		LogReteChanges lrc = new LogReteChanges(el, oldRete, newRete);
		lrc.logReteChanges();
		
		Document doc = loadFromFile(ls.lastLogFile);
		
		
		List kids = doc.getRootElement().getChildren();
	    // sewall 2007/01/17: skip 2 extra PACT_BR START_TUTOR entries at start 
		for(int i = 4 /*2*/; i < kids.size(); i++)
		{
			Element elem = (Element) (kids.get(i));
			assertEquals("log_action", elem.getName());
			assertEquals("EXTERNAL_EDITOR", elem.getAttributeValue("source_id"));
			Element e2 = elem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			if (e2.getChildText("action_type").equals("ADD_deffacts"))
				assertEquals("MAIN::addedDeffacts", e2.getChildText("argument"));
			else if (e2.getChildText("action_type").equals("EDIT_deffacts"))
			{
				assertEquals("MAIN::changedDeffacts", e2.getChildText("argument"));
				assertEquals("(deffacts MAIN::changedDeffacts \"bbb\")", 
						e2.getChildText("result"));
				assertEquals("(deffacts MAIN::changedDeffacts \"ddd\")", 
						e2.getChildText("result_details"));				
			}
			else if (e2.getChildText("action_type").equals("DELETE_deffacts"))
				assertEquals("MAIN::deletedDeffacts", e2.getChildText("argument"));
			else
				fail("Incorrect action type: " + e2.getChildText("action_type"));
		}

	}
	
	private static Document loadFromFile (String filename) throws IOException, JDOMException
	{
		File f = new File(filename);
		
		LogFormatUtils.makeValidXML(f);
		LogFormatUtils.unescapeAll(f);

		SAXBuilder saxb = new SAXBuilder();
		Document d = saxb.build(f);
		
		f.delete(); //may not work

		return d;
	}
	
	public static final String usageMsg = "Usage:\n"
		+ "  LogReteChangesTest [-d] [test ...]\n"
		+ "where--\n"
		+ "  -d means turn on debugging;\n"
		+ "  test ... are individual tests to run; if none specified, runs suite().\n";
	
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
				System.err.println("Undefined command-line option " + opt
						+ ". " + usageMsg);
			System.exit(1);
			}
		}
		if (debugOn)
			trace.addDebugCode("util");
		if (i >= args.length)
			junit.textui.TestRunner.run(LogReteChangesTest.suite());
		else {
			for (; i < args.length; ++i) {
				junit.textui.TestRunner.run(new LogReteChangesTest(args[i]));
			}
		}
	}

	public LogReteChangesTest()
	{
		super();
	}
	public LogReteChangesTest(String arg0)
	{
		super(arg0);
	}

	protected void setUp() throws Exception
	{
		super.setUp();
		File f = new File("." + File.separator + "test" + File.separator
				+ "edu" + File.separator + "cmu" + File.separator + "pact"
				+ File.separator + "Log");
		// ./test/edu/cmu/pact/log/
		
		LOGGING_DIR = f.getCanonicalPath();

		brc.getProperties().setProperty(LoggingSupport.ENABLE_AUTHOR_LOGGING, Boolean.TRUE.toString());
		brc.getProperties().setProperty(BR_Controller.DISK_LOGGING_DIR, LOGGING_DIR);
		brc.getProperties().setProperty(BR_Controller.USE_DISK_LOGGING, Boolean.TRUE.toString());
		ls = brc.getLoggingSupport();
		el = new EventLogger(ls);
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(LogReteChangesTest.class);
		return suite;
	}

}
