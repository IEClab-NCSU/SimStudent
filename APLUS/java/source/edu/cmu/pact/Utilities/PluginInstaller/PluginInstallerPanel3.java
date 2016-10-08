/*
 * Created on Dec 7, 2004
 *
 */
package edu.cmu.pact.Utilities.PluginInstaller;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.cmu.pact.ctatview.wizardframe.DefaultWizardPanel;
import edu.cmu.pact.ctatview.wizardframe.WizardDialog;



public class PluginInstallerPanel3 extends DefaultWizardPanel {
	private static final String INTRO_TEXT = 
		"<html>You must first install Eclipse before you " +
		"can use this installer.<br><br>" +
		"Once you have installed Eclipse, you can install the editor plugins <br>" +
		"by running the Plugin Installer again.  On Windows, it is listed in the <br>" +
		"Cognitive Tutor Authoring Tools program group in the start menu.<br><br>" +
		"Please visit http://www.eclipse.org/downloads/ to download <br>" +
		"and install the latest version of Eclipse.<br><br>" +
		"Click Finish to exit.";
	
	WizardDialog dialog;
	
	public PluginInstallerPanel3 (WizardDialog g,String panelName) {
		super(g, panelName);
		dialog = g;
		Box box = new Box(BoxLayout.Y_AXIS);
		JLabel j = new JLabel(INTRO_TEXT);
		box.add(j);
		add(box);
		
	}

	
	/* (non-Javadoc)
	 * @see pact.util.WizardFrame.WizardPanelInterface#getNextButtonState()
	 */
	public boolean getNextButtonState() {
		return false;
	}

	/* (non-Javadoc)
	 * @see pact.util.WizardFrame.WizardPanelInterface#getPreviousButtonState()
	 */
	public boolean getPreviousButtonState() {
		return true;
	}

	/* (non-Javadoc)
	 * @see pact.util.WizardFrame.WizardPanelInterface#getCancelButtonState()
	 */
	public boolean getCancelButtonState() {
		return true;
	}

	/* (non-Javadoc)
	 * @see pact.util.WizardFrame.WizardPanelInterface#getFinishButtonState()
	 */
	public boolean getFinishButtonState() {
		return true;
	}

	/* (non-Javadoc)
	 * @see pact.util.WizardFrame.WizardPanelInterface#getNextPanelName()
	 */
	public String getNextPanelName() {
		return null;
	}


	/**
	 * 
	 */
	public void finish() {
		JOptionPane.showMessageDialog(parent,"After you have installed eclipse, " +
				"you can run the plugin installer again from the Authoring Tools folder.", 
				 "Finish", JOptionPane.INFORMATION_MESSAGE);
		System.exit (0);
	}
	

}