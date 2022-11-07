/*
 * This code is the property of Carnegie Mellon Universtity's Human-Computer 
 * Interaction Institute.  This code is released for academic research 
 * purposes ONLY.  For information on licensing this code,
 * please contact Ken Koedinger (koedinger@cmu.edu) at Carnegie Mellon
 * University.  This code can not be used for any commercial purpose.
 *
 * $Id: SocketProxyTest.java 20364 2014-05-22 14:16:17Z awang1 $
 */
package edu.cmu.pact.SocketProxy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Log.LogConsole;
import edu.cmu.pact.Log.LogFormatUtils;
import edu.cmu.pact.Log.Transaction;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * Test for SocketProxy.   Reads from a file and
 * sends messages to the SocketProxy, which runs in a different process.
 * Opens a Listener to receive messages from the Behavior Recorder.
 */
public class SocketProxyTest extends TestCase {

	/**
	 * Listen on a given socket. For each connection accepted, process
	 * a one-line message and close the connection.
	 */
	class SocketReaderForTest extends Thread {

		/** 
		 * Construct a socketReader thread for the given socket number.
		 */
		SocketReaderForTest() {
			super();
		}

		/**
		 * Listen on {@link #inSock}. For each connection accepted, process
		 * one message terminated by a line-feed; then close the connection.
		 */
		public void run() {
			try {
				InputStreamReader isr =
					new InputStreamReader(inSock.getInputStream(), "UTF-8");
				inStream = new BufferedReader(isr);
				trace.out("sp", "inSock="+inSock+" inStream="+inStream);
				while (!isStopping()) {
					String msg = (eom >= 0 ? SocketReader.readToEom(inStream, eom) : 
											SocketReader.readAll(inStream));

					if (!suppressPrint)
						trace.out("\nSocketProxyTest.listener received:\n"+msg);

					if (msg.length() < 1) {
						if (transactionSet != null)
							transactionSet.cancelIncompleteTransactions();
						break;
					}
					if (waitForResponse)
						transactionSet.addResponse(msg);
				}
				if (inStream != null)
					inStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Date format for testing time stamps. */
	private static final DateFormat dateWithDashes = new SimpleDateFormat("yyyy-MM-dd");
	
	/** Date format for timestamps. */
	private static final DateFormat tsFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");

	
	/** Raw OLI Log of CTAT for Flash-recorded Flash input. */
	// private static final String FLASH_LOG = "test/SocketProxyTestConvertLog.log";
	private static final String FLASH_LOG = "test/edu/cmu/pact/SocketProxy/FlashLogFile.log";

	/** Listener for reading msgs from BR. */
	private Thread listener = null;

	/** Flag set by other thread to tell listener to quit. */
	private boolean stopping = false;

	/**
	 * Set the {@link #stopping} flag and interrupt this thread.
	 */
	synchronized void stopListener() {
		stopping = true;
		listener.interrupt();
	}

	/**
	 * Get the {@link #stopping} flag value under mutex control.
	 */
	private synchronized boolean isStopping() {
		return stopping;
	}

	/**
	 * DNS name of host to connect to.  Default value is "localhost".
	 */
	private String host = "localhost";

	/**
	 * Listener port number.  Default value is
	 * {@link SocketProxy#DEFAULT_CLIENT_PORT}.
	 */
	private int listenerPort = Utils.DEFAULT_CLIENT_PORT;

	/**
	 * Client port number.  Default value is
	 * {@link SocketProxy#DEFAULT_SERVER_PORT}.
	 */
	private int clientPort = Utils.DEFAULT_SERVER_PORT;

	/** Whether to prompt before each message to send. */
	private boolean oneAtATime = false;

	/**
	 * Input file of messages to read.  Format is one msg per line.
	 */
	private String inputFile = null;
  
	/** If {@link SocketToolProxy#OLI_XML_FMT} or
		{@link SocketToolProxy#XMLCONVERTER_FMT}, then MsgSource
		reads file as msgs delimited by blank lines. */
	private int msgFormat = SocketToolProxy.COMM_FMT;

	/** If nonnegative, listener should expect this character as an
		end-of-message delimiter. */
	private int eom = -1;

	/**
	 * If true, then this class will send
	 * a single message for each socket connection.  Else the same socket(s)
	 * will be reused for all messages. Default true.
	 */
	private boolean oneMsgPerSocket = true;

	/** Whether we should convert an OLI log file. */
	private boolean convertLog = false;

	/** Whether to show the ui. */
	private boolean showFrame = false;

	/** Socket for writing data to server. */
	private Socket outSock = null;

	/** Socket for reading data from server. */
	private Socket inSock = null;

	/** Stream for writing data to server. */
	private PrintWriter outStream = null;

	/** Stream for reading data from server. */
	private BufferedReader inStream = null;

	/** Userid, which might come from log_session_start element. */
	private String userId = "";

	/** Session id, from sess_ref attribute of log_action element. */
	private String sessionId = "";

	/** Session id, from date_time attribute of log_action element. */
	private String dateTime = "1970/01/01 00:00:00";

	/** Session id, from timezone attribute of log_action element. */
	private String timeZone = "UTC";

	/** Whether to wait on the {@link #listener} for a response. */
	private boolean waitForResponse = false;

	/** Set of transactions (all or perhaps just those outstanding). */
	private Transaction.TransactionSet transactionSet = new Transaction.TransactionSet();

	/** If true, suppress printing of messages sent & received. */
	public static boolean suppressPrint = false;

	/** Number of client threads. */
	private static int nSenders = 0;
	
	public SocketProxyTest() {
		this (null, 1000);
	}
	
	/**
	 * Call JUnit Test constructor, then construct a listener process.
	 *
	 * @param  superArg arg for superclass constructor
	 * @param  listenerPort Socket number listen on
	 */
	public SocketProxyTest(String arg, int listenerPort) {
		super(arg);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SocketProxyTest.class);
//        suite.addTest(new SocketProxyTest("readMsgFileTest", listenerPort));
    	return suite;
	}
	
	/**
	 * 
	 */
	public void testConvertLogToolMsgs()
			throws Exception {
//		BR_Controller ctlr = new BR_Controller();
//		UniversalToolProxy utp = new UniversalToolProxy();
//		boolean saveConvertLog = convertLog;
//		convertLog = true;
//		trace.addDebugCode("log");
//		try {
//			XMLOutputter xmlout = new XMLOutputter();
//
//			Document doc = LogFormatUtils.parseLog(new File(FLASH_LOG), convertLog);
//			assertNotNull("parsing "+FLASH_LOG, doc);
//
//			Element root = doc.getRootElement();
//			String dtdVersion = null;
//			if ("tutor_related_message_sequence".equals(root.getName()))
//				dtdVersion = root.getAttributeValue("version_number");
//			List logEntries = root.getChildren();
//			int i = 0;
//			for (Iterator it = logEntries.iterator(); it.hasNext(); ++i) {
//				Element elt = getToolMessageElement((Element) it.next());
//				if (elt == null)
//					continue;
//				String eltStr = xmlout.outputString(elt);
//				trace.out("log", "tool_message element is\n"+eltStr);
//
//				MetaElement meta = new MetaElement(getUserId(), getSessionId(),
//						getDateTime(), getTimeZone());
//				TutorActionLog tal = TutorActionLog.factory(eltStr, dtdVersion, meta);
//				assertNotNull("creating TutorActionLog from\n"+eltStr, tal);
//				trace.out("log", "TutorActionLog element is\n"+tal.toString());
//				assertEquals("TutorActionLog.getTopElementType()", "tool_message", tal.getTopElementType());
//				/* assertEquals("timeStamp misinterpreted", "2007-05-04",
//                   dateWithDashes.format(tal.getTimeStamp())); */
//
//				MessageObject omo = OLIMessageObject.factory(eltStr, dtdVersion, utp.getToolProxy(), ctlr);
//				// trace.out("log", "MessageObject transaction id "+ omo.getTransactionId() + ", msg\n" + omo);
//				trace.out("log", "MessageObject: "+ omo);
//				assertEquals("OLIMessageObject.getMessageTypeProperty", "InterfaceAction", omo.getMessageTypeProperty());
//			}
//		} finally {
//			convertLog = saveConvertLog;
//		}
	}

	/**
	 * A {@link SocketProxyTest.MsgSource} of tool_messages from a log.
	 */
	class ToolMsgSource implements MsgSource {
		private final XMLOutputter xmlout = new XMLOutputter();
		private final List logEntries;
		ToolMsgSource(List logEntries) {
			this.logEntries = new ArrayList(logEntries);
		}
		public String next() {
			while(!logEntries.isEmpty()) {
				Element elt = (Element) logEntries.remove(0);
				Element toolMsgElt = getToolMessageElement(elt);
				if (toolMsgElt != null)
					return xmlout.outputString(toolMsgElt);
			}
			return null;
		}
	}

	/**
	 * Get the tool_message element from the body of a log_action element.
	 * Tries to find tool_message 
	 * @param elt log element 
	 * @return tutor_related_message_sequence element, if tool_message is
	 *         a child of it; null if not
	 */
	public Element getToolMessageElement(Element elt) {
		if ("log_session_start".equals(elt.getName())) {
			setUserId(elt.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
			return null;
		}
		if ("tutor_related_message_sequence".equals(elt.getName()))
			return elt;
		if ("tool_message".equals(elt.getName()))
			return elt;
		if (!"log_action".equals(elt.getName()))
			return null;
		setUserId(elt.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
		setSessionId(elt.getAttributeValue("sess_ref"));
		setDateTime(elt.getAttributeValue("date_time"));
		setTimeZone(elt.getAttributeValue("timezone"));
		String actionId = elt.getAttributeValue("action_id");
		if (actionId == null)
			return null;
		actionId = actionId.toLowerCase();
		if (!actionId.startsWith("tool"))
			return null;
		Element child = elt.getChild("tutor_related_message_sequence");
		if (child == null)
			return null;
		if (child.getChild("tool_message") == null) {
			trace.err("Log entry inconsistent: action_id is tool_message,"+
					" but no tool_message element in content");
			return null;
		}
		return child;
	}

	/**
	 * @return the {@link #userId}
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param new value for {@link #userId}; sets "" if value is null
	 */
	private void setUserId(String attributeValue) {
		if (attributeValue == null || attributeValue.length() < 1)
			return;
		userId = attributeValue;
	}

	/**
	 * Listen for messages on a socket.
	 */
	public void listenerTest() {
        create2wayConnection(host, clientPort);		
	}
	
	/**
	 * Send all the messages in {@link #inputFile}. Messages are delimited
	 * by blank lines if in XML; else by end-of-line characters.
	 */
	public void readMsgFileTest() {

		if (inputFile == null)
			return;

		MsgSource msgSrc = null;
		try {
			Reader reader = null;
			try {
				reader = new FileReader(inputFile);
			} catch (FileNotFoundException fnfe) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(inputFile);
				if (is == null)
					throw fnfe;
				reader = new BufferedReader(new InputStreamReader(is));
			}
			final BufferedReader bRdr = new BufferedReader(reader);
			if (msgFormat == SocketToolProxy.OLI_XML_FMT ||
						msgFormat == SocketToolProxy.XMLCONVERTER_FMT) {
				msgSrc = new MsgSource() {
					// read stream until find blank line
					public String next() {
						try {
							StringBuffer result = new StringBuffer(4096);
							String line;
							while (null != (line = bRdr.readLine())) {
								if (line.trim().length() < 1) {
									if (result.length() > 0)
										return result.toString();
                                    continue;
								}
                                result.append(line);
                                result.append('\n');
							}
							if (result.length() > 0)
								return result.toString();
                            return null;
						} catch (IOException ioe) {
							ioe.printStackTrace();
							return null;
						}
					}
				};
			} else {
				msgSrc = new MsgSource() {
					// return one line at a time
					public String next() {
						try {
							return bRdr.readLine();
						} catch (IOException ioe) {
							ioe.printStackTrace();
							return null;
						}
					}
				};
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionFailedError("Error opening msg file " +
										   inputFile + ": " + e);
		}

		List<Transaction> txs = runClient(host, clientPort, msgSrc, oneAtATime);
		printSummary(host, inputFile, getSessionId(), txs);
	}

	/**
	 * Print a summary of transaction statistics to stdout.
	 * @param host2
	 * @param inputFile2
	 * @param sessionId2
	 * @param txs transactions to summarize
	 */
	private void printSummary(String host2, String inputFile2,
			String sessionId2, List<Transaction> txs) {
		Date startTime = null;
		long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
		double avg = 0; int nTx = 0;
		for (Transaction tx : txs) {
			Date sendTime = tx.getSendTime();
			if (startTime == null || startTime.after(sendTime))
				startTime = sendTime;
			long duration = tx.getDuration();
			if (duration < 0)
				continue;
			if (duration < min)
				min = duration;
			if (max < duration)
				max = duration;
			avg += duration; nTx++;
		}
		if (nTx > 0)
			avg /= nTx;
		synchronized(System.out) {
			System.out.printf("%s\t%s\t%s\t%s\t%d\t%.2f\t%d\t%d\r\n",
					host2, inputFile2, sessionId2, tsFmt.format(startTime), nTx, avg, min, max);
			System.out.flush();
		}
	}

	/** Help message with command-line arguments. */
	public static final String usageMsg =
		"Usage:\n" +
		"  SocketProxyTest [-c clientPort] [-d] [-e eom] [-L listenerPort] [-h host] \\\n" +
		"    [-o] [-w] [-b] [-m] [-x|-X|-F] [-q] [-t testName] [-T threads] [inputFile...]\n" +
		"where--\n" +
		"  clientPort is the port number to which to send to; default\n" +
		"    " + Utils.DEFAULT_SERVER_PORT + ";\n" +
		"  -d means turn on debug output;\n" +
		"  -q means to suppress the printing of messages sent and received;\n" +
		"  eom is an end-of-message character, expressed as a hex integer;\n" +
		"  host  DNS hostname where server is listening; default localhost;\n" +
		"  -o means prompt before sending each msg;\n" +
		"  -w means wait for tutor's response before sending next msg;\n" +
		"  listenerPort is the port number on which to listen; default\n" +
		"    " + Utils.DEFAULT_CLIENT_PORT + ";\n" +
		"  -x means to expect a log file in OLI XML (DataShop DTD) format;\n" +
		"  -X means to expect messages in OLI XML format;\n" +
		"  -F means to expect messages in XML-ized Comm format;\n" +
		"  threads is a count of clients to run concurrently (default 1 per inputFile);\n" +
		"  testName is one of {listenerTest, readMsgFileTest, sendToolMessages};\n" +
		"  inputFile is a file of messages (1 per line) to send to the\n" +
		"    clientPort; multiple inputFiles create multithreaded concurrent tests.\n";

	/**
	 * Command-line usage: see {@link #usageMsg}.
	 */
	public static void main(String args[]) {
		String testName = "readMsgFileTest";
		boolean debugOn = false;
		boolean waitForResponse = false;
		boolean showFrame = false;
		boolean oneAtATime = false;
		String host = "localhost";
		int eom = -1;
		int listenerPort = Utils.DEFAULT_CLIENT_PORT;
		int clientPort = Utils.DEFAULT_SERVER_PORT;
		boolean convertLog = false;
		int msgFormat = SocketToolProxy.COMM_FMT;
		int i;

		for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
			char opt = args[i].charAt(1);
			switch (opt) {
			case 'w':
				waitForResponse = true;
				break;
			case 'f':
				showFrame = true;
				break;
			case 'd':
				debugOn = true;
				break;
			case 'o':
				oneAtATime = true;
				break;
			case 'e':
				try {
					if (args.length < ++i)
						throw new Exception("Missing end-of-message argument");
					eom = Integer.parseInt(args[i], 16);
				} catch (Exception e) {
					System.err.println("Bad argument after -" + opt + ": " +
									   e + ". " + usageMsg);
					System.exit(1);
				}
				break;
			case 'h':
			    try {
			        if (args.length < ++i)
			            throw new Exception("Missing host argument");
			        host = args[i];
			    } catch (Exception e) {
					System.err.println("Bad argument after -" + opt + ": " +
							   e + ". " + usageMsg);
					System.exit(1);
			    }
				break;
			case 'c':
			case 'l':
			case 'L':
				try {
					if (args.length < ++i)
						throw new Exception("Missing port number");
					if (opt == 'c')
						clientPort = Integer.parseInt(args[i]);
					else
						listenerPort = Integer.parseInt(args[i]);
				} catch (Exception e) {
					System.err.println("Bad argument after -" + opt + ": " +
									   e + ". " + usageMsg);
					System.exit(1);
				}
				break;
			case 'T':
				try {
					if (args.length < ++i)
						throw new Exception("Missing port number");
					nSenders = Integer.parseInt(args[i]);
				} catch (Exception e) {
					System.err.println("Bad argument after -" + opt + ": " +
									   e + ". " + usageMsg);
					System.exit(1);
				}
				break;
			case 'x':
				convertLog  = true;
				msgFormat = SocketToolProxy.OLI_XML_FMT;
				break;
  			case 'X':
				msgFormat = SocketToolProxy.OLI_XML_FMT;
				break;
			case 'F':
				msgFormat = SocketToolProxy.XMLCONVERTER_FMT;
  				break;
			case 't':
				try {
					if (args.length < ++i)
						throw new Exception("Missing test name");
					testName = args[i];
				} catch (Exception e) {
					System.err.println("Bad argument after -" + opt + ": " +
									   e + ". " + usageMsg);
					System.exit(1);
				}
				break;
			case 'q':
				suppressPrint = true;
				break;
			default:
				System.err.println("Undefined command-line option " + opt +
								   ". " + usageMsg);
				System.exit(1);
			}
		}
		if (debugOn)
			trace.addDebugCode("sp");
		if ("listenerTest".equals(testName))
			exec(null, false, testName, waitForResponse, showFrame, oneAtATime, eom,
					host, clientPort, convertLog, msgFormat, listenerPort);
		else {
			String[] inputFiles;
			if (nSenders < 2) {
				nSenders = args.length - i;
				inputFiles = Arrays.copyOfRange(args, i, args.length);
			} else {
				inputFiles = new String[nSenders];
				for (int n = 0, k = i; k < args.length && n < nSenders; ++n) {
					inputFiles[n] = args[k++];
					if (k >= args.length)
						k = i;
				}
			}
			for (i = 0; i < inputFiles.length; ++i) {
				exec(inputFiles[i], nSenders > 1, testName, waitForResponse, showFrame, oneAtATime, eom,
						host, clientPort, convertLog, msgFormat, listenerPort);
			}
		}
		
	}
	
	private static void exec(String inputFile, boolean multithread, String testName,
			boolean waitForResponse, boolean showFrame, boolean oneAtATime, int eom,
			String host, int clientPort, boolean convertLog, int msgFormat, int listenerPort) {

		final SocketProxyTest tst =
			new SocketProxyTest(testName, listenerPort);
		tst.waitForResponse = waitForResponse;
		tst.showFrame = showFrame;
		tst.oneAtATime = oneAtATime;
		tst.eom = eom;
		tst.host = host;
		tst.clientPort = clientPort;
		tst.convertLog = convertLog;
		tst.msgFormat = msgFormat;
		tst.inputFile = inputFile;

		trace.out("sp", "command-line args: listenerPort " + listenerPort +
				", clientPort " + clientPort +
				", inputFile " + inputFile);
		if (!multithread)
			tst.run();
		else {
			Thread sender = new Thread() {
				public void run() {
					tst.run();
				}
			};
			sender.start();
		}
		
	}

	/**
	 * Send msgs from the given file to the given port on the localhost.
	 *
	 * @param  host DNS name to connect to; if null, uses localhost
	 * @param  clientPort port to connect to on given host
	 * @param  msgSrc source of one-line messages to send
	 * @param  oneAtATime if true, prompt after sending each msg
	 */
	private List<Transaction> runClient(String host, int clientPort,
			MsgSource msgSrc, boolean oneAtATime) {
		List<Transaction> txs = new ArrayList<Transaction>();
		try {
			setSessionId(Logger.generateGUID());
			
			BufferedReader promptRdr =
				new BufferedReader(new InputStreamReader(System.in));
			String msg;

			/*
			 * This prompt lets you get the Behavior Recorder server
			 * started after this process's listener has been started.
			 */ 
			if (nSenders < 2) {
				trace.out("\nStart SocketProxy, open interface and " +
								   "graph in the Behavior Recorder,\nand then " +
								   "press Enter here to start sending.\n");
				promptRdr.readLine();
			}

			while (null != (msg = msgSrc.next())) {
				if (msg.trim().length() < 1)        // skip blank lines
					continue;
				if (msg.trim().charAt(0) == '#') {  // just print comment lines
					trace.out("\nComment: " + msg + "\n");
					continue;
				}
				trace.out("sp", "runClient waitForResponse "+waitForResponse+
						", to send:\n" + msg);
				if (waitForResponse)
					txs.add(sendAndWaitForTutorResponse(host, clientPort, msg));
				else
					sendString(host, clientPort, msg);
				if (oneAtATime) {
					trace.out("\n___press Enter to send next msg");
					promptRdr.readLine();
				}
			}
			if (nSenders < 2) {
				do {
					trace.out("\n___enter 'q' to stop listener: ");
				} while (!promptRdr.readLine().toLowerCase().startsWith("q"));
			}			
			this.resetOutputStream(true);  // true=>close unconditionally
			stopListener();

		} catch (IOException ioe) {
			System.err.println("error on prompt reader");
			ioe.printStackTrace();
		}
		return txs;
	}

	/**
	 * Send a message and wait for a tutor reply to the given message.
	 * Creates {@link Transaction}, calls {@link #sendString(String, int, String)},
	 * waits for complete tutor response.
	 * @param  host DNS name to connect to; if null, uses localhost
	 * @param  clientPort port to connect to on given host
	 * @param  msg message to send
	 * @return transaction record
	 */
	private Transaction sendAndWaitForTutorResponse(String host, int clientPort, String msg) {
		Transaction tx = new Transaction(msg, true);
		tx.setSessionId(getSessionId());
		transactionSet.add(tx);
		synchronized(tx) {
			String toSend = tx.getRequestText();
			if (!suppressPrint)
				trace.out("\nSocketProxyTest.sendAndWait sent:\n"+toSend);
			sendString(host, clientPort, toSend);
			tx.setSendTime(new Date());
			if (tx.hasNoResponse())
				return tx;
			while(!tx.isResponseComplete()) {
				try {
					tx.wait();
				} catch (InterruptedException ie) {
					trace.err("Exception during waitForTutorResponse("+msg+"):\n "+
							ie+"; cause "+ie.getCause());
				}
			}
			tx.setReceiveTime(System.currentTimeMillis());
		}
		return tx;
	}

	/**
	 * Connect, send a single message and close the connection.
	 * @param  host DNS name to connect to; if null, uses localhost
	 * @param  clientPort port to connect to on given host
	 * @param  str message to send
	 */
	private void sendString(String host, int clientPort, String str) {
		try{
		    PrintWriter out = getOutputStream(host, clientPort);
			trace.out("sp", "sendString() out="+out+" str=\n "+str);
		    if (out == null)
		        return;
			out.println(str);
			if (eom >= 0)
				out.write(eom);
			out.flush();
			resetOutputStream(false);
		} catch(Exception e){
			//e.printStackTrace();
			trace.out("sp", "SocketToolProxy failed to connect outgoing socket to Tutor Interface: " + e.toString());
		}
	}
	
	/**
	 * Open a socket to send a message, unless it is already open.
	 * @param  host DNS name to connect to; if null, uses localhost
	 * @param  clientPort port to connect to on given host
	 * @return {@link #outStream}
	 * @throws UnknownHostException, IOException
	 */
	private PrintWriter getOutputStream(String host, int clientPort)
			throws Exception {
		trace.out("sp", "getOutputStream() outSock="+outSock+" outStream="+outStream);
	    if (outSock == null)
	        create2wayConnection(host, clientPort);
		trace.out("sp", "getOutputStream() outSock="+outSock+
				" outStream="+outStream);
	    if (outStream == null)
	        outStream = new PrintWriter(outSock.getOutputStream(), false);

	    return outStream;
	}
	
	/**
	 * Create a socket connection. Side effects:<ul>
	 *   <li>sets {@link #outSock}</li>
	 *   <li>sets {@link #inSock}</li>
	 *   <li>sets {@link #outStream}</li>
	 *   <li>sets {@link #listener} and starts new listener thread</li>
	 * </ul>
	 * @param host
	 * @param clientPort
	 * @return
	 */
	private Socket create2wayConnection(String host, int clientPort) {
		try {
	        InetAddress addr = InetAddress.getLocalHost();
	        if (host != null && host.length() > 0)
	            addr = InetAddress.getByName(host);
	        outStream = null;
	        inSock = outSock = new Socket(addr, clientPort);
	        outStream = new PrintWriter(outSock.getOutputStream(), false);
	        listener = new SocketReaderForTest();
	        listener.start();
	        return outSock;
		} catch (Exception e) {
			trace.err("Error connecting to host "+host+", port "+clientPort+": "+e+
					(e.getCause() == null ? "" : "; cause: "+e.getCause()));
			return null;
		}
	}

	/**
	 * Close the socket if sending only one msg at a time.
	 * @param  closeUnconditionally if true will always close
	 */
	private void resetOutputStream(boolean closeUnconditionally) throws IOException {
	    trace.out("sp", "resetOutputStream() closeUnconditionally="+
	    		closeUnconditionally);
	    if (!closeUnconditionally)
	        return;
	    if (closeUnconditionally)
	    	stopListener();
	    if (outStream != null)
	        outStream.close();
	    outStream = null;
	    if (inStream != null) {
	        inStream.close();
	        inStream = null;
	    }
	    outSock.close();
	    outSock = null;
	    inSock = null;
	}

	/**
	 * @return the {@link #timeZone}
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param atributeValue new value for {@link #timeZone}
	 */
	private void setTimeZone(String attributeValue) {
		if (attributeValue == null || attributeValue.length() < 1)
			return;
		timeZone = attributeValue;
	}

	/**
	 * @return the {@link #dateTime}
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param attributeValue new value for {@link #dateTime}
	 */
	public void setDateTime(String attributeValue) {
		if (attributeValue == null || attributeValue.length() < 1)
			return;
		dateTime = attributeValue;
	}

	/**
	 * @return the {@link #sessionId}
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param attributeValue new value for {@link #sessionId}
	 */
	public void setSessionId(String attributeValue) {
		if (attributeValue == null)
			return;
		sessionId = attributeValue;
	}
}
