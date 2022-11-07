package edu.cmu.old_pact.cmu.sm;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

//Number expressions -- store any kind of number (except fractions)

public class NumberExpression extends NumericExpression {
	private Number value;
	private int numberType;

	private int sigfigs;
	private static boolean useSigFigs = false;

	private static double epsilon = .00001; //remainders below this considered to be 0
	public static final int typeInt = 0;
	public static final int typeLong = 1;
	public static final int typeFloat = 2;
	public static final int typeDouble = 3;
	public static final int defaultMathMLDecimalPlaces = 4; // decimal places to use in mathML
	public static final int defaultPrintDecimalPlaces = 8; //decimal places to use in printing
	private static NumberFormat printFormat;
	private static int defaultCompareDecimalPlaces = 2; //decimal places to consider when comparing numbers
	private static NumberFormat compareFormat;
	
	//static initializer, used to set parameters of number format
	static {
		printFormat = NumberFormat.getInstance();
		printFormat.setMaximumFractionDigits(defaultPrintDecimalPlaces);
		printFormat.setMinimumFractionDigits(0);
		printFormat.setGroupingUsed(false);
		compareFormat = NumberFormat.getInstance();
		compareFormat.setMaximumFractionDigits(defaultCompareDecimalPlaces);
		compareFormat.setMinimumFractionDigits(defaultCompareDecimalPlaces);
	}
	
	//default constructor creates 1
	public NumberExpression() {
		this(1);
	}
	
	public NumberExpression (Number val,int numType) {
		value = val;
		numberType = numType;
		sigfigs = countSigFigs(value);
	}

	/*this constructor maintains the type of the number passed in
      (i.e. it represents "3.0" as a float internally, rather than
      converting it to an integer).*/
	public NumberExpression(String newVal) throws NumberFormatException{
		String val = stripCommas(newVal);
		//trace.out("NE: new: " + val);
		try{//integer
			value = Integer.valueOf(val);
			numberType = typeInt;
		}
		catch(NumberFormatException nfe1){
			try{//long
				value = Long.valueOf(val);
				numberType = typeLong;
			}
			catch(NumberFormatException nfe2){
				/*we skip float here because it was doing silly stuff
                  like converting 0.8 to 0.80000001*/
				//double
				/*if double fails, the exception should make it to the
				  outside world*/
				value = Double.valueOf(val);
				numberType = typeDouble;
				/*if we don't care about sig figs, we convert 3.0 to 3*/
				if(!getUseSigFigs() &&
				   (value.longValue() == value.doubleValue())){
					value = new Long(value.longValue());
					numberType = typeLong;
				}
			}
		}
		sigfigs = countSigFigs(val);
	}

	//This constructor tries to figure out the type of the number
	public NumberExpression (Number val) {
		int iVal = val.intValue();
		double dVal = val.doubleValue();
		long lVal = val.longValue();
		float fVal = val.floatValue();
		
		if (dVal == lVal) { //not a float
			if (iVal == lVal)
				numberType = typeInt;
			else
				numberType = typeLong;
		}
		else if (dVal == fVal)
			numberType = typeFloat;
		else
			numberType = typeDouble;
		value = val;
		sigfigs = countSigFigs(val);
	}
	
	public NumberExpression (int val) {
		value = new Integer(val);
		numberType = typeInt;
		sigfigs = countSigFigs(value);
	}
	
	public NumberExpression (long val) {
		value = new Long(val);
		numberType = typeLong;
		sigfigs = countSigFigs(value);
	}

	public NumberExpression (float val) {
		int intPart = (int)Math.floor(val);
		float rem = val-intPart;
		if (rem < epsilon) {
			value = new Integer(intPart);
			numberType = typeInt;
		}
		else {
			value = new Float(val);
			numberType = typeFloat;
		}
		sigfigs = countSigFigs(value);
	}

	public NumberExpression (double val) {
		long longPart = (long)Math.floor(val);
		double rem = val-longPart;
		if (Math.abs(rem) < epsilon) {
			if (longPart > Integer.MAX_VALUE || longPart < Integer.MIN_VALUE) {
				value = new Long(longPart);
				numberType = typeLong;
			}
			else {
				value = new Integer((int)Math.floor(val));
				numberType = typeInt;
			}
		}
		else {
			value = new Double(val);
			numberType = 3;
		}
		sigfigs = countSigFigs(value);
	}
	
	public static int countSigFigs(Number num){
		return countSigFigs(num.toString());
	}

	public static int countSigFigs(String num){
		int ret = num.length();
		if(num.indexOf('.') != -1){
			ret -= 1;
		}
		return ret;
	}

	public static void setUseSigFigs(boolean val){
		useSigFigs = val;
	}

	public static boolean getUseSigFigs(){
		return useSigFigs;
	}

	public static void setPrintDecimalPlaces(int places) {
		printFormat.setMaximumFractionDigits(places);
		printFormat.setMinimumFractionDigits(0);
	}
	
	public static int getPrintDecimalPlaces() {
		return printFormat.getMaximumFractionDigits();
	}

	public static void setPrintMinDecimalPlaces(int places){
		printFormat.setMinimumFractionDigits(places);
	}

	public static int getPrintMinDecimalPlaces(){
		return printFormat.getMinimumFractionDigits();
	}

	public static void setCompareDecimalPlaces(int places) {
		compareFormat.setMaximumFractionDigits(places);
		compareFormat.setMinimumFractionDigits(places);
	}
	
	public static int getCompareDecimalPlaces() {
		return compareFormat.getMaximumFractionDigits();
	}

	public int getNumberType() {
		if (this.value instanceof Long)
			return 1;
		else if (this.value instanceof Integer)
			return 0;
		else if (this.value instanceof Double)
			return 3;
		else if (this.value instanceof Float)
			return 2;
		else
			throw new NumberFormatException("Number expression has unknown subtype");
	}
	
	public Number getValue() {
		return value;
	}
	
	public boolean isIntegerType() {
		int numType;
		
		numType = getNumberType();
		if (numType == 0 || numType == 1)
			return true;
		else
			return false;
	}
	
	public boolean isFloatType() {
		int numType;
		
		numType = getNumberType();
		if (numType == 2 || numType == 3)
			return true;
		else
			return false;
	}
	
	public boolean isFractionType() {
		return false;
	}
	
	public double doubleValue() {
		return value.doubleValue();
	}

	public Expression negate() {
		NumberExpression newVal;
		if(doubleValue() == 0){
			/*negating zero should return -0 (a TermExpression)*/
			return super.negate();
		}
		else{
			if (numberType == 0)
				newVal = new NumberExpression(0-value.intValue());
			else if (numberType == 1)
				newVal = new NumberExpression(0-value.longValue());
			else if (numberType == 2)
				newVal = new NumberExpression(0.0-value.floatValue());
			else if (numberType == 3)
				newVal = new NumberExpression(0.0-value.doubleValue());
			else	
				newVal = new NumberExpression(0);
			return newVal;
		}
	}
	
	public Expression reciprocal() {
		NumberExpression numerator = new NumberExpression(1);

		return new FractionExpression(numerator,this).adjustNegative();
	}
	
	//special method to test for -1
	public boolean isNegOne() {
		if (isIntegerType() && value.intValue() == -1)
			return true;
		else
			return false;
	}
	
	public boolean isOne() {
		if (isIntegerType() && value.intValue() == 1)
			return true;
		else
			return false;
	}

	public boolean isZero() {
		if (isIntegerType() && value.intValue() == 0)
			return true;
		else
			return false;
	}
	
        public NumericExpression numericSimplifiedCoefficient(){
            return this;
        }

	public Expression exceptNumericSimplifiedCoefficient(){
		return null;
	}

	public Expression simplifiedCoefficient() {
		return this;
	}
	
	public Expression exceptSimplifiedCoefficient() {
		return null;
	}
	
	public boolean algebraicEqual(Expression ex){
		if(ex instanceof NumericExpression){
			double exval = ((NumericExpression)ex).doubleValue();
			boolean ret = compareFormat.format(doubleValue()).equals(compareFormat.format(exval));
			return ret;
		}
		else if(ex instanceof ConstantExpression){
			NumericExpression exnum = (NumericExpression)ex.substConstants();
			boolean ret = compareFormat.format(doubleValue()).equals(compareFormat.format(exnum.doubleValue()));
			return ret;
		}
		else{
			return super.algebraicEqual(ex);
		}
	}

	public boolean exactEqual(Expression ex) {
		if (ex instanceof NumberExpression) {
			NumberExpression numEx = (NumberExpression)ex;
			boolean ret = (compareFormat.format(doubleValue()).equals(compareFormat.format(numEx.doubleValue())));
			return ret;
		}
		else
			return false;
	}
	
	public static String stripCommas(String input){
		StringBuffer ret = new StringBuffer(input.length());
		StringTokenizer st = new StringTokenizer(input,",");
		while(st.hasMoreTokens()){
			ret.append(st.nextToken());
		}

		return ret.toString();
	}

	public Queryable getProperty(String prop) throws NoSuchFieldException {
		//"sign" is a part-attribute of a NumericExpression
		if (prop.equalsIgnoreCase("sign")) {
			String signString;
			if (isNegative())
				signString = "-";
			else
				signString = "+";
			return new ExpressionPart("sign",this,signString);
		}
		//so is "except sign"
		else if (prop.equalsIgnoreCase("except sign")) {
			String noSignString = this.absoluteValue().toString();
			return new ExpressionPart("except sign",this,noSignString);
		}
		else
			return super.getProperty(prop);
	}

	public void setProperty (String prop, String setVal) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("sign")) {
			if (setVal.equals("-") && !isNegative())
				value = negate().getNumberValue();
			else if (setVal.equals("+") && isNegative())
				value = negate().getNumberValue();
		}
		else if (prop.equalsIgnoreCase("except sign")) {
			try {
				Number newValue = new DecimalFormat().parse(setVal);
				if (isNegative()) {
					NumberExpression tempNum = new NumberExpression(0-newValue.doubleValue());
					newValue = tempNum.getValue();
				}
				value = newValue;
			}
			catch (java.text.ParseException e) {
				trace.out("can't parse "+setVal);
			}
		}
		else
			super.setProperty(prop,setVal);
	}

	public String toASCII(String openParen,String closeParen) {
		if (Expression.printStruct)
			return debugForm();

		String finalString;
		if(useSigFigs){
			if(doubleValue() < 0.01 ||
			   doubleValue() >= 10000000.0){
				/*numbers outside these limits are converted to
                  scientific notation by default; here we use
                  BigDecimal to avoid that.*/
				/*yes, I know that the real lower limit is 0.001, not
                  0.01.  But there is a very weird bug converting
                  doubles to strings: trace.out(.003) gives
                  0.0030.  <shrug>  So we handle that with BigDecimal
                  instead.*/
				BigDecimal bd = new BigDecimal(NumberExpression.roundDecimalPlaces(value.doubleValue(),
																				   printFormat.getMaximumFractionDigits()));
				/*we're pretty much guaranteed to encounter
                  floating-point sillyness here ... we just chop off
                  the appropriate number of decimal places*/
				finalString = bd.toString();
				int curSF = countSigFigs(finalString);
				if(curSF > sigfigs){
					bd = bd.setScale(bd.scale() - (curSF-sigfigs),
									 BigDecimal.ROUND_HALF_UP);
					finalString = bd.toString();
				}
			}
			else{
				finalString = String.valueOf(NumberExpression.roundDecimalPlaces(value.doubleValue(),printFormat.getMaximumFractionDigits()));
			}
			if(countSigFigs(finalString) == sigfigs + 1){
				/*this could be one of two things: 4 being printed as
                  4.0, in which case we want to get rid of the ".0"
                  bit; or .5 being printed as 0.5, in which case we
                  want to leave it alone*/
				if(value.longValue() == value.doubleValue()){
					finalString = String.valueOf(value.longValue());
				}
			}
			else if(countSigFigs(finalString) > sigfigs){
				//code to convert to exponent would go here
				trace.out("NE.tA: warning: too many sig figs: " + finalString +
								   " (should be " + sigfigs + ")");
			}
			/*only start adding zeros if we aren't dealing with
              scientific notation (e.g. 3.0E-5)*/
			else if(finalString.indexOf('E') == -1){
				boolean hasDP = finalString.indexOf('.') > 0;
				while(countSigFigs(finalString) < sigfigs){
					if(!hasDP){
						finalString += ".";
						hasDP = true;
					}
					finalString += "0";
				}
			}
		}
		else if ((numberType == typeDouble) ||
			(numberType == typeFloat) ||
			(getPrintMinDecimalPlaces() != 0)) {
			finalString = String.valueOf(NumberExpression.roundDecimalPlaces(value.doubleValue(),printFormat.getMaximumFractionDigits()));
			while(countDecPlaces(finalString) < getPrintMinDecimalPlaces()){
				//I think there will always be a decimal point here already ...
				finalString += "0";
			}
		}
		else if (numberType == typeInt)
			finalString = String.valueOf(value.intValue());
		else if (numberType == typeLong)
			finalString = String.valueOf(value.longValue());
//		else if (numberType == typeFloat)
//			finalString = printFormat.format(value.floatValue());
//		else if (numberType == typeDouble)
//			finalString = printFormat.format(value.doubleValue());
		/*else if (numberType == typeFloat) {
			finalString = String.valueOf(NumberExpression.roundDecimalPlaces(value.floatValue(),printFormat.getMaximumFractionDigits()));
			}*/
		else
			finalString = "???";
//		if (numberType == typeFloat || numberType == typeDouble)
//			finalString = roundDecimal(finalString);

		return finalString;
	}

	public static int countDecPlaces(String num){
		if(num.indexOf('.') != -1){
			return (num.length()-1) - num.indexOf('.');
		}
		else{
			return 0;
		}
	}
	
	//MRJ 2.1.4 *still* doesn't round off numbers properly (it gives sqrt(154.88)=12.44 instead of 12.45 to 2 places), so
	//we do roundoff on our own.
	private static float roundDecimalPlaces(float num,int places) {
		long offset = Math.round(Math.pow(10.0,places));
		float mult = num*offset;
		int whole = Math.round(mult);
		float fin = (float)((double)whole/offset);
		return fin;
	}
	
	private static double roundDecimalPlaces(double num,int places) {
		long offset = Math.round(Math.pow(10.0,places));
		double mult = num*offset;
		long whole = Math.round(mult);
		double fin = ((double)whole/offset);
		return fin;
	}
	
	public String toMathML() {
		int min = getPrintMinDecimalPlaces();
		setPrintDecimalPlaces(defaultMathMLDecimalPlaces);
		setPrintMinDecimalPlaces(min);
		StringBuffer sb = new StringBuffer(mathmlSBsize);
		String finalML;
		if (isNegative()) {
			sb.append("<mrow>");
			sb.append(addMathMLPartAttributes("sign","<mo form='prefix'>-</mo>"));
			sb.append(addMathMLPartAttributes("exceptsign","<mn>"+negate().toString()+"</mn>"));
			sb.append("</mrow>");
			finalML = addMathMLAttributes(sb.toString());
		}
		else {
			String exceptSignML = addMathMLPartAttributes("except sign","<mn>"+toString()+"</mn>");
			finalML = addMathMLAttributes(exceptSignML);
		}
		setPrintDecimalPlaces(defaultPrintDecimalPlaces);
		setPrintMinDecimalPlaces(min);
		return finalML;
	}

	public String debugForm() {
		return "[NumExp: "+numberType+"::"+String.valueOf(value.doubleValue())+"]";
	}
	
	public static final double getEpsilon() {
		return epsilon;
	}
}
