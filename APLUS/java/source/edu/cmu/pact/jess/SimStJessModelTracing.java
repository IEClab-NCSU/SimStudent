/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.util.Vector;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.miss.BKT.BKT;

/**
 * Sim Student augmentation of {@link JessModelTracing}. Added 2013-08-19 for academic version. 
 * @author sewall
 */
public class SimStJessModelTracing extends JessModelTracing {
	
	/**
	 * Returns the superclass constructor result {@link JessModelTracing#JessModelTracing(MTRete, CTAT_Controller)}.
	 * @param r
	 * @param controller
	 */
	public SimStJessModelTracing(MTRete r, CTAT_Controller controller) {
		super(r, controller);
	
	}
	
	int testFiringNodeSAI_AlgMatcher(String predictedSelection, String predictedAction,
			String predictedInput) {
		int result = SimStRuleActivationNode.isSAIFound_AlgMatcher(getStudentSelection(),
				getStudentAction(), getStudentInput(), predictedSelection, 
				predictedAction, predictedInput);
		nodeNowFiring.setMatchResult(result);
		return result; 
	}	

        
	int testFiringNodeSAI_StoMatcher(String predictedSelection, String predictedAction,
                String predictedInput) {
        int result = SimStRuleActivationNode.isSAIFound_StoMatcher(getStudentSelection(),
                        getStudentAction(), getStudentInput(), predictedSelection, 
                        predictedAction, predictedInput);
        nodeNowFiring.setMatchResult(result);
        return result; 
	}       

	
	
}
