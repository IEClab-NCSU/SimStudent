/*
 * Created on Mar 30, 2005
 *
 */
package edu.cmu.pact.ErrorMsgWindow;

/**
 * @author Kuok Chiang Kim
 * 
 * Displays parsing messages from Jess file editor (Eclipse)
 *
 */
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.text.DateFormat;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
//import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;

public class ErrorMsgWindow extends AbstractCtatWindow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7347261388883585907L;
	private ErrorMsgPanel cmp;
	
	//////////////////////////////////////////////////////////////////////
	//	
	//////////////////////////////////////////////////////////////////////
	public ErrorMsgWindow(CTAT_Launcher server) {
		super(server);
		this.setTitle("Messages from Eclipse Jess File Editor");
		this.applyPreferences();
        
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	setVisible (false);
            	dispose();
            }
        });

		cmp = new ErrorMsgPanel();
        getContentPane().add(cmp, BorderLayout.CENTER);
        setSize(400, 250);
        setLocation (200, 200);
        
    }

	/**
	 * Append message from Jess parser to message window
	 * @param message
	 */
	public void addMessage (String message) {		
		Date date = new Date(System.currentTimeMillis());
		String strCurrentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
		strCurrentDate += " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
		cmp.messageTextArea.append (strCurrentDate + ": " + message + "\n");
	}

}

