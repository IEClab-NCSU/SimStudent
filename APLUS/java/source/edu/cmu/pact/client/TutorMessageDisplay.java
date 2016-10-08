package edu.cmu.pact.client;

import java.awt.Container;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import pact.CommWidgets.WrapperSupport;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintPanel;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.LogContext;
import edu.cmu.pslc.logging.Message;
import edu.cmu.pslc.logging.PlainMessage;
import edu.cmu.pslc.logging.ToolMessage;

/**
 * Convert different message formats to and from Comm.
 * 
 */
public class TutorMessageDisplay implements TutorAddon, MessageEventListener { //implements MessageEventListener
		
	/** Message processor for {@link hintWindow}. */
	private HintMessagesManagerForClient messagesManager;
	
	/** Panel with hint & done buttons, message display. */
	//try not to re-instantiate hint panels with networking ... see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6471418
	//you may get some infinite loop on a Java font method ...
	//see where HintPanel.fontSize's initializes
	private HintPanel hintPanel;

	/** Logger for message conversion. */
	private LogContext logger = null;
	
	/** Reference to communications layer. */
	MessageConnection msgConn = null;
	
	private TutorController tutorController;
	/** List of action names which we filter out */
	protected List<String> filter;
	
	/** For advancing problems */
	protected ProblemAdvance problemAdvance;
	
	public TutorMessageDisplay(LogContext logContext,Container frame, String host, int port) {
		this(logContext, frame, host, port, null);
	}
	
	/**
	 * Instantiate and link together the objects in the tutor engine's student interface.
	 * Set ProblemAdvance to null if you do not want to advance at all.
	 * For Tutorshop implementation use the default TutorshopAdvance class, with appropriate
	 * parameters set from client
	 */
	public TutorMessageDisplay(LogContext logContext, Container frame, String host, int port, ProblemAdvance advance) {
		tutorController = new TutorController(logContext, this);
		messagesManager = new HintMessagesManagerForClient(tutorController);
		messagesManager.setStudentInterfaceWrapper(this);
		hintPanel = new HintPanel(tutorController, messagesManager);
		messagesManager.setHintInterface(hintPanel);
		msgConn = new MessageConnection(host, port);
		this.logger = logContext;
		msgConn.addMessageEventListener(messagesManager);
		problemAdvance = advance;
		//problem advance, what should we do with it?LOJAS
		msgConn.addMessageEventListener(this);
		if(problemAdvance!=null){
			problemAdvance.setMessageConnection(msgConn);
			msgConn.addMessageEventListener(problemAdvance);
		}
		if (frame != null) {
			frame.add(hintPanel);
			frame.validate();
			frame.setVisible(true);
		}
		msgConn.openConnection();
		List<String> preferenceNames = new ArrayList(), preferenceValues = new ArrayList();
		ContextMessage cm = logContext.getContextMessage();
		//ClassElement ce  = cm.getClassElement();
		//preferenceNames.add("log_service_url");
		//preferenceValues.add(logContext.getLogServiceURL());
		
		preferenceNames.add("log_to_remote_server");
		//preferenceValues.add(logContext.isLogToRemoteServerTrue());
		preferenceValues.add("false");
		
		preferenceNames.add("log_to_disk");
		//preferenceValues.add(logContext.isLogToDiskTrue());
		preferenceValues.add("false");
		
		preferenceNames.add(Logger.STUDENT_NAME_PROPERTY);
		preferenceValues.add(logContext.getUserId());
		
		preferenceNames.add("problem_name");
		preferenceValues.add(logContext.getProblemName());

		preferenceNames.add("question_file");
		preferenceValues.add(logContext.getQuestionFile());
		
		preferenceNames.add(Logger.SESSION_ID_PROPERTY);
		preferenceValues.add(logContext.getSessionId());
		
		preferenceNames.add("ProblemName");
		preferenceValues.add(logContext.getQuestionFile());
		
		preferenceNames.add("problem_context");
		preferenceValues.add(logContext.getContextMessage().getNameAttribute());
		
		preferenceNames.add("context_message_id");
		preferenceValues.add(logContext.getContextMessage().getContextMessageId());
		//retain guid... or get it from constructor/parameters
		startTutoringServiceSession(preferenceNames, preferenceValues);
		//send the interface identification method.
		// session id from..
	}
	
	/**
	 * @return the {@link #msgConn}
	 */
	public MessageConnection getMessageConnection() {
		return msgConn;
	}
	
    /**
     * Request a hint from the tutoring system.
     * @see pact.CommWidgets.StudentInterfaceWrapper#requestHint()
     */
	public void requestHint() {
		ToolMessage msg = createHintRequest();
		if(logger != null)
			logger.logIt(msg, new Date());
		msgConn.unfilteredSendString(msg.toString(false));
	}
	
	/**
	 * Added to support general CommWidgets in a simulator, not
	 * just Hint and Done buttons.
	 * @param msg text to pass to {@link #msgConn}.sendString(String)
	 */
	public void sendString(String msg) {
		msgConn.unfilteredSendString(msg);
	}
	
	/**
	 * Send a LoadProblem message to BR_Controller
	 */
	public void requestLoadProblem(String filename) {
		//ToolMessage msg = ToolMessage.create(logger.getContextMessage());
		//ContextMessage s = ContextMessage.createStartProblem();
	}
	
	/**
	 * Request a done from the tutoring system.
	 */
	public void requestDone() {
		ToolMessage msg = createDoneRequest();
		if(logger != null)
			logger.logIt(msg, new Date());
		msgConn.unfilteredSendString(msg.toString(false));
	}
	
	/**
	 * Format a HINT_REQUEST message for the host. When available, the
	 * currently- or last-active UI component should be sent as the selection.
	 * @return request as formatted string.
	 */
	private ToolMessage createHintRequest() {
		if (logger == null)
			return null;
		ToolMessage msg = ToolMessage.create(logger.getContextMessage());
		msg.setAsHintRequest();
		msg.addSai("Help", "ButtonPressed", "-1");
		return msg;
	}
	
	/**
	 * Format a done message for the host.  See above
	 */
	private ToolMessage createDoneRequest() {
		if (logger == null)
			return null;
		ToolMessage msg = ToolMessage.create(logger.getContextMessage());
		msg.setAsAttempt();
		msg.addSai("done", "ButtonPressed", "-1");
		return msg;
	}
	
	/**
	 * Generate a SetPreferences messages. The arguments are 2 parallel lists
	 * of names and values with a 1:1 relationship between them.
	 * @param preferences names
	 * @param values value for each preference
	 * @return message created, as a String
	 */
	String createSetPreferenceRequest(List preferences, List values) {
		if (logger == null)
			return null;
		PlainMessage msg = PlainMessage.create(logger.getContextMessage());
		msg.addProperty("MessageType", "SetPreferences");
		for(int i = 0; i < preferences.size(); i ++)
			msg.addProperty(preferences.get(i).toString(), values.get(i).toString());
		return msg.toString();
	}
	
	/**
	 * Send an Interface Identification message. This is the first message from a client
	 * to the tutoring service, to create and identify a session.
	 * @param session guid to use; if null create a new one
	 * @return the session guid
	 */
	String sendInterfaceIdentification(String sessionGuid) {
		if (sessionGuid == null)
			sessionGuid = (new java.rmi.server.UID()).toString();
		String msg = "<message>"+
		  "<verb>NotePropertySet</verb>"+
		   "<properties>"+
		    "<MessageType>InterfaceIdentification</MessageType>"+
		    "<Guid>"+sessionGuid+"</Guid>"+
		   "</properties>"+
		  "</message>";
		sendString(msg);
		return sessionGuid;
	}
	
	/**
	 * Start a session with the tutoring service.
	 * @param preference names
	 * @param preference values
	 * @return sessionGuid
	 */
	public String startTutoringServiceSession(List preferenceNames, List preferenceValues) {
		String sessionGuid = sendInterfaceIdentification(null);
		String setPrefsMsg = createSetPreferenceRequest(preferenceNames, preferenceValues);
		sendString(setPrefsMsg);
		return sessionGuid;
	}
	
	/**
	 * @see edu.cmu.pact.client.TutorAddon#getProblemAdvance()
	 */
	public ProblemAdvance getProblemAdvance()
	{
		return problemAdvance;
	}
	
	/**
	 * For stand-alone execution, independent of a simulator.
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("TutorMessageDisplay");
		String host = null;
		int port = MessageConnection.DEFAULT_PORT;
		int i = 0;
		for (; i < args.length; ++i) {
			switch(i) {
			case 0:
				host = args[i]; break;
			case 1:
				port = Integer.parseInt(args[i]); break;
			}
		}
		//I am commenting this out since the main method here is somewhat sketchy and out-of-sync
		//TMD is designed to work with DataShopTracer, but might not be very haapp with an actual logger.
		//new TutorMessageDisplay(new Logger(null), frame.getContentPane(), host, port);
		frame.validate();
		frame.setVisible(true);
	}

	/**
	 * @return the {@link #logger if it refers to an instance of a Logger}
	 */
	public LogContext getLogger() {
		return logger;
	} 

	public void enableLMSLogin(boolean loginEnabled) {
		// TODO Auto-generated method stub
		
	}

	public JFrame getActiveWindow() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHeight() {
		return (hintPanel != null ? hintPanel.getHeight() : 0);
	}

	public HintWindowInterface getHintInterface() {
		return hintPanel;
	}

	public JComponent getTutorPanel() {
		return hintPanel;
	}
	
	public void reset()
	{
		hintPanel.reset();
	}

	public int getWidth() {
		return (hintPanel != null ? hintPanel.getWidth() : 0);
	}

	public WrapperSupport getWrapperSupport() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setLocation(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public CTAT_Options setTutorPanel(JComponent tutorPanel) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	public void showAdvanceProblemMenuItem() {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void addMessageEventListener(MessageEventListener me) {
		if(msgConn != null)
			msgConn.addMessageEventListener(me);
	}

	public void openConnection() {
		if(msgConn != null)
			msgConn.openConnection();
	}

	public void closeConnection() {
		if(msgConn != null)
			msgConn.stopListener();
	}
	
	public void removeMessageEventListener(MessageEventListener me) {
		if(msgConn != null)
			msgConn.addMessageEventListener(me);
	}

	public boolean filter(String str) {
		if(filter != null)
		{
			//there's probably a more elegant way of doing this
			int actionStart = str.indexOf("<action>"), actionEnd = str.indexOf("</action>");
			if(actionStart >= 0 && actionEnd > actionStart)
			{
				String action = str.substring(actionStart + "<action>".length(), actionEnd);
				for(String filtered : filter)
					if(filtered.equals(action))
						return false;
			}
		}
		return true;
	}

	public void setFilter(List<String> filter) {
		this.filter = filter;
	}

	/**
	 * Log the message event received.
	 * @param me
	 * @see edu.cmu.pact.Utilities.MessageEventListener#messageEventOccurred(edu.cmu.pact.Utilities.MessageEvent)
	 */
	public void messageEventOccurred(MessageEvent me) {
		if (logger == null)
			return;
		Object msg = me.getMessage();
		if (msg instanceof Message)
			logger.logIt((Message) msg, new Date());
		else if (msg instanceof String)
			logger.logIt((String) msg, new Date());
	}
}
