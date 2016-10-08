/*
 * Copyright 2006 Carnegie Mellon University.
 */
package pact.CommWidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pact.CommWidgets.event.StudentActionListener;

/**
 * Marks student interfaces that can send the Done event, which is 
 * sent as an {@link ActionEvent}.
 * @author sewall
 */
public interface SendsDone extends StudentActionListener {

	/**
	 * Add a listener interested in the Done action event.
	 * @param doneListener
	 */
	public void addActionListener(ActionListener doneListener);

	/**
	 * Remove listener no longer interested in the Done action event.
	 * @param doneListener
	 */
	public void removeActionListener(ActionListener doneListener);
}
