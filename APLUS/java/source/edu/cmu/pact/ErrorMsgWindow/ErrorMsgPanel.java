/*
 * Created on Mar 30, 2005
 *
 */
package edu.cmu.pact.ErrorMsgWindow;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;

public class ErrorMsgPanel extends JPanel {
	
	protected JTextArea messageTextArea;

    public ErrorMsgPanel() {
        ImageIcon icon = null;
	    JTabbedPane tabbedPane = new JTabbedPane();
	    JScrollPane scrollPane;
	    
        JPanel panel = new JPanel (false);
        panel.setLayout (new GridLayout (1, 1));
        messageTextArea = new JTextArea();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(messageTextArea); }
        scrollPane = new JScrollPane (messageTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Messages", icon, scrollPane, "Messages from Jess file parser");

        //Add the tabbed pane to this panel.
        setLayout(new GridLayout(1, 1)); 
   
        tabbedPane.setSelectedIndex(0);
   
		//tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        add(tabbedPane);
    }
}
