package edu.cmu.pact.miss.minerva_3_1;

import java.util.HashMap;
import java.util.Map;


public class Abstractor {
	
	/*
	 * Replaces variables ('x' and 'y' at present) with generic variable symbol ('v')
	 * Well! how about that! No thought put in into the case of the vars there
	 * I'll change it now.
	 */
	protected static String abstractVars(String equation) {
		equation = equation.replaceAll("[a-zA-Z&&[^v]]", "v");
		return equation;
	}
	
	/*
	 * returns String of equation with constants abstracted from left to right
	 */
	public String abstractNumbersAbsolute(String equation) {
	    String constantized = "";
	    int nextConstant = 65;

	    for (int i = 0; i < equation.length(); i++) {
	        if (!Character.isDigit(equation.charAt(i))) {
	            constantized += equation.charAt(i);
	        } else {
	            if (((i == equation.length() - 1)
	                    || !Character.isDigit(equation.charAt(i + 1)))) {
	                constantized += (char) nextConstant;
	                nextConstant++;
	            }
	        }
	    }

	    return constantized;
	}
	
	/*
	 * 	Abstracts current equation taking into account constants which
	 * 	were defined earilier within the problem
	 */
	public String abstractNumbersContext(String equation,
			Map<String, String> abstractionConstants, char nextConstant) {
		String newEquation = "";

		for (int i = 0; i < equation.length(); i++) {
			if (!Character.isDigit(equation.charAt(i))) {
				newEquation += equation.charAt(i);
			} else {
				int start = i;

				while (i + 1 < equation.length()
						&& Character.isDigit(equation.charAt(i + 1))) {
					i++;
				}

				String val = equation.substring(start, i + 1);

				if (abstractionConstants.containsKey(val)) {
					newEquation += abstractionConstants.get(val);
				} else {
					String newConstant = "";
					newConstant += nextConstant;
					abstractionConstants.put(val, newConstant);
					nextConstant++;

					newEquation += newConstant;
				}
			}

		}

		return newEquation;
	}
	
	protected Map<String, String> abstractNumbers(String equation) {
		Map<String, String> constants = new HashMap<String, String>();
		String newEquation = "";
		int nextConstant = 65;

		for (int i = 0; i < equation.length(); i++) {
			if (!Character.isDigit(equation.charAt(i))) {
				newEquation += equation.charAt(i);
			} else {
				int start = i;

				while (i + 1 < equation.length()
						&& Character.isDigit(equation.charAt(i + 1))) {
					i++;
				}

				String val = equation.substring(start, i + 1);

				if (!constants.containsKey(val)) {
					String newConstant = "";
					newConstant += (char) nextConstant;
					constants.put(val, newConstant);
					nextConstant++;
				}
			}

		}

		return constants;
	}
	
	/*
	 * returns String of equation with extraneous negative signs removed
	 */
	String removeNegatives(String equation) {

		String newEquation = "";

		for (int i = 0; i < equation.length(); i++) {
			if (i == 0 && equation.charAt(0) == '-'
					|| equation.charAt(i) == '(' || equation.charAt(i) == ')') {
				// ignores non-number characters at beginning of equation
				// awlee: why the inclusion of parenthesis?
			}  else if (i > 0 && equation.charAt(i) == '-') {
				if (equation.charAt(i - 1) != '+'
						&& equation.charAt(i - 1) != '/'
						&& equation.charAt(i - 1) != '(' && equation.charAt(i-1) != ' ' && equation.charAt(i-1) != '=') {
					//newEquation += equation.charAt(i);
					newEquation += '+';
				}
			} else {
				newEquation += (equation.charAt(i));
			}
		}
		// System.out.println("newEquation: " + newEquation);
		return newEquation;
	}
}
