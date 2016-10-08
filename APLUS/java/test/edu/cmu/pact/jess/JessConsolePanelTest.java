/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import jess.JessException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.oli.log.client.DiskLogger;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.SinkLogger;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class JessConsolePanelTest extends TestCase {

	private JessConsolePanel jcp;
	private LoggingSupport ls;
	private DiskLogger saveDiskLogger;
	private MTRete rete;
	private SinkLogger sink;
	private static int doDisplayFrame = 0; // 0=>hide; 1=>show; 2=>show & keep
	private JFrame displayFrame = null;

	/**
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		File f = new File("." + File.separator + "test" + File.separator
				+ "edu" + File.separator + "cmu" + File.separator + "pact"
				+ File.separator + "log");  // ./test/edu/cmu/pact/log/
		
		String loggingDir = f.getCanonicalPath();
		CTAT_Launcher ctatLauncher = new CTAT_Launcher(new String[0]);
		BR_Controller brc = ctatLauncher.getFocusedController();
		brc.getProperties().setProperty(LoggingSupport.ENABLE_AUTHOR_LOGGING, Boolean.TRUE.toString());
		brc.getProperties().setProperty(BR_Controller.DISK_LOGGING_DIR, loggingDir);
		brc.getProperties().setProperty(BR_Controller.USE_DISK_LOGGING, Boolean.TRUE.toString());
		ls = brc.getLoggingSupport();

		sink = new SinkLogger();
		saveDiskLogger = ls.getSubstituteDiskLogger();
	    ls.setSubstituteDiskLogger(sink);
	
		//EventLogger el = new EventLogger(ls);
		rete = new MTRete();
		//rete.setEventLogger(el);
		jcp = new JessConsolePanel(ctatLauncher, true);
	    
	    if (doDisplayFrame > 0) {
			displayFrame = new JFrame("JessConsolePanelTest");
			displayFrame.getContentPane().add(jcp);
			displayFrame.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			        displayFrame.dispose();
				}
			});
			displayFrame.pack();
			displayFrame.show();
	    }
	}

	private static final String[] resetLog = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>USER_COMMAND</action_type>\n"+
		"  <argument>(reset)</argument>\n"+
		"  <result>TRUE</result>\n"+
		"  <result_details />\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>"
	};
	
	private static final String[] deftemplateLog = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>USER_COMMAND</action_type>\n"+
		"  <argument>(deftemplate MAIN::textField (slot name) (slot value))</argument>\n"+
		"  <result>TRUE</result>\n"+
		"  <result_details />\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>\n"
	};
	
	private static final String[] assertsLog = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>USER_COMMAND</action_type>\n"+
		"  <argument>(bind ?f1 (assert (textField (name tf1) (value 1))))</argument>\n"+
		"  <result>&lt;Fact-1&gt;</result>\n"+
		"  <result_details>==&gt; f-1 (MAIN::textField (name tf1) (value 1))</result_details>\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>USER_COMMAND</action_type>\n"+
		"  <argument>(bind ?f2 (assert (textField (name tf2) (value 2))))</argument>\n"+
		"  <result>&lt;Fact-2&gt;</result>\n"+
		"  <result_details>==&gt; f-2 (MAIN::textField (name tf2) (value 2))</result_details>\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>\n"				
	};
	
	private static final String[] factsLog = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"2\">\n"+
		" <author_action_message>\n"+
		"  <action_type>USER_COMMAND</action_type>\n"+
		"  <argument>(facts)</argument>\n"+
		"  <result>nil</result>\n"+
		"  <result_details>f-0   (MAIN::initial-fact)&#xD;\n"+
		"f-1   (MAIN::textField (name tf1) (value 1))&#xD;\n"+
		"f-2   (MAIN::textField (name tf2) (value 2))&#xD;\n"+
		"For a total of 3 facts in module MAIN.</result_details>\n"+
		" </author_action_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	/**
	 * Test several console commands.
	 */		
	public void testConsoleCmds() {
		jcp.submitCommand("(reset)");
		sink.checkStringsVsInfoFields("(reset)", resetLog, true);

		jcp.submitCommand("(deftemplate MAIN::textField (slot name) (slot value))");
		try {
			rete.eval("(printout t \"this should not be logged\" crlf)");
		} catch (JessException je) {
			je.printStackTrace();
		}
		sink.checkStringsVsInfoFields("(deftemplate ...)", deftemplateLog, true);
		
		try {                            // watch will write to console on each assert
			rete.eval("(watch facts)");  // before the assert function actually returns
		} catch (JessException je) {     // to the caller, so should log
			je.printStackTrace();
		}
		jcp.submitCommand("(bind ?f1 (assert (textField (name tf1) (value 1))))");
		jcp.submitCommand("(bind ?f2 (assert (textField (name tf2) (value 2))))");
		sink.checkStringsVsInfoFields("assertsLog", assertsLog, true);

		jcp.submitCommand("(facts)");
		sink.checkStringsVsInfoFields("(facts)", factsLog, true);
	}

	/**
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
        if (doDisplayFrame < 2 && displayFrame != null)
        	displayFrame.dispose();
		ls.setSubstituteDiskLogger(saveDiskLogger);
		jcp = null;
		rete.halt();
		rete = null;
	}

	/**
	 * Suite to run all tests in this class.
	 */
	public static Test suite() {
		return new TestSuite(JessConsolePanelTest.class);
	}

	/**
	 * Command-line syntax help.
	 */
	public static final String usageMsg =
		"Usage:\n" +
		"  java [-cp classpath] edu.cmu.pact.jess.JessConsolePanelTest [-d] [-f|F]\n" +
		"where--\n" +
		"  -d  means turn on debug messages;\n" +
		"  -f  means display a GUI frame with Jess output; -F avoids closing the frame.";

	/**
	 * Command-line options: see {@link #usageMsg}.
	 */
	public static void main(String[] args) {
		int i = 0;
		for (i = 0; (i < args.length) && ('-' == args[i].charAt(0)); i++) {
			switch(args[i].charAt(1)) {
			case 'd':
				trace.addDebugCode("log");
				break;
			case 'F':
				doDisplayFrame = 2;
				break;
			case 'f':
				doDisplayFrame = 1;
				break;
			default:
				System.err.println("Unknown option '" + args[i].charAt(1) +
								   "'. " + usageMsg);
			    System.exit(1);
			}
		}
		junit.textui.TestRunner.run(JessConsolePanelTest.suite());
	}
}
