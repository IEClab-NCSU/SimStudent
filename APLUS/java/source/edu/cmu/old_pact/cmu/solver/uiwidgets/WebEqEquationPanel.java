package edu.cmu.old_pact.cmu.solver.uiwidgets;
import java.awt.Panel;

import webeq3.fonts.FontBroker;
import webeq3.parser.Parser;
import webeq3.util.ErrorHandler;
import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;


public class WebEqEquationPanel extends StandardEquationPanel {
	protected String leftSide;
	protected String rightSide;
	protected String leftMathML;
	protected String rightMathML;
	protected String leftTypein;
	protected String rightTypein;
	protected SymbolManipulator sm;
	protected SolverPEquation myEquation;
	protected webeq3.app.Handler myHandler;
	private static ErrorHandler err = new ErrorHandler();
    private static Parser theParser = new webeq3.parser.mathml.mathml();
    
	static {
		//initialize fonts
	    FontBroker.initialize(true);	    
		theParser.init(new webeq3.app.Handler()); //why do I need to pass a handler here? Does it matter which one?
	}
	
	public WebEqEquationPanel(PanelParameters parms,boolean typein) {
		super(parms,typein);
		sm = new SymbolManipulator();
		sm.setOutputType(SymbolManipulator.mathMLOutput);
	}
	
	public void clear(){
		super.clear();
		removeAll();
		//sm = null;
		myHandler = null;
    	myEquation = null;
    }
		
	
	public void alignWith(Panel thePanel) { //forget about alignment, for now...
	};


	public void setEquation(String left, String right) {
		leftSide = left;
		rightSide = right;
		
		if (getParent() == null)
			throw new UninitializedError("can't set equation on WebEQEquationPanel until panel is added to container");

		//parse the left and right sides into MathML
		//System.out.println("left side is "+left);

		try {
			leftMathML = sm.noOp(left);
			rightMathML = sm.noOp(right);
			//sm = null;
		}
		catch (BadExpressionError err) {
			//hmm -- should be a better way to deal with unparsable equations...
			//System.out.println("Can't parse equation: "+err);
			//leftMathML = "<mtext>bad</mtext>";
			//rightMathML = "<mtext>equation</mtext>";
			String solverDisplay = "bad equation";
			String dialogDisplay = left+"="+right;
			if(right.equalsIgnoreCase("?")){
				solverDisplay = "Must provide equation";
				dialogDisplay = left;
			}
			leftMathML = "<mtext>"+solverDisplay+" : "+left+"</mtext>";
			rightMathML = "<mtext>"+right+"</mtext>";
			/*EquationDialog dlog = new EquationDialog(SolverFrame.getSelf(),"Enter equation:",
			  true,"new",dialogDisplay);
			  dlog.setVisible(true);
			  dlog.toFront();*/
		}
/*
leftMathML = "<mfrac><mrow> <mn>3</mn> <mi>x</mi> </mrow><mfrac><mfrac><mn>4</mn> <mn>3</mn></mfrac><mn>4</mn></mfrac></mfrac>";
rightMathML = "<mfrac><mfrac><mn>5</mn><mn>3</mn></mfrac><mn>4</mn></mfrac>";
//rightMathML = "<mfrac><mn>5</mn><mfrac><mn>3</mn><mn>4</mn></mfrac></mfrac>";
*/		
		//create the PEquation
		myHandler = new webeq3.app.Handler();
    	myEquation = new SolverPEquation(myHandler);
    	myHandler.setParameters(myEquation,(String [])null);
		
		String equationML = WebEqHelper.getEquationML(leftMathML,rightMathML,myFont,myForeColor);
//System.out.println("|| in WebEqEquationPanel equationML = "+equationML);
		try{
			theParser.parse(equationML, "", myEquation.root, err);
		}
		catch(Exception e) {
			System.out.println("Error parsing: "+e);
			e.printStackTrace();
   		}
		this.add(myEquation);
		
   		//Set parameters for webEQ (this has to be done *after* the equation is added to the panels &
   		//the panels are added to the applet

		WebEqHelper.refreshEquation(myEquation,myFont.getSize());
	    System.gc();
	}

/*
	public void paint(Graphics g) {
	 	System.out.println("myEquation = "+myEquation);
	 	if(myEquation != null)
	 	   System.out.println("getSize = "+getSize()+" myEquation.getSize() = "+myEquation.getSize());
	 	if(myEquation != null && getSize().width < myEquation.getSize().width){
	 		LayoutManager layout=getLayout();
 			if (layout!=null) {
	 			Dimension newsize=layout.preferredLayoutSize(this);
 				if (myWidth < newsize.width) {
 					parms.setWidth(newsize.width);
 					setSize(newsize.width, getSize().height);
 					layout.layoutContainer(this);
 					WebEqHelper.refreshEquation(myEquation,myHandler,newsize.width,myHeight,myFont.getSize());
 				}
 			}
	   }	
	   super.paint(g);
	}	
*/	

	protected void replaceButtonWithEquation(String side, String exp) {
		int otherSideStatus;
		if (side.equalsIgnoreCase("left")) {
			leftTypeinStatus = TYPEIN_CONFIRMED;
			otherSideStatus = rightTypeinStatus;
		leftTypein = exp;
		}
		else {
			rightTypeinStatus = TYPEIN_CONFIRMED;
			otherSideStatus = leftTypeinStatus;
		rightTypein = exp;
		}
		
		if (otherSideStatus == TYPEIN_CONFIRMED || otherSideStatus == TYPEIN_OK) { //TYPEIN_OK shouldn't happen, but just in case...
			removeAll(); //remove everything from equation panel
			setEquation(leftTypein,rightTypein); //add new equation, using user's entries
			setNextEquation(leftTypein+"="+rightTypein);
			SolverFrame.getSelf().enableMenuOperations();
//////			setInstructions("");
		}
		else {
			int componentNum;
			String sideExpression;
			if (side.equalsIgnoreCase("left")) {
				componentNum = 0;
			}
			else {
				componentNum = 2;
			}
			remove(componentNum);
			
			webeq3.app.Handler theHandler = new webeq3.app.Handler();
			synchronized(theHandler){
				SolverPEquation expression = new SolverPEquation(theHandler);
				theHandler.setParameters(expression,(String [])null);
					//WebEqHelper.makePEquation(exp,theHandler,myForeColor);
				try{
					String exprML = WebEqHelper.getExpressionML(sm.noOp(exp),myFont,myForeColor);
					theParser.parse(exprML,"",expression.root,err);
				}
				catch(Exception e){
					System.out.println("Error parsing: " + e);
				}

				add(expression,componentNum);
			
				int thirdWidth = myWidth/3;
				//WebEqHelper.refreshEquation(expression,theHandler,thirdWidth,25,myFont.getSize());
				WebEqHelper.refreshEquation(expression,myFont.getSize());
			}
////		if (side.equalsIgnoreCase("left"))
////			setInstructions("Click on right to enter the new right side.");
////		else
////			setInstructions("Click on left to enter the new left side.");
		
		}
	}
	
}
