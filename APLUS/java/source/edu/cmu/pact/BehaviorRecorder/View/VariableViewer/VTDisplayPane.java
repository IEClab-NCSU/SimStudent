package edu.cmu.pact.BehaviorRecorder.View.VariableViewer;

import javax.swing.JScrollPane;

public abstract class VTDisplayPane extends JScrollPane {
	private int instance;
	private String type;
	
	public abstract int getInstance();
	
	public abstract String getType();
}
