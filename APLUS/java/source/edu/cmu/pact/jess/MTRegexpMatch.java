/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Pattern;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * A serializeable regex matcher. Patterned on the jess function.
 */
public class MTRegexpMatch implements Userfunction, Serializable {

	private static HashMap<String, Pattern> s_patterns = new HashMap<String, Pattern>();

	/**
	 * @return
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
        return "mtregexp";
	}

	/**
	 * @param vv
	 * @param context
	 * @return
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expression = vv.get(1).stringValue(context);
		String trial = vv.get(2).stringValue(context);
		Pattern regex = getPattern(expression);
		boolean match = regex.matcher(trial).matches();
		return match ? Funcall.TRUE : Funcall.FALSE;
	}

	private Pattern getPattern(String expression) {
		synchronized(s_patterns) {
			Pattern p = (Pattern) s_patterns.get(expression);
			if (p == null) {
				p = Pattern.compile(expression);
				s_patterns.put(expression, p);
			}
			return p;
		}
	}
}
