/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author sewall
 *
 */
public class DirectoryFilter extends FileFilter implements java.io.FileFilter {

	/** See {@link #getDescription()}. */
	private static final String DROP_DOWN_LABEL = "Directories";

	/**
	 * Accept directories only.
	 * @param f filename presented to testing
	 * @return true if f is a directory
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept (File f) {
		return f.isDirectory();
	}

	/**
	 * Label for the drop-down list item for the files we accept.
	 * @return #DROP_DOWN_LABEL
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription () {
		return DROP_DOWN_LABEL;
	}
}
