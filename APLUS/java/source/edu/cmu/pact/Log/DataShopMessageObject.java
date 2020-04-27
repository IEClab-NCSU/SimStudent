/**
 * Copyright 2011 Carnegie Mellon University.
 */
package edu.cmu.pact.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.sun.corba.se.impl.io.TypeMismatchException;

import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.Log.TutorActionLog.ActionEvaluation;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.jess.JessModelTracing;
import edu.cmu.pact.jess.LogWorkingMemory;
import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.LogContext;
import edu.cmu.pslc.logging.Message;
import edu.cmu.pslc.logging.PlainMessage;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.TutorMessage;
import edu.cmu.pslc.logging.element.MetaElement;
import edu.cmu.pslc.logging.element.SemanticEventElement;
import edu.cmu.pslc.logging.element.SkillElement;
import edu.cmu.pslc.logging.element.UiEventElement;

/**
 * Extension to internal {@link MessageObject} to include the equivalent DataShop log message.
 * @author sewall
 */
public class DataShopMessageObject extends MessageObject {

	public static final String HINT_REQUEST = "HINT_REQUEST";

	/** Custom field name for step identifier. */
    public static final String STEP_ID = "step_id";

	/** Semantic event name for context message. */
	protected static final String START_PROBLEM = "START_PROBLEM";

	/** Underlying message from XML. */
	private TutorActionLogV4 logMsg = null;
	
	/** String formatted for VLab replay. */
	private String replay = null;
	
	/** False if message sent from tool to tutor, true if from tutor to tool. */
	private boolean tutorToTool;
	
	/**
	 * @return the replay
	 */
	public String getReplay() {
		return replay;
	}

	/** OLI ActionEvaluation text. */
	private String actionEvaluation;

	private LogContext logger;

	/** XML Element from which this instance was parsed, as a string. */
	private String originalElementString;

	/** ProblemName for this series of messages. */
	private String problemName = "";

	/**
	 * @param actionEvaluation new value for {@link #actionEvaluation}
	 */
	protected void setActionEvaluation(String actionEvaluation) {
		this.actionEvaluation = actionEvaluation;
	}

	/**
	 * @return the {@link #actionEvaluation}
	 */
	protected String getActionEvaluation() {
		return actionEvaluation;
	}

	/**
	 * Create from an internal message.
	 * @param mo base message to clone
	 * @param tutorToTool false if from tool to tutor; true if from tutor to tool
	 * @param logger to supply context_message info
	 */
	public DataShopMessageObject(MessageObject mo, boolean tutorToTool, LogContext logger) {
		super(mo);
		this.tutorToTool = tutorToTool;
		this.logger = logger;
		logMsg = null;
		
		replay = (String) getProperty(TutorActionLogV4.REPLAY);
		
		String indicator = (String) getProperty(PseudoTutorMessageBuilder.INDICATOR);
		if (indicator != null && indicator.length() > 0)
			setActionEvaluation(indicator);
		else
			setActionEvaluation(msgTypeToActionEval());
		String subtype = (String) getProperty(PseudoTutorMessageBuilder.SUBTYPE);

		if (trace.getDebugCode("log")) trace.out("log", "tutorToTool "+tutorToTool+
				", subtype "+subtype+", indicator "+indicator+
				", actionEval "+getActionEvaluation()+"; message\n  "+this);
	}

	/**
	 * Try to derive an action evaluation from the {@link #getMessageType()}.
	 * @return action evaluation or null
	 */
	private String msgTypeToActionEval() {
		String msgType = getMessageType();
		if (MsgType.NEXT_HINT_MESSAGE.equalsIgnoreCase(msgType))
			return "HINT_NEXT";
		if (MsgType.PREVIOUS_HINT_MESSAGE.equalsIgnoreCase(msgType))
			return "HINT_PREVIOUS";
		return null;
	}

	/**
	 * Create from a DataShop log message.
	 * @param xmlStr text of DataShop message
	 * @param logger
	 */
	public DataShopMessageObject(String xmlStr, Logger logger) {
		super();
		MetaElement meta = null;
		if (logger != null && logger.getContextMessage() != null)
			meta = logger.getContextMessage().getMetaElement();
		
		if (trace.getDebugCode("mo")) trace.out("mo", "DatatShopMessageObject constructor: meta element = "+meta.toString());
		
		TutorActionLog logAct = TutorActionLog.factory(xmlStr, TutorActionLogV4.VERSION_NUMBER, meta); 
		init(MsgType.INTERFACE_ACTION);   // placeholder message type; OLI2Native may revise
		OLI2Native(logAct);
	}

	/**
	 * String representation as XML pretty-print. See
	 * {@link edu.cmu.oli.log.TutorActionLog#toString()}. return
	 * {@link #logMsg}.{@link edu.cmu.oli.log.TutorActionLog#toString()};
	 * empty string if logMsg null
	 */
	public String toXML() {
		if (getLogMsg() == null)
			return super.toXML();
		else
			return getLogMsg().toString();
	}

	/**
	 * Internal message types (in lower case) that must be logged as tool_messages.
	 * See {@link #getTutorToToolRestriction(String)}.
	 */
	private static final List<String> toolOnlyMessageTypes = Arrays.asList( new String[] {
		MsgType.INTERFACE_ACTION.toLowerCase()
	} );

	/**
	 * Internal message types (in lower case) that must be logged as tutor_messages.
	 * See {@link #getTutorToToolRestriction(String)}.
	 */
	private static final List<String> tutorOnlyMessageTypes = Arrays.asList( new String[] {
		MsgType.ASSOCIATED_RULES.toLowerCase(),
		MsgType.NEXT_HINT_MESSAGE.toLowerCase(),
		MsgType.PREVIOUS_HINT_MESSAGE.toLowerCase()
	} );

	/**
	 * Internal message types (in lower case) that must be logged as context_messages.
	 */
	private static final List<String> contextMessageTypes = Arrays.asList( new String[] {
		MsgType.START_PROBLEM.toLowerCase(),
		MsgType.LOAD_BRD_FILE_SUCCESS.toLowerCase()
	} );

	/**
	 * Internal message types (in lower case) that must be logged as UIEvents.
	 */
	private static final List<String> uiEventMessageTypes = Arrays.asList( new String[] {
		MsgType.GLOSSARY.toLowerCase(),
		MsgType.COGNITIVE_LOAD.toLowerCase()
	} );
	
	/**
	 * @param messageType internal message type
	 * @return {@link Boolean#FALSE} if messageType can only be tool-to-tutor;
	 *         {@link Boolean#TRUE} if messageType can only be tutor-to-tool;
	 *         null if either        
	 */
	private Boolean getTutorToToolRestriction(String messageType) {
		if (toolOnlyMessageTypes.contains(messageType))
			return Boolean.FALSE;
		if (tutorOnlyMessageTypes.contains(messageType))
			return Boolean.TRUE;
		return null;
	}

	/**
	 * Set a value in the {@link #properties} map. Override calls superclass method and
	 * nulls {@link #logMsg} to force update.
	 * @param propertyName key in {@link #properties} will be trimmed, in lower case
	 * @param propertyValue
	 * @param useAsIs
	 */
	protected void setPropertyInternal(String propertyName, Object propertyValue, boolean useAsIs) {
		super.setPropertyInternal(propertyName, propertyValue, useAsIs);
		logMsg = null;
	}
	
	/**
	 * Create a TutorActionLogV4 instance from a MessageObject.
	 */
	public TutorActionLogV4 getLogMsg() {
		if (logMsg != null)
			{if (trace.getDebugCode("mo")) trace.out("mo", "getLogMsg() logMsg already exists");
			return logMsg;}
		
		Class cls = whichDataShopMsgType();
		if (trace.getDebugCode("mo")) trace.out("mo", "getLogMsg() logMsg is of type "+cls.getName());

		if (cls == PlainMessage.class){
			if (trace.getDebugCode("mo")) trace.out("mo", "creating a new plain message");
			return logMsg = createPlainMessage();}

		if (cls == ContextMessage.class)
			return logMsg = createContextMessage();

		if (cls == TutorMessage.class) {
			ToolMessage lastToolMessage = getLastToolMessage(logger);
			/*lastToolMessage.setTransactionId(getTransactionId());*/  // FIXME why copy back to tool msg?
			TutorMessage msg = TutorMessage.create(lastToolMessage);
			String actionEvaluation = getActionEvaluation();
			setResponseType(actionEvaluation, msg);
			logMsg = new TutorActionLogV4(msg);
			if (trace.getDebugCode("mo")) trace.out("mo", "tutorMessage: new logMsg before adding more info "+logMsg.toString());
			System.out.println(" Setting the problem Name : "+logger.getProblemName());
			logMsg.setProblemName(logger.getProblemName());
			createEventDesc(logMsg);
			
			addCustomFields(logMsg);

			if(replay != null)
				logMsg.setReplay(replay);
			if (actionEvaluation != null && actionEvaluation.toUpperCase().contains("HINT")) {
				populateHintElements(logMsg);
				createSkills(logMsg);
				addTimeStamp(logMsg);
				addStepId(logMsg);
				
				if (trace.getDebugCode("mo")) trace.out("mo", "tutorMessage: in if loop, after more settings "+logMsg.toString());
				return logMsg;
			}
		} else {
			ToolMessage msg = ToolMessage.create(logger.getContextMessage());
			if (isUIEventMsg(this))
				setAsUIEvent(msg, this.getTransactionId());
			else {
				/*
				 * ToolMessage.setAsXXX() methods generate new transaction ids,
				 * so fix the id after calling them.
				 */
				if (isHintMsg(this))
					msg.setAsHintRequest();
				else {
					msg.setAsAttempt();
					String semEvtName = ((SemanticEventElement) msg.getEventElement()).getName();
			        SemanticEventElement see2 =
			        	new SemanticEventElement(getTransactionId(), semEvtName,
			        			(String) getProperty(PseudoTutorMessageBuilder.TRIGGER),
			        			(String) getProperty(PseudoTutorMessageBuilder.SUBTYPE));
			        msg.setEventElement(see2);
				}
				msg.setTransactionId(getTransactionId());
				logger.setLastToolMessage(msg);
			}
			logMsg = new TutorActionLogV4(msg);
			logMsg.setProblemName(logger.getProblemName());
			createEventDesc(logMsg);
			if(replay != null)
				((TutorActionLogV4)logMsg).setReplay(replay);
			if (isUIEventMsg(this) || isHintMsg(this)) {
				addTimeStamp((TutorActionLogV4) logMsg);
				return logMsg;
			}
		}
		
		if (getActionEvaluation() != null){ logMsg.addActionEvaluation(getActionEvaluation()); }
		
		//not a hint, so likely that it's giving one advice for an incorrect
		List<String> advices = createTutorAdvices();
		if(advices != null){
			for(String advice : advices){
				logMsg.addTutorAdvice(advice);
			}
		}
		
		createSkills(logMsg);
		if(replay != null)
			((TutorActionLogV4)logMsg).setReplay(replay);
		addTimeStamp(logMsg);
		addWMImages(logMsg);
		addStepId(logMsg);

		if (trace.getDebugCode("mo")) trace.out("mo", "getLogMsg() reached the end of the method call: "+logMsg.toString());
		return logMsg;
	}

	protected void addCustomFields(TutorActionLogV4 logMsg) {
		Message msg = logMsg.getMsg();
		if(!(msg instanceof TutorMessage)){ throw new TypeMismatchException("Only add custom fields to tutor_messages (not including time)"); }
		
		Object test = getProperty("custom_fields");
		if(!(test instanceof List)){ trace.out("mo", "addCustomFields can't get a list of custom_fields"); return; }
		
		for(Object obj : (List) test){
			if(trace.getDebugCode("mo")){ trace.out("mo", "addCustomFields trying to add obj "+new XMLOutputter().outputString((Element) obj)); }
			if(!(obj instanceof Element)){ trace.out("mo", "addCustomFields obj is not an element: "+obj); continue; }

			String name = ((Element) obj).getChildText("name");
			String value = ((Element) obj).getChildText("value");
			
			((TutorMessage) msg).addCustomField(name, value);
		}
	}

	/**
	 * Generate a list of custom elements from the # property.
	 * @param logMsg msg to modify
	 * @return number of WM images added
	 */
	protected int addWMImages(TutorActionLogV4 logMsg) {
		Message msg = logMsg.getMsg();
		int count = 0;
		Vector<String> wmImages = (Vector<String>) getProperty(PseudoTutorMessageBuilder.WMIAGESS_PNAME);
		if (wmImages != null) {
			for (Iterator<String> it = wmImages.iterator(); it.hasNext(); ++count) {
				String[] parsedImage = LogWorkingMemory.parseImage(it.next());
				if (msg instanceof TutorMessage) 
					((TutorMessage) msg).addCustomField(parsedImage[0], parsedImage[1]);
				else if (msg instanceof ToolMessage)  
				 	((ToolMessage)  msg).addCustomField(parsedImage[0], parsedImage[1]);
			}
		}
		return count;
	}

	/**
	 * Set the underlying DataShop elements to show that the given message is a UIEvent,
	 * not a SemanticEvent.
	 * @param msg ToolMessage to convert
	 * @param id UIEvent id attribute
	 */
	protected void setAsUIEvent(ToolMessage msg, String id) {
		msg.setEventElement(new UiEventElement(id, calcSemanticEvt()));
	}	
	
	private static final String[][] msgTypeToSemanticEvtArr = {
		{ MsgType.INTERFACE_ACTION, null },               // could be hint or attempt
		{ MsgType.GLOSSARY.toLowerCase(), "GLOSSARY" },
		{ MsgType.COGNITIVE_LOAD.toLowerCase(), "COGNITIVE_LOAD" },
	};
	private static final Map<String, String> msgTypeToSemanticEvt = new HashMap<String, String>();
	static {
		for (int i = 0; i < msgTypeToSemanticEvtArr.length; ++i)
			msgTypeToSemanticEvt.put(msgTypeToSemanticEvtArr[i][0], msgTypeToSemanticEvtArr[i][1]);
	}

	/**
	 * Try to figure a semantic event element name.
	 * @return semantic event name or {@link #getMessageType()} if can't find one
	 */
	private String calcSemanticEvt() {
		String semanticEvtName = msgTypeToSemanticEvt.get(getMessageType());
		if (semanticEvtName != null)
			return semanticEvtName;
		if (MsgType.INTERFACE_ACTION.equalsIgnoreCase(getMessageType())) {
			if (isHintMsg(this))
				return HINT_REQUEST;
			else
				return "ATTEMPT";
		}
		if (MsgType.ASSOCIATED_RULES.equalsIgnoreCase(getMessageType())) {
			if (isHintMsg(this))
				return "HINT_MSG";
			else
				return "RESULT";
		}
		trace.err("calcSemanticEvt(): no semantic event; using message type "+getMessageType());
		return getMessageType();
	}

	/**
	 * Tell whether a MessageObject is a hint request or result.
	 * @param mo native MessageObject to query
	 * @return true if the first selection item is "hint" or "help"
	 */
	static boolean isHintMsg(MessageObject mo) {
		String selection0 = mo.getSelection0();
		if ("help".equalsIgnoreCase(selection0))
			return true;
		if ("hint".equalsIgnoreCase(selection0))
			return true;
		if (HintMessagesManager.NEXT_HINT_BUTTON.equalsIgnoreCase(selection0))
			return true;
		if (HintMessagesManager.PREVIOUS_HINT_BUTTON.equalsIgnoreCase(selection0))
			return true;
		return false;
	}

	static boolean isUIEventMsg(MessageObject mo) {
		return uiEventMessageTypes.contains(mo.getMessageType().toLowerCase()); 
	}

	/**
	 * Log the link identifier in a custom field.
	 * @param logMsg result msg to modify
	 */
	private void addStepId(TutorActionLogV4 logMsg) {
		Message msg = logMsg.getMsg();
		String stepId = (String) getProperty(PseudoTutorMessageBuilder.STEP_ID);
		if (stepId == null || stepId.length() < 1)
			return;
		if (msg instanceof TutorMessage) 
			((TutorMessage) msg).addCustomField(STEP_ID, stepId);
		else if (msg instanceof ToolMessage)  
		 	((ToolMessage)  msg).addCustomField(STEP_ID, stepId);
	}

	/**
	 * Add a millisecond timestamp as a custom field in tool_ and tutor_messages.
	 * @param logMsg
	 */
	protected void addTimeStamp(TutorActionLogV4 logMsg) {
		Message msg = logMsg.getMsg();
		Date ts = logMsg.getTimeStamp();
		String tsUTC = UTCTimeStamp(ts);
		 if (msg instanceof TutorMessage) 
			 ((TutorMessage) msg).addCustomField("tutor_event_time", tsUTC);
		 else if (msg instanceof ToolMessage)  
			 ((ToolMessage)  msg).addCustomField("tool_event_time",  tsUTC);
	}

	/**
	 * Fill out the contents of a hint response in the DataShop message.
	 */
	protected void populateHintElements(TutorActionLogV4 result) {
		Integer totalHints = getPropertyAsInteger(HintMessagesManager.TOTAL_HINTS_AVAIABLE);
		Integer currentHintNumber = getPropertyAsInteger(HintMessagesManager.CURRENT_HINT_NUMBER);
		if (trace.getDebugCode("mo")) trace.out("mo", ", totalHints "+totalHints+
				", currentHintNo "+currentHintNumber+", actionEvaluation "
				+ getActionEvaluation());
		
		//add the action evaluation
		result.addActionEvaluation(getActionEvaluation(),
				(currentHintNumber != null ? currentHintNumber.intValue() : 1),
				(totalHints != null ? totalHints.intValue() : -1));
		if(trace.getDebugCode("mo"))
			trace.out("hint num "+currentHintNumber+", totalHints "+totalHints);
		
		//add the hint
		List<String> adviceList = createTutorAdvices();
		int h = adviceList.size() - 1;
		if(h >= currentHintNumber - 1)
			h = currentHintNumber - 1;
		if(trace.getDebugCode("mo"))
			trace.out("adviceList.size "+adviceList.size()+", adviceList["+h+"]:\n  "+adviceList.get(h));
		logMsg.addTutorAdvice(adviceList.get(h));
	}
	
	/**
	 * Generate the list of {@link TutorActionLog#Skill} elements from the
	 * {@link #RULES_PNAME} property

	 * @param logMsg
	 *            result msg to modify
	 * @return number of skills added
	 */
	protected int createSkills(TutorActionLogV4 logMsg) {
		int result = 0;
		Vector<String> skills = (Vector<String>) getProperty(PseudoTutorMessageBuilder.SKILLS_PNAME);
		if (skills != null) {
			for (Iterator<String> it = skills.iterator(); it.hasNext(); ++result)
				parseAndAddSkill(it.next(), logMsg);
			return result;
		}
		Vector<String> rules = (Vector<String>) getProperty(PseudoTutorMessageBuilder.RULES_PNAME);
		if (rules != null) {
			for (Iterator<String> it = rules.iterator(); it.hasNext(); ++result)
				parseAndAddSkill(it.next(), logMsg);
		}
		return result;
	}

	/**
	 * Parse a skill value from an AssociatedRules message into its parts and add
	 * the skill to the given Log message. Input text is expected to be in format 
	 * "name category=pKnown=mastery", where<ol>
	 *        <li>name is the skill name</li>
	 *        <li>category is the skill category attribute</li>
	 *        <li>pKnown is the updated probability that the skill is known</li>
	 *        <li>mastery is either 1 if the mastery level has been reached or 0 if not</li>
	 *        </ol>
	 * @param text skill string in format described above
	 * @param logMsg
	 */
	private void parseAndAddSkill(String text, TutorActionLogV4 logMsg) {
		String category = "", probability = null;
		String sbDelim = (String) getProperty(SKILL_BAR_DELIMITER_TAG);
		if (sbDelim == null || sbDelim.length() < 1)
			sbDelim = Skill.SKILL_BAR_DELIMITER;
		String[] parts = text.split(sbDelim);
		if (parts.length > 1)
			probability = parts[1];
		int categoryIndex = text.lastIndexOf(' ')+1;
		if (categoryIndex > 0) {
			category = parts[0].substring(categoryIndex);
			text = parts[0].substring(0, categoryIndex-1);
		} else
			text = parts[0];
		logMsg.addSkill(text, category, probability);
	}

	/**
	 * Generate a list of TutorAdvice elements from {@value HintMessagesManager#HINTS_MESSAGE}
	 * property. This does NOT add them to the message itself.
	 * @return list of the advices
	 */
	protected List<String> createTutorAdvices() {
        Object v = getProperty(PseudoTutorMessageBuilder.TUTOR_ADVICE);
        if (v == null)
            v = getProperty(HintMessagesManager.HINTS_MESSAGE);
        if (v == null)
            return new ArrayList<String>();
        if (v instanceof Vector) {
            Vector<String> advices = (Vector<String>) v;
            return new ArrayList<String>(advices);
        } else if (v instanceof String) {
            List<String> result = new ArrayList<String>();
            result.add((String) v);
            return result;
        }
        throw new IllegalArgumentException("TutorAdvice property is unrecognized type "+v.getClass().getName());
	}

	/**
	 * Generate the {@link TutorActionLog.EventDescriptor} (s) from the
	 * selection-action-input properties.
	 * @param logMsg result msg to modify
	 */
	protected void createEventDesc(TutorActionLogV4 logMsg) {
		Iterator<String> sit = null, ait = null, iit = null;
		int n = 0;
		Vector<String> selections = getSelection();
		if (selections != null) {
			sit = selections.iterator();
			n = Math.max(n, selections.size());
		}
		Vector<String> actions = getAction();
		if (actions != null) {
			ait = actions.iterator();
			n = Math.max(n, actions.size());
		}
		Vector<String> inputs = getInput();
		if (inputs != null) {
			iit = inputs.iterator();
			n = Math.max(n, inputs.size());
		}
		for (int i = 0; i < n; ++i) {
			String ac, se, in;
			ac = ((ait != null && ait.hasNext()) ? (String) ait.next() : "");
			se = ((sit != null && sit.hasNext()) ? (String) sit.next() : "");
			in = ((iit != null && iit.hasNext()) ? (String) iit.next() : "");
			logMsg.addEventDescriptor(getTransactionId(), ac, se, in);
		}
	}

	/** Regular expression ".*[hH][iI][nN][tT].*" for {@link #setResponseType(String, TutorMessage)}. */
	private static final Pattern HintPattern = Pattern.compile(".*[hH][iI][nN][tT].*");

	/**
	 * Characterize the given {@link TutorMessage} as correct, incorrect, etc.
	 * @param actionEvaluation one "CORRECT", "INCORRECT" or "HINT"
	 * @param msg TutorMessage to set
	 */
	protected void setResponseType(String actionEvaluation, TutorMessage msg) {
		if (actionEvaluation == null) {
			trace.err("DataShopMessageObject: null action evaluation for tutor response");
			return;
		}
		if ("CORRECT".equalsIgnoreCase(actionEvaluation) ||
				JessModelTracing.SUCCESS.equalsIgnoreCase(actionEvaluation))
			msg.setAsCorrectAttemptResponse();
		else if ("INCORRECT".equalsIgnoreCase(actionEvaluation) ||
				JessModelTracing.BUG.equalsIgnoreCase(actionEvaluation) ||
				JessModelTracing.NO_MODEL.equalsIgnoreCase(actionEvaluation) ||
				JessModelTracing.FIREABLEBUG.equalsIgnoreCase(actionEvaluation))
			msg.setAsIncorrectAttemptResponse();
		else if (HintPattern.matcher(actionEvaluation).matches())
			msg.setAsHintResponse(actionEvaluation);
		else
			trace.err("ConvV4: no response type for action evaluation "+actionEvaluation);
	}

	/**
	 * Find a last tool message. Returns result of
	 * {@link #getLogger()}.{@link Logger#getLastToolMessage()} if 
	 * not null. Else creates a dummy tool message.
	 * @param logger to call {@link Logger#getLastToolMessage()}
	 * @return real or fake last tool msg
	 */
	private ToolMessage getLastToolMessage(LogContext logger) {
		ToolMessage result = logger.getLastToolMessage();
		if (result != null)
			return result;
		result = ToolMessage.create(logger.getContextMessage());
		result.setAsAttempt();
		return result;
	}

	/**
	 * @return ActionLog wrapping a DataShop {@link Message} instance
	 */
	private TutorActionLogV4 createContextMessage() {
		ContextMessage msg = logger.getContextMessage(START_PROBLEM);
		TutorActionLogV4 result = new TutorActionLogV4(msg);
		if (replay != null)
			result.setReplay(replay);
		return result;
	}

	/**
	 * @return ActionLog wrapping a DataShop {@link Message} instance
	 */
	private TutorActionLogV4 createPlainMessage() {
		PlainMessage msg = PlainMessage.create(logger.getContextMessage());
		TutorActionLogV4 result = new TutorActionLogV4(msg);
		populateProperties(this, result);
		if (replay != null)
			result.setReplay(replay);
		
		if (trace.getDebugCode("mo")) trace.out("mo", "createPlainMessage() "+result.toString());
		return result;
	}

	/**
	 * Fill in the property list of a plain message.
	 * @param propertyNames
	 * @param propertyValues
	 * @param mo
	 * @param result
	 */
	protected void populateProperties(MessageObject mo, TutorActionLogV4 result) {
		String verb = mo.getVerb();
		if (verb != null && verb.length() > 0)
			result.addMsgProperty("verb", verb);

		List<String> propertyNames = getPropertyNames();
		if (trace.getDebugCode("mo")) trace.out("mo", "propertyNames.size() " + propertyNames.size());
		for (int i = 0; i < propertyNames.size(); ++i) {
			String name = (String) propertyNames.get(i);
			Object value = getProperty(name);
			if (value instanceof List)
				result.addMsgProperty(name, (List) value);
			else
				result.addMsgProperty(name, value.toString());
		}
	}

	/**
	 * Decide which of the subclasses of the DataShop {@link Message} class we should log as.
	 * @return Class instance
	 */
	private Class whichDataShopMsgType() {
		String msgType = getMessageType().toLowerCase();
		if (tutorOnlyMessageTypes.contains(msgType) && tutorToTool)
			return TutorMessage.class;
		if (toolOnlyMessageTypes.contains(msgType)) {
			if (!tutorToTool)
				return ToolMessage.class;
			String subtype = (String) getProperty(PseudoTutorMessageBuilder.SUBTYPE);
			if (PseudoTutorMessageBuilder.TUTOR_PERFORMED.equalsIgnoreCase(subtype))
				return ToolMessage.class;					
		}
		if (contextMessageTypes.contains(msgType))
			return ContextMessage.class;
		return PlainMessage.class;
	}

	/**
	 * @param logMsg new value for {@link #logMsg}
	 */
	protected void setLogMsg(TutorActionLogV4 logMsg) {
		this.logMsg = logMsg;
	}

	/**
	 * Get the identifier from the {@link ContextMessage} associated with
	 * this message. The ContextMessage records the problem, course, unit,
	 * section, etc., environment in which this action was taken.
	 * @return contextMessageId
	 */
	public String getContextMessageId() {
		return getLogMsg().getContextMessageId();
	}

	/** @return timeStamp from {@link #logMsg}; null if unset */
	public Date getTimeStamp() {
		return getLogMsg().getTimeStamp();
	}

	/** See {@link #UTCTimeStamp(Date)}. */
	public static final String UTCTimeStampFmt = "yyyy-MM-dd HH:mm:ss.SSS z";
	private static final DateFormat UTCTimeStampDateFmt = new SimpleDateFormat(UTCTimeStampFmt);
	static {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		UTCTimeStampDateFmt.setTimeZone(tz);
	}
	/**
	 * @param ts timestamp to convert
	 * @return ts formatted by {@value #UTCTimeStampFmt}
	 */
	public static String UTCTimeStamp(Date ts) {
		String tsUTC = UTCTimeStampDateFmt.format(ts);
		if (trace.getDebugCode("log")) trace.out("log", "Convert from \""+ts+"\" => "+tsUTC);
		return tsUTC;
	}

	/**
	 * Record the original XML element used to create this object, if any.
	 * Contents should be formatted XML of the element parsed to generate
	 * this instance.
	 */
	public void setOriginalElementString(String eltStr) {
		originalElementString = eltStr;
	}

	/**
	 * @return the {@link #originalElementString}
	 */
	public String getOriginalElementString() {
		return originalElementString;
	}

	/**
	 * Build a native Comm message from a TutorActionLog message.
	 * @param logMsg TutorActionLog instance to translate
	 */
	protected void OLI2Native(TutorActionLog logMsg) {
		
		if (trace.getDebugCode("log")) trace.out("log", "DataShopMessageObject("+logMsg+") class "+
				logMsg.getClass().getSimpleName());
		
		TutorActionLogV4 logV4 = null;
		if(logMsg instanceof TutorActionLogV4) {
			logV4 = (TutorActionLogV4) logMsg;
			if (logV4.getReplay() != null)
				setProperty(TutorActionLogV4.REPLAY, logV4.getReplay());
		}
		if (HINT_REQUEST.equals(getSemanticEvt(logMsg))) {
			createPropertyList(logMsg);
			setMessageType(MsgType.INTERFACE_ACTION);
			reviseSAIForHintRequest();
		} else {
			if (TutorActionLog.TOOL_MSG_ELEMENT.equals(logMsg.getTopElementType()))
				setMessageType(MsgType.INTERFACE_ACTION);
			else if (TutorActionLog.TUTOR_MSG_ELEMENT.equals(logMsg.getTopElementType()))
				setMessageType(MsgType.ASSOCIATED_RULES);
			createPropertyList(logMsg);
		}
		if(logMsg instanceof TutorActionLogV4)
			this.logMsg = (TutorActionLogV4) logMsg;
	}

	/**
	 * Fix-up method for messages built from OLI log elements.
	 */
	private void reviseSAIForHintRequest() {
		String action0 = getAction0();
		if ("ButtonPressed".equalsIgnoreCase(action0))  // no-op presume button is hint button
			return;
		Vector<String> selections = getSelection();
		Vector<String> actions = getAction();
		Vector<String> inputs = getInput();

		if (selections == null) selections = new Vector<String>();
		if (actions == null) actions = new Vector<String>();
		if (inputs == null) inputs = new Vector<String>();
		
		selections.add(0, "Hint");
		actions.add(0, "ButtonPressed");
		inputs.add(0, "-1");
		
		setSelection(selections);
		setAction(actions);
		setInput(inputs);
	}

	/**
	 * Generate our property list from a TutorActionLog message. 
	 * 
	 * @param logMsg TutorActionLog instance to translate
	 * @param mo native Comm message to populate
	 */
	protected void createPropertyList(TutorActionLog logMsg) {
		
		setProblemName(logMsg.getProblemName());
		
		String tid = logMsg.getTransactionId();
		if (tid != null && tid.length() > 0) {
			setTransactionId(tid);
		}

		Iterator it = logMsg.actionEvaluationsIterator();
		if (it.hasNext()) {
			ActionEvaluation ae = (ActionEvaluation) it.next();
			setProperty("Indicator", ae.getText().trim());
		}

		it = logMsg.eventDescriptorsIterator();
		if (it.hasNext()) {
			Vector selections = new Vector();
			Vector actions = new Vector();
			Vector inputs = new Vector();
			do {
				TutorActionLog.EventDescriptor ed = (TutorActionLog.EventDescriptor) it.next();
				selections.addAll(makeStringVector(ed.getSelections()));
				actions.addAll(makeStringVector(ed.getActions()));
				inputs.addAll(makeStringVector(ed.getInputs()));
			} while (it.hasNext());
			setSelection(selections);
			setAction(actions);
			setInput(inputs);
		}

		it = logMsg.tutorAdvicesIterator();
		if (it.hasNext()) {
			if (getSemanticEvt(logMsg) != null && getSemanticEvt(logMsg).toLowerCase().contains("hint")) {
				// add hint messages separately, in a vector
				Vector advices = new Vector();
				do {
					advices.add(it.next());
				} while (it.hasNext());
				setProperty(PseudoTutorMessageBuilder.TUTOR_ADVICE, advices);
			} else {
				// concatenate success or buggy messages
				StringBuffer advices = new StringBuffer((String) it.next());
				while (it.hasNext())
					advices.append(' ').append(it.next());
				setProperty(PseudoTutorMessageBuilder.TUTOR_ADVICE, s2v(advices.toString()));
			}
		}
		
		// Prior to DataShop v4, store only the rule names
		if (!(logMsg instanceof TutorActionLogV4)) {
			it = logMsg.skillsIterator();
			if (it.hasNext() ) {
				Vector ruleNames = new Vector();
				do {
					TutorActionLog.Skill skill = (TutorActionLog.Skill) it.next();
					ruleNames.add(skill.text);
				} while (it.hasNext());
				setProperty(PseudoTutorMessageBuilder.RULES_PNAME, ruleNames);
			}
			return;
		}	// from here on, can assume logMsg is TutorActionLogV4

		Message msg = ((TutorActionLogV4) logMsg).getMsg();
		if (msg instanceof TutorMessage) {
			Vector skills = new Vector();
			Vector ruleNames = new Vector();
			List skillList = ((TutorMessage) msg).getSkillList(); 
			for (Iterator itSkill = skillList.iterator(); itSkill.hasNext(); ) {
				SkillElement skill = (SkillElement) itSkill.next();
				String name = skill.getName();
				if (name == null || name.length() < 1)
					name = "unnamed";
				String category = skill.getCategory();
				if (category != null && category.length() > 0)
					name = name + " " + category;
				if (name == null || name.trim().length() < 1)
					name = "unnamed";
				ruleNames.add(name);
				if ("unnamed".equalsIgnoreCase(name)) 
					continue;                          // don't create unnamed skills
				try {
					String probability = skill.getProbability();
					float p = Float.parseFloat(probability);
					skills.add(name + Skill.SKILL_BAR_DELIMITER + probability + Skill.SKILL_BAR_DELIMITER + "0");
				} catch (NullPointerException npe) {
				} catch (NumberFormatException nfe) {
				}
			}
			if (ruleNames.size() > 0)
				setProperty(PseudoTutorMessageBuilder.RULES_PNAME, ruleNames);
			if (skills.size() > 0)
				setProperty(PseudoTutorMessageBuilder.SKILLS_PNAME, ruleNames);
		}
	}

	/**
	 * @return semantic_event element's name; null if not event
	 */
	private String getSemanticEvt(TutorActionLog logMsg) {
		if (logMsg == null)
			return null;
		TutorActionLog.SemanticEvent se = logMsg.getSemanticEvent(null);  // null: get first
		if (se == null)
			return null;
		else
			return se.getName();
	}

	/**
	 * Return a Vector of Strings from the given List of TextIsString elements.
	 * 
	 * @param list
	 *            List of TextIsString elements
	 * @return Vector with String elements
	 */
	public static Vector makeStringVector(List list) {
		Vector result = new Vector();
		for (Iterator it = list.iterator(); it.hasNext();)
			result.add(((TutorActionLog.TextIsString) it.next()).toString());
		if (result.size() < 1)
			result.add("");
		return result;
	}

	/** @return userId from {@link #logMsg}; null if unset */
	public String getUserId() {
		return getLogMsg().getUserGuid();
	}

	/**
	 * Set userId property}.
	 * @param new value for {@link #logMsg}setUserGuid()
	 */
	public void setUserId(String userId) {
		getLogMsg().setUserGuid(userId);
	}

	/** @return sessionId from {@link #logMsg}; null if unset */
	public String getSessionId() {
		return getLogMsg().getSessionId();
	}

	/**
	 * Set sessionId property}.
	 * @param new value for {@link #logMsg}setSessionId()
	 */
	public void setSessionId(String sessionId) {
		getLogMsg().setSessionId(sessionId);
	}

	/**
	 * Set timeStamp property}.
	 * @param new value for {@link #logMsg}setTimeStamp()
	 */
	public void setTimeStamp(Date timeStamp) {
		getLogMsg().setTimeStamp(timeStamp);
	}

	/**
	 * Return the current problem name.
	 * @return value of {@link #problemName}
	 */
	public String getProblemName() {
		return problemName;
	}

	/**
	 * @param newProblemName new value for {@link #problemName}
	 */
	void setProblemName(String newProblemName) {
		problemName = newProblemName;
	}}
