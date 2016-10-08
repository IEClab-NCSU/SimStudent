/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;

/**
 * Function callable from Jess code (see {@link #getName()} to test
 * student-entered selection, action and input against the given arguments.
 * Will test against selection, action and input values currently in
 * {@link JessModelTracing}.
 * @author sewall
 */
public class TestSAI extends PredictObservableAction {
	
	/** Function name, as known to Jess. */
	private static final String TEST_SAI = "test-SAI";


	/**
	 * No-argument constructor for use from (load-function).
	 */
	public TestSAI() {
		super();
	}

	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public TestSAI(JessModelTracing jmt) {
		super();

	}

	/**
	 * Return the name of this function as registered with Jess.
	 * @return "test-SAI"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return TEST_SAI;
	}

	/**
	 * Test the given arguments against the student values stored in
	 * the attached model tracer {@link #jmt}.  <b>This function halts
	 * the Rete when the match fails.</b>
	 * @param vv argument list: order is<ol>
	 *        <li>selection</li>
	 *        <li>action</li>
	 *        <li>input</li>
	 *        </ol>
	 * @param context Jess context for resolving values
	 * @return {@link jess.Funcall#FALSE} on match failure--and halts Rete;
	 *         else {@link jess.Funcall#TRUE} 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		
		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(TEST_SAI))
			throw new JessException(TEST_SAI, "called but ValueVector head differs",
					vv.get(0).stringValue(context));
		String predictedSelection = MTRete.NOT_SPECIFIED;
		String predictedAction = MTRete.NOT_SPECIFIED;
		String predictedInput = MTRete.NOT_SPECIFIED;
	//	String predictedInputTestFunction =   "ExactMatcher"; // MTRete.NOT_SPECIFIED;
		String predictedInputTestFunction =   MTRete.NOT_SPECIFIED;
	
		if (getJmt() == null) // to avoid problems with startup
			return Funcall.TRUE;

		if(vv.size() > 1) {
			predictedSelection = vv.get(1).resolveValue(context).stringValue(context);
			if(vv.size() > 2) {
				predictedAction = vv.get(2).resolveValue(context).stringValue(context);
				if(vv.size() > 3) {
					predictedInput = vv.get(3).resolveValue(context).stringValue(context);
				}
			}
		}
		if(predictedSelection == MTRete.NOT_SPECIFIED &&
				predictedAction == MTRete.NOT_SPECIFIED &&
				predictedInput == MTRete.NOT_SPECIFIED) {
			throw new JessException(TEST_SAI, "at least one selection,"+
					" action or input argument must be specified",
					MTRete.NOT_SPECIFIED);
		}
		int matchResult = RuleActivationNode.NOT_SPEC;

	//	jmt.setRuleSAI(predictedSelection, predictedAction, predictedInput);
		if (vv.size() > 4) {
			predictedInputTestFunction = vv.get(vv.size()-1).resolveValue(context).stringValue(context);
            trace.out("[ " + predictedSelection + " , "+ predictedAction + " , "+ predictedInput +
            		" , "+ predictedInputTestFunction + " ] ");
		}

        if (trace.getDebugCode("mtt")) trace.out("mtt", "predict-obs-act[ " + predictedSelection + " , "+ predictedAction + " , "+
        		predictedInput + " , "+ predictedInputTestFunction + " ] ");
		jmt.setRuleSAI(predictedSelection, predictedAction,	predictedInput, predictedInputTestFunction);
			
		Matcher m = getMatcher(vv, context);
		
		if (m != null) {
			matchResult = jmt.testFiringNodeSAI(m, jmt.isHintTrace());
		} else if (!jmt.isHintTrace()) {
			matchResult = jmt.testFiringNodeSAI(predictedSelection,
					predictedAction, predictedInput, predictedInputTestFunction, context);
		} else {
			matchResult = jmt.testFiringNodeSelection(predictedSelection, context);
		}
		
		if (trace.getDebugCode("mtt")) trace.out("mtt", "(predict-observable-action " + predictedSelection
				+ " " + predictedAction + " " + predictedInput + ") returns "
				+ matchResult);
		Value result = Funcall.TRUE;
		if (!jmt.isHintTrace() && matchResult == RuleActivationNode.NO_MATCH) {
			jmt.haltRete(getName());
			result = Funcall.FALSE;
		}
		if (trace.getDebugCode("mtt")) trace.out("mtt", "predict-observable-action returning "+result.stringValue(context));
		return result;
	}

	/**
	 * Return a {@link Matcher} instance if specified as the last argument.
	 * If the last argument is not a Matcher instance and there are exactly
	 * 3 arguments, then return an {@link ExactMatcher}. Initialize the Matcher
	 * with the arguments via {@link Matcher#setParameterByIndex(String, int)}.
	 * @param vv full Jess argument list
	 * @param context
	 * @return Matcher instance or null
	 * @throws JessException
	 */
	public Matcher getMatcher(ValueVector vv, Context context)
			throws JessException {
		Matcher m = null;
		Value cnv = vv.get(vv.size()-1).resolveValue(context);
		String possibleClassName = (cnv == null ? null : cnv.stringValue(context));
		int lastArgIndex = vv.size() - 2;

		m = jmt.getMatcher(possibleClassName);
		if (m == null && vv.size() == 4) {  // 4=>3 args, since 1st is function name
			m = new ExactMatcher();
			lastArgIndex = vv.size() - 1;
		}
		if (m != null) {
			for (int i = 1; i <= lastArgIndex; i++) {
				Value rv = vv.get(i).resolveValue(context);
				String param = (rv == null ? null : rv.stringValue(context));
				if (MTRete.NOT_SPECIFIED.equals(param))
					m.setParamNotSpecified(i-1, Matcher.NOT_SPECIFIED);
				else if (MTRete.DONT_CARE.equals(param))
					m.setParamNotSpecified(i-1, Matcher.DONT_CARE);
				else
					m.setParameterByIndex(param, i-1);
			}
		}
		return m;
	}

	/**
	 * Get a reference to the model tracer. If the field {@link #jmt} is null,
	 * tries to reestablish the connection via
	 * {@link MTRete#getJmt()}.
	 * 
	 * @return Returns the jmt.
	 */
	protected JessModelTracing getJmt() {
		if (jmt == null) {
			if (context != null) {
				if (context.getEngine() instanceof MTRete)
					jmt = ((MTRete) context.getEngine()).getJmt();
			}
		}
		return jmt;
	}

	/**
	 * @param jmt The jmt to set.
	 */
	protected void setJmt(JessModelTracing jmt) {
		this.jmt = jmt;
	}


}

