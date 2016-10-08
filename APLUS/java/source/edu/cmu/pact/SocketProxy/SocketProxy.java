//SocketProxy.java - proxy between arbitrary student interface and Behavior Recorder, using sockets.
//Gus Prevas, Carnegie Mellon University, 2004

package edu.cmu.pact.SocketProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import pact.CommWidgets.RemoteProxy;
import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.StudentInterfaceConnectionStatus;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.AuthorLauncherServer;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Log.DataShopMessageObject;
import edu.cmu.pact.TutoringService.Collaborators;
import edu.cmu.pact.TutoringService.LauncherServer;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class SocketProxy extends Thread implements RemoteProxy {

	/**
	 * The amount of time that the client can do nothing before disconnecting (in ms):
	 * 30 min (Flash timeout) + 5 min (so Flash exits first) * 60 sec/min * 1000 ms/sec.
	 */
	private static long MAX_IDLE_TIME = (30+5)*60*1000;

	/**
	 * @return {@value #MAX_IDLE_TIME}
	 */
	public static long getMaxIdleTime(){
		return MAX_IDLE_TIME;
	}

	/**
	 * @param maxValue new value for #MAX_IDLE_TIME 
	 */
	public static void setMaxIdleTime(long maxValue) {
		MAX_IDLE_TIME = maxValue;
	}

	/** TCP port number to listen on. */
	private int serverPort = Utils.DEFAULT_SERVER_PORT;

	/** Hostname to which {@link SocketToolProxy} will send. */
	private String clientHost = Utils.DEFAULT_CLIENT_HOST;

	/** A flag for the socket to kill itself */
	private boolean gotQuitMsg;

	/** A serversocket to accept conections */
	private ServerSocket servsock;
	
	/** TCP port to which {@link SocketToolProxy} will send. */
	private int clientPort = Utils.DEFAULT_CLIENT_PORT;

	/** If true, log received msgs only: do not pass to Behavior Recorder. */
	private boolean logOnly = false;

	/**
	 * ActionHandler should expect msgs in this format. Value must be one of
	 * {@link SocketToolProxy#OLI_XML_FMT},
	 * {@link SocketToolProxy#XMLCONVERTER_FMT},
	 * {@link SocketToolProxy#COMM_FMT} (the default).
	 */
	private int msgFormat = SocketToolProxy.XMLCONVERTER_FMT;

	/**
	 * If nonnegative, listener should expect this character as an
	 * end-of-message delimiter.
	 */
	private int eom = 0;

	/**
	 * If true, then output class {@link SocketToolProxy} should use the same
	 * socket connection as this class. Else SocketToolProxy should open its own
	 * socket to host {@link #clientHost}, port {@link #clientPort}. Default
	 * value is true.
	 */
	private boolean useSingleSocket = true;

	/**
	 * If true, then the {@link SocketToolProxy} instantiated from this class
	 * will try to connect before this class tries to listen. Else this class
	 * will listen for connections first. Default value is false.
	 */
	private boolean connectFirst = false;

	/**
	 * If true, then this class and its {@link SocketToolProxy} will send a
	 * single message for each socket connection. Else the same socket(s) will
	 * be reused for all messages
	 */
	private boolean oneMsgPerSocket = false;
	
	/** Socket for connection that reads messages from student interface. */
	protected Socket sock = null;

	/** Reader for connection that reads messages from student interface. */
	private BufferedReader in = null;

	private BR_Controller controller;

	protected SocketToolProxy utp;

	private ActionHandler actionHandler;

	/**
	 * Substitute for OLI Logging Server. See {@link #handleLogRecord(String, Socket)}.
	 */
	private LogServlet logServlet = null;

	private boolean quitOnConnectionBreak = false;

	public static String getControllerProperty(BR_Controller controller, String property) {
    	if (controller == null)
    		return null;
    	
    	if (trace.getDebugCode("log")) trace.out("log", "SocketProxy.getControllerProperty != null\nBR_Controller ="+controller+"\nproperty="+property);
    	if (trace.getDebugCode("log")) trace.out("log", "SocketProxy.getControllerProperty returns "+(String)controller.getProperties().getProperty(property));
        return (String)controller.getProperties().getProperty(property);
    }
    
    String getGuid() {
    	return getGuid(controller);
    }

    public static String getGuid(BR_Controller controller) {
        return controller.getLauncher().getSessionId();
    }

    /**
     * @return {@link #controller} != null
     */
    private boolean isOnline() {
    	return (controller != null);
//    	if (controller == null)
//    		return false;
//    	return controller.inTutoringServiceMode();
    }
    
	private class disconnect extends TimerTask   {
		
		private boolean preserveSession;
		public disconnect(boolean preserveSession) {
			this.preserveSession = preserveSession;
		}
		
		public void run() {
			if (logServlet != null)                                             // exit logger
				logServlet.exit();
			//sendHousekeepingMessage();
            if(getActionHandler() != null)
            	getActionHandler().enqueue(MessageObject.makeQuitMessage());    // exit BR thread
            if (controller!=null) {
            	if(!preserveSession)
            		getController().getLauncher().getLauncherServer().removeSession(getGuid());
                if (sock!=null)
                	RemoteToolProxy.sendInterfaceForceDisconnectMsg(controller);
            }
			if (utp != null)
			{
				setToolProxySocket(null);  // flushes output stream
			}
            try {
				if (sock != null)
					sock.close();
			} catch (Exception e) {
				trace.err("disconnect(): Exception closing socket " + e);
			}
			try {
				if (servsock != null)
					servsock.close();
			} catch (Exception e) {
				trace.err("disconnect(): Exception closing server socket");
			}
			in = null;
			sock = null;
		}
	}

	public void setController(BR_Controller controller) {
		setController(controller, null);
	}
	
	/**
	 * Set this instance's {@link #controller}.
	 * 
	 * @param controller
	 */
	public void setController(BR_Controller controller, BufferedReader br) {
		this.controller = controller;
		if (trace.getDebugCode("sp")) trace.out("sp", "SocketProxy.setController("+controller+") isOnline() "+isOnline()+", utp "+utp);
        if (controller!=null) {
            controller.setRemoteProxy(this); 

            if (isOnline())
            {
            	synchronized(controller.getProperties().listenerMutex)
            	{
            		if (utp == null)
            			utp = createSocketToolProxy(sock, controller);

            		setupSocket(this.sock);
            		if(br == null){
	            		try {
	            			InputStream is = this.sock.getInputStream();
	            			//new BufferedReader(new InputStreamReader(lSock.getInputStream()));
	            			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        				if (trace.getDebugCode("sp")) trace.out("sp", "\nSocketProxy.setController() sock="+
	        						(sock == null ? null : sock.getRemoteSocketAddress())+
	        						", in="+in+" to read...");
	            		} catch (IOException ioe) {
	            			trace.err("Error getting input stream from sock "+
	            					(sock == null ? null : sock.getRemoteSocketAddress()));
	            			ioe.printStackTrace();
	            		}
            		}else{
            			if(trace.getDebugCode("sp"))
            				trace.out("sp", "Reusing the old bufferedReader. Lets hope for success.");
            			in = br;
            		}
            	}
            }
        }
	}

	/**
	 * No-argument constructor sets all defaults, null {@link #controller}.
	 */
	public SocketProxy() {
		this(Utils.DEFAULT_SERVER_PORT, Utils.DEFAULT_CLIENT_HOST, Utils.DEFAULT_CLIENT_PORT,
				false, SocketToolProxy.COMM_FMT, null);
	}

	/**
	 * Construct a listener process.
	 * 
	 * @param serverPort
	 *            Socket number listen on
	 * @param clientHost
	 *            hostname for {@link SocketToolProxy} to send to
	 * @param clientPort
	 *            TCP port for {@link SocketToolProxy} to send to
	 * @param logOnly
	 *            if true, log messages only: do not pass them to
	 *            {@link ActionHandler}
	 * @param msgFormat
	 *            ActionHandler should expect messages in this format: value is
	 *            one of {@link SocketToolProxy#COMM_FMT}, etc.
	 */
	public SocketProxy(int serverPort, String clientHost, int clientPort,
			boolean logOnly, int msgFormat, BR_Controller controller) {
		if (trace.getDebugCode("sp"))
			trace.printStack("sp", String.format("SocketProxy(%d, %s, %d, %b, %d, %s): isOnline = #%b#",
					serverPort, clientHost, clientPort, logOnly, msgFormat,
					controller == null ? "null" : controller.toString(), isOnline()));
		this.serverPort = serverPort;
		this.clientHost = clientHost;
		this.clientPort = clientPort;
		this.logOnly = logOnly;
		this.msgFormat = msgFormat;
		this.controller = controller;
		sock = null;
		in = null;
	}

	/**
	 * Constructor for sockets already established: this class does not need to call accept().
	 * @param sock
	 * @param msgFormat represented as a letter
	 */
	public SocketProxy(Socket sock, String msgFormat) {
		this(sock, msgFormat == null ? SocketToolProxy.XMLCONVERTER_FMT : interpretMsgFormat(msgFormat));
	}

	/**
	 * Constructor for sockets already established: this class does not need to call accept().
	 * @param sock
	 * @param msgFormat
	 */
	public SocketProxy(Socket sock, int msgFormat) {
		
		if (trace.getDebugCode("sp"))
			trace.printStack("sp", String.format("SocketProxy(%s, %d) isOnline = #%b#",
					sock == null ? "null" : sock.toString(), msgFormat,	isOnline()));
		String remoteAddress = sock.getRemoteSocketAddress().toString();
		String clientHost = remoteAddress.split(":")[0];
		int clientPort = new Integer(remoteAddress.split(":")[1]).intValue();
		this.serverPort = sock.getLocalPort();
		this.clientHost = clientHost;
		this.clientPort = clientPort;
		this.logOnly = false;
		this.msgFormat = msgFormat;
		this.controller = null;
		this.sock = sock;
		this.quitOnConnectionBreak = true;
		if (trace.getDebugCode("sp")) trace.out("sp", "SocketProxy("+remoteAddress+")");

	}

	/**
	 * Create and start a {@link LogServlet} for our {@link SocketProxy}.
	 * @param setPrefsMsg 
	 */
	public void setupLogServlet(MessageObject setPrefsMsg) {
		String guid = getGuid();
		Object guidObj = (setPrefsMsg == null ? null : setPrefsMsg.getProperty(Logger.SESSION_ID_PROPERTY));
		if(guidObj instanceof String)
			guid = (String) guidObj;
		LogServlet logServlet =
			new LogServlet(controller.getPreferencesModel(), setPrefsMsg,
					controller.inTutoringServiceMode(), guid);
		
		if (trace.getDebugCode("log")) trace.out("log", "SocketProxy.setLogServlet("+logServlet+")");
		
		setLogServlet(logServlet);
		TSLauncherServer ls = this.getController().getLauncher().getLauncherServer();
		
		if (trace.getDebugCode("log"))
			trace.out("log", "SocketProxy TSLaunchServer ls = "+trace.nh(ls)+
					";\n  guid = "+guid+"; getGuid() = "+getGuid()+
					";\n  BR_Controller ="+controller+" ("+trace.nh(controller)+")");
		
		if (ls != null)
			logServlet.setLogInfo(ls.getLogInfo(guid));

		(new Thread(logServlet)).start();
	}

	/**
	 * Return the end-of-message character.
	 * 
	 * @return {@link #eom}; default value -1 means "no delimiter"
	 */
	public int getEom() {
		return eom;
	}

	/**
	 * Set the end-of-message character.
	 * 
	 * @param new
	 *            value for {@link #eom}; value -1 means "no delimiter"
	 */
	public void setEom(int eom) {
		this.eom = eom;
		if (utp != null)
			utp.setEom(eom);
	}

	/**
	 * Set the end-of-message character.
	 * 
	 * @param new
	 *            value for {@link #eom}; value -1 means "no delimiter"
	 */
	public void setEom(String eomStr) {
		if(eomStr == null)
			return;
		try {
			int eom = Integer.parseInt(eomStr, 16);
			setEom(eom);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid character '" + eomStr
					+ "' for end-of-message: " + e);
		}
	}

	/**
	 * Tell whether this instance and the companion SocketToolProxy should use
	 * the same socket connection.
	 * 
	 * @return value of {@link #useSingleSocket}
	 */
	public boolean getUseSingleSocket() {
		return useSingleSocket;
	}

	/**
	 * Set whether this instance and the companion SocketToolProxy should use
	 * the same socket connection.
	 * 
	 * @param new value for {@link #useSingleSocket};
	 */
	public void setUseSingleSocket(boolean useSingleSocket) {
		this.useSingleSocket = useSingleSocket;
	}

	/**
	 * Set whether this instance and the companion SocketToolProxy should use
	 * the same socket connection.
	 * 
	 * @param new value for {@link #useSingleSocket};
	 */
	public void setUseSingleSocket(String useSingleSocket) {
		if(useSingleSocket == null)
			return;
		this.useSingleSocket = Boolean.parseBoolean(useSingleSocket);
	}

    private void controllerStart() 
    {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SP.controllerStart() ctlr "+controller+", isOnline "+isOnline()+
					", isAcceptingSSMsgs "+(controller == null ? null : controller.isAcceptingStartStateMessages()));
        if (controller==null)
            return;
        if (isOnline())  // don't change the tracer state if in the tutoring service
        	return;
		if (controller.isAcceptingStartStateMessages())
		{
			controller.interfaceConnected ();
			controller.startNewProblem(); // prompt student interface to send interface description
		}	
	//	else
	//		controller.goToStartState(true, true); // send start state msgs & go to start state upon accept()
    }
	
	/**
	 * Set up our {@link #sock} and/or the {@link #utp}'s socket.
	 * @return {@link #sock}
	 */
	protected Socket setupSocket(Socket sock) {
		if (useSingleSocket && !connectFirst)
			setToolProxySocket(sock);
		if (useSingleSocket && connectFirst)
			sock = (Socket) utp.getSocket(); // get socket from SocketProxy
		return sock;
	}

	/**
	 * Create and initialize the {@link SocketToolProxy} instance.
	 * @param sock socket to connect
	 * @param ctlr argument for {@link SocketToolProxy} constructor
	 * @return new instance, initialized
	 */
	protected SocketToolProxy createSocketToolProxy(Socket sock, BR_Controller ctlr) {
		SocketToolProxy stp = new SocketToolProxy(ctlr);
		stp.setConnectFirst(connectFirst);
		if (useSingleSocket && !connectFirst)
			stp.init(sock, msgFormat, eom, controller);
		else
			stp.init(clientHost, clientPort, msgFormat, eom,
					oneMsgPerSocket, controller);
		return stp;
	}

	/**
	 * Listen on a Socket. For each connection accepted, read the socket until
	 * EOF, process the entire content as a single message, then close the
	 * connection.
	 */
	public void run() {
		try {

			if (trace.getDebugCode("sp")) trace.out("sp", "Controller = " + controller);
			if (trace.getDebugCode("sp")) trace.out("sp", "SocketProxy.listen(): isOnline = #"+isOnline()+"#");

			gotQuitMsg = false;
			servsock = null;
	//		sock = null;
	//		in = null;
			
			createActionHandler();

			while (!gotQuitMsg) {
				Socket oldSock = sock;
				if (trace.getDebugCode("sp")) trace.out("sp", "SocketProxy.run() sock "+sock+", in "+in);
				if (sock == null) 
					break;            // if tutoring service, let LauncherServer accept()
				
				if (trace.getDebugCode("sp")) trace.out("sp", "\nSocketProxy.listen(in="+in+") to read...");

				String msg = null;

				Timer timer = null;
				if (isOnline()) {
					timer = new Timer();
					timer.schedule(new disconnect(false), MAX_IDLE_TIME < Long.MAX_VALUE ?
							MAX_IDLE_TIME : Long.MAX_VALUE-System.currentTimeMillis()-1000);
				}
				try {

					msg = (eom >= 0 ? readToEom(in, eom) : readAll(in));
					if (isOnline()) {
						timer.cancel();
						timer = null;
					}
				} catch (Exception e) {
					if (isOnline() && timer != null) {
						timer.cancel();
						timer = null;
					}
					trace.err("\nSocketProxy.listen() read exception: " + e
							+ "\n");
				}
				if (trace.getDebugCode("tsltsp")) trace.outNT("tsltsp", ""+getGuid()+" "+msg);

				if (handlePolicyFileRequest(msg, sock)) {
					oldSock = sock = null;
					in = null;
					continue;
				}
				if (handleLogRecord(msg, sock))
					continue;
				if (sock != oldSock) {   // new connection
					sock = setupSocket(sock);
			        controllerStart();
				}

				if (msg == null || msg.length() <= 0) { // got EOF
					/*
					 * If we are running the TutoringService, after we lose the connection,
					 * then terminate this SocketProxy.
					 */
					if (isOnline() || quitOnConnectionBreak) {
						Collaborators.abort(controller, getGuid(), "A participant has left. Please close the tutor.");
						if (trace.getDebugCode("sp")) trace.outln("sp", "SocketProxy.listen() running disconnect function;"+
								" controller "+controller);
						disconnect dc = new disconnect(true);
						dc.run();
						if(getToolProxy() instanceof RemoteToolProxy)
							((RemoteToolProxy) getToolProxy()).prepareForDisconnect();
						if(controller != null)
							controller.setRemoteProxy(null);
						return;         // exit SocketProxy thread
					}
					closeConnection();                  // sets sock null, tells sender
					continue;
				}
				if ("q".equals(msg.trim())) {
					gotQuitMsg = true;
				} else if (!logOnly) {
					/*
					 * 
					 static function escapeXML(xmlToEscape:String):String{
						xmlToEscape = xmlToEscape.split("<").join("&lt;"); 
						xmlToEscape = xmlToEscape.split(">").join("&gt;");
						return xmlToEscape;		
					}*/
					MessageObject mo = convertMsg(msg);
					if (mo != null){
						TSLauncherServer ls = this.getController().getLauncher().getLauncherServer();
						if(trace.getDebugCode("collab"))
							trace.out("collab", "SP.run(): ls "+trace.nh(ls)+"; guid "+getGuid());
						String guid = null;
						if(ls !=null) {
							guid = getGuid();
							ls.updateTimeStamp(guid);
							mo.setTransactionInfo(ls.createTransactionInfo(guid));
						}
						if(ls == null || ls.enqueueToCollaborators(guid, mo) < 1) {
							if (trace.getDebugCode("sp"))
								trace.out("sp", "guid "+guid+" enqueue to ActionHandler: "+msg);
							getActionHandler().enqueue(mo);
						}
					}
				}
				if (oneMsgPerSocket)
					closeConnection();
			}
			// if (!isOnline.equals("true"))
			closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Log a given message if it should be written to the OLI Logging Server.
	 * @param msg message to test
	 * @param sock socket to pass to {@link LogServlet#enqueue(MessageSocket)}
	 * @return true if a log message, after calling {@link LogServlet#enqueue(MessageSocket)}
	 */
	private boolean handleLogRecord(String msg, Socket sock) {
		return LogServlet.handleLogRecord(msg, sock, logServlet);
	}

	/**
	 * Close the apparatus that reads messages from the student interface. Calls
	 * close() and nulls {@link #in} and {@link #sock}. If
	 * {@link #useSingleSocket} is true, notifies {@link #utp}.
	 */
	private void closeConnection() 
	{
		if (trace.getDebugCode("sp")) trace.out("sp", "CloseConnect(): closing connection");
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
			trace.err("closeConnection(): Exception closing reader " + e);
		}
		try {
			if (sock != null)
				sock.close();
		} catch (Exception e) {
			trace.err("closeConnection(): Exception closing socket " + e);
		}
		in = null;
		sock = null;
				
		if (useSingleSocket)
			setToolProxySocket(sock);
		
		if (controller!=null)
		{
			controller.interfaceDisconnected ();
		}
	}

	/**
	 * Set the proper {@link #utp} socket.
	 * @param sock new value for {@link #utp}.setSocket()
	 */
	protected void setToolProxySocket(Socket sock) {
		utp.setSocket(sock);
		if(sock == null || sock.isClosed())
			utp.setStudentInterfaceConnectionStatus(StudentInterfaceConnectionStatus.Disconnected);
	}
	
	/**
	 * Close the apparatus that reads messages from the student interface. Calls
	 * close() and nulls {@link #in} and {@link #sock}. If
	 * {@link #useSingleSocket} is true, notifies {@link #utp}.
	 * @param preserveSession if true, don't dismantle session information
	 */
	public void extCloseConnection(boolean preserveSession) {
		if (trace.getDebugCode("sp")) trace.out("sp", "extCloseConnection(): closing connection");
		disconnect disc = new disconnect(preserveSession);
		disc.run();
		if(controller != null)
			controller.setRemoteProxy(null);
	}

	/**
	 * Open the server socket to listen for connections from the client.
	 * 
	 * @return new ServerSocket; exits VM after warning if can't open socket
	 */
	private ServerSocket openServerSocket() {
		try {
			ServerSocket ss = new ServerSocket(serverPort);
			return ss;
		} catch (Exception e) {
			String errMsg = "opening server port " + serverPort + ": " + e;
			trace.errStack("FATAL ERROR, exiting JVM: " + errMsg, e);
			if (trace.getDebugCode("sp")) trace.out("sp", "FATAL ERROR, exiting JVM: " + errMsg);
            reportFatalError(errMsg);
			return null; // not reached
		}
	}

	/**
	 * If there is a user interface, report a fatal problem to the user and
	 * call {@link System#exit(int) System.exit(11)}.  Never returns.
	 * @param errMsg
	 */
	protected void reportFatalError(String errMsg) {
		if (!isOnline() && controller!=null)
		    JOptionPane.showMessageDialog(controller.getActiveWindow(),
		                                  "Fatal error " + errMsg + "\nProgram will exit.",
		                                  "Error Opening Port", JOptionPane.ERROR_MESSAGE);
		System.exit(11);
	}



	/**
	 * Read all the characters from a Reader into a String. Reads until receives
	 * end-of-file mark.
	 * 
	 * @param rdr
	 *            Reader to read; should be BufferedReader or equivalent for
	 *            efficiency
	 * @return String with all characters from Reader
	 * @exception IOException
	 */
	public static String readAll(Reader rdr) throws IOException {
		return SocketReader.readAll(rdr);
	}
	public static String readToEom(Reader rdr, int eom) throws IOException {
		return SocketReader.readToEom(rdr, eom);
	}
	/** Help message with command-line arguments. */
	public static final String usageMsg = "Usage:\n"
			+ "  java -classpath ... [-DBehaviorRecorderVisible={true|false}]\\\n"
			+ "      [-DBehaviorRecorderMode={Pseudo-Tutor|Tutor|Demonstrate}]\\\n"
			+ "    SocketProxy [-h clientHost] [-c clientPort] [-d [debugCode,...]] [-e eom] [-b] [-m]\\\n"
			+ "      [-i] [-p] [-L] [-s serverPort] [-X|-M]\n"
			+ "where--\n"
			+ "  -DBehaviorRecorderVisible=... controls whether the BR is displayed (default true);\n"
			+ "  -DBehaviorRecorderMode=... controls the initial BR (default Demonstrate);\n"
			+ "  clientHost is the host on which to listen; default "
			+ "    "
			+ Utils.DEFAULT_CLIENT_HOST
			+ ";\n"
			+ "  clientPort is the port number on which to listen; default "
			+ "    "
			+ Utils.DEFAULT_CLIENT_PORT
			+ ";\n"
			+ "  -d means turn on debugging; if debugCode(s) are present, uses them; default code is \"sp\";\n"
			+ "  eom is an end-of-message character, expressed as a hex integer;\n"
			+ "    e.g., 0A for line-feed, 00 for ASCII NUL;\n"
			+ "  -b means use a single socket for bidirectional communication;\n"
			+ "  -m means send multiple messages per connection;\n"
			+ "  -i means to connect first; default with -m is to listen first;\n"
			+ "  -L means log messages only: do not pass to Behavior Recorder;\n"
			+ "  serverPort is the port number on which to listen; default\n"
			+ "    "
			+ Utils.DEFAULT_SERVER_PORT
			+ ";\n"
			+ "  -X means to expect messages in OLI XML format (default is native Comm);\n"
			+ "  -M means to expect messages in XML-ized Comm format.\n";

	/**
	 * @return Returns the logOnly.
	 */
	public boolean isLogOnly() {
		return logOnly;
	}

	/**
	 * @param logOnly new value for {@link #logOnly}
	 */
	public void setLogOnly(boolean logOnly) {
		this.logOnly = logOnly;
	}

	/**
	 * @param logOnly new value for {@link #logOnly}
	 */
	public void setLogOnly(String logOnly) {
		if(logOnly == null)
			return;
		this.logOnly = Boolean.parseBoolean(logOnly);
	}

	/**
	 * @return Returns the clientHost.
	 */
	public String getClientHost() {
		return clientHost;
	}

	/**
	 * @param clientHost new value for {@link #clientHost} to set.
	 */
	public void setClientHost(String clientHost) {
		if (clientHost == null || clientHost.length() < 1)
			return;
		this.clientHost = clientHost;
	}

	/**
	 * @return Returns the connectFirst.
	 */
	public boolean isConnectFirst() {
		return connectFirst;
	}

	/**
	 * @param connectFirst The connectFirst to set.
	 */
	public void setConnectFirst(boolean connectFirst) {
		this.connectFirst = connectFirst;
	}

	/**
	 * @param connectFirst new value for {@link #connectFirst}
	 */
	public void setConnectFirst(String connectFirst) {
		if(connectFirst == null)
			return;
		this.connectFirst = Boolean.parseBoolean(connectFirst);
	}

	/**
	 * @return Returns the oneMsgPerSocket.
	 */
	public boolean isOneMsgPerSocket() {
		return oneMsgPerSocket;
	}

	/**
	 * @param oneMsgPerSocket The oneMsgPerSocket to set.
	 */
	public void setOneMsgPerSocket(boolean oneMsgPerSocket) {
		this.oneMsgPerSocket = oneMsgPerSocket;
	}

	/**
	 * @param oneMsgPerSocket new value for {@link #oneMsgPerSocket}
	 */
	public void setOneMsgPerSocket(String oneMsgPerSocket) {
		if(oneMsgPerSocket == null)
			return;
		this.oneMsgPerSocket = Boolean.parseBoolean(oneMsgPerSocket);
	}

	/**
	 * @return Returns the clientPort.
	 */
	public int getClientPort() {
		return clientPort;
	}

	/**
	 * @param clientPort new value for {@link #clientPort}
	 */
	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	/**
	 * @param clientPort new value for {@link #clientPort}
	 */
	public void setClientPort(String clientPort) {
		if(clientPort == null)
			return;
		this.clientPort = Integer.parseInt(clientPort);
	}

	/**
	 * @return Returns the serverPort.
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort
	 *            The serverPort to set.
	 */
	public void setServerPort(int serverPort) {
		//trace.out("mg", "SocketProxy (setServerPort): serverPort = " + serverPort);
		this.serverPort = serverPort;
	}
	
	/**
	 * Error-checked String input for setting {@link #msgFormat}.
	 * 
	 * @param fmt
	 *            if an integer, set according to the values of
	 *            {@link SocketToolProxy#COMM_FMT},
	 *            {@link SocketToolProxy#XMLCONVERTER_FMT} or
	 *            {@link SocketToolProxy#OLI_XML_FMT}
	 */
	public void setMsgFormat(String fmt) {
		try {
			int newMsgFormat = interpretMsgFormat(fmt); 
			msgFormat = newMsgFormat;
			if (utp != null)
				utp.setFormat(newMsgFormat);
		} catch (Exception e) {
			// no-op if bad argument
		}
	}
	
	/**
	 * Error-checked String input for setting {@link #msgFormat}.
	 * 
	 * @param fmt
	 *            if an integer, set according to the values of
	 *            {@link SocketToolProxy#COMM_FMT},
	 *            {@link SocketToolProxy#XMLCONVERTER_FMT} or
	 *            {@link SocketToolProxy#OLI_XML_FMT}
	 */
	public static int interpretMsgFormat(String fmt) {
		try {
			int i = Integer.parseInt(fmt);
			if (i < SocketToolProxy.COMM_FMT
					|| SocketToolProxy.OLI_XML_FMT < i)
				throw new IllegalArgumentException(
						"invalid message format value " + fmt);
			return i;
		} catch (NumberFormatException nfe) {
			if (fmt == null)
				throw new IllegalArgumentException(
						"missing message format value");
			if (fmt.startsWith("x") || fmt.startsWith("X"))
				return SocketToolProxy.OLI_XML_FMT;
			else if (fmt.startsWith("m") || fmt.startsWith("M"))
				return SocketToolProxy.XMLCONVERTER_FMT;
			else if (fmt.startsWith("d") || fmt.startsWith("D"))
				return SocketToolProxy.COMM_FMT;
			else
				throw new IllegalArgumentException(
						"invalid message format value " + fmt);
		}
	}

	/**
	 * Get {@link #sock}.
	 * 
	 * @return Returns the sock.
	 */
	Socket getSocket() {
		return sock;
	}

	/**
	 * Set {@link #sock}.
	 * 
	 * @param sock
	 *            The sock to set.
	 */
	void setSocket(Socket sock) {
		this.sock = sock;
	}
		
	private void sendHousekeepingMessage()
	{
		try {
			InetAddress addr = InetAddress.getByName("localhost");
			Socket commSock = new Socket(addr, Utils.DEFAULT_LAUNCHER_COMM_PORT);
			PrintWriter pw = new PrintWriter(commSock.getOutputStream());
			pw.write(getGuid());
			pw.close();
			commSock.close();
			pw = null;
			commSock = null;
		} catch (Exception e) {}
	}

	/**
	 * @return the {@link #msgFormat}
	 */
	public int getMsgFormat() {
		return msgFormat;
	}

	/**
	 * @return the {@link #controller}
	 */
	protected BR_Controller getController() {
		return controller;
	}
	
	/**
	 * @return the {@link #utp}
	 */
	public UniversalToolProxy getToolProxy() {
		return utp;
	}

	/**
	 * Set {@link #actionHandler} to an new instance of {@link ActionHandler}.
	 */
	protected void createActionHandler() {
		setActionHandler(new ActionHandler(this.getController()));
		new Thread(getActionHandler()).start();
	}

	/**
	 * @return the {@link #actionHandler}
	 */
	public ActionHandler getActionHandler() {
		return actionHandler;
	}

	/**
	 * @param actionHandler new value for {@link #actionHandler}
	 */
	protected void setActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	/**
	 * @param msg
	 * @return Comm Message
	 */
	private MessageObject convertMsg(String msg) {
		return convertMsg(msg, getMsgFormat());
	}

	/**
 	 * Convert a message to internal format {@link SocketToolProxy#XMLCONVERTER_FMT}.
	 * @param msg string to convert
	 * @param msgFmt value from {@link #interpretMsgFormat(String)}
	 * @return Comm message
	 */
	public static MessageObject convertMsg(String msg, int msgFmt) {
		try {
			MessageObject mo = null;
			if (msgFmt == SocketToolProxy.OLI_XML_FMT) {
				mo = DataShopMessageObject.parse(msg);
			} else if (msgFmt == SocketToolProxy.XMLCONVERTER_FMT) {
				mo = MessageObject.parse(msg);
			} else {
				throw new IllegalArgumentException("SocketProxy.convertMsg() unknown message format "+
						msgFmt);   // msg prefix dumped by exception-catcher
			}
			if (trace.getDebugCode("sp")) trace.out("sp", "SocketProxy.convertMsg() msgFormat "+msgFmt+
					", messageType "+(mo == null ? "[null mo]" : mo.getMessageType()));
			return mo;
		} catch (Exception e) {
			trace.err("Error converting message \""+
					(msg == null ? null : (msg.length() < 40 ? msg : msg.substring(0, 40)))+
					"\": "+e);
			e.printStackTrace();
			return null;
		}
	}

	private static final String SP_MSG_FORMAT = "spMsgFormat"; 

	/**
	 * Scan a list of command-line arguments for a message format.
	 * @param argv
	 * @return value of "spMsgFormat" parameter, if found;
	 *  default {@link SocketToolProxy#XMLCONVERTER_FMT}
	 */
	public static int argvToMsgFormat(String[] argv) {
		int defaultResult = SocketToolProxy.XMLCONVERTER_FMT;
		if (argv == null)
			return defaultResult;
		try {
			for (int i = 0; i < argv.length; ++i) {
				if (argv[i] == null)
					continue;
				String arg = argv[i].toLowerCase();
				int s = arg.indexOf(SP_MSG_FORMAT.toLowerCase());
				if (s < 0)
					continue;
				int e = s + SP_MSG_FORMAT.length();
				if (arg.length() >= (e + 2) && arg.charAt(e) == '=')
					return interpretMsgFormat(arg.substring(e+1, e+2));
				if (++i < argv.length)
					return interpretMsgFormat(argv[i].substring(0, 1));
			}
		} catch (Exception e) {
			// fall out for default
		}
		return defaultResult;
	}

	/**
	 * Text of socket policy request, used by Flash's security apparatus.
	 * See {@link #handlePolicyFileRequest(String, Socket)}.
	 */
	private static final String policyFileRequest = "<policy-file-request/>";

	/**
	 * Text of response to socket policy request, used by Flash's security apparatus.
	 * See {@link #handlePolicyFileRequest(String, Socket)}. Value is:
	 * <pre>
	 * {@value #socketPolicyContent}
	 * </pre>
	 */
    private static final String socketPolicyContent = "<cross-domain-policy>\n"+
				    	"<site-control permitted-cross-domain-policies=\"master-only\"/>\n"+
				    	"<allow-access-from domain=\"*\" to-ports=\"*\" />\n" +
				    	"</cross-domain-policy>\0";

    /**
     * Check whether the given message is a socket policy request. If so, will reply with
     * {@link #socketPolicyContent} and close the socket.
	 * See the Flash Player document
	 * <a href="http://www.adobe.com/devnet/flashplayer/articles/fplayer9_security_05.html#_Configuring_Socket_Policy">
	 * Configuring Socket Policy</a> for details.
     * @param msg string received from socket
     * @param pSock socket to respond to
     * @return true if msg was a policy file request: if true is returned, pSock has been closed;
     *         false (no-op) otherwise
     */
	public static boolean handlePolicyFileRequest(String msg, Socket pSock) {
		try {
			if (msg == null)
				throw new IOException("null message from socket");
			if (!msg.regionMatches(0, policyFileRequest, 0, policyFileRequest.length()))
				return false;
			/*This is a policy file request*/
			if (trace.getDebugCode("ls")) trace.outNT("ls", "SocketProxy.handlePolicyFileRequest()\nReceived a policy request on "+
					"local "+pSock.getLocalPort()+", remote "+pSock.getPort()+"\n");
			PrintWriter pw = new PrintWriter(pSock.getOutputStream());
			pw.write(socketPolicyContent);
			pw.close();
			pSock.close();
		} catch (IOException ioe) {
			StringBuffer errMsg = new StringBuffer("Error in security handshake with Flash");
			errMsg.append(".\nMore info: exception sending policy file response: ").append(ioe);
			if (ioe.getCause() != null)
				errMsg.append("\n cause: ").append(ioe.getCause());
			trace.errStack(errMsg.toString(), ioe);
			// if !isOnline(), JOptionPane.showMessageDialog() to warn user.
			try { if (pSock != null) pSock.close(); } catch (Exception e) {}
			// fall through to return true since socket now closed
		}
		return true;
	}

	/**
	 * @return {@link #socketPolicyContent}
	 */
	public static String getSocketPolicyContent() {
		return socketPolicyContent;
	}

	/**
	 * @return the {@link #logServlet}
	 */
	public synchronized LogServlet getLogServlet() {
		return logServlet;
	}

	/**
	 * @param logServlet new value for {@link #logServlet}
	 */
	public synchronized void setLogServlet(LogServlet logServlet) {
		this.logServlet = logServlet;
	}
}
