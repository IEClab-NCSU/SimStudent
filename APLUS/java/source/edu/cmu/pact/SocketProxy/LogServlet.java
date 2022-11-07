/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.SocketProxy;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.hcii.ctat.CTATHTTPExchange;
import edu.cmu.hcii.ctat.ExitableServer;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Log.LogInfo;
import edu.cmu.pact.Log.LogWriterForwarder;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

/**
 * Substitute for the OLI Logging Service.
 */
public class LogServlet extends LogWriterForwarder implements ExitableServer 
{
	private static final long serialVersionUID = 201406031840L;

	/** Name of preference that turns this class on and off. */
	public static final String ENABLE_LOG_SERVICE = "EnableLogService";
	
	/** Name of preference that turns this class on and off. */
	public static final String ENABLE_LOG_FORWARDING = "EnableLogForwarding";
	
	/** Formatter for filename. */
	private static SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.SSS");
	
	/** Formatter for Http response. */
	private static SimpleDateFormat respDateFmt =       //Date: Wed, 02 Feb 2011 21:20:09 EST
		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

	/** Match an HTTP POST header */
	private static Pattern HttpPostHeader = 
		Pattern.compile("^POST \\S+ HTTP.*\\r?\\n\\r?\\n", Pattern.DOTALL);

	/** Access to parameters. */
	private PreferencesModel prefs = null; 
	
	/** Access to parameter overrides. */
	private MessageObject setPrefsMsg = null;

	/** True if called from the Tutoring Service; false if at author time. */
	private final boolean inTutoringServiceMode;

	/** If not null, use this as the base filename (excludes the .log extension). */
	private String baseFilename = null;

	/**
	 * Constructor for clients that don't know the session id.
	 * @param prefs
	 * @param inTutoringServiceMode
	 */
	public LogServlet(PreferencesModel prefs, boolean inTutoringServiceMode) {
		this(prefs, null, inTutoringServiceMode, null);
	}

	/**
	 * Constructor for a known session id, which becomes the {@link #baseFilename}.
	 * Initialize fields and call {@link #reset()}.
	 * @param prefs source for initialization parameters
	 * @param setPrefsMsg overrides for initialization parameters
	 * @param inTutoringServiceMode
	 * @param baseFilename
	 */
	public LogServlet (PreferencesModel prefs, 
					   MessageObject setPrefsMsg,
					   boolean inTutoringServiceMode,
					   String baseFilename) 
	{
		this.prefs = prefs;
		this.setPrefsMsg = setPrefsMsg;
		this.inTutoringServiceMode = inTutoringServiceMode;
		this.baseFilename = "LogService-"+(baseFilename != null ? baseFilename : dateFmt.format(new Date()));
		
		// MvV: append a path so that the logfile ends up in the logs directory
		// for the local tutorshop
		
		/*
		if (this.baseFilename.indexOf(CTATLink.logdir)==-1)
		{
			this.baseFilename=(CTATLink.logdir+this.baseFilename);
		}
		*/	
		
		reset();
	}
	
	private synchronized void reset() {
		Boolean enable = getBooleanPreference(ENABLE_LOG_SERVICE, null);
		String dirname = getPreference(BR_Controller.DISK_LOGGING_DIR, Logger.DISK_LOG_DIR_PROPERTY);
		String filename = baseFilename+".log";
		if (trace.getDebugCode("log")) trace.outNT("log", "LogServlet.reset(): enable "+enable+
				", directory "+dirname+", file "+filename);

		errCount = 0;             // zero since no longer trying that file
		if(enable == null || !enable.booleanValue()) {
			close();
			return;
		}
		try {
			logFile = new File(dirname, filename);
			writer = new BufferedWriter(new FileWriter(logFile, true));
		} catch (Exception e) {
			trace.errStack("Error opening file "+filename+" in directory "+dirname+": "+e, e);
			writer = null;
			logFile = null;
		}
	}

	/**
	 * Return a String preference from {@link #prefs}, possibly overridden by {@link #setPrefsMsg}. 
	 * @param prefName preference name for {@link PreferencesModel#getStringValue(String)}
	 * @param propName if not null, property name for {@link MessageObject#getProperty(String)}.
	 * @return value from {@link #setPrefsMsg} or {@link #prefs} or null
	 */
	private String getPreference(String prefName, String propName) {
		String result = null;
		if (setPrefsMsg != null) {
			Object prop = setPrefsMsg.getProperty(propName == null ? prefName : propName);
			if (prop != null)
				result = prop.toString();
		}
		if (result == null && prefs != null)
			result = prefs.getStringValue(prefName);
		if (trace.getDebugCode("log")) trace.out("log", "getPref("+prefName+", "+propName+") => "+result);
		return result;
	}

	/**
	 * Return a Boolean preference from {@link #prefs}, possibly overridden by
	 * {@link #setPrefsMsg}. 
	 * @param prefName preference name for {@link PreferencesModel#getBooleanValue(String)}
	 * @param propName if not null, property name for {@link MessageObject#getPropertyAsBoolean(String)}.
	 * @return value from {@link #setPrefsMsg} or {@link #prefs} or null
	 */
	private Boolean getBooleanPreference(String prefName, String propName) {
		Boolean result = null;
		if (setPrefsMsg != null)
			result = setPrefsMsg.getPropertyAsBoolean(propName == null ? prefName : propName);
		if (result == null && prefs != null)
			result = prefs.getBooleanValue(prefName);
		if (trace.getDebugCode("log")) trace.out("log", "getBooleanPref("+prefName+", "+propName+") => "+result);
		return result;
	}

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
	 * Format an HTTP reply, write it to the socket and close the socket.
	 * @param sock socket to write
	 * @param response content to write
	 * @return true if wrote a message
	 */
	protected boolean sendResponse(Socket sock, String response) {
		if (trace.getDebugCode("logservice"))
			trace.out("logservice", "sendResponse("+sock+","+response+")");
		if (sock == null)
			return false;
		boolean result = false;
		try {
			PrintWriter pw = new PrintWriter(sock.getOutputStream());
			pw.write("HTTP/1.1 200 OK\r\n");
			pw.write("Date: "+respDateFmt.format(new Date())+"\r\n");
			pw.write("Connection: close\r\n");
			pw.write("Server: CTATTutoringService\r\n");
			pw.write("Access-Control-Allow-Origin: *\r\n");
			pw.write("Content-Length: "+response.length()+"\r\n");
			pw.write("Content-Type: text/plain\r\n");  // +";charset=UTF-8\r\n"
			pw.write("\r\n");
			pw.write(response);
//			pw.write("\0");   // not wanted for HTTP?
			pw.flush();
			result = true;
		} catch (Exception e) {
			trace.errStack("Error writing HTTP response: "+e+
					"\n  Original response content:\n  "+response, e);
			result = false;
		}
		try {
			sock.close();
		} catch (Exception e) {
			trace.errStack("Error closing HTTP response socket: "+e+
					"\n  Original response content:\n  "+response, e);
		}
		if (trace.getDebugCode("logservice")) trace.out("logservice", "sendResponse(): "+result);
		return result;
	}

	/**
	 * Check whether this server is configured to forward log messages to a remote server.
	 * @return false if preference {@value #ENABLE_LOG_FORWARDING} is false
	 *               or preference {@value BR_Controller#USE_OLI_LOGGING} is false
	 *               or preference {@value BR_Controller#OLI_LOGGING_URL} is null
	 *                and property {@value Logger#LOG_SERVICE_URL_PROPERTY} is null
	 */
    protected boolean canForward() {
		Boolean enableLogFwd = getBooleanPreference(ENABLE_LOG_FORWARDING, null);
		if (trace.getDebugCode("logservice"))
			trace.out("logservice", "canForward() enable "+enableLogFwd);
		if (enableLogFwd != null && !enableLogFwd.booleanValue())
			return false;

		Boolean pref = getBooleanPreference(BR_Controller.USE_OLI_LOGGING, "log_to_remote_server"); 
		if (trace.getDebugCode("logservice"))
			trace.out("logservice", "canForward() tsMode "+inTutoringServiceMode+", oli logging pref "+pref);
		if (!inTutoringServiceMode && !(pref == null ? true : pref.booleanValue()))
			return false;

		setLogServerURL(getPreference(BR_Controller.OLI_LOGGING_URL, Logger.LOG_SERVICE_URL_PROPERTY));
		if (trace.getDebugCode("logservice")) trace.out("logservice", "canForward("+logServerURL+")");
		if (logServerURL == null || logServerURL.length() < 1) {
			logInfo.incrementForwardLogErrors();
			return false;
		}
		return true;
	}

    /**
     * Test {@link #extractLogRecord(String)}
     * @param file with single message; if no args, reads message from stdin
     * @return prints log record, or nothing if doesn't find one
     */
    public static void main(String[] args) throws IOException {
    	InputStream in = (args.length > 0 ? new FileInputStream(args[0]) : System.in);
    	ByteArrayOutputStream stdin = new ByteArrayOutputStream();
    	for (int c = -1; 0 <= (c = in.read()); c = -1)
    		stdin.write(c);
    	boolean[] isHTTP = new boolean[1];
    	String logMsg = extractLogRecord(stdin.toString(), isHTTP);
    	trace.out(logMsg);
    }

    /**
     * Recognize a message as a log entry.
     * @param msg
     * @param isHTTP to return true if a log record with an HTTP header
     * @return null if not a log entry; else portion of this message to be logged
     */
    public static String extractLogRecord(String msg, boolean[] isHTTP) {
    	if (isHTTP != null)
    		isHTTP[0] = false;
    	if (msg == null)
    		return null;
    	int idx = 0;
    	String start = msg.substring(idx).trim();
    	if (QUIT_MSG.equals(start))
    		return msg;
    	if (start.startsWith("<log_act") || start.startsWith("<log_ses"))
    		return msg;
    	for (int i = 0; i < 2; ++i) {  // loop to simulate 1 recursive call
			if (trace.getDebugCode("logservice"))
				trace.outNT("logservice", "HttpPostHeader msg["+i+"] "+msg);
    		if (msg.startsWith("<?")) {
    			if ((idx = msg.indexOf("?>")) < 0)
    				return null;
    			else
    				idx += 2;
    			break;
    		} else if (i < 1) {
    			Matcher m = HttpPostHeader.matcher(msg);
    			boolean matched = m.find();
    	    	if (isHTTP != null)
    	    		isHTTP[0] = matched;
				if (trace.getDebugCode("logservice"))
					trace.outNT("logservice", "HttpPostHeader matched "+matched+
							(matched ? " at ["+m.start()+","+m.end()+"]" : ""));
    			if (!matched || 0 != m.start())
    				return null;
    			msg = msg.substring(m.end());
    		}
    	}
    	start = msg.substring(idx).trim();
		if (trace.getDebugCode("logservice"))
			trace.outNT("logservice", "stripped msg: "+start);
    	if (start.startsWith("<log_act") || start.startsWith("<log_ses"))
    		return msg;
    	return null;
    }

	/**
	 * @param logInfo new value for {@link #logInfo}
	 */
	public void setLogInfo(LogInfo logInfo) {
		if (trace.getDebugCode("ls")) trace.out("ls", "LogServlet setLogInfo: logInfo = "+logInfo);
		this.logInfo = logInfo;
	}
	
	/** Set this flag to tell the server to quit. */
	private volatile boolean nowExiting = false;

	/**
	 * @return {@link #nowExiting}
	 * @see edu.cmu.hcii.ctat.ExitableServer#isExiting()
	 */
	@Override
	public synchronized boolean isExiting() {
		return nowExiting;
	}

	/**
	 * Set {@link #nowExiting} true. This instance should be running in a thread blocked
	 * on {@link ServerSocket#accept()} awaiting client connections: interrupt the
	 * thread to tell it to exit.
	 * @return previous value of {@link #nowExiting} 
	 * @see edu.cmu.hcii.ctat.ExitableServer#startExiting()
	 */
	@Override
	public synchronized boolean startExiting() {
		boolean result = nowExiting;
		nowExiting = true;
		if (trace.getDebugCode("ls"))
			trace.out("ls", "LogServlet.startExiting() previous nowExiting "+result);
		try {
			exit();
		} catch (Exception e) {
			trace.errStack("LogServlet.startExiting() error queuing quit msg "+e+
					";\n  cause "+e.getCause(), e);
		}
		return result;
	}

	/**
	 * Log a given message if it should be written to the OLI Logging Server.
	 * @param exchange HTTP request-response
	 * @param logServlet logger to use.
	 * @return true if a log message, after calling {@link LogServlet#enqueue(MessageReplyPair)}
	 */
	public static boolean handleLogRecord(CTATHTTPExchange exchange, LogServlet logServlet)
			throws Exception {
		String msg = extractLogRecord(exchange.getRequestBodyAsString(), null);
		if (msg == null)
			return false;
		if (logServlet == null)
			throw new IllegalStateException("<p>No LogServlet available for request <pre>"+
					msg+"</pre></p>");
		String response = logServlet.logOrQueueAndReply(msg);
		logServlet.sendResponse(exchange.getSocket(), response);
		return true;
	}

	/**
	 * Log a given message if it should be written to the OLI Logging Server.
	 * @param msg message to test
	 * @param sock socket to pass to {@link LogServlet#enqueue(MessageReplyPair)}
	 * @param logServlet logger to use.
	 * @return true if a log message, after calling {@link LogServlet#enqueue(MessageReplyPair)}
	 */
	public static boolean handleLogRecord(String msg, Socket sock, LogServlet logServlet) {
		boolean[] isHTTP = new boolean[1];
		String msgSansHeader = extractLogRecord(msg, isHTTP); // strips HTTP header, if any
		if (trace.getDebugCode("logservice"))
			trace.outNT("logservice", "handleLogRecord() msgSansHeader = "+msgSansHeader+"\n	msg = "+msg+"\n		sock ="+sock);
		if (msgSansHeader == null)
			return false;
		if (!isHTTP[0] && System.getProperty("breakSocketLogging") != null)  // hack for testing
			return true;
		if (logServlet == null)
			throw new IllegalStateException("<p>No LogServlet available for request <pre>"+
					msg+"</pre></p>");
		
		String response = logServlet.logOrQueueAndReply(msgSansHeader);
		if(!(msg.equals(msgSansHeader)))
			logServlet.sendResponse(sock, response);         // must reply only if stripped header
		return true;
	}

	/**
	 * Whether we should log. 
	 * @return true if {@link #inTutoringServiceMode} or logging property or preference is true
	 */
	protected boolean shouldLog() {
		Boolean pref = null;
		return (inTutoringServiceMode || 
				((pref = getBooleanPreference(BR_Controller.USE_DISK_LOGGING, "log_to_disk")) == null ? false : pref.booleanValue()));
	}	
}
