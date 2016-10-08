package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;

public class GroupEditor extends JPanel {
	private static final long serialVersionUID = 5396443650424885843L;
	private CTAT_Launcher server;
	private AddGroupUI groupCreator;
	private GroupTree groupEditor;

	public GroupEditor(CTAT_Launcher server) {
    	this.server = server;
    	this.groupCreator = new AddGroupUI(this.server);
    	this.groupEditor = new GroupTree(this.server);
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	this.add(this.groupEditor);
    	this.add(this.groupCreator);
		refresh();
	}

	public void refresh() {
		this.groupCreator.refresh();
		this.groupEditor.refresh();
	}

}
