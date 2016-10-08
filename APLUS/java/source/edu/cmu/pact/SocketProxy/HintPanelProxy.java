/*
 * This code is the property of Carnegie Mellon Universtity's Human-Computer 
 * Interaction Institute.  This code is released for academic research 
 * purposes ONLY.  For information on licensing this code,
 * please contact Ken Koedinger (koedinger@cmu.edu) at Carnegie Mellon
 * University.  This code can not be used for any commercial purpose.
 *
 * $Id: HintPanelProxy.java 20364 2014-05-22 14:16:17Z awang1 $
 */
package edu.cmu.pact.SocketProxy;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintPanel;
import edu.cmu.pact.CommManager.RemoteCommMessageHandler;
import edu.cmu.pact.Log.LogConsole;
import edu.cmu.pact.Log.LogFormatUtils;
import edu.cmu.pact.Log.Transaction;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

/**
 * Test for HintPanelProxy.   Reads from a file and
 * sends messages to the HintPanelProxy, which runs in a different process.
 * Opens a Listener to receive messages from the Behavior Recorder.
 */
public class HintPanelProxy {

	/**
	 * Listen on a given socket. For each connection accepted, process
	 * a one-line message and close the connection.
	 */
	class SocketReader2 extends Thread {

		/** 
		 * Construct a socketReader thread for the given socket number.
		 */
		SocketReader2() {
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
				if (trace.getDebugCode("sp")) trace.out("sp", "inSock="+inSock+" inStream="+inStream);
				while (!isStopping()) {
					String msg = (eom >= 0 ? SocketReader.readToEom(inStream, eom) : 
								             SocketReader.readAll(inStream));

					if (trace.getDebugCode("sp")) trace.out("sp", "\nHintPanelProxy.listener received:\n" + msg);
					MessageObject mo = Logger.actionLogToMessageObject(msg, true);
					if (trace.getDebugCode("sp")) trace.out("sp", "\n message object:\n" + (mo==null ? null : mo.toXML()));
                    hp.handleMessageObject(mo);

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
	
	/** Raw OLI Log of CTAT for Flash-recorded Flash input. */
	// private static final String FLASH_LOG = "test/SocketProxyTestConvertLog.log";
	private static final String FLASH_LOG = "/afs/cs.cmu.edu/project/iwt/Trunk/AuthoringTools/java/tmp/sewall_20071116114341975.xml";

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
	 * {@link HintPanelProxy#DEFAULT_CLIENT_PORT}.
	 */
	private int listenerPort = Utils.DEFAULT_CLIENT_PORT;

	/**
	 * Client port number.  Default value is
	 * {@link HintPanelProxy#DEFAULT_SERVER_PORT}.
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

	private static int nSenders = 0;
	
	public HintPanelProxy() {
		this (null, 1000);
	}

    private java.lang.reflect.Method m;

    private HintPanel hp;
    private RemoteCommMessageHandler handler;

    public void setHintPanel(HintPanel panel) {
        hp = panel;
    }

    public void setHandler(RemoteCommMessageHandler h) {
        handler = h;
    }

	/**
	 * Call JUnit Test constructor, then construct a listener process.
	 *
	 * @param  superArg arg for superclass constructor
	 * @param  listenerPort Socket number listen on
	 */
	public HintPanelProxy(String arg, int listenerPort) {
        try {
            m = getClass().getMethod(arg);
        } catch (Exception e) {
            try {
                m = getClass().getMethod("readMsgFileTest");
            } catch (Exception e2) {
                m = null;
            }
        }
	}

	public void run() {
        try {
            m.invoke(this);
        } catch (Exception e) {
        }
    }

	/**
	 * A {@link HintPanelProxy.MsgSource} of tool_messages from a log.
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
		if (!"tool_message".equals(actionId))
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
	 * Send all the messages in {@link #inputFile}. Messages are delimited
	 * by blank lines if in XML; else by end-of-line characters.
	 */
	public void readMsgFileTest() {

		if (inputFile == null)
			return;

		MsgSource msgSrc = null;
		try {
			FileReader reader = new FileReader(inputFile);
			final BufferedReader bRdr = new BufferedReader(reader);
			if (msgFormat == SocketToolProxy.OLI_XML_FMT ||
						msgFormat == SocketToolProxy.XMLCONVERTER_FMT) {
				msgSrc = new MsgSource() {
					// read stream until find blank line
					public String next() {
						try {
                            if (handler.hasMessage())
                                return handler.nextMessage();
                            
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
                            if (handler.hasMessage())
                                return handler.nextMessage();
                            
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
			throw new IllegalStateException("Error opening msg file " + inputFile + ": " + e);
		}

		runClient(host, clientPort, msgSrc, oneAtATime);
	}

	/** Help message with command-line arguments. */
	public static final String usageMsg =
		"Usage:\n" +
		"  HintPanelProxy [-c clientPort] [-d] [-e eom] [-L listenerPort] [-h host] \\\n    [-o] [-w] [-b] [-m] [-x|-X|-F] [-t testName] [inputFile]\n" +
		"where--\n" +
		"  clientPort is the port number to which to send to; default\n" +
		"    " + Utils.DEFAULT_SERVER_PORT + ";\n" +
		"  -d means turn on debugging;\n" +
		"  eom is an end-of-message character, expressed as a hex integer;\n" +
		"  host  DNS hostname where server is listening; default localhost;\n" +
		"  -o means prompt before sending each msg;\n" +
		"  -w means wait for tutor's response before sending next msg;\n" +
		"  listenerPort is the port number on which to listen; default\n" +
		"    " + Utils.DEFAULT_CLIENT_PORT + ";\n" +
		"  inputFile is a file of messages (1 per line) to send to the\n" +
		"    clientPort;\n" +
		"  -x means to expect a log file in OLI XML (DataShop DTD) format;\n" +
		"  -X means to expect messages in OLI XML format;\n" +
		"  -F means to expect messages in XML-ized Comm format;\n" +
		"  testName is one of {readMsgFileTest, sendToolMessages}.\n";

    
    private static void createAndShowGUI(HintPanel hp) {
        //Create and set up the window.
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(hp, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

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
		String inputFile = null;
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
			default:
				System.err.println("Undefined command-line option " + opt +
								   ". " + usageMsg);
				System.exit(1);
			}
		}

		nSenders = args.length - i;
		for ( ; i < args.length; ++i) {
			inputFile = args[i];

			if (debugOn)
				trace.addDebugCode("sp");

			if (trace.getDebugCode("sp")) trace.out("sp", "command-line args: listenerPort " + listenerPort +
					", clientPort " + clientPort +
					", inputFile " + inputFile);

			final HintPanelProxy tst = new HintPanelProxy(testName, listenerPort);
			tst.waitForResponse = waitForResponse;
			tst.showFrame = showFrame;
			tst.oneAtATime = oneAtATime;
			tst.eom = eom;
			tst.host = host;
			tst.clientPort = clientPort;
			tst.convertLog = convertLog;
			tst.msgFormat = msgFormat;
			tst.inputFile = inputFile;
            
            RemoteCommMessageHandler handler = new RemoteCommMessageHandler();
            final HintPanel hp = new HintPanel(handler);
            tst.setHintPanel(hp);
            tst.setHandler(handler);
            
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        createAndShowGUI(hp);
                    }
                });
            
			if (nSenders < 2)
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
        
	}

	/**
	 * Send msgs from the given file to the given port on the localhost.
	 *
	 * @param  host DNS name to connect to; if null, uses localhost
	 * @param  clientPort port to connect to on given host
	 * @param  msgSrc source of one-line messages to send
	 * @param  oneAtATime if true, prompt after sending each msg
	 */
	private void runClient(String host, int clientPort,
			MsgSource msgSrc, boolean oneAtATime) {
		try {
			BufferedReader promptRdr =
				new BufferedReader(new InputStreamReader(System.in));
			String msg;

			/*
			 * This prompt lets you get the Behavior Recorder server
			 * started after this process's listener has been started.
			 */ 
			if (nSenders < 2) {
				System.out.println("\nStart HintPanelProxy, open interface and " +
								   "graph in the Behavior Recorder,\nand then " +
								   "press Enter here to start sending.\n");
				promptRdr.readLine();
			}
			while (null != (msg = msgSrc.next())) {
				if (msg.trim().length() < 1)        // skip blank lines
					continue;
				if (msg.trim().charAt(0) == '#') {  // just print comment lines
					System.out.println("\nComment: " + msg + "\n");
					continue;
				}
				if (trace.getDebugCode("sp")) trace.out("sp", "runClient waitForResponse "+waitForResponse+
						", to send:\n" + msg);
				if (waitForResponse)
					sendAndWaitForTutorResponse(host, clientPort, msg);
				else
					sendString(host, clientPort, msg);
				if (oneAtATime) {
					System.out.println("\n___press Enter to send next msg");
					promptRdr.readLine();
				}
			}
			if (nSenders < 2) {
				do {
					System.out.print("\n___enter 'q' to stop listener: ");
				} while (!promptRdr.readLine().toLowerCase().startsWith("q"));
			}			
			this.resetOutputStream(true);  // true=>close unconditionally
			stopListener();

		} catch (IOException ioe) {
			System.err.println("error on prompt reader");
			ioe.printStackTrace();
		}
	}

	/**
	 * Send a message and wait for a tutor reply to the given message.
	 * Creates {@link Transaction}, calls {@link #sendString(String, int, String)},
	 * waits for complete tutor response.
	 * @param  host DNS name to connect to; if null, uses localhost
	 * @param  clientPort port to connect to on given host
	 * @param  msg message to send
	 */
	private void sendAndWaitForTutorResponse(String host, int clientPort, String msg) {
		/*
		 * InterfaceIdentification msg has no response from the tutor,
		 * so send it & don't wait for a response.
		 */
		if (msg.contains("InterfaceIdentification")) {
			sendString(host, clientPort, msg);
			return;
		}
		Transaction tx = new Transaction(msg, false);
		transactionSet.add(tx);
		synchronized(tx) {
			sendString(host, clientPort, msg);
			while(!tx.isResponseComplete()) {
				try {
					tx.wait();
				} catch (InterruptedException ie) {
					trace.err("Exception during waitForTutorResponse("+msg+"):\n "+
							ie+"; cause "+ie.getCause());
				}
			}
		}
	}

	/**
	 * Check the 
	 * @param msg
	 * @return
	 */
	private boolean responseReceived(String msg) {
		
		return false;
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
			if (trace.getDebugCode("sp")) trace.out("sp", "sendString() out="+out+" str=\n "+str);
		    if (out == null)
		        return;
			out.println(str);
			if (eom >= 0)
				out.write(eom);
			out.flush();
			resetOutputStream(false);
		} catch(Exception e){
			//e.printStackTrace();
			if (trace.getDebugCode("sp")) trace.out("sp", "SocketToolProxy failed to connect outgoing socket to Tutor Interface: " + e.toString());
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
		if (trace.getDebugCode("sp")) trace.out("sp", "getOutputStream() outSock="+outSock+" outStream="+outStream);
	    if (outSock == null) { 
	        InetAddress addr = InetAddress.getLocalHost();
	        if (host != null && host.length() > 0)
	            addr = InetAddress.getByName(host);
	        outSock = new Socket(addr, clientPort);
	        outStream = null;
	        inSock = outSock;
	        listener = new SocketReader2();
	        listener.start();
	    }
		if (trace.getDebugCode("sp")) trace.out("sp", "getOutputStream() outSock="+outSock+
				" outStream="+outStream);
	    if (outStream == null)
	        outStream = new PrintWriter(outSock.getOutputStream(), false);

	    return outStream;
	}
	
	/**
	 * Close the socket if sending only one msg at a time.
	 * @param  closeUnconditionally if true will always close
	 */
	private void resetOutputStream(boolean closeUnconditionally) throws IOException {
	    if (trace.getDebugCode("sp")) trace.out("sp", "resetOutputStream() closeUnconditionally="+
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

/**
 * A stream or other data source that supports reading line-by-line.
 */
interface MsgSource {
	/**
	 * Return the next message.
	 *
	 * @return next message from source; null if no more messages
	 */
	public String next();
}
