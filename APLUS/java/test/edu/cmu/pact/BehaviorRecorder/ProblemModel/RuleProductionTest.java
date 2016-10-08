/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;

/**
 * @author sewall
 *
 */
public class RuleProductionTest extends TestCase {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(RuleProductionTest.class);
		return suite;
	}

	public void testUpdateOpportunityCounts() {
		ProblemStateReaderJDom psr = new ProblemStateReaderJDom(null);
		RuleProduction.Catalog rpc = new RuleProduction.Catalog(); 
		ProblemModel pm = psr.loadBRDFileIntoProblemModel("test/5-6plus7-9_R1.brd", rpc);

		Vector<ProblemEdge> preferredPathEdges = pm.findPathForProblemSkillsMatrix(pm.getStartNode());
		rpc.updateOpportunityCounts(preferredPathEdges);
		String ruleName;
		
		ruleName = "determine-lcm add-fractons";
		assertEquals(ruleName+" oppty count", 1,
				rpc.getRuleProduction(ruleName).getOpportunityCount().intValue());
		ruleName = "copy-denom add-fractons";
		assertEquals(ruleName+" oppty count", 1,
				rpc.getRuleProduction(ruleName).getOpportunityCount().intValue());
		ruleName = "convert-num add-fractons";
		assertEquals(ruleName+" oppty count", 2,
				rpc.getRuleProduction(ruleName).getOpportunityCount().intValue());
		ruleName = "add-nums add-fractons";
		assertEquals(ruleName+" oppty count", 1,
				rpc.getRuleProduction(ruleName).getOpportunityCount().intValue());
		ruleName = "copy-sum-denom add-fractons";
		assertEquals(ruleName+" oppty count", 1,
				rpc.getRuleProduction(ruleName).getOpportunityCount().intValue());
	}

}
