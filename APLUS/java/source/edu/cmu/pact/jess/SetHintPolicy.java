/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.HintPolicyEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;

/**
 * @author sewall
 *
 */
public class SetHintPolicy implements Userfunction, Serializable {
	
	/** For {@link Serializable}, a long with digits yyyymmddHHMM from the time this class was last edited. **/
	private static final long serialVersionUID = 201311061041L;
	
	/** Function name, as known to Jess. */
	private static final String FUNCTION_NAME = "set-hint-policy";

	/**
	 * Return the name of this function as registered with Jess.
	 * @return {@value #FUNCTION_NAME}
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return FUNCTION_NAME;
	}

	/**
	 * Set the hint policy for the problem.
	 * Calls {@link ProblemModel#setHintPolicy(HintPolicyEnum)} with the argument.
	 * @param vv arguments
	 * @param context Rete, variable bindings
	 * @return prior value from {@link ProblemModel#getHintPolicy()};
	 *         if no argument or invalid argument, prints all legal arguments
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		if(!(context.getEngine() instanceof MTRete))
			return new Value("ProblemModel not available", RU.STRING);
		ProblemModel pm = ((MTRete) context.getEngine()).getMT().getController().getProblemModel();
		HintPolicyEnum oldPolicy = pm.getHintPolicy();
		String errorMsg = "";
		if (vv.size() > 1) {
			String arg = vv.get(1).stringValue(context);
			HintPolicyEnum newPolicy = HintPolicyEnum.fromString(arg, true);
			if(newPolicy != null) {
				pm.setHintPolicy(newPolicy);
				return new Value(oldPolicy.toString(), RU.STRING);
			}
			errorMsg = "Invalid policy \""+arg+"\". ";
		}
		StringBuilder sb = new StringBuilder(errorMsg).append("Valid arguments:");
		HintPolicyEnum[] policies = HintPolicyEnum.values();
		for(HintPolicyEnum policy : policies)
			sb.append("\n  ").append(policy.toString());
		return new Value(sb.toString(), RU.STRING);
	}

}
