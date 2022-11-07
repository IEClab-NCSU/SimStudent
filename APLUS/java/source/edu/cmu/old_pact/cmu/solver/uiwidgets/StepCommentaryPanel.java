package edu.cmu.old_pact.cmu.solver.uiwidgets;
import java.awt.BorderLayout;
import java.awt.Color;

import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.htmlPanel.HtmlPanel;

public class StepCommentaryPanel extends HtmlSolverPanel {

	public StepCommentaryPanel(PanelParameters parms) {
		super(parms);
		setLayout(new BorderLayout());
	}
	
	public void setStepComment(String comment) {
		removeAll();
			//create HtmlPanel without scroll bar
		htmlPanel = new HtmlPanel(275,55,myBackColor);
		htmlPanel.setFgColor(myForeColor);
		add("Center",htmlPanel);
		htmlPanel.displayHtml(comment);
		setSize(275,55);
	}
	
	public void setColor(Color newColor) {
		setForeColor(newColor);
		if(htmlPanel != null) {
			htmlPanel.setFgColor(newColor);
		}
	}
	
	public void setStep(String left,String right,String op,String arg) {
		//trace.out("SCP.sS: " + op);

		// add expression tag around arg to display in the html panel
	    String argexpr = "<expression>"+arg+"</expression>";

		/*be a bit more verbose for operations on a side*/
		if(arg != null){
			if(arg.equalsIgnoreCase("left")){
				arg = "the left side";
			}
			else if(arg.equalsIgnoreCase("right")){
				arg = "the right side";
			}
			else if(arg.equalsIgnoreCase("both")){
				arg = "both sides";
			}
		}
	   
		if (op.equals("subtract"))
			setStepComment("Subtract "+argexpr+" from both sides");
		else if (op.equals("add"))
			setStepComment("Add "+argexpr+" to both sides");
		else if (op.equals("multiply"))
			setStepComment("Multiply both sides by "+argexpr);
		else if (op.equals("divide"))
			setStepComment("Divide both sides by "+argexpr);
		else if (op.equals("squareroot"))
			setStepComment("Take positive square root of both sides");
		// ALLEN
		else if (op.equals("cm"))
			setStepComment("Cross Multiply");
		// end ALLEN
		else if (op.equals("distribute") &&
				 !SolverFrame.getSelf().getTypeInMode())
			setStepComment("Distribute on "+arg);
		else if(op.equals("Factor")){
			setStepComment("Factor out " + argexpr);
		}
		else if(op.equals("Substitute Constants")){
			setStepComment("Substitute constants");
		}
		/*no comment for done ("you are not done" is a bug message now)*/
		else if (op.equalsIgnoreCase("done") ||
				op.equalsIgnoreCase("DoneNoSolution") ||
				op.equalsIgnoreCase("DoneInfiniteSolutions"))
			//setStepComment("You are not done");
			;
		else{
			String comment;
			if(arg == null){
				comment = op;
			}
			else{
 				comment = op + " on " + arg;
			}
			setStepComment(comment);
		}
	}
}
