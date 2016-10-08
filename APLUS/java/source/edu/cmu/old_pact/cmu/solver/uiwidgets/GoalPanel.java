package edu.cmu.old_pact.cmu.solver.uiwidgets;
import java.awt.FlowLayout;

import edu.cmu.old_pact.htmlPanel.AdjustableHtmlPanel;

//import java.awt.*;

public class GoalPanel extends HtmlSolverPanel {
	
	public GoalPanel(PanelParameters parms) {
		super(parms);
	}
	
	public void setInstructions(String instructions) {
		clear();
		htmlPanel = new AdjustableHtmlPanel(myWidth-widthBuffer,myHeight,AdjustableHtmlPanel.ADJUST_BOTH);
		htmlPanel.setBorder(true);
		htmlPanel.setBgColor(myBackColor);
		//htmlPanel = new HtmlPanel(200,50,myBackColor);
		htmlPanel.setFgColor(myForeColor);
		setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		/*the sky too is folding under you
		  and it's all over now, baby blue
		  all your seasick sailors they're all rowing home
		  your own empty handed army is all going home
		  your lover who just walked out the door
		  has taken all his blankets from the floor
		  the carpet too is moving under you
		  and it's all over now, baby blue
		      -bd*/
		add("Left",htmlPanel);
		//instructions += "<br><hr>";
		//htmlPanel.setMathMLFont(myFont);
		htmlPanel.displayHtml(instructions);
		
	}
}
