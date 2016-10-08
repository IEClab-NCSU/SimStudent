/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;

import org.xml.sax.helpers.AttributesImpl;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;

/**
 * @author sewall
 *
 */
public class SolverMatcherElement extends VectorMatcherElement {

	private boolean autoSimplify;
	private boolean typeinMode;
	private String goalName;
	
	/**
	 * Changes parent {@link VectorMatcherElement#elementName} to "solverMatcher".
	 * @param sm
	 */
	public SolverMatcherElement(SolverMatcher sm) {
		super(sm);
		elementName = "solverMatcher";      // adjust parent elementName
		autoSimplify = sm.getAutoSimplify();
		typeinMode = sm.getTypeinMode();
		goalName = sm.getGoalName();
	}

	protected AttributesImpl getAttributes() {
		AttributesImpl atts = super.getAttributes();
		atts.addAttribute("", "AutoSimplify", "", "String", Boolean.toString(autoSimplify));
		atts.addAttribute("", "TypeinMode", "", "String", Boolean.toString(typeinMode));
		if (goalName != null)
			atts.addAttribute("", "Goal", "", "String", goalName);
		return atts;
	}
}
