package edu.cmu.old_pact.cmu.spreadsheet;

public interface Focusable {
	boolean hasFocus();
	void requestFocus();
	void writeToCell();
	int[] getPosition();
	boolean isEditable();
	Gridable getGridable();
	String getName();
}