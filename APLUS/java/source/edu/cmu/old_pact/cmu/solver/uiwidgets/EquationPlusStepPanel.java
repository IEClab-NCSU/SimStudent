package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Panel;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.cmu.uiwidgets.LinePanel;
import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.cmu.uiwidgets.StackLayout;

//a EquationPlusStepPanel is a panel that displays
//each side of the equation and the steps

public class EquationPlusStepPanel extends Panel {

 
    private String leftASCII="";
    private String rightASCII="";
    private String myAction="";
    private String myArg="";
    private String leftTypein="left";
    private String rightTypein="right";
    
	private final static java.awt.Color backColor = edu.cmu.old_pact.cmu.uiwidgets.SolverFrame.stdColor; //chalkboard color
    //original colors
    private final static Color foreColor = new Color(200,200,100); //yellow chalk
    private final static Color flagColor = new Color(255,154,77); //orange chalk
	private final static Color typeinFlagColor = new Color(217,92,0); //darker red, because orange chalk is unreadable on a grey button
    private final static Color transformColor = new Color(159,202,223); //blue chalk
    
    //web-safe colors
    //private final static Color foreColor = new Color(204,204,102); //yellow chalk #CCCC66
    //private final static Color flagColor = new Color(255,153,102); //orange chalk #FF9966
    //private final static Color transformColor = new Color(153,204,255); //blue chalk #99CCFF

    //scratch colors
    //private final static Color foreColor = Color.black;//new Color(128,128,0); //yellow chalk
    //private final static Color flagColor = new Color(128,0,0); //orange (dark red, really) chalk
    //private final static Color transformColor = new Color(0,0,128); //blue chalk

	private final static Color commentColor = transformColor;
	private final static Color instructionColor = Color.white; //white

	private EquationPanel equationPanel; //this is the panel that holds the PEquation
	private InstructionsPanel instructionsPanel; //this is the panel that offers generic instructions on the step
	private TransformationPanel transformationPanel; //this is the panel that holds the transformation (currently empty)
	private StepCommentaryPanel stepCommentaryPanel; //this is the panel that holds the step commentary (currently empty)
	
//	private final static String mainEquationFont = "Groening Plain";
	private final static Font mainEquationFont = new Font("Comic Sans MS",Font.PLAIN,18);
	private final static Font flagFont = new Font("Comic Sans MS",Font.PLAIN,18);
	private final static Font commentFont = new Font("Georgia", Font.PLAIN, 18);
	private final static int equationPointsize = 24;
	
	private boolean commentSet = false;
	private boolean stepSet = false;
	
	private Panel contentPanel;
	private Panel topContent;
	private Panel botContent;
    
//    static {
//    	sm.setOutputType(SymbolManipulator.mathMLOutput);
//    }
    
    //the EquationPlusStepPanel holds the equation, following step and related text. The layout is as follows:
    //
    //    EquationPlusStepPanel (stack layout arranges topPanel above contentPanel
    //    --------------------------------------------------------
    //    -                 Top Panel                            -
    //    -            (adds vertical spacing)                   -
    //    - ---------------------------------------------------- -
    //    - ---------------------------------------------------- -
    //    -                         |                            -  \
    //    -  Equation Panel         |   instructions Panel       -   \
    //    -                         |                            -    \
    //    -                         |                            -      content panel (2x2 grid layout)
    //    - ----------------------- | -------------------------- -    /
    //    -                         |                            -   /
    //    -  Transformation Panel   |   Step Commentary Panel    -  /
    //    -                         |                            - /
    //    -                         |                            -
    //    --------------------------------------------------------
    
    
    
	public EquationPlusStepPanel(boolean typein) {
		super();
		
		setForeground(foreColor);
		setBackground(backColor);
		setLayout(new StackLayout(2));

		//add a panel at the top, for spacing
		Panel topPanel = new Panel();
		topPanel.setSize(getSize().width,20);
		add(topPanel);
		//PanelParameters parms = new PanelParameters(250,40,mainEquationFont,foreColor,backColor);
		//PanelParameters parms = new PanelParameters(250,44,mainEquationFont,foreColor,backColor);
//		PanelParameters parms = new PanelParameters(250,35,mainEquationFont,foreColor,backColor);
		PanelParameters parms = new PanelParameters(300,45,mainEquationFont,foreColor,backColor);
		
		equationPanel = SolverPanelFactory.makeEquationPanel(parms,typein); //this is the Equation panel
		parms.setForeColor(transformColor);
		transformationPanel = SolverPanelFactory.makeTransformationPanel(parms); //this is the transformation panel 
		parms.setForeColor(instructionColor);
		parms.setFont(commentFont);
		instructionsPanel = SolverPanelFactory.makeInstructionsPanel(parms); //this is the panel that holds the instructions
		parms.setForeColor(commentColor);
		stepCommentaryPanel = SolverPanelFactory.makeStepCommentaryPanel(parms); //this is the panel that holds the step commentary

		//do the layout
		contentPanel = new Panel(); //contentPanel holds the equation and information about the transformation
		//contentPanel.setLayout(new StackLayout(0));
		contentPanel.setLayout(new GridBagLayout());
		add(contentPanel); //add the contentPanel to the EquationPlusStepPanel
		
		Color[] col = {Color.white};
		LinePanel forLine = new LinePanel(1, col);

		//Now that we've created the panels, add them to the display
		topContent = new Panel();
		botContent = new Panel();
		
		topContent.setLayout(new FlowLayout(0));
		topContent.add(equationPanel);
		topContent.add(instructionsPanel);
		
		botContent.setLayout(new FlowLayout(0));
		botContent.add(transformationPanel);
		botContent.add(stepCommentaryPanel);

		//contentPanel.add(topContent);
		//contentPanel.add(botContent);		
		//contentPanel.add(forLine);
		GridbagCon.viewset(contentPanel, topContent, 0,0,1,1, 0,0,0,0);
		GridbagCon.viewset(contentPanel, botContent, 0,1,1,1, 0,0,0,0);
		GridbagCon.viewset(contentPanel, forLine, 0,2,1,1, 0,0,0,0);
	} 
	
	public void addEquation(String left, String right) {
		setInternalEquationString(left,right);
		equationPanel.setEquation(left,right);
		
		topContent.repaint();
		botContent.repaint();
	}
	
	public void clearAll(){
		equationPanel.clear();
		instructionsPanel.clear();
		transformationPanel.clear();
		stepCommentaryPanel.clear();
	}
	
	public void setInternalEquationString(String left, String right) {
		leftASCII = left;
		rightASCII = right;
	}
	
	public void setTypeinExpressionString(String value,String side) {
		if (side.equalsIgnoreCase("left")){ 
			leftTypein=value;
			//leftASCII = value;
		}
		else {
			rightTypein = value;
			//rightASCII = value;
		}
		equationPanel.setTypeinSideText(side.toLowerCase(),value);
	}
	
	public String leftSide() {
		return leftASCII;
	}

	public String rightSide() {
		return rightASCII;
	}
	
	public String getTypeInString(){
		return leftTypein+" = "+rightTypein;
	}

/*	
	//functions to set the left and right
	public void setLeft(String newLeft) {
		leftASCII = newLeft;
		try {
			leftMathML = sm.noOp(newLeft);
		}
		catch (BadExpressionError err) {
			//hmm -- should be a better way to deal with unparsable equations...
			trace.out("Can't parse expression: "+newLeft);
		}
		try {
			theParser.parse(getEquationML(leftMathML,rightMathML), "", myEquation.root, err);
		}
		catch(Exception e) {
			trace.out("Error parsing: "+e);
			e.printStackTrace();
   		}
	}
	
	public void setRight(String newRight) {
		rightASCII = newRight;
		try {
			rightMathML = sm.noOp(newRight);
		}
		catch (BadExpressionError err) {
			//hmm -- should be a better way to deal with unparsable equations...
			trace.out("Can't parse expression: "+newRight);
		}
		try {
			theParser.parse(getEquationML(leftMathML,rightMathML), "", myEquation.root, err);
		}
		catch(Exception e) {
			trace.out("Error parsing: "+e);
			e.printStackTrace();
   		}
	}
*/	
	
	public void clearStep() {
		if(transformationPanel != null){
			//transformationPanel.setWarning(false);
			transformationPanel.clear();
			transformationPanel.setColor(transformColor); //in case it was flagged...
			stepSet = false;
		}
		if(stepCommentaryPanel != null){
			stepCommentaryPanel.clear();
			stepCommentaryPanel.setColor(commentColor); //in case it was flagged...
		}
		myAction = "";
		myArg = "";
		SolverFrame.getSelf().scrollToBottom();
	}
	
	public boolean stepIsSet(){
		return stepSet;
	}

	public void setStep(String action, String arg, String userarg) {
		transformationPanel.setStep(leftASCII,rightASCII,action,arg);
		stepSet = true;
		if (arg == "noOp")
			commentSet = true;
		if(!commentSet)
			stepCommentaryPanel.setStep(leftASCII,rightASCII,action,userarg);
		else
			commentSet = false;
		topContent.repaint();
		botContent.repaint();
	}
	
	public void flag() {
		transformationPanel.setColor(flagColor);
		//transformationPanel.setWarning(true);
		stepCommentaryPanel.setColor(flagColor);
	}
	
	public void unflag() {
		transformationPanel.setColor(transformColor);
		stepCommentaryPanel.setColor(transformColor);
	}
	
	//flagging a side...
	//We don't actually change the visual display -- this is done in completeTypein
	public void flag(String side) {
		equationPanel.setTypeinOK(side,false);
		equationPanel.setTypeinSideColor(side,typeinFlagColor);
	}
	
	public void unflag(String side) {
		equationPanel.setTypeinOK(side,true);
		equationPanel.setTypeinSideColor(side,foreColor);
	}

	public void displayCompletionMessage(){
		unflag();
		transformationPanel.displayCompletionMessage();
	}
	
	//completeTypeinAction is called when the tutor has finished cycling on a typein entry
	//this checks the typein status of the side and changes the display to reflect the new state
	public void completeTypeinAction(String side,String input) {
		if (side.equalsIgnoreCase("left"))
			equationPanel.setTypeinSideText("left",leftTypein);
		else
			equationPanel.setTypeinSideText("right",rightTypein);
	}
	
	public void displaySide(String side, String expression){
		equationPanel.setTypeinOK(side,true);
		setTypeinExpressionString(expression, side);
	}
	
	public String getEquationString() {
		return leftSide() + " = " + rightSide();
		//return leftASCII+" = "+rightASCII;
	}
	
	public int getStepState(){
		return equationPanel.getStepState();
	}

	public void resetStepCommentary(String newComment){
		stepCommentaryPanel.setStepComment(newComment);
		commentSet = true;
		botContent.repaint();
		/*
		botContent.setSize(botContent.preferredSize());
		botContent.validate();
		contentPanel.validate();
		this.validate();
		*/
	}
	
}
	
	
	
