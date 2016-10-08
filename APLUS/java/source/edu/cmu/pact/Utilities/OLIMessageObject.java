package edu.cmu.pact.Utilities;

import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.oli.log.client.ActionLog;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Log.TutorActionLog;
import edu.cmu.pact.Log.TutorActionLogV4;
import edu.cmu.pact.Utilities.OLIMessageConverters.Conv;
import edu.cmu.pact.Utilities.OLIMessageConverters.HintConv;
import edu.cmu.pact.Utilities.OLIMessageConverters.StartProblemConv;
import edu.cmu.pslc.logging.LogContext;

/**
 * A MessageObject derived from an XML element adhering to the OLI logging
 * specification.
 * <p>
 * Tool-tutor protocol states and logging
 * </p>
 * <table>
 * <tr>
 * <th>Use
 * <th>Context
 * <th>Native
 * <th>OLI
 * <th>Other
 * <tr>
 * <td>Author
 * <td>Before start state
 * <td>?!!!
 * <td>ATTEMPT
 * <td>
 * <tr>
 * <td>Author
 * <td>Begin start state
 * <td>?!!!
 * <td>START_TUTOR
 * <td>
 * <tr>
 * <td>Author
 * <td>
 * <td>?!!!
 * <td>DEFINE_WIDGET
 * <td>
 * <tr>
 * <td>Author
 * <td>Ser state
 * <td>?!!!
 * <td>ATTEMPT
 * <td> </table>
 */
public class OLIMessageObject extends MessageObject {

	/**
	 * DTDv2-specific converter for StartProblem and StartTutor messages.
	 */
	static class StartProblemConvV2 extends StartProblemConv {

		/**
		 * @param semanticEvt
		 * @param actionEvaluation
		 * @param commMsgType
		 * @param logger
		 */
		StartProblemConvV2(String semanticEvt, String actionEvaluation,
				String commMsgType, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, logger);
		}

		/**
		 * Return the DTD version-specific type of {@link TutorActionLog}.
		 * @return instance of TutorActionLog
		 * @see edu.cmu.pact.Utilities.OLIMessageConverters.StartProblemConv#createTutorActionLogInstance()
		 */
		protected TutorActionLog createTutorActionLogInstance(LogContext logger) {
			logger.resetAttemptId(); // new attempt_id per problem
			return new TutorActionLog(TutorActionLog.CURRICULUM_MSG_ELEMENT);			// TODO Auto-generated method stub
		}
		
		/** Set {@link OLIMessageObject#problemName} in parent class. */
		protected TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
				MessageObject mo, boolean tutorToTool, LogContext logger) {
			TutorActionLog result = createTutorActionLogInstance(logger);
			Object pnObj = OLIMessageObject.getValue(propertyNames, propertyValues, PROBLEM_NAME);
			String localProblemName = (String) (pnObj == null ? "null" : pnObj.toString());
//			FIXME set in Logger?
//			OLIMessageObject.setProblemName(localProblemName); // !!!set in parent
			result.setProblemName(localProblemName);
			
			result.setAttemptId(logger.getContextMessage().getContextMessageId());
	
			String school = (String) OLIMessageObject.getValue(propertyNames, propertyValues,
					"SchoolName");
			if (school == null)
				school = logger.getSchoolName();
			if (school != null)
				result.setSchoolName(school);
			String course = (String) OLIMessageObject.getValue(propertyNames, propertyValues,
					"CourseName");
			if (course == null)
				course = logger.getCourseName();
			if (course != null)
				result.setCourseName(course);
			String unit = (String) OLIMessageObject.getValue(propertyNames, propertyValues,
					"UnitName");
			if (unit == null)
				unit = logger.getUnitName();
			if (unit != null)
				result.setUnitName(unit);
			String section = (String) OLIMessageObject.getValue(propertyNames, propertyValues,
					"SectionName");
			if (section == null)
				section = logger.getSectionName();
			if (section != null)
				result.setSectionName(section);
			return result;
		}

	}

	/** Specific converter for SkillsFileSuccess messages. */
	static class SkillsConvV2 extends ConvV2 {
	
		SkillsConvV2(String semanticEvt, String actionEvaluation,
				String commMsgType, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, null, null, logger);
		}
	
		/** Set {@link OLIMessageObject#problemName} in parent class. */
		protected TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
				MessageObject mo, boolean tutorToTool, LogContext logger) {
			TutorActionLog result = super.native2OLI(propertyNames,
					propertyValues, mo, tutorToTool, logger);
			String indicator = (String) OLIMessageObject.getValue(propertyNames, propertyValues,
					INDICATOR_PNAME);
			if (indicator != null)
				result.addActionEvaluation(indicator.toUpperCase());
			return result;
		}
	
		/** Set up an AssociatedRules message. */
		protected void OLI2Native(TutorActionLog logMsg, OLIMessageObject mo) {
	
			super.OLI2Native(logMsg, mo);
			Iterator it = logMsg.actionEvaluationsIterator();
			if (it.hasNext()) {
				TutorActionLog.ActionEvaluation ae = (TutorActionLog.ActionEvaluation) it
						.next();
				String indicator = OLIMessageObject.initialCapital(ae.getText());
				if (indicator.length() > 0)
					OLIMessageObject.setProperty(mo, INDICATOR_PNAME, indicator);
			}
			
			it = logMsg.skillsIterator();
			Vector ruleNames = new Vector();
			if (it.hasNext() ) {
				do {
					TutorActionLog.Skill skill = (TutorActionLog.Skill) it.next();
					ruleNames.add(skill.text);
				} while (it.hasNext());
			}
			if (ruleNames.size() > 0)
				mo.setProperty(RULES_PNAME, ruleNames);
		}
	}
	
	/**
	 * Specific converter for LoadBRDFileSuccess messages. 
	 */
	static class LoadBRDConvV2 extends ConvV2 {

		/**
		 * @param semanticEvt
		 * @param actionEvaluation
		 * @param commMsgType
		 * @param logger
		 */
		public LoadBRDConvV2(String semanticEvt, String actionEvaluation,
				String commMsgType, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, null,
					null, logger);
			// TODO Auto-generated constructor stub
		}

		/**
		 * Set {@link OLIMessageObject#problemName} in parent class.
		 * @param propertyNames
		 * @param propertyValues
		 * @param mo
		 * @param tutorToTool
		 * @param logger source of context info
		 * @return
		 * @see edu.cmu.pact.Utilities.OLIMessageConverters.LoadBRDConv#native2OLI(java.util.Vector, java.util.Vector, edu.cmu.old_pact.dormin.MessageObject, boolean)
		 */
		protected TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
				MessageObject mo, boolean tutorToTool, LogContext logger) {
			TutorActionLog result = super.native2OLI(propertyNames,
					propertyValues, mo, tutorToTool, logger);
			result.setAttemptId(logger.getContextMessage().getContextMessageId());
	
			String localProblemName = (String) OLIMessageObject.getValue(propertyNames,
					propertyValues, LoadBRD_PNAME);
//			FIXME set in Logger?
//			OLIMessageObject.setProblemName(localProblemName); // !!!set in parent
			result.setProblemName(localProblemName);
			result.addEventDescriptor(getSemanticEvtId(), "", localProblemName, "");
			return result;
		}
		
		/** Set up a StartProblem message. */
		protected void OLI2Native(TutorActionLog logMsg, OLIMessageObject mo) {
			super.OLI2Native(logMsg, mo);
			String localProblemName = logMsg.getSelectionText(0);
//			FIXME set in Logger?
//			OLIMessageObject.setProblemName(localProblemName);
			mo.setProblemName(localProblemName);
			OLIMessageObject.setProperty(mo, LoadBRD_PNAME, localProblemName);
		}
	}

	/**
	 * A DataShop DTDv2 converter for most messages.
	 */
	static class ConvV2 extends Conv {

		/**
		 * @param semanticEvt
		 * @param actionEvaluation
		 * @param commMsgType
		 * @param advicePname
		 * @param tutorToToolRestriction
		 * @param logger
		 */
		public ConvV2(String semanticEvt, String actionEvaluation,
				String commMsgType, String advicePname,
				Boolean tutorToToolRestriction, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, advicePname,
					tutorToToolRestriction, logger);
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * Create a TutorActionLog instance of the proper type.
		 * @return 
		 */
		protected TutorActionLog getTutorLogMessage() {
			return new TutorActionLog(TutorActionLog.TUTOR_MSG_ELEMENT);
		}
		
		/**
		 * Build a tool_ or tutor_message from the name-value pairs in a native
		 * Comm message. Special cases:
		 * <ul>
		 * <li>if mo is a hint message, return
		 * {@link OLIMessageConverters.HintConv#native2OLI}
		 * <li>if tutorToTool fails to match {@link #tutorToToolRestriction},
		 * return {@link OLIMessageConverters.OtherMsgConv#native2OLI}
		 * </ul>
		 * 
		 * @param propertyNames
		 *            List of names, 1:1 with propertyValues
		 * @param propertyValues
		 *            List of values, 1:1 with propertyNames
		 * @param mo
		 *            original MessageObject
		 * @param tutorToTool
		 *            true if message was from tutor to tool; false if from tool
		 *            to tutor
		 * @param logger source of context info
		 * @return TutorActionLog instance with translated data
		 */
		protected TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
				MessageObject mo, boolean tutorToTool, LogContext logger) {
			OLIMessageConverters convs = getConvs();
			if (HintConv.isHintMsg(mo))
				return convs.getHintConv().native2OLI(propertyNames, propertyValues, mo,
						tutorToTool, logger);
			if (getTutorToToolRestriction() != null
					&& getTutorToToolRestriction().booleanValue() != tutorToTool)
				return convs.getOtherMsgConv().native2OLI(propertyNames, propertyValues,
						mo, tutorToTool, logger);
			TutorActionLog result = null;
			if (tutorToTool)
				result = new TutorActionLog(TutorActionLog.TUTOR_MSG_ELEMENT);
			else
				result = new TutorActionLog(TutorActionLog.TOOL_MSG_ELEMENT);

			result.setProblemName(logger.getProblemName());
			result.setAttemptId(logger.getContextMessage().getContextMessageId());
			setSemanticEvtId(result.addSemanticEvent(getSemanticEvt(), mo
					.getSemanticEventId(), mo.getLinkedSemanticEventId()));
			if (getActionEvaluation() != null)
				result.addActionEvaluation(getActionEvaluation());
			createEventDesc(propertyNames, propertyValues, result);
			createTutorAdvices(propertyNames, propertyValues, result);
			createSkills(propertyNames, propertyValues, result);
			return result;
		}

	}

	/** {@link MessageObject} parameter holding vector of property names. */
	public static final String PROPERTYNAMES = "PROPERTYNAMES";

	/** {@link MessageObject} parameter holding vector of property values. */
	public static final String PROPERTYVALUES = "PROPERTYVALUES";

	/** Comm message type for most interface actions. */
	public static final String INTERFACE_ACTION = "InterfaceAction";

	public static final String GLOSSARY = "Glossary";
	public static final String COGNITIVE_LOAD = "CognitiveLoad";

	/** ProblemName for this series of messages. */
	private String problemName = "";

	/** Source of context. */
	protected LogContext logger;

	/** Underlying message from XML. */
	private TutorActionLog logMsg = null;
	
	/** Access to converters stored in {@link Logger}. */
	private static OLIMessageConverters convs;

	/** XML Element from which this instance was parsed, as a string. */
	private String originalElementString;

	/**
	 * An iterator to create a TutorActionLog from XML elements in a stream. The
	 * next() method will call the {@link OLIMessageObject} constructor on the
	 * current child of the stream's root element.
	 */
	private static class Factory implements Iterator {

		/**
		 * Iterator from {@link edu.cmu.oli.log.TutorActionLog#factoryIterator}.
		 * Delegate for Iterator methods.
		 */
		private Iterator iterator;

		/** Shared proxy object for all instances returned. */
		private ObjectProxy top;

		private BR_Controller controller;

		/**
		 * Constructor builds Document from Reader, saves Iterator. See
		 * {@link edu.cmu.oli.log.TutorActionLog.Factory}.
		 * 
		 * @param rdr Reader for {@link builder}
		 * @param dtdVersion if not null, protocol version from higher-level element
		 * @param top argument for superclass constructor
		 *            {@link MessageObject#MessageObject(ObjectProxy)}
		 * @param controller
		 */
		private Factory(Reader rdr, String dtdVersion, ObjectProxy top, BR_Controller controller) {
			this.top = top;
			this.controller = controller;
			iterator = TutorActionLog.factoryIterator(rdr, dtdVersion, null);
		}

		/** Returns state of {@link #iterator}. */
		public boolean hasNext() {
			return iterator.hasNext();
		}

		/** Calls constructor, passes next element in {@link #iterator}. */
		public Object next() {
			TutorActionLog logMsg = (TutorActionLog) iterator.next();
			if (trace.getDebugCode("log")) trace.out("log", "OLIMessageObject.Factory.next(): logMsg class "+logMsg.getClass());
			if (logMsg instanceof TutorActionLogV4)
				return new OLIMessageObjectV4(logMsg, top, controller);
			else
				return new OLIMessageObject(logMsg, top, controller);
		}

		/** always throws {@link java.lang.UnsupportedOperationException} */
		public void remove() {
			throw new UnsupportedOperationException("cannot remove");
		}
	}

	/**
	 * Find a converter given a native message.
	 * @param mo message to convert
	 * @return {@link OLIMessageConverters.Conv} that knows how to convert messages of this type
	 */
	public OLIMessageConverters.Conv getNative2OLIConverter(MessageObject mo) {
		return getConvs().getNative2OLIConverter(mo);
	}

	/**
	 * Find a converter given an OLI semantic event and action evaluation.
	 * 
	 * @param oli
	 *            OLI SemanticEvent text
	 * @param oliEval
	 *            OLI ActionEvaluation text; if null, omitted for lookup
	 * @return {@link OLIMessageConverters.Conv} that knows how to convert messages of this type
	 */
	public OLIMessageConverters.Conv getOLI2NativeConverter(String oli, String oliEval) {
		if (oli == null || getConvs()==null)
			return null;
		String key = oli;
		if (oliEval != null)
			key = (key + " " + oliEval);
		key = key.toUpperCase();
		OLIMessageConverters.Conv conv = (OLIMessageConverters.Conv)getConvs().getMapOLI2Native().get(key);
		if (conv == null || conv.getCommMsgType() == null) {
			System.err.println("no native mapping for OLI event " + oli
					+ ", actionEvaluation " + oliEval);
			conv = new ConvV2(oli.toUpperCase(), oliEval, oli, null, null, logger);
			getConvs().getMapOLI2Native().put(key, conv);
		}
		return conv;
	}

	// /////////////////////////////////////////////////////////////////////
	/**
	 * Constructor for already-built Log Message.
	 * 
	 * @param logMsg
	 *            TutorActionLog to incorporate: maintains reference-- does not
	 *            make copy
	 * @param top
	 *            argument for superclass constructor
	 *            {@link MessageObject#MessageObject(ObjectProxy)}
	 */
	// /////////////////////////////////////////////////////////////////////
	protected OLIMessageObject(TutorActionLog logMsg, ObjectProxy top, BR_Controller controller) {
		super(top);
		this.setLogMsg(logMsg);
		logger = controller==null ? null : controller.getLogger();
		setConvs(initConvs(logger));
		if (trace.getDebugCode("mo")) trace.out("mo", "creating message object: top proxy = " + top
				+ ", logMsg\n" + logMsg);
		init();
	}

	/**
	 * Get the first message from an XML string and convert it to an OLIMessageElement.
	 * @param xmlStr
	 * @param dtdVersion if not null, protocol version from higher-level element
	 * @param top
	 * @param controller
	 * @return result of {@link OLIMessageObject.Factory#next()}
	 */
	public static OLIMessageObject factory(String xmlStr, String dtdVersion, ObjectProxy top,
			BR_Controller controller)
	{
		Iterator it = factoryIterator(new StringReader(xmlStr), dtdVersion, top, controller);
		return (OLIMessageObject) it.next();
	}

	/**
	 * Create a factory that will return a Iterator whose next() method will
	 * (successively) create TutorActionLog instances for each log element in
	 * the given Reader.
	 * 
	 * @param rdr input XML content
	 * @param dtdVersion if not null, protocol version from higher-level element
	 * @param top argument for superclass constructor
	 *            {@link MessageObject#MessageObject(ObjectProxy)}
	 * @return Iterator whose next() method returns a TutorActionLog instance
	 */
	// ////////////////////////////////////////////////////////////////////
	public static Iterator factoryIterator(Reader rdr, String dtdVersion, ObjectProxy top,
			BR_Controller controller) {
		return new Factory(rdr, dtdVersion, top, controller);
	}


    // /////////////////////////////////////////////////////////////////////
    /**
     * Constructor for XML Element input.
     * 
     * @param elt
     *            XML log element to analyze
     * @param top
     *            argument for superclass constructor
     *            {@link MessageObject#MessageObject(ObjectProxy)}
     */
    // /////////////////////////////////////////////////////////////////////
    protected OLIMessageObject(Element elt, ObjectProxy top, BR_Controller controller) {
    	this(elt, top, controller==null ? null : controller.getLogger());
    }
    
    /**
     * Constructor for XML Element input.
     * @param elt XML log element to analyze
     * @param top argument for superclass constructor
     *            {@link MessageObject#MessageObject(ObjectProxy)}
     * @param logger source of context info
     */
    // /////////////////////////////////////////////////////////////////////
    protected OLIMessageObject(Element elt, ObjectProxy top, LogContext logger) {
        super(top);
        try {
            setLogMsg(createTutorActionLog(elt));
        } catch (Exception e) {
            e.printStackTrace();
            setParsingException(new DorminException(e.toString()));
            return;
        }
		this.logger = logger;
        setConvs(initConvs(logger));
        if (trace.getDebugCode("mo")) trace.out("mo", "creating message object: element = " + elt
                + ", top proxy = " + top);
        init();
    }

    /**
     * @param elt &lt;tool_message&gt;, etc., instance
     * @return {@link TutorActionLog} instance
     */
    protected TutorActionLog createTutorActionLog(Element elt) {
    	return new TutorActionLog(elt, null);
	}

	// /////////////////////////////////////////////////////////////////////
	/**
	 * Constructor for existing native message object.
	 * 
	 * @param mo
	 *            native MessgeObject
	 * @param tutorToTool
	 *            true if message was from tutor to tool; false if from tool to
	 *            tutor
	 */
	// /////////////////////////////////////////////////////////////////////
	public OLIMessageObject(MessageObject mo, boolean tutorToTool,
			BR_Controller controller) {
		this(mo, tutorToTool, controller.getLogger());
	}
	
	/**
	 * Constructor for existing native message object.
	 * @param mo  native MessgeObject
	 * @param tutorToTool true if message was from tutor to tool;
	 *                    false if from tool to tutor
	 * @param logger source of context info
	 */
	// /////////////////////////////////////////////////////////////////////
	public OLIMessageObject(MessageObject mo, boolean tutorToTool, LogContext logger) {
		super(mo.toString(), mo.getTopProxy());
        this.logger = logger;
		setConvs(initConvs(logger));
		Vector propertyNames = null;
		Vector propertyValues = null;
		try {
			propertyNames = mo.extractListValue(PROPERTYNAMES);
			propertyValues = mo.extractListValue(PROPERTYVALUES);
		} catch (DorminException e) {
			e.printStackTrace();
			setParsingException(e);
			return;
		}
		setSemanticEventId(mo.getSemanticEventId());
		setLinkedSemanticEventId(mo.getLinkedSemanticEventId());

		OLIMessageConverters.Conv conv = getNative2OLIConverter(mo);
		if (trace.getDebugCode("mo")) trace.out("mo", "messageType " + mo.getMessageTypeProperty() + ", conv " + conv
				+ ", linkedSemanticEventId " + mo.getLinkedSemanticEventId());
		setLogMsg(conv.native2OLI(propertyNames, propertyValues, mo, tutorToTool, logger));
		setUserId(logger.getStudentName());
		setSessionId(logger.getSessionId());
		getLogMsg().setSourceId(logger.getSourceId());
	}

	/**
	 * Constructor for XML String input.
	 * 
	 * @param xmlStr String with complete XML file, including at least one message
	 * @param dtdVersion optional protocol version from higher-level element
	 * @param top argument for superclass constructor
	 *            {@link MessageObject#MessageObject(ObjectProxy)}
	 * @param logger for message conversion
	 */
	protected OLIMessageObject(String xmlStr, String dtdVersion, ObjectProxy top,
			LogContext logger) {
		super(top);
		try {
			setLogMsg(TutorActionLog.factory(xmlStr, dtdVersion, null));
		} catch (Exception e) {
			e.printStackTrace();
			setParsingException(new DorminException(e.toString()));
			return;
		}
		this.logger = logger;
		setConvs(initConvs(logger));
		if (trace.getDebugCode("mo")) trace.out("mo", "creating message object: String " + xmlStr + "\n top proxy = " + top);
		init();
	}

	// /////////////////////////////////////////////////////////////////////
	/**
	 * Common code for constructors.
	 */
	// /////////////////////////////////////////////////////////////////////
	private void init() {

		OLIMessageConverters.Conv conv = null;
		Iterator it = getLogMsg().semanticEventsIterator();
		if (it.hasNext()) {
			TutorActionLog.SemanticEvent se = (TutorActionLog.SemanticEvent) it.next();
			if (se != null) {
				setSemanticEventId(se.getId());
				setLinkedSemanticEventId(se.getLinkedEventId());
			}
			it = getLogMsg().actionEvaluationsIterator();
			if (it.hasNext()) {
				TutorActionLog.ActionEvaluation ae = (TutorActionLog.ActionEvaluation) it.next();
				conv = getOLI2NativeConverter(se.getName(), ae.getText());
			} else
				conv = getOLI2NativeConverter(se.getName(), null);
		} else
			conv = getConvs().getOtherMsgConv();
        if (conv!=null) {
            if (trace.getDebugCode("log")) trace.out("log", "OLIMsgObj.init(): getLogMsg() "+
                      (getLogMsg() == null ? "null" : getLogMsg().getClass())+", conv "+conv.getClass());
            conv.OLI2Native(getLogMsg(), this);
        }
	}

	/**
	 * Get the {@link edu.cmu.oli.log.client.ActionLog} object. Updates info
	 * field with {@link #toXML()}, then returns {@link #logMsg}.
	 * 
	 * @return value of {@link #logMsg}; null if none
	 */
	public ActionLog getLogObject() {
		if (getLogMsg() != null)
			getLogMsg().setInfo(toXML());
		return getLogMsg();
	}

	/**
	 * String representation as XML pretty-print. See
	 * {@link edu.cmu.oli.log.TutorActionLog#toString()}. return
	 * {@link #logMsg}.{@link edu.cmu.oli.log.TutorActionLog#toString()};
	 * empty string if logMsg null
	 */
	public String toXML() {
		if (getLogMsg() == null)
			return "";
		else
			return getLogMsg().toString();
	}

	/**
	 * Return the current problem name.
	 * 
	 * @return value of {@link #problemName}
	 */
	public String getProblemName() {
		return problemName;
	}

	/**
	 * Set the current problem name.
	 * 
	 * @param newProblemName
	 *            new value for {@link #problemName}
	 */
	void setProblemName(String newProblemName) {
		problemName = newProblemName;
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
	 * 
	 * @param new
	 *            value for {@link #logMsg}setUserGuid()
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
	 * 
	 * @param new
	 *            value for {@link #logMsg}setSessionId()
	 */
	public void setSessionId(String sessionId) {
		getLogMsg().setSessionId(sessionId);
	}

	/** @return timeStamp from {@link #logMsg}; null if unset */
	public Date getTimeStamp() {
		return getLogMsg().getTimeStamp();
	}

	/**
	 * Set timeStamp property}.
	 * 
	 * @param new
	 *            value for {@link #logMsg}setTimeStamp()
	 */
	public void setTimeStamp(Date timeStamp) {
		getLogMsg().setTimeStamp(timeStamp);
	}

	/**
	 * Set a String to Titlecase, with the initial char upper case and the rest
	 * lower case.
	 * 
	 * @param s
	 *            String to operate on
	 * @return same String with case modified
	 */
	public static String initialCapital(String s) {
		if (s == null || s.length() < 1)
			return s;
		StringBuffer result = new StringBuffer(s.toLowerCase());
		result.replace(0, 1, s.substring(0, 1).toUpperCase());
		return s.toString();
	}

	/**
	 * Get a property value from the PROPERTY lists of a Comm message. A
	 * property is a (name,value) pair encoded in the {@link #PROPERTYNAMES} and
	 * {@link #PROPERTYVALUES} parameters of the message.
	 * 
	 * @param mo
	 *            MessageObject to read
	 * @param propertyName
	 *            property's name
	 * @return value of the named element; null if property name is not found
	 */
	public static Object getPropertyValue(MessageObject mo, String propertyName) {
		int pIndex = -1;
		Object propertyValue = null;
		try {
			Vector pNames = (Vector) mo.getParameter(PROPERTYNAMES);
			Vector pValues = (Vector) mo.getParameter(PROPERTYVALUES);
			pIndex = pNames.indexOf(propertyName);
			if (pIndex < 0) // didn't find property
				return null;
			return pValues.get(pIndex);
		} catch (edu.cmu.old_pact.dormin.MissingParameterException mpe) {
			RuntimeException re = new RuntimeException(mpe.getClass().getName()
					+ ": " + mpe.getMessage());
			re.setStackTrace(mpe.getStackTrace());
			throw re;
		} catch (ArrayIndexOutOfBoundsException obe) {
			RuntimeException re = new RuntimeException(
					"Comm msg property list error: name index " + pIndex
							+ " not in values list; " + obe.getMessage());
			re.setStackTrace(obe.getStackTrace());
			throw re;
		}
	}

	/**
	 * Get a property element value from the PROPERTY lists of a Comm message.
	 * A property is a (name,value) pair; if the property value is a List, then
	 * this method returns the indexed element of the List.
	 * 
	 * @param mo
	 *            MessageObject to read
	 * @param propertyName
	 *            property's name
	 * @param index
	 *            element index
	 * @return value of the indexed element; null if the property name is not
	 *         found, the value is not a List or the index is out of bounds
	 */
	public static Object getPropertyElement(MessageObject mo,
			String propertyName, int index) {
		Object propertyValue = getPropertyValue(mo, propertyName);
		if (!(propertyValue instanceof List)) // prop not found or not a list
			return null;
		List v = (List) propertyValue;
		if (index < 0 || v.size() <= index) // index is out of bounds
			return null;
		return v.get(index);
	}

	/**
	 * Insert a property element value into the PROPERTY lists of a Comm
	 * message. A property is a (name,value) pair. If the property value is a
	 * List, then this method inserts this value at the given index of the List.
	 * 
	 * @param mo
	 *            MessageObject to read
	 * @param propertyName
	 *            property's name
	 * @param index
	 *            element index
	 * @return value of the indexed element; null if the property name is not
	 *         found, the value is not a List or the index is out of bounds
	 */
	public static void insertPropertyElement(MessageObject mo,
			String propertyName, int index, String elementValue) {
		Object propertyValue = getPropertyValue(mo, propertyName);
		if (!(propertyValue instanceof List)) // prop not found or not a list
			return;
		List v = (List) propertyValue;
		v.add(index, elementValue);
	}

	/**
	 * Set a property (name,value) pair in the PROPERTY lists of a Comm
	 * message.
	 * 
	 * @param mo
	 *            MessageObject to modify
	 * @param propertyName
	 *            new property's name
	 * @param propertyValue
	 *            new property's value
	 */
	public static void setProperty(MessageObject mo, String propertyName,
			Object propertyValue) {
		int pIndex = -1;
		try {
			Vector pNames = (Vector) mo.getParameter(PROPERTYNAMES);
			Vector pValues = (Vector) mo.getParameter(PROPERTYVALUES);
			pIndex = pNames.indexOf(propertyName);
			if (pIndex < 0) { // didn't find property: add it
				pNames.add(propertyName);
				pValues.add(propertyValue);
			} else {
				pValues.set(pIndex, propertyValue);
			}
		} catch (edu.cmu.old_pact.dormin.MissingParameterException mpe) {
			RuntimeException re = new RuntimeException(mpe.getClass().getName()
					+ ": " + mpe.getMessage());
			re.setStackTrace(mpe.getStackTrace());
			throw re;
		}
	}

	/**
	 * Extract the desired value from propertyValues and return it
	 * 
	 * @param propertyNames
	 *            Property name vector from Comm message
	 * @param propertyValues
	 *            Property value vector from Comm message
	 * @param propertyName
	 *            The property name of the value being sought
	 * @return property value requested, or null if not found
	 */
	public static Object getValue(Vector propertyNames, Vector propertyValues,
			String propertyName) {
		int pos = fieldPosition(propertyNames, propertyName);
		if (pos != -1)
			return propertyValues.elementAt(pos);
		else
			return null;
	}

	/**
	 * Extract a field position from a comm message vector.
	 * 
	 * @param from
	 *            Vector of String instances to scan
	 * @param fieldName
	 *            name to match in from vector, case-insensitive
	 * @return index of fieldName in from vector; -1 if not found
	 */
	public static int fieldPosition(Vector from, String fieldName) {
		int result = -1;
		int s = from.size();
		for (int i = 0; i < s; i++) {
			if (((String) from.elementAt(i)).equalsIgnoreCase(fieldName))
				return i;
		}
		return result;
	}

	/**
	 * Return the default session identifier.
	 * 
	 * @return value of {@link #controller.getLogger().getSessionId()}
	 */
	public String getDefaultSessionID() {
		return logger.getSessionId();
	}

	/**
	 * Generate a globally-unique identifier.
	 * 
	 * @return result of {@link Logger#generateGUID()}
	 */
	public static String generateGUID() {
		return Logger.generateGUID(); // !!!stub implementation
	}

	/**
	 * The bundle of DataShop DTDv2 converters.
	 */
	static class ConvertersV2 extends OLIMessageConverters {
		
		/**
		 * Constructor sets all fields.
		 * @param logger reference for {@link OLIMessageConverters.Conv} constructors.
		 */
		ConvertersV2(LogContext logger) {
			super(new OLIMessageConverters.OtherMsgConv(logger),
					new OLIMessageConverters.HintConv("HINT_REQUEST", null, INTERFACE_ACTION, logger),
					new HashMap(),
					new HashMap());

			/*
			 * Create both message type conversion maps from a local list. Maintain
			 * the list in the case (upper, lower, mixed) you want for output. All
			 * lookups are case-insentive.
			 */
			Boolean F = Boolean.FALSE;
			Boolean T = Boolean.TRUE;

			OLIMessageConverters.Conv[] conv = {
				// Initialize entries in the case you want for output.
				// Semantic Evt ActionEval Comm MessageType
				new ConvV2("ATTEMPT", null, INTERFACE_ACTION, null, F, logger),
				new ConvV2("RESULT", "CORRECT", "CorrectAction", "SuccessMsg", T, logger),
				new ConvV2("RESULT", "INCORRECT", "IncorrectAction", "BuggyMsg", T, logger),
				new ConvV2("RESULT", "CORRECT", "SuccessMessage", "SuccessMsg", T, logger),
				new ConvV2("RESULT", "INCORRECT", "BuggyMessage", "BuggyMsg", T, logger),
				new ConvV2("RESULT", "INCORRECT", "NotDoneMessage", "Message", T, logger),
				new SkillsConvV2("RESULT", null, "AssociatedRules", logger),
				new StartProblemConvV2("START_TUTOR", null, "StartProblem", logger),
				new StartProblemConvV2("START_PROBLEM", null, "StartProblem", logger),
				new OLIMessageConverters.HintConv("HINT_MSG", "HINT", "ShowHintsMessage", logger),
				new LoadBRDConvV2("START_TUTOR", null, "LoadBRDFileSuccess", logger)
			};

			initConvMaps(conv);
		}
	}
	
	/**
	 * Initialize the set of {@link OLIMessageConverters.Conv} for this instance.
	 * Also calls {@link Logger#getOLIMessageConverters(OLIMessageConverters)}
	 * @param logger no-op if {@link Logger#getOLIMessageConverters()} is set;
	 *               otherwise calls {@link Logger#getOLIMessageConverters(OLIMessageConverters)}
	 *               with returned value
	 * @return OLIMessageConverters instance created
	 */
	protected OLIMessageConverters initConvs(LogContext logger) {
		if (logger==null)
            return null;
        
		synchronized(logger) {  // Make sure this only runs once
			TutorActionLog msg = getLogMsg();
			if(getConvs()==null)
				setConvs(new ConvertersV2(logger));
			return getConvs();
		}
	}

	/**
	 * @param logMsg new value for {@link #logMsg}
	 */
	protected void setLogMsg(TutorActionLog logMsg) {
		this.logMsg = logMsg;
	}

	/**
	 * @return the {@link #logMsg}
	 */
	protected TutorActionLog getLogMsg() {
		return logMsg;
	}

	/**
	 * @return the {@link #convs}
	 */
	protected static OLIMessageConverters getConvs() {
		return convs;
	}

	/**
	 * @param convs new value for {@link #convs}
	 */
	protected void setConvs(OLIMessageConverters convs) {
		OLIMessageObject.convs = convs;
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
}
