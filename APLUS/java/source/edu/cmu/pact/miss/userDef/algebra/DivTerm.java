	package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import cl.utilities.sm.Expression;
import cl.utilities.sm.HSParser;
import cl.utilities.sm.ParseException;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.function.DomainException;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;
public class DivTerm extends CLBased {

	public DivTerm() 
	{
		setArity(2);
		setName("div-term");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});

	}

	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
		Expression exp1 = parse(expString1);
		Expression exp2 = parse(expString2);
		Expression divided = exp1.divide(exp2).simplify();
		
		try{			
	        AlgExp e1=AlgExp.parseExp(expString1);
	        AlgExp e2=AlgExp.parseExp(expString2);
	        
	        if (e1.equals(AlgExp.ZERO)) return "0";
	        if (e1.equals(e2)) return "1";
	        if (!e2.isSimple() || e2.equals(AlgExp.ZERO)) return null;
		}
		catch(ExpParseException e) {
			e.printStackTrace();
			return null;
		}
		return typecheck(expString1, expString2, divided);
		/*return divTerm((String)args.get(0),(String)args.get(1));*/	
	}
}
