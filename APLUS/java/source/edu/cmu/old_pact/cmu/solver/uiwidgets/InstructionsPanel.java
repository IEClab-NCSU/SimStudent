package edu.cmu.old_pact.cmu.solver.uiwidgets;
import edu.cmu.old_pact.htmlPanel.HtmlPanel;

//import java.awt.*;

public class InstructionsPanel extends HtmlSolverPanel {

	public InstructionsPanel(PanelParameters parms) {
		super(parms);
	}
	
	public void setInstructions(String instructions) {
		clear();
		htmlPanel = new HtmlPanel(myWidth-widthBuffer,myHeight,myBackColor);
		//htmlPanel = new HtmlPanel(200,50,myBackColor);
		htmlPanel.setFgColor(myForeColor);
		add("Center",htmlPanel);
		htmlPanel.displayHtml(instructions);
		
	}
}
