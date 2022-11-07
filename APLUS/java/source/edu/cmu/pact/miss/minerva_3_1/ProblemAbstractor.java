/*
 * 	Andrew Lee
 * 	May 02 2009
 * 	Transforms equations of a math problem.
 */
package edu.cmu.pact.miss.minerva_3_1;

import java.util.*;

public class ProblemAbstractor extends Abstractor {

	/*
	 * Abstraction with...
	 * - variables replaced to generic symbol
	 * - equation oriented to reduce mirror image equation
	 * - constants abstracted from left to right
	 */
	public String abstractEquationAbsolute(String equation) {
		// trace.out("absolute abstraction equation: " + equation);
		
		equation = abstractVars(equation);
		// trace.out("abstractVars() = " + equation);
		
		equation = orient(equation);
		equation = abstractNumbersAbsolute(equation);
		// trace.out("abstractNumbersAbsolute() = " + equation);
		
		return equation;
	}

	/*
	 * Absolute Abstraction with...
	 * - unnecessary negative signs removed
	 */
	public String abstractEquationSimple(String equation) {
		// trace.out("simple abstract equation: " + equation);
		equation = abstractEquationAbsolute(equation);
		equation = removeNegatives(equation);

		// trace.out(equation);
		return equation;
	}

	/*
	 * Abstraction with...
	 * - variables replaced to generic symbol
	 * - equation oriented to reduce mirror image equation
	 * - constants abstracted within scope of entire problem
	 * - variable constant abstracted to 'A'
	 */
	public String abstractEquationContext(String equation,
			Map<String, String> abstractionConstants, char nextConstant) {
		// trace.out("Context abstract equation: " + equation);
		equation = abstractVars(equation);
		// trace.out("abstractVars(equation): " + equation);
		equation = orient(equation);
		// trace.out("orient(equation): " + equation);
		abstractVarNumContext(equation, abstractionConstants);
		// trace.out("abstractVarNumContext: " + equation);
		equation = abstractNumbersContext(equation, abstractionConstants, nextConstant);
		// trace.out("abstractNumbersContext: " + equation);
		
		Set keys = abstractionConstants.keySet();
		Iterator itr = keys.iterator();
		while(itr.hasNext()) {
			Object key = itr.next();
			// trace.out(key + "   " + abstractionConstants.get(key));
		}
		return equation;
	}

	/*
	 * Abstracts variable's constant to 'A' 
	 */
	public void abstractVarNumContext(String equation, Map<String, String> abstractionConstants) {
	    
	    equation = abstractVars(equation);
	    
	    int vIndex = equation.indexOf('v');
	    // trace.out("vIndex: " + vIndex + "length: " + equation.length());
	    
	    if (vIndex >= 2) {
	        int leftIndex = vIndex - 2;
	        
	        if (Character.isDigit(equation.charAt(leftIndex))) {
	            while (leftIndex - 1 >= 0 && Character.isDigit(equation.charAt(leftIndex - 1))) {
	                leftIndex--;
	            }

	            abstractionConstants.put(equation.substring(leftIndex, vIndex - 1), "A");
	            // trace.out(equation.substring(leftIndex, vIndex-1) + "       " + "A");
	            return;
	        }
	    }
	    
	    if (vIndex + 2 < equation.length()) {
	        
	        int rightIndex = vIndex + 2;
	        
	        if (equation.charAt(rightIndex) == '/') {
	            while (!Character.isDigit(equation.charAt(rightIndex))) {
	                rightIndex++;
	            }
	            int start = rightIndex;
	            
	            while (Character.isDigit(equation.charAt(rightIndex + 1))) {
	                rightIndex++;
	            }
	            
	            abstractionConstants.put(equation.substring(start, rightIndex + 1), "A");
	            // trace.out(equation.substring(start, rightIndex+1) + "       " + "A");
	        }
	    }
	}
	
	/*
	 * To eliminate mirror images of equations returns string where variable is
	 * to left of equation
	 */
	private String orient(String equation) {
	    
	    String orient = "";
	    
	    // trace.out("orient(" + equation + ")");
	    int vIndex = equation.indexOf('v');
	    int equalsIndex = equation.indexOf('=');
	    // trace.out("vIndex = " + vIndex + ", equalsIndex = " + equalsIndex);
	    
	    if (vIndex > equalsIndex) {
	        orient = equation.substring(equalsIndex + 2) + " = " + equation.substring(0, equalsIndex);
	    } else {
	        orient = equation;
	    }
	    
	    return orient;
	}
}
