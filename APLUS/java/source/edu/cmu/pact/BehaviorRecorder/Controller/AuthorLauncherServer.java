/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.hcii.ctat.ExitableServer;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.TutoringService.Collaborators;
import edu.cmu.pact.TutoringService.LauncherServer;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.TutoringService.TSLogInfo;
import edu.cmu.pact.TutoringService.TransactionInfo.Single;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * @author sewall
 */
public class AuthorLauncherServer extends TSLauncherServer {

	/** Connections to behavior graph windows. */
	private final CTATTabManager tabManager;
	
	/** Command-line arguments, needed for initializing {@link SocketReader}. */
	private final Map<String, String> socketArgs;
	
	/** End-of-message character for stream sockets. Default is NUL. */
	private int eom = '\0';

	/** The listener for incoming connections from remote user interfaces. */
	private Listener listener = null;

	/**
	 * @param tcpPort listen to this port for stream socket connections
	 * @param ctat_Launcher
	 * @param tabManager
	 * @param cmdLineArgs command-line arguments, needed for initializing {@link SocketReader}
	 */
	public AuthorLauncherServer(int tcpPort, CTATTabManager tabManager, String[] cmdLineArgs) {
		super();
		this.tabManager = tabManager;		

		socketArgs = createSocketArgsList(cmdLineArgs == null ? new String[0] : cmdLineArgs);
		try {
			int eom = -1; String eomStr = null;
			if((eomStr = socketArgs.get("eom")) != null && (eom = Integer.valueOf(eomStr, 16)) >= 0)
				this.eom = eom;
		} catch(Exception e) {
			trace.errStack("Error converting end of message (eom) argument to integer", e); 
		}
		if(tcpPort > 0)
			listener = new Listener(tcpPort);
	}

	/**
	 * Start the {@link #listener} thread.
	 */
	void startListener() {
		if(listener != null)
			listener.start();
	}

	/**
	 * Find all command-line arguments beginning with "-sp" and create a map whose keys are the
	 * options and whose option values are the values given. All keys are coerced to lower-case.
	 * @param cmdLineArgs
	 * @return map with options as keys, values as values; getter coerces all keys to lower-case
	 */
	private Map<String, String> createSocketArgsList(String[] cmdLineArgs) {
		Map<String, String> result = new LinkedHashMap<String, String>() {
			
			/** For {@link Serializable}. */
			private static final long serialVersionUID = 201401141600L;

			/**
			 * Replace {LinkedHashMap#get(Object)} one that coerces keys to lower-case
			 * @param key
			 * @return value for key; null if key is null
			 */
			public String get(String key) {
				String result;
				if(key == null)
					result = null;
				else
					result = super.get(key.toLowerCase());
				if(trace.getDebugCode("sp"))
					trace.out("socketArgs.get("+key+") returns "+result+";");
				return result;
			}
			
			/**
			 * Replace {LinkedHashMap#get(Object)} one that coerces keys to lower-case
			 * @param key
			 * @return
			 */
			public boolean containsKey(String key) {
				if(key == null)
					return false;
				else
					return super.containsKey(key.toLowerCase());
			}
		};
		
		for(int i = 0; i < cmdLineArgs.length; ++i) {
			String option = cmdLineArgs[i];
			if(!(option.startsWith("-sp")))
				continue;
			String value;
			int eq = option.indexOf('=');
			if(eq >= 0) {                            // argument is of format "-spServerPort=1502"
				value = option.substring(eq+1);
				option = option.substring(3, eq).toLowerCase();              // 3 == length("-sp")
			} else {                                 // argument is of format "-spServerPort 1502"
				value = (++i < cmdLineArgs.length ? cmdLineArgs[i] : null);
				option = option.substring(3).toLowerCase();
			}
			result.put(option, value);
		}
		return result;
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
			setName("AuthorLauncherServer.Listener_on_port_"+port);
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
				trace.out("ls", "AuthorLauncherServer.Listener waiting on port "+ss.getLocalPort());
			try {
				while (!nowExiting) {
					Socket s = ss.accept();
					Connection conn = new Connection(s);
					if(trace.getDebugCode("ls"))
						trace.out("ls", "AuthorLauncherServer.Listener accepted socket "+s);
					(new Thread(conn)).start();				
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
	 * Read the first message from a socket and dispatch as policy request or tutor session.
	 */
	class Connection implements Runnable {

		/** Socket for this connection. */
		private Socket sock;
		
		/**
		 * @param localPort TCP port assigned to this end of the connection
		 */
		public Connection(Socket sock) {
			this.sock = sock; 
		}

		/**
		 * Close the given input stream and socket. Handles all exceptions.
		 * @param br
		 */
		private void close(Reader br) {
			try {
				if (br != null) br.close(); 
			} catch (Exception e) {
				trace.err("LauncherServer.close(): Error closing socket stream: "+e+"; cause "+e.getCause()); 
			}
			try {
				if (sock != null) sock.close();
				sock = null;
			} catch (Exception es) {
				trace.err("Error closing socket: "+es+"; cause "+es.getCause()); 
			}
		}

		/**
		 * Run LauncherConsumerThread
		 * Get the GUID and setPrefs message from the socket.
		 */
		public void run()
		{
			BufferedReader br =  null;
			try {
				if (trace.getDebugCode("ls"))
					trace.outNT("ls", "Trying to get GUID");
				br = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
				String result = SocketReader.readToEom(br, '\0');

				if (SocketProxy.handlePolicyFileRequest(result, sock))
					return;

				if (trace.getDebugCode("ls"))
					trace.outNT("ls", "AuthorLauncherServer.Session possible Guid: "+result);
				String guid = MessageObject.getPropertyFromXML(result, "Guid");
				if (guid == null || !tabManager.connectSocket(guid, sock, br, socketArgs)) {
					sendDisconnect(sock, guid);
					if (trace.getDebugCode("ls"))
						trace.outNT("ls", "AuthorLauncherServer.Session: guid "+guid+", closing socket");
					close(br);
				}

			} catch (Exception e) {
				trace.errStack("AuthorLauncherServer.Session.run() 1st half error, closing socket: ", e);
				if (trace.getDebugCode("ls"))
					trace.outNT("ls", "Exception in session.run : " + e);
				close(br);
				return;
			}
		}
	}
	public boolean removeSession(String guid) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Create and send a {@value MsgType#INTERFACE_FORCE_DISCONNECT} message to tell the
	 * student interface to quit.
	 * @param sock socket to write; will call {@link Socket#getOutputStream()}
	 * @param guid stem for {@link MessageObject#setTransactionId(String)} argument
	 */
	private void sendDisconnect(Socket sock, String guid) {
		PrintWriter out = null;
		try {
			if(sock == null || (out = new PrintWriter(sock.getOutputStream())) == null)
				return;
		} catch(Exception e) {
			trace.errStack("AuthorLauncherServer.sendDisconnect() could not open output stream for"+
					" socket "+sock, e);
			return;
		}
		MessageObject mo = RemoteToolProxy.createInterfaceForceDisconnectMsg(guid+System.currentTimeMillis());
		String msg = RemoteToolProxy.insertXMLPrologue(mo.toString());
		if(trace.getDebugCode("tsltstp"))
			trace.outNT("tsltstp", guid+" "+msg);
		SocketReader.sendString(msg, out, eom);
	}

	public void updateTimeStamp(String guid) {
		// TODO Auto-generated method stub
		
	}

	public Single createTransactionInfo(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateTransactionInfo(String sessionId, Object info) {
		// TODO Auto-generated method stub
		
	}

	public NtpClient getNtpClient() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** List of session ids in the current collaboration. */
	private String[] collabSessionIDs = new String[0];

	/** Unique identifier for collaboration. Value is timestamp when collaboration began. */
	private String collabId = "";

	/** Listeners for collaboration state changes. */
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();

	/**
	 * Create a Collaborators instance with manufactured userids. First calls
	 * {@link #stopAuthorTimeCollaboration()} to end any prior session. 
	 * @param teamSize number of userids to create.
	 */
	public void startAuthorTimeCollaboration(int teamSize) {
		if(trace.getDebugCode("collab"))
			trace.out("collab", "AuthorLauncherServer.startAuthorTimeCollaboration("+teamSize+")");
		stopAuthorTimeCollaboration(true);
		if(teamSize < 2)
			return;
		collabId = Collaborators.dateFmt.format(new Date());  // unique identifier
		collabSessionIDs = new String[teamSize];
		fireChangeEvent(this);
	}

	/**
	 * Empties {@link #allCollaborators}.
	 */
	public void stopAuthorTimeCollaboration() {
		stopAuthorTimeCollaboration(false);
	}

	/**
	 * Empties {@link #allCollaborators}.
	 * @param suppressEvent if true, don't call {@link #fireChangeEvent(Object)}
	 */
	private void stopAuthorTimeCollaboration(boolean suppressEvent) {
		for(String sessionID : collabSessionIDs) {
			if(sessionID != null)
				endCollaboration(sessionID);
		}
		collabSessionIDs = new String[0];
		collabId = "";
		if(!suppressEvent)
			fireChangeEvent(this);
	}

	/**
	 * Alter a {@value MsgType#SET_PREFERENCES} message as needed for collaboration.
	 * If changed, record the changes in the session.
	 * @param setPrefs message to modify
	 * @param sessionID a session id processed by {@link #editGuidForCollaboration(String)}
	 * @return setPrefs message, edited
	 */
	public MessageObject editSetPreferences(MessageObject setPrefs, String sessionID) {
		if(Collaborators.editSetPreferences(setPrefs, collabId, collabSessionIDs.length, sessionID)) {
			Session session = getSession(sessionID);
			session.setUserGuid((String) setPrefs.getProperty(Logger.STUDENT_NAME_PROPERTY));
			session.setSchoolName((String) setPrefs.getProperty(Logger.SCHOOL_NAME_PROPERTY));
			session.setTeam(Collaborators.getUserids(setPrefs, null).toString());
		}
		return setPrefs;
	}

	/**
	 * Provide the count of collaborators connected so far together with the team size.
	 * @return 2-element array with no. of collaborators connected so far in array[0]
	 *         and team size in array[1]
	 */
	public int[] getCollaborationCounts() {
		int i;
		for(i = 0; i < collabSessionIDs.length && collabSessionIDs[i] != null; ++i);
		int[] result = { i, collabSessionIDs.length };
		return result;
	}

	/**
	 * Add collaboration information to a session id when author time collaboration is in use.
	 * Also sets entries in {@link #collabSessionIDs}.
	 * @param guid string to edit
	 * @return altered string
	 */
	public String editGuidForCollaboration(String guid) {
		int i;
		for(i = 0; i < collabSessionIDs.length && collabSessionIDs[i] != null; ++i);
		if(i >= collabSessionIDs.length)
			return guid;
		collabSessionIDs[i] =
				Collaborators.editSessionIDForCollaboration(guid, collabId, i+1, collabSessionIDs.length);
		fireChangeEvent(this);
		return collabSessionIDs[i];
	}
    
    /**
     * The listener list is maintained here, not in {@link UniversalToolProxy},
	 * because instances of that class may not survive as
	 * the communications link to the student interface is broken and reconnected.
     * @param listener listener to add to {@link #changeListeners}
     */
    public void addChangeListener(ChangeListener listener) {
    	changeListeners.add(listener);
    }
    
    /**
     * @param listener listener to remove from {@link #changeListeners}
     */
    public void removeChangeListener(ChangeListener listener) {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "BR_Controller.removeChangeListener("+trace.nh(listener)+")");
    	changeListeners.remove(listener);
    }

	/**
	 * Send a {@link ChangeEvent} to all {@link #changeListeners}.
	 * @param source for {@link ChangeEvent#getSource()} 
	 */
    public void fireChangeEvent(Object source) {
    	ChangeEvent ce = new ChangeEvent(source);
    	for (ChangeListener listener : changeListeners) {
    		listener.stateChanged(ce);
    	}
	}
}
