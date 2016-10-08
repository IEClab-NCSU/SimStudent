/*
 * HintsDialogWindow.java
 *
 * Created on July 13, 2004, 7:19 PM
 */

package edu.cmu.pact.BehaviorRecorder.View.HintWindow;

/**
 *
 * @author  zzhang
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.ProblemDoneEvent;
import pact.CommWidgets.event.ProblemDoneListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.Utilities.trace;

public class HintWindow extends JDialog implements ActionListener, 
                StudentActionListener, IncorrectActionListener, 
                ProblemDoneListener, HintWindowInterface {
    
    /** Label on button to retrieve next hint in sequence. */
    private static final String NEXT_HINT = "  Next Hint >>  ";

    /** Label on button to retrieve previous hint in sequence. */
    private static final String PREVIOUS_HINT = "  << Previous Hint  ";

	HintMessagesManager messagesManager;
    
    // message to display 
    String message;
    
    // message display window, support HTML format message
    private JEditorPane hintsJEditorPane;
    protected JScrollPane hintsJEditorPaneScrollPane;

    private JPanel okCancelPanel = new JPanel();

    private Hints.HintJButton previousJButton = new Hints.HintJButton(PREVIOUS_HINT);
    private Hints.HintJButton nextJButton = new Hints.HintJButton(NEXT_HINT);
    private Hints.HintJButton okJButton = new Hints.HintJButton("    OK    ");
    
    // default font family 
    String fontFamily = JCommWidget.getDefaultFont().getFamily();
    
    // default font size size 
    int fontSize = JCommWidget.getDefaultFont().getSize();
	
    // buggy messages font
    Font bugFont = new Font(fontFamily, Font.BOLD, 12);
    
    Container contentPane = getContentPane();

    boolean visibleFlag = false;

	/** Whether feedback to the student should be suppressed (true) or displayed (false). */
	private boolean suppressFeedback = false;

    /**
     * Creates a new instance of HintsDialogWindow
     * @param owner Frame; frame in which this dialog would be displayed
     * @param messagesManager
     */
    public HintWindow(Frame parent, HintMessagesManager messagesManager) {
        super(parent, false);
        init(messagesManager);
    }
    
    /**
     * Common code for all constructors.
     * @param messagesManager
     */
    private void init(HintMessagesManager messagesManager_a) {
		setTitle("Hint Window");
		messagesManager = messagesManager_a;
		messagesManager.setHintInterface(this);
		
        setLocation(new java.awt.Point(400,200));
        setSize(400, 260);
        setResizable(true);
        contentPane.setLayout(new BorderLayout());

        hintsJEditorPane = new JEditorPane();
        hintsJEditorPane.setName("hintsJEditorPane");
        hintsJEditorPane.setContentType("text/html");
        hintsJEditorPane.setText("<html><br><br><br><br></html>");
        //hintsJEditorPane.setContentType("text/plain");
        hintsJEditorPane.setAutoscrolls(true);
        hintsJEditorPane.setEditable(false);
        hintsJEditorPane.setFocusable(false);
        hintsJEditorPaneScrollPane = new JScrollPane(hintsJEditorPane);
        contentPane.add(hintsJEditorPaneScrollPane, BorderLayout.CENTER);

        okCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        okCancelPanel.add(previousJButton);
        okCancelPanel.add(nextJButton);
        okCancelPanel.add(okJButton);

        contentPane.add(okCancelPanel, BorderLayout.SOUTH);

        previousJButton.addActionListener(this);
        nextJButton.addActionListener(this);
        okJButton.addActionListener(this);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                messagesManager.dialogCloseCleanup();
                reset();

                visibleFlag = false;
                setVisible(false);
            }
        });
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    /**

     */
    /////////////////////////////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent ae) {
        JButton selectedButton = (JButton) ae.getSource();
        
        trace.out(5, this, "actionPerformed ae.getSource() = " + selectedButton.getText());
        
        if (selectedButton == okJButton) {
            
            messagesManager.dialogCloseCleanup();
            reset();
            
            visibleFlag = false;
            setVisible(false);
            
            return;
        }
        
        if (selectedButton == nextJButton)
            message = messagesManager.getNextMessage();
        else if (selectedButton == previousJButton) 
            message = messagesManager.getPreviousMessage();
            
        showMessage(message);
        
        return;
    }
    
    /** Display the message and enable/disable buttons accordingly */
    public void showMessage(String message)
    {
        if (trace.getDebugCode("inter")) trace.out("inter", "show message: " + message);
        hintsJEditorPane.setText("");
        
        if (message == null) {
            
            trace.out(5, this, "show message is null");
            reset();
            messagesManager.dialogCloseCleanup();
            visibleFlag = false;
            setVisible(false);
            
            return;
        }
        
        if(messagesManager.getMessageType().equals(HintMessagesManager.BUGGY_MESSAGE)){
            hintsJEditorPane.setFont(bugFont);
            hintsJEditorPane.setText("<b>" + message);
        }else{
            hintsJEditorPane.setFont(JCommWidget.getDefaultFont());
            hintsJEditorPane.setText(message);
            System.err.println("hintsJEditorPane Text = " + hintsJEditorPane.getText());
        }
        
        trace.out(5, this, "repaint hintsJEditorPane.");

        resetButtonEnables();
        
        messagesManager.resetHighlightWidgets();

        if (message.trim().equalsIgnoreCase("HighlightNextCell") || message.trim().equals("") ) {
            visibleFlag = false;
            setVisible(false);
        } else {
            visibleFlag = true;
            setVisible(true);
        }

        
        return;
    }
    
    private void resetButtonEnables() {
        previousJButton.setEnabled(messagesManager.hasPreviousMessage());
        nextJButton.setEnabled(messagesManager.hasNextMessage());
        
        if (visibleFlag) {
            this.repaint();
            this.validate();
        }
        
        return;
    }
    
    public void reset() {
        
        if (!visibleFlag)
            return;
        
        trace.out(5, this, "reset hint window");
        hintsJEditorPane.setText("");
        
        previousJButton.setEnabled(false);
        nextJButton.setEnabled(false);
        messagesManager.reset();
        
        repaint();
        
        return;
    }

    public void studentActionPerformed(StudentActionEvent sae) {
        // TODO Auto-generated method stub
        
    }

    public void incorrectActionPerformed(IncorrectActionEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void problemDone(ProblemDoneEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void setDisplayHint(boolean displayHint) {
        // TODO Auto-generated method stub
        
    }

    public void displaySuccessMessage() {
        // TODO Auto-generated method stub
        
    }

    public Component getDoneButton() {
        // TODO Auto-generated method stub
        return null;
    }

    public Component getHintButton() {
        // TODO Auto-generated method stub
        return null;
    }

    public void displayBuggyMessage(String buggyMessage) {
        // TODO Auto-generated method stub
        
    }

    public JButton getNextHintButton() {
        return this.nextJButton;
    }

    public JButton getPrevHintButton() {
        return this.previousJButton;
    }

	/**
	 * Side effect: set the {@link #okJButton}, {@link #nextJButton}, {@link #previousJButton}
	 * enabled or disabled. 
	 * @param new value for {@link #suppressFeedback}
	 */
	public void setSuppressFeedback(boolean suppressFeedback) {
		this.suppressFeedback = suppressFeedback;
		if (okJButton != null)
			okJButton.setEnabled(!suppressFeedback);		
		if (nextJButton != null)
			nextJButton.setEnabled(!suppressFeedback);		
		if (previousJButton != null)
			previousJButton.setEnabled(!suppressFeedback);		
	}

    /**
	 * @return the {@link #suppressFeedback}
	 */
	public boolean getSuppressFeedback() {
		return suppressFeedback;
	}
}
