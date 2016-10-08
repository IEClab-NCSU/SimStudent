package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Label;
import java.awt.Panel;

public class PlainEquationPanel extends StandardEquationPanel {
	protected String leftExpression;
	protected String rightExpression;
	
	public PlainEquationPanel(PanelParameters parms,boolean typein) {
		super(parms,typein);
	}
	
	public void setEquation(String left, String right) {
		removeAll();
		leftExpression = left;
		rightExpression = right;
		add(makeLabel(left+" = "+right));
//		add(makeLabel("="));
//		add(makeLabel(right));
	}
	
	public void alignWith(Panel thePanel) {
	};
	
	public void replaceButtonWithEquation(String side,String expression) {
		int compNum;
		if (side.equalsIgnoreCase("left"))
			compNum = 0;
		else
			compNum = 2;
		remove(compNum);
		add(makeLabel(expression),compNum);
	}
	
	private Label makeLabel(String text) {
		Label theLabel = new Label(text);
		theLabel.setForeground(myForeColor);
		theLabel.setBackground(myBackColor);
		theLabel.setFont(myFont);
		return theLabel;
	}
}
