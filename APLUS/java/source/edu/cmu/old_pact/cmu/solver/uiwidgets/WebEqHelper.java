package edu.cmu.old_pact.cmu.solver.uiwidgets;

/**
* The WebEqHelper class is used to store routines that help when dealing with WebEq
*/

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import webeq3.parser.Parser;
import webeq3.schema.Box;
import webeq3.util.ErrorHandler;
import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

public class WebEqHelper {
	/**
	* Workaround for cutting last symbol in eq.
	*/ 
	//public static String phantom = "";
	public static final String phantom = "<msubsup><mo>&InvisibleTimes;</mo><mphantom><mi>2</mi>"+
									"</mphantom><mo>&InvisibleTimes;</mo></msubsup>";
	
	public static void refreshEquation(SolverPEquation theEquation,webeq3.app.Handler handler, 
										int width, int height, int pointsize) {
   		theEquation.initBG();
    	theEquation.setPointSize(pointsize);
   		theEquation.resize(width,height);
    	theEquation.registerControls();
    	theEquation.redraw();
	}
	
	public static void refreshEquation(SolverPEquation theEquation,int pointsize) {
   		theEquation.initBG();
    	theEquation.setPointSize(pointsize);
    	theEquation.registerControls();
    	theEquation.redraw();
	}
	
    /*the library version of toHexString does not put in leading zeros, but webeq's
      colors (and web color specs in general) need to have them, so we use this
      simple wrapper.*/
    private static String toHexStringLeadingZero(int x){
        String s = Integer.toHexString(x);

        if(s.length() == 1){
            s = "0" + s;
        }

        return s;
    }

	public static String getWebColor(Color rgbColor) {
		return toHexStringLeadingZero(rgbColor.getRed())+
                    toHexStringLeadingZero(rgbColor.getGreen())+
                    toHexStringLeadingZero(rgbColor.getBlue());
	}
	
	public static void setChildAttributes(Box root,int attNum,String value) {
		int numChildren = root.getNumChildren();
		for (int i=0;i<numChildren;++i) {
			Box child = root.getChild(i);
			//child.setAttribute(attNum,value);
			setChildAttributes(child,attNum,value);
		}
	}
	
	public static String getExpressionML(String expr,Font font,Color color){
		return "<mstyle fontfamily='"+font.getFamily()+"' fontcolor='#"+getWebColor(color)+"'>"+expr+"</mstyle>"+phantom;
	}

	public static String getEquationML(String leftML, String rightML, Font font, 
										Color color, String comparisonML) {
		return "<mstyle fontfamily='"+font.getFamily()+"' fontcolor='#"+getWebColor(color)+"'>"+leftML+comparisonML+rightML+"</mstyle>"+phantom;
	}
	
	public static String getEquationML(String leftML, String rightML, Font font, Color color) {
		return getEquationML(leftML,rightML,font,color,"<mo>=</mo>");
	}

	public static SolverPEquation makePEquation(String ascii,webeq3.app.Handler myHandler,
										Color color) {
		String mathML = ascii;
		SymbolManipulator sm = new SymbolManipulator();
		sm.setOutputType(SymbolManipulator.mathMLOutput);
		try {
			mathML = sm.format(ascii,"self","fontcolor","#"+getWebColor(color));
			mathML = mathML+phantom;
		}
		catch (BadExpressionError err) {
			//hmm -- should be a better way to deal with unparsable equations...
			System.out.println("Can't parse equation: "+err);
		}
		catch (NoSuchFieldException e) {
		}
		//create the PEquation
    	SolverPEquation theEquation = new SolverPEquation(myHandler);
    	myHandler.setParameters(theEquation,(String [])null);

		try{
			Parser theParser = new webeq3.parser.mathml.mathml();
			ErrorHandler err = new ErrorHandler();
			theParser.init(myHandler);
			theParser.parse(mathML, "", theEquation.root, err);
			theParser = null;
		}
		catch(Exception e) {
			System.out.println("Error parsing: "+e);
			e.printStackTrace();
   		}
		return theEquation;
	}
	
	//getAlignedTerm returns the position of the term in the expression that
	//matches (either exactly or in its algebraic form) the term to be added or
	//subtracted. This information is used to align the operation with the preceeding
	//expression
	private static int getAlignedTerm(String expression, String arg) {
		SymbolManipulator sm = new SymbolManipulator();
		sm.autoStandardize = true;
		String negArg;
		String result="";
		
		try {
			negArg = sm.negate(arg);
		}
		catch (BadExpressionError err) {
			System.out.println("Bad expression in getAlignedTerm: "+err);
			negArg = arg;
		}
		int foundTerm = -1;
		try {
			Queryable[] terms;
			terms = sm.runArrayScript("terms",expression);
			for (int i=0;i<terms.length && foundTerm==-1;++i) {
				String thisTerm = terms[i].getStringValue();
				if (sm.algebraicEqual(thisTerm,arg) || sm.algebraicEqual(thisTerm,negArg))
					foundTerm = i;
			}
			//if not found, match against a term that is of the same form
			if (foundTerm == -1) {
				for (int i=0;i<terms.length && foundTerm==-1;++i) {
					String thisTerm = terms[i].getStringValue();
					if (sm.patternMatches(thisTerm,arg) || sm.patternMatches(thisTerm,negArg))
						foundTerm = i;
				}
			}
			//if still not found, line up argument based on whether or not it is a number
			//(form match doesn't catch match b/w fraction and integer)
			if (foundTerm == -1) {
				for (int i=0;i<terms.length && foundTerm==-1;++i) {
					String thisTerm = terms[i].getStringValue();
					boolean argIsNum = sm.isNumber(arg);
					if (sm.isNumber(thisTerm) && argIsNum)
						foundTerm = i;
					else if (!sm.isNumber(thisTerm) && !argIsNum)
						foundTerm = i;
				}
			}
			sm = null;
			
		}
		catch (BadExpressionError err) {
			System.out.println("Bad expression in getAlignedTerm: "+err);
		}
		catch (NoSuchFieldException err) {
			System.out.println("can't get terms of "+expression);
		}

		if (foundTerm == -1)
			return 0; //if we don't find anything, align with the first term
		else
			return foundTerm;
	}
	

	public static String[] getAlignedExpressions(String arg, String expression, String op) {
		int foundTerm = getAlignedTerm(expression,arg);
		int argLength;
		String argSuffix="";
		String expressionTerm;
		int expressionTermLength;
		String expressionTermPrefix="";
		SymbolManipulator sm = new SymbolManipulator();
		String originalExpression="";
		String alignedOperatorExpression="";
		String argSource = arg;
		
		if (op.equals("subtract"))
			argSource = "-"+argSource;
		
		try {
			int numTerms = Integer.parseInt(sm.runScript("length of terms",expression));
			
			//Construct prefixes and suffixes if the argument and aligned terms are different lengths
			argLength = arg.length()+1; //add 1 for the operator
			if (numTerms == 1)
				expressionTerm = expression;
			else
				expressionTerm = sm.runScript("item "+(foundTerm+1)+" of terms",expression);
			expressionTermLength = expressionTerm.length();
			if (foundTerm > 0)
				expressionTermLength+=1; //add 1, since operator is already there
			if (argLength > expressionTermLength) 
				expressionTermPrefix = "<mphantom><mo>"+argSource.substring(0,argLength-expressionTermLength)+"</mo></mphantom>";
			
			else if (expressionTermLength > argLength)
				argSuffix = "<mphantom>"+expressionTerm.substring(expressionTermLength-argLength,expressionTermLength-argLength)+"</mphantom>";
			
			//reconstruct the MathML for the original expression
			sm.setOutputType(SymbolManipulator.mathMLOutput);
			if (numTerms > 1) {
//				System.out.println("1 case, prefix: "+expressionTermPrefix);
				if (foundTerm == 0)
					originalExpression = sm.runScript("[set] ['prefix'] [term 1] ['"+expressionTermPrefix+"']",
												  		expression);
				else
					originalExpression = sm.runScript("[set] ['prefix'] [operator "+foundTerm+"] ['"+expressionTermPrefix+"']",
												  		expression);
			}
			else {
//				System.out.println("2 case, prefix: "+expressionTermPrefix);
				originalExpression = expressionTermPrefix+sm.format(expression);
			}
//			System.out.println("OrigEx: "+originalExpression);
			sm.setOutputType(SymbolManipulator.intermediateOutput);
			
	 		//set up the alignedOperatorExpression (the expression that represents the operator)
	 		alignedOperatorExpression = expression;
			//System.out.println("WEH.gAE[1]: aOE = " + alignedOperatorExpression);
			//replace argument (if it is different)
			if (numTerms == 1){
				alignedOperatorExpression = arg; //since there's only 1 term, just replace it with arg
				//System.out.println("WEH.gAE[2]: aOE = " + alignedOperatorExpression);
			}
			
	
			else /*if (!expressionTerm.equals(arg))*/{
				alignedOperatorExpression = sm.runScript("[set] ['term "+(foundTerm+1)+"'] [self] ['"+arg+"']",
										  					alignedOperatorExpression);
				//System.out.println("WEH.gAE[3]: aOE = " + alignedOperatorExpression);
			}
			
	
			if (op.equals("subtract")) {
				/*at this point, we have the term we're subtracting in
                  place in the original exrpession.  Now we need to
                  insert the '-' for the subtraction.*/
				if (numTerms == 1){
					alignedOperatorExpression = "-"+alignedOperatorExpression;
					//System.out.println("WEH.gAE[4]: aOE = " + alignedOperatorExpression);
				}
				else {
					for(int i=0; i< numTerms; i++){
						if(i != foundTerm) {
							alignedOperatorExpression = sm.runScript("[set] ['operator'] [item "+(i+1)+" of terms] ['+']",
										  						alignedOperatorExpression);
							//System.out.println("WEH.gAE[7]: aOE = " + alignedOperatorExpression);
						}
						else if (i == foundTerm){
							alignedOperatorExpression = sm.runScript("[set] ['operator'] [item "+(foundTerm+1)+" of terms] ['-']",
																	 alignedOperatorExpression);
							//System.out.println("WEH.gAE[6]: aOE = " + alignedOperatorExpression);
						}
					}
				}
					
			}
			if (op.equals("add")) {
				if (numTerms > 1) 
					alignedOperatorExpression = sm.runScript("[set] ['operator'] [item "+(foundTerm+1)+" of terms] ['+']",
															alignedOperatorExpression);
			}
//			System.out.println("Aligning "+arg+" with "+expression+" before IE: "+alignedOperatorExpression);
			alignedOperatorExpression = invisibleExcept(alignedOperatorExpression,foundTerm,numTerms,argSuffix);
//			System.out.println("Aligning "+arg+" with "+expression+" after IE: "+alignedOperatorExpression);

			if (foundTerm == 0 && op.equals("add"))  //if this is the first term and it is positive, make the operator explicit
				alignedOperatorExpression = "<mo>+</mo>"+alignedOperatorExpression;
		}
		catch (BadExpressionError err) {
			System.out.println("Bad expression in getAlignedExpressions: "+err);
		}
		catch (NoSuchFieldException err) {
			System.out.println("can't get terms of "+expression);
		}
		sm = null;
		return new String[] {alignedOperatorExpression,originalExpression};
	}

	private static String invisibleExcept(String expression, int termNum, int totalTerms,String suffix) {
		int numAccessors = totalTerms-1;
		//add in accessors for the operator
		if (termNum == 0)
			numAccessors += totalTerms-1;
		else
			numAccessors += totalTerms-2;
		if (suffix.length() > 0)
			numAccessors+=1;
		String accessor[] = new String[numAccessors];
		String attribute[] = new String[numAccessors];
		String value[] = new String[numAccessors];
		SymbolManipulator formatter = new SymbolManipulator();
		formatter.setOutputType(SymbolManipulator.mathMLOutput);
		formatter.allowDoubleSigns = true;
		int accessNum=0;
		for (int i=0;i<totalTerms;++i) {
			if (i == termNum && suffix.length() > 0) {
				accessor[accessNum] = "item "+(i+1)+" of terms";
				attribute[accessNum] = "suffix";
				value[accessNum] = suffix;
				accessNum++;
			}
			else if (i != termNum) {
				accessor[accessNum] = "item "+(i+1)+" of terms";
				//accessor[accessNum] = "absolute value of item "+(i+1)+" of terms";
				attribute[accessNum] = "mphantom";
				accessNum++;
				if (i > 0) {
					accessor[accessNum] = "operator "+i;
					attribute[accessNum] = "mphantom";
					accessNum++;
				}
			}
		}

		String result = "";
		try {
			result = formatter.format(expression,accessor,attribute,value);
		}
		catch (BadExpressionError err) {
			System.out.println("Bad expression in invisibleExcept: "+err);
		}
		catch (NoSuchFieldException err) {
			System.out.println("Attribute not found in invisibleExcept:"+err);
		}
		formatter = null;
		return result;
	}
	
	public static void getContainers(Component c) {
		Container parent = c.getParent();
		System.out.println("Parents of "+c+":");
		while (parent != null) {
			System.out.println(parent+"::"+parent.getSize());
			parent = parent.getParent();
		}
		System.out.println("----");
	}
}

	
