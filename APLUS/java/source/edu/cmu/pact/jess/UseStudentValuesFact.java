package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * Jess access to {@link MTRete#setUseStudentValuesFact(boolean)}.
 * @author sewall
 */
public class UseStudentValuesFact implements Serializable, Userfunction {
	
	/** For {@link Serializable}, a long with digits yyyymmddHHMM from the time this class was last edited. **/
	private static final long serialVersionUID = 201308311051L;

	/** String for {@link #getName()}. */
	private static final String NAME = "use-student-values-fact";

	/**
	 * @return {@value #NAME}
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Call {@link MTRete#setUseStudentValuesFact(Boolean)}. No-op if no argument.
	 * @param vv element[1] passed to function
	 * @param context
	 * @return function result: prior value of {@link MTRete#getUseStudentValuesFact()}
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		MTRete mtr = (MTRete) context.getEngine();
		Boolean result;
		if(vv.size() > 1) {
			Value arg = vv.get(1).resolveValue(context);
			if(Funcall.NIL.equals(arg))
				result = mtr.setUseStudentValuesFact(null);
			else {
				String symbol = arg.symbolValue(context); 
				result = mtr.setUseStudentValuesFact(Boolean.valueOf(symbol));
			}
		} else {
			result = mtr.getUseStudentValuesFact();
		}
		if(result == null)
			return Funcall.NIL;
		else
			return (result.booleanValue() ? Funcall.TRUE : Funcall.FALSE);
	}
}