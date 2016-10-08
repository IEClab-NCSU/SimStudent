/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.TutoringService;

import java.util.Date;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * Test {@link TransactionInfo}.
 */
public class TransactionInfoTest extends TestCase {

	static {
		trace.addDebugCode("tx");
	}
	public static Test suite() {
		return new TestSuite(TransactionInfoTest.class);
	}

	/**
	 * Simulate running a problem with several steps, each lasting
	 * a given duration.
	 */
	private class Problem {
		private String problemName;
		private int[] times;
		/**
		 * @param problemName
		 * @param times step durations, in ms
		 */
		Problem(String problemName, int ... times) {
			this.problemName = problemName;
			this.times = times;
		}
		/**
		 * Call the {@link TransactionInfo} methods, waiting the given
		 * durations.
		 */
		public void execute() {
			txInfo.update(Boolean.FALSE);
			for (int i = 0; i < times.length; ++i) {
				TransactionInfo.Single txInfo0 = txInfo.create();
				Utils.sleep(times[i]);
				txInfo0.update(Boolean.TRUE);
				if (trace.getDebugCode("tx"))
					trace.outNT("tx", "problem "+problemName+"["+i+"]: "+txInfo0.toAttributes());
			}
			txInfo.update(Boolean.TRUE);
		}
	};
	
	/**
	 * Simulate a session running several problems.
	 */
	private class Session extends Thread {
		private String sessionId;
		private Problem[] problems;
		Session (String sessionId, Problem[] problems) {
			this.sessionId = sessionId;
			this.problems = problems;
		}
		/**
		 * Run each problem in {@link #problems}.
		 */
		public void run() {
			for (Problem p : problems) {
				p.execute();
				if (trace.getDebugCode("tx"))
					trace.outNT("tx", "completed "+sessionId+", "+p.problemName+": "+txInfo.toAttributes());
				Utils.sleep(100);
			}
		}
	};
	
	private TransactionInfo txInfo = new TransactionInfo();
	
	private Problem[] problems = new Problem[] {
			new Problem("p1", 100, 200, 300),	
			new Problem("p2", 500, 300),	
			new Problem("p3", 100, 600, 300, 400, 200)	
	};
	
	Session[] sessions = new Session[] {
			new Session("S1", problems),	
			new Session("S2", problems),	
			new Session("S3", problems),	
			new Session("S4", problems)
	};

	public void testProblemSessions() {
		int txCount = 0;
		long firstDuration = -1;
		long maxDuration = -1;
		long totalDuration = 0;
		for (Problem p : problems) {
			txCount += p.times.length;
			for (int t : p.times) {
				if (firstDuration < 0)
					firstDuration = t;
				maxDuration = Math.max(maxDuration, t);
				totalDuration += t;
			}
		}
		totalDuration *= sessions.length;
		Date earliestTxTime = new Date();

		for (Session sess : sessions)
			sess.start();
		for (Session sess : sessions) {
			try {
				sess.join(10000);  // max wait 10 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Date latestTxTime = new Date();
		
		Map<String, String> attrs = txInfo.toAttributes();
		for (String sessionId : attrs.keySet())
			System.out.printf("%-30s=\"%s\"\n", sessionId, attrs.get(sessionId));
		
		assertTrue("first tx start time", earliestTxTime.getTime() <= txInfo.getFirst().getStartTime().getTime() &&
				txInfo.getFirst().getStartTime().getTime() <= latestTxTime.getTime());
		assertTrue("first tx duration should be near "+firstDuration+", was "+txInfo.getFirst().getDuration(),
				firstDuration <= txInfo.getFirst().getDuration() &&
				txInfo.getFirst().getDuration() < firstDuration+100);
		assertTrue("longest tx start time", earliestTxTime.getTime() <= txInfo.getLongest().getStartTime().getTime() &&
				txInfo.getLongest().getStartTime().getTime() <= latestTxTime.getTime());
		assertTrue("longest tx duration should be near "+maxDuration+", was "+txInfo.getLongest().getDuration(),
				maxDuration <= txInfo.getLongest().getDuration() &&
				txInfo.getLongest().getDuration() < maxDuration+100);
		assertTrue("total duration should be near "+totalDuration+", was "+txInfo.getTotalDuration(),
				totalDuration <= txInfo.getTotalDuration() &&
				txInfo.getTotalDuration() < totalDuration+500);
		assertEquals("transactions started", sessions.length*txCount, txInfo.getTransactionCount());
		assertEquals("transactions completed", sessions.length*txCount, txInfo.getTransactionCompletedCount());
		assertEquals("problems started", sessions.length*problems.length, txInfo.getProblemCount());
		assertEquals("problems completed", sessions.length*problems.length, txInfo.getProblemCompletedCount());
	}
}
