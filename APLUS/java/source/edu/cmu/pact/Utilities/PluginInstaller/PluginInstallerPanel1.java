/*
 * Created on Dec 7, 2004
 *  
 */
package edu.cmu.pact.Utilities.PluginInstaller;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import edu.cmu.pact.Utilities.WindowUtils;
import edu.cmu.pact.ctatview.wizardframe.DefaultWizardPanel;
import edu.cmu.pact.ctatview.wizardframe.WizardDialog;


public class PluginInstallerPanel1 extends DefaultWizardPanel {
	private static final String INTRO_TEXT = 
		"<html>This tool will automatically install the Jess <br>"
	  + "extenstions to Eclipse to allow you to edit Jess files "
      + "easily.<br><br>  " +
	  	"Check the box below if you have already " +
	  	"installed Eclipse.<br></html>";

	private static final String TEXT2 = 
		"<html><br>Click Next to continue.</html>";
	
	private JCheckBox checkBox;

	WizardDialog dialog;

	public PluginInstallerPanel1(WizardDialog g, String panelName) {
		super(g, panelName);
		dialog = g;
		Box box = new Box(BoxLayout.Y_AXIS);
		JLabel j = new JLabel(INTRO_TEXT);

		box.add(WindowUtils.wrapLeft(j));
		checkBox = new JCheckBox("Eclipse is installed on this computer.");
		
		box.add(WindowUtils.wrapLeft(checkBox));
		box.add(WindowUtils.wrapLeft(new JLabel(TEXT2)));
		add(box);

	}
 
	public String getNextPanelName() {
		if (checkBox.isSelected()) {
			return "get file";
		}
		// else
		return "no file";
	}

	/* (non-Javadoc)
	 * @see pact.util.WizardFrame.WizardPanelInterface#getNextButtonState()
	 */
	public boolean getNextButtonState() {
		return true;
	}

	/* (non-Javadoc)
	 * @see pact.util.WizardFrame.WizardPanelInterface#getPreviousButtonState()
	 */
	public boolean getPreviousButtonState() {
		return false;
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
		return false;
	}
}

