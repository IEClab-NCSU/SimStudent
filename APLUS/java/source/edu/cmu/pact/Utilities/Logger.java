/*
 * $Id: Logger.java 21309 2014-10-02 14:02:01Z nikolaos $
 * $Author: nikolaos $
 */
package edu.cmu.pact.Utilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.cmu.oli.log.client.ActionLog;
import edu.cmu.oli.log.client.AsyncStreamLogger;
import edu.cmu.oli.log.client.DiskLogger;
import edu.cmu.oli.log.client.SessionLog;
import edu.cmu.oli.log.client.StreamLogger;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.Log.DataShopMessageObject;
import edu.cmu.pact.Log.LogConsole;
import edu.cmu.pact.Log.TutorActionLogV4;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.LogContext;
import edu.cmu.pslc.logging.Message;
import edu.cmu.pslc.logging.OliDatabaseLogger;
import edu.cmu.pslc.logging.OliDiskLogger;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.TutorMessage;
import edu.cmu.pslc.logging.element.ConditionElement;
import edu.cmu.pslc.logging.element.DatasetElement;
import edu.cmu.pslc.logging.element.LevelElement;
import edu.cmu.pslc.logging.element.MetaElement;
import edu.cmu.pslc.logging.element.ProblemElement;

/**
 * This class is the top-level interface to the logging services.
 */
public class Logger implements AsyncStreamLogger.Listener, LogContext, PropertyChangeListener {

    public void resetLogger() {
        if (trace.getDebugCode("log")) trace.out("log", "RESET LOGGER logFileDir "+logFileDir);
        startDiskLogging(this.logFileDir);
    }
    /**
     * Change the directory for OLI logging. Calls
     * {@link #startOLILogging(String) startOLILogging(loggingURL)} if
     * {@link #streamLogger} is not null.
     * 
     * @param loggingURL
     *            new URL for {@link StreamLogger} messages
     */
    public void changeOLILoggingURL(String loggingURL) {
    	if (loggingURL == null || loggingURL.length() < 1)
    		streamLogger = null;
    	else
    		startOLILogging(loggingURL);
    }

    /**
     * Change the directory for disk logging. Calls
     * {@link #startDiskLogging(String) startDiskLogging(logFileDir)} if
     * {@link #diskLogger} is not null.
     * 
     * @param logFileDir
     *            new directory for {@link DiskLogger} files
     */
    public void changeDiskLoggingDir(String logFileDir) {
    	if (trace.getDebugCode("log")) trace.out("log", "disk logger = " + diskLogger);
    	if (logFileDir == null || logFileDir.length() < 1)
            diskLogger = null;
    	else
    		startDiskLogging(logFileDir);
    }

    /**
     * Handle a failure result from a logger. Displays the error to the user,
     * prints a stack trace and disables logging of the given sort.
     * 
     * @param logEx
     *            Exception thrown by logger; null if none
     * @param logDest
     *            destination URL or directory; used in error msg
     * @param preferenceName
     *            name of the preference that controls this logging
     */
    private void handleLogError(Throwable logEx, String logDest,
            String preferenceName) {
        if (logEx != null)
            logEx.printStackTrace();
        String displayMsg = "Error trying to log to " + logDest + ".";
        if (logEx != null)
            displayMsg = displayMsg + " Details:\n" + logEx.toString();
      //  Utils.showExceptionOccuredDialog(null, displayMsg, "Warning");
    }

    /**
     * Log a message in OLI XML format. Logs to both disk and server, as
     * indicated by {@link #useDiskLogging()}, {@link #useOLILogging()}.
     * 
     * @param o
     *            message to log
     * @param tutorToTool
     *            true if message was from tutor to tool; false if from tool to
     *            tutor
     * @return result from {@link #oliLog(ActionLog)}
     */
    public boolean oliLog(MessageObject o, boolean tutorToTool) {
    	//LogReader: graph+interface/log messages go through
    	//editAndLogStartProblemMessage, handleMessageUTP(MessageObject mo), handleCommMessage[toolToTutor]
    	//before coming here.
    	if (trace.getDebugCode("log"))
    		trace.out("log", "oliLog(MessageObject,tutorToTool bool): enableLog="+enableLog+", tutorToTool "+tutorToTool+
    				", mo"+ o.toString());
    	
    	//Send all associated rules to LogConsole for comparison
    	if (trace.getDebugCode("log")) trace.out("log", "oliLog isAssociatedRules "+isAssociatedRules(o));
    	if(logConsole != null && isAssociatedRules(o)){	//TODO Not sure if this correctly resets on LogConsole window close.
    		logConsole.sendMsgToLogConsole(o);
    	}
    	
    	return oliLog(o, tutorToTool, (String) null);
    }

    /**
     * Check if a MessageObject passed is Associated Rules. Used to pass only
     * necessary information to LogConsole.
     * @param message
     * @return
     */
    private boolean isAssociatedRules(MessageObject message){
    	if(message.getMessageType().equals("AssociatedRules")){
    		if (trace.getDebugCode("log")) trace.out("log", "is associated rules");
    		return true;
    	}
		return false;
    }
    
    /**
     * Log a message in OLI XML format. Logs to both disk and server, as
     * indicated by {@link #useDiskLogging()}, {@link #useOLILogging()}.
     * N.B. if no custom fields are desired, pass a single null argument to
     * distinguish this signature from {@link #oliLog(MessageObject, boolean)}.
     * @param o message to log
     * @param tutorToTool true if message was from tutor to tool;
     *        false if from tool to tutor
     *        {@link TutorMessage#addCustomField(String, String)}            
     * @return result from {@link #oliLog(ActionLog)}
     */
    public boolean oliLog(MessageObject o, boolean tutorToTool, String... custom) {
    	if (trace.getDebugCode("log"))
    		trace.out("log", "oliLog(MessageObject, tutorToTool, Strings): enableLog="+enableLog+", tutorToTool "
    				+tutorToTool+", mo="+o.toString());
    	
    	if (o.isLoggingSuppressed())
    		return false;
    	
        if (!useOLILogging() && !useDiskLogging())
            return false;

        TutorActionLogV4 alog = messageObjectToTutorActionLog(o, tutorToTool);

        if (alog == null)
        	return false;
        else {
        	Message m = alog.getMsg();
        	if (custom != null) {
        		if (m instanceof ToolMessage) {
        			ToolMessage tm = (ToolMessage) m;	//tm is never used after adding custom fields?
        			for (int i = 1; i < custom.length; i += 2)
        				tm.addCustomField(custom[i-1], custom[i]);
        		} else if (m instanceof TutorMessage) {
        			TutorMessage tm = (TutorMessage) m;
        			for (int i = 1; i < custom.length; i += 2)
        				tm.addCustomField(custom[i-1], custom[i]);
        		}
        	}
        	return oliLog(alog);
        }
    }

    /**
     * Convert a {@link MessageObject} to the format for logging.
     * @param o message to log
     * @param tutorToTool true if a tutor message; false if a tool message
     * @return
     */
    TutorActionLogV4 messageObjectToTutorActionLog(MessageObject o, boolean tutorToTool) {
    	TutorActionLogV4 result = null;
    	try {
    		DataShopMessageObject mo = new DataShopMessageObject(o, tutorToTool, this);
    		result = mo.getLogMsg();
    		
//    		if (trace.getDebugCode("miss")) trace.out("miss", "Converted a MessageObject to TutorActionLogV4. Original MessageObject: " + o.toString()+"\nNew TutorActionLogV4: "+result.toString());
    	} catch (Exception e) {
    		trace.errStack("logging msg:\n  "+o, e);
    	}
		return result;
	}

    /**
     * Convert a DataShop log message to our internal message format {@link MessageObject}. 
     * @param msgXML XML message text
     * @param tutorToTool true if a tutor message; false if a tool message
     * @return
     */
    public static MessageObject actionLogToMessageObject(String msgXML, boolean tutorToTool) {
    	try {
    		Element msgElt = elementForString(msgXML);

    	} catch (Exception e) {
    		trace.errStack("converting msgXML:\n  "+msgXML, e);
    	}
		return null;
	}
    
    private static final List<String> DataShopMsgTypes = Arrays.asList(new String[] {
        		Message.TOOL_MSG_ELEMENT,
        		Message.TUTOR_MSG_ELEMENT,
        		Message.CONTEXT_MSG_ELEMENT,
        		Message.MSG_ELEMENT
        });
    
    private static Element getMessageElement(Element elt) {
    	if (elt == null)
    		return null;
    	if (DataShopMsgTypes.contains(elt.getName()))
    		return elt;
        Element child = tutorRelatedMessageSequence(elt);
        if (child==null)
        	return null;
        if (DataShopMsgTypes.contains(child.getName()))
        	return child;
        for (String eltName : DataShopMsgTypes) {
        	Element grandchild = child.getChild(eltName);
            if (grandchild!=null)
                return grandchild;
        }
        return null;
    }

    /**
     * Find the {@value Message#MSG_SEQUENCE_ELEMENT} child element in a DataShop message element.
     * @param elt root element from message
     * @return
     */
    private static Element tutorRelatedMessageSequence(Element elt) {
    	if (trace.getDebugCode("log"))
    		trace.out("log", "elt "+
    				(elt == null ? "null" : elt.getName()+", date "+elt.getAttributeValue("date_time")));
    	if (elt == null)
    		return null;
        if ("log_session_start".equals(elt.getName()))
            return null;
        if (Message.MSG_SEQUENCE_ELEMENT.equals(elt.getName()))
            return elt;
        if (DataShopMsgTypes.contains(elt.getName()))
            return elt;
        if (!TutorActionLogV4.LOG_ACTION_ELEMENT.equals(elt.getName()))
            return null;
        return elt.getChild(Message.MSG_SEQUENCE_ELEMENT);
    }

    private static Element elementForString(String eltStr) {
        try {
            return ((new SAXBuilder()).build(new java.io.StringReader(eltStr))).getRootElement();
        } catch (Exception e) {
            System.err.println("Error parsing " + eltStr + "\ne");
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Log a {@link edu.cmu.oli.log.client.ActionLog} entry. Creates sessionLog
     * entry if needed. Sets user and session identifiers from
     * {@link #controller}.{@link BR_Controller#getLogger()}.{@link Logger#getStudentName()},
     * {@link #controller}.{@link BR_Controller#getLogger()}.{@link Logger#getSessionId()},
     * {@link #controller}.{@link BR_Controller#getLogger()}.{@link Logger#getAuthToken()}.
     * 
     * @param alog
     *            the ActionLog entry to log; no-op if null
     * @return OR'd result from
     *         {@link edu.cmu.oli.log.client.DiskLogger.logActionLog},
     *         {@link edu.cmu.oli.log.client.StreamLogger.logActionLog}
     */
    public boolean oliLog(ActionLog alog) {
    	if (trace.getDebugCode("log"))
    		trace.out("log", "oliLog(ActionLog): enableLog="+enableLog+", action log = " + alog);
        if (enableLog == false) return false;
    	if (alog == null) return false;
        alog.setUserGuid("x");  // CTAT2598: keep userid out of log_action msgs
        alog.setSessionId(getServer().getLogger().getSessionId());
        alog.setAuthToken(getAuthToken());
        
    	if (trace.getDebugCode("log"))
    		trace.out("log", "oliLog(ActionLog) after setting more data:" + alog);
        
        return oliLogInternal(alog);
    }
    /**
     * Log a {@link edu.cmu.oli.log.client.ActionLog} entry. Creates sessionLog
     * entry if needed. Logs to both disk and server, as indicated by
     * {@link #useDiskLogging()}, {@link #useOLILogging()}.
     * 
     * @param alog
     *            the ActionLog entry to log; no-op if null; assumes sessionId,
     *            userId already set as desired
     * @return OR'd result from
     *         {@link edu.cmu.oli.log.client.DiskLogger.logActionLog},
     *         {@link edu.cmu.oli.log.client.StreamLogger.logActionLog}
     */
    protected synchronized boolean oliLogInternal(ActionLog alog) {

    	boolean doStrm = useOLILogging();
        boolean doDisk = useDiskLogging();
        Boolean strmResult = Boolean.FALSE;
        Boolean diskResult = Boolean.FALSE;
        if (alog == null)
            return false;

       
        // log a session record if needed
        logSessionLog((doStrm ? streamLogger : null),
                (doDisk ? diskLogger : null));
        try {
            if (doStrm && streamLogger != null) {
                strmResult = streamLogger.logActionLog(alog);              
//                trace.out("log", "stream log result " + strmResult
//                        + ", last error " + log.getLastError());
                if (!strmResult.booleanValue())
                    handleLogError(streamLogger.getLastError(), streamLogger.getURL().toString(),
                            BR_Controller.USE_OLI_LOGGING);
            }
        } catch (Exception e) {
        //	trace.err("bla");
        		handleLogError(e, streamLogger.getURL().toString(), BR_Controller.USE_OLI_LOGGING);
        }

        try {
            if (doDisk && diskLogger != null) {
                diskResult = diskLogger.logActionLog(alog);
               // trace.out("--------------------- The end of this log ");
//                trace.out("log", "disk log result " + diskResult
//                        + ", last error " + diskLogger.getLastError());
                if (!diskResult.booleanValue())
                    handleLogError(diskLogger.getLastError(), diskLogFile,
                            BR_Controller.USE_DISK_LOGGING);
            }
        } catch (Exception e) {
            handleLogError(e, diskLogFile, BR_Controller.USE_DISK_LOGGING);
        }
        return strmResult.booleanValue() || diskResult.booleanValue();
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }
	
	/** Set this from the command line with -DenableAuthorLogging=true to enable author logging. */
    public static final String ENABLE_AUTHOR_LOGGING = "enableAuthorLogging";
    /**
     * For use with junit testing.  It represents the last file on the disk
     * that was logged to.
     */
    public String lastLogFile;
    protected String userID = "";



    protected String diskLogFile = null;

    // In student mode, do not log author actions to save time
    protected boolean enableAuthorLog = false;
    
    // To turn off all logs during state restoring
    protected boolean enableLog = true;

    protected String logFileDir;
    
    protected StreamLogger streamLogger;

    protected DiskLogger diskLogger;
	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	/** System property for authentication token. */
	public static final String LOG_SERVICE_URL_PROPERTY = "log_service_url";

	/** System property for disk logging directory. */
	public static final String DISK_LOG_DIR_PROPERTY = "log_to_disk_directory";

	/** System property for student name. */
	public static final String STUDENT_NAME_PROPERTY = "user_guid";

	/** System property for session id. */
	public static final String SESSION_ID_PROPERTY = "session_id";

	/** System property for authentication token. */
	public static final String AUTH_TOKEN_PROPERTY = "auth_token";

	/** System property for source id. */
	public static final String SOURCE_ID_PROPERTY = "source_id";

	/** System property for problem name. */
	public static final String PROBLEM_NAME_PROPERTY = "problem_name";

	/** Label for description field for problem data display in DataShop. */
	public static final String PROBLEM_TUTOR_FLAG_PROPERTY = "problem_tutorflag"; 

	/** Label for description field for problem data display in DataShop. */
	public static final String PROBLEM_OTHER_FLAG_PROPERTY = "problem_otherproblemflag"; 

	/** Label for description field for problem data display in DataShop. */
	public static final String PROBLEM_CONTEXT_PROPERTY = "problem_context"; 
	
	/** Unique id identifying the problem context for logging"*/
	public static final String CONTEXT_MSG_ID_PROPERTY = "context_message_id";
	
	/** System property for question file. */
	public static final String QUESTION_FILE_PROPERTY = "question_file";
	
	/** System property for student interface class name. */
	public static final String STUDENT_INTERFACE_PROPERTY = "student_interface";

	/** System property for school name. */
	public static final String SCHOOL_NAME_PROPERTY = "school_name";

	/** System property for course name. */
	public static final String COURSE_NAME_PROPERTY = "course_name";

	/** System property for unit name. */
	public static final String UNIT_NAME_PROPERTY = "unit_name";

	/** System property for section name. */
	public static final String SECTION_NAME_PROPERTY = "section_name";

	/** The maximum number of instructors to be sent in our context message */
	private static final int MAX_INSTRUCTORS = 11;

	/** The maximum number of conditions to be sent in our context message */
	private static final int MAX_CONDITIONS = 11;

	/** The maximum depth of level recursion allowed to be sent in our context message */
	private static final int MAX_LEVELS = 12;

	/** The maximum number of custom properties permitted in our context message */
	private static final int MAX_CUSTOM_PROPERTIES = 11;

	/** Whether we've logged a session already. */
	private boolean loggedSessionAlready = false;
	
	/** Collection of {@link OLIMessageConverters.Conv} instances that depend on this Logger. */
	private OLIMessageConverters oLIMessageConverters = null;

	/** For formatting dates for filenames.  Format: "yyyyMMddHHmmssSSS". */
	private static DateFormat dateFmt =
		new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	/** Student, session, time. */
	private MetaElement metaElement;

	/** Cached value for authentication token. */
	private String authToken = null;

	/** Fixed info_type value for all messages: refers to DTD. */
	private static final String INFO_TYPE = "tutor_message.dtd";

	/** Default value for source field in OLI log entry envelope. */
	public static final String DEFAULT_SOURCE_ID = "PACT_CTAT";
	
	/** Cached value for source id. */
	private String sourceId = DEFAULT_SOURCE_ID;

	/** Cached value for problem name. */
	private String problemName = "UndefinedProblem";

	/** Cached value for question file. */
	private String questionFile = null;

	/** Cached value for school name. */
	private String schoolName = null;

	
	/** Cached value for course name. */
	private String courseName;
	
	/** Cached value for dataset name. */
	private String datasetName = "UndefinedCourse";

	/** Top-level object. */
	private TSLauncherServer server;

    /** The current context message. */
	private volatile ContextMessage contextMessage;

	/** The MessageType (e.g. "StartProblem") whence the context information came. */
	private String contextMessageName;
	
	/** Context Message id- to be sent back to the client*/
	private String contextMessageId;

	/** A list of zero or more experimental conditions that this problem occurs in. */
	private String[] experimentalConditions;

	/** Attribute on problem element in context_message: values "test", "pre-test", etc. */
	private String problemTutorFlag;

	/** Attribute "other" of problem element in context_message. */
	private String problemOtherFlag;

	/** Value for description field for problem data display in DataShop. */
	private String problemContext;
	
	/** A list of instructors for this problem */
	private ArrayList instructors;
	
	/** A list of all condition names */ 
	private ArrayList conditionNames;
	
	/** A list of all condition types */
	private ArrayList conditionTypes;
	
	/** A list of all condition descriptions */
	private ArrayList conditionDescriptions;
	
	/** The corresponding name for each level inside our DataSet */
	private ArrayList levelNames;
	
	/** The corresponding type for each level inside our DataSet */
	private ArrayList levelTypes;
	
	/** The corresponding name for each custom property */
	private ArrayList customNames;
	
	/** The corresponding value for each custom property */
	private ArrayList customValues;
	

	/** ToolMessage in current transaction. */
	private ToolMessage lastToolMessage;

	private String className;

	private String classPeriod;

	private String classDescription; 

	/** Reference to LogConsole to send it Associated Rules */
	private LogConsole logConsole = null;
	
    public Logger(String studentName, TSLauncherServer server) {
    	this.server = server;
        if (this.server != null && this.server.getProperties() != null)
        	this.server.getProperties().addPropertyChangeListener(this);
        
        trace.out ("log", "student name = " + studentName);
        if (studentName == null)
            studentName = getServerProperty(STUDENT_NAME_PROPERTY);
        
        trace.out ("log", "student name = " + studentName);
        if (studentName == null || studentName.length() < 1) {
            
        	try {
        		studentName = System.getProperty("user.name");
        	} catch (Exception e) {
        		trace.err("Unable to access system propertie user.name: "+e.getMessage());
        	}
            if (studentName == null || studentName.length() < 1)
                studentName = "unknown";
        }

        createMetaElement(studentName,
        		getServerProperty(SESSION_ID_PROPERTY));
        contextMessageName = "START_TUTOR";
        contextMessage = ContextMessage.create(contextMessageName, metaElement);
		if((contextMessageId!=null) && (contextMessageId.length() > 0))
			contextMessage.setContextMessageId(contextMessageId);
        trace.out ("log", "Create logger: student name = " + studentName);
//        trace.printStack("log");

        instructors = new ArrayList();
        for (int i = 0; i < MAX_INSTRUCTORS; i++) instructors.add(null);
        customNames = new ArrayList();
        for (int i = 0; i < MAX_CUSTOM_PROPERTIES; i++) customNames.add(null);
        customValues = new ArrayList();
        for (int i = 0; i < MAX_CUSTOM_PROPERTIES; i++) customValues.add(null);
        conditionNames = new ArrayList();
        for (int i = 0; i < MAX_CONDITIONS; i++) conditionNames.add(null);
        conditionTypes = new ArrayList();
        for (int i = 0; i < MAX_CONDITIONS; i++) conditionTypes.add(null);
        conditionDescriptions = new ArrayList();
        for (int i = 0; i < MAX_CONDITIONS; i++) conditionDescriptions.add(null);
        levelNames = new ArrayList();
        for (int i = 0; i < MAX_LEVELS; i++) levelNames.add(null);
        levelTypes = new ArrayList();
        for (int i = 0; i < MAX_LEVELS; i++) levelTypes.add(null);
        
        authToken = getServerProperty(AUTH_TOKEN_PROPERTY);
        setSourceId(getServerProperty(SOURCE_ID_PROPERTY));
        setProblemName(getServerProperty(PROBLEM_NAME_PROPERTY));
        questionFile = getServerProperty(QUESTION_FILE_PROPERTY);
        setSchoolName(getServerProperty(SCHOOL_NAME_PROPERTY));
        setClassName(getServerProperty(CLASS_NAME_PROPERTY));
        setClassPeriod(getServerProperty(CLASS_PERIOD_PROPERTY));
        setClassDescription(getServerProperty(CLASS_DESCRIPTION_PROPERTY));
        setCourseName(getServerProperty(COURSE_NAME_PROPERTY));
        if (this.server!=null)
            setDatasetName((String)this.server.getPreferencesModel().getStringValue(DATASET_NAME_PROPERTY));
        setDatasetName(getServerProperty(DATASET_NAME_PROPERTY));
        setUnitName(getServerProperty(UNIT_NAME_PROPERTY));
        setSectionName(getServerProperty(SECTION_NAME_PROPERTY));
        
        
    }

    private String getServerProperty(String property) {
        return getServer()==null ? null : (String)getServer().getProperties().getProperty(property);
    }

    /**
     * Set the problem name attribute.
     * @param problemName
     */
    public void setProblemName(String problemName) {
    	if (trace.getDebugCode("log")) trace.out("log", this.toString()+".problemName was "+this.problemName+", now "+problemName);
    	this.problemName = problemName;
		reviseContextMessage();
	}


	/** Internal constructor. Sets {@link #sessionId}. */
	public Logger(TSLauncherServer server) {
        this (null, server);
	}
    /**
	 * Set the session identifier.
	 * @param new sessionID; uses {@link #generateGUID()} if arg is empty or null
	 */
	public void setSessionId(String sessionId) {
		if (sessionId == null )
			return;
		createMetaElement(getStudentName(), sessionId);
		loggedSessionAlready = false;     // force new session record
	}

	/**
	 * Generate a unique identifier. Returns toString() of a new instance of
	 * {@link java.util.UUID}, prefixed by "L" to ensure 1st char is
	 * alphabetic (see XML spec).
	 * !!!STUB: should append machine identifier for global uniqueness.
	 *
	 * @return  UID.toString() result
	 */
	public static String generateGUID() {
		UUID uid = UUID.randomUUID();
		return "L" + uid.toString();
	}

	//////////////////////////////////////////////////////
	/**
	 * Called when a PropertyChangeEvent is sent from the PreferencesModel.
	 *
	 * @param  evt PropertyChangeEvent detailing change
	 */
	//////////////////////////////////////////////////////
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		
		if (name.equalsIgnoreCase(AUTH_TOKEN_PROPERTY)) { 
	        authToken = (String) newValue;
		} else if (name.equalsIgnoreCase(SOURCE_ID_PROPERTY)) { 
	        setSourceId((String) newValue);
		} else if (name.equalsIgnoreCase(PROBLEM_NAME_PROPERTY)) { 
	        setProblemName((String) newValue);
		} else if (name.equalsIgnoreCase(PROBLEM_CONTEXT_PROPERTY)) { 
	        setProblemContext((String) newValue);
		} else if (name.equalsIgnoreCase(PROBLEM_TUTOR_FLAG_PROPERTY)) { 
	        setProblemTutorFlag((String) newValue);
		} else if (name.equalsIgnoreCase(QUESTION_FILE_PROPERTY)) { 
	        questionFile = (String) newValue;
		} else if (name.equalsIgnoreCase(SCHOOL_NAME_PROPERTY)) { 
	        setSchoolName((String) newValue);
		} else if (name.equalsIgnoreCase(CLASS_NAME_PROPERTY)) {
	        setClassName((String) newValue);
		} else if (name.equalsIgnoreCase(INSTRUCTOR_NAME_PROPERTY)) {
	        addInstructorName((String) newValue, 0);
		} else if (name.equalsIgnoreCase(COURSE_NAME_PROPERTY)) { 
	        setCourseName((String) newValue);
		} else if (name.equalsIgnoreCase(UNIT_NAME_PROPERTY)) { 
	        setUnitName((String) newValue);
		} else if (name.equalsIgnoreCase(SECTION_NAME_PROPERTY)) { 
	        setSectionName((String) newValue);
		} else if (name.equalsIgnoreCase(this.SESSION_ID_PROPERTY)) { 
	        setSessionId((String) newValue);
		} else if (name.equalsIgnoreCase(DATASET_NAME_PROPERTY)) { 
	        setDatasetName((String) newValue);
		} else if (name.toLowerCase().startsWith(DATASET_LEVEL_NAME)) {
			try {
				int index = Math.min(0, Integer.parseInt(name.substring(DATASET_LEVEL_NAME.length()))-1);
		        addDatasetLevelName((String) newValue, index); 
			} catch (Exception e) {
				trace.out("log", "Exception converting suffix to integer from name \""+name+"\": "+e);
		        addDatasetLevelName((String) newValue, 0); 
			}
		} else if (name.toLowerCase().startsWith(DATASET_LEVEL_TYPE)) {
			try {
				int index = Math.min(0, Integer.parseInt(name.substring(DATASET_LEVEL_TYPE.length()))-1);
		        addDatasetLevelType((String) newValue, index); 
			} catch (Exception e) {
				trace.out("log", "Exception converting suffix to integer from name \""+name+"\": "+e);
		        addDatasetLevelType((String) newValue, 0); 
			}
		} else if (name.toLowerCase().startsWith(STUDY_CONDITION_NAME)) {
			try {
				int index = Math.min(0, Integer.parseInt(name.substring(STUDY_CONDITION_NAME.length()))-1);
		        addStudyConditionName((String) newValue, index); 
			} catch (Exception e) {
				trace.out("log", "Exception converting suffix to integer from name \""+name+"\": "+e);
		        addStudyConditionName((String) newValue, 0); 
			}
		} else if (name.toLowerCase().startsWith(STUDY_CONDITION_TYPE)) {
			try {
				int index = Math.min(0, Integer.parseInt(name.substring(STUDY_CONDITION_TYPE.length()))-1);
		        addStudyConditionType((String) newValue, index); 
			} catch (Exception e) {
				trace.out("log", "Exception converting suffix to integer from name \""+name+"\": "+e);
		        addStudyConditionType((String) newValue, 0); 
			}
		} else
			return;
		if (trace.getDebugCode("log")) trace.out("log", "Changed " + name + " from " + evt.getOldValue() +
				  " to " + newValue);
	}

	/**
	 * Notify the user class of an error in logging.
	 *
	 * @param  errorDescription should describe the error that occurred
	 *             in terms that might be shown to a user
	 */
	public void notifyError(String errorDescription) {
		System.err.println("Logger: Error from AsyncStreamLogger: " + 
						   errorDescription);
	}

	/**
	 * @return the {@link #experimentalConditions}
	 */
	public String[] getExperimentalConditions() {
		return experimentalConditions;
	}

	/**
	 * Set the list of {@link #experimentalConditions} from a comma-delimited
	 * string.
	 * @param experimentalConditions comma-delimited string
	 */
	public void setExperimentalConditions(String experimentalConditions) {
		String[] split = experimentalConditions.split(",");
		for (int i = 0; i < split.length; ++i)
			split[i] = split[i].trim();
		setExperimentalConditions(split);
	}

	/**
	 * @param experimentalConditions new value for {@link #experimentalConditions}
	 */
	public void setExperimentalConditions(String[] experimentalConditions) {
		this.experimentalConditions = experimentalConditions;
		reviseContextMessage();
	}

	/**
	 * Log a {@link #edu.cmu.oli.log.client.SessionLog} record.
	 * No-op if loggedSessionAlready is set already.
	 *
	 * @param  log StreamLogger to use; if null, does not log to stream
	 * @param  dLog DiskLogger to use; if null, does not log to disk
	 * @return true if logged to at least one logger
	 */
	public boolean logSessionLog(StreamLogger log, DiskLogger dLog) {
		if (loggedSessionAlready)
			return false;

		synchronized(this) {   // recheck w/in synch block after unlocked check
			if (loggedSessionAlready)
				return false;
			boolean result = false;
			SessionLog slog = new SessionLog();
			
			String username = getServerProperty(STUDENT_NAME_PROPERTY);
			if (trace.getDebugCode("miss")) trace.out("miss", "to log session log; "+STUDENT_NAME_PROPERTY+" is "+username+
					", studentName is "+getStudentName() + "\n	log Streamlogger is " + log + "\n	dLog DiskLogger is " + dLog);
			
			if (username == null || username.length() < 1)
				username = getStudentName();
			slog.setUserGuid(username);
			slog.setSessionId(getSessionId());
			slog.setAuthToken(getAuthToken());
			if (log != null) {
				try {
					Boolean strmResult = log.logSessionLog(slog);
					result = result || strmResult.booleanValue();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (dLog != null) {
				try {
					Boolean diskResult = dLog.logSessionLog(slog);
					result = result || diskResult.booleanValue();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			loggedSessionAlready = result;  // this must be last in synch block
			return result;
		}
	}

	/**
	 * Return a filename built from the output of
	 * {@link #getStudentName()} and a timestamp formatted by
	 * {@link #dateFmt}.
	 *
	 * @return String of form <i>userID</i>_<i>yyyyMMddhhmmssSSS.log</i>
	 */
	public String getDefaultFilename() {
		return getStudentName() + "_" + dateFmt.format(new Date()) + ".log";
	}

	/** Get value for student name.
		@return {@link #studentName} */
	public String getStudentName() {
		return metaElement.getUserId();
	}

	/**
	 * Set value for student name. Clears {@link #loggedSessionAlready}
	 * and changes {@link #diskLogger}'s file name and recalculates session id
	 * if student name changed.
	 * @param  studentName new value for {@link #studentName}; no-op if null
	 */
	synchronized void setStudentName(String studentName) {
		if (trace.getDebugCode("log")) trace.out("log", "old studentName "+getStudentName()+
				", new studentName"+studentName);
		if (studentName == null )
			return;
		boolean nameChanged = (!studentName.equals(getStudentName()));
		if (nameChanged) {
			createMetaElement(studentName, getSessionId());
			loggedSessionAlready = false;
		}
	}
	
	/**
	 * Set value for student name. Clears {@link #loggedSessionAlready}
	 * and changes {@link #diskLogger}'s file name and recalculates session id
	 * if student name changed.
	 * @param  studentName new value for {@link #studentName}; no-op if null
	 */
	synchronized void setAnonymizedStudentName(String studentName) {
		if (trace.getDebugCode("log")) trace.out("log", "old studentName "+getStudentName()+
				", new studentName"+studentName);
		if (studentName == null )
			return;
		boolean nameChanged = (!studentName.equals(getStudentName()));
		if (nameChanged) {
			createMetaElement(studentName, getSessionId(), true);
			loggedSessionAlready = false;
		}
	}

	/**
	 * Set up the {@link #metaElement}. Also calls {@link #reviseupdateContextMessage}.
	 * @param studentName
	 * @param sessionId
	 */
	private void createMetaElement(String studentName, String sessionId) {
		createMetaElement(studentName, sessionId, false);
	}
	
	/**
	 * Set up the {@link #metaElement}. Also calls {@link #reviseupdateContextMessage}.
	 * @param studentName
	 * @param sessionId
	 * @param anonFlag
	 */
	private void createMetaElement(String studentName, String sessionId, boolean anonFlag) {
		String now = ctxMsgDateFmt.format(new Date());
		int tzIndex = now.lastIndexOf(' ');
		String tz = now.substring(tzIndex+1);
		if (sessionId == null || sessionId.length() < 1)
			sessionId = generateGUID();
		metaElement =
			new MetaElement(studentName, anonFlag, sessionId, now.substring(0, tzIndex), tz);
		reviseContextMessage();
	}
	
	/**
	 * Replace the current {@link #contextMessage} with current values.
	 */
	private void reviseContextMessage() {
		contextMessage = null;
	}
	
	public void setContextMessageId(String newCMId){	
		contextMessageId = newCMId;
		reviseContextMessage();
	}
	

	/** Get value for session id.
		@return {@link #sessionId} */
	public String getSessionId() {
		if(metaElement ==null)
			return null;
		return metaElement.getSessionId();
	}

	/** Get value for authentication token.
		@return {@link #authToken} */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken new value for {@link #authToken}
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * @return the proper value for the info_type field in the OLI log entry envelope
	 */
	public String getInfoType() {
		return INFO_TYPE;
	}

	/**
	 * @return {@link #sourceId} get the value for source id.
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * Uses {@link #DEFAULT_SOURCE_ID} ({@value #DEFAULT_SOURCE_ID}) if argument null or empty.
	 *  
	 * @param sourceId new value for {@link #sourceId}
	 */
	public void setSourceId(String sourceId) {
		if (sourceId == null || sourceId.length() < 1)
			this.sourceId =	DEFAULT_SOURCE_ID;
		else
			this.sourceId = sourceId;
	}

	/** Get value for problem name.
		@return {@link #problemName} */
	public String getProblemName() {
    	if (trace.getDebugCode("pm")) trace.out("pm", this.toString()+".problemName is "+problemName);
		return problemName;
	}
	
	/**
	 * Get a problem description suitable for DataShop display.
	 * @return {@link #getProblemName()}; default value is {@link #getProblemName()}
	 */
	public String getProblemContext() {
		if (problemContext != null && problemContext.length() > 0)
			return problemContext;
		else
			return getProblemName();
	}

	/** Get value for question file.
		@return {@link #questionFile} */
	public String getQuestionFile() {
		return questionFile;
	}

	/** Get value for school name.
		@return {@link #schoolName} */
	public String getSchoolName() {
		return schoolName;
	}

	/** Set value for school name.
		@param  schoolName new value for {@link #schoolName} */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
		reviseContextMessage();
	}

	/** Get value for course name.
		@return {@link #datasetName} */
	public String getDatasetName() {
		return datasetName;
	}

	/** set value for course name.
		@param courseName*/
	//	@deprecated use {@link #setDatasetName(String)} instead
	public void setCourseName(String  courseName) {
		 this.courseName = courseName;
		//setDatasetName(courseName);
	}
	
	/** Get value for course name.
		@return {@link #courseName}*/
	//	@deprecated use {@link #getDatasetName()} instead 
	public String getCourseName() {
		 return courseName;
		//return getDatasetName();
	}

	/** Set value for course name.
		@param  datasetName new value for {@link #datasetName} */
	public void setDatasetName(String datasetName) {
		if (datasetName != null && datasetName.length() > 0) {
			if (!datasetName.equalsIgnoreCase(this.datasetName)) {
				this.datasetName = datasetName;
				reviseContextMessage();
			}
		}
	}

	/** Get value for unit name.
		@return {@link #getDatasetLevelName(0)} 
		@deprecated use {@link #getDatasetLevelName} instead*/
	public String getUnitName() {
		if (trace.getDebugCode("sp")) trace.out("sp", "Logger "+this+".getUnitName "+getDatasetLevelName(0));
		return (String) getDatasetLevelName(0);
	}

	/** Set value for unit name.
		@param  unitName new value for {@link #levelNames[0]} and {@link #levelTypes[0]} 
		@deprecated use {@link #addDatasetLevelName} instead */
	public void setUnitName(String unitName) {
		if (trace.getDebugCode("sp")) trace.out("sp", "Logger "+this+".setUnitName old "+getDatasetLevelName(0)+
				", new "+ unitName);
/*		this.unitName = unitName;
		reviseContextMessage(); */
		this.addDatasetLevelName(unitName, 0);
		this.addDatasetLevelType("Unit", 0);
	}


	/** Get value for section name.
	 *  @deprecated use {@link #getDatasetLevelName) instead
		@return {@link #getDatasetLevelName(1)} */
	public String getSectionName() {
		return (String) getDatasetLevelName(1);
	}

	/** Set value for section name.
		@param  sectionName new value for {@link #levelName[1]} and {@link #levelType[1]}
		@deprecated use {@link #addDatasetLevelName} instead */
	public void setSectionName(String sectionName) {
		/* this.sectionName = sectionName;
		reviseContextMessage(); */
		this.addDatasetLevelName(sectionName, 1);
		this.addDatasetLevelType("Section", 1);
	}

	/** 
	 * Get value for attempt identifier.
	 * @return {@link #attemptId}
	 * @deprecated use {@link #getContextMessageId()}
	 */
	public String getAttemptId() {
		return getContextMessageId();
	}
	
	/**
	 * @return the identifier from the {@link #contextMessage}
	 */
	public String getContextMessageId() {
		return contextMessageId;
	}

	/**
	 * @return the {@link #oLIMessageConverters}
	 */
	OLIMessageConverters getOLIMessageConverters() {
		return oLIMessageConverters;
	}


	/**
	 * @param messageConverters new value for {@link #oLIMessageConverters}
	 */
	void setOLIMessageConverters(OLIMessageConverters messageConverters) {
		oLIMessageConverters = messageConverters;
	}

	/** Date format for context message yyyy-MM-dd HH:mm:ss.SSS z. */
	private static final DateFormat ctxMsgDateFmt =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");

	/** DataShop DTDv4 parameter name for dataset name. */
	public static final String DATASET_NAME_PROPERTY = "dataset_name";

	public static final String INSTRUCTOR_NAME_PROPERTY = "instructor_name";

	public static final String STUDY_CONDITION_NAME = "study_condition_name";

	public static final String STUDY_CONDITION_TYPE = "study_condition_type";

	public static final String STUDY_CONDITION_DESCRIPTION = "study_condition_description";

	public static final String DATASET_LEVEL_NAME = "dataset_level_name";

	public static final String DATASET_LEVEL_TYPE = "dataset_level_type";

	public static final String CLASS_NAME_PROPERTY = "class_name";

	public static final String CLASS_PERIOD_PROPERTY = "period_name";

	public static final String CLASS_DESCRIPTION_PROPERTY = "class_description";

	public static final String CUSTOM_FIELD_VALUE = "custom_field_value";

	public static final String CUSTOM_FIELD_NAME = "custom_field_name";
		
	/**
	 * @see #getContextMessage(String)
	 * @return result of {@link #getContextMessage(String) getContextMessage(null)}
	 */
	public ContextMessage getContextMessage() {
		return getContextMessage(null);
	}
	
	/**
	 * Return a message that encapsulates problem context data, including
	 * school, problem, condition, unit, etc.
	 * Synchronization: other methods may null {@link #contextMessage} if some
	 * of its data change. This method must always return a non-null value. 
	 * @param contextMessageName create instance if not null
	 * @return {@link #contextMessage}; create instance if null
	 */
	public synchronized ContextMessage getContextMessage(String contextMessageName) {
        ContextMessage result = contextMessage;
        if (result != null && (contextMessageName == null || 
        		!(contextMessageName.equalsIgnoreCase(this.contextMessageName)))) {
        	return result;
        }
        if (contextMessageName != null && contextMessageName.length() > 0)
            result = ContextMessage.create(contextMessageName, metaElement);
        else
        	result = ContextMessage.createStartProblem(metaElement);
        result.setSource(getSourceId());
        result.setSchool(getSchoolName());
        result.setClassDescription(getClassDescription());
        result.setClassName(getClassName());
        result.setPeriod(getClassPeriod());
        
        for (int i = 0; i < instructors.size(); i++)
        	if (getInstructorName(i) != null)
        		result.addInstructor(getInstructorName(i));
       
        ProblemElement problemElt = new ProblemElement(getProblemTutorFlag(), getProblemOtherFlag(),
        		getProblemName(), getProblemContext());
        LevelElement levelElt = null;
        for (int i = levelNames.size() - 2; i >= 0; i--)
        	if (getDatasetLevelName(i) != null)
        		if (levelElt == null)
        			levelElt = new LevelElement(getDatasetLevelType(i), getDatasetLevelName(i), problemElt);
        		else
        			levelElt = new LevelElement(getDatasetLevelType(i), getDatasetLevelName(i), levelElt);
        //If levelElt is still null, just create a default "Course" level
    	if (levelElt == null)
        	levelElt = new LevelElement("Course", getCourseName(), problemElt);
        if(getDatasetName().equalsIgnoreCase("UndefinedCourse"))
        	    result.setDataset(new DatasetElement(getCourseName(),levelElt));
        else 
        		result.setDataset(new DatasetElement(getDatasetName(), levelElt));

        if (experimentalConditions != null) {
        	for (int i = 0; i < experimentalConditions.length; ++i)
        		result.addCondition(new ConditionElement(experimentalConditions[i]));
        }
        else {
        	for (int i = 0; i < conditionNames.size(); i++) {
        		if (getStudyConditionName(i) != null)
        			result.addCondition(new ConditionElement(getStudyConditionName(i), 
        												     getStudyConditionType(i),
        												     getStudyConditionDescription(i)));
        	}
        }

       
        
        for (int i = 0; i < customNames.size(); i++) {
        	if (trace.getDebugCode("ls")) trace.out("ls", "Custom Names[" + i + "]=" + getCustomFieldName(i));
    		if (getCustomFieldName(i) != null)
    			result.addCustomField(getCustomFieldName(i), getCustomFieldValue(i));
    	}

        
		//contextMessageId.is
		if((contextMessageId!=null) && (contextMessageId.length() > 0))
			result.setContextMessageId(contextMessageId);
		contextMessage = result;  // save work for future
		return result;
	}


	/**
	 * @param problemContext new value for {@link #problemContext}
	 */
	public void setProblemContext(String problemContext) {
		this.problemContext = problemContext;
		if (trace.getDebugCode("ls")) trace.out("ls", "Setting problemContext to " + problemContext);
		reviseContextMessage();
	}

	/**
	 * Force a revision to the {@link #getContextMessageId()} value.
	 * @deprecated Use {@link #getContextMessage(String)} instead
	 */
	public void resetAttemptId() {
		setContextMessageId(null);
		//reviseContextMessage();
	}

	/**
	 * @param msg new value for {@link #lastToolMessage}
	 */
	public void setLastToolMessage(ToolMessage msg) {
		this.lastToolMessage = msg; 
	}

	/**
	 * @return the {@link #lastToolMessage}
	 */
	public ToolMessage getLastToolMessage() {
		return lastToolMessage;
	}

	/**
	 * @param value The name of the instructor to add to {@link #instructors}
	 * @param index The index of the instructor
	 */
	public void addInstructorName(String value, int index) {
		//If we're going to overwrite something, we must be processing a new problem, so clear the old data
		if (getInstructorName(index) != null)
			for (int i = 0; i < this.instructors.size(); i++) this.instructors.set(index,null);
		this.instructors.set(index,value);
		reviseContextMessage();
	}

	/**
	 * @param index The index of the instructor name 
	 * @return the name in {@link #instructors} at index
	 */
	public String getInstructorName(int index) 	{
		return (String)this.instructors.get(index);
	}
	
	/**
	 * @param value The condition name added to {@link #conditionNames}
	 * @param index The index of the condition
	 */
	public void addStudyConditionName(String value, int index) {
		//If we're going to overwrite something, we must be processing a new problem, so clear the old data
		if (getStudyConditionName(index) != null)
			for (int i = 0; i < this.conditionNames.size(); i++) this.conditionNames.set(index,null);
		this.conditionNames.set(index,value);
		reviseContextMessage();
	}

	/**
	 * Set the list of {@link #conditionNames} from a comma-delimited
	 * string.
	 * @param conditionNames comma-delimited string
	 */
	public void setStudyConditionNames(String conditionNames) {
		if (conditionNames == null) return;
		String[] split = conditionNames.split(",");
		for (int i = 0; i < split.length; ++i) {
			split[i] = split[i].trim();
			addStudyConditionName(split[i], i);
		}
	}

	/**
	 * @param index The index of the condition name 
	 * @return the name in {@link #conditionNames} at index
	 */
	public String getStudyConditionName(int index) 	{
		return (String)this.conditionNames.get(index);
	}
	
	/**
	 * @param value The condition type added to {@link #conditionTypes}
	 * @param index The index of the condition
	 */
	public void addStudyConditionType(String value, int index) {
		//If we're going to overwrite something, we must be processing a new problem, so clear the old data
		if (getStudyConditionType(index) != null)
			for (int i = 0; i < this.conditionTypes.size(); i++) this.conditionTypes.set(index,null);		
		this.conditionTypes.set(index,value);
		reviseContextMessage();
	}

	/**
	 * Set the list of {@link #conditionTypes} from a comma-delimited
	 * string.
	 * @param conditionTypes comma-delimited string
	 */
	public void setStudyConditionTypes(String conditionTypes) {
		if (conditionTypes == null) return;
		String[] split = conditionTypes.split(",");
		for (int i = 0; i < split.length; ++i) {
			split[i] = split[i].trim();
			addStudyConditionType(split[i], i);
		}
	}

	public String getStudyConditionType(int index) {
		return (String)this.conditionTypes.get(index);
	}
	/**
	 * @param value The condition description added to {@link #conditionDescriptions}
	 * @param index The index of the condition
	 */
	public void addStudyConditionDescription(String value, int index) {
		//If we're going to overwrite something, we must be processing a new problem, so clear the old data
		if (getStudyConditionDescription(index) != null)
			for (int i = 0; i < this.conditionDescriptions.size(); i++) this.conditionDescriptions.set(index,null);
		this.conditionDescriptions.add(index,value);
		reviseContextMessage();
	}

	/**
	 * Set the list of {@link #conditionDescriptions} from a comma-delimited
	 * string.
	 * @param conditionDescriptions comma-delimited string
	 */
	public void setStudyConditionDescriptions(String conditionDescriptions) {
		if (conditionDescriptions == null) return;
		String[] split = conditionDescriptions.split(",");
		for (int i = 0; i < split.length; ++i) {
			split[i] = split[i].trim();
			addStudyConditionDescription(split[i], i);
		}
	}

	public String getStudyConditionDescription(int index) {
		return (String)this.conditionDescriptions.get(index);
	}
	
	/**
	 * @param value The level name added to {@link #levelNames}
	 * @param index The index of the level
	 */	
	public void addDatasetLevelName(String value, int index) {
		for (int i = levelNames.size(); i <= index+1; i++)
			levelNames.add(null);		
		levelNames.set(index + 1,value);
		reviseContextMessage();
	}

	public String getDatasetLevelName(int index) {
		return (String)this.levelNames.get(index);
	}
	
	/**
	 * @param value The level type added to {@link #levelTypes}
	 * @param index The index of the level
	 */	
	public void addDatasetLevelType(String value, int index) {
		for (int i = levelTypes.size(); i <= index+1; i++)
			levelTypes.add(null);		
		levelTypes.set(index + 1,value);
		reviseContextMessage();
	}
	
	public String getDatasetLevelType(int index) {
		return (String)this.levelTypes.get(index);
	}

	public void setClassName(String className) {
		if (className != null && className.length() > 0) {
			if (!className.equalsIgnoreCase(this.className)) {
				this.className = className;
				reviseContextMessage();
			}
		}
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassPeriod(String classPeriod) {
		if (classPeriod != null && classPeriod.length() > 0) {
			if (!classPeriod.equalsIgnoreCase(this.classPeriod)) {
				this.classPeriod = classPeriod;
				reviseContextMessage();
			}
		}
	}
	
	public String getClassPeriod() {
		return classPeriod;
	}

	public void setClassDescription(String classDescription) {
		if (classDescription != null && classDescription.length() > 0) {
			if (!classDescription.equalsIgnoreCase(this.classDescription)) {
				this.classDescription = classDescription;
				reviseContextMessage();
			}
		}
	}
	
	public String getClassDescription() {
		return classDescription;
	}

	public void addCustomFieldValue(String value, int index) {
		//If we're going to overwrite something, we must be processing a new problem, so clear the old data
		if (getCustomFieldValue(index) != null)
			for (int i = 0; i < this.customValues.size(); i++) this.customValues.set(index,null);
		this.customValues.set(index,value);
		reviseContextMessage();
	}
	
	public String getCustomFieldValue(int index)
	{
		return (String) customValues.get(index);
	}

	public void addCustomFieldName(String value, int index) {
		if (trace.getDebugCode("ls")) trace.out("ls", "Adding Custom Field Name: " + value + " at index " + index);
		//If we're going to overwrite something, we must be processing a new problem, so clear the old data
		if (getCustomFieldName(index) != null)
			for (int i = 0; i < this.customNames.size(); i++) this.customNames.set(index,null);
		this.customNames.set(index,value);
		reviseContextMessage();
	}
	
	public String getCustomFieldName(int index)
	{
		return (String) customNames.get(index);
	}

	/**
	 * Sets {@link #loggedSessionAlready} to false to force logging of session record.
	 */
	private void setSessionChanged() {
		loggedSessionAlready = false;
	}

	/**
	 * @return the {@link #problemTutorFlag}
	 */
	public String getProblemTutorFlag() {
		return problemTutorFlag;
	}

	/**
	 * @param problemTutorFlag new value for {@link #problemTutorFlag}
	 */
	public void setProblemTutorFlag(String problemTutorFlag) {
		this.problemTutorFlag = problemTutorFlag;
	}

	/**
	 * @return the {@link #problemOtherFlag}
	 */
	public String getProblemOtherFlag() {
		return problemOtherFlag;
	}

	/**
	 * @param problemOtherFlag new value for {@link #problemOtherFlag}
	 */
	public void setProblemOtherFlag(String problemOtherFlag) {
		this.problemOtherFlag = problemOtherFlag;
	}

	/**
	 * Pass a DataShop message to the logger.
	 * @param dsMsg
	 * @param timeStamp
	 * @see edu.cmu.pslc.logging.LogContext#logIt(edu.cmu.pslc.logging.Message, java.util.Date)
	 */
	public boolean logIt(String dsMsg, Date timeStamp) {
		final String encoding = OliDatabaseLogger.ENCODING_UTF8; 
		
		if (trace.getDebugCode("miss")) trace.out("miss", "Logging it with a Message obj+++++++++++++++"+dsMsg.toString());
		
		if (getServer() == null || getServer().getLoggingSupport() == null)
			return false;
		
		StreamLogger sLogger = getOLILogger();
		DiskLogger dLogger = getDiskLogger();
		Boolean sLogResult = new Boolean(sLogger == null);
		Boolean dLogResult = new Boolean(dLogger == null);
		if (sLogResult && dLogResult)
			return false;                // no loggers
		ActionLog aLog = createActionLog(dsMsg, timeStamp);
		if (sLogger != null)
			sLogResult = sLogger.logActionLog(aLog);
		if (dLogger != null)
			dLogResult = dLogger.logActionLog(aLog);
		return sLogResult != null && sLogResult.booleanValue() && dLogResult != null && dLogResult.booleanValue();
	}

	private ActionLog createActionLog(String dsMsg, Date timeStamp) {
        ActionLog actionLog = new ActionLog();
        actionLog.setTimeStamp(timeStamp);
        actionLog.setInfo(dsMsg);
        if (getContextMessage() != null) {
        	actionLog.setUserGuid(getContextMessage().getUserId());
        	actionLog.setSessionId(getContextMessage().getSessionId());
        	actionLog.setTimezone(getContextMessage().getTimeZone());
        } else {
        	actionLog.setUserGuid(getStudentName());
        	actionLog.setSessionId(getSessionId());
        	actionLog.setTimezone(TimeZone.getDefault().toString());
        }
        actionLog.setSourceId(getSourceId());
        actionLog.setInfoType(getInfoType());
		return actionLog;
	}

	/**
	 * Pass a DataShop message to the logger.
	 * @param dsMsg
	 * @param timeStamp
	 * @see edu.cmu.pslc.logging.LogContext#logIt(edu.cmu.pslc.logging.Message, java.util.Date)
	 */
	public boolean logIt(Message dsMsg, Date timeStamp) {
		final String encoding = OliDatabaseLogger.ENCODING_UTF8; 
		

		if (trace.getDebugCode("miss")) trace.out("miss", "Logging it with a Message obj+++++++++++++++"+dsMsg.toString());
		trace.out(" The following is logged at LogIt : "+dsMsg.toString());
		StreamLogger sLogger = getOLILogger();
		DiskLogger dLogger = getDiskLogger();
		boolean result = true;
		if (sLogger != null) {
			OliDatabaseLogger odl = new OliDatabaseLogger(sLogger, encoding);
			result &= odl.log(dsMsg, timeStamp);
		}
		if (dLogger != null) {
			OliDiskLogger odl = new OliDiskLogger(dLogger, encoding);
			result &= odl.log(dsMsg, timeStamp);
		}
		return result;
	}
    // ////////////////////////////////////////////////////
    /**
     * Public access to the OLI (server) logger. If returns null, then logging
     * is disabled.
     * 
     * @return {@link #streamLogger}; returns null if {@link #useOLILogging()} is false
     */
    // ////////////////////////////////////////////////////
    public StreamLogger getOLILogger() {
        if (useOLILogging())
            return streamLogger;
        
        return null;
    }

	/**
	 * Method for testing only. Do not use this method for creating a logger.
	 * @return diskLogger
	 */
	public DiskLogger getSubstituteDiskLogger() {
		return diskLogger;
	}
	/**
	 * Method for testing only. Do not use this method for creating a logger.
	 * @param logger
	 */
	public void setSubstituteDiskLogger(DiskLogger logger) {
		diskLogger = logger;
	}
    
    // ////////////////////////////////////////////////////
    /**
     * Public access to the disk logger. If returns null, then disk logging is
     * disabled.
     * 
     * @return {@link #diskLogger}; returns null if {@link #useDiskLogging()} is
     *         false
     */
    // ////////////////////////////////////////////////////
    public DiskLogger getDiskLogger() {
        if (useDiskLogging())
            return diskLogger;
        else
            return null;
    }
//is it safe to do geturl.tostring.. can url be null??..
	public String getLogServiceURL() {
		if(getServer()!=null){
			if(getOLILogger()!=null)
				return getOLILogger().getURL().toString();
		}
		return null;
	}
    /**
     * Return the current state of the preference named by
     * {@link BR_Controller#USE_DISK_LOGGING}.
     */
    public boolean useDiskLogging() {
    	PreferencesModel pm = getServer().getPreferencesModel();
        Boolean v = pm.getBooleanValue(BR_Controller.USE_DISK_LOGGING);
        if (v == null)
            return false;

        if (v.booleanValue() && diskLogger == null) {
            String logFileDir = pm
                    .getStringValue(BR_Controller.DISK_LOGGING_DIR);

            startDiskLogging(logFileDir);

        } else if (!v.booleanValue() && diskLogger != null) {
            diskLogger = null;
        }
        return v.booleanValue();
    }
	public String getUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String isLogToDiskTrue() {
		// TODO Auto-generated method stub
		if(useDiskLogging())
				return "true";
		return "false";
	}
    /**
     * Return the current state of the preference named by
     * {@link BRPanel#USE_OLI_LOGGING}.
     */
    protected boolean useOLILogging() {
        PreferencesModel pm = getServer().getPreferencesModel();
        Boolean v = pm.getBooleanValue(BR_Controller.USE_OLI_LOGGING);
        if (v == null)
            return false;

        if (v.booleanValue() && streamLogger == null) {
            String loggingURL = pm
                    .getStringValue(BR_Controller.OLI_LOGGING_URL);
            startOLILogging(loggingURL);
        } else if (!v.booleanValue() && streamLogger != null) {
            streamLogger = null;
        }
        return v.booleanValue();
    }
    // ////////////////////////////////////////////////////
    /**
     * Open a stream to log messages to a host. Sets {@link #streamLogger}.
     * 
     * @param loggingURL
     *            URL of logging service
     */
    // ////////////////////////////////////////////////////
    protected synchronized void startOLILogging(String loggingURL) {
        try {
            streamLogger = new StreamLogger();
            streamLogger.setURL(loggingURL);
            //hmmm maybe have logcontext do a setSessionChanged?
            setSessionChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ////////////////////////////////////////////////////
    /**
     * Open a disk log file "tutorlog.xml" and write an initial entry. Sets
     * {@link #diskLogger}.
     * 
     * @param logFileDir
     *            path name for log file; if null or empty, uses default "."
     */
    // ////////////////////////////////////////////////////
    protected synchronized void startDiskLogging(String logFileDir) {
        this.logFileDir = logFileDir; 
        trace.out ("log", "START DISK LOGGING");
        try {
            diskLogger = new DiskLogger();
            if (logFileDir == null || logFileDir.length() < 1)
                logFileDir = ".";
            else {
                File dir = new File(logFileDir);
                if (!dir.isDirectory() && !dir.mkdirs()) {
                	Utils.showExceptionOccuredDialog(null,
            				"Cannot create directory " + logFileDir +
                            ": to log to current directory instead", "Warning");
                    logFileDir = ".";
                }
            }
            setSessionChanged();
            
            diskLogFile = logFileDir + File.separator+ getDefaultFilename();
            diskLogger.setOutfile(diskLogFile);
            
            //for junit testing
            lastLogFile = diskLogFile;
            
            trace.out("diskLogger = " + diskLogger + ", logFileDir " + logFileDir
                    + ", diskLogFile " + diskLogFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public String isLogToRemoteServerTrue() {
		// TODO Auto-generated method stub
		if(getServer()!=null)
			if(useOLILogging())
				return "true";
		return "false";
	}
	
	protected TSLauncherServer getServer() {
		return this.server;
	}
	
	public void setLogConsole(LogConsole logConsole){
		this.logConsole = logConsole;
	}
}
