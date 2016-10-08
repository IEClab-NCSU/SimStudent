/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import javax.swing.table.DefaultTableColumnModel;

import edu.cmu.pact.Utilities.trace;


/**
 * Override {@link DefaultTableColumnModel#moveColumn(int, int)} to prevent moves.
 */
public class ImmovableColumnColumnModel extends DefaultTableColumnModel {
	
	private static final long serialVersionUID = 201402211706L;

	public ImmovableColumnColumnModel() {
		super();
	}
	
	/**
	 * No-op to prevent user from moving columns.
	 * @param columnIndex
	 * @param newIndex
	 * @see javax.swing.table.DefaultTableColumnModel#moveColumn(int, int)
	 */
	public void moveColumn(int columnIndex, int newIndex) {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "!* moveColumn("+columnIndex+","+newIndex+")");
		// super.moveColumn(columnIndex, newIndex);
	}
}
