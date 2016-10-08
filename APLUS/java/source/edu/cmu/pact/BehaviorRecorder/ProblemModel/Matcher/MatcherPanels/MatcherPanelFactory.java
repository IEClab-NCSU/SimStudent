/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * modified by Ko
 * We no longer use the MatcherPanelFactory since each of selection, action,
 * and input get their own vector of matchers
 */
public class MatcherPanelFactory {

	/**
	 * @param string
	 * @param edgeData 
	 * @return
	 */
	public static MatcherPanel createMatcherPanel(String matcherType, EdgeData edgeData, Boolean allowToolReportedActions, Integer num) {
		return createMatcherPanel(matcherType, edgeData, allowToolReportedActions, num, 0);
	}
	
	public static MatcherPanel createMatcherPanel(String matcherType, EdgeData edgeData, Boolean allowToolReportedActions, Integer num, int index) {
		boolean allowToolActions = allowToolReportedActions.booleanValue();
		int n = num.intValue();

        if (trace.getDebugCode("functions")) trace.outln("functions", "createMatcherPanel: " + matcherType);
		if (matcherType.equals (Matcher.ANY_MATCHER)) {
			return new AnyMatcherPanel(edgeData, allowToolActions, n, index);
		} 
		
		if (matcherType.equals (Matcher.EXACT_MATCHER)) {
			return new ExactMatcherPanel (edgeData, allowToolActions, n, index);
		}
		
		if (matcherType.equals (Matcher.RANGE_MATCHER)) {
			return new RangeMatcherPanel (edgeData, allowToolActions, n, index);
		}

		if (matcherType.equals (Matcher.REGULAR_EXPRESSION_MATCHER)) {
			return new RegexMatcherPanel (edgeData, allowToolActions, n, index);
		}

		if (matcherType.equals (Matcher.WILDCARD_MATCHER)) {
			return new WildcardMatcherPanel (edgeData, allowToolActions, n, index);
		}
		
		if (matcherType.equals (Matcher.EXPRESSION_MATCHER)) {
            if (trace.getDebugCode("functions")) trace.outln("functions", "creating expression matcher...");
		    return new ExpressionMatcherPanel(edgeData, allowToolActions, n, index);
        }
		
        if (trace.getDebugCode("functions")) trace.outln("functions", "matcher for " + matcherType + " not found.");
		return null;
	}

}
