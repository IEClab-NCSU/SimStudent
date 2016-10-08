package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.io.Serializable;

public class SingleLinkEditorContextInfo implements Serializable {
	public boolean isSelected;
	public boolean isHovered;
	
	public SingleLinkEditorContextInfo() {
		isSelected = false;
		isHovered = false;
	}
}
