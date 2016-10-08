/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pslc.logging;

import java.util.Date;


/**
 * Abstracts those methods needed for a logging subsystem.
 * @author sewall
 */
public interface LogContext {
	public String getCourseName();
	public String getSchoolName();
	public String getSectionName();
	public String getUnitName();
	public void resetAttemptId();
	public ContextMessage getContextMessage(String contextMessageName);
	/**
	 * Get the proper value for the info_type field in the OLI log entry envelope.
	 * @return descriptor for type or format of info field; can be name of governing DTD
	 */
	public String getInfoType();
	/**
	 * Get the sessionID for this logger.
	 * @return String containing the session ID.
	 */
	public String getSessionId();
	/**
	 * Get the URL of the log service.
	 * @return the URL of the log service
	 */
	public String getLogServiceURL();
	
	public void setLastToolMessage(ToolMessage msg);
	public ToolMessage getLastToolMessage();
	/**
	 * 
	 * @return whether or not logging to disk is set
	 */
	public String isLogToDiskTrue();
	
	/**
	 * 
	 * @return whether logging to remote server is set.
	 */
	public String isLogToRemoteServerTrue();
	/**
	 * 
	 * @return the user guid
	 */
	public String getUserId();
	/**
	 * 
	 * @return the problem name
	 */
	public String getProblemName();
	public String getStudentName();
	/**
	 *
	 * @return the question filename if specified
	 */
	public String getQuestionFile();
	/**
	 * Get the value for source_id field in the OLI log entry envelope.
	 * @return identifier for source of log entry
	 */
	
	//public String getClassName();
	
	//public String getSchoolName();
	
	//public String getInstructorName();
	
	public String getSourceId();
	
	/**
	 * Return the current ContextMessage instance.
	 * @return {@link ContextMessage}.
	 */
	public ContextMessage getContextMessage();
//return null..
	/**
	 * Write the message to {@link #fileLogger}, {@link #oliDiskLogger}, {@link #oliDbLogger}.
	 * @param msg message payload to log
	 * @param timeStamp timestamp for log message envelope
	 */
	public boolean logIt(String msg, Date timeStamp);

	/**
	 * Write the message to {@link #fileLogger}, {@link #oliDiskLogger}, {@link #oliDbLogger}.
	 * @param dsMsg message to log
	 * @param timeStamp timestamp for log message envelope
	 */
	public boolean logIt(Message dsMsg, Date timeStamp);

}