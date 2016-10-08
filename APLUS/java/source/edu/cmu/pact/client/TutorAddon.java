package edu.cmu.pact.client;

/**
 * This interface provides access to a HintPanel (or equivalent) and its
 * connections
 */
import java.util.List;

import javax.swing.JComponent;

import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.Utilities.MessageEventListener;

public interface TutorAddon extends StudentInterfaceWrapper, MessageEventListener
{
	public ProblemAdvance getProblemAdvance();
	
	public JComponent getTutorPanel();

	public void addMessageEventListener(MessageEventListener me);
	
	public void removeMessageEventListener(MessageEventListener me);
	
	public void openConnection();
	
	public void closeConnection();
	
	/**
	 * Start a session with the tutoring service.
	 * @param preference names (will use the toString() value of each element)
	 * @param preference values (will use the toString() value of each element)
	 * @return sessionGuid
	 */
	public String startTutoringServiceSession(List preferenceNames, List preferenceValues);	
	
	//reset the hint panel
	public void reset();
	
	//a filter for outbound messages to CTAT (list of action names)
	public void setFilter(List<String> filter);
	
	//filter out any action names according to the set filter
	public boolean filter(String str);
}
