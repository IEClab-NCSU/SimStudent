/*
 * Created on Apr 30, 2005
 */
package edu.cmu.pact.Utilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import edu.cmu.oli.log.client.ActionLog;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Log.TutorActionLog;
import edu.cmu.pact.jess.JessConsole;

/**
 * For logging of arbitrary events as <message> entries in the OLI format.
 * Log entries created by this class will have the source_id field set to
 * {@link #PACT_CTAT_EVENT}.
 * <b>This class is currently stateless: no more than a single instance is
 * needed.</b>
 * @author sewall
 */
public class EventLogger implements ActionListener {
	
	/** Object to actually do the logging. */
	private LoggingSupport loggingSupport;
    
    /** Source_id for all events logged through this class. */
    private static final String PACT_CTAT_EVENT = "PACT_CTAT_EVENT";
    
    /**
     * Constructor sets all state.
     * @param  loggingSupport value for {@link #loggingSupport}.
     */
    public EventLogger(LoggingSupport loggingSupport) {
        this.loggingSupport = loggingSupport;
	}

    /**
     * Log an {@link java.awt.event.ActionEvent}.
     * {@see java.awt.event.ActionListener}.
     * @param  evt action to log: no-op if event id not ACTION_PERFORMED 
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt == null || evt.getID() != ActionEvent.ACTION_PERFORMED)
            return;
        if (loggingSupport == null)
        	return;
        if (trace.getDebugCode("mps")) trace.out("mps", "ActionEvent id " + evt.getID() + ", cmd " +
                	evt.getActionCommand());
        
		// author action log for CLEAR & STOP
        if (evt.getActionCommand().equalsIgnoreCase(JessConsole.CLEAR)
				|| evt.getActionCommand().equalsIgnoreCase(JessConsole.STOP))
			loggingSupport.authorActionLog(AuthorActionLog.JESS_CONSOLE, evt
					.getActionCommand(), "", "", "");
		
		// log(evt.getActionCommand());
    }
    

    public boolean log(boolean authorAction, 
						String toolname, 
						String actionType, 
						String argument, 
						String result,
						String result2) {
		
        if (loggingSupport == null)
            return true;
        
		if (authorAction)
			return loggingSupport.
						authorActionLog(toolname, actionType, argument,
										result, result2);
		
        return loggingSupport.
						programActionLog(toolname, actionType, argument,
										result, result2);
	}
	
	
    /**
     * Log an entry with a MessageType and one other property.
     * @param  messageType value for MessageType property.
     * @param  name0 property name
     * @param  value0 property value
     * @return result of logger's action
     */
	
	/*
    public boolean log(String messageType, String name0, Object value0) {
        String[] names = {name0};
        Object[] values = {value0};
        ActionLog logEntry = createLogEntry(messageType, names, values);
		return logIt(logEntry);
	}
*/
	
    /**
     * Log an entry with just a MessageType property.
     * @param  messageType value for MessageType property.
     * @return result of logger's action
     */
	/*
    public boolean log(String messageType) {
        ActionLog logEntry = createLogEntry(messageType, null, null);
		return logIt(logEntry);
	}
*/
    /**
     * Log an ActionLog entry. No-op if {@link #loggingSupport} null.
     * @param  entry to log
     * @return result of logger's action
     */
	private boolean logIt(ActionLog logEntry) {
        if (loggingSupport != null)
            return loggingSupport.oliLog(logEntry);
        else
            return false;
    }

	/**
	 * Create a {@link edu.cmu.oli.log.client.TutorActionLog} entry
	 * from a Message type and parallel lists of (name,value) pairs.
	 * @param  messageType value for MessageType property.
	 * @param  names array of names; skips null entries
	 * @param  values parallel array of values; elements must be of type
	 *         String or List
	 * @return logEntry created
	 */
	private ActionLog createLogEntry(String messageType,
									 String[] names,
									 Object[] values) {
		TutorActionLog logEntry =
			new TutorActionLog(TutorActionLog.MSG_ELEMENT);
		logEntry.setSourceId(PACT_CTAT_EVENT);
		if (messageType != null)
			logEntry.addMsgProperty("MessageType", messageType);
		if (names != null && values != null) {
			int n = Math.min(names.length, values.length);
			for (int i = 0; i < n; ++i) {
			    if (names[i] == null)          // skip entries with null names
			        continue;
				if (values[i] instanceof List)
					logEntry.addMsgProperty(names[i], (List) values[i]);
				else if (values[i] != null)
					logEntry.addMsgProperty(names[i], values[i].toString());
				else
					logEntry.addMsgProperty(names[i], "(null)");
			}
		}
		return logEntry;
	}

	/**
	 * @return value of {@link #loggingSupport}
	 */
	public LoggingSupport getLoggingSupport() {
		return loggingSupport;
	}
}
