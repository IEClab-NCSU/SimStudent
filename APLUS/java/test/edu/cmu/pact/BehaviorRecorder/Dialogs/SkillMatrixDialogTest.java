/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class SkillMatrixDialogTest extends TestCase {
	
	public static Test suite() {
		return new TestSuite(SkillMatrixDialogTest.class);
	}

	/**
	 * Test method for {@link edu.cmu.pact.BehaviorRecorder.Dialogs.SkillMatrixDialog#splitBySubdir(java.util.List)}.
	 */
	public void testSplitBySubdir() {
		String dir = "Projects";
		List<File> brdFiles = Utils.findFiles(new File(dir), new BrdFilter());
		List<List<File>> result = SkillMatrixDialog.splitBySubdir(brdFiles);
		int parentLen = 0;
		for (File f : brdFiles)
			parentLen = Math.max(parentLen, f.getParent().length());
		trace.out("skills", "splitBySubdir(\""+dir+"\"):\n");
		int i = 0;
		for (List<File> flist : result) {
			for (File f : flist) {
				++i;
				if (trace.getDebugCode("skills"))
					System.out.printf("%3d. %-"+parentLen+"s %s\n", i, f.getParent(), f.getName());
			}
		}
		assertEquals("Utils.findFiles().size() != result size", brdFiles.size(), i);
	}

}
