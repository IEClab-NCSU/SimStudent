package edu.cmu.pact.miss.MetaTutor;

import java.awt.Component;

import javax.swing.JButton;

public interface APlusHintDialogInterface {

	/**	 */
	public static final String HELP = "Help";
	
	/**	 */
	public static final String HINT = "Hint";

	/**
	 * @param message
	 */
	public abstract void showMessage(String message);
	
	/**	 */
	public abstract void reset();
	
	/**
	 * @param displayHint
	 */
	public abstract void setDisplayHint(boolean displayHint);
	
	/**
	 * 
	 */
	public abstract void displaySuccessMessage();
	
	/**
	 * 
	 * @return
	 */
	public abstract Component getDoneButton();
	
	/**
	 * 
	 * @return
	 */
	public abstract Component getHintButton();
	
	/**
	 * 
	 * @return
	 */
	public abstract JButton getPrevHintButton();
	
	/**
	 * 
	 * @return
	 */
	public abstract JButton getNextHintButton();
	
	 /**	*/
    public abstract void displayBuggyMessage(String buggyMessage);

    /**		*/
	public abstract boolean isHintButton(Component c);


	/**		*/
	public void setSuppressFeedback(boolean suppressFeedback);
	
    /**		*/
	public boolean getSuppressFeedback();
	
	public void showThinkBubble();

}
