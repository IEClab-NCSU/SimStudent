package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;

/**
 * Function callable from Jess code (see {@link #getName()} to test
 * student-entered selection, action and input against the given arguments.
 * Will test against selection, action and input values currently in
 * {@link JessModelTracing}. Note that this function returns true or false
 * and is meant to be evaluated on the left hand side of a jess rule (as
 * opposed to {@link PredictObservableAction})
 * @author epfeifer
 */
public class LHSPredictObservableAction implements Userfunction, Serializable {
	
	/** Function name, as known to Jess. */
	private static final String LHS_PREDICT_OBSERVABLE_ACTION = "lhs-predict-oa";

	/** Model tracer instance with student values. */
	protected transient JessModelTracing jmt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;
	
	/**
	 * No-argument constructor for use from (load-function).
	 */
	public LHSPredictObservableAction() {
		this(null);
	}
	
	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public LHSPredictObservableAction(JessModelTracing jmt) {
		super();
		this.jmt = jmt;
	}
	
	/**
	 * Return the name of this function as registered with Jess.
	 * @return "predict-observable-action"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return LHS_PREDICT_OBSERVABLE_ACTION;
	}
	
	/**
	 * Test the given arguments against the student values passed
	 * to it. Uses jmt to determine if is a hint trace {@link #jmt}.
	 *  Note that this function differs from its right hand side 
	 *  counterpart in that it does  <b> not </b> halt the Rete when
	 *  it returns false.
	 * @param vv argument list: order is<ol>
	 * 		  <li>student selection</li>
	 * 		  <li>student action</li>
	 *  	  <li>student input</li>
	 *        <li>predicted selection</li>
	 *        <li>predicted action</li>
	 *        <li>predicted input</li>
	 *        </ol>
	 * @param context Jess context for resolving values
	 * @return  {@link jess.Funcall#FALSE} on match failure,
	 *         else {@link jess.Funcall#TRUE} 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 * @see PredictObservableAction
	 */
	//RuleActivationNode.testSingleValue private->package private
	public Value call(ValueVector vv, Context context) throws JessException {
		return internalCall(vv, context, false);
	}
	
	/**
	 * Test the given arguments against the student values passed
	 * to it. Uses jmt to determine if is a hint trace {@link #jmt}.
	 *  Note that this function differs from its right hand side 
	 *  counterpart in that it does  <b> not </b> halt the Rete when
	 *  it returns false.
	 * @param vv argument list: order is<ol>
	 * 		  <li>student selection</li>
	 * 		  <li>student action</li>
	 *  	  <li>student input</li>
	 *        <li>predicted selection</li>
	 *        <li>predicted action</li>
	 *        <li>predicted input</li>
	 *        </ol>
	 * @param context Jess context for resolving values
	 * @param fromBuggyRule true if from a buggy rule; if true and this is a hint
	 *        request, always returns {@link jess.Funcall#FALSE} 
	 * @return  {@link jess.Funcall#FALSE} on match failure,
	 *         else {@link jess.Funcall#TRUE} 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 * @see PredictObservableAction
	 */
	//RuleActivationNode.testSingleValue private->package private
	protected Value internalCall(ValueVector vv, Context context, boolean fromBuggyRule)
			throws JessException {

		if (trace.getDebugCode("eep")) trace.out("eep","lhspoa called");
		
		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(getName()))
			throw new JessException(getName(), "called but ValueVector head differs",
					vv.get(0).stringValue(context));
		
		if (vv.size() < 3)
			throw new JessException(getName(), "called but too few arguments","args num:"+vv.size());
			
		String sSelection = vv.get(1).resolveValue(context).stringValue(context);
		String sAction	  = vv.get(2).resolveValue(context).stringValue(context);
		String sInput	  = vv.get(3).resolveValue(context).stringValue(context);
		
		String predictedSelection = MTRete.NOT_SPECIFIED;
		String predictedAction = MTRete.NOT_SPECIFIED;
		String predictedInput = MTRete.NOT_SPECIFIED;
	//	String predictedInputTestFunction =   "ExactMatcher"; // MTRete.NOT_SPECIFIED;
		String predictedInputTestFunction =   MTRete.NOT_SPECIFIED;
	
		if (getJmt() == null) // to avoid problems with startup
			return Funcall.TRUE;
			
		if(vv.size() > 4) {
			predictedSelection = vv.get(4).resolveValue(context).stringValue(context);
			if(vv.size() > 5) {
				predictedAction = vv.get(5).resolveValue(context).stringValue(context);
				if(vv.size() > 6) {
					predictedInput = vv.get(6).resolveValue(context).stringValue(context);
				}
			}
		}
		if (vv.size() > 7)
			predictedInputTestFunction = vv.get(vv.size()-1).stringValue(context);
		
        if (trace.getDebugCode("mt"))
        	trace.out("mt", String.format("lhs-predict-oa(%s, %s, %s, %s, %s, %s, %s)",
        			sSelection, sAction, sInput, predictedSelection, predictedAction, predictedInput,
        			predictedInputTestFunction));
       
		boolean isHint= (getJmt().isHintTrace() || "hint".equalsIgnoreCase(sSelection));
		if(isHint && fromBuggyRule)	{
        	if (trace.getDebugCode("mt")) trace.out("mt","BUGGYLHS HINT RETURNS: FALSE for [ "+sSelection+
        			" , "+sAction+" , "+sInput+" ] isHint:"+getJmt().isHintTrace()+" sS==hint:"+
        			sSelection.equalsIgnoreCase("hint"));
        	return Funcall.FALSE;
        } 

		if(predictedSelection == MTRete.NOT_SPECIFIED &&
				predictedAction == MTRete.NOT_SPECIFIED &&
				predictedInput == MTRete.NOT_SPECIFIED) {
			throw new JessException(getName(), "at least one selection,"+
					" action or input argument must be specified",
					MTRete.NOT_SPECIFIED);
		}
//		int matchResult = RuleActivationNode.NOT_SPEC;

	//	jmt.setRuleSAI(predictedSelection, predictedAction, predictedInput);
        //note- currently does not account for what the student selected
        if(isHint) {
        	if (trace.getDebugCode("mt")) trace.out("mt","LHS HINT RETURNS: TRUE for [ "+sSelection+
        			" , "+sAction+" , "+sInput+" ] isHint:"+getJmt().isHintTrace()+" sS==hint:"+
        			sSelection.equalsIgnoreCase("hint"));
        	return Funcall.TRUE;
        } 
        
        RuleActivationNode r = RuleActivationNode.create(null,0);

		if (r.isSAIFound(sSelection, sAction, sInput,
				predictedSelection, predictedAction, predictedInput,
				predictedInputTestFunction, false, context)
				== RuleActivationNode.NO_MATCH) {
			if (trace.getDebugCode("mt")) trace.out("mt", "LHS RETURNS: FALSE for [ "+sSelection+
        			" , "+sAction+" , "+sInput+" ]");
			return Funcall.FALSE;
		}
		
		if (trace.getDebugCode("mt")) trace.out("mt", "LHS RETURNS:TRUE for [ "+sSelection+
    			" , "+sAction+" , "+sInput+" ] isHint:"+getJmt().isHintTrace()+" sS==hint:"+
    			sSelection.equalsIgnoreCase("hint"));
		return Funcall.TRUE;
	}
        /*else if ( (predictedSelection.equalsIgnoreCase(sSelection) || 
        		predictedSelection.equalsIgnoreCase(MTRete.DONT_CARE)) 	&&
        	 (predictedAction.equalsIgnoreCase(sAction) 	  || 
        		predictedAction.equalsIgnoreCase(MTRete.DONT_CARE)) 	&&
        	 (predictedInput.equalsIgnoreCase(sInput)		  ||
        		predictedInput.equalsIgnoreCase(MTRete.DONT_CARE)) )
        	{if (trace.getDebugCode("eep")) trace.out("eep", "LHS RETURNS: TRUE for [ "+sSelection+
        			" , "+sAction+" , "+sInput+" ]");
        	return Funcall.TRUE;}
        
        if (trace.getDebugCode("eep")) trace.out("eep", "LHS RETURNS:FALSE for [ "+sSelection+
    			" , "+sAction+" , "+sInput+" ] isHint:"+getJmt().isHintTrace()+" sS==hint:"+
    			sSelection.equalsIgnoreCase("hint"));
        
        return	Funcall.FALSE;
	}*///better to call the same methods as predict-observable-action
        
		/*
        jmt.setRuleSAI(predictedSelection, predictedAction,	predictedInput, predictedInputTestFunction);
		
        
		Matcher m = getMatcher(vv, context);
		
		//epfeifer no issue up to this point
		if (m != null) {
			matchResult = jmt.LHStestFiringNodeSAI(m, jmt.isHintTrace()); //this line is a problem
			if (trace.getDebugCode("eep")) trace.out("eep","jmt.testFiringNodeSAI returned:"+matchResult);
		} else if (!jmt.isHintTrace()) {
			matchResult = jmt.testFiringNodeSAI(predictedSelection,
					predictedAction, predictedInput, predictedInputTestFunction, context);
		} else {
			matchResult = jmt.testFiringNodeSelection(predictedSelection);
		}

		
		if (trace.getDebugCode("mtt")) trace.out("mtt", "(predict-observable-action " + predictedSelection
				+ " " + predictedAction + " " + predictedInput + ") returns "
				+ matchResult);
		Value result = Funcall.TRUE;
		if (!jmt.isHintTrace() && matchResult == RuleActivationNode.NO_MATCH) {
//			jmt.haltRete(getName()); does *not* halt Rete
			result = Funcall.FALSE;
		}
		if (trace.getDebugCode("eep")) trace.out("eep", "LHS RETURNS:"+result.stringValue(context));
		return result;
	}*/ //not need to go this far into current firing node if SAI is given
	
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

/**
 *
 */
class BuggyLHSPredictObservableAction extends LHSPredictObservableAction implements Userfunction, Serializable {
	
	/** Function name, as known to Jess. */
	private static final String BUGGY_LHS_PREDICT_OBSERVABLE_ACTION = "buggy-lhs-predict-oa";

	/**
	 * No-argument constructor for use from (load-function).
	 */
	public BuggyLHSPredictObservableAction() {
		this(null);
	}
	
	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public BuggyLHSPredictObservableAction(JessModelTracing jmt) {
		super(jmt);
	}
		
	/**
	 * Return the name of this function as registered with Jess.
	 * @return "predict-observable-action"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return BUGGY_LHS_PREDICT_OBSERVABLE_ACTION;
	}
	
	/**
	 * Works as {@link LHSPredictObservableAction#call(ValueVector, Context)}
	 * except that, if this is a hint request, always returns {@link jess.Funcall#FALSE}. 
	 * Test the given arguments against the student values passed
	 * to it. Uses jmt to determine if is a hint trace {@link #jmt}.
	 *  Note that this function differs from its right hand side 
	 *  counterpart in that it does  <b> not </b> halt the Rete when
	 *  it returns false.
	 * @param vv argument list: order is<ol>
	 * 		  <li>student selection</li>
	 * 		  <li>student action</li>
	 *  	  <li>student input</li>
	 *        <li>predicted selection</li>
	 *        <li>predicted action</li>
	 *        <li>predicted input</li>
	 *        </ol>
	 * @param context Jess context for resolving values
	 * @return  {@link jess.Funcall#FALSE} on match failure,
	 *         else {@link jess.Funcall#TRUE} 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 * @see PredictObservableAction
	 */
	public Value call(ValueVector vv, Context context)
			throws JessException {
		return internalCall(vv, context, true);
	}

}
