/*
 * Created on Mar 11, 2005
 *
 */
package edu.cmu.pact.Utilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Log.AuthorAction;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Log.ProgramAction;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;

/**
 * 
 * A class to hold logging procedures (moved from UniversalToolProxy)
 * 
 * @author mpschnei
 * 
 * Created on: Mar 11, 2005
 */
public class LoggingSupport extends Logger implements PropertyChangeListener {

	private TSLauncherServer server;
    protected String tutorSessionID = "none"
    	+ DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG)
                .format(new Date());
    
    public LoggingSupport(TSLauncherServer server) {
    	
    	super(server);
    	server.setLogger(this);
        this.server = server;
        // When this class gets loaded in NetBeans it can't find
        // JDOM and it causes an error. This should prevent that. mps 1/11/05
        try {
            setLoggingUserID(getServer().getLogger().getStudentName(), false);
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
    }

    public void setEnableAuthorLog(boolean enableAuthorLog) {
        this.enableAuthorLog = enableAuthorLog;
    }


    
    /**
	 * Logs an author action.
	 * 
	 * @param toolName
	 *            the tool that generated the action (e.g. student interface,
	 *            behavior recorder, etc) If a Class object or a CTATWindow
	 *            object would provide more information, call the appropriate
	 *            overloaded calls instead.
	 * @param actionType
	 *            the type of action that occurred
	 * @param argument
	 *            the argument to the action
	 * @param result
	 *            the result of the action
	 * @param resultDetails
	 *            any appropriate information about the result
	 * @return
	 */
    public boolean authorActionLog(String toolName, String actionType,
            String argument, String result, Object resultDetails) {

        if (enableAuthorLog == false)
            return true;

        AuthorAction authorAction = new AuthorAction(actionType, argument,
                result, resultDetails);

        // we may set toolName as sourceId
        authorAction.setSourceId(toolName);
        return oliLog(authorAction);
    }
    
    /**
     * Logging call to be used when this.getClass() gives useful information
     * @param tool usually <b>this</b>.getClass()
     * @param actionType
     * @param argument
     * @param result
     * @param resultDetails
     * @return
     */
    public boolean authorActionLog(Class tool, String actionType,
            String argument, String result, Object resultDetails) 
    {
    	return authorActionLog(tool.toString(), actionType,
    			argument, result, resultDetails);
    }
    
    /**
     * Logging call to be used when the object generating the action is a
     * CTATWindow (e.g. BehaviorRecorder)
     * @param tool usually <b>this</b>
     * @param actionType
     * @param argument
     * @param result
     * @param resultDetails
     * @return
     */
    public boolean authorActionLog(AbstractCtatWindow tool, String actionType,
            String argument, String result, Object resultDetails)
    {
    	// a pretty arbitrary thing to log; feel free to change this
    	// to log whatever information is more important.
    	return authorActionLog(tool.getName() + " " + tool.getTitle(),
    			actionType, argument, result, resultDetails);
    }
    
    /**
     * Overloaded logging method used when only a result is known
     * @param result
     * @param resultDetails
     * @return
     */
    public boolean authorActionLog(String result, Object resultDetails)
    {
    	return authorActionLog("", "", "", result, resultDetails);
    }
    
    /**
     * Overloaded logging method when the result is not yet known
     * @param tool
     * @param actionType
     * @param argument
     * @return
     */
    public boolean authorActionLog(String tool, String actionType, String argument)
    {
    	return authorActionLog(tool, actionType, argument, "", null);
    }
    
    public boolean authorActionLog(Class tool, String actionType, String argument)
    {
    	return authorActionLog(tool, actionType, argument, "", null);
    }
    
    public boolean authorActionLog(AbstractCtatWindow tool, String actionType, String argument)
    {
    	return authorActionLog(tool, actionType, argument, "", null);
    }

    public boolean programActionLog(String toolName, String actionType,
            String argument, String result, String result2) {

		if (enableAuthorLog == false)
            return true;
		
        ProgramAction programAction = new ProgramAction(actionType, argument,
                result, result2);

        // we may set toolName as sourceId
        programAction.setSourceId(toolName);

        return oliLog(programAction);
    }



 



    /**
     * @param problemFullName
     * @param actionType
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveFileToAuthorLog(String problemFullName, String actionType) {
        try {
            File file = new File(problemFullName);
            FileReader fr;
            fr = new FileReader(file);

            char[] fileText = new char[(int) file.length()];

            fr.read(fileText);

            authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER, actionType,
                    problemFullName, new String(fileText), "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) { 
            e.printStackTrace();
        }
    }

    
    /**
     * Set the user identifier parameter for the log. Restarts logging if new
     * value is different and nonempty. 
     * @param _userID new value for {@link #userID}
     * @param restartLogging false to not restart logging in any case
     */
    public void setLoggingUserID(String _userID, boolean restartLogging) {
    	if (_userID == null) {
    		userID = "";            // do NOT let field be set to null
    		return;
    	}
    	restartLogging = restartLogging &&     // restart if different & nonempty
    		(!userID.equalsIgnoreCase(_userID) && _userID.length() > 0);
    	userID = _userID;  // must set before calling startDiskLogging()
		if (restartLogging && useDiskLogging())
			startDiskLogging(logFileDir);
    }

    /**
     * @param tutorSessionID
     *            The tutorSessionID to set.
     */
    public void setTutorSessionID(String tutorSessionID) {
        this.tutorSessionID = tutorSessionID;
    }

    /**
     * @return Returns the tutorSessionID.
     */
    public String getTutorSessionID() {
        return tutorSessionID;
    }

    public void setStudentName(String studentName) {
        trace.out ("log", "set student name: " + studentName);
        super.setStudentName(studentName);
        setLoggingUserID(studentName, true);
    }

    public void setAnonymizedStudentName(String studentName) {
        trace.out ("log", "set student name: " + studentName);
        super.setAnonymizedStudentName(studentName);
        setLoggingUserID(studentName, true);
    }




    /**
     * Respond to property change events. These would transmit
     * changes in system properties.
     * @param evt 
     */
	public void propertyChange(PropertyChangeEvent evt) {
		if (trace.getDebugCode("log")) trace.out("log", "propertyChange("+evt+")");
		String name = evt.getPropertyName();
		String oldValue = (String) evt.getOldValue();
		String newValue = (String) evt.getNewValue();
		super.propertyChange(evt);
		if (Logger.DISK_LOG_DIR_PROPERTY.equalsIgnoreCase(name))
			changeDiskLoggingDir(newValue);
		else if (Logger.AUTH_TOKEN_PROPERTY.equalsIgnoreCase(name))
			setAuthToken(newValue);
		else if (Logger.SESSION_ID_PROPERTY.equalsIgnoreCase(name))
			setSessionId(newValue);
		else if (Logger.LOG_SERVICE_URL_PROPERTY.equalsIgnoreCase(name))
			changeOLILoggingURL(newValue);
		else if (BR_Controller.USE_OLI_LOGGING.equalsIgnoreCase(name))
			loggingPreferenceOnOff(name, newValue);
	    else if (BR_Controller.DISK_LOGGING_DIR.equalsIgnoreCase(name))
            changeDiskLoggingDir(newValue);
        else if (BR_Controller.USE_DISK_LOGGING.equalsIgnoreCase(name))
			loggingPreferenceOnOff(name, newValue);
        else if (Logger.STUDENT_NAME_PROPERTY.equalsIgnoreCase(name))
			setStudentName(newValue);
		else if (Logger.SCHOOL_NAME_PROPERTY.equalsIgnoreCase(name))
			setSchoolName(newValue);
		else if (Logger.COURSE_NAME_PROPERTY.equalsIgnoreCase(name))
			setCourseName(newValue);
		else if (Logger.UNIT_NAME_PROPERTY.equalsIgnoreCase(name))
			setUnitName(newValue);
		else if (Logger.SECTION_NAME_PROPERTY.equalsIgnoreCase(name))
			setSectionName(newValue);
		else if (ENABLE_AUTHOR_LOGGING.equalsIgnoreCase(name))
			setEnableAuthorLog(Boolean.parseBoolean(newValue));
		else if(Logger.STUDY_CONDITION_NAME.equalsIgnoreCase(name))
			setStudyConditionNames(newValue);
	}

	/**
	 * Switch a logging preference on or off. Changes the preference in the PreferencesModel.
	 * @param name {@link BR_Controller#USE_OLI_LOGGING} or {@link BR_Controller#USE_DISK_LOGGING} 
	 * @param newValue true to enable, false to disable logging
	 */
	private void loggingPreferenceOnOff(String name, String newValue) {
		Boolean nv = Boolean.parseBoolean(newValue);
		if (BR_Controller.USE_OLI_LOGGING.equalsIgnoreCase(name))
			getServer().getPreferencesModel().setBooleanValue(BR_Controller.USE_OLI_LOGGING, nv);
		else if (BR_Controller.USE_DISK_LOGGING.equalsIgnoreCase(name))
			getServer().getPreferencesModel().setBooleanValue(BR_Controller.USE_DISK_LOGGING, nv);
		else
			return;
        if (trace.getDebugCode("log")) trace.out("log", name+" now "+(nv != null && nv.booleanValue()));
	}




}
