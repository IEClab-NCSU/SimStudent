/**
 * Created: Dec 16, 2013
 * @author mazda
 * 
 */
package SimStAlgebraV8;

import java.util.LinkedList;
import java.util.List;

import jess.JessException;
import SimStudent2.ProductionSystem.UserDefJessSymbol;
import cl.utilities.sm.Expression;
import cl.utilities.sm.HSParser;
import cl.utilities.sm.ParseException;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.function.DomainException;

/**
 * @author mazda
 *
 */
public abstract class AlgebraV8UserDefJessSymbol extends UserDefJessSymbol {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Type of arguments and output value
    //
    // As a search heuristic to prune irrelevant node expansion in RHS operator search,
    // we now care about the type of arguments for each operator. Basically, the 
    // search agent does not propose an operator sequence that has a type mismatch, 
    // namely, if an output from OP1 is fed into OP2, then the type of return value
    // of OP1 must be consistent with the type of argument of OP2. 
    // 
	// Algebraic term
	public static final int TYPE_ARITH_TERM = 1;
    // Algebraic expressions
    public static final int TYPE_ARITH_EXP = 2;
    // A list (e.g., "[-3x 3 5x]")
    public static final int TYPE_EXP_LIST = 3;
    // A single word (no space) representing a skill used in CL Algebra I 
    public static final int TYPE_SIMPLE_SKILL = 4;
    // A simple skills followed by a space and an argument (e.g., "add 3x")
    public static final int TYPE_SKILL_OPERAND = 5;
    // A general type encompassing all other types
	public static final int TYPE_OBJECT = 6;
	
	// For type checking
	//
	public static String[] VALID_SIMPLE_SKILL_NAMES = {
        "add", "clt", "combine", "divide", "rf", "subtract", "mt", "distribute", "rds",
        "aproot", "ivm", "multiply", "done"
    };
	
	protected final String SKILL_ADD = "add";
	protected final String SKILL_SUBTRACT = "subtract";
	protected final String SKILL_DIVIDE = "divide";
	protected final String SKILL_MULTIPLY = "multiply";

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Type consistency checking 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/*
	 * For this particular domain, TYPE_ARITH_TERM is compatible with TYPE_ARITH_EXP
	 * 
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#isCompatibleType(int, int)
	 */
	public boolean isCompatibleType(int typeUncertain, int typeRequired){
		
		return (typeRequired == TYPE_ARITH_EXP && typeUncertain == TYPE_ARITH_TERM) ||
				(typeUncertain == typeRequired) ||
				(typeRequired == TYPE_OBJECT );
    }
	
	// - - - - -  - - - - -  - - - - -  - - - - -  - - - - -  - - - - -  
	// Arguments and outputs type checking
	// - - - - -  - - - - -  - - - - -  - - - - -  - - - - -  - - - - -  
	
	// Checking the type of arguments
	//
	public int valueType( String value ) throws JessException {

		int valueType = -1;
        
		if (value.equals(FALSE_VALUE)) {
			
			throw new JessException("AlgebraV8UserDefJessSymbol", "FALSE_VALUE being passed to valueType()", "");
			
		} else if (isValidSimpleSkill(value) || isSkillOperand(value) ) {
            
			valueType = TYPE_SKILL_OPERAND;
            
        } else if (isArithTerm(value)) {
        	
        	valueType = TYPE_ARITH_TERM;
        	
        } else if (isArithmeticExpression(value)) {
            
        	valueType = TYPE_ARITH_EXP;
            
        } else if (isExprList(value)) {
            
        	valueType = TYPE_EXP_LIST;
        }

		return new Integer(valueType);
    }
	
	boolean isMemberOf(String name, String[] list) {

		boolean isListMember = false;
        
		if (name != null) {
            for(int i=0; i < list.length; i++) {
                if (name.equals(list[i])) {
                    isListMember = true;
                    break;
                }
            }
        }
        return isListMember;
    }
	
	// TYPE_SKILL_OPERAND
	//
	public boolean isValidSimpleSkill(String exp) {
		
		return isMemberOf(exp, VALID_SIMPLE_SKILL_NAMES);
    }

	public boolean isSkillOperand(String exp) {

		// TraceLog.out("@@@@@@@ " + exp);
		
		boolean isSkillOperand = false;
        
		int spaceIdx = exp.indexOf(' ');
        if (spaceIdx > 0) {
        
        	String[] token = exp.split(" ");
            
        	if (token.length == 2) {
            
        		if (isValidSimpleSkill(token[0]) && isArithmeticExpression(token[1])) {
            	
        			isSkillOperand = true;
        		}}}
        
        return isSkillOperand;
    }
	
	
	// TYPE_TERM
	//
	public boolean isArithTerm(String term) {
		
		boolean isArithTerm = true;
		
		    // Only the first letter could be '+' or '-'
		if (term.lastIndexOf('+') > 0 || term.lastIndexOf('-') > 0 ||
				// There shouldn't be any division or multiplication
				term.indexOf('/') > -1 || term.indexOf('*') > -1 || 
				// There shouldn't be any parenthesis
				term.indexOf('(') > -1 || term.indexOf(')') > -1)
			isArithTerm = false;
		
		return isArithTerm;
	}
	
	// TYPE_ARITH_EXP
	//
	public boolean isArithmeticExpression( String exp ) {    
    	
		// TraceLog.out("##### " + exp);
		
		if (exp.length() < 1) return false;
		if (exp.toUpperCase().indexOf("FALSE") > -1) return false; 
		if (exp.indexOf(' ') > -1) return false; 
		if (isValidSimpleSkill(exp)) return false;
		if (isExprList(exp)) return false;
		return true;
    }

	// TYPE_EXP_LIST
	//
	public boolean isExprList (String s){

		return (s != null && s.length()>0 && s.charAt(0)=='[' && s.charAt(s.length()-1)==']');
    }

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Helper Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param s
	 * @return True if s is not a null string
	 */
	boolean holds(String s) {
		
		return (s != null && !s.equals(FALSE_VALUE));
	}
	
	/**
	 * @param exp
	 * @return
	 */
	private LinkedList<String> extractTerms(String exp) {
		
		// System.out.println("extractTerms( " + exp + " )\n");
		
		LinkedList<String> terms = new LinkedList<String>();
		
		int inParenthesis = 0;
		String tmpStr = "";
		
		for (int i = 0; i < exp.length(); i++) {
			
			char c = exp.charAt(i);
			switch (c) {
			case '(':
				inParenthesis++;
				tmpStr += c;
				break;
			case ')':
				inParenthesis--;
				tmpStr += c;
				break;
			case '+':
			case '-':
				// Hit a separator...
				if (i > 0 && inParenthesis == 0) {
					terms.add(tmpStr);
					// System.out.println(" >> |" + tmpStr + "|\n");
					tmpStr = "" + c;
				} else {
					tmpStr += c;
				}
				break;
			default:
				tmpStr += c;
			}
		}
		terms.add(tmpStr);
		// System.out.println(" >> |" + tmpStr + "|\n");
		
		return terms;
	}

	// ToDo Stop using CL code for Algebra parsing
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Carnegie Leanring based codes -- this must be all gone...
	// Currently, the code needs utilities.jar in CTAT/lib/cl 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public Expression parse(String expString) {
	    // trace.out("expString = " + expString);
		HSParser parser = new HSParser();
		SMParserSettings settings = SMParserSettings.MIXED_NUMBERS_E_AS_VAR;
		String[] vars = getVars(expString);
		try {
			//1 expands numerator
			//2 expands denominator
			//3 expands numerator and denominator
			return parser.parse(expString, vars, settings).expand(3);
		} catch (ParseException e) {
		    System.out.println("expString" + expString);
			e.printStackTrace();
		} catch (DomainException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String[] getVars(String expString) {	
		List<String> vars = new LinkedList<String>();
		
		for(int x = 0; x < expString.length(); x++) {
			if(Character.isLetter(expString.charAt(x))) {
				String var = "";
				var += expString.charAt(x);
				
				vars.add(var);
			}
		}
		
		if(vars.size() == 0)
			return null;
		else
			return vars.toArray(new String[0]);
	}
	
	public boolean isDecimal(String exp){
		for(int i = 0; i < exp.length(); i++) {
			if(exp.charAt(i) == '.')
				return true;
		}
		return false;
	}
	
	public boolean isFraction(String exp){
		for(int i = 0; i < exp.length(); i++) {
			if(exp.charAt(i) == '/')
				return true;
		}
		return false;
	}
	
	public String typecheck(String expString1, String expString2, Expression exp) {
	
		String val;
		
		if((isDecimal(expString1) && !isFraction(expString2)) ||
			(isDecimal(expString2) && !isFraction(expString1))) {
			val = exp.expand(3).simplify().fractionToDecimal(false).toString();
		}
		else {
			val = exp.expand(3).simplify().decimalToFraction().toString();
		}
		
		if(val.indexOf('^') != -1)
			return null;
		
		return val;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Algebra Operators
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
    public String getSkillOperandSkill(String skillOperand) {
       
    	String skill = null;
        
    	if (isSkillOperand(skillOperand)) {
            skill = skillOperand.split(" ")[0];
        }
        
    	return skill;
    }
	
    /**
     * @param exp
     * @return T if exp is a polynomial expression
     */
    protected String isPolynomial(String exp) {
		
		return hasPlusMinusInTheMiddle(exp, true) ? TRUE_VALUE : FALSE_VALUE;
	}

	/**
	 * @param exp
	 * @param protectParenthesis: if True, consider term(s) surrounded by parenthesis as a single term
	 * @return True if the given exp has a '+' or '-' in the middle
	 */
	boolean hasPlusMinusInTheMiddle(String exp, boolean protectParenthesis) {
		
		boolean hasPorM = false;
		int inParenthesis = 0;
		
		for (int i = 1; !hasPorM && i < exp.length(); i++) {
			
			switch (exp.charAt(i)) {
			case '(': 
				if (protectParenthesis) inParenthesis++;
				break;
			case ')':
				if (protectParenthesis) inParenthesis--;
				break;
			case '+':
			case '-':
				if (inParenthesis == 0) {
					hasPorM = true;
					break;
				}
			}
		}

		return hasPorM;
	}

	/**
	 * @param exp
	 * @return
	 */
	protected String isMonomial(String exp) {
		
		return !hasPlusMinusInTheMiddle(exp, false) ? TRUE_VALUE : FALSE_VALUE;
	}
	
	/**
	 * @param exp
	 * @return
	 */
	protected String isConstantTerm(String exp) {
		
		// String isMonomial = isMonomial(exp);
		// String isVarTerm = isVarTerm(exp);
		// System.out.println("isMonomial = " + isMonomial + ", isVarTerm = " + isVarTerm);
		
		return (holds(isMonomial(exp)) && !holds(isVarTerm(exp))) ? exp : FALSE_VALUE;
	}
	
	/**
	 * @param exp
	 * @return
	 */
	protected String isVarTerm(String exp) {
    	
		String isVarTerm = null;
		
		if (holds(isMonomial(exp))) {
			
			for (int i = 0; i < exp.length(); i++) {
				char c = exp.charAt(i);
				if ( Character.isAlphabetic(c) ) {
					isVarTerm = TRUE_VALUE;
					break;
				}
			}
		}
		
		return isVarTerm;
    }
    
	/**
	 * @param exp
	 * @return
	 */
	protected String hasCoefficient(String exp) {
		
		String hasCoefficient = null;

		// The argument must be monomial to have this function invocation make sense 
		if (holds(isMonomial(exp))) {

			// Set the flag to be false, and ...
			hasCoefficient = FALSE_VALUE;

			// Switch it into true only when the condition holds.
			if (holds(isVarTerm(exp))) {
				for (int i = 0; i < exp.length(); i++) {
					char c = exp.charAt(i);
					if (c == '-' || Character.isDigit(c)) {
						hasCoefficient = TRUE_VALUE;
						break;
					}
				}
			}
		}
		
		return hasCoefficient;
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected String isLastConstTermNegative(String exp) {
		
		String isLastConstTermNegative = null;

		if (holds(isPolynomial(exp))) {
			
			LinkedList<String> terms = extractTerms(exp);
			for (int i = terms.size(); i > 0; i--) {
				
				String term = terms.get(i-1);
				
				if (holds(isConstantTerm(term))) {
					
					if (holds(isNegativeTerm(term))) {
						isLastConstTermNegative = TRUE_VALUE;
					}
					break;
				}
			}
			if (isLastConstTermNegative == null) isLastConstTermNegative = FALSE_VALUE;
		}
			
		return isLastConstTermNegative;
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected String isLastTermNegative(String exp) {
		
		String isLastTermNegative = FALSE_VALUE;
		
		LinkedList<String> terms = extractTerms(exp);
		String lastTerm = (terms != null) ? terms.getLast() : null;
		
		if ( lastTerm != null ) {
			isLastTermNegative = isNegativeTerm(lastTerm);
		}
		
		return isLastTermNegative;
	}
	
	
	/**
	 * @param string
	 * @return
	 */
	protected String getAllButFirstChar(String term) {
		
		String allButFirstChar = FALSE_VALUE;
		
		if (term != null && term.length() > 1) {
			allButFirstChar = term.substring(1);
		}
		
		return allButFirstChar;
	}

	/**
	 * @param exp
	 * @return
	 */
	protected String getFirstTerm(String exp) {
		
		String firstTerm = FALSE_VALUE;
		
		LinkedList<String> terms = extractTerms(exp);
		
		if (terms.size() > 0) {
			firstTerm = terms.get(0);
		}
		
		return firstTerm;
	}

	/**
	 * @param exp
	 * @return
	 */
	protected String getConstTerm(String exp) {
		
		String constTerm = FALSE_VALUE;
		
		LinkedList<String> terms = extractTerms(exp);
		for (int i = 0; i < terms.size(); i++) {
			
			String term = terms.get(i);
			if (holds(isConstantTerm(term))) {
				constTerm = term;
				break;
			}
		}
		
		return constTerm;
	}

	/**
	 * @param exp
	 * @return
	 */
	protected String hasVarTerm(String exp) {
		
		String hasVarTerm = FALSE_VALUE;
		
		LinkedList<String> terms = extractTerms(exp);
		
		for (int i = 0; i < terms.size(); i++) {
			
			String term = terms.get(i);
			if (holds(isVarTerm(term))) {
				hasVarTerm = TRUE_VALUE;
				break;
			}
		}

		return hasVarTerm;
	}
	
	/**
	 * @param term
	 * @return
	 */
	private String isNegativeTerm(String term) {
		
		String isNegativeTerm = null;
		
		if ( holds(isMonomial(term))) {
			isNegativeTerm = (term.charAt(0) == '-') ? TRUE_VALUE : FALSE_VALUE;
		}
		
		return isNegativeTerm;
	}

	/**
	 * @param string
	 * @return
	 */
	protected String homogeneous(String exp) {
		
		String homogeneous = "T";
		
		LinkedList<String> terms = extractTerms(exp);
		boolean firstTermSign = holds(isNegativeTerm(terms.getFirst()));
		
		for (int i = 1; i < terms.size(); i++) {
			if ( holds(isNegativeTerm(terms.get(i))) != firstTermSign ) {
				homogeneous = null;
				break;
			}
		}
		return homogeneous;
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected String getFirstIntegerWithoutSign(String exp) {
		
		String firstInteger = "";
		
		boolean getIt = false;
		for (int i = 0; i < exp.length(); i++) {
			
			char c = exp.charAt(i);
			if (Character.isDigit(c) || c == '.') {
			
				getIt = true;
				firstInteger += c;
			
			} else {
				
				if (getIt) break;
			}
		}
		
		return (firstInteger.length() > 0) ? firstInteger : FALSE_VALUE;
	}

}
