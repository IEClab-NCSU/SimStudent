/**
 * 
 */
package edu.cmu.pact.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.SinkLogger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.CommException;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.Skill;

/**
 * @author sewall
 *
 */
public class DataShopMessageObjectTest extends TestCase {

	/** Replaceable tag for problem context (absolute path to brd) in test data. */
	private static final String PROBLEM_CONTEXT_TAG = "_%_PROBLEM_CONTEXT_%_";

	/** Replaceable tag for ContextMessageId in test data. */
	private static final String CONTEXT_MESSAGE_ID_TAG = "_%_CONTEXT_MESSAGE_ID_%_"; 
	
	/** Replaceable tag for TransactionId in test data. */
	public static final String TRANSACTION_ID_TAG = "_%_TRANSACTION_ID_%_"; 
	
	/** Replaceable tag for TransactionId in test data. */
	public static final String EVENT_TIME = "_%_EVENT_TIME_%_";
	
	/** Needed for context_message. */
	private Logger logger;

	/** The ubiquitous controller. */
	private BR_Controller controller;

	/** Substitute logger to let us examine the log. */
	private SinkLogger sink;

	/** Positions of Comm property arguments to {@link #checkConvFromXML(String, String...)} */
	private static final String[][] ConvFromXMLPropertyNames = {
		{"MessageType"}, {"Selection"}, {"Action"}, {"Input"},
		{"Indicator"}, {"TutorAdvice", "HintsMessage"}, {"Skills"}
	};

    /**
     * Factory for native MessageObjects.
     * 
     * @param messageType
     * @param selection
     * @param action
     * @param input
     * @return new MessageObject with these properties
     */
    public static MessageObject getMessageObject(String messageType,
            String selection, String action, String input) {
    	return getMessageObject(messageType, selection, action, input, null, null);
    }

    /**
     * Factory for native MessageObjects.
     * 
     * @param messageType
     * @param selection
     * @param action
     * @param input
     * @param selection2 second selection for hint requests
     * @param action2 second action for hint requests
     * @return new MessageObject with these properties
     */
    public static MessageObject getMessageObject(String messageType, String selection,
            String action, String input, String selection2, String action2) {
        MessageObject mo = MessageObject.create(messageType); // blank verb
        Vector<String> selections = new Vector<String>();
        Vector<String> actions = new Vector<String>();
        Vector<String> inputs = new Vector<String>();

        selections.add(selection);
        if (selection2 != null)
            selections.add(selection2);
        actions.add(action);
        if (action2 != null)
            actions.add(action2);
        inputs.add(input);

        mo.setSelection(selections);
        mo.setAction(actions);
        mo.setInput(inputs);
        
        mo.setTransactionId(MessageObject.makeTransactionId());

        return mo;
    }
	
	/**
	 * Run a log-playback session and test the log messages produced. 
	 * @throws CommException
	 */
	public void testLogSessionFromXML() {
		checkConvFromXML("canonC4R1Request", canonC4R1Request,
				"InterfaceAction", sv("table1_C4R1"), sv("UpdateTable"), sv("5"));

		checkConvFromXML("canonC4R1Response", canonC4R1TutorMessage, 
				new Object[]{"AssociatedRules", sv("table1_C6R4"), sv("UpdateTable"), sv("5"),
				"InCorrect",
				sv("Instead of the step you are working on, please work on the highlighted step."),
				sv("add-addends addition")});

		checkConvFromXML("canonHint1Request", canonHint1Request,
				"InterfaceAction", sv("Help", "table1_C5R1"), sv("ButtonPressed", "PreviousFocus"), sv("-1", ""));

		checkConvFromXML("canonHint1Response", canonHint1TutorMessage, 
				new Object[]{"AssociatedRules", sv("table1_C5R1"), sv("UpdateTable"), sv("1"),
				"Hint",
				sv("Write carry from the ones to the next column.",
						"The sum that you have, 15, is greater than 9. So you need to carry 10 of the 15 to the tens column.",
						"Write 1 at the top of the second column from the right."),
				sv("write-carry addition")});
	}

	/**
	 * Turn a list of Strings into a Vector.
	 * @param args
	 * @return Vector instance with given Strings.
	 */
	private Vector<String> sv(String... args) {
		if (args == null)
			return null;
		Vector result = new Vector();
		for (String arg: args)
			result.add(arg);
		return result;
	}

	/**
	 * Convert several XML log messages to Comm and test resulting property values.
	 * @param label tag for error messages
	 * @param logMsg XML to convert
	 * @param args arrays (one array per logMsg[] element) of property values to check,
	 *        with each array element named by {@link #ConvFromXMLPropertyNames}.
	 */
	private void checkConvFromXML(String label, String[] logMsgs, Object[]... argArrays) {
		int i = 0;
		for (Object[] argArray: argArrays) {
			String logMsg = logMsgs[i];
			convFromXML(label+"["+i+"]", logMsg, argArray);
			i++;
		}
	}

	/**
	 * Convert an XML log message to Comm and test resulting property values.
	 * @param label tag for error messages
	 * @param logMsg XML to convert
	 * @param args property values to check, named by {@link #ConvFromXMLPropertyNames}.
	 */
	private void checkConvFromXML(String label, String logMsg, Object... args) {
		List argsList = new ArrayList();
		for (Object arg: args)
			argsList.add(arg);
		convFromXML(label, logMsg, argsList.toArray());
	}

	/**
	 * Convert an XML log message to Comm and test resulting property values.
	 * @param label tag for error messages
	 * @param logMsg XML to convert
	 * @param args property values to check, named by {@link #ConvFromXMLPropertyNames}.
	 */
	private void convFromXML(String label, String logMsg, Object[] args) {
		MessageObject mo = new DataShopMessageObject(logMsg, logger);
		int i = 0;
		for (Object arg: args) {
			String[] names = ConvFromXMLPropertyNames[i];
			Object msgProperty = null;
			for (int n = 0; n < names.length && msgProperty == null; ++n)
				msgProperty = mo.getProperty(names[n]);
			if (arg instanceof Vector)
				assertEquals("convFromXML("+label+" "+names+")", (Vector) arg, (Vector) msgProperty);
			else
				assertEquals("convFromXML("+label+" "+names+")", (String) arg, (String) msgProperty);
			i++;
		}
	}

	/**
	 * Run a tutor session and test the log messages produced.
	 * @throws CommException
	 */
	public void testTutorSessionToXML() {
		checkLatestMessages("Start State", canonStartStateMessages);
		checkRequestResponse("table1_C4R1", "UpdateTable", "5", // wrong selection
				canonC4R1Request, canonC4R1Response);
		checkRequestResponse("Help", "ButtonPressed", "-1", "table1_C5R1", "PreviousFocus",
				canonHint1Request, canonHint1Response);
		checkNextHintMessage(true, canonHint1RequestNext1, canonHint1ResponseNext1);
		checkNextHintMessage(true, canonHint1RequestNext2, canonHint1ResponseNext2);
		checkNextHintMessage(false, canonHint1RequestPrevious1, canonHint1ResponsePrevious1);
		checkNextHintMessage(false, canonHint1RequestPrevious2, canonHint1ResponsePrevious2);
		checkRequestResponse("table1_C5R1", "UpdateTable", "1", // success msg
				canonC5R1Request, canonC5R1Response);
//		checkMessages("table1_C6R4", "UpdateTable", "5",        // tutor-performed checked
//				canonC6R4Response, sink.getLatestInfoFields()); // with responses above
		checkRequestResponse("table1_C5R4", "UpdateTable", "5", // buggy msg
				canonC5R4_1Request, canonC5R4_1Response);
		checkRequestResponse("table1_C5R4", "UpdateTable", "6",
				canonC5R4_2Request, canonC5R4_2Response);
		checkRequestResponse("table1_C4R1", "UpdateTable", "2", // no model
				canonC4R1_1Request, canonC4R1_1Response);
		checkRequestResponse("table1_C4R1", "UpdateTable", "1",
				canonC4R1_2Request, canonC4R1_2Response);
		checkRequestResponse("Help", "ButtonPressed", "-1",
				canonHint2Request, canonHint2Response);
		checkRequestResponse("Done", "ButtonPressed", "-1",
				canonDoneRequest, canonNotDoneResponse);
		checkRequestResponse("table1_C4R4", "UpdateTable", "8",
				canonC4R4Request, canonC4R4Response);
		checkRequestResponse("Done", "ButtonPressed", "-1",
				canonDoneRequest, canonDoneResponse);
		
		List savedMsgs = controller.getProcessTraversedLinks().getCommMsgs();
		for (int i = 0; i < savedMsgs.size() && i < 3; ++i)
			trace.out("br", "saved TraversedLink msg["+i+"]=\n"+(MessageObject) savedMsgs.get(i));
		controller.getProcessTraversedLinks().saveTraversedLinks_Tofile("traversedLinks.xml");
		controller.getProcessTraversedLinks().loadTraversedLinks_Fromfile("traversedLinks.xml");
		List restoredMsgs = controller.getProcessTraversedLinks().getCommMsgs();
		assertEquals("saved TraversedLink msg count not match restored",
				savedMsgs.size(), restoredMsgs.size());
		for (int i = 0; i < savedMsgs.size(); ++i) {
			MessageObject smo = (MessageObject) savedMsgs.get(i);
			MessageObject rmo = (MessageObject) restoredMsgs.get(i);
			assertEquals("saved TraversedLink msg["+i+"] mismatch", smo.toString(), rmo.toString());
		}
		
	}
	
	private void checkRequestResponse(String selection, String action, String input,
			String canonRequest, String[] canonResponses) {
		checkRequestResponse(selection, action, input, null, null,
				canonRequest, canonResponses);
	}
	
	private void checkRequestResponse(String selection, String action, String input,
			String selection2, String action2,
			String canonRequest, String[] canonResponses) {
		String messageType = "InterfaceAction";

		MessageObject mo = getMessageObject(messageType, selection,
					action, input, selection2, action2);
				
		DataShopMessageObject omo =
			new DataShopMessageObject(mo, false, logger);  // false==toolToTutor
		
		String outputMessageType = omo.getMessageType();
		Vector<String> selections = omo.getSelection();
		Vector<String> actions = omo.getAction();
		Vector<String> inputs = omo.getInput();

		assertEquals(messageType, outputMessageType);
		assertEquals(selection, selections.get(0));
		assertEquals(action, actions.get(0));
		assertEquals(input, inputs.get(0));

		checkMessage("request("+selection+","+action+","+input+")",
				canonRequest, omo);
		controller.handleCommMessage(omo);
		List responses = sink.getLatestInfoFields();
//		String[] canon = new String[1+canonResponses.length];
//		canon[0] = canonRequest;
		List canon = new ArrayList();
		canon.add(canonRequest);
		canon.addAll(Arrays.asList(canonResponses));
		checkMessages(selection, action, input, canon, responses);
	}

	/**
	 * @param selection
	 * @param action
	 * @param input
	 * @param canon
	 * @param actual
	 */
	private void checkMessages(String selection, String action, String input,
			List canon, List actual) {
		for (int i = 0; i < actual.size(); ++i) {
//			MessageObject resp = (MessageObject) responses.get(i);
//			DataShopMessageObject oresp =
//				new DataShopMessageObject(resp, true, logger);  // true==tutorToTool
			checkMessage("response["+i+"]("+selection+","+action+","+input+")",
					(String) canon.get(i), (String) actual.get(i));
		}
	}

	/**
	 * Check a next or previous hint request and its response.
	 * @param next true if request for next, false if for previous hint
	 * @param canonRequest expected request
	 * @param canonResponse expected response
	 */
	private void checkNextHintMessage(boolean next, String canonRequest,
			String canonResponse) {
		HintMessagesManager mgr = controller.getHintMessagesManager();
		if (next)
			mgr.getNextMessage();         // simulate Next Hint button press
		else
			mgr.getPreviousMessage();     // simulate Previous Hint button press

		String[] canon = {canonRequest, canonResponse};
		checkMessages(null, null, null, Arrays.asList(canon), sink.getLatestInfoFields());
//		MessageObject mo = mgr.getNextHintRequest(next);
//		trace.out("log", (next ? "next" : "prev")+" hint request:\n"+mo);
//		DataShopMessageObject omo = new DataShopMessageObject(mo, false, logger);
//		checkMessage((next ? "next" : "prev")+" hint request", canonRequest, omo.toXML());
//		
//		mo = mgr.getNextHintResponse(next);
//		trace.out("log", (next ? "next" : "prev")+" hint response:\n"+mo);
//		omo = new DataShopMessageObject(mo, true, logger);
//		checkMessage((next ? "next" : "prev")+" hint response", canonResponse, omo.toXML());
	}

	/**
	 * Check a list of messages.
	 * @param label
	 * @param canonMsgList
	 */
	public void checkLatestMessages(String label, String[] canonMsgList) {
		List msgs = sink.getLatestInfoFields();
		int i = 0;
		for (Iterator it = msgs.iterator(); it.hasNext(); ++i) {
			String info = (String) it.next();
			checkMessage(label+ " Message["+i+"]", canonMsgList[i], info);
		}
	}
	
	/**
	 * Check a message against a canonical text.
	 * @param label
	 * @param canon expected text
	 * @param actual actual text
	 */
	private void checkMessage(String label, String canon, String actual) {
		trace.out("log", label+" message:\n"+actual);
		String contextMsgId = getAttrValue("context_message_id", actual);
		String transactionId = getAttrValue("transaction_id", actual);
		trace.out("log", "getAttrValue(transaction_id) returns "+transactionId);
		if (transactionId == null)
			transactionId = getEltText("transaction_id", actual);
		String problemContext = logger.getProblemContext();
		String timestamp = getCustomfieldValue("event_time", actual);
		
//		System.err.println("getCustomfieldValue = [" + timestamp + "] =" + (timestamp != null) );
		
		String alteredCanon = canon;
		if (contextMsgId != null)
			alteredCanon = canon.replaceAll(CONTEXT_MESSAGE_ID_TAG, contextMsgId);
		if (transactionId != null)
			alteredCanon = alteredCanon.replaceAll(TRANSACTION_ID_TAG, transactionId);
		if (problemContext != null)
			alteredCanon = alteredCanon.replaceAll(PROBLEM_CONTEXT_TAG, problemContext);
		if (timestamp != null) 
			alteredCanon = alteredCanon.replaceAll(EVENT_TIME, timestamp);

		assertEquals(label, alteredCanon, actual);
	}
	

	private String getEltText(String label, String actual) {
		int labelS = actual.indexOf(label+"\">\n\t\t");
		int dataS = labelS+label.length()+5;
		trace.out("log", "getEltText("+label+") labelS "+labelS+", dataS="+dataS);
		if (labelS < 0)
			return null;
		int dataE = actual.indexOf("\n", dataS);
		if (dataE >= dataS)
			return actual.substring(dataS, dataE);
		else
			return null;
	}

	/**
	 * Check a message against a canonical text.
	 * @param label
	 * @param canon expected text
	 * @param actual actual message object
	 */
	private void checkMessage(String label, String canon, DataShopMessageObject omo) {
		trace.out("log", label+" message:\n"+omo.toXML());
		String contextMsgId = omo.getContextMessageId();
		String transactionId = omo.getTransactionId();
		String problemContext = logger.getProblemContext();
		String timestamp = omo.UTCTimeStamp(omo.getTimeStamp());
		
		String alteredCanon = canon;
		if (contextMsgId != null)
			alteredCanon = canon.replaceAll(CONTEXT_MESSAGE_ID_TAG, contextMsgId);
		if (transactionId != null)
			alteredCanon = alteredCanon.replaceAll(TRANSACTION_ID_TAG, transactionId);
		if (problemContext != null)
			alteredCanon = alteredCanon.replaceAll(PROBLEM_CONTEXT_TAG, problemContext);
		if (timestamp != null)
			alteredCanon = alteredCanon.replaceAll(EVENT_TIME, timestamp);
		assertEquals(label, alteredCanon, omo.toXML());
	}

	/**
	 * Find the context_message_id attribute in an XML string.
	 * @param name attribute name
	 * @param xmlText the XML document, as a string
	 * @return first value of the attribute, if found; else returns null
	 */
	private String getAttrValue(String name, String xmlText) {
		String label = " "+name.trim()+"=\"";
		int begin = xmlText.indexOf(label);
		trace.out("log", "begin("+label+")="+begin+"+label.length()="+(begin+label.length()));
		if (begin < 0)
			return null;
		int end = xmlText.indexOf('\"', begin+=label.length());
		if (begin >= 0 && end >= begin) {
			String result = xmlText.substring(begin, end);
			trace.out("log", "result("+label+")="+result);
			return result;
		}
		return null;
	}
	
	private String getCustomfieldValue(String name, String xmlText) {

		int begin = xmlText.indexOf(name);
//		trace.out("log", "begin("+name+")="+begin+"+name.length()="+(begin+name.length()));
			begin = xmlText.indexOf("value", begin += name.length());
		int end = xmlText.indexOf("/value", begin);

		if (begin >= 0 && end >= begin) {
			String result = xmlText.substring(begin + 6, end - 1);
//			trace.out("log", "["+begin+" , " + end+"]=" + result);
			trace.out("log", "result("+name+")="+result);
			return result;
		}
		return null;
	}
	

	/**
	 * Create an InterfaceAction message, convert it using
	 * {@link edu.cmu.pact.Utilities.DataShopMessageObject#DataShopMessageObject(MessageObject, boolean, BR_Controller)},
	 * process it through the example-tracer and convert the responses.
	 * @param selection 
	 * @param action
	 * @param input
	 */
	public void playToolMessage(String selection, String action, String input) {
		playToolMessage(selection, action, input, null, null);
	}
	
	/**
	 * Create an InterfaceAction message, convert it using
	 * {@link edu.cmu.pact.Utilities.DataShopMessageObject#DataShopMessageObject(MessageObject, boolean, BR_Controller)},
	 * process it through the example-tracer and convert the responses.
	 * @param selection 
	 * @param action
	 * @param input
	 */
	public void playToolMessage(String selection, String action, String input,
			String selection2, String action2) {
		String messageType = "InterfaceAction";

		MessageObject mo = getMessageObject(messageType, selection,
					action, input, selection2, action2);
				
		DataShopMessageObject omo =
			new DataShopMessageObject(mo, false, logger);  // false==toolToTutor
		String outputMessageType = omo.getMessageType();
		Vector<String> selections = omo.getSelection();
		Vector<String> actions = omo.getAction(); 
		Vector<String> inputs = omo.getInput(); 

		assertEquals(messageType, outputMessageType);
		assertEquals(selection, selections.get(0));
		assertEquals(action, actions.get(0));
		assertEquals(input, inputs.get(0));

		trace.out("log", "testToolMessageToXML:\n"+mo+"\n"+omo.toXML());
		controller.handleCommMessage(omo);
		List responses = sink.getLatestInfoFields();
		for (int i = 0; i < responses.size(); ++i) {
			MessageObject resp = (MessageObject) responses.get(i);
			DataShopMessageObject oresp =
				new DataShopMessageObject(resp, true, logger);  // true==tutorToTool
			trace.out("log", "playToolMessage("+selection+","+action+","+input+
					") response["+i+"]:\n"+resp+"\n"+oresp.toXML());
		}
	}
	
	/**
	 * Load brd file 687plus178.brd.
	 * @throws Exception
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        String[] args = {
            	"-Dunit_name=TestUnit"
        };
        CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
        controller = launcher.getFocusedController();
        assertNotNull(controller);
        UniversalToolProxy sinkTP =
        	new PseudoTutorMessageHandlerTest.SinkToolProxy(controller);
        controller.setUniversalToolProxy(sinkTP);
        controller.getProblemModel().setUseCommWidgetFlag(false);

        controller.getPreferencesModel().setBooleanValue(BR_Controller.USE_DISK_LOGGING,
        		Boolean.TRUE);
        sink = new SinkLogger();
        controller.getLoggingSupport().setSubstituteDiskLogger(sink);
        
        String problemFileLocation = "678plus187.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.addDebugCode("log");
        trace.out("log", "problemFileLocation str = "+problemFileLocation+", url = "+url);
        if (url == null)
        	trace.err("null URL for problemFileLocation " +problemFileLocation);
        else
            controller.openBRFromURL(url.toString());        
        logger = controller.getLogger();
	}

	/**
	 * Remove the members established by {@link #setUp()}.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		sink = null;
		controller = null;
	}

    public static Test suite() {
        return new TestSuite(DataShopMessageObjectTest.class);
    }

    /** Expected set of start state messages. */
	private static final String[] canonStartStateMessages = {
//		contextMessage
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<context_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\" name=\"START_PROBLEM\">\n"+
		"\t<class>\n"+
		"\t</class>\n"+
		"\t<dataset>\n"+
		"\t\t<name>UndefinedCourse</name>\n"+
		"\t\t<level type=\"Unit\">\n"+
		"\t\t\t<name>TestUnit</name>\n"+
		"\t\t\t<problem>\n"+
		"\t\t\t\t<name>678plus187</name>\n"+
		"\t\t\t\t<context>_%_PROBLEM_CONTEXT_%_</context>\n"+
		"\t\t\t</problem>\n"+
		"\t\t</level>\n"+
		"\t</dataset>\n"+
		"</context_message>\n"+
		"</tutor_related_message_sequence>\n" 
	};

	private static final String canonC4R1Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>5</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	/** Tutor message for C4R1 step. */
	private static String[] canonC4R1TutorMessage = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C6R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>5</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>InCorrect</action_evaluation>\n"+
		"\t<tutor_advice>Instead of the step you are working on, please work on the highlighted step.</tutor_advice>\n"+
		"\t<skill probability=\"0.26769233\">\n"+
		"\t\t<name>add-addends</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>1</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static String[] canonHint1TutorMessage = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_MSG\" subtype=\"Hint\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation current_hint_number=\"1\" total_hints_available=\"3\">Hint</action_evaluation>\n"+
		"\t<tutor_advice>Write carry from the ones to the next column.</tutor_advice>\n"+
		"\t<tutor_advice>The sum that you have, 15, is greater than 9. So you need to carry 10 of the 15 to the tens column.</tutor_advice>\n"+
		"\t<tutor_advice>Write 1 at the top of the second column from the right.</tutor_advice>\n"+
		"\t<skill probability=\"0.26769233\">\n"+
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>10</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String[] canonC4R1Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tInCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C4R1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>5</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		canonC4R1TutorMessage[0],
		
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tHighlightMsg\n"+
		"\t</property>\n"+
		"\t<property name=\"HighlightMsgText\">\n"+
		"\t\tInstead of the step you are working on, please work on the highlighted step.\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C6R4</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\ttrue\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonHint1Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_REQUEST\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>Help</selection>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<action>PreviousFocus</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t\t<input></input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonHint1Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tShowHintsMessage\n"+
		"\t</property>\n"+
		"\t<property name=\"HintsMessage\">\n"+
		"\t\t<entry>Write carry from the ones to the next column.</entry>\n"+
		"\t\t<entry>The sum that you have, 15, is greater than 9. So you need to carry 10 of the 15 to the tens column.</entry>\n"+
		"\t\t<entry>Write 1 at the top of the second column from the right.</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C5R1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"StepID\">\n"+
		"\t\t10\n"+
		"\t</property>\n"+
		"\t<property name=\"Rules\">\n"+
		"\t\t<entry>write-carry addition</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Skills\">\n"+
		"\t\t<entry>write-carry addition"+Skill.SKILL_BAR_DELIMITER+"0.26769233"+Skill.SKILL_BAR_DELIMITER+"0</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		canonHint1TutorMessage [0]
	};

	private static final String canonHint1RequestNext1 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_REQUEST\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>NextHintButton</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonHint1ResponseNext1 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_MSG\" subtype=\"HINT_NEXT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation current_hint_number=\"2\" total_hints_available=\"3\">HINT_NEXT</action_evaluation>\n"+
		"\t<tutor_advice>The sum that you have, 15, is greater than 9. So you need to carry 10 of the 15 to the tens column.</tutor_advice>\n"+
		"\t<skill>\n"+
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>10</value>\n"+
		"\t</custom_field>\n"+		
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonHint1RequestNext2 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_REQUEST\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>NextHintButton</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonHint1ResponseNext2 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_MSG\" subtype=\"HINT_NEXT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation current_hint_number=\"3\" total_hints_available=\"3\">HINT_NEXT</action_evaluation>\n"+
		"\t<tutor_advice>Write 1 at the top of the second column from the right.</tutor_advice>\n"+
		"\t<skill>\n"+
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>10</value>\n"+
		"\t</custom_field>\n"+		
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonHint1RequestPrevious1 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_REQUEST\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>PreviousHintButton</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonHint1ResponsePrevious1 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_MSG\" subtype=\"HINT_PREVIOUS\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation current_hint_number=\"2\" total_hints_available=\"3\">HINT_PREVIOUS</action_evaluation>\n"+
		"\t<tutor_advice>The sum that you have, 15, is greater than 9. So you need to carry 10 of the 15 to the tens column.</tutor_advice>\n"+
		"\t<skill>\n"+
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>10</value>\n"+
		"\t</custom_field>\n"+		
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonHint1RequestPrevious2 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_REQUEST\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>PreviousHintButton</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonHint1ResponsePrevious2 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_MSG\" subtype=\"HINT_PREVIOUS\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation current_hint_number=\"1\" total_hints_available=\"3\">HINT_PREVIOUS</action_evaluation>\n"+
		"\t<tutor_advice>Write carry from the ones to the next column.</tutor_advice>\n"+
		"\t<skill>\n"+
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>10</value>\n"+
		"\t</custom_field>\n"+		
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String canonC5R1Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonC5R1Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C5R1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",
		
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>Correct</action_evaluation>\n"+
		"\t<tutor_advice><![CDATA[Usually you should do the sum <b>before</b> the carry.]]></tutor_advice>\n"+
		"\t<skill probability=\"0.26769233\">\n"+  // 0.6270968
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>10</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tSuccessMessage\n"+
		"\t</property>\n"+
		"\t<property name=\"SuccessMsg\">\n"+
		"\t\t<![CDATA[Usually you should do the sum <b>before</b> the carry.]]>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\ttrue\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\" trigger=\"DATA\" subtype=\"tutor-performed\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C6R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>5</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n",
		
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C6R4</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>5</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C6R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>5</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>Correct</action_evaluation>\n"+
		"\t<skill>\n"+
		"\t\t<name>unnamed</name>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>12</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonC5R4_1Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>5</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonC5R4_1Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tInCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C5R4</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>5</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>6</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>InCorrect</action_evaluation>\n"+
		"\t<tutor_advice>You forgot to add the carry.</tutor_advice>\n"+
		"\t<skill probability=\"0.26769233\">\n"+
		"\t\t<name>add-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>4</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tBuggyMessage\n"+
		"\t</property>\n"+
		"\t<property name=\"BuggyMsg\">\n"+
		"\t\tYou forgot to add the carry.\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C5R4</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\ttrue\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonC5R4_2Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>6</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonC5R4_2Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C5R4</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>6</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C5R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>6</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>Correct</action_evaluation>\n"+
		"\t<skill probability=\"0.26769233\">\n"+  // was 0.6270968
		"\t\t<name>add-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>4</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonC4R1_1Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>2</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonC4R1_1Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tInCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C4R1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>2</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>InCorrect</action_evaluation>\n"+
		"\t<skill probability=\"0.25247115\">\n"+  // was 0.4787268
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>6</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonC4R1_2Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonC4R1_2Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C4R1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R1</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>Correct</action_evaluation>\n"+
		"\t<skill probability=\"0.25247115\">\n"+  // was 0.7983072
		"\t\t<name>write-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>6</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonHint2Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_REQUEST\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>Help</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonHint2Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tShowHintsMessage\n"+
		"\t</property>\n"+
		"\t<property name=\"HintsMessage\">\n"+
		"\t\t<entry>There is a carry in to this column so you need to add the value carried in.</entry>\n"+
		"\t\t<entry>This gives 6 + 1 + 1 equals 8.</entry>\n"+
		"\t\t<entry><![CDATA[Please enter '8' in the highlighted cell.]]></entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C4R4</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>8</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"StepID\">\n"+
		"\t\t8\n"+
		"\t</property>\n"+
		"\t<property name=\"Rules\">\n"+
		"\t\t<entry>add-carry addition</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Skills\">\n"+
		"\t\t<entry>add-carry addition"+Skill.SKILL_BAR_DELIMITER+"0.25247115"+Skill.SKILL_BAR_DELIMITER+"0</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",
		
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"HINT_MSG\" subtype=\"Hint\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>8</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation current_hint_number=\"1\" total_hints_available=\"3\">Hint</action_evaluation>\n"+
		"\t<tutor_advice>There is a carry in to this column so you need to add the value carried in.</tutor_advice>\n"+
		"\t<tutor_advice>This gives 6 + 1 + 1 equals 8.</tutor_advice>\n"+
		"\t<tutor_advice><![CDATA[Please enter '8' in the highlighted cell.]]></tutor_advice>\n"+
		"\t<skill probability=\"0.25247115\">\n"+
		"\t\t<name>add-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>8</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonC4R4Request =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>8</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonC4R4Response = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>table1_C4R4</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>UpdateTable</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>8</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>8</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>Correct</action_evaluation>\n"+
		"\t<skill probability=\"0.25247115\">\n"+
		"\t\t<name>add-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>8</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};

	private static final String canonDoneRequest =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tool_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"ATTEMPT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>Done</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tool_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"</tool_message>\n"+
		"</tutor_related_message_sequence>\n";

	private static final String[] canonNotDoneResponse = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tInCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>Done</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>ButtonPressed</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>-1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>table1_C4R4</selection>\n"+
		"\t\t<action>UpdateTable</action>\n"+
		"\t\t<input>8</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>InCorrect</action_evaluation>\n"+
		"\t<tutor_advice><![CDATA[I'm sorry, but you are not done yet. Please continue working.]]></tutor_advice>\n"+
		"\t<skill probability=\"0.25247115\">\n"+
		"\t\t<name>add-carry</name>\n"+
		"\t\t<category>addition</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>8</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tBuggyMessage\n"+
		"\t</property>\n"+
		"\t<property name=\"BuggyMsg\">\n"+
		"\t\t<![CDATA[I'm sorry, but you are not done yet. Please continue working.]]>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\ttrue\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n"
	};
	
	private static final String[] canonDoneResponse = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<property name=\"verb\">\n"+
		"\t\tSendNoteProperty\n"+
		"\t</property>\n"+
		"\t<property name=\"MessageType\">\n"+
		"\t\tCorrectAction\n"+
		"\t</property>\n"+
		"\t<property name=\"Selection\">\n"+
		"\t\t<entry>Done</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Action\">\n"+
		"\t\t<entry>ButtonPressed</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"Input\">\n"+
		"\t\t<entry>-1</entry>\n"+
		"\t</property>\n"+
		"\t<property name=\"transaction_id\">\n"+
		"\t\t_%_TRANSACTION_ID_%_\n"+
		"\t</property>\n"+
		"\t<property name=\"end_of_transaction\">\n"+
		"\t\tfalse\n"+
		"\t</property>\n"+
		"</message>\n"+
		"</tutor_related_message_sequence>\n",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<tutor_related_message_sequence version_number=\"4\" > \n"+
		"<tutor_message context_message_id=\"_%_CONTEXT_MESSAGE_ID_%_\">\n"+
		"\t<problem_name>678plus187</problem_name>\n"+
		"\t<semantic_event transaction_id=\"_%_TRANSACTION_ID_%_\" name=\"RESULT\"/>\n"+
		"\t<event_descriptor>\n"+
		"\t\t<selection>Done</selection>\n"+
		"\t\t<action>ButtonPressed</action>\n"+
		"\t\t<input>-1</input>\n"+
		"\t</event_descriptor>\n"+
		"\t<action_evaluation>Correct</action_evaluation>\n"+
		"\t<skill probability=\"0.66\">\n"+
		"\t\t<name>done</name>\n"+
		"\t\t<category>Done</category>\n"+
		"\t</skill>\n"+
		"\t<custom_field>\n"+
		"\t\t<name>tutor_event_time</name>\n"+
		"\t\t<value>_%_EVENT_TIME_%_</value>\n"+
		"\t</custom_field>\n"+		
		"\t<custom_field>\n"+
		"\t\t<name>step_id</name>\n"+
		"\t\t<value>17</value>\n"+
		"\t</custom_field>\n"+
		"</tutor_message>\n"+
		"</tutor_related_message_sequence>\n"
	};
}
