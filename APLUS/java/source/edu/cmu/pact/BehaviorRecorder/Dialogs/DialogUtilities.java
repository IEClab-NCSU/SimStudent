/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;

/**
 * Utilities common to several dialogs.
 */
public class DialogUtilities {

	/** See {@link JFileChooser#FILES_ONLY}. */
	public static final int FILES_ONLY = JFileChooser.FILES_ONLY;

	/** See {@link JFileChooser#DIRECTORIES_ONLY}. */
	public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;

	/** See {@link JFileChooser#FILES_AND_DIRECTORIES}. */
	public static final int FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;
	
	/** Operating system, from {@link System#getProperty(String)}. Used to choose dialog. */
	private static String osName = System.getProperty("os.name");

	/**
	 * Select a filename using {@link FileDialog} with an optional filter.
	 * @param targetDir starting directory to display
	 * @param filter to create filter for files of desired type
	 * @param title dialog window title
	 * @param buttonLabel {@link #SAVE} or {@link #LOAD} 
	 * @param ctlr for {@link CTAT_Controller#getActiveWindow()}
	 * @return file chosen; null if none or canceled
	 */
	public static File chooseFile(String targetDir, FileFilter filter, String title,
			String buttonLabel, CTAT_Controller ctlr) {
		return chooseFile(targetDir, null, filter, title, buttonLabel, ctlr);
	}

	/**
	 * Select a filename using {@link FileDialog} with an optional filter.
	 * @param targetDir starting directory to display
	 * @param initialFilename if not null, set the filename choice to this name
	 * @param filter if not null, filter for files of desired type
	 * @param title dialog window title
	 * @param buttonLabel label for approve button
	 * @param ctlr for {@link CTAT_Controller#getActiveWindow()}
	 * @return file chosen; null if none or canceled
	 */
	public static File chooseFile(String targetDir, String initialFilename, FileFilter filter,
			String title, String buttonLabel, CTAT_Controller ctlr) {
		return chooseFile(targetDir, initialFilename, filter, title, buttonLabel,
				ctlr == null ? null : ctlr.getActiveWindow());
	}

	/**
	 * Select a filename using {@link FileDialog} with an optional filter.
	 * @param targetDir starting directory to display
	 * @param initialFilename if not null, set the filename choice to this name
	 * @param filter if not null, filter for files of desired type
	 * @param title dialog window title
	 * @param buttonLabel label for approve button
	 * @param parent for {@link CTAT_Controller#getActiveWindow()}
	 * @return file chosen; null if none or canceled
	 */
	public static File chooseFile(String targetDir, String initialFilename, FileFilter filter,
			String title, String buttonLabel, Component parent) {
		return chooseFile(targetDir, initialFilename, filter, title, buttonLabel,
				FILES_ONLY, parent);
	}

	/**
	 * Select a filename using {@link FileDialog} with an optional filter.
	 * @param targetDir starting directory to display
	 * @param initialFilename if not null, set the filename choice to this name
	 * @param filter if not null, filter for files of desired type
	 * @param title dialog window title
	 * @param buttonLabel label for approve button
	 * @param mode one of {@link #FILES_ONLY}, {@link #DIRECTORIES_ONLY} or {@link #FILES_AND_DIRECTORIES}
	 * @param parent for {@link CTAT_Controller#getActiveWindow()}
	 * @return file chosen; null if none or canceled
	 */
	public static File chooseFile(String targetDir, String initialFilename, FileFilter filter,
			String title, String buttonLabel, int mode, Component parent) {
		if (osName != null && osName.toUpperCase().startsWith("MAC OS"))		
			return awtChooseFile(targetDir, initialFilename, filter, title, buttonLabel, mode, parent);
		else
			return swingChooseFile(targetDir, initialFilename, filter, title, buttonLabel, mode, parent);
	}

	/**
	 * Select a filename using {@link JFileChooser} with an optional filter.
	 * @param targetDir starting directory to display
	 * @param initialFilename if not null, set the filename choice to this name
	 * @param filter if not null, filter for files of desired type
	 * @param title dialog window title
	 * @param buttonLabel label for approve button
	 * @param mode one of {@link #FILES_ONLY}, {@link #DIRECTORIES_ONLY} or {@link #FILES_AND_DIRECTORIES}
	 * @param parent for {@link CTAT_Controller#getActiveWindow()}
	 * @return file chosen; null if none or canceled
	 */
	static File swingChooseFile(String targetDir, String initialFilename, FileFilter filter,
			String title, String buttonLabel, int mode, Component parent) {
	    JFileChooser fc = new JFileChooser(targetDir);
	    fc.setDialogTitle(title);
	    if (initialFilename != null && initialFilename.length() > 0)
		    fc.setSelectedFile(new File(targetDir, initialFilename));
	    fc.setFileSelectionMode(mode);
	    if (filter != null) {
	    	fc.setFileFilter(filter);
	    	if (filter instanceof DirectoryFilter) {
	    		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    		fc.setAcceptAllFileFilterUsed(false);
	    	}
	    }
	    int i = fc.showDialog(parent, buttonLabel);  // blocks for user input

	    if (i != JFileChooser.APPROVE_OPTION)
	        return null;
	    else
	    	return fc.getSelectedFile();
	}

	/**
	 * Use the AWT {@link FileDialog} to choose a file. As of JDK 1.6 and
	 * according to Apple's recommendations, this provides a better dialog. 
	 * @param targetDir
	 * @param initialFilename
	 * @param filter
	 * @param title
	 * @param buttonLabel use {@link FileDialog#SAVE} if contains "save" or "create";
	 *        else use {@link FileDialog#LOAD}
	 * @param filesOrDirectories
	 * @param parent
	 * @return
	 */
	static File awtChooseFile(String targetDir, String initialFilename,
			final FileFilter filter, String title, String buttonLabel,
			final int filesOrDirectories, Component parent) {
		FileDialog d = null;
		Frame frame = null;
		int mode = FileDialog.LOAD;
		if (buttonLabel != null) {
			buttonLabel = buttonLabel.toUpperCase();
			if (buttonLabel.contains("SAVE") || buttonLabel.contains("CREATE"))
				mode = FileDialog.SAVE;
		}
		if (filesOrDirectories != FILES_ONLY) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			mode = FileDialog.LOAD;     // CTAT2930: can't choose directories with SAVE mode 
		}
		if (parent instanceof Dialog)
			d = new FileDialog((Dialog) parent, title, mode);
		else {
			frame = new Frame();
			if (parent != null)
				frame.setBounds(parent.getBounds());
			d = new FileDialog(frame, title, mode);
		}
		d.setFile(initialFilename);
		if (filter != null || filesOrDirectories != FILES_ONLY) {
			FilenameFilter awtFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					File f = new File(dir, name);
					if (filter != null && filter.accept(f))
						return true;
					if (f.isDirectory())
						return filesOrDirectories != FILES_ONLY;
					return filter == null;
				}
			};
			d.setFilenameFilter(awtFilter);
		}
		d.setVisible(true);
		
		String selectedDir = d.getDirectory();  // blocks for user input
		String selectedItem = d.getFile();
		if (frame != null) {
			if (trace.getDebugCode("br"))
				trace.out("br", "before frame.dispose()");
			frame.dispose();
			if (trace.getDebugCode("br"))
				trace.out("br", "after frame.dispose()");
		}
	    	System.setProperty("apple.awt.fileDialogForDirectories", "false");
		if (selectedItem == null)
			return null;
		return new File(selectedDir, selectedItem);  // dir can be null
	}
}
