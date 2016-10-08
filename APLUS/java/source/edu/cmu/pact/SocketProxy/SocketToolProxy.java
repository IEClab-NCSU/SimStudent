//SocketToolProxy - Universal Tool Proxy substitute for communicating with arbitrary interface across sockets
//Gus Prevas, Carnegie Mellon University, 2004

package edu.cmu.pact.SocketProxy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.StudentInterfaceConnectionStatus;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

public class SocketToolProxy extends RemoteToolProxy{

	/** Set of MessageTypes that should not be in the start state. */
	private static Set<String> msgTypesToOmitFromStartState = new HashSet<String>();
	static {
		msgTypesToOmitFromStartState.add("InterfaceIdentification".toLowerCase());
		msgTypesToOmitFromStartState.add("SetPreferences".toLowerCase());
		msgTypesToOmitFromStartState.add("ResetAction".toLowerCase());
	}

	/** If true, log received msgs only: do not send to socket. */
	private boolean logOnly = false;

	/** Destination hostname for outbound connection. */
	private String destHost = "localhost";


	/** Whether to send one msg per TCP connection. Default is false. */
	private boolean oneMsgPerConnection = false;

	/** Socket for sending. */
	private Socket sock = null;

	/** Output stream on {@link #sock}. */
	private PrintWriter out = null;

	/**
	 * If true, then this class will send a single message on each socket
	 * connection.  Else the same socket will be reused for all messages.
	 */
	private boolean oneMsgPerSocket = false;

	/** Whether to initiate a connection. */
	private boolean connectFirst = false;
    
    /**
	 * Constructor just calls superclass constructor.
	 */
	public SocketToolProxy(BR_Controller controller) {
		super();
        controller.setUniversalToolProxy(this);
        this.controller = controller;
    } 

	/**
	 * Initialize the interface with an existing socket.
	 * Calls superclass init().
	 *
	 * @param  sock existing socket; null if should open new socket
	 *            to host {@link #destHost}, port {@link #destPort}
	 * @param  format one of {@link #XMLCONVERTER_FMT}, {@link #OLI_XML_FMT}
	 *            or {@link #COMM_FMT}, the default
	 * @param {@link #eom}; default value -1 means "no delimiter"; see
	 *            note at {@link #setEom(int)}
	 * @param  controller BR_Controller instance for superclass
	 */
	public void init(Socket sock, int format, int eom,
					 BR_Controller controller) {
		super.init(controller);
		this.sock = sock;
		setFormat(format);
		setEom(eom);
	}

	/**
	 * Initialize the interface with a destination hostname and port.
	 * Calls superclass init().
	 *
	 * @param  destHost destination host
	 * @param  destPort port number on destHost.
	 * @param  format one of {@link #XMLCONVERTER_FMT}, {@link #OLI_XML_FMT}
	 *            or {@link #COMM_FMT}, the default
	 * @param {@link #eom}; default value -1 means "no delimiter"; see
	 *            note at {@link #setEom(int)}
	 * @param  oneMsgPerSocket value for {@link #oneMsgPerSocket}
	 * @param  controller BR_Controller instance for superclass
	 */
	public void init(String destHost, int destPort, int format, int eom,
					 boolean oneMsgPerSocket, BR_Controller controller) {
		super.init(controller);
		this.destHost = destHost;
		this.destPort = destPort;
		setFormat(format);
		setEom(eom);
		this.oneMsgPerSocket = oneMsgPerSocket;
	}

		/** @deprecated Replaced by setFormat(XMLCONVERTER_FMT) */
	public void setUseXML(boolean useXML) {
		setFormat(XMLCONVERTER_FMT);
	}
	
	/**
	 * Send a single String. Opens a socket on localhost port
	 * {@link #destPort}, sends str, closes the connection.
	 *
	 * @param  str String to send
	 */
	public void sendXMLString(String str) {
		try {
			if (trace.getDebugCode("sp")) trace.out("sp", "SocketToolProxy: guid "+SocketProxy.getGuid(controller)+" to send " + str);
			if (oneMsgPerSocket || out == null)
				connect();
			if (out == null) {
				if (trace.getDebugCode("sp")) trace.out("sp", "failed to connect out socket: null stream");
			} else {
				str = insertXMLPrologue(str);
				SocketReader.sendString(str, out, eom);
				if (trace.getDebugCode("tsltstp")) trace.outNT("tsltstp", getGuid()+" "+str);
				if (oneMsgPerSocket)
					disconnect();
			}
		} catch(Exception e){
			//e.printStackTrace();
			trace.err("SocketToolProxy failed to connect outgoing socket to Tutor Interface: " + e.toString());
		}
	}

	private String getGuid() {
		return SocketProxy.getGuid(getController());
	}

	/**
	 * Get the socket used for output. Calls {@link #connect()} to ensure
	 * socket is connected.
	 * @return value of {@link #sock}
	 */
	protected Object getSocket() {
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sock;
	}

	/**
	 * Set the socket used for output. If argument differs from current
	 * value of {@link #sock}, calls {@link #disconnect()} first. 
	 * If argument is not null, calls {@link #connect()} to ensure
	 * socket is connected.
	 *
	 * @param  sock new value for {@link #sock}
	 */
	void setSocket(Socket sock) {
		if (trace.getDebugCode("sp")) trace.out("sp", "old socket " + this.sock + ", new socket " + sock);
		if (out != null) {
			out.flush();             // flush output stream always
			out.close();
		}
		out = null;
		if (this.sock != sock) {     // disconnect only if changing socket
			try {
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.sock = sock;
		if (sock == null)
			return;
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Placeholder for retrying msgs queued when link was down.
	 */
	private void recover() {}

	/**
	 * Connect the socket. Creates and connects socket {@link #sock} and
	 * stream {@link #out} if not already exist. Calls {@link #recover()} if
	 * recreates stream.
	 *
	 * @return value of {@link #sock}
	 */
	private synchronized Socket connect() throws IOException {
		if (sock == null && getConnectFirst()) {
			InetAddress addr = InetAddress.getByName(destHost);
			sock = new Socket(addr, destPort);
			out = null;
		}
		if (out == null && sock != null) {
			out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
			if (!Utils.isRuntime()) {
				setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.NewlyConnected);
			}
			recover();
		}
		return sock;
	}

	/**
	 * Disconnect the socket.  Flushes and closes and nulls
	 * output stream {@link #out} and socket {@link #sock}.
	 */
	synchronized private void disconnect() throws IOException {
		if (out != null) {
			out.flush();
			out.close();
		}
		out = null;
		if (sock != null)
			sock.close();
		sock = null;
		if (!Utils.isRuntime())
			setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.Disconnected);
	}

	/**
	 * Set the logOnly flag.
	 * @param  logOnly new value for {@link #logOnly}
	 */
	public void setLogOnly(boolean logOnly) {
		this.logOnly = logOnly;
	}

	/**
	 * Get the logOnly flag.
	 * @return value of {@link #logOnly}
	 */
	public boolean getLogOnly() {
		return logOnly;
	}

	public void handleMessage(MessageObject o) {
		super.handleMessage(o);
		sendToTee(convertMsgToString(o, teeFormat));
	}

	/**
	 * Label for student interface connection status.
	 * @return "Flash"
	 */
	public String getStudentInterfacePlatform() {
		return "Flash";
	}

	/**
	 * Decide whether a message needs to be sent and generate the actual String if so.
	 * @param o source message 
	 * @return string to send
	 */
	protected String createMessageString(MessageObject o) {
		String toSend = super.createMessageString(o);
		if (logOnly) {
			System.out.println("SocketToolProxy.handleCommMessage():\n" + toSend);
			return null;
		}		
		return toSend;

	}

	/**
	 * Convert a Comm message to a string in the given output format. 
	 * @param o source message
	 * @param format target format
	 * @return
	 */
	private String convertMsgToString(MessageObject o, int format) {
        String msgType = o.getMessageType();
		String toSend = null;
		if (format == XMLCONVERTER_FMT) {			
			toSend = o.toString();
			toSend = toSend.replace('\n', ' ');
			if (trace.getDebugCode("sp")) trace.out("sp", "XML-ized msg, type " + msgType + ":\n" + toSend);
		} else if (format == OLI_XML_FMT) {
// FIXME	OLIMessageObject newMsgObj = new OLIMessageObjectV4(o, true, controller.getLogger());
//			toSend = newMsgObj.toXML();
			if (trace.getDebugCode("sp")) trace.out("sp", "OLI XML-ized msg, type " + msgType + ":\n" + toSend);
		} else
			toSend = null;
		return toSend;
	}
	
	/**
	 * @return Returns {@link #connectFirst}.
	 */
	boolean getConnectFirst() {
		return connectFirst;
	}

	/**
	 * @param connectFirst The connectFirst to set.
	 */
	void setConnectFirst(boolean connectFirst) {
		this.connectFirst = connectFirst;
	}

	/**
	 * Guess the message format from the first character or the presence of a &lt;MessageType&gt; element.
	 * @param msg message to scan
	 * @return {@link #COMM_FMT}, {@link #XMLCONVERTER_FMT} or {@link #OLI_XML_FMT} 
	 */
	public static int deriveMsgFormat(String msg) {
		if (msg.charAt(0) == '[')
			return COMM_FMT;
		else if (MessageObject.getPropertyFromXML(msg, "MessageType") != null)
			return XMLCONVERTER_FMT;
		else
			return OLI_XML_FMT;
	}

	/**
	 * 
	 */
	public void rebootStartState(boolean startStateLock) {
		//FIXME
		MessageObject mo = MessageObject.create("InterfaceReboot");
		handleMessage(mo);
	}
	
	/**
	 * If the new status is disconnected, cancel {@link #waitForSetPreferences} by calling
	 * {@link #setWaitForSetPreferences(long) setWaitForSetPreferences(-1)}. Then call the
	 * superclass implementation.
	 * @param sics new value for {@link #studentInterfaceConnectionStatus};
	 *        a null argument doesn't change the field but does send an event
	 */
	public void setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus sics) {
		if(!sics.isConnected())
			this.awaitSetPreferences(false);
		super.setStudentInterfaceConnectionStatus(sics);
	}

	/** Maximum ms to wait for a {@value MsgType#SET_PREFERENCES} message after interface connected. */
	private static final long MAX_WAIT_FOR_SET_PREFERENCES = 1500;

	/** Maximum ms to wait for {@value MsgType#INTERFACE_DESCRIPTION} messages after request. */
	private static final long MAX_WAIT_FOR_INTERFACE_DESCRIPTIONS = 2500;
	
	/** To generate unique names for {@link Timer} threads. */
	private long nTimers = 0;

	/** Index in {@link #timers} for the timer awaiting a {@value MsgType#SET_PREFERENCES} message. */
	private static final int SET_PREFERENCES_TIMER_INDEX = 0;

	/** Index in {@link #timers} for the timer awaiting a {@value MsgType#GET_ALL_INTERFACE_DESCRIPTIONS} reply. */
	private static final int INTERFACE_DESCRIPTIONS_TIMER_INDEX = 1;

	/** Stores timers {@link #SET_PREFERENCES_TIMER_INDEX},  . */
	private Timer[] timers = { null, null };
	
	/**
	 * Start or cancel a timer at the given index in {@link #timers}. Timer will wait up to the
	 * given number of ms before calling the given {@link Runnable#run()} method. When the timer
	 * expires, calls the method, cancels the timer and nulls the slot in {@link #timers}.
	 * @param ms milliseconds to wait; if <= 0, clear alarm and return prior ms left
	 */
	private synchronized void setWaitForIncomingMsgs(long ms, final int t, final Runnable runUponTimeout) {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", String.format("STP.setWaitForIncomingMsgs(%d, %d, %s) timers [%s,%s]",
					ms, t, trace.nh(runUponTimeout), trace.nh(timers[0]), trace.nh(timers[1])));
		if(timers[t] != null)
			timers[t].cancel();
		timers[t] = null;              // never > 1 thread outstanding
		if(ms <= 0)
			return;

		timers[t] = new Timer((++nTimers)+"STP.timer@"+hashCode()+"_"+getGuid(), true); 
		TimerTask task = new TimerTask() {
			private Timer myTimer = timers[t]; 
			/**
			 * Call {@link BR_Controller#goToStartState(boolean, boolean) BR_Controller.goToStartState(true, true)}.
			 * @see java.util.TimerTask#run()
			 */
			public void run() {
				if(myTimer != timers[t])
					return;
				timers[t].cancel();
				timers[t] = null;
				runUponTimeout.run();
			}
		};
		timers[t].schedule(task, ms);
	}

	/**
	 * Start or stop waiting up to {@value #MAX_WAIT_FOR_SET_PREFERENCES} ms for a
	 * {@value MsgType#SET_PREFERENCES} message before calling
	 * {@link BR_Controller#goToStartState(boolean, boolean)}
	 * to send the start state on our own initiative.
	 * @param begin true means begin wait; false means stop waiting
	 */
	public void awaitSetPreferences(boolean begin) {
		if(Utils.isRuntime())
			return;
		setWaitForIncomingMsgs(begin ? MAX_WAIT_FOR_SET_PREFERENCES : -1,
				SET_PREFERENCES_TIMER_INDEX,
				new Runnable() {
					public void run() {
						getController().goToStartState(true, true);
					}
				});
	}

	/**
	 * Start or stop waiting up to {@value #MAX_WAIT_FOR_INTERFACE_DESCRIPTIONS} ms 
	 * for responses to a {@link #getAllInterfaceDescriptions()} request before calling
	 * {@link #fireStartStateEvent(Object)} to signal that the start state has been received.
	 * @param begin true means begin wait; false means stop waiting
	 */
	public void awaitInterfaceDescriptions(boolean begin) {
		if(Utils.isRuntime())
			return;
		setWaitForIncomingMsgs(begin ? MAX_WAIT_FOR_INTERFACE_DESCRIPTIONS : -1,
				INTERFACE_DESCRIPTIONS_TIMER_INDEX,
				new Runnable() {
					public void run() {
						fireStartStateEvent(getStartStateModel());
					}
				});
	}
}
