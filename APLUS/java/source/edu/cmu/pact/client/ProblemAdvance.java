package edu.cmu.pact.client;

import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;

/**
 * An interface for advancing problems.  This class should listen on MessageConnection
 * for a Done response from CTAT, then get the next problem from a curriculum service,
 * send the appropriate setPreferences to CTAT, and load a new problem to the student
 * interface
 * 
 * See TutorshopAdvance for an example implementation
 * @author wko2
 *
 */
public interface ProblemAdvance extends MessageEventListener {
	
	/** Set a message connection for sending setPreference back to CTAT */
	public void setMessageConnection(MessageConnection msgConn);
	
	/** Advance the problem, return name */
	public String advanceProblem();
	
	/** Add a listener for a problem name or path returned by advanceProblem */
	public void addProblemAdvancedListener(ProblemAdvancedListener pal);
	
	/** Listen on CTAT messages for a returned Done request */
	void messageEventOccurred(MessageEvent me);
	
	/** Send problem to ProblemAdvancedListeners */
	public void fireProblemAdvanced(String problemName);
}
