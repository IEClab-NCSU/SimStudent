/**
 * Copyright 2007 Carnegie Mellon University. 
 */
package edu.cmu.pact.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jdom.Element;

import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.Log.TutorActionLog;
import edu.cmu.pact.Log.TutorActionLog.ActionEvaluation;
import edu.cmu.pact.Log.TutorActionLogV4;
import edu.cmu.pact.Utilities.OLIMessageConverters.Conv;
import edu.cmu.pact.Utilities.OLIMessageConverters.HintConv;
import edu.cmu.pact.Utilities.OLIMessageConverters.StartProblemConv;
import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.jess.JessModelTracing;
import edu.cmu.pact.jess.LogWorkingMemory;
import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.LogContext;
import edu.cmu.pslc.logging.Message;
import edu.cmu.pslc.logging.PlainMessage;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.TutorMessage;
import edu.cmu.pslc.logging.element.SkillElement;

/**
 * A MessageObject derived from an XML element adhering to version 4 of
 * the PSLC DataShop logging specification.
 * @author sewall
 * @see OliMessageObject
 */
public class OLIMessageObjectV4 extends OLIMessageObject {

	/** Property name for working memory images. */
	public static final String WMIAGESS_PNAME = "WMImages";
	
	/** Property name for step identifier. */
    public static final String STEP_ID = "step_id";

	/**
	 * A class to convert skills messages to DTDv4 format.
	 */
	static class SkillsConvV4 extends ConvV4 {

		/**
		 * @param semanticEvt
		 * @param actionEvaluation
		 * @param commMsgType
		 * @param advicePname
		 * @param tutorToToolRestriction
		 * @param logger
		 */
		public SkillsConvV4(String semanticEvt,	String commMsgType,
				Boolean tutorToToolRestriction, LogContext logger) {
			super(semanticEvt, null, commMsgType, "TutorAdvice", tutorToToolRestriction, logger);
		}

		/**
		 * Build a native Comm message from a TutorActionLog message.
		 * @param logMsg TutorActionLog instance to translate
		 * @param mo native Comm message to populate
		 */
		protected void OLI2Native(TutorActionLog logMsg, OLIMessageObject mo) {
			super.OLI2Native(logMsg, mo);
			Iterator it = logMsg.actionEvaluationsIterator();
			if (it.hasNext()) {
				ActionEvaluation ae = (ActionEvaluation) it.next();
				mo.setProperty("Indicator", ae.getText().trim());
			}
			
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
					mo.setProperty(RULES_PNAME, ruleNames);
				if (skills.size() > 0)
					mo.setProperty(SKILLS_PNAME, ruleNames);
			}
		}
	}
	
	/**
	 * DTDv4-specific converter for non-tutor messages. Translates to the type
	 * {@link TutorActionLog#MSG_ELEMENT}.
	 */
	static class OtherMsgConvV4 extends ConvV4 {
	
		/** Constructor for enclosing class-only access. */
		OtherMsgConvV4(LogContext logger) {
			super("unused1", "unused2", "unused3", null, null, logger);
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
			
			PlainMessage msg = PlainMessage.create(logger.getContextMessage());
			TutorActionLogV4 result = new TutorActionLogV4(msg);
			populateProperties(propertyNames, propertyValues, mo, result);
			return result;
		}
	}

	/**
	 * DTDv4-specific converter for StartProblem and StartTutor messages.
	 */
	static class StartProblemConvV4 extends StartProblemConv {

		/**
		 * @param semanticEvt
		 * @param actionEvaluation
		 * @param commMsgType
		 * @param logger
		 */
		StartProblemConvV4(String semanticEvt, String actionEvaluation,
				String commMsgType, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, logger);
		}
		
		/** Set {@link OLIMessageObject#problemName} in parent class. */
		protected TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
				MessageObject mo, boolean tutorToTool, LogContext logger) {

			Object pnObj = OLIMessageObject.getValue(propertyNames, propertyValues, PROBLEM_NAME);
			String localProblemName = (String) (pnObj == null ? "null" : pnObj.toString());

			// sewall 3/3/07: don't do this side effect; other start-problem code should handle 
			// getLogger().setProblemName(localProblemName);

			// revise ContextMessage in logger
			ContextMessage msg = logger.getContextMessage(getSemanticEvt());
			TutorActionLogV4 result = new TutorActionLogV4(msg);
			return result;
		}
	}

	/**
	 * A DataShop DTDv4 converter for tool messages.
	 */
	static class ConvV4 extends Conv {

		/**
		 * Constructor for superclass.
		 * @param semanticEvt
		 * @param actionEvaluation
		 * @param commMsgType
		 * @param advicePname
		 * @param tutorToToolRestriction
		 * @param logger
		 */
		public ConvV4(String semanticEvt, String actionEvaluation,
				String commMsgType, String advicePname,
				Boolean tutorToToolRestriction, LogContext logger) {
			super(semanticEvt, actionEvaluation, commMsgType, advicePname,
					tutorToToolRestriction, logger);
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
		 * @param propertyNames  List of names, 1:1 with propertyValues
		 * @param propertyValues List of values, 1:1 with propertyNames
		 * @param mo              original MessageObject
		 * @param tutorToTool     true if message was from tutor to tool; false if from tool
		 *                        to tutor
		 * @param logger source of context info
		 * @return TutorActionLog instance with translated data
		 */
		protected TutorActionLog native2OLI(Vector propertyNames, Vector propertyValues,
				MessageObject mo, boolean tutorToTool, LogContext logger) {
			
			OLIMessageConverters convs = getConvs();
			
			TutorActionLog result = null;
			
			//unsure of placement, so we insert everywhere ...
			String replay = (String)mo.getProperty(TutorActionLogV4.REPLAY);
			
			String indicator = (String) mo.getProperty("Indicator");
			if (trace.getDebugCode("log")) trace.out("log", "indicator "+indicator+", actionEval "+getActionEvaluation());
			if (indicator != null && indicator.length() > 0)
				setActionEvaluation(indicator);
			
			if (getTutorToToolRestriction() != null
					&& getTutorToToolRestriction().booleanValue() != tutorToTool)
			{
				result = convs.getOtherMsgConv().native2OLI(propertyNames, propertyValues,
						mo, tutorToTool, logger);
				if(result instanceof TutorActionLogV4)
					((TutorActionLogV4)result).setReplay(replay);
			}
			
			if (tutorToTool) {
				ToolMessage lastToolMessage = getLastToolMessage(logger);
				lastToolMessage.setTransactionId(mo.getTransactionId());
				TutorMessage msg = TutorMessage.create(lastToolMessage);
				String actionEvaluation = getActionEvaluation();
				setResponseType(actionEvaluation, msg);
				result = new TutorActionLogV4(msg);
				result.setProblemName(logger.getProblemName());
				createEventDesc(propertyNames, propertyValues, result);
				if(replay != null)
					((TutorActionLogV4)result).setReplay(replay);
				if (actionEvaluation != null && actionEvaluation.toUpperCase().contains("HINT")) {
					populateHintElements(propertyNames, propertyValues, result);
					addTimeStamp((TutorActionLogV4) result);
					addStepId(propertyNames, propertyValues, (TutorActionLogV4) result);
					return result;
				}
			}
			else {
				ToolMessage msg = ToolMessage.create(logger.getContextMessage());
				//trace.out("log", "native2OLI: msg after create = " + msg);
				if (HintConv.isUIEventMsg(mo))
					setAsUIEvent(msg, mo.getTransactionId());
				else {
					/*
					 * ToolMessage.setAsXXX() methods generate new transaction ids,
					 * so fix the id after calling them.
					 */
					if (HintConv.isHintMsg(mo))
						msg.setAsHintRequest();
					else
						msg.setAsAttempt();
					msg.setTransactionId(mo.getTransactionId());
					logger.setLastToolMessage(msg);
				}
				//trace.out("log", "native2OLI: msg after setAs.. = " + msg);
				result = new TutorActionLogV4(msg);
				result.setProblemName(logger.getProblemName());
				createEventDesc(propertyNames, propertyValues, result);
				//trace.out("log", "native2OLI: msg after createEventDesc = " + msg);
				//trace.out("log", "native2OLI: result after createEventDesc = " + result);
				if(replay != null)
					((TutorActionLogV4)result).setReplay(replay);
				if (HintConv.isUIEventMsg(mo) || HintConv.isHintMsg(mo)) {
					addTimeStamp((TutorActionLogV4) result);
					return result;
				}
			}
			if (getActionEvaluation() != null) result.addActionEvaluation(getActionEvaluation());
			createTutorAdvices(propertyNames, propertyValues, result);
			createSkills(propertyNames, propertyValues, result);
			if(replay != null)
				((TutorActionLogV4)result).setReplay(replay);
			addTimeStamp((TutorActionLogV4) result);
			addWMImages(propertyNames, propertyValues, (TutorActionLogV4) result);
			addStepId(propertyNames, propertyValues, (TutorActionLogV4) result);
			return result;
		}

		/**
		 * Log the link identifier in a custom field.
		 * @param propertyNames  {@link OLIMessageObject#PROPERTYNAMES} parameter from msg
		 * @param propertyValues {@link OLIMessageObject#PROPERTYVALUES} parameter from msg
		 * @param logMsg result msg to modify
		 */
		private void addStepId(Vector propertyNames, Vector propertyValues,
				TutorActionLogV4 logMsg) {
			Message msg = logMsg.getMsg();
			String stepId = (String) OLIMessageObject.getValue(propertyNames, propertyValues,
					PseudoTutorMessageBuilder.STEP_ID);
			if (stepId == null || stepId.length() < 1)
				return;
			if (msg instanceof TutorMessage) 
				((TutorMessage) msg).addCustomField(STEP_ID, stepId);
			else if (msg instanceof ToolMessage)  
			 	((ToolMessage)  msg).addCustomField(STEP_ID, stepId);
		}

		/**
		 * Generate a list of custom elements from the # property.
		 * @param propertyNames  {@link OLIMessageObject#PROPERTYNAMES} parameter from msg
		 * @param propertyValues {@link OLIMessageObject#PROPERTYVALUES} parameter from msg
		 * @param logMsg result msg to modify
		 * @return number of WM images added
		 */
		protected int addWMImages(Vector propertyNames, Vector propertyValues,
				TutorActionLogV4 logMsg) {
			Message msg = logMsg.getMsg();
			int count = 0;
			Vector wmImages = (Vector) OLIMessageObject.getValue(propertyNames, propertyValues,
					WMIAGESS_PNAME);
			if (wmImages != null) {
				for (Iterator it = wmImages.iterator(); it.hasNext(); ++count) {
					String[] parsedImage = LogWorkingMemory.parseImage((String) it.next());
					if (msg instanceof TutorMessage) 
						((TutorMessage) msg).addCustomField(parsedImage[0], parsedImage[1]);
					else if (msg instanceof ToolMessage)  
					 	((ToolMessage)  msg).addCustomField(parsedImage[0], parsedImage[1]);
				}
			}
			return count;
		}

		public void addTimeStamp(TutorActionLogV4 result) {
			Message msg = result.getMsg();

			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
			df.setTimeZone(tz);
			String now = df.format(result.getTimeStamp());
//			System.out.printf("Date from \"%27s\" == %s\n\n", result
//					.getTimeStamp(), now);

			 if 	 (msg instanceof TutorMessage) 
				 	 ((TutorMessage) msg).addCustomField("tutor_event_time", now);
			 else if (msg instanceof ToolMessage)  
				 	((ToolMessage)  msg).addCustomField("tool_event_time",  now);
//			 else   ((ContextMessage)  msg).addCustomField(msg.getUserId(),  now);
				
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
			
//			trace.printStack();
			
			TutorActionLogV4 logV4 = null;
			if(logMsg instanceof TutorActionLogV4 &&
					(logV4 = (TutorActionLogV4)logMsg).getReplay() != null)
				mo.addPropertyElement(TutorActionLogV4.REPLAY, logV4.getReplay());
			
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
			if (trace.getDebugCode("log")) trace.out("log", "OLIMsgObjV4.ConvV4.OLI2Native("+logMsg+")");
			createPropertyList(logMsg, mo);
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
			Vector skills = (Vector) OLIMessageObject.getValue(propertyNames, propertyValues,
					SKILLS_PNAME);
			if (skills != null) {
				for (Iterator it = skills.iterator(); it.hasNext(); ++result)
					parseAndAddSkill((String) it.next(), (TutorActionLogV4) logMsg);
				return result;
			}
			Vector rules = (Vector) OLIMessageObject.getValue(propertyNames, propertyValues,
					RULES_PNAME);
			if (rules != null) {
				for (Iterator it = rules.iterator(); it.hasNext(); ++result)
					parseAndAddSkill((String) it.next(), (TutorActionLogV4) logMsg);
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
			String[] parts = text.split(Skill.SKILL_BAR_DELIMITER);
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

		/** Regular expression ".*[hH][iI][nN][tT].*" to test actionEvaluation. */
		private static final Pattern HintPattern = Pattern.compile(".*[hH][iI][nN][tT].*");

		/**
		 * Characterize the given {@link TutorMessage} as correct, incorrect, etc.
		 * @param actionEvaluation one "CORRECT", "INCORRECT" or "HINT"
		 * @param msg TutorMessage to set
		 */
		protected void setResponseType(String actionEvaluation, TutorMessage msg) {
			if (actionEvaluation == null) {
				trace.err("ConvV4: null action evaluation in tutor msg");
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
	}
	
	/**
	 * The bundle of DTDv4 converters.
	 */
	static class ConvertersV4 extends OLIMessageConverters {
		
		/**
		 * Constructor sets all fields.
		 * @param logger reference for {@link OLIMessageConverters.Conv} constructors.
		 */
		ConvertersV4(LogContext logger) {
			super(new OtherMsgConvV4(logger),
					new ConvV4("HINT_REQUEST", null, INTERFACE_ACTION, null, Boolean.FALSE, logger),
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
				new ConvV4("ATTEMPT", null, INTERFACE_ACTION, null, F, logger),
				new ConvV4("GLOSSARY", null, GLOSSARY, null, F, logger),
				new ConvV4("COGNITIVE_LOAD", null, COGNITIVE_LOAD, null, F, logger),
				
				// sewall 11/14/07: commented-out entries to consolidate RESULT logging into AssociatedRules msg
//				new ConvV4("RESULT", "CORRECT", "CorrectAction", "SuccessMsg", T, logger),
//				new ConvV4("RESULT", "INCORRECT", "IncorrectAction", "BuggyMsg", T, logger),
//				new ConvV4("RESULT", "CORRECT", "SuccessMessage", "SuccessMsg", T, logger),
//				new ConvV4("RESULT", "INCORRECT", "BuggyMessage", "BuggyMsg", T, logger),
//				new ConvV4("RESULT", "INCORRECT", "HighlightMsg", "HighlightMsgText", T, logger),
//				new ConvV4("RESULT", "INCORRECT", "NotDoneMessage", "Message", T, logger),
				
				new SkillsConvV4("RESULT", "AssociatedRules", T, logger),
				new StartProblemConvV4("START_TUTOR", null, "StartProblem", logger),
				new StartProblemConvV4("START_PROBLEM", null, "StartProblem", logger),

//              sewall 2010/02/05 CTAT2303: move hint response logging to AssociatedRules
//				new ConvV4("HINT_MSG", "HINT", "ShowHintsMessage", "HintsMessage", T, logger),
				new ConvV4("HINT_MSG", "HINT_NEXT", "NextHintMessage", "HintsMessage", T, logger),
				new ConvV4("HINT_MSG", "HINT_PREVIOUS", "PreviousHintMessage", "HintsMessage", T, logger),
				new StartProblemConvV4("START_TUTOR", null, "LoadBRDFileSuccess", logger)
			};

			initConvMaps(conv);
		}
	}
	
	public static ConvertersV4 converters;
	/**
	 * @param elt
	 * @param top
	 * @param controller
	 */
	public OLIMessageObjectV4(Element elt, ObjectProxy top, LogContext logger) {
		super(elt, top, logger);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param mo
	 * @param tutorToTool
	 * @param controller
	 */
	public OLIMessageObjectV4(MessageObject mo, boolean tutorToTool, LogContext logger) {
		super(mo, tutorToTool, logger);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param xmlStr
	 * @param logger
	 */
	public OLIMessageObjectV4(String xmlStr, LogContext logger) {
		super(xmlStr, "4", null, logger);
	}

	/**
	 * @param xmlStr
	 * @param top
	 * @param controller
	 */
	public OLIMessageObjectV4(String xmlStr, ObjectProxy top, BR_Controller controller) {
		super(xmlStr, "4", top, (controller == null ? null : controller.getLogger()));
	}

	/**
	 * Constructor for converting an OLI log entry.
	 * @param logMsg
	 * @param top
	 * @param controller
	 */
	public OLIMessageObjectV4(TutorActionLog logMsg, ObjectProxy top, BR_Controller controller) {
		super(logMsg, top, controller);
	}

    /**
     * @param elt &lt;tool_message&gt;, etc., instance
     * @return {@link TutorActionLog} instance
     */
    protected TutorActionLog createTutorActionLog(Element elt) {
    	return new TutorActionLogV4(elt, null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Initialize the set of {@link OLIMessageConverters.Conv} for this instance.
	 * If converting from an existing ActionLog element, use convs for its version.
	 * Also calls {@link Logger#getOLIMessageConverters(OLIMessageConverters)}
	 * @param logger no-op if {@link Logger#getOLIMessageConverters()} is set;
	 *               otherwise calls {@link Logger#getOLIMessageConverters(OLIMessageConverters)}
	 *               with returned value
	 * @return OLIMessageConverters instance created
	 */
	protected OLIMessageConverters initConvs(LogContext logger) {
		if (logger==null) {
            if(getConvs()==null)
            	setConvs(new ConvertersV4(logger));
            return getConvs();
        }
		
		synchronized(logger) {  // Make sure this only runs once
			TutorActionLog msg = getLogMsg();
			if(getConvs()==null){
				if (msg != null) {
					int versNum = Integer.parseInt(msg.getDTDVersionNumber());
					if (trace.getDebugCode("log")) trace.out("log", "OLIMsgObjV4.initConvs() msg "+msg.getClass()+", versNum "+versNum);
					if (versNum > 2)
						setConvs(new ConvertersV4(logger));
					else
						setConvs(new ConvertersV2(logger));
				} else {
					setConvs(new ConvertersV4(logger));
				}
				
			}
			return getConvs();
		}
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

		if (conv == null || conv.getCommMsgType() == null)
			conv = (OLIMessageConverters.Conv)getConvs().getMapOLI2Native().get(oli);

		if (conv == null || conv.getCommMsgType() == null) {
			System.err.println("no native mapping for OLI event " + oli
					+ ", actionEvaluation " + oliEval);
			conv = new OtherMsgConvV4(logger);
			getConvs().getMapOLI2Native().put(key, conv);
		}
		return conv;
	}
	
	/**
	 * Get the identifier from the {@link ContextMessage} associated with
	 * this message. The ContextMessage records the problem, course, unit,
	 * section, etc., environment in which this action was taken.
	 * @return contextMessageId
	 */
	public String getContextMessageId() {
		return ((TutorActionLogV4) getLogMsg()).getContextMessageId();
	}

	/**
	 * Get the transaction identifier for this message. Each student attempt
	 * generates a transaction identifier, which is copied onto the tutor's
	 * response to that attempt.
	 * @return translation identifier, if this is a {@link ToolMessage} or
	 *         {@link TutorMessage}; null otherwise
	 */
	public String getTransactionId() {
		if (getLogMsg() == null)
			return null;
		else
			return ((TutorActionLogV4) getLogMsg()).getTransactionId();
	}
	
	public String UTCTimeStamp(Date result) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
		df.setTimeZone(tz);
		String now = df.format(result);
		System.err.printf("Convert from \"%27s\" == %s\n\n", result, now);
		return now;
	}
}

