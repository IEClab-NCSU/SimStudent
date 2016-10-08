/**
 * Copyright 2012 Carnegie Mellon University.
 */
package edu.cmu.pact.TutoringService;

import edu.cmu.pact.Log.LogInfo;

/**
 * Collects logging information for the {@link Monitor}. The top-level class
 * accumulates aggregate numbers from all loggers.
 */
public class TSLogInfo extends LogInfo {
	
	/**
	 * Logging information for a single session.
	 */
	public class Session extends LogInfo {

		/**
		 * Also increments summary by calling {@link TSLogInfo#incrementDiskLogEntries()}.
		 * @return super{@link #incrementDiskLogEntries()}
		 * @see LogInfo#incrementDiskLogEntries()
		 */
		public synchronized long incrementDiskLogEntries() {
			TSLogInfo.this.incrementDiskLogEntries();
			return super.incrementDiskLogEntries();
		}

		/**
		 * Also increments summary by calling {@link TSLogInfo#incrementDiskLogErrors()}.
		 * @return super{@link #incrementDiskLogErrors()}
		 * @see LogInfo#incrementDiskLogErrors()
		 */
		public synchronized long incrementDiskLogErrors() {
			TSLogInfo.this.incrementDiskLogErrors();
			return super.incrementDiskLogErrors();
		}

		/**
		 * Also increments summary by calling {@link TSLogInfo#incrementForwardLogEntries()}.
		 * @return super{@link #incrementForwardLogEntries()}
		 * @see LogInfo#incrementForwardLogEntries()
		 */
		public synchronized long incrementForwardLogEntries() {
			TSLogInfo.this.incrementForwardLogEntries();
			return super.incrementForwardLogEntries();
		}

		/**
		 * Also increments summary by calling {@link TSLogInfo#incrementForwardLogErrors()}.
		 * @return super{@link #incrementForwardLogErrors()}
		 * @see LogInfo#incrementForwardLogErrors()
		 */
		public synchronized long incrementForwardLogErrors() {
			TSLogInfo.this.incrementForwardLogErrors();
			return super.incrementForwardLogErrors();
		}
		
	}
	
	/**
	 * Create a {@link TSLogInfo.Session} instance.
	 */
	public Session create() {
		return new Session();
	}
}
