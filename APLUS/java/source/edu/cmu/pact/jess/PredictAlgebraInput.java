/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;

/**
 * Function callable from Jess code (see {@link #getName()} to test
 * student-entered selection, action and input against the given arguments.
 * Will test against selection, action and input values currently in
 * {@link JessModelTracing}.
 * @author sewall
 */
public class PredictAlgebraInput extends PredictObservableAction {
	
	/** Function name, as known to Jess. */
	private static final String PREDICT_ALGEBRA_INPUT = "predict-algebra-input";


	/**
	 * No-argument constructor for use from (load-function).
	 */
	public PredictAlgebraInput() {
		super();
	}

	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public PredictAlgebraInput(JessModelTracing jmt) {
		super();

	}

	/**
	 * Return the name of this function as registered with Jess.
	 * @return "predict-algebra-input"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return PREDICT_ALGEBRA_INPUT;
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
	 */	public Value call(ValueVector vv, Context context) throws JessException {
		
//                 System.out.println("entered PredictAlgebraInput.call()");
             
		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(PREDICT_ALGEBRA_INPUT))
			throw new JessException(PREDICT_ALGEBRA_INPUT, "called but ValueVector head differs",
					vv.get(0).stringValue(context));
		String predictedSelection = MTRete.NOT_SPECIFIED;
		String predictedAction = MTRete.NOT_SPECIFIED;
		String predictedInput = MTRete.NOT_SPECIFIED;

		// Update the WM when the SimStRete is running.
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				predictedSelection = vv.get(1).resolveValue(context).stringValue(context);
				if(vv.size() > 2) {
					predictedAction = vv.get(2).resolveValue(context).stringValue(context);
					if(vv.size() > 3) {
						predictedInput = vv.get(3).resolveValue(context).stringValue(context);
					}
				}
			}
			if(predictedSelection != MTRete.NOT_SPECIFIED && predictedAction != MTRete.NOT_SPECIFIED
					&& predictedInput != MTRete.NOT_SPECIFIED) {
				if(getJmt() != null)
					jmt.setRuleSAI(predictedSelection, predictedAction, predictedInput);
				
				
				return Funcall.TRUE;
			} else {
				return Funcall.FALSE;
			}
		}
		
			if (getJmt() == null || !getJmt().isModelTracing()) {
				if(trace.getDebugCode("miss"))trace.out("miss", "Return call in PredicatAlgebraInput");
				return Funcall.TRUE;
			}
		
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
			throw new JessException(PREDICT_ALGEBRA_INPUT, "at least one selection,"+
					" action or input argument must be specified",
					MTRete.NOT_SPECIFIED);
		}
		int matchResult = RuleActivationNode.NOT_SPEC;
		jmt.setRuleSAI(predictedSelection, predictedAction,	predictedInput);
		if (!jmt.isHintTrace()) {

			try {
				matchResult = ((SimStJessModelTracing) jmt).testFiringNodeSAI_AlgMatcher(predictedSelection,
						predictedAction, predictedInput);
			} catch(ClassCastException cce) {
				throw new ClassCastException("("+getName()+") requires the Sim Student version of CTAT;"+
						" the current version is "+VersionInformation.getReleaseString()+
						", which "+(VersionInformation.includesSimSt() ? "should" : "does not")+
						" include Sim Student; nested exception: "+cce);
			}
		} else {
		    matchResult = jmt.testFiringNodeSelection(predictedSelection, context);
		}
		if (trace.getDebugCode("mt")) trace.out("mt", "(predict-algebra-input "+predictedSelection+" "+predictedAction+
				" "+predictedInput+") returns "+matchResult);
		Value result = Funcall.TRUE;
		if (!jmt.isHintTrace() && matchResult == RuleActivationNode.NO_MATCH) {
			jmt.haltRete(getName());
			result = Funcall.FALSE;
		}
		if (trace.getDebugCode("mt")) trace.out("mt", "predict-algebra-input returning "+result.stringValue(context));
		return result;
	}


}
