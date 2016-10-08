package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** SF Number
 * 
 * @author Reid Van Lehn <rvanlehn@mit.edu>
 *
 * New number class which incorporates significant figure functionality
 * into the numbers. Handles unusual number cases (i.e. 1000.) to ensure
 * ability to determine sig figs. Wraps a string (though can be created from
 * primitive types as well). Does not currently include addition/subtraction 
 * functionality.
 *
 */

public class SFNumber {

	private boolean isDecimal;
	private boolean hasExponent; 
	private String decimalPart; //String rep of decimal part of number
	private String exponentPart; //String rep of exponent part, without 'E' 
	private int sigFigs; //number of sig figs for this number
	private boolean exactNumber = false; //if true, count as infinite sig figs 
	
	
	/**
	 * Constructor. First checks to ensure that number is correctly formatted;
	 * if so, stores both decimal and exponent parts separately and records number
	 * of sig figs.
	 * 
	 * @param input
	 */
	public SFNumber(String input) {
		//Ensure input is valid number
		if (!checkInput(input)) {
			throw new NumberFormatException(
				"Invalid input: " + input);
		}
		//Parse the input and save
		isDecimal = input.indexOf(".") > -1;
		input = input.toUpperCase();
		hasExponent = input.indexOf("E") > -1;
		//Store numeric representation
		storeNumber(input, hasExponent);
		sigFigs = length();
	}

	/**
	 * Stroes the decimal and exponent parts that are explicitly given.
	 * 
	 * @param decPart
	 * @param expPart
	 */
	public SFNumber(String decPart, String expPart) {
		if (!checkInput(decPart+expPart))
				throw new NumberFormatException("Invalid input: " +
											    decPart + "E" + expPart);
		//parse input and save
		isDecimal = decPart.indexOf(".") > -1;
		hasExponent = (expPart != null) && !expPart.equals("0");
		decimalPart = decPart;
		exponentPart = expPart;
		//Ensure proper scientific notation
		convertToScientificNotation();
		sigFigs = length();
	}
	
	/**
	 * Determines whether number is exact or not.
	 * 
	 * @param input
	 * @param bExact
	 */
	public SFNumber(String input, boolean bExact) {
		this(input);
		exactNumber = bExact;
	}

	public SFNumber(int number, boolean bExact) {
		this(String.valueOf(number));
		exactNumber = bExact;
	}
	
	public SFNumber(String decPart, String expPart, boolean bExact) {
		this(decPart, expPart);
		exactNumber = bExact;
	}

	/** 
	 *  Returns false if number contains a character other than numbers, eE, ., +-
	 * 
	 * @param input
	 * @return false if there are illegal characters, true otherwise
	 */
	public static boolean checkInput(String input) {
		//First digit cannot be 0 unless it's a decimal
		if (input.charAt(0) == '0' && input.charAt(1) != '.')
			return false;
		//Matches any invalid character
		String regX = "[^0-9+eE\056\055]";
		Pattern p = Pattern.compile(regX);
		Matcher m = p.matcher(input);
		return(!m.find());
	}
	
	/**
	 * Stores components of string as Doubles for doing arithmetic
	 * 
	 * @param input
	 * @param hasExp
	 */
	public void storeNumber(String input, boolean hasExp) {
		//First find non-exponent 
		if (hasExp) {
			int expIndex = input.indexOf("E");
			decimalPart = input.substring(0, expIndex);
			exponentPart = input.substring(expIndex+1);
		}
		else {
			decimalPart = input;
			exponentPart = "0";
		}
		convertToScientificNotation(); //stores as proper sci notation
	}
	
	/**
	 *  Algorithm: number types
	 *  
	 *  124.999
	 *  1.5125
	 *  0.00052515
	 *  0.4244
	 *  1000
	 *  1000.
	 * 
	 * If it has a decimal point, move to left/right such that 
	 * single digit to left. Increment for moving left, decrement for moving right
	 * Assume exponent of 0 if there is no exponent.
	 * 
	 * Leading 0s have no impact on sig figs, so don't worry about them
	 * Trailing 0s: Trim and increment for each trimmed 0
	 */
	public void convertToScientificNotation() {
                       
		int goalIndex = -1; //location in string where decimal point eventually ends up
		int decIndex = -1; //Index of decimal point
		int exponentIncrement = 0; //how much to increment exponent by
		StringBuffer buffer = new StringBuffer(decimalPart);
		if (length() == 1)
			return; //single digit
		if (isDecimal) {
			decIndex = buffer.indexOf(".");
			if (decIndex == 1 && buffer.charAt(0) != '0') {
				//Already in correct position
				return;
			}
			else if (decIndex == 1) {
				//leading 0, so move decimal point to right
				for (int i=decIndex+1; i<buffer.length(); i++) {
					exponentIncrement--;
					if (buffer.charAt(i) != '0') {
						goalIndex = i;
						break;
					}
				}
			}
			else if (decIndex > 1) {
				//move to left
				goalIndex = 1;
				exponentIncrement += decIndex - 1;
			}
		}
		else {
			//Not a decimal, so move fake decimal from far right 
			//Trim trailing 0s as well. (to the right or to the left?)
                        
                        //trim trailing zeros to the right
			for (int j=buffer.length()-1; j>-1; j--) {
				exponentIncrement++;
				if (buffer.charAt(j) != '0') { //what if it's not a trailing 0??
					//Set decimal point index there for purpose of measuring difference
					goalIndex = j;
					exponentIncrement += j - 1;
					break;
				}
				//otherwise, remove 0
				buffer.deleteCharAt(j);
			}
                        
                        //gustavo: now, we need to move the decimal point further
                        //until there is only one number to its left
			for (int j=buffer.length()-1; j>0; j--) {
			    //exponentIncrement++;

                            //what if it's not a trailing 0??
			    //Set decimal point index there for purpose of measuring difference
			    goalIndex = j; //i.e. goalIndex = 1;
			}
                        
		}
		//Now move decimal point and change exponent
		if (isDecimal) {
			buffer.deleteCharAt(decIndex);
			isDecimal = false; //no decimal for moment
		}
		if (buffer.length() != 1) {
			try {
				if (goalIndex != buffer.length()) {
                                        //this is not necessarily inserting in the SciNo place
                                        //goalIndex is just the buffer.length - 1
                                        buffer.insert(goalIndex, ".");
					isDecimal = true;
				}
				//delete leading 0s	
				buffer.delete(0, buffer.indexOf(".")-1); //this is bad if buffer looks like "6015"
			} catch (StringIndexOutOfBoundsException e) {
				buffer.delete(0, buffer.length()-1);
			}
		}
		decimalPart = buffer.toString();
		//increment exponent
		int exponentValue = Double.valueOf(exponentPart).intValue();
		exponentValue += exponentIncrement;
		exponentPart = String.valueOf(exponentValue);
		hasExponent = exponentValue != 0;
        }
	
	/*
	//Returns the number of significant figures for this number.
	public int determineSigFigs() {
		int numSigFigs = 0; 
		//will return either normal notation or scientific notation
		char[] chars = decimalPart.toCharArray(); //for easier parsing
		//Find index of decimal point
		int pointIndex = decimalPart.indexOf(".");
		if (pointIndex == -1) {
			//no decimal point
			pointIndex = chars.length;
		}
		boolean firstNonZero = false; //if true, start counting sig. figs
		//Automatically true if is a decimal number and there is a left side
		// i.e. for 14.000 but not 0.0055
		if (isDecimal && pointIndex >= 2)
			firstNonZero = true;
		//Start by considering left side of decimal point
		for (int i=(pointIndex-1); i>-1; i--) {
			char curChar = chars[i];
			if (!firstNonZero) {
				if (curChar != '0') {
					firstNonZero = true;
					numSigFigs++;
				}
			}
			else {
				//count all as sig-figs after first non-zero
				numSigFigs++;
			}
		}
		//Now count to right of decimal point
		for (int j=(pointIndex+1); j<chars.length; j++) {
			char curChar = chars[j];
			if (!firstNonZero) {
				//only has decimal values
				if (curChar != '0') {
					firstNonZero = true;
					numSigFigs++;
				}		
			}
			else {
				numSigFigs++;
			}
		}
		return numSigFigs;
	}

*/
	
	/** 
	 * Returns this number rounded to the desired number of significant figures
	 */
	public SFNumber round(int desiredSigFigs) {
		//First check to see if it should be rounded
		//<= not <  -tmandel
		int numSigFigs = getSigFigs();
		if (numSigFigs <= desiredSigFigs) { 
			sigFigs=desiredSigFigs;//tmandel: must update the sig figs even if more precise
			return this; //should never happen, really
		}
		boolean foundFirstDigit = false; //true when first non-trailing 0 found
		char[] chars = decimalPart.toCharArray(); //for easier parsing
		int precisionIndex = -1; //index of digit giving desired num SF
		/* If number has decimal point, iterate from rightmost side
		to leftmost side, subtracting for every value (since leading 0s won't 
		ever be reached due to insignificance)
		If number does not have decimal point, iterate from back to find first
		non-zero digit, then decrement for rest of digits.
		*/
		//Increment numSigFigs so that finding first sig fig does not decrement improperly
		numSigFigs++;
		for (int i=chars.length-1; i>-1; i--) {
			if (isDecimal && chars[i] != '.')
				numSigFigs--;
			else if (chars[i] != '.'){
				if (!foundFirstDigit && chars[i] != '0') {
					numSigFigs--;
					foundFirstDigit = true;
				}
				else if (foundFirstDigit)
					numSigFigs--;
			}
			//Check to see if loop is done.
			if (numSigFigs == desiredSigFigs) {
				//Record index where this became true
				precisionIndex = i;
				break;
			}
			else if (numSigFigs ==0) {
				throw new NumberFormatException("Error rounding " + this);
			}
		}
		//Now that we know precision index, round based on next digit 
		//Should not go out of bounds due to initial check
		int roundedDigit = Character.getNumericValue(chars[precisionIndex]);
		char nextChar = chars[precisionIndex+1];
		if (nextChar == '.')
			nextChar = chars[precisionIndex+2];
		int nextDigit = Character.getNumericValue(nextChar);
		/* Perform implementation of BigDecimal 'HALF_EVEN' Rounding
		 * Rounds up for nextDigit > 5 and down for nextDigit < 5.
		 * If nextDigit = 5, round to nearest even number
		 * So 1.35 = 1.4, 1.45 = 1.4
		 */
		if (nextDigit > 5) 
			roundedDigit++;
		else if (nextDigit==5) {
			if ((roundedDigit % 2) == 1) {
				//Round up to nearest even
				roundedDigit++;
			}
		}
		//Convert back to char
		chars[precisionIndex] = (char)(roundedDigit + '0');
		//Check to make sure answer that should be like 100. isn't 100 
		if (chars[precisionIndex] == '0' && chars[precisionIndex+1] == '.') {
			//Simply increment precision index to include decimal
			precisionIndex++;
		}
		//Rebuild new SFNumber
		StringBuffer sb = new StringBuffer();
		for (int j=0; j<(precisionIndex+1); j++) {
			sb.append(chars[j]);
		}
		//Reinclude trailings 0s if applicable
		if (!isDecimal || (precisionIndex < decimalPart.indexOf("."))) {
			//add trailing 0s
			for (int l=precisionIndex+1; l<chars.length; l++) {
				if (chars[l] == '.')
					break;
				sb.append(0);
			}
		}
		return (new SFNumber(sb.toString(), exponentPart));
	}
	
	/**
	 * Multiplies thus number by the multiplier. Optionally
	 * rounds to the minimum sig figs between the multiplier and 
	 * the original number if bExact is false. 
	 * 
	 * Multiplies decimals together, adds exponents, then reconverts to
	 * scientific notation. 
	 * 
	 * TODO: Optimize this method
	 * 
	 * @param multiplier
	 * @param bExact
	 * @return Product of multiplication, possibly rounded
	 */
	public SFNumber multiplyBy(SFNumber multiplier, boolean bExact) {
		int numSF = 0;
		//If inexact, use min number of sig figs
		if (!bExact) {
			// Find minimum significant figures of each
			int sfMult = multiplier.getSigFigs();
			int thisSF = this.getSigFigs();
			numSF = Math.min(sfMult, thisSF);
		}
		//Now retrieve decimal parts of each and convert to Doubles
		Double thisDecPart = new Double(decimalPart);
		Double multDecPart = new Double(multiplier.getDecimalPart());
		//Multiply double parts together
		double decProd = thisDecPart.doubleValue() * multDecPart.doubleValue();
		//Now add exponent parts
		Double thisExpPart, multExpPart;
		if (hasExponent)
			thisExpPart = new Double(exponentPart);
		else
			thisExpPart = new Double(0d);
		if (multiplier.getExponent() != null)
			multExpPart = new Double(multiplier.getExponent());
		else 
			multExpPart = new Double(0d);
		int sum = thisExpPart.intValue() + multExpPart.intValue();
		// create new number
		SFNumber product = new SFNumber(String.valueOf(decProd),
											String.valueOf(sum));
		//optionally round
		if (!bExact) 
			return (product.round(numSF));
		else
			return product;
	}
	
	/** 
	 * Same principle as multiplyBy.
	 * 
	 * @param divisor
	 * @param bExact
	 * @return
	 */
	public SFNumber divideBy(SFNumber divisor, boolean bExact) {
		int numSF = 0;
		//If inexact, use min sig figs
		if (!bExact) {
			// Find minimum significant figures of each
			int sfDiv = divisor.getSigFigs();
			int thisSF = this.getSigFigs();
			numSF = Math.min(sfDiv, thisSF);
		}
		//Now retrieve decimal parts of each and convert to Doubles
		Double thisDecPart = new Double(decimalPart);
		Double divDecPart = new Double(divisor.getDecimalPart());
		//Multiply double parts together
		double decResult = thisDecPart.doubleValue() / divDecPart.doubleValue();
		//Now add exponent parts
		Double thisExpPart, divExpPart;
		if (hasExponent)
			thisExpPart = new Double(exponentPart);
		else
			thisExpPart = new Double(0d);
		if (divisor.getExponent() != null)
			divExpPart = new Double(divisor.getExponent());
		else 
			divExpPart = new Double(0d);
		int sum = thisExpPart.intValue() - divExpPart.intValue();
		// create new number
		SFNumber result = new SFNumber(String.valueOf(decResult),
											String.valueOf(sum));
		//optionally round
		if (!bExact) 
			return (result.round(numSF));
		else
			return result;
	}
	
	//No setter methods for these - must create new number instead
	public int getSigFigs() {
		if (exactNumber) {
			//return effectively infinite number of sig figs
			return Integer.MAX_VALUE;
		}
		return sigFigs;
	}
	
	public String getDecimalPart() {
		return decimalPart;
	}
	
	public String getExponent() {
		return exponentPart;
	}
	
	//Returns num of digits in decimal part.
	//This is the only part that matters for Sig Fig considerations.
	public int length() {
		if (isDecimal)
			return decimalPart.length()-1;
		else
			return decimalPart.length();
	}
	
	//Concatenate decimal parts and exponent parts together
	public String toString() {
		if (hasExponent) //Gustavo 26Jan2007: Integer.valueOf is necessary to prevent printing e.g. "1.6e-02"
		{	
			if(getSigFigs()==1) //tmandel: Double.valueOf will add an extra ".0" to an integer value
				return Integer.valueOf(decimalPart) + "e" + Integer.valueOf(exponentPart);
			else
				return Double.valueOf(decimalPart) + "e" + Integer.valueOf(exponentPart);
		}
		else
			return decimalPart;
	}

}
