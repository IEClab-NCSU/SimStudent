/*
 * $Id: AsyncStreamLogger.java 12714 2011-07-14 23:40:40Z sewall $
 */
package edu.cmu.oli.log.client;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.xml.sax.SAXException;

import edu.cmu.pact.Utilities.trace;

/**
 * A variant of {@link StreamLogger} that runs in a separate thread
 * so that logger callers need not wait for logging transactions to
 * be recorded.  Warning: this class effectively buffers log transactions
 * to memory while awaiting server acceptance of the log data.
 * The results are
 * <ol>
 * <li>possible data loss: in the event of abrupt
 * system exit, buffered transactions not yet sent to the server are lost; and
 * <li>possible resource exhaustion: in the event that the host never
 * comes up, the buffered transactions grow without bound.
 * </ol>
 */
public class AsyncStreamLogger extends StreamLogger
									   implements Runnable {

	/**
	 * Interface for classes using this class.
	 */
	public static interface Listener {

		/**
		 * Notify the user class of an error in logging.
		 *
		 * @param  errorDescription should describe the error that occurred
		 *             in terms that might be shown to a user
		 */
		public void notifyError(String errorDescription);
	}

	/** Queue of messages to send. Element type is Log object. */
	private LinkedList queue = null;

	/** DiskLogger for use when server unavailable. */
	private DiskLogger diskLogger = null;

	/** Filename for {@link #diskLogger}. Path is current working directory. */
	private String diskLoggerFile = null;

	/** Filename prefix for {@link #diskLoggerFile} instances. */
	private static final String FILE_PREFIX = "tutorlog_";

	/** Filename suffix for {@link #diskLoggerFile} instances. */
	private static final String FILE_SUFFIX = ".xml";

	/** Set of listener classes, to notify in case of failure. */
	private Set listeners = null;

	/**
	 * Calls superclass constructor, creates {@link #queue}, starts thread.
	 * Registers caller.
	 */
	public AsyncStreamLogger(Listener listener) {
		super();
		listeners = Collections.synchronizedSet(new HashSet());
		queue = new LinkedList();
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * Process the queue of messages until it is exhausted.  Then wait
	 * for more.
	 */
	public void run() {
		Log msg = null;

		while (null != (msg = dequeue())) {
			Boolean result = new Boolean(false);
			trace.out("" + Thread.currentThread().getName() +
					  " dequeued:\n" + msg + "\n");

			if (msg instanceof ActionLog)
				result = logActionLog((ActionLog) msg);
		    else if (msg instanceof SessionLog)
				result = logSessionLog((SessionLog) msg);
		    else {
				System.err.println(getClass().getName() +
								   "run(): Unsupported Log class " +
								   msg.getClass());
			}
			if (!result.booleanValue())
				logToDisk(msg);
			else
				sendQueuedMessages();
		}
	}

	/**
	 * Log a message to disk. Use this when server logging is failing.
	 * Creates {@link #diskLogger} if none yet exists.
	 * Creates file named {@link #diskLoggerFile} unless already exists.
	 *
	 * @param  msg message to log
	 * @return result from {@link #diskLogger} method call
	 */
	private Boolean logToDisk(Log msg) {

		Boolean result = new Boolean(false);

		if (diskLoggerFile == null) {
			trace.printStack("log", "start logging");
			diskLoggerFile = FILE_PREFIX +
				String.valueOf(System.currentTimeMillis()) + FILE_SUFFIX;
			diskLogger = new DiskLogger();
			diskLogger.setOutfile(diskLoggerFile);
		}		
		if (msg instanceof ActionLog)
			result = diskLogger.logActionLog((ActionLog) msg);
		else if (msg instanceof SessionLog) {
			try {
				result = diskLogger.logSessionLog((SessionLog) msg);
			} catch (SAXException se) {
				System.err.println("Error logging to disk:\n\"" + msg + "\"");
				se.printStackTrace();
			} catch (UnsupportedEncodingException uee) {
				System.err.println("Error logging to disk:\n\"" + msg + "\"");
				uee.printStackTrace();
			}
		} else {
				System.err.println(getClass().getName() +
								   "logToDisk(): Unsupported Log class " +
								   msg.getClass());
		}
		return result;
	}

	/**
	 * Send a file of messages to the server. Use this when the server
	 * has returned to operation and some messages may have been saved
	 * to disk during its outage.  No-op if no {@link #diskLoggerFile}.
	 * Nulls {@link #diskLoggerFile}, {@link #diskLogger} and deletes
	 * file if sends entire file successfully.
	 *
	 * @return number of messages sent
	 */
	private int sendQueuedMessages() {
		int result = 0;
		if (diskLoggerFile == null)
			return result;

		/* !!!
		 * do while more messages and no error
		 *   read file until "<?" at start of next prologue; push back "<?";
		 *   if stream log succeeds 
		 *     continue
		 *   saveRemainderOfFile(file.getPosition())
		 * done
		 * if finished messages
		 *   delete file
		 *   null diskLoggerFile, diskLogger
		 */
		return result;
	}

	/**
	 * Remove and return the message at the head of the queue.
	 * Calls wait() until queue is nonempty.
	 *
	 * @return message from head of queue
	 */
	private synchronized Log dequeue() {
		while (queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		return (Log) queue.removeFirst();
	}

	/**
	 * Enqueue a message for later processing.  Calls notifyAll()
	 * after adding to wake up waiting dequeue().
	 *
	 * @param  msg message to queue
	 * @return length of the queue after adding msg
	 */
	private synchronized int enqueue(Log msg) {
		queue.addLast(msg);
		int result = queue.size();
		notifyAll();
		return result;
	}

	/**
	 * Override of superclass method enqueues message for logging.
	 *
	 * @param  msg log msg
	 * @return always returns Boolean(true)
	 */
	public Boolean logSessionLog(SessionLog msg) {
		enqueue(msg);
		return new Boolean(true);
	}

	/**
	 * Override of superclass method enqueues message for logging.
	 *
	 * @param  msg log msg
	 * @return always returns Boolean(true)
	 */
	public Boolean logActionLog(ActionLog msg) {
		enqueue(msg);
		return new Boolean(true);
	}

	/**
	 * Remove a listener from the listener set.
	 *
	 * @param  listener {@link AsyncStreamLogger.Listener} to remove 
	 */
	public synchronized void removeListener(Listener listener) {
		if (listeners != null)
			listeners.remove(listener);
	}
}
