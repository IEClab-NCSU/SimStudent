/**
 * Copyright 2007 Carnegie Mellon University.
 */

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Logging data collected.
 */
public class LogInfo {
	
	/** Attribute name for {@link #forwardLogEntries}. */
	private static final String FORWARD_LOG_ENTRIES = "forwardLogEntries";

	/** Attribute name for {@link #diskLogEntries}. */
	private static final String DISK_LOG_ENTRIES = "diskLogEntries";

	/** Attribute name for {@link #forwardLogErrors}. */
	private static final String FORWARD_LOG_ERRORS = "forwardLogErrors";

	/** Attribute name for {@link #diskLogErrors}. */
	private static final String DISK_LOG_ERRORS = "diskLogErrors";

	/** Count of log messages written to disk. */
	private long diskLogEntries = 0;

	/** Count of log messages forwarded to a log server. */
	private long forwardLogEntries = 0;

	/** Number of errors recorded while trying to log messages disk. */
	private long diskLogErrors = 0;

	/** Number of errors recorded while trying to forward messages to a log server. */
	private long forwardLogErrors = 0;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[disk ").append(this.diskLogEntries);
		sb.append(", fwd ").append(this.forwardLogEntries);
		return sb.append(']').toString();
	}

	/**
	 * @return Map with keys {@value #FORWARD_LOG_ENTRIES}, {@value #DISK_LOG_ENTRIES}, ...
	 */
	public Map<String, String> toAttributes() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put(DISK_LOG_ENTRIES, Long.toString(diskLogEntries));
		result.put(FORWARD_LOG_ENTRIES, Long.toString(forwardLogEntries ));
		result.put(DISK_LOG_ERRORS, Long.toString(diskLogErrors));
		result.put(FORWARD_LOG_ERRORS, Long.toString(forwardLogErrors ));
		return result;
	}

	/** 
	 * Increment log messages written to disk.
	 * @return incremented {@link #diskLogEntries}
	 */
	public synchronized long incrementDiskLogEntries() { return ++diskLogEntries; }

	/** 
	 * Increment log messages forwarded to a log server.
	 * @return incremented {@link #forwardLogEntries}
	 */
	public synchronized long incrementForwardLogEntries() { return ++forwardLogEntries; }

	/** 
	 * Increment errors recorded while trying to log messages disk.
	 * @return incremented {@link #diskLogErrors}
	 */
	public synchronized long incrementDiskLogErrors() { return ++diskLogErrors; }

	/** 
	 * Increment errors recorded while trying to forward messages to a log server.
	 * @return incremented {@link #forwardLogErrors}
	 */
	public synchronized long incrementForwardLogErrors() { return ++forwardLogErrors; }

}
