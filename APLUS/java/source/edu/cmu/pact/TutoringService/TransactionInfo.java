/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.TutoringService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class to maintain cost and performance information about transactions.
 * First used to monitor tutoring system performance.
 */
public class TransactionInfo {

	/** Date format for XML attributes, from {@link Monitor#dateFmt}. */
	private static final DateFormat externalDateFmt = Monitor.dateFmt;
	
	/** Internal date format for #toString(). */
	private static final DateFormat dateFmt = new SimpleDateFormat("HH:mm:ss.SSS");

	/** Information about a single transaction. */
	public class Single {

		/** Element name for {@link #toElement()}. */
		private static final String TAG = "Transaction";

		/** Attribute name for {@link #startTime}. */
		private static final String START_TIME = "startTime";

		/** Attribute name for {@link #duration}. */
		private static final String DURATION = "duration";

		/** Transaction start time. */
		private Date startTime = new Date();

		/** Elapsed time spent processing this transaction, in milliseconds. */
		private long duration = -1;  // initialize to "unset"
		
		/**
		 * @return the {@link #startTime}
		 */
		public Date getStartTime() {
			return startTime;
		}

		/**
		 * Return the {@link #duration} in milliseconds, if set. Else return
		 * the number of milliseconds since {@link #getStartTime()}.
		 * @return the {@link #duration} or the current elapsed time
		 */
		public long getDuration() {
			if (duration < 0)
				return System.currentTimeMillis() - startTime.getTime();
			return duration;
		}
		
		/**
		 * Set the #duration as the number of milliseconds since
		 * {@link #getStartTime()}. Calls {@link TransactionInfo#updateAfterTransaction(Single)}.
		 * @return the {@link #duration}
		 */
		public long setEndTime() {
			duration = System.currentTimeMillis() - startTime.getTime();
			updateAfterTransaction(this);
			return duration;
		}

		/**
		 * @return Map with keys {@value #START_TIME}, {@value #DURATION}.
		 * @see edu.cmu.hcii.MonitorInfo#toAttributes()
		 */
		public Map<String, String> toAttributes() {
			Map<String, String> result = new LinkedHashMap<String, String>();
			result.put(START_TIME, externalDateFmt.format(startTime));
			result.put(DURATION, Long.toString(duration));
			return result;
		}

		/**
		 * Call {@link #setEndTime()}.
		 * @param info
		 * @see edu.cmu.hcii.MonitorInfo#updateAfterTransaction(Object)
		 */
		public void update(Object info) {
			if (info instanceof Boolean && ((Boolean) info).booleanValue())
				setEndTime();
		}
	}
	
	/**
	 * @return the {@link #problemCount}
	 */
	public int getProblemCount() {
		return problemCount;
	}

	/** The number of transactions started. */
	private int transactionCount = 0;
	
	/** The number of transactions completed. */
	private int transactionCompletedCount = 0;
	
	/** The number of problems started. */
	private int problemCount = 0;
	
	/** The number of problems completed. */
	private int problemCompletedCount = 0;

	/** The first transaction recorded. */
	private Single first = null;

	/** The last transaction recorded. */
	private Single latest = null;

	/** The slowest transaction recorded. */
	private Single longest = null;

	/** Total time spent on all {@link Single} transactions. */
	private long totalDuration = 0;
	
	/**
	 * Create a {@link Single Single} instance.
	 * Increments {@link #transactionCount}, sets {@link #latest}, calls
	 */
	public synchronized Single create() {
		latest = new Single();
		transactionCount++;
		updateAfterTransaction(latest);
		return latest;
	}

	/**
	 * Revise the summary information using the given
	 * individual transaction info.
	 * @param current single transaction info
	 */
	private synchronized void updateAfterTransaction(Single current) {
		if (first == null)
			first = longest = current;
		if (current.duration >= 0) {
			totalDuration += current.duration;
			transactionCompletedCount++;
			if (longest.duration < current.duration)
				longest = current;
		}
	}

	/**
	 * Create a map of attribute name=&gt;value pairs, e.g.:
	 * <li>totalTransactionMs="1323"</li>
  	 * <li>transactionCount="23"</li>
  	 * <li>firstTransactionTime="Wed, 02 Feb 2012 14:22:33 GMT"</li>
  	 * <li>longestTransactionMs="523123"</li>
  	 * <li>longestTransactionStartTime="Wed, 02 Feb 2012 03:22:33 GMT"</li>
	 * @return map with iterator in this order (above)
	 * @see edu.cmu.hcii.MonitorInfo#toAttributes()
	 */
	public Map<String, String> toAttributes() 
	{
		Map<String, String> result = new LinkedHashMap<String, String>();
		
		result.put("totalTransactionMs", Long.toString(totalDuration));
		result.put("transactionCount", Integer.toString(transactionCount));
		result.put("firstTransactionTime",(first == null ? "" : externalDateFmt.format(first.startTime)));
		result.put("longestTransactionMs",(longest == null ? "0" : Long.toString(longest.duration)));
		result.put("longestTransactionStartTime",(longest == null ? "" : externalDateFmt.format(longest.startTime)));
		
		return result;
	}

	/**
	 * Create a map of raw time attribute name=&gt;value pairs, e.g.:
	 * <li>totalTransactionMs="1323"</li>
  	 * <li>transactionCount="23"</li>
  	 * <li>firstTransactionTime="24254935624"</li>
  	 * <li>longestTransactionMs="523123"</li>
  	 * <li>longestTransactionStartTime="035368342826"</li>
	 * @return map with iterator in this order (above)
	 * @see edu.cmu.hcii.MonitorInfo#toAttributes()
	 */
	public Map<String, String> toAttributesRaw() 
	{
		Map<String, String> result = new LinkedHashMap<String, String>();
		
		result.put("totalTransactionMs", Long.toString(totalDuration));
		result.put("transactionCount", Integer.toString(transactionCount));
		result.put("firstTransactionTime",(first == null ? "" : String.format ("%d",first.startTime.getTime())));
		result.put("longestTransactionMs",(longest == null ? "0" : Long.toString(longest.duration)));
		result.put("longestTransactionStartTime",(longest == null ? "" : String.format ("%d",longest.startTime.getTime())));
		
		return result;
	}	
	
	/**
	 * Increment {@link #problemCompletedCount} if argument is {@link Boolean#TRUE},
	 * else increment {@link #problemCount}. 
	 * @param info 
	 * @see edu.cmu.hcii.MonitorInfo#updateAfterTransaction(java.lang.Object)
	 */
	public synchronized void update(Object info) {
		if (info instanceof Boolean && ((Boolean) info).booleanValue())
			problemCompletedCount++;
		else
			problemCount++;
	}

	/**
	 * @return the {@link #first}
	 */
	public Single getFirst() {
		return first;
	}

	/**
	 * @return the {@link #latest}
	 */
	public Single getLatest() {
		return latest;
	}

	/**
	 * @return the {@link #longest}
	 */
	public Single getLongest() {
		return longest;
	}

	/**
	 * @return the {@link #transactionCount}
	 */
	public int getTransactionCount() {
		return transactionCount;
	}

	/**
	 * @return the {@link #transactionCompletedCount}
	 */
	public int getTransactionCompletedCount() {
		return transactionCompletedCount;
	}

	/**
	 * @return the {@link #problemCompletedCount}
	 */
	public int getProblemCompletedCount() {
		return problemCompletedCount;
	}

	/**
	 * @return the {@link #totalDuration}
	 */
	public long getTotalDuration() {
		return totalDuration;
	}
}
