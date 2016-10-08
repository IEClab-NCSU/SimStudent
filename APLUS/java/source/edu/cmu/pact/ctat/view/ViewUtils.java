package edu.cmu.pact.ctat.view;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * This class contains a bunch of methods for standard UI tasks.
 */
public class ViewUtils {

	/**
	 * Constructor to prevent instantiation.
	 */
	private ViewUtils() {}
	
	/**
	 * Set an empty border of width 10 pixels on a the given panel.
	 * @param panel component to get the border: should be JPanel or JLabel
	 * @see JComponent#setBorder(Border) for notes on the use of setBorder(): as of v1.6,
	 * the Java API documentation recommends that the component be a JPanel or JLabel
	 */
	public static void setStandardBorder(JComponent panel) {
		if (panel == null)
			return;
		Border border = BorderFactory.createEmptyBorder(10,10,10,10);
		panel.setBorder(border);
	}
}
