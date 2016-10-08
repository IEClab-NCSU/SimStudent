package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import cl.utilities.sm.Expression;
import cl.utilities.sm.HSParser;
import cl.utilities.sm.ParseException;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.function.DomainException;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ComplexTerm;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;
import edu.cmu.pact.miss.userDef.algebra.expression.SimpleTerm;

public class Coefficient extends CLBased 
{
    
    public Coefficient()
    {
	setArity(1);
	setName("coefficient");
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
    	if(((String)args.get(0)).trim().isEmpty()) {
    		return null;
    	}
    	
    	Expression exp = parse((String)args.get(0));
		
    	if(isCoefficientDone(exp)) 
    		return null;
    	
    	while(!isCoefficientDone(exp)) {
    		previous = exp.toString();
    		exp = exp.simplifiedCoefficient();
    		
        	if(exp.toString().equals(previous)) {
        		return null;
        	}
    	}
    	
		return exp.toString();
		
		//return coefficient((String)args.get(0));
    }
    
    private String previous = null;
    
    public boolean isCoefficientDone(Expression exp) {
    	if(exp == null)
    		return true;
    	
    	String expStr = exp.toString();
    	
    	for(int i = 0; i < expStr.length(); i++) {
    		if(Character.isLetter(expStr.charAt(i))) {
    			return false;
    		}
    	}
    	
    	return true;
    }
}
