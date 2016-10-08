package edu.cmu.pact.ctat.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.GlobalTestSuiteManager;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary.CompletionValue;

public class ProblemSummaryTest extends TestCase {

	private ProblemSummary ps;
	
	/** Canon for {@link #testToXML()}. */
	private static final String expectedXML =
		"<ProblemSummary ProblemName=\"problemName\""+
		" CompletionStatus=\"incomplete\""+
		" Correct=\"8\" UniqueCorrect=\"4\" UniqueCorrectUnassisted=\"2\" Hints=\"5\" UniqueHints=\"4\""+
		" HintsOnly=\"2\" Errors=\"5\" UniqueErrors=\"4\" ErrorsOnly=\"2\" UniqueSteps=\"8\" TimeElapsed=\"0\">"+
		"<Skills>"+
		"<Skill name=\"write-sum\" category=\"addition\" description=\"Adding\" label=\"write-sum\" opportunityCount=\"0\" pGuess=\"0.4\" pKnown=\"0.5\" pLearn=\"0.7\" pSlip=\"0.6\" />"+
		"</Skills>"+
		"</ProblemSummary>";
	
	/** Canon for {@link #testQuizToXML()}. */
	private static final String expectedQuizXML =
		"<ProblemSummary ProblemName=\"problemName\""+
		" CompletionStatus=\"complete\""+
		" Correct=\"8\" UniqueCorrect=\"8\" UniqueCorrectUnassisted=\"2\" Hints=\"5\" UniqueHints=\"0\""+
		" HintsOnly=\"2\" Errors=\"5\" UniqueErrors=\"0\" ErrorsOnly=\"2\" UniqueSteps=\"8\" TimeElapsed=\"0\">"+
		"<Skills>"+
		"<Skill name=\"write-sum\" category=\"addition\" description=\"Adding\" label=\"write-sum\" opportunityCount=\"0\" pGuess=\"0.4\" pKnown=\"0.5\" pLearn=\"0.7\" pSlip=\"0.6\" />"+
		"</Skills>"+
		"</ProblemSummary>";
	
	public static Test suite()
	{
        return new TestSuite(ProblemSummaryTest.class);
	}
	
	public void testToXML() throws Exception
	{
		makePS(false);
		String xml = ps.toXML();
		if(trace.getDebugCode("ps"))
			trace.out("ps", "ps.toXML() =>\n"+xml);
		assertEquals(expectedXML, xml.trim());
		assertEquals("SCORM.getRawScore()", "25", SCORM.getRawScore(ps));
		assertEquals("SCORM.getMinScore()", "0", SCORM.getMinScore(ps));
		assertEquals("SCORM.getMaxScore()", "100", SCORM.getMaxScore(ps));
		assertEquals("SCORM.getLessonStatus()", "incomplete", SCORM.getLessonStatus(ps));
	}
	
	public void testQuizToXML() throws Exception
	{
		makePS(true);
		String xml = ps.toXML();
		assertEquals(expectedQuizXML, xml.trim());
		assertEquals("SCORM.getLessonStatus()", "completed", SCORM.getLessonStatus(ps));
	}
	
	public void testFactory() throws Exception
	{
		ProblemSummary ps2 = ProblemSummary.factory(expectedXML);
		assertEquals("factory() XML", expectedXML, ps2.toXML());
		assertEquals("CompletionStatus", "incomplete", ps2.getCompletionStatus().toString());
		assertEquals("UniqueCorrectUnassisted", 2, ps2.getUniqueCorrectUnassisted());
		assertEquals("UniqueCorrect", 4, ps2.getUniqueCorrect());
		assertEquals("Correct", 8, ps2.getCorrect());
		assertEquals("UniqueHints", 4, ps2.getUniqueHints());
		assertEquals("Hints", 5, ps2.getHints());
		assertEquals("HintsOnly", 2, ps2.getHintsOnly());
		assertEquals("UniqueErrors", 4, ps2.getUniqueErrors());
		assertEquals("Errors", 5, ps2.getErrors());
		assertEquals("ErrorsOnly", 2, ps2.getErrorsOnly());
		assertEquals("UniqueSteps", 8, ps2.getUniqueSteps());
		List<Skill> skillList = ps2.getSkills().getAllSkills();
		assertEquals("nSkills", 1, skillList.size());
		assertEquals("skill[0].skillName", "write-sum addition", skillList.get(0).getSkillName());
		assertEquals("skill[0].category", "addition", skillList.get(0).getCategory());
		assertEquals("skill[0].description", "Adding", skillList.get(0).getDescription());
	}
	
	public void testTimeElapsed() throws Exception {
		makePS(false);
		long et = 0;
		final int n = 5;
		final long ms = 300;  // 0.3 second
		for (int i = 1; i <= n; ++i) {
			ps.startTimer();
			Thread.sleep(ms, 500000);  // add 0.0005 sec fuzz for interest
			if (i < n) {
				et = ps.restartTimer();
				long min = i*ms/2, max = 3*i*ms/2;
				if (trace.getDebugCode("ps"))
					trace.out("ps", "min "+min+", et "+et+", max "+max);
				assertTrue("Step["+i+"] time elapsed "+et+" out of range ["+
						min+","+max+"]", min <= et && et <= max);
			}
		}
		long min = n*ms/2, max = 3*n*ms/2;
		ps.stopTimer();
		et = ps.getTimeElapsed();
		if (trace.getDebugCode("ps"))
			trace.out("ps", "min "+min+", et "+et+", max "+max);
		assertTrue("Final time elapsed "+et+" out of range ["+
						min+","+max+"]", min <= et && et <= max);
		assertEquals("SCORM session time mismatch",
				String.format("%04d:%02d:%02d.%02d", 
						et / (60*60*1000),
						(et % (60*60*1000))/(60*1000),
						(et % (60*1000))/1000,
						(et % 1000)/10), SCORM.getSessionTime(ps));
	}
	
	/**
	 * No-arg constructor for {@link GlobalTestSuiteManager}.
	 */
	public ProblemSummaryTest() {super();}
	
	public ProblemSummaryTest(String arg0) {
		super(arg0);
	}

	protected void makePS(boolean isQuiz) throws Exception {
		Skill sk = new Skill("write-sum addition", new Float(0.4), new Float(0.5), new Float(0.6), new Float(0.7));
		sk.setDescription("Adding");
		ArrayList<Skill> skills = new ArrayList<Skill>();
		skills.add(sk);
		ps = new ProblemSummary("problemName", new Skills(skills), isQuiz);
		ps.addHint("step1");     // H1 uH1 Ho1
		ps.addHint("step1");     // H2 uH1 Ho1
		ps.addError("step1");    // H2 uH1     E1 uE1
		ps.addError("step2");    // H2 uH1     E2 uE2 Eo1
		ps.addError("step2");    // H2 uH1     E3 uE2 Eo1
		ps.addCorrect("step1");  // H2 uH1     E3 uE2 Eo1 C1
		ps.addCorrect("step2");  // H2 uH1     E3 uE2 Eo1 C2
		ps.addHint("step3");     // H3 uH2 Ho1 E3 uE2 Eo1 C2
		ps.addCorrect("step3");  // H3 uH2 Ho1 E3 uE2 Eo1 C3 uC1
		ps.addCorrect("step4");  // H3 uH2 Ho1 E3 uE2 Eo1 C4 uC2 Cu1
		ps.addCorrect("step5");  // H3 uH2 Ho1 E3 uE2 Eo1 C5 uC3 Cu2
		ps.addHint("step6");     // H4 uH3 Ho2 E3 uE2 Eo1 C5 uC3 Cu2
		ps.addCorrect("step6");  // H4 uH3 Ho2 E3 uE2 Eo1 C6 uC4 Cu2
		ps.addError("step7");    // H4 uH3 Ho2 E4 uE3 Eo2 C6 uC4 Cu2
		ps.addCorrect("step7");  // H4 uH3 Ho2 E4 uE3 Eo2 C7 uC4 Cu2
		ps.addError("step8");    // H4 uH3 Ho2 E5 uE4 Eo3 C7 uC4 Cu2
		ps.addHint("step8");     // H5 uH4 Ho2 E5 uE4 Eo2 C7 uC4 Cu2
		ps.addCorrect("step8");  // H5 uH4 Ho2 E5 uE4 Eo2 C8 uC4 Cu2
		if(isQuiz)
			ps.setCompletionStatus(CompletionValue.complete, false);

		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}