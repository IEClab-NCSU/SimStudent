/*
 * Created on Oct 12, 2004
 *
 */
package edu.cmu.pact.ctatview.wizardframe;

import javax.swing.JPanel;


/**
 * @author mpschnei
 * 
 */
public interface WizardPanelInterface {

	public boolean getNextButtonState();
	
	public boolean getPreviousButtonState();
	
	public boolean getCancelButtonState();
	
	public boolean getFinishButtonState();
	
	// Returns the name of the panel that should come next, depending
	// on the state of this panel.
	public String getNextPanelName();
	
	public JPanel getJPanel();

	/**
	 * @return
	 */
	public String getPanelName();
}