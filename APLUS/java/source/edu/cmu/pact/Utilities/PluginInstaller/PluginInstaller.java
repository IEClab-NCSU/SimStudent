/*
 * Created on Nov 30, 2004
 *
 */
package edu.cmu.pact.Utilities.PluginInstaller;

import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.wizardframe.WizardDialog;

/**
 * @author mpschnei
 *  
 */
public class PluginInstaller extends WizardDialog {

	private final String TITLE = "Install Eclipse Plugins";

	private PluginInstallerPanel2 panel2;

	private PluginInstallerPanel3 panel3;

	public PluginInstaller() {

		panel2 = new PluginInstallerPanel2(this, "get file");
		panel3 = new PluginInstallerPanel3(this, "no file");
		setTitle(TITLE);
		setSize(400, 400);
		addPanel(new PluginInstallerPanel1(this, "first"));
		addPanel(panel2);
		addPanel(panel3);
		setCurrentPanel("first");

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				cancelButtonPressed();
			}

			// This method is called after a window is closed
			public void windowClosed(WindowEvent evt) {
				Frame frame = (Frame) evt.getSource();
				trace.out ("window closed");
			}
		});
	}

	public void finishButtonPressed() {
		if (getCurrentPanelName().equals("get file"))
			panel2.finish();
		else
			panel3.finish();
	}

	/**
	 *  
	 */
	public void cancelButtonPressed() {

		String message = "<html>"
				+ "The Eclipse Jess editor plugin has not been installed.<br>"
				+ "To run the editor installer again please use the link provided<br>"
				+ "in the Cognitive Tutor Authoring Tools installation folder.<br><br>"
				+ "Do you want to cancel the installation of the"
				+ "<br>Eclipse Jess editor plugin?<br><br>" + "</html>";

		String title = "Cancel Jess Editor Installation?";
		int answer = JOptionPane.showConfirmDialog(this, message, title,
				JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			setVisible(false);
			System.exit(0);
		} else
			setVisible(true);
		trace.addDebugCode ("mps");
		trace.printStack("mps");
	}

	public static void main(String[] argv) {
		edu.cmu.pact.Utilities.Utils.setNativeLookAndFeel();

		PluginInstaller p = new PluginInstaller();
        p.setLocationRelativeTo(null);
		p.setVisible(true);

	}
}