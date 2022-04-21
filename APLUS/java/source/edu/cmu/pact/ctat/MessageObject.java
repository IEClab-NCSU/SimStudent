/*
 * Copyright 2011 Carnegie Mellon University
 */
package edu.cmu.pact.ctat;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.CommManager.CommManager;
import edu.cmu.pact.TutoringService.TransactionInfo;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;

/**
 * The message used for communicating from tool to tutor and across parts of
 * the tutor. 
 * <p>Differences from {@link edu.cmu.pact.SocketProxy.XMLConverter}:
 * the logic that saved Integer and Boolean values instead of Strings is omitted.
 * In the old code, the static method buildValue(String name, String valStr) used
 * to convert as follows:
 * <tt>
 * 		if ("Rows".equals(name) || "Columns".equals(name)) {
 *			return (new Integer(valStr));
 *		} else if ("Number".equals(name)) {
 *			return (new Integer(valStr));
 *		} else if ("true".equals(valStr) || "false".equals(valStr)) {
 *			return (new Boolean(valStr));
 * </tt> 
 */
public class MessageObject implements Cloneable {

	/**
	 * For converting embedded XML to strings.
	 */
	private static XMLOutputter outputter =
		new XMLOutputter(
			    Format.getCompactFormat().setOmitDeclaration(true).setLineSeparator("").setIndent("")
		);

	/** {@link #properties} child tag. Message type used for dispatching. */
	public static final String MESSAGE_TYPE = "MessageType";

	/** {@link #properties} child tag. Element of (selection, action, input) tuple. */
	public static final String SELECTION = "Selection";

	/** {@link #properties} child tag. Element of (selection, action, input) tuple. */
	public static final String ACTION = "Action";

	/** {@link #properties} child tag. Element of (selection, action, input) tuple. */
	public static final String INPUT = "Input";

	/** A name-value pair for retaining property name in their original case. */
	private class NameValue implements Serializable {
		final String name;
		final Object value;
		NameValue(String name, Object value) {
			this(name, value, false);
		}
		NameValue(String name, Object value, boolean useValueAsIs) {
			this.name = name;
			if (value == null)
				this.value = null;  // was the word "null" in Comm message conversion
			else if (useValueAsIs)
				this.value = value;
			else if (!(value instanceof List))
				this.value = value.toString();   // convert numbers to strings
			else {
				if (((List) value).size() < 1)
					this.value = new Vector<String>();
				else if (((List) value).get(0) instanceof String) 
					((List) (this.value = new Vector<String>())).addAll((List) value);
				else
					((List) (this.value = new Vector())).addAll((List) value);
			}
		}

		public String toString() {
			StringBuffer sb = new StringBuffer("(");
			sb.append(name).append(',').append(value).append(")");
			return sb.toString();
		}
	}
	
	/** Top-level tag. */
	private static final String ROOT_TAG = "message";
	
	/** {@link #root} child tag. The verb was once used for dispatching, and may still be by TDK. */
	private static final String VERB_TAG = "verb";

	/** {@link #root} child tag. Most of the data are in this child element. */
	private static final String PROPERTIES_TAG = "properties";

	/** Special message meaning client wants to stop. */
	private static final String QUIT_MSG = "quit";

	/** Default verb value for messages from tutor to tool. */
	public static final String DEFAULT_VERB = "SendNoteProperty";
	
	/** Output generator for compact string output without preserving whitespace. */
	private static final XMLOutputter noWhiteSpaceOutputter = new XMLOutputter();
	static {
		Format fmt = Format.getCompactFormat();
		fmt.setExpandEmptyElements(false);
		fmt.setOmitDeclaration(true);
		fmt.setLineSeparator("");
		fmt.setIndent("");
		fmt.setTextMode(Format.TextMode.TRIM);
		noWhiteSpaceOutputter.setFormat(fmt);
	}
	
	/** Output generator for one-line string output. */
	private static final XMLOutputter singleLineOutputter = new XMLOutputter();
	static {
		Format fmt = Format.getRawFormat();
		fmt.setOmitDeclaration(true);
		fmt.setLineSeparator("");
		fmt.setIndent("");
		fmt.setTextMode(Format.TextMode.PRESERVE);
		singleLineOutputter.setFormat(fmt);
	}
	
	/** Output generator for multi-line string output. */
	private static final XMLOutputter multiLineOutputter = new XMLOutputter();
	static {
		Format fmt = Format.getPrettyFormat();
		fmt.setOmitDeclaration(true);
		fmt.setLineSeparator("\r\n");
		fmt.setIndent("    ");
		fmt.setTextMode(Format.TextMode.PRESERVE);
		multiLineOutputter.setFormat(fmt);
	}

	/** Whether this is a {@link #QUIT_MSG}. */
	private static final Element quitMsg = new Element(QUIT_MSG);

	/** Transaction identifier links messages in a common tutoring request-response action. */
	protected static final String TRANSACTION_ID_TAG = "transaction_id";

	/** Tag name for elements of list-valued properties. */
	public static final String VALUE_TAG = "value";

	/** Tag name for skill bar delimiter, needed for parsing skill bars into Skill elements. */
	public static final String SKILL_BAR_DELIMITER_TAG = "skillBarDelimiter";

	/**
	 * Create a unique string suitable for a {@link #getTransactionId()} value.
	 * @return "T" + {@link UUID#randomUUID()}.toString()
	 */
	public static String makeTransactionId() {
		return "T" + UUID.randomUUID().toString();
	}

	/** The XML tree that stores all the data. */
	private Element root = null;
	
	/** Second-level child. Most of the data are in this child element. */
	private Map<String, NameValue> properties = null;
	
	/** Original or generated text. */
	private String text = null;

	/** Convenience pointer to heavily-used property value. */
	private String messageType;
	
	/** See {@link #isLoggingSuppressed()}: whether this msg should be logged. */
	private boolean doNotLog = false;
	
	/** See {@link #lockTransactionId(String)}, {@link #setTransactionId(String)}. */
	private boolean isTransactionIdLocked = false;

	/**
	 * Convenience function for creating vector from string.
	 * @param s
	 * @return Vector with s as first element; null if s is null 
	 */
	public static Vector<String> s2v(String s) {
		if (s == null)
			return null;
		Vector<String> result = new Vector<String>();
		result.add(s);
		return result;
	}
	
	/**
	 * Construct from string, as when reading a socket or file.
	 * @param text XML string
	 * @return new instance built from XML
	 */
	public static MessageObject parse(String text) {
		if (text == null)
			throw new IllegalArgumentException("message text is null");
		MessageObject mo = new MessageObject();
		mo.text = text.trim();
		if (mo.isQuitMsg()) {
			mo.root = (Element) quitMsg.clone();        // don't let root be null
			return mo;
		}
		Document msgDoc = null;        // parse the string into an XML element
		try {
			Reader strIn = new StringReader(text);
			SAXBuilder builder = new SAXBuilder(false);
			msgDoc = builder.build(strIn);
		} catch (Exception e) {
			throw (new RuntimeException("error parsing XML message: document="+msgDoc, e));
		}
		mo.root = msgDoc.getRootElement();
		if (!ROOT_TAG.equalsIgnoreCase(mo.root.getName())) {  // perhaps a bundle of msgs
			List<Element> bundledMsgs = (List<Element>) mo.root.getChildren(ROOT_TAG);
			if (bundledMsgs != null && bundledMsgs.size() > 0)
				mo.root = bundledMsgs.get(bundledMsgs.size()-1);  // get the last one
		}
			
		mo.makeProperties(mo.root.getChild(PROPERTIES_TAG));
		return mo;
	}

	/** 
	 * Create from an already-parsed XML &lt;message&gt; Element.
	 * @param messageElement root element of message
	 * @return new instance with same contents
	 */
	public static MessageObject fromElement(Element messageElement) {
		MessageObject mo = new MessageObject();
		mo.root = new Element(ROOT_TAG);
		mo.setVerb(messageElement.getChildText(VERB_TAG));
		mo.makeProperties(messageElement.getChild(PROPERTIES_TAG));
		return mo;
	}
	
	/**
	 * @param elt Element to stringify
	 * @return result of {@link #outputter}.{@link XMLOutputter#outputString(Element) outputString(Element)}
	 */
	public static String element2String(Element elt) {
		return outputter.outputString(elt);
	}

	/**
	 * Return the content of the given property tag.
	 * @param xml message to scan
	 * @param property tag to find
	 * @return content between &lt;<i>property</i>&gt; and &lt;/<i>property</i>&gt;
	 */
	public static String getPropertyFromXML(String xml, String property)
	{
		if (xml == null)
			return null;
		String[] result = xml.split("<" + property + ">");
		if (result.length < 2)
			return null;
		else
			return (result[1]).split("</" + property + ">")[0];
	}
	
	/**
	 * Initialize the {@link #properties} Map from an Element.
	 * @param propertiesElt
	 */
	private void makeProperties(Element propertiesElt) {
		properties = new LinkedHashMap<String, NameValue>();
		if (propertiesElt == null)
			return;
    	if (trace.getDebugCode("log")) trace.out("log", "in makeProperties(Element)"
    			+ "\npropertiesElt "+multiLineOutputter.outputString(propertiesElt));
		int childIndex = -1;
		for (Object obj : propertiesElt.getChildren()) {
			childIndex++;
			if (!(obj instanceof Element)) {
				trace.err("non-element child at "+PROPERTIES_TAG+" child #"+childIndex+":\n  "+text);
				continue;
			}
			Element propertyElt = (Element) obj;

			NameValue nv = null;
			String name = propertyElt.getName();	// Parse the message property name
			if (name == null || name.length() < 1) {
				trace.err("tag name null or empty in "+PROPERTIES_TAG+" element child #"+childIndex+":\n  "+text);
				continue;
			}
			// Parse the value(s) of the property
            List childList = propertyElt.getChildren();
            if (childList == null || childList.size() < 1) {
                String text = propertyElt.getText();
				nv = new NameValue(name, text);
            } else if (!(childList.get(0) instanceof Element)) {
            	trace.err("Message property child "+name+
            			" not an element:\n  "+outputter.outputString(propertyElt));
            } else if (VALUE_TAG.equalsIgnoreCase(((Element) childList.get(0)).getName())) {
                List<String> values = new Vector<String>();
                for (Iterator valueIt = childList.iterator(); valueIt.hasNext();) {
                    Element valueElt = (Element) valueIt.next();
                    String value = valueElt.getText();
                    values.add(value);
                }
				nv = new NameValue(name, values, true);  // true: have cloned list already
            } else if (childList.size() < 2) {           // single non <value> Element
            	nv = new NameValue(name, childList.get(0), true);
            } else {                                     // multiple non <value> Elements
            	if (trace.getDebugCode("msg"))
            		trace.out("msg", "Message property child "+name+
            				" has > 1 child elements:\n  "+outputter.outputString(propertyElt));
            	nv = new NameValue(name, new Vector(childList), true);
            }
			properties.put(name.trim().toLowerCase(), nv);
		}
		if (trace.getDebugCode("msg")) trace.out("msg", "makeProperties result:\n  "+properties);
	}

	/**
	 * No-arg constructor for factory use.
	 */
	protected MessageObject() {}

	/**
	 * Create with given verb and properties from an instance already built.
	 * @param verb
	 * @param properties will make a shallow copy 
	 */
	protected MessageObject(MessageObject mo) {
    	if (trace.getDebugCode("log")) trace.out("log", "in MessageObject(MessageObject mo)");
		init(mo.getMessageType());
		setVerb(mo.getVerb());
		for (NameValue property : mo.properties.values()){
			if (trace.getDebugCode("log")) trace.out("log", "in MO, name: "+property.name+" value: "+property.value);
			setProperty(property.name, property.value);
		}
	}

	/**
	 * Create with default verb {@value #DEFAULT_VERB} and given MessageType.
	 * @param messageType
	 * @return new instance
	 */
	public static MessageObject create(String messageType) {
		return create(messageType, (String) null);
	}

	/**
	 * Create with default verb {@value #DEFAULT_VERB} and given MessageType.
	 * @param messageType
	 * @return new instance
	 */
	public static MessageObject create(String messageType, String verb) {
		MessageObject result = new MessageObject(); 
		result.init(messageType);
		if (verb != null)
			result.setVerb(verb);
		return result;
	}
	
	/**
	 * Initialize the root, verb and properties elements from scratch.
	 * @param messageType
	 */
	protected void init(String messageType) {
		text = null;
		root = new Element(ROOT_TAG);
		setVerb(DEFAULT_VERB);
		makeProperties(null);
		setMessageType(messageType);
	}

	/**
	 * @param messageType new value for 
	 */
	public void setMessageType(String messageType) {
		setProperty(MESSAGE_TYPE, messageType);
		this.messageType = messageType;
		text = null;
	}

	/**
	 * Create or edit the {@link #VERB_TAG} element.
	 * @param verb new value 
	 */
	public void setVerb(String verb) {
		Element elt = null;
		if ((elt = root.getChild(VERB_TAG)) == null) {
			elt = new Element(VERB_TAG);
			root.addContent(elt);
		} 
		elt.setText(verb);
		text = null;
	}

	/**
	 * Tell whether this instance is the special {@link #QUIT_MSG}, which is
	 * sent when the client wants to stop.
	 * @return true if {@link #text} matches {@value #QUIT_MSG}
	 */
	public boolean isQuitMsg() {
		return QUIT_MSG.equalsIgnoreCase(text);
	}

	/**
	 * @return message matching {@link #QUIT_MSG}
	 */
	public static MessageObject makeQuitMessage() {
		return parse(QUIT_MSG);
	}

	/**
	 * Dump a summary of this message.
	 * @return result from {@link #summary(MessageObject)}
	 */
	public String summary() {
		return summary(this);
	}

	/**
	 * Dump a summary of a message.
	 * @param mo
	 * @return summary with, at least, the message type.
	 */
	public static String summary(MessageObject mo) {
		if(mo == null)
			return null;
		String type = mo.getMessageType();
		StringBuilder sb = new StringBuilder(type);
		if(MsgType.INTERFACE_ACTION.equalsIgnoreCase(type)
				|| MsgType.UNTUTORED_ACTION.equalsIgnoreCase(type)) {
			sb.append(": SAI[").append(mo.getSelection());
			sb.append(',').append(mo.getAction());
			sb.append(',').append(mo.getInput()).append("]");
			sb.append(" TxID ").append(mo.getTransactionId());
		} else if(MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(type)) {
			sb.append(": ").append(mo.getProperty("WidgetType"));
			sb.append("[").append(mo.getProperty("CommName")).append("]");
		} else if(MsgType.START_PROBLEM.equalsIgnoreCase(type)) {
			sb.append(": ").append(mo.getProperty("ProblemName"));
		} else if(MsgType.SET_PREFERENCES.equalsIgnoreCase(type)) {
			String brdFile = (String) mo.getProperty(Logger.QUESTION_FILE_PROPERTY);
			if(brdFile == null)
				brdFile = (String) mo.getProperty("ProblemName");
			sb.append(": ").append(brdFile);
			sb.append(", ").append(mo.getProperty(Logger.STUDENT_NAME_PROPERTY));
			sb.append(", ").append(mo.getProperty(Logger.SESSION_ID_PROPERTY));
		}
		return sb.toString();
	}
	
	/**
	 * @return single-line XML string, with no trailing newline. Refreshes cached {@link #text}.
	 */
	public String toString() {
		if (text == null) {
			synchronized(this) {
				if (text == null) {
					makePropertiesElement();
					text = singleLineOutputter.outputString(root);
					if(trace.getDebugCode("msgtext"))
						trace.outNT("msgtext", "MO.toString()\n  map: "+properties+"\n  txt: "+text);
				}
			}
		}
		return text;
	}
	
	/**
	 * @return single-line XML string, with no trailing newline. Refreshes cached {@link #text}.
	 */
	public String toMinimalXML() {
		Element root = makeXMLPropertiesElement();
		String result = noWhiteSpaceOutputter.outputString(root);
		if(trace.getDebugCode("msgtext"))
			trace.outNT("msgtext", "MO.toMinimalXML()\n  map: "+properties+"\n  txt: "+result);
		return result;
	}
	
	private Element makeXMLPropertiesElement() {
    	if (trace.getDebugCode("log")) trace.out("log", "in makeXMLPropertiesElement()");
		boolean found = root.removeChild(PROPERTIES_TAG);  // ensure fresh copy of properties info
		if (trace.getDebugCode("msg")) trace.out("msg", "old properties tag "+(found ? "found" : "not found")+
				", map:\n  "+properties);
		Element pElt = new Element(PROPERTIES_TAG);
		for (String key : properties.keySet()) {
			NameValue nv = properties.get(key);
			Element elt = new Element(nv.name);
			if (nv.value == null) {
				elt.setText("");
			} else if (nv.value instanceof List && ((List) nv.value).size() > 0) {
				List values = (List) nv.value;
				Object value0 = values.get(0);
				if (value0 instanceof Element) {   // add Elements as elements to preserve outputting
					for (Object v : values) {
						elt.addContent((Element)((Element) v).clone());
					}
				} else {
					for (Object v : values) {
						Element vElt = new Element(VALUE_TAG);
						vElt.setText(v == null ? "" : v.toString());
						elt.addContent(vElt);
					}
				}
			} else if (nv.value instanceof Element) {
				elt.addContent((Element)((Element) nv.value).clone());
			} else {
				elt.setText(nv.value.toString());
			}
			pElt.addContent(elt);
		}
		root.addContent(pElt);
		text = null;                 // saved string now invalid
		return pElt;
	}

	/**
	 * @return multi-line indented XML string. Refreshes cached {@link #text}.
	 */
	public String toXML() {
		if (text == null)
			makePropertiesElement();
		return multiLineOutputter.outputString(root);
	}
	
	/**
	 * @return {@link #root}, after refreshing {@link #text} if needed.
	 */
	public Element toElement() {
		if (text == null)
			makePropertiesElement();
		return (Element) root.clone();
	}

	/**
	 * @return {@value #PROPERTIES_TAG} child of {@link #root}, after refreshing, if needed. 
	 */
	public Element getPropertiesElement() {
		if (text == null)
			makePropertiesElement();
		return (Element) root.getChild(PROPERTIES_TAG).clone();
	}

	/**
	 * Generate the &lt;{@value #PROPERTIES_TAG}&gt; element from the {@link #properties} map.
	 */
	private synchronized void makePropertiesElement() {
    	if (trace.getDebugCode("log")) trace.out("log", "in makePropertiesElement()");
		boolean found = root.removeChild(PROPERTIES_TAG);  // ensure fresh copy of properties info
		if (trace.getDebugCode("msg")) trace.out("msg", "old properties tag "+(found ? "found" : "not found")+
				", map:\n  "+properties);
		Element pElt = new Element(PROPERTIES_TAG);
		for (String key : properties.keySet()) {
			NameValue nv = properties.get(key);
			Element elt = new Element(nv.name);
			if (nv.value == null) {
				elt.setText("");
			} else if (nv.value instanceof List && ((List) nv.value).size() > 0) {
				List values = (List) nv.value;
				Object value0 = values.get(0);
				if (value0 instanceof Element) {   // add Elements as elements to preserve outputting
					for (Object v : values) {
						elt.addContent((Element)((Element) v).clone());
					}
				} else {
					for (Object v : values) {
						Element vElt = new Element(VALUE_TAG);
						vElt.setText(v == null ? "" : v.toString());
						elt.addContent(vElt);
					}
				}
			} else if (nv.value instanceof Element) {
				elt.addContent((Element)((Element) nv.value).clone());
			} else {
				elt.setText(nv.value.toString());
			}
			pElt.addContent(elt);
		}
		root.addContent(pElt);
		text = null;                 // saved string now invalid
	}

	/**
	 * Semantic event identifier of linked event. This call sets {@link #isTransactionIdLocked} to
	 * prevent {@link #setTransactionId(String)} from changing the value later.
	 * @param id new value for {@link #getTransactionId()} 
	 */
	public void lockTransactionId(String id) {
		if (id == null || id.length() < 1)
			throw new IllegalArgumentException("lockTranactionId() argument \""+id+"\" must be a valid id");
		setPropertyInternal(TRANSACTION_ID_TAG, id, false);
		isTransactionIdLocked = true;
	}

	/**
	 * Semantic event identifier of linked event. No-op if {@link #isTransactionIdLocked} is true.
	 * @param id new value for {@link #getTransactionId()}
	 */
	public void setTransactionId(String id) {
		if (isTransactionIdLocked)
			return;
		if (id == null || id.length() < 1) {
			if (trace.getDebugCode("msg"))
				trace.out("msg", "setTranactionId() invalid id \""+id+"\"; replacing with makeTransactionId()");
			id = makeTransactionId();
		}
		setPropertyInternal(TRANSACTION_ID_TAG, id, false);
	}

	/**
	 * Set a value in the {@link #properties} map. Nulls {@link #text} to force update.
	 * @param propertyName key in {@link #properties} will be trimmed, in lower case
	 * @param propertyValue
	 * @param useAsIs for {@link MessageObject.NameValue#NameValue(String, Object, boolean)}
	 */
	protected void setPropertyInternal(String propertyName, Object propertyValue, boolean useAsIs) {
		NameValue nv = new NameValue(propertyName, propertyValue, useAsIs);
		properties.put(propertyName.trim().toLowerCase(), nv);
		text = null;
	}

	/**
	 * Set a value in the {@link #properties} map. Nulls {@link #text} to force update.
	 * This public method is sensitive to {@link #isTransactionIdLocked}.
	 * @param propertyName key in {@link #properties} will be trimmed, in lower case
	 * @param propertyValue
	 */
	public void setProperty(String propertyName, Object propertyValue) {
		setProperty(propertyName, propertyValue, propertyValue instanceof Element);
	}

	/**
	 * Set a value in the {@link #properties} map. Nulls {@link #text} to force update.
	 * This public method is sensitive to {@link #isTransactionIdLocked}.
	 * @param propertyName key in {@link #properties} will be trimmed, in lower case
	 * @param propertyValue
	 * @param useAsIs don't try to convert value type
	 */
	public void setProperty(String propertyName, Object propertyValue, boolean useAsIs) {
		if (TRANSACTION_ID_TAG.equalsIgnoreCase(propertyName))
			setTransactionId(propertyValue == null ? null : propertyValue.toString());
		else
			this.setPropertyInternal(propertyName, propertyValue, useAsIs);
	}

	/**
	 * Get a value from the {@link #properties} map.
	 * @param propertyName search will be case-insensitive
	 * @return
	 */
	public Object getProperty(String propertyName) {
		if (properties == null)
			return null;
		NameValue nv = properties.get(propertyName.trim().toLowerCase());
		if (nv == null)
			return null;
		else if (nv.value instanceof List && ((List) nv.value).get(0) instanceof String) {
			Vector<String> newListValue = new Vector<String>();
			newListValue.addAll((List) nv.value);
			return newListValue;
		} else
			return nv.value;
	}

	/**
	 * @param messageType type to test
	 * @return true if argument matches (ignoring case), {@link #getMessageType()}
	 */
	public boolean isMessageType(String messageType) {
		String myMsgType = getMessageType();
		if(messageType == null)
			return (myMsgType == null);
		else
			return messageType.equalsIgnoreCase(myMsgType);
	}

	/**
	 * @return {@link #messageType}; if null, tries to set from {@link #properties}
	 */
	public String getMessageType() {
		if (messageType == null)
			messageType = (String) getProperty(MESSAGE_TYPE);
		return messageType;
	}

	/**
	 * Convenience method to test whether one of the given types matches this message's
	 * {@link #getMessageTypeProperty()}.
	 * @param types
	 * @return true if any element of types[] matches (case-insensitive)
	 */
    public boolean isMessageType(String[] types) {
        for (String msgType: types) {
            if (isMessageType(msgType))
                return true;
        }
        return false;
    }

    /**
     * @return value of {@value #TRANSACTION_ID_TAG} property
     */
	public String getTransactionId() {
		return (String) getProperty(TRANSACTION_ID_TAG);
	}

	/**
	 * @return text content of the {@value #VERB_TAG} element, if any
	 */
	public String getVerb() {
		Element vElt = null;
		if (root == null || (vElt = root.getChild(VERB_TAG)) == null)
			return null;
		return vElt.getText();
	}

	/**
	 * @param selection vector; will copy before saving
	 */
	public void setSelection(Vector<String> selection) {
		setProperty(SELECTION, (Vector<String>) selection);
	}

	/**
	 * @param selection vector first element; will copy before saving
	 */
	public void setSelection(String selection) {
		setProperty(SELECTION, s2v(selection));
	}

	/**
	 * @return result of {@link #getProperty(String) getProperty({@value #SELECTION})}
	 */
	public Vector<String> getSelection() {
		return (Vector<String>) getProperty(SELECTION);
	}

	/**
	 * @param action vector; will copy before saving
	 */
	public void setAction(Vector<String> action) {
		setProperty(ACTION, (Vector<String>) action);
	}

	/**
	 * @param action vector first element; will copy before saving
	 */
	public void setAction(String action) {
		setProperty(ACTION, s2v(action));
	}

	/**
	 * @return result of {@link #getProperty(String) getProperty({@value #ACTION})}
	 */
	public Vector<String> getAction() {
		return (Vector<String>) getProperty(ACTION);
	}

	/**
	 * @param input vector; will copy before saving
	 */
	public void setInput(Vector<String> input) {
		setProperty(INPUT, (Vector<String>) input);
	}

	/**
	 * @param input vector first element; will copy before saving
	 */
	public void setInput(String input) {
		setProperty(INPUT, s2v(input));
	}

	/**
	 * @return result of {@link #getProperty(String) getProperty({@value #INPUT})}
	 */
	public Vector<String> getInput() {
		return (Vector<String>) getProperty(INPUT);
	}

	/**
	 * @return {@link #clone()}
	 */
	public MessageObject copy() {
		return (MessageObject) clone();
	}

	/**
	 * @return deep copy of this instance
	 */
	protected Object clone() {
		MessageObject result = parse(toString());
		return result;
	}

	/**
	 * @return all value fields of {@link MessageObject.NameValue} elements in {@link #properties};
	 *         like {@link #getPropertyNames()}, skips entries with null names
	 */
	public List getPropertyValues() {
		List values = new ArrayList();
		for (NameValue nv : properties.values()) {
			if (nv == null || nv.name == null)
				continue;
			values.add(nv.value);
		}
		return values;
	}

	/**
	 * @return all non-null names of {@link MessageObject.NameValue} elements in {@link #properties}
	 */
	public List<String> getPropertyNames() {
		List<String> names = new ArrayList<String>();
		for (NameValue nv : properties.values()) {
			if (nv == null || nv.name == null)
				continue;
			names.add(nv.name);
		}
		return names;
	}
	
	/**
	 * @param b new value for {@link #doNotLog}
	 */
	public void suppressLogging(boolean b) {
		this.doNotLog = b;
	}
	
	/**
	 * @return {@link #doNotLog}
	 */
	public boolean isLoggingSuppressed() {
		return doNotLog;
	}

	/**
	 * Retrieve a property and convert to Integer.
	 * @param propertyName
	 * @return null if cannot convert value; original property must be scalar
	 */
	public Integer getPropertyAsInteger(String propertyName) {
		try {
			Object obj = getProperty(propertyName);
			if (obj instanceof Integer) return (Integer) obj;
			Integer result = Integer.valueOf((String) obj);
			return result;
		} catch (Exception e) {
			trace.errStack("cannot convert property "+propertyName+" to int", e);
			return null;
		}
	}

	/**
	 * Retrieve a property and convert to Boolean.
	 * @param propertyName
	 * @return null if cannot convert value; original property must be scalar
	 */
	public Boolean getPropertyAsBoolean(String propertyName) {
		try {
			Object obj = getProperty(propertyName);
			if (obj == null)
				return null;
			Boolean result = Boolean.valueOf((String) obj);
			return result;
		} catch (Exception e) {
			trace.errStack("cannot convert property "+propertyName+" to Boolean", e);
			return null;
		}
	}
	
	public static boolean showMessage = false;

	public synchronized void send(Target destinationTarget){
		
		Integer messageInt = new Integer (0);
		messageInt = (Integer)(getProperty("MESSAGENUMBER"));
		
		//trace.out (5, this, "sending message. messageInt = " + messageInt);
		if (messageInt == null) {
			this.setProperty("MESSAGENUMBER", Communicator.messageNumber);
			Communicator.addMessage(Communicator.messageNumber, this); 
			Communicator.messageNumber++;
		}
		
			
		try {
			CommManager.instance().sendJavaMessage(this);
		} catch (NullPointerException e) {	
			trace.err("Can't find CommManager: null pointer exception");
		} catch (java.lang.NoClassDefFoundError e) {
			trace.err ("Can't find CommManager: no class def found");
		} catch (Exception e) { 
			trace.err ("CommManager: exception = " + e);
		}

		
		if(showMessage && destinationTarget != null)
		  destinationTarget.transmitEvent(this);

		
	}
	
	//getParsedParameterString is used for debugging. It prints out the parameters parsed and their
	//values
	public String getParsedParameterString() {
		String outstring = "";
		List Names = this.getPropertyNames();
		List Values = this.getPropertyValues();
		for (int i=0;i<Names.size();++i)
			outstring = outstring + Names.get(i)+"="+Values.get(i)+"|";
		return outstring;
	}

	/** Carries information about the transaction initiated by the this message. */
	private transient TransactionInfo.Single transactionInfo;

    private static boolean cmp(Object o1, Object o2) {
        if (o1==null)
            return o2==null;
        if (o2==null)
            return o1==null;
        if (o1 instanceof String && o2 instanceof String)
            return ((String)o1).equalsIgnoreCase((String)o2);
        return o1.equals(o2);
    }

    /**
     * Tell if the named property has the given value. If the property is vector-valued,
     * returns true if any element of the vector matches. String comparisons are 
     * case-insensitive.
     * @param name
     * @param value
     * @return true if finds a match
     */
    public boolean matchProperty(String name, Object value) {
        Object property = getProperty(name);

        trace.out("sp", "matchProperty(" + name + ", " + value + ", (" + property.getClass() + ")" + property + ")");
        if (property instanceof Vector) {
            for (Object o: (Vector)property) {
                if (cmp(value, o))
                    return true;
            }
            return false;
        } else
            return cmp(value, property);
    }

    /**
     * Add an element to a vector-valued property with the given name.  If there's
     * no property by this name, creates one. 
     * @param name property name 
     * @param value value for new vector element
     * @throws IllegalStateException if finds existing property but it's not vector-valued
     */
    public void addPropertyElement(String name, Object value) {
        Object prev = getProperty(name);
        trace.out("sp", "addPropertyElement(" + name + ", " + value + ") before: " + prev);
        Vector v;

        if (prev instanceof Vector) {
            v = (Vector)prev;
            v.add(value);
        } else if (prev == null) {
        	v = new Vector();
        	v.add(value);
            setProperty(name, value, true);
        } else
        	throw new IllegalStateException("addPropertyElement(" + name + ", " + value +
        			"): property already exists with value " + prev);
        trace.out("sp", "addPropertyElement(" + name + ", " + value + ") after: " + getProperty(name));
    }

    /**
     * If this message came from a DataShop log entry, record the XML of the
     * tool, tutor or context_message.
     * @return null : not yet implemented
     */
	public String getDataShopElementString() {
		try {
			throw new RuntimeException("Not yet implemented");
		} catch (Exception e) {
			trace.errStack("DataShop element for MessageObject", e);
			return null;
		}
	}

	/**
	 * Convenience method to get the first element of the selection property.
	 * @return first element or null
	 */
	public String getFirstSelection() {
		return getSelection0();
	}

	/**
	 * Convenience method to get the first element of the selection property.
	 * @return first element or null
	 */
	public String getSelection0() {
		Vector<String> selections = getSelection();
		if (selections == null || selections.size() < 1)
			return null;
		else
			return selections.get(0);
	}

	/**
	 * Convenience method to get the first element of the action property.
	 * @return first element or null
	 */
	public String getFirstAction() {
		return getAction0();
	}

	/**
	 * Convenience method to get the first element of the action property.
	 * @return first element or null
	 */
	public String getAction0() {
		Vector<String> actions = getAction();
		if (actions == null || actions.size() < 1)
			return null;
		else
			return actions.get(0);
	}

	/**
	 * Convenience method to get the first element of the input property.
	 * @return first element or null
	 */
	public String getFirstInput() {
		return getInput0();
	}

	/**
	 * Convenience method to get the first element of the input property.
	 * @return first element or null
	 */
	public String getInput0() {
		Vector<String> inputs = getInput();
		if (inputs == null || inputs.size() < 1)
			return null;
		else
			return inputs.get(0);
	}

	/**
	 * Extract the desired value from propertyValues and return it.
	 * Holdover from old message class.
	 * @param propertyNames
	 *            Property name vector from Comm message
	 * @param propertyValues
	 *            Property value vector from Comm message
	 * @param propertyName
	 *            The property name of the value being sought
	 * 
	 * The property value requested, or null if not found
	 */
	static public Object getValue(Vector propertyNames, Vector propertyValues,
	        String propertyName) {
	    int pos = fieldPosition(propertyNames, propertyName);
	
	    if (pos != -1)
	        return propertyValues.elementAt(pos);
	
	    return null;
	}

	/**
	 * Extracts a field position from a comm message vector
	 */
	public static int fieldPosition(Vector from, String fieldName) {
	    int toret = -1;
	    int s = from.size();
	    for (int i = 0; i < s; i++) {
	        Object o = from.elementAt(i);
	        if (((String) o).equalsIgnoreCase(fieldName))
	            return i;
	    }
	
	    return toret;
	}

	/**
	 * Set a {@link #transactionInfo} only if not already set.
	 * @param transactionInfo value for {@link #transactionInfo}
	 */
	public void setTransactionInfo(TransactionInfo.Single transactionInfo) {
		if (this.transactionInfo == null)
			this.transactionInfo = transactionInfo;
	}

	/**
	 * @return the {@link #transactionInfo}
	 */
	public TransactionInfo.Single getTransactionInfo() {
		return transactionInfo;
	}
}
