/*
 * Created on Mar 26, 2004
 *
 */
package edu.cmu.pact.BehaviorRecorder.View.HintWindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommQuestion;
import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.ProblemDoneEvent;
import pact.CommWidgets.event.ProblemDoneListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.BehaviorRecorder.Controller.HintMessagesManagerImpl;
import edu.cmu.pact.CommManager.RemoteCommMessageHandler;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.TutorController;

/**
 * 
 * This is the Hint Message panel which gets embedded in the TutorWrapper
 * 
 * @author sanket
 * 
 */
public class HintPanel extends JPanel implements ActionListener,
        StudentActionListener, IncorrectActionListener, ProblemDoneListener,
        HintWindowInterface {
	
	/**
	 * A JEditorPane whose minimum and preferred dimensions preserve some room, even when empty.
	 */
    public class MinSizeJEditorPane extends JEditorPane {
        /**
         * Calculate a minimum size from the fonts. Sets {@link #minDimension} if unset 
         * @return {@link #minDimension}
         * @see javax.swing.JComponent#getMinimumSize()
         */
    	public Dimension getMinimumSize() {
    		if (minDimension == null) {
    			FontMetrics bfm = messageTxt.getFontMetrics(bugFont);
    			FontMetrics fm = messageTxt.getFontMetrics(JCommWidget.getDefaultFont());
    			int height = Math.max(fm.getHeight(), bfm.getHeight());
    			int width = 0;
    			int[] widths = fm.getWidths();
    			int[] bWidths = bfm.getWidths();
    			for (int i = Math.min(widths.length, bWidths.length) - 1; 0 <= i; --i) {
    				if (width < widths[i])
    					width = widths[i];
    				if (width < bWidths[i])
    					width = bWidths[i];
    			}
    			minDimension = new Dimension(20*width, 2*height); // 20-characters x 2 lines
    		}
    		return minDimension;
    	}
    	
    	/** 
    	 * @return size that's at least as big as {@link super#getPreferredSize()} and {@link #minDimension}. 
    	 */
    	public Dimension getPreferredSize() {
    		Dimension md = this.getMinimumSize();
    		try {                // sewall 2012/03/05: super.getPreferredSize() was crashing
    			Dimension pd = super.getPreferredSize();
        		Dimension d = new Dimension(Math.max(pd.width, md.width), Math.max(pd.height, md.height));
        		return d;
    		} catch (Exception e) {
    			trace.errStack("error from MinSizeJEditorPane.setPreferredSize(): "+e+
    					(e.getCause() == null ? "" : "; cause "+e.getCause()), e);
    			return md;
    		}
    	}
    }
	
	/** Label for previous-hint button. */
    private static final String PREVIOUS = " << ";

	/** Label for next-hint button. */
	private static final String NEXT = " >> ";

	/** Comm name for done button. */
	public static final String DONE = "done";

	/** instance of the message manager */
    private HintMessagesManager messagesManager;

    /** Done button */
    JCommButton doneBtn;

    /** Hint button */
    JCommButton hintBtn;

    /** when clicked the student is presented with the next hint */
    Hints.HintJButton nextBtn;

    /** when clicked the student is presented with the previous hint */
    Hints.HintJButton prevBtn;

    JEditorPane messageTxt;

    /** default font family */
    String fontFamily = JCommWidget.getDefaultFont().getFamily();

    /** default font size size */
    int fontSize = JCommWidget.getDefaultFont().getSize();

    /** font used for the buggy messages */
    Font bugFont = new Font(fontFamily, Font.BOLD, 12);

    /** indicates whether the commPanel is a dfa panel or not */
    private boolean dfaPanel = false;

    /**
     * controls whether to display the hint from the arc or instead display the
     * standard hint message like in the case of original question, we dont
     * provide any hint messages to the student. We display the next scaffolding
     * question and standard buggy message. Hints from the arc will be displayed
     * only when the displayHint = true This variable is false initially if the
     * first question is an original question and will be set to true when the
     * student asks for a hint on the original question or makes an error on the
     * original question.
     * 
     * This is set to false by the JCommPanel if there are more than 1 question
     * on the interface
     */
    boolean displayHint = true;

    /**
     * message that should be displayed to the student when hint button is
     * clicked for the original question or an error is made on the original
     * question
     */
    private String originalQuestionMessage = "Let me try to break this problem"
            + " down for you. Answer the following questions first";

    String pretestMessage = "No, but we will come back to this item later. Please click on the done button.";

    boolean pretest = false;

    private static String successMessage = "Good. That is correct. Click on the done button.";

    /** Minimun size of the message panel. See {@link #getMinimumSize()}. */
	private Dimension minDimension = null;

	/** Whether feedback to the student should be suppressed (true) or displayed (false). */
	private boolean suppressFeedback = false;

    public HintPanel(TutorController controller, HintMessagesManager hintMessagesManager) {
        this.messagesManager = hintMessagesManager;
        this.setName("HintPanel");

        doneBtn = new JCommButton() {
        	public void sendValue() {
        		if (this.getUniversalToolProxy() == null){
                	messagesManager.requestDone();
        		}else{
        			super.sendValue();
        		}
        	}
        };
        //doneBtn.messagesManager;
        doneBtn.setCommName("done", controller);
        doneBtn.setText("Done");

        hintBtn = new JCommButton() {
        	public void sendValue() {
        		if (this.getUniversalToolProxy() == null)
                	messagesManager.requestHint();
        		else
        			super.sendValue();
        	}
        }; 
        hintBtn.setCommName(HINT, controller);
        hintBtn.setText(HELP);

        nextBtn = new Hints.HintJButton(NEXT);
        nextBtn.addActionListener(this);

        prevBtn = new Hints.HintJButton(PREVIOUS);
        prevBtn.addActionListener(this);

        messageTxt = new MinSizeJEditorPane();
        messageTxt.setName("hintPanelMessageTxt");
        messageTxt.setContentType("text/html");
        messageTxt.setAutoscrolls(true);
        messageTxt.setEditable(false);

        // creating a panel for the buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(doneBtn);
        btnPanel.add(hintBtn);
        btnPanel.add(prevBtn);
        btnPanel.add(nextBtn);

        JScrollPane sp = new JScrollPane(messageTxt);

        JLabel header = new JLabel("Messages");
        Font f = JCommWidget.getDefaultFont();
        header.setFont(new Font(f.getName(), Font.BOLD, 12));
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(header);

        Container panel = this;
        panel.setLayout(new BorderLayout());
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        reset();
    }

    public HintPanel(HintMessagesManager hintMessagesManager) {
        this(null, hintMessagesManager);
    }

    public HintPanel(RemoteCommMessageHandler handler) {
        this(handler.messagesManager());
        handler.setHintInterface((HintWindowInterface)this);
        doneBtn.setUniversalToolProxy(handler);
        hintBtn.setUniversalToolProxy(handler);
    }
    
    public HintPanel() {
        this(new HintMessagesManagerImpl(null));
    }

	/*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        String message = null;

        if (source.equals(nextBtn))
            message = messagesManager.getNextMessage();
        else if (source.equals(prevBtn))
            message = messagesManager.getPreviousMessage();

        showMessage(message);
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.HintWindowInterface#showMessage(java.lang.String)
     */
    public void showMessage(String message) {
        if (trace.getDebugCode("mps")) trace.out("mps", "showMessage(" + message + ")");
        
        if (message != null) {
        	String msgType = messagesManager.getMessageType();
            if (msgType.equalsIgnoreCase(HintMessagesManager.BUGGY_MESSAGE)
            		|| msgType.equalsIgnoreCase(HintMessagesManager.INCORRECT_ACTION)) {
                messageTxt.setFont(bugFont);
                messageTxt.setText("<b>" + message);
            } else {
                messageTxt.setFont(JCommWidget.getDefaultFont());
                messageTxt.setText(message);
            }

        } else
            /* clearMessages() */;  // sewall 2008/05/11: don't make no-op highlight msg blank prior buggy 

        messageTxt.repaint();

        messagesManager.resetHighlightWidgets();

        checkButtons();
    }

    /**
     * clears the message area
     */
    public void clearMessages() {
        messageTxt.setText("");
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.HintWindowInterface#reset()
     */
    public void reset() {
        clearMessages();
        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        messagesManager.reset();
        if (dfaPanel) {
            hintBtn.setEnabled(false);
        }
    }

    /**
     * this method should be called after any message is displayed
     */
    private void checkButtons() {
        String type = messagesManager.getMessageType();
        if (type.equals(HintMessagesManager.BUGGY_MESSAGE)
                || type.equals(HintMessagesManager.SUCCESS_MESSAGE)) {
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            repaint();
            return;
        }
        if (type.equals(HintMessagesManager.SHOW_HINTS_MESSAGE)) {
            if (messagesManager.hasNextMessage()) {
                nextBtn.setEnabled(true);
            } else {
                nextBtn.setEnabled(false);
            }
            if (messagesManager.hasPreviousMessage()) {
                prevBtn.setEnabled(true);
            } else {
                prevBtn.setEnabled(false);
            }
            hintBtn.reset(null);
        }
        repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.event.StudentActionListener#studentActionPerformed(pact.CommWidgets.event.StudentActionEvent)
     */
    public void studentActionPerformed(StudentActionEvent sae) {
        reset();
        if (dfaPanel) {
            hintBtn.setEnabled(false);
        }
        // display the success message
        Object source = sae.getSource();
        if (source instanceof JCommQuestion) {
            if (((JCommQuestion) source).isOriginalQuestion()) {
                displaySuccessMessage();
            }
        }
        displayHint = true;
    }

    /**
     * display the next message
     */
    private void showNextMessage() {

        String message = "";
        // if the display hint is set to false then display the standard message
        if (!messagesManager.getMessageType().equals(
                HintMessagesManager.SUCCESS_MESSAGE)
                && !displayHint) {
            // set the displayHint to true
            if (!displayHint) {
                displayHint = true;
            }
            // set the messages to the original question message vector
            displayMessage(originalQuestionMessage,
                    HintMessagesManager.BUGGY_MESSAGE);
        }
        // get the next message from the brd
        message = messagesManager.getNextMessage();
        // display the next message
        showMessage(message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.event.IncorrectActionListener#incorrectActionPerformed(pact.CommWidgets.event.IncorrectActionEvent)
     */
    public void incorrectActionPerformed(IncorrectActionEvent e) {
        // System.out.println("inside message frame incoprrect action
        // performed");
        // if this is a dfa item then enable the hint button
        if (dfaPanel) {
            hintBtn.setEnabled(true);
        }
        // display the bug message
        showNextMessage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.HintWindowInterface#setDisplayHint(boolean)
     */
    public synchronized void setDisplayHint(boolean displayHint) {
        this.displayHint = displayHint;
    }

    private void displayMessage(String message, String type) {

        Vector v = new Vector();
        v.add(message);
        messagesManager.setMessages(v);
        messagesManager.setMessageType(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.HintWindowInterface#displaySuccessMessage()
     */
    public void displaySuccessMessage() {
        this.displayMessage(successMessage, HintMessagesManager.SUCCESS_MESSAGE);
        showNextMessage();
    }

	/**
	 * Side effect: set the {@link #hintBtn}, {@link #nextBtn}, {@link #prevBtn} enabled or disabled. 
	 * @param new value for {@link #suppressFeedback}
	 */
	public void setSuppressFeedback(boolean suppressFeedback) {
		this.suppressFeedback = suppressFeedback;
		if (hintBtn != null)
			hintBtn.setEnabled(!suppressFeedback);		
		if (nextBtn != null)
			nextBtn.setEnabled(!suppressFeedback);		
		if (prevBtn != null)
			prevBtn.setEnabled(!suppressFeedback);		
	}

    /**
	 * @return the {@link #suppressFeedback}
	 */
	public boolean getSuppressFeedback() {
		return suppressFeedback;
	}

	/*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.HintWindowInterface#getDoneButton()
     */
    public Component getDoneButton() {
        return doneBtn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.HintWindowInterface#getHintButton()
     */
    public Component getHintButton() {
        return hintBtn;
    }

    public JButton getPrevHintButton() {
        return prevBtn;
    }
    
    public JButton getNextHintButton() {
        return nextBtn;
    }
    
    public HintMessagesManager getMessagesManager() {
        return messagesManager;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.event.ProblemDoneListener#problemDone(pact.CommWidgets.event.ProblemDoneEvent)
     */
    public void problemDone(ProblemDoneEvent e) {
        displayBuggyMessage(pretestMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.HintWindowInterface#displayBuggyMessage(java.lang.String)
     */
    public void displayBuggyMessage(String buggyMessage) {
        displayMessage(buggyMessage, HintMessagesManager.BUGGY_MESSAGE);
        showNextMessage();
    }

    public void handleMessageObject(MessageObject mo) {
        if (mo==null)
            return;
        if (trace.getDebugCode("sp")) trace.out("sp", "isCorrectOrIncorrect: " + (MsgType.isCorrectOrIncorrect(mo)));
        if (MsgType.isCorrectOrIncorrect(mo))
            clearMessages();
        if (trace.getDebugCode("sp")) trace.out("sp", "hasTextFeedback: " + (MsgType.hasTextFeedback(mo)));
        if (MsgType.hasTextFeedback(mo)) {
            getMessagesManager().setMessageObject(mo);
            nextBtn.setEnabled(true);
        }
    }
}
