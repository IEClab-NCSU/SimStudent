package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class TimeoutDelay extends AbstractAction {


		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean done = false;
	Connection connection = null;
	Connection otherConnection = null;
	String timeoutType = "";
	TimeoutRecovery recovery;
	
	TimeoutDelay(TimeoutRecovery recov, String type, Connection connect)
	{
		timeoutType = type;
		connection = connect;
		recovery = recov;
	}
	

	TimeoutDelay(TimeoutRecovery recov, String type, Connection connect, Connection connect2)
	{
		timeoutType = type;
		connection = connect;
		otherConnection = connect2;
		recovery = recov;
	}
	
		
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(!done)
		{
			recovery.timeoutRecovery(timeoutType, connection, otherConnection);
			done = true;
			
		}
	}
		
	

}
