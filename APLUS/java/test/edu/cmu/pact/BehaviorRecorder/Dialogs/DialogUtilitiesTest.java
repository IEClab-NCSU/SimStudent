package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.UIManager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.BrdFilter;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DirectoryFilter;
import edu.cmu.pact.BehaviorRecorder.Dialogs.TxtFilter;
import edu.cmu.pact.ctat.model.CtatModeModelTest;

/**
 * Not a JUnit test. Instead shows the difference between {@link javax.swing.JFileChooser}
 * and {@link java.awt.FileDialog}. The latter is used in
 * {@link DialogUtilities#chooseFile(String, String, java.io.FilenameFilter, String, int, BR_Controller)}
 * @author sewall
 */
public class DialogUtilitiesTest extends TestCase {

    public static Test suite() {
        return new TestSuite(DialogUtilitiesTest.class);
    }
    
    public void testDummy() {
    	assertTrue("dummy method to avoid JUnit warning", true);
    }

	private static void usageExit(String msg) {
		System.err.println(msg+". Usage:\n"+
						   "java -cp ... DialogUtilitiesTest [-n] [-f|-d] [-brd|-txt] [-h]\n"+
						   "where--\n"+
						   "  -n    means load native L&F;\n"+
						   "  -f    means use FILES_ONLY; default FILES_AND_DIRECTORIES;\n"+
						   "  -d    means use DIRECTORIES_ONLY;\n"+
						   "  -brd  means use a BrdFilter;\n"+
						   "  -txt  means use a TxtFilter;\n"+
						   "  -h    displays this help.\n");
	}

	/**
	 * @param args optional argument is "native" to load the native look and feel
	 */
	public static void main(String[] args) throws Exception {
		int fileOrDir = DialogUtilities.FILES_AND_DIRECTORIES;
		FileFilter filter = null;
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i];
			if (!arg.startsWith("-"))
				break;
			switch(arg.charAt(1)) {
			case 'n': case 'N':
				loadNativeLaF(); break;
			case 'f': case 'F':
				fileOrDir = DialogUtilities.FILES_ONLY; break;
			case 'd': case 'D':
				fileOrDir = DialogUtilities.DIRECTORIES_ONLY;
				filter = new DirectoryFilter(); break;
			case 'b': case 'B':
				filter = new BrdFilter(); break;
			case 't': case 'T':
				filter = new TxtFilter(); break;
			case 'h': case 'H':
				usageExit("Help");
			default:
				usageExit("Unknown switch "+arg);
			}
		}
		
		final FileFilter finalFilter = filter;
		final int finalFileOrDir = fileOrDir;
		class dialog extends Thread {
			File f = null;
			String os = null;
			dialog(String os) { this.os = os; }
			public void run() {
				if (os.toUpperCase().contains("MAC"))
				    f = DialogUtilities.awtChooseFile(null, null, finalFilter,
							"awt.FileDialog: Open BRD", "Load",
							finalFileOrDir, null);
				else
					f = DialogUtilities.swingChooseFile(null, null, finalFilter,
							"swing.FileChooser: Open BRD", "Load",
							finalFileOrDir, null);
			}
			File getFile() { return f; }
		};
		dialog du = new dialog("Mac OS");
		dialog fc = new dialog("default");

		du.start(); Thread.sleep(500); fc.start();  // sleep: avoid deadlock of 2 modal windows

		du.join(); fc.join();                       // wait for both to finish

		File uf = du.getFile(), cf = fc.getFile();
		
		System.out.printf("%s %c= %s\n", cf, (cf == null ? '?' : cf.equals(uf) ? '=' : '!'), uf);
		System.exit(0);
	}

	private static void loadNativeLaF() {
	    try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    	System.exit(2);
	    }
	}
}

