package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import cl.utilities.sm.Expression;
import cl.utilities.sm.HSParser;
import cl.utilities.sm.ParseException;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.function.DomainException;

public abstract class ArithmeticOperator extends EqFeaturePredicate {
	
	public Expression parse(String expString) {
		HSParser parser = new HSParser();
		SMParserSettings settings = SMParserSettings.MIXED_NUMBERS_E_AS_VAR;
		String[] vars = getVars(expString);
		try {
			//1 expands numerator
			//2 expands denominator
			//3 expands numerator and denominator
			return parser.parse(expString, vars, settings).expand(3);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DomainException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String[] getVars(String expString) {	
		for(int x = 0; x < expString.length(); x++) {
			if(Character.isLetter(expString.charAt(x))) {
				String var = "";
				var += expString.charAt(x);
				
				String[] vars = new String[1];
				vars[0] = var;
				return vars;
			}
		}
		
		return null;
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
	
		if((isDecimal(expString1) && !isFraction(expString2)) ||
			(isDecimal(expString2) && !isFraction(expString1))) {
			return exp.expand(3).simplify().fractionToDecimal(false).toString();
		}
		else {
			return exp.expand(3).simplify().decimalToFraction().toString();
		}
	}
}