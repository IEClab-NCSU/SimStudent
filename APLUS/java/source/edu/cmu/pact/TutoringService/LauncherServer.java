/**
 * Copyright 2011 Carnegie Mellon University.
 */
package edu.cmu.pact.TutoringService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom.Element;

import pact.CommWidgets.RemoteToolProxy;
import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATHTTPSServer;
import edu.cmu.hcii.ctat.CTATHTTPServer;
import edu.cmu.hcii.ctat.ExitableServer;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.SocketProxy.HTTPToolProxy;
import edu.cmu.pact.SocketProxy.LogServlet;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.SocketProxy.SocketToolProxy;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/** Parent class: one per servlet. */
public class LauncherServer extends TSLauncherServer implements Runnable, ExitableServer {

	/** By default, listen on this port for tutor connections. */
	private static final int DEFAULT_SERVER_PORT = 1502;

	/** Bind to this port to serve socket policy requests from Flash Player 9.0.115.0 and up. */
	private static final int MASTER_SOCKET_POLICY_PORT = 843;

	/** Keystore parsed from command line. */
	private static String keystore = null;//	/** The sessions table. */
	
    private final int commPort;

	private SessionKeeper sk = null;

	/** Communicates with NTP server to provide NTP timestamps. */
	private NtpClient ntpClient = null;
	
	/** Housekeeping thread. */
	private Monitor ct = null;
	
	/** Timestamp set by {@link #shutdown()}. */
	private volatile Date shutdownTime = null;
	
	/** Servers for {@link #startExiting()} to halt. */
	private List<ExitableServer> otherServers = new LinkedList<ExitableServer>();

	/** Ports on which to listen for HTTP connections from tutor clients. */
	private List<Integer> httpPorts;

	/** Ports on which to listen for HTTPS connections from tutor clients. */
	private List<Integer> httpsPorts;

	/** Ports on which to listen for TCP stream socket connections from tutor clients. */
	private List<Integer> tcpPorts;

	/**
	 * Virtual machine entry point for the server-based tutoring service. 
	 * @param args (see {@link #parseCmdLine(String[], List, List)}): 
	 *        default "-t" {@value #DEFAULT_SERVER_PORT} if none
	 */
	public static void main(String[] args)
	{
		create(args.length > 0 ? args : new String[] { "-t", Integer.toString(DEFAULT_SERVER_PORT) });
	}

	/**
	 * Factory. Command line format: 
	 *   <code>java -cp ... LauncherServer -t [tcpPorts] -h [httpPorts]</code>
	 * @param args HTTP and TCP ports for tutor clients; see above
	 * @return instance with one thread listening on each given port
	 */
	@SuppressWarnings("unchecked")
	public static LauncherServer create(String[] args)
	{
		List<Integer> httpPorts = new ArrayList<Integer>();
		List<Integer> httpsPorts = new ArrayList<Integer>();
		List<Integer> tcpPorts = new ArrayList<Integer>();
		parseCmdLine(args, httpPorts, httpsPorts, tcpPorts);
		
	    CTATBase.debug ("LauncherServer","Listening on HTTP ports "+httpPorts+
	    		", HTTPS ports "+httpsPorts+", TCP ports "+tcpPorts);
        
    	String crossDomainPolicy = "<?xml version=\"1.0\"?>\n" +
    					"<!DOCTYPE cross-domain-policy SYSTEM \"http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd\">\n" + 
    					"<cross-domain-policy>\n" + 
    					"<allow-access-from domain=\"*\" to-ports=\"*\" />\n" +
    					"</cross-domain-policy>\0";

    	LauncherServer ls = new LauncherServer (httpPorts,
    											httpsPorts,
    											tcpPorts, 
    											Monitor.MONITOR_PORT,
    											1504,
    											crossDomainPolicy,
    											MASTER_SOCKET_POLICY_PORT,
    											SocketProxy.getSocketPolicyContent());

    	Thread t = new Thread(new ThreadGroup("TutoringService"), ls, "LauncherServer");
    	
		t.start();		

		return ls;
	}

	/**
	 * Parse an array of command-line arguments.
	 * <b>N.B.:</b> calls {@link System#exit(int) System.exit(2)} on bad argument.
	 * @param args array from arguments in the form
	 *        <code>[-h httpPort...] [-s httpsPort...] [-t tcpPort...]</code>
	 * @param httpPorts to return HTTP ports listed
	 * @param tcpPorts to return TCP ports listed
	 */
	private static void parseCmdLine(String[] args, 
			List<Integer> httpPorts,
			List<Integer> httpsPorts,
			List<Integer> tcpPorts) 
	{				
		List<Integer> currentList = tcpPorts;  // which list we're in now
		
		int i = -1;
		try {
			for(i = 0; i < args.length; ++i) {
				String arg = args[i];
				if("-json".equalsIgnoreCase(arg)) {
					HTTPToolProxy.setOutputJSON(true);
					continue;
				}
				if("-h".equalsIgnoreCase(arg)) {
					currentList = httpPorts;
					continue;
				}
				if("-s".equalsIgnoreCase(arg)) {
					currentList = httpsPorts;
					continue;
				}
				if("-t".equalsIgnoreCase(arg)) {
					currentList = tcpPorts;
					continue;
				}
				if("-keystore".equalsIgnoreCase(arg)) {
					keystore = args[++i];
					continue;
				}
				currentList.add(new Integer(arg));
			}
		} catch(Exception e) {
			trace.err("Fatal error: error at argument "+(i+1)+" \""+args[i]+"\": "+e);
			System.err.printf("\nCommand-line arguments are\n"+
					"    \"[-json] [-h httpPort...] [-s httpsPort...] [-t tcpPort...] [-keystore file.jks]\"\n"+
					"where--\n"+
					"    -json means that HTTP output should be in JSON; default is XML;\n"+
					"    httpPort... is a space-separated list of ports on which to listen for HTTP clients;\n"+
					"    httpsPort... is a space-separated list of ports on which to listen for HTTPS clients;\n"+
					"    tcpPort... is a space-separated list of ports on which to listen for TCP socket clients;\n"+
					"    file.jks is the full path to the JKS keystore file for https connections (default %s);\n"+
					"by default, uses single TCP port %d.\n",
					CTATHTTPSServer.defaultKeystore, DEFAULT_SERVER_PORT);
			System.exit(2);
		}
	}

	/**
	 * Constructor for internal calls and unit tests. For Tutoring Service running
	 * outside local TutorShop or running outside an Applet, use {@link #create(String[])}.
	 */
	public LauncherServer() {
		this(null, null, null, -1, -1, "", -1, "");
	}
    
    /**
     * Constructor
     * @param serverPort
     * @param commPort
     * @param crossDomainPolicyPort
     * @param policy
     */
	LauncherServer(List<Integer> httpPorts, 
				   List<Integer> httpsPorts,
				   List<Integer> tcpPorts, 
				   int commPort,
				   int crossDomainPolicyPort,
				   String crossDomainPolicyContent,
				   int socketPolicyPort, 
				   String socketPolicyContent)
	{
		super();
		
		this.httpPorts = httpPorts;
		this.httpsPorts = httpsPorts;
		this.tcpPorts = tcpPorts;
		this.commPort = commPort;

		getPreferencesModel().setBooleanValue("Restore workspace", Boolean.FALSE); // for Runtime
		
		/*
		neither of these two threads are started, however according to documentation:
		http://www.adobe.com/devnet/flashplayer/articles/flash_player_9_security.pdf
		they may eventually be required (socketPolicyServer listening on port 843 instead of
		the current response on port 1501)
		if (crossDomainPolicyPort > 0
				&& crossDomainPolicyContent != null && crossDomainPolicyContent.length() > 0)
			crossDomainPolicyServer = new PolicyThread(crossDomainPolicyContent, crossDomainPolicyPort);
		
		if (socketPolicyPort > 0
				&& socketPolicyContent != null && socketPolicyContent.length() > 0)
			socketPolicyServer = new PolicyThread(socketPolicyContent, crossDomainPolicyPort);
		*/
	}
		
	public void sendIdentificationRequest(Socket sock)
	{
		String toSend = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceIdentificationRequest</MessageType></properties></message>";
		trace.out("Sending XML msg via remote port#" + sock.getPort() + " and local port# " + sock.getLocalPort() + ":\n" + toSend);
		try {
			PrintWriter pw = new PrintWriter(sock.getOutputStream());
			pw.write(toSend);
		} catch (IOException e) {}
	}
	
	private class SessionKeeper extends Thread implements ExitableServer
	{
		private long maxIdleTime;
		private Thread myThread = null;
		public SessionKeeper(){
			maxIdleTime = SocketProxy.getMaxIdleTime();
		}
		public void run(){
			myThread  = Thread.currentThread();
			while(!nowExiting){
				try {
					Thread.sleep(maxIdleTime+60000);
				} catch (InterruptedException e) {
					if (trace.getDebugCode("ls"))
						trace.out("ls", "SessionKeeper.run() nowExiting "+nowExiting+
								"; Thread.sleep() threw "+e);
					e.printStackTrace();
					if (nowExiting)
						break;
				}
				//sessions.
				Long currTime = new Date().getTime();
				synchronized(sessionsMutex){
					Iterator<String> sessionIterator = sessions.keySet().iterator();
					while(sessionIterator.hasNext()){
						String key = sessionIterator.next();
						TSLauncherServer.Session s;
						if(key == null || (s = sessions.get(key)) == null)
							continue;
						if((currTime-s.getTimeStamp().getTime()) > maxIdleTime){
							sessionIterator.remove();
							RemoteToolProxy.sendInterfaceForceDisconnectMsg(s.controller);
						}
					}
				}
			}
		}
		private volatile boolean nowExiting = false;
		public boolean isExiting() {
			return nowExiting;
		}
		public boolean startExiting() {
			boolean result = nowExiting;
			nowExiting = true;
			if (myThread != null)
				myThread.interrupt();  // wake run() from sleep()
			if (trace.getDebugCode("ls"))
				trace.out("ls", "SessionKeeper.startExiting() previous nowExiting "+result+
					", interrupted "+myThread+"; nSessions to disconnect "+sessions.size());
			TSLauncherServer.Session s;
			synchronized(sessionsMutex){
				Iterator<Entry<String, TSLauncherServer.Session>> sessionIterator = sessions.entrySet().iterator();
				while(sessionIterator.hasNext()){
					s = sessionIterator.next().getValue();
					sessionIterator.remove();
					s.controller.disconnect(false);  // false: remove session
				}
			}
			return result;
		}
	}

	/**
	 * Listen on a {@link ServerSocket} and accept client connections. 
	 */
	class Listener extends Thread implements ExitableServer {

		/** Socket to bound to port. */
		private ServerSocket ss;
		
		/** Set this flag to tell the listener to quit. */
		private volatile boolean nowExiting = false;
		
		/** Timestamp set by {@link #startExiting()}. */
		private volatile Date shutdownTime = null;
		
		/**
		 * Open {@link #ss} on the given port.
		 * @param port
		 */
		Listener(int port) {
			setName("LauncherServer.Listener_on_port_"+port);
			try {
				ss = new ServerSocket(port);
				if(trace.getDebugCode("ls"))
					trace.out("ls", "LauncherServer.Listener("+port+") created on local port "+ss.getLocalPort());
			} catch(Exception e) {
				trace.err("Error opening server socket on port "+port+" in LauncherServer.Listener constructor: "+
						e+(e.getCause() == null ? "" : ";\n  cause: "+e.getCause()));
				e.printStackTrace();
			}
		}
		
		/**
		 * Loop forever accepting connections on {@link #ss} and creating new
		 * {@link LauncherServer.Session} instances.
		 */
		public void run() {
			if(trace.getDebugCode("ls"))
				trace.out("ls", "LauncherServer.Listener waiting on port "+ss.getLocalPort());
			try {
				while (!nowExiting) {
					Socket s = ss.accept();
					Session sess = new Session(s);
					sess.setServerPort(ss.getLocalPort());
					if(trace.getDebugCode("ls"))
						trace.out("ls", "LauncherServer.Listener accepted socket "+s.toString());
					sess.start();				
				}	
			} catch (SocketException se) {
				trace.err("Shutdown Time "+shutdownTime+", nowExiting "+nowExiting+"; exception "+se+
						 (se.getCause() == null ? "" : ";\n  cause: "+se.getCause()));
			} catch (IOException io) { 
				trace.err("Fatal I/O error from LauncherServer top-level thread: exception "+io+
						 (io.getCause() == null ? "" : ";\ncause: "+io.getCause()));
				io.printStackTrace(); 
			}
		}

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
		public boolean startExiting() {
			boolean result = nowExiting;
			nowExiting = true;
			if (trace.getDebugCode("ls"))
				trace.out("ls", "LauncherServer.Listener.startExiting() server socket to close "+ss);
			try {
				ss.close();
				ss = null;
			} catch (Exception e) {
				trace.errStack("LauncherServer.Listener.startExiting() error closing server socket "+e+
						";\n  cause "+e.getCause(), e);
			}
			return result;
		}
	}

	/**
	 * Run LauncherServer
	 * Creates the umbrella socket with the Monitor class
	 * Passes new socket connections to instances of LauncherConsumerThread.
	 * 
	 */
	public void run() {
        trace.out("LauncherServer : entered run");

        Thread lsThread = new Thread(logServlet, "LogServlet");
        lsThread.start(); otherServers.add(logServlet);

        sk = new SessionKeeper();
        sk.setName("SessionKeeper"); sk.start(); otherServers.add(sk);
        
        ntpClient = new NtpClient();
        ntpClient.setName("NtpClient"); ntpClient.start(); otherServers.add(ntpClient);

        // crossDomainPolicyServer.start(); otherServers.add(crossDomainPolicyServer);
        // socketPolicyServer.start(); otherServers.add(socketPolicyServer);
		
        try {
        	shutdownTime = null;
			if(commPort > 0) {
				ct = new Monitor(commPort);
				ct.addRequestHandler(SessionRequest.NAME, new SessionRequest(this));
				ct.addRequestHandler(ServiceRequest.NAME, new ServiceRequest(this));
				ct.setName("Monitor"); ct.start(); otherServers.add(ct);
			}
		} catch (Exception io) { 
			trace.err("Fatal error from LauncherServer top-level thread: exception "+io+
					 (io.getCause() == null ? "" : ";\ncause: "+io.getCause()));
			io.printStackTrace(); 
		}
        
        startHTTPHandler();
        startHTTPSHandler();
        
        int i = tcpPorts.size();
        Listener listener = null;
        while(0 < --i) 
        {
        	CTATBase.debug ("LauncherServer","Starting server on: " + tcpPorts.get(i));
        	// create new thread for 2nd and other ports
        	listener = new Listener(tcpPorts.get(i));
        	otherServers.add(listener);
        	listener.start();
        }
        
        CTATBase.debug ("LauncherServer","Starting server on: " + tcpPorts.get(i));
        
        listener = new Listener(tcpPorts.get(i));
    	otherServers.add(listener);               // just use this thread for 1st port
        listener.run();    
	}

	/**
	 * Start a {@link LauncherHandler} instance for each port in {@link #httpPorts}. 
	 */
	private void startHTTPHandler() {
		if(trace.getDebugCode("ll"))
			trace.outNT("ll", "LS.startHTTPHander() httpPorts "+httpPorts);
		for(Integer httpPort : httpPorts) {
	        CTATHTTPServer server = new CTATHTTPServer(httpPort.intValue(), null, null,
	        		new LauncherHandler(this));
	        server.startWebServer();
		}
	}

	/**

	 * Start a {@link LauncherHandler} instance for each port in {@link #httpsPorts}.
	 * For each, calls {@link CTATHTTPSServer#setKeystore(String)} with argument {@link #keystore}.
	 */
	private void startHTTPSHandler() {
		if(trace.getDebugCode("ll"))
			trace.outNT("ll", "LS.startHTTPHander() httpsPorts "+httpsPorts);
		for(Integer httpsPort : httpsPorts) {
	        CTATHTTPSServer server = new CTATHTTPSServer(httpsPort.intValue(), null, null,
	        		new LauncherHandler(this));
	        server.setKeystore(keystore);  // setter is no-op if arg null or empty
	        server.startWebServer();
		}
	}

	/**
	 * Call {@link #startExiting()}, pause, then {@link System#exit(int) System.exit(0)}.
	 */
	public void shutdown() {
		startExiting();
		Utils.sleep(500);           // allow threads to quit
		System.exit(0);
	}
	
	class PolicyThread extends Thread implements ExitableServer
	{
		/** Bind to this port to listen for policy connections. */
		private int policyPort;
		
		/** The actual policy string to send in response */
		private String policyContent;
		
		private class PolicyThreadConsumer extends Thread
		{
			Socket pSock;

			/**
			 * @param sock active socket
			 */
			public PolicyThreadConsumer (Socket sock)
			{		
				this.pSock = sock;
			}
			
			public void run()
			{
				try {
					trace.out("Received a policy connection on "+pSock.getLocalPort()+"\n");
					PrintWriter pw = new PrintWriter(pSock.getOutputStream());
					pw.write(policyContent);
					pw.close();
					//br.close();
					pSock.close();
				} catch (IOException ioe) {System.out.println(ioe.getStackTrace());} 
			}
		}

		/**
		 * @param policyContent String to send in response
		 * @param policyPort
		 */
		public PolicyThread(String policyContent, int policyPort)
		{
			this.policyContent = policyContent;
			this.policyPort = policyPort;
		}
		
		public void run()
		{
			try {
				policySocket = new ServerSocket(this.policyPort);
				while (!nowExiting)
				{
                    trace.out("waiting for a policy connection...");
					Socket ps = policySocket.accept();
					PolicyThreadConsumer ptc = new PolicyThreadConsumer(ps);
					ptc.start();
				}
			} catch (IOException e) {System.out.println(e.getStackTrace());}
		}
		
		/** Set this flag to tell the server to quit. */
		private volatile boolean nowExiting = false;

		/** Server socket listening for policy requests. */
		private ServerSocket policySocket = null;

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
			shutdownTime = new Date();
			boolean result = nowExiting;
			nowExiting = true;
			if (trace.getDebugCode("ls"))
				trace.out("ls", "PolicyThread.startExiting() previous nowExiting "+result+
					", server socket to close "+policySocket);
			try {
				if (policySocket != null)
					policySocket.close();
			} catch (Exception e) {
				trace.errStack("PolicyThread.startExiting() error closing server socket "+e+
						";\n  cause "+e.getCause(), e);
			}
			policySocket = null;
			return result;
		}
	}
	
	public void updateTimeStamp(String guid){
		synchronized(sessionsMutex){
			Session s = sessions.get(guid);
			if(s != null)
				s.updateTimeStamp();
		}
	}

	/**
	 * Kill a session by removing it from {@link #sessions} and closing its socket.
	 * @param guid
	 * @return
	 * @see edu.cmu.pact.TutoringService.TSLauncherServer#removeSession(java.lang.String)
	 */
	public boolean removeSession(String guid) {
		Session sess = null;
		Collaborators collabs = null;
		synchronized(sessionsMutex){
			sess = sessions.remove(guid);
			if (sess != null) {
				collabs = allCollaborators.removeSession(sess);
				if(sess.getController() != null)
					sess.getController().disconnect(true);  // true: we've removed the session ourselves
			}
			if (trace.getDebugCode("ls"))
				trace.outln("ls", (sess != null ? "Removed" : "Found no")+
						" session for GUID: "+guid+". Updated sessions.size: "+sessions.size()+
						", collabs "+collabs+", allCollaborators.size() "+allCollaborators.size());
		}
		try {
			if (sess != null && sess.lSock != null)
				sess.lSock.close();
		} catch (Exception e) {
			trace.errStack("removeSession(): error closing socket for GUID "+guid, e);
		}
		return sess != null;
	}

	/**
	 * @return the {@link #ntpClient}
	 */
	public NtpClient getNtpClient() {
		return ntpClient;
	}

	/**
	 * @return {@link #sessions}.{@link Map#keySet() keySet()}
	 */
	public Set<String> getSessionKeys() {
		synchronized(sessionsMutex){
			return sessions.keySet();
		}
	}

	/**
	 * Create a structure to record current transaction timing info.
	 * @param sessionId session identifier
	 */
	public TransactionInfo.Single createTransactionInfo(String sessionId) {
		if (sessionId == null)
			return null;
		Session session = getSession(sessionId);
		if (session != null)
			return session.txInfo.create();
		return null;
	}

	/**
	 * Update current transaction timing info.
	 * @param sessionId
	 * @param info
	 * @see edu.cmu.pact.TutoringService.TSLauncherServer#updateTransactionInfo(java.lang.String, java.lang.Object)
	 */
	public void updateTransactionInfo(String sessionId, Object info) {
		if (sessionId == null)
			return;
		Session session = getSession(sessionId);
		if (session != null)
			session.txInfo.update(info);
	}

	/**
	 * Generate a element with data on an individual {@link LauncherServer.Session}. 
	 * @param guid
	 * @return
	 */
	Element generateSessionElement(String guid) 
	{
		CTATBase.debug ("LauncherServer","generateSessionElement(String guid)");
		
		Session sess = getSession(guid);
		
		if (sess == null) 
		{
			trace.err ("null session object for guid \""+guid+"\"");
			return null;
		}
		
		Element session = new Element("session");
		session.setAttribute("guid", guid);
		//session.setAttribute("lastMessage", dateFmt.format(sess.getTimeStamp()));
		session.setAttribute("lastMessage", String.format ("%d",sess.getTimeStamp().getTime()));
		session.setAttribute("ip", sess.getIPAddr());
		Map<String, String> txInfo = sess.getTxInfo().toAttributesRaw();
		
		for (String key : txInfo.keySet())
			session.setAttribute(key, txInfo.get(key));
				
		Map<String, String> logInfo = sess.getLogInfo().toAttributes();
				
		for (String key : logInfo.keySet())
			session.setAttribute(key, logInfo.get(key));
				
		return session;
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
		shutdownTime = new Date();
		if(trace.getDebugCode("ls"))
			trace.out("ls", "LauncherServer.startExiting() at "+shutdownTime);
		for (ExitableServer es : otherServers)
			es.startExiting();

		return result;
	}
}
