package edu.cmu.pact.Utilities;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.MissingParameterException;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.Log.TutorActionLog;
import edu.cmu.pslc.logging.LogContext;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.element.UiEventElement;

/**
 * A class to bundle the OLI message converters (instances of
 * {@link OLIMessageConverters.Conv}) used by all instances of
 * {@link OLIMessageObject} for this logger.
 */
public abstract class OLIMessageConverters {

	private static final String CONV_TO_DELIMITER = "=>";

	/** Value for {@link MessageObject#getConvertInstructions()}. */
	public static final String OTHER_MSG_CONV = "OtherMsgConv";

	/**
		 * For translation between OLI and native message types. See the static
		 * initializer for the enclosing class.
		 */
		static abstract class Conv {
			
			/** Native property name for skills. */
			protected static final String SKILLS_PNAME = "Skills";
			
			/** Previous native property name for skills. */
			protected static final String RULES_PNAME = "Rules";
			
			/** Native property name for action evaluation in S. */
			protected static final String INDICATOR_PNAME = "Indicator";

			/** Native property name for LoadBRD msgs. */
			protected static final String LoadBRD_PNAME = "BRDFilePath";

			/** OLI SemanticEvent text. */
			private final String semanticEvt;
	
			/** Unique identifier for semanticEvt. */
			private String semanticEvtId = "";
	
			/** OLI ActionEvaluation text. */
			private String actionEvaluation;
	
			/** Native Comm MessageType. */
			private final String commMsgType;
	
			/** Native Comm parameter name for tutor_advice element. */
			private String advicePname;
	
			/**
			 * Restriction on use. True if only tutor-To-Tool, false if only
			 * tool-to-tutor, null if unrestricted.
			 */
			private Boolean tutorToToolRestriction = null;
	
			/**
			 * Constructor sets all fields.
			 */
			protected Conv(String semanticEvt, String actionEvaluation, String commMsgType,
					String advicePname, Boolean tutorToToolRestriction, LogContext logger) {
				
				this.semanticEvt = semanticEvt;
				this.setActionEvaluation(actionEvaluation);
				this.commMsgType = commMsgType;
				this.setAdvicePname(advicePname);
				this.setTutorToToolRestriction(tutorToToolRestriction);
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
			protected abstract TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
					MessageObject mo, boolean tutorToTool, LogContext logger);

			/**
			 * Fill in the property list of a plain message.
			 * @param propertyNames
			 * @param propertyValues
			 * @param mo
			 * @param result
			 */
			protected void populateProperties(Vector propertyNames, Vector propertyValues,
					MessageObject mo, TutorActionLog result) {
				String verb = mo.getVerb();
				if (verb != null && verb.length() > 0)
					result.addMsgProperty("verb", verb);
		
				if (trace.getDebugCode("mo")) trace.out("mo", "propertyNames.size() " + propertyNames.size());
				for (int i = 0; i < propertyNames.size(); ++i) {
					String name = (String) propertyNames.get(i);
					Object value = (i < propertyValues.size() ? propertyValues
							.get(i) : "");
					if (value instanceof List)
						result.addMsgProperty(name, (List) value);
					else
						result.addMsgProperty(name, value.toString());
				}

			}
			/**
			 * Set the common fields in tutor messages.  This is a support method for 
			 * {@link OLIMessageConverters.HintConv#native2OLI(Vector, Vector, MessageObject, boolean)}
			 * @param propertyNames
			 * @param propertyValues
			 * @param result 
			 */
			protected void populateHintElements(Vector propertyNames, Vector propertyValues,
					TutorActionLog result) {
				int nAdvices = createTutorAdvices(propertyNames, propertyValues,
						result);
				Integer totalHints =
					(Integer) OLIMessageObject.getValue(propertyNames, propertyValues,
							HintMessagesManager.TOTAL_HINTS_AVAIABLE);
				Integer currentHintNumber =
					(Integer) OLIMessageObject.getValue(propertyNames, propertyValues,
							HintMessagesManager.CURRENT_HINT_NUMBER);
				if (trace.getDebugCode("mo")) trace.out("mo", "no. advices "+nAdvices+", totalHints "+totalHints+
						", currentHintNo "+currentHintNumber+", actionEvaluation "
						+ getActionEvaluation());
				result.addActionEvaluation(getActionEvaluation(),
						(currentHintNumber != null ? currentHintNumber.intValue() : 1),
						(totalHints != null ? totalHints.intValue() : nAdvices));
				createSkills(propertyNames, propertyValues, result);
			}
	
			/**
			 * Build a native Comm message from a TutorActionLog message.
			 * 
			 * @param logMsg
			 *            TutorActionLog instance to translate
			 * @param mo
			 *            native Comm message to populate
			 */
			protected void OLI2Native(TutorActionLog logMsg, OLIMessageObject mo) {
				if (getSemanticEvt().equals("HINT_REQUEST")) {
					OLIMessageObject.getConvs().getHintConv().OLI2Native(logMsg, mo);
					return;
				}
				createPropertyList(logMsg, mo);
			}
	
			/**
			 * Generate the property list for a native Comm message from a
			 * TutorActionLog message. The property list is a pair of Vectors
			 * PROPERTYNAMES and PROPERTYVALUES whose elements are 1:1, to form a
			 * list of (name, value) pairs.
			 * 
			 * @param logMsg
			 *            TutorActionLog instance to translate
			 * @param mo
			 *            native Comm message to populate
			 */
			protected void createPropertyList(TutorActionLog logMsg,
					OLIMessageObject mo) {
				boolean toAddParams = false;
				Vector pNames;
				Vector pValues;
				try {
					pNames = (Vector) mo.getParameter("PROPERTYNAMES");
					pValues = (Vector) mo.getParameter("PROPERTYVALUES");
				} catch (MissingParameterException mpe) {
					pNames = new Vector();
					pValues = new Vector();
					toAddParams = true;
				}	
				
				mo.setProblemName(logMsg.getProblemName());
				
				String tid = logMsg.getTransactionId();
				if (tid != null && tid.length() > 0) {
					mo.setTransactionId(tid);
					pNames.add(MessageObject.TRANSACTION_ID);
					pValues.add(tid);
				}

				Iterator it = logMsg.eventDescriptorsIterator();
				if (it.hasNext()) {
					
					Vector selections = new Vector();
					Vector actions = new Vector();
					Vector inputs = new Vector();
					do {
						TutorActionLog.EventDescriptor ed = (TutorActionLog.EventDescriptor) it
								.next();
						selections.addAll(OLIMessageObject.makeStringVector(ed.getSelections()));
						actions.addAll(OLIMessageObject.makeStringVector(ed.getActions()));
						inputs.addAll(OLIMessageObject.makeStringVector(ed.getInputs()));
					} while (it.hasNext());
					pNames.add("Selection");
					pValues.add(selections);
					pNames.add("Action");
					pValues.add(actions);
					pNames.add("Input");
					pValues.add(inputs);
				}
	
				it = logMsg.tutorAdvicesIterator();
				if (getAdvicePname() != null && it.hasNext()) {
					if (getSemanticEvt() != null && getSemanticEvt().toLowerCase().contains("hint")) {
						Vector advices = new Vector();
						do {
							advices.add(it.next());
						} while (it.hasNext());
						pNames.add(getAdvicePname());
						pValues.add(advices);
					} else {
						StringBuffer advices = new StringBuffer((String) it.next());
						while (it.hasNext())
							advices.append(' ').append(it.next());
						pNames.add(getAdvicePname());
						pValues.add(advices.toString());
					}
				}
				
				it = logMsg.skillsIterator();
				if (it.hasNext() ) {
					Vector ruleNames = new Vector();
					do {
						TutorActionLog.Skill skill = (TutorActionLog.Skill) it.next();
						ruleNames.add(skill.text);
					} while (it.hasNext());
					pNames.add("Rules");
					pValues.add(ruleNames);
				}
				
				boolean messageTypeSet = false;  // ensure MessageType property gets set exactly once
				for (it = logMsg.msgPropertiesIterator(); it.hasNext(); ) {
					TutorActionLog.MsgProperty mp = (TutorActionLog.MsgProperty) it.next();
					String name = mp.getName();
					if ("MessageType".equalsIgnoreCase(name))
						messageTypeSet = true;
					pNames.add(name);
					pValues.add(mp.isList() ? new Vector(mp.getList()) : mp.getStringValue());
				}
				if (!messageTypeSet) {
					pNames.add("MessageType");
					pValues.add(getCommMsgType());
				}
				
				if (toAddParams) {
					mo.addParameter(OLIMessageObject.PROPERTYNAMES, pNames);
					mo.addParameter(OLIMessageObject.PROPERTYVALUES, pValues);
				}
			}
	
			/**
			 * Generate the list of {@link TutorActionLog#Skill} elements from the
			 * {@link #RULES_PNAME} property
			 * 
			 * @param propertyNames
			 *            {@link OLIMessageObject#PROPERTYNAMES} parameter from msg
			 * @param propertyValues
			 *            {@link OLIMessageObject#PROPERTYVALUES} parameter from msg
			 * @param logMsg
			 *            result msg to modify
			 * @return number of skills added
			 */
			protected int createSkills(Vector propertyNames, Vector propertyValues,
					TutorActionLog logMsg) {
				int result = 0;
				Vector rules = (Vector) OLIMessageObject.getValue(propertyNames, propertyValues,
						RULES_PNAME);
				if (rules != null) {
					for (Iterator it = rules.iterator(); it.hasNext(); ++result)
						logMsg.addSkill((String) it.next());
				}
				return result;
			}
	
			/**
			 * Generate the {@link TutorActionLog.EventDescriptor} (s) from the
			 * selection-action-input properties.
			 * 
			 * @param propertyNames
			 *            {@link OLIMessageObject#PROPERTYNAMES} parameter from msg
			 * @param propertyValues
			 *            {@link OLIMessageObject#PROPERTYVALUES} parameter from msg
			 * @param logMsg
			 *            result msg to modify
			 * @return totalHints number of hints available
			 */
			protected int createTutorAdvices(Vector propertyNames,
					Vector propertyValues, TutorActionLog logMsg) {
				int totalHints = 0;
				if (getAdvicePname() == null)
					return totalHints;
				Object v = OLIMessageObject.getValue(propertyNames, propertyValues, getAdvicePname());
				if (v instanceof Vector) {
					Vector advices = (Vector) v;
					for (Iterator it = advices.iterator(); it.hasNext(); ++totalHints)
						logMsg.addTutorAdvice((String) it.next());
				} else if (v instanceof String) {
					logMsg.addTutorAdvice((String) v);
					++totalHints;
				}
				return totalHints; // also returns here if v is null
			}

			protected void setAsUIEvent(ToolMessage msg, String id) {
				msg.setEventElement(new UiEventElement(id, getSemanticEvt()));
			}

			/**
			 * Generate the {@link TutorActionLog.EventDescriptor} (s) from the
			 * selection-action-input properties.
			 * 
			 * @param propertyNames
			 *            {@link OLIMessageObject#PROPERTYNAMES} parameter from msg
			 * @param propertyValues
			 *            {@link OLIMessageObject#PROPERTYVALUES} parameter from msg
			 * @param logMsg
			 *            result msg to modify
			 */
			protected void createEventDesc(Vector propertyNames,
					Vector propertyValues, TutorActionLog logMsg) {
				Iterator sit = null, ait = null, iit = null;
				int n = 0;
				Vector selections = (Vector) OLIMessageObject.getValue(propertyNames,
						propertyValues, "Selection");
				if (selections != null) {
					sit = selections.iterator();
					n = Math.max(n, selections.size());
				}
				Vector actions = (Vector) OLIMessageObject.getValue(propertyNames, propertyValues,
						"Action");
				if (actions != null) {
					ait = actions.iterator();
					n = Math.max(n, actions.size());
				}
				Vector inputs = (Vector) OLIMessageObject.getValue(propertyNames, propertyValues,
						"Input");
				if (inputs != null) {
					iit = inputs.iterator();
					n = Math.max(n, inputs.size());
				}
				for (int i = 0; i < n; ++i) {
					String ac, se, in;
					ac = ((ait != null && ait.hasNext()) ? (String) ait.next() : "");
					se = ((sit != null && sit.hasNext()) ? (String) sit.next() : "");
					in = ((iit != null && iit.hasNext()) ? (String) iit.next() : "");
					logMsg.addEventDescriptor(getSemanticEvtId(), ac, se, in);
				}
			}
	
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
			 * @param advicePname new value for {@link #advicePname}
			 */
			protected void setAdvicePname(String advicePname) {
				this.advicePname = advicePname;
			}
	
			/**
			 * @return the {@link #advicePname}
			 */
			protected String getAdvicePname() {
				return advicePname;
			}
	
			/**
			 * @return the {@link #commMsgType}
			 */
			protected String getCommMsgType() {
				return commMsgType;
			}
	
			/**
			 * @return the {@link #semanticEvt}
			 */
			protected String getSemanticEvt() {
				return semanticEvt;
			}
	
			/**
			 * @param semanticEvtId new value for {@link #semanticEvtId}
			 */
			protected void setSemanticEvtId(String semanticEvtId) {
				this.semanticEvtId = semanticEvtId;
			}
	
			/**
			 * @return the {@link #semanticEvtId}
			 */
			protected String getSemanticEvtId() {
				return semanticEvtId;
			}
	
			/**
			 * @param tutorToToolRestriction new value for {@link #tutorToToolRestriction}
			 */
			protected void setTutorToToolRestriction(Boolean tutorToToolRestriction) {
				this.tutorToToolRestriction = tutorToToolRestriction;
			}
	
			/**
			 * @return the {@link #tutorToToolRestriction}
			 */
			protected Boolean getTutorToToolRestriction() {
				return tutorToToolRestriction;
			}
		}

	/** Specific converter for hint requests and responses. */
	static class HintConv extends Conv {
	
		HintConv(String semanticEvt, String actionEvaluation,
				String commMsgType, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, "HintsMessage",
					null, logger);
		}
	
		/**
		 * Tell whether a MessageObject is a hint request or result.
		 * 
		 * @param mo
		 *            native MessageObject to query
		 * @return true if the first selection item is "hint" or "help"
		 */
		static boolean isHintMsg(MessageObject mo) {
			String selection0 = (String) OLIMessageObject.getPropertyElement(mo, "Selection", 0);
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
			String selection = (String) OLIMessageObject.getPropertyValue(mo, "MessageType");
			if ("Glossary".equalsIgnoreCase(selection) || "CognitiveLoad".equalsIgnoreCase(selection)) return true;
			else return false;
		}

		/**
		 * Build a TutorActionLog message from the name-value pairs in a native
		 * Comm message. !!!STUB: currently handles only 1st hint request.
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
			TutorActionLog result = null;
			if (tutorToTool)
				result = new TutorActionLog(TutorActionLog.TUTOR_MSG_ELEMENT);
			else
				result = new TutorActionLog(TutorActionLog.TOOL_MSG_ELEMENT);
	
			result.setProblemName(logger.getProblemName());
			result.setAttemptId(logger.getContextMessage().getContextMessageId());
			setSemanticEvtId(result.addSemanticEvent(getSemanticEvt(),
					mo.getSemanticEventId(), mo.getLinkedSemanticEventId()));
			createEventDesc(propertyNames, propertyValues, result);
			populateHintElements(propertyNames, propertyValues, result);
			return result;
		}
	
		/**
		 * Convert an OLI hint request or hint response to native Comm.
		 * <p>
		 * For hint requests, changes the MessageType to
		 * {@link OLIMessageObject#INTERFACE_ACTION}. If the first Action value is not
		 * "ButtonPressed" (for the Hint button), then inserts that action.
		 * 
		 * @param logMsg
		 *            source message
		 * @param mo
		 *            destination message
		 */
		protected void OLI2Native(TutorActionLog logMsg, OLIMessageObject mo) {
	
			if (getSemanticEvt().equals("HINT_REQUEST")) {
				createPropertyList(logMsg, mo);
				OLIMessageObject.setProperty(mo, "MessageType", OLIMessageObject.INTERFACE_ACTION);
				String action0 = (String) OLIMessageObject.getPropertyElement(mo, "Action", 0);
				if (!("ButtonPressed".equalsIgnoreCase(action0))) {
					OLIMessageObject.insertPropertyElement(mo, "Action", 0, "ButtonPressed");
					OLIMessageObject.insertPropertyElement(mo, "Selection", 0, "Hint");
					OLIMessageObject.insertPropertyElement(mo, "Input", 0, "-1");
				}
				return;
			}
			super.OLI2Native(logMsg, mo);
		}
	}

	/**
	 * Converter for non-tutor messages. Translates to the type
	 * {@link TutorActionLog#MSG_ELEMENT}.
	 */
	static class OtherMsgConv extends Conv {
	
		/** Constructor for enclosing class-only access. */
		OtherMsgConv(LogContext logger) {
			super("unused1", "unused2", "unused3", null, null, logger);
			trace.printStack();
		}
	
		/**
		 * Build a native Comm message from a TutorActionLog.MSG_ELEMENT
		 * message. Culls out any verb property and calls
		 * {@link MessageObject#setVerb(String)}. Otherwise loads
		 * 
		 * @param logMsg
		 *            TutorActionLog instance to translate
		 * @param mo
		 *            native Comm message to populate
		 */
		protected void OLI2Native(TutorActionLog logMsg, OLIMessageObject mo) {
	
			// Generate the Names and Values lists needed by the superclass
	
			Iterator it = logMsg.msgPropertiesIterator();
			if (it.hasNext()) {
				Vector pNames = new Vector();
				Vector pValues = new Vector();
	
				do {
					TutorActionLog.MsgProperty property = (TutorActionLog.MsgProperty) it
							.next();
					String name = property.getName();
					Object value = null;
	
					if ("verb".equalsIgnoreCase(name)) {
						mo.setVerb(property.getStringValue());
						continue;
					}
					if (property.isList()) // copy logMsg list: don't reference
						value = new Vector(property.getList());
					else
						value = property.getStringValue();
					pNames.add(name);
					pValues.add(value);
				} while (it.hasNext());
	
				mo.addParameter(OLIMessageObject.PROPERTYNAMES, pNames);
				mo.addParameter(OLIMessageObject.PROPERTYVALUES, pValues);
			}
		}
	
		/**
		 * Build a TutorActionLog message from the name-value pairs in a native
		 * Comm message.
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
	
			TutorActionLog result = new TutorActionLog(
					TutorActionLog.MSG_ELEMENT);
	
			result.setAttemptId(logger.getContextMessage().getContextMessageId());
	
			populateProperties(propertyNames, propertyValues, mo, result);
			return result;
		}
	}

	/** Specific converter for START_TUTOR messages. */
	static abstract class StartProblemConv extends Conv {
	
		protected static final String PROBLEM_NAME = "ProblemName";
	
		StartProblemConv(String semanticEvt, String actionEvaluation,
				String commMsgType, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, null, null, logger);
		}
		
		/** Set {@link OLIMessageObject#problemName} in parent class. */
		protected abstract TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
				MessageObject mo, boolean tutorToTool, LogContext logger);

		/** Set up a StartProblem message. */
		protected void OLI2Native(TutorActionLog logMsg, OLIMessageObject mo) {
	
			super.OLI2Native(logMsg, mo);
			String localProblemName = logMsg.getProblemName();
//			FIXME set in Logger?
//			OLIMessageObject.setProblemName(localProblemName);
			mo.setProblemName(localProblemName);
			OLIMessageObject.setProperty(mo, PROBLEM_NAME, localProblemName);
	
			String s = null;
			if (null != (s = logMsg.getSchoolName()))
				OLIMessageObject.setProperty(mo, "SchoolName", s);
			if (null != (s = logMsg.getCourseName()))
				OLIMessageObject.setProperty(mo, "CourseName", s);
			if (null != (s = logMsg.getUnitName()))
				OLIMessageObject.setProperty(mo, "UnitName", s);
			if (null != (s = logMsg.getSectionName()))
				OLIMessageObject.setProperty(mo, "SectionName", s);
		}
	}

	/**
	 * Map native message type to OLI action identifier. Key is native
	 * MessageType, value is {@link #Conv} instance.
	 */
	private final Map mapNative2OLI;

	/**
	 * Map OLI action identifier to native message type. Key is action id +
	 * action eval, value is {@link #Conv} instance.
	 */
	private final Map mapOLI2Native;

	/**
	 * Conv instance used by Conv for hint requests. This
	 * instance is not used for hint responses.
	 */
	private final OLIMessageConverters.Conv hintConv;

	/** Common converter for non-tutor messages. */
	private final OLIMessageConverters.Conv otherMsgConv;
	
	/**
	 * Constructor sets all fields.
	 * @param logger reference for {@link OLIMessageConverters.Conv} constructors.
	 */
	OLIMessageConverters(OLIMessageConverters.Conv otherMsgConv, OLIMessageConverters.Conv hintConv, Map mapNative2OLI,
			Map mapOLI2Native) {
		this.otherMsgConv = otherMsgConv;
		this.hintConv = hintConv;
		this.mapNative2OLI = mapNative2OLI;
		this.mapOLI2Native = mapOLI2Native;
	}

	Map getMapNative2OLI() {
		return mapNative2OLI;
	}

	Map getMapOLI2Native() {
		return mapOLI2Native;
	}

	/**
	 * @return the {@link #hintConv}
	 */
	OLIMessageConverters.Conv getHintConv() {
		return hintConv;
	}

	/**
	 * @return the {@link #otherMsgConv}
	 */
	OLIMessageConverters.Conv getOtherMsgConv() {
		return otherMsgConv;
	}

	/**
	 * Initialize the saved {@link OLIMessageConverters.Conv} maps.
	 * @param conv
	 */
	protected void initConvMaps(OLIMessageConverters.Conv[] conv) {
		getMapOLI2Native().put(getHintConv().getSemanticEvt().toUpperCase(), getHintConv());
		for (int i = 0; i < conv.length; ++i) {
			if (conv[i].getActionEvaluation() == null)
				getMapOLI2Native().put(conv[i].getSemanticEvt().toUpperCase(), conv[i]);
			else {
				String key = conv[i].getSemanticEvt() + " "
				+ conv[i].getActionEvaluation();
				getMapOLI2Native().put(key.toUpperCase(), conv[i]);
			}
			getMapNative2OLI().put(conv[i].getCommMsgType().toLowerCase(), conv[i]);
		}
	}

	/**
	 * Determine a {@link #Conv} object from characteristics in the MessageObject from
	 * information determined by {@link MessageObject#getConvertInstructions()}.
	 * @param mo
	 * @return converter, if information available; else null
	 */
	Conv getNative2OLIConverter(MessageObject mo) {
		String convInstructions = mo.getConvertInstructions();
		String messageType = mo.getMessageTypeProperty();
		if (convInstructions != null)
		{
			String[] ci = convInstructions.split(CONV_TO_DELIMITER, 2);
			if (ci.length > 1 && ci[0].equalsIgnoreCase(messageType)) {
				if (ci[1].contains(OTHER_MSG_CONV))
					return getOtherMsgConv();
			}
		}
		if (messageType == null)
			return null;
		String key = messageType.toLowerCase();
		Conv result = (OLIMessageConverters.Conv) getMapNative2OLI().get(key);
		if (result == null) {
			if (trace.getDebugCode("mo")) trace.out("mo", "no OLI mapping for native message type " + messageType);
			result = getOtherMsgConv();
		}
		return result;
	}

	public static void setConvertInstructions(MessageObject mo,
			String messageType, String msgConv) {
		mo.setConvertInstructions(messageType + CONV_TO_DELIMITER + msgConv);
	}
}
