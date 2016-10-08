package edu.cmu.old_pact.cmu.solver.uiwidgets;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Label;

import webeq3.constants.AttributeConstants;
import webeq3.parser.Parser;
import webeq3.util.ErrorHandler;
import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;

public class WebEqTransformationPanel extends TransformationPanel {
	private SolverPEquation transformEquation=null;
	private webeq3.app.Handler transformHandler;
    private static Parser theParser = new webeq3.parser.mathml.mathml();
	private static ErrorHandler err = new ErrorHandler();
	private SymbolManipulator sm;
	
	static {
		theParser.init(new webeq3.app.Handler());
	}

	public WebEqTransformationPanel(PanelParameters parms) {
		super(parms);
		sm = new SymbolManipulator();
		setForeground(myForeColor);
	}
	
	public void setStep(String left, String right, String op, String arg) {
		if (op.equalsIgnoreCase("add") ||
			op.equalsIgnoreCase("subtract"))
			setAddOrSubtractStep(left,right,op,arg);
		else if (op.equalsIgnoreCase("multiply"))
			setMultiplyStep(left,right,op,arg);
		else if (op.equalsIgnoreCase("divide"))
			setDivideStep(left,right,op,arg);
		else if (op.equalsIgnoreCase("squareroot"))
			setRootStep(left,right,"root", "2");
		// ALLEN
		else if (op.equalsIgnoreCase("cm")) {
			if (arg != "noOp") {
				setCrossMultiplyStep(left,right,op,arg);
			}
			else {
				Label lab = new Label("Cross multiply");
				lab.setFont(new Font("helvetica",Font.BOLD,10));
				add("Center",lab);
			}
		}
		// end ALLEN
		/*these operations only have a textual description; we can't
          really depict the operation symbolically*/
		else if ((op.equalsIgnoreCase("distribute") ||
				  op.equalsIgnoreCase("factor") ||
				  op.equalsIgnoreCase("combine like terms") ||
				  op.equalsIgnoreCase("perform multiplication") ||
				  op.equalsIgnoreCase("reduce fractions") ||
				  op.equalsIgnoreCase("substitute constants"))){
			/*&&
			  !SolverFrame.getSelf().getTypeInMode()){*/
			setInvisibleStep(left,right,op,arg);
		}
		else if(op.equalsIgnoreCase("done") ||
				op.equalsIgnoreCase("DoneNoSolution") ||
				op.equalsIgnoreCase("DoneInfiniteSolutions"))
			;//don't display anything until we hear from the tutor
		else{
			/*we should really never get down here: if an op doesn't
              have a symbolic representation, it should be listed
              above to use setInvisibleStep*/
			Label lab;
			if(arg == null){
				lab = new Label(op);
			}
			else{
				lab = new Label(op+" on "+arg);
			}
			lab.setFont(new Font("helvetica",Font.BOLD,10));
			add("Center",lab);
		}
		//sm = null;
		System.gc();
	}
	
	public void setColor(Color theColor) {
		myForeColor = theColor;
		if (transformEquation != null) {
			String col = "#"+WebEqHelper.getWebColor(theColor);
			WebEqHelper.setChildAttributes(transformEquation.root,AttributeConstants.COLOR,col);
			transformEquation.directRedraw();  //////was redraw
		}
		setForeground(myForeColor);
		this.repaint();
	}
	
	public void setFont(Font theFont) {
		myFont = theFont;
		if (transformEquation != null) {
			WebEqHelper.setChildAttributes(transformEquation.root,AttributeConstants.FONTFAMILY,theFont.getFamily());
			transformEquation.directRedraw();  //////was redraw
		}
	}
	
	public void displayCompletionMessage(){
		removeAll();
		add("Center",new Label("Equation has been solved."));
	}

	public void clear() {
		super.clear();
		removeAll();
		//sm = null;
		transformHandler = null;
    	transformEquation = null;
	}

	private void addEquationToPanel(String equationML) {
		if (getParent() == null)
			throw new UninitializedError("can't set equation on WebEQTransformationPanel until panel is added to container");

		transformHandler = new webeq3.app.Handler();
    	transformEquation = new SolverPEquation(transformHandler);
    	transformHandler.setParameters(transformEquation,(String [])null);

		try{
			theParser.parse(equationML, "", transformEquation.root,err);
		}
		catch(Exception e) {
			System.out.println("Error parsing: "+e);
			e.printStackTrace();
   		}		
		add("Center",transformEquation);
		
		WebEqHelper.refreshEquation(transformEquation, myFont.getSize());
	}				 
	
	private void setAddOrSubtractStep(String left,String right,String op,String arg) {
		String[] leftSides = WebEqHelper.getAlignedExpressions(arg,left,op);
		String[] rightSides = WebEqHelper.getAlignedExpressions(arg,right,op);
		//System.out.println("in WETP leftSides = "+leftSides[0]+"::"+leftSides[1]);
		//System.out.println("in WETP rightSides = "+rightSides[0]+"::"+rightSides[1]);
		String operatorML =  WebEqHelper.getEquationML(leftSides[0],rightSides[0],myFont,myForeColor,"<mphantom><mo>=</mo></mphantom>");
	 	String equationML =  WebEqHelper.getEquationML(leftSides[1],rightSides[1],myFont,myForeColor);
		
//		fullML = "<mstyle fontcolor='#cccc66'><mrow><mphantom><mrow> <mn>3</mn> <mi>x</mi> </mrow></mphantom><mo form=infix>-</mo><mn>4</mn></mrow><mo>=</mo><mo>-</mo><mn>4</mn></mstyle>";
		//System.out.println("---from setAddOrSubtractStep: operator ML is "+operatorML);
		//System.out.println("equation ML is "+equationML);
		
		addEquationToPanel(operatorML); //add the operator equation to the transformation panel    	
//    	resetEquation(equationML);

	}
	
	private void setRootStep(String left,String right, String action, String arg){
		sm.distributeDenominator = false;
		sm.setOutputType(SymbolManipulator.mathMLOutput);
		String equationML;
		try {
			if (Integer.parseInt(sm.runScript("length of terms",left)) > 1)
				left = "("+left+")";
			if (Integer.parseInt(sm.runScript("length of terms",right)) > 1)
				right = "("+right+")";
			if (Integer.parseInt(sm.runScript("length of terms",arg)) > 1)
				arg = "("+arg+")";
				
			String argML = sm.format(arg);
			String oldLeftML = sm.format(left);
			String oldRightML = sm.format(right);
			
			String firstWrap, lastWrap;
			if(arg.equals("2")){
				firstWrap = "<apply><root/>";
				lastWrap = "</apply>";
			}
			else{
				firstWrap = "<mroot>";
				lastWrap = "</mroot>";
			}
			
			String newLeft = firstWrap+oldLeftML+lastWrap;
			String newRight = firstWrap+oldRightML+lastWrap;
			equationML = WebEqHelper.getEquationML(newLeft,newRight,myFont,myForeColor);
//System.out.println("in setRootStep	equationML = "+equationML);		
		}
		catch (BadExpressionError err) {
			equationML = "<mtext>"+action+" of "+arg+"</mtext>";
			System.out.println("ERROR parsing arg or equation: "+left+":"+right+":"+arg);
		}
		catch (NoSuchFieldException err) {
			equationML = "<mtext>"+action+" of "+arg+"</mtext>";
			System.out.println("ERROR getting terms: "+left+":"+right+":"+arg);
		}

		addEquationToPanel(equationML);
	}
	
	// ALLEN
	private void setCrossMultiplyStep(String left,String right,String action,String arg) {
		sm.distributeDenominator = false;
		sm.setOutputType(SymbolManipulator.mathMLOutput);
		
		String equationML = crossMultiplyMLStr(left, right, action,  arg);
		addEquationToPanel(equationML);
	}
	
	private String crossMultiplyMLStr(String left,String right,String action, String arg) {

		String equationML;
		String newLeft = null;
		String newRight = null;
		if (arg == "noOp") {
			newLeft = left;
			newRight = right;
		}
		else {
			try {
				sm.setOutputType(SymbolManipulator.asciiOutput);
				String numLeft = sm.runScript("numerator",left);
				sm.setOutputType(SymbolManipulator.mathMLOutput);
				if (Integer.parseInt(sm.runScript("length of terms",numLeft)) > 1)
					numLeft = "("+numLeft+")";
					
				sm.setOutputType(SymbolManipulator.asciiOutput);
				String numRight = sm.runScript("numerator",right);
				sm.setOutputType(SymbolManipulator.mathMLOutput);
				if (Integer.parseInt(sm.runScript("length of terms",numRight)) > 1)
					numRight = "("+numRight+")";

				sm.setOutputType(SymbolManipulator.asciiOutput);
				String denLeft = sm.runScript("denominator",left);
				sm.setOutputType(SymbolManipulator.mathMLOutput);
				if (Integer.parseInt(sm.runScript("length of terms",denLeft)) > 1)
					denLeft = "("+denLeft+")";

				sm.setOutputType(SymbolManipulator.asciiOutput);				
				String denRight = sm.runScript("denominator",right);
				sm.setOutputType(SymbolManipulator.mathMLOutput);
				if (Integer.parseInt(sm.runScript("length of terms",denRight)) > 1)
					denRight = "("+denRight+")";
				
				String opML = "<mo>&cdot;</mo>";
				
				newLeft = sm.format(numLeft)+opML+sm.format(denRight);
				newRight = sm.format(denLeft)+opML+sm.format(numRight);
			}
			catch (BadExpressionError err) {
				equationML = "<mtext>"+action+" by "+arg+"</mtext>";
				System.out.println("ERROR parsing arg or equation: "+left+":"+right+":"+arg);
			}
			catch (NoSuchFieldException err) {
				equationML = "<mtext>"+action+" by "+arg+"</mtext>";
				System.out.println("ERROR getting terms: "+left+":"+right+":"+arg);
			}
		}
		equationML = WebEqHelper.getEquationML(newLeft,newRight,myFont,myForeColor);
		return equationML;
	}
	// end ALLEN

	
	/*use this when you need the equation to be there to take up
      space, but don't want it to actually show up*/
	private void setInvisibleStep(String left,String right,String action, String arg) {
		sm.setOutputType(SymbolManipulator.mathMLOutput);
		String equationML = sm.format(left)+"="+sm.format(right);
		equationML = WebEqHelper.getEquationML(sm.format(left),sm.format(right),myFont,myForeColor);
		equationML = "<mphantom>" + equationML + "</mphantom>";
		addEquationToPanel(equationML);
	}
	

	private void setMultiplyStep(String left,String right,String action, String arg) {
		sm.distributeDenominator = false;
		sm.setOutputType(SymbolManipulator.mathMLOutput);
		
		String equationML = multiplyMLStr(left, right, action,  arg);
		addEquationToPanel(equationML);
	}
	
	private String reverseArg(String arg){
		int ind = arg.indexOf("/");
		if(ind == -1)
			return null;
		if(ind == 0 || ind == (arg.length()-1)) return null;
		
		/*String toret = arg.substring(ind+1)+"/"+arg.substring(0,ind);
		  return toret;*/
		sm.setOutputType(SymbolManipulator.asciiOutput);				
		String ret = null;
		try{
			ret = sm.reciprocal(arg);
			if(sm.complexity(ret) > sm.complexity(arg)){
				/*multiplying by the reciprocal of "(a+b)/(c+d)" isn't
                  any clearer than dividing by it*/
				ret = null;
			}
		}
		catch(BadExpressionError bee){;}
//		finally{
			sm.setOutputType(SymbolManipulator.mathMLOutput);
			return ret;
//		}
	}
	
	private String multiplyMLStr(String left,String right,String action, String arg) {
		String equationML;
		try {
			if (Integer.parseInt(sm.runScript("length of terms",left)) > 1)
				left = "("+left+")";
			if (Integer.parseInt(sm.runScript("length of terms",right)) > 1)
				right = "("+right+")";
			if (Integer.parseInt(sm.runScript("length of terms",arg)) > 1)
				arg = "("+arg+")";
			String opML;
			opML = "<mo>&cdot;</mo>";
			
			String argML = sm.format(arg);
			String oldLeftML = sm.format(left);
			String oldRightML = sm.format(right);
			String newLeft = oldLeftML+opML+argML;
			String newRight = oldRightML+opML+argML;
			equationML = WebEqHelper.getEquationML(newLeft,newRight,myFont,myForeColor);
//System.out.println("in WETP equationML (MULTIPLY) = "+equationML);
		}
		catch (BadExpressionError err) {
			equationML = "<mtext>"+action+" by "+arg+"</mtext>";
			System.out.println("ERROR parsing arg or equation: "+left+":"+right+":"+arg);
		}
		catch (NoSuchFieldException err) {
			equationML = "<mtext>"+action+" by "+arg+"</mtext>";
			System.out.println("ERROR getting terms: "+left+":"+right+":"+arg);
		}
		return equationML;
	}
	

	private void setDivideStep(String left,String right,String action, String arg) {
		sm.distributeDenominator = false;
		sm.setOutputType(SymbolManipulator.mathMLOutput);
		String equationML;
		
		String fractionStr = reverseArg(arg);
		//System.out.println("WETP.sDS: reverseArg(" + arg + ") = " + fractionStr);
			//add <expression> tag around arg - to be handled by HtmlPanel
		String displayArg = "<expression>"+arg+"</expression>";
		String displayFraction = "<expression>"+fractionStr+"</expression>";
		
		if(fractionStr != null) {
			equationML = multiplyMLStr(left,right,action, fractionStr);
			String newComment = "Divide both sides by "+displayArg+
						" (which is the same as multiplying by the reciprocal, "+displayFraction+")";
			resetStepCommentary(newComment);
		}
		else{
			try {
				if (Integer.parseInt(sm.runScript("length of terms",left)) > 1)
					left = "("+left+")";
				if (Integer.parseInt(sm.runScript("length of terms",right)) > 1)
					right = "("+right+")";
				if (Integer.parseInt(sm.runScript("length of terms",arg)) > 1)
					arg = "("+arg+")";
				
			
				String argML = "<mrow>"+sm.format(arg)+"</mrow></mfrac>";
				String oldLeftML = sm.format(left);
				String oldRightML = sm.format(right);
			
				String firstWrap = "<mfrac linethickness='2'><mrow><mphantom>";
				String lastWrap = "</mphantom></mrow>";

				String newLeft = firstWrap+oldLeftML+lastWrap+argML;
				String newRight = firstWrap+oldRightML+lastWrap+argML;
				equationML = WebEqHelper.getEquationML(newLeft,newRight,myFont,myForeColor,"<mphantom><mo>=</mo></mphantom>");
			}
			catch (BadExpressionError err) {
				equationML = "<mtext>"+action+" by "+arg+"</mtext>";
				System.out.println("ERROR parsing arg or equation: "+left+":"+right+":"+arg);
			}
			catch (NoSuchFieldException err) {
				equationML = "<mtext>"+action+" by "+arg+"</mtext>";
				System.out.println("ERROR getting terms: "+left+":"+right+":"+arg);
			}
			
		} // if(fractionStr == null)
		addEquationToPanel(equationML) ;
	}
	
	protected void resetStepCommentary(String newComment){
		getEplusSPanel().resetStepCommentary(newComment);
	}
	
	EquationPlusStepPanel getEplusSPanel() { 
		Component parent = getParent();
		while (! (parent instanceof EquationPlusStepPanel)) {
			parent = parent.getParent();
		}
		return ((EquationPlusStepPanel) parent);
	}
	

//	public boolean handleEvent(Event e) {
//		if (e.target.equals(transformEquation)) {
//			return transformEquation.handleEvent(e);
//		}
//		else
//			return super.handleEvent(e);
//	}
	
}
