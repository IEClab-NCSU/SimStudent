/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat.model;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.ctat.XMLSupport;

/**
 * @author sewall
 *
 */
public class SkillsTest extends TestCase {

	/** Sample for {@link #testFactory()}. */
	private static final String xmlWriteSum =
		"<Skills><Skill name=\"write-sum\" category=\"addition\""+
		" description=\"Adding single digits in place column.\"" +
		" label=\"Sum digits\""+
		" opportunityCount=\"2\""+
		" pGuess=\"0.2\" pKnown=\"0.66\" pLearn=\"0.15\" pSlip=\"0.3\" />"+
		"</Skills>";
	
	/** Sample for {@link #testToXMLElement()}. See also static initializer. */
	private static Skill writeSum =
		new Skill("write-sum addition", 0.2F, 0.66F, 0.3F, 0.15F);
	
	/** Sample for {@link #testGetSkillBarVector()}. */
	private static Skill addCarry =
		new Skill("add-carry addition", 0.3F, 0.67F, 0.4F, 0.16F);
	
	/* Set #writeSum.description. */
	static {
		writeSum.setLabel("Sum digits");
		writeSum.setDescription("Adding single digits in place column.");
		writeSum.setOpportunityCount("2");
	}

	
	/** Formatter for toXML(): compact XML. */
	private static XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
	
	public static Test suite()
	{
		TestSuite suite = new TestSuite(SkillsTest.class);
		return suite;
	}
	
	public void testGetSkillBarVector() {
		Skills skills = new Skills();
		skills.add(writeSum);
		skills.add(addCarry);

		Vector<String> sbv = skills.getSkillBarVector();
		assertEquals("SkillBarVector size()", 2, sbv.size());
		assertEquals("SkillBarVector[0]", "write-sum addition"+Skill.SKILL_BAR_DELIMITER+"0.66"+Skill.SKILL_BAR_DELIMITER+"0", sbv.get(0));
		assertEquals("SkillBarVector[1]", "add-carry addition"+Skill.SKILL_BAR_DELIMITER+"0.67"+Skill.SKILL_BAR_DELIMITER+"0", sbv.get(1));

		sbv = skills.getSkillBarVector(true);
		assertEquals("SkillBarVector size()", 2, sbv.size());
		assertEquals("SkillBarVector[0]", "write-sum addition"+Skill.SKILL_BAR_DELIMITER+"0.66"+Skill.SKILL_BAR_DELIMITER+"0"+Skill.SKILL_BAR_DELIMITER+"Sum digits", sbv.get(0));
		assertEquals("SkillBarVector[1]", "add-carry addition"+Skill.SKILL_BAR_DELIMITER+"0.67"+Skill.SKILL_BAR_DELIMITER+"0"+Skill.SKILL_BAR_DELIMITER+"add-carry", sbv.get(1));
	}

	/**
	 * Test method for {@link edu.cmu.pact.ctat.model.Skills#factory(org.jdom.Element)}.
	 */
	public final void testFactory() throws Exception {
		Document doc = XMLSupport.parse(xmlWriteSum);
		Skills skills = Skills.factory(doc.getRootElement());
		Skill writeSum = skills.getSkill("write-sum addition");
		assertEquals("pGuess", 0.2F, writeSum.getPGuess(), Float.MIN_VALUE);
	}

	/**
	 * Test method for {@link edu.cmu.pact.ctat.model.Skills#toXMLElement()}.
	 */
	public final void testToXMLElement() {
		Skills skills = new Skills();
		skills.add(writeSum);
		Element elt = skills.toXMLElement();
		String xml = outputter.outputString(elt);
		assertEquals("single writeSum skill", xmlWriteSum, xml);
	}
}
