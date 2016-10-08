/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.Log;

import java.rmi.server.UID;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

import edu.cmu.oli.log.client.ActionLog;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pslc.logging.AbstractMessageLogger;
import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.Message;
import edu.cmu.pslc.logging.PlainMessage;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.TutorMessage;
import edu.cmu.pslc.logging.element.ActionEvaluationElement;
import edu.cmu.pslc.logging.element.EventElement;
import edu.cmu.pslc.logging.element.MetaElement;
import edu.cmu.pslc.logging.element.PropertyElement;
import edu.cmu.pslc.logging.element.SemanticEventElement;
import edu.cmu.pslc.logging.element.SkillElement;

/**
 * @author sewall
 *
 */
public class TutorActionLogV4 extends TutorActionLog {
	
	//should not be in V2, assuming we only have one ...
	private String replay;
	
	public static final String REPLAY = "replay";
	
	/**
	 * Instance of DTDv4 message logger for top- and bottom-of-element framing.
	 * @see #getInfo().
	 */
	private class DTDv4Logger extends AbstractMessageLogger {
		DTDv4Logger() {
			super(ENCODING_UTF8);
		}
		public boolean log(Message msg) { return log(msg, null); }
		public boolean log(Message msg, Date date) { return false; }
		public void close() {}
		/**
		 * Return the given {@link Message} as a complete XML document, for
		 * the OLI {@link ActionLog} info field.
		 * @param msg
		 * @return complete XML document, excluding DOCTYPE, as String
		 */
		public String getInfo(Message msg) {
			return getOpenXml() + msg.toString(false) + getCloseXml();
		}
	};

	/** Attribute value of DTD version this class can accept and generate. */
	public static final String VERSION_NUMBER = "4";

	/** Used by {@link #getInfo()}. */
	private DTDv4Logger dtdV4Logger = new DTDv4Logger();

	/**
	 * Generate a unique identifier. Returns toString() of a new instance of
	 * {@link java.rmi.server.UID}, prefixed by "T" (for "transaction")
	 * to ensure 1st char is alphabetic (see XML spec).
	 * !!!STUB: should append machine identifier for global uniqueness.
	 * @return  UID.toString() result
	 */
	static String generateGUID() {
		UID uid = new UID();
		return "T" + uid.toString();
	}
	
	/**
	 * Constructor for {@link ContextMessage}.
	 * @param contextMessage
	 */
	public TutorActionLogV4(ContextMessage contextMessage) {
		super(CONTEXT_MSG_ELEMENT);
		this.msg = contextMessage;
	}		
	
	/**
	 * Constructor for {@link ToolMessage}.
	 * @param toolMessage
	 */
	public TutorActionLogV4(ToolMessage toolMessage) {
		super(TOOL_MSG_ELEMENT);
		this.msg = toolMessage; 
	}		
	
	/**
	 * Constructor for {@link TutorMessage}.
	 * @param tutorMessage
	 */
	public TutorActionLogV4(TutorMessage tutorMessage) {
		super(TUTOR_MSG_ELEMENT);
		this.msg = tutorMessage; 
	}		
	
	/**
	 * Constructor for {@link PlainMessage}.
	 * @param plainMessage
	 */
	public TutorActionLogV4(PlainMessage plainMessage) {
		super(MSG_ELEMENT);
		this.msg = plainMessage;
	}

	/**
	 * @param elt message element from XML
	 * @meta userid, sessionid, timestamp from logging system
	 */
	public TutorActionLogV4(Element elt, MetaElement meta) {
		super(elt, meta);  // calls setTopElementType()
	}

	/**
	 * @return the {@link #WERSION_NUMBER}
	 */
	public String getDTDVersionNumber() {
		return VERSION_NUMBER;
	}
	
	/**
	 * Get all the Skills into the skills iterator. Sets {@link #skills}.
	 * @param msgElt top-level tutor_message Element
	 * @param ns namespace for msgElt
	 */
	protected void parseSkills(Element msgElt, Namespace ns) throws JDOMException {
		Iterator it = msgElt.getChildren(Skill.ELEMENT, ns).iterator();
		if (it.hasNext()) {
			skills = new LinkedList();
			do {
				String name = "unnamed", category = "";
				Element e = (Element) it.next();
				Element nameElt = e.getChild("name", ns);
				if (nameElt != null && nameElt.getTextNormalize().length() > 0)
					name = nameElt.getTextNormalize();
				Element categoryElt = e.getChild("category", ns);
				if (categoryElt != null)
					category = categoryElt.getTextNormalize();
				SkillElement skillElt = new SkillElement(name, category);
				if (category.length() > 0)
					name = categoryElt.getTextNormalize() + " " + name;
				String probability = e.getAttributeValue("probability", ns);
				skillElt.setProbability(probability);
				((TutorMessage) getMsg()).addSkill(skillElt);
				skills.add(new Skill(name, probability));
			} while(it.hasNext());
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Add a MsgProperty with a scalar String value.
	 *
	 * @param  name property name
	 * @param  stringValue scalar value
	 */
	//////////////////////////////////////////////////////////////////////
	public void addMsgProperty(String name, String stringValue) {
		((PlainMessage) getMsg()).addProperty(name, stringValue);
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Add a MsgProperty with a List value.
	 *
	 * @param  name property name
	 * @param  list list value
	 */
	//////////////////////////////////////////////////////////////////////
	public void addMsgProperty(String name, List list) {
		PropertyElement pe = new PropertyElement(name, list);
		((PlainMessage) getMsg()).addProperty(pe);
	}
	
	/**
	 * @return string representation of the OLI info field
	 */
	public String getInfo() {
		return dtdV4Logger.getInfo(msg);
	}

	/**
	 * XML string representation of complete XML Document, including prologue.
	 * @return XML document with indentation
	 */
	public String toString() {
		return getInfo();
	}
	
	/**
	 * @param topElementType new value for {@link #topElementType}
	 */
	protected void setTopElementType(String topElementType) {
		trace.out("log", "called V4 setTopElementType");
		if (TOOL_MSG_ELEMENT.equalsIgnoreCase(topElementType)) {
			this.topElementType = TOOL_MSG_ELEMENT;
		} else if (TUTOR_MSG_ELEMENT.equalsIgnoreCase(topElementType)) {
			this.topElementType = TUTOR_MSG_ELEMENT;
		} else if (CONTEXT_MSG_ELEMENT.equalsIgnoreCase(topElementType)) {
			this.topElementType = CONTEXT_MSG_ELEMENT;
		} else if (MSG_ELEMENT.equalsIgnoreCase(topElementType)) {
			this.topElementType = MSG_ELEMENT;
		} else
			throw new IllegalArgumentException("Undefined element type: " +
											   topElementType);
	}

	/**
	 * @return the {@link #msg}
	 */
	public edu.cmu.pslc.logging.Message getMsg() {
		return msg;
	}

	/**
	 * Set the ProblemName as a String.
	 * @param  name value for {@link #msg}.setProblemName().
	 */
	public void setProblemName(String name) {
		if (getMsg() instanceof ToolMessage) {
			((ToolMessage) getMsg()).setProblemName(name);
		} else if (getMsg() instanceof TutorMessage) {
			((TutorMessage) getMsg()).setProblemName(name);
		}
	}

	/**
	 * Add an event descriptor element.
	 * @param dummySemanticEvtId
	 * @param ac
	 * @param se
	 * @param in
	 * @return first argument
	 * @see edu.cmu.oli.log.client.TutorActionLog#addEventDescriptor(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String addEventDescriptor(String dummySemanticEvtId, String ac, String se, String in) {
		if (getMsg() instanceof ToolMessage) {
			((ToolMessage) getMsg()).addSai(se, ac, in);
		} else if (getMsg() instanceof TutorMessage) {
			((TutorMessage) getMsg()).addSai(se, ac, in);
		}
		return dummySemanticEvtId;
	}

	/**
	 * Add a tutor_advice value.
	 * @param  text text of tutor advice created
	 */
	public void addTutorAdvice(String text) {
		TutorMessage msg = (TutorMessage) getMsg();
		msg.addTutorAdvice(text);
	}

	/**
	 * Add an ActionEvaluation with a hint number and total hints available.
	 *
	 * @param  text body text of ActionEvaluation created
	 * @param  currentHintNumber value for
	 *            {@link ActionEvaluation#currentHintNumber}
	 * @param  totalHintsAvailable value for
	 *            {@link ActionEvaluation#totalHintsAvailable}
	 */
	public void addActionEvaluation(String text,
									String currentHintNumber,
									String totalHintsAvailable) {
		ActionEvaluationElement aee = new ActionEvaluationElement(currentHintNumber,
				totalHintsAvailable, null, null, text);
		TutorMessage msg = (TutorMessage) getMsg();
		msg.setActionEvaluationElement(aee);
	}

	
	/**
	 * Add a Skill with an optional probability.
	 *
	 * @param  text body text of Skill created
	 * @param  probability value for {@link Skill#probability}; can be null
	 */
	//////////////////////////////////////////////////////////////////////
	public void addSkill(String name, String category, String probability) {
		TutorMessage msg = (TutorMessage) getMsg();
		SkillElement skill = new SkillElement(name, category);
		if (probability != null)
			skill.setProbability(probability);
		msg.addSkill(skill);
	}
	
	/**
	 * Get the identifier from the {@link ContextMessage} associated with
	 * this message. The ContextMessage records the problem, course, unit,
	 * section, etc., environment in which this action was taken.
	 * @return contextMessageId
	 */
	public String getContextMessageId() {
		if (getMsg() != null)
			return getMsg().getContextMessageId();
		else
			return null;
	}
	
	/**
	 * Get the transaction identifier for this message. Each student attempt
	 * generates a transaction identifier, which is copied onto the tutor's
	 * response to that attempt.
	 * @return transcation identifier, if this is a {@link ToolMessage} or
	 *         {@link TutorMessage}; null otherwise
	 */
	public String getTransactionId() {
		EventElement ee = null;
		if (getMsg() instanceof TutorMessage)
			ee = ((TutorMessage) getMsg()).getEventElement();
		else if (getMsg() instanceof ToolMessage)
			ee = ((ToolMessage) getMsg()).getEventElement();
		if (ee instanceof SemanticEventElement)
			return ((SemanticEventElement)ee).getTransactionId();
		else
			return null;
	}
	
	//public
	public String getReplay()
	{
		return replay;
	}
	
	//private?
	public void setReplay(String newReplay)
	{
		this.replay = newReplay;
	}
	
	protected void parseCommonContent(Element msgElt, Namespace ns) throws JDOMException {
		super.parseCommonContent(msgElt, ns);
		
		{
			List elts = msgElt.getChildren(TutorActionLogV4.REPLAY);
			
			trace.out("log", "TutorActionLogV4.parseCommonContent("+msg+","+msgElt.getName()+") "+
					TutorActionLogV4.REPLAY+" count "+elts.size());
			Iterator it = elts.iterator();
			
			//only take the first, not sure why we'd have more than one
			if (it.hasNext())
				setReplay(((Element)it.next()).getText()); //otherwise the old one gets lost somewhere
		}
	}
	
}
