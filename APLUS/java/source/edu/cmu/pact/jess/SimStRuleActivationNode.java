/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import jess.Activation;

import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.IsEquivalent;
import edu.cmu.pact.miss.userDef.stoichiometry.StoFeatPredicate;

/**
 * SimStudent augmentation of {@link RuleActivationNode}. Added 2013-08-19 to accommodate academic version. 
 * @author sewall
 */
public class SimStRuleActivationNode extends RuleActivationNode {

	/**
	 * Returns {@link RuleActivationNode#RuleActivationNode(int, Activation, int, RuleActivationNode)}.
	 * @param agendaIndex
	 * @param act
	 * @param searchDepth
	 * @param parent
	 */
	public SimStRuleActivationNode(int agendaIndex, Activation act,
			int searchDepth, RuleActivationNode parent) {
		super(agendaIndex, act, searchDepth, parent);
	}

	
	private static EqFeaturePredicate eqFP = null;
	static {
		if (VersionInformation.includesCL())
			eqFP = new IsEquivalent();
	}

	private static EqFeaturePredicate getEqFeaturePredicateInstance(){
	       return (EqFeaturePredicate)eqFP;
	}

	/**
	 * Set input matcher {@link #eqFP} to a new instance of the given class.
	 * Tries to instantiate using class's no-argument constructor.
	 * @param className fully-qualified class name for new input matcher
	 */
	public static void setEqFeaturePredicateClass(String className){
        try {
        	Class inputMatcherCls = Class.forName(className);
        	eqFP = (EqFeaturePredicate) inputMatcherCls.newInstance();
        } catch (ClassNotFoundException cnfe) {
        	trace.err("setInputMatcher() cannot load class "+className+": "+
        			cnfe+(cnfe.getCause() == null ? "" : "; cause: "+cnfe.getCause()));
        } catch (ClassCastException cce) {
        	trace.err("setInputMatcher() wrong type for feature predicate "+className+": "+
        			cce+(cce.getCause() == null ? "" : "; cause: "+cce.getCause()));
        } catch (Exception e) {
        	trace.err("setInputMatcher() cannot instantiate "+className+": "+
        			e+(e.getCause() == null ? "" : "; cause: "+e.getCause()));
        }
	}

        private static StoFeatPredicate stoFP = new edu.cmu.pact.miss.userDef.stoichiometry.IsEquivalent();

        private static StoFeatPredicate getStoFeaturePredicateInstance(){
               return (StoFeatPredicate)stoFP;
        }

        
	private static boolean testInputMatcher(String s1, String s2){				
		EqFeaturePredicate fp = getEqFeaturePredicateInstance();

		//since inputMatcher is not static, this will inputMatcher implemented in eqFP's getClass().
                String result = fp.inputMatcher(s1, s2); 
//                trace.out("gusmiss", "result = " + result);
		return (result != null && result.equals("T"));
	}

        private static boolean testInputMatcher_Sto(String s1, String s2){                          
                StoFeatPredicate fp = getStoFeaturePredicateInstance();

                //since inputMatcher is not static, this will inputMatcher implemented in eqFP's getClass().
            String result = fp.inputMatcher(s1, s2); 
                return (result != null && result.equals("T"));
        }

        
        private static int testSingleValue_AlgMatcher(String rv, String sv, String notSpec,
			String label) {
		int result = -99;              // junk value for "unset"
		
		if (rv.equals(notSpec))
			result = NOT_SPEC;
		else if (rv.equals(MTRete.DONT_CARE))               // all values MATCH
			result = MATCH;
		else if (rv.equals(sv) || testInputMatcher(rv,sv)) //EqFeaturePredicate.inputMatcher(rv,sv).equals("T"))
		         //RhsState.getMatcherInstance().inputMatcher(rv,sv).equals("T")) //|| matcher(rv,sv))       // student value MATCHes  //else if (rv.equals(sv))                        // student value MATCHes
			result = MATCH;
		else                // match required but student value failed to match
			result = NO_MATCH;
		if (trace.getDebugCode("mt")) trace.out("mt", "testSingleValue "+label+" returns "+
				matchIntToString(result));
		return result;
	}
    	

        private static int testSingleValue_StoMatcher(String rv, String sv, String notSpec,
                        String label) {
                int result = -99;              // junk value for "unset"
                                
                
                if (rv.equals(notSpec))
                        result = NOT_SPEC;
                else if (rv.equals(MTRete.DONT_CARE))               // all values MATCH
                        result = MATCH;
                else if (rv.equals(sv) || testInputMatcher_Sto(rv,sv)) //StoFeatPredicate.inputMatcher(rv,sv).equals("T"))
                         //RhsState.getMatcherInstance().inputMatcher(rv,sv).equals("T")) //|| matcher(rv,sv))       // student value MATCHes  //else if (rv.equals(sv))                        // student value MATCHes
                        result = MATCH;
                else                // match required but student value failed to match
                        result = NO_MATCH;
                if (trace.getDebugCode("mt")) trace.out("mt", "testSingleValue "+label+" returns "+
                                matchIntToString(result));
                return result;
        }
	

        
        static int isSAIFound_StoMatcher(String selection, String action, String input,
                        String predictedSelection, String predictedAction,
                        String predictedInput) {
                
                if (trace.getDebugCode("mt")) trace.out("mt", "***isSAIFound() s " + selection + "=?" +
                                  predictedSelection + ", a " + action + "=?" +
                                  predictedAction + ", i " + input + "=?" +
                                  predictedInput + ";"); //,"mt"
                final int S = 0x1;   // whether we've matched student's selection
                final int A = 0x2;   // whether we've matched student's action
                final int I = 0x4;   // whether we've matched student's input
                int result = 0;      // match results so far
                int m;
                                

                m = testSingleValue_StoMatcher(predictedSelection, selection, MTRete.NOT_SPECIFIED,
                                "selection");
                if (m == NO_MATCH)
                        return NO_MATCH;      // selection was required but failed to match
                else if (m == MATCH)
                        result |= S;
                m = testSingleValue_StoMatcher(predictedAction, action, MTRete.NOT_SPECIFIED,
                                "action");
                if (m == NO_MATCH)
                        return NO_MATCH;      // action was required but failed to match
                else if (m == MATCH)
                        result |= A;
                m = testSingleValue_StoMatcher(predictedInput, input, MTRete.NOT_SPECIFIED,
                                "input");
                if (m == NO_MATCH)
                        return NO_MATCH;      // input was required but failed to match
                else if (m == MATCH)
                        result |= I;
                if ((result & (S|A|I)) == (S|A|I))
                        return MATCH;                   // all test results were MATCH
                else
                        return NOT_SPEC;       // at least one of S/A/I is unspecified
        }       
        

        
        

    	
    	
    	static int isSAIFound_AlgMatcher(String selection, String action, String input,
    			String predictedSelection, String predictedAction,
    			String predictedInput) {
    		
    		if (trace.getDebugCode("mt")) trace.out("mt", "***isSAIFound() s " + selection + "=?" +
    				  predictedSelection + ", a " + action + "=?" +
    				  predictedAction + ", i " + input + "=?" +
    				  predictedInput + ";"); //,"mt"
    		final int S = 0x1;   // whether we've matched student's selection
    		final int A = 0x2;   // whether we've matched student's action
    		final int I = 0x4;   // whether we've matched student's input
    		int result = 0;      // match results so far
    		int m;
    				

    		m = testSingleValue_AlgMatcher(predictedSelection, selection, MTRete.NOT_SPECIFIED,
    				"selection");
    		if (m == NO_MATCH)
    			return NO_MATCH;      // selection was required but failed to match
    		else if (m == MATCH)
    			result |= S;
    		m = testSingleValue_AlgMatcher(predictedAction, action, MTRete.NOT_SPECIFIED,
    				"action");
    		if (m == NO_MATCH)
    			return NO_MATCH;      // action was required but failed to match
    		else if (m == MATCH)
    			result |= A;
    		m = testSingleValue_AlgMatcher(predictedInput, input, MTRete.NOT_SPECIFIED,
    				"input");
    		if (m == NO_MATCH)
    			return NO_MATCH;      // input was required but failed to match
    		else if (m == MATCH)
    			result |= I;
    		if ((result & (S|A|I)) == (S|A|I))
    			return MATCH;                   // all test results were MATCH
    		else
    			return NOT_SPEC;       // at least one of S/A/I is unspecified
    	}	
        
}
