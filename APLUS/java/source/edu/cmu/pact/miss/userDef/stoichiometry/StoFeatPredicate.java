/** 
 *  Class StoFeatPredicate
 *  Defines methods for operators and features of SimStoichiometry.
 *  Reid Van Lehn <rvanlehn@mit.edu> Summer 2006
 * 
 */

package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.FeaturePredicate;


public abstract class StoFeatPredicate extends FeaturePredicate {
	
	//Hard-coded list of possible units.
	//Cannot be loaded from text file due to nature of feature preds.
	//Only used in case of improper input (not space separated)
	/*
	private static String[] units = {
		//mass
		 "mg", "kg", 
		"milligrams", "milligram", "kilograms", "kilogram",
		"lbs", "lb", "pounds", "pound", "grams", "gram", "g",
		//volume
		"L", "mL", "kL", "milliliters",
		"milliliter", "kiloliters", "kiloliters", "gallons", 
		"gallon", "gal", "gals", "liters", "liter", "fl oz", 
		"fluid ounces",
		//mols
		"millimoles", "millimols", "millimole", "millimol",
		"mmol", "mols", "mole", "moles", "mol"
	};
	*/
	
	//Log the different types of unit to see if they are convertable
	public static String[] massUnits = {
		"g", "lbs", "kg", "mg"
	}; 
	
	public static String[] volUnits = {
		"gals", "L", "mL", "kL"
	};
	
	//avogadro's number with infinite precision (in terms of sig figs)
	public static final SFNumber AVOGADRO = new SFNumber(
							"6.0221415E23", true);
	

       
        
        public StoFeatPredicate() {
	}
	
	/**
	 * Given a string representing the contents of JCommTextField
	 * looking like 50 mg Fe, parses text into number, unit, substance.
	 * 
	 * In current implementation, assumes that author correctly enters text of form
	 *  5.0e1 mg Fe - i.e. space separated, always scientific notation
	 * 
	 * @param expression
	 * @return Vector with 3 elements: SFNumber for number, String for unit, 
	 * String for substance. 
	 */
	public Vector parseExpression(String expression) {
		//first try tokenizing on spaces
		String[] parsedArray = expression.split(" ");
		String number, unit, substance; 
		unit = null;
		
		// Scratch this - assume author has correctly inputted data
		/*
		//Check the result
		if (parsedArray.length != 3) {
			//Manually parse for unit
			int unitIndex = 0;
			int unitLength = 0; 
			for (int i=0; i<units.length; i++) {
				//for purposes of matching
				expression = expression.toLowerCase();
				unitIndex = expression.indexOf(units[i]);
				if (unitIndex > -1) {
					unitLength = units[i].length();
					unit = units[i];
					break;
				}
			}
			if (unitIndex == -1) {
				//Didn't find unit - error in input
				return null; //handle error elsewhere
			}
			//Having id'ed unit, parse other two inputs
			//Assume they are entered in correct order
			number = expression.substring(0, unitIndex);
			// Now retrieve substance string
			substance = expression.substring(unitIndex+unitLength);
			substance.trim();
		}
		else {
		*/
		//TODO: Check input?
		number = parsedArray[0];
		unit = parsedArray[1];
		substance = parsedArray[2];
		//}
		SFNumber sfNum = new SFNumber(number.trim());
		StoSubstance stoSub = new StoSubstance(substance);
		
		//Populate vector for output (ordered)
		Vector parsed = new Vector();
		parsed.add(sfNum);
		parsed.add(unit);
		parsed.add(stoSub);
		return parsed; 
	}

//	 ----------------------------------------------------------
//	 ------------------ Feature functions --------------------
//	 ----------------------------------------------------------
	
	/**
	 * Simply checks to see if two FoA contain the same unit. 
	 * Does not prevent same FoA from being compared against itself.
	 * 
	 * @param firstExp, secondExp
	 * @return String "T" if units match, null otherwise
	 */
	public String matchUnit(String firstExp, String secondExp) {
//		Parse both and compare the Vectors
		Vector first = parseExpression(firstExp);
		Vector second = parseExpression(secondExp);

		try {
			String firstUnit = (String)first.get(1);
			String secondUnit = (String)second.get(1);
			return matchUnitFlash(firstUnit, secondUnit);

		} catch (Exception e) {
			return null;
		}
		/*
		 try {
			if (firstUnit.equals(secondUnit)) {
				return "T";
			}
			else
				return null;
		} catch (Exception e) {
			return null;
		}
		*/
	}
	
	
	public String matchUnitFlash(String firstUnit, String secondUnit) {
		try {
			if (firstUnit.equals(secondUnit)) {
				return "T";
			}
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	/** 
	 * Returns "T" if two FoA have the same substance. Checks by direct
	 * string matching.
	 * 
	 * @param firstExp
	 * @param secondExp
	 * @return "T" if substances match, null otherwise
	 */
	public String matchSubstance(String firstExp, String secondExp) {
//		Parse both and compare the Vectors
		Vector first = parseExpression(firstExp);
		Vector second = parseExpression(secondExp);
		try{
			StoSubstance firstSub = (StoSubstance)first.get(2);
			StoSubstance secondSub = (StoSubstance)second.get(2);
			return matchSubstanceFlash(firstSub.toString(), secondSub.toString());
		}
		catch (ClassCastException E) {
			return null;
		}
/*		try {
			if (firstSub.toString().equals(secondSub.toString())) 
				return "T";
			else
				return null;
		} catch (ClassCastException E) {
			return null;
		}
		*/
	}
	
	public String matchObject(String firstStr, String secondStr) {
		try {
			if (firstStr.equals(secondStr))
				return "T";
			else
				return null;
		} catch (ClassCastException E) {
			return null;
		}
	}
	public String matchSubstanceFlash(String firstSubStr, String secondSubStr) {
		try {
			if (firstSubStr.equals(secondSubStr))
				return "T";
			else
				return null;
		} catch (ClassCastException E) {
			return null;
		}
	}

	/** If one of the FoA has a unit of the form xxx/xxx (i.e. g/mol) then
	 * returns "T" if the top unit (i.e. left side of division if written in that form) 
	 * is equal to the unit of the other FoA (by string matching). 
	 * @param givenVal
	 * @param input
	 * @return "T" if units match, null otherwise (and if one unit is not of form xxx/xxx)
	 */
	public String matchTopUnit(String givenVal, String input) {
		if (givenVal.equals(input)) {
			return null; //don't allow same string to be compared against itself
		}
		Vector givenVec = parseExpression(givenVal);
		Vector inputVec = parseExpression(input);

		try {
			String givenUnit = (String)givenVec.get(1);
			String inputUnit = (String)inputVec.get(1);
			return matchTopUnitFlash(givenUnit, inputUnit);
		} catch (Exception e) {}
		return null;

		/*
		try {
			String givenUnit = (String)givenVec.get(1);
			//may throw an exception, which is ok
			String[] splitUnits = givenUnit.split("/");
			if (splitUnits.length != 2)
				return null;
			if (splitUnits[0].equals((String)inputVec.get(1)))
				return "T";
		} catch (Exception e) {}
		return null;
		*/
	}

	public String matchTopUnitFlash(String givenUnit, String inputUnit) {
		try {
			//may throw an exception, which is ok
			String[] splitUnits = givenUnit.split("/");
			if (splitUnits.length != 2)
				return null;
			if (splitUnits[0].equals(inputUnit))
				return "T";
		} catch (Exception e) {}
		return null;
	}

	/**
	 * Same as matchTopUnit but for the bottom half of the division.
	 * 
	 * @param givenVal
	 * @param input
	 * @return
	 */
	public String matchBottomUnit(String givenVal, String input) {
		if (givenVal.equals(input))
			return null;
		Vector givenVec = parseExpression(givenVal);
		Vector inputVec = parseExpression(input);
		 try {
				String givenUnit = (String)givenVec.get(1);
				String inputUnit = (String)inputVec.get(1);
				return matchBottomUnitFlash(givenUnit, inputUnit);
			} catch (Exception e) {}
			return null;
		/*
		 try {
			String givenUnit = (String)givenVec.get(1);
			//may throw an exception, which is ok
			String[] splitUnits = givenUnit.split("/");
			if (splitUnits.length != 2)
				return null;
			if (splitUnits[1].equals((String)inputVec.get(1)))
				return "T";
		} catch (Exception e) {}
		return null;
	*/
	}
	
	public String matchBottomUnitFlash(String givenUnit, String inputUnit) {
		 try {
				//may throw an exception, which is ok
				String[] splitUnits = givenUnit.split("/");
				if (splitUnits.length != 2)
					return null;
				if (splitUnits[1].equals(inputUnit))
					return "T";
			} catch (Exception e) {}
			return null;		
	}

	/**
	 * Directly string matches one input to see if it is the same as the reason supplied.
	 * Called by each Reason specific matching feature (there are 5)
	 * 
	 * @param input
	 * @param givenReason
	 * @return
	 */
	public String matchReason(String input, String givenReason) {
		if (input.equals(givenReason))
			return "T";
		else
			return null;
	}
	
	/** Returns "T" if one FoA has a substance that contains the substance
	 * of the other FoA. For example, P4O10 contains O. 
	 * Works by simply finding the index of one substance in the other. 
	 * 
	 * @param firstExp
	 * @param secondExp
	 * @return "T" if one substance contains the other (via indexOf), null otherwise
	 */
	public String containsSubstance(String firstExp, String secondExp) {
		Vector firstVec = parseExpression(firstExp);
		Vector secondVec = parseExpression(secondExp);
		try {
			StoSubstance larger = (StoSubstance)firstVec.get(2);
			StoSubstance smaller = (StoSubstance)secondVec.get(2);
			String largerString = larger.toString();
			String smallerString = smaller.toString();
			return containsSubstanceFlash(largerString, smallerString);
		} catch (Exception E) {}
		return null;
		/*
		try {
			StoSubstance larger = (StoSubstance)firstVec.get(2);
			StoSubstance smaller = (StoSubstance)secondVec.get(2);
			String largerString = larger.toString();
			String smallerString = smaller.toString();
			if (!largerString.equals(smallerString)
				&& largerString.indexOf(smallerString) != -1)
				return "T";
			
		} catch (Exception E) {}
		return null;
		*/
	}
	
	public String containsSubstanceFlash(String largerString, String smallerString) {
		try {
			if (!largerString.equals(smallerString)
				&& largerString.indexOf(smallerString) != -1)
				return "T";
		} catch (Exception E) {}
		return null;
	}

	/** Looks to see if one input has the number of significant figures
	 * specified by the other input. This is intended to check specifically for 
	 * the goal condition, where one number represents the integer number of sig figs.
	 * So if one input is 4, returns T if the other input has 4 sig figs.
	 * 
	 * @param numSigFigs
	 * @param exp
	 * @return "T" if one input has num of sig figs supplied by other integer input
	 */
	public String hasCorrectSigFigs(String numSigFigs, String exp) {
		Vector sfVec = parseExpression(numSigFigs);
		Vector expVec = parseExpression(exp);

		try {
			SFNumber numSF = (SFNumber)sfVec.get(0);
			SFNumber inputNum = (SFNumber)expVec.get(0);
			return hasCorrectSigFigsFlash(numSF.toString(), inputNum.toString());
		}
		catch (Exception e) {};
		return null;
		
		/*
		try {
			SFNumber numSF = (SFNumber)sfVec.get(0);
			SFNumber inputNum = (SFNumber)expVec.get(0);
			int SFint = Integer.parseInt(numSF.getDecimalPart());
			//must be numeric between 0-10
			if (numSF.getExponent().equals("0") 
				&& SFint > 0 
				&& inputNum.getSigFigs() == SFint) {
				return "T";
			}
		} catch (Exception e) {};
		return null;
		*/
	}

	public String hasCorrectSigFigsFlash(String numSFStr, String inputNumStr) {
		try {
			SFNumber numSF = new SFNumber(numSFStr);
			SFNumber inputNum = new SFNumber(inputNumStr);
			
			int SFint = Integer.parseInt(numSF.getDecimalPart());
			//must be numeric between 0-10
			if (numSF.getExponent().equals("0") 
				&& SFint > 0 
				&& inputNum.getSigFigs() == SFint) {
				return "T";
			}
		} catch (Exception e) {};
		return null;
	}

	/**
	 * Returns true if the first unit can be converted to the second unit.
	 * Since conversions only occur between mass units or volume units, simply
	 * searches to see if the units are both contained in the corresponding
	 * mass and volume unit arrays.
	 * 
	 * @param first
	 * @param second
	 * @return "T" if units can be converted, null otherwise
	 */
	public String canBeConverted(String first, String second) {
		if (first.equals(second))
			return null;
		Vector firstVec = parseExpression(first);
		Vector secondVec = parseExpression(second);

		try {
			String firstUnit = (String)firstVec.get(1);
			String secondUnit = (String)secondVec.get(1);
			return canBeConvertedFlash(firstUnit, secondUnit);
		} catch (Exception e) {}
		return null;
		
		/*
		try {
			boolean hasFirst = false;
			boolean hasSecond = false;
			String firstUnit = (String)firstVec.get(1);
			String secondUnit = (String)secondVec.get(1);
			//Don't return true if they are the same unit
			if (firstUnit.equals(secondUnit))
				return null;
			//
			for (int i=0; i<massUnits.length; i++) {
				if (massUnits[i].equals(firstUnit))
					hasFirst = true;
				else if (massUnits[i].equals(secondUnit))
					hasSecond = true;
			}
			if (hasFirst && hasSecond)  {
				return "T";
			}
			else
				hasFirst = hasSecond = false;
			for (int j=0; j<volUnits.length; j++) {
				if (volUnits[j].equals(firstUnit))
					hasFirst = true;
				else if (volUnits[j].equals(secondUnit))
					hasSecond = true;
			}
			if (hasFirst && hasSecond) {
				return "T";
			}
			else
				return null;
		} catch (Exception e) {}
		return null;
		*/
	}

	public String canBeConvertedFlash(String firstUnit, String secondUnit) {
		try {
			boolean hasFirst = false;
			boolean hasSecond = false;
			//Don't return true if they are the same unit
			if (firstUnit.equals(secondUnit))
				return null;
			//
			for (int i=0; i<massUnits.length; i++) {
				if (massUnits[i].equals(firstUnit))
					hasFirst = true;
				else if (massUnits[i].equals(secondUnit))
					hasSecond = true;
			}
			if (hasFirst && hasSecond)  {
				return "T";
			}
			else
				hasFirst = hasSecond = false;
			for (int j=0; j<volUnits.length; j++) {
				if (volUnits[j].equals(firstUnit))
					hasFirst = true;
				else if (volUnits[j].equals(secondUnit))
					hasSecond = true;
			}
			if (hasFirst && hasSecond) {
				return "T";
			}
			else
				return null;
		} catch (Exception e) {}
		return null;
	}
	
	
	
// ----------------------------------------------------------
// ------------------ Operator functions --------------------
// ----------------------------------------------------------
	

	/** Exactly multiplies two expressions, meaning that the most precise
	 * possible product is returned (based on java's constraint) without
	 * rounding due to significant figures. 
	 * 
	 * @return String containing exact product with same unit and substance as one expression
	 */
	public String multExact(String firstExpression, String secondExpression) {
		//No need to parse with current gui-tmandel
		//Parse both 
		//Vector firstVec = parseExpression(firstExpression);
		//Vector secondVec = parseExpression(secondExpression);
		//Retrieve numbers from both
		try {
			/*SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			firstVec.setElementAt(multExactFlash(num1, num2), 0);
			return (outputToString(firstVec));*/
			SFNumber num1 =new SFNumber(firstExpression.trim());
			SFNumber num2 =new SFNumber(secondExpression.trim());
			Vector res= new Vector();
			res.add(multExactFlash(num1,num2));
			return (res.get(0).toString());
		} catch (Exception e) {
			return null; //error in parsing number
		}
		/*
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			//find minimum sig figs
			SFNumber product = num1.multiplyBy(num2, true);
			//Arbitrarily set first vector's product; other operators 
			//can change unit and substance
			firstVec.setElementAt(product, 0);
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null; //error in parsing number
		}
		*/
	}
	
	public SFNumber multExactFlash(SFNumber num1, SFNumber num2) {
		try {
			//find minimum sig figs
			SFNumber product = num1.multiplyBy(num2, true);
			return product;
		} catch (Exception e) {
			return null; //error in parsing number
		}
	}

	/** 
	 * Extracts the numbers from two expressions and multiplies them together. 
	 * The number that is returned is rounded to the lowest number of sig figs 
	 * used by the two initial factors. 
	 * 
	 * @param firstExpression
	 * @param secondExpression
	 * @return String containing rounded product with same unit, substance as one exp
	 */
	public String multTwoTerms(String firstExpression, String secondExpression) {
		//Parse both 
		Vector firstVec = parseExpression(firstExpression);
		Vector secondVec = parseExpression(secondExpression);
		//Retrieve numbers from both
		
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			firstVec.setElementAt(multTwoTermsFlash(num1,num2), 0);
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null; //error in parsing number
		}
		/*
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			//find minimum sig figs
			SFNumber product = num1.multiplyBy(num2, false);
			//Arbitrarily set first vector's product; other operators 
			//can change unit and substance
			firstVec.setElementAt(product, 0);
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null; //error in parsing number
		}
		*/
	}
	
	
	public SFNumber multTwoTermsFlash(SFNumber num1, SFNumber num2) {
		try {
			//find minimum sig figs
			SFNumber product = num1.multiplyBy(num2, false);
			trace.out("num1=" + num1+"  num2 = " + num2 + "    product = " + product);
			return product;
		} catch (Exception e) {
			return null; //error in parsing number
		}
	}

	/**  
	 *  Same idea as multTwoTerms except for 3.
	 * 
	 * @param firstExpression
	 * @param secondExpression
	 * @param thirdExpression
	 * @return Rounded product of 3 factors with unit/substance of one of them
	 */
	public String multThreeTerms(String firstExpression, String secondExpression,
			String thirdExpression) {
		//Parse both 
		Vector firstVec = parseExpression(firstExpression);
		Vector secondVec = parseExpression(secondExpression);
		Vector thirdVec = parseExpression(thirdExpression);
		//Retrieve numbers from both
		
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			SFNumber num3 = (SFNumber)thirdVec.get(0);

			//find minimum sig figs
			SFNumber product1 = num1.multiplyBy(num2, false);
			SFNumber product2 = product1.multiplyBy(num3, false);
			firstVec.setElementAt(multThreeTermsFlash(num1, num2, num3), 0);			
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null; //error in parsing number
		}
		
		/*
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			SFNumber num3 = (SFNumber)thirdVec.get(0);
			//find minimum sig figs
			SFNumber product1 = num1.multiplyBy(num2, false);
			SFNumber product2 = product1.multiplyBy(num3, false);
			firstVec.setElementAt(product2, 0);
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null; //error in parsing number
		}
		*/
	}
	
	private Object multThreeTermsFlash(SFNumber num1, SFNumber num2, SFNumber num3) {
		try {
			//find minimum sig figs
			SFNumber product1 = num1.multiplyBy(num2, false);
			SFNumber product2 = product1.multiplyBy(num3, false);
			return product2; 
		} catch (Exception e) {
			return null; //error in parsing number
		}
	}

	//	Different operator for each set of possibilities to overcome depth-limit
	public String multFourTerms(String firstExpression, String secondExpression,
			String thirdExpression, String fourthExpression) {
		//Parse both 
		Vector firstVec = parseExpression(firstExpression);
		Vector secondVec = parseExpression(secondExpression);
		Vector thirdVec = parseExpression(thirdExpression);
		Vector fourthVec = parseExpression(fourthExpression);
		//Retrieve numbers from both
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			SFNumber num3 = (SFNumber)thirdVec.get(0);
			SFNumber num4 = (SFNumber)fourthVec.get(0);
			//find minimum sig figs
			SFNumber product1 = num1.multiplyBy(num2, false);
			SFNumber product2 = product1.multiplyBy(num3, false);
			SFNumber product3 = product2.multiplyBy(num4, false);
			firstVec.setElementAt(product3, 0);
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null; //error in parsing number
		}
	}

	/** Divides first expression by second, returning a vector
	 *  with the unrounded result of the division and the unit/substance
	 * of the first expression.
	 * 
	 * @param firstExp
	 * @param secondExp
	 * @return String with firstExp/secondExp (unrounded), unit/substance of firstExp
	 */
	public String divideExact(String firstExp, String secondExp) {
		//No longer need to parse - tmandel
		//Parse both
		//Vector firstVec = parseExpression(firstExp);
		//Vector secondVec = parseExpression(secondExp);
		try {
			SFNumber num1 =new SFNumber(firstExp.trim());
			SFNumber num2 =new SFNumber(secondExp.trim());
			//trace.out(firstVec.get(0));
			//SFNumber num1 = (SFNumber)firstVec.get(0);
			//SFNumber num2 = (SFNumber)secondVec.get(0);
			//firstVec.setElementAt(divideExactFlash(num1,num2), 0);
			//return (outputToString(firstVec));
			Vector res= new Vector();
			res.add(divideExactFlash(num1,num2));
			return (res.get(0).toString());
		} catch (Exception e) {
			return null;
		}
		/*
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			//now divide using correct rounding context
			//May throw an exception
			SFNumber result = num1.divideBy(num2, true);
			firstVec.setElementAt(result, 0);
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null;
		}
		*/
	}
	
	public SFNumber divideExactFlash(SFNumber num1, SFNumber num2) {
		try {
			//now divide using correct rounding context
			//May throw an exception
			SFNumber result = num1.divideBy(num2, true);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	/** Rounds firstExp to secondExp sig figs
	 * 
	 * @param firstExp
	 * @param secondExp
	 * @return String with firstExp rounded to secondExp sig figs
	 */
	public String round(String firstExp, String secondExp) {
		try {
			SFNumber num1 =new SFNumber(firstExp.trim());
			int sf =Integer.parseInt(secondExp.trim());
			//trace.out(firstVec.get(0));
			//SFNumber num1 = (SFNumber)firstVec.get(0);
			//SFNumber num2 = (SFNumber)secondVec.get(0);
			//firstVec.setElementAt(divideExactFlash(num1,num2), 0);
			//return (outputToString(firstVec));
			Vector res= new Vector();
			res.add(num1.round(sf));
			return (res.get(0).toString());
		} catch (Exception e) {
			return null;
		}
	}

	/** Rounded division of firstExp by secondExp. Rounds to minimum number of sig figs
	 * in the two expression. Otherwise identical to divideExact.
	 * 
	 * @param firstxp
	 * @param secondExp
	 * @return String with number the unrounded result of division
	 */
	public String divideTerms(String firstExp, String secondExp) {
		//Parse both 
		
		Vector firstVec = parseExpression(firstExp);
		Vector secondVec = parseExpression(secondExp);
		try {
			SFNumber num1 = (SFNumber)firstVec.get(0);
			SFNumber num2 = (SFNumber)secondVec.get(0);
			//now divide using correct rounding context
			//May throw an exception
			SFNumber result = num1.divideBy(num2, false);
			firstVec.setElementAt(result, 0);
			return (outputToString(firstVec));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Explicitly sets the unit of one FoA to be the unit of the other FoA.
	 * @param input
	 * @param expected
	 * @param reason
	 * @return String with the unit of input = unit of expected
	 */
	public String setUnit(String input, String expected, String reason) {
		//if (reason == ReasonOperator.UNITCONV || reason == ReasonOperator.COMPSTO)
		//	return null;
//		First parse the input of both
		Vector inputVec = parseExpression(input);
		Vector expectedVec = parseExpression(expected);
		//Get the unit from input Vec
		try {
			String unit = (String)inputVec.get(1); //assume correct orders
			//Set unit in second vector
			expectedVec.setElementAt(unit, 1);
			return (outputToString(expectedVec));
		} catch (ClassCastException e) {
			return null;
		}
	}

	//Gustavo 17Jan2007: this is the identity function. All the Set*Flash's handlers are
	//going to be the identity function.
	public String setUnitFlash(String unit) {
		return unit;
	}
	
	
	/**
	 * Called by all of the unit-conversion operators. Explicitly sets the unit
	 * of the input to be the supplied 'unit' parameter if the input's unit equals
	 * the expectedUnit parameter. Also sets number to the value given. 
	 * 
	 * @param input
	 * @param expectedUnit
	 * @param unit
	 * @param number
	 * @param reason
	 * @return String with unit = unit, number = number, substance same as before
	 */
	public String convertUnit(String input, String expectedUnit, 
						      String unit, String number, String reason) {
		//Only check for equality, not inequality, in event
		//that a non-reason is supplied 
		//if (reason != ReasonOperator.UNITCONV)
			//return null;
		Vector inputVec = parseExpression(input);
		try {
			if (((String)inputVec.get(1)).equals(expectedUnit)) {
				inputVec.setElementAt(unit, 1);
				inputVec.setElementAt(new SFNumber(number, true), 0);
				return (outputToString(inputVec));
			}
		} catch (Exception e) {}
		return null;
	}
	

	/**
	 * If one of the FoA has a unit of the form xxx/xxx (i.e. g/mol) then
	 * take the top unit (i.e. the g in this case) and set as the unit on 
	 * the other input, then return.
	 * 
	 * @param givenVal
	 * @param input
	 * @param reason
	 * @return String with unit of input = top unit of given val
	 */
	public String takeTopUnit(String givenVal, String input, String reason) {
		//if (reason == ReasonOperator.UNITCONV || reason == ReasonOperator.COMPSTO)
		//	return null;
		Vector givenVec = parseExpression(givenVal);
		Vector inputVec = parseExpression(input);
		try {
			String givenUnit = (String)givenVec.get(1);
			//may throw an exception, which is ok
			String[] splitUnits = givenUnit.split("/");
			inputVec.setElementAt(splitUnits[0], 1);
			return (outputToString(inputVec));
		} catch (Exception e) {}
		return null;
	}

	/**
	 * Same as takeTopUnit but for bottom.
	 * 
	 * @param givenVal
	 * @param input
	 * @param reason
	 * @return String with unit of input = bottom unit of given val
	 */
	public String takeBottomUnit(String givenVal, String input, String reason) {
		//if (reason == ReasonOperator.UNITCONV || reason == ReasonOperator.COMPSTO)
		//	return null;
		Vector givenVec = parseExpression(givenVal);
		Vector inputVec = parseExpression(input);
		try {
			String givenUnit = (String)givenVec.get(1);
			//may throw an exception, which is ok
			String[] splitUnits = givenUnit.split("/");
			inputVec.setElementAt(splitUnits[1], 1);
			return (outputToString(inputVec));
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 *  Simply sets the number of an input to 1 (for purposes of convesions
	 *  molar ratios and the like).
	 * 
	 * @param input
	 * @param reason
	 * @return String with number set to one, same substance/unit
	 */
	public String setNumberToOne(String input, String reason) {
		//if (reason == ReasonOperator.UNITCONV || reason == ReasonOperator.AVOGADRO)
		//	return null;
		Vector inputVec = parseExpression(input);
		try {
			//inputVec.setElementAt(new SFNumber("1", true), 0);
			inputVec.setElementAt(new SFNumber("1", true), 0);
			return outputToString(inputVec);
		} catch (Exception e) {}
		return null;
	}
	
	
	public String oneFlash(){
		return "1";
	}
	
		
	/** 
	 * Explicitly sets substance of expected to be the substance of input.
	 * 
	 * @param input
	 * @param expected
	 * @param reason
	 * @return String expected with substance set to be input's substance
 	 */
	public String setSubstance(String input, String expected, String reason) {
		//if (reason == ReasonOperator.UNITCONV)
		//	return null;
		//First parse the input of both
		Vector inputVec = parseExpression(input);
		Vector expectedVec = parseExpression(expected);
		//Get the unit from input Vec

		
		try {
			String substance = (String)inputVec.get(2); //assume correct orders
			//Set unit in second vector
			expectedVec.setElementAt(setSubstanceFlash(substance), 2);
			return (outputToString(expectedVec));
		} catch (ClassCastException e) {
			return null;
		}

		
		/*
		try {
			String substance = (String)inputVec.get(2); //assume correct orders
			//Set unit in second vector
			expectedVec.setElementAt(substance, 2);
			return (outputToString(expectedVec));
		} catch (ClassCastException e) {
			return null;
		}
*/
	}

	//Gustavo 17 Jan 2007
	//identity function
	public String setSubstanceFlash(String substance) {
		return substance;
	}

	/**
	 * Explicitly sets the number of expected to be the number of input.
	 * 
	 * @param input
	 * @param expected
	 * @param reason
	 * @return String with input's number, expected's other values.
	 */
	public String setNumber(String input, String expected, String reason) {
		//if (reason == ReasonOperator.COMPSTO || reason == ReasonOperator.UNITCONV)
		//	return null;
		Vector inputVec = parseExpression(input);
		Vector expectedVec = parseExpression(expected);
		//Get the unit from input Vec
		try {
			SFNumber number = (SFNumber)inputVec.get(0); //assume correct orders
			//Set unit in second vector
			expectedVec.setElementAt(number, 2);
			return (outputToString(expectedVec));
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	// Based on molecular ratio of input substance, generate individual
	// output substances (i.e. 1 mol P4010, yields 10 mol O or 4 mol P)
	public String molarRatio(String input, String goal, String reason) {
		//if (reason != ReasonOperator.COMPSTO)
		//	return null;
		// Based on molar ratio of compound and expected unit, output a 
		// number that reflects the ratio for that goalSubtance
		Vector inputVec = parseExpression(input);
		Vector goalVec = parseExpression(goal);
		StoSubstance goalSubstance = (StoSubstance)goalVec.get(2);
		StoSubstance inputSubstance = (StoSubstance)inputVec.get(2);
		//decompose the input substance
		Vector components = inputSubstance.getComponents();
		//Match component 
		for (int i=0; i<components.size(); i++) {
			try {
				String[] curComp = (String[])components.get(i);
				//Try to match the atom name against the goal's atom name
				if (curComp[0].equals(goalSubstance.toString())) {
					Vector output = new Vector();
					output.add(new SFNumber(curComp[1], true));
					output.add(new String("mol"));
					output.add(goalSubstance);
					return outputToString(output);
				}
			} catch (ClassCastException e) {}
		}
		return null;
	}
        
        public String ucValueNumerator(String reason, String unit1, String unit2) {
            if (reason.equalsIgnoreCase("Unit Conversion"))
                return ucValue(unit1, unit2)[0];
            else
                return null;
        }

        public String ucValueNumerator(String unit1, String unit2) {
            return ucValue(unit1, unit2)[0];
        }
        
        
        public String ucValueDenominator(String reason, String unit1, String unit2) {
            if (reason.equalsIgnoreCase("Unit Conversion"))
                return ucValue(unit1, unit2)[1];                
            else
                return null;
        
        }

        public String ucValueDenominator(String unit1, String unit2) {
            return ucValue(unit1, unit2)[1];                
        }

        
        //Gustavo 27Feb2007:
        //in the current implementation, one value is being thrown out.
        /**
         * First element is the numerator. Second element is the denominator.
         */
        public String[] ucValue(String unit1, String unit2) {
            
            String[][] conversions = {{"g", "mg", "1000", "1"},
                    {"kL", "L", "1000", "1"},
                    {"L", "mL", "1000", "1"},
                    {"kg", "g", "1000", "1"},
                    {"kL", "mL", "1000000", "1"},
                    {"lb", "g", "453.6", "1"}};

            String[] result = new String[2];

            //do unit1 and unit2 match any pairs?
            for (int i=0; i<conversions.length; i++){
                String[] conv = conversions[i];
                String u1 = conv[0];
                String u2 = conv[1];
                String v1 = conv[2];
                String v2 = conv[3];            
                if (u1.equalsIgnoreCase(unit1)&&u2.equalsIgnoreCase(unit2)){
                    result[0]=v1;
                    result[1]=v2;                    
                }
                else if (u1.equalsIgnoreCase(unit2)&&u2.equalsIgnoreCase(unit1)){
                    result[0]=v2;
                    result[1]=v1;                    
               }
            }
            return result;
        }
            
            /**
             * Given two units, return intermediate unit for us to convert between.
             */
            public String interUnit(String unit1, String unit2) {
                
                String[][] conversions = {
                		{"kg", "mg", "g"},
                        {"kL", "mL", "L"},
                        {"mm", "m", "cm"},
                        {"cm", "km", "m"},
                    };

                String result = null;

                //do unit1 and unit2 match any pairs?
                for (int i=0; i<conversions.length; i++){
                    String[] conv = conversions[i];
                    String u1 = conv[0];
                    String u2 = conv[1];
                    String v1 = conv[2];            
                    if (u1.equalsIgnoreCase(unit1)&&u2.equalsIgnoreCase(unit2)){
                        result=v1;                
                    }
                    else if (u1.equalsIgnoreCase(unit2)&&u2.equalsIgnoreCase(unit1)){
                        result=v1;                    
                    }
                }
                return result;
            }
            
//            
//            if (unit1.equals("g")&&unit2.equals("mg")){
//                value[0]="1000";
//                value[1]="1";
//            }
//            if (unit1.equals("kL")&&unit2.equals("L")){
//                trace.out("gusmiss", "kL,L");
//                value[0]="1000";
//                value[1]="1";
//            }
//            if (unit1.equals("L")&&unit2.equals("kL")){
//                trace.out("gusmiss", "L,kL");
//                value[0]="1";
//                value[1]="1000";
//            }
//            if (unit1.equals("lb")&&unit2.equals("g")){
//                value[0]="453.6";
//                value[1]="1";
//            }
//            if (result[0]==null){
//                new Exception("unit conversion value not found!").printStackTrace();
//                trace.out("stoich", "unit1 = " + unit1 + ", unit2 = " + unit2);
//            }


    /**
	 * Explicitly sets number to Avogadro's number as defined above.
	 * 
	 * @param expected
	 * @return
	 */
	public String avogadro(String expected) {
//		First parse input
		Vector expectedVec = parseExpression(expected);
		expectedVec.setElementAt(StoFeatPredicate.AVOGADRO, 0);
		return (outputToString(expectedVec));
	}
		
	
	public String inputMatcher(String s1, String s2) {
		String result = convertBoolean(stoichInputMatcher(s1,s2));
		//trace.out("boots21", "stoInputMatcher: result = " + result);
		return result;
	}
		
	
	public static boolean isNumber(String s){
		return edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate.isFloatingPointNumber(s);
	}


	private final static float ALLOWANCE = (float) 0.01;

        
	public boolean stoichInputMatcher(String s1, String s2) {
	    if (s1.equalsIgnoreCase(s2))
	        return true;
	    if (isNumber(s1)&&isNumber(s2)){
	        
                SFNumber num1 = new SFNumber(s1);//.toString();
                SFNumber num2 = new SFNumber(s2);//.toString();
                
                boolean decimalMatch = doesDecimalMatch(Double.valueOf(num1.getDecimalPart()),
                        Double.valueOf(num2.getDecimalPart()), ALLOWANCE);
                        
                boolean exponentMatch = Integer.valueOf(num1.getExponent()).equals(Integer.valueOf(num2.getExponent()));
                
	        return decimalMatch && exponentMatch;
	        //return num1.equals(num2);
	    }
	    return false;
	}

	private boolean doesDecimalMatch(Double d1, Double d2, float allowance) {
	    boolean result;
	    double e1 = d1.doubleValue();
	    double e2 = d2.doubleValue();
	    if (java.lang.Math.abs(e1-e2)<allowance)
	        result = true;
	    else
	        result = false;

	    return result;
	}

    //Gustavo 23Jan2007:
	//returns the molecular weight of 'subs'	
	//will we need to correct for # of significant figures
	public String molWeightValue(String subs) {
		String result=null;
		if (subs.equals("AsO2-"))
			result="106.9";
		if (subs.equals("COH4"))
			result="32.04";
		if (subs.equals("KCl"))
			result="74.551";
		if (subs.equals("CCl4"))
			result="152";
		if (subs.equals("Fe"))
			result="55.845";  //55.85 on problem 3T_53
		if (subs.equals("hemoglobin"))
			result="66627.97";
		if (subs.equals("MgSO4"))
			result="120.37";
		if (subs.equals(""))
			result="";

		
		return result;
	}


	
	/** 
	 * Converts a vector back to a String by concatenating its elements.
	 * 
	 * @param output
	 * @return String representing the elements of this vector
	 */
	public String outputToString(Vector output) {
		String outputStr = output.get(0) + " " + output.get(1)+ " "
				       + output.get(2);
		return outputStr;
	}
	
}
