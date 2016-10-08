package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class IsEquivalentWeak extends EqFeaturePredicate {

    public IsEquivalentWeak() {
        setArity(2);
        setName("is-equivalent-weak");
    }

    public String apply(Vector args) {
    	String s = inputMatcher((String)args.get(0), (String)args.get(1));
    	return s;
        }
    
    public String inputMatcher( String exp1, String exp2 ) {

        // having null in the arguments must be wrong
        if (exp1 == null || exp2 == null) return null;
    	
    	//jinyul - weak input matcher implementation for EqFeaturePredicate - parse out the negative sign in 
    	//         the instruction.
    	for(int x=0;x<exp1.length();x++) {
    		if(exp1.charAt(x)=='-') {
    			if(x<exp1.length()-1) {
    				exp1=exp1.substring(0,x).concat(exp1.substring(x+1));
    			}
    			else
    				exp1=exp1.substring(0,x);
    		}
    	}
    	for(int y=0;y<exp2.length();y++) {
    		if(exp2.charAt(y)=='-') {
    			if(y<exp2.length()-1) {
    				exp2=exp2.substring(0,y).concat(exp2.substring(y+1));
    			}
    			else
    				exp2=exp2.substring(0,y);
    		}
    	}
    	return super.inputMatcher(exp1, exp2);
    }
    
}
