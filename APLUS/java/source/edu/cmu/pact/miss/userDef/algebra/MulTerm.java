package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import cl.utilities.sm.Expression;
import cl.utilities.sm.HSParser;
import cl.utilities.sm.ParseException;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.function.DomainException;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;
public class MulTerm extends CLBased 
{
	public MulTerm()
	{
		setName("mul-term");
		setArity(2);
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
		
	}
	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
	    try {			
            AlgExp e1=AlgExp.parseExp(expString1);
            AlgExp e2=AlgExp.parseExp(expString2);

            if (e1.isPolynomial() && e2.isPolynomial()) return null;
            if (e1.isPolynomial() && e2.isSimpleTerm()) return null;
            if (e1.isSimpleTerm() && e2.isPolynomial()) return null;
	    }
	    catch(ExpParseException e) {
	    	e.printStackTrace();
	    }
		
		Expression exp1 = parse(expString1);
		Expression exp2 = parse(expString2);
		Expression multiplied = exp1.multiply(exp2).simplify();
		
		return typecheck(expString1, expString2, multiplied);
		/*return mulTerm((String)args.get(0),(String)args.get(1));*/
		
	}
}