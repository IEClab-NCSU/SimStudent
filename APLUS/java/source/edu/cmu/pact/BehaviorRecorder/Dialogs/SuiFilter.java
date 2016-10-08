package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
  * Filter to work with JFileChooser to select .sui type file.
  */
public class SuiFilter extends FileFilter implements java.io.FileFilter {

	/** See {@link #getDescription()}. */
	private static final String DROP_DOWN_LABEL = "Student User Interface Files (*.sui)";

	/**
	 * Accept directories and .sui files.
	 * @param f filename presented to testing
	 * @return true if f is a directory or ends with ".sui"
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept (File f) {
		String fileStr = f.getPath();
	  
		// either directory or .sui file
		return (f.isDirectory() || fileStr.toLowerCase().endsWith(".sui"));
	}

	/**
	 * Label for the drop-down list item for the files we accept.
	 * @return {@value #DROP_DOWN_LABEL}
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription () {
		return DROP_DOWN_LABEL;
	}
}
