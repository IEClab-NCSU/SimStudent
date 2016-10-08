package edu.cmu.pact.Log;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.server.UID;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.oli.log.client.ActionLog;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;

/**
 * A log message whose body is derived from an XML element conforming to
 * the OLI logging specification for tutor-related messages.
 */
public class AuthorActionLog extends ActionLog implements TextIsString {

    /** Default source value for the oli action log. */
    public static final String DEFAULT_SOURCE = "DATASHOP";

    /** Default time zone if none in msg. */
    public static final String DEFAULT_TIME_ZONE = "UTC";

    /** Element type of message sequence, from log file or data base report. */
    public static final String MSG_SEQUENCE_ELEMENT =
        "tutor_related_message_sequence";

    /** Name of message sequence element attribute {@link #VERSION_NUMBER}. */
    public static final String VERSION_NUMBER_ATTR = "version_number";

    /** Attribute value of DTD version this class can accept and generate. */
    public static final String VERSION_NUMBER = "2";

    /** Element type of curriculum system messages. */
    public static final String CURRICULUM_MSG_ELEMENT = "curriculum_message";

    /** Element type of other messages. */
    public static final String MSG_ELEMENT = "message";

    /** Element type for problem name. */
    public static final String PROBLEMNAME_ELEMENT = "problem_name";

    /** Element type for problem name. */
    public static final String SCHOOLNAME_ELEMENT = Logger.SCHOOL_NAME_PROPERTY;

    /** Element type for problem name. */
    public static final String COURSENAME_ELEMENT = "course_name";

    /** Element type for problem name. */
    public static final String UNITNAME_ELEMENT = "unit_name";

    /** Element type for problem name. */
    public static final String SECTIONNAME_ELEMENT = "section_name";

    /** Element type for property. */
    public static final String PROPERTY_ELEMENT = "property";

    /** Element type for custom_field. */
    public static final String CUSTOM_FIELD_ELEMENT = "custom_field";
    /** Element type for custom_field:name. */
    public static final String CUSTOM_FIELD_NAME_ELEMENT = "name";
    /** Element type for custom_field:value. */
    public static final String CUSTOM_FIELD_VALUE_ELEMENT = "value";

    /** Fixed info_type value for all messages: refers to DTD. */
    static final String INFO_TYPE = "author_message.dtd";

    /** Empty iterator for empty lists. */
    private static Iterator emptyIterator = EmptyIterator.getInstance();

    /** Formatter to interpret time field with millisecond granularity. */
    private static DateFormat dateFmtMS =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");

    /** Formatter to interpret time field with second granularity. */
    private static DateFormat dateFmtSS =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    /** Formatter to interpret time field with minute granularity. */
    private static DateFormat dateFmt =
        new SimpleDateFormat("yyyy-MM-dd HH:mm z");

    /**
     * Formatter for toString().  Pretty-print, preserve XML declaration
     *    "<code>&lt;?xml ... &gt;</code>", include encoding in declaration,
     * use \n for line separator.
     */
    private static XMLOutputter outputter =
        new XMLOutputter(Format.getPrettyFormat().setIndent(" ").setOmitEncoding(false).
                         setOmitDeclaration(false).setLineSeparator("\n"));

    /**
     * Top-level element tag: {@link #AuthorAction.AUTHOR_MSG_ELEMENT} 
     * or {ProgramAction.PROGRAM_MSG_ELEMENT}.
     */
    private String topElementType = null;

    /** Attempt identifier: see DTD. */
    private String attemptId = null;

    /** Message name attribute, more like a message sub type. */
    private String name = null;

    /** ProblemName object. */
    private String problemName = null;

    /** Custom fields in message. */
    private Map customFields = null;

    /** SchoolName object. */
    private String schoolName = null;

    /** CourseName object. */
    private String courseName = null;

    /** UnitName object. */
    private String unitName = null;

    /** SectionName object. */
    private String sectionName = null;

    /** MsgProperty objects in message. Entry type is {@link MsgProperty}. */
    private List msgProperties = null;

	
	// action type element
	public static final String ACTION_TYPE_ELEMENT = "action_type";
	
	// argument element 
	public static final String ARGUMENT_ELEMENT = "argument";
	
	// result element 
	public static final String RESULT_ELEMENT = "result";
	
	// resultDetails element
	public static final String RESULT_DETAILS_ELEMENT = "result_details";
	
	// action type info
	private String actionType = null;
	
	// argument info
	private String argument = null;
	
	// result info
	private String result = null;
	
	// resultDetails info
	private Object resultDetails = null;
	
	// valid tool names
	public static final String 	STUDENT_INTERFACE = "STUDENT_INTERFACE",
								BEHAVIOR_RECORDER = "BEHAVIOR_RECORDER",
								CTAT_WINDOW = "CTAT_WINDOW",
								DOCKING_WINDOW = "DOCKING_WINDOW",
								JESS_CONSOLE = "JESS_CONSOLE",
								ECLIPSE = "ECLIPSE",
								EXTERNAL_EDITOR = "EXTERNAL_EDITOR",
								CONFLICT_TREE = "CONFLICT_TREE",
								WHY_NOT_WINDOW = "WHY_NOT_WINDOW",
								WORKING_MEMORY_EDITOR = "WORKING_MEMORY_EDITOR",
								SIM_STUDENT_PLE = "SIM_STUDENT_PLE",
								SKILLS_CONSOLE = "SKILLS_CONSOLE";
	
	/*
	 * All actionType constants are in their files, depending on which toolName
	 * they correspond to, as follows:
	 * BEHAVIOR_RECORDER : edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller
	 * CTAT_WINDOW : edu.cmu.pact.Utilities.CTATWindow
	 * JESS_CONSOLE : edu.cmu.pact.jess.JessConsole
	 * CONFLICT_TREE : edu.cmu.pact.jess.RuleActivationTree
	 * WHY_NOT_WINDOW : edu.cmu.pact.jess.WhyNot
	 * WORKING_MEMORY_EDITOR : edu.cmu.pact.jess.WMEEditor
	 */
	
	// WPI loggings
	public static final String WPI_ACTION_HANDLER = "WPI_ACTION_HANDLER";
	public static final String WPI_ACTIONLABEL_HANDLER = "WPI_ACTIONLABEL_HANDLER";
	public static final String WPI_RULELABEL_HANDLER = "WPI_RULELABEL_HANDLER";
	
	
	public AuthorActionLog (String actionType, 
							String argument,
							String result,
							Object resultDetails) {
		if (trace.getDebugCode("log"))
			trace.printStack("log", "AuthorActionLog("+actionType+","+argument+","+result+","+resultDetails+")");
		this.actionType = (actionType == null ? "" : actionType); 
		this.argument = (argument == null ? "" : argument); 
		this.result = (result == null ? "" : result);
		this.resultDetails = resultDetails;//(resultDetails.toString() == null ? "" : resultDetails.toString());
	}
	
	public AuthorActionLog (String actionType, String argument)
	{
		this(actionType, argument, null, null);
	}
	
	public AuthorActionLog (String result, Object resultDetails)
	{
		this(null, null, result, resultDetails);
	}
	
	protected void  addChildElements (Element rootEle){
		
		if (actionType != null) {
			rootEle.addContent(stringToElement(ACTION_TYPE_ELEMENT, actionType));
		}
		
		if (argument != null) {
			rootEle.addContent(stringToElement(ARGUMENT_ELEMENT, argument));
		}
		
		if (result != null) {
			rootEle.addContent(stringToElement(RESULT_ELEMENT, result));
		}
		
		if (resultDetails != null) {
//			trace.err("!1 "+resultDetails);
			rootEle.addContent(stringToElement(RESULT_DETAILS_ELEMENT, resultDetails.toString()));
//			trace.err("!2 "+outputter.outputString(rootEle));
		}
	
		return;
	}
	
	
	/**
	 * Return the actionType.
	 * @return the text
	 */
	public String getActionType() { return actionType; }
	
	/**
	 * Return the argument.
	 * @return the text
	 */
	public String getArgument() { return argument; }
	
	/**
	 * Return the result.
	 * @return the total number of hints available
	 */
	public String getResult() { return result; }
	
	/**
	 * Return the resultDetails.
	 * @return the total number of hints available
	 */
	public Object getResultDetails() { return resultDetails; }
	
    /**
     * Constructor for building log message from values.
     * Throws IllegalArgumentException if topElementType not defined, though
     * this is an unchecked exception.
     *
     * @param  topElementType top-level element name: {@link #TOOL_MSG_ELEMENT}
     *             or {@link #TUTOR_MSG_ELEMENT}
     *             or {@link #CURRICULUM_MSG_ELEMENT} or {@link #MSG_ELEMENT}
     */
    public AuthorActionLog (String topElementType) {
		// zz may need tutor actionlogs or author logs
        if (CURRICULUM_MSG_ELEMENT.equalsIgnoreCase(topElementType)) {
            this.topElementType = CURRICULUM_MSG_ELEMENT;
        } else if (MSG_ELEMENT.equalsIgnoreCase(topElementType)) {
            this.topElementType = MSG_ELEMENT;
        } else {
            throw new IllegalArgumentException("Undefined element type: " + topElementType);
        }
    }
	
	protected void setTopElementType (String topElementType) {
		this.topElementType = topElementType;
	}
	
	public AuthorActionLog () {
		
	}

    /**
     * Constructor for XML Element input.
     * Throws a RuntimeException instead of the JDOMException.
     * @param elt XML element to analyze
     */
    public AuthorActionLog (Element elt) {
        try {
            parseElement(elt);
        } catch (JDOMException de) {
            Throwable cause = de.getCause();
            if (cause != null) {
                cause.printStackTrace();
            } else {
                de.printStackTrace();
            }
            throw(new RuntimeException("XML exception = " + de.toString()
                                       + (cause == null ? "" : "; cause " + cause)));
        }
    }

    /**
     * Constructor for String input: creates instance from first
     * {@link #TOOL_MSG_ELEMENT}, {@link #TUTOR_MSG_ELEMENT}
     * {@link #CURRICULUM_MSG_ELEMENT} or {@link #MSG_ELEMENT}
     * element in the input string, ignores all others.
     *
     * @param  xmlStr String of XML with at least one message element
     * @return TutorActionLog from first element; null on error
     */
    public static AuthorActionLog factory(String xmlStr) {
        Iterator it = factoryIterator(new StringReader(xmlStr));
        return (AuthorActionLog) it.next();
    }

    /**
     * Create a factory that will return a Iterator whose next() method
     * will (successively) create TutorActionLog instances for each log
     * element in the given Reader.
     * Builds Document from Reader, returns Iterator on top-level child
     * elements.  Accepts root element of name
     * {@link AuthorActionLog#MSG_SEQUENCE_ELEMENT} for sequence.
     * Also will generate single-member iterator if root element
     * is {@link #AuthorAction.AUTHOR_MSG_ELEMENT}, 
     * {@link #ProgramAction.PROGRAM_MSG_ELEMENT},
     * {@link #CURRICULUM_MSG_ELEMENT} or {@link #MSG_ELEMENT}.
     *
     * @param  xmlStr the xml string to iterator over
     * @return Iterator whose next() method returns a TutorActionLog instance
     * @throws JDOMException thrown if xml is invalid
     */
    public static AuthorActionLog myFactory(String xmlStr) throws JDOMException {
        Iterator it = myFactoryIterator(new StringReader(xmlStr));
        return (AuthorActionLog) it.next();
    }

    /**
     * My Factory Iterator.
     * @param rdr the thing to read from
     * @return an iterator
     * @throws JDOMException thrown if xml is invalid
     */
    public static Iterator myFactoryIterator(Reader rdr) throws JDOMException {
            try {
                return new Factory(rdr);
            } catch (IOException exception) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                cause.printStackTrace();
            } else {
                exception.printStackTrace();
            }
            throw(new RuntimeException("XML exception = " + exception.toString()
                                       + (cause == null ? "" : "; cause " + cause)));
            }
    }

    /**
     * Factory iterator used to iterate over the elements.
     * @param rdr the thing to read from
     * @return an iterator
     */
    public static Iterator factoryIterator(Reader rdr) {
        try {
            return new Factory(rdr);
        } catch (JDOMException de) {
            Throwable cause = de.getCause();
            if (cause != null) {
                cause.printStackTrace();
            } else {
                de.printStackTrace();
            }
            throw(new RuntimeException("XML exception = " + de.toString()
                                      + (cause == null ? "" : "; cause " + cause)));
        } catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException(e.toString()));
        }
    }

    /**
     * XML string representation of complete XML Document, including prologue.
     *
     * @return pretty-print of the result of {@link #getDocument()}.
     */
    public String toString() {
        return outputter.outputString(getDocument());
    }

    /**
     * Generate a complete XML document for this object.
     * The root element will be of type {@link #MSG_SEQUENCE_ELEMENT}
     * @return Document
     * @see org.jdom.Document
     */
    protected Document getDocument() {
        Element root = new Element(MSG_SEQUENCE_ELEMENT);
        root.setAttribute(VERSION_NUMBER_ATTR, VERSION_NUMBER);
        root.addContent(getElement());
        return new Document(root);
    }

    /**
     * String representation is pretty-print of XML Element.
     * @return XML pretty-print from the outputter
     */
    public String getElementString() {
        return outputter.outputString(getElement());
    }

    /**
     * Generate an element for this object.
     * @return DOM {@link org.jdom.Element Element}
     */
    protected Element getElement() {
        Element root = new Element(topElementType);
        if (attemptId != null) {
            root.setAttribute("attempt_id", attemptId);
        }

        if (CURRICULUM_MSG_ELEMENT.equals(topElementType)) {
            if (name != null) {
                root.setAttribute("name", name);
            }

            return getCurriculumElement(root);
        }

        if (MSG_ELEMENT.equals(topElementType)) {
            return getMsgElement(root);
        }

        if (problemName != null) {
            root.addContent(stringToElement(PROBLEMNAME_ELEMENT, problemName));
        }

        return root;
    }

    /**
     * Get the top-level element type, one of {@link #CURRICULUM_MSG_ELEMENT},
     * etc.
     * @return the top element type
     */
    public String getTopElementType() {
        return topElementType;
    }

    /**
     * Fill in the content of a curriculum message.
     *
     * @param  root element of type {@link #CURRICULUM_MSG_ELEMENT}
     * @return root argument
     */
    protected Element getCurriculumElement(Element root) {

        if (schoolName != null) {
            root.addContent(stringToElement(SCHOOLNAME_ELEMENT, schoolName));
        }
        if (courseName != null) {
            root.addContent(stringToElement(COURSENAME_ELEMENT, courseName));
        }
        if (unitName != null) {
            root.addContent(stringToElement(UNITNAME_ELEMENT, unitName));
        }
        if (sectionName != null) {
            root.addContent(stringToElement(SECTIONNAME_ELEMENT, sectionName));
        }
        if (problemName != null) {
            root.addContent(stringToElement(PROBLEMNAME_ELEMENT, problemName));
        }

        return root;
    }

    /**
     * Fill in the content of a custom field element.
     *
     * @param  root element of type {@link #CUSTOM_FIELD_ELEMENT}
     * @param customName name of the custom field
     * @param customValue value of the custom field
     * @return root argument
     */
    protected Element getCustomFieldElement(Element root, String customName, String customValue) {

        Element cfElement =  new Element(CUSTOM_FIELD_ELEMENT);

        cfElement.addContent(stringToElement(CUSTOM_FIELD_NAME_ELEMENT, customName));
        cfElement.addContent(stringToElement(CUSTOM_FIELD_VALUE_ELEMENT, customValue));

        root.addContent(cfElement);

        return root;
    }

    /**
     * Fill in the content of a message of type {@link #MSG_ELEMENT}.
     *
     * @param  root element of type MSG_ELEMENT
     * @return root argument
     */
    protected Element getMsgElement(Element root) {
        if (msgProperties == null) {
            return root;
        }
        for (Iterator it = msgProperties.iterator(); it.hasNext();) {
            root.addContent(((MsgProperty) it.next()).getElement());
        }
        return root;
    }

    /**
     * Extract data from an XML Element message.
     *
     * @param  elt Element with message data
     * @exception JDOMException on any error
     */
    protected void parseElement(Element elt) throws JDOMException {

        topElementType = elt.getName();

        attemptId = elt.getAttributeValue("attempt_id");

        Element metaElt = elt.getChild("meta");
        if (metaElt != null) {
            setUserGuid(metaElt.getChildTextTrim("user_id"));
            setSessionId(metaElt.getChildTextTrim(Logger.SESSION_ID_PROPERTY));
            String time = metaElt.getChildTextTrim("time");
            if (time != null && time.length() > 0) {
                ParsePosition pos = new ParsePosition(0);
                String timeZone = metaElt.getChildTextTrim("time_zone");
                if (timeZone != null && timeZone.length() > 0) {
                    timeZone = DEFAULT_TIME_ZONE;
                }
                setTimezone(timeZone);
                String twz = time + " " + timeZone;
                Date timeStamp = dateFmtMS.parse(twz, pos);
                if (null != timeStamp) {
                    setTimeStamp(timeStamp);
                } else {
                    timeStamp = dateFmtSS.parse(twz, pos);
                    if (null != timeStamp) {
                        setTimeStamp(timeStamp);
                    } else {
                        timeStamp = dateFmt.parse(twz, pos);
                        if (null != timeStamp) {
                            setTimeStamp(timeStamp);
                        } else {
                            setTimeStamp(new Date());
                        }
                    }
                }
            }
        }

        //dtasse added the next line.  There's always an action type/argument/result, or
        //at least some combination of them.
        parseActionMsgElement(elt);
		// zz may need more 
        if (CURRICULUM_MSG_ELEMENT.equals(topElementType)) {
            parseCurriculumContent(elt);
        } else if (MSG_ELEMENT.equals(topElementType)) {
            parseMsgContent(elt);
        } else {
            throw new JDOMException("Undefined message element tag: " + topElementType);
        }
    }

    /**
     * Extract data from an {@link #CURRICULUM_MSG_ELEMENT} Element.
     *
     * @param  msgElt Element with message data
     * @exception JDOMException on any error
     */
    protected void parseCurriculumContent(Element msgElt)
            throws JDOMException {

        schoolName  = msgElt.getChildText(SCHOOLNAME_ELEMENT);
        courseName  = msgElt.getChildText(COURSENAME_ELEMENT);
        unitName    = msgElt.getChildText(UNITNAME_ELEMENT);
        sectionName = msgElt.getChildText(SECTIONNAME_ELEMENT);
        problemName = msgElt.getChildText(PROBLEMNAME_ELEMENT);
		
		return;
    }

    /**
     * Extract data from an {@link #MSG_ELEMENT} Element.
     *
     * @param  msgElt Element with message data
     * @exception JDOMException on any error
     */
    protected void parseMsgContent(Element msgElt) throws JDOMException {
        Iterator it = msgElt.getChildren(PROPERTY_ELEMENT).iterator();
        if (!it.hasNext()) {
            return;
        }
        msgProperties = new LinkedList();
        do {
            MsgProperty p = new MsgProperty((Element) it.next());
            msgProperties.add(p);
        } while(it.hasNext());
    }

	protected void parseActionMsgElement(Element elt) {
		// only need these info
		actionType = elt.getChildText(ACTION_TYPE_ELEMENT);
		argument = elt.getChildText(ARGUMENT_ELEMENT);
		result = elt.getChildText(RESULT_ELEMENT);
		resultDetails = elt.getChildText(RESULT_DETAILS_ELEMENT);
	}
   

    /**
     * Add a custom field.
     * @param  customName name of the custom field
     * @param  customValue value of the custom field
     */
    public void addCustomField(String customName, String customValue) {
        if (customFields == null) {
            customFields = new LinkedHashMap();
        }
        customFields.put(customName, customValue);
    }


    /**
     * Add a MsgProperty with a scalar String value.
     *
     * @param  text property name
     * @param  stringValue scalar value
     */
    public void addMsgProperty(String text, String stringValue) {
        if (msgProperties == null) {
            msgProperties = new LinkedList();
        }
        MsgProperty e = new MsgProperty(text, stringValue);
        msgProperties.add(e);
    }

    /**
     * Add a MsgProperty with a List value.
     *
     * @param  text property text
     * @param  list list value
     */
    public void addMsgProperty(String text, List list) {
        if (msgProperties == null) {
            msgProperties = new LinkedList();
        }
        MsgProperty e = new MsgProperty(text, list);
        msgProperties.add(e);
    }

    /**
     * Returns the problem name as a string, an empty string if it is null.
     * @return problem name
     */
    public String getProblemName() {
        return (problemName != null ? problemName : "");
    }

    /**
     * Sets the problem name as a string, an empty string if it is null.
     * @param  name the problem name
     */
    public void setProblemName(String name) {
        problemName = (name != null ? name : "");
    }


    /**
     * Return an iterator over all custom fields.
     * EmptyIterator if no list.
     * @return Iterator on the custom fields
     */
    public Iterator customFieldsIterator() {
        if (customFields == null) {
            return emptyIterator;
        }
        return customFields.keySet().iterator();
    }


    /**
     * Returns the school name as a string, an empty string if it is null.
     * @return the school name
     */
    public String getSchoolName() {
        return (schoolName != null ? schoolName : "");
    }

    /**
     * Sets the school name as a String, an empty string if it is null.
     * @param name - the new school name
     */
    public void setSchoolName(String name) {
        schoolName = (name != null ? name : "");
    }

    /**
     * Returns the course name as a string, an empty string if it is null.
     * @return the course name
     */
    public String getCourseName() {
        return (courseName != null ? courseName : "");
    }

    /**
     * Set the course name as a string.
     * @param name - the new course name
     */
    public void setCourseName(String name) {
        courseName = (name != null ? name : "");
    }

    /**
     * Returns the unit name as a string, an empty string if it is null.
     * @return the unit name
     */
    public String getUnitName() {
        return (unitName != null ? unitName : "");
    }

    /**
     * Set the unit name as a string, an empty string if it is null.
     * @param name - the new unit name
     */
    public void setUnitName(String name) {
        unitName = (name != null ? name : "");
    }

    /**
     * Returns the section name as a string, an empty string if it is null.
     * @return the section name
     */
    public String getSectionName() {
        return (sectionName != null ? sectionName : "");
    }

    /**
     * Sets the section name as a string, an emptry string if it is null.
     * @param name - the new section name
     */
    public void setSectionName(String name) {
        sectionName = (name != null ? name : "");
    }

    /**
     * Return an iterator over all {@link TutorActionLog.MsgProperty} elements.
     * EmptyIterator if no list.
     * @return Iterator the message properties
     */
    public Iterator msgPropertiesIterator() {
        if (msgProperties == null) {
            return emptyIterator;
        }
        return msgProperties.iterator();
    }

    /**
     * Override the base class method to ensure that loggers and other
     * handlers will get our value for the info field.
     *
     * @return result of {@link #toString()}
     */
    public String getInfo() {
        return toString();
    }

    /**
     * Set the attempt id.
     * @param attemptId the attempt id
     */
    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    /**
     * Return the attemptId.
     * @return the attempt id
     */
    public String getAttemptId() {
        return attemptId;
    }

    /**
     * Set the name attribute.
     * @param name the subtype of the message
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the name.
     * @return the name of the mesage
     */
    public String getName() {
        return name;
    }

    /**
     * Override the base class method to ensure that loggers and other
     * handlers will get our value for the action_id field.
     * @return value of the top element type
     */
    public String getActionId() {
        return topElementType;
    }

    /**
     * Override the base class method to ensure that loggers and other
     * handlers will get the correct value for the info_type field.
     * @return constant value for the info type
     */
    public String getInfoType() {
        return INFO_TYPE;
    }

    /**
     * Create an XML element from a String.
     *
     * @param  tag XML element name
     * @param  text text of the element; will be escaped to avoid
     *         conflict w/ XML elements
     * @return element with text as content; null if an error occurs
     */
    public static Element stringToElement(String tag, String text) {
        Element result = new Element(tag);
//      String escText = outputter.escapeElementEntities(text);
//		trace.err("!3 "+escText);  
        result.setText(text);  // sewall 8/31/07: outputter.outputString() does escapes
//		trace.err("!4 "+outputter.outputString(result));
        return result;
    }

    /**
     * Check the name of the element.
     *
     * @param elt the xml element
     * @param elementName name of the element
     * @throws JDOMException thrown if xml is invalid
     */
    public static void checkElementName(Element elt, String elementName) throws JDOMException {
        if (null == elt) {
            throw new JDOMException("the element is null, expected " + elementName);
        } else if (!(elementName.equals(elt.getName()))) {
            throw new JDOMException("the element is invalid, expected " + elementName
                    + " instead of " + elt.getName());
        }
    }

    /**
     * Check the id of the element.
     *
     * @param elt the xml element
     * @param elementName name of the element
     * @return the id attribute
     * @throws JDOMException thrown if element passed in is null
     *                       or if the id is missing or invalid
     */
    public static String getElementId(Element elt, String elementName)
            throws JDOMException {
        if (null == elt) {
            throw new JDOMException("the element is null, expected " + elementName);
        }

        String id = elt.getAttributeValue("id");

        if (null == id) {
            throw new JDOMException("missing id attribute " + id + " for element " + elementName);
        } else if (id.length() < 1) {
            throw new JDOMException("empty id attibute: " + id + " for element " + elementName);
        }

        return id;
    }

    /**
     * Generate a unique identifier. Returns toString() of a new instance of
     * {@link java.rmi.server.UID}.
     * Prepend "DS" as a DTD requires IDs to start with an alphabetic character.
     * TBD: should append machine identifier * for global uniqueness.
     *
     * @return  UID.toString() result
     */
    public static String generateGUID() {
        UID uid = new UID();
        return "DS" + uid.toString();
    }
	
	

    /**
     * A name-value pair. The value may be either a single String or
     * List of Strings.
     */
    public static final class MsgProperty {

        /** Element name for this class. */
        public static final String ELEMENT = "property";

        /** Element name for single entry of list-valued property. */
        public static final String ENTRY_ELEMENT = "entry";

        /** Name (attribute) of msgProperty element. */
        private String name = null;

        /** Property's value as scalar String. */
        private String stringValue = null;

        /** Property's value as List. */
        private List entries = null;

        /**
         * Construct from given data.
         * @param  name of the message property
         * @param  value of the message property
         */
        private MsgProperty(String name, String value) {
            this.name = (name == null ? "" : name);
            this.stringValue = value;
        }

        /**
         * Construct from given data.
         * @param  name of the message property
         * @param  entries list of entry elements
         *
         */
        private MsgProperty(String name, List entries) {
            this.name = (name == null ? "" : name);
            if (entries == null) {
                this.stringValue = "";
            } else {
                this.entries = new LinkedList(entries);
            }
        }

        /**
         * Construct from XML Element.
         *
         * @param  elt event_descriptor element
         * @exception JDOMException if elt is not a msgProperty
         */
        private MsgProperty(Element elt) throws JDOMException {
            AuthorActionLog.checkElementName(elt, ELEMENT);
            name = elt.getAttributeValue("name");

            Iterator it = elt.getChildren(ENTRY_ELEMENT).iterator();
            if (!it.hasNext()) {
                stringValue = elt.getText();  // empty string if no content
            } else {
                entries = new LinkedList();
                do {
                    entries.add(((Element) it.next()).getText());
                } while (it.hasNext());
            }
        }

        /** Generate an element for this object.
            @return DOM {@link org.jdom.Element Element} */
        public Element getElement() {
            Element result = new Element(ELEMENT);
            result.setAttribute("name", name);
            if (isList()) {
                for (Iterator it = entries.iterator(); it.hasNext();) {
                    Element e = new Element(ENTRY_ELEMENT);
                    result.addContent(e.setText((String) it.next()));
                }
            } else {
                result.setText(stringValue);
            }
            return result;
        }

        /** Get the property name.
          * @return property name
          */
        public String getName() { return name; }

        /** Tell whether this property is list-valued.
          * @return true if there are entry elements
          */
        public boolean isList() { return (entries != null); }

        /** Get a scalar value. Result valid if there are no entry elements
          * @return string value
          */
        public String getStringValue() { return stringValue; }

        /** Get a list value. Result valid if there are entry elements
          * @return list of entry elements
          */
        public List getList() { return entries; }
    }

    /**
     * An iterator to return the TutorActionLog elements in a stream.
     * The next() method will call the {@link TutorActionLog} constructor on
     * the current child of the stream's root element.
     */
    private static final class Factory implements Iterator {

        /** Document built from Reader. */
        private Document doc = null;

        /** Iterator on doc root children. Delegate for Iterator methods.*/
        private Iterator iterator;

        /**
         * Constructor builds Document from Reader, saves Iterator.
         * Accepts root element of name
         * {@link AuthorActionLog#MSG_SEQUENCE_ELEMENT} for sequence.
         * Will generate single-member iterator if root element
         * is {@link #AuthorAction.AUTHOR_MSG_ELEMENT}, 
         * {@link #ProgramAction.PROGRAM_MSG_ELEMENT},
         * {@link #CURRICULUM_MSG_ELEMENT} or {@link #MSG_ELEMENT}.
         *
         * @param  rdr Reader for {@link org.jdom.input.SAXBuilder}
         * @exception JDOMException, IOException
         */
        private Factory(Reader rdr) throws JDOMException, IOException {

            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(rdr);
            Element root = doc.getRootElement();
            String name = root.getName();
            if (MSG_SEQUENCE_ELEMENT.equals(name)) {
                iterator = root.getChildren().iterator();
            } else if (//AuthorAction.AUTHOR_MSG_ELEMENT.equals(name)
                     //||
					 //ProgramAction.PROGRAM_MSG_ELEMENT.equals(name)
                     //||
                     CURRICULUM_MSG_ELEMENT.equals(name)
                     ||
                     MSG_ELEMENT.equals(name)) {
                List singleElt = new LinkedList();
                singleElt.add(root);
                iterator = singleElt.iterator();
            } else {
                throw new JDOMException("Bad root element: " + name);
            }
        }

        /**
         * Returns true if the iteration has more elements.
         * @return true if the iterator has more elements.
         */
        public boolean hasNext() {
            return iterator.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         * @return the next element in the iteration.
         */
        public Object next() {
            Element msgElt = (Element) iterator.next();  // throws if empty
            return new AuthorActionLog(msgElt);
        }

        /**
         * This method always throws {@link java.lang.UnsupportedOperationException}.
         */
        public void remove() {
            throw new UnsupportedOperationException("cannot remove");
        }
    } // end inner class Factory

} // end class TutorActionLog


