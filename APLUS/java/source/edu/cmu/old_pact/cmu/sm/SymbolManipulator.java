package edu.cmu.old_pact.cmu.sm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

public class SymbolManipulator {
	public boolean autoSimplify = false;  //if true, simplifies [RF,CLT & MT]
	public boolean autoStandardize = false; //if true, simplifies and standardizes
	public boolean autoCombineLikeTerms = false; //These are only used if autoSimplify &
	public boolean autoReduceFractions = false;  //autoStandardize are false
	public boolean autoMultiplyThrough = false;  //
	public boolean autoExpandExponent = false;  //
	public boolean autoDistribute = false; //if true, always distributes
	public boolean distributeDenominator = false; //if true, distributing (X+5)/9 becomes x/9+5/9; otherwise, (X+5)/9 doesn't distribute
												  //(this applies to autodistribute and to explicit calls to distribute that don't specify this argument)
	public boolean allowDoubleSigns = true; //if true, can have X+-5, etc.
	public boolean allowExtraParens = true; //if true, can have (3+x)+4 or 3((5+x))
	public boolean allowNegativeExponents = true; //if true, use x^-2 instead of 1/x^2 [not implemented]
	public boolean autoSort = false; //if true, expressions are sorted
//	public int printDecimalPlaces = 2;
//	public boolean useWordVariables = false; //if true, variables can be words, not just letters (not implemented, requires parser)

	private static java.util.Stack argStack = new java.util.Stack();
//	private static SMTokenManager tokes = null;
//	private static SM parser = null;
	private ExpressionParser myParser=new AsciiParser();
	
	public static final int asciiOutput = 0;      	//give output in standard ascii (e.g. "x^2+1/2")
	public static final int intermediateOutput = 1;   //give output in ascii, using square brackets for implied parens
	public static final int mathMLOutput = 2;     	//output mathML (presentation)
	public static final int debugOutput = 3;      	//output debugging form
	public static final int serializeOutput = 4;  	//output a serialized expression
    public static final int customOutput = 5;         //output determined by ExpressionFormatter
   	
    public static final boolean canSimplifyExponentPower = true;
    private ExpressionFormatter outputFormatter = null;

    /*varList is a list of strings which the parser should treat as
      variables.  maintainVarList determines whether we snag the
      variable list when the user passes it in as well as whether we
      fill it in when the caller subsequently fails to provide it.
      maintainVarList used to be static and default to true, but that
      was causing bugs.  I think I originally had it that way because
      the solver needed to remember the variable across SolverFrame,
      SolverTutor, the rules, etc.  Now what we'll do is default it to
      false, so anybody who wants their instance of SymbolManipulator
      to join the I've-got-the-same-variables-as-you-do party has to
      call setMaintainVarList(true) explicitly.

	  This scheme of thing was also creating problems in the few
	  instances that the Expression classes call on the symbol
	  manipulator to parse things (e.g. in new Equation(String)).
	  There was no way for the expression class to know whether it
	  should call setMaintainVarList(true) ... because there is no
	  longer a static thing here that it could check.  <sigh> So what
	  we're going to do now is add a static boolean to the top-level
	  Expression class to indicate whether we're in an
	  I've-got-the-same-variables-as-you-do context.  In theory this
	  thing should be synchronized ...*/
    private boolean maintainVarList = false;
    private static String [] varList = null;

	private int outputType = asciiOutput;

	public SymbolManipulator() {
		myParser.init();
	}
	
	public void setParser(ExpressionParser newParser) {
		myParser = newParser;
		newParser.init();
	}
	
	public int getOutputType(){
		return outputType;
	}

	public void setOutputType(int newType) {
		outputType = newType;
	}
	
	public void setOutputType(ExpressionFormatter outputFormat) {
		outputType = customOutput;
		outputFormatter = outputFormat;
	}
	
//	public static void initParser(String theExpression) {
//	    ASCII_CharStream instream = new ASCII_CharStream(new java.io.StringBufferInputStream(theExpression),0,0,theExpression.length());
//
//		if (tokes == null) {
//			tokes = new SMTokenManager(instream);
//			parser = new SM(tokes);
//		}
//		else {
//	    	tokes.ReInit(instream); //re-initialize token manager (get reused each time)
//			parser.ReInit(tokes);
//		}
//	}
	

    /**
       * Get the value of varList.
       * @return Value of varList.
       */
    public static String [] getVarList() {return varList;}
    
    /**
       * Set the value of varList.
       * @param v  Value to assign to varList.
       */
    public static void setVarList(String []  v) {if(v != null){varList = v;}}
    
    /**
       * Get the value of maintainVarList.
       * @return Value of maintainVarList.  */
    public boolean getMaintainVarList() {return maintainVarList;}
    
    /**
       * Set the value of maintainVarList.
       * @param v  Value to assign to maintainVarList.
       */
    public void setMaintainVarList(boolean  v) {maintainVarList = v;}
    
    public static void forgetVarList() {varList = null;}

	//should probably be private
	public Expression parse(String theExpression) throws ParseException {
            Expression e;

            if(maintainVarList && varList != null){
                //trace.out("SM: using stored variable list");
                return myParser.parse(theExpression,varList);
            }
            else{
                e = myParser.parse(theExpression);
                //trace.out("SM: parsed \"" + theExpression + "\" with no variables.");
                return e;
            }
	}

        public Expression parse(String theExpression,String[] vars) throws ParseException {
            Expression e;
            if(maintainVarList){
                varList = vars;
            }
            e = myParser.parse(theExpression,vars);
            /*trace.out("SM: parsed \"" + theExpression + "\" with variables " +
              vars + "\n    " + e.debugForm());*/
            return e;
        }
			
	//Yeech - number of decimal places (print and compare) is stored in a static veriable in NumberExpression.
	//This is convenient, since we don't have to pass the number of decimal places around, but multiple SymbolManipulators
	//can stomp on each other. One solution would be to set the number of decimal places in the NumberExpression before doing
	//the guts of any public SymbolManipulator action. Probably a better way would be to create a class called "ExpressionArgs"
	//which contains this kind of information and is passed to all expression methods. We could include decimal place info
	//as well as distributeDenominator and maybe some other stuff
	public static void setUseSigFigs(boolean val){
		NumberExpression.setUseSigFigs(val);
	}

	public static boolean getUseSigFigs(){
		return NumberExpression.getUseSigFigs();
	}

	public void setPrintDecimalPlaces(int places) {
		NumberExpression.setPrintDecimalPlaces(places);
	}
	
	public int getPrintDecimalPlaces() {
		return NumberExpression.getPrintDecimalPlaces();
	}

	public void setPrintMinDecimalPlaces(int places) {
		NumberExpression.setPrintMinDecimalPlaces(places);
	}
	
	public int getPrintMinDecimalPlaces() {
		return NumberExpression.getPrintMinDecimalPlaces();
	}

	public void setCompareDecimalPlaces(int places) {
		NumberExpression.setCompareDecimalPlaces(places);
	}

	public int getCompareDecimalPlaces() {
		return NumberExpression.getCompareDecimalPlaces();
	}
	
	private boolean expressionOK(Expression ex) {
		return (ex != null && !(ex instanceof BadExpression));
	}
	
	private Expression doAuto(Expression ex) {
		Expression result = ex;
		if (autoStandardize){
			//trace.out("SM: doAuto: about to standardize: " + ex.debugForm());
			if(distributeDenominator){
				result = ex.standardize(Expression.DISTBOTH);
			}
			else{
				result = ex.standardize(Expression.DISTNUM);
			}
			//trace.out("SM: doAuto: standardized: " + result.debugForm());
		}
		else {
			Expression prevResult;
			int loopcount = 0;
			do{
				prevResult = result;
				loopcount++;
				if (autoSimplify) {
					//trace.out("SM.dA: about to simplify: " + result.debugForm());
					result = result.simplify();
					//trace.out("SM.dA: simplified: " + result.debugForm());
				}
				else { //(maybe) do individual simplifications
					if (!allowExtraParens)
						result = result.removeParens();
					if (autoExpandExponent)
						result = result.expandExponent();
					if (autoMultiplyThrough)
						result = result.multiplyThrough();
					if (autoCombineLikeTerms)
						result = result.combineLikeTerms();
					if (autoReduceFractions)
						result = result.reduceFractions();
					if (!allowDoubleSigns)
						result = result.removeDoubleSigns();
				}
				if (autoDistribute){
					if(distributeDenominator){
						result = result.distribute(Expression.DISTBOTH);
					}
					else{
						result = result.distribute(Expression.DISTNUM);
					}
				}
				else if(distributeDenominator){
					result = result.distribute(Expression.DISTDEN);
				}
				/*trace.out("SM.doAuto: end of simp loop: " + result.debugForm());
				  trace.out("                             " + prevResult.debugForm());*/
			}while(!result.exactEqual(prevResult) && loopcount <= 10);
			if(loopcount > 10){
				trace.out("SM.doAuto: simplification loop for " + result);
			}
		}
		if (autoSort){
			result = result.sort();
		}
		return result;
	}
		
	//finalizeExpression does final cleanup on the expression, depending on the
	//automatic settings
	//This includes doing full or partial simplification or standardization
	private String finalizeExpression(Expression ex) {
		//trace.out("SM: finalizeExpression of: "+ex.toString());
		Expression result = doAuto(ex);
		//trace.out("SM: after finalize: "+result.toString());
		return convertOutput(result);
	}
	
	private String convertOutput(Expression result) {
		//trace.out("SM convertOutput result = "+result.debugForm()+" outputType = "+outputType);
		if (outputType == asciiOutput)
			return result.toString();
		else if (outputType == intermediateOutput)
			return result.toIntermediateString();
		else if (outputType == debugOutput)
			return result.debugForm();
		else if (outputType == mathMLOutput)
			return result.toMathML();
		else if (outputType == serializeOutput) {
			//serialize object to a string and return the string
			try {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				ObjectOutputStream outObj = new ObjectOutputStream(outStream);
				outObj.writeObject(result);
				return outStream.toString();
			}
			catch (IOException err) { //should it just throw this error?
				System.out.println("Error trying to serialize "+result.toString());
				return result.toString();
			}
		}
		else if (outputType == customOutput && outputFormatter != null)
			return outputFormatter.produceOutput(result);
		else {
			trace.out("Don't support output type "+outputType+" will use ascii");
			return result.toString();
		}
	}
	
	public boolean isExpression(String potentialExp) {
		boolean ok = false;
		try {
			parse(potentialExp);
			ok = true;
		}
		catch (ParseException err) {
//			trace.out("Found parse error for "+potentialExp);
		}
		catch (TokenMgrError err) {
		}
		return ok;
	}
	
	public boolean isNumber(String potentialNum) {
		boolean ok = false;
		try {
			Expression term = parse(potentialNum);
			if (term instanceof NumericExpression)
				ok = true;
			else
				ok = false;
		}
		catch (ParseException err) {
		}
		catch (TokenMgrError err) {
		}
		return ok;
	}

	public boolean algebraicEqual(String one,String two) throws BadExpressionError {
		try {
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);
			
			//trace.out("Cannon 1: "+oneEx.cannonicalize().toString());
			//trace.out("Cannon 1: "+oneEx.cannonicalize().debugForm());
			//trace.out("Cannon 2: "+twoEx.cannonicalize().toString());
			//trace.out("Cannon 2: "+twoEx.cannonicalize().debugForm());

			//trace.out("SM: algebraicEqual: " + oneEx + ".(" + twoEx + ")");
			boolean result = oneEx.algebraicEqual(twoEx);
			return result;
		}
		catch (ParseException err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch (TokenMgrError err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}

	public boolean exactEqual(String one,String two) throws BadExpressionError{
		try{
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);

			boolean result = oneEx.exactEqual(twoEx);
			return result;
		}
		catch(ParseException err){
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch(TokenMgrError err){
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}

	public boolean similar(String one,String two) throws BadExpressionError {
		try {
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);
			
			boolean result = oneEx.similar(twoEx);
			/*trace.out("SM.similar(" + oneEx + "," + twoEx + "): " + result);
			  if(!result){
			  trace.out("reason: " + oneEx.whyNotSimilar(twoEx));
			  }*/
			return result;
		}
		catch (ParseException err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch (TokenMgrError err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}


	public String combineLikeTerms(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.combineLikeTerms();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}


	public String reduceFractions(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);
			Expression result = ex.reduceFractions();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public String expandExponent(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.expandExponent();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String eliminateExponent(String expString) throws BadExpressionError {
		return eliminateExponent(expString,false);
	}

	public String eliminateExponent(String expString,boolean explicitMult) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.eliminateExponent();
			if((outputType == asciiOutput) && explicitMult){
				return addExplicitMult(result.toString());
			}
			else{
				return convertOutput(result);
			}
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
		catch (NoSuchFieldException err){
			throw new BadExpressionError(expString);
		}
	}

	public String addExplicitMult(String expr) throws BadExpressionError,NoSuchFieldException{
		String s = sort(expr);
		int len = Integer.parseInt(runScript("length of factors",s));
		//figure that we'll average 2 chars per factor ...
		StringBuffer ex = new StringBuffer(3*len);
		for(int i=0;i<len;i++){
			ex.append(runScript("item " + (i+1) + " of factors",s));
			if(i != len-1){
				ex.append("*");
			}
		}
		return ex.toString();
	}

        public String substConstants(String expString) throws BadExpressionError{
            try{
                Expression ex = parse(expString);

                Expression result = ex.substConstants();
                return convertOutput(result);
            }
            catch(ParseException err){
                throw new BadExpressionError(expString);
            }
            catch(TokenMgrError err){
                throw new BadExpressionError(expString);
            }
        }
	
	public String multiplyThrough(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.multiplyThrough();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String simplify(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.simplify();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public String cannonicalize(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.cannonicalize();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
		
	public boolean canSimplify(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canSimplify();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String standardize(String expString,boolean distributeDenominator) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			int type = Expression.DISTNUM;
			if(distributeDenominator){
				type |= Expression.DISTDEN;
			}
			Expression result = ex.standardize(type);
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public String standardize(String expString) throws BadExpressionError {
		return standardize(expString,distributeDenominator);
	}

	public String removeExtraParens(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.removeParens();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String removeDoubleSigns(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.removeDoubleSigns();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String fractionToDecimal(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.fractionToDecimal();
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String absoluteValue(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.absoluteValue();
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String negate(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.negate();
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String reciprocal(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.reciprocal();
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public String coefficient(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.simplifiedCoefficient();
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

        public String numericCoefficient(String expString) throws BadExpressionError {
            try{
                Expression ex = parse(expString);

                Expression result = ex.numericSimplifiedCoefficient();
                return finalizeExpression(result);
            }
            catch(ParseException err){
                throw new BadExpressionError(expString);
            }
            catch(TokenMgrError err){

                throw new BadExpressionError(expString);
            }
        }

	public String distribute(String expString, boolean dd) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			int type = Expression.DISTNUM;
			if(dd){
				type |= Expression.DISTDEN;
			}
			//trace.out("SM.distribute: " + ex.debugForm());
			Expression result = ex.distribute(type);
			//trace.out("SM.distribute: about to finalize: " + result.debugForm());
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	//By default, we use the value of distributeDenominator to determine whether to distribute the denominator
	public String distribute(String expString) throws BadExpressionError {
		return distribute(expString,distributeDenominator);
	}

	public boolean canDistribute(String expString, boolean dd) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			int type = Expression.DISTNUM;
			if(dd){
				type |= Expression.DISTDEN;
			}
			return ex.canDistribute(type);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	//By default, we use the value of distributeDenominator to determine whether to consider distributing the denominator
	public boolean canDistribute(String expString) throws BadExpressionError {
		return canDistribute(expString,distributeDenominator);
	}
	
	//By default, we use the value of distributeDenominator to determine whether to consider distributing the denominator
	public String distributeOne(String expString,boolean dd) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			int type = Expression.DISTNUM;
			if(dd){
				type |= Expression.DISTDEN;
			}
			Expression result = ex.distributeOne(type);
			String resultString = convertOutput(result);
			return (resultString);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public String distributeOne(String expString) throws BadExpressionError {
		return distributeOne(expString,distributeDenominator);
	}

	public String getOneToDistribute(String expString,boolean dd) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			int type = Expression.DISTNUM;
			if(dd){
				type |= Expression.DISTDEN;
			}
			Expression result = ex.getOneToDistribute(type);
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String getOneToDistribute(String expString) throws BadExpressionError {
		return getOneToDistribute(expString,distributeDenominator);
	}
	
	public String[] getPartsToDistribute(String expString, boolean dd) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			int type = Expression.DISTNUM;
			if(dd){
				type |= Expression.DISTDEN;
			}
			Expression[] result = ex.getPartsToDistribute(type);
			String[] stringResults = new String[result.length];
			for (int i=0;i<result.length;++i)
				stringResults[i] = convertOutput(result[i]);
			result = null;
			return stringResults;
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
		
	public String[] getPartsToDistribute(String expString) throws BadExpressionError {
		return getPartsToDistribute(expString,distributeDenominator);
	}

	public boolean canCombineLikeTerms(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canCombineLikeTerms();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public boolean canCombineLikeTermsWhole(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canCombineLikeTermsWhole();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public boolean canReduceFractions(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canReduceFractions();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public boolean canReduceFractionsWhole(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canReduceFractionsWhole();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public boolean canMultiplyThrough(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canMultiplyThrough();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public boolean canMultiplyThroughWhole(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canMultiplyThroughWhole();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public boolean canExpandExponent(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canExpandExponent();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public boolean canEliminateExponent(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canEliminateExponent();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
        public boolean canSubstConstants(String expString) throws BadExpressionError{
            try{
                Expression ex = parse(expString);

                return ex.canSubstConstants();
            }
            catch(ParseException err){
                throw new BadExpressionError(expString);
            }
            catch(TokenMgrError err){
                throw new BadExpressionError(expString);
            }
        }

	public boolean canRemoveExtraParens(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			return ex.canRemoveParens();
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	/*returns true if expString contains a term like x3.  x*3 and 3x
      and 3*x are all okay (return false).  This has to happen here
      rather than within the expression classes because we need to
      have access to the original string representation.*/
	public boolean hasConstantAfterVar(String expString) throws BadExpressionError{
		try{
			Expression ex = parse(expString);
			//trace.out("SM.hCAV: " + expString + ": " + ex.debugForm());

			boolean reversed = false;
			ExpressionArray terms = termComponents(ex);
			if(terms != null){
				for(int i=0;i<terms.size() && !reversed;i++){
					TermExpression term = (TermExpression)terms.expressionAt(i);
					//trace.out(" SM.hCAV: processing term " + i + ": " + term);
					int factors = term.evalQuery("length of factors").getNumberValue().intValue();
					boolean foundVar = false;
					for(int j=1;j<=factors && !reversed;j++){
						//trace.out("  SM.hCAV: processing factor " + j + " [" + foundVar + "]");
						if(foundVar){
							if(term.evalQuery("isNumber of item " + j + " of factors").getBooleanValue()){
								/*we've got a number after a variable.
                                  now we have to check to see if they
                                  also forgot the '*'.*/
								reversed = hasTermNoAsterisk(expString,term,j-1);
								//trace.out("   SM.hCAV: reversed=" + reversed);
							}
						}
						else if(!term.evalQuery("isNumber of item " + j + " of factors").getBooleanValue()){
							foundVar = true;
						}
					}
				}
			}

			return reversed;
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
		catch (NoSuchFieldException nsfe){
			return false; //?
		}
	}

	private static ExpressionArray termComponents(Expression ex){
		ExpressionArray ret = null;
		if(ex instanceof TermExpression){
			ret = ExpressionArray.allocate();
			ret.addExpression(ex);
		}

		if(ex.canHaveComponents()){
			ExpressionArray components = ex.getComponentArray();
			for(int i=0;i<components.size();i++){
				ExpressionArray compterms = termComponents(components.expressionAt(i));
				if(compterms != null){
					if(ret == null){
						ret = compterms;
					}
					else{
						for(int j=0;j<compterms.size();j++){
							ret.addExpression(compterms.expressionAt(j));
						}
					}
				}
			}
		}

		return ret;
	}

	/*returns true if the term te appears in exp with no asterisk
      after the specified factor (first factor is 1)*/
	private static boolean hasTermNoAsterisk(String exp,TermExpression te,int afterfactor){
		//trace.out("SM.hTNA(" + exp + "," + te + "," + afterfactor + ")");
		Vector comps = te.getComponents();
		int i = 0;
		while((i=exp.indexOf(comps.elementAt(0).toString(),i)) != -1){
			boolean foundall = true;
			for(int compnum=1;compnum<comps.size() && foundall;compnum++){
				do{
					i++;
				}while(exp.charAt(i) == '(' ||
					   exp.charAt(i) == ')' ||
					   (compnum != afterfactor &&
						exp.charAt(i) == '*'));
				if(i != exp.indexOf(comps.elementAt(compnum).toString(),i)){
					foundall = false;
				}
			}
			if(foundall){
				return true;
			}
		}

		return false;
	}

        public boolean canFactor(String expString) throws BadExpressionError{
            try{
                Expression ex = parse(expString);

                return ex.canFactor();
            }
            catch(ParseException err){
                throw new BadExpressionError(expString);
            }
            catch(TokenMgrError err){
                throw new BadExpressionError(expString);
            }
        }

        public boolean canFactor(String expString,String factor) throws BadExpressionError{
            try{
                Expression ex = parse(expString);
                Expression fact = parse(factor);

                return ex.canFactor(fact);
            }
            catch(ParseException err){
                throw new BadExpressionError(expString);
            }
            catch(TokenMgrError err){
                throw new BadExpressionError(expString);
            }
        }

	public boolean canFactorPiecemeal(String expString,String factor) throws BadExpressionError{
		try{
			Expression ex = parse(expString);
			Expression fact = parse(factor);
			
			return ex.canFactorPiecemeal(fact);
		}
		catch(ParseException err){
			throw new BadExpressionError(expString);
		}
		catch(TokenMgrError err){
			throw new BadExpressionError(expString);
		}
	}

	public String factorPiecemeal(String expString,String factor) throws BadExpressionError {
		try {
			Expression ex = parse(expString);
			Expression fact = parse(factor);

			Expression result = ex.factorPiecemeal(fact);
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String factor(String expString,String factor) throws BadExpressionError {
		try {
			Expression ex = parse(expString);
			Expression fact = parse(factor);

			Expression result = ex.factor(fact);
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String factor(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.factor();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}

	public String factorQuadratic(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.factorQuadratic();
			return convertOutput(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public String expand(String expString,boolean dd) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			int type = Expression.DISTNUM;
			if(dd){
				type |= Expression.DISTDEN;
			}
			Expression result = ex.expand(type);
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
	
	public String expand(String expString) throws BadExpressionError {
		return expand(expString,distributeDenominator);
	}
	
	public String sort(String expString) throws BadExpressionError {
		try {
			Expression ex = parse(expString);

			Expression result = ex.sort();
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(expString);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(expString);
		}
	}
		
	
	//noOp is sometimes useful when you don't really want to do anything but the "automatic" stuff
	//(auto-simplifications and output conversions)
	public String noOp(String exp) throws BadExpressionError {
		try {
			Expression oneEx = parse(exp);
			return finalizeExpression(oneEx);
		}
		catch (ParseException err) {
			throw new BadExpressionError(exp);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(exp);
		}
	}
	
	//noOpExprOrEquation should be used with MathMl output only.
	//calls noOp on expressions and each part of an equation
	public String noOpExprOrEquation(String str) throws BadExpressionError {
		boolean oldUseSigFigs = getUseSigFigs();
		setUseSigFigs(true);

		String  str1="", str2="", oper="", s="";
	    String res = "";
	    Expression exp;
	    int ind = str.indexOf("=");
		
		if(ind > 0) {   // "=" found
		  	s = str.substring(ind-1,ind);			 
		  	if(s.equals(">")) { // ">=" found
		    	oper = "<mo>&ge;</mo>";
		    	str1 = str.substring(0,ind-1);
		    	str2 = str.substring(ind+1);
		  	}		   
		  	else if(s.equals("<")) { // "<=" found
		         	oper = "<mo>&leq;</mo>";
		         	str1 = str.substring(0,ind-1);
		         	str2 = str.substring(ind+1);		         	 
		       	}
		       	else {             // "="
		           		oper = "<mo>=</mo>";
		           		str1 = str.substring(0,ind);
		           		str2 = str.substring(ind+1);
		       		 }
		 	} else {    // "=" NOT found
		    	ind = str.indexOf("<");
		    	if(ind > 0) {  // "<" found
		      	oper = "<mo>&less;</mo>";
		      	str1 = str.substring(0,ind);
		      	str2 = str.substring(ind+1);
		    	} else {
		        	ind = str.indexOf(">");
		        	if(ind > 0) {  // ">" found
		          	oper = "<mo>&gt;</mo>";
		          	str1 = str.substring(0,ind);
		          	str2 = str.substring(ind+1);
		         } else   // one expression
		         	str1 = str;
		     }
		 } 
		 try {
		 	exp = parse(str1);
		 	res = finalizeExpression(exp);

		 	if(str2.equals("")) {			  
				setUseSigFigs(oldUseSigFigs);
			   return res; 
		 	}		
			 exp = parse(str2);
			 res = res + oper + finalizeExpression(exp);
			 setUseSigFigs(oldUseSigFigs);
	     	return res;
		}
		catch (ParseException err) {
			setUseSigFigs(oldUseSigFigs);
			throw new BadExpressionError(str);
		}
		catch (TokenMgrError err) {
			setUseSigFigs(oldUseSigFigs);
			throw new BadExpressionError(str);
		}	
	} 

	/*
	public String noOpExprOrEquation(String str) throws BadExpressionError {
		String res, exp1, exp2;
		Expression oneEx;
		int ind = str.indexOf("=");
		
		try {
			if(ind == -1) {
			  oneEx = parse(str);
			  return finalizeExpression(oneEx); 
			}
			exp1 = str.substring(0,ind);
			exp2 = str.substring(ind+1); 
			oneEx = parse(exp1);
			res = finalizeExpression(oneEx);
			oneEx = parse(exp2);
			return res + "<mo>=</mo>" + finalizeExpression(oneEx);
		}
		catch (ParseException err) {
			throw new BadExpressionError(str);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(str);
		}	
	} 
*/		
	
	//format allows you to specify display attributes of the expression
	//This should only be used with MathML output, since other types of output don't
	//pay any attention to display attributes.
	//Format will also do any autosimplifications asked of it
	//
	//exp should be the expression (as a string)
	//accessor is a query which results in an Expression
	//attribute is a string specifying the attribute you want set
	//value is the value of that attribute
	//
	//This depends on the fact that the accessor returns an element that is part of the
	//expression, rather than creating a new element to respond to the access. In general,
	//this will only work when the accessor really accesses a portion of the Expression,
	//but it is up to the caller to verify that this is true
	public String format(String exp,String accessor,String attribute,String value) throws BadExpressionError, NoSuchFieldException {
		try {
			Expression oneEx = parse(exp);
			Expression simplified = doAuto(oneEx);
			Queryable element = simplified.evalQuery(accessor);
			element.setProperty(attribute,value);
			return convertOutput(simplified);
		}
		catch (ParseException err) {
			throw new BadExpressionError(exp);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(exp);
		}
	}		
	
	//format can also take a list of attributes, strings and values
	public String format(String exp,String accessor[],String attribute[],String value[]) throws BadExpressionError, NoSuchFieldException {
		try {
			Expression oneEx = parse(exp);
			Expression simplified = doAuto(oneEx);
			//trace.out("SM.format: " + exp + " => " + oneEx.debugForm()  + " => " + simplified.debugForm());
			for (int i=0;i<accessor.length;++i) {
				Queryable element = simplified.evalQuery(accessor[i]);
				element.setProperty(attribute[i],value[i]);
			}
			return convertOutput(simplified);
		}
		catch (ParseException err) {
			throw new BadExpressionError(exp);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(exp);
		}
	}
	
	//...or Vectors
	public String format(String exp,Vector accessor,Vector attribute,Vector value) throws BadExpressionError, NoSuchFieldException {
		try {
			Expression oneEx = parse(exp);
			Expression simplified = doAuto(oneEx);
			for (int i=0;i<accessor.size();++i) {
				String access = (String)(accessor.elementAt(i));
				String attrib = (String)(attribute.elementAt(i));
				String val = (String)(value.elementAt(i));
				Queryable element = simplified.evalQuery(access);
				element.setProperty(attrib,val);
			}
			return convertOutput(simplified);
		}
		catch (ParseException err) {
			throw new BadExpressionError(exp);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(exp);
		}
	}

	//format without any arguments is just used to output in a specific format (without any display attributes)
	//This is sometimes more convenient, since it handles the exceptions
	public String format(String exp) {
		String outputString = exp;
		try {
			Expression oneEx = parse(exp);
			Expression simplified = doAuto(oneEx);
			outputString = convertOutput(simplified);
		}
		catch (ParseException err) {
			System.out.println("Error parsing "+exp);
		}
		catch (TokenMgrError err) {
			System.out.println("Token Error parsing "+exp);
		}
		return outputString;
	}

	public String add(String one,String two) throws BadExpressionError {
		try {
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);

			Expression result;
			result = oneEx.add(twoEx);
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch (TokenMgrError err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}

	public String subtract(String one,String two) throws BadExpressionError {
		try {
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);

			Expression result;
			result = oneEx.subtract(twoEx);
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch (TokenMgrError err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}

	public boolean equationMatch(String eq1, String eq2) throws BadExpressionError 
	{
		boolean found = false;
		try
			{ 
                Equation e = new Equation(eq1);
    			Expression uE = e.getLeft().subtract(e.getRight());
        	    Expression ex = uE.simplify();
        	    String uInput= ex.getStringValue()+ "=0";
        	    
        	    Equation e2 = new Equation(eq2);
    			Expression uE2 = e2.getLeft().subtract(e2.getRight());
        	    Expression ex2 = uE2.simplify();
        	    String etlValue= ex2.getStringValue()+ "=0";
        	    
    			found = algebraicMatches(etlValue, uInput);
    			if(!found)
    			    {
						ex = ex.negate();
						uInput = ex.getStringValue();
						found = algebraicMatches(etlValue, uInput + "=0");
    			    }
				return found;
			}
		//catch (ParseException err) {
		//	throw new BadExpressionError(eq1);
		//}
		catch (TokenMgrError err) {
			throw new BadExpressionError(eq1);
		}
	}

	public String multiply(String one,String two) throws BadExpressionError {
		try {
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);

			Expression result;
			//trace.out("SM: multiplying: " + oneEx + " * " + twoEx);
			result = oneEx.multiply(twoEx);
			//trace.out("SM: multiplied; finalizing: " + result.debugForm());
//			trace.out("mult before finalize: "+result);
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch (TokenMgrError err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}
	
	public String divide(String one,String two) throws BadExpressionError {
		try {
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);

			Expression result;
			//trace.out("SM: dividing: " + oneEx + " / " + twoEx);
			result = oneEx.divide(twoEx);
			//trace.out("SM: divided; finalizing: " + result.debugForm());
//			trace.out("divisor: "+twoEx.debugForm());
//			trace.out("Divide before clean: "+result.debugForm());
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch (TokenMgrError err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}
	
	public String power(String one,String two) throws BadExpressionError {
		try {
			Expression oneEx = parse(one);
			Expression twoEx = parse(two);

			Expression result;
			result = oneEx.power(twoEx);
			
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
		catch (TokenMgrError err) {
			if (!isExpression(one))
				throw new BadExpressionError(one);
			else
				throw new BadExpressionError(two);
		}
	}
	
	public String squareroot(String ex) throws BadExpressionError {
		try {
			Expression oneEx = parse(ex);

			Expression result;
			result = oneEx.squareroot();
			//trace.out("squareroot of "+ex+" is "+result);
			
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			throw new BadExpressionError(ex);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(ex);
		}
	}
	
	public String root(String base,String rt) throws BadExpressionError {
		try {
			Expression baseEx = parse(base);
			Expression rtEx = parse(rt);

			Expression result;
			result = baseEx.root(rtEx);
			
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(base))
				throw new BadExpressionError(base);
			else
				throw new BadExpressionError(rt);
		}
		catch (TokenMgrError err) {
			if (!isExpression(base))
				throw new BadExpressionError(base);
			else
				throw new BadExpressionError(rt);
		}
	}


	// ALLEN
	public String crossMultiplyLeft(String left,String right) throws BadExpressionError {
		try {
			Expression leftEx = parse(left);
			Expression rightEx = parse(right);
			
			Expression leftNumEx = leftEx.numerator();
			Expression rightDenEx = rightEx.denominator();
			
			Expression result;
			result = (leftNumEx.multiply(rightDenEx));
			
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
		catch (TokenMgrError err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
	}
	
	public String crossMultiplyRight(String left,String right) throws BadExpressionError {
		try {
			Expression leftEx = parse(left);
			Expression rightEx = parse(right);
			
			Expression leftDenEx = leftEx.denominator();
			Expression rightNumEx = rightEx.numerator();
			
			Expression result;
			result = (leftDenEx.multiply(rightNumEx));
			
			return finalizeExpression(result);
		}
		catch (ParseException err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
		catch (TokenMgrError err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
	}
	// end ALLEN


	//Substitute provides a way to evaluate an expression with some constant (or other expression) substituted for a variable
	public String substitute(String expStr,String oldForm,String newForm) throws BadExpressionError {
		try {
			Expression exp = parse(expStr);
			Expression old = parse(oldForm);
			Expression nw = parse(newForm);
			
			Expression newexp = exp.substitute(old,nw);
			return finalizeExpression(newexp);
		}
		catch (ParseException err) {
			if (!isExpression(expStr))
				throw new BadExpressionError(expStr);
			else if (!isExpression(oldForm))
				throw new BadExpressionError(oldForm);
			else
				throw new BadExpressionError(newForm);
		}
		catch (TokenMgrError err) {
			if (!isExpression(expStr))
				throw new BadExpressionError(expStr);
			else if (!isExpression(oldForm))
				throw new BadExpressionError(oldForm);
			else
				throw new BadExpressionError(newForm);
		}
	}
	
	public String solveFor(String left,String right,String var) throws BadExpressionError {
		boolean simpMode = autoStandardize; //save simplification mode
		autoStandardize = true;
		String result = solveForInternal(left,right,var,0);
		autoStandardize = simpMode;
		return result;
	}
	
	private String solveForInternal(String left,String right,String var,int count) throws BadExpressionError {
//		trace.out("SolveForInternal: "+left+" = "+right+"  "+count);
		if (count > 10)
			return ""; //cop out if we don't solve in < 10 steps
		try {
			Expression leftEx = parse(left);
			Expression rightEx = parse(right);
			if (left.equals(var) && !rightEx.variablesUsed().contains(var))
				return right;
			else if (!rightEx.variablesUsed().contains(var) &&
						!leftEx.variablesUsed().contains(var))
				return "";
			else if (rightEx.variablesUsed().contains(var))
				return solveForInternal(subtract(left,right),"0",var,count+1);
			else if (leftEx instanceof PolyExpression) {
				PolyExpression leftPoly =  (PolyExpression)leftEx;
				Expression nonVarTerm = null;
				for (int i=0;i<leftPoly.numberOfTerms()&&nonVarTerm==null;++i) {
					Expression thisPoly = leftPoly.getTermAt(i);
					if (!thisPoly.variablesUsed().contains(var))
						nonVarTerm = thisPoly;
				}
				if (nonVarTerm != null)
					return solveForInternal(subtract(left,nonVarTerm.toString()),
											subtract(right,nonVarTerm.toString()),
											var,count+1);
				else //variables in all terms on left, assume linear, so must simplify
					return solveForInternal(simplify(left),simplify(right),var,count+1);
			}
			else if (canDistribute(left))
				return solveForInternal(distribute(left),right,var,count+1);
			else {
				String coeff = leftEx.simplifiedCoefficient().toString();
				return solveForInternal(divide(left,coeff),divide(right,coeff),var,count+1);
			}
		}
		catch (ParseException err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
		catch (TokenMgrError err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
	}

	public String runScript(String[] script,String expEq) throws BadExpressionError, NoSuchFieldException{
		if (expEq.indexOf('=') >=0) {
			Equation form = new Equation(expEq);
			Expression.setMaintainVars(maintainVarList);
			String val = form.evalQuery(script).getStringValue();
			Expression.setMaintainVars(false);
			return val;
		}
		else {
			try {
				Expression form = parse(expEq);
				Expression.setMaintainVars(maintainVarList);
				Queryable result = form.evalQuery(script);
				Expression.setMaintainVars(false);
				String returnVal;
				if (result instanceof Expression)
					returnVal = convertOutput((Expression)result);
				else
					returnVal = result.getStringValue();
				return returnVal;
			}
			catch (ParseException err) {
				throw new BadExpressionError(expEq);
			}
			catch (TokenMgrError err) {
				throw new BadExpressionError(expEq);
			}
		}
	}

	public String runScript(String script,String expEq) throws BadExpressionError, NoSuchFieldException {
		if (expEq.indexOf('=') >=0) {
			Equation form = new Equation(expEq);
			Expression.setMaintainVars(maintainVarList);
			String val = form.evalQuery(script).getStringValue();
			Expression.setMaintainVars(false);
			return val;
		}
		else {
			try {
				Expression form = parse(expEq);
				Expression.setMaintainVars(maintainVarList);
				Queryable result = form.evalQuery(script);
				Expression.setMaintainVars(false);
				String returnVal;
				if (result instanceof Expression)
					returnVal = convertOutput((Expression)result);
				else
					returnVal = result.getStringValue();
				return returnVal;
			}
			catch (ParseException err) {
				throw new BadExpressionError(expEq);
			}
			catch (TokenMgrError err) {
				throw new BadExpressionError(expEq);
			}
		}
	}
		
	public Queryable[] runArrayScript(String[] script,String expEq) throws BadExpressionError, NoSuchFieldException {
		if (expEq.indexOf('=') >=0) {
			Equation form = new Equation(expEq);
			Expression.setMaintainVars(maintainVarList);
			Queryable[] val = form.evalQuery(script).getArrayValue();
			Expression.setMaintainVars(false);
			return val;
		}
		else {
			try {
				Expression form = parse(expEq);
				Expression.setMaintainVars(maintainVarList);
				Queryable[] val = form.evalQuery(script).getArrayValue();
				Expression.setMaintainVars(false);
				return val;
			}
			catch (ParseException err) {
				throw new BadExpressionError(expEq);
			}
			catch (TokenMgrError err) {
				throw new BadExpressionError(expEq);
			}
		}
	}

	public Queryable[] runArrayScript(String script,String expEq) throws BadExpressionError, NoSuchFieldException {
		if (expEq.indexOf('=') >=0) {
			Equation form = new Equation(expEq);
			Expression.setMaintainVars(maintainVarList);
			Queryable[] val = form.evalQuery(script).getArrayValue();
			Expression.setMaintainVars(false);
			return val;
		}
		else {
			try {
				Expression form = parse(expEq);
				Expression.setMaintainVars(maintainVarList);
				Queryable[] val = form.evalQuery(script).getArrayValue();
				Expression.setMaintainVars(false);
				return val;
			}
			catch (ParseException err) {
				throw new BadExpressionError(expEq);
			}
			catch (TokenMgrError err) {
				throw new BadExpressionError(expEq);
			}
		}
	}

	public Vector equationVariablesUsed(String equation) throws BadExpressionError{
		try{
			Equation eq = new Equation(equation);
			if(eq.getRight() == null){
				throw new ParseException();
			}

			Vector lVars = eq.getLeft().variablesUsed();
			Vector rVars = eq.getRight().variablesUsed();

			for(int i=0;i<rVars.size();i++){
				lVars.addElement(rVars.elementAt(i));
			}
			rVars.removeAllElements();
			rVars = null;
			return lVars;
		}
		catch (ParseException err) {
			throw new BadExpressionError(equation);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(equation);
		}
	}

	public Equation getPattern(String equation) throws BadExpressionError {
		int equalPos = equation.indexOf('=');
		String left = equation.substring(0,equalPos);
		String right = equation.substring(equalPos+1);
//		trace.out("Left is "+left);
//		trace.out("Right is "+right);
		try {
			Expression leftEx = parse(left);
			Expression rightEx = parse(right);
			Equation form = new Equation(leftEx,rightEx);
//			trace.out("Expression pattern is: "+form.getPattern());
			return form;
		}
		catch (ParseException err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
		catch (TokenMgrError err) {
			if (!isExpression(left))
				throw new BadExpressionError(left);
			else
				throw new BadExpressionError(right);
		}
	}
	
	//patternMatches returns true if the two equations or expression have the same "form" (ax, ax+b=c, etc.)
	public boolean patternMatches(String eq1, String eq2) throws BadExpressionError {
		Equation first = Equation.makeForm(eq1);
		Equation second = Equation.makeForm(eq2);
		boolean matches = first.patternMatches(second);
		return matches;
	}

	/*algebraicMatches applies to generic relations (equations +
      inequalities).  It returns true if the relations are
      algebraically equivalent.*/
	public boolean algebraicMatches(String rel1,String rel2) throws BadExpressionError{
		Relation first = new Relation(rel1);
		Relation second = new Relation(rel2);
		boolean matches = first.matches(second);
		return matches;
	}

	public int complexity(String ex) throws BadExpressionError {
		try {
			Expression oneEx = parse(ex);

			int result;
			result = oneEx.complexity();
			return result;
		}
		catch (ParseException err) {
			throw new BadExpressionError(ex);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(ex);
		}
	}

	//getStateDescription returns the value of all state variables (assumed to be public)
	//This is sometimes useful in debugging
	public String getStateDescription() {
		try {
			Class smClass = Class.forName("cmu.sm.SymbolManipulator");
			Field[] fields = smClass.getDeclaredFields();
			String result = "SM: \n";
			for (int i=0;i<fields.length;++i) {
				Field thisField = fields[i];
				if (Modifier.isPublic(thisField.getModifiers()) &&
					!Modifier.isStatic(thisField.getModifiers())) {
					try {
						result+=thisField.getName()+": "+thisField.get(this)+"\n";
					}
					catch (IllegalAccessException err) {
						result+=err.toString()+"\n";
					}
				}
			}
			fields = null;
			return result;
		}
		catch (ClassNotFoundException err) {
			return "Can't get information about SymbolManipulator class";
		}
	}
	
	private static String getBoolChar(boolean b) {
		if (b)
			return "T";
		else
			return "F";
	}

	private static boolean getCharBool(char c){
		return String.valueOf(c).equals("T");
	}
	
	//getState returns a string indicating the state of the SM
	//This is used in cacheing routines, so you can check to see if the state changed
	//the string returned is not intended to be readable.
	public String getState() {
		StringBuffer ret = new StringBuffer(32);
		ret.append(getBoolChar(autoSimplify));
		ret.append(getBoolChar(autoStandardize));
		ret.append(getBoolChar(autoCombineLikeTerms));
		ret.append(getBoolChar(autoReduceFractions));
		ret.append(getBoolChar(autoMultiplyThrough));
		ret.append(getBoolChar(autoExpandExponent));
		ret.append(getBoolChar(autoDistribute));
		ret.append(getBoolChar(distributeDenominator));
		ret.append(getBoolChar(allowDoubleSigns));
		ret.append(getBoolChar(allowExtraParens));
		ret.append(getBoolChar(allowNegativeExponents));
		ret.append(getBoolChar(autoSort));
		ret.append(String.valueOf(outputType));
		ret.append(((outputFormatter == null) ? "null" : outputFormatter.toString()));
		ret.append(String.valueOf(getCompareDecimalPlaces()));
		ret.append(String.valueOf(getPrintDecimalPlaces()));

		return ret.toString();
	}

	/*this is used to return to some previous state gotten from
      getState() (just the first 12 boolean elements, tho)*/
	public void setState(String state){
		if(state.length() >= 12){
			autoSimplify = getCharBool(state.charAt(0));
			autoStandardize = getCharBool(state.charAt(1));
			autoCombineLikeTerms = getCharBool(state.charAt(2));
			autoReduceFractions  = getCharBool(state.charAt(3));
			autoMultiplyThrough= getCharBool(state.charAt(4));
			autoExpandExponent  = getCharBool(state.charAt(5));
			autoDistribute = getCharBool(state.charAt(6));
			distributeDenominator = getCharBool(state.charAt(7));
			allowDoubleSigns = getCharBool(state.charAt(8));
			allowExtraParens = getCharBool(state.charAt(9));
			allowNegativeExponents = getCharBool(state.charAt(10));
			autoSort = getCharBool(state.charAt(11));
		}
	}

	/*turns on/off the individual boolean settings that collectively
      make up the autoSimplify setting*/
	public void setIndividualAutoSimp(boolean val){
		allowExtraParens = allowDoubleSigns = !val;
		autoExpandExponent =
			autoMultiplyThrough =
			autoCombineLikeTerms =
			autoReduceFractions = val;
	}

	public void setIndividualAutoStand(boolean val){
		autoDistribute = val;
		setIndividualAutoSimp(val);
	}
}

//doSimplificationStep was a good idea, but reflection is damn slow

	//doSimplificationStep uses reflection to do the requested simplification
	//we do this in a loop, since some simplification steps won't be complete on one call
/*	private String doSimplificationStep(String step, String expString) throws BadExpressionError {
		try {
			Class smClass = Class.forName("cmu.sm.Expression");
			Class[] parameters = new Class[0];
			Method simpMethod = smClass.getMethod(step,parameters);
			String canMethodName = "can"+String.valueOf(step.charAt(0)).toUpperCase()+step.substring(1);
			Method canMethod = smClass.getMethod(canMethodName,parameters);
			try {
				Expression theExpression = parse(expString);
				Object[] args = new Object[0];
				int count = 0;
				if (((Boolean)(canMethod.invoke(theExpression,args))).booleanValue()) {
					theExpression = (Expression)(simpMethod.invoke(theExpression,args));
				}
				return convertOutput(theExpression);
			}
			catch (ParseException err) {
				throw new BadExpressionError(expString);
			}
			catch (TokenMgrError err) {
				throw new BadExpressionError(expString);
			}
		}
		catch (ClassNotFoundException err) {
			trace.out(err);
			return expString;
		}
		catch (NoSuchMethodException err) {
			trace.out(err);
			return expString;
		}
		catch (IllegalAccessException err) {
			trace.out(err);
			return expString;
		}
		catch (InvocationTargetException err) {
			Throwable exception = err.getTargetException();
			if (exception instanceof ParseException)
				throw new BadExpressionError(expString);
			else if (exception instanceof TokenMgrError)
				throw new BadExpressionError(expString);
			else {
				trace.out(err+exception.toString());
				return expString;
			}
		}			
	}
*/
