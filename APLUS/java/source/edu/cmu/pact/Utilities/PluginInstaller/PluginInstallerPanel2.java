/*
 * Created on Dec 7, 2004
 *  
 */
package edu.cmu.pact.Utilities.PluginInstaller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.WindowUtils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.wizardframe.DefaultWizardPanel;
import edu.cmu.pact.ctatview.wizardframe.WizardDialog;


public class PluginInstallerPanel2 extends DefaultWizardPanel implements
		ActionListener {
	private static final String INTRO_TEXT = "<html>Please select the folder where Eclipse is installed.<br>"
			+ "This folder should contain the \"configuration\", \"features\",<br>"
			+ "\"plugins\", and \"workspace\" folders, among others.<br>"
			+ "<br>"
			+ "On Windows this is usually \"c:\\Program Files\\eclipse\\\"" +
					"<br><br>" +
					"On the Mac it is usually \"/Applications/eclipse/\"" +
					"<br></html>";

	WizardDialog dialog;

	private JButton openDialogButton;

	private JFileChooser chooser;

	private JTextField location;

	private String folder;

	public PluginInstallerPanel2(WizardDialog g, String panelName) {
		super(g, panelName);

		dialog = g;
		Box box = new Box(BoxLayout.Y_AXIS);
		JLabel j = new JLabel(INTRO_TEXT);
		location = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(location); }
		location.setEditable(false);
		openDialogButton = new JButton("Select location");
		openDialogButton.addActionListener(this);

		location.setPreferredSize(new Dimension(250, 25));

		box.add(WindowUtils.wrapLeft(j));
		box.add(WindowUtils.wrapLeft(location));
		box.add(Box.createVerticalStrut(8));
		box.add(WindowUtils.wrapLeft(openDialogButton));

		add(box);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.util.WizardFrame.WizardPanelInterface#getNextButtonState()
	 */
	public boolean getNextButtonState() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.util.WizardFrame.WizardPanelInterface#getPreviousButtonState()
	 */
	public boolean getPreviousButtonState() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.util.WizardFrame.WizardPanelInterface#getCancelButtonState()
	 */
	public boolean getCancelButtonState() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.util.WizardFrame.WizardPanelInterface#getFinishButtonState()
	 */
	public boolean getFinishButtonState() {
		if (folder == null)
			return false;
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.util.WizardFrame.WizardPanelInterface#getNextPanelName()
	 */
	public String getNextPanelName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		trace.out ("action performed");
		if (chooser == null) {
			chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		int returnVal = chooser.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			setEclipseFolder(chooser.getSelectedFile());
		}
	}

	/**
	 * @param file
	 */
	private void setEclipseFolder(File file) {
		if (!new File(file.getAbsolutePath() + "/plugins").exists()) {
			showWrongFolderError(file.getAbsolutePath());
			return;
		}
		location.setText (file.getAbsolutePath());
		folder = file.getAbsolutePath();
		dialog.updateButtonState();
	}

	/**
	 *  
	 */
	private void showWrongFolderError(String folder) {
		String message = "<html>The folder you entered: " + folder + "<br>"
				+ "does not appear to be the Eclipse installation folder, <br>"
				+ "because it does not contain a \"plugins\" sub-folder."
				+ "<br><br>Please check the folder name and try again.</html>";

		String title = "Wrong folder";

		JOptionPane.showMessageDialog(dialog, message, title,
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * 
	 */
	public void finish() {
		if (!new File(location.getText() + "/plugins").exists()) {
			showWrongFolderError(location.getText());
			return;
		}
		try {
			copyDirectory (new File("eclipse"), new File(location.getText()));
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "<html>An unrecoverable error occured<br>" +
					"while copying these files.  The files could not be found.<br><br>" +
					"Sorry.</html>",
					"Error copying files.", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "<html>An unrecoverable error occured<br>" +
					"while copying these files.  The files could not be read.<br><br>" +
					"Sorry.</html>",
					"Error copying files.", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return;
		}
		JOptionPane.showMessageDialog(this, "<html>Congratulations!<br><br>" +
				"The Eclipse Jess editor plugins have been installed.</html>");
		System.exit(0);
	}

	
	  // Copies all files under srcDir to dstDir.
    // If dstDir does not exist, it will be created.
    private void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }
    
            String[] children = srcDir.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]),
                                     new File(dstDir, children[i]));
            }
        } else {
            // This method is implemented in e1071 Copying a File
            copyFile(srcDir, dstDir);
        }
    }
    
//  Copies src file to dst file.
    // If the dst file does not exist, it is created
    void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
    
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
