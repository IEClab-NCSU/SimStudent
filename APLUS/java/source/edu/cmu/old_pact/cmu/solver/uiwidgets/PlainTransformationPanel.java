package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Label;

public class PlainTransformationPanel extends TransformationPanel {
	Label myLabel=null;
	
	public PlainTransformationPanel(PanelParameters parms) {
		super(parms);
	}
	
	public void setStep(String left, String right, String op, String arg) {
		removeAll();
		if (op.equalsIgnoreCase("subtract"))
			myLabel = new Label("-"+arg+"   "+"-"+arg);
		else if (op.equalsIgnoreCase("add"))
			myLabel = new Label("+"+arg+"   "+"+"+arg);
		else if (op.equalsIgnoreCase("multiply"))
			myLabel = new Label(left+"*"+arg+"   "+right+"*"+arg);
		else if (op.equalsIgnoreCase("divide"))
			myLabel = new Label("("+left+")/"+arg+"   "+"("+right+")/"+arg);
		else if (!arg.equals(""))
			myLabel = new Label(op+" on "+arg);
		else
			myLabel = new Label(op);
		myLabel.setFont(myFont);
		myLabel.setForeground(myForeColor);
		myLabel.setBackground(myBackColor);
		add("Center",myLabel);
	}
	
	public void setColor(Color theColor) {
		if (myLabel != null)
			myLabel.setForeground(theColor);
		myForeColor = theColor;
	}
	
	public void setFont(Font theFont) {
		if (myLabel != null)
			myLabel.setFont(theFont);
		myFont = theFont;
	}
	
	public void displayCompletionMessage(){
		myLabel = new Label("Equation has been solved.");
		myLabel.setFont(myFont);
		myLabel.setForeground(myForeColor);
		myLabel.setBackground(myBackColor);
		add("Center",myLabel);
	}
}
