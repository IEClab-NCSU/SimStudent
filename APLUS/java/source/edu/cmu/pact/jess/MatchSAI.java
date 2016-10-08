/*
 * $Id: MatchSAI.java 12714 2011-07-14 23:40:40Z sewall $
 *
 */
package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;

/**
 * This class is used to match the current (selection, action, input)
 * against the given arguments. 
 * 
 * @author sewall
 */
public class MatchSAI implements Userfunction, Serializable {

	/**
	 * Required by {@link jess.Userfunction} to provide the name by
	 * which Jess code finds this class's {@link #call(ValueVector,Context)}
	 * method.
	 *
	 * @return "match-SAI"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return "match-SAI";
	}

	/**
	 * Match the 
	 */
	public Value call(ValueVector vv, Context context) throws JessException {

		if (!("match-SAI".equals(getArgAsString(vv, context, 0))))
			return Funcall.FALSE;
		
		Value selectionV = getArg(vv, context, 1);
		Value actionV = getArg(vv, context, 2);
		Value inputV = getArg(vv, context, 3);

		return Funcall.FALSE;
	}

	/**
	 * Return a value from the argument string as a String.
	 *
	 * @param  vv argument vector provided by
	 *            {@link jess.Userfunction#call(ValueVector,Context)}
	 * @param  context Context argument provided by
	 *            {@link jess.Userfunction#call(ValueVector,Context)}
	 * @param  index index of desired element of vv
	 * @return resolved value at given index converted to String;
	 *            null if no value at that index
	 */
	private String getArgAsString(ValueVector vv, Context context, int index) {
		Value v = getArg(vv, context, index);
		if (v == null)
			return null;
		try {
			return v.stringValue(context);
		} catch (JessException je) {
			trace.out("Exception handled: " + je.getMessage());
			return null;
		}
	}

	/**
	 * Return a value from the argument string.
	 *
	 * @param  vv argument vector provided by
	 *            {@link jess.Userfunction#call(ValueVector,Context)}
	 * @param  context Context argument provided by
	 *            {@link jess.Userfunction#call(ValueVector,Context)}
	 * @param  index index of desired element of vv
	 * @return resolved value at given index; null if no value
	 */
	private Value getArg(ValueVector vv, Context context, int index) {
		if (index < 0 || vv.size() <= index)
			return null;
		try{
			Value v = vv.get(index);
			return v.resolveValue(context);
		} catch (JessException ex) {
			trace.out("Exception handled: " + ex.getMessage());
			return null;
		}
	}
}
