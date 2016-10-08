/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.Preferences;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DirectoryFilter;

/**
 * Display a file chooser dialog to supply a file-valued preference.
 * So far, this package only needs to let users choose directories,
 * so this class has only a {@link #chooseDirectory(JFrame, String)}
 * method, not one for general files.
 */
public class FileBrowser extends JButton {

	private JTextField textField;
	private String preferenceName;
	private String title;
	
	public FileBrowser (JTextField textField, String preferenceName, String title) {
		super("Browse...");
		this.textField = textField;
		this.preferenceName = preferenceName;
		this.title = title;
	}

	public String getPreferenceName() {
		return preferenceName;
	}
	
	public JTextField getTextField() {
		return textField;
	}

	/**
	 * Let the user choose a directory.
	 * @param frame
	 * @param currentDir
	 * @return
	 */
	public String chooseDirectory (JFrame frame, String currentDir) {
		File file = DialogUtilities.chooseFile(currentDir, null, new DirectoryFilter(),
				title, "Select", DialogUtilities.DIRECTORIES_ONLY, frame);
		if (file == null)
			return null;
		return file.toString();
	}
}
