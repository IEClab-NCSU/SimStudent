package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Author: zzhang
 * 
 * Filter to work with JFileChooser to select .txt type file.
*/

public class TxtFilter extends FileFilter implements java.io.FileFilter {

	/** See {@link #getDescription()}. */
	private static final String DROP_DOWN_LABEL = "Directories or text files (*.txt)";
	
	/**
	 * Accept directories and .txt files.
	 * @param f filename presented to testing
	 * @return true if f is a directory or ends with ".brd"
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept (File f) {
		String path = f.getPath();
		return (f.isDirectory() || path.toLowerCase().endsWith(".txt"));
	}

	/**
	 * From Swing implementation. Not used in AWT.
	 * @return {@value #DROP_DOWN_LABEL}
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription () {
		return DROP_DOWN_LABEL;
	}

}
