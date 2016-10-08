/**
 * Created: Dec 20, 2013 6:41:46 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;
import cl.utilities.sm.Expression;

/**
 * @author mazda
 *
 */
public class DivTerm extends AlgebraV8UserDefJessSymbol {

	public DivTerm() {

		setArity(2);
		setName("div-term");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
	}
		
	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {

		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
		Expression exp1 = parse(expString1);
		Expression exp2 = parse(expString2);
		Expression divided = exp1.divide(exp2).simplify();

		/*
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
		*/
		
		return typecheck(expString1, expString2, divided);
	}
}
