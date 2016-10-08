package edu.cmu.pact.miss.minerva_3_1;

import java.util.Map;

/*
 * 	Andrew Lee
 * 	May 02 2009
 * 	Transforms equations of the step of a problem.
 * 	Many functions are similarly named to counterparts in ProblemAbstractor
 * 	but actually behave differently. 
 */

public class StepAbstractor extends Abstractor {
    
    static final String ABST_NUM = "N";
    static final String ABST_VAR = "v";
    
    public boolean leftSide;
	public String preequation;
	
	/*
	 * Abstraction with...
	 * - variables replaced to generic symbol
	 * - equation oriented to have more variables on left side of =
	 * - constants abstracted from left to right
	 */
	/*
	public String signedAbstraction(String equation) {
		equation = abstractVars(equation);
		//equation = assignLHS(equation) + " = " + assignRHS(equation); //awlee: ??? ordering?
		preequation = equation;
		equation = abstractNumbersAbsolute(equation);

		return equation;
	}
	*/
	public String signedAbstraction(String exp) {
	    
	    String signedAbst = "";
	    
	    boolean isScanningInt = false;
	    for (int i = 0; i < exp.length(); i++ ) {
	        
	        char c = exp.charAt(i);
	        if (Character.isDigit(c)) {
	            if (isScanningInt) {
	                ;
	            } else {
	                signedAbst += ABST_NUM;
	                isScanningInt = true;
	            }
	        } else {
	            isScanningInt = false;
	            if (Character.isLetter(c)) {
	            	
	            	if((i != exp.length()-1)){
						if(Character.isLetter(exp.charAt(i+1)) || (exp.charAt(i+1))== ' ')				
							signedAbst += exp.charAt(i);
					
						else{
							signedAbst += ABST_VAR;							
							}
						
					}
					else{
						signedAbst += ABST_VAR;
					}
	            	
	                //signedAbst += ABST_VAR;
	            } else {
	                signedAbst += c;
	            }
	        }
	    }
	    return signedAbst;
	}

	/*
	 * Absolute Abstraction with...
	 * - unnecessary negative signs removed
	 */
	public String unsignedAbstraction(String equation) {
		equation = signedAbstraction(equation);
		equation = removeNegatives(equation);

		return equation;
	}

	String removeNegatives(String exp) {
	    
	    String removeNegatives = "";
        String previous = "";    
	    
	    for (int i = 0; i < exp.length(); i++) {

	        char c = exp.charAt(i);
	        
	        String s = "";
	        
	        
	        if (c == '-') {
	        	
	            if(previous.length()>0){
	            	if(previous.equals(")") || Character.isLetter(previous.charAt(0)) || Character.isDigit(previous.charAt(0)))
	            		s = "+";
	            }
	        } 
	        else {
	            s += c;
	        }
	        
	        removeNegatives += s;
	        previous = s;
	    }
	    
	    return removeNegatives;
	}
	
	/*
	 * 	returns same as abstractAbsolute, except applied to input
	 */
	public String abstractInputAbsolute(String input) {
		input = abstractVars(input);
		input = abstractSignedInputSimpleContext(abstractNumbers(preequation), input);

		return input;
	}
	
	/*
	 * 	returns same as abstractSimple, except applied to input
	 */
	public String abstractInputSimple(String input) {
		input = abstractVars(input);
		input = abstractSignedInputSimpleContext(abstractNumbers(preequation),
				input);
		input = removeNegatives(input);

		return input;
	}

	/*
	 * 	???
	 */
	public String abstractSignedInputSimpleContext(Map<String, String> constants,
			String inputEquation) {
		String newEquation = "";
		//int nextConstant = 65+25;//begins at 'Z', decreases for each new constant
		int nextConstant = 65;
		int nextVariable = 97; 
			
		
		
		for (int i = 0; i < inputEquation.length(); i++) {
			if (!Character.isDigit(inputEquation.charAt(i))) {
				if(!Character.isLetter(inputEquation.charAt(i))){
					newEquation += inputEquation.charAt(i);
				}
				else if((i != inputEquation.length()-1)){
					if(Character.isLetter(inputEquation.charAt(i+1)) || (inputEquation.charAt(i+1))== ' ')				
						newEquation += inputEquation.charAt(i);
				
					else{
							newEquation += ABST_VAR;							
						
					}
				}
				else{
					newEquation += ABST_VAR;
				}
			} else {
				int start = i;

				while (i + 1 < inputEquation.length()
						&& Character.isDigit(inputEquation.charAt(i + 1))) {
					i++;
				}

				String val = inputEquation.substring(start, i + 1);

				if (constants.containsKey(val)) {
					newEquation += constants.get(val);
				} else {
					String newConstant = "";
					newConstant += (char) nextConstant;
					constants.put(val, newConstant);
					nextConstant++;

					newEquation += newConstant;
				}
			}

		}

		return newEquation;
	}
	
	public String abstractUnsignedInputSimpleContext(Map<String, String> constants,
			String inputEquation){
		inputEquation = abstractSignedInputSimpleContext(constants, inputEquation);
		inputEquation = removeNegatives(inputEquation);

		return inputEquation;
	}

	/*
	 * 	returns the number of times the variable appear in an equation
	 */
	private int getVCount(String equation) {
		int vCount = 0;

		for (int i = 0; i < equation.length(); i++) {
			if (equation.charAt(i) == 'v') {
				vCount++;
			}
		}

		return vCount;
	}

	/*
	 * 	Given an equation, determines desired LHS of equation based on variable frequency
	 */
	protected String assignLHS(String equation) {
		int indexEqualSign = equation.indexOf('=');

		if (equation.indexOf('=') == -1)
			return equation;

		int vCount = getVCount(equation);

		if (vCount == 1) {

			if (equation.indexOf("v") < indexEqualSign) {
				leftSide = true;
				return equation.substring(0, indexEqualSign - 1);
			}
			leftSide = false;
			return equation.substring(indexEqualSign + 2);
		} else if (vCount == 3) {
			int index1 = equation.indexOf('v');
			int index2 = equation.indexOf('v', index1);
			int index3 = equation.indexOf('v', index2);

			if (indexEqualSign < index2 && indexEqualSign < index3) {
				leftSide = false;
				return equation.substring(indexEqualSign + 2);
			}
		}
		leftSide = true;
		return equation.substring(0, indexEqualSign - 1);
	}
	
	/*
	 * 	Given an equation, determines desired RHS of equation based on variable frequency
	 */
	protected String assignRHS(String equation) {
		int indexEqualSign = equation.indexOf('=');

		if (equation.indexOf('=') == -1)
			return "";
		/*
		int vCount = getVCount(equation);

		if (vCount == 1) {
			if (equation.indexOf("v") < indexEqualSign) {
				return equation.substring(indexEqualSign + 2);
			}

			return equation.substring(0, indexEqualSign - 1);
		} else if (vCount == 3) {
			int index1 = equation.indexOf('v');
			int index2 = equation.indexOf('v', index1);
			int index3 = equation.indexOf('v', index2);

			if (indexEqualSign < index2 && indexEqualSign < index3)
				return equation.substring(0, indexEqualSign - 1);
		}
		*/
		if(leftSide)
			return equation.substring(indexEqualSign + 2);
		else 
			return equation.substring(0, indexEqualSign - 1);
	}
}
