/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Rete;
import jess.Userfunction;
import jess.Userpackage;
import jess.Value;
import jess.ValueVector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesVariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.algEquiv;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.algEquivTerms;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.algEquivTermsSameOrder;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.algEval;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.algStrictEquivTerms;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.algStrictEquivTermsSameOrder;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.assign;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.isAlgValid;

/**
 * Wrappers for functions in edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions:<ul>
 * <li>{@link algEquiv}</li>
 * <li>{@link algEquivTerms}</li>
 * <li>{@link algEquivTermsSameOrder}</li>
 * <li>{@link algEval}</li>
 * <li>{@link isAlgValid}</li>
 * <li>{@link assign}</li>
 * </ul>
 */
public class AlgExprFunctions implements Userpackage, Iterable<algUsesVariableTable> {

	/** Functions for {@link #add(Rete)} to load. */
	private List<algUsesVariableTable> functionInstances = null;
	
	/**
	 * Create and populate {@link #functionInstances}.
	 */
	public AlgExprFunctions() {
		functionInstances = new LinkedList<algUsesVariableTable>();
		functionInstances.add(new algEquivMT());
		functionInstances.add(new algStrictEquivTermsMT());
		functionInstances.add(new algStrictEquivTermsSameOrderMT());
		functionInstances.add(new algEquivTermsMT());
		functionInstances.add(new algEquivTermsSameOrderMT());
		functionInstances.add(new algEvalMT());
		functionInstances.add(new isAlgValidMT());
		functionInstances.add(new assignMT());
	}

	/**
	 * @return {@link #functionInstances}.{@link List#size() size()}
	 */
	public int size() {
		return functionInstances.size();
	}

	/**
	 * Call {@link Rete#addUserfunction(Userfunction)} on each item in {@link #functionInstances}.
	 * @param engine
	 * @see jess.Userpackage#add(jess.Rete)
	 */
	public void add(Rete engine) {
		for(Userfunction fn : functionInstances)
			engine.addUserfunction(fn);
	}

	/**
	 * Provide the given {@link VariableTable} to each function instance.
	 * @param variableTable
	 */
	public void setVariableTable(VariableTable variableTable) {
		for(UsesVariableTable fn : functionInstances) {
			fn.setVariableTable(variableTable);
		}
	}

	public Iterator<algUsesVariableTable> iterator() {
		return (Iterator<algUsesVariableTable>) functionInstances.iterator();
	}
}

abstract class algUsesVariableTable implements Serializable, UsesVariableTable, Userfunction {
	
	/** Function instance to call. **/
	protected UsesVariableTable fn = null;
	
	/**
	 * @param variableTable argument for
	 * {@link #fn}.{@link UsesVariableTable#setVariableTable(VariableTable) setVariableTable(VariableTable)}
	 */
	public void setVariableTable(VariableTable variableTable) {
		fn.setVariableTable(variableTable);
	}
	
	/**
	 * @return {@link #fn}.getClass().getSimpleName();
	 */
	public String getName() {
		return fn.getClass().getSimpleName();
	}

	/**
	 * Call {@link algEquiv#algEquiv(String, String)}.
	 * @param vv element[1] and element[2] passed to function
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public abstract Value call(ValueVector vv, Context context) throws JessException;
}

/**
 * Jess access to {@link algEquiv}.
 */
class algEquivMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public algEquivMT() {
		fn = new algEquiv();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link algEquiv#algEquiv(String, String)}.
	 * @param vv element[1] and element[2] passed to function
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expr0 = vv.get(1).stringValue(context);
		String expr1 = vv.get(2).stringValue(context);
		Boolean match = ((algEquiv) fn).algEquiv(expr0, expr1);
		if(match != null && match.booleanValue())
			return Funcall.TRUE;
		else
			return Funcall.FALSE;
	}
}

/**
 * Jess access to {@link algEquivTerms}.
 */
class algEquivTermsMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public algEquivTermsMT() {
		fn = new algEquivTerms();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link algEquivTerms#algEquivTerms(String)}.
	 * @param vv element[1] and element[2] passed to function
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expr0 = vv.get(1).stringValue(context);
		String expr1 = vv.get(2).stringValue(context);
		Boolean match = ((algEquivTerms) fn).algEquivTerms(expr0, expr1);
		if(match != null && match.booleanValue())
			return Funcall.TRUE;
		else
			return Funcall.FALSE;
	}
}

/**
 * Jess access to {@link algEquivTermsSameOrder}.
 */
class algEquivTermsSameOrderMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public algEquivTermsSameOrderMT() {
		fn = new algEquivTermsSameOrder();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link algEquivTermsSameOrder#algEquivTermsSameOrder(String)}.
	 * @param vv element[1] and element[2] passed to function
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expr0 = vv.get(1).stringValue(context);
		String expr1 = vv.get(2).stringValue(context);
		Boolean match = ((algEquivTermsSameOrder) fn).algEquivTermsSameOrder(expr0, expr1);
		if(match != null && match.booleanValue())
			return Funcall.TRUE;
		else
			return Funcall.FALSE;
	}
}

/**
 * Jess access to {@link algStrictEquivTerms}.
 */
class algStrictEquivTermsMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public algStrictEquivTermsMT() {
		fn = new algStrictEquivTerms();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link algStrictEquivTerms#algStrictEquivTerms(String)}.
	 * @param vv element[1] and element[2] passed to function
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expr0 = vv.get(1).stringValue(context);
		String expr1 = vv.get(2).stringValue(context);
		Boolean match = ((algStrictEquivTerms) fn).algStrictEquivTerms(expr0, expr1);
		if(match != null && match.booleanValue())
			return Funcall.TRUE;
		else
			return Funcall.FALSE;
	}
}

/**
 * Jess access to {@link algStrictEquivTermsSameOrder}.
 */
class algStrictEquivTermsSameOrderMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public algStrictEquivTermsSameOrderMT() {
		fn = new algStrictEquivTermsSameOrder();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link algStrictEquivTermsSameOrder#algStrictEquivTermsSameOrder(String)}.
	 * @param vv element[1] and element[2] passed to function
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expr0 = vv.get(1).stringValue(context);
		String expr1 = vv.get(2).stringValue(context);
		Boolean match = ((algStrictEquivTermsSameOrder) fn).algStrictEquivTermsSameOrder(expr0, expr1);
		if(match != null && match.booleanValue())
			return Funcall.TRUE;
		else
			return Funcall.FALSE;
	}
}

/**
 * Jess access to {@link algEval}.
 */
class algEvalMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public algEvalMT() {
		fn = new algEval();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link algEval#algEval(String)}.
	 * @param vv element[1] passed to function.
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expr = vv.get(1).stringValue(context);
		Double result = ((algEval) fn).algEval(expr);
		Value v = new Value(result == null ? Double.NaN : result.doubleValue(), RU.FLOAT);
		return v;
	}
}

/**
 * Jess access to {@link isAlgValid}.
 */
class isAlgValidMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public isAlgValidMT() {
		fn = new isAlgValid();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link isAlgValid#isAlgValid(String)}.
	 * @param vv element[1] passed to function.
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		String expr = vv.get(1).stringValue(context);
		boolean result = ((isAlgValid) fn).isAlgValid(expr);
		return (result ? Funcall.TRUE : Funcall.FALSE);
	}
}

/**
 * Jess access to {@link assign}.
 */
class assignMT extends algUsesVariableTable {

	/**
	 * Initialize {@link #fn}.
	 */
	public assignMT() {
		fn = new assign();
		fn.setVariableTable(null);
	}

	/**
	 * Call {@link assign#assign(String)}.
	 * @param vv elements [1], [2] passed to function as variable name, value.
	 * @param context
	 * @return function result
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		boolean result;
		String var = vv.get(1).stringValue(context);
		if(vv.size() <= 2)
			result = ((assign) fn).assign(var);
		else {
			Object val = Utils.jessValueToJava(vv.get(2), context);
			result = ((assign) fn).assign(var, val);
		}
		return (result ? Funcall.TRUE : Funcall.FALSE);
	}
}
