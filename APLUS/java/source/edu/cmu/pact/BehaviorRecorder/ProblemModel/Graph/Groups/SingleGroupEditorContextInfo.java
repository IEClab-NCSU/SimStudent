package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.awt.Color;
import java.io.Serializable;

public class SingleGroupEditorContextInfo implements Serializable {
	public boolean isHovered;
	public boolean isDisplayedOnGraph;
	public boolean isExpanded;
	public Color color;
	
	public static int nextColor = 0;
	//List of possible group colors
	public static Color[] colors = new Color[]{new Color(226, 229, 159),
											   new Color(223, 212, 219),
											   new Color(249, 223, 121),
											   new Color(204, 229, 162),
											   new Color(167, 230, 196),
											   new Color(189, 208, 238),
											   new Color(228, 210, 231),
											   new Color(235, 198, 211)};
	
	public SingleGroupEditorContextInfo() {
		isHovered = false;
		isDisplayedOnGraph = true;
		isExpanded = false;
		color = colors[nextColor++];
		nextColor %= colors.length;
	}
}
