package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.Utilities.trace;

public class AddTerm extends CLBased {
	
    private static final long serialVersionUID = 1L;
    
    public AddTerm()
    {
        setArity(2);
        setName("add-term");
        setReturnValueType(TYPE_ARITH_EXP);
        setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
		Expression exp1 = parse(expString1);
		Expression exp2 = parse(expString2);
		Expression added = exp1.add(exp2).simplify();
		
		return typecheck(expString1, expString2, added);
        /*return addTerm((String)args.get(0),(String)args.get(1));*/
    }
}
