package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

public class EditContextEvent {
	public static final int EXPANSION = 1;
	public static final int SHOWNONGRAPH = 2;
	public static final int OTHER = 3;
	private int type;
	public EditContextEvent(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
}
