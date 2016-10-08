/**
 * Copyright 2011 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.View.HintWindow;

import java.awt.Component;
import java.util.List;

import javax.swing.JButton;

import edu.cmu.pact.ctat.view.AvoidsBackGrading;

/**
 * Utilities to consolidate common hint logic.
 */
public class Hints {

	/**
	 * Combine a JButton with a marker interface telling that it's a hint button.
	 */
	public static class HintJButton extends JButton implements AvoidsBackGrading {

		/** For superclass no-argument constructor. */
		public HintJButton() { super(); }
		
		/**
		 * For superclass constructor accepting a label.
		 * @param text label for this button
		 */
		public HintJButton(String text) { super(text); }
	}

	/** Label for hint buttons. */
	public static final String HELP = "Help";
	
	/** Lower-case label for hint buttons. */
	private static final String help = HELP.toLowerCase();

	/** Label for hint buttons. */
	public static final String HINT = "hint";
	
	/** Lower-case label for hint buttons. */
	private static final String hint = HINT.toLowerCase();

	/**
	 * Private constructor prevents instantiation.
	 */
	private Hints() {}

	/**
	 * Tell whether the given text is a label for a hint button.
	 * @param text
	 * @return true if text matches {@value #HINT} or {@value #HELP}, ignoring case
	 */
	public static boolean isHintLabel(String text) {
		if (HELP.equalsIgnoreCase(text))
			return true;
		if (HINT.equalsIgnoreCase(text))
			return true;
		return false;
	}

	/**
	 * Tell whether the given object is a label for a hint button.
	 * @param obj
	 * @return true if obj.toString() satisfies {@link #isHintLabel(String)}
	 */
	public static boolean isHintLabel(Object obj) {
		if (obj == null)
			return false;
		return isHintLabel(obj.toString());
	}

	/**
	 * Tell whether the vector is a selection for a hint button.
	 * @param v
	 * @return true if first element of v satisfies {@link #isHintLabel(String)}
	 */
	public static boolean isHintSelection(Object v) {
		if (v instanceof List)
			return ((List) v).size() < 1 ? false : isHintLabel(((List) v).get(0));
		else
			return isHintLabel((String) v);
	}

	/**
	 * Tell whether a JButton is a hint button.
	 * @param b the button
	 * @return true if {@link #isHintLabel(String) isHintLabel(b.getText())}
	 *              or {@link #isHintButton(Component) isHintButton(b)}
	 */
	public static boolean isHintButton(JButton b) {
		if (b instanceof HintJButton)
			return true;
		return false;
	}

	/**
	 * Tell whether a Component is a hint button.
	 * @param c
	 * @return true if {@link #isHintLabel(String) isHintLabel(c.getName())}
	 *              or {@link Component#getName()} startsWith {@value #HINT} or {@value #HELP},
	 *                 ignoring case
	 */
	public static boolean isHintComponent(Component c) {
		if (c == null)
			return false;
		if (c instanceof JButton && isHintButton((JButton) c))
			return true;
		String name = c.getName();
		if (isHintLabel(name))
			return true;
		if (name == null)
			return false;
		name = name.toLowerCase();
		if (name.startsWith(hint))
			return true;
		if (name.startsWith(help))
			return true;
		return false;
	}
}
