package edu.cmu.pact.miss.PeerLearning.TutorialBuilderUtility;

import java.awt.Container;


/**
 * Central hub for creating tutorial building utilities
 * @author Stephen
 *
 */

public class TutorialBuilderUtility {
	
	/**
	 * Creates a utility to show a hierarchy and highlight elements on the main GUI
	 * @param c Main GUI for the target program
	 */
	public static void createHighlightUtility(Container c)
	{
		DOMTree tree = new DOMTree(c);
		@SuppressWarnings("unused")
		SelectionScreen s = new SelectionScreen(tree);
	}
	
	public static void createHighlightUtility(Container c, String configFilePath)
	{
		DOMTree tree = new DOMTree(c);
		ConfigFileRunner cfr = new ConfigFileRunner(configFilePath, tree);
		c.addComponentListener(cfr);
	}
}
