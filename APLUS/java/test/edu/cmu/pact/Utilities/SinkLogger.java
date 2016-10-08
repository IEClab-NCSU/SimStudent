/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import edu.cmu.oli.log.client.ActionLog;
import edu.cmu.oli.log.client.DiskLogger;
import edu.cmu.oli.log.client.Log;
import edu.cmu.oli.log.client.SessionLog;
import edu.cmu.oli.log.client.SupplementLog;

/**
 * A substitute for {@link DiskLogger} that logs messages to a {@link List}.
 */
public class SinkLogger extends DiskLogger {

	private List entries = new ArrayList();

	public Boolean logSessionLog(SessionLog logEntry) {
		entries.add(logEntry);
		return Boolean.TRUE;
	}
	public Boolean logActionLog(ActionLog logEntry) {
		entries.add(logEntry);
		return Boolean.TRUE;
	}
	public Boolean logSupplementLog(SupplementLog logEntry) {
		entries.add(logEntry);
		return Boolean.TRUE;
	}
	/**
	 * Collect the messages received by
	 * {@link SinkLogger#logActionLog(ActionLog)} and other log calls since the last call
	 * to this method. Clears {@link #entries}.
	 * @return list of latest messages: element type will be some subclass of
	 *         {@link edu.cmu.oli.log.client.Log}
	 */
	public List getLatestMsgs() {
		List result = new ArrayList(entries);
		trace.out("log", "sink.getLatestMsgs() retrieves "+result.size());
		entries.clear();
		return result;
	}
	/**
	 * Collect the info fields from {@link ActionLog} messages received by
	 * calls to {@link DiskLogger#logActionLog(ActionLog)} since the last call
	 * to this method. Calls {@link SinkLogger#getLatestMsgs()}.
	 * @return list of info fields from latest ActionLog messages: element
	 *         type will be String
	 */
	public List getLatestInfoFields() {
		List result = new ArrayList();
		List msgs = getLatestMsgs();
		for (Iterator it = msgs.iterator(); it.hasNext();) {
			Log msg = (Log) it.next();
			if (msg instanceof ActionLog)
				result.add(((ActionLog) msg).getInfo());
		}
		trace.out("log", "sink.getLatestInfoFields() retrieves "+result.size());
		return result;
	}

	/**
	 * Compare a list of strings against expected ones.
	 * @param label
	 * @param expected
	 */
	public void checkStringsVsInfoFields(String label, String[] expected) {
		checkStringsVsInfoFields(label, expected, true);
	}

	/**
	 * Compare a list of strings against expected ones.
	 * @param label
	 * @param expected
	 * @param doAsserts if false, don't do asserts
	 */
	public void checkStringsVsInfoFields(String label, String[] expected, boolean doAsserts) {
		List infoFields = getLatestInfoFields();
		int i = 0;
		for (Iterator it = infoFields.iterator(); it.hasNext(); ++i)
			trace.out("log", label+"["+i+"]:\n "+it.next());
		if (doAsserts)
			TestCase.assertEquals("Wrong number of "+label, expected.length, infoFields.size());
		i = 0;
		for (Iterator it = infoFields.iterator(); it.hasNext() && i < expected.length; ++i) {
			String actualItem = (String)it.next();
			if (doAsserts)
				TestCase.assertEquals(label+"["+i+"]", expected[i].trim(), actualItem.trim());
		}
	}
}
