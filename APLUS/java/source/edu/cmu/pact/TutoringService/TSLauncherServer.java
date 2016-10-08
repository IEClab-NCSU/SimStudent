/*
 * Copyright 2011 Carnegie Mellon University.
 */

package edu.cmu.pact.TutoringService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import cl.launcher.Session;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Properties;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.SocketProxy.LogServlet;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.SocketProxy.SocketToolProxy;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.CtatLMSClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctatview.CtatFrameController;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pact.miss.MissControllerStub;
import edu.cmu.pact.miss.console.controller.MissController;
import edu.cmu.pslc.logging.LogContext;

/**
 * This interface abstracts methods of the Launcher Server needed by lower-level classes to support
 * variants in the server and client environments.
 */
public abstract class TSLauncherServer {

	/** Collaborators. */
	protected Collaborators.All allCollaborators = new Collaborators.All();

	/** The rules or skills available for attachment to links. */
    private CTAT_Properties properties;
	private final Random gen = new Random(System.currentTimeMillis());
    private LogContext logger;
    
  	/** Object to log author events. */
  	private EventLogger eventLogger;


	/** Access to the preferences, for {@link #getPreferencesModel()}. */
	private PreferencesModel preferencesModel;
	
	private void initLogging() {
		this.logger = new LoggingSupport(this);
		eventLogger = new EventLogger(getLogger());
        getProperties().addPropertyChangeListener(getLogger());
	}

	/**
	 * Load the preferences file if necessary and maintain a reference to the model.  
	 * @return reference to the {@link PreferencesModel}
	 */
	public PreferencesModel getPreferencesModel() 
	{
        if (preferencesModel == null) {
            preferencesModel = new PreferencesModel();
            preferencesModel.setPreventSaves(Utils.isRuntime());
        }
        return preferencesModel;
    }
	
	public void setLogger(LogContext lc){
		this.logger = lc;
	}
    
    public LoggingSupport getLoggingSupport(){
    	return (LoggingSupport)logger;
    }
    
    public LoggingSupport getLogger(){
    	return getLoggingSupport();
    }
    
    public CTAT_Properties getProperties() {
    	return this.properties;
    }
	
	public Random getRandomGenerator() {
		return this.gen;
	}

	public EventLogger getEventLogger() {
		return eventLogger;
	}

	public void setEventLogger(EventLogger eventLogger) {
		this.eventLogger = eventLogger;
	}

	/**
	 * Access to lower-level routines for housekeeping on the sessions table.
	 * @param guid session identifier
	 * @return true if session was found (and removed); false if not found
	 */
	public abstract boolean removeSession(String guid);
	
	/**
	 * Set a new value in the session inactivity timer.
	 * @param guid session identifier
	 */
	public abstract void updateTimeStamp(String guid);

	/**
	 * Create an object to record current transaction info.
	 * @param sessionId session identifier
	 * @return object containing transaction info
	 */
	public abstract TransactionInfo.Single createTransactionInfo(String sessionId);

	/**
	 * Update current transaction info.
	 * @param sessionId session identifier
	 * @param info
	 */
	public abstract void updateTransactionInfo(String sessionId, Object info);

	/**
	 * @return the {@link NtpClient} instance shared by all sessions
	 */
	public abstract NtpClient getNtpClient();

	/**
	 * No-op in this superclass.
	 * @param teamSize
	 */
	public void startAuthorTimeCollaboration(int teamSize) {}

	/**
	 * No-op in this superclass.
	 */
	public void stopAuthorTimeCollaboration() {}

	/**
	 * No-op in this superclass.
	 * @param guid string to edit
	 * @return altered string
	 */
	public String editGuidForCollaboration(String guid) {
		return guid;
	}

	/**
	 * Possibly set up collaboration between this instance and others in the same 
	 * Tutoring Service.
	 * @param sessionId to find our {@link LauncherServer.Session}
	 * @param setPrefs {@value MsgType#SET_PREFERENCES} message with all parameters
	 * @see edu.cmu.pact.TutoringService.TSLauncherServer#checkForCollaborators(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * @return shared {@link Collaborators} or null if not collaborating 
	 */
	public Collaborators checkForCollaborators(String sessionId, MessageObject setPrefs)
			throws Collaborators.NotReadyException {
		Session s = getSession(sessionId);
		if(s == null)
			return null;
		Collaborators result = Collaborators.create(s, setPrefs);
		return result;
	}
	
	/**
	 * If the session identified by the guid is in a collaborating group, then
	 * queue the message to all sessions in the group.
	 * @param guid
	 * @param mo
	 * @return number of collaborator 
	 */
	public int enqueueToCollaborators(String guid, MessageObject mo) {
		if(trace.getDebugCode("collab"))
			trace.out("collab", "enqueueToCollab("+guid+", "+mo.summary()+") gets session "+getSession(guid));
		if(!Collaborators.isMsgToBeShared(mo))
			return 0;                            // means "don't share"
		Session s = getSession(guid);
		if(s == null)
			return -1;
		return allCollaborators.enqueueToCollaborators(s, mo);
	}

	/**
	 * Remove this session from a {@link Collaborators} instance.
	 * @param sessionId session's guid
	 */	
	public void endCollaboration(String sessionId) {
		Session sess = getSession(sessionId);
		if(sess == null)
			return;
		Collaborators.remove(sess);
	}
	
	/**
	 * @return the {@link #allCollaborators}
	 */
	Collaborators.All getAllCollaborators() {
		return allCollaborators;
	}

	/**
	 * @return current Collaborators set, if any
	 */	
	public Collaborators.Collaborator findCollaborator(String sessionId) {
		Session sess = getSession(sessionId);
		if(sess == null)
			return null;
		return getAllCollaborators().findCollaborator(sess);
	}

	/**
	 * Alter a {@value MsgType#SET_PREFERENCES} message as needed for collaboration.
	 * No-op in this superclass.
	 * @param setPrefs
	 * @param sessionID 
	 * @return setPrefs message, unaltered
	 */
	public MessageObject editSetPreferences(MessageObject setPrefs, String sessionID) {
		return setPrefs;
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	/*
	 * Moved Session subclass from LauncherServer here to TSLauncherServer
	 */
	//////////////////////////////////////////////////////////////////////////////////

	/**
	 * Represents a user session.
	 * @author johnl2
	 */
	public class Session extends Thread
	{
	    protected int serverPort;
		/**
		 * @return the {@link #serverPort}
		 */
		public int getServerPort() {
			return serverPort;
		}

		/**
		 * @param serverPort new value for {@link #serverPort}
		 */
		public void setServerPort(int serverPort) {
			this.serverPort = serverPort;
		}

		protected Socket lSock;		
		protected MessageObject setPreferencesMsg = null;
		protected String brdFile;
		protected String guid;
		protected Date timeStamp;
		protected String ipAddr;
		protected SingleSessionLauncher launcher;
		protected BR_Controller controller;

		/** Information for the monitor. */
		protected TransactionInfo txInfo = new TransactionInfo();
		
		/** Information for the monitor. */
		protected TSLogInfo.Session logInfo;
		
		protected SocketProxy socketProxy;
		protected String userGuid;
		protected String schoolName;
		protected String team;

		/**
		 * Constructor for unit tests.
		 * @param guid
		 */
		public Session(String guid, String userGuid) {
			this.guid = guid;
			this.userGuid = userGuid;

			//2014/5/29 copied from old LauncherServer constructor
			if(TSLauncherServer.this.logInfo != null)
				this.logInfo = TSLauncherServer.this.logInfo.create();
		}

		/**
		 * Main constructor.
		 * @param sock socket to listen on
		 */
		Session (Socket sock)
		{		
			this.lSock = sock;
			if(TSLauncherServer.this.logInfo != null)
				this.logInfo = TSLauncherServer.this.logInfo.create();
		}
		
		/**
		 * @return the {@link #guid}
		 */
		String getGuid() {
			return guid;
		}
		
		/**
		 * @param new value for {@link #guid}
		 */
		void setGuid(String guid) {
			this.guid = guid;
		}

		/**
		 * Recover a session on a new socket.
		 * @param sock
		 * @param msgFormat
		 */
		private void connectSocket(Socket sock, int msgFormat, BufferedReader br)
		{
			SocketProxy sp = new SocketProxy(sock, msgFormat);
			controller.setRemoteProxy(sp);
			sp.setController(controller, br);
			setSocketProxyParameters(sp);
			sp.start();     // start the listener on this new socket
		} 

		/**
		 * Set the {@link SocketProxy} parameters needed for communicating with CTAT Flash tutors.
		 * @param sp
		 */
		private void setSocketProxyParameters(SocketProxy sp) {
			sp.setEom(0);
			sp.setUseSingleSocket(true);
			sp.setOneMsgPerSocket(false);
			sp.setMsgFormat(Integer.toString(SocketToolProxy.XMLCONVERTER_FMT));
			sp.setServerPort(serverPort);
		}
		protected void updateTimeStamp(){
			setTimeStamp(new Date());
		}

		/**
		 * One of {@link SocketToolProxy#XMLCONVERTER_FMT}, {@link SocketToolProxy#COMM_FMT}
		 * or {@link SocketToolProxy#OLI_XML_FMT}.
		 */
		protected int msgFormat = SocketToolProxy.XMLCONVERTER_FMT;
		
		/**
		 * @return the {@link #msgFormat}
		 */
		int getMsgFormat() {
			return msgFormat;
		}

		/**
		 * @return the {@link #setPreferencesMsg}
		 */
		public MessageObject getSetPreferencesMsg() {
			return setPreferencesMsg;
		}

		/**
		 * @param msg new value for {@link #setPreferencesMsg}
		 */
		void setSetPreferencesMsg(MessageObject msg) {
			if(!MsgType.SET_PREFERENCES.equalsIgnoreCase(msg.getMessageType()))
				throw new IllegalArgumentException("Session.setSetPreferencesMsg() Wrong message type "+
						msg.getMessageType()+";\n  text: "+msg.toElement());
			this.setPreferencesMsg = msg;
		}

		/**
		 * Run LauncherConsumerThread
		 * Get the GUID and setPrefs message from the socket.
		 */
		public void run()
		{
			BufferedReader br =  null;
			try {
				/* InterfaceIdentificationRequest */
				if (trace.getDebugCode("ls")) trace.outNT("ls", "Trying to get GUID");
				br = new BufferedReader(new InputStreamReader(lSock.getInputStream(), "UTF-8"));
				String result = SocketReader.readToEom(br, '\0');

				if (SocketProxy.handlePolicyFileRequest(result, lSock))
					return;

				if (LogServlet.handleLogRecord(result, lSock, logServlet))
					return;

				if (trace.getDebugCode("ls")) trace.outNT("ls", "LauncherServer.Session possible Guid: "+result);
				guid = MessageObject.getPropertyFromXML(result, "Guid");
				if (guid == null)
				{
					if (trace.getDebugCode("ls")) trace.outNT("ls", "LauncherServer.Session: Null guid, closing socket");
					close(br, lSock);
					return;
				}
				msgFormat = SocketToolProxy.deriveMsgFormat(result);

				Session oldSession = getSession(guid); 

				if (oldSession == null ||                // this is a new session: get SetPreferences
						oldSession.controller == null)   // or ctlr null: sewall 2013-10-16 load test 
				{
					setGuid(guid);

					result = SocketReader.readToEom(br, '\0');
					if (trace.getDebugCode("tsltsp"))
						 trace.outNT("tsltsp", ""+guid+" "+result);

					if (result.length() < 1) {
						if (trace.getDebugCode("ls"))
							trace.outNT("ls", "LauncherServer.Session: empty SetPreferences, closing socket " + result.length());
						close(br, lSock);
						return;
					}

					setPreferencesMsg = SocketProxy.convertMsg(result, msgFormat);
					String s = "(none)";
					if (setPreferencesMsg == null ||
							!MsgType.SET_PREFERENCES.equalsIgnoreCase(s=setPreferencesMsg.getMessageType())) {
						if (trace.getDebugCode("ls"))
							trace.outNT("ls", "Set pref msg turned out to be null or msg type "+s);
						close(br, lSock);
						return;
					}
					getSetPreferencesMsg().setTransactionInfo(txInfo.create());
					if(!findBRDFilename(getSetPreferencesMsg())) {
						close(br, lSock);
						return;
					}
				}

			} catch (Exception e) {
				trace.errStack("LauncherServer.Session.run() 1st half error, closing socket: ", e);
				if (trace.getDebugCode("ls"))
					trace.outNT("ls", "Exception in session.run : " + e);
				close(null, lSock);
				return;
			}
				
			try {
				// Add session to sessions HashMap
				Session oldSession = addSession(this);  // add or replace session in table

				if (oldSession == null || oldSession.controller == null)  // sewall 2013-10-16
				{  	
					setLoggingProperties(getSetPreferencesMsg());
					if (trace.getDebugCode("ls"))
						trace.out("ls", "LauncherServer: new session: guid "+guid+", school "+schoolName+
								", userGuid "+userGuid+"\n  BRD File = " + brdFile);

					setupController(br);

					setSocketProxy((SocketProxy) controller.getRemoteProxy());
					controller.getRemoteProxy().setupLogServlet(getSetPreferencesMsg());
					processSetPreferences();
					setTimeStamp(new Date());
				}
				else {
					trace.out("ls", "LauncherServer: session recovered: guid "+guid+
							", getUserGuid() "+getUserGuid());
					setTimeStamp(oldSession.getTimeStamp());
					setUserGuid(oldSession.getUserGuid());
					setTeam(oldSession.getTeam());
					setSchoolName(oldSession.getSchoolName());
					setLauncher(oldSession.getLauncher());
					controller = oldSession.controller;
					brdFile = oldSession.brdFile;
					setSetPreferencesMsg(oldSession.getSetPreferencesMsg());
					connectSocket(lSock, getMsgFormat(), br);
					controller.getRemoteProxy().setupLogServlet(getSetPreferencesMsg());
				}
				setIPAddr(lSock.getInetAddress().getHostAddress());
			} catch (Exception e) {
				trace.errStack("LauncherServer.Session.run() 2nd half error on guid "+guid+", closing socket", e);
				close(null, lSock);
				return;
			} 
		}

		protected void processSetPreferences() {
			if (getSetPreferencesMsg() != null && controller != null) {
				controller.handleCommMessage(getSetPreferencesMsg());
				getSetPreferencesMsg().getTransactionInfo().update(Boolean.TRUE);
			}
		}

		protected void setupController(BufferedReader br) {
			createLauncher(br);
			getLauncher().setSessionId(guid);
			controller = getLauncher().getController();
			trace.out("sp", "LauncherServer.Session.run() controller "+
					controller+", setPreferencesMsg "+getSetPreferencesMsg());
		}

		protected void createLauncher(BufferedReader br) {
			String argv[] = {"-Dguid=" + guid, "-DisOnline=true", "-Dschool_name=School1", "-Dcourse_name=\"Course1\"", "-DBehaviorRecorderMode=Example-tracing Tutor",  // removed "-Dcourse_name=\"Course1\"", "-Dunit_name=\"Unit1\"",
					"-DBehaviorRecorderVisible=false", "-spEOM", "00", "-spUseSingleSocket", "true", "-spOneMsgPerSocket", "false", "-spMsgFormat", String.valueOf(getMsgFormat()), "-spServerPort", Integer.toString(serverPort), "-debugCodes"};
			setLauncher(new SingleSessionLauncher(lSock, br, argv, false, TSLauncherServer.this, getSetPreferencesMsg(), null));
		}

		protected void setLoggingProperties(MessageObject setPrefsMsg) {
			setTeam(Collaborators.getUserids(setPrefsMsg, null).toString());
			setSchoolName((String) setPrefsMsg.getProperty(Logger.SCHOOL_NAME_PROPERTY));
			setUserGuid((String) setPrefsMsg.getProperty(Logger.STUDENT_NAME_PROPERTY));
		}

		/**
		 * Search for the BRD filename in {@link #getSetPreferencesMsg()}. Sets {@link #brdFile}.
		 * @param setPrefsMsg a {@value MsgType#SET_PREFERENCES} message
		 * @return true if found; else false (an error)
		 */
		protected boolean findBRDFilename(MessageObject setPrefsMsg) {
			String brdFileP =
					(String) setPrefsMsg.getProperty("ProblemName");					 
			String brdFileQ =
					(String) setPrefsMsg.getProperty(Logger.QUESTION_FILE_PROPERTY);					 
			brdFile = (brdFileP != null && brdFileP.length() > 0 ? brdFileP : brdFileQ);
			if (brdFile == null || brdFile.length() < 1) {
				if (trace.getDebugCode("ls"))
					trace.outNT("ls", "LauncherServer.Session: SetPreferences has no brdFile, exiting");
				return false;
			}
			return true;
		}

		/**
		 * @return the {@link #schoolName}
		 */
		public String getSchoolName() {
			return schoolName;
		}

		public void setSchoolName(String schoolName) {
			this.schoolName = (schoolName == null ? "" : schoolName);
		}

		/**
		 * @return the {@link #team}
		 */
		public String getTeam() {
			return team;
		}

		/**
		 * @param team new value for {@link #team}
		 */
		public void setTeam(String team) {
			this.team = team;
		}

		public void setUserGuid(String userGuid) {
			this.userGuid = (userGuid == null ? "" : userGuid);
		}

		/**
		 * @return {@link #userGuid}
		 */
		public String getUserGuid() {
			return userGuid;
		}

		/**
		 * @return {@link #socketProxy}
		 */
		SocketProxy getSocketProxy() {
			return socketProxy;
		}

		/**
		 * @param socketProxy new value for {@link #socketProxy}
		 */
		void setSocketProxy(SocketProxy socketProxy) {
			this.socketProxy = socketProxy;
		}

		/**
		 * @param timeStamp new value for {@link #timeStamp}
		 */
		public void setTimeStamp(Date timeStamp) {
			this.timeStamp = timeStamp;
		}

		/**
		 * @return the {@link #timeStamp}
		 */
		public Date getTimeStamp() {
			return timeStamp;
		}

		/**
		 * @return the {@link #iPAddr}
		 */
		public String getIPAddr() {
			return ipAddr;
		}

		/**
		 * @param ipAddr new value for {@link #ipAddr}
		 */
		void setIPAddr(String ipAddr) {
			this.ipAddr = ipAddr;
		}

		/**
		 * @return the {@link #txInfo}
		 */
		public TransactionInfo getTxInfo() {
			return txInfo;
		}

		public TSLogInfo.Session getLogInfo() {
			return logInfo;
		}

		/**
		 * @return the {@link #controller}
		 */
		public BR_Controller getController() {
			return controller;
		}

		public void setController(BR_Controller controller) {
			this.controller = controller;
		}
		
		/**
		 * @return "[{@link #guid}, {@link #userGuid}, 
		 * {@link #schoolName}, {@link #team}, {@link Thread#toString()}]"
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder("[guid=");
			sb.append(guid).append(", userguid=");
			sb.append(userGuid).append(", schoolName=");
			sb.append(schoolName).append(", getLogInfo=");
			sb.append(getLogInfo().toString()).append(", team=");
			sb.append(team).append(", super=");
			sb.append(super.toString()).append("]");
			return sb.toString();
		}

		/**
		 * @return {@link LauncherServer}.this
		 */
		TSLauncherServer getLauncherServer() {
			return TSLauncherServer.this;
		}

		void removeFromLauncherServer() {
			getLauncherServer().removeSession(guid);
		}

		/**
		 * @param launcher new value for {@link #launcher}
		 */
		protected void setLauncher(SingleSessionLauncher launcher) {
			this.launcher = launcher;
		}

		/**
		 * @return {@link #launcher}
		 */
		protected SingleSessionLauncher getLauncher() {
			return launcher;
		}
	}
	
	/**
	 * Close the given input stream and socket. Handles all exceptions
	 * @param br
	 * @param lSock
	 */
	private void close(Reader br, Socket lSock) {
		try {
			if (br != null)
				br.close(); 
		} catch (Exception e) {
			trace.err("LauncherServer.close(): Error closing socket stream: "+e+"; cause "+e.getCause()); 
		}
		try {
			if (lSock != null)
				lSock.close(); 
		} catch (Exception es) {
			trace.err("Error closing socket: "+es+"; cause "+es.getCause()); 
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	/**
	 * Attempt to move some functionality from LauncherServer here to TSLauncherServer.
	 * Mostly to do with allowing CTAT_Launcher to access logInfo instances
	 */
	//////////////////////////////////////////////////////////////////////////////////
	/** Information on HTTP clients' {@link #logServlet} usage for the monitor. */
	protected TSLogInfo.Session httpLogInfo;
	
	/** Log service instance used by HTTP clients outside of other sessions. */
	protected LogServlet logServlet;
	
	/** Aggregate logging information for the tutoring service. */
	protected TSLogInfo logInfo = new TSLogInfo();
	
	/** An object to synchronize on when manipulating the {@link #sessions} table. */
	protected String sessionsMutex = "sessionsMutex";
	
	/** The sessions table. */
	protected HashMap<String, Session> sessions = new HashMap<String, Session>();
	
	/**
	 * For simplicity, try to make this the only constructor.
	 */
	public TSLauncherServer() {
		//this part is copied from the above constructor
		//Subclasses now all call super();
		properties = new CTAT_Properties(this);
		initLogging();
		
		//added from LauncherServer
		PreferencesModel prefs = new PreferencesModel();
		prefs.setPreventSaves(true);
		logServlet = new LogServlet(prefs, true);      // single servlet for HTTP logging
		httpLogInfo = logInfo.create();
		logServlet.setLogInfo(httpLogInfo);
		
		if (trace.getDebugCode("ls")) trace.out("ls", "TS LS constructor completed");
	}
	
	/**
	 * @return the {@link #httpLogInfo}
	 */
	TSLogInfo.Session getHttpLogInfo() {
		return httpLogInfo;
	}
	
	/**
	 * Return a structure to record current logging info.
	 * @param sessionId session identifier
	 * @return record for log in
	 */
	public TSLogInfo.Session getLogInfo(String sessionId) {
		TSLogInfo.Session result = null;
		Session session = getSession(sessionId);
		
		if (trace.getDebugCode("logservice")) 
			trace.out("logservice", "ls.getLogInfo: session variable is " + session);
		
		if (session != null)
			result = session.getLogInfo();
		
		if (trace.getDebugCode("logservice")) trace.out("logservice", "ls.getLogInfo("+sessionId+")  returns "+result);
		
		return result;
	}
	
    /**
	 * @return the {@link #logInfo}
	 */
	TSLogInfo getSummaryLogInfo() {
		return logInfo;
	}
	
	/**
	 * @param guid session identifier
	 * @return session instance; caller should treat it as read-only
	 */
	public Session getSession(String guid) {
		synchronized(sessionsMutex){
			return sessions.get(guid);
		}
	}
	
	/**
	 * Create a new session with the given session id.
	 * @param guid value for session id
	 * @return new session id
	 */
	public String addNewSession(String guid) {
        Session session = new Session(guid, null);
        addSession(session);
        
        if(trace.getDebugCode("tab"))
        	trace.out("tab", "TSLauncherServer.addNewSession: guid = "+guid+
        			"\n    user_guid = "+null+"\n    session = "+session);
        
        return session.getGuid();
	}
	
	/**
	 * @param session add this to {@link #sessions}
	 */
	public Session addSession(Session session) {
		synchronized(sessionsMutex) {
			String key = session.getGuid();
			Session result = sessions.put(key, session);   // add or replace session in table
			if (trace.getDebugCode("ls"))
				trace.printStack("ls", "added session "+key+"; found prior entry "+result+
						"; sessions.size() now "+sessions.size());
			return result;
		}
	}
	
	/**
	 * @return the {@link #logServlet}
	 */
	LogServlet getLogServlet() {
		return logServlet;
	}
}
