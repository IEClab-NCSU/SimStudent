package edu.cmu.old_pact.cmu.sm;

//An Equation substitutes letters for the constants in an equation
//It also maintains the bindings of the variables.

import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.old_pact.cmu.sm.query.StandardMethods;
import edu.cmu.old_pact.cmu.sm.query.StringQuery;

public class Equation implements Queryable {
	private Expression boundLeft;
	private Expression boundRight;
	private Expression leftside;
	private Expression rightside;
	private String form;
	private Vector bindings;
	private Vector varBindings;
	private String currentLetter;
	private String pattern = "";
	private String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	private class Binding {
		public Number val;
		public String var;
                public String origVar;
		
		public Binding(Number num,String vari) {
			val = num;
			var = vari;
                        origVar = null;
		}

                public Binding(String oVar,String vari) {
                        val = null;
                        var = vari;
                        origVar = oVar;
                }
	}

	private class VarBinding {
		public String source;
		public String value;
		
		public VarBinding(String src,String val) {
			source = src;
			value = val;
		}
	}
	
	public Equation(Expression left,Expression right) {
		initialize(left,right);
	}

        public Equation(String equation) throws BadExpressionError{
            parseEquation(equation,null,false);
        }

        public Equation(String equation,String[] vars) throws BadExpressionError{
            parseEquation(equation,vars,true);
        }

	public void finalize() throws Throwable{
		try{
			boundLeft = boundRight = null;
			leftside = rightside = null;
			form = currentLetter = pattern = alphabet = null;

			if(bindings != null){
				bindings.removeAllElements();
			}
			bindings = null;

			if(varBindings != null){
				varBindings.removeAllElements();
			}
			varBindings = null;
		}
		finally{
			super.finalize();
		}
	}

	private void parseEquation(String equation,String[] vars,boolean useVars) throws BadExpressionError {
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(Expression.getMaintainVars());
		try {
			int equalPos = equation.indexOf('=');
			if (equalPos > 0) {
				String leftString = equation.substring(0,equalPos);
				String rightString = equation.substring(equalPos+1);
                                Expression leftEx, rightEx;
                                if(useVars){
                                    leftEx = sm.parse(leftString,vars);
                                    rightEx = sm.parse(rightString,vars);
                                }
                                else{
                                    //	System.out.println("about to parse: "+leftString);
                                    leftEx = sm.parse(leftString);
                                    //	System.out.println("about to parse: "+rightString);
                                    rightEx = sm.parse(rightString);
                                }
				initialize(leftEx,rightEx);
			}
			else { //if no equals sign, set right side to null
				Expression leftEx = sm.parse(equation);
				initialize(leftEx,null);
			}
		}
		catch (ParseException err) {
			throw new BadExpressionError(equation);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(equation);
		}
	}
	
	//makeForm is used when we want to create an equation form, but we don't care about the constants
	//We assume that all letters from a-k represent constants and just copy the rest of the expression
	public static Equation makeForm(String form) throws BadExpressionError {
		String constantLetters = "abcdefghijk";
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(false);
		Equation fe = new Equation(form);
		Expression fLeft = fe.getLeft();
		Expression fRight = fe.getRight();
		for(int i=0;i<constantLetters.length();i++){
			fLeft = fLeft.substitute(new VariableExpression(new String(new char[] {constantLetters.charAt(i)})),
									 new NumberExpression(i+2));
			if(fRight != null){
				fRight = fRight.substitute(new VariableExpression(new String(new char[] {constantLetters.charAt(i)})),
										   new NumberExpression(i+2));
			}
		}
		if(fRight == null){
			fe = new Equation(fLeft.toString());
		}
		else{
			fe = new Equation(fLeft.toString() + "=" + fRight.toString());
		}
		return fe;
	}
		
	private void initialize(Expression left,Expression right) {
		bindings = new Vector();
		varBindings = new Vector();
		currentLetter = "a"; //needs to be before "getBindings"
		leftside = left;
                //System.out.println("Equation init:           leftside = " + leftside.debugForm());
		rightside = right;
		//Olga
		left = left.sort();
		if (right != null)
			right = right.sort();
		//end Olga
		addVariableBindings();
                //System.out.println("Equation init:      left[.sort()] = " + left.debugForm());
		boundLeft = left.getBindings(this);
                //System.out.println("Equation init: left.getBindings() = " + boundLeft.debugForm());
                boundLeft = boundLeft.sort();
                //System.out.println("Equation init:          boundLeft = " + boundLeft.debugForm());
		if (right != null)
			boundRight = right.getBindings(this).sort();
		else
			boundRight = null;
	}
		
	public void addBinding(Number val,String var) {
		bindings.addElement(new Binding(val,var));
		int place = alphabet.indexOf(var);
		currentLetter = alphabet.substring(place+1,place+2);
	}
	
	public void addBinding(String val,String var) {
		bindings.addElement(new Binding(val,var));
		int place = alphabet.indexOf(var);
		currentLetter = alphabet.substring(place+1,place+2);
	}
	
	//The first variable we see is always Z, the second Y and the third X,
        //continuing to step backwards from the end of the alphabet for each
        //subsequent variable that we encounter.
	//This allows us to match "3y+4=5" against "ax+b=c"
	//We store variable bindings like numeric bindings, so that we can be
	//consistent about which source variable is x, which is y and which is z
	//This needs to be done after leftside and rightside are set but before
	//getBindings is called
	private void addVariableBindings() {
		Vector leftVars = leftside.variablesUsed();
		Vector rightVars;
		if (rightside != null)
			rightVars = rightside.variablesUsed();
		else
			rightVars = new Vector();
		Vector allVars = new Vector();
		//insertion sort left and right vars into allVars
		insertVars(leftVars,allVars);
		insertVars(rightVars,allVars);
		String alpha = "ZYXWVUTSRQPONMLKJIHGFEDCBA";
		for (int i=0;i<allVars.size();++i)
			varBindings.addElement(new VarBinding((String)(allVars.elementAt(i)),alpha.substring(i,i+1)));
		leftVars.removeAllElements();
		leftVars = null;
		rightVars.removeAllElements();
		rightVars = null;
		allVars.removeAllElements();
		allVars = null;
	}
		
	private void insertVars(Vector newvars,Vector allvars) {
		for (int i=0;i<newvars.size();++i) {
			String thisvar = ((String)(newvars.elementAt(i))).toUpperCase();
			boolean done = false;
			for (int j=0;j<allvars.size() && !done;++j) {
				String compareString = (String) (allvars.elementAt(j));
				int comp = compareString.compareTo(thisvar);
				if (comp == 0)
					done = true; //variable already there
				else if (comp > 0) {
					allvars.insertElementAt(thisvar,j);
					done = true;
				}
			}
			if (!done)
				allvars.addElement(thisvar); //if not in yet, put it at the end
		}
	}
						
	public String getNextLetter() {
		return currentLetter;
	}
	
	//getPatternVariable is used to retrieve the variable that should be used in the
	//pattern. Unlike with numeric constants, where we use
	//a new letter each time, with variables, we need to check if we've assigned a
	//pattern variable to the source yet. This way, "3y+3=5y" becomes "ax+b=cx" and
	//"3y+3=5x" becomes "ay+b=cx"
	public String getPatternVariable(String sourceVar) {
		String foundVar = null;
		for (int i=0;i<varBindings.size() && foundVar==null;++i) {
			VarBinding bind = (VarBinding)(varBindings.elementAt(i));
			if (bind.source.equalsIgnoreCase(sourceVar))
				foundVar = bind.value;
		}
		return foundVar;
	}
	
	public void setPattern(Expression pat) {
		pattern = pat.toString();
	}
	
	public String getPattern() {
		if (boundRight != null)
			return boundLeft.toString()+"="+boundRight.toString();
		else
			return boundLeft.toString();
	}
	
	public Expression getBoundLeft() {
		return boundLeft;
	}
	
	public Expression getBoundRight() {
		return boundRight;
	}
	
	public Expression getLeft() {
		return leftside;
	}
	
	public Expression getRight() {
		return rightside;
	}
	
	private static boolean safeExactMatch(Expression ex1,Expression ex2) {
		if (ex1 == null && ex2 == null)
			return true;
		else if (ex1 == null || ex2 == null)
			return false;
		else
			return ex1.exactEqual(ex2);
	}
	
	public boolean expressionPatternMatches(Expression left, Expression right) {
		boolean leftmatches = safeExactMatch(left,boundLeft);
		boolean rightmatches = safeExactMatch(right,boundRight);
		boolean leftReverseMatches = safeExactMatch(left,boundRight);
		boolean rightReverseMatches = safeExactMatch(right,boundLeft);
		return ((leftmatches && rightmatches) ||
				(leftReverseMatches && rightReverseMatches));
	}
	
	public boolean patternMatches(Equation other) {
		return other.expressionPatternMatches(boundLeft,boundRight);
	}
	
	//sideMatch returns 1 if the given expression matches the left side, 2 if it matches the right and 0 if neither
	//We assume that the argument represents an expression (that is, it only has a left side)
	public int sideMatch(Equation other) {
		if (other.getBoundLeft().exactEqual(boundLeft))
			return 1;
		else if (other.getBoundLeft().exactEqual(boundRight))
			return 2;
		else
			return 0;
	}
		
	public Queryable getProperty (String prop) throws NoSuchFieldException {
		Queryable result;
		if (prop.equalsIgnoreCase("Left") ||
			prop.equalsIgnoreCase("Left side"))
			result = leftside;
		else if (prop.equalsIgnoreCase("Right") ||
			prop.equalsIgnoreCase("Right side"))
			result = rightside;
		//"variable side expression" assumes caller knows either
		//1 side or the other has the variable (not both, not neither)
		else if (prop.equalsIgnoreCase("Variable side expression")) {
			if (leftside.variablesUsed().size() > 0)
				result = leftside;
			else
				result = rightside;
		}
                else if (prop.length() > 31 && prop.substring(0,31).equalsIgnoreCase("target variable side expression")){
                    String v = prop.substring(33);
                    if(leftside.variablesUsed().contains(v)){
                        result = leftside;
                    }
                    else{
                        result = rightside;
                    }
                }
		else if (prop.equalsIgnoreCase("Variable side")) {
			if (leftside.variablesUsed().size() > 0 &&
				rightside.variablesUsed().size() > 0)
				result = new StringQuery("both");
			else if (leftside.variablesUsed().size() > 0)
				result = new StringQuery("left");
			else
				result = new StringQuery("right");
		}
                else if (prop.length() > 20 && prop.substring(0,20).equalsIgnoreCase("target variable side")){
                    String v = prop.substring(22);
                    if(leftside.variablesUsed().contains(v) &&
                       rightside.variablesUsed().contains(v)){
                        result = new StringQuery("both");
                    }
                    else if(leftside.variablesUsed().contains(v)){
                        result = new StringQuery("left");
                    }
                    else{
                        result = new StringQuery("right");
                    }
                }
		else if (prop.equalsIgnoreCase("Constant side expression")) {
			if (leftside.variablesUsed().size() == 0)
				result = leftside;
			else
				result = rightside;
		}
		else if (prop.equalsIgnoreCase("Pattern") ||
				 prop.equalsIgnoreCase("Form"))
				result = new StringQuery(getPattern());
		//"matches form <equation>" allows us to test matches using expressions, rather than
		//strings. This means that we can ignore differences in constants, match against
		//the left or right sides, and ignore issues having to do with printing to strings
		else if (prop.length() > 12 && prop.substring(0,12).equalsIgnoreCase("matches form")) {
			try {
				Equation matchForm = Equation.makeForm(prop.substring(13));
				result = new BooleanQuery(patternMatches(matchForm));
			}
			catch (BadExpressionError err) {
				System.out.println("Can't parse "+prop.substring(13));
				result = new BooleanQuery(false); //maybe should throw error?
			}
		}
		//The "form matching <expression>" asks to return a side that matches the given expression
		else if (prop.length() > 13 && prop.substring(0,13).equalsIgnoreCase("form matching")) {
			try {
				Equation matchForm = Equation.makeForm(prop.substring(14));
				int side = sideMatch(matchForm);
				if (side == 1)
					result = leftside;
				else if (side == 2)
					result = rightside;
				else
					throw new NoSuchFieldException("Neither side matches "+prop.substring(14));
			}
			catch (BadExpressionError err) {
				System.out.println("Can't parse "+prop.substring(13));
				result = new BooleanQuery(false); //maybe should throw error?
			}
		}				
		//The "side matching <expression>" returns a string with the side that matches
		else if (prop.length() > 13 && prop.substring(0,13).equalsIgnoreCase("side matching")) {
			try {
				Equation matchForm = Equation.makeForm(prop.substring(14));
				int side = sideMatch(matchForm);
				if (side == 1)
					result = new StringQuery("left");
				else if (side == 2)
					result = new StringQuery("right");
				else
					throw new NoSuchFieldException("Neither side matches "+prop.substring(14));
			}
			catch (BadExpressionError err) {
				System.out.println("Can't parse "+prop.substring(13));
				result = new BooleanQuery(false); //maybe should throw error?
			}
		}
		//side having property returns the name of the side which matches the given property
		//For example, "side having property canCombineLikeTerms" on 3x+4x=5 return "left"
		else if (prop.length() > 20 && prop.substring(0,20).equalsIgnoreCase("side having property")) {
			String property = prop.substring(21);
			boolean leftTrue = evalQuery(property+" of left side").getBooleanValue();
			boolean rightTrue = evalQuery(property+" of right side").getBooleanValue();
			if (leftTrue && rightTrue)
				return new StringQuery("both");
			else if (leftTrue)
				return new StringQuery("left");
			else if (rightTrue)
				return new StringQuery("right");
			else
				return new StringQuery("none");
		}
		else if (prop.equalsIgnoreCase("equation")) {
			result = new StringQuery(leftside.toString()+" = "+rightside.toString());
		}
		else if (prop.equalsIgnoreCase("all Numbers")) {
			Vector leftNums = leftside.allNumbers();
			Vector rightNums = rightside.allNumbers();
			for (int i=0;i<rightNums.size();++i)
				leftNums.addElement(rightNums.elementAt(i));
			result = new ArrayQuery(leftNums);
			rightNums.removeAllElements();
			rightNums = null;
		}
		else
			throw new NoSuchFieldException("Equation does not have property: "+prop);	
		return result;
	}
	
	public void setProperty(String prop, String value) throws NoSuchFieldException {
		throw new NoSuchFieldException("Equation does not have property: "+prop);
	}
	
	public Queryable evalQuery(String[] query) throws NoSuchFieldException{
		return StandardMethods.evalQuery(query,this);
	}

	public Queryable evalQuery(String query) throws NoSuchFieldException {
		return StandardMethods.evalQuery(query,this);
	}
	
	public Queryable applyOp(String op,Vector args) throws NoSuchFieldException {
		if (Expression.isBinaryOp(op))
			return Expression.applyBinaryOp(op,args);
		else if(Expression.isUnaryOp(op)){
			return Expression.applyUnaryOp(op,args);
		}
		else {
			return StandardMethods.applyOp(op,args);
		}
	}
				
	public Number getNumberValue() {
		throw new ClassCastException();
	}
	
	public String getStringValue() {
		return "[EI: "+leftside+"="+rightside+"::"+boundLeft+"="+boundRight+"]";
	}
	
	public boolean getBooleanValue() {
		throw new ClassCastException();
	}
	
	public Queryable[] getArrayValue() {
		throw new ClassCastException();
	}
	
	public String toString() {
		if(rightside == null){
			return leftside.toString() + " = null";
		}
		else{
			return leftside.toString() + " = " + rightside.toString();
		}
		
	}

	public String debugForm() {
		if(leftside != null){
			if(rightside != null){
				return leftside.debugForm() + " = " + rightside.debugForm();
			}
			else{
				return leftside.debugForm() + " = null";
			}
		}
		else{
			return "null";
		}
	}
}
