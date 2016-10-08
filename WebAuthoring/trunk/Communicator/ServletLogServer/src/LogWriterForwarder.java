/**
 * Copyright 2014 Carnegie Mellon University.
 */

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

/**
 * Facility to log a message and optionally forward it to a server.
 * Must call {@link #setFile(File)} to open the log file; appends if file exists.
 * To enable forwarding, call {@link #setLogServerURL(String)}.
 */
public abstract class LogWriterForwarder extends LinkedList<MessageReplyPair> implements Runnable {

	private static final long serialVersionUID = -201406031700L;
	
	private static final String STATUS_SUCCESS = "status=success\r\n";

	private static final String STATUS_FAILURE = "status=failure\r\n";

	/** Name of preference that turns this class on and off. */
	public static final String ENABLE_LOG_SERVICE = "EnableLogService";
	
	/** Name of preference that turns this class on and off. */
	public static final String ENABLE_LOG_FORWARDING = "EnableLogForwarding";

	/** {@link #enqueue(String)} this message {@value #QUIT_MSG} to exit thread. */
	public static final String QUIT_MSG = "<quit/>";

	/** I/O error count threshold before current log file closed. */
	private static final int MAX_IO_ERROR_COUNT = 10;
	
	/** Filename of log file. */
	protected File logFile = null;

	/** To write the file. */
	protected BufferedWriter writer = null;

	/** I/O error count on current log file. */
	protected int errCount;
	
	/** Web address of the logging server to forward to. */
	protected String logServerURL = null;

	/** Information for the monitor. */
	protected LogInfo logInfo;

	/**
	 * Close the {@link #writer} and so close the file. Nulls {@link #writer}, {@link #logFile}.
	 */
	private synchronized void close() {
		if (writer == null)
			return;
		try {
			writer.flush();
			writer.close();
		} catch (Exception ioe) {
			trace.errStack("LogServlet.close(): error closing writer for log file "+logFile, ioe);
		}
		writer = null;
		logFile = null;
	}
	
	/**
	 * Read the queue until a {@link #QUIT_MSG} ({@value #QUIT_MSG}) is found.
	 * Try to forward each message found.
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		MessageReplyPair msgPlus = null;
		while (!(QUIT_MSG.equalsIgnoreCase((msgPlus = dequeue()).getMessage()))) {
			if (trace.getDebugCode("logservice")) trace.out("logservice", "thread "+Thread.currentThread().getName()
					+ " queue length "+size()+", dequeued:\n\n"+msgPlus+"\n");
			boolean responded = false;

			String response = forwardContent(msgPlus.getMessage());
			if (response != null)
				responded = sendResponse(msgPlus, response);    // send reply from forwardContent()
			if (!responded)
				responded = sendResponse(msgPlus, STATUS_SUCCESS+"cause=no_logging_attempted\r\n");
			if (!responded && msgPlus.isReplyRequired())
				trace.err("failed to reply to message: "+msgPlus.toString());
		}
		close();
	}

	/**
	 * Remove and return the message at the head of the queue. Calls wait()
	 * until queue is nonempty.
	 * @return message from head of queue
	 */
	public synchronized MessageReplyPair dequeue() {
		while (isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		return removeFirst();
	}

	/**
	 * Cause the forwarding thread to exit by queuing a quit command.
	 */
	public void exit() {
		enqueue(new MessageReplyPair(QUIT_MSG, null));
	}

	/**
	 * Enqueue a message for later processing. Calls notifyAll() after
	 * adding to wake up waiting dequeue().
	 * @param msg message to queue
	 * @return length of the queue after adding msg
	 */
	public int enqueue(MessageReplyPair msgPlus) {
		int result = size();
		synchronized(this) {
			addLast(msgPlus);
			result = size();  // revised after addLast()
			notifyAll();
		}
		return result;
	}

	/**
	 * Write a message to the file and formulate a response.
	 * @param msgPlus holds the message to log
	 * @return response to return to sender; null if this call was a no-op
	 */
	protected String log(MessageReplyPair msgPlus) {
		String response = null;
		if(!shouldLog())
			return response;

		if (trace.getDebugCode("logservice")) trace.out("logservice", "log("+msgPlus.toString()+")");
		synchronized(writer) {
			try {
				writer.write(msgPlus.getMessage().trim());   // trim: no trailing newline
				writer.flush();
				errCount = 0;             // zero on each successful write
				response = STATUS_SUCCESS;
				logInfo.incrementDiskLogEntries();
			} catch (IOException ioe) {
				errCount++;
				logInfo.incrementDiskLogErrors();
				trace.errStack("LogServlet.log() error (count now "+errCount+") writing log: "+ioe+
						(ioe.getCause() == null ? "" : ";\n cause "+ioe.getCause()), ioe);
		        response = STATUS_FAILURE+"cause=internal_error\r\n";
				if (errCount > MAX_IO_ERROR_COUNT) {
			        String message = "*** CLOSING LOG **** LogWriterForwarder.log() I/O error count exceeds threshold "+MAX_IO_ERROR_COUNT;
					trace.err(message);
					close();
					response += ("message="+message+"\r\n");
				} else
		        	response += ("message=Error writing to file: "+ioe.toString()+"\r\n");
			}
		}
		if (trace.getDebugCode("logservice")) trace.out("logservice", "log(): "+response);
		return response;
	}

	/**
	 * Forward the given buffer to the {@link #logSvcUrl}, if defined.
	 * @param msg buffer to send
	 * @return response for client
	 */
	protected String forwardContent(String msg) {
		if(!canForward())
			return null;

		String response = null;
		HttpURLConnection conn = null;
		URL url = null;
		try {
			url = new URL(getLogServerURL());
			conn = openConnection(url);
			OutputStream os = conn.getOutputStream();
			os.write(msg.getBytes());
			os.flush();
			os.close();
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream readBuf = new ByteArrayOutputStream();
			int i = 0;
			for (int c = is.read(); c >= 0; ++i, c = is.read())
				readBuf.write(c);
			if (trace.getDebugCode("log"))
				trace.outNT("log", "content of "+getLogServerURL()+" response (length "+i+"): "+
						readBuf+"; headers "+conn.getHeaderFields());
			response = STATUS_SUCCESS;
			logInfo.incrementForwardLogEntries();
		} catch (Exception ex) {
			logInfo.incrementForwardLogErrors();
			trace.errStack("LogServlet.sendContent() error "+ex, ex);
	        response = STATUS_FAILURE+"cause=forwarding_error\r\n";
        	response += ("message=Error writing to URL "+url+": "+ex.toString()+"\r\n");
		}
		if (trace.getDebugCode("logservice")) trace.out("logservice", "forwardContent(): "+response);
	    return response;
	}

	/**
	 * Override this method if log forwarding depends on more than a call to
	 * {@link #setLogServerURL(String)}.
	 * @return true if {@link #getLogServerURL()} is not null and not empty
	 */
    protected boolean canForward() {
    	return getLogServerURL() != null && getLogServerURL().length() > 0;
	}

	/**
     * Open a URL for an HTTP connection. Sets method POST, Content-Type text/xml.
     * Tries {@link URLConnection#connect()}. From edu/cmu/oli/log/client/StreamLogger.java.
     * @param url
     * @return connection
     * @throws IOException
     */
    private HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml");
        conn.setRequestProperty("Checksum", "They call it a log");  // adapted from OLI code
        conn.connect();
        return conn;
    }

	/**
	 * @param logInfo new value for {@link #logInfo}
	 */
	public void setLogInfo(LogInfo logInfo) {
		if (trace.getDebugCode("ls")) trace.out("ls", "LogServlet setLogInfo: logInfo = "+logInfo);
		this.logInfo = logInfo;
	}

	/**
	 * Try to log and/or queue a message and reply to the sender.
	 * @param msgPlus
	 */
	public void logOrQueueAndReply(MessageReplyPair msgPlus) {
		boolean responded = false;
		String response = log(msgPlus);       // returns null if shouldn't log
		if (response != null)
			responded = sendResponse(msgPlus, response);
		msgPlus.setReplyRequired(!responded);
		responded |= queueAndReply(msgPlus);  // returns false if can't queue
		if(!responded)
			sendResponse(msgPlus, STATUS_FAILURE+"cause=service_unavailable\r\n");
	}

	/**
	 * Try to queue a log message for forwarding and reply to the sender.
	 * @param msgPlus
	 * @return false if {@link #canForward()} returns false; else result of
	 *         {@link #sendResponse(MessageReplyPair, String)} after queuing
	 */
	private boolean queueAndReply(MessageReplyPair msgPlus) {
		if(!canForward())
			return false;
		int queueLength = enqueue(msgPlus);
		return sendResponse(msgPlus, STATUS_SUCCESS+"queue_position="+queueLength+"\r\n");
	}

	/**
	 * Create or open the log file to append. Sets {@link #logFile}, {@link #writer}.
	 * @param logFile new value for #logFile
	 */
	public void setFile(File logFile) {
		this.logFile = logFile; 
		try {
			writer = new BufferedWriter(new FileWriter(logFile, true));
		} catch (Exception e) {
			logInfo.incrementDiskLogErrors();
	        trace.errStack("Error opening file "+logFile+": "+e.toString()+"; cause: "+e.getCause(), e);
			writer = null;
		}
	}

	/**
	 * @param logServerURL new value for {@link #logServerURL}
	 */
	public void setLogServerURL(String logServerURL) {
		this.logServerURL = logServerURL;
	}

	/**
	 * @return the {@link #logServerURL}
	 */
	public String getLogServerURL() {
		return logServerURL;
	}

	/**
	 * Whether we should log. Override this method if there's a means to disable logging. 
	 * @return true in this superclass
	 */
	protected boolean shouldLog() {
		return true;
	}

	/**
	 * Send a reply.
	 * @param msgPlus has reply mechanism
	 * @param response content to write
	 * @return true if wrote a message
	 */
	protected abstract boolean sendResponse(MessageReplyPair msgPlus, String response);
}
