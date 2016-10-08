/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.ctat.model.Skills;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.Funcall;
import jess.JessException;
import jess.LongValue;
import jess.QueryResult;
import jess.RU;
import jess.Rete;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;



/**
 * Jess access to {@link MTRete#useProblemSummary(Boolean)}.
 * @author sewall
 */
public class UseProblemSummary implements Serializable, Userfunction {
	
	/** For {@link Serializable}, a long with digits yyyymmddHHMM from the time this class was last edited. **/
	private static final long serialVersionUID = 201309281751L;

	/** String for {@link #getName()}. */
	private static final String NAME = "use-problem-summary";

	/**
	 * @return {@value #NAME}
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Call {@link MTRete#useProblemSummary(Boolean)}. No-op if no argument or nil argument.
	 * @param vv element[1] passed to function
	 * @param context
	 * @return function result: prior value from {@link MTRete#accessProblemSummary()}
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		MTRete mtr = (MTRete) context.getEngine();
		Boolean result;
		if(vv.size() > 1) {
			Value arg = vv.get(1).resolveValue(context);
			if(Funcall.NIL.equals(arg))
				result = mtr.getUseProblemSummary();
			else {
				String symbol = arg.symbolValue(context);
				boolean newValue = Boolean.valueOf(symbol);
				result = mtr.useProblemSummary(newValue);
				if(newValue) {
					CTAT_Controller ctlr = (mtr.getMT() == null ? null : mtr.getMT().getController());
					ProblemSummary ps = (ctlr == null ? null : ctlr.getProblemSummary());
					(new ProblemSummaryAccess()).updateProblemSummaryFacts(ps, mtr, mtr.getTextOutput());
				}
			}
		} else
			result = mtr.getUseProblemSummary();

		if(result == null)
			return Funcall.NIL;
		else
			return (result.booleanValue() ? Funcall.TRUE : Funcall.FALSE);
	}
}