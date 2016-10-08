/*
 * Created on Jul 15, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.View.HintWindow;

import java.awt.Component;

import javax.swing.JButton;

public interface HintWindowInterface {
	
    /** Label for hint button. */
	public static final String HELP = Hints.HELP;
	/** Comm name for hint button. */
	public static final String HINT = Hints.HINT;
	
	/**
     * Display the message and enable/disable buttons accordingly
     */
    public abstract void showMessage(String message);
    
    /**
     * this method should be called when ever a correct action is performed
     * and also when ever a new problem is loaded
     */
    public abstract void reset();

    /**
     * @param displayHint The displayHint to set.
     */
    public abstract void setDisplayHint(boolean displayHint);

    public abstract void displaySuccessMessage();

    /**
     * @return
     */
    public abstract Component getDoneButton();

    public abstract Component getHintButton();
    
    public abstract JButton getPrevHintButton();
    
    public abstract JButton getNextHintButton();

    /**
     * @param string
     * @param string2
     */
    public abstract void displayBuggyMessage(String buggyMessage);


	/**
	 * @param suppressFeedback true to prevent feedback to the student, false to allow feedback
	 */
	public void setSuppressFeedback(boolean suppressFeedback);
	
    /**
	 * @return true if feedback to the student is prevented, false if allowed 
	 */
	public boolean getSuppressFeedback();
}
