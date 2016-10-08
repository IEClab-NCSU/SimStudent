/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class MergeMassProductionDialogTest extends TestCase {
	
	private MergeMassProductionDialog tstObj = new MergeMassProductionDialog();

	public static Test suite() {
		return new TestSuite(MergeMassProductionDialogTest.class);
	}
	
	private static String[] CellRows = {
		"%(comboBox-values-reasons)%\t\"\"\"--\"\" \"\"the shallowest slope\"\" \"\"the steepest slope\"\" \"\"the smallest intercept\"\" \"\"the largest intercept\"\" \"\"the smallest X value for Y=700\"\" \"\"the smallest Y intercept\"\" \"\"the largest Y intercept\"\" \"\"the largest X value for Y=700\"\" \"\"equal slopes\"\"\"\t\"\"\"--\"\" \"\"the shallowest slope\"\" \"\"the steepest slope\"\" \"\"the smallest intercept\"\" \"\"the largest intercept\"\" \"\"the smallest X value for Y=160\"\" \"\"the smallest Y intercept\"\" \"\"the largest Y intercept\"\" \"\"the largest X value for Y=160\"\" \"\"equal slopes\"\"\"\t\"\"\"--\"\" \"\"the shallowest slope\"\" \"\"the steepest slope\"\" \"\"the smallest intercept\"\" \"\"the largest intercept\"\" \"\"the smallest X value for Y=50\"\" \"\"the smallest Y intercept\"\" \"\"the largest Y intercept\"\" \"\"the largest X value for Y=50\"\" \"\"equal slopes\"\"\"\n",
		"%(comboBox-values)%\t\"\"\"--\"\" \"\"Carl\"\" \"\"Delia\"\"\"\t\"\"\"--\"\" \"\"Miguel\"\" \"\"Gabrielle\"\" \"\"Wyatt\"\"\"\t\"\"\"--\"\" \"\"Connie\"\" \"\"Ronnie\"\" \"\"Lonnie\"\" \"\"Donnie\"\"\"\t\t\t\n"
	};
	
	private static String[][] CellsSingleQuotes = {
		{ "%(comboBox-values-reasons)%", "\"--\" \"the shallowest slope\" \"the steepest slope\" \"the smallest intercept\" \"the largest intercept\" \"the smallest X value for Y=700\" \"the smallest Y intercept\" \"the largest Y intercept\" \"the largest X value for Y=700\" \"equal slopes\"", "\"--\" \"the shallowest slope\" \"the steepest slope\" \"the smallest intercept\" \"the largest intercept\" \"the smallest X value for Y=160\" \"the smallest Y intercept\" \"the largest Y intercept\" \"the largest X value for Y=160\" \"equal slopes\"", "\"--\" \"the shallowest slope\" \"the steepest slope\" \"the smallest intercept\" \"the largest intercept\" \"the smallest X value for Y=50\" \"the smallest Y intercept\" \"the largest Y intercept\" \"the largest X value for Y=50\" \"equal slopes\"" },
		{ "%(comboBox-values)%", "\"--\" \"Carl\" \"Delia\"", "\"--\" \"Miguel\" \"Gabrielle\" \"Wyatt\"", "\"--\" \"Connie\" \"Ronnie\" \"Lonnie\" \"Donnie\"" }		
	};
	
	private static String[][] CellsDoubleQuotes = {
		{ "%(comboBox-values-reasons)%", "\"\"\"--\"\" \"\"the shallowest slope\"\" \"\"the steepest slope\"\" \"\"the smallest intercept\"\" \"\"the largest intercept\"\" \"\"the smallest X value for Y=700\"\" \"\"the smallest Y intercept\"\" \"\"the largest Y intercept\"\" \"\"the largest X value for Y=700\"\" \"\"equal slopes\"\"\"", "\"\"\"--\"\" \"\"the shallowest slope\"\" \"\"the steepest slope\"\" \"\"the smallest intercept\"\" \"\"the largest intercept\"\" \"\"the smallest X value for Y=160\"\" \"\"the smallest Y intercept\"\" \"\"the largest Y intercept\"\" \"\"the largest X value for Y=160\"\" \"\"equal slopes\"\"\"", "\"\"\"--\"\" \"\"the shallowest slope\"\" \"\"the steepest slope\"\" \"\"the smallest intercept\"\" \"\"the largest intercept\"\" \"\"the smallest X value for Y=50\"\" \"\"the smallest Y intercept\"\" \"\"the largest Y intercept\"\" \"\"the largest X value for Y=50\"\" \"\"equal slopes\"\"\"" },
		{ "%(comboBox-values)%", "\"\"\"--\"\" \"\"Carl\"\" \"\"Delia\"\"\"", "\"\"\"--\"\" \"\"Miguel\"\" \"\"Gabrielle\"\" \"\"Wyatt\"\"\"", "\"\"\"--\"\" \"\"Connie\"\" \"\"Ronnie\"\" \"\"Lonnie\"\" \"\"Donnie\"\"\"" }		
	};
	
	/**
	 * Test method for {@link edu.cmu.pact.BehaviorRecorder.Dialogs.MergeMassProductionDialog#getRowConfigureData(java.lang.String)}.
	 */
	public final void testGetRowConfigureData() {
		for (int i = 0; i < CellRows.length; ++i) {
			String s = CellRows[i];
			List<String> result = tstObj.getRowConfigureData(s.trim(), true);
			for (int j = 0; j < CellsSingleQuotes[i].length; ++j)
				assertEquals("CellsSingleQuotes["+i+"]["+j+"]", CellsSingleQuotes[i][j], result.get(j));
		}
		for (int i = 0; i < CellRows.length; ++i) {
			String s = CellRows[i];
			List<String> result = tstObj.getRowConfigureData(s.trim(), false);
			for (int j = 0; j < CellsDoubleQuotes[i].length; ++j)
				assertEquals("CellsDoubleQuotes["+i+"]["+j+"]", CellsDoubleQuotes[i][j], result.get(j));
		}
	}

}
