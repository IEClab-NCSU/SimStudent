package edu.cmu.pact.ctat.model;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.GlobalTestSuiteManager;

public class SkillTest extends TestCase {
	
	/**
	 * No-arg constructor for {@link GlobalTestSuiteManager}.
	 */
	public SkillTest() {super();}

	public SkillTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static Test suite()
	{
		TestSuite suite = new TestSuite(SkillTest.class);
		return suite;
	}
	
	public void testMakeStepID() {
		String[] s, a;
		String id;
		
		s = new String[] { "textField1" }; a = new String[] { "UpdateTextField" };
		assertEquals(id = "["+s[0]+"]["+a[0]+"]", id,
				Skill.makeStepID(new Vector(Arrays.asList(s)), new Vector(Arrays.asList(a))));
		
		s = new String[] { "hint", "textField1" }; a = new String[] { "ButtonPressed", "PreviousFocus" };
		assertEquals(id = "["+s[1]+"]["+a[0]+"]", id,
				Skill.makeStepID(new Vector(Arrays.asList(s)), new Vector(Arrays.asList(a))));
	}
	
	public void testDebitSkills()
	{
		float[] pknownVals = { 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F };
		System.out.printf("Incorrect:\n");
		Skill[] skills = new Skill[pknownVals.length];
		for (int i = 0; i < skills.length; ++i)
		{
			skills[i] = new Skill("pknown_pct_"+((int)(pknownVals[i]*100+0.5)), new Float(0.200), pknownVals[i], new Float(0.400), new Float(0.200));
			float newPKnown = skills[i].updatePKnown(Skill.INCORRECT);
			float delta = newPKnown - pknownVals[i];
			System.out.printf("[%d] %-25s %4.2f %10.8f %10.7f %10.7f\n", i, skills[i].getSkillName(), pknownVals[i],
					newPKnown, delta, delta/pknownVals[i]);
		}
		
	}
	
	public void testCreditSkills()
	{
		float[] pknownVals = { 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F };
		Skill[] skills = new Skill[pknownVals.length];
		System.out.printf("\nCorrect:\n");
		for (int i = 0; i < skills.length; ++i)
		{
			skills[i] = new Skill("pknown_pct_"+((int)(pknownVals[i]*100+0.5)), new Float(0.200), pknownVals[i], new Float(0.400), new Float(0.200));
			float newPKnown = skills[i].updatePKnown(Skill.CORRECT);
			float delta = newPKnown - pknownVals[i];
			System.out.printf("[%d] %-25s %4.2f %10.8f %10.7f %10.7f\n", i, skills[i].getSkillName(), pknownVals[i],
					newPKnown, delta, delta/pknownVals[i]);
		}
	}
	
	public void testUpdatePknown()
	{
		Skill sk = new Skill("skill1", new Float(0.2), new Float(0.250), new Float(0.4), new Float(0.2));
		float f = 0F;
		assertEquals("[1] sc.updateSkill(true)", f=0.6F, sk.updatePKnown(Skill.CORRECT), 1e-7);
		assertEquals("[2] sc.updateSkill(false)", f=0.54285717F, sk.updatePKnown(Skill.INCORRECT), 1e-7);
		assertEquals("[3] sc.updateSkill(true)", f=0.82465756F, sk.updatePKnown(Skill.CORRECT), 1e-7);
		assertEquals("[4] sc.updateSkill(false)", f=0.7613054F, sk.updatePKnown(Skill.INCORRECT), 1e-7);
		assertEquals("[5] sc.updateSkill(true)", f=0.92430234F, sk.updatePKnown(Skill.CORRECT), 1e-7);
		assertEquals("SkillBar no Mastery",
				"skill1"+Skill.SKILL_BAR_DELIMITER+f+Skill.SKILL_BAR_DELIMITER+"0", sk.getSkillBarString());
		assertEquals("[6] sc.updateSkill(true)", f=0.97874111F, sk.updatePKnown(Skill.CORRECT), 1e-7);
		assertEquals("SkillBar Mastery",
				"skill1"+Skill.SKILL_BAR_DELIMITER+f+Skill.SKILL_BAR_DELIMITER+"1", sk.getSkillBarString());
	}
}
