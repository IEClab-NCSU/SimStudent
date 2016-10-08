package SimStAlgebraV8.LucyWeakPK;  // packagename is the package where you put this file.

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.CLBased;

public class GetFirstIntegerWithoutSign extends CLBased {
    
    private static final long serialVersionUID = 1L;
    
    public GetFirstIntegerWithoutSign()
    {
        setArity(1);
        setName("get-first-integer-without-sign");
        setReturnValueType(TYPE_ARITH_EXP);
        setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) 
    {
        String expString = (String)args.get(0);
        if(expString == null)
        	return null;
        String firstInt = TermGrabber.findNthInteger(expString, 0);
        if(firstInt == null)
        	return null;
        return TermGrabber.stripSign(firstInt);
    }
}