package pact.CommWidgets;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTab;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.Hints;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionComparator;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.model.CtatListModel;
import edu.cmu.pact.ctat.model.StartStateModel;

public abstract class RemoteToolProxy extends UniversalToolProxy {

	/** Constant meaning that messages should be skipped. */
	public static final int NO_FMT = -1;

	/** Constant meaning that messages should remain in Comm format.
	This is currently the default. */
	public static final int COMM_FMT = 0;

	/** Constant meaning that messages should be formatted by
		{@link #XMLConverter}. */
	public static final int XMLCONVERTER_FMT = 1;

	/** Constant meaning that messages should be formatted by
		{@link #OLIMessageObject}. */
	public static final int OLI_XML_FMT = 2;

	/** Action names for {@link #getFlashActionNames()}. */
	private static List<String> FlashActionNames = Arrays.asList(new String[] {
			"UpdateTextArea",         "showDone",               "ChangeHorizontalUnit",
			"UpdateComboBox",         "OkayPressed",            "ChangeVerticalLabel",         
			"UpdateTextField",        "ComponentReset",         "ChangeVerticalUnit",          
			"goToStep",               "SpecifiedAngleSet",      "setFinalPie",                 
			"ButtonPressed",          "playClip",               "hideAllBars",                 
			"showStep",               "Solver_startProblem",    "solverMethod",                
			"WasJustHitByA",          "UpdateText",             "ChangeHorizontalInterval",    
			"UpdateRadioButton",      "FractionBarBlockDrop", // "ChangeLowerHorizontalBoundary", these 
			"UpdateCheckBox",         "grapherPointAdded",    // "ChangeLowerVerticalBoundary",    are
			"SetVisible",             "replaceAnswer",        // "ChangeUpperHorizontalBoundary",   too
			"Partition",              "addPointTPA",          // "ChangeUpperVerticalBoundary",      long
			"PieChartSubmit",         "processFormula",         "ChangeVerticalInterval",      
			"FractionBarSubmit",      "SetText",                "FractionBarBlockDrag",        
			"No_Action",              "highlightAnswer",        "grapherCurveAdded",           
			"SetPieces",              "assignImageURL",         "littleTickmarks",             
			"PieSliceDrop",           "StopPointAddIntent",     "pieReset",                    
			"IndicatePointAddIntent", "ShowAllBars",            "setNumberLine",               
			"GotoAndStop",            "ChangeHorizontalLabel",  "PieSliceDrag",                
			"showAdviceQ"
	});
	
	/** Selection names for {@link #getFlashFixedSelectionNames()}. */
	private static List<String> FlashFixedSelectionNames = Arrays.asList(new String[] {
			"_root"
	});
	
	static {
		Collections.sort(FlashActionNames, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(FlashFixedSelectionNames, String.CASE_INSENSITIVE_ORDER);
	}

	/** Format for messages to send. One of {@link #XMLCONVERTER_FMT},
        {@link #OLI_XML_FMT} or {@link #COMM_FMT}, the default. */
	private int format = COMM_FMT;
	
	/** Message bundles currently in progress.*/
	private Map<String, StringBuffer> bundles = new HashMap<String, StringBuffer>();
	
	/** Whether the .brd should hold all {@value MsgType#INTERFACE_DESCRIPTION} messages. Default false. */
	private boolean storeAllInterfaceDescriptions = false;
	
	/** Set of threads awaiting responses. See {@link #prepareForDisconnect()}. */
	protected Set<Thread> waitingThreads = new LinkedHashSet<Thread>();
	
	/** Message types that should be sent even when restoring the problem state. */
	private static final Set<String> msgTypesToSendWhileRestoring = new HashSet<String>(); 
	static {
		msgTypesToSendWhileRestoring.add(MsgType.VERSION_INFO.toLowerCase());
		msgTypesToSendWhileRestoring.add(MsgType.PROBLEM_RESTORE_END.toLowerCase());
	}

	/**
	 * @return return type Object because it might not always be a {@link Socket}. 
	 */
	protected abstract Object getSocket();

	/**
	 * Decide whether a message needs to be sent and generate the actual String if so.
	 * @param o source message 
	 * @return string to send
	 */
	protected String createMessageString(MessageObject o) {
		if (!allowWhileRestoringProblemState(o))
			return null;
		if (suppressFeedback(o) == FeedbackEnum.HIDE_ALL_FEEDBACK)
			return null;
		if (trace.getDebugCode("mo")) trace.out("mo", "STP.handleMessage tid="+o.getTransactionId()+"\n"+o);
		return o.toString();
	}
	
	protected abstract void sendXMLString(String string);
	
	/**
	 * Updates the saiTable with the most recent InterfaceActionSAI if in authoring mode
	 * @param msg
	 */
	private void updateSAITable(MessageObject msg) {
		BR_Controller ctlr = getController();
		if (ctlr == null || Utils.isRuntime())
			return;
		if (!"InterfaceAction".equalsIgnoreCase(msg.getMessageType()))
			return;
		Object s = msg.getProperty("Selection");
		Object a = msg.getProperty("Action");
		Object i = msg.getProperty("Input");
		ctlr.updateSAITable((List) (s instanceof List ? s : null),
				(List) (a instanceof List ? a : null),
				(List) (s instanceof List ? i : null),
				Hints.isHintSelection(s) ? "Hint Request" : "Tutored");
	}
	
	/**
	 * Prefix an XML prologue <tt>&lt;<?xml version="1.0" encoding="UTF-8"?&gt;</tt>
	 * if it is not already present.
	 * @param str string to modify
	 * @return prefixed string; str unchanged if null or begins with "&lt;?"
	 */
	public static String insertXMLPrologue(String str) {
		if (str == null || str.startsWith("<?"))
			return str;
		StringBuilder sb = new StringBuilder(str);
		sb.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		return sb.toString();
	}
	
	/** 
	 * Create and send an interface force quit to explicitly tell
	 * the interface we are shutting down.
	 * @param controller: use this controller's UTP
	 */
	public static void sendInterfaceForceDisconnectMsg(BR_Controller controller){
		if(controller==null)
			return;
		MessageObject mo = createInterfaceForceDisconnectMsg(controller.getSemanticEventId());
    	controller.handleMessageUTP(mo);
	}

	/**
	 * Create an interface force disconnect message to explicitly tell
	 * the interface to shut down.
	 * @param transactionId argument for {@link MessageObject#setTransactionId(String)}
	 * @return message
	 */
    public static MessageObject createInterfaceForceDisconnectMsg(String transactionId) {
    	MessageObject mo = MessageObject.create(MsgType.INTERFACE_FORCE_DISCONNECT);
		mo.setVerb("Disconnect");
		mo.setTransactionId(transactionId);
		return mo;
	}
	
	/**
	 * Test
	 * {@link #getController()}.{@link BR_Controller#isRestoringProblemState(String) isRestoringProblemState()}} 
	 * and the message type to see whether we should send this message.
	 * @param mo message to test
	 * @return true if message should be sent; else false
	 */
	protected boolean allowWhileRestoringProblemState(MessageObject mo) {
		String msgType = mo.getMessageType();
		BR_Controller controller = null;
		if (msgType != null && msgTypesToSendWhileRestoring.contains(msgType.toLowerCase()))
			return true;
		if ((controller = getController()) == null)
			return true;
		if (controller.isRestoringProblemState(mo.getTransactionId()))
			return false;         // prevent: now amid restoring
		return true;              // allow because not restoring
	}
	
	/**
	 * Label for student interface connection status.
	 * @return "Flash"
	 */
	public String getStudentInterfacePlatform() {
		return FLASH;
	} //endMsgType.equalsIgnoreCase(o.getMessageType())
	
	/**
     * Subclasses may override this method to bundle messages for performance.
     * When called with a message whose MessageType matches endMsgType, the
     * subclass should include that message as the last and then flush the bundle. 
     * @param o message to send or bundle for sending later
     * @param bundleName a label for the bundle
     * @param endMsgType the MessageType value that signals the end of the bundle
     */
	public void bundleMessage(MessageObject o, String bundleName, String endMsgType) {
		bundleMessage(o, bundleName, endMsgType.equalsIgnoreCase(o.getMessageType()));
	}
	
	/**
     * Subclasses may override this method to bundle messages for performance.
     * When called with a message whose MessageType matches endMsgType, the
     * subclass should include that message as the last and then flush the bundle. 
     * @param o message to send or bundle for sending later
     * @param bundleName a label for the bundle
     * @param flush true if this message is the last in the bundle--triggers forwarding
     */
	public void bundleMessage(MessageObject o, String bundleName, boolean flush) {

		boolean suppress = suppressMsgFromInterface(o);
		if (trace.getDebugCode("msg"))
			trace.out("msg", String.format("RTP.bundleMessage(%-20.35s..., %s, %b) suppress %b",
					o, bundleName, flush, suppress));
		if(suppress)
			return;
		
		String toSend = createMessageString(o);
		if (trace.getDebugCode("msg")) trace.out("msg", "RTP.bundleMessage toSend\n"+toSend);
		if (toSend == null)
			return;
		StringBuffer bundle = bundles.get(bundleName);
		if (bundle != null) {                          // bundle already in progress
			bundle.append(toSend);
			if (flush) {
				bundle.append("</").append(bundleName).append(">");
				sendXMLString(bundle.toString());
				bundles.remove(bundleName);
			}
		} else if(flush) {                             // single msg to be sent alone
			sendXMLString(toSend);
		}else {                                        // start a new bundle
			bundle = new StringBuffer();
			bundle.append("<").append(bundleName).append(">");
			bundle.append(toSend);
			bundles.put(bundleName, bundle);
		}
	}
	
	/**
	 * Tool-specific processing for a Comm message from the tutor. Message will be delayed if
	 * {@link #processUpdateAfterDelay(MessageObject)} returns true, but this method nevertheless
	 * returns without blocking.
	 * @param o message to send
	 * @see pact.CommWidgets.UniversalToolProxy#handleMessage(edu.cmu.pact.ctat.MessageObject)
	 */
	public void handleMessageByPlatform(MessageObject o) {
		
		if(checkMessage(o)){
			return;
		}

		String toSend = createMessageString(o);
		if (toSend == null)
			return;
		sendXMLString(toSend);
	}

	/**
	 * Map to record UI versions for which some messages can be skipped. See
	 * {@link #suppressMsgFromInterface(MessageObject)}. Key is message type in lower case;
	 * value is minimum UI version for which this message doesn't need to be sent.
	 */
	private static final Map<String, String> omitMsgIfUIRecent = new LinkedHashMap<String, String>();
	static {
		String[][] typesToOmit = {
			{ MsgType.START_PROBLEM.toLowerCase(), "3.3" }
		};
		int i = 0;     // for trace
		for(String[] typeVersion : typesToOmit) {
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("omitMsgIfUIRecent[%2d] %-20s => %s\n",
						i++, typeVersion[0], typeVersion[1]));
			omitMsgIfUIRecent.put(typeVersion[0], typeVersion[1]);
		}
	}

	/**
	 * Test whether a message should be suppressed from the user interface. The map
	 * {@link #omitMsgIfUIRecent} gives the minimum {@link BR_Controller#getCommShellVersion()}
	 * value for which the given message (using its message type) should be omitted.
	 * @param o message to test
	 * @return true if message should be <i>omitted</i> from those sent to the user interface
	 */
	protected boolean suppressMsgFromInterface(MessageObject o) {
		String msgType = o.getMessageType();
		if(msgType == null) {
			trace.err("RTP.suppressMsgFromInterface(): no MessageType in message.\n  "+o);
			return false;  // even though this message is nonsense
		}
		String omitIfVersion = omitMsgIfUIRecent.get(msgType.toLowerCase());  // keys all in lower case
		if(omitIfVersion == null)
			return false;
		String uiVersion = getController().getCommShellVersion();
		if(uiVersion == null)
			return false;
		boolean result = (VersionComparator.vc.compare(omitIfVersion, uiVersion) <= 0);
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "*! RTP.suppressMsgFromInterface(): to "+(result ? "suppress" : "send")+
					" "+msgType);
		return result;
	}

	/**
	 * Set the format for messages to send.
	 *
	 * @param  format one of {@link #XMLCONVERTER_FMT}, {@link #OLI_XML_FMT}
	 *            or {@link #COMM_FMT}, the default
	 */
	public void setFormat(int format) {
		if (format == OLI_XML_FMT || format == XMLCONVERTER_FMT)
			this.format = format;
		else
			this.format = COMM_FMT;
	}
	
	/**
	 * Get the format for messages to send.
	 *
	 * @return value of {@link #format}
	 */
	public int getFormat(){
		return format;
	}

	/**
	 * Send a {@value MsgType#GET_ALL_INTERFACE_DESCRIPTIONS} message to the interface asynchronously.
	 * Note that this method returns immediately; the response has not yet been received. 
	 * @return false if no interface is connected; else true
	 * @see pact.CommWidgets.UniversalToolProxy#getAllInterfaceDescriptions()
	 */
	public boolean getAllInterfaceDescriptions() {
		if(!getStudentInterfaceConnectionStatus().isConnected())
			return false;
		if(getController().clientSupports(MsgType.StartStateMessages))
			getController().redirectNextBundle(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS);  // FIXME
		if(getController().clientSupports(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS))
			handleMessage(MessageObject.create(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS));
		else {  // try both of these
			handleMessage(MessageObject.create(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS));
			handleMessage(MessageObject.create(MsgType.START_PROBLEM));
		}
		return true;
	}

	/**
	 * Interrupts any waitingThreads() and clears the set.
	 */
	public void prepareForDisconnect() {
		synchronized(waitingThreads) {
			for(Thread t : waitingThreads)
				t.interrupt();
			waitingThreads.clear(); // FIXME not needed if done in finally{ } block in retrieveInterfaceDescriptions() 
		}
	}

	/**
	 * List returned here was derived from the Action names found most often in the IES/FlashTutors
	 * directory in SVN as of 2014-03-11.
	 * @return sorted list of all action names
	 */
	public static List<String> getFlashActionNames() {
		return FlashActionNames ;
	}

	/**
	 * @return {@link #getFlashActionNames()}
	 */
	public List<String> listActionNames() {
		return getFlashActionNames();
	}

	/**
	 * @return {@link #getFlashFixedSelectionNames()}
	 */
	protected List<String> listFixedSelectionNames() {
		return getFlashFixedSelectionNames();
	}

	/**
	 * @return {@link #FlashFixedSelectionNames}
	 */
	public static List<String> getFlashFixedSelectionNames() {
		return FlashFixedSelectionNames;
	}

	/**
	 * Populate the given list model with the selection names. 
	 * @param listModel
	 */
	public void listSelectionNames(CtatListModel listModel) {
		if(getStartStateModel().nInterfaceDescriptionsFromInterface() > 0)
			listModel.addAll(getStartStateModel().getComponentNames());
		else {
			addStartStateListener(listModel);
			getController().redirectNextBundle(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS);  // FIXME
			handleMessage(MessageObject.create(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS));
	    	if(trace.getDebugCode("editstudentinput"))
	    		trace.printStack("editstudentinput", "RTP.listSelectionNames("+trace.nh(listModel)
	    				+") n listeners "+startStateListeners.size());
		}
	}

	/**
	 * @param msgType {@link MessageObject#getMessageType()} value to test
	 * @param commShellVersion determines whether client is new enough to support msgType
	 * @return {@link Boolean#TRUE} if supported; else {@link Boolean#FALSE}
	 * @see UniversalToolProxy#clientSupports(String, String)
	 */
	public Boolean clientSupports(String msgType, String commShellVersion) {
		boolean result;
		if(MsgType.StartStateMessages.equalsIgnoreCase(msgType))
			result = (commShellVersion != null);  // any client with a version can bundle the start state
		else if(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS.equalsIgnoreCase(msgType))
			result = (VersionComparator.vc.compare("3.3", commShellVersion) <= 0);
		else if(MsgType.INTERFACE_REBOOT.equalsIgnoreCase(msgType))
			result = (VersionComparator.vc.compare("3.3", commShellVersion) <= 0);
		else
			result = true;
		if(trace.getDebugCode("msg"))
			trace.out("msg", "RTP.clientSupports("+msgType+", "+commShellVersion+") returns "+result);
		return (result ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * @return value of {@link #storeAllInterfaceDescriptions}
	 */
	public boolean getStoreAllInterfaceDescriptions() {
		return storeAllInterfaceDescriptions;
	}

	/**
	 * @param storeAllInterfaceDescriptions new value for {@link #storeAllInterfaceDescriptions}
	 */
	public void setStoreAllInterfaceDescriptions(boolean storeAllInterfaceDescriptions) {
		if(trace.getDebugCode("intdesc"))
			trace.printStack("intdesc", "PM.setStoreAllInterfaceDescriptions("+storeAllInterfaceDescriptions+")");
		this.storeAllInterfaceDescriptions = storeAllInterfaceDescriptions;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	/**
	 * These following variables and method were originally from SocketToolProxy.
	 * They have been moved up to RemoteToolProxy on 5/23/2014.
	 * This makes the teeSocket more applicable to any remote Java student interface.
	 */
	/////////////////////////////////////////////////////////////////////////////
	
	/** Output stream on {@link #teeSock}. */
	protected PrintWriter teeOut = null;

	/** Second output socket for sending msgs. */
	protected Socket teeSock = null;
	
	/**
	 * One of {@link #OLI_XML_FMT}, {@link #XMLCONVERTER_FMT}, etc., for
	 * formatting messages to the {@link #teeSock}.
	 */
	protected int teeFormat = NO_FMT;
	
	/** If nonnegative, listener will expect this character as an
	end-of-message delimiter. In this case, this class will not
	close the socket after each message. Otherwise it will send
    only 1 message per connection. */
	protected int eom = -1;

	/** Destination TCP port number for outbound connection. */
	protected int destPort = 1501;	//Port taken from SocketToolPort. Might be different for other interfaces

	/**
	 * Set the socket used for tee output. If argument differs from current
	 * value of {@link #teeSock}, closes existing socket first.
	 * If argument is not null, calls {@link #connect()} to ensure
	 * socket is connected. Also sets {@link #teeFormat}.
	 * @param teeSock new value for {@link #teeSock}
	 * @param teeFormat new value for {@link #teeFormat}
	 */
	public void setTeeSocket(Socket teeSock, int teeFormat) {
		if (trace.getDebugCode("sp")) trace.out("sp", "SocketToolProxy: old tee socket " + this.teeSock + ", new socket " + teeSock);
		if (teeOut != null) {
			teeOut.flush();             // flush output stream always
			teeOut.close();
		}
		teeOut = null;
		if (this.teeSock != teeSock) {     // disconnect only if changing socket
			try {
				if (this.teeSock != null)
					this.teeSock.close();
				this.teeSock = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.teeSock = teeSock;
		this.teeFormat = teeFormat;
		if (teeSock == null)
			return;
		try {
			getTeeSocket();            // reinitialize teeOut
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Chain of {@link #setTeeSocket(Socket teeSock,int teeFormat)} overloaded method. <p>
	 *  If method is not passed a teeFormat, teeFormat defaults to NO_FMT = -1.
	 * @param teeSock2
	 */
	public void setTeeSocket(Socket teeSock2) {
		setTeeSocket(teeSock2, this.teeFormat);
	}
	
	/**
	 * Get the socket for the tee connection. If {@link #teeSock} is not null
	 * and {@link #teeOut} is null, sets {@link #teeOut}.
	 * @return the {@link #teeSock}
	 */
	public Socket getTeeSocket() {
		try {
			if (teeOut == null && teeSock != null)
				teeOut = new PrintWriter(new OutputStreamWriter(teeSock.getOutputStream(), "UTF-8"));
		} catch (Exception e) {
			trace.err("error opening output stream for teeSock: "+e);
			e.printStackTrace();
		}
		return teeSock;
	}
	
	public void sendToTee(String str) {
		if (str == null)
			return;
		try {
			getTeeSocket();             // opens teeOut if socket non-null
			if (teeOut != null) {
				str = insertXMLPrologue(str);
				if (trace.getDebugCode("sp"))
					trace.out("sp", "SocketToolProxy: sendToTee:\n" + str);
				SocketReader.sendString(str, teeOut, eom);    // no-op if teeOut null
			}
		} catch(Exception e){
			trace.err("SocketToolProxy failed to connect outgoing socket to Tutor Interface: " + e.toString());
		}
	}
	
	/**
	 * Return the end-of-message character. See {@link #setEom(int)}:
	 * this value affects socket reuse.
	 *
	 * @return {@link #eom}; default value -1 means "no delimiter"
	 */
	public int getEom() {
		return eom;
	}
	
	/**
	 * Set the end-of-message character. If nonnegative, listener will
	 * expect this character as an end-of-message delimiter. In this
	 * case, this class will not close the socket after each
	 * message. Otherwise it will send only 1 message per connection.
	 *
	 * @param {@link #eom}; default value -1 means "no delimiter"
	 */
	public void setEom(int eom) {
		this.eom = eom;
	}
	
	public int getDestPort(){
		return destPort;
	}

	public void setDestPort(int newnum){
		destPort = newnum;
	}
}
