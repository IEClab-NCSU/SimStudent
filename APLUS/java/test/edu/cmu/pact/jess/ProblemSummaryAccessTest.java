/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import jess.Fact;
import jess.JessException;
import jess.QueryResult;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.ProblemSummaryTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class ProblemSummaryAccessTest extends TestCase {

	private Rete rete = null;
	private TextOutput textOutput = null;
	
	public static Test suite()
	{
        return new TestSuite(ProblemSummaryAccessTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		rete = new Rete();
		textOutput = TextOutput.getTextOutput(System.out);		
	}

	/**
	 * Test method for {@link ProblemSummaryAccess#updateProblemSummaryFacts(ProblemSummary, jess.Rete, TextOutput)}.
	 */
	public void testUpdateProblemSummaryFacts() throws Exception {
		ProblemSummary ps = ProblemSummary.factory(sourceXML);
		ProblemSummaryAccess psa = new ProblemSummaryAccess();

		Fact psFact = psa.updateProblemSummaryFacts(ps, rete, textOutput);
		assertNotNull("updateProblemSummaryFacts() returns null", psFact);
		checkProblemSummary(psFact);
		
		ps = ProblemSummary.factory(secondXML);
		psFact = psa.updateProblemSummaryFacts(ps, rete, textOutput);
		checkProblemSummary2(psFact);
	}

	private void checkProblemSummary(Fact psFact) throws JessException {

		String slot;
		Value v;

		slot = "ProblemName"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "got-a-problem", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());

		slot = "CompletionStatus"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "incomplete", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());

		slot = "Correct"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 8, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueCorrect"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 4, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueCorrectUnassisted"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 2, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "Hints"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 5, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueHints"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 4, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "HintsOnly"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 2, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "Errors"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 5, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueErrors"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 4, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "ErrorsOnly"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 2, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueSteps"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 8, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "TimeElapsed"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 333L, v.longValue(null)); assertEquals(slot+" type mismatch", RU.LONG, v.type());

		checkSkills(psFact.getSlotValue(ProblemSummaryAccess.SKILLS), 1);
	}

	private void checkProblemSummary2(Fact psFact) throws JessException {

		String slot;
		Value v;

		slot = "ProblemName"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "got-no-problem", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());

		slot = "CompletionStatus"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "incomplete", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());

		slot = "Correct"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 88, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueCorrect"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 44, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueCorrectUnassisted"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 22, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "Hints"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 55, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueHints"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 44, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "HintsOnly"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 22, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "Errors"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 55, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueErrors"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 44, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "ErrorsOnly"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 22, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "UniqueSteps"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 88, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());

		slot = "TimeElapsed"; v=psFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 333333L, v.longValue(null)); assertEquals(slot+" type mismatch", RU.LONG, v.type());

		checkSkills(psFact.getSlotValue(ProblemSummaryAccess.SKILLS), 2);
	}

	/** Source XML for problem summary instance. */
	private static final String sourceXML =
		"<ProblemSummary ProblemName=\"got-a-problem\""+
		"  CompletionStatus=\"incomplete\""+
		"  Correct=\"8\" UniqueCorrect=\"4\" UniqueCorrectUnassisted=\"2\" Hints=\"5\" UniqueHints=\"4\""+
		"  HintsOnly=\"2\" Errors=\"5\" UniqueErrors=\"4\" ErrorsOnly=\"2\" UniqueSteps=\"8\" TimeElapsed=\"333\">"+
		"  <Skills>"+
		"    <Skill name=\"write-sum\" category=\"MAIN\" description=\"Adding\" label=\"write-sum\""+
		"      opportunityCount=\"0\" pGuess=\"0.5\" pKnown=\"0.6\" pLearn=\"0.7\" pSlip=\"0.8\" />"+
		"    <Skill name=\"write-carry\" category=\"MAIN\" description=\"Carrying\" label=\"write-carry\""+
		"      opportunityCount=\"9\" pGuess=\"0.1\" pKnown=\"0.2\" pLearn=\"0.3\" pSlip=\"0.4\" />"+
		"  </Skills>"+
		"</ProblemSummary>";

	/** Source XML for problem summary instance. */
	private static final String secondXML =
		"<ProblemSummary ProblemName=\"got-no-problem\""+
		"  CompletionStatus=\"incomplete\""+
		"  Correct=\"88\" UniqueCorrect=\"44\" UniqueCorrectUnassisted=\"22\" Hints=\"55\" UniqueHints=\"44\""+
		"  HintsOnly=\"22\" Errors=\"55\" UniqueErrors=\"44\" ErrorsOnly=\"22\" UniqueSteps=\"88\" TimeElapsed=\"333333\">"+
		"  <Skills>"+
//		"    <Skill name=\"write-sum\" category=\"MAIN\" description=\"Adding\" label=\"write-sum\""+
//		"      opportunityCount=\"0\" pGuess=\"0.5\" pKnown=\"0.6\" pLearn=\"0.7\" pSlip=\"0.8\" />"+
//		"    <Skill name=\"write-carry\" category=\"MAIN\" description=\"Carrying\" label=\"write-carry\""+
//		"      opportunityCount=\"9\" pGuess=\"0.1\" pKnown=\"0.2\" pLearn=\"0.3\" pSlip=\"0.4\" />"+
		"    <Skill name=\"write-sum\" category=\"MAIN\" description=\"Adding\" label=\"write-sum\""+
		"      opportunityCount=\"10\" pGuess=\"0.55\" pKnown=\"0.66\" pLearn=\"0.77\" pSlip=\"0.88\" />"+
		"    <Skill name=\"write-carry\" category=\"MAIN\" description=\"Carrying\" label=\"write-carry\""+
		"      opportunityCount=\"99\" pGuess=\"0.11\" pKnown=\"0.22\" pLearn=\"0.33\" pSlip=\"0.44\" />"+
		"  </Skills>"+
		"</ProblemSummary>";

	private static final double delta = 1e31*Float.MIN_NORMAL;
	{
		System.out.printf("delta for Float tests = %.15f\n", delta);		
	}
	
	private void checkWriteSumSkill(Fact skFact) throws JessException {
		String slot;
		Value v;
		
		slot = "name"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-sum", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "category"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "MAIN", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "description"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "Adding", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "label"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-sum", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "opportunityCount"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());
		
		slot = "pGuess"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.5, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pKnown"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.6, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pLearn"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.7, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pSlip"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.8, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
	}
	
	private void checkWriteSumSkill2(Fact skFact) throws JessException {
		String slot;
		Value v;
		
		slot = "name"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-sum", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "category"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "MAIN", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "description"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "Adding", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "label"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-sum", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "opportunityCount"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 10, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());
		
		slot = "pGuess"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.55, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pKnown"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.66, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pLearn"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.77, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pSlip"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.88, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
	}

	private void checkWriteCarrySkill(Fact skFact) throws JessException {
		String slot;
		Value v;
		
		slot = "name"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-carry", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "category"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "MAIN", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "description"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "Carrying", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "label"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-carry", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "opportunityCount"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 9, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());
		
		slot = "pGuess"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.1, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pKnown"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.2, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pLearn"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.3, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pSlip"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.4, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
	}

	private void checkWriteCarrySkill2(Fact skFact) throws JessException {
		String slot;
		Value v;
		
		slot = "name"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-carry", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "category"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "MAIN", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "description"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "Carrying", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "label"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", "write-carry", v.stringValue(null)); assertEquals(slot+" type mismatch", RU.STRING, v.type());
		
		slot = "opportunityCount"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 99, v.intValue(null)); assertEquals(slot+" type mismatch", RU.INTEGER, v.type());
		
		slot = "pGuess"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.11, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pKnown"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.22, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pLearn"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.33, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
		
		slot = "pSlip"; v=skFact.getSlotValue(slot);
		assertEquals(slot+" value mismatch", 0.44, v.floatValue(null), delta); assertEquals(slot+" type mismatch", RU.FLOAT, v.type());
	}
	
	private void checkSkills(Value v, int which) throws JessException {
		assertEquals("skills type mismatch", RU.getTypeName(RU.LIST), RU.getTypeName(v.type()));
		ValueVector vv = v.listValue(null);
		assertEquals("skills count mismatch", 2, vv.size());
		byte inMultislot = 0;
		for(int i = 0; i < vv.size(); ++i) {
			Fact skFact = vv.get(i).factValue(null);
			String name = skFact.getSlotValue("name").stringValue(null);
			if("write-sum".equals(name)) {
				ValueVector queryArgs = new ValueVector(2);
				queryArgs.add(new Value("write-sum", RU.STRING));
				queryArgs.add(new Value("MAIN", RU.STRING));
				QueryResult qr = rete.runQueryStar(ProblemSummaryAccess.GET_SKILL, queryArgs);
				assertTrue("query for write-sum skill failed", qr.next());
				qr.close();

				if(which == 2)
					checkWriteSumSkill2(skFact);
				else
					checkWriteSumSkill(skFact);
				inMultislot |= 1;
			} else if("write-carry".equals(name)) {
				ValueVector queryArgs = new ValueVector(2);
				queryArgs.add(new Value("write-carry", RU.STRING));
				queryArgs.add(new Value("MAIN", RU.STRING));
				QueryResult qr = rete.runQueryStar(ProblemSummaryAccess.GET_SKILL, queryArgs);
				assertTrue("query for write-carry skill failed", qr.next());
				qr.close();

				if(which == 2)
					checkWriteCarrySkill2(skFact);
				else
					checkWriteCarrySkill(skFact);
				inMultislot |= 2;
			} else 
				fail("unexpected skill name \""+name+"\" at index "+i);
		}
		if(inMultislot != (1|2))
			fail("missing from multislot fact for skill \""+((inMultislot & 1) == 0 ? "write-sum" : "write-carry")+"\"");
	}

	/**
	 * Test method for {@link ProblemSummaryAccess#updateProblemSummaryFacts(ProblemSummary, jess.Rete, TextOutput)}.
	 */
	public void testUpdateNullProblemSummary() throws Exception {
		try {
			QueryResult qr = rete.runQueryStar(ProblemSummaryAccess.GET_PROBLEM_SUMMARY, new ValueVector(0));
			fail("testUpdateNullProblemSummary() ran query "+ProblemSummaryAccess.GET_PROBLEM_SUMMARY+" before defined");
		} catch(JessException je) {
			if(trace.getDebugCode("ps"))
				trace.out("ps", "expected exception running query before defined: "+je);
		}
		
		ProblemSummaryAccess psa = new ProblemSummaryAccess();
		Fact psFact = psa.updateProblemSummaryFacts(null, rete, textOutput);

		assertNull("updateProblemSummaryFacts(null) returns non-null", psFact);
		
		QueryResult qr = rete.runQueryStar(ProblemSummaryAccess.GET_PROBLEM_SUMMARY, new ValueVector(0));
		assertFalse("updateProblemSummaryFacts(null) created a ProblemSummaryFact", qr.next());
		
		ValueVector queryArgs = new ValueVector(2);
		queryArgs.add(new Value("write-sum", RU.STRING));
		queryArgs.add(new Value("MAIN", RU.STRING));
		qr = rete.runQueryStar(ProblemSummaryAccess.GET_SKILL, queryArgs);
		assertFalse("updateProblemSummaryFacts(null) created a ProblemSummaryFact", qr.next());		
	}
}
