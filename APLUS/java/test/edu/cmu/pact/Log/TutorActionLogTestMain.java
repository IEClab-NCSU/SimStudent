/*
 * $Id: TutorActionLogTestMain.java 13682 2012-05-17 02:22:05Z sewall $
 *
 * Copyright (c) 2002-2004 Carnegie Mellon University.
 */
package edu.cmu.pact.Log;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import edu.cmu.oli.log.client.DiskLogger;
import edu.cmu.oli.log.client.SessionLog;
import edu.cmu.oli.log.client.StreamLogger;
import edu.cmu.pact.Utilities.trace;

/**
 * @version       $Revision: 13682 $ $Date: 2012-05-16 22:22:05 -0400 (Wed, 16 May 2012) $
 * @author        John A Rinderle
 * <a href="mailto:jar2@andrew.cmu.edu">(jar2@andrew.cmu.edu)</a>
 * @version       Bill Jerome
 * <a href="mailto:wjj@andrew.cmu.edu">(wjj@andrew.cmu.edu)</a>
 */
public class TutorActionLogTestMain {

	
	/** Dev server at OLI for testing. */
	private static final String DEFAULT_SERVLET_URL =
		"http://oli0.andrew.cmu.edu/log";

	/** Default disk file for disk logging. */
	private static final String DEFAULT_OUTFILE = "TutorActionLogTest.log";

    private static final String _SOURCE_ID = "TutorActionLogTest";
    
    private StreamLogger streamLogger = null;
    
    private DiskLogger diskLogger = null;
    
	/**
	 * Constructor, also executes the test.
	 *
	 * @param  userId unique user identifier
	 * @param  sessionId unique session identifier
	 * @param  servletURL host logger address; if null, do not stream log
	 * @param  outfile disk logger file; if null, do not disk log
	 * @param  count Number of actions to simulate
	 */
    public TutorActionLogTestMain(final String userId,
							  final String sessionId,
							  final String servletURL,
							  final String outfile,
							  final int count) {
		// Parameter checking
		if (userId == null) {
			throw (new NullPointerException("parameter 'userId' is not defined"));
		} else if (sessionId == null) {
			throw (new NullPointerException("parameter 'sessionId' is not defined"));
		} else if (count <= 0) {
			throw (new IllegalArgumentException("'count' must be > zero"));
		}

		SessionLog sessionLog = new SessionLog();  // configure the session log
		sessionLog.setClassId("TutorActionLogTestClassId");
		sessionLog.setTreatmentId("TutorActionLogTestTreatmentId");
		sessionLog.setUserGuid(userId);
		sessionLog.setSessionId(sessionId);
		sessionLog.setInfoType("tutor_related_message_sequence");

		if (servletURL != null) {
			streamLogger = new StreamLogger();
			streamLogger.setURL(servletURL);
		}
		if (outfile != null) {
			diskLogger = new DiskLogger();
			diskLogger.setOutfile(outfile);
		}
		try {                                          // send it
			if (streamLogger != null)
				streamLogger.logSessionLog(sessionLog);
			if (diskLogger != null)
				diskLogger.logSessionLog(sessionLog);
		} catch (SAXException se) {
			se.printStackTrace();
			return;
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
			return;
		}

		// Simulate actions in log
		simulateActions(count, _SOURCE_ID, userId, sessionId);
    }

	/**
	 * Send the given number of log messages. Contents are entries in 
	 * local msgs array.
	 *
	 * @param  count number to send
	 * @param  sourceId matches session parameter
	 * @param  userId matches session parameter
	 * @param  sessionId matches session parameter
	 */
	private void simulateActions(int count,
								 String sourceId,
								 String userId,
								 String sessionId) {
		String msgs =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<tutor_related_message_sequence>\n" +
			" <tool_message attempt_id=\"NGUID-1083702363589.63667429449968040\">\n" +
			"  <semantic_event id=\"NGUID-1083702363589.63667429449968041\"\n" +
			"                  name=\"InterfaceAction\"/>\n" +
			"  <event_descriptor>\n" +
			"   <action>UpdateTable</action>\n" +
			"   <selection>table1_C6R4</selection>\n" +
			"   <input>5</input>\n" +
			"  </event_descriptor>\n" +
			" </tool_message>\n" +
			" <tutor_message>\n" +
			"  <semantic_event id=\"1096352280169_226930285183144076\"\n" +
			"   semantic_event_id=\"NGUID-1083702363589.63667429449968041\"\n" +
            "   name=\"RESULT\" trigger=\"DATA\" />\n" +
			"  <event_descriptor event_id=\"1096352280169_226930285183144076\">\n" +
			"    <action>ButtonPressed</action>\n" +
			"    <selection>Done</selection>\n" +
			"    <input>-1</input>\n" +
			"  </event_descriptor>\n" +
			"  <action_evaluation>INCORRECT</action_evaluation>\n" +
			"  <tutor_advice>You're so wrong.</tutor_advice>\n" +
			"  <skill probability=\"0.5\">box-filling-skill</skill>\n" +
			"  <skill>compiler-error-generating-skill</skill>\n" +
			"  <production>focus-on-first-column</production>\n" +
			" </tutor_message>\n" +
			" <tool_message attempt_id=\"NGUID-1083702363589.63667429449968042\">\n" +
			"  <semantic_event id=\"NGUID-1083702363589.63667429449968043\"\n" +
			"                  name=\"InterfaceAction\"/>\n" +
			"  <event_descriptor>\n" +
			"   <action>UpdateTable</action>\n" +
			"   <selection>table1_C5R1</selection>\n" +
			"   <input>1</input>\n" +
			"  </event_descriptor>\n" +
			" </tool_message>\n" +
			"</tutor_related_message_sequence>\n";
    
		trace.out("msgs=\n" + msgs);
		Reader rdr = new StringReader(msgs);
		Iterator it = TutorActionLog.factoryIterator(rdr, null, null);
		for (int i = 0; i < count && it.hasNext(); ++i) {
			TutorActionLog logEntry = (TutorActionLog) it.next();
			logEntry.setSourceId(sourceId);
			logEntry.setUserGuid(userId);
			logEntry.setSessionId(sessionId);

			trace.out("Sending log entry [" + i + "], getInfo()=\n" +
							   logEntry.getInfo());
			if (streamLogger != null)
				streamLogger.logActionLog(logEntry);
			if (diskLogger != null)
				diskLogger.logActionLog(logEntry);
		}
	}

	/**
	 * Print a usage message and exit. Gives help with Command-line arguments.
	 *
	 * @param  errMsg specific error msg
	 */
	private static void usageExit(String errMsg) {
		String usageMsg =
			"Usage:\n" +
			"  java TutorActionLogTest [-c count] [-l [servletURL]] [-f outfile] [-s sessionId] [-u userId] [infile]\n" +
			"where--\n" +
			"  count is the number of msgs to log (default all);\n" +
			"  -l means log to the servletURL; default is log to disk only;\n" +
			"  servletURL defaults to " + DEFAULT_SERVLET_URL + ";\n" +
			"  -f by itself means don't log to disk; with outfile means use\n" +
			"    different outfile; default is " + DEFAULT_OUTFILE + ";\n" +
			"  sessionId defaults to milliseconds since 1 Jan 1970;\n" +
			"  userId is userGuid; default from System property user.name;\n" +
			"  infile is an optional <tutor_related_message_sequence> file to read.\n";

		System.err.println(errMsg + "\n" + usageMsg);
		System.exit(1);
	}

	/**
	 * See {@link #usageExit} for command-line args.
	 */
	public static void main(String[] args) {
		String userId = System.getProperty("user.name");
		String sessionId = Long.toString(System.currentTimeMillis());
		String servletURL = null;
		String outfile = DEFAULT_OUTFILE;
		int count = Integer.MAX_VALUE;
		int i = 0;

		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			char sw = args[i].charAt(1);
			switch(sw) {
			case 'c':
				if (args.length <= ++i)
					usageExit("Missing count");
				try {
					count = Integer.parseInt(args[i]);
				} catch (NumberFormatException nfe) {
					usageExit("Bad count: " + nfe);
				}
				break;
			case 'f':
				if (args.length <= i+1 || args[i+1].charAt(0) == '-')
					outfile = null;
				else
					outfile = args[++i];
				break;
			case 'l':
				if (args.length <= i+1 || args[i+1].charAt(0) == '-')
					servletURL = DEFAULT_SERVLET_URL;
				else
					servletURL = args[++i];
				break;
			case 's':
				if (args.length <= ++i)
					usageExit("Missing sessionId.");
				sessionId = args[i];
				break;
			case 'u':
				if (args.length <= ++i)
					usageExit("Missing userId.");
				userId = args[i];
				break;
			default:
				usageExit("Undefined switch '" + sw + "'.");
			}
		}
		if (i < args.length) {
			String infile = args[i++];
			int entryNo = 0;
			try {
				Reader rdr = new FileReader(infile);
				Iterator it = TutorActionLog.factoryIterator(rdr, null, null);
				for (entryNo = 1; it.hasNext(); ++entryNo) {
					TutorActionLog entry = (TutorActionLog) it.next();
					trace.out(entryNo + ":\t" + entry);
				}
			} catch (Exception e) {
				System.err.println("Error reading " + infile + ", entry " +
								   entryNo + ":");
				e.printStackTrace();
			}
		} else {
			TutorActionLogTestMain tutorActionLogTest =
				new TutorActionLogTestMain(userId, sessionId, servletURL, outfile,
									   count);
		}
	}


    public static Test suite() {
        return new TestSuite(TutorActionLogTestMain.class);
    }

}
